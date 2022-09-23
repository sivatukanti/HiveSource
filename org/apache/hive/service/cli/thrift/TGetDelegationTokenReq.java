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

public class TGetDelegationTokenReq implements TBase<TGetDelegationTokenReq, _Fields>, Serializable, Cloneable
{
    private static final TStruct STRUCT_DESC;
    private static final TField SESSION_HANDLE_FIELD_DESC;
    private static final TField OWNER_FIELD_DESC;
    private static final TField RENEWER_FIELD_DESC;
    private static final Map<Class<? extends IScheme>, SchemeFactory> schemes;
    private TSessionHandle sessionHandle;
    private String owner;
    private String renewer;
    public static final Map<_Fields, FieldMetaData> metaDataMap;
    
    public TGetDelegationTokenReq() {
    }
    
    public TGetDelegationTokenReq(final TSessionHandle sessionHandle, final String owner, final String renewer) {
        this();
        this.sessionHandle = sessionHandle;
        this.owner = owner;
        this.renewer = renewer;
    }
    
    public TGetDelegationTokenReq(final TGetDelegationTokenReq other) {
        if (other.isSetSessionHandle()) {
            this.sessionHandle = new TSessionHandle(other.sessionHandle);
        }
        if (other.isSetOwner()) {
            this.owner = other.owner;
        }
        if (other.isSetRenewer()) {
            this.renewer = other.renewer;
        }
    }
    
    @Override
    public TGetDelegationTokenReq deepCopy() {
        return new TGetDelegationTokenReq(this);
    }
    
