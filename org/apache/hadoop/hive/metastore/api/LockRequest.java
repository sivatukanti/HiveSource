// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.hive.metastore.api;

import java.util.BitSet;
import org.apache.thrift.protocol.TTupleProtocol;
import org.apache.thrift.protocol.TList;
import org.apache.thrift.protocol.TProtocolUtil;
import java.util.EnumSet;
import java.util.Collections;
import org.apache.thrift.meta_data.FieldValueMetaData;
import org.apache.thrift.meta_data.ListMetaData;
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
import org.apache.thrift.protocol.TProtocolException;
import org.apache.thrift.TException;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.TBaseHelper;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.thrift.EncodingUtils;
import java.util.Iterator;
import java.util.ArrayList;
import org.apache.thrift.meta_data.FieldMetaData;
import java.util.List;
import org.apache.thrift.scheme.SchemeFactory;
import org.apache.thrift.scheme.IScheme;
import java.util.Map;
import org.apache.thrift.protocol.TField;
import org.apache.thrift.protocol.TStruct;
import java.io.Serializable;
import org.apache.thrift.TBase;

public class LockRequest implements TBase<LockRequest, _Fields>, Serializable, Cloneable
{
    private static final TStruct STRUCT_DESC;
    private static final TField COMPONENT_FIELD_DESC;
    private static final TField TXNID_FIELD_DESC;
    private static final TField USER_FIELD_DESC;
    private static final TField HOSTNAME_FIELD_DESC;
    private static final Map<Class<? extends IScheme>, SchemeFactory> schemes;
    private List<LockComponent> component;
    private long txnid;
    private String user;
    private String hostname;
    private static final int __TXNID_ISSET_ID = 0;
    private byte __isset_bitfield;
    private _Fields[] optionals;
    public static final Map<_Fields, FieldMetaData> metaDataMap;
    
    public LockRequest() {
        this.__isset_bitfield = 0;
        this.optionals = new _Fields[] { _Fields.TXNID };
    }
    
    public LockRequest(final List<LockComponent> component, final String user, final String hostname) {
        this();
        this.component = component;
        this.user = user;
        this.hostname = hostname;
    }
    
    public LockRequest(final LockRequest other) {
        this.__isset_bitfield = 0;
        this.optionals = new _Fields[] { _Fields.TXNID };
        this.__isset_bitfield = other.__isset_bitfield;
        if (other.isSetComponent()) {
            final List<LockComponent> __this__component = new ArrayList<LockComponent>();
            for (final LockComponent other_element : other.component) {
                __this__component.add(new LockComponent(other_element));
            }
            this.component = __this__component;
        }
        this.txnid = other.txnid;
        if (other.isSetUser()) {
            this.user = other.user;
        }
        if (other.isSetHostname()) {
            this.hostname = other.hostname;
        }
    }
    
    @Override
    public LockRequest deepCopy() {
        return new LockRequest(this);
    }
    
    @Override
    public void clear() {
        this.component = null;
        this.setTxnidIsSet(false);
        this.txnid = 0L;
        this.user = null;
        this.hostname = null;
    }
    
    public int getComponentSize() {
        return (this.component == null) ? 0 : this.component.size();
    }
    
    public Iterator<LockComponent> getComponentIterator() {
        return (this.component == null) ? null : this.component.iterator();
    }
    
    public void addToComponent(final LockComponent elem) {
        if (this.component == null) {
            this.component = new ArrayList<LockComponent>();
        }
        this.component.add(elem);
    }
    
    public List<LockComponent> getComponent() {
        return this.component;
    }
    
    public void setComponent(final List<LockComponent> component) {
        this.component = component;
    }
    
    public void unsetComponent() {
        this.component = null;
    }
    
    public boolean isSetComponent() {
        return this.component != null;
    }
    
    public void setComponentIsSet(final boolean value) {
        if (!value) {
            this.component = null;
        }
    }
    
    public long getTxnid() {
        return this.txnid;
    }
    
    public void setTxnid(final long txnid) {
        this.txnid = txnid;
        this.setTxnidIsSet(true);
    }
    
    public void unsetTxnid() {
        this.__isset_bitfield = EncodingUtils.clearBit(this.__isset_bitfield, 0);
    }
    
    public boolean isSetTxnid() {
        return EncodingUtils.testBit(this.__isset_bitfield, 0);
    }
    
