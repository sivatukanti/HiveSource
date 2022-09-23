// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.zookeeper;

import org.slf4j.LoggerFactory;
import java.util.LinkedList;
import java.util.List;
import java.net.SocketAddress;
import java.net.InetSocketAddress;
import org.apache.jute.InputArchive;
import org.apache.zookeeper.proto.ConnectResponse;
import java.io.InputStream;
import org.apache.jute.BinaryInputArchive;
import org.apache.zookeeper.server.ByteBufferInputStream;
import java.io.IOException;
import org.apache.zookeeper.common.Time;
import java.nio.ByteBuffer;
import org.slf4j.Logger;

abstract class ClientCnxnSocket
{
    private static final Logger LOG;
    protected boolean initialized;
    protected final ByteBuffer lenBuffer;
    protected ByteBuffer incomingBuffer;
    protected long sentCount;
    protected long recvCount;
    protected long lastHeard;
    protected long lastSend;
    protected long now;
    protected ClientCnxn.SendThread sendThread;
    protected long sessionId;
    
    ClientCnxnSocket() {
        this.lenBuffer = ByteBuffer.allocateDirect(4);
        this.incomingBuffer = this.lenBuffer;
        this.sentCount = 0L;
        this.recvCount = 0L;
    }
    
    void introduce(final ClientCnxn.SendThread sendThread, final long sessionId) {
        this.sendThread = sendThread;
        this.sessionId = sessionId;
    }
    
    void updateNow() {
        this.now = Time.currentElapsedTime();
    }
    
    int getIdleRecv() {
        return (int)(this.now - this.lastHeard);
    }
    
    int getIdleSend() {
        return (int)(this.now - this.lastSend);
    }
    
    long getSentCount() {
        return this.sentCount;
    }
    
    long getRecvCount() {
        return this.recvCount;
    }
    
    void updateLastHeard() {
        this.lastHeard = this.now;
    }
    
    void updateLastSend() {
        this.lastSend = this.now;
    }
    
    void updateLastSendAndHeard() {
        this.lastSend = this.now;
        this.lastHeard = this.now;
    }
    
    protected void readLength() throws IOException {
        final int len = this.incomingBuffer.getInt();
        if (len < 0 || len >= ClientCnxn.packetLen) {
            throw new IOException("Packet len" + len + " is out of range!");
        }
        this.incomingBuffer = ByteBuffer.allocate(len);
    }
    
    void readConnectResult() throws IOException {
        if (ClientCnxnSocket.LOG.isTraceEnabled()) {
            final StringBuilder buf = new StringBuilder("0x[");
            for (final byte b : this.incomingBuffer.array()) {
                buf.append(Integer.toHexString(b) + ",");
            }
            buf.append("]");
            ClientCnxnSocket.LOG.trace("readConnectResult " + this.incomingBuffer.remaining() + " " + buf.toString());
        }
        final ByteBufferInputStream bbis = new ByteBufferInputStream(this.incomingBuffer);
        final BinaryInputArchive bbia = BinaryInputArchive.getArchive(bbis);
        final ConnectResponse conRsp = new ConnectResponse();
        conRsp.deserialize(bbia, "connect");
        boolean isRO = false;
        try {
            isRO = bbia.readBool("readOnly");
        }
        catch (IOException e) {
            ClientCnxnSocket.LOG.warn("Connected to an old server; r-o mode will be unavailable");
        }
        this.sessionId = conRsp.getSessionId();
        this.sendThread.onConnected(conRsp.getTimeOut(), this.sessionId, conRsp.getPasswd(), isRO);
    }
    
    abstract boolean isConnected();
    
    abstract void connect(final InetSocketAddress p0) throws IOException;
    
    abstract SocketAddress getRemoteSocketAddress();
    
    abstract SocketAddress getLocalSocketAddress();
    
    abstract void cleanup();
    
    abstract void close();
    
    abstract void wakeupCnxn();
    
    abstract void enableWrite();
    
    abstract void disableWrite();
    
    abstract void enableReadWriteOnly();
    
    abstract void doTransport(final int p0, final List<ClientCnxn.Packet> p1, final LinkedList<ClientCnxn.Packet> p2, final ClientCnxn p3) throws IOException, InterruptedException;
    
    abstract void testableCloseSocket() throws IOException;
    
    abstract void sendPacket(final ClientCnxn.Packet p0) throws IOException;
    
    static {
        LOG = LoggerFactory.getLogger(ClientCnxnSocket.class);
    }
}
