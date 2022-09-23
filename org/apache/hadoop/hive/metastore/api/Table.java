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
import org.apache.thrift.meta_data.ListMetaData;
import org.apache.thrift.meta_data.StructMetaData;
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

public class Table implements TBase<Table, _Fields>, Serializable, Cloneable
{
    private static final TStruct STRUCT_DESC;
    private static final TField TABLE_NAME_FIELD_DESC;
    private static final TField DB_NAME_FIELD_DESC;
    private static final TField OWNER_FIELD_DESC;
    private static final TField CREATE_TIME_FIELD_DESC;
    private static final TField LAST_ACCESS_TIME_FIELD_DESC;
    private static final TField RETENTION_FIELD_DESC;
    private static final TField SD_FIELD_DESC;
    private static final TField PARTITION_KEYS_FIELD_DESC;
    private static final TField PARAMETERS_FIELD_DESC;
    private static final TField VIEW_ORIGINAL_TEXT_FIELD_DESC;
    private static final TField VIEW_EXPANDED_TEXT_FIELD_DESC;
    private static final TField TABLE_TYPE_FIELD_DESC;
    private static final TField PRIVILEGES_FIELD_DESC;
    private static final TField TEMPORARY_FIELD_DESC;
    private static final Map<Class<? extends IScheme>, SchemeFactory> schemes;
    private String tableName;
    private String dbName;
    private String owner;
    private int createTime;
    private int lastAccessTime;
    private int retention;
    private StorageDescriptor sd;
    private List<FieldSchema> partitionKeys;
    private Map<String, String> parameters;
    private String viewOriginalText;
    private String viewExpandedText;
    private String tableType;
    private PrincipalPrivilegeSet privileges;
    private boolean temporary;
    private static final int __CREATETIME_ISSET_ID = 0;
    private static final int __LASTACCESSTIME_ISSET_ID = 1;
    private static final int __RETENTION_ISSET_ID = 2;
    private static final int __TEMPORARY_ISSET_ID = 3;
    private byte __isset_bitfield;
    private _Fields[] optionals;
    public static final Map<_Fields, FieldMetaData> metaDataMap;
    
    public Table() {
        this.__isset_bitfield = 0;
        this.optionals = new _Fields[] { _Fields.PRIVILEGES, _Fields.TEMPORARY };
        this.temporary = false;
    }
    
    public Table(final String tableName, final String dbName, final String owner, final int createTime, final int lastAccessTime, final int retention, final StorageDescriptor sd, final List<FieldSchema> partitionKeys, final Map<String, String> parameters, final String viewOriginalText, final String viewExpandedText, final String tableType) {
        this();
        this.tableName = tableName;
        this.dbName = dbName;
        this.owner = owner;
        this.createTime = createTime;
        this.setCreateTimeIsSet(true);
        this.lastAccessTime = lastAccessTime;
        this.setLastAccessTimeIsSet(true);
        this.retention = retention;
        this.setRetentionIsSet(true);
        this.sd = sd;
        this.partitionKeys = partitionKeys;
        this.parameters = parameters;
        this.viewOriginalText = viewOriginalText;
        this.viewExpandedText = viewExpandedText;
        this.tableType = tableType;
    }
    
