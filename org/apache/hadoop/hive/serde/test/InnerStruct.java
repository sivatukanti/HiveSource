// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.hive.serde.test;

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

public class InnerStruct implements TBase<InnerStruct, _Fields>, Serializable, Cloneable
{
    private static final TStruct STRUCT_DESC;
    private static final TField FIELD0_FIELD_DESC;
    private static final Map<Class<? extends IScheme>, SchemeFactory> schemes;
    private int field0;
    private static final int __FIELD0_ISSET_ID = 0;
    private byte __isset_bitfield;
    public static final Map<_Fields, FieldMetaData> metaDataMap;
    
    public InnerStruct() {
        this.__isset_bitfield = 0;
    }
    
    public InnerStruct(final int field0) {
        this();
        this.field0 = field0;
        this.setField0IsSet(true);
    }
    
    public InnerStruct(final InnerStruct other) {
        this.__isset_bitfield = 0;
        this.__isset_bitfield = other.__isset_bitfield;
        this.field0 = other.field0;
    }
    
    @Override
    public InnerStruct deepCopy() {
        return new InnerStruct(this);
    }
    
    @Override
    public void clear() {
        this.setField0IsSet(false);
        this.field0 = 0;
    }
    
    public int getField0() {
        return this.field0;
    }
    
    public void setField0(final int field0) {
        this.field0 = field0;
        this.setField0IsSet(true);
    }
    
    public void unsetField0() {
        this.__isset_bitfield = EncodingUtils.clearBit(this.__isset_bitfield, 0);
    }
    
    public boolean isSetField0() {
        return EncodingUtils.testBit(this.__isset_bitfield, 0);
    }
    
    public void setField0IsSet(final boolean value) {
        this.__isset_bitfield = EncodingUtils.setBit(this.__isset_bitfield, 0, value);
    }
    
    @Override
    public void setFieldValue(final _Fields field, final Object value) {
        switch (field) {
            case FIELD0: {
                if (value == null) {
                    this.unsetField0();
                    break;
                }
                this.setField0((int)value);
                break;
            }
        }
    }
    
    @Override
    public Object getFieldValue(final _Fields field) {
        switch (field) {
            case FIELD0: {
                return this.getField0();
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
            case FIELD0: {
                return this.isSetField0();
            }
            default: {
                throw new IllegalStateException();
            }
        }
    }
    
    @Override
    public boolean equals(final Object that) {
        return that != null && that instanceof InnerStruct && this.equals((InnerStruct)that);
    }
    
    public boolean equals(final InnerStruct that) {
        if (that == null) {
            return false;
        }
        final boolean this_present_field0 = true;
        final boolean that_present_field0 = true;
        if (this_present_field0 || that_present_field0) {
            if (!this_present_field0 || !that_present_field0) {
                return false;
            }
            if (this.field0 != that.field0) {
                return false;
            }
        }
        return true;
    }
    
    @Override
    public int hashCode() {
        final HashCodeBuilder builder = new HashCodeBuilder();
        final boolean present_field0 = true;
        builder.append(present_field0);
        if (present_field0) {
            builder.append(this.field0);
        }
        return builder.toHashCode();
    }
    
    @Override
    public int compareTo(final InnerStruct other) {
        if (!this.getClass().equals(other.getClass())) {
            return this.getClass().getName().compareTo(other.getClass().getName());
        }
        int lastComparison = 0;
        final InnerStruct typedOther = other;
        lastComparison = Boolean.valueOf(this.isSetField0()).compareTo(Boolean.valueOf(typedOther.isSetField0()));
        if (lastComparison != 0) {
            return lastComparison;
        }
        if (this.isSetField0()) {
            lastComparison = TBaseHelper.compareTo(this.field0, typedOther.field0);
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
        InnerStruct.schemes.get(iprot.getScheme()).getScheme().read(iprot, this);
    }
    
    @Override
    public void write(final TProtocol oprot) throws TException {
        InnerStruct.schemes.get(oprot.getScheme()).getScheme().write(oprot, this);
    }
    
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("InnerStruct(");
        boolean first = true;
        sb.append("field0:");
        sb.append(this.field0);
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
        STRUCT_DESC = new TStruct("InnerStruct");
        FIELD0_FIELD_DESC = new TField("field0", (byte)8, (short)1);
        (schemes = new HashMap<Class<? extends IScheme>, SchemeFactory>()).put(StandardScheme.class, new InnerStructStandardSchemeFactory());
        InnerStruct.schemes.put(TupleScheme.class, new InnerStructTupleSchemeFactory());
        final Map<_Fields, FieldMetaData> tmpMap = new EnumMap<_Fields, FieldMetaData>(_Fields.class);
        tmpMap.put(_Fields.FIELD0, new FieldMetaData("field0", (byte)3, new FieldValueMetaData((byte)8)));
        FieldMetaData.addStructMetaDataMap(InnerStruct.class, metaDataMap = Collections.unmodifiableMap((Map<? extends _Fields, ? extends FieldMetaData>)tmpMap));
    }
    
    public enum _Fields implements TFieldIdEnum
    {
        FIELD0((short)1, "field0");
        
        private static final Map<String, _Fields> byName;
        private final short _thriftId;
        private final String _fieldName;
        
        public static _Fields findByThriftId(final int fieldId) {
            switch (fieldId) {
                case 1: {
                    return _Fields.FIELD0;
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
    
    private static class InnerStructStandardSchemeFactory implements SchemeFactory
    {
        @Override
        public InnerStructStandardScheme getScheme() {
            return new InnerStructStandardScheme();
        }
    }
    
    private static class InnerStructStandardScheme extends StandardScheme<InnerStruct>
    {
        @Override
        public void read(final TProtocol iprot, final InnerStruct struct) throws TException {
            iprot.readStructBegin();
            while (true) {
                final TField schemeField = iprot.readFieldBegin();
                if (schemeField.type == 0) {
                    break;
                }
                switch (schemeField.id) {
                    case 1: {
                        if (schemeField.type == 8) {
                            struct.field0 = iprot.readI32();
                            struct.setField0IsSet(true);
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
        public void write(final TProtocol oprot, final InnerStruct struct) throws TException {
            struct.validate();
            oprot.writeStructBegin(InnerStruct.STRUCT_DESC);
            oprot.writeFieldBegin(InnerStruct.FIELD0_FIELD_DESC);
            oprot.writeI32(struct.field0);
            oprot.writeFieldEnd();
            oprot.writeFieldStop();
            oprot.writeStructEnd();
        }
    }
    
    private static class InnerStructTupleSchemeFactory implements SchemeFactory
    {
        @Override
        public InnerStructTupleScheme getScheme() {
            return new InnerStructTupleScheme();
        }
    }
    
    private static class InnerStructTupleScheme extends TupleScheme<InnerStruct>
    {
        @Override
        public void write(final TProtocol prot, final InnerStruct struct) throws TException {
            final TTupleProtocol oprot = (TTupleProtocol)prot;
            final BitSet optionals = new BitSet();
            if (struct.isSetField0()) {
                optionals.set(0);
            }
            oprot.writeBitSet(optionals, 1);
            if (struct.isSetField0()) {
                oprot.writeI32(struct.field0);
            }
        }
        
        @Override
        public void read(final TProtocol prot, final InnerStruct struct) throws TException {
            final TTupleProtocol iprot = (TTupleProtocol)prot;
            final BitSet incoming = iprot.readBitSet(1);
            if (incoming.get(0)) {
                struct.field0 = iprot.readI32();
                struct.setField0IsSet(true);
            }
        }
    }
}
