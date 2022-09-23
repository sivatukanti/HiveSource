// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.hive.serde.test;

import java.util.BitSet;
import org.apache.thrift.protocol.TTupleProtocol;
import org.apache.thrift.protocol.TList;
import org.apache.thrift.protocol.TProtocolUtil;
import java.util.EnumSet;
import java.util.Collections;
import org.apache.thrift.meta_data.ListMetaData;
import org.apache.thrift.meta_data.StructMetaData;
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

public class ThriftTestObj implements TBase<ThriftTestObj, _Fields>, Serializable, Cloneable
{
    private static final TStruct STRUCT_DESC;
    private static final TField FIELD1_FIELD_DESC;
    private static final TField FIELD2_FIELD_DESC;
    private static final TField FIELD3_FIELD_DESC;
    private static final Map<Class<? extends IScheme>, SchemeFactory> schemes;
    private int field1;
    private String field2;
    private List<InnerStruct> field3;
    private static final int __FIELD1_ISSET_ID = 0;
    private byte __isset_bitfield;
    public static final Map<_Fields, FieldMetaData> metaDataMap;
    
    public ThriftTestObj() {
        this.__isset_bitfield = 0;
    }
    
    public ThriftTestObj(final int field1, final String field2, final List<InnerStruct> field3) {
        this();
        this.field1 = field1;
        this.setField1IsSet(true);
        this.field2 = field2;
        this.field3 = field3;
    }
    
    public ThriftTestObj(final ThriftTestObj other) {
        this.__isset_bitfield = 0;
        this.__isset_bitfield = other.__isset_bitfield;
        this.field1 = other.field1;
        if (other.isSetField2()) {
            this.field2 = other.field2;
        }
        if (other.isSetField3()) {
            final List<InnerStruct> __this__field3 = new ArrayList<InnerStruct>();
            for (final InnerStruct other_element : other.field3) {
                __this__field3.add(new InnerStruct(other_element));
            }
            this.field3 = __this__field3;
        }
    }
    
    @Override
    public ThriftTestObj deepCopy() {
        return new ThriftTestObj(this);
    }
    
    @Override
    public void clear() {
        this.setField1IsSet(false);
        this.field1 = 0;
        this.field2 = null;
        this.field3 = null;
    }
    
    public int getField1() {
        return this.field1;
    }
    
    public void setField1(final int field1) {
        this.field1 = field1;
        this.setField1IsSet(true);
    }
    
    public void unsetField1() {
        this.__isset_bitfield = EncodingUtils.clearBit(this.__isset_bitfield, 0);
    }
    
    public boolean isSetField1() {
        return EncodingUtils.testBit(this.__isset_bitfield, 0);
    }
    
    public void setField1IsSet(final boolean value) {
        this.__isset_bitfield = EncodingUtils.setBit(this.__isset_bitfield, 0, value);
    }
    
    public String getField2() {
        return this.field2;
    }
    
    public void setField2(final String field2) {
        this.field2 = field2;
    }
    
    public void unsetField2() {
        this.field2 = null;
    }
    
    public boolean isSetField2() {
        return this.field2 != null;
    }
    
    public void setField2IsSet(final boolean value) {
        if (!value) {
            this.field2 = null;
        }
    }
    
    public int getField3Size() {
        return (this.field3 == null) ? 0 : this.field3.size();
    }
    
    public Iterator<InnerStruct> getField3Iterator() {
        return (this.field3 == null) ? null : this.field3.iterator();
    }
    
    public void addToField3(final InnerStruct elem) {
        if (this.field3 == null) {
            this.field3 = new ArrayList<InnerStruct>();
        }
        this.field3.add(elem);
    }
    
    public List<InnerStruct> getField3() {
        return this.field3;
    }
    
    public void setField3(final List<InnerStruct> field3) {
        this.field3 = field3;
    }
    
    public void unsetField3() {
        this.field3 = null;
    }
    
    public boolean isSetField3() {
        return this.field3 != null;
    }
    
    public void setField3IsSet(final boolean value) {
        if (!value) {
            this.field3 = null;
        }
    }
    
