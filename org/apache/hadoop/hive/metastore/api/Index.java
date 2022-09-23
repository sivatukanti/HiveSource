// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.hive.metastore.api;

import java.util.BitSet;
import org.apache.thrift.protocol.TTupleProtocol;
import org.apache.thrift.protocol.TMap;
import org.apache.thrift.protocol.TProtocolUtil;
import java.util.EnumSet;
import java.util.Collections;
import org.apache.thrift.meta_data.MapMetaData;
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
import org.apache.thrift.meta_data.FieldMetaData;
import org.apache.thrift.scheme.SchemeFactory;
import org.apache.thrift.scheme.IScheme;
import java.util.Map;
import org.apache.thrift.protocol.TField;
import org.apache.thrift.protocol.TStruct;
import java.io.Serializable;
import org.apache.thrift.TBase;

public class Index implements TBase<Index, _Fields>, Serializable, Cloneable
{
    private static final TStruct STRUCT_DESC;
    private static final TField INDEX_NAME_FIELD_DESC;
    private static final TField INDEX_HANDLER_CLASS_FIELD_DESC;
    private static final TField DB_NAME_FIELD_DESC;
    private static final TField ORIG_TABLE_NAME_FIELD_DESC;
    private static final TField CREATE_TIME_FIELD_DESC;
    private static final TField LAST_ACCESS_TIME_FIELD_DESC;
    private static final TField INDEX_TABLE_NAME_FIELD_DESC;
    private static final TField SD_FIELD_DESC;
    private static final TField PARAMETERS_FIELD_DESC;
    private static final TField DEFERRED_REBUILD_FIELD_DESC;
    private static final Map<Class<? extends IScheme>, SchemeFactory> schemes;
    private String indexName;
    private String indexHandlerClass;
    private String dbName;
    private String origTableName;
    private int createTime;
    private int lastAccessTime;
    private String indexTableName;
    private StorageDescriptor sd;
    private Map<String, String> parameters;
    private boolean deferredRebuild;
    private static final int __CREATETIME_ISSET_ID = 0;
    private static final int __LASTACCESSTIME_ISSET_ID = 1;
    private static final int __DEFERREDREBUILD_ISSET_ID = 2;
    private byte __isset_bitfield;
    public static final Map<_Fields, FieldMetaData> metaDataMap;
    
    public Index() {
        this.__isset_bitfield = 0;
    }
    
    public Index(final String indexName, final String indexHandlerClass, final String dbName, final String origTableName, final int createTime, final int lastAccessTime, final String indexTableName, final StorageDescriptor sd, final Map<String, String> parameters, final boolean deferredRebuild) {
        this();
        this.indexName = indexName;
        this.indexHandlerClass = indexHandlerClass;
        this.dbName = dbName;
        this.origTableName = origTableName;
        this.createTime = createTime;
        this.setCreateTimeIsSet(true);
        this.lastAccessTime = lastAccessTime;
        this.setLastAccessTimeIsSet(true);
        this.indexTableName = indexTableName;
        this.sd = sd;
        this.parameters = parameters;
        this.deferredRebuild = deferredRebuild;
        this.setDeferredRebuildIsSet(true);
    }
    
    public Index(final Index other) {
        this.__isset_bitfield = 0;
        this.__isset_bitfield = other.__isset_bitfield;
        if (other.isSetIndexName()) {
            this.indexName = other.indexName;
        }
        if (other.isSetIndexHandlerClass()) {
            this.indexHandlerClass = other.indexHandlerClass;
        }
        if (other.isSetDbName()) {
            this.dbName = other.dbName;
        }
        if (other.isSetOrigTableName()) {
            this.origTableName = other.origTableName;
        }
        this.createTime = other.createTime;
        this.lastAccessTime = other.lastAccessTime;
        if (other.isSetIndexTableName()) {
            this.indexTableName = other.indexTableName;
        }
        if (other.isSetSd()) {
            this.sd = new StorageDescriptor(other.sd);
        }
        if (other.isSetParameters()) {
            final Map<String, String> __this__parameters = new HashMap<String, String>();
            for (final Map.Entry<String, String> other_element : other.parameters.entrySet()) {
                final String other_element_key = other_element.getKey();
                final String other_element_value = other_element.getValue();
                final String __this__parameters_copy_key = other_element_key;
                final String __this__parameters_copy_value = other_element_value;
                __this__parameters.put(__this__parameters_copy_key, __this__parameters_copy_value);
            }
            this.parameters = __this__parameters;
        }
        this.deferredRebuild = other.deferredRebuild;
    }
    
