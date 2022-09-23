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

public class PrivilegeBag implements TBase<PrivilegeBag, _Fields>, Serializable, Cloneable
{
    private static final TStruct STRUCT_DESC;
    private static final TField PRIVILEGES_FIELD_DESC;
    private static final Map<Class<? extends IScheme>, SchemeFactory> schemes;
    private List<HiveObjectPrivilege> privileges;
    public static final Map<_Fields, FieldMetaData> metaDataMap;
    
    public PrivilegeBag() {
    }
    
    public PrivilegeBag(final List<HiveObjectPrivilege> privileges) {
        this();
        this.privileges = privileges;
    }
    
    public PrivilegeBag(final PrivilegeBag other) {
        if (other.isSetPrivileges()) {
            final List<HiveObjectPrivilege> __this__privileges = new ArrayList<HiveObjectPrivilege>();
            for (final HiveObjectPrivilege other_element : other.privileges) {
                __this__privileges.add(new HiveObjectPrivilege(other_element));
            }
            this.privileges = __this__privileges;
        }
    }
    
    @Override
    public PrivilegeBag deepCopy() {
        return new PrivilegeBag(this);
    }
    
    @Override
    public void clear() {
        this.privileges = null;
    }
    
    public int getPrivilegesSize() {
        return (this.privileges == null) ? 0 : this.privileges.size();
    }
    
    public Iterator<HiveObjectPrivilege> getPrivilegesIterator() {
        return (this.privileges == null) ? null : this.privileges.iterator();
    }
    
    public void addToPrivileges(final HiveObjectPrivilege elem) {
        if (this.privileges == null) {
            this.privileges = new ArrayList<HiveObjectPrivilege>();
        }
        this.privileges.add(elem);
    }
    
    public List<HiveObjectPrivilege> getPrivileges() {
        return this.privileges;
    }
    
    public void setPrivileges(final List<HiveObjectPrivilege> privileges) {
        this.privileges = privileges;
    }
    
    public void unsetPrivileges() {
        this.privileges = null;
    }
    
    public boolean isSetPrivileges() {
        return this.privileges != null;
    }
    
    public void setPrivilegesIsSet(final boolean value) {
        if (!value) {
            this.privileges = null;
        }
    }
    
    @Override
    public void setFieldValue(final _Fields field, final Object value) {
        switch (field) {
            case PRIVILEGES: {
                if (value == null) {
                    this.unsetPrivileges();
                    break;
                }
                this.setPrivileges((List<HiveObjectPrivilege>)value);
                break;
            }
        }
    }
    
    @Override
    public Object getFieldValue(final _Fields field) {
        switch (field) {
            case PRIVILEGES: {
                return this.getPrivileges();
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
            case PRIVILEGES: {
                return this.isSetPrivileges();
            }
            default: {
                throw new IllegalStateException();
            }
        }
    }
    
    @Override
    public boolean equals(final Object that) {
        return that != null && that instanceof PrivilegeBag && this.equals((PrivilegeBag)that);
    }
    
    public boolean equals(final PrivilegeBag that) {
        if (that == null) {
            return false;
        }
        final boolean this_present_privileges = this.isSetPrivileges();
        final boolean that_present_privileges = that.isSetPrivileges();
        if (this_present_privileges || that_present_privileges) {
            if (!this_present_privileges || !that_present_privileges) {
                return false;
            }
            if (!this.privileges.equals(that.privileges)) {
                return false;
            }
        }
        return true;
    }
    
    @Override
    public int hashCode() {
        final HashCodeBuilder builder = new HashCodeBuilder();
        final boolean present_privileges = this.isSetPrivileges();
        builder.append(present_privileges);
        if (present_privileges) {
            builder.append(this.privileges);
        }
        return builder.toHashCode();
    }
    
    @Override
    public int compareTo(final PrivilegeBag other) {
        if (!this.getClass().equals(other.getClass())) {
            return this.getClass().getName().compareTo(other.getClass().getName());
        }
        int lastComparison = 0;
        final PrivilegeBag typedOther = other;
        lastComparison = Boolean.valueOf(this.isSetPrivileges()).compareTo(Boolean.valueOf(typedOther.isSetPrivileges()));
        if (lastComparison != 0) {
            return lastComparison;
        }
        if (this.isSetPrivileges()) {
            lastComparison = TBaseHelper.compareTo(this.privileges, typedOther.privileges);
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
        PrivilegeBag.schemes.get(iprot.getScheme()).getScheme().read(iprot, this);
    }
    
    @Override
    public void write(final TProtocol oprot) throws TException {
        PrivilegeBag.schemes.get(oprot.getScheme()).getScheme().write(oprot, this);
    }
    
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("PrivilegeBag(");
        boolean first = true;
        sb.append("privileges:");
        if (this.privileges == null) {
            sb.append("null");
        }
        else {
            sb.append(this.privileges);
        }
        first = false;
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
            this.read(new TCompactProtocol(new TIOStreamTransport(in)));
        }
        catch (TException te) {
            throw new IOException(te);
        }
    }
    
