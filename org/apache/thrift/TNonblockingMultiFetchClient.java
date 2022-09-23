// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.thrift;

import java.util.Iterator;
import java.nio.channels.SelectionKey;
import java.net.SocketAddress;
import java.nio.channels.SocketChannel;
import java.io.IOException;
import java.nio.channels.Selector;
import org.slf4j.LoggerFactory;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.FutureTask;
import java.util.concurrent.Executors;
import java.util.Collections;
import java.net.InetSocketAddress;
import java.util.List;
import java.nio.ByteBuffer;
import org.slf4j.Logger;

public class TNonblockingMultiFetchClient
{
    private static final Logger LOGGER;
    private int maxRecvBufBytesPerServer;
    private int fetchTimeoutSeconds;
    private ByteBuffer requestBuf;
    private ByteBuffer requestBufDuplication;
    private List<InetSocketAddress> servers;
    private TNonblockingMultiFetchStats stats;
    private ByteBuffer[] recvBuf;
    
    public TNonblockingMultiFetchClient(final int maxRecvBufBytesPerServer, final int fetchTimeoutSeconds, final ByteBuffer requestBuf, final List<InetSocketAddress> servers) {
        this.maxRecvBufBytesPerServer = maxRecvBufBytesPerServer;
        this.fetchTimeoutSeconds = fetchTimeoutSeconds;
        this.requestBuf = requestBuf;
        this.servers = servers;
        this.stats = new TNonblockingMultiFetchStats();
        this.recvBuf = null;
    }
    
    public synchronized int getMaxRecvBufBytesPerServer() {
        return this.maxRecvBufBytesPerServer;
    }
    
    public synchronized int getFetchTimeoutSeconds() {
        return this.fetchTimeoutSeconds;
    }
    
    public synchronized ByteBuffer getRequestBuf() {
        if (this.requestBuf == null) {
            return null;
        }
        if (this.requestBufDuplication == null) {
            this.requestBufDuplication = this.requestBuf.duplicate();
        }
        return this.requestBufDuplication;
    }
    
    public synchronized List<InetSocketAddress> getServerList() {
        if (this.servers == null) {
            return null;
        }
        return Collections.unmodifiableList((List<? extends InetSocketAddress>)this.servers);
    }
    
    public synchronized TNonblockingMultiFetchStats getFetchStats() {
        return this.stats;
    }
    
    public synchronized ByteBuffer[] fetch() {
        this.recvBuf = null;
        this.stats.clear();
        if (this.servers == null || this.servers.size() == 0 || this.requestBuf == null || this.fetchTimeoutSeconds <= 0) {
            return this.recvBuf;
        }
        final ExecutorService executor = Executors.newSingleThreadExecutor();
        final MultiFetch multiFetch = new MultiFetch();
        final FutureTask<?> task = new FutureTask<Object>(multiFetch, null);
        executor.execute(task);
        try {
            task.get(this.fetchTimeoutSeconds, TimeUnit.SECONDS);
        }
        catch (InterruptedException ie) {
            task.cancel(true);
            TNonblockingMultiFetchClient.LOGGER.error("interrupted during fetch: " + ie.toString());
        }
        catch (ExecutionException ee) {
            task.cancel(true);
            TNonblockingMultiFetchClient.LOGGER.error("exception during fetch: " + ee.toString());
        }
        catch (TimeoutException te) {
            task.cancel(true);
            TNonblockingMultiFetchClient.LOGGER.error("timeout for fetch: " + te.toString());
        }
        executor.shutdownNow();
        multiFetch.close();
        return this.recvBuf;
    }
    
    static {
        LOGGER = LoggerFactory.getLogger(TNonblockingMultiFetchClient.class.getName());
    }
    
    private class MultiFetch implements Runnable
    {
        private Selector selector;
        
