// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.yarn.api.records;

import java.io.IOException;
import org.apache.hadoop.yarn.util.Records;
import java.text.NumberFormat;
import org.apache.hadoop.classification.InterfaceStability;
import org.apache.hadoop.classification.InterfaceAudience;

@InterfaceAudience.Public
@InterfaceStability.Unstable
public abstract class ReservationId implements Comparable<ReservationId>
{
    @InterfaceAudience.Private
    @InterfaceStability.Unstable
    public static final String reserveIdStrPrefix = "reservation_";
    protected long clusterTimestamp;
    protected long id;
    static final ThreadLocal<NumberFormat> reservIdFormat;
    
    @InterfaceAudience.Private
    @InterfaceStability.Unstable
    public static ReservationId newInstance(final long clusterTimestamp, final long id) {
        final ReservationId reservationId = Records.newRecord(ReservationId.class);
        reservationId.setClusterTimestamp(clusterTimestamp);
        reservationId.setId(id);
        reservationId.build();
        return reservationId;
    }
    
    @InterfaceAudience.Public
    @InterfaceStability.Unstable
    public abstract long getId();
    
    @InterfaceAudience.Private
    @InterfaceStability.Unstable
    protected abstract void setId(final long p0);
    
    @InterfaceAudience.Public
    @InterfaceStability.Unstable
    public abstract long getClusterTimestamp();
    
    @InterfaceAudience.Private
    @InterfaceStability.Unstable
    protected abstract void setClusterTimestamp(final long p0);
    
    protected abstract void build();
    
    @Override
    public int compareTo(final ReservationId other) {
        if (this.getClusterTimestamp() - other.getClusterTimestamp() == 0L) {
            return (this.getId() > this.getId()) ? 1 : ((this.getId() < this.getId()) ? -1 : 0);
        }
        return (this.getClusterTimestamp() > other.getClusterTimestamp()) ? 1 : ((this.getClusterTimestamp() < other.getClusterTimestamp()) ? -1 : 0);
    }
    
    @Override
    public String toString() {
        return "reservation_" + this.getClusterTimestamp() + "_" + ReservationId.reservIdFormat.get().format(this.getId());
    }
    
    @InterfaceAudience.Public
    @InterfaceStability.Unstable
    public static ReservationId parseReservationId(final String reservationId) throws IOException {
        if (reservationId == null) {
            return null;
        }
        if (!reservationId.startsWith("reservation_")) {
            throw new IOException("The specified reservation id is invalid: " + reservationId);
        }
        final String[] resFields = reservationId.split("_");
        if (resFields.length != 3) {
            throw new IOException("The specified reservation id is not parseable: " + reservationId);
        }
        return newInstance(Long.parseLong(resFields[1]), Long.parseLong(resFields[2]));
    }
    
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = 31 * result + (int)(this.getClusterTimestamp() ^ this.getClusterTimestamp() >>> 32);
        result = 31 * result + (int)(this.getId() ^ this.getId() >>> 32);
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
        final ReservationId other = (ReservationId)obj;
        return this.getClusterTimestamp() == other.getClusterTimestamp() && this.getId() == other.getId();
    }
    
    static {
        reservIdFormat = new ThreadLocal<NumberFormat>() {
            public NumberFormat initialValue() {
                final NumberFormat fmt = NumberFormat.getInstance();
                fmt.setGroupingUsed(false);
                fmt.setMinimumIntegerDigits(4);
                return fmt;
            }
        };
    }
}
