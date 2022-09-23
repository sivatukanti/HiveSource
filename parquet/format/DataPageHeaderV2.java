// 
// Decompiled by Procyon v0.5.36
// 

package parquet.format;

import java.util.Iterator;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Collections;
import parquet.org.apache.thrift.meta_data.StructMetaData;
import parquet.org.apache.thrift.TEnum;
import parquet.org.apache.thrift.meta_data.EnumMetaData;
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

public class DataPageHeaderV2 implements TBase<DataPageHeaderV2, _Fields>, Serializable, Cloneable
{
    private static final TStruct STRUCT_DESC;
    private static final TField NUM_VALUES_FIELD_DESC;
    private static final TField NUM_NULLS_FIELD_DESC;
    private static final TField NUM_ROWS_FIELD_DESC;
    private static final TField ENCODING_FIELD_DESC;
    private static final TField DEFINITION_LEVELS_BYTE_LENGTH_FIELD_DESC;
    private static final TField REPETITION_LEVELS_BYTE_LENGTH_FIELD_DESC;
    private static final TField IS_COMPRESSED_FIELD_DESC;
    private static final TField STATISTICS_FIELD_DESC;
    public int num_values;
    public int num_nulls;
    public int num_rows;
    public Encoding encoding;
    public int definition_levels_byte_length;
    public int repetition_levels_byte_length;
    public boolean is_compressed;
    public Statistics statistics;
    private static final int __NUM_VALUES_ISSET_ID = 0;
    private static final int __NUM_NULLS_ISSET_ID = 1;
    private static final int __NUM_ROWS_ISSET_ID = 2;
    private static final int __DEFINITION_LEVELS_BYTE_LENGTH_ISSET_ID = 3;
    private static final int __REPETITION_LEVELS_BYTE_LENGTH_ISSET_ID = 4;
    private static final int __IS_COMPRESSED_ISSET_ID = 5;
    private BitSet __isset_bit_vector;
    public static final Map<_Fields, FieldMetaData> metaDataMap;
    
    public DataPageHeaderV2() {
        this.__isset_bit_vector = new BitSet(6);
        this.is_compressed = true;
    }
    
    public DataPageHeaderV2(final int num_values, final int num_nulls, final int num_rows, final Encoding encoding, final int definition_levels_byte_length, final int repetition_levels_byte_length) {
        this();
        this.num_values = num_values;
        this.setNum_valuesIsSet(true);
        this.num_nulls = num_nulls;
        this.setNum_nullsIsSet(true);
        this.num_rows = num_rows;
        this.setNum_rowsIsSet(true);
        this.encoding = encoding;
        this.definition_levels_byte_length = definition_levels_byte_length;
        this.setDefinition_levels_byte_lengthIsSet(true);
        this.repetition_levels_byte_length = repetition_levels_byte_length;
        this.setRepetition_levels_byte_lengthIsSet(true);
    }
    
    public DataPageHeaderV2(final DataPageHeaderV2 other) {
        (this.__isset_bit_vector = new BitSet(6)).clear();
        this.__isset_bit_vector.or(other.__isset_bit_vector);
        this.num_values = other.num_values;
        this.num_nulls = other.num_nulls;
        this.num_rows = other.num_rows;
        if (other.isSetEncoding()) {
            this.encoding = other.encoding;
        }
        this.definition_levels_byte_length = other.definition_levels_byte_length;
        this.repetition_levels_byte_length = other.repetition_levels_byte_length;
        this.is_compressed = other.is_compressed;
        if (other.isSetStatistics()) {
            this.statistics = new Statistics(other.statistics);
        }
    }
    
    @Override
    public DataPageHeaderV2 deepCopy() {
        return new DataPageHeaderV2(this);
    }
    
    @Override
    public void clear() {
        this.setNum_valuesIsSet(false);
        this.num_values = 0;
        this.setNum_nullsIsSet(false);
        this.num_nulls = 0;
        this.setNum_rowsIsSet(false);
        this.num_rows = 0;
        this.encoding = null;
        this.setDefinition_levels_byte_lengthIsSet(false);
        this.definition_levels_byte_length = 0;
        this.setRepetition_levels_byte_lengthIsSet(false);
        this.repetition_levels_byte_length = 0;
        this.is_compressed = true;
        this.statistics = null;
    }
    
    public int getNum_values() {
        return this.num_values;
    }
    