        public void run() {
            final long t1 = System.currentTimeMillis();
            final int numTotalServers = TNonblockingMultiFetchClient.this.servers.size();
            TNonblockingMultiFetchClient.this.stats.setNumTotalServers(numTotalServers);
            TNonblockingMultiFetchClient.this.recvBuf = new ByteBuffer[numTotalServers];
            final ByteBuffer[] sendBuf = new ByteBuffer[numTotalServers];
            final long[] numBytesRead = new long[numTotalServers];
            final int[] frameSize = new int[numTotalServers];
            final boolean[] hasReadFrameSize = new boolean[numTotalServers];
            try {
                this.selector = Selector.open();
            }
            catch (IOException e) {
                TNonblockingMultiFetchClient.LOGGER.error("selector opens error: " + e.toString());
                return;
            }
            for (int i = 0; i < numTotalServers; ++i) {
                sendBuf[i] = TNonblockingMultiFetchClient.this.requestBuf.duplicate();
                TNonblockingMultiFetchClient.this.recvBuf[i] = ByteBuffer.allocate(4);
                TNonblockingMultiFetchClient.this.stats.incTotalRecvBufBytes(4);
                final InetSocketAddress server = TNonblockingMultiFetchClient.this.servers.get(i);
                SocketChannel s = null;
                SelectionKey key = null;
                try {
                    s = SocketChannel.open();
                    s.configureBlocking(false);
                    s.connect(server);
                    key = s.register(this.selector, s.validOps());
                    key.attach(i);
                }
                catch (Exception e2) {
                    TNonblockingMultiFetchClient.this.stats.incNumConnectErrorServers();
                    final String err = String.format("set up socket to server %s error: %s", server.toString(), e2.toString());
                    TNonblockingMultiFetchClient.LOGGER.error(err);
                    if (s != null) {
                        try {
                            s.close();
                        }
                        catch (Exception ex) {}
                    }
                    if (key != null) {
                        key.cancel();
                    }
                }
            }
            while (TNonblockingMultiFetchClient.this.stats.getNumReadCompletedServers() + TNonblockingMultiFetchClient.this.stats.getNumConnectErrorServers() < TNonblockingMultiFetchClient.this.stats.getNumTotalServers()) {
                if (Thread.currentThread().isInterrupted()) {
                    return;
                }
                try {
                    this.selector.select();
                }
                catch (Exception e3) {
                    TNonblockingMultiFetchClient.LOGGER.error("selector selects error: " + e3.toString());
                    continue;
                }
                final Iterator<SelectionKey> it = this.selector.selectedKeys().iterator();
                while (it.hasNext()) {
                    final SelectionKey selKey = it.next();
                    it.remove();
                    final int index = (int)selKey.attachment();
                    if (selKey.isValid() && selKey.isConnectable()) {
                        try {
                            final SocketChannel sChannel = (SocketChannel)selKey.channel();
                            sChannel.finishConnect();
                        }
                        catch (Exception e4) {
                            TNonblockingMultiFetchClient.this.stats.incNumConnectErrorServers();
                            final String err2 = String.format("socket %d connects to server %s error: %s", index, TNonblockingMultiFetchClient.this.servers.get(index).toString(), e4.toString());
                            TNonblockingMultiFetchClient.LOGGER.error(err2);
                        }
                    }
                    if (selKey.isValid() && selKey.isWritable() && sendBuf[index].hasRemaining()) {
                        try {
                            final SocketChannel sChannel = (SocketChannel)selKey.channel();
                            sChannel.write(sendBuf[index]);
                        }
                        catch (Exception e4) {
                            final String err2 = String.format("socket %d writes to server %s error: %s", index, TNonblockingMultiFetchClient.this.servers.get(index).toString(), e4.toString());
                            TNonblockingMultiFetchClient.LOGGER.error(err2);
                        }
                    }
                    if (selKey.isValid() && selKey.isReadable()) {
                        try {
                            final SocketChannel sChannel = (SocketChannel)selKey.channel();
                            final int bytesRead = sChannel.read(TNonblockingMultiFetchClient.this.recvBuf[index]);
                            if (bytesRead <= 0) {
                                continue;
                            }
                            final long[] array = numBytesRead;
                            final int n = index;
                            array[n] += bytesRead;
                            if (!hasReadFrameSize[index] && TNonblockingMultiFetchClient.this.recvBuf[index].remaining() == 0) {
                                frameSize[index] = TNonblockingMultiFetchClient.this.recvBuf[index].getInt(0);
                                if (frameSize[index] <= 0) {
                                    TNonblockingMultiFetchClient.this.stats.incNumInvalidFrameSize();
                                    final String err = String.format("Read an invalid frame size %d from %s. Does the server use TFramedTransport? ", frameSize[index], TNonblockingMultiFetchClient.this.servers.get(index).toString());
                                    TNonblockingMultiFetchClient.LOGGER.error(err);
                                    sChannel.close();
                                    continue;
                                }
                                if (frameSize[index] + 4 > TNonblockingMultiFetchClient.this.stats.getMaxResponseBytes()) {
                                    TNonblockingMultiFetchClient.this.stats.setMaxResponseBytes(frameSize[index] + 4);
                                }
                                if (frameSize[index] + 4 > TNonblockingMultiFetchClient.this.maxRecvBufBytesPerServer) {
                                    TNonblockingMultiFetchClient.this.stats.incNumOverflowedRecvBuf();
                                    final String err = String.format("Read frame size %d from %s, total buffer size would exceed limit %d", frameSize[index], TNonblockingMultiFetchClient.this.servers.get(index).toString(), TNonblockingMultiFetchClient.this.maxRecvBufBytesPerServer);
                                    TNonblockingMultiFetchClient.LOGGER.error(err);
                                    sChannel.close();
                                    continue;
                                }
                                (TNonblockingMultiFetchClient.this.recvBuf[index] = ByteBuffer.allocate(frameSize[index] + 4)).putInt(frameSize[index]);
                                TNonblockingMultiFetchClient.this.stats.incTotalRecvBufBytes(frameSize[index]);
                                hasReadFrameSize[index] = true;
                            }
                            if (!hasReadFrameSize[index] || numBytesRead[index] < frameSize[index] + 4) {
                                continue;
                            }
                            sChannel.close();
                            TNonblockingMultiFetchClient.this.stats.incNumReadCompletedServers();
                            final long t2 = System.currentTimeMillis();
                            TNonblockingMultiFetchClient.this.stats.setReadTime(t2 - t1);
                        }
                        catch (Exception e4) {
                            final String err2 = String.format("socket %d reads from server %s error: %s", index, TNonblockingMultiFetchClient.this.servers.get(index).toString(), e4.toString());
                            TNonblockingMultiFetchClient.LOGGER.error(err2);
                        }
                    }
                }
            }
        }
        
        public void close() {
            try {
                if (this.selector.isOpen()) {
                    for (final SelectionKey selKey : this.selector.keys()) {
                        final SocketChannel sChannel = (SocketChannel)selKey.channel();
                        sChannel.close();
                    }
                    this.selector.close();
                }
            }
            catch (IOException e) {
                TNonblockingMultiFetchClient.LOGGER.error("free resource error: " + e.toString());
            }
        }
    }
}
