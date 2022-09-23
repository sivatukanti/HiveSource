// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.hive.serde2.thrift.test;

import java.util.BitSet;
import org.apache.thrift.protocol.TTupleProtocol;
import org.apache.thrift.protocol.TSet;
import org.apache.thrift.protocol.TProtocolUtil;
import java.util.EnumSet;
import java.util.Collections;
import org.apache.thrift.meta_data.FieldValueMetaData;
import org.apache.thrift.meta_data.SetMetaData;
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
import org.apache.thrift.TException;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.TBaseHelper;
import org.apache.commons.lang.builder.HashCodeBuilder;
import java.util.Iterator;
import java.util.HashSet;
import org.apache.thrift.meta_data.FieldMetaData;
import java.util.Set;
import org.apache.thrift.scheme.SchemeFactory;
import org.apache.thrift.scheme.IScheme;
import java.util.Map;
import org.apache.thrift.protocol.TField;
import org.apache.thrift.protocol.TStruct;
import java.io.Serializable;
import org.apache.thrift.TBase;

public class SetIntString implements TBase<SetIntString, _Fields>, Serializable, Cloneable
{
    private static final TStruct STRUCT_DESC;
    private static final TField S_INT_STRING_FIELD_DESC;
    private static final TField A_STRING_FIELD_DESC;
    private static final Map<Class<? extends IScheme>, SchemeFactory> schemes;
    private Set<IntString> sIntString;
    private String aString;
    public static final Map<_Fields, FieldMetaData> metaDataMap;
    
    public SetIntString() {
    }
    
    public SetIntString(final Set<IntString> sIntString, final String aString) {
        this();
        this.sIntString = sIntString;
        this.aString = aString;
    }
    
    public SetIntString(final SetIntString other) {
        if (other.isSetSIntString()) {
            final Set<IntString> __this__sIntString = new HashSet<IntString>();
            for (final IntString other_element : other.sIntString) {
                __this__sIntString.add(new IntString(other_element));
            }
            this.sIntString = __this__sIntString;
        }
        if (other.isSetAString()) {
            this.aString = other.aString;
        }
    }
    
    @Override
    public SetIntString deepCopy() {
        return new SetIntString(this);
    }
    
    @Override
    public void clear() {
        this.sIntString = null;
        this.aString = null;
    }
    
    public int getSIntStringSize() {
        return (this.sIntString == null) ? 0 : this.sIntString.size();
    }
    
    public Iterator<IntString> getSIntStringIterator() {
        return (this.sIntString == null) ? null : this.sIntString.iterator();
    }
    
    public void addToSIntString(final IntString elem) {
        if (this.sIntString == null) {
            this.sIntString = new HashSet<IntString>();
        }
        this.sIntString.add(elem);
    }
    
    public Set<IntString> getSIntString() {
        return this.sIntString;
    }
    
    public void setSIntString(final Set<IntString> sIntString) {
        this.sIntString = sIntString;
    }
    
    public void unsetSIntString() {
        this.sIntString = null;
    }
    
    public boolean isSetSIntString() {
        return this.sIntString != null;
    }
    
    public void setSIntStringIsSet(final boolean value) {
        if (!value) {
            this.sIntString = null;
        }
    }
    
    public String getAString() {
        return this.aString;
    }
    
    public void setAString(final String aString) {
        this.aString = aString;
    }
    
    public void unsetAString() {
        this.aString = null;
    }
    
    public boolean isSetAString() {
        return this.aString != null;
    }
    
    public void setAStringIsSet(final boolean value) {
        if (!value) {
            this.aString = null;
        }
    }
    
    @Override
    public void setFieldValue(final _Fields field, final Object value) {
        switch (field) {
            case S_INT_STRING: {
                if (value == null) {
                    this.unsetSIntString();
                    break;
                }
                this.setSIntString((Set<IntString>)value);
                break;
            }
            case A_STRING: {
                if (value == null) {
                    this.unsetAString();
                    break;
                }
                this.setAString((String)value);
                break;
            }
        }
    }
    
