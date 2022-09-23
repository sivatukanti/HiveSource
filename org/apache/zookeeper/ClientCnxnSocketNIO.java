// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.zookeeper;

import org.slf4j.LoggerFactory;
import java.nio.ByteBuffer;
import java.util.Iterator;
import java.util.Set;
import java.net.SocketAddress;
import java.net.InetSocketAddress;
import java.util.ListIterator;
import java.nio.channels.SocketChannel;
import java.util.LinkedList;
import java.util.List;
import java.io.IOException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import org.slf4j.Logger;

public class ClientCnxnSocketNIO extends ClientCnxnSocket
{
    private static final Logger LOG;
    private final Selector selector;
    private SelectionKey sockKey;
    
    ClientCnxnSocketNIO() throws IOException {
        this.selector = Selector.open();
    }
    
    @Override
    boolean isConnected() {
        return this.sockKey != null;
    }
    
    void doIO(final List<ClientCnxn.Packet> pendingQueue, final LinkedList<ClientCnxn.Packet> outgoingQueue, final ClientCnxn cnxn) throws InterruptedException, IOException {
        final SocketChannel sock = (SocketChannel)this.sockKey.channel();
        if (sock == null) {
            throw new IOException("Socket is null!");
        }
        if (this.sockKey.isReadable()) {
            final int rc = sock.read(this.incomingBuffer);
            if (rc < 0) {
                throw new ClientCnxn.EndOfStreamException("Unable to read additional data from server sessionid 0x" + Long.toHexString(this.sessionId) + ", likely server has closed socket");
            }
            if (!this.incomingBuffer.hasRemaining()) {
                this.incomingBuffer.flip();
                if (this.incomingBuffer == this.lenBuffer) {
                    ++this.recvCount;
                    this.readLength();
                }
                else if (!this.initialized) {
                    this.readConnectResult();
                    this.enableRead();
                    if (this.findSendablePacket(outgoingQueue, cnxn.sendThread.clientTunneledAuthenticationInProgress()) != null) {
                        this.enableWrite();
                    }
                    this.lenBuffer.clear();
                    this.incomingBuffer = this.lenBuffer;
                    this.updateLastHeard();
                    this.initialized = true;
                }
                else {
                    this.sendThread.readResponse(this.incomingBuffer);
                    this.lenBuffer.clear();
                    this.incomingBuffer = this.lenBuffer;
                    this.updateLastHeard();
                }
            }
        }
        if (this.sockKey.isWritable()) {
            synchronized (outgoingQueue) {
                final ClientCnxn.Packet p = this.findSendablePacket(outgoingQueue, cnxn.sendThread.clientTunneledAuthenticationInProgress());
                if (p != null) {
                    this.updateLastSend();
                    if (p.bb == null) {
                        if (p.requestHeader != null && p.requestHeader.getType() != 11 && p.requestHeader.getType() != 100) {
                            p.requestHeader.setXid(cnxn.getXid());
                        }
                        p.createBB();
                    }
                    sock.write(p.bb);
                    if (!p.bb.hasRemaining()) {
                        ++this.sentCount;
                        outgoingQueue.removeFirstOccurrence(p);
                        if (p.requestHeader != null && p.requestHeader.getType() != 11 && p.requestHeader.getType() != 100) {
                            synchronized (pendingQueue) {
                                pendingQueue.add(p);
                            }
                        }
                    }
                }
                if (outgoingQueue.isEmpty()) {
                    this.disableWrite();
                }
                else if (!this.initialized && p != null && !p.bb.hasRemaining()) {
                    this.disableWrite();
                }
                else {
                    this.enableWrite();
                }
            }
        }
    }
    
    private ClientCnxn.Packet findSendablePacket(final LinkedList<ClientCnxn.Packet> outgoingQueue, final boolean clientTunneledAuthenticationInProgress) {
        synchronized (outgoingQueue) {
            if (outgoingQueue.isEmpty()) {
                return null;
            }
            if (outgoingQueue.getFirst().bb != null || !clientTunneledAuthenticationInProgress) {
                return outgoingQueue.getFirst();
            }
            final ListIterator<ClientCnxn.Packet> iter = outgoingQueue.listIterator();
            while (iter.hasNext()) {
                final ClientCnxn.Packet p = iter.next();
                if (p.requestHeader == null) {
                    iter.remove();
                    outgoingQueue.add(0, p);
                    return p;
                }
                if (!ClientCnxnSocketNIO.LOG.isDebugEnabled()) {
                    continue;
                }
                ClientCnxnSocketNIO.LOG.debug("deferring non-priming packet: " + p + "until SASL authentication completes.");
            }
            return null;
        }
    }
    
    @Override
    void cleanup() {
        if (this.sockKey != null) {
            final SocketChannel sock = (SocketChannel)this.sockKey.channel();
            this.sockKey.cancel();
            try {
                sock.socket().shutdownInput();
            }
            catch (IOException e) {
                if (ClientCnxnSocketNIO.LOG.isDebugEnabled()) {
                    ClientCnxnSocketNIO.LOG.debug("Ignoring exception during shutdown input", e);
                }
            }
            try {
                sock.socket().shutdownOutput();
            }
            catch (IOException e) {
                if (ClientCnxnSocketNIO.LOG.isDebugEnabled()) {
                    ClientCnxnSocketNIO.LOG.debug("Ignoring exception during shutdown output", e);
                }
            }
            try {
                sock.socket().close();
            }
            catch (IOException e) {
                if (ClientCnxnSocketNIO.LOG.isDebugEnabled()) {
                    ClientCnxnSocketNIO.LOG.debug("Ignoring exception during socket close", e);
                }
            }
            try {
                sock.close();
            }
            catch (IOException e) {
                if (ClientCnxnSocketNIO.LOG.isDebugEnabled()) {
                    ClientCnxnSocketNIO.LOG.debug("Ignoring exception during channel close", e);
                }
            }
        }
        try {
            Thread.sleep(100L);
        }
        catch (InterruptedException e2) {
            if (ClientCnxnSocketNIO.LOG.isDebugEnabled()) {
                ClientCnxnSocketNIO.LOG.debug("SendThread interrupted during sleep, ignoring");
            }
        }
        this.sockKey = null;
    }
    
