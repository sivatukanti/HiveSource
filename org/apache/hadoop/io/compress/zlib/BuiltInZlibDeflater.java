// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.io.compress.zlib;

import org.slf4j.LoggerFactory;
import org.apache.hadoop.conf.Configuration;
import java.io.IOException;
import org.slf4j.Logger;
import org.apache.hadoop.io.compress.Compressor;
import java.util.zip.Deflater;

public class BuiltInZlibDeflater extends Deflater implements Compressor
{
    private static final Logger LOG;
    
    public BuiltInZlibDeflater(final int level, final boolean nowrap) {
        super(level, nowrap);
    }
    
    public BuiltInZlibDeflater(final int level) {
        super(level);
    }
    
    public BuiltInZlibDeflater() {
    }
    
    @Override
    public synchronized int compress(final byte[] b, final int off, final int len) throws IOException {
        return super.deflate(b, off, len);
    }
    
    @Override
    public void reinit(final Configuration conf) {
        this.reset();
        if (conf == null) {
            return;
        }
        this.setLevel(ZlibFactory.getCompressionLevel(conf).compressionLevel());
        final ZlibCompressor.CompressionStrategy strategy = ZlibFactory.getCompressionStrategy(conf);
        try {
            this.setStrategy(strategy.compressionStrategy());
        }
        catch (IllegalArgumentException ill) {
            BuiltInZlibDeflater.LOG.warn(strategy + " not supported by BuiltInZlibDeflater.");
            this.setStrategy(0);
        }
        if (BuiltInZlibDeflater.LOG.isDebugEnabled()) {
            BuiltInZlibDeflater.LOG.debug("Reinit compressor with new compression configuration");
        }
    }
    
    static {
        LOG = LoggerFactory.getLogger(BuiltInZlibDeflater.class);
    }
}
