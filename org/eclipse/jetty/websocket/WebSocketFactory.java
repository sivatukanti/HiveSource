// 
// Decompiled by Procyon v0.5.36
// 

package org.eclipse.jetty.websocket;

import org.eclipse.jetty.util.log.Log;
import java.util.Iterator;
import java.io.IOException;
import java.util.Enumeration;
import java.util.List;
import org.eclipse.jetty.http.HttpParser;
import org.eclipse.jetty.http.HttpException;
import org.eclipse.jetty.io.EndPoint;
import org.eclipse.jetty.util.QuotedStringTokenizer;
import java.util.ArrayList;
import org.eclipse.jetty.io.ConnectedEndPoint;
import org.eclipse.jetty.server.BlockingHttpConnection;
import org.eclipse.jetty.server.AbstractHttpConnection;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.Map;
import java.util.Queue;
import org.eclipse.jetty.util.log.Logger;
import org.eclipse.jetty.util.component.AbstractLifeCycle;

public class WebSocketFactory extends AbstractLifeCycle
{
    private static final Logger LOG;
    private final Queue<WebSocketServletConnection> connections;
    private final Map<String, Class<? extends Extension>> _extensionClasses;
    private final Acceptor _acceptor;
    private WebSocketBuffers _buffers;
    private int _maxIdleTime;
    private int _maxTextMessageSize;
    private int _maxBinaryMessageSize;
    
    public WebSocketFactory(final Acceptor acceptor) {
        this(acceptor, 65536);
    }
    
    public WebSocketFactory(final Acceptor acceptor, final int bufferSize) {
        this.connections = new ConcurrentLinkedQueue<WebSocketServletConnection>();
        (this._extensionClasses = new HashMap<String, Class<? extends Extension>>()).put("identity", IdentityExtension.class);
        this._extensionClasses.put("fragment", FragmentExtension.class);
        this._extensionClasses.put("x-deflate-frame", DeflateFrameExtension.class);
        this._maxIdleTime = 300000;
        this._maxTextMessageSize = 16384;
        this._maxBinaryMessageSize = -1;
        this._buffers = new WebSocketBuffers(bufferSize);
        this._acceptor = acceptor;
    }
    
    public Map<String, Class<? extends Extension>> getExtensionClassesMap() {
        return this._extensionClasses;
    }
    
    public long getMaxIdleTime() {
        return this._maxIdleTime;
    }
    
    public void setMaxIdleTime(final int maxIdleTime) {
        this._maxIdleTime = maxIdleTime;
    }
    
    public int getBufferSize() {
        return this._buffers.getBufferSize();
    }
    
    public void setBufferSize(final int bufferSize) {
        if (bufferSize != this.getBufferSize()) {
            this._buffers = new WebSocketBuffers(bufferSize);
        }
    }
    
    public int getMaxTextMessageSize() {
        return this._maxTextMessageSize;
    }
    
    public void setMaxTextMessageSize(final int maxTextMessageSize) {
        this._maxTextMessageSize = maxTextMessageSize;
    }
    
    public int getMaxBinaryMessageSize() {
        return this._maxBinaryMessageSize;
    }
    
    public void setMaxBinaryMessageSize(final int maxBinaryMessageSize) {
        this._maxBinaryMessageSize = maxBinaryMessageSize;
    }
    
    @Override
    protected void doStop() throws Exception {
        this.closeConnections();
    }
    
    public void upgrade(final HttpServletRequest request, final HttpServletResponse response, final WebSocket websocket, final String protocol) throws IOException {
        if (!"websocket".equalsIgnoreCase(request.getHeader("Upgrade"))) {
            throw new IllegalStateException("!Upgrade:websocket");
        }
        if (!"HTTP/1.1".equals(request.getProtocol())) {
            throw new IllegalStateException("!HTTP/1.1");
        }
        int draft = request.getIntHeader("Sec-WebSocket-Version");
        if (draft < 0) {
            draft = request.getIntHeader("Sec-WebSocket-Draft");
        }
        final AbstractHttpConnection http = AbstractHttpConnection.getCurrentConnection();
        if (http instanceof BlockingHttpConnection) {
            throw new IllegalStateException("Websockets not supported on blocking connectors");
        }
        final ConnectedEndPoint endp = (ConnectedEndPoint)http.getEndPoint();
        final List<String> extensions_requested = new ArrayList<String>();
        final Enumeration<String> e = request.getHeaders("Sec-WebSocket-Extensions");
        while (e.hasMoreElements()) {
            final QuotedStringTokenizer tok = new QuotedStringTokenizer(e.nextElement(), ",");
            while (tok.hasMoreTokens()) {
                extensions_requested.add(tok.nextToken());
            }
        }
        WebSocketServletConnection connection = null;
        switch (draft) {
            case -1:
            case 0: {
                connection = new WebSocketServletConnectionD00(this, websocket, endp, this._buffers, http.getTimeStamp(), this._maxIdleTime, protocol);
                break;
            }
            case 1:
            case 2:
            case 3:
            case 4:
            case 5:
            case 6: {
                connection = new WebSocketServletConnectionD06(this, websocket, endp, this._buffers, http.getTimeStamp(), this._maxIdleTime, protocol);
                break;
            }
            case 7:
            case 8: {
                final List<Extension> extensions = this.initExtensions(extensions_requested, 5, 5, 3);
                connection = new WebSocketServletConnectionD08(this, websocket, endp, this._buffers, http.getTimeStamp(), this._maxIdleTime, protocol, extensions, draft);
                break;
            }
            case 13: {
                final List<Extension> extensions = this.initExtensions(extensions_requested, 5, 5, 3);
                connection = new WebSocketServletConnectionRFC6455(this, websocket, endp, this._buffers, http.getTimeStamp(), this._maxIdleTime, protocol, extensions, draft);
                break;
            }
            default: {
                WebSocketFactory.LOG.warn("Unsupported Websocket version: " + draft, new Object[0]);
                response.setHeader("Sec-WebSocket-Version", "13, 8, 6, 0");
                throw new HttpException(400, "Unsupported websocket version specification: " + draft);
            }
        }
        this.addConnection(connection);
        connection.getConnection().setMaxBinaryMessageSize(this._maxBinaryMessageSize);
        connection.getConnection().setMaxTextMessageSize(this._maxTextMessageSize);
        connection.handshake(request, response, protocol);
        response.flushBuffer();
        connection.fillBuffersFrom(((HttpParser)http.getParser()).getHeaderBuffer());
        connection.fillBuffersFrom(((HttpParser)http.getParser()).getBodyBuffer());
        WebSocketFactory.LOG.debug("Websocket upgrade {} {} {} {}", request.getRequestURI(), draft, protocol, connection);
        request.setAttribute("org.eclipse.jetty.io.Connection", connection);
    }
    
