// 
// Decompiled by Procyon v0.5.36
// 

package org.eclipse.jetty.io;

import java.util.Collection;
import java.util.function.Consumer;
import java.util.Objects;
import org.eclipse.jetty.util.component.ContainerLifeCycle;
import java.io.IOException;
import java.util.Map;

public interface ClientConnectionFactory
{
    public static final String CONNECTOR_CONTEXT_KEY = "client.connector";
    
    Connection newConnection(final EndPoint p0, final Map<String, Object> p1) throws IOException;
    
    default Connection customize(final Connection connection, final Map<String, Object> context) {
        final ContainerLifeCycle connector = context.get("client.connector");
        final Collection<Connection.Listener> beans = connector.getBeans(Connection.Listener.class);
        Objects.requireNonNull(connection);
        beans.forEach(connection::addListener);
        return connection;
    }
}
