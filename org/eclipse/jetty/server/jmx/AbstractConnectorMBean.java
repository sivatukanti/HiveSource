// 
// Decompiled by Procyon v0.5.36
// 

package org.eclipse.jetty.server.jmx;

import java.util.Iterator;
import org.eclipse.jetty.server.ConnectionFactory;
import org.eclipse.jetty.server.AbstractConnector;
import org.eclipse.jetty.util.annotation.ManagedObject;
import org.eclipse.jetty.jmx.ObjectMBean;

@ManagedObject("MBean Wrapper for Connectors")
public class AbstractConnectorMBean extends ObjectMBean
{
    final AbstractConnector _connector;
    
    public AbstractConnectorMBean(final Object managedObject) {
        super(managedObject);
        this._connector = (AbstractConnector)managedObject;
    }
    
    @Override
    public String getObjectContextBasis() {
        final StringBuilder buffer = new StringBuilder();
        for (final ConnectionFactory f : this._connector.getConnectionFactories()) {
            final String protocol = f.getProtocol();
            if (protocol != null) {
                if (buffer.length() > 0) {
                    buffer.append("|");
                }
                buffer.append(protocol);
            }
        }
        return String.format("%s@%x", buffer.toString(), this._connector.hashCode());
    }
}
