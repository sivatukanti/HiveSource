// 
// Decompiled by Procyon v0.5.36
// 

package org.eclipse.jetty.io;

import org.eclipse.jetty.util.log.Log;
import java.util.concurrent.TimeUnit;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.io.EOFException;
import java.io.IOException;
import java.nio.channels.ClosedChannelException;
import java.util.ArrayDeque;
import org.eclipse.jetty.util.BufferUtil;
import org.eclipse.jetty.util.thread.Scheduler;
import java.util.Queue;
import java.util.concurrent.locks.Condition;
import org.eclipse.jetty.util.thread.Locker;
import java.nio.ByteBuffer;
import java.net.InetSocketAddress;
import org.eclipse.jetty.util.log.Logger;

public class ByteArrayEndPoint extends AbstractEndPoint
{
    static final Logger LOG;
    public static final InetSocketAddress NOIP;
    private static final ByteBuffer EOF;
    private final Runnable _runFillable;
    private final Locker _locker;
    private final Condition _hasOutput;
    private final Queue<ByteBuffer> _inQ;
    private ByteBuffer _out;
    private boolean _ishut;
    private boolean _oshut;
    private boolean _closed;
    private boolean _growOutput;
    
    public ByteArrayEndPoint() {
        this(null, 0L, null, null);
    }
    
    public ByteArrayEndPoint(final byte[] input, final int outputSize) {
        this(null, 0L, (input != null) ? BufferUtil.toBuffer(input) : null, BufferUtil.allocate(outputSize));
    }
    
    public ByteArrayEndPoint(final String input, final int outputSize) {
        this(null, 0L, (input != null) ? BufferUtil.toBuffer(input) : null, BufferUtil.allocate(outputSize));
    }
    
    public ByteArrayEndPoint(final Scheduler scheduler, final long idleTimeoutMs) {
        this(scheduler, idleTimeoutMs, null, null);
    }
    
    public ByteArrayEndPoint(final Scheduler timer, final long idleTimeoutMs, final byte[] input, final int outputSize) {
        this(timer, idleTimeoutMs, (input != null) ? BufferUtil.toBuffer(input) : null, BufferUtil.allocate(outputSize));
    }
    
    public ByteArrayEndPoint(final Scheduler timer, final long idleTimeoutMs, final String input, final int outputSize) {
        this(timer, idleTimeoutMs, (input != null) ? BufferUtil.toBuffer(input) : null, BufferUtil.allocate(outputSize));
    }
    
    public ByteArrayEndPoint(final Scheduler timer, final long idleTimeoutMs, final ByteBuffer input, final ByteBuffer output) {
        super(timer, ByteArrayEndPoint.NOIP, ByteArrayEndPoint.NOIP);
        this._runFillable = new Runnable() {
            @Override
            public void run() {
                ByteArrayEndPoint.this.getFillInterest().fillable();
            }
        };
        this._locker = new Locker();
        this._hasOutput = this._locker.newCondition();
        this._inQ = new ArrayDeque<ByteBuffer>();
        if (BufferUtil.hasContent(input)) {
            this.addInput(input);
        }
        this._out = ((output == null) ? BufferUtil.allocate(1024) : output);
        this.setIdleTimeout(idleTimeoutMs);
    }
    
    @Override
    protected void onIncompleteFlush() {
    }
    
    protected void execute(final Runnable task) {
        new Thread(task, "BAEPoint-" + Integer.toHexString(this.hashCode())).start();
    }
    
    @Override
    protected void needsFillInterest() throws IOException {
        final Locker.Lock lock = this._locker.lock();
        Throwable x0 = null;
        try {
            if (this._closed) {
                throw new ClosedChannelException();
            }
            final ByteBuffer in = this._inQ.peek();
            if (BufferUtil.hasContent(in) || in == ByteArrayEndPoint.EOF) {
                this.execute(this._runFillable);
            }
        }
        catch (Throwable t) {
            x0 = t;
            throw t;
        }
        finally {
            if (lock != null) {
                $closeResource(x0, lock);
            }
        }
    }
    
