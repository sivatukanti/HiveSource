// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.hive.metastore.api;

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

public class OpenTxnRequest implements TBase<OpenTxnRequest, _Fields>, Serializable, Cloneable
{
    private static final TStruct STRUCT_DESC;
    private static final TField NUM_TXNS_FIELD_DESC;
    private static final TField USER_FIELD_DESC;
    private static final TField HOSTNAME_FIELD_DESC;
    private static final Map<Class<? extends IScheme>, SchemeFactory> schemes;
    private int num_txns;
    private String user;
    private String hostname;
    private static final int __NUM_TXNS_ISSET_ID = 0;
    private byte __isset_bitfield;
    public static final Map<_Fields, FieldMetaData> metaDataMap;
    
    public OpenTxnRequest() {
        this.__isset_bitfield = 0;
    }
    
    public OpenTxnRequest(final int num_txns, final String user, final String hostname) {
        this();
        this.num_txns = num_txns;
        this.setNum_txnsIsSet(true);
        this.user = user;
        this.hostname = hostname;
    }
    
    public OpenTxnRequest(final OpenTxnRequest other) {
        this.__isset_bitfield = 0;
        this.__isset_bitfield = other.__isset_bitfield;
        this.num_txns = other.num_txns;
        if (other.isSetUser()) {
            this.user = other.user;
        }
        if (other.isSetHostname()) {
            this.hostname = other.hostname;
        }
    }
    
    @Override
    public OpenTxnRequest deepCopy() {
        return new OpenTxnRequest(this);
    }
    
    @Override
    public void clear() {
        this.setNum_txnsIsSet(false);
        this.num_txns = 0;
        this.user = null;
        this.hostname = null;
    }
    
    public int getNum_txns() {
        return this.num_txns;
    }
    
    public void setNum_txns(final int num_txns) {
        this.num_txns = num_txns;
        this.setNum_txnsIsSet(true);
    }
    
    public void unsetNum_txns() {
        this.__isset_bitfield = EncodingUtils.clearBit(this.__isset_bitfield, 0);
    }
    
    public boolean isSetNum_txns() {
        return EncodingUtils.testBit(this.__isset_bitfield, 0);
    }
    
