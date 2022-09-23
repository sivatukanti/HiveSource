// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hive.service.cli.thrift;

import org.apache.thrift.protocol.TTupleProtocol;
import org.apache.thrift.protocol.TMap;
import org.apache.thrift.protocol.TProtocolUtil;
import java.util.EnumSet;
import java.util.Collections;
import org.apache.thrift.meta_data.MapMetaData;
import org.apache.thrift.meta_data.FieldValueMetaData;
import java.util.EnumMap;
import org.apache.thrift.scheme.TupleScheme;
import org.apache.thrift.scheme.StandardScheme;
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
import java.util.HashMap;
import org.apache.thrift.meta_data.FieldMetaData;
import org.apache.thrift.scheme.SchemeFactory;
import org.apache.thrift.scheme.IScheme;
import java.util.Map;
import org.apache.thrift.protocol.TField;
import org.apache.thrift.protocol.TStruct;
import java.io.Serializable;
import org.apache.thrift.TBase;

public class TUnionTypeEntry implements TBase<TUnionTypeEntry, _Fields>, Serializable, Cloneable
{
    private static final TStruct STRUCT_DESC;
    private static final TField NAME_TO_TYPE_PTR_FIELD_DESC;
    private static final Map<Class<? extends IScheme>, SchemeFactory> schemes;
    private Map<String, Integer> nameToTypePtr;
    public static final Map<_Fields, FieldMetaData> metaDataMap;
    
    public TUnionTypeEntry() {
    }
    
    public TUnionTypeEntry(final Map<String, Integer> nameToTypePtr) {
        this();
        this.nameToTypePtr = nameToTypePtr;
    }
    
    public TUnionTypeEntry(final TUnionTypeEntry other) {
        if (other.isSetNameToTypePtr()) {
            final Map<String, Integer> __this__nameToTypePtr = new HashMap<String, Integer>();
            for (final Map.Entry<String, Integer> other_element : other.nameToTypePtr.entrySet()) {
                final String other_element_key = other_element.getKey();
                final Integer other_element_value = other_element.getValue();
                final String __this__nameToTypePtr_copy_key = other_element_key;
                final Integer __this__nameToTypePtr_copy_value = other_element_value;
                __this__nameToTypePtr.put(__this__nameToTypePtr_copy_key, __this__nameToTypePtr_copy_value);
            }
            this.nameToTypePtr = __this__nameToTypePtr;
        }
    }
    
    @Override
    public TUnionTypeEntry deepCopy() {
        return new TUnionTypeEntry(this);
    }
    
    @Override
    public void clear() {
        this.nameToTypePtr = null;
    }
    
    public int getNameToTypePtrSize() {
        return (this.nameToTypePtr == null) ? 0 : this.nameToTypePtr.size();
    }
    
    public void putToNameToTypePtr(final String key, final int val) {
        if (this.nameToTypePtr == null) {
            this.nameToTypePtr = new HashMap<String, Integer>();
        }
        this.nameToTypePtr.put(key, val);
    }
    
    public Map<String, Integer> getNameToTypePtr() {
        return this.nameToTypePtr;
    }
    
    public void setNameToTypePtr(final Map<String, Integer> nameToTypePtr) {
        this.nameToTypePtr = nameToTypePtr;
    }
    
    public void unsetNameToTypePtr() {
        this.nameToTypePtr = null;
    }
    
    public boolean isSetNameToTypePtr() {
        return this.nameToTypePtr != null;
    }
    
    public void setNameToTypePtrIsSet(final boolean value) {
        if (!value) {
            this.nameToTypePtr = null;
        }
    }
    
    @Override
    public void setFieldValue(final _Fields field, final Object value) {
        switch (field) {
            case NAME_TO_TYPE_PTR: {
                if (value == null) {
                    this.unsetNameToTypePtr();
                    break;
                }
                this.setNameToTypePtr((Map<String, Integer>)value);
                break;
            }
        }
    }
    
