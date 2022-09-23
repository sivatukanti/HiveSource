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
import org.apache.thrift.protocol.TProtocolException;
import org.apache.thrift.TException;
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

public class LockComponent implements TBase<LockComponent, _Fields>, Serializable, Cloneable
{
    private static final TStruct STRUCT_DESC;
    private static final TField TYPE_FIELD_DESC;
    private static final TField LEVEL_FIELD_DESC;
    private static final TField DBNAME_FIELD_DESC;
    private static final TField TABLENAME_FIELD_DESC;
    private static final TField PARTITIONNAME_FIELD_DESC;
    private static final Map<Class<? extends IScheme>, SchemeFactory> schemes;
    private LockType type;
    private LockLevel level;
    private String dbname;
    private String tablename;
    private String partitionname;
    private _Fields[] optionals;
    public static final Map<_Fields, FieldMetaData> metaDataMap;
    
    public LockComponent() {
        this.optionals = new _Fields[] { _Fields.TABLENAME, _Fields.PARTITIONNAME };
    }
    
    public LockComponent(final LockType type, final LockLevel level, final String dbname) {
        this();
        this.type = type;
        this.level = level;
        this.dbname = dbname;
    }
    
    public LockComponent(final LockComponent other) {
        this.optionals = new _Fields[] { _Fields.TABLENAME, _Fields.PARTITIONNAME };
        if (other.isSetType()) {
            this.type = other.type;
        }
        if (other.isSetLevel()) {
            this.level = other.level;
        }
        if (other.isSetDbname()) {
            this.dbname = other.dbname;
        }
        if (other.isSetTablename()) {
            this.tablename = other.tablename;
        }
        if (other.isSetPartitionname()) {
            this.partitionname = other.partitionname;
        }
    }
    
    @Override
    public LockComponent deepCopy() {
        return new LockComponent(this);
    }
    
    @Override
    public void clear() {
        this.type = null;
        this.level = null;
        this.dbname = null;
        this.tablename = null;
        this.partitionname = null;
    }
    
    public LockType getType() {
        return this.type;
    }
    
    public void setType(final LockType type) {
        this.type = type;
    }
    
    public void unsetType() {
        this.type = null;
    }
    
    public boolean isSetType() {
        return this.type != null;
    }
    
    public void setTypeIsSet(final boolean value) {
        if (!value) {
            this.type = null;
        }
    }
    
    public LockLevel getLevel() {
        return this.level;
    }
    
    public void setLevel(final LockLevel level) {
        this.level = level;
    }
    
    public void unsetLevel() {
        this.level = null;
    }
    
    public boolean isSetLevel() {
        return this.level != null;
    }
    
    public void setLevelIsSet(final boolean value) {
        if (!value) {
            this.level = null;
        }
    }
    
    public String getDbname() {
        return this.dbname;
    }
    
    public void setDbname(final String dbname) {
        this.dbname = dbname;
    }
    
    public void unsetDbname() {
        this.dbname = null;
    }
    
    public boolean isSetDbname() {
        return this.dbname != null;
    }
    
    public void setDbnameIsSet(final boolean value) {
        if (!value) {
            this.dbname = null;
        }
    }
    
    public String getTablename() {
        return this.tablename;
    }
    
    public void setTablename(final String tablename) {
        this.tablename = tablename;
    }
    
    public void unsetTablename() {
        this.tablename = null;
    }
    
    public boolean isSetTablename() {
        return this.tablename != null;
    }
    
    public void setTablenameIsSet(final boolean value) {
        if (!value) {
            this.tablename = null;
        }
    }
    
    public String getPartitionname() {
        return this.partitionname;
    }
    
    public void setPartitionname(final String partitionname) {
        this.partitionname = partitionname;
    }
    
    public void unsetPartitionname() {
        this.partitionname = null;
    }
    
    public boolean isSetPartitionname() {
        return this.partitionname != null;
    }
    
    public void setPartitionnameIsSet(final boolean value) {
        if (!value) {
            this.partitionname = null;
        }
    }
    
