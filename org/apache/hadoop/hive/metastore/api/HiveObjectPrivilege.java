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

public class HiveObjectPrivilege implements TBase<HiveObjectPrivilege, _Fields>, Serializable, Cloneable
{
    private static final TStruct STRUCT_DESC;
    private static final TField HIVE_OBJECT_FIELD_DESC;
    private static final TField PRINCIPAL_NAME_FIELD_DESC;
    private static final TField PRINCIPAL_TYPE_FIELD_DESC;
    private static final TField GRANT_INFO_FIELD_DESC;
    private static final Map<Class<? extends IScheme>, SchemeFactory> schemes;
    private HiveObjectRef hiveObject;
    private String principalName;
    private PrincipalType principalType;
    private PrivilegeGrantInfo grantInfo;
    public static final Map<_Fields, FieldMetaData> metaDataMap;
    
    public HiveObjectPrivilege() {
    }
    
    public HiveObjectPrivilege(final HiveObjectRef hiveObject, final String principalName, final PrincipalType principalType, final PrivilegeGrantInfo grantInfo) {
        this();
        this.hiveObject = hiveObject;
        this.principalName = principalName;
        this.principalType = principalType;
        this.grantInfo = grantInfo;
    }
    
    public HiveObjectPrivilege(final HiveObjectPrivilege other) {
        if (other.isSetHiveObject()) {
            this.hiveObject = new HiveObjectRef(other.hiveObject);
        }
        if (other.isSetPrincipalName()) {
            this.principalName = other.principalName;
        }
        if (other.isSetPrincipalType()) {
            this.principalType = other.principalType;
        }
        if (other.isSetGrantInfo()) {
            this.grantInfo = new PrivilegeGrantInfo(other.grantInfo);
        }
    }
    
    @Override
    public HiveObjectPrivilege deepCopy() {
        return new HiveObjectPrivilege(this);
    }
    
    @Override
    public void clear() {
        this.hiveObject = null;
        this.principalName = null;
        this.principalType = null;
        this.grantInfo = null;
    }
    
    public HiveObjectRef getHiveObject() {
        return this.hiveObject;
    }
    
    public void setHiveObject(final HiveObjectRef hiveObject) {
        this.hiveObject = hiveObject;
    }
    
    public void unsetHiveObject() {
        this.hiveObject = null;
    }
    
    public boolean isSetHiveObject() {
        return this.hiveObject != null;
    }
    