    @Override
    public Object getFieldValue(final _Fields field) {
        switch (field) {
            case S_INT_STRING: {
                return this.getSIntString();
            }
            case A_STRING: {
                return this.getAString();
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
            case S_INT_STRING: {
                return this.isSetSIntString();
            }
            case A_STRING: {
                return this.isSetAString();
            }
            default: {
                throw new IllegalStateException();
            }
        }
    }
    
    @Override
    public boolean equals(final Object that) {
        return that != null && that instanceof SetIntString && this.equals((SetIntString)that);
    }
    
    public boolean equals(final SetIntString that) {
        if (that == null) {
            return false;
        }
        final boolean this_present_sIntString = this.isSetSIntString();
        final boolean that_present_sIntString = that.isSetSIntString();
        if (this_present_sIntString || that_present_sIntString) {
            if (!this_present_sIntString || !that_present_sIntString) {
                return false;
            }
            if (!this.sIntString.equals(that.sIntString)) {
                return false;
            }
        }
        final boolean this_present_aString = this.isSetAString();
        final boolean that_present_aString = that.isSetAString();
        if (this_present_aString || that_present_aString) {
            if (!this_present_aString || !that_present_aString) {
                return false;
            }
            if (!this.aString.equals(that.aString)) {
                return false;
            }
        }
        return true;
    }
    
    @Override
    public int hashCode() {
        final HashCodeBuilder builder = new HashCodeBuilder();
        final boolean present_sIntString = this.isSetSIntString();
        builder.append(present_sIntString);
        if (present_sIntString) {
            builder.append(this.sIntString);
        }
        final boolean present_aString = this.isSetAString();
        builder.append(present_aString);
        if (present_aString) {
            builder.append(this.aString);
        }
        return builder.toHashCode();
    }
    
    @Override
    public int compareTo(final SetIntString other) {
        if (!this.getClass().equals(other.getClass())) {
            return this.getClass().getName().compareTo(other.getClass().getName());
        }
        int lastComparison = 0;
        final SetIntString typedOther = other;
        lastComparison = Boolean.valueOf(this.isSetSIntString()).compareTo(Boolean.valueOf(typedOther.isSetSIntString()));
        if (lastComparison != 0) {
            return lastComparison;
        }
        if (this.isSetSIntString()) {
            lastComparison = TBaseHelper.compareTo(this.sIntString, typedOther.sIntString);
            if (lastComparison != 0) {
                return lastComparison;
            }
        }
        lastComparison = Boolean.valueOf(this.isSetAString()).compareTo(Boolean.valueOf(typedOther.isSetAString()));
        if (lastComparison != 0) {
            return lastComparison;
        }
        if (this.isSetAString()) {
            lastComparison = TBaseHelper.compareTo(this.aString, typedOther.aString);
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
        SetIntString.schemes.get(iprot.getScheme()).getScheme().read(iprot, this);
    }
    
    @Override
    public void write(final TProtocol oprot) throws TException {
        SetIntString.schemes.get(oprot.getScheme()).getScheme().write(oprot, this);
    }
    
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("SetIntString(");
        boolean first = true;
        sb.append("sIntString:");
        if (this.sIntString == null) {
            sb.append("null");
        }
        else {
            sb.append(this.sIntString);
        }
        first = false;
        if (!first) {
            sb.append(", ");
        }
        sb.append("aString:");
        if (this.aString == null) {
            sb.append("null");
        }
        else {
            sb.append(this.aString);
        }
        first = false;
        sb.append(")");
        return sb.toString();
    }
    
    public void validate() throws TException {
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
        STRUCT_DESC = new TStruct("SetIntString");
        S_INT_STRING_FIELD_DESC = new TField("sIntString", (byte)14, (short)1);
        A_STRING_FIELD_DESC = new TField("aString", (byte)11, (short)2);
        (schemes = new HashMap<Class<? extends IScheme>, SchemeFactory>()).put(StandardScheme.class, new SetIntStringStandardSchemeFactory());
        SetIntString.schemes.put(TupleScheme.class, new SetIntStringTupleSchemeFactory());
        final Map<_Fields, FieldMetaData> tmpMap = new EnumMap<_Fields, FieldMetaData>(_Fields.class);
        tmpMap.put(_Fields.S_INT_STRING, new FieldMetaData("sIntString", (byte)3, new SetMetaData((byte)14, new StructMetaData((byte)12, IntString.class))));
        tmpMap.put(_Fields.A_STRING, new FieldMetaData("aString", (byte)3, new FieldValueMetaData((byte)11)));
        FieldMetaData.addStructMetaDataMap(SetIntString.class, metaDataMap = Collections.unmodifiableMap((Map<? extends _Fields, ? extends FieldMetaData>)tmpMap));
    }
    
    public enum _Fields implements TFieldIdEnum
    {
        S_INT_STRING((short)1, "sIntString"), 
        A_STRING((short)2, "aString");
        
        private static final Map<String, _Fields> byName;
        private final short _thriftId;
        private final String _fieldName;
        
        public static _Fields findByThriftId(final int fieldId) {
            switch (fieldId) {
                case 1: {
                    return _Fields.S_INT_STRING;
                }
                case 2: {
                    return _Fields.A_STRING;
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
    
    private static class SetIntStringStandardSchemeFactory implements SchemeFactory
    {
        @Override
        public SetIntStringStandardScheme getScheme() {
            return new SetIntStringStandardScheme();
        }
    }
    
    private static class SetIntStringStandardScheme extends StandardScheme<SetIntString>
    {
        @Override
        public void read(final TProtocol iprot, final SetIntString struct) throws TException {
            iprot.readStructBegin();
            while (true) {
                final TField schemeField = iprot.readFieldBegin();
                if (schemeField.type == 0) {
                    break;
                }
                switch (schemeField.id) {
                    case 1: {
                        if (schemeField.type == 14) {
                            final TSet _set82 = iprot.readSetBegin();
                            struct.sIntString = (Set<IntString>)new HashSet(2 * _set82.size);
                            for (int _i83 = 0; _i83 < _set82.size; ++_i83) {
                                final IntString _elem84 = new IntString();
                                _elem84.read(iprot);
                                struct.sIntString.add(_elem84);
                            }
                            iprot.readSetEnd();
                            struct.setSIntStringIsSet(true);
                            break;
                        }
                        TProtocolUtil.skip(iprot, schemeField.type);
                        break;
                    }
                    case 2: {
                        if (schemeField.type == 11) {
                            struct.aString = iprot.readString();
                            struct.setAStringIsSet(true);
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
        public void write(final TProtocol oprot, final SetIntString struct) throws TException {
            struct.validate();
            oprot.writeStructBegin(SetIntString.STRUCT_DESC);
            if (struct.sIntString != null) {
                oprot.writeFieldBegin(SetIntString.S_INT_STRING_FIELD_DESC);
                oprot.writeSetBegin(new TSet((byte)12, struct.sIntString.size()));
                for (final IntString _iter85 : struct.sIntString) {
                    _iter85.write(oprot);
                }
                oprot.writeSetEnd();
                oprot.writeFieldEnd();
            }
            if (struct.aString != null) {
                oprot.writeFieldBegin(SetIntString.A_STRING_FIELD_DESC);
                oprot.writeString(struct.aString);
                oprot.writeFieldEnd();
            }
            oprot.writeFieldStop();
            oprot.writeStructEnd();
        }
    }
    
    private static class SetIntStringTupleSchemeFactory implements SchemeFactory
    {
        @Override
        public SetIntStringTupleScheme getScheme() {
            return new SetIntStringTupleScheme();
        }
    }
    
    private static class SetIntStringTupleScheme extends TupleScheme<SetIntString>
    {
        @Override
        public void write(final TProtocol prot, final SetIntString struct) throws TException {
            final TTupleProtocol oprot = (TTupleProtocol)prot;
            final BitSet optionals = new BitSet();
            if (struct.isSetSIntString()) {
                optionals.set(0);
            }
            if (struct.isSetAString()) {
                optionals.set(1);
            }
            oprot.writeBitSet(optionals, 2);
            if (struct.isSetSIntString()) {
                oprot.writeI32(struct.sIntString.size());
                for (final IntString _iter86 : struct.sIntString) {
                    _iter86.write(oprot);
                }
            }
            if (struct.isSetAString()) {
                oprot.writeString(struct.aString);
            }
        }
        
        @Override
        public void read(final TProtocol prot, final SetIntString struct) throws TException {
            final TTupleProtocol iprot = (TTupleProtocol)prot;
            final BitSet incoming = iprot.readBitSet(2);
            if (incoming.get(0)) {
                final TSet _set87 = new TSet((byte)12, iprot.readI32());
                struct.sIntString = (Set<IntString>)new HashSet(2 * _set87.size);
                for (int _i88 = 0; _i88 < _set87.size; ++_i88) {
                    final IntString _elem89 = new IntString();
                    _elem89.read(iprot);
                    struct.sIntString.add(_elem89);
                }
                struct.setSIntStringIsSet(true);
            }
            if (incoming.get(1)) {
                struct.aString = iprot.readString();
                struct.setAStringIsSet(true);
            }
        }
    }
}
