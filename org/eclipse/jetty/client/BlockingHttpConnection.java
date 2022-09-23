// 
// Decompiled by Procyon v0.5.36
// 

package org.eclipse.jetty.client;

import org.eclipse.jetty.util.log.Log;
import java.io.InterruptedIOException;
import org.eclipse.jetty.io.Connection;
import java.io.IOException;
import org.eclipse.jetty.io.EndPoint;
import org.eclipse.jetty.io.Buffers;
import org.eclipse.jetty.io.Buffer;
import org.eclipse.jetty.util.log.Logger;

public class BlockingHttpConnection extends AbstractHttpConnection
{
    private static final Logger LOG;
    private boolean _requestComplete;
    private Buffer _requestContentChunk;
    
    BlockingHttpConnection(final Buffers requestBuffers, final Buffers responseBuffers, final EndPoint endPoint) {
        super(requestBuffers, responseBuffers, endPoint);
    }
    
    @Override
    protected void reset() throws IOException {
        this._requestComplete = false;
        super.reset();
    }
    
    @Override
    public Connection handle() throws IOException {
        Connection connection = this;
        try {
            boolean failed = false;
            while (this._endp.isOpen() && connection == this) {
                BlockingHttpConnection.LOG.debug("open={} more={}", this._endp.isOpen(), this._parser.isMoreInBuffer());
                HttpExchange exchange;
                synchronized (this) {
                    exchange = this._exchange;
                    while (exchange == null) {
                        try {
                            this.wait();
                            exchange = this._exchange;
                            continue;
                        }
                        catch (InterruptedException e2) {
                            throw new InterruptedIOException();
                        }
                        break;
                    }
                }
                BlockingHttpConnection.LOG.debug("exchange {}", exchange);
                try {
                    if (!this._generator.isCommitted() && exchange != null && exchange.getStatus() == 2) {
                        BlockingHttpConnection.LOG.debug("commit", new Object[0]);
                        this.commitRequest();
                    }
                    while (this._generator.isCommitted() && !this._generator.isComplete()) {
                        if (this._generator.flushBuffer() > 0) {
                            BlockingHttpConnection.LOG.debug("flushed", new Object[0]);
                        }
                        if (this._generator.isState(2)) {
                            if (this._requestContentChunk == null) {
                                this._requestContentChunk = exchange.getRequestContentChunk(null);
                            }
                            if (this._requestContentChunk == null) {
                                BlockingHttpConnection.LOG.debug("complete", new Object[0]);
                                this._generator.complete();
                            }
                            else {
                                if (!this._generator.isEmpty()) {
                                    continue;
                                }
                                BlockingHttpConnection.LOG.debug("addChunk", new Object[0]);
                                final Buffer chunk = this._requestContentChunk;
                                this._requestContentChunk = exchange.getRequestContentChunk(null);
                                this._generator.addContent(chunk, this._requestContentChunk == null);
                            }
                        }
                    }
                    if (this._generator.isComplete() && !this._requestComplete) {
                        BlockingHttpConnection.LOG.debug("requestComplete", new Object[0]);
                        this._requestComplete = true;
                        exchange.getEventListener().onRequestComplete();
                    }
                    if (!this._parser.isComplete() && this._parser.parseAvailable()) {
                        BlockingHttpConnection.LOG.debug("parsed", new Object[0]);
                    }
                    this._endp.flush();
                }
                catch (Throwable e) {
                    BlockingHttpConnection.LOG.debug("Failure on " + this._exchange, e);
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
                    BlockingHttpConnection.LOG.debug("{} {}", this._generator, this._parser);
                    BlockingHttpConnection.LOG.debug("{}", this._endp);
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
        }
        return connection;
    }
    
    @Override
    public boolean send(final HttpExchange ex) throws IOException {
        final boolean sent = super.send(ex);
        if (sent) {
            synchronized (this) {
                this.notifyAll();
            }
        }
        return sent;
    }
    
    static {
        LOG = Log.getLogger(BlockingHttpConnection.class);
    }
}
