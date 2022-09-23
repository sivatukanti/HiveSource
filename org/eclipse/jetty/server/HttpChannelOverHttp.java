// 
// Decompiled by Procyon v0.5.36
// 

package org.eclipse.jetty.server;

import org.eclipse.jetty.util.log.Log;
import java.util.ArrayList;
import org.eclipse.jetty.http.HttpCompliance;
import org.eclipse.jetty.io.Connection;
import java.util.Iterator;
import org.eclipse.jetty.http.BadMessageException;
import org.eclipse.jetty.http.HttpMethod;
import java.nio.ByteBuffer;
import org.eclipse.jetty.http.HttpGenerator;
import java.io.IOException;
import org.eclipse.jetty.http.HttpHeader;
import org.eclipse.jetty.http.HttpHeaderValue;
import org.eclipse.jetty.http.HostPortHttpField;
import org.eclipse.jetty.http.HttpVersion;
import org.eclipse.jetty.http.HttpURI;
import org.eclipse.jetty.io.EndPoint;
import java.util.List;
import org.eclipse.jetty.http.MetaData;
import org.eclipse.jetty.http.HttpFields;
import org.eclipse.jetty.http.HttpField;
import org.eclipse.jetty.util.log.Logger;
import org.eclipse.jetty.http.HttpParser;

public class HttpChannelOverHttp extends HttpChannel implements HttpParser.RequestHandler, HttpParser.ComplianceHandler
{
    private static final Logger LOG;
    private static final HttpField PREAMBLE_UPGRADE_H2C;
    private static final String ATTR_COMPLIANCE_VIOLATIONS = "org.eclipse.jetty.http.compliance.violations";
    private final HttpFields _fields;
    private final MetaData.Request _metadata;
    private final HttpConnection _httpConnection;
    private HttpField _connection;
    private HttpField _upgrade;
    private boolean _delayedForContent;
    private boolean _unknownExpectation;
    private boolean _expect100Continue;
    private boolean _expect102Processing;
    private List<String> _complianceViolations;
    
    public HttpChannelOverHttp(final HttpConnection httpConnection, final Connector connector, final HttpConfiguration config, final EndPoint endPoint, final HttpTransport transport) {
        super(connector, config, endPoint, transport);
        this._fields = new HttpFields();
        this._metadata = new MetaData.Request(this._fields);
        this._upgrade = null;
        this._unknownExpectation = false;
        this._expect100Continue = false;
        this._expect102Processing = false;
        this._httpConnection = httpConnection;
        this._metadata.setURI(new HttpURI());
    }
    
    @Override
    protected HttpInput newHttpInput(final HttpChannelState state) {
        return new HttpInputOverHTTP(state);
    }
    
    @Override
    public void recycle() {
        super.recycle();
        this._unknownExpectation = false;
        this._expect100Continue = false;
        this._expect102Processing = false;
        this._metadata.recycle();
        this._connection = null;
        this._fields.clear();
        this._upgrade = null;
    }
    
    @Override
    public boolean isExpecting100Continue() {
        return this._expect100Continue;
    }
    
    @Override
    public boolean isExpecting102Processing() {
        return this._expect102Processing;
    }
    
    @Override
    public boolean startRequest(final String method, final String uri, final HttpVersion version) {
        this._metadata.setMethod(method);
        this._metadata.getURI().parseRequestTarget(method, uri);
        this._metadata.setHttpVersion(version);
        this._unknownExpectation = false;
        this._expect100Continue = false;
        return this._expect102Processing = false;
    }
    
    @Override
    public void parsedHeader(final HttpField field) {
        final HttpHeader header = field.getHeader();
        final String value = field.getValue();
        if (header != null) {
            switch (header) {
                case CONNECTION: {
                    this._connection = field;
                    break;
                }
                case HOST: {
                    if (!this._metadata.getURI().isAbsolute() && field instanceof HostPortHttpField) {
                        final HostPortHttpField hp = (HostPortHttpField)field;
                        this._metadata.getURI().setAuthority(hp.getHost(), hp.getPort());
                        break;
                    }
                    break;
                }
                case EXPECT: {
                    if (this._metadata.getHttpVersion() == HttpVersion.HTTP_1_1) {
                        HttpHeaderValue expect = HttpHeaderValue.CACHE.get(value);
                        switch ((expect == null) ? HttpHeaderValue.UNKNOWN : expect) {
                            case CONTINUE: {
                                this._expect100Continue = true;
                                break;
                            }
                            case PROCESSING: {
                                this._expect102Processing = true;
                                break;
                            }
                            default: {
                                final String[] values = field.getValues();
                                for (int i = 0; values != null && i < values.length; ++i) {
                                    expect = HttpHeaderValue.CACHE.get(values[i].trim());
                                    if (expect == null) {
                                        this._unknownExpectation = true;
                                    }
                                    else {
                                        switch (expect) {
                                            case CONTINUE: {
                                                this._expect100Continue = true;
                                                break;
                                            }
                                            case PROCESSING: {
                                                this._expect102Processing = true;
                                                break;
                                            }
                                            default: {
                                                this._unknownExpectation = true;
                                                break;
                                            }
                                        }
                                    }
                                }
                                break;
                            }
                        }
                        break;
                    }
                    break;
                }
                case UPGRADE: {
                    this._upgrade = field;
                    break;
                }
            }
        }
        this._fields.add(field);
    }
    
