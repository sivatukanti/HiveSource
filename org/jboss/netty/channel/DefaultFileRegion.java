// 
// Decompiled by Procyon v0.5.36
// 

package org.jboss.netty.channel;

import org.jboss.netty.logging.InternalLoggerFactory;
import java.io.IOException;
import java.nio.channels.WritableByteChannel;
import java.nio.channels.FileChannel;
import org.jboss.netty.logging.InternalLogger;

public class DefaultFileRegion implements FileRegion
{
    private static final InternalLogger logger;
    private final FileChannel file;
    private final long position;
    private final long count;
    private final boolean releaseAfterTransfer;
    
    public DefaultFileRegion(final FileChannel file, final long position, final long count) {
        this(file, position, count, false);
    }
    
    public DefaultFileRegion(final FileChannel file, final long position, final long count, final boolean releaseAfterTransfer) {
        this.file = file;
        this.position = position;
        this.count = count;
        this.releaseAfterTransfer = releaseAfterTransfer;
    }
    
    public long getPosition() {
        return this.position;
    }
    
    public long getCount() {
        return this.count;
    }
    
    public boolean releaseAfterTransfer() {
        return this.releaseAfterTransfer;
    }
    
    public long transferTo(final WritableByteChannel target, final long position) throws IOException {
        final long count = this.count - position;
        if (count < 0L || position < 0L) {
            throw new IllegalArgumentException("position out of range: " + position + " (expected: 0 - " + (this.count - 1L) + ')');
        }
        if (count == 0L) {
            return 0L;
        }
        return this.file.transferTo(this.position + position, count, target);
    }
    
    public void releaseExternalResources() {
        try {
            this.file.close();
        }
        catch (IOException e) {
            if (DefaultFileRegion.logger.isWarnEnabled()) {
                DefaultFileRegion.logger.warn("Failed to close a file.", e);
            }
        }
    }
    
    static {
        logger = InternalLoggerFactory.getInstance(DefaultFileRegion.class);
    }
}
