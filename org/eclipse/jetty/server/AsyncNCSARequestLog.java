// 
// Decompiled by Procyon v0.5.36
// 

package org.eclipse.jetty.server;

import java.util.concurrent.TimeUnit;
import org.eclipse.jetty.util.log.Log;
import java.io.IOException;
import org.eclipse.jetty.util.BlockingArrayQueue;
import java.util.concurrent.BlockingQueue;
import org.eclipse.jetty.util.log.Logger;

public class AsyncNCSARequestLog extends NCSARequestLog
{
    private static final Logger LOG;
    private final BlockingQueue<String> _queue;
    private transient WriterThread _thread;
    private boolean _warnedFull;
    
    public AsyncNCSARequestLog() {
        this(null, null);
    }
    
    public AsyncNCSARequestLog(final BlockingQueue<String> queue) {
        this(null, queue);
    }
    
    public AsyncNCSARequestLog(final String filename) {
        this(filename, null);
    }
    
    public AsyncNCSARequestLog(final String filename, BlockingQueue<String> queue) {
        super(filename);
        if (queue == null) {
            queue = new BlockingArrayQueue<String>(1024);
        }
        this._queue = queue;
    }
    
    @Override
    protected synchronized void doStart() throws Exception {
        super.doStart();
        (this._thread = new WriterThread()).start();
    }
    
    @Override
    protected void doStop() throws Exception {
        this._thread.interrupt();
        this._thread.join();
        super.doStop();
        this._thread = null;
    }
    
    @Override
    public void write(final String log) throws IOException {
        if (!this._queue.offer(log)) {
            if (this._warnedFull) {
                AsyncNCSARequestLog.LOG.warn("Log Queue overflow", new Object[0]);
            }
            this._warnedFull = true;
        }
    }
    
    static {
        LOG = Log.getLogger(AsyncNCSARequestLog.class);
    }
    
    private class WriterThread extends Thread
    {
        WriterThread() {
            this.setName("AsyncNCSARequestLog@" + Integer.toString(AsyncNCSARequestLog.this.hashCode(), 16));
        }
        
        @Override
        public void run() {
            while (AsyncNCSARequestLog.this.isRunning()) {
                try {
                    String log = AsyncNCSARequestLog.this._queue.poll(10L, TimeUnit.SECONDS);
                    if (log != null) {
                        NCSARequestLog.this.write(log);
                    }
                    while (!AsyncNCSARequestLog.this._queue.isEmpty()) {
                        log = (String)AsyncNCSARequestLog.this._queue.poll();
                        if (log != null) {
                            NCSARequestLog.this.write(log);
                        }
                    }
                }
                catch (IOException e) {
                    AsyncNCSARequestLog.LOG.warn(e);
                }
                catch (InterruptedException e2) {
                    AsyncNCSARequestLog.LOG.ignore(e2);
                }
            }
        }
    }
}
