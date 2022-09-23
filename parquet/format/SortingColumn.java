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

public class SortingColumn implements TBase<SortingColumn, _Fields>, Serializable, Cloneable
{
    private static final TStruct STRUCT_DESC;
    private static final TField COLUMN_IDX_FIELD_DESC;
    private static final TField DESCENDING_FIELD_DESC;
    private static final TField NULLS_FIRST_FIELD_DESC;
    public int column_idx;
    public boolean descending;
    public boolean nulls_first;
    private static final int __COLUMN_IDX_ISSET_ID = 0;
    private static final int __DESCENDING_ISSET_ID = 1;
    private static final int __NULLS_FIRST_ISSET_ID = 2;
    private BitSet __isset_bit_vector;
    public static final Map<_Fields, FieldMetaData> metaDataMap;
    
    public SortingColumn() {
        this.__isset_bit_vector = new BitSet(3);
    }
    
    public SortingColumn(final int column_idx, final boolean descending, final boolean nulls_first) {
        this();
        this.column_idx = column_idx;
        this.setColumn_idxIsSet(true);
        this.descending = descending;
        this.setDescendingIsSet(true);
        this.nulls_first = nulls_first;
        this.setNulls_firstIsSet(true);
    }
    
    public SortingColumn(final SortingColumn other) {
        (this.__isset_bit_vector = new BitSet(3)).clear();
        this.__isset_bit_vector.or(other.__isset_bit_vector);
        this.column_idx = other.column_idx;
        this.descending = other.descending;
        this.nulls_first = other.nulls_first;
    }
    
    @Override
    public SortingColumn deepCopy() {
        return new SortingColumn(this);
    }
    
    @Override
    public void clear() {
        this.setColumn_idxIsSet(false);
        this.column_idx = 0;
        this.setDescendingIsSet(false);
        this.setNulls_firstIsSet(this.descending = false);
        this.nulls_first = false;
    }
    
    public int getColumn_idx() {
        return this.column_idx;
    }
    
    public SortingColumn setColumn_idx(final int column_idx) {
        this.column_idx = column_idx;
        this.setColumn_idxIsSet(true);
        return this;
    }
    
    public void unsetColumn_idx() {
        this.__isset_bit_vector.clear(0);
    }
    
    public boolean isSetColumn_idx() {
        return this.__isset_bit_vector.get(0);
    }
    
    public void setColumn_idxIsSet(final boolean value) {
        this.__isset_bit_vector.set(0, value);
    }
    
    public boolean isDescending() {
        return this.descending;
    }
    
    public SortingColumn setDescending(final boolean descending) {
        this.descending = descending;
        this.setDescendingIsSet(true);
        return this;
    }
    
    public void unsetDescending() {
        this.__isset_bit_vector.clear(1);
    }
    
    public boolean isSetDescending() {
        return this.__isset_bit_vector.get(1);
    }
    
    public void setDescendingIsSet(final boolean value) {
        this.__isset_bit_vector.set(1, value);
    }
    
    public boolean isNulls_first() {
        return this.nulls_first;
    }
    
    public SortingColumn setNulls_first(final boolean nulls_first) {
        this.nulls_first = nulls_first;
        this.setNulls_firstIsSet(true);
        return this;
    }
    
    public void unsetNulls_first() {
        this.__isset_bit_vector.clear(2);
    }
    
    public boolean isSetNulls_first() {
        return this.__isset_bit_vector.get(2);
    }
    
    public void setNulls_firstIsSet(final boolean value) {
        this.__isset_bit_vector.set(2, value);
    }
    
    @Override
    public void setFieldValue(final _Fields field, final Object value) {
        switch (field) {
            case COLUMN_IDX: {
                if (value == null) {
                    this.unsetColumn_idx();
                    break;
                }
                this.setColumn_idx((int)value);
                break;
            }
            case DESCENDING: {
                if (value == null) {
                    this.unsetDescending();
                    break;
                }
                this.setDescending((boolean)value);
                break;
            }
            case NULLS_FIRST: {
                if (value == null) {
                    this.unsetNulls_first();
                    break;
                }
                this.setNulls_first((boolean)value);
                break;
            }
        }
    }
    
