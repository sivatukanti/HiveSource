// 
// Decompiled by Procyon v0.5.36
// 

package parquet.format;

import java.util.Iterator;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Collections;
import parquet.org.apache.thrift.meta_data.FieldValueMetaData;
import java.util.EnumMap;
import parquet.org.apache.thrift.TFieldIdEnum;
import parquet.org.apache.thrift.TException;
import parquet.org.apache.thrift.protocol.TProtocolUtil;
import parquet.org.apache.thrift.protocol.TProtocol;
import org.apache.commons.lang.builder.HashCodeBuilder;
import parquet.org.apache.thrift.TBaseHelper;
import parquet.org.apache.thrift.meta_data.FieldMetaData;
import java.util.Map;
import java.util.BitSet;
import java.nio.ByteBuffer;
import parquet.org.apache.thrift.protocol.TField;
import parquet.org.apache.thrift.protocol.TStruct;
import java.io.Serializable;
import parquet.org.apache.thrift.TBase;

public class Statistics implements TBase<Statistics, _Fields>, Serializable, Cloneable
{
    private static final TStruct STRUCT_DESC;
    private static final TField MAX_FIELD_DESC;
    private static final TField MIN_FIELD_DESC;
    private static final TField NULL_COUNT_FIELD_DESC;
    private static final TField DISTINCT_COUNT_FIELD_DESC;
    public ByteBuffer max;
    public ByteBuffer min;
    public long null_count;
    public long distinct_count;
    private static final int __NULL_COUNT_ISSET_ID = 0;
    private static final int __DISTINCT_COUNT_ISSET_ID = 1;
    private BitSet __isset_bit_vector;
    public static final Map<_Fields, FieldMetaData> metaDataMap;
    
    public Statistics() {
        this.__isset_bit_vector = new BitSet(2);
    }
    
    public Statistics(final Statistics other) {
        (this.__isset_bit_vector = new BitSet(2)).clear();
        this.__isset_bit_vector.or(other.__isset_bit_vector);
        if (other.isSetMax()) {
            this.max = TBaseHelper.copyBinary(other.max);
        }
        if (other.isSetMin()) {
            this.min = TBaseHelper.copyBinary(other.min);
        }
        this.null_count = other.null_count;
        this.distinct_count = other.distinct_count;
    }
    
    @Override
    public Statistics deepCopy() {
        return new Statistics(this);
    }
    
    @Override
    public void clear() {
        this.max = null;
        this.min = null;
        this.setNull_countIsSet(false);
        this.null_count = 0L;
        this.setDistinct_countIsSet(false);
        this.distinct_count = 0L;
    }
    
    public byte[] getMax() {
        this.setMax(TBaseHelper.rightSize(this.max));
        return this.max.array();
    }
    
    public ByteBuffer BufferForMax() {
        return this.max;
    }
    
    public Statistics setMax(final byte[] max) {
        this.setMax(ByteBuffer.wrap(max));
        return this;
    }
    
    public Statistics setMax(final ByteBuffer max) {
        this.max = max;
        return this;
    }
    
    public void unsetMax() {
        this.max = null;
    }
    
    public boolean isSetMax() {
        return this.max != null;
    }
    
    public void setMaxIsSet(final boolean value) {
        if (!value) {
            this.max = null;
        }
    }
    
    public byte[] getMin() {
        this.setMin(TBaseHelper.rightSize(this.min));
        return this.min.array();
    }
    
    public ByteBuffer BufferForMin() {
        return this.min;
    }
    
    public Statistics setMin(final byte[] min) {
        this.setMin(ByteBuffer.wrap(min));
        return this;
    }
    
    public Statistics setMin(final ByteBuffer min) {
        this.min = min;
        return this;
    }
    
    public void unsetMin() {
        this.min = null;
    }
    
    public boolean isSetMin() {
        return this.min != null;
    }
    
