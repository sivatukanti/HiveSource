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
import org.apache.thrift.EncodingUtils;
import org.apache.thrift.meta_data.FieldMetaData;
import org.apache.thrift.scheme.SchemeFactory;
import org.apache.thrift.scheme.IScheme;
import java.util.Map;
import org.apache.thrift.protocol.TField;
import org.apache.thrift.protocol.TStruct;
import java.io.Serializable;
import org.apache.thrift.TBase;

public class TArrayTypeEntry implements TBase<TArrayTypeEntry, _Fields>, Serializable, Cloneable
{
    private static final TStruct STRUCT_DESC;
    private static final TField OBJECT_TYPE_PTR_FIELD_DESC;
    private static final Map<Class<? extends IScheme>, SchemeFactory> schemes;
    private int objectTypePtr;
    private static final int __OBJECTTYPEPTR_ISSET_ID = 0;
    private byte __isset_bitfield;
    public static final Map<_Fields, FieldMetaData> metaDataMap;
    
    public TArrayTypeEntry() {
        this.__isset_bitfield = 0;
    }
    
    public TArrayTypeEntry(final int objectTypePtr) {
        this();
        this.objectTypePtr = objectTypePtr;
        this.setObjectTypePtrIsSet(true);
    }
    
    public TArrayTypeEntry(final TArrayTypeEntry other) {
        this.__isset_bitfield = 0;
        this.__isset_bitfield = other.__isset_bitfield;
        this.objectTypePtr = other.objectTypePtr;
    }
    
    @Override
    public TArrayTypeEntry deepCopy() {
        return new TArrayTypeEntry(this);
    }
    
    @Override
    public void clear() {
        this.setObjectTypePtrIsSet(false);
        this.objectTypePtr = 0;
    }
    
    public int getObjectTypePtr() {
        return this.objectTypePtr;
    }
    
    public void setObjectTypePtr(final int objectTypePtr) {
        this.objectTypePtr = objectTypePtr;
        this.setObjectTypePtrIsSet(true);
    }
    
    public void unsetObjectTypePtr() {
        this.__isset_bitfield = EncodingUtils.clearBit(this.__isset_bitfield, 0);
    }
    
    public boolean isSetObjectTypePtr() {
        return EncodingUtils.testBit(this.__isset_bitfield, 0);
    }
    
    public void setObjectTypePtrIsSet(final boolean value) {
        this.__isset_bitfield = EncodingUtils.setBit(this.__isset_bitfield, 0, value);
    }
    
    @Override
    public void setFieldValue(final _Fields field, final Object value) {
        switch (field) {
            case OBJECT_TYPE_PTR: {
                if (value == null) {
                    this.unsetObjectTypePtr();
                    break;
                }
                this.setObjectTypePtr((int)value);
                break;
            }
        }
    }
    
