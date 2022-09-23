// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hive.service.cli.thrift;

import org.apache.thrift.protocol.TTupleProtocol;
import org.apache.thrift.protocol.TList;
import org.apache.thrift.protocol.TProtocolUtil;
import java.util.EnumSet;
import java.util.Collections;
import org.apache.thrift.meta_data.FieldValueMetaData;
import org.apache.thrift.meta_data.ListMetaData;
import org.apache.thrift.meta_data.StructMetaData;
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

public class TTableSchema implements TBase<TTableSchema, _Fields>, Serializable, Cloneable
{
    private static final TStruct STRUCT_DESC;
    private static final TField COLUMNS_FIELD_DESC;
    private static final Map<Class<? extends IScheme>, SchemeFactory> schemes;
    private List<TColumnDesc> columns;
    public static final Map<_Fields, FieldMetaData> metaDataMap;
    
    public TTableSchema() {
    }
    
    public TTableSchema(final List<TColumnDesc> columns) {
        this();
        this.columns = columns;
    }
    
    public TTableSchema(final TTableSchema other) {
        if (other.isSetColumns()) {
            final List<TColumnDesc> __this__columns = new ArrayList<TColumnDesc>();
            for (final TColumnDesc other_element : other.columns) {
                __this__columns.add(new TColumnDesc(other_element));
            }
            this.columns = __this__columns;
        }
    }
    
    @Override
    public TTableSchema deepCopy() {
        return new TTableSchema(this);
    }
    
    @Override
    public void clear() {
        this.columns = null;
    }
    
    public int getColumnsSize() {
        return (this.columns == null) ? 0 : this.columns.size();
    }
    
    public Iterator<TColumnDesc> getColumnsIterator() {
        return (this.columns == null) ? null : this.columns.iterator();
    }
    
    public void addToColumns(final TColumnDesc elem) {
        if (this.columns == null) {
            this.columns = new ArrayList<TColumnDesc>();
        }
        this.columns.add(elem);
    }
    
    public List<TColumnDesc> getColumns() {
        return this.columns;
    }
    
    public void setColumns(final List<TColumnDesc> columns) {
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
            case COLUMNS: {
                if (value == null) {
                    this.unsetColumns();
                    break;
                }
                this.setColumns((List<TColumnDesc>)value);
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
            default: {
                throw new IllegalStateException();
            }
        }
    }
    
    @Override
    public boolean equals(final Object that) {
        return that != null && that instanceof TTableSchema && this.equals((TTableSchema)that);
    }
    
    public boolean equals(final TTableSchema that) {
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
        return builder.toHashCode();
    }
    
    @Override
    public int compareTo(final TTableSchema other) {
        if (!this.getClass().equals(other.getClass())) {
            return this.getClass().getName().compareTo(other.getClass().getName());
        }
        int lastComparison = 0;
        final TTableSchema typedOther = other;
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
        TTableSchema.schemes.get(iprot.getScheme()).getScheme().read(iprot, this);
    }
    