    public void addInputEOF() {
        this.addInput((ByteBuffer)null);
    }
    
    public void addInput(final ByteBuffer in) {
        boolean fillable = false;
        final Locker.Lock lock = this._locker.lock();
        Throwable x0 = null;
        try {
            if (this._inQ.peek() == ByteArrayEndPoint.EOF) {
                throw new RuntimeIOException(new EOFException());
            }
            final boolean was_empty = this._inQ.isEmpty();
            if (in == null) {
                this._inQ.add(ByteArrayEndPoint.EOF);
                fillable = true;
            }
            if (BufferUtil.hasContent(in)) {
                this._inQ.add(in);
                fillable = was_empty;
            }
        }
        catch (Throwable t) {
            x0 = t;
            throw t;
        }
        finally {
            if (lock != null) {
                $closeResource(x0, lock);
            }
        }
        if (fillable) {
            this._runFillable.run();
        }
    }
    
    public void addInputAndExecute(final ByteBuffer in) {
        boolean fillable = false;
        final Locker.Lock lock = this._locker.lock();
        Throwable x0 = null;
        try {
            if (this._inQ.peek() == ByteArrayEndPoint.EOF) {
                throw new RuntimeIOException(new EOFException());
            }
            final boolean was_empty = this._inQ.isEmpty();
            if (in == null) {
                this._inQ.add(ByteArrayEndPoint.EOF);
                fillable = true;
            }
            if (BufferUtil.hasContent(in)) {
                this._inQ.add(in);
                fillable = was_empty;
            }
        }
        catch (Throwable t) {
            x0 = t;
            throw t;
        }
        finally {
            if (lock != null) {
                $closeResource(x0, lock);
            }
        }
        if (fillable) {
            this.execute(this._runFillable);
        }
    }
    
    public void addInput(final String s) {
        this.addInput(BufferUtil.toBuffer(s, StandardCharsets.UTF_8));
    }
    
    public void addInput(final String s, final Charset charset) {
        this.addInput(BufferUtil.toBuffer(s, charset));
    }
    
    public ByteBuffer getOutput() {
        final Locker.Lock lock = this._locker.lock();
        Throwable x0 = null;
        try {
            return this._out;
        }
        catch (Throwable t) {
            x0 = t;
            throw t;
        }
        finally {
            if (lock != null) {
                $closeResource(x0, lock);
            }
        }
    }
    
    public String getOutputString() {
        return this.getOutputString(StandardCharsets.UTF_8);
    }
    
    public String getOutputString(final Charset charset) {
        return BufferUtil.toString(this._out, charset);
    }
    
    public ByteBuffer takeOutput() {
        final Locker.Lock lock = this._locker.lock();
        Throwable x0 = null;
        ByteBuffer b;
        try {
            b = this._out;
            this._out = BufferUtil.allocate(b.capacity());
        }
        catch (Throwable t) {
            x0 = t;
            throw t;
        }
        finally {
            if (lock != null) {
                $closeResource(x0, lock);
            }
        }
        this.getWriteFlusher().completeWrite();
        return b;
    }
    
    public ByteBuffer waitForOutput(final long time, final TimeUnit unit) throws InterruptedException {
        final Locker.Lock lock = this._locker.lock();
        Throwable x0 = null;
        ByteBuffer b;
        try {
            while (BufferUtil.isEmpty(this._out) && !this._closed && !this._oshut) {
                this._hasOutput.await(time, unit);
            }
            b = this._out;
            this._out = BufferUtil.allocate(b.capacity());
        }
        catch (Throwable t) {
            x0 = t;
            throw t;
        }
        finally {
            if (lock != null) {
                $closeResource(x0, lock);
            }
        }
        this.getWriteFlusher().completeWrite();
        return b;
    }
    
    public String takeOutputString() {
        return this.takeOutputString(StandardCharsets.UTF_8);
    }
    
