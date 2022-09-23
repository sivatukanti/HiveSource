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
import org.apache.thrift.TEnum;
import org.apache.thrift.meta_data.EnumMetaData;
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
import org.apache.thrift.meta_data.FieldMetaData;
import org.apache.thrift.scheme.SchemeFactory;
import org.apache.thrift.scheme.IScheme;
import java.util.Map;
import org.apache.thrift.protocol.TField;
import org.apache.thrift.protocol.TStruct;
import java.io.Serializable;
import org.apache.thrift.TBase;

public class MiniStruct implements TBase<MiniStruct, _Fields>, Serializable, Cloneable
{
    private static final TStruct STRUCT_DESC;
    private static final TField MY_STRING_FIELD_DESC;
    private static final TField MY_ENUM_FIELD_DESC;
    private static final Map<Class<? extends IScheme>, SchemeFactory> schemes;
    private String my_string;
    private MyEnum my_enum;
    private _Fields[] optionals;
    public static final Map<_Fields, FieldMetaData> metaDataMap;
    
    public MiniStruct() {
        this.optionals = new _Fields[] { _Fields.MY_STRING, _Fields.MY_ENUM };
    }
    
    public MiniStruct(final MiniStruct other) {
        this.optionals = new _Fields[] { _Fields.MY_STRING, _Fields.MY_ENUM };
        if (other.isSetMy_string()) {
            this.my_string = other.my_string;
        }
        if (other.isSetMy_enum()) {
            this.my_enum = other.my_enum;
        }
    }
    
    @Override
    public MiniStruct deepCopy() {
        return new MiniStruct(this);
    }
    
    @Override
    public void clear() {
        this.my_string = null;
        this.my_enum = null;
    }
    
    public String getMy_string() {
        return this.my_string;
    }
    
    public void setMy_string(final String my_string) {
        this.my_string = my_string;
    }
    
    public void unsetMy_string() {
        this.my_string = null;
    }
    
    public boolean isSetMy_string() {
        return this.my_string != null;
    }
    
    public void setMy_stringIsSet(final boolean value) {
        if (!value) {
            this.my_string = null;
        }
    }
    
    public MyEnum getMy_enum() {
        return this.my_enum;
    }
    
    public void setMy_enum(final MyEnum my_enum) {
        this.my_enum = my_enum;
    }
    
    public void unsetMy_enum() {
        this.my_enum = null;
    }
    
    public boolean isSetMy_enum() {
        return this.my_enum != null;
    }
    
    public void setMy_enumIsSet(final boolean value) {
        if (!value) {
            this.my_enum = null;
        }
    }
    
    @Override
    public void setFieldValue(final _Fields field, final Object value) {
        switch (field) {
            case MY_STRING: {
                if (value == null) {
                    this.unsetMy_string();
                    break;
                }
                this.setMy_string((String)value);
                break;
            }
            case MY_ENUM: {
                if (value == null) {
                    this.unsetMy_enum();
                    break;
                }
                this.setMy_enum((MyEnum)value);
                break;
            }
        }
    }
    
