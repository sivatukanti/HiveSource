// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.hive.metastore.api;

import java.util.BitSet;
import org.apache.thrift.protocol.TTupleProtocol;
import org.apache.thrift.protocol.TList;
import org.apache.thrift.protocol.TMap;
import org.apache.thrift.protocol.TProtocolUtil;
import java.util.EnumSet;
import java.util.Collections;
import org.apache.thrift.meta_data.MapMetaData;
import org.apache.thrift.meta_data.ListMetaData;
import org.apache.thrift.meta_data.StructMetaData;
import org.apache.thrift.meta_data.FieldValueMetaData;
import java.util.EnumMap;
import org.apache.thrift.scheme.TupleScheme;
import org.apache.thrift.scheme.StandardScheme;
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
import java.util.Iterator;
import java.util.ArrayList;
import java.util.HashMap;
import org.apache.thrift.meta_data.FieldMetaData;
import java.util.List;
import org.apache.thrift.scheme.SchemeFactory;
import org.apache.thrift.scheme.IScheme;
import java.util.Map;
import org.apache.thrift.protocol.TField;
import org.apache.thrift.protocol.TStruct;
import java.io.Serializable;
import org.apache.thrift.TBase;

public class PrincipalPrivilegeSet implements TBase<PrincipalPrivilegeSet, _Fields>, Serializable, Cloneable
{
    private static final TStruct STRUCT_DESC;
    private static final TField USER_PRIVILEGES_FIELD_DESC;
    private static final TField GROUP_PRIVILEGES_FIELD_DESC;
    private static final TField ROLE_PRIVILEGES_FIELD_DESC;
    private static final Map<Class<? extends IScheme>, SchemeFactory> schemes;
    private Map<String, List<PrivilegeGrantInfo>> userPrivileges;
    private Map<String, List<PrivilegeGrantInfo>> groupPrivileges;
    private Map<String, List<PrivilegeGrantInfo>> rolePrivileges;
    public static final Map<_Fields, FieldMetaData> metaDataMap;
    
    public PrincipalPrivilegeSet() {
    }
    
    public PrincipalPrivilegeSet(final Map<String, List<PrivilegeGrantInfo>> userPrivileges, final Map<String, List<PrivilegeGrantInfo>> groupPrivileges, final Map<String, List<PrivilegeGrantInfo>> rolePrivileges) {
        this();
        this.userPrivileges = userPrivileges;
        this.groupPrivileges = groupPrivileges;
        this.rolePrivileges = rolePrivileges;
    }
    
    public PrincipalPrivilegeSet(final PrincipalPrivilegeSet other) {
        if (other.isSetUserPrivileges()) {
            final Map<String, List<PrivilegeGrantInfo>> __this__userPrivileges = new HashMap<String, List<PrivilegeGrantInfo>>();
            for (final Map.Entry<String, List<PrivilegeGrantInfo>> other_element : other.userPrivileges.entrySet()) {
                final String other_element_key = other_element.getKey();
                final List<PrivilegeGrantInfo> other_element_value = other_element.getValue();
                final String __this__userPrivileges_copy_key = other_element_key;
                final List<PrivilegeGrantInfo> __this__userPrivileges_copy_value = new ArrayList<PrivilegeGrantInfo>();
                for (final PrivilegeGrantInfo other_element_value_element : other_element_value) {
                    __this__userPrivileges_copy_value.add(new PrivilegeGrantInfo(other_element_value_element));
                }
                __this__userPrivileges.put(__this__userPrivileges_copy_key, __this__userPrivileges_copy_value);
            }
            this.userPrivileges = __this__userPrivileges;
        }
        if (other.isSetGroupPrivileges()) {
            final Map<String, List<PrivilegeGrantInfo>> __this__groupPrivileges = new HashMap<String, List<PrivilegeGrantInfo>>();
            for (final Map.Entry<String, List<PrivilegeGrantInfo>> other_element : other.groupPrivileges.entrySet()) {
                final String other_element_key = other_element.getKey();
                final List<PrivilegeGrantInfo> other_element_value = other_element.getValue();
                final String __this__groupPrivileges_copy_key = other_element_key;
                final List<PrivilegeGrantInfo> __this__groupPrivileges_copy_value = new ArrayList<PrivilegeGrantInfo>();
                for (final PrivilegeGrantInfo other_element_value_element : other_element_value) {
                    __this__groupPrivileges_copy_value.add(new PrivilegeGrantInfo(other_element_value_element));
                }
                __this__groupPrivileges.put(__this__groupPrivileges_copy_key, __this__groupPrivileges_copy_value);
            }
            this.groupPrivileges = __this__groupPrivileges;
        }
        if (other.isSetRolePrivileges()) {
            final Map<String, List<PrivilegeGrantInfo>> __this__rolePrivileges = new HashMap<String, List<PrivilegeGrantInfo>>();
            for (final Map.Entry<String, List<PrivilegeGrantInfo>> other_element : other.rolePrivileges.entrySet()) {
                final String other_element_key = other_element.getKey();
                final List<PrivilegeGrantInfo> other_element_value = other_element.getValue();
                final String __this__rolePrivileges_copy_key = other_element_key;
                final List<PrivilegeGrantInfo> __this__rolePrivileges_copy_value = new ArrayList<PrivilegeGrantInfo>();
                for (final PrivilegeGrantInfo other_element_value_element : other_element_value) {
                    __this__rolePrivileges_copy_value.add(new PrivilegeGrantInfo(other_element_value_element));
                }
                __this__rolePrivileges.put(__this__rolePrivileges_copy_key, __this__rolePrivileges_copy_value);
            }
            this.rolePrivileges = __this__rolePrivileges;
        }
    }
    
