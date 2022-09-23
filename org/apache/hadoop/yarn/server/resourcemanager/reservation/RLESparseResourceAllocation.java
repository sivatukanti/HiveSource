// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.yarn.server.resourcemanager.reservation;

import java.io.IOException;
import java.io.Writer;
import com.google.gson.stream.JsonWriter;
import java.io.StringWriter;
import java.util.SortedMap;
import org.apache.hadoop.yarn.util.Records;
import java.util.List;
import java.util.Iterator;
import java.util.Set;
import java.util.NavigableMap;
import org.apache.hadoop.yarn.util.resource.Resources;
import org.apache.hadoop.yarn.api.records.ReservationRequest;
import java.util.Map;
import org.apache.hadoop.yarn.util.resource.ResourceCalculator;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.TreeMap;
import org.apache.hadoop.yarn.api.records.Resource;

public class RLESparseResourceAllocation
{
    private static final int THRESHOLD = 100;
    private static final Resource ZERO_RESOURCE;
    private TreeMap<Long, Resource> cumulativeCapacity;
    private final ReentrantReadWriteLock readWriteLock;
    private final Lock readLock;
    private final Lock writeLock;
    private final ResourceCalculator resourceCalculator;
    private final Resource minAlloc;
    
    public RLESparseResourceAllocation(final ResourceCalculator resourceCalculator, final Resource minAlloc) {
        this.cumulativeCapacity = new TreeMap<Long, Resource>();
        this.readWriteLock = new ReentrantReadWriteLock();
        this.readLock = this.readWriteLock.readLock();
        this.writeLock = this.readWriteLock.writeLock();
        this.resourceCalculator = resourceCalculator;
        this.minAlloc = minAlloc;
    }
    
    private boolean isSameAsPrevious(final Long key, final Resource capacity) {
        final Map.Entry<Long, Resource> previous = this.cumulativeCapacity.lowerEntry(key);
        return previous != null && previous.getValue().equals(capacity);
    }
    
    private boolean isSameAsNext(final Long key, final Resource capacity) {
        final Map.Entry<Long, Resource> next = this.cumulativeCapacity.higherEntry(key);
        return next != null && next.getValue().equals(capacity);
    }
    
    public boolean addInterval(final ReservationInterval reservationInterval, final ReservationRequest capacity) {
        final Resource totCap = Resources.multiply(capacity.getCapability(), (float)capacity.getNumContainers());
        if (totCap.equals(RLESparseResourceAllocation.ZERO_RESOURCE)) {
            return true;
        }
        this.writeLock.lock();
        try {
            final long startKey = reservationInterval.getStartTime();
            final long endKey = reservationInterval.getEndTime();
            final NavigableMap<Long, Resource> ticks = this.cumulativeCapacity.headMap(endKey, false);
            if (ticks != null && !ticks.isEmpty()) {
                Resource updatedCapacity = Resource.newInstance(0, 0);
                final Map.Entry<Long, Resource> lowEntry = ticks.floorEntry(startKey);
                if (lowEntry == null) {
                    this.cumulativeCapacity.put(startKey, totCap);
                }
                else {
                    updatedCapacity = Resources.add(lowEntry.getValue(), totCap);
                    if (startKey == lowEntry.getKey() && this.isSameAsPrevious(lowEntry.getKey(), updatedCapacity)) {
                        this.cumulativeCapacity.remove(lowEntry.getKey());
                    }
                    else {
                        this.cumulativeCapacity.put(startKey, updatedCapacity);
                    }
                }
                final Set<Map.Entry<Long, Resource>> overlapSet = ticks.tailMap(startKey, false).entrySet();
                for (final Map.Entry<Long, Resource> entry : overlapSet) {
                    updatedCapacity = Resources.add(entry.getValue(), totCap);
                    entry.setValue(updatedCapacity);
                }
            }
            else {
                this.cumulativeCapacity.put(startKey, totCap);
            }
            final Resource nextTick = this.cumulativeCapacity.get(endKey);
            if (nextTick != null) {
                if (this.isSameAsPrevious(endKey, nextTick)) {
                    this.cumulativeCapacity.remove(endKey);
                }
            }
            else {
                this.cumulativeCapacity.put(endKey, Resources.subtract(this.cumulativeCapacity.floorEntry(endKey).getValue(), totCap));
            }
            return true;
        }
        finally {
            this.writeLock.unlock();
        }
    }
    
    public boolean addCompositeInterval(final ReservationInterval reservationInterval, final List<ReservationRequest> ReservationRequests, final Resource clusterResource) {
        final ReservationRequest aggregateReservationRequest = Records.newRecord(ReservationRequest.class);
        final Resource capacity = Resource.newInstance(0, 0);
        for (final ReservationRequest ReservationRequest : ReservationRequests) {
            Resources.addTo(capacity, Resources.multiply(ReservationRequest.getCapability(), ReservationRequest.getNumContainers()));
        }
        aggregateReservationRequest.setNumContainers((int)Math.ceil(Resources.divide(this.resourceCalculator, clusterResource, capacity, this.minAlloc)));
        aggregateReservationRequest.setCapability(this.minAlloc);
        return this.addInterval(reservationInterval, aggregateReservationRequest);
    }
    
