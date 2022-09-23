// 
// Decompiled by Procyon v0.5.36
// 

package parquet.format;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Collections;
import parquet.org.apache.thrift.meta_data.StructMetaData;
import parquet.org.apache.thrift.meta_data.ListMetaData;
import parquet.org.apache.thrift.meta_data.FieldValueMetaData;
import parquet.org.apache.thrift.TEnum;
import parquet.org.apache.thrift.meta_data.EnumMetaData;
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

public class ColumnMetaData implements TBase<ColumnMetaData, _Fields>, Serializable, Cloneable
{
    private static final TStruct STRUCT_DESC;
    private static final TField TYPE_FIELD_DESC;
    private static final TField ENCODINGS_FIELD_DESC;
    private static final TField PATH_IN_SCHEMA_FIELD_DESC;
    private static final TField CODEC_FIELD_DESC;
    private static final TField NUM_VALUES_FIELD_DESC;
    private static final TField TOTAL_UNCOMPRESSED_SIZE_FIELD_DESC;
    private static final TField TOTAL_COMPRESSED_SIZE_FIELD_DESC;
    private static final TField KEY_VALUE_METADATA_FIELD_DESC;
    private static final TField DATA_PAGE_OFFSET_FIELD_DESC;
    private static final TField INDEX_PAGE_OFFSET_FIELD_DESC;
    private static final TField DICTIONARY_PAGE_OFFSET_FIELD_DESC;
    private static final TField STATISTICS_FIELD_DESC;
    public Type type;
    public List<Encoding> encodings;
    public List<String> path_in_schema;
    public CompressionCodec codec;
    public long num_values;
    public long total_uncompressed_size;
    public long total_compressed_size;
    public List<KeyValue> key_value_metadata;
    public long data_page_offset;
    public long index_page_offset;
    public long dictionary_page_offset;
    public Statistics statistics;
    private static final int __NUM_VALUES_ISSET_ID = 0;
    private static final int __TOTAL_UNCOMPRESSED_SIZE_ISSET_ID = 1;
    private static final int __TOTAL_COMPRESSED_SIZE_ISSET_ID = 2;
    private static final int __DATA_PAGE_OFFSET_ISSET_ID = 3;
    private static final int __INDEX_PAGE_OFFSET_ISSET_ID = 4;
    private static final int __DICTIONARY_PAGE_OFFSET_ISSET_ID = 5;
    private BitSet __isset_bit_vector;
    public static final Map<_Fields, FieldMetaData> metaDataMap;
    
    public ColumnMetaData() {
        this.__isset_bit_vector = new BitSet(6);
    }
    
    public ColumnMetaData(final Type type, final List<Encoding> encodings, final List<String> path_in_schema, final CompressionCodec codec, final long num_values, final long total_uncompressed_size, final long total_compressed_size, final long data_page_offset) {
        this();
        this.type = type;
        this.encodings = encodings;
        this.path_in_schema = path_in_schema;
        this.codec = codec;
        this.num_values = num_values;
        this.setNum_valuesIsSet(true);
        this.total_uncompressed_size = total_uncompressed_size;
        this.setTotal_uncompressed_sizeIsSet(true);
        this.total_compressed_size = total_compressed_size;
        this.setTotal_compressed_sizeIsSet(true);
        this.data_page_offset = data_page_offset;
        this.setData_page_offsetIsSet(true);
    }
    
    public ColumnMetaData(final ColumnMetaData other) {
        (this.__isset_bit_vector = new BitSet(6)).clear();
        this.__isset_bit_vector.or(other.__isset_bit_vector);
        if (other.isSetType()) {
            this.type = other.type;
        }
        if (other.isSetEncodings()) {
            final List<Encoding> __this__encodings = new ArrayList<Encoding>();
            for (final Encoding other_element : other.encodings) {
                __this__encodings.add(other_element);
            }
            this.encodings = __this__encodings;
        }
        if (other.isSetPath_in_schema()) {
            final List<String> __this__path_in_schema = new ArrayList<String>();
            for (final String other_element2 : other.path_in_schema) {
                __this__path_in_schema.add(other_element2);
            }
            this.path_in_schema = __this__path_in_schema;
        }
        if (other.isSetCodec()) {
            this.codec = other.codec;
        }
        this.num_values = other.num_values;
        this.total_uncompressed_size = other.total_uncompressed_size;
        this.total_compressed_size = other.total_compressed_size;
        if (other.isSetKey_value_metadata()) {
            final List<KeyValue> __this__key_value_metadata = new ArrayList<KeyValue>();
            for (final KeyValue other_element3 : other.key_value_metadata) {
                __this__key_value_metadata.add(new KeyValue(other_element3));
            }
            this.key_value_metadata = __this__key_value_metadata;
        }
        this.data_page_offset = other.data_page_offset;
        this.index_page_offset = other.index_page_offset;
        this.dictionary_page_offset = other.dictionary_page_offset;
        if (other.isSetStatistics()) {
            this.statistics = new Statistics(other.statistics);
        }
    }
    
    @Override
    public ColumnMetaData deepCopy() {
        return new ColumnMetaData(this);
    }
    
    @Override
    public void clear() {
        this.type = null;
        this.encodings = null;
        this.path_in_schema = null;
        this.codec = null;
        this.setNum_valuesIsSet(false);
        this.num_values = 0L;
        this.setTotal_uncompressed_sizeIsSet(false);
        this.total_uncompressed_size = 0L;
        this.setTotal_compressed_sizeIsSet(false);
        this.total_compressed_size = 0L;
        this.key_value_metadata = null;
        this.setData_page_offsetIsSet(false);
        this.data_page_offset = 0L;
        this.setIndex_page_offsetIsSet(false);
        this.index_page_offset = 0L;
        this.setDictionary_page_offsetIsSet(false);
        this.dictionary_page_offset = 0L;
        this.statistics = null;
    }
    
