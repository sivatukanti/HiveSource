// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.io.compress;

import java.io.BufferedInputStream;
import org.apache.hadoop.io.compress.bzip2.CBZip2InputStream;
import java.nio.charset.StandardCharsets;
import org.apache.hadoop.io.compress.bzip2.CBZip2OutputStream;
import org.apache.hadoop.fs.Seekable;
import java.io.InputStream;
import org.apache.hadoop.io.compress.bzip2.Bzip2Factory;
import java.io.IOException;
import java.io.OutputStream;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.classification.InterfaceStability;
import org.apache.hadoop.classification.InterfaceAudience;
import org.apache.hadoop.conf.Configurable;

@InterfaceAudience.Public
@InterfaceStability.Evolving
public class BZip2Codec implements Configurable, SplittableCompressionCodec
{
    private static final String HEADER = "BZ";
    private static final int HEADER_LEN;
    private static final String SUB_HEADER = "h9";
    private static final int SUB_HEADER_LEN;
    private Configuration conf;
    
    @Override
    public void setConf(final Configuration conf) {
        this.conf = conf;
    }
    
    @Override
    public Configuration getConf() {
        return this.conf;
    }
    
    @Override
    public CompressionOutputStream createOutputStream(final OutputStream out) throws IOException {
        return CompressionCodec.Util.createOutputStreamWithCodecPool(this, this.conf, out);
    }
    
    @Override
    public CompressionOutputStream createOutputStream(final OutputStream out, final Compressor compressor) throws IOException {
        return Bzip2Factory.isNativeBzip2Loaded(this.conf) ? new CompressorStream(out, compressor, this.conf.getInt("io.file.buffer.size", 4096)) : new BZip2CompressionOutputStream(out);
    }
    
    @Override
    public Class<? extends Compressor> getCompressorType() {
        return Bzip2Factory.getBzip2CompressorType(this.conf);
    }
    
    @Override
    public Compressor createCompressor() {
        return Bzip2Factory.getBzip2Compressor(this.conf);
    }
    
    @Override
    public CompressionInputStream createInputStream(final InputStream in) throws IOException {
        return CompressionCodec.Util.createInputStreamWithCodecPool(this, this.conf, in);
    }
    
    @Override
    public CompressionInputStream createInputStream(final InputStream in, final Decompressor decompressor) throws IOException {
        return Bzip2Factory.isNativeBzip2Loaded(this.conf) ? new DecompressorStream(in, decompressor, this.conf.getInt("io.file.buffer.size", 4096)) : new BZip2CompressionInputStream(in, 0L, Long.MAX_VALUE, READ_MODE.BYBLOCK);
    }
    
    @Override
    public SplitCompressionInputStream createInputStream(final InputStream seekableIn, final Decompressor decompressor, final long start, final long end, final READ_MODE readMode) throws IOException {
        if (!(seekableIn instanceof Seekable)) {
            throw new IOException("seekableIn must be an instance of " + Seekable.class.getName());
        }
        ((Seekable)seekableIn).seek(start);
        return new BZip2CompressionInputStream(seekableIn, start, end, readMode);
    }
    
    @Override
    public Class<? extends Decompressor> getDecompressorType() {
        return Bzip2Factory.getBzip2DecompressorType(this.conf);
    }
    
    @Override
    public Decompressor createDecompressor() {
        return Bzip2Factory.getBzip2Decompressor(this.conf);
    }
    
    @Override
    public String getDefaultExtension() {
        return ".bz2";
    }
    
    static {
        HEADER_LEN = "BZ".length();
        SUB_HEADER_LEN = "h9".length();
    }
    
    private static class BZip2CompressionOutputStream extends CompressionOutputStream
    {
        private CBZip2OutputStream output;
        private boolean needsReset;
        
        public BZip2CompressionOutputStream(final OutputStream out) throws IOException {
            super(out);
            this.needsReset = true;
        }
        
        private void writeStreamHeader() throws IOException {
            if (super.out != null) {
                this.out.write("BZ".getBytes(StandardCharsets.UTF_8));
            }
        }
        
        @Override
        public void finish() throws IOException {
            if (this.needsReset) {
                this.internalReset();
            }
            this.output.finish();
            this.needsReset = true;
        }
        
        private void internalReset() throws IOException {
            if (this.needsReset) {
                this.needsReset = false;
                this.writeStreamHeader();
                this.output = new CBZip2OutputStream(this.out);
            }
        }
        
        @Override
        public void resetState() throws IOException {
            this.needsReset = true;
        }
        
        @Override
        public void write(final int b) throws IOException {
            if (this.needsReset) {
                this.internalReset();
            }
            this.output.write(b);
        }
        
        @Override
        public void write(final byte[] b, final int off, final int len) throws IOException {
            if (this.needsReset) {
                this.internalReset();
            }
            this.output.write(b, off, len);
        }
        
        @Override
        public void close() throws IOException {
            try {
                super.close();
            }
            finally {
                this.output.close();
            }
        }
    }
    
    private static class BZip2CompressionInputStream extends SplitCompressionInputStream
    {
        private CBZip2InputStream input;
        boolean needsReset;
        private BufferedInputStream bufferedIn;
        private boolean isHeaderStripped;
        private boolean isSubHeaderStripped;
        private READ_MODE readMode;
        private long startingPos;
        POS_ADVERTISEMENT_STATE_MACHINE posSM;
        long compressedStreamPosition;
        
