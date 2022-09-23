// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.hive.metastore.api;

import org.apache.thrift.protocol.TTupleProtocol;
import org.apache.thrift.protocol.TList;
import org.apache.thrift.protocol.TProtocolUtil;
import java.util.EnumSet;
import java.util.Collections;
import org.apache.thrift.meta_data.ListMetaData;
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

public class AddDynamicPartitions implements TBase<AddDynamicPartitions, _Fields>, Serializable, Cloneable
{
    private static final TStruct STRUCT_DESC;
    private static final TField TXNID_FIELD_DESC;
    private static final TField DBNAME_FIELD_DESC;
    private static final TField TABLENAME_FIELD_DESC;
    private static final TField PARTITIONNAMES_FIELD_DESC;
    private static final Map<Class<? extends IScheme>, SchemeFactory> schemes;
    private long txnid;
    private String dbname;
    private String tablename;
    private List<String> partitionnames;
    private static final int __TXNID_ISSET_ID = 0;
    private byte __isset_bitfield;
    public static final Map<_Fields, FieldMetaData> metaDataMap;
    
    public AddDynamicPartitions() {
        this.__isset_bitfield = 0;
    }
    
    public AddDynamicPartitions(final long txnid, final String dbname, final String tablename, final List<String> partitionnames) {
        this();
        this.txnid = txnid;
        this.setTxnidIsSet(true);
        this.dbname = dbname;
        this.tablename = tablename;
        this.partitionnames = partitionnames;
    }
    
    public AddDynamicPartitions(final AddDynamicPartitions other) {
        this.__isset_bitfield = 0;
        this.__isset_bitfield = other.__isset_bitfield;
        this.txnid = other.txnid;
        if (other.isSetDbname()) {
            this.dbname = other.dbname;
        }
        if (other.isSetTablename()) {
            this.tablename = other.tablename;
        }
        if (other.isSetPartitionnames()) {
            final List<String> __this__partitionnames = new ArrayList<String>();
            for (final String other_element : other.partitionnames) {
                __this__partitionnames.add(other_element);
            }
            this.partitionnames = __this__partitionnames;
        }
    }
    
    @Override
    public AddDynamicPartitions deepCopy() {
        return new AddDynamicPartitions(this);
    }
    
    @Override
    public void clear() {
        this.setTxnidIsSet(false);
        this.txnid = 0L;
        this.dbname = null;
        this.tablename = null;
        this.partitionnames = null;
    }
    
    public long getTxnid() {
        return this.txnid;
    }
    
    public void setTxnid(final long txnid) {
        this.txnid = txnid;
        this.setTxnidIsSet(true);
    }
    
    public void unsetTxnid() {
        this.__isset_bitfield = EncodingUtils.clearBit(this.__isset_bitfield, 0);
    }
    
    public boolean isSetTxnid() {
        return EncodingUtils.testBit(this.__isset_bitfield, 0);
    }
    
