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

public class CommitTxnRequest implements TBase<CommitTxnRequest, _Fields>, Serializable, Cloneable
{
    private static final TStruct STRUCT_DESC;
    private static final TField TXNID_FIELD_DESC;
    private static final Map<Class<? extends IScheme>, SchemeFactory> schemes;
    private long txnid;
    private static final int __TXNID_ISSET_ID = 0;
    private byte __isset_bitfield;
    public static final Map<_Fields, FieldMetaData> metaDataMap;
    
    public CommitTxnRequest() {
        this.__isset_bitfield = 0;
    }
    
    public CommitTxnRequest(final long txnid) {
        this();
        this.txnid = txnid;
        this.setTxnidIsSet(true);
    }
    
    public CommitTxnRequest(final CommitTxnRequest other) {
        this.__isset_bitfield = 0;
        this.__isset_bitfield = other.__isset_bitfield;
        this.txnid = other.txnid;
    }
    
    @Override
    public CommitTxnRequest deepCopy() {
        return new CommitTxnRequest(this);
    }
    
    @Override
    public void clear() {
        this.setTxnidIsSet(false);
        this.txnid = 0L;
    }
    
    public long getTxnid() {
        return this.txnid;
    }
    
    public void setTxnid(final long txnid) {
        this.txnid = txnid;
        this.setTxnidIsSet(true);
    }
    
    public void unsetTxnid() {
        this.__isset_bitfield = EncodingUtils.clearBit(this.__isset_bitfield, 0);
    }
    
    public boolean isSetTxnid() {
        return EncodingUtils.testBit(this.__isset_bitfield, 0);
    }
    
    public void setTxnidIsSet(final boolean value) {
        this.__isset_bitfield = EncodingUtils.setBit(this.__isset_bitfield, 0, value);
    }
    
    @Override
    public void setFieldValue(final _Fields field, final Object value) {
        switch (field) {
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
        return that != null && that instanceof CommitTxnRequest && this.equals((CommitTxnRequest)that);
    }
    
    public boolean equals(final CommitTxnRequest that) {
        if (that == null) {
            return false;
        }
        final boolean this_present_txnid = true;
        final boolean that_present_txnid = true;
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
        final boolean present_txnid = true;
        builder.append(present_txnid);
        if (present_txnid) {
            builder.append(this.txnid);
        }
        return builder.toHashCode();
    }
    
    @Override
    public int compareTo(final CommitTxnRequest other) {
        if (!this.getClass().equals(other.getClass())) {
            return this.getClass().getName().compareTo(other.getClass().getName());
        }
        int lastComparison = 0;
        final CommitTxnRequest typedOther = other;
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
        CommitTxnRequest.schemes.get(iprot.getScheme()).getScheme().read(iprot, this);
    }
    
    @Override
    public void write(final TProtocol oprot) throws TException {
        CommitTxnRequest.schemes.get(oprot.getScheme()).getScheme().write(oprot, this);
    }
    
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("CommitTxnRequest(");
        boolean first = true;
        sb.append("txnid:");
        sb.append(this.txnid);
        first = false;
        sb.append(")");
        return sb.toString();
    }
    
    public void validate() throws TException {
        if (!this.isSetTxnid()) {
            throw new TProtocolException("Required field 'txnid' is unset! Struct:" + this.toString());
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
        STRUCT_DESC = new TStruct("CommitTxnRequest");
        TXNID_FIELD_DESC = new TField("txnid", (byte)10, (short)1);
        (schemes = new HashMap<Class<? extends IScheme>, SchemeFactory>()).put(StandardScheme.class, new CommitTxnRequestStandardSchemeFactory());
        CommitTxnRequest.schemes.put(TupleScheme.class, new CommitTxnRequestTupleSchemeFactory());
        final Map<_Fields, FieldMetaData> tmpMap = new EnumMap<_Fields, FieldMetaData>(_Fields.class);
        tmpMap.put(_Fields.TXNID, new FieldMetaData("txnid", (byte)1, new FieldValueMetaData((byte)10)));
        FieldMetaData.addStructMetaDataMap(CommitTxnRequest.class, metaDataMap = Collections.unmodifiableMap((Map<? extends _Fields, ? extends FieldMetaData>)tmpMap));
    }
    
    public enum _Fields implements TFieldIdEnum
    {
        TXNID((short)1, "txnid");
        
        private static final Map<String, _Fields> byName;
        private final short _thriftId;
        private final String _fieldName;
        
        public static _Fields findByThriftId(final int fieldId) {
            switch (fieldId) {
                case 1: {
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
    
    private static class CommitTxnRequestStandardSchemeFactory implements SchemeFactory
    {
        @Override
        public CommitTxnRequestStandardScheme getScheme() {
            return new CommitTxnRequestStandardScheme();
        }
    }
    
    private static class CommitTxnRequestStandardScheme extends StandardScheme<CommitTxnRequest>
    {
        @Override
        public void read(final TProtocol iprot, final CommitTxnRequest struct) throws TException {
            iprot.readStructBegin();
            while (true) {
                final TField schemeField = iprot.readFieldBegin();
                if (schemeField.type == 0) {
                    break;
                }
                switch (schemeField.id) {
                    case 1: {
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
        public void write(final TProtocol oprot, final CommitTxnRequest struct) throws TException {
            struct.validate();
            oprot.writeStructBegin(CommitTxnRequest.STRUCT_DESC);
            oprot.writeFieldBegin(CommitTxnRequest.TXNID_FIELD_DESC);
            oprot.writeI64(struct.txnid);
            oprot.writeFieldEnd();
            oprot.writeFieldStop();
            oprot.writeStructEnd();
        }
    }
    
    private static class CommitTxnRequestTupleSchemeFactory implements SchemeFactory
    {
        @Override
        public CommitTxnRequestTupleScheme getScheme() {
            return new CommitTxnRequestTupleScheme();
        }
    }
    
    private static class CommitTxnRequestTupleScheme extends TupleScheme<CommitTxnRequest>
    {
        @Override
        public void write(final TProtocol prot, final CommitTxnRequest struct) throws TException {
            final TTupleProtocol oprot = (TTupleProtocol)prot;
            oprot.writeI64(struct.txnid);
        }
        
        @Override
        public void read(final TProtocol prot, final CommitTxnRequest struct) throws TException {
            final TTupleProtocol iprot = (TTupleProtocol)prot;
            struct.txnid = iprot.readI64();
            struct.setTxnidIsSet(true);
        }
    }
}
