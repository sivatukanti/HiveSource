// 
// Decompiled by Procyon v0.5.36
// 

package parquet.it.unimi.dsi.fastutil.bytes;

import java.util.Iterator;

public interface ByteIterator extends Iterator<Byte>
{
    byte nextByte();
    
    int skip(final int p0);
}
