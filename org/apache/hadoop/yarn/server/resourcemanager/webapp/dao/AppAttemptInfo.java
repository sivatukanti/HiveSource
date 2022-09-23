// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.yarn.server.resourcemanager.webapp.dao;

import org.apache.hadoop.yarn.api.records.Container;
import org.apache.hadoop.yarn.webapp.util.WebAppUtils;
import org.apache.hadoop.yarn.util.ConverterUtils;
import org.apache.hadoop.yarn.server.resourcemanager.rmapp.attempt.RMAppAttempt;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "appAttempt")
@XmlAccessorType(XmlAccessType.FIELD)
public class AppAttemptInfo
{
    protected int id;
    protected long startTime;
    protected String containerId;
    protected String nodeHttpAddress;
    protected String nodeId;
    protected String logsLink;
    
    public AppAttemptInfo() {
    }
    
    public AppAttemptInfo(final RMAppAttempt attempt, final String user) {
        this.startTime = 0L;
        this.containerId = "";
        this.nodeHttpAddress = "";
        this.nodeId = "";
        this.logsLink = "";
        if (attempt != null) {
            this.id = attempt.getAppAttemptId().getAttemptId();
            this.startTime = attempt.getStartTime();
            final Container masterContainer = attempt.getMasterContainer();
            if (masterContainer != null) {
                this.containerId = masterContainer.getId().toString();
                this.nodeHttpAddress = masterContainer.getNodeHttpAddress();
                this.nodeId = masterContainer.getNodeId().toString();
                this.logsLink = WebAppUtils.getRunningLogURL("//" + masterContainer.getNodeHttpAddress(), ConverterUtils.toString(masterContainer.getId()), user);
            }
        }
    }
    
    public int getAttemptId() {
        return this.id;
    }
    
    public long getStartTime() {
        return this.startTime;
    }
    
    public String getNodeHttpAddress() {
        return this.nodeHttpAddress;
    }
    
    public String getLogsLink() {
        return this.logsLink;
    }
}
