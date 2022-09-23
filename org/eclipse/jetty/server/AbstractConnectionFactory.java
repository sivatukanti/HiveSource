// 
// Decompiled by Procyon v0.5.36
// 

package org.eclipse.jetty.server;

import org.eclipse.jetty.util.ArrayUtil;
import org.eclipse.jetty.util.ssl.SslContextFactory;
import java.util.Iterator;
import org.eclipse.jetty.io.Connection;
import org.eclipse.jetty.io.EndPoint;
import org.eclipse.jetty.io.AbstractConnection;
import org.eclipse.jetty.util.annotation.ManagedAttribute;
import java.util.Collections;
import java.util.Arrays;
import java.util.List;
import org.eclipse.jetty.util.annotation.ManagedObject;
import org.eclipse.jetty.util.component.ContainerLifeCycle;

@ManagedObject
public abstract class AbstractConnectionFactory extends ContainerLifeCycle implements ConnectionFactory
{
    private final String _protocol;
    private final List<String> _protocols;
    private int _inputbufferSize;
    
    protected AbstractConnectionFactory(final String protocol) {
        this._inputbufferSize = 8192;
        this._protocol = protocol;
        this._protocols = Collections.unmodifiableList((List<? extends String>)Arrays.asList(protocol));
    }
    
    protected AbstractConnectionFactory(final String... protocols) {
        this._inputbufferSize = 8192;
        this._protocol = protocols[0];
        this._protocols = Collections.unmodifiableList((List<? extends String>)Arrays.asList((T[])protocols));
    }
    
    @ManagedAttribute(value = "The protocol name", readonly = true)
    @Override
    public String getProtocol() {
        return this._protocol;
    }
    
    @Override
    public List<String> getProtocols() {
        return this._protocols;
    }
    
    @ManagedAttribute("The buffer size used to read from the network")
    public int getInputBufferSize() {
        return this._inputbufferSize;
    }
    
    public void setInputBufferSize(final int size) {
        this._inputbufferSize = size;
    }
    
    protected AbstractConnection configure(final AbstractConnection connection, final Connector connector, final EndPoint endPoint) {
        connection.setInputBufferSize(this.getInputBufferSize());
        if (connector instanceof ContainerLifeCycle) {
            final ContainerLifeCycle aggregate = (ContainerLifeCycle)connector;
            for (final Connection.Listener listener : aggregate.getBeans(Connection.Listener.class)) {
                connection.addListener(listener);
            }
        }
        for (final Connection.Listener listener2 : this.getBeans(Connection.Listener.class)) {
            connection.addListener(listener2);
        }
        return connection;
    }
    
    @Override
    public String toString() {
        return String.format("%s@%x%s", this.getClass().getSimpleName(), this.hashCode(), this.getProtocols());
    }
    
    public static ConnectionFactory[] getFactories(final SslContextFactory sslContextFactory, ConnectionFactory... factories) {
        factories = ArrayUtil.removeNulls(factories);
        if (sslContextFactory == null) {
            return factories;
        }
        for (final ConnectionFactory factory : factories) {
            if (factory instanceof HttpConfiguration.ConnectionFactory) {
                final HttpConfiguration config = ((HttpConfiguration.ConnectionFactory)factory).getHttpConfiguration();
                if (config.getCustomizer(SecureRequestCustomizer.class) == null) {
                    config.addCustomizer(new SecureRequestCustomizer());
                }
            }
        }
        return ArrayUtil.prependToArray(new SslConnectionFactory(sslContextFactory, factories[0].getProtocol()), factories, ConnectionFactory.class);
    }
}
