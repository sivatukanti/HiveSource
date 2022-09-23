// 
// Decompiled by Procyon v0.5.36
// 

package parquet.org.apache.thrift.server;

import java.io.OutputStream;
import parquet.org.apache.thrift.transport.TIOStreamTransport;
import parquet.org.apache.thrift.transport.TMemoryInputTransport;
import parquet.org.apache.thrift.protocol.TProtocol;
import parquet.org.apache.thrift.transport.TTransport;
import parquet.org.apache.thrift.TException;
import parquet.org.apache.thrift.TByteArrayOutputStream;
import java.nio.ByteBuffer;
import parquet.org.apache.thrift.transport.TNonblockingTransport;
import java.util.Iterator;
import java.nio.channels.SelectionKey;
import java.nio.channels.spi.SelectorProvider;
import java.util.HashSet;
import java.util.Set;
import java.nio.channels.Selector;
import parquet.org.apache.thrift.transport.TTransportFactory;
import parquet.org.apache.thrift.transport.TFramedTransport;
import parquet.org.apache.thrift.transport.TServerTransport;
import parquet.org.slf4j.LoggerFactory;
import java.io.IOException;
import parquet.org.apache.thrift.transport.TNonblockingServerTransport;
import parquet.org.apache.thrift.transport.TTransportException;
import java.util.concurrent.atomic.AtomicLong;
import parquet.org.slf4j.Logger;

public class TNonblockingServer extends TServer
{
    private static final Logger LOGGER;
    private volatile boolean stopped_;
    private SelectThread selectThread_;
    private final long MAX_READ_BUFFER_BYTES;
    private final AtomicLong readBufferBytesAllocated;
    
    public TNonblockingServer(final AbstractNonblockingServerArgs args) {
        super(args);
        this.stopped_ = true;
        this.readBufferBytesAllocated = new AtomicLong(0L);
        this.MAX_READ_BUFFER_BYTES = args.maxReadBufferBytes;
    }
    
    @Override
    public void serve() {
        if (!this.startListening()) {
            return;
        }
        if (!this.startSelectorThread()) {
            return;
        }
        this.setServing(true);
        this.joinSelector();
        this.setServing(false);
        this.stopListening();
    }
    
    protected boolean startListening() {
        try {
            this.serverTransport_.listen();
            return true;
        }
        catch (TTransportException ttx) {
            TNonblockingServer.LOGGER.error("Failed to start listening on server socket!", ttx);
            return false;
        }
    }
    
    protected void stopListening() {
        this.serverTransport_.close();
    }
    
    protected boolean startSelectorThread() {
        try {
            this.selectThread_ = new SelectThread((TNonblockingServerTransport)this.serverTransport_);
            this.stopped_ = false;
            this.selectThread_.start();
            return true;
        }
        catch (IOException e) {
            TNonblockingServer.LOGGER.error("Failed to start selector thread!", e);
            return false;
        }
    }
    
    protected void joinSelector() {
        try {
            this.selectThread_.join();
        }
        catch (InterruptedException ex) {}
    }
    
    @Override
    public void stop() {
        this.stopped_ = true;
        if (this.selectThread_ != null) {
            this.selectThread_.wakeupSelector();
        }
    }
    
    protected boolean requestInvoke(final FrameBuffer frameBuffer) {
        frameBuffer.invoke();
        return true;
    }
    
    protected void requestSelectInterestChange(final FrameBuffer frameBuffer) {
        this.selectThread_.requestSelectInterestChange(frameBuffer);
    }
    
    public boolean isStopped() {
        return this.selectThread_.isStopped();
    }
    
    static {
        LOGGER = LoggerFactory.getLogger(TNonblockingServer.class.getName());
    }
    
    public static class Args extends AbstractNonblockingServerArgs<Args>
    {
        public Args(final TNonblockingServerTransport transport) {
            super(transport);
        }
    }
    
    public abstract static class AbstractNonblockingServerArgs<T extends AbstractNonblockingServerArgs<T>> extends AbstractServerArgs<T>
    {
        public long maxReadBufferBytes;
        
        public AbstractNonblockingServerArgs(final TNonblockingServerTransport transport) {
            super(transport);
            this.maxReadBufferBytes = Long.MAX_VALUE;
            this.transportFactory(new TFramedTransport.Factory());
        }
    }
    
    protected class SelectThread extends Thread
    {
        private final TNonblockingServerTransport serverTransport;
        private final Selector selector;
        private final Set<FrameBuffer> selectInterestChanges;
        
