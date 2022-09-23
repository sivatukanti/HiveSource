// 
// Decompiled by Procyon v0.5.36
// 

package org.eclipse.jetty.server;

import org.eclipse.jetty.util.StringUtil;
import org.eclipse.jetty.http.QuotedCSV;
import java.util.Iterator;
import org.eclipse.jetty.http.HttpFields;
import org.eclipse.jetty.http.HttpScheme;
import java.net.InetSocketAddress;
import org.eclipse.jetty.http.HttpField;
import org.eclipse.jetty.http.HttpHeader;
import org.eclipse.jetty.http.HostPortHttpField;

public class ForwardedRequestCustomizer implements HttpConfiguration.Customizer
{
    private HostPortHttpField _forcedHost;
    private String _forwardedHeader;
    private String _forwardedHostHeader;
    private String _forwardedServerHeader;
    private String _forwardedForHeader;
    private String _forwardedProtoHeader;
    private String _forwardedHttpsHeader;
    private String _forwardedCipherSuiteHeader;
    private String _forwardedSslSessionIdHeader;
    private boolean _proxyAsAuthority;
    private boolean _sslIsSecure;
    
    public ForwardedRequestCustomizer() {
        this._forwardedHeader = HttpHeader.FORWARDED.toString();
        this._forwardedHostHeader = HttpHeader.X_FORWARDED_HOST.toString();
        this._forwardedServerHeader = HttpHeader.X_FORWARDED_SERVER.toString();
        this._forwardedForHeader = HttpHeader.X_FORWARDED_FOR.toString();
        this._forwardedProtoHeader = HttpHeader.X_FORWARDED_PROTO.toString();
        this._forwardedHttpsHeader = "X-Proxied-Https";
        this._forwardedCipherSuiteHeader = "Proxy-auth-cert";
        this._forwardedSslSessionIdHeader = "Proxy-ssl-id";
        this._proxyAsAuthority = false;
        this._sslIsSecure = true;
    }
    
    public boolean getProxyAsAuthority() {
        return this._proxyAsAuthority;
    }
    
    public void setProxyAsAuthority(final boolean proxyAsAuthority) {
        this._proxyAsAuthority = proxyAsAuthority;
    }
    
    public void setForwardedOnly(final boolean rfc7239only) {
        if (rfc7239only) {
            if (this._forwardedHeader == null) {
                this._forwardedHeader = HttpHeader.FORWARDED.toString();
            }
            this._forwardedHostHeader = null;
            this._forwardedHostHeader = null;
            this._forwardedServerHeader = null;
            this._forwardedForHeader = null;
            this._forwardedProtoHeader = null;
            this._forwardedHttpsHeader = null;
        }
        else {
            if (this._forwardedHostHeader == null) {
                this._forwardedHostHeader = HttpHeader.X_FORWARDED_HOST.toString();
            }
            if (this._forwardedServerHeader == null) {
                this._forwardedServerHeader = HttpHeader.X_FORWARDED_SERVER.toString();
            }
            if (this._forwardedForHeader == null) {
                this._forwardedForHeader = HttpHeader.X_FORWARDED_FOR.toString();
            }
            if (this._forwardedProtoHeader == null) {
                this._forwardedProtoHeader = HttpHeader.X_FORWARDED_PROTO.toString();
            }
            if (this._forwardedHttpsHeader == null) {
                this._forwardedHttpsHeader = "X-Proxied-Https";
            }
        }
    }
    
    public String getForcedHost() {
        return this._forcedHost.getValue();
    }
    
    public void setForcedHost(final String hostAndPort) {
        this._forcedHost = new HostPortHttpField(hostAndPort);
    }
    
    public String getForwardedHeader() {
        return this._forwardedHeader;
    }
    
    public void setForwardedHeader(final String forwardedHeader) {
        this._forwardedHeader = forwardedHeader;
    }
    
    public String getForwardedHostHeader() {
        return this._forwardedHostHeader;
    }
    
    public void setForwardedHostHeader(final String forwardedHostHeader) {
        this._forwardedHostHeader = forwardedHostHeader;
    }
    
    public String getForwardedServerHeader() {
        return this._forwardedServerHeader;
    }
    
