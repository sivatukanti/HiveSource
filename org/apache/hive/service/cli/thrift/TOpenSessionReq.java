// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hive.service.cli.thrift;

import java.util.BitSet;
import org.apache.thrift.protocol.TTupleProtocol;
import org.apache.thrift.protocol.TMap;
import org.apache.thrift.protocol.TProtocolUtil;
import java.util.EnumSet;
import java.util.Collections;
import org.apache.thrift.meta_data.MapMetaData;
import org.apache.thrift.meta_data.FieldValueMetaData;
import org.apache.thrift.TEnum;
import org.apache.thrift.meta_data.EnumMetaData;
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
import org.apache.thrift.protocol.TProtocolException;
import org.apache.thrift.TException;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.TBaseHelper;
import org.apache.commons.lang.builder.HashCodeBuilder;
import java.util.Iterator;
import java.util.HashMap;
import org.apache.thrift.meta_data.FieldMetaData;
import org.apache.thrift.scheme.SchemeFactory;
import org.apache.thrift.scheme.IScheme;
import java.util.Map;
import org.apache.thrift.protocol.TField;
import org.apache.thrift.protocol.TStruct;
import java.io.Serializable;
import org.apache.thrift.TBase;

public class TOpenSessionReq implements TBase<TOpenSessionReq, _Fields>, Serializable, Cloneable
{
    private static final TStruct STRUCT_DESC;
    private static final TField CLIENT_PROTOCOL_FIELD_DESC;
    private static final TField USERNAME_FIELD_DESC;
    private static final TField PASSWORD_FIELD_DESC;
    private static final TField CONFIGURATION_FIELD_DESC;
    private static final Map<Class<? extends IScheme>, SchemeFactory> schemes;
    private TProtocolVersion client_protocol;
    private String username;
    private String password;
    private Map<String, String> configuration;
    private _Fields[] optionals;
    public static final Map<_Fields, FieldMetaData> metaDataMap;
    
    public TOpenSessionReq() {
        this.optionals = new _Fields[] { _Fields.USERNAME, _Fields.PASSWORD, _Fields.CONFIGURATION };
        this.client_protocol = TProtocolVersion.HIVE_CLI_SERVICE_PROTOCOL_V8;
    }
    
    public TOpenSessionReq(final TProtocolVersion client_protocol) {
        this();
        this.client_protocol = client_protocol;
    }
    
    public TOpenSessionReq(final TOpenSessionReq other) {
        this.optionals = new _Fields[] { _Fields.USERNAME, _Fields.PASSWORD, _Fields.CONFIGURATION };
        if (other.isSetClient_protocol()) {
            this.client_protocol = other.client_protocol;
        }
        if (other.isSetUsername()) {
            this.username = other.username;
        }
        if (other.isSetPassword()) {
            this.password = other.password;
        }
        if (other.isSetConfiguration()) {
            final Map<String, String> __this__configuration = new HashMap<String, String>();
            for (final Map.Entry<String, String> other_element : other.configuration.entrySet()) {
                final String other_element_key = other_element.getKey();
                final String other_element_value = other_element.getValue();
                final String __this__configuration_copy_key = other_element_key;
                final String __this__configuration_copy_value = other_element_value;
                __this__configuration.put(__this__configuration_copy_key, __this__configuration_copy_value);
            }
            this.configuration = __this__configuration;
        }
    }
    
    @Override
    public TOpenSessionReq deepCopy() {
        return new TOpenSessionReq(this);
    }
    
    @Override
    public void clear() {
        this.client_protocol = TProtocolVersion.HIVE_CLI_SERVICE_PROTOCOL_V8;
        this.username = null;
        this.password = null;
        this.configuration = null;
    }
    
    public TProtocolVersion getClient_protocol() {
        return this.client_protocol;
    }
    
    public void setClient_protocol(final TProtocolVersion client_protocol) {
        this.client_protocol = client_protocol;
    }
    
    public void unsetClient_protocol() {
        this.client_protocol = null;
    }
    
    public boolean isSetClient_protocol() {
        return this.client_protocol != null;
    }
    
    public void setClient_protocolIsSet(final boolean value) {
        if (!value) {
            this.client_protocol = null;
        }
    }
    
    public String getUsername() {
        return this.username;
    }
    
    public void setUsername(final String username) {
        this.username = username;
    }
    
