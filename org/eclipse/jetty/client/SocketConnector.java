// 
// Decompiled by Procyon v0.5.36
// 

package org.eclipse.jetty.client;

import org.eclipse.jetty.util.log.Log;
import org.eclipse.jetty.io.EndPoint;
import java.net.Socket;
import org.eclipse.jetty.io.Connection;
import java.io.IOException;
import java.io.InterruptedIOException;
import org.eclipse.jetty.io.bio.SocketEndPoint;
import java.net.SocketAddress;
import javax.net.SocketFactory;
import org.eclipse.jetty.util.log.Logger;
import org.eclipse.jetty.util.component.AbstractLifeCycle;

class SocketConnector extends AbstractLifeCycle implements HttpClient.Connector
{
    private static final Logger LOG;
    private final HttpClient _httpClient;
    
    SocketConnector(final HttpClient httpClient) {
        this._httpClient = httpClient;
    }
    
    public void startConnection(final HttpDestination destination) throws IOException {
        final Socket socket = destination.isSecure() ? this._httpClient.getSslContextFactory().newSslSocket() : SocketFactory.getDefault().createSocket();
        socket.setSoTimeout(0);
        socket.setTcpNoDelay(true);
        final Address address = destination.isProxied() ? destination.getProxy() : destination.getAddress();
        socket.connect(address.toSocketAddress(), this._httpClient.getConnectTimeout());
        final EndPoint endpoint = new SocketEndPoint(socket);
        final AbstractHttpConnection connection = new BlockingHttpConnection(this._httpClient.getRequestBuffers(), this._httpClient.getResponseBuffers(), endpoint);
        connection.setDestination(destination);
        destination.onNewConnection(connection);
        this._httpClient.getThreadPool().dispatch((Runnable)new Runnable() {
            public void run() {
                try {
                    Connection con = connection;
                    while (true) {
                        final Connection next = con.handle();
                        if (next == con) {
                            break;
                        }
                        con = next;
                    }
                }
                catch (IOException e) {
                    if (e instanceof InterruptedIOException) {
                        SocketConnector.LOG.ignore(e);
                    }
                    else {
                        SocketConnector.LOG.debug(e);
                        destination.onException(e);
                    }
                    try {
                        destination.returnConnection(connection, true);
                    }
                    catch (IOException e) {
                        SocketConnector.LOG.debug(e);
                    }
                }
                finally {
                    try {
                        destination.returnConnection(connection, true);
                    }
                    catch (IOException e2) {
                        SocketConnector.LOG.debug(e2);
                    }
                }
            }
        });
    }
    
    static {
        LOG = Log.getLogger(SocketConnector.class);
    }
}
