// 
// Decompiled by Procyon v0.5.36
// 

package parquet.format.event;

import parquet.org.apache.thrift.TException;
import parquet.org.apache.thrift.protocol.TProtocolUtil;
import parquet.org.apache.thrift.protocol.TProtocol;

class SkippingFieldConsumer implements FieldConsumer
{
    @Override
    public void consumeField(final TProtocol protocol, final EventBasedThriftReader reader, final short id, final byte type) throws TException {
        TProtocolUtil.skip(protocol, type);
    }
}