    public void unsetUsername() {
        this.username = null;
    }
    
    public boolean isSetUsername() {
        return this.username != null;
    }
    
    public void setUsernameIsSet(final boolean value) {
        if (!value) {
            this.username = null;
        }
    }
    
    public String getPassword() {
        return this.password;
    }
    
    public void setPassword(final String password) {
        this.password = password;
    }
    
    public void unsetPassword() {
        this.password = null;
    }
    
    public boolean isSetPassword() {
        return this.password != null;
    }
    
    public void setPasswordIsSet(final boolean value) {
        if (!value) {
            this.password = null;
        }
    }
    
    public int getConfigurationSize() {
        return (this.configuration == null) ? 0 : this.configuration.size();
    }
    
    public void putToConfiguration(final String key, final String val) {
        if (this.configuration == null) {
            this.configuration = new HashMap<String, String>();
        }
        this.configuration.put(key, val);
    }
    
    public Map<String, String> getConfiguration() {
        return this.configuration;
    }
    
    public void setConfiguration(final Map<String, String> configuration) {
        this.configuration = configuration;
    }
    
    public void unsetConfiguration() {
        this.configuration = null;
    }
    
    public boolean isSetConfiguration() {
        return this.configuration != null;
    }
    
    public void setConfigurationIsSet(final boolean value) {
        if (!value) {
            this.configuration = null;
        }
    }
    
    @Override
    public void setFieldValue(final _Fields field, final Object value) {
        switch (field) {
            case CLIENT_PROTOCOL: {
                if (value == null) {
                    this.unsetClient_protocol();
                    break;
                }
                this.setClient_protocol((TProtocolVersion)value);
                break;
            }
            case USERNAME: {
                if (value == null) {
                    this.unsetUsername();
                    break;
                }
                this.setUsername((String)value);
                break;
            }
            case PASSWORD: {
                if (value == null) {
                    this.unsetPassword();
                    break;
                }
                this.setPassword((String)value);
                break;
            }
            case CONFIGURATION: {
                if (value == null) {
                    this.unsetConfiguration();
                    break;
                }
                this.setConfiguration((Map<String, String>)value);
                break;
            }
        }
    }
    
