// 
// Decompiled by Procyon v0.5.36
// 

package parquet.format;

import java.util.Iterator;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Collections;
import parquet.org.apache.thrift.meta_data.StructMetaData;
import parquet.org.apache.thrift.meta_data.FieldValueMetaData;
import java.util.EnumMap;
import parquet.org.apache.thrift.TFieldIdEnum;
import parquet.org.apache.thrift.TException;
import parquet.org.apache.thrift.protocol.TProtocolException;
import parquet.org.apache.thrift.protocol.TProtocolUtil;
import parquet.org.apache.thrift.protocol.TProtocol;
import parquet.org.apache.thrift.TBaseHelper;
import org.apache.commons.lang.builder.HashCodeBuilder;
import parquet.org.apache.thrift.meta_data.FieldMetaData;
import java.util.Map;
import java.util.BitSet;
import parquet.org.apache.thrift.protocol.TField;
import parquet.org.apache.thrift.protocol.TStruct;
import java.io.Serializable;
import parquet.org.apache.thrift.TBase;

public class ColumnChunk implements TBase<ColumnChunk, _Fields>, Serializable, Cloneable
{
    private static final TStruct STRUCT_DESC;
    private static final TField FILE_PATH_FIELD_DESC;
    private static final TField FILE_OFFSET_FIELD_DESC;
    private static final TField META_DATA_FIELD_DESC;
    public String file_path;
    public long file_offset;
    public ColumnMetaData meta_data;
    private static final int __FILE_OFFSET_ISSET_ID = 0;
    private BitSet __isset_bit_vector;
    public static final Map<_Fields, FieldMetaData> metaDataMap;
    
    public ColumnChunk() {
        this.__isset_bit_vector = new BitSet(1);
    }
    
    public ColumnChunk(final long file_offset) {
        this();
        this.file_offset = file_offset;
        this.setFile_offsetIsSet(true);
    }
    
    public ColumnChunk(final ColumnChunk other) {
        (this.__isset_bit_vector = new BitSet(1)).clear();
        this.__isset_bit_vector.or(other.__isset_bit_vector);
        if (other.isSetFile_path()) {
            this.file_path = other.file_path;
        }
        this.file_offset = other.file_offset;
        if (other.isSetMeta_data()) {
            this.meta_data = new ColumnMetaData(other.meta_data);
        }
    }
    
    @Override
    public ColumnChunk deepCopy() {
        return new ColumnChunk(this);
    }
    
    @Override
    public void clear() {
        this.file_path = null;
        this.setFile_offsetIsSet(false);
        this.file_offset = 0L;
        this.meta_data = null;
    }
    
    public String getFile_path() {
        return this.file_path;
    }
    
    public ColumnChunk setFile_path(final String file_path) {
        this.file_path = file_path;
        return this;
    }
    
    public void unsetFile_path() {
        this.file_path = null;
    }
    
    public boolean isSetFile_path() {
        return this.file_path != null;
    }
    
    public void setFile_pathIsSet(final boolean value) {
        if (!value) {
            this.file_path = null;
        }
    }
    
    public long getFile_offset() {
        return this.file_offset;
    }
    
    public ColumnChunk setFile_offset(final long file_offset) {
        this.file_offset = file_offset;
        this.setFile_offsetIsSet(true);
        return this;
    }
    
    public void unsetFile_offset() {
        this.__isset_bit_vector.clear(0);
    }
    
    public boolean isSetFile_offset() {
        return this.__isset_bit_vector.get(0);
    }
    
    public void setFile_offsetIsSet(final boolean value) {
        this.__isset_bit_vector.set(0, value);
    }
    
    public ColumnMetaData getMeta_data() {
        return this.meta_data;
    }
    
    public ColumnChunk setMeta_data(final ColumnMetaData meta_data) {
        this.meta_data = meta_data;
        return this;
    }
    
    public void unsetMeta_data() {
        this.meta_data = null;
    }
    
    public boolean isSetMeta_data() {
        return this.meta_data != null;
    }
    
    public void setMeta_dataIsSet(final boolean value) {
        if (!value) {
            this.meta_data = null;
        }
    }
    
