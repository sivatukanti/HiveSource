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
import org.apache.thrift.TEnum;
import org.apache.thrift.meta_data.EnumMetaData;
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
import org.apache.thrift.EncodingUtils;
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

public class Function implements TBase<Function, _Fields>, Serializable, Cloneable
{
    private static final TStruct STRUCT_DESC;
    private static final TField FUNCTION_NAME_FIELD_DESC;
    private static final TField DB_NAME_FIELD_DESC;
    private static final TField CLASS_NAME_FIELD_DESC;
    private static final TField OWNER_NAME_FIELD_DESC;
    private static final TField OWNER_TYPE_FIELD_DESC;
    private static final TField CREATE_TIME_FIELD_DESC;
    private static final TField FUNCTION_TYPE_FIELD_DESC;
    private static final TField RESOURCE_URIS_FIELD_DESC;
    private static final Map<Class<? extends IScheme>, SchemeFactory> schemes;
    private String functionName;
    private String dbName;
    private String className;
    private String ownerName;
    private PrincipalType ownerType;
    private int createTime;
    private FunctionType functionType;
    private List<ResourceUri> resourceUris;
    private static final int __CREATETIME_ISSET_ID = 0;
    private byte __isset_bitfield;
    public static final Map<_Fields, FieldMetaData> metaDataMap;
    
    public Function() {
        this.__isset_bitfield = 0;
    }
    
    public Function(final String functionName, final String dbName, final String className, final String ownerName, final PrincipalType ownerType, final int createTime, final FunctionType functionType, final List<ResourceUri> resourceUris) {
        this();
        this.functionName = functionName;
        this.dbName = dbName;
        this.className = className;
        this.ownerName = ownerName;
        this.ownerType = ownerType;
        this.createTime = createTime;
        this.setCreateTimeIsSet(true);
        this.functionType = functionType;
        this.resourceUris = resourceUris;
    }
    
    public Function(final Function other) {
        this.__isset_bitfield = 0;
        this.__isset_bitfield = other.__isset_bitfield;
        if (other.isSetFunctionName()) {
            this.functionName = other.functionName;
        }
        if (other.isSetDbName()) {
            this.dbName = other.dbName;
        }
        if (other.isSetClassName()) {
            this.className = other.className;
        }
        if (other.isSetOwnerName()) {
            this.ownerName = other.ownerName;
        }
        if (other.isSetOwnerType()) {
            this.ownerType = other.ownerType;
        }
        this.createTime = other.createTime;
        if (other.isSetFunctionType()) {
            this.functionType = other.functionType;
        }
        if (other.isSetResourceUris()) {
            final List<ResourceUri> __this__resourceUris = new ArrayList<ResourceUri>();
            for (final ResourceUri other_element : other.resourceUris) {
                __this__resourceUris.add(new ResourceUri(other_element));
            }
            this.resourceUris = __this__resourceUris;
        }
    }
    
    @Override
    public Function deepCopy() {
        return new Function(this);
    }
    
    @Override
    public void clear() {
        this.functionName = null;
        this.dbName = null;
        this.className = null;
        this.ownerName = null;
        this.ownerType = null;
        this.setCreateTimeIsSet(false);
        this.createTime = 0;
        this.functionType = null;
        this.resourceUris = null;
    }
    
    public String getFunctionName() {
        return this.functionName;
    }
    
    public void setFunctionName(final String functionName) {
        this.functionName = functionName;
    }
    
    public void unsetFunctionName() {
        this.functionName = null;
    }
    
    public boolean isSetFunctionName() {
        return this.functionName != null;
    }
    
