// 
// Decompiled by Procyon v0.5.36
// 

package parquet.format.event;

import parquet.org.apache.thrift.TException;
import parquet.org.apache.thrift.protocol.TProtocol;

public interface FieldConsumer
{
    void consumeField(final TProtocol p0, final EventBasedThriftReader p1, final short p2, final byte p3) throws TException;
}