    @Override
    public PrincipalPrivilegeSet deepCopy() {
        return new PrincipalPrivilegeSet(this);
    }
    
    @Override
    public void clear() {
        this.userPrivileges = null;
        this.groupPrivileges = null;
        this.rolePrivileges = null;
    }
    
    public int getUserPrivilegesSize() {
        return (this.userPrivileges == null) ? 0 : this.userPrivileges.size();
    }
    
    public void putToUserPrivileges(final String key, final List<PrivilegeGrantInfo> val) {
        if (this.userPrivileges == null) {
            this.userPrivileges = new HashMap<String, List<PrivilegeGrantInfo>>();
        }
        this.userPrivileges.put(key, val);
    }
    
    public Map<String, List<PrivilegeGrantInfo>> getUserPrivileges() {
        return this.userPrivileges;
    }
    
    public void setUserPrivileges(final Map<String, List<PrivilegeGrantInfo>> userPrivileges) {
        this.userPrivileges = userPrivileges;
    }
    
    public void unsetUserPrivileges() {
        this.userPrivileges = null;
    }
    
    public boolean isSetUserPrivileges() {
        return this.userPrivileges != null;
    }
    
    public void setUserPrivilegesIsSet(final boolean value) {
        if (!value) {
            this.userPrivileges = null;
        }
    }
    
    public int getGroupPrivilegesSize() {
        return (this.groupPrivileges == null) ? 0 : this.groupPrivileges.size();
    }
    
    public void putToGroupPrivileges(final String key, final List<PrivilegeGrantInfo> val) {
        if (this.groupPrivileges == null) {
            this.groupPrivileges = new HashMap<String, List<PrivilegeGrantInfo>>();
        }
        this.groupPrivileges.put(key, val);
    }
    
    public Map<String, List<PrivilegeGrantInfo>> getGroupPrivileges() {
        return this.groupPrivileges;
    }
    
    public void setGroupPrivileges(final Map<String, List<PrivilegeGrantInfo>> groupPrivileges) {
        this.groupPrivileges = groupPrivileges;
    }
    
    public void unsetGroupPrivileges() {
        this.groupPrivileges = null;
    }
    
    public boolean isSetGroupPrivileges() {
        return this.groupPrivileges != null;
    }
    
    public void setGroupPrivilegesIsSet(final boolean value) {
        if (!value) {
            this.groupPrivileges = null;
        }
    }
    
    public int getRolePrivilegesSize() {
        return (this.rolePrivileges == null) ? 0 : this.rolePrivileges.size();
    }
    
    public void putToRolePrivileges(final String key, final List<PrivilegeGrantInfo> val) {
        if (this.rolePrivileges == null) {
            this.rolePrivileges = new HashMap<String, List<PrivilegeGrantInfo>>();
        }
        this.rolePrivileges.put(key, val);
    }
    
    public Map<String, List<PrivilegeGrantInfo>> getRolePrivileges() {
        return this.rolePrivileges;
    }
    
    public void setRolePrivileges(final Map<String, List<PrivilegeGrantInfo>> rolePrivileges) {
        this.rolePrivileges = rolePrivileges;
    }
    
