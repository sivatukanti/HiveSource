// 
// Decompiled by Procyon v0.5.36
// 

package parquet.format;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Collections;
import parquet.org.apache.thrift.meta_data.FieldValueMetaData;
import parquet.org.apache.thrift.meta_data.ListMetaData;
import parquet.org.apache.thrift.meta_data.StructMetaData;
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

public class RowGroup implements TBase<RowGroup, _Fields>, Serializable, Cloneable
{
    private static final TStruct STRUCT_DESC;
    private static final TField COLUMNS_FIELD_DESC;
    private static final TField TOTAL_BYTE_SIZE_FIELD_DESC;
    private static final TField NUM_ROWS_FIELD_DESC;
    private static final TField SORTING_COLUMNS_FIELD_DESC;
    public List<ColumnChunk> columns;
    public long total_byte_size;
    public long num_rows;
    public List<SortingColumn> sorting_columns;
    private static final int __TOTAL_BYTE_SIZE_ISSET_ID = 0;
    private static final int __NUM_ROWS_ISSET_ID = 1;
    private BitSet __isset_bit_vector;
    public static final Map<_Fields, FieldMetaData> metaDataMap;
    
    public RowGroup() {
        this.__isset_bit_vector = new BitSet(2);
    }
    
    public RowGroup(final List<ColumnChunk> columns, final long total_byte_size, final long num_rows) {
        this();
        this.columns = columns;
        this.total_byte_size = total_byte_size;
        this.setTotal_byte_sizeIsSet(true);
        this.num_rows = num_rows;
        this.setNum_rowsIsSet(true);
    }
    
    public RowGroup(final RowGroup other) {
        (this.__isset_bit_vector = new BitSet(2)).clear();
        this.__isset_bit_vector.or(other.__isset_bit_vector);
        if (other.isSetColumns()) {
            final List<ColumnChunk> __this__columns = new ArrayList<ColumnChunk>();
            for (final ColumnChunk other_element : other.columns) {
                __this__columns.add(new ColumnChunk(other_element));
            }
            this.columns = __this__columns;
        }
        this.total_byte_size = other.total_byte_size;
        this.num_rows = other.num_rows;
        if (other.isSetSorting_columns()) {
            final List<SortingColumn> __this__sorting_columns = new ArrayList<SortingColumn>();
            for (final SortingColumn other_element2 : other.sorting_columns) {
                __this__sorting_columns.add(new SortingColumn(other_element2));
            }
            this.sorting_columns = __this__sorting_columns;
        }
    }
    
    @Override
    public RowGroup deepCopy() {
        return new RowGroup(this);
    }
    
    @Override
    public void clear() {
        this.columns = null;
        this.setTotal_byte_sizeIsSet(false);
        this.total_byte_size = 0L;
        this.setNum_rowsIsSet(false);
        this.num_rows = 0L;
        this.sorting_columns = null;
    }
    
    public int getColumnsSize() {
        return (this.columns == null) ? 0 : this.columns.size();
    }
    
    public Iterator<ColumnChunk> getColumnsIterator() {
        return (this.columns == null) ? null : this.columns.iterator();
    }
    
    public void addToColumns(final ColumnChunk elem) {
        if (this.columns == null) {
            this.columns = new ArrayList<ColumnChunk>();
        }
        this.columns.add(elem);
    }
    
    public List<ColumnChunk> getColumns() {
        return this.columns;
    }
    
    public RowGroup setColumns(final List<ColumnChunk> columns) {
        this.columns = columns;
        return this;
    }
    
    public void unsetColumns() {
        this.columns = null;
    }
    
    public boolean isSetColumns() {
        return this.columns != null;
    }
    
    public void setColumnsIsSet(final boolean value) {
        if (!value) {
            this.columns = null;
        }
    }
    
    public long getTotal_byte_size() {
        return this.total_byte_size;
    }
    
    public RowGroup setTotal_byte_size(final long total_byte_size) {
        this.total_byte_size = total_byte_size;
        this.setTotal_byte_sizeIsSet(true);
        return this;
    }
    
    public void unsetTotal_byte_size() {
        this.__isset_bit_vector.clear(0);
    }
    
    public boolean isSetTotal_byte_size() {
        return this.__isset_bit_vector.get(0);
    }
    
