// 
// Decompiled by Procyon v0.5.36
// 

package org.eclipse.jetty.io.nio;

import org.eclipse.jetty.util.thread.Timeout;
import java.nio.ByteBuffer;
import javax.net.ssl.SSLException;
import java.io.IOException;
import org.eclipse.jetty.io.Buffer;
import javax.net.ssl.SSLEngineResult;
import org.eclipse.jetty.io.Connection;
import org.eclipse.jetty.util.log.Log;
import org.eclipse.jetty.io.EndPoint;
import java.util.concurrent.atomic.AtomicBoolean;
import org.eclipse.jetty.io.AsyncEndPoint;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLEngine;
import org.eclipse.jetty.util.log.Logger;
import org.eclipse.jetty.io.AbstractConnection;

public class SslConnection extends AbstractConnection implements AsyncConnection
{
    private final Logger _logger;
    private static final NIOBuffer __ZERO_BUFFER;
    private static final ThreadLocal<SslBuffers> __buffers;
    private final SSLEngine _engine;
    private final SSLSession _session;
    private AsyncConnection _connection;
    private final SslEndPoint _sslEndPoint;
    private int _allocations;
    private SslBuffers _buffers;
    private NIOBuffer _inbound;
    private NIOBuffer _unwrapBuf;
    private NIOBuffer _outbound;
    private AsyncEndPoint _aEndp;
    private boolean _allowRenegotiate;
    private boolean _handshook;
    private boolean _ishut;
    private boolean _oshut;
    private final AtomicBoolean _progressed;
    
    public SslConnection(final SSLEngine engine, final EndPoint endp) {
        this(engine, endp, System.currentTimeMillis());
    }
    
    public SslConnection(final SSLEngine engine, final EndPoint endp, final long timeStamp) {
        super(endp, timeStamp);
        this._logger = Log.getLogger("org.eclipse.jetty.io.nio.ssl");
        this._allowRenegotiate = true;
        this._progressed = new AtomicBoolean();
        this._engine = engine;
        this._session = this._engine.getSession();
        this._aEndp = (AsyncEndPoint)endp;
        this._sslEndPoint = this.newSslEndPoint();
    }
    
    protected SslEndPoint newSslEndPoint() {
        return new SslEndPoint();
    }
    
    public boolean isAllowRenegotiate() {
        return this._allowRenegotiate;
    }
    
    public void setAllowRenegotiate(final boolean allowRenegotiate) {
        this._allowRenegotiate = allowRenegotiate;
    }
    
    private void allocateBuffers() {
        synchronized (this) {
            if (this._allocations++ == 0 && this._buffers == null) {
                this._buffers = SslConnection.__buffers.get();
                if (this._buffers == null) {
                    this._buffers = new SslBuffers(this._session.getPacketBufferSize() * 2, this._session.getApplicationBufferSize() * 2);
                }
                this._inbound = this._buffers._in;
                this._outbound = this._buffers._out;
                this._unwrapBuf = this._buffers._unwrap;
                SslConnection.__buffers.set(null);
            }
        }
    }
    
    private void releaseBuffers() {
        synchronized (this) {
            final int allocations = this._allocations - 1;
            this._allocations = allocations;
            if (allocations == 0 && this._buffers != null && this._inbound.length() == 0 && this._outbound.length() == 0 && this._unwrapBuf.length() == 0) {
                this._inbound = null;
                this._outbound = null;
                this._unwrapBuf = null;
                SslConnection.__buffers.set(this._buffers);
                this._buffers = null;
            }
        }
    }
    
    public Connection handle() throws IOException {
        try {
            this.allocateBuffers();
            boolean progress = true;
            while (progress) {
                progress = false;
                if (this._engine.getHandshakeStatus() != SSLEngineResult.HandshakeStatus.NOT_HANDSHAKING) {
                    progress = this.process(null, null);
                }
                final AsyncConnection next = (AsyncConnection)this._connection.handle();
                if (next != this._connection && next != null) {
                    this._connection = next;
                    progress = true;
                }
                this._logger.debug("{} handle {} progress={}", this._session, this, progress);
            }
        }
        finally {
            this.releaseBuffers();
            if (!this._ishut && this._sslEndPoint.isInputShutdown() && this._sslEndPoint.isOpen()) {
                this._ishut = true;
                try {
                    this._connection.onInputShutdown();
                }
                catch (Throwable x) {
                    this._logger.warn("onInputShutdown failed", x);
                    try {
                        this._sslEndPoint.close();
                    }
                    catch (IOException e2) {
                        this._logger.ignore(e2);
                    }
                }
            }
        }
        return this;
    }
    
