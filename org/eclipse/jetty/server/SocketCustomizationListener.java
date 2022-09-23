// 
// Decompiled by Procyon v0.5.36
// 

package org.eclipse.jetty.server;

import java.net.Socket;
import org.eclipse.jetty.io.EndPoint;
import org.eclipse.jetty.io.ChannelEndPoint;
import org.eclipse.jetty.io.ssl.SslConnection;
import org.eclipse.jetty.io.Connection;

public class SocketCustomizationListener implements Connection.Listener
{
    private final boolean _ssl;
    
    public SocketCustomizationListener() {
        this(true);
    }
    
    public SocketCustomizationListener(final boolean ssl) {
        this._ssl = ssl;
    }
    
    @Override
    public void onOpened(final Connection connection) {
        EndPoint endp = connection.getEndPoint();
        boolean ssl = false;
        if (this._ssl && endp instanceof SslConnection.DecryptedEndPoint) {
            endp = ((SslConnection.DecryptedEndPoint)endp).getSslConnection().getEndPoint();
            ssl = true;
        }
        if (endp instanceof ChannelEndPoint) {
            final Socket socket = ((ChannelEndPoint)endp).getSocket();
            this.customize(socket, connection.getClass(), ssl);
        }
    }
    
    protected void customize(final Socket socket, final Class<? extends Connection> connection, final boolean ssl) {
    }
    
    @Override
    public void onClosed(final Connection connection) {
    }
}
