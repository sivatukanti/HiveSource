// 
// Decompiled by Procyon v0.5.36
// 

package parquet.format.event;

import parquet.org.apache.thrift.protocol.TMap;
import parquet.org.apache.thrift.protocol.TSet;
import parquet.org.apache.thrift.protocol.TList;
import parquet.org.apache.thrift.TException;
import parquet.org.apache.thrift.protocol.TProtocol;

public abstract class TypedConsumer
{
    public final byte type;
    
    private TypedConsumer(final byte type) {
        this.type = type;
    }
    
    public final void read(final TProtocol protocol, final EventBasedThriftReader reader, final byte type) throws TException {
        if (this.type != type) {
            throw new TException("Incorrect type in stream. Expected " + this.type + " but got " + type);
        }
        this.read(protocol, reader);
    }
    
    abstract void read(final TProtocol p0, final EventBasedThriftReader p1) throws TException;
    
    public abstract static class DoubleConsumer extends TypedConsumer
    {
        protected DoubleConsumer() {
            super((byte)4, null);
        }
        
        @Override
        final void read(final TProtocol protocol, final EventBasedThriftReader reader) throws TException {
            this.consume(protocol.readDouble());
        }
        
        public abstract void consume(final double p0);
    }
    
    public abstract static class ByteConsumer extends TypedConsumer
    {
        protected ByteConsumer() {
            super((byte)3, null);
        }
        
        @Override
        final void read(final TProtocol protocol, final EventBasedThriftReader reader) throws TException {
            this.consume(protocol.readByte());
        }
        
        public abstract void consume(final byte p0);
    }
    
    public abstract static class BoolConsumer extends TypedConsumer
    {
        protected BoolConsumer() {
            super((byte)2, null);
        }
        
        @Override
        final void read(final TProtocol protocol, final EventBasedThriftReader reader) throws TException {
            this.consume(protocol.readBool());
        }
        
        public abstract void consume(final boolean p0);
    }
    
    public abstract static class I32Consumer extends TypedConsumer
    {
        protected I32Consumer() {
            super((byte)8, null);
        }
        
        @Override
        final void read(final TProtocol protocol, final EventBasedThriftReader reader) throws TException {
            this.consume(protocol.readI32());
        }
        
        public abstract void consume(final int p0);
    }
    
    public abstract static class I64Consumer extends TypedConsumer
    {
        protected I64Consumer() {
            super((byte)10, null);
        }
        
        @Override
        final void read(final TProtocol protocol, final EventBasedThriftReader reader) throws TException {
            this.consume(protocol.readI64());
        }
        
        public abstract void consume(final long p0);
    }
    
    public abstract static class I16Consumer extends TypedConsumer
    {
        protected I16Consumer() {
            super((byte)6, null);
        }
        
        @Override
        final void read(final TProtocol protocol, final EventBasedThriftReader reader) throws TException {
            this.consume(protocol.readI16());
        }
        
        public abstract void consume(final short p0);
    }
    
    public abstract static class StringConsumer extends TypedConsumer
    {
        protected StringConsumer() {
            super((byte)11, null);
        }
        
        @Override
        final void read(final TProtocol protocol, final EventBasedThriftReader reader) throws TException {
            this.consume(protocol.readString());
        }
        
        public abstract void consume(final String p0);
    }
    
    public abstract static class StructConsumer extends TypedConsumer
    {
        protected StructConsumer() {
            super((byte)12, null);
        }
        
        @Override
        final void read(final TProtocol protocol, final EventBasedThriftReader reader) throws TException {
            this.consumeStruct(protocol, reader);
        }
        
        public abstract void consumeStruct(final TProtocol p0, final EventBasedThriftReader p1) throws TException;
    }
    
    public abstract static class ListConsumer extends TypedConsumer
    {
        protected ListConsumer() {
            super((byte)15, null);
        }
        
        @Override
        final void read(final TProtocol protocol, final EventBasedThriftReader reader) throws TException {
            this.consumeList(protocol, reader, protocol.readListBegin());
            protocol.readListEnd();
        }
        
        public void consumeList(final TProtocol protocol, final EventBasedThriftReader reader, final TList tList) throws TException {
            reader.readListContent(this, tList);
        }
        
        public abstract void consumeElement(final TProtocol p0, final EventBasedThriftReader p1, final byte p2) throws TException;
    }
    
    public abstract static class SetConsumer extends TypedConsumer
    {
        protected SetConsumer() {
            super((byte)14, null);
        }
        
        @Override
        final void read(final TProtocol protocol, final EventBasedThriftReader reader) throws TException {
            this.consumeSet(protocol, reader, protocol.readSetBegin());
            protocol.readSetEnd();
        }
        
        public void consumeSet(final TProtocol protocol, final EventBasedThriftReader reader, final TSet tSet) throws TException {
            reader.readSetContent(this, tSet);
        }
        
        public abstract void consumeElement(final TProtocol p0, final EventBasedThriftReader p1, final byte p2) throws TException;
    }
    
    public abstract static class MapConsumer extends TypedConsumer
    {
        protected MapConsumer() {
            super((byte)13, null);
        }
        
        @Override
        final void read(final TProtocol protocol, final EventBasedThriftReader reader) throws TException {
            this.consumeMap(protocol, reader, protocol.readMapBegin());
            protocol.readMapEnd();
        }
        
        public void consumeMap(final TProtocol protocol, final EventBasedThriftReader reader, final TMap tMap) throws TException {
            reader.readMapContent(this, tMap);
        }
        
        public abstract void consumeEntry(final TProtocol p0, final EventBasedThriftReader p1, final byte p2, final byte p3) throws TException;
    }
}
