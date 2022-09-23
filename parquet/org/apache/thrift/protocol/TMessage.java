// 
// Decompiled by Procyon v0.5.36
// 

package parquet.org.apache.thrift.protocol;

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
    public boolean equals(final Object other) {
        return other instanceof TMessage && this.equals((TMessage)other);
    }
    
    public boolean equals(final TMessage other) {
        return this.name.equals(other.name) && this.type == other.type && this.seqid == other.seqid;
    }
}