        public SelectThread(final TNonblockingServerTransport serverTransport) throws IOException {
            this.selectInterestChanges = new HashSet<FrameBuffer>();
            (this.serverTransport = serverTransport).registerSelector(this.selector = SelectorProvider.provider().openSelector());
        }
        
        public boolean isStopped() {
            return TNonblockingServer.this.stopped_;
        }
        
        @Override
        public void run() {
            try {
                while (!TNonblockingServer.this.stopped_) {
                    this.select();
                    this.processInterestChanges();
                }
            }
            catch (Throwable t) {
                TNonblockingServer.LOGGER.error("run() exiting due to uncaught error", t);
            }
            finally {
                TNonblockingServer.this.stopped_ = true;
            }
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
        
        private void select() {
            try {
                this.selector.select();
                final Iterator<SelectionKey> selectedKeys = this.selector.selectedKeys().iterator();
                while (!TNonblockingServer.this.stopped_ && selectedKeys.hasNext()) {
                    final SelectionKey key = selectedKeys.next();
                    selectedKeys.remove();
                    if (!key.isValid()) {
                        this.cleanupSelectionkey(key);
                    }
                    else if (key.isAcceptable()) {
                        this.handleAccept();
                    }
                    else if (key.isReadable()) {
                        this.handleRead(key);
                    }
                    else if (key.isWritable()) {
                        this.handleWrite(key);
                    }
                    else {
                        TNonblockingServer.LOGGER.warn("Unexpected state in select! " + key.interestOps());
                    }
                }
            }
            catch (IOException e) {
                TNonblockingServer.LOGGER.warn("Got an IOException while selecting!", e);
            }
        }
        
        private void processInterestChanges() {
            synchronized (this.selectInterestChanges) {
                for (final FrameBuffer fb : this.selectInterestChanges) {
                    fb.changeSelectInterests();
                }
                this.selectInterestChanges.clear();
            }
        }
        
        private void handleAccept() throws IOException {
            SelectionKey clientKey = null;
            TNonblockingTransport client = null;
            try {
                client = (TNonblockingTransport)this.serverTransport.accept();
                clientKey = client.registerSelector(this.selector, 1);
                final FrameBuffer frameBuffer = new FrameBuffer(client, clientKey);
                clientKey.attach(frameBuffer);
            }
            catch (TTransportException tte) {
                TNonblockingServer.LOGGER.warn("Exception trying to accept!", tte);
                tte.printStackTrace();
                if (clientKey != null) {
                    this.cleanupSelectionkey(clientKey);
                }
                if (client != null) {
                    client.close();
                }
            }
        }
        
        private void handleRead(final SelectionKey key) {
            final FrameBuffer buffer = (FrameBuffer)key.attachment();
            if (!buffer.read()) {
                this.cleanupSelectionkey(key);
                return;
            }
            if (buffer.isFrameFullyRead() && !TNonblockingServer.this.requestInvoke(buffer)) {
                this.cleanupSelectionkey(key);
            }
        }
        
        private void handleWrite(final SelectionKey key) {
            final FrameBuffer buffer = (FrameBuffer)key.attachment();
            if (!buffer.write()) {
                this.cleanupSelectionkey(key);
            }
        }
        
        private void cleanupSelectionkey(final SelectionKey key) {
            final FrameBuffer buffer = (FrameBuffer)key.attachment();
            if (buffer != null) {
                buffer.close();
            }
            key.cancel();
        }
    }
    
    protected class FrameBuffer
    {
        private static final int READING_FRAME_SIZE = 1;
        private static final int READING_FRAME = 2;
        private static final int READ_FRAME_COMPLETE = 3;
        private static final int AWAITING_REGISTER_WRITE = 4;
        private static final int WRITING = 6;
        private static final int AWAITING_REGISTER_READ = 7;
        private static final int AWAITING_CLOSE = 8;
        public final TNonblockingTransport trans_;
        private final SelectionKey selectionKey_;
        private int state_;
        private ByteBuffer buffer_;
        private TByteArrayOutputStream response_;
        
        public FrameBuffer(final TNonblockingTransport trans, final SelectionKey selectionKey) {
            this.state_ = 1;
            this.trans_ = trans;
            this.selectionKey_ = selectionKey;
            this.buffer_ = ByteBuffer.allocate(4);
        }
        
