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

public class TRenewDelegationTokenReq implements TBase<TRenewDelegationTokenReq, _Fields>, Serializable, Cloneable
{
    private static final TStruct STRUCT_DESC;
    private static final TField SESSION_HANDLE_FIELD_DESC;
    private static final TField DELEGATION_TOKEN_FIELD_DESC;
    private static final Map<Class<? extends IScheme>, SchemeFactory> schemes;
    private TSessionHandle sessionHandle;
    private String delegationToken;
    public static final Map<_Fields, FieldMetaData> metaDataMap;
    
    public TRenewDelegationTokenReq() {
    }
    
    public TRenewDelegationTokenReq(final TSessionHandle sessionHandle, final String delegationToken) {
        this();
        this.sessionHandle = sessionHandle;
        this.delegationToken = delegationToken;
    }
    
    public TRenewDelegationTokenReq(final TRenewDelegationTokenReq other) {
        if (other.isSetSessionHandle()) {
            this.sessionHandle = new TSessionHandle(other.sessionHandle);
        }
        if (other.isSetDelegationToken()) {
            this.delegationToken = other.delegationToken;
        }
    }
    
    @Override
    public TRenewDelegationTokenReq deepCopy() {
        return new TRenewDelegationTokenReq(this);
    }
    
    @Override
    public void clear() {
        this.sessionHandle = null;
        this.delegationToken = null;
    }
    
    public TSessionHandle getSessionHandle() {
        return this.sessionHandle;
    }
    
    public void setSessionHandle(final TSessionHandle sessionHandle) {
        this.sessionHandle = sessionHandle;
    }
    
    public void unsetSessionHandle() {
        this.sessionHandle = null;
    }
    
    public boolean isSetSessionHandle() {
        return this.sessionHandle != null;
    }
    
    public void setSessionHandleIsSet(final boolean value) {
        if (!value) {
            this.sessionHandle = null;
        }
    }
    
    public String getDelegationToken() {
        return this.delegationToken;
    }
    
    public void setDelegationToken(final String delegationToken) {
        this.delegationToken = delegationToken;
    }
    
    public void unsetDelegationToken() {
        this.delegationToken = null;
    }
    
    public boolean isSetDelegationToken() {
        return this.delegationToken != null;
    }
    
    public void setDelegationTokenIsSet(final boolean value) {
        if (!value) {
            this.delegationToken = null;
        }
    }
    
    @Override
    public void setFieldValue(final _Fields field, final Object value) {
        switch (field) {
            case SESSION_HANDLE: {
                if (value == null) {
                    this.unsetSessionHandle();
                    break;
                }
                this.setSessionHandle((TSessionHandle)value);
                break;
            }
            case DELEGATION_TOKEN: {
                if (value == null) {
                    this.unsetDelegationToken();
                    break;
                }
                this.setDelegationToken((String)value);
                break;
            }
        }
    }
    
