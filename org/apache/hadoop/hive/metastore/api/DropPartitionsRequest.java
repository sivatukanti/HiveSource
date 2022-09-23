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

public class DropPartitionsRequest implements TBase<DropPartitionsRequest, _Fields>, Serializable, Cloneable
{
    private static final TStruct STRUCT_DESC;
    private static final TField DB_NAME_FIELD_DESC;
    private static final TField TBL_NAME_FIELD_DESC;
    private static final TField PARTS_FIELD_DESC;
    private static final TField DELETE_DATA_FIELD_DESC;
    private static final TField IF_EXISTS_FIELD_DESC;
    private static final TField IGNORE_PROTECTION_FIELD_DESC;
    private static final TField ENVIRONMENT_CONTEXT_FIELD_DESC;
    private static final TField NEED_RESULT_FIELD_DESC;
    private static final Map<Class<? extends IScheme>, SchemeFactory> schemes;
    private String dbName;
    private String tblName;
    private RequestPartsSpec parts;
    private boolean deleteData;
    private boolean ifExists;
    private boolean ignoreProtection;
    private EnvironmentContext environmentContext;
    private boolean needResult;
    private static final int __DELETEDATA_ISSET_ID = 0;
    private static final int __IFEXISTS_ISSET_ID = 1;
    private static final int __IGNOREPROTECTION_ISSET_ID = 2;
    private static final int __NEEDRESULT_ISSET_ID = 3;
    private byte __isset_bitfield;
    private _Fields[] optionals;
    public static final Map<_Fields, FieldMetaData> metaDataMap;
    
    public DropPartitionsRequest() {
        this.__isset_bitfield = 0;
        this.optionals = new _Fields[] { _Fields.DELETE_DATA, _Fields.IF_EXISTS, _Fields.IGNORE_PROTECTION, _Fields.ENVIRONMENT_CONTEXT, _Fields.NEED_RESULT };
        this.ifExists = true;
        this.needResult = true;
    }
    
    public DropPartitionsRequest(final String dbName, final String tblName, final RequestPartsSpec parts) {
        this();
        this.dbName = dbName;
        this.tblName = tblName;
        this.parts = parts;
    }
    
    public DropPartitionsRequest(final DropPartitionsRequest other) {
        this.__isset_bitfield = 0;
        this.optionals = new _Fields[] { _Fields.DELETE_DATA, _Fields.IF_EXISTS, _Fields.IGNORE_PROTECTION, _Fields.ENVIRONMENT_CONTEXT, _Fields.NEED_RESULT };
        this.__isset_bitfield = other.__isset_bitfield;
        if (other.isSetDbName()) {
            this.dbName = other.dbName;
        }
        if (other.isSetTblName()) {
            this.tblName = other.tblName;
        }
        if (other.isSetParts()) {
            this.parts = new RequestPartsSpec(other.parts);
        }
        this.deleteData = other.deleteData;
        this.ifExists = other.ifExists;
        this.ignoreProtection = other.ignoreProtection;
        if (other.isSetEnvironmentContext()) {
            this.environmentContext = new EnvironmentContext(other.environmentContext);
        }
        this.needResult = other.needResult;
    }
    
    @Override
    public DropPartitionsRequest deepCopy() {
        return new DropPartitionsRequest(this);
    }
    
