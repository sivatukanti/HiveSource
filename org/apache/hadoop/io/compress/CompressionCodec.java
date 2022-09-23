// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.io.compress;

import org.apache.hadoop.conf.Configuration;
import java.io.InputStream;
import java.io.IOException;
import java.io.OutputStream;
import org.apache.hadoop.classification.InterfaceStability;
import org.apache.hadoop.classification.InterfaceAudience;

@InterfaceAudience.Public
@InterfaceStability.Evolving
public interface CompressionCodec
{
    CompressionOutputStream createOutputStream(final OutputStream p0) throws IOException;
    
    CompressionOutputStream createOutputStream(final OutputStream p0, final Compressor p1) throws IOException;
    
    Class<? extends Compressor> getCompressorType();
    
    Compressor createCompressor();
    
    CompressionInputStream createInputStream(final InputStream p0) throws IOException;
    
    CompressionInputStream createInputStream(final InputStream p0, final Decompressor p1) throws IOException;
    
    Class<? extends Decompressor> getDecompressorType();
    
    Decompressor createDecompressor();
    
    String getDefaultExtension();
    
    public static class Util
    {
        static CompressionOutputStream createOutputStreamWithCodecPool(final CompressionCodec codec, final Configuration conf, final OutputStream out) throws IOException {
            final Compressor compressor = CodecPool.getCompressor(codec, conf);
            CompressionOutputStream stream = null;
            try {
                stream = codec.createOutputStream(out, compressor);
            }
            finally {
                if (stream == null) {
                    CodecPool.returnCompressor(compressor);
                }
                else {
                    stream.setTrackedCompressor(compressor);
                }
            }
            return stream;
        }
        
        static CompressionInputStream createInputStreamWithCodecPool(final CompressionCodec codec, final Configuration conf, final InputStream in) throws IOException {
            final Decompressor decompressor = CodecPool.getDecompressor(codec);
            CompressionInputStream stream = null;
            try {
                stream = codec.createInputStream(in, decompressor);
            }
            finally {
                if (stream == null) {
                    CodecPool.returnDecompressor(decompressor);
                }
                else {
                    stream.setTrackedDecompressor(decompressor);
                }
            }
            return stream;
        }
    }
}
