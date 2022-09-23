// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.commons.compress.compressors.gzip;

import java.io.IOException;
import java.io.OutputStream;
import java.util.zip.GZIPOutputStream;
import org.apache.commons.compress.compressors.CompressorOutputStream;

public class GzipCompressorOutputStream extends CompressorOutputStream
{
    private final GZIPOutputStream out;
    
    public GzipCompressorOutputStream(final OutputStream outputStream) throws IOException {
        this.out = new GZIPOutputStream(outputStream);
    }
    
    @Override
    public void write(final int b) throws IOException {
        this.out.write(b);
    }
    
    @Override
    public void write(final byte[] b) throws IOException {
        this.out.write(b);
    }
    
    @Override
    public void write(final byte[] b, final int from, final int length) throws IOException {
        this.out.write(b, from, length);
    }
    
    @Override
    public void close() throws IOException {
        this.out.close();
    }
}
