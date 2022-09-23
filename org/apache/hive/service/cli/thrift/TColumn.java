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

public class TColumn extends TUnion<TColumn, _Fields>
{
    private static final TStruct STRUCT_DESC;
    private static final TField BOOL_VAL_FIELD_DESC;
    private static final TField BYTE_VAL_FIELD_DESC;
    private static final TField I16_VAL_FIELD_DESC;
    private static final TField I32_VAL_FIELD_DESC;
    private static final TField I64_VAL_FIELD_DESC;
    private static final TField DOUBLE_VAL_FIELD_DESC;
    private static final TField STRING_VAL_FIELD_DESC;
    private static final TField BINARY_VAL_FIELD_DESC;
    public static final Map<_Fields, FieldMetaData> metaDataMap;
    
    public TColumn() {
    }
    
    public TColumn(final _Fields setField, final Object value) {
        super(setField, value);
    }
    
    public TColumn(final TColumn other) {
        super(other);
    }
    
    @Override
    public TColumn deepCopy() {
        return new TColumn(this);
    }
    
    public static TColumn boolVal(final TBoolColumn value) {
        final TColumn x = new TColumn();
        x.setBoolVal(value);
        return x;
    }
    
    public static TColumn byteVal(final TByteColumn value) {
        final TColumn x = new TColumn();
        x.setByteVal(value);
        return x;
    }
    
    public static TColumn i16Val(final TI16Column value) {
        final TColumn x = new TColumn();
        x.setI16Val(value);
        return x;
    }
    
    public static TColumn i32Val(final TI32Column value) {
        final TColumn x = new TColumn();
        x.setI32Val(value);
        return x;
    }
    
    public static TColumn i64Val(final TI64Column value) {
        final TColumn x = new TColumn();
        x.setI64Val(value);
        return x;
    }
    
    public static TColumn doubleVal(final TDoubleColumn value) {
        final TColumn x = new TColumn();
        x.setDoubleVal(value);
        return x;
    }
    
    public static TColumn stringVal(final TStringColumn value) {
        final TColumn x = new TColumn();
        x.setStringVal(value);
        return x;
    }
    
    public static TColumn binaryVal(final TBinaryColumn value) {
        final TColumn x = new TColumn();
        x.setBinaryVal(value);
        return x;
    }
    
