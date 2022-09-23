// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.io;

import java.io.IOException;
import org.slf4j.LoggerFactory;
import com.google.common.base.Preconditions;
import java.io.FileDescriptor;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.TimeUnit;
import org.apache.hadoop.io.nativeio.NativeIO;
import java.util.concurrent.ThreadPoolExecutor;
import org.slf4j.Logger;
import org.apache.hadoop.classification.InterfaceStability;
import org.apache.hadoop.classification.InterfaceAudience;

@InterfaceAudience.Private
@InterfaceStability.Evolving
public class ReadaheadPool
{
    static final Logger LOG;
    private static final int POOL_SIZE = 4;
    private static final int MAX_POOL_SIZE = 16;
    private static final int CAPACITY = 1024;
    private final ThreadPoolExecutor pool;
    private static ReadaheadPool instance;
    
    public static ReadaheadPool getInstance() {
        synchronized (ReadaheadPool.class) {
            if (ReadaheadPool.instance == null && NativeIO.isAvailable()) {
                ReadaheadPool.instance = new ReadaheadPool();
            }
            return ReadaheadPool.instance;
        }
    }
    
    private ReadaheadPool() {
        (this.pool = new ThreadPoolExecutor(4, 16, 3L, TimeUnit.SECONDS, new ArrayBlockingQueue<Runnable>(1024))).setRejectedExecutionHandler(new ThreadPoolExecutor.DiscardOldestPolicy());
        this.pool.setThreadFactory(new ThreadFactoryBuilder().setDaemon(true).setNameFormat("Readahead Thread #%d").build());
    }
    
    public ReadaheadRequest readaheadStream(final String identifier, final FileDescriptor fd, final long curPos, final long readaheadLength, final long maxOffsetToRead, ReadaheadRequest lastReadahead) {
        Preconditions.checkArgument(curPos <= maxOffsetToRead, "Readahead position %s higher than maxOffsetToRead %s", curPos, maxOffsetToRead);
        if (readaheadLength <= 0L) {
            return null;
        }
        long lastOffset = Long.MIN_VALUE;
        if (lastReadahead != null) {
            lastOffset = lastReadahead.getOffset();
        }
        final long nextOffset = lastOffset + readaheadLength / 2L;
        if (curPos < nextOffset) {
            return lastReadahead;
        }
        if (lastReadahead != null) {
            lastReadahead.cancel();
            lastReadahead = null;
        }
        final long length = Math.min(readaheadLength, maxOffsetToRead - curPos);
        if (length <= 0L) {
            return null;
        }
        return this.submitReadahead(identifier, fd, curPos, length);
    }
    
    public ReadaheadRequest submitReadahead(final String identifier, final FileDescriptor fd, final long off, final long len) {
        final ReadaheadRequestImpl req = new ReadaheadRequestImpl(identifier, fd, off, len);
        this.pool.execute(req);
        if (ReadaheadPool.LOG.isTraceEnabled()) {
            ReadaheadPool.LOG.trace("submit readahead: " + req);
        }
        return req;
    }
    
    static {
        LOG = LoggerFactory.getLogger(ReadaheadPool.class);
    }
    
    private static class ReadaheadRequestImpl implements Runnable, ReadaheadRequest
    {
        private final String identifier;
        private final FileDescriptor fd;
        private final long off;
        private final long len;
        private volatile boolean canceled;
        
        private ReadaheadRequestImpl(final String identifier, final FileDescriptor fd, final long off, final long len) {
            this.canceled = false;
            this.identifier = identifier;
            this.fd = fd;
            this.off = off;
            this.len = len;
        }
        
        @Override
        public void run() {
            if (this.canceled) {
                return;
            }
            try {
                if (this.fd.valid()) {
                    NativeIO.POSIX.getCacheManipulator().posixFadviseIfPossible(this.identifier, this.fd, this.off, this.len, NativeIO.POSIX.POSIX_FADV_WILLNEED);
                }
            }
            catch (IOException ioe) {
                if (this.canceled) {
                    return;
                }
                ReadaheadPool.LOG.warn("Failed readahead on " + this.identifier, ioe);
            }
        }
        
        @Override
        public void cancel() {
            this.canceled = true;
        }
        
        @Override
        public long getOffset() {
            return this.off;
        }
        
        @Override
        public long getLength() {
            return this.len;
        }
        
        @Override
        public String toString() {
            return "ReadaheadRequestImpl [identifier='" + this.identifier + "', fd=" + this.fd + ", off=" + this.off + ", len=" + this.len + "]";
        }
    }
    
    public interface ReadaheadRequest
    {
        void cancel();
        
        long getOffset();
        
        long getLength();
    }
}
