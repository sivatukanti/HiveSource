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

public class HeartbeatTxnRangeRequest implements TBase<HeartbeatTxnRangeRequest, _Fields>, Serializable, Cloneable
{
    private static final TStruct STRUCT_DESC;
    private static final TField MIN_FIELD_DESC;
    private static final TField MAX_FIELD_DESC;
    private static final Map<Class<? extends IScheme>, SchemeFactory> schemes;
    private long min;
    private long max;
    private static final int __MIN_ISSET_ID = 0;
    private static final int __MAX_ISSET_ID = 1;
    private byte __isset_bitfield;
    public static final Map<_Fields, FieldMetaData> metaDataMap;
    
    public HeartbeatTxnRangeRequest() {
        this.__isset_bitfield = 0;
    }
    
    public HeartbeatTxnRangeRequest(final long min, final long max) {
        this();
        this.min = min;
        this.setMinIsSet(true);
        this.max = max;
        this.setMaxIsSet(true);
    }
    
    public HeartbeatTxnRangeRequest(final HeartbeatTxnRangeRequest other) {
        this.__isset_bitfield = 0;
        this.__isset_bitfield = other.__isset_bitfield;
        this.min = other.min;
        this.max = other.max;
    }
    
    @Override
    public HeartbeatTxnRangeRequest deepCopy() {
        return new HeartbeatTxnRangeRequest(this);
    }
    
    @Override
    public void clear() {
        this.setMinIsSet(false);
        this.min = 0L;
        this.setMaxIsSet(false);
        this.max = 0L;
    }
    
    public long getMin() {
        return this.min;
    }
    
    public void setMin(final long min) {
        this.min = min;
        this.setMinIsSet(true);
    }
    
    public void unsetMin() {
        this.__isset_bitfield = EncodingUtils.clearBit(this.__isset_bitfield, 0);
    }
    
    public boolean isSetMin() {
        return EncodingUtils.testBit(this.__isset_bitfield, 0);
    }
    
    public void setMinIsSet(final boolean value) {
        this.__isset_bitfield = EncodingUtils.setBit(this.__isset_bitfield, 0, value);
    }
    
    public long getMax() {
        return this.max;
    }
    
    public void setMax(final long max) {
        this.max = max;
        this.setMaxIsSet(true);
    }
    
    public void unsetMax() {
        this.__isset_bitfield = EncodingUtils.clearBit(this.__isset_bitfield, 1);
    }
    
    public boolean isSetMax() {
        return EncodingUtils.testBit(this.__isset_bitfield, 1);
    }
    
    public void setMaxIsSet(final boolean value) {
        this.__isset_bitfield = EncodingUtils.setBit(this.__isset_bitfield, 1, value);
    }
    
    @Override
    public void setFieldValue(final _Fields field, final Object value) {
        switch (field) {
            case MIN: {
                if (value == null) {
                    this.unsetMin();
                    break;
                }
                this.setMin((long)value);
                break;
            }
            case MAX: {
                if (value == null) {
                    this.unsetMax();
                    break;
                }
                this.setMax((long)value);
                break;
            }
        }
    }
    
