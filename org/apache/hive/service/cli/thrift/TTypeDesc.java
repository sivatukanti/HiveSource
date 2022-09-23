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

public class TTypeDesc implements TBase<TTypeDesc, _Fields>, Serializable, Cloneable
{
    private static final TStruct STRUCT_DESC;
    private static final TField TYPES_FIELD_DESC;
    private static final Map<Class<? extends IScheme>, SchemeFactory> schemes;
    private List<TTypeEntry> types;
    public static final Map<_Fields, FieldMetaData> metaDataMap;
    
    public TTypeDesc() {
    }
    
    public TTypeDesc(final List<TTypeEntry> types) {
        this();
        this.types = types;
    }
    
    public TTypeDesc(final TTypeDesc other) {
        if (other.isSetTypes()) {
            final List<TTypeEntry> __this__types = new ArrayList<TTypeEntry>();
            for (final TTypeEntry other_element : other.types) {
                __this__types.add(new TTypeEntry(other_element));
            }
            this.types = __this__types;
        }
    }
    
    @Override
    public TTypeDesc deepCopy() {
        return new TTypeDesc(this);
    }
    
    @Override
    public void clear() {
        this.types = null;
    }
    
    public int getTypesSize() {
        return (this.types == null) ? 0 : this.types.size();
    }
    
    public Iterator<TTypeEntry> getTypesIterator() {
        return (this.types == null) ? null : this.types.iterator();
    }
    
    public void addToTypes(final TTypeEntry elem) {
        if (this.types == null) {
            this.types = new ArrayList<TTypeEntry>();
        }
        this.types.add(elem);
    }
    
    public List<TTypeEntry> getTypes() {
        return this.types;
    }
    
    public void setTypes(final List<TTypeEntry> types) {
        this.types = types;
    }
    
    public void unsetTypes() {
        this.types = null;
    }
    
    public boolean isSetTypes() {
        return this.types != null;
    }
    
    public void setTypesIsSet(final boolean value) {
        if (!value) {
            this.types = null;
        }
    }
    
    @Override
    public void setFieldValue(final _Fields field, final Object value) {
        switch (field) {
            case TYPES: {
                if (value == null) {
                    this.unsetTypes();
                    break;
                }
                this.setTypes((List<TTypeEntry>)value);
                break;
            }
        }
    }
    