    @Override
    public void setFieldValue(final _Fields field, final Object value) {
        switch (field) {
            case FILE_PATH: {
                if (value == null) {
                    this.unsetFile_path();
                    break;
                }
                this.setFile_path((String)value);
                break;
            }
            case FILE_OFFSET: {
                if (value == null) {
                    this.unsetFile_offset();
                    break;
                }
                this.setFile_offset((long)value);
                break;
            }
            case META_DATA: {
                if (value == null) {
                    this.unsetMeta_data();
                    break;
                }
                this.setMeta_data((ColumnMetaData)value);
                break;
            }
        }
    }
    
    @Override
    public Object getFieldValue(final _Fields field) {
        switch (field) {
            case FILE_PATH: {
                return this.getFile_path();
            }
            case FILE_OFFSET: {
                return new Long(this.getFile_offset());
            }
            case META_DATA: {
                return this.getMeta_data();
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
            case FILE_PATH: {
                return this.isSetFile_path();
            }
            case FILE_OFFSET: {
                return this.isSetFile_offset();
            }
            case META_DATA: {
                return this.isSetMeta_data();
            }
            default: {
                throw new IllegalStateException();
            }
        }
    }
    
    @Override
    public boolean equals(final Object that) {
        return that != null && that instanceof ColumnChunk && this.equals((ColumnChunk)that);
    }
    
    public boolean equals(final ColumnChunk that) {
        if (that == null) {
            return false;
        }
        final boolean this_present_file_path = this.isSetFile_path();
        final boolean that_present_file_path = that.isSetFile_path();
        if (this_present_file_path || that_present_file_path) {
            if (!this_present_file_path || !that_present_file_path) {
                return false;
            }
            if (!this.file_path.equals(that.file_path)) {
                return false;
            }
        }
        final boolean this_present_file_offset = true;
        final boolean that_present_file_offset = true;
        if (this_present_file_offset || that_present_file_offset) {
            if (!this_present_file_offset || !that_present_file_offset) {
                return false;
            }
            if (this.file_offset != that.file_offset) {
                return false;
            }
        }
        final boolean this_present_meta_data = this.isSetMeta_data();
        final boolean that_present_meta_data = that.isSetMeta_data();
        if (this_present_meta_data || that_present_meta_data) {
            if (!this_present_meta_data || !that_present_meta_data) {
                return false;
            }
            if (!this.meta_data.equals(that.meta_data)) {
                return false;
            }
        }
        return true;
    }
    
    @Override
    public int hashCode() {
        final HashCodeBuilder builder = new HashCodeBuilder();
        final boolean present_file_path = this.isSetFile_path();
        builder.append(present_file_path);
        if (present_file_path) {
            builder.append(this.file_path);
        }
        final boolean present_file_offset = true;
        builder.append(present_file_offset);
        if (present_file_offset) {
            builder.append(this.file_offset);
        }
        final boolean present_meta_data = this.isSetMeta_data();
        builder.append(present_meta_data);
        if (present_meta_data) {
            builder.append(this.meta_data);
        }
        return builder.toHashCode();
    }
    
    @Override
    public int compareTo(final ColumnChunk other) {
        if (!this.getClass().equals(other.getClass())) {
            return this.getClass().getName().compareTo(other.getClass().getName());
        }
        int lastComparison = 0;
        final ColumnChunk typedOther = other;
        lastComparison = Boolean.valueOf(this.isSetFile_path()).compareTo(Boolean.valueOf(typedOther.isSetFile_path()));
        if (lastComparison != 0) {
            return lastComparison;
        }
        if (this.isSetFile_path()) {
            lastComparison = TBaseHelper.compareTo(this.file_path, typedOther.file_path);
            if (lastComparison != 0) {
                return lastComparison;
            }
        }
        lastComparison = Boolean.valueOf(this.isSetFile_offset()).compareTo(Boolean.valueOf(typedOther.isSetFile_offset()));
        if (lastComparison != 0) {
            return lastComparison;
        }
        if (this.isSetFile_offset()) {
            lastComparison = TBaseHelper.compareTo(this.file_offset, typedOther.file_offset);
            if (lastComparison != 0) {
                return lastComparison;
            }
        }
        lastComparison = Boolean.valueOf(this.isSetMeta_data()).compareTo(Boolean.valueOf(typedOther.isSetMeta_data()));
        if (lastComparison != 0) {
            return lastComparison;
        }
        if (this.isSetMeta_data()) {
            lastComparison = TBaseHelper.compareTo(this.meta_data, typedOther.meta_data);
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
                    if (field.type == 11) {
                        this.file_path = iprot.readString();
                        break;
                    }
                    TProtocolUtil.skip(iprot, field.type);
                    break;
                }
                case 2: {
                    if (field.type == 10) {
                        this.file_offset = iprot.readI64();
                        this.setFile_offsetIsSet(true);
                        break;
                    }
                    TProtocolUtil.skip(iprot, field.type);
                    break;
                }
                case 3: {
                    if (field.type == 12) {
                        (this.meta_data = new ColumnMetaData()).read(iprot);
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
        if (!this.isSetFile_offset()) {
            throw new TProtocolException("Required field 'file_offset' was not found in serialized data! Struct: " + this.toString());
        }
        this.validate();
    }
    
    @Override
    public void write(final TProtocol oprot) throws TException {
        this.validate();
        oprot.writeStructBegin(ColumnChunk.STRUCT_DESC);
        if (this.file_path != null && this.isSetFile_path()) {
            oprot.writeFieldBegin(ColumnChunk.FILE_PATH_FIELD_DESC);
            oprot.writeString(this.file_path);
            oprot.writeFieldEnd();
        }
        oprot.writeFieldBegin(ColumnChunk.FILE_OFFSET_FIELD_DESC);
        oprot.writeI64(this.file_offset);
        oprot.writeFieldEnd();
        if (this.meta_data != null && this.isSetMeta_data()) {
            oprot.writeFieldBegin(ColumnChunk.META_DATA_FIELD_DESC);
            this.meta_data.write(oprot);
            oprot.writeFieldEnd();
        }
        oprot.writeFieldStop();
        oprot.writeStructEnd();
    }
    
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("ColumnChunk(");
        boolean first = true;
        if (this.isSetFile_path()) {
            sb.append("file_path:");
            if (this.file_path == null) {
                sb.append("null");
            }
            else {
                sb.append(this.file_path);
            }
            first = false;
        }
        if (!first) {
            sb.append(", ");
        }
        sb.append("file_offset:");
        sb.append(this.file_offset);
        first = false;
        if (this.isSetMeta_data()) {
            if (!first) {
                sb.append(", ");
            }
            sb.append("meta_data:");
            if (this.meta_data == null) {
                sb.append("null");
            }
            else {
                sb.append(this.meta_data);
            }
            first = false;
        }
        sb.append(")");
        return sb.toString();
    }
    
    public void validate() throws TException {
    }
    
    static {
        STRUCT_DESC = new TStruct("ColumnChunk");
        FILE_PATH_FIELD_DESC = new TField("file_path", (byte)11, (short)1);
        FILE_OFFSET_FIELD_DESC = new TField("file_offset", (byte)10, (short)2);
        META_DATA_FIELD_DESC = new TField("meta_data", (byte)12, (short)3);
        final Map<_Fields, FieldMetaData> tmpMap = new EnumMap<_Fields, FieldMetaData>(_Fields.class);
        tmpMap.put(_Fields.FILE_PATH, new FieldMetaData("file_path", (byte)2, new FieldValueMetaData((byte)11)));
        tmpMap.put(_Fields.FILE_OFFSET, new FieldMetaData("file_offset", (byte)1, new FieldValueMetaData((byte)10)));
        tmpMap.put(_Fields.META_DATA, new FieldMetaData("meta_data", (byte)2, new StructMetaData((byte)12, ColumnMetaData.class)));
        FieldMetaData.addStructMetaDataMap(ColumnChunk.class, metaDataMap = Collections.unmodifiableMap((Map<? extends _Fields, ? extends FieldMetaData>)tmpMap));
    }
    
    public enum _Fields implements TFieldIdEnum
    {
        FILE_PATH((short)1, "file_path"), 
        FILE_OFFSET((short)2, "file_offset"), 
        META_DATA((short)3, "meta_data");
        
        private static final Map<String, _Fields> byName;
        private final short _thriftId;
        private final String _fieldName;
        
        public static _Fields findByThriftId(final int fieldId) {
            switch (fieldId) {
                case 1: {
                    return _Fields.FILE_PATH;
                }
                case 2: {
                    return _Fields.FILE_OFFSET;
                }
                case 3: {
                    return _Fields.META_DATA;
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
