// 
// Decompiled by Procyon v0.5.36
// 

package org.eclipse.jetty.server;

import org.eclipse.jetty.util.IteratingNestedCallback;
import org.eclipse.jetty.util.IteratingCallback;
import org.eclipse.jetty.util.log.Log;
import java.io.Closeable;
import org.eclipse.jetty.http.HttpContent;
import java.nio.channels.ReadableByteChannel;
import java.io.InputStream;
import java.nio.channels.WritePendingException;
import org.eclipse.jetty.util.BufferUtil;
import org.eclipse.jetty.io.EofException;
import org.eclipse.jetty.util.Callback;
import java.io.IOException;
import java.util.concurrent.atomic.AtomicReference;
import javax.servlet.WriteListener;
import java.nio.ByteBuffer;
import org.eclipse.jetty.util.SharedBlockingCallback;
import org.eclipse.jetty.util.log.Logger;
import javax.servlet.ServletOutputStream;

public class HttpOutput extends ServletOutputStream implements Runnable
{
    private static Logger LOG;
    private final HttpChannel _channel;
    private final SharedBlockingCallback _writeBlocker;
    private Interceptor _interceptor;
    private long _written;
    private ByteBuffer _aggregate;
    private int _bufferSize;
    private int _commitSize;
    private WriteListener _writeListener;
    private volatile Throwable _onError;
    private final AtomicReference<OutputState> _state;
    
    public HttpOutput(final HttpChannel channel) {
        this._state = new AtomicReference<OutputState>(OutputState.OPEN);
        this._channel = channel;
        this._interceptor = channel;
        this._writeBlocker = new WriteBlocker(channel);
        final HttpConfiguration config = channel.getHttpConfiguration();
        this._bufferSize = config.getOutputBufferSize();
        this._commitSize = config.getOutputAggregationSize();
        if (this._commitSize > this._bufferSize) {
            HttpOutput.LOG.warn("OutputAggregationSize {} exceeds bufferSize {}", this._commitSize, this._bufferSize);
            this._commitSize = this._bufferSize;
        }
    }
    
    public HttpChannel getHttpChannel() {
        return this._channel;
    }
    
    public Interceptor getInterceptor() {
        return this._interceptor;
    }
    
    public void setInterceptor(final Interceptor filter) {
        this._interceptor = filter;
    }
    
    public boolean isWritten() {
        return this._written > 0L;
    }
    
    public long getWritten() {
        return this._written;
    }
    
    public void reopen() {
        this._state.set(OutputState.OPEN);
    }
    
    private boolean isLastContentToWrite(final int len) {
        this._written += len;
        return this._channel.getResponse().isAllContentWritten(this._written);
    }
    
    public boolean isAllContentWritten() {
        return this._channel.getResponse().isAllContentWritten(this._written);
    }
    
    protected SharedBlockingCallback.Blocker acquireWriteBlockingCallback() throws IOException {
        return this._writeBlocker.acquire();
    }
    
    private void write(final ByteBuffer content, final boolean complete) throws IOException {
        try {
            final SharedBlockingCallback.Blocker blocker = this._writeBlocker.acquire();
            Throwable x0 = null;
            try {
                this.write(content, complete, blocker);
                blocker.block();
            }
            catch (Throwable t) {
                x0 = t;
                throw t;
            }
            finally {
                if (blocker != null) {
                    $closeResource(x0, blocker);
                }
            }
        }
        catch (Exception failure) {
            if (HttpOutput.LOG.isDebugEnabled()) {
                HttpOutput.LOG.debug(failure);
            }
            this.abort(failure);
            if (failure instanceof IOException) {
                throw failure;
            }
            throw new IOException(failure);
        }
    }
    
    protected void write(final ByteBuffer content, final boolean complete, final Callback callback) {
        this._interceptor.write(content, complete, callback);
    }
    
    private void abort(final Throwable failure) {
        this.closed();
        this._channel.abort(failure);
    }
    
    @Override
    public void close() {
        while (true) {
            final OutputState state = this._state.get();
            switch (state) {
                case CLOSED: {}
                case UNREADY: {
                    if (this._state.compareAndSet(state, OutputState.ERROR)) {
                        this._writeListener.onError((this._onError == null) ? new EofException("Async close") : this._onError);
                        continue;
                    }
                    continue;
                }
                default: {
                    if (!this._state.compareAndSet(state, OutputState.CLOSED)) {
                        continue;
                    }
                    try {
                        this.write(BufferUtil.hasContent(this._aggregate) ? this._aggregate : BufferUtil.EMPTY_BUFFER, !this._channel.getResponse().isIncluding());
                    }
                    catch (IOException ex) {}
                    finally {
                        this.releaseBuffer();
                    }
                }
            }
        }
    }
    
