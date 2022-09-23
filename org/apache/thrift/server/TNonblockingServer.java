// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.thrift.server;

import org.apache.thrift.transport.TTransportException;
import org.apache.thrift.transport.TNonblockingTransport;
import java.util.Iterator;
import java.nio.channels.SelectionKey;
import java.io.IOException;
import org.apache.thrift.transport.TNonblockingServerTransport;

public class TNonblockingServer extends AbstractNonblockingServer
{
    private volatile boolean stopped_;
    private SelectAcceptThread selectAcceptThread_;
    
    public TNonblockingServer(final AbstractNonblockingServerArgs args) {
        super(args);
        this.stopped_ = false;
    }
    
    @Override
    protected boolean startThreads() {
        try {
            (this.selectAcceptThread_ = new SelectAcceptThread((TNonblockingServerTransport)this.serverTransport_)).start();
            return true;
        }
        catch (IOException e) {
            this.LOGGER.error("Failed to start selector thread!", e);
            return false;
        }
    }
    
    @Override
    protected void waitForShutdown() {
        this.joinSelector();
    }
    
    protected void joinSelector() {
        try {
            this.selectAcceptThread_.join();
        }
        catch (InterruptedException ex) {}
    }
    
    @Override
    public void stop() {
        this.stopped_ = true;
        if (this.selectAcceptThread_ != null) {
            this.selectAcceptThread_.wakeupSelector();
        }
    }
    
    @Override
    protected boolean requestInvoke(final FrameBuffer frameBuffer) {
        frameBuffer.invoke();
        return true;
    }
    
    public boolean isStopped() {
        return this.selectAcceptThread_.isStopped();
    }
    
    public static class Args extends AbstractNonblockingServerArgs<Args>
    {
        public Args(final TNonblockingServerTransport transport) {
            super(transport);
        }
    }
    
    protected class SelectAcceptThread extends AbstractSelectThread
    {
        private final TNonblockingServerTransport serverTransport;
        
        public SelectAcceptThread(final TNonblockingServerTransport serverTransport) throws IOException {
            (this.serverTransport = serverTransport).registerSelector(this.selector);
        }
        
        public boolean isStopped() {
            return TNonblockingServer.this.stopped_;
        }
        
        @Override
        public void run() {
            try {
                if (TNonblockingServer.this.eventHandler_ != null) {
                    TNonblockingServer.this.eventHandler_.preServe();
                }
                while (!TNonblockingServer.this.stopped_) {
                    this.select();
                    this.processInterestChanges();
                }
                for (final SelectionKey selectionKey : this.selector.keys()) {
                    this.cleanupSelectionKey(selectionKey);
                }
            }
            catch (Throwable t) {
                TNonblockingServer.this.LOGGER.error("run() exiting due to uncaught error", t);
            }
            finally {
                try {
                    this.selector.close();
                }
                catch (IOException e) {
                    TNonblockingServer.this.LOGGER.error("Got an IOException while closing selector!", e);
                }
                TNonblockingServer.this.stopped_ = true;
            }
        }
        
        private void select() {
            try {
                this.selector.select();
                final Iterator<SelectionKey> selectedKeys = this.selector.selectedKeys().iterator();
                while (!TNonblockingServer.this.stopped_ && selectedKeys.hasNext()) {
                    final SelectionKey key = selectedKeys.next();
                    selectedKeys.remove();
                    if (!key.isValid()) {
                        this.cleanupSelectionKey(key);
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
                        TNonblockingServer.this.LOGGER.warn("Unexpected state in select! " + key.interestOps());
                    }
                }
            }
            catch (IOException e) {
                TNonblockingServer.this.LOGGER.warn("Got an IOException while selecting!", e);
            }
        }
        
        protected FrameBuffer createFrameBuffer(final TNonblockingTransport trans, final SelectionKey selectionKey, final AbstractSelectThread selectThread) {
            return TNonblockingServer.this.processorFactory_.isAsyncProcessor() ? new AsyncFrameBuffer(trans, selectionKey, selectThread) : new FrameBuffer(trans, selectionKey, selectThread);
        }
        
        private void handleAccept() throws IOException {
            SelectionKey clientKey = null;
            TNonblockingTransport client = null;
            try {
                client = (TNonblockingTransport)this.serverTransport.accept();
                clientKey = client.registerSelector(this.selector, 1);
                final FrameBuffer frameBuffer = this.createFrameBuffer(client, clientKey, this);
                clientKey.attach(frameBuffer);
            }
            catch (TTransportException tte) {
                TNonblockingServer.this.LOGGER.warn("Exception trying to accept!", tte);
                tte.printStackTrace();
                if (clientKey != null) {
                    this.cleanupSelectionKey(clientKey);
                }
                if (client != null) {
                    client.close();
                }
            }
        }
    }
}
