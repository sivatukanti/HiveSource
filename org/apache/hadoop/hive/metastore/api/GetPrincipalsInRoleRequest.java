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
import org.apache.thrift.meta_data.FieldMetaData;
import org.apache.thrift.scheme.SchemeFactory;
import org.apache.thrift.scheme.IScheme;
import java.util.Map;
import org.apache.thrift.protocol.TField;
import org.apache.thrift.protocol.TStruct;
import java.io.Serializable;
import org.apache.thrift.TBase;

public class GetPrincipalsInRoleRequest implements TBase<GetPrincipalsInRoleRequest, _Fields>, Serializable, Cloneable
{
    private static final TStruct STRUCT_DESC;
    private static final TField ROLE_NAME_FIELD_DESC;
    private static final Map<Class<? extends IScheme>, SchemeFactory> schemes;
    private String roleName;
    public static final Map<_Fields, FieldMetaData> metaDataMap;
    
    public GetPrincipalsInRoleRequest() {
    }
    
    public GetPrincipalsInRoleRequest(final String roleName) {
        this();
        this.roleName = roleName;
    }
    
    public GetPrincipalsInRoleRequest(final GetPrincipalsInRoleRequest other) {
        if (other.isSetRoleName()) {
            this.roleName = other.roleName;
        }
    }
    
    @Override
    public GetPrincipalsInRoleRequest deepCopy() {
        return new GetPrincipalsInRoleRequest(this);
    }
    