    public void setFunctionNameIsSet(final boolean value) {
        if (!value) {
            this.functionName = null;
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
    
    public String getClassName() {
        return this.className;
    }
    
    public void setClassName(final String className) {
        this.className = className;
    }
    
    public void unsetClassName() {
        this.className = null;
    }
    
    public boolean isSetClassName() {
        return this.className != null;
    }
    
    public void setClassNameIsSet(final boolean value) {
        if (!value) {
            this.className = null;
        }
    }
    
    public String getOwnerName() {
        return this.ownerName;
    }
    
    public void setOwnerName(final String ownerName) {
        this.ownerName = ownerName;
    }
    
    public void unsetOwnerName() {
        this.ownerName = null;
    }
    
    public boolean isSetOwnerName() {
        return this.ownerName != null;
    }
    
    public void setOwnerNameIsSet(final boolean value) {
        if (!value) {
            this.ownerName = null;
        }
    }
    
    public PrincipalType getOwnerType() {
        return this.ownerType;
    }
    
    public void setOwnerType(final PrincipalType ownerType) {
        this.ownerType = ownerType;
    }
    
    public void unsetOwnerType() {
        this.ownerType = null;
    }
    
    public boolean isSetOwnerType() {
        return this.ownerType != null;
    }
    
    public void setOwnerTypeIsSet(final boolean value) {
        if (!value) {
            this.ownerType = null;
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
    
    public FunctionType getFunctionType() {
        return this.functionType;
    }
    
    public void setFunctionType(final FunctionType functionType) {
        this.functionType = functionType;
    }
    
    public void unsetFunctionType() {
        this.functionType = null;
    }
    
    public boolean isSetFunctionType() {
        return this.functionType != null;
    }
    
    public void setFunctionTypeIsSet(final boolean value) {
        if (!value) {
            this.functionType = null;
        }
    }
    
    public int getResourceUrisSize() {
        return (this.resourceUris == null) ? 0 : this.resourceUris.size();
    }
    
    public Iterator<ResourceUri> getResourceUrisIterator() {
        return (this.resourceUris == null) ? null : this.resourceUris.iterator();
    }
    
    public void addToResourceUris(final ResourceUri elem) {
        if (this.resourceUris == null) {
            this.resourceUris = new ArrayList<ResourceUri>();
        }
        this.resourceUris.add(elem);
    }
    
    public List<ResourceUri> getResourceUris() {
        return this.resourceUris;
    }
    
    public void setResourceUris(final List<ResourceUri> resourceUris) {
        this.resourceUris = resourceUris;
    }
    
    public void unsetResourceUris() {
        this.resourceUris = null;
    }
    
    public boolean isSetResourceUris() {
        return this.resourceUris != null;
    }
    
    public void setResourceUrisIsSet(final boolean value) {
        if (!value) {
            this.resourceUris = null;
        }
    }
    
    @Override
    public void setFieldValue(final _Fields field, final Object value) {
        switch (field) {
            case FUNCTION_NAME: {
                if (value == null) {
                    this.unsetFunctionName();
                    break;
                }
                this.setFunctionName((String)value);
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
            case CLASS_NAME: {
                if (value == null) {
                    this.unsetClassName();
                    break;
                }
                this.setClassName((String)value);
                break;
            }
            case OWNER_NAME: {
                if (value == null) {
                    this.unsetOwnerName();
                    break;
                }
                this.setOwnerName((String)value);
                break;
            }
            case OWNER_TYPE: {
                if (value == null) {
                    this.unsetOwnerType();
                    break;
                }
                this.setOwnerType((PrincipalType)value);
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
            case FUNCTION_TYPE: {
                if (value == null) {
                    this.unsetFunctionType();
                    break;
                }
                this.setFunctionType((FunctionType)value);
                break;
            }
            case RESOURCE_URIS: {
                if (value == null) {
                    this.unsetResourceUris();
                    break;
                }
                this.setResourceUris((List<ResourceUri>)value);
                break;
            }
        }
    }
    
    @Override
    public Object getFieldValue(final _Fields field) {
        switch (field) {
            case FUNCTION_NAME: {
                return this.getFunctionName();
            }
            case DB_NAME: {
                return this.getDbName();
            }
            case CLASS_NAME: {
                return this.getClassName();
            }
            case OWNER_NAME: {
                return this.getOwnerName();
            }
            case OWNER_TYPE: {
                return this.getOwnerType();
            }
            case CREATE_TIME: {
                return this.getCreateTime();
            }
            case FUNCTION_TYPE: {
                return this.getFunctionType();
            }
            case RESOURCE_URIS: {
                return this.getResourceUris();
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
            case FUNCTION_NAME: {
                return this.isSetFunctionName();
            }
            case DB_NAME: {
                return this.isSetDbName();
            }
            case CLASS_NAME: {
                return this.isSetClassName();
            }
            case OWNER_NAME: {
                return this.isSetOwnerName();
            }
            case OWNER_TYPE: {
                return this.isSetOwnerType();
            }
            case CREATE_TIME: {
                return this.isSetCreateTime();
            }
            case FUNCTION_TYPE: {
                return this.isSetFunctionType();
            }
            case RESOURCE_URIS: {
                return this.isSetResourceUris();
            }
            default: {
                throw new IllegalStateException();
            }
        }
    }
    
    @Override
    public boolean equals(final Object that) {
        return that != null && that instanceof Function && this.equals((Function)that);
    }
    
    public boolean equals(final Function that) {
        if (that == null) {
            return false;
        }
        final boolean this_present_functionName = this.isSetFunctionName();
        final boolean that_present_functionName = that.isSetFunctionName();
        if (this_present_functionName || that_present_functionName) {
            if (!this_present_functionName || !that_present_functionName) {
                return false;
            }
            if (!this.functionName.equals(that.functionName)) {
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
        final boolean this_present_className = this.isSetClassName();
        final boolean that_present_className = that.isSetClassName();
        if (this_present_className || that_present_className) {
            if (!this_present_className || !that_present_className) {
                return false;
            }
            if (!this.className.equals(that.className)) {
                return false;
            }
        }
        final boolean this_present_ownerName = this.isSetOwnerName();
        final boolean that_present_ownerName = that.isSetOwnerName();
        if (this_present_ownerName || that_present_ownerName) {
            if (!this_present_ownerName || !that_present_ownerName) {
                return false;
            }
            if (!this.ownerName.equals(that.ownerName)) {
                return false;
            }
        }
        final boolean this_present_ownerType = this.isSetOwnerType();
        final boolean that_present_ownerType = that.isSetOwnerType();
        if (this_present_ownerType || that_present_ownerType) {
            if (!this_present_ownerType || !that_present_ownerType) {
                return false;
            }
            if (!this.ownerType.equals(that.ownerType)) {
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
        final boolean this_present_functionType = this.isSetFunctionType();
        final boolean that_present_functionType = that.isSetFunctionType();
        if (this_present_functionType || that_present_functionType) {
            if (!this_present_functionType || !that_present_functionType) {
                return false;
            }
            if (!this.functionType.equals(that.functionType)) {
                return false;
            }
        }
        final boolean this_present_resourceUris = this.isSetResourceUris();
        final boolean that_present_resourceUris = that.isSetResourceUris();
        if (this_present_resourceUris || that_present_resourceUris) {
            if (!this_present_resourceUris || !that_present_resourceUris) {
                return false;
            }
            if (!this.resourceUris.equals(that.resourceUris)) {
                return false;
            }
        }
        return true;
    }
    
    @Override
    public int hashCode() {
        final HashCodeBuilder builder = new HashCodeBuilder();
        final boolean present_functionName = this.isSetFunctionName();
        builder.append(present_functionName);
        if (present_functionName) {
            builder.append(this.functionName);
        }
        final boolean present_dbName = this.isSetDbName();
        builder.append(present_dbName);
        if (present_dbName) {
            builder.append(this.dbName);
        }
        final boolean present_className = this.isSetClassName();
        builder.append(present_className);
        if (present_className) {
            builder.append(this.className);
        }
        final boolean present_ownerName = this.isSetOwnerName();
        builder.append(present_ownerName);
        if (present_ownerName) {
            builder.append(this.ownerName);
        }
        final boolean present_ownerType = this.isSetOwnerType();
        builder.append(present_ownerType);
        if (present_ownerType) {
            builder.append(this.ownerType.getValue());
        }
        final boolean present_createTime = true;
        builder.append(present_createTime);
        if (present_createTime) {
            builder.append(this.createTime);
        }
        final boolean present_functionType = this.isSetFunctionType();
        builder.append(present_functionType);
        if (present_functionType) {
            builder.append(this.functionType.getValue());
        }
        final boolean present_resourceUris = this.isSetResourceUris();
        builder.append(present_resourceUris);
        if (present_resourceUris) {
            builder.append(this.resourceUris);
        }
        return builder.toHashCode();
    }
    
    @Override
    public int compareTo(final Function other) {
        if (!this.getClass().equals(other.getClass())) {
            return this.getClass().getName().compareTo(other.getClass().getName());
        }
        int lastComparison = 0;
        final Function typedOther = other;
        lastComparison = Boolean.valueOf(this.isSetFunctionName()).compareTo(Boolean.valueOf(typedOther.isSetFunctionName()));
        if (lastComparison != 0) {
            return lastComparison;
        }
        if (this.isSetFunctionName()) {
            lastComparison = TBaseHelper.compareTo(this.functionName, typedOther.functionName);
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
        lastComparison = Boolean.valueOf(this.isSetClassName()).compareTo(Boolean.valueOf(typedOther.isSetClassName()));
        if (lastComparison != 0) {
            return lastComparison;
        }
        if (this.isSetClassName()) {
            lastComparison = TBaseHelper.compareTo(this.className, typedOther.className);
            if (lastComparison != 0) {
                return lastComparison;
            }
        }
        lastComparison = Boolean.valueOf(this.isSetOwnerName()).compareTo(Boolean.valueOf(typedOther.isSetOwnerName()));
        if (lastComparison != 0) {
            return lastComparison;
        }
        if (this.isSetOwnerName()) {
            lastComparison = TBaseHelper.compareTo(this.ownerName, typedOther.ownerName);
            if (lastComparison != 0) {
                return lastComparison;
            }
        }
        lastComparison = Boolean.valueOf(this.isSetOwnerType()).compareTo(Boolean.valueOf(typedOther.isSetOwnerType()));
        if (lastComparison != 0) {
            return lastComparison;
        }
        if (this.isSetOwnerType()) {
            lastComparison = TBaseHelper.compareTo(this.ownerType, typedOther.ownerType);
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
        lastComparison = Boolean.valueOf(this.isSetFunctionType()).compareTo(Boolean.valueOf(typedOther.isSetFunctionType()));
        if (lastComparison != 0) {
            return lastComparison;
        }
        if (this.isSetFunctionType()) {
            lastComparison = TBaseHelper.compareTo(this.functionType, typedOther.functionType);
            if (lastComparison != 0) {
                return lastComparison;
            }
        }
        lastComparison = Boolean.valueOf(this.isSetResourceUris()).compareTo(Boolean.valueOf(typedOther.isSetResourceUris()));
        if (lastComparison != 0) {
            return lastComparison;
        }
        if (this.isSetResourceUris()) {
            lastComparison = TBaseHelper.compareTo(this.resourceUris, typedOther.resourceUris);
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
        Function.schemes.get(iprot.getScheme()).getScheme().read(iprot, this);
    }
    
    @Override
    public void write(final TProtocol oprot) throws TException {
        Function.schemes.get(oprot.getScheme()).getScheme().write(oprot, this);
    }
    
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("Function(");
        boolean first = true;
        sb.append("functionName:");
        if (this.functionName == null) {
            sb.append("null");
        }
        else {
            sb.append(this.functionName);
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
        sb.append("className:");
        if (this.className == null) {
            sb.append("null");
        }
        else {
            sb.append(this.className);
        }
        first = false;
        if (!first) {
            sb.append(", ");
        }
        sb.append("ownerName:");
        if (this.ownerName == null) {
            sb.append("null");
        }
        else {
            sb.append(this.ownerName);
        }
        first = false;
        if (!first) {
            sb.append(", ");
        }
        sb.append("ownerType:");
        if (this.ownerType == null) {
            sb.append("null");
        }
        else {
            sb.append(this.ownerType);
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
        sb.append("functionType:");
        if (this.functionType == null) {
            sb.append("null");
        }
        else {
            sb.append(this.functionType);
        }
        first = false;
        if (!first) {
            sb.append(", ");
        }
        sb.append("resourceUris:");
        if (this.resourceUris == null) {
            sb.append("null");
        }
        else {
            sb.append(this.resourceUris);
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
            this.__isset_bitfield = 0;
            this.read(new TCompactProtocol(new TIOStreamTransport(in)));
        }
        catch (TException te) {
            throw new IOException(te);
        }
    }
    
    static {
        STRUCT_DESC = new TStruct("Function");
        FUNCTION_NAME_FIELD_DESC = new TField("functionName", (byte)11, (short)1);
        DB_NAME_FIELD_DESC = new TField("dbName", (byte)11, (short)2);
        CLASS_NAME_FIELD_DESC = new TField("className", (byte)11, (short)3);
        OWNER_NAME_FIELD_DESC = new TField("ownerName", (byte)11, (short)4);
        OWNER_TYPE_FIELD_DESC = new TField("ownerType", (byte)8, (short)5);
        CREATE_TIME_FIELD_DESC = new TField("createTime", (byte)8, (short)6);
        FUNCTION_TYPE_FIELD_DESC = new TField("functionType", (byte)8, (short)7);
        RESOURCE_URIS_FIELD_DESC = new TField("resourceUris", (byte)15, (short)8);
        (schemes = new HashMap<Class<? extends IScheme>, SchemeFactory>()).put(StandardScheme.class, new FunctionStandardSchemeFactory());
        Function.schemes.put(TupleScheme.class, new FunctionTupleSchemeFactory());
        final Map<_Fields, FieldMetaData> tmpMap = new EnumMap<_Fields, FieldMetaData>(_Fields.class);
        tmpMap.put(_Fields.FUNCTION_NAME, new FieldMetaData("functionName", (byte)3, new FieldValueMetaData((byte)11)));
        tmpMap.put(_Fields.DB_NAME, new FieldMetaData("dbName", (byte)3, new FieldValueMetaData((byte)11)));
        tmpMap.put(_Fields.CLASS_NAME, new FieldMetaData("className", (byte)3, new FieldValueMetaData((byte)11)));
        tmpMap.put(_Fields.OWNER_NAME, new FieldMetaData("ownerName", (byte)3, new FieldValueMetaData((byte)11)));
        tmpMap.put(_Fields.OWNER_TYPE, new FieldMetaData("ownerType", (byte)3, new EnumMetaData((byte)16, PrincipalType.class)));
        tmpMap.put(_Fields.CREATE_TIME, new FieldMetaData("createTime", (byte)3, new FieldValueMetaData((byte)8)));
        tmpMap.put(_Fields.FUNCTION_TYPE, new FieldMetaData("functionType", (byte)3, new EnumMetaData((byte)16, FunctionType.class)));
        tmpMap.put(_Fields.RESOURCE_URIS, new FieldMetaData("resourceUris", (byte)3, new ListMetaData((byte)15, new StructMetaData((byte)12, ResourceUri.class))));
        FieldMetaData.addStructMetaDataMap(Function.class, metaDataMap = Collections.unmodifiableMap((Map<? extends _Fields, ? extends FieldMetaData>)tmpMap));
    }
    
    public enum _Fields implements TFieldIdEnum
    {
        FUNCTION_NAME((short)1, "functionName"), 
        DB_NAME((short)2, "dbName"), 
        CLASS_NAME((short)3, "className"), 
        OWNER_NAME((short)4, "ownerName"), 
        OWNER_TYPE((short)5, "ownerType"), 
        CREATE_TIME((short)6, "createTime"), 
        FUNCTION_TYPE((short)7, "functionType"), 
        RESOURCE_URIS((short)8, "resourceUris");
        
        private static final Map<String, _Fields> byName;
        private final short _thriftId;
        private final String _fieldName;
        
        public static _Fields findByThriftId(final int fieldId) {
            switch (fieldId) {
                case 1: {
                    return _Fields.FUNCTION_NAME;
                }
                case 2: {
                    return _Fields.DB_NAME;
                }
                case 3: {
                    return _Fields.CLASS_NAME;
                }
                case 4: {
                    return _Fields.OWNER_NAME;
                }
                case 5: {
                    return _Fields.OWNER_TYPE;
                }
                case 6: {
                    return _Fields.CREATE_TIME;
                }
                case 7: {
                    return _Fields.FUNCTION_TYPE;
                }
                case 8: {
                    return _Fields.RESOURCE_URIS;
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
    
    private static class FunctionStandardSchemeFactory implements SchemeFactory
    {
        @Override
        public FunctionStandardScheme getScheme() {
            return new FunctionStandardScheme();
        }
    }
    
    private static class FunctionStandardScheme extends StandardScheme<Function>
    {
        @Override
        public void read(final TProtocol iprot, final Function struct) throws TException {
            iprot.readStructBegin();
            while (true) {
                final TField schemeField = iprot.readFieldBegin();
                if (schemeField.type == 0) {
                    break;
                }
                switch (schemeField.id) {
                    case 1: {
                        if (schemeField.type == 11) {
                            struct.functionName = iprot.readString();
                            struct.setFunctionNameIsSet(true);
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
                            struct.className = iprot.readString();
                            struct.setClassNameIsSet(true);
                            break;
                        }
                        TProtocolUtil.skip(iprot, schemeField.type);
                        break;
                    }
                    case 4: {
                        if (schemeField.type == 11) {
                            struct.ownerName = iprot.readString();
                            struct.setOwnerNameIsSet(true);
                            break;
                        }
                        TProtocolUtil.skip(iprot, schemeField.type);
                        break;
                    }
                    case 5: {
                        if (schemeField.type == 8) {
                            struct.ownerType = PrincipalType.findByValue(iprot.readI32());
                            struct.setOwnerTypeIsSet(true);
                            break;
                        }
                        TProtocolUtil.skip(iprot, schemeField.type);
                        break;
                    }
                    case 6: {
                        if (schemeField.type == 8) {
                            struct.createTime = iprot.readI32();
                            struct.setCreateTimeIsSet(true);
                            break;
                        }
                        TProtocolUtil.skip(iprot, schemeField.type);
                        break;
                    }
                    case 7: {
                        if (schemeField.type == 8) {
                            struct.functionType = FunctionType.findByValue(iprot.readI32());
                            struct.setFunctionTypeIsSet(true);
                            break;
                        }
                        TProtocolUtil.skip(iprot, schemeField.type);
                        break;
                    }
                    case 8: {
                        if (schemeField.type == 15) {
                            final TList _list420 = iprot.readListBegin();
                            struct.resourceUris = (List<ResourceUri>)new ArrayList(_list420.size);
                            for (int _i421 = 0; _i421 < _list420.size; ++_i421) {
                                final ResourceUri _elem422 = new ResourceUri();
                                _elem422.read(iprot);
                                struct.resourceUris.add(_elem422);
                            }
                            iprot.readListEnd();
                            struct.setResourceUrisIsSet(true);
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
        public void write(final TProtocol oprot, final Function struct) throws TException {
            struct.validate();
            oprot.writeStructBegin(Function.STRUCT_DESC);
            if (struct.functionName != null) {
                oprot.writeFieldBegin(Function.FUNCTION_NAME_FIELD_DESC);
                oprot.writeString(struct.functionName);
                oprot.writeFieldEnd();
            }
            if (struct.dbName != null) {
                oprot.writeFieldBegin(Function.DB_NAME_FIELD_DESC);
                oprot.writeString(struct.dbName);
                oprot.writeFieldEnd();
            }
            if (struct.className != null) {
                oprot.writeFieldBegin(Function.CLASS_NAME_FIELD_DESC);
                oprot.writeString(struct.className);
                oprot.writeFieldEnd();
            }
            if (struct.ownerName != null) {
                oprot.writeFieldBegin(Function.OWNER_NAME_FIELD_DESC);
                oprot.writeString(struct.ownerName);
                oprot.writeFieldEnd();
            }
            if (struct.ownerType != null) {
                oprot.writeFieldBegin(Function.OWNER_TYPE_FIELD_DESC);
                oprot.writeI32(struct.ownerType.getValue());
                oprot.writeFieldEnd();
            }
            oprot.writeFieldBegin(Function.CREATE_TIME_FIELD_DESC);
            oprot.writeI32(struct.createTime);
            oprot.writeFieldEnd();
            if (struct.functionType != null) {
                oprot.writeFieldBegin(Function.FUNCTION_TYPE_FIELD_DESC);
                oprot.writeI32(struct.functionType.getValue());
                oprot.writeFieldEnd();
            }
            if (struct.resourceUris != null) {
                oprot.writeFieldBegin(Function.RESOURCE_URIS_FIELD_DESC);
                oprot.writeListBegin(new TList((byte)12, struct.resourceUris.size()));
                for (final ResourceUri _iter423 : struct.resourceUris) {
                    _iter423.write(oprot);
                }
                oprot.writeListEnd();
                oprot.writeFieldEnd();
            }
            oprot.writeFieldStop();
            oprot.writeStructEnd();
        }
    }
    
    private static class FunctionTupleSchemeFactory implements SchemeFactory
    {
        @Override
        public FunctionTupleScheme getScheme() {
            return new FunctionTupleScheme();
        }
    }
    
    private static class FunctionTupleScheme extends TupleScheme<Function>
    {
        @Override
        public void write(final TProtocol prot, final Function struct) throws TException {
            final TTupleProtocol oprot = (TTupleProtocol)prot;
            final BitSet optionals = new BitSet();
            if (struct.isSetFunctionName()) {
                optionals.set(0);
            }
            if (struct.isSetDbName()) {
                optionals.set(1);
            }
            if (struct.isSetClassName()) {
                optionals.set(2);
            }
            if (struct.isSetOwnerName()) {
                optionals.set(3);
            }
            if (struct.isSetOwnerType()) {
                optionals.set(4);
            }
            if (struct.isSetCreateTime()) {
                optionals.set(5);
            }
            if (struct.isSetFunctionType()) {
                optionals.set(6);
            }
            if (struct.isSetResourceUris()) {
                optionals.set(7);
            }
            oprot.writeBitSet(optionals, 8);
            if (struct.isSetFunctionName()) {
                oprot.writeString(struct.functionName);
            }
            if (struct.isSetDbName()) {
                oprot.writeString(struct.dbName);
            }
            if (struct.isSetClassName()) {
                oprot.writeString(struct.className);
            }
            if (struct.isSetOwnerName()) {
                oprot.writeString(struct.ownerName);
            }
            if (struct.isSetOwnerType()) {
                oprot.writeI32(struct.ownerType.getValue());
            }
            if (struct.isSetCreateTime()) {
                oprot.writeI32(struct.createTime);
            }
            if (struct.isSetFunctionType()) {
                oprot.writeI32(struct.functionType.getValue());
            }
            if (struct.isSetResourceUris()) {
                oprot.writeI32(struct.resourceUris.size());
                for (final ResourceUri _iter424 : struct.resourceUris) {
                    _iter424.write(oprot);
                }
            }
        }
        
        @Override
        public void read(final TProtocol prot, final Function struct) throws TException {
            final TTupleProtocol iprot = (TTupleProtocol)prot;
            final BitSet incoming = iprot.readBitSet(8);
            if (incoming.get(0)) {
                struct.functionName = iprot.readString();
                struct.setFunctionNameIsSet(true);
            }
            if (incoming.get(1)) {
                struct.dbName = iprot.readString();
                struct.setDbNameIsSet(true);
            }
            if (incoming.get(2)) {
                struct.className = iprot.readString();
                struct.setClassNameIsSet(true);
            }
            if (incoming.get(3)) {
                struct.ownerName = iprot.readString();
                struct.setOwnerNameIsSet(true);
            }
            if (incoming.get(4)) {
                struct.ownerType = PrincipalType.findByValue(iprot.readI32());
                struct.setOwnerTypeIsSet(true);
            }
            if (incoming.get(5)) {
                struct.createTime = iprot.readI32();
                struct.setCreateTimeIsSet(true);
            }
            if (incoming.get(6)) {
                struct.functionType = FunctionType.findByValue(iprot.readI32());
                struct.setFunctionTypeIsSet(true);
            }
            if (incoming.get(7)) {
                final TList _list425 = new TList((byte)12, iprot.readI32());
                struct.resourceUris = (List<ResourceUri>)new ArrayList(_list425.size);
                for (int _i426 = 0; _i426 < _list425.size; ++_i426) {
                    final ResourceUri _elem427 = new ResourceUri();
                    _elem427.read(iprot);
                    struct.resourceUris.add(_elem427);
                }
                struct.setResourceUrisIsSet(true);
            }
        }
    }
}
