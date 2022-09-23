// 
// Decompiled by Procyon v0.5.36
// 

package org.eclipse.jetty.server;

import org.eclipse.jetty.util.Callback;
import org.eclipse.jetty.util.BufferUtil;
import org.eclipse.jetty.io.EofException;
import org.eclipse.jetty.util.log.Log;
import org.eclipse.jetty.io.RuntimeIOException;
import java.util.Objects;
import java.io.EOFException;
import java.util.concurrent.TimeoutException;
import java.nio.ByteBuffer;
import org.eclipse.jetty.http.BadMessageException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.Executor;
import java.io.IOException;
import java.util.ArrayDeque;
import javax.servlet.ReadListener;
import java.util.Deque;
import org.eclipse.jetty.util.log.Logger;
import javax.servlet.ServletInputStream;

public class HttpInput extends ServletInputStream implements Runnable
{
    private static final Logger LOG;
    private static final Content EOF_CONTENT;
    private static final Content EARLY_EOF_CONTENT;
    private final byte[] _oneByteBuffer;
    private final Deque<Content> _inputQ;
    private final HttpChannelState _channelState;
    private ReadListener _listener;
    private State _state;
    private long _firstByteTimeStamp;
    private long _contentArrived;
    private long _contentConsumed;
    private long _blockUntil;
    private boolean _waitingForContent;
    protected static final State STREAM;
    protected static final State ASYNC;
    protected static final State EARLY_EOF;
    protected static final State EOF;
    protected static final State AEOF;
    
    public HttpInput(final HttpChannelState state) {
        this._oneByteBuffer = new byte[1];
        this._inputQ = new ArrayDeque<Content>();
        this._state = HttpInput.STREAM;
        this._firstByteTimeStamp = -1L;
        this._channelState = state;
    }
    
    protected HttpChannelState getHttpChannelState() {
        return this._channelState;
    }
    
    public void recycle() {
        synchronized (this._inputQ) {
            for (Content item = this._inputQ.poll(); item != null; item = this._inputQ.poll()) {
                item.failed(null);
            }
            this._listener = null;
            this._state = HttpInput.STREAM;
            this._contentArrived = 0L;
            this._contentConsumed = 0L;
            this._firstByteTimeStamp = -1L;
            this._blockUntil = 0L;
            this._waitingForContent = false;
        }
    }
    
    @Override
    public int available() {
        int available = 0;
        boolean woken = false;
        synchronized (this._inputQ) {
            Content content = this._inputQ.peek();
            if (content == null) {
                try {
                    this.produceContent();
                }
                catch (IOException e) {
                    woken = this.failed(e);
                }
                content = this._inputQ.peek();
            }
            if (content != null) {
                available = this.remaining(content);
            }
        }
        if (woken) {
            this.wake();
        }
        return available;
    }
    
    private void wake() {
        final HttpChannel channel = this._channelState.getHttpChannel();
        final Executor executor = channel.getConnector().getServer().getThreadPool();
        executor.execute(channel);
    }
    
    private long getBlockingTimeout() {
        return this.getHttpChannelState().getHttpChannel().getHttpConfiguration().getBlockingTimeout();
    }
    
    @Override
    public int read() throws IOException {
        final int read = this.read(this._oneByteBuffer, 0, 1);
        if (read == 0) {
            throw new IllegalStateException("unready read=0");
        }
        return (read < 0) ? -1 : (this._oneByteBuffer[0] & 0xFF);
    }
    
    @Override
    public int read(final byte[] b, final int off, final int len) throws IOException {
        synchronized (this._inputQ) {
            if (!this.isAsync() && this._blockUntil == 0L) {
                final long blockingTimeout = this.getBlockingTimeout();
                if (blockingTimeout > 0L) {
                    this._blockUntil = System.nanoTime() + TimeUnit.MILLISECONDS.toNanos(blockingTimeout);
                }
            }
            final long minRequestDataRate = this._channelState.getHttpChannel().getHttpConfiguration().getMinRequestDataRate();
            if (minRequestDataRate > 0L && this._firstByteTimeStamp != -1L) {
                final long period = System.nanoTime() - this._firstByteTimeStamp;
                if (period > 0L) {
                    final long minimum_data = minRequestDataRate * TimeUnit.NANOSECONDS.toMillis(period) / TimeUnit.SECONDS.toMillis(1L);
                    if (this._contentArrived < minimum_data) {
                        throw new BadMessageException(408, String.format("Request data rate < %d B/s", minRequestDataRate));
                    }
                }
            }
            while (true) {
                final Content item = this.nextContent();
                if (item != null) {
                    final int l = this.get(item, b, off, len);
                    if (HttpInput.LOG.isDebugEnabled()) {
                        HttpInput.LOG.debug("{} read {} from {}", this, l, item);
                    }
                    this.consumeNonContent();
                    return l;
                }
                if (!this._state.blockForContent(this)) {
                    return this._state.noContent();
                }
            }
        }
    }
    
