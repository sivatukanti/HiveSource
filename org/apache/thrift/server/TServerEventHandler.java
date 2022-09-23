// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.thrift.server;

import org.apache.thrift.transport.TTransport;
import org.apache.thrift.protocol.TProtocol;

public interface TServerEventHandler
{
    void preServe();
    
    ServerContext createContext(final TProtocol p0, final TProtocol p1);
    
    void deleteContext(final ServerContext p0, final TProtocol p1, final TProtocol p2);
    
    void processContext(final ServerContext p0, final TTransport p1, final TTransport p2);
}
