// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.zookeeper.server;

import org.jboss.netty.channel.WriteCompletionEvent;
import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.ExceptionEvent;
import org.jboss.netty.channel.ChannelStateEvent;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.SimpleChannelHandler;
import java.util.HashSet;
import java.net.SocketAddress;
import java.io.IOException;
import org.jboss.netty.channel.ChannelHandler;
import org.jboss.netty.channel.ChannelFactory;
import java.util.concurrent.Executor;
import org.jboss.netty.channel.socket.nio.NioServerSocketChannelFactory;
import java.util.concurrent.Executors;
import org.jboss.netty.channel.group.DefaultChannelGroup;
import org.slf4j.LoggerFactory;
import java.net.InetSocketAddress;
import java.util.Set;
import java.net.InetAddress;
import java.util.HashMap;
import org.jboss.netty.channel.group.ChannelGroup;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.bootstrap.ServerBootstrap;
import org.slf4j.Logger;

public class NettyServerCnxnFactory extends ServerCnxnFactory
{
    Logger LOG;
    ServerBootstrap bootstrap;
    Channel parentChannel;
    ChannelGroup allChannels;
    HashMap<InetAddress, Set<NettyServerCnxn>> ipMap;
    InetSocketAddress localAddress;
    int maxClientCnxns;
    CnxnChannelHandler channelHandler;
    boolean killed;
    
    NettyServerCnxnFactory() {
        this.LOG = LoggerFactory.getLogger(NettyServerCnxnFactory.class);
        this.allChannels = new DefaultChannelGroup("zkServerCnxns");
        this.ipMap = new HashMap<InetAddress, Set<NettyServerCnxn>>();
        this.maxClientCnxns = 60;
        this.channelHandler = new CnxnChannelHandler();
        (this.bootstrap = new ServerBootstrap(new NioServerSocketChannelFactory(Executors.newCachedThreadPool(), Executors.newCachedThreadPool()))).setOption("reuseAddress", true);
        this.bootstrap.setOption("child.tcpNoDelay", true);
        this.bootstrap.setOption("child.soLinger", -1);
        this.bootstrap.getPipeline().addLast("servercnxnfactory", this.channelHandler);
    }
    
    @Override
    public void closeAll() {
        if (this.LOG.isDebugEnabled()) {
            this.LOG.debug("closeAll()");
        }
        NettyServerCnxn[] allCnxns = null;
        synchronized (this.cnxns) {
            allCnxns = this.cnxns.toArray(new NettyServerCnxn[this.cnxns.size()]);
        }
        for (final NettyServerCnxn cnxn : allCnxns) {
            try {
                cnxn.close();
            }
            catch (Exception e) {
                this.LOG.warn("Ignoring exception closing cnxn sessionid 0x" + Long.toHexString(cnxn.getSessionId()), e);
            }
        }
        if (this.LOG.isDebugEnabled()) {
            this.LOG.debug("allChannels size:" + this.allChannels.size() + " cnxns size:" + allCnxns.length);
        }
    }
    
    @Override
    public void closeSession(final long sessionId) {
        if (this.LOG.isDebugEnabled()) {
            this.LOG.debug("closeSession sessionid:0x" + sessionId);
        }
        final NettyServerCnxn cnxn = this.sessionMap.remove(sessionId);
        if (cnxn != null) {
            try {
                cnxn.close();
            }
            catch (Exception e) {
                this.LOG.warn("exception during session close", e);
            }
        }
    }
    
    @Override
    public void configure(final InetSocketAddress addr, final int maxClientCnxns) throws IOException {
        this.configureSaslLogin();
        this.localAddress = addr;
        this.maxClientCnxns = maxClientCnxns;
    }
    
    @Override
    public int getMaxClientCnxnsPerHost() {
        return this.maxClientCnxns;
    }
    
    @Override
    public void setMaxClientCnxnsPerHost(final int max) {
        this.maxClientCnxns = max;
    }
    
    @Override
    public int getLocalPort() {
        return this.localAddress.getPort();
    }
    
    @Override
    public void join() throws InterruptedException {
        synchronized (this) {
            while (!this.killed) {
                this.wait();
            }
        }
    }
    
    @Override
    public void shutdown() {
        this.LOG.info("shutdown called " + this.localAddress);
        if (this.login != null) {
            this.login.shutdown();
        }
        if (this.parentChannel != null) {
            this.parentChannel.close().awaitUninterruptibly();
            this.closeAll();
            this.allChannels.close().awaitUninterruptibly();
            this.bootstrap.releaseExternalResources();
        }
        if (this.zkServer != null) {
            this.zkServer.shutdown();
        }
        synchronized (this) {
            this.killed = true;
            this.notifyAll();
        }
    }
    
    @Override
    public void start() {
        this.LOG.info("binding to port " + this.localAddress);
        this.parentChannel = this.bootstrap.bind(this.localAddress);
    }
    