    public String takeOutputString(final Charset charset) {
        final ByteBuffer buffer = this.takeOutput();
        return BufferUtil.toString(buffer, charset);
    }
    
    public void setOutput(final ByteBuffer out) {
        final Locker.Lock lock = this._locker.lock();
        Throwable x0 = null;
        try {
            this._out = out;
        }
        catch (Throwable t) {
            x0 = t;
            throw t;
        }
        finally {
            if (lock != null) {
                $closeResource(x0, lock);
            }
        }
        this.getWriteFlusher().completeWrite();
    }
    
    @Override
    public boolean isOpen() {
        final Locker.Lock lock = this._locker.lock();
        Throwable x0 = null;
        try {
            return !this._closed;
        }
        catch (Throwable t) {
            x0 = t;
            throw t;
        }
        finally {
            if (lock != null) {
                $closeResource(x0, lock);
            }
        }
    }
    
    @Override
    public boolean isInputShutdown() {
        final Locker.Lock lock = this._locker.lock();
        Throwable x0 = null;
        try {
            return this._ishut || this._closed;
        }
        catch (Throwable t) {
            x0 = t;
            throw t;
        }
        finally {
            if (lock != null) {
                $closeResource(x0, lock);
            }
        }
    }
    
    @Override
    public boolean isOutputShutdown() {
        final Locker.Lock lock = this._locker.lock();
        Throwable x0 = null;
        try {
            return this._oshut || this._closed;
        }
        catch (Throwable t) {
            x0 = t;
            throw t;
        }
        finally {
            if (lock != null) {
                $closeResource(x0, lock);
            }
        }
    }
    
    public void shutdownInput() {
        boolean close = false;
        final Locker.Lock lock = this._locker.lock();
        Throwable x0 = null;
        try {
            this._ishut = true;
            if (this._oshut && !this._closed) {
                final boolean closed = true;
                this._closed = closed;
                close = closed;
            }
        }
        catch (Throwable t) {
            x0 = t;
            throw t;
        }
        finally {
            if (lock != null) {
                $closeResource(x0, lock);
            }
        }
        if (close) {
            super.close();
        }
    }
    
    @Override
    public void shutdownOutput() {
        boolean close = false;
        final Locker.Lock lock = this._locker.lock();
        Throwable x0 = null;
        try {
            this._oshut = true;
            this._hasOutput.signalAll();
            if (this._ishut && !this._closed) {
                final boolean closed = true;
                this._closed = closed;
                close = closed;
            }
        }
        catch (Throwable t) {
            x0 = t;
            throw t;
        }
        finally {
            if (lock != null) {
                $closeResource(x0, lock);
            }
        }
        if (close) {
            super.close();
        }
    }
    
    @Override
    public void close() {
        boolean close = false;
        final Locker.Lock lock = this._locker.lock();
        Throwable x0 = null;
        try {
            if (!this._closed) {
                final boolean closed = true;
                this._oshut = closed;
                this._ishut = closed;
                this._closed = closed;
                close = closed;
            }
            this._hasOutput.signalAll();
        }
        catch (Throwable t) {
            x0 = t;
            throw t;
        }
        finally {
            if (lock != null) {
                $closeResource(x0, lock);
            }
        }
        if (close) {
            super.close();
        }
    }
    
    public boolean hasMore() {
        return this.getOutput().position() > 0;
    }
    
