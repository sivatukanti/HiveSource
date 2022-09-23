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
import org.apache.thrift.meta_data.FieldMetaData;
import org.apache.thrift.scheme.SchemeFactory;
import org.apache.thrift.scheme.IScheme;
import java.util.Map;
import org.apache.thrift.protocol.TField;
import org.apache.thrift.protocol.TStruct;
import java.io.Serializable;
import org.apache.thrift.TBase;

public class GetRoleGrantsForPrincipalRequest implements TBase<GetRoleGrantsForPrincipalRequest, _Fields>, Serializable, Cloneable
{
    private static final TStruct STRUCT_DESC;
    private static final TField PRINCIPAL_NAME_FIELD_DESC;
    private static final TField PRINCIPAL_TYPE_FIELD_DESC;
    private static final Map<Class<? extends IScheme>, SchemeFactory> schemes;
    private String principal_name;
    private PrincipalType principal_type;
    public static final Map<_Fields, FieldMetaData> metaDataMap;
    
    public GetRoleGrantsForPrincipalRequest() {
    }
    
    public GetRoleGrantsForPrincipalRequest(final String principal_name, final PrincipalType principal_type) {
        this();
        this.principal_name = principal_name;
        this.principal_type = principal_type;
    }
    
    public GetRoleGrantsForPrincipalRequest(final GetRoleGrantsForPrincipalRequest other) {
        if (other.isSetPrincipal_name()) {
            this.principal_name = other.principal_name;
        }
        if (other.isSetPrincipal_type()) {
            this.principal_type = other.principal_type;
        }
    }
    
    @Override
    public GetRoleGrantsForPrincipalRequest deepCopy() {
        return new GetRoleGrantsForPrincipalRequest(this);
    }
    
    @Override
    public void clear() {
        this.principal_name = null;
        this.principal_type = null;
    }
    
    public String getPrincipal_name() {
        return this.principal_name;
    }
    
    public void setPrincipal_name(final String principal_name) {
        this.principal_name = principal_name;
    }
    
    public void unsetPrincipal_name() {
        this.principal_name = null;
    }
    
    public boolean isSetPrincipal_name() {
        return this.principal_name != null;
    }
    
    public void setPrincipal_nameIsSet(final boolean value) {
        if (!value) {
            this.principal_name = null;
        }
    }
    
    public PrincipalType getPrincipal_type() {
        return this.principal_type;
    }
    
    public void setPrincipal_type(final PrincipalType principal_type) {
        this.principal_type = principal_type;
    }
    
    public void unsetPrincipal_type() {
        this.principal_type = null;
    }
    
    public boolean isSetPrincipal_type() {
        return this.principal_type != null;
    }
    
    public void setPrincipal_typeIsSet(final boolean value) {
        if (!value) {
            this.principal_type = null;
        }
    }
    
    @Override
    public void setFieldValue(final _Fields field, final Object value) {
        switch (field) {
            case PRINCIPAL_NAME: {
                if (value == null) {
                    this.unsetPrincipal_name();
                    break;
                }
                this.setPrincipal_name((String)value);
                break;
            }
            case PRINCIPAL_TYPE: {
                if (value == null) {
                    this.unsetPrincipal_type();
                    break;
                }
                this.setPrincipal_type((PrincipalType)value);
                break;
            }
        }
    }
    
