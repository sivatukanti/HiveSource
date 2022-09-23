// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.yarn.api.records;

import java.io.Serializable;
import java.util.Comparator;
import org.apache.hadoop.yarn.util.Records;
import org.apache.hadoop.classification.InterfaceStability;
import org.apache.hadoop.classification.InterfaceAudience;

@InterfaceAudience.Public
@InterfaceStability.Unstable
public abstract class ReservationRequest implements Comparable<ReservationRequest>
{
    @InterfaceAudience.Public
    @InterfaceStability.Unstable
    public static ReservationRequest newInstance(final Resource capability, final int numContainers) {
        return newInstance(capability, numContainers, 1, -1L);
    }
    
    @InterfaceAudience.Public
    @InterfaceStability.Unstable
    public static ReservationRequest newInstance(final Resource capability, final int numContainers, final int concurrency, final long duration) {
        final ReservationRequest request = Records.newRecord(ReservationRequest.class);
        request.setCapability(capability);
        request.setNumContainers(numContainers);
        request.setConcurrency(concurrency);
        request.setDuration(duration);
        return request;
    }
    
    @InterfaceAudience.Public
    @InterfaceStability.Unstable
    public abstract Resource getCapability();
    
    @InterfaceAudience.Public
    @InterfaceStability.Unstable
    public abstract void setCapability(final Resource p0);
    
    @InterfaceAudience.Public
    @InterfaceStability.Unstable
    public abstract int getNumContainers();
    
    @InterfaceAudience.Public
    @InterfaceStability.Unstable
    public abstract void setNumContainers(final int p0);
    
    @InterfaceAudience.Public
    @InterfaceStability.Unstable
    public abstract int getConcurrency();
    
    @InterfaceAudience.Public
    @InterfaceStability.Unstable
    public abstract void setConcurrency(final int p0);
    
    @InterfaceAudience.Public
    @InterfaceStability.Unstable
    public abstract long getDuration();
    
    @InterfaceAudience.Public
    @InterfaceStability.Unstable
    public abstract void setDuration(final long p0);
    
    @Override
    public int hashCode() {
        final int prime = 2153;
        int result = 2459;
        final Resource capability = this.getCapability();
        result = 2153 * result + ((capability == null) ? 0 : capability.hashCode());
        result = 2153 * result + this.getNumContainers();
        result = 2153 * result + this.getConcurrency();
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
        final ReservationRequest other = (ReservationRequest)obj;
        final Resource capability = this.getCapability();
        if (capability == null) {
            if (other.getCapability() != null) {
                return false;
            }
        }
        else if (!capability.equals(other.getCapability())) {
            return false;
        }
        return this.getNumContainers() == other.getNumContainers() && this.getConcurrency() == other.getConcurrency();
    }
    
    @Override
    public int compareTo(final ReservationRequest other) {
        final int numContainersComparison = this.getNumContainers() - other.getNumContainers();
        if (numContainersComparison != 0) {
            return numContainersComparison;
        }
        final int concurrencyComparison = this.getConcurrency() - other.getConcurrency();
        if (concurrencyComparison == 0) {
            return this.getCapability().compareTo(other.getCapability());
        }
        return concurrencyComparison;
    }
    
    @InterfaceAudience.Public
    @InterfaceStability.Unstable
    public static class ReservationRequestComparator implements Comparator<ReservationRequest>, Serializable
    {
        private static final long serialVersionUID = 1L;
        
        @Override
        public int compare(final ReservationRequest r1, final ReservationRequest r2) {
            int ret = r1.getNumContainers() - r2.getNumContainers();
            if (ret == 0) {
                ret = r1.getConcurrency() - r2.getConcurrency();
            }
            if (ret == 0) {
                ret = r1.getCapability().compareTo(r2.getCapability());
            }
            return ret;
        }
    }
}