    @Override
    public Object getFieldValue(final _Fields field) {
        switch (field) {
            case NAME_TO_TYPE_PTR: {
                return this.getNameToTypePtr();
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
            case NAME_TO_TYPE_PTR: {
                return this.isSetNameToTypePtr();
            }
            default: {
                throw new IllegalStateException();
            }
        }
    }
    
    @Override
    public boolean equals(final Object that) {
        return that != null && that instanceof TUnionTypeEntry && this.equals((TUnionTypeEntry)that);
    }
    
    public boolean equals(final TUnionTypeEntry that) {
        if (that == null) {
            return false;
        }
        final boolean this_present_nameToTypePtr = this.isSetNameToTypePtr();
        final boolean that_present_nameToTypePtr = that.isSetNameToTypePtr();
        if (this_present_nameToTypePtr || that_present_nameToTypePtr) {
            if (!this_present_nameToTypePtr || !that_present_nameToTypePtr) {
                return false;
            }
            if (!this.nameToTypePtr.equals(that.nameToTypePtr)) {
                return false;
            }
        }
        return true;
    }
    
    @Override
    public int hashCode() {
        final HashCodeBuilder builder = new HashCodeBuilder();
        final boolean present_nameToTypePtr = this.isSetNameToTypePtr();
        builder.append(present_nameToTypePtr);
        if (present_nameToTypePtr) {
            builder.append(this.nameToTypePtr);
        }
        return builder.toHashCode();
    }
    
    @Override
    public int compareTo(final TUnionTypeEntry other) {
        if (!this.getClass().equals(other.getClass())) {
            return this.getClass().getName().compareTo(other.getClass().getName());
        }
        int lastComparison = 0;
        final TUnionTypeEntry typedOther = other;
        lastComparison = Boolean.valueOf(this.isSetNameToTypePtr()).compareTo(Boolean.valueOf(typedOther.isSetNameToTypePtr()));
        if (lastComparison != 0) {
            return lastComparison;
        }
        if (this.isSetNameToTypePtr()) {
            lastComparison = TBaseHelper.compareTo(this.nameToTypePtr, typedOther.nameToTypePtr);
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
        TUnionTypeEntry.schemes.get(iprot.getScheme()).getScheme().read(iprot, this);
    }
    
    @Override
    public void write(final TProtocol oprot) throws TException {
        TUnionTypeEntry.schemes.get(oprot.getScheme()).getScheme().write(oprot, this);
    }
    
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("TUnionTypeEntry(");
        boolean first = true;
        sb.append("nameToTypePtr:");
        if (this.nameToTypePtr == null) {
            sb.append("null");
        }
        else {
            sb.append(this.nameToTypePtr);
        }
        first = false;
        sb.append(")");
        return sb.toString();
    }
    
    public void validate() throws TException {
        if (!this.isSetNameToTypePtr()) {
            throw new TProtocolException("Required field 'nameToTypePtr' is unset! Struct:" + this.toString());
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
        STRUCT_DESC = new TStruct("TUnionTypeEntry");
        NAME_TO_TYPE_PTR_FIELD_DESC = new TField("nameToTypePtr", (byte)13, (short)1);
        (schemes = new HashMap<Class<? extends IScheme>, SchemeFactory>()).put(StandardScheme.class, new TUnionTypeEntryStandardSchemeFactory());
        TUnionTypeEntry.schemes.put(TupleScheme.class, new TUnionTypeEntryTupleSchemeFactory());
        final Map<_Fields, FieldMetaData> tmpMap = new EnumMap<_Fields, FieldMetaData>(_Fields.class);
        tmpMap.put(_Fields.NAME_TO_TYPE_PTR, new FieldMetaData("nameToTypePtr", (byte)1, new MapMetaData((byte)13, new FieldValueMetaData((byte)11), new FieldValueMetaData((byte)8, "TTypeEntryPtr"))));
        FieldMetaData.addStructMetaDataMap(TUnionTypeEntry.class, metaDataMap = Collections.unmodifiableMap((Map<? extends _Fields, ? extends FieldMetaData>)tmpMap));
    }
    
    public enum _Fields implements TFieldIdEnum
    {
        NAME_TO_TYPE_PTR((short)1, "nameToTypePtr");
        
        private static final Map<String, _Fields> byName;
        private final short _thriftId;
        private final String _fieldName;
        
        public static _Fields findByThriftId(final int fieldId) {
            switch (fieldId) {
                case 1: {
                    return _Fields.NAME_TO_TYPE_PTR;
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
    
    private static class TUnionTypeEntryStandardSchemeFactory implements SchemeFactory
    {
        @Override
        public TUnionTypeEntryStandardScheme getScheme() {
            return new TUnionTypeEntryStandardScheme();
        }
    }
    
    private static class TUnionTypeEntryStandardScheme extends StandardScheme<TUnionTypeEntry>
    {
        @Override
        public void read(final TProtocol iprot, final TUnionTypeEntry struct) throws TException {
            iprot.readStructBegin();
            while (true) {
                final TField schemeField = iprot.readFieldBegin();
                if (schemeField.type == 0) {
                    break;
                }
                switch (schemeField.id) {
                    case 1: {
                        if (schemeField.type == 13) {
                            final TMap _map20 = iprot.readMapBegin();
                            struct.nameToTypePtr = (Map<String, Integer>)new HashMap(2 * _map20.size);
                            for (int _i21 = 0; _i21 < _map20.size; ++_i21) {
                                final String _key22 = iprot.readString();
                                final int _val23 = iprot.readI32();
                                struct.nameToTypePtr.put(_key22, _val23);
                            }
                            iprot.readMapEnd();
                            struct.setNameToTypePtrIsSet(true);
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
        public void write(final TProtocol oprot, final TUnionTypeEntry struct) throws TException {
            struct.validate();
            oprot.writeStructBegin(TUnionTypeEntry.STRUCT_DESC);
            if (struct.nameToTypePtr != null) {
                oprot.writeFieldBegin(TUnionTypeEntry.NAME_TO_TYPE_PTR_FIELD_DESC);
                oprot.writeMapBegin(new TMap((byte)11, (byte)8, struct.nameToTypePtr.size()));
                for (final Map.Entry<String, Integer> _iter24 : struct.nameToTypePtr.entrySet()) {
                    oprot.writeString(_iter24.getKey());
                    oprot.writeI32(_iter24.getValue());
                }
                oprot.writeMapEnd();
                oprot.writeFieldEnd();
            }
            oprot.writeFieldStop();
            oprot.writeStructEnd();
        }
    }
    
    private static class TUnionTypeEntryTupleSchemeFactory implements SchemeFactory
    {
        @Override
        public TUnionTypeEntryTupleScheme getScheme() {
            return new TUnionTypeEntryTupleScheme();
        }
    }
    
    private static class TUnionTypeEntryTupleScheme extends TupleScheme<TUnionTypeEntry>
    {
        @Override
        public void write(final TProtocol prot, final TUnionTypeEntry struct) throws TException {
            final TTupleProtocol oprot = (TTupleProtocol)prot;
            oprot.writeI32(struct.nameToTypePtr.size());
            for (final Map.Entry<String, Integer> _iter25 : struct.nameToTypePtr.entrySet()) {
                oprot.writeString(_iter25.getKey());
                oprot.writeI32(_iter25.getValue());
            }
        }
        
        @Override
        public void read(final TProtocol prot, final TUnionTypeEntry struct) throws TException {
            final TTupleProtocol iprot = (TTupleProtocol)prot;
            final TMap _map26 = new TMap((byte)11, (byte)8, iprot.readI32());
            struct.nameToTypePtr = (Map<String, Integer>)new HashMap(2 * _map26.size);
            for (int _i27 = 0; _i27 < _map26.size; ++_i27) {
                final String _key28 = iprot.readString();
                final int _val29 = iprot.readI32();
                struct.nameToTypePtr.put(_key28, _val29);
            }
            struct.setNameToTypePtrIsSet(true);
        }
    }
}
