/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.syncope.common.lib.to;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.ws.rs.PathParam;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import org.apache.syncope.common.lib.report.AbstractReportletConf;

@XmlRootElement(name = "report")
@XmlType
public class ReportTO extends AbstractStartEndBean {

    private static final long serialVersionUID = 5274568072084814410L;

    private long key;

    private String name;

    private final List<AbstractReportletConf> reportletConfs = new ArrayList<>();

    private String cronExpression;

    private final List<ReportExecTO> executions = new ArrayList<>();

    private String latestExecStatus;

    private Date lastExec;

    private Date nextExec;

    public long getKey() {
        return key;
    }

    @PathParam("key")
    public void setKey(final long key) {
        this.key = key;
    }

    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    @XmlElementWrapper(name = "reportletConfs")
    @XmlElement(name = "reportletConf")
    @JsonProperty("reportletConfs")
    public List<AbstractReportletConf> getReportletConfs() {
        return reportletConfs;
    }

    public String getCronExpression() {
        return cronExpression;
    }

    public void setCronExpression(final String cronExpression) {
        this.cronExpression = cronExpression;
    }

    @XmlElementWrapper(name = "executions")
    @XmlElement(name = "execution")
    @JsonProperty("executions")
    public List<ReportExecTO> getExecutions() {
        return executions;
    }

    public String getLatestExecStatus() {
        return latestExecStatus;
    }

    public void setLatestExecStatus(final String latestExecStatus) {
        this.latestExecStatus = latestExecStatus;
    }

    @SuppressWarnings("CPD-START")
    public Date getLastExec() {
        return lastExec == null
                ? null
                : new Date(lastExec.getTime());
    }

    public void setLastExec(final Date lastExec) {
        if (lastExec != null) {
            this.lastExec = new Date(lastExec.getTime());
        }
    }

    public Date getNextExec() {
        return nextExec == null
                ? null
                : new Date(nextExec.getTime());
    }

    public void setNextExec(final Date nextExec) {
        if (nextExec != null) {
            this.nextExec = new Date(nextExec.getTime());
        }
    }

}
