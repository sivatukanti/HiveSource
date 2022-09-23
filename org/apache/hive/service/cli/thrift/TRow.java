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

public class TRow implements TBase<TRow, _Fields>, Serializable, Cloneable
{
    private static final TStruct STRUCT_DESC;
    private static final TField COL_VALS_FIELD_DESC;
    private static final Map<Class<? extends IScheme>, SchemeFactory> schemes;
    private List<TColumnValue> colVals;
    public static final Map<_Fields, FieldMetaData> metaDataMap;
    
    public TRow() {
    }
    
    public TRow(final List<TColumnValue> colVals) {
        this();
        this.colVals = colVals;
    }
    
    public TRow(final TRow other) {
        if (other.isSetColVals()) {
            final List<TColumnValue> __this__colVals = new ArrayList<TColumnValue>();
            for (final TColumnValue other_element : other.colVals) {
                __this__colVals.add(new TColumnValue(other_element));
            }
            this.colVals = __this__colVals;
        }
    }
    
    @Override
    public TRow deepCopy() {
        return new TRow(this);
    }
    
    @Override
    public void clear() {
        this.colVals = null;
    }
    
    public int getColValsSize() {
        return (this.colVals == null) ? 0 : this.colVals.size();
    }
    
    public Iterator<TColumnValue> getColValsIterator() {
        return (this.colVals == null) ? null : this.colVals.iterator();
    }
    
    public void addToColVals(final TColumnValue elem) {
        if (this.colVals == null) {
            this.colVals = new ArrayList<TColumnValue>();
        }
        this.colVals.add(elem);
    }
    
    public List<TColumnValue> getColVals() {
        return this.colVals;
    }
    
    public void setColVals(final List<TColumnValue> colVals) {
        this.colVals = colVals;
    }
    
    public void unsetColVals() {
        this.colVals = null;
    }
    
    public boolean isSetColVals() {
        return this.colVals != null;
    }
    
    public void setColValsIsSet(final boolean value) {
        if (!value) {
            this.colVals = null;
        }
    }
    
    @Override
    public void setFieldValue(final _Fields field, final Object value) {
        switch (field) {
            case COL_VALS: {
                if (value == null) {
                    this.unsetColVals();
                    break;
                }
                this.setColVals((List<TColumnValue>)value);
                break;
            }
        }
    }
    
