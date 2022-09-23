// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.kerby.kerberos.kerb.transport;

import org.slf4j.LoggerFactory;
import java.net.Socket;
import java.net.SocketException;
import java.nio.channels.ClosedChannelException;
import java.net.SocketTimeoutException;
import java.io.IOException;
import java.net.SocketAddress;
import java.util.HashMap;
import java.nio.ByteBuffer;
import java.net.InetSocketAddress;
import java.util.Map;
import java.nio.channels.DatagramChannel;
import java.net.ServerSocket;
import org.slf4j.Logger;

public abstract class KdcNetwork
{
    private static final Logger LOG;
    protected static final int MAX_MESSAGE_SIZE = 65507;
    private static final int KDC_TCP_TRANSPORT_TIMEOUT = 3000;
    private static final int KDC_TCP_SERVER_TIMEOUT = 100;
    private TransportPair tpair;
    private boolean isStopped;
    private ServerSocket tcpServer;
    private DatagramChannel udpServer;
    private Map<InetSocketAddress, KdcUdpTransport> transports;
    private ByteBuffer recvBuffer;
    
    public KdcNetwork() {
        this.transports = new HashMap<InetSocketAddress, KdcUdpTransport>();
    }
    
    public synchronized void init() {
        this.isStopped = false;
    }
    
    public synchronized void listen(final TransportPair tpair) throws IOException {
        this.tpair = tpair;
        if (tpair.tcpAddress != null) {
            (this.tcpServer = new ServerSocket()).setSoTimeout(100);
            this.tcpServer.bind(tpair.tcpAddress);
        }
        if (tpair.udpAddress != null) {
            (this.udpServer = DatagramChannel.open()).configureBlocking(false);
            this.udpServer.bind(tpair.udpAddress);
            this.recvBuffer = ByteBuffer.allocate(65507);
        }
    }
    
    public synchronized void start() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                KdcNetwork.this.run();
            }
        }).start();
    }
    
    private void run() {
        while (true) {
            synchronized (this) {
                if (this.isStopped) {
                    break;
                }
            }
            if (this.tpair.tcpAddress != null) {
                try {
                    this.checkAndAccept();
                }
                catch (SocketTimeoutException | ClosedChannelException | SocketException ex) {}
                catch (IOException e) {
                    throw new RuntimeException("Error occured while checking tcp connections", e);
                }
            }
            if (this.tpair.udpAddress != null) {
                try {
                    this.checkUdpMessage();
                    continue;
                }
                catch (SocketTimeoutException | ClosedChannelException | SocketException ex2) {
                    continue;
                }
                catch (IOException e) {
                    throw new RuntimeException("Error occured while checking udp connections", e);
                }
                break;
            }
        }
    }
    
    public synchronized void stop() {
        try {
            if (this.tcpServer != null) {
                this.tcpServer.close();
            }
            if (this.udpServer != null) {
                this.udpServer.close();
            }
        }
        catch (IOException e) {
            KdcNetwork.LOG.warn("KDC network stopping error " + e);
        }
        this.isStopped = true;
    }
    
    private void checkAndAccept() throws IOException {
        if (this.tcpServer.isClosed()) {
            return;
        }
        final Socket socket;
        if ((socket = this.tcpServer.accept()) != null) {
            socket.setSoTimeout(3000);
            final KrbTransport transport = new KrbTcpTransport(socket);
            this.onNewTransport(transport);
        }
    }
    
    private void checkUdpMessage() throws IOException {
        if (!this.udpServer.isOpen()) {
            return;
        }
        final InetSocketAddress fromAddress = (InetSocketAddress)this.udpServer.receive(this.recvBuffer);
        if (fromAddress != null) {
            this.recvBuffer.flip();
            KdcUdpTransport transport = this.transports.get(fromAddress);
            if (transport == null) {
                transport = new KdcUdpTransport(this.udpServer, fromAddress);
                transport.onRecvMessage(this.recvBuffer);
                this.onNewTransport(transport);
            }
            else {
                transport.onRecvMessage(this.recvBuffer);
            }
            this.recvBuffer = ByteBuffer.allocate(65507);
        }
    }
    
    protected abstract void onNewTransport(final KrbTransport p0);
    
    static {
        LOG = LoggerFactory.getLogger(KdcNetwork.class);
    }
}