    void closed() {
        while (true) {
            final OutputState state = this._state.get();
            switch (state) {
                case CLOSED: {}
                case UNREADY: {
                    if (this._state.compareAndSet(state, OutputState.ERROR)) {
                        this._writeListener.onError((this._onError == null) ? new EofException("Async closed") : this._onError);
                        continue;
                    }
                    continue;
                }
                default: {
                    if (!this._state.compareAndSet(state, OutputState.CLOSED)) {
                        continue;
                    }
                    try {
                        this._channel.getResponse().closeOutput();
                    }
                    catch (Throwable x) {
                        if (HttpOutput.LOG.isDebugEnabled()) {
                            HttpOutput.LOG.debug(x);
                        }
                        this.abort(x);
                    }
                    finally {
                        this.releaseBuffer();
                    }
                }
            }
        }
    }
    
    private void releaseBuffer() {
        if (this._aggregate != null) {
            this._channel.getConnector().getByteBufferPool().release(this._aggregate);
            this._aggregate = null;
        }
    }
    
    public boolean isClosed() {
        return this._state.get() == OutputState.CLOSED;
    }
    
    @Override
    public void flush() throws IOException {
        while (true) {
            switch (this._state.get()) {
                case OPEN: {
                    this.write(BufferUtil.hasContent(this._aggregate) ? this._aggregate : BufferUtil.EMPTY_BUFFER, false);
                }
                case ASYNC: {
                    throw new IllegalStateException("isReady() not called");
                }
                case READY: {
                    if (!this._state.compareAndSet(OutputState.READY, OutputState.PENDING)) {
                        continue;
                    }
                    new AsyncFlush().iterate();
                }
                case UNREADY:
                case PENDING: {
                    throw new WritePendingException();
                }
                case ERROR: {
                    throw new EofException(this._onError);
                }
                case CLOSED: {}
                default: {
                    throw new IllegalStateException();
                }
            }
        }
    }
    
    @Override
    public void write(final byte[] b, int off, int len) throws IOException {
        while (true) {
            switch (this._state.get()) {
                case OPEN: {
                    final int capacity = this.getBufferSize();
                    final boolean last = this.isLastContentToWrite(len);
                    if (!last && len <= this._commitSize) {
                        if (this._aggregate == null) {
                            this._aggregate = this._channel.getByteBufferPool().acquire(capacity, this._interceptor.isOptimizedForDirectBuffers());
                        }
                        final int filled = BufferUtil.fill(this._aggregate, b, off, len);
                        if (filled == len && !BufferUtil.isFull(this._aggregate)) {
                            return;
                        }
                        off += filled;
                        len -= filled;
                    }
                    if (BufferUtil.hasContent(this._aggregate)) {
                        this.write(this._aggregate, last && len == 0);
                        if (len > 0 && !last && len <= this._commitSize && len <= BufferUtil.space(this._aggregate)) {
                            BufferUtil.append(this._aggregate, b, off, len);
                            return;
                        }
                    }
                    if (len > 0) {
                        final ByteBuffer view = ByteBuffer.wrap(b, off, len);
                        while (len > this.getBufferSize()) {
                            final int p = view.position();
                            final int l = p + this.getBufferSize();
                            view.limit(p + this.getBufferSize());
                            this.write(view, false);
                            len -= this.getBufferSize();
                            view.limit(l + Math.min(len, this.getBufferSize()));
                            view.position(l);
                        }
                        this.write(view, last);
                    }
                    else if (last) {
                        this.write(BufferUtil.EMPTY_BUFFER, true);
                    }
                    if (last) {
                        this.closed();
                    }
                }
                case ASYNC: {
                    throw new IllegalStateException("isReady() not called");
                }
                case READY: {
                    if (!this._state.compareAndSet(OutputState.READY, OutputState.PENDING)) {
                        continue;
                    }
                    final boolean last2 = this.isLastContentToWrite(len);
                    if (!last2 && len <= this._commitSize) {
                        if (this._aggregate == null) {
                            this._aggregate = this._channel.getByteBufferPool().acquire(this.getBufferSize(), this._interceptor.isOptimizedForDirectBuffers());
                        }
                        final int filled2 = BufferUtil.fill(this._aggregate, b, off, len);
                        if (filled2 == len && !BufferUtil.isFull(this._aggregate)) {
                            if (!this._state.compareAndSet(OutputState.PENDING, OutputState.ASYNC)) {
                                throw new IllegalStateException();
                            }
                            return;
                        }
                        else {
                            off += filled2;
                            len -= filled2;
                        }
                    }
                    new AsyncWrite(b, off, len, last2).iterate();
                }
                case UNREADY:
                case PENDING: {
                    throw new WritePendingException();
                }
                case ERROR: {
                    throw new EofException(this._onError);
                }
                case CLOSED: {
                    throw new EofException("Closed");
                }
                default: {
                    throw new IllegalStateException();
                }
            }
        }
    }
    
