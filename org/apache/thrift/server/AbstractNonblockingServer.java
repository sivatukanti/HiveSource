// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.thrift.server;

import org.apache.thrift.TBaseAsyncProcessor;
import org.apache.thrift.TException;
import java.io.OutputStream;
import org.apache.thrift.transport.TIOStreamTransport;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.transport.TTransport;
import org.apache.thrift.transport.TMemoryInputTransport;
import org.apache.thrift.TByteArrayOutputStream;
import java.nio.ByteBuffer;
import org.apache.thrift.transport.TNonblockingTransport;
import java.nio.channels.SelectionKey;
import java.util.Iterator;
import java.io.IOException;
import java.nio.channels.spi.SelectorProvider;
import java.util.HashSet;
import java.util.Set;
import java.nio.channels.Selector;
import org.apache.thrift.transport.TTransportFactory;
import org.apache.thrift.transport.TFramedTransport;
import org.apache.thrift.transport.TServerTransport;
import org.apache.thrift.transport.TNonblockingServerTransport;
import org.apache.thrift.transport.TTransportException;
import org.slf4j.LoggerFactory;
import java.util.concurrent.atomic.AtomicLong;
import org.slf4j.Logger;

public abstract class AbstractNonblockingServer extends TServer
{
    protected final Logger LOGGER;
    final long MAX_READ_BUFFER_BYTES;
    final AtomicLong readBufferBytesAllocated;
    
    public AbstractNonblockingServer(final AbstractNonblockingServerArgs args) {
        super(args);
        this.LOGGER = LoggerFactory.getLogger(this.getClass().getName());
        this.readBufferBytesAllocated = new AtomicLong(0L);
        this.MAX_READ_BUFFER_BYTES = args.maxReadBufferBytes;
    }
    
    @Override
    public void serve() {
        if (!this.startThreads()) {
            return;
        }
        if (!this.startListening()) {
            return;
        }
        this.setServing(true);
        this.waitForShutdown();
        this.setServing(false);
        this.stopListening();
    }
    
    protected abstract boolean startThreads();
    
    protected abstract void waitForShutdown();
    
    protected boolean startListening() {
        try {
            this.serverTransport_.listen();
            return true;
        }
        catch (TTransportException ttx) {
            this.LOGGER.error("Failed to start listening on server socket!", ttx);
            return false;
        }
    }
    
    protected void stopListening() {
        this.serverTransport_.close();
    }
    
    protected abstract boolean requestInvoke(final FrameBuffer p0);
    
    public abstract static class AbstractNonblockingServerArgs<T extends AbstractNonblockingServerArgs<T>> extends AbstractServerArgs<T>
    {
        public long maxReadBufferBytes;
        
        public AbstractNonblockingServerArgs(final TNonblockingServerTransport transport) {
            super(transport);
            this.maxReadBufferBytes = Long.MAX_VALUE;
            this.transportFactory(new TFramedTransport.Factory());
        }
    }
    
    protected abstract class AbstractSelectThread extends Thread
    {
        protected final Selector selector;
        protected final Set<FrameBuffer> selectInterestChanges;
        
        public AbstractSelectThread() throws IOException {
            this.selectInterestChanges = new HashSet<FrameBuffer>();
            this.selector = SelectorProvider.provider().openSelector();
        }
        
        public void wakeupSelector() {
            this.selector.wakeup();
        }
        
        public void requestSelectInterestChange(final FrameBuffer frameBuffer) {
            synchronized (this.selectInterestChanges) {
                this.selectInterestChanges.add(frameBuffer);
            }
            this.selector.wakeup();
        }
        
        protected void processInterestChanges() {
            synchronized (this.selectInterestChanges) {
                for (final FrameBuffer fb : this.selectInterestChanges) {
                    fb.changeSelectInterests();
                }
                this.selectInterestChanges.clear();
            }
        }
        
        protected void handleRead(final SelectionKey key) {
            final FrameBuffer buffer = (FrameBuffer)key.attachment();
            if (!buffer.read()) {
                this.cleanupSelectionKey(key);
                return;
            }
            if (buffer.isFrameFullyRead() && !AbstractNonblockingServer.this.requestInvoke(buffer)) {
                this.cleanupSelectionKey(key);
            }
        }
        
        protected void handleWrite(final SelectionKey key) {
            final FrameBuffer buffer = (FrameBuffer)key.attachment();
            if (!buffer.write()) {
                this.cleanupSelectionKey(key);
            }
        }
        
        protected void cleanupSelectionKey(final SelectionKey key) {
            final FrameBuffer buffer = (FrameBuffer)key.attachment();
            if (buffer != null) {
                buffer.close();
            }
            key.cancel();
        }
    }
    
