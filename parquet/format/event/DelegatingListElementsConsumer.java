// 
// Decompiled by Procyon v0.5.36
// 

package parquet.format.event;

import parquet.org.apache.thrift.TException;
import parquet.org.apache.thrift.protocol.TProtocol;

class DelegatingListElementsConsumer extends ListConsumer
{
    private TypedConsumer elementConsumer;
    
    protected DelegatingListElementsConsumer(final TypedConsumer consumer) {
        this.elementConsumer = consumer;
    }
    
    @Override
    public void consumeElement(final TProtocol protocol, final EventBasedThriftReader reader, final byte elemType) throws TException {
        this.elementConsumer.read(protocol, reader, elemType);
    }
}
