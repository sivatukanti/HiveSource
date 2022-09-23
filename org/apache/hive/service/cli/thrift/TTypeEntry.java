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

public class TTypeEntry extends TUnion<TTypeEntry, _Fields>
{
    private static final TStruct STRUCT_DESC;
    private static final TField PRIMITIVE_ENTRY_FIELD_DESC;
    private static final TField ARRAY_ENTRY_FIELD_DESC;
    private static final TField MAP_ENTRY_FIELD_DESC;
    private static final TField STRUCT_ENTRY_FIELD_DESC;
    private static final TField UNION_ENTRY_FIELD_DESC;
    private static final TField USER_DEFINED_TYPE_ENTRY_FIELD_DESC;
    public static final Map<_Fields, FieldMetaData> metaDataMap;
    
    public TTypeEntry() {
    }
    
    public TTypeEntry(final _Fields setField, final Object value) {
        super(setField, value);
    }
    
    public TTypeEntry(final TTypeEntry other) {
        super(other);
    }
    
    @Override
    public TTypeEntry deepCopy() {
        return new TTypeEntry(this);
    }
    
    public static TTypeEntry primitiveEntry(final TPrimitiveTypeEntry value) {
        final TTypeEntry x = new TTypeEntry();
        x.setPrimitiveEntry(value);
        return x;
    }
    
    public static TTypeEntry arrayEntry(final TArrayTypeEntry value) {
        final TTypeEntry x = new TTypeEntry();
        x.setArrayEntry(value);
        return x;
    }
    
    public static TTypeEntry mapEntry(final TMapTypeEntry value) {
        final TTypeEntry x = new TTypeEntry();
        x.setMapEntry(value);
        return x;
    }
    
    public static TTypeEntry structEntry(final TStructTypeEntry value) {
        final TTypeEntry x = new TTypeEntry();
        x.setStructEntry(value);
        return x;
    }
    
    public static TTypeEntry unionEntry(final TUnionTypeEntry value) {
        final TTypeEntry x = new TTypeEntry();
        x.setUnionEntry(value);
        return x;
    }
    
    public static TTypeEntry userDefinedTypeEntry(final TUserDefinedTypeEntry value) {
        final TTypeEntry x = new TTypeEntry();
        x.setUserDefinedTypeEntry(value);
        return x;
    }
    
