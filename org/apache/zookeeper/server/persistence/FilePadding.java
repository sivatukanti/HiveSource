// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.zookeeper.server.persistence;

import org.slf4j.LoggerFactory;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.nio.ByteBuffer;
import org.slf4j.Logger;

public class FilePadding
{
    private static final Logger LOG;
    private static long preAllocSize;
    private static final ByteBuffer fill;
    private long currentSize;
    
    public static long getPreAllocSize() {
        return FilePadding.preAllocSize;
    }
    
    public static void setPreallocSize(final long size) {
        FilePadding.preAllocSize = size;
    }
    
    public void setCurrentSize(final long currentSize) {
        this.currentSize = currentSize;
    }
    
    long padFile(final FileChannel fileChannel) throws IOException {
        final long newFileSize = calculateFileSizeWithPadding(fileChannel.position(), this.currentSize, FilePadding.preAllocSize);
        if (this.currentSize != newFileSize) {
            fileChannel.write((ByteBuffer)FilePadding.fill.position(0), newFileSize - FilePadding.fill.remaining());
            this.currentSize = newFileSize;
        }
        return this.currentSize;
    }
    
    public static long calculateFileSizeWithPadding(final long position, long fileSize, final long preAllocSize) {
        if (preAllocSize > 0L && position + 4096L >= fileSize) {
            if (position > fileSize) {
                fileSize = position + preAllocSize;
                fileSize -= fileSize % preAllocSize;
            }
            else {
                fileSize += preAllocSize;
            }
        }
        return fileSize;
    }
    
    static {
        FilePadding.preAllocSize = 67108864L;
        fill = ByteBuffer.allocateDirect(1);
        LOG = LoggerFactory.getLogger(FileTxnLog.class);
        final String size = System.getProperty("zookeeper.preAllocSize");
        if (size != null) {
            try {
                FilePadding.preAllocSize = Long.parseLong(size) * 1024L;
            }
            catch (NumberFormatException e) {
                FilePadding.LOG.warn(size + " is not a valid value for preAllocSize");
            }
        }
    }
}
