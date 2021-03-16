/*
 * Copyright (c) 2021 Open Answers Ltd.
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

package io.top4j.javaagent.mbeans.jvm.internals;

import io.top4j.javaagent.config.Constants;
import io.top4j.javaagent.mbeans.jvm.internals.HotspotInternals;
import io.top4j.javaagent.utils.MBeanHelper;

import javax.management.InstanceAlreadyExistsException;

public class HotspotInternalsAgent {

    public static void agentmain(String agentArgs) throws Exception {
        initInternalsMBean();
    }

    private static void initInternalsMBean() {

        try {
            // instantiate new MBeanHelper used to access Internals MBean attributes and operations
            MBeanHelper mbeanHelper = new MBeanHelper(Constants.INTERNALS_STATS_TYPE);
            // instantiate new MBean
            HotspotInternals mbean = new HotspotInternals();
            // register MBean with MBean server
            mbeanHelper.registerMBean(mbean);
        } catch (Exception e) {
            // we are executing in the target JVM - use the simplest possible reporting
            if (!(e.getCause() instanceof InstanceAlreadyExistsException))
                System.err.println("TOP4J: Failed to initialise HotspotInternals MBean: " + e.getMessage());
        }
    }

}