    public void setTxnidIsSet(final boolean value) {
        this.__isset_bitfield = EncodingUtils.setBit(this.__isset_bitfield, 0, value);
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
    
    public int getPartitionnamesSize() {
        return (this.partitionnames == null) ? 0 : this.partitionnames.size();
    }
    
    public Iterator<String> getPartitionnamesIterator() {
        return (this.partitionnames == null) ? null : this.partitionnames.iterator();
    }
    
    public void addToPartitionnames(final String elem) {
        if (this.partitionnames == null) {
            this.partitionnames = new ArrayList<String>();
        }
        this.partitionnames.add(elem);
    }
    
    public List<String> getPartitionnames() {
        return this.partitionnames;
    }
    
    public void setPartitionnames(final List<String> partitionnames) {
        this.partitionnames = partitionnames;
    }
    
    public void unsetPartitionnames() {
        this.partitionnames = null;
    }
    
    public boolean isSetPartitionnames() {
        return this.partitionnames != null;
    }
    
    public void setPartitionnamesIsSet(final boolean value) {
        if (!value) {
            this.partitionnames = null;
        }
    }
    
    @Override
    public void setFieldValue(final _Fields field, final Object value) {
        switch (field) {
            case TXNID: {
                if (value == null) {
                    this.unsetTxnid();
                    break;
                }
                this.setTxnid((long)value);
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
            case PARTITIONNAMES: {
                if (value == null) {
                    this.unsetPartitionnames();
                    break;
                }
                this.setPartitionnames((List<String>)value);
                break;
            }
        }
    }
    
    @Override
    public Object getFieldValue(final _Fields field) {
        switch (field) {
            case TXNID: {
                return this.getTxnid();
            }
            case DBNAME: {
                return this.getDbname();
            }
            case TABLENAME: {
                return this.getTablename();
            }
            case PARTITIONNAMES: {
                return this.getPartitionnames();
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
            case TXNID: {
                return this.isSetTxnid();
            }
            case DBNAME: {
                return this.isSetDbname();
            }
            case TABLENAME: {
                return this.isSetTablename();
            }
            case PARTITIONNAMES: {
                return this.isSetPartitionnames();
            }
            default: {
                throw new IllegalStateException();
            }
        }
    }
    
    @Override
    public boolean equals(final Object that) {
        return that != null && that instanceof AddDynamicPartitions && this.equals((AddDynamicPartitions)that);
    }
    
    public boolean equals(final AddDynamicPartitions that) {
        if (that == null) {
            return false;
        }
        final boolean this_present_txnid = true;
        final boolean that_present_txnid = true;
        if (this_present_txnid || that_present_txnid) {
            if (!this_present_txnid || !that_present_txnid) {
                return false;
            }
            if (this.txnid != that.txnid) {
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
        final boolean this_present_partitionnames = this.isSetPartitionnames();
        final boolean that_present_partitionnames = that.isSetPartitionnames();
        if (this_present_partitionnames || that_present_partitionnames) {
            if (!this_present_partitionnames || !that_present_partitionnames) {
                return false;
            }
            if (!this.partitionnames.equals(that.partitionnames)) {
                return false;
            }
        }
        return true;
    }
    
    @Override
    public int hashCode() {
        final HashCodeBuilder builder = new HashCodeBuilder();
        final boolean present_txnid = true;
        builder.append(present_txnid);
        if (present_txnid) {
            builder.append(this.txnid);
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
        final boolean present_partitionnames = this.isSetPartitionnames();
        builder.append(present_partitionnames);
        if (present_partitionnames) {
            builder.append(this.partitionnames);
        }
        return builder.toHashCode();
    }
    
    @Override
    public int compareTo(final AddDynamicPartitions other) {
        if (!this.getClass().equals(other.getClass())) {
            return this.getClass().getName().compareTo(other.getClass().getName());
        }
        int lastComparison = 0;
        final AddDynamicPartitions typedOther = other;
        lastComparison = Boolean.valueOf(this.isSetTxnid()).compareTo(Boolean.valueOf(typedOther.isSetTxnid()));
        if (lastComparison != 0) {
            return lastComparison;
        }
        if (this.isSetTxnid()) {
            lastComparison = TBaseHelper.compareTo(this.txnid, typedOther.txnid);
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
        lastComparison = Boolean.valueOf(this.isSetPartitionnames()).compareTo(Boolean.valueOf(typedOther.isSetPartitionnames()));
        if (lastComparison != 0) {
            return lastComparison;
        }
        if (this.isSetPartitionnames()) {
            lastComparison = TBaseHelper.compareTo(this.partitionnames, typedOther.partitionnames);
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
        AddDynamicPartitions.schemes.get(iprot.getScheme()).getScheme().read(iprot, this);
    }
    
    @Override
    public void write(final TProtocol oprot) throws TException {
        AddDynamicPartitions.schemes.get(oprot.getScheme()).getScheme().write(oprot, this);
    }
    
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("AddDynamicPartitions(");
        boolean first = true;
        sb.append("txnid:");
        sb.append(this.txnid);
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
        if (!first) {
            sb.append(", ");
        }
        sb.append("partitionnames:");
        if (this.partitionnames == null) {
            sb.append("null");
        }
        else {
            sb.append(this.partitionnames);
        }
        first = false;
        sb.append(")");
        return sb.toString();
    }
    
    public void validate() throws TException {
        if (!this.isSetTxnid()) {
            throw new TProtocolException("Required field 'txnid' is unset! Struct:" + this.toString());
        }
        if (!this.isSetDbname()) {
            throw new TProtocolException("Required field 'dbname' is unset! Struct:" + this.toString());
        }
        if (!this.isSetTablename()) {
            throw new TProtocolException("Required field 'tablename' is unset! Struct:" + this.toString());
        }
        if (!this.isSetPartitionnames()) {
            throw new TProtocolException("Required field 'partitionnames' is unset! Struct:" + this.toString());
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
        STRUCT_DESC = new TStruct("AddDynamicPartitions");
        TXNID_FIELD_DESC = new TField("txnid", (byte)10, (short)1);
        DBNAME_FIELD_DESC = new TField("dbname", (byte)11, (short)2);
        TABLENAME_FIELD_DESC = new TField("tablename", (byte)11, (short)3);
        PARTITIONNAMES_FIELD_DESC = new TField("partitionnames", (byte)15, (short)4);
        (schemes = new HashMap<Class<? extends IScheme>, SchemeFactory>()).put(StandardScheme.class, new AddDynamicPartitionsStandardSchemeFactory());
        AddDynamicPartitions.schemes.put(TupleScheme.class, new AddDynamicPartitionsTupleSchemeFactory());
        final Map<_Fields, FieldMetaData> tmpMap = new EnumMap<_Fields, FieldMetaData>(_Fields.class);
        tmpMap.put(_Fields.TXNID, new FieldMetaData("txnid", (byte)1, new FieldValueMetaData((byte)10)));
        tmpMap.put(_Fields.DBNAME, new FieldMetaData("dbname", (byte)1, new FieldValueMetaData((byte)11)));
        tmpMap.put(_Fields.TABLENAME, new FieldMetaData("tablename", (byte)1, new FieldValueMetaData((byte)11)));
        tmpMap.put(_Fields.PARTITIONNAMES, new FieldMetaData("partitionnames", (byte)1, new ListMetaData((byte)15, new FieldValueMetaData((byte)11))));
        FieldMetaData.addStructMetaDataMap(AddDynamicPartitions.class, metaDataMap = Collections.unmodifiableMap((Map<? extends _Fields, ? extends FieldMetaData>)tmpMap));
    }
    
    public enum _Fields implements TFieldIdEnum
    {
        TXNID((short)1, "txnid"), 
        DBNAME((short)2, "dbname"), 
        TABLENAME((short)3, "tablename"), 
        PARTITIONNAMES((short)4, "partitionnames");
        
        private static final Map<String, _Fields> byName;
        private final short _thriftId;
        private final String _fieldName;
        
        public static _Fields findByThriftId(final int fieldId) {
            switch (fieldId) {
                case 1: {
                    return _Fields.TXNID;
                }
                case 2: {
                    return _Fields.DBNAME;
                }
                case 3: {
                    return _Fields.TABLENAME;
                }
                case 4: {
                    return _Fields.PARTITIONNAMES;
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
    
    private static class AddDynamicPartitionsStandardSchemeFactory implements SchemeFactory
    {
        @Override
        public AddDynamicPartitionsStandardScheme getScheme() {
            return new AddDynamicPartitionsStandardScheme();
        }
    }
    
    private static class AddDynamicPartitionsStandardScheme extends StandardScheme<AddDynamicPartitions>
    {
        @Override
        public void read(final TProtocol iprot, final AddDynamicPartitions struct) throws TException {
            iprot.readStructBegin();
            while (true) {
                final TField schemeField = iprot.readFieldBegin();
                if (schemeField.type == 0) {
                    break;
                }
                switch (schemeField.id) {
                    case 1: {
                        if (schemeField.type == 10) {
                            struct.txnid = iprot.readI64();
                            struct.setTxnidIsSet(true);
                            break;
                        }
                        TProtocolUtil.skip(iprot, schemeField.type);
                        break;
                    }
                    case 2: {
                        if (schemeField.type == 11) {
                            struct.dbname = iprot.readString();
                            struct.setDbnameIsSet(true);
                            break;
                        }
                        TProtocolUtil.skip(iprot, schemeField.type);
                        break;
                    }
                    case 3: {
                        if (schemeField.type == 11) {
                            struct.tablename = iprot.readString();
                            struct.setTablenameIsSet(true);
                            break;
                        }
                        TProtocolUtil.skip(iprot, schemeField.type);
                        break;
                    }
                    case 4: {
                        if (schemeField.type == 15) {
                            final TList _list492 = iprot.readListBegin();
                            struct.partitionnames = (List<String>)new ArrayList(_list492.size);
                            for (int _i493 = 0; _i493 < _list492.size; ++_i493) {
                                final String _elem494 = iprot.readString();
                                struct.partitionnames.add(_elem494);
                            }
                            iprot.readListEnd();
                            struct.setPartitionnamesIsSet(true);
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
        public void write(final TProtocol oprot, final AddDynamicPartitions struct) throws TException {
            struct.validate();
            oprot.writeStructBegin(AddDynamicPartitions.STRUCT_DESC);
            oprot.writeFieldBegin(AddDynamicPartitions.TXNID_FIELD_DESC);
            oprot.writeI64(struct.txnid);
            oprot.writeFieldEnd();
            if (struct.dbname != null) {
                oprot.writeFieldBegin(AddDynamicPartitions.DBNAME_FIELD_DESC);
                oprot.writeString(struct.dbname);
                oprot.writeFieldEnd();
            }
            if (struct.tablename != null) {
                oprot.writeFieldBegin(AddDynamicPartitions.TABLENAME_FIELD_DESC);
                oprot.writeString(struct.tablename);
                oprot.writeFieldEnd();
            }
            if (struct.partitionnames != null) {
                oprot.writeFieldBegin(AddDynamicPartitions.PARTITIONNAMES_FIELD_DESC);
                oprot.writeListBegin(new TList((byte)11, struct.partitionnames.size()));
                for (final String _iter495 : struct.partitionnames) {
                    oprot.writeString(_iter495);
                }
                oprot.writeListEnd();
                oprot.writeFieldEnd();
            }
            oprot.writeFieldStop();
            oprot.writeStructEnd();
        }
    }
    
    private static class AddDynamicPartitionsTupleSchemeFactory implements SchemeFactory
    {
        @Override
        public AddDynamicPartitionsTupleScheme getScheme() {
            return new AddDynamicPartitionsTupleScheme();
        }
    }
    
    private static class AddDynamicPartitionsTupleScheme extends TupleScheme<AddDynamicPartitions>
    {
        @Override
        public void write(final TProtocol prot, final AddDynamicPartitions struct) throws TException {
            final TTupleProtocol oprot = (TTupleProtocol)prot;
            oprot.writeI64(struct.txnid);
            oprot.writeString(struct.dbname);
            oprot.writeString(struct.tablename);
            oprot.writeI32(struct.partitionnames.size());
            for (final String _iter496 : struct.partitionnames) {
                oprot.writeString(_iter496);
            }
        }
        
        @Override
        public void read(final TProtocol prot, final AddDynamicPartitions struct) throws TException {
            final TTupleProtocol iprot = (TTupleProtocol)prot;
            struct.txnid = iprot.readI64();
            struct.setTxnidIsSet(true);
            struct.dbname = iprot.readString();
            struct.setDbnameIsSet(true);
            struct.tablename = iprot.readString();
            struct.setTablenameIsSet(true);
            final TList _list497 = new TList((byte)11, iprot.readI32());
            struct.partitionnames = (List<String>)new ArrayList(_list497.size);
            for (int _i498 = 0; _i498 < _list497.size; ++_i498) {
                final String _elem499 = iprot.readString();
                struct.partitionnames.add(_elem499);
            }
            struct.setPartitionnamesIsSet(true);
        }
    }
}
