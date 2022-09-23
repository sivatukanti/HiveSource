// 
// Decompiled by Procyon v0.5.36
// 

package org.eclipse.jetty.websocket;

import java.io.IOException;
import org.eclipse.jetty.io.Buffer;
import java.util.Iterator;
import org.eclipse.jetty.util.QuotedStringTokenizer;
import java.util.HashMap;
import java.util.Map;

public class AbstractExtension implements Extension
{
    private static final int[] __mask;
    private final String _name;
    private final Map<String, String> _parameters;
    private WebSocketParser.FrameHandler _inbound;
    private WebSocketGenerator _outbound;
    private WebSocket.FrameConnection _connection;
    
    public AbstractExtension(final String name) {
        this._parameters = new HashMap<String, String>();
        this._name = name;
    }
    
    public WebSocket.FrameConnection getConnection() {
        return this._connection;
    }
    
    public boolean init(final Map<String, String> parameters) {
        this._parameters.putAll(parameters);
        return true;
    }
    
    public String getInitParameter(final String name) {
        return this._parameters.get(name);
    }
    
    public String getInitParameter(final String name, final String dft) {
        if (!this._parameters.containsKey(name)) {
            return dft;
        }
        return this._parameters.get(name);
    }
    
    public int getInitParameter(final String name, final int dft) {
        final String v = this._parameters.get(name);
        if (v == null) {
            return dft;
        }
        return Integer.valueOf(v);
    }
    
    public void bind(final WebSocket.FrameConnection connection, final WebSocketParser.FrameHandler incoming, final WebSocketGenerator outgoing) {
        this._connection = connection;
        this._inbound = incoming;
        this._outbound = outgoing;
    }
    
    public String getName() {
        return this._name;
    }
    
    public String getParameterizedName() {
        final StringBuilder name = new StringBuilder();
        name.append(this._name);
        for (final String param : this._parameters.keySet()) {
            name.append(';').append(param).append('=').append(QuotedStringTokenizer.quoteIfNeeded(this._parameters.get(param), ";="));
        }
        return name.toString();
    }
    
    public void onFrame(final byte flags, final byte opcode, final Buffer buffer) {
        this._inbound.onFrame(flags, opcode, buffer);
    }
    
    public void close(final int code, final String message) {
        this._inbound.close(code, message);
    }
    
    public int flush() throws IOException {
        return this._outbound.flush();
    }
    
    public boolean isBufferEmpty() {
        return this._outbound.isBufferEmpty();
    }
    
    public void addFrame(final byte flags, final byte opcode, final byte[] content, final int offset, final int length) throws IOException {
        this._outbound.addFrame(flags, opcode, content, offset, length);
    }
    
    public byte setFlag(final byte flags, final int rsv) {
        if (rsv < 1 || rsv > 3) {
            throw new IllegalArgumentException("rsv" + rsv);
        }
        final byte b = (byte)(flags | AbstractExtension.__mask[rsv]);
        return b;
    }
    
    public byte clearFlag(final byte flags, final int rsv) {
        if (rsv < 1 || rsv > 3) {
            throw new IllegalArgumentException("rsv" + rsv);
        }
        return (byte)(flags & ~AbstractExtension.__mask[rsv]);
    }
    
    public boolean isFlag(final byte flags, final int rsv) {
        if (rsv < 1 || rsv > 3) {
            throw new IllegalArgumentException("rsv" + rsv);
        }
        return (flags & AbstractExtension.__mask[rsv]) != 0x0;
    }
    
    @Override
    public String toString() {
        return this.getParameterizedName();
    }
    
    static {
        __mask = new int[] { -1, 4, 2, 1 };
    }
}
