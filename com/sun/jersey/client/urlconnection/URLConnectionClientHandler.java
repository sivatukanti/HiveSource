// 
// Decompiled by Procyon v0.5.36
// 

package com.sun.jersey.client.urlconnection;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import com.sun.jersey.core.header.InBoundHeaders;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.lang.reflect.Field;
import java.net.ProtocolException;
import javax.ws.rs.core.MultivaluedMap;
import java.io.IOException;
import com.sun.jersey.api.client.CommittingOutputStream;
import java.io.OutputStream;
import com.sun.jersey.api.client.RequestWriter;
import javax.net.ssl.HttpsURLConnection;
import java.net.HttpURLConnection;
import com.sun.jersey.api.client.ClientHandlerException;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.ClientRequest;
import com.sun.jersey.api.client.TerminatingClientHandler;

public final class URLConnectionClientHandler extends TerminatingClientHandler
{
    public static final String PROPERTY_HTTP_URL_CONNECTION_SET_METHOD_WORKAROUND = "com.sun.jersey.client.property.httpUrlConnectionSetMethodWorkaround";
    private HttpURLConnectionFactory httpURLConnectionFactory;
    
    public URLConnectionClientHandler(final HttpURLConnectionFactory httpURLConnectionFactory) {
        this.httpURLConnectionFactory = null;
        this.httpURLConnectionFactory = httpURLConnectionFactory;
    }
    
    public URLConnectionClientHandler() {
        this((HttpURLConnectionFactory)null);
    }
    
    @Override
    public ClientResponse handle(final ClientRequest ro) {
        try {
            return this._invoke(ro);
        }
        catch (Exception ex) {
            throw new ClientHandlerException(ex);
        }
    }
    
    private ClientResponse _invoke(final ClientRequest ro) throws IOException {
        HttpURLConnection uc;
        if (this.httpURLConnectionFactory == null) {
            uc = (HttpURLConnection)ro.getURI().toURL().openConnection();
        }
        else {
            uc = this.httpURLConnectionFactory.getHttpURLConnection(ro.getURI().toURL());
        }
        final Integer readTimeout = ro.getProperties().get("com.sun.jersey.client.property.readTimeout");
        if (readTimeout != null) {
            uc.setReadTimeout(readTimeout);
        }
        final Integer connectTimeout = ro.getProperties().get("com.sun.jersey.client.property.connectTimeout");
        if (connectTimeout != null) {
            uc.setConnectTimeout(connectTimeout);
        }
        final Boolean followRedirects = ro.getProperties().get("com.sun.jersey.client.property.followRedirects");
        if (followRedirects != null) {
            uc.setInstanceFollowRedirects(followRedirects);
        }
        if (uc instanceof HttpsURLConnection) {
            final HTTPSProperties httpsProperties = ro.getProperties().get("com.sun.jersey.client.impl.urlconnection.httpsProperties");
            if (httpsProperties != null) {
                httpsProperties.setConnection((HttpsURLConnection)uc);
            }
        }
        final Boolean httpUrlConnectionSetMethodWorkaround = ro.getProperties().get("com.sun.jersey.client.property.httpUrlConnectionSetMethodWorkaround");
        if (httpUrlConnectionSetMethodWorkaround != null && httpUrlConnectionSetMethodWorkaround) {
            setRequestMethodUsingWorkaroundForJREBug(uc, ro.getMethod());
        }
        else {
            uc.setRequestMethod(ro.getMethod());
        }
        this.writeOutBoundHeaders(ro.getHeaders(), uc);
        final Object entity = ro.getEntity();
        if (entity != null) {
            uc.setDoOutput(true);
            this.writeRequestEntity(ro, new RequestEntityWriterListener() {
                @Override
                public void onRequestEntitySize(final long size) {
                    if (size != -1L && size < 2147483647L) {
                        uc.setFixedLengthStreamingMode((int)size);
                    }
                    else {
                        final Integer chunkedEncodingSize = ro.getProperties().get("com.sun.jersey.client.property.chunkedEncodingSize");
                        if (chunkedEncodingSize != null) {
                            uc.setChunkedStreamingMode(chunkedEncodingSize);
                        }
                    }
                }
                
                @Override
                public OutputStream onGetOutputStream() throws IOException {
                    return new CommittingOutputStream() {
                        @Override
                        protected OutputStream getOutputStream() throws IOException {
                            return uc.getOutputStream();
                        }
                        
                        public void commit() throws IOException {
                            URLConnectionClientHandler.this.writeOutBoundHeaders(ro.getHeaders(), uc);
                        }
                    };
                }
            });
        }
        else {
            this.writeOutBoundHeaders(ro.getHeaders(), uc);
        }
        return new URLConnectionResponse(uc.getResponseCode(), this.getInBoundHeaders(uc), this.getInputStream(uc), ro.getMethod(), uc);
    }
    
    private static final void setRequestMethodUsingWorkaroundForJREBug(final HttpURLConnection httpURLConnection, final String method) {
        try {
            httpURLConnection.setRequestMethod(method);
        }
        catch (ProtocolException pe) {
            try {
                final Class<?> httpURLConnectionClass = httpURLConnection.getClass();
                final Field methodField = httpURLConnectionClass.getSuperclass().getDeclaredField("method");
                methodField.setAccessible(true);
                methodField.set(httpURLConnection, method);
            }
            catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }
    
    private void writeOutBoundHeaders(final MultivaluedMap<String, Object> metadata, final HttpURLConnection uc) {
        for (final Map.Entry<String, List<Object>> e : metadata.entrySet()) {
            final List<Object> vs = e.getValue();
            if (vs.size() == 1) {
                uc.setRequestProperty(e.getKey(), ClientRequest.getHeaderValue(vs.get(0)));
            }
            else {
                final StringBuilder b = new StringBuilder();
                boolean add = false;
                for (final Object v : e.getValue()) {
                    if (add) {
                        b.append(',');
                    }
                    add = true;
                    b.append(ClientRequest.getHeaderValue(v));
                }
                uc.setRequestProperty(e.getKey(), b.toString());
            }
        }
    }
    
    private InBoundHeaders getInBoundHeaders(final HttpURLConnection uc) {
        final InBoundHeaders headers = new InBoundHeaders();
        for (final Map.Entry<String, List<String>> e : uc.getHeaderFields().entrySet()) {
            if (e.getKey() != null) {
                headers.put(e.getKey(), (List<V>)e.getValue());
            }
        }
        return headers;
    }
    
    private InputStream getInputStream(final HttpURLConnection uc) throws IOException {
        if (uc.getResponseCode() < 300) {
            return uc.getInputStream();
        }
        final InputStream ein = uc.getErrorStream();
        return (ein != null) ? ein : new ByteArrayInputStream(new byte[0]);
    }
    
    private final class URLConnectionResponse extends ClientResponse
    {
        private final String method;
        private final HttpURLConnection uc;
        
        URLConnectionResponse(final int status, final InBoundHeaders headers, final InputStream entity, final String method, final HttpURLConnection uc) {
            super(status, headers, entity, URLConnectionClientHandler.this.getMessageBodyWorkers());
            this.method = method;
            this.uc = uc;
        }
        
        @Override
        public boolean hasEntity() {
            if (this.method.equals("HEAD") || this.getEntityInputStream() == null) {
                return false;
            }
            final int l = this.uc.getContentLength();
            return l > 0 || l == -1;
        }
        
        @Override
        public String toString() {
            return this.uc.getRequestMethod() + " " + this.uc.getURL() + " returned a response status of " + this.getStatus() + " " + this.getClientResponseStatus();
        }
    }
}
