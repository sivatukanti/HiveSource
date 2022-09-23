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

public class GrantRevokePrivilegeResponse implements TBase<GrantRevokePrivilegeResponse, _Fields>, Serializable, Cloneable
{
    private static final TStruct STRUCT_DESC;
    private static final TField SUCCESS_FIELD_DESC;
    private static final Map<Class<? extends IScheme>, SchemeFactory> schemes;
    private boolean success;
    private static final int __SUCCESS_ISSET_ID = 0;
    private byte __isset_bitfield;
    private _Fields[] optionals;
    public static final Map<_Fields, FieldMetaData> metaDataMap;
    
    public GrantRevokePrivilegeResponse() {
        this.__isset_bitfield = 0;
        this.optionals = new _Fields[] { _Fields.SUCCESS };
    }
    
    public GrantRevokePrivilegeResponse(final GrantRevokePrivilegeResponse other) {
        this.__isset_bitfield = 0;
        this.optionals = new _Fields[] { _Fields.SUCCESS };
        this.__isset_bitfield = other.__isset_bitfield;
        this.success = other.success;
    }
    
    @Override
    public GrantRevokePrivilegeResponse deepCopy() {
        return new GrantRevokePrivilegeResponse(this);
    }
    
    @Override
    public void clear() {
        this.setSuccessIsSet(false);
        this.success = false;
    }
    
    public boolean isSuccess() {
        return this.success;
    }
    
    public void setSuccess(final boolean success) {
        this.success = success;
        this.setSuccessIsSet(true);
    }
    
    public void unsetSuccess() {
        this.__isset_bitfield = EncodingUtils.clearBit(this.__isset_bitfield, 0);
    }
    
    public boolean isSetSuccess() {
        return EncodingUtils.testBit(this.__isset_bitfield, 0);
    }
    
    public void setSuccessIsSet(final boolean value) {
        this.__isset_bitfield = EncodingUtils.setBit(this.__isset_bitfield, 0, value);
    }
    
    @Override
    public void setFieldValue(final _Fields field, final Object value) {
        switch (field) {
            case SUCCESS: {
                if (value == null) {
                    this.unsetSuccess();
                    break;
                }
                this.setSuccess((boolean)value);
                break;
            }
        }
    }
    
