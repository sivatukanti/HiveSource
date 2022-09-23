// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.avro.file;

import java.util.zip.InflaterOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.zip.DeflaterOutputStream;
import java.nio.ByteBuffer;
import java.util.zip.Inflater;
import java.util.zip.Deflater;
import java.io.ByteArrayOutputStream;

class DeflateCodec extends Codec
{
    private ByteArrayOutputStream outputBuffer;
    private Deflater deflater;
    private Inflater inflater;
    private boolean nowrap;
    private int compressionLevel;
    
    public DeflateCodec(final int compressionLevel) {
        this.nowrap = true;
        this.compressionLevel = compressionLevel;
    }
    
    @Override
    public String getName() {
        return "deflate";
    }
    
    @Override
    public ByteBuffer compress(final ByteBuffer data) throws IOException {
        final ByteArrayOutputStream baos = this.getOutputBuffer(data.remaining());
        final DeflaterOutputStream ios = new DeflaterOutputStream(baos, this.getDeflater());
        this.writeAndClose(data, ios);
        final ByteBuffer result = ByteBuffer.wrap(baos.toByteArray());
        return result;
    }
    
    @Override
    public ByteBuffer decompress(final ByteBuffer data) throws IOException {
        final ByteArrayOutputStream baos = this.getOutputBuffer(data.remaining());
        final InflaterOutputStream ios = new InflaterOutputStream(baos, this.getInflater());
        this.writeAndClose(data, ios);
        final ByteBuffer result = ByteBuffer.wrap(baos.toByteArray());
        return result;
    }
    
    private void writeAndClose(final ByteBuffer data, final OutputStream to) throws IOException {
        final byte[] input = data.array();
        final int offset = data.arrayOffset() + data.position();
        final int length = data.remaining();
        try {
            to.write(input, offset, length);
        }
        finally {
            to.close();
        }
    }
    
    private Inflater getInflater() {
        if (null == this.inflater) {
            this.inflater = new Inflater(this.nowrap);
        }
        this.inflater.reset();
        return this.inflater;
    }
    
    private Deflater getDeflater() {
        if (null == this.deflater) {
            this.deflater = new Deflater(this.compressionLevel, this.nowrap);
        }
        this.deflater.reset();
        return this.deflater;
    }
    
    private ByteArrayOutputStream getOutputBuffer(final int suggestedLength) {
        if (null == this.outputBuffer) {
            this.outputBuffer = new ByteArrayOutputStream(suggestedLength);
        }
        this.outputBuffer.reset();
        return this.outputBuffer;
    }
    
    @Override
    public int hashCode() {
        return this.nowrap ? 0 : 1;
    }
    
    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (this.getClass() != obj.getClass()) {
            return false;
        }
        final DeflateCodec other = (DeflateCodec)obj;
        return this.nowrap == other.nowrap;
    }
    
    @Override
    public String toString() {
        return this.getName() + "-" + this.compressionLevel;
    }
    
    static class Option extends CodecFactory
    {
        private int compressionLevel;
        
        Option(final int compressionLevel) {
            this.compressionLevel = compressionLevel;
        }
        
        @Override
        protected Codec createInstance() {
            return new DeflateCodec(this.compressionLevel);
        }
    }
}
