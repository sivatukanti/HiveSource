// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.avro.file;

import java.io.InputStream;
import org.apache.commons.compress.utils.IOUtils;
import org.apache.commons.compress.compressors.xz.XZCompressorInputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.OutputStream;
import org.apache.commons.compress.compressors.xz.XZCompressorOutputStream;
import java.nio.ByteBuffer;
import java.io.ByteArrayOutputStream;

public class XZCodec extends Codec
{
    private ByteArrayOutputStream outputBuffer;
    private int compressionLevel;
    
    public XZCodec(final int compressionLevel) {
        this.compressionLevel = compressionLevel;
    }
    
    @Override
    public String getName() {
        return "xz";
    }
    
    @Override
    public ByteBuffer compress(final ByteBuffer data) throws IOException {
        final ByteArrayOutputStream baos = this.getOutputBuffer(data.remaining());
        final OutputStream ios = new XZCompressorOutputStream(baos, this.compressionLevel);
        this.writeAndClose(data, ios);
        return ByteBuffer.wrap(baos.toByteArray());
    }
    
    @Override
    public ByteBuffer decompress(final ByteBuffer data) throws IOException {
        final ByteArrayOutputStream baos = this.getOutputBuffer(data.remaining());
        final InputStream bytesIn = new ByteArrayInputStream(data.array(), data.arrayOffset() + data.position(), data.remaining());
        final InputStream ios = new XZCompressorInputStream(bytesIn);
        try {
            IOUtils.copy(ios, baos);
        }
        finally {
            ios.close();
        }
        return ByteBuffer.wrap(baos.toByteArray());
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
    
    private ByteArrayOutputStream getOutputBuffer(final int suggestedLength) {
        if (null == this.outputBuffer) {
            this.outputBuffer = new ByteArrayOutputStream(suggestedLength);
        }
        this.outputBuffer.reset();
        return this.outputBuffer;
    }
    
    @Override
    public int hashCode() {
        return this.compressionLevel;
    }
    
    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (this.getClass() != obj.getClass()) {
            return false;
        }
        final XZCodec other = (XZCodec)obj;
        return this.compressionLevel == other.compressionLevel;
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
            return new XZCodec(this.compressionLevel);
        }
    }
}