    @Override
    public int fill(final ByteBuffer buffer) throws IOException {
        int filled = 0;
        boolean close = false;
        final Locker.Lock lock = this._locker.lock();
        Throwable x0 = null;
        Label_0209: {
            try {
                while (!this._closed) {
                    if (this._ishut) {
                        return -1;
                    }
                    if (!this._inQ.isEmpty()) {
                        final ByteBuffer in = this._inQ.peek();
                        if (in == ByteArrayEndPoint.EOF) {
                            this._ishut = true;
                            if (this._oshut) {
                                final boolean closed = true;
                                this._closed = closed;
                                close = closed;
                            }
                            filled = -1;
                        }
                        else {
                            if (!BufferUtil.hasContent(in)) {
                                this._inQ.poll();
                                continue;
                            }
                            filled = BufferUtil.append(buffer, in);
                            if (BufferUtil.isEmpty(in)) {
                                this._inQ.poll();
                            }
                        }
                    }
                    break Label_0209;
                }
                throw new EofException("CLOSED");
            }
            catch (Throwable t) {
                x0 = t;
                throw t;
            }
            finally {
                if (lock != null) {
                    $closeResource(x0, lock);
                }
            }
        }
        if (close) {
            super.close();
        }
        if (filled > 0) {
            this.notIdle();
        }
        return filled;
    }
    
    @Override
    public boolean flush(final ByteBuffer... buffers) throws IOException {
        boolean flushed = true;
        final Locker.Lock lock = this._locker.lock();
        Throwable x0 = null;
        try {
            if (this._closed) {
                throw new IOException("CLOSED");
            }
            if (this._oshut) {
                throw new IOException("OSHUT");
            }
            boolean idle = true;
            for (final ByteBuffer b : buffers) {
                if (BufferUtil.hasContent(b)) {
                    if (this._growOutput && b.remaining() > BufferUtil.space(this._out)) {
                        BufferUtil.compact(this._out);
                        if (b.remaining() > BufferUtil.space(this._out)) {
                            final ByteBuffer n = BufferUtil.allocate(this._out.capacity() + b.remaining() * 2);
                            BufferUtil.append(n, this._out);
                            this._out = n;
                        }
                    }
                    if (BufferUtil.append(this._out, b) > 0) {
                        idle = false;
                    }
                    if (BufferUtil.hasContent(b)) {
                        flushed = false;
                        break;
                    }
                }
            }
            if (!idle) {
                this.notIdle();
                this._hasOutput.signalAll();
            }
        }
        catch (Throwable t) {
            x0 = t;
            throw t;
        }
        finally {
            if (lock != null) {
                $closeResource(x0, lock);
            }
        }
        return flushed;
    }
    
    public void reset() {
        final Locker.Lock lock = this._locker.lock();
        Throwable x0 = null;
        try {
            this.getFillInterest().onClose();
            this.getWriteFlusher().onClose();
            this._ishut = false;
            this._oshut = false;
            this._closed = false;
            this._inQ.clear();
            BufferUtil.clear(this._out);
        }
        catch (Throwable t) {
            x0 = t;
            throw t;
        }
        finally {
            if (lock != null) {
                $closeResource(x0, lock);
            }
        }
    }
    
    @Override
    public Object getTransport() {
        return null;
    }
    
    public boolean isGrowOutput() {
        return this._growOutput;
    }
    
    public void setGrowOutput(final boolean growOutput) {
        this._growOutput = growOutput;
    }
    
    @Override
    public String toString() {
        final Locker.Lock lock = this._locker.lock();
        Throwable x0 = null;
        int q;
        ByteBuffer b;
        String o;
        try {
            q = this._inQ.size();
            b = this._inQ.peek();
            o = BufferUtil.toDetailString(this._out);
        }
        catch (Throwable t) {
            x0 = t;
            throw t;
        }
        finally {
            if (lock != null) {
                $closeResource(x0, lock);
            }
        }
        return String.format("%s[q=%d,q[0]=%s,o=%s]", super.toString(), q, b, o);
    }
    
    private static /* synthetic */ void $closeResource(final Throwable x0, final AutoCloseable x1) {
        if (x0 != null) {
            try {
                x1.close();
            }
            catch (Throwable exception) {
                x0.addSuppressed(exception);
            }
        }
        else {
            x1.close();
        }
    }
    
    static {
        LOG = Log.getLogger(ByteArrayEndPoint.class);
        NOIP = new InetSocketAddress(0);
        EOF = BufferUtil.allocate(0);
    }
}
