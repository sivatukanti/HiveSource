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
public abstract class ApplicationId implements Comparable<ApplicationId>
{
    @InterfaceAudience.Private
    @InterfaceStability.Unstable
    public static final String appIdStrPrefix = "application_";
    static final ThreadLocal<NumberFormat> appIdFormat;
    
    @InterfaceAudience.Private
    @InterfaceStability.Unstable
    public static ApplicationId newInstance(final long clusterTimestamp, final int id) {
        final ApplicationId appId = Records.newRecord(ApplicationId.class);
        appId.setClusterTimestamp(clusterTimestamp);
        appId.setId(id);
        appId.build();
        return appId;
    }
    
    @InterfaceAudience.Public
    @InterfaceStability.Stable
    public abstract int getId();
    
    @InterfaceAudience.Private
    @InterfaceStability.Unstable
    protected abstract void setId(final int p0);
    
    @InterfaceAudience.Public
    @InterfaceStability.Stable
    public abstract long getClusterTimestamp();
    
    @InterfaceAudience.Private
    @InterfaceStability.Unstable
    protected abstract void setClusterTimestamp(final long p0);
    
    protected abstract void build();
    
    @Override
    public int compareTo(final ApplicationId other) {
        if (this.getClusterTimestamp() - other.getClusterTimestamp() == 0L) {
            return this.getId() - other.getId();
        }
        return (this.getClusterTimestamp() > other.getClusterTimestamp()) ? 1 : ((this.getClusterTimestamp() < other.getClusterTimestamp()) ? -1 : 0);
    }
    
    @Override
    public String toString() {
        return "application_" + this.getClusterTimestamp() + "_" + ApplicationId.appIdFormat.get().format(this.getId());
    }
    
    @Override
    public int hashCode() {
        final int prime = 371237;
        int result = 6521;
        final long clusterTimestamp = this.getClusterTimestamp();
        result = 371237 * result + (int)(clusterTimestamp ^ clusterTimestamp >>> 32);
        result = 371237 * result + this.getId();
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
        final ApplicationId other = (ApplicationId)obj;
        return this.getClusterTimestamp() == other.getClusterTimestamp() && this.getId() == other.getId();
    }
    
    static {
        appIdFormat = new ThreadLocal<NumberFormat>() {
            public NumberFormat initialValue() {
                final NumberFormat fmt = NumberFormat.getInstance();
                fmt.setGroupingUsed(false);
                fmt.setMinimumIntegerDigits(4);
                return fmt;
            }
        };
    }
}
