// 
// Decompiled by Procyon v0.5.36
// 

package org.eclipse.jetty.client;

import org.eclipse.jetty.util.log.Log;
import org.eclipse.jetty.io.Connection;
import java.io.IOException;
import org.eclipse.jetty.io.EndPoint;
import org.eclipse.jetty.io.Buffers;
import org.eclipse.jetty.io.AsyncEndPoint;
import org.eclipse.jetty.io.Buffer;
import org.eclipse.jetty.util.log.Logger;
import org.eclipse.jetty.io.nio.AsyncConnection;

public class AsyncHttpConnection extends AbstractHttpConnection implements AsyncConnection
{
    private static final Logger LOG;
    private boolean _requestComplete;
    private Buffer _requestContentChunk;
    private final AsyncEndPoint _asyncEndp;
    
    AsyncHttpConnection(final Buffers requestBuffers, final Buffers responseBuffers, final EndPoint endp) {
        super(requestBuffers, responseBuffers, endp);
        this._asyncEndp = (AsyncEndPoint)endp;
    }
    
    @Override
    protected void reset() throws IOException {
        this._requestComplete = false;
        super.reset();
    }
    
    @Override
    public Connection handle() throws IOException {
        Connection connection = this;
        boolean progress = true;
        try {
            boolean failed = false;
            while (progress && connection == this) {
                AsyncHttpConnection.LOG.debug("while open={} more={} progress={}", this._endp.isOpen(), this._parser.isMoreInBuffer(), progress);
                progress = false;
                HttpExchange exchange = this._exchange;
                AsyncHttpConnection.LOG.debug("exchange {} on {}", exchange, this);
                try {
                    if (!this._generator.isCommitted() && exchange != null && exchange.getStatus() == 2) {
                        AsyncHttpConnection.LOG.debug("commit {}", exchange);
                        progress = true;
                        this.commitRequest();
                    }
                    if (this._generator.isCommitted() && !this._generator.isComplete()) {
                        if (this._generator.flushBuffer() > 0) {
                            AsyncHttpConnection.LOG.debug("flushed", new Object[0]);
                            progress = true;
                        }
                        if (this._generator.isState(2)) {
                            if (this._requestContentChunk == null) {
                                this._requestContentChunk = exchange.getRequestContentChunk(null);
                            }
                            if (this._requestContentChunk == null) {
                                AsyncHttpConnection.LOG.debug("complete {}", exchange);
                                progress = true;
                                this._generator.complete();
                            }
                            else if (this._generator.isEmpty()) {
                                AsyncHttpConnection.LOG.debug("addChunk", new Object[0]);
                                progress = true;
                                final Buffer chunk = this._requestContentChunk;
                                this._requestContentChunk = exchange.getRequestContentChunk(null);
                                this._generator.addContent(chunk, this._requestContentChunk == null);
                            }
                        }
                    }
                    if (this._generator.isComplete() && !this._requestComplete) {
                        AsyncHttpConnection.LOG.debug("requestComplete {}", exchange);
                        progress = true;
                        this._requestComplete = true;
                        exchange.getEventListener().onRequestComplete();
                    }
                    if (!this._parser.isComplete() && this._parser.parseAvailable()) {
                        AsyncHttpConnection.LOG.debug("parsed {}", exchange);
                        progress = true;
                    }
                    this._endp.flush();
                    if (this._asyncEndp.hasProgressed()) {
                        AsyncHttpConnection.LOG.debug("hasProgressed {}", exchange);
                        progress = true;
                    }
                }
                catch (Throwable e) {
                    AsyncHttpConnection.LOG.debug("Failure on " + this._exchange, e);
                    failed = true;
                    synchronized (this) {
                        if (exchange != null) {
                            if (exchange.getStatus() != 10 && exchange.getStatus() != 11 && !exchange.isDone() && exchange.setStatus(9)) {
                                exchange.getEventListener().onException(e);
                            }
                        }
                        else {
                            if (e instanceof IOException) {
                                throw (IOException)e;
                            }
                            if (e instanceof Error) {
                                throw (Error)e;
                            }
                            if (e instanceof RuntimeException) {
                                throw (RuntimeException)e;
                            }
                            throw new RuntimeException(e);
                        }
                    }
                }
                finally {
                    AsyncHttpConnection.LOG.debug("finally {} on {} progress={} {}", exchange, this, progress, this._endp);
                    final boolean complete = failed || (this._generator.isComplete() && this._parser.isComplete());
                    if (complete) {
                        final boolean persistent = !failed && this._parser.isPersistent() && this._generator.isPersistent();
                        this._generator.setPersistent(persistent);
                        this.reset();
                        if (persistent) {
                            this._endp.setMaxIdleTime((int)this._destination.getHttpClient().getIdleTimeout());
                        }
                        synchronized (this) {
                            exchange = this._exchange;
                            this._exchange = null;
                            if (exchange != null) {
                                exchange.cancelTimeout(this._destination.getHttpClient());
                            }
                            if (this._status == 101) {
                                final Connection switched = exchange.onSwitchProtocol(this._endp);
                                if (switched != null) {
                                    connection = switched;
                                }
                                this._pipeline = null;
                                if (this._pipeline != null) {
                                    this._destination.send(this._pipeline);
                                }
                                this._pipeline = null;
                                connection = switched;
                            }
                            if (this._pipeline != null) {
                                if (!persistent || connection != this) {
                                    this._destination.send(this._pipeline);
                                }
                                else {
                                    this._exchange = this._pipeline;
                                }
                                this._pipeline = null;
                            }
                            if (this._exchange == null && !this.isReserved()) {
                                this._destination.returnConnection(this, !persistent);
                            }
                        }
                    }
                }
            }
        }
        finally {
            this._parser.returnBuffers();
            this._generator.returnBuffers();
            AsyncHttpConnection.LOG.debug("unhandle {} on {}", this._exchange, this._endp);
        }
        return connection;
    }
    
    public void onInputShutdown() throws IOException {
        if (this._generator.isIdle()) {
            this._endp.shutdownOutput();
        }
    }
    
    @Override
    public boolean send(final HttpExchange ex) throws IOException {
        final boolean sent = super.send(ex);
        if (sent) {
            this._asyncEndp.asyncDispatch();
        }
        return sent;
    }
    
    static {
        LOG = Log.getLogger(AsyncHttpConnection.class);
    }
}
