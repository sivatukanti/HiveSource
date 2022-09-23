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
import org.apache.thrift.meta_data.StructMetaData;
import org.apache.thrift.meta_data.FieldValueMetaData;
import org.apache.thrift.TEnum;
import org.apache.thrift.meta_data.EnumMetaData;
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

public class GrantRevokePrivilegeRequest implements TBase<GrantRevokePrivilegeRequest, _Fields>, Serializable, Cloneable
{
    private static final TStruct STRUCT_DESC;
    private static final TField REQUEST_TYPE_FIELD_DESC;
    private static final TField PRIVILEGES_FIELD_DESC;
    private static final TField REVOKE_GRANT_OPTION_FIELD_DESC;
    private static final Map<Class<? extends IScheme>, SchemeFactory> schemes;
    private GrantRevokeType requestType;
    private PrivilegeBag privileges;
    private boolean revokeGrantOption;
    private static final int __REVOKEGRANTOPTION_ISSET_ID = 0;
    private byte __isset_bitfield;
    private _Fields[] optionals;
    public static final Map<_Fields, FieldMetaData> metaDataMap;
    
    public GrantRevokePrivilegeRequest() {
        this.__isset_bitfield = 0;
        this.optionals = new _Fields[] { _Fields.REVOKE_GRANT_OPTION };
    }
    
    public GrantRevokePrivilegeRequest(final GrantRevokeType requestType, final PrivilegeBag privileges) {
        this();
        this.requestType = requestType;
        this.privileges = privileges;
    }
    
    public GrantRevokePrivilegeRequest(final GrantRevokePrivilegeRequest other) {
        this.__isset_bitfield = 0;
        this.optionals = new _Fields[] { _Fields.REVOKE_GRANT_OPTION };
        this.__isset_bitfield = other.__isset_bitfield;
        if (other.isSetRequestType()) {
            this.requestType = other.requestType;
        }
        if (other.isSetPrivileges()) {
            this.privileges = new PrivilegeBag(other.privileges);
        }
        this.revokeGrantOption = other.revokeGrantOption;
    }
    
    @Override
    public GrantRevokePrivilegeRequest deepCopy() {
        return new GrantRevokePrivilegeRequest(this);
    }
    
    @Override
    public void clear() {
        this.requestType = null;
        this.privileges = null;
        this.setRevokeGrantOptionIsSet(false);
        this.revokeGrantOption = false;
    }
    
    public GrantRevokeType getRequestType() {
        return this.requestType;
    }
    
    public void setRequestType(final GrantRevokeType requestType) {
        this.requestType = requestType;
    }
    
    public void unsetRequestType() {
        this.requestType = null;
    }
    
    public boolean isSetRequestType() {
        return this.requestType != null;
    }
    
    public void setRequestTypeIsSet(final boolean value) {
        if (!value) {
            this.requestType = null;
        }
    }
    
    public PrivilegeBag getPrivileges() {
        return this.privileges;
    }
    
    public void setPrivileges(final PrivilegeBag privileges) {
        this.privileges = privileges;
    }
    
    public void unsetPrivileges() {
        this.privileges = null;
    }
    
    public boolean isSetPrivileges() {
        return this.privileges != null;
    }
    
    public void setPrivilegesIsSet(final boolean value) {
        if (!value) {
            this.privileges = null;
        }
    }
    
    public boolean isRevokeGrantOption() {
        return this.revokeGrantOption;
    }
    
    public void setRevokeGrantOption(final boolean revokeGrantOption) {
        this.revokeGrantOption = revokeGrantOption;
        this.setRevokeGrantOptionIsSet(true);
    }
    
    public void unsetRevokeGrantOption() {
        this.__isset_bitfield = EncodingUtils.clearBit(this.__isset_bitfield, 0);
    }
    
    public boolean isSetRevokeGrantOption() {
        return EncodingUtils.testBit(this.__isset_bitfield, 0);
    }
    
    public void setRevokeGrantOptionIsSet(final boolean value) {
        this.__isset_bitfield = EncodingUtils.setBit(this.__isset_bitfield, 0, value);
    }
    
