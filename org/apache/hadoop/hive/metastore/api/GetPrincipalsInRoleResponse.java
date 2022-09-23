// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.hive.metastore.api;

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

public class GetPrincipalsInRoleResponse implements TBase<GetPrincipalsInRoleResponse, _Fields>, Serializable, Cloneable
{
    private static final TStruct STRUCT_DESC;
    private static final TField PRINCIPAL_GRANTS_FIELD_DESC;
    private static final Map<Class<? extends IScheme>, SchemeFactory> schemes;
    private List<RolePrincipalGrant> principalGrants;
    public static final Map<_Fields, FieldMetaData> metaDataMap;
    
    public GetPrincipalsInRoleResponse() {
    }
    
    public GetPrincipalsInRoleResponse(final List<RolePrincipalGrant> principalGrants) {
        this();
        this.principalGrants = principalGrants;
    }
    
    public GetPrincipalsInRoleResponse(final GetPrincipalsInRoleResponse other) {
        if (other.isSetPrincipalGrants()) {
            final List<RolePrincipalGrant> __this__principalGrants = new ArrayList<RolePrincipalGrant>();
            for (final RolePrincipalGrant other_element : other.principalGrants) {
                __this__principalGrants.add(new RolePrincipalGrant(other_element));
            }
            this.principalGrants = __this__principalGrants;
        }
    }
    
    @Override
    public GetPrincipalsInRoleResponse deepCopy() {
        return new GetPrincipalsInRoleResponse(this);
    }
    
    @Override
    public void clear() {
        this.principalGrants = null;
    }
    
    public int getPrincipalGrantsSize() {
        return (this.principalGrants == null) ? 0 : this.principalGrants.size();
    }
    
    public Iterator<RolePrincipalGrant> getPrincipalGrantsIterator() {
        return (this.principalGrants == null) ? null : this.principalGrants.iterator();
    }
    
    public void addToPrincipalGrants(final RolePrincipalGrant elem) {
        if (this.principalGrants == null) {
            this.principalGrants = new ArrayList<RolePrincipalGrant>();
        }
        this.principalGrants.add(elem);
    }
    
    public List<RolePrincipalGrant> getPrincipalGrants() {
        return this.principalGrants;
    }
    
    public void setPrincipalGrants(final List<RolePrincipalGrant> principalGrants) {
        this.principalGrants = principalGrants;
    }
    
    public void unsetPrincipalGrants() {
        this.principalGrants = null;
    }
    
    public boolean isSetPrincipalGrants() {
        return this.principalGrants != null;
    }
    
    public void setPrincipalGrantsIsSet(final boolean value) {
        if (!value) {
            this.principalGrants = null;
        }
    }
    
    @Override
    public void setFieldValue(final _Fields field, final Object value) {
        switch (field) {
            case PRINCIPAL_GRANTS: {
                if (value == null) {
                    this.unsetPrincipalGrants();
                    break;
                }
                this.setPrincipalGrants((List<RolePrincipalGrant>)value);
                break;
            }
        }
    }
    
