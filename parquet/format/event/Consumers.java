// 
// Decompiled by Procyon v0.5.36
// 

package parquet.format.event;

import java.util.HashMap;
import java.util.Collections;
import java.util.Map;
import parquet.org.apache.thrift.TException;
import java.util.ArrayList;
import parquet.org.apache.thrift.protocol.TList;
import parquet.org.apache.thrift.protocol.TProtocol;
import parquet.org.apache.thrift.TFieldIdEnum;
import parquet.org.apache.thrift.TBase;
import java.util.List;

public class Consumers
{
    public static DelegatingFieldConsumer fieldConsumer() {
        return new DelegatingFieldConsumer();
    }
    
    public static <T extends TBase<T, ? extends TFieldIdEnum>> TypedConsumer.ListConsumer listOf(final Class<T> c, final Consumer<List<T>> consumer) {
        class ListConsumer implements Consumer<T>
        {
            List<T> list;
            
            @Override
            public void consume(final T t) {
                this.list.add(t);
            }
        }
        final ListConsumer co = new ListConsumer();
        return new DelegatingListElementsConsumer(struct(c, co)) {
            @Override
            public void consumeList(final TProtocol protocol, final EventBasedThriftReader reader, final TList tList) throws TException {
                co.list = new ArrayList<T>();
                super.consumeList(protocol, reader, tList);
                consumer.consume(co.list);
            }
        };
    }
    
    public static TypedConsumer.ListConsumer listElementsOf(final TypedConsumer consumer) {
        return new DelegatingListElementsConsumer(consumer);
    }
    
    public static <T extends TBase<T, ? extends TFieldIdEnum>> TypedConsumer.StructConsumer struct(final Class<T> c, final Consumer<T> consumer) {
        return new TBaseStructConsumer<Object>(c, consumer);
    }
    
    public static class DelegatingFieldConsumer implements FieldConsumer
    {
        private final Map<Short, TypedConsumer> contexts;
        private final FieldConsumer defaultFieldEventConsumer;
        
        private DelegatingFieldConsumer(final FieldConsumer defaultFieldEventConsumer, final Map<Short, TypedConsumer> contexts) {
            this.defaultFieldEventConsumer = defaultFieldEventConsumer;
            this.contexts = Collections.unmodifiableMap((Map<? extends Short, ? extends TypedConsumer>)contexts);
        }
        
        private DelegatingFieldConsumer() {
            this(new SkippingFieldConsumer());
        }
        
        private DelegatingFieldConsumer(final FieldConsumer defaultFieldEventConsumer) {
            this(defaultFieldEventConsumer, Collections.emptyMap());
        }
        
        public DelegatingFieldConsumer onField(final TFieldIdEnum e, final TypedConsumer typedConsumer) {
            final Map<Short, TypedConsumer> newContexts = new HashMap<Short, TypedConsumer>(this.contexts);
            newContexts.put(e.getThriftFieldId(), typedConsumer);
            return new DelegatingFieldConsumer(this.defaultFieldEventConsumer, newContexts);
        }
        
        @Override
        public void consumeField(final TProtocol protocol, final EventBasedThriftReader reader, final short id, final byte type) throws TException {
            final TypedConsumer delegate = this.contexts.get(id);
            if (delegate != null) {
                delegate.read(protocol, reader, type);
            }
            else {
                this.defaultFieldEventConsumer.consumeField(protocol, reader, id, type);
            }
        }
    }
    
    public interface Consumer<T>
    {
        void consume(final T p0);
    }
}