    public boolean isIdle() {
        return false;
    }
    
    public boolean isSuspended() {
        return false;
    }
    
    @Override
    public void onClose() {
    }
    
    public void onIdleExpired(final long idleForMs) {
        try {
            this._logger.debug("onIdleExpired {}ms on {}", idleForMs, this);
            if (this._endp.isOutputShutdown()) {
                this._sslEndPoint.close();
            }
            else {
                this._sslEndPoint.shutdownOutput();
            }
        }
        catch (IOException e) {
            this._logger.warn(e);
            super.onIdleExpired(idleForMs);
        }
    }
    
    public void onInputShutdown() throws IOException {
    }
    
    private synchronized boolean process(Buffer toFill, Buffer toFlush) throws IOException {
        boolean some_progress = false;
        try {
            this.allocateBuffers();
            if (toFill == null) {
                this._unwrapBuf.compact();
                toFill = this._unwrapBuf;
            }
            else if (toFill.capacity() < this._session.getApplicationBufferSize()) {
                final boolean progress = this.process(null, toFlush);
                if (this._unwrapBuf != null && this._unwrapBuf.hasContent()) {
                    this._unwrapBuf.skip(toFill.put(this._unwrapBuf));
                    return true;
                }
                return progress;
            }
            else if (this._unwrapBuf != null && this._unwrapBuf.hasContent()) {
                this._unwrapBuf.skip(toFill.put(this._unwrapBuf));
                return true;
            }
            if (toFlush == null) {
                toFlush = SslConnection.__ZERO_BUFFER;
            }
            boolean progress = true;
            while (progress) {
                progress = false;
                int filled = 0;
                int flushed = 0;
                try {
                    if (this._inbound.space() > 0 && (filled = this._endp.fill((Buffer)this._inbound)) > 0) {
                        progress = true;
                    }
                    if (this._outbound.hasContent() && (flushed = this._endp.flush((Buffer)this._outbound)) > 0) {
                        progress = true;
                    }
                }
                catch (IOException e) {
                    this._endp.close();
                    throw e;
                }
                finally {
                    this._logger.debug("{} {} {} filled={}/{} flushed={}/{}", this._session, this, this._engine.getHandshakeStatus(), filled, this._inbound.length(), flushed, this._outbound.length());
                }
                switch (this._engine.getHandshakeStatus()) {
                    case FINISHED: {
                        throw new IllegalStateException();
                    }
                    case NOT_HANDSHAKING: {
                        if (toFill.space() > 0 && this._inbound.hasContent() && this.unwrap(toFill)) {
                            progress = true;
                        }
                        if (toFlush.hasContent() && this._outbound.space() > 0 && this.wrap(toFlush)) {
                            progress = true;
                            break;
                        }
                        break;
                    }
                    case NEED_TASK: {
                        Runnable task;
                        while ((task = this._engine.getDelegatedTask()) != null) {
                            progress = true;
                            task.run();
                        }
                        break;
                    }
                    case NEED_WRAP: {
                        if (this._handshook && !this._allowRenegotiate) {
                            this._endp.close();
                            break;
                        }
                        if (this.wrap(toFlush)) {
                            progress = true;
                            break;
                        }
                        break;
                    }
                    case NEED_UNWRAP: {
                        if (this._handshook && !this._allowRenegotiate) {
                            this._endp.close();
                            break;
                        }
                        if (!this._inbound.hasContent() && filled == -1) {
                            this._endp.shutdownInput();
                            break;
                        }
                        if (this.unwrap(toFill)) {
                            progress = true;
                            break;
                        }
                        break;
                    }
                }
                if (this._endp.isOpen() && this._endp.isInputShutdown() && !this._inbound.hasContent()) {
                    this._engine.closeInbound();
                }
                if (this._endp.isOpen() && this._engine.isOutboundDone() && !this._outbound.hasContent()) {
                    this._endp.shutdownOutput();
                }
                some_progress |= progress;
            }
            if (toFill == this._unwrapBuf && this._unwrapBuf.hasContent()) {
                this._aEndp.asyncDispatch();
            }
        }
        finally {
            this.releaseBuffers();
            if (some_progress) {
                this._progressed.set(true);
            }
        }
        return some_progress;
    }
    