    public void unsetRolePrivileges() {
        this.rolePrivileges = null;
    }
    
    public boolean isSetRolePrivileges() {
        return this.rolePrivileges != null;
    }
    
    public void setRolePrivilegesIsSet(final boolean value) {
        if (!value) {
            this.rolePrivileges = null;
        }
    }
    
    @Override
    public void setFieldValue(final _Fields field, final Object value) {
        switch (field) {
            case USER_PRIVILEGES: {
                if (value == null) {
                    this.unsetUserPrivileges();
                    break;
                }
                this.setUserPrivileges((Map<String, List<PrivilegeGrantInfo>>)value);
                break;
            }
            case GROUP_PRIVILEGES: {
                if (value == null) {
                    this.unsetGroupPrivileges();
                    break;
                }
                this.setGroupPrivileges((Map<String, List<PrivilegeGrantInfo>>)value);
                break;
            }
            case ROLE_PRIVILEGES: {
                if (value == null) {
                    this.unsetRolePrivileges();
                    break;
                }
                this.setRolePrivileges((Map<String, List<PrivilegeGrantInfo>>)value);
                break;
            }
        }
    }
    
    @Override
    public Object getFieldValue(final _Fields field) {
        switch (field) {
            case USER_PRIVILEGES: {
                return this.getUserPrivileges();
            }
            case GROUP_PRIVILEGES: {
                return this.getGroupPrivileges();
            }
            case ROLE_PRIVILEGES: {
                return this.getRolePrivileges();
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
            case USER_PRIVILEGES: {
                return this.isSetUserPrivileges();
            }
            case GROUP_PRIVILEGES: {
                return this.isSetGroupPrivileges();
            }
            case ROLE_PRIVILEGES: {
                return this.isSetRolePrivileges();
            }
            default: {
                throw new IllegalStateException();
            }
        }
    }
    
    @Override
    public boolean equals(final Object that) {
        return that != null && that instanceof PrincipalPrivilegeSet && this.equals((PrincipalPrivilegeSet)that);
    }
    
    public boolean equals(final PrincipalPrivilegeSet that) {
        if (that == null) {
            return false;
        }
        final boolean this_present_userPrivileges = this.isSetUserPrivileges();
        final boolean that_present_userPrivileges = that.isSetUserPrivileges();
        if (this_present_userPrivileges || that_present_userPrivileges) {
            if (!this_present_userPrivileges || !that_present_userPrivileges) {
                return false;
            }
            if (!this.userPrivileges.equals(that.userPrivileges)) {
                return false;
            }
        }
        final boolean this_present_groupPrivileges = this.isSetGroupPrivileges();
        final boolean that_present_groupPrivileges = that.isSetGroupPrivileges();
        if (this_present_groupPrivileges || that_present_groupPrivileges) {
            if (!this_present_groupPrivileges || !that_present_groupPrivileges) {
                return false;
            }
            if (!this.groupPrivileges.equals(that.groupPrivileges)) {
                return false;
            }
        }
        final boolean this_present_rolePrivileges = this.isSetRolePrivileges();
        final boolean that_present_rolePrivileges = that.isSetRolePrivileges();
        if (this_present_rolePrivileges || that_present_rolePrivileges) {
            if (!this_present_rolePrivileges || !that_present_rolePrivileges) {
                return false;
            }
            if (!this.rolePrivileges.equals(that.rolePrivileges)) {
                return false;
            }
        }
        return true;
    }
    
    @Override
    public int hashCode() {
        final HashCodeBuilder builder = new HashCodeBuilder();
        final boolean present_userPrivileges = this.isSetUserPrivileges();
        builder.append(present_userPrivileges);
        if (present_userPrivileges) {
            builder.append(this.userPrivileges);
        }
        final boolean present_groupPrivileges = this.isSetGroupPrivileges();
        builder.append(present_groupPrivileges);
        if (present_groupPrivileges) {
            builder.append(this.groupPrivileges);
        }
        final boolean present_rolePrivileges = this.isSetRolePrivileges();
        builder.append(present_rolePrivileges);
        if (present_rolePrivileges) {
            builder.append(this.rolePrivileges);
        }
        return builder.toHashCode();
    }
    
    @Override
    public int compareTo(final PrincipalPrivilegeSet other) {
        if (!this.getClass().equals(other.getClass())) {
            return this.getClass().getName().compareTo(other.getClass().getName());
        }
        int lastComparison = 0;
        final PrincipalPrivilegeSet typedOther = other;
        lastComparison = Boolean.valueOf(this.isSetUserPrivileges()).compareTo(Boolean.valueOf(typedOther.isSetUserPrivileges()));
        if (lastComparison != 0) {
            return lastComparison;
        }
        if (this.isSetUserPrivileges()) {
            lastComparison = TBaseHelper.compareTo(this.userPrivileges, typedOther.userPrivileges);
            if (lastComparison != 0) {
                return lastComparison;
            }
        }
        lastComparison = Boolean.valueOf(this.isSetGroupPrivileges()).compareTo(Boolean.valueOf(typedOther.isSetGroupPrivileges()));
        if (lastComparison != 0) {
            return lastComparison;
        }
        if (this.isSetGroupPrivileges()) {
            lastComparison = TBaseHelper.compareTo(this.groupPrivileges, typedOther.groupPrivileges);
            if (lastComparison != 0) {
                return lastComparison;
            }
        }
        lastComparison = Boolean.valueOf(this.isSetRolePrivileges()).compareTo(Boolean.valueOf(typedOther.isSetRolePrivileges()));
        if (lastComparison != 0) {
            return lastComparison;
        }
        if (this.isSetRolePrivileges()) {
            lastComparison = TBaseHelper.compareTo(this.rolePrivileges, typedOther.rolePrivileges);
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
        PrincipalPrivilegeSet.schemes.get(iprot.getScheme()).getScheme().read(iprot, this);
    }
    
    @Override
    public void write(final TProtocol oprot) throws TException {
        PrincipalPrivilegeSet.schemes.get(oprot.getScheme()).getScheme().write(oprot, this);
    }
    
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("PrincipalPrivilegeSet(");
        boolean first = true;
        sb.append("userPrivileges:");
        if (this.userPrivileges == null) {
            sb.append("null");
        }
        else {
            sb.append(this.userPrivileges);
        }
        first = false;
        if (!first) {
            sb.append(", ");
        }
        sb.append("groupPrivileges:");
        if (this.groupPrivileges == null) {
            sb.append("null");
        }
        else {
            sb.append(this.groupPrivileges);
        }
        first = false;
        if (!first) {
            sb.append(", ");
        }
        sb.append("rolePrivileges:");
        if (this.rolePrivileges == null) {
            sb.append("null");
        }
        else {
            sb.append(this.rolePrivileges);
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
            this.read(new TCompactProtocol(new TIOStreamTransport(in)));
        }
        catch (TException te) {
            throw new IOException(te);
        }
    }
    
    static {
        STRUCT_DESC = new TStruct("PrincipalPrivilegeSet");
        USER_PRIVILEGES_FIELD_DESC = new TField("userPrivileges", (byte)13, (short)1);
        GROUP_PRIVILEGES_FIELD_DESC = new TField("groupPrivileges", (byte)13, (short)2);
        ROLE_PRIVILEGES_FIELD_DESC = new TField("rolePrivileges", (byte)13, (short)3);
        (schemes = new HashMap<Class<? extends IScheme>, SchemeFactory>()).put(StandardScheme.class, new PrincipalPrivilegeSetStandardSchemeFactory());
        PrincipalPrivilegeSet.schemes.put(TupleScheme.class, new PrincipalPrivilegeSetTupleSchemeFactory());
        final Map<_Fields, FieldMetaData> tmpMap = new EnumMap<_Fields, FieldMetaData>(_Fields.class);
        tmpMap.put(_Fields.USER_PRIVILEGES, new FieldMetaData("userPrivileges", (byte)3, new MapMetaData((byte)13, new FieldValueMetaData((byte)11), new ListMetaData((byte)15, new StructMetaData((byte)12, PrivilegeGrantInfo.class)))));
        tmpMap.put(_Fields.GROUP_PRIVILEGES, new FieldMetaData("groupPrivileges", (byte)3, new MapMetaData((byte)13, new FieldValueMetaData((byte)11), new ListMetaData((byte)15, new StructMetaData((byte)12, PrivilegeGrantInfo.class)))));
        tmpMap.put(_Fields.ROLE_PRIVILEGES, new FieldMetaData("rolePrivileges", (byte)3, new MapMetaData((byte)13, new FieldValueMetaData((byte)11), new ListMetaData((byte)15, new StructMetaData((byte)12, PrivilegeGrantInfo.class)))));
        FieldMetaData.addStructMetaDataMap(PrincipalPrivilegeSet.class, metaDataMap = Collections.unmodifiableMap((Map<? extends _Fields, ? extends FieldMetaData>)tmpMap));
    }
    
    public enum _Fields implements TFieldIdEnum
    {
        USER_PRIVILEGES((short)1, "userPrivileges"), 
        GROUP_PRIVILEGES((short)2, "groupPrivileges"), 
        ROLE_PRIVILEGES((short)3, "rolePrivileges");
        
        private static final Map<String, _Fields> byName;
        private final short _thriftId;
        private final String _fieldName;
        
        public static _Fields findByThriftId(final int fieldId) {
            switch (fieldId) {
                case 1: {
                    return _Fields.USER_PRIVILEGES;
                }
                case 2: {
                    return _Fields.GROUP_PRIVILEGES;
                }
                case 3: {
                    return _Fields.ROLE_PRIVILEGES;
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
    
    private static class PrincipalPrivilegeSetStandardSchemeFactory implements SchemeFactory
    {
        @Override
        public PrincipalPrivilegeSetStandardScheme getScheme() {
            return new PrincipalPrivilegeSetStandardScheme();
        }
    }
    
    private static class PrincipalPrivilegeSetStandardScheme extends StandardScheme<PrincipalPrivilegeSet>
    {
        @Override
        public void read(final TProtocol iprot, final PrincipalPrivilegeSet struct) throws TException {
            iprot.readStructBegin();
            while (true) {
                final TField schemeField = iprot.readFieldBegin();
                if (schemeField.type == 0) {
                    break;
                }
                switch (schemeField.id) {
                    case 1: {
                        if (schemeField.type == 13) {
                            final TMap _map24 = iprot.readMapBegin();
                            struct.userPrivileges = (Map<String, List<PrivilegeGrantInfo>>)new HashMap(2 * _map24.size);
                            for (int _i25 = 0; _i25 < _map24.size; ++_i25) {
                                final String _key26 = iprot.readString();
                                final TList _list28 = iprot.readListBegin();
                                final List<PrivilegeGrantInfo> _val27 = new ArrayList<PrivilegeGrantInfo>(_list28.size);
                                for (int _i26 = 0; _i26 < _list28.size; ++_i26) {
                                    final PrivilegeGrantInfo _elem30 = new PrivilegeGrantInfo();
                                    _elem30.read(iprot);
                                    _val27.add(_elem30);
                                }
                                iprot.readListEnd();
                                struct.userPrivileges.put(_key26, _val27);
                            }
                            iprot.readMapEnd();
                            struct.setUserPrivilegesIsSet(true);
                            break;
                        }
                        TProtocolUtil.skip(iprot, schemeField.type);
                        break;
                    }
                    case 2: {
                        if (schemeField.type == 13) {
                            final TMap _map25 = iprot.readMapBegin();
                            struct.groupPrivileges = (Map<String, List<PrivilegeGrantInfo>>)new HashMap(2 * _map25.size);
                            for (int _i27 = 0; _i27 < _map25.size; ++_i27) {
                                final String _key27 = iprot.readString();
                                final TList _list29 = iprot.readListBegin();
                                final List<PrivilegeGrantInfo> _val28 = new ArrayList<PrivilegeGrantInfo>(_list29.size);
                                for (int _i28 = 0; _i28 < _list29.size; ++_i28) {
                                    final PrivilegeGrantInfo _elem31 = new PrivilegeGrantInfo();
                                    _elem31.read(iprot);
                                    _val28.add(_elem31);
                                }
                                iprot.readListEnd();
                                struct.groupPrivileges.put(_key27, _val28);
                            }
                            iprot.readMapEnd();
                            struct.setGroupPrivilegesIsSet(true);
                            break;
                        }
                        TProtocolUtil.skip(iprot, schemeField.type);
                        break;
                    }
                    case 3: {
                        if (schemeField.type == 13) {
                            final TMap _map26 = iprot.readMapBegin();
                            struct.rolePrivileges = (Map<String, List<PrivilegeGrantInfo>>)new HashMap(2 * _map26.size);
                            for (int _i29 = 0; _i29 < _map26.size; ++_i29) {
                                final String _key28 = iprot.readString();
                                final TList _list30 = iprot.readListBegin();
                                final List<PrivilegeGrantInfo> _val29 = new ArrayList<PrivilegeGrantInfo>(_list30.size);
                                for (int _i30 = 0; _i30 < _list30.size; ++_i30) {
                                    final PrivilegeGrantInfo _elem32 = new PrivilegeGrantInfo();
                                    _elem32.read(iprot);
                                    _val29.add(_elem32);
                                }
                                iprot.readListEnd();
                                struct.rolePrivileges.put(_key28, _val29);
                            }
                            iprot.readMapEnd();
                            struct.setRolePrivilegesIsSet(true);
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
        public void write(final TProtocol oprot, final PrincipalPrivilegeSet struct) throws TException {
            struct.validate();
            oprot.writeStructBegin(PrincipalPrivilegeSet.STRUCT_DESC);
            if (struct.userPrivileges != null) {
                oprot.writeFieldBegin(PrincipalPrivilegeSet.USER_PRIVILEGES_FIELD_DESC);
                oprot.writeMapBegin(new TMap((byte)11, (byte)15, struct.userPrivileges.size()));
                for (final Map.Entry<String, List<PrivilegeGrantInfo>> _iter45 : struct.userPrivileges.entrySet()) {
                    oprot.writeString(_iter45.getKey());
                    oprot.writeListBegin(new TList((byte)12, _iter45.getValue().size()));
                    for (final PrivilegeGrantInfo _iter46 : _iter45.getValue()) {
                        _iter46.write(oprot);
                    }
                    oprot.writeListEnd();
                }
                oprot.writeMapEnd();
                oprot.writeFieldEnd();
            }
            if (struct.groupPrivileges != null) {
                oprot.writeFieldBegin(PrincipalPrivilegeSet.GROUP_PRIVILEGES_FIELD_DESC);
                oprot.writeMapBegin(new TMap((byte)11, (byte)15, struct.groupPrivileges.size()));
                for (final Map.Entry<String, List<PrivilegeGrantInfo>> _iter47 : struct.groupPrivileges.entrySet()) {
                    oprot.writeString(_iter47.getKey());
                    oprot.writeListBegin(new TList((byte)12, _iter47.getValue().size()));
                    for (final PrivilegeGrantInfo _iter48 : _iter47.getValue()) {
                        _iter48.write(oprot);
                    }
                    oprot.writeListEnd();
                }
                oprot.writeMapEnd();
                oprot.writeFieldEnd();
            }
            if (struct.rolePrivileges != null) {
                oprot.writeFieldBegin(PrincipalPrivilegeSet.ROLE_PRIVILEGES_FIELD_DESC);
                oprot.writeMapBegin(new TMap((byte)11, (byte)15, struct.rolePrivileges.size()));
                for (final Map.Entry<String, List<PrivilegeGrantInfo>> _iter49 : struct.rolePrivileges.entrySet()) {
                    oprot.writeString(_iter49.getKey());
                    oprot.writeListBegin(new TList((byte)12, _iter49.getValue().size()));
                    for (final PrivilegeGrantInfo _iter50 : _iter49.getValue()) {
                        _iter50.write(oprot);
                    }
                    oprot.writeListEnd();
                }
                oprot.writeMapEnd();
                oprot.writeFieldEnd();
            }
            oprot.writeFieldStop();
            oprot.writeStructEnd();
        }
    }
    
    private static class PrincipalPrivilegeSetTupleSchemeFactory implements SchemeFactory
    {
        @Override
        public PrincipalPrivilegeSetTupleScheme getScheme() {
            return new PrincipalPrivilegeSetTupleScheme();
        }
    }
    
    private static class PrincipalPrivilegeSetTupleScheme extends TupleScheme<PrincipalPrivilegeSet>
    {
        @Override
        public void write(final TProtocol prot, final PrincipalPrivilegeSet struct) throws TException {
            final TTupleProtocol oprot = (TTupleProtocol)prot;
            final BitSet optionals = new BitSet();
            if (struct.isSetUserPrivileges()) {
                optionals.set(0);
            }
            if (struct.isSetGroupPrivileges()) {
                optionals.set(1);
            }
            if (struct.isSetRolePrivileges()) {
                optionals.set(2);
            }
            oprot.writeBitSet(optionals, 3);
            if (struct.isSetUserPrivileges()) {
                oprot.writeI32(struct.userPrivileges.size());
                for (final Map.Entry<String, List<PrivilegeGrantInfo>> _iter51 : struct.userPrivileges.entrySet()) {
                    oprot.writeString(_iter51.getKey());
                    oprot.writeI32(_iter51.getValue().size());
                    for (final PrivilegeGrantInfo _iter52 : _iter51.getValue()) {
                        _iter52.write(oprot);
                    }
                }
            }
            if (struct.isSetGroupPrivileges()) {
                oprot.writeI32(struct.groupPrivileges.size());
                for (final Map.Entry<String, List<PrivilegeGrantInfo>> _iter53 : struct.groupPrivileges.entrySet()) {
                    oprot.writeString(_iter53.getKey());
                    oprot.writeI32(_iter53.getValue().size());
                    for (final PrivilegeGrantInfo _iter54 : _iter53.getValue()) {
                        _iter54.write(oprot);
                    }
                }
            }
            if (struct.isSetRolePrivileges()) {
                oprot.writeI32(struct.rolePrivileges.size());
                for (final Map.Entry<String, List<PrivilegeGrantInfo>> _iter55 : struct.rolePrivileges.entrySet()) {
                    oprot.writeString(_iter55.getKey());
                    oprot.writeI32(_iter55.getValue().size());
                    for (final PrivilegeGrantInfo _iter56 : _iter55.getValue()) {
                        _iter56.write(oprot);
                    }
                }
            }
        }
        
        @Override
        public void read(final TProtocol prot, final PrincipalPrivilegeSet struct) throws TException {
            final TTupleProtocol iprot = (TTupleProtocol)prot;
            final BitSet incoming = iprot.readBitSet(3);
            if (incoming.get(0)) {
                final TMap _map57 = new TMap((byte)11, (byte)15, iprot.readI32());
                struct.userPrivileges = (Map<String, List<PrivilegeGrantInfo>>)new HashMap(2 * _map57.size);
                for (int _i58 = 0; _i58 < _map57.size; ++_i58) {
                    final String _key59 = iprot.readString();
                    final TList _list61 = new TList((byte)12, iprot.readI32());
                    final List<PrivilegeGrantInfo> _val60 = new ArrayList<PrivilegeGrantInfo>(_list61.size);
                    for (int _i59 = 0; _i59 < _list61.size; ++_i59) {
                        final PrivilegeGrantInfo _elem63 = new PrivilegeGrantInfo();
                        _elem63.read(iprot);
                        _val60.add(_elem63);
                    }
                    struct.userPrivileges.put(_key59, _val60);
                }
                struct.setUserPrivilegesIsSet(true);
            }
            if (incoming.get(1)) {
                final TMap _map58 = new TMap((byte)11, (byte)15, iprot.readI32());
                struct.groupPrivileges = (Map<String, List<PrivilegeGrantInfo>>)new HashMap(2 * _map58.size);
                for (int _i60 = 0; _i60 < _map58.size; ++_i60) {
                    final String _key60 = iprot.readString();
                    final TList _list62 = new TList((byte)12, iprot.readI32());
                    final List<PrivilegeGrantInfo> _val61 = new ArrayList<PrivilegeGrantInfo>(_list62.size);
                    for (int _i61 = 0; _i61 < _list62.size; ++_i61) {
                        final PrivilegeGrantInfo _elem64 = new PrivilegeGrantInfo();
                        _elem64.read(iprot);
                        _val61.add(_elem64);
                    }
                    struct.groupPrivileges.put(_key60, _val61);
                }
                struct.setGroupPrivilegesIsSet(true);
            }
            if (incoming.get(2)) {
                final TMap _map59 = new TMap((byte)11, (byte)15, iprot.readI32());
                struct.rolePrivileges = (Map<String, List<PrivilegeGrantInfo>>)new HashMap(2 * _map59.size);
                for (int _i62 = 0; _i62 < _map59.size; ++_i62) {
                    final String _key61 = iprot.readString();
                    final TList _list63 = new TList((byte)12, iprot.readI32());
                    final List<PrivilegeGrantInfo> _val62 = new ArrayList<PrivilegeGrantInfo>(_list63.size);
                    for (int _i63 = 0; _i63 < _list63.size; ++_i63) {
                        final PrivilegeGrantInfo _elem65 = new PrivilegeGrantInfo();
                        _elem65.read(iprot);
                        _val62.add(_elem65);
                    }
                    struct.rolePrivileges.put(_key61, _val62);
                }
                struct.setRolePrivilegesIsSet(true);
            }
        }
    }
}