    @Override
    public Object getFieldValue(final _Fields field) {
        switch (field) {
            case COLUMN_IDX: {
                return new Integer(this.getColumn_idx());
            }
            case DESCENDING: {
                return new Boolean(this.isDescending());
            }
            case NULLS_FIRST: {
                return new Boolean(this.isNulls_first());
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
            case COLUMN_IDX: {
                return this.isSetColumn_idx();
            }
            case DESCENDING: {
                return this.isSetDescending();
            }
            case NULLS_FIRST: {
                return this.isSetNulls_first();
            }
            default: {
                throw new IllegalStateException();
            }
        }
    }
    
    @Override
    public boolean equals(final Object that) {
        return that != null && that instanceof SortingColumn && this.equals((SortingColumn)that);
    }
    
    public boolean equals(final SortingColumn that) {
        if (that == null) {
            return false;
        }
        final boolean this_present_column_idx = true;
        final boolean that_present_column_idx = true;
        if (this_present_column_idx || that_present_column_idx) {
            if (!this_present_column_idx || !that_present_column_idx) {
                return false;
            }
            if (this.column_idx != that.column_idx) {
                return false;
            }
        }
        final boolean this_present_descending = true;
        final boolean that_present_descending = true;
        if (this_present_descending || that_present_descending) {
            if (!this_present_descending || !that_present_descending) {
                return false;
            }
            if (this.descending != that.descending) {
                return false;
            }
        }
        final boolean this_present_nulls_first = true;
        final boolean that_present_nulls_first = true;
        if (this_present_nulls_first || that_present_nulls_first) {
            if (!this_present_nulls_first || !that_present_nulls_first) {
                return false;
            }
            if (this.nulls_first != that.nulls_first) {
                return false;
            }
        }
        return true;
    }
    
    @Override
    public int hashCode() {
        final HashCodeBuilder builder = new HashCodeBuilder();
        final boolean present_column_idx = true;
        builder.append(present_column_idx);
        if (present_column_idx) {
            builder.append(this.column_idx);
        }
        final boolean present_descending = true;
        builder.append(present_descending);
        if (present_descending) {
            builder.append(this.descending);
        }
        final boolean present_nulls_first = true;
        builder.append(present_nulls_first);
        if (present_nulls_first) {
            builder.append(this.nulls_first);
        }
        return builder.toHashCode();
    }
    
    @Override
    public int compareTo(final SortingColumn other) {
        if (!this.getClass().equals(other.getClass())) {
            return this.getClass().getName().compareTo(other.getClass().getName());
        }
        int lastComparison = 0;
        final SortingColumn typedOther = other;
        lastComparison = Boolean.valueOf(this.isSetColumn_idx()).compareTo(Boolean.valueOf(typedOther.isSetColumn_idx()));
        if (lastComparison != 0) {
            return lastComparison;
        }
        if (this.isSetColumn_idx()) {
            lastComparison = TBaseHelper.compareTo(this.column_idx, typedOther.column_idx);
            if (lastComparison != 0) {
                return lastComparison;
            }
        }
        lastComparison = Boolean.valueOf(this.isSetDescending()).compareTo(Boolean.valueOf(typedOther.isSetDescending()));
        if (lastComparison != 0) {
            return lastComparison;
        }
        if (this.isSetDescending()) {
            lastComparison = TBaseHelper.compareTo(this.descending, typedOther.descending);
            if (lastComparison != 0) {
                return lastComparison;
            }
        }
        lastComparison = Boolean.valueOf(this.isSetNulls_first()).compareTo(Boolean.valueOf(typedOther.isSetNulls_first()));
        if (lastComparison != 0) {
            return lastComparison;
        }
        if (this.isSetNulls_first()) {
            lastComparison = TBaseHelper.compareTo(this.nulls_first, typedOther.nulls_first);
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
                        this.column_idx = iprot.readI32();
                        this.setColumn_idxIsSet(true);
                        break;
                    }
                    TProtocolUtil.skip(iprot, field.type);
                    break;
                }
                case 2: {
                    if (field.type == 2) {
                        this.descending = iprot.readBool();
                        this.setDescendingIsSet(true);
                        break;
                    }
                    TProtocolUtil.skip(iprot, field.type);
                    break;
                }
                case 3: {
                    if (field.type == 2) {
                        this.nulls_first = iprot.readBool();
                        this.setNulls_firstIsSet(true);
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
        if (!this.isSetColumn_idx()) {
            throw new TProtocolException("Required field 'column_idx' was not found in serialized data! Struct: " + this.toString());
        }
        if (!this.isSetDescending()) {
            throw new TProtocolException("Required field 'descending' was not found in serialized data! Struct: " + this.toString());
        }
        if (!this.isSetNulls_first()) {
            throw new TProtocolException("Required field 'nulls_first' was not found in serialized data! Struct: " + this.toString());
        }
        this.validate();
    }
    
    @Override
    public void write(final TProtocol oprot) throws TException {
        this.validate();
        oprot.writeStructBegin(SortingColumn.STRUCT_DESC);
        oprot.writeFieldBegin(SortingColumn.COLUMN_IDX_FIELD_DESC);
        oprot.writeI32(this.column_idx);
        oprot.writeFieldEnd();
        oprot.writeFieldBegin(SortingColumn.DESCENDING_FIELD_DESC);
        oprot.writeBool(this.descending);
        oprot.writeFieldEnd();
        oprot.writeFieldBegin(SortingColumn.NULLS_FIRST_FIELD_DESC);
        oprot.writeBool(this.nulls_first);
        oprot.writeFieldEnd();
        oprot.writeFieldStop();
        oprot.writeStructEnd();
    }
    
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("SortingColumn(");
        boolean first = true;
        sb.append("column_idx:");
        sb.append(this.column_idx);
        first = false;
        if (!first) {
            sb.append(", ");
        }
        sb.append("descending:");
        sb.append(this.descending);
        first = false;
        if (!first) {
            sb.append(", ");
        }
        sb.append("nulls_first:");
        sb.append(this.nulls_first);
        first = false;
        sb.append(")");
        return sb.toString();
    }
    
    public void validate() throws TException {
    }
    
    static {
        STRUCT_DESC = new TStruct("SortingColumn");
        COLUMN_IDX_FIELD_DESC = new TField("column_idx", (byte)8, (short)1);
        DESCENDING_FIELD_DESC = new TField("descending", (byte)2, (short)2);
        NULLS_FIRST_FIELD_DESC = new TField("nulls_first", (byte)2, (short)3);
        final Map<_Fields, FieldMetaData> tmpMap = new EnumMap<_Fields, FieldMetaData>(_Fields.class);
        tmpMap.put(_Fields.COLUMN_IDX, new FieldMetaData("column_idx", (byte)1, new FieldValueMetaData((byte)8)));
        tmpMap.put(_Fields.DESCENDING, new FieldMetaData("descending", (byte)1, new FieldValueMetaData((byte)2)));
        tmpMap.put(_Fields.NULLS_FIRST, new FieldMetaData("nulls_first", (byte)1, new FieldValueMetaData((byte)2)));
        FieldMetaData.addStructMetaDataMap(SortingColumn.class, metaDataMap = Collections.unmodifiableMap((Map<? extends _Fields, ? extends FieldMetaData>)tmpMap));
    }
    
    public enum _Fields implements TFieldIdEnum
    {
        COLUMN_IDX((short)1, "column_idx"), 
        DESCENDING((short)2, "descending"), 
        NULLS_FIRST((short)3, "nulls_first");
        
        private static final Map<String, _Fields> byName;
        private final short _thriftId;
        private final String _fieldName;
        
        public static _Fields findByThriftId(final int fieldId) {
            switch (fieldId) {
                case 1: {
                    return _Fields.COLUMN_IDX;
                }
                case 2: {
                    return _Fields.DESCENDING;
                }
                case 3: {
                    return _Fields.NULLS_FIRST;
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