    protected void produceContent() throws IOException {
    }
    
    protected Content nextContent() throws IOException {
        Content content = this.pollContent();
        if (content == null && !this.isFinished()) {
            this.produceContent();
            content = this.pollContent();
        }
        return content;
    }
    
    protected Content pollContent() {
        Content content;
        for (content = this._inputQ.peek(); content != null && this.remaining(content) == 0; content = this._inputQ.peek()) {
            this._inputQ.poll();
            content.succeeded();
            if (HttpInput.LOG.isDebugEnabled()) {
                HttpInput.LOG.debug("{} consumed {}", this, content);
            }
            if (content == HttpInput.EOF_CONTENT) {
                if (this._listener == null) {
                    this._state = HttpInput.EOF;
                }
                else {
                    this._state = HttpInput.AEOF;
                    final boolean woken = this._channelState.onReadReady();
                    if (woken) {
                        this.wake();
                    }
                }
            }
            else if (content == HttpInput.EARLY_EOF_CONTENT) {
                this._state = HttpInput.EARLY_EOF;
            }
        }
        return content;
    }
    
    protected void consumeNonContent() {
        for (Content content = this._inputQ.peek(); content != null && this.remaining(content) == 0 && !(content instanceof EofContent); content = this._inputQ.peek()) {
            this._inputQ.poll();
            content.succeeded();
            if (HttpInput.LOG.isDebugEnabled()) {
                HttpInput.LOG.debug("{} consumed {}", this, content);
            }
        }
    }
    
    protected Content nextReadable() throws IOException {
        Content content = this.pollReadable();
        if (content == null && !this.isFinished()) {
            this.produceContent();
            content = this.pollReadable();
        }
        return content;
    }
    
    protected Content pollReadable() {
        for (Content content = this._inputQ.peek(); content != null; content = this._inputQ.peek()) {
            if (content == HttpInput.EOF_CONTENT || content == HttpInput.EARLY_EOF_CONTENT || this.remaining(content) > 0) {
                return content;
            }
            this._inputQ.poll();
            content.succeeded();
            if (HttpInput.LOG.isDebugEnabled()) {
                HttpInput.LOG.debug("{} consumed {}", this, content);
            }
        }
        return null;
    }
    
    protected int remaining(final Content item) {
        return item.remaining();
    }
    
    protected int get(final Content content, final byte[] buffer, final int offset, final int length) {
        final int l = Math.min(content.remaining(), length);
        content.getContent().get(buffer, offset, l);
        this._contentConsumed += l;
        return l;
    }
    
    protected void skip(final Content content, final int length) {
        final int l = Math.min(content.remaining(), length);
        final ByteBuffer buffer = content.getContent();
        buffer.position(buffer.position() + l);
        this._contentConsumed += l;
        if (l > 0 && !content.hasContent()) {
            this.pollContent();
        }
    }
    
    protected void blockForContent() throws IOException {
        try {
            this._waitingForContent = true;
            this._channelState.getHttpChannel().onBlockWaitForContent();
            boolean loop = false;
            long timeout = 0L;
            while (true) {
                if (this._blockUntil != 0L) {
                    timeout = TimeUnit.NANOSECONDS.toMillis(this._blockUntil - System.nanoTime());
                    if (timeout <= 0L) {
                        throw new TimeoutException(String.format("Blocking timeout %d ms", this.getBlockingTimeout()));
                    }
                }
                if (loop) {
                    break;
                }
                if (HttpInput.LOG.isDebugEnabled()) {
                    HttpInput.LOG.debug("{} blocking for content timeout={}", this, timeout);
                }
                if (timeout > 0L) {
                    this._inputQ.wait(timeout);
                }
                else {
                    this._inputQ.wait();
                }
                loop = true;
            }
        }
        catch (Throwable x) {
            this._channelState.getHttpChannel().onBlockWaitForContentFailure(x);
        }
    }
    
