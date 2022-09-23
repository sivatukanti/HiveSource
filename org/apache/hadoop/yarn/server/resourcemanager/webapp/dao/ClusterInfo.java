// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.yarn.server.resourcemanager.webapp.dao;

import org.apache.hadoop.util.VersionInfo;
import org.apache.hadoop.yarn.util.YarnVersionInfo;
import org.apache.hadoop.yarn.server.resourcemanager.ResourceManager;
import org.apache.hadoop.ha.HAServiceProtocol;
import org.apache.hadoop.service.Service;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class ClusterInfo
{
    protected long id;
    protected long startedOn;
    protected Service.STATE state;
    protected HAServiceProtocol.HAServiceState haState;
    protected String rmStateStoreName;
    protected String resourceManagerVersion;
    protected String resourceManagerBuildVersion;
    protected String resourceManagerVersionBuiltOn;
    protected String hadoopVersion;
    protected String hadoopBuildVersion;
    protected String hadoopVersionBuiltOn;
    
    public ClusterInfo() {
    }
    
    public ClusterInfo(final ResourceManager rm) {
        final long ts = ResourceManager.getClusterTimeStamp();
        this.id = ts;
        this.state = rm.getServiceState();
        this.haState = rm.getRMContext().getHAServiceState();
        this.rmStateStoreName = rm.getRMContext().getStateStore().getClass().getName();
        this.startedOn = ts;
        this.resourceManagerVersion = YarnVersionInfo.getVersion();
        this.resourceManagerBuildVersion = YarnVersionInfo.getBuildVersion();
        this.resourceManagerVersionBuiltOn = YarnVersionInfo.getDate();
        this.hadoopVersion = VersionInfo.getVersion();
        this.hadoopBuildVersion = VersionInfo.getBuildVersion();
        this.hadoopVersionBuiltOn = VersionInfo.getDate();
    }
    
    public String getState() {
        return this.state.toString();
    }
    
    public String getHAState() {
        return this.haState.toString();
    }
    
    public String getRMStateStore() {
        return this.rmStateStoreName;
    }
    
    public String getRMVersion() {
        return this.resourceManagerVersion;
    }
    
    public String getRMBuildVersion() {
        return this.resourceManagerBuildVersion;
    }
    
    public String getRMVersionBuiltOn() {
        return this.resourceManagerVersionBuiltOn;
    }
    
    public String getHadoopVersion() {
        return this.hadoopVersion;
    }
    
    public String getHadoopBuildVersion() {
        return this.hadoopBuildVersion;
    }
    
    public String getHadoopVersionBuiltOn() {
        return this.hadoopVersionBuiltOn;
    }
    
    public long getClusterId() {
        return this.id;
    }
    
    public long getStartedOn() {
        return this.startedOn;
    }
}