    @Override
    protected void checkType(final _Fields setField, final Object value) throws ClassCastException {
        switch (setField) {
            case BOOL_VAL: {
                if (value instanceof TBoolColumn) {
                    break;
                }
                throw new ClassCastException("Was expecting value of type TBoolColumn for field 'boolVal', but got " + value.getClass().getSimpleName());
            }
            case BYTE_VAL: {
                if (value instanceof TByteColumn) {
                    break;
                }
                throw new ClassCastException("Was expecting value of type TByteColumn for field 'byteVal', but got " + value.getClass().getSimpleName());
            }
            case I16_VAL: {
                if (value instanceof TI16Column) {
                    break;
                }
                throw new ClassCastException("Was expecting value of type TI16Column for field 'i16Val', but got " + value.getClass().getSimpleName());
            }
            case I32_VAL: {
                if (value instanceof TI32Column) {
                    break;
                }
                throw new ClassCastException("Was expecting value of type TI32Column for field 'i32Val', but got " + value.getClass().getSimpleName());
            }
            case I64_VAL: {
                if (value instanceof TI64Column) {
                    break;
                }
                throw new ClassCastException("Was expecting value of type TI64Column for field 'i64Val', but got " + value.getClass().getSimpleName());
            }
            case DOUBLE_VAL: {
                if (value instanceof TDoubleColumn) {
                    break;
                }
                throw new ClassCastException("Was expecting value of type TDoubleColumn for field 'doubleVal', but got " + value.getClass().getSimpleName());
            }
            case STRING_VAL: {
                if (value instanceof TStringColumn) {
                    break;
                }
                throw new ClassCastException("Was expecting value of type TStringColumn for field 'stringVal', but got " + value.getClass().getSimpleName());
            }
            case BINARY_VAL: {
                if (value instanceof TBinaryColumn) {
                    break;
                }
                throw new ClassCastException("Was expecting value of type TBinaryColumn for field 'binaryVal', but got " + value.getClass().getSimpleName());
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
                if (field.type == TColumn.BOOL_VAL_FIELD_DESC.type) {
                    final TBoolColumn boolVal = new TBoolColumn();
                    boolVal.read(iprot);
                    return boolVal;
                }
                TProtocolUtil.skip(iprot, field.type);
                return null;
            }
            case BYTE_VAL: {
                if (field.type == TColumn.BYTE_VAL_FIELD_DESC.type) {
                    final TByteColumn byteVal = new TByteColumn();
                    byteVal.read(iprot);
                    return byteVal;
                }
                TProtocolUtil.skip(iprot, field.type);
                return null;
            }
            case I16_VAL: {
                if (field.type == TColumn.I16_VAL_FIELD_DESC.type) {
                    final TI16Column i16Val = new TI16Column();
                    i16Val.read(iprot);
                    return i16Val;
                }
                TProtocolUtil.skip(iprot, field.type);
                return null;
            }
            case I32_VAL: {
                if (field.type == TColumn.I32_VAL_FIELD_DESC.type) {
                    final TI32Column i32Val = new TI32Column();
                    i32Val.read(iprot);
                    return i32Val;
                }
                TProtocolUtil.skip(iprot, field.type);
                return null;
            }
            case I64_VAL: {
                if (field.type == TColumn.I64_VAL_FIELD_DESC.type) {
                    final TI64Column i64Val = new TI64Column();
                    i64Val.read(iprot);
                    return i64Val;
                }
                TProtocolUtil.skip(iprot, field.type);
                return null;
            }
            case DOUBLE_VAL: {
                if (field.type == TColumn.DOUBLE_VAL_FIELD_DESC.type) {
                    final TDoubleColumn doubleVal = new TDoubleColumn();
                    doubleVal.read(iprot);
                    return doubleVal;
                }
                TProtocolUtil.skip(iprot, field.type);
                return null;
            }
            case STRING_VAL: {
                if (field.type == TColumn.STRING_VAL_FIELD_DESC.type) {
                    final TStringColumn stringVal = new TStringColumn();
                    stringVal.read(iprot);
                    return stringVal;
                }
                TProtocolUtil.skip(iprot, field.type);
                return null;
            }
            case BINARY_VAL: {
                if (field.type == TColumn.BINARY_VAL_FIELD_DESC.type) {
                    final TBinaryColumn binaryVal = new TBinaryColumn();
                    binaryVal.read(iprot);
                    return binaryVal;
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
                final TBoolColumn boolVal = (TBoolColumn)this.value_;
                boolVal.write(oprot);
            }
            case BYTE_VAL: {
                final TByteColumn byteVal = (TByteColumn)this.value_;
                byteVal.write(oprot);
            }
            case I16_VAL: {
                final TI16Column i16Val = (TI16Column)this.value_;
                i16Val.write(oprot);
            }
            case I32_VAL: {
                final TI32Column i32Val = (TI32Column)this.value_;
                i32Val.write(oprot);
            }
            case I64_VAL: {
                final TI64Column i64Val = (TI64Column)this.value_;
                i64Val.write(oprot);
            }
            case DOUBLE_VAL: {
                final TDoubleColumn doubleVal = (TDoubleColumn)this.value_;
                doubleVal.write(oprot);
            }
            case STRING_VAL: {
                final TStringColumn stringVal = (TStringColumn)this.value_;
                stringVal.write(oprot);
            }
            case BINARY_VAL: {
                final TBinaryColumn binaryVal = (TBinaryColumn)this.value_;
                binaryVal.write(oprot);
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
                final TBoolColumn boolVal = new TBoolColumn();
                boolVal.read(iprot);
                return boolVal;
            }
            case BYTE_VAL: {
                final TByteColumn byteVal = new TByteColumn();
                byteVal.read(iprot);
                return byteVal;
            }
            case I16_VAL: {
                final TI16Column i16Val = new TI16Column();
                i16Val.read(iprot);
                return i16Val;
            }
            case I32_VAL: {
                final TI32Column i32Val = new TI32Column();
                i32Val.read(iprot);
                return i32Val;
            }
            case I64_VAL: {
                final TI64Column i64Val = new TI64Column();
                i64Val.read(iprot);
                return i64Val;
            }
            case DOUBLE_VAL: {
                final TDoubleColumn doubleVal = new TDoubleColumn();
                doubleVal.read(iprot);
                return doubleVal;
            }
            case STRING_VAL: {
                final TStringColumn stringVal = new TStringColumn();
                stringVal.read(iprot);
                return stringVal;
            }
            case BINARY_VAL: {
                final TBinaryColumn binaryVal = new TBinaryColumn();
                binaryVal.read(iprot);
                return binaryVal;
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
                final TBoolColumn boolVal = (TBoolColumn)this.value_;
                boolVal.write(oprot);
            }
            case BYTE_VAL: {
                final TByteColumn byteVal = (TByteColumn)this.value_;
                byteVal.write(oprot);
            }
            case I16_VAL: {
                final TI16Column i16Val = (TI16Column)this.value_;
                i16Val.write(oprot);
            }
            case I32_VAL: {
                final TI32Column i32Val = (TI32Column)this.value_;
                i32Val.write(oprot);
            }
            case I64_VAL: {
                final TI64Column i64Val = (TI64Column)this.value_;
                i64Val.write(oprot);
            }
            case DOUBLE_VAL: {
                final TDoubleColumn doubleVal = (TDoubleColumn)this.value_;
                doubleVal.write(oprot);
            }
            case STRING_VAL: {
                final TStringColumn stringVal = (TStringColumn)this.value_;
                stringVal.write(oprot);
            }
            case BINARY_VAL: {
                final TBinaryColumn binaryVal = (TBinaryColumn)this.value_;
                binaryVal.write(oprot);
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
                return TColumn.BOOL_VAL_FIELD_DESC;
            }
            case BYTE_VAL: {
                return TColumn.BYTE_VAL_FIELD_DESC;
            }
            case I16_VAL: {
                return TColumn.I16_VAL_FIELD_DESC;
            }
            case I32_VAL: {
                return TColumn.I32_VAL_FIELD_DESC;
            }
            case I64_VAL: {
                return TColumn.I64_VAL_FIELD_DESC;
            }
            case DOUBLE_VAL: {
                return TColumn.DOUBLE_VAL_FIELD_DESC;
            }
            case STRING_VAL: {
                return TColumn.STRING_VAL_FIELD_DESC;
            }
            case BINARY_VAL: {
                return TColumn.BINARY_VAL_FIELD_DESC;
            }
            default: {
                throw new IllegalArgumentException("Unknown field id " + setField);
            }
        }
    }
    
    @Override
    protected TStruct getStructDesc() {
        return TColumn.STRUCT_DESC;
    }
    
    @Override
    protected _Fields enumForId(final short id) {
        return _Fields.findByThriftIdOrThrow(id);
    }
    
    @Override
    public _Fields fieldForId(final int fieldId) {
        return _Fields.findByThriftId(fieldId);
    }
    
    public TBoolColumn getBoolVal() {
        if (this.getSetField() == _Fields.BOOL_VAL) {
            return (TBoolColumn)this.getFieldValue();
        }
        throw new RuntimeException("Cannot get field 'boolVal' because union is currently set to " + this.getFieldDesc(((TUnion<T, _Fields>)this).getSetField()).name);
    }
    
    public void setBoolVal(final TBoolColumn value) {
        if (value == null) {
            throw new NullPointerException();
        }
        this.setField_ = (F)_Fields.BOOL_VAL;
        this.value_ = value;
    }
    
    public TByteColumn getByteVal() {
        if (this.getSetField() == _Fields.BYTE_VAL) {
            return (TByteColumn)this.getFieldValue();
        }
        throw new RuntimeException("Cannot get field 'byteVal' because union is currently set to " + this.getFieldDesc(((TUnion<T, _Fields>)this).getSetField()).name);
    }
    
    public void setByteVal(final TByteColumn value) {
        if (value == null) {
            throw new NullPointerException();
        }
        this.setField_ = (F)_Fields.BYTE_VAL;
        this.value_ = value;
    }
    
    public TI16Column getI16Val() {
        if (this.getSetField() == _Fields.I16_VAL) {
            return (TI16Column)this.getFieldValue();
        }
        throw new RuntimeException("Cannot get field 'i16Val' because union is currently set to " + this.getFieldDesc(((TUnion<T, _Fields>)this).getSetField()).name);
    }
    
    public void setI16Val(final TI16Column value) {
        if (value == null) {
            throw new NullPointerException();
        }
        this.setField_ = (F)_Fields.I16_VAL;
        this.value_ = value;
    }
    
    public TI32Column getI32Val() {
        if (this.getSetField() == _Fields.I32_VAL) {
            return (TI32Column)this.getFieldValue();
        }
        throw new RuntimeException("Cannot get field 'i32Val' because union is currently set to " + this.getFieldDesc(((TUnion<T, _Fields>)this).getSetField()).name);
    }
    
    public void setI32Val(final TI32Column value) {
        if (value == null) {
            throw new NullPointerException();
        }
        this.setField_ = (F)_Fields.I32_VAL;
        this.value_ = value;
    }
    
    public TI64Column getI64Val() {
        if (this.getSetField() == _Fields.I64_VAL) {
            return (TI64Column)this.getFieldValue();
        }
        throw new RuntimeException("Cannot get field 'i64Val' because union is currently set to " + this.getFieldDesc(((TUnion<T, _Fields>)this).getSetField()).name);
    }
    
    public void setI64Val(final TI64Column value) {
        if (value == null) {
            throw new NullPointerException();
        }
        this.setField_ = (F)_Fields.I64_VAL;
        this.value_ = value;
    }
    
    public TDoubleColumn getDoubleVal() {
        if (this.getSetField() == _Fields.DOUBLE_VAL) {
            return (TDoubleColumn)this.getFieldValue();
        }
        throw new RuntimeException("Cannot get field 'doubleVal' because union is currently set to " + this.getFieldDesc(((TUnion<T, _Fields>)this).getSetField()).name);
    }
    
    public void setDoubleVal(final TDoubleColumn value) {
        if (value == null) {
            throw new NullPointerException();
        }
        this.setField_ = (F)_Fields.DOUBLE_VAL;
        this.value_ = value;
    }
    
    public TStringColumn getStringVal() {
        if (this.getSetField() == _Fields.STRING_VAL) {
            return (TStringColumn)this.getFieldValue();
        }
        throw new RuntimeException("Cannot get field 'stringVal' because union is currently set to " + this.getFieldDesc(((TUnion<T, _Fields>)this).getSetField()).name);
    }
    
    public void setStringVal(final TStringColumn value) {
        if (value == null) {
            throw new NullPointerException();
        }
        this.setField_ = (F)_Fields.STRING_VAL;
        this.value_ = value;
    }
    
    public TBinaryColumn getBinaryVal() {
        if (this.getSetField() == _Fields.BINARY_VAL) {
            return (TBinaryColumn)this.getFieldValue();
        }
        throw new RuntimeException("Cannot get field 'binaryVal' because union is currently set to " + this.getFieldDesc(((TUnion<T, _Fields>)this).getSetField()).name);
    }
    
    public void setBinaryVal(final TBinaryColumn value) {
        if (value == null) {
            throw new NullPointerException();
        }
        this.setField_ = (F)_Fields.BINARY_VAL;
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
    
    public boolean isSetBinaryVal() {
        return this.setField_ == _Fields.BINARY_VAL;
    }
    
    @Override
    public boolean equals(final Object other) {
        return other instanceof TColumn && this.equals((TColumn)other);
    }
    
    public boolean equals(final TColumn other) {
        return other != null && this.getSetField() == other.getSetField() && this.getFieldValue().equals(other.getFieldValue());
    }
    
    @Override
    public int compareTo(final TColumn other) {
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
        STRUCT_DESC = new TStruct("TColumn");
        BOOL_VAL_FIELD_DESC = new TField("boolVal", (byte)12, (short)1);
        BYTE_VAL_FIELD_DESC = new TField("byteVal", (byte)12, (short)2);
        I16_VAL_FIELD_DESC = new TField("i16Val", (byte)12, (short)3);
        I32_VAL_FIELD_DESC = new TField("i32Val", (byte)12, (short)4);
        I64_VAL_FIELD_DESC = new TField("i64Val", (byte)12, (short)5);
        DOUBLE_VAL_FIELD_DESC = new TField("doubleVal", (byte)12, (short)6);
        STRING_VAL_FIELD_DESC = new TField("stringVal", (byte)12, (short)7);
        BINARY_VAL_FIELD_DESC = new TField("binaryVal", (byte)12, (short)8);
        final Map<_Fields, FieldMetaData> tmpMap = new EnumMap<_Fields, FieldMetaData>(_Fields.class);
        tmpMap.put(_Fields.BOOL_VAL, new FieldMetaData("boolVal", (byte)3, new StructMetaData((byte)12, TBoolColumn.class)));
        tmpMap.put(_Fields.BYTE_VAL, new FieldMetaData("byteVal", (byte)3, new StructMetaData((byte)12, TByteColumn.class)));
        tmpMap.put(_Fields.I16_VAL, new FieldMetaData("i16Val", (byte)3, new StructMetaData((byte)12, TI16Column.class)));
        tmpMap.put(_Fields.I32_VAL, new FieldMetaData("i32Val", (byte)3, new StructMetaData((byte)12, TI32Column.class)));
        tmpMap.put(_Fields.I64_VAL, new FieldMetaData("i64Val", (byte)3, new StructMetaData((byte)12, TI64Column.class)));
        tmpMap.put(_Fields.DOUBLE_VAL, new FieldMetaData("doubleVal", (byte)3, new StructMetaData((byte)12, TDoubleColumn.class)));
        tmpMap.put(_Fields.STRING_VAL, new FieldMetaData("stringVal", (byte)3, new StructMetaData((byte)12, TStringColumn.class)));
        tmpMap.put(_Fields.BINARY_VAL, new FieldMetaData("binaryVal", (byte)3, new StructMetaData((byte)12, TBinaryColumn.class)));
        FieldMetaData.addStructMetaDataMap(TColumn.class, metaDataMap = Collections.unmodifiableMap((Map<? extends _Fields, ? extends FieldMetaData>)tmpMap));
    }
    
    public enum _Fields implements TFieldIdEnum
    {
        BOOL_VAL((short)1, "boolVal"), 
        BYTE_VAL((short)2, "byteVal"), 
        I16_VAL((short)3, "i16Val"), 
        I32_VAL((short)4, "i32Val"), 
        I64_VAL((short)5, "i64Val"), 
        DOUBLE_VAL((short)6, "doubleVal"), 
        STRING_VAL((short)7, "stringVal"), 
        BINARY_VAL((short)8, "binaryVal");
        
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
                case 8: {
                    return _Fields.BINARY_VAL;
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
