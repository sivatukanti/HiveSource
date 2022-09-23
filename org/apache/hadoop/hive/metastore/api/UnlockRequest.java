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

public class UnlockRequest implements TBase<UnlockRequest, _Fields>, Serializable, Cloneable
{
    private static final TStruct STRUCT_DESC;
    private static final TField LOCKID_FIELD_DESC;
    private static final Map<Class<? extends IScheme>, SchemeFactory> schemes;
    private long lockid;
    private static final int __LOCKID_ISSET_ID = 0;
    private byte __isset_bitfield;
    public static final Map<_Fields, FieldMetaData> metaDataMap;
    
    public UnlockRequest() {
        this.__isset_bitfield = 0;
    }
    
    public UnlockRequest(final long lockid) {
        this();
        this.lockid = lockid;
        this.setLockidIsSet(true);
    }
    
    public UnlockRequest(final UnlockRequest other) {
        this.__isset_bitfield = 0;
        this.__isset_bitfield = other.__isset_bitfield;
        this.lockid = other.lockid;
    }
    
    @Override
    public UnlockRequest deepCopy() {
        return new UnlockRequest(this);
    }
    
    @Override
    public void clear() {
        this.setLockidIsSet(false);
        this.lockid = 0L;
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
        }
    }
    
    @Override
    public Object getFieldValue(final _Fields field) {
        switch (field) {
            case LOCKID: {
                return this.getLockid();
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
            default: {
                throw new IllegalStateException();
            }
        }
    }
    
    @Override
    public boolean equals(final Object that) {
        return that != null && that instanceof UnlockRequest && this.equals((UnlockRequest)that);
    }
    
    public boolean equals(final UnlockRequest that) {
        if (that == null) {
            return false;
        }
        final boolean this_present_lockid = true;
        final boolean that_present_lockid = true;
        if (this_present_lockid || that_present_lockid) {
            if (!this_present_lockid || !that_present_lockid) {
                return false;
            }
            if (this.lockid != that.lockid) {
                return false;
            }
        }
        return true;
    }
    
    @Override
    public int hashCode() {
        final HashCodeBuilder builder = new HashCodeBuilder();
        final boolean present_lockid = true;
        builder.append(present_lockid);
        if (present_lockid) {
            builder.append(this.lockid);
        }
        return builder.toHashCode();
    }
    
    @Override
    public int compareTo(final UnlockRequest other) {
        if (!this.getClass().equals(other.getClass())) {
            return this.getClass().getName().compareTo(other.getClass().getName());
        }
        int lastComparison = 0;
        final UnlockRequest typedOther = other;
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
        return 0;
    }
    
    @Override
    public _Fields fieldForId(final int fieldId) {
        return _Fields.findByThriftId(fieldId);
    }
    
    @Override
    public void read(final TProtocol iprot) throws TException {
        UnlockRequest.schemes.get(iprot.getScheme()).getScheme().read(iprot, this);
    }
    
    @Override
    public void write(final TProtocol oprot) throws TException {
        UnlockRequest.schemes.get(oprot.getScheme()).getScheme().write(oprot, this);
    }
    
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("UnlockRequest(");
        boolean first = true;
        sb.append("lockid:");
        sb.append(this.lockid);
        first = false;
        sb.append(")");
        return sb.toString();
    }
    
    public void validate() throws TException {
        if (!this.isSetLockid()) {
            throw new TProtocolException("Required field 'lockid' is unset! Struct:" + this.toString());
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
        STRUCT_DESC = new TStruct("UnlockRequest");
        LOCKID_FIELD_DESC = new TField("lockid", (byte)10, (short)1);
        (schemes = new HashMap<Class<? extends IScheme>, SchemeFactory>()).put(StandardScheme.class, new UnlockRequestStandardSchemeFactory());
        UnlockRequest.schemes.put(TupleScheme.class, new UnlockRequestTupleSchemeFactory());
        final Map<_Fields, FieldMetaData> tmpMap = new EnumMap<_Fields, FieldMetaData>(_Fields.class);
        tmpMap.put(_Fields.LOCKID, new FieldMetaData("lockid", (byte)1, new FieldValueMetaData((byte)10)));
        FieldMetaData.addStructMetaDataMap(UnlockRequest.class, metaDataMap = Collections.unmodifiableMap((Map<? extends _Fields, ? extends FieldMetaData>)tmpMap));
    }
    
    public enum _Fields implements TFieldIdEnum
    {
        LOCKID((short)1, "lockid");
        
        private static final Map<String, _Fields> byName;
        private final short _thriftId;
        private final String _fieldName;
        
        public static _Fields findByThriftId(final int fieldId) {
            switch (fieldId) {
                case 1: {
                    return _Fields.LOCKID;
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
    
    private static class UnlockRequestStandardSchemeFactory implements SchemeFactory
    {
        @Override
        public UnlockRequestStandardScheme getScheme() {
            return new UnlockRequestStandardScheme();
        }
    }
    
    private static class UnlockRequestStandardScheme extends StandardScheme<UnlockRequest>
    {
        @Override
        public void read(final TProtocol iprot, final UnlockRequest struct) throws TException {
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
        public void write(final TProtocol oprot, final UnlockRequest struct) throws TException {
            struct.validate();
            oprot.writeStructBegin(UnlockRequest.STRUCT_DESC);
            oprot.writeFieldBegin(UnlockRequest.LOCKID_FIELD_DESC);
            oprot.writeI64(struct.lockid);
            oprot.writeFieldEnd();
            oprot.writeFieldStop();
            oprot.writeStructEnd();
        }
    }
    
    private static class UnlockRequestTupleSchemeFactory implements SchemeFactory
    {
        @Override
        public UnlockRequestTupleScheme getScheme() {
            return new UnlockRequestTupleScheme();
        }
    }
    
    private static class UnlockRequestTupleScheme extends TupleScheme<UnlockRequest>
    {
        @Override
        public void write(final TProtocol prot, final UnlockRequest struct) throws TException {
            final TTupleProtocol oprot = (TTupleProtocol)prot;
            oprot.writeI64(struct.lockid);
        }
        
        @Override
        public void read(final TProtocol prot, final UnlockRequest struct) throws TException {
            final TTupleProtocol iprot = (TTupleProtocol)prot;
            struct.lockid = iprot.readI64();
            struct.setLockidIsSet(true);
        }
    }
}