    public Table(final Table other) {
        this.__isset_bitfield = 0;
        this.optionals = new _Fields[] { _Fields.PRIVILEGES, _Fields.TEMPORARY };
        this.__isset_bitfield = other.__isset_bitfield;
        if (other.isSetTableName()) {
            this.tableName = other.tableName;
        }
        if (other.isSetDbName()) {
            this.dbName = other.dbName;
        }
        if (other.isSetOwner()) {
            this.owner = other.owner;
        }
        this.createTime = other.createTime;
        this.lastAccessTime = other.lastAccessTime;
        this.retention = other.retention;
        if (other.isSetSd()) {
            this.sd = new StorageDescriptor(other.sd);
        }
        if (other.isSetPartitionKeys()) {
            final List<FieldSchema> __this__partitionKeys = new ArrayList<FieldSchema>();
            for (final FieldSchema other_element : other.partitionKeys) {
                __this__partitionKeys.add(new FieldSchema(other_element));
            }
            this.partitionKeys = __this__partitionKeys;
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
        if (other.isSetViewOriginalText()) {
            this.viewOriginalText = other.viewOriginalText;
        }
        if (other.isSetViewExpandedText()) {
            this.viewExpandedText = other.viewExpandedText;
        }
        if (other.isSetTableType()) {
            this.tableType = other.tableType;
        }
        if (other.isSetPrivileges()) {
            this.privileges = new PrincipalPrivilegeSet(other.privileges);
        }
        this.temporary = other.temporary;
    }
    
    @Override
    public Table deepCopy() {
        return new Table(this);
    }
    
    @Override
    public void clear() {
        this.tableName = null;
        this.dbName = null;
        this.owner = null;
        this.setCreateTimeIsSet(false);
        this.createTime = 0;
        this.setLastAccessTimeIsSet(false);
        this.lastAccessTime = 0;
        this.setRetentionIsSet(false);
        this.retention = 0;
        this.sd = null;
        this.partitionKeys = null;
        this.parameters = null;
        this.viewOriginalText = null;
        this.viewExpandedText = null;
        this.tableType = null;
        this.privileges = null;
        this.temporary = false;
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
    
    public String getOwner() {
        return this.owner;
    }
    
    public void setOwner(final String owner) {
        this.owner = owner;
    }
    
    public void unsetOwner() {
        this.owner = null;
    }
    
    public boolean isSetOwner() {
        return this.owner != null;
    }
    
    public void setOwnerIsSet(final boolean value) {
        if (!value) {
            this.owner = null;
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
    
    public int getRetention() {
        return this.retention;
    }
    
    public void setRetention(final int retention) {
        this.retention = retention;
        this.setRetentionIsSet(true);
    }
    
    public void unsetRetention() {
        this.__isset_bitfield = EncodingUtils.clearBit(this.__isset_bitfield, 2);
    }
    
    public boolean isSetRetention() {
        return EncodingUtils.testBit(this.__isset_bitfield, 2);
    }
    
    public void setRetentionIsSet(final boolean value) {
        this.__isset_bitfield = EncodingUtils.setBit(this.__isset_bitfield, 2, value);
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
    
    public int getPartitionKeysSize() {
        return (this.partitionKeys == null) ? 0 : this.partitionKeys.size();
    }
    
    public Iterator<FieldSchema> getPartitionKeysIterator() {
        return (this.partitionKeys == null) ? null : this.partitionKeys.iterator();
    }
    
    public void addToPartitionKeys(final FieldSchema elem) {
        if (this.partitionKeys == null) {
            this.partitionKeys = new ArrayList<FieldSchema>();
        }
        this.partitionKeys.add(elem);
    }
    
    public List<FieldSchema> getPartitionKeys() {
        return this.partitionKeys;
    }
    
    public void setPartitionKeys(final List<FieldSchema> partitionKeys) {
        this.partitionKeys = partitionKeys;
    }
    
    public void unsetPartitionKeys() {
        this.partitionKeys = null;
    }
    
    public boolean isSetPartitionKeys() {
        return this.partitionKeys != null;
    }
    
    public void setPartitionKeysIsSet(final boolean value) {
        if (!value) {
            this.partitionKeys = null;
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
    
    public String getViewOriginalText() {
        return this.viewOriginalText;
    }
    
    public void setViewOriginalText(final String viewOriginalText) {
        this.viewOriginalText = viewOriginalText;
    }
    
    public void unsetViewOriginalText() {
        this.viewOriginalText = null;
    }
    
    public boolean isSetViewOriginalText() {
        return this.viewOriginalText != null;
    }
    
    public void setViewOriginalTextIsSet(final boolean value) {
        if (!value) {
            this.viewOriginalText = null;
        }
    }
    
    public String getViewExpandedText() {
        return this.viewExpandedText;
    }
    
    public void setViewExpandedText(final String viewExpandedText) {
        this.viewExpandedText = viewExpandedText;
    }
    
    public void unsetViewExpandedText() {
        this.viewExpandedText = null;
    }
    
    public boolean isSetViewExpandedText() {
        return this.viewExpandedText != null;
    }
    
    public void setViewExpandedTextIsSet(final boolean value) {
        if (!value) {
            this.viewExpandedText = null;
        }
    }
    
    public String getTableType() {
        return this.tableType;
    }
    
    public void setTableType(final String tableType) {
        this.tableType = tableType;
    }
    
    public void unsetTableType() {
        this.tableType = null;
    }
    
    public boolean isSetTableType() {
        return this.tableType != null;
    }
    
    public void setTableTypeIsSet(final boolean value) {
        if (!value) {
            this.tableType = null;
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
    
    public boolean isTemporary() {
        return this.temporary;
    }
    
    public void setTemporary(final boolean temporary) {
        this.temporary = temporary;
        this.setTemporaryIsSet(true);
    }
    
    public void unsetTemporary() {
        this.__isset_bitfield = EncodingUtils.clearBit(this.__isset_bitfield, 3);
    }
    
    public boolean isSetTemporary() {
        return EncodingUtils.testBit(this.__isset_bitfield, 3);
    }
    
    public void setTemporaryIsSet(final boolean value) {
        this.__isset_bitfield = EncodingUtils.setBit(this.__isset_bitfield, 3, value);
    }
    
    @Override
    public void setFieldValue(final _Fields field, final Object value) {
        switch (field) {
            case TABLE_NAME: {
                if (value == null) {
                    this.unsetTableName();
                    break;
                }
                this.setTableName((String)value);
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
            case OWNER: {
                if (value == null) {
                    this.unsetOwner();
                    break;
                }
                this.setOwner((String)value);
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
            case RETENTION: {
                if (value == null) {
                    this.unsetRetention();
                    break;
                }
                this.setRetention((int)value);
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
            case PARTITION_KEYS: {
                if (value == null) {
                    this.unsetPartitionKeys();
                    break;
                }
                this.setPartitionKeys((List<FieldSchema>)value);
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
            case VIEW_ORIGINAL_TEXT: {
                if (value == null) {
                    this.unsetViewOriginalText();
                    break;
                }
                this.setViewOriginalText((String)value);
                break;
            }
            case VIEW_EXPANDED_TEXT: {
                if (value == null) {
                    this.unsetViewExpandedText();
                    break;
                }
                this.setViewExpandedText((String)value);
                break;
            }
            case TABLE_TYPE: {
                if (value == null) {
                    this.unsetTableType();
                    break;
                }
                this.setTableType((String)value);
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
            case TEMPORARY: {
                if (value == null) {
                    this.unsetTemporary();
                    break;
                }
                this.setTemporary((boolean)value);
                break;
            }
        }
    }
    
    @Override
    public Object getFieldValue(final _Fields field) {
        switch (field) {
            case TABLE_NAME: {
                return this.getTableName();
            }
            case DB_NAME: {
                return this.getDbName();
            }
            case OWNER: {
                return this.getOwner();
            }
            case CREATE_TIME: {
                return this.getCreateTime();
            }
            case LAST_ACCESS_TIME: {
                return this.getLastAccessTime();
            }
            case RETENTION: {
                return this.getRetention();
            }
            case SD: {
                return this.getSd();
            }
            case PARTITION_KEYS: {
                return this.getPartitionKeys();
            }
            case PARAMETERS: {
                return this.getParameters();
            }
            case VIEW_ORIGINAL_TEXT: {
                return this.getViewOriginalText();
            }
            case VIEW_EXPANDED_TEXT: {
                return this.getViewExpandedText();
            }
            case TABLE_TYPE: {
                return this.getTableType();
            }
            case PRIVILEGES: {
                return this.getPrivileges();
            }
            case TEMPORARY: {
                return this.isTemporary();
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
            case TABLE_NAME: {
                return this.isSetTableName();
            }
            case DB_NAME: {
                return this.isSetDbName();
            }
            case OWNER: {
                return this.isSetOwner();
            }
            case CREATE_TIME: {
                return this.isSetCreateTime();
            }
            case LAST_ACCESS_TIME: {
                return this.isSetLastAccessTime();
            }
            case RETENTION: {
                return this.isSetRetention();
            }
            case SD: {
                return this.isSetSd();
            }
            case PARTITION_KEYS: {
                return this.isSetPartitionKeys();
            }
            case PARAMETERS: {
                return this.isSetParameters();
            }
            case VIEW_ORIGINAL_TEXT: {
                return this.isSetViewOriginalText();
            }
            case VIEW_EXPANDED_TEXT: {
                return this.isSetViewExpandedText();
            }
            case TABLE_TYPE: {
                return this.isSetTableType();
            }
            case PRIVILEGES: {
                return this.isSetPrivileges();
            }
            case TEMPORARY: {
                return this.isSetTemporary();
            }
            default: {
                throw new IllegalStateException();
            }
        }
    }
    
    @Override
    public boolean equals(final Object that) {
        return that != null && that instanceof Table && this.equals((Table)that);
    }
    
    public boolean equals(final Table that) {
        if (that == null) {
            return false;
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
        final boolean this_present_owner = this.isSetOwner();
        final boolean that_present_owner = that.isSetOwner();
        if (this_present_owner || that_present_owner) {
            if (!this_present_owner || !that_present_owner) {
                return false;
            }
            if (!this.owner.equals(that.owner)) {
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
        final boolean this_present_retention = true;
        final boolean that_present_retention = true;
        if (this_present_retention || that_present_retention) {
            if (!this_present_retention || !that_present_retention) {
                return false;
            }
            if (this.retention != that.retention) {
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
        final boolean this_present_partitionKeys = this.isSetPartitionKeys();
        final boolean that_present_partitionKeys = that.isSetPartitionKeys();
        if (this_present_partitionKeys || that_present_partitionKeys) {
            if (!this_present_partitionKeys || !that_present_partitionKeys) {
                return false;
            }
            if (!this.partitionKeys.equals(that.partitionKeys)) {
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
        final boolean this_present_viewOriginalText = this.isSetViewOriginalText();
        final boolean that_present_viewOriginalText = that.isSetViewOriginalText();
        if (this_present_viewOriginalText || that_present_viewOriginalText) {
            if (!this_present_viewOriginalText || !that_present_viewOriginalText) {
                return false;
            }
            if (!this.viewOriginalText.equals(that.viewOriginalText)) {
                return false;
            }
        }
        final boolean this_present_viewExpandedText = this.isSetViewExpandedText();
        final boolean that_present_viewExpandedText = that.isSetViewExpandedText();
        if (this_present_viewExpandedText || that_present_viewExpandedText) {
            if (!this_present_viewExpandedText || !that_present_viewExpandedText) {
                return false;
            }
            if (!this.viewExpandedText.equals(that.viewExpandedText)) {
                return false;
            }
        }
        final boolean this_present_tableType = this.isSetTableType();
        final boolean that_present_tableType = that.isSetTableType();
        if (this_present_tableType || that_present_tableType) {
            if (!this_present_tableType || !that_present_tableType) {
                return false;
            }
            if (!this.tableType.equals(that.tableType)) {
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
        final boolean this_present_temporary = this.isSetTemporary();
        final boolean that_present_temporary = that.isSetTemporary();
        if (this_present_temporary || that_present_temporary) {
            if (!this_present_temporary || !that_present_temporary) {
                return false;
            }
            if (this.temporary != that.temporary) {
                return false;
            }
        }
        return true;
    }
    
    @Override
    public int hashCode() {
        final HashCodeBuilder builder = new HashCodeBuilder();
        final boolean present_tableName = this.isSetTableName();
        builder.append(present_tableName);
        if (present_tableName) {
            builder.append(this.tableName);
        }
        final boolean present_dbName = this.isSetDbName();
        builder.append(present_dbName);
        if (present_dbName) {
            builder.append(this.dbName);
        }
        final boolean present_owner = this.isSetOwner();
        builder.append(present_owner);
        if (present_owner) {
            builder.append(this.owner);
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
        final boolean present_retention = true;
        builder.append(present_retention);
        if (present_retention) {
            builder.append(this.retention);
        }
        final boolean present_sd = this.isSetSd();
        builder.append(present_sd);
        if (present_sd) {
            builder.append(this.sd);
        }
        final boolean present_partitionKeys = this.isSetPartitionKeys();
        builder.append(present_partitionKeys);
        if (present_partitionKeys) {
            builder.append(this.partitionKeys);
        }
        final boolean present_parameters = this.isSetParameters();
        builder.append(present_parameters);
        if (present_parameters) {
            builder.append(this.parameters);
        }
        final boolean present_viewOriginalText = this.isSetViewOriginalText();
        builder.append(present_viewOriginalText);
        if (present_viewOriginalText) {
            builder.append(this.viewOriginalText);
        }
        final boolean present_viewExpandedText = this.isSetViewExpandedText();
        builder.append(present_viewExpandedText);
        if (present_viewExpandedText) {
            builder.append(this.viewExpandedText);
        }
        final boolean present_tableType = this.isSetTableType();
        builder.append(present_tableType);
        if (present_tableType) {
            builder.append(this.tableType);
        }
        final boolean present_privileges = this.isSetPrivileges();
        builder.append(present_privileges);
        if (present_privileges) {
            builder.append(this.privileges);
        }
        final boolean present_temporary = this.isSetTemporary();
        builder.append(present_temporary);
        if (present_temporary) {
            builder.append(this.temporary);
        }
        return builder.toHashCode();
    }
    
    @Override
    public int compareTo(final Table other) {
        if (!this.getClass().equals(other.getClass())) {
            return this.getClass().getName().compareTo(other.getClass().getName());
        }
        int lastComparison = 0;
        final Table typedOther = other;
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
        lastComparison = Boolean.valueOf(this.isSetOwner()).compareTo(Boolean.valueOf(typedOther.isSetOwner()));
        if (lastComparison != 0) {
            return lastComparison;
        }
        if (this.isSetOwner()) {
            lastComparison = TBaseHelper.compareTo(this.owner, typedOther.owner);
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
        lastComparison = Boolean.valueOf(this.isSetRetention()).compareTo(Boolean.valueOf(typedOther.isSetRetention()));
        if (lastComparison != 0) {
            return lastComparison;
        }
        if (this.isSetRetention()) {
            lastComparison = TBaseHelper.compareTo(this.retention, typedOther.retention);
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
        lastComparison = Boolean.valueOf(this.isSetPartitionKeys()).compareTo(Boolean.valueOf(typedOther.isSetPartitionKeys()));
        if (lastComparison != 0) {
            return lastComparison;
        }
        if (this.isSetPartitionKeys()) {
            lastComparison = TBaseHelper.compareTo(this.partitionKeys, typedOther.partitionKeys);
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
        lastComparison = Boolean.valueOf(this.isSetViewOriginalText()).compareTo(Boolean.valueOf(typedOther.isSetViewOriginalText()));
        if (lastComparison != 0) {
            return lastComparison;
        }
        if (this.isSetViewOriginalText()) {
            lastComparison = TBaseHelper.compareTo(this.viewOriginalText, typedOther.viewOriginalText);
            if (lastComparison != 0) {
                return lastComparison;
            }
        }
        lastComparison = Boolean.valueOf(this.isSetViewExpandedText()).compareTo(Boolean.valueOf(typedOther.isSetViewExpandedText()));
        if (lastComparison != 0) {
            return lastComparison;
        }
        if (this.isSetViewExpandedText()) {
            lastComparison = TBaseHelper.compareTo(this.viewExpandedText, typedOther.viewExpandedText);
            if (lastComparison != 0) {
                return lastComparison;
            }
        }
        lastComparison = Boolean.valueOf(this.isSetTableType()).compareTo(Boolean.valueOf(typedOther.isSetTableType()));
        if (lastComparison != 0) {
            return lastComparison;
        }
        if (this.isSetTableType()) {
            lastComparison = TBaseHelper.compareTo(this.tableType, typedOther.tableType);
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
        lastComparison = Boolean.valueOf(this.isSetTemporary()).compareTo(Boolean.valueOf(typedOther.isSetTemporary()));
        if (lastComparison != 0) {
            return lastComparison;
        }
        if (this.isSetTemporary()) {
            lastComparison = TBaseHelper.compareTo(this.temporary, typedOther.temporary);
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
        Table.schemes.get(iprot.getScheme()).getScheme().read(iprot, this);
    }
    
    @Override
    public void write(final TProtocol oprot) throws TException {
        Table.schemes.get(oprot.getScheme()).getScheme().write(oprot, this);
    }
    
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("Table(");
        boolean first = true;
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
        sb.append("owner:");
        if (this.owner == null) {
            sb.append("null");
        }
        else {
            sb.append(this.owner);
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
        sb.append("retention:");
        sb.append(this.retention);
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
        sb.append("partitionKeys:");
        if (this.partitionKeys == null) {
            sb.append("null");
        }
        else {
            sb.append(this.partitionKeys);
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
        if (!first) {
            sb.append(", ");
        }
        sb.append("viewOriginalText:");
        if (this.viewOriginalText == null) {
            sb.append("null");
        }
        else {
            sb.append(this.viewOriginalText);
        }
        first = false;
        if (!first) {
            sb.append(", ");
        }
        sb.append("viewExpandedText:");
        if (this.viewExpandedText == null) {
            sb.append("null");
        }
        else {
            sb.append(this.viewExpandedText);
        }
        first = false;
        if (!first) {
            sb.append(", ");
        }
        sb.append("tableType:");
        if (this.tableType == null) {
            sb.append("null");
        }
        else {
            sb.append(this.tableType);
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
        if (this.isSetTemporary()) {
            if (!first) {
                sb.append(", ");
            }
            sb.append("temporary:");
            sb.append(this.temporary);
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
        STRUCT_DESC = new TStruct("Table");
        TABLE_NAME_FIELD_DESC = new TField("tableName", (byte)11, (short)1);
        DB_NAME_FIELD_DESC = new TField("dbName", (byte)11, (short)2);
        OWNER_FIELD_DESC = new TField("owner", (byte)11, (short)3);
        CREATE_TIME_FIELD_DESC = new TField("createTime", (byte)8, (short)4);
        LAST_ACCESS_TIME_FIELD_DESC = new TField("lastAccessTime", (byte)8, (short)5);
        RETENTION_FIELD_DESC = new TField("retention", (byte)8, (short)6);
        SD_FIELD_DESC = new TField("sd", (byte)12, (short)7);
        PARTITION_KEYS_FIELD_DESC = new TField("partitionKeys", (byte)15, (short)8);
        PARAMETERS_FIELD_DESC = new TField("parameters", (byte)13, (short)9);
        VIEW_ORIGINAL_TEXT_FIELD_DESC = new TField("viewOriginalText", (byte)11, (short)10);
        VIEW_EXPANDED_TEXT_FIELD_DESC = new TField("viewExpandedText", (byte)11, (short)11);
        TABLE_TYPE_FIELD_DESC = new TField("tableType", (byte)11, (short)12);
        PRIVILEGES_FIELD_DESC = new TField("privileges", (byte)12, (short)13);
        TEMPORARY_FIELD_DESC = new TField("temporary", (byte)2, (short)14);
        (schemes = new HashMap<Class<? extends IScheme>, SchemeFactory>()).put(StandardScheme.class, new TableStandardSchemeFactory());
        Table.schemes.put(TupleScheme.class, new TableTupleSchemeFactory());
        final Map<_Fields, FieldMetaData> tmpMap = new EnumMap<_Fields, FieldMetaData>(_Fields.class);
        tmpMap.put(_Fields.TABLE_NAME, new FieldMetaData("tableName", (byte)3, new FieldValueMetaData((byte)11)));
        tmpMap.put(_Fields.DB_NAME, new FieldMetaData("dbName", (byte)3, new FieldValueMetaData((byte)11)));
        tmpMap.put(_Fields.OWNER, new FieldMetaData("owner", (byte)3, new FieldValueMetaData((byte)11)));
        tmpMap.put(_Fields.CREATE_TIME, new FieldMetaData("createTime", (byte)3, new FieldValueMetaData((byte)8)));
        tmpMap.put(_Fields.LAST_ACCESS_TIME, new FieldMetaData("lastAccessTime", (byte)3, new FieldValueMetaData((byte)8)));
        tmpMap.put(_Fields.RETENTION, new FieldMetaData("retention", (byte)3, new FieldValueMetaData((byte)8)));
        tmpMap.put(_Fields.SD, new FieldMetaData("sd", (byte)3, new StructMetaData((byte)12, StorageDescriptor.class)));
        tmpMap.put(_Fields.PARTITION_KEYS, new FieldMetaData("partitionKeys", (byte)3, new ListMetaData((byte)15, new StructMetaData((byte)12, FieldSchema.class))));
        tmpMap.put(_Fields.PARAMETERS, new FieldMetaData("parameters", (byte)3, new MapMetaData((byte)13, new FieldValueMetaData((byte)11), new FieldValueMetaData((byte)11))));
        tmpMap.put(_Fields.VIEW_ORIGINAL_TEXT, new FieldMetaData("viewOriginalText", (byte)3, new FieldValueMetaData((byte)11)));
        tmpMap.put(_Fields.VIEW_EXPANDED_TEXT, new FieldMetaData("viewExpandedText", (byte)3, new FieldValueMetaData((byte)11)));
        tmpMap.put(_Fields.TABLE_TYPE, new FieldMetaData("tableType", (byte)3, new FieldValueMetaData((byte)11)));
        tmpMap.put(_Fields.PRIVILEGES, new FieldMetaData("privileges", (byte)2, new StructMetaData((byte)12, PrincipalPrivilegeSet.class)));
        tmpMap.put(_Fields.TEMPORARY, new FieldMetaData("temporary", (byte)2, new FieldValueMetaData((byte)2)));
        FieldMetaData.addStructMetaDataMap(Table.class, metaDataMap = Collections.unmodifiableMap((Map<? extends _Fields, ? extends FieldMetaData>)tmpMap));
    }
    
    public enum _Fields implements TFieldIdEnum
    {
        TABLE_NAME((short)1, "tableName"), 
        DB_NAME((short)2, "dbName"), 
        OWNER((short)3, "owner"), 
        CREATE_TIME((short)4, "createTime"), 
        LAST_ACCESS_TIME((short)5, "lastAccessTime"), 
        RETENTION((short)6, "retention"), 
        SD((short)7, "sd"), 
        PARTITION_KEYS((short)8, "partitionKeys"), 
        PARAMETERS((short)9, "parameters"), 
        VIEW_ORIGINAL_TEXT((short)10, "viewOriginalText"), 
        VIEW_EXPANDED_TEXT((short)11, "viewExpandedText"), 
        TABLE_TYPE((short)12, "tableType"), 
        PRIVILEGES((short)13, "privileges"), 
        TEMPORARY((short)14, "temporary");
        
        private static final Map<String, _Fields> byName;
        private final short _thriftId;
        private final String _fieldName;
        
        public static _Fields findByThriftId(final int fieldId) {
            switch (fieldId) {
                case 1: {
                    return _Fields.TABLE_NAME;
                }
                case 2: {
                    return _Fields.DB_NAME;
                }
                case 3: {
                    return _Fields.OWNER;
                }
                case 4: {
                    return _Fields.CREATE_TIME;
                }
                case 5: {
                    return _Fields.LAST_ACCESS_TIME;
                }
                case 6: {
                    return _Fields.RETENTION;
                }
                case 7: {
                    return _Fields.SD;
                }
                case 8: {
                    return _Fields.PARTITION_KEYS;
                }
                case 9: {
                    return _Fields.PARAMETERS;
                }
                case 10: {
                    return _Fields.VIEW_ORIGINAL_TEXT;
                }
                case 11: {
                    return _Fields.VIEW_EXPANDED_TEXT;
                }
                case 12: {
                    return _Fields.TABLE_TYPE;
                }
                case 13: {
                    return _Fields.PRIVILEGES;
                }
                case 14: {
                    return _Fields.TEMPORARY;
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
    
    private static class TableStandardSchemeFactory implements SchemeFactory
    {
        @Override
        public TableStandardScheme getScheme() {
            return new TableStandardScheme();
        }
    }
    
    private static class TableStandardScheme extends StandardScheme<Table>
    {
        @Override
        public void read(final TProtocol iprot, final Table struct) throws TException {
            iprot.readStructBegin();
            while (true) {
                final TField schemeField = iprot.readFieldBegin();
                if (schemeField.type == 0) {
                    break;
                }
                switch (schemeField.id) {
                    case 1: {
                        if (schemeField.type == 11) {
                            struct.tableName = iprot.readString();
                            struct.setTableNameIsSet(true);
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
                            struct.owner = iprot.readString();
                            struct.setOwnerIsSet(true);
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
                        if (schemeField.type == 8) {
                            struct.retention = iprot.readI32();
                            struct.setRetentionIsSet(true);
                            break;
                        }
                        TProtocolUtil.skip(iprot, schemeField.type);
                        break;
                    }
                    case 7: {
                        if (schemeField.type == 12) {
                            struct.sd = new StorageDescriptor();
                            struct.sd.read(iprot);
                            struct.setSdIsSet(true);
                            break;
                        }
                        TProtocolUtil.skip(iprot, schemeField.type);
                        break;
                    }
                    case 8: {
                        if (schemeField.type == 15) {
                            final TList _list190 = iprot.readListBegin();
                            struct.partitionKeys = (List<FieldSchema>)new ArrayList(_list190.size);
                            for (int _i191 = 0; _i191 < _list190.size; ++_i191) {
                                final FieldSchema _elem192 = new FieldSchema();
                                _elem192.read(iprot);
                                struct.partitionKeys.add(_elem192);
                            }
                            iprot.readListEnd();
                            struct.setPartitionKeysIsSet(true);
                            break;
                        }
                        TProtocolUtil.skip(iprot, schemeField.type);
                        break;
                    }
                    case 9: {
                        if (schemeField.type == 13) {
                            final TMap _map193 = iprot.readMapBegin();
                            struct.parameters = (Map<String, String>)new HashMap(2 * _map193.size);
                            for (int _i192 = 0; _i192 < _map193.size; ++_i192) {
                                final String _key195 = iprot.readString();
                                final String _val196 = iprot.readString();
                                struct.parameters.put(_key195, _val196);
                            }
                            iprot.readMapEnd();
                            struct.setParametersIsSet(true);
                            break;
                        }
                        TProtocolUtil.skip(iprot, schemeField.type);
                        break;
                    }
                    case 10: {
                        if (schemeField.type == 11) {
                            struct.viewOriginalText = iprot.readString();
                            struct.setViewOriginalTextIsSet(true);
                            break;
                        }
                        TProtocolUtil.skip(iprot, schemeField.type);
                        break;
                    }
                    case 11: {
                        if (schemeField.type == 11) {
                            struct.viewExpandedText = iprot.readString();
                            struct.setViewExpandedTextIsSet(true);
                            break;
                        }
                        TProtocolUtil.skip(iprot, schemeField.type);
                        break;
                    }
                    case 12: {
                        if (schemeField.type == 11) {
                            struct.tableType = iprot.readString();
                            struct.setTableTypeIsSet(true);
                            break;
                        }
                        TProtocolUtil.skip(iprot, schemeField.type);
                        break;
                    }
                    case 13: {
                        if (schemeField.type == 12) {
                            struct.privileges = new PrincipalPrivilegeSet();
                            struct.privileges.read(iprot);
                            struct.setPrivilegesIsSet(true);
                            break;
                        }
                        TProtocolUtil.skip(iprot, schemeField.type);
                        break;
                    }
                    case 14: {
                        if (schemeField.type == 2) {
                            struct.temporary = iprot.readBool();
                            struct.setTemporaryIsSet(true);
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
        public void write(final TProtocol oprot, final Table struct) throws TException {
            struct.validate();
            oprot.writeStructBegin(Table.STRUCT_DESC);
            if (struct.tableName != null) {
                oprot.writeFieldBegin(Table.TABLE_NAME_FIELD_DESC);
                oprot.writeString(struct.tableName);
                oprot.writeFieldEnd();
            }
            if (struct.dbName != null) {
                oprot.writeFieldBegin(Table.DB_NAME_FIELD_DESC);
                oprot.writeString(struct.dbName);
                oprot.writeFieldEnd();
            }
            if (struct.owner != null) {
                oprot.writeFieldBegin(Table.OWNER_FIELD_DESC);
                oprot.writeString(struct.owner);
                oprot.writeFieldEnd();
            }
            oprot.writeFieldBegin(Table.CREATE_TIME_FIELD_DESC);
            oprot.writeI32(struct.createTime);
            oprot.writeFieldEnd();
            oprot.writeFieldBegin(Table.LAST_ACCESS_TIME_FIELD_DESC);
            oprot.writeI32(struct.lastAccessTime);
            oprot.writeFieldEnd();
            oprot.writeFieldBegin(Table.RETENTION_FIELD_DESC);
            oprot.writeI32(struct.retention);
            oprot.writeFieldEnd();
            if (struct.sd != null) {
                oprot.writeFieldBegin(Table.SD_FIELD_DESC);
                struct.sd.write(oprot);
                oprot.writeFieldEnd();
            }
            if (struct.partitionKeys != null) {
                oprot.writeFieldBegin(Table.PARTITION_KEYS_FIELD_DESC);
                oprot.writeListBegin(new TList((byte)12, struct.partitionKeys.size()));
                for (final FieldSchema _iter197 : struct.partitionKeys) {
                    _iter197.write(oprot);
                }
                oprot.writeListEnd();
                oprot.writeFieldEnd();
            }
            if (struct.parameters != null) {
                oprot.writeFieldBegin(Table.PARAMETERS_FIELD_DESC);
                oprot.writeMapBegin(new TMap((byte)11, (byte)11, struct.parameters.size()));
                for (final Map.Entry<String, String> _iter198 : struct.parameters.entrySet()) {
                    oprot.writeString(_iter198.getKey());
                    oprot.writeString(_iter198.getValue());
                }
                oprot.writeMapEnd();
                oprot.writeFieldEnd();
            }
            if (struct.viewOriginalText != null) {
                oprot.writeFieldBegin(Table.VIEW_ORIGINAL_TEXT_FIELD_DESC);
                oprot.writeString(struct.viewOriginalText);
                oprot.writeFieldEnd();
            }
            if (struct.viewExpandedText != null) {
                oprot.writeFieldBegin(Table.VIEW_EXPANDED_TEXT_FIELD_DESC);
                oprot.writeString(struct.viewExpandedText);
                oprot.writeFieldEnd();
            }
            if (struct.tableType != null) {
                oprot.writeFieldBegin(Table.TABLE_TYPE_FIELD_DESC);
                oprot.writeString(struct.tableType);
                oprot.writeFieldEnd();
            }
            if (struct.privileges != null && struct.isSetPrivileges()) {
                oprot.writeFieldBegin(Table.PRIVILEGES_FIELD_DESC);
                struct.privileges.write(oprot);
                oprot.writeFieldEnd();
            }
            if (struct.isSetTemporary()) {
                oprot.writeFieldBegin(Table.TEMPORARY_FIELD_DESC);
                oprot.writeBool(struct.temporary);
                oprot.writeFieldEnd();
            }
            oprot.writeFieldStop();
            oprot.writeStructEnd();
        }
    }
    
    private static class TableTupleSchemeFactory implements SchemeFactory
    {
        @Override
        public TableTupleScheme getScheme() {
            return new TableTupleScheme();
        }
    }
    
    private static class TableTupleScheme extends TupleScheme<Table>
    {
        @Override
        public void write(final TProtocol prot, final Table struct) throws TException {
            final TTupleProtocol oprot = (TTupleProtocol)prot;
            final BitSet optionals = new BitSet();
            if (struct.isSetTableName()) {
                optionals.set(0);
            }
            if (struct.isSetDbName()) {
                optionals.set(1);
            }
            if (struct.isSetOwner()) {
                optionals.set(2);
            }
            if (struct.isSetCreateTime()) {
                optionals.set(3);
            }
            if (struct.isSetLastAccessTime()) {
                optionals.set(4);
            }
            if (struct.isSetRetention()) {
                optionals.set(5);
            }
            if (struct.isSetSd()) {
                optionals.set(6);
            }
            if (struct.isSetPartitionKeys()) {
                optionals.set(7);
            }
            if (struct.isSetParameters()) {
                optionals.set(8);
            }
            if (struct.isSetViewOriginalText()) {
                optionals.set(9);
            }
            if (struct.isSetViewExpandedText()) {
                optionals.set(10);
            }
            if (struct.isSetTableType()) {
                optionals.set(11);
            }
            if (struct.isSetPrivileges()) {
                optionals.set(12);
            }
            if (struct.isSetTemporary()) {
                optionals.set(13);
            }
            oprot.writeBitSet(optionals, 14);
            if (struct.isSetTableName()) {
                oprot.writeString(struct.tableName);
            }
            if (struct.isSetDbName()) {
                oprot.writeString(struct.dbName);
            }
            if (struct.isSetOwner()) {
                oprot.writeString(struct.owner);
            }
            if (struct.isSetCreateTime()) {
                oprot.writeI32(struct.createTime);
            }
            if (struct.isSetLastAccessTime()) {
                oprot.writeI32(struct.lastAccessTime);
            }
            if (struct.isSetRetention()) {
                oprot.writeI32(struct.retention);
            }
            if (struct.isSetSd()) {
                struct.sd.write(oprot);
            }
            if (struct.isSetPartitionKeys()) {
                oprot.writeI32(struct.partitionKeys.size());
                for (final FieldSchema _iter199 : struct.partitionKeys) {
                    _iter199.write(oprot);
                }
            }
            if (struct.isSetParameters()) {
                oprot.writeI32(struct.parameters.size());
                for (final Map.Entry<String, String> _iter200 : struct.parameters.entrySet()) {
                    oprot.writeString(_iter200.getKey());
                    oprot.writeString(_iter200.getValue());
                }
            }
            if (struct.isSetViewOriginalText()) {
                oprot.writeString(struct.viewOriginalText);
            }
            if (struct.isSetViewExpandedText()) {
                oprot.writeString(struct.viewExpandedText);
            }
            if (struct.isSetTableType()) {
                oprot.writeString(struct.tableType);
            }
            if (struct.isSetPrivileges()) {
                struct.privileges.write(oprot);
            }
            if (struct.isSetTemporary()) {
                oprot.writeBool(struct.temporary);
            }
        }
        
        @Override
        public void read(final TProtocol prot, final Table struct) throws TException {
            final TTupleProtocol iprot = (TTupleProtocol)prot;
            final BitSet incoming = iprot.readBitSet(14);
            if (incoming.get(0)) {
                struct.tableName = iprot.readString();
                struct.setTableNameIsSet(true);
            }
            if (incoming.get(1)) {
                struct.dbName = iprot.readString();
                struct.setDbNameIsSet(true);
            }
            if (incoming.get(2)) {
                struct.owner = iprot.readString();
                struct.setOwnerIsSet(true);
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
                struct.retention = iprot.readI32();
                struct.setRetentionIsSet(true);
            }
            if (incoming.get(6)) {
                struct.sd = new StorageDescriptor();
                struct.sd.read(iprot);
                struct.setSdIsSet(true);
            }
            if (incoming.get(7)) {
                final TList _list201 = new TList((byte)12, iprot.readI32());
                struct.partitionKeys = (List<FieldSchema>)new ArrayList(_list201.size);
                for (int _i202 = 0; _i202 < _list201.size; ++_i202) {
                    final FieldSchema _elem203 = new FieldSchema();
                    _elem203.read(iprot);
                    struct.partitionKeys.add(_elem203);
                }
                struct.setPartitionKeysIsSet(true);
            }
            if (incoming.get(8)) {
                final TMap _map204 = new TMap((byte)11, (byte)11, iprot.readI32());
                struct.parameters = (Map<String, String>)new HashMap(2 * _map204.size);
                for (int _i203 = 0; _i203 < _map204.size; ++_i203) {
                    final String _key206 = iprot.readString();
                    final String _val207 = iprot.readString();
                    struct.parameters.put(_key206, _val207);
                }
                struct.setParametersIsSet(true);
            }
            if (incoming.get(9)) {
                struct.viewOriginalText = iprot.readString();
                struct.setViewOriginalTextIsSet(true);
            }
            if (incoming.get(10)) {
                struct.viewExpandedText = iprot.readString();
                struct.setViewExpandedTextIsSet(true);
            }
            if (incoming.get(11)) {
                struct.tableType = iprot.readString();
                struct.setTableTypeIsSet(true);
            }
            if (incoming.get(12)) {
                struct.privileges = new PrincipalPrivilegeSet();
                struct.privileges.read(iprot);
                struct.setPrivilegesIsSet(true);
            }
            if (incoming.get(13)) {
                struct.temporary = iprot.readBool();
                struct.setTemporaryIsSet(true);
            }
        }
    }
}