    private synchronized boolean wrap(final Buffer buffer) throws IOException {
        final ByteBuffer bbuf = this.extractByteBuffer(buffer);
        SSLEngineResult result;
        synchronized (bbuf) {
            this._outbound.compact();
            final ByteBuffer out_buffer = this._outbound.getByteBuffer();
            synchronized (out_buffer) {
                try {
                    bbuf.position(buffer.getIndex());
                    bbuf.limit(buffer.putIndex());
                    out_buffer.position(this._outbound.putIndex());
                    out_buffer.limit(out_buffer.capacity());
                    result = this._engine.wrap(bbuf, out_buffer);
                    if (this._logger.isDebugEnabled()) {
                        this._logger.debug("{} wrap {} {} consumed={} produced={}", this._session, result.getStatus(), result.getHandshakeStatus(), result.bytesConsumed(), result.bytesProduced());
                    }
                    buffer.skip(result.bytesConsumed());
                    this._outbound.setPutIndex(this._outbound.putIndex() + result.bytesProduced());
                }
                catch (SSLException e) {
                    this._logger.debug(String.valueOf(this._endp), e);
                    this._endp.close();
                    throw e;
                }
                finally {
                    out_buffer.position(0);
                    out_buffer.limit(out_buffer.capacity());
                    bbuf.position(0);
                    bbuf.limit(bbuf.capacity());
                }
            }
        }
        switch (result.getStatus()) {
            case BUFFER_UNDERFLOW: {
                throw new IllegalStateException();
            }
            case BUFFER_OVERFLOW: {
                break;
            }
            case OK: {
                if (result.getHandshakeStatus() == SSLEngineResult.HandshakeStatus.FINISHED) {
                    this._handshook = true;
                    break;
                }
                break;
            }
            case CLOSED: {
                this._logger.debug("wrap CLOSE {} {}", this, result);
                if (result.getHandshakeStatus() == SSLEngineResult.HandshakeStatus.FINISHED) {
                    this._endp.close();
                    break;
                }
                break;
            }
            default: {
                this._logger.debug("{} wrap default {}", this._session, result);
                throw new IOException(result.toString());
            }
        }
        return result.bytesConsumed() > 0 || result.bytesProduced() > 0;
    }
    
    private synchronized boolean unwrap(final Buffer buffer) throws IOException {
        if (!this._inbound.hasContent()) {
            return false;
        }
        final ByteBuffer bbuf = this.extractByteBuffer(buffer);
        SSLEngineResult result;
        synchronized (bbuf) {
            final ByteBuffer in_buffer = this._inbound.getByteBuffer();
            synchronized (in_buffer) {
                try {
                    bbuf.position(buffer.putIndex());
                    bbuf.limit(buffer.capacity());
                    in_buffer.position(this._inbound.getIndex());
                    in_buffer.limit(this._inbound.putIndex());
                    result = this._engine.unwrap(in_buffer, bbuf);
                    if (this._logger.isDebugEnabled()) {
                        this._logger.debug("{} unwrap {} {} consumed={} produced={}", this._session, result.getStatus(), result.getHandshakeStatus(), result.bytesConsumed(), result.bytesProduced());
                    }
                    this._inbound.skip(result.bytesConsumed());
                    this._inbound.compact();
                    buffer.setPutIndex(buffer.putIndex() + result.bytesProduced());
                }
                catch (SSLException e) {
                    this._logger.debug(String.valueOf(this._endp), e);
                    this._endp.close();
                    throw e;
                }
                finally {
                    in_buffer.position(0);
                    in_buffer.limit(in_buffer.capacity());
                    bbuf.position(0);
                    bbuf.limit(bbuf.capacity());
                }
            }
        }
        switch (result.getStatus()) {
            case BUFFER_UNDERFLOW: {
                if (this._endp.isInputShutdown()) {
                    this._inbound.clear();
                    break;
                }
                break;
            }
            case BUFFER_OVERFLOW: {
                this._logger.debug("{} unwrap {} {}->{}", this._session, result.getStatus(), this._inbound.toDetailString(), buffer.toDetailString());
                break;
            }
            case OK: {
                if (result.getHandshakeStatus() == SSLEngineResult.HandshakeStatus.FINISHED) {
                    this._handshook = true;
                    break;
                }
                break;
            }
            case CLOSED: {
                this._logger.debug("unwrap CLOSE {} {}", this, result);
                if (result.getHandshakeStatus() == SSLEngineResult.HandshakeStatus.FINISHED) {
                    this._endp.close();
                    break;
                }
                break;
            }
            default: {
                this._logger.debug("{} wrap default {}", this._session, result);
                throw new IOException(result.toString());
            }
        }
        return result.bytesConsumed() > 0 || result.bytesProduced() > 0;
    }
    
