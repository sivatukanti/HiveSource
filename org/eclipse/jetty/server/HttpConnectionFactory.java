// 
// Decompiled by Procyon v0.5.36
// 

package org.eclipse.jetty.server;

import org.eclipse.jetty.io.AbstractConnection;
import org.eclipse.jetty.io.Connection;
import org.eclipse.jetty.io.EndPoint;
import org.eclipse.jetty.http.HttpVersion;
import org.eclipse.jetty.util.annotation.Name;
import org.eclipse.jetty.http.HttpCompliance;

public class HttpConnectionFactory extends AbstractConnectionFactory implements HttpConfiguration.ConnectionFactory
{
    private final HttpConfiguration _config;
    private HttpCompliance _httpCompliance;
    private boolean _recordHttpComplianceViolations;
    
    public HttpConnectionFactory() {
        this(new HttpConfiguration());
    }
    
    public HttpConnectionFactory(@Name("config") final HttpConfiguration config) {
        this(config, null);
    }
    
    public HttpConnectionFactory(@Name("config") final HttpConfiguration config, @Name("compliance") final HttpCompliance compliance) {
        super(HttpVersion.HTTP_1_1.asString());
        this._recordHttpComplianceViolations = false;
        this._config = config;
        this._httpCompliance = ((compliance == null) ? HttpCompliance.RFC7230 : compliance);
        if (config == null) {
            throw new IllegalArgumentException("Null HttpConfiguration");
        }
        this.addBean(this._config);
    }
    
    @Override
    public HttpConfiguration getHttpConfiguration() {
        return this._config;
    }
    
    public HttpCompliance getHttpCompliance() {
        return this._httpCompliance;
    }
    
    public boolean isRecordHttpComplianceViolations() {
        return this._recordHttpComplianceViolations;
    }
    
    public void setHttpCompliance(final HttpCompliance httpCompliance) {
        this._httpCompliance = httpCompliance;
    }
    
    @Override
    public Connection newConnection(final Connector connector, final EndPoint endPoint) {
        final HttpConnection conn = new HttpConnection(this._config, connector, endPoint, this._httpCompliance, this.isRecordHttpComplianceViolations());
        return this.configure(conn, connector, endPoint);
    }
    
    public void setRecordHttpComplianceViolations(final boolean recordHttpComplianceViolations) {
        this._recordHttpComplianceViolations = recordHttpComplianceViolations;
    }
}
