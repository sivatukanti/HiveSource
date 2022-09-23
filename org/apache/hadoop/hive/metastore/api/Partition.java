// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.hive.metastore.api;

import java.util.BitSet;
import org.apache.thrift.protocol.TTupleProtocol;
import org.apache.thrift.protocol.TMap;
import org.apache.thrift.protocol.TList;
import org.apache.thrift.protocol.TProtocolUtil;
import java.util.EnumSet;
import java.util.Collections;
import org.apache.thrift.meta_data.MapMetaData;
import org.apache.thrift.meta_data.StructMetaData;
import org.apache.thrift.meta_data.ListMetaData;
import org.apache.thrift.meta_data.FieldValueMetaData;
import java.util.EnumMap;
import org.apache.thrift.scheme.TupleScheme;
import org.apache.thrift.scheme.StandardScheme;
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
import org.apache.thrift.EncodingUtils;
import java.util.Iterator;
import java.util.HashMap;
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

public class Partition implements TBase<Partition, _Fields>, Serializable, Cloneable
{
    private static final TStruct STRUCT_DESC;
    private static final TField VALUES_FIELD_DESC;
    private static final TField DB_NAME_FIELD_DESC;
    private static final TField TABLE_NAME_FIELD_DESC;
    private static final TField CREATE_TIME_FIELD_DESC;
    private static final TField LAST_ACCESS_TIME_FIELD_DESC;
    private static final TField SD_FIELD_DESC;
    private static final TField PARAMETERS_FIELD_DESC;
    private static final TField PRIVILEGES_FIELD_DESC;
    private static final Map<Class<? extends IScheme>, SchemeFactory> schemes;
    private List<String> values;
    private String dbName;
    private String tableName;
    private int createTime;
    private int lastAccessTime;
    private StorageDescriptor sd;
    private Map<String, String> parameters;
    private PrincipalPrivilegeSet privileges;
    private static final int __CREATETIME_ISSET_ID = 0;
    private static final int __LASTACCESSTIME_ISSET_ID = 1;
    private byte __isset_bitfield;
    private _Fields[] optionals;
    public static final Map<_Fields, FieldMetaData> metaDataMap;
    
    public Partition() {
        this.__isset_bitfield = 0;
        this.optionals = new _Fields[] { _Fields.PRIVILEGES };
    }
    
    public Partition(final List<String> values, final String dbName, final String tableName, final int createTime, final int lastAccessTime, final StorageDescriptor sd, final Map<String, String> parameters) {
        this();
        this.values = values;
        this.dbName = dbName;
        this.tableName = tableName;
        this.createTime = createTime;
        this.setCreateTimeIsSet(true);
        this.lastAccessTime = lastAccessTime;
        this.setLastAccessTimeIsSet(true);
        this.sd = sd;
        this.parameters = parameters;
    }
    
    public Partition(final Partition other) {
        this.__isset_bitfield = 0;
        this.optionals = new _Fields[] { _Fields.PRIVILEGES };
        this.__isset_bitfield = other.__isset_bitfield;
        if (other.isSetValues()) {
            final List<String> __this__values = new ArrayList<String>();
            for (final String other_element : other.values) {
                __this__values.add(other_element);
            }
            this.values = __this__values;
        }
        if (other.isSetDbName()) {
            this.dbName = other.dbName;
        }
        if (other.isSetTableName()) {
            this.tableName = other.tableName;
        }
        this.createTime = other.createTime;
        this.lastAccessTime = other.lastAccessTime;
        if (other.isSetSd()) {
            this.sd = new StorageDescriptor(other.sd);
        }
        if (other.isSetParameters()) {
            final Map<String, String> __this__parameters = new HashMap<String, String>();
            for (final Map.Entry<String, String> other_element2 : other.parameters.entrySet()) {
                final String other_element_key = other_element2.getKey();
                final String other_element_value = other_element2.getValue();
                final String __this__parameters_copy_key = other_element_key;
                final String __this__parameters_copy_value = other_element_value;
                __this__parameters.put(__this__parameters_copy_key, __this__parameters_copy_value);
            }
            this.parameters = __this__parameters;
        }
        if (other.isSetPrivileges()) {
            this.privileges = new PrincipalPrivilegeSet(other.privileges);
        }
    }
    