    public void setForwardedServerHeader(final String forwardedServerHeader) {
        this._forwardedServerHeader = forwardedServerHeader;
    }
    
    public String getForwardedForHeader() {
        return this._forwardedForHeader;
    }
    
    public void setForwardedForHeader(final String forwardedRemoteAddressHeader) {
        this._forwardedForHeader = forwardedRemoteAddressHeader;
    }
    
    public String getForwardedProtoHeader() {
        return this._forwardedProtoHeader;
    }
    
    public void setForwardedProtoHeader(final String forwardedProtoHeader) {
        this._forwardedProtoHeader = forwardedProtoHeader;
    }
    
    public String getForwardedCipherSuiteHeader() {
        return this._forwardedCipherSuiteHeader;
    }
    
    public void setForwardedCipherSuiteHeader(final String forwardedCipherSuite) {
        this._forwardedCipherSuiteHeader = forwardedCipherSuite;
    }
    
    public String getForwardedSslSessionIdHeader() {
        return this._forwardedSslSessionIdHeader;
    }
    
    public void setForwardedSslSessionIdHeader(final String forwardedSslSessionId) {
        this._forwardedSslSessionIdHeader = forwardedSslSessionId;
    }
    
    public String getForwardedHttpsHeader() {
        return this._forwardedHttpsHeader;
    }
    
    public void setForwardedHttpsHeader(final String forwardedHttpsHeader) {
        this._forwardedHttpsHeader = forwardedHttpsHeader;
    }
    
    public boolean isSslIsSecure() {
        return this._sslIsSecure;
    }
    
    public void setSslIsSecure(final boolean sslIsSecure) {
        this._sslIsSecure = sslIsSecure;
    }
    
    @Override
    public void customize(final Connector connector, final HttpConfiguration config, final Request request) {
        final HttpFields httpFields = request.getHttpFields();
        RFC7239 rfc7239 = null;
        String forwardedHost = null;
        String forwardedServer = null;
        String forwardedFor = null;
        String forwardedProto = null;
        String forwardedHttps = null;
        for (final HttpField field : httpFields) {
            final String name = field.getName();
            if (this.getForwardedCipherSuiteHeader() != null && this.getForwardedCipherSuiteHeader().equalsIgnoreCase(name)) {
                request.setAttribute("javax.servlet.request.cipher_suite", field.getValue());
                if (this.isSslIsSecure()) {
                    request.setSecure(true);
                    request.setScheme(config.getSecureScheme());
                }
            }
            if (this.getForwardedSslSessionIdHeader() != null && this.getForwardedSslSessionIdHeader().equalsIgnoreCase(name)) {
                request.setAttribute("javax.servlet.request.ssl_session_id", field.getValue());
                if (this.isSslIsSecure()) {
                    request.setSecure(true);
                    request.setScheme(config.getSecureScheme());
                }
            }
            if (forwardedHost == null && this._forwardedHostHeader != null && this._forwardedHostHeader.equalsIgnoreCase(name)) {
                forwardedHost = this.getLeftMost(field.getValue());
            }
            if (forwardedServer == null && this._forwardedServerHeader != null && this._forwardedServerHeader.equalsIgnoreCase(name)) {
                forwardedServer = this.getLeftMost(field.getValue());
            }
            if (forwardedFor == null && this._forwardedForHeader != null && this._forwardedForHeader.equalsIgnoreCase(name)) {
                forwardedFor = this.getLeftMost(field.getValue());
            }
            if (forwardedProto == null && this._forwardedProtoHeader != null && this._forwardedProtoHeader.equalsIgnoreCase(name)) {
                forwardedProto = this.getLeftMost(field.getValue());
            }
            if (forwardedHttps == null && this._forwardedHttpsHeader != null && this._forwardedHttpsHeader.equalsIgnoreCase(name)) {
                forwardedHttps = this.getLeftMost(field.getValue());
            }
            if (this._forwardedHeader != null && this._forwardedHeader.equalsIgnoreCase(name)) {
                if (rfc7239 == null) {
                    rfc7239 = new RFC7239();
                }
                rfc7239.addValue(field.getValue());
            }
        }
        if (this._forcedHost != null) {
            httpFields.put(this._forcedHost);
            request.setAuthority(this._forcedHost.getHost(), this._forcedHost.getPort());
        }
        else if (rfc7239 != null && rfc7239._host != null) {
            final HostPortHttpField auth = rfc7239._host;
            httpFields.put(auth);
            request.setAuthority(auth.getHost(), auth.getPort());
        }
        else if (forwardedHost != null) {
            final HostPortHttpField auth = new HostPortHttpField(forwardedHost);
            httpFields.put(auth);
            request.setAuthority(auth.getHost(), auth.getPort());
        }
        else if (this._proxyAsAuthority) {
            if (rfc7239 != null && rfc7239._by != null) {
                final HostPortHttpField auth = rfc7239._by;
                httpFields.put(auth);
                request.setAuthority(auth.getHost(), auth.getPort());
            }
            else if (forwardedServer != null) {
                request.setAuthority(forwardedServer, request.getServerPort());
            }
        }
        if (rfc7239 != null && rfc7239._for != null) {
            request.setRemoteAddr(InetSocketAddress.createUnresolved(rfc7239._for.getHost(), rfc7239._for.getPort()));
        }
        else if (forwardedFor != null) {
            request.setRemoteAddr(InetSocketAddress.createUnresolved(forwardedFor, request.getRemotePort()));
        }
        if (rfc7239 != null && rfc7239._proto != null) {
            request.setScheme(rfc7239._proto);
            if (rfc7239._proto.equals(config.getSecureScheme())) {
                request.setSecure(true);
            }
        }
        else if (forwardedProto != null) {
            request.setScheme(forwardedProto);
            if (forwardedProto.equals(config.getSecureScheme())) {
                request.setSecure(true);
            }
        }
        else if (forwardedHttps != null && ("on".equalsIgnoreCase(forwardedHttps) || "true".equalsIgnoreCase(forwardedHttps))) {
            request.setScheme(HttpScheme.HTTPS.asString());
            if (HttpScheme.HTTPS.asString().equals(config.getSecureScheme())) {
                request.setSecure(true);
            }
        }
    }
    
