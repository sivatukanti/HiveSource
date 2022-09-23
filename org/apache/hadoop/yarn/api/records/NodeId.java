// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.yarn.api.records;

import org.apache.hadoop.yarn.util.Records;
import org.apache.hadoop.classification.InterfaceStability;
import org.apache.hadoop.classification.InterfaceAudience;

@InterfaceAudience.Public
@InterfaceStability.Stable
public abstract class NodeId implements Comparable<NodeId>
{
    @InterfaceAudience.Private
    @InterfaceStability.Unstable
    public static NodeId newInstance(final String host, final int port) {
        final NodeId nodeId = Records.newRecord(NodeId.class);
        nodeId.setHost(host);
        nodeId.setPort(port);
        nodeId.build();
        return nodeId;
    }
    
    @InterfaceAudience.Public
    @InterfaceStability.Stable
    public abstract String getHost();
    
    @InterfaceAudience.Private
    @InterfaceStability.Unstable
    protected abstract void setHost(final String p0);
    
    @InterfaceAudience.Public
    @InterfaceStability.Stable
    public abstract int getPort();
    
    @InterfaceAudience.Private
    @InterfaceStability.Unstable
    protected abstract void setPort(final int p0);
    
    @Override
    public String toString() {
        return this.getHost() + ":" + this.getPort();
    }
    
    @Override
    public int hashCode() {
        final int prime = 493217;
        int result = 8501;
        result = 493217 * result + this.getHost().hashCode();
        result = 493217 * result + this.getPort();
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
        final NodeId other = (NodeId)obj;
        return this.getHost().equals(other.getHost()) && this.getPort() == other.getPort();
    }
    
    @Override
    public int compareTo(final NodeId other) {
        final int hostCompare = this.getHost().compareTo(other.getHost());
        if (hostCompare != 0) {
            return hostCompare;
        }
        if (this.getPort() > other.getPort()) {
            return 1;
        }
        if (this.getPort() < other.getPort()) {
            return -1;
        }
        return 0;
    }
    
    protected abstract void build();
}
