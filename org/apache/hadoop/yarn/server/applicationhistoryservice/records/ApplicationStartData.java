// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.yarn.server.applicationhistoryservice.records;

import org.apache.hadoop.yarn.util.Records;
import org.apache.hadoop.yarn.api.records.ApplicationId;
import org.apache.hadoop.classification.InterfaceStability;
import org.apache.hadoop.classification.InterfaceAudience;

@InterfaceAudience.Public
@InterfaceStability.Unstable
public abstract class ApplicationStartData
{
    @InterfaceAudience.Public
    @InterfaceStability.Unstable
    public static ApplicationStartData newInstance(final ApplicationId applicationId, final String applicationName, final String applicationType, final String queue, final String user, final long submitTime, final long startTime) {
        final ApplicationStartData appSD = Records.newRecord(ApplicationStartData.class);
        appSD.setApplicationId(applicationId);
        appSD.setApplicationName(applicationName);
        appSD.setApplicationType(applicationType);
        appSD.setQueue(queue);
        appSD.setUser(user);
        appSD.setSubmitTime(submitTime);
        appSD.setStartTime(startTime);
        return appSD;
    }
    
    @InterfaceAudience.Public
    @InterfaceStability.Unstable
    public abstract ApplicationId getApplicationId();
    
    @InterfaceAudience.Public
    @InterfaceStability.Unstable
    public abstract void setApplicationId(final ApplicationId p0);
    
    @InterfaceAudience.Public
    @InterfaceStability.Unstable
    public abstract String getApplicationName();
    
    @InterfaceAudience.Public
    @InterfaceStability.Unstable
    public abstract void setApplicationName(final String p0);
    
    @InterfaceAudience.Public
    @InterfaceStability.Unstable
    public abstract String getApplicationType();
    
    @InterfaceAudience.Public
    @InterfaceStability.Unstable
    public abstract void setApplicationType(final String p0);
    
    @InterfaceAudience.Public
    @InterfaceStability.Unstable
    public abstract String getUser();
    
    @InterfaceAudience.Public
    @InterfaceStability.Unstable
    public abstract void setUser(final String p0);
    
    @InterfaceAudience.Public
    @InterfaceStability.Unstable
    public abstract String getQueue();
    
    @InterfaceAudience.Public
    @InterfaceStability.Unstable
    public abstract void setQueue(final String p0);
    
    @InterfaceAudience.Public
    @InterfaceStability.Unstable
    public abstract long getSubmitTime();
    
    @InterfaceAudience.Public
    @InterfaceStability.Unstable
    public abstract void setSubmitTime(final long p0);
    
    @InterfaceAudience.Public
    @InterfaceStability.Unstable
    public abstract long getStartTime();
    
    @InterfaceAudience.Public
    @InterfaceStability.Unstable
    public abstract void setStartTime(final long p0);
}
