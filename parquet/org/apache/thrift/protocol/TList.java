// 
// Decompiled by Procyon v0.5.36
// 

package parquet.org.apache.thrift.protocol;

public final class TList
{
    public final byte elemType;
    public final int size;
    
    public TList() {
        this((byte)0, 0);
    }
    
    public TList(final byte t, final int s) {
        this.elemType = t;
        this.size = s;
    }
}