    public void setHiveObjectIsSet(final boolean value) {
        if (!value) {
            this.hiveObject = null;
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
    
    public PrivilegeGrantInfo getGrantInfo() {
        return this.grantInfo;
    }
    
    public void setGrantInfo(final PrivilegeGrantInfo grantInfo) {
        this.grantInfo = grantInfo;
    }
    
    public void unsetGrantInfo() {
        this.grantInfo = null;
    }
    
    public boolean isSetGrantInfo() {
        return this.grantInfo != null;
    }
    
    public void setGrantInfoIsSet(final boolean value) {
        if (!value) {
            this.grantInfo = null;
        }
    }
    
    @Override
    public void setFieldValue(final _Fields field, final Object value) {
        switch (field) {
            case HIVE_OBJECT: {
                if (value == null) {
                    this.unsetHiveObject();
                    break;
                }
                this.setHiveObject((HiveObjectRef)value);
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
            case GRANT_INFO: {
                if (value == null) {
                    this.unsetGrantInfo();
                    break;
                }
                this.setGrantInfo((PrivilegeGrantInfo)value);
                break;
            }
        }
    }
    
    @Override
    public Object getFieldValue(final _Fields field) {
        switch (field) {
            case HIVE_OBJECT: {
                return this.getHiveObject();
            }
            case PRINCIPAL_NAME: {
                return this.getPrincipalName();
            }
            case PRINCIPAL_TYPE: {
                return this.getPrincipalType();
            }
            case GRANT_INFO: {
                return this.getGrantInfo();
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
            case HIVE_OBJECT: {
                return this.isSetHiveObject();
            }
            case PRINCIPAL_NAME: {
                return this.isSetPrincipalName();
            }
            case PRINCIPAL_TYPE: {
                return this.isSetPrincipalType();
            }
            case GRANT_INFO: {
                return this.isSetGrantInfo();
            }
            default: {
                throw new IllegalStateException();
            }
        }
    }
    
    @Override
    public boolean equals(final Object that) {
        return that != null && that instanceof HiveObjectPrivilege && this.equals((HiveObjectPrivilege)that);
    }
    
    public boolean equals(final HiveObjectPrivilege that) {
        if (that == null) {
            return false;
        }
        final boolean this_present_hiveObject = this.isSetHiveObject();
        final boolean that_present_hiveObject = that.isSetHiveObject();
        if (this_present_hiveObject || that_present_hiveObject) {
            if (!this_present_hiveObject || !that_present_hiveObject) {
                return false;
            }
            if (!this.hiveObject.equals(that.hiveObject)) {
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
        final boolean this_present_grantInfo = this.isSetGrantInfo();
        final boolean that_present_grantInfo = that.isSetGrantInfo();
        if (this_present_grantInfo || that_present_grantInfo) {
            if (!this_present_grantInfo || !that_present_grantInfo) {
                return false;
            }
            if (!this.grantInfo.equals(that.grantInfo)) {
                return false;
            }
        }
        return true;
    }
    
    @Override
    public int hashCode() {
        final HashCodeBuilder builder = new HashCodeBuilder();
        final boolean present_hiveObject = this.isSetHiveObject();
        builder.append(present_hiveObject);
        if (present_hiveObject) {
            builder.append(this.hiveObject);
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
        final boolean present_grantInfo = this.isSetGrantInfo();
        builder.append(present_grantInfo);
        if (present_grantInfo) {
            builder.append(this.grantInfo);
        }
        return builder.toHashCode();
    }
    
    @Override
    public int compareTo(final HiveObjectPrivilege other) {
        if (!this.getClass().equals(other.getClass())) {
            return this.getClass().getName().compareTo(other.getClass().getName());
        }
        int lastComparison = 0;
        final HiveObjectPrivilege typedOther = other;
        lastComparison = Boolean.valueOf(this.isSetHiveObject()).compareTo(Boolean.valueOf(typedOther.isSetHiveObject()));
        if (lastComparison != 0) {
            return lastComparison;
        }
        if (this.isSetHiveObject()) {
            lastComparison = TBaseHelper.compareTo(this.hiveObject, typedOther.hiveObject);
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
        lastComparison = Boolean.valueOf(this.isSetGrantInfo()).compareTo(Boolean.valueOf(typedOther.isSetGrantInfo()));
        if (lastComparison != 0) {
            return lastComparison;
        }
        if (this.isSetGrantInfo()) {
            lastComparison = TBaseHelper.compareTo(this.grantInfo, typedOther.grantInfo);
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
        HiveObjectPrivilege.schemes.get(iprot.getScheme()).getScheme().read(iprot, this);
    }
    
    @Override
    public void write(final TProtocol oprot) throws TException {
        HiveObjectPrivilege.schemes.get(oprot.getScheme()).getScheme().write(oprot, this);
    }
    
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("HiveObjectPrivilege(");
        boolean first = true;
        sb.append("hiveObject:");
        if (this.hiveObject == null) {
            sb.append("null");
        }
        else {
            sb.append(this.hiveObject);
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
        sb.append("grantInfo:");
        if (this.grantInfo == null) {
            sb.append("null");
        }
        else {
            sb.append(this.grantInfo);
        }
        first = false;
        sb.append(")");
        return sb.toString();
    }
    
    public void validate() throws TException {
        if (this.hiveObject != null) {
            this.hiveObject.validate();
        }
        if (this.grantInfo != null) {
            this.grantInfo.validate();
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
        STRUCT_DESC = new TStruct("HiveObjectPrivilege");
        HIVE_OBJECT_FIELD_DESC = new TField("hiveObject", (byte)12, (short)1);
        PRINCIPAL_NAME_FIELD_DESC = new TField("principalName", (byte)11, (short)2);
        PRINCIPAL_TYPE_FIELD_DESC = new TField("principalType", (byte)8, (short)3);
        GRANT_INFO_FIELD_DESC = new TField("grantInfo", (byte)12, (short)4);
        (schemes = new HashMap<Class<? extends IScheme>, SchemeFactory>()).put(StandardScheme.class, new HiveObjectPrivilegeStandardSchemeFactory());
        HiveObjectPrivilege.schemes.put(TupleScheme.class, new HiveObjectPrivilegeTupleSchemeFactory());
        final Map<_Fields, FieldMetaData> tmpMap = new EnumMap<_Fields, FieldMetaData>(_Fields.class);
        tmpMap.put(_Fields.HIVE_OBJECT, new FieldMetaData("hiveObject", (byte)3, new StructMetaData((byte)12, HiveObjectRef.class)));
        tmpMap.put(_Fields.PRINCIPAL_NAME, new FieldMetaData("principalName", (byte)3, new FieldValueMetaData((byte)11)));
        tmpMap.put(_Fields.PRINCIPAL_TYPE, new FieldMetaData("principalType", (byte)3, new EnumMetaData((byte)16, PrincipalType.class)));
        tmpMap.put(_Fields.GRANT_INFO, new FieldMetaData("grantInfo", (byte)3, new StructMetaData((byte)12, PrivilegeGrantInfo.class)));
        FieldMetaData.addStructMetaDataMap(HiveObjectPrivilege.class, metaDataMap = Collections.unmodifiableMap((Map<? extends _Fields, ? extends FieldMetaData>)tmpMap));
    }
    
    public enum _Fields implements TFieldIdEnum
    {
        HIVE_OBJECT((short)1, "hiveObject"), 
        PRINCIPAL_NAME((short)2, "principalName"), 
        PRINCIPAL_TYPE((short)3, "principalType"), 
        GRANT_INFO((short)4, "grantInfo");
        
        private static final Map<String, _Fields> byName;
        private final short _thriftId;
        private final String _fieldName;
        
        public static _Fields findByThriftId(final int fieldId) {
            switch (fieldId) {
                case 1: {
                    return _Fields.HIVE_OBJECT;
                }
                case 2: {
                    return _Fields.PRINCIPAL_NAME;
                }
                case 3: {
                    return _Fields.PRINCIPAL_TYPE;
                }
                case 4: {
                    return _Fields.GRANT_INFO;
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
    
    private static class HiveObjectPrivilegeStandardSchemeFactory implements SchemeFactory
    {
        @Override
        public HiveObjectPrivilegeStandardScheme getScheme() {
            return new HiveObjectPrivilegeStandardScheme();
        }
    }
    
    private static class HiveObjectPrivilegeStandardScheme extends StandardScheme<HiveObjectPrivilege>
    {
        @Override
        public void read(final TProtocol iprot, final HiveObjectPrivilege struct) throws TException {
            iprot.readStructBegin();
            while (true) {
                final TField schemeField = iprot.readFieldBegin();
                if (schemeField.type == 0) {
                    break;
                }
                switch (schemeField.id) {
                    case 1: {
                        if (schemeField.type == 12) {
                            struct.hiveObject = new HiveObjectRef();
                            struct.hiveObject.read(iprot);
                            struct.setHiveObjectIsSet(true);
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
                        if (schemeField.type == 12) {
                            struct.grantInfo = new PrivilegeGrantInfo();
                            struct.grantInfo.read(iprot);
                            struct.setGrantInfoIsSet(true);
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
        public void write(final TProtocol oprot, final HiveObjectPrivilege struct) throws TException {
            struct.validate();
            oprot.writeStructBegin(HiveObjectPrivilege.STRUCT_DESC);
            if (struct.hiveObject != null) {
                oprot.writeFieldBegin(HiveObjectPrivilege.HIVE_OBJECT_FIELD_DESC);
                struct.hiveObject.write(oprot);
                oprot.writeFieldEnd();
            }
            if (struct.principalName != null) {
                oprot.writeFieldBegin(HiveObjectPrivilege.PRINCIPAL_NAME_FIELD_DESC);
                oprot.writeString(struct.principalName);
                oprot.writeFieldEnd();
            }
            if (struct.principalType != null) {
                oprot.writeFieldBegin(HiveObjectPrivilege.PRINCIPAL_TYPE_FIELD_DESC);
                oprot.writeI32(struct.principalType.getValue());
                oprot.writeFieldEnd();
            }
            if (struct.grantInfo != null) {
                oprot.writeFieldBegin(HiveObjectPrivilege.GRANT_INFO_FIELD_DESC);
                struct.grantInfo.write(oprot);
                oprot.writeFieldEnd();
            }
            oprot.writeFieldStop();
            oprot.writeStructEnd();
        }
    }
    
    private static class HiveObjectPrivilegeTupleSchemeFactory implements SchemeFactory
    {
        @Override
        public HiveObjectPrivilegeTupleScheme getScheme() {
            return new HiveObjectPrivilegeTupleScheme();
        }
    }
    
    private static class HiveObjectPrivilegeTupleScheme extends TupleScheme<HiveObjectPrivilege>
    {
        @Override
        public void write(final TProtocol prot, final HiveObjectPrivilege struct) throws TException {
            final TTupleProtocol oprot = (TTupleProtocol)prot;
            final BitSet optionals = new BitSet();
            if (struct.isSetHiveObject()) {
                optionals.set(0);
            }
            if (struct.isSetPrincipalName()) {
                optionals.set(1);
            }
            if (struct.isSetPrincipalType()) {
                optionals.set(2);
            }
            if (struct.isSetGrantInfo()) {
                optionals.set(3);
            }
            oprot.writeBitSet(optionals, 4);
            if (struct.isSetHiveObject()) {
                struct.hiveObject.write(oprot);
            }
            if (struct.isSetPrincipalName()) {
                oprot.writeString(struct.principalName);
            }
            if (struct.isSetPrincipalType()) {
                oprot.writeI32(struct.principalType.getValue());
            }
            if (struct.isSetGrantInfo()) {
                struct.grantInfo.write(oprot);
            }
        }
        
        @Override
        public void read(final TProtocol prot, final HiveObjectPrivilege struct) throws TException {
            final TTupleProtocol iprot = (TTupleProtocol)prot;
            final BitSet incoming = iprot.readBitSet(4);
            if (incoming.get(0)) {
                struct.hiveObject = new HiveObjectRef();
                struct.hiveObject.read(iprot);
                struct.setHiveObjectIsSet(true);
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
                struct.grantInfo = new PrivilegeGrantInfo();
                struct.grantInfo.read(iprot);
                struct.setGrantInfoIsSet(true);
            }
        }
    }
}
