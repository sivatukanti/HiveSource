// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.avro.file;

import java.io.IOException;
import org.xerial.snappy.Snappy;
import java.nio.ByteBuffer;
import java.util.zip.CRC32;

class SnappyCodec extends Codec
{
    private CRC32 crc32;
    
    private SnappyCodec() {
        this.crc32 = new CRC32();
    }
    
    @Override
    public String getName() {
        return "snappy";
    }
    
    @Override
    public ByteBuffer compress(final ByteBuffer in) throws IOException {
        final ByteBuffer out = ByteBuffer.allocate(Snappy.maxCompressedLength(in.remaining()) + 4);
        final int size = Snappy.compress(in.array(), in.position(), in.remaining(), out.array(), 0);
        this.crc32.reset();
        this.crc32.update(in.array(), in.position(), in.remaining());
        out.putInt(size, (int)this.crc32.getValue());
        out.limit(size + 4);
        return out;
    }
    
    @Override
    public ByteBuffer decompress(final ByteBuffer in) throws IOException {
        final ByteBuffer out = ByteBuffer.allocate(Snappy.uncompressedLength(in.array(), in.position(), in.remaining() - 4));
        final int size = Snappy.uncompress(in.array(), in.position(), in.remaining() - 4, out.array(), 0);
        out.limit(size);
        this.crc32.reset();
        this.crc32.update(out.array(), 0, size);
        if (in.getInt(in.limit() - 4) != (int)this.crc32.getValue()) {
            throw new IOException("Checksum failure");
        }
        return out;
    }
    
    @Override
    public int hashCode() {
        return this.getName().hashCode();
    }
    
    @Override
    public boolean equals(final Object obj) {
        return this == obj || this.getClass() == obj.getClass();
    }
    
    static class Option extends CodecFactory
    {
        @Override
        protected Codec createInstance() {
            return new SnappyCodec(null);
        }
    }
}
