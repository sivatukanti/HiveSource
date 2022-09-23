// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.thrift.protocol;

public final class TMessage
{
    public final String name;
    public final byte type;
    public final int seqid;
    
    public TMessage() {
        this("", (byte)0, 0);
    }
    
    public TMessage(final String n, final byte t, final int s) {
        this.name = n;
        this.type = t;
        this.seqid = s;
    }
    
    @Override
    public String toString() {
        return "<TMessage name:'" + this.name + "' type: " + this.type + " seqid:" + this.seqid + ">";
    }
    
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = 31 * result + ((this.name == null) ? 0 : this.name.hashCode());
        result = 31 * result + this.seqid;
        result = 31 * result + this.type;
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
        final TMessage other = (TMessage)obj;
        if (this.name == null) {
            if (other.name != null) {
                return false;
            }
        }
        else if (!this.name.equals(other.name)) {
            return false;
        }
        return this.seqid == other.seqid && this.type == other.type;
    }
}
