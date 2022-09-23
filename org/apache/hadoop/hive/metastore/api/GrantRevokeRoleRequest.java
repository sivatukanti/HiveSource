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

public class GrantRevokeRoleRequest implements TBase<GrantRevokeRoleRequest, _Fields>, Serializable, Cloneable
{
    private static final TStruct STRUCT_DESC;
    private static final TField REQUEST_TYPE_FIELD_DESC;
    private static final TField ROLE_NAME_FIELD_DESC;
    private static final TField PRINCIPAL_NAME_FIELD_DESC;
    private static final TField PRINCIPAL_TYPE_FIELD_DESC;
    private static final TField GRANTOR_FIELD_DESC;
    private static final TField GRANTOR_TYPE_FIELD_DESC;
    private static final TField GRANT_OPTION_FIELD_DESC;
    private static final Map<Class<? extends IScheme>, SchemeFactory> schemes;
    private GrantRevokeType requestType;
    private String roleName;
    private String principalName;
    private PrincipalType principalType;
    private String grantor;
    private PrincipalType grantorType;
    private boolean grantOption;
    private static final int __GRANTOPTION_ISSET_ID = 0;
    private byte __isset_bitfield;
    private _Fields[] optionals;
    public static final Map<_Fields, FieldMetaData> metaDataMap;
    
    public GrantRevokeRoleRequest() {
        this.__isset_bitfield = 0;
        this.optionals = new _Fields[] { _Fields.GRANTOR, _Fields.GRANTOR_TYPE, _Fields.GRANT_OPTION };
    }
    
    public GrantRevokeRoleRequest(final GrantRevokeType requestType, final String roleName, final String principalName, final PrincipalType principalType) {
        this();
        this.requestType = requestType;
        this.roleName = roleName;
        this.principalName = principalName;
        this.principalType = principalType;
    }
    
    public GrantRevokeRoleRequest(final GrantRevokeRoleRequest other) {
        this.__isset_bitfield = 0;
        this.optionals = new _Fields[] { _Fields.GRANTOR, _Fields.GRANTOR_TYPE, _Fields.GRANT_OPTION };
        this.__isset_bitfield = other.__isset_bitfield;
        if (other.isSetRequestType()) {
            this.requestType = other.requestType;
        }
        if (other.isSetRoleName()) {
            this.roleName = other.roleName;
        }
        if (other.isSetPrincipalName()) {
            this.principalName = other.principalName;
        }
        if (other.isSetPrincipalType()) {
            this.principalType = other.principalType;
        }
        if (other.isSetGrantor()) {
            this.grantor = other.grantor;
        }
        if (other.isSetGrantorType()) {
            this.grantorType = other.grantorType;
        }
        this.grantOption = other.grantOption;
    }
    
    @Override
    public GrantRevokeRoleRequest deepCopy() {
        return new GrantRevokeRoleRequest(this);
    }
    
    @Override
    public void clear() {
        this.requestType = null;
        this.roleName = null;
        this.principalName = null;
        this.principalType = null;
        this.grantor = null;
        this.grantorType = null;
        this.setGrantOptionIsSet(false);
        this.grantOption = false;
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
        this.__isset_bitfield = EncodingUtils.clearBit(this.__isset_bitfield, 0);
    }
    
    public boolean isSetGrantOption() {
        return EncodingUtils.testBit(this.__isset_bitfield, 0);
    }
    
