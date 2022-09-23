// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hive.service.cli.thrift;

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
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.thrift.TBaseHelper;
import org.apache.thrift.meta_data.FieldMetaData;
import java.nio.ByteBuffer;
import org.apache.thrift.scheme.SchemeFactory;
import org.apache.thrift.scheme.IScheme;
import java.util.Map;
import org.apache.thrift.protocol.TField;
import org.apache.thrift.protocol.TStruct;
import java.io.Serializable;
import org.apache.thrift.TBase;

public class THandleIdentifier implements TBase<THandleIdentifier, _Fields>, Serializable, Cloneable
{
    private static final TStruct STRUCT_DESC;
    private static final TField GUID_FIELD_DESC;
    private static final TField SECRET_FIELD_DESC;
    private static final Map<Class<? extends IScheme>, SchemeFactory> schemes;
    private ByteBuffer guid;
    private ByteBuffer secret;
    public static final Map<_Fields, FieldMetaData> metaDataMap;
    
    public THandleIdentifier() {
    }
    
    public THandleIdentifier(final ByteBuffer guid, final ByteBuffer secret) {
        this();
        this.guid = guid;
        this.secret = secret;
    }
    
    public THandleIdentifier(final THandleIdentifier other) {
        if (other.isSetGuid()) {
            this.guid = TBaseHelper.copyBinary(other.guid);
        }
        if (other.isSetSecret()) {
            this.secret = TBaseHelper.copyBinary(other.secret);
        }
    }
    
    @Override
    public THandleIdentifier deepCopy() {
        return new THandleIdentifier(this);
    }
    
    @Override
    public void clear() {
        this.guid = null;
        this.secret = null;
    }
    
    public byte[] getGuid() {
        this.setGuid(TBaseHelper.rightSize(this.guid));
        return (byte[])((this.guid == null) ? null : this.guid.array());
    }
    
    public ByteBuffer bufferForGuid() {
        return this.guid;
    }
    
    public void setGuid(final byte[] guid) {
        this.setGuid((guid == null) ? ((ByteBuffer)null) : ByteBuffer.wrap(guid));
    }
    
    public void setGuid(final ByteBuffer guid) {
        this.guid = guid;
    }
    
    public void unsetGuid() {
        this.guid = null;
    }
    
    public boolean isSetGuid() {
        return this.guid != null;
    }
    
    public void setGuidIsSet(final boolean value) {
        if (!value) {
            this.guid = null;
        }
    }
    
    public byte[] getSecret() {
        this.setSecret(TBaseHelper.rightSize(this.secret));
        return (byte[])((this.secret == null) ? null : this.secret.array());
    }
    
    public ByteBuffer bufferForSecret() {
        return this.secret;
    }
    
    public void setSecret(final byte[] secret) {
        this.setSecret((secret == null) ? ((ByteBuffer)null) : ByteBuffer.wrap(secret));
    }
    
    public void setSecret(final ByteBuffer secret) {
        this.secret = secret;
    }
    
    public void unsetSecret() {
        this.secret = null;
    }
    
    public boolean isSetSecret() {
        return this.secret != null;
    }
    
    public void setSecretIsSet(final boolean value) {
        if (!value) {
            this.secret = null;
        }
    }
    
    @Override
    public void setFieldValue(final _Fields field, final Object value) {
        switch (field) {
            case GUID: {
                if (value == null) {
                    this.unsetGuid();
                    break;
                }
                this.setGuid((ByteBuffer)value);
                break;
            }
            case SECRET: {
                if (value == null) {
                    this.unsetSecret();
                    break;
                }
                this.setSecret((ByteBuffer)value);
                break;
            }
        }
    }
    