    @Override
    public void clear() {
        this.dbName = null;
        this.tblName = null;
        this.parts = null;
        this.setDeleteDataIsSet(false);
        this.deleteData = false;
        this.ifExists = true;
        this.setIgnoreProtectionIsSet(false);
        this.ignoreProtection = false;
        this.environmentContext = null;
        this.needResult = true;
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
    
    public String getTblName() {
        return this.tblName;
    }
    
    public void setTblName(final String tblName) {
        this.tblName = tblName;
    }
    
    public void unsetTblName() {
        this.tblName = null;
    }
    
    public boolean isSetTblName() {
        return this.tblName != null;
    }
    
    public void setTblNameIsSet(final boolean value) {
        if (!value) {
            this.tblName = null;
        }
    }
    
    public RequestPartsSpec getParts() {
        return this.parts;
    }
    
    public void setParts(final RequestPartsSpec parts) {
        this.parts = parts;
    }
    
    public void unsetParts() {
        this.parts = null;
    }
    
    public boolean isSetParts() {
        return this.parts != null;
    }
    
    public void setPartsIsSet(final boolean value) {
        if (!value) {
            this.parts = null;
        }
    }
    
    public boolean isDeleteData() {
        return this.deleteData;
    }
    
    public void setDeleteData(final boolean deleteData) {
        this.deleteData = deleteData;
        this.setDeleteDataIsSet(true);
    }
    
    public void unsetDeleteData() {
        this.__isset_bitfield = EncodingUtils.clearBit(this.__isset_bitfield, 0);
    }
    
    public boolean isSetDeleteData() {
        return EncodingUtils.testBit(this.__isset_bitfield, 0);
    }
    
    public void setDeleteDataIsSet(final boolean value) {
        this.__isset_bitfield = EncodingUtils.setBit(this.__isset_bitfield, 0, value);
    }
    
    public boolean isIfExists() {
        return this.ifExists;
    }
    
    public void setIfExists(final boolean ifExists) {
        this.ifExists = ifExists;
        this.setIfExistsIsSet(true);
    }
    
    public void unsetIfExists() {
        this.__isset_bitfield = EncodingUtils.clearBit(this.__isset_bitfield, 1);
    }
    
    public boolean isSetIfExists() {
        return EncodingUtils.testBit(this.__isset_bitfield, 1);
    }
    
    public void setIfExistsIsSet(final boolean value) {
        this.__isset_bitfield = EncodingUtils.setBit(this.__isset_bitfield, 1, value);
    }
    
    public boolean isIgnoreProtection() {
        return this.ignoreProtection;
    }
    
    public void setIgnoreProtection(final boolean ignoreProtection) {
        this.ignoreProtection = ignoreProtection;
        this.setIgnoreProtectionIsSet(true);
    }
    
    public void unsetIgnoreProtection() {
        this.__isset_bitfield = EncodingUtils.clearBit(this.__isset_bitfield, 2);
    }
    
    public boolean isSetIgnoreProtection() {
        return EncodingUtils.testBit(this.__isset_bitfield, 2);
    }
    
    public void setIgnoreProtectionIsSet(final boolean value) {
        this.__isset_bitfield = EncodingUtils.setBit(this.__isset_bitfield, 2, value);
    }
    
    public EnvironmentContext getEnvironmentContext() {
        return this.environmentContext;
    }
    
    public void setEnvironmentContext(final EnvironmentContext environmentContext) {
        this.environmentContext = environmentContext;
    }
    
    public void unsetEnvironmentContext() {
        this.environmentContext = null;
    }
    
    public boolean isSetEnvironmentContext() {
        return this.environmentContext != null;
    }
    
    public void setEnvironmentContextIsSet(final boolean value) {
        if (!value) {
            this.environmentContext = null;
        }
    }
    
    public boolean isNeedResult() {
        return this.needResult;
    }
    
    public void setNeedResult(final boolean needResult) {
        this.needResult = needResult;
        this.setNeedResultIsSet(true);
    }
    
    public void unsetNeedResult() {
        this.__isset_bitfield = EncodingUtils.clearBit(this.__isset_bitfield, 3);
    }
    
    public boolean isSetNeedResult() {
        return EncodingUtils.testBit(this.__isset_bitfield, 3);
    }
    
    public void setNeedResultIsSet(final boolean value) {
        this.__isset_bitfield = EncodingUtils.setBit(this.__isset_bitfield, 3, value);
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
            case TBL_NAME: {
                if (value == null) {
                    this.unsetTblName();
                    break;
                }
                this.setTblName((String)value);
                break;
            }
            case PARTS: {
                if (value == null) {
                    this.unsetParts();
                    break;
                }
                this.setParts((RequestPartsSpec)value);
                break;
            }
            case DELETE_DATA: {
                if (value == null) {
                    this.unsetDeleteData();
                    break;
                }
                this.setDeleteData((boolean)value);
                break;
            }
            case IF_EXISTS: {
                if (value == null) {
                    this.unsetIfExists();
                    break;
                }
                this.setIfExists((boolean)value);
                break;
            }
            case IGNORE_PROTECTION: {
                if (value == null) {
                    this.unsetIgnoreProtection();
                    break;
                }
                this.setIgnoreProtection((boolean)value);
                break;
            }
            case ENVIRONMENT_CONTEXT: {
                if (value == null) {
                    this.unsetEnvironmentContext();
                    break;
                }
                this.setEnvironmentContext((EnvironmentContext)value);
                break;
            }
            case NEED_RESULT: {
                if (value == null) {
                    this.unsetNeedResult();
                    break;
                }
                this.setNeedResult((boolean)value);
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
            case TBL_NAME: {
                return this.getTblName();
            }
            case PARTS: {
                return this.getParts();
            }
            case DELETE_DATA: {
                return this.isDeleteData();
            }
            case IF_EXISTS: {
                return this.isIfExists();
            }
            case IGNORE_PROTECTION: {
                return this.isIgnoreProtection();
            }
            case ENVIRONMENT_CONTEXT: {
                return this.getEnvironmentContext();
            }
            case NEED_RESULT: {
                return this.isNeedResult();
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
            case TBL_NAME: {
                return this.isSetTblName();
            }
            case PARTS: {
                return this.isSetParts();
            }
            case DELETE_DATA: {
                return this.isSetDeleteData();
            }
            case IF_EXISTS: {
                return this.isSetIfExists();
            }
            case IGNORE_PROTECTION: {
                return this.isSetIgnoreProtection();
            }
            case ENVIRONMENT_CONTEXT: {
                return this.isSetEnvironmentContext();
            }
            case NEED_RESULT: {
                return this.isSetNeedResult();
            }
            default: {
                throw new IllegalStateException();
            }
        }
    }
    
    @Override
    public boolean equals(final Object that) {
        return that != null && that instanceof DropPartitionsRequest && this.equals((DropPartitionsRequest)that);
    }
    
    public boolean equals(final DropPartitionsRequest that) {
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
        final boolean this_present_tblName = this.isSetTblName();
        final boolean that_present_tblName = that.isSetTblName();
        if (this_present_tblName || that_present_tblName) {
            if (!this_present_tblName || !that_present_tblName) {
                return false;
            }
            if (!this.tblName.equals(that.tblName)) {
                return false;
            }
        }
        final boolean this_present_parts = this.isSetParts();
        final boolean that_present_parts = that.isSetParts();
        if (this_present_parts || that_present_parts) {
            if (!this_present_parts || !that_present_parts) {
                return false;
            }
            if (!this.parts.equals(that.parts)) {
                return false;
            }
        }
        final boolean this_present_deleteData = this.isSetDeleteData();
        final boolean that_present_deleteData = that.isSetDeleteData();
        if (this_present_deleteData || that_present_deleteData) {
            if (!this_present_deleteData || !that_present_deleteData) {
                return false;
            }
            if (this.deleteData != that.deleteData) {
                return false;
            }
        }
        final boolean this_present_ifExists = this.isSetIfExists();
        final boolean that_present_ifExists = that.isSetIfExists();
        if (this_present_ifExists || that_present_ifExists) {
            if (!this_present_ifExists || !that_present_ifExists) {
                return false;
            }
            if (this.ifExists != that.ifExists) {
                return false;
            }
        }
        final boolean this_present_ignoreProtection = this.isSetIgnoreProtection();
        final boolean that_present_ignoreProtection = that.isSetIgnoreProtection();
        if (this_present_ignoreProtection || that_present_ignoreProtection) {
            if (!this_present_ignoreProtection || !that_present_ignoreProtection) {
                return false;
            }
            if (this.ignoreProtection != that.ignoreProtection) {
                return false;
            }
        }
        final boolean this_present_environmentContext = this.isSetEnvironmentContext();
        final boolean that_present_environmentContext = that.isSetEnvironmentContext();
        if (this_present_environmentContext || that_present_environmentContext) {
            if (!this_present_environmentContext || !that_present_environmentContext) {
                return false;
            }
            if (!this.environmentContext.equals(that.environmentContext)) {
                return false;
            }
        }
        final boolean this_present_needResult = this.isSetNeedResult();
        final boolean that_present_needResult = that.isSetNeedResult();
        if (this_present_needResult || that_present_needResult) {
            if (!this_present_needResult || !that_present_needResult) {
                return false;
            }
            if (this.needResult != that.needResult) {
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
        final boolean present_tblName = this.isSetTblName();
        builder.append(present_tblName);
        if (present_tblName) {
            builder.append(this.tblName);
        }
        final boolean present_parts = this.isSetParts();
        builder.append(present_parts);
        if (present_parts) {
            builder.append(this.parts);
        }
        final boolean present_deleteData = this.isSetDeleteData();
        builder.append(present_deleteData);
        if (present_deleteData) {
            builder.append(this.deleteData);
        }
        final boolean present_ifExists = this.isSetIfExists();
        builder.append(present_ifExists);
        if (present_ifExists) {
            builder.append(this.ifExists);
        }
        final boolean present_ignoreProtection = this.isSetIgnoreProtection();
        builder.append(present_ignoreProtection);
        if (present_ignoreProtection) {
            builder.append(this.ignoreProtection);
        }
        final boolean present_environmentContext = this.isSetEnvironmentContext();
        builder.append(present_environmentContext);
        if (present_environmentContext) {
            builder.append(this.environmentContext);
        }
        final boolean present_needResult = this.isSetNeedResult();
        builder.append(present_needResult);
        if (present_needResult) {
            builder.append(this.needResult);
        }
        return builder.toHashCode();
    }
    
    @Override
    public int compareTo(final DropPartitionsRequest other) {
        if (!this.getClass().equals(other.getClass())) {
            return this.getClass().getName().compareTo(other.getClass().getName());
        }
        int lastComparison = 0;
        final DropPartitionsRequest typedOther = other;
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
        lastComparison = Boolean.valueOf(this.isSetTblName()).compareTo(Boolean.valueOf(typedOther.isSetTblName()));
        if (lastComparison != 0) {
            return lastComparison;
        }
        if (this.isSetTblName()) {
            lastComparison = TBaseHelper.compareTo(this.tblName, typedOther.tblName);
            if (lastComparison != 0) {
                return lastComparison;
            }
        }
        lastComparison = Boolean.valueOf(this.isSetParts()).compareTo(Boolean.valueOf(typedOther.isSetParts()));
        if (lastComparison != 0) {
            return lastComparison;
        }
        if (this.isSetParts()) {
            lastComparison = TBaseHelper.compareTo(this.parts, typedOther.parts);
            if (lastComparison != 0) {
                return lastComparison;
            }
        }
        lastComparison = Boolean.valueOf(this.isSetDeleteData()).compareTo(Boolean.valueOf(typedOther.isSetDeleteData()));
        if (lastComparison != 0) {
            return lastComparison;
        }
        if (this.isSetDeleteData()) {
            lastComparison = TBaseHelper.compareTo(this.deleteData, typedOther.deleteData);
            if (lastComparison != 0) {
                return lastComparison;
            }
        }
        lastComparison = Boolean.valueOf(this.isSetIfExists()).compareTo(Boolean.valueOf(typedOther.isSetIfExists()));
        if (lastComparison != 0) {
            return lastComparison;
        }
        if (this.isSetIfExists()) {
            lastComparison = TBaseHelper.compareTo(this.ifExists, typedOther.ifExists);
            if (lastComparison != 0) {
                return lastComparison;
            }
        }
        lastComparison = Boolean.valueOf(this.isSetIgnoreProtection()).compareTo(Boolean.valueOf(typedOther.isSetIgnoreProtection()));
        if (lastComparison != 0) {
            return lastComparison;
        }
        if (this.isSetIgnoreProtection()) {
            lastComparison = TBaseHelper.compareTo(this.ignoreProtection, typedOther.ignoreProtection);
            if (lastComparison != 0) {
                return lastComparison;
            }
        }
        lastComparison = Boolean.valueOf(this.isSetEnvironmentContext()).compareTo(Boolean.valueOf(typedOther.isSetEnvironmentContext()));
        if (lastComparison != 0) {
            return lastComparison;
        }
        if (this.isSetEnvironmentContext()) {
            lastComparison = TBaseHelper.compareTo(this.environmentContext, typedOther.environmentContext);
            if (lastComparison != 0) {
                return lastComparison;
            }
        }
        lastComparison = Boolean.valueOf(this.isSetNeedResult()).compareTo(Boolean.valueOf(typedOther.isSetNeedResult()));
        if (lastComparison != 0) {
            return lastComparison;
        }
        if (this.isSetNeedResult()) {
            lastComparison = TBaseHelper.compareTo(this.needResult, typedOther.needResult);
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
        DropPartitionsRequest.schemes.get(iprot.getScheme()).getScheme().read(iprot, this);
    }
    
    @Override
    public void write(final TProtocol oprot) throws TException {
        DropPartitionsRequest.schemes.get(oprot.getScheme()).getScheme().write(oprot, this);
    }
    
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("DropPartitionsRequest(");
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
        sb.append("tblName:");
        if (this.tblName == null) {
            sb.append("null");
        }
        else {
            sb.append(this.tblName);
        }
        first = false;
        if (!first) {
            sb.append(", ");
        }
        sb.append("parts:");
        if (this.parts == null) {
            sb.append("null");
        }
        else {
            sb.append(this.parts);
        }
        first = false;
        if (this.isSetDeleteData()) {
            if (!first) {
                sb.append(", ");
            }
            sb.append("deleteData:");
            sb.append(this.deleteData);
            first = false;
        }
        if (this.isSetIfExists()) {
            if (!first) {
                sb.append(", ");
            }
            sb.append("ifExists:");
            sb.append(this.ifExists);
            first = false;
        }
        if (this.isSetIgnoreProtection()) {
            if (!first) {
                sb.append(", ");
            }
            sb.append("ignoreProtection:");
            sb.append(this.ignoreProtection);
            first = false;
        }
        if (this.isSetEnvironmentContext()) {
            if (!first) {
                sb.append(", ");
            }
            sb.append("environmentContext:");
            if (this.environmentContext == null) {
                sb.append("null");
            }
            else {
                sb.append(this.environmentContext);
            }
            first = false;
        }
        if (this.isSetNeedResult()) {
            if (!first) {
                sb.append(", ");
            }
            sb.append("needResult:");
            sb.append(this.needResult);
            first = false;
        }
        sb.append(")");
        return sb.toString();
    }
    
    public void validate() throws TException {
        if (!this.isSetDbName()) {
            throw new TProtocolException("Required field 'dbName' is unset! Struct:" + this.toString());
        }
        if (!this.isSetTblName()) {
            throw new TProtocolException("Required field 'tblName' is unset! Struct:" + this.toString());
        }
        if (!this.isSetParts()) {
            throw new TProtocolException("Required field 'parts' is unset! Struct:" + this.toString());
        }
        if (this.environmentContext != null) {
            this.environmentContext.validate();
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
        STRUCT_DESC = new TStruct("DropPartitionsRequest");
        DB_NAME_FIELD_DESC = new TField("dbName", (byte)11, (short)1);
        TBL_NAME_FIELD_DESC = new TField("tblName", (byte)11, (short)2);
        PARTS_FIELD_DESC = new TField("parts", (byte)12, (short)3);
        DELETE_DATA_FIELD_DESC = new TField("deleteData", (byte)2, (short)4);
        IF_EXISTS_FIELD_DESC = new TField("ifExists", (byte)2, (short)5);
        IGNORE_PROTECTION_FIELD_DESC = new TField("ignoreProtection", (byte)2, (short)6);
        ENVIRONMENT_CONTEXT_FIELD_DESC = new TField("environmentContext", (byte)12, (short)7);
        NEED_RESULT_FIELD_DESC = new TField("needResult", (byte)2, (short)8);
        (schemes = new HashMap<Class<? extends IScheme>, SchemeFactory>()).put(StandardScheme.class, new DropPartitionsRequestStandardSchemeFactory());
        DropPartitionsRequest.schemes.put(TupleScheme.class, new DropPartitionsRequestTupleSchemeFactory());
        final Map<_Fields, FieldMetaData> tmpMap = new EnumMap<_Fields, FieldMetaData>(_Fields.class);
        tmpMap.put(_Fields.DB_NAME, new FieldMetaData("dbName", (byte)1, new FieldValueMetaData((byte)11)));
        tmpMap.put(_Fields.TBL_NAME, new FieldMetaData("tblName", (byte)1, new FieldValueMetaData((byte)11)));
        tmpMap.put(_Fields.PARTS, new FieldMetaData("parts", (byte)1, new StructMetaData((byte)12, RequestPartsSpec.class)));
        tmpMap.put(_Fields.DELETE_DATA, new FieldMetaData("deleteData", (byte)2, new FieldValueMetaData((byte)2)));
        tmpMap.put(_Fields.IF_EXISTS, new FieldMetaData("ifExists", (byte)2, new FieldValueMetaData((byte)2)));
        tmpMap.put(_Fields.IGNORE_PROTECTION, new FieldMetaData("ignoreProtection", (byte)2, new FieldValueMetaData((byte)2)));
        tmpMap.put(_Fields.ENVIRONMENT_CONTEXT, new FieldMetaData("environmentContext", (byte)2, new StructMetaData((byte)12, EnvironmentContext.class)));
        tmpMap.put(_Fields.NEED_RESULT, new FieldMetaData("needResult", (byte)2, new FieldValueMetaData((byte)2)));
        FieldMetaData.addStructMetaDataMap(DropPartitionsRequest.class, metaDataMap = Collections.unmodifiableMap((Map<? extends _Fields, ? extends FieldMetaData>)tmpMap));
    }
    
    public enum _Fields implements TFieldIdEnum
    {
        DB_NAME((short)1, "dbName"), 
        TBL_NAME((short)2, "tblName"), 
        PARTS((short)3, "parts"), 
        DELETE_DATA((short)4, "deleteData"), 
        IF_EXISTS((short)5, "ifExists"), 
        IGNORE_PROTECTION((short)6, "ignoreProtection"), 
        ENVIRONMENT_CONTEXT((short)7, "environmentContext"), 
        NEED_RESULT((short)8, "needResult");
        
        private static final Map<String, _Fields> byName;
        private final short _thriftId;
        private final String _fieldName;
        
        public static _Fields findByThriftId(final int fieldId) {
            switch (fieldId) {
                case 1: {
                    return _Fields.DB_NAME;
                }
                case 2: {
                    return _Fields.TBL_NAME;
                }
                case 3: {
                    return _Fields.PARTS;
                }
                case 4: {
                    return _Fields.DELETE_DATA;
                }
                case 5: {
                    return _Fields.IF_EXISTS;
                }
                case 6: {
                    return _Fields.IGNORE_PROTECTION;
                }
                case 7: {
                    return _Fields.ENVIRONMENT_CONTEXT;
                }
                case 8: {
                    return _Fields.NEED_RESULT;
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
    
    private static class DropPartitionsRequestStandardSchemeFactory implements SchemeFactory
    {
        @Override
        public DropPartitionsRequestStandardScheme getScheme() {
            return new DropPartitionsRequestStandardScheme();
        }
    }
    
    private static class DropPartitionsRequestStandardScheme extends StandardScheme<DropPartitionsRequest>
    {
        @Override
        public void read(final TProtocol iprot, final DropPartitionsRequest struct) throws TException {
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
                            struct.tblName = iprot.readString();
                            struct.setTblNameIsSet(true);
                            break;
                        }
                        TProtocolUtil.skip(iprot, schemeField.type);
                        break;
                    }
                    case 3: {
                        if (schemeField.type == 12) {
                            struct.parts = new RequestPartsSpec();
                            struct.parts.read(iprot);
                            struct.setPartsIsSet(true);
                            break;
                        }
                        TProtocolUtil.skip(iprot, schemeField.type);
                        break;
                    }
                    case 4: {
                        if (schemeField.type == 2) {
                            struct.deleteData = iprot.readBool();
                            struct.setDeleteDataIsSet(true);
                            break;
                        }
                        TProtocolUtil.skip(iprot, schemeField.type);
                        break;
                    }
                    case 5: {
                        if (schemeField.type == 2) {
                            struct.ifExists = iprot.readBool();
                            struct.setIfExistsIsSet(true);
                            break;
                        }
                        TProtocolUtil.skip(iprot, schemeField.type);
                        break;
                    }
                    case 6: {
                        if (schemeField.type == 2) {
                            struct.ignoreProtection = iprot.readBool();
                            struct.setIgnoreProtectionIsSet(true);
                            break;
                        }
                        TProtocolUtil.skip(iprot, schemeField.type);
                        break;
                    }
                    case 7: {
                        if (schemeField.type == 12) {
                            struct.environmentContext = new EnvironmentContext();
                            struct.environmentContext.read(iprot);
                            struct.setEnvironmentContextIsSet(true);
                            break;
                        }
                        TProtocolUtil.skip(iprot, schemeField.type);
                        break;
                    }
                    case 8: {
                        if (schemeField.type == 2) {
                            struct.needResult = iprot.readBool();
                            struct.setNeedResultIsSet(true);
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
        public void write(final TProtocol oprot, final DropPartitionsRequest struct) throws TException {
            struct.validate();
            oprot.writeStructBegin(DropPartitionsRequest.STRUCT_DESC);
            if (struct.dbName != null) {
                oprot.writeFieldBegin(DropPartitionsRequest.DB_NAME_FIELD_DESC);
                oprot.writeString(struct.dbName);
                oprot.writeFieldEnd();
            }
            if (struct.tblName != null) {
                oprot.writeFieldBegin(DropPartitionsRequest.TBL_NAME_FIELD_DESC);
                oprot.writeString(struct.tblName);
                oprot.writeFieldEnd();
            }
            if (struct.parts != null) {
                oprot.writeFieldBegin(DropPartitionsRequest.PARTS_FIELD_DESC);
                struct.parts.write(oprot);
                oprot.writeFieldEnd();
            }
            if (struct.isSetDeleteData()) {
                oprot.writeFieldBegin(DropPartitionsRequest.DELETE_DATA_FIELD_DESC);
                oprot.writeBool(struct.deleteData);
                oprot.writeFieldEnd();
            }
            if (struct.isSetIfExists()) {
                oprot.writeFieldBegin(DropPartitionsRequest.IF_EXISTS_FIELD_DESC);
                oprot.writeBool(struct.ifExists);
                oprot.writeFieldEnd();
            }
            if (struct.isSetIgnoreProtection()) {
                oprot.writeFieldBegin(DropPartitionsRequest.IGNORE_PROTECTION_FIELD_DESC);
                oprot.writeBool(struct.ignoreProtection);
                oprot.writeFieldEnd();
            }
            if (struct.environmentContext != null && struct.isSetEnvironmentContext()) {
                oprot.writeFieldBegin(DropPartitionsRequest.ENVIRONMENT_CONTEXT_FIELD_DESC);
                struct.environmentContext.write(oprot);
                oprot.writeFieldEnd();
            }
            if (struct.isSetNeedResult()) {
                oprot.writeFieldBegin(DropPartitionsRequest.NEED_RESULT_FIELD_DESC);
                oprot.writeBool(struct.needResult);
                oprot.writeFieldEnd();
            }
            oprot.writeFieldStop();
            oprot.writeStructEnd();
        }
    }
    
    private static class DropPartitionsRequestTupleSchemeFactory implements SchemeFactory
    {
        @Override
        public DropPartitionsRequestTupleScheme getScheme() {
            return new DropPartitionsRequestTupleScheme();
        }
    }
    
    private static class DropPartitionsRequestTupleScheme extends TupleScheme<DropPartitionsRequest>
    {
        @Override
        public void write(final TProtocol prot, final DropPartitionsRequest struct) throws TException {
            final TTupleProtocol oprot = (TTupleProtocol)prot;
            oprot.writeString(struct.dbName);
            oprot.writeString(struct.tblName);
            struct.parts.write(oprot);
            final BitSet optionals = new BitSet();
            if (struct.isSetDeleteData()) {
                optionals.set(0);
            }
            if (struct.isSetIfExists()) {
                optionals.set(1);
            }
            if (struct.isSetIgnoreProtection()) {
                optionals.set(2);
            }
            if (struct.isSetEnvironmentContext()) {
                optionals.set(3);
            }
            if (struct.isSetNeedResult()) {
                optionals.set(4);
            }
            oprot.writeBitSet(optionals, 5);
            if (struct.isSetDeleteData()) {
                oprot.writeBool(struct.deleteData);
            }
            if (struct.isSetIfExists()) {
                oprot.writeBool(struct.ifExists);
            }
            if (struct.isSetIgnoreProtection()) {
                oprot.writeBool(struct.ignoreProtection);
            }
            if (struct.isSetEnvironmentContext()) {
                struct.environmentContext.write(oprot);
            }
            if (struct.isSetNeedResult()) {
                oprot.writeBool(struct.needResult);
            }
        }
        
        @Override
        public void read(final TProtocol prot, final DropPartitionsRequest struct) throws TException {
            final TTupleProtocol iprot = (TTupleProtocol)prot;
            struct.dbName = iprot.readString();
            struct.setDbNameIsSet(true);
            struct.tblName = iprot.readString();
            struct.setTblNameIsSet(true);
            struct.parts = new RequestPartsSpec();
            struct.parts.read(iprot);
            struct.setPartsIsSet(true);
            final BitSet incoming = iprot.readBitSet(5);
            if (incoming.get(0)) {
                struct.deleteData = iprot.readBool();
                struct.setDeleteDataIsSet(true);
            }
            if (incoming.get(1)) {
                struct.ifExists = iprot.readBool();
                struct.setIfExistsIsSet(true);
            }
            if (incoming.get(2)) {
                struct.ignoreProtection = iprot.readBool();
                struct.setIgnoreProtectionIsSet(true);
            }
            if (incoming.get(3)) {
                struct.environmentContext = new EnvironmentContext();
                struct.environmentContext.read(iprot);
                struct.setEnvironmentContextIsSet(true);
            }
            if (incoming.get(4)) {
                struct.needResult = iprot.readBool();
                struct.setNeedResultIsSet(true);
            }
        }
    }
}