    private ByteBuffer extractByteBuffer(final Buffer buffer) {
        if (buffer.buffer() instanceof NIOBuffer) {
            return ((NIOBuffer)buffer.buffer()).getByteBuffer();
        }
        return ByteBuffer.wrap(buffer.array());
    }
    
    public AsyncEndPoint getSslEndPoint() {
        return this._sslEndPoint;
    }
    
    @Override
    public String toString() {
        return String.format("%s %s", super.toString(), this._sslEndPoint);
    }
    
    static {
        __ZERO_BUFFER = new IndirectNIOBuffer(0);
        __buffers = new ThreadLocal<SslBuffers>();
    }
    
    private static class SslBuffers
    {
        final NIOBuffer _in;
        final NIOBuffer _out;
        final NIOBuffer _unwrap;
        
        SslBuffers(final int packetSize, final int appSize) {
            this._in = new IndirectNIOBuffer(packetSize);
            this._out = new IndirectNIOBuffer(packetSize);
            this._unwrap = new IndirectNIOBuffer(appSize);
        }
    }
    
    public class SslEndPoint implements AsyncEndPoint
    {
        public SSLEngine getSslEngine() {
            return SslConnection.this._engine;
        }
        
        public AsyncEndPoint getEndpoint() {
            return SslConnection.this._aEndp;
        }
        
        public void shutdownOutput() throws IOException {
            synchronized (SslConnection.this) {
                SslConnection.this._logger.debug("{} ssl endp.oshut {}", SslConnection.this._session, this);
                SslConnection.this._engine.closeOutbound();
                SslConnection.this._oshut = true;
            }
            this.flush();
        }
        
        public boolean isOutputShutdown() {
            synchronized (SslConnection.this) {
                return SslConnection.this._oshut || !this.isOpen() || SslConnection.this._engine.isOutboundDone();
            }
        }
        
        public void shutdownInput() throws IOException {
            SslConnection.this._logger.debug("{} ssl endp.ishut!", SslConnection.this._session);
        }
        
        public boolean isInputShutdown() {
            synchronized (SslConnection.this) {
                return SslConnection.this._endp.isInputShutdown() && (SslConnection.this._unwrapBuf == null || !SslConnection.this._unwrapBuf.hasContent()) && (SslConnection.this._inbound == null || !SslConnection.this._inbound.hasContent());
            }
        }
        
        public void close() throws IOException {
            SslConnection.this._logger.debug("{} ssl endp.close", SslConnection.this._session);
            SslConnection.this._endp.close();
        }
        
        public int fill(final Buffer buffer) throws IOException {
            final int size = buffer.length();
            SslConnection.this.process(buffer, null);
            final int filled = buffer.length() - size;
            if (filled == 0 && this.isInputShutdown()) {
                return -1;
            }
            return filled;
        }
        
        public int flush(final Buffer buffer) throws IOException {
            final int size = buffer.length();
            SslConnection.this.process(null, buffer);
            return size - buffer.length();
        }
        
