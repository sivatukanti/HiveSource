// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hive.service.cli.thrift;

import java.util.Iterator;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Collections;
import org.apache.thrift.meta_data.FieldValueMetaData;
import org.apache.thrift.meta_data.StructMetaData;
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

public class TColumnValue extends TUnion<TColumnValue, _Fields>
{
    private static final TStruct STRUCT_DESC;
    private static final TField BOOL_VAL_FIELD_DESC;
    private static final TField BYTE_VAL_FIELD_DESC;
    private static final TField I16_VAL_FIELD_DESC;
    private static final TField I32_VAL_FIELD_DESC;
    private static final TField I64_VAL_FIELD_DESC;
    private static final TField DOUBLE_VAL_FIELD_DESC;
    private static final TField STRING_VAL_FIELD_DESC;
    public static final Map<_Fields, FieldMetaData> metaDataMap;
    
    public TColumnValue() {
    }
    
    public TColumnValue(final _Fields setField, final Object value) {
        super(setField, value);
    }
    
    public TColumnValue(final TColumnValue other) {
        super(other);
    }
    
    @Override
    public TColumnValue deepCopy() {
        return new TColumnValue(this);
    }
    
    public static TColumnValue boolVal(final TBoolValue value) {
        final TColumnValue x = new TColumnValue();
        x.setBoolVal(value);
        return x;
    }
    
    public static TColumnValue byteVal(final TByteValue value) {
        final TColumnValue x = new TColumnValue();
        x.setByteVal(value);
        return x;
    }
    
    public static TColumnValue i16Val(final TI16Value value) {
        final TColumnValue x = new TColumnValue();
        x.setI16Val(value);
        return x;
    }
    
    public static TColumnValue i32Val(final TI32Value value) {
        final TColumnValue x = new TColumnValue();
        x.setI32Val(value);
        return x;
    }
    
    public static TColumnValue i64Val(final TI64Value value) {
        final TColumnValue x = new TColumnValue();
        x.setI64Val(value);
        return x;
    }
    
    public static TColumnValue doubleVal(final TDoubleValue value) {
        final TColumnValue x = new TColumnValue();
        x.setDoubleVal(value);
        return x;
    }
    
    public static TColumnValue stringVal(final TStringValue value) {
        final TColumnValue x = new TColumnValue();
        x.setStringVal(value);
        return x;
    }
    