    public void setNum_txnsIsSet(final boolean value) {
        this.__isset_bitfield = EncodingUtils.setBit(this.__isset_bitfield, 0, value);
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
            case NUM_TXNS: {
                if (value == null) {
                    this.unsetNum_txns();
                    break;
                }
                this.setNum_txns((int)value);
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
            case NUM_TXNS: {
                return this.getNum_txns();
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
            case NUM_TXNS: {
                return this.isSetNum_txns();
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
        return that != null && that instanceof OpenTxnRequest && this.equals((OpenTxnRequest)that);
    }
    
    public boolean equals(final OpenTxnRequest that) {
        if (that == null) {
            return false;
        }
        final boolean this_present_num_txns = true;
        final boolean that_present_num_txns = true;
        if (this_present_num_txns || that_present_num_txns) {
            if (!this_present_num_txns || !that_present_num_txns) {
                return false;
            }
            if (this.num_txns != that.num_txns) {
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
        final boolean present_num_txns = true;
        builder.append(present_num_txns);
        if (present_num_txns) {
            builder.append(this.num_txns);
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
    public int compareTo(final OpenTxnRequest other) {
        if (!this.getClass().equals(other.getClass())) {
            return this.getClass().getName().compareTo(other.getClass().getName());
        }
        int lastComparison = 0;
        final OpenTxnRequest typedOther = other;
        lastComparison = Boolean.valueOf(this.isSetNum_txns()).compareTo(Boolean.valueOf(typedOther.isSetNum_txns()));
        if (lastComparison != 0) {
            return lastComparison;
        }
        if (this.isSetNum_txns()) {
            lastComparison = TBaseHelper.compareTo(this.num_txns, typedOther.num_txns);
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
        OpenTxnRequest.schemes.get(iprot.getScheme()).getScheme().read(iprot, this);
    }
    
    @Override
    public void write(final TProtocol oprot) throws TException {
        OpenTxnRequest.schemes.get(oprot.getScheme()).getScheme().write(oprot, this);
    }
    
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("OpenTxnRequest(");
        boolean first = true;
        sb.append("num_txns:");
        sb.append(this.num_txns);
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
        if (!this.isSetNum_txns()) {
            throw new TProtocolException("Required field 'num_txns' is unset! Struct:" + this.toString());
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
        STRUCT_DESC = new TStruct("OpenTxnRequest");
        NUM_TXNS_FIELD_DESC = new TField("num_txns", (byte)8, (short)1);
        USER_FIELD_DESC = new TField("user", (byte)11, (short)2);
        HOSTNAME_FIELD_DESC = new TField("hostname", (byte)11, (short)3);
        (schemes = new HashMap<Class<? extends IScheme>, SchemeFactory>()).put(StandardScheme.class, new OpenTxnRequestStandardSchemeFactory());
        OpenTxnRequest.schemes.put(TupleScheme.class, new OpenTxnRequestTupleSchemeFactory());
        final Map<_Fields, FieldMetaData> tmpMap = new EnumMap<_Fields, FieldMetaData>(_Fields.class);
        tmpMap.put(_Fields.NUM_TXNS, new FieldMetaData("num_txns", (byte)1, new FieldValueMetaData((byte)8)));
        tmpMap.put(_Fields.USER, new FieldMetaData("user", (byte)1, new FieldValueMetaData((byte)11)));
        tmpMap.put(_Fields.HOSTNAME, new FieldMetaData("hostname", (byte)1, new FieldValueMetaData((byte)11)));
        FieldMetaData.addStructMetaDataMap(OpenTxnRequest.class, metaDataMap = Collections.unmodifiableMap((Map<? extends _Fields, ? extends FieldMetaData>)tmpMap));
    }
    
    public enum _Fields implements TFieldIdEnum
    {
        NUM_TXNS((short)1, "num_txns"), 
        USER((short)2, "user"), 
        HOSTNAME((short)3, "hostname");
        
        private static final Map<String, _Fields> byName;
        private final short _thriftId;
        private final String _fieldName;
        
        public static _Fields findByThriftId(final int fieldId) {
            switch (fieldId) {
                case 1: {
                    return _Fields.NUM_TXNS;
                }
                case 2: {
                    return _Fields.USER;
                }
                case 3: {
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
    
    private static class OpenTxnRequestStandardSchemeFactory implements SchemeFactory
    {
        @Override
        public OpenTxnRequestStandardScheme getScheme() {
            return new OpenTxnRequestStandardScheme();
        }
    }
    
    private static class OpenTxnRequestStandardScheme extends StandardScheme<OpenTxnRequest>
    {
        @Override
        public void read(final TProtocol iprot, final OpenTxnRequest struct) throws TException {
            iprot.readStructBegin();
            while (true) {
                final TField schemeField = iprot.readFieldBegin();
                if (schemeField.type == 0) {
                    break;
                }
                switch (schemeField.id) {
                    case 1: {
                        if (schemeField.type == 8) {
                            struct.num_txns = iprot.readI32();
                            struct.setNum_txnsIsSet(true);
                            break;
                        }
                        TProtocolUtil.skip(iprot, schemeField.type);
                        break;
                    }
                    case 2: {
                        if (schemeField.type == 11) {
                            struct.user = iprot.readString();
                            struct.setUserIsSet(true);
                            break;
                        }
                        TProtocolUtil.skip(iprot, schemeField.type);
                        break;
                    }
                    case 3: {
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
        public void write(final TProtocol oprot, final OpenTxnRequest struct) throws TException {
            struct.validate();
            oprot.writeStructBegin(OpenTxnRequest.STRUCT_DESC);
            oprot.writeFieldBegin(OpenTxnRequest.NUM_TXNS_FIELD_DESC);
            oprot.writeI32(struct.num_txns);
            oprot.writeFieldEnd();
            if (struct.user != null) {
                oprot.writeFieldBegin(OpenTxnRequest.USER_FIELD_DESC);
                oprot.writeString(struct.user);
                oprot.writeFieldEnd();
            }
            if (struct.hostname != null) {
                oprot.writeFieldBegin(OpenTxnRequest.HOSTNAME_FIELD_DESC);
                oprot.writeString(struct.hostname);
                oprot.writeFieldEnd();
            }
            oprot.writeFieldStop();
            oprot.writeStructEnd();
        }
    }
    
    private static class OpenTxnRequestTupleSchemeFactory implements SchemeFactory
    {
        @Override
        public OpenTxnRequestTupleScheme getScheme() {
            return new OpenTxnRequestTupleScheme();
        }
    }
    
    private static class OpenTxnRequestTupleScheme extends TupleScheme<OpenTxnRequest>
    {
        @Override
        public void write(final TProtocol prot, final OpenTxnRequest struct) throws TException {
            final TTupleProtocol oprot = (TTupleProtocol)prot;
            oprot.writeI32(struct.num_txns);
            oprot.writeString(struct.user);
            oprot.writeString(struct.hostname);
        }
        
        @Override
        public void read(final TProtocol prot, final OpenTxnRequest struct) throws TException {
            final TTupleProtocol iprot = (TTupleProtocol)prot;
            struct.num_txns = iprot.readI32();
            struct.setNum_txnsIsSet(true);
            struct.user = iprot.readString();
            struct.setUserIsSet(true);
            struct.hostname = iprot.readString();
            struct.setHostnameIsSet(true);
        }
    }
}
