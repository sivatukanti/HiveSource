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

public class TGetInfoValue extends TUnion<TGetInfoValue, _Fields>
{
    private static final TStruct STRUCT_DESC;
    private static final TField STRING_VALUE_FIELD_DESC;
    private static final TField SMALL_INT_VALUE_FIELD_DESC;
    private static final TField INTEGER_BITMASK_FIELD_DESC;
    private static final TField INTEGER_FLAG_FIELD_DESC;
    private static final TField BINARY_VALUE_FIELD_DESC;
    private static final TField LEN_VALUE_FIELD_DESC;
    public static final Map<_Fields, FieldMetaData> metaDataMap;
    
    public TGetInfoValue() {
    }
    
    public TGetInfoValue(final _Fields setField, final Object value) {
        super(setField, value);
    }
    
    public TGetInfoValue(final TGetInfoValue other) {
        super(other);
    }
    
    @Override
    public TGetInfoValue deepCopy() {
        return new TGetInfoValue(this);
    }
    
    public static TGetInfoValue stringValue(final String value) {
        final TGetInfoValue x = new TGetInfoValue();
        x.setStringValue(value);
        return x;
    }
    
    public static TGetInfoValue smallIntValue(final short value) {
        final TGetInfoValue x = new TGetInfoValue();
        x.setSmallIntValue(value);
        return x;
    }
    
    public static TGetInfoValue integerBitmask(final int value) {
        final TGetInfoValue x = new TGetInfoValue();
        x.setIntegerBitmask(value);
        return x;
    }
    
    public static TGetInfoValue integerFlag(final int value) {
        final TGetInfoValue x = new TGetInfoValue();
        x.setIntegerFlag(value);
        return x;
    }
    
    public static TGetInfoValue binaryValue(final int value) {
        final TGetInfoValue x = new TGetInfoValue();
        x.setBinaryValue(value);
        return x;
    }
    
    public static TGetInfoValue lenValue(final long value) {
        final TGetInfoValue x = new TGetInfoValue();
        x.setLenValue(value);
        return x;
    }
    