    public void setTotal_byte_sizeIsSet(final boolean value) {
        this.__isset_bit_vector.set(0, value);
    }
    
    public long getNum_rows() {
        return this.num_rows;
    }
    
    public RowGroup setNum_rows(final long num_rows) {
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
    
    public int getSorting_columnsSize() {
        return (this.sorting_columns == null) ? 0 : this.sorting_columns.size();
    }
    
    public Iterator<SortingColumn> getSorting_columnsIterator() {
        return (this.sorting_columns == null) ? null : this.sorting_columns.iterator();
    }
    
    public void addToSorting_columns(final SortingColumn elem) {
        if (this.sorting_columns == null) {
            this.sorting_columns = new ArrayList<SortingColumn>();
        }
        this.sorting_columns.add(elem);
    }
    
    public List<SortingColumn> getSorting_columns() {
        return this.sorting_columns;
    }
    
    public RowGroup setSorting_columns(final List<SortingColumn> sorting_columns) {
        this.sorting_columns = sorting_columns;
        return this;
    }
    
    public void unsetSorting_columns() {
        this.sorting_columns = null;
    }
    
    public boolean isSetSorting_columns() {
        return this.sorting_columns != null;
    }
    
    public void setSorting_columnsIsSet(final boolean value) {
        if (!value) {
            this.sorting_columns = null;
        }
    }
    
    @Override
    public void setFieldValue(final _Fields field, final Object value) {
        switch (field) {
            case COLUMNS: {
                if (value == null) {
                    this.unsetColumns();
                    break;
                }
                this.setColumns((List<ColumnChunk>)value);
                break;
            }
            case TOTAL_BYTE_SIZE: {
                if (value == null) {
                    this.unsetTotal_byte_size();
                    break;
                }
                this.setTotal_byte_size((long)value);
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
            case SORTING_COLUMNS: {
                if (value == null) {
                    this.unsetSorting_columns();
                    break;
                }
                this.setSorting_columns((List<SortingColumn>)value);
                break;
            }
        }
    }
    
    @Override
    public Object getFieldValue(final _Fields field) {
        switch (field) {
            case COLUMNS: {
                return this.getColumns();
            }
            case TOTAL_BYTE_SIZE: {
                return new Long(this.getTotal_byte_size());
            }
            case NUM_ROWS: {
                return new Long(this.getNum_rows());
            }
            case SORTING_COLUMNS: {
                return this.getSorting_columns();
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
            case COLUMNS: {
                return this.isSetColumns();
            }
            case TOTAL_BYTE_SIZE: {
                return this.isSetTotal_byte_size();
            }
            case NUM_ROWS: {
                return this.isSetNum_rows();
            }
            case SORTING_COLUMNS: {
                return this.isSetSorting_columns();
            }
            default: {
                throw new IllegalStateException();
            }
        }
    }
    
    @Override
    public boolean equals(final Object that) {
        return that != null && that instanceof RowGroup && this.equals((RowGroup)that);
    }
    
    public boolean equals(final RowGroup that) {
        if (that == null) {
            return false;
        }
        final boolean this_present_columns = this.isSetColumns();
        final boolean that_present_columns = that.isSetColumns();
        if (this_present_columns || that_present_columns) {
            if (!this_present_columns || !that_present_columns) {
                return false;
            }
            if (!this.columns.equals(that.columns)) {
                return false;
            }
        }
        final boolean this_present_total_byte_size = true;
        final boolean that_present_total_byte_size = true;
        if (this_present_total_byte_size || that_present_total_byte_size) {
            if (!this_present_total_byte_size || !that_present_total_byte_size) {
                return false;
            }
            if (this.total_byte_size != that.total_byte_size) {
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
        final boolean this_present_sorting_columns = this.isSetSorting_columns();
        final boolean that_present_sorting_columns = that.isSetSorting_columns();
        if (this_present_sorting_columns || that_present_sorting_columns) {
            if (!this_present_sorting_columns || !that_present_sorting_columns) {
                return false;
            }
            if (!this.sorting_columns.equals(that.sorting_columns)) {
                return false;
            }
        }
        return true;
    }
    
    @Override
    public int hashCode() {
        final HashCodeBuilder builder = new HashCodeBuilder();
        final boolean present_columns = this.isSetColumns();
        builder.append(present_columns);
        if (present_columns) {
            builder.append(this.columns);
        }
        final boolean present_total_byte_size = true;
        builder.append(present_total_byte_size);
        if (present_total_byte_size) {
            builder.append(this.total_byte_size);
        }
        final boolean present_num_rows = true;
        builder.append(present_num_rows);
        if (present_num_rows) {
            builder.append(this.num_rows);
        }
        final boolean present_sorting_columns = this.isSetSorting_columns();
        builder.append(present_sorting_columns);
        if (present_sorting_columns) {
            builder.append(this.sorting_columns);
        }
        return builder.toHashCode();
    }
    
    @Override
    public int compareTo(final RowGroup other) {
        if (!this.getClass().equals(other.getClass())) {
            return this.getClass().getName().compareTo(other.getClass().getName());
        }
        int lastComparison = 0;
        final RowGroup typedOther = other;
        lastComparison = Boolean.valueOf(this.isSetColumns()).compareTo(Boolean.valueOf(typedOther.isSetColumns()));
        if (lastComparison != 0) {
            return lastComparison;
        }
        if (this.isSetColumns()) {
            lastComparison = TBaseHelper.compareTo(this.columns, typedOther.columns);
            if (lastComparison != 0) {
                return lastComparison;
            }
        }
        lastComparison = Boolean.valueOf(this.isSetTotal_byte_size()).compareTo(Boolean.valueOf(typedOther.isSetTotal_byte_size()));
        if (lastComparison != 0) {
            return lastComparison;
        }
        if (this.isSetTotal_byte_size()) {
            lastComparison = TBaseHelper.compareTo(this.total_byte_size, typedOther.total_byte_size);
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
        lastComparison = Boolean.valueOf(this.isSetSorting_columns()).compareTo(Boolean.valueOf(typedOther.isSetSorting_columns()));
        if (lastComparison != 0) {
            return lastComparison;
        }
        if (this.isSetSorting_columns()) {
            lastComparison = TBaseHelper.compareTo(this.sorting_columns, typedOther.sorting_columns);
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
                    if (field.type == 15) {
                        final TList _list12 = iprot.readListBegin();
                        this.columns = new ArrayList<ColumnChunk>(_list12.size);
                        for (int _i13 = 0; _i13 < _list12.size; ++_i13) {
                            final ColumnChunk _elem14 = new ColumnChunk();
                            _elem14.read(iprot);
                            this.columns.add(_elem14);
                        }
                        iprot.readListEnd();
                        break;
                    }
                    TProtocolUtil.skip(iprot, field.type);
                    break;
                }
                case 2: {
                    if (field.type == 10) {
                        this.total_byte_size = iprot.readI64();
                        this.setTotal_byte_sizeIsSet(true);
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
                        final TList _list13 = iprot.readListBegin();
                        this.sorting_columns = new ArrayList<SortingColumn>(_list13.size);
                        for (int _i14 = 0; _i14 < _list13.size; ++_i14) {
                            final SortingColumn _elem15 = new SortingColumn();
                            _elem15.read(iprot);
                            this.sorting_columns.add(_elem15);
                        }
                        iprot.readListEnd();
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
        if (!this.isSetTotal_byte_size()) {
            throw new TProtocolException("Required field 'total_byte_size' was not found in serialized data! Struct: " + this.toString());
        }
        if (!this.isSetNum_rows()) {
            throw new TProtocolException("Required field 'num_rows' was not found in serialized data! Struct: " + this.toString());
        }
        this.validate();
    }
    
    @Override
    public void write(final TProtocol oprot) throws TException {
        this.validate();
        oprot.writeStructBegin(RowGroup.STRUCT_DESC);
        if (this.columns != null) {
            oprot.writeFieldBegin(RowGroup.COLUMNS_FIELD_DESC);
            oprot.writeListBegin(new TList((byte)12, this.columns.size()));
            for (final ColumnChunk _iter18 : this.columns) {
                _iter18.write(oprot);
            }
            oprot.writeListEnd();
            oprot.writeFieldEnd();
        }
        oprot.writeFieldBegin(RowGroup.TOTAL_BYTE_SIZE_FIELD_DESC);
        oprot.writeI64(this.total_byte_size);
        oprot.writeFieldEnd();
        oprot.writeFieldBegin(RowGroup.NUM_ROWS_FIELD_DESC);
        oprot.writeI64(this.num_rows);
        oprot.writeFieldEnd();
        if (this.sorting_columns != null && this.isSetSorting_columns()) {
            oprot.writeFieldBegin(RowGroup.SORTING_COLUMNS_FIELD_DESC);
            oprot.writeListBegin(new TList((byte)12, this.sorting_columns.size()));
            for (final SortingColumn _iter19 : this.sorting_columns) {
                _iter19.write(oprot);
            }
            oprot.writeListEnd();
            oprot.writeFieldEnd();
        }
        oprot.writeFieldStop();
        oprot.writeStructEnd();
    }
    
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("RowGroup(");
        boolean first = true;
        sb.append("columns:");
        if (this.columns == null) {
            sb.append("null");
        }
        else {
            sb.append(this.columns);
        }
        first = false;
        if (!first) {
            sb.append(", ");
        }
        sb.append("total_byte_size:");
        sb.append(this.total_byte_size);
        first = false;
        if (!first) {
            sb.append(", ");
        }
        sb.append("num_rows:");
        sb.append(this.num_rows);
        first = false;
        if (this.isSetSorting_columns()) {
            if (!first) {
                sb.append(", ");
            }
            sb.append("sorting_columns:");
            if (this.sorting_columns == null) {
                sb.append("null");
            }
            else {
                sb.append(this.sorting_columns);
            }
            first = false;
        }
        sb.append(")");
        return sb.toString();
    }
    
    public void validate() throws TException {
        if (this.columns == null) {
            throw new TProtocolException("Required field 'columns' was not present! Struct: " + this.toString());
        }
    }
    
    static {
        STRUCT_DESC = new TStruct("RowGroup");
        COLUMNS_FIELD_DESC = new TField("columns", (byte)15, (short)1);
        TOTAL_BYTE_SIZE_FIELD_DESC = new TField("total_byte_size", (byte)10, (short)2);
        NUM_ROWS_FIELD_DESC = new TField("num_rows", (byte)10, (short)3);
        SORTING_COLUMNS_FIELD_DESC = new TField("sorting_columns", (byte)15, (short)4);
        final Map<_Fields, FieldMetaData> tmpMap = new EnumMap<_Fields, FieldMetaData>(_Fields.class);
        tmpMap.put(_Fields.COLUMNS, new FieldMetaData("columns", (byte)1, new ListMetaData((byte)15, new StructMetaData((byte)12, ColumnChunk.class))));
        tmpMap.put(_Fields.TOTAL_BYTE_SIZE, new FieldMetaData("total_byte_size", (byte)1, new FieldValueMetaData((byte)10)));
        tmpMap.put(_Fields.NUM_ROWS, new FieldMetaData("num_rows", (byte)1, new FieldValueMetaData((byte)10)));
        tmpMap.put(_Fields.SORTING_COLUMNS, new FieldMetaData("sorting_columns", (byte)2, new ListMetaData((byte)15, new StructMetaData((byte)12, SortingColumn.class))));
        FieldMetaData.addStructMetaDataMap(RowGroup.class, metaDataMap = Collections.unmodifiableMap((Map<? extends _Fields, ? extends FieldMetaData>)tmpMap));
    }
    
    public enum _Fields implements TFieldIdEnum
    {
        COLUMNS((short)1, "columns"), 
        TOTAL_BYTE_SIZE((short)2, "total_byte_size"), 
        NUM_ROWS((short)3, "num_rows"), 
        SORTING_COLUMNS((short)4, "sorting_columns");
        
        private static final Map<String, _Fields> byName;
        private final short _thriftId;
        private final String _fieldName;
        
        public static _Fields findByThriftId(final int fieldId) {
            switch (fieldId) {
                case 1: {
                    return _Fields.COLUMNS;
                }
                case 2: {
                    return _Fields.TOTAL_BYTE_SIZE;
                }
                case 3: {
                    return _Fields.NUM_ROWS;
                }
                case 4: {
                    return _Fields.SORTING_COLUMNS;
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
