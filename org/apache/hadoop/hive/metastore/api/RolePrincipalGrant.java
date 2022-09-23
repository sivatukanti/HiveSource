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

public class RolePrincipalGrant implements TBase<RolePrincipalGrant, _Fields>, Serializable, Cloneable
{
    private static final TStruct STRUCT_DESC;
    private static final TField ROLE_NAME_FIELD_DESC;
    private static final TField PRINCIPAL_NAME_FIELD_DESC;
    private static final TField PRINCIPAL_TYPE_FIELD_DESC;
    private static final TField GRANT_OPTION_FIELD_DESC;
    private static final TField GRANT_TIME_FIELD_DESC;
    private static final TField GRANTOR_NAME_FIELD_DESC;
    private static final TField GRANTOR_PRINCIPAL_TYPE_FIELD_DESC;
    private static final Map<Class<? extends IScheme>, SchemeFactory> schemes;
    private String roleName;
    private String principalName;
    private PrincipalType principalType;
    private boolean grantOption;
    private int grantTime;
    private String grantorName;
    private PrincipalType grantorPrincipalType;
    private static final int __GRANTOPTION_ISSET_ID = 0;
    private static final int __GRANTTIME_ISSET_ID = 1;
    private byte __isset_bitfield;
    public static final Map<_Fields, FieldMetaData> metaDataMap;
    
    public RolePrincipalGrant() {
        this.__isset_bitfield = 0;
    }
    
    public RolePrincipalGrant(final String roleName, final String principalName, final PrincipalType principalType, final boolean grantOption, final int grantTime, final String grantorName, final PrincipalType grantorPrincipalType) {
        this();
        this.roleName = roleName;
        this.principalName = principalName;
        this.principalType = principalType;
        this.grantOption = grantOption;
        this.setGrantOptionIsSet(true);
        this.grantTime = grantTime;
        this.setGrantTimeIsSet(true);
        this.grantorName = grantorName;
        this.grantorPrincipalType = grantorPrincipalType;
    }
    
    public RolePrincipalGrant(final RolePrincipalGrant other) {
        this.__isset_bitfield = 0;
        this.__isset_bitfield = other.__isset_bitfield;
        if (other.isSetRoleName()) {
            this.roleName = other.roleName;
        }
        if (other.isSetPrincipalName()) {
            this.principalName = other.principalName;
        }
        if (other.isSetPrincipalType()) {
            this.principalType = other.principalType;
        }
        this.grantOption = other.grantOption;
        this.grantTime = other.grantTime;
        if (other.isSetGrantorName()) {
            this.grantorName = other.grantorName;
        }
        if (other.isSetGrantorPrincipalType()) {
            this.grantorPrincipalType = other.grantorPrincipalType;
        }
    }
    
    @Override
    public RolePrincipalGrant deepCopy() {
        return new RolePrincipalGrant(this);
    }
    
