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
import org.apache.thrift.TEnum;
import org.apache.thrift.meta_data.EnumMetaData;
import org.apache.thrift.meta_data.FieldValueMetaData;
import org.apache.thrift.meta_data.StructMetaData;
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

public class TOpenSessionResp implements TBase<TOpenSessionResp, _Fields>, Serializable, Cloneable
{
    private static final TStruct STRUCT_DESC;
    private static final TField STATUS_FIELD_DESC;
    private static final TField SERVER_PROTOCOL_VERSION_FIELD_DESC;
    private static final TField SESSION_HANDLE_FIELD_DESC;
    private static final TField CONFIGURATION_FIELD_DESC;
    private static final Map<Class<? extends IScheme>, SchemeFactory> schemes;
    private TStatus status;
    private TProtocolVersion serverProtocolVersion;
    private TSessionHandle sessionHandle;
    private Map<String, String> configuration;
    private _Fields[] optionals;
    public static final Map<_Fields, FieldMetaData> metaDataMap;
    
    public TOpenSessionResp() {
        this.optionals = new _Fields[] { _Fields.SESSION_HANDLE, _Fields.CONFIGURATION };
        this.serverProtocolVersion = TProtocolVersion.HIVE_CLI_SERVICE_PROTOCOL_V8;
    }
    
    public TOpenSessionResp(final TStatus status, final TProtocolVersion serverProtocolVersion) {
        this();
        this.status = status;
        this.serverProtocolVersion = serverProtocolVersion;
    }
    
