// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.util;

import java.nio.channels.AsynchronousCloseException;
import com.google.common.base.Preconditions;
import java.nio.channels.ClosedChannelException;
import java.util.concurrent.atomic.AtomicInteger;

public class CloseableReferenceCount
{
    private static final int STATUS_CLOSED_MASK = 1073741824;
    private final AtomicInteger status;
    
    public CloseableReferenceCount() {
        this.status = new AtomicInteger(0);
    }
    
    public void reference() throws ClosedChannelException {
        final int curBits = this.status.incrementAndGet();
        if ((curBits & 0x40000000) != 0x0) {
            this.status.decrementAndGet();
            throw new ClosedChannelException();
        }
    }
    
    public boolean unreference() {
        final int newVal = this.status.decrementAndGet();
        Preconditions.checkState(newVal != -1, (Object)"called unreference when the reference count was already at 0.");
        return newVal == 1073741824;
    }
    
    public void unreferenceCheckClosed() throws ClosedChannelException {
        final int newVal = this.status.decrementAndGet();
        if ((newVal & 0x40000000) != 0x0) {
            throw new AsynchronousCloseException();
        }
    }
    
    public boolean isOpen() {
        return (this.status.get() & 0x40000000) == 0x0;
    }
    
    public int setClosed() throws ClosedChannelException {
        while (true) {
            final int curBits = this.status.get();
            if ((curBits & 0x40000000) != 0x0) {
                throw new ClosedChannelException();
            }
            if (this.status.compareAndSet(curBits, curBits | 0x40000000)) {
                return curBits & 0xBFFFFFFF;
            }
        }
    }
    
    public int getReferenceCount() {
        return this.status.get() & 0xBFFFFFFF;
    }
}