    static {
        STRUCT_DESC = new TStruct("PrivilegeBag");
        PRIVILEGES_FIELD_DESC = new TField("privileges", (byte)15, (short)1);
        (schemes = new HashMap<Class<? extends IScheme>, SchemeFactory>()).put(StandardScheme.class, new PrivilegeBagStandardSchemeFactory());
        PrivilegeBag.schemes.put(TupleScheme.class, new PrivilegeBagTupleSchemeFactory());
        final Map<_Fields, FieldMetaData> tmpMap = new EnumMap<_Fields, FieldMetaData>(_Fields.class);
        tmpMap.put(_Fields.PRIVILEGES, new FieldMetaData("privileges", (byte)3, new ListMetaData((byte)15, new StructMetaData((byte)12, HiveObjectPrivilege.class))));
        FieldMetaData.addStructMetaDataMap(PrivilegeBag.class, metaDataMap = Collections.unmodifiableMap((Map<? extends _Fields, ? extends FieldMetaData>)tmpMap));
    }
    
    public enum _Fields implements TFieldIdEnum
    {
        PRIVILEGES((short)1, "privileges");
        
        private static final Map<String, _Fields> byName;
        private final short _thriftId;
        private final String _fieldName;
        
        public static _Fields findByThriftId(final int fieldId) {
            switch (fieldId) {
                case 1: {
                    return _Fields.PRIVILEGES;
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
    
    private static class PrivilegeBagStandardSchemeFactory implements SchemeFactory
    {
        @Override
        public PrivilegeBagStandardScheme getScheme() {
            return new PrivilegeBagStandardScheme();
        }
    }
    
    private static class PrivilegeBagStandardScheme extends StandardScheme<PrivilegeBag>
    {
        @Override
        public void read(final TProtocol iprot, final PrivilegeBag struct) throws TException {
            iprot.readStructBegin();
            while (true) {
                final TField schemeField = iprot.readFieldBegin();
                if (schemeField.type == 0) {
                    break;
                }
                switch (schemeField.id) {
                    case 1: {
                        if (schemeField.type == 15) {
                            final TList _list16 = iprot.readListBegin();
                            struct.privileges = (List<HiveObjectPrivilege>)new ArrayList(_list16.size);
                            for (int _i17 = 0; _i17 < _list16.size; ++_i17) {
                                final HiveObjectPrivilege _elem18 = new HiveObjectPrivilege();
                                _elem18.read(iprot);
                                struct.privileges.add(_elem18);
                            }
                            iprot.readListEnd();
                            struct.setPrivilegesIsSet(true);
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
        public void write(final TProtocol oprot, final PrivilegeBag struct) throws TException {
            struct.validate();
            oprot.writeStructBegin(PrivilegeBag.STRUCT_DESC);
            if (struct.privileges != null) {
                oprot.writeFieldBegin(PrivilegeBag.PRIVILEGES_FIELD_DESC);
                oprot.writeListBegin(new TList((byte)12, struct.privileges.size()));
                for (final HiveObjectPrivilege _iter19 : struct.privileges) {
                    _iter19.write(oprot);
                }
                oprot.writeListEnd();
                oprot.writeFieldEnd();
            }
            oprot.writeFieldStop();
            oprot.writeStructEnd();
        }
    }
    
    private static class PrivilegeBagTupleSchemeFactory implements SchemeFactory
    {
        @Override
        public PrivilegeBagTupleScheme getScheme() {
            return new PrivilegeBagTupleScheme();
        }
    }
    
    private static class PrivilegeBagTupleScheme extends TupleScheme<PrivilegeBag>
    {
        @Override
        public void write(final TProtocol prot, final PrivilegeBag struct) throws TException {
            final TTupleProtocol oprot = (TTupleProtocol)prot;
            final BitSet optionals = new BitSet();
            if (struct.isSetPrivileges()) {
                optionals.set(0);
            }
            oprot.writeBitSet(optionals, 1);
            if (struct.isSetPrivileges()) {
                oprot.writeI32(struct.privileges.size());
                for (final HiveObjectPrivilege _iter20 : struct.privileges) {
                    _iter20.write(oprot);
                }
            }
        }
        
        @Override
        public void read(final TProtocol prot, final PrivilegeBag struct) throws TException {
            final TTupleProtocol iprot = (TTupleProtocol)prot;
            final BitSet incoming = iprot.readBitSet(1);
            if (incoming.get(0)) {
                final TList _list21 = new TList((byte)12, iprot.readI32());
                struct.privileges = (List<HiveObjectPrivilege>)new ArrayList(_list21.size);
                for (int _i22 = 0; _i22 < _list21.size; ++_i22) {
                    final HiveObjectPrivilege _elem23 = new HiveObjectPrivilege();
                    _elem23.read(iprot);
                    struct.privileges.add(_elem23);
                }
                struct.setPrivilegesIsSet(true);
            }
        }
    }
}
