// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.yarn.server.resourcemanager.webapp.dao;

import java.util.HashSet;
import org.apache.hadoop.yarn.api.records.Priority;
import javax.xml.bind.annotation.XmlElementWrapper;
import java.util.Set;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "application-submission-context")
@XmlAccessorType(XmlAccessType.FIELD)
public class ApplicationSubmissionContextInfo
{
    @XmlElement(name = "application-id")
    String applicationId;
    @XmlElement(name = "application-name")
    String applicationName;
    String queue;
    int priority;
    @XmlElement(name = "am-container-spec")
    ContainerLaunchContextInfo containerInfo;
    @XmlElement(name = "unmanaged-AM")
    boolean isUnmanagedAM;
    @XmlElement(name = "cancel-tokens-when-complete")
    boolean cancelTokensWhenComplete;
    @XmlElement(name = "max-app-attempts")
    int maxAppAttempts;
    @XmlElement(name = "resource")
    ResourceInfo resource;
    @XmlElement(name = "application-type")
    String applicationType;
    @XmlElement(name = "keep-containers-across-application-attempts")
    boolean keepContainers;
    @XmlElementWrapper(name = "application-tags")
    @XmlElement(name = "tag")
    Set<String> tags;
    @XmlElement(name = "app-node-label-expression")
    String appNodeLabelExpression;
    @XmlElement(name = "am-container-node-label-expression")
    String amContainerNodeLabelExpression;
    
    public ApplicationSubmissionContextInfo() {
        this.applicationId = "";
        this.applicationName = "";
        this.containerInfo = new ContainerLaunchContextInfo();
        this.resource = new ResourceInfo();
        this.priority = Priority.UNDEFINED.getPriority();
        this.isUnmanagedAM = false;
        this.cancelTokensWhenComplete = true;
        this.keepContainers = false;
        this.applicationType = "";
        this.tags = new HashSet<String>();
        this.appNodeLabelExpression = "";
        this.amContainerNodeLabelExpression = "";
    }
    
    public String getApplicationId() {
        return this.applicationId;
    }
    
    public String getApplicationName() {
        return this.applicationName;
    }
    
    public String getQueue() {
        return this.queue;
    }
    
    public int getPriority() {
        return this.priority;
    }
    
    public ContainerLaunchContextInfo getContainerLaunchContextInfo() {
        return this.containerInfo;
    }
    
    public boolean getUnmanagedAM() {
        return this.isUnmanagedAM;
    }
    
    public boolean getCancelTokensWhenComplete() {
        return this.cancelTokensWhenComplete;
    }
    
    public int getMaxAppAttempts() {
        return this.maxAppAttempts;
    }
    
    public ResourceInfo getResource() {
        return this.resource;
    }
    
    public String getApplicationType() {
        return this.applicationType;
    }
    
    public boolean getKeepContainersAcrossApplicationAttempts() {
        return this.keepContainers;
    }
    
    public Set<String> getApplicationTags() {
        return this.tags;
    }
    
    public String getAppNodeLabelExpression() {
        return this.appNodeLabelExpression;
    }
    
    public String getAMContainerNodeLabelExpression() {
        return this.amContainerNodeLabelExpression;
    }
    
    public void setApplicationId(final String applicationId) {
        this.applicationId = applicationId;
    }
    
    public void setApplicationName(final String applicationName) {
        this.applicationName = applicationName;
    }
    
    public void setQueue(final String queue) {
        this.queue = queue;
    }
    
    public void setPriority(final int priority) {
        this.priority = priority;
    }
    
    public void setContainerLaunchContextInfo(final ContainerLaunchContextInfo containerLaunchContext) {
        this.containerInfo = containerLaunchContext;
    }
    
    public void setUnmanagedAM(final boolean isUnmanagedAM) {
        this.isUnmanagedAM = isUnmanagedAM;
    }
    
    public void setCancelTokensWhenComplete(final boolean cancelTokensWhenComplete) {
        this.cancelTokensWhenComplete = cancelTokensWhenComplete;
    }
    
    public void setMaxAppAttempts(final int maxAppAttempts) {
        this.maxAppAttempts = maxAppAttempts;
    }
    
    public void setResource(final ResourceInfo resource) {
        this.resource = resource;
    }
    
    public void setApplicationType(final String applicationType) {
        this.applicationType = applicationType;
    }
    
    public void setKeepContainersAcrossApplicationAttempts(final boolean keepContainers) {
        this.keepContainers = keepContainers;
    }
    
    public void setApplicationTags(final Set<String> tags) {
        this.tags = tags;
    }
    
    public void setAppNodeLabelExpression(final String appNodeLabelExpression) {
        this.appNodeLabelExpression = appNodeLabelExpression;
    }
    
    public void setAMContainerNodeLabelExpression(final String nodeLabelExpression) {
        this.amContainerNodeLabelExpression = nodeLabelExpression;
    }
}
