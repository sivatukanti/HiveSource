// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.hive.metastore.api;

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
import org.apache.thrift.protocol.TProtocolException;
import org.apache.thrift.TException;
import org.apache.thrift.protocol.TProtocol;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.thrift.EncodingUtils;
import org.apache.thrift.TBaseHelper;
import org.apache.thrift.meta_data.FieldMetaData;
import java.nio.ByteBuffer;
import org.apache.thrift.scheme.SchemeFactory;
import org.apache.thrift.scheme.IScheme;
import java.util.Map;
import org.apache.thrift.protocol.TField;
import org.apache.thrift.protocol.TStruct;
import java.io.Serializable;
import org.apache.thrift.TBase;

public class Decimal implements TBase<Decimal, _Fields>, Serializable, Cloneable
{
    private static final TStruct STRUCT_DESC;
    private static final TField UNSCALED_FIELD_DESC;
    private static final TField SCALE_FIELD_DESC;
    private static final Map<Class<? extends IScheme>, SchemeFactory> schemes;
    private ByteBuffer unscaled;
    private short scale;
    private static final int __SCALE_ISSET_ID = 0;
    private byte __isset_bitfield;
    public static final Map<_Fields, FieldMetaData> metaDataMap;
    
    public Decimal() {
        this.__isset_bitfield = 0;
    }
    
    public Decimal(final ByteBuffer unscaled, final short scale) {
        this();
        this.unscaled = unscaled;
        this.scale = scale;
        this.setScaleIsSet(true);
    }
    
    public Decimal(final Decimal other) {
        this.__isset_bitfield = 0;
        this.__isset_bitfield = other.__isset_bitfield;
        if (other.isSetUnscaled()) {
            this.unscaled = TBaseHelper.copyBinary(other.unscaled);
        }
        this.scale = other.scale;
    }
    
    @Override
    public Decimal deepCopy() {
        return new Decimal(this);
    }
    
    @Override
    public void clear() {
        this.unscaled = null;
        this.setScaleIsSet(false);
        this.scale = 0;
    }
    
    public byte[] getUnscaled() {
        this.setUnscaled(TBaseHelper.rightSize(this.unscaled));
        return (byte[])((this.unscaled == null) ? null : this.unscaled.array());
    }
    
    public ByteBuffer bufferForUnscaled() {
        return this.unscaled;
    }
    
    public void setUnscaled(final byte[] unscaled) {
        this.setUnscaled((unscaled == null) ? ((ByteBuffer)null) : ByteBuffer.wrap(unscaled));
    }
    
    public void setUnscaled(final ByteBuffer unscaled) {
        this.unscaled = unscaled;
    }
    
    public void unsetUnscaled() {
        this.unscaled = null;
    }
    
    public boolean isSetUnscaled() {
        return this.unscaled != null;
    }
    
    public void setUnscaledIsSet(final boolean value) {
        if (!value) {
            this.unscaled = null;
        }
    }
    
    public short getScale() {
        return this.scale;
    }
    
    public void setScale(final short scale) {
        this.scale = scale;
        this.setScaleIsSet(true);
    }
    
    public void unsetScale() {
        this.__isset_bitfield = EncodingUtils.clearBit(this.__isset_bitfield, 0);
    }
    
    public boolean isSetScale() {
        return EncodingUtils.testBit(this.__isset_bitfield, 0);
    }
    
    public void setScaleIsSet(final boolean value) {
        this.__isset_bitfield = EncodingUtils.setBit(this.__isset_bitfield, 0, value);
    }
    
    @Override
    public void setFieldValue(final _Fields field, final Object value) {
        switch (field) {
            case UNSCALED: {
                if (value == null) {
                    this.unsetUnscaled();
                    break;
                }
                this.setUnscaled((ByteBuffer)value);
                break;
            }
            case SCALE: {
                if (value == null) {
                    this.unsetScale();
                    break;
                }
                this.setScale((short)value);
                break;
            }
        }
    }
    
