// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.thrift;

import org.apache.thrift.protocol.TProtocol;

public interface TServiceClientFactory<T extends TServiceClient>
{
    T getClient(final TProtocol p0);
    
    T getClient(final TProtocol p0, final TProtocol p1);
}