    @Override
    public void setFieldValue(final _Fields field, final Object value) {
        switch (field) {
            case REQUEST_TYPE: {
                if (value == null) {
                    this.unsetRequestType();
                    break;
                }
                this.setRequestType((GrantRevokeType)value);
                break;
            }
            case PRIVILEGES: {
                if (value == null) {
                    this.unsetPrivileges();
                    break;
                }
                this.setPrivileges((PrivilegeBag)value);
                break;
            }
            case REVOKE_GRANT_OPTION: {
                if (value == null) {
                    this.unsetRevokeGrantOption();
                    break;
                }
                this.setRevokeGrantOption((boolean)value);
                break;
            }
        }
    }
    
    @Override
    public Object getFieldValue(final _Fields field) {
        switch (field) {
            case REQUEST_TYPE: {
                return this.getRequestType();
            }
            case PRIVILEGES: {
                return this.getPrivileges();
            }
            case REVOKE_GRANT_OPTION: {
                return this.isRevokeGrantOption();
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
            case REQUEST_TYPE: {
                return this.isSetRequestType();
            }
            case PRIVILEGES: {
                return this.isSetPrivileges();
            }
            case REVOKE_GRANT_OPTION: {
                return this.isSetRevokeGrantOption();
            }
            default: {
                throw new IllegalStateException();
            }
        }
    }
    
    @Override
    public boolean equals(final Object that) {
        return that != null && that instanceof GrantRevokePrivilegeRequest && this.equals((GrantRevokePrivilegeRequest)that);
    }
    
    public boolean equals(final GrantRevokePrivilegeRequest that) {
        if (that == null) {
            return false;
        }
        final boolean this_present_requestType = this.isSetRequestType();
        final boolean that_present_requestType = that.isSetRequestType();
        if (this_present_requestType || that_present_requestType) {
            if (!this_present_requestType || !that_present_requestType) {
                return false;
            }
            if (!this.requestType.equals(that.requestType)) {
                return false;
            }
        }
        final boolean this_present_privileges = this.isSetPrivileges();
        final boolean that_present_privileges = that.isSetPrivileges();
        if (this_present_privileges || that_present_privileges) {
            if (!this_present_privileges || !that_present_privileges) {
                return false;
            }
            if (!this.privileges.equals(that.privileges)) {
                return false;
            }
        }
        final boolean this_present_revokeGrantOption = this.isSetRevokeGrantOption();
        final boolean that_present_revokeGrantOption = that.isSetRevokeGrantOption();
        if (this_present_revokeGrantOption || that_present_revokeGrantOption) {
            if (!this_present_revokeGrantOption || !that_present_revokeGrantOption) {
                return false;
            }
            if (this.revokeGrantOption != that.revokeGrantOption) {
                return false;
            }
        }
        return true;
    }
    
    @Override
    public int hashCode() {
        final HashCodeBuilder builder = new HashCodeBuilder();
        final boolean present_requestType = this.isSetRequestType();
        builder.append(present_requestType);
        if (present_requestType) {
            builder.append(this.requestType.getValue());
        }
        final boolean present_privileges = this.isSetPrivileges();
        builder.append(present_privileges);
        if (present_privileges) {
            builder.append(this.privileges);
        }
        final boolean present_revokeGrantOption = this.isSetRevokeGrantOption();
        builder.append(present_revokeGrantOption);
        if (present_revokeGrantOption) {
            builder.append(this.revokeGrantOption);
        }
        return builder.toHashCode();
    }
    
    @Override
    public int compareTo(final GrantRevokePrivilegeRequest other) {
        if (!this.getClass().equals(other.getClass())) {
            return this.getClass().getName().compareTo(other.getClass().getName());
        }
        int lastComparison = 0;
        final GrantRevokePrivilegeRequest typedOther = other;
        lastComparison = Boolean.valueOf(this.isSetRequestType()).compareTo(Boolean.valueOf(typedOther.isSetRequestType()));
        if (lastComparison != 0) {
            return lastComparison;
        }
        if (this.isSetRequestType()) {
            lastComparison = TBaseHelper.compareTo(this.requestType, typedOther.requestType);
            if (lastComparison != 0) {
                return lastComparison;
            }
        }
        lastComparison = Boolean.valueOf(this.isSetPrivileges()).compareTo(Boolean.valueOf(typedOther.isSetPrivileges()));
        if (lastComparison != 0) {
            return lastComparison;
        }
        if (this.isSetPrivileges()) {
            lastComparison = TBaseHelper.compareTo(this.privileges, typedOther.privileges);
            if (lastComparison != 0) {
                return lastComparison;
            }
        }
        lastComparison = Boolean.valueOf(this.isSetRevokeGrantOption()).compareTo(Boolean.valueOf(typedOther.isSetRevokeGrantOption()));
        if (lastComparison != 0) {
            return lastComparison;
        }
        if (this.isSetRevokeGrantOption()) {
            lastComparison = TBaseHelper.compareTo(this.revokeGrantOption, typedOther.revokeGrantOption);
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
        GrantRevokePrivilegeRequest.schemes.get(iprot.getScheme()).getScheme().read(iprot, this);
    }
    
    @Override
    public void write(final TProtocol oprot) throws TException {
        GrantRevokePrivilegeRequest.schemes.get(oprot.getScheme()).getScheme().write(oprot, this);
    }
    
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("GrantRevokePrivilegeRequest(");
        boolean first = true;
        sb.append("requestType:");
        if (this.requestType == null) {
            sb.append("null");
        }
        else {
            sb.append(this.requestType);
        }
        first = false;
        if (!first) {
            sb.append(", ");
        }
        sb.append("privileges:");
        if (this.privileges == null) {
            sb.append("null");
        }
        else {
            sb.append(this.privileges);
        }
        first = false;
        if (this.isSetRevokeGrantOption()) {
            if (!first) {
                sb.append(", ");
            }
            sb.append("revokeGrantOption:");
            sb.append(this.revokeGrantOption);
            first = false;
        }
        sb.append(")");
        return sb.toString();
    }
    
    public void validate() throws TException {
        if (this.privileges != null) {
            this.privileges.validate();
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
        STRUCT_DESC = new TStruct("GrantRevokePrivilegeRequest");
        REQUEST_TYPE_FIELD_DESC = new TField("requestType", (byte)8, (short)1);
        PRIVILEGES_FIELD_DESC = new TField("privileges", (byte)12, (short)2);
        REVOKE_GRANT_OPTION_FIELD_DESC = new TField("revokeGrantOption", (byte)2, (short)3);
        (schemes = new HashMap<Class<? extends IScheme>, SchemeFactory>()).put(StandardScheme.class, new GrantRevokePrivilegeRequestStandardSchemeFactory());
        GrantRevokePrivilegeRequest.schemes.put(TupleScheme.class, new GrantRevokePrivilegeRequestTupleSchemeFactory());
        final Map<_Fields, FieldMetaData> tmpMap = new EnumMap<_Fields, FieldMetaData>(_Fields.class);
        tmpMap.put(_Fields.REQUEST_TYPE, new FieldMetaData("requestType", (byte)3, new EnumMetaData((byte)16, GrantRevokeType.class)));
        tmpMap.put(_Fields.PRIVILEGES, new FieldMetaData("privileges", (byte)3, new StructMetaData((byte)12, PrivilegeBag.class)));
        tmpMap.put(_Fields.REVOKE_GRANT_OPTION, new FieldMetaData("revokeGrantOption", (byte)2, new FieldValueMetaData((byte)2)));
        FieldMetaData.addStructMetaDataMap(GrantRevokePrivilegeRequest.class, metaDataMap = Collections.unmodifiableMap((Map<? extends _Fields, ? extends FieldMetaData>)tmpMap));
    }
    
    public enum _Fields implements TFieldIdEnum
    {
        REQUEST_TYPE((short)1, "requestType"), 
        PRIVILEGES((short)2, "privileges"), 
        REVOKE_GRANT_OPTION((short)3, "revokeGrantOption");
        
        private static final Map<String, _Fields> byName;
        private final short _thriftId;
        private final String _fieldName;
        
        public static _Fields findByThriftId(final int fieldId) {
            switch (fieldId) {
                case 1: {
                    return _Fields.REQUEST_TYPE;
                }
                case 2: {
                    return _Fields.PRIVILEGES;
                }
                case 3: {
                    return _Fields.REVOKE_GRANT_OPTION;
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
    
    private static class GrantRevokePrivilegeRequestStandardSchemeFactory implements SchemeFactory
    {
        @Override
        public GrantRevokePrivilegeRequestStandardScheme getScheme() {
            return new GrantRevokePrivilegeRequestStandardScheme();
        }
    }
    
    private static class GrantRevokePrivilegeRequestStandardScheme extends StandardScheme<GrantRevokePrivilegeRequest>
    {
        @Override
        public void read(final TProtocol iprot, final GrantRevokePrivilegeRequest struct) throws TException {
            iprot.readStructBegin();
            while (true) {
                final TField schemeField = iprot.readFieldBegin();
                if (schemeField.type == 0) {
                    break;
                }
                switch (schemeField.id) {
                    case 1: {
                        if (schemeField.type == 8) {
                            struct.requestType = GrantRevokeType.findByValue(iprot.readI32());
                            struct.setRequestTypeIsSet(true);
                            break;
                        }
                        TProtocolUtil.skip(iprot, schemeField.type);
                        break;
                    }
                    case 2: {
                        if (schemeField.type == 12) {
                            struct.privileges = new PrivilegeBag();
                            struct.privileges.read(iprot);
                            struct.setPrivilegesIsSet(true);
                            break;
                        }
                        TProtocolUtil.skip(iprot, schemeField.type);
                        break;
                    }
                    case 3: {
                        if (schemeField.type == 2) {
                            struct.revokeGrantOption = iprot.readBool();
                            struct.setRevokeGrantOptionIsSet(true);
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
        public void write(final TProtocol oprot, final GrantRevokePrivilegeRequest struct) throws TException {
            struct.validate();
            oprot.writeStructBegin(GrantRevokePrivilegeRequest.STRUCT_DESC);
            if (struct.requestType != null) {
                oprot.writeFieldBegin(GrantRevokePrivilegeRequest.REQUEST_TYPE_FIELD_DESC);
                oprot.writeI32(struct.requestType.getValue());
                oprot.writeFieldEnd();
            }
            if (struct.privileges != null) {
                oprot.writeFieldBegin(GrantRevokePrivilegeRequest.PRIVILEGES_FIELD_DESC);
                struct.privileges.write(oprot);
                oprot.writeFieldEnd();
            }
            if (struct.isSetRevokeGrantOption()) {
                oprot.writeFieldBegin(GrantRevokePrivilegeRequest.REVOKE_GRANT_OPTION_FIELD_DESC);
                oprot.writeBool(struct.revokeGrantOption);
                oprot.writeFieldEnd();
            }
            oprot.writeFieldStop();
            oprot.writeStructEnd();
        }
    }
    
    private static class GrantRevokePrivilegeRequestTupleSchemeFactory implements SchemeFactory
    {
        @Override
        public GrantRevokePrivilegeRequestTupleScheme getScheme() {
            return new GrantRevokePrivilegeRequestTupleScheme();
        }
    }
    
    private static class GrantRevokePrivilegeRequestTupleScheme extends TupleScheme<GrantRevokePrivilegeRequest>
    {
        @Override
        public void write(final TProtocol prot, final GrantRevokePrivilegeRequest struct) throws TException {
            final TTupleProtocol oprot = (TTupleProtocol)prot;
            final BitSet optionals = new BitSet();
            if (struct.isSetRequestType()) {
                optionals.set(0);
            }
            if (struct.isSetPrivileges()) {
                optionals.set(1);
            }
            if (struct.isSetRevokeGrantOption()) {
                optionals.set(2);
            }
            oprot.writeBitSet(optionals, 3);
            if (struct.isSetRequestType()) {
                oprot.writeI32(struct.requestType.getValue());
            }
            if (struct.isSetPrivileges()) {
                struct.privileges.write(oprot);
            }
            if (struct.isSetRevokeGrantOption()) {
                oprot.writeBool(struct.revokeGrantOption);
            }
        }
        
        @Override
        public void read(final TProtocol prot, final GrantRevokePrivilegeRequest struct) throws TException {
            final TTupleProtocol iprot = (TTupleProtocol)prot;
            final BitSet incoming = iprot.readBitSet(3);
            if (incoming.get(0)) {
                struct.requestType = GrantRevokeType.findByValue(iprot.readI32());
                struct.setRequestTypeIsSet(true);
            }
            if (incoming.get(1)) {
                struct.privileges = new PrivilegeBag();
                struct.privileges.read(iprot);
                struct.setPrivilegesIsSet(true);
            }
            if (incoming.get(2)) {
                struct.revokeGrantOption = iprot.readBool();
                struct.setRevokeGrantOptionIsSet(true);
            }
        }
    }
}
