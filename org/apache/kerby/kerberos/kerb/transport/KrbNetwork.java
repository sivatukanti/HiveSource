// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.kerby.kerberos.kerb.transport;

import org.slf4j.LoggerFactory;
import java.net.SocketAddress;
import java.net.Socket;
import java.io.IOException;
import org.slf4j.Logger;

public class KrbNetwork
{
    private static final Logger LOG;
    private int socketTimeout;
    private TransportPair tpair;
    
    public KrbNetwork() {
        this.socketTimeout = 10000;
    }
    
    public KrbTransport connect(final TransportPair tpair) throws IOException {
        this.tpair = tpair;
        KrbTransport transport = null;
        if (tpair.tcpAddress != null) {
            try {
                transport = this.tcpConnect();
            }
            catch (IOException e2) {
                if (tpair.udpAddress != null) {
                    try {
                        transport = new KrbUdpTransport(tpair.udpAddress);
                    }
                    catch (Exception e3) {
                        transport = null;
                    }
                }
            }
            catch (Exception e) {
                KrbNetwork.LOG.error("TCP connect Failed. " + e.toString());
            }
        }
        else if (tpair.udpAddress != null) {
            try {
                transport = new KrbUdpTransport(tpair.udpAddress);
            }
            catch (Exception e4) {
                transport = null;
            }
        }
        if (transport == null) {
            throw new IOException("Failed to establish the transport");
        }
        return transport;
    }
    
    private KrbTcpTransport tcpConnect() throws IOException {
        final Socket socket = new Socket();
        socket.setSoTimeout(this.socketTimeout);
        socket.connect(this.tpair.tcpAddress);
        return new KrbTcpTransport(socket);
    }
    
    public void setSocketTimeout(final int milliSeconds) {
        this.socketTimeout = milliSeconds;
    }
    
    static {
        LOG = LoggerFactory.getLogger(KrbNetwork.class);
    }
}
