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

public class PrivilegeGrantInfo implements TBase<PrivilegeGrantInfo, _Fields>, Serializable, Cloneable
{
    private static final TStruct STRUCT_DESC;
    private static final TField PRIVILEGE_FIELD_DESC;
    private static final TField CREATE_TIME_FIELD_DESC;
    private static final TField GRANTOR_FIELD_DESC;
    private static final TField GRANTOR_TYPE_FIELD_DESC;
    private static final TField GRANT_OPTION_FIELD_DESC;
    private static final Map<Class<? extends IScheme>, SchemeFactory> schemes;
    private String privilege;
    private int createTime;
    private String grantor;
    private PrincipalType grantorType;
    private boolean grantOption;
    private static final int __CREATETIME_ISSET_ID = 0;
    private static final int __GRANTOPTION_ISSET_ID = 1;
    private byte __isset_bitfield;
    public static final Map<_Fields, FieldMetaData> metaDataMap;
    
    public PrivilegeGrantInfo() {
        this.__isset_bitfield = 0;
    }
    
    public PrivilegeGrantInfo(final String privilege, final int createTime, final String grantor, final PrincipalType grantorType, final boolean grantOption) {
        this();
        this.privilege = privilege;
        this.createTime = createTime;
        this.setCreateTimeIsSet(true);
        this.grantor = grantor;
        this.grantorType = grantorType;
        this.grantOption = grantOption;
        this.setGrantOptionIsSet(true);
    }
    
    public PrivilegeGrantInfo(final PrivilegeGrantInfo other) {
        this.__isset_bitfield = 0;
        this.__isset_bitfield = other.__isset_bitfield;
        if (other.isSetPrivilege()) {
            this.privilege = other.privilege;
        }
        this.createTime = other.createTime;
        if (other.isSetGrantor()) {
            this.grantor = other.grantor;
        }
        if (other.isSetGrantorType()) {
            this.grantorType = other.grantorType;
        }
        this.grantOption = other.grantOption;
    }
    
    @Override
    public PrivilegeGrantInfo deepCopy() {
        return new PrivilegeGrantInfo(this);
    }
    
    @Override
    public void clear() {
        this.privilege = null;
        this.setCreateTimeIsSet(false);
        this.createTime = 0;
        this.grantor = null;
        this.grantorType = null;
        this.setGrantOptionIsSet(false);
        this.grantOption = false;
    }
    
    public String getPrivilege() {
        return this.privilege;
    }
    
    public void setPrivilege(final String privilege) {
        this.privilege = privilege;
    }
    
    public void unsetPrivilege() {
        this.privilege = null;
    }
    
    public boolean isSetPrivilege() {
        return this.privilege != null;
    }
    