    @Override
    public void clear() {
        this.roleName = null;
        this.principalName = null;
        this.principalType = null;
        this.setGrantOptionIsSet(false);
        this.setGrantTimeIsSet(this.grantOption = false);
        this.grantTime = 0;
        this.grantorName = null;
        this.grantorPrincipalType = null;
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
    
    public String getPrincipalName() {
        return this.principalName;
    }
    
    public void setPrincipalName(final String principalName) {
        this.principalName = principalName;
    }
    
    public void unsetPrincipalName() {
        this.principalName = null;
    }
    
    public boolean isSetPrincipalName() {
        return this.principalName != null;
    }
    
    public void setPrincipalNameIsSet(final boolean value) {
        if (!value) {
            this.principalName = null;
        }
    }
    
    public PrincipalType getPrincipalType() {
        return this.principalType;
    }
    
    public void setPrincipalType(final PrincipalType principalType) {
        this.principalType = principalType;
    }
    
    public void unsetPrincipalType() {
        this.principalType = null;
    }
    
    public boolean isSetPrincipalType() {
        return this.principalType != null;
    }
    
    public void setPrincipalTypeIsSet(final boolean value) {
        if (!value) {
            this.principalType = null;
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
        this.__isset_bitfield = EncodingUtils.clearBit(this.__isset_bitfield, 0);
    }
    
    public boolean isSetGrantOption() {
        return EncodingUtils.testBit(this.__isset_bitfield, 0);
    }
    
    public void setGrantOptionIsSet(final boolean value) {
        this.__isset_bitfield = EncodingUtils.setBit(this.__isset_bitfield, 0, value);
    }
    
    public int getGrantTime() {
        return this.grantTime;
    }
    
    public void setGrantTime(final int grantTime) {
        this.grantTime = grantTime;
        this.setGrantTimeIsSet(true);
    }
    
    public void unsetGrantTime() {
        this.__isset_bitfield = EncodingUtils.clearBit(this.__isset_bitfield, 1);
    }
    
    public boolean isSetGrantTime() {
        return EncodingUtils.testBit(this.__isset_bitfield, 1);
    }
    
    public void setGrantTimeIsSet(final boolean value) {
        this.__isset_bitfield = EncodingUtils.setBit(this.__isset_bitfield, 1, value);
    }
    
    public String getGrantorName() {
        return this.grantorName;
    }
    
    public void setGrantorName(final String grantorName) {
        this.grantorName = grantorName;
    }
    
    public void unsetGrantorName() {
        this.grantorName = null;
    }
    
    public boolean isSetGrantorName() {
        return this.grantorName != null;
    }
    
    public void setGrantorNameIsSet(final boolean value) {
        if (!value) {
            this.grantorName = null;
        }
    }
    
    public PrincipalType getGrantorPrincipalType() {
        return this.grantorPrincipalType;
    }
    
    public void setGrantorPrincipalType(final PrincipalType grantorPrincipalType) {
        this.grantorPrincipalType = grantorPrincipalType;
    }
    
    public void unsetGrantorPrincipalType() {
        this.grantorPrincipalType = null;
    }
    
    public boolean isSetGrantorPrincipalType() {
        return this.grantorPrincipalType != null;
    }
    
    public void setGrantorPrincipalTypeIsSet(final boolean value) {
        if (!value) {
            this.grantorPrincipalType = null;
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
            case PRINCIPAL_NAME: {
                if (value == null) {
                    this.unsetPrincipalName();
                    break;
                }
                this.setPrincipalName((String)value);
                break;
            }
            case PRINCIPAL_TYPE: {
                if (value == null) {
                    this.unsetPrincipalType();
                    break;
                }
                this.setPrincipalType((PrincipalType)value);
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
            case GRANT_TIME: {
                if (value == null) {
                    this.unsetGrantTime();
                    break;
                }
                this.setGrantTime((int)value);
                break;
            }
            case GRANTOR_NAME: {
                if (value == null) {
                    this.unsetGrantorName();
                    break;
                }
                this.setGrantorName((String)value);
                break;
            }
            case GRANTOR_PRINCIPAL_TYPE: {
                if (value == null) {
                    this.unsetGrantorPrincipalType();
                    break;
                }
                this.setGrantorPrincipalType((PrincipalType)value);
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
            case PRINCIPAL_NAME: {
                return this.getPrincipalName();
            }
            case PRINCIPAL_TYPE: {
                return this.getPrincipalType();
            }
            case GRANT_OPTION: {
                return this.isGrantOption();
            }
            case GRANT_TIME: {
                return this.getGrantTime();
            }
            case GRANTOR_NAME: {
                return this.getGrantorName();
            }
            case GRANTOR_PRINCIPAL_TYPE: {
                return this.getGrantorPrincipalType();
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
            case PRINCIPAL_NAME: {
                return this.isSetPrincipalName();
            }
            case PRINCIPAL_TYPE: {
                return this.isSetPrincipalType();
            }
            case GRANT_OPTION: {
                return this.isSetGrantOption();
            }
            case GRANT_TIME: {
                return this.isSetGrantTime();
            }
            case GRANTOR_NAME: {
                return this.isSetGrantorName();
            }
            case GRANTOR_PRINCIPAL_TYPE: {
                return this.isSetGrantorPrincipalType();
            }
            default: {
                throw new IllegalStateException();
            }
        }
    }
    
    @Override
    public boolean equals(final Object that) {
        return that != null && that instanceof RolePrincipalGrant && this.equals((RolePrincipalGrant)that);
    }
    
    public boolean equals(final RolePrincipalGrant that) {
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
        final boolean this_present_principalName = this.isSetPrincipalName();
        final boolean that_present_principalName = that.isSetPrincipalName();
        if (this_present_principalName || that_present_principalName) {
            if (!this_present_principalName || !that_present_principalName) {
                return false;
            }
            if (!this.principalName.equals(that.principalName)) {
                return false;
            }
        }
        final boolean this_present_principalType = this.isSetPrincipalType();
        final boolean that_present_principalType = that.isSetPrincipalType();
        if (this_present_principalType || that_present_principalType) {
            if (!this_present_principalType || !that_present_principalType) {
                return false;
            }
            if (!this.principalType.equals(that.principalType)) {
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
        final boolean this_present_grantTime = true;
        final boolean that_present_grantTime = true;
        if (this_present_grantTime || that_present_grantTime) {
            if (!this_present_grantTime || !that_present_grantTime) {
                return false;
            }
            if (this.grantTime != that.grantTime) {
                return false;
            }
        }
        final boolean this_present_grantorName = this.isSetGrantorName();
        final boolean that_present_grantorName = that.isSetGrantorName();
        if (this_present_grantorName || that_present_grantorName) {
            if (!this_present_grantorName || !that_present_grantorName) {
                return false;
            }
            if (!this.grantorName.equals(that.grantorName)) {
                return false;
            }
        }
        final boolean this_present_grantorPrincipalType = this.isSetGrantorPrincipalType();
        final boolean that_present_grantorPrincipalType = that.isSetGrantorPrincipalType();
        if (this_present_grantorPrincipalType || that_present_grantorPrincipalType) {
            if (!this_present_grantorPrincipalType || !that_present_grantorPrincipalType) {
                return false;
            }
            if (!this.grantorPrincipalType.equals(that.grantorPrincipalType)) {
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
        final boolean present_principalName = this.isSetPrincipalName();
        builder.append(present_principalName);
        if (present_principalName) {
            builder.append(this.principalName);
        }
        final boolean present_principalType = this.isSetPrincipalType();
        builder.append(present_principalType);
        if (present_principalType) {
            builder.append(this.principalType.getValue());
        }
        final boolean present_grantOption = true;
        builder.append(present_grantOption);
        if (present_grantOption) {
            builder.append(this.grantOption);
        }
        final boolean present_grantTime = true;
        builder.append(present_grantTime);
        if (present_grantTime) {
            builder.append(this.grantTime);
        }
        final boolean present_grantorName = this.isSetGrantorName();
        builder.append(present_grantorName);
        if (present_grantorName) {
            builder.append(this.grantorName);
        }
        final boolean present_grantorPrincipalType = this.isSetGrantorPrincipalType();
        builder.append(present_grantorPrincipalType);
        if (present_grantorPrincipalType) {
            builder.append(this.grantorPrincipalType.getValue());
        }
        return builder.toHashCode();
    }
    
    @Override
    public int compareTo(final RolePrincipalGrant other) {
        if (!this.getClass().equals(other.getClass())) {
            return this.getClass().getName().compareTo(other.getClass().getName());
        }
        int lastComparison = 0;
        final RolePrincipalGrant typedOther = other;
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
        lastComparison = Boolean.valueOf(this.isSetPrincipalName()).compareTo(Boolean.valueOf(typedOther.isSetPrincipalName()));
        if (lastComparison != 0) {
            return lastComparison;
        }
        if (this.isSetPrincipalName()) {
            lastComparison = TBaseHelper.compareTo(this.principalName, typedOther.principalName);
            if (lastComparison != 0) {
                return lastComparison;
            }
        }
        lastComparison = Boolean.valueOf(this.isSetPrincipalType()).compareTo(Boolean.valueOf(typedOther.isSetPrincipalType()));
        if (lastComparison != 0) {
            return lastComparison;
        }
        if (this.isSetPrincipalType()) {
            lastComparison = TBaseHelper.compareTo(this.principalType, typedOther.principalType);
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
        lastComparison = Boolean.valueOf(this.isSetGrantTime()).compareTo(Boolean.valueOf(typedOther.isSetGrantTime()));
        if (lastComparison != 0) {
            return lastComparison;
        }
        if (this.isSetGrantTime()) {
            lastComparison = TBaseHelper.compareTo(this.grantTime, typedOther.grantTime);
            if (lastComparison != 0) {
                return lastComparison;
            }
        }
        lastComparison = Boolean.valueOf(this.isSetGrantorName()).compareTo(Boolean.valueOf(typedOther.isSetGrantorName()));
        if (lastComparison != 0) {
            return lastComparison;
        }
        if (this.isSetGrantorName()) {
            lastComparison = TBaseHelper.compareTo(this.grantorName, typedOther.grantorName);
            if (lastComparison != 0) {
                return lastComparison;
            }
        }
        lastComparison = Boolean.valueOf(this.isSetGrantorPrincipalType()).compareTo(Boolean.valueOf(typedOther.isSetGrantorPrincipalType()));
        if (lastComparison != 0) {
            return lastComparison;
        }
        if (this.isSetGrantorPrincipalType()) {
            lastComparison = TBaseHelper.compareTo(this.grantorPrincipalType, typedOther.grantorPrincipalType);
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
        RolePrincipalGrant.schemes.get(iprot.getScheme()).getScheme().read(iprot, this);
    }
    
    @Override
    public void write(final TProtocol oprot) throws TException {
        RolePrincipalGrant.schemes.get(oprot.getScheme()).getScheme().write(oprot, this);
    }
    
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("RolePrincipalGrant(");
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
        sb.append("principalName:");
        if (this.principalName == null) {
            sb.append("null");
        }
        else {
            sb.append(this.principalName);
        }
        first = false;
        if (!first) {
            sb.append(", ");
        }
        sb.append("principalType:");
        if (this.principalType == null) {
            sb.append("null");
        }
        else {
            sb.append(this.principalType);
        }
        first = false;
        if (!first) {
            sb.append(", ");
        }
        sb.append("grantOption:");
        sb.append(this.grantOption);
        first = false;
        if (!first) {
            sb.append(", ");
        }
        sb.append("grantTime:");
        sb.append(this.grantTime);
        first = false;
        if (!first) {
            sb.append(", ");
        }
        sb.append("grantorName:");
        if (this.grantorName == null) {
            sb.append("null");
        }
        else {
            sb.append(this.grantorName);
        }
        first = false;
        if (!first) {
            sb.append(", ");
        }
        sb.append("grantorPrincipalType:");
        if (this.grantorPrincipalType == null) {
            sb.append("null");
        }
        else {
            sb.append(this.grantorPrincipalType);
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
        STRUCT_DESC = new TStruct("RolePrincipalGrant");
        ROLE_NAME_FIELD_DESC = new TField("roleName", (byte)11, (short)1);
        PRINCIPAL_NAME_FIELD_DESC = new TField("principalName", (byte)11, (short)2);
        PRINCIPAL_TYPE_FIELD_DESC = new TField("principalType", (byte)8, (short)3);
        GRANT_OPTION_FIELD_DESC = new TField("grantOption", (byte)2, (short)4);
        GRANT_TIME_FIELD_DESC = new TField("grantTime", (byte)8, (short)5);
        GRANTOR_NAME_FIELD_DESC = new TField("grantorName", (byte)11, (short)6);
        GRANTOR_PRINCIPAL_TYPE_FIELD_DESC = new TField("grantorPrincipalType", (byte)8, (short)7);
        (schemes = new HashMap<Class<? extends IScheme>, SchemeFactory>()).put(StandardScheme.class, new RolePrincipalGrantStandardSchemeFactory());
        RolePrincipalGrant.schemes.put(TupleScheme.class, new RolePrincipalGrantTupleSchemeFactory());
        final Map<_Fields, FieldMetaData> tmpMap = new EnumMap<_Fields, FieldMetaData>(_Fields.class);
        tmpMap.put(_Fields.ROLE_NAME, new FieldMetaData("roleName", (byte)3, new FieldValueMetaData((byte)11)));
        tmpMap.put(_Fields.PRINCIPAL_NAME, new FieldMetaData("principalName", (byte)3, new FieldValueMetaData((byte)11)));
        tmpMap.put(_Fields.PRINCIPAL_TYPE, new FieldMetaData("principalType", (byte)3, new EnumMetaData((byte)16, PrincipalType.class)));
        tmpMap.put(_Fields.GRANT_OPTION, new FieldMetaData("grantOption", (byte)3, new FieldValueMetaData((byte)2)));
        tmpMap.put(_Fields.GRANT_TIME, new FieldMetaData("grantTime", (byte)3, new FieldValueMetaData((byte)8)));
        tmpMap.put(_Fields.GRANTOR_NAME, new FieldMetaData("grantorName", (byte)3, new FieldValueMetaData((byte)11)));
        tmpMap.put(_Fields.GRANTOR_PRINCIPAL_TYPE, new FieldMetaData("grantorPrincipalType", (byte)3, new EnumMetaData((byte)16, PrincipalType.class)));
        FieldMetaData.addStructMetaDataMap(RolePrincipalGrant.class, metaDataMap = Collections.unmodifiableMap((Map<? extends _Fields, ? extends FieldMetaData>)tmpMap));
    }
    
    public enum _Fields implements TFieldIdEnum
    {
        ROLE_NAME((short)1, "roleName"), 
        PRINCIPAL_NAME((short)2, "principalName"), 
        PRINCIPAL_TYPE((short)3, "principalType"), 
        GRANT_OPTION((short)4, "grantOption"), 
        GRANT_TIME((short)5, "grantTime"), 
        GRANTOR_NAME((short)6, "grantorName"), 
        GRANTOR_PRINCIPAL_TYPE((short)7, "grantorPrincipalType");
        
        private static final Map<String, _Fields> byName;
        private final short _thriftId;
        private final String _fieldName;
        
        public static _Fields findByThriftId(final int fieldId) {
            switch (fieldId) {
                case 1: {
                    return _Fields.ROLE_NAME;
                }
                case 2: {
                    return _Fields.PRINCIPAL_NAME;
                }
                case 3: {
                    return _Fields.PRINCIPAL_TYPE;
                }
                case 4: {
                    return _Fields.GRANT_OPTION;
                }
                case 5: {
                    return _Fields.GRANT_TIME;
                }
                case 6: {
                    return _Fields.GRANTOR_NAME;
                }
                case 7: {
                    return _Fields.GRANTOR_PRINCIPAL_TYPE;
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
    
    private static class RolePrincipalGrantStandardSchemeFactory implements SchemeFactory
    {
        @Override
        public RolePrincipalGrantStandardScheme getScheme() {
            return new RolePrincipalGrantStandardScheme();
        }
    }
    
    private static class RolePrincipalGrantStandardScheme extends StandardScheme<RolePrincipalGrant>
    {
        @Override
        public void read(final TProtocol iprot, final RolePrincipalGrant struct) throws TException {
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
                        if (schemeField.type == 11) {
                            struct.principalName = iprot.readString();
                            struct.setPrincipalNameIsSet(true);
                            break;
                        }
                        TProtocolUtil.skip(iprot, schemeField.type);
                        break;
                    }
                    case 3: {
                        if (schemeField.type == 8) {
                            struct.principalType = PrincipalType.findByValue(iprot.readI32());
                            struct.setPrincipalTypeIsSet(true);
                            break;
                        }
                        TProtocolUtil.skip(iprot, schemeField.type);
                        break;
                    }
                    case 4: {
                        if (schemeField.type == 2) {
                            struct.grantOption = iprot.readBool();
                            struct.setGrantOptionIsSet(true);
                            break;
                        }
                        TProtocolUtil.skip(iprot, schemeField.type);
                        break;
                    }
                    case 5: {
                        if (schemeField.type == 8) {
                            struct.grantTime = iprot.readI32();
                            struct.setGrantTimeIsSet(true);
                            break;
                        }
                        TProtocolUtil.skip(iprot, schemeField.type);
                        break;
                    }
                    case 6: {
                        if (schemeField.type == 11) {
                            struct.grantorName = iprot.readString();
                            struct.setGrantorNameIsSet(true);
                            break;
                        }
                        TProtocolUtil.skip(iprot, schemeField.type);
                        break;
                    }
                    case 7: {
                        if (schemeField.type == 8) {
                            struct.grantorPrincipalType = PrincipalType.findByValue(iprot.readI32());
                            struct.setGrantorPrincipalTypeIsSet(true);
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
        public void write(final TProtocol oprot, final RolePrincipalGrant struct) throws TException {
            struct.validate();
            oprot.writeStructBegin(RolePrincipalGrant.STRUCT_DESC);
            if (struct.roleName != null) {
                oprot.writeFieldBegin(RolePrincipalGrant.ROLE_NAME_FIELD_DESC);
                oprot.writeString(struct.roleName);
                oprot.writeFieldEnd();
            }
            if (struct.principalName != null) {
                oprot.writeFieldBegin(RolePrincipalGrant.PRINCIPAL_NAME_FIELD_DESC);
                oprot.writeString(struct.principalName);
                oprot.writeFieldEnd();
            }
            if (struct.principalType != null) {
                oprot.writeFieldBegin(RolePrincipalGrant.PRINCIPAL_TYPE_FIELD_DESC);
                oprot.writeI32(struct.principalType.getValue());
                oprot.writeFieldEnd();
            }
            oprot.writeFieldBegin(RolePrincipalGrant.GRANT_OPTION_FIELD_DESC);
            oprot.writeBool(struct.grantOption);
            oprot.writeFieldEnd();
            oprot.writeFieldBegin(RolePrincipalGrant.GRANT_TIME_FIELD_DESC);
            oprot.writeI32(struct.grantTime);
            oprot.writeFieldEnd();
            if (struct.grantorName != null) {
                oprot.writeFieldBegin(RolePrincipalGrant.GRANTOR_NAME_FIELD_DESC);
                oprot.writeString(struct.grantorName);
                oprot.writeFieldEnd();
            }
            if (struct.grantorPrincipalType != null) {
                oprot.writeFieldBegin(RolePrincipalGrant.GRANTOR_PRINCIPAL_TYPE_FIELD_DESC);
                oprot.writeI32(struct.grantorPrincipalType.getValue());
                oprot.writeFieldEnd();
            }
            oprot.writeFieldStop();
            oprot.writeStructEnd();
        }
    }
    
    private static class RolePrincipalGrantTupleSchemeFactory implements SchemeFactory
    {
        @Override
        public RolePrincipalGrantTupleScheme getScheme() {
            return new RolePrincipalGrantTupleScheme();
        }
    }
    
    private static class RolePrincipalGrantTupleScheme extends TupleScheme<RolePrincipalGrant>
    {
        @Override
        public void write(final TProtocol prot, final RolePrincipalGrant struct) throws TException {
            final TTupleProtocol oprot = (TTupleProtocol)prot;
            final BitSet optionals = new BitSet();
            if (struct.isSetRoleName()) {
                optionals.set(0);
            }
            if (struct.isSetPrincipalName()) {
                optionals.set(1);
            }
            if (struct.isSetPrincipalType()) {
                optionals.set(2);
            }
            if (struct.isSetGrantOption()) {
                optionals.set(3);
            }
            if (struct.isSetGrantTime()) {
                optionals.set(4);
            }
            if (struct.isSetGrantorName()) {
                optionals.set(5);
            }
            if (struct.isSetGrantorPrincipalType()) {
                optionals.set(6);
            }
            oprot.writeBitSet(optionals, 7);
            if (struct.isSetRoleName()) {
                oprot.writeString(struct.roleName);
            }
            if (struct.isSetPrincipalName()) {
                oprot.writeString(struct.principalName);
            }
            if (struct.isSetPrincipalType()) {
                oprot.writeI32(struct.principalType.getValue());
            }
            if (struct.isSetGrantOption()) {
                oprot.writeBool(struct.grantOption);
            }
            if (struct.isSetGrantTime()) {
                oprot.writeI32(struct.grantTime);
            }
            if (struct.isSetGrantorName()) {
                oprot.writeString(struct.grantorName);
            }
            if (struct.isSetGrantorPrincipalType()) {
                oprot.writeI32(struct.grantorPrincipalType.getValue());
            }
        }
        
        @Override
        public void read(final TProtocol prot, final RolePrincipalGrant struct) throws TException {
            final TTupleProtocol iprot = (TTupleProtocol)prot;
            final BitSet incoming = iprot.readBitSet(7);
            if (incoming.get(0)) {
                struct.roleName = iprot.readString();
                struct.setRoleNameIsSet(true);
            }
            if (incoming.get(1)) {
                struct.principalName = iprot.readString();
                struct.setPrincipalNameIsSet(true);
            }
            if (incoming.get(2)) {
                struct.principalType = PrincipalType.findByValue(iprot.readI32());
                struct.setPrincipalTypeIsSet(true);
            }
            if (incoming.get(3)) {
                struct.grantOption = iprot.readBool();
                struct.setGrantOptionIsSet(true);
            }
            if (incoming.get(4)) {
                struct.grantTime = iprot.readI32();
                struct.setGrantTimeIsSet(true);
            }
            if (incoming.get(5)) {
                struct.grantorName = iprot.readString();
                struct.setGrantorNameIsSet(true);
            }
            if (incoming.get(6)) {
                struct.grantorPrincipalType = PrincipalType.findByValue(iprot.readI32());
                struct.setGrantorPrincipalTypeIsSet(true);
            }
        }
    }
}
