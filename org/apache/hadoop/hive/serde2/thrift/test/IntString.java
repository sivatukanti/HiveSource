// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.hive.serde2.thrift.test;

import java.util.BitSet;
import org.apache.thrift.protocol.TTupleProtocol;
import org.apache.thrift.protocol.TProtocolUtil;
import java.util.Iterator;
import java.util.EnumSet;
import java.util.Collections;
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
import org.apache.thrift.TException;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.TBaseHelper;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.thrift.EncodingUtils;
import org.apache.thrift.meta_data.FieldMetaData;
import org.apache.thrift.scheme.SchemeFactory;
import org.apache.thrift.scheme.IScheme;
import java.util.Map;
import org.apache.thrift.protocol.TField;
import org.apache.thrift.protocol.TStruct;
import java.io.Serializable;
import org.apache.thrift.TBase;

public class IntString implements TBase<IntString, _Fields>, Serializable, Cloneable
{
    private static final TStruct STRUCT_DESC;
    private static final TField MYINT_FIELD_DESC;
    private static final TField MY_STRING_FIELD_DESC;
    private static final TField UNDERSCORE_INT_FIELD_DESC;
    private static final Map<Class<? extends IScheme>, SchemeFactory> schemes;
    private int myint;
    private String myString;
    private int underscore_int;
    private static final int __MYINT_ISSET_ID = 0;
    private static final int __UNDERSCORE_INT_ISSET_ID = 1;
    private byte __isset_bitfield;
    public static final Map<_Fields, FieldMetaData> metaDataMap;
    
    public IntString() {
        this.__isset_bitfield = 0;
    }
    
    public IntString(final int myint, final String myString, final int underscore_int) {
        this();
        this.myint = myint;
        this.setMyintIsSet(true);
        this.myString = myString;
        this.underscore_int = underscore_int;
        this.setUnderscore_intIsSet(true);
    }
    
    public IntString(final IntString other) {
        this.__isset_bitfield = 0;
        this.__isset_bitfield = other.__isset_bitfield;
        this.myint = other.myint;
        if (other.isSetMyString()) {
            this.myString = other.myString;
        }
        this.underscore_int = other.underscore_int;
    }
    
    @Override
    public IntString deepCopy() {
        return new IntString(this);
    }
    
    @Override
    public void clear() {
        this.setMyintIsSet(false);
        this.myint = 0;
        this.myString = null;
        this.setUnderscore_intIsSet(false);
        this.underscore_int = 0;
    }
    
    public int getMyint() {
        return this.myint;
    }
    
    public void setMyint(final int myint) {
        this.myint = myint;
        this.setMyintIsSet(true);
    }
    
    public void unsetMyint() {
        this.__isset_bitfield = EncodingUtils.clearBit(this.__isset_bitfield, 0);
    }
    
    public boolean isSetMyint() {
        return EncodingUtils.testBit(this.__isset_bitfield, 0);
    }
    
    public void setMyintIsSet(final boolean value) {
        this.__isset_bitfield = EncodingUtils.setBit(this.__isset_bitfield, 0, value);
    }
    
    public String getMyString() {
        return this.myString;
    }
    
    public void setMyString(final String myString) {
        this.myString = myString;
    }
    
    public void unsetMyString() {
        this.myString = null;
    }
    
    public boolean isSetMyString() {
        return this.myString != null;
    }
    
    public void setMyStringIsSet(final boolean value) {
        if (!value) {
            this.myString = null;
        }
    }
    
    public int getUnderscore_int() {
        return this.underscore_int;
    }
    
    public void setUnderscore_int(final int underscore_int) {
        this.underscore_int = underscore_int;
        this.setUnderscore_intIsSet(true);
    }
    
    public void unsetUnderscore_int() {
        this.__isset_bitfield = EncodingUtils.clearBit(this.__isset_bitfield, 1);
    }
    
    public boolean isSetUnderscore_int() {
        return EncodingUtils.testBit(this.__isset_bitfield, 1);
    }
    
    public void setUnderscore_intIsSet(final boolean value) {
        this.__isset_bitfield = EncodingUtils.setBit(this.__isset_bitfield, 1, value);
    }
    
    @Override
    public void setFieldValue(final _Fields field, final Object value) {
        switch (field) {
            case MYINT: {
                if (value == null) {
                    this.unsetMyint();
                    break;
                }
                this.setMyint((int)value);
                break;
            }
            case MY_STRING: {
                if (value == null) {
                    this.unsetMyString();
                    break;
                }
                this.setMyString((String)value);
                break;
            }
            case UNDERSCORE_INT: {
                if (value == null) {
                    this.unsetUnderscore_int();
                    break;
                }
                this.setUnderscore_int((int)value);
                break;
            }
        }
    }
    
