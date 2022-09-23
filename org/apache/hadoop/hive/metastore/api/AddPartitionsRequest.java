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
import org.apache.thrift.protocol.TProtocolException;
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

public class AddPartitionsRequest implements TBase<AddPartitionsRequest, _Fields>, Serializable, Cloneable
{
    private static final TStruct STRUCT_DESC;
    private static final TField DB_NAME_FIELD_DESC;
    private static final TField TBL_NAME_FIELD_DESC;
    private static final TField PARTS_FIELD_DESC;
    private static final TField IF_NOT_EXISTS_FIELD_DESC;
    private static final TField NEED_RESULT_FIELD_DESC;
    private static final Map<Class<? extends IScheme>, SchemeFactory> schemes;
    private String dbName;
    private String tblName;
    private List<Partition> parts;
    private boolean ifNotExists;
    private boolean needResult;
    private static final int __IFNOTEXISTS_ISSET_ID = 0;
    private static final int __NEEDRESULT_ISSET_ID = 1;
    private byte __isset_bitfield;
    private _Fields[] optionals;
    public static final Map<_Fields, FieldMetaData> metaDataMap;
    
    public AddPartitionsRequest() {
        this.__isset_bitfield = 0;
        this.optionals = new _Fields[] { _Fields.NEED_RESULT };
        this.needResult = true;
    }
    
    public AddPartitionsRequest(final String dbName, final String tblName, final List<Partition> parts, final boolean ifNotExists) {
        this();
        this.dbName = dbName;
        this.tblName = tblName;
        this.parts = parts;
        this.ifNotExists = ifNotExists;
        this.setIfNotExistsIsSet(true);
    }
    
    public AddPartitionsRequest(final AddPartitionsRequest other) {
        this.__isset_bitfield = 0;
        this.optionals = new _Fields[] { _Fields.NEED_RESULT };
        this.__isset_bitfield = other.__isset_bitfield;
        if (other.isSetDbName()) {
            this.dbName = other.dbName;
        }
        if (other.isSetTblName()) {
            this.tblName = other.tblName;
        }
        if (other.isSetParts()) {
            final List<Partition> __this__parts = new ArrayList<Partition>();
            for (final Partition other_element : other.parts) {
                __this__parts.add(new Partition(other_element));
            }
            this.parts = __this__parts;
        }
        this.ifNotExists = other.ifNotExists;
        this.needResult = other.needResult;
    }
    
    @Override
    public AddPartitionsRequest deepCopy() {
        return new AddPartitionsRequest(this);
    }
    