    public Type getType() {
        return this.type;
    }
    
    public ColumnMetaData setType(final Type type) {
        this.type = type;
        return this;
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
    
    public int getEncodingsSize() {
        return (this.encodings == null) ? 0 : this.encodings.size();
    }
    
    public Iterator<Encoding> getEncodingsIterator() {
        return (this.encodings == null) ? null : this.encodings.iterator();
    }
    
    public void addToEncodings(final Encoding elem) {
        if (this.encodings == null) {
            this.encodings = new ArrayList<Encoding>();
        }
        this.encodings.add(elem);
    }
    
    public List<Encoding> getEncodings() {
        return this.encodings;
    }
    
    public ColumnMetaData setEncodings(final List<Encoding> encodings) {
        this.encodings = encodings;
        return this;
    }
    
    public void unsetEncodings() {
        this.encodings = null;
    }
    
    public boolean isSetEncodings() {
        return this.encodings != null;
    }
    
    public void setEncodingsIsSet(final boolean value) {
        if (!value) {
            this.encodings = null;
        }
    }
    
    public int getPath_in_schemaSize() {
        return (this.path_in_schema == null) ? 0 : this.path_in_schema.size();
    }
    
    public Iterator<String> getPath_in_schemaIterator() {
        return (this.path_in_schema == null) ? null : this.path_in_schema.iterator();
    }
    
    public void addToPath_in_schema(final String elem) {
        if (this.path_in_schema == null) {
            this.path_in_schema = new ArrayList<String>();
        }
        this.path_in_schema.add(elem);
    }
    
    public List<String> getPath_in_schema() {
        return this.path_in_schema;
    }
    
    public ColumnMetaData setPath_in_schema(final List<String> path_in_schema) {
        this.path_in_schema = path_in_schema;
        return this;
    }
    
    public void unsetPath_in_schema() {
        this.path_in_schema = null;
    }
    
    public boolean isSetPath_in_schema() {
        return this.path_in_schema != null;
    }
    
    public void setPath_in_schemaIsSet(final boolean value) {
        if (!value) {
            this.path_in_schema = null;
        }
    }
    
    public CompressionCodec getCodec() {
        return this.codec;
    }
    
    public ColumnMetaData setCodec(final CompressionCodec codec) {
        this.codec = codec;
        return this;
    }
    
    public void unsetCodec() {
        this.codec = null;
    }
    
    public boolean isSetCodec() {
        return this.codec != null;
    }
    
    public void setCodecIsSet(final boolean value) {
        if (!value) {
            this.codec = null;
        }
    }
    
    public long getNum_values() {
        return this.num_values;
    }
    
    public ColumnMetaData setNum_values(final long num_values) {
        this.num_values = num_values;
        this.setNum_valuesIsSet(true);
        return this;
    }
    
    public void unsetNum_values() {
        this.__isset_bit_vector.clear(0);
    }
    
    public boolean isSetNum_values() {
        return this.__isset_bit_vector.get(0);
    }
    
    public void setNum_valuesIsSet(final boolean value) {
        this.__isset_bit_vector.set(0, value);
    }
    
    public long getTotal_uncompressed_size() {
        return this.total_uncompressed_size;
    }
    
    public ColumnMetaData setTotal_uncompressed_size(final long total_uncompressed_size) {
        this.total_uncompressed_size = total_uncompressed_size;
        this.setTotal_uncompressed_sizeIsSet(true);
        return this;
    }
    
    public void unsetTotal_uncompressed_size() {
        this.__isset_bit_vector.clear(1);
    }
    
    public boolean isSetTotal_uncompressed_size() {
        return this.__isset_bit_vector.get(1);
    }
    
    public void setTotal_uncompressed_sizeIsSet(final boolean value) {
        this.__isset_bit_vector.set(1, value);
    }
    
    public long getTotal_compressed_size() {
        return this.total_compressed_size;
    }
    
    public ColumnMetaData setTotal_compressed_size(final long total_compressed_size) {
        this.total_compressed_size = total_compressed_size;
        this.setTotal_compressed_sizeIsSet(true);
        return this;
    }
    
    public void unsetTotal_compressed_size() {
        this.__isset_bit_vector.clear(2);
    }
    
    public boolean isSetTotal_compressed_size() {
        return this.__isset_bit_vector.get(2);
    }
    
    public void setTotal_compressed_sizeIsSet(final boolean value) {
        this.__isset_bit_vector.set(2, value);
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
    
    public ColumnMetaData setKey_value_metadata(final List<KeyValue> key_value_metadata) {
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
    
    public long getData_page_offset() {
        return this.data_page_offset;
    }
    
    public ColumnMetaData setData_page_offset(final long data_page_offset) {
        this.data_page_offset = data_page_offset;
        this.setData_page_offsetIsSet(true);
        return this;
    }
    
    public void unsetData_page_offset() {
        this.__isset_bit_vector.clear(3);
    }
    
    public boolean isSetData_page_offset() {
        return this.__isset_bit_vector.get(3);
    }
    
    public void setData_page_offsetIsSet(final boolean value) {
        this.__isset_bit_vector.set(3, value);
    }
    
    public long getIndex_page_offset() {
        return this.index_page_offset;
    }
    
    public ColumnMetaData setIndex_page_offset(final long index_page_offset) {
        this.index_page_offset = index_page_offset;
        this.setIndex_page_offsetIsSet(true);
        return this;
    }
    
    public void unsetIndex_page_offset() {
        this.__isset_bit_vector.clear(4);
    }
    
    public boolean isSetIndex_page_offset() {
        return this.__isset_bit_vector.get(4);
    }
    
    public void setIndex_page_offsetIsSet(final boolean value) {
        this.__isset_bit_vector.set(4, value);
    }
    
    public long getDictionary_page_offset() {
        return this.dictionary_page_offset;
    }
    
    public ColumnMetaData setDictionary_page_offset(final long dictionary_page_offset) {
        this.dictionary_page_offset = dictionary_page_offset;
        this.setDictionary_page_offsetIsSet(true);
        return this;
    }
    
    public void unsetDictionary_page_offset() {
        this.__isset_bit_vector.clear(5);
    }
    
    public boolean isSetDictionary_page_offset() {
        return this.__isset_bit_vector.get(5);
    }
    
    public void setDictionary_page_offsetIsSet(final boolean value) {
        this.__isset_bit_vector.set(5, value);
    }
    
    public Statistics getStatistics() {
        return this.statistics;
    }
    
    public ColumnMetaData setStatistics(final Statistics statistics) {
        this.statistics = statistics;
        return this;
    }
    
    public void unsetStatistics() {
        this.statistics = null;
    }
    
    public boolean isSetStatistics() {
        return this.statistics != null;
    }
    
    public void setStatisticsIsSet(final boolean value) {
        if (!value) {
            this.statistics = null;
        }
    }
    
    @Override
    public void setFieldValue(final _Fields field, final Object value) {
        switch (field) {
            case TYPE: {
                if (value == null) {
                    this.unsetType();
                    break;
                }
                this.setType((Type)value);
                break;
            }
            case ENCODINGS: {
                if (value == null) {
                    this.unsetEncodings();
                    break;
                }
                this.setEncodings((List<Encoding>)value);
                break;
            }
            case PATH_IN_SCHEMA: {
                if (value == null) {
                    this.unsetPath_in_schema();
                    break;
                }
                this.setPath_in_schema((List<String>)value);
                break;
            }
            case CODEC: {
                if (value == null) {
                    this.unsetCodec();
                    break;
                }
                this.setCodec((CompressionCodec)value);
                break;
            }
            case NUM_VALUES: {
                if (value == null) {
                    this.unsetNum_values();
                    break;
                }
                this.setNum_values((long)value);
                break;
            }
            case TOTAL_UNCOMPRESSED_SIZE: {
                if (value == null) {
                    this.unsetTotal_uncompressed_size();
                    break;
                }
                this.setTotal_uncompressed_size((long)value);
                break;
            }
            case TOTAL_COMPRESSED_SIZE: {
                if (value == null) {
                    this.unsetTotal_compressed_size();
                    break;
                }
                this.setTotal_compressed_size((long)value);
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
            case DATA_PAGE_OFFSET: {
                if (value == null) {
                    this.unsetData_page_offset();
                    break;
                }
                this.setData_page_offset((long)value);
                break;
            }
            case INDEX_PAGE_OFFSET: {
                if (value == null) {
                    this.unsetIndex_page_offset();
                    break;
                }
                this.setIndex_page_offset((long)value);
                break;
            }
            case DICTIONARY_PAGE_OFFSET: {
                if (value == null) {
                    this.unsetDictionary_page_offset();
                    break;
                }
                this.setDictionary_page_offset((long)value);
                break;
            }
            case STATISTICS: {
                if (value == null) {
                    this.unsetStatistics();
                    break;
                }
                this.setStatistics((Statistics)value);
                break;
            }
        }
    }
    
    @Override
    public Object getFieldValue(final _Fields field) {
        switch (field) {
            case TYPE: {
                return this.getType();
            }
            case ENCODINGS: {
                return this.getEncodings();
            }
            case PATH_IN_SCHEMA: {
                return this.getPath_in_schema();
            }
            case CODEC: {
                return this.getCodec();
            }
            case NUM_VALUES: {
                return new Long(this.getNum_values());
            }
            case TOTAL_UNCOMPRESSED_SIZE: {
                return new Long(this.getTotal_uncompressed_size());
            }
            case TOTAL_COMPRESSED_SIZE: {
                return new Long(this.getTotal_compressed_size());
            }
            case KEY_VALUE_METADATA: {
                return this.getKey_value_metadata();
            }
            case DATA_PAGE_OFFSET: {
                return new Long(this.getData_page_offset());
            }
            case INDEX_PAGE_OFFSET: {
                return new Long(this.getIndex_page_offset());
            }
            case DICTIONARY_PAGE_OFFSET: {
                return new Long(this.getDictionary_page_offset());
            }
            case STATISTICS: {
                return this.getStatistics();
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
            case TYPE: {
                return this.isSetType();
            }
            case ENCODINGS: {
                return this.isSetEncodings();
            }
            case PATH_IN_SCHEMA: {
                return this.isSetPath_in_schema();
            }
            case CODEC: {
                return this.isSetCodec();
            }
            case NUM_VALUES: {
                return this.isSetNum_values();
            }
            case TOTAL_UNCOMPRESSED_SIZE: {
                return this.isSetTotal_uncompressed_size();
            }
            case TOTAL_COMPRESSED_SIZE: {
                return this.isSetTotal_compressed_size();
            }
            case KEY_VALUE_METADATA: {
                return this.isSetKey_value_metadata();
            }
            case DATA_PAGE_OFFSET: {
                return this.isSetData_page_offset();
            }
            case INDEX_PAGE_OFFSET: {
                return this.isSetIndex_page_offset();
            }
            case DICTIONARY_PAGE_OFFSET: {
                return this.isSetDictionary_page_offset();
            }
            case STATISTICS: {
                return this.isSetStatistics();
            }
            default: {
                throw new IllegalStateException();
            }
        }
    }
    
    @Override
    public boolean equals(final Object that) {
        return that != null && that instanceof ColumnMetaData && this.equals((ColumnMetaData)that);
    }
    
    public boolean equals(final ColumnMetaData that) {
        if (that == null) {
            return false;
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
        final boolean this_present_encodings = this.isSetEncodings();
        final boolean that_present_encodings = that.isSetEncodings();
        if (this_present_encodings || that_present_encodings) {
            if (!this_present_encodings || !that_present_encodings) {
                return false;
            }
            if (!this.encodings.equals(that.encodings)) {
                return false;
            }
        }
        final boolean this_present_path_in_schema = this.isSetPath_in_schema();
        final boolean that_present_path_in_schema = that.isSetPath_in_schema();
        if (this_present_path_in_schema || that_present_path_in_schema) {
            if (!this_present_path_in_schema || !that_present_path_in_schema) {
                return false;
            }
            if (!this.path_in_schema.equals(that.path_in_schema)) {
                return false;
            }
        }
        final boolean this_present_codec = this.isSetCodec();
        final boolean that_present_codec = that.isSetCodec();
        if (this_present_codec || that_present_codec) {
            if (!this_present_codec || !that_present_codec) {
                return false;
            }
            if (!this.codec.equals(that.codec)) {
                return false;
            }
        }
        final boolean this_present_num_values = true;
        final boolean that_present_num_values = true;
        if (this_present_num_values || that_present_num_values) {
            if (!this_present_num_values || !that_present_num_values) {
                return false;
            }
            if (this.num_values != that.num_values) {
                return false;
            }
        }
        final boolean this_present_total_uncompressed_size = true;
        final boolean that_present_total_uncompressed_size = true;
        if (this_present_total_uncompressed_size || that_present_total_uncompressed_size) {
            if (!this_present_total_uncompressed_size || !that_present_total_uncompressed_size) {
                return false;
            }
            if (this.total_uncompressed_size != that.total_uncompressed_size) {
                return false;
            }
        }
        final boolean this_present_total_compressed_size = true;
        final boolean that_present_total_compressed_size = true;
        if (this_present_total_compressed_size || that_present_total_compressed_size) {
            if (!this_present_total_compressed_size || !that_present_total_compressed_size) {
                return false;
            }
            if (this.total_compressed_size != that.total_compressed_size) {
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
        final boolean this_present_data_page_offset = true;
        final boolean that_present_data_page_offset = true;
        if (this_present_data_page_offset || that_present_data_page_offset) {
            if (!this_present_data_page_offset || !that_present_data_page_offset) {
                return false;
            }
            if (this.data_page_offset != that.data_page_offset) {
                return false;
            }
        }
        final boolean this_present_index_page_offset = this.isSetIndex_page_offset();
        final boolean that_present_index_page_offset = that.isSetIndex_page_offset();
        if (this_present_index_page_offset || that_present_index_page_offset) {
            if (!this_present_index_page_offset || !that_present_index_page_offset) {
                return false;
            }
            if (this.index_page_offset != that.index_page_offset) {
                return false;
            }
        }
        final boolean this_present_dictionary_page_offset = this.isSetDictionary_page_offset();
        final boolean that_present_dictionary_page_offset = that.isSetDictionary_page_offset();
        if (this_present_dictionary_page_offset || that_present_dictionary_page_offset) {
            if (!this_present_dictionary_page_offset || !that_present_dictionary_page_offset) {
                return false;
            }
            if (this.dictionary_page_offset != that.dictionary_page_offset) {
                return false;
            }
        }
        final boolean this_present_statistics = this.isSetStatistics();
        final boolean that_present_statistics = that.isSetStatistics();
        if (this_present_statistics || that_present_statistics) {
            if (!this_present_statistics || !that_present_statistics) {
                return false;
            }
            if (!this.statistics.equals(that.statistics)) {
                return false;
            }
        }
        return true;
    }
    
    @Override
    public int hashCode() {
        final HashCodeBuilder builder = new HashCodeBuilder();
        final boolean present_type = this.isSetType();
        builder.append(present_type);
        if (present_type) {
            builder.append(this.type.getValue());
        }
        final boolean present_encodings = this.isSetEncodings();
        builder.append(present_encodings);
        if (present_encodings) {
            builder.append(this.encodings);
        }
        final boolean present_path_in_schema = this.isSetPath_in_schema();
        builder.append(present_path_in_schema);
        if (present_path_in_schema) {
            builder.append(this.path_in_schema);
        }
        final boolean present_codec = this.isSetCodec();
        builder.append(present_codec);
        if (present_codec) {
            builder.append(this.codec.getValue());
        }
        final boolean present_num_values = true;
        builder.append(present_num_values);
        if (present_num_values) {
            builder.append(this.num_values);
        }
        final boolean present_total_uncompressed_size = true;
        builder.append(present_total_uncompressed_size);
        if (present_total_uncompressed_size) {
            builder.append(this.total_uncompressed_size);
        }
        final boolean present_total_compressed_size = true;
        builder.append(present_total_compressed_size);
        if (present_total_compressed_size) {
            builder.append(this.total_compressed_size);
        }
        final boolean present_key_value_metadata = this.isSetKey_value_metadata();
        builder.append(present_key_value_metadata);
        if (present_key_value_metadata) {
            builder.append(this.key_value_metadata);
        }
        final boolean present_data_page_offset = true;
        builder.append(present_data_page_offset);
        if (present_data_page_offset) {
            builder.append(this.data_page_offset);
        }
        final boolean present_index_page_offset = this.isSetIndex_page_offset();
        builder.append(present_index_page_offset);
        if (present_index_page_offset) {
            builder.append(this.index_page_offset);
        }
        final boolean present_dictionary_page_offset = this.isSetDictionary_page_offset();
        builder.append(present_dictionary_page_offset);
        if (present_dictionary_page_offset) {
            builder.append(this.dictionary_page_offset);
        }
        final boolean present_statistics = this.isSetStatistics();
        builder.append(present_statistics);
        if (present_statistics) {
            builder.append(this.statistics);
        }
        return builder.toHashCode();
    }
    
    @Override
    public int compareTo(final ColumnMetaData other) {
        if (!this.getClass().equals(other.getClass())) {
            return this.getClass().getName().compareTo(other.getClass().getName());
        }
        int lastComparison = 0;
        final ColumnMetaData typedOther = other;
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
        lastComparison = Boolean.valueOf(this.isSetEncodings()).compareTo(Boolean.valueOf(typedOther.isSetEncodings()));
        if (lastComparison != 0) {
            return lastComparison;
        }
        if (this.isSetEncodings()) {
            lastComparison = TBaseHelper.compareTo(this.encodings, typedOther.encodings);
            if (lastComparison != 0) {
                return lastComparison;
            }
        }
        lastComparison = Boolean.valueOf(this.isSetPath_in_schema()).compareTo(Boolean.valueOf(typedOther.isSetPath_in_schema()));
        if (lastComparison != 0) {
            return lastComparison;
        }
        if (this.isSetPath_in_schema()) {
            lastComparison = TBaseHelper.compareTo(this.path_in_schema, typedOther.path_in_schema);
            if (lastComparison != 0) {
                return lastComparison;
            }
        }
        lastComparison = Boolean.valueOf(this.isSetCodec()).compareTo(Boolean.valueOf(typedOther.isSetCodec()));
        if (lastComparison != 0) {
            return lastComparison;
        }
        if (this.isSetCodec()) {
            lastComparison = TBaseHelper.compareTo(this.codec, typedOther.codec);
            if (lastComparison != 0) {
                return lastComparison;
            }
        }
        lastComparison = Boolean.valueOf(this.isSetNum_values()).compareTo(Boolean.valueOf(typedOther.isSetNum_values()));
        if (lastComparison != 0) {
            return lastComparison;
        }
        if (this.isSetNum_values()) {
            lastComparison = TBaseHelper.compareTo(this.num_values, typedOther.num_values);
            if (lastComparison != 0) {
                return lastComparison;
            }
        }
        lastComparison = Boolean.valueOf(this.isSetTotal_uncompressed_size()).compareTo(Boolean.valueOf(typedOther.isSetTotal_uncompressed_size()));
        if (lastComparison != 0) {
            return lastComparison;
        }
        if (this.isSetTotal_uncompressed_size()) {
            lastComparison = TBaseHelper.compareTo(this.total_uncompressed_size, typedOther.total_uncompressed_size);
            if (lastComparison != 0) {
                return lastComparison;
            }
        }
        lastComparison = Boolean.valueOf(this.isSetTotal_compressed_size()).compareTo(Boolean.valueOf(typedOther.isSetTotal_compressed_size()));
        if (lastComparison != 0) {
            return lastComparison;
        }
        if (this.isSetTotal_compressed_size()) {
            lastComparison = TBaseHelper.compareTo(this.total_compressed_size, typedOther.total_compressed_size);
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
        lastComparison = Boolean.valueOf(this.isSetData_page_offset()).compareTo(Boolean.valueOf(typedOther.isSetData_page_offset()));
        if (lastComparison != 0) {
            return lastComparison;
        }
        if (this.isSetData_page_offset()) {
            lastComparison = TBaseHelper.compareTo(this.data_page_offset, typedOther.data_page_offset);
            if (lastComparison != 0) {
                return lastComparison;
            }
        }
        lastComparison = Boolean.valueOf(this.isSetIndex_page_offset()).compareTo(Boolean.valueOf(typedOther.isSetIndex_page_offset()));
        if (lastComparison != 0) {
            return lastComparison;
        }
        if (this.isSetIndex_page_offset()) {
            lastComparison = TBaseHelper.compareTo(this.index_page_offset, typedOther.index_page_offset);
            if (lastComparison != 0) {
                return lastComparison;
            }
        }
        lastComparison = Boolean.valueOf(this.isSetDictionary_page_offset()).compareTo(Boolean.valueOf(typedOther.isSetDictionary_page_offset()));
        if (lastComparison != 0) {
            return lastComparison;
        }
        if (this.isSetDictionary_page_offset()) {
            lastComparison = TBaseHelper.compareTo(this.dictionary_page_offset, typedOther.dictionary_page_offset);
            if (lastComparison != 0) {
                return lastComparison;
            }
        }
        lastComparison = Boolean.valueOf(this.isSetStatistics()).compareTo(Boolean.valueOf(typedOther.isSetStatistics()));
        if (lastComparison != 0) {
            return lastComparison;
        }
        if (this.isSetStatistics()) {
            lastComparison = TBaseHelper.compareTo(this.statistics, typedOther.statistics);
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
                        this.type = Type.findByValue(iprot.readI32());
                        break;
                    }
                    TProtocolUtil.skip(iprot, field.type);
                    break;
                }
                case 2: {
                    if (field.type == 15) {
                        final TList _list0 = iprot.readListBegin();
                        this.encodings = new ArrayList<Encoding>(_list0.size);
                        for (int _i1 = 0; _i1 < _list0.size; ++_i1) {
                            final Encoding _elem2 = Encoding.findByValue(iprot.readI32());
                            this.encodings.add(_elem2);
                        }
                        iprot.readListEnd();
                        break;
                    }
                    TProtocolUtil.skip(iprot, field.type);
                    break;
                }
                case 3: {
                    if (field.type == 15) {
                        final TList _list2 = iprot.readListBegin();
                        this.path_in_schema = new ArrayList<String>(_list2.size);
                        for (int _i2 = 0; _i2 < _list2.size; ++_i2) {
                            final String _elem3 = iprot.readString();
                            this.path_in_schema.add(_elem3);
                        }
                        iprot.readListEnd();
                        break;
                    }
                    TProtocolUtil.skip(iprot, field.type);
                    break;
                }
                case 4: {
                    if (field.type == 8) {
                        this.codec = CompressionCodec.findByValue(iprot.readI32());
                        break;
                    }
                    TProtocolUtil.skip(iprot, field.type);
                    break;
                }
                case 5: {
                    if (field.type == 10) {
                        this.num_values = iprot.readI64();
                        this.setNum_valuesIsSet(true);
                        break;
                    }
                    TProtocolUtil.skip(iprot, field.type);
                    break;
                }
                case 6: {
                    if (field.type == 10) {
                        this.total_uncompressed_size = iprot.readI64();
                        this.setTotal_uncompressed_sizeIsSet(true);
                        break;
                    }
                    TProtocolUtil.skip(iprot, field.type);
                    break;
                }
                case 7: {
                    if (field.type == 10) {
                        this.total_compressed_size = iprot.readI64();
                        this.setTotal_compressed_sizeIsSet(true);
                        break;
                    }
                    TProtocolUtil.skip(iprot, field.type);
                    break;
                }
                case 8: {
                    if (field.type == 15) {
                        final TList _list3 = iprot.readListBegin();
                        this.key_value_metadata = new ArrayList<KeyValue>(_list3.size);
                        for (int _i3 = 0; _i3 < _list3.size; ++_i3) {
                            final KeyValue _elem4 = new KeyValue();
                            _elem4.read(iprot);
                            this.key_value_metadata.add(_elem4);
                        }
                        iprot.readListEnd();
                        break;
                    }
                    TProtocolUtil.skip(iprot, field.type);
                    break;
                }
                case 9: {
                    if (field.type == 10) {
                        this.data_page_offset = iprot.readI64();
                        this.setData_page_offsetIsSet(true);
                        break;
                    }
                    TProtocolUtil.skip(iprot, field.type);
                    break;
                }
                case 10: {
                    if (field.type == 10) {
                        this.index_page_offset = iprot.readI64();
                        this.setIndex_page_offsetIsSet(true);
                        break;
                    }
                    TProtocolUtil.skip(iprot, field.type);
                    break;
                }
                case 11: {
                    if (field.type == 10) {
                        this.dictionary_page_offset = iprot.readI64();
                        this.setDictionary_page_offsetIsSet(true);
                        break;
                    }
                    TProtocolUtil.skip(iprot, field.type);
                    break;
                }
                case 12: {
                    if (field.type == 12) {
                        (this.statistics = new Statistics()).read(iprot);
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
        if (!this.isSetNum_values()) {
            throw new TProtocolException("Required field 'num_values' was not found in serialized data! Struct: " + this.toString());
        }
        if (!this.isSetTotal_uncompressed_size()) {
            throw new TProtocolException("Required field 'total_uncompressed_size' was not found in serialized data! Struct: " + this.toString());
        }
        if (!this.isSetTotal_compressed_size()) {
            throw new TProtocolException("Required field 'total_compressed_size' was not found in serialized data! Struct: " + this.toString());
        }
        if (!this.isSetData_page_offset()) {
            throw new TProtocolException("Required field 'data_page_offset' was not found in serialized data! Struct: " + this.toString());
        }
        this.validate();
    }
    
    @Override
    public void write(final TProtocol oprot) throws TException {
        this.validate();
        oprot.writeStructBegin(ColumnMetaData.STRUCT_DESC);
        if (this.type != null) {
            oprot.writeFieldBegin(ColumnMetaData.TYPE_FIELD_DESC);
            oprot.writeI32(this.type.getValue());
            oprot.writeFieldEnd();
        }
        if (this.encodings != null) {
            oprot.writeFieldBegin(ColumnMetaData.ENCODINGS_FIELD_DESC);
            oprot.writeListBegin(new TList((byte)8, this.encodings.size()));
            for (final Encoding _iter9 : this.encodings) {
                oprot.writeI32(_iter9.getValue());
            }
            oprot.writeListEnd();
            oprot.writeFieldEnd();
        }
        if (this.path_in_schema != null) {
            oprot.writeFieldBegin(ColumnMetaData.PATH_IN_SCHEMA_FIELD_DESC);
            oprot.writeListBegin(new TList((byte)11, this.path_in_schema.size()));
            for (final String _iter10 : this.path_in_schema) {
                oprot.writeString(_iter10);
            }
            oprot.writeListEnd();
            oprot.writeFieldEnd();
        }
        if (this.codec != null) {
            oprot.writeFieldBegin(ColumnMetaData.CODEC_FIELD_DESC);
            oprot.writeI32(this.codec.getValue());
            oprot.writeFieldEnd();
        }
        oprot.writeFieldBegin(ColumnMetaData.NUM_VALUES_FIELD_DESC);
        oprot.writeI64(this.num_values);
        oprot.writeFieldEnd();
        oprot.writeFieldBegin(ColumnMetaData.TOTAL_UNCOMPRESSED_SIZE_FIELD_DESC);
        oprot.writeI64(this.total_uncompressed_size);
        oprot.writeFieldEnd();
        oprot.writeFieldBegin(ColumnMetaData.TOTAL_COMPRESSED_SIZE_FIELD_DESC);
        oprot.writeI64(this.total_compressed_size);
        oprot.writeFieldEnd();
        if (this.key_value_metadata != null && this.isSetKey_value_metadata()) {
            oprot.writeFieldBegin(ColumnMetaData.KEY_VALUE_METADATA_FIELD_DESC);
            oprot.writeListBegin(new TList((byte)12, this.key_value_metadata.size()));
            for (final KeyValue _iter11 : this.key_value_metadata) {
                _iter11.write(oprot);
            }
            oprot.writeListEnd();
            oprot.writeFieldEnd();
        }
        oprot.writeFieldBegin(ColumnMetaData.DATA_PAGE_OFFSET_FIELD_DESC);
        oprot.writeI64(this.data_page_offset);
        oprot.writeFieldEnd();
        if (this.isSetIndex_page_offset()) {
            oprot.writeFieldBegin(ColumnMetaData.INDEX_PAGE_OFFSET_FIELD_DESC);
            oprot.writeI64(this.index_page_offset);
            oprot.writeFieldEnd();
        }
        if (this.isSetDictionary_page_offset()) {
            oprot.writeFieldBegin(ColumnMetaData.DICTIONARY_PAGE_OFFSET_FIELD_DESC);
            oprot.writeI64(this.dictionary_page_offset);
            oprot.writeFieldEnd();
        }
        if (this.statistics != null && this.isSetStatistics()) {
            oprot.writeFieldBegin(ColumnMetaData.STATISTICS_FIELD_DESC);
            this.statistics.write(oprot);
            oprot.writeFieldEnd();
        }
        oprot.writeFieldStop();
        oprot.writeStructEnd();
    }
    
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("ColumnMetaData(");
        boolean first = true;
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
        sb.append("encodings:");
        if (this.encodings == null) {
            sb.append("null");
        }
        else {
            sb.append(this.encodings);
        }
        first = false;
        if (!first) {
            sb.append(", ");
        }
        sb.append("path_in_schema:");
        if (this.path_in_schema == null) {
            sb.append("null");
        }
        else {
            sb.append(this.path_in_schema);
        }
        first = false;
        if (!first) {
            sb.append(", ");
        }
        sb.append("codec:");
        if (this.codec == null) {
            sb.append("null");
        }
        else {
            sb.append(this.codec);
        }
        first = false;
        if (!first) {
            sb.append(", ");
        }
        sb.append("num_values:");
        sb.append(this.num_values);
        first = false;
        if (!first) {
            sb.append(", ");
        }
        sb.append("total_uncompressed_size:");
        sb.append(this.total_uncompressed_size);
        first = false;
        if (!first) {
            sb.append(", ");
        }
        sb.append("total_compressed_size:");
        sb.append(this.total_compressed_size);
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
        if (!first) {
            sb.append(", ");
        }
        sb.append("data_page_offset:");
        sb.append(this.data_page_offset);
        first = false;
        if (this.isSetIndex_page_offset()) {
            if (!first) {
                sb.append(", ");
            }
            sb.append("index_page_offset:");
            sb.append(this.index_page_offset);
            first = false;
        }
        if (this.isSetDictionary_page_offset()) {
            if (!first) {
                sb.append(", ");
            }
            sb.append("dictionary_page_offset:");
            sb.append(this.dictionary_page_offset);
            first = false;
        }
        if (this.isSetStatistics()) {
            if (!first) {
                sb.append(", ");
            }
            sb.append("statistics:");
            if (this.statistics == null) {
                sb.append("null");
            }
            else {
                sb.append(this.statistics);
            }
            first = false;
        }
        sb.append(")");
        return sb.toString();
    }
    
    public void validate() throws TException {
        if (this.type == null) {
            throw new TProtocolException("Required field 'type' was not present! Struct: " + this.toString());
        }
        if (this.encodings == null) {
            throw new TProtocolException("Required field 'encodings' was not present! Struct: " + this.toString());
        }
        if (this.path_in_schema == null) {
            throw new TProtocolException("Required field 'path_in_schema' was not present! Struct: " + this.toString());
        }
        if (this.codec == null) {
            throw new TProtocolException("Required field 'codec' was not present! Struct: " + this.toString());
        }
    }
    
    static {
        STRUCT_DESC = new TStruct("ColumnMetaData");
        TYPE_FIELD_DESC = new TField("type", (byte)8, (short)1);
        ENCODINGS_FIELD_DESC = new TField("encodings", (byte)15, (short)2);
        PATH_IN_SCHEMA_FIELD_DESC = new TField("path_in_schema", (byte)15, (short)3);
        CODEC_FIELD_DESC = new TField("codec", (byte)8, (short)4);
        NUM_VALUES_FIELD_DESC = new TField("num_values", (byte)10, (short)5);
        TOTAL_UNCOMPRESSED_SIZE_FIELD_DESC = new TField("total_uncompressed_size", (byte)10, (short)6);
        TOTAL_COMPRESSED_SIZE_FIELD_DESC = new TField("total_compressed_size", (byte)10, (short)7);
        KEY_VALUE_METADATA_FIELD_DESC = new TField("key_value_metadata", (byte)15, (short)8);
        DATA_PAGE_OFFSET_FIELD_DESC = new TField("data_page_offset", (byte)10, (short)9);
        INDEX_PAGE_OFFSET_FIELD_DESC = new TField("index_page_offset", (byte)10, (short)10);
        DICTIONARY_PAGE_OFFSET_FIELD_DESC = new TField("dictionary_page_offset", (byte)10, (short)11);
        STATISTICS_FIELD_DESC = new TField("statistics", (byte)12, (short)12);
        final Map<_Fields, FieldMetaData> tmpMap = new EnumMap<_Fields, FieldMetaData>(_Fields.class);
        tmpMap.put(_Fields.TYPE, new FieldMetaData("type", (byte)1, new EnumMetaData((byte)16, Type.class)));
        tmpMap.put(_Fields.ENCODINGS, new FieldMetaData("encodings", (byte)1, new ListMetaData((byte)15, new EnumMetaData((byte)16, Encoding.class))));
        tmpMap.put(_Fields.PATH_IN_SCHEMA, new FieldMetaData("path_in_schema", (byte)1, new ListMetaData((byte)15, new FieldValueMetaData((byte)11))));
        tmpMap.put(_Fields.CODEC, new FieldMetaData("codec", (byte)1, new EnumMetaData((byte)16, CompressionCodec.class)));
        tmpMap.put(_Fields.NUM_VALUES, new FieldMetaData("num_values", (byte)1, new FieldValueMetaData((byte)10)));
        tmpMap.put(_Fields.TOTAL_UNCOMPRESSED_SIZE, new FieldMetaData("total_uncompressed_size", (byte)1, new FieldValueMetaData((byte)10)));
        tmpMap.put(_Fields.TOTAL_COMPRESSED_SIZE, new FieldMetaData("total_compressed_size", (byte)1, new FieldValueMetaData((byte)10)));
        tmpMap.put(_Fields.KEY_VALUE_METADATA, new FieldMetaData("key_value_metadata", (byte)2, new ListMetaData((byte)15, new StructMetaData((byte)12, KeyValue.class))));
        tmpMap.put(_Fields.DATA_PAGE_OFFSET, new FieldMetaData("data_page_offset", (byte)1, new FieldValueMetaData((byte)10)));
        tmpMap.put(_Fields.INDEX_PAGE_OFFSET, new FieldMetaData("index_page_offset", (byte)2, new FieldValueMetaData((byte)10)));
        tmpMap.put(_Fields.DICTIONARY_PAGE_OFFSET, new FieldMetaData("dictionary_page_offset", (byte)2, new FieldValueMetaData((byte)10)));
        tmpMap.put(_Fields.STATISTICS, new FieldMetaData("statistics", (byte)2, new StructMetaData((byte)12, Statistics.class)));
        FieldMetaData.addStructMetaDataMap(ColumnMetaData.class, metaDataMap = Collections.unmodifiableMap((Map<? extends _Fields, ? extends FieldMetaData>)tmpMap));
    }
    
    public enum _Fields implements TFieldIdEnum
    {
        TYPE((short)1, "type"), 
        ENCODINGS((short)2, "encodings"), 
        PATH_IN_SCHEMA((short)3, "path_in_schema"), 
        CODEC((short)4, "codec"), 
        NUM_VALUES((short)5, "num_values"), 
        TOTAL_UNCOMPRESSED_SIZE((short)6, "total_uncompressed_size"), 
        TOTAL_COMPRESSED_SIZE((short)7, "total_compressed_size"), 
        KEY_VALUE_METADATA((short)8, "key_value_metadata"), 
        DATA_PAGE_OFFSET((short)9, "data_page_offset"), 
        INDEX_PAGE_OFFSET((short)10, "index_page_offset"), 
        DICTIONARY_PAGE_OFFSET((short)11, "dictionary_page_offset"), 
        STATISTICS((short)12, "statistics");
        
        private static final Map<String, _Fields> byName;
        private final short _thriftId;
        private final String _fieldName;
        
        public static _Fields findByThriftId(final int fieldId) {
            switch (fieldId) {
                case 1: {
                    return _Fields.TYPE;
                }
                case 2: {
                    return _Fields.ENCODINGS;
                }
                case 3: {
                    return _Fields.PATH_IN_SCHEMA;
                }
                case 4: {
                    return _Fields.CODEC;
                }
                case 5: {
                    return _Fields.NUM_VALUES;
                }
                case 6: {
                    return _Fields.TOTAL_UNCOMPRESSED_SIZE;
                }
                case 7: {
                    return _Fields.TOTAL_COMPRESSED_SIZE;
                }
                case 8: {
                    return _Fields.KEY_VALUE_METADATA;
                }
                case 9: {
                    return _Fields.DATA_PAGE_OFFSET;
                }
                case 10: {
                    return _Fields.INDEX_PAGE_OFFSET;
                }
                case 11: {
                    return _Fields.DICTIONARY_PAGE_OFFSET;
                }
                case 12: {
                    return _Fields.STATISTICS;
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
