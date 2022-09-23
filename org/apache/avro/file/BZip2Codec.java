// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.avro.file;

import java.io.InputStream;
import org.apache.commons.compress.compressors.bzip2.BZip2CompressorInputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.OutputStream;
import org.apache.commons.compress.compressors.bzip2.BZip2CompressorOutputStream;
import java.nio.ByteBuffer;
import java.io.ByteArrayOutputStream;

public class BZip2Codec extends Codec
{
    public static final int DEFAULT_BUFFER_SIZE = 65536;
    private ByteArrayOutputStream outputBuffer;
    
    @Override
    public String getName() {
        return "bzip2";
    }
    
    @Override
    public ByteBuffer compress(final ByteBuffer uncompressedData) throws IOException {
        final ByteArrayOutputStream baos = this.getOutputBuffer(uncompressedData.remaining());
        final BZip2CompressorOutputStream outputStream = new BZip2CompressorOutputStream(baos);
        try {
            outputStream.write(uncompressedData.array(), uncompressedData.position(), uncompressedData.remaining());
        }
        finally {
            outputStream.close();
        }
        final ByteBuffer result = ByteBuffer.wrap(baos.toByteArray());
        return result;
    }
    
    @Override
    public ByteBuffer decompress(final ByteBuffer compressedData) throws IOException {
        final ByteArrayInputStream bais = new ByteArrayInputStream(compressedData.array());
        final BZip2CompressorInputStream inputStream = new BZip2CompressorInputStream(bais);
        try {
            final ByteArrayOutputStream baos = new ByteArrayOutputStream();
            final byte[] buffer = new byte[65536];
            int readCount = -1;
            while ((readCount = inputStream.read(buffer, compressedData.position(), buffer.length)) > 0) {
                baos.write(buffer, 0, readCount);
            }
            final ByteBuffer result = ByteBuffer.wrap(baos.toByteArray());
            return result;
        }
        finally {
            inputStream.close();
        }
    }
    
    @Override
    public int hashCode() {
        return this.getName().hashCode();
    }
    
    @Override
    public boolean equals(final Object obj) {
        return this == obj || this.getClass() == obj.getClass();
    }
    
    private ByteArrayOutputStream getOutputBuffer(final int suggestedLength) {
        if (null == this.outputBuffer) {
            this.outputBuffer = new ByteArrayOutputStream(suggestedLength);
        }
        this.outputBuffer.reset();
        return this.outputBuffer;
    }
    
    static class Option extends CodecFactory
    {
        @Override
        protected Codec createInstance() {
            return new BZip2Codec();
        }
    }
}
