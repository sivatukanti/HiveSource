// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.tools.ant.util.java15;

import java.net.InetAddress;
import java.net.SocketAddress;
import java.util.Iterator;
import java.util.List;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.ProxySelector;
import java.net.URISyntaxException;
import org.apache.tools.ant.BuildException;
import java.net.URI;

public class ProxyDiagnostics
{
    private String destination;
    private URI destURI;
    public static final String DEFAULT_DESTINATION = "http://ant.apache.org/";
    
    public ProxyDiagnostics(final String destination) {
        this.destination = destination;
        try {
            this.destURI = new URI(destination);
        }
        catch (URISyntaxException e) {
            throw new BuildException(e);
        }
    }
    
    public ProxyDiagnostics() {
        this("http://ant.apache.org/");
    }
    
    @Override
    public String toString() {
        final ProxySelector selector = ProxySelector.getDefault();
        final List list = selector.select(this.destURI);
        final StringBuffer result = new StringBuffer();
        final Iterator proxies = list.listIterator();
        while (proxies.hasNext()) {
            final Proxy proxy = proxies.next();
            final SocketAddress address = proxy.address();
            if (address == null) {
                result.append("Direct connection\n");
            }
            else {
                result.append(proxy.toString());
                if (address instanceof InetSocketAddress) {
                    final InetSocketAddress ina = (InetSocketAddress)address;
                    result.append(' ');
                    result.append(ina.getHostName());
                    result.append(':');
                    result.append(ina.getPort());
                    if (ina.isUnresolved()) {
                        result.append(" [unresolved]");
                    }
                    else {
                        final InetAddress addr = ina.getAddress();
                        result.append(" [");
                        result.append(addr.getHostAddress());
                        result.append(']');
                    }
                }
                result.append('\n');
            }
        }
        return result.toString();
    }
}