    @Override
    public Index deepCopy() {
        return new Index(this);
    }
    
    @Override
    public void clear() {
        this.indexName = null;
        this.indexHandlerClass = null;
        this.dbName = null;
        this.origTableName = null;
        this.setCreateTimeIsSet(false);
        this.createTime = 0;
        this.setLastAccessTimeIsSet(false);
        this.lastAccessTime = 0;
        this.indexTableName = null;
        this.sd = null;
        this.parameters = null;
        this.setDeferredRebuildIsSet(false);
        this.deferredRebuild = false;
    }
    
    public String getIndexName() {
        return this.indexName;
    }
    
    public void setIndexName(final String indexName) {
        this.indexName = indexName;
    }
    
    public void unsetIndexName() {
        this.indexName = null;
    }
    
    public boolean isSetIndexName() {
        return this.indexName != null;
    }
    
    public void setIndexNameIsSet(final boolean value) {
        if (!value) {
            this.indexName = null;
        }
    }
    
    public String getIndexHandlerClass() {
        return this.indexHandlerClass;
    }
    
    public void setIndexHandlerClass(final String indexHandlerClass) {
        this.indexHandlerClass = indexHandlerClass;
    }
    
    public void unsetIndexHandlerClass() {
        this.indexHandlerClass = null;
    }
    
    public boolean isSetIndexHandlerClass() {
        return this.indexHandlerClass != null;
    }
    