        public int flush(final Buffer header, final Buffer buffer, final Buffer trailer) throws IOException {
            if (header != null && header.hasContent()) {
                return this.flush(header);
            }
            if (buffer != null && buffer.hasContent()) {
                return this.flush(buffer);
            }
            if (trailer != null && trailer.hasContent()) {
                return this.flush(trailer);
            }
            return 0;
        }
        
        public boolean blockReadable(final long millisecs) throws IOException {
            long now;
            long end;
            for (now = System.currentTimeMillis(), end = ((millisecs > 0L) ? (now + millisecs) : Long.MAX_VALUE); now < end && !SslConnection.this.process(null, null); now = System.currentTimeMillis()) {
                SslConnection.this._endp.blockReadable(end - now);
            }
            return now < end;
        }
        
        public boolean blockWritable(final long millisecs) throws IOException {
            return SslConnection.this._endp.blockWritable(millisecs);
        }
        
        public boolean isOpen() {
            return SslConnection.this._endp.isOpen();
        }
        
        public Object getTransport() {
            return SslConnection.this._endp;
        }
        
        public void flush() throws IOException {
            SslConnection.this.process(null, null);
        }
        
        public void asyncDispatch() {
            SslConnection.this._aEndp.asyncDispatch();
        }
        
        public void scheduleWrite() {
            SslConnection.this._aEndp.scheduleWrite();
        }
        
        public void onIdleExpired(final long idleForMs) {
            SslConnection.this._aEndp.onIdleExpired(idleForMs);
        }
        
        public void setCheckForIdle(final boolean check) {
            SslConnection.this._aEndp.setCheckForIdle(check);
        }
        
        public boolean isCheckForIdle() {
            return SslConnection.this._aEndp.isCheckForIdle();
        }
        
        public void scheduleTimeout(final Timeout.Task task, final long timeoutMs) {
            SslConnection.this._aEndp.scheduleTimeout(task, timeoutMs);
        }
        
        public void cancelTimeout(final Timeout.Task task) {
            SslConnection.this._aEndp.cancelTimeout(task);
        }
        
        public boolean isWritable() {
            return SslConnection.this._aEndp.isWritable();
        }
        
        public boolean hasProgressed() {
            return SslConnection.this._progressed.getAndSet(false);
        }
        
        public String getLocalAddr() {
            return SslConnection.this._aEndp.getLocalAddr();
        }
        
        public String getLocalHost() {
            return SslConnection.this._aEndp.getLocalHost();
        }
        
        public int getLocalPort() {
            return SslConnection.this._aEndp.getLocalPort();
        }
        
        public String getRemoteAddr() {
            return SslConnection.this._aEndp.getRemoteAddr();
        }
        
        public String getRemoteHost() {
            return SslConnection.this._aEndp.getRemoteHost();
        }
        
        public int getRemotePort() {
            return SslConnection.this._aEndp.getRemotePort();
        }
        
        public boolean isBlocking() {
            return false;
        }
        
        public int getMaxIdleTime() {
            return SslConnection.this._aEndp.getMaxIdleTime();
        }
        
        public void setMaxIdleTime(final int timeMs) throws IOException {
            SslConnection.this._aEndp.setMaxIdleTime(timeMs);
        }
        
        public Connection getConnection() {
            return SslConnection.this._connection;
        }
        
        public void setConnection(final Connection connection) {
            SslConnection.this._connection = (AsyncConnection)connection;
        }
        
        @Override
        public String toString() {
            final Buffer inbound = SslConnection.this._inbound;
            final Buffer outbound = SslConnection.this._outbound;
            final Buffer unwrap = SslConnection.this._unwrapBuf;
            final int i = (inbound == null) ? -1 : inbound.length();
            final int o = (outbound == null) ? -1 : outbound.length();
            final int u = (unwrap == null) ? -1 : unwrap.length();
            return String.format("SSL %s i/o/u=%d/%d/%d ishut=%b oshut=%b {%s}", SslConnection.this._engine.getHandshakeStatus(), i, o, u, SslConnection.this._ishut, SslConnection.this._oshut, SslConnection.this._connection);
        }
    }
}
