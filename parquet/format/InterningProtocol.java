// 
// Decompiled by Procyon v0.5.36
// 

package parquet.format;

import java.nio.ByteBuffer;
import parquet.org.apache.thrift.protocol.TSet;
import parquet.org.apache.thrift.protocol.TList;
import parquet.org.apache.thrift.protocol.TMap;
import parquet.org.apache.thrift.protocol.TField;
import parquet.org.apache.thrift.protocol.TStruct;
import parquet.org.apache.thrift.TException;
import parquet.org.apache.thrift.protocol.TMessage;
import parquet.org.apache.thrift.transport.TTransport;
import parquet.org.apache.thrift.protocol.TProtocol;

public class InterningProtocol extends TProtocol
{
    private final TProtocol delegate;
    
    public InterningProtocol(final TProtocol delegate) {
        super(delegate.getTransport());
        this.delegate = delegate;
    }
    
    @Override
    public TTransport getTransport() {
        return this.delegate.getTransport();
    }
    
    @Override
    public void writeMessageBegin(final TMessage message) throws TException {
        this.delegate.writeMessageBegin(message);
    }
    
    @Override
    public void writeMessageEnd() throws TException {
        this.delegate.writeMessageEnd();
    }
    
    @Override
    public int hashCode() {
        return this.delegate.hashCode();
    }
    
    @Override
    public void writeStructBegin(final TStruct struct) throws TException {
        this.delegate.writeStructBegin(struct);
    }
    
    @Override
    public void writeStructEnd() throws TException {
        this.delegate.writeStructEnd();
    }
    
    @Override
    public void writeFieldBegin(final TField field) throws TException {
        this.delegate.writeFieldBegin(field);
    }
    
    @Override
    public void writeFieldEnd() throws TException {
        this.delegate.writeFieldEnd();
    }
    
    @Override
    public void writeFieldStop() throws TException {
        this.delegate.writeFieldStop();
    }
    
    @Override
    public void writeMapBegin(final TMap map) throws TException {
        this.delegate.writeMapBegin(map);
    }
    
    @Override
    public void writeMapEnd() throws TException {
        this.delegate.writeMapEnd();
    }
    
    @Override
    public void writeListBegin(final TList list) throws TException {
        this.delegate.writeListBegin(list);
    }
    
    @Override
    public void writeListEnd() throws TException {
        this.delegate.writeListEnd();
    }
    
    @Override
    public void writeSetBegin(final TSet set) throws TException {
        this.delegate.writeSetBegin(set);
    }
    
    @Override
    public void writeSetEnd() throws TException {
        this.delegate.writeSetEnd();
    }
    
    @Override
    public void writeBool(final boolean b) throws TException {
        this.delegate.writeBool(b);
    }
    
    @Override
    public void writeByte(final byte b) throws TException {
        this.delegate.writeByte(b);
    }
    
    @Override
    public void writeI16(final short i16) throws TException {
        this.delegate.writeI16(i16);
    }
    
    @Override
    public void writeI32(final int i32) throws TException {
        this.delegate.writeI32(i32);
    }
    
    @Override
    public void writeI64(final long i64) throws TException {
        this.delegate.writeI64(i64);
    }
    
    @Override
    public void writeDouble(final double dub) throws TException {
        this.delegate.writeDouble(dub);
    }
    
    @Override
    public void writeString(final String str) throws TException {
        this.delegate.writeString(str);
    }
    
    @Override
    public void writeBinary(final ByteBuffer buf) throws TException {
        this.delegate.writeBinary(buf);
    }
    
    @Override
    public TMessage readMessageBegin() throws TException {
        return this.delegate.readMessageBegin();
    }
    
    @Override
    public void readMessageEnd() throws TException {
        this.delegate.readMessageEnd();
    }
    
    @Override
    public TStruct readStructBegin() throws TException {
        return this.delegate.readStructBegin();
    }
    
    @Override
    public void readStructEnd() throws TException {
        this.delegate.readStructEnd();
    }
    
    @Override
    public TField readFieldBegin() throws TException {
        return this.delegate.readFieldBegin();
    }
    
    @Override
    public void readFieldEnd() throws TException {
        this.delegate.readFieldEnd();
    }
    
    @Override
    public TMap readMapBegin() throws TException {
        return this.delegate.readMapBegin();
    }
    
    @Override
    public void readMapEnd() throws TException {
        this.delegate.readMapEnd();
    }
    
    @Override
    public TList readListBegin() throws TException {
        return this.delegate.readListBegin();
    }
    
    @Override
    public void readListEnd() throws TException {
        this.delegate.readListEnd();
    }
    
    @Override
    public TSet readSetBegin() throws TException {
        return this.delegate.readSetBegin();
    }
    
    @Override
    public void readSetEnd() throws TException {
        this.delegate.readSetEnd();
    }
    
    @Override
    public boolean equals(final Object obj) {
        return this.delegate.equals(obj);
    }
    
    @Override
    public boolean readBool() throws TException {
        return this.delegate.readBool();
    }
    
    @Override
    public byte readByte() throws TException {
        return this.delegate.readByte();
    }
    
    @Override
    public short readI16() throws TException {
        return this.delegate.readI16();
    }
    
    @Override
    public int readI32() throws TException {
        return this.delegate.readI32();
    }
    
    @Override
    public long readI64() throws TException {
        return this.delegate.readI64();
    }
    
    @Override
    public double readDouble() throws TException {
        return this.delegate.readDouble();
    }
    
    @Override
    public String readString() throws TException {
        return this.delegate.readString().intern();
    }
    
    @Override
    public ByteBuffer readBinary() throws TException {
        return this.delegate.readBinary();
    }
    
    @Override
    public void reset() {
        this.delegate.reset();
    }
    
    @Override
    public String toString() {
        return this.delegate.toString();
    }
}
