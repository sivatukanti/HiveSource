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
import org.apache.thrift.meta_data.FieldValueMetaData;
import org.apache.thrift.TEnum;
import org.apache.thrift.meta_data.EnumMetaData;
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

public class HiveObjectRef implements TBase<HiveObjectRef, _Fields>, Serializable, Cloneable
{
    private static final TStruct STRUCT_DESC;
    private static final TField OBJECT_TYPE_FIELD_DESC;
    private static final TField DB_NAME_FIELD_DESC;
    private static final TField OBJECT_NAME_FIELD_DESC;
    private static final TField PART_VALUES_FIELD_DESC;
    private static final TField COLUMN_NAME_FIELD_DESC;
    private static final Map<Class<? extends IScheme>, SchemeFactory> schemes;
    private HiveObjectType objectType;
    private String dbName;
    private String objectName;
    private List<String> partValues;
    private String columnName;
    public static final Map<_Fields, FieldMetaData> metaDataMap;
    
    public HiveObjectRef() {
    }
    
    public HiveObjectRef(final HiveObjectType objectType, final String dbName, final String objectName, final List<String> partValues, final String columnName) {
        this();
        this.objectType = objectType;
        this.dbName = dbName;
        this.objectName = objectName;
        this.partValues = partValues;
        this.columnName = columnName;
    }
    
    public HiveObjectRef(final HiveObjectRef other) {
        if (other.isSetObjectType()) {
            this.objectType = other.objectType;
        }
        if (other.isSetDbName()) {
            this.dbName = other.dbName;
        }
        if (other.isSetObjectName()) {
            this.objectName = other.objectName;
        }
        if (other.isSetPartValues()) {
            final List<String> __this__partValues = new ArrayList<String>();
            for (final String other_element : other.partValues) {
                __this__partValues.add(other_element);
            }
            this.partValues = __this__partValues;
        }
        if (other.isSetColumnName()) {
            this.columnName = other.columnName;
        }
    }
    
    @Override
    public HiveObjectRef deepCopy() {
        return new HiveObjectRef(this);
    }
    
    @Override
    public void clear() {
        this.objectType = null;
        this.dbName = null;
        this.objectName = null;
        this.partValues = null;
        this.columnName = null;
    }
    
    public HiveObjectType getObjectType() {
        return this.objectType;
    }
    
    public void setObjectType(final HiveObjectType objectType) {
        this.objectType = objectType;
    }
    
    public void unsetObjectType() {
        this.objectType = null;
    }
    
    public boolean isSetObjectType() {
        return this.objectType != null;
    }
    
    public void setObjectTypeIsSet(final boolean value) {
        if (!value) {
            this.objectType = null;
        }
    }
    
    public String getDbName() {
        return this.dbName;
    }
    
    public void setDbName(final String dbName) {
        this.dbName = dbName;
    }
    
    public void unsetDbName() {
        this.dbName = null;
    }
    
    public boolean isSetDbName() {
        return this.dbName != null;
    }
    
    public void setDbNameIsSet(final boolean value) {
        if (!value) {
            this.dbName = null;
        }
    }
    
    public String getObjectName() {
        return this.objectName;
    }
    
    public void setObjectName(final String objectName) {
        this.objectName = objectName;
    }
    
    public void unsetObjectName() {
        this.objectName = null;
    }
    
    public boolean isSetObjectName() {
        return this.objectName != null;
    }
    
    public void setObjectNameIsSet(final boolean value) {
        if (!value) {
            this.objectName = null;
        }
    }
    
    public int getPartValuesSize() {
        return (this.partValues == null) ? 0 : this.partValues.size();
    }
    
    public Iterator<String> getPartValuesIterator() {
        return (this.partValues == null) ? null : this.partValues.iterator();
    }
    
    public void addToPartValues(final String elem) {
        if (this.partValues == null) {
            this.partValues = new ArrayList<String>();
        }
        this.partValues.add(elem);
    }
    
    public List<String> getPartValues() {
        return this.partValues;
    }
    
    public void setPartValues(final List<String> partValues) {
        this.partValues = partValues;
    }
    
    public void unsetPartValues() {
        this.partValues = null;
    }
    
    public boolean isSetPartValues() {
        return this.partValues != null;
    }
    
    public void setPartValuesIsSet(final boolean value) {
        if (!value) {
            this.partValues = null;
        }
    }
    
    public String getColumnName() {
        return this.columnName;
    }
    
    public void setColumnName(final String columnName) {
        this.columnName = columnName;
    }
    
