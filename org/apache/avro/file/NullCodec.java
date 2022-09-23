// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.avro.file;

import java.io.IOException;
import java.nio.ByteBuffer;

final class NullCodec extends Codec
{
    private static final NullCodec INSTANCE;
    public static final CodecFactory OPTION;
    
    @Override
    public String getName() {
        return "null";
    }
    
    @Override
    public ByteBuffer compress(final ByteBuffer buffer) throws IOException {
        return buffer;
    }
    
    @Override
    public ByteBuffer decompress(final ByteBuffer data) throws IOException {
        return data;
    }
    
    @Override
    public boolean equals(final Object other) {
        return this == other || this.getClass() == other.getClass();
    }
    
    @Override
    public int hashCode() {
        return 2;
    }
    
    static {
        INSTANCE = new NullCodec();
        OPTION = new Option();
    }
    
    static class Option extends CodecFactory
    {
        @Override
        protected Codec createInstance() {
            return NullCodec.INSTANCE;
        }
    }
}