    @Override
    public void write(final TProtocol oprot) throws TException {
        TTableSchema.schemes.get(oprot.getScheme()).getScheme().write(oprot, this);
    }
    
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("TTableSchema(");
        boolean first = true;
        sb.append("columns:");
        if (this.columns == null) {
            sb.append("null");
        }
        else {
            sb.append(this.columns);
        }
        first = false;
        sb.append(")");
        return sb.toString();
    }
    
    public void validate() throws TException {
        if (!this.isSetColumns()) {
            throw new TProtocolException("Required field 'columns' is unset! Struct:" + this.toString());
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
            this.read(new TCompactProtocol(new TIOStreamTransport(in)));
        }
        catch (TException te) {
            throw new IOException(te);
        }
    }
    
    static {
        STRUCT_DESC = new TStruct("TTableSchema");
        COLUMNS_FIELD_DESC = new TField("columns", (byte)15, (short)1);
        (schemes = new HashMap<Class<? extends IScheme>, SchemeFactory>()).put(StandardScheme.class, new TTableSchemaStandardSchemeFactory());
        TTableSchema.schemes.put(TupleScheme.class, new TTableSchemaTupleSchemeFactory());
        final Map<_Fields, FieldMetaData> tmpMap = new EnumMap<_Fields, FieldMetaData>(_Fields.class);
        tmpMap.put(_Fields.COLUMNS, new FieldMetaData("columns", (byte)1, new ListMetaData((byte)15, new StructMetaData((byte)12, TColumnDesc.class))));
        FieldMetaData.addStructMetaDataMap(TTableSchema.class, metaDataMap = Collections.unmodifiableMap((Map<? extends _Fields, ? extends FieldMetaData>)tmpMap));
    }
    
    public enum _Fields implements TFieldIdEnum
    {
        COLUMNS((short)1, "columns");
        
        private static final Map<String, _Fields> byName;
        private final short _thriftId;
        private final String _fieldName;
        
        public static _Fields findByThriftId(final int fieldId) {
            switch (fieldId) {
                case 1: {
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
    
    private static class TTableSchemaStandardSchemeFactory implements SchemeFactory
    {
        @Override
        public TTableSchemaStandardScheme getScheme() {
            return new TTableSchemaStandardScheme();
        }
    }
    
    private static class TTableSchemaStandardScheme extends StandardScheme<TTableSchema>
    {
        @Override
        public void read(final TProtocol iprot, final TTableSchema struct) throws TException {
            iprot.readStructBegin();
            while (true) {
                final TField schemeField = iprot.readFieldBegin();
                if (schemeField.type == 0) {
                    break;
                }
                switch (schemeField.id) {
                    case 1: {
                        if (schemeField.type == 15) {
                            final TList _list38 = iprot.readListBegin();
                            struct.columns = (List<TColumnDesc>)new ArrayList(_list38.size);
                            for (int _i39 = 0; _i39 < _list38.size; ++_i39) {
                                final TColumnDesc _elem40 = new TColumnDesc();
                                _elem40.read(iprot);
                                struct.columns.add(_elem40);
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
        public void write(final TProtocol oprot, final TTableSchema struct) throws TException {
            struct.validate();
            oprot.writeStructBegin(TTableSchema.STRUCT_DESC);
            if (struct.columns != null) {
                oprot.writeFieldBegin(TTableSchema.COLUMNS_FIELD_DESC);
                oprot.writeListBegin(new TList((byte)12, struct.columns.size()));
                for (final TColumnDesc _iter41 : struct.columns) {
                    _iter41.write(oprot);
                }
                oprot.writeListEnd();
                oprot.writeFieldEnd();
            }
            oprot.writeFieldStop();
            oprot.writeStructEnd();
        }
    }
    
    private static class TTableSchemaTupleSchemeFactory implements SchemeFactory
    {
        @Override
        public TTableSchemaTupleScheme getScheme() {
            return new TTableSchemaTupleScheme();
        }
    }
    
    private static class TTableSchemaTupleScheme extends TupleScheme<TTableSchema>
    {
        @Override
        public void write(final TProtocol prot, final TTableSchema struct) throws TException {
            final TTupleProtocol oprot = (TTupleProtocol)prot;
            oprot.writeI32(struct.columns.size());
            for (final TColumnDesc _iter42 : struct.columns) {
                _iter42.write(oprot);
            }
        }
        
        @Override
        public void read(final TProtocol prot, final TTableSchema struct) throws TException {
            final TTupleProtocol iprot = (TTupleProtocol)prot;
            final TList _list43 = new TList((byte)12, iprot.readI32());
            struct.columns = (List<TColumnDesc>)new ArrayList(_list43.size);
            for (int _i44 = 0; _i44 < _list43.size; ++_i44) {
                final TColumnDesc _elem45 = new TColumnDesc();
                _elem45.read(iprot);
                struct.columns.add(_elem45);
            }
            struct.setColumnsIsSet(true);
        }
    }
}
