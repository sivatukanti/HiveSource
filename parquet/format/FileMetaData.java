// 
// Decompiled by Procyon v0.5.36
// 

package parquet.format;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Collections;
import parquet.org.apache.thrift.meta_data.ListMetaData;
import parquet.org.apache.thrift.meta_data.StructMetaData;
import parquet.org.apache.thrift.meta_data.FieldValueMetaData;
import java.util.EnumMap;
import parquet.org.apache.thrift.TFieldIdEnum;
import parquet.org.apache.thrift.TException;
import parquet.org.apache.thrift.protocol.TList;
import parquet.org.apache.thrift.protocol.TProtocolException;
import parquet.org.apache.thrift.protocol.TProtocolUtil;
import parquet.org.apache.thrift.protocol.TProtocol;
import parquet.org.apache.thrift.TBaseHelper;
import org.apache.commons.lang.builder.HashCodeBuilder;
import java.util.Iterator;
import java.util.ArrayList;
import parquet.org.apache.thrift.meta_data.FieldMetaData;
import java.util.Map;
import java.util.BitSet;
import java.util.List;
import parquet.org.apache.thrift.protocol.TField;
import parquet.org.apache.thrift.protocol.TStruct;
import java.io.Serializable;
import parquet.org.apache.thrift.TBase;

public class FileMetaData implements TBase<FileMetaData, _Fields>, Serializable, Cloneable
{
    private static final TStruct STRUCT_DESC;
    private static final TField VERSION_FIELD_DESC;
    private static final TField SCHEMA_FIELD_DESC;
    private static final TField NUM_ROWS_FIELD_DESC;
    private static final TField ROW_GROUPS_FIELD_DESC;
    private static final TField KEY_VALUE_METADATA_FIELD_DESC;
    private static final TField CREATED_BY_FIELD_DESC;
    public int version;
    public List<SchemaElement> schema;
    public long num_rows;
    public List<RowGroup> row_groups;
    public List<KeyValue> key_value_metadata;
    public String created_by;
    private static final int __VERSION_ISSET_ID = 0;
    private static final int __NUM_ROWS_ISSET_ID = 1;
    private BitSet __isset_bit_vector;
    public static final Map<_Fields, FieldMetaData> metaDataMap;
    
    public FileMetaData() {
        this.__isset_bit_vector = new BitSet(2);
    }
    
    public FileMetaData(final int version, final List<SchemaElement> schema, final long num_rows, final List<RowGroup> row_groups) {
        this();
        this.version = version;
        this.setVersionIsSet(true);
        this.schema = schema;
        this.num_rows = num_rows;
        this.setNum_rowsIsSet(true);
        this.row_groups = row_groups;
    }
    
    public FileMetaData(final FileMetaData other) {
        (this.__isset_bit_vector = new BitSet(2)).clear();
        this.__isset_bit_vector.or(other.__isset_bit_vector);
        this.version = other.version;
        if (other.isSetSchema()) {
            final List<SchemaElement> __this__schema = new ArrayList<SchemaElement>();
            for (final SchemaElement other_element : other.schema) {
                __this__schema.add(new SchemaElement(other_element));
            }
            this.schema = __this__schema;
        }
        this.num_rows = other.num_rows;
        if (other.isSetRow_groups()) {
            final List<RowGroup> __this__row_groups = new ArrayList<RowGroup>();
            for (final RowGroup other_element2 : other.row_groups) {
                __this__row_groups.add(new RowGroup(other_element2));
            }
            this.row_groups = __this__row_groups;
        }
        if (other.isSetKey_value_metadata()) {
            final List<KeyValue> __this__key_value_metadata = new ArrayList<KeyValue>();
            for (final KeyValue other_element3 : other.key_value_metadata) {
                __this__key_value_metadata.add(new KeyValue(other_element3));
            }
            this.key_value_metadata = __this__key_value_metadata;
        }
        if (other.isSetCreated_by()) {
            this.created_by = other.created_by;
        }
    }
    
    @Override
    public FileMetaData deepCopy() {
        return new FileMetaData(this);
    }
    
    @Override
    public void clear() {
        this.setVersionIsSet(false);
        this.version = 0;
        this.schema = null;
        this.setNum_rowsIsSet(false);
        this.num_rows = 0L;
        this.row_groups = null;
        this.key_value_metadata = null;
        this.created_by = null;
    }
    
    public int getVersion() {
        return this.version;
    }
    