    @Override
    public Object getFieldValue(final _Fields field) {
        switch (field) {
            case TYPES: {
                return this.getTypes();
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
            case TYPES: {
                return this.isSetTypes();
            }
            default: {
                throw new IllegalStateException();
            }
        }
    }
    
    @Override
    public boolean equals(final Object that) {
        return that != null && that instanceof TTypeDesc && this.equals((TTypeDesc)that);
    }
    
    public boolean equals(final TTypeDesc that) {
        if (that == null) {
            return false;
        }
        final boolean this_present_types = this.isSetTypes();
        final boolean that_present_types = that.isSetTypes();
        if (this_present_types || that_present_types) {
            if (!this_present_types || !that_present_types) {
                return false;
            }
            if (!this.types.equals(that.types)) {
                return false;
            }
        }
        return true;
    }
    
    @Override
    public int hashCode() {
        final HashCodeBuilder builder = new HashCodeBuilder();
        final boolean present_types = this.isSetTypes();
        builder.append(present_types);
        if (present_types) {
            builder.append(this.types);
        }
        return builder.toHashCode();
    }
    
    @Override
    public int compareTo(final TTypeDesc other) {
        if (!this.getClass().equals(other.getClass())) {
            return this.getClass().getName().compareTo(other.getClass().getName());
        }
        int lastComparison = 0;
        final TTypeDesc typedOther = other;
        lastComparison = Boolean.valueOf(this.isSetTypes()).compareTo(Boolean.valueOf(typedOther.isSetTypes()));
        if (lastComparison != 0) {
            return lastComparison;
        }
        if (this.isSetTypes()) {
            lastComparison = TBaseHelper.compareTo(this.types, typedOther.types);
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
        TTypeDesc.schemes.get(iprot.getScheme()).getScheme().read(iprot, this);
    }
    
    @Override
    public void write(final TProtocol oprot) throws TException {
        TTypeDesc.schemes.get(oprot.getScheme()).getScheme().write(oprot, this);
    }
    
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("TTypeDesc(");
        boolean first = true;
        sb.append("types:");
        if (this.types == null) {
            sb.append("null");
        }
        else {
            sb.append(this.types);
        }
        first = false;
        sb.append(")");
        return sb.toString();
    }
    
    public void validate() throws TException {
        if (!this.isSetTypes()) {
            throw new TProtocolException("Required field 'types' is unset! Struct:" + this.toString());
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
        STRUCT_DESC = new TStruct("TTypeDesc");
        TYPES_FIELD_DESC = new TField("types", (byte)15, (short)1);
        (schemes = new HashMap<Class<? extends IScheme>, SchemeFactory>()).put(StandardScheme.class, new TTypeDescStandardSchemeFactory());
        TTypeDesc.schemes.put(TupleScheme.class, new TTypeDescTupleSchemeFactory());
        final Map<_Fields, FieldMetaData> tmpMap = new EnumMap<_Fields, FieldMetaData>(_Fields.class);
        tmpMap.put(_Fields.TYPES, new FieldMetaData("types", (byte)1, new ListMetaData((byte)15, new StructMetaData((byte)12, TTypeEntry.class))));
        FieldMetaData.addStructMetaDataMap(TTypeDesc.class, metaDataMap = Collections.unmodifiableMap((Map<? extends _Fields, ? extends FieldMetaData>)tmpMap));
    }
    
    public enum _Fields implements TFieldIdEnum
    {
        TYPES((short)1, "types");
        
        private static final Map<String, _Fields> byName;
        private final short _thriftId;
        private final String _fieldName;
        
        public static _Fields findByThriftId(final int fieldId) {
            switch (fieldId) {
                case 1: {
                    return _Fields.TYPES;
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
    
    private static class TTypeDescStandardSchemeFactory implements SchemeFactory
    {
        @Override
        public TTypeDescStandardScheme getScheme() {
            return new TTypeDescStandardScheme();
        }
    }
    
    private static class TTypeDescStandardScheme extends StandardScheme<TTypeDesc>
    {
        @Override
        public void read(final TProtocol iprot, final TTypeDesc struct) throws TException {
            iprot.readStructBegin();
            while (true) {
                final TField schemeField = iprot.readFieldBegin();
                if (schemeField.type == 0) {
                    break;
                }
                switch (schemeField.id) {
                    case 1: {
                        if (schemeField.type == 15) {
                            final TList _list30 = iprot.readListBegin();
                            struct.types = (List<TTypeEntry>)new ArrayList(_list30.size);
                            for (int _i31 = 0; _i31 < _list30.size; ++_i31) {
                                final TTypeEntry _elem32 = new TTypeEntry();
                                _elem32.read(iprot);
                                struct.types.add(_elem32);
                            }
                            iprot.readListEnd();
                            struct.setTypesIsSet(true);
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
        public void write(final TProtocol oprot, final TTypeDesc struct) throws TException {
            struct.validate();
            oprot.writeStructBegin(TTypeDesc.STRUCT_DESC);
            if (struct.types != null) {
                oprot.writeFieldBegin(TTypeDesc.TYPES_FIELD_DESC);
                oprot.writeListBegin(new TList((byte)12, struct.types.size()));
                for (final TTypeEntry _iter33 : struct.types) {
                    _iter33.write(oprot);
                }
                oprot.writeListEnd();
                oprot.writeFieldEnd();
            }
            oprot.writeFieldStop();
            oprot.writeStructEnd();
        }
    }
    
    private static class TTypeDescTupleSchemeFactory implements SchemeFactory
    {
        @Override
        public TTypeDescTupleScheme getScheme() {
            return new TTypeDescTupleScheme();
        }
    }
    
    private static class TTypeDescTupleScheme extends TupleScheme<TTypeDesc>
    {
        @Override
        public void write(final TProtocol prot, final TTypeDesc struct) throws TException {
            final TTupleProtocol oprot = (TTupleProtocol)prot;
            oprot.writeI32(struct.types.size());
            for (final TTypeEntry _iter34 : struct.types) {
                _iter34.write(oprot);
            }
        }
        
        @Override
        public void read(final TProtocol prot, final TTypeDesc struct) throws TException {
            final TTupleProtocol iprot = (TTupleProtocol)prot;
            final TList _list35 = new TList((byte)12, iprot.readI32());
            struct.types = (List<TTypeEntry>)new ArrayList(_list35.size);
            for (int _i36 = 0; _i36 < _list35.size; ++_i36) {
                final TTypeEntry _elem37 = new TTypeEntry();
                _elem37.read(iprot);
                struct.types.add(_elem37);
            }
            struct.setTypesIsSet(true);
        }
    }
}