    @Override
    public Object getFieldValue(final _Fields field) {
        switch (field) {
            case OBJECT_TYPE_PTR: {
                return this.getObjectTypePtr();
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
            case OBJECT_TYPE_PTR: {
                return this.isSetObjectTypePtr();
            }
            default: {
                throw new IllegalStateException();
            }
        }
    }
    
    @Override
    public boolean equals(final Object that) {
        return that != null && that instanceof TArrayTypeEntry && this.equals((TArrayTypeEntry)that);
    }
    
    public boolean equals(final TArrayTypeEntry that) {
        if (that == null) {
            return false;
        }
        final boolean this_present_objectTypePtr = true;
        final boolean that_present_objectTypePtr = true;
        if (this_present_objectTypePtr || that_present_objectTypePtr) {
            if (!this_present_objectTypePtr || !that_present_objectTypePtr) {
                return false;
            }
            if (this.objectTypePtr != that.objectTypePtr) {
                return false;
            }
        }
        return true;
    }
    
    @Override
    public int hashCode() {
        final HashCodeBuilder builder = new HashCodeBuilder();
        final boolean present_objectTypePtr = true;
        builder.append(present_objectTypePtr);
        if (present_objectTypePtr) {
            builder.append(this.objectTypePtr);
        }
        return builder.toHashCode();
    }
    
    @Override
    public int compareTo(final TArrayTypeEntry other) {
        if (!this.getClass().equals(other.getClass())) {
            return this.getClass().getName().compareTo(other.getClass().getName());
        }
        int lastComparison = 0;
        final TArrayTypeEntry typedOther = other;
        lastComparison = Boolean.valueOf(this.isSetObjectTypePtr()).compareTo(Boolean.valueOf(typedOther.isSetObjectTypePtr()));
        if (lastComparison != 0) {
            return lastComparison;
        }
        if (this.isSetObjectTypePtr()) {
            lastComparison = TBaseHelper.compareTo(this.objectTypePtr, typedOther.objectTypePtr);
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
        TArrayTypeEntry.schemes.get(iprot.getScheme()).getScheme().read(iprot, this);
    }
    
    @Override
    public void write(final TProtocol oprot) throws TException {
        TArrayTypeEntry.schemes.get(oprot.getScheme()).getScheme().write(oprot, this);
    }
    
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("TArrayTypeEntry(");
        boolean first = true;
        sb.append("objectTypePtr:");
        sb.append(this.objectTypePtr);
        first = false;
        sb.append(")");
        return sb.toString();
    }
    
    public void validate() throws TException {
        if (!this.isSetObjectTypePtr()) {
            throw new TProtocolException("Required field 'objectTypePtr' is unset! Struct:" + this.toString());
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
        STRUCT_DESC = new TStruct("TArrayTypeEntry");
        OBJECT_TYPE_PTR_FIELD_DESC = new TField("objectTypePtr", (byte)8, (short)1);
        (schemes = new HashMap<Class<? extends IScheme>, SchemeFactory>()).put(StandardScheme.class, new TArrayTypeEntryStandardSchemeFactory());
        TArrayTypeEntry.schemes.put(TupleScheme.class, new TArrayTypeEntryTupleSchemeFactory());
        final Map<_Fields, FieldMetaData> tmpMap = new EnumMap<_Fields, FieldMetaData>(_Fields.class);
        tmpMap.put(_Fields.OBJECT_TYPE_PTR, new FieldMetaData("objectTypePtr", (byte)1, new FieldValueMetaData((byte)8, "TTypeEntryPtr")));
        FieldMetaData.addStructMetaDataMap(TArrayTypeEntry.class, metaDataMap = Collections.unmodifiableMap((Map<? extends _Fields, ? extends FieldMetaData>)tmpMap));
    }
    
    public enum _Fields implements TFieldIdEnum
    {
        OBJECT_TYPE_PTR((short)1, "objectTypePtr");
        
        private static final Map<String, _Fields> byName;
        private final short _thriftId;
        private final String _fieldName;
        
        public static _Fields findByThriftId(final int fieldId) {
            switch (fieldId) {
                case 1: {
                    return _Fields.OBJECT_TYPE_PTR;
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
    
    private static class TArrayTypeEntryStandardSchemeFactory implements SchemeFactory
    {
        @Override
        public TArrayTypeEntryStandardScheme getScheme() {
            return new TArrayTypeEntryStandardScheme();
        }
    }
    
    private static class TArrayTypeEntryStandardScheme extends StandardScheme<TArrayTypeEntry>
    {
        @Override
        public void read(final TProtocol iprot, final TArrayTypeEntry struct) throws TException {
            iprot.readStructBegin();
            while (true) {
                final TField schemeField = iprot.readFieldBegin();
                if (schemeField.type == 0) {
                    break;
                }
                switch (schemeField.id) {
                    case 1: {
                        if (schemeField.type == 8) {
                            struct.objectTypePtr = iprot.readI32();
                            struct.setObjectTypePtrIsSet(true);
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
        public void write(final TProtocol oprot, final TArrayTypeEntry struct) throws TException {
            struct.validate();
            oprot.writeStructBegin(TArrayTypeEntry.STRUCT_DESC);
            oprot.writeFieldBegin(TArrayTypeEntry.OBJECT_TYPE_PTR_FIELD_DESC);
            oprot.writeI32(struct.objectTypePtr);
            oprot.writeFieldEnd();
            oprot.writeFieldStop();
            oprot.writeStructEnd();
        }
    }
    
    private static class TArrayTypeEntryTupleSchemeFactory implements SchemeFactory
    {
        @Override
        public TArrayTypeEntryTupleScheme getScheme() {
            return new TArrayTypeEntryTupleScheme();
        }
    }
    
    private static class TArrayTypeEntryTupleScheme extends TupleScheme<TArrayTypeEntry>
    {
        @Override
        public void write(final TProtocol prot, final TArrayTypeEntry struct) throws TException {
            final TTupleProtocol oprot = (TTupleProtocol)prot;
            oprot.writeI32(struct.objectTypePtr);
        }
        
        @Override
        public void read(final TProtocol prot, final TArrayTypeEntry struct) throws TException {
            final TTupleProtocol iprot = (TTupleProtocol)prot;
            struct.objectTypePtr = iprot.readI32();
            struct.setObjectTypePtrIsSet(true);
        }
    }
}