    @Override
    protected void checkType(final _Fields setField, final Object value) throws ClassCastException {
        switch (setField) {
            case PRIMITIVE_ENTRY: {
                if (value instanceof TPrimitiveTypeEntry) {
                    break;
                }
                throw new ClassCastException("Was expecting value of type TPrimitiveTypeEntry for field 'primitiveEntry', but got " + value.getClass().getSimpleName());
            }
            case ARRAY_ENTRY: {
                if (value instanceof TArrayTypeEntry) {
                    break;
                }
                throw new ClassCastException("Was expecting value of type TArrayTypeEntry for field 'arrayEntry', but got " + value.getClass().getSimpleName());
            }
            case MAP_ENTRY: {
                if (value instanceof TMapTypeEntry) {
                    break;
                }
                throw new ClassCastException("Was expecting value of type TMapTypeEntry for field 'mapEntry', but got " + value.getClass().getSimpleName());
            }
            case STRUCT_ENTRY: {
                if (value instanceof TStructTypeEntry) {
                    break;
                }
                throw new ClassCastException("Was expecting value of type TStructTypeEntry for field 'structEntry', but got " + value.getClass().getSimpleName());
            }
            case UNION_ENTRY: {
                if (value instanceof TUnionTypeEntry) {
                    break;
                }
                throw new ClassCastException("Was expecting value of type TUnionTypeEntry for field 'unionEntry', but got " + value.getClass().getSimpleName());
            }
            case USER_DEFINED_TYPE_ENTRY: {
                if (value instanceof TUserDefinedTypeEntry) {
                    break;
                }
                throw new ClassCastException("Was expecting value of type TUserDefinedTypeEntry for field 'userDefinedTypeEntry', but got " + value.getClass().getSimpleName());
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
            case PRIMITIVE_ENTRY: {
                if (field.type == TTypeEntry.PRIMITIVE_ENTRY_FIELD_DESC.type) {
                    final TPrimitiveTypeEntry primitiveEntry = new TPrimitiveTypeEntry();
                    primitiveEntry.read(iprot);
                    return primitiveEntry;
                }
                TProtocolUtil.skip(iprot, field.type);
                return null;
            }
            case ARRAY_ENTRY: {
                if (field.type == TTypeEntry.ARRAY_ENTRY_FIELD_DESC.type) {
                    final TArrayTypeEntry arrayEntry = new TArrayTypeEntry();
                    arrayEntry.read(iprot);
                    return arrayEntry;
                }
                TProtocolUtil.skip(iprot, field.type);
                return null;
            }
            case MAP_ENTRY: {
                if (field.type == TTypeEntry.MAP_ENTRY_FIELD_DESC.type) {
                    final TMapTypeEntry mapEntry = new TMapTypeEntry();
                    mapEntry.read(iprot);
                    return mapEntry;
                }
                TProtocolUtil.skip(iprot, field.type);
                return null;
            }
            case STRUCT_ENTRY: {
                if (field.type == TTypeEntry.STRUCT_ENTRY_FIELD_DESC.type) {
                    final TStructTypeEntry structEntry = new TStructTypeEntry();
                    structEntry.read(iprot);
                    return structEntry;
                }
                TProtocolUtil.skip(iprot, field.type);
                return null;
            }
            case UNION_ENTRY: {
                if (field.type == TTypeEntry.UNION_ENTRY_FIELD_DESC.type) {
                    final TUnionTypeEntry unionEntry = new TUnionTypeEntry();
                    unionEntry.read(iprot);
                    return unionEntry;
                }
                TProtocolUtil.skip(iprot, field.type);
                return null;
            }
            case USER_DEFINED_TYPE_ENTRY: {
                if (field.type == TTypeEntry.USER_DEFINED_TYPE_ENTRY_FIELD_DESC.type) {
                    final TUserDefinedTypeEntry userDefinedTypeEntry = new TUserDefinedTypeEntry();
                    userDefinedTypeEntry.read(iprot);
                    return userDefinedTypeEntry;
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
            case PRIMITIVE_ENTRY: {
                final TPrimitiveTypeEntry primitiveEntry = (TPrimitiveTypeEntry)this.value_;
                primitiveEntry.write(oprot);
            }
            case ARRAY_ENTRY: {
                final TArrayTypeEntry arrayEntry = (TArrayTypeEntry)this.value_;
                arrayEntry.write(oprot);
            }
            case MAP_ENTRY: {
                final TMapTypeEntry mapEntry = (TMapTypeEntry)this.value_;
                mapEntry.write(oprot);
            }
            case STRUCT_ENTRY: {
                final TStructTypeEntry structEntry = (TStructTypeEntry)this.value_;
                structEntry.write(oprot);
            }
            case UNION_ENTRY: {
                final TUnionTypeEntry unionEntry = (TUnionTypeEntry)this.value_;
                unionEntry.write(oprot);
            }
            case USER_DEFINED_TYPE_ENTRY: {
                final TUserDefinedTypeEntry userDefinedTypeEntry = (TUserDefinedTypeEntry)this.value_;
                userDefinedTypeEntry.write(oprot);
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
            case PRIMITIVE_ENTRY: {
                final TPrimitiveTypeEntry primitiveEntry = new TPrimitiveTypeEntry();
                primitiveEntry.read(iprot);
                return primitiveEntry;
            }
            case ARRAY_ENTRY: {
                final TArrayTypeEntry arrayEntry = new TArrayTypeEntry();
                arrayEntry.read(iprot);
                return arrayEntry;
            }
            case MAP_ENTRY: {
                final TMapTypeEntry mapEntry = new TMapTypeEntry();
                mapEntry.read(iprot);
                return mapEntry;
            }
            case STRUCT_ENTRY: {
                final TStructTypeEntry structEntry = new TStructTypeEntry();
                structEntry.read(iprot);
                return structEntry;
            }
            case UNION_ENTRY: {
                final TUnionTypeEntry unionEntry = new TUnionTypeEntry();
                unionEntry.read(iprot);
                return unionEntry;
            }
            case USER_DEFINED_TYPE_ENTRY: {
                final TUserDefinedTypeEntry userDefinedTypeEntry = new TUserDefinedTypeEntry();
                userDefinedTypeEntry.read(iprot);
                return userDefinedTypeEntry;
            }
            default: {
                throw new IllegalStateException("setField wasn't null, but didn't match any of the case statements!");
            }
        }
    }
    
    @Override
    protected void tupleSchemeWriteValue(final TProtocol oprot) throws TException {
        switch ((_Fields)this.setField_) {
            case PRIMITIVE_ENTRY: {
                final TPrimitiveTypeEntry primitiveEntry = (TPrimitiveTypeEntry)this.value_;
                primitiveEntry.write(oprot);
            }
            case ARRAY_ENTRY: {
                final TArrayTypeEntry arrayEntry = (TArrayTypeEntry)this.value_;
                arrayEntry.write(oprot);
            }
            case MAP_ENTRY: {
                final TMapTypeEntry mapEntry = (TMapTypeEntry)this.value_;
                mapEntry.write(oprot);
            }
            case STRUCT_ENTRY: {
                final TStructTypeEntry structEntry = (TStructTypeEntry)this.value_;
                structEntry.write(oprot);
            }
            case UNION_ENTRY: {
                final TUnionTypeEntry unionEntry = (TUnionTypeEntry)this.value_;
                unionEntry.write(oprot);
            }
            case USER_DEFINED_TYPE_ENTRY: {
                final TUserDefinedTypeEntry userDefinedTypeEntry = (TUserDefinedTypeEntry)this.value_;
                userDefinedTypeEntry.write(oprot);
            }
            default: {
                throw new IllegalStateException("Cannot write union with unknown field " + this.setField_);
            }
        }
    }
    
    @Override
    protected TField getFieldDesc(final _Fields setField) {
        switch (setField) {
            case PRIMITIVE_ENTRY: {
                return TTypeEntry.PRIMITIVE_ENTRY_FIELD_DESC;
            }
            case ARRAY_ENTRY: {
                return TTypeEntry.ARRAY_ENTRY_FIELD_DESC;
            }
            case MAP_ENTRY: {
                return TTypeEntry.MAP_ENTRY_FIELD_DESC;
            }
            case STRUCT_ENTRY: {
                return TTypeEntry.STRUCT_ENTRY_FIELD_DESC;
            }
            case UNION_ENTRY: {
                return TTypeEntry.UNION_ENTRY_FIELD_DESC;
            }
            case USER_DEFINED_TYPE_ENTRY: {
                return TTypeEntry.USER_DEFINED_TYPE_ENTRY_FIELD_DESC;
            }
            default: {
                throw new IllegalArgumentException("Unknown field id " + setField);
            }
        }
    }
    
    @Override
    protected TStruct getStructDesc() {
        return TTypeEntry.STRUCT_DESC;
    }
    
    @Override
    protected _Fields enumForId(final short id) {
        return _Fields.findByThriftIdOrThrow(id);
    }
    
    @Override
    public _Fields fieldForId(final int fieldId) {
        return _Fields.findByThriftId(fieldId);
    }
    
    public TPrimitiveTypeEntry getPrimitiveEntry() {
        if (this.getSetField() == _Fields.PRIMITIVE_ENTRY) {
            return (TPrimitiveTypeEntry)this.getFieldValue();
        }
        throw new RuntimeException("Cannot get field 'primitiveEntry' because union is currently set to " + this.getFieldDesc(((TUnion<T, _Fields>)this).getSetField()).name);
    }
    
    public void setPrimitiveEntry(final TPrimitiveTypeEntry value) {
        if (value == null) {
            throw new NullPointerException();
        }
        this.setField_ = (F)_Fields.PRIMITIVE_ENTRY;
        this.value_ = value;
    }
    
    public TArrayTypeEntry getArrayEntry() {
        if (this.getSetField() == _Fields.ARRAY_ENTRY) {
            return (TArrayTypeEntry)this.getFieldValue();
        }
        throw new RuntimeException("Cannot get field 'arrayEntry' because union is currently set to " + this.getFieldDesc(((TUnion<T, _Fields>)this).getSetField()).name);
    }
    
    public void setArrayEntry(final TArrayTypeEntry value) {
        if (value == null) {
            throw new NullPointerException();
        }
        this.setField_ = (F)_Fields.ARRAY_ENTRY;
        this.value_ = value;
    }
    
    public TMapTypeEntry getMapEntry() {
        if (this.getSetField() == _Fields.MAP_ENTRY) {
            return (TMapTypeEntry)this.getFieldValue();
        }
        throw new RuntimeException("Cannot get field 'mapEntry' because union is currently set to " + this.getFieldDesc(((TUnion<T, _Fields>)this).getSetField()).name);
    }
    
    public void setMapEntry(final TMapTypeEntry value) {
        if (value == null) {
            throw new NullPointerException();
        }
        this.setField_ = (F)_Fields.MAP_ENTRY;
        this.value_ = value;
    }
    
    public TStructTypeEntry getStructEntry() {
        if (this.getSetField() == _Fields.STRUCT_ENTRY) {
            return (TStructTypeEntry)this.getFieldValue();
        }
        throw new RuntimeException("Cannot get field 'structEntry' because union is currently set to " + this.getFieldDesc(((TUnion<T, _Fields>)this).getSetField()).name);
    }
    
    public void setStructEntry(final TStructTypeEntry value) {
        if (value == null) {
            throw new NullPointerException();
        }
        this.setField_ = (F)_Fields.STRUCT_ENTRY;
        this.value_ = value;
    }
    
    public TUnionTypeEntry getUnionEntry() {
        if (this.getSetField() == _Fields.UNION_ENTRY) {
            return (TUnionTypeEntry)this.getFieldValue();
        }
        throw new RuntimeException("Cannot get field 'unionEntry' because union is currently set to " + this.getFieldDesc(((TUnion<T, _Fields>)this).getSetField()).name);
    }
    
    public void setUnionEntry(final TUnionTypeEntry value) {
        if (value == null) {
            throw new NullPointerException();
        }
        this.setField_ = (F)_Fields.UNION_ENTRY;
        this.value_ = value;
    }
    
    public TUserDefinedTypeEntry getUserDefinedTypeEntry() {
        if (this.getSetField() == _Fields.USER_DEFINED_TYPE_ENTRY) {
            return (TUserDefinedTypeEntry)this.getFieldValue();
        }
        throw new RuntimeException("Cannot get field 'userDefinedTypeEntry' because union is currently set to " + this.getFieldDesc(((TUnion<T, _Fields>)this).getSetField()).name);
    }
    
    public void setUserDefinedTypeEntry(final TUserDefinedTypeEntry value) {
        if (value == null) {
            throw new NullPointerException();
        }
        this.setField_ = (F)_Fields.USER_DEFINED_TYPE_ENTRY;
        this.value_ = value;
    }
    
    public boolean isSetPrimitiveEntry() {
        return this.setField_ == _Fields.PRIMITIVE_ENTRY;
    }
    
    public boolean isSetArrayEntry() {
        return this.setField_ == _Fields.ARRAY_ENTRY;
    }
    
    public boolean isSetMapEntry() {
        return this.setField_ == _Fields.MAP_ENTRY;
    }
    
    public boolean isSetStructEntry() {
        return this.setField_ == _Fields.STRUCT_ENTRY;
    }
    
    public boolean isSetUnionEntry() {
        return this.setField_ == _Fields.UNION_ENTRY;
    }
    
    public boolean isSetUserDefinedTypeEntry() {
        return this.setField_ == _Fields.USER_DEFINED_TYPE_ENTRY;
    }
    
    @Override
    public boolean equals(final Object other) {
        return other instanceof TTypeEntry && this.equals((TTypeEntry)other);
    }
    
    public boolean equals(final TTypeEntry other) {
        return other != null && this.getSetField() == other.getSetField() && this.getFieldValue().equals(other.getFieldValue());
    }
    
    @Override
    public int compareTo(final TTypeEntry other) {
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
        STRUCT_DESC = new TStruct("TTypeEntry");
        PRIMITIVE_ENTRY_FIELD_DESC = new TField("primitiveEntry", (byte)12, (short)1);
        ARRAY_ENTRY_FIELD_DESC = new TField("arrayEntry", (byte)12, (short)2);
        MAP_ENTRY_FIELD_DESC = new TField("mapEntry", (byte)12, (short)3);
        STRUCT_ENTRY_FIELD_DESC = new TField("structEntry", (byte)12, (short)4);
        UNION_ENTRY_FIELD_DESC = new TField("unionEntry", (byte)12, (short)5);
        USER_DEFINED_TYPE_ENTRY_FIELD_DESC = new TField("userDefinedTypeEntry", (byte)12, (short)6);
        final Map<_Fields, FieldMetaData> tmpMap = new EnumMap<_Fields, FieldMetaData>(_Fields.class);
        tmpMap.put(_Fields.PRIMITIVE_ENTRY, new FieldMetaData("primitiveEntry", (byte)3, new StructMetaData((byte)12, TPrimitiveTypeEntry.class)));
        tmpMap.put(_Fields.ARRAY_ENTRY, new FieldMetaData("arrayEntry", (byte)3, new StructMetaData((byte)12, TArrayTypeEntry.class)));
        tmpMap.put(_Fields.MAP_ENTRY, new FieldMetaData("mapEntry", (byte)3, new StructMetaData((byte)12, TMapTypeEntry.class)));
        tmpMap.put(_Fields.STRUCT_ENTRY, new FieldMetaData("structEntry", (byte)3, new StructMetaData((byte)12, TStructTypeEntry.class)));
        tmpMap.put(_Fields.UNION_ENTRY, new FieldMetaData("unionEntry", (byte)3, new StructMetaData((byte)12, TUnionTypeEntry.class)));
        tmpMap.put(_Fields.USER_DEFINED_TYPE_ENTRY, new FieldMetaData("userDefinedTypeEntry", (byte)3, new StructMetaData((byte)12, TUserDefinedTypeEntry.class)));
        FieldMetaData.addStructMetaDataMap(TTypeEntry.class, metaDataMap = Collections.unmodifiableMap((Map<? extends _Fields, ? extends FieldMetaData>)tmpMap));
    }
    
    public enum _Fields implements TFieldIdEnum
    {
        PRIMITIVE_ENTRY((short)1, "primitiveEntry"), 
        ARRAY_ENTRY((short)2, "arrayEntry"), 
        MAP_ENTRY((short)3, "mapEntry"), 
        STRUCT_ENTRY((short)4, "structEntry"), 
        UNION_ENTRY((short)5, "unionEntry"), 
        USER_DEFINED_TYPE_ENTRY((short)6, "userDefinedTypeEntry");
        
        private static final Map<String, _Fields> byName;
        private final short _thriftId;
        private final String _fieldName;
        
        public static _Fields findByThriftId(final int fieldId) {
            switch (fieldId) {
                case 1: {
                    return _Fields.PRIMITIVE_ENTRY;
                }
                case 2: {
                    return _Fields.ARRAY_ENTRY;
                }
                case 3: {
                    return _Fields.MAP_ENTRY;
                }
                case 4: {
                    return _Fields.STRUCT_ENTRY;
                }
                case 5: {
                    return _Fields.UNION_ENTRY;
                }
                case 6: {
                    return _Fields.USER_DEFINED_TYPE_ENTRY;
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
