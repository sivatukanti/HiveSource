// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.hive.metastore.api;

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

public class LockResponse implements TBase<LockResponse, _Fields>, Serializable, Cloneable
{
    private static final TStruct STRUCT_DESC;
    private static final TField LOCKID_FIELD_DESC;
    private static final TField STATE_FIELD_DESC;
    private static final Map<Class<? extends IScheme>, SchemeFactory> schemes;
    private long lockid;
    private LockState state;
    private static final int __LOCKID_ISSET_ID = 0;
    private byte __isset_bitfield;
    public static final Map<_Fields, FieldMetaData> metaDataMap;
    
    public LockResponse() {
        this.__isset_bitfield = 0;
    }
    
    public LockResponse(final long lockid, final LockState state) {
        this();
        this.lockid = lockid;
        this.setLockidIsSet(true);
        this.state = state;
    }
    
    public LockResponse(final LockResponse other) {
        this.__isset_bitfield = 0;
        this.__isset_bitfield = other.__isset_bitfield;
        this.lockid = other.lockid;
        if (other.isSetState()) {
            this.state = other.state;
        }
    }
    
    @Override
    public LockResponse deepCopy() {
        return new LockResponse(this);
    }
    
    @Override
    public void clear() {
        this.setLockidIsSet(false);
        this.lockid = 0L;
        this.state = null;
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
    
    public LockState getState() {
        return this.state;
    }
    
    public void setState(final LockState state) {
        this.state = state;
    }
    
    public void unsetState() {
        this.state = null;
    }
    
    public boolean isSetState() {
        return this.state != null;
    }
    
    public void setStateIsSet(final boolean value) {
        if (!value) {
            this.state = null;
        }
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
            case STATE: {
                if (value == null) {
                    this.unsetState();
                    break;
                }
                this.setState((LockState)value);
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
            case STATE: {
                return this.getState();
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
            case STATE: {
                return this.isSetState();
            }
            default: {
                throw new IllegalStateException();
            }
        }
    }
    
    @Override
    public boolean equals(final Object that) {
        return that != null && that instanceof LockResponse && this.equals((LockResponse)that);
    }
    
    public boolean equals(final LockResponse that) {
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
        final boolean this_present_state = this.isSetState();
        final boolean that_present_state = that.isSetState();
        if (this_present_state || that_present_state) {
            if (!this_present_state || !that_present_state) {
                return false;
            }
            if (!this.state.equals(that.state)) {
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
        final boolean present_state = this.isSetState();
        builder.append(present_state);
        if (present_state) {
            builder.append(this.state.getValue());
        }
        return builder.toHashCode();
    }
    
    @Override
    public int compareTo(final LockResponse other) {
        if (!this.getClass().equals(other.getClass())) {
            return this.getClass().getName().compareTo(other.getClass().getName());
        }
        int lastComparison = 0;
        final LockResponse typedOther = other;
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
        lastComparison = Boolean.valueOf(this.isSetState()).compareTo(Boolean.valueOf(typedOther.isSetState()));
        if (lastComparison != 0) {
            return lastComparison;
        }
        if (this.isSetState()) {
            lastComparison = TBaseHelper.compareTo(this.state, typedOther.state);
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
        LockResponse.schemes.get(iprot.getScheme()).getScheme().read(iprot, this);
    }
    
    @Override
    public void write(final TProtocol oprot) throws TException {
        LockResponse.schemes.get(oprot.getScheme()).getScheme().write(oprot, this);
    }
    
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("LockResponse(");
        boolean first = true;
        sb.append("lockid:");
        sb.append(this.lockid);
        first = false;
        if (!first) {
            sb.append(", ");
        }
        sb.append("state:");
        if (this.state == null) {
            sb.append("null");
        }
        else {
            sb.append(this.state);
        }
        first = false;
        sb.append(")");
        return sb.toString();
    }
    
    public void validate() throws TException {
        if (!this.isSetLockid()) {
            throw new TProtocolException("Required field 'lockid' is unset! Struct:" + this.toString());
        }
        if (!this.isSetState()) {
            throw new TProtocolException("Required field 'state' is unset! Struct:" + this.toString());
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
        STRUCT_DESC = new TStruct("LockResponse");
        LOCKID_FIELD_DESC = new TField("lockid", (byte)10, (short)1);
        STATE_FIELD_DESC = new TField("state", (byte)8, (short)2);
        (schemes = new HashMap<Class<? extends IScheme>, SchemeFactory>()).put(StandardScheme.class, new LockResponseStandardSchemeFactory());
        LockResponse.schemes.put(TupleScheme.class, new LockResponseTupleSchemeFactory());
        final Map<_Fields, FieldMetaData> tmpMap = new EnumMap<_Fields, FieldMetaData>(_Fields.class);
        tmpMap.put(_Fields.LOCKID, new FieldMetaData("lockid", (byte)1, new FieldValueMetaData((byte)10)));
        tmpMap.put(_Fields.STATE, new FieldMetaData("state", (byte)1, new EnumMetaData((byte)16, LockState.class)));
        FieldMetaData.addStructMetaDataMap(LockResponse.class, metaDataMap = Collections.unmodifiableMap((Map<? extends _Fields, ? extends FieldMetaData>)tmpMap));
    }
    
    public enum _Fields implements TFieldIdEnum
    {
        LOCKID((short)1, "lockid"), 
        STATE((short)2, "state");
        
        private static final Map<String, _Fields> byName;
        private final short _thriftId;
        private final String _fieldName;
        
        public static _Fields findByThriftId(final int fieldId) {
            switch (fieldId) {
                case 1: {
                    return _Fields.LOCKID;
                }
                case 2: {
                    return _Fields.STATE;
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
    
    private static class LockResponseStandardSchemeFactory implements SchemeFactory
    {
        @Override
        public LockResponseStandardScheme getScheme() {
            return new LockResponseStandardScheme();
        }
    }
    
    private static class LockResponseStandardScheme extends StandardScheme<LockResponse>
    {
        @Override
        public void read(final TProtocol iprot, final LockResponse struct) throws TException {
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
                        if (schemeField.type == 8) {
                            struct.state = LockState.findByValue(iprot.readI32());
                            struct.setStateIsSet(true);
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
        public void write(final TProtocol oprot, final LockResponse struct) throws TException {
            struct.validate();
            oprot.writeStructBegin(LockResponse.STRUCT_DESC);
            oprot.writeFieldBegin(LockResponse.LOCKID_FIELD_DESC);
            oprot.writeI64(struct.lockid);
            oprot.writeFieldEnd();
            if (struct.state != null) {
                oprot.writeFieldBegin(LockResponse.STATE_FIELD_DESC);
                oprot.writeI32(struct.state.getValue());
                oprot.writeFieldEnd();
            }
            oprot.writeFieldStop();
            oprot.writeStructEnd();
        }
    }
    
    private static class LockResponseTupleSchemeFactory implements SchemeFactory
    {
        @Override
        public LockResponseTupleScheme getScheme() {
            return new LockResponseTupleScheme();
        }
    }
    
    private static class LockResponseTupleScheme extends TupleScheme<LockResponse>
    {
        @Override
        public void write(final TProtocol prot, final LockResponse struct) throws TException {
            final TTupleProtocol oprot = (TTupleProtocol)prot;
            oprot.writeI64(struct.lockid);
            oprot.writeI32(struct.state.getValue());
        }
        
        @Override
        public void read(final TProtocol prot, final LockResponse struct) throws TException {
            final TTupleProtocol iprot = (TTupleProtocol)prot;
            struct.lockid = iprot.readI64();
            struct.setLockidIsSet(true);
            struct.state = LockState.findByValue(iprot.readI32());
            struct.setStateIsSet(true);
        }
    }
}
