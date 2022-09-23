// 
// Decompiled by Procyon v0.5.36
// 

package parquet.format;

import java.util.Iterator;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Collections;
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

public class DictionaryPageHeader implements TBase<DictionaryPageHeader, _Fields>, Serializable, Cloneable
{
    private static final TStruct STRUCT_DESC;
    private static final TField NUM_VALUES_FIELD_DESC;
    private static final TField ENCODING_FIELD_DESC;
    private static final TField IS_SORTED_FIELD_DESC;
    public int num_values;
    public Encoding encoding;
    public boolean is_sorted;
    private static final int __NUM_VALUES_ISSET_ID = 0;
    private static final int __IS_SORTED_ISSET_ID = 1;
    private BitSet __isset_bit_vector;
    public static final Map<_Fields, FieldMetaData> metaDataMap;
    
    public DictionaryPageHeader() {
        this.__isset_bit_vector = new BitSet(2);
    }
    
    public DictionaryPageHeader(final int num_values, final Encoding encoding) {
        this();
        this.num_values = num_values;
        this.setNum_valuesIsSet(true);
        this.encoding = encoding;
    }
    
    public DictionaryPageHeader(final DictionaryPageHeader other) {
        (this.__isset_bit_vector = new BitSet(2)).clear();
        this.__isset_bit_vector.or(other.__isset_bit_vector);
        this.num_values = other.num_values;
        if (other.isSetEncoding()) {
            this.encoding = other.encoding;
        }
        this.is_sorted = other.is_sorted;
    }
    
    @Override
    public DictionaryPageHeader deepCopy() {
        return new DictionaryPageHeader(this);
    }
    
    @Override
    public void clear() {
        this.setNum_valuesIsSet(false);
        this.num_values = 0;
        this.encoding = null;
        this.setIs_sortedIsSet(false);
        this.is_sorted = false;
    }
    
    public int getNum_values() {
        return this.num_values;
    }
    
    public DictionaryPageHeader setNum_values(final int num_values) {
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
    
    public DictionaryPageHeader setEncoding(final Encoding encoding) {
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
    
    public boolean isIs_sorted() {
        return this.is_sorted;
    }
    
    public DictionaryPageHeader setIs_sorted(final boolean is_sorted) {
        this.is_sorted = is_sorted;
        this.setIs_sortedIsSet(true);
        return this;
    }
    
    public void unsetIs_sorted() {
        this.__isset_bit_vector.clear(1);
    }
    
    public boolean isSetIs_sorted() {
        return this.__isset_bit_vector.get(1);
    }
    
    public void setIs_sortedIsSet(final boolean value) {
        this.__isset_bit_vector.set(1, value);
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
            case IS_SORTED: {
                if (value == null) {
                    this.unsetIs_sorted();
                    break;
                }
                this.setIs_sorted((boolean)value);
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
            case IS_SORTED: {
                return new Boolean(this.isIs_sorted());
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
            case IS_SORTED: {
                return this.isSetIs_sorted();
            }
            default: {
                throw new IllegalStateException();
            }
        }
    }
    
    @Override
    public boolean equals(final Object that) {
        return that != null && that instanceof DictionaryPageHeader && this.equals((DictionaryPageHeader)that);
    }
    
    public boolean equals(final DictionaryPageHeader that) {
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
        final boolean this_present_is_sorted = this.isSetIs_sorted();
        final boolean that_present_is_sorted = that.isSetIs_sorted();
        if (this_present_is_sorted || that_present_is_sorted) {
            if (!this_present_is_sorted || !that_present_is_sorted) {
                return false;
            }
            if (this.is_sorted != that.is_sorted) {
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
        final boolean present_is_sorted = this.isSetIs_sorted();
        builder.append(present_is_sorted);
        if (present_is_sorted) {
            builder.append(this.is_sorted);
        }
        return builder.toHashCode();
    }
    
    @Override
    public int compareTo(final DictionaryPageHeader other) {
        if (!this.getClass().equals(other.getClass())) {
            return this.getClass().getName().compareTo(other.getClass().getName());
        }
        int lastComparison = 0;
        final DictionaryPageHeader typedOther = other;
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
        lastComparison = Boolean.valueOf(this.isSetIs_sorted()).compareTo(Boolean.valueOf(typedOther.isSetIs_sorted()));
        if (lastComparison != 0) {
            return lastComparison;
        }
        if (this.isSetIs_sorted()) {
            lastComparison = TBaseHelper.compareTo(this.is_sorted, typedOther.is_sorted);
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
                    if (field.type == 2) {
                        this.is_sorted = iprot.readBool();
                        this.setIs_sortedIsSet(true);
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
        oprot.writeStructBegin(DictionaryPageHeader.STRUCT_DESC);
        oprot.writeFieldBegin(DictionaryPageHeader.NUM_VALUES_FIELD_DESC);
        oprot.writeI32(this.num_values);
        oprot.writeFieldEnd();
        if (this.encoding != null) {
            oprot.writeFieldBegin(DictionaryPageHeader.ENCODING_FIELD_DESC);
            oprot.writeI32(this.encoding.getValue());
            oprot.writeFieldEnd();
        }
        if (this.isSetIs_sorted()) {
            oprot.writeFieldBegin(DictionaryPageHeader.IS_SORTED_FIELD_DESC);
            oprot.writeBool(this.is_sorted);
            oprot.writeFieldEnd();
        }
        oprot.writeFieldStop();
        oprot.writeStructEnd();
    }
    
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("DictionaryPageHeader(");
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
        if (this.isSetIs_sorted()) {
            if (!first) {
                sb.append(", ");
            }
            sb.append("is_sorted:");
            sb.append(this.is_sorted);
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
        STRUCT_DESC = new TStruct("DictionaryPageHeader");
        NUM_VALUES_FIELD_DESC = new TField("num_values", (byte)8, (short)1);
        ENCODING_FIELD_DESC = new TField("encoding", (byte)8, (short)2);
        IS_SORTED_FIELD_DESC = new TField("is_sorted", (byte)2, (short)3);
        final Map<_Fields, FieldMetaData> tmpMap = new EnumMap<_Fields, FieldMetaData>(_Fields.class);
        tmpMap.put(_Fields.NUM_VALUES, new FieldMetaData("num_values", (byte)1, new FieldValueMetaData((byte)8)));
        tmpMap.put(_Fields.ENCODING, new FieldMetaData("encoding", (byte)1, new EnumMetaData((byte)16, Encoding.class)));
        tmpMap.put(_Fields.IS_SORTED, new FieldMetaData("is_sorted", (byte)2, new FieldValueMetaData((byte)2)));
        FieldMetaData.addStructMetaDataMap(DictionaryPageHeader.class, metaDataMap = Collections.unmodifiableMap((Map<? extends _Fields, ? extends FieldMetaData>)tmpMap));
    }
    
    public enum _Fields implements TFieldIdEnum
    {
        NUM_VALUES((short)1, "num_values"), 
        ENCODING((short)2, "encoding"), 
        IS_SORTED((short)3, "is_sorted");
        
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
                    return _Fields.IS_SORTED;
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
