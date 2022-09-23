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

public class ShowCompactResponseElement implements TBase<ShowCompactResponseElement, _Fields>, Serializable, Cloneable
{
    private static final TStruct STRUCT_DESC;
    private static final TField DBNAME_FIELD_DESC;
    private static final TField TABLENAME_FIELD_DESC;
    private static final TField PARTITIONNAME_FIELD_DESC;
    private static final TField TYPE_FIELD_DESC;
    private static final TField STATE_FIELD_DESC;
    private static final TField WORKERID_FIELD_DESC;
    private static final TField START_FIELD_DESC;
    private static final TField RUN_AS_FIELD_DESC;
    private static final Map<Class<? extends IScheme>, SchemeFactory> schemes;
    private String dbname;
    private String tablename;
    private String partitionname;
    private CompactionType type;
    private String state;
    private String workerid;
    private long start;
    private String runAs;
    private static final int __START_ISSET_ID = 0;
    private byte __isset_bitfield;
    private _Fields[] optionals;
    public static final Map<_Fields, FieldMetaData> metaDataMap;
    
    public ShowCompactResponseElement() {
        this.__isset_bitfield = 0;
        this.optionals = new _Fields[] { _Fields.PARTITIONNAME, _Fields.WORKERID, _Fields.START, _Fields.RUN_AS };
    }
    
    public ShowCompactResponseElement(final String dbname, final String tablename, final CompactionType type, final String state) {
        this();
        this.dbname = dbname;
        this.tablename = tablename;
        this.type = type;
        this.state = state;
    }
    
    public ShowCompactResponseElement(final ShowCompactResponseElement other) {
        this.__isset_bitfield = 0;
        this.optionals = new _Fields[] { _Fields.PARTITIONNAME, _Fields.WORKERID, _Fields.START, _Fields.RUN_AS };
        this.__isset_bitfield = other.__isset_bitfield;
        if (other.isSetDbname()) {
            this.dbname = other.dbname;
        }
        if (other.isSetTablename()) {
            this.tablename = other.tablename;
        }
        if (other.isSetPartitionname()) {
            this.partitionname = other.partitionname;
        }
        if (other.isSetType()) {
            this.type = other.type;
        }
        if (other.isSetState()) {
            this.state = other.state;
        }
        if (other.isSetWorkerid()) {
            this.workerid = other.workerid;
        }
        this.start = other.start;
        if (other.isSetRunAs()) {
            this.runAs = other.runAs;
        }
    }
    
    @Override
    public ShowCompactResponseElement deepCopy() {
        return new ShowCompactResponseElement(this);
    }
    
