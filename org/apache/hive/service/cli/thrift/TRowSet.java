// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hive.service.cli.thrift;

import java.util.BitSet;
import org.apache.thrift.protocol.TTupleProtocol;
import org.apache.thrift.protocol.TList;
import org.apache.thrift.protocol.TProtocolUtil;
import java.util.EnumSet;
import java.util.Collections;
import org.apache.thrift.meta_data.ListMetaData;
import org.apache.thrift.meta_data.StructMetaData;
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
import java.util.Iterator;
import java.util.ArrayList;
import org.apache.thrift.meta_data.FieldMetaData;
import java.util.List;
import org.apache.thrift.scheme.SchemeFactory;
import org.apache.thrift.scheme.IScheme;
import java.util.Map;
import org.apache.thrift.protocol.TField;
import org.apache.thrift.protocol.TStruct;
import java.io.Serializable;
import org.apache.thrift.TBase;

public class TRowSet implements TBase<TRowSet, _Fields>, Serializable, Cloneable
{
    private static final TStruct STRUCT_DESC;
    private static final TField START_ROW_OFFSET_FIELD_DESC;
    private static final TField ROWS_FIELD_DESC;
    private static final TField COLUMNS_FIELD_DESC;
    private static final Map<Class<? extends IScheme>, SchemeFactory> schemes;
    private long startRowOffset;
    private List<TRow> rows;
    private List<TColumn> columns;
    private static final int __STARTROWOFFSET_ISSET_ID = 0;
    private byte __isset_bitfield;
    private _Fields[] optionals;
    public static final Map<_Fields, FieldMetaData> metaDataMap;
    
    public TRowSet() {
        this.__isset_bitfield = 0;
        this.optionals = new _Fields[] { _Fields.COLUMNS };
    }
    
    public TRowSet(final long startRowOffset, final List<TRow> rows) {
        this();
        this.startRowOffset = startRowOffset;
        this.setStartRowOffsetIsSet(true);
        this.rows = rows;
    }
    
    public TRowSet(final TRowSet other) {
        this.__isset_bitfield = 0;
        this.optionals = new _Fields[] { _Fields.COLUMNS };
        this.__isset_bitfield = other.__isset_bitfield;
        this.startRowOffset = other.startRowOffset;
        if (other.isSetRows()) {
            final List<TRow> __this__rows = new ArrayList<TRow>();
            for (final TRow other_element : other.rows) {
                __this__rows.add(new TRow(other_element));
            }
            this.rows = __this__rows;
        }
        if (other.isSetColumns()) {
            final List<TColumn> __this__columns = new ArrayList<TColumn>();
            for (final TColumn other_element2 : other.columns) {
                __this__columns.add(new TColumn(other_element2));
            }
            this.columns = __this__columns;
        }
    }
    
    @Override
    public TRowSet deepCopy() {
        return new TRowSet(this);
    }
    
    @Override
    public void clear() {
        this.setStartRowOffsetIsSet(false);
        this.startRowOffset = 0L;
        this.rows = null;
        this.columns = null;
    }
    
    public long getStartRowOffset() {
        return this.startRowOffset;
    }
    
    public void setStartRowOffset(final long startRowOffset) {
        this.startRowOffset = startRowOffset;
        this.setStartRowOffsetIsSet(true);
    }
    
    public void unsetStartRowOffset() {
        this.__isset_bitfield = EncodingUtils.clearBit(this.__isset_bitfield, 0);
    }
    
    public boolean isSetStartRowOffset() {
        return EncodingUtils.testBit(this.__isset_bitfield, 0);
    }
    
    public void setStartRowOffsetIsSet(final boolean value) {
        this.__isset_bitfield = EncodingUtils.setBit(this.__isset_bitfield, 0, value);
    }
    
    public int getRowsSize() {
        return (this.rows == null) ? 0 : this.rows.size();
    }
    
    public Iterator<TRow> getRowsIterator() {
        return (this.rows == null) ? null : this.rows.iterator();
    }
    
    public void addToRows(final TRow elem) {
        if (this.rows == null) {
            this.rows = new ArrayList<TRow>();
        }
        this.rows.add(elem);
    }
    
    public List<TRow> getRows() {
        return this.rows;
    }
    
    public void setRows(final List<TRow> rows) {
        this.rows = rows;
    }
    
    public void unsetRows() {
        this.rows = null;
    }
    
    public boolean isSetRows() {
        return this.rows != null;
    }
    