    @Override
    public void clear() {
        this.dbName = null;
        this.tblName = null;
        this.parts = null;
        this.setIfNotExistsIsSet(false);
        this.ifNotExists = false;
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
    
    public int getPartsSize() {
        return (this.parts == null) ? 0 : this.parts.size();
    }
    
    public Iterator<Partition> getPartsIterator() {
        return (this.parts == null) ? null : this.parts.iterator();
    }
    
    public void addToParts(final Partition elem) {
        if (this.parts == null) {
            this.parts = new ArrayList<Partition>();
        }
        this.parts.add(elem);
    }
    
    public List<Partition> getParts() {
        return this.parts;
    }
    
    public void setParts(final List<Partition> parts) {
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
    
    public boolean isIfNotExists() {
        return this.ifNotExists;
    }
    
    public void setIfNotExists(final boolean ifNotExists) {
        this.ifNotExists = ifNotExists;
        this.setIfNotExistsIsSet(true);
    }
    
    public void unsetIfNotExists() {
        this.__isset_bitfield = EncodingUtils.clearBit(this.__isset_bitfield, 0);
    }
    
    public boolean isSetIfNotExists() {
        return EncodingUtils.testBit(this.__isset_bitfield, 0);
    }
    
    public void setIfNotExistsIsSet(final boolean value) {
        this.__isset_bitfield = EncodingUtils.setBit(this.__isset_bitfield, 0, value);
    }
    
    public boolean isNeedResult() {
        return this.needResult;
    }
    
    public void setNeedResult(final boolean needResult) {
        this.needResult = needResult;
        this.setNeedResultIsSet(true);
    }
    
    public void unsetNeedResult() {
        this.__isset_bitfield = EncodingUtils.clearBit(this.__isset_bitfield, 1);
    }
    
    public boolean isSetNeedResult() {
        return EncodingUtils.testBit(this.__isset_bitfield, 1);
    }
    
    public void setNeedResultIsSet(final boolean value) {
        this.__isset_bitfield = EncodingUtils.setBit(this.__isset_bitfield, 1, value);
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
                this.setParts((List<Partition>)value);
                break;
            }
            case IF_NOT_EXISTS: {
                if (value == null) {
                    this.unsetIfNotExists();
                    break;
                }
                this.setIfNotExists((boolean)value);
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
            case IF_NOT_EXISTS: {
                return this.isIfNotExists();
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
            case IF_NOT_EXISTS: {
                return this.isSetIfNotExists();
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
        return that != null && that instanceof AddPartitionsRequest && this.equals((AddPartitionsRequest)that);
    }
    
    public boolean equals(final AddPartitionsRequest that) {
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
        final boolean this_present_ifNotExists = true;
        final boolean that_present_ifNotExists = true;
        if (this_present_ifNotExists || that_present_ifNotExists) {
            if (!this_present_ifNotExists || !that_present_ifNotExists) {
                return false;
            }
            if (this.ifNotExists != that.ifNotExists) {
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
        final boolean present_ifNotExists = true;
        builder.append(present_ifNotExists);
        if (present_ifNotExists) {
            builder.append(this.ifNotExists);
        }
        final boolean present_needResult = this.isSetNeedResult();
        builder.append(present_needResult);
        if (present_needResult) {
            builder.append(this.needResult);
        }
        return builder.toHashCode();
    }
    
    @Override
    public int compareTo(final AddPartitionsRequest other) {
        if (!this.getClass().equals(other.getClass())) {
            return this.getClass().getName().compareTo(other.getClass().getName());
        }
        int lastComparison = 0;
        final AddPartitionsRequest typedOther = other;
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
        lastComparison = Boolean.valueOf(this.isSetIfNotExists()).compareTo(Boolean.valueOf(typedOther.isSetIfNotExists()));
        if (lastComparison != 0) {
            return lastComparison;
        }
        if (this.isSetIfNotExists()) {
            lastComparison = TBaseHelper.compareTo(this.ifNotExists, typedOther.ifNotExists);
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
        AddPartitionsRequest.schemes.get(iprot.getScheme()).getScheme().read(iprot, this);
    }
    
    @Override
    public void write(final TProtocol oprot) throws TException {
        AddPartitionsRequest.schemes.get(oprot.getScheme()).getScheme().write(oprot, this);
    }
    
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("AddPartitionsRequest(");
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
        if (!first) {
            sb.append(", ");
        }
        sb.append("ifNotExists:");
        sb.append(this.ifNotExists);
        first = false;
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
        if (!this.isSetIfNotExists()) {
            throw new TProtocolException("Required field 'ifNotExists' is unset! Struct:" + this.toString());
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
        STRUCT_DESC = new TStruct("AddPartitionsRequest");
        DB_NAME_FIELD_DESC = new TField("dbName", (byte)11, (short)1);
        TBL_NAME_FIELD_DESC = new TField("tblName", (byte)11, (short)2);
        PARTS_FIELD_DESC = new TField("parts", (byte)15, (short)3);
        IF_NOT_EXISTS_FIELD_DESC = new TField("ifNotExists", (byte)2, (short)4);
        NEED_RESULT_FIELD_DESC = new TField("needResult", (byte)2, (short)5);
        (schemes = new HashMap<Class<? extends IScheme>, SchemeFactory>()).put(StandardScheme.class, new AddPartitionsRequestStandardSchemeFactory());
        AddPartitionsRequest.schemes.put(TupleScheme.class, new AddPartitionsRequestTupleSchemeFactory());
        final Map<_Fields, FieldMetaData> tmpMap = new EnumMap<_Fields, FieldMetaData>(_Fields.class);
        tmpMap.put(_Fields.DB_NAME, new FieldMetaData("dbName", (byte)1, new FieldValueMetaData((byte)11)));
        tmpMap.put(_Fields.TBL_NAME, new FieldMetaData("tblName", (byte)1, new FieldValueMetaData((byte)11)));
        tmpMap.put(_Fields.PARTS, new FieldMetaData("parts", (byte)1, new ListMetaData((byte)15, new StructMetaData((byte)12, Partition.class))));
        tmpMap.put(_Fields.IF_NOT_EXISTS, new FieldMetaData("ifNotExists", (byte)1, new FieldValueMetaData((byte)2)));
        tmpMap.put(_Fields.NEED_RESULT, new FieldMetaData("needResult", (byte)2, new FieldValueMetaData((byte)2)));
        FieldMetaData.addStructMetaDataMap(AddPartitionsRequest.class, metaDataMap = Collections.unmodifiableMap((Map<? extends _Fields, ? extends FieldMetaData>)tmpMap));
    }
    
    public enum _Fields implements TFieldIdEnum
    {
        DB_NAME((short)1, "dbName"), 
        TBL_NAME((short)2, "tblName"), 
        PARTS((short)3, "parts"), 
        IF_NOT_EXISTS((short)4, "ifNotExists"), 
        NEED_RESULT((short)5, "needResult");
        
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
                    return _Fields.IF_NOT_EXISTS;
                }
                case 5: {
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
    
    private static class AddPartitionsRequestStandardSchemeFactory implements SchemeFactory
    {
        @Override
        public AddPartitionsRequestStandardScheme getScheme() {
            return new AddPartitionsRequestStandardScheme();
        }
    }
    
    private static class AddPartitionsRequestStandardScheme extends StandardScheme<AddPartitionsRequest>
    {
        @Override
        public void read(final TProtocol iprot, final AddPartitionsRequest struct) throws TException {
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
                        if (schemeField.type == 15) {
                            final TList _list388 = iprot.readListBegin();
                            struct.parts = (List<Partition>)new ArrayList(_list388.size);
                            for (int _i389 = 0; _i389 < _list388.size; ++_i389) {
                                final Partition _elem390 = new Partition();
                                _elem390.read(iprot);
                                struct.parts.add(_elem390);
                            }
                            iprot.readListEnd();
                            struct.setPartsIsSet(true);
                            break;
                        }
                        TProtocolUtil.skip(iprot, schemeField.type);
                        break;
                    }
                    case 4: {
                        if (schemeField.type == 2) {
                            struct.ifNotExists = iprot.readBool();
                            struct.setIfNotExistsIsSet(true);
                            break;
                        }
                        TProtocolUtil.skip(iprot, schemeField.type);
                        break;
                    }
                    case 5: {
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
        public void write(final TProtocol oprot, final AddPartitionsRequest struct) throws TException {
            struct.validate();
            oprot.writeStructBegin(AddPartitionsRequest.STRUCT_DESC);
            if (struct.dbName != null) {
                oprot.writeFieldBegin(AddPartitionsRequest.DB_NAME_FIELD_DESC);
                oprot.writeString(struct.dbName);
                oprot.writeFieldEnd();
            }
            if (struct.tblName != null) {
                oprot.writeFieldBegin(AddPartitionsRequest.TBL_NAME_FIELD_DESC);
                oprot.writeString(struct.tblName);
                oprot.writeFieldEnd();
            }
            if (struct.parts != null) {
                oprot.writeFieldBegin(AddPartitionsRequest.PARTS_FIELD_DESC);
                oprot.writeListBegin(new TList((byte)12, struct.parts.size()));
                for (final Partition _iter391 : struct.parts) {
                    _iter391.write(oprot);
                }
                oprot.writeListEnd();
                oprot.writeFieldEnd();
            }
            oprot.writeFieldBegin(AddPartitionsRequest.IF_NOT_EXISTS_FIELD_DESC);
            oprot.writeBool(struct.ifNotExists);
            oprot.writeFieldEnd();
            if (struct.isSetNeedResult()) {
                oprot.writeFieldBegin(AddPartitionsRequest.NEED_RESULT_FIELD_DESC);
                oprot.writeBool(struct.needResult);
                oprot.writeFieldEnd();
            }
            oprot.writeFieldStop();
            oprot.writeStructEnd();
        }
    }
    
    private static class AddPartitionsRequestTupleSchemeFactory implements SchemeFactory
    {
        @Override
        public AddPartitionsRequestTupleScheme getScheme() {
            return new AddPartitionsRequestTupleScheme();
        }
    }
    
    private static class AddPartitionsRequestTupleScheme extends TupleScheme<AddPartitionsRequest>
    {
        @Override
        public void write(final TProtocol prot, final AddPartitionsRequest struct) throws TException {
            final TTupleProtocol oprot = (TTupleProtocol)prot;
            oprot.writeString(struct.dbName);
            oprot.writeString(struct.tblName);
            oprot.writeI32(struct.parts.size());
            for (final Partition _iter392 : struct.parts) {
                _iter392.write(oprot);
            }
            oprot.writeBool(struct.ifNotExists);
            final BitSet optionals = new BitSet();
            if (struct.isSetNeedResult()) {
                optionals.set(0);
            }
            oprot.writeBitSet(optionals, 1);
            if (struct.isSetNeedResult()) {
                oprot.writeBool(struct.needResult);
            }
        }
        
        @Override
        public void read(final TProtocol prot, final AddPartitionsRequest struct) throws TException {
            final TTupleProtocol iprot = (TTupleProtocol)prot;
            struct.dbName = iprot.readString();
            struct.setDbNameIsSet(true);
            struct.tblName = iprot.readString();
            struct.setTblNameIsSet(true);
            final TList _list393 = new TList((byte)12, iprot.readI32());
            struct.parts = (List<Partition>)new ArrayList(_list393.size);
            for (int _i394 = 0; _i394 < _list393.size; ++_i394) {
                final Partition _elem395 = new Partition();
                _elem395.read(iprot);
                struct.parts.add(_elem395);
            }
            struct.setPartsIsSet(true);
            struct.ifNotExists = iprot.readBool();
            struct.setIfNotExistsIsSet(true);
            final BitSet incoming = iprot.readBitSet(1);
            if (incoming.get(0)) {
                struct.needResult = iprot.readBool();
                struct.setNeedResultIsSet(true);
            }
        }
    }
}
