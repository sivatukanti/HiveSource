// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.yarn.server.resourcemanager.reservation;

public class ReservationInterval implements Comparable<ReservationInterval>
{
    private final long startTime;
    private final long endTime;
    
    public ReservationInterval(final long startTime, final long endTime) {
        this.startTime = startTime;
        this.endTime = endTime;
    }
    
    public long getStartTime() {
        return this.startTime;
    }
    
    public long getEndTime() {
        return this.endTime;
    }
    
    public boolean isOverlap(final long tick) {
        return this.startTime <= tick && tick <= this.endTime;
    }
    
    @Override
    public int compareTo(final ReservationInterval anotherInterval) {
        long diff = 0L;
        if (this.startTime == anotherInterval.getStartTime()) {
            diff = this.endTime - anotherInterval.getEndTime();
        }
        else {
            diff = this.startTime - anotherInterval.getStartTime();
        }
        if (diff < 0L) {
            return -1;
        }
        if (diff > 0L) {
            return 1;
        }
        return 0;
    }
    
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = 31 * result + (int)(this.endTime ^ this.endTime >>> 32);
        result = 31 * result + (int)(this.startTime ^ this.startTime >>> 32);
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
        if (!(obj instanceof ReservationInterval)) {
            return false;
        }
        final ReservationInterval other = (ReservationInterval)obj;
        return this.endTime == other.endTime && this.startTime == other.startTime;
    }
    
    @Override
    public String toString() {
        return "[" + this.startTime + ", " + this.endTime + "]";
    }
}
