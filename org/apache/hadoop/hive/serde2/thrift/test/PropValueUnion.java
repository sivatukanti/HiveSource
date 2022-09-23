// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.hive.serde2.thrift.test;

import java.util.EnumSet;
import java.util.Collections;
import org.apache.thrift.meta_data.MapMetaData;
import org.apache.thrift.meta_data.ListMetaData;
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
import java.util.Iterator;
import org.apache.thrift.TException;
import org.apache.thrift.protocol.TMap;
import org.apache.thrift.protocol.TList;
import java.util.HashMap;
import java.util.ArrayList;
import org.apache.thrift.protocol.TProtocolUtil;
import org.apache.thrift.protocol.TProtocol;
import java.util.List;
import org.apache.thrift.meta_data.FieldMetaData;
import java.util.Map;
import org.apache.thrift.protocol.TField;
import org.apache.thrift.protocol.TStruct;
import org.apache.thrift.TUnion;

public class PropValueUnion extends TUnion<PropValueUnion, _Fields>
{
    private static final TStruct STRUCT_DESC;
    private static final TField INT_VALUE_FIELD_DESC;
    private static final TField LONG_VALUE_FIELD_DESC;
    private static final TField STRING_VALUE_FIELD_DESC;
    private static final TField DOUBLE_VALUE_FIELD_DESC;
    private static final TField FLAG_FIELD_DESC;
    private static final TField L_STRING_FIELD_DESC;
    private static final TField UNION_MSTRING_STRING_FIELD_DESC;
    public static final Map<_Fields, FieldMetaData> metaDataMap;
    
    public PropValueUnion() {
    }
    
    public PropValueUnion(final _Fields setField, final Object value) {
        super(setField, value);
    }
    
    public PropValueUnion(final PropValueUnion other) {
        super(other);
    }
    
    @Override
    public PropValueUnion deepCopy() {
        return new PropValueUnion(this);
    }
    
    public static PropValueUnion intValue(final int value) {
        final PropValueUnion x = new PropValueUnion();
        x.setIntValue(value);
        return x;
    }
    
    public static PropValueUnion longValue(final long value) {
        final PropValueUnion x = new PropValueUnion();
        x.setLongValue(value);
        return x;
    }
    
    public static PropValueUnion stringValue(final String value) {
        final PropValueUnion x = new PropValueUnion();
        x.setStringValue(value);
        return x;
    }
    
    public static PropValueUnion doubleValue(final double value) {
        final PropValueUnion x = new PropValueUnion();
        x.setDoubleValue(value);
        return x;
    }
    
    public static PropValueUnion flag(final boolean value) {
        final PropValueUnion x = new PropValueUnion();
        x.setFlag(value);
        return x;
    }
    
    public static PropValueUnion lString(final List<String> value) {
        final PropValueUnion x = new PropValueUnion();
        x.setLString(value);
        return x;
    }
    
    public static PropValueUnion unionMStringString(final Map<String, String> value) {
        final PropValueUnion x = new PropValueUnion();
        x.setUnionMStringString(value);
        return x;
    }
    
