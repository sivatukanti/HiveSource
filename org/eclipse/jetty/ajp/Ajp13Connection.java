// 
// Decompiled by Procyon v0.5.36
// 

package org.eclipse.jetty.ajp;

import org.eclipse.jetty.http.HttpException;
import java.util.Iterator;
import java.util.Collection;
import java.security.cert.Certificate;
import java.security.cert.X509Certificate;
import java.io.InputStream;
import java.io.ByteArrayInputStream;
import java.security.cert.CertificateFactory;
import org.eclipse.jetty.io.Buffer;
import org.eclipse.jetty.util.log.Log;
import java.io.IOException;
import org.eclipse.jetty.http.HttpFields;
import org.eclipse.jetty.http.HttpURI;
import javax.servlet.ServletInputStream;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.http.Generator;
import org.eclipse.jetty.http.Parser;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.io.EndPoint;
import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.util.log.Logger;
import org.eclipse.jetty.server.BlockingHttpConnection;

public class Ajp13Connection extends BlockingHttpConnection
{
    private static final Logger LOG;
    
    public Ajp13Connection(final Connector connector, final EndPoint endPoint, final Server server) {
        super(connector, endPoint, server, new Ajp13Parser(connector.getRequestBuffers(), endPoint), new Ajp13Generator(connector.getResponseBuffers(), endPoint), new Ajp13Request());
        ((Ajp13Parser)this._parser).setEventHandler(new RequestHandler());
        ((Ajp13Parser)this._parser).setGenerator((Ajp13Generator)this._generator);
        ((Ajp13Request)this._request).setConnection(this);
    }
    
    @Override
    public boolean isConfidential(final Request request) {
        return ((Ajp13Request)request).isSslSecure();
    }
    
    @Override
    public boolean isIntegral(final Request request) {
        return ((Ajp13Request)request).isSslSecure();
    }
    
    @Override
    public ServletInputStream getInputStream() {
        if (this._in == null) {
            this._in = new Ajp13Parser.Input((Ajp13Parser)this._parser, this._connector.getMaxIdleTime());
        }
        return this._in;
    }
    
    static {
        LOG = Log.getLogger(Ajp13Connection.class);
    }
    
    private class RequestHandler implements Ajp13Parser.EventHandler
    {
        public void startForwardRequest() throws IOException {
            Ajp13Connection.this._uri.clear();
            ((Ajp13Request)Ajp13Connection.this._request).setSslSecure(false);
            Ajp13Connection.this._request.setTimeStamp(System.currentTimeMillis());
            Ajp13Connection.this._request.setUri(Ajp13Connection.this._uri);
        }
        
        public void parsedAuthorizationType(final Buffer authType) throws IOException {
        }
        
        public void parsedRemoteUser(final Buffer remoteUser) throws IOException {
            ((Ajp13Request)Ajp13Connection.this._request).setRemoteUser(remoteUser.toString());
        }
        
        public void parsedServletPath(final Buffer servletPath) throws IOException {
            Ajp13Connection.this._request.setServletPath(servletPath.toString());
        }
        
        public void parsedContextPath(final Buffer context) throws IOException {
            Ajp13Connection.this._request.setContextPath(context.toString());
        }
        
        public void parsedSslCert(final Buffer sslCert) throws IOException {
            try {
                final CertificateFactory cf = CertificateFactory.getInstance("X.509");
                final ByteArrayInputStream bis = new ByteArrayInputStream(sslCert.toString().getBytes());
                final Collection<? extends Certificate> certCollection = cf.generateCertificates(bis);
                final X509Certificate[] certificates = new X509Certificate[certCollection.size()];
                int i = 0;
                for (final Object aCertCollection : certCollection) {
                    certificates[i++] = (X509Certificate)aCertCollection;
                }
                Ajp13Connection.this._request.setAttribute("javax.servlet.request.X509Certificate", certificates);
            }
            catch (Exception e) {
                Ajp13Connection.LOG.warn(e.toString(), new Object[0]);
                Ajp13Connection.LOG.ignore(e);
                if (sslCert != null) {
                    Ajp13Connection.this._request.setAttribute("javax.servlet.request.X509Certificate", sslCert.toString());
                }
            }
        }
        
        public void parsedSslCipher(final Buffer sslCipher) throws IOException {
            Ajp13Connection.this._request.setAttribute("javax.servlet.request.cipher_suite", sslCipher.toString());
        }
        
        public void parsedSslSession(final Buffer sslSession) throws IOException {
            Ajp13Connection.this._request.setAttribute("javax.servlet.request.ssl_session", sslSession.toString());
        }
        
        public void parsedSslKeySize(final int keySize) throws IOException {
            Ajp13Connection.this._request.setAttribute("javax.servlet.request.key_size", new Integer(keySize));
        }
        
        public void parsedMethod(final Buffer method) throws IOException {
            if (method == null) {
                throw new HttpException(400);
            }
            Ajp13Connection.this._request.setMethod(method.toString());
        }
        
        public void parsedUri(final Buffer uri) throws IOException {
            Ajp13Connection.this._uri.parse(uri.toString());
        }
        
        public void parsedProtocol(final Buffer protocol) throws IOException {
            if (protocol != null && protocol.length() > 0) {
                Ajp13Connection.this._request.setProtocol(protocol.toString());
            }
        }
        
        public void parsedRemoteAddr(final Buffer addr) throws IOException {
            if (addr != null && addr.length() > 0) {
                Ajp13Connection.this._request.setRemoteAddr(addr.toString());
            }
        }
        
        public void parsedRemoteHost(final Buffer name) throws IOException {
            if (name != null && name.length() > 0) {
                Ajp13Connection.this._request.setRemoteHost(name.toString());
            }
        }
        
        public void parsedServerName(final Buffer name) throws IOException {
            if (name != null && name.length() > 0) {
                Ajp13Connection.this._request.setServerName(name.toString());
            }
        }
        
        public void parsedServerPort(final int port) throws IOException {
            Ajp13Connection.this._request.setServerPort(port);
        }
        
        public void parsedSslSecure(final boolean secure) throws IOException {
            ((Ajp13Request)Ajp13Connection.this._request).setSslSecure(secure);
        }
        
        public void parsedQueryString(final Buffer value) throws IOException {
            final String u = Ajp13Connection.this._uri + "?" + value;
            Ajp13Connection.this._uri.parse(u);
        }
        
        public void parsedHeader(final Buffer name, final Buffer value) throws IOException {
            Ajp13Connection.this._requestFields.add(name, value);
        }
        
        public void parsedRequestAttribute(final String key, final Buffer value) throws IOException {
            if (value == null) {
                Ajp13Connection.this._request.removeAttribute(key);
            }
            else {
                Ajp13Connection.this._request.setAttribute(key, value.toString());
            }
        }
        
        public void parsedRequestAttribute(final String key, final int value) throws IOException {
            Ajp13Connection.this._request.setAttribute(key, Integer.toString(value));
        }
        
        public void headerComplete() throws IOException {
            BlockingHttpConnection.this.handleRequest();
        }
        
        public void messageComplete(final long contextLength) throws IOException {
        }
        
        public void content(final Buffer ref) throws IOException {
        }
    }
}