    @Override
    public Partition deepCopy() {
        return new Partition(this);
    }
    
    @Override
    public void clear() {
        this.values = null;
        this.dbName = null;
        this.tableName = null;
        this.setCreateTimeIsSet(false);
        this.createTime = 0;
        this.setLastAccessTimeIsSet(false);
        this.lastAccessTime = 0;
        this.sd = null;
        this.parameters = null;
        this.privileges = null;
    }
    
    public int getValuesSize() {
        return (this.values == null) ? 0 : this.values.size();
    }
    
    public Iterator<String> getValuesIterator() {
        return (this.values == null) ? null : this.values.iterator();
    }
    
    public void addToValues(final String elem) {
        if (this.values == null) {
            this.values = new ArrayList<String>();
        }
        this.values.add(elem);
    }
    
    public List<String> getValues() {
        return this.values;
    }
    
    public void setValues(final List<String> values) {
        this.values = values;
    }
    
    public void unsetValues() {
        this.values = null;
    }
    
    public boolean isSetValues() {
        return this.values != null;
    }
    
    public void setValuesIsSet(final boolean value) {
        if (!value) {
            this.values = null;
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
    
    public int getCreateTime() {
        return this.createTime;
    }
    
    public void setCreateTime(final int createTime) {
        this.createTime = createTime;
        this.setCreateTimeIsSet(true);
    }
    
    public void unsetCreateTime() {
        this.__isset_bitfield = EncodingUtils.clearBit(this.__isset_bitfield, 0);
    }
    
    public boolean isSetCreateTime() {
        return EncodingUtils.testBit(this.__isset_bitfield, 0);
    }
    
    public void setCreateTimeIsSet(final boolean value) {
        this.__isset_bitfield = EncodingUtils.setBit(this.__isset_bitfield, 0, value);
    }
    
    public int getLastAccessTime() {
        return this.lastAccessTime;
    }
    
    public void setLastAccessTime(final int lastAccessTime) {
        this.lastAccessTime = lastAccessTime;
        this.setLastAccessTimeIsSet(true);
    }
    
    public void unsetLastAccessTime() {
        this.__isset_bitfield = EncodingUtils.clearBit(this.__isset_bitfield, 1);
    }
    
    public boolean isSetLastAccessTime() {
        return EncodingUtils.testBit(this.__isset_bitfield, 1);
    }
    
    public void setLastAccessTimeIsSet(final boolean value) {
        this.__isset_bitfield = EncodingUtils.setBit(this.__isset_bitfield, 1, value);
    }
    
    public StorageDescriptor getSd() {
        return this.sd;
    }
    
    public void setSd(final StorageDescriptor sd) {
        this.sd = sd;
    }
    
    public void unsetSd() {
        this.sd = null;
    }
    
    public boolean isSetSd() {
        return this.sd != null;
    }
    
    public void setSdIsSet(final boolean value) {
        if (!value) {
            this.sd = null;
        }
    }
    
    public int getParametersSize() {
        return (this.parameters == null) ? 0 : this.parameters.size();
    }
    
    public void putToParameters(final String key, final String val) {
        if (this.parameters == null) {
            this.parameters = new HashMap<String, String>();
        }
        this.parameters.put(key, val);
    }
    
    public Map<String, String> getParameters() {
        return this.parameters;
    }
    
    public void setParameters(final Map<String, String> parameters) {
        this.parameters = parameters;
    }
    
    public void unsetParameters() {
        this.parameters = null;
    }
    
    public boolean isSetParameters() {
        return this.parameters != null;
    }
    
    public void setParametersIsSet(final boolean value) {
        if (!value) {
            this.parameters = null;
        }
    }
    
    public PrincipalPrivilegeSet getPrivileges() {
        return this.privileges;
    }
    
    public void setPrivileges(final PrincipalPrivilegeSet privileges) {
        this.privileges = privileges;
    }
    
    public void unsetPrivileges() {
        this.privileges = null;
    }
    
    public boolean isSetPrivileges() {
        return this.privileges != null;
    }
    
    public void setPrivilegesIsSet(final boolean value) {
        if (!value) {
            this.privileges = null;
        }
    }
    
    @Override
    public void setFieldValue(final _Fields field, final Object value) {
        switch (field) {
            case VALUES: {
                if (value == null) {
                    this.unsetValues();
                    break;
                }
                this.setValues((List<String>)value);
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
            case CREATE_TIME: {
                if (value == null) {
                    this.unsetCreateTime();
                    break;
                }
                this.setCreateTime((int)value);
                break;
            }
            case LAST_ACCESS_TIME: {
                if (value == null) {
                    this.unsetLastAccessTime();
                    break;
                }
                this.setLastAccessTime((int)value);
                break;
            }
            case SD: {
                if (value == null) {
                    this.unsetSd();
                    break;
                }
                this.setSd((StorageDescriptor)value);
                break;
            }
            case PARAMETERS: {
                if (value == null) {
                    this.unsetParameters();
                    break;
                }
                this.setParameters((Map<String, String>)value);
                break;
            }
            case PRIVILEGES: {
                if (value == null) {
                    this.unsetPrivileges();
                    break;
                }
                this.setPrivileges((PrincipalPrivilegeSet)value);
                break;
            }
        }
    }
    
    @Override
    public Object getFieldValue(final _Fields field) {
        switch (field) {
            case VALUES: {
                return this.getValues();
            }
            case DB_NAME: {
                return this.getDbName();
            }
            case TABLE_NAME: {
                return this.getTableName();
            }
            case CREATE_TIME: {
                return this.getCreateTime();
            }
            case LAST_ACCESS_TIME: {
                return this.getLastAccessTime();
            }
            case SD: {
                return this.getSd();
            }
            case PARAMETERS: {
                return this.getParameters();
            }
            case PRIVILEGES: {
                return this.getPrivileges();
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
            case VALUES: {
                return this.isSetValues();
            }
            case DB_NAME: {
                return this.isSetDbName();
            }
            case TABLE_NAME: {
                return this.isSetTableName();
            }
            case CREATE_TIME: {
                return this.isSetCreateTime();
            }
            case LAST_ACCESS_TIME: {
                return this.isSetLastAccessTime();
            }
            case SD: {
                return this.isSetSd();
            }
            case PARAMETERS: {
                return this.isSetParameters();
            }
            case PRIVILEGES: {
                return this.isSetPrivileges();
            }
            default: {
                throw new IllegalStateException();
            }
        }
    }
    
    @Override
    public boolean equals(final Object that) {
        return that != null && that instanceof Partition && this.equals((Partition)that);
    }
    
    public boolean equals(final Partition that) {
        if (that == null) {
            return false;
        }
        final boolean this_present_values = this.isSetValues();
        final boolean that_present_values = that.isSetValues();
        if (this_present_values || that_present_values) {
            if (!this_present_values || !that_present_values) {
                return false;
            }
            if (!this.values.equals(that.values)) {
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
        final boolean this_present_createTime = true;
        final boolean that_present_createTime = true;
        if (this_present_createTime || that_present_createTime) {
            if (!this_present_createTime || !that_present_createTime) {
                return false;
            }
            if (this.createTime != that.createTime) {
                return false;
            }
        }
        final boolean this_present_lastAccessTime = true;
        final boolean that_present_lastAccessTime = true;
        if (this_present_lastAccessTime || that_present_lastAccessTime) {
            if (!this_present_lastAccessTime || !that_present_lastAccessTime) {
                return false;
            }
            if (this.lastAccessTime != that.lastAccessTime) {
                return false;
            }
        }
        final boolean this_present_sd = this.isSetSd();
        final boolean that_present_sd = that.isSetSd();
        if (this_present_sd || that_present_sd) {
            if (!this_present_sd || !that_present_sd) {
                return false;
            }
            if (!this.sd.equals(that.sd)) {
                return false;
            }
        }
        final boolean this_present_parameters = this.isSetParameters();
        final boolean that_present_parameters = that.isSetParameters();
        if (this_present_parameters || that_present_parameters) {
            if (!this_present_parameters || !that_present_parameters) {
                return false;
            }
            if (!this.parameters.equals(that.parameters)) {
                return false;
            }
        }
        final boolean this_present_privileges = this.isSetPrivileges();
        final boolean that_present_privileges = that.isSetPrivileges();
        if (this_present_privileges || that_present_privileges) {
            if (!this_present_privileges || !that_present_privileges) {
                return false;
            }
            if (!this.privileges.equals(that.privileges)) {
                return false;
            }
        }
        return true;
    }
    
    @Override
    public int hashCode() {
        final HashCodeBuilder builder = new HashCodeBuilder();
        final boolean present_values = this.isSetValues();
        builder.append(present_values);
        if (present_values) {
            builder.append(this.values);
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
        final boolean present_createTime = true;
        builder.append(present_createTime);
        if (present_createTime) {
            builder.append(this.createTime);
        }
        final boolean present_lastAccessTime = true;
        builder.append(present_lastAccessTime);
        if (present_lastAccessTime) {
            builder.append(this.lastAccessTime);
        }
        final boolean present_sd = this.isSetSd();
        builder.append(present_sd);
        if (present_sd) {
            builder.append(this.sd);
        }
        final boolean present_parameters = this.isSetParameters();
        builder.append(present_parameters);
        if (present_parameters) {
            builder.append(this.parameters);
        }
        final boolean present_privileges = this.isSetPrivileges();
        builder.append(present_privileges);
        if (present_privileges) {
            builder.append(this.privileges);
        }
        return builder.toHashCode();
    }
    
    @Override
    public int compareTo(final Partition other) {
        if (!this.getClass().equals(other.getClass())) {
            return this.getClass().getName().compareTo(other.getClass().getName());
        }
        int lastComparison = 0;
        final Partition typedOther = other;
        lastComparison = Boolean.valueOf(this.isSetValues()).compareTo(Boolean.valueOf(typedOther.isSetValues()));
        if (lastComparison != 0) {
            return lastComparison;
        }
        if (this.isSetValues()) {
            lastComparison = TBaseHelper.compareTo(this.values, typedOther.values);
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
        lastComparison = Boolean.valueOf(this.isSetCreateTime()).compareTo(Boolean.valueOf(typedOther.isSetCreateTime()));
        if (lastComparison != 0) {
            return lastComparison;
        }
        if (this.isSetCreateTime()) {
            lastComparison = TBaseHelper.compareTo(this.createTime, typedOther.createTime);
            if (lastComparison != 0) {
                return lastComparison;
            }
        }
        lastComparison = Boolean.valueOf(this.isSetLastAccessTime()).compareTo(Boolean.valueOf(typedOther.isSetLastAccessTime()));
        if (lastComparison != 0) {
            return lastComparison;
        }
        if (this.isSetLastAccessTime()) {
            lastComparison = TBaseHelper.compareTo(this.lastAccessTime, typedOther.lastAccessTime);
            if (lastComparison != 0) {
                return lastComparison;
            }
        }
        lastComparison = Boolean.valueOf(this.isSetSd()).compareTo(Boolean.valueOf(typedOther.isSetSd()));
        if (lastComparison != 0) {
            return lastComparison;
        }
        if (this.isSetSd()) {
            lastComparison = TBaseHelper.compareTo(this.sd, typedOther.sd);
            if (lastComparison != 0) {
                return lastComparison;
            }
        }
        lastComparison = Boolean.valueOf(this.isSetParameters()).compareTo(Boolean.valueOf(typedOther.isSetParameters()));
        if (lastComparison != 0) {
            return lastComparison;
        }
        if (this.isSetParameters()) {
            lastComparison = TBaseHelper.compareTo(this.parameters, typedOther.parameters);
            if (lastComparison != 0) {
                return lastComparison;
            }
        }
        lastComparison = Boolean.valueOf(this.isSetPrivileges()).compareTo(Boolean.valueOf(typedOther.isSetPrivileges()));
        if (lastComparison != 0) {
            return lastComparison;
        }
        if (this.isSetPrivileges()) {
            lastComparison = TBaseHelper.compareTo(this.privileges, typedOther.privileges);
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
        Partition.schemes.get(iprot.getScheme()).getScheme().read(iprot, this);
    }
    
    @Override
    public void write(final TProtocol oprot) throws TException {
        Partition.schemes.get(oprot.getScheme()).getScheme().write(oprot, this);
    }
    
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("Partition(");
        boolean first = true;
        sb.append("values:");
        if (this.values == null) {
            sb.append("null");
        }
        else {
            sb.append(this.values);
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
        sb.append("createTime:");
        sb.append(this.createTime);
        first = false;
        if (!first) {
            sb.append(", ");
        }
        sb.append("lastAccessTime:");
        sb.append(this.lastAccessTime);
        first = false;
        if (!first) {
            sb.append(", ");
        }
        sb.append("sd:");
        if (this.sd == null) {
            sb.append("null");
        }
        else {
            sb.append(this.sd);
        }
        first = false;
        if (!first) {
            sb.append(", ");
        }
        sb.append("parameters:");
        if (this.parameters == null) {
            sb.append("null");
        }
        else {
            sb.append(this.parameters);
        }
        first = false;
        if (this.isSetPrivileges()) {
            if (!first) {
                sb.append(", ");
            }
            sb.append("privileges:");
            if (this.privileges == null) {
                sb.append("null");
            }
            else {
                sb.append(this.privileges);
            }
            first = false;
        }
        sb.append(")");
        return sb.toString();
    }
    
    public void validate() throws TException {
        if (this.sd != null) {
            this.sd.validate();
        }
        if (this.privileges != null) {
            this.privileges.validate();
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
        STRUCT_DESC = new TStruct("Partition");
        VALUES_FIELD_DESC = new TField("values", (byte)15, (short)1);
        DB_NAME_FIELD_DESC = new TField("dbName", (byte)11, (short)2);
        TABLE_NAME_FIELD_DESC = new TField("tableName", (byte)11, (short)3);
        CREATE_TIME_FIELD_DESC = new TField("createTime", (byte)8, (short)4);
        LAST_ACCESS_TIME_FIELD_DESC = new TField("lastAccessTime", (byte)8, (short)5);
        SD_FIELD_DESC = new TField("sd", (byte)12, (short)6);
        PARAMETERS_FIELD_DESC = new TField("parameters", (byte)13, (short)7);
        PRIVILEGES_FIELD_DESC = new TField("privileges", (byte)12, (short)8);
        (schemes = new HashMap<Class<? extends IScheme>, SchemeFactory>()).put(StandardScheme.class, new PartitionStandardSchemeFactory());
        Partition.schemes.put(TupleScheme.class, new PartitionTupleSchemeFactory());
        final Map<_Fields, FieldMetaData> tmpMap = new EnumMap<_Fields, FieldMetaData>(_Fields.class);
        tmpMap.put(_Fields.VALUES, new FieldMetaData("values", (byte)3, new ListMetaData((byte)15, new FieldValueMetaData((byte)11))));
        tmpMap.put(_Fields.DB_NAME, new FieldMetaData("dbName", (byte)3, new FieldValueMetaData((byte)11)));
        tmpMap.put(_Fields.TABLE_NAME, new FieldMetaData("tableName", (byte)3, new FieldValueMetaData((byte)11)));
        tmpMap.put(_Fields.CREATE_TIME, new FieldMetaData("createTime", (byte)3, new FieldValueMetaData((byte)8)));
        tmpMap.put(_Fields.LAST_ACCESS_TIME, new FieldMetaData("lastAccessTime", (byte)3, new FieldValueMetaData((byte)8)));
        tmpMap.put(_Fields.SD, new FieldMetaData("sd", (byte)3, new StructMetaData((byte)12, StorageDescriptor.class)));
        tmpMap.put(_Fields.PARAMETERS, new FieldMetaData("parameters", (byte)3, new MapMetaData((byte)13, new FieldValueMetaData((byte)11), new FieldValueMetaData((byte)11))));
        tmpMap.put(_Fields.PRIVILEGES, new FieldMetaData("privileges", (byte)2, new StructMetaData((byte)12, PrincipalPrivilegeSet.class)));
        FieldMetaData.addStructMetaDataMap(Partition.class, metaDataMap = Collections.unmodifiableMap((Map<? extends _Fields, ? extends FieldMetaData>)tmpMap));
    }
    
    public enum _Fields implements TFieldIdEnum
    {
        VALUES((short)1, "values"), 
        DB_NAME((short)2, "dbName"), 
        TABLE_NAME((short)3, "tableName"), 
        CREATE_TIME((short)4, "createTime"), 
        LAST_ACCESS_TIME((short)5, "lastAccessTime"), 
        SD((short)6, "sd"), 
        PARAMETERS((short)7, "parameters"), 
        PRIVILEGES((short)8, "privileges");
        
        private static final Map<String, _Fields> byName;
        private final short _thriftId;
        private final String _fieldName;
        
        public static _Fields findByThriftId(final int fieldId) {
            switch (fieldId) {
                case 1: {
                    return _Fields.VALUES;
                }
                case 2: {
                    return _Fields.DB_NAME;
                }
                case 3: {
                    return _Fields.TABLE_NAME;
                }
                case 4: {
                    return _Fields.CREATE_TIME;
                }
                case 5: {
                    return _Fields.LAST_ACCESS_TIME;
                }
                case 6: {
                    return _Fields.SD;
                }
                case 7: {
                    return _Fields.PARAMETERS;
                }
                case 8: {
                    return _Fields.PRIVILEGES;
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
    
    private static class PartitionStandardSchemeFactory implements SchemeFactory
    {
        @Override
        public PartitionStandardScheme getScheme() {
            return new PartitionStandardScheme();
        }
    }
    
    private static class PartitionStandardScheme extends StandardScheme<Partition>
    {
        @Override
        public void read(final TProtocol iprot, final Partition struct) throws TException {
            iprot.readStructBegin();
            while (true) {
                final TField schemeField = iprot.readFieldBegin();
                if (schemeField.type == 0) {
                    break;
                }
                switch (schemeField.id) {
                    case 1: {
                        if (schemeField.type == 15) {
                            final TList _list208 = iprot.readListBegin();
                            struct.values = (List<String>)new ArrayList(_list208.size);
                            for (int _i209 = 0; _i209 < _list208.size; ++_i209) {
                                final String _elem210 = iprot.readString();
                                struct.values.add(_elem210);
                            }
                            iprot.readListEnd();
                            struct.setValuesIsSet(true);
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
                        if (schemeField.type == 8) {
                            struct.createTime = iprot.readI32();
                            struct.setCreateTimeIsSet(true);
                            break;
                        }
                        TProtocolUtil.skip(iprot, schemeField.type);
                        break;
                    }
                    case 5: {
                        if (schemeField.type == 8) {
                            struct.lastAccessTime = iprot.readI32();
                            struct.setLastAccessTimeIsSet(true);
                            break;
                        }
                        TProtocolUtil.skip(iprot, schemeField.type);
                        break;
                    }
                    case 6: {
                        if (schemeField.type == 12) {
                            struct.sd = new StorageDescriptor();
                            struct.sd.read(iprot);
                            struct.setSdIsSet(true);
                            break;
                        }
                        TProtocolUtil.skip(iprot, schemeField.type);
                        break;
                    }
                    case 7: {
                        if (schemeField.type == 13) {
                            final TMap _map211 = iprot.readMapBegin();
                            struct.parameters = (Map<String, String>)new HashMap(2 * _map211.size);
                            for (int _i210 = 0; _i210 < _map211.size; ++_i210) {
                                final String _key213 = iprot.readString();
                                final String _val214 = iprot.readString();
                                struct.parameters.put(_key213, _val214);
                            }
                            iprot.readMapEnd();
                            struct.setParametersIsSet(true);
                            break;
                        }
                        TProtocolUtil.skip(iprot, schemeField.type);
                        break;
                    }
                    case 8: {
                        if (schemeField.type == 12) {
                            struct.privileges = new PrincipalPrivilegeSet();
                            struct.privileges.read(iprot);
                            struct.setPrivilegesIsSet(true);
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
        public void write(final TProtocol oprot, final Partition struct) throws TException {
            struct.validate();
            oprot.writeStructBegin(Partition.STRUCT_DESC);
            if (struct.values != null) {
                oprot.writeFieldBegin(Partition.VALUES_FIELD_DESC);
                oprot.writeListBegin(new TList((byte)11, struct.values.size()));
                for (final String _iter215 : struct.values) {
                    oprot.writeString(_iter215);
                }
                oprot.writeListEnd();
                oprot.writeFieldEnd();
            }
            if (struct.dbName != null) {
                oprot.writeFieldBegin(Partition.DB_NAME_FIELD_DESC);
                oprot.writeString(struct.dbName);
                oprot.writeFieldEnd();
            }
            if (struct.tableName != null) {
                oprot.writeFieldBegin(Partition.TABLE_NAME_FIELD_DESC);
                oprot.writeString(struct.tableName);
                oprot.writeFieldEnd();
            }
            oprot.writeFieldBegin(Partition.CREATE_TIME_FIELD_DESC);
            oprot.writeI32(struct.createTime);
            oprot.writeFieldEnd();
            oprot.writeFieldBegin(Partition.LAST_ACCESS_TIME_FIELD_DESC);
            oprot.writeI32(struct.lastAccessTime);
            oprot.writeFieldEnd();
            if (struct.sd != null) {
                oprot.writeFieldBegin(Partition.SD_FIELD_DESC);
                struct.sd.write(oprot);
                oprot.writeFieldEnd();
            }
            if (struct.parameters != null) {
                oprot.writeFieldBegin(Partition.PARAMETERS_FIELD_DESC);
                oprot.writeMapBegin(new TMap((byte)11, (byte)11, struct.parameters.size()));
                for (final Map.Entry<String, String> _iter216 : struct.parameters.entrySet()) {
                    oprot.writeString(_iter216.getKey());
                    oprot.writeString(_iter216.getValue());
                }
                oprot.writeMapEnd();
                oprot.writeFieldEnd();
            }
            if (struct.privileges != null && struct.isSetPrivileges()) {
                oprot.writeFieldBegin(Partition.PRIVILEGES_FIELD_DESC);
                struct.privileges.write(oprot);
                oprot.writeFieldEnd();
            }
            oprot.writeFieldStop();
            oprot.writeStructEnd();
        }
    }
    
    private static class PartitionTupleSchemeFactory implements SchemeFactory
    {
        @Override
        public PartitionTupleScheme getScheme() {
            return new PartitionTupleScheme();
        }
    }
    
    private static class PartitionTupleScheme extends TupleScheme<Partition>
    {
        @Override
        public void write(final TProtocol prot, final Partition struct) throws TException {
            final TTupleProtocol oprot = (TTupleProtocol)prot;
            final BitSet optionals = new BitSet();
            if (struct.isSetValues()) {
                optionals.set(0);
            }
            if (struct.isSetDbName()) {
                optionals.set(1);
            }
            if (struct.isSetTableName()) {
                optionals.set(2);
            }
            if (struct.isSetCreateTime()) {
                optionals.set(3);
            }
            if (struct.isSetLastAccessTime()) {
                optionals.set(4);
            }
            if (struct.isSetSd()) {
                optionals.set(5);
            }
            if (struct.isSetParameters()) {
                optionals.set(6);
            }
            if (struct.isSetPrivileges()) {
                optionals.set(7);
            }
            oprot.writeBitSet(optionals, 8);
            if (struct.isSetValues()) {
                oprot.writeI32(struct.values.size());
                for (final String _iter217 : struct.values) {
                    oprot.writeString(_iter217);
                }
            }
            if (struct.isSetDbName()) {
                oprot.writeString(struct.dbName);
            }
            if (struct.isSetTableName()) {
                oprot.writeString(struct.tableName);
            }
            if (struct.isSetCreateTime()) {
                oprot.writeI32(struct.createTime);
            }
            if (struct.isSetLastAccessTime()) {
                oprot.writeI32(struct.lastAccessTime);
            }
            if (struct.isSetSd()) {
                struct.sd.write(oprot);
            }
            if (struct.isSetParameters()) {
                oprot.writeI32(struct.parameters.size());
                for (final Map.Entry<String, String> _iter218 : struct.parameters.entrySet()) {
                    oprot.writeString(_iter218.getKey());
                    oprot.writeString(_iter218.getValue());
                }
            }
            if (struct.isSetPrivileges()) {
                struct.privileges.write(oprot);
            }
        }
        
        @Override
        public void read(final TProtocol prot, final Partition struct) throws TException {
            final TTupleProtocol iprot = (TTupleProtocol)prot;
            final BitSet incoming = iprot.readBitSet(8);
            if (incoming.get(0)) {
                final TList _list219 = new TList((byte)11, iprot.readI32());
                struct.values = (List<String>)new ArrayList(_list219.size);
                for (int _i220 = 0; _i220 < _list219.size; ++_i220) {
                    final String _elem221 = iprot.readString();
                    struct.values.add(_elem221);
                }
                struct.setValuesIsSet(true);
            }
            if (incoming.get(1)) {
                struct.dbName = iprot.readString();
                struct.setDbNameIsSet(true);
            }
            if (incoming.get(2)) {
                struct.tableName = iprot.readString();
                struct.setTableNameIsSet(true);
            }
            if (incoming.get(3)) {
                struct.createTime = iprot.readI32();
                struct.setCreateTimeIsSet(true);
            }
            if (incoming.get(4)) {
                struct.lastAccessTime = iprot.readI32();
                struct.setLastAccessTimeIsSet(true);
            }
            if (incoming.get(5)) {
                struct.sd = new StorageDescriptor();
                struct.sd.read(iprot);
                struct.setSdIsSet(true);
            }
            if (incoming.get(6)) {
                final TMap _map222 = new TMap((byte)11, (byte)11, iprot.readI32());
                struct.parameters = (Map<String, String>)new HashMap(2 * _map222.size);
                for (int _i221 = 0; _i221 < _map222.size; ++_i221) {
                    final String _key224 = iprot.readString();
                    final String _val225 = iprot.readString();
                    struct.parameters.put(_key224, _val225);
                }
                struct.setParametersIsSet(true);
            }
            if (incoming.get(7)) {
                struct.privileges = new PrincipalPrivilegeSet();
                struct.privileges.read(iprot);
                struct.setPrivilegesIsSet(true);
            }
        }
    }
}
