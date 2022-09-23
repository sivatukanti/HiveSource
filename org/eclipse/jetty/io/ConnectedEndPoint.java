// 
// Decompiled by Procyon v0.5.36
// 

package org.eclipse.jetty.io;

public interface ConnectedEndPoint extends EndPoint
{
    Connection getConnection();
    
    void setConnection(final Connection p0);
}
