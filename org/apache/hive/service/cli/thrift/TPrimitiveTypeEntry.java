// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hive.service.cli.thrift;

import java.util.BitSet;
import org.apache.thrift.protocol.TTupleProtocol;
import org.apache.thrift.protocol.TProtocolUtil;
import java.util.Iterator;
import java.util.EnumSet;
import java.util.Collections;
import org.apache.thrift.meta_data.StructMetaData;
import org.apache.thrift.meta_data.FieldValueMetaData;
import org.apache.thrift.TEnum;
import org.apache.thrift.meta_data.EnumMetaData;
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
import org.apache.thrift.meta_data.FieldMetaData;
import org.apache.thrift.scheme.SchemeFactory;
import org.apache.thrift.scheme.IScheme;
import java.util.Map;
import org.apache.thrift.protocol.TField;
import org.apache.thrift.protocol.TStruct;
import java.io.Serializable;
import org.apache.thrift.TBase;

public class TPrimitiveTypeEntry implements TBase<TPrimitiveTypeEntry, _Fields>, Serializable, Cloneable
{
    private static final TStruct STRUCT_DESC;
    private static final TField TYPE_FIELD_DESC;
    private static final TField TYPE_QUALIFIERS_FIELD_DESC;
    private static final Map<Class<? extends IScheme>, SchemeFactory> schemes;
    private TTypeId type;
    private TTypeQualifiers typeQualifiers;
    private _Fields[] optionals;
    public static final Map<_Fields, FieldMetaData> metaDataMap;
    
    public TPrimitiveTypeEntry() {
        this.optionals = new _Fields[] { _Fields.TYPE_QUALIFIERS };
    }
    
    public TPrimitiveTypeEntry(final TTypeId type) {
        this();
        this.type = type;
    }
    
    public TPrimitiveTypeEntry(final TPrimitiveTypeEntry other) {
        this.optionals = new _Fields[] { _Fields.TYPE_QUALIFIERS };
        if (other.isSetType()) {
            this.type = other.type;
        }
        if (other.isSetTypeQualifiers()) {
            this.typeQualifiers = new TTypeQualifiers(other.typeQualifiers);
        }
    }
    
    @Override
    public TPrimitiveTypeEntry deepCopy() {
        return new TPrimitiveTypeEntry(this);
    }
    
    @Override
    public void clear() {
        this.type = null;
        this.typeQualifiers = null;
    }
    
    public TTypeId getType() {
        return this.type;
    }
    
    public void setType(final TTypeId type) {
        this.type = type;
    }
    
    public void unsetType() {
        this.type = null;
    }
    
    public boolean isSetType() {
        return this.type != null;
    }
    
    public void setTypeIsSet(final boolean value) {
        if (!value) {
            this.type = null;
        }
    }
    
    public TTypeQualifiers getTypeQualifiers() {
        return this.typeQualifiers;
    }
    
    public void setTypeQualifiers(final TTypeQualifiers typeQualifiers) {
        this.typeQualifiers = typeQualifiers;
    }
    
    public void unsetTypeQualifiers() {
        this.typeQualifiers = null;
    }
    
    public boolean isSetTypeQualifiers() {
        return this.typeQualifiers != null;
    }
    
    public void setTypeQualifiersIsSet(final boolean value) {
        if (!value) {
            this.typeQualifiers = null;
        }
    }
    
    @Override
    public void setFieldValue(final _Fields field, final Object value) {
        switch (field) {
            case TYPE: {
                if (value == null) {
                    this.unsetType();
                    break;
                }
                this.setType((TTypeId)value);
                break;
            }
            case TYPE_QUALIFIERS: {
                if (value == null) {
                    this.unsetTypeQualifiers();
                    break;
                }
                this.setTypeQualifiers((TTypeQualifiers)value);
                break;
            }
        }
    }
    