    public void setMinIsSet(final boolean value) {
        if (!value) {
            this.min = null;
        }
    }
    
    public long getNull_count() {
        return this.null_count;
    }
    
    public Statistics setNull_count(final long null_count) {
        this.null_count = null_count;
        this.setNull_countIsSet(true);
        return this;
    }
    
    public void unsetNull_count() {
        this.__isset_bit_vector.clear(0);
    }
    
    public boolean isSetNull_count() {
        return this.__isset_bit_vector.get(0);
    }
    
    public void setNull_countIsSet(final boolean value) {
        this.__isset_bit_vector.set(0, value);
    }
    
    public long getDistinct_count() {
        return this.distinct_count;
    }
    
    public Statistics setDistinct_count(final long distinct_count) {
        this.distinct_count = distinct_count;
        this.setDistinct_countIsSet(true);
        return this;
    }
    
    public void unsetDistinct_count() {
        this.__isset_bit_vector.clear(1);
    }
    
    public boolean isSetDistinct_count() {
        return this.__isset_bit_vector.get(1);
    }
    
    public void setDistinct_countIsSet(final boolean value) {
        this.__isset_bit_vector.set(1, value);
    }
    
    @Override
    public void setFieldValue(final _Fields field, final Object value) {
        switch (field) {
            case MAX: {
                if (value == null) {
                    this.unsetMax();
                    break;
                }
                this.setMax((ByteBuffer)value);
                break;
            }
            case MIN: {
                if (value == null) {
                    this.unsetMin();
                    break;
                }
                this.setMin((ByteBuffer)value);
                break;
            }
            case NULL_COUNT: {
                if (value == null) {
                    this.unsetNull_count();
                    break;
                }
                this.setNull_count((long)value);
                break;
            }
            case DISTINCT_COUNT: {
                if (value == null) {
                    this.unsetDistinct_count();
                    break;
                }
                this.setDistinct_count((long)value);
                break;
            }
        }
    }
    