    @Override
    void close() {
        try {
            if (ClientCnxnSocketNIO.LOG.isTraceEnabled()) {
                ClientCnxnSocketNIO.LOG.trace("Doing client selector close");
            }
            this.selector.close();
            if (ClientCnxnSocketNIO.LOG.isTraceEnabled()) {
                ClientCnxnSocketNIO.LOG.trace("Closed client selector");
            }
        }
        catch (IOException e) {
            ClientCnxnSocketNIO.LOG.warn("Ignoring exception during selector close", e);
        }
    }
    
    SocketChannel createSock() throws IOException {
        final SocketChannel sock = SocketChannel.open();
        sock.configureBlocking(false);
        sock.socket().setSoLinger(false, -1);
        sock.socket().setTcpNoDelay(true);
        return sock;
    }
    
    void registerAndConnect(final SocketChannel sock, final InetSocketAddress addr) throws IOException {
        this.sockKey = sock.register(this.selector, 8);
        final boolean immediateConnect = sock.connect(addr);
        if (immediateConnect) {
            this.sendThread.primeConnection();
        }
    }
    
    @Override
    void connect(final InetSocketAddress addr) throws IOException {
        final SocketChannel sock = this.createSock();
        try {
            this.registerAndConnect(sock, addr);
        }
        catch (IOException e) {
            ClientCnxnSocketNIO.LOG.error("Unable to open socket to " + addr);
            sock.close();
            throw e;
        }
        this.initialized = false;
        this.lenBuffer.clear();
        this.incomingBuffer = this.lenBuffer;
    }
    
    @Override
    SocketAddress getRemoteSocketAddress() {
        try {
            return ((SocketChannel)this.sockKey.channel()).socket().getRemoteSocketAddress();
        }
        catch (NullPointerException e) {
            return null;
        }
    }
    
    @Override
    SocketAddress getLocalSocketAddress() {
        try {
            return ((SocketChannel)this.sockKey.channel()).socket().getLocalSocketAddress();
        }
        catch (NullPointerException e) {
            return null;
        }
    }
    
    @Override
    synchronized void wakeupCnxn() {
        this.selector.wakeup();
    }
    
    @Override
    void doTransport(final int waitTimeOut, final List<ClientCnxn.Packet> pendingQueue, final LinkedList<ClientCnxn.Packet> outgoingQueue, final ClientCnxn cnxn) throws IOException, InterruptedException {
        this.selector.select(waitTimeOut);
        final Set<SelectionKey> selected;
        synchronized (this) {
            selected = this.selector.selectedKeys();
        }
        this.updateNow();
        for (final SelectionKey k : selected) {
            final SocketChannel sc = (SocketChannel)k.channel();
            if ((k.readyOps() & 0x8) != 0x0) {
                if (!sc.finishConnect()) {
                    continue;
                }
                this.updateLastSendAndHeard();
                this.sendThread.primeConnection();
            }
            else {
                if ((k.readyOps() & 0x5) == 0x0) {
                    continue;
                }
                this.doIO(pendingQueue, outgoingQueue, cnxn);
            }
        }
        if (this.sendThread.getZkState().isConnected()) {
            synchronized (outgoingQueue) {
                if (this.findSendablePacket(outgoingQueue, cnxn.sendThread.clientTunneledAuthenticationInProgress()) != null) {
                    this.enableWrite();
                }
            }
        }
        selected.clear();
    }
    
    @Override
    void testableCloseSocket() throws IOException {
        ClientCnxnSocketNIO.LOG.info("testableCloseSocket() called");
        ((SocketChannel)this.sockKey.channel()).socket().close();
    }
    
    @Override
    synchronized void enableWrite() {
        final int i = this.sockKey.interestOps();
        if ((i & 0x4) == 0x0) {
            this.sockKey.interestOps(i | 0x4);
        }
    }
    
    public synchronized void disableWrite() {
        final int i = this.sockKey.interestOps();
        if ((i & 0x4) != 0x0) {
            this.sockKey.interestOps(i & 0xFFFFFFFB);
        }
    }
    
    private synchronized void enableRead() {
        final int i = this.sockKey.interestOps();
        if ((i & 0x1) == 0x0) {
            this.sockKey.interestOps(i | 0x1);
        }
    }
    
    @Override
    synchronized void enableReadWriteOnly() {
        this.sockKey.interestOps(5);
    }
    
    Selector getSelector() {
        return this.selector;
    }
    
    @Override
    void sendPacket(final ClientCnxn.Packet p) throws IOException {
        final SocketChannel sock = (SocketChannel)this.sockKey.channel();
        if (sock == null) {
            throw new IOException("Socket is null!");
        }
        p.createBB();
        final ByteBuffer pbb = p.bb;
        sock.write(pbb);
    }
    
    static {
        LOG = LoggerFactory.getLogger(ClientCnxnSocketNIO.class);
    }
}