        public BZip2CompressionInputStream(final InputStream in) throws IOException {
            this(in, 0L, Long.MAX_VALUE, READ_MODE.CONTINUOUS);
        }
        
        public BZip2CompressionInputStream(final InputStream in, final long start, final long end, final READ_MODE readMode) throws IOException {
            super(in, start, end);
            this.isHeaderStripped = false;
            this.isSubHeaderStripped = false;
            this.readMode = READ_MODE.CONTINUOUS;
            this.startingPos = 0L;
            this.posSM = POS_ADVERTISEMENT_STATE_MACHINE.HOLD;
            this.compressedStreamPosition = 0L;
            this.needsReset = false;
            this.bufferedIn = new BufferedInputStream(super.in);
            this.startingPos = super.getPos();
            this.readMode = readMode;
            long numSkipped = 0L;
            if (this.startingPos == 0L) {
                this.bufferedIn = this.readStreamHeader();
            }
            else if (this.readMode == READ_MODE.BYBLOCK && this.startingPos <= BZip2Codec.HEADER_LEN + BZip2Codec.SUB_HEADER_LEN) {
                long skipBytes;
                numSkipped = (skipBytes = BZip2Codec.HEADER_LEN + BZip2Codec.SUB_HEADER_LEN + 1 - this.startingPos);
                while (skipBytes > 0L) {
                    final long s = this.bufferedIn.skip(skipBytes);
                    if (s > 0L) {
                        skipBytes -= s;
                    }
                    else {
                        if (this.bufferedIn.read() == -1) {
                            break;
                        }
                        --skipBytes;
                    }
                }
            }
            this.input = new CBZip2InputStream(this.bufferedIn, readMode);
            if (this.isHeaderStripped) {
                this.input.updateReportedByteCount(BZip2Codec.HEADER_LEN);
            }
            if (this.isSubHeaderStripped) {
                this.input.updateReportedByteCount(BZip2Codec.SUB_HEADER_LEN);
            }
            if (numSkipped > 0L) {
                this.input.updateReportedByteCount((int)numSkipped);
            }
            if (this.readMode != READ_MODE.BYBLOCK || this.startingPos != 0L) {
                this.updatePos(false);
            }
        }
        
        private BufferedInputStream readStreamHeader() throws IOException {
            if (super.in != null) {
                this.bufferedIn.mark(BZip2Codec.HEADER_LEN);
                final byte[] headerBytes = new byte[BZip2Codec.HEADER_LEN];
                int actualRead = this.bufferedIn.read(headerBytes, 0, BZip2Codec.HEADER_LEN);
                if (actualRead != -1) {
                    final String header = new String(headerBytes, StandardCharsets.UTF_8);
                    if (header.compareTo("BZ") != 0) {
                        this.bufferedIn.reset();
                    }
                    else {
                        this.isHeaderStripped = true;
                        if (this.readMode == READ_MODE.BYBLOCK) {
                            actualRead = this.bufferedIn.read(headerBytes, 0, BZip2Codec.SUB_HEADER_LEN);
                            if (actualRead != -1) {
                                this.isSubHeaderStripped = true;
                            }
                        }
                    }
                }
            }
            if (this.bufferedIn == null) {
                throw new IOException("Failed to read bzip2 stream.");
            }
            return this.bufferedIn;
        }
        
        @Override
        public void close() throws IOException {
            if (!this.needsReset) {
                try {
                    this.input.close();
                    this.needsReset = true;
                }
                finally {
                    super.close();
                }
            }
        }
        
        @Override
        public int read(final byte[] b, final int off, final int len) throws IOException {
            if (this.needsReset) {
                this.internalReset();
            }
            int result = 0;
            result = this.input.read(b, off, len);
            if (result == -2) {
                this.posSM = POS_ADVERTISEMENT_STATE_MACHINE.ADVERTISE;
            }
            if (this.posSM == POS_ADVERTISEMENT_STATE_MACHINE.ADVERTISE) {
                result = this.input.read(b, off, off + 1);
                this.updatePos(true);
                this.posSM = POS_ADVERTISEMENT_STATE_MACHINE.HOLD;
            }
            return result;
        }
        
        @Override
        public int read() throws IOException {
            final byte[] b = { 0 };
            final int result = this.read(b, 0, 1);
            return (result < 0) ? result : (b[0] & 0xFF);
        }
        
        private void internalReset() throws IOException {
            if (this.needsReset) {
                this.needsReset = false;
                final BufferedInputStream bufferedIn = this.readStreamHeader();
                this.input = new CBZip2InputStream(bufferedIn, this.readMode);
            }
        }
        
        @Override
        public void resetState() throws IOException {
            this.needsReset = true;
        }
        
        @Override
        public long getPos() {
            return this.compressedStreamPosition;
        }
        
        private void updatePos(final boolean shouldAddOn) {
            final int addOn = shouldAddOn ? 1 : 0;
            this.compressedStreamPosition = this.startingPos + this.input.getProcessedByteCount() + addOn;
        }
        
        private enum POS_ADVERTISEMENT_STATE_MACHINE
        {
            HOLD, 
            ADVERTISE;
        }
    }
}