    @Override
    public Object getFieldValue(final _Fields field) {
        switch (field) {
            case PRINCIPAL_NAME: {
                return this.getPrincipal_name();
            }
            case PRINCIPAL_TYPE: {
                return this.getPrincipal_type();
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
            case PRINCIPAL_NAME: {
                return this.isSetPrincipal_name();
            }
            case PRINCIPAL_TYPE: {
                return this.isSetPrincipal_type();
            }
            default: {
                throw new IllegalStateException();
            }
        }
    }
    
    @Override
    public boolean equals(final Object that) {
        return that != null && that instanceof GetRoleGrantsForPrincipalRequest && this.equals((GetRoleGrantsForPrincipalRequest)that);
    }
    
    public boolean equals(final GetRoleGrantsForPrincipalRequest that) {
        if (that == null) {
            return false;
        }
        final boolean this_present_principal_name = this.isSetPrincipal_name();
        final boolean that_present_principal_name = that.isSetPrincipal_name();
        if (this_present_principal_name || that_present_principal_name) {
            if (!this_present_principal_name || !that_present_principal_name) {
                return false;
            }
            if (!this.principal_name.equals(that.principal_name)) {
                return false;
            }
        }
        final boolean this_present_principal_type = this.isSetPrincipal_type();
        final boolean that_present_principal_type = that.isSetPrincipal_type();
        if (this_present_principal_type || that_present_principal_type) {
            if (!this_present_principal_type || !that_present_principal_type) {
                return false;
            }
            if (!this.principal_type.equals(that.principal_type)) {
                return false;
            }
        }
        return true;
    }
    
    @Override
    public int hashCode() {
        final HashCodeBuilder builder = new HashCodeBuilder();
        final boolean present_principal_name = this.isSetPrincipal_name();
        builder.append(present_principal_name);
        if (present_principal_name) {
            builder.append(this.principal_name);
        }
        final boolean present_principal_type = this.isSetPrincipal_type();
        builder.append(present_principal_type);
        if (present_principal_type) {
            builder.append(this.principal_type.getValue());
        }
        return builder.toHashCode();
    }
    
    @Override
    public int compareTo(final GetRoleGrantsForPrincipalRequest other) {
        if (!this.getClass().equals(other.getClass())) {
            return this.getClass().getName().compareTo(other.getClass().getName());
        }
        int lastComparison = 0;
        final GetRoleGrantsForPrincipalRequest typedOther = other;
        lastComparison = Boolean.valueOf(this.isSetPrincipal_name()).compareTo(Boolean.valueOf(typedOther.isSetPrincipal_name()));
        if (lastComparison != 0) {
            return lastComparison;
        }
        if (this.isSetPrincipal_name()) {
            lastComparison = TBaseHelper.compareTo(this.principal_name, typedOther.principal_name);
            if (lastComparison != 0) {
                return lastComparison;
            }
        }
        lastComparison = Boolean.valueOf(this.isSetPrincipal_type()).compareTo(Boolean.valueOf(typedOther.isSetPrincipal_type()));
        if (lastComparison != 0) {
            return lastComparison;
        }
        if (this.isSetPrincipal_type()) {
            lastComparison = TBaseHelper.compareTo(this.principal_type, typedOther.principal_type);
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
        GetRoleGrantsForPrincipalRequest.schemes.get(iprot.getScheme()).getScheme().read(iprot, this);
    }
    
    @Override
    public void write(final TProtocol oprot) throws TException {
        GetRoleGrantsForPrincipalRequest.schemes.get(oprot.getScheme()).getScheme().write(oprot, this);
    }
    
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("GetRoleGrantsForPrincipalRequest(");
        boolean first = true;
        sb.append("principal_name:");
        if (this.principal_name == null) {
            sb.append("null");
        }
        else {
            sb.append(this.principal_name);
        }
        first = false;
        if (!first) {
            sb.append(", ");
        }
        sb.append("principal_type:");
        if (this.principal_type == null) {
            sb.append("null");
        }
        else {
            sb.append(this.principal_type);
        }
        first = false;
        sb.append(")");
        return sb.toString();
    }
    
    public void validate() throws TException {
        if (!this.isSetPrincipal_name()) {
            throw new TProtocolException("Required field 'principal_name' is unset! Struct:" + this.toString());
        }
        if (!this.isSetPrincipal_type()) {
            throw new TProtocolException("Required field 'principal_type' is unset! Struct:" + this.toString());
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
        STRUCT_DESC = new TStruct("GetRoleGrantsForPrincipalRequest");
        PRINCIPAL_NAME_FIELD_DESC = new TField("principal_name", (byte)11, (short)1);
        PRINCIPAL_TYPE_FIELD_DESC = new TField("principal_type", (byte)8, (short)2);
        (schemes = new HashMap<Class<? extends IScheme>, SchemeFactory>()).put(StandardScheme.class, new GetRoleGrantsForPrincipalRequestStandardSchemeFactory());
        GetRoleGrantsForPrincipalRequest.schemes.put(TupleScheme.class, new GetRoleGrantsForPrincipalRequestTupleSchemeFactory());
        final Map<_Fields, FieldMetaData> tmpMap = new EnumMap<_Fields, FieldMetaData>(_Fields.class);
        tmpMap.put(_Fields.PRINCIPAL_NAME, new FieldMetaData("principal_name", (byte)1, new FieldValueMetaData((byte)11)));
        tmpMap.put(_Fields.PRINCIPAL_TYPE, new FieldMetaData("principal_type", (byte)1, new EnumMetaData((byte)16, PrincipalType.class)));
        FieldMetaData.addStructMetaDataMap(GetRoleGrantsForPrincipalRequest.class, metaDataMap = Collections.unmodifiableMap((Map<? extends _Fields, ? extends FieldMetaData>)tmpMap));
    }
    
    public enum _Fields implements TFieldIdEnum
    {
        PRINCIPAL_NAME((short)1, "principal_name"), 
        PRINCIPAL_TYPE((short)2, "principal_type");
        
        private static final Map<String, _Fields> byName;
        private final short _thriftId;
        private final String _fieldName;
        
        public static _Fields findByThriftId(final int fieldId) {
            switch (fieldId) {
                case 1: {
                    return _Fields.PRINCIPAL_NAME;
                }
                case 2: {
                    return _Fields.PRINCIPAL_TYPE;
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
    
    private static class GetRoleGrantsForPrincipalRequestStandardSchemeFactory implements SchemeFactory
    {
        @Override
        public GetRoleGrantsForPrincipalRequestStandardScheme getScheme() {
            return new GetRoleGrantsForPrincipalRequestStandardScheme();
        }
    }
    
    private static class GetRoleGrantsForPrincipalRequestStandardScheme extends StandardScheme<GetRoleGrantsForPrincipalRequest>
    {
        @Override
        public void read(final TProtocol iprot, final GetRoleGrantsForPrincipalRequest struct) throws TException {
            iprot.readStructBegin();
            while (true) {
                final TField schemeField = iprot.readFieldBegin();
                if (schemeField.type == 0) {
                    break;
                }
                switch (schemeField.id) {
                    case 1: {
                        if (schemeField.type == 11) {
                            struct.principal_name = iprot.readString();
                            struct.setPrincipal_nameIsSet(true);
                            break;
                        }
                        TProtocolUtil.skip(iprot, schemeField.type);
                        break;
                    }
                    case 2: {
                        if (schemeField.type == 8) {
                            struct.principal_type = PrincipalType.findByValue(iprot.readI32());
                            struct.setPrincipal_typeIsSet(true);
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
        public void write(final TProtocol oprot, final GetRoleGrantsForPrincipalRequest struct) throws TException {
            struct.validate();
            oprot.writeStructBegin(GetRoleGrantsForPrincipalRequest.STRUCT_DESC);
            if (struct.principal_name != null) {
                oprot.writeFieldBegin(GetRoleGrantsForPrincipalRequest.PRINCIPAL_NAME_FIELD_DESC);
                oprot.writeString(struct.principal_name);
                oprot.writeFieldEnd();
            }
            if (struct.principal_type != null) {
                oprot.writeFieldBegin(GetRoleGrantsForPrincipalRequest.PRINCIPAL_TYPE_FIELD_DESC);
                oprot.writeI32(struct.principal_type.getValue());
                oprot.writeFieldEnd();
            }
            oprot.writeFieldStop();
            oprot.writeStructEnd();
        }
    }
    
    private static class GetRoleGrantsForPrincipalRequestTupleSchemeFactory implements SchemeFactory
    {
        @Override
        public GetRoleGrantsForPrincipalRequestTupleScheme getScheme() {
            return new GetRoleGrantsForPrincipalRequestTupleScheme();
        }
    }
    
    private static class GetRoleGrantsForPrincipalRequestTupleScheme extends TupleScheme<GetRoleGrantsForPrincipalRequest>
    {
        @Override
        public void write(final TProtocol prot, final GetRoleGrantsForPrincipalRequest struct) throws TException {
            final TTupleProtocol oprot = (TTupleProtocol)prot;
            oprot.writeString(struct.principal_name);
            oprot.writeI32(struct.principal_type.getValue());
        }
        
        @Override
        public void read(final TProtocol prot, final GetRoleGrantsForPrincipalRequest struct) throws TException {
            final TTupleProtocol iprot = (TTupleProtocol)prot;
            struct.principal_name = iprot.readString();
            struct.setPrincipal_nameIsSet(true);
            struct.principal_type = PrincipalType.findByValue(iprot.readI32());
            struct.setPrincipal_typeIsSet(true);
        }
    }
}
