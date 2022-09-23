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

public class FireEventRequest implements TBase<FireEventRequest, _Fields>, Serializable, Cloneable
{
    private static final TStruct STRUCT_DESC;
    private static final TField SUCCESSFUL_FIELD_DESC;
    private static final TField DATA_FIELD_DESC;
    private static final TField DB_NAME_FIELD_DESC;
    private static final TField TABLE_NAME_FIELD_DESC;
    private static final TField PARTITION_VALS_FIELD_DESC;
    private static final Map<Class<? extends IScheme>, SchemeFactory> schemes;
    private boolean successful;
    private FireEventRequestData data;
    private String dbName;
    private String tableName;
    private List<String> partitionVals;
    private static final int __SUCCESSFUL_ISSET_ID = 0;
    private byte __isset_bitfield;
    private _Fields[] optionals;
    public static final Map<_Fields, FieldMetaData> metaDataMap;
    
    public FireEventRequest() {
        this.__isset_bitfield = 0;
        this.optionals = new _Fields[] { _Fields.DB_NAME, _Fields.TABLE_NAME, _Fields.PARTITION_VALS };
    }
    
    public FireEventRequest(final boolean successful, final FireEventRequestData data) {
        this();
        this.successful = successful;
        this.setSuccessfulIsSet(true);
        this.data = data;
    }
    
    public FireEventRequest(final FireEventRequest other) {
        this.__isset_bitfield = 0;
        this.optionals = new _Fields[] { _Fields.DB_NAME, _Fields.TABLE_NAME, _Fields.PARTITION_VALS };
        this.__isset_bitfield = other.__isset_bitfield;
        this.successful = other.successful;
        if (other.isSetData()) {
            this.data = new FireEventRequestData(other.data);
        }
        if (other.isSetDbName()) {
            this.dbName = other.dbName;
        }
        if (other.isSetTableName()) {
            this.tableName = other.tableName;
        }
        if (other.isSetPartitionVals()) {
            final List<String> __this__partitionVals = new ArrayList<String>();
            for (final String other_element : other.partitionVals) {
                __this__partitionVals.add(other_element);
            }
            this.partitionVals = __this__partitionVals;
        }
    }
    
    @Override
    public FireEventRequest deepCopy() {
        return new FireEventRequest(this);
    }
    
    @Override
    public void clear() {
        this.setSuccessfulIsSet(false);
        this.successful = false;
        this.data = null;
        this.dbName = null;
        this.tableName = null;
        this.partitionVals = null;
    }
    
    public boolean isSuccessful() {
        return this.successful;
    }
    
    public void setSuccessful(final boolean successful) {
        this.successful = successful;
        this.setSuccessfulIsSet(true);
    }
    
    public void unsetSuccessful() {
        this.__isset_bitfield = EncodingUtils.clearBit(this.__isset_bitfield, 0);
    }
    
    public boolean isSetSuccessful() {
        return EncodingUtils.testBit(this.__isset_bitfield, 0);
    }
    
    public void setSuccessfulIsSet(final boolean value) {
        this.__isset_bitfield = EncodingUtils.setBit(this.__isset_bitfield, 0, value);
    }
    
    public FireEventRequestData getData() {
        return this.data;
    }
    
    public void setData(final FireEventRequestData data) {
        this.data = data;
    }
    
    public void unsetData() {
        this.data = null;
    }
    
    public boolean isSetData() {
        return this.data != null;
    }
    
