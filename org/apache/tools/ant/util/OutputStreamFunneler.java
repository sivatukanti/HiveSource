// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.tools.ant.util;

import java.io.IOException;
import java.io.OutputStream;

public class OutputStreamFunneler
{
    public static final long DEFAULT_TIMEOUT_MILLIS = 1000L;
    private OutputStream out;
    private int count;
    private boolean closed;
    private long timeoutMillis;
    
    public OutputStreamFunneler(final OutputStream out) {
        this(out, 1000L);
    }
    
    public OutputStreamFunneler(final OutputStream out, final long timeoutMillis) {
        this.count = 0;
        if (out == null) {
            throw new IllegalArgumentException("OutputStreamFunneler.<init>:  out == null");
        }
        this.out = out;
        this.closed = false;
        this.setTimeout(timeoutMillis);
    }
    
    public synchronized void setTimeout(final long timeoutMillis) {
        this.timeoutMillis = timeoutMillis;
    }
    
    public synchronized OutputStream getFunnelInstance() throws IOException {
        this.dieIfClosed();
        try {
            return new Funnel();
        }
        finally {
            this.notifyAll();
        }
    }
    
    private synchronized void release(final Funnel funnel) throws IOException {
        if (!funnel.closed) {
            try {
                if (this.timeoutMillis > 0L) {
                    try {
                        this.wait(this.timeoutMillis);
                    }
                    catch (InterruptedException ex) {}
                }
                if (--this.count == 0) {
                    this.close();
                }
            }
            finally {
                funnel.closed = true;
            }
        }
    }
    
    private synchronized void close() throws IOException {
        try {
            this.dieIfClosed();
            this.out.close();
        }
        finally {
            this.closed = true;
        }
    }
    
    private synchronized void dieIfClosed() throws IOException {
        if (this.closed) {
            throw new IOException("The funneled OutputStream has been closed.");
        }
    }
    
    private final class Funnel extends OutputStream
    {
        private boolean closed;
        
        private Funnel() {
            this.closed = false;
            synchronized (OutputStreamFunneler.this) {
                ++OutputStreamFunneler.this.count;
            }
        }
        
        @Override
        public void flush() throws IOException {
            synchronized (OutputStreamFunneler.this) {
                OutputStreamFunneler.this.dieIfClosed();
                OutputStreamFunneler.this.out.flush();
            }
        }
        
        @Override
        public void write(final int b) throws IOException {
            synchronized (OutputStreamFunneler.this) {
                OutputStreamFunneler.this.dieIfClosed();
                OutputStreamFunneler.this.out.write(b);
            }
        }
        
        @Override
        public void write(final byte[] b) throws IOException {
            synchronized (OutputStreamFunneler.this) {
                OutputStreamFunneler.this.dieIfClosed();
                OutputStreamFunneler.this.out.write(b);
            }
        }
        
        @Override
        public void write(final byte[] b, final int off, final int len) throws IOException {
            synchronized (OutputStreamFunneler.this) {
                OutputStreamFunneler.this.dieIfClosed();
                OutputStreamFunneler.this.out.write(b, off, len);
            }
        }
        
        @Override
        public void close() throws IOException {
            OutputStreamFunneler.this.release(this);
        }
    }
}
