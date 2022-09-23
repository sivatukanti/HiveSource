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

public class Role implements TBase<Role, _Fields>, Serializable, Cloneable
{
    private static final TStruct STRUCT_DESC;
    private static final TField ROLE_NAME_FIELD_DESC;
    private static final TField CREATE_TIME_FIELD_DESC;
    private static final TField OWNER_NAME_FIELD_DESC;
    private static final Map<Class<? extends IScheme>, SchemeFactory> schemes;
    private String roleName;
    private int createTime;
    private String ownerName;
    private static final int __CREATETIME_ISSET_ID = 0;
    private byte __isset_bitfield;
    public static final Map<_Fields, FieldMetaData> metaDataMap;
    
    public Role() {
        this.__isset_bitfield = 0;
    }
    
    public Role(final String roleName, final int createTime, final String ownerName) {
        this();
        this.roleName = roleName;
        this.createTime = createTime;
        this.setCreateTimeIsSet(true);
        this.ownerName = ownerName;
    }
    
    public Role(final Role other) {
        this.__isset_bitfield = 0;
        this.__isset_bitfield = other.__isset_bitfield;
        if (other.isSetRoleName()) {
            this.roleName = other.roleName;
        }
        this.createTime = other.createTime;
        if (other.isSetOwnerName()) {
            this.ownerName = other.ownerName;
        }
    }
    
    @Override
    public Role deepCopy() {
        return new Role(this);
    }
    
    @Override
    public void clear() {
        this.roleName = null;
        this.setCreateTimeIsSet(false);
        this.createTime = 0;
        this.ownerName = null;
    }
    
    public String getRoleName() {
        return this.roleName;
    }
    
    public void setRoleName(final String roleName) {
        this.roleName = roleName;
    }
    
    public void unsetRoleName() {
        this.roleName = null;
    }
    
    public boolean isSetRoleName() {
        return this.roleName != null;
    }
    
    public void setRoleNameIsSet(final boolean value) {
        if (!value) {
            this.roleName = null;
        }
    }
    
    public int getCreateTime() {
        return this.createTime;
    }
    
    public void setCreateTime(final int createTime) {
        this.createTime = createTime;
        this.setCreateTimeIsSet(true);
    }
    
    public void unsetCreateTime() {
        this.__isset_bitfield = EncodingUtils.clearBit(this.__isset_bitfield, 0);
    }
    
    public boolean isSetCreateTime() {
        return EncodingUtils.testBit(this.__isset_bitfield, 0);
    }
    
    public void setCreateTimeIsSet(final boolean value) {
        this.__isset_bitfield = EncodingUtils.setBit(this.__isset_bitfield, 0, value);
    }
    
    public String getOwnerName() {
        return this.ownerName;
    }
    
    public void setOwnerName(final String ownerName) {
        this.ownerName = ownerName;
    }
    
    public void unsetOwnerName() {
        this.ownerName = null;
    }
    
    public boolean isSetOwnerName() {
        return this.ownerName != null;
    }
    
    public void setOwnerNameIsSet(final boolean value) {
        if (!value) {
            this.ownerName = null;
        }
    }
    
    @Override
    public void setFieldValue(final _Fields field, final Object value) {
        switch (field) {
            case ROLE_NAME: {
                if (value == null) {
                    this.unsetRoleName();
                    break;
                }
                this.setRoleName((String)value);
                break;
            }
            case CREATE_TIME: {
                if (value == null) {
                    this.unsetCreateTime();
                    break;
                }
                this.setCreateTime((int)value);
                break;
            }
            case OWNER_NAME: {
                if (value == null) {
                    this.unsetOwnerName();
                    break;
                }
                this.setOwnerName((String)value);
                break;
            }
        }
    }
    
