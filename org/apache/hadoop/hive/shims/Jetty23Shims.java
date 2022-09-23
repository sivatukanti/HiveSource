// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.hive.shims;

import org.mortbay.jetty.Connector;
import org.mortbay.jetty.bio.SocketConnector;
import org.mortbay.jetty.Handler;
import org.mortbay.jetty.handler.RequestLogHandler;
import org.mortbay.jetty.webapp.WebAppContext;
import org.mortbay.jetty.Server;
import java.io.IOException;

public class Jetty23Shims implements JettyShims
{
    @Override
    public Server startServer(final String listen, final int port) throws IOException {
        final Server s = new Server();
        s.setupListenerHostPort(listen, port);
        return s;
    }
    
    private static class Server extends org.mortbay.jetty.Server implements JettyShims.Server
    {
        @Override
        public void addWar(final String war, final String contextPath) {
            final WebAppContext wac = new WebAppContext();
            wac.setContextPath(contextPath);
            wac.setWar(war);
            final RequestLogHandler rlh = new RequestLogHandler();
            rlh.setHandler(wac);
            this.addHandler(rlh);
        }
        
        public void setupListenerHostPort(final String listen, final int port) throws IOException {
            final SocketConnector connector = new SocketConnector();
            connector.setPort(port);
            connector.setHost(listen);
            this.addConnector(connector);
        }
    }
}
