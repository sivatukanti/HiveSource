// 
// Decompiled by Procyon v0.5.36
// 

package org.eclipse.jetty.server;

import org.eclipse.jetty.io.AbstractConnection;
import javax.net.ssl.SSLEngine;
import org.eclipse.jetty.io.ssl.SslConnection;
import org.eclipse.jetty.http.HttpVersion;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import org.eclipse.jetty.io.Connection;
import org.eclipse.jetty.io.EndPoint;
import java.util.ArrayList;
import java.util.List;

public abstract class NegotiatingServerConnectionFactory extends AbstractConnectionFactory
{
    private final List<String> negotiatedProtocols;
    private String defaultProtocol;
    
    public static void checkProtocolNegotiationAvailable() {
        try {
            final String javaVersion = System.getProperty("java.version");
            final String alpnClassName = "org.eclipse.jetty.alpn.ALPN";
            if (javaVersion.startsWith("1.")) {
                final Class<?> klass = ClassLoader.getSystemClassLoader().loadClass(alpnClassName);
                if (klass.getClassLoader() != null) {
                    throw new IllegalStateException(alpnClassName + " must be on JVM boot classpath");
                }
            }
            else {
                NegotiatingServerConnectionFactory.class.getClassLoader().loadClass(alpnClassName);
            }
        }
        catch (ClassNotFoundException x) {
            throw new IllegalStateException("No ALPN classes available");
        }
    }
    
    public NegotiatingServerConnectionFactory(final String protocol, final String... negotiatedProtocols) {
        super(protocol);
        this.negotiatedProtocols = new ArrayList<String>();
        if (negotiatedProtocols != null) {
            for (String p : negotiatedProtocols) {
                p = p.trim();
                if (!p.isEmpty()) {
                    this.negotiatedProtocols.add(p.trim());
                }
            }
        }
    }
    
    public String getDefaultProtocol() {
        return this.defaultProtocol;
    }
    
    public void setDefaultProtocol(final String defaultProtocol) {
        final String dft = (defaultProtocol == null) ? "" : defaultProtocol.trim();
        this.defaultProtocol = (dft.isEmpty() ? null : dft);
    }
    
    public List<String> getNegotiatedProtocols() {
        return this.negotiatedProtocols;
    }
    
    @Override
    public Connection newConnection(final Connector connector, final EndPoint endPoint) {
        List<String> negotiated = this.negotiatedProtocols;
        if (negotiated.isEmpty()) {
            final ConnectionFactory f;
            negotiated = connector.getProtocols().stream().filter(p -> {
                f = connector.getConnectionFactory(p);
                return !(f instanceof SslConnectionFactory) && !(f instanceof NegotiatingServerConnectionFactory);
            }).collect((Collector<? super Object, ?, List<String>>)Collectors.toList());
        }
        String dft = this.defaultProtocol;
        if (dft == null && !negotiated.isEmpty()) {
            if (negotiated.contains(HttpVersion.HTTP_1_1.asString())) {
                dft = HttpVersion.HTTP_1_1.asString();
            }
            else {
                dft = negotiated.get(0);
            }
        }
        SSLEngine engine = null;
        EndPoint ep = endPoint;
        while (engine == null && ep != null) {
            if (ep instanceof SslConnection.DecryptedEndPoint) {
                engine = ((SslConnection.DecryptedEndPoint)ep).getSslConnection().getSSLEngine();
            }
            else {
                ep = null;
            }
        }
        return this.configure(this.newServerConnection(connector, endPoint, engine, negotiated, dft), connector, endPoint);
    }
    
    protected abstract AbstractConnection newServerConnection(final Connector p0, final EndPoint p1, final SSLEngine p2, final List<String> p3, final String p4);
    
    @Override
    public String toString() {
        return String.format("%s@%x{%s,%s,%s}", this.getClass().getSimpleName(), this.hashCode(), this.getProtocols(), this.getDefaultProtocol(), this.getNegotiatedProtocols());
    }
}
