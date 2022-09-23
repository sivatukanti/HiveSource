// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.io.file.tfile;

import org.apache.hadoop.io.compress.DefaultCodec;
import java.io.BufferedOutputStream;
import org.apache.hadoop.io.compress.CompressionInputStream;
import java.io.BufferedInputStream;
import org.apache.hadoop.util.ReflectionUtils;
import org.apache.hadoop.io.compress.CodecPool;
import org.apache.hadoop.io.compress.Compressor;
import org.apache.hadoop.io.compress.Decompressor;
import java.io.InputStream;
import org.apache.hadoop.io.compress.CompressionCodec;
import org.apache.hadoop.conf.Configuration;
import java.io.IOException;
import java.io.OutputStream;
import org.apache.hadoop.io.compress.CompressionOutputStream;
import java.io.FilterOutputStream;
import org.slf4j.LoggerFactory;
import java.util.ArrayList;
import org.slf4j.Logger;

public final class Compression
{
    static final Logger LOG;
    
    private Compression() {
    }
    
    public static Algorithm getCompressionAlgorithmByName(final String compressName) {
        final Algorithm[] array;
        final Algorithm[] algos = array = Algorithm.class.getEnumConstants();
        for (final Algorithm a : array) {
            if (a.getName().equals(compressName)) {
                return a;
            }
        }
        throw new IllegalArgumentException("Unsupported compression algorithm name: " + compressName);
    }
    
    static String[] getSupportedAlgorithms() {
        final Algorithm[] algos = Algorithm.class.getEnumConstants();
        final ArrayList<String> ret = new ArrayList<String>();
        for (final Algorithm a : algos) {
            if (a.isSupported()) {
                ret.add(a.getName());
            }
        }
        return ret.toArray(new String[ret.size()]);
    }
    
    static {
        LOG = LoggerFactory.getLogger(Compression.class);
    }
    
    static class FinishOnFlushCompressionStream extends FilterOutputStream
    {
        public FinishOnFlushCompressionStream(final CompressionOutputStream cout) {
            super(cout);
        }
        
        @Override
        public void write(final byte[] b, final int off, final int len) throws IOException {
            this.out.write(b, off, len);
        }
        
        @Override
        public void flush() throws IOException {
            final CompressionOutputStream cout = (CompressionOutputStream)this.out;
            cout.finish();
            cout.flush();
            cout.resetState();
        }
    }
    
    public enum Algorithm
    {
        LZO("lzo") {
            private transient boolean checked;
            private transient ClassNotFoundException cnf;
            private transient boolean reinitCodecInTests;
            private static final String defaultClazz = "org.apache.hadoop.io.compress.LzoCodec";
            private transient String clazz;
            private transient CompressionCodec codec;
            
            {
                this.checked = false;
                this.codec = null;
            }
            
            private String getLzoCodecClass() {
                final String extClazzConf = Compression$Algorithm$1.conf.get("io.compression.codec.lzo.class");
                final String extClazz = (extClazzConf != null) ? extClazzConf : System.getProperty("io.compression.codec.lzo.class");
                return (extClazz != null) ? extClazz : "org.apache.hadoop.io.compress.LzoCodec";
            }
            
            @Override
            public synchronized boolean isSupported() {
                if (!this.checked || this.reinitCodecInTests) {
                    this.checked = true;
                    this.reinitCodecInTests = Compression$Algorithm$1.conf.getBoolean("test.reload.lzo.codec", false);
                    this.clazz = this.getLzoCodecClass();
                    try {
                        Compression.LOG.info("Trying to load Lzo codec class: " + this.clazz);
                        this.codec = ReflectionUtils.newInstance(Class.forName(this.clazz), Compression$Algorithm$1.conf);
                    }
                    catch (ClassNotFoundException e) {
                        this.cnf = e;
                    }
                }
                return this.codec != null;
            }
            
            @Override
            CompressionCodec getCodec() throws IOException {
                if (!this.isSupported()) {
                    throw new IOException(String.format("LZO codec %s=%s could not be loaded", "io.compression.codec.lzo.class", this.clazz), this.cnf);
                }
                return this.codec;
            }
            
            @Override
            public synchronized InputStream createDecompressionStream(final InputStream downStream, final Decompressor decompressor, final int downStreamBufferSize) throws IOException {
                if (!this.isSupported()) {
                    throw new IOException("LZO codec class not specified. Did you forget to set property io.compression.codec.lzo.class?");
                }
                InputStream bis1 = null;
                if (downStreamBufferSize > 0) {
                    bis1 = new BufferedInputStream(downStream, downStreamBufferSize);
                }
                else {
                    bis1 = downStream;
                }
                Compression$Algorithm$1.conf.setInt("io.compression.codec.lzo.buffersize", 65536);
                final CompressionInputStream cis = this.codec.createInputStream(bis1, decompressor);
                final BufferedInputStream bis2 = new BufferedInputStream(cis, 1024);
                return bis2;
            }
            
            @Override
            public synchronized OutputStream createCompressionStream(final OutputStream downStream, final Compressor compressor, final int downStreamBufferSize) throws IOException {
                if (!this.isSupported()) {
                    throw new IOException("LZO codec class not specified. Did you forget to set property io.compression.codec.lzo.class?");
                }
                OutputStream bos1 = null;
                if (downStreamBufferSize > 0) {
                    bos1 = new BufferedOutputStream(downStream, downStreamBufferSize);
                }
                else {
                    bos1 = downStream;
                }
                Compression$Algorithm$1.conf.setInt("io.compression.codec.lzo.buffersize", 65536);
                final CompressionOutputStream cos = this.codec.createOutputStream(bos1, compressor);
                final BufferedOutputStream bos2 = new BufferedOutputStream(new FinishOnFlushCompressionStream(cos), 4096);
                return bos2;
            }
        }, 
        GZ("gz") {
            private transient DefaultCodec codec;
            
            @Override
            CompressionCodec getCodec() {
                if (this.codec == null) {
                    (this.codec = new DefaultCodec()).setConf(Compression$Algorithm$2.conf);
                }
                return this.codec;
            }
            
            @Override
            public synchronized InputStream createDecompressionStream(final InputStream downStream, final Decompressor decompressor, final int downStreamBufferSize) throws IOException {
                if (downStreamBufferSize > 0) {
                    this.codec.getConf().setInt("io.file.buffer.size", downStreamBufferSize);
                }
                final CompressionInputStream cis = this.codec.createInputStream(downStream, decompressor);
                final BufferedInputStream bis2 = new BufferedInputStream(cis, 1024);
                return bis2;
            }
            
            @Override
            public synchronized OutputStream createCompressionStream(final OutputStream downStream, final Compressor compressor, final int downStreamBufferSize) throws IOException {
                OutputStream bos1 = null;
                if (downStreamBufferSize > 0) {
                    bos1 = new BufferedOutputStream(downStream, downStreamBufferSize);
                }
                else {
                    bos1 = downStream;
                }
                this.codec.getConf().setInt("io.file.buffer.size", 32768);
                final CompressionOutputStream cos = this.codec.createOutputStream(bos1, compressor);
                final BufferedOutputStream bos2 = new BufferedOutputStream(new FinishOnFlushCompressionStream(cos), 4096);
                return bos2;
            }
            
            @Override
            public boolean isSupported() {
                return true;
            }
        }, 
        NONE("none") {
            @Override
            CompressionCodec getCodec() {
                return null;
            }
            
            @Override
            public synchronized InputStream createDecompressionStream(final InputStream downStream, final Decompressor decompressor, final int downStreamBufferSize) throws IOException {
                if (downStreamBufferSize > 0) {
                    return new BufferedInputStream(downStream, downStreamBufferSize);
                }
                return downStream;
            }
            
            @Override
            public synchronized OutputStream createCompressionStream(final OutputStream downStream, final Compressor compressor, final int downStreamBufferSize) throws IOException {
                if (downStreamBufferSize > 0) {
                    return new BufferedOutputStream(downStream, downStreamBufferSize);
                }
                return downStream;
            }
            
            @Override
            public boolean isSupported() {
                return true;
            }
        };
        
