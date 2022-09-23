// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.thrift.async;

import org.apache.thrift.transport.TTransportException;
import java.io.IOException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import org.apache.thrift.transport.TFramedTransport;
import org.apache.thrift.transport.TTransport;
import org.apache.thrift.transport.TMemoryBuffer;
import org.apache.thrift.TException;
import org.apache.thrift.protocol.TProtocol;
import java.nio.ByteBuffer;
import org.apache.thrift.protocol.TProtocolFactory;
import org.apache.thrift.transport.TNonblockingTransport;
import java.util.concurrent.atomic.AtomicLong;

public abstract class TAsyncMethodCall<T>
{
    private static final int INITIAL_MEMORY_BUFFER_SIZE = 128;
    private static AtomicLong sequenceIdCounter;
    private State state;
    protected final TNonblockingTransport transport;
    private final TProtocolFactory protocolFactory;
    protected final TAsyncClient client;
    private final AsyncMethodCallback<T> callback;
    private final boolean isOneway;
    private long sequenceId;
    private final long timeout;
    private ByteBuffer sizeBuffer;
    private final byte[] sizeBufferArray;
    private ByteBuffer frameBuffer;
    private long startTime;
    
    protected TAsyncMethodCall(final TAsyncClient client, final TProtocolFactory protocolFactory, final TNonblockingTransport transport, final AsyncMethodCallback<T> callback, final boolean isOneway) {
        this.state = null;
        this.sizeBufferArray = new byte[4];
        this.startTime = System.currentTimeMillis();
        this.transport = transport;
        this.callback = callback;
        this.protocolFactory = protocolFactory;
        this.client = client;
        this.isOneway = isOneway;
        this.sequenceId = TAsyncMethodCall.sequenceIdCounter.getAndIncrement();
        this.timeout = client.getTimeout();
    }
    
    protected State getState() {
        return this.state;
    }
    
    protected boolean isFinished() {
        return this.state == State.RESPONSE_READ;
    }
    
    protected long getStartTime() {
        return this.startTime;
    }
    
    protected long getSequenceId() {
        return this.sequenceId;
    }
    
    public TAsyncClient getClient() {
        return this.client;
    }
    
    public boolean hasTimeout() {
        return this.timeout > 0L;
    }
    
    public long getTimeoutTimestamp() {
        return this.timeout + this.startTime;
    }
    
    protected abstract void write_args(final TProtocol p0) throws TException;
    
    protected void prepareMethodCall() throws TException {
        final TMemoryBuffer memoryBuffer = new TMemoryBuffer(128);
        final TProtocol protocol = this.protocolFactory.getProtocol(memoryBuffer);
        this.write_args(protocol);
        final int length = memoryBuffer.length();
        this.frameBuffer = ByteBuffer.wrap(memoryBuffer.getArray(), 0, length);
        TFramedTransport.encodeFrameSize(length, this.sizeBufferArray);
        this.sizeBuffer = ByteBuffer.wrap(this.sizeBufferArray);
    }
    
    void start(final Selector sel) throws IOException {
        SelectionKey key;
        if (this.transport.isOpen()) {
            this.state = State.WRITING_REQUEST_SIZE;
            key = this.transport.registerSelector(sel, 4);
        }
        else {
            this.state = State.CONNECTING;
            key = this.transport.registerSelector(sel, 8);
            if (this.transport.startConnect()) {
                this.registerForFirstWrite(key);
            }
        }
        key.attach(this);
    }
    
    void registerForFirstWrite(final SelectionKey key) throws IOException {
        this.state = State.WRITING_REQUEST_SIZE;
        key.interestOps(4);
    }
    
    protected ByteBuffer getFrameBuffer() {
        return this.frameBuffer;
    }
    
    protected void transition(final SelectionKey key) {
        Exception e = null;
        if (!key.isValid()) {
            key.cancel();
            e = new TTransportException("Selection key not valid!");
            this.onError(e);
            return;
        }
        try {
            switch (this.state) {
                case CONNECTING: {
                    this.doConnecting(key);
                    break;
                }
                case WRITING_REQUEST_SIZE: {
                    this.doWritingRequestSize();
                    break;
                }
                case WRITING_REQUEST_BODY: {
                    this.doWritingRequestBody(key);
                    break;
                }
                case READING_RESPONSE_SIZE: {
                    this.doReadingResponseSize();
                    break;
                }
                case READING_RESPONSE_BODY: {
                    this.doReadingResponseBody(key);
                    break;
                }
                default: {
                    throw new IllegalStateException("Method call in state " + this.state + " but selector called transition method. Seems like a bug...");
                }
            }
        }
        catch (Exception e) {
            key.cancel();
            key.attach(null);
            this.onError(e);
        }
    }
    
    protected void onError(final Exception e) {
        this.client.onError(e);
        this.callback.onError(e);
        this.state = State.ERROR;
    }
    
    private void doReadingResponseBody(final SelectionKey key) throws IOException {
        if (this.transport.read(this.frameBuffer) < 0) {
            throw new IOException("Read call frame failed");
        }
        if (this.frameBuffer.remaining() == 0) {
            this.cleanUpAndFireCallback(key);
        }
    }
    
    private void cleanUpAndFireCallback(final SelectionKey key) {
        this.state = State.RESPONSE_READ;
        key.interestOps(0);
        key.attach(null);
        this.client.onComplete();
        this.callback.onComplete((T)this);
    }
    
    private void doReadingResponseSize() throws IOException {
        if (this.transport.read(this.sizeBuffer) < 0) {
            throw new IOException("Read call frame size failed");
        }
        if (this.sizeBuffer.remaining() == 0) {
            this.state = State.READING_RESPONSE_BODY;
            this.frameBuffer = ByteBuffer.allocate(TFramedTransport.decodeFrameSize(this.sizeBufferArray));
        }
    }
    
    private void doWritingRequestBody(final SelectionKey key) throws IOException {
        if (this.transport.write(this.frameBuffer) < 0) {
            throw new IOException("Write call frame failed");
        }
        if (this.frameBuffer.remaining() == 0) {
            if (this.isOneway) {
                this.cleanUpAndFireCallback(key);
            }
            else {
                this.state = State.READING_RESPONSE_SIZE;
                this.sizeBuffer.rewind();
                key.interestOps(1);
            }
        }
    }
    
    private void doWritingRequestSize() throws IOException {
        if (this.transport.write(this.sizeBuffer) < 0) {
            throw new IOException("Write call frame size failed");
        }
        if (this.sizeBuffer.remaining() == 0) {
            this.state = State.WRITING_REQUEST_BODY;
        }
    }
    
    private void doConnecting(final SelectionKey key) throws IOException {
        if (!key.isConnectable() || !this.transport.finishConnect()) {
            throw new IOException("not connectable or finishConnect returned false after we got an OP_CONNECT");
        }
        this.registerForFirstWrite(key);
    }
    
    static {
        TAsyncMethodCall.sequenceIdCounter = new AtomicLong(0L);
    }
    
    public enum State
    {
        CONNECTING, 
        WRITING_REQUEST_SIZE, 
        WRITING_REQUEST_BODY, 
        READING_RESPONSE_SIZE, 
        READING_RESPONSE_BODY, 
        RESPONSE_READ, 
        ERROR;
    }
}
