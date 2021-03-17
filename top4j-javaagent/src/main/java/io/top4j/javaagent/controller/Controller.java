/*
 * Copyright (c) 2019 Open Answers Ltd. https://www.openanswers.co.uk
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.top4j.javaagent.controller;

import com.sun.tools.attach.VirtualMachine;

import java.nio.file.Paths;
import java.security.CodeSource;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Logger;

import io.top4j.javaagent.config.Configurator;
import io.top4j.javaagent.config.Constants;
import io.top4j.javaagent.mbeans.jvm.JVMStats;
import io.top4j.javaagent.mbeans.jvm.internals.HotspotInternals;
import io.top4j.javaagent.mbeans.jvm.internals.HotspotInternalsMBean;
import io.top4j.javaagent.messaging.LoggerQueue;
import io.top4j.javaagent.utils.MBeanHelper;

import javax.management.JMX;
import javax.management.MBeanServer;

public final class Controller extends Thread {

    private LoggerQueue loggerQueue;
    private Configurator config;
    private long interval;
    private boolean statsLoggerEnabled;

    private static final Logger LOGGER = Logger.getLogger(LoggerThread.class.getName());

    /**
     * Create a polling thread to track JVM stats.
     *
     */
    public Controller(Configurator config) {
        super("Top4J Monitor");
        this.config = config;
        this.interval = Long.parseLong(config.get("collector.poll.frequency"));
        this.statsLoggerEnabled = config.isStatsLoggerEnabled();
        if (statsLoggerEnabled) {
            // create LoggerQueue
            this.loggerQueue = new LoggerQueue(100);
        }


        // If isHotspotInternalsEnabled, register the HotspotInternalsMBean.
        // In the event of any error, we just turn off the option and continue.
        if (config.isHotspotInternalsEnabled()) {
            // If we have been given a remote pid, it means we ran from the console and so want to inject the
            // internals agent into the remote jvm to register the HotspotInternalsMBean...
            if (config.getRemoteJvmPid() > 0) {
                try {
                    CodeSource src = Controller.class.getProtectionDomain().getCodeSource(); // get this top4j-cli jar
                    if (src != null) {
                        String jar = Paths.get(src.getLocation().toURI()).toString();
                        VirtualMachine vm = VirtualMachine.attach(String.valueOf(config.getRemoteJvmPid()));
                        vm.loadAgent(jar.toString());
                        LOGGER.finest("instrumented remote JVM for hotspot internals");
                    }
                } catch (Throwable t) {
                    // NB This can throw an Exception due to handling of the return code from the agent and yet still work.
                    LOGGER.finest("Error injecting agent into remote JVM for hotpspot internals: " + t.getMessage());
                }
            }
            // Otherwise, just bind the bean locally
            else {
                try {
                    // instantiate new MBeanHelper used to access Internals MBean attributes and operations
                    MBeanHelper mbeanHelper = new MBeanHelper(Constants.INTERNALS_STATS_TYPE);
                    // instantiate new MBean
                    HotspotInternals mbean = new HotspotInternals();
                    // register MBean with MBean server
                    mbeanHelper.registerMBean(mbean);
                }
                catch (Exception e) {
                    LOGGER.finest("Failed to initialise HotspotInternals MBean due to " + e.getMessage());
                }
            }

            // Now test that the bean works - if not, turn off the option
            try {
                HotspotInternalsMBean proxy = JMX.newMBeanProxy(config.getMBeanServerConnection(), new HotspotInternals().getObjectName(), HotspotInternalsMBean.class);
                proxy.getInternalThreadCpuTimes();
            }
            catch (Throwable t) {
                config.setHotspotInternalsEnabled(false);
                LOGGER.fine("Hotspot internals not available");
            }
        }

        try {
            // instantiate new MBeanHelper used to access JVMStats MBean attributes and operations
            MBeanHelper jvmStatsMBeanHelper = new MBeanHelper(Constants.AGENT_TYPE, Constants.JVM_STATS_TYPE);
            // instantiate new JVMStats MBean
            JVMStats jvmStatsMBean = new JVMStats(config, loggerQueue);
            // register JVMStats MBean with MBean server
            jvmStatsMBeanHelper.registerMBean(jvmStatsMBean);

        } catch (Exception e) {
            LOGGER.severe("Failed to initialise JVM stats MBean due to " + e.getMessage());
        }

        setDaemon(true);
    }

    /**
     * Run the thread until interrupted.
     */
    @Override
    public void run() {

        if (statsLoggerEnabled) {
            try {
                // create and start logger thread
                LoggerThread statsLogger = new LoggerThread(config, loggerQueue);
                statsLogger.start();
            } catch (Exception e) {
                LOGGER.severe("Failed to initialise LoggerThread due to " + e.getMessage());
            }
        }

        try {
            // create new TimerTask to run JVM stats collector
            TimerTask collector = new Collector(config);
            // create new Timer to schedule JVM stats collector
            Timer timer = new Timer("Top4J Stats Collector", true);
            // run JVM stats collector at fixed interval
            timer.scheduleAtFixedRate(collector, 0, interval);
        } catch (Exception e) {
            LOGGER.severe("Failed to initialise stats Collector thread due to " + e.getMessage());
        }

    }

}