        public boolean read() {
            if (this.state_ == 1) {
                if (!this.internalRead()) {
                    return false;
                }
                if (this.buffer_.remaining() != 0) {
                    return true;
                }
                final int frameSize = this.buffer_.getInt(0);
                if (frameSize <= 0) {
                    TNonblockingServer.LOGGER.error("Read an invalid frame size of " + frameSize + ". Are you using TFramedTransport on the client side?");
                    return false;
                }
                if (frameSize > TNonblockingServer.this.MAX_READ_BUFFER_BYTES) {
                    TNonblockingServer.LOGGER.error("Read a frame size of " + frameSize + ", which is bigger than the maximum allowable buffer size for ALL connections.");
                    return false;
                }
                if (TNonblockingServer.this.readBufferBytesAllocated.get() + frameSize > TNonblockingServer.this.MAX_READ_BUFFER_BYTES) {
                    return true;
                }
                TNonblockingServer.this.readBufferBytesAllocated.addAndGet(frameSize);
                this.buffer_ = ByteBuffer.allocate(frameSize);
                this.state_ = 2;
            }
            if (this.state_ != 2) {
                TNonblockingServer.LOGGER.error("Read was called but state is invalid (" + this.state_ + ")");
                return false;
            }
            if (!this.internalRead()) {
                return false;
            }
            if (this.buffer_.remaining() == 0) {
                this.selectionKey_.interestOps(0);
                this.state_ = 3;
            }
            return true;
        }
        
        public boolean write() {
            if (this.state_ == 6) {
                try {
                    if (this.trans_.write(this.buffer_) < 0) {
                        return false;
                    }
                }
                catch (IOException e) {
                    TNonblockingServer.LOGGER.warn("Got an IOException during write!", e);
                    return false;
                }
                if (this.buffer_.remaining() == 0) {
                    this.prepareRead();
                }
                return true;
            }
            TNonblockingServer.LOGGER.error("Write was called, but state is invalid (" + this.state_ + ")");
            return false;
        }
        
        public void changeSelectInterests() {
            if (this.state_ == 4) {
                this.selectionKey_.interestOps(4);
                this.state_ = 6;
            }
            else if (this.state_ == 7) {
                this.prepareRead();
            }
            else if (this.state_ == 8) {
                this.close();
                this.selectionKey_.cancel();
            }
            else {
                TNonblockingServer.LOGGER.error("changeSelectInterest was called, but state is invalid (" + this.state_ + ")");
            }
        }
        
        public void close() {
            if (this.state_ == 2 || this.state_ == 3) {
                TNonblockingServer.this.readBufferBytesAllocated.addAndGet(-this.buffer_.array().length);
            }
            this.trans_.close();
        }
        
        public boolean isFrameFullyRead() {
            return this.state_ == 3;
        }
        
        public void responseReady() {
            TNonblockingServer.this.readBufferBytesAllocated.addAndGet(-this.buffer_.array().length);
            if (this.response_.len() == 0) {
                this.state_ = 7;
                this.buffer_ = null;
            }
            else {
                this.buffer_ = ByteBuffer.wrap(this.response_.get(), 0, this.response_.len());
                this.state_ = 4;
            }
            this.requestSelectInterestChange();
        }
        
        public void invoke() {
            final TTransport inTrans = this.getInputTransport();
            final TProtocol inProt = TNonblockingServer.this.inputProtocolFactory_.getProtocol(inTrans);
            final TProtocol outProt = TNonblockingServer.this.outputProtocolFactory_.getProtocol(this.getOutputTransport());
            try {
                TNonblockingServer.this.processorFactory_.getProcessor(inTrans).process(inProt, outProt);
                this.responseReady();
                return;
            }
            catch (TException te) {
                TNonblockingServer.LOGGER.warn("Exception while invoking!", te);
            }
            catch (Exception e) {
                TNonblockingServer.LOGGER.error("Unexpected exception while invoking!", e);
            }
            this.state_ = 8;
            this.requestSelectInterestChange();
        }
        
        private TTransport getInputTransport() {
            return new TMemoryInputTransport(this.buffer_.array());
        }
        
        private TTransport getOutputTransport() {
            this.response_ = new TByteArrayOutputStream();
            return TNonblockingServer.this.outputTransportFactory_.getTransport(new TIOStreamTransport(this.response_));
        }
        
        private boolean internalRead() {
            try {
                return this.trans_.read(this.buffer_) >= 0;
            }
            catch (IOException e) {
                TNonblockingServer.LOGGER.warn("Got an IOException in internalRead!", e);
                return false;
            }
        }
        
        private void prepareRead() {
            this.selectionKey_.interestOps(1);
            this.buffer_ = ByteBuffer.allocate(4);
            this.state_ = 1;
        }
        
        private void requestSelectInterestChange() {
            if (Thread.currentThread() == TNonblockingServer.this.selectThread_) {
                this.changeSelectInterests();
            }
            else {
                TNonblockingServer.this.requestSelectInterestChange(this);
            }
        }
    }
}