    public boolean removeInterval(final ReservationInterval reservationInterval, final ReservationRequest capacity) {
        final Resource totCap = Resources.multiply(capacity.getCapability(), (float)capacity.getNumContainers());
        if (totCap.equals(RLESparseResourceAllocation.ZERO_RESOURCE)) {
            return true;
        }
        this.writeLock.lock();
        try {
            final long startKey = reservationInterval.getStartTime();
            final long endKey = reservationInterval.getEndTime();
            final NavigableMap<Long, Resource> ticks = this.cumulativeCapacity.headMap(endKey, false);
            final SortedMap<Long, Resource> overlapSet = ticks.tailMap(startKey);
            if (overlapSet != null && !overlapSet.isEmpty()) {
                Resource updatedCapacity = Resource.newInstance(0, 0);
                long currentKey = -1L;
                for (final Map.Entry<Long, Resource> entry : overlapSet.entrySet()) {
                    currentKey = entry.getKey();
                    updatedCapacity = Resources.subtract(entry.getValue(), totCap);
                    this.cumulativeCapacity.put(currentKey, updatedCapacity);
                }
                final Long firstKey = overlapSet.firstKey();
                if (this.isSameAsPrevious(firstKey, overlapSet.get(firstKey))) {
                    this.cumulativeCapacity.remove(firstKey);
                }
                if (currentKey != -1L && this.isSameAsNext(currentKey, updatedCapacity)) {
                    this.cumulativeCapacity.remove(this.cumulativeCapacity.higherKey(currentKey));
                }
            }
            return true;
        }
        finally {
            this.writeLock.unlock();
        }
    }
    
    public Resource getCapacityAtTime(final long tick) {
        this.readLock.lock();
        try {
            final Map.Entry<Long, Resource> closestStep = this.cumulativeCapacity.floorEntry(tick);
            if (closestStep != null) {
                return Resources.clone(closestStep.getValue());
            }
            return Resources.clone(RLESparseResourceAllocation.ZERO_RESOURCE);
        }
        finally {
            this.readLock.unlock();
        }
    }
    
    public long getEarliestStartTime() {
        this.readLock.lock();
        try {
            if (this.cumulativeCapacity.isEmpty()) {
                return -1L;
            }
            return this.cumulativeCapacity.firstKey();
        }
        finally {
            this.readLock.unlock();
        }
    }
    
    public long getLatestEndTime() {
        this.readLock.lock();
        try {
            if (this.cumulativeCapacity.isEmpty()) {
                return -1L;
            }
            return this.cumulativeCapacity.lastKey();
        }
        finally {
            this.readLock.unlock();
        }
    }
    
    public boolean isEmpty() {
        this.readLock.lock();
        try {
            return this.cumulativeCapacity.isEmpty() || (this.cumulativeCapacity.size() == 1 && this.cumulativeCapacity.firstEntry().getValue().equals(RLESparseResourceAllocation.ZERO_RESOURCE));
        }
        finally {
            this.readLock.unlock();
        }
    }
    
    @Override
    public String toString() {
        final StringBuilder ret = new StringBuilder();
        this.readLock.lock();
        try {
            if (this.cumulativeCapacity.size() > 100) {
                ret.append("Number of steps: ").append(this.cumulativeCapacity.size()).append(" earliest entry: ").append(this.cumulativeCapacity.firstKey()).append(" latest entry: ").append(this.cumulativeCapacity.lastKey());
            }
            else {
                for (final Map.Entry<Long, Resource> r : this.cumulativeCapacity.entrySet()) {
                    ret.append(r.getKey()).append(": ").append(r.getValue()).append("\n ");
                }
            }
            return ret.toString();
        }
        finally {
            this.readLock.unlock();
        }
    }
    
    public String toMemJSONString() {
        final StringWriter json = new StringWriter();
        final JsonWriter jsonWriter = new JsonWriter(json);
        this.readLock.lock();
        try {
            jsonWriter.beginObject();
            for (final Map.Entry<Long, Resource> r : this.cumulativeCapacity.entrySet()) {
                jsonWriter.name(r.getKey().toString()).value(r.getValue().toString());
            }
            jsonWriter.endObject();
            jsonWriter.close();
            return json.toString();
        }
        catch (IOException e) {
            return "";
        }
        finally {
            this.readLock.unlock();
        }
    }
    
    static {
        ZERO_RESOURCE = Resource.newInstance(0, 0);
    }
}
