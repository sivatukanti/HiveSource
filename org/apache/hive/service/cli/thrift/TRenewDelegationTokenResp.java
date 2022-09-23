// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hive.service.cli.thrift;

import org.apache.thrift.protocol.TTupleProtocol;
import org.apache.thrift.protocol.TProtocolUtil;
import java.util.Iterator;
import java.util.EnumSet;
import java.util.Collections;
import org.apache.thrift.meta_data.FieldValueMetaData;
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
import org.apache.thrift.protocol.TProtocolException;
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

public class TRenewDelegationTokenResp implements TBase<TRenewDelegationTokenResp, _Fields>, Serializable, Cloneable
{
    private static final TStruct STRUCT_DESC;
    private static final TField STATUS_FIELD_DESC;
    private static final Map<Class<? extends IScheme>, SchemeFactory> schemes;
    private TStatus status;
    public static final Map<_Fields, FieldMetaData> metaDataMap;
    
    public TRenewDelegationTokenResp() {
    }
    
    public TRenewDelegationTokenResp(final TStatus status) {
        this();
        this.status = status;
    }
    
    public TRenewDelegationTokenResp(final TRenewDelegationTokenResp other) {
        if (other.isSetStatus()) {
            this.status = new TStatus(other.status);
        }
    }
    
    @Override
    public TRenewDelegationTokenResp deepCopy() {
        return new TRenewDelegationTokenResp(this);
    }
    
    @Override
    public void clear() {
        this.status = null;
    }
    
    public TStatus getStatus() {
        return this.status;
    }
    
    public void setStatus(final TStatus status) {
        this.status = status;
    }
    
    public void unsetStatus() {
        this.status = null;
    }
    
    public boolean isSetStatus() {
        return this.status != null;
    }
    
    public void setStatusIsSet(final boolean value) {
        if (!value) {
            this.status = null;
        }
    }
    
    @Override
    public void setFieldValue(final _Fields field, final Object value) {
        switch (field) {
            case STATUS: {
                if (value == null) {
                    this.unsetStatus();
                    break;
                }
                this.setStatus((TStatus)value);
                break;
            }
        }
    }
    
