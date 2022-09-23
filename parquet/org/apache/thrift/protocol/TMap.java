// 
// Decompiled by Procyon v0.5.36
// 

package parquet.org.apache.thrift.protocol;

public final class TMap
{
    public final byte keyType;
    public final byte valueType;
    public final int size;
    
    public TMap() {
        this((byte)0, (byte)0, 0);
    }
    
    public TMap(final byte k, final byte v, final int s) {
        this.keyType = k;
        this.valueType = v;
        this.size = s;
    }
}
