// 
// Decompiled by Procyon v0.5.36
// 

package org.eclipse.jetty.server;

import org.eclipse.jetty.http.BadMessageException;
import org.eclipse.jetty.http.HttpFields;
import org.eclipse.jetty.http.MetaData;
import org.eclipse.jetty.io.Connection;
import org.eclipse.jetty.io.EndPoint;
import java.util.List;

public interface ConnectionFactory
{
    String getProtocol();
    
    List<String> getProtocols();
    
    Connection newConnection(final Connector p0, final EndPoint p1);
    
    public interface Upgrading extends ConnectionFactory
    {
        Connection upgradeConnection(final Connector p0, final EndPoint p1, final MetaData.Request p2, final HttpFields p3) throws BadMessageException;
    }
}