    public void setPrivilegeIsSet(final boolean value) {
        if (!value) {
            this.privilege = null;
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
    
    public String getGrantor() {
        return this.grantor;
    }
    
    public void setGrantor(final String grantor) {
        this.grantor = grantor;
    }
    
    public void unsetGrantor() {
        this.grantor = null;
    }
    
    public boolean isSetGrantor() {
        return this.grantor != null;
    }
    
    public void setGrantorIsSet(final boolean value) {
        if (!value) {
            this.grantor = null;
        }
    }
    
    public PrincipalType getGrantorType() {
        return this.grantorType;
    }
    
    public void setGrantorType(final PrincipalType grantorType) {
        this.grantorType = grantorType;
    }
    
    public void unsetGrantorType() {
        this.grantorType = null;
    }
    
    public boolean isSetGrantorType() {
        return this.grantorType != null;
    }
    
    public void setGrantorTypeIsSet(final boolean value) {
        if (!value) {
            this.grantorType = null;
        }
    }
    
    public boolean isGrantOption() {
        return this.grantOption;
    }
    
    public void setGrantOption(final boolean grantOption) {
        this.grantOption = grantOption;
        this.setGrantOptionIsSet(true);
    }
    
    public void unsetGrantOption() {
        this.__isset_bitfield = EncodingUtils.clearBit(this.__isset_bitfield, 1);
    }
    
    public boolean isSetGrantOption() {
        return EncodingUtils.testBit(this.__isset_bitfield, 1);
    }
    
    public void setGrantOptionIsSet(final boolean value) {
        this.__isset_bitfield = EncodingUtils.setBit(this.__isset_bitfield, 1, value);
    }
    
    @Override
    public void setFieldValue(final _Fields field, final Object value) {
        switch (field) {
            case PRIVILEGE: {
                if (value == null) {
                    this.unsetPrivilege();
                    break;
                }
                this.setPrivilege((String)value);
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
            case GRANTOR: {
                if (value == null) {
                    this.unsetGrantor();
                    break;
                }
                this.setGrantor((String)value);
                break;
            }
            case GRANTOR_TYPE: {
                if (value == null) {
                    this.unsetGrantorType();
                    break;
                }
                this.setGrantorType((PrincipalType)value);
                break;
            }
            case GRANT_OPTION: {
                if (value == null) {
                    this.unsetGrantOption();
                    break;
                }
                this.setGrantOption((boolean)value);
                break;
            }
        }
    }
    
    @Override
    public Object getFieldValue(final _Fields field) {
        switch (field) {
            case PRIVILEGE: {
                return this.getPrivilege();
            }
            case CREATE_TIME: {
                return this.getCreateTime();
            }
            case GRANTOR: {
                return this.getGrantor();
            }
            case GRANTOR_TYPE: {
                return this.getGrantorType();
            }
            case GRANT_OPTION: {
                return this.isGrantOption();
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
            case PRIVILEGE: {
                return this.isSetPrivilege();
            }
            case CREATE_TIME: {
                return this.isSetCreateTime();
            }
            case GRANTOR: {
                return this.isSetGrantor();
            }
            case GRANTOR_TYPE: {
                return this.isSetGrantorType();
            }
            case GRANT_OPTION: {
                return this.isSetGrantOption();
            }
            default: {
                throw new IllegalStateException();
            }
        }
    }
    
    @Override
    public boolean equals(final Object that) {
        return that != null && that instanceof PrivilegeGrantInfo && this.equals((PrivilegeGrantInfo)that);
    }
    
    public boolean equals(final PrivilegeGrantInfo that) {
        if (that == null) {
            return false;
        }
        final boolean this_present_privilege = this.isSetPrivilege();
        final boolean that_present_privilege = that.isSetPrivilege();
        if (this_present_privilege || that_present_privilege) {
            if (!this_present_privilege || !that_present_privilege) {
                return false;
            }
            if (!this.privilege.equals(that.privilege)) {
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
        final boolean this_present_grantor = this.isSetGrantor();
        final boolean that_present_grantor = that.isSetGrantor();
        if (this_present_grantor || that_present_grantor) {
            if (!this_present_grantor || !that_present_grantor) {
                return false;
            }
            if (!this.grantor.equals(that.grantor)) {
                return false;
            }
        }
        final boolean this_present_grantorType = this.isSetGrantorType();
        final boolean that_present_grantorType = that.isSetGrantorType();
        if (this_present_grantorType || that_present_grantorType) {
            if (!this_present_grantorType || !that_present_grantorType) {
                return false;
            }
            if (!this.grantorType.equals(that.grantorType)) {
                return false;
            }
        }
        final boolean this_present_grantOption = true;
        final boolean that_present_grantOption = true;
        if (this_present_grantOption || that_present_grantOption) {
            if (!this_present_grantOption || !that_present_grantOption) {
                return false;
            }
            if (this.grantOption != that.grantOption) {
                return false;
            }
        }
        return true;
    }
    
    @Override
    public int hashCode() {
        final HashCodeBuilder builder = new HashCodeBuilder();
        final boolean present_privilege = this.isSetPrivilege();
        builder.append(present_privilege);
        if (present_privilege) {
            builder.append(this.privilege);
        }
        final boolean present_createTime = true;
        builder.append(present_createTime);
        if (present_createTime) {
            builder.append(this.createTime);
        }
        final boolean present_grantor = this.isSetGrantor();
        builder.append(present_grantor);
        if (present_grantor) {
            builder.append(this.grantor);
        }
        final boolean present_grantorType = this.isSetGrantorType();
        builder.append(present_grantorType);
        if (present_grantorType) {
            builder.append(this.grantorType.getValue());
        }
        final boolean present_grantOption = true;
        builder.append(present_grantOption);
        if (present_grantOption) {
            builder.append(this.grantOption);
        }
        return builder.toHashCode();
    }
    
    @Override
    public int compareTo(final PrivilegeGrantInfo other) {
        if (!this.getClass().equals(other.getClass())) {
            return this.getClass().getName().compareTo(other.getClass().getName());
        }
        int lastComparison = 0;
        final PrivilegeGrantInfo typedOther = other;
        lastComparison = Boolean.valueOf(this.isSetPrivilege()).compareTo(Boolean.valueOf(typedOther.isSetPrivilege()));
        if (lastComparison != 0) {
            return lastComparison;
        }
        if (this.isSetPrivilege()) {
            lastComparison = TBaseHelper.compareTo(this.privilege, typedOther.privilege);
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
        lastComparison = Boolean.valueOf(this.isSetGrantor()).compareTo(Boolean.valueOf(typedOther.isSetGrantor()));
        if (lastComparison != 0) {
            return lastComparison;
        }
        if (this.isSetGrantor()) {
            lastComparison = TBaseHelper.compareTo(this.grantor, typedOther.grantor);
            if (lastComparison != 0) {
                return lastComparison;
            }
        }
        lastComparison = Boolean.valueOf(this.isSetGrantorType()).compareTo(Boolean.valueOf(typedOther.isSetGrantorType()));
        if (lastComparison != 0) {
            return lastComparison;
        }
        if (this.isSetGrantorType()) {
            lastComparison = TBaseHelper.compareTo(this.grantorType, typedOther.grantorType);
            if (lastComparison != 0) {
                return lastComparison;
            }
        }
        lastComparison = Boolean.valueOf(this.isSetGrantOption()).compareTo(Boolean.valueOf(typedOther.isSetGrantOption()));
        if (lastComparison != 0) {
            return lastComparison;
        }
        if (this.isSetGrantOption()) {
            lastComparison = TBaseHelper.compareTo(this.grantOption, typedOther.grantOption);
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
        PrivilegeGrantInfo.schemes.get(iprot.getScheme()).getScheme().read(iprot, this);
    }
    
    @Override
    public void write(final TProtocol oprot) throws TException {
        PrivilegeGrantInfo.schemes.get(oprot.getScheme()).getScheme().write(oprot, this);
    }
    
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("PrivilegeGrantInfo(");
        boolean first = true;
        sb.append("privilege:");
        if (this.privilege == null) {
            sb.append("null");
        }
        else {
            sb.append(this.privilege);
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
        sb.append("grantor:");
        if (this.grantor == null) {
            sb.append("null");
        }
        else {
            sb.append(this.grantor);
        }
        first = false;
        if (!first) {
            sb.append(", ");
        }
        sb.append("grantorType:");
        if (this.grantorType == null) {
            sb.append("null");
        }
        else {
            sb.append(this.grantorType);
        }
        first = false;
        if (!first) {
            sb.append(", ");
        }
        sb.append("grantOption:");
        sb.append(this.grantOption);
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
        STRUCT_DESC = new TStruct("PrivilegeGrantInfo");
        PRIVILEGE_FIELD_DESC = new TField("privilege", (byte)11, (short)1);
        CREATE_TIME_FIELD_DESC = new TField("createTime", (byte)8, (short)2);
        GRANTOR_FIELD_DESC = new TField("grantor", (byte)11, (short)3);
        GRANTOR_TYPE_FIELD_DESC = new TField("grantorType", (byte)8, (short)4);
        GRANT_OPTION_FIELD_DESC = new TField("grantOption", (byte)2, (short)5);
        (schemes = new HashMap<Class<? extends IScheme>, SchemeFactory>()).put(StandardScheme.class, new PrivilegeGrantInfoStandardSchemeFactory());
        PrivilegeGrantInfo.schemes.put(TupleScheme.class, new PrivilegeGrantInfoTupleSchemeFactory());
        final Map<_Fields, FieldMetaData> tmpMap = new EnumMap<_Fields, FieldMetaData>(_Fields.class);
        tmpMap.put(_Fields.PRIVILEGE, new FieldMetaData("privilege", (byte)3, new FieldValueMetaData((byte)11)));
        tmpMap.put(_Fields.CREATE_TIME, new FieldMetaData("createTime", (byte)3, new FieldValueMetaData((byte)8)));
        tmpMap.put(_Fields.GRANTOR, new FieldMetaData("grantor", (byte)3, new FieldValueMetaData((byte)11)));
        tmpMap.put(_Fields.GRANTOR_TYPE, new FieldMetaData("grantorType", (byte)3, new EnumMetaData((byte)16, PrincipalType.class)));
        tmpMap.put(_Fields.GRANT_OPTION, new FieldMetaData("grantOption", (byte)3, new FieldValueMetaData((byte)2)));
        FieldMetaData.addStructMetaDataMap(PrivilegeGrantInfo.class, metaDataMap = Collections.unmodifiableMap((Map<? extends _Fields, ? extends FieldMetaData>)tmpMap));
    }
    
    public enum _Fields implements TFieldIdEnum
    {
        PRIVILEGE((short)1, "privilege"), 
        CREATE_TIME((short)2, "createTime"), 
        GRANTOR((short)3, "grantor"), 
        GRANTOR_TYPE((short)4, "grantorType"), 
        GRANT_OPTION((short)5, "grantOption");
        
        private static final Map<String, _Fields> byName;
        private final short _thriftId;
        private final String _fieldName;
        
        public static _Fields findByThriftId(final int fieldId) {
            switch (fieldId) {
                case 1: {
                    return _Fields.PRIVILEGE;
                }
                case 2: {
                    return _Fields.CREATE_TIME;
                }
                case 3: {
                    return _Fields.GRANTOR;
                }
                case 4: {
                    return _Fields.GRANTOR_TYPE;
                }
                case 5: {
                    return _Fields.GRANT_OPTION;
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
    
    private static class PrivilegeGrantInfoStandardSchemeFactory implements SchemeFactory
    {
        @Override
        public PrivilegeGrantInfoStandardScheme getScheme() {
            return new PrivilegeGrantInfoStandardScheme();
        }
    }
    
    private static class PrivilegeGrantInfoStandardScheme extends StandardScheme<PrivilegeGrantInfo>
    {
        @Override
        public void read(final TProtocol iprot, final PrivilegeGrantInfo struct) throws TException {
            iprot.readStructBegin();
            while (true) {
                final TField schemeField = iprot.readFieldBegin();
                if (schemeField.type == 0) {
                    break;
                }
                switch (schemeField.id) {
                    case 1: {
                        if (schemeField.type == 11) {
                            struct.privilege = iprot.readString();
                            struct.setPrivilegeIsSet(true);
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
                            struct.grantor = iprot.readString();
                            struct.setGrantorIsSet(true);
                            break;
                        }
                        TProtocolUtil.skip(iprot, schemeField.type);
                        break;
                    }
                    case 4: {
                        if (schemeField.type == 8) {
                            struct.grantorType = PrincipalType.findByValue(iprot.readI32());
                            struct.setGrantorTypeIsSet(true);
                            break;
                        }
                        TProtocolUtil.skip(iprot, schemeField.type);
                        break;
                    }
                    case 5: {
                        if (schemeField.type == 2) {
                            struct.grantOption = iprot.readBool();
                            struct.setGrantOptionIsSet(true);
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
        public void write(final TProtocol oprot, final PrivilegeGrantInfo struct) throws TException {
            struct.validate();
            oprot.writeStructBegin(PrivilegeGrantInfo.STRUCT_DESC);
            if (struct.privilege != null) {
                oprot.writeFieldBegin(PrivilegeGrantInfo.PRIVILEGE_FIELD_DESC);
                oprot.writeString(struct.privilege);
                oprot.writeFieldEnd();
            }
            oprot.writeFieldBegin(PrivilegeGrantInfo.CREATE_TIME_FIELD_DESC);
            oprot.writeI32(struct.createTime);
            oprot.writeFieldEnd();
            if (struct.grantor != null) {
                oprot.writeFieldBegin(PrivilegeGrantInfo.GRANTOR_FIELD_DESC);
                oprot.writeString(struct.grantor);
                oprot.writeFieldEnd();
            }
            if (struct.grantorType != null) {
                oprot.writeFieldBegin(PrivilegeGrantInfo.GRANTOR_TYPE_FIELD_DESC);
                oprot.writeI32(struct.grantorType.getValue());
                oprot.writeFieldEnd();
            }
            oprot.writeFieldBegin(PrivilegeGrantInfo.GRANT_OPTION_FIELD_DESC);
            oprot.writeBool(struct.grantOption);
            oprot.writeFieldEnd();
            oprot.writeFieldStop();
            oprot.writeStructEnd();
        }
    }
    
    private static class PrivilegeGrantInfoTupleSchemeFactory implements SchemeFactory
    {
        @Override
        public PrivilegeGrantInfoTupleScheme getScheme() {
            return new PrivilegeGrantInfoTupleScheme();
        }
    }
    
    private static class PrivilegeGrantInfoTupleScheme extends TupleScheme<PrivilegeGrantInfo>
    {
        @Override
        public void write(final TProtocol prot, final PrivilegeGrantInfo struct) throws TException {
            final TTupleProtocol oprot = (TTupleProtocol)prot;
            final BitSet optionals = new BitSet();
            if (struct.isSetPrivilege()) {
                optionals.set(0);
            }
            if (struct.isSetCreateTime()) {
                optionals.set(1);
            }
            if (struct.isSetGrantor()) {
                optionals.set(2);
            }
            if (struct.isSetGrantorType()) {
                optionals.set(3);
            }
            if (struct.isSetGrantOption()) {
                optionals.set(4);
            }
            oprot.writeBitSet(optionals, 5);
            if (struct.isSetPrivilege()) {
                oprot.writeString(struct.privilege);
            }
            if (struct.isSetCreateTime()) {
                oprot.writeI32(struct.createTime);
            }
            if (struct.isSetGrantor()) {
                oprot.writeString(struct.grantor);
            }
            if (struct.isSetGrantorType()) {
                oprot.writeI32(struct.grantorType.getValue());
            }
            if (struct.isSetGrantOption()) {
                oprot.writeBool(struct.grantOption);
            }
        }
        
        @Override
        public void read(final TProtocol prot, final PrivilegeGrantInfo struct) throws TException {
            final TTupleProtocol iprot = (TTupleProtocol)prot;
            final BitSet incoming = iprot.readBitSet(5);
            if (incoming.get(0)) {
                struct.privilege = iprot.readString();
                struct.setPrivilegeIsSet(true);
            }
            if (incoming.get(1)) {
                struct.createTime = iprot.readI32();
                struct.setCreateTimeIsSet(true);
            }
            if (incoming.get(2)) {
                struct.grantor = iprot.readString();
                struct.setGrantorIsSet(true);
            }
            if (incoming.get(3)) {
                struct.grantorType = PrincipalType.findByValue(iprot.readI32());
                struct.setGrantorTypeIsSet(true);
            }
            if (incoming.get(4)) {
                struct.grantOption = iprot.readBool();
                struct.setGrantOptionIsSet(true);
            }
        }
    }
}