    @Override
    public Object getFieldValue(final _Fields field) {
        switch (field) {
            case MYINT: {
                return this.getMyint();
            }
            case MY_STRING: {
                return this.getMyString();
            }
            case UNDERSCORE_INT: {
                return this.getUnderscore_int();
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
            case MYINT: {
                return this.isSetMyint();
            }
            case MY_STRING: {
                return this.isSetMyString();
            }
            case UNDERSCORE_INT: {
                return this.isSetUnderscore_int();
            }
            default: {
                throw new IllegalStateException();
            }
        }
    }
    
    @Override
    public boolean equals(final Object that) {
        return that != null && that instanceof IntString && this.equals((IntString)that);
    }
    
    public boolean equals(final IntString that) {
        if (that == null) {
            return false;
        }
        final boolean this_present_myint = true;
        final boolean that_present_myint = true;
        if (this_present_myint || that_present_myint) {
            if (!this_present_myint || !that_present_myint) {
                return false;
            }
            if (this.myint != that.myint) {
                return false;
            }
        }
        final boolean this_present_myString = this.isSetMyString();
        final boolean that_present_myString = that.isSetMyString();
        if (this_present_myString || that_present_myString) {
            if (!this_present_myString || !that_present_myString) {
                return false;
            }
            if (!this.myString.equals(that.myString)) {
                return false;
            }
        }
        final boolean this_present_underscore_int = true;
        final boolean that_present_underscore_int = true;
        if (this_present_underscore_int || that_present_underscore_int) {
            if (!this_present_underscore_int || !that_present_underscore_int) {
                return false;
            }
            if (this.underscore_int != that.underscore_int) {
                return false;
            }
        }
        return true;
    }
    
    @Override
    public int hashCode() {
        final HashCodeBuilder builder = new HashCodeBuilder();
        final boolean present_myint = true;
        builder.append(present_myint);
        if (present_myint) {
            builder.append(this.myint);
        }
        final boolean present_myString = this.isSetMyString();
        builder.append(present_myString);
        if (present_myString) {
            builder.append(this.myString);
        }
        final boolean present_underscore_int = true;
        builder.append(present_underscore_int);
        if (present_underscore_int) {
            builder.append(this.underscore_int);
        }
        return builder.toHashCode();
    }
    
    @Override
    public int compareTo(final IntString other) {
        if (!this.getClass().equals(other.getClass())) {
            return this.getClass().getName().compareTo(other.getClass().getName());
        }
        int lastComparison = 0;
        final IntString typedOther = other;
        lastComparison = Boolean.valueOf(this.isSetMyint()).compareTo(Boolean.valueOf(typedOther.isSetMyint()));
        if (lastComparison != 0) {
            return lastComparison;
        }
        if (this.isSetMyint()) {
            lastComparison = TBaseHelper.compareTo(this.myint, typedOther.myint);
            if (lastComparison != 0) {
                return lastComparison;
            }
        }
        lastComparison = Boolean.valueOf(this.isSetMyString()).compareTo(Boolean.valueOf(typedOther.isSetMyString()));
        if (lastComparison != 0) {
            return lastComparison;
        }
        if (this.isSetMyString()) {
            lastComparison = TBaseHelper.compareTo(this.myString, typedOther.myString);
            if (lastComparison != 0) {
                return lastComparison;
            }
        }
        lastComparison = Boolean.valueOf(this.isSetUnderscore_int()).compareTo(Boolean.valueOf(typedOther.isSetUnderscore_int()));
        if (lastComparison != 0) {
            return lastComparison;
        }
        if (this.isSetUnderscore_int()) {
            lastComparison = TBaseHelper.compareTo(this.underscore_int, typedOther.underscore_int);
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
        IntString.schemes.get(iprot.getScheme()).getScheme().read(iprot, this);
    }
    
    @Override
    public void write(final TProtocol oprot) throws TException {
        IntString.schemes.get(oprot.getScheme()).getScheme().write(oprot, this);
    }
    
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("IntString(");
        boolean first = true;
        sb.append("myint:");
        sb.append(this.myint);
        first = false;
        if (!first) {
            sb.append(", ");
        }
        sb.append("myString:");
        if (this.myString == null) {
            sb.append("null");
        }
        else {
            sb.append(this.myString);
        }
        first = false;
        if (!first) {
            sb.append(", ");
        }
        sb.append("underscore_int:");
        sb.append(this.underscore_int);
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
            this.__isset_bitfield = 0;
            this.read(new TCompactProtocol(new TIOStreamTransport(in)));
        }
        catch (TException te) {
            throw new IOException(te);
        }
    }
    
