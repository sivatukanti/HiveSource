// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.hive.metastore.api;

import java.util.BitSet;
import org.apache.thrift.protocol.TTupleProtocol;
import org.apache.thrift.protocol.TList;
import org.apache.thrift.protocol.TProtocolUtil;
import java.util.EnumSet;
import java.util.Collections;
import org.apache.thrift.meta_data.ListMetaData;
import org.apache.thrift.meta_data.StructMetaData;
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
import java.util.Iterator;
import java.util.ArrayList;
import org.apache.thrift.meta_data.FieldMetaData;
import java.util.List;
import org.apache.thrift.scheme.SchemeFactory;
import org.apache.thrift.scheme.IScheme;
import java.util.Map;
import org.apache.thrift.protocol.TField;
import org.apache.thrift.protocol.TStruct;
import java.io.Serializable;
import org.apache.thrift.TBase;

public class Type implements TBase<Type, _Fields>, Serializable, Cloneable
{
    private static final TStruct STRUCT_DESC;
    private static final TField NAME_FIELD_DESC;
    private static final TField TYPE1_FIELD_DESC;
    private static final TField TYPE2_FIELD_DESC;
    private static final TField FIELDS_FIELD_DESC;
    private static final Map<Class<? extends IScheme>, SchemeFactory> schemes;
    private String name;
    private String type1;
    private String type2;
    private List<FieldSchema> fields;
    private _Fields[] optionals;
    public static final Map<_Fields, FieldMetaData> metaDataMap;
    
    public Type() {
        this.optionals = new _Fields[] { _Fields.TYPE1, _Fields.TYPE2, _Fields.FIELDS };
    }
    
    public Type(final String name) {
        this();
        this.name = name;
    }
    
    public Type(final Type other) {
        this.optionals = new _Fields[] { _Fields.TYPE1, _Fields.TYPE2, _Fields.FIELDS };
        if (other.isSetName()) {
            this.name = other.name;
        }
        if (other.isSetType1()) {
            this.type1 = other.type1;
        }
        if (other.isSetType2()) {
            this.type2 = other.type2;
        }
        if (other.isSetFields()) {
            final List<FieldSchema> __this__fields = new ArrayList<FieldSchema>();
            for (final FieldSchema other_element : other.fields) {
                __this__fields.add(new FieldSchema(other_element));
            }
            this.fields = __this__fields;
        }
    }
    
    @Override
    public Type deepCopy() {
        return new Type(this);
    }
    
    @Override
    public void clear() {
        this.name = null;
        this.type1 = null;
        this.type2 = null;
        this.fields = null;
    }
    
    public String getName() {
        return this.name;
    }
    
    public void setName(final String name) {
        this.name = name;
    }
    
    public void unsetName() {
        this.name = null;
    }
    
    public boolean isSetName() {
        return this.name != null;
    }
    
    public void setNameIsSet(final boolean value) {
        if (!value) {
            this.name = null;
        }
    }
    
    public String getType1() {
        return this.type1;
    }
    
    public void setType1(final String type1) {
        this.type1 = type1;
    }
    
    public void unsetType1() {
        this.type1 = null;
    }
    
    public boolean isSetType1() {
        return this.type1 != null;
    }
    
    public void setType1IsSet(final boolean value) {
        if (!value) {
            this.type1 = null;
        }
    }
    
    public String getType2() {
        return this.type2;
    }
    
    public void setType2(final String type2) {
        this.type2 = type2;
    }
    
    public void unsetType2() {
        this.type2 = null;
    }
    
    public boolean isSetType2() {
        return this.type2 != null;
    }
    
    public void setType2IsSet(final boolean value) {
        if (!value) {
            this.type2 = null;
        }
    }
    
    public int getFieldsSize() {
        return (this.fields == null) ? 0 : this.fields.size();
    }
    
    public Iterator<FieldSchema> getFieldsIterator() {
        return (this.fields == null) ? null : this.fields.iterator();
    }
    
    public void addToFields(final FieldSchema elem) {
        if (this.fields == null) {
            this.fields = new ArrayList<FieldSchema>();
        }
        this.fields.add(elem);
    }
    
    public List<FieldSchema> getFields() {
        return this.fields;
    }
    
    public void setFields(final List<FieldSchema> fields) {
        this.fields = fields;
    }
    
    public void unsetFields() {
        this.fields = null;
    }
    
    public boolean isSetFields() {
        return this.fields != null;
    }
    
    public void setFieldsIsSet(final boolean value) {
        if (!value) {
            this.fields = null;
        }
    }
    