    @Override
    public Object getFieldValue(final _Fields field) {
        switch (field) {
            case COL_VALS: {
                return this.getColVals();
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
            case COL_VALS: {
                return this.isSetColVals();
            }
            default: {
                throw new IllegalStateException();
            }
        }
    }
    
    @Override
    public boolean equals(final Object that) {
        return that != null && that instanceof TRow && this.equals((TRow)that);
    }
    
    public boolean equals(final TRow that) {
        if (that == null) {
            return false;
        }
        final boolean this_present_colVals = this.isSetColVals();
        final boolean that_present_colVals = that.isSetColVals();
        if (this_present_colVals || that_present_colVals) {
            if (!this_present_colVals || !that_present_colVals) {
                return false;
            }
            if (!this.colVals.equals(that.colVals)) {
                return false;
            }
        }
        return true;
    }
    
    @Override
    public int hashCode() {
        final HashCodeBuilder builder = new HashCodeBuilder();
        final boolean present_colVals = this.isSetColVals();
        builder.append(present_colVals);
        if (present_colVals) {
            builder.append(this.colVals);
        }
        return builder.toHashCode();
    }
    
    @Override
    public int compareTo(final TRow other) {
        if (!this.getClass().equals(other.getClass())) {
            return this.getClass().getName().compareTo(other.getClass().getName());
        }
        int lastComparison = 0;
        final TRow typedOther = other;
        lastComparison = Boolean.valueOf(this.isSetColVals()).compareTo(Boolean.valueOf(typedOther.isSetColVals()));
        if (lastComparison != 0) {
            return lastComparison;
        }
        if (this.isSetColVals()) {
            lastComparison = TBaseHelper.compareTo(this.colVals, typedOther.colVals);
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
        TRow.schemes.get(iprot.getScheme()).getScheme().read(iprot, this);
    }
    
    @Override
    public void write(final TProtocol oprot) throws TException {
        TRow.schemes.get(oprot.getScheme()).getScheme().write(oprot, this);
    }
    
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("TRow(");
        boolean first = true;
        sb.append("colVals:");
        if (this.colVals == null) {
            sb.append("null");
        }
        else {
            sb.append(this.colVals);
        }
        first = false;
        sb.append(")");
        return sb.toString();
    }
    
    public void validate() throws TException {
        if (!this.isSetColVals()) {
            throw new TProtocolException("Required field 'colVals' is unset! Struct:" + this.toString());
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
        STRUCT_DESC = new TStruct("TRow");
        COL_VALS_FIELD_DESC = new TField("colVals", (byte)15, (short)1);
        (schemes = new HashMap<Class<? extends IScheme>, SchemeFactory>()).put(StandardScheme.class, new TRowStandardSchemeFactory());
        TRow.schemes.put(TupleScheme.class, new TRowTupleSchemeFactory());
        final Map<_Fields, FieldMetaData> tmpMap = new EnumMap<_Fields, FieldMetaData>(_Fields.class);
        tmpMap.put(_Fields.COL_VALS, new FieldMetaData("colVals", (byte)1, new ListMetaData((byte)15, new StructMetaData((byte)12, TColumnValue.class))));
        FieldMetaData.addStructMetaDataMap(TRow.class, metaDataMap = Collections.unmodifiableMap((Map<? extends _Fields, ? extends FieldMetaData>)tmpMap));
    }
    
    public enum _Fields implements TFieldIdEnum
    {
        COL_VALS((short)1, "colVals");
        
        private static final Map<String, _Fields> byName;
        private final short _thriftId;
        private final String _fieldName;
        
        public static _Fields findByThriftId(final int fieldId) {
            switch (fieldId) {
                case 1: {
                    return _Fields.COL_VALS;
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
    
    private static class TRowStandardSchemeFactory implements SchemeFactory
    {
        @Override
        public TRowStandardScheme getScheme() {
            return new TRowStandardScheme();
        }
    }
    
    private static class TRowStandardScheme extends StandardScheme<TRow>
    {
        @Override
        public void read(final TProtocol iprot, final TRow struct) throws TException {
            iprot.readStructBegin();
            while (true) {
                final TField schemeField = iprot.readFieldBegin();
                if (schemeField.type == 0) {
                    break;
                }
                switch (schemeField.id) {
                    case 1: {
                        if (schemeField.type == 15) {
                            final TList _list46 = iprot.readListBegin();
                            struct.colVals = (List<TColumnValue>)new ArrayList(_list46.size);
                            for (int _i47 = 0; _i47 < _list46.size; ++_i47) {
                                final TColumnValue _elem48 = new TColumnValue();
                                _elem48.read(iprot);
                                struct.colVals.add(_elem48);
                            }
                            iprot.readListEnd();
                            struct.setColValsIsSet(true);
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
        public void write(final TProtocol oprot, final TRow struct) throws TException {
            struct.validate();
            oprot.writeStructBegin(TRow.STRUCT_DESC);
            if (struct.colVals != null) {
                oprot.writeFieldBegin(TRow.COL_VALS_FIELD_DESC);
                oprot.writeListBegin(new TList((byte)12, struct.colVals.size()));
                for (final TColumnValue _iter49 : struct.colVals) {
                    _iter49.write(oprot);
                }
                oprot.writeListEnd();
                oprot.writeFieldEnd();
            }
            oprot.writeFieldStop();
            oprot.writeStructEnd();
        }
    }
    
    private static class TRowTupleSchemeFactory implements SchemeFactory
    {
        @Override
        public TRowTupleScheme getScheme() {
            return new TRowTupleScheme();
        }
    }
    
    private static class TRowTupleScheme extends TupleScheme<TRow>
    {
        @Override
        public void write(final TProtocol prot, final TRow struct) throws TException {
            final TTupleProtocol oprot = (TTupleProtocol)prot;
            oprot.writeI32(struct.colVals.size());
            for (final TColumnValue _iter50 : struct.colVals) {
                _iter50.write(oprot);
            }
        }
        
        @Override
        public void read(final TProtocol prot, final TRow struct) throws TException {
            final TTupleProtocol iprot = (TTupleProtocol)prot;
            final TList _list51 = new TList((byte)12, iprot.readI32());
            struct.colVals = (List<TColumnValue>)new ArrayList(_list51.size);
            for (int _i52 = 0; _i52 < _list51.size; ++_i52) {
                final TColumnValue _elem53 = new TColumnValue();
                _elem53.read(iprot);
                struct.colVals.add(_elem53);
            }
            struct.setColValsIsSet(true);
        }
    }
}