    @Override
    public Object getFieldValue(final _Fields field) {
        switch (field) {
            case SUCCESS: {
                return this.isSuccess();
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
            case SUCCESS: {
                return this.isSetSuccess();
            }
            default: {
                throw new IllegalStateException();
            }
        }
    }
    
    @Override
    public boolean equals(final Object that) {
        return that != null && that instanceof GrantRevokePrivilegeResponse && this.equals((GrantRevokePrivilegeResponse)that);
    }
    
    public boolean equals(final GrantRevokePrivilegeResponse that) {
        if (that == null) {
            return false;
        }
        final boolean this_present_success = this.isSetSuccess();
        final boolean that_present_success = that.isSetSuccess();
        if (this_present_success || that_present_success) {
            if (!this_present_success || !that_present_success) {
                return false;
            }
            if (this.success != that.success) {
                return false;
            }
        }
        return true;
    }
    
    @Override
    public int hashCode() {
        final HashCodeBuilder builder = new HashCodeBuilder();
        final boolean present_success = this.isSetSuccess();
        builder.append(present_success);
        if (present_success) {
            builder.append(this.success);
        }
        return builder.toHashCode();
    }
    
    @Override
    public int compareTo(final GrantRevokePrivilegeResponse other) {
        if (!this.getClass().equals(other.getClass())) {
            return this.getClass().getName().compareTo(other.getClass().getName());
        }
        int lastComparison = 0;
        final GrantRevokePrivilegeResponse typedOther = other;
        lastComparison = Boolean.valueOf(this.isSetSuccess()).compareTo(Boolean.valueOf(typedOther.isSetSuccess()));
        if (lastComparison != 0) {
            return lastComparison;
        }
        if (this.isSetSuccess()) {
            lastComparison = TBaseHelper.compareTo(this.success, typedOther.success);
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
        GrantRevokePrivilegeResponse.schemes.get(iprot.getScheme()).getScheme().read(iprot, this);
    }
    
    @Override
    public void write(final TProtocol oprot) throws TException {
        GrantRevokePrivilegeResponse.schemes.get(oprot.getScheme()).getScheme().write(oprot, this);
    }
    
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("GrantRevokePrivilegeResponse(");
        boolean first = true;
        if (this.isSetSuccess()) {
            sb.append("success:");
            sb.append(this.success);
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
        STRUCT_DESC = new TStruct("GrantRevokePrivilegeResponse");
        SUCCESS_FIELD_DESC = new TField("success", (byte)2, (short)1);
        (schemes = new HashMap<Class<? extends IScheme>, SchemeFactory>()).put(StandardScheme.class, new GrantRevokePrivilegeResponseStandardSchemeFactory());
        GrantRevokePrivilegeResponse.schemes.put(TupleScheme.class, new GrantRevokePrivilegeResponseTupleSchemeFactory());
        final Map<_Fields, FieldMetaData> tmpMap = new EnumMap<_Fields, FieldMetaData>(_Fields.class);
        tmpMap.put(_Fields.SUCCESS, new FieldMetaData("success", (byte)2, new FieldValueMetaData((byte)2)));
        FieldMetaData.addStructMetaDataMap(GrantRevokePrivilegeResponse.class, metaDataMap = Collections.unmodifiableMap((Map<? extends _Fields, ? extends FieldMetaData>)tmpMap));
    }
    
    public enum _Fields implements TFieldIdEnum
    {
        SUCCESS((short)1, "success");
        
        private static final Map<String, _Fields> byName;
        private final short _thriftId;
        private final String _fieldName;
        
        public static _Fields findByThriftId(final int fieldId) {
            switch (fieldId) {
                case 1: {
                    return _Fields.SUCCESS;
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
    
    private static class GrantRevokePrivilegeResponseStandardSchemeFactory implements SchemeFactory
    {
        @Override
        public GrantRevokePrivilegeResponseStandardScheme getScheme() {
            return new GrantRevokePrivilegeResponseStandardScheme();
        }
    }
    
    private static class GrantRevokePrivilegeResponseStandardScheme extends StandardScheme<GrantRevokePrivilegeResponse>
    {
        @Override
        public void read(final TProtocol iprot, final GrantRevokePrivilegeResponse struct) throws TException {
            iprot.readStructBegin();
            while (true) {
                final TField schemeField = iprot.readFieldBegin();
                if (schemeField.type == 0) {
                    break;
                }
                switch (schemeField.id) {
                    case 1: {
                        if (schemeField.type == 2) {
                            struct.success = iprot.readBool();
                            struct.setSuccessIsSet(true);
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
        public void write(final TProtocol oprot, final GrantRevokePrivilegeResponse struct) throws TException {
            struct.validate();
            oprot.writeStructBegin(GrantRevokePrivilegeResponse.STRUCT_DESC);
            if (struct.isSetSuccess()) {
                oprot.writeFieldBegin(GrantRevokePrivilegeResponse.SUCCESS_FIELD_DESC);
                oprot.writeBool(struct.success);
                oprot.writeFieldEnd();
            }
            oprot.writeFieldStop();
            oprot.writeStructEnd();
        }
    }
    
    private static class GrantRevokePrivilegeResponseTupleSchemeFactory implements SchemeFactory
    {
        @Override
        public GrantRevokePrivilegeResponseTupleScheme getScheme() {
            return new GrantRevokePrivilegeResponseTupleScheme();
        }
    }
    
    private static class GrantRevokePrivilegeResponseTupleScheme extends TupleScheme<GrantRevokePrivilegeResponse>
    {
        @Override
        public void write(final TProtocol prot, final GrantRevokePrivilegeResponse struct) throws TException {
            final TTupleProtocol oprot = (TTupleProtocol)prot;
            final BitSet optionals = new BitSet();
            if (struct.isSetSuccess()) {
                optionals.set(0);
            }
            oprot.writeBitSet(optionals, 1);
            if (struct.isSetSuccess()) {
                oprot.writeBool(struct.success);
            }
        }
        
        @Override
        public void read(final TProtocol prot, final GrantRevokePrivilegeResponse struct) throws TException {
            final TTupleProtocol iprot = (TTupleProtocol)prot;
            final BitSet incoming = iprot.readBitSet(1);
            if (incoming.get(0)) {
                struct.success = iprot.readBool();
                struct.setSuccessIsSet(true);
            }
        }
    }
}