    @Override
    public Object getFieldValue(final _Fields field) {
        switch (field) {
            case CLIENT_PROTOCOL: {
                return this.getClient_protocol();
            }
            case USERNAME: {
                return this.getUsername();
            }
            case PASSWORD: {
                return this.getPassword();
            }
            case CONFIGURATION: {
                return this.getConfiguration();
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
            case CLIENT_PROTOCOL: {
                return this.isSetClient_protocol();
            }
            case USERNAME: {
                return this.isSetUsername();
            }
            case PASSWORD: {
                return this.isSetPassword();
            }
            case CONFIGURATION: {
                return this.isSetConfiguration();
            }
            default: {
                throw new IllegalStateException();
            }
        }
    }
    
    @Override
    public boolean equals(final Object that) {
        return that != null && that instanceof TOpenSessionReq && this.equals((TOpenSessionReq)that);
    }
    
    public boolean equals(final TOpenSessionReq that) {
        if (that == null) {
            return false;
        }
        final boolean this_present_client_protocol = this.isSetClient_protocol();
        final boolean that_present_client_protocol = that.isSetClient_protocol();
        if (this_present_client_protocol || that_present_client_protocol) {
            if (!this_present_client_protocol || !that_present_client_protocol) {
                return false;
            }
            if (!this.client_protocol.equals(that.client_protocol)) {
                return false;
            }
        }
        final boolean this_present_username = this.isSetUsername();
        final boolean that_present_username = that.isSetUsername();
        if (this_present_username || that_present_username) {
            if (!this_present_username || !that_present_username) {
                return false;
            }
            if (!this.username.equals(that.username)) {
                return false;
            }
        }
        final boolean this_present_password = this.isSetPassword();
        final boolean that_present_password = that.isSetPassword();
        if (this_present_password || that_present_password) {
            if (!this_present_password || !that_present_password) {
                return false;
            }
            if (!this.password.equals(that.password)) {
                return false;
            }
        }
        final boolean this_present_configuration = this.isSetConfiguration();
        final boolean that_present_configuration = that.isSetConfiguration();
        if (this_present_configuration || that_present_configuration) {
            if (!this_present_configuration || !that_present_configuration) {
                return false;
            }
            if (!this.configuration.equals(that.configuration)) {
                return false;
            }
        }
        return true;
    }
    
    @Override
    public int hashCode() {
        final HashCodeBuilder builder = new HashCodeBuilder();
        final boolean present_client_protocol = this.isSetClient_protocol();
        builder.append(present_client_protocol);
        if (present_client_protocol) {
            builder.append(this.client_protocol.getValue());
        }
        final boolean present_username = this.isSetUsername();
        builder.append(present_username);
        if (present_username) {
            builder.append(this.username);
        }
        final boolean present_password = this.isSetPassword();
        builder.append(present_password);
        if (present_password) {
            builder.append(this.password);
        }
        final boolean present_configuration = this.isSetConfiguration();
        builder.append(present_configuration);
        if (present_configuration) {
            builder.append(this.configuration);
        }
        return builder.toHashCode();
    }
    
    @Override
    public int compareTo(final TOpenSessionReq other) {
        if (!this.getClass().equals(other.getClass())) {
            return this.getClass().getName().compareTo(other.getClass().getName());
        }
        int lastComparison = 0;
        final TOpenSessionReq typedOther = other;
        lastComparison = Boolean.valueOf(this.isSetClient_protocol()).compareTo(Boolean.valueOf(typedOther.isSetClient_protocol()));
        if (lastComparison != 0) {
            return lastComparison;
        }
        if (this.isSetClient_protocol()) {
            lastComparison = TBaseHelper.compareTo(this.client_protocol, typedOther.client_protocol);
            if (lastComparison != 0) {
                return lastComparison;
            }
        }
        lastComparison = Boolean.valueOf(this.isSetUsername()).compareTo(Boolean.valueOf(typedOther.isSetUsername()));
        if (lastComparison != 0) {
            return lastComparison;
        }
        if (this.isSetUsername()) {
            lastComparison = TBaseHelper.compareTo(this.username, typedOther.username);
            if (lastComparison != 0) {
                return lastComparison;
            }
        }
        lastComparison = Boolean.valueOf(this.isSetPassword()).compareTo(Boolean.valueOf(typedOther.isSetPassword()));
        if (lastComparison != 0) {
            return lastComparison;
        }
        if (this.isSetPassword()) {
            lastComparison = TBaseHelper.compareTo(this.password, typedOther.password);
            if (lastComparison != 0) {
                return lastComparison;
            }
        }
        lastComparison = Boolean.valueOf(this.isSetConfiguration()).compareTo(Boolean.valueOf(typedOther.isSetConfiguration()));
        if (lastComparison != 0) {
            return lastComparison;
        }
        if (this.isSetConfiguration()) {
            lastComparison = TBaseHelper.compareTo(this.configuration, typedOther.configuration);
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
        TOpenSessionReq.schemes.get(iprot.getScheme()).getScheme().read(iprot, this);
    }
    
    @Override
    public void write(final TProtocol oprot) throws TException {
        TOpenSessionReq.schemes.get(oprot.getScheme()).getScheme().write(oprot, this);
    }
    
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("TOpenSessionReq(");
        boolean first = true;
        sb.append("client_protocol:");
        if (this.client_protocol == null) {
            sb.append("null");
        }
        else {
            sb.append(this.client_protocol);
        }
        first = false;
        if (this.isSetUsername()) {
            if (!first) {
                sb.append(", ");
            }
            sb.append("username:");
            if (this.username == null) {
                sb.append("null");
            }
            else {
                sb.append(this.username);
            }
            first = false;
        }
        if (this.isSetPassword()) {
            if (!first) {
                sb.append(", ");
            }
            sb.append("password:");
            if (this.password == null) {
                sb.append("null");
            }
            else {
                sb.append(this.password);
            }
            first = false;
        }
        if (this.isSetConfiguration()) {
            if (!first) {
                sb.append(", ");
            }
            sb.append("configuration:");
            if (this.configuration == null) {
                sb.append("null");
            }
            else {
                sb.append(this.configuration);
            }
            first = false;
        }
        sb.append(")");
        return sb.toString();
    }
    
    public void validate() throws TException {
        if (!this.isSetClient_protocol()) {
            throw new TProtocolException("Required field 'client_protocol' is unset! Struct:" + this.toString());
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
        STRUCT_DESC = new TStruct("TOpenSessionReq");
        CLIENT_PROTOCOL_FIELD_DESC = new TField("client_protocol", (byte)8, (short)1);
        USERNAME_FIELD_DESC = new TField("username", (byte)11, (short)2);
        PASSWORD_FIELD_DESC = new TField("password", (byte)11, (short)3);
        CONFIGURATION_FIELD_DESC = new TField("configuration", (byte)13, (short)4);
        (schemes = new HashMap<Class<? extends IScheme>, SchemeFactory>()).put(StandardScheme.class, new TOpenSessionReqStandardSchemeFactory());
        TOpenSessionReq.schemes.put(TupleScheme.class, new TOpenSessionReqTupleSchemeFactory());
        final Map<_Fields, FieldMetaData> tmpMap = new EnumMap<_Fields, FieldMetaData>(_Fields.class);
        tmpMap.put(_Fields.CLIENT_PROTOCOL, new FieldMetaData("client_protocol", (byte)1, new EnumMetaData((byte)16, TProtocolVersion.class)));
        tmpMap.put(_Fields.USERNAME, new FieldMetaData("username", (byte)2, new FieldValueMetaData((byte)11)));
        tmpMap.put(_Fields.PASSWORD, new FieldMetaData("password", (byte)2, new FieldValueMetaData((byte)11)));
        tmpMap.put(_Fields.CONFIGURATION, new FieldMetaData("configuration", (byte)2, new MapMetaData((byte)13, new FieldValueMetaData((byte)11), new FieldValueMetaData((byte)11))));
        FieldMetaData.addStructMetaDataMap(TOpenSessionReq.class, metaDataMap = Collections.unmodifiableMap((Map<? extends _Fields, ? extends FieldMetaData>)tmpMap));
    }
    
    public enum _Fields implements TFieldIdEnum
    {
        CLIENT_PROTOCOL((short)1, "client_protocol"), 
        USERNAME((short)2, "username"), 
        PASSWORD((short)3, "password"), 
        CONFIGURATION((short)4, "configuration");
        
        private static final Map<String, _Fields> byName;
        private final short _thriftId;
        private final String _fieldName;
        
        public static _Fields findByThriftId(final int fieldId) {
            switch (fieldId) {
                case 1: {
                    return _Fields.CLIENT_PROTOCOL;
                }
                case 2: {
                    return _Fields.USERNAME;
                }
                case 3: {
                    return _Fields.PASSWORD;
                }
                case 4: {
                    return _Fields.CONFIGURATION;
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
    
    private static class TOpenSessionReqStandardSchemeFactory implements SchemeFactory
    {
        @Override
        public TOpenSessionReqStandardScheme getScheme() {
            return new TOpenSessionReqStandardScheme();
        }
    }
    
    private static class TOpenSessionReqStandardScheme extends StandardScheme<TOpenSessionReq>
    {
        @Override
        public void read(final TProtocol iprot, final TOpenSessionReq struct) throws TException {
            iprot.readStructBegin();
            while (true) {
                final TField schemeField = iprot.readFieldBegin();
                if (schemeField.type == 0) {
                    break;
                }
                switch (schemeField.id) {
                    case 1: {
                        if (schemeField.type == 8) {
                            struct.client_protocol = TProtocolVersion.findByValue(iprot.readI32());
                            struct.setClient_protocolIsSet(true);
                            break;
                        }
                        TProtocolUtil.skip(iprot, schemeField.type);
                        break;
                    }
                    case 2: {
                        if (schemeField.type == 11) {
                            struct.username = iprot.readString();
                            struct.setUsernameIsSet(true);
                            break;
                        }
                        TProtocolUtil.skip(iprot, schemeField.type);
                        break;
                    }
                    case 3: {
                        if (schemeField.type == 11) {
                            struct.password = iprot.readString();
                            struct.setPasswordIsSet(true);
                            break;
                        }
                        TProtocolUtil.skip(iprot, schemeField.type);
                        break;
                    }
                    case 4: {
                        if (schemeField.type == 13) {
                            final TMap _map142 = iprot.readMapBegin();
                            struct.configuration = (Map<String, String>)new HashMap(2 * _map142.size);
                            for (int _i143 = 0; _i143 < _map142.size; ++_i143) {
                                final String _key144 = iprot.readString();
                                final String _val145 = iprot.readString();
                                struct.configuration.put(_key144, _val145);
                            }
                            iprot.readMapEnd();
                            struct.setConfigurationIsSet(true);
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
        public void write(final TProtocol oprot, final TOpenSessionReq struct) throws TException {
            struct.validate();
            oprot.writeStructBegin(TOpenSessionReq.STRUCT_DESC);
            if (struct.client_protocol != null) {
                oprot.writeFieldBegin(TOpenSessionReq.CLIENT_PROTOCOL_FIELD_DESC);
                oprot.writeI32(struct.client_protocol.getValue());
                oprot.writeFieldEnd();
            }
            if (struct.username != null && struct.isSetUsername()) {
                oprot.writeFieldBegin(TOpenSessionReq.USERNAME_FIELD_DESC);
                oprot.writeString(struct.username);
                oprot.writeFieldEnd();
            }
            if (struct.password != null && struct.isSetPassword()) {
                oprot.writeFieldBegin(TOpenSessionReq.PASSWORD_FIELD_DESC);
                oprot.writeString(struct.password);
                oprot.writeFieldEnd();
            }
            if (struct.configuration != null && struct.isSetConfiguration()) {
                oprot.writeFieldBegin(TOpenSessionReq.CONFIGURATION_FIELD_DESC);
                oprot.writeMapBegin(new TMap((byte)11, (byte)11, struct.configuration.size()));
                for (final Map.Entry<String, String> _iter146 : struct.configuration.entrySet()) {
                    oprot.writeString(_iter146.getKey());
                    oprot.writeString(_iter146.getValue());
                }
                oprot.writeMapEnd();
                oprot.writeFieldEnd();
            }
            oprot.writeFieldStop();
            oprot.writeStructEnd();
        }
    }
    
    private static class TOpenSessionReqTupleSchemeFactory implements SchemeFactory
    {
        @Override
        public TOpenSessionReqTupleScheme getScheme() {
            return new TOpenSessionReqTupleScheme();
        }
    }
    
    private static class TOpenSessionReqTupleScheme extends TupleScheme<TOpenSessionReq>
    {
        @Override
        public void write(final TProtocol prot, final TOpenSessionReq struct) throws TException {
            final TTupleProtocol oprot = (TTupleProtocol)prot;
            oprot.writeI32(struct.client_protocol.getValue());
            final BitSet optionals = new BitSet();
            if (struct.isSetUsername()) {
                optionals.set(0);
            }
            if (struct.isSetPassword()) {
                optionals.set(1);
            }
            if (struct.isSetConfiguration()) {
                optionals.set(2);
            }
            oprot.writeBitSet(optionals, 3);
            if (struct.isSetUsername()) {
                oprot.writeString(struct.username);
            }
            if (struct.isSetPassword()) {
                oprot.writeString(struct.password);
            }
            if (struct.isSetConfiguration()) {
                oprot.writeI32(struct.configuration.size());
                for (final Map.Entry<String, String> _iter147 : struct.configuration.entrySet()) {
                    oprot.writeString(_iter147.getKey());
                    oprot.writeString(_iter147.getValue());
                }
            }
        }
        
        @Override
        public void read(final TProtocol prot, final TOpenSessionReq struct) throws TException {
            final TTupleProtocol iprot = (TTupleProtocol)prot;
            struct.client_protocol = TProtocolVersion.findByValue(iprot.readI32());
            struct.setClient_protocolIsSet(true);
            final BitSet incoming = iprot.readBitSet(3);
            if (incoming.get(0)) {
                struct.username = iprot.readString();
                struct.setUsernameIsSet(true);
            }
            if (incoming.get(1)) {
                struct.password = iprot.readString();
                struct.setPasswordIsSet(true);
            }
            if (incoming.get(2)) {
                final TMap _map148 = new TMap((byte)11, (byte)11, iprot.readI32());
                struct.configuration = (Map<String, String>)new HashMap(2 * _map148.size);
                for (int _i149 = 0; _i149 < _map148.size; ++_i149) {
                    final String _key150 = iprot.readString();
                    final String _val151 = iprot.readString();
                    struct.configuration.put(_key150, _val151);
                }
                struct.setConfigurationIsSet(true);
            }
        }
    }
}
