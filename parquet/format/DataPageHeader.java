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

public class DataPageHeader implements TBase<DataPageHeader, _Fields>, Serializable, Cloneable
{
    private static final TStruct STRUCT_DESC;
    private static final TField NUM_VALUES_FIELD_DESC;
    private static final TField ENCODING_FIELD_DESC;
    private static final TField DEFINITION_LEVEL_ENCODING_FIELD_DESC;
    private static final TField REPETITION_LEVEL_ENCODING_FIELD_DESC;
    private static final TField STATISTICS_FIELD_DESC;
    public int num_values;
    public Encoding encoding;
    public Encoding definition_level_encoding;
    public Encoding repetition_level_encoding;
    public Statistics statistics;
    private static final int __NUM_VALUES_ISSET_ID = 0;
    private BitSet __isset_bit_vector;
    public static final Map<_Fields, FieldMetaData> metaDataMap;
    
    public DataPageHeader() {
        this.__isset_bit_vector = new BitSet(1);
    }
    
    public DataPageHeader(final int num_values, final Encoding encoding, final Encoding definition_level_encoding, final Encoding repetition_level_encoding) {
        this();
        this.num_values = num_values;
        this.setNum_valuesIsSet(true);
        this.encoding = encoding;
        this.definition_level_encoding = definition_level_encoding;
        this.repetition_level_encoding = repetition_level_encoding;
    }
    
    public DataPageHeader(final DataPageHeader other) {
        (this.__isset_bit_vector = new BitSet(1)).clear();
        this.__isset_bit_vector.or(other.__isset_bit_vector);
        this.num_values = other.num_values;
        if (other.isSetEncoding()) {
            this.encoding = other.encoding;
        }
        if (other.isSetDefinition_level_encoding()) {
            this.definition_level_encoding = other.definition_level_encoding;
        }
        if (other.isSetRepetition_level_encoding()) {
            this.repetition_level_encoding = other.repetition_level_encoding;
        }
        if (other.isSetStatistics()) {
            this.statistics = new Statistics(other.statistics);
        }
    }
    
    @Override
    public DataPageHeader deepCopy() {
        return new DataPageHeader(this);
    }
    
    @Override
    public void clear() {
        this.setNum_valuesIsSet(false);
        this.num_values = 0;
        this.encoding = null;
        this.definition_level_encoding = null;
        this.repetition_level_encoding = null;
        this.statistics = null;
    }
    
    public int getNum_values() {
        return this.num_values;
    }
    
