// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.curator.framework.imps;

import java.io.InputStream;
import java.util.zip.GZIPInputStream;
import java.io.ByteArrayInputStream;
import java.io.OutputStream;
import java.util.zip.GZIPOutputStream;
import java.io.ByteArrayOutputStream;
import org.apache.curator.framework.api.CompressionProvider;

public class GzipCompressionProvider implements CompressionProvider
{
    @Override
    public byte[] compress(final String path, final byte[] data) throws Exception {
        final ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        final GZIPOutputStream out = new GZIPOutputStream(bytes);
        try {
            out.write(data);
            out.finish();
        }
        finally {
            out.close();
        }
        return bytes.toByteArray();
    }
    
    @Override
    public byte[] decompress(final String path, final byte[] compressedData) throws Exception {
        final ByteArrayOutputStream bytes = new ByteArrayOutputStream(compressedData.length);
        final GZIPInputStream in = new GZIPInputStream(new ByteArrayInputStream(compressedData));
        try {
            final byte[] buffer = new byte[compressedData.length];
            while (true) {
                final int bytesRead = in.read(buffer, 0, buffer.length);
                if (bytesRead < 0) {
                    break;
                }
                bytes.write(buffer, 0, bytesRead);
            }
        }
        finally {
            in.close();
        }
        return bytes.toByteArray();
    }
}