    @Override
    public Object getFieldValue(final _Fields field) {
        switch (field) {
            case PRINCIPAL_GRANTS: {
                return this.getPrincipalGrants();
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
            case PRINCIPAL_GRANTS: {
                return this.isSetPrincipalGrants();
            }
            default: {
                throw new IllegalStateException();
            }
        }
    }
    
    @Override
    public boolean equals(final Object that) {
        return that != null && that instanceof GetPrincipalsInRoleResponse && this.equals((GetPrincipalsInRoleResponse)that);
    }
    
    public boolean equals(final GetPrincipalsInRoleResponse that) {
        if (that == null) {
            return false;
        }
        final boolean this_present_principalGrants = this.isSetPrincipalGrants();
        final boolean that_present_principalGrants = that.isSetPrincipalGrants();
        if (this_present_principalGrants || that_present_principalGrants) {
            if (!this_present_principalGrants || !that_present_principalGrants) {
                return false;
            }
            if (!this.principalGrants.equals(that.principalGrants)) {
                return false;
            }
        }
        return true;
    }
    
    @Override
    public int hashCode() {
        final HashCodeBuilder builder = new HashCodeBuilder();
        final boolean present_principalGrants = this.isSetPrincipalGrants();
        builder.append(present_principalGrants);
        if (present_principalGrants) {
            builder.append(this.principalGrants);
        }
        return builder.toHashCode();
    }
    
    @Override
    public int compareTo(final GetPrincipalsInRoleResponse other) {
        if (!this.getClass().equals(other.getClass())) {
            return this.getClass().getName().compareTo(other.getClass().getName());
        }
        int lastComparison = 0;
        final GetPrincipalsInRoleResponse typedOther = other;
        lastComparison = Boolean.valueOf(this.isSetPrincipalGrants()).compareTo(Boolean.valueOf(typedOther.isSetPrincipalGrants()));
        if (lastComparison != 0) {
            return lastComparison;
        }
        if (this.isSetPrincipalGrants()) {
            lastComparison = TBaseHelper.compareTo(this.principalGrants, typedOther.principalGrants);
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
        GetPrincipalsInRoleResponse.schemes.get(iprot.getScheme()).getScheme().read(iprot, this);
    }
    
    @Override
    public void write(final TProtocol oprot) throws TException {
        GetPrincipalsInRoleResponse.schemes.get(oprot.getScheme()).getScheme().write(oprot, this);
    }
    
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("GetPrincipalsInRoleResponse(");
        boolean first = true;
        sb.append("principalGrants:");
        if (this.principalGrants == null) {
            sb.append("null");
        }
        else {
            sb.append(this.principalGrants);
        }
        first = false;
        sb.append(")");
        return sb.toString();
    }
    
    public void validate() throws TException {
        if (!this.isSetPrincipalGrants()) {
            throw new TProtocolException("Required field 'principalGrants' is unset! Struct:" + this.toString());
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
        STRUCT_DESC = new TStruct("GetPrincipalsInRoleResponse");
        PRINCIPAL_GRANTS_FIELD_DESC = new TField("principalGrants", (byte)15, (short)1);
        (schemes = new HashMap<Class<? extends IScheme>, SchemeFactory>()).put(StandardScheme.class, new GetPrincipalsInRoleResponseStandardSchemeFactory());
        GetPrincipalsInRoleResponse.schemes.put(TupleScheme.class, new GetPrincipalsInRoleResponseTupleSchemeFactory());
        final Map<_Fields, FieldMetaData> tmpMap = new EnumMap<_Fields, FieldMetaData>(_Fields.class);
        tmpMap.put(_Fields.PRINCIPAL_GRANTS, new FieldMetaData("principalGrants", (byte)1, new ListMetaData((byte)15, new StructMetaData((byte)12, RolePrincipalGrant.class))));
        FieldMetaData.addStructMetaDataMap(GetPrincipalsInRoleResponse.class, metaDataMap = Collections.unmodifiableMap((Map<? extends _Fields, ? extends FieldMetaData>)tmpMap));
    }
    
    public enum _Fields implements TFieldIdEnum
    {
        PRINCIPAL_GRANTS((short)1, "principalGrants");
        
        private static final Map<String, _Fields> byName;
        private final short _thriftId;
        private final String _fieldName;
        
        public static _Fields findByThriftId(final int fieldId) {
            switch (fieldId) {
                case 1: {
                    return _Fields.PRINCIPAL_GRANTS;
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
    
    private static class GetPrincipalsInRoleResponseStandardSchemeFactory implements SchemeFactory
    {
        @Override
        public GetPrincipalsInRoleResponseStandardScheme getScheme() {
            return new GetPrincipalsInRoleResponseStandardScheme();
        }
    }
    
    private static class GetPrincipalsInRoleResponseStandardScheme extends StandardScheme<GetPrincipalsInRoleResponse>
    {
        @Override
        public void read(final TProtocol iprot, final GetPrincipalsInRoleResponse struct) throws TException {
            iprot.readStructBegin();
            while (true) {
                final TField schemeField = iprot.readFieldBegin();
                if (schemeField.type == 0) {
                    break;
                }
                switch (schemeField.id) {
                    case 1: {
                        if (schemeField.type == 15) {
                            final TList _list86 = iprot.readListBegin();
                            struct.principalGrants = (List<RolePrincipalGrant>)new ArrayList(_list86.size);
                            for (int _i87 = 0; _i87 < _list86.size; ++_i87) {
                                final RolePrincipalGrant _elem88 = new RolePrincipalGrant();
                                _elem88.read(iprot);
                                struct.principalGrants.add(_elem88);
                            }
                            iprot.readListEnd();
                            struct.setPrincipalGrantsIsSet(true);
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
        public void write(final TProtocol oprot, final GetPrincipalsInRoleResponse struct) throws TException {
            struct.validate();
            oprot.writeStructBegin(GetPrincipalsInRoleResponse.STRUCT_DESC);
            if (struct.principalGrants != null) {
                oprot.writeFieldBegin(GetPrincipalsInRoleResponse.PRINCIPAL_GRANTS_FIELD_DESC);
                oprot.writeListBegin(new TList((byte)12, struct.principalGrants.size()));
                for (final RolePrincipalGrant _iter89 : struct.principalGrants) {
                    _iter89.write(oprot);
                }
                oprot.writeListEnd();
                oprot.writeFieldEnd();
            }
            oprot.writeFieldStop();
            oprot.writeStructEnd();
        }
    }
    
    private static class GetPrincipalsInRoleResponseTupleSchemeFactory implements SchemeFactory
    {
        @Override
        public GetPrincipalsInRoleResponseTupleScheme getScheme() {
            return new GetPrincipalsInRoleResponseTupleScheme();
        }
    }
    
    private static class GetPrincipalsInRoleResponseTupleScheme extends TupleScheme<GetPrincipalsInRoleResponse>
    {
        @Override
        public void write(final TProtocol prot, final GetPrincipalsInRoleResponse struct) throws TException {
            final TTupleProtocol oprot = (TTupleProtocol)prot;
            oprot.writeI32(struct.principalGrants.size());
            for (final RolePrincipalGrant _iter90 : struct.principalGrants) {
                _iter90.write(oprot);
            }
        }
        
        @Override
        public void read(final TProtocol prot, final GetPrincipalsInRoleResponse struct) throws TException {
            final TTupleProtocol iprot = (TTupleProtocol)prot;
            final TList _list91 = new TList((byte)12, iprot.readI32());
            struct.principalGrants = (List<RolePrincipalGrant>)new ArrayList(_list91.size);
            for (int _i92 = 0; _i92 < _list91.size; ++_i92) {
                final RolePrincipalGrant _elem93 = new RolePrincipalGrant();
                _elem93.read(iprot);
                struct.principalGrants.add(_elem93);
            }
            struct.setPrincipalGrantsIsSet(true);
        }
    }
}