    public DataPageHeader setNum_values(final int num_values) {
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
    
    public Encoding getEncoding() {
        return this.encoding;
    }
    
    public DataPageHeader setEncoding(final Encoding encoding) {
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
    
    public Encoding getDefinition_level_encoding() {
        return this.definition_level_encoding;
    }
    
    public DataPageHeader setDefinition_level_encoding(final Encoding definition_level_encoding) {
        this.definition_level_encoding = definition_level_encoding;
        return this;
    }
    
    public void unsetDefinition_level_encoding() {
        this.definition_level_encoding = null;
    }
    
    public boolean isSetDefinition_level_encoding() {
        return this.definition_level_encoding != null;
    }
    
    public void setDefinition_level_encodingIsSet(final boolean value) {
        if (!value) {
            this.definition_level_encoding = null;
        }
    }
    
    public Encoding getRepetition_level_encoding() {
        return this.repetition_level_encoding;
    }
    
    public DataPageHeader setRepetition_level_encoding(final Encoding repetition_level_encoding) {
        this.repetition_level_encoding = repetition_level_encoding;
        return this;
    }
    
    public void unsetRepetition_level_encoding() {
        this.repetition_level_encoding = null;
    }
    
    public boolean isSetRepetition_level_encoding() {
        return this.repetition_level_encoding != null;
    }
    
    public void setRepetition_level_encodingIsSet(final boolean value) {
        if (!value) {
            this.repetition_level_encoding = null;
        }
    }
    
    public Statistics getStatistics() {
        return this.statistics;
    }
    
    public DataPageHeader setStatistics(final Statistics statistics) {
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
            case ENCODING: {
                if (value == null) {
                    this.unsetEncoding();
                    break;
                }
                this.setEncoding((Encoding)value);
                break;
            }
            case DEFINITION_LEVEL_ENCODING: {
                if (value == null) {
                    this.unsetDefinition_level_encoding();
                    break;
                }
                this.setDefinition_level_encoding((Encoding)value);
                break;
            }
            case REPETITION_LEVEL_ENCODING: {
                if (value == null) {
                    this.unsetRepetition_level_encoding();
                    break;
                }
                this.setRepetition_level_encoding((Encoding)value);
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
            case ENCODING: {
                return this.getEncoding();
            }
            case DEFINITION_LEVEL_ENCODING: {
                return this.getDefinition_level_encoding();
            }
            case REPETITION_LEVEL_ENCODING: {
                return this.getRepetition_level_encoding();
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
            case ENCODING: {
                return this.isSetEncoding();
            }
            case DEFINITION_LEVEL_ENCODING: {
                return this.isSetDefinition_level_encoding();
            }
            case REPETITION_LEVEL_ENCODING: {
                return this.isSetRepetition_level_encoding();
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
        return that != null && that instanceof DataPageHeader && this.equals((DataPageHeader)that);
    }
    
    public boolean equals(final DataPageHeader that) {
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
        final boolean this_present_definition_level_encoding = this.isSetDefinition_level_encoding();
        final boolean that_present_definition_level_encoding = that.isSetDefinition_level_encoding();
        if (this_present_definition_level_encoding || that_present_definition_level_encoding) {
            if (!this_present_definition_level_encoding || !that_present_definition_level_encoding) {
                return false;
            }
            if (!this.definition_level_encoding.equals(that.definition_level_encoding)) {
                return false;
            }
        }
        final boolean this_present_repetition_level_encoding = this.isSetRepetition_level_encoding();
        final boolean that_present_repetition_level_encoding = that.isSetRepetition_level_encoding();
        if (this_present_repetition_level_encoding || that_present_repetition_level_encoding) {
            if (!this_present_repetition_level_encoding || !that_present_repetition_level_encoding) {
                return false;
            }
            if (!this.repetition_level_encoding.equals(that.repetition_level_encoding)) {
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
        final boolean present_encoding = this.isSetEncoding();
        builder.append(present_encoding);
        if (present_encoding) {
            builder.append(this.encoding.getValue());
        }
        final boolean present_definition_level_encoding = this.isSetDefinition_level_encoding();
        builder.append(present_definition_level_encoding);
        if (present_definition_level_encoding) {
            builder.append(this.definition_level_encoding.getValue());
        }
        final boolean present_repetition_level_encoding = this.isSetRepetition_level_encoding();
        builder.append(present_repetition_level_encoding);
        if (present_repetition_level_encoding) {
            builder.append(this.repetition_level_encoding.getValue());
        }
        final boolean present_statistics = this.isSetStatistics();
        builder.append(present_statistics);
        if (present_statistics) {
            builder.append(this.statistics);
        }
        return builder.toHashCode();
    }
    
    @Override
    public int compareTo(final DataPageHeader other) {
        if (!this.getClass().equals(other.getClass())) {
            return this.getClass().getName().compareTo(other.getClass().getName());
        }
        int lastComparison = 0;
        final DataPageHeader typedOther = other;
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
        lastComparison = Boolean.valueOf(this.isSetDefinition_level_encoding()).compareTo(Boolean.valueOf(typedOther.isSetDefinition_level_encoding()));
        if (lastComparison != 0) {
            return lastComparison;
        }
        if (this.isSetDefinition_level_encoding()) {
            lastComparison = TBaseHelper.compareTo(this.definition_level_encoding, typedOther.definition_level_encoding);
            if (lastComparison != 0) {
                return lastComparison;
            }
        }
        lastComparison = Boolean.valueOf(this.isSetRepetition_level_encoding()).compareTo(Boolean.valueOf(typedOther.isSetRepetition_level_encoding()));
        if (lastComparison != 0) {
            return lastComparison;
        }
        if (this.isSetRepetition_level_encoding()) {
            lastComparison = TBaseHelper.compareTo(this.repetition_level_encoding, typedOther.repetition_level_encoding);
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
                        this.encoding = Encoding.findByValue(iprot.readI32());
                        break;
                    }
                    TProtocolUtil.skip(iprot, field.type);
                    break;
                }
                case 3: {
                    if (field.type == 8) {
                        this.definition_level_encoding = Encoding.findByValue(iprot.readI32());
                        break;
                    }
                    TProtocolUtil.skip(iprot, field.type);
                    break;
                }
                case 4: {
                    if (field.type == 8) {
                        this.repetition_level_encoding = Encoding.findByValue(iprot.readI32());
                        break;
                    }
                    TProtocolUtil.skip(iprot, field.type);
                    break;
                }
                case 5: {
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
        this.validate();
    }
    
    @Override
    public void write(final TProtocol oprot) throws TException {
        this.validate();
        oprot.writeStructBegin(DataPageHeader.STRUCT_DESC);
        oprot.writeFieldBegin(DataPageHeader.NUM_VALUES_FIELD_DESC);
        oprot.writeI32(this.num_values);
        oprot.writeFieldEnd();
        if (this.encoding != null) {
            oprot.writeFieldBegin(DataPageHeader.ENCODING_FIELD_DESC);
            oprot.writeI32(this.encoding.getValue());
            oprot.writeFieldEnd();
        }
        if (this.definition_level_encoding != null) {
            oprot.writeFieldBegin(DataPageHeader.DEFINITION_LEVEL_ENCODING_FIELD_DESC);
            oprot.writeI32(this.definition_level_encoding.getValue());
            oprot.writeFieldEnd();
        }
        if (this.repetition_level_encoding != null) {
            oprot.writeFieldBegin(DataPageHeader.REPETITION_LEVEL_ENCODING_FIELD_DESC);
            oprot.writeI32(this.repetition_level_encoding.getValue());
            oprot.writeFieldEnd();
        }
        if (this.statistics != null && this.isSetStatistics()) {
            oprot.writeFieldBegin(DataPageHeader.STATISTICS_FIELD_DESC);
            this.statistics.write(oprot);
            oprot.writeFieldEnd();
        }
        oprot.writeFieldStop();
        oprot.writeStructEnd();
    }
    
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("DataPageHeader(");
        boolean first = true;
        sb.append("num_values:");
        sb.append(this.num_values);
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
        sb.append("definition_level_encoding:");
        if (this.definition_level_encoding == null) {
            sb.append("null");
        }
        else {
            sb.append(this.definition_level_encoding);
        }
        first = false;
        if (!first) {
            sb.append(", ");
        }
        sb.append("repetition_level_encoding:");
        if (this.repetition_level_encoding == null) {
            sb.append("null");
        }
        else {
            sb.append(this.repetition_level_encoding);
        }
        first = false;
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
        if (this.definition_level_encoding == null) {
            throw new TProtocolException("Required field 'definition_level_encoding' was not present! Struct: " + this.toString());
        }
        if (this.repetition_level_encoding == null) {
            throw new TProtocolException("Required field 'repetition_level_encoding' was not present! Struct: " + this.toString());
        }
    }
    
    static {
        STRUCT_DESC = new TStruct("DataPageHeader");
        NUM_VALUES_FIELD_DESC = new TField("num_values", (byte)8, (short)1);
        ENCODING_FIELD_DESC = new TField("encoding", (byte)8, (short)2);
        DEFINITION_LEVEL_ENCODING_FIELD_DESC = new TField("definition_level_encoding", (byte)8, (short)3);
        REPETITION_LEVEL_ENCODING_FIELD_DESC = new TField("repetition_level_encoding", (byte)8, (short)4);
        STATISTICS_FIELD_DESC = new TField("statistics", (byte)12, (short)5);
        final Map<_Fields, FieldMetaData> tmpMap = new EnumMap<_Fields, FieldMetaData>(_Fields.class);
        tmpMap.put(_Fields.NUM_VALUES, new FieldMetaData("num_values", (byte)1, new FieldValueMetaData((byte)8)));
        tmpMap.put(_Fields.ENCODING, new FieldMetaData("encoding", (byte)1, new EnumMetaData((byte)16, Encoding.class)));
        tmpMap.put(_Fields.DEFINITION_LEVEL_ENCODING, new FieldMetaData("definition_level_encoding", (byte)1, new EnumMetaData((byte)16, Encoding.class)));
        tmpMap.put(_Fields.REPETITION_LEVEL_ENCODING, new FieldMetaData("repetition_level_encoding", (byte)1, new EnumMetaData((byte)16, Encoding.class)));
        tmpMap.put(_Fields.STATISTICS, new FieldMetaData("statistics", (byte)2, new StructMetaData((byte)12, Statistics.class)));
        FieldMetaData.addStructMetaDataMap(DataPageHeader.class, metaDataMap = Collections.unmodifiableMap((Map<? extends _Fields, ? extends FieldMetaData>)tmpMap));
    }
    
    public enum _Fields implements TFieldIdEnum
    {
        NUM_VALUES((short)1, "num_values"), 
        ENCODING((short)2, "encoding"), 
        DEFINITION_LEVEL_ENCODING((short)3, "definition_level_encoding"), 
        REPETITION_LEVEL_ENCODING((short)4, "repetition_level_encoding"), 
        STATISTICS((short)5, "statistics");
        
        private static final Map<String, _Fields> byName;
        private final short _thriftId;
        private final String _fieldName;
        
        public static _Fields findByThriftId(final int fieldId) {
            switch (fieldId) {
                case 1: {
                    return _Fields.NUM_VALUES;
                }
                case 2: {
                    return _Fields.ENCODING;
                }
                case 3: {
                    return _Fields.DEFINITION_LEVEL_ENCODING;
                }
                case 4: {
                    return _Fields.REPETITION_LEVEL_ENCODING;
                }
                case 5: {
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