    @Override
    public void setFieldValue(final _Fields field, final Object value) {
        switch (field) {
            case TYPE: {
                if (value == null) {
                    this.unsetType();
                    break;
                }
                this.setType((LockType)value);
                break;
            }
            case LEVEL: {
                if (value == null) {
                    this.unsetLevel();
                    break;
                }
                this.setLevel((LockLevel)value);
                break;
            }
            case DBNAME: {
                if (value == null) {
                    this.unsetDbname();
                    break;
                }
                this.setDbname((String)value);
                break;
            }
            case TABLENAME: {
                if (value == null) {
                    this.unsetTablename();
                    break;
                }
                this.setTablename((String)value);
                break;
            }
            case PARTITIONNAME: {
                if (value == null) {
                    this.unsetPartitionname();
                    break;
                }
                this.setPartitionname((String)value);
                break;
            }
        }
    }
    
    @Override
    public Object getFieldValue(final _Fields field) {
        switch (field) {
            case TYPE: {
                return this.getType();
            }
            case LEVEL: {
                return this.getLevel();
            }
            case DBNAME: {
                return this.getDbname();
            }
            case TABLENAME: {
                return this.getTablename();
            }
            case PARTITIONNAME: {
                return this.getPartitionname();
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
            case TYPE: {
                return this.isSetType();
            }
            case LEVEL: {
                return this.isSetLevel();
            }
            case DBNAME: {
                return this.isSetDbname();
            }
            case TABLENAME: {
                return this.isSetTablename();
            }
            case PARTITIONNAME: {
                return this.isSetPartitionname();
            }
            default: {
                throw new IllegalStateException();
            }
        }
    }
    
    @Override
    public boolean equals(final Object that) {
        return that != null && that instanceof LockComponent && this.equals((LockComponent)that);
    }
    
    public boolean equals(final LockComponent that) {
        if (that == null) {
            return false;
        }
        final boolean this_present_type = this.isSetType();
        final boolean that_present_type = that.isSetType();
        if (this_present_type || that_present_type) {
            if (!this_present_type || !that_present_type) {
                return false;
            }
            if (!this.type.equals(that.type)) {
                return false;
            }
        }
        final boolean this_present_level = this.isSetLevel();
        final boolean that_present_level = that.isSetLevel();
        if (this_present_level || that_present_level) {
            if (!this_present_level || !that_present_level) {
                return false;
            }
            if (!this.level.equals(that.level)) {
                return false;
            }
        }
        final boolean this_present_dbname = this.isSetDbname();
        final boolean that_present_dbname = that.isSetDbname();
        if (this_present_dbname || that_present_dbname) {
            if (!this_present_dbname || !that_present_dbname) {
                return false;
            }
            if (!this.dbname.equals(that.dbname)) {
                return false;
            }
        }
        final boolean this_present_tablename = this.isSetTablename();
        final boolean that_present_tablename = that.isSetTablename();
        if (this_present_tablename || that_present_tablename) {
            if (!this_present_tablename || !that_present_tablename) {
                return false;
            }
            if (!this.tablename.equals(that.tablename)) {
                return false;
            }
        }
        final boolean this_present_partitionname = this.isSetPartitionname();
        final boolean that_present_partitionname = that.isSetPartitionname();
        if (this_present_partitionname || that_present_partitionname) {
            if (!this_present_partitionname || !that_present_partitionname) {
                return false;
            }
            if (!this.partitionname.equals(that.partitionname)) {
                return false;
            }
        }
        return true;
    }
    
    @Override
    public int hashCode() {
        final HashCodeBuilder builder = new HashCodeBuilder();
        final boolean present_type = this.isSetType();
        builder.append(present_type);
        if (present_type) {
            builder.append(this.type.getValue());
        }
        final boolean present_level = this.isSetLevel();
        builder.append(present_level);
        if (present_level) {
            builder.append(this.level.getValue());
        }
        final boolean present_dbname = this.isSetDbname();
        builder.append(present_dbname);
        if (present_dbname) {
            builder.append(this.dbname);
        }
        final boolean present_tablename = this.isSetTablename();
        builder.append(present_tablename);
        if (present_tablename) {
            builder.append(this.tablename);
        }
        final boolean present_partitionname = this.isSetPartitionname();
        builder.append(present_partitionname);
        if (present_partitionname) {
            builder.append(this.partitionname);
        }
        return builder.toHashCode();
    }
    
    @Override
    public int compareTo(final LockComponent other) {
        if (!this.getClass().equals(other.getClass())) {
            return this.getClass().getName().compareTo(other.getClass().getName());
        }
        int lastComparison = 0;
        final LockComponent typedOther = other;
        lastComparison = Boolean.valueOf(this.isSetType()).compareTo(Boolean.valueOf(typedOther.isSetType()));
        if (lastComparison != 0) {
            return lastComparison;
        }
        if (this.isSetType()) {
            lastComparison = TBaseHelper.compareTo(this.type, typedOther.type);
            if (lastComparison != 0) {
                return lastComparison;
            }
        }
        lastComparison = Boolean.valueOf(this.isSetLevel()).compareTo(Boolean.valueOf(typedOther.isSetLevel()));
        if (lastComparison != 0) {
            return lastComparison;
        }
        if (this.isSetLevel()) {
            lastComparison = TBaseHelper.compareTo(this.level, typedOther.level);
            if (lastComparison != 0) {
                return lastComparison;
            }
        }
        lastComparison = Boolean.valueOf(this.isSetDbname()).compareTo(Boolean.valueOf(typedOther.isSetDbname()));
        if (lastComparison != 0) {
            return lastComparison;
        }
        if (this.isSetDbname()) {
            lastComparison = TBaseHelper.compareTo(this.dbname, typedOther.dbname);
            if (lastComparison != 0) {
                return lastComparison;
            }
        }
        lastComparison = Boolean.valueOf(this.isSetTablename()).compareTo(Boolean.valueOf(typedOther.isSetTablename()));
        if (lastComparison != 0) {
            return lastComparison;
        }
        if (this.isSetTablename()) {
            lastComparison = TBaseHelper.compareTo(this.tablename, typedOther.tablename);
            if (lastComparison != 0) {
                return lastComparison;
            }
        }
        lastComparison = Boolean.valueOf(this.isSetPartitionname()).compareTo(Boolean.valueOf(typedOther.isSetPartitionname()));
        if (lastComparison != 0) {
            return lastComparison;
        }
        if (this.isSetPartitionname()) {
            lastComparison = TBaseHelper.compareTo(this.partitionname, typedOther.partitionname);
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
        LockComponent.schemes.get(iprot.getScheme()).getScheme().read(iprot, this);
    }
    
    @Override
    public void write(final TProtocol oprot) throws TException {
        LockComponent.schemes.get(oprot.getScheme()).getScheme().write(oprot, this);
    }
    
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("LockComponent(");
        boolean first = true;
        sb.append("type:");
        if (this.type == null) {
            sb.append("null");
        }
        else {
            sb.append(this.type);
        }
        first = false;
        if (!first) {
            sb.append(", ");
        }
        sb.append("level:");
        if (this.level == null) {
            sb.append("null");
        }
        else {
            sb.append(this.level);
        }
        first = false;
        if (!first) {
            sb.append(", ");
        }
        sb.append("dbname:");
        if (this.dbname == null) {
            sb.append("null");
        }
        else {
            sb.append(this.dbname);
        }
        first = false;
        if (this.isSetTablename()) {
            if (!first) {
                sb.append(", ");
            }
            sb.append("tablename:");
            if (this.tablename == null) {
                sb.append("null");
            }
            else {
                sb.append(this.tablename);
            }
            first = false;
        }
        if (this.isSetPartitionname()) {
            if (!first) {
                sb.append(", ");
            }
            sb.append("partitionname:");
            if (this.partitionname == null) {
                sb.append("null");
            }
            else {
                sb.append(this.partitionname);
            }
            first = false;
        }
        sb.append(")");
        return sb.toString();
    }
    
    public void validate() throws TException {
        if (!this.isSetType()) {
            throw new TProtocolException("Required field 'type' is unset! Struct:" + this.toString());
        }
        if (!this.isSetLevel()) {
            throw new TProtocolException("Required field 'level' is unset! Struct:" + this.toString());
        }
        if (!this.isSetDbname()) {
            throw new TProtocolException("Required field 'dbname' is unset! Struct:" + this.toString());
        }
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
        STRUCT_DESC = new TStruct("LockComponent");
        TYPE_FIELD_DESC = new TField("type", (byte)8, (short)1);
        LEVEL_FIELD_DESC = new TField("level", (byte)8, (short)2);
        DBNAME_FIELD_DESC = new TField("dbname", (byte)11, (short)3);
        TABLENAME_FIELD_DESC = new TField("tablename", (byte)11, (short)4);
        PARTITIONNAME_FIELD_DESC = new TField("partitionname", (byte)11, (short)5);
        (schemes = new HashMap<Class<? extends IScheme>, SchemeFactory>()).put(StandardScheme.class, new LockComponentStandardSchemeFactory());
        LockComponent.schemes.put(TupleScheme.class, new LockComponentTupleSchemeFactory());
        final Map<_Fields, FieldMetaData> tmpMap = new EnumMap<_Fields, FieldMetaData>(_Fields.class);
        tmpMap.put(_Fields.TYPE, new FieldMetaData("type", (byte)1, new EnumMetaData((byte)16, LockType.class)));
        tmpMap.put(_Fields.LEVEL, new FieldMetaData("level", (byte)1, new EnumMetaData((byte)16, LockLevel.class)));
        tmpMap.put(_Fields.DBNAME, new FieldMetaData("dbname", (byte)1, new FieldValueMetaData((byte)11)));
        tmpMap.put(_Fields.TABLENAME, new FieldMetaData("tablename", (byte)2, new FieldValueMetaData((byte)11)));
        tmpMap.put(_Fields.PARTITIONNAME, new FieldMetaData("partitionname", (byte)2, new FieldValueMetaData((byte)11)));
        FieldMetaData.addStructMetaDataMap(LockComponent.class, metaDataMap = Collections.unmodifiableMap((Map<? extends _Fields, ? extends FieldMetaData>)tmpMap));
    }
    
    public enum _Fields implements TFieldIdEnum
    {
        TYPE((short)1, "type"), 
        LEVEL((short)2, "level"), 
        DBNAME((short)3, "dbname"), 
        TABLENAME((short)4, "tablename"), 
        PARTITIONNAME((short)5, "partitionname");
        
        private static final Map<String, _Fields> byName;
        private final short _thriftId;
        private final String _fieldName;
        
        public static _Fields findByThriftId(final int fieldId) {
            switch (fieldId) {
                case 1: {
                    return _Fields.TYPE;
                }
                case 2: {
                    return _Fields.LEVEL;
                }
                case 3: {
                    return _Fields.DBNAME;
                }
                case 4: {
                    return _Fields.TABLENAME;
                }
                case 5: {
                    return _Fields.PARTITIONNAME;
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
    
    private static class LockComponentStandardSchemeFactory implements SchemeFactory
    {
        @Override
        public LockComponentStandardScheme getScheme() {
            return new LockComponentStandardScheme();
        }
    }
    
    private static class LockComponentStandardScheme extends StandardScheme<LockComponent>
    {
        @Override
        public void read(final TProtocol iprot, final LockComponent struct) throws TException {
            iprot.readStructBegin();
            while (true) {
                final TField schemeField = iprot.readFieldBegin();
                if (schemeField.type == 0) {
                    break;
                }
                switch (schemeField.id) {
                    case 1: {
                        if (schemeField.type == 8) {
                            struct.type = LockType.findByValue(iprot.readI32());
                            struct.setTypeIsSet(true);
                            break;
                        }
                        TProtocolUtil.skip(iprot, schemeField.type);
                        break;
                    }
                    case 2: {
                        if (schemeField.type == 8) {
                            struct.level = LockLevel.findByValue(iprot.readI32());
                            struct.setLevelIsSet(true);
                            break;
                        }
                        TProtocolUtil.skip(iprot, schemeField.type);
                        break;
                    }
                    case 3: {
                        if (schemeField.type == 11) {
                            struct.dbname = iprot.readString();
                            struct.setDbnameIsSet(true);
                            break;
                        }
                        TProtocolUtil.skip(iprot, schemeField.type);
                        break;
                    }
                    case 4: {
                        if (schemeField.type == 11) {
                            struct.tablename = iprot.readString();
                            struct.setTablenameIsSet(true);
                            break;
                        }
                        TProtocolUtil.skip(iprot, schemeField.type);
                        break;
                    }
                    case 5: {
                        if (schemeField.type == 11) {
                            struct.partitionname = iprot.readString();
                            struct.setPartitionnameIsSet(true);
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
        public void write(final TProtocol oprot, final LockComponent struct) throws TException {
            struct.validate();
            oprot.writeStructBegin(LockComponent.STRUCT_DESC);
            if (struct.type != null) {
                oprot.writeFieldBegin(LockComponent.TYPE_FIELD_DESC);
                oprot.writeI32(struct.type.getValue());
                oprot.writeFieldEnd();
            }
            if (struct.level != null) {
                oprot.writeFieldBegin(LockComponent.LEVEL_FIELD_DESC);
                oprot.writeI32(struct.level.getValue());
                oprot.writeFieldEnd();
            }
            if (struct.dbname != null) {
                oprot.writeFieldBegin(LockComponent.DBNAME_FIELD_DESC);
                oprot.writeString(struct.dbname);
                oprot.writeFieldEnd();
            }
            if (struct.tablename != null && struct.isSetTablename()) {
                oprot.writeFieldBegin(LockComponent.TABLENAME_FIELD_DESC);
                oprot.writeString(struct.tablename);
                oprot.writeFieldEnd();
            }
            if (struct.partitionname != null && struct.isSetPartitionname()) {
                oprot.writeFieldBegin(LockComponent.PARTITIONNAME_FIELD_DESC);
                oprot.writeString(struct.partitionname);
                oprot.writeFieldEnd();
            }
            oprot.writeFieldStop();
            oprot.writeStructEnd();
        }
    }
    
    private static class LockComponentTupleSchemeFactory implements SchemeFactory
    {
        @Override
        public LockComponentTupleScheme getScheme() {
            return new LockComponentTupleScheme();
        }
    }
    
    private static class LockComponentTupleScheme extends TupleScheme<LockComponent>
    {
        @Override
        public void write(final TProtocol prot, final LockComponent struct) throws TException {
            final TTupleProtocol oprot = (TTupleProtocol)prot;
            oprot.writeI32(struct.type.getValue());
            oprot.writeI32(struct.level.getValue());
            oprot.writeString(struct.dbname);
            final BitSet optionals = new BitSet();
            if (struct.isSetTablename()) {
                optionals.set(0);
            }
            if (struct.isSetPartitionname()) {
                optionals.set(1);
            }
            oprot.writeBitSet(optionals, 2);
            if (struct.isSetTablename()) {
                oprot.writeString(struct.tablename);
            }
            if (struct.isSetPartitionname()) {
                oprot.writeString(struct.partitionname);
            }
        }
        
        @Override
        public void read(final TProtocol prot, final LockComponent struct) throws TException {
            final TTupleProtocol iprot = (TTupleProtocol)prot;
            struct.type = LockType.findByValue(iprot.readI32());
            struct.setTypeIsSet(true);
            struct.level = LockLevel.findByValue(iprot.readI32());
            struct.setLevelIsSet(true);
            struct.dbname = iprot.readString();
            struct.setDbnameIsSet(true);
            final BitSet incoming = iprot.readBitSet(2);
            if (incoming.get(0)) {
                struct.tablename = iprot.readString();
                struct.setTablenameIsSet(true);
            }
            if (incoming.get(1)) {
                struct.partitionname = iprot.readString();
                struct.setPartitionnameIsSet(true);
            }
        }
    }
}