    @Override
    public void continue100(final int available) throws IOException {
        if (this.isExpecting100Continue()) {
            this._expect100Continue = false;
            if (available == 0) {
                if (this.getResponse().isCommitted()) {
                    throw new IOException("Committed before 100 Continues");
                }
                final boolean committed = this.sendResponse(HttpGenerator.CONTINUE_100_INFO, null, false);
                if (!committed) {
                    throw new IOException("Concurrent commit while trying to send 100-Continue");
                }
            }
        }
    }
    
    @Override
    public void earlyEOF() {
        this._httpConnection.getGenerator().setPersistent(false);
        if (this._metadata.getMethod() == null) {
            this._httpConnection.close();
        }
        else if (this.onEarlyEOF() || this._delayedForContent) {
            this._delayedForContent = false;
            this.handle();
        }
    }
    
    @Override
    public boolean content(final ByteBuffer content) {
        final HttpInput.Content c = this._httpConnection.newContent(content);
        final boolean handle = this.onContent(c) || this._delayedForContent;
        this._delayedForContent = false;
        return handle;
    }
    
    @Override
    public void onAsyncWaitForContent() {
        this._httpConnection.asyncReadFillInterested();
    }
    
    @Override
    public void onBlockWaitForContent() {
        this._httpConnection.blockingReadFillInterested();
    }
    
    @Override
    public void onBlockWaitForContentFailure(final Throwable failure) {
        this._httpConnection.blockingReadFailure(failure);
    }
    
    @Override
    public void badMessage(final int status, final String reason) {
        this._httpConnection.getGenerator().setPersistent(false);
        try {
            this.onRequest(this._metadata);
            this.getRequest().getHttpInput().earlyEOF();
        }
        catch (Exception e) {
            HttpChannelOverHttp.LOG.ignore(e);
        }
        this.onBadMessage(status, reason);
    }
    
    @Override
    public boolean headerComplete() {
        if (this._complianceViolations != null) {
            this.getRequest().setAttribute("org.eclipse.jetty.http.compliance.violations", this._complianceViolations);
        }
        boolean persistent = false;
        switch (this._metadata.getHttpVersion()) {
            case HTTP_0_9: {
                persistent = false;
                break;
            }
            case HTTP_1_0: {
                persistent = (this.getHttpConfiguration().isPersistentConnectionsEnabled() && this._connection != null && (this._connection.contains(HttpHeaderValue.KEEP_ALIVE.asString()) || this._fields.contains(HttpHeader.CONNECTION, HttpHeaderValue.KEEP_ALIVE.asString())));
                if (!persistent) {
                    persistent = HttpMethod.CONNECT.is(this._metadata.getMethod());
                }
                if (persistent) {
                    this.getResponse().getHttpFields().add(HttpHeader.CONNECTION, HttpHeaderValue.KEEP_ALIVE);
                    break;
                }
                break;
            }
            case HTTP_1_1: {
                if (this._unknownExpectation) {
                    this.badMessage(417, null);
                    return false;
                }
                persistent = (this.getHttpConfiguration().isPersistentConnectionsEnabled() && (this._connection == null || (!this._connection.contains(HttpHeaderValue.CLOSE.asString()) && !this._fields.contains(HttpHeader.CONNECTION, HttpHeaderValue.CLOSE.asString()))));
                if (!persistent) {
                    persistent = HttpMethod.CONNECT.is(this._metadata.getMethod());
                }
                if (!persistent) {
                    this.getResponse().getHttpFields().add(HttpHeader.CONNECTION, HttpHeaderValue.CLOSE);
                }
                if (this._upgrade != null && this.upgrade()) {
                    return true;
                }
                break;
            }
            case HTTP_2: {
                this._upgrade = HttpChannelOverHttp.PREAMBLE_UPGRADE_H2C;
                if (HttpMethod.PRI.is(this._metadata.getMethod()) && "*".equals(this._metadata.getURI().toString()) && this._fields.size() == 0 && this.upgrade()) {
                    return true;
                }
                this.badMessage(426, null);
                this._httpConnection.getParser().close();
                return false;
            }
            default: {
                throw new IllegalStateException("unsupported version " + this._metadata.getHttpVersion());
            }
        }
        if (!persistent) {
            this._httpConnection.getGenerator().setPersistent(false);
        }
        this.onRequest(this._metadata);
        this._delayedForContent = (this.getHttpConfiguration().isDelayDispatchUntilContent() && (this._httpConnection.getParser().getContentLength() > 0L || this._httpConnection.getParser().isChunking()) && !this.isExpecting100Continue() && !this.isCommitted() && this._httpConnection.isRequestBufferEmpty());
        return !this._delayedForContent;
    }
    