    @Override
    protected void checkType(final _Fields setField, final Object value) throws ClassCastException {
        switch (setField) {
            case STRING_VALUE: {
                if (value instanceof String) {
                    break;
                }
                throw new ClassCastException("Was expecting value of type String for field 'stringValue', but got " + value.getClass().getSimpleName());
            }
            case SMALL_INT_VALUE: {
                if (value instanceof Short) {
                    break;
                }
                throw new ClassCastException("Was expecting value of type Short for field 'smallIntValue', but got " + value.getClass().getSimpleName());
            }
            case INTEGER_BITMASK: {
                if (value instanceof Integer) {
                    break;
                }
                throw new ClassCastException("Was expecting value of type Integer for field 'integerBitmask', but got " + value.getClass().getSimpleName());
            }
            case INTEGER_FLAG: {
                if (value instanceof Integer) {
                    break;
                }
                throw new ClassCastException("Was expecting value of type Integer for field 'integerFlag', but got " + value.getClass().getSimpleName());
            }
            case BINARY_VALUE: {
                if (value instanceof Integer) {
                    break;
                }
                throw new ClassCastException("Was expecting value of type Integer for field 'binaryValue', but got " + value.getClass().getSimpleName());
            }
            case LEN_VALUE: {
                if (value instanceof Long) {
                    break;
                }
                throw new ClassCastException("Was expecting value of type Long for field 'lenValue', but got " + value.getClass().getSimpleName());
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
            case STRING_VALUE: {
                if (field.type == TGetInfoValue.STRING_VALUE_FIELD_DESC.type) {
                    final String stringValue = iprot.readString();
                    return stringValue;
                }
                TProtocolUtil.skip(iprot, field.type);
                return null;
            }
            case SMALL_INT_VALUE: {
                if (field.type == TGetInfoValue.SMALL_INT_VALUE_FIELD_DESC.type) {
                    final Short smallIntValue = iprot.readI16();
                    return smallIntValue;
                }
                TProtocolUtil.skip(iprot, field.type);
                return null;
            }
            case INTEGER_BITMASK: {
                if (field.type == TGetInfoValue.INTEGER_BITMASK_FIELD_DESC.type) {
                    final Integer integerBitmask = iprot.readI32();
                    return integerBitmask;
                }
                TProtocolUtil.skip(iprot, field.type);
                return null;
            }
            case INTEGER_FLAG: {
                if (field.type == TGetInfoValue.INTEGER_FLAG_FIELD_DESC.type) {
                    final Integer integerFlag = iprot.readI32();
                    return integerFlag;
                }
                TProtocolUtil.skip(iprot, field.type);
                return null;
            }
            case BINARY_VALUE: {
                if (field.type == TGetInfoValue.BINARY_VALUE_FIELD_DESC.type) {
                    final Integer binaryValue = iprot.readI32();
                    return binaryValue;
                }
                TProtocolUtil.skip(iprot, field.type);
                return null;
            }
            case LEN_VALUE: {
                if (field.type == TGetInfoValue.LEN_VALUE_FIELD_DESC.type) {
                    final Long lenValue = iprot.readI64();
                    return lenValue;
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
            case STRING_VALUE: {
                final String stringValue = (String)this.value_;
                oprot.writeString(stringValue);
            }
            case SMALL_INT_VALUE: {
                final Short smallIntValue = (Short)this.value_;
                oprot.writeI16(smallIntValue);
            }
            case INTEGER_BITMASK: {
                final Integer integerBitmask = (Integer)this.value_;
                oprot.writeI32(integerBitmask);
            }
            case INTEGER_FLAG: {
                final Integer integerFlag = (Integer)this.value_;
                oprot.writeI32(integerFlag);
            }
            case BINARY_VALUE: {
                final Integer binaryValue = (Integer)this.value_;
                oprot.writeI32(binaryValue);
            }
            case LEN_VALUE: {
                final Long lenValue = (Long)this.value_;
                oprot.writeI64(lenValue);
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
            case STRING_VALUE: {
                final String stringValue = iprot.readString();
                return stringValue;
            }
            case SMALL_INT_VALUE: {
                final Short smallIntValue = iprot.readI16();
                return smallIntValue;
            }
            case INTEGER_BITMASK: {
                final Integer integerBitmask = iprot.readI32();
                return integerBitmask;
            }
            case INTEGER_FLAG: {
                final Integer integerFlag = iprot.readI32();
                return integerFlag;
            }
            case BINARY_VALUE: {
                final Integer binaryValue = iprot.readI32();
                return binaryValue;
            }
            case LEN_VALUE: {
                final Long lenValue = iprot.readI64();
                return lenValue;
            }
            default: {
                throw new IllegalStateException("setField wasn't null, but didn't match any of the case statements!");
            }
        }
    }
    
    @Override
    protected void tupleSchemeWriteValue(final TProtocol oprot) throws TException {
        switch ((_Fields)this.setField_) {
            case STRING_VALUE: {
                final String stringValue = (String)this.value_;
                oprot.writeString(stringValue);
            }
            case SMALL_INT_VALUE: {
                final Short smallIntValue = (Short)this.value_;
                oprot.writeI16(smallIntValue);
            }
            case INTEGER_BITMASK: {
                final Integer integerBitmask = (Integer)this.value_;
                oprot.writeI32(integerBitmask);
            }
            case INTEGER_FLAG: {
                final Integer integerFlag = (Integer)this.value_;
                oprot.writeI32(integerFlag);
            }
            case BINARY_VALUE: {
                final Integer binaryValue = (Integer)this.value_;
                oprot.writeI32(binaryValue);
            }
            case LEN_VALUE: {
                final Long lenValue = (Long)this.value_;
                oprot.writeI64(lenValue);
            }
            default: {
                throw new IllegalStateException("Cannot write union with unknown field " + this.setField_);
            }
        }
    }
    
    @Override
    protected TField getFieldDesc(final _Fields setField) {
        switch (setField) {
            case STRING_VALUE: {
                return TGetInfoValue.STRING_VALUE_FIELD_DESC;
            }
            case SMALL_INT_VALUE: {
                return TGetInfoValue.SMALL_INT_VALUE_FIELD_DESC;
            }
            case INTEGER_BITMASK: {
                return TGetInfoValue.INTEGER_BITMASK_FIELD_DESC;
            }
            case INTEGER_FLAG: {
                return TGetInfoValue.INTEGER_FLAG_FIELD_DESC;
            }
            case BINARY_VALUE: {
                return TGetInfoValue.BINARY_VALUE_FIELD_DESC;
            }
            case LEN_VALUE: {
                return TGetInfoValue.LEN_VALUE_FIELD_DESC;
            }
            default: {
                throw new IllegalArgumentException("Unknown field id " + setField);
            }
        }
    }
    
    @Override
    protected TStruct getStructDesc() {
        return TGetInfoValue.STRUCT_DESC;
    }
    
    @Override
    protected _Fields enumForId(final short id) {
        return _Fields.findByThriftIdOrThrow(id);
    }
    
    @Override
    public _Fields fieldForId(final int fieldId) {
        return _Fields.findByThriftId(fieldId);
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
    
    public short getSmallIntValue() {
        if (this.getSetField() == _Fields.SMALL_INT_VALUE) {
            return (short)this.getFieldValue();
        }
        throw new RuntimeException("Cannot get field 'smallIntValue' because union is currently set to " + this.getFieldDesc(((TUnion<T, _Fields>)this).getSetField()).name);
    }
    
    public void setSmallIntValue(final short value) {
        this.setField_ = (F)_Fields.SMALL_INT_VALUE;
        this.value_ = value;
    }
    
    public int getIntegerBitmask() {
        if (this.getSetField() == _Fields.INTEGER_BITMASK) {
            return (int)this.getFieldValue();
        }
        throw new RuntimeException("Cannot get field 'integerBitmask' because union is currently set to " + this.getFieldDesc(((TUnion<T, _Fields>)this).getSetField()).name);
    }
    
    public void setIntegerBitmask(final int value) {
        this.setField_ = (F)_Fields.INTEGER_BITMASK;
        this.value_ = value;
    }
    
    public int getIntegerFlag() {
        if (this.getSetField() == _Fields.INTEGER_FLAG) {
            return (int)this.getFieldValue();
        }
        throw new RuntimeException("Cannot get field 'integerFlag' because union is currently set to " + this.getFieldDesc(((TUnion<T, _Fields>)this).getSetField()).name);
    }
    
    public void setIntegerFlag(final int value) {
        this.setField_ = (F)_Fields.INTEGER_FLAG;
        this.value_ = value;
    }
    
    public int getBinaryValue() {
        if (this.getSetField() == _Fields.BINARY_VALUE) {
            return (int)this.getFieldValue();
        }
        throw new RuntimeException("Cannot get field 'binaryValue' because union is currently set to " + this.getFieldDesc(((TUnion<T, _Fields>)this).getSetField()).name);
    }
    
    public void setBinaryValue(final int value) {
        this.setField_ = (F)_Fields.BINARY_VALUE;
        this.value_ = value;
    }
    
    public long getLenValue() {
        if (this.getSetField() == _Fields.LEN_VALUE) {
            return (long)this.getFieldValue();
        }
        throw new RuntimeException("Cannot get field 'lenValue' because union is currently set to " + this.getFieldDesc(((TUnion<T, _Fields>)this).getSetField()).name);
    }
    
    public void setLenValue(final long value) {
        this.setField_ = (F)_Fields.LEN_VALUE;
        this.value_ = value;
    }
    
    public boolean isSetStringValue() {
        return this.setField_ == _Fields.STRING_VALUE;
    }
    
    public boolean isSetSmallIntValue() {
        return this.setField_ == _Fields.SMALL_INT_VALUE;
    }
    
    public boolean isSetIntegerBitmask() {
        return this.setField_ == _Fields.INTEGER_BITMASK;
    }
    
    public boolean isSetIntegerFlag() {
        return this.setField_ == _Fields.INTEGER_FLAG;
    }
    
    public boolean isSetBinaryValue() {
        return this.setField_ == _Fields.BINARY_VALUE;
    }
    
    public boolean isSetLenValue() {
        return this.setField_ == _Fields.LEN_VALUE;
    }
    
    @Override
    public boolean equals(final Object other) {
        return other instanceof TGetInfoValue && this.equals((TGetInfoValue)other);
    }
    
    public boolean equals(final TGetInfoValue other) {
        return other != null && this.getSetField() == other.getSetField() && this.getFieldValue().equals(other.getFieldValue());
    }
    
    @Override
    public int compareTo(final TGetInfoValue other) {
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
        STRUCT_DESC = new TStruct("TGetInfoValue");
        STRING_VALUE_FIELD_DESC = new TField("stringValue", (byte)11, (short)1);
        SMALL_INT_VALUE_FIELD_DESC = new TField("smallIntValue", (byte)6, (short)2);
        INTEGER_BITMASK_FIELD_DESC = new TField("integerBitmask", (byte)8, (short)3);
        INTEGER_FLAG_FIELD_DESC = new TField("integerFlag", (byte)8, (short)4);
        BINARY_VALUE_FIELD_DESC = new TField("binaryValue", (byte)8, (short)5);
        LEN_VALUE_FIELD_DESC = new TField("lenValue", (byte)10, (short)6);
        final Map<_Fields, FieldMetaData> tmpMap = new EnumMap<_Fields, FieldMetaData>(_Fields.class);
        tmpMap.put(_Fields.STRING_VALUE, new FieldMetaData("stringValue", (byte)3, new FieldValueMetaData((byte)11)));
        tmpMap.put(_Fields.SMALL_INT_VALUE, new FieldMetaData("smallIntValue", (byte)3, new FieldValueMetaData((byte)6)));
        tmpMap.put(_Fields.INTEGER_BITMASK, new FieldMetaData("integerBitmask", (byte)3, new FieldValueMetaData((byte)8)));
        tmpMap.put(_Fields.INTEGER_FLAG, new FieldMetaData("integerFlag", (byte)3, new FieldValueMetaData((byte)8)));
        tmpMap.put(_Fields.BINARY_VALUE, new FieldMetaData("binaryValue", (byte)3, new FieldValueMetaData((byte)8)));
        tmpMap.put(_Fields.LEN_VALUE, new FieldMetaData("lenValue", (byte)3, new FieldValueMetaData((byte)10)));
        FieldMetaData.addStructMetaDataMap(TGetInfoValue.class, metaDataMap = Collections.unmodifiableMap((Map<? extends _Fields, ? extends FieldMetaData>)tmpMap));
    }
    
    public enum _Fields implements TFieldIdEnum
    {
        STRING_VALUE((short)1, "stringValue"), 
        SMALL_INT_VALUE((short)2, "smallIntValue"), 
        INTEGER_BITMASK((short)3, "integerBitmask"), 
        INTEGER_FLAG((short)4, "integerFlag"), 
        BINARY_VALUE((short)5, "binaryValue"), 
        LEN_VALUE((short)6, "lenValue");
        
        private static final Map<String, _Fields> byName;
        private final short _thriftId;
        private final String _fieldName;
        
        public static _Fields findByThriftId(final int fieldId) {
            switch (fieldId) {
                case 1: {
                    return _Fields.STRING_VALUE;
                }
                case 2: {
                    return _Fields.SMALL_INT_VALUE;
                }
                case 3: {
                    return _Fields.INTEGER_BITMASK;
                }
                case 4: {
                    return _Fields.INTEGER_FLAG;
                }
                case 5: {
                    return _Fields.BINARY_VALUE;
                }
                case 6: {
                    return _Fields.LEN_VALUE;
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