    public boolean addContent(final Content item) {
        synchronized (this._inputQ) {
            this._waitingForContent = false;
            if (this._firstByteTimeStamp == -1L) {
                this._firstByteTimeStamp = System.nanoTime();
            }
            if (this.isFinished()) {
                final Throwable failure = this.isError() ? ((ErrorState)this._state).getError() : new EOFException("Content after EOF");
                item.failed(failure);
                return false;
            }
            this._contentArrived += item.remaining();
            this._inputQ.offer(item);
            if (HttpInput.LOG.isDebugEnabled()) {
                HttpInput.LOG.debug("{} addContent {}", this, item);
            }
            return this.wakeup();
        }
    }
    
    public boolean hasContent() {
        synchronized (this._inputQ) {
            return this._inputQ.size() > 0;
        }
    }
    
    public void unblock() {
        synchronized (this._inputQ) {
            this._inputQ.notify();
        }
    }
    
    public long getContentConsumed() {
        synchronized (this._inputQ) {
            return this._contentConsumed;
        }
    }
    
    public boolean earlyEOF() {
        return this.addContent(HttpInput.EARLY_EOF_CONTENT);
    }
    
    public boolean eof() {
        return this.addContent(HttpInput.EOF_CONTENT);
    }
    
    public boolean consumeAll() {
        synchronized (this._inputQ) {
            try {
                while (true) {
                    final Content item = this.nextContent();
                    if (item == null) {
                        break;
                    }
                    this.skip(item, this.remaining(item));
                }
                return this.isFinished() && !this.isError();
            }
            catch (IOException e) {
                HttpInput.LOG.debug(e);
                return false;
            }
        }
    }
    
    public boolean isError() {
        synchronized (this._inputQ) {
            return this._state instanceof ErrorState;
        }
    }
    
    public boolean isAsync() {
        synchronized (this._inputQ) {
            return this._state == HttpInput.ASYNC;
        }
    }
    
    @Override
    public boolean isFinished() {
        synchronized (this._inputQ) {
            return this._state instanceof EOFState;
        }
    }
    
    @Override
    public boolean isReady() {
        try {
            synchronized (this._inputQ) {
                if (this._listener == null) {
                    return true;
                }
                if (this._state instanceof EOFState) {
                    return true;
                }
                if (this.nextReadable() != null) {
                    return true;
                }
                this._channelState.onReadUnready();
                this._waitingForContent = true;
            }
            return false;
        }
        catch (IOException e) {
            HttpInput.LOG.ignore(e);
            return true;
        }
    }
    
    @Override
    public void setReadListener(ReadListener readListener) {
        readListener = Objects.requireNonNull(readListener);
        boolean woken = false;
        try {
            synchronized (this._inputQ) {
                if (this._listener != null) {
                    throw new IllegalStateException("ReadListener already set");
                }
                if (this._state != HttpInput.STREAM) {
                    throw new IllegalStateException("State " + HttpInput.STREAM + " != " + this._state);
                }
                this._state = HttpInput.ASYNC;
                this._listener = readListener;
                final boolean content = this.nextContent() != null;
                if (content) {
                    woken = this._channelState.onReadReady();
                }
                else {
                    this._channelState.onReadUnready();
                    this._waitingForContent = true;
                }
            }
        }
        catch (IOException e) {
            throw new RuntimeIOException(e);
        }
        if (woken) {
            this.wake();
        }
    }
    
    public boolean onIdleTimeout(final Throwable x) {
        synchronized (this._inputQ) {
            if (this._waitingForContent && !this.isError()) {
                x.addSuppressed(new Throwable("HttpInput idle timeout"));
                this._state = new ErrorState(x);
                return this.wakeup();
            }
            return false;
        }
    }
    
    public boolean failed(final Throwable x) {
        synchronized (this._inputQ) {
            if (this.isError()) {
                if (HttpInput.LOG.isDebugEnabled()) {
                    final Throwable failure = new Throwable(((ErrorState)this._state).getError());
                    failure.addSuppressed(x);
                    HttpInput.LOG.debug(failure);
                }
            }
            else {
                x.addSuppressed(new Throwable("HttpInput failure"));
                this._state = new ErrorState(x);
            }
            return this.wakeup();
        }
    }
    
    private boolean wakeup() {
        if (this._listener != null) {
            return this._channelState.onReadPossible();
        }
        this._inputQ.notify();
        return false;
    }
    
