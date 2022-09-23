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

public class TMapTypeEntry implements TBase<TMapTypeEntry, _Fields>, Serializable, Cloneable
{
    private static final TStruct STRUCT_DESC;
    private static final TField KEY_TYPE_PTR_FIELD_DESC;
    private static final TField VALUE_TYPE_PTR_FIELD_DESC;
    private static final Map<Class<? extends IScheme>, SchemeFactory> schemes;
    private int keyTypePtr;
    private int valueTypePtr;
    private static final int __KEYTYPEPTR_ISSET_ID = 0;
    private static final int __VALUETYPEPTR_ISSET_ID = 1;
    private byte __isset_bitfield;
    public static final Map<_Fields, FieldMetaData> metaDataMap;
    
    public TMapTypeEntry() {
        this.__isset_bitfield = 0;
    }
    
    public TMapTypeEntry(final int keyTypePtr, final int valueTypePtr) {
        this();
        this.keyTypePtr = keyTypePtr;
        this.setKeyTypePtrIsSet(true);
        this.valueTypePtr = valueTypePtr;
        this.setValueTypePtrIsSet(true);
    }
    
    public TMapTypeEntry(final TMapTypeEntry other) {
        this.__isset_bitfield = 0;
        this.__isset_bitfield = other.__isset_bitfield;
        this.keyTypePtr = other.keyTypePtr;
        this.valueTypePtr = other.valueTypePtr;
    }
    
    @Override
    public TMapTypeEntry deepCopy() {
        return new TMapTypeEntry(this);
    }
    
    @Override
    public void clear() {
        this.setKeyTypePtrIsSet(false);
        this.keyTypePtr = 0;
        this.setValueTypePtrIsSet(false);
        this.valueTypePtr = 0;
    }
    
    public int getKeyTypePtr() {
        return this.keyTypePtr;
    }
    
    public void setKeyTypePtr(final int keyTypePtr) {
        this.keyTypePtr = keyTypePtr;
        this.setKeyTypePtrIsSet(true);
    }
    
    public void unsetKeyTypePtr() {
        this.__isset_bitfield = EncodingUtils.clearBit(this.__isset_bitfield, 0);
    }
    
    public boolean isSetKeyTypePtr() {
        return EncodingUtils.testBit(this.__isset_bitfield, 0);
    }
    
    public void setKeyTypePtrIsSet(final boolean value) {
        this.__isset_bitfield = EncodingUtils.setBit(this.__isset_bitfield, 0, value);
    }
    
    public int getValueTypePtr() {
        return this.valueTypePtr;
    }
    
    public void setValueTypePtr(final int valueTypePtr) {
        this.valueTypePtr = valueTypePtr;
        this.setValueTypePtrIsSet(true);
    }
    
    public void unsetValueTypePtr() {
        this.__isset_bitfield = EncodingUtils.clearBit(this.__isset_bitfield, 1);
    }
    
    public boolean isSetValueTypePtr() {
        return EncodingUtils.testBit(this.__isset_bitfield, 1);
    }
    
    public void setValueTypePtrIsSet(final boolean value) {
        this.__isset_bitfield = EncodingUtils.setBit(this.__isset_bitfield, 1, value);
    }
    
    @Override
    public void setFieldValue(final _Fields field, final Object value) {
        switch (field) {
            case KEY_TYPE_PTR: {
                if (value == null) {
                    this.unsetKeyTypePtr();
                    break;
                }
                this.setKeyTypePtr((int)value);
                break;
            }
            case VALUE_TYPE_PTR: {
                if (value == null) {
                    this.unsetValueTypePtr();
                    break;
                }
                this.setValueTypePtr((int)value);
                break;
            }
        }
    }
    
