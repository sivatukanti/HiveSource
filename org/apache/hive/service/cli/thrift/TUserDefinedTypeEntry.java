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

public class TUserDefinedTypeEntry implements TBase<TUserDefinedTypeEntry, _Fields>, Serializable, Cloneable
{
    private static final TStruct STRUCT_DESC;
    private static final TField TYPE_CLASS_NAME_FIELD_DESC;
    private static final Map<Class<? extends IScheme>, SchemeFactory> schemes;
    private String typeClassName;
    public static final Map<_Fields, FieldMetaData> metaDataMap;
    
    public TUserDefinedTypeEntry() {
    }
    
    public TUserDefinedTypeEntry(final String typeClassName) {
        this();
        this.typeClassName = typeClassName;
    }
    
    public TUserDefinedTypeEntry(final TUserDefinedTypeEntry other) {
        if (other.isSetTypeClassName()) {
            this.typeClassName = other.typeClassName;
        }
    }
    
    @Override
    public TUserDefinedTypeEntry deepCopy() {
        return new TUserDefinedTypeEntry(this);
    }
    
    @Override
    public void clear() {
        this.typeClassName = null;
    }
    
    public String getTypeClassName() {
        return this.typeClassName;
    }
    
    public void setTypeClassName(final String typeClassName) {
        this.typeClassName = typeClassName;
    }
    
    public void unsetTypeClassName() {
        this.typeClassName = null;
    }
    
    public boolean isSetTypeClassName() {
        return this.typeClassName != null;
    }
    
    public void setTypeClassNameIsSet(final boolean value) {
        if (!value) {
            this.typeClassName = null;
        }
    }
    
    @Override
    public void setFieldValue(final _Fields field, final Object value) {
        switch (field) {
            case TYPE_CLASS_NAME: {
                if (value == null) {
                    this.unsetTypeClassName();
                    break;
                }
                this.setTypeClassName((String)value);
                break;
            }
        }
    }
    
