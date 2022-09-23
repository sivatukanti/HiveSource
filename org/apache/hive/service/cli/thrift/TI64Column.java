// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hive.service.cli.thrift;

import org.apache.thrift.protocol.TTupleProtocol;
import org.apache.thrift.protocol.TList;
import org.apache.thrift.protocol.TProtocolUtil;
import java.util.EnumSet;
import java.util.Collections;
import org.apache.thrift.meta_data.ListMetaData;
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
import org.apache.commons.lang.builder.HashCodeBuilder;
import java.util.Iterator;
import org.apache.thrift.TBaseHelper;
import java.util.ArrayList;
import org.apache.thrift.meta_data.FieldMetaData;
import java.nio.ByteBuffer;
import java.util.List;
import org.apache.thrift.scheme.SchemeFactory;
import org.apache.thrift.scheme.IScheme;
import java.util.Map;
import org.apache.thrift.protocol.TField;
import org.apache.thrift.protocol.TStruct;
import java.io.Serializable;
import org.apache.thrift.TBase;

public class TI64Column implements TBase<TI64Column, _Fields>, Serializable, Cloneable
{
    private static final TStruct STRUCT_DESC;
    private static final TField VALUES_FIELD_DESC;
    private static final TField NULLS_FIELD_DESC;
    private static final Map<Class<? extends IScheme>, SchemeFactory> schemes;
    private List<Long> values;
    private ByteBuffer nulls;
    public static final Map<_Fields, FieldMetaData> metaDataMap;
    
    public TI64Column() {
    }
    
    public TI64Column(final List<Long> values, final ByteBuffer nulls) {
        this();
        this.values = values;
        this.nulls = nulls;
    }
    
    public TI64Column(final TI64Column other) {
        if (other.isSetValues()) {
            final List<Long> __this__values = new ArrayList<Long>();
            for (final Long other_element : other.values) {
                __this__values.add(other_element);
            }
            this.values = __this__values;
        }
        if (other.isSetNulls()) {
            this.nulls = TBaseHelper.copyBinary(other.nulls);
        }
    }
    
    @Override
    public TI64Column deepCopy() {
        return new TI64Column(this);
    }
    
    @Override
    public void clear() {
        this.values = null;
        this.nulls = null;
    }
    
    public int getValuesSize() {
        return (this.values == null) ? 0 : this.values.size();
    }
    
    public Iterator<Long> getValuesIterator() {
        return (this.values == null) ? null : this.values.iterator();
    }
    
    public void addToValues(final long elem) {
        if (this.values == null) {
            this.values = new ArrayList<Long>();
        }
        this.values.add(elem);
    }
    
    public List<Long> getValues() {
        return this.values;
    }
    
    public void setValues(final List<Long> values) {
        this.values = values;
    }
    
    public void unsetValues() {
        this.values = null;
    }
    
    public boolean isSetValues() {
        return this.values != null;
    }
    
    public void setValuesIsSet(final boolean value) {
        if (!value) {
            this.values = null;
        }
    }
    
    public byte[] getNulls() {
        this.setNulls(TBaseHelper.rightSize(this.nulls));
        return (byte[])((this.nulls == null) ? null : this.nulls.array());
    }
    
    public ByteBuffer bufferForNulls() {
        return this.nulls;
    }
    
    public void setNulls(final byte[] nulls) {
        this.setNulls((nulls == null) ? ((ByteBuffer)null) : ByteBuffer.wrap(nulls));
    }
    
    public void setNulls(final ByteBuffer nulls) {
        this.nulls = nulls;
    }
    
    public void unsetNulls() {
        this.nulls = null;
    }
    
    public boolean isSetNulls() {
        return this.nulls != null;
    }
    
    public void setNullsIsSet(final boolean value) {
        if (!value) {
            this.nulls = null;
        }
    }
    
    @Override
    public void setFieldValue(final _Fields field, final Object value) {
        switch (field) {
            case VALUES: {
                if (value == null) {
                    this.unsetValues();
                    break;
                }
                this.setValues((List<Long>)value);
                break;
            }
            case NULLS: {
                if (value == null) {
                    this.unsetNulls();
                    break;
                }
                this.setNulls((ByteBuffer)value);
                break;
            }
        }
    }
    
