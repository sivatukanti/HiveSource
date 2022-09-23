// 
// Decompiled by Procyon v0.5.36
// 

package parquet.org.apache.thrift;

import parquet.org.apache.thrift.protocol.TProtocol;

public interface TProcessor
{
    boolean process(final TProtocol p0, final TProtocol p1) throws TException;
}
