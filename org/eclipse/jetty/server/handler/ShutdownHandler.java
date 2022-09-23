// 
// Decompiled by Procyon v0.5.36
// 

package org.eclipse.jetty.server.handler;

import org.eclipse.jetty.util.log.Log;
import java.net.InetSocketAddress;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletRequest;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.server.NetworkConnector;
import java.io.IOException;
import java.net.SocketException;
import java.net.HttpURLConnection;
import java.net.URL;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.util.log.Logger;

public class ShutdownHandler extends HandlerWrapper
{
    private static final Logger LOG;
    private final String _shutdownToken;
    private boolean _sendShutdownAtStart;
    private boolean _exitJvm;
    
    @Deprecated
    public ShutdownHandler(final Server server, final String shutdownToken) {
        this(shutdownToken);
    }
    
    public ShutdownHandler(final String shutdownToken) {
        this(shutdownToken, false, false);
    }
    
    public ShutdownHandler(final String shutdownToken, final boolean exitJVM, final boolean sendShutdownAtStart) {
        this._exitJvm = false;
        this._shutdownToken = shutdownToken;
        this.setExitJvm(exitJVM);
        this.setSendShutdownAtStart(sendShutdownAtStart);
    }
    
    public void sendShutdown() throws IOException {
        final URL url = new URL(this.getServerUrl() + "/shutdown?token=" + this._shutdownToken);
        try {
            final HttpURLConnection connection = (HttpURLConnection)url.openConnection();
            connection.setRequestMethod("POST");
            connection.getResponseCode();
            ShutdownHandler.LOG.info("Shutting down " + url + ": " + connection.getResponseCode() + " " + connection.getResponseMessage(), new Object[0]);
        }
        catch (SocketException e2) {
            ShutdownHandler.LOG.debug("Not running", new Object[0]);
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    
    private String getServerUrl() {
        NetworkConnector connector = null;
        for (final Connector c : this.getServer().getConnectors()) {
            if (c instanceof NetworkConnector) {
                connector = (NetworkConnector)c;
                break;
            }
        }
        if (connector == null) {
            return "http://localhost";
        }
        return "http://localhost:" + connector.getPort();
    }
    
    @Override
    protected void doStart() throws Exception {
        super.doStart();
        if (this._sendShutdownAtStart) {
            this.sendShutdown();
        }
    }
    
    @Override
    public void handle(final String target, final Request baseRequest, final HttpServletRequest request, final HttpServletResponse response) throws IOException, ServletException {
        if (!target.equals("/shutdown")) {
            super.handle(target, baseRequest, request, response);
            return;
        }
        if (!request.getMethod().equals("POST")) {
            response.sendError(400);
            return;
        }
        if (!this.hasCorrectSecurityToken(request)) {
            ShutdownHandler.LOG.warn("Unauthorized tokenless shutdown attempt from " + request.getRemoteAddr(), new Object[0]);
            response.sendError(401);
            return;
        }
        if (!this.requestFromLocalhost(baseRequest)) {
            ShutdownHandler.LOG.warn("Unauthorized non-loopback shutdown attempt from " + request.getRemoteAddr(), new Object[0]);
            response.sendError(401);
            return;
        }
        ShutdownHandler.LOG.info("Shutting down by request from " + request.getRemoteAddr(), new Object[0]);
        this.doShutdown(baseRequest, response);
    }
    
    protected void doShutdown(final Request baseRequest, final HttpServletResponse response) throws IOException {
        for (final Connector connector : this.getServer().getConnectors()) {
            connector.shutdown();
        }
        response.sendError(200, "Connectors closed, commencing full shutdown");
        baseRequest.setHandled(true);
        final Server server = this.getServer();
        new Thread() {
            @Override
            public void run() {
                try {
                    ShutdownHandler.this.shutdownServer(server);
                }
                catch (InterruptedException e) {
                    ShutdownHandler.LOG.ignore(e);
                }
                catch (Exception e2) {
                    throw new RuntimeException("Shutting down server", e2);
                }
            }
        }.start();
    }
    
    private boolean requestFromLocalhost(final Request request) {
        final InetSocketAddress addr = request.getRemoteInetSocketAddress();
        return addr != null && addr.getAddress().isLoopbackAddress();
    }
    
    private boolean hasCorrectSecurityToken(final HttpServletRequest request) {
        final String tok = request.getParameter("token");
        if (ShutdownHandler.LOG.isDebugEnabled()) {
            ShutdownHandler.LOG.debug("Token: {}", tok);
        }
        return this._shutdownToken.equals(tok);
    }
    
    private void shutdownServer(final Server server) throws Exception {
        server.stop();
        if (this._exitJvm) {
            System.exit(0);
        }
    }
    
    public void setExitJvm(final boolean exitJvm) {
        this._exitJvm = exitJvm;
    }
    
    public boolean isSendShutdownAtStart() {
        return this._sendShutdownAtStart;
    }
    
    public void setSendShutdownAtStart(final boolean sendShutdownAtStart) {
        this._sendShutdownAtStart = sendShutdownAtStart;
    }
    
    public String getShutdownToken() {
        return this._shutdownToken;
    }
    
    public boolean isExitJvm() {
        return this._exitJvm;
    }
    
    static {
        LOG = Log.getLogger(ShutdownHandler.class);
    }
}