    @Override
    protected void checkType(final _Fields setField, final Object value) throws ClassCastException {
        switch (setField) {
            case INT_VALUE: {
                if (value instanceof Integer) {
                    break;
                }
                throw new ClassCastException("Was expecting value of type Integer for field 'intValue', but got " + value.getClass().getSimpleName());
            }
            case LONG_VALUE: {
                if (value instanceof Long) {
                    break;
                }
                throw new ClassCastException("Was expecting value of type Long for field 'longValue', but got " + value.getClass().getSimpleName());
            }
            case STRING_VALUE: {
                if (value instanceof String) {
                    break;
                }
                throw new ClassCastException("Was expecting value of type String for field 'stringValue', but got " + value.getClass().getSimpleName());
            }
            case DOUBLE_VALUE: {
                if (value instanceof Double) {
                    break;
                }
                throw new ClassCastException("Was expecting value of type Double for field 'doubleValue', but got " + value.getClass().getSimpleName());
            }
            case FLAG: {
                if (value instanceof Boolean) {
                    break;
                }
                throw new ClassCastException("Was expecting value of type Boolean for field 'flag', but got " + value.getClass().getSimpleName());
            }
            case L_STRING: {
                if (value instanceof List) {
                    break;
                }
                throw new ClassCastException("Was expecting value of type List<String> for field 'lString', but got " + value.getClass().getSimpleName());
            }
            case UNION_MSTRING_STRING: {
                if (value instanceof Map) {
                    break;
                }
                throw new ClassCastException("Was expecting value of type Map<String,String> for field 'unionMStringString', but got " + value.getClass().getSimpleName());
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
            case INT_VALUE: {
                if (field.type == PropValueUnion.INT_VALUE_FIELD_DESC.type) {
                    final Integer intValue = iprot.readI32();
                    return intValue;
                }
                TProtocolUtil.skip(iprot, field.type);
                return null;
            }
            case LONG_VALUE: {
                if (field.type == PropValueUnion.LONG_VALUE_FIELD_DESC.type) {
                    final Long longValue = iprot.readI64();
                    return longValue;
                }
                TProtocolUtil.skip(iprot, field.type);
                return null;
            }
            case STRING_VALUE: {
                if (field.type == PropValueUnion.STRING_VALUE_FIELD_DESC.type) {
                    final String stringValue = iprot.readString();
                    return stringValue;
                }
                TProtocolUtil.skip(iprot, field.type);
                return null;
            }
            case DOUBLE_VALUE: {
                if (field.type == PropValueUnion.DOUBLE_VALUE_FIELD_DESC.type) {
                    final Double doubleValue = iprot.readDouble();
                    return doubleValue;
                }
                TProtocolUtil.skip(iprot, field.type);
                return null;
            }
            case FLAG: {
                if (field.type == PropValueUnion.FLAG_FIELD_DESC.type) {
                    final Boolean flag = iprot.readBool();
                    return flag;
                }
                TProtocolUtil.skip(iprot, field.type);
                return null;
            }
            case L_STRING: {
                if (field.type == PropValueUnion.L_STRING_FIELD_DESC.type) {
                    final TList _list0 = iprot.readListBegin();
                    final List<String> lString = new ArrayList<String>(_list0.size);
                    for (int _i1 = 0; _i1 < _list0.size; ++_i1) {
                        final String _elem2 = iprot.readString();
                        lString.add(_elem2);
                    }
                    iprot.readListEnd();
                    return lString;
                }
                TProtocolUtil.skip(iprot, field.type);
                return null;
            }
            case UNION_MSTRING_STRING: {
                if (field.type == PropValueUnion.UNION_MSTRING_STRING_FIELD_DESC.type) {
                    final TMap _map3 = iprot.readMapBegin();
                    final Map<String, String> unionMStringString = new HashMap<String, String>(2 * _map3.size);
                    for (int _i2 = 0; _i2 < _map3.size; ++_i2) {
                        final String _key5 = iprot.readString();
                        final String _val6 = iprot.readString();
                        unionMStringString.put(_key5, _val6);
                    }
                    iprot.readMapEnd();
                    return unionMStringString;
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
            case INT_VALUE: {
                final Integer intValue = (Integer)this.value_;
                oprot.writeI32(intValue);
            }
            case LONG_VALUE: {
                final Long longValue = (Long)this.value_;
                oprot.writeI64(longValue);
            }
            case STRING_VALUE: {
                final String stringValue = (String)this.value_;
                oprot.writeString(stringValue);
            }
            case DOUBLE_VALUE: {
                final Double doubleValue = (Double)this.value_;
                oprot.writeDouble(doubleValue);
            }
            case FLAG: {
                final Boolean flag = (Boolean)this.value_;
                oprot.writeBool(flag);
            }
            case L_STRING: {
                final List<String> lString = (List<String>)this.value_;
                oprot.writeListBegin(new TList((byte)11, lString.size()));
                for (final String _iter7 : lString) {
                    oprot.writeString(_iter7);
                }
                oprot.writeListEnd();
            }
            case UNION_MSTRING_STRING: {
                final Map<String, String> unionMStringString = (Map<String, String>)this.value_;
                oprot.writeMapBegin(new TMap((byte)11, (byte)11, unionMStringString.size()));
                for (final Map.Entry<String, String> _iter8 : unionMStringString.entrySet()) {
                    oprot.writeString(_iter8.getKey());
                    oprot.writeString(_iter8.getValue());
                }
                oprot.writeMapEnd();
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
            case INT_VALUE: {
                final Integer intValue = iprot.readI32();
                return intValue;
            }
            case LONG_VALUE: {
                final Long longValue = iprot.readI64();
                return longValue;
            }
            case STRING_VALUE: {
                final String stringValue = iprot.readString();
                return stringValue;
            }
            case DOUBLE_VALUE: {
                final Double doubleValue = iprot.readDouble();
                return doubleValue;
            }
            case FLAG: {
                final Boolean flag = iprot.readBool();
                return flag;
            }
            case L_STRING: {
                final TList _list9 = iprot.readListBegin();
                final List<String> lString = new ArrayList<String>(_list9.size);
                for (int _i10 = 0; _i10 < _list9.size; ++_i10) {
                    final String _elem11 = iprot.readString();
                    lString.add(_elem11);
                }
                iprot.readListEnd();
                return lString;
            }
            case UNION_MSTRING_STRING: {
                final TMap _map12 = iprot.readMapBegin();
                final Map<String, String> unionMStringString = new HashMap<String, String>(2 * _map12.size);
                for (int _i11 = 0; _i11 < _map12.size; ++_i11) {
                    final String _key14 = iprot.readString();
                    final String _val15 = iprot.readString();
                    unionMStringString.put(_key14, _val15);
                }
                iprot.readMapEnd();
                return unionMStringString;
            }
            default: {
                throw new IllegalStateException("setField wasn't null, but didn't match any of the case statements!");
            }
        }
    }
    
    @Override
    protected void tupleSchemeWriteValue(final TProtocol oprot) throws TException {
        switch ((_Fields)this.setField_) {
            case INT_VALUE: {
                final Integer intValue = (Integer)this.value_;
                oprot.writeI32(intValue);
            }
            case LONG_VALUE: {
                final Long longValue = (Long)this.value_;
                oprot.writeI64(longValue);
            }
            case STRING_VALUE: {
                final String stringValue = (String)this.value_;
                oprot.writeString(stringValue);
            }
            case DOUBLE_VALUE: {
                final Double doubleValue = (Double)this.value_;
                oprot.writeDouble(doubleValue);
            }
            case FLAG: {
                final Boolean flag = (Boolean)this.value_;
                oprot.writeBool(flag);
            }
            case L_STRING: {
                final List<String> lString = (List<String>)this.value_;
                oprot.writeListBegin(new TList((byte)11, lString.size()));
                for (final String _iter16 : lString) {
                    oprot.writeString(_iter16);
                }
                oprot.writeListEnd();
            }
            case UNION_MSTRING_STRING: {
                final Map<String, String> unionMStringString = (Map<String, String>)this.value_;
                oprot.writeMapBegin(new TMap((byte)11, (byte)11, unionMStringString.size()));
                for (final Map.Entry<String, String> _iter17 : unionMStringString.entrySet()) {
                    oprot.writeString(_iter17.getKey());
                    oprot.writeString(_iter17.getValue());
                }
                oprot.writeMapEnd();
            }
            default: {
                throw new IllegalStateException("Cannot write union with unknown field " + this.setField_);
            }
        }
    }
    
    @Override
    protected TField getFieldDesc(final _Fields setField) {
        switch (setField) {
            case INT_VALUE: {
                return PropValueUnion.INT_VALUE_FIELD_DESC;
            }
            case LONG_VALUE: {
                return PropValueUnion.LONG_VALUE_FIELD_DESC;
            }
            case STRING_VALUE: {
                return PropValueUnion.STRING_VALUE_FIELD_DESC;
            }
            case DOUBLE_VALUE: {
                return PropValueUnion.DOUBLE_VALUE_FIELD_DESC;
            }
            case FLAG: {
                return PropValueUnion.FLAG_FIELD_DESC;
            }
            case L_STRING: {
                return PropValueUnion.L_STRING_FIELD_DESC;
            }
            case UNION_MSTRING_STRING: {
                return PropValueUnion.UNION_MSTRING_STRING_FIELD_DESC;
            }
            default: {
                throw new IllegalArgumentException("Unknown field id " + setField);
            }
        }
    }
    
    @Override
    protected TStruct getStructDesc() {
        return PropValueUnion.STRUCT_DESC;
    }
    
    @Override
    protected _Fields enumForId(final short id) {
        return _Fields.findByThriftIdOrThrow(id);
    }
    
    @Override
    public _Fields fieldForId(final int fieldId) {
        return _Fields.findByThriftId(fieldId);
    }
    
    public int getIntValue() {
        if (this.getSetField() == _Fields.INT_VALUE) {
            return (int)this.getFieldValue();
        }
        throw new RuntimeException("Cannot get field 'intValue' because union is currently set to " + this.getFieldDesc(((TUnion<T, _Fields>)this).getSetField()).name);
    }
    
    public void setIntValue(final int value) {
        this.setField_ = (F)_Fields.INT_VALUE;
        this.value_ = value;
    }
    
    public long getLongValue() {
        if (this.getSetField() == _Fields.LONG_VALUE) {
            return (long)this.getFieldValue();
        }
        throw new RuntimeException("Cannot get field 'longValue' because union is currently set to " + this.getFieldDesc(((TUnion<T, _Fields>)this).getSetField()).name);
    }
    
    public void setLongValue(final long value) {
        this.setField_ = (F)_Fields.LONG_VALUE;
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
    
    public double getDoubleValue() {
        if (this.getSetField() == _Fields.DOUBLE_VALUE) {
            return (double)this.getFieldValue();
        }
        throw new RuntimeException("Cannot get field 'doubleValue' because union is currently set to " + this.getFieldDesc(((TUnion<T, _Fields>)this).getSetField()).name);
    }
    
    public void setDoubleValue(final double value) {
        this.setField_ = (F)_Fields.DOUBLE_VALUE;
        this.value_ = value;
    }
    
    public boolean getFlag() {
        if (this.getSetField() == _Fields.FLAG) {
            return (boolean)this.getFieldValue();
        }
        throw new RuntimeException("Cannot get field 'flag' because union is currently set to " + this.getFieldDesc(((TUnion<T, _Fields>)this).getSetField()).name);
    }
    
    public void setFlag(final boolean value) {
        this.setField_ = (F)_Fields.FLAG;
        this.value_ = value;
    }
    
    public List<String> getLString() {
        if (this.getSetField() == _Fields.L_STRING) {
            return (List<String>)this.getFieldValue();
        }
        throw new RuntimeException("Cannot get field 'lString' because union is currently set to " + this.getFieldDesc(((TUnion<T, _Fields>)this).getSetField()).name);
    }
    
    public void setLString(final List<String> value) {
        if (value == null) {
            throw new NullPointerException();
        }
        this.setField_ = (F)_Fields.L_STRING;
        this.value_ = value;
    }
    
    public Map<String, String> getUnionMStringString() {
        if (this.getSetField() == _Fields.UNION_MSTRING_STRING) {
            return (Map<String, String>)this.getFieldValue();
        }
        throw new RuntimeException("Cannot get field 'unionMStringString' because union is currently set to " + this.getFieldDesc(((TUnion<T, _Fields>)this).getSetField()).name);
    }
    
    public void setUnionMStringString(final Map<String, String> value) {
        if (value == null) {
            throw new NullPointerException();
        }
        this.setField_ = (F)_Fields.UNION_MSTRING_STRING;
        this.value_ = value;
    }
    
    public boolean isSetIntValue() {
        return this.setField_ == _Fields.INT_VALUE;
    }
    
    public boolean isSetLongValue() {
        return this.setField_ == _Fields.LONG_VALUE;
    }
    
    public boolean isSetStringValue() {
        return this.setField_ == _Fields.STRING_VALUE;
    }
    
    public boolean isSetDoubleValue() {
        return this.setField_ == _Fields.DOUBLE_VALUE;
    }
    
    public boolean isSetFlag() {
        return this.setField_ == _Fields.FLAG;
    }
    
    public boolean isSetLString() {
        return this.setField_ == _Fields.L_STRING;
    }
    
    public boolean isSetUnionMStringString() {
        return this.setField_ == _Fields.UNION_MSTRING_STRING;
    }
    
    @Override
    public boolean equals(final Object other) {
        return other instanceof PropValueUnion && this.equals((PropValueUnion)other);
    }
    
    public boolean equals(final PropValueUnion other) {
        return other != null && this.getSetField() == other.getSetField() && this.getFieldValue().equals(other.getFieldValue());
    }
    
    @Override
    public int compareTo(final PropValueUnion other) {
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
        STRUCT_DESC = new TStruct("PropValueUnion");
        INT_VALUE_FIELD_DESC = new TField("intValue", (byte)8, (short)1);
        LONG_VALUE_FIELD_DESC = new TField("longValue", (byte)10, (short)2);
        STRING_VALUE_FIELD_DESC = new TField("stringValue", (byte)11, (short)3);
        DOUBLE_VALUE_FIELD_DESC = new TField("doubleValue", (byte)4, (short)4);
        FLAG_FIELD_DESC = new TField("flag", (byte)2, (short)5);
        L_STRING_FIELD_DESC = new TField("lString", (byte)15, (short)6);
        UNION_MSTRING_STRING_FIELD_DESC = new TField("unionMStringString", (byte)13, (short)7);
        final Map<_Fields, FieldMetaData> tmpMap = new EnumMap<_Fields, FieldMetaData>(_Fields.class);
        tmpMap.put(_Fields.INT_VALUE, new FieldMetaData("intValue", (byte)2, new FieldValueMetaData((byte)8)));
        tmpMap.put(_Fields.LONG_VALUE, new FieldMetaData("longValue", (byte)2, new FieldValueMetaData((byte)10)));
        tmpMap.put(_Fields.STRING_VALUE, new FieldMetaData("stringValue", (byte)2, new FieldValueMetaData((byte)11)));
        tmpMap.put(_Fields.DOUBLE_VALUE, new FieldMetaData("doubleValue", (byte)2, new FieldValueMetaData((byte)4)));
        tmpMap.put(_Fields.FLAG, new FieldMetaData("flag", (byte)2, new FieldValueMetaData((byte)2)));
        tmpMap.put(_Fields.L_STRING, new FieldMetaData("lString", (byte)3, new ListMetaData((byte)15, new FieldValueMetaData((byte)11))));
        tmpMap.put(_Fields.UNION_MSTRING_STRING, new FieldMetaData("unionMStringString", (byte)3, new MapMetaData((byte)13, new FieldValueMetaData((byte)11), new FieldValueMetaData((byte)11))));
        FieldMetaData.addStructMetaDataMap(PropValueUnion.class, metaDataMap = Collections.unmodifiableMap((Map<? extends _Fields, ? extends FieldMetaData>)tmpMap));
    }
    
    public enum _Fields implements TFieldIdEnum
    {
        INT_VALUE((short)1, "intValue"), 
        LONG_VALUE((short)2, "longValue"), 
        STRING_VALUE((short)3, "stringValue"), 
        DOUBLE_VALUE((short)4, "doubleValue"), 
        FLAG((short)5, "flag"), 
        L_STRING((short)6, "lString"), 
        UNION_MSTRING_STRING((short)7, "unionMStringString");
        
        private static final Map<String, _Fields> byName;
        private final short _thriftId;
        private final String _fieldName;
        
        public static _Fields findByThriftId(final int fieldId) {
            switch (fieldId) {
                case 1: {
                    return _Fields.INT_VALUE;
                }
                case 2: {
                    return _Fields.LONG_VALUE;
                }
                case 3: {
                    return _Fields.STRING_VALUE;
                }
                case 4: {
                    return _Fields.DOUBLE_VALUE;
                }
                case 5: {
                    return _Fields.FLAG;
                }
                case 6: {
                    return _Fields.L_STRING;
                }
                case 7: {
                    return _Fields.UNION_MSTRING_STRING;
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