    @Override
    public Object getFieldValue(final _Fields field) {
        switch (field) {
            case SESSION_HANDLE: {
                return this.getSessionHandle();
            }
            case DELEGATION_TOKEN: {
                return this.getDelegationToken();
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
            case SESSION_HANDLE: {
                return this.isSetSessionHandle();
            }
            case DELEGATION_TOKEN: {
                return this.isSetDelegationToken();
            }
            default: {
                throw new IllegalStateException();
            }
        }
    }
    
    @Override
    public boolean equals(final Object that) {
        return that != null && that instanceof TRenewDelegationTokenReq && this.equals((TRenewDelegationTokenReq)that);
    }
    
    public boolean equals(final TRenewDelegationTokenReq that) {
        if (that == null) {
            return false;
        }
        final boolean this_present_sessionHandle = this.isSetSessionHandle();
        final boolean that_present_sessionHandle = that.isSetSessionHandle();
        if (this_present_sessionHandle || that_present_sessionHandle) {
            if (!this_present_sessionHandle || !that_present_sessionHandle) {
                return false;
            }
            if (!this.sessionHandle.equals(that.sessionHandle)) {
                return false;
            }
        }
        final boolean this_present_delegationToken = this.isSetDelegationToken();
        final boolean that_present_delegationToken = that.isSetDelegationToken();
        if (this_present_delegationToken || that_present_delegationToken) {
            if (!this_present_delegationToken || !that_present_delegationToken) {
                return false;
            }
            if (!this.delegationToken.equals(that.delegationToken)) {
                return false;
            }
        }
        return true;
    }
    
    @Override
    public int hashCode() {
        final HashCodeBuilder builder = new HashCodeBuilder();
        final boolean present_sessionHandle = this.isSetSessionHandle();
        builder.append(present_sessionHandle);
        if (present_sessionHandle) {
            builder.append(this.sessionHandle);
        }
        final boolean present_delegationToken = this.isSetDelegationToken();
        builder.append(present_delegationToken);
        if (present_delegationToken) {
            builder.append(this.delegationToken);
        }
        return builder.toHashCode();
    }
    
    @Override
    public int compareTo(final TRenewDelegationTokenReq other) {
        if (!this.getClass().equals(other.getClass())) {
            return this.getClass().getName().compareTo(other.getClass().getName());
        }
        int lastComparison = 0;
        final TRenewDelegationTokenReq typedOther = other;
        lastComparison = Boolean.valueOf(this.isSetSessionHandle()).compareTo(Boolean.valueOf(typedOther.isSetSessionHandle()));
        if (lastComparison != 0) {
            return lastComparison;
        }
        if (this.isSetSessionHandle()) {
            lastComparison = TBaseHelper.compareTo(this.sessionHandle, typedOther.sessionHandle);
            if (lastComparison != 0) {
                return lastComparison;
            }
        }
        lastComparison = Boolean.valueOf(this.isSetDelegationToken()).compareTo(Boolean.valueOf(typedOther.isSetDelegationToken()));
        if (lastComparison != 0) {
            return lastComparison;
        }
        if (this.isSetDelegationToken()) {
            lastComparison = TBaseHelper.compareTo(this.delegationToken, typedOther.delegationToken);
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
        TRenewDelegationTokenReq.schemes.get(iprot.getScheme()).getScheme().read(iprot, this);
    }
    
    @Override
    public void write(final TProtocol oprot) throws TException {
        TRenewDelegationTokenReq.schemes.get(oprot.getScheme()).getScheme().write(oprot, this);
    }
    
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("TRenewDelegationTokenReq(");
        boolean first = true;
        sb.append("sessionHandle:");
        if (this.sessionHandle == null) {
            sb.append("null");
        }
        else {
            sb.append(this.sessionHandle);
        }
        first = false;
        if (!first) {
            sb.append(", ");
        }
        sb.append("delegationToken:");
        if (this.delegationToken == null) {
            sb.append("null");
        }
        else {
            sb.append(this.delegationToken);
        }
        first = false;
        sb.append(")");
        return sb.toString();
    }
    
    public void validate() throws TException {
        if (!this.isSetSessionHandle()) {
            throw new TProtocolException("Required field 'sessionHandle' is unset! Struct:" + this.toString());
        }
        if (!this.isSetDelegationToken()) {
            throw new TProtocolException("Required field 'delegationToken' is unset! Struct:" + this.toString());
        }
        if (this.sessionHandle != null) {
            this.sessionHandle.validate();
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
        STRUCT_DESC = new TStruct("TRenewDelegationTokenReq");
        SESSION_HANDLE_FIELD_DESC = new TField("sessionHandle", (byte)12, (short)1);
        DELEGATION_TOKEN_FIELD_DESC = new TField("delegationToken", (byte)11, (short)2);
        (schemes = new HashMap<Class<? extends IScheme>, SchemeFactory>()).put(StandardScheme.class, new TRenewDelegationTokenReqStandardSchemeFactory());
        TRenewDelegationTokenReq.schemes.put(TupleScheme.class, new TRenewDelegationTokenReqTupleSchemeFactory());
        final Map<_Fields, FieldMetaData> tmpMap = new EnumMap<_Fields, FieldMetaData>(_Fields.class);
        tmpMap.put(_Fields.SESSION_HANDLE, new FieldMetaData("sessionHandle", (byte)1, new StructMetaData((byte)12, TSessionHandle.class)));
        tmpMap.put(_Fields.DELEGATION_TOKEN, new FieldMetaData("delegationToken", (byte)1, new FieldValueMetaData((byte)11)));
        FieldMetaData.addStructMetaDataMap(TRenewDelegationTokenReq.class, metaDataMap = Collections.unmodifiableMap((Map<? extends _Fields, ? extends FieldMetaData>)tmpMap));
    }
    
    public enum _Fields implements TFieldIdEnum
    {
        SESSION_HANDLE((short)1, "sessionHandle"), 
        DELEGATION_TOKEN((short)2, "delegationToken");
        
        private static final Map<String, _Fields> byName;
        private final short _thriftId;
        private final String _fieldName;
        
        public static _Fields findByThriftId(final int fieldId) {
            switch (fieldId) {
                case 1: {
                    return _Fields.SESSION_HANDLE;
                }
                case 2: {
                    return _Fields.DELEGATION_TOKEN;
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
    
    private static class TRenewDelegationTokenReqStandardSchemeFactory implements SchemeFactory
    {
        @Override
        public TRenewDelegationTokenReqStandardScheme getScheme() {
            return new TRenewDelegationTokenReqStandardScheme();
        }
    }
    
    private static class TRenewDelegationTokenReqStandardScheme extends StandardScheme<TRenewDelegationTokenReq>
    {
        @Override
        public void read(final TProtocol iprot, final TRenewDelegationTokenReq struct) throws TException {
            iprot.readStructBegin();
            while (true) {
                final TField schemeField = iprot.readFieldBegin();
                if (schemeField.type == 0) {
                    break;
                }
                switch (schemeField.id) {
                    case 1: {
                        if (schemeField.type == 12) {
                            struct.sessionHandle = new TSessionHandle();
                            struct.sessionHandle.read(iprot);
                            struct.setSessionHandleIsSet(true);
                            break;
                        }
                        TProtocolUtil.skip(iprot, schemeField.type);
                        break;
                    }
                    case 2: {
                        if (schemeField.type == 11) {
                            struct.delegationToken = iprot.readString();
                            struct.setDelegationTokenIsSet(true);
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
        public void write(final TProtocol oprot, final TRenewDelegationTokenReq struct) throws TException {
            struct.validate();
            oprot.writeStructBegin(TRenewDelegationTokenReq.STRUCT_DESC);
            if (struct.sessionHandle != null) {
                oprot.writeFieldBegin(TRenewDelegationTokenReq.SESSION_HANDLE_FIELD_DESC);
                struct.sessionHandle.write(oprot);
                oprot.writeFieldEnd();
            }
            if (struct.delegationToken != null) {
                oprot.writeFieldBegin(TRenewDelegationTokenReq.DELEGATION_TOKEN_FIELD_DESC);
                oprot.writeString(struct.delegationToken);
                oprot.writeFieldEnd();
            }
            oprot.writeFieldStop();
            oprot.writeStructEnd();
        }
    }
    
    private static class TRenewDelegationTokenReqTupleSchemeFactory implements SchemeFactory
    {
        @Override
        public TRenewDelegationTokenReqTupleScheme getScheme() {
            return new TRenewDelegationTokenReqTupleScheme();
        }
    }
    
    private static class TRenewDelegationTokenReqTupleScheme extends TupleScheme<TRenewDelegationTokenReq>
    {
        @Override
        public void write(final TProtocol prot, final TRenewDelegationTokenReq struct) throws TException {
            final TTupleProtocol oprot = (TTupleProtocol)prot;
            struct.sessionHandle.write(oprot);
            oprot.writeString(struct.delegationToken);
        }
        
        @Override
        public void read(final TProtocol prot, final TRenewDelegationTokenReq struct) throws TException {
            final TTupleProtocol iprot = (TTupleProtocol)prot;
            struct.sessionHandle = new TSessionHandle();
            struct.sessionHandle.read(iprot);
            struct.setSessionHandleIsSet(true);
            struct.delegationToken = iprot.readString();
            struct.setDelegationTokenIsSet(true);
        }
    }
}