    protected String getLeftMost(final String headerValue) {
        if (headerValue == null) {
            return null;
        }
        final int commaIndex = headerValue.indexOf(44);
        if (commaIndex == -1) {
            return headerValue;
        }
        return headerValue.substring(0, commaIndex).trim();
    }
    
    @Override
    public String toString() {
        return String.format("%s@%x", this.getClass().getSimpleName(), this.hashCode());
    }
    
    @Deprecated
    public String getHostHeader() {
        return this._forcedHost.getValue();
    }
    
    @Deprecated
    public void setHostHeader(final String hostHeader) {
        this._forcedHost = new HostPortHttpField(hostHeader);
    }
    
    private final class RFC7239 extends QuotedCSV
    {
        HostPortHttpField _by;
        HostPortHttpField _for;
        HostPortHttpField _host;
        String _proto;
        
        private RFC7239() {
            super(false, new String[0]);
        }
        
        @Override
        protected void parsedParam(final StringBuffer buffer, final int valueLength, final int paramName, final int paramValue) {
            if (valueLength == 0 && paramValue > paramName) {
                final String name = StringUtil.asciiToLowerCase(buffer.substring(paramName, paramValue - 1));
                final String value = buffer.substring(paramValue);
                final String s = name;
                switch (s) {
                    case "by": {
                        if (this._by == null && !value.startsWith("_") && !"unknown".equals(value)) {
                            this._by = new HostPortHttpField(value);
                            break;
                        }
                        break;
                    }
                    case "for": {
                        if (this._for == null && !value.startsWith("_") && !"unknown".equals(value)) {
                            this._for = new HostPortHttpField(value);
                            break;
                        }
                        break;
                    }
                    case "host": {
                        if (this._host == null) {
                            this._host = new HostPortHttpField(value);
                            break;
                        }
                        break;
                    }
                    case "proto": {
                        if (this._proto == null) {
                            this._proto = value;
                            break;
                        }
                        break;
                    }
                }
            }
        }
    }
}