    @Override
    public void clear() {
        this.roleName = null;
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
    
    @Override
    public void setFieldValue(final _Fields field, final Object value) {
        switch (field) {
            case ROLE_NAME: {
                if (value == null) {
                    this.unsetRoleName();
                    break;
                }
                this.setRoleName((String)value);
                break;
            }
        }
    }
    
    @Override
    public Object getFieldValue(final _Fields field) {
        switch (field) {
            case ROLE_NAME: {
                return this.getRoleName();
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
            case ROLE_NAME: {
                return this.isSetRoleName();
            }
            default: {
                throw new IllegalStateException();
            }
        }
    }
    
    @Override
    public boolean equals(final Object that) {
        return that != null && that instanceof GetPrincipalsInRoleRequest && this.equals((GetPrincipalsInRoleRequest)that);
    }
    
    public boolean equals(final GetPrincipalsInRoleRequest that) {
        if (that == null) {
            return false;
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
        return true;
    }
    
    @Override
    public int hashCode() {
        final HashCodeBuilder builder = new HashCodeBuilder();
        final boolean present_roleName = this.isSetRoleName();
        builder.append(present_roleName);
        if (present_roleName) {
            builder.append(this.roleName);
        }
        return builder.toHashCode();
    }
    
    @Override
    public int compareTo(final GetPrincipalsInRoleRequest other) {
        if (!this.getClass().equals(other.getClass())) {
            return this.getClass().getName().compareTo(other.getClass().getName());
        }
        int lastComparison = 0;
        final GetPrincipalsInRoleRequest typedOther = other;
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
        return 0;
    }
    
    @Override
    public _Fields fieldForId(final int fieldId) {
        return _Fields.findByThriftId(fieldId);
    }
    
    @Override
    public void read(final TProtocol iprot) throws TException {
        GetPrincipalsInRoleRequest.schemes.get(iprot.getScheme()).getScheme().read(iprot, this);
    }
    
    @Override
    public void write(final TProtocol oprot) throws TException {
        GetPrincipalsInRoleRequest.schemes.get(oprot.getScheme()).getScheme().write(oprot, this);
    }
    
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("GetPrincipalsInRoleRequest(");
        boolean first = true;
        sb.append("roleName:");
        if (this.roleName == null) {
            sb.append("null");
        }
        else {
            sb.append(this.roleName);
        }
        first = false;
        sb.append(")");
        return sb.toString();
    }
    
    public void validate() throws TException {
        if (!this.isSetRoleName()) {
            throw new TProtocolException("Required field 'roleName' is unset! Struct:" + this.toString());
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
        STRUCT_DESC = new TStruct("GetPrincipalsInRoleRequest");
        ROLE_NAME_FIELD_DESC = new TField("roleName", (byte)11, (short)1);
        (schemes = new HashMap<Class<? extends IScheme>, SchemeFactory>()).put(StandardScheme.class, new GetPrincipalsInRoleRequestStandardSchemeFactory());
        GetPrincipalsInRoleRequest.schemes.put(TupleScheme.class, new GetPrincipalsInRoleRequestTupleSchemeFactory());
        final Map<_Fields, FieldMetaData> tmpMap = new EnumMap<_Fields, FieldMetaData>(_Fields.class);
        tmpMap.put(_Fields.ROLE_NAME, new FieldMetaData("roleName", (byte)1, new FieldValueMetaData((byte)11)));
        FieldMetaData.addStructMetaDataMap(GetPrincipalsInRoleRequest.class, metaDataMap = Collections.unmodifiableMap((Map<? extends _Fields, ? extends FieldMetaData>)tmpMap));
    }
    
    public enum _Fields implements TFieldIdEnum
    {
        ROLE_NAME((short)1, "roleName");
        
        private static final Map<String, _Fields> byName;
        private final short _thriftId;
        private final String _fieldName;
        
        public static _Fields findByThriftId(final int fieldId) {
            switch (fieldId) {
                case 1: {
                    return _Fields.ROLE_NAME;
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
    
    private static class GetPrincipalsInRoleRequestStandardSchemeFactory implements SchemeFactory
    {
        @Override
        public GetPrincipalsInRoleRequestStandardScheme getScheme() {
            return new GetPrincipalsInRoleRequestStandardScheme();
        }
    }
    
    private static class GetPrincipalsInRoleRequestStandardScheme extends StandardScheme<GetPrincipalsInRoleRequest>
    {
        @Override
        public void read(final TProtocol iprot, final GetPrincipalsInRoleRequest struct) throws TException {
            iprot.readStructBegin();
            while (true) {
                final TField schemeField = iprot.readFieldBegin();
                if (schemeField.type == 0) {
                    break;
                }
                switch (schemeField.id) {
                    case 1: {
                        if (schemeField.type == 11) {
                            struct.roleName = iprot.readString();
                            struct.setRoleNameIsSet(true);
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
        public void write(final TProtocol oprot, final GetPrincipalsInRoleRequest struct) throws TException {
            struct.validate();
            oprot.writeStructBegin(GetPrincipalsInRoleRequest.STRUCT_DESC);
            if (struct.roleName != null) {
                oprot.writeFieldBegin(GetPrincipalsInRoleRequest.ROLE_NAME_FIELD_DESC);
                oprot.writeString(struct.roleName);
                oprot.writeFieldEnd();
            }
            oprot.writeFieldStop();
            oprot.writeStructEnd();
        }
    }
    
    private static class GetPrincipalsInRoleRequestTupleSchemeFactory implements SchemeFactory
    {
        @Override
        public GetPrincipalsInRoleRequestTupleScheme getScheme() {
            return new GetPrincipalsInRoleRequestTupleScheme();
        }
    }
    
    private static class GetPrincipalsInRoleRequestTupleScheme extends TupleScheme<GetPrincipalsInRoleRequest>
    {
        @Override
        public void write(final TProtocol prot, final GetPrincipalsInRoleRequest struct) throws TException {
            final TTupleProtocol oprot = (TTupleProtocol)prot;
            oprot.writeString(struct.roleName);
        }
        
        @Override
        public void read(final TProtocol prot, final GetPrincipalsInRoleRequest struct) throws TException {
            final TTupleProtocol iprot = (TTupleProtocol)prot;
            struct.roleName = iprot.readString();
            struct.setRoleNameIsSet(true);
        }
    }
}
