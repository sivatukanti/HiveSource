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
import org.apache.thrift.EncodingUtils;
import org.apache.thrift.meta_data.FieldMetaData;
import org.apache.thrift.scheme.SchemeFactory;
import org.apache.thrift.scheme.IScheme;
import java.util.Map;
import org.apache.thrift.protocol.TField;
import org.apache.thrift.protocol.TStruct;
import java.io.Serializable;
import org.apache.thrift.TBase;

public class ColumnStatisticsDesc implements TBase<ColumnStatisticsDesc, _Fields>, Serializable, Cloneable
{
    private static final TStruct STRUCT_DESC;
    private static final TField IS_TBL_LEVEL_FIELD_DESC;
    private static final TField DB_NAME_FIELD_DESC;
    private static final TField TABLE_NAME_FIELD_DESC;
    private static final TField PART_NAME_FIELD_DESC;
    private static final TField LAST_ANALYZED_FIELD_DESC;
    private static final Map<Class<? extends IScheme>, SchemeFactory> schemes;
    private boolean isTblLevel;
    private String dbName;
    private String tableName;
    private String partName;
    private long lastAnalyzed;
    private static final int __ISTBLLEVEL_ISSET_ID = 0;
    private static final int __LASTANALYZED_ISSET_ID = 1;
    private byte __isset_bitfield;
    private _Fields[] optionals;
    public static final Map<_Fields, FieldMetaData> metaDataMap;
    
    public ColumnStatisticsDesc() {
        this.__isset_bitfield = 0;
        this.optionals = new _Fields[] { _Fields.PART_NAME, _Fields.LAST_ANALYZED };
    }
    
    public ColumnStatisticsDesc(final boolean isTblLevel, final String dbName, final String tableName) {
        this();
        this.isTblLevel = isTblLevel;
        this.setIsTblLevelIsSet(true);
        this.dbName = dbName;
        this.tableName = tableName;
    }
    
    public ColumnStatisticsDesc(final ColumnStatisticsDesc other) {
        this.__isset_bitfield = 0;
        this.optionals = new _Fields[] { _Fields.PART_NAME, _Fields.LAST_ANALYZED };
        this.__isset_bitfield = other.__isset_bitfield;
        this.isTblLevel = other.isTblLevel;
        if (other.isSetDbName()) {
            this.dbName = other.dbName;
        }
        if (other.isSetTableName()) {
            this.tableName = other.tableName;
        }
        if (other.isSetPartName()) {
            this.partName = other.partName;
        }
        this.lastAnalyzed = other.lastAnalyzed;
    }
    
    @Override
    public ColumnStatisticsDesc deepCopy() {
        return new ColumnStatisticsDesc(this);
    }
    
    @Override
    public void clear() {
        this.setIsTblLevelIsSet(false);
        this.isTblLevel = false;
        this.dbName = null;
        this.tableName = null;
        this.partName = null;
        this.setLastAnalyzedIsSet(false);
        this.lastAnalyzed = 0L;
    }
    
    public boolean isIsTblLevel() {
        return this.isTblLevel;
    }
    
    public void setIsTblLevel(final boolean isTblLevel) {
        this.isTblLevel = isTblLevel;
        this.setIsTblLevelIsSet(true);
    }
    
    public void unsetIsTblLevel() {
        this.__isset_bitfield = EncodingUtils.clearBit(this.__isset_bitfield, 0);
    }
    
    public boolean isSetIsTblLevel() {
        return EncodingUtils.testBit(this.__isset_bitfield, 0);
    }
    