    public DataPageHeaderV2 setNum_values(final int num_values) {
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
    
    public int getNum_nulls() {
        return this.num_nulls;
    }
    
    public DataPageHeaderV2 setNum_nulls(final int num_nulls) {
        this.num_nulls = num_nulls;
        this.setNum_nullsIsSet(true);
        return this;
    }
    
    public void unsetNum_nulls() {
        this.__isset_bit_vector.clear(1);
    }
    
    public boolean isSetNum_nulls() {
        return this.__isset_bit_vector.get(1);
    }
    
    public void setNum_nullsIsSet(final boolean value) {
        this.__isset_bit_vector.set(1, value);
    }
    
    public int getNum_rows() {
        return this.num_rows;
    }
    
    public DataPageHeaderV2 setNum_rows(final int num_rows) {
        this.num_rows = num_rows;
        this.setNum_rowsIsSet(true);
        return this;
    }
    
    public void unsetNum_rows() {
        this.__isset_bit_vector.clear(2);
    }
    
    public boolean isSetNum_rows() {
        return this.__isset_bit_vector.get(2);
    }
    
    public void setNum_rowsIsSet(final boolean value) {
        this.__isset_bit_vector.set(2, value);
    }
    
    public Encoding getEncoding() {
        return this.encoding;
    }
    
    public DataPageHeaderV2 setEncoding(final Encoding encoding) {
        this.encoding = encoding;
        return this;
    }
    
    public void unsetEncoding() {
        this.encoding = null;
    }
    
    public boolean isSetEncoding() {
        return this.encoding != null;
    }
    
    public void setEncodingIsSet(final boolean value) {
        if (!value) {
            this.encoding = null;
        }
    }
    
    public int getDefinition_levels_byte_length() {
        return this.definition_levels_byte_length;
    }
    
    public DataPageHeaderV2 setDefinition_levels_byte_length(final int definition_levels_byte_length) {
        this.definition_levels_byte_length = definition_levels_byte_length;
        this.setDefinition_levels_byte_lengthIsSet(true);
        return this;
    }
    
    public void unsetDefinition_levels_byte_length() {
        this.__isset_bit_vector.clear(3);
    }
    
    public boolean isSetDefinition_levels_byte_length() {
        return this.__isset_bit_vector.get(3);
    }
    
    public void setDefinition_levels_byte_lengthIsSet(final boolean value) {
        this.__isset_bit_vector.set(3, value);
    }
    
    public int getRepetition_levels_byte_length() {
        return this.repetition_levels_byte_length;
    }
    
    public DataPageHeaderV2 setRepetition_levels_byte_length(final int repetition_levels_byte_length) {
        this.repetition_levels_byte_length = repetition_levels_byte_length;
        this.setRepetition_levels_byte_lengthIsSet(true);
        return this;
    }
    
    public void unsetRepetition_levels_byte_length() {
        this.__isset_bit_vector.clear(4);
    }
    
    public boolean isSetRepetition_levels_byte_length() {
        return this.__isset_bit_vector.get(4);
    }
    
    public void setRepetition_levels_byte_lengthIsSet(final boolean value) {
        this.__isset_bit_vector.set(4, value);
    }
    
    public boolean isIs_compressed() {
        return this.is_compressed;
    }
    
    public DataPageHeaderV2 setIs_compressed(final boolean is_compressed) {
        this.is_compressed = is_compressed;
        this.setIs_compressedIsSet(true);
        return this;
    }
    
    public void unsetIs_compressed() {
        this.__isset_bit_vector.clear(5);
    }
    
    public boolean isSetIs_compressed() {
        return this.__isset_bit_vector.get(5);
    }
    
    public void setIs_compressedIsSet(final boolean value) {
        this.__isset_bit_vector.set(5, value);
    }
    
    public Statistics getStatistics() {
        return this.statistics;
    }
    
    public DataPageHeaderV2 setStatistics(final Statistics statistics) {
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
            case NUM_VALUES: {
                if (value == null) {
                    this.unsetNum_values();
                    break;
                }
                this.setNum_values((int)value);
                break;
            }
            case NUM_NULLS: {
                if (value == null) {
                    this.unsetNum_nulls();
                    break;
                }
                this.setNum_nulls((int)value);
                break;
            }
            case NUM_ROWS: {
                if (value == null) {
                    this.unsetNum_rows();
                    break;
                }
                this.setNum_rows((int)value);
                break;
            }
            case ENCODING: {
                if (value == null) {
                    this.unsetEncoding();
                    break;
                }
                this.setEncoding((Encoding)value);
                break;
            }
            case DEFINITION_LEVELS_BYTE_LENGTH: {
                if (value == null) {
                    this.unsetDefinition_levels_byte_length();
                    break;
                }
                this.setDefinition_levels_byte_length((int)value);
                break;
            }
            case REPETITION_LEVELS_BYTE_LENGTH: {
                if (value == null) {
                    this.unsetRepetition_levels_byte_length();
                    break;
                }
                this.setRepetition_levels_byte_length((int)value);
                break;
            }
            case IS_COMPRESSED: {
                if (value == null) {
                    this.unsetIs_compressed();
                    break;
                }
                this.setIs_compressed((boolean)value);
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
            case NUM_VALUES: {
                return new Integer(this.getNum_values());
            }
            case NUM_NULLS: {
                return new Integer(this.getNum_nulls());
            }
            case NUM_ROWS: {
                return new Integer(this.getNum_rows());
            }
            case ENCODING: {
                return this.getEncoding();
            }
            case DEFINITION_LEVELS_BYTE_LENGTH: {
                return new Integer(this.getDefinition_levels_byte_length());
            }
            case REPETITION_LEVELS_BYTE_LENGTH: {
                return new Integer(this.getRepetition_levels_byte_length());
            }
            case IS_COMPRESSED: {
                return new Boolean(this.isIs_compressed());
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
            case NUM_VALUES: {
                return this.isSetNum_values();
            }
            case NUM_NULLS: {
                return this.isSetNum_nulls();
            }
            case NUM_ROWS: {
                return this.isSetNum_rows();
            }
            case ENCODING: {
                return this.isSetEncoding();
            }
            case DEFINITION_LEVELS_BYTE_LENGTH: {
                return this.isSetDefinition_levels_byte_length();
            }
            case REPETITION_LEVELS_BYTE_LENGTH: {
                return this.isSetRepetition_levels_byte_length();
            }
            case IS_COMPRESSED: {
                return this.isSetIs_compressed();
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
        return that != null && that instanceof DataPageHeaderV2 && this.equals((DataPageHeaderV2)that);
    }
    
    public boolean equals(final DataPageHeaderV2 that) {
        if (that == null) {
            return false;
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
        final boolean this_present_num_nulls = true;
        final boolean that_present_num_nulls = true;
        if (this_present_num_nulls || that_present_num_nulls) {
            if (!this_present_num_nulls || !that_present_num_nulls) {
                return false;
            }
            if (this.num_nulls != that.num_nulls) {
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
        final boolean this_present_encoding = this.isSetEncoding();
        final boolean that_present_encoding = that.isSetEncoding();
        if (this_present_encoding || that_present_encoding) {
            if (!this_present_encoding || !that_present_encoding) {
                return false;
            }
            if (!this.encoding.equals(that.encoding)) {
                return false;
            }
        }
        final boolean this_present_definition_levels_byte_length = true;
        final boolean that_present_definition_levels_byte_length = true;
        if (this_present_definition_levels_byte_length || that_present_definition_levels_byte_length) {
            if (!this_present_definition_levels_byte_length || !that_present_definition_levels_byte_length) {
                return false;
            }
            if (this.definition_levels_byte_length != that.definition_levels_byte_length) {
                return false;
            }
        }
        final boolean this_present_repetition_levels_byte_length = true;
        final boolean that_present_repetition_levels_byte_length = true;
        if (this_present_repetition_levels_byte_length || that_present_repetition_levels_byte_length) {
            if (!this_present_repetition_levels_byte_length || !that_present_repetition_levels_byte_length) {
                return false;
            }
            if (this.repetition_levels_byte_length != that.repetition_levels_byte_length) {
                return false;
            }
        }
        final boolean this_present_is_compressed = this.isSetIs_compressed();
        final boolean that_present_is_compressed = that.isSetIs_compressed();
        if (this_present_is_compressed || that_present_is_compressed) {
            if (!this_present_is_compressed || !that_present_is_compressed) {
                return false;
            }
            if (this.is_compressed != that.is_compressed) {
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
        final boolean present_num_values = true;
        builder.append(present_num_values);
        if (present_num_values) {
            builder.append(this.num_values);
        }
        final boolean present_num_nulls = true;
        builder.append(present_num_nulls);
        if (present_num_nulls) {
            builder.append(this.num_nulls);
        }
        final boolean present_num_rows = true;
        builder.append(present_num_rows);
        if (present_num_rows) {
            builder.append(this.num_rows);
        }
        final boolean present_encoding = this.isSetEncoding();
        builder.append(present_encoding);
        if (present_encoding) {
            builder.append(this.encoding.getValue());
        }
        final boolean present_definition_levels_byte_length = true;
        builder.append(present_definition_levels_byte_length);
        if (present_definition_levels_byte_length) {
            builder.append(this.definition_levels_byte_length);
        }
        final boolean present_repetition_levels_byte_length = true;
        builder.append(present_repetition_levels_byte_length);
        if (present_repetition_levels_byte_length) {
            builder.append(this.repetition_levels_byte_length);
        }
        final boolean present_is_compressed = this.isSetIs_compressed();
        builder.append(present_is_compressed);
        if (present_is_compressed) {
            builder.append(this.is_compressed);
        }
        final boolean present_statistics = this.isSetStatistics();
        builder.append(present_statistics);
        if (present_statistics) {
            builder.append(this.statistics);
        }
        return builder.toHashCode();
    }
    
    @Override
    public int compareTo(final DataPageHeaderV2 other) {
        if (!this.getClass().equals(other.getClass())) {
            return this.getClass().getName().compareTo(other.getClass().getName());
        }
        int lastComparison = 0;
        final DataPageHeaderV2 typedOther = other;
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
        lastComparison = Boolean.valueOf(this.isSetNum_nulls()).compareTo(Boolean.valueOf(typedOther.isSetNum_nulls()));
        if (lastComparison != 0) {
            return lastComparison;
        }
        if (this.isSetNum_nulls()) {
            lastComparison = TBaseHelper.compareTo(this.num_nulls, typedOther.num_nulls);
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
        lastComparison = Boolean.valueOf(this.isSetEncoding()).compareTo(Boolean.valueOf(typedOther.isSetEncoding()));
        if (lastComparison != 0) {
            return lastComparison;
        }
        if (this.isSetEncoding()) {
            lastComparison = TBaseHelper.compareTo(this.encoding, typedOther.encoding);
            if (lastComparison != 0) {
                return lastComparison;
            }
        }
        lastComparison = Boolean.valueOf(this.isSetDefinition_levels_byte_length()).compareTo(Boolean.valueOf(typedOther.isSetDefinition_levels_byte_length()));
        if (lastComparison != 0) {
            return lastComparison;
        }
        if (this.isSetDefinition_levels_byte_length()) {
            lastComparison = TBaseHelper.compareTo(this.definition_levels_byte_length, typedOther.definition_levels_byte_length);
            if (lastComparison != 0) {
                return lastComparison;
            }
        }
        lastComparison = Boolean.valueOf(this.isSetRepetition_levels_byte_length()).compareTo(Boolean.valueOf(typedOther.isSetRepetition_levels_byte_length()));
        if (lastComparison != 0) {
            return lastComparison;
        }
        if (this.isSetRepetition_levels_byte_length()) {
            lastComparison = TBaseHelper.compareTo(this.repetition_levels_byte_length, typedOther.repetition_levels_byte_length);
            if (lastComparison != 0) {
                return lastComparison;
            }
        }
        lastComparison = Boolean.valueOf(this.isSetIs_compressed()).compareTo(Boolean.valueOf(typedOther.isSetIs_compressed()));
        if (lastComparison != 0) {
            return lastComparison;
        }
        if (this.isSetIs_compressed()) {
            lastComparison = TBaseHelper.compareTo(this.is_compressed, typedOther.is_compressed);
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
                        this.num_values = iprot.readI32();
                        this.setNum_valuesIsSet(true);
                        break;
                    }
                    TProtocolUtil.skip(iprot, field.type);
                    break;
                }
                case 2: {
                    if (field.type == 8) {
                        this.num_nulls = iprot.readI32();
                        this.setNum_nullsIsSet(true);
                        break;
                    }
                    TProtocolUtil.skip(iprot, field.type);
                    break;
                }
                case 3: {
                    if (field.type == 8) {
                        this.num_rows = iprot.readI32();
                        this.setNum_rowsIsSet(true);
                        break;
                    }
                    TProtocolUtil.skip(iprot, field.type);
                    break;
                }
                case 4: {
                    if (field.type == 8) {
                        this.encoding = Encoding.findByValue(iprot.readI32());
                        break;
                    }
                    TProtocolUtil.skip(iprot, field.type);
                    break;
                }
                case 5: {
                    if (field.type == 8) {
                        this.definition_levels_byte_length = iprot.readI32();
                        this.setDefinition_levels_byte_lengthIsSet(true);
                        break;
                    }
                    TProtocolUtil.skip(iprot, field.type);
                    break;
                }
                case 6: {
                    if (field.type == 8) {
                        this.repetition_levels_byte_length = iprot.readI32();
                        this.setRepetition_levels_byte_lengthIsSet(true);
                        break;
                    }
                    TProtocolUtil.skip(iprot, field.type);
                    break;
                }
                case 7: {
                    if (field.type == 2) {
                        this.is_compressed = iprot.readBool();
                        this.setIs_compressedIsSet(true);
                        break;
                    }
                    TProtocolUtil.skip(iprot, field.type);
                    break;
                }
                case 8: {
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
        if (!this.isSetNum_nulls()) {
            throw new TProtocolException("Required field 'num_nulls' was not found in serialized data! Struct: " + this.toString());
        }
        if (!this.isSetNum_rows()) {
            throw new TProtocolException("Required field 'num_rows' was not found in serialized data! Struct: " + this.toString());
        }
        if (!this.isSetDefinition_levels_byte_length()) {
            throw new TProtocolException("Required field 'definition_levels_byte_length' was not found in serialized data! Struct: " + this.toString());
        }
        if (!this.isSetRepetition_levels_byte_length()) {
            throw new TProtocolException("Required field 'repetition_levels_byte_length' was not found in serialized data! Struct: " + this.toString());
        }
        this.validate();
    }
    
    @Override
    public void write(final TProtocol oprot) throws TException {
        this.validate();
        oprot.writeStructBegin(DataPageHeaderV2.STRUCT_DESC);
        oprot.writeFieldBegin(DataPageHeaderV2.NUM_VALUES_FIELD_DESC);
        oprot.writeI32(this.num_values);
        oprot.writeFieldEnd();
        oprot.writeFieldBegin(DataPageHeaderV2.NUM_NULLS_FIELD_DESC);
        oprot.writeI32(this.num_nulls);
        oprot.writeFieldEnd();
        oprot.writeFieldBegin(DataPageHeaderV2.NUM_ROWS_FIELD_DESC);
        oprot.writeI32(this.num_rows);
        oprot.writeFieldEnd();
        if (this.encoding != null) {
            oprot.writeFieldBegin(DataPageHeaderV2.ENCODING_FIELD_DESC);
            oprot.writeI32(this.encoding.getValue());
            oprot.writeFieldEnd();
        }
        oprot.writeFieldBegin(DataPageHeaderV2.DEFINITION_LEVELS_BYTE_LENGTH_FIELD_DESC);
        oprot.writeI32(this.definition_levels_byte_length);
        oprot.writeFieldEnd();
        oprot.writeFieldBegin(DataPageHeaderV2.REPETITION_LEVELS_BYTE_LENGTH_FIELD_DESC);
        oprot.writeI32(this.repetition_levels_byte_length);
        oprot.writeFieldEnd();
        if (this.isSetIs_compressed()) {
            oprot.writeFieldBegin(DataPageHeaderV2.IS_COMPRESSED_FIELD_DESC);
            oprot.writeBool(this.is_compressed);
            oprot.writeFieldEnd();
        }
        if (this.statistics != null && this.isSetStatistics()) {
            oprot.writeFieldBegin(DataPageHeaderV2.STATISTICS_FIELD_DESC);
            this.statistics.write(oprot);
            oprot.writeFieldEnd();
        }
        oprot.writeFieldStop();
        oprot.writeStructEnd();
    }
    
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("DataPageHeaderV2(");
        boolean first = true;
        sb.append("num_values:");
        sb.append(this.num_values);
        first = false;
        if (!first) {
            sb.append(", ");
        }
        sb.append("num_nulls:");
        sb.append(this.num_nulls);
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
        sb.append("encoding:");
        if (this.encoding == null) {
            sb.append("null");
        }
        else {
            sb.append(this.encoding);
        }
        first = false;
        if (!first) {
            sb.append(", ");
        }
        sb.append("definition_levels_byte_length:");
        sb.append(this.definition_levels_byte_length);
        first = false;
        if (!first) {
            sb.append(", ");
        }
        sb.append("repetition_levels_byte_length:");
        sb.append(this.repetition_levels_byte_length);
        first = false;
        if (this.isSetIs_compressed()) {
            if (!first) {
                sb.append(", ");
            }
            sb.append("is_compressed:");
            sb.append(this.is_compressed);
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
        if (this.encoding == null) {
            throw new TProtocolException("Required field 'encoding' was not present! Struct: " + this.toString());
        }
    }
    
    static {
        STRUCT_DESC = new TStruct("DataPageHeaderV2");
        NUM_VALUES_FIELD_DESC = new TField("num_values", (byte)8, (short)1);
        NUM_NULLS_FIELD_DESC = new TField("num_nulls", (byte)8, (short)2);
        NUM_ROWS_FIELD_DESC = new TField("num_rows", (byte)8, (short)3);
        ENCODING_FIELD_DESC = new TField("encoding", (byte)8, (short)4);
        DEFINITION_LEVELS_BYTE_LENGTH_FIELD_DESC = new TField("definition_levels_byte_length", (byte)8, (short)5);
        REPETITION_LEVELS_BYTE_LENGTH_FIELD_DESC = new TField("repetition_levels_byte_length", (byte)8, (short)6);
        IS_COMPRESSED_FIELD_DESC = new TField("is_compressed", (byte)2, (short)7);
        STATISTICS_FIELD_DESC = new TField("statistics", (byte)12, (short)8);
        final Map<_Fields, FieldMetaData> tmpMap = new EnumMap<_Fields, FieldMetaData>(_Fields.class);
        tmpMap.put(_Fields.NUM_VALUES, new FieldMetaData("num_values", (byte)1, new FieldValueMetaData((byte)8)));
        tmpMap.put(_Fields.NUM_NULLS, new FieldMetaData("num_nulls", (byte)1, new FieldValueMetaData((byte)8)));
        tmpMap.put(_Fields.NUM_ROWS, new FieldMetaData("num_rows", (byte)1, new FieldValueMetaData((byte)8)));
        tmpMap.put(_Fields.ENCODING, new FieldMetaData("encoding", (byte)1, new EnumMetaData((byte)16, Encoding.class)));
        tmpMap.put(_Fields.DEFINITION_LEVELS_BYTE_LENGTH, new FieldMetaData("definition_levels_byte_length", (byte)1, new FieldValueMetaData((byte)8)));
        tmpMap.put(_Fields.REPETITION_LEVELS_BYTE_LENGTH, new FieldMetaData("repetition_levels_byte_length", (byte)1, new FieldValueMetaData((byte)8)));
        tmpMap.put(_Fields.IS_COMPRESSED, new FieldMetaData("is_compressed", (byte)2, new FieldValueMetaData((byte)2)));
        tmpMap.put(_Fields.STATISTICS, new FieldMetaData("statistics", (byte)2, new StructMetaData((byte)12, Statistics.class)));
        FieldMetaData.addStructMetaDataMap(DataPageHeaderV2.class, metaDataMap = Collections.unmodifiableMap((Map<? extends _Fields, ? extends FieldMetaData>)tmpMap));
    }
    
    public enum _Fields implements TFieldIdEnum
    {
        NUM_VALUES((short)1, "num_values"), 
        NUM_NULLS((short)2, "num_nulls"), 
        NUM_ROWS((short)3, "num_rows"), 
        ENCODING((short)4, "encoding"), 
        DEFINITION_LEVELS_BYTE_LENGTH((short)5, "definition_levels_byte_length"), 
        REPETITION_LEVELS_BYTE_LENGTH((short)6, "repetition_levels_byte_length"), 
        IS_COMPRESSED((short)7, "is_compressed"), 
        STATISTICS((short)8, "statistics");
        
        private static final Map<String, _Fields> byName;
        private final short _thriftId;
        private final String _fieldName;
        
        public static _Fields findByThriftId(final int fieldId) {
            switch (fieldId) {
                case 1: {
                    return _Fields.NUM_VALUES;
                }
                case 2: {
                    return _Fields.NUM_NULLS;
                }
                case 3: {
                    return _Fields.NUM_ROWS;
                }
                case 4: {
                    return _Fields.ENCODING;
                }
                case 5: {
                    return _Fields.DEFINITION_LEVELS_BYTE_LENGTH;
                }
                case 6: {
                    return _Fields.REPETITION_LEVELS_BYTE_LENGTH;
                }
                case 7: {
                    return _Fields.IS_COMPRESSED;
                }
                case 8: {
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