    public void write(final ByteBuffer buffer) throws IOException {
        while (true) {
            switch (this._state.get()) {
                case OPEN: {
                    final int len = BufferUtil.length(buffer);
                    final boolean last = this.isLastContentToWrite(len);
                    if (BufferUtil.hasContent(this._aggregate)) {
                        this.write(this._aggregate, last && len == 0);
                    }
                    if (len > 0) {
                        this.write(buffer, last);
                    }
                    else if (last) {
                        this.write(BufferUtil.EMPTY_BUFFER, true);
                    }
                    if (last) {
                        this.closed();
                    }
                }
                case ASYNC: {
                    throw new IllegalStateException("isReady() not called");
                }
                case READY: {
                    if (!this._state.compareAndSet(OutputState.READY, OutputState.PENDING)) {
                        continue;
                    }
                    final boolean last2 = this.isLastContentToWrite(buffer.remaining());
                    new AsyncWrite(buffer, last2).iterate();
                }
                case UNREADY:
                case PENDING: {
                    throw new WritePendingException();
                }
                case ERROR: {
                    throw new EofException(this._onError);
                }
                case CLOSED: {
                    throw new EofException("Closed");
                }
                default: {
                    throw new IllegalStateException();
                }
            }
        }
    }
    
    @Override
    public void write(final int b) throws IOException {
        ++this._written;
        final boolean complete = this._channel.getResponse().isAllContentWritten(this._written);
        while (true) {
            switch (this._state.get()) {
                case OPEN: {
                    if (this._aggregate == null) {
                        this._aggregate = this._channel.getByteBufferPool().acquire(this.getBufferSize(), this._interceptor.isOptimizedForDirectBuffers());
                    }
                    BufferUtil.append(this._aggregate, (byte)b);
                    if (complete || BufferUtil.isFull(this._aggregate)) {
                        this.write(this._aggregate, complete);
                        if (complete) {
                            this.closed();
                        }
                    }
                }
                case ASYNC: {
                    throw new IllegalStateException("isReady() not called");
                }
                case READY: {
                    if (!this._state.compareAndSet(OutputState.READY, OutputState.PENDING)) {
                        continue;
                    }
                    if (this._aggregate == null) {
                        this._aggregate = this._channel.getByteBufferPool().acquire(this.getBufferSize(), this._interceptor.isOptimizedForDirectBuffers());
                    }
                    BufferUtil.append(this._aggregate, (byte)b);
                    if (complete || BufferUtil.isFull(this._aggregate)) {
                        new AsyncFlush().iterate();
                        return;
                    }
                    if (!this._state.compareAndSet(OutputState.PENDING, OutputState.ASYNC)) {
                        throw new IllegalStateException();
                    }
                }
                case UNREADY:
                case PENDING: {
                    throw new WritePendingException();
                }
                case ERROR: {
                    throw new EofException(this._onError);
                }
                case CLOSED: {
                    throw new EofException("Closed");
                }
                default: {
                    throw new IllegalStateException();
                }
            }
        }
    }
    
    @Override
    public void print(final String s) throws IOException {
        if (this.isClosed()) {
            throw new IOException("Closed");
        }
        this.write(s.getBytes(this._channel.getResponse().getCharacterEncoding()));
    }
    
    public void sendContent(final ByteBuffer content) throws IOException {
        if (HttpOutput.LOG.isDebugEnabled()) {
            HttpOutput.LOG.debug("sendContent({})", BufferUtil.toDetailString(content));
        }
        this.write(content, true);
        this.closed();
    }
    