    public TOpenSessionResp(final TOpenSessionResp other) {
        this.optionals = new _Fields[] { _Fields.SESSION_HANDLE, _Fields.CONFIGURATION };
        if (other.isSetStatus()) {
            this.status = new TStatus(other.status);
        }
        if (other.isSetServerProtocolVersion()) {
            this.serverProtocolVersion = other.serverProtocolVersion;
        }
        if (other.isSetSessionHandle()) {
            this.sessionHandle = new TSessionHandle(other.sessionHandle);
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
    public TOpenSessionResp deepCopy() {
        return new TOpenSessionResp(this);
    }
    
    @Override
    public void clear() {
        this.status = null;
        this.serverProtocolVersion = TProtocolVersion.HIVE_CLI_SERVICE_PROTOCOL_V8;
        this.sessionHandle = null;
        this.configuration = null;
    }
    
    public TStatus getStatus() {
        return this.status;
    }
    
    public void setStatus(final TStatus status) {
        this.status = status;
    }
    
    public void unsetStatus() {
        this.status = null;
    }
    
    public boolean isSetStatus() {
        return this.status != null;
    }
    
    public void setStatusIsSet(final boolean value) {
        if (!value) {
            this.status = null;
        }
    }
    
    public TProtocolVersion getServerProtocolVersion() {
        return this.serverProtocolVersion;
    }
    
    public void setServerProtocolVersion(final TProtocolVersion serverProtocolVersion) {
        this.serverProtocolVersion = serverProtocolVersion;
    }
    
    public void unsetServerProtocolVersion() {
        this.serverProtocolVersion = null;
    }
    
    public boolean isSetServerProtocolVersion() {
        return this.serverProtocolVersion != null;
    }
    
    public void setServerProtocolVersionIsSet(final boolean value) {
        if (!value) {
            this.serverProtocolVersion = null;
        }
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
            case STATUS: {
                if (value == null) {
                    this.unsetStatus();
                    break;
                }
                this.setStatus((TStatus)value);
                break;
            }
            case SERVER_PROTOCOL_VERSION: {
                if (value == null) {
                    this.unsetServerProtocolVersion();
                    break;
                }
                this.setServerProtocolVersion((TProtocolVersion)value);
                break;
            }
            case SESSION_HANDLE: {
                if (value == null) {
                    this.unsetSessionHandle();
                    break;
                }
                this.setSessionHandle((TSessionHandle)value);
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
            case STATUS: {
                return this.getStatus();
            }
            case SERVER_PROTOCOL_VERSION: {
                return this.getServerProtocolVersion();
            }
            case SESSION_HANDLE: {
                return this.getSessionHandle();
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
            case STATUS: {
                return this.isSetStatus();
            }
            case SERVER_PROTOCOL_VERSION: {
                return this.isSetServerProtocolVersion();
            }
            case SESSION_HANDLE: {
                return this.isSetSessionHandle();
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
        return that != null && that instanceof TOpenSessionResp && this.equals((TOpenSessionResp)that);
    }
    
    public boolean equals(final TOpenSessionResp that) {
        if (that == null) {
            return false;
        }
        final boolean this_present_status = this.isSetStatus();
        final boolean that_present_status = that.isSetStatus();
        if (this_present_status || that_present_status) {
            if (!this_present_status || !that_present_status) {
                return false;
            }
            if (!this.status.equals(that.status)) {
                return false;
            }
        }
        final boolean this_present_serverProtocolVersion = this.isSetServerProtocolVersion();
        final boolean that_present_serverProtocolVersion = that.isSetServerProtocolVersion();
        if (this_present_serverProtocolVersion || that_present_serverProtocolVersion) {
            if (!this_present_serverProtocolVersion || !that_present_serverProtocolVersion) {
                return false;
            }
            if (!this.serverProtocolVersion.equals(that.serverProtocolVersion)) {
                return false;
            }
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
        final boolean present_status = this.isSetStatus();
        builder.append(present_status);
        if (present_status) {
            builder.append(this.status);
        }
        final boolean present_serverProtocolVersion = this.isSetServerProtocolVersion();
        builder.append(present_serverProtocolVersion);
        if (present_serverProtocolVersion) {
            builder.append(this.serverProtocolVersion.getValue());
        }
        final boolean present_sessionHandle = this.isSetSessionHandle();
        builder.append(present_sessionHandle);
        if (present_sessionHandle) {
            builder.append(this.sessionHandle);
        }
        final boolean present_configuration = this.isSetConfiguration();
        builder.append(present_configuration);
        if (present_configuration) {
            builder.append(this.configuration);
        }
        return builder.toHashCode();
    }
    
    @Override
    public int compareTo(final TOpenSessionResp other) {
        if (!this.getClass().equals(other.getClass())) {
            return this.getClass().getName().compareTo(other.getClass().getName());
        }
        int lastComparison = 0;
        final TOpenSessionResp typedOther = other;
        lastComparison = Boolean.valueOf(this.isSetStatus()).compareTo(Boolean.valueOf(typedOther.isSetStatus()));
        if (lastComparison != 0) {
            return lastComparison;
        }
        if (this.isSetStatus()) {
            lastComparison = TBaseHelper.compareTo(this.status, typedOther.status);
            if (lastComparison != 0) {
                return lastComparison;
            }
        }
        lastComparison = Boolean.valueOf(this.isSetServerProtocolVersion()).compareTo(Boolean.valueOf(typedOther.isSetServerProtocolVersion()));
        if (lastComparison != 0) {
            return lastComparison;
        }
        if (this.isSetServerProtocolVersion()) {
            lastComparison = TBaseHelper.compareTo(this.serverProtocolVersion, typedOther.serverProtocolVersion);
            if (lastComparison != 0) {
                return lastComparison;
            }
        }
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
        TOpenSessionResp.schemes.get(iprot.getScheme()).getScheme().read(iprot, this);
    }
    
    @Override
    public void write(final TProtocol oprot) throws TException {
        TOpenSessionResp.schemes.get(oprot.getScheme()).getScheme().write(oprot, this);
    }
    
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("TOpenSessionResp(");
        boolean first = true;
        sb.append("status:");
        if (this.status == null) {
            sb.append("null");
        }
        else {
            sb.append(this.status);
        }
        first = false;
        if (!first) {
            sb.append(", ");
        }
        sb.append("serverProtocolVersion:");
        if (this.serverProtocolVersion == null) {
            sb.append("null");
        }
        else {
            sb.append(this.serverProtocolVersion);
        }
        first = false;
        if (this.isSetSessionHandle()) {
            if (!first) {
                sb.append(", ");
            }
            sb.append("sessionHandle:");
            if (this.sessionHandle == null) {
                sb.append("null");
            }
            else {
                sb.append(this.sessionHandle);
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
        if (!this.isSetStatus()) {
            throw new TProtocolException("Required field 'status' is unset! Struct:" + this.toString());
        }
        if (!this.isSetServerProtocolVersion()) {
            throw new TProtocolException("Required field 'serverProtocolVersion' is unset! Struct:" + this.toString());
        }
        if (this.status != null) {
            this.status.validate();
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
        STRUCT_DESC = new TStruct("TOpenSessionResp");
        STATUS_FIELD_DESC = new TField("status", (byte)12, (short)1);
        SERVER_PROTOCOL_VERSION_FIELD_DESC = new TField("serverProtocolVersion", (byte)8, (short)2);
        SESSION_HANDLE_FIELD_DESC = new TField("sessionHandle", (byte)12, (short)3);
        CONFIGURATION_FIELD_DESC = new TField("configuration", (byte)13, (short)4);
        (schemes = new HashMap<Class<? extends IScheme>, SchemeFactory>()).put(StandardScheme.class, new TOpenSessionRespStandardSchemeFactory());
        TOpenSessionResp.schemes.put(TupleScheme.class, new TOpenSessionRespTupleSchemeFactory());
        final Map<_Fields, FieldMetaData> tmpMap = new EnumMap<_Fields, FieldMetaData>(_Fields.class);
        tmpMap.put(_Fields.STATUS, new FieldMetaData("status", (byte)1, new StructMetaData((byte)12, TStatus.class)));
        tmpMap.put(_Fields.SERVER_PROTOCOL_VERSION, new FieldMetaData("serverProtocolVersion", (byte)1, new EnumMetaData((byte)16, TProtocolVersion.class)));
        tmpMap.put(_Fields.SESSION_HANDLE, new FieldMetaData("sessionHandle", (byte)2, new StructMetaData((byte)12, TSessionHandle.class)));
        tmpMap.put(_Fields.CONFIGURATION, new FieldMetaData("configuration", (byte)2, new MapMetaData((byte)13, new FieldValueMetaData((byte)11), new FieldValueMetaData((byte)11))));
        FieldMetaData.addStructMetaDataMap(TOpenSessionResp.class, metaDataMap = Collections.unmodifiableMap((Map<? extends _Fields, ? extends FieldMetaData>)tmpMap));
    }
    
    public enum _Fields implements TFieldIdEnum
    {
        STATUS((short)1, "status"), 
        SERVER_PROTOCOL_VERSION((short)2, "serverProtocolVersion"), 
        SESSION_HANDLE((short)3, "sessionHandle"), 
        CONFIGURATION((short)4, "configuration");
        
        private static final Map<String, _Fields> byName;
        private final short _thriftId;
        private final String _fieldName;
        
        public static _Fields findByThriftId(final int fieldId) {
            switch (fieldId) {
                case 1: {
                    return _Fields.STATUS;
                }
                case 2: {
                    return _Fields.SERVER_PROTOCOL_VERSION;
                }
                case 3: {
                    return _Fields.SESSION_HANDLE;
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
    
    private static class TOpenSessionRespStandardSchemeFactory implements SchemeFactory
    {
        @Override
        public TOpenSessionRespStandardScheme getScheme() {
            return new TOpenSessionRespStandardScheme();
        }
    }
    
    private static class TOpenSessionRespStandardScheme extends StandardScheme<TOpenSessionResp>
    {
        @Override
        public void read(final TProtocol iprot, final TOpenSessionResp struct) throws TException {
            iprot.readStructBegin();
            while (true) {
                final TField schemeField = iprot.readFieldBegin();
                if (schemeField.type == 0) {
                    break;
                }
                switch (schemeField.id) {
                    case 1: {
                        if (schemeField.type == 12) {
                            struct.status = new TStatus();
                            struct.status.read(iprot);
                            struct.setStatusIsSet(true);
                            break;
                        }
                        TProtocolUtil.skip(iprot, schemeField.type);
                        break;
                    }
                    case 2: {
                        if (schemeField.type == 8) {
                            struct.serverProtocolVersion = TProtocolVersion.findByValue(iprot.readI32());
                            struct.setServerProtocolVersionIsSet(true);
                            break;
                        }
                        TProtocolUtil.skip(iprot, schemeField.type);
                        break;
                    }
                    case 3: {
                        if (schemeField.type == 12) {
                            struct.sessionHandle = new TSessionHandle();
                            struct.sessionHandle.read(iprot);
                            struct.setSessionHandleIsSet(true);
                            break;
                        }
                        TProtocolUtil.skip(iprot, schemeField.type);
                        break;
                    }
                    case 4: {
                        if (schemeField.type == 13) {
                            final TMap _map152 = iprot.readMapBegin();
                            struct.configuration = (Map<String, String>)new HashMap(2 * _map152.size);
                            for (int _i153 = 0; _i153 < _map152.size; ++_i153) {
                                final String _key154 = iprot.readString();
                                final String _val155 = iprot.readString();
                                struct.configuration.put(_key154, _val155);
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
        public void write(final TProtocol oprot, final TOpenSessionResp struct) throws TException {
            struct.validate();
            oprot.writeStructBegin(TOpenSessionResp.STRUCT_DESC);
            if (struct.status != null) {
                oprot.writeFieldBegin(TOpenSessionResp.STATUS_FIELD_DESC);
                struct.status.write(oprot);
                oprot.writeFieldEnd();
            }
            if (struct.serverProtocolVersion != null) {
                oprot.writeFieldBegin(TOpenSessionResp.SERVER_PROTOCOL_VERSION_FIELD_DESC);
                oprot.writeI32(struct.serverProtocolVersion.getValue());
                oprot.writeFieldEnd();
            }
            if (struct.sessionHandle != null && struct.isSetSessionHandle()) {
                oprot.writeFieldBegin(TOpenSessionResp.SESSION_HANDLE_FIELD_DESC);
                struct.sessionHandle.write(oprot);
                oprot.writeFieldEnd();
            }
            if (struct.configuration != null && struct.isSetConfiguration()) {
                oprot.writeFieldBegin(TOpenSessionResp.CONFIGURATION_FIELD_DESC);
                oprot.writeMapBegin(new TMap((byte)11, (byte)11, struct.configuration.size()));
                for (final Map.Entry<String, String> _iter156 : struct.configuration.entrySet()) {
                    oprot.writeString(_iter156.getKey());
                    oprot.writeString(_iter156.getValue());
                }
                oprot.writeMapEnd();
                oprot.writeFieldEnd();
            }
            oprot.writeFieldStop();
            oprot.writeStructEnd();
        }
    }
    
    private static class TOpenSessionRespTupleSchemeFactory implements SchemeFactory
    {
        @Override
        public TOpenSessionRespTupleScheme getScheme() {
            return new TOpenSessionRespTupleScheme();
        }
    }
    
    private static class TOpenSessionRespTupleScheme extends TupleScheme<TOpenSessionResp>
    {
        @Override
        public void write(final TProtocol prot, final TOpenSessionResp struct) throws TException {
            final TTupleProtocol oprot = (TTupleProtocol)prot;
            struct.status.write(oprot);
            oprot.writeI32(struct.serverProtocolVersion.getValue());
            final BitSet optionals = new BitSet();
            if (struct.isSetSessionHandle()) {
                optionals.set(0);
            }
            if (struct.isSetConfiguration()) {
                optionals.set(1);
            }
            oprot.writeBitSet(optionals, 2);
            if (struct.isSetSessionHandle()) {
                struct.sessionHandle.write(oprot);
            }
            if (struct.isSetConfiguration()) {
                oprot.writeI32(struct.configuration.size());
                for (final Map.Entry<String, String> _iter157 : struct.configuration.entrySet()) {
                    oprot.writeString(_iter157.getKey());
                    oprot.writeString(_iter157.getValue());
                }
            }
        }
        
        @Override
        public void read(final TProtocol prot, final TOpenSessionResp struct) throws TException {
            final TTupleProtocol iprot = (TTupleProtocol)prot;
            struct.status = new TStatus();
            struct.status.read(iprot);
            struct.setStatusIsSet(true);
            struct.serverProtocolVersion = TProtocolVersion.findByValue(iprot.readI32());
            struct.setServerProtocolVersionIsSet(true);
            final BitSet incoming = iprot.readBitSet(2);
            if (incoming.get(0)) {
                struct.sessionHandle = new TSessionHandle();
                struct.sessionHandle.read(iprot);
                struct.setSessionHandleIsSet(true);
            }
            if (incoming.get(1)) {
                final TMap _map158 = new TMap((byte)11, (byte)11, iprot.readI32());
                struct.configuration = (Map<String, String>)new HashMap(2 * _map158.size);
                for (int _i159 = 0; _i159 < _map158.size; ++_i159) {
                    final String _key160 = iprot.readString();
                    final String _val161 = iprot.readString();
                    struct.configuration.put(_key160, _val161);
                }
                struct.setConfigurationIsSet(true);
            }
        }
    }
}