    @Override
    protected void checkType(final _Fields setField, final Object value) throws ClassCastException {
        switch (setField) {
            case BOOL_VAL: {
                if (value instanceof TBoolValue) {
                    break;
                }
                throw new ClassCastException("Was expecting value of type TBoolValue for field 'boolVal', but got " + value.getClass().getSimpleName());
            }
            case BYTE_VAL: {
                if (value instanceof TByteValue) {
                    break;
                }
                throw new ClassCastException("Was expecting value of type TByteValue for field 'byteVal', but got " + value.getClass().getSimpleName());
            }
            case I16_VAL: {
                if (value instanceof TI16Value) {
                    break;
                }
                throw new ClassCastException("Was expecting value of type TI16Value for field 'i16Val', but got " + value.getClass().getSimpleName());
            }
            case I32_VAL: {
                if (value instanceof TI32Value) {
                    break;
                }
                throw new ClassCastException("Was expecting value of type TI32Value for field 'i32Val', but got " + value.getClass().getSimpleName());
            }
            case I64_VAL: {
                if (value instanceof TI64Value) {
                    break;
                }
                throw new ClassCastException("Was expecting value of type TI64Value for field 'i64Val', but got " + value.getClass().getSimpleName());
            }
            case DOUBLE_VAL: {
                if (value instanceof TDoubleValue) {
                    break;
                }
                throw new ClassCastException("Was expecting value of type TDoubleValue for field 'doubleVal', but got " + value.getClass().getSimpleName());
            }
            case STRING_VAL: {
                if (value instanceof TStringValue) {
                    break;
                }
                throw new ClassCastException("Was expecting value of type TStringValue for field 'stringVal', but got " + value.getClass().getSimpleName());
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
            case BOOL_VAL: {
                if (field.type == TColumnValue.BOOL_VAL_FIELD_DESC.type) {
                    final TBoolValue boolVal = new TBoolValue();
                    boolVal.read(iprot);
                    return boolVal;
                }
                TProtocolUtil.skip(iprot, field.type);
                return null;
            }
            case BYTE_VAL: {
                if (field.type == TColumnValue.BYTE_VAL_FIELD_DESC.type) {
                    final TByteValue byteVal = new TByteValue();
                    byteVal.read(iprot);
                    return byteVal;
                }
                TProtocolUtil.skip(iprot, field.type);
                return null;
            }
            case I16_VAL: {
                if (field.type == TColumnValue.I16_VAL_FIELD_DESC.type) {
                    final TI16Value i16Val = new TI16Value();
                    i16Val.read(iprot);
                    return i16Val;
                }
                TProtocolUtil.skip(iprot, field.type);
                return null;
            }
            case I32_VAL: {
                if (field.type == TColumnValue.I32_VAL_FIELD_DESC.type) {
                    final TI32Value i32Val = new TI32Value();
                    i32Val.read(iprot);
                    return i32Val;
                }
                TProtocolUtil.skip(iprot, field.type);
                return null;
            }
            case I64_VAL: {
                if (field.type == TColumnValue.I64_VAL_FIELD_DESC.type) {
                    final TI64Value i64Val = new TI64Value();
                    i64Val.read(iprot);
                    return i64Val;
                }
                TProtocolUtil.skip(iprot, field.type);
                return null;
            }
            case DOUBLE_VAL: {
                if (field.type == TColumnValue.DOUBLE_VAL_FIELD_DESC.type) {
                    final TDoubleValue doubleVal = new TDoubleValue();
                    doubleVal.read(iprot);
                    return doubleVal;
                }
                TProtocolUtil.skip(iprot, field.type);
                return null;
            }
            case STRING_VAL: {
                if (field.type == TColumnValue.STRING_VAL_FIELD_DESC.type) {
                    final TStringValue stringVal = new TStringValue();
                    stringVal.read(iprot);
                    return stringVal;
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
            case BOOL_VAL: {
                final TBoolValue boolVal = (TBoolValue)this.value_;
                boolVal.write(oprot);
            }
            case BYTE_VAL: {
                final TByteValue byteVal = (TByteValue)this.value_;
                byteVal.write(oprot);
            }
            case I16_VAL: {
                final TI16Value i16Val = (TI16Value)this.value_;
                i16Val.write(oprot);
            }
            case I32_VAL: {
                final TI32Value i32Val = (TI32Value)this.value_;
                i32Val.write(oprot);
            }
            case I64_VAL: {
                final TI64Value i64Val = (TI64Value)this.value_;
                i64Val.write(oprot);
            }
            case DOUBLE_VAL: {
                final TDoubleValue doubleVal = (TDoubleValue)this.value_;
                doubleVal.write(oprot);
            }
            case STRING_VAL: {
                final TStringValue stringVal = (TStringValue)this.value_;
                stringVal.write(oprot);
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
            case BOOL_VAL: {
                final TBoolValue boolVal = new TBoolValue();
                boolVal.read(iprot);
                return boolVal;
            }
            case BYTE_VAL: {
                final TByteValue byteVal = new TByteValue();
                byteVal.read(iprot);
                return byteVal;
            }
            case I16_VAL: {
                final TI16Value i16Val = new TI16Value();
                i16Val.read(iprot);
                return i16Val;
            }
            case I32_VAL: {
                final TI32Value i32Val = new TI32Value();
                i32Val.read(iprot);
                return i32Val;
            }
            case I64_VAL: {
                final TI64Value i64Val = new TI64Value();
                i64Val.read(iprot);
                return i64Val;
            }
            case DOUBLE_VAL: {
                final TDoubleValue doubleVal = new TDoubleValue();
                doubleVal.read(iprot);
                return doubleVal;
            }
            case STRING_VAL: {
                final TStringValue stringVal = new TStringValue();
                stringVal.read(iprot);
                return stringVal;
            }
            default: {
                throw new IllegalStateException("setField wasn't null, but didn't match any of the case statements!");
            }
        }
    }
    
    @Override
    protected void tupleSchemeWriteValue(final TProtocol oprot) throws TException {
        switch ((_Fields)this.setField_) {
            case BOOL_VAL: {
                final TBoolValue boolVal = (TBoolValue)this.value_;
                boolVal.write(oprot);
            }
            case BYTE_VAL: {
                final TByteValue byteVal = (TByteValue)this.value_;
                byteVal.write(oprot);
            }
            case I16_VAL: {
                final TI16Value i16Val = (TI16Value)this.value_;
                i16Val.write(oprot);
            }
            case I32_VAL: {
                final TI32Value i32Val = (TI32Value)this.value_;
                i32Val.write(oprot);
            }
            case I64_VAL: {
                final TI64Value i64Val = (TI64Value)this.value_;
                i64Val.write(oprot);
            }
            case DOUBLE_VAL: {
                final TDoubleValue doubleVal = (TDoubleValue)this.value_;
                doubleVal.write(oprot);
            }
            case STRING_VAL: {
                final TStringValue stringVal = (TStringValue)this.value_;
                stringVal.write(oprot);
            }
            default: {
                throw new IllegalStateException("Cannot write union with unknown field " + this.setField_);
            }
        }
    }
    
    @Override
    protected TField getFieldDesc(final _Fields setField) {
        switch (setField) {
            case BOOL_VAL: {
                return TColumnValue.BOOL_VAL_FIELD_DESC;
            }
            case BYTE_VAL: {
                return TColumnValue.BYTE_VAL_FIELD_DESC;
            }
            case I16_VAL: {
                return TColumnValue.I16_VAL_FIELD_DESC;
            }
            case I32_VAL: {
                return TColumnValue.I32_VAL_FIELD_DESC;
            }
            case I64_VAL: {
                return TColumnValue.I64_VAL_FIELD_DESC;
            }
            case DOUBLE_VAL: {
                return TColumnValue.DOUBLE_VAL_FIELD_DESC;
            }
            case STRING_VAL: {
                return TColumnValue.STRING_VAL_FIELD_DESC;
            }
            default: {
                throw new IllegalArgumentException("Unknown field id " + setField);
            }
        }
    }
    
    @Override
    protected TStruct getStructDesc() {
        return TColumnValue.STRUCT_DESC;
    }
    
    @Override
    protected _Fields enumForId(final short id) {
        return _Fields.findByThriftIdOrThrow(id);
    }
    
    @Override
    public _Fields fieldForId(final int fieldId) {
        return _Fields.findByThriftId(fieldId);
    }
    
    public TBoolValue getBoolVal() {
        if (this.getSetField() == _Fields.BOOL_VAL) {
            return (TBoolValue)this.getFieldValue();
        }
        throw new RuntimeException("Cannot get field 'boolVal' because union is currently set to " + this.getFieldDesc(((TUnion<T, _Fields>)this).getSetField()).name);
    }
    
    public void setBoolVal(final TBoolValue value) {
        if (value == null) {
            throw new NullPointerException();
        }
        this.setField_ = (F)_Fields.BOOL_VAL;
        this.value_ = value;
    }
    
    public TByteValue getByteVal() {
        if (this.getSetField() == _Fields.BYTE_VAL) {
            return (TByteValue)this.getFieldValue();
        }
        throw new RuntimeException("Cannot get field 'byteVal' because union is currently set to " + this.getFieldDesc(((TUnion<T, _Fields>)this).getSetField()).name);
    }
    
    public void setByteVal(final TByteValue value) {
        if (value == null) {
            throw new NullPointerException();
        }
        this.setField_ = (F)_Fields.BYTE_VAL;
        this.value_ = value;
    }
    
    public TI16Value getI16Val() {
        if (this.getSetField() == _Fields.I16_VAL) {
            return (TI16Value)this.getFieldValue();
        }
        throw new RuntimeException("Cannot get field 'i16Val' because union is currently set to " + this.getFieldDesc(((TUnion<T, _Fields>)this).getSetField()).name);
    }
    
    public void setI16Val(final TI16Value value) {
        if (value == null) {
            throw new NullPointerException();
        }
        this.setField_ = (F)_Fields.I16_VAL;
        this.value_ = value;
    }
    
    public TI32Value getI32Val() {
        if (this.getSetField() == _Fields.I32_VAL) {
            return (TI32Value)this.getFieldValue();
        }
        throw new RuntimeException("Cannot get field 'i32Val' because union is currently set to " + this.getFieldDesc(((TUnion<T, _Fields>)this).getSetField()).name);
    }
    
    public void setI32Val(final TI32Value value) {
        if (value == null) {
            throw new NullPointerException();
        }
        this.setField_ = (F)_Fields.I32_VAL;
        this.value_ = value;
    }
    
    public TI64Value getI64Val() {
        if (this.getSetField() == _Fields.I64_VAL) {
            return (TI64Value)this.getFieldValue();
        }
        throw new RuntimeException("Cannot get field 'i64Val' because union is currently set to " + this.getFieldDesc(((TUnion<T, _Fields>)this).getSetField()).name);
    }
    
    public void setI64Val(final TI64Value value) {
        if (value == null) {
            throw new NullPointerException();
        }
        this.setField_ = (F)_Fields.I64_VAL;
        this.value_ = value;
    }
    
    public TDoubleValue getDoubleVal() {
        if (this.getSetField() == _Fields.DOUBLE_VAL) {
            return (TDoubleValue)this.getFieldValue();
        }
        throw new RuntimeException("Cannot get field 'doubleVal' because union is currently set to " + this.getFieldDesc(((TUnion<T, _Fields>)this).getSetField()).name);
    }
    
    public void setDoubleVal(final TDoubleValue value) {
        if (value == null) {
            throw new NullPointerException();
        }
        this.setField_ = (F)_Fields.DOUBLE_VAL;
        this.value_ = value;
    }
    
    public TStringValue getStringVal() {
        if (this.getSetField() == _Fields.STRING_VAL) {
            return (TStringValue)this.getFieldValue();
        }
        throw new RuntimeException("Cannot get field 'stringVal' because union is currently set to " + this.getFieldDesc(((TUnion<T, _Fields>)this).getSetField()).name);
    }
    
    public void setStringVal(final TStringValue value) {
        if (value == null) {
            throw new NullPointerException();
        }
        this.setField_ = (F)_Fields.STRING_VAL;
        this.value_ = value;
    }
    
    public boolean isSetBoolVal() {
        return this.setField_ == _Fields.BOOL_VAL;
    }
    
    public boolean isSetByteVal() {
        return this.setField_ == _Fields.BYTE_VAL;
    }
    
    public boolean isSetI16Val() {
        return this.setField_ == _Fields.I16_VAL;
    }
    
    public boolean isSetI32Val() {
        return this.setField_ == _Fields.I32_VAL;
    }
    
    public boolean isSetI64Val() {
        return this.setField_ == _Fields.I64_VAL;
    }
    
    public boolean isSetDoubleVal() {
        return this.setField_ == _Fields.DOUBLE_VAL;
    }
    
    public boolean isSetStringVal() {
        return this.setField_ == _Fields.STRING_VAL;
    }
    
    @Override
    public boolean equals(final Object other) {
        return other instanceof TColumnValue && this.equals((TColumnValue)other);
    }
    
    public boolean equals(final TColumnValue other) {
        return other != null && this.getSetField() == other.getSetField() && this.getFieldValue().equals(other.getFieldValue());
    }
    
    @Override
    public int compareTo(final TColumnValue other) {
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
        STRUCT_DESC = new TStruct("TColumnValue");
        BOOL_VAL_FIELD_DESC = new TField("boolVal", (byte)12, (short)1);
        BYTE_VAL_FIELD_DESC = new TField("byteVal", (byte)12, (short)2);
        I16_VAL_FIELD_DESC = new TField("i16Val", (byte)12, (short)3);
        I32_VAL_FIELD_DESC = new TField("i32Val", (byte)12, (short)4);
        I64_VAL_FIELD_DESC = new TField("i64Val", (byte)12, (short)5);
        DOUBLE_VAL_FIELD_DESC = new TField("doubleVal", (byte)12, (short)6);
        STRING_VAL_FIELD_DESC = new TField("stringVal", (byte)12, (short)7);
        final Map<_Fields, FieldMetaData> tmpMap = new EnumMap<_Fields, FieldMetaData>(_Fields.class);
        tmpMap.put(_Fields.BOOL_VAL, new FieldMetaData("boolVal", (byte)3, new StructMetaData((byte)12, TBoolValue.class)));
        tmpMap.put(_Fields.BYTE_VAL, new FieldMetaData("byteVal", (byte)3, new StructMetaData((byte)12, TByteValue.class)));
        tmpMap.put(_Fields.I16_VAL, new FieldMetaData("i16Val", (byte)3, new StructMetaData((byte)12, TI16Value.class)));
        tmpMap.put(_Fields.I32_VAL, new FieldMetaData("i32Val", (byte)3, new StructMetaData((byte)12, TI32Value.class)));
        tmpMap.put(_Fields.I64_VAL, new FieldMetaData("i64Val", (byte)3, new StructMetaData((byte)12, TI64Value.class)));
        tmpMap.put(_Fields.DOUBLE_VAL, new FieldMetaData("doubleVal", (byte)3, new StructMetaData((byte)12, TDoubleValue.class)));
        tmpMap.put(_Fields.STRING_VAL, new FieldMetaData("stringVal", (byte)3, new StructMetaData((byte)12, TStringValue.class)));
        FieldMetaData.addStructMetaDataMap(TColumnValue.class, metaDataMap = Collections.unmodifiableMap((Map<? extends _Fields, ? extends FieldMetaData>)tmpMap));
    }
    
    public enum _Fields implements TFieldIdEnum
    {
        BOOL_VAL((short)1, "boolVal"), 
        BYTE_VAL((short)2, "byteVal"), 
        I16_VAL((short)3, "i16Val"), 
        I32_VAL((short)4, "i32Val"), 
        I64_VAL((short)5, "i64Val"), 
        DOUBLE_VAL((short)6, "doubleVal"), 
        STRING_VAL((short)7, "stringVal");
        
        private static final Map<String, _Fields> byName;
        private final short _thriftId;
        private final String _fieldName;
        
        public static _Fields findByThriftId(final int fieldId) {
            switch (fieldId) {
                case 1: {
                    return _Fields.BOOL_VAL;
                }
                case 2: {
                    return _Fields.BYTE_VAL;
                }
                case 3: {
                    return _Fields.I16_VAL;
                }
                case 4: {
                    return _Fields.I32_VAL;
                }
                case 5: {
                    return _Fields.I64_VAL;
                }
                case 6: {
                    return _Fields.DOUBLE_VAL;
                }
                case 7: {
                    return _Fields.STRING_VAL;
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