    @Override
    public Object getFieldValue(final _Fields field) {
        switch (field) {
            case VALUES: {
                return this.getValues();
            }
            case NULLS: {
                return this.getNulls();
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
            case VALUES: {
                return this.isSetValues();
            }
            case NULLS: {
                return this.isSetNulls();
            }
            default: {
                throw new IllegalStateException();
            }
        }
    }
    
    @Override
    public boolean equals(final Object that) {
        return that != null && that instanceof TI64Column && this.equals((TI64Column)that);
    }
    
    public boolean equals(final TI64Column that) {
        if (that == null) {
            return false;
        }
        final boolean this_present_values = this.isSetValues();
        final boolean that_present_values = that.isSetValues();
        if (this_present_values || that_present_values) {
            if (!this_present_values || !that_present_values) {
                return false;
            }
            if (!this.values.equals(that.values)) {
                return false;
            }
        }
        final boolean this_present_nulls = this.isSetNulls();
        final boolean that_present_nulls = that.isSetNulls();
        if (this_present_nulls || that_present_nulls) {
            if (!this_present_nulls || !that_present_nulls) {
                return false;
            }
            if (!this.nulls.equals(that.nulls)) {
                return false;
            }
        }
        return true;
    }
    
    @Override
    public int hashCode() {
        final HashCodeBuilder builder = new HashCodeBuilder();
        final boolean present_values = this.isSetValues();
        builder.append(present_values);
        if (present_values) {
            builder.append(this.values);
        }
        final boolean present_nulls = this.isSetNulls();
        builder.append(present_nulls);
        if (present_nulls) {
            builder.append(this.nulls);
        }
        return builder.toHashCode();
    }
    
    @Override
    public int compareTo(final TI64Column other) {
        if (!this.getClass().equals(other.getClass())) {
            return this.getClass().getName().compareTo(other.getClass().getName());
        }
        int lastComparison = 0;
        final TI64Column typedOther = other;
        lastComparison = Boolean.valueOf(this.isSetValues()).compareTo(Boolean.valueOf(typedOther.isSetValues()));
        if (lastComparison != 0) {
            return lastComparison;
        }
        if (this.isSetValues()) {
            lastComparison = TBaseHelper.compareTo(this.values, typedOther.values);
            if (lastComparison != 0) {
                return lastComparison;
            }
        }
        lastComparison = Boolean.valueOf(this.isSetNulls()).compareTo(Boolean.valueOf(typedOther.isSetNulls()));
        if (lastComparison != 0) {
            return lastComparison;
        }
        if (this.isSetNulls()) {
            lastComparison = TBaseHelper.compareTo(this.nulls, typedOther.nulls);
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
        TI64Column.schemes.get(iprot.getScheme()).getScheme().read(iprot, this);
    }
    
    @Override
    public void write(final TProtocol oprot) throws TException {
        TI64Column.schemes.get(oprot.getScheme()).getScheme().write(oprot, this);
    }
    
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("TI64Column(");
        boolean first = true;
        sb.append("values:");
        if (this.values == null) {
            sb.append("null");
        }
        else {
            sb.append(this.values);
        }
        first = false;
        if (!first) {
            sb.append(", ");
        }
        sb.append("nulls:");
        if (this.nulls == null) {
            sb.append("null");
        }
        else {
            TBaseHelper.toString(this.nulls, sb);
        }
        first = false;
        sb.append(")");
        return sb.toString();
    }
    
    public void validate() throws TException {
        if (!this.isSetValues()) {
            throw new TProtocolException("Required field 'values' is unset! Struct:" + this.toString());
        }
        if (!this.isSetNulls()) {
            throw new TProtocolException("Required field 'nulls' is unset! Struct:" + this.toString());
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
        STRUCT_DESC = new TStruct("TI64Column");
        VALUES_FIELD_DESC = new TField("values", (byte)15, (short)1);
        NULLS_FIELD_DESC = new TField("nulls", (byte)11, (short)2);
        (schemes = new HashMap<Class<? extends IScheme>, SchemeFactory>()).put(StandardScheme.class, new TI64ColumnStandardSchemeFactory());
        TI64Column.schemes.put(TupleScheme.class, new TI64ColumnTupleSchemeFactory());
        final Map<_Fields, FieldMetaData> tmpMap = new EnumMap<_Fields, FieldMetaData>(_Fields.class);
        tmpMap.put(_Fields.VALUES, new FieldMetaData("values", (byte)1, new ListMetaData((byte)15, new FieldValueMetaData((byte)10))));
        tmpMap.put(_Fields.NULLS, new FieldMetaData("nulls", (byte)1, new FieldValueMetaData((byte)11, true)));
        FieldMetaData.addStructMetaDataMap(TI64Column.class, metaDataMap = Collections.unmodifiableMap((Map<? extends _Fields, ? extends FieldMetaData>)tmpMap));
    }
    
    public enum _Fields implements TFieldIdEnum
    {
        VALUES((short)1, "values"), 
        NULLS((short)2, "nulls");
        
        private static final Map<String, _Fields> byName;
        private final short _thriftId;
        private final String _fieldName;
        
        public static _Fields findByThriftId(final int fieldId) {
            switch (fieldId) {
                case 1: {
                    return _Fields.VALUES;
                }
                case 2: {
                    return _Fields.NULLS;
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
    
    private static class TI64ColumnStandardSchemeFactory implements SchemeFactory
    {
        @Override
        public TI64ColumnStandardScheme getScheme() {
            return new TI64ColumnStandardScheme();
        }
    }
    
    private static class TI64ColumnStandardScheme extends StandardScheme<TI64Column>
    {
        @Override
        public void read(final TProtocol iprot, final TI64Column struct) throws TException {
            iprot.readStructBegin();
            while (true) {
                final TField schemeField = iprot.readFieldBegin();
                if (schemeField.type == 0) {
                    break;
                }
                switch (schemeField.id) {
                    case 1: {
                        if (schemeField.type == 15) {
                            final TList _list86 = iprot.readListBegin();
                            struct.values = (List<Long>)new ArrayList(_list86.size);
                            for (int _i87 = 0; _i87 < _list86.size; ++_i87) {
                                final long _elem88 = iprot.readI64();
                                struct.values.add(_elem88);
                            }
                            iprot.readListEnd();
                            struct.setValuesIsSet(true);
                            break;
                        }
                        TProtocolUtil.skip(iprot, schemeField.type);
                        break;
                    }
                    case 2: {
                        if (schemeField.type == 11) {
                            struct.nulls = iprot.readBinary();
                            struct.setNullsIsSet(true);
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
        public void write(final TProtocol oprot, final TI64Column struct) throws TException {
            struct.validate();
            oprot.writeStructBegin(TI64Column.STRUCT_DESC);
            if (struct.values != null) {
                oprot.writeFieldBegin(TI64Column.VALUES_FIELD_DESC);
                oprot.writeListBegin(new TList((byte)10, struct.values.size()));
                for (final long _iter89 : struct.values) {
                    oprot.writeI64(_iter89);
                }
                oprot.writeListEnd();
                oprot.writeFieldEnd();
            }
            if (struct.nulls != null) {
                oprot.writeFieldBegin(TI64Column.NULLS_FIELD_DESC);
                oprot.writeBinary(struct.nulls);
                oprot.writeFieldEnd();
            }
            oprot.writeFieldStop();
            oprot.writeStructEnd();
        }
    }
    
    private static class TI64ColumnTupleSchemeFactory implements SchemeFactory
    {
        @Override
        public TI64ColumnTupleScheme getScheme() {
            return new TI64ColumnTupleScheme();
        }
    }
    
    private static class TI64ColumnTupleScheme extends TupleScheme<TI64Column>
    {
        @Override
        public void write(final TProtocol prot, final TI64Column struct) throws TException {
            final TTupleProtocol oprot = (TTupleProtocol)prot;
            oprot.writeI32(struct.values.size());
            for (final long _iter90 : struct.values) {
                oprot.writeI64(_iter90);
            }
            oprot.writeBinary(struct.nulls);
        }
        
        @Override
        public void read(final TProtocol prot, final TI64Column struct) throws TException {
            final TTupleProtocol iprot = (TTupleProtocol)prot;
            final TList _list91 = new TList((byte)10, iprot.readI32());
            struct.values = (List<Long>)new ArrayList(_list91.size);
            for (int _i92 = 0; _i92 < _list91.size; ++_i92) {
                final long _elem93 = iprot.readI64();
                struct.values.add(_elem93);
            }
            struct.setValuesIsSet(true);
            struct.nulls = iprot.readBinary();
            struct.setNullsIsSet(true);
        }
    }
}
