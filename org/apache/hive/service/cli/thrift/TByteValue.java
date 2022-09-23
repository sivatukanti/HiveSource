// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hive.service.cli.thrift;

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
import org.apache.thrift.TException;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.TBaseHelper;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.thrift.EncodingUtils;
import org.apache.thrift.meta_data.FieldMetaData;
import org.apache.thrift.scheme.SchemeFactory;
import org.apache.thrift.scheme.IScheme;
import java.util.Map;
import org.apache.thrift.protocol.TField;
import org.apache.thrift.protocol.TStruct;
import java.io.Serializable;
import org.apache.thrift.TBase;

public class TByteValue implements TBase<TByteValue, _Fields>, Serializable, Cloneable
{
    private static final TStruct STRUCT_DESC;
    private static final TField VALUE_FIELD_DESC;
    private static final Map<Class<? extends IScheme>, SchemeFactory> schemes;
    private byte value;
    private static final int __VALUE_ISSET_ID = 0;
    private byte __isset_bitfield;
    private _Fields[] optionals;
    public static final Map<_Fields, FieldMetaData> metaDataMap;
    
    public TByteValue() {
        this.__isset_bitfield = 0;
        this.optionals = new _Fields[] { _Fields.VALUE };
    }
    
    public TByteValue(final TByteValue other) {
        this.__isset_bitfield = 0;
        this.optionals = new _Fields[] { _Fields.VALUE };
        this.__isset_bitfield = other.__isset_bitfield;
        this.value = other.value;
    }
    
    @Override
    public TByteValue deepCopy() {
        return new TByteValue(this);
    }
    
    @Override
    public void clear() {
        this.setValueIsSet(false);
        this.value = 0;
    }
    
    public byte getValue() {
        return this.value;
    }
    
    public void setValue(final byte value) {
        this.value = value;
        this.setValueIsSet(true);
    }
    
    public void unsetValue() {
        this.__isset_bitfield = EncodingUtils.clearBit(this.__isset_bitfield, 0);
    }
    
    public boolean isSetValue() {
        return EncodingUtils.testBit(this.__isset_bitfield, 0);
    }
    
    public void setValueIsSet(final boolean value) {
        this.__isset_bitfield = EncodingUtils.setBit(this.__isset_bitfield, 0, value);
    }
    
    @Override
    public void setFieldValue(final _Fields field, final Object value) {
        switch (field) {
            case VALUE: {
                if (value == null) {
                    this.unsetValue();
                    break;
                }
                this.setValue((byte)value);
                break;
            }
        }
    }
    
    @Override
    public Object getFieldValue(final _Fields field) {
        switch (field) {
            case VALUE: {
                return this.getValue();
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
            case VALUE: {
                return this.isSetValue();
            }
            default: {
                throw new IllegalStateException();
            }
        }
    }
    
    @Override
    public boolean equals(final Object that) {
        return that != null && that instanceof TByteValue && this.equals((TByteValue)that);
    }
    
    public boolean equals(final TByteValue that) {
        if (that == null) {
            return false;
        }
        final boolean this_present_value = this.isSetValue();
        final boolean that_present_value = that.isSetValue();
        if (this_present_value || that_present_value) {
            if (!this_present_value || !that_present_value) {
                return false;
            }
            if (this.value != that.value) {
                return false;
            }
        }
        return true;
    }
    
    @Override
    public int hashCode() {
        final HashCodeBuilder builder = new HashCodeBuilder();
        final boolean present_value = this.isSetValue();
        builder.append(present_value);
        if (present_value) {
            builder.append(this.value);
        }
        return builder.toHashCode();
    }
    
    @Override
    public int compareTo(final TByteValue other) {
        if (!this.getClass().equals(other.getClass())) {
            return this.getClass().getName().compareTo(other.getClass().getName());
        }
        int lastComparison = 0;
        final TByteValue typedOther = other;
        lastComparison = Boolean.valueOf(this.isSetValue()).compareTo(Boolean.valueOf(typedOther.isSetValue()));
        if (lastComparison != 0) {
            return lastComparison;
        }
        if (this.isSetValue()) {
            lastComparison = TBaseHelper.compareTo(this.value, typedOther.value);
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
        TByteValue.schemes.get(iprot.getScheme()).getScheme().read(iprot, this);
    }
    
    @Override
    public void write(final TProtocol oprot) throws TException {
        TByteValue.schemes.get(oprot.getScheme()).getScheme().write(oprot, this);
    }
    
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("TByteValue(");
        boolean first = true;
        if (this.isSetValue()) {
            sb.append("value:");
            sb.append(this.value);
            first = false;
        }
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
            this.__isset_bitfield = 0;
            this.read(new TCompactProtocol(new TIOStreamTransport(in)));
        }
        catch (TException te) {
            throw new IOException(te);
        }
    }
    