    public void setDataIsSet(final boolean value) {
        if (!value) {
            this.data = null;
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
    
    public int getPartitionValsSize() {
        return (this.partitionVals == null) ? 0 : this.partitionVals.size();
    }
    
    public Iterator<String> getPartitionValsIterator() {
        return (this.partitionVals == null) ? null : this.partitionVals.iterator();
    }
    
    public void addToPartitionVals(final String elem) {
        if (this.partitionVals == null) {
            this.partitionVals = new ArrayList<String>();
        }
        this.partitionVals.add(elem);
    }
    
    public List<String> getPartitionVals() {
        return this.partitionVals;
    }
    
    public void setPartitionVals(final List<String> partitionVals) {
        this.partitionVals = partitionVals;
    }
    
    public void unsetPartitionVals() {
        this.partitionVals = null;
    }
    
    public boolean isSetPartitionVals() {
        return this.partitionVals != null;
    }
    
    public void setPartitionValsIsSet(final boolean value) {
        if (!value) {
            this.partitionVals = null;
        }
    }
    
    @Override
    public void setFieldValue(final _Fields field, final Object value) {
        switch (field) {
            case SUCCESSFUL: {
                if (value == null) {
                    this.unsetSuccessful();
                    break;
                }
                this.setSuccessful((boolean)value);
                break;
            }
            case DATA: {
                if (value == null) {
                    this.unsetData();
                    break;
                }
                this.setData((FireEventRequestData)value);
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
            case PARTITION_VALS: {
                if (value == null) {
                    this.unsetPartitionVals();
                    break;
                }
                this.setPartitionVals((List<String>)value);
                break;
            }
        }
    }
    
    @Override
    public Object getFieldValue(final _Fields field) {
        switch (field) {
            case SUCCESSFUL: {
                return this.isSuccessful();
            }
            case DATA: {
                return this.getData();
            }
            case DB_NAME: {
                return this.getDbName();
            }
            case TABLE_NAME: {
                return this.getTableName();
            }
            case PARTITION_VALS: {
                return this.getPartitionVals();
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
            case SUCCESSFUL: {
                return this.isSetSuccessful();
            }
            case DATA: {
                return this.isSetData();
            }
            case DB_NAME: {
                return this.isSetDbName();
            }
            case TABLE_NAME: {
                return this.isSetTableName();
            }
            case PARTITION_VALS: {
                return this.isSetPartitionVals();
            }
            default: {
                throw new IllegalStateException();
            }
        }
    }
    
    @Override
    public boolean equals(final Object that) {
        return that != null && that instanceof FireEventRequest && this.equals((FireEventRequest)that);
    }
    
    public boolean equals(final FireEventRequest that) {
        if (that == null) {
            return false;
        }
        final boolean this_present_successful = true;
        final boolean that_present_successful = true;
        if (this_present_successful || that_present_successful) {
            if (!this_present_successful || !that_present_successful) {
                return false;
            }
            if (this.successful != that.successful) {
                return false;
            }
        }
        final boolean this_present_data = this.isSetData();
        final boolean that_present_data = that.isSetData();
        if (this_present_data || that_present_data) {
            if (!this_present_data || !that_present_data) {
                return false;
            }
            if (!this.data.equals(that.data)) {
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
        final boolean this_present_partitionVals = this.isSetPartitionVals();
        final boolean that_present_partitionVals = that.isSetPartitionVals();
        if (this_present_partitionVals || that_present_partitionVals) {
            if (!this_present_partitionVals || !that_present_partitionVals) {
                return false;
            }
            if (!this.partitionVals.equals(that.partitionVals)) {
                return false;
            }
        }
        return true;
    }
    
    @Override
    public int hashCode() {
        final HashCodeBuilder builder = new HashCodeBuilder();
        final boolean present_successful = true;
        builder.append(present_successful);
        if (present_successful) {
            builder.append(this.successful);
        }
        final boolean present_data = this.isSetData();
        builder.append(present_data);
        if (present_data) {
            builder.append(this.data);
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
        final boolean present_partitionVals = this.isSetPartitionVals();
        builder.append(present_partitionVals);
        if (present_partitionVals) {
            builder.append(this.partitionVals);
        }
        return builder.toHashCode();
    }
    
    @Override
    public int compareTo(final FireEventRequest other) {
        if (!this.getClass().equals(other.getClass())) {
            return this.getClass().getName().compareTo(other.getClass().getName());
        }
        int lastComparison = 0;
        final FireEventRequest typedOther = other;
        lastComparison = Boolean.valueOf(this.isSetSuccessful()).compareTo(Boolean.valueOf(typedOther.isSetSuccessful()));
        if (lastComparison != 0) {
            return lastComparison;
        }
        if (this.isSetSuccessful()) {
            lastComparison = TBaseHelper.compareTo(this.successful, typedOther.successful);
            if (lastComparison != 0) {
                return lastComparison;
            }
        }
        lastComparison = Boolean.valueOf(this.isSetData()).compareTo(Boolean.valueOf(typedOther.isSetData()));
        if (lastComparison != 0) {
            return lastComparison;
        }
        if (this.isSetData()) {
            lastComparison = TBaseHelper.compareTo(this.data, typedOther.data);
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
        lastComparison = Boolean.valueOf(this.isSetPartitionVals()).compareTo(Boolean.valueOf(typedOther.isSetPartitionVals()));
        if (lastComparison != 0) {
            return lastComparison;
        }
        if (this.isSetPartitionVals()) {
            lastComparison = TBaseHelper.compareTo(this.partitionVals, typedOther.partitionVals);
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
        FireEventRequest.schemes.get(iprot.getScheme()).getScheme().read(iprot, this);
    }
    
    @Override
    public void write(final TProtocol oprot) throws TException {
        FireEventRequest.schemes.get(oprot.getScheme()).getScheme().write(oprot, this);
    }
    
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("FireEventRequest(");
        boolean first = true;
        sb.append("successful:");
        sb.append(this.successful);
        first = false;
        if (!first) {
            sb.append(", ");
        }
        sb.append("data:");
        if (this.data == null) {
            sb.append("null");
        }
        else {
            sb.append(this.data);
        }
        first = false;
        if (this.isSetDbName()) {
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
        }
        if (this.isSetTableName()) {
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
        }
        if (this.isSetPartitionVals()) {
            if (!first) {
                sb.append(", ");
            }
            sb.append("partitionVals:");
            if (this.partitionVals == null) {
                sb.append("null");
            }
            else {
                sb.append(this.partitionVals);
            }
            first = false;
        }
        sb.append(")");
        return sb.toString();
    }
    
    public void validate() throws TException {
        if (!this.isSetSuccessful()) {
            throw new TProtocolException("Required field 'successful' is unset! Struct:" + this.toString());
        }
        if (!this.isSetData()) {
            throw new TProtocolException("Required field 'data' is unset! Struct:" + this.toString());
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
        STRUCT_DESC = new TStruct("FireEventRequest");
        SUCCESSFUL_FIELD_DESC = new TField("successful", (byte)2, (short)1);
        DATA_FIELD_DESC = new TField("data", (byte)12, (short)2);
        DB_NAME_FIELD_DESC = new TField("dbName", (byte)11, (short)3);
        TABLE_NAME_FIELD_DESC = new TField("tableName", (byte)11, (short)4);
        PARTITION_VALS_FIELD_DESC = new TField("partitionVals", (byte)15, (short)5);
        (schemes = new HashMap<Class<? extends IScheme>, SchemeFactory>()).put(StandardScheme.class, new FireEventRequestStandardSchemeFactory());
        FireEventRequest.schemes.put(TupleScheme.class, new FireEventRequestTupleSchemeFactory());
        final Map<_Fields, FieldMetaData> tmpMap = new EnumMap<_Fields, FieldMetaData>(_Fields.class);
        tmpMap.put(_Fields.SUCCESSFUL, new FieldMetaData("successful", (byte)1, new FieldValueMetaData((byte)2)));
        tmpMap.put(_Fields.DATA, new FieldMetaData("data", (byte)1, new StructMetaData((byte)12, FireEventRequestData.class)));
        tmpMap.put(_Fields.DB_NAME, new FieldMetaData("dbName", (byte)2, new FieldValueMetaData((byte)11)));
        tmpMap.put(_Fields.TABLE_NAME, new FieldMetaData("tableName", (byte)2, new FieldValueMetaData((byte)11)));
        tmpMap.put(_Fields.PARTITION_VALS, new FieldMetaData("partitionVals", (byte)2, new ListMetaData((byte)15, new FieldValueMetaData((byte)11))));
        FieldMetaData.addStructMetaDataMap(FireEventRequest.class, metaDataMap = Collections.unmodifiableMap((Map<? extends _Fields, ? extends FieldMetaData>)tmpMap));
    }
    
    public enum _Fields implements TFieldIdEnum
    {
        SUCCESSFUL((short)1, "successful"), 
        DATA((short)2, "data"), 
        DB_NAME((short)3, "dbName"), 
        TABLE_NAME((short)4, "tableName"), 
        PARTITION_VALS((short)5, "partitionVals");
        
        private static final Map<String, _Fields> byName;
        private final short _thriftId;
        private final String _fieldName;
        
        public static _Fields findByThriftId(final int fieldId) {
            switch (fieldId) {
                case 1: {
                    return _Fields.SUCCESSFUL;
                }
                case 2: {
                    return _Fields.DATA;
                }
                case 3: {
                    return _Fields.DB_NAME;
                }
                case 4: {
                    return _Fields.TABLE_NAME;
                }
                case 5: {
                    return _Fields.PARTITION_VALS;
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
    
    private static class FireEventRequestStandardSchemeFactory implements SchemeFactory
    {
        @Override
        public FireEventRequestStandardScheme getScheme() {
            return new FireEventRequestStandardScheme();
        }
    }
    
    private static class FireEventRequestStandardScheme extends StandardScheme<FireEventRequest>
    {
        @Override
        public void read(final TProtocol iprot, final FireEventRequest struct) throws TException {
            iprot.readStructBegin();
            while (true) {
                final TField schemeField = iprot.readFieldBegin();
                if (schemeField.type == 0) {
                    break;
                }
                switch (schemeField.id) {
                    case 1: {
                        if (schemeField.type == 2) {
                            struct.successful = iprot.readBool();
                            struct.setSuccessfulIsSet(true);
                            break;
                        }
                        TProtocolUtil.skip(iprot, schemeField.type);
                        break;
                    }
                    case 2: {
                        if (schemeField.type == 12) {
                            struct.data = new FireEventRequestData();
                            struct.data.read(iprot);
                            struct.setDataIsSet(true);
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
                            struct.tableName = iprot.readString();
                            struct.setTableNameIsSet(true);
                            break;
                        }
                        TProtocolUtil.skip(iprot, schemeField.type);
                        break;
                    }
                    case 5: {
                        if (schemeField.type == 15) {
                            final TList _list516 = iprot.readListBegin();
                            struct.partitionVals = (List<String>)new ArrayList(_list516.size);
                            for (int _i517 = 0; _i517 < _list516.size; ++_i517) {
                                final String _elem518 = iprot.readString();
                                struct.partitionVals.add(_elem518);
                            }
                            iprot.readListEnd();
                            struct.setPartitionValsIsSet(true);
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
        public void write(final TProtocol oprot, final FireEventRequest struct) throws TException {
            struct.validate();
            oprot.writeStructBegin(FireEventRequest.STRUCT_DESC);
            oprot.writeFieldBegin(FireEventRequest.SUCCESSFUL_FIELD_DESC);
            oprot.writeBool(struct.successful);
            oprot.writeFieldEnd();
            if (struct.data != null) {
                oprot.writeFieldBegin(FireEventRequest.DATA_FIELD_DESC);
                struct.data.write(oprot);
                oprot.writeFieldEnd();
            }
            if (struct.dbName != null && struct.isSetDbName()) {
                oprot.writeFieldBegin(FireEventRequest.DB_NAME_FIELD_DESC);
                oprot.writeString(struct.dbName);
                oprot.writeFieldEnd();
            }
            if (struct.tableName != null && struct.isSetTableName()) {
                oprot.writeFieldBegin(FireEventRequest.TABLE_NAME_FIELD_DESC);
                oprot.writeString(struct.tableName);
                oprot.writeFieldEnd();
            }
            if (struct.partitionVals != null && struct.isSetPartitionVals()) {
                oprot.writeFieldBegin(FireEventRequest.PARTITION_VALS_FIELD_DESC);
                oprot.writeListBegin(new TList((byte)11, struct.partitionVals.size()));
                for (final String _iter519 : struct.partitionVals) {
                    oprot.writeString(_iter519);
                }
                oprot.writeListEnd();
                oprot.writeFieldEnd();
            }
            oprot.writeFieldStop();
            oprot.writeStructEnd();
        }
    }
    
    private static class FireEventRequestTupleSchemeFactory implements SchemeFactory
    {
        @Override
        public FireEventRequestTupleScheme getScheme() {
            return new FireEventRequestTupleScheme();
        }
    }
    
    private static class FireEventRequestTupleScheme extends TupleScheme<FireEventRequest>
    {
        @Override
        public void write(final TProtocol prot, final FireEventRequest struct) throws TException {
            final TTupleProtocol oprot = (TTupleProtocol)prot;
            oprot.writeBool(struct.successful);
            struct.data.write(oprot);
            final BitSet optionals = new BitSet();
            if (struct.isSetDbName()) {
                optionals.set(0);
            }
            if (struct.isSetTableName()) {
                optionals.set(1);
            }
            if (struct.isSetPartitionVals()) {
                optionals.set(2);
            }
            oprot.writeBitSet(optionals, 3);
            if (struct.isSetDbName()) {
                oprot.writeString(struct.dbName);
            }
            if (struct.isSetTableName()) {
                oprot.writeString(struct.tableName);
            }
            if (struct.isSetPartitionVals()) {
                oprot.writeI32(struct.partitionVals.size());
                for (final String _iter520 : struct.partitionVals) {
                    oprot.writeString(_iter520);
                }
            }
        }
        
        @Override
        public void read(final TProtocol prot, final FireEventRequest struct) throws TException {
            final TTupleProtocol iprot = (TTupleProtocol)prot;
            struct.successful = iprot.readBool();
            struct.setSuccessfulIsSet(true);
            struct.data = new FireEventRequestData();
            struct.data.read(iprot);
            struct.setDataIsSet(true);
            final BitSet incoming = iprot.readBitSet(3);
            if (incoming.get(0)) {
                struct.dbName = iprot.readString();
                struct.setDbNameIsSet(true);
            }
            if (incoming.get(1)) {
                struct.tableName = iprot.readString();
                struct.setTableNameIsSet(true);
            }
            if (incoming.get(2)) {
                final TList _list521 = new TList((byte)11, iprot.readI32());
                struct.partitionVals = (List<String>)new ArrayList(_list521.size);
                for (int _i522 = 0; _i522 < _list521.size; ++_i522) {
                    final String _elem523 = iprot.readString();
                    struct.partitionVals.add(_elem523);
                }
                struct.setPartitionValsIsSet(true);
            }
        }
    }
}
