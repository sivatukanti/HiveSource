// 
// Decompiled by Procyon v0.5.36
// 

package org.eclipse.jetty.server;

import org.eclipse.jetty.util.component.Container;
import org.eclipse.jetty.io.ConnectionStatistics;

public class ServerConnectionStatistics extends ConnectionStatistics
{
    public static void addToAllConnectors(final Server server) {
        for (final Connector connector : server.getConnectors()) {
            if (connector instanceof Container) {
                ((Container)connector).addBean(new ConnectionStatistics());
            }
        }
    }
}
