// 
// Decompiled by Procyon v0.5.36
// 

package org.eclipse.jetty.util;

import org.eclipse.jetty.util.log.Log;
import java.io.InterruptedIOException;
import java.util.concurrent.CancellationException;
import java.io.IOException;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.CountDownLatch;
import org.eclipse.jetty.util.log.Logger;

@Deprecated
public class BlockingCallback implements Callback.NonBlocking
{
    private static final Logger LOG;
    private static Throwable SUCCEEDED;
    private final CountDownLatch _latch;
    private final AtomicReference<Throwable> _state;
    
    public BlockingCallback() {
        this._latch = new CountDownLatch(1);
        this._state = new AtomicReference<Throwable>();
    }
    
    @Override
    public void succeeded() {
        if (this._state.compareAndSet(null, BlockingCallback.SUCCEEDED)) {
            this._latch.countDown();
        }
    }
    
    @Override
    public void failed(final Throwable cause) {
        if (this._state.compareAndSet(null, cause)) {
            this._latch.countDown();
        }
    }
    
    public void block() throws IOException {
        try {
            this._latch.await();
            final Throwable state = this._state.get();
            if (state == BlockingCallback.SUCCEEDED) {
                return;
            }
            if (state instanceof IOException) {
                throw (IOException)state;
            }
            if (state instanceof CancellationException) {
                throw (CancellationException)state;
            }
            throw new IOException(state);
        }
        catch (InterruptedException e) {
            throw new InterruptedIOException() {
                {
                    this.initCause(e);
                }
            };
        }
        finally {
            this._state.set(null);
        }
    }
    
    @Override
    public String toString() {
        return String.format("%s@%x{%s}", BlockingCallback.class.getSimpleName(), this.hashCode(), this._state.get());
    }
    
    static {
        LOG = Log.getLogger(BlockingCallback.class);
        BlockingCallback.SUCCEEDED = new ConstantThrowable("SUCCEEDED");
    }
}
