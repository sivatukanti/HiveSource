// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hive.service.cli.thrift;

import java.util.Iterator;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Collections;
import org.apache.thrift.meta_data.FieldValueMetaData;
import java.util.EnumMap;
import org.apache.thrift.TBase;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.IOException;
import org.apache.thrift.transport.TTransport;
import org.apache.thrift.protocol.TCompactProtocol;
import java.io.OutputStream;
import org.apache.thrift.transport.TIOStreamTransport;
import java.io.ObjectOutputStream;
import org.apache.thrift.TFieldIdEnum;
import org.apache.thrift.TEnum;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.thrift.TBaseHelper;
import org.apache.thrift.protocol.TProtocolException;
import org.apache.thrift.TException;
import org.apache.thrift.protocol.TProtocolUtil;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.meta_data.FieldMetaData;
import java.util.Map;
import org.apache.thrift.protocol.TField;
import org.apache.thrift.protocol.TStruct;
import org.apache.thrift.TUnion;

public class TTypeQualifierValue extends TUnion<TTypeQualifierValue, _Fields>
{
    private static final TStruct STRUCT_DESC;
    private static final TField I32_VALUE_FIELD_DESC;
    private static final TField STRING_VALUE_FIELD_DESC;
    public static final Map<_Fields, FieldMetaData> metaDataMap;
    
    public TTypeQualifierValue() {
    }
    
    public TTypeQualifierValue(final _Fields setField, final Object value) {
        super(setField, value);
    }
    
    public TTypeQualifierValue(final TTypeQualifierValue other) {
        super(other);
    }
    
    @Override
    public TTypeQualifierValue deepCopy() {
        return new TTypeQualifierValue(this);
    }
    
    public static TTypeQualifierValue i32Value(final int value) {
        final TTypeQualifierValue x = new TTypeQualifierValue();
        x.setI32Value(value);
        return x;
    }
    
    public static TTypeQualifierValue stringValue(final String value) {
        final TTypeQualifierValue x = new TTypeQualifierValue();
        x.setStringValue(value);
        return x;
    }
    
    @Override
    protected void checkType(final _Fields setField, final Object value) throws ClassCastException {
        switch (setField) {
            case I32_VALUE: {
                if (value instanceof Integer) {
                    break;
                }
                throw new ClassCastException("Was expecting value of type Integer for field 'i32Value', but got " + value.getClass().getSimpleName());
            }
            case STRING_VALUE: {
                if (value instanceof String) {
                    break;
                }
                throw new ClassCastException("Was expecting value of type String for field 'stringValue', but got " + value.getClass().getSimpleName());
            }
            default: {
                throw new IllegalArgumentException("Unknown field id " + setField);
            }
        }
    }
    
    @Override
    protected Object standardSchemeReadValue(final TProtocol iprot, final TField field) throws TException {
        final _Fields setField = _Fields.findByThriftId(field.id);
        if (setField == null) {
            return null;
        }
        switch (setField) {
            case I32_VALUE: {
                if (field.type == TTypeQualifierValue.I32_VALUE_FIELD_DESC.type) {
                    final Integer i32Value = iprot.readI32();
                    return i32Value;
                }
                TProtocolUtil.skip(iprot, field.type);
                return null;
            }
            case STRING_VALUE: {
                if (field.type == TTypeQualifierValue.STRING_VALUE_FIELD_DESC.type) {
                    final String stringValue = iprot.readString();
                    return stringValue;
                }
                TProtocolUtil.skip(iprot, field.type);
                return null;
            }
            default: {
                throw new IllegalStateException("setField wasn't null, but didn't match any of the case statements!");
            }
        }
    }
    
    @Override
    protected void standardSchemeWriteValue(final TProtocol oprot) throws TException {
        switch ((_Fields)this.setField_) {
            case I32_VALUE: {
                final Integer i32Value = (Integer)this.value_;
                oprot.writeI32(i32Value);
            }
            case STRING_VALUE: {
                final String stringValue = (String)this.value_;
                oprot.writeString(stringValue);
            }
            default: {
                throw new IllegalStateException("Cannot write union with unknown field " + this.setField_);
            }
        }
    }
    
    @Override
    protected Object tupleSchemeReadValue(final TProtocol iprot, final short fieldID) throws TException {
        final _Fields setField = _Fields.findByThriftId(fieldID);
        if (setField == null) {
            throw new TProtocolException("Couldn't find a field with field id " + fieldID);
        }
        switch (setField) {
            case I32_VALUE: {
                final Integer i32Value = iprot.readI32();
                return i32Value;
            }
            case STRING_VALUE: {
                final String stringValue = iprot.readString();
                return stringValue;
            }
            default: {
                throw new IllegalStateException("setField wasn't null, but didn't match any of the case statements!");
            }
        }
    }
    