    @Override
    public void startup(final ZooKeeperServer zks) throws IOException, InterruptedException {
        this.start();
        this.setZooKeeperServer(zks);
        zks.startdata();
        zks.startup();
    }
    
    @Override
    public Iterable<ServerCnxn> getConnections() {
        return this.cnxns;
    }
    
    @Override
    public InetSocketAddress getLocalAddress() {
        return this.localAddress;
    }
    
    private void addCnxn(final NettyServerCnxn cnxn) {
        synchronized (this.cnxns) {
            this.cnxns.add(cnxn);
            synchronized (this.ipMap) {
                final InetAddress addr = ((InetSocketAddress)cnxn.channel.getRemoteAddress()).getAddress();
                Set<NettyServerCnxn> s = this.ipMap.get(addr);
                if (s == null) {
                    s = new HashSet<NettyServerCnxn>();
                }
                s.add(cnxn);
                this.ipMap.put(addr, s);
            }
        }
    }
    
    public void removeCnxn(final ServerCnxn cnxn) {
        synchronized (this.cnxns) {
            if (!this.cnxns.remove(cnxn)) {
                if (this.LOG.isDebugEnabled()) {
                    this.LOG.debug("cnxns size:" + this.cnxns.size());
                }
                return;
            }
            if (this.LOG.isDebugEnabled()) {
                this.LOG.debug("close in progress for sessionid:0x" + Long.toHexString(cnxn.getSessionId()));
            }
            synchronized (this.ipMap) {
                final Set<NettyServerCnxn> s = this.ipMap.get(cnxn.getSocketAddress());
                s.remove(cnxn);
            }
        }
    }
    
    @ChannelHandler.Sharable
    class CnxnChannelHandler extends SimpleChannelHandler
    {
        @Override
        public void channelClosed(final ChannelHandlerContext ctx, final ChannelStateEvent e) throws Exception {
            if (NettyServerCnxnFactory.this.LOG.isTraceEnabled()) {
                NettyServerCnxnFactory.this.LOG.trace("Channel closed " + e);
            }
            NettyServerCnxnFactory.this.allChannels.remove(ctx.getChannel());
        }
        
        @Override
        public void channelConnected(final ChannelHandlerContext ctx, final ChannelStateEvent e) throws Exception {
            if (NettyServerCnxnFactory.this.LOG.isTraceEnabled()) {
                NettyServerCnxnFactory.this.LOG.trace("Channel connected " + e);
            }
            NettyServerCnxnFactory.this.allChannels.add(ctx.getChannel());
            final NettyServerCnxn cnxn = new NettyServerCnxn(ctx.getChannel(), NettyServerCnxnFactory.this.zkServer, NettyServerCnxnFactory.this);
            ctx.setAttachment(cnxn);
            NettyServerCnxnFactory.this.addCnxn(cnxn);
        }
        
        @Override
        public void channelDisconnected(final ChannelHandlerContext ctx, final ChannelStateEvent e) throws Exception {
            if (NettyServerCnxnFactory.this.LOG.isTraceEnabled()) {
                NettyServerCnxnFactory.this.LOG.trace("Channel disconnected " + e);
            }
            final NettyServerCnxn cnxn = (NettyServerCnxn)ctx.getAttachment();
            if (cnxn != null) {
                if (NettyServerCnxnFactory.this.LOG.isTraceEnabled()) {
                    NettyServerCnxnFactory.this.LOG.trace("Channel disconnect caused close " + e);
                }
                cnxn.close();
            }
        }
        
        @Override
        public void exceptionCaught(final ChannelHandlerContext ctx, final ExceptionEvent e) throws Exception {
            NettyServerCnxnFactory.this.LOG.warn("Exception caught " + e, e.getCause());
            final NettyServerCnxn cnxn = (NettyServerCnxn)ctx.getAttachment();
            if (cnxn != null) {
                if (NettyServerCnxnFactory.this.LOG.isDebugEnabled()) {
                    NettyServerCnxnFactory.this.LOG.debug("Closing " + cnxn);
                }
                cnxn.close();
            }
        }
        
        @Override
        public void messageReceived(final ChannelHandlerContext ctx, final MessageEvent e) throws Exception {
            if (NettyServerCnxnFactory.this.LOG.isTraceEnabled()) {
                NettyServerCnxnFactory.this.LOG.trace("message received called " + e.getMessage());
            }
            try {
                if (NettyServerCnxnFactory.this.LOG.isDebugEnabled()) {
                    NettyServerCnxnFactory.this.LOG.debug("New message " + e.toString() + " from " + ctx.getChannel());
                }
                final NettyServerCnxn cnxn = (NettyServerCnxn)ctx.getAttachment();
                synchronized (cnxn) {
                    this.processMessage(e, cnxn);
                }
            }
            catch (Exception ex) {
                NettyServerCnxnFactory.this.LOG.error("Unexpected exception in receive", ex);
                throw ex;
            }
        }
        
