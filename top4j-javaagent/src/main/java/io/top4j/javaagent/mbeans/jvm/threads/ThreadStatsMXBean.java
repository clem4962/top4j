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

import io.top4j.javaagent.mbeans.StatsMXBean;

public interface ThreadStatsMXBean extends StatsMXBean {

	void setCpuUsage(double cpuUsage);

	double getCpuUsage();
	
	void setUserCpuUsage(double userCpuUsage);
	
	double getUserCpuUsage();
	
	void setSysCpuUsage(double sysCpuUsage);
	
	double getSysCpuUsage();
	
	long getThreadCount();
	
	void setThreadCount(long threadCount);

	long getRunnableThreadCount();

	void setRunnableThreadCount(long runnableThreadCount);

	long getBlockedThreadCount();

	void setBlockedThreadCount(long blockedThreadCount);

	long getWaitingThreadCount();

	void setWaitingThreadCount(long waitingThreadCount);

	long getTimedWaitingThreadCount();

	void setTimedWaitingThreadCount(long timedWaitingThreadCount);

}
