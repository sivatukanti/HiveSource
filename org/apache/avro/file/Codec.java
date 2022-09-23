// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.avro.file;

import java.io.IOException;
import java.nio.ByteBuffer;

public abstract class Codec
{
    public abstract String getName();
    
    public abstract ByteBuffer compress(final ByteBuffer p0) throws IOException;
    
    public abstract ByteBuffer decompress(final ByteBuffer p0) throws IOException;
    
    @Override
    public abstract boolean equals(final Object p0);
    
    @Override
    public abstract int hashCode();
    
    @Override
    public String toString() {
        return this.getName();
    }
}