    @Override
    public Object getFieldValue(final _Fields field) {
        switch (field) {
            case MY_STRING: {
                return this.getMy_string();
            }
            case MY_ENUM: {
                return this.getMy_enum();
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
            case MY_STRING: {
                return this.isSetMy_string();
            }
            case MY_ENUM: {
                return this.isSetMy_enum();
            }
            default: {
                throw new IllegalStateException();
            }
        }
    }
    
    @Override
    public boolean equals(final Object that) {
        return that != null && that instanceof MiniStruct && this.equals((MiniStruct)that);
    }
    
    public boolean equals(final MiniStruct that) {
        if (that == null) {
            return false;
        }
        final boolean this_present_my_string = this.isSetMy_string();
        final boolean that_present_my_string = that.isSetMy_string();
        if (this_present_my_string || that_present_my_string) {
            if (!this_present_my_string || !that_present_my_string) {
                return false;
            }
            if (!this.my_string.equals(that.my_string)) {
                return false;
            }
        }
        final boolean this_present_my_enum = this.isSetMy_enum();
        final boolean that_present_my_enum = that.isSetMy_enum();
        if (this_present_my_enum || that_present_my_enum) {
            if (!this_present_my_enum || !that_present_my_enum) {
                return false;
            }
            if (!this.my_enum.equals(that.my_enum)) {
                return false;
            }
        }
        return true;
    }
    
    @Override
    public int hashCode() {
        final HashCodeBuilder builder = new HashCodeBuilder();
        final boolean present_my_string = this.isSetMy_string();
        builder.append(present_my_string);
        if (present_my_string) {
            builder.append(this.my_string);
        }
        final boolean present_my_enum = this.isSetMy_enum();
        builder.append(present_my_enum);
        if (present_my_enum) {
            builder.append(this.my_enum.getValue());
        }
        return builder.toHashCode();
    }
    
    @Override
    public int compareTo(final MiniStruct other) {
        if (!this.getClass().equals(other.getClass())) {
            return this.getClass().getName().compareTo(other.getClass().getName());
        }
        int lastComparison = 0;
        final MiniStruct typedOther = other;
        lastComparison = Boolean.valueOf(this.isSetMy_string()).compareTo(Boolean.valueOf(typedOther.isSetMy_string()));
        if (lastComparison != 0) {
            return lastComparison;
        }
        if (this.isSetMy_string()) {
            lastComparison = TBaseHelper.compareTo(this.my_string, typedOther.my_string);
            if (lastComparison != 0) {
                return lastComparison;
            }
        }
        lastComparison = Boolean.valueOf(this.isSetMy_enum()).compareTo(Boolean.valueOf(typedOther.isSetMy_enum()));
        if (lastComparison != 0) {
            return lastComparison;
        }
        if (this.isSetMy_enum()) {
            lastComparison = TBaseHelper.compareTo(this.my_enum, typedOther.my_enum);
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
        MiniStruct.schemes.get(iprot.getScheme()).getScheme().read(iprot, this);
    }
    
    @Override
    public void write(final TProtocol oprot) throws TException {
        MiniStruct.schemes.get(oprot.getScheme()).getScheme().write(oprot, this);
    }
    
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("MiniStruct(");
        boolean first = true;
        if (this.isSetMy_string()) {
            sb.append("my_string:");
            if (this.my_string == null) {
                sb.append("null");
            }
            else {
                sb.append(this.my_string);
            }
            first = false;
        }
        if (this.isSetMy_enum()) {
            if (!first) {
                sb.append(", ");
            }
            sb.append("my_enum:");
            if (this.my_enum == null) {
                sb.append("null");
            }
            else {
                sb.append(this.my_enum);
            }
            first = false;
        }
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
        STRUCT_DESC = new TStruct("MiniStruct");
        MY_STRING_FIELD_DESC = new TField("my_string", (byte)11, (short)1);
        MY_ENUM_FIELD_DESC = new TField("my_enum", (byte)8, (short)2);
        (schemes = new HashMap<Class<? extends IScheme>, SchemeFactory>()).put(StandardScheme.class, new MiniStructStandardSchemeFactory());
        MiniStruct.schemes.put(TupleScheme.class, new MiniStructTupleSchemeFactory());
        final Map<_Fields, FieldMetaData> tmpMap = new EnumMap<_Fields, FieldMetaData>(_Fields.class);
        tmpMap.put(_Fields.MY_STRING, new FieldMetaData("my_string", (byte)2, new FieldValueMetaData((byte)11)));
        tmpMap.put(_Fields.MY_ENUM, new FieldMetaData("my_enum", (byte)2, new EnumMetaData((byte)16, MyEnum.class)));
        FieldMetaData.addStructMetaDataMap(MiniStruct.class, metaDataMap = Collections.unmodifiableMap((Map<? extends _Fields, ? extends FieldMetaData>)tmpMap));
    }
    
    public enum _Fields implements TFieldIdEnum
    {
        MY_STRING((short)1, "my_string"), 
        MY_ENUM((short)2, "my_enum");
        
        private static final Map<String, _Fields> byName;
        private final short _thriftId;
        private final String _fieldName;
        
        public static _Fields findByThriftId(final int fieldId) {
            switch (fieldId) {
                case 1: {
                    return _Fields.MY_STRING;
                }
                case 2: {
                    return _Fields.MY_ENUM;
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
    
    private static class MiniStructStandardSchemeFactory implements SchemeFactory
    {
        @Override
        public MiniStructStandardScheme getScheme() {
            return new MiniStructStandardScheme();
        }
    }
    
    private static class MiniStructStandardScheme extends StandardScheme<MiniStruct>
    {
        @Override
        public void read(final TProtocol iprot, final MiniStruct struct) throws TException {
            iprot.readStructBegin();
            while (true) {
                final TField schemeField = iprot.readFieldBegin();
                if (schemeField.type == 0) {
                    break;
                }
                switch (schemeField.id) {
                    case 1: {
                        if (schemeField.type == 11) {
                            struct.my_string = iprot.readString();
                            struct.setMy_stringIsSet(true);
                            break;
                        }
                        TProtocolUtil.skip(iprot, schemeField.type);
                        break;
                    }
                    case 2: {
                        if (schemeField.type == 8) {
                            struct.my_enum = MyEnum.findByValue(iprot.readI32());
                            struct.setMy_enumIsSet(true);
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
        public void write(final TProtocol oprot, final MiniStruct struct) throws TException {
            struct.validate();
            oprot.writeStructBegin(MiniStruct.STRUCT_DESC);
            if (struct.my_string != null && struct.isSetMy_string()) {
                oprot.writeFieldBegin(MiniStruct.MY_STRING_FIELD_DESC);
                oprot.writeString(struct.my_string);
                oprot.writeFieldEnd();
            }
            if (struct.my_enum != null && struct.isSetMy_enum()) {
                oprot.writeFieldBegin(MiniStruct.MY_ENUM_FIELD_DESC);
                oprot.writeI32(struct.my_enum.getValue());
                oprot.writeFieldEnd();
            }
            oprot.writeFieldStop();
            oprot.writeStructEnd();
        }
    }
    
    private static class MiniStructTupleSchemeFactory implements SchemeFactory
    {
        @Override
        public MiniStructTupleScheme getScheme() {
            return new MiniStructTupleScheme();
        }
    }
    
    private static class MiniStructTupleScheme extends TupleScheme<MiniStruct>
    {
        @Override
        public void write(final TProtocol prot, final MiniStruct struct) throws TException {
            final TTupleProtocol oprot = (TTupleProtocol)prot;
            final BitSet optionals = new BitSet();
            if (struct.isSetMy_string()) {
                optionals.set(0);
            }
            if (struct.isSetMy_enum()) {
                optionals.set(1);
            }
            oprot.writeBitSet(optionals, 2);
            if (struct.isSetMy_string()) {
                oprot.writeString(struct.my_string);
            }
            if (struct.isSetMy_enum()) {
                oprot.writeI32(struct.my_enum.getValue());
            }
        }
        
        @Override
        public void read(final TProtocol prot, final MiniStruct struct) throws TException {
            final TTupleProtocol iprot = (TTupleProtocol)prot;
            final BitSet incoming = iprot.readBitSet(2);
            if (incoming.get(0)) {
                struct.my_string = iprot.readString();
                struct.setMy_stringIsSet(true);
            }
            if (incoming.get(1)) {
                struct.my_enum = MyEnum.findByValue(iprot.readI32());
                struct.setMy_enumIsSet(true);
            }
        }
    }
}