    @Override
    public Object getFieldValue(final _Fields field) {
        switch (field) {
            case MAX: {
                return this.getMax();
            }
            case MIN: {
                return this.getMin();
            }
            case NULL_COUNT: {
                return new Long(this.getNull_count());
            }
            case DISTINCT_COUNT: {
                return new Long(this.getDistinct_count());
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
            case MAX: {
                return this.isSetMax();
            }
            case MIN: {
                return this.isSetMin();
            }
            case NULL_COUNT: {
                return this.isSetNull_count();
            }
            case DISTINCT_COUNT: {
                return this.isSetDistinct_count();
            }
            default: {
                throw new IllegalStateException();
            }
        }
    }
    
    @Override
    public boolean equals(final Object that) {
        return that != null && that instanceof Statistics && this.equals((Statistics)that);
    }
    
    public boolean equals(final Statistics that) {
        if (that == null) {
            return false;
        }
        final boolean this_present_max = this.isSetMax();
        final boolean that_present_max = that.isSetMax();
        if (this_present_max || that_present_max) {
            if (!this_present_max || !that_present_max) {
                return false;
            }
            if (!this.max.equals(that.max)) {
                return false;
            }
        }
        final boolean this_present_min = this.isSetMin();
        final boolean that_present_min = that.isSetMin();
        if (this_present_min || that_present_min) {
            if (!this_present_min || !that_present_min) {
                return false;
            }
            if (!this.min.equals(that.min)) {
                return false;
            }
        }
        final boolean this_present_null_count = this.isSetNull_count();
        final boolean that_present_null_count = that.isSetNull_count();
        if (this_present_null_count || that_present_null_count) {
            if (!this_present_null_count || !that_present_null_count) {
                return false;
            }
            if (this.null_count != that.null_count) {
                return false;
            }
        }
        final boolean this_present_distinct_count = this.isSetDistinct_count();
        final boolean that_present_distinct_count = that.isSetDistinct_count();
        if (this_present_distinct_count || that_present_distinct_count) {
            if (!this_present_distinct_count || !that_present_distinct_count) {
                return false;
            }
            if (this.distinct_count != that.distinct_count) {
                return false;
            }
        }
        return true;
    }
    
    @Override
    public int hashCode() {
        final HashCodeBuilder builder = new HashCodeBuilder();
        final boolean present_max = this.isSetMax();
        builder.append(present_max);
        if (present_max) {
            builder.append(this.max);
        }
        final boolean present_min = this.isSetMin();
        builder.append(present_min);
        if (present_min) {
            builder.append(this.min);
        }
        final boolean present_null_count = this.isSetNull_count();
        builder.append(present_null_count);
        if (present_null_count) {
            builder.append(this.null_count);
        }
        final boolean present_distinct_count = this.isSetDistinct_count();
        builder.append(present_distinct_count);
        if (present_distinct_count) {
            builder.append(this.distinct_count);
        }
        return builder.toHashCode();
    }
    
    @Override
    public int compareTo(final Statistics other) {
        if (!this.getClass().equals(other.getClass())) {
            return this.getClass().getName().compareTo(other.getClass().getName());
        }
        int lastComparison = 0;
        final Statistics typedOther = other;
        lastComparison = Boolean.valueOf(this.isSetMax()).compareTo(Boolean.valueOf(typedOther.isSetMax()));
        if (lastComparison != 0) {
            return lastComparison;
        }
        if (this.isSetMax()) {
            lastComparison = TBaseHelper.compareTo(this.max, typedOther.max);
            if (lastComparison != 0) {
                return lastComparison;
            }
        }
        lastComparison = Boolean.valueOf(this.isSetMin()).compareTo(Boolean.valueOf(typedOther.isSetMin()));
        if (lastComparison != 0) {
            return lastComparison;
        }
        if (this.isSetMin()) {
            lastComparison = TBaseHelper.compareTo(this.min, typedOther.min);
            if (lastComparison != 0) {
                return lastComparison;
            }
        }
        lastComparison = Boolean.valueOf(this.isSetNull_count()).compareTo(Boolean.valueOf(typedOther.isSetNull_count()));
        if (lastComparison != 0) {
            return lastComparison;
        }
        if (this.isSetNull_count()) {
            lastComparison = TBaseHelper.compareTo(this.null_count, typedOther.null_count);
            if (lastComparison != 0) {
                return lastComparison;
            }
        }
        lastComparison = Boolean.valueOf(this.isSetDistinct_count()).compareTo(Boolean.valueOf(typedOther.isSetDistinct_count()));
        if (lastComparison != 0) {
            return lastComparison;
        }
        if (this.isSetDistinct_count()) {
            lastComparison = TBaseHelper.compareTo(this.distinct_count, typedOther.distinct_count);
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
                        this.max = iprot.readBinary();
                        break;
                    }
                    TProtocolUtil.skip(iprot, field.type);
                    break;
                }
                case 2: {
                    if (field.type == 11) {
                        this.min = iprot.readBinary();
                        break;
                    }
                    TProtocolUtil.skip(iprot, field.type);
                    break;
                }
                case 3: {
                    if (field.type == 10) {
                        this.null_count = iprot.readI64();
                        this.setNull_countIsSet(true);
                        break;
                    }
                    TProtocolUtil.skip(iprot, field.type);
                    break;
                }
                case 4: {
                    if (field.type == 10) {
                        this.distinct_count = iprot.readI64();
                        this.setDistinct_countIsSet(true);
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
        this.validate();
    }
    
    @Override
    public void write(final TProtocol oprot) throws TException {
        this.validate();
        oprot.writeStructBegin(Statistics.STRUCT_DESC);
        if (this.max != null && this.isSetMax()) {
            oprot.writeFieldBegin(Statistics.MAX_FIELD_DESC);
            oprot.writeBinary(this.max);
            oprot.writeFieldEnd();
        }
        if (this.min != null && this.isSetMin()) {
            oprot.writeFieldBegin(Statistics.MIN_FIELD_DESC);
            oprot.writeBinary(this.min);
            oprot.writeFieldEnd();
        }
        if (this.isSetNull_count()) {
            oprot.writeFieldBegin(Statistics.NULL_COUNT_FIELD_DESC);
            oprot.writeI64(this.null_count);
            oprot.writeFieldEnd();
        }
        if (this.isSetDistinct_count()) {
            oprot.writeFieldBegin(Statistics.DISTINCT_COUNT_FIELD_DESC);
            oprot.writeI64(this.distinct_count);
            oprot.writeFieldEnd();
        }
        oprot.writeFieldStop();
        oprot.writeStructEnd();
    }
    
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("Statistics(");
        boolean first = true;
        if (this.isSetMax()) {
            sb.append("max:");
            if (this.max == null) {
                sb.append("null");
            }
            else {
                TBaseHelper.toString(this.max, sb);
            }
            first = false;
        }
        if (this.isSetMin()) {
            if (!first) {
                sb.append(", ");
            }
            sb.append("min:");
            if (this.min == null) {
                sb.append("null");
            }
            else {
                TBaseHelper.toString(this.min, sb);
            }
            first = false;
        }
        if (this.isSetNull_count()) {
            if (!first) {
                sb.append(", ");
            }
            sb.append("null_count:");
            sb.append(this.null_count);
            first = false;
        }
        if (this.isSetDistinct_count()) {
            if (!first) {
                sb.append(", ");
            }
            sb.append("distinct_count:");
            sb.append(this.distinct_count);
            first = false;
        }
        sb.append(")");
        return sb.toString();
    }
    
    public void validate() throws TException {
    }
    
    static {
        STRUCT_DESC = new TStruct("Statistics");
        MAX_FIELD_DESC = new TField("max", (byte)11, (short)1);
        MIN_FIELD_DESC = new TField("min", (byte)11, (short)2);
        NULL_COUNT_FIELD_DESC = new TField("null_count", (byte)10, (short)3);
        DISTINCT_COUNT_FIELD_DESC = new TField("distinct_count", (byte)10, (short)4);
        final Map<_Fields, FieldMetaData> tmpMap = new EnumMap<_Fields, FieldMetaData>(_Fields.class);
        tmpMap.put(_Fields.MAX, new FieldMetaData("max", (byte)2, new FieldValueMetaData((byte)11)));
        tmpMap.put(_Fields.MIN, new FieldMetaData("min", (byte)2, new FieldValueMetaData((byte)11)));
        tmpMap.put(_Fields.NULL_COUNT, new FieldMetaData("null_count", (byte)2, new FieldValueMetaData((byte)10)));
        tmpMap.put(_Fields.DISTINCT_COUNT, new FieldMetaData("distinct_count", (byte)2, new FieldValueMetaData((byte)10)));
        FieldMetaData.addStructMetaDataMap(Statistics.class, metaDataMap = Collections.unmodifiableMap((Map<? extends _Fields, ? extends FieldMetaData>)tmpMap));
    }
    
    public enum _Fields implements TFieldIdEnum
    {
        MAX((short)1, "max"), 
        MIN((short)2, "min"), 
        NULL_COUNT((short)3, "null_count"), 
        DISTINCT_COUNT((short)4, "distinct_count");
        
        private static final Map<String, _Fields> byName;
        private final short _thriftId;
        private final String _fieldName;
        
        public static _Fields findByThriftId(final int fieldId) {
            switch (fieldId) {
                case 1: {
                    return _Fields.MAX;
                }
                case 2: {
                    return _Fields.MIN;
                }
                case 3: {
                    return _Fields.NULL_COUNT;
                }
                case 4: {
                    return _Fields.DISTINCT_COUNT;
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