    @Override
    public Object getFieldValue(final _Fields field) {
        switch (field) {
            case TYPE: {
                return this.getType();
            }
            case TYPE_QUALIFIERS: {
                return this.getTypeQualifiers();
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
            case TYPE: {
                return this.isSetType();
            }
            case TYPE_QUALIFIERS: {
                return this.isSetTypeQualifiers();
            }
            default: {
                throw new IllegalStateException();
            }
        }
    }
    
    @Override
    public boolean equals(final Object that) {
        return that != null && that instanceof TPrimitiveTypeEntry && this.equals((TPrimitiveTypeEntry)that);
    }
    
    public boolean equals(final TPrimitiveTypeEntry that) {
        if (that == null) {
            return false;
        }
        final boolean this_present_type = this.isSetType();
        final boolean that_present_type = that.isSetType();
        if (this_present_type || that_present_type) {
            if (!this_present_type || !that_present_type) {
                return false;
            }
            if (!this.type.equals(that.type)) {
                return false;
            }
        }
        final boolean this_present_typeQualifiers = this.isSetTypeQualifiers();
        final boolean that_present_typeQualifiers = that.isSetTypeQualifiers();
        if (this_present_typeQualifiers || that_present_typeQualifiers) {
            if (!this_present_typeQualifiers || !that_present_typeQualifiers) {
                return false;
            }
            if (!this.typeQualifiers.equals(that.typeQualifiers)) {
                return false;
            }
        }
        return true;
    }
    
    @Override
    public int hashCode() {
        final HashCodeBuilder builder = new HashCodeBuilder();
        final boolean present_type = this.isSetType();
        builder.append(present_type);
        if (present_type) {
            builder.append(this.type.getValue());
        }
        final boolean present_typeQualifiers = this.isSetTypeQualifiers();
        builder.append(present_typeQualifiers);
        if (present_typeQualifiers) {
            builder.append(this.typeQualifiers);
        }
        return builder.toHashCode();
    }
    
    @Override
    public int compareTo(final TPrimitiveTypeEntry other) {
        if (!this.getClass().equals(other.getClass())) {
            return this.getClass().getName().compareTo(other.getClass().getName());
        }
        int lastComparison = 0;
        final TPrimitiveTypeEntry typedOther = other;
        lastComparison = Boolean.valueOf(this.isSetType()).compareTo(Boolean.valueOf(typedOther.isSetType()));
        if (lastComparison != 0) {
            return lastComparison;
        }
        if (this.isSetType()) {
            lastComparison = TBaseHelper.compareTo(this.type, typedOther.type);
            if (lastComparison != 0) {
                return lastComparison;
            }
        }
        lastComparison = Boolean.valueOf(this.isSetTypeQualifiers()).compareTo(Boolean.valueOf(typedOther.isSetTypeQualifiers()));
        if (lastComparison != 0) {
            return lastComparison;
        }
        if (this.isSetTypeQualifiers()) {
            lastComparison = TBaseHelper.compareTo(this.typeQualifiers, typedOther.typeQualifiers);
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
        TPrimitiveTypeEntry.schemes.get(iprot.getScheme()).getScheme().read(iprot, this);
    }
    
    @Override
    public void write(final TProtocol oprot) throws TException {
        TPrimitiveTypeEntry.schemes.get(oprot.getScheme()).getScheme().write(oprot, this);
    }
    
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("TPrimitiveTypeEntry(");
        boolean first = true;
        sb.append("type:");
        if (this.type == null) {
            sb.append("null");
        }
        else {
            sb.append(this.type);
        }
        first = false;
        if (this.isSetTypeQualifiers()) {
            if (!first) {
                sb.append(", ");
            }
            sb.append("typeQualifiers:");
            if (this.typeQualifiers == null) {
                sb.append("null");
            }
            else {
                sb.append(this.typeQualifiers);
            }
            first = false;
        }
        sb.append(")");
        return sb.toString();
    }
    
    public void validate() throws TException {
        if (!this.isSetType()) {
            throw new TProtocolException("Required field 'type' is unset! Struct:" + this.toString());
        }
        if (this.typeQualifiers != null) {
            this.typeQualifiers.validate();
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
        STRUCT_DESC = new TStruct("TPrimitiveTypeEntry");
        TYPE_FIELD_DESC = new TField("type", (byte)8, (short)1);
        TYPE_QUALIFIERS_FIELD_DESC = new TField("typeQualifiers", (byte)12, (short)2);
        (schemes = new HashMap<Class<? extends IScheme>, SchemeFactory>()).put(StandardScheme.class, new TPrimitiveTypeEntryStandardSchemeFactory());
        TPrimitiveTypeEntry.schemes.put(TupleScheme.class, new TPrimitiveTypeEntryTupleSchemeFactory());
        final Map<_Fields, FieldMetaData> tmpMap = new EnumMap<_Fields, FieldMetaData>(_Fields.class);
        tmpMap.put(_Fields.TYPE, new FieldMetaData("type", (byte)1, new EnumMetaData((byte)16, TTypeId.class)));
        tmpMap.put(_Fields.TYPE_QUALIFIERS, new FieldMetaData("typeQualifiers", (byte)2, new StructMetaData((byte)12, TTypeQualifiers.class)));
        FieldMetaData.addStructMetaDataMap(TPrimitiveTypeEntry.class, metaDataMap = Collections.unmodifiableMap((Map<? extends _Fields, ? extends FieldMetaData>)tmpMap));
    }
    
    public enum _Fields implements TFieldIdEnum
    {
        TYPE((short)1, "type"), 
        TYPE_QUALIFIERS((short)2, "typeQualifiers");
        
        private static final Map<String, _Fields> byName;
        private final short _thriftId;
        private final String _fieldName;
        
        public static _Fields findByThriftId(final int fieldId) {
            switch (fieldId) {
                case 1: {
                    return _Fields.TYPE;
                }
                case 2: {
                    return _Fields.TYPE_QUALIFIERS;
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
    
    private static class TPrimitiveTypeEntryStandardSchemeFactory implements SchemeFactory
    {
        @Override
        public TPrimitiveTypeEntryStandardScheme getScheme() {
            return new TPrimitiveTypeEntryStandardScheme();
        }
    }
    
    private static class TPrimitiveTypeEntryStandardScheme extends StandardScheme<TPrimitiveTypeEntry>
    {
        @Override
        public void read(final TProtocol iprot, final TPrimitiveTypeEntry struct) throws TException {
            iprot.readStructBegin();
            while (true) {
                final TField schemeField = iprot.readFieldBegin();
                if (schemeField.type == 0) {
                    break;
                }
                switch (schemeField.id) {
                    case 1: {
                        if (schemeField.type == 8) {
                            struct.type = TTypeId.findByValue(iprot.readI32());
                            struct.setTypeIsSet(true);
                            break;
                        }
                        TProtocolUtil.skip(iprot, schemeField.type);
                        break;
                    }
                    case 2: {
                        if (schemeField.type == 12) {
                            struct.typeQualifiers = new TTypeQualifiers();
                            struct.typeQualifiers.read(iprot);
                            struct.setTypeQualifiersIsSet(true);
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
        public void write(final TProtocol oprot, final TPrimitiveTypeEntry struct) throws TException {
            struct.validate();
            oprot.writeStructBegin(TPrimitiveTypeEntry.STRUCT_DESC);
            if (struct.type != null) {
                oprot.writeFieldBegin(TPrimitiveTypeEntry.TYPE_FIELD_DESC);
                oprot.writeI32(struct.type.getValue());
                oprot.writeFieldEnd();
            }
            if (struct.typeQualifiers != null && struct.isSetTypeQualifiers()) {
                oprot.writeFieldBegin(TPrimitiveTypeEntry.TYPE_QUALIFIERS_FIELD_DESC);
                struct.typeQualifiers.write(oprot);
                oprot.writeFieldEnd();
            }
            oprot.writeFieldStop();
            oprot.writeStructEnd();
        }
    }
    
    private static class TPrimitiveTypeEntryTupleSchemeFactory implements SchemeFactory
    {
        @Override
        public TPrimitiveTypeEntryTupleScheme getScheme() {
            return new TPrimitiveTypeEntryTupleScheme();
        }
    }
    
    private static class TPrimitiveTypeEntryTupleScheme extends TupleScheme<TPrimitiveTypeEntry>
    {
        @Override
        public void write(final TProtocol prot, final TPrimitiveTypeEntry struct) throws TException {
            final TTupleProtocol oprot = (TTupleProtocol)prot;
            oprot.writeI32(struct.type.getValue());
            final BitSet optionals = new BitSet();
            if (struct.isSetTypeQualifiers()) {
                optionals.set(0);
            }
            oprot.writeBitSet(optionals, 1);
            if (struct.isSetTypeQualifiers()) {
                struct.typeQualifiers.write(oprot);
            }
        }
        
        @Override
        public void read(final TProtocol prot, final TPrimitiveTypeEntry struct) throws TException {
            final TTupleProtocol iprot = (TTupleProtocol)prot;
            struct.type = TTypeId.findByValue(iprot.readI32());
            struct.setTypeIsSet(true);
            final BitSet incoming = iprot.readBitSet(1);
            if (incoming.get(0)) {
                struct.typeQualifiers = new TTypeQualifiers();
                struct.typeQualifiers.read(iprot);
                struct.setTypeQualifiersIsSet(true);
            }
        }
    }
}