    @Override
    public Object getFieldValue(final _Fields field) {
        switch (field) {
            case ROLE_NAME: {
                return this.getRoleName();
            }
            case CREATE_TIME: {
                return this.getCreateTime();
            }
            case OWNER_NAME: {
                return this.getOwnerName();
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
            case ROLE_NAME: {
                return this.isSetRoleName();
            }
            case CREATE_TIME: {
                return this.isSetCreateTime();
            }
            case OWNER_NAME: {
                return this.isSetOwnerName();
            }
            default: {
                throw new IllegalStateException();
            }
        }
    }
    
    @Override
    public boolean equals(final Object that) {
        return that != null && that instanceof Role && this.equals((Role)that);
    }
    
    public boolean equals(final Role that) {
        if (that == null) {
            return false;
        }
        final boolean this_present_roleName = this.isSetRoleName();
        final boolean that_present_roleName = that.isSetRoleName();
        if (this_present_roleName || that_present_roleName) {
            if (!this_present_roleName || !that_present_roleName) {
                return false;
            }
            if (!this.roleName.equals(that.roleName)) {
                return false;
            }
        }
        final boolean this_present_createTime = true;
        final boolean that_present_createTime = true;
        if (this_present_createTime || that_present_createTime) {
            if (!this_present_createTime || !that_present_createTime) {
                return false;
            }
            if (this.createTime != that.createTime) {
                return false;
            }
        }
        final boolean this_present_ownerName = this.isSetOwnerName();
        final boolean that_present_ownerName = that.isSetOwnerName();
        if (this_present_ownerName || that_present_ownerName) {
            if (!this_present_ownerName || !that_present_ownerName) {
                return false;
            }
            if (!this.ownerName.equals(that.ownerName)) {
                return false;
            }
        }
        return true;
    }
    
    @Override
    public int hashCode() {
        final HashCodeBuilder builder = new HashCodeBuilder();
        final boolean present_roleName = this.isSetRoleName();
        builder.append(present_roleName);
        if (present_roleName) {
            builder.append(this.roleName);
        }
        final boolean present_createTime = true;
        builder.append(present_createTime);
        if (present_createTime) {
            builder.append(this.createTime);
        }
        final boolean present_ownerName = this.isSetOwnerName();
        builder.append(present_ownerName);
        if (present_ownerName) {
            builder.append(this.ownerName);
        }
        return builder.toHashCode();
    }
    
    @Override
    public int compareTo(final Role other) {
        if (!this.getClass().equals(other.getClass())) {
            return this.getClass().getName().compareTo(other.getClass().getName());
        }
        int lastComparison = 0;
        final Role typedOther = other;
        lastComparison = Boolean.valueOf(this.isSetRoleName()).compareTo(Boolean.valueOf(typedOther.isSetRoleName()));
        if (lastComparison != 0) {
            return lastComparison;
        }
        if (this.isSetRoleName()) {
            lastComparison = TBaseHelper.compareTo(this.roleName, typedOther.roleName);
            if (lastComparison != 0) {
                return lastComparison;
            }
        }
        lastComparison = Boolean.valueOf(this.isSetCreateTime()).compareTo(Boolean.valueOf(typedOther.isSetCreateTime()));
        if (lastComparison != 0) {
            return lastComparison;
        }
        if (this.isSetCreateTime()) {
            lastComparison = TBaseHelper.compareTo(this.createTime, typedOther.createTime);
            if (lastComparison != 0) {
                return lastComparison;
            }
        }
        lastComparison = Boolean.valueOf(this.isSetOwnerName()).compareTo(Boolean.valueOf(typedOther.isSetOwnerName()));
        if (lastComparison != 0) {
            return lastComparison;
        }
        if (this.isSetOwnerName()) {
            lastComparison = TBaseHelper.compareTo(this.ownerName, typedOther.ownerName);
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
        Role.schemes.get(iprot.getScheme()).getScheme().read(iprot, this);
    }
    
    @Override
    public void write(final TProtocol oprot) throws TException {
        Role.schemes.get(oprot.getScheme()).getScheme().write(oprot, this);
    }
    
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("Role(");
        boolean first = true;
        sb.append("roleName:");
        if (this.roleName == null) {
            sb.append("null");
        }
        else {
            sb.append(this.roleName);
        }
        first = false;
        if (!first) {
            sb.append(", ");
        }
        sb.append("createTime:");
        sb.append(this.createTime);
        first = false;
        if (!first) {
            sb.append(", ");
        }
        sb.append("ownerName:");
        if (this.ownerName == null) {
            sb.append("null");
        }
        else {
            sb.append(this.ownerName);
        }
        first = false;
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
        STRUCT_DESC = new TStruct("Role");
        ROLE_NAME_FIELD_DESC = new TField("roleName", (byte)11, (short)1);
        CREATE_TIME_FIELD_DESC = new TField("createTime", (byte)8, (short)2);
        OWNER_NAME_FIELD_DESC = new TField("ownerName", (byte)11, (short)3);
        (schemes = new HashMap<Class<? extends IScheme>, SchemeFactory>()).put(StandardScheme.class, new RoleStandardSchemeFactory());
        Role.schemes.put(TupleScheme.class, new RoleTupleSchemeFactory());
        final Map<_Fields, FieldMetaData> tmpMap = new EnumMap<_Fields, FieldMetaData>(_Fields.class);
        tmpMap.put(_Fields.ROLE_NAME, new FieldMetaData("roleName", (byte)3, new FieldValueMetaData((byte)11)));
        tmpMap.put(_Fields.CREATE_TIME, new FieldMetaData("createTime", (byte)3, new FieldValueMetaData((byte)8)));
        tmpMap.put(_Fields.OWNER_NAME, new FieldMetaData("ownerName", (byte)3, new FieldValueMetaData((byte)11)));
        FieldMetaData.addStructMetaDataMap(Role.class, metaDataMap = Collections.unmodifiableMap((Map<? extends _Fields, ? extends FieldMetaData>)tmpMap));
    }
    
    public enum _Fields implements TFieldIdEnum
    {
        ROLE_NAME((short)1, "roleName"), 
        CREATE_TIME((short)2, "createTime"), 
        OWNER_NAME((short)3, "ownerName");
        
        private static final Map<String, _Fields> byName;
        private final short _thriftId;
        private final String _fieldName;
        
        public static _Fields findByThriftId(final int fieldId) {
            switch (fieldId) {
                case 1: {
                    return _Fields.ROLE_NAME;
                }
                case 2: {
                    return _Fields.CREATE_TIME;
                }
                case 3: {
                    return _Fields.OWNER_NAME;
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
    
    private static class RoleStandardSchemeFactory implements SchemeFactory
    {
        @Override
        public RoleStandardScheme getScheme() {
            return new RoleStandardScheme();
        }
    }
    
    private static class RoleStandardScheme extends StandardScheme<Role>
    {
        @Override
        public void read(final TProtocol iprot, final Role struct) throws TException {
            iprot.readStructBegin();
            while (true) {
                final TField schemeField = iprot.readFieldBegin();
                if (schemeField.type == 0) {
                    break;
                }
                switch (schemeField.id) {
                    case 1: {
                        if (schemeField.type == 11) {
                            struct.roleName = iprot.readString();
                            struct.setRoleNameIsSet(true);
                            break;
                        }
                        TProtocolUtil.skip(iprot, schemeField.type);
                        break;
                    }
                    case 2: {
                        if (schemeField.type == 8) {
                            struct.createTime = iprot.readI32();
                            struct.setCreateTimeIsSet(true);
                            break;
                        }
                        TProtocolUtil.skip(iprot, schemeField.type);
                        break;
                    }
                    case 3: {
                        if (schemeField.type == 11) {
                            struct.ownerName = iprot.readString();
                            struct.setOwnerNameIsSet(true);
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
        public void write(final TProtocol oprot, final Role struct) throws TException {
            struct.validate();
            oprot.writeStructBegin(Role.STRUCT_DESC);
            if (struct.roleName != null) {
                oprot.writeFieldBegin(Role.ROLE_NAME_FIELD_DESC);
                oprot.writeString(struct.roleName);
                oprot.writeFieldEnd();
            }
            oprot.writeFieldBegin(Role.CREATE_TIME_FIELD_DESC);
            oprot.writeI32(struct.createTime);
            oprot.writeFieldEnd();
            if (struct.ownerName != null) {
                oprot.writeFieldBegin(Role.OWNER_NAME_FIELD_DESC);
                oprot.writeString(struct.ownerName);
                oprot.writeFieldEnd();
            }
            oprot.writeFieldStop();
            oprot.writeStructEnd();
        }
    }
    
    private static class RoleTupleSchemeFactory implements SchemeFactory
    {
        @Override
        public RoleTupleScheme getScheme() {
            return new RoleTupleScheme();
        }
    }
    
    private static class RoleTupleScheme extends TupleScheme<Role>
    {
        @Override
        public void write(final TProtocol prot, final Role struct) throws TException {
            final TTupleProtocol oprot = (TTupleProtocol)prot;
            final BitSet optionals = new BitSet();
            if (struct.isSetRoleName()) {
                optionals.set(0);
            }
            if (struct.isSetCreateTime()) {
                optionals.set(1);
            }
            if (struct.isSetOwnerName()) {
                optionals.set(2);
            }
            oprot.writeBitSet(optionals, 3);
            if (struct.isSetRoleName()) {
                oprot.writeString(struct.roleName);
            }
            if (struct.isSetCreateTime()) {
                oprot.writeI32(struct.createTime);
            }
            if (struct.isSetOwnerName()) {
                oprot.writeString(struct.ownerName);
            }
        }
        
        @Override
        public void read(final TProtocol prot, final Role struct) throws TException {
            final TTupleProtocol iprot = (TTupleProtocol)prot;
            final BitSet incoming = iprot.readBitSet(3);
            if (incoming.get(0)) {
                struct.roleName = iprot.readString();
                struct.setRoleNameIsSet(true);
            }
            if (incoming.get(1)) {
                struct.createTime = iprot.readI32();
                struct.setCreateTimeIsSet(true);
            }
            if (incoming.get(2)) {
                struct.ownerName = iprot.readString();
                struct.setOwnerNameIsSet(true);
            }
        }
    }
}