    public void setIndexHandlerClassIsSet(final boolean value) {
        if (!value) {
            this.indexHandlerClass = null;
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
    
    public String getOrigTableName() {
        return this.origTableName;
    }
    
    public void setOrigTableName(final String origTableName) {
        this.origTableName = origTableName;
    }
    
    public void unsetOrigTableName() {
        this.origTableName = null;
    }
    
    public boolean isSetOrigTableName() {
        return this.origTableName != null;
    }
    
    public void setOrigTableNameIsSet(final boolean value) {
        if (!value) {
            this.origTableName = null;
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
    
    public String getIndexTableName() {
        return this.indexTableName;
    }
    
    public void setIndexTableName(final String indexTableName) {
        this.indexTableName = indexTableName;
    }
    
    public void unsetIndexTableName() {
        this.indexTableName = null;
    }
    
    public boolean isSetIndexTableName() {
        return this.indexTableName != null;
    }
    
    public void setIndexTableNameIsSet(final boolean value) {
        if (!value) {
            this.indexTableName = null;
        }
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
    
    public boolean isDeferredRebuild() {
        return this.deferredRebuild;
    }
    
    public void setDeferredRebuild(final boolean deferredRebuild) {
        this.deferredRebuild = deferredRebuild;
        this.setDeferredRebuildIsSet(true);
    }
    
    public void unsetDeferredRebuild() {
        this.__isset_bitfield = EncodingUtils.clearBit(this.__isset_bitfield, 2);
    }
    
    public boolean isSetDeferredRebuild() {
        return EncodingUtils.testBit(this.__isset_bitfield, 2);
    }
    
    public void setDeferredRebuildIsSet(final boolean value) {
        this.__isset_bitfield = EncodingUtils.setBit(this.__isset_bitfield, 2, value);
    }
    
    @Override
    public void setFieldValue(final _Fields field, final Object value) {
        switch (field) {
            case INDEX_NAME: {
                if (value == null) {
                    this.unsetIndexName();
                    break;
                }
                this.setIndexName((String)value);
                break;
            }
            case INDEX_HANDLER_CLASS: {
                if (value == null) {
                    this.unsetIndexHandlerClass();
                    break;
                }
                this.setIndexHandlerClass((String)value);
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
            case ORIG_TABLE_NAME: {
                if (value == null) {
                    this.unsetOrigTableName();
                    break;
                }
                this.setOrigTableName((String)value);
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
            case INDEX_TABLE_NAME: {
                if (value == null) {
                    this.unsetIndexTableName();
                    break;
                }
                this.setIndexTableName((String)value);
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
            case DEFERRED_REBUILD: {
                if (value == null) {
                    this.unsetDeferredRebuild();
                    break;
                }
                this.setDeferredRebuild((boolean)value);
                break;
            }
        }
    }
    
    @Override
    public Object getFieldValue(final _Fields field) {
        switch (field) {
            case INDEX_NAME: {
                return this.getIndexName();
            }
            case INDEX_HANDLER_CLASS: {
                return this.getIndexHandlerClass();
            }
            case DB_NAME: {
                return this.getDbName();
            }
            case ORIG_TABLE_NAME: {
                return this.getOrigTableName();
            }
            case CREATE_TIME: {
                return this.getCreateTime();
            }
            case LAST_ACCESS_TIME: {
                return this.getLastAccessTime();
            }
            case INDEX_TABLE_NAME: {
                return this.getIndexTableName();
            }
            case SD: {
                return this.getSd();
            }
            case PARAMETERS: {
                return this.getParameters();
            }
            case DEFERRED_REBUILD: {
                return this.isDeferredRebuild();
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
            case INDEX_NAME: {
                return this.isSetIndexName();
            }
            case INDEX_HANDLER_CLASS: {
                return this.isSetIndexHandlerClass();
            }
            case DB_NAME: {
                return this.isSetDbName();
            }
            case ORIG_TABLE_NAME: {
                return this.isSetOrigTableName();
            }
            case CREATE_TIME: {
                return this.isSetCreateTime();
            }
            case LAST_ACCESS_TIME: {
                return this.isSetLastAccessTime();
            }
            case INDEX_TABLE_NAME: {
                return this.isSetIndexTableName();
            }
            case SD: {
                return this.isSetSd();
            }
            case PARAMETERS: {
                return this.isSetParameters();
            }
            case DEFERRED_REBUILD: {
                return this.isSetDeferredRebuild();
            }
            default: {
                throw new IllegalStateException();
            }
        }
    }
    
    @Override
    public boolean equals(final Object that) {
        return that != null && that instanceof Index && this.equals((Index)that);
    }
    
    public boolean equals(final Index that) {
        if (that == null) {
            return false;
        }
        final boolean this_present_indexName = this.isSetIndexName();
        final boolean that_present_indexName = that.isSetIndexName();
        if (this_present_indexName || that_present_indexName) {
            if (!this_present_indexName || !that_present_indexName) {
                return false;
            }
            if (!this.indexName.equals(that.indexName)) {
                return false;
            }
        }
        final boolean this_present_indexHandlerClass = this.isSetIndexHandlerClass();
        final boolean that_present_indexHandlerClass = that.isSetIndexHandlerClass();
        if (this_present_indexHandlerClass || that_present_indexHandlerClass) {
            if (!this_present_indexHandlerClass || !that_present_indexHandlerClass) {
                return false;
            }
            if (!this.indexHandlerClass.equals(that.indexHandlerClass)) {
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
        final boolean this_present_origTableName = this.isSetOrigTableName();
        final boolean that_present_origTableName = that.isSetOrigTableName();
        if (this_present_origTableName || that_present_origTableName) {
            if (!this_present_origTableName || !that_present_origTableName) {
                return false;
            }
            if (!this.origTableName.equals(that.origTableName)) {
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
        final boolean this_present_indexTableName = this.isSetIndexTableName();
        final boolean that_present_indexTableName = that.isSetIndexTableName();
        if (this_present_indexTableName || that_present_indexTableName) {
            if (!this_present_indexTableName || !that_present_indexTableName) {
                return false;
            }
            if (!this.indexTableName.equals(that.indexTableName)) {
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
        final boolean this_present_deferredRebuild = true;
        final boolean that_present_deferredRebuild = true;
        if (this_present_deferredRebuild || that_present_deferredRebuild) {
            if (!this_present_deferredRebuild || !that_present_deferredRebuild) {
                return false;
            }
            if (this.deferredRebuild != that.deferredRebuild) {
                return false;
            }
        }
        return true;
    }
    
    @Override
    public int hashCode() {
        final HashCodeBuilder builder = new HashCodeBuilder();
        final boolean present_indexName = this.isSetIndexName();
        builder.append(present_indexName);
        if (present_indexName) {
            builder.append(this.indexName);
        }
        final boolean present_indexHandlerClass = this.isSetIndexHandlerClass();
        builder.append(present_indexHandlerClass);
        if (present_indexHandlerClass) {
            builder.append(this.indexHandlerClass);
        }
        final boolean present_dbName = this.isSetDbName();
        builder.append(present_dbName);
        if (present_dbName) {
            builder.append(this.dbName);
        }
        final boolean present_origTableName = this.isSetOrigTableName();
        builder.append(present_origTableName);
        if (present_origTableName) {
            builder.append(this.origTableName);
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
        final boolean present_indexTableName = this.isSetIndexTableName();
        builder.append(present_indexTableName);
        if (present_indexTableName) {
            builder.append(this.indexTableName);
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
        final boolean present_deferredRebuild = true;
        builder.append(present_deferredRebuild);
        if (present_deferredRebuild) {
            builder.append(this.deferredRebuild);
        }
        return builder.toHashCode();
    }
    
    @Override
    public int compareTo(final Index other) {
        if (!this.getClass().equals(other.getClass())) {
            return this.getClass().getName().compareTo(other.getClass().getName());
        }
        int lastComparison = 0;
        final Index typedOther = other;
        lastComparison = Boolean.valueOf(this.isSetIndexName()).compareTo(Boolean.valueOf(typedOther.isSetIndexName()));
        if (lastComparison != 0) {
            return lastComparison;
        }
        if (this.isSetIndexName()) {
            lastComparison = TBaseHelper.compareTo(this.indexName, typedOther.indexName);
            if (lastComparison != 0) {
                return lastComparison;
            }
        }
        lastComparison = Boolean.valueOf(this.isSetIndexHandlerClass()).compareTo(Boolean.valueOf(typedOther.isSetIndexHandlerClass()));
        if (lastComparison != 0) {
            return lastComparison;
        }
        if (this.isSetIndexHandlerClass()) {
            lastComparison = TBaseHelper.compareTo(this.indexHandlerClass, typedOther.indexHandlerClass);
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
        lastComparison = Boolean.valueOf(this.isSetOrigTableName()).compareTo(Boolean.valueOf(typedOther.isSetOrigTableName()));
        if (lastComparison != 0) {
            return lastComparison;
        }
        if (this.isSetOrigTableName()) {
            lastComparison = TBaseHelper.compareTo(this.origTableName, typedOther.origTableName);
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
        lastComparison = Boolean.valueOf(this.isSetIndexTableName()).compareTo(Boolean.valueOf(typedOther.isSetIndexTableName()));
        if (lastComparison != 0) {
            return lastComparison;
        }
        if (this.isSetIndexTableName()) {
            lastComparison = TBaseHelper.compareTo(this.indexTableName, typedOther.indexTableName);
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
        lastComparison = Boolean.valueOf(this.isSetDeferredRebuild()).compareTo(Boolean.valueOf(typedOther.isSetDeferredRebuild()));
        if (lastComparison != 0) {
            return lastComparison;
        }
        if (this.isSetDeferredRebuild()) {
            lastComparison = TBaseHelper.compareTo(this.deferredRebuild, typedOther.deferredRebuild);
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
        Index.schemes.get(iprot.getScheme()).getScheme().read(iprot, this);
    }
    
    @Override
    public void write(final TProtocol oprot) throws TException {
        Index.schemes.get(oprot.getScheme()).getScheme().write(oprot, this);
    }
    
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("Index(");
        boolean first = true;
        sb.append("indexName:");
        if (this.indexName == null) {
            sb.append("null");
        }
        else {
            sb.append(this.indexName);
        }
        first = false;
        if (!first) {
            sb.append(", ");
        }
        sb.append("indexHandlerClass:");
        if (this.indexHandlerClass == null) {
            sb.append("null");
        }
        else {
            sb.append(this.indexHandlerClass);
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
        sb.append("origTableName:");
        if (this.origTableName == null) {
            sb.append("null");
        }
        else {
            sb.append(this.origTableName);
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
        sb.append("indexTableName:");
        if (this.indexTableName == null) {
            sb.append("null");
        }
        else {
            sb.append(this.indexTableName);
        }
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
        if (!first) {
            sb.append(", ");
        }
        sb.append("deferredRebuild:");
        sb.append(this.deferredRebuild);
        first = false;
        sb.append(")");
        return sb.toString();
    }
    
    public void validate() throws TException {
        if (this.sd != null) {
            this.sd.validate();
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
        STRUCT_DESC = new TStruct("Index");
        INDEX_NAME_FIELD_DESC = new TField("indexName", (byte)11, (short)1);
        INDEX_HANDLER_CLASS_FIELD_DESC = new TField("indexHandlerClass", (byte)11, (short)2);
        DB_NAME_FIELD_DESC = new TField("dbName", (byte)11, (short)3);
        ORIG_TABLE_NAME_FIELD_DESC = new TField("origTableName", (byte)11, (short)4);
        CREATE_TIME_FIELD_DESC = new TField("createTime", (byte)8, (short)5);
        LAST_ACCESS_TIME_FIELD_DESC = new TField("lastAccessTime", (byte)8, (short)6);
        INDEX_TABLE_NAME_FIELD_DESC = new TField("indexTableName", (byte)11, (short)7);
        SD_FIELD_DESC = new TField("sd", (byte)12, (short)8);
        PARAMETERS_FIELD_DESC = new TField("parameters", (byte)13, (short)9);
        DEFERRED_REBUILD_FIELD_DESC = new TField("deferredRebuild", (byte)2, (short)10);
        (schemes = new HashMap<Class<? extends IScheme>, SchemeFactory>()).put(StandardScheme.class, new IndexStandardSchemeFactory());
        Index.schemes.put(TupleScheme.class, new IndexTupleSchemeFactory());
        final Map<_Fields, FieldMetaData> tmpMap = new EnumMap<_Fields, FieldMetaData>(_Fields.class);
        tmpMap.put(_Fields.INDEX_NAME, new FieldMetaData("indexName", (byte)3, new FieldValueMetaData((byte)11)));
        tmpMap.put(_Fields.INDEX_HANDLER_CLASS, new FieldMetaData("indexHandlerClass", (byte)3, new FieldValueMetaData((byte)11)));
        tmpMap.put(_Fields.DB_NAME, new FieldMetaData("dbName", (byte)3, new FieldValueMetaData((byte)11)));
        tmpMap.put(_Fields.ORIG_TABLE_NAME, new FieldMetaData("origTableName", (byte)3, new FieldValueMetaData((byte)11)));
        tmpMap.put(_Fields.CREATE_TIME, new FieldMetaData("createTime", (byte)3, new FieldValueMetaData((byte)8)));
        tmpMap.put(_Fields.LAST_ACCESS_TIME, new FieldMetaData("lastAccessTime", (byte)3, new FieldValueMetaData((byte)8)));
        tmpMap.put(_Fields.INDEX_TABLE_NAME, new FieldMetaData("indexTableName", (byte)3, new FieldValueMetaData((byte)11)));
        tmpMap.put(_Fields.SD, new FieldMetaData("sd", (byte)3, new StructMetaData((byte)12, StorageDescriptor.class)));
        tmpMap.put(_Fields.PARAMETERS, new FieldMetaData("parameters", (byte)3, new MapMetaData((byte)13, new FieldValueMetaData((byte)11), new FieldValueMetaData((byte)11))));
        tmpMap.put(_Fields.DEFERRED_REBUILD, new FieldMetaData("deferredRebuild", (byte)3, new FieldValueMetaData((byte)2)));
        FieldMetaData.addStructMetaDataMap(Index.class, metaDataMap = Collections.unmodifiableMap((Map<? extends _Fields, ? extends FieldMetaData>)tmpMap));
    }
    
    public enum _Fields implements TFieldIdEnum
    {
        INDEX_NAME((short)1, "indexName"), 
        INDEX_HANDLER_CLASS((short)2, "indexHandlerClass"), 
        DB_NAME((short)3, "dbName"), 
        ORIG_TABLE_NAME((short)4, "origTableName"), 
        CREATE_TIME((short)5, "createTime"), 
        LAST_ACCESS_TIME((short)6, "lastAccessTime"), 
        INDEX_TABLE_NAME((short)7, "indexTableName"), 
        SD((short)8, "sd"), 
        PARAMETERS((short)9, "parameters"), 
        DEFERRED_REBUILD((short)10, "deferredRebuild");
        
        private static final Map<String, _Fields> byName;
        private final short _thriftId;
        private final String _fieldName;
        
        public static _Fields findByThriftId(final int fieldId) {
            switch (fieldId) {
                case 1: {
                    return _Fields.INDEX_NAME;
                }
                case 2: {
                    return _Fields.INDEX_HANDLER_CLASS;
                }
                case 3: {
                    return _Fields.DB_NAME;
                }
                case 4: {
                    return _Fields.ORIG_TABLE_NAME;
                }
                case 5: {
                    return _Fields.CREATE_TIME;
                }
                case 6: {
                    return _Fields.LAST_ACCESS_TIME;
                }
                case 7: {
                    return _Fields.INDEX_TABLE_NAME;
                }
                case 8: {
                    return _Fields.SD;
                }
                case 9: {
                    return _Fields.PARAMETERS;
                }
                case 10: {
                    return _Fields.DEFERRED_REBUILD;
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
    
    private static class IndexStandardSchemeFactory implements SchemeFactory
    {
        @Override
        public IndexStandardScheme getScheme() {
            return new IndexStandardScheme();
        }
    }
    
    private static class IndexStandardScheme extends StandardScheme<Index>
    {
        @Override
        public void read(final TProtocol iprot, final Index struct) throws TException {
            iprot.readStructBegin();
            while (true) {
                final TField schemeField = iprot.readFieldBegin();
                if (schemeField.type == 0) {
                    break;
                }
                switch (schemeField.id) {
                    case 1: {
                        if (schemeField.type == 11) {
                            struct.indexName = iprot.readString();
                            struct.setIndexNameIsSet(true);
                            break;
                        }
                        TProtocolUtil.skip(iprot, schemeField.type);
                        break;
                    }
                    case 2: {
                        if (schemeField.type == 11) {
                            struct.indexHandlerClass = iprot.readString();
                            struct.setIndexHandlerClassIsSet(true);
                            break;
                        }
                        TProtocolUtil.skip(iprot, schemeField.type);
                        break;
                    }
                    case 3: {
                        if (schemeField.type == 11) {
                            struct.dbName = iprot.readString();
                            struct.setDbNameIsSet(true);
                            break;
                        }
                        TProtocolUtil.skip(iprot, schemeField.type);
                        break;
                    }
                    case 4: {
                        if (schemeField.type == 11) {
                            struct.origTableName = iprot.readString();
                            struct.setOrigTableNameIsSet(true);
                            break;
                        }
                        TProtocolUtil.skip(iprot, schemeField.type);
                        break;
                    }
                    case 5: {
                        if (schemeField.type == 8) {
                            struct.createTime = iprot.readI32();
                            struct.setCreateTimeIsSet(true);
                            break;
                        }
                        TProtocolUtil.skip(iprot, schemeField.type);
                        break;
                    }
                    case 6: {
                        if (schemeField.type == 8) {
                            struct.lastAccessTime = iprot.readI32();
                            struct.setLastAccessTimeIsSet(true);
                            break;
                        }
                        TProtocolUtil.skip(iprot, schemeField.type);
                        break;
                    }
                    case 7: {
                        if (schemeField.type == 11) {
                            struct.indexTableName = iprot.readString();
                            struct.setIndexTableNameIsSet(true);
                            break;
                        }
                        TProtocolUtil.skip(iprot, schemeField.type);
                        break;
                    }
                    case 8: {
                        if (schemeField.type == 12) {
                            struct.sd = new StorageDescriptor();
                            struct.sd.read(iprot);
                            struct.setSdIsSet(true);
                            break;
                        }
                        TProtocolUtil.skip(iprot, schemeField.type);
                        break;
                    }
                    case 9: {
                        if (schemeField.type == 13) {
                            final TMap _map260 = iprot.readMapBegin();
                            struct.parameters = (Map<String, String>)new HashMap(2 * _map260.size);
                            for (int _i261 = 0; _i261 < _map260.size; ++_i261) {
                                final String _key262 = iprot.readString();
                                final String _val263 = iprot.readString();
                                struct.parameters.put(_key262, _val263);
                            }
                            iprot.readMapEnd();
                            struct.setParametersIsSet(true);
                            break;
                        }
                        TProtocolUtil.skip(iprot, schemeField.type);
                        break;
                    }
                    case 10: {
                        if (schemeField.type == 2) {
                            struct.deferredRebuild = iprot.readBool();
                            struct.setDeferredRebuildIsSet(true);
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
        public void write(final TProtocol oprot, final Index struct) throws TException {
            struct.validate();
            oprot.writeStructBegin(Index.STRUCT_DESC);
            if (struct.indexName != null) {
                oprot.writeFieldBegin(Index.INDEX_NAME_FIELD_DESC);
                oprot.writeString(struct.indexName);
                oprot.writeFieldEnd();
            }
            if (struct.indexHandlerClass != null) {
                oprot.writeFieldBegin(Index.INDEX_HANDLER_CLASS_FIELD_DESC);
                oprot.writeString(struct.indexHandlerClass);
                oprot.writeFieldEnd();
            }
            if (struct.dbName != null) {
                oprot.writeFieldBegin(Index.DB_NAME_FIELD_DESC);
                oprot.writeString(struct.dbName);
                oprot.writeFieldEnd();
            }
            if (struct.origTableName != null) {
                oprot.writeFieldBegin(Index.ORIG_TABLE_NAME_FIELD_DESC);
                oprot.writeString(struct.origTableName);
                oprot.writeFieldEnd();
            }
            oprot.writeFieldBegin(Index.CREATE_TIME_FIELD_DESC);
            oprot.writeI32(struct.createTime);
            oprot.writeFieldEnd();
            oprot.writeFieldBegin(Index.LAST_ACCESS_TIME_FIELD_DESC);
            oprot.writeI32(struct.lastAccessTime);
            oprot.writeFieldEnd();
            if (struct.indexTableName != null) {
                oprot.writeFieldBegin(Index.INDEX_TABLE_NAME_FIELD_DESC);
                oprot.writeString(struct.indexTableName);
                oprot.writeFieldEnd();
            }
            if (struct.sd != null) {
                oprot.writeFieldBegin(Index.SD_FIELD_DESC);
                struct.sd.write(oprot);
                oprot.writeFieldEnd();
            }
            if (struct.parameters != null) {
                oprot.writeFieldBegin(Index.PARAMETERS_FIELD_DESC);
                oprot.writeMapBegin(new TMap((byte)11, (byte)11, struct.parameters.size()));
                for (final Map.Entry<String, String> _iter264 : struct.parameters.entrySet()) {
                    oprot.writeString(_iter264.getKey());
                    oprot.writeString(_iter264.getValue());
                }
                oprot.writeMapEnd();
                oprot.writeFieldEnd();
            }
            oprot.writeFieldBegin(Index.DEFERRED_REBUILD_FIELD_DESC);
            oprot.writeBool(struct.deferredRebuild);
            oprot.writeFieldEnd();
            oprot.writeFieldStop();
            oprot.writeStructEnd();
        }
    }
    
    private static class IndexTupleSchemeFactory implements SchemeFactory
    {
        @Override
        public IndexTupleScheme getScheme() {
            return new IndexTupleScheme();
        }
    }
    
    private static class IndexTupleScheme extends TupleScheme<Index>
    {
        @Override
        public void write(final TProtocol prot, final Index struct) throws TException {
            final TTupleProtocol oprot = (TTupleProtocol)prot;
            final BitSet optionals = new BitSet();
            if (struct.isSetIndexName()) {
                optionals.set(0);
            }
            if (struct.isSetIndexHandlerClass()) {
                optionals.set(1);
            }
            if (struct.isSetDbName()) {
                optionals.set(2);
            }
            if (struct.isSetOrigTableName()) {
                optionals.set(3);
            }
            if (struct.isSetCreateTime()) {
                optionals.set(4);
            }
            if (struct.isSetLastAccessTime()) {
                optionals.set(5);
            }
            if (struct.isSetIndexTableName()) {
                optionals.set(6);
            }
            if (struct.isSetSd()) {
                optionals.set(7);
            }
            if (struct.isSetParameters()) {
                optionals.set(8);
            }
            if (struct.isSetDeferredRebuild()) {
                optionals.set(9);
            }
            oprot.writeBitSet(optionals, 10);
            if (struct.isSetIndexName()) {
                oprot.writeString(struct.indexName);
            }
            if (struct.isSetIndexHandlerClass()) {
                oprot.writeString(struct.indexHandlerClass);
            }
            if (struct.isSetDbName()) {
                oprot.writeString(struct.dbName);
            }
            if (struct.isSetOrigTableName()) {
                oprot.writeString(struct.origTableName);
            }
            if (struct.isSetCreateTime()) {
                oprot.writeI32(struct.createTime);
            }
            if (struct.isSetLastAccessTime()) {
                oprot.writeI32(struct.lastAccessTime);
            }
            if (struct.isSetIndexTableName()) {
                oprot.writeString(struct.indexTableName);
            }
            if (struct.isSetSd()) {
                struct.sd.write(oprot);
            }
            if (struct.isSetParameters()) {
                oprot.writeI32(struct.parameters.size());
                for (final Map.Entry<String, String> _iter265 : struct.parameters.entrySet()) {
                    oprot.writeString(_iter265.getKey());
                    oprot.writeString(_iter265.getValue());
                }
            }
            if (struct.isSetDeferredRebuild()) {
                oprot.writeBool(struct.deferredRebuild);
            }
        }
        
        @Override
        public void read(final TProtocol prot, final Index struct) throws TException {
            final TTupleProtocol iprot = (TTupleProtocol)prot;
            final BitSet incoming = iprot.readBitSet(10);
            if (incoming.get(0)) {
                struct.indexName = iprot.readString();
                struct.setIndexNameIsSet(true);
            }
            if (incoming.get(1)) {
                struct.indexHandlerClass = iprot.readString();
                struct.setIndexHandlerClassIsSet(true);
            }
            if (incoming.get(2)) {
                struct.dbName = iprot.readString();
                struct.setDbNameIsSet(true);
            }
            if (incoming.get(3)) {
                struct.origTableName = iprot.readString();
                struct.setOrigTableNameIsSet(true);
            }
            if (incoming.get(4)) {
                struct.createTime = iprot.readI32();
                struct.setCreateTimeIsSet(true);
            }
            if (incoming.get(5)) {
                struct.lastAccessTime = iprot.readI32();
                struct.setLastAccessTimeIsSet(true);
            }
            if (incoming.get(6)) {
                struct.indexTableName = iprot.readString();
                struct.setIndexTableNameIsSet(true);
            }
            if (incoming.get(7)) {
                struct.sd = new StorageDescriptor();
                struct.sd.read(iprot);
                struct.setSdIsSet(true);
            }
            if (incoming.get(8)) {
                final TMap _map266 = new TMap((byte)11, (byte)11, iprot.readI32());
                struct.parameters = (Map<String, String>)new HashMap(2 * _map266.size);
                for (int _i267 = 0; _i267 < _map266.size; ++_i267) {
                    final String _key268 = iprot.readString();
                    final String _val269 = iprot.readString();
                    struct.parameters.put(_key268, _val269);
                }
                struct.setParametersIsSet(true);
            }
            if (incoming.get(9)) {
                struct.deferredRebuild = iprot.readBool();
                struct.setDeferredRebuildIsSet(true);
            }
        }
    }
}
