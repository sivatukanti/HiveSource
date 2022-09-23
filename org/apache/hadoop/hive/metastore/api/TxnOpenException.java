// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.hive.metastore.api;

import java.util.BitSet;
import org.apache.thrift.protocol.TTupleProtocol;
import org.apache.thrift.protocol.TProtocolUtil;
import java.util.Iterator;
import java.util.EnumSet;
import java.util.Collections;
import org.apache.thrift.meta_data.FieldValueMetaData;
import java.util.EnumMap;
import org.apache.thrift.scheme.TupleScheme;
import org.apache.thrift.scheme.StandardScheme;
import java.util.HashMap;
import org.apache.thrift.TFieldIdEnum;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.IOException;
import org.apache.thrift.transport.TTransport;
import org.apache.thrift.protocol.TCompactProtocol;
import java.io.OutputStream;
import org.apache.thrift.transport.TIOStreamTransport;
import java.io.ObjectOutputStream;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.TBaseHelper;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.thrift.meta_data.FieldMetaData;
import org.apache.thrift.scheme.SchemeFactory;
import org.apache.thrift.scheme.IScheme;
import java.util.Map;
import org.apache.thrift.protocol.TField;
import org.apache.thrift.protocol.TStruct;
import java.io.Serializable;
import org.apache.thrift.TBase;
import org.apache.thrift.TException;

public class TxnOpenException extends TException implements TBase<TxnOpenException, _Fields>, Serializable, Cloneable
{
    private static final TStruct STRUCT_DESC;
    private static final TField MESSAGE_FIELD_DESC;
    private static final Map<Class<? extends IScheme>, SchemeFactory> schemes;
    private String message;
    public static final Map<_Fields, FieldMetaData> metaDataMap;
    
    public TxnOpenException() {
    }
    
    public TxnOpenException(final String message) {
        this();
        this.message = message;
    }
    
    public TxnOpenException(final TxnOpenException other) {
        if (other.isSetMessage()) {
            this.message = other.message;
        }
    }
    
    @Override
    public TxnOpenException deepCopy() {
        return new TxnOpenException(this);
    }
    
    @Override
    public void clear() {
        this.message = null;
    }
    
    @Override
    public String getMessage() {
        return this.message;
    }
    
    public void setMessage(final String message) {
        this.message = message;
    }
    
    public void unsetMessage() {
        this.message = null;
    }
    
    public boolean isSetMessage() {
        return this.message != null;
    }
    
    public void setMessageIsSet(final boolean value) {
        if (!value) {
            this.message = null;
        }
    }
    
    @Override
    public void setFieldValue(final _Fields field, final Object value) {
        switch (field) {
            case MESSAGE: {
                if (value == null) {
                    this.unsetMessage();
                    break;
                }
                this.setMessage((String)value);
                break;
            }
        }
    }
    
    @Override
    public Object getFieldValue(final _Fields field) {
        switch (field) {
            case MESSAGE: {
                return this.getMessage();
            }
            default: {
                throw new IllegalStateException();
            }
        }
    }
    
    @Override
    public boolean isSet(final _Fields field) {
        if (field == null) {
            throw new IllegalArgumentException();
        }
        switch (field) {
            case MESSAGE: {
                return this.isSetMessage();
            }
            default: {
                throw new IllegalStateException();
            }
        }
    }
    
    @Override
    public boolean equals(final Object that) {
        return that != null && that instanceof TxnOpenException && this.equals((TxnOpenException)that);
    }
    
    public boolean equals(final TxnOpenException that) {
        if (that == null) {
            return false;
        }
        final boolean this_present_message = this.isSetMessage();
        final boolean that_present_message = that.isSetMessage();
        if (this_present_message || that_present_message) {
            if (!this_present_message || !that_present_message) {
                return false;
            }
            if (!this.message.equals(that.message)) {
                return false;
            }
        }
        return true;
    }
    
    @Override
    public int hashCode() {
        final HashCodeBuilder builder = new HashCodeBuilder();
        final boolean present_message = this.isSetMessage();
        builder.append(present_message);
        if (present_message) {
            builder.append(this.message);
        }
        return builder.toHashCode();
    }
    
    @Override
    public int compareTo(final TxnOpenException other) {
        if (!this.getClass().equals(other.getClass())) {
            return this.getClass().getName().compareTo(other.getClass().getName());
        }
        int lastComparison = 0;
        final TxnOpenException typedOther = other;
        lastComparison = Boolean.valueOf(this.isSetMessage()).compareTo(Boolean.valueOf(typedOther.isSetMessage()));
        if (lastComparison != 0) {
            return lastComparison;
        }
        if (this.isSetMessage()) {
            lastComparison = TBaseHelper.compareTo(this.message, typedOther.message);
            if (lastComparison != 0) {
                return lastComparison;
            }
        }
        return 0;
    }
    
    @Override
    public _Fields fieldForId(final int fieldId) {
        return _Fields.findByThriftId(fieldId);
    }
    
    @Override
    public void read(final TProtocol iprot) throws TException {
        TxnOpenException.schemes.get(iprot.getScheme()).getScheme().read(iprot, this);
    }
    