    public void setIsTblLevelIsSet(final boolean value) {
        this.__isset_bitfield = EncodingUtils.setBit(this.__isset_bitfield, 0, value);
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
    
    public String getTableName() {
        return this.tableName;
    }
    
    public void setTableName(final String tableName) {
        this.tableName = tableName;
    }
    
    public void unsetTableName() {
        this.tableName = null;
    }
    
    public boolean isSetTableName() {
        return this.tableName != null;
    }
    
    public void setTableNameIsSet(final boolean value) {
        if (!value) {
            this.tableName = null;
        }
    }
    
    public String getPartName() {
        return this.partName;
    }
    
    public void setPartName(final String partName) {
        this.partName = partName;
    }
    
    public void unsetPartName() {
        this.partName = null;
    }
    
    public boolean isSetPartName() {
        return this.partName != null;
    }
    
    public void setPartNameIsSet(final boolean value) {
        if (!value) {
            this.partName = null;
        }
    }
    
    public long getLastAnalyzed() {
        return this.lastAnalyzed;
    }
    
    public void setLastAnalyzed(final long lastAnalyzed) {
        this.lastAnalyzed = lastAnalyzed;
        this.setLastAnalyzedIsSet(true);
    }
    
    public void unsetLastAnalyzed() {
        this.__isset_bitfield = EncodingUtils.clearBit(this.__isset_bitfield, 1);
    }
    
    public boolean isSetLastAnalyzed() {
        return EncodingUtils.testBit(this.__isset_bitfield, 1);
    }
    
    public void setLastAnalyzedIsSet(final boolean value) {
        this.__isset_bitfield = EncodingUtils.setBit(this.__isset_bitfield, 1, value);
    }
    
    @Override
    public void setFieldValue(final _Fields field, final Object value) {
        switch (field) {
            case IS_TBL_LEVEL: {
                if (value == null) {
                    this.unsetIsTblLevel();
                    break;
                }
                this.setIsTblLevel((boolean)value);
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
            case TABLE_NAME: {
                if (value == null) {
                    this.unsetTableName();
                    break;
                }
                this.setTableName((String)value);
                break;
            }
            case PART_NAME: {
                if (value == null) {
                    this.unsetPartName();
                    break;
                }
                this.setPartName((String)value);
                break;
            }
            case LAST_ANALYZED: {
                if (value == null) {
                    this.unsetLastAnalyzed();
                    break;
                }
                this.setLastAnalyzed((long)value);
                break;
            }
        }
    }
    
    @Override
    public Object getFieldValue(final _Fields field) {
        switch (field) {
            case IS_TBL_LEVEL: {
                return this.isIsTblLevel();
            }
            case DB_NAME: {
                return this.getDbName();
            }
            case TABLE_NAME: {
                return this.getTableName();
            }
            case PART_NAME: {
                return this.getPartName();
            }
            case LAST_ANALYZED: {
                return this.getLastAnalyzed();
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
            case IS_TBL_LEVEL: {
                return this.isSetIsTblLevel();
            }
            case DB_NAME: {
                return this.isSetDbName();
            }
            case TABLE_NAME: {
                return this.isSetTableName();
            }
            case PART_NAME: {
                return this.isSetPartName();
            }
            case LAST_ANALYZED: {
                return this.isSetLastAnalyzed();
            }
            default: {
                throw new IllegalStateException();
            }
        }
    }
    
    @Override
    public boolean equals(final Object that) {
        return that != null && that instanceof ColumnStatisticsDesc && this.equals((ColumnStatisticsDesc)that);
    }
    
    public boolean equals(final ColumnStatisticsDesc that) {
        if (that == null) {
            return false;
        }
        final boolean this_present_isTblLevel = true;
        final boolean that_present_isTblLevel = true;
        if (this_present_isTblLevel || that_present_isTblLevel) {
            if (!this_present_isTblLevel || !that_present_isTblLevel) {
                return false;
            }
            if (this.isTblLevel != that.isTblLevel) {
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
        final boolean this_present_tableName = this.isSetTableName();
        final boolean that_present_tableName = that.isSetTableName();
        if (this_present_tableName || that_present_tableName) {
            if (!this_present_tableName || !that_present_tableName) {
                return false;
            }
            if (!this.tableName.equals(that.tableName)) {
                return false;
            }
        }
        final boolean this_present_partName = this.isSetPartName();
        final boolean that_present_partName = that.isSetPartName();
        if (this_present_partName || that_present_partName) {
            if (!this_present_partName || !that_present_partName) {
                return false;
            }
            if (!this.partName.equals(that.partName)) {
                return false;
            }
        }
        final boolean this_present_lastAnalyzed = this.isSetLastAnalyzed();
        final boolean that_present_lastAnalyzed = that.isSetLastAnalyzed();
        if (this_present_lastAnalyzed || that_present_lastAnalyzed) {
            if (!this_present_lastAnalyzed || !that_present_lastAnalyzed) {
                return false;
            }
            if (this.lastAnalyzed != that.lastAnalyzed) {
                return false;
            }
        }
        return true;
    }
    
    @Override
    public int hashCode() {
        final HashCodeBuilder builder = new HashCodeBuilder();
        final boolean present_isTblLevel = true;
        builder.append(present_isTblLevel);
        if (present_isTblLevel) {
            builder.append(this.isTblLevel);
        }
        final boolean present_dbName = this.isSetDbName();
        builder.append(present_dbName);
        if (present_dbName) {
            builder.append(this.dbName);
        }
        final boolean present_tableName = this.isSetTableName();
        builder.append(present_tableName);
        if (present_tableName) {
            builder.append(this.tableName);
        }
        final boolean present_partName = this.isSetPartName();
        builder.append(present_partName);
        if (present_partName) {
            builder.append(this.partName);
        }
        final boolean present_lastAnalyzed = this.isSetLastAnalyzed();
        builder.append(present_lastAnalyzed);
        if (present_lastAnalyzed) {
            builder.append(this.lastAnalyzed);
        }
        return builder.toHashCode();
    }
    
    @Override
    public int compareTo(final ColumnStatisticsDesc other) {
        if (!this.getClass().equals(other.getClass())) {
            return this.getClass().getName().compareTo(other.getClass().getName());
        }
        int lastComparison = 0;
        final ColumnStatisticsDesc typedOther = other;
        lastComparison = Boolean.valueOf(this.isSetIsTblLevel()).compareTo(Boolean.valueOf(typedOther.isSetIsTblLevel()));
        if (lastComparison != 0) {
            return lastComparison;
        }
        if (this.isSetIsTblLevel()) {
            lastComparison = TBaseHelper.compareTo(this.isTblLevel, typedOther.isTblLevel);
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
        lastComparison = Boolean.valueOf(this.isSetTableName()).compareTo(Boolean.valueOf(typedOther.isSetTableName()));
        if (lastComparison != 0) {
            return lastComparison;
        }
        if (this.isSetTableName()) {
            lastComparison = TBaseHelper.compareTo(this.tableName, typedOther.tableName);
            if (lastComparison != 0) {
                return lastComparison;
            }
        }
        lastComparison = Boolean.valueOf(this.isSetPartName()).compareTo(Boolean.valueOf(typedOther.isSetPartName()));
        if (lastComparison != 0) {
            return lastComparison;
        }
        if (this.isSetPartName()) {
            lastComparison = TBaseHelper.compareTo(this.partName, typedOther.partName);
            if (lastComparison != 0) {
                return lastComparison;
            }
        }
        lastComparison = Boolean.valueOf(this.isSetLastAnalyzed()).compareTo(Boolean.valueOf(typedOther.isSetLastAnalyzed()));
        if (lastComparison != 0) {
            return lastComparison;
        }
        if (this.isSetLastAnalyzed()) {
            lastComparison = TBaseHelper.compareTo(this.lastAnalyzed, typedOther.lastAnalyzed);
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
        ColumnStatisticsDesc.schemes.get(iprot.getScheme()).getScheme().read(iprot, this);
    }
    
    @Override
    public void write(final TProtocol oprot) throws TException {
        ColumnStatisticsDesc.schemes.get(oprot.getScheme()).getScheme().write(oprot, this);
    }
    
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("ColumnStatisticsDesc(");
        boolean first = true;
        sb.append("isTblLevel:");
        sb.append(this.isTblLevel);
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
        sb.append("tableName:");
        if (this.tableName == null) {
            sb.append("null");
        }
        else {
            sb.append(this.tableName);
        }
        first = false;
        if (this.isSetPartName()) {
            if (!first) {
                sb.append(", ");
            }
            sb.append("partName:");
            if (this.partName == null) {
                sb.append("null");
            }
            else {
                sb.append(this.partName);
            }
            first = false;
        }
        if (this.isSetLastAnalyzed()) {
            if (!first) {
                sb.append(", ");
            }
            sb.append("lastAnalyzed:");
            sb.append(this.lastAnalyzed);
            first = false;
        }
        sb.append(")");
        return sb.toString();
    }
    
    public void validate() throws TException {
        if (!this.isSetIsTblLevel()) {
            throw new TProtocolException("Required field 'isTblLevel' is unset! Struct:" + this.toString());
        }
        if (!this.isSetDbName()) {
            throw new TProtocolException("Required field 'dbName' is unset! Struct:" + this.toString());
        }
        if (!this.isSetTableName()) {
            throw new TProtocolException("Required field 'tableName' is unset! Struct:" + this.toString());
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
            this.__isset_bitfield = 0;
            this.read(new TCompactProtocol(new TIOStreamTransport(in)));
        }
        catch (TException te) {
            throw new IOException(te);
        }
    }
    
    static {
        STRUCT_DESC = new TStruct("ColumnStatisticsDesc");
        IS_TBL_LEVEL_FIELD_DESC = new TField("isTblLevel", (byte)2, (short)1);
        DB_NAME_FIELD_DESC = new TField("dbName", (byte)11, (short)2);
        TABLE_NAME_FIELD_DESC = new TField("tableName", (byte)11, (short)3);
        PART_NAME_FIELD_DESC = new TField("partName", (byte)11, (short)4);
        LAST_ANALYZED_FIELD_DESC = new TField("lastAnalyzed", (byte)10, (short)5);
        (schemes = new HashMap<Class<? extends IScheme>, SchemeFactory>()).put(StandardScheme.class, new ColumnStatisticsDescStandardSchemeFactory());
        ColumnStatisticsDesc.schemes.put(TupleScheme.class, new ColumnStatisticsDescTupleSchemeFactory());
        final Map<_Fields, FieldMetaData> tmpMap = new EnumMap<_Fields, FieldMetaData>(_Fields.class);
        tmpMap.put(_Fields.IS_TBL_LEVEL, new FieldMetaData("isTblLevel", (byte)1, new FieldValueMetaData((byte)2)));
        tmpMap.put(_Fields.DB_NAME, new FieldMetaData("dbName", (byte)1, new FieldValueMetaData((byte)11)));
        tmpMap.put(_Fields.TABLE_NAME, new FieldMetaData("tableName", (byte)1, new FieldValueMetaData((byte)11)));
        tmpMap.put(_Fields.PART_NAME, new FieldMetaData("partName", (byte)2, new FieldValueMetaData((byte)11)));
        tmpMap.put(_Fields.LAST_ANALYZED, new FieldMetaData("lastAnalyzed", (byte)2, new FieldValueMetaData((byte)10)));
        FieldMetaData.addStructMetaDataMap(ColumnStatisticsDesc.class, metaDataMap = Collections.unmodifiableMap((Map<? extends _Fields, ? extends FieldMetaData>)tmpMap));
    }
    
    public enum _Fields implements TFieldIdEnum
    {
        IS_TBL_LEVEL((short)1, "isTblLevel"), 
        DB_NAME((short)2, "dbName"), 
        TABLE_NAME((short)3, "tableName"), 
        PART_NAME((short)4, "partName"), 
        LAST_ANALYZED((short)5, "lastAnalyzed");
        
        private static final Map<String, _Fields> byName;
        private final short _thriftId;
        private final String _fieldName;
        
        public static _Fields findByThriftId(final int fieldId) {
            switch (fieldId) {
                case 1: {
                    return _Fields.IS_TBL_LEVEL;
                }
                case 2: {
                    return _Fields.DB_NAME;
                }
                case 3: {
                    return _Fields.TABLE_NAME;
                }
                case 4: {
                    return _Fields.PART_NAME;
                }
                case 5: {
                    return _Fields.LAST_ANALYZED;
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
    
    private static class ColumnStatisticsDescStandardSchemeFactory implements SchemeFactory
    {
        @Override
        public ColumnStatisticsDescStandardScheme getScheme() {
            return new ColumnStatisticsDescStandardScheme();
        }
    }
    
    private static class ColumnStatisticsDescStandardScheme extends StandardScheme<ColumnStatisticsDesc>
    {
        @Override
        public void read(final TProtocol iprot, final ColumnStatisticsDesc struct) throws TException {
            iprot.readStructBegin();
            while (true) {
                final TField schemeField = iprot.readFieldBegin();
                if (schemeField.type == 0) {
                    break;
                }
                switch (schemeField.id) {
                    case 1: {
                        if (schemeField.type == 2) {
                            struct.isTblLevel = iprot.readBool();
                            struct.setIsTblLevelIsSet(true);
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
                            struct.tableName = iprot.readString();
                            struct.setTableNameIsSet(true);
                            break;
                        }
                        TProtocolUtil.skip(iprot, schemeField.type);
                        break;
                    }
                    case 4: {
                        if (schemeField.type == 11) {
                            struct.partName = iprot.readString();
                            struct.setPartNameIsSet(true);
                            break;
                        }
                        TProtocolUtil.skip(iprot, schemeField.type);
                        break;
                    }
                    case 5: {
                        if (schemeField.type == 10) {
                            struct.lastAnalyzed = iprot.readI64();
                            struct.setLastAnalyzedIsSet(true);
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
        public void write(final TProtocol oprot, final ColumnStatisticsDesc struct) throws TException {
            struct.validate();
            oprot.writeStructBegin(ColumnStatisticsDesc.STRUCT_DESC);
            oprot.writeFieldBegin(ColumnStatisticsDesc.IS_TBL_LEVEL_FIELD_DESC);
            oprot.writeBool(struct.isTblLevel);
            oprot.writeFieldEnd();
            if (struct.dbName != null) {
                oprot.writeFieldBegin(ColumnStatisticsDesc.DB_NAME_FIELD_DESC);
                oprot.writeString(struct.dbName);
                oprot.writeFieldEnd();
            }
            if (struct.tableName != null) {
                oprot.writeFieldBegin(ColumnStatisticsDesc.TABLE_NAME_FIELD_DESC);
                oprot.writeString(struct.tableName);
                oprot.writeFieldEnd();
            }
            if (struct.partName != null && struct.isSetPartName()) {
                oprot.writeFieldBegin(ColumnStatisticsDesc.PART_NAME_FIELD_DESC);
                oprot.writeString(struct.partName);
                oprot.writeFieldEnd();
            }
            if (struct.isSetLastAnalyzed()) {
                oprot.writeFieldBegin(ColumnStatisticsDesc.LAST_ANALYZED_FIELD_DESC);
                oprot.writeI64(struct.lastAnalyzed);
                oprot.writeFieldEnd();
            }
            oprot.writeFieldStop();
            oprot.writeStructEnd();
        }
    }
    
    private static class ColumnStatisticsDescTupleSchemeFactory implements SchemeFactory
    {
        @Override
        public ColumnStatisticsDescTupleScheme getScheme() {
            return new ColumnStatisticsDescTupleScheme();
        }
    }
    
    private static class ColumnStatisticsDescTupleScheme extends TupleScheme<ColumnStatisticsDesc>
    {
        @Override
        public void write(final TProtocol prot, final ColumnStatisticsDesc struct) throws TException {
            final TTupleProtocol oprot = (TTupleProtocol)prot;
            oprot.writeBool(struct.isTblLevel);
            oprot.writeString(struct.dbName);
            oprot.writeString(struct.tableName);
            final BitSet optionals = new BitSet();
            if (struct.isSetPartName()) {
                optionals.set(0);
            }
            if (struct.isSetLastAnalyzed()) {
                optionals.set(1);
            }
            oprot.writeBitSet(optionals, 2);
            if (struct.isSetPartName()) {
                oprot.writeString(struct.partName);
            }
            if (struct.isSetLastAnalyzed()) {
                oprot.writeI64(struct.lastAnalyzed);
            }
        }
        
        @Override
        public void read(final TProtocol prot, final ColumnStatisticsDesc struct) throws TException {
            final TTupleProtocol iprot = (TTupleProtocol)prot;
            struct.isTblLevel = iprot.readBool();
            struct.setIsTblLevelIsSet(true);
            struct.dbName = iprot.readString();
            struct.setDbNameIsSet(true);
            struct.tableName = iprot.readString();
            struct.setTableNameIsSet(true);
            final BitSet incoming = iprot.readBitSet(2);
            if (incoming.get(0)) {
                struct.partName = iprot.readString();
                struct.setPartNameIsSet(true);
            }
            if (incoming.get(1)) {
                struct.lastAnalyzed = iprot.readI64();
                struct.setLastAnalyzedIsSet(true);
            }
        }
    }
}