    @Override
    public void setFieldValue(final _Fields field, final Object value) {
        switch (field) {
            case FIELD1: {
                if (value == null) {
                    this.unsetField1();
                    break;
                }
                this.setField1((int)value);
                break;
            }
            case FIELD2: {
                if (value == null) {
                    this.unsetField2();
                    break;
                }
                this.setField2((String)value);
                break;
            }
            case FIELD3: {
                if (value == null) {
                    this.unsetField3();
                    break;
                }
                this.setField3((List<InnerStruct>)value);
                break;
            }
        }
    }
    
    @Override
    public Object getFieldValue(final _Fields field) {
        switch (field) {
            case FIELD1: {
                return this.getField1();
            }
            case FIELD2: {
                return this.getField2();
            }
            case FIELD3: {
                return this.getField3();
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
            case FIELD1: {
                return this.isSetField1();
            }
            case FIELD2: {
                return this.isSetField2();
            }
            case FIELD3: {
                return this.isSetField3();
            }
            default: {
                throw new IllegalStateException();
            }
        }
    }
    
    @Override
    public boolean equals(final Object that) {
        return that != null && that instanceof ThriftTestObj && this.equals((ThriftTestObj)that);
    }
    
    public boolean equals(final ThriftTestObj that) {
        if (that == null) {
            return false;
        }
        final boolean this_present_field1 = true;
        final boolean that_present_field1 = true;
        if (this_present_field1 || that_present_field1) {
            if (!this_present_field1 || !that_present_field1) {
                return false;
            }
            if (this.field1 != that.field1) {
                return false;
            }
        }
        final boolean this_present_field2 = this.isSetField2();
        final boolean that_present_field2 = that.isSetField2();
        if (this_present_field2 || that_present_field2) {
            if (!this_present_field2 || !that_present_field2) {
                return false;
            }
            if (!this.field2.equals(that.field2)) {
                return false;
            }
        }
        final boolean this_present_field3 = this.isSetField3();
        final boolean that_present_field3 = that.isSetField3();
        if (this_present_field3 || that_present_field3) {
            if (!this_present_field3 || !that_present_field3) {
                return false;
            }
            if (!this.field3.equals(that.field3)) {
                return false;
            }
        }
        return true;
    }
    
    @Override
    public int hashCode() {
        final HashCodeBuilder builder = new HashCodeBuilder();
        final boolean present_field1 = true;
        builder.append(present_field1);
        if (present_field1) {
            builder.append(this.field1);
        }
        final boolean present_field2 = this.isSetField2();
        builder.append(present_field2);
        if (present_field2) {
            builder.append(this.field2);
        }
        final boolean present_field3 = this.isSetField3();
        builder.append(present_field3);
        if (present_field3) {
            builder.append(this.field3);
        }
        return builder.toHashCode();
    }
    
    @Override
    public int compareTo(final ThriftTestObj other) {
        if (!this.getClass().equals(other.getClass())) {
            return this.getClass().getName().compareTo(other.getClass().getName());
        }
        int lastComparison = 0;
        final ThriftTestObj typedOther = other;
        lastComparison = Boolean.valueOf(this.isSetField1()).compareTo(Boolean.valueOf(typedOther.isSetField1()));
        if (lastComparison != 0) {
            return lastComparison;
        }
        if (this.isSetField1()) {
            lastComparison = TBaseHelper.compareTo(this.field1, typedOther.field1);
            if (lastComparison != 0) {
                return lastComparison;
            }
        }
        lastComparison = Boolean.valueOf(this.isSetField2()).compareTo(Boolean.valueOf(typedOther.isSetField2()));
        if (lastComparison != 0) {
            return lastComparison;
        }
        if (this.isSetField2()) {
            lastComparison = TBaseHelper.compareTo(this.field2, typedOther.field2);
            if (lastComparison != 0) {
                return lastComparison;
            }
        }
        lastComparison = Boolean.valueOf(this.isSetField3()).compareTo(Boolean.valueOf(typedOther.isSetField3()));
        if (lastComparison != 0) {
            return lastComparison;
        }
        if (this.isSetField3()) {
            lastComparison = TBaseHelper.compareTo(this.field3, typedOther.field3);
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
        ThriftTestObj.schemes.get(iprot.getScheme()).getScheme().read(iprot, this);
    }
    
    @Override
    public void write(final TProtocol oprot) throws TException {
        ThriftTestObj.schemes.get(oprot.getScheme()).getScheme().write(oprot, this);
    }
    
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("ThriftTestObj(");
        boolean first = true;
        sb.append("field1:");
        sb.append(this.field1);
        first = false;
        if (!first) {
            sb.append(", ");
        }
        sb.append("field2:");
        if (this.field2 == null) {
            sb.append("null");
        }
        else {
            sb.append(this.field2);
        }
        first = false;
        if (!first) {
            sb.append(", ");
        }
        sb.append("field3:");
        if (this.field3 == null) {
            sb.append("null");
        }
        else {
            sb.append(this.field3);
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
            this.__isset_bitfield = 0;
            this.read(new TCompactProtocol(new TIOStreamTransport(in)));
        }
        catch (TException te) {
            throw new IOException(te);
        }
    }
    
    static {
        STRUCT_DESC = new TStruct("ThriftTestObj");
        FIELD1_FIELD_DESC = new TField("field1", (byte)8, (short)1);
        FIELD2_FIELD_DESC = new TField("field2", (byte)11, (short)2);
        FIELD3_FIELD_DESC = new TField("field3", (byte)15, (short)3);
        (schemes = new HashMap<Class<? extends IScheme>, SchemeFactory>()).put(StandardScheme.class, new ThriftTestObjStandardSchemeFactory());
        ThriftTestObj.schemes.put(TupleScheme.class, new ThriftTestObjTupleSchemeFactory());
        final Map<_Fields, FieldMetaData> tmpMap = new EnumMap<_Fields, FieldMetaData>(_Fields.class);
        tmpMap.put(_Fields.FIELD1, new FieldMetaData("field1", (byte)3, new FieldValueMetaData((byte)8)));
        tmpMap.put(_Fields.FIELD2, new FieldMetaData("field2", (byte)3, new FieldValueMetaData((byte)11)));
        tmpMap.put(_Fields.FIELD3, new FieldMetaData("field3", (byte)3, new ListMetaData((byte)15, new StructMetaData((byte)12, InnerStruct.class))));
        FieldMetaData.addStructMetaDataMap(ThriftTestObj.class, metaDataMap = Collections.unmodifiableMap((Map<? extends _Fields, ? extends FieldMetaData>)tmpMap));
    }
    
    public enum _Fields implements TFieldIdEnum
    {
        FIELD1((short)1, "field1"), 
        FIELD2((short)2, "field2"), 
        FIELD3((short)3, "field3");
        
        private static final Map<String, _Fields> byName;
        private final short _thriftId;
        private final String _fieldName;
        
        public static _Fields findByThriftId(final int fieldId) {
            switch (fieldId) {
                case 1: {
                    return _Fields.FIELD1;
                }
                case 2: {
                    return _Fields.FIELD2;
                }
                case 3: {
                    return _Fields.FIELD3;
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
    
    private static class ThriftTestObjStandardSchemeFactory implements SchemeFactory
    {
        @Override
        public ThriftTestObjStandardScheme getScheme() {
            return new ThriftTestObjStandardScheme();
        }
    }
    
    private static class ThriftTestObjStandardScheme extends StandardScheme<ThriftTestObj>
    {
        @Override
        public void read(final TProtocol iprot, final ThriftTestObj struct) throws TException {
            iprot.readStructBegin();
            while (true) {
                final TField schemeField = iprot.readFieldBegin();
                if (schemeField.type == 0) {
                    break;
                }
                switch (schemeField.id) {
                    case 1: {
                        if (schemeField.type == 8) {
                            struct.field1 = iprot.readI32();
                            struct.setField1IsSet(true);
                            break;
                        }
                        TProtocolUtil.skip(iprot, schemeField.type);
                        break;
                    }
                    case 2: {
                        if (schemeField.type == 11) {
                            struct.field2 = iprot.readString();
                            struct.setField2IsSet(true);
                            break;
                        }
                        TProtocolUtil.skip(iprot, schemeField.type);
                        break;
                    }
                    case 3: {
                        if (schemeField.type == 15) {
                            final TList _list0 = iprot.readListBegin();
                            struct.field3 = (List<InnerStruct>)new ArrayList(_list0.size);
                            for (int _i1 = 0; _i1 < _list0.size; ++_i1) {
                                final InnerStruct _elem2 = new InnerStruct();
                                _elem2.read(iprot);
                                struct.field3.add(_elem2);
                            }
                            iprot.readListEnd();
                            struct.setField3IsSet(true);
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
        public void write(final TProtocol oprot, final ThriftTestObj struct) throws TException {
            struct.validate();
            oprot.writeStructBegin(ThriftTestObj.STRUCT_DESC);
            oprot.writeFieldBegin(ThriftTestObj.FIELD1_FIELD_DESC);
            oprot.writeI32(struct.field1);
            oprot.writeFieldEnd();
            if (struct.field2 != null) {
                oprot.writeFieldBegin(ThriftTestObj.FIELD2_FIELD_DESC);
                oprot.writeString(struct.field2);
                oprot.writeFieldEnd();
            }
            if (struct.field3 != null) {
                oprot.writeFieldBegin(ThriftTestObj.FIELD3_FIELD_DESC);
                oprot.writeListBegin(new TList((byte)12, struct.field3.size()));
                for (final InnerStruct _iter3 : struct.field3) {
                    _iter3.write(oprot);
                }
                oprot.writeListEnd();
                oprot.writeFieldEnd();
            }
            oprot.writeFieldStop();
            oprot.writeStructEnd();
        }
    }
    
    private static class ThriftTestObjTupleSchemeFactory implements SchemeFactory
    {
        @Override
        public ThriftTestObjTupleScheme getScheme() {
            return new ThriftTestObjTupleScheme();
        }
    }
    
    private static class ThriftTestObjTupleScheme extends TupleScheme<ThriftTestObj>
    {
        @Override
        public void write(final TProtocol prot, final ThriftTestObj struct) throws TException {
            final TTupleProtocol oprot = (TTupleProtocol)prot;
            final BitSet optionals = new BitSet();
            if (struct.isSetField1()) {
                optionals.set(0);
            }
            if (struct.isSetField2()) {
                optionals.set(1);
            }
            if (struct.isSetField3()) {
                optionals.set(2);
            }
            oprot.writeBitSet(optionals, 3);
            if (struct.isSetField1()) {
                oprot.writeI32(struct.field1);
            }
            if (struct.isSetField2()) {
                oprot.writeString(struct.field2);
            }
            if (struct.isSetField3()) {
                oprot.writeI32(struct.field3.size());
                for (final InnerStruct _iter4 : struct.field3) {
                    _iter4.write(oprot);
                }
            }
        }
        
        @Override
        public void read(final TProtocol prot, final ThriftTestObj struct) throws TException {
            final TTupleProtocol iprot = (TTupleProtocol)prot;
            final BitSet incoming = iprot.readBitSet(3);
            if (incoming.get(0)) {
                struct.field1 = iprot.readI32();
                struct.setField1IsSet(true);
            }
            if (incoming.get(1)) {
                struct.field2 = iprot.readString();
                struct.setField2IsSet(true);
            }
            if (incoming.get(2)) {
                final TList _list5 = new TList((byte)12, iprot.readI32());
                struct.field3 = (List<InnerStruct>)new ArrayList(_list5.size);
                for (int _i6 = 0; _i6 < _list5.size; ++_i6) {
                    final InnerStruct _elem7 = new InnerStruct();
                    _elem7.read(iprot);
                    struct.field3.add(_elem7);
                }
                struct.setField3IsSet(true);
            }
        }
    }
}