    public void sendContent(final InputStream in) throws IOException {
        try {
            final SharedBlockingCallback.Blocker blocker = this._writeBlocker.acquire();
            Throwable x0 = null;
            try {
                new InputStreamWritingCB(in, blocker).iterate();
                blocker.block();
            }
            catch (Throwable t) {
                x0 = t;
                throw t;
            }
            finally {
                if (blocker != null) {
                    $closeResource(x0, blocker);
                }
            }
        }
        catch (Throwable failure) {
            if (HttpOutput.LOG.isDebugEnabled()) {
                HttpOutput.LOG.debug(failure);
            }
            this.abort(failure);
            throw failure;
        }
    }
    
    public void sendContent(final ReadableByteChannel in) throws IOException {
        try {
            final SharedBlockingCallback.Blocker blocker = this._writeBlocker.acquire();
            Throwable x0 = null;
            try {
                new ReadableByteChannelWritingCB(in, blocker).iterate();
                blocker.block();
            }
            catch (Throwable t) {
                x0 = t;
                throw t;
            }
            finally {
                if (blocker != null) {
                    $closeResource(x0, blocker);
                }
            }
        }
        catch (Throwable failure) {
            if (HttpOutput.LOG.isDebugEnabled()) {
                HttpOutput.LOG.debug(failure);
            }
            this.abort(failure);
            throw failure;
        }
    }
    
    public void sendContent(final HttpContent content) throws IOException {
        try {
            final SharedBlockingCallback.Blocker blocker = this._writeBlocker.acquire();
            Throwable x0 = null;
            try {
                this.sendContent(content, blocker);
                blocker.block();
            }
            catch (Throwable t) {
                x0 = t;
                throw t;
            }
            finally {
                if (blocker != null) {
                    $closeResource(x0, blocker);
                }
            }
        }
        catch (Throwable failure) {
            if (HttpOutput.LOG.isDebugEnabled()) {
                HttpOutput.LOG.debug(failure);
            }
            this.abort(failure);
            throw failure;
        }
    }
    
    public void sendContent(final ByteBuffer content, final Callback callback) {
        if (HttpOutput.LOG.isDebugEnabled()) {
            HttpOutput.LOG.debug("sendContent(buffer={},{})", BufferUtil.toDetailString(content), callback);
        }
        this.write(content, true, new Callback.Nested(callback) {
            @Override
            public void succeeded() {
                HttpOutput.this.closed();
                super.succeeded();
            }
            
            @Override
            public void failed(final Throwable x) {
                HttpOutput.this.abort(x);
                super.failed(x);
            }
        });
    }
    
    public void sendContent(final InputStream in, final Callback callback) {
        if (HttpOutput.LOG.isDebugEnabled()) {
            HttpOutput.LOG.debug("sendContent(stream={},{})", in, callback);
        }
        new InputStreamWritingCB(in, callback).iterate();
    }
    
    public void sendContent(final ReadableByteChannel in, final Callback callback) {
        if (HttpOutput.LOG.isDebugEnabled()) {
            HttpOutput.LOG.debug("sendContent(channel={},{})", in, callback);
        }
        new ReadableByteChannelWritingCB(in, callback).iterate();
    }
    
    public void sendContent(final HttpContent httpContent, final Callback callback) {
        if (HttpOutput.LOG.isDebugEnabled()) {
            HttpOutput.LOG.debug("sendContent(http={},{})", httpContent, callback);
        }
        if (BufferUtil.hasContent(this._aggregate)) {
            callback.failed(new IOException("cannot sendContent() after write()"));
            return;
        }
        if (this._channel.isCommitted()) {
            callback.failed(new IOException("cannot sendContent(), output already committed"));
            return;
        }
        while (true) {
            switch (this._state.get()) {
                case OPEN: {
                    if (!this._state.compareAndSet(OutputState.OPEN, OutputState.PENDING)) {
                        continue;
                    }
                    ByteBuffer buffer = this._channel.useDirectBuffers() ? httpContent.getDirectBuffer() : null;
                    if (buffer == null) {
                        buffer = httpContent.getIndirectBuffer();
                    }
                    if (buffer != null) {
                        this.sendContent(buffer, callback);
                        return;
                    }
                    try {
                        final ReadableByteChannel rbc = httpContent.getReadableByteChannel();
                        if (rbc != null) {
                            this.sendContent(rbc, callback);
                            return;
                        }
                        final InputStream in = httpContent.getInputStream();
                        if (in != null) {
                            this.sendContent(in, callback);
                            return;
                        }
                        throw new IllegalArgumentException("unknown content for " + httpContent);
                    }
                    catch (Throwable th) {
                        this.abort(th);
                        callback.failed(th);
                        return;
                    }
                    break;
                }
                case ERROR: {
                    callback.failed(new EofException(this._onError));
                }
                case CLOSED: {
                    callback.failed(new EofException("Closed"));
                }
                default: {
                    throw new IllegalStateException();
                }
            }
        }
    }
    
