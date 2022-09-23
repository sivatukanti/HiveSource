// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hive.service.cli.thrift;

import org.apache.thrift.protocol.TTupleProtocol;
import org.apache.thrift.protocol.TMap;
import org.apache.thrift.protocol.TProtocolUtil;
import java.util.EnumSet;
import java.util.Collections;
import org.apache.thrift.meta_data.MapMetaData;
import org.apache.thrift.meta_data.StructMetaData;
import org.apache.thrift.meta_data.FieldValueMetaData;
import java.util.EnumMap;
import org.apache.thrift.scheme.TupleScheme;
import org.apache.thrift.scheme.StandardScheme;
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
import java.util.HashMap;
import org.apache.thrift.meta_data.FieldMetaData;
import org.apache.thrift.scheme.SchemeFactory;
import org.apache.thrift.scheme.IScheme;
import java.util.Map;
import org.apache.thrift.protocol.TField;
import org.apache.thrift.protocol.TStruct;
import java.io.Serializable;
import org.apache.thrift.TBase;

public class TTypeQualifiers implements TBase<TTypeQualifiers, _Fields>, Serializable, Cloneable
{
    private static final TStruct STRUCT_DESC;
    private static final TField QUALIFIERS_FIELD_DESC;
    private static final Map<Class<? extends IScheme>, SchemeFactory> schemes;
    private Map<String, TTypeQualifierValue> qualifiers;
    public static final Map<_Fields, FieldMetaData> metaDataMap;
    
    public TTypeQualifiers() {
    }
    
    public TTypeQualifiers(final Map<String, TTypeQualifierValue> qualifiers) {
        this();
        this.qualifiers = qualifiers;
    }
    
    public TTypeQualifiers(final TTypeQualifiers other) {
        if (other.isSetQualifiers()) {
            final Map<String, TTypeQualifierValue> __this__qualifiers = new HashMap<String, TTypeQualifierValue>();
            for (final Map.Entry<String, TTypeQualifierValue> other_element : other.qualifiers.entrySet()) {
                final String other_element_key = other_element.getKey();
                final TTypeQualifierValue other_element_value = other_element.getValue();
                final String __this__qualifiers_copy_key = other_element_key;
                final TTypeQualifierValue __this__qualifiers_copy_value = new TTypeQualifierValue(other_element_value);
                __this__qualifiers.put(__this__qualifiers_copy_key, __this__qualifiers_copy_value);
            }
            this.qualifiers = __this__qualifiers;
        }
    }
    
    @Override
    public TTypeQualifiers deepCopy() {
        return new TTypeQualifiers(this);
    }
    
    @Override
    public void clear() {
        this.qualifiers = null;
    }
    
    public int getQualifiersSize() {
        return (this.qualifiers == null) ? 0 : this.qualifiers.size();
    }
    
    public void putToQualifiers(final String key, final TTypeQualifierValue val) {
        if (this.qualifiers == null) {
            this.qualifiers = new HashMap<String, TTypeQualifierValue>();
        }
        this.qualifiers.put(key, val);
    }
    
    public Map<String, TTypeQualifierValue> getQualifiers() {
        return this.qualifiers;
    }
    
    public void setQualifiers(final Map<String, TTypeQualifierValue> qualifiers) {
        this.qualifiers = qualifiers;
    }
    
    public void unsetQualifiers() {
        this.qualifiers = null;
    }
    
    public boolean isSetQualifiers() {
        return this.qualifiers != null;
    }
    
    public void setQualifiersIsSet(final boolean value) {
        if (!value) {
            this.qualifiers = null;
        }
    }
    
    @Override
    public void setFieldValue(final _Fields field, final Object value) {
        switch (field) {
            case QUALIFIERS: {
                if (value == null) {
                    this.unsetQualifiers();
                    break;
                }
                this.setQualifiers((Map<String, TTypeQualifierValue>)value);
                break;
            }
        }
    }
    