    private boolean upgrade() throws BadMessageException {
        if (HttpChannelOverHttp.LOG.isDebugEnabled()) {
            HttpChannelOverHttp.LOG.debug("upgrade {} {}", this, this._upgrade);
        }
        if (this._upgrade != HttpChannelOverHttp.PREAMBLE_UPGRADE_H2C && (this._connection == null || !this._connection.contains("upgrade"))) {
            throw new BadMessageException(400);
        }
        ConnectionFactory.Upgrading factory = null;
        for (final ConnectionFactory f : this.getConnector().getConnectionFactories()) {
            if (f instanceof ConnectionFactory.Upgrading && f.getProtocols().contains(this._upgrade.getValue())) {
                factory = (ConnectionFactory.Upgrading)f;
                break;
            }
        }
        if (factory == null) {
            if (HttpChannelOverHttp.LOG.isDebugEnabled()) {
                HttpChannelOverHttp.LOG.debug("No factory for {} in {}", this._upgrade, this.getConnector());
            }
            return false;
        }
        final HttpFields response101 = new HttpFields();
        final Connection upgrade_connection = factory.upgradeConnection(this.getConnector(), this.getEndPoint(), this._metadata, response101);
        if (upgrade_connection == null) {
            if (HttpChannelOverHttp.LOG.isDebugEnabled()) {
                HttpChannelOverHttp.LOG.debug("Upgrade ignored for {} by {}", this._upgrade, factory);
            }
            return false;
        }
        try {
            if (this._upgrade != HttpChannelOverHttp.PREAMBLE_UPGRADE_H2C) {
                this.sendResponse(new MetaData.Response(HttpVersion.HTTP_1_1, 101, response101, 0L), null, true);
            }
        }
        catch (IOException e) {
            throw new BadMessageException(500, null, e);
        }
        if (HttpChannelOverHttp.LOG.isDebugEnabled()) {
            HttpChannelOverHttp.LOG.debug("Upgrade from {} to {}", this.getEndPoint().getConnection(), upgrade_connection);
        }
        this.getRequest().setAttribute("org.eclipse.jetty.server.HttpConnection.UPGRADE", upgrade_connection);
        this.getResponse().setStatus(101);
        this.getHttpTransport().onCompleted();
        return true;
    }
    
    @Override
    protected void handleException(final Throwable x) {
        this._httpConnection.getGenerator().setPersistent(false);
        super.handleException(x);
    }
    
    @Override
    public void abort(final Throwable failure) {
        super.abort(failure);
        this._httpConnection.getGenerator().setPersistent(false);
    }
    
    @Override
    public boolean contentComplete() {
        final boolean handle = this.onContentComplete() || this._delayedForContent;
        this._delayedForContent = false;
        return handle;
    }
    
    @Override
    public boolean messageComplete() {
        return this.onRequestComplete();
    }
    
    @Override
    public int getHeaderCacheSize() {
        return this.getHttpConfiguration().getHeaderCacheSize();
    }
    
    @Override
    public void onComplianceViolation(final HttpCompliance compliance, final HttpCompliance required, final String reason) {
        if (this._httpConnection.isRecordHttpComplianceViolations()) {
            if (this._complianceViolations == null) {
                this._complianceViolations = new ArrayList<String>();
            }
            final String violation = String.format("%s<%s: %s for %s", compliance, required, reason, this.getHttpTransport());
            this._complianceViolations.add(violation);
            if (HttpChannelOverHttp.LOG.isDebugEnabled()) {
                HttpChannelOverHttp.LOG.debug(violation, new Object[0]);
            }
        }
    }
    
    static {
        LOG = Log.getLogger(HttpChannelOverHttp.class);
        PREAMBLE_UPGRADE_H2C = new HttpField(HttpHeader.UPGRADE, "h2c");
    }
}
