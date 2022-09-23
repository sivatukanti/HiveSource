// 
// Decompiled by Procyon v0.5.36
// 

package parquet.format.event;

import parquet.org.apache.thrift.protocol.TList;
import parquet.org.apache.thrift.protocol.TMap;
import parquet.org.apache.thrift.protocol.TSet;
import parquet.org.apache.thrift.protocol.TField;
import parquet.org.apache.thrift.TException;
import parquet.org.apache.thrift.protocol.TProtocol;

public final class EventBasedThriftReader
{
    private final TProtocol protocol;
    
    public EventBasedThriftReader(final TProtocol protocol) {
        this.protocol = protocol;
    }
    
    public void readStruct(final FieldConsumer c) throws TException {
        this.protocol.readStructBegin();
        this.readStructContent(c);
        this.protocol.readStructEnd();
    }
    
    public void readStructContent(final FieldConsumer c) throws TException {
        while (true) {
            final TField field = this.protocol.readFieldBegin();
            if (field.type == 0) {
                break;
            }
            c.consumeField(this.protocol, this, field.id, field.type);
        }
    }
    
    public void readSetContent(final TypedConsumer.SetConsumer eventConsumer, final TSet tSet) throws TException {
        for (int i = 0; i < tSet.size; ++i) {
            eventConsumer.consumeElement(this.protocol, this, tSet.elemType);
        }
    }
    
    public void readMapContent(final TypedConsumer.MapConsumer eventConsumer, final TMap tMap) throws TException {
        for (int i = 0; i < tMap.size; ++i) {
            eventConsumer.consumeEntry(this.protocol, this, tMap.keyType, tMap.valueType);
        }
    }
    
    public void readMapEntry(final byte keyType, final TypedConsumer keyConsumer, final byte valueType, final TypedConsumer valueConsumer) throws TException {
        keyConsumer.read(this.protocol, this, keyType);
        valueConsumer.read(this.protocol, this, valueType);
    }
    
    public void readListContent(final TypedConsumer.ListConsumer eventConsumer, final TList tList) throws TException {
        for (int i = 0; i < tList.size; ++i) {
            eventConsumer.consumeElement(this.protocol, this, tList.elemType);
        }
    }
}
