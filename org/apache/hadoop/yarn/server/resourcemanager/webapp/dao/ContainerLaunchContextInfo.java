// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.yarn.server.resourcemanager.webapp.dao;

import java.util.Map;
import java.util.ArrayList;
import org.apache.hadoop.yarn.api.records.ApplicationAccessType;
import javax.xml.bind.annotation.XmlElement;
import java.util.List;
import javax.xml.bind.annotation.XmlElementWrapper;
import java.util.HashMap;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "container-launch-context-info")
@XmlAccessorType(XmlAccessType.FIELD)
public class ContainerLaunchContextInfo
{
    @XmlElementWrapper(name = "local-resources")
    HashMap<String, LocalResourceInfo> local_resources;
    HashMap<String, String> environment;
    @XmlElementWrapper(name = "commands")
    @XmlElement(name = "command", type = String.class)
    List<String> commands;
    @XmlElementWrapper(name = "service-data")
    HashMap<String, String> servicedata;
    @XmlElement(name = "credentials")
    CredentialsInfo credentials;
    @XmlElementWrapper(name = "application-acls")
    HashMap<ApplicationAccessType, String> acls;
    
    public ContainerLaunchContextInfo() {
        this.local_resources = new HashMap<String, LocalResourceInfo>();
        this.environment = new HashMap<String, String>();
        this.commands = new ArrayList<String>();
        this.servicedata = new HashMap<String, String>();
        this.credentials = new CredentialsInfo();
        this.acls = new HashMap<ApplicationAccessType, String>();
    }
    
    public Map<String, LocalResourceInfo> getResources() {
        return this.local_resources;
    }
    
    public Map<String, String> getEnvironment() {
        return this.environment;
    }
    
    public List<String> getCommands() {
        return this.commands;
    }
    
    public Map<String, String> getAuxillaryServiceData() {
        return this.servicedata;
    }
    
    public CredentialsInfo getCredentials() {
        return this.credentials;
    }
    
    public Map<ApplicationAccessType, String> getAcls() {
        return this.acls;
    }
    
    public void setResources(final HashMap<String, LocalResourceInfo> resources) {
        this.local_resources = resources;
    }
    
    public void setEnvironment(final HashMap<String, String> environment) {
        this.environment = environment;
    }
    
    public void setCommands(final List<String> commands) {
        this.commands = commands;
    }
    
    public void setAuxillaryServiceData(final HashMap<String, String> serviceData) {
        this.servicedata = serviceData;
    }
    
    public void setCredentials(final CredentialsInfo credentials) {
        this.credentials = credentials;
    }
    
    public void setAcls(final HashMap<ApplicationAccessType, String> acls) {
        this.acls = acls;
    }
}