    @Override
    public void setFieldValue(final _Fields field, final Object value) {
        switch (field) {
            case NAME: {
                if (value == null) {
                    this.unsetName();
                    break;
                }
                this.setName((String)value);
                break;
            }
            case TYPE1: {
                if (value == null) {
                    this.unsetType1();
                    break;
                }
                this.setType1((String)value);
                break;
            }
            case TYPE2: {
                if (value == null) {
                    this.unsetType2();
                    break;
                }
                this.setType2((String)value);
                break;
            }
            case FIELDS: {
                if (value == null) {
                    this.unsetFields();
                    break;
                }
                this.setFields((List<FieldSchema>)value);
                break;
            }
        }
    }
    
    @Override
    public Object getFieldValue(final _Fields field) {
        switch (field) {
            case NAME: {
                return this.getName();
            }
            case TYPE1: {
                return this.getType1();
            }
            case TYPE2: {
                return this.getType2();
            }
            case FIELDS: {
                return this.getFields();
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
            case NAME: {
                return this.isSetName();
            }
            case TYPE1: {
                return this.isSetType1();
            }
            case TYPE2: {
                return this.isSetType2();
            }
            case FIELDS: {
                return this.isSetFields();
            }
            default: {
                throw new IllegalStateException();
            }
        }
    }
    
    @Override
    public boolean equals(final Object that) {
        return that != null && that instanceof Type && this.equals((Type)that);
    }
    
    public boolean equals(final Type that) {
        if (that == null) {
            return false;
        }
        final boolean this_present_name = this.isSetName();
        final boolean that_present_name = that.isSetName();
        if (this_present_name || that_present_name) {
            if (!this_present_name || !that_present_name) {
                return false;
            }
            if (!this.name.equals(that.name)) {
                return false;
            }
        }
        final boolean this_present_type1 = this.isSetType1();
        final boolean that_present_type1 = that.isSetType1();
        if (this_present_type1 || that_present_type1) {
            if (!this_present_type1 || !that_present_type1) {
                return false;
            }
            if (!this.type1.equals(that.type1)) {
                return false;
            }
        }
        final boolean this_present_type2 = this.isSetType2();
        final boolean that_present_type2 = that.isSetType2();
        if (this_present_type2 || that_present_type2) {
            if (!this_present_type2 || !that_present_type2) {
                return false;
            }
            if (!this.type2.equals(that.type2)) {
                return false;
            }
        }
        final boolean this_present_fields = this.isSetFields();
        final boolean that_present_fields = that.isSetFields();
        if (this_present_fields || that_present_fields) {
            if (!this_present_fields || !that_present_fields) {
                return false;
            }
            if (!this.fields.equals(that.fields)) {
                return false;
            }
        }
        return true;
    }
    
    @Override
    public int hashCode() {
        final HashCodeBuilder builder = new HashCodeBuilder();
        final boolean present_name = this.isSetName();
        builder.append(present_name);
        if (present_name) {
            builder.append(this.name);
        }
        final boolean present_type1 = this.isSetType1();
        builder.append(present_type1);
        if (present_type1) {
            builder.append(this.type1);
        }
        final boolean present_type2 = this.isSetType2();
        builder.append(present_type2);
        if (present_type2) {
            builder.append(this.type2);
        }
        final boolean present_fields = this.isSetFields();
        builder.append(present_fields);
        if (present_fields) {
            builder.append(this.fields);
        }
        return builder.toHashCode();
    }
    
    @Override
    public int compareTo(final Type other) {
        if (!this.getClass().equals(other.getClass())) {
            return this.getClass().getName().compareTo(other.getClass().getName());
        }
        int lastComparison = 0;
        final Type typedOther = other;
        lastComparison = Boolean.valueOf(this.isSetName()).compareTo(Boolean.valueOf(typedOther.isSetName()));
        if (lastComparison != 0) {
            return lastComparison;
        }
        if (this.isSetName()) {
            lastComparison = TBaseHelper.compareTo(this.name, typedOther.name);
            if (lastComparison != 0) {
                return lastComparison;
            }
        }
        lastComparison = Boolean.valueOf(this.isSetType1()).compareTo(Boolean.valueOf(typedOther.isSetType1()));
        if (lastComparison != 0) {
            return lastComparison;
        }
        if (this.isSetType1()) {
            lastComparison = TBaseHelper.compareTo(this.type1, typedOther.type1);
            if (lastComparison != 0) {
                return lastComparison;
            }
        }
        lastComparison = Boolean.valueOf(this.isSetType2()).compareTo(Boolean.valueOf(typedOther.isSetType2()));
        if (lastComparison != 0) {
            return lastComparison;
        }
        if (this.isSetType2()) {
            lastComparison = TBaseHelper.compareTo(this.type2, typedOther.type2);
            if (lastComparison != 0) {
                return lastComparison;
            }
        }
        lastComparison = Boolean.valueOf(this.isSetFields()).compareTo(Boolean.valueOf(typedOther.isSetFields()));
        if (lastComparison != 0) {
            return lastComparison;
        }
        if (this.isSetFields()) {
            lastComparison = TBaseHelper.compareTo(this.fields, typedOther.fields);
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
        Type.schemes.get(iprot.getScheme()).getScheme().read(iprot, this);
    }
    
    @Override
    public void write(final TProtocol oprot) throws TException {
        Type.schemes.get(oprot.getScheme()).getScheme().write(oprot, this);
    }
    
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("Type(");
        boolean first = true;
        sb.append("name:");
        if (this.name == null) {
            sb.append("null");
        }
        else {
            sb.append(this.name);
        }
        first = false;
        if (this.isSetType1()) {
            if (!first) {
                sb.append(", ");
            }
            sb.append("type1:");
            if (this.type1 == null) {
                sb.append("null");
            }
            else {
                sb.append(this.type1);
            }
            first = false;
        }
        if (this.isSetType2()) {
            if (!first) {
                sb.append(", ");
            }
            sb.append("type2:");
            if (this.type2 == null) {
                sb.append("null");
            }
            else {
                sb.append(this.type2);
            }
            first = false;
        }
        if (this.isSetFields()) {
            if (!first) {
                sb.append(", ");
            }
            sb.append("fields:");
            if (this.fields == null) {
                sb.append("null");
            }
            else {
                sb.append(this.fields);
            }
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
            this.read(new TCompactProtocol(new TIOStreamTransport(in)));
        }
        catch (TException te) {
            throw new IOException(te);
        }
    }
    
    static {
        STRUCT_DESC = new TStruct("Type");
        NAME_FIELD_DESC = new TField("name", (byte)11, (short)1);
        TYPE1_FIELD_DESC = new TField("type1", (byte)11, (short)2);
        TYPE2_FIELD_DESC = new TField("type2", (byte)11, (short)3);
        FIELDS_FIELD_DESC = new TField("fields", (byte)15, (short)4);
        (schemes = new HashMap<Class<? extends IScheme>, SchemeFactory>()).put(StandardScheme.class, new TypeStandardSchemeFactory());
        Type.schemes.put(TupleScheme.class, new TypeTupleSchemeFactory());
        final Map<_Fields, FieldMetaData> tmpMap = new EnumMap<_Fields, FieldMetaData>(_Fields.class);
        tmpMap.put(_Fields.NAME, new FieldMetaData("name", (byte)3, new FieldValueMetaData((byte)11)));
        tmpMap.put(_Fields.TYPE1, new FieldMetaData("type1", (byte)2, new FieldValueMetaData((byte)11)));
        tmpMap.put(_Fields.TYPE2, new FieldMetaData("type2", (byte)2, new FieldValueMetaData((byte)11)));
        tmpMap.put(_Fields.FIELDS, new FieldMetaData("fields", (byte)2, new ListMetaData((byte)15, new StructMetaData((byte)12, FieldSchema.class))));
        FieldMetaData.addStructMetaDataMap(Type.class, metaDataMap = Collections.unmodifiableMap((Map<? extends _Fields, ? extends FieldMetaData>)tmpMap));
    }
    
    public enum _Fields implements TFieldIdEnum
    {
        NAME((short)1, "name"), 
        TYPE1((short)2, "type1"), 
        TYPE2((short)3, "type2"), 
        FIELDS((short)4, "fields");
        
        private static final Map<String, _Fields> byName;
        private final short _thriftId;
        private final String _fieldName;
        
        public static _Fields findByThriftId(final int fieldId) {
            switch (fieldId) {
                case 1: {
                    return _Fields.NAME;
                }
                case 2: {
                    return _Fields.TYPE1;
                }
                case 3: {
                    return _Fields.TYPE2;
                }
                case 4: {
                    return _Fields.FIELDS;
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
    
    private static class TypeStandardSchemeFactory implements SchemeFactory
    {
        @Override
        public TypeStandardScheme getScheme() {
            return new TypeStandardScheme();
        }
    }
    
    private static class TypeStandardScheme extends StandardScheme<Type>
    {
        @Override
        public void read(final TProtocol iprot, final Type struct) throws TException {
            iprot.readStructBegin();
            while (true) {
                final TField schemeField = iprot.readFieldBegin();
                if (schemeField.type == 0) {
                    break;
                }
                switch (schemeField.id) {
                    case 1: {
                        if (schemeField.type == 11) {
                            struct.name = iprot.readString();
                            struct.setNameIsSet(true);
                            break;
                        }
                        TProtocolUtil.skip(iprot, schemeField.type);
                        break;
                    }
                    case 2: {
                        if (schemeField.type == 11) {
                            struct.type1 = iprot.readString();
                            struct.setType1IsSet(true);
                            break;
                        }
                        TProtocolUtil.skip(iprot, schemeField.type);
                        break;
                    }
                    case 3: {
                        if (schemeField.type == 11) {
                            struct.type2 = iprot.readString();
                            struct.setType2IsSet(true);
                            break;
                        }
                        TProtocolUtil.skip(iprot, schemeField.type);
                        break;
                    }
                    case 4: {
                        if (schemeField.type == 15) {
                            final TList _list0 = iprot.readListBegin();
                            struct.fields = (List<FieldSchema>)new ArrayList(_list0.size);
                            for (int _i1 = 0; _i1 < _list0.size; ++_i1) {
                                final FieldSchema _elem2 = new FieldSchema();
                                _elem2.read(iprot);
                                struct.fields.add(_elem2);
                            }
                            iprot.readListEnd();
                            struct.setFieldsIsSet(true);
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
        public void write(final TProtocol oprot, final Type struct) throws TException {
            struct.validate();
            oprot.writeStructBegin(Type.STRUCT_DESC);
            if (struct.name != null) {
                oprot.writeFieldBegin(Type.NAME_FIELD_DESC);
                oprot.writeString(struct.name);
                oprot.writeFieldEnd();
            }
            if (struct.type1 != null && struct.isSetType1()) {
                oprot.writeFieldBegin(Type.TYPE1_FIELD_DESC);
                oprot.writeString(struct.type1);
                oprot.writeFieldEnd();
            }
            if (struct.type2 != null && struct.isSetType2()) {
                oprot.writeFieldBegin(Type.TYPE2_FIELD_DESC);
                oprot.writeString(struct.type2);
                oprot.writeFieldEnd();
            }
            if (struct.fields != null && struct.isSetFields()) {
                oprot.writeFieldBegin(Type.FIELDS_FIELD_DESC);
                oprot.writeListBegin(new TList((byte)12, struct.fields.size()));
                for (final FieldSchema _iter3 : struct.fields) {
                    _iter3.write(oprot);
                }
                oprot.writeListEnd();
                oprot.writeFieldEnd();
            }
            oprot.writeFieldStop();
            oprot.writeStructEnd();
        }
    }
    
    private static class TypeTupleSchemeFactory implements SchemeFactory
    {
        @Override
        public TypeTupleScheme getScheme() {
            return new TypeTupleScheme();
        }
    }
    
    private static class TypeTupleScheme extends TupleScheme<Type>
    {
        @Override
        public void write(final TProtocol prot, final Type struct) throws TException {
            final TTupleProtocol oprot = (TTupleProtocol)prot;
            final BitSet optionals = new BitSet();
            if (struct.isSetName()) {
                optionals.set(0);
            }
            if (struct.isSetType1()) {
                optionals.set(1);
            }
            if (struct.isSetType2()) {
                optionals.set(2);
            }
            if (struct.isSetFields()) {
                optionals.set(3);
            }
            oprot.writeBitSet(optionals, 4);
            if (struct.isSetName()) {
                oprot.writeString(struct.name);
            }
            if (struct.isSetType1()) {
                oprot.writeString(struct.type1);
            }
            if (struct.isSetType2()) {
                oprot.writeString(struct.type2);
            }
            if (struct.isSetFields()) {
                oprot.writeI32(struct.fields.size());
                for (final FieldSchema _iter4 : struct.fields) {
                    _iter4.write(oprot);
                }
            }
        }
        
        @Override
        public void read(final TProtocol prot, final Type struct) throws TException {
            final TTupleProtocol iprot = (TTupleProtocol)prot;
            final BitSet incoming = iprot.readBitSet(4);
            if (incoming.get(0)) {
                struct.name = iprot.readString();
                struct.setNameIsSet(true);
            }
            if (incoming.get(1)) {
                struct.type1 = iprot.readString();
                struct.setType1IsSet(true);
            }
            if (incoming.get(2)) {
                struct.type2 = iprot.readString();
                struct.setType2IsSet(true);
            }
            if (incoming.get(3)) {
                final TList _list5 = new TList((byte)12, iprot.readI32());
                struct.fields = (List<FieldSchema>)new ArrayList(_list5.size);
                for (int _i6 = 0; _i6 < _list5.size; ++_i6) {
                    final FieldSchema _elem7 = new FieldSchema();
                    _elem7.read(iprot);
                    struct.fields.add(_elem7);
                }
                struct.setFieldsIsSet(true);
            }
        }
    }
}