    @Override
    public void write(final TProtocol oprot) throws TException {
        TxnOpenException.schemes.get(oprot.getScheme()).getScheme().write(oprot, this);
    }
    
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("TxnOpenException(");
        boolean first = true;
        sb.append("message:");
        if (this.message == null) {
            sb.append("null");
        }
        else {
            sb.append(this.message);
        }
        first = false;
        sb.append(")");
        return sb.toString();
    }
    
    public void validate() throws TException {
    }
    
    private void writeObject(final ObjectOutputStream out) throws IOException {
        try {
            this.write(new TCompactProtocol(new TIOStreamTransport(out)));
        }
        catch (TException te) {
            throw new IOException(te);
        }
    }
    
    private void readObject(final ObjectInputStream in) throws IOException, ClassNotFoundException {
        try {
            this.read(new TCompactProtocol(new TIOStreamTransport(in)));
        }
        catch (TException te) {
            throw new IOException(te);
        }
    }
    
    static {
        STRUCT_DESC = new TStruct("TxnOpenException");
        MESSAGE_FIELD_DESC = new TField("message", (byte)11, (short)1);
        (schemes = new HashMap<Class<? extends IScheme>, SchemeFactory>()).put(StandardScheme.class, new TxnOpenExceptionStandardSchemeFactory());
        TxnOpenException.schemes.put(TupleScheme.class, new TxnOpenExceptionTupleSchemeFactory());
        final Map<_Fields, FieldMetaData> tmpMap = new EnumMap<_Fields, FieldMetaData>(_Fields.class);
        tmpMap.put(_Fields.MESSAGE, new FieldMetaData("message", (byte)3, new FieldValueMetaData((byte)11)));
        FieldMetaData.addStructMetaDataMap(TxnOpenException.class, metaDataMap = Collections.unmodifiableMap((Map<? extends _Fields, ? extends FieldMetaData>)tmpMap));
    }
    
    public enum _Fields implements TFieldIdEnum
    {
        MESSAGE((short)1, "message");
        
        private static final Map<String, _Fields> byName;
        private final short _thriftId;
        private final String _fieldName;
        
        public static _Fields findByThriftId(final int fieldId) {
            switch (fieldId) {
                case 1: {
                    return _Fields.MESSAGE;
                }
                default: {
                    return null;
                }
            }
        }
        
        public static _Fields findByThriftIdOrThrow(final int fieldId) {
            final _Fields fields = findByThriftId(fieldId);
            if (fields == null) {
                throw new IllegalArgumentException("Field " + fieldId + " doesn't exist!");
            }
            return fields;
        }
        
        public static _Fields findByName(final String name) {
            return _Fields.byName.get(name);
        }
        
        private _Fields(final short thriftId, final String fieldName) {
            this._thriftId = thriftId;
            this._fieldName = fieldName;
        }
        
        @Override
        public short getThriftFieldId() {
            return this._thriftId;
        }
        
        @Override
        public String getFieldName() {
            return this._fieldName;
        }
        
        static {
            byName = new HashMap<String, _Fields>();
            for (final _Fields field : EnumSet.allOf(_Fields.class)) {
                _Fields.byName.put(field.getFieldName(), field);
            }
        }
    }
    
    private static class TxnOpenExceptionStandardSchemeFactory implements SchemeFactory
    {
        @Override
        public TxnOpenExceptionStandardScheme getScheme() {
            return new TxnOpenExceptionStandardScheme();
        }
    }
    
    private static class TxnOpenExceptionStandardScheme extends StandardScheme<TxnOpenException>
    {
        @Override
        public void read(final TProtocol iprot, final TxnOpenException struct) throws TException {
            iprot.readStructBegin();
            while (true) {
                final TField schemeField = iprot.readFieldBegin();
                if (schemeField.type == 0) {
                    break;
                }
                switch (schemeField.id) {
                    case 1: {
                        if (schemeField.type == 11) {
                            struct.message = iprot.readString();
                            struct.setMessageIsSet(true);
                            break;
                        }
                        TProtocolUtil.skip(iprot, schemeField.type);
                        break;
                    }
                    default: {
                        TProtocolUtil.skip(iprot, schemeField.type);
                        break;
                    }
                }
                iprot.readFieldEnd();
            }
            iprot.readStructEnd();
            struct.validate();
        }
        
        @Override
        public void write(final TProtocol oprot, final TxnOpenException struct) throws TException {
            struct.validate();
            oprot.writeStructBegin(TxnOpenException.STRUCT_DESC);
            if (struct.message != null) {
                oprot.writeFieldBegin(TxnOpenException.MESSAGE_FIELD_DESC);
                oprot.writeString(struct.message);
                oprot.writeFieldEnd();
            }
            oprot.writeFieldStop();
            oprot.writeStructEnd();
        }
    }
    
    private static class TxnOpenExceptionTupleSchemeFactory implements SchemeFactory
    {
        @Override
        public TxnOpenExceptionTupleScheme getScheme() {
            return new TxnOpenExceptionTupleScheme();
        }
    }
    
    private static class TxnOpenExceptionTupleScheme extends TupleScheme<TxnOpenException>
    {
        @Override
        public void write(final TProtocol prot, final TxnOpenException struct) throws TException {
            final TTupleProtocol oprot = (TTupleProtocol)prot;
            final BitSet optionals = new BitSet();
            if (struct.isSetMessage()) {
                optionals.set(0);
            }
            oprot.writeBitSet(optionals, 1);
            if (struct.isSetMessage()) {
                oprot.writeString(struct.message);
            }
        }
        
        @Override
        public void read(final TProtocol prot, final TxnOpenException struct) throws TException {
            final TTupleProtocol iprot = (TTupleProtocol)prot;
            final BitSet incoming = iprot.readBitSet(1);
            if (incoming.get(0)) {
                struct.message = iprot.readString();
                struct.setMessageIsSet(true);
            }
        }
    }
}