    @Override
    protected void tupleSchemeWriteValue(final TProtocol oprot) throws TException {
        switch ((_Fields)this.setField_) {
            case I32_VALUE: {
                final Integer i32Value = (Integer)this.value_;
                oprot.writeI32(i32Value);
            }
            case STRING_VALUE: {
                final String stringValue = (String)this.value_;
                oprot.writeString(stringValue);
            }
            default: {
                throw new IllegalStateException("Cannot write union with unknown field " + this.setField_);
            }
        }
    }
    
    @Override
    protected TField getFieldDesc(final _Fields setField) {
        switch (setField) {
            case I32_VALUE: {
                return TTypeQualifierValue.I32_VALUE_FIELD_DESC;
            }
            case STRING_VALUE: {
                return TTypeQualifierValue.STRING_VALUE_FIELD_DESC;
            }
            default: {
                throw new IllegalArgumentException("Unknown field id " + setField);
            }
        }
    }
    
    @Override
    protected TStruct getStructDesc() {
        return TTypeQualifierValue.STRUCT_DESC;
    }
    
    @Override
    protected _Fields enumForId(final short id) {
        return _Fields.findByThriftIdOrThrow(id);
    }
    
    @Override
    public _Fields fieldForId(final int fieldId) {
        return _Fields.findByThriftId(fieldId);
    }
    
    public int getI32Value() {
        if (this.getSetField() == _Fields.I32_VALUE) {
            return (int)this.getFieldValue();
        }
        throw new RuntimeException("Cannot get field 'i32Value' because union is currently set to " + this.getFieldDesc(((TUnion<T, _Fields>)this).getSetField()).name);
    }
    
    public void setI32Value(final int value) {
        this.setField_ = (F)_Fields.I32_VALUE;
        this.value_ = value;
    }
    
    public String getStringValue() {
        if (this.getSetField() == _Fields.STRING_VALUE) {
            return (String)this.getFieldValue();
        }
        throw new RuntimeException("Cannot get field 'stringValue' because union is currently set to " + this.getFieldDesc(((TUnion<T, _Fields>)this).getSetField()).name);
    }
    
    public void setStringValue(final String value) {
        if (value == null) {
            throw new NullPointerException();
        }
        this.setField_ = (F)_Fields.STRING_VALUE;
        this.value_ = value;
    }
    
    public boolean isSetI32Value() {
        return this.setField_ == _Fields.I32_VALUE;
    }
    
    public boolean isSetStringValue() {
        return this.setField_ == _Fields.STRING_VALUE;
    }
    
    @Override
    public boolean equals(final Object other) {
        return other instanceof TTypeQualifierValue && this.equals((TTypeQualifierValue)other);
    }
    
    public boolean equals(final TTypeQualifierValue other) {
        return other != null && this.getSetField() == other.getSetField() && this.getFieldValue().equals(other.getFieldValue());
    }
    
    @Override
    public int compareTo(final TTypeQualifierValue other) {
        final int lastComparison = TBaseHelper.compareTo(((TUnion<T, Comparable>)this).getSetField(), ((TUnion<T, Comparable>)other).getSetField());
        if (lastComparison == 0) {
            return TBaseHelper.compareTo(this.getFieldValue(), other.getFieldValue());
        }
        return lastComparison;
    }
    
    @Override
    public int hashCode() {
        final HashCodeBuilder hcb = new HashCodeBuilder();
        hcb.append(this.getClass().getName());
        final TFieldIdEnum setField = ((TUnion<T, TFieldIdEnum>)this).getSetField();
        if (setField != null) {
            hcb.append(setField.getThriftFieldId());
            final Object value = this.getFieldValue();
            if (value instanceof TEnum) {
                hcb.append(((TEnum)this.getFieldValue()).getValue());
            }
            else {
                hcb.append(value);
            }
        }
        return hcb.toHashCode();
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
        STRUCT_DESC = new TStruct("TTypeQualifierValue");
        I32_VALUE_FIELD_DESC = new TField("i32Value", (byte)8, (short)1);
        STRING_VALUE_FIELD_DESC = new TField("stringValue", (byte)11, (short)2);
        final Map<_Fields, FieldMetaData> tmpMap = new EnumMap<_Fields, FieldMetaData>(_Fields.class);
        tmpMap.put(_Fields.I32_VALUE, new FieldMetaData("i32Value", (byte)2, new FieldValueMetaData((byte)8)));
        tmpMap.put(_Fields.STRING_VALUE, new FieldMetaData("stringValue", (byte)2, new FieldValueMetaData((byte)11)));
        FieldMetaData.addStructMetaDataMap(TTypeQualifierValue.class, metaDataMap = Collections.unmodifiableMap((Map<? extends _Fields, ? extends FieldMetaData>)tmpMap));
    }
    
    public enum _Fields implements TFieldIdEnum
    {
        I32_VALUE((short)1, "i32Value"), 
        STRING_VALUE((short)2, "stringValue");
        
        private static final Map<String, _Fields> byName;
        private final short _thriftId;
        private final String _fieldName;
        
        public static _Fields findByThriftId(final int fieldId) {
            switch (fieldId) {
                case 1: {
                    return _Fields.I32_VALUE;
                }
                case 2: {
                    return _Fields.STRING_VALUE;
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
}