    public int getBufferSize() {
        return this._bufferSize;
    }
    
    public void setBufferSize(final int size) {
        this._bufferSize = size;
        this._commitSize = size;
    }
    
    public void recycle() {
        this._interceptor = this._channel;
        final HttpConfiguration config = this._channel.getHttpConfiguration();
        this._bufferSize = config.getOutputBufferSize();
        this._commitSize = config.getOutputAggregationSize();
        if (this._commitSize > this._bufferSize) {
            this._commitSize = this._bufferSize;
        }
        this.releaseBuffer();
        this._written = 0L;
        this._writeListener = null;
        this._onError = null;
        this.reopen();
    }
    
    public void resetBuffer() {
        this._interceptor.resetBuffer();
        if (BufferUtil.hasContent(this._aggregate)) {
            BufferUtil.clear(this._aggregate);
        }
        this._written = 0L;
        this.reopen();
    }
    
    @Override
    public void setWriteListener(final WriteListener writeListener) {
        if (!this._channel.getState().isAsync()) {
            throw new IllegalStateException("!ASYNC");
        }
        if (this._state.compareAndSet(OutputState.OPEN, OutputState.READY)) {
            this._writeListener = writeListener;
            if (this._channel.getState().onWritePossible()) {
                this._channel.execute(this._channel);
            }
            return;
        }
        throw new IllegalStateException();
    }
    
    @Override
    public boolean isReady() {
        while (true) {
            switch (this._state.get()) {
                case OPEN: {
                    return true;
                }
                case ASYNC: {
                    if (!this._state.compareAndSet(OutputState.ASYNC, OutputState.READY)) {
                        continue;
                    }
                    return true;
                }
                case READY: {
                    return true;
                }
                case PENDING: {
                    if (!this._state.compareAndSet(OutputState.PENDING, OutputState.UNREADY)) {
                        continue;
                    }
                    return false;
                }
                case UNREADY: {
                    return false;
                }
                case ERROR: {
                    return true;
                }
                case CLOSED: {
                    return true;
                }
                default: {
                    throw new IllegalStateException();
                }
            }
        }
    }
    
    @Override
    public void run() {
    Label_0240:
        while (true) {
            final OutputState state = this._state.get();
            if (this._onError != null) {
                switch (state) {
                    case CLOSED:
                    case ERROR: {
                        this._onError = null;
                        break Label_0240;
                    }
                    default: {
                        if (this._state.compareAndSet(state, OutputState.ERROR)) {
                            final Throwable th = this._onError;
                            this._onError = null;
                            if (HttpOutput.LOG.isDebugEnabled()) {
                                HttpOutput.LOG.debug("onError", th);
                            }
                            this._writeListener.onError(th);
                            this.close();
                            break Label_0240;
                        }
                        continue;
                    }
                }
            }
            else {
                switch (this._state.get()) {
                    case CLOSED:
                    case UNREADY:
                    case ASYNC:
                    case READY:
                    case PENDING: {
                        try {
                            this._writeListener.onWritePossible();
                            break Label_0240;
                        }
                        catch (Throwable e) {
                            this._onError = e;
                            continue;
                        }
                        break;
                    }
                }
                this._onError = new IllegalStateException("state=" + this._state.get());
            }
        }
    }
    
    private void close(final Closeable resource) {
        try {
            resource.close();
        }
        catch (Throwable x) {
            HttpOutput.LOG.ignore(x);
        }
    }
    
    @Override
    public String toString() {
        return String.format("%s@%x{%s}", this.getClass().getSimpleName(), this.hashCode(), this._state.get());
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
        HttpOutput.LOG = Log.getLogger(HttpOutput.class);
    }
    
    public interface Interceptor
    {
        void write(final ByteBuffer p0, final boolean p1, final Callback p2);
        