    @Override
    public Object getFieldValue(final _Fields field) {
        switch (field) {
            case GUID: {
                return this.getGuid();
            }
            case SECRET: {
                return this.getSecret();
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
            case GUID: {
                return this.isSetGuid();
            }
            case SECRET: {
                return this.isSetSecret();
            }
            default: {
                throw new IllegalStateException();
            }
        }
    }
    
    @Override
    public boolean equals(final Object that) {
        return that != null && that instanceof THandleIdentifier && this.equals((THandleIdentifier)that);
    }
    
    public boolean equals(final THandleIdentifier that) {
        if (that == null) {
            return false;
        }
        final boolean this_present_guid = this.isSetGuid();
        final boolean that_present_guid = that.isSetGuid();
        if (this_present_guid || that_present_guid) {
            if (!this_present_guid || !that_present_guid) {
                return false;
            }
            if (!this.guid.equals(that.guid)) {
                return false;
            }
        }
        final boolean this_present_secret = this.isSetSecret();
        final boolean that_present_secret = that.isSetSecret();
        if (this_present_secret || that_present_secret) {
            if (!this_present_secret || !that_present_secret) {
                return false;
            }
            if (!this.secret.equals(that.secret)) {
                return false;
            }
        }
        return true;
    }
    
    @Override
    public int hashCode() {
        final HashCodeBuilder builder = new HashCodeBuilder();
        final boolean present_guid = this.isSetGuid();
        builder.append(present_guid);
        if (present_guid) {
            builder.append(this.guid);
        }
        final boolean present_secret = this.isSetSecret();
        builder.append(present_secret);
        if (present_secret) {
            builder.append(this.secret);
        }
        return builder.toHashCode();
    }
    
    @Override
    public int compareTo(final THandleIdentifier other) {
        if (!this.getClass().equals(other.getClass())) {
            return this.getClass().getName().compareTo(other.getClass().getName());
        }
        int lastComparison = 0;
        final THandleIdentifier typedOther = other;
        lastComparison = Boolean.valueOf(this.isSetGuid()).compareTo(Boolean.valueOf(typedOther.isSetGuid()));
        if (lastComparison != 0) {
            return lastComparison;
        }
        if (this.isSetGuid()) {
            lastComparison = TBaseHelper.compareTo(this.guid, typedOther.guid);
            if (lastComparison != 0) {
                return lastComparison;
            }
        }
        lastComparison = Boolean.valueOf(this.isSetSecret()).compareTo(Boolean.valueOf(typedOther.isSetSecret()));
        if (lastComparison != 0) {
            return lastComparison;
        }
        if (this.isSetSecret()) {
            lastComparison = TBaseHelper.compareTo(this.secret, typedOther.secret);
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
        THandleIdentifier.schemes.get(iprot.getScheme()).getScheme().read(iprot, this);
    }
    
    @Override
    public void write(final TProtocol oprot) throws TException {
        THandleIdentifier.schemes.get(oprot.getScheme()).getScheme().write(oprot, this);
    }
    
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("THandleIdentifier(");
        boolean first = true;
        sb.append("guid:");
        if (this.guid == null) {
            sb.append("null");
        }
        else {
            TBaseHelper.toString(this.guid, sb);
        }
        first = false;
        if (!first) {
            sb.append(", ");
        }
        sb.append("secret:");
        if (this.secret == null) {
            sb.append("null");
        }
        else {
            TBaseHelper.toString(this.secret, sb);
        }
        first = false;
        sb.append(")");
        return sb.toString();
    }
    
    public void validate() throws TException {
        if (!this.isSetGuid()) {
            throw new TProtocolException("Required field 'guid' is unset! Struct:" + this.toString());
        }
        if (!this.isSetSecret()) {
            throw new TProtocolException("Required field 'secret' is unset! Struct:" + this.toString());
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
        STRUCT_DESC = new TStruct("THandleIdentifier");
        GUID_FIELD_DESC = new TField("guid", (byte)11, (short)1);
        SECRET_FIELD_DESC = new TField("secret", (byte)11, (short)2);
        (schemes = new HashMap<Class<? extends IScheme>, SchemeFactory>()).put(StandardScheme.class, new THandleIdentifierStandardSchemeFactory());
        THandleIdentifier.schemes.put(TupleScheme.class, new THandleIdentifierTupleSchemeFactory());
        final Map<_Fields, FieldMetaData> tmpMap = new EnumMap<_Fields, FieldMetaData>(_Fields.class);
        tmpMap.put(_Fields.GUID, new FieldMetaData("guid", (byte)1, new FieldValueMetaData((byte)11, true)));
        tmpMap.put(_Fields.SECRET, new FieldMetaData("secret", (byte)1, new FieldValueMetaData((byte)11, true)));
        FieldMetaData.addStructMetaDataMap(THandleIdentifier.class, metaDataMap = Collections.unmodifiableMap((Map<? extends _Fields, ? extends FieldMetaData>)tmpMap));
    }
    
    public enum _Fields implements TFieldIdEnum
    {
        GUID((short)1, "guid"), 
        SECRET((short)2, "secret");
        
        private static final Map<String, _Fields> byName;
        private final short _thriftId;
        private final String _fieldName;
        
        public static _Fields findByThriftId(final int fieldId) {
            switch (fieldId) {
                case 1: {
                    return _Fields.GUID;
                }
                case 2: {
                    return _Fields.SECRET;
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
    
    private static class THandleIdentifierStandardSchemeFactory implements SchemeFactory
    {
        @Override
        public THandleIdentifierStandardScheme getScheme() {
            return new THandleIdentifierStandardScheme();
        }
    }
    
    private static class THandleIdentifierStandardScheme extends StandardScheme<THandleIdentifier>
    {
        @Override
        public void read(final TProtocol iprot, final THandleIdentifier struct) throws TException {
            iprot.readStructBegin();
            while (true) {
                final TField schemeField = iprot.readFieldBegin();
                if (schemeField.type == 0) {
                    break;
                }
                switch (schemeField.id) {
                    case 1: {
                        if (schemeField.type == 11) {
                            struct.guid = iprot.readBinary();
                            struct.setGuidIsSet(true);
                            break;
                        }
                        TProtocolUtil.skip(iprot, schemeField.type);
                        break;
                    }
                    case 2: {
                        if (schemeField.type == 11) {
                            struct.secret = iprot.readBinary();
                            struct.setSecretIsSet(true);
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
        public void write(final TProtocol oprot, final THandleIdentifier struct) throws TException {
            struct.validate();
            oprot.writeStructBegin(THandleIdentifier.STRUCT_DESC);
            if (struct.guid != null) {
                oprot.writeFieldBegin(THandleIdentifier.GUID_FIELD_DESC);
                oprot.writeBinary(struct.guid);
                oprot.writeFieldEnd();
            }
            if (struct.secret != null) {
                oprot.writeFieldBegin(THandleIdentifier.SECRET_FIELD_DESC);
                oprot.writeBinary(struct.secret);
                oprot.writeFieldEnd();
            }
            oprot.writeFieldStop();
            oprot.writeStructEnd();
        }
    }
    
    private static class THandleIdentifierTupleSchemeFactory implements SchemeFactory
    {
        @Override
        public THandleIdentifierTupleScheme getScheme() {
            return new THandleIdentifierTupleScheme();
        }
    }
    
    private static class THandleIdentifierTupleScheme extends TupleScheme<THandleIdentifier>
    {
        @Override
        public void write(final TProtocol prot, final THandleIdentifier struct) throws TException {
            final TTupleProtocol oprot = (TTupleProtocol)prot;
            oprot.writeBinary(struct.guid);
            oprot.writeBinary(struct.secret);
        }
        
        @Override
        public void read(final TProtocol prot, final THandleIdentifier struct) throws TException {
            final TTupleProtocol iprot = (TTupleProtocol)prot;
            struct.guid = iprot.readBinary();
            struct.setGuidIsSet(true);
            struct.secret = iprot.readBinary();
            struct.setSecretIsSet(true);
        }
    }
}