    @Override
    public void clear() {
        this.dbname = null;
        this.tablename = null;
        this.partitionname = null;
        this.type = null;
        this.state = null;
        this.workerid = null;
        this.setStartIsSet(false);
        this.start = 0L;
        this.runAs = null;
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
    
    public String getPartitionname() {
        return this.partitionname;
    }
    
    public void setPartitionname(final String partitionname) {
        this.partitionname = partitionname;
    }
    
    public void unsetPartitionname() {
        this.partitionname = null;
    }
    
    public boolean isSetPartitionname() {
        return this.partitionname != null;
    }
    
    public void setPartitionnameIsSet(final boolean value) {
        if (!value) {
            this.partitionname = null;
        }
    }
    
    public CompactionType getType() {
        return this.type;
    }
    
    public void setType(final CompactionType type) {
        this.type = type;
    }
    
    public void unsetType() {
        this.type = null;
    }
    
    public boolean isSetType() {
        return this.type != null;
    }
    
    public void setTypeIsSet(final boolean value) {
        if (!value) {
            this.type = null;
        }
    }
    
    public String getState() {
        return this.state;
    }
    
    public void setState(final String state) {
        this.state = state;
    }
    
    public void unsetState() {
        this.state = null;
    }
    
    public boolean isSetState() {
        return this.state != null;
    }
    
    public void setStateIsSet(final boolean value) {
        if (!value) {
            this.state = null;
        }
    }
    
    public String getWorkerid() {
        return this.workerid;
    }
    
    public void setWorkerid(final String workerid) {
        this.workerid = workerid;
    }
    
    public void unsetWorkerid() {
        this.workerid = null;
    }
    
    public boolean isSetWorkerid() {
        return this.workerid != null;
    }
    
    public void setWorkeridIsSet(final boolean value) {
        if (!value) {
            this.workerid = null;
        }
    }
    
    public long getStart() {
        return this.start;
    }
    
    public void setStart(final long start) {
        this.start = start;
        this.setStartIsSet(true);
    }
    
    public void unsetStart() {
        this.__isset_bitfield = EncodingUtils.clearBit(this.__isset_bitfield, 0);
    }
    
    public boolean isSetStart() {
        return EncodingUtils.testBit(this.__isset_bitfield, 0);
    }
    
    public void setStartIsSet(final boolean value) {
        this.__isset_bitfield = EncodingUtils.setBit(this.__isset_bitfield, 0, value);
    }
    
    public String getRunAs() {
        return this.runAs;
    }
    
    public void setRunAs(final String runAs) {
        this.runAs = runAs;
    }
    
    public void unsetRunAs() {
        this.runAs = null;
    }
    
    public boolean isSetRunAs() {
        return this.runAs != null;
    }
    
    public void setRunAsIsSet(final boolean value) {
        if (!value) {
            this.runAs = null;
        }
    }
    
    @Override
    public void setFieldValue(final _Fields field, final Object value) {
        switch (field) {
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
            case PARTITIONNAME: {
                if (value == null) {
                    this.unsetPartitionname();
                    break;
                }
                this.setPartitionname((String)value);
                break;
            }
            case TYPE: {
                if (value == null) {
                    this.unsetType();
                    break;
                }
                this.setType((CompactionType)value);
                break;
            }
            case STATE: {
                if (value == null) {
                    this.unsetState();
                    break;
                }
                this.setState((String)value);
                break;
            }
            case WORKERID: {
                if (value == null) {
                    this.unsetWorkerid();
                    break;
                }
                this.setWorkerid((String)value);
                break;
            }
            case START: {
                if (value == null) {
                    this.unsetStart();
                    break;
                }
                this.setStart((long)value);
                break;
            }
            case RUN_AS: {
                if (value == null) {
                    this.unsetRunAs();
                    break;
                }
                this.setRunAs((String)value);
                break;
            }
        }
    }
    
    @Override
    public Object getFieldValue(final _Fields field) {
        switch (field) {
            case DBNAME: {
                return this.getDbname();
            }
            case TABLENAME: {
                return this.getTablename();
            }
            case PARTITIONNAME: {
                return this.getPartitionname();
            }
            case TYPE: {
                return this.getType();
            }
            case STATE: {
                return this.getState();
            }
            case WORKERID: {
                return this.getWorkerid();
            }
            case START: {
                return this.getStart();
            }
            case RUN_AS: {
                return this.getRunAs();
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
            case DBNAME: {
                return this.isSetDbname();
            }
            case TABLENAME: {
                return this.isSetTablename();
            }
            case PARTITIONNAME: {
                return this.isSetPartitionname();
            }
            case TYPE: {
                return this.isSetType();
            }
            case STATE: {
                return this.isSetState();
            }
            case WORKERID: {
                return this.isSetWorkerid();
            }
            case START: {
                return this.isSetStart();
            }
            case RUN_AS: {
                return this.isSetRunAs();
            }
            default: {
                throw new IllegalStateException();
            }
        }
    }
    
    @Override
    public boolean equals(final Object that) {
        return that != null && that instanceof ShowCompactResponseElement && this.equals((ShowCompactResponseElement)that);
    }
    
    public boolean equals(final ShowCompactResponseElement that) {
        if (that == null) {
            return false;
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
        final boolean this_present_partitionname = this.isSetPartitionname();
        final boolean that_present_partitionname = that.isSetPartitionname();
        if (this_present_partitionname || that_present_partitionname) {
            if (!this_present_partitionname || !that_present_partitionname) {
                return false;
            }
            if (!this.partitionname.equals(that.partitionname)) {
                return false;
            }
        }
        final boolean this_present_type = this.isSetType();
        final boolean that_present_type = that.isSetType();
        if (this_present_type || that_present_type) {
            if (!this_present_type || !that_present_type) {
                return false;
            }
            if (!this.type.equals(that.type)) {
                return false;
            }
        }
        final boolean this_present_state = this.isSetState();
        final boolean that_present_state = that.isSetState();
        if (this_present_state || that_present_state) {
            if (!this_present_state || !that_present_state) {
                return false;
            }
            if (!this.state.equals(that.state)) {
                return false;
            }
        }
        final boolean this_present_workerid = this.isSetWorkerid();
        final boolean that_present_workerid = that.isSetWorkerid();
        if (this_present_workerid || that_present_workerid) {
            if (!this_present_workerid || !that_present_workerid) {
                return false;
            }
            if (!this.workerid.equals(that.workerid)) {
                return false;
            }
        }
        final boolean this_present_start = this.isSetStart();
        final boolean that_present_start = that.isSetStart();
        if (this_present_start || that_present_start) {
            if (!this_present_start || !that_present_start) {
                return false;
            }
            if (this.start != that.start) {
                return false;
            }
        }
        final boolean this_present_runAs = this.isSetRunAs();
        final boolean that_present_runAs = that.isSetRunAs();
        if (this_present_runAs || that_present_runAs) {
            if (!this_present_runAs || !that_present_runAs) {
                return false;
            }
            if (!this.runAs.equals(that.runAs)) {
                return false;
            }
        }
        return true;
    }
    
    @Override
    public int hashCode() {
        final HashCodeBuilder builder = new HashCodeBuilder();
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
        final boolean present_partitionname = this.isSetPartitionname();
        builder.append(present_partitionname);
        if (present_partitionname) {
            builder.append(this.partitionname);
        }
        final boolean present_type = this.isSetType();
        builder.append(present_type);
        if (present_type) {
            builder.append(this.type.getValue());
        }
        final boolean present_state = this.isSetState();
        builder.append(present_state);
        if (present_state) {
            builder.append(this.state);
        }
        final boolean present_workerid = this.isSetWorkerid();
        builder.append(present_workerid);
        if (present_workerid) {
            builder.append(this.workerid);
        }
        final boolean present_start = this.isSetStart();
        builder.append(present_start);
        if (present_start) {
            builder.append(this.start);
        }
        final boolean present_runAs = this.isSetRunAs();
        builder.append(present_runAs);
        if (present_runAs) {
            builder.append(this.runAs);
        }
        return builder.toHashCode();
    }
    
    @Override
    public int compareTo(final ShowCompactResponseElement other) {
        if (!this.getClass().equals(other.getClass())) {
            return this.getClass().getName().compareTo(other.getClass().getName());
        }
        int lastComparison = 0;
        final ShowCompactResponseElement typedOther = other;
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
        lastComparison = Boolean.valueOf(this.isSetPartitionname()).compareTo(Boolean.valueOf(typedOther.isSetPartitionname()));
        if (lastComparison != 0) {
            return lastComparison;
        }
        if (this.isSetPartitionname()) {
            lastComparison = TBaseHelper.compareTo(this.partitionname, typedOther.partitionname);
            if (lastComparison != 0) {
                return lastComparison;
            }
        }
        lastComparison = Boolean.valueOf(this.isSetType()).compareTo(Boolean.valueOf(typedOther.isSetType()));
        if (lastComparison != 0) {
            return lastComparison;
        }
        if (this.isSetType()) {
            lastComparison = TBaseHelper.compareTo(this.type, typedOther.type);
            if (lastComparison != 0) {
                return lastComparison;
            }
        }
        lastComparison = Boolean.valueOf(this.isSetState()).compareTo(Boolean.valueOf(typedOther.isSetState()));
        if (lastComparison != 0) {
            return lastComparison;
        }
        if (this.isSetState()) {
            lastComparison = TBaseHelper.compareTo(this.state, typedOther.state);
            if (lastComparison != 0) {
                return lastComparison;
            }
        }
        lastComparison = Boolean.valueOf(this.isSetWorkerid()).compareTo(Boolean.valueOf(typedOther.isSetWorkerid()));
        if (lastComparison != 0) {
            return lastComparison;
        }
        if (this.isSetWorkerid()) {
            lastComparison = TBaseHelper.compareTo(this.workerid, typedOther.workerid);
            if (lastComparison != 0) {
                return lastComparison;
            }
        }
        lastComparison = Boolean.valueOf(this.isSetStart()).compareTo(Boolean.valueOf(typedOther.isSetStart()));
        if (lastComparison != 0) {
            return lastComparison;
        }
        if (this.isSetStart()) {
            lastComparison = TBaseHelper.compareTo(this.start, typedOther.start);
            if (lastComparison != 0) {
                return lastComparison;
            }
        }
        lastComparison = Boolean.valueOf(this.isSetRunAs()).compareTo(Boolean.valueOf(typedOther.isSetRunAs()));
        if (lastComparison != 0) {
            return lastComparison;
        }
        if (this.isSetRunAs()) {
            lastComparison = TBaseHelper.compareTo(this.runAs, typedOther.runAs);
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
        ShowCompactResponseElement.schemes.get(iprot.getScheme()).getScheme().read(iprot, this);
    }
    
    @Override
    public void write(final TProtocol oprot) throws TException {
        ShowCompactResponseElement.schemes.get(oprot.getScheme()).getScheme().write(oprot, this);
    }
    
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("ShowCompactResponseElement(");
        boolean first = true;
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
        if (this.isSetPartitionname()) {
            if (!first) {
                sb.append(", ");
            }
            sb.append("partitionname:");
            if (this.partitionname == null) {
                sb.append("null");
            }
            else {
                sb.append(this.partitionname);
            }
            first = false;
        }
        if (!first) {
            sb.append(", ");
        }
        sb.append("type:");
        if (this.type == null) {
            sb.append("null");
        }
        else {
            sb.append(this.type);
        }
        first = false;
        if (!first) {
            sb.append(", ");
        }
        sb.append("state:");
        if (this.state == null) {
            sb.append("null");
        }
        else {
            sb.append(this.state);
        }
        first = false;
        if (this.isSetWorkerid()) {
            if (!first) {
                sb.append(", ");
            }
            sb.append("workerid:");
            if (this.workerid == null) {
                sb.append("null");
            }
            else {
                sb.append(this.workerid);
            }
            first = false;
        }
        if (this.isSetStart()) {
            if (!first) {
                sb.append(", ");
            }
            sb.append("start:");
            sb.append(this.start);
            first = false;
        }
        if (this.isSetRunAs()) {
            if (!first) {
                sb.append(", ");
            }
            sb.append("runAs:");
            if (this.runAs == null) {
                sb.append("null");
            }
            else {
                sb.append(this.runAs);
            }
            first = false;
        }
        sb.append(")");
        return sb.toString();
    }
    
    public void validate() throws TException {
        if (!this.isSetDbname()) {
            throw new TProtocolException("Required field 'dbname' is unset! Struct:" + this.toString());
        }
        if (!this.isSetTablename()) {
            throw new TProtocolException("Required field 'tablename' is unset! Struct:" + this.toString());
        }
        if (!this.isSetType()) {
            throw new TProtocolException("Required field 'type' is unset! Struct:" + this.toString());
        }
        if (!this.isSetState()) {
            throw new TProtocolException("Required field 'state' is unset! Struct:" + this.toString());
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
        STRUCT_DESC = new TStruct("ShowCompactResponseElement");
        DBNAME_FIELD_DESC = new TField("dbname", (byte)11, (short)1);
        TABLENAME_FIELD_DESC = new TField("tablename", (byte)11, (short)2);
        PARTITIONNAME_FIELD_DESC = new TField("partitionname", (byte)11, (short)3);
        TYPE_FIELD_DESC = new TField("type", (byte)8, (short)4);
        STATE_FIELD_DESC = new TField("state", (byte)11, (short)5);
        WORKERID_FIELD_DESC = new TField("workerid", (byte)11, (short)6);
        START_FIELD_DESC = new TField("start", (byte)10, (short)7);
        RUN_AS_FIELD_DESC = new TField("runAs", (byte)11, (short)8);
        (schemes = new HashMap<Class<? extends IScheme>, SchemeFactory>()).put(StandardScheme.class, new ShowCompactResponseElementStandardSchemeFactory());
        ShowCompactResponseElement.schemes.put(TupleScheme.class, new ShowCompactResponseElementTupleSchemeFactory());
        final Map<_Fields, FieldMetaData> tmpMap = new EnumMap<_Fields, FieldMetaData>(_Fields.class);
        tmpMap.put(_Fields.DBNAME, new FieldMetaData("dbname", (byte)1, new FieldValueMetaData((byte)11)));
        tmpMap.put(_Fields.TABLENAME, new FieldMetaData("tablename", (byte)1, new FieldValueMetaData((byte)11)));
        tmpMap.put(_Fields.PARTITIONNAME, new FieldMetaData("partitionname", (byte)2, new FieldValueMetaData((byte)11)));
        tmpMap.put(_Fields.TYPE, new FieldMetaData("type", (byte)1, new EnumMetaData((byte)16, CompactionType.class)));
        tmpMap.put(_Fields.STATE, new FieldMetaData("state", (byte)1, new FieldValueMetaData((byte)11)));
        tmpMap.put(_Fields.WORKERID, new FieldMetaData("workerid", (byte)2, new FieldValueMetaData((byte)11)));
        tmpMap.put(_Fields.START, new FieldMetaData("start", (byte)2, new FieldValueMetaData((byte)10)));
        tmpMap.put(_Fields.RUN_AS, new FieldMetaData("runAs", (byte)2, new FieldValueMetaData((byte)11)));
        FieldMetaData.addStructMetaDataMap(ShowCompactResponseElement.class, metaDataMap = Collections.unmodifiableMap((Map<? extends _Fields, ? extends FieldMetaData>)tmpMap));
    }
    
    public enum _Fields implements TFieldIdEnum
    {
        DBNAME((short)1, "dbname"), 
        TABLENAME((short)2, "tablename"), 
        PARTITIONNAME((short)3, "partitionname"), 
        TYPE((short)4, "type"), 
        STATE((short)5, "state"), 
        WORKERID((short)6, "workerid"), 
        START((short)7, "start"), 
        RUN_AS((short)8, "runAs");
        
        private static final Map<String, _Fields> byName;
        private final short _thriftId;
        private final String _fieldName;
        
        public static _Fields findByThriftId(final int fieldId) {
            switch (fieldId) {
                case 1: {
                    return _Fields.DBNAME;
                }
                case 2: {
                    return _Fields.TABLENAME;
                }
                case 3: {
                    return _Fields.PARTITIONNAME;
                }
                case 4: {
                    return _Fields.TYPE;
                }
                case 5: {
                    return _Fields.STATE;
                }
                case 6: {
                    return _Fields.WORKERID;
                }
                case 7: {
                    return _Fields.START;
                }
                case 8: {
                    return _Fields.RUN_AS;
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
    
    private static class ShowCompactResponseElementStandardSchemeFactory implements SchemeFactory
    {
        @Override
        public ShowCompactResponseElementStandardScheme getScheme() {
            return new ShowCompactResponseElementStandardScheme();
        }
    }
    
    private static class ShowCompactResponseElementStandardScheme extends StandardScheme<ShowCompactResponseElement>
    {
        @Override
        public void read(final TProtocol iprot, final ShowCompactResponseElement struct) throws TException {
            iprot.readStructBegin();
            while (true) {
                final TField schemeField = iprot.readFieldBegin();
                if (schemeField.type == 0) {
                    break;
                }
                switch (schemeField.id) {
                    case 1: {
                        if (schemeField.type == 11) {
                            struct.dbname = iprot.readString();
                            struct.setDbnameIsSet(true);
                            break;
                        }
                        TProtocolUtil.skip(iprot, schemeField.type);
                        break;
                    }
                    case 2: {
                        if (schemeField.type == 11) {
                            struct.tablename = iprot.readString();
                            struct.setTablenameIsSet(true);
                            break;
                        }
                        TProtocolUtil.skip(iprot, schemeField.type);
                        break;
                    }
                    case 3: {
                        if (schemeField.type == 11) {
                            struct.partitionname = iprot.readString();
                            struct.setPartitionnameIsSet(true);
                            break;
                        }
                        TProtocolUtil.skip(iprot, schemeField.type);
                        break;
                    }
                    case 4: {
                        if (schemeField.type == 8) {
                            struct.type = CompactionType.findByValue(iprot.readI32());
                            struct.setTypeIsSet(true);
                            break;
                        }
                        TProtocolUtil.skip(iprot, schemeField.type);
                        break;
                    }
                    case 5: {
                        if (schemeField.type == 11) {
                            struct.state = iprot.readString();
                            struct.setStateIsSet(true);
                            break;
                        }
                        TProtocolUtil.skip(iprot, schemeField.type);
                        break;
                    }
                    case 6: {
                        if (schemeField.type == 11) {
                            struct.workerid = iprot.readString();
                            struct.setWorkeridIsSet(true);
                            break;
                        }
                        TProtocolUtil.skip(iprot, schemeField.type);
                        break;
                    }
                    case 7: {
                        if (schemeField.type == 10) {
                            struct.start = iprot.readI64();
                            struct.setStartIsSet(true);
                            break;
                        }
                        TProtocolUtil.skip(iprot, schemeField.type);
                        break;
                    }
                    case 8: {
                        if (schemeField.type == 11) {
                            struct.runAs = iprot.readString();
                            struct.setRunAsIsSet(true);
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
        public void write(final TProtocol oprot, final ShowCompactResponseElement struct) throws TException {
            struct.validate();
            oprot.writeStructBegin(ShowCompactResponseElement.STRUCT_DESC);
            if (struct.dbname != null) {
                oprot.writeFieldBegin(ShowCompactResponseElement.DBNAME_FIELD_DESC);
                oprot.writeString(struct.dbname);
                oprot.writeFieldEnd();
            }
            if (struct.tablename != null) {
                oprot.writeFieldBegin(ShowCompactResponseElement.TABLENAME_FIELD_DESC);
                oprot.writeString(struct.tablename);
                oprot.writeFieldEnd();
            }
            if (struct.partitionname != null && struct.isSetPartitionname()) {
                oprot.writeFieldBegin(ShowCompactResponseElement.PARTITIONNAME_FIELD_DESC);
                oprot.writeString(struct.partitionname);
                oprot.writeFieldEnd();
            }
            if (struct.type != null) {
                oprot.writeFieldBegin(ShowCompactResponseElement.TYPE_FIELD_DESC);
                oprot.writeI32(struct.type.getValue());
                oprot.writeFieldEnd();
            }
            if (struct.state != null) {
                oprot.writeFieldBegin(ShowCompactResponseElement.STATE_FIELD_DESC);
                oprot.writeString(struct.state);
                oprot.writeFieldEnd();
            }
            if (struct.workerid != null && struct.isSetWorkerid()) {
                oprot.writeFieldBegin(ShowCompactResponseElement.WORKERID_FIELD_DESC);
                oprot.writeString(struct.workerid);
                oprot.writeFieldEnd();
            }
            if (struct.isSetStart()) {
                oprot.writeFieldBegin(ShowCompactResponseElement.START_FIELD_DESC);
                oprot.writeI64(struct.start);
                oprot.writeFieldEnd();
            }
            if (struct.runAs != null && struct.isSetRunAs()) {
                oprot.writeFieldBegin(ShowCompactResponseElement.RUN_AS_FIELD_DESC);
                oprot.writeString(struct.runAs);
                oprot.writeFieldEnd();
            }
            oprot.writeFieldStop();
            oprot.writeStructEnd();
        }
    }
    
    private static class ShowCompactResponseElementTupleSchemeFactory implements SchemeFactory
    {
        @Override
        public ShowCompactResponseElementTupleScheme getScheme() {
            return new ShowCompactResponseElementTupleScheme();
        }
    }
    
    private static class ShowCompactResponseElementTupleScheme extends TupleScheme<ShowCompactResponseElement>
    {
        @Override
        public void write(final TProtocol prot, final ShowCompactResponseElement struct) throws TException {
            final TTupleProtocol oprot = (TTupleProtocol)prot;
            oprot.writeString(struct.dbname);
            oprot.writeString(struct.tablename);
            oprot.writeI32(struct.type.getValue());
            oprot.writeString(struct.state);
            final BitSet optionals = new BitSet();
            if (struct.isSetPartitionname()) {
                optionals.set(0);
            }
            if (struct.isSetWorkerid()) {
                optionals.set(1);
            }
            if (struct.isSetStart()) {
                optionals.set(2);
            }
            if (struct.isSetRunAs()) {
                optionals.set(3);
            }
            oprot.writeBitSet(optionals, 4);
            if (struct.isSetPartitionname()) {
                oprot.writeString(struct.partitionname);
            }
            if (struct.isSetWorkerid()) {
                oprot.writeString(struct.workerid);
            }
            if (struct.isSetStart()) {
                oprot.writeI64(struct.start);
            }
            if (struct.isSetRunAs()) {
                oprot.writeString(struct.runAs);
            }
        }
        
        @Override
        public void read(final TProtocol prot, final ShowCompactResponseElement struct) throws TException {
            final TTupleProtocol iprot = (TTupleProtocol)prot;
            struct.dbname = iprot.readString();
            struct.setDbnameIsSet(true);
            struct.tablename = iprot.readString();
            struct.setTablenameIsSet(true);
            struct.type = CompactionType.findByValue(iprot.readI32());
            struct.setTypeIsSet(true);
            struct.state = iprot.readString();
            struct.setStateIsSet(true);
            final BitSet incoming = iprot.readBitSet(4);
            if (incoming.get(0)) {
                struct.partitionname = iprot.readString();
                struct.setPartitionnameIsSet(true);
            }
            if (incoming.get(1)) {
                struct.workerid = iprot.readString();
                struct.setWorkeridIsSet(true);
            }
            if (incoming.get(2)) {
                struct.start = iprot.readI64();
                struct.setStartIsSet(true);
            }
            if (incoming.get(3)) {
                struct.runAs = iprot.readString();
                struct.setRunAsIsSet(true);
            }
        }
    }
}
