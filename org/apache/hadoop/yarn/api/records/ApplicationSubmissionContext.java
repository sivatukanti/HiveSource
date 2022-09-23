// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.yarn.api.records;

import java.util.Set;
import org.apache.hadoop.yarn.util.Records;
import org.apache.hadoop.classification.InterfaceStability;
import org.apache.hadoop.classification.InterfaceAudience;

@InterfaceAudience.Public
@InterfaceStability.Stable
public abstract class ApplicationSubmissionContext
{
    @InterfaceAudience.Public
    @InterfaceStability.Stable
    public static ApplicationSubmissionContext newInstance(final ApplicationId applicationId, final String applicationName, final String queue, final Priority priority, final ContainerLaunchContext amContainer, final boolean isUnmanagedAM, final boolean cancelTokensWhenComplete, final int maxAppAttempts, final Resource resource, final String applicationType, final boolean keepContainers, final String appLabelExpression, final String amContainerLabelExpression) {
        final ApplicationSubmissionContext context = Records.newRecord(ApplicationSubmissionContext.class);
        context.setApplicationId(applicationId);
        context.setApplicationName(applicationName);
        context.setQueue(queue);
        context.setPriority(priority);
        context.setAMContainerSpec(amContainer);
        context.setUnmanagedAM(isUnmanagedAM);
        context.setCancelTokensWhenComplete(cancelTokensWhenComplete);
        context.setMaxAppAttempts(maxAppAttempts);
        context.setApplicationType(applicationType);
        context.setKeepContainersAcrossApplicationAttempts(keepContainers);
        context.setNodeLabelExpression(appLabelExpression);
        context.setResource(resource);
        final ResourceRequest amReq = Records.newRecord(ResourceRequest.class);
        amReq.setResourceName("*");
        amReq.setCapability(resource);
        amReq.setNumContainers(1);
        amReq.setRelaxLocality(true);
        amReq.setNodeLabelExpression(amContainerLabelExpression);
        context.setAMContainerResourceRequest(amReq);
        return context;
    }
    
    public static ApplicationSubmissionContext newInstance(final ApplicationId applicationId, final String applicationName, final String queue, final Priority priority, final ContainerLaunchContext amContainer, final boolean isUnmanagedAM, final boolean cancelTokensWhenComplete, final int maxAppAttempts, final Resource resource, final String applicationType, final boolean keepContainers) {
        return newInstance(applicationId, applicationName, queue, priority, amContainer, isUnmanagedAM, cancelTokensWhenComplete, maxAppAttempts, resource, applicationType, keepContainers, null, null);
    }
    
    @InterfaceAudience.Public
    @InterfaceStability.Stable
    public static ApplicationSubmissionContext newInstance(final ApplicationId applicationId, final String applicationName, final String queue, final Priority priority, final ContainerLaunchContext amContainer, final boolean isUnmanagedAM, final boolean cancelTokensWhenComplete, final int maxAppAttempts, final Resource resource, final String applicationType) {
        return newInstance(applicationId, applicationName, queue, priority, amContainer, isUnmanagedAM, cancelTokensWhenComplete, maxAppAttempts, resource, applicationType, false, null, null);
    }
    
    @InterfaceAudience.Public
    @InterfaceStability.Stable
    public static ApplicationSubmissionContext newInstance(final ApplicationId applicationId, final String applicationName, final String queue, final Priority priority, final ContainerLaunchContext amContainer, final boolean isUnmanagedAM, final boolean cancelTokensWhenComplete, final int maxAppAttempts, final Resource resource) {
        return newInstance(applicationId, applicationName, queue, priority, amContainer, isUnmanagedAM, cancelTokensWhenComplete, maxAppAttempts, resource, null);
    }
    
    @InterfaceAudience.Public
    @InterfaceStability.Stable
    public static ApplicationSubmissionContext newInstance(final ApplicationId applicationId, final String applicationName, final String queue, final ContainerLaunchContext amContainer, final boolean isUnmanagedAM, final boolean cancelTokensWhenComplete, final int maxAppAttempts, final String applicationType, final boolean keepContainers, final String appLabelExpression, final ResourceRequest resourceRequest) {
        final ApplicationSubmissionContext context = Records.newRecord(ApplicationSubmissionContext.class);
        context.setApplicationId(applicationId);
        context.setApplicationName(applicationName);
        context.setQueue(queue);
        context.setAMContainerSpec(amContainer);
        context.setUnmanagedAM(isUnmanagedAM);
        context.setCancelTokensWhenComplete(cancelTokensWhenComplete);
        context.setMaxAppAttempts(maxAppAttempts);
        context.setApplicationType(applicationType);
        context.setKeepContainersAcrossApplicationAttempts(keepContainers);
        context.setAMContainerResourceRequest(resourceRequest);
        return context;
    }
    
    @InterfaceAudience.Public
    @InterfaceStability.Stable
    public static ApplicationSubmissionContext newInstance(final ApplicationId applicationId, final String applicationName, final String queue, final Priority priority, final ContainerLaunchContext amContainer, final boolean isUnmanagedAM, final boolean cancelTokensWhenComplete, final int maxAppAttempts, final Resource resource, final String applicationType, final boolean keepContainers, final long attemptFailuresValidityInterval) {
        final ApplicationSubmissionContext context = newInstance(applicationId, applicationName, queue, priority, amContainer, isUnmanagedAM, cancelTokensWhenComplete, maxAppAttempts, resource, applicationType, keepContainers);
        context.setAttemptFailuresValidityInterval(attemptFailuresValidityInterval);
        return context;
    }
    
