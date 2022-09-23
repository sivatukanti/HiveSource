// 
// Decompiled by Procyon v0.5.36
// 

package org.eclipse.jetty.server;

import org.eclipse.jetty.util.log.Log;
import org.eclipse.jetty.http.HttpException;
import org.eclipse.jetty.io.Connection;
import java.io.IOException;
import org.eclipse.jetty.http.Generator;
import org.eclipse.jetty.http.Parser;
import org.eclipse.jetty.io.EndPoint;
import org.eclipse.jetty.util.log.Logger;

public class BlockingHttpConnection extends AbstractHttpConnection
{
    private static final Logger LOG;
    
    public BlockingHttpConnection(final Connector connector, final EndPoint endpoint, final Server server) {
        super(connector, endpoint, server);
    }
    
    public BlockingHttpConnection(final Connector connector, final EndPoint endpoint, final Server server, final Parser parser, final Generator generator, final Request request) {
        super(connector, endpoint, server, parser, generator, request);
    }
    
    @Override
    protected void handleRequest() throws IOException {
        super.handleRequest();
    }
    
    @Override
    public Connection handle() throws IOException {
        Connection connection = this;
        try {
            AbstractHttpConnection.setCurrentConnection(this);
            while (this._endp.isOpen() && connection == this) {
                try {
                    if (!this._parser.isComplete() && !this._endp.isInputShutdown()) {
                        this._parser.parseAvailable();
                    }
                    if (this._generator.isCommitted() && !this._generator.isComplete() && !this._endp.isOutputShutdown()) {
                        this._generator.flushBuffer();
                    }
                    this._endp.flush();
                }
                catch (HttpException e) {
                    if (BlockingHttpConnection.LOG.isDebugEnabled()) {
                        BlockingHttpConnection.LOG.debug("uri=" + this._uri, new Object[0]);
                        BlockingHttpConnection.LOG.debug("fields=" + this._requestFields, new Object[0]);
                        BlockingHttpConnection.LOG.debug(e);
                    }
                    this._generator.sendError(e.getStatus(), e.getReason(), null, true);
                    this._parser.reset();
                    this._endp.shutdownOutput();
                }
                finally {
                    if (this._parser.isComplete() && this._generator.isComplete()) {
                        this.reset();
                        if (this._response.getStatus() == 101) {
                            final Connection switched = (Connection)this._request.getAttribute("org.eclipse.jetty.io.Connection");
                            if (switched != null) {
                                connection = switched;
                            }
                        }
                        if (!this._generator.isPersistent() && !this._endp.isOutputShutdown()) {
                            BlockingHttpConnection.LOG.warn("Safety net oshut!!! Please open a bugzilla", new Object[0]);
                            this._endp.shutdownOutput();
                        }
                    }
                    if (this._endp.isInputShutdown() && this._generator.isIdle() && !this._request.getAsyncContinuation().isSuspended()) {
                        this._endp.close();
                    }
                }
            }
            return connection;
        }
        finally {
            AbstractHttpConnection.setCurrentConnection(null);
            this._parser.returnBuffers();
            this._generator.returnBuffers();
        }
    }
    
    static {
        LOG = Log.getLogger(BlockingHttpConnection.class);
    }
}
