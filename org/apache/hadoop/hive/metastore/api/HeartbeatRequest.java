// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.hive.metastore.api;

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

public class HeartbeatRequest implements TBase<HeartbeatRequest, _Fields>, Serializable, Cloneable
{
    private static final TStruct STRUCT_DESC;
    private static final TField LOCKID_FIELD_DESC;
    private static final TField TXNID_FIELD_DESC;
    private static final Map<Class<? extends IScheme>, SchemeFactory> schemes;
    private long lockid;
    private long txnid;
    private static final int __LOCKID_ISSET_ID = 0;
    private static final int __TXNID_ISSET_ID = 1;
    private byte __isset_bitfield;
    private _Fields[] optionals;
    public static final Map<_Fields, FieldMetaData> metaDataMap;
    
    public HeartbeatRequest() {
        this.__isset_bitfield = 0;
        this.optionals = new _Fields[] { _Fields.LOCKID, _Fields.TXNID };
    }
    
    public HeartbeatRequest(final HeartbeatRequest other) {
        this.__isset_bitfield = 0;
        this.optionals = new _Fields[] { _Fields.LOCKID, _Fields.TXNID };
        this.__isset_bitfield = other.__isset_bitfield;
        this.lockid = other.lockid;
        this.txnid = other.txnid;
    }
    
    @Override
    public HeartbeatRequest deepCopy() {
        return new HeartbeatRequest(this);
    }
    
    @Override
    public void clear() {
        this.setLockidIsSet(false);
        this.lockid = 0L;
        this.setTxnidIsSet(false);
        this.txnid = 0L;
    }
    
    public long getLockid() {
        return this.lockid;
    }
    
    public void setLockid(final long lockid) {
        this.lockid = lockid;
        this.setLockidIsSet(true);
    }
    
    public void unsetLockid() {
        this.__isset_bitfield = EncodingUtils.clearBit(this.__isset_bitfield, 0);
    }
    
    public boolean isSetLockid() {
        return EncodingUtils.testBit(this.__isset_bitfield, 0);
    }
    
    public void setLockidIsSet(final boolean value) {
        this.__isset_bitfield = EncodingUtils.setBit(this.__isset_bitfield, 0, value);
    }
    
    public long getTxnid() {
        return this.txnid;
    }
    
    public void setTxnid(final long txnid) {
        this.txnid = txnid;
        this.setTxnidIsSet(true);
    }
    
    public void unsetTxnid() {
        this.__isset_bitfield = EncodingUtils.clearBit(this.__isset_bitfield, 1);
    }
    
    public boolean isSetTxnid() {
        return EncodingUtils.testBit(this.__isset_bitfield, 1);
    }
    
    public void setTxnidIsSet(final boolean value) {
        this.__isset_bitfield = EncodingUtils.setBit(this.__isset_bitfield, 1, value);
    }
    
    @Override
    public void setFieldValue(final _Fields field, final Object value) {
        switch (field) {
            case LOCKID: {
                if (value == null) {
                    this.unsetLockid();
                    break;
                }
                this.setLockid((long)value);
                break;
            }
            case TXNID: {
                if (value == null) {
                    this.unsetTxnid();
                    break;
                }
                this.setTxnid((long)value);
                break;
            }
        }
    }
    