    public void unsetColumnName() {
        this.columnName = null;
    }
    
    public boolean isSetColumnName() {
        return this.columnName != null;
    }
    
    public void setColumnNameIsSet(final boolean value) {
        if (!value) {
            this.columnName = null;
        }
    }
    
    @Override
    public void setFieldValue(final _Fields field, final Object value) {
        switch (field) {
            case OBJECT_TYPE: {
                if (value == null) {
                    this.unsetObjectType();
                    break;
                }
                this.setObjectType((HiveObjectType)value);
                break;
            }
            case DB_NAME: {
                if (value == null) {
                    this.unsetDbName();
                    break;
                }
                this.setDbName((String)value);
                break;
            }
            case OBJECT_NAME: {
                if (value == null) {
                    this.unsetObjectName();
                    break;
                }
                this.setObjectName((String)value);
                break;
            }
            case PART_VALUES: {
                if (value == null) {
                    this.unsetPartValues();
                    break;
                }
                this.setPartValues((List<String>)value);
                break;
            }
            case COLUMN_NAME: {
                if (value == null) {
                    this.unsetColumnName();
                    break;
                }
                this.setColumnName((String)value);
                break;
            }
        }
    }
    
    @Override
    public Object getFieldValue(final _Fields field) {
        switch (field) {
            case OBJECT_TYPE: {
                return this.getObjectType();
            }
            case DB_NAME: {
                return this.getDbName();
            }
            case OBJECT_NAME: {
                return this.getObjectName();
            }
            case PART_VALUES: {
                return this.getPartValues();
            }
            case COLUMN_NAME: {
                return this.getColumnName();
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
            case OBJECT_TYPE: {
                return this.isSetObjectType();
            }
            case DB_NAME: {
                return this.isSetDbName();
            }
            case OBJECT_NAME: {
                return this.isSetObjectName();
            }
            case PART_VALUES: {
                return this.isSetPartValues();
            }
            case COLUMN_NAME: {
                return this.isSetColumnName();
            }
            default: {
                throw new IllegalStateException();
            }
        }
    }
    
    @Override
    public boolean equals(final Object that) {
        return that != null && that instanceof HiveObjectRef && this.equals((HiveObjectRef)that);
    }
    
    public boolean equals(final HiveObjectRef that) {
        if (that == null) {
            return false;
        }
        final boolean this_present_objectType = this.isSetObjectType();
        final boolean that_present_objectType = that.isSetObjectType();
        if (this_present_objectType || that_present_objectType) {
            if (!this_present_objectType || !that_present_objectType) {
                return false;
            }
            if (!this.objectType.equals(that.objectType)) {
                return false;
            }
        }
        final boolean this_present_dbName = this.isSetDbName();
        final boolean that_present_dbName = that.isSetDbName();
        if (this_present_dbName || that_present_dbName) {
            if (!this_present_dbName || !that_present_dbName) {
                return false;
            }
            if (!this.dbName.equals(that.dbName)) {
                return false;
            }
        }
        final boolean this_present_objectName = this.isSetObjectName();
        final boolean that_present_objectName = that.isSetObjectName();
        if (this_present_objectName || that_present_objectName) {
            if (!this_present_objectName || !that_present_objectName) {
                return false;
            }
            if (!this.objectName.equals(that.objectName)) {
                return false;
            }
        }
        final boolean this_present_partValues = this.isSetPartValues();
        final boolean that_present_partValues = that.isSetPartValues();
        if (this_present_partValues || that_present_partValues) {
            if (!this_present_partValues || !that_present_partValues) {
                return false;
            }
            if (!this.partValues.equals(that.partValues)) {
                return false;
            }
        }
        final boolean this_present_columnName = this.isSetColumnName();
        final boolean that_present_columnName = that.isSetColumnName();
        if (this_present_columnName || that_present_columnName) {
            if (!this_present_columnName || !that_present_columnName) {
                return false;
            }
            if (!this.columnName.equals(that.columnName)) {
                return false;
            }
        }
        return true;
    }
    
    @Override
    public int hashCode() {
        final HashCodeBuilder builder = new HashCodeBuilder();
        final boolean present_objectType = this.isSetObjectType();
        builder.append(present_objectType);
        if (present_objectType) {
            builder.append(this.objectType.getValue());
        }
        final boolean present_dbName = this.isSetDbName();
        builder.append(present_dbName);
        if (present_dbName) {
            builder.append(this.dbName);
        }
        final boolean present_objectName = this.isSetObjectName();
        builder.append(present_objectName);
        if (present_objectName) {
            builder.append(this.objectName);
        }
        final boolean present_partValues = this.isSetPartValues();
        builder.append(present_partValues);
        if (present_partValues) {
            builder.append(this.partValues);
        }
        final boolean present_columnName = this.isSetColumnName();
        builder.append(present_columnName);
        if (present_columnName) {
            builder.append(this.columnName);
        }
        return builder.toHashCode();
    }
    
    @Override
    public int compareTo(final HiveObjectRef other) {
        if (!this.getClass().equals(other.getClass())) {
            return this.getClass().getName().compareTo(other.getClass().getName());
        }
        int lastComparison = 0;
        final HiveObjectRef typedOther = other;
        lastComparison = Boolean.valueOf(this.isSetObjectType()).compareTo(Boolean.valueOf(typedOther.isSetObjectType()));
        if (lastComparison != 0) {
            return lastComparison;
        }
        if (this.isSetObjectType()) {
            lastComparison = TBaseHelper.compareTo(this.objectType, typedOther.objectType);
            if (lastComparison != 0) {
                return lastComparison;
            }
        }
        lastComparison = Boolean.valueOf(this.isSetDbName()).compareTo(Boolean.valueOf(typedOther.isSetDbName()));
        if (lastComparison != 0) {
            return lastComparison;
        }
        if (this.isSetDbName()) {
            lastComparison = TBaseHelper.compareTo(this.dbName, typedOther.dbName);
            if (lastComparison != 0) {
                return lastComparison;
            }
        }
        lastComparison = Boolean.valueOf(this.isSetObjectName()).compareTo(Boolean.valueOf(typedOther.isSetObjectName()));
        if (lastComparison != 0) {
            return lastComparison;
        }
        if (this.isSetObjectName()) {
            lastComparison = TBaseHelper.compareTo(this.objectName, typedOther.objectName);
            if (lastComparison != 0) {
                return lastComparison;
            }
        }
        lastComparison = Boolean.valueOf(this.isSetPartValues()).compareTo(Boolean.valueOf(typedOther.isSetPartValues()));
        if (lastComparison != 0) {
            return lastComparison;
        }
        if (this.isSetPartValues()) {
            lastComparison = TBaseHelper.compareTo(this.partValues, typedOther.partValues);
            if (lastComparison != 0) {
                return lastComparison;
            }
        }
        lastComparison = Boolean.valueOf(this.isSetColumnName()).compareTo(Boolean.valueOf(typedOther.isSetColumnName()));
        if (lastComparison != 0) {
            return lastComparison;
        }
        if (this.isSetColumnName()) {
            lastComparison = TBaseHelper.compareTo(this.columnName, typedOther.columnName);
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
        HiveObjectRef.schemes.get(iprot.getScheme()).getScheme().read(iprot, this);
    }
    
    @Override
    public void write(final TProtocol oprot) throws TException {
        HiveObjectRef.schemes.get(oprot.getScheme()).getScheme().write(oprot, this);
    }
    
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("HiveObjectRef(");
        boolean first = true;
        sb.append("objectType:");
        if (this.objectType == null) {
            sb.append("null");
        }
        else {
            sb.append(this.objectType);
        }
        first = false;
        if (!first) {
            sb.append(", ");
        }
        sb.append("dbName:");
        if (this.dbName == null) {
            sb.append("null");
        }
        else {
            sb.append(this.dbName);
        }
        first = false;
        if (!first) {
            sb.append(", ");
        }
        sb.append("objectName:");
        if (this.objectName == null) {
            sb.append("null");
        }
        else {
            sb.append(this.objectName);
        }
        first = false;
        if (!first) {
            sb.append(", ");
        }
        sb.append("partValues:");
        if (this.partValues == null) {
            sb.append("null");
        }
        else {
            sb.append(this.partValues);
        }
        first = false;
        if (!first) {
            sb.append(", ");
        }
        sb.append("columnName:");
        if (this.columnName == null) {
            sb.append("null");
        }
        else {
            sb.append(this.columnName);
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
        STRUCT_DESC = new TStruct("HiveObjectRef");
        OBJECT_TYPE_FIELD_DESC = new TField("objectType", (byte)8, (short)1);
        DB_NAME_FIELD_DESC = new TField("dbName", (byte)11, (short)2);
        OBJECT_NAME_FIELD_DESC = new TField("objectName", (byte)11, (short)3);
        PART_VALUES_FIELD_DESC = new TField("partValues", (byte)15, (short)4);
        COLUMN_NAME_FIELD_DESC = new TField("columnName", (byte)11, (short)5);
        (schemes = new HashMap<Class<? extends IScheme>, SchemeFactory>()).put(StandardScheme.class, new HiveObjectRefStandardSchemeFactory());
        HiveObjectRef.schemes.put(TupleScheme.class, new HiveObjectRefTupleSchemeFactory());
        final Map<_Fields, FieldMetaData> tmpMap = new EnumMap<_Fields, FieldMetaData>(_Fields.class);
        tmpMap.put(_Fields.OBJECT_TYPE, new FieldMetaData("objectType", (byte)3, new EnumMetaData((byte)16, HiveObjectType.class)));
        tmpMap.put(_Fields.DB_NAME, new FieldMetaData("dbName", (byte)3, new FieldValueMetaData((byte)11)));
        tmpMap.put(_Fields.OBJECT_NAME, new FieldMetaData("objectName", (byte)3, new FieldValueMetaData((byte)11)));
        tmpMap.put(_Fields.PART_VALUES, new FieldMetaData("partValues", (byte)3, new ListMetaData((byte)15, new FieldValueMetaData((byte)11))));
        tmpMap.put(_Fields.COLUMN_NAME, new FieldMetaData("columnName", (byte)3, new FieldValueMetaData((byte)11)));
        FieldMetaData.addStructMetaDataMap(HiveObjectRef.class, metaDataMap = Collections.unmodifiableMap((Map<? extends _Fields, ? extends FieldMetaData>)tmpMap));
    }
    
    public enum _Fields implements TFieldIdEnum
    {
        OBJECT_TYPE((short)1, "objectType"), 
        DB_NAME((short)2, "dbName"), 
        OBJECT_NAME((short)3, "objectName"), 
        PART_VALUES((short)4, "partValues"), 
        COLUMN_NAME((short)5, "columnName");
        
        private static final Map<String, _Fields> byName;
        private final short _thriftId;
        private final String _fieldName;
        
        public static _Fields findByThriftId(final int fieldId) {
            switch (fieldId) {
                case 1: {
                    return _Fields.OBJECT_TYPE;
                }
                case 2: {
                    return _Fields.DB_NAME;
                }
                case 3: {
                    return _Fields.OBJECT_NAME;
                }
                case 4: {
                    return _Fields.PART_VALUES;
                }
                case 5: {
                    return _Fields.COLUMN_NAME;
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
    
    private static class HiveObjectRefStandardSchemeFactory implements SchemeFactory
    {
        @Override
        public HiveObjectRefStandardScheme getScheme() {
            return new HiveObjectRefStandardScheme();
        }
    }
    
    private static class HiveObjectRefStandardScheme extends StandardScheme<HiveObjectRef>
    {
        @Override
        public void read(final TProtocol iprot, final HiveObjectRef struct) throws TException {
            iprot.readStructBegin();
            while (true) {
                final TField schemeField = iprot.readFieldBegin();
                if (schemeField.type == 0) {
                    break;
                }
                switch (schemeField.id) {
                    case 1: {
                        if (schemeField.type == 8) {
                            struct.objectType = HiveObjectType.findByValue(iprot.readI32());
                            struct.setObjectTypeIsSet(true);
                            break;
                        }
                        TProtocolUtil.skip(iprot, schemeField.type);
                        break;
                    }
                    case 2: {
                        if (schemeField.type == 11) {
                            struct.dbName = iprot.readString();
                            struct.setDbNameIsSet(true);
                            break;
                        }
                        TProtocolUtil.skip(iprot, schemeField.type);
                        break;
                    }
                    case 3: {
                        if (schemeField.type == 11) {
                            struct.objectName = iprot.readString();
                            struct.setObjectNameIsSet(true);
                            break;
                        }
                        TProtocolUtil.skip(iprot, schemeField.type);
                        break;
                    }
                    case 4: {
                        if (schemeField.type == 15) {
                            final TList _list8 = iprot.readListBegin();
                            struct.partValues = (List<String>)new ArrayList(_list8.size);
                            for (int _i9 = 0; _i9 < _list8.size; ++_i9) {
                                final String _elem10 = iprot.readString();
                                struct.partValues.add(_elem10);
                            }
                            iprot.readListEnd();
                            struct.setPartValuesIsSet(true);
                            break;
                        }
                        TProtocolUtil.skip(iprot, schemeField.type);
                        break;
                    }
                    case 5: {
                        if (schemeField.type == 11) {
                            struct.columnName = iprot.readString();
                            struct.setColumnNameIsSet(true);
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
        public void write(final TProtocol oprot, final HiveObjectRef struct) throws TException {
            struct.validate();
            oprot.writeStructBegin(HiveObjectRef.STRUCT_DESC);
            if (struct.objectType != null) {
                oprot.writeFieldBegin(HiveObjectRef.OBJECT_TYPE_FIELD_DESC);
                oprot.writeI32(struct.objectType.getValue());
                oprot.writeFieldEnd();
            }
            if (struct.dbName != null) {
                oprot.writeFieldBegin(HiveObjectRef.DB_NAME_FIELD_DESC);
                oprot.writeString(struct.dbName);
                oprot.writeFieldEnd();
            }
            if (struct.objectName != null) {
                oprot.writeFieldBegin(HiveObjectRef.OBJECT_NAME_FIELD_DESC);
                oprot.writeString(struct.objectName);
                oprot.writeFieldEnd();
            }
            if (struct.partValues != null) {
                oprot.writeFieldBegin(HiveObjectRef.PART_VALUES_FIELD_DESC);
                oprot.writeListBegin(new TList((byte)11, struct.partValues.size()));
                for (final String _iter11 : struct.partValues) {
                    oprot.writeString(_iter11);
                }
                oprot.writeListEnd();
                oprot.writeFieldEnd();
            }
            if (struct.columnName != null) {
                oprot.writeFieldBegin(HiveObjectRef.COLUMN_NAME_FIELD_DESC);
                oprot.writeString(struct.columnName);
                oprot.writeFieldEnd();
            }
            oprot.writeFieldStop();
            oprot.writeStructEnd();
        }
    }
    
    private static class HiveObjectRefTupleSchemeFactory implements SchemeFactory
    {
        @Override
        public HiveObjectRefTupleScheme getScheme() {
            return new HiveObjectRefTupleScheme();
        }
    }
    
    private static class HiveObjectRefTupleScheme extends TupleScheme<HiveObjectRef>
    {
        @Override
        public void write(final TProtocol prot, final HiveObjectRef struct) throws TException {
            final TTupleProtocol oprot = (TTupleProtocol)prot;
            final BitSet optionals = new BitSet();
            if (struct.isSetObjectType()) {
                optionals.set(0);
            }
            if (struct.isSetDbName()) {
                optionals.set(1);
            }
            if (struct.isSetObjectName()) {
                optionals.set(2);
            }
            if (struct.isSetPartValues()) {
                optionals.set(3);
            }
            if (struct.isSetColumnName()) {
                optionals.set(4);
            }
            oprot.writeBitSet(optionals, 5);
            if (struct.isSetObjectType()) {
                oprot.writeI32(struct.objectType.getValue());
            }
            if (struct.isSetDbName()) {
                oprot.writeString(struct.dbName);
            }
            if (struct.isSetObjectName()) {
                oprot.writeString(struct.objectName);
            }
            if (struct.isSetPartValues()) {
                oprot.writeI32(struct.partValues.size());
                for (final String _iter12 : struct.partValues) {
                    oprot.writeString(_iter12);
                }
            }
            if (struct.isSetColumnName()) {
                oprot.writeString(struct.columnName);
            }
        }
        
        @Override
        public void read(final TProtocol prot, final HiveObjectRef struct) throws TException {
            final TTupleProtocol iprot = (TTupleProtocol)prot;
            final BitSet incoming = iprot.readBitSet(5);
            if (incoming.get(0)) {
                struct.objectType = HiveObjectType.findByValue(iprot.readI32());
                struct.setObjectTypeIsSet(true);
            }
            if (incoming.get(1)) {
                struct.dbName = iprot.readString();
                struct.setDbNameIsSet(true);
            }
            if (incoming.get(2)) {
                struct.objectName = iprot.readString();
                struct.setObjectNameIsSet(true);
            }
            if (incoming.get(3)) {
                final TList _list13 = new TList((byte)11, iprot.readI32());
                struct.partValues = (List<String>)new ArrayList(_list13.size);
                for (int _i14 = 0; _i14 < _list13.size; ++_i14) {
                    final String _elem15 = iprot.readString();
                    struct.partValues.add(_elem15);
                }
                struct.setPartValuesIsSet(true);
            }
            if (incoming.get(4)) {
                struct.columnName = iprot.readString();
                struct.setColumnNameIsSet(true);
            }
        }
    }
}
