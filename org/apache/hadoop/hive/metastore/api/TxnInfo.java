// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.hive.metastore.api;

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
import org.apache.thrift.protocol.TProtocolException;
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

public class TxnInfo implements TBase<TxnInfo, _Fields>, Serializable, Cloneable
{
    private static final TStruct STRUCT_DESC;
    private static final TField ID_FIELD_DESC;
    private static final TField STATE_FIELD_DESC;
    private static final TField USER_FIELD_DESC;
    private static final TField HOSTNAME_FIELD_DESC;
    private static final Map<Class<? extends IScheme>, SchemeFactory> schemes;
    private long id;
    private TxnState state;
    private String user;
    private String hostname;
    private static final int __ID_ISSET_ID = 0;
    private byte __isset_bitfield;
    public static final Map<_Fields, FieldMetaData> metaDataMap;
    
    public TxnInfo() {
        this.__isset_bitfield = 0;
    }
    
    public TxnInfo(final long id, final TxnState state, final String user, final String hostname) {
        this();
        this.id = id;
        this.setIdIsSet(true);
        this.state = state;
        this.user = user;
        this.hostname = hostname;
    }
    
    public TxnInfo(final TxnInfo other) {
        this.__isset_bitfield = 0;
        this.__isset_bitfield = other.__isset_bitfield;
        this.id = other.id;
        if (other.isSetState()) {
            this.state = other.state;
        }
        if (other.isSetUser()) {
            this.user = other.user;
        }
        if (other.isSetHostname()) {
            this.hostname = other.hostname;
        }
    }
    
    @Override
    public TxnInfo deepCopy() {
        return new TxnInfo(this);
    }
    
    @Override
    public void clear() {
        this.setIdIsSet(false);
        this.id = 0L;
        this.state = null;
        this.user = null;
        this.hostname = null;
    }
    
    public long getId() {
        return this.id;
    }
    
    public void setId(final long id) {
        this.id = id;
        this.setIdIsSet(true);
    }
    
    public void unsetId() {
        this.__isset_bitfield = EncodingUtils.clearBit(this.__isset_bitfield, 0);
    }
    
    public boolean isSetId() {
        return EncodingUtils.testBit(this.__isset_bitfield, 0);
    }
    
    public void setIdIsSet(final boolean value) {
        this.__isset_bitfield = EncodingUtils.setBit(this.__isset_bitfield, 0, value);
    }
    
    public TxnState getState() {
        return this.state;
    }
    
    public void setState(final TxnState state) {
        this.state = state;
    }
    
    public void unsetState() {
        this.state = null;
    }
    
    public boolean isSetState() {
        return this.state != null;
    }
    
    public void setStateIsSet(final boolean value) {
        if (!value) {
            this.state = null;
        }
    }
    
    public String getUser() {
        return this.user;
    }
    
    public void setUser(final String user) {
        this.user = user;
    }
    
    public void unsetUser() {
        this.user = null;
    }
    
    public boolean isSetUser() {
        return this.user != null;
    }
    
    public void setUserIsSet(final boolean value) {
        if (!value) {
            this.user = null;
        }
    }
    
    public String getHostname() {
        return this.hostname;
    }
    
    public void setHostname(final String hostname) {
        this.hostname = hostname;
    }
    
    public void unsetHostname() {
        this.hostname = null;
    }
    
    public boolean isSetHostname() {
        return this.hostname != null;
    }
    
    public void setHostnameIsSet(final boolean value) {
        if (!value) {
            this.hostname = null;
        }
    }
    
    @Override
    public void setFieldValue(final _Fields field, final Object value) {
        switch (field) {
            case ID: {
                if (value == null) {
                    this.unsetId();
                    break;
                }
                this.setId((long)value);
                break;
            }
            case STATE: {
                if (value == null) {
                    this.unsetState();
                    break;
                }
                this.setState((TxnState)value);
                break;
            }
            case USER: {
                if (value == null) {
                    this.unsetUser();
                    break;
                }
                this.setUser((String)value);
                break;
            }
            case HOSTNAME: {
                if (value == null) {
                    this.unsetHostname();
                    break;
                }
                this.setHostname((String)value);
                break;
            }
        }
    }
    