    @InterfaceAudience.Public
    @InterfaceStability.Stable
    public static ApplicationSubmissionContext newInstance(final ApplicationId applicationId, final String applicationName, final String queue, final Priority priority, final ContainerLaunchContext amContainer, final boolean isUnmanagedAM, final boolean cancelTokensWhenComplete, final int maxAppAttempts, final Resource resource, final String applicationType, final boolean keepContainers, final LogAggregationContext logAggregationContext) {
        final ApplicationSubmissionContext context = newInstance(applicationId, applicationName, queue, priority, amContainer, isUnmanagedAM, cancelTokensWhenComplete, maxAppAttempts, resource, applicationType, keepContainers);
        context.setLogAggregationContext(logAggregationContext);
        return context;
    }
    
    @InterfaceAudience.Public
    @InterfaceStability.Stable
    public abstract ApplicationId getApplicationId();
    
    @InterfaceAudience.Public
    @InterfaceStability.Stable
    public abstract void setApplicationId(final ApplicationId p0);
    
    @InterfaceAudience.Public
    @InterfaceStability.Stable
    public abstract String getApplicationName();
    
    @InterfaceAudience.Public
    @InterfaceStability.Stable
    public abstract void setApplicationName(final String p0);
    
    @InterfaceAudience.Public
    @InterfaceStability.Stable
    public abstract String getQueue();
    
    @InterfaceAudience.Public
    @InterfaceStability.Stable
    public abstract void setQueue(final String p0);
    
    @InterfaceAudience.Public
    @InterfaceStability.Stable
    public abstract Priority getPriority();
    
    @InterfaceAudience.Private
    @InterfaceStability.Unstable
    public abstract void setPriority(final Priority p0);
    
    @InterfaceAudience.Public
    @InterfaceStability.Stable
    public abstract ContainerLaunchContext getAMContainerSpec();
    
    @InterfaceAudience.Public
    @InterfaceStability.Stable
    public abstract void setAMContainerSpec(final ContainerLaunchContext p0);
    
    @InterfaceAudience.Public
    @InterfaceStability.Stable
    public abstract boolean getUnmanagedAM();
    
    @InterfaceAudience.Public
    @InterfaceStability.Stable
    public abstract void setUnmanagedAM(final boolean p0);
    
    @InterfaceAudience.LimitedPrivate({ "mapreduce" })
    @InterfaceStability.Unstable
    public abstract boolean getCancelTokensWhenComplete();
    
    @InterfaceAudience.LimitedPrivate({ "mapreduce" })
    @InterfaceStability.Unstable
    public abstract void setCancelTokensWhenComplete(final boolean p0);
    
    @InterfaceAudience.Public
    @InterfaceStability.Stable
    public abstract int getMaxAppAttempts();
    
    @InterfaceAudience.Public
    @InterfaceStability.Stable
    public abstract void setMaxAppAttempts(final int p0);
    
    @InterfaceAudience.Public
    public abstract Resource getResource();
    
    @InterfaceAudience.Public
    public abstract void setResource(final Resource p0);
    
    @InterfaceAudience.Public
    @InterfaceStability.Stable
    public abstract String getApplicationType();
    
    @InterfaceAudience.Public
    @InterfaceStability.Stable
    public abstract void setApplicationType(final String p0);
    
    @InterfaceAudience.Public
    @InterfaceStability.Stable
    public abstract boolean getKeepContainersAcrossApplicationAttempts();
    
    @InterfaceAudience.Public
    @InterfaceStability.Stable
    public abstract void setKeepContainersAcrossApplicationAttempts(final boolean p0);
    
    @InterfaceAudience.Public
    @InterfaceStability.Stable
    public abstract Set<String> getApplicationTags();
    
    @InterfaceAudience.Public
    @InterfaceStability.Stable
    public abstract void setApplicationTags(final Set<String> p0);
    
    @InterfaceAudience.Public
    @InterfaceStability.Evolving
    public abstract String getNodeLabelExpression();
    
    @InterfaceAudience.Public
    @InterfaceStability.Evolving
    public abstract void setNodeLabelExpression(final String p0);
    
    @InterfaceAudience.Public
    @InterfaceStability.Evolving
    public abstract ResourceRequest getAMContainerResourceRequest();
    
    @InterfaceAudience.Public
    @InterfaceStability.Evolving
    public abstract void setAMContainerResourceRequest(final ResourceRequest p0);
    
    @InterfaceAudience.Public
    @InterfaceStability.Stable
    public abstract long getAttemptFailuresValidityInterval();
    
    @InterfaceAudience.Public
    @InterfaceStability.Stable
    public abstract void setAttemptFailuresValidityInterval(final long p0);
    
    @InterfaceAudience.Public
    @InterfaceStability.Stable
    public abstract LogAggregationContext getLogAggregationContext();
    
    @InterfaceAudience.Public
    @InterfaceStability.Stable
    public abstract void setLogAggregationContext(final LogAggregationContext p0);
    
    @InterfaceAudience.Public
    @InterfaceStability.Unstable
    public abstract ReservationId getReservationID();
    
    @InterfaceAudience.Public
    @InterfaceStability.Unstable
    public abstract void setReservationID(final ReservationId p0);
}
