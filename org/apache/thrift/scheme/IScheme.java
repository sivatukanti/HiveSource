// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.thrift.scheme;

import org.apache.thrift.TException;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.TBase;

public interface IScheme<T extends TBase>
{
    void read(final TProtocol p0, final T p1) throws TException;
    
    void write(final TProtocol p0, final T p1) throws TException;
}