    @Override
    public Object getFieldValue(final _Fields field) {
        switch (field) {
            case STATUS: {
                return this.getStatus();
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
            case STATUS: {
                return this.isSetStatus();
            }
            default: {
                throw new IllegalStateException();
            }
        }
    }
    
    @Override
    public boolean equals(final Object that) {
        return that != null && that instanceof TRenewDelegationTokenResp && this.equals((TRenewDelegationTokenResp)that);
    }
    
    public boolean equals(final TRenewDelegationTokenResp that) {
        if (that == null) {
            return false;
        }
        final boolean this_present_status = this.isSetStatus();
        final boolean that_present_status = that.isSetStatus();
        if (this_present_status || that_present_status) {
            if (!this_present_status || !that_present_status) {
                return false;
            }
            if (!this.status.equals(that.status)) {
                return false;
            }
        }
        return true;
    }
    
    @Override
    public int hashCode() {
        final HashCodeBuilder builder = new HashCodeBuilder();
        final boolean present_status = this.isSetStatus();
        builder.append(present_status);
        if (present_status) {
            builder.append(this.status);
        }
        return builder.toHashCode();
    }
    
    @Override
    public int compareTo(final TRenewDelegationTokenResp other) {
        if (!this.getClass().equals(other.getClass())) {
            return this.getClass().getName().compareTo(other.getClass().getName());
        }
        int lastComparison = 0;
        final TRenewDelegationTokenResp typedOther = other;
        lastComparison = Boolean.valueOf(this.isSetStatus()).compareTo(Boolean.valueOf(typedOther.isSetStatus()));
        if (lastComparison != 0) {
            return lastComparison;
        }
        if (this.isSetStatus()) {
            lastComparison = TBaseHelper.compareTo(this.status, typedOther.status);
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
        TRenewDelegationTokenResp.schemes.get(iprot.getScheme()).getScheme().read(iprot, this);
    }
    
    @Override
    public void write(final TProtocol oprot) throws TException {
        TRenewDelegationTokenResp.schemes.get(oprot.getScheme()).getScheme().write(oprot, this);
    }
    
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("TRenewDelegationTokenResp(");
        boolean first = true;
        sb.append("status:");
        if (this.status == null) {
            sb.append("null");
        }
        else {
            sb.append(this.status);
        }
        first = false;
        sb.append(")");
        return sb.toString();
    }
    
    public void validate() throws TException {
        if (!this.isSetStatus()) {
            throw new TProtocolException("Required field 'status' is unset! Struct:" + this.toString());
        }
        if (this.status != null) {
            this.status.validate();
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
            this.read(new TCompactProtocol(new TIOStreamTransport(in)));
        }
        catch (TException te) {
            throw new IOException(te);
        }
    }
    
    static {
        STRUCT_DESC = new TStruct("TRenewDelegationTokenResp");
        STATUS_FIELD_DESC = new TField("status", (byte)12, (short)1);
        (schemes = new HashMap<Class<? extends IScheme>, SchemeFactory>()).put(StandardScheme.class, new TRenewDelegationTokenRespStandardSchemeFactory());
        TRenewDelegationTokenResp.schemes.put(TupleScheme.class, new TRenewDelegationTokenRespTupleSchemeFactory());
        final Map<_Fields, FieldMetaData> tmpMap = new EnumMap<_Fields, FieldMetaData>(_Fields.class);
        tmpMap.put(_Fields.STATUS, new FieldMetaData("status", (byte)1, new StructMetaData((byte)12, TStatus.class)));
        FieldMetaData.addStructMetaDataMap(TRenewDelegationTokenResp.class, metaDataMap = Collections.unmodifiableMap((Map<? extends _Fields, ? extends FieldMetaData>)tmpMap));
    }
    
    public enum _Fields implements TFieldIdEnum
    {
        STATUS((short)1, "status");
        
        private static final Map<String, _Fields> byName;
        private final short _thriftId;
        private final String _fieldName;
        
        public static _Fields findByThriftId(final int fieldId) {
            switch (fieldId) {
                case 1: {
                    return _Fields.STATUS;
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
    
    private static class TRenewDelegationTokenRespStandardSchemeFactory implements SchemeFactory
    {
        @Override
        public TRenewDelegationTokenRespStandardScheme getScheme() {
            return new TRenewDelegationTokenRespStandardScheme();
        }
    }
    
    private static class TRenewDelegationTokenRespStandardScheme extends StandardScheme<TRenewDelegationTokenResp>
    {
        @Override
        public void read(final TProtocol iprot, final TRenewDelegationTokenResp struct) throws TException {
            iprot.readStructBegin();
            while (true) {
                final TField schemeField = iprot.readFieldBegin();
                if (schemeField.type == 0) {
                    break;
                }
                switch (schemeField.id) {
                    case 1: {
                        if (schemeField.type == 12) {
                            struct.status = new TStatus();
                            struct.status.read(iprot);
                            struct.setStatusIsSet(true);
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
        public void write(final TProtocol oprot, final TRenewDelegationTokenResp struct) throws TException {
            struct.validate();
            oprot.writeStructBegin(TRenewDelegationTokenResp.STRUCT_DESC);
            if (struct.status != null) {
                oprot.writeFieldBegin(TRenewDelegationTokenResp.STATUS_FIELD_DESC);
                struct.status.write(oprot);
                oprot.writeFieldEnd();
            }
            oprot.writeFieldStop();
            oprot.writeStructEnd();
        }
    }
    
    private static class TRenewDelegationTokenRespTupleSchemeFactory implements SchemeFactory
    {
        @Override
        public TRenewDelegationTokenRespTupleScheme getScheme() {
            return new TRenewDelegationTokenRespTupleScheme();
        }
    }
    
    private static class TRenewDelegationTokenRespTupleScheme extends TupleScheme<TRenewDelegationTokenResp>
    {
        @Override
        public void write(final TProtocol prot, final TRenewDelegationTokenResp struct) throws TException {
            final TTupleProtocol oprot = (TTupleProtocol)prot;
            struct.status.write(oprot);
        }
        
        @Override
        public void read(final TProtocol prot, final TRenewDelegationTokenResp struct) throws TException {
            final TTupleProtocol iprot = (TTupleProtocol)prot;
            struct.status = new TStatus();
            struct.status.read(iprot);
            struct.setStatusIsSet(true);
        }
    }
}