    public void setGrantOptionIsSet(final boolean value) {
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
            case REQUEST_TYPE: {
                return this.getRequestType();
            }
            case ROLE_NAME: {
                return this.getRoleName();
            }
            case PRINCIPAL_NAME: {
                return this.getPrincipalName();
            }
            case PRINCIPAL_TYPE: {
                return this.getPrincipalType();
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
            case REQUEST_TYPE: {
                return this.isSetRequestType();
            }
            case ROLE_NAME: {
                return this.isSetRoleName();
            }
            case PRINCIPAL_NAME: {
                return this.isSetPrincipalName();
            }
            case PRINCIPAL_TYPE: {
                return this.isSetPrincipalType();
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
        return that != null && that instanceof GrantRevokeRoleRequest && this.equals((GrantRevokeRoleRequest)that);
    }
    
    public boolean equals(final GrantRevokeRoleRequest that) {
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
        final boolean this_present_grantOption = this.isSetGrantOption();
        final boolean that_present_grantOption = that.isSetGrantOption();
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
        final boolean present_requestType = this.isSetRequestType();
        builder.append(present_requestType);
        if (present_requestType) {
            builder.append(this.requestType.getValue());
        }
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
        final boolean present_grantOption = this.isSetGrantOption();
        builder.append(present_grantOption);
        if (present_grantOption) {
            builder.append(this.grantOption);
        }
        return builder.toHashCode();
    }
    
    @Override
    public int compareTo(final GrantRevokeRoleRequest other) {
        if (!this.getClass().equals(other.getClass())) {
            return this.getClass().getName().compareTo(other.getClass().getName());
        }
        int lastComparison = 0;
        final GrantRevokeRoleRequest typedOther = other;
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
        GrantRevokeRoleRequest.schemes.get(iprot.getScheme()).getScheme().read(iprot, this);
    }
    
    @Override
    public void write(final TProtocol oprot) throws TException {
        GrantRevokeRoleRequest.schemes.get(oprot.getScheme()).getScheme().write(oprot, this);
    }
    
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("GrantRevokeRoleRequest(");
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
        if (this.isSetGrantor()) {
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
        }
        if (this.isSetGrantorType()) {
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
        }
        if (this.isSetGrantOption()) {
            if (!first) {
                sb.append(", ");
            }
            sb.append("grantOption:");
            sb.append(this.grantOption);
            first = false;
        }
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
        STRUCT_DESC = new TStruct("GrantRevokeRoleRequest");
        REQUEST_TYPE_FIELD_DESC = new TField("requestType", (byte)8, (short)1);
        ROLE_NAME_FIELD_DESC = new TField("roleName", (byte)11, (short)2);
        PRINCIPAL_NAME_FIELD_DESC = new TField("principalName", (byte)11, (short)3);
        PRINCIPAL_TYPE_FIELD_DESC = new TField("principalType", (byte)8, (short)4);
        GRANTOR_FIELD_DESC = new TField("grantor", (byte)11, (short)5);
        GRANTOR_TYPE_FIELD_DESC = new TField("grantorType", (byte)8, (short)6);
        GRANT_OPTION_FIELD_DESC = new TField("grantOption", (byte)2, (short)7);
        (schemes = new HashMap<Class<? extends IScheme>, SchemeFactory>()).put(StandardScheme.class, new GrantRevokeRoleRequestStandardSchemeFactory());
        GrantRevokeRoleRequest.schemes.put(TupleScheme.class, new GrantRevokeRoleRequestTupleSchemeFactory());
        final Map<_Fields, FieldMetaData> tmpMap = new EnumMap<_Fields, FieldMetaData>(_Fields.class);
        tmpMap.put(_Fields.REQUEST_TYPE, new FieldMetaData("requestType", (byte)3, new EnumMetaData((byte)16, GrantRevokeType.class)));
        tmpMap.put(_Fields.ROLE_NAME, new FieldMetaData("roleName", (byte)3, new FieldValueMetaData((byte)11)));
        tmpMap.put(_Fields.PRINCIPAL_NAME, new FieldMetaData("principalName", (byte)3, new FieldValueMetaData((byte)11)));
        tmpMap.put(_Fields.PRINCIPAL_TYPE, new FieldMetaData("principalType", (byte)3, new EnumMetaData((byte)16, PrincipalType.class)));
        tmpMap.put(_Fields.GRANTOR, new FieldMetaData("grantor", (byte)2, new FieldValueMetaData((byte)11)));
        tmpMap.put(_Fields.GRANTOR_TYPE, new FieldMetaData("grantorType", (byte)2, new EnumMetaData((byte)16, PrincipalType.class)));
        tmpMap.put(_Fields.GRANT_OPTION, new FieldMetaData("grantOption", (byte)2, new FieldValueMetaData((byte)2)));
        FieldMetaData.addStructMetaDataMap(GrantRevokeRoleRequest.class, metaDataMap = Collections.unmodifiableMap((Map<? extends _Fields, ? extends FieldMetaData>)tmpMap));
    }
    
    public enum _Fields implements TFieldIdEnum
    {
        REQUEST_TYPE((short)1, "requestType"), 
        ROLE_NAME((short)2, "roleName"), 
        PRINCIPAL_NAME((short)3, "principalName"), 
        PRINCIPAL_TYPE((short)4, "principalType"), 
        GRANTOR((short)5, "grantor"), 
        GRANTOR_TYPE((short)6, "grantorType"), 
        GRANT_OPTION((short)7, "grantOption");
        
        private static final Map<String, _Fields> byName;
        private final short _thriftId;
        private final String _fieldName;
        
        public static _Fields findByThriftId(final int fieldId) {
            switch (fieldId) {
                case 1: {
                    return _Fields.REQUEST_TYPE;
                }
                case 2: {
                    return _Fields.ROLE_NAME;
                }
                case 3: {
                    return _Fields.PRINCIPAL_NAME;
                }
                case 4: {
                    return _Fields.PRINCIPAL_TYPE;
                }
                case 5: {
                    return _Fields.GRANTOR;
                }
                case 6: {
                    return _Fields.GRANTOR_TYPE;
                }
                case 7: {
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
    
    private static class GrantRevokeRoleRequestStandardSchemeFactory implements SchemeFactory
    {
        @Override
        public GrantRevokeRoleRequestStandardScheme getScheme() {
            return new GrantRevokeRoleRequestStandardScheme();
        }
    }
    
    private static class GrantRevokeRoleRequestStandardScheme extends StandardScheme<GrantRevokeRoleRequest>
    {
        @Override
        public void read(final TProtocol iprot, final GrantRevokeRoleRequest struct) throws TException {
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
                        if (schemeField.type == 11) {
                            struct.roleName = iprot.readString();
                            struct.setRoleNameIsSet(true);
                            break;
                        }
                        TProtocolUtil.skip(iprot, schemeField.type);
                        break;
                    }
                    case 3: {
                        if (schemeField.type == 11) {
                            struct.principalName = iprot.readString();
                            struct.setPrincipalNameIsSet(true);
                            break;
                        }
                        TProtocolUtil.skip(iprot, schemeField.type);
                        break;
                    }
                    case 4: {
                        if (schemeField.type == 8) {
                            struct.principalType = PrincipalType.findByValue(iprot.readI32());
                            struct.setPrincipalTypeIsSet(true);
                            break;
                        }
                        TProtocolUtil.skip(iprot, schemeField.type);
                        break;
                    }
                    case 5: {
                        if (schemeField.type == 11) {
                            struct.grantor = iprot.readString();
                            struct.setGrantorIsSet(true);
                            break;
                        }
                        TProtocolUtil.skip(iprot, schemeField.type);
                        break;
                    }
                    case 6: {
                        if (schemeField.type == 8) {
                            struct.grantorType = PrincipalType.findByValue(iprot.readI32());
                            struct.setGrantorTypeIsSet(true);
                            break;
                        }
                        TProtocolUtil.skip(iprot, schemeField.type);
                        break;
                    }
                    case 7: {
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
        public void write(final TProtocol oprot, final GrantRevokeRoleRequest struct) throws TException {
            struct.validate();
            oprot.writeStructBegin(GrantRevokeRoleRequest.STRUCT_DESC);
            if (struct.requestType != null) {
                oprot.writeFieldBegin(GrantRevokeRoleRequest.REQUEST_TYPE_FIELD_DESC);
                oprot.writeI32(struct.requestType.getValue());
                oprot.writeFieldEnd();
            }
            if (struct.roleName != null) {
                oprot.writeFieldBegin(GrantRevokeRoleRequest.ROLE_NAME_FIELD_DESC);
                oprot.writeString(struct.roleName);
                oprot.writeFieldEnd();
            }
            if (struct.principalName != null) {
                oprot.writeFieldBegin(GrantRevokeRoleRequest.PRINCIPAL_NAME_FIELD_DESC);
                oprot.writeString(struct.principalName);
                oprot.writeFieldEnd();
            }
            if (struct.principalType != null) {
                oprot.writeFieldBegin(GrantRevokeRoleRequest.PRINCIPAL_TYPE_FIELD_DESC);
                oprot.writeI32(struct.principalType.getValue());
                oprot.writeFieldEnd();
            }
            if (struct.grantor != null && struct.isSetGrantor()) {
                oprot.writeFieldBegin(GrantRevokeRoleRequest.GRANTOR_FIELD_DESC);
                oprot.writeString(struct.grantor);
                oprot.writeFieldEnd();
            }
            if (struct.grantorType != null && struct.isSetGrantorType()) {
                oprot.writeFieldBegin(GrantRevokeRoleRequest.GRANTOR_TYPE_FIELD_DESC);
                oprot.writeI32(struct.grantorType.getValue());
                oprot.writeFieldEnd();
            }
            if (struct.isSetGrantOption()) {
                oprot.writeFieldBegin(GrantRevokeRoleRequest.GRANT_OPTION_FIELD_DESC);
                oprot.writeBool(struct.grantOption);
                oprot.writeFieldEnd();
            }
            oprot.writeFieldStop();
            oprot.writeStructEnd();
        }
    }
    
    private static class GrantRevokeRoleRequestTupleSchemeFactory implements SchemeFactory
    {
        @Override
        public GrantRevokeRoleRequestTupleScheme getScheme() {
            return new GrantRevokeRoleRequestTupleScheme();
        }
    }
    
    private static class GrantRevokeRoleRequestTupleScheme extends TupleScheme<GrantRevokeRoleRequest>
    {
        @Override
        public void write(final TProtocol prot, final GrantRevokeRoleRequest struct) throws TException {
            final TTupleProtocol oprot = (TTupleProtocol)prot;
            final BitSet optionals = new BitSet();
            if (struct.isSetRequestType()) {
                optionals.set(0);
            }
            if (struct.isSetRoleName()) {
                optionals.set(1);
            }
            if (struct.isSetPrincipalName()) {
                optionals.set(2);
            }
            if (struct.isSetPrincipalType()) {
                optionals.set(3);
            }
            if (struct.isSetGrantor()) {
                optionals.set(4);
            }
            if (struct.isSetGrantorType()) {
                optionals.set(5);
            }
            if (struct.isSetGrantOption()) {
                optionals.set(6);
            }
            oprot.writeBitSet(optionals, 7);
            if (struct.isSetRequestType()) {
                oprot.writeI32(struct.requestType.getValue());
            }
            if (struct.isSetRoleName()) {
                oprot.writeString(struct.roleName);
            }
            if (struct.isSetPrincipalName()) {
                oprot.writeString(struct.principalName);
            }
            if (struct.isSetPrincipalType()) {
                oprot.writeI32(struct.principalType.getValue());
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
        public void read(final TProtocol prot, final GrantRevokeRoleRequest struct) throws TException {
            final TTupleProtocol iprot = (TTupleProtocol)prot;
            final BitSet incoming = iprot.readBitSet(7);
            if (incoming.get(0)) {
                struct.requestType = GrantRevokeType.findByValue(iprot.readI32());
                struct.setRequestTypeIsSet(true);
            }
            if (incoming.get(1)) {
                struct.roleName = iprot.readString();
                struct.setRoleNameIsSet(true);
            }
            if (incoming.get(2)) {
                struct.principalName = iprot.readString();
                struct.setPrincipalNameIsSet(true);
            }
            if (incoming.get(3)) {
                struct.principalType = PrincipalType.findByValue(iprot.readI32());
                struct.setPrincipalTypeIsSet(true);
            }
            if (incoming.get(4)) {
                struct.grantor = iprot.readString();
                struct.setGrantorIsSet(true);
            }
            if (incoming.get(5)) {
                struct.grantorType = PrincipalType.findByValue(iprot.readI32());
                struct.setGrantorTypeIsSet(true);
            }
            if (incoming.get(6)) {
                struct.grantOption = iprot.readBool();
                struct.setGrantOptionIsSet(true);
            }
        }
    }
}
