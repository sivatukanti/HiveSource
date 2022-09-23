// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.yarn.server.resourcemanager.webapp.dao;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "NewApplication")
@XmlAccessorType(XmlAccessType.FIELD)
public class NewApplication
{
    @XmlElement(name = "application-id")
    String applicationId;
    @XmlElement(name = "maximum-resource-capability")
    ResourceInfo maximumResourceCapability;
    
    public NewApplication() {
        this.applicationId = "";
        this.maximumResourceCapability = new ResourceInfo();
    }
    
    public NewApplication(final String appId, final ResourceInfo maxResources) {
        this.applicationId = appId;
        this.maximumResourceCapability = maxResources;
    }
    
    public String getApplicationId() {
        return this.applicationId;
    }
    
    public ResourceInfo getMaximumResourceCapability() {
        return this.maximumResourceCapability;
    }
}