        Interceptor getNextInterceptor();
        
        boolean isOptimizedForDirectBuffers();
        
        default void resetBuffer() throws IllegalStateException {
            final Interceptor next = this.getNextInterceptor();
            if (next != null) {
                next.resetBuffer();
            }
        }
    }
    
    private enum OutputState
    {
        OPEN, 
        ASYNC, 
        READY, 
        PENDING, 
        UNREADY, 
        ERROR, 
        CLOSED;
    }
    
    private abstract class AsyncICB extends IteratingCallback
    {
        final boolean _last;
        
        AsyncICB(final boolean last) {
            this._last = last;
        }
        
        @Override
        protected void onCompleteSuccess() {
        Label_0161:
            while (true) {
                final OutputState last = HttpOutput.this._state.get();
                switch (last) {
                    case PENDING: {
                        if (!HttpOutput.this._state.compareAndSet(OutputState.PENDING, OutputState.ASYNC)) {
                            continue;
                        }
                        break Label_0161;
                    }
                    case UNREADY: {
                        if (!HttpOutput.this._state.compareAndSet(OutputState.UNREADY, OutputState.READY)) {
                            continue;
                        }
                        if (this._last) {
                            HttpOutput.this.closed();
                        }
                        if (HttpOutput.this._channel.getState().onWritePossible()) {
                            HttpOutput.this._channel.execute(HttpOutput.this._channel);
                            break Label_0161;
                        }
                        break Label_0161;
                    }
                    case CLOSED: {
                        break Label_0161;
                    }
                    default: {
                        throw new IllegalStateException();
                    }
                }
            }
        }
        
        public void onCompleteFailure(final Throwable e) {
            HttpOutput.this._onError = ((e == null) ? new IOException() : e);
            if (HttpOutput.this._channel.getState().onWritePossible()) {
                HttpOutput.this._channel.execute(HttpOutput.this._channel);
            }
        }
    }
    
    private class AsyncFlush extends AsyncICB
    {
        protected volatile boolean _flushed;
        
        public AsyncFlush() {
            super(false);
        }
        
        @Override
        protected Action process() {
            if (BufferUtil.hasContent(HttpOutput.this._aggregate)) {
                this._flushed = true;
                HttpOutput.this.write(HttpOutput.this._aggregate, false, this);
                return Action.SCHEDULED;
            }
            if (!this._flushed) {
                this._flushed = true;
                HttpOutput.this.write(BufferUtil.EMPTY_BUFFER, false, this);
                return Action.SCHEDULED;
            }
            return Action.SUCCEEDED;
        }
    }
    
    private class AsyncWrite extends AsyncICB
    {
        private final ByteBuffer _buffer;
        private final ByteBuffer _slice;
        private final int _len;
        protected volatile boolean _completed;
        
        public AsyncWrite(final byte[] b, final int off, final int len, final boolean last) {
            super(last);
            this._buffer = ByteBuffer.wrap(b, off, len);
            this._len = len;
            this._slice = ((this._len < HttpOutput.this.getBufferSize()) ? null : this._buffer.duplicate());
        }
        
        public AsyncWrite(final ByteBuffer buffer, final boolean last) {
            super(last);
            this._buffer = buffer;
            this._len = buffer.remaining();
            if (this._buffer.isDirect() || this._len < HttpOutput.this.getBufferSize()) {
                this._slice = null;
            }
            else {
                this._slice = this._buffer.duplicate();
            }
        }
        
        @Override
        protected Action process() {
            if (BufferUtil.hasContent(HttpOutput.this._aggregate)) {
                this._completed = (this._len == 0);
                HttpOutput.this.write(HttpOutput.this._aggregate, this._last && this._completed, this);
                return Action.SCHEDULED;
            }
            if (!this._last && this._len < BufferUtil.space(HttpOutput.this._aggregate) && this._len < HttpOutput.this._commitSize) {
                final int position = BufferUtil.flipToFill(HttpOutput.this._aggregate);
                BufferUtil.put(this._buffer, HttpOutput.this._aggregate);
                BufferUtil.flipToFlush(HttpOutput.this._aggregate, position);
                return Action.SUCCEEDED;
            }
            if (this._buffer.hasRemaining()) {
                if (this._slice == null) {
                    this._completed = true;
                    HttpOutput.this.write(this._buffer, this._last, this);
                    return Action.SCHEDULED;
                }
                final int p = this._buffer.position();
                final int l = Math.min(HttpOutput.this.getBufferSize(), this._buffer.remaining());
                final int pl = p + l;
                this._slice.limit(pl);
                this._buffer.position(pl);
                this._slice.position(p);
                this._completed = !this._buffer.hasRemaining();
                HttpOutput.this.write(this._slice, this._last && this._completed, this);
                return Action.SCHEDULED;
            }
            else {
                if (this._last && !this._completed) {
                    this._completed = true;
                    HttpOutput.this.write(BufferUtil.EMPTY_BUFFER, true, this);
                    return Action.SCHEDULED;
                }
                if (HttpOutput.LOG.isDebugEnabled() && this._completed) {
                    HttpOutput.LOG.debug("EOF of {}", this);
                }
                return Action.SUCCEEDED;
            }
        }
    }
    