    @Override
    public Object getFieldValue(final _Fields field) {
        switch (field) {
            case TYPE_CLASS_NAME: {
                return this.getTypeClassName();
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
            case TYPE_CLASS_NAME: {
                return this.isSetTypeClassName();
            }
            default: {
                throw new IllegalStateException();
            }
        }
    }
    
    @Override
    public boolean equals(final Object that) {
        return that != null && that instanceof TUserDefinedTypeEntry && this.equals((TUserDefinedTypeEntry)that);
    }
    
    public boolean equals(final TUserDefinedTypeEntry that) {
        if (that == null) {
            return false;
        }
        final boolean this_present_typeClassName = this.isSetTypeClassName();
        final boolean that_present_typeClassName = that.isSetTypeClassName();
        if (this_present_typeClassName || that_present_typeClassName) {
            if (!this_present_typeClassName || !that_present_typeClassName) {
                return false;
            }
            if (!this.typeClassName.equals(that.typeClassName)) {
                return false;
            }
        }
        return true;
    }
    
    @Override
    public int hashCode() {
        final HashCodeBuilder builder = new HashCodeBuilder();
        final boolean present_typeClassName = this.isSetTypeClassName();
        builder.append(present_typeClassName);
        if (present_typeClassName) {
            builder.append(this.typeClassName);
        }
        return builder.toHashCode();
    }
    
    @Override
    public int compareTo(final TUserDefinedTypeEntry other) {
        if (!this.getClass().equals(other.getClass())) {
            return this.getClass().getName().compareTo(other.getClass().getName());
        }
        int lastComparison = 0;
        final TUserDefinedTypeEntry typedOther = other;
        lastComparison = Boolean.valueOf(this.isSetTypeClassName()).compareTo(Boolean.valueOf(typedOther.isSetTypeClassName()));
        if (lastComparison != 0) {
            return lastComparison;
        }
        if (this.isSetTypeClassName()) {
            lastComparison = TBaseHelper.compareTo(this.typeClassName, typedOther.typeClassName);
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
        TUserDefinedTypeEntry.schemes.get(iprot.getScheme()).getScheme().read(iprot, this);
    }
    
    @Override
    public void write(final TProtocol oprot) throws TException {
        TUserDefinedTypeEntry.schemes.get(oprot.getScheme()).getScheme().write(oprot, this);
    }
    
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("TUserDefinedTypeEntry(");
        boolean first = true;
        sb.append("typeClassName:");
        if (this.typeClassName == null) {
            sb.append("null");
        }
        else {
            sb.append(this.typeClassName);
        }
        first = false;
        sb.append(")");
        return sb.toString();
    }
    
    public void validate() throws TException {
        if (!this.isSetTypeClassName()) {
            throw new TProtocolException("Required field 'typeClassName' is unset! Struct:" + this.toString());
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
        STRUCT_DESC = new TStruct("TUserDefinedTypeEntry");
        TYPE_CLASS_NAME_FIELD_DESC = new TField("typeClassName", (byte)11, (short)1);
        (schemes = new HashMap<Class<? extends IScheme>, SchemeFactory>()).put(StandardScheme.class, new TUserDefinedTypeEntryStandardSchemeFactory());
        TUserDefinedTypeEntry.schemes.put(TupleScheme.class, new TUserDefinedTypeEntryTupleSchemeFactory());
        final Map<_Fields, FieldMetaData> tmpMap = new EnumMap<_Fields, FieldMetaData>(_Fields.class);
        tmpMap.put(_Fields.TYPE_CLASS_NAME, new FieldMetaData("typeClassName", (byte)1, new FieldValueMetaData((byte)11)));
        FieldMetaData.addStructMetaDataMap(TUserDefinedTypeEntry.class, metaDataMap = Collections.unmodifiableMap((Map<? extends _Fields, ? extends FieldMetaData>)tmpMap));
    }
    
    public enum _Fields implements TFieldIdEnum
    {
        TYPE_CLASS_NAME((short)1, "typeClassName");
        
        private static final Map<String, _Fields> byName;
        private final short _thriftId;
        private final String _fieldName;
        
        public static _Fields findByThriftId(final int fieldId) {
            switch (fieldId) {
                case 1: {
                    return _Fields.TYPE_CLASS_NAME;
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
    
    private static class TUserDefinedTypeEntryStandardSchemeFactory implements SchemeFactory
    {
        @Override
        public TUserDefinedTypeEntryStandardScheme getScheme() {
            return new TUserDefinedTypeEntryStandardScheme();
        }
    }
    
    private static class TUserDefinedTypeEntryStandardScheme extends StandardScheme<TUserDefinedTypeEntry>
    {
        @Override
        public void read(final TProtocol iprot, final TUserDefinedTypeEntry struct) throws TException {
            iprot.readStructBegin();
            while (true) {
                final TField schemeField = iprot.readFieldBegin();
                if (schemeField.type == 0) {
                    break;
                }
                switch (schemeField.id) {
                    case 1: {
                        if (schemeField.type == 11) {
                            struct.typeClassName = iprot.readString();
                            struct.setTypeClassNameIsSet(true);
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
        public void write(final TProtocol oprot, final TUserDefinedTypeEntry struct) throws TException {
            struct.validate();
            oprot.writeStructBegin(TUserDefinedTypeEntry.STRUCT_DESC);
            if (struct.typeClassName != null) {
                oprot.writeFieldBegin(TUserDefinedTypeEntry.TYPE_CLASS_NAME_FIELD_DESC);
                oprot.writeString(struct.typeClassName);
                oprot.writeFieldEnd();
            }
            oprot.writeFieldStop();
            oprot.writeStructEnd();
        }
    }
    
    private static class TUserDefinedTypeEntryTupleSchemeFactory implements SchemeFactory
    {
        @Override
        public TUserDefinedTypeEntryTupleScheme getScheme() {
            return new TUserDefinedTypeEntryTupleScheme();
        }
    }
    
    private static class TUserDefinedTypeEntryTupleScheme extends TupleScheme<TUserDefinedTypeEntry>
    {
        @Override
        public void write(final TProtocol prot, final TUserDefinedTypeEntry struct) throws TException {
            final TTupleProtocol oprot = (TTupleProtocol)prot;
            oprot.writeString(struct.typeClassName);
        }
        
        @Override
        public void read(final TProtocol prot, final TUserDefinedTypeEntry struct) throws TException {
            final TTupleProtocol iprot = (TTupleProtocol)prot;
            struct.typeClassName = iprot.readString();
            struct.setTypeClassNameIsSet(true);
        }
    }
}