    static {
        STRUCT_DESC = new TStruct("TByteValue");
        VALUE_FIELD_DESC = new TField("value", (byte)3, (short)1);
        (schemes = new HashMap<Class<? extends IScheme>, SchemeFactory>()).put(StandardScheme.class, new TByteValueStandardSchemeFactory());
        TByteValue.schemes.put(TupleScheme.class, new TByteValueTupleSchemeFactory());
        final Map<_Fields, FieldMetaData> tmpMap = new EnumMap<_Fields, FieldMetaData>(_Fields.class);
        tmpMap.put(_Fields.VALUE, new FieldMetaData("value", (byte)2, new FieldValueMetaData((byte)3)));
        FieldMetaData.addStructMetaDataMap(TByteValue.class, metaDataMap = Collections.unmodifiableMap((Map<? extends _Fields, ? extends FieldMetaData>)tmpMap));
    }
    
    public enum _Fields implements TFieldIdEnum
    {
        VALUE((short)1, "value");
        
        private static final Map<String, _Fields> byName;
        private final short _thriftId;
        private final String _fieldName;
        
        public static _Fields findByThriftId(final int fieldId) {
            switch (fieldId) {
                case 1: {
                    return _Fields.VALUE;
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
    
    private static class TByteValueStandardSchemeFactory implements SchemeFactory
    {
        @Override
        public TByteValueStandardScheme getScheme() {
            return new TByteValueStandardScheme();
        }
    }
    
    private static class TByteValueStandardScheme extends StandardScheme<TByteValue>
    {
        @Override
        public void read(final TProtocol iprot, final TByteValue struct) throws TException {
            iprot.readStructBegin();
            while (true) {
                final TField schemeField = iprot.readFieldBegin();
                if (schemeField.type == 0) {
                    break;
                }
                switch (schemeField.id) {
                    case 1: {
                        if (schemeField.type == 3) {
                            struct.value = iprot.readByte();
                            struct.setValueIsSet(true);
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
        public void write(final TProtocol oprot, final TByteValue struct) throws TException {
            struct.validate();
            oprot.writeStructBegin(TByteValue.STRUCT_DESC);
            if (struct.isSetValue()) {
                oprot.writeFieldBegin(TByteValue.VALUE_FIELD_DESC);
                oprot.writeByte(struct.value);
                oprot.writeFieldEnd();
            }
            oprot.writeFieldStop();
            oprot.writeStructEnd();
        }
    }
    
    private static class TByteValueTupleSchemeFactory implements SchemeFactory
    {
        @Override
        public TByteValueTupleScheme getScheme() {
            return new TByteValueTupleScheme();
        }
    }
    
    private static class TByteValueTupleScheme extends TupleScheme<TByteValue>
    {
        @Override
        public void write(final TProtocol prot, final TByteValue struct) throws TException {
            final TTupleProtocol oprot = (TTupleProtocol)prot;
            final BitSet optionals = new BitSet();
            if (struct.isSetValue()) {
                optionals.set(0);
            }
            oprot.writeBitSet(optionals, 1);
            if (struct.isSetValue()) {
                oprot.writeByte(struct.value);
            }
        }
        
        @Override
        public void read(final TProtocol prot, final TByteValue struct) throws TException {
            final TTupleProtocol iprot = (TTupleProtocol)prot;
            final BitSet incoming = iprot.readBitSet(1);
            if (incoming.get(0)) {
                struct.value = iprot.readByte();
                struct.setValueIsSet(true);
            }
        }
    }
}
