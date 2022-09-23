// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.yarn.api.records;

import org.apache.hadoop.yarn.util.Records;
import org.apache.hadoop.classification.InterfaceStability;
import org.apache.hadoop.classification.InterfaceAudience;

@InterfaceAudience.Public
@InterfaceStability.Stable
public abstract class NMToken
{
    @InterfaceAudience.Private
    @InterfaceStability.Unstable
    public static NMToken newInstance(final NodeId nodeId, final Token token) {
        final NMToken nmToken = Records.newRecord(NMToken.class);
        nmToken.setNodeId(nodeId);
        nmToken.setToken(token);
        return nmToken;
    }
    
    @InterfaceAudience.Public
    @InterfaceStability.Stable
    public abstract NodeId getNodeId();
    
    @InterfaceAudience.Public
    @InterfaceStability.Stable
    public abstract void setNodeId(final NodeId p0);
    
    @InterfaceAudience.Public
    @InterfaceStability.Stable
    public abstract Token getToken();
    
    @InterfaceAudience.Public
    @InterfaceStability.Stable
    public abstract void setToken(final Token p0);
    
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = 31 * result + ((this.getNodeId() == null) ? 0 : this.getNodeId().hashCode());
        result = 31 * result + ((this.getToken() == null) ? 0 : this.getToken().hashCode());
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
        final NMToken other = (NMToken)obj;
        if (this.getNodeId() == null) {
            if (other.getNodeId() != null) {
                return false;
            }
        }
        else if (!this.getNodeId().equals(other.getNodeId())) {
            return false;
        }
        if (this.getToken() == null) {
            if (other.getToken() != null) {
                return false;
            }
        }
        else if (!this.getToken().equals(other.getToken())) {
            return false;
        }
        return true;
    }
}