    @Override
    public Object getFieldValue(final _Fields field) {
        switch (field) {
            case ID: {
                return this.getId();
            }
            case STATE: {
                return this.getState();
            }
            case USER: {
                return this.getUser();
            }
            case HOSTNAME: {
                return this.getHostname();
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
            case ID: {
                return this.isSetId();
            }
            case STATE: {
                return this.isSetState();
            }
            case USER: {
                return this.isSetUser();
            }
            case HOSTNAME: {
                return this.isSetHostname();
            }
            default: {
                throw new IllegalStateException();
            }
        }
    }
    
    @Override
    public boolean equals(final Object that) {
        return that != null && that instanceof TxnInfo && this.equals((TxnInfo)that);
    }
    
    public boolean equals(final TxnInfo that) {
        if (that == null) {
            return false;
        }
        final boolean this_present_id = true;
        final boolean that_present_id = true;
        if (this_present_id || that_present_id) {
            if (!this_present_id || !that_present_id) {
                return false;
            }
            if (this.id != that.id) {
                return false;
            }
        }
        final boolean this_present_state = this.isSetState();
        final boolean that_present_state = that.isSetState();
        if (this_present_state || that_present_state) {
            if (!this_present_state || !that_present_state) {
                return false;
            }
            if (!this.state.equals(that.state)) {
                return false;
            }
        }
        final boolean this_present_user = this.isSetUser();
        final boolean that_present_user = that.isSetUser();
        if (this_present_user || that_present_user) {
            if (!this_present_user || !that_present_user) {
                return false;
            }
            if (!this.user.equals(that.user)) {
                return false;
            }
        }
        final boolean this_present_hostname = this.isSetHostname();
        final boolean that_present_hostname = that.isSetHostname();
        if (this_present_hostname || that_present_hostname) {
            if (!this_present_hostname || !that_present_hostname) {
                return false;
            }
            if (!this.hostname.equals(that.hostname)) {
                return false;
            }
        }
        return true;
    }
    
    @Override
    public int hashCode() {
        final HashCodeBuilder builder = new HashCodeBuilder();
        final boolean present_id = true;
        builder.append(present_id);
        if (present_id) {
            builder.append(this.id);
        }
        final boolean present_state = this.isSetState();
        builder.append(present_state);
        if (present_state) {
            builder.append(this.state.getValue());
        }
        final boolean present_user = this.isSetUser();
        builder.append(present_user);
        if (present_user) {
            builder.append(this.user);
        }
        final boolean present_hostname = this.isSetHostname();
        builder.append(present_hostname);
        if (present_hostname) {
            builder.append(this.hostname);
        }
        return builder.toHashCode();
    }
    
    @Override
    public int compareTo(final TxnInfo other) {
        if (!this.getClass().equals(other.getClass())) {
            return this.getClass().getName().compareTo(other.getClass().getName());
        }
        int lastComparison = 0;
        final TxnInfo typedOther = other;
        lastComparison = Boolean.valueOf(this.isSetId()).compareTo(Boolean.valueOf(typedOther.isSetId()));
        if (lastComparison != 0) {
            return lastComparison;
        }
        if (this.isSetId()) {
            lastComparison = TBaseHelper.compareTo(this.id, typedOther.id);
            if (lastComparison != 0) {
                return lastComparison;
            }
        }
        lastComparison = Boolean.valueOf(this.isSetState()).compareTo(Boolean.valueOf(typedOther.isSetState()));
        if (lastComparison != 0) {
            return lastComparison;
        }
        if (this.isSetState()) {
            lastComparison = TBaseHelper.compareTo(this.state, typedOther.state);
            if (lastComparison != 0) {
                return lastComparison;
            }
        }
        lastComparison = Boolean.valueOf(this.isSetUser()).compareTo(Boolean.valueOf(typedOther.isSetUser()));
        if (lastComparison != 0) {
            return lastComparison;
        }
        if (this.isSetUser()) {
            lastComparison = TBaseHelper.compareTo(this.user, typedOther.user);
            if (lastComparison != 0) {
                return lastComparison;
            }
        }
        lastComparison = Boolean.valueOf(this.isSetHostname()).compareTo(Boolean.valueOf(typedOther.isSetHostname()));
        if (lastComparison != 0) {
            return lastComparison;
        }
        if (this.isSetHostname()) {
            lastComparison = TBaseHelper.compareTo(this.hostname, typedOther.hostname);
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
        TxnInfo.schemes.get(iprot.getScheme()).getScheme().read(iprot, this);
    }
    
    @Override
    public void write(final TProtocol oprot) throws TException {
        TxnInfo.schemes.get(oprot.getScheme()).getScheme().write(oprot, this);
    }
    
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("TxnInfo(");
        boolean first = true;
        sb.append("id:");
        sb.append(this.id);
        first = false;
        if (!first) {
            sb.append(", ");
        }
        sb.append("state:");
        if (this.state == null) {
            sb.append("null");
        }
        else {
            sb.append(this.state);
        }
        first = false;
        if (!first) {
            sb.append(", ");
        }
        sb.append("user:");
        if (this.user == null) {
            sb.append("null");
        }
        else {
            sb.append(this.user);
        }
        first = false;
        if (!first) {
            sb.append(", ");
        }
        sb.append("hostname:");
        if (this.hostname == null) {
            sb.append("null");
        }
        else {
            sb.append(this.hostname);
        }
        first = false;
        sb.append(")");
        return sb.toString();
    }
    
    public void validate() throws TException {
        if (!this.isSetId()) {
            throw new TProtocolException("Required field 'id' is unset! Struct:" + this.toString());
        }
        if (!this.isSetState()) {
            throw new TProtocolException("Required field 'state' is unset! Struct:" + this.toString());
        }
        if (!this.isSetUser()) {
            throw new TProtocolException("Required field 'user' is unset! Struct:" + this.toString());
        }
        if (!this.isSetHostname()) {
            throw new TProtocolException("Required field 'hostname' is unset! Struct:" + this.toString());
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
        STRUCT_DESC = new TStruct("TxnInfo");
        ID_FIELD_DESC = new TField("id", (byte)10, (short)1);
        STATE_FIELD_DESC = new TField("state", (byte)8, (short)2);
        USER_FIELD_DESC = new TField("user", (byte)11, (short)3);
        HOSTNAME_FIELD_DESC = new TField("hostname", (byte)11, (short)4);
        (schemes = new HashMap<Class<? extends IScheme>, SchemeFactory>()).put(StandardScheme.class, new TxnInfoStandardSchemeFactory());
        TxnInfo.schemes.put(TupleScheme.class, new TxnInfoTupleSchemeFactory());
        final Map<_Fields, FieldMetaData> tmpMap = new EnumMap<_Fields, FieldMetaData>(_Fields.class);
        tmpMap.put(_Fields.ID, new FieldMetaData("id", (byte)1, new FieldValueMetaData((byte)10)));
        tmpMap.put(_Fields.STATE, new FieldMetaData("state", (byte)1, new EnumMetaData((byte)16, TxnState.class)));
        tmpMap.put(_Fields.USER, new FieldMetaData("user", (byte)1, new FieldValueMetaData((byte)11)));
        tmpMap.put(_Fields.HOSTNAME, new FieldMetaData("hostname", (byte)1, new FieldValueMetaData((byte)11)));
        FieldMetaData.addStructMetaDataMap(TxnInfo.class, metaDataMap = Collections.unmodifiableMap((Map<? extends _Fields, ? extends FieldMetaData>)tmpMap));
    }
    
    public enum _Fields implements TFieldIdEnum
    {
        ID((short)1, "id"), 
        STATE((short)2, "state"), 
        USER((short)3, "user"), 
        HOSTNAME((short)4, "hostname");
        
        private static final Map<String, _Fields> byName;
        private final short _thriftId;
        private final String _fieldName;
        
        public static _Fields findByThriftId(final int fieldId) {
            switch (fieldId) {
                case 1: {
                    return _Fields.ID;
                }
                case 2: {
                    return _Fields.STATE;
                }
                case 3: {
                    return _Fields.USER;
                }
                case 4: {
                    return _Fields.HOSTNAME;
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
    
    private static class TxnInfoStandardSchemeFactory implements SchemeFactory
    {
        @Override
        public TxnInfoStandardScheme getScheme() {
            return new TxnInfoStandardScheme();
        }
    }
    
    private static class TxnInfoStandardScheme extends StandardScheme<TxnInfo>
    {
        @Override
        public void read(final TProtocol iprot, final TxnInfo struct) throws TException {
            iprot.readStructBegin();
            while (true) {
                final TField schemeField = iprot.readFieldBegin();
                if (schemeField.type == 0) {
                    break;
                }
                switch (schemeField.id) {
                    case 1: {
                        if (schemeField.type == 10) {
                            struct.id = iprot.readI64();
                            struct.setIdIsSet(true);
                            break;
                        }
                        TProtocolUtil.skip(iprot, schemeField.type);
                        break;
                    }
                    case 2: {
                        if (schemeField.type == 8) {
                            struct.state = TxnState.findByValue(iprot.readI32());
                            struct.setStateIsSet(true);
                            break;
                        }
                        TProtocolUtil.skip(iprot, schemeField.type);
                        break;
                    }
                    case 3: {
                        if (schemeField.type == 11) {
                            struct.user = iprot.readString();
                            struct.setUserIsSet(true);
                            break;
                        }
                        TProtocolUtil.skip(iprot, schemeField.type);
                        break;
                    }
                    case 4: {
                        if (schemeField.type == 11) {
                            struct.hostname = iprot.readString();
                            struct.setHostnameIsSet(true);
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
        public void write(final TProtocol oprot, final TxnInfo struct) throws TException {
            struct.validate();
            oprot.writeStructBegin(TxnInfo.STRUCT_DESC);
            oprot.writeFieldBegin(TxnInfo.ID_FIELD_DESC);
            oprot.writeI64(struct.id);
            oprot.writeFieldEnd();
            if (struct.state != null) {
                oprot.writeFieldBegin(TxnInfo.STATE_FIELD_DESC);
                oprot.writeI32(struct.state.getValue());
                oprot.writeFieldEnd();
            }
            if (struct.user != null) {
                oprot.writeFieldBegin(TxnInfo.USER_FIELD_DESC);
                oprot.writeString(struct.user);
                oprot.writeFieldEnd();
            }
            if (struct.hostname != null) {
                oprot.writeFieldBegin(TxnInfo.HOSTNAME_FIELD_DESC);
                oprot.writeString(struct.hostname);
                oprot.writeFieldEnd();
            }
            oprot.writeFieldStop();
            oprot.writeStructEnd();
        }
    }
    
    private static class TxnInfoTupleSchemeFactory implements SchemeFactory
    {
        @Override
        public TxnInfoTupleScheme getScheme() {
            return new TxnInfoTupleScheme();
        }
    }
    
    private static class TxnInfoTupleScheme extends TupleScheme<TxnInfo>
    {
        @Override
        public void write(final TProtocol prot, final TxnInfo struct) throws TException {
            final TTupleProtocol oprot = (TTupleProtocol)prot;
            oprot.writeI64(struct.id);
            oprot.writeI32(struct.state.getValue());
            oprot.writeString(struct.user);
            oprot.writeString(struct.hostname);
        }
        
        @Override
        public void read(final TProtocol prot, final TxnInfo struct) throws TException {
            final TTupleProtocol iprot = (TTupleProtocol)prot;
            struct.id = iprot.readI64();
            struct.setIdIsSet(true);
            struct.state = TxnState.findByValue(iprot.readI32());
            struct.setStateIsSet(true);
            struct.user = iprot.readString();
            struct.setUserIsSet(true);
            struct.hostname = iprot.readString();
            struct.setHostnameIsSet(true);
        }
    }
}