        protected static final Configuration conf;
        private final String compressName;
        private static final int DATA_IBUF_SIZE = 1024;
        private static final int DATA_OBUF_SIZE = 4096;
        public static final String CONF_LZO_CLASS = "io.compression.codec.lzo.class";
        
        private Algorithm(final String name) {
            this.compressName = name;
        }
        
        abstract CompressionCodec getCodec() throws IOException;
        
        public abstract InputStream createDecompressionStream(final InputStream p0, final Decompressor p1, final int p2) throws IOException;
        
        public abstract OutputStream createCompressionStream(final OutputStream p0, final Compressor p1, final int p2) throws IOException;
        
        public abstract boolean isSupported();
        
        public Compressor getCompressor() throws IOException {
            final CompressionCodec codec = this.getCodec();
            if (codec != null) {
                final Compressor compressor = CodecPool.getCompressor(codec);
                if (compressor != null) {
                    if (compressor.finished()) {
                        Compression.LOG.warn("Compressor obtained from CodecPool already finished()");
                    }
                    else if (Compression.LOG.isDebugEnabled()) {
                        Compression.LOG.debug("Got a compressor: " + compressor.hashCode());
                    }
                    compressor.reset();
                }
                return compressor;
            }
            return null;
        }
        
        public void returnCompressor(final Compressor compressor) {
            if (compressor != null) {
                if (Compression.LOG.isDebugEnabled()) {
                    Compression.LOG.debug("Return a compressor: " + compressor.hashCode());
                }
                CodecPool.returnCompressor(compressor);
            }
        }
        
        public Decompressor getDecompressor() throws IOException {
            final CompressionCodec codec = this.getCodec();
            if (codec != null) {
                final Decompressor decompressor = CodecPool.getDecompressor(codec);
                if (decompressor != null) {
                    if (decompressor.finished()) {
                        Compression.LOG.warn("Deompressor obtained from CodecPool already finished()");
                    }
                    else if (Compression.LOG.isDebugEnabled()) {
                        Compression.LOG.debug("Got a decompressor: " + decompressor.hashCode());
                    }
                    decompressor.reset();
                }
                return decompressor;
            }
            return null;
        }
        
        public void returnDecompressor(final Decompressor decompressor) {
            if (decompressor != null) {
                if (Compression.LOG.isDebugEnabled()) {
                    Compression.LOG.debug("Returned a decompressor: " + decompressor.hashCode());
                }
                CodecPool.returnDecompressor(decompressor);
            }
        }
        
        public String getName() {
            return this.compressName;
        }
        
        static {
            conf = new Configuration();
        }
    }
}