    @Override
    public Object getFieldValue(final _Fields field) {
        switch (field) {
            case QUALIFIERS: {
                return this.getQualifiers();
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
            case QUALIFIERS: {
                return this.isSetQualifiers();
            }
            default: {
                throw new IllegalStateException();
            }
        }
    }
    
    @Override
    public boolean equals(final Object that) {
        return that != null && that instanceof TTypeQualifiers && this.equals((TTypeQualifiers)that);
    }
    
    public boolean equals(final TTypeQualifiers that) {
        if (that == null) {
            return false;
        }
        final boolean this_present_qualifiers = this.isSetQualifiers();
        final boolean that_present_qualifiers = that.isSetQualifiers();
        if (this_present_qualifiers || that_present_qualifiers) {
            if (!this_present_qualifiers || !that_present_qualifiers) {
                return false;
            }
            if (!this.qualifiers.equals(that.qualifiers)) {
                return false;
            }
        }
        return true;
    }
    
    @Override
    public int hashCode() {
        final HashCodeBuilder builder = new HashCodeBuilder();
        final boolean present_qualifiers = this.isSetQualifiers();
        builder.append(present_qualifiers);
        if (present_qualifiers) {
            builder.append(this.qualifiers);
        }
        return builder.toHashCode();
    }
    
    @Override
    public int compareTo(final TTypeQualifiers other) {
        if (!this.getClass().equals(other.getClass())) {
            return this.getClass().getName().compareTo(other.getClass().getName());
        }
        int lastComparison = 0;
        final TTypeQualifiers typedOther = other;
        lastComparison = Boolean.valueOf(this.isSetQualifiers()).compareTo(Boolean.valueOf(typedOther.isSetQualifiers()));
        if (lastComparison != 0) {
            return lastComparison;
        }
        if (this.isSetQualifiers()) {
            lastComparison = TBaseHelper.compareTo(this.qualifiers, typedOther.qualifiers);
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
        TTypeQualifiers.schemes.get(iprot.getScheme()).getScheme().read(iprot, this);
    }
    
    @Override
    public void write(final TProtocol oprot) throws TException {
        TTypeQualifiers.schemes.get(oprot.getScheme()).getScheme().write(oprot, this);
    }
    
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("TTypeQualifiers(");
        boolean first = true;
        sb.append("qualifiers:");
        if (this.qualifiers == null) {
            sb.append("null");
        }
        else {
            sb.append(this.qualifiers);
        }
        first = false;
        sb.append(")");
        return sb.toString();
    }
    
    public void validate() throws TException {
        if (!this.isSetQualifiers()) {
            throw new TProtocolException("Required field 'qualifiers' is unset! Struct:" + this.toString());
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
        STRUCT_DESC = new TStruct("TTypeQualifiers");
        QUALIFIERS_FIELD_DESC = new TField("qualifiers", (byte)13, (short)1);
        (schemes = new HashMap<Class<? extends IScheme>, SchemeFactory>()).put(StandardScheme.class, new TTypeQualifiersStandardSchemeFactory());
        TTypeQualifiers.schemes.put(TupleScheme.class, new TTypeQualifiersTupleSchemeFactory());
        final Map<_Fields, FieldMetaData> tmpMap = new EnumMap<_Fields, FieldMetaData>(_Fields.class);
        tmpMap.put(_Fields.QUALIFIERS, new FieldMetaData("qualifiers", (byte)1, new MapMetaData((byte)13, new FieldValueMetaData((byte)11), new StructMetaData((byte)12, TTypeQualifierValue.class))));
        FieldMetaData.addStructMetaDataMap(TTypeQualifiers.class, metaDataMap = Collections.unmodifiableMap((Map<? extends _Fields, ? extends FieldMetaData>)tmpMap));
    }
    
    public enum _Fields implements TFieldIdEnum
    {
        QUALIFIERS((short)1, "qualifiers");
        
        private static final Map<String, _Fields> byName;
        private final short _thriftId;
        private final String _fieldName;
        
        public static _Fields findByThriftId(final int fieldId) {
            switch (fieldId) {
                case 1: {
                    return _Fields.QUALIFIERS;
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
    
    private static class TTypeQualifiersStandardSchemeFactory implements SchemeFactory
    {
        @Override
        public TTypeQualifiersStandardScheme getScheme() {
            return new TTypeQualifiersStandardScheme();
        }
    }
    
    private static class TTypeQualifiersStandardScheme extends StandardScheme<TTypeQualifiers>
    {
        @Override
        public void read(final TProtocol iprot, final TTypeQualifiers struct) throws TException {
            iprot.readStructBegin();
            while (true) {
                final TField schemeField = iprot.readFieldBegin();
                if (schemeField.type == 0) {
                    break;
                }
                switch (schemeField.id) {
                    case 1: {
                        if (schemeField.type == 13) {
                            final TMap _map0 = iprot.readMapBegin();
                            struct.qualifiers = (Map<String, TTypeQualifierValue>)new HashMap(2 * _map0.size);
                            for (int _i1 = 0; _i1 < _map0.size; ++_i1) {
                                final String _key2 = iprot.readString();
                                final TTypeQualifierValue _val3 = new TTypeQualifierValue();
                                _val3.read(iprot);
                                struct.qualifiers.put(_key2, _val3);
                            }
                            iprot.readMapEnd();
                            struct.setQualifiersIsSet(true);
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
        public void write(final TProtocol oprot, final TTypeQualifiers struct) throws TException {
            struct.validate();
            oprot.writeStructBegin(TTypeQualifiers.STRUCT_DESC);
            if (struct.qualifiers != null) {
                oprot.writeFieldBegin(TTypeQualifiers.QUALIFIERS_FIELD_DESC);
                oprot.writeMapBegin(new TMap((byte)11, (byte)12, struct.qualifiers.size()));
                for (final Map.Entry<String, TTypeQualifierValue> _iter4 : struct.qualifiers.entrySet()) {
                    oprot.writeString(_iter4.getKey());
                    _iter4.getValue().write(oprot);
                }
                oprot.writeMapEnd();
                oprot.writeFieldEnd();
            }
            oprot.writeFieldStop();
            oprot.writeStructEnd();
        }
    }
    
    private static class TTypeQualifiersTupleSchemeFactory implements SchemeFactory
    {
        @Override
        public TTypeQualifiersTupleScheme getScheme() {
            return new TTypeQualifiersTupleScheme();
        }
    }
    
    private static class TTypeQualifiersTupleScheme extends TupleScheme<TTypeQualifiers>
    {
        @Override
        public void write(final TProtocol prot, final TTypeQualifiers struct) throws TException {
            final TTupleProtocol oprot = (TTupleProtocol)prot;
            oprot.writeI32(struct.qualifiers.size());
            for (final Map.Entry<String, TTypeQualifierValue> _iter5 : struct.qualifiers.entrySet()) {
                oprot.writeString(_iter5.getKey());
                _iter5.getValue().write(oprot);
            }
        }
        
        @Override
        public void read(final TProtocol prot, final TTypeQualifiers struct) throws TException {
            final TTupleProtocol iprot = (TTupleProtocol)prot;
            final TMap _map6 = new TMap((byte)11, (byte)12, iprot.readI32());
            struct.qualifiers = (Map<String, TTypeQualifierValue>)new HashMap(2 * _map6.size);
            for (int _i7 = 0; _i7 < _map6.size; ++_i7) {
                final String _key8 = iprot.readString();
                final TTypeQualifierValue _val9 = new TTypeQualifierValue();
                _val9.read(iprot);
                struct.qualifiers.put(_key8, _val9);
            }
            struct.setQualifiersIsSet(true);
        }
    }
}