    public FileMetaData setVersion(final int version) {
        this.version = version;
        this.setVersionIsSet(true);
        return this;
    }
    
    public void unsetVersion() {
        this.__isset_bit_vector.clear(0);
    }
    
    public boolean isSetVersion() {
        return this.__isset_bit_vector.get(0);
    }
    
    public void setVersionIsSet(final boolean value) {
        this.__isset_bit_vector.set(0, value);
    }
    
    public int getSchemaSize() {
        return (this.schema == null) ? 0 : this.schema.size();
    }
    
    public Iterator<SchemaElement> getSchemaIterator() {
        return (this.schema == null) ? null : this.schema.iterator();
    }
    
    public void addToSchema(final SchemaElement elem) {
        if (this.schema == null) {
            this.schema = new ArrayList<SchemaElement>();
        }
        this.schema.add(elem);
    }
    
    public List<SchemaElement> getSchema() {
        return this.schema;
    }
    
    public FileMetaData setSchema(final List<SchemaElement> schema) {
        this.schema = schema;
        return this;
    }
    
    public void unsetSchema() {
        this.schema = null;
    }
    
    public boolean isSetSchema() {
        return this.schema != null;
    }
    
    public void setSchemaIsSet(final boolean value) {
        if (!value) {
            this.schema = null;
        }
    }
    
    public long getNum_rows() {
        return this.num_rows;
    }
    
    public FileMetaData setNum_rows(final long num_rows) {
        this.num_rows = num_rows;
        this.setNum_rowsIsSet(true);
        return this;
    }
    
    public void unsetNum_rows() {
        this.__isset_bit_vector.clear(1);
    }
    
    public boolean isSetNum_rows() {
        return this.__isset_bit_vector.get(1);
    }
    
    public void setNum_rowsIsSet(final boolean value) {
        this.__isset_bit_vector.set(1, value);
    }
    
    public int getRow_groupsSize() {
        return (this.row_groups == null) ? 0 : this.row_groups.size();
    }
    
    public Iterator<RowGroup> getRow_groupsIterator() {
        return (this.row_groups == null) ? null : this.row_groups.iterator();
    }
    
    public void addToRow_groups(final RowGroup elem) {
        if (this.row_groups == null) {
            this.row_groups = new ArrayList<RowGroup>();
        }
        this.row_groups.add(elem);
    }
    
    public List<RowGroup> getRow_groups() {
        return this.row_groups;
    }
    
    public FileMetaData setRow_groups(final List<RowGroup> row_groups) {
        this.row_groups = row_groups;
        return this;
    }
    
    public void unsetRow_groups() {
        this.row_groups = null;
    }
    
    public boolean isSetRow_groups() {
        return this.row_groups != null;
    }
    
    public void setRow_groupsIsSet(final boolean value) {
        if (!value) {
            this.row_groups = null;
        }
    }
    
    public int getKey_value_metadataSize() {
        return (this.key_value_metadata == null) ? 0 : this.key_value_metadata.size();
    }
    
    public Iterator<KeyValue> getKey_value_metadataIterator() {
        return (this.key_value_metadata == null) ? null : this.key_value_metadata.iterator();
    }
    
    public void addToKey_value_metadata(final KeyValue elem) {
        if (this.key_value_metadata == null) {
            this.key_value_metadata = new ArrayList<KeyValue>();
        }
        this.key_value_metadata.add(elem);
    }
    
    public List<KeyValue> getKey_value_metadata() {
        return this.key_value_metadata;
    }
    
    public FileMetaData setKey_value_metadata(final List<KeyValue> key_value_metadata) {
        this.key_value_metadata = key_value_metadata;
        return this;
    }
    
    public void unsetKey_value_metadata() {
        this.key_value_metadata = null;
    }
    
    public boolean isSetKey_value_metadata() {
        return this.key_value_metadata != null;
    }
    
    public void setKey_value_metadataIsSet(final boolean value) {
        if (!value) {
            this.key_value_metadata = null;
        }
    }
    
    public String getCreated_by() {
        return this.created_by;
    }
    
    public FileMetaData setCreated_by(final String created_by) {
        this.created_by = created_by;
        return this;
    }
    
    public void unsetCreated_by() {
        this.created_by = null;
    }
    
    public boolean isSetCreated_by() {
        return this.created_by != null;
    }
    
    public void setCreated_byIsSet(final boolean value) {
        if (!value) {
            this.created_by = null;
        }
    }
    