    @Override
    public Object getFieldValue(final _Fields field) {
        switch (field) {
            case LOCKID: {
                return this.getLockid();
            }
            case TXNID: {
                return this.getTxnid();
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
            case LOCKID: {
                return this.isSetLockid();
            }
            case TXNID: {
                return this.isSetTxnid();
            }
            default: {
                throw new IllegalStateException();
            }
        }
    }
    
    @Override
    public boolean equals(final Object that) {
        return that != null && that instanceof HeartbeatRequest && this.equals((HeartbeatRequest)that);
    }
    
    public boolean equals(final HeartbeatRequest that) {
        if (that == null) {
            return false;
        }
        final boolean this_present_lockid = this.isSetLockid();
        final boolean that_present_lockid = that.isSetLockid();
        if (this_present_lockid || that_present_lockid) {
            if (!this_present_lockid || !that_present_lockid) {
                return false;
            }
            if (this.lockid != that.lockid) {
                return false;
            }
        }
        final boolean this_present_txnid = this.isSetTxnid();
        final boolean that_present_txnid = that.isSetTxnid();
        if (this_present_txnid || that_present_txnid) {
            if (!this_present_txnid || !that_present_txnid) {
                return false;
            }
            if (this.txnid != that.txnid) {
                return false;
            }
        }
        return true;
    }
    
    @Override
    public int hashCode() {
        final HashCodeBuilder builder = new HashCodeBuilder();
        final boolean present_lockid = this.isSetLockid();
        builder.append(present_lockid);
        if (present_lockid) {
            builder.append(this.lockid);
        }
        final boolean present_txnid = this.isSetTxnid();
        builder.append(present_txnid);
        if (present_txnid) {
            builder.append(this.txnid);
        }
        return builder.toHashCode();
    }
    
    @Override
    public int compareTo(final HeartbeatRequest other) {
        if (!this.getClass().equals(other.getClass())) {
            return this.getClass().getName().compareTo(other.getClass().getName());
        }
        int lastComparison = 0;
        final HeartbeatRequest typedOther = other;
        lastComparison = Boolean.valueOf(this.isSetLockid()).compareTo(Boolean.valueOf(typedOther.isSetLockid()));
        if (lastComparison != 0) {
            return lastComparison;
        }
        if (this.isSetLockid()) {
            lastComparison = TBaseHelper.compareTo(this.lockid, typedOther.lockid);
            if (lastComparison != 0) {
                return lastComparison;
            }
        }
        lastComparison = Boolean.valueOf(this.isSetTxnid()).compareTo(Boolean.valueOf(typedOther.isSetTxnid()));
        if (lastComparison != 0) {
            return lastComparison;
        }
        if (this.isSetTxnid()) {
            lastComparison = TBaseHelper.compareTo(this.txnid, typedOther.txnid);
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
        HeartbeatRequest.schemes.get(iprot.getScheme()).getScheme().read(iprot, this);
    }
    
    @Override
    public void write(final TProtocol oprot) throws TException {
        HeartbeatRequest.schemes.get(oprot.getScheme()).getScheme().write(oprot, this);
    }
    
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("HeartbeatRequest(");
        boolean first = true;
        if (this.isSetLockid()) {
            sb.append("lockid:");
            sb.append(this.lockid);
            first = false;
        }
        if (this.isSetTxnid()) {
            if (!first) {
                sb.append(", ");
            }
            sb.append("txnid:");
            sb.append(this.txnid);
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
            this.__isset_bitfield = 0;
            this.read(new TCompactProtocol(new TIOStreamTransport(in)));
        }
        catch (TException te) {
            throw new IOException(te);
        }
    }
    
    static {
        STRUCT_DESC = new TStruct("HeartbeatRequest");
        LOCKID_FIELD_DESC = new TField("lockid", (byte)10, (short)1);
        TXNID_FIELD_DESC = new TField("txnid", (byte)10, (short)2);
        (schemes = new HashMap<Class<? extends IScheme>, SchemeFactory>()).put(StandardScheme.class, new HeartbeatRequestStandardSchemeFactory());
        HeartbeatRequest.schemes.put(TupleScheme.class, new HeartbeatRequestTupleSchemeFactory());
        final Map<_Fields, FieldMetaData> tmpMap = new EnumMap<_Fields, FieldMetaData>(_Fields.class);
        tmpMap.put(_Fields.LOCKID, new FieldMetaData("lockid", (byte)2, new FieldValueMetaData((byte)10)));
        tmpMap.put(_Fields.TXNID, new FieldMetaData("txnid", (byte)2, new FieldValueMetaData((byte)10)));
        FieldMetaData.addStructMetaDataMap(HeartbeatRequest.class, metaDataMap = Collections.unmodifiableMap((Map<? extends _Fields, ? extends FieldMetaData>)tmpMap));
    }
    
    public enum _Fields implements TFieldIdEnum
    {
        LOCKID((short)1, "lockid"), 
        TXNID((short)2, "txnid");
        
        private static final Map<String, _Fields> byName;
        private final short _thriftId;
        private final String _fieldName;
        
        public static _Fields findByThriftId(final int fieldId) {
            switch (fieldId) {
                case 1: {
                    return _Fields.LOCKID;
                }
                case 2: {
                    return _Fields.TXNID;
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
    
    private static class HeartbeatRequestStandardSchemeFactory implements SchemeFactory
    {
        @Override
        public HeartbeatRequestStandardScheme getScheme() {
            return new HeartbeatRequestStandardScheme();
        }
    }
    
    private static class HeartbeatRequestStandardScheme extends StandardScheme<HeartbeatRequest>
    {
        @Override
        public void read(final TProtocol iprot, final HeartbeatRequest struct) throws TException {
            iprot.readStructBegin();
            while (true) {
                final TField schemeField = iprot.readFieldBegin();
                if (schemeField.type == 0) {
                    break;
                }
                switch (schemeField.id) {
                    case 1: {
                        if (schemeField.type == 10) {
                            struct.lockid = iprot.readI64();
                            struct.setLockidIsSet(true);
                            break;
                        }
                        TProtocolUtil.skip(iprot, schemeField.type);
                        break;
                    }
                    case 2: {
                        if (schemeField.type == 10) {
                            struct.txnid = iprot.readI64();
                            struct.setTxnidIsSet(true);
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
        public void write(final TProtocol oprot, final HeartbeatRequest struct) throws TException {
            struct.validate();
            oprot.writeStructBegin(HeartbeatRequest.STRUCT_DESC);
            if (struct.isSetLockid()) {
                oprot.writeFieldBegin(HeartbeatRequest.LOCKID_FIELD_DESC);
                oprot.writeI64(struct.lockid);
                oprot.writeFieldEnd();
            }
            if (struct.isSetTxnid()) {
                oprot.writeFieldBegin(HeartbeatRequest.TXNID_FIELD_DESC);
                oprot.writeI64(struct.txnid);
                oprot.writeFieldEnd();
            }
            oprot.writeFieldStop();
            oprot.writeStructEnd();
        }
    }
    
    private static class HeartbeatRequestTupleSchemeFactory implements SchemeFactory
    {
        @Override
        public HeartbeatRequestTupleScheme getScheme() {
            return new HeartbeatRequestTupleScheme();
        }
    }
    
    private static class HeartbeatRequestTupleScheme extends TupleScheme<HeartbeatRequest>
    {
        @Override
        public void write(final TProtocol prot, final HeartbeatRequest struct) throws TException {
            final TTupleProtocol oprot = (TTupleProtocol)prot;
            final BitSet optionals = new BitSet();
            if (struct.isSetLockid()) {
                optionals.set(0);
            }
            if (struct.isSetTxnid()) {
                optionals.set(1);
            }
            oprot.writeBitSet(optionals, 2);
            if (struct.isSetLockid()) {
                oprot.writeI64(struct.lockid);
            }
            if (struct.isSetTxnid()) {
                oprot.writeI64(struct.txnid);
            }
        }
        
        @Override
        public void read(final TProtocol prot, final HeartbeatRequest struct) throws TException {
            final TTupleProtocol iprot = (TTupleProtocol)prot;
            final BitSet incoming = iprot.readBitSet(2);
            if (incoming.get(0)) {
                struct.lockid = iprot.readI64();
                struct.setLockidIsSet(true);
            }
            if (incoming.get(1)) {
                struct.txnid = iprot.readI64();
                struct.setTxnidIsSet(true);
            }
        }
    }
}