    @Override
    public void clear() {
        this.sessionHandle = null;
        this.owner = null;
        this.renewer = null;
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
    
    public String getOwner() {
        return this.owner;
    }
    
    public void setOwner(final String owner) {
        this.owner = owner;
    }
    
    public void unsetOwner() {
        this.owner = null;
    }
    
    public boolean isSetOwner() {
        return this.owner != null;
    }
    
    public void setOwnerIsSet(final boolean value) {
        if (!value) {
            this.owner = null;
        }
    }
    
    public String getRenewer() {
        return this.renewer;
    }
    
    public void setRenewer(final String renewer) {
        this.renewer = renewer;
    }
    
    public void unsetRenewer() {
        this.renewer = null;
    }
    
    public boolean isSetRenewer() {
        return this.renewer != null;
    }
    
    public void setRenewerIsSet(final boolean value) {
        if (!value) {
            this.renewer = null;
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
            case OWNER: {
                if (value == null) {
                    this.unsetOwner();
                    break;
                }
                this.setOwner((String)value);
                break;
            }
            case RENEWER: {
                if (value == null) {
                    this.unsetRenewer();
                    break;
                }
                this.setRenewer((String)value);
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
            case OWNER: {
                return this.getOwner();
            }
            case RENEWER: {
                return this.getRenewer();
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
            case OWNER: {
                return this.isSetOwner();
            }
            case RENEWER: {
                return this.isSetRenewer();
            }
            default: {
                throw new IllegalStateException();
            }
        }
    }
    
    @Override
    public boolean equals(final Object that) {
        return that != null && that instanceof TGetDelegationTokenReq && this.equals((TGetDelegationTokenReq)that);
    }
    
    public boolean equals(final TGetDelegationTokenReq that) {
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
        final boolean this_present_owner = this.isSetOwner();
        final boolean that_present_owner = that.isSetOwner();
        if (this_present_owner || that_present_owner) {
            if (!this_present_owner || !that_present_owner) {
                return false;
            }
            if (!this.owner.equals(that.owner)) {
                return false;
            }
        }
        final boolean this_present_renewer = this.isSetRenewer();
        final boolean that_present_renewer = that.isSetRenewer();
        if (this_present_renewer || that_present_renewer) {
            if (!this_present_renewer || !that_present_renewer) {
                return false;
            }
            if (!this.renewer.equals(that.renewer)) {
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
        final boolean present_owner = this.isSetOwner();
        builder.append(present_owner);
        if (present_owner) {
            builder.append(this.owner);
        }
        final boolean present_renewer = this.isSetRenewer();
        builder.append(present_renewer);
        if (present_renewer) {
            builder.append(this.renewer);
        }
        return builder.toHashCode();
    }
    
    @Override
    public int compareTo(final TGetDelegationTokenReq other) {
        if (!this.getClass().equals(other.getClass())) {
            return this.getClass().getName().compareTo(other.getClass().getName());
        }
        int lastComparison = 0;
        final TGetDelegationTokenReq typedOther = other;
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
        lastComparison = Boolean.valueOf(this.isSetOwner()).compareTo(Boolean.valueOf(typedOther.isSetOwner()));
        if (lastComparison != 0) {
            return lastComparison;
        }
        if (this.isSetOwner()) {
            lastComparison = TBaseHelper.compareTo(this.owner, typedOther.owner);
            if (lastComparison != 0) {
                return lastComparison;
            }
        }
        lastComparison = Boolean.valueOf(this.isSetRenewer()).compareTo(Boolean.valueOf(typedOther.isSetRenewer()));
        if (lastComparison != 0) {
            return lastComparison;
        }
        if (this.isSetRenewer()) {
            lastComparison = TBaseHelper.compareTo(this.renewer, typedOther.renewer);
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
        TGetDelegationTokenReq.schemes.get(iprot.getScheme()).getScheme().read(iprot, this);
    }
    
    @Override
    public void write(final TProtocol oprot) throws TException {
        TGetDelegationTokenReq.schemes.get(oprot.getScheme()).getScheme().write(oprot, this);
    }
    
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("TGetDelegationTokenReq(");
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
        sb.append("owner:");
        if (this.owner == null) {
            sb.append("null");
        }
        else {
            sb.append(this.owner);
        }
        first = false;
        if (!first) {
            sb.append(", ");
        }
        sb.append("renewer:");
        if (this.renewer == null) {
            sb.append("null");
        }
        else {
            sb.append(this.renewer);
        }
        first = false;
        sb.append(")");
        return sb.toString();
    }
    
    public void validate() throws TException {
        if (!this.isSetSessionHandle()) {
            throw new TProtocolException("Required field 'sessionHandle' is unset! Struct:" + this.toString());
        }
        if (!this.isSetOwner()) {
            throw new TProtocolException("Required field 'owner' is unset! Struct:" + this.toString());
        }
        if (!this.isSetRenewer()) {
            throw new TProtocolException("Required field 'renewer' is unset! Struct:" + this.toString());
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
        STRUCT_DESC = new TStruct("TGetDelegationTokenReq");
        SESSION_HANDLE_FIELD_DESC = new TField("sessionHandle", (byte)12, (short)1);
        OWNER_FIELD_DESC = new TField("owner", (byte)11, (short)2);
        RENEWER_FIELD_DESC = new TField("renewer", (byte)11, (short)3);
        (schemes = new HashMap<Class<? extends IScheme>, SchemeFactory>()).put(StandardScheme.class, new TGetDelegationTokenReqStandardSchemeFactory());
        TGetDelegationTokenReq.schemes.put(TupleScheme.class, new TGetDelegationTokenReqTupleSchemeFactory());
        final Map<_Fields, FieldMetaData> tmpMap = new EnumMap<_Fields, FieldMetaData>(_Fields.class);
        tmpMap.put(_Fields.SESSION_HANDLE, new FieldMetaData("sessionHandle", (byte)1, new StructMetaData((byte)12, TSessionHandle.class)));
        tmpMap.put(_Fields.OWNER, new FieldMetaData("owner", (byte)1, new FieldValueMetaData((byte)11)));
        tmpMap.put(_Fields.RENEWER, new FieldMetaData("renewer", (byte)1, new FieldValueMetaData((byte)11)));
        FieldMetaData.addStructMetaDataMap(TGetDelegationTokenReq.class, metaDataMap = Collections.unmodifiableMap((Map<? extends _Fields, ? extends FieldMetaData>)tmpMap));
    }
    
    public enum _Fields implements TFieldIdEnum
    {
        SESSION_HANDLE((short)1, "sessionHandle"), 
        OWNER((short)2, "owner"), 
        RENEWER((short)3, "renewer");
        
        private static final Map<String, _Fields> byName;
        private final short _thriftId;
        private final String _fieldName;
        
        public static _Fields findByThriftId(final int fieldId) {
            switch (fieldId) {
                case 1: {
                    return _Fields.SESSION_HANDLE;
                }
                case 2: {
                    return _Fields.OWNER;
                }
                case 3: {
                    return _Fields.RENEWER;
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
    
    private static class TGetDelegationTokenReqStandardSchemeFactory implements SchemeFactory
    {
        @Override
        public TGetDelegationTokenReqStandardScheme getScheme() {
            return new TGetDelegationTokenReqStandardScheme();
        }
    }
    
    private static class TGetDelegationTokenReqStandardScheme extends StandardScheme<TGetDelegationTokenReq>
    {
        @Override
        public void read(final TProtocol iprot, final TGetDelegationTokenReq struct) throws TException {
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
                            struct.owner = iprot.readString();
                            struct.setOwnerIsSet(true);
                            break;
                        }
                        TProtocolUtil.skip(iprot, schemeField.type);
                        break;
                    }
                    case 3: {
                        if (schemeField.type == 11) {
                            struct.renewer = iprot.readString();
                            struct.setRenewerIsSet(true);
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
        public void write(final TProtocol oprot, final TGetDelegationTokenReq struct) throws TException {
            struct.validate();
            oprot.writeStructBegin(TGetDelegationTokenReq.STRUCT_DESC);
            if (struct.sessionHandle != null) {
                oprot.writeFieldBegin(TGetDelegationTokenReq.SESSION_HANDLE_FIELD_DESC);
                struct.sessionHandle.write(oprot);
                oprot.writeFieldEnd();
            }
            if (struct.owner != null) {
                oprot.writeFieldBegin(TGetDelegationTokenReq.OWNER_FIELD_DESC);
                oprot.writeString(struct.owner);
                oprot.writeFieldEnd();
            }
            if (struct.renewer != null) {
                oprot.writeFieldBegin(TGetDelegationTokenReq.RENEWER_FIELD_DESC);
                oprot.writeString(struct.renewer);
                oprot.writeFieldEnd();
            }
            oprot.writeFieldStop();
            oprot.writeStructEnd();
        }
    }
    
    private static class TGetDelegationTokenReqTupleSchemeFactory implements SchemeFactory
    {
        @Override
        public TGetDelegationTokenReqTupleScheme getScheme() {
            return new TGetDelegationTokenReqTupleScheme();
        }
    }
    
    private static class TGetDelegationTokenReqTupleScheme extends TupleScheme<TGetDelegationTokenReq>
    {
        @Override
        public void write(final TProtocol prot, final TGetDelegationTokenReq struct) throws TException {
            final TTupleProtocol oprot = (TTupleProtocol)prot;
            struct.sessionHandle.write(oprot);
            oprot.writeString(struct.owner);
            oprot.writeString(struct.renewer);
        }
        
        @Override
        public void read(final TProtocol prot, final TGetDelegationTokenReq struct) throws TException {
            final TTupleProtocol iprot = (TTupleProtocol)prot;
            struct.sessionHandle = new TSessionHandle();
            struct.sessionHandle.read(iprot);
            struct.setSessionHandleIsSet(true);
            struct.owner = iprot.readString();
            struct.setOwnerIsSet(true);
            struct.renewer = iprot.readString();
            struct.setRenewerIsSet(true);
        }
    }
}
