/*
 * Copyright (c) 2019 Open Answers Ltd.
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

package io.top4j.javaagent.mbeans.jvm.threads;

import io.top4j.javaagent.utils.ThreadHelper;

import javax.management.MBeanServerConnection;
import java.io.IOException;

/**
 * Created by ryan on 27/07/15.
 */
public class HotMethod implements HotMethodMXBean {

    volatile private String methodName;
    volatile private String threadName;
	volatile private long threadId;
    volatile private double loadProfile;
    volatile private StackTraceElement[] stackTrace;
    private ThreadHelper threadHelper;

    public HotMethod( MBeanServerConnection mbsc ) throws IOException {

        this.threadHelper = new ThreadHelper( mbsc );

    }

    @Override
    public void setMethodName(String methodName) {
       this.methodName = methodName;
    }

    @Override
    public String getMethodName() {
        return methodName;
    }

    @Override
    public void setThreadName(String threadName) {
        this.threadName = threadName;
    }

    @Override
    public String getThreadName() {
        return threadName;
    }

    @Override
    public void setThreadId(long threadId) {
        this.threadId = threadId;
    }

    @Override
    public long getThreadId() {
        return threadId;
    }

    @Override
    public void setLoadProfile(double loadProfile) {
       this.loadProfile = loadProfile;
    }

    @Override
    public double getLoadProfile() {
        return loadProfile;
    }

    @Override
    public String getStackTrace(int maxDepth) {
        return threadHelper.getStackTraceAsString(stackTrace);
    }

    public void setStackTrace(StackTraceElement[] stackTrace) {
       this.stackTrace = stackTrace;
    }
}
