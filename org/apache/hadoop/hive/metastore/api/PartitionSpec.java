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
import org.apache.thrift.meta_data.FieldMetaData;
import org.apache.thrift.scheme.SchemeFactory;
import org.apache.thrift.scheme.IScheme;
import java.util.Map;
import org.apache.thrift.protocol.TField;
import org.apache.thrift.protocol.TStruct;
import java.io.Serializable;
import org.apache.thrift.TBase;

public class PartitionSpec implements TBase<PartitionSpec, _Fields>, Serializable, Cloneable
{
    private static final TStruct STRUCT_DESC;
    private static final TField DB_NAME_FIELD_DESC;
    private static final TField TABLE_NAME_FIELD_DESC;
    private static final TField ROOT_PATH_FIELD_DESC;
    private static final TField SHARED_SDPARTITION_SPEC_FIELD_DESC;
    private static final TField PARTITION_LIST_FIELD_DESC;
    private static final Map<Class<? extends IScheme>, SchemeFactory> schemes;
    private String dbName;
    private String tableName;
    private String rootPath;
    private PartitionSpecWithSharedSD sharedSDPartitionSpec;
    private PartitionListComposingSpec partitionList;
    private _Fields[] optionals;
    public static final Map<_Fields, FieldMetaData> metaDataMap;
    
    public PartitionSpec() {
        this.optionals = new _Fields[] { _Fields.SHARED_SDPARTITION_SPEC, _Fields.PARTITION_LIST };
    }
    
    public PartitionSpec(final String dbName, final String tableName, final String rootPath) {
        this();
        this.dbName = dbName;
        this.tableName = tableName;
        this.rootPath = rootPath;
    }
    
    public PartitionSpec(final PartitionSpec other) {
        this.optionals = new _Fields[] { _Fields.SHARED_SDPARTITION_SPEC, _Fields.PARTITION_LIST };
        if (other.isSetDbName()) {
            this.dbName = other.dbName;
        }
        if (other.isSetTableName()) {
            this.tableName = other.tableName;
        }
        if (other.isSetRootPath()) {
            this.rootPath = other.rootPath;
        }
        if (other.isSetSharedSDPartitionSpec()) {
            this.sharedSDPartitionSpec = new PartitionSpecWithSharedSD(other.sharedSDPartitionSpec);
        }
        if (other.isSetPartitionList()) {
            this.partitionList = new PartitionListComposingSpec(other.partitionList);
        }
    }
    
    @Override
    public PartitionSpec deepCopy() {
        return new PartitionSpec(this);
    }
    
