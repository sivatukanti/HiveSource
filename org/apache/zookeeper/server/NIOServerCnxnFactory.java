// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.zookeeper.server;

import org.slf4j.LoggerFactory;
import java.util.Iterator;
import java.util.List;
import java.util.Collections;
import java.util.Collection;
import java.util.ArrayList;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import java.util.HashSet;
import java.net.SocketAddress;
import java.net.InetSocketAddress;
import java.io.IOException;
import java.util.Set;
import java.net.InetAddress;
import java.util.HashMap;
import java.nio.ByteBuffer;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import org.slf4j.Logger;

public class NIOServerCnxnFactory extends ServerCnxnFactory implements Runnable
{
    private static final Logger LOG;
    ServerSocketChannel ss;
    final Selector selector;
    final ByteBuffer directBuffer;
    final HashMap<InetAddress, Set<NIOServerCnxn>> ipMap;
    int maxClientCnxns;
    Thread thread;
    
    public NIOServerCnxnFactory() throws IOException {
        this.selector = Selector.open();
        this.directBuffer = ByteBuffer.allocateDirect(65536);
        this.ipMap = new HashMap<InetAddress, Set<NIOServerCnxn>>();
        this.maxClientCnxns = 60;
    }
    
    @Override
    public void configure(final InetSocketAddress addr, final int maxcc) throws IOException {
        this.configureSaslLogin();
        (this.thread = new ZooKeeperThread(this, "NIOServerCxn.Factory:" + addr)).setDaemon(true);
        this.maxClientCnxns = maxcc;
        this.ss = ServerSocketChannel.open();
        this.ss.socket().setReuseAddress(true);
        NIOServerCnxnFactory.LOG.info("binding to port " + addr);
        this.ss.socket().bind(addr);
        this.ss.configureBlocking(false);
        this.ss.register(this.selector, 16);
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
    public void start() {
        if (this.thread.getState() == Thread.State.NEW) {
            this.thread.start();
        }
    }
    
    @Override
    public void startup(final ZooKeeperServer zks) throws IOException, InterruptedException {
        this.start();
        this.setZooKeeperServer(zks);
        zks.startdata();
        zks.startup();
    }
    
    @Override
    public InetSocketAddress getLocalAddress() {
        return (InetSocketAddress)this.ss.socket().getLocalSocketAddress();
    }
    
    @Override
    public int getLocalPort() {
        return this.ss.socket().getLocalPort();
    }
    
    private void addCnxn(final NIOServerCnxn cnxn) {
        synchronized (this.cnxns) {
            this.cnxns.add(cnxn);
            synchronized (this.ipMap) {
                final InetAddress addr = cnxn.sock.socket().getInetAddress();
                Set<NIOServerCnxn> s = this.ipMap.get(addr);
                if (s == null) {
                    s = new HashSet<NIOServerCnxn>(2);
                    s.add(cnxn);
                    this.ipMap.put(addr, s);
                }
                else {
                    s.add(cnxn);
                }
            }
        }
    }
    
    public void removeCnxn(final NIOServerCnxn cnxn) {
        synchronized (this.cnxns) {
            final long sessionId = cnxn.getSessionId();
            if (sessionId != 0L) {
                this.sessionMap.remove(sessionId);
            }
            if (!this.cnxns.remove(cnxn)) {
                return;
            }
            synchronized (this.ipMap) {
                final Set<NIOServerCnxn> s = this.ipMap.get(cnxn.getSocketAddress());
                s.remove(cnxn);
            }
            this.unregisterConnection(cnxn);
        }
    }
    
    protected NIOServerCnxn createConnection(final SocketChannel sock, final SelectionKey sk) throws IOException {
        return new NIOServerCnxn(this.zkServer, sock, sk, this);
    }
    
    private int getClientCnxnCount(final InetAddress cl) {
        synchronized (this.ipMap) {
            final Set<NIOServerCnxn> s = this.ipMap.get(cl);
            if (s == null) {
                return 0;
            }
            return s.size();
        }
    }
    
    @Override
    public void run() {
        while (!this.ss.socket().isClosed()) {
            try {
                this.selector.select(1000L);
                final Set<SelectionKey> selected;
                synchronized (this) {
                    selected = this.selector.selectedKeys();
                }
                final ArrayList<SelectionKey> selectedList = new ArrayList<SelectionKey>(selected);
                Collections.shuffle(selectedList);
                for (final SelectionKey k : selectedList) {
                    if ((k.readyOps() & 0x10) != 0x0) {
                        final SocketChannel sc = ((ServerSocketChannel)k.channel()).accept();
                        final InetAddress ia = sc.socket().getInetAddress();
                        final int cnxncount = this.getClientCnxnCount(ia);
                        if (this.maxClientCnxns > 0 && cnxncount >= this.maxClientCnxns) {
                            NIOServerCnxnFactory.LOG.warn("Too many connections from " + ia + " - max is " + this.maxClientCnxns);
                            sc.close();
                        }
                        else {
                            NIOServerCnxnFactory.LOG.info("Accepted socket connection from " + sc.socket().getRemoteSocketAddress());
                            sc.configureBlocking(false);
                            final SelectionKey sk = sc.register(this.selector, 1);
                            final NIOServerCnxn cnxn = this.createConnection(sc, sk);
                            sk.attach(cnxn);
                            this.addCnxn(cnxn);
                        }
                    }
                    else if ((k.readyOps() & 0x5) != 0x0) {
                        final NIOServerCnxn c = (NIOServerCnxn)k.attachment();
                        c.doIO(k);
                    }
                    else {
                        if (!NIOServerCnxnFactory.LOG.isDebugEnabled()) {
                            continue;
                        }
                        NIOServerCnxnFactory.LOG.debug("Unexpected ops in select " + k.readyOps());
                    }
                }
                selected.clear();
            }
            catch (RuntimeException e) {
                NIOServerCnxnFactory.LOG.warn("Ignoring unexpected runtime exception", e);
            }
            catch (Exception e2) {
                NIOServerCnxnFactory.LOG.warn("Ignoring exception", e2);
            }
        }
        this.closeAll();
        NIOServerCnxnFactory.LOG.info("NIOServerCnxn factory exited run method");
    }
    
    @Override
    public synchronized void closeAll() {
        this.selector.wakeup();
        final HashSet<NIOServerCnxn> cnxns;
        synchronized (this.cnxns) {
            cnxns = (HashSet<NIOServerCnxn>)this.cnxns.clone();
        }
        for (final NIOServerCnxn cnxn : cnxns) {
            try {
                cnxn.close();
            }
            catch (Exception e) {
                NIOServerCnxnFactory.LOG.warn("Ignoring exception closing cnxn sessionid 0x" + Long.toHexString(cnxn.sessionId), e);
            }
        }
    }
    
    @Override
    public void shutdown() {
        try {
            this.ss.close();
            this.closeAll();
            this.thread.interrupt();
            this.thread.join();
            if (this.login != null) {
                this.login.shutdown();
            }
        }
        catch (InterruptedException e) {
            NIOServerCnxnFactory.LOG.warn("Ignoring interrupted exception during shutdown", e);
        }
        catch (Exception e2) {
            NIOServerCnxnFactory.LOG.warn("Ignoring unexpected exception during shutdown", e2);
        }
        try {
            this.selector.close();
        }
        catch (IOException e3) {
            NIOServerCnxnFactory.LOG.warn("Selector closing", e3);
        }
        if (this.zkServer != null) {
            this.zkServer.shutdown();
        }
    }
    
    @Override
    public synchronized void closeSession(final long sessionId) {
        this.selector.wakeup();
        this.closeSessionWithoutWakeup(sessionId);
    }
    
    private void closeSessionWithoutWakeup(final long sessionId) {
        final NIOServerCnxn cnxn = this.sessionMap.remove(sessionId);
        if (cnxn != null) {
            try {
                cnxn.close();
            }
            catch (Exception e) {
                NIOServerCnxnFactory.LOG.warn("exception during session close", e);
            }
        }
    }
    
    @Override
    public void join() throws InterruptedException {
        this.thread.join();
    }
    
    @Override
    public Iterable<ServerCnxn> getConnections() {
        return this.cnxns;
    }
    
    static {
        LOG = LoggerFactory.getLogger(NIOServerCnxnFactory.class);
        try {
            Selector.open().close();
        }
        catch (IOException ie) {
            NIOServerCnxnFactory.LOG.error("Selector failed to open", ie);
        }
    }
}