    private class InputStreamWritingCB extends IteratingNestedCallback
    {
        private final InputStream _in;
        private final ByteBuffer _buffer;
        private boolean _eof;
        
        public InputStreamWritingCB(final InputStream in, final Callback callback) {
            super(callback);
            this._in = in;
            this._buffer = HttpOutput.this._channel.getByteBufferPool().acquire(HttpOutput.this.getBufferSize(), false);
        }
        
        @Override
        protected Action process() throws Exception {
            if (this._eof) {
                if (HttpOutput.LOG.isDebugEnabled()) {
                    HttpOutput.LOG.debug("EOF of {}", this);
                }
                this._in.close();
                HttpOutput.this.closed();
                HttpOutput.this._channel.getByteBufferPool().release(this._buffer);
                return Action.SUCCEEDED;
            }
            int len = 0;
            while (len < this._buffer.capacity() && !this._eof) {
                final int r = this._in.read(this._buffer.array(), this._buffer.arrayOffset() + len, this._buffer.capacity() - len);
                if (r < 0) {
                    this._eof = true;
                }
                else {
                    len += r;
                }
            }
            this._buffer.position(0);
            this._buffer.limit(len);
            HttpOutput.this.write(this._buffer, this._eof, this);
            return Action.SCHEDULED;
        }
        
        public void onCompleteFailure(final Throwable x) {
            HttpOutput.this.abort(x);
            HttpOutput.this._channel.getByteBufferPool().release(this._buffer);
            HttpOutput.this.close(this._in);
            super.onCompleteFailure(x);
        }
    }
    
    private class ReadableByteChannelWritingCB extends IteratingNestedCallback
    {
        private final ReadableByteChannel _in;
        private final ByteBuffer _buffer;
        private boolean _eof;
        
        public ReadableByteChannelWritingCB(final ReadableByteChannel in, final Callback callback) {
            super(callback);
            this._in = in;
            this._buffer = HttpOutput.this._channel.getByteBufferPool().acquire(HttpOutput.this.getBufferSize(), HttpOutput.this._channel.useDirectBuffers());
        }
        
        @Override
        protected Action process() throws Exception {
            if (this._eof) {
                if (HttpOutput.LOG.isDebugEnabled()) {
                    HttpOutput.LOG.debug("EOF of {}", this);
                }
                this._in.close();
                HttpOutput.this.closed();
                HttpOutput.this._channel.getByteBufferPool().release(this._buffer);
                return Action.SUCCEEDED;
            }
            BufferUtil.clearToFill(this._buffer);
            while (this._buffer.hasRemaining() && !this._eof) {
                this._eof = (this._in.read(this._buffer) < 0);
            }
            BufferUtil.flipToFlush(this._buffer, 0);
            HttpOutput.this.write(this._buffer, this._eof, this);
            return Action.SCHEDULED;
        }
        
        public void onCompleteFailure(final Throwable x) {
            HttpOutput.this.abort(x);
            HttpOutput.this._channel.getByteBufferPool().release(this._buffer);
            HttpOutput.this.close(this._in);
            super.onCompleteFailure(x);
        }
    }
    
    private static class WriteBlocker extends SharedBlockingCallback
    {
        private final HttpChannel _channel;
        
        private WriteBlocker(final HttpChannel channel) {
            this._channel = channel;
        }
        
        @Override
        protected long getIdleTimeout() {
            final long blockingTimeout = this._channel.getHttpConfiguration().getBlockingTimeout();
            if (blockingTimeout == 0L) {
                return this._channel.getIdleTimeout();
            }
            return blockingTimeout;
        }
    }
}
