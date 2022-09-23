// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.yarn.server.resourcemanager.reservation;

import org.apache.hadoop.yarn.util.resource.Resources;
import java.util.Collections;
import java.util.Iterator;
import org.apache.hadoop.yarn.api.records.Resource;
import org.apache.hadoop.yarn.util.resource.ResourceCalculator;
import org.apache.hadoop.yarn.api.records.ReservationRequest;
import java.util.Map;
import org.apache.hadoop.yarn.api.records.ReservationDefinition;
import org.apache.hadoop.yarn.api.records.ReservationId;

class InMemoryReservationAllocation implements ReservationAllocation
{
    private final String planName;
    private final ReservationId reservationID;
    private final String user;
    private final ReservationDefinition contract;
    private final long startTime;
    private final long endTime;
    private final Map<ReservationInterval, ReservationRequest> allocationRequests;
    private boolean hasGang;
    private long acceptedAt;
    private RLESparseResourceAllocation resourcesOverTime;
    
    InMemoryReservationAllocation(final ReservationId reservationID, final ReservationDefinition contract, final String user, final String planName, final long startTime, final long endTime, final Map<ReservationInterval, ReservationRequest> allocationRequests, final ResourceCalculator calculator, final Resource minAlloc) {
        this.hasGang = false;
        this.acceptedAt = -1L;
        this.contract = contract;
        this.startTime = startTime;
        this.endTime = endTime;
        this.reservationID = reservationID;
        this.user = user;
        this.allocationRequests = allocationRequests;
        this.planName = planName;
        this.resourcesOverTime = new RLESparseResourceAllocation(calculator, minAlloc);
        for (final Map.Entry<ReservationInterval, ReservationRequest> r : allocationRequests.entrySet()) {
            this.resourcesOverTime.addInterval(r.getKey(), r.getValue());
            if (r.getValue().getConcurrency() > 1) {
                this.hasGang = true;
            }
        }
    }
    
    @Override
    public ReservationId getReservationId() {
        return this.reservationID;
    }
    
    @Override
    public ReservationDefinition getReservationDefinition() {
        return this.contract;
    }
    
    @Override
    public long getStartTime() {
        return this.startTime;
    }
    
    @Override
    public long getEndTime() {
        return this.endTime;
    }
    
    @Override
    public Map<ReservationInterval, ReservationRequest> getAllocationRequests() {
        return Collections.unmodifiableMap((Map<? extends ReservationInterval, ? extends ReservationRequest>)this.allocationRequests);
    }
    
    @Override
    public String getPlanName() {
        return this.planName;
    }
    
    @Override
    public String getUser() {
        return this.user;
    }
    
    @Override
    public boolean containsGangs() {
        return this.hasGang;
    }
    
    @Override
    public void setAcceptanceTimestamp(final long acceptedAt) {
        this.acceptedAt = acceptedAt;
    }
    
    @Override
    public long getAcceptanceTime() {
        return this.acceptedAt;
    }
    
    @Override
    public Resource getResourcesAtTime(final long tick) {
        if (tick < this.startTime || tick >= this.endTime) {
            return Resource.newInstance(0, 0);
        }
        return Resources.clone(this.resourcesOverTime.getCapacityAtTime(tick));
    }
    
    @Override
    public String toString() {
        final StringBuilder sBuf = new StringBuilder();
        sBuf.append(this.getReservationId()).append(" user:").append(this.getUser()).append(" startTime: ").append(this.getStartTime()).append(" endTime: ").append(this.getEndTime()).append(" alloc:[").append(this.resourcesOverTime.toString()).append("] ");
        return sBuf.toString();
    }
    
    @Override
    public int compareTo(final ReservationAllocation other) {
        if (this.getAcceptanceTime() > other.getAcceptanceTime()) {
            return -1;
        }
        if (this.getAcceptanceTime() < other.getAcceptanceTime()) {
            return 1;
        }
        return 0;
    }
    
    @Override
    public int hashCode() {
        return this.reservationID.hashCode();
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
        final InMemoryReservationAllocation other = (InMemoryReservationAllocation)obj;
        return this.reservationID.equals(other.getReservationId());
    }
}