    private enum FrameBufferState
    {
        READING_FRAME_SIZE, 
        READING_FRAME, 
        READ_FRAME_COMPLETE, 
        AWAITING_REGISTER_WRITE, 
        WRITING, 
        AWAITING_REGISTER_READ, 
        AWAITING_CLOSE;
    }
    
    public class FrameBuffer
    {
        private final Logger LOGGER;
        protected final TNonblockingTransport trans_;
        protected final SelectionKey selectionKey_;
        protected final AbstractSelectThread selectThread_;
        protected FrameBufferState state_;
        protected ByteBuffer buffer_;
        protected final TByteArrayOutputStream response_;
        protected final TMemoryInputTransport frameTrans_;
        protected final TTransport inTrans_;
        protected final TTransport outTrans_;
        protected final TProtocol inProt_;
        protected final TProtocol outProt_;
        protected final ServerContext context_;
        
        public FrameBuffer(final TNonblockingTransport trans, final SelectionKey selectionKey, final AbstractSelectThread selectThread) {
            this.LOGGER = LoggerFactory.getLogger(this.getClass().getName());
            this.state_ = FrameBufferState.READING_FRAME_SIZE;
            this.trans_ = trans;
            this.selectionKey_ = selectionKey;
            this.selectThread_ = selectThread;
            this.buffer_ = ByteBuffer.allocate(4);
            this.frameTrans_ = new TMemoryInputTransport();
            this.response_ = new TByteArrayOutputStream();
            this.inTrans_ = AbstractNonblockingServer.this.inputTransportFactory_.getTransport(this.frameTrans_);
            this.outTrans_ = AbstractNonblockingServer.this.outputTransportFactory_.getTransport(new TIOStreamTransport(this.response_));
            this.inProt_ = AbstractNonblockingServer.this.inputProtocolFactory_.getProtocol(this.inTrans_);
            this.outProt_ = AbstractNonblockingServer.this.outputProtocolFactory_.getProtocol(this.outTrans_);
            if (AbstractNonblockingServer.this.eventHandler_ != null) {
                this.context_ = AbstractNonblockingServer.this.eventHandler_.createContext(this.inProt_, this.outProt_);
            }
            else {
                this.context_ = null;
            }
        }
        
        public boolean read() {
            if (this.state_ == FrameBufferState.READING_FRAME_SIZE) {
                if (!this.internalRead()) {
                    return false;
                }
                if (this.buffer_.remaining() != 0) {
                    return true;
                }
                final int frameSize = this.buffer_.getInt(0);
                if (frameSize <= 0) {
                    this.LOGGER.error("Read an invalid frame size of " + frameSize + ". Are you using TFramedTransport on the client side?");
                    return false;
                }
                if (frameSize > AbstractNonblockingServer.this.MAX_READ_BUFFER_BYTES) {
                    this.LOGGER.error("Read a frame size of " + frameSize + ", which is bigger than the maximum allowable buffer size for ALL connections.");
                    return false;
                }
                if (AbstractNonblockingServer.this.readBufferBytesAllocated.get() + frameSize > AbstractNonblockingServer.this.MAX_READ_BUFFER_BYTES) {
                    return true;
                }
                AbstractNonblockingServer.this.readBufferBytesAllocated.addAndGet(frameSize + 4);
                (this.buffer_ = ByteBuffer.allocate(frameSize + 4)).putInt(frameSize);
                this.state_ = FrameBufferState.READING_FRAME;
            }
            if (this.state_ != FrameBufferState.READING_FRAME) {
                this.LOGGER.error("Read was called but state is invalid (" + this.state_ + ")");
                return false;
            }
            if (!this.internalRead()) {
                return false;
            }
            if (this.buffer_.remaining() == 0) {
                this.selectionKey_.interestOps(0);
                this.state_ = FrameBufferState.READ_FRAME_COMPLETE;
            }
            return true;
        }
        
        public boolean write() {
            if (this.state_ == FrameBufferState.WRITING) {
                try {
                    if (this.trans_.write(this.buffer_) < 0) {
                        return false;
                    }
                }
                catch (IOException e) {
                    this.LOGGER.warn("Got an IOException during write!", e);
                    return false;
                }
                if (this.buffer_.remaining() == 0) {
                    this.prepareRead();
                }
                return true;
            }
            this.LOGGER.error("Write was called, but state is invalid (" + this.state_ + ")");
            return false;
        }
        
