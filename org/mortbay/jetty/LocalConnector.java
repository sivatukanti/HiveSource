// 
// Decompiled by Procyon v0.5.36
// 

package org.mortbay.jetty;

import java.io.IOException;
import org.mortbay.io.EndPoint;
import org.mortbay.io.Buffer;
import org.mortbay.io.ByteArrayBuffer;
import org.mortbay.io.ByteArrayEndPoint;

public class LocalConnector extends AbstractConnector
{
    ByteArrayEndPoint _endp;
    ByteArrayBuffer _in;
    ByteArrayBuffer _out;
    Server _server;
    boolean _accepting;
    boolean _keepOpen;
    
    public LocalConnector() {
        this.setPort(1);
    }
    
    public Object getConnection() {
        return this._endp;
    }
    
    public void setServer(final Server server) {
        super.setServer(server);
        this._server = server;
    }
    
    public void clear() {
        this._in.clear();
        this._out.clear();
    }
    
    public void reopen() {
        this._in.clear();
        this._out.clear();
        (this._endp = new ByteArrayEndPoint()).setIn(this._in);
        this._endp.setOut(this._out);
        this._endp.setGrowOutput(true);
        this._accepting = false;
    }
    
    public void doStart() throws Exception {
        this._in = new ByteArrayBuffer(8192);
        this._out = new ByteArrayBuffer(8192);
        (this._endp = new ByteArrayEndPoint()).setIn(this._in);
        this._endp.setOut(this._out);
        this._endp.setGrowOutput(true);
        this._accepting = false;
        super.doStart();
    }
    
    public String getResponses(final String requests) throws Exception {
        return this.getResponses(requests, false);
    }
    
    public String getResponses(final String requests, final boolean keepOpen) throws Exception {
        final ByteArrayBuffer buf = new ByteArrayBuffer(requests);
        if (this._in.space() < buf.length()) {
            final ByteArrayBuffer n = new ByteArrayBuffer(this._in.length() + buf.length());
            n.put(this._in);
            this._in = n;
            this._endp.setIn(this._in);
        }
        this._in.put(buf);
        synchronized (this) {
            this._keepOpen = keepOpen;
            this._accepting = true;
            this.notify();
            while (this._accepting) {
                this.wait();
            }
        }
        this._out = this._endp.getOut();
        return this._out.toString();
    }
    
    public ByteArrayBuffer getResponses(final ByteArrayBuffer buf, final boolean keepOpen) throws Exception {
        if (this._in.space() < buf.length()) {
            final ByteArrayBuffer n = new ByteArrayBuffer(this._in.length() + buf.length());
            n.put(this._in);
            this._in = n;
            this._endp.setIn(this._in);
        }
        this._in.put(buf);
        synchronized (this) {
            this._keepOpen = keepOpen;
            this._accepting = true;
            this.notify();
            while (this._accepting) {
                this.wait();
            }
        }
        return this._out = this._endp.getOut();
    }
    
    protected Buffer newBuffer(final int size) {
        return new ByteArrayBuffer(size);
    }
    
    protected void accept(final int acceptorID) throws IOException, InterruptedException {
        HttpConnection connection = null;
        while (this.isRunning()) {
            synchronized (this) {
                try {
                    while (!this._accepting) {
                        this.wait();
                    }
                }
                catch (InterruptedException e) {
                    return;
                }
            }
            try {
                if (connection == null) {
                    connection = new HttpConnection(this, this._endp, this.getServer());
                    this.connectionOpened(connection);
                }
                while (this._in.length() > 0) {
                    connection.handle();
                }
            }
            finally {
                if (!this._keepOpen) {
                    this.connectionClosed(connection);
                    connection.destroy();
                    connection = null;
                }
                synchronized (this) {
                    this._accepting = false;
                    this.notify();
                }
            }
        }
    }
    
    public void open() throws IOException {
    }
    
    public void close() throws IOException {
    }
    
    public int getLocalPort() {
        return -1;
    }
}
