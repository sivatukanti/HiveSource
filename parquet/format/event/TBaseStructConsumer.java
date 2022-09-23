// 
// Decompiled by Procyon v0.5.36
// 

package parquet.format.event;

import parquet.org.apache.thrift.TException;
import parquet.org.apache.thrift.protocol.TProtocol;
import parquet.org.apache.thrift.TFieldIdEnum;
import parquet.org.apache.thrift.TBase;

class TBaseStructConsumer<T extends TBase<T, ? extends TFieldIdEnum>> extends StructConsumer
{
    private final Class<T> c;
    private Consumers.Consumer<T> consumer;
    
    public TBaseStructConsumer(final Class<T> c, final Consumers.Consumer<T> consumer) {
        this.c = c;
        this.consumer = consumer;
    }
    
    @Override
    public void consumeStruct(final TProtocol protocol, final EventBasedThriftReader reader) throws TException {
        final T o = this.newObject();
        o.read(protocol);
        this.consumer.consume(o);
    }
    
    protected T newObject() {
        try {
            return this.c.newInstance();
        }
        catch (InstantiationException e) {
            throw new RuntimeException(this.c.getName(), e);
        }
        catch (IllegalAccessException e2) {
            throw new RuntimeException(this.c.getName(), e2);
        }
    }
}
