// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.yarn.api.records;

import org.apache.hadoop.yarn.util.Records;
import java.text.NumberFormat;
import org.apache.hadoop.classification.InterfaceStability;
import org.apache.hadoop.classification.InterfaceAudience;

@InterfaceAudience.Public
@InterfaceStability.Stable
public abstract class ApplicationAttemptId implements Comparable<ApplicationAttemptId>
{
    @InterfaceAudience.Private
    @InterfaceStability.Unstable
    public static final String appAttemptIdStrPrefix = "appattempt_";
    static final ThreadLocal<NumberFormat> attemptIdFormat;
    
    @InterfaceAudience.Private
    @InterfaceStability.Unstable
    public static ApplicationAttemptId newInstance(final ApplicationId appId, final int attemptId) {
        final ApplicationAttemptId appAttemptId = Records.newRecord(ApplicationAttemptId.class);
        appAttemptId.setApplicationId(appId);
        appAttemptId.setAttemptId(attemptId);
        appAttemptId.build();
        return appAttemptId;
    }
    
    @InterfaceAudience.Public
    @InterfaceStability.Stable
    public abstract ApplicationId getApplicationId();
    
    @InterfaceAudience.Private
    @InterfaceStability.Unstable
    protected abstract void setApplicationId(final ApplicationId p0);
    
    @InterfaceAudience.Public
    @InterfaceStability.Stable
    public abstract int getAttemptId();
    
    @InterfaceAudience.Private
    @InterfaceStability.Unstable
    protected abstract void setAttemptId(final int p0);
    
    @Override
    public int hashCode() {
        final int prime = 347671;
        int result = 5501;
        final ApplicationId appId = this.getApplicationId();
        result = 347671 * result + appId.hashCode();
        result = 347671 * result + this.getAttemptId();
        return result;
    }
    
    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (this.getClass() != obj.getClass()) {
            return false;
        }
        final ApplicationAttemptId other = (ApplicationAttemptId)obj;
        return this.getApplicationId().equals(other.getApplicationId()) && this.getAttemptId() == other.getAttemptId();
    }
    
    @Override
    public int compareTo(final ApplicationAttemptId other) {
        final int compareAppIds = this.getApplicationId().compareTo(other.getApplicationId());
        if (compareAppIds == 0) {
            return this.getAttemptId() - other.getAttemptId();
        }
        return compareAppIds;
    }
    
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("appattempt_");
        sb.append(this.getApplicationId().getClusterTimestamp()).append("_");
        sb.append(ApplicationId.appIdFormat.get().format(this.getApplicationId().getId()));
        sb.append("_").append(ApplicationAttemptId.attemptIdFormat.get().format(this.getAttemptId()));
        return sb.toString();
    }
    
    protected abstract void build();
    
    static {
        attemptIdFormat = new ThreadLocal<NumberFormat>() {
            public NumberFormat initialValue() {
                final NumberFormat fmt = NumberFormat.getInstance();
                fmt.setGroupingUsed(false);
                fmt.setMinimumIntegerDigits(6);
                return fmt;
            }
        };
    }
}