    @Override
    public Object getFieldValue(final _Fields field) {
        switch (field) {
            case UNSCALED: {
                return this.getUnscaled();
            }
            case SCALE: {
                return this.getScale();
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
            case UNSCALED: {
                return this.isSetUnscaled();
            }
            case SCALE: {
                return this.isSetScale();
            }
            default: {
                throw new IllegalStateException();
            }
        }
    }
    
    @Override
    public boolean equals(final Object that) {
        return that != null && that instanceof Decimal && this.equals((Decimal)that);
    }
    
    public boolean equals(final Decimal that) {
        if (that == null) {
            return false;
        }
        final boolean this_present_unscaled = this.isSetUnscaled();
        final boolean that_present_unscaled = that.isSetUnscaled();
        if (this_present_unscaled || that_present_unscaled) {
            if (!this_present_unscaled || !that_present_unscaled) {
                return false;
            }
            if (!this.unscaled.equals(that.unscaled)) {
                return false;
            }
        }
        final boolean this_present_scale = true;
        final boolean that_present_scale = true;
        if (this_present_scale || that_present_scale) {
            if (!this_present_scale || !that_present_scale) {
                return false;
            }
            if (this.scale != that.scale) {
                return false;
            }
        }
        return true;
    }
    
    @Override
    public int hashCode() {
        final HashCodeBuilder builder = new HashCodeBuilder();
        final boolean present_unscaled = this.isSetUnscaled();
        builder.append(present_unscaled);
        if (present_unscaled) {
            builder.append(this.unscaled);
        }
        final boolean present_scale = true;
        builder.append(present_scale);
        if (present_scale) {
            builder.append(this.scale);
        }
        return builder.toHashCode();
    }
    
    @Override
    public int compareTo(final Decimal other) {
        if (!this.getClass().equals(other.getClass())) {
            return this.getClass().getName().compareTo(other.getClass().getName());
        }
        int lastComparison = 0;
        final Decimal typedOther = other;
        lastComparison = Boolean.valueOf(this.isSetUnscaled()).compareTo(Boolean.valueOf(typedOther.isSetUnscaled()));
        if (lastComparison != 0) {
            return lastComparison;
        }
        if (this.isSetUnscaled()) {
            lastComparison = TBaseHelper.compareTo(this.unscaled, typedOther.unscaled);
            if (lastComparison != 0) {
                return lastComparison;
            }
        }
        lastComparison = Boolean.valueOf(this.isSetScale()).compareTo(Boolean.valueOf(typedOther.isSetScale()));
        if (lastComparison != 0) {
            return lastComparison;
        }
        if (this.isSetScale()) {
            lastComparison = TBaseHelper.compareTo(this.scale, typedOther.scale);
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
        Decimal.schemes.get(iprot.getScheme()).getScheme().read(iprot, this);
    }
    
    @Override
    public void write(final TProtocol oprot) throws TException {
        Decimal.schemes.get(oprot.getScheme()).getScheme().write(oprot, this);
    }
    
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("Decimal(");
        boolean first = true;
        sb.append("unscaled:");
        if (this.unscaled == null) {
            sb.append("null");
        }
        else {
            TBaseHelper.toString(this.unscaled, sb);
        }
        first = false;
        if (!first) {
            sb.append(", ");
        }
        sb.append("scale:");
        sb.append(this.scale);
        first = false;
        sb.append(")");
        return sb.toString();
    }
    
    public void validate() throws TException {
        if (!this.isSetUnscaled()) {
            throw new TProtocolException("Required field 'unscaled' is unset! Struct:" + this.toString());
        }
        if (!this.isSetScale()) {
            throw new TProtocolException("Required field 'scale' is unset! Struct:" + this.toString());
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
            this.__isset_bitfield = 0;
            this.read(new TCompactProtocol(new TIOStreamTransport(in)));
        }
        catch (TException te) {
            throw new IOException(te);
        }
    }
    
    static {
        STRUCT_DESC = new TStruct("Decimal");
        UNSCALED_FIELD_DESC = new TField("unscaled", (byte)11, (short)1);
        SCALE_FIELD_DESC = new TField("scale", (byte)6, (short)3);
        (schemes = new HashMap<Class<? extends IScheme>, SchemeFactory>()).put(StandardScheme.class, new DecimalStandardSchemeFactory());
        Decimal.schemes.put(TupleScheme.class, new DecimalTupleSchemeFactory());
        final Map<_Fields, FieldMetaData> tmpMap = new EnumMap<_Fields, FieldMetaData>(_Fields.class);
        tmpMap.put(_Fields.UNSCALED, new FieldMetaData("unscaled", (byte)1, new FieldValueMetaData((byte)11, true)));
        tmpMap.put(_Fields.SCALE, new FieldMetaData("scale", (byte)1, new FieldValueMetaData((byte)6)));
        FieldMetaData.addStructMetaDataMap(Decimal.class, metaDataMap = Collections.unmodifiableMap((Map<? extends _Fields, ? extends FieldMetaData>)tmpMap));
    }
    
    public enum _Fields implements TFieldIdEnum
    {
        UNSCALED((short)1, "unscaled"), 
        SCALE((short)3, "scale");
        
        private static final Map<String, _Fields> byName;
        private final short _thriftId;
        private final String _fieldName;
        
        public static _Fields findByThriftId(final int fieldId) {
            switch (fieldId) {
                case 1: {
                    return _Fields.UNSCALED;
                }
                case 3: {
                    return _Fields.SCALE;
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
    
    private static class DecimalStandardSchemeFactory implements SchemeFactory
    {
        @Override
        public DecimalStandardScheme getScheme() {
            return new DecimalStandardScheme();
        }
    }
    
    private static class DecimalStandardScheme extends StandardScheme<Decimal>
    {
        @Override
        public void read(final TProtocol iprot, final Decimal struct) throws TException {
            iprot.readStructBegin();
            while (true) {
                final TField schemeField = iprot.readFieldBegin();
                if (schemeField.type == 0) {
                    break;
                }
                switch (schemeField.id) {
                    case 1: {
                        if (schemeField.type == 11) {
                            struct.unscaled = iprot.readBinary();
                            struct.setUnscaledIsSet(true);
                            break;
                        }
                        TProtocolUtil.skip(iprot, schemeField.type);
                        break;
                    }
                    case 3: {
                        if (schemeField.type == 6) {
                            struct.scale = iprot.readI16();
                            struct.setScaleIsSet(true);
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
        public void write(final TProtocol oprot, final Decimal struct) throws TException {
            struct.validate();
            oprot.writeStructBegin(Decimal.STRUCT_DESC);
            if (struct.unscaled != null) {
                oprot.writeFieldBegin(Decimal.UNSCALED_FIELD_DESC);
                oprot.writeBinary(struct.unscaled);
                oprot.writeFieldEnd();
            }
            oprot.writeFieldBegin(Decimal.SCALE_FIELD_DESC);
            oprot.writeI16(struct.scale);
            oprot.writeFieldEnd();
            oprot.writeFieldStop();
            oprot.writeStructEnd();
        }
    }
    
    private static class DecimalTupleSchemeFactory implements SchemeFactory
    {
        @Override
        public DecimalTupleScheme getScheme() {
            return new DecimalTupleScheme();
        }
    }
    
    private static class DecimalTupleScheme extends TupleScheme<Decimal>
    {
        @Override
        public void write(final TProtocol prot, final Decimal struct) throws TException {
            final TTupleProtocol oprot = (TTupleProtocol)prot;
            oprot.writeBinary(struct.unscaled);
            oprot.writeI16(struct.scale);
        }
        
        @Override
        public void read(final TProtocol prot, final Decimal struct) throws TException {
            final TTupleProtocol iprot = (TTupleProtocol)prot;
            struct.unscaled = iprot.readBinary();
            struct.setUnscaledIsSet(true);
            struct.scale = iprot.readI16();
            struct.setScaleIsSet(true);
        }
    }
}