        private void processMessage(final MessageEvent e, final NettyServerCnxn cnxn) {
            if (NettyServerCnxnFactory.this.LOG.isDebugEnabled()) {
                NettyServerCnxnFactory.this.LOG.debug(Long.toHexString(cnxn.sessionId) + " queuedBuffer: " + cnxn.queuedBuffer);
            }
            if (e instanceof NettyServerCnxn.ResumeMessageEvent) {
                NettyServerCnxnFactory.this.LOG.debug("Received ResumeMessageEvent");
                if (cnxn.queuedBuffer != null) {
                    if (NettyServerCnxnFactory.this.LOG.isTraceEnabled()) {
                        NettyServerCnxnFactory.this.LOG.trace("processing queue " + Long.toHexString(cnxn.sessionId) + " queuedBuffer 0x" + ChannelBuffers.hexDump(cnxn.queuedBuffer));
                    }
                    cnxn.receiveMessage(cnxn.queuedBuffer);
                    if (!cnxn.queuedBuffer.readable()) {
                        NettyServerCnxnFactory.this.LOG.debug("Processed queue - no bytes remaining");
                        cnxn.queuedBuffer = null;
                    }
                    else {
                        NettyServerCnxnFactory.this.LOG.debug("Processed queue - bytes remaining");
                    }
                }
                else {
                    NettyServerCnxnFactory.this.LOG.debug("queue empty");
                }
                cnxn.channel.setReadable(true);
            }
            else {
                final ChannelBuffer buf = (ChannelBuffer)e.getMessage();
                if (NettyServerCnxnFactory.this.LOG.isTraceEnabled()) {
                    NettyServerCnxnFactory.this.LOG.trace(Long.toHexString(cnxn.sessionId) + " buf 0x" + ChannelBuffers.hexDump(buf));
                }
                if (cnxn.throttled) {
                    NettyServerCnxnFactory.this.LOG.debug("Received message while throttled");
                    if (cnxn.queuedBuffer == null) {
                        NettyServerCnxnFactory.this.LOG.debug("allocating queue");
                        cnxn.queuedBuffer = ChannelBuffers.dynamicBuffer(buf.readableBytes());
                    }
                    cnxn.queuedBuffer.writeBytes(buf);
                    if (NettyServerCnxnFactory.this.LOG.isTraceEnabled()) {
                        NettyServerCnxnFactory.this.LOG.trace(Long.toHexString(cnxn.sessionId) + " queuedBuffer 0x" + ChannelBuffers.hexDump(cnxn.queuedBuffer));
                    }
                }
                else {
                    NettyServerCnxnFactory.this.LOG.debug("not throttled");
                    if (cnxn.queuedBuffer != null) {
                        if (NettyServerCnxnFactory.this.LOG.isTraceEnabled()) {
                            NettyServerCnxnFactory.this.LOG.trace(Long.toHexString(cnxn.sessionId) + " queuedBuffer 0x" + ChannelBuffers.hexDump(cnxn.queuedBuffer));
                        }
                        cnxn.queuedBuffer.writeBytes(buf);
                        if (NettyServerCnxnFactory.this.LOG.isTraceEnabled()) {
                            NettyServerCnxnFactory.this.LOG.trace(Long.toHexString(cnxn.sessionId) + " queuedBuffer 0x" + ChannelBuffers.hexDump(cnxn.queuedBuffer));
                        }
                        cnxn.receiveMessage(cnxn.queuedBuffer);
                        if (!cnxn.queuedBuffer.readable()) {
                            NettyServerCnxnFactory.this.LOG.debug("Processed queue - no bytes remaining");
                            cnxn.queuedBuffer = null;
                        }
                        else {
                            NettyServerCnxnFactory.this.LOG.debug("Processed queue - bytes remaining");
                        }
                    }
                    else {
                        cnxn.receiveMessage(buf);
                        if (buf.readable()) {
                            if (NettyServerCnxnFactory.this.LOG.isTraceEnabled()) {
                                NettyServerCnxnFactory.this.LOG.trace("Before copy " + buf);
                            }
                            (cnxn.queuedBuffer = ChannelBuffers.dynamicBuffer(buf.readableBytes())).writeBytes(buf);
                            if (NettyServerCnxnFactory.this.LOG.isTraceEnabled()) {
                                NettyServerCnxnFactory.this.LOG.trace("Copy is " + cnxn.queuedBuffer);
                                NettyServerCnxnFactory.this.LOG.trace(Long.toHexString(cnxn.sessionId) + " queuedBuffer 0x" + ChannelBuffers.hexDump(cnxn.queuedBuffer));
                            }
                        }
                    }
                }
            }
        }
        
        @Override
        public void writeComplete(final ChannelHandlerContext ctx, final WriteCompletionEvent e) throws Exception {
            if (NettyServerCnxnFactory.this.LOG.isTraceEnabled()) {
                NettyServerCnxnFactory.this.LOG.trace("write complete " + e);
            }
        }
    }
}