    public void setTxnidIsSet(final boolean value) {
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
            case COMPONENT: {
                if (value == null) {
                    this.unsetComponent();
                    break;
                }
                this.setComponent((List<LockComponent>)value);
                break;
            }
            case TXNID: {
                if (value == null) {
                    this.unsetTxnid();
                    break;
                }
                this.setTxnid((long)value);
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
            case COMPONENT: {
                return this.getComponent();
            }
            case TXNID: {
                return this.getTxnid();
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
            case COMPONENT: {
                return this.isSetComponent();
            }
            case TXNID: {
                return this.isSetTxnid();
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
        return that != null && that instanceof LockRequest && this.equals((LockRequest)that);
    }
    
    public boolean equals(final LockRequest that) {
        if (that == null) {
            return false;
        }
        final boolean this_present_component = this.isSetComponent();
        final boolean that_present_component = that.isSetComponent();
        if (this_present_component || that_present_component) {
            if (!this_present_component || !that_present_component) {
                return false;
            }
            if (!this.component.equals(that.component)) {
                return false;
            }
        }
        final boolean this_present_txnid = this.isSetTxnid();
        final boolean that_present_txnid = that.isSetTxnid();
        if (this_present_txnid || that_present_txnid) {
            if (!this_present_txnid || !that_present_txnid) {
                return false;
            }
            if (this.txnid != that.txnid) {
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
        final boolean present_component = this.isSetComponent();
        builder.append(present_component);
        if (present_component) {
            builder.append(this.component);
        }
        final boolean present_txnid = this.isSetTxnid();
        builder.append(present_txnid);
        if (present_txnid) {
            builder.append(this.txnid);
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
    public int compareTo(final LockRequest other) {
        if (!this.getClass().equals(other.getClass())) {
            return this.getClass().getName().compareTo(other.getClass().getName());
        }
        int lastComparison = 0;
        final LockRequest typedOther = other;
        lastComparison = Boolean.valueOf(this.isSetComponent()).compareTo(Boolean.valueOf(typedOther.isSetComponent()));
        if (lastComparison != 0) {
            return lastComparison;
        }
        if (this.isSetComponent()) {
            lastComparison = TBaseHelper.compareTo(this.component, typedOther.component);
            if (lastComparison != 0) {
                return lastComparison;
            }
        }
        lastComparison = Boolean.valueOf(this.isSetTxnid()).compareTo(Boolean.valueOf(typedOther.isSetTxnid()));
        if (lastComparison != 0) {
            return lastComparison;
        }
        if (this.isSetTxnid()) {
            lastComparison = TBaseHelper.compareTo(this.txnid, typedOther.txnid);
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
        LockRequest.schemes.get(iprot.getScheme()).getScheme().read(iprot, this);
    }
    
    @Override
    public void write(final TProtocol oprot) throws TException {
        LockRequest.schemes.get(oprot.getScheme()).getScheme().write(oprot, this);
    }
    
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("LockRequest(");
        boolean first = true;
        sb.append("component:");
        if (this.component == null) {
            sb.append("null");
        }
        else {
            sb.append(this.component);
        }
        first = false;
        if (this.isSetTxnid()) {
            if (!first) {
                sb.append(", ");
            }
            sb.append("txnid:");
            sb.append(this.txnid);
            first = false;
        }
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
        if (!this.isSetComponent()) {
            throw new TProtocolException("Required field 'component' is unset! Struct:" + this.toString());
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
        STRUCT_DESC = new TStruct("LockRequest");
        COMPONENT_FIELD_DESC = new TField("component", (byte)15, (short)1);
        TXNID_FIELD_DESC = new TField("txnid", (byte)10, (short)2);
        USER_FIELD_DESC = new TField("user", (byte)11, (short)3);
        HOSTNAME_FIELD_DESC = new TField("hostname", (byte)11, (short)4);
        (schemes = new HashMap<Class<? extends IScheme>, SchemeFactory>()).put(StandardScheme.class, new LockRequestStandardSchemeFactory());
        LockRequest.schemes.put(TupleScheme.class, new LockRequestTupleSchemeFactory());
        final Map<_Fields, FieldMetaData> tmpMap = new EnumMap<_Fields, FieldMetaData>(_Fields.class);
        tmpMap.put(_Fields.COMPONENT, new FieldMetaData("component", (byte)1, new ListMetaData((byte)15, new StructMetaData((byte)12, LockComponent.class))));
        tmpMap.put(_Fields.TXNID, new FieldMetaData("txnid", (byte)2, new FieldValueMetaData((byte)10)));
        tmpMap.put(_Fields.USER, new FieldMetaData("user", (byte)1, new FieldValueMetaData((byte)11)));
        tmpMap.put(_Fields.HOSTNAME, new FieldMetaData("hostname", (byte)1, new FieldValueMetaData((byte)11)));
        FieldMetaData.addStructMetaDataMap(LockRequest.class, metaDataMap = Collections.unmodifiableMap((Map<? extends _Fields, ? extends FieldMetaData>)tmpMap));
    }
    
    public enum _Fields implements TFieldIdEnum
    {
        COMPONENT((short)1, "component"), 
        TXNID((short)2, "txnid"), 
        USER((short)3, "user"), 
        HOSTNAME((short)4, "hostname");
        
        private static final Map<String, _Fields> byName;
        private final short _thriftId;
        private final String _fieldName;
        
        public static _Fields findByThriftId(final int fieldId) {
            switch (fieldId) {
                case 1: {
                    return _Fields.COMPONENT;
                }
                case 2: {
                    return _Fields.TXNID;
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
    
    private static class LockRequestStandardSchemeFactory implements SchemeFactory
    {
        @Override
        public LockRequestStandardScheme getScheme() {
            return new LockRequestStandardScheme();
        }
    }
    
    private static class LockRequestStandardScheme extends StandardScheme<LockRequest>
    {
        @Override
        public void read(final TProtocol iprot, final LockRequest struct) throws TException {
            iprot.readStructBegin();
            while (true) {
                final TField schemeField = iprot.readFieldBegin();
                if (schemeField.type == 0) {
                    break;
                }
                switch (schemeField.id) {
                    case 1: {
                        if (schemeField.type == 15) {
                            final TList _list452 = iprot.readListBegin();
                            struct.component = (List<LockComponent>)new ArrayList(_list452.size);
                            for (int _i453 = 0; _i453 < _list452.size; ++_i453) {
                                final LockComponent _elem454 = new LockComponent();
                                _elem454.read(iprot);
                                struct.component.add(_elem454);
                            }
                            iprot.readListEnd();
                            struct.setComponentIsSet(true);
                            break;
                        }
                        TProtocolUtil.skip(iprot, schemeField.type);
                        break;
                    }
                    case 2: {
                        if (schemeField.type == 10) {
                            struct.txnid = iprot.readI64();
                            struct.setTxnidIsSet(true);
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
        public void write(final TProtocol oprot, final LockRequest struct) throws TException {
            struct.validate();
            oprot.writeStructBegin(LockRequest.STRUCT_DESC);
            if (struct.component != null) {
                oprot.writeFieldBegin(LockRequest.COMPONENT_FIELD_DESC);
                oprot.writeListBegin(new TList((byte)12, struct.component.size()));
                for (final LockComponent _iter455 : struct.component) {
                    _iter455.write(oprot);
                }
                oprot.writeListEnd();
                oprot.writeFieldEnd();
            }
            if (struct.isSetTxnid()) {
                oprot.writeFieldBegin(LockRequest.TXNID_FIELD_DESC);
                oprot.writeI64(struct.txnid);
                oprot.writeFieldEnd();
            }
            if (struct.user != null) {
                oprot.writeFieldBegin(LockRequest.USER_FIELD_DESC);
                oprot.writeString(struct.user);
                oprot.writeFieldEnd();
            }
            if (struct.hostname != null) {
                oprot.writeFieldBegin(LockRequest.HOSTNAME_FIELD_DESC);
                oprot.writeString(struct.hostname);
                oprot.writeFieldEnd();
            }
            oprot.writeFieldStop();
            oprot.writeStructEnd();
        }
    }
    
    private static class LockRequestTupleSchemeFactory implements SchemeFactory
    {
        @Override
        public LockRequestTupleScheme getScheme() {
            return new LockRequestTupleScheme();
        }
    }
    
    private static class LockRequestTupleScheme extends TupleScheme<LockRequest>
    {
        @Override
        public void write(final TProtocol prot, final LockRequest struct) throws TException {
            final TTupleProtocol oprot = (TTupleProtocol)prot;
            oprot.writeI32(struct.component.size());
            for (final LockComponent _iter456 : struct.component) {
                _iter456.write(oprot);
            }
            oprot.writeString(struct.user);
            oprot.writeString(struct.hostname);
            final BitSet optionals = new BitSet();
            if (struct.isSetTxnid()) {
                optionals.set(0);
            }
            oprot.writeBitSet(optionals, 1);
            if (struct.isSetTxnid()) {
                oprot.writeI64(struct.txnid);
            }
        }
        
        @Override
        public void read(final TProtocol prot, final LockRequest struct) throws TException {
            final TTupleProtocol iprot = (TTupleProtocol)prot;
            final TList _list457 = new TList((byte)12, iprot.readI32());
            struct.component = (List<LockComponent>)new ArrayList(_list457.size);
            for (int _i458 = 0; _i458 < _list457.size; ++_i458) {
                final LockComponent _elem459 = new LockComponent();
                _elem459.read(iprot);
                struct.component.add(_elem459);
            }
            struct.setComponentIsSet(true);
            struct.user = iprot.readString();
            struct.setUserIsSet(true);
            struct.hostname = iprot.readString();
            struct.setHostnameIsSet(true);
            final BitSet incoming = iprot.readBitSet(1);
            if (incoming.get(0)) {
                struct.txnid = iprot.readI64();
                struct.setTxnidIsSet(true);
            }
        }
    }
}
