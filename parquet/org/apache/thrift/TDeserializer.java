// 
// Decompiled by Procyon v0.5.36
// 

package parquet.org.apache.thrift;

import parquet.org.apache.thrift.protocol.TProtocolUtil;
import parquet.org.apache.thrift.protocol.TField;
import java.nio.ByteBuffer;
import java.io.UnsupportedEncodingException;
import parquet.org.apache.thrift.transport.TTransport;
import parquet.org.apache.thrift.protocol.TProtocolFactory;
import parquet.org.apache.thrift.protocol.TBinaryProtocol;
import parquet.org.apache.thrift.transport.TMemoryInputTransport;
import parquet.org.apache.thrift.protocol.TProtocol;

public class TDeserializer
{
    private final TProtocol protocol_;
    private final TMemoryInputTransport trans_;
    
    public TDeserializer() {
        this(new TBinaryProtocol.Factory());
    }
    
    public TDeserializer(final TProtocolFactory protocolFactory) {
        this.trans_ = new TMemoryInputTransport();
        this.protocol_ = protocolFactory.getProtocol(this.trans_);
    }
    
    public void deserialize(final TBase base, final byte[] bytes) throws TException {
        try {
            this.trans_.reset(bytes);
            base.read(this.protocol_);
        }
        finally {
            this.trans_.clear();
            this.protocol_.reset();
        }
    }
    
    public void deserialize(final TBase base, final String data, final String charset) throws TException {
        try {
            this.deserialize(base, data.getBytes(charset));
        }
        catch (UnsupportedEncodingException uex) {
            throw new TException("JVM DOES NOT SUPPORT ENCODING: " + charset);
        }
        finally {
            this.protocol_.reset();
        }
    }
    
    public void partialDeserialize(final TBase tb, final byte[] bytes, final TFieldIdEnum fieldIdPathFirst, final TFieldIdEnum... fieldIdPathRest) throws TException {
        try {
            if (this.locateField(bytes, fieldIdPathFirst, fieldIdPathRest) != null) {
                tb.read(this.protocol_);
            }
        }
        catch (Exception e) {
            throw new TException(e);
        }
        finally {
            this.trans_.clear();
            this.protocol_.reset();
        }
    }
    
    public Boolean partialDeserializeBool(final byte[] bytes, final TFieldIdEnum fieldIdPathFirst, final TFieldIdEnum... fieldIdPathRest) throws TException {
        return (Boolean)this.partialDeserializeField((byte)2, bytes, fieldIdPathFirst, fieldIdPathRest);
    }
    
    public Byte partialDeserializeByte(final byte[] bytes, final TFieldIdEnum fieldIdPathFirst, final TFieldIdEnum... fieldIdPathRest) throws TException {
        return (Byte)this.partialDeserializeField((byte)3, bytes, fieldIdPathFirst, fieldIdPathRest);
    }
    
    public Double partialDeserializeDouble(final byte[] bytes, final TFieldIdEnum fieldIdPathFirst, final TFieldIdEnum... fieldIdPathRest) throws TException {
        return (Double)this.partialDeserializeField((byte)4, bytes, fieldIdPathFirst, fieldIdPathRest);
    }
    
    public Short partialDeserializeI16(final byte[] bytes, final TFieldIdEnum fieldIdPathFirst, final TFieldIdEnum... fieldIdPathRest) throws TException {
        return (Short)this.partialDeserializeField((byte)6, bytes, fieldIdPathFirst, fieldIdPathRest);
    }
    
    public Integer partialDeserializeI32(final byte[] bytes, final TFieldIdEnum fieldIdPathFirst, final TFieldIdEnum... fieldIdPathRest) throws TException {
        return (Integer)this.partialDeserializeField((byte)8, bytes, fieldIdPathFirst, fieldIdPathRest);
    }
    
    public Long partialDeserializeI64(final byte[] bytes, final TFieldIdEnum fieldIdPathFirst, final TFieldIdEnum... fieldIdPathRest) throws TException {
        return (Long)this.partialDeserializeField((byte)10, bytes, fieldIdPathFirst, fieldIdPathRest);
    }
    