    static {
        STRUCT_DESC = new TStruct("IntString");
        MYINT_FIELD_DESC = new TField("myint", (byte)8, (short)1);
        MY_STRING_FIELD_DESC = new TField("myString", (byte)11, (short)2);
        UNDERSCORE_INT_FIELD_DESC = new TField("underscore_int", (byte)8, (short)3);
        (schemes = new HashMap<Class<? extends IScheme>, SchemeFactory>()).put(StandardScheme.class, new IntStringStandardSchemeFactory());
        IntString.schemes.put(TupleScheme.class, new IntStringTupleSchemeFactory());
        final Map<_Fields, FieldMetaData> tmpMap = new EnumMap<_Fields, FieldMetaData>(_Fields.class);
        tmpMap.put(_Fields.MYINT, new FieldMetaData("myint", (byte)3, new FieldValueMetaData((byte)8)));
        tmpMap.put(_Fields.MY_STRING, new FieldMetaData("myString", (byte)3, new FieldValueMetaData((byte)11)));
        tmpMap.put(_Fields.UNDERSCORE_INT, new FieldMetaData("underscore_int", (byte)3, new FieldValueMetaData((byte)8)));
        FieldMetaData.addStructMetaDataMap(IntString.class, metaDataMap = Collections.unmodifiableMap((Map<? extends _Fields, ? extends FieldMetaData>)tmpMap));
    }
    
    public enum _Fields implements TFieldIdEnum
    {
        MYINT((short)1, "myint"), 
        MY_STRING((short)2, "myString"), 
        UNDERSCORE_INT((short)3, "underscore_int");
        
        private static final Map<String, _Fields> byName;
        private final short _thriftId;
        private final String _fieldName;
        
        public static _Fields findByThriftId(final int fieldId) {
            switch (fieldId) {
                case 1: {
                    return _Fields.MYINT;
                }
                case 2: {
                    return _Fields.MY_STRING;
                }
                case 3: {
                    return _Fields.UNDERSCORE_INT;
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
    
    private static class IntStringStandardSchemeFactory implements SchemeFactory
    {
        @Override
        public IntStringStandardScheme getScheme() {
            return new IntStringStandardScheme();
        }
    }
    
    private static class IntStringStandardScheme extends StandardScheme<IntString>
    {
        @Override
        public void read(final TProtocol iprot, final IntString struct) throws TException {
            iprot.readStructBegin();
            while (true) {
                final TField schemeField = iprot.readFieldBegin();
                if (schemeField.type == 0) {
                    break;
                }
                switch (schemeField.id) {
                    case 1: {
                        if (schemeField.type == 8) {
                            struct.myint = iprot.readI32();
                            struct.setMyintIsSet(true);
                            break;
                        }
                        TProtocolUtil.skip(iprot, schemeField.type);
                        break;
                    }
                    case 2: {
                        if (schemeField.type == 11) {
                            struct.myString = iprot.readString();
                            struct.setMyStringIsSet(true);
                            break;
                        }
                        TProtocolUtil.skip(iprot, schemeField.type);
                        break;
                    }
                    case 3: {
                        if (schemeField.type == 8) {
                            struct.underscore_int = iprot.readI32();
                            struct.setUnderscore_intIsSet(true);
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
        public void write(final TProtocol oprot, final IntString struct) throws TException {
            struct.validate();
            oprot.writeStructBegin(IntString.STRUCT_DESC);
            oprot.writeFieldBegin(IntString.MYINT_FIELD_DESC);
            oprot.writeI32(struct.myint);
            oprot.writeFieldEnd();
            if (struct.myString != null) {
                oprot.writeFieldBegin(IntString.MY_STRING_FIELD_DESC);
                oprot.writeString(struct.myString);
                oprot.writeFieldEnd();
            }
            oprot.writeFieldBegin(IntString.UNDERSCORE_INT_FIELD_DESC);
            oprot.writeI32(struct.underscore_int);
            oprot.writeFieldEnd();
            oprot.writeFieldStop();
            oprot.writeStructEnd();
        }
    }
    
    private static class IntStringTupleSchemeFactory implements SchemeFactory
    {
        @Override
        public IntStringTupleScheme getScheme() {
            return new IntStringTupleScheme();
        }
    }
    
    private static class IntStringTupleScheme extends TupleScheme<IntString>
    {
        @Override
        public void write(final TProtocol prot, final IntString struct) throws TException {
            final TTupleProtocol oprot = (TTupleProtocol)prot;
            final BitSet optionals = new BitSet();
            if (struct.isSetMyint()) {
                optionals.set(0);
            }
            if (struct.isSetMyString()) {
                optionals.set(1);
            }
            if (struct.isSetUnderscore_int()) {
                optionals.set(2);
            }
            oprot.writeBitSet(optionals, 3);
            if (struct.isSetMyint()) {
                oprot.writeI32(struct.myint);
            }
            if (struct.isSetMyString()) {
                oprot.writeString(struct.myString);
            }
            if (struct.isSetUnderscore_int()) {
                oprot.writeI32(struct.underscore_int);
            }
        }
        
        @Override
        public void read(final TProtocol prot, final IntString struct) throws TException {
            final TTupleProtocol iprot = (TTupleProtocol)prot;
            final BitSet incoming = iprot.readBitSet(3);
            if (incoming.get(0)) {
                struct.myint = iprot.readI32();
                struct.setMyintIsSet(true);
            }
            if (incoming.get(1)) {
                struct.myString = iprot.readString();
                struct.setMyStringIsSet(true);
            }
            if (incoming.get(2)) {
                struct.underscore_int = iprot.readI32();
                struct.setUnderscore_intIsSet(true);
            }
        }
    }
}
