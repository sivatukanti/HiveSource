// 
// Decompiled by Procyon v0.5.36
// 

package parquet.org.apache.thrift.protocol;

import java.nio.ByteBuffer;
import parquet.org.apache.thrift.TException;
import parquet.org.apache.thrift.transport.TTransport;

public abstract class TProtocol
{
    protected TTransport trans_;
    
    private TProtocol() {
    }
    
    protected TProtocol(final TTransport trans) {
        this.trans_ = trans;
    }
    
    public TTransport getTransport() {
        return this.trans_;
    }
    
    public abstract void writeMessageBegin(final TMessage p0) throws TException;
    
    public abstract void writeMessageEnd() throws TException;
    
    public abstract void writeStructBegin(final TStruct p0) throws TException;
    
    public abstract void writeStructEnd() throws TException;
    
    public abstract void writeFieldBegin(final TField p0) throws TException;
    
    public abstract void writeFieldEnd() throws TException;
    
    public abstract void writeFieldStop() throws TException;
    
    public abstract void writeMapBegin(final TMap p0) throws TException;
    
    public abstract void writeMapEnd() throws TException;
    
    public abstract void writeListBegin(final TList p0) throws TException;
    
    public abstract void writeListEnd() throws TException;
    
    public abstract void writeSetBegin(final TSet p0) throws TException;
    
    public abstract void writeSetEnd() throws TException;
    
    public abstract void writeBool(final boolean p0) throws TException;
    
    public abstract void writeByte(final byte p0) throws TException;
    
    public abstract void writeI16(final short p0) throws TException;
    
    public abstract void writeI32(final int p0) throws TException;
    
    public abstract void writeI64(final long p0) throws TException;
    
    public abstract void writeDouble(final double p0) throws TException;
    
    public abstract void writeString(final String p0) throws TException;
    
    public abstract void writeBinary(final ByteBuffer p0) throws TException;
    
    public abstract TMessage readMessageBegin() throws TException;
    
    public abstract void readMessageEnd() throws TException;
    
    public abstract TStruct readStructBegin() throws TException;
    
    public abstract void readStructEnd() throws TException;
    
    public abstract TField readFieldBegin() throws TException;
    
    public abstract void readFieldEnd() throws TException;
    
    public abstract TMap readMapBegin() throws TException;
    
    public abstract void readMapEnd() throws TException;
    
    public abstract TList readListBegin() throws TException;
    
    public abstract void readListEnd() throws TException;
    
    public abstract TSet readSetBegin() throws TException;
    
    public abstract void readSetEnd() throws TException;
    
    public abstract boolean readBool() throws TException;
    
    public abstract byte readByte() throws TException;
    
    public abstract short readI16() throws TException;
    
    public abstract int readI32() throws TException;
    
    public abstract long readI64() throws TException;
    
    public abstract double readDouble() throws TException;
    
    public abstract String readString() throws TException;
    
    public abstract ByteBuffer readBinary() throws TException;
    
    public void reset() {
    }
}