    public void setRowsIsSet(final boolean value) {
        if (!value) {
            this.rows = null;
        }
    }
    
    public int getColumnsSize() {
        return (this.columns == null) ? 0 : this.columns.size();
    }
    
    public Iterator<TColumn> getColumnsIterator() {
        return (this.columns == null) ? null : this.columns.iterator();
    }
    
    public void addToColumns(final TColumn elem) {
        if (this.columns == null) {
            this.columns = new ArrayList<TColumn>();
        }
        this.columns.add(elem);
    }
    
    public List<TColumn> getColumns() {
        return this.columns;
    }
    
    public void setColumns(final List<TColumn> columns) {
        this.columns = columns;
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
    
    @Override
    public void setFieldValue(final _Fields field, final Object value) {
        switch (field) {
            case START_ROW_OFFSET: {
                if (value == null) {
                    this.unsetStartRowOffset();
                    break;
                }
                this.setStartRowOffset((long)value);
                break;
            }
            case ROWS: {
                if (value == null) {
                    this.unsetRows();
                    break;
                }
                this.setRows((List<TRow>)value);
                break;
            }
            case COLUMNS: {
                if (value == null) {
                    this.unsetColumns();
                    break;
                }
                this.setColumns((List<TColumn>)value);
                break;
            }
        }
    }
    
    @Override
    public Object getFieldValue(final _Fields field) {
        switch (field) {
            case START_ROW_OFFSET: {
                return this.getStartRowOffset();
            }
            case ROWS: {
                return this.getRows();
            }
            case COLUMNS: {
                return this.getColumns();
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
            case START_ROW_OFFSET: {
                return this.isSetStartRowOffset();
            }
            case ROWS: {
                return this.isSetRows();
            }
            case COLUMNS: {
                return this.isSetColumns();
            }
            default: {
                throw new IllegalStateException();
            }
        }
    }
    
    @Override
    public boolean equals(final Object that) {
        return that != null && that instanceof TRowSet && this.equals((TRowSet)that);
    }
    
    public boolean equals(final TRowSet that) {
        if (that == null) {
            return false;
        }
        final boolean this_present_startRowOffset = true;
        final boolean that_present_startRowOffset = true;
        if (this_present_startRowOffset || that_present_startRowOffset) {
            if (!this_present_startRowOffset || !that_present_startRowOffset) {
                return false;
            }
            if (this.startRowOffset != that.startRowOffset) {
                return false;
            }
        }
        final boolean this_present_rows = this.isSetRows();
        final boolean that_present_rows = that.isSetRows();
        if (this_present_rows || that_present_rows) {
            if (!this_present_rows || !that_present_rows) {
                return false;
            }
            if (!this.rows.equals(that.rows)) {
                return false;
            }
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
        return true;
    }
    
    @Override
    public int hashCode() {
        final HashCodeBuilder builder = new HashCodeBuilder();
        final boolean present_startRowOffset = true;
        builder.append(present_startRowOffset);
        if (present_startRowOffset) {
            builder.append(this.startRowOffset);
        }
        final boolean present_rows = this.isSetRows();
        builder.append(present_rows);
        if (present_rows) {
            builder.append(this.rows);
        }
        final boolean present_columns = this.isSetColumns();
        builder.append(present_columns);
        if (present_columns) {
            builder.append(this.columns);
        }
        return builder.toHashCode();
    }
    
    @Override
    public int compareTo(final TRowSet other) {
        if (!this.getClass().equals(other.getClass())) {
            return this.getClass().getName().compareTo(other.getClass().getName());
        }
        int lastComparison = 0;
        final TRowSet typedOther = other;
        lastComparison = Boolean.valueOf(this.isSetStartRowOffset()).compareTo(Boolean.valueOf(typedOther.isSetStartRowOffset()));
        if (lastComparison != 0) {
            return lastComparison;
        }
        if (this.isSetStartRowOffset()) {
            lastComparison = TBaseHelper.compareTo(this.startRowOffset, typedOther.startRowOffset);
            if (lastComparison != 0) {
                return lastComparison;
            }
        }
        lastComparison = Boolean.valueOf(this.isSetRows()).compareTo(Boolean.valueOf(typedOther.isSetRows()));
        if (lastComparison != 0) {
            return lastComparison;
        }
        if (this.isSetRows()) {
            lastComparison = TBaseHelper.compareTo(this.rows, typedOther.rows);
            if (lastComparison != 0) {
                return lastComparison;
            }
        }
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
        return 0;
    }
    
    @Override
    public _Fields fieldForId(final int fieldId) {
        return _Fields.findByThriftId(fieldId);
    }
    
    @Override
    public void read(final TProtocol iprot) throws TException {
        TRowSet.schemes.get(iprot.getScheme()).getScheme().read(iprot, this);
    }
    
    @Override
    public void write(final TProtocol oprot) throws TException {
        TRowSet.schemes.get(oprot.getScheme()).getScheme().write(oprot, this);
    }
    
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("TRowSet(");
        boolean first = true;
        sb.append("startRowOffset:");
        sb.append(this.startRowOffset);
        first = false;
        if (!first) {
            sb.append(", ");
        }
        sb.append("rows:");
        if (this.rows == null) {
            sb.append("null");
        }
        else {
            sb.append(this.rows);
        }
        first = false;
        if (this.isSetColumns()) {
            if (!first) {
                sb.append(", ");
            }
            sb.append("columns:");
            if (this.columns == null) {
                sb.append("null");
            }
            else {
                sb.append(this.columns);
            }
            first = false;
        }
        sb.append(")");
        return sb.toString();
    }
    
    public void validate() throws TException {
        if (!this.isSetStartRowOffset()) {
            throw new TProtocolException("Required field 'startRowOffset' is unset! Struct:" + this.toString());
        }
        if (!this.isSetRows()) {
            throw new TProtocolException("Required field 'rows' is unset! Struct:" + this.toString());
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
        STRUCT_DESC = new TStruct("TRowSet");
        START_ROW_OFFSET_FIELD_DESC = new TField("startRowOffset", (byte)10, (short)1);
        ROWS_FIELD_DESC = new TField("rows", (byte)15, (short)2);
        COLUMNS_FIELD_DESC = new TField("columns", (byte)15, (short)3);
        (schemes = new HashMap<Class<? extends IScheme>, SchemeFactory>()).put(StandardScheme.class, new TRowSetStandardSchemeFactory());
        TRowSet.schemes.put(TupleScheme.class, new TRowSetTupleSchemeFactory());
        final Map<_Fields, FieldMetaData> tmpMap = new EnumMap<_Fields, FieldMetaData>(_Fields.class);
        tmpMap.put(_Fields.START_ROW_OFFSET, new FieldMetaData("startRowOffset", (byte)1, new FieldValueMetaData((byte)10)));
        tmpMap.put(_Fields.ROWS, new FieldMetaData("rows", (byte)1, new ListMetaData((byte)15, new StructMetaData((byte)12, TRow.class))));
        tmpMap.put(_Fields.COLUMNS, new FieldMetaData("columns", (byte)2, new ListMetaData((byte)15, new StructMetaData((byte)12, TColumn.class))));
        FieldMetaData.addStructMetaDataMap(TRowSet.class, metaDataMap = Collections.unmodifiableMap((Map<? extends _Fields, ? extends FieldMetaData>)tmpMap));
    }
    
    public enum _Fields implements TFieldIdEnum
    {
        START_ROW_OFFSET((short)1, "startRowOffset"), 
        ROWS((short)2, "rows"), 
        COLUMNS((short)3, "columns");
        
        private static final Map<String, _Fields> byName;
        private final short _thriftId;
        private final String _fieldName;
        
        public static _Fields findByThriftId(final int fieldId) {
            switch (fieldId) {
                case 1: {
                    return _Fields.START_ROW_OFFSET;
                }
                case 2: {
                    return _Fields.ROWS;
                }
                case 3: {
                    return _Fields.COLUMNS;
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
    
    private static class TRowSetStandardSchemeFactory implements SchemeFactory
    {
        @Override
        public TRowSetStandardScheme getScheme() {
            return new TRowSetStandardScheme();
        }
    }
    
    private static class TRowSetStandardScheme extends StandardScheme<TRowSet>
    {
        @Override
        public void read(final TProtocol iprot, final TRowSet struct) throws TException {
            iprot.readStructBegin();
            while (true) {
                final TField schemeField = iprot.readFieldBegin();
                if (schemeField.type == 0) {
                    break;
                }
                switch (schemeField.id) {
                    case 1: {
                        if (schemeField.type == 10) {
                            struct.startRowOffset = iprot.readI64();
                            struct.setStartRowOffsetIsSet(true);
                            break;
                        }
                        TProtocolUtil.skip(iprot, schemeField.type);
                        break;
                    }
                    case 2: {
                        if (schemeField.type == 15) {
                            final TList _list118 = iprot.readListBegin();
                            struct.rows = (List<TRow>)new ArrayList(_list118.size);
                            for (int _i119 = 0; _i119 < _list118.size; ++_i119) {
                                final TRow _elem120 = new TRow();
                                _elem120.read(iprot);
                                struct.rows.add(_elem120);
                            }
                            iprot.readListEnd();
                            struct.setRowsIsSet(true);
                            break;
                        }
                        TProtocolUtil.skip(iprot, schemeField.type);
                        break;
                    }
                    case 3: {
                        if (schemeField.type == 15) {
                            final TList _list119 = iprot.readListBegin();
                            struct.columns = (List<TColumn>)new ArrayList(_list119.size);
                            for (int _i120 = 0; _i120 < _list119.size; ++_i120) {
                                final TColumn _elem121 = new TColumn();
                                _elem121.read(iprot);
                                struct.columns.add(_elem121);
                            }
                            iprot.readListEnd();
                            struct.setColumnsIsSet(true);
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
        public void write(final TProtocol oprot, final TRowSet struct) throws TException {
            struct.validate();
            oprot.writeStructBegin(TRowSet.STRUCT_DESC);
            oprot.writeFieldBegin(TRowSet.START_ROW_OFFSET_FIELD_DESC);
            oprot.writeI64(struct.startRowOffset);
            oprot.writeFieldEnd();
            if (struct.rows != null) {
                oprot.writeFieldBegin(TRowSet.ROWS_FIELD_DESC);
                oprot.writeListBegin(new TList((byte)12, struct.rows.size()));
                for (final TRow _iter124 : struct.rows) {
                    _iter124.write(oprot);
                }
                oprot.writeListEnd();
                oprot.writeFieldEnd();
            }
            if (struct.columns != null && struct.isSetColumns()) {
                oprot.writeFieldBegin(TRowSet.COLUMNS_FIELD_DESC);
                oprot.writeListBegin(new TList((byte)12, struct.columns.size()));
                for (final TColumn _iter125 : struct.columns) {
                    _iter125.write(oprot);
                }
                oprot.writeListEnd();
                oprot.writeFieldEnd();
            }
            oprot.writeFieldStop();
            oprot.writeStructEnd();
        }
    }
    
    private static class TRowSetTupleSchemeFactory implements SchemeFactory
    {
        @Override
        public TRowSetTupleScheme getScheme() {
            return new TRowSetTupleScheme();
        }
    }
    
    private static class TRowSetTupleScheme extends TupleScheme<TRowSet>
    {
        @Override
        public void write(final TProtocol prot, final TRowSet struct) throws TException {
            final TTupleProtocol oprot = (TTupleProtocol)prot;
            oprot.writeI64(struct.startRowOffset);
            oprot.writeI32(struct.rows.size());
            for (final TRow _iter126 : struct.rows) {
                _iter126.write(oprot);
            }
            final BitSet optionals = new BitSet();
            if (struct.isSetColumns()) {
                optionals.set(0);
            }
            oprot.writeBitSet(optionals, 1);
            if (struct.isSetColumns()) {
                oprot.writeI32(struct.columns.size());
                for (final TColumn _iter127 : struct.columns) {
                    _iter127.write(oprot);
                }
            }
        }
        
        @Override
        public void read(final TProtocol prot, final TRowSet struct) throws TException {
            final TTupleProtocol iprot = (TTupleProtocol)prot;
            struct.startRowOffset = iprot.readI64();
            struct.setStartRowOffsetIsSet(true);
            final TList _list128 = new TList((byte)12, iprot.readI32());
            struct.rows = (List<TRow>)new ArrayList(_list128.size);
            for (int _i129 = 0; _i129 < _list128.size; ++_i129) {
                final TRow _elem130 = new TRow();
                _elem130.read(iprot);
                struct.rows.add(_elem130);
            }
            struct.setRowsIsSet(true);
            final BitSet incoming = iprot.readBitSet(1);
            if (incoming.get(0)) {
                final TList _list129 = new TList((byte)12, iprot.readI32());
                struct.columns = (List<TColumn>)new ArrayList(_list129.size);
                for (int _i130 = 0; _i130 < _list129.size; ++_i130) {
                    final TColumn _elem131 = new TColumn();
                    _elem131.read(iprot);
                    struct.columns.add(_elem131);
                }
                struct.setColumnsIsSet(true);
            }
        }
    }
}