    @Override
    public void clear() {
        this.dbName = null;
        this.tableName = null;
        this.rootPath = null;
        this.sharedSDPartitionSpec = null;
        this.partitionList = null;
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
    
    public String getRootPath() {
        return this.rootPath;
    }
    
    public void setRootPath(final String rootPath) {
        this.rootPath = rootPath;
    }
    
    public void unsetRootPath() {
        this.rootPath = null;
    }
    
    public boolean isSetRootPath() {
        return this.rootPath != null;
    }
    
    public void setRootPathIsSet(final boolean value) {
        if (!value) {
            this.rootPath = null;
        }
    }
    
    public PartitionSpecWithSharedSD getSharedSDPartitionSpec() {
        return this.sharedSDPartitionSpec;
    }
    
    public void setSharedSDPartitionSpec(final PartitionSpecWithSharedSD sharedSDPartitionSpec) {
        this.sharedSDPartitionSpec = sharedSDPartitionSpec;
    }
    
    public void unsetSharedSDPartitionSpec() {
        this.sharedSDPartitionSpec = null;
    }
    
    public boolean isSetSharedSDPartitionSpec() {
        return this.sharedSDPartitionSpec != null;
    }
    
    public void setSharedSDPartitionSpecIsSet(final boolean value) {
        if (!value) {
            this.sharedSDPartitionSpec = null;
        }
    }
    
    public PartitionListComposingSpec getPartitionList() {
        return this.partitionList;
    }
    
    public void setPartitionList(final PartitionListComposingSpec partitionList) {
        this.partitionList = partitionList;
    }
    
    public void unsetPartitionList() {
        this.partitionList = null;
    }
    
    public boolean isSetPartitionList() {
        return this.partitionList != null;
    }
    
    public void setPartitionListIsSet(final boolean value) {
        if (!value) {
            this.partitionList = null;
        }
    }
    
    @Override
    public void setFieldValue(final _Fields field, final Object value) {
        switch (field) {
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
            case ROOT_PATH: {
                if (value == null) {
                    this.unsetRootPath();
                    break;
                }
                this.setRootPath((String)value);
                break;
            }
            case SHARED_SDPARTITION_SPEC: {
                if (value == null) {
                    this.unsetSharedSDPartitionSpec();
                    break;
                }
                this.setSharedSDPartitionSpec((PartitionSpecWithSharedSD)value);
                break;
            }
            case PARTITION_LIST: {
                if (value == null) {
                    this.unsetPartitionList();
                    break;
                }
                this.setPartitionList((PartitionListComposingSpec)value);
                break;
            }
        }
    }
    
    @Override
    public Object getFieldValue(final _Fields field) {
        switch (field) {
            case DB_NAME: {
                return this.getDbName();
            }
            case TABLE_NAME: {
                return this.getTableName();
            }
            case ROOT_PATH: {
                return this.getRootPath();
            }
            case SHARED_SDPARTITION_SPEC: {
                return this.getSharedSDPartitionSpec();
            }
            case PARTITION_LIST: {
                return this.getPartitionList();
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
            case DB_NAME: {
                return this.isSetDbName();
            }
            case TABLE_NAME: {
                return this.isSetTableName();
            }
            case ROOT_PATH: {
                return this.isSetRootPath();
            }
            case SHARED_SDPARTITION_SPEC: {
                return this.isSetSharedSDPartitionSpec();
            }
            case PARTITION_LIST: {
                return this.isSetPartitionList();
            }
            default: {
                throw new IllegalStateException();
            }
        }
    }
    
    @Override
    public boolean equals(final Object that) {
        return that != null && that instanceof PartitionSpec && this.equals((PartitionSpec)that);
    }
    
    public boolean equals(final PartitionSpec that) {
        if (that == null) {
            return false;
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
        final boolean this_present_rootPath = this.isSetRootPath();
        final boolean that_present_rootPath = that.isSetRootPath();
        if (this_present_rootPath || that_present_rootPath) {
            if (!this_present_rootPath || !that_present_rootPath) {
                return false;
            }
            if (!this.rootPath.equals(that.rootPath)) {
                return false;
            }
        }
        final boolean this_present_sharedSDPartitionSpec = this.isSetSharedSDPartitionSpec();
        final boolean that_present_sharedSDPartitionSpec = that.isSetSharedSDPartitionSpec();
        if (this_present_sharedSDPartitionSpec || that_present_sharedSDPartitionSpec) {
            if (!this_present_sharedSDPartitionSpec || !that_present_sharedSDPartitionSpec) {
                return false;
            }
            if (!this.sharedSDPartitionSpec.equals(that.sharedSDPartitionSpec)) {
                return false;
            }
        }
        final boolean this_present_partitionList = this.isSetPartitionList();
        final boolean that_present_partitionList = that.isSetPartitionList();
        if (this_present_partitionList || that_present_partitionList) {
            if (!this_present_partitionList || !that_present_partitionList) {
                return false;
            }
            if (!this.partitionList.equals(that.partitionList)) {
                return false;
            }
        }
        return true;
    }
    
    @Override
    public int hashCode() {
        final HashCodeBuilder builder = new HashCodeBuilder();
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
        final boolean present_rootPath = this.isSetRootPath();
        builder.append(present_rootPath);
        if (present_rootPath) {
            builder.append(this.rootPath);
        }
        final boolean present_sharedSDPartitionSpec = this.isSetSharedSDPartitionSpec();
        builder.append(present_sharedSDPartitionSpec);
        if (present_sharedSDPartitionSpec) {
            builder.append(this.sharedSDPartitionSpec);
        }
        final boolean present_partitionList = this.isSetPartitionList();
        builder.append(present_partitionList);
        if (present_partitionList) {
            builder.append(this.partitionList);
        }
        return builder.toHashCode();
    }
    
    @Override
    public int compareTo(final PartitionSpec other) {
        if (!this.getClass().equals(other.getClass())) {
            return this.getClass().getName().compareTo(other.getClass().getName());
        }
        int lastComparison = 0;
        final PartitionSpec typedOther = other;
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
        lastComparison = Boolean.valueOf(this.isSetRootPath()).compareTo(Boolean.valueOf(typedOther.isSetRootPath()));
        if (lastComparison != 0) {
            return lastComparison;
        }
        if (this.isSetRootPath()) {
            lastComparison = TBaseHelper.compareTo(this.rootPath, typedOther.rootPath);
            if (lastComparison != 0) {
                return lastComparison;
            }
        }
        lastComparison = Boolean.valueOf(this.isSetSharedSDPartitionSpec()).compareTo(Boolean.valueOf(typedOther.isSetSharedSDPartitionSpec()));
        if (lastComparison != 0) {
            return lastComparison;
        }
        if (this.isSetSharedSDPartitionSpec()) {
            lastComparison = TBaseHelper.compareTo(this.sharedSDPartitionSpec, typedOther.sharedSDPartitionSpec);
            if (lastComparison != 0) {
                return lastComparison;
            }
        }
        lastComparison = Boolean.valueOf(this.isSetPartitionList()).compareTo(Boolean.valueOf(typedOther.isSetPartitionList()));
        if (lastComparison != 0) {
            return lastComparison;
        }
        if (this.isSetPartitionList()) {
            lastComparison = TBaseHelper.compareTo(this.partitionList, typedOther.partitionList);
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
        PartitionSpec.schemes.get(iprot.getScheme()).getScheme().read(iprot, this);
    }
    
    @Override
    public void write(final TProtocol oprot) throws TException {
        PartitionSpec.schemes.get(oprot.getScheme()).getScheme().write(oprot, this);
    }
    
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("PartitionSpec(");
        boolean first = true;
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
        if (!first) {
            sb.append(", ");
        }
        sb.append("rootPath:");
        if (this.rootPath == null) {
            sb.append("null");
        }
        else {
            sb.append(this.rootPath);
        }
        first = false;
        if (this.isSetSharedSDPartitionSpec()) {
            if (!first) {
                sb.append(", ");
            }
            sb.append("sharedSDPartitionSpec:");
            if (this.sharedSDPartitionSpec == null) {
                sb.append("null");
            }
            else {
                sb.append(this.sharedSDPartitionSpec);
            }
            first = false;
        }
        if (this.isSetPartitionList()) {
            if (!first) {
                sb.append(", ");
            }
            sb.append("partitionList:");
            if (this.partitionList == null) {
                sb.append("null");
            }
            else {
                sb.append(this.partitionList);
            }
            first = false;
        }
        sb.append(")");
        return sb.toString();
    }
    
    public void validate() throws TException {
        if (this.sharedSDPartitionSpec != null) {
            this.sharedSDPartitionSpec.validate();
        }
        if (this.partitionList != null) {
            this.partitionList.validate();
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
        STRUCT_DESC = new TStruct("PartitionSpec");
        DB_NAME_FIELD_DESC = new TField("dbName", (byte)11, (short)1);
        TABLE_NAME_FIELD_DESC = new TField("tableName", (byte)11, (short)2);
        ROOT_PATH_FIELD_DESC = new TField("rootPath", (byte)11, (short)3);
        SHARED_SDPARTITION_SPEC_FIELD_DESC = new TField("sharedSDPartitionSpec", (byte)12, (short)4);
        PARTITION_LIST_FIELD_DESC = new TField("partitionList", (byte)12, (short)5);
        (schemes = new HashMap<Class<? extends IScheme>, SchemeFactory>()).put(StandardScheme.class, new PartitionSpecStandardSchemeFactory());
        PartitionSpec.schemes.put(TupleScheme.class, new PartitionSpecTupleSchemeFactory());
        final Map<_Fields, FieldMetaData> tmpMap = new EnumMap<_Fields, FieldMetaData>(_Fields.class);
        tmpMap.put(_Fields.DB_NAME, new FieldMetaData("dbName", (byte)3, new FieldValueMetaData((byte)11)));
        tmpMap.put(_Fields.TABLE_NAME, new FieldMetaData("tableName", (byte)3, new FieldValueMetaData((byte)11)));
        tmpMap.put(_Fields.ROOT_PATH, new FieldMetaData("rootPath", (byte)3, new FieldValueMetaData((byte)11)));
        tmpMap.put(_Fields.SHARED_SDPARTITION_SPEC, new FieldMetaData("sharedSDPartitionSpec", (byte)2, new StructMetaData((byte)12, PartitionSpecWithSharedSD.class)));
        tmpMap.put(_Fields.PARTITION_LIST, new FieldMetaData("partitionList", (byte)2, new StructMetaData((byte)12, PartitionListComposingSpec.class)));
        FieldMetaData.addStructMetaDataMap(PartitionSpec.class, metaDataMap = Collections.unmodifiableMap((Map<? extends _Fields, ? extends FieldMetaData>)tmpMap));
    }
    
    public enum _Fields implements TFieldIdEnum
    {
        DB_NAME((short)1, "dbName"), 
        TABLE_NAME((short)2, "tableName"), 
        ROOT_PATH((short)3, "rootPath"), 
        SHARED_SDPARTITION_SPEC((short)4, "sharedSDPartitionSpec"), 
        PARTITION_LIST((short)5, "partitionList");
        
        private static final Map<String, _Fields> byName;
        private final short _thriftId;
        private final String _fieldName;
        
        public static _Fields findByThriftId(final int fieldId) {
            switch (fieldId) {
                case 1: {
                    return _Fields.DB_NAME;
                }
                case 2: {
                    return _Fields.TABLE_NAME;
                }
                case 3: {
                    return _Fields.ROOT_PATH;
                }
                case 4: {
                    return _Fields.SHARED_SDPARTITION_SPEC;
                }
                case 5: {
                    return _Fields.PARTITION_LIST;
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
    
    private static class PartitionSpecStandardSchemeFactory implements SchemeFactory
    {
        @Override
        public PartitionSpecStandardScheme getScheme() {
            return new PartitionSpecStandardScheme();
        }
    }
    
    private static class PartitionSpecStandardScheme extends StandardScheme<PartitionSpec>
    {
        @Override
        public void read(final TProtocol iprot, final PartitionSpec struct) throws TException {
            iprot.readStructBegin();
            while (true) {
                final TField schemeField = iprot.readFieldBegin();
                if (schemeField.type == 0) {
                    break;
                }
                switch (schemeField.id) {
                    case 1: {
                        if (schemeField.type == 11) {
                            struct.dbName = iprot.readString();
                            struct.setDbNameIsSet(true);
                            break;
                        }
                        TProtocolUtil.skip(iprot, schemeField.type);
                        break;
                    }
                    case 2: {
                        if (schemeField.type == 11) {
                            struct.tableName = iprot.readString();
                            struct.setTableNameIsSet(true);
                            break;
                        }
                        TProtocolUtil.skip(iprot, schemeField.type);
                        break;
                    }
                    case 3: {
                        if (schemeField.type == 11) {
                            struct.rootPath = iprot.readString();
                            struct.setRootPathIsSet(true);
                            break;
                        }
                        TProtocolUtil.skip(iprot, schemeField.type);
                        break;
                    }
                    case 4: {
                        if (schemeField.type == 12) {
                            struct.sharedSDPartitionSpec = new PartitionSpecWithSharedSD();
                            struct.sharedSDPartitionSpec.read(iprot);
                            struct.setSharedSDPartitionSpecIsSet(true);
                            break;
                        }
                        TProtocolUtil.skip(iprot, schemeField.type);
                        break;
                    }
                    case 5: {
                        if (schemeField.type == 12) {
                            struct.partitionList = new PartitionListComposingSpec();
                            struct.partitionList.read(iprot);
                            struct.setPartitionListIsSet(true);
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
        public void write(final TProtocol oprot, final PartitionSpec struct) throws TException {
            struct.validate();
            oprot.writeStructBegin(PartitionSpec.STRUCT_DESC);
            if (struct.dbName != null) {
                oprot.writeFieldBegin(PartitionSpec.DB_NAME_FIELD_DESC);
                oprot.writeString(struct.dbName);
                oprot.writeFieldEnd();
            }
            if (struct.tableName != null) {
                oprot.writeFieldBegin(PartitionSpec.TABLE_NAME_FIELD_DESC);
                oprot.writeString(struct.tableName);
                oprot.writeFieldEnd();
            }
            if (struct.rootPath != null) {
                oprot.writeFieldBegin(PartitionSpec.ROOT_PATH_FIELD_DESC);
                oprot.writeString(struct.rootPath);
                oprot.writeFieldEnd();
            }
            if (struct.sharedSDPartitionSpec != null && struct.isSetSharedSDPartitionSpec()) {
                oprot.writeFieldBegin(PartitionSpec.SHARED_SDPARTITION_SPEC_FIELD_DESC);
                struct.sharedSDPartitionSpec.write(oprot);
                oprot.writeFieldEnd();
            }
            if (struct.partitionList != null && struct.isSetPartitionList()) {
                oprot.writeFieldBegin(PartitionSpec.PARTITION_LIST_FIELD_DESC);
                struct.partitionList.write(oprot);
                oprot.writeFieldEnd();
            }
            oprot.writeFieldStop();
            oprot.writeStructEnd();
        }
    }
    
    private static class PartitionSpecTupleSchemeFactory implements SchemeFactory
    {
        @Override
        public PartitionSpecTupleScheme getScheme() {
            return new PartitionSpecTupleScheme();
        }
    }
    
    private static class PartitionSpecTupleScheme extends TupleScheme<PartitionSpec>
    {
        @Override
        public void write(final TProtocol prot, final PartitionSpec struct) throws TException {
            final TTupleProtocol oprot = (TTupleProtocol)prot;
            final BitSet optionals = new BitSet();
            if (struct.isSetDbName()) {
                optionals.set(0);
            }
            if (struct.isSetTableName()) {
                optionals.set(1);
            }
            if (struct.isSetRootPath()) {
                optionals.set(2);
            }
            if (struct.isSetSharedSDPartitionSpec()) {
                optionals.set(3);
            }
            if (struct.isSetPartitionList()) {
                optionals.set(4);
            }
            oprot.writeBitSet(optionals, 5);
            if (struct.isSetDbName()) {
                oprot.writeString(struct.dbName);
            }
            if (struct.isSetTableName()) {
                oprot.writeString(struct.tableName);
            }
            if (struct.isSetRootPath()) {
                oprot.writeString(struct.rootPath);
            }
            if (struct.isSetSharedSDPartitionSpec()) {
                struct.sharedSDPartitionSpec.write(oprot);
            }
            if (struct.isSetPartitionList()) {
                struct.partitionList.write(oprot);
            }
        }
        
        @Override
        public void read(final TProtocol prot, final PartitionSpec struct) throws TException {
            final TTupleProtocol iprot = (TTupleProtocol)prot;
            final BitSet incoming = iprot.readBitSet(5);
            if (incoming.get(0)) {
                struct.dbName = iprot.readString();
                struct.setDbNameIsSet(true);
            }
            if (incoming.get(1)) {
                struct.tableName = iprot.readString();
                struct.setTableNameIsSet(true);
            }
            if (incoming.get(2)) {
                struct.rootPath = iprot.readString();
                struct.setRootPathIsSet(true);
            }
            if (incoming.get(3)) {
                struct.sharedSDPartitionSpec = new PartitionSpecWithSharedSD();
                struct.sharedSDPartitionSpec.read(iprot);
                struct.setSharedSDPartitionSpecIsSet(true);
            }
            if (incoming.get(4)) {
                struct.partitionList = new PartitionListComposingSpec();
                struct.partitionList.read(iprot);
                struct.setPartitionListIsSet(true);
            }
        }
    }
}