        public void changeSelectInterests() {
            if (this.state_ == FrameBufferState.AWAITING_REGISTER_WRITE) {
                this.selectionKey_.interestOps(4);
                this.state_ = FrameBufferState.WRITING;
            }
            else if (this.state_ == FrameBufferState.AWAITING_REGISTER_READ) {
                this.prepareRead();
            }
            else if (this.state_ == FrameBufferState.AWAITING_CLOSE) {
                this.close();
                this.selectionKey_.cancel();
            }
            else {
                this.LOGGER.error("changeSelectInterest was called, but state is invalid (" + this.state_ + ")");
            }
        }
        
        public void close() {
            if (this.state_ == FrameBufferState.READING_FRAME || this.state_ == FrameBufferState.READ_FRAME_COMPLETE || this.state_ == FrameBufferState.AWAITING_CLOSE) {
                AbstractNonblockingServer.this.readBufferBytesAllocated.addAndGet(-this.buffer_.array().length);
            }
            this.trans_.close();
            if (AbstractNonblockingServer.this.eventHandler_ != null) {
                AbstractNonblockingServer.this.eventHandler_.deleteContext(this.context_, this.inProt_, this.outProt_);
            }
        }
        
        public boolean isFrameFullyRead() {
            return this.state_ == FrameBufferState.READ_FRAME_COMPLETE;
        }
        
        public void responseReady() {
            AbstractNonblockingServer.this.readBufferBytesAllocated.addAndGet(-this.buffer_.array().length);
            if (this.response_.len() == 0) {
                this.state_ = FrameBufferState.AWAITING_REGISTER_READ;
                this.buffer_ = null;
            }
            else {
                this.buffer_ = ByteBuffer.wrap(this.response_.get(), 0, this.response_.len());
                this.state_ = FrameBufferState.AWAITING_REGISTER_WRITE;
            }
            this.requestSelectInterestChange();
        }
        
        public void invoke() {
            this.frameTrans_.reset(this.buffer_.array());
            this.response_.reset();
            try {
                if (AbstractNonblockingServer.this.eventHandler_ != null) {
                    AbstractNonblockingServer.this.eventHandler_.processContext(this.context_, this.inTrans_, this.outTrans_);
                }
                AbstractNonblockingServer.this.processorFactory_.getProcessor(this.inTrans_).process(this.inProt_, this.outProt_);
                this.responseReady();
                return;
            }
            catch (TException te) {
                this.LOGGER.warn("Exception while invoking!", te);
            }
            catch (Throwable t) {
                this.LOGGER.error("Unexpected throwable while invoking!", t);
            }
            this.state_ = FrameBufferState.AWAITING_CLOSE;
            this.requestSelectInterestChange();
        }
        
        private boolean internalRead() {
            try {
                return this.trans_.read(this.buffer_) >= 0;
            }
            catch (IOException e) {
                this.LOGGER.warn("Got an IOException in internalRead!", e);
                return false;
            }
        }
        
        private void prepareRead() {
            this.selectionKey_.interestOps(1);
            this.buffer_ = ByteBuffer.allocate(4);
            this.state_ = FrameBufferState.READING_FRAME_SIZE;
        }
        
        protected void requestSelectInterestChange() {
            if (Thread.currentThread() == this.selectThread_) {
                this.changeSelectInterests();
            }
            else {
                this.selectThread_.requestSelectInterestChange(this);
            }
        }
    }
    
    public class AsyncFrameBuffer extends FrameBuffer
    {
        public AsyncFrameBuffer(final TNonblockingTransport trans, final SelectionKey selectionKey, final AbstractSelectThread selectThread) {
            super(trans, selectionKey, selectThread);
        }
        
        public TProtocol getInputProtocol() {
            return this.inProt_;
        }
        
        public TProtocol getOutputProtocol() {
            return this.outProt_;
        }
        
        @Override
        public void invoke() {
            this.frameTrans_.reset(this.buffer_.array());
            this.response_.reset();
            try {
                if (AbstractNonblockingServer.this.eventHandler_ != null) {
                    AbstractNonblockingServer.this.eventHandler_.processContext(this.context_, this.inTrans_, this.outTrans_);
                }
                ((TBaseAsyncProcessor)AbstractNonblockingServer.this.processorFactory_.getProcessor(this.inTrans_)).process(this);
                return;
            }
            catch (TException te) {
                AbstractNonblockingServer.this.LOGGER.warn("Exception while invoking!", te);
            }
            catch (Throwable t) {
                AbstractNonblockingServer.this.LOGGER.error("Unexpected throwable while invoking!", t);
            }
            this.state_ = FrameBufferState.AWAITING_CLOSE;
            this.requestSelectInterestChange();
        }
    }
}