    protected String[] parseProtocols(String protocol) {
        if (protocol == null) {
            return new String[] { null };
        }
        protocol = protocol.trim();
        if (protocol == null || protocol.length() == 0) {
            return new String[] { null };
        }
        final String[] passed = protocol.split("\\s*,\\s*");
        final String[] protocols = new String[passed.length + 1];
        System.arraycopy(passed, 0, protocols, 0, passed.length);
        return protocols;
    }
    
    public boolean acceptWebSocket(final HttpServletRequest request, final HttpServletResponse response) throws IOException {
        if (!"websocket".equalsIgnoreCase(request.getHeader("Upgrade"))) {
            return false;
        }
        String origin = request.getHeader("Origin");
        if (origin == null) {
            origin = request.getHeader("Sec-WebSocket-Origin");
        }
        if (!this._acceptor.checkOrigin(request, origin)) {
            response.sendError(403);
            return false;
        }
        WebSocket websocket = null;
        Enumeration<String> protocols;
        String protocol;
        String candidate;
        String[] arr$;
        int len$;
        int i$;
        String p = null;
        for (protocols = request.getHeaders("Sec-WebSocket-Protocol"), protocol = null; protocol == null && protocols != null && protocols.hasMoreElements(); protocol = p) {
            candidate = protocols.nextElement();
            arr$ = this.parseProtocols(candidate);
            for (len$ = arr$.length, i$ = 0; i$ < len$; ++i$) {
                p = arr$[i$];
                websocket = this._acceptor.doWebSocketConnect(request, p);
                if (websocket != null) {
                    break;
                }
            }
        }
        if (websocket == null) {
            websocket = this._acceptor.doWebSocketConnect(request, null);
            if (websocket == null) {
                response.sendError(503);
                return false;
            }
        }
        this.upgrade(request, response, websocket, protocol);
        return true;
    }
    
    public List<Extension> initExtensions(final List<String> requested, final int maxDataOpcodes, final int maxControlOpcodes, final int maxReservedBits) {
        final List<Extension> extensions = new ArrayList<Extension>();
        for (final String rExt : requested) {
            final QuotedStringTokenizer tok = new QuotedStringTokenizer(rExt, ";");
            final String extName = tok.nextToken().trim();
            final Map<String, String> parameters = new HashMap<String, String>();
            while (tok.hasMoreTokens()) {
                final QuotedStringTokenizer nv = new QuotedStringTokenizer(tok.nextToken().trim(), "=");
                final String name = nv.nextToken().trim();
                final String value = nv.hasMoreTokens() ? nv.nextToken().trim() : null;
                parameters.put(name, value);
            }
            final Extension extension = this.newExtension(extName);
            if (extension == null) {
                continue;
            }
            if (!extension.init(parameters)) {
                continue;
            }
            WebSocketFactory.LOG.debug("add {} {}", extName, parameters);
            extensions.add(extension);
        }
        WebSocketFactory.LOG.debug("extensions={}", extensions);
        return extensions;
    }
    
    private Extension newExtension(final String name) {
        try {
            final Class<? extends Extension> extClass = this._extensionClasses.get(name);
            if (extClass != null) {
                return (Extension)extClass.newInstance();
            }
        }
        catch (Exception e) {
            WebSocketFactory.LOG.warn(e);
        }
        return null;
    }
    
    protected boolean addConnection(final WebSocketServletConnection connection) {
        return this.isRunning() && this.connections.add(connection);
    }
    
    protected boolean removeConnection(final WebSocketServletConnection connection) {
        return this.connections.remove(connection);
    }
    
    protected void closeConnections() {
        for (final WebSocketServletConnection connection : this.connections) {
            connection.shutdown();
        }
    }
    
    static {
        LOG = Log.getLogger(WebSocketFactory.class);
    }
    
    public interface Acceptor
    {
        WebSocket doWebSocketConnect(final HttpServletRequest p0, final String p1);
        
        boolean checkOrigin(final HttpServletRequest p0, final String p1);
    }
}
