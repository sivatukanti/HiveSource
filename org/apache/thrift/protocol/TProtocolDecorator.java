// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.thrift.protocol;

import java.nio.ByteBuffer;
import org.apache.thrift.TException;

public abstract class TProtocolDecorator extends TProtocol
{
    private final TProtocol concreteProtocol;
    
    public TProtocolDecorator(final TProtocol protocol) {
        super(protocol.getTransport());
        this.concreteProtocol = protocol;
    }
    
    @Override
    public void writeMessageBegin(final TMessage tMessage) throws TException {
        this.concreteProtocol.writeMessageBegin(tMessage);
    }
    
    @Override
    public void writeMessageEnd() throws TException {
        this.concreteProtocol.writeMessageEnd();
    }
    
    @Override
    public void writeStructBegin(final TStruct tStruct) throws TException {
        this.concreteProtocol.writeStructBegin(tStruct);
    }
    
    @Override
    public void writeStructEnd() throws TException {
        this.concreteProtocol.writeStructEnd();
    }
    
    @Override
    public void writeFieldBegin(final TField tField) throws TException {
        this.concreteProtocol.writeFieldBegin(tField);
    }
    
    @Override
    public void writeFieldEnd() throws TException {
        this.concreteProtocol.writeFieldEnd();
    }
    
    @Override
    public void writeFieldStop() throws TException {
        this.concreteProtocol.writeFieldStop();
    }
    
    @Override
    public void writeMapBegin(final TMap tMap) throws TException {
        this.concreteProtocol.writeMapBegin(tMap);
    }
    
    @Override
    public void writeMapEnd() throws TException {
        this.concreteProtocol.writeMapEnd();
    }
    
    @Override
    public void writeListBegin(final TList tList) throws TException {
        this.concreteProtocol.writeListBegin(tList);
    }
    
    @Override
    public void writeListEnd() throws TException {
        this.concreteProtocol.writeListEnd();
    }
    
    @Override
    public void writeSetBegin(final TSet tSet) throws TException {
        this.concreteProtocol.writeSetBegin(tSet);
    }
    
    @Override
    public void writeSetEnd() throws TException {
        this.concreteProtocol.writeSetEnd();
    }
    
    @Override
    public void writeBool(final boolean b) throws TException {
        this.concreteProtocol.writeBool(b);
    }
    
    @Override
    public void writeByte(final byte b) throws TException {
        this.concreteProtocol.writeByte(b);
    }
    
    @Override
    public void writeI16(final short i) throws TException {
        this.concreteProtocol.writeI16(i);
    }
    
    @Override
    public void writeI32(final int i) throws TException {
        this.concreteProtocol.writeI32(i);
    }
    
    @Override
    public void writeI64(final long l) throws TException {
        this.concreteProtocol.writeI64(l);
    }
    
    @Override
    public void writeDouble(final double v) throws TException {
        this.concreteProtocol.writeDouble(v);
    }
    
    @Override
    public void writeString(final String s) throws TException {
        this.concreteProtocol.writeString(s);
    }
    
    @Override
    public void writeBinary(final ByteBuffer buf) throws TException {
        this.concreteProtocol.writeBinary(buf);
    }
    
    @Override
    public TMessage readMessageBegin() throws TException {
        return this.concreteProtocol.readMessageBegin();
    }
    
    @Override
    public void readMessageEnd() throws TException {
        this.concreteProtocol.readMessageEnd();
    }
    
    @Override
    public TStruct readStructBegin() throws TException {
        return this.concreteProtocol.readStructBegin();
    }
    
    @Override
    public void readStructEnd() throws TException {
        this.concreteProtocol.readStructEnd();
    }
    
    @Override
    public TField readFieldBegin() throws TException {
        return this.concreteProtocol.readFieldBegin();
    }
    
    @Override
    public void readFieldEnd() throws TException {
        this.concreteProtocol.readFieldEnd();
    }
    
    @Override
    public TMap readMapBegin() throws TException {
        return this.concreteProtocol.readMapBegin();
    }
    
    @Override
    public void readMapEnd() throws TException {
        this.concreteProtocol.readMapEnd();
    }
    
    @Override
    public TList readListBegin() throws TException {
        return this.concreteProtocol.readListBegin();
    }
    
    @Override
    public void readListEnd() throws TException {
        this.concreteProtocol.readListEnd();
    }
    
    @Override
    public TSet readSetBegin() throws TException {
        return this.concreteProtocol.readSetBegin();
    }
    
    @Override
    public void readSetEnd() throws TException {
        this.concreteProtocol.readSetEnd();
    }
    
    @Override
    public boolean readBool() throws TException {
        return this.concreteProtocol.readBool();
    }
    
    @Override
    public byte readByte() throws TException {
        return this.concreteProtocol.readByte();
    }
    
    @Override
    public short readI16() throws TException {
        return this.concreteProtocol.readI16();
    }
    
    @Override
    public int readI32() throws TException {
        return this.concreteProtocol.readI32();
    }
    
    @Override
    public long readI64() throws TException {
        return this.concreteProtocol.readI64();
    }
    
    @Override
    public double readDouble() throws TException {
        return this.concreteProtocol.readDouble();
    }
    
    @Override
    public String readString() throws TException {
        return this.concreteProtocol.readString();
    }
    
    @Override
    public ByteBuffer readBinary() throws TException {
        return this.concreteProtocol.readBinary();
    }
}