    @Override
    public Object getFieldValue(final _Fields field) {
        switch (field) {
            case KEY_TYPE_PTR: {
                return this.getKeyTypePtr();
            }
            case VALUE_TYPE_PTR: {
                return this.getValueTypePtr();
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
            case KEY_TYPE_PTR: {
                return this.isSetKeyTypePtr();
            }
            case VALUE_TYPE_PTR: {
                return this.isSetValueTypePtr();
            }
            default: {
                throw new IllegalStateException();
            }
        }
    }
    
    @Override
    public boolean equals(final Object that) {
        return that != null && that instanceof TMapTypeEntry && this.equals((TMapTypeEntry)that);
    }
    
    public boolean equals(final TMapTypeEntry that) {
        if (that == null) {
            return false;
        }
        final boolean this_present_keyTypePtr = true;
        final boolean that_present_keyTypePtr = true;
        if (this_present_keyTypePtr || that_present_keyTypePtr) {
            if (!this_present_keyTypePtr || !that_present_keyTypePtr) {
                return false;
            }
            if (this.keyTypePtr != that.keyTypePtr) {
                return false;
            }
        }
        final boolean this_present_valueTypePtr = true;
        final boolean that_present_valueTypePtr = true;
        if (this_present_valueTypePtr || that_present_valueTypePtr) {
            if (!this_present_valueTypePtr || !that_present_valueTypePtr) {
                return false;
            }
            if (this.valueTypePtr != that.valueTypePtr) {
                return false;
            }
        }
        return true;
    }
    
    @Override
    public int hashCode() {
        final HashCodeBuilder builder = new HashCodeBuilder();
        final boolean present_keyTypePtr = true;
        builder.append(present_keyTypePtr);
        if (present_keyTypePtr) {
            builder.append(this.keyTypePtr);
        }
        final boolean present_valueTypePtr = true;
        builder.append(present_valueTypePtr);
        if (present_valueTypePtr) {
            builder.append(this.valueTypePtr);
        }
        return builder.toHashCode();
    }
    
    @Override
    public int compareTo(final TMapTypeEntry other) {
        if (!this.getClass().equals(other.getClass())) {
            return this.getClass().getName().compareTo(other.getClass().getName());
        }
        int lastComparison = 0;
        final TMapTypeEntry typedOther = other;
        lastComparison = Boolean.valueOf(this.isSetKeyTypePtr()).compareTo(Boolean.valueOf(typedOther.isSetKeyTypePtr()));
        if (lastComparison != 0) {
            return lastComparison;
        }
        if (this.isSetKeyTypePtr()) {
            lastComparison = TBaseHelper.compareTo(this.keyTypePtr, typedOther.keyTypePtr);
            if (lastComparison != 0) {
                return lastComparison;
            }
        }
        lastComparison = Boolean.valueOf(this.isSetValueTypePtr()).compareTo(Boolean.valueOf(typedOther.isSetValueTypePtr()));
        if (lastComparison != 0) {
            return lastComparison;
        }
        if (this.isSetValueTypePtr()) {
            lastComparison = TBaseHelper.compareTo(this.valueTypePtr, typedOther.valueTypePtr);
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
        TMapTypeEntry.schemes.get(iprot.getScheme()).getScheme().read(iprot, this);
    }
    
    @Override
    public void write(final TProtocol oprot) throws TException {
        TMapTypeEntry.schemes.get(oprot.getScheme()).getScheme().write(oprot, this);
    }
    
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("TMapTypeEntry(");
        boolean first = true;
        sb.append("keyTypePtr:");
        sb.append(this.keyTypePtr);
        first = false;
        if (!first) {
            sb.append(", ");
        }
        sb.append("valueTypePtr:");
        sb.append(this.valueTypePtr);
        first = false;
        sb.append(")");
        return sb.toString();
    }
    
    public void validate() throws TException {
        if (!this.isSetKeyTypePtr()) {
            throw new TProtocolException("Required field 'keyTypePtr' is unset! Struct:" + this.toString());
        }
        if (!this.isSetValueTypePtr()) {
            throw new TProtocolException("Required field 'valueTypePtr' is unset! Struct:" + this.toString());
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
        STRUCT_DESC = new TStruct("TMapTypeEntry");
        KEY_TYPE_PTR_FIELD_DESC = new TField("keyTypePtr", (byte)8, (short)1);
        VALUE_TYPE_PTR_FIELD_DESC = new TField("valueTypePtr", (byte)8, (short)2);
        (schemes = new HashMap<Class<? extends IScheme>, SchemeFactory>()).put(StandardScheme.class, new TMapTypeEntryStandardSchemeFactory());
        TMapTypeEntry.schemes.put(TupleScheme.class, new TMapTypeEntryTupleSchemeFactory());
        final Map<_Fields, FieldMetaData> tmpMap = new EnumMap<_Fields, FieldMetaData>(_Fields.class);
        tmpMap.put(_Fields.KEY_TYPE_PTR, new FieldMetaData("keyTypePtr", (byte)1, new FieldValueMetaData((byte)8, "TTypeEntryPtr")));
        tmpMap.put(_Fields.VALUE_TYPE_PTR, new FieldMetaData("valueTypePtr", (byte)1, new FieldValueMetaData((byte)8, "TTypeEntryPtr")));
        FieldMetaData.addStructMetaDataMap(TMapTypeEntry.class, metaDataMap = Collections.unmodifiableMap((Map<? extends _Fields, ? extends FieldMetaData>)tmpMap));
    }
    
    public enum _Fields implements TFieldIdEnum
    {
        KEY_TYPE_PTR((short)1, "keyTypePtr"), 
        VALUE_TYPE_PTR((short)2, "valueTypePtr");
        
        private static final Map<String, _Fields> byName;
        private final short _thriftId;
        private final String _fieldName;
        
        public static _Fields findByThriftId(final int fieldId) {
            switch (fieldId) {
                case 1: {
                    return _Fields.KEY_TYPE_PTR;
                }
                case 2: {
                    return _Fields.VALUE_TYPE_PTR;
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
    
    private static class TMapTypeEntryStandardSchemeFactory implements SchemeFactory
    {
        @Override
        public TMapTypeEntryStandardScheme getScheme() {
            return new TMapTypeEntryStandardScheme();
        }
    }
    
    private static class TMapTypeEntryStandardScheme extends StandardScheme<TMapTypeEntry>
    {
        @Override
        public void read(final TProtocol iprot, final TMapTypeEntry struct) throws TException {
            iprot.readStructBegin();
            while (true) {
                final TField schemeField = iprot.readFieldBegin();
                if (schemeField.type == 0) {
                    break;
                }
                switch (schemeField.id) {
                    case 1: {
                        if (schemeField.type == 8) {
                            struct.keyTypePtr = iprot.readI32();
                            struct.setKeyTypePtrIsSet(true);
                            break;
                        }
                        TProtocolUtil.skip(iprot, schemeField.type);
                        break;
                    }
                    case 2: {
                        if (schemeField.type == 8) {
                            struct.valueTypePtr = iprot.readI32();
                            struct.setValueTypePtrIsSet(true);
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
        public void write(final TProtocol oprot, final TMapTypeEntry struct) throws TException {
            struct.validate();
            oprot.writeStructBegin(TMapTypeEntry.STRUCT_DESC);
            oprot.writeFieldBegin(TMapTypeEntry.KEY_TYPE_PTR_FIELD_DESC);
            oprot.writeI32(struct.keyTypePtr);
            oprot.writeFieldEnd();
            oprot.writeFieldBegin(TMapTypeEntry.VALUE_TYPE_PTR_FIELD_DESC);
            oprot.writeI32(struct.valueTypePtr);
            oprot.writeFieldEnd();
            oprot.writeFieldStop();
            oprot.writeStructEnd();
        }
    }
    
    private static class TMapTypeEntryTupleSchemeFactory implements SchemeFactory
    {
        @Override
        public TMapTypeEntryTupleScheme getScheme() {
            return new TMapTypeEntryTupleScheme();
        }
    }
    
    private static class TMapTypeEntryTupleScheme extends TupleScheme<TMapTypeEntry>
    {
        @Override
        public void write(final TProtocol prot, final TMapTypeEntry struct) throws TException {
            final TTupleProtocol oprot = (TTupleProtocol)prot;
            oprot.writeI32(struct.keyTypePtr);
            oprot.writeI32(struct.valueTypePtr);
        }
        
        @Override
        public void read(final TProtocol prot, final TMapTypeEntry struct) throws TException {
            final TTupleProtocol iprot = (TTupleProtocol)prot;
            struct.keyTypePtr = iprot.readI32();
            struct.setKeyTypePtrIsSet(true);
            struct.valueTypePtr = iprot.readI32();
            struct.setValueTypePtrIsSet(true);
        }
    }
}
