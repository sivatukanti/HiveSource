// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.yarn.api.records;

import java.util.Iterator;
import org.apache.hadoop.yarn.util.Records;
import java.text.NumberFormat;
import com.google.common.base.Splitter;
import org.apache.hadoop.classification.InterfaceStability;
import org.apache.hadoop.classification.InterfaceAudience;

@InterfaceAudience.Public
@InterfaceStability.Stable
public abstract class ContainerId implements Comparable<ContainerId>
{
    public static final long CONTAINER_ID_BITMASK = 1099511627775L;
    private static final Splitter _SPLITTER;
    private static final String CONTAINER_PREFIX = "container";
    private static final String EPOCH_PREFIX = "e";
    private static final ThreadLocal<NumberFormat> appAttemptIdAndEpochFormat;
    private static final ThreadLocal<NumberFormat> containerIdFormat;
    
    @InterfaceAudience.Private
    @InterfaceStability.Unstable
    public static ContainerId newContainerId(final ApplicationAttemptId appAttemptId, final long containerId) {
        final ContainerId id = Records.newRecord(ContainerId.class);
        id.setContainerId(containerId);
        id.setApplicationAttemptId(appAttemptId);
        id.build();
        return id;
    }
    
    @InterfaceAudience.Private
    @Deprecated
    @InterfaceStability.Unstable
    public static ContainerId newInstance(final ApplicationAttemptId appAttemptId, final int containerId) {
        final ContainerId id = Records.newRecord(ContainerId.class);
        id.setContainerId(containerId);
        id.setApplicationAttemptId(appAttemptId);
        id.build();
        return id;
    }
    
    @InterfaceAudience.Public
    @InterfaceStability.Stable
    public abstract ApplicationAttemptId getApplicationAttemptId();
    
    @InterfaceAudience.Private
    @InterfaceStability.Unstable
    protected abstract void setApplicationAttemptId(final ApplicationAttemptId p0);
    
    @InterfaceAudience.Public
    @Deprecated
    @InterfaceStability.Stable
    public abstract int getId();
    
    @InterfaceAudience.Public
    @InterfaceStability.Unstable
    public abstract long getContainerId();
    
    @InterfaceAudience.Private
    @InterfaceStability.Unstable
    protected abstract void setContainerId(final long p0);
    
    @Override
    public int hashCode() {
        int result = (int)(this.getContainerId() ^ this.getContainerId() >>> 32);
        result = 31 * result + this.getApplicationAttemptId().hashCode();
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
        final ContainerId other = (ContainerId)obj;
        return this.getApplicationAttemptId().equals(other.getApplicationAttemptId()) && this.getContainerId() == other.getContainerId();
    }
    
    @Override
    public int compareTo(final ContainerId other) {
        if (this.getApplicationAttemptId().compareTo(other.getApplicationAttemptId()) == 0) {
            return Long.valueOf(this.getContainerId()).compareTo(other.getContainerId());
        }
        return this.getApplicationAttemptId().compareTo(other.getApplicationAttemptId());
    }
    
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append("container_");
        final long epoch = this.getContainerId() >> 40;
        if (epoch > 0L) {
            sb.append("e").append(ContainerId.appAttemptIdAndEpochFormat.get().format(epoch)).append("_");
        }
        final ApplicationId appId = this.getApplicationAttemptId().getApplicationId();
        sb.append(appId.getClusterTimestamp()).append("_");
        sb.append(ApplicationId.appIdFormat.get().format(appId.getId())).append("_");
        sb.append(ContainerId.appAttemptIdAndEpochFormat.get().format(this.getApplicationAttemptId().getAttemptId())).append("_");
        sb.append(ContainerId.containerIdFormat.get().format(0xFFFFFFFFFFL & this.getContainerId()));
        return sb.toString();
    }
    
    @InterfaceAudience.Public
    @InterfaceStability.Unstable
    public static ContainerId fromString(final String containerIdStr) {
        final Iterator<String> it = ContainerId._SPLITTER.split(containerIdStr).iterator();
        if (!it.next().equals("container")) {
            throw new IllegalArgumentException("Invalid ContainerId prefix: " + containerIdStr);
        }
        try {
            final String epochOrClusterTimestampStr = it.next();
            long epoch = 0L;
            ApplicationAttemptId appAttemptID = null;
            if (epochOrClusterTimestampStr.startsWith("e")) {
                final String epochStr = epochOrClusterTimestampStr;
                epoch = Integer.parseInt(epochStr.substring("e".length()));
                appAttemptID = toApplicationAttemptId(it);
            }
            else {
                final String clusterTimestampStr = epochOrClusterTimestampStr;
                final long clusterTimestamp = Long.parseLong(clusterTimestampStr);
                appAttemptID = toApplicationAttemptId(clusterTimestamp, it);
            }
            final long id = Long.parseLong(it.next());
            final long cid = epoch << 40 | id;
            final ContainerId containerId = newContainerId(appAttemptID, cid);
            return containerId;
        }
        catch (NumberFormatException n) {
            throw new IllegalArgumentException("Invalid ContainerId: " + containerIdStr, n);
        }
    }
    
    private static ApplicationAttemptId toApplicationAttemptId(final Iterator<String> it) throws NumberFormatException {
        return toApplicationAttemptId(Long.parseLong(it.next()), it);
    }
    
    private static ApplicationAttemptId toApplicationAttemptId(final long clusterTimestamp, final Iterator<String> it) throws NumberFormatException {
        final ApplicationId appId = ApplicationId.newInstance(clusterTimestamp, Integer.parseInt(it.next()));
        final ApplicationAttemptId appAttemptId = ApplicationAttemptId.newInstance(appId, Integer.parseInt(it.next()));
        return appAttemptId;
    }
    
    protected abstract void build();
    
    static {
        _SPLITTER = Splitter.on('_').trimResults();
        appAttemptIdAndEpochFormat = new ThreadLocal<NumberFormat>() {
            public NumberFormat initialValue() {
                final NumberFormat fmt = NumberFormat.getInstance();
                fmt.setGroupingUsed(false);
                fmt.setMinimumIntegerDigits(2);
                return fmt;
            }
        };
        containerIdFormat = new ThreadLocal<NumberFormat>() {
            public NumberFormat initialValue() {
                final NumberFormat fmt = NumberFormat.getInstance();
                fmt.setGroupingUsed(false);
                fmt.setMinimumIntegerDigits(6);
                return fmt;
            }
        };
    }
}