    @Override
    public void run() {
        boolean aeof = false;
        final ReadListener listener;
        final Throwable error;
        synchronized (this._inputQ) {
            if (this._state == HttpInput.EOF) {
                return;
            }
            if (this._state == HttpInput.AEOF) {
                this._state = HttpInput.EOF;
                aeof = true;
            }
            listener = this._listener;
            error = ((this._state instanceof ErrorState) ? ((ErrorState)this._state).getError() : null);
        }
        try {
            if (error != null) {
                this._channelState.getHttpChannel().getResponse().getHttpFields().add(HttpConnection.CONNECTION_CLOSE);
                listener.onError(error);
            }
            else if (aeof) {
                listener.onAllDataRead();
            }
            else {
                listener.onDataAvailable();
            }
        }
        catch (Throwable e) {
            HttpInput.LOG.warn(e.toString(), new Object[0]);
            HttpInput.LOG.debug(e);
            try {
                if (aeof || error == null) {
                    this._channelState.getHttpChannel().getResponse().getHttpFields().add(HttpConnection.CONNECTION_CLOSE);
                    listener.onError(e);
                }
            }
            catch (Throwable e2) {
                HttpInput.LOG.warn(e2.toString(), new Object[0]);
                HttpInput.LOG.debug(e2);
                throw new RuntimeIOException(e2);
            }
        }
    }
    
    @Override
    public String toString() {
        final State state;
        final long consumed;
        final int q;
        final Content content;
        synchronized (this._inputQ) {
            state = this._state;
            consumed = this._contentConsumed;
            q = this._inputQ.size();
            content = this._inputQ.peekFirst();
        }
        return String.format("%s@%x[c=%d,q=%d,[0]=%s,s=%s]", this.getClass().getSimpleName(), this.hashCode(), consumed, q, content, state);
    }
    
    static {
        LOG = Log.getLogger(HttpInput.class);
        EOF_CONTENT = new EofContent("EOF");
        EARLY_EOF_CONTENT = new EofContent("EARLY_EOF");
        STREAM = new State() {
            @Override
            public boolean blockForContent(final HttpInput input) throws IOException {
                input.blockForContent();
                return true;
            }
            
            @Override
            public String toString() {
                return "STREAM";
            }
        };
        ASYNC = new State() {
            @Override
            public int noContent() throws IOException {
                return 0;
            }
            
            @Override
            public String toString() {
                return "ASYNC";
            }
        };
        EARLY_EOF = new EOFState() {
            @Override
            public int noContent() throws IOException {
                throw new EofException("Early EOF");
            }
            
            @Override
            public String toString() {
                return "EARLY_EOF";
            }
        };
        EOF = new EOFState() {
            @Override
            public String toString() {
                return "EOF";
            }
        };
        AEOF = new EOFState() {
            @Override
            public String toString() {
                return "AEOF";
            }
        };
    }
    
    public static class PoisonPillContent extends Content
    {
        private final String _name;
        
        public PoisonPillContent(final String name) {
            super(BufferUtil.EMPTY_BUFFER);
            this._name = name;
        }
        
        @Override
        public String toString() {
            return this._name;
        }
    }
    
    public static class EofContent extends PoisonPillContent
    {
        EofContent(final String name) {
            super(name);
        }
    }
    
    public static class Content implements Callback
    {
        private final ByteBuffer _content;
        
        public Content(final ByteBuffer content) {
            this._content = content;
        }
        
        @Override
        public boolean isNonBlocking() {
            return true;
        }
        
        public ByteBuffer getContent() {
            return this._content;
        }
        
        public boolean hasContent() {
            return this._content.hasRemaining();
        }
        
        public int remaining() {
            return this._content.remaining();
        }
        
        @Override
        public String toString() {
            return String.format("Content@%x{%s}", this.hashCode(), BufferUtil.toDetailString(this._content));
        }
    }
    
    protected abstract static class State
    {
        public boolean blockForContent(final HttpInput in) throws IOException {
            return false;
        }
        
        public int noContent() throws IOException {
            return -1;
        }
    }
    
    protected static class EOFState extends State
    {
    }
    
    protected class ErrorState extends EOFState
    {
        final Throwable _error;
        
        ErrorState(final Throwable error) {
            this._error = error;
        }
        
        public Throwable getError() {
            return this._error;
        }
        
        @Override
        public int noContent() throws IOException {
            if (this._error instanceof IOException) {
                throw (IOException)this._error;
            }
            throw new IOException(this._error);
        }
        
        @Override
        public String toString() {
            return "ERROR:" + this._error;
        }
    }
}