    @Override
    public void setFieldValue(final _Fields field, final Object value) {
        switch (field) {
            case VERSION: {
                if (value == null) {
                    this.unsetVersion();
                    break;
                }
                this.setVersion((int)value);
                break;
            }
            case SCHEMA: {
                if (value == null) {
                    this.unsetSchema();
                    break;
                }
                this.setSchema((List<SchemaElement>)value);
                break;
            }
            case NUM_ROWS: {
                if (value == null) {
                    this.unsetNum_rows();
                    break;
                }
                this.setNum_rows((long)value);
                break;
            }
            case ROW_GROUPS: {
                if (value == null) {
                    this.unsetRow_groups();
                    break;
                }
                this.setRow_groups((List<RowGroup>)value);
                break;
            }
            case KEY_VALUE_METADATA: {
                if (value == null) {
                    this.unsetKey_value_metadata();
                    break;
                }
                this.setKey_value_metadata((List<KeyValue>)value);
                break;
            }
            case CREATED_BY: {
                if (value == null) {
                    this.unsetCreated_by();
                    break;
                }
                this.setCreated_by((String)value);
                break;
            }
        }
    }
    
    @Override
    public Object getFieldValue(final _Fields field) {
        switch (field) {
            case VERSION: {
                return new Integer(this.getVersion());
            }
            case SCHEMA: {
                return this.getSchema();
            }
            case NUM_ROWS: {
                return new Long(this.getNum_rows());
            }
            case ROW_GROUPS: {
                return this.getRow_groups();
            }
            case KEY_VALUE_METADATA: {
                return this.getKey_value_metadata();
            }
            case CREATED_BY: {
                return this.getCreated_by();
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
            case VERSION: {
                return this.isSetVersion();
            }
            case SCHEMA: {
                return this.isSetSchema();
            }
            case NUM_ROWS: {
                return this.isSetNum_rows();
            }
            case ROW_GROUPS: {
                return this.isSetRow_groups();
            }
            case KEY_VALUE_METADATA: {
                return this.isSetKey_value_metadata();
            }
            case CREATED_BY: {
                return this.isSetCreated_by();
            }
            default: {
                throw new IllegalStateException();
            }
        }
    }
    
    @Override
    public boolean equals(final Object that) {
        return that != null && that instanceof FileMetaData && this.equals((FileMetaData)that);
    }
    
    public boolean equals(final FileMetaData that) {
        if (that == null) {
            return false;
        }
        final boolean this_present_version = true;
        final boolean that_present_version = true;
        if (this_present_version || that_present_version) {
            if (!this_present_version || !that_present_version) {
                return false;
            }
            if (this.version != that.version) {
                return false;
            }
        }
        final boolean this_present_schema = this.isSetSchema();
        final boolean that_present_schema = that.isSetSchema();
        if (this_present_schema || that_present_schema) {
            if (!this_present_schema || !that_present_schema) {
                return false;
            }
            if (!this.schema.equals(that.schema)) {
                return false;
            }
        }
        final boolean this_present_num_rows = true;
        final boolean that_present_num_rows = true;
        if (this_present_num_rows || that_present_num_rows) {
            if (!this_present_num_rows || !that_present_num_rows) {
                return false;
            }
            if (this.num_rows != that.num_rows) {
                return false;
            }
        }
        final boolean this_present_row_groups = this.isSetRow_groups();
        final boolean that_present_row_groups = that.isSetRow_groups();
        if (this_present_row_groups || that_present_row_groups) {
            if (!this_present_row_groups || !that_present_row_groups) {
                return false;
            }
            if (!this.row_groups.equals(that.row_groups)) {
                return false;
            }
        }
        final boolean this_present_key_value_metadata = this.isSetKey_value_metadata();
        final boolean that_present_key_value_metadata = that.isSetKey_value_metadata();
        if (this_present_key_value_metadata || that_present_key_value_metadata) {
            if (!this_present_key_value_metadata || !that_present_key_value_metadata) {
                return false;
            }
            if (!this.key_value_metadata.equals(that.key_value_metadata)) {
                return false;
            }
        }
        final boolean this_present_created_by = this.isSetCreated_by();
        final boolean that_present_created_by = that.isSetCreated_by();
        if (this_present_created_by || that_present_created_by) {
            if (!this_present_created_by || !that_present_created_by) {
                return false;
            }
            if (!this.created_by.equals(that.created_by)) {
                return false;
            }
        }
        return true;
    }
    
    @Override
    public int hashCode() {
        final HashCodeBuilder builder = new HashCodeBuilder();
        final boolean present_version = true;
        builder.append(present_version);
        if (present_version) {
            builder.append(this.version);
        }
        final boolean present_schema = this.isSetSchema();
        builder.append(present_schema);
        if (present_schema) {
            builder.append(this.schema);
        }
        final boolean present_num_rows = true;
        builder.append(present_num_rows);
        if (present_num_rows) {
            builder.append(this.num_rows);
        }
        final boolean present_row_groups = this.isSetRow_groups();
        builder.append(present_row_groups);
        if (present_row_groups) {
            builder.append(this.row_groups);
        }
        final boolean present_key_value_metadata = this.isSetKey_value_metadata();
        builder.append(present_key_value_metadata);
        if (present_key_value_metadata) {
            builder.append(this.key_value_metadata);
        }
        final boolean present_created_by = this.isSetCreated_by();
        builder.append(present_created_by);
        if (present_created_by) {
            builder.append(this.created_by);
        }
        return builder.toHashCode();
    }
    
    @Override
    public int compareTo(final FileMetaData other) {
        if (!this.getClass().equals(other.getClass())) {
            return this.getClass().getName().compareTo(other.getClass().getName());
        }
        int lastComparison = 0;
        final FileMetaData typedOther = other;
        lastComparison = Boolean.valueOf(this.isSetVersion()).compareTo(Boolean.valueOf(typedOther.isSetVersion()));
        if (lastComparison != 0) {
            return lastComparison;
        }
        if (this.isSetVersion()) {
            lastComparison = TBaseHelper.compareTo(this.version, typedOther.version);
            if (lastComparison != 0) {
                return lastComparison;
            }
        }
        lastComparison = Boolean.valueOf(this.isSetSchema()).compareTo(Boolean.valueOf(typedOther.isSetSchema()));
        if (lastComparison != 0) {
            return lastComparison;
        }
        if (this.isSetSchema()) {
            lastComparison = TBaseHelper.compareTo(this.schema, typedOther.schema);
            if (lastComparison != 0) {
                return lastComparison;
            }
        }
        lastComparison = Boolean.valueOf(this.isSetNum_rows()).compareTo(Boolean.valueOf(typedOther.isSetNum_rows()));
        if (lastComparison != 0) {
            return lastComparison;
        }
        if (this.isSetNum_rows()) {
            lastComparison = TBaseHelper.compareTo(this.num_rows, typedOther.num_rows);
            if (lastComparison != 0) {
                return lastComparison;
            }
        }
        lastComparison = Boolean.valueOf(this.isSetRow_groups()).compareTo(Boolean.valueOf(typedOther.isSetRow_groups()));
        if (lastComparison != 0) {
            return lastComparison;
        }
        if (this.isSetRow_groups()) {
            lastComparison = TBaseHelper.compareTo(this.row_groups, typedOther.row_groups);
            if (lastComparison != 0) {
                return lastComparison;
            }
        }
        lastComparison = Boolean.valueOf(this.isSetKey_value_metadata()).compareTo(Boolean.valueOf(typedOther.isSetKey_value_metadata()));
        if (lastComparison != 0) {
            return lastComparison;
        }
        if (this.isSetKey_value_metadata()) {
            lastComparison = TBaseHelper.compareTo(this.key_value_metadata, typedOther.key_value_metadata);
            if (lastComparison != 0) {
                return lastComparison;
            }
        }
        lastComparison = Boolean.valueOf(this.isSetCreated_by()).compareTo(Boolean.valueOf(typedOther.isSetCreated_by()));
        if (lastComparison != 0) {
            return lastComparison;
        }
        if (this.isSetCreated_by()) {
            lastComparison = TBaseHelper.compareTo(this.created_by, typedOther.created_by);
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
        iprot.readStructBegin();
        while (true) {
            final TField field = iprot.readFieldBegin();
            if (field.type == 0) {
                break;
            }
            switch (field.id) {
                case 1: {
                    if (field.type == 8) {
                        this.version = iprot.readI32();
                        this.setVersionIsSet(true);
                        break;
                    }
                    TProtocolUtil.skip(iprot, field.type);
                    break;
                }
                case 2: {
                    if (field.type == 15) {
                        final TList _list20 = iprot.readListBegin();
                        this.schema = new ArrayList<SchemaElement>(_list20.size);
                        for (int _i21 = 0; _i21 < _list20.size; ++_i21) {
                            final SchemaElement _elem22 = new SchemaElement();
                            _elem22.read(iprot);
                            this.schema.add(_elem22);
                        }
                        iprot.readListEnd();
                        break;
                    }
                    TProtocolUtil.skip(iprot, field.type);
                    break;
                }
                case 3: {
                    if (field.type == 10) {
                        this.num_rows = iprot.readI64();
                        this.setNum_rowsIsSet(true);
                        break;
                    }
                    TProtocolUtil.skip(iprot, field.type);
                    break;
                }
                case 4: {
                    if (field.type == 15) {
                        final TList _list21 = iprot.readListBegin();
                        this.row_groups = new ArrayList<RowGroup>(_list21.size);
                        for (int _i22 = 0; _i22 < _list21.size; ++_i22) {
                            final RowGroup _elem23 = new RowGroup();
                            _elem23.read(iprot);
                            this.row_groups.add(_elem23);
                        }
                        iprot.readListEnd();
                        break;
                    }
                    TProtocolUtil.skip(iprot, field.type);
                    break;
                }
                case 5: {
                    if (field.type == 15) {
                        final TList _list22 = iprot.readListBegin();
                        this.key_value_metadata = new ArrayList<KeyValue>(_list22.size);
                        for (int _i23 = 0; _i23 < _list22.size; ++_i23) {
                            final KeyValue _elem24 = new KeyValue();
                            _elem24.read(iprot);
                            this.key_value_metadata.add(_elem24);
                        }
                        iprot.readListEnd();
                        break;
                    }
                    TProtocolUtil.skip(iprot, field.type);
                    break;
                }
                case 6: {
                    if (field.type == 11) {
                        this.created_by = iprot.readString();
                        break;
                    }
                    TProtocolUtil.skip(iprot, field.type);
                    break;
                }
                default: {
                    TProtocolUtil.skip(iprot, field.type);
                    break;
                }
            }
            iprot.readFieldEnd();
        }
        iprot.readStructEnd();
        if (!this.isSetVersion()) {
            throw new TProtocolException("Required field 'version' was not found in serialized data! Struct: " + this.toString());
        }
        if (!this.isSetNum_rows()) {
            throw new TProtocolException("Required field 'num_rows' was not found in serialized data! Struct: " + this.toString());
        }
        this.validate();
    }
    
    @Override
    public void write(final TProtocol oprot) throws TException {
        this.validate();
        oprot.writeStructBegin(FileMetaData.STRUCT_DESC);
        oprot.writeFieldBegin(FileMetaData.VERSION_FIELD_DESC);
        oprot.writeI32(this.version);
        oprot.writeFieldEnd();
        if (this.schema != null) {
            oprot.writeFieldBegin(FileMetaData.SCHEMA_FIELD_DESC);
            oprot.writeListBegin(new TList((byte)12, this.schema.size()));
            for (final SchemaElement _iter29 : this.schema) {
                _iter29.write(oprot);
            }
            oprot.writeListEnd();
            oprot.writeFieldEnd();
        }
        oprot.writeFieldBegin(FileMetaData.NUM_ROWS_FIELD_DESC);
        oprot.writeI64(this.num_rows);
        oprot.writeFieldEnd();
        if (this.row_groups != null) {
            oprot.writeFieldBegin(FileMetaData.ROW_GROUPS_FIELD_DESC);
            oprot.writeListBegin(new TList((byte)12, this.row_groups.size()));
            for (final RowGroup _iter30 : this.row_groups) {
                _iter30.write(oprot);
            }
            oprot.writeListEnd();
            oprot.writeFieldEnd();
        }
        if (this.key_value_metadata != null && this.isSetKey_value_metadata()) {
            oprot.writeFieldBegin(FileMetaData.KEY_VALUE_METADATA_FIELD_DESC);
            oprot.writeListBegin(new TList((byte)12, this.key_value_metadata.size()));
            for (final KeyValue _iter31 : this.key_value_metadata) {
                _iter31.write(oprot);
            }
            oprot.writeListEnd();
            oprot.writeFieldEnd();
        }
        if (this.created_by != null && this.isSetCreated_by()) {
            oprot.writeFieldBegin(FileMetaData.CREATED_BY_FIELD_DESC);
            oprot.writeString(this.created_by);
            oprot.writeFieldEnd();
        }
        oprot.writeFieldStop();
        oprot.writeStructEnd();
    }
    
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("FileMetaData(");
        boolean first = true;
        sb.append("version:");
        sb.append(this.version);
        first = false;
        if (!first) {
            sb.append(", ");
        }
        sb.append("schema:");
        if (this.schema == null) {
            sb.append("null");
        }
        else {
            sb.append(this.schema);
        }
        first = false;
        if (!first) {
            sb.append(", ");
        }
        sb.append("num_rows:");
        sb.append(this.num_rows);
        first = false;
        if (!first) {
            sb.append(", ");
        }
        sb.append("row_groups:");
        if (this.row_groups == null) {
            sb.append("null");
        }
        else {
            sb.append(this.row_groups);
        }
        first = false;
        if (this.isSetKey_value_metadata()) {
            if (!first) {
                sb.append(", ");
            }
            sb.append("key_value_metadata:");
            if (this.key_value_metadata == null) {
                sb.append("null");
            }
            else {
                sb.append(this.key_value_metadata);
            }
            first = false;
        }
        if (this.isSetCreated_by()) {
            if (!first) {
                sb.append(", ");
            }
            sb.append("created_by:");
            if (this.created_by == null) {
                sb.append("null");
            }
            else {
                sb.append(this.created_by);
            }
            first = false;
        }
        sb.append(")");
        return sb.toString();
    }
    
    public void validate() throws TException {
        if (this.schema == null) {
            throw new TProtocolException("Required field 'schema' was not present! Struct: " + this.toString());
        }
        if (this.row_groups == null) {
            throw new TProtocolException("Required field 'row_groups' was not present! Struct: " + this.toString());
        }
    }
    
    static {
        STRUCT_DESC = new TStruct("FileMetaData");
        VERSION_FIELD_DESC = new TField("version", (byte)8, (short)1);
        SCHEMA_FIELD_DESC = new TField("schema", (byte)15, (short)2);
        NUM_ROWS_FIELD_DESC = new TField("num_rows", (byte)10, (short)3);
        ROW_GROUPS_FIELD_DESC = new TField("row_groups", (byte)15, (short)4);
        KEY_VALUE_METADATA_FIELD_DESC = new TField("key_value_metadata", (byte)15, (short)5);
        CREATED_BY_FIELD_DESC = new TField("created_by", (byte)11, (short)6);
        final Map<_Fields, FieldMetaData> tmpMap = new EnumMap<_Fields, FieldMetaData>(_Fields.class);
        tmpMap.put(_Fields.VERSION, new FieldMetaData("version", (byte)1, new FieldValueMetaData((byte)8)));
        tmpMap.put(_Fields.SCHEMA, new FieldMetaData("schema", (byte)1, new ListMetaData((byte)15, new StructMetaData((byte)12, SchemaElement.class))));
        tmpMap.put(_Fields.NUM_ROWS, new FieldMetaData("num_rows", (byte)1, new FieldValueMetaData((byte)10)));
        tmpMap.put(_Fields.ROW_GROUPS, new FieldMetaData("row_groups", (byte)1, new ListMetaData((byte)15, new StructMetaData((byte)12, RowGroup.class))));
        tmpMap.put(_Fields.KEY_VALUE_METADATA, new FieldMetaData("key_value_metadata", (byte)2, new ListMetaData((byte)15, new StructMetaData((byte)12, KeyValue.class))));
        tmpMap.put(_Fields.CREATED_BY, new FieldMetaData("created_by", (byte)2, new FieldValueMetaData((byte)11)));
        FieldMetaData.addStructMetaDataMap(FileMetaData.class, metaDataMap = Collections.unmodifiableMap((Map<? extends _Fields, ? extends FieldMetaData>)tmpMap));
    }
    
    public enum _Fields implements TFieldIdEnum
    {
        VERSION((short)1, "version"), 
        SCHEMA((short)2, "schema"), 
        NUM_ROWS((short)3, "num_rows"), 
        ROW_GROUPS((short)4, "row_groups"), 
        KEY_VALUE_METADATA((short)5, "key_value_metadata"), 
        CREATED_BY((short)6, "created_by");
        
        private static final Map<String, _Fields> byName;
        private final short _thriftId;
        private final String _fieldName;
        
        public static _Fields findByThriftId(final int fieldId) {
            switch (fieldId) {
                case 1: {
                    return _Fields.VERSION;
                }
                case 2: {
                    return _Fields.SCHEMA;
                }
                case 3: {
                    return _Fields.NUM_ROWS;
                }
                case 4: {
                    return _Fields.ROW_GROUPS;
                }
                case 5: {
                    return _Fields.KEY_VALUE_METADATA;
                }
                case 6: {
                    return _Fields.CREATED_BY;
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
}
