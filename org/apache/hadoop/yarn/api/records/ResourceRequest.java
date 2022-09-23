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
@InterfaceStability.Stable
public abstract class ResourceRequest implements Comparable<ResourceRequest>
{
    public static final String ANY = "*";
    
    @InterfaceAudience.Public
    @InterfaceStability.Stable
    public static ResourceRequest newInstance(final Priority priority, final String hostName, final Resource capability, final int numContainers) {
        return newInstance(priority, hostName, capability, numContainers, true);
    }
    
    @InterfaceAudience.Public
    @InterfaceStability.Stable
    public static ResourceRequest newInstance(final Priority priority, final String hostName, final Resource capability, final int numContainers, final boolean relaxLocality) {
        return newInstance(priority, hostName, capability, numContainers, relaxLocality, null);
    }
    
    @InterfaceAudience.Public
    @InterfaceStability.Stable
    public static ResourceRequest newInstance(final Priority priority, final String hostName, final Resource capability, final int numContainers, final boolean relaxLocality, final String labelExpression) {
        final ResourceRequest request = Records.newRecord(ResourceRequest.class);
        request.setPriority(priority);
        request.setResourceName(hostName);
        request.setCapability(capability);
        request.setNumContainers(numContainers);
        request.setRelaxLocality(relaxLocality);
        request.setNodeLabelExpression(labelExpression);
        return request;
    }
    
    @InterfaceAudience.Public
    @InterfaceStability.Stable
    public static boolean isAnyLocation(final String hostName) {
        return "*".equals(hostName);
    }
    
    @InterfaceAudience.Public
    @InterfaceStability.Stable
    public abstract Priority getPriority();
    
    @InterfaceAudience.Public
    @InterfaceStability.Stable
    public abstract void setPriority(final Priority p0);
    
    @InterfaceAudience.Public
    @InterfaceStability.Stable
    public abstract String getResourceName();
    
    @InterfaceAudience.Public
    @InterfaceStability.Stable
    public abstract void setResourceName(final String p0);
    
    @InterfaceAudience.Public
    @InterfaceStability.Stable
    public abstract Resource getCapability();
    
    @InterfaceAudience.Public
    @InterfaceStability.Stable
    public abstract void setCapability(final Resource p0);
    
    @InterfaceAudience.Public
    @InterfaceStability.Stable
    public abstract int getNumContainers();
    
    @InterfaceAudience.Public
    @InterfaceStability.Stable
    public abstract void setNumContainers(final int p0);
    
    @InterfaceAudience.Public
    @InterfaceStability.Stable
    public abstract boolean getRelaxLocality();
    
    @InterfaceAudience.Public
    @InterfaceStability.Stable
    public abstract void setRelaxLocality(final boolean p0);
    
    @InterfaceAudience.Public
    @InterfaceStability.Evolving
    public abstract String getNodeLabelExpression();
    
    @InterfaceAudience.Public
    @InterfaceStability.Evolving
    public abstract void setNodeLabelExpression(final String p0);
    
    @Override
    public int hashCode() {
        final int prime = 2153;
        int result = 2459;
        final Resource capability = this.getCapability();
        final String hostName = this.getResourceName();
        final Priority priority = this.getPriority();
        result = 2153 * result + ((capability == null) ? 0 : capability.hashCode());
        result = 2153 * result + ((hostName == null) ? 0 : hostName.hashCode());
        result = 2153 * result + this.getNumContainers();
        result = 2153 * result + ((priority == null) ? 0 : priority.hashCode());
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
        final ResourceRequest other = (ResourceRequest)obj;
        final Resource capability = this.getCapability();
        if (capability == null) {
            if (other.getCapability() != null) {
                return false;
            }
        }
        else if (!capability.equals(other.getCapability())) {
            return false;
        }
        final String hostName = this.getResourceName();
        if (hostName == null) {
            if (other.getResourceName() != null) {
                return false;
            }
        }
        else if (!hostName.equals(other.getResourceName())) {
            return false;
        }
        if (this.getNumContainers() != other.getNumContainers()) {
            return false;
        }
        final Priority priority = this.getPriority();
        if (priority == null) {
            if (other.getPriority() != null) {
                return false;
            }
        }
        else if (!priority.equals(other.getPriority())) {
            return false;
        }
        if (this.getNodeLabelExpression() == null) {
            if (other.getNodeLabelExpression() != null) {
                return false;
            }
        }
        else {
            final String label1 = this.getNodeLabelExpression().replaceAll("[\\t ]", "");
            final String label2 = (other.getNodeLabelExpression() == null) ? null : other.getNodeLabelExpression().replaceAll("[\\t ]", "");
            if (!label1.equals(label2)) {
                return false;
            }
        }
        return true;
    }
    
    @Override
    public int compareTo(final ResourceRequest other) {
        final int priorityComparison = this.getPriority().compareTo(other.getPriority());
        if (priorityComparison != 0) {
            return priorityComparison;
        }
        final int hostNameComparison = this.getResourceName().compareTo(other.getResourceName());
        if (hostNameComparison != 0) {
            return hostNameComparison;
        }
        final int capabilityComparison = this.getCapability().compareTo(other.getCapability());
        if (capabilityComparison == 0) {
            return this.getNumContainers() - other.getNumContainers();
        }
        return capabilityComparison;
    }
    
    @InterfaceAudience.Public
    @InterfaceStability.Stable
    public static class ResourceRequestComparator implements Comparator<ResourceRequest>, Serializable
    {
        private static final long serialVersionUID = 1L;
        
        @Override
        public int compare(final ResourceRequest r1, final ResourceRequest r2) {
            int ret = r1.getPriority().compareTo(r2.getPriority());
            if (ret == 0) {
                final String h1 = r1.getResourceName();
                final String h2 = r2.getResourceName();
                ret = h1.compareTo(h2);
            }
            if (ret == 0) {
                ret = r1.getCapability().compareTo(r2.getCapability());
            }
            return ret;
        }
    }
}
