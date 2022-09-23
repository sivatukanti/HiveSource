// 
// Decompiled by Procyon v0.5.36
// 

package parquet.hadoop;

import org.apache.hadoop.io.compress.CompressionOutputStream;
import java.io.OutputStream;
import java.io.ByteArrayOutputStream;
import org.apache.hadoop.io.compress.Compressor;
import java.io.IOException;
import java.io.InputStream;
import java.io.ByteArrayInputStream;
import parquet.bytes.BytesInput;
import org.apache.hadoop.io.compress.CodecPool;
import org.apache.hadoop.io.compress.Decompressor;
import java.util.Iterator;
import org.apache.hadoop.util.ReflectionUtils;
import java.util.HashMap;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.io.compress.CompressionCodec;
import parquet.hadoop.metadata.CompressionCodecName;
import java.util.Map;

class CodecFactory
{
    private final Map<CompressionCodecName, BytesCompressor> compressors;
    private final Map<CompressionCodecName, BytesDecompressor> decompressors;
    private final Map<String, CompressionCodec> codecByName;
    private final Configuration configuration;
    
    public CodecFactory(final Configuration configuration) {
        this.compressors = new HashMap<CompressionCodecName, BytesCompressor>();
        this.decompressors = new HashMap<CompressionCodecName, BytesDecompressor>();
        this.codecByName = new HashMap<String, CompressionCodec>();
        this.configuration = configuration;
    }
    
    private CompressionCodec getCodec(final CompressionCodecName codecName) {
        final String codecClassName = codecName.getHadoopCompressionCodecClassName();
        if (codecClassName == null) {
            return null;
        }
        CompressionCodec codec = this.codecByName.get(codecClassName);
        if (codec != null) {
            return codec;
        }
        try {
            final Class<?> codecClass = Class.forName(codecClassName);
            codec = ReflectionUtils.newInstance(codecClass, this.configuration);
            this.codecByName.put(codecClassName, codec);
            return codec;
        }
        catch (ClassNotFoundException e) {
            throw new BadConfigurationException("Class " + codecClassName + " was not found", e);
        }
    }
    
    public BytesCompressor getCompressor(final CompressionCodecName codecName, final int pageSize) {
        BytesCompressor comp = this.compressors.get(codecName);
        if (comp == null) {
            final CompressionCodec codec = this.getCodec(codecName);
            comp = new BytesCompressor(codecName, codec, pageSize);
            this.compressors.put(codecName, comp);
        }
        return comp;
    }
    
    public BytesDecompressor getDecompressor(final CompressionCodecName codecName) {
        BytesDecompressor decomp = this.decompressors.get(codecName);
        if (decomp == null) {
            final CompressionCodec codec = this.getCodec(codecName);
            decomp = new BytesDecompressor(codec);
            this.decompressors.put(codecName, decomp);
        }
        return decomp;
    }
    
    public void release() {
        for (final BytesCompressor compressor : this.compressors.values()) {
            compressor.release();
        }
        this.compressors.clear();
        for (final BytesDecompressor decompressor : this.decompressors.values()) {
            decompressor.release();
        }
        this.decompressors.clear();
    }
    
    public class BytesDecompressor
    {
        private final CompressionCodec codec;
        private final Decompressor decompressor;
        
        public BytesDecompressor(final CompressionCodec codec) {
            this.codec = codec;
            if (codec != null) {
                this.decompressor = CodecPool.getDecompressor(codec);
            }
            else {
                this.decompressor = null;
            }
        }
        
        public BytesInput decompress(final BytesInput bytes, final int uncompressedSize) throws IOException {
            BytesInput decompressed;
            if (this.codec != null) {
                this.decompressor.reset();
                final InputStream is = this.codec.createInputStream(new ByteArrayInputStream(bytes.toByteArray()), this.decompressor);
                decompressed = BytesInput.from(is, uncompressedSize);
            }
            else {
                decompressed = bytes;
            }
            return decompressed;
        }
        
        private void release() {
            if (this.decompressor != null) {
                CodecPool.returnDecompressor(this.decompressor);
            }
        }
    }
    
    public static class BytesCompressor
    {
        private final CompressionCodec codec;
        private final Compressor compressor;
        private final ByteArrayOutputStream compressedOutBuffer;
        private final CompressionCodecName codecName;
        
        public BytesCompressor(final CompressionCodecName codecName, final CompressionCodec codec, final int pageSize) {
            this.codecName = codecName;
            this.codec = codec;
            if (codec != null) {
                this.compressor = CodecPool.getCompressor(codec);
                this.compressedOutBuffer = new ByteArrayOutputStream(pageSize);
            }
            else {
                this.compressor = null;
                this.compressedOutBuffer = null;
            }
        }
        
        public BytesInput compress(final BytesInput bytes) throws IOException {
            BytesInput compressedBytes;
            if (this.codec == null) {
                compressedBytes = bytes;
            }
            else {
                this.compressedOutBuffer.reset();
                if (this.compressor != null) {
                    this.compressor.reset();
                }
                final CompressionOutputStream cos = this.codec.createOutputStream(this.compressedOutBuffer, this.compressor);
                bytes.writeAllTo(cos);
                cos.finish();
                cos.close();
                compressedBytes = BytesInput.from(this.compressedOutBuffer);
            }
            return compressedBytes;
        }
        
        private void release() {
            if (this.compressor != null) {
                CodecPool.returnCompressor(this.compressor);
            }
        }
        
        public CompressionCodecName getCodecName() {
            return this.codecName;
        }
    }
}
