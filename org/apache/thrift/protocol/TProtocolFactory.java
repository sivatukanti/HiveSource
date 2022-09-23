// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.thrift.protocol;

import org.apache.thrift.transport.TTransport;
import java.io.Serializable;

public interface TProtocolFactory extends Serializable
{
    TProtocol getProtocol(final TTransport p0);
}