    public String partialDeserializeString(final byte[] bytes, final TFieldIdEnum fieldIdPathFirst, final TFieldIdEnum... fieldIdPathRest) throws TException {
        return (String)this.partialDeserializeField((byte)11, bytes, fieldIdPathFirst, fieldIdPathRest);
    }
    
    public ByteBuffer partialDeserializeByteArray(final byte[] bytes, final TFieldIdEnum fieldIdPathFirst, final TFieldIdEnum... fieldIdPathRest) throws TException {
        return (ByteBuffer)this.partialDeserializeField((byte)100, bytes, fieldIdPathFirst, fieldIdPathRest);
    }
    
    public Short partialDeserializeSetFieldIdInUnion(final byte[] bytes, final TFieldIdEnum fieldIdPathFirst, final TFieldIdEnum... fieldIdPathRest) throws TException {
        try {
            final TField field = this.locateField(bytes, fieldIdPathFirst, fieldIdPathRest);
            if (field != null) {
                this.protocol_.readStructBegin();
                return this.protocol_.readFieldBegin().id;
            }
            return null;
        }
        catch (Exception e) {
            throw new TException(e);
        }
        finally {
            this.trans_.clear();
            this.protocol_.reset();
        }
    }
    
    private Object partialDeserializeField(final byte ttype, final byte[] bytes, final TFieldIdEnum fieldIdPathFirst, final TFieldIdEnum... fieldIdPathRest) throws TException {
        try {
            final TField field = this.locateField(bytes, fieldIdPathFirst, fieldIdPathRest);
            if (field != null) {
                switch (ttype) {
                    case 2: {
                        if (field.type == 2) {
                            return this.protocol_.readBool();
                        }
                        break;
                    }
                    case 3: {
                        if (field.type == 3) {
                            return this.protocol_.readByte();
                        }
                        break;
                    }
                    case 4: {
                        if (field.type == 4) {
                            return this.protocol_.readDouble();
                        }
                        break;
                    }
                    case 6: {
                        if (field.type == 6) {
                            return this.protocol_.readI16();
                        }
                        break;
                    }
                    case 8: {
                        if (field.type == 8) {
                            return this.protocol_.readI32();
                        }
                        break;
                    }
                    case 10: {
                        if (field.type == 10) {
                            return this.protocol_.readI64();
                        }
                        break;
                    }
                    case 11: {
                        if (field.type == 11) {
                            return this.protocol_.readString();
                        }
                        break;
                    }
                    case 100: {
                        if (field.type == 11) {
                            return this.protocol_.readBinary();
                        }
                        break;
                    }
                }
            }
            return null;
        }
        catch (Exception e) {
            throw new TException(e);
        }
        finally {
            this.trans_.clear();
            this.protocol_.reset();
        }
    }
    
    private TField locateField(final byte[] bytes, final TFieldIdEnum fieldIdPathFirst, final TFieldIdEnum... fieldIdPathRest) throws TException {
        this.trans_.reset(bytes);
        final TFieldIdEnum[] fieldIdPath = new TFieldIdEnum[fieldIdPathRest.length + 1];
        fieldIdPath[0] = fieldIdPathFirst;
        for (int i = 0; i < fieldIdPathRest.length; ++i) {
            fieldIdPath[i + 1] = fieldIdPathRest[i];
        }
        int curPathIndex = 0;
        TField field = null;
        this.protocol_.readStructBegin();
        while (curPathIndex < fieldIdPath.length) {
            field = this.protocol_.readFieldBegin();
            if (field.type == 0 || field.id > fieldIdPath[curPathIndex].getThriftFieldId()) {
                return null;
            }
            if (field.id != fieldIdPath[curPathIndex].getThriftFieldId()) {
                TProtocolUtil.skip(this.protocol_, field.type);
                this.protocol_.readFieldEnd();
            }
            else {
                if (++curPathIndex >= fieldIdPath.length) {
                    continue;
                }
                this.protocol_.readStructBegin();
            }
        }
        return field;
    }
    
    public void fromString(final TBase base, final String data) throws TException {
        this.deserialize(base, data.getBytes());
    }
}
