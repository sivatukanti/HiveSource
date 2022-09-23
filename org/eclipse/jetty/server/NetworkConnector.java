// 
// Decompiled by Procyon v0.5.36
// 

package org.eclipse.jetty.server;

import java.io.IOException;
import java.io.Closeable;

public interface NetworkConnector extends Connector, Closeable
{
    void open() throws IOException;
    
    void close();
    
    boolean isOpen();
    
    String getHost();
    
    int getPort();
    
    int getLocalPort();
}