    @Override
    public Object getFieldValue(final _Fields field) {
        switch (field) {
            case MIN: {
                return this.getMin();
            }
            case MAX: {
                return this.getMax();
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
            case MIN: {
                return this.isSetMin();
            }
            case MAX: {
                return this.isSetMax();
            }
            default: {
                throw new IllegalStateException();
            }
        }
    }
    
    @Override
    public boolean equals(final Object that) {
        return that != null && that instanceof HeartbeatTxnRangeRequest && this.equals((HeartbeatTxnRangeRequest)that);
    }
    
    public boolean equals(final HeartbeatTxnRangeRequest that) {
        if (that == null) {
            return false;
        }
        final boolean this_present_min = true;
        final boolean that_present_min = true;
        if (this_present_min || that_present_min) {
            if (!this_present_min || !that_present_min) {
                return false;
            }
            if (this.min != that.min) {
                return false;
            }
        }
        final boolean this_present_max = true;
        final boolean that_present_max = true;
        if (this_present_max || that_present_max) {
            if (!this_present_max || !that_present_max) {
                return false;
            }
            if (this.max != that.max) {
                return false;
            }
        }
        return true;
    }
    
    @Override
    public int hashCode() {
        final HashCodeBuilder builder = new HashCodeBuilder();
        final boolean present_min = true;
        builder.append(present_min);
        if (present_min) {
            builder.append(this.min);
        }
        final boolean present_max = true;
        builder.append(present_max);
        if (present_max) {
            builder.append(this.max);
        }
        return builder.toHashCode();
    }
    
    @Override
    public int compareTo(final HeartbeatTxnRangeRequest other) {
        if (!this.getClass().equals(other.getClass())) {
            return this.getClass().getName().compareTo(other.getClass().getName());
        }
        int lastComparison = 0;
        final HeartbeatTxnRangeRequest typedOther = other;
        lastComparison = Boolean.valueOf(this.isSetMin()).compareTo(Boolean.valueOf(typedOther.isSetMin()));
        if (lastComparison != 0) {
            return lastComparison;
        }
        if (this.isSetMin()) {
            lastComparison = TBaseHelper.compareTo(this.min, typedOther.min);
            if (lastComparison != 0) {
                return lastComparison;
            }
        }
        lastComparison = Boolean.valueOf(this.isSetMax()).compareTo(Boolean.valueOf(typedOther.isSetMax()));
        if (lastComparison != 0) {
            return lastComparison;
        }
        if (this.isSetMax()) {
            lastComparison = TBaseHelper.compareTo(this.max, typedOther.max);
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
        HeartbeatTxnRangeRequest.schemes.get(iprot.getScheme()).getScheme().read(iprot, this);
    }
    
    @Override
    public void write(final TProtocol oprot) throws TException {
        HeartbeatTxnRangeRequest.schemes.get(oprot.getScheme()).getScheme().write(oprot, this);
    }
    
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("HeartbeatTxnRangeRequest(");
        boolean first = true;
        sb.append("min:");
        sb.append(this.min);
        first = false;
        if (!first) {
            sb.append(", ");
        }
        sb.append("max:");
        sb.append(this.max);
        first = false;
        sb.append(")");
        return sb.toString();
    }
    
    public void validate() throws TException {
        if (!this.isSetMin()) {
            throw new TProtocolException("Required field 'min' is unset! Struct:" + this.toString());
        }
        if (!this.isSetMax()) {
            throw new TProtocolException("Required field 'max' is unset! Struct:" + this.toString());
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
        STRUCT_DESC = new TStruct("HeartbeatTxnRangeRequest");
        MIN_FIELD_DESC = new TField("min", (byte)10, (short)1);
        MAX_FIELD_DESC = new TField("max", (byte)10, (short)2);
        (schemes = new HashMap<Class<? extends IScheme>, SchemeFactory>()).put(StandardScheme.class, new HeartbeatTxnRangeRequestStandardSchemeFactory());
        HeartbeatTxnRangeRequest.schemes.put(TupleScheme.class, new HeartbeatTxnRangeRequestTupleSchemeFactory());
        final Map<_Fields, FieldMetaData> tmpMap = new EnumMap<_Fields, FieldMetaData>(_Fields.class);
        tmpMap.put(_Fields.MIN, new FieldMetaData("min", (byte)1, new FieldValueMetaData((byte)10)));
        tmpMap.put(_Fields.MAX, new FieldMetaData("max", (byte)1, new FieldValueMetaData((byte)10)));
        FieldMetaData.addStructMetaDataMap(HeartbeatTxnRangeRequest.class, metaDataMap = Collections.unmodifiableMap((Map<? extends _Fields, ? extends FieldMetaData>)tmpMap));
    }
    
    public enum _Fields implements TFieldIdEnum
    {
        MIN((short)1, "min"), 
        MAX((short)2, "max");
        
        private static final Map<String, _Fields> byName;
        private final short _thriftId;
        private final String _fieldName;
        
        public static _Fields findByThriftId(final int fieldId) {
            switch (fieldId) {
                case 1: {
                    return _Fields.MIN;
                }
                case 2: {
                    return _Fields.MAX;
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
    
    private static class HeartbeatTxnRangeRequestStandardSchemeFactory implements SchemeFactory
    {
        @Override
        public HeartbeatTxnRangeRequestStandardScheme getScheme() {
            return new HeartbeatTxnRangeRequestStandardScheme();
        }
    }
    
    private static class HeartbeatTxnRangeRequestStandardScheme extends StandardScheme<HeartbeatTxnRangeRequest>
    {
        @Override
        public void read(final TProtocol iprot, final HeartbeatTxnRangeRequest struct) throws TException {
            iprot.readStructBegin();
            while (true) {
                final TField schemeField = iprot.readFieldBegin();
                if (schemeField.type == 0) {
                    break;
                }
                switch (schemeField.id) {
                    case 1: {
                        if (schemeField.type == 10) {
                            struct.min = iprot.readI64();
                            struct.setMinIsSet(true);
                            break;
                        }
                        TProtocolUtil.skip(iprot, schemeField.type);
                        break;
                    }
                    case 2: {
                        if (schemeField.type == 10) {
                            struct.max = iprot.readI64();
                            struct.setMaxIsSet(true);
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
        public void write(final TProtocol oprot, final HeartbeatTxnRangeRequest struct) throws TException {
            struct.validate();
            oprot.writeStructBegin(HeartbeatTxnRangeRequest.STRUCT_DESC);
            oprot.writeFieldBegin(HeartbeatTxnRangeRequest.MIN_FIELD_DESC);
            oprot.writeI64(struct.min);
            oprot.writeFieldEnd();
            oprot.writeFieldBegin(HeartbeatTxnRangeRequest.MAX_FIELD_DESC);
            oprot.writeI64(struct.max);
            oprot.writeFieldEnd();
            oprot.writeFieldStop();
            oprot.writeStructEnd();
        }
    }
    
    private static class HeartbeatTxnRangeRequestTupleSchemeFactory implements SchemeFactory
    {
        @Override
        public HeartbeatTxnRangeRequestTupleScheme getScheme() {
            return new HeartbeatTxnRangeRequestTupleScheme();
        }
    }
    
    private static class HeartbeatTxnRangeRequestTupleScheme extends TupleScheme<HeartbeatTxnRangeRequest>
    {
        @Override
        public void write(final TProtocol prot, final HeartbeatTxnRangeRequest struct) throws TException {
            final TTupleProtocol oprot = (TTupleProtocol)prot;
            oprot.writeI64(struct.min);
            oprot.writeI64(struct.max);
        }
        
        @Override
        public void read(final TProtocol prot, final HeartbeatTxnRangeRequest struct) throws TException {
            final TTupleProtocol iprot = (TTupleProtocol)prot;
            struct.min = iprot.readI64();
            struct.setMinIsSet(true);
            struct.max = iprot.readI64();
            struct.setMaxIsSet(true);
        }
    }
}
