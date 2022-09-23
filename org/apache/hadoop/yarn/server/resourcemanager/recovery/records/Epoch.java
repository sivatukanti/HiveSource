// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.yarn.server.resourcemanager.recovery.records;

import org.apache.hadoop.yarn.proto.YarnServerResourceManagerRecoveryProtos;
import org.apache.hadoop.yarn.util.Records;
import org.apache.hadoop.classification.InterfaceStability;
import org.apache.hadoop.classification.InterfaceAudience;

@InterfaceAudience.Private
@InterfaceStability.Unstable
public abstract class Epoch
{
    public static Epoch newInstance(final long sequenceNumber) {
        final Epoch epoch = Records.newRecord(Epoch.class);
        epoch.setEpoch(sequenceNumber);
        return epoch;
    }
    
    public abstract long getEpoch();
    
    public abstract void setEpoch(final long p0);
    
    public abstract YarnServerResourceManagerRecoveryProtos.EpochProto getProto();
    
    @Override
    public String toString() {
        return String.valueOf(this.getEpoch());
    }
    
    @Override
    public int hashCode() {
        return (int)(this.getEpoch() ^ this.getEpoch() >>> 32);
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
        final Epoch other = (Epoch)obj;
        return this.getEpoch() == other.getEpoch();
    }
}
