// 
// Decompiled by Procyon v0.5.36
// 

package org.eclipse.jetty.server;

import org.eclipse.jetty.util.log.Log;
import java.io.IOException;
import org.eclipse.jetty.io.nio.SelectChannelEndPoint;
import org.eclipse.jetty.http.HttpException;
import org.eclipse.jetty.io.Connection;
import org.eclipse.jetty.io.EndPoint;
import org.eclipse.jetty.io.AsyncEndPoint;
import org.eclipse.jetty.util.log.Logger;
import org.eclipse.jetty.io.nio.AsyncConnection;

public class AsyncHttpConnection extends AbstractHttpConnection implements AsyncConnection
{
    private static final int NO_PROGRESS_INFO;
    private static final int NO_PROGRESS_CLOSE;
    private static final Logger LOG;
    private int _total_no_progress;
    private final AsyncEndPoint _asyncEndp;
    
    public AsyncHttpConnection(final Connector connector, final EndPoint endpoint, final Server server) {
        super(connector, endpoint, server);
        this._asyncEndp = (AsyncEndPoint)endpoint;
    }
    
    @Override
    public Connection handle() throws IOException {
        Connection connection = this;
        boolean some_progress = false;
        boolean progress = true;
        try {
            AbstractHttpConnection.setCurrentConnection(this);
            this._asyncEndp.setCheckForIdle(false);
            while (progress && connection == this) {
                progress = false;
                try {
                    if (this._request._async.isAsync()) {
                        if (this._request._async.isDispatchable()) {
                            this.handleRequest();
                        }
                    }
                    else if (!this._parser.isComplete() && this._parser.parseAvailable()) {
                        progress = true;
                    }
                    if (this._generator.isCommitted() && !this._generator.isComplete() && !this._endp.isOutputShutdown() && this._generator.flushBuffer() > 0) {
                        progress = true;
                    }
                    this._endp.flush();
                    if (!this._asyncEndp.hasProgressed()) {
                        continue;
                    }
                    progress = true;
                }
                catch (HttpException e) {
                    if (AsyncHttpConnection.LOG.isDebugEnabled()) {
                        AsyncHttpConnection.LOG.debug("uri=" + this._uri, new Object[0]);
                        AsyncHttpConnection.LOG.debug("fields=" + this._requestFields, new Object[0]);
                        AsyncHttpConnection.LOG.debug(e);
                    }
                    progress = true;
                    this._generator.sendError(e.getStatus(), e.getReason(), null, true);
                }
                finally {
                    some_progress |= progress;
                    if (this._parser.isComplete() && this._generator.isComplete()) {
                        progress = true;
                        if (this._response.getStatus() == 101) {
                            final Connection switched = (Connection)this._request.getAttribute("org.eclipse.jetty.io.Connection");
                            if (switched != null) {
                                connection = switched;
                            }
                        }
                        this.reset();
                        if (!this._generator.isPersistent() && !this._endp.isOutputShutdown()) {
                            AsyncHttpConnection.LOG.warn("Safety net oshut!!!  IF YOU SEE THIS, PLEASE RAISE BUGZILLA", new Object[0]);
                            this._endp.shutdownOutput();
                        }
                    }
                    else if (this._request.getAsyncContinuation().isAsyncStarted()) {
                        AsyncHttpConnection.LOG.debug("suspended {}", this);
                        progress = false;
                    }
                }
            }
        }
        finally {
            AbstractHttpConnection.setCurrentConnection(null);
            if (!this._request.getAsyncContinuation().isAsyncStarted()) {
                this._parser.returnBuffers();
                this._generator.returnBuffers();
                this._asyncEndp.setCheckForIdle(true);
            }
            if (some_progress) {
                this._total_no_progress = 0;
            }
            else {
                ++this._total_no_progress;
                if (AsyncHttpConnection.NO_PROGRESS_INFO > 0 && this._total_no_progress % AsyncHttpConnection.NO_PROGRESS_INFO == 0 && (AsyncHttpConnection.NO_PROGRESS_CLOSE <= 0 || this._total_no_progress < AsyncHttpConnection.NO_PROGRESS_CLOSE)) {
                    AsyncHttpConnection.LOG.info("EndPoint making no progress: " + this._total_no_progress + " " + this._endp + " " + this, new Object[0]);
                }
                if (AsyncHttpConnection.NO_PROGRESS_CLOSE > 0 && this._total_no_progress == AsyncHttpConnection.NO_PROGRESS_CLOSE) {
                    AsyncHttpConnection.LOG.warn("Closing EndPoint making no progress: " + this._total_no_progress + " " + this._endp + " " + this, new Object[0]);
                    if (this._endp instanceof SelectChannelEndPoint) {
                        ((SelectChannelEndPoint)this._endp).getChannel().close();
                    }
                }
            }
        }
        return connection;
    }
    
    public void onInputShutdown() throws IOException {
        if (this._generator.isIdle() && !this._request.getAsyncContinuation().isSuspended()) {
            this._endp.close();
        }
        if (this._parser.isIdle()) {
            this._parser.setPersistent(false);
        }
    }
    
    static {
        NO_PROGRESS_INFO = Integer.getInteger("org.mortbay.jetty.NO_PROGRESS_INFO", 100);
        NO_PROGRESS_CLOSE = Integer.getInteger("org.mortbay.jetty.NO_PROGRESS_CLOSE", 200);
        LOG = Log.getLogger(AsyncHttpConnection.class);
    }
}
