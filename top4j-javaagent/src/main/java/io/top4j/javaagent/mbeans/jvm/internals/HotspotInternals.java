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
import sun.management.HotspotRuntimeMBean;
import sun.management.HotspotThreadMBean;
import sun.management.ManagementFactoryHelper;

import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;
import java.util.Map;

public class HotspotInternals implements HotspotInternalsMBean {

    public Map<String, Long> getInternalThreadCpuTimes() {
        HotspotThreadMBean hstmb = ManagementFactoryHelper.getHotspotThreadMBean();
        if (hstmb == null)
            return null;
        return hstmb.getInternalThreadCpuTimes();
    }

    public long getTotalSafepointTime() {
        HotspotRuntimeMBean hsrmb = ManagementFactoryHelper.getHotspotRuntimeMBean();
        if (hsrmb == null)
            return 0L;
        return hsrmb.getTotalSafepointTime();
    }

    public ObjectName getObjectName() {
        try {
            return new ObjectName(Constants.DOMAIN + ":type=" + Constants.INTERNALS_STATS_TYPE);
        } catch (MalformedObjectNameException e) {
            return null;
        }
    }
}
