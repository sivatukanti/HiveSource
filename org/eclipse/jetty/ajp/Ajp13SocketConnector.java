// 
// Decompiled by Procyon v0.5.36
// 

package org.eclipse.jetty.ajp;

import org.eclipse.jetty.util.log.Log;
import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.io.Connection;
import java.io.IOException;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.io.EndPoint;
import org.eclipse.jetty.util.log.Logger;
import org.eclipse.jetty.server.bio.SocketConnector;

public class Ajp13SocketConnector extends SocketConnector
{
    private static final Logger LOG;
    static String __secretWord;
    static boolean __allowShutdown;
    
    public Ajp13SocketConnector() {
        super.setRequestHeaderSize(8192);
        super.setResponseHeaderSize(8192);
        super.setRequestBufferSize(8192);
        super.setResponseBufferSize(8192);
        super.setMaxIdleTime(0);
    }
    
    @Override
    protected void doStart() throws Exception {
        super.doStart();
        Ajp13SocketConnector.LOG.info("AJP13 is not a secure protocol. Please protect port {}", Integer.toString(this.getLocalPort()));
    }
    
    @Override
    public void customize(final EndPoint endpoint, final Request request) throws IOException {
        super.customize(endpoint, request);
        if (request.isSecure()) {
            request.setScheme("https");
        }
    }
    
    @Override
    protected Connection newConnection(final EndPoint endpoint) {
        return new Ajp13Connection(this, endpoint, this.getServer());
    }
    
    public boolean isConfidential(final Request request) {
        return ((Ajp13Request)request).isSslSecure();
    }
    
    public boolean isIntegral(final Request request) {
        return ((Ajp13Request)request).isSslSecure();
    }
    
    @Deprecated
    public void setHeaderBufferSize(final int headerBufferSize) {
        Ajp13SocketConnector.LOG.debug("IGNORED ", new Object[0]);
    }
    
    public void setRequestBufferSize(final int requestBufferSize) {
        Ajp13SocketConnector.LOG.debug("IGNORED ", new Object[0]);
    }
    
    public void setResponseBufferSize(final int responseBufferSize) {
        Ajp13SocketConnector.LOG.debug("IGNORED ", new Object[0]);
    }
    
    public void setAllowShutdown(final boolean allowShutdown) {
        Ajp13SocketConnector.LOG.warn("AJP13: Shutdown Request is: " + allowShutdown, new Object[0]);
        Ajp13SocketConnector.__allowShutdown = allowShutdown;
    }
    
    public void setSecretWord(final String secretWord) {
        Ajp13SocketConnector.LOG.warn("AJP13: Shutdown Request secret word is : " + secretWord, new Object[0]);
        Ajp13SocketConnector.__secretWord = secretWord;
    }
    
    static {
        LOG = Log.getLogger(Ajp13SocketConnector.class);
        Ajp13SocketConnector.__secretWord = null;
        Ajp13SocketConnector.__allowShutdown = false;
    }
}
