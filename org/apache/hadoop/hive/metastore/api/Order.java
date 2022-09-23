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

public class Order implements TBase<Order, _Fields>, Serializable, Cloneable
{
    private static final TStruct STRUCT_DESC;
    private static final TField COL_FIELD_DESC;
    private static final TField ORDER_FIELD_DESC;
    private static final Map<Class<? extends IScheme>, SchemeFactory> schemes;
    private String col;
    private int order;
    private static final int __ORDER_ISSET_ID = 0;
    private byte __isset_bitfield;
    public static final Map<_Fields, FieldMetaData> metaDataMap;
    
    public Order() {
        this.__isset_bitfield = 0;
    }
    
    public Order(final String col, final int order) {
        this();
        this.col = col;
        this.order = order;
        this.setOrderIsSet(true);
    }
    
    public Order(final Order other) {
        this.__isset_bitfield = 0;
        this.__isset_bitfield = other.__isset_bitfield;
        if (other.isSetCol()) {
            this.col = other.col;
        }
        this.order = other.order;
    }
    
    @Override
    public Order deepCopy() {
        return new Order(this);
    }
    
    @Override
    public void clear() {
        this.col = null;
        this.setOrderIsSet(false);
        this.order = 0;
    }
    
    public String getCol() {
        return this.col;
    }
    
    public void setCol(final String col) {
        this.col = col;
    }
    
    public void unsetCol() {
        this.col = null;
    }
    
    public boolean isSetCol() {
        return this.col != null;
    }
    
    public void setColIsSet(final boolean value) {
        if (!value) {
            this.col = null;
        }
    }
    
    public int getOrder() {
        return this.order;
    }
    
    public void setOrder(final int order) {
        this.order = order;
        this.setOrderIsSet(true);
    }
    
    public void unsetOrder() {
        this.__isset_bitfield = EncodingUtils.clearBit(this.__isset_bitfield, 0);
    }
    
    public boolean isSetOrder() {
        return EncodingUtils.testBit(this.__isset_bitfield, 0);
    }
    
    public void setOrderIsSet(final boolean value) {
        this.__isset_bitfield = EncodingUtils.setBit(this.__isset_bitfield, 0, value);
    }
    
    @Override
    public void setFieldValue(final _Fields field, final Object value) {
        switch (field) {
            case COL: {
                if (value == null) {
                    this.unsetCol();
                    break;
                }
                this.setCol((String)value);
                break;
            }
            case ORDER: {
                if (value == null) {
                    this.unsetOrder();
                    break;
                }
                this.setOrder((int)value);
                break;
            }
        }
    }
    
    @Override
    public Object getFieldValue(final _Fields field) {
        switch (field) {
            case COL: {
                return this.getCol();
            }
            case ORDER: {
                return this.getOrder();
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
            case COL: {
                return this.isSetCol();
            }
            case ORDER: {
                return this.isSetOrder();
            }
            default: {
                throw new IllegalStateException();
            }
        }
    }
    
    @Override
    public boolean equals(final Object that) {
        return that != null && that instanceof Order && this.equals((Order)that);
    }
    
    public boolean equals(final Order that) {
        if (that == null) {
            return false;
        }
        final boolean this_present_col = this.isSetCol();
        final boolean that_present_col = that.isSetCol();
        if (this_present_col || that_present_col) {
            if (!this_present_col || !that_present_col) {
                return false;
            }
            if (!this.col.equals(that.col)) {
                return false;
            }
        }
        final boolean this_present_order = true;
        final boolean that_present_order = true;
        if (this_present_order || that_present_order) {
            if (!this_present_order || !that_present_order) {
                return false;
            }
            if (this.order != that.order) {
                return false;
            }
        }
        return true;
    }
    
    @Override
    public int hashCode() {
        final HashCodeBuilder builder = new HashCodeBuilder();
        final boolean present_col = this.isSetCol();
        builder.append(present_col);
        if (present_col) {
            builder.append(this.col);
        }
        final boolean present_order = true;
        builder.append(present_order);
        if (present_order) {
            builder.append(this.order);
        }
        return builder.toHashCode();
    }
    
    @Override
    public int compareTo(final Order other) {
        if (!this.getClass().equals(other.getClass())) {
            return this.getClass().getName().compareTo(other.getClass().getName());
        }
        int lastComparison = 0;
        final Order typedOther = other;
        lastComparison = Boolean.valueOf(this.isSetCol()).compareTo(Boolean.valueOf(typedOther.isSetCol()));
        if (lastComparison != 0) {
            return lastComparison;
        }
        if (this.isSetCol()) {
            lastComparison = TBaseHelper.compareTo(this.col, typedOther.col);
            if (lastComparison != 0) {
                return lastComparison;
            }
        }
        lastComparison = Boolean.valueOf(this.isSetOrder()).compareTo(Boolean.valueOf(typedOther.isSetOrder()));
        if (lastComparison != 0) {
            return lastComparison;
        }
        if (this.isSetOrder()) {
            lastComparison = TBaseHelper.compareTo(this.order, typedOther.order);
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
        Order.schemes.get(iprot.getScheme()).getScheme().read(iprot, this);
    }
    
    @Override
    public void write(final TProtocol oprot) throws TException {
        Order.schemes.get(oprot.getScheme()).getScheme().write(oprot, this);
    }
    
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("Order(");
        boolean first = true;
        sb.append("col:");
        if (this.col == null) {
            sb.append("null");
        }
        else {
            sb.append(this.col);
        }
        first = false;
        if (!first) {
            sb.append(", ");
        }
        sb.append("order:");
        sb.append(this.order);
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
        STRUCT_DESC = new TStruct("Order");
        COL_FIELD_DESC = new TField("col", (byte)11, (short)1);
        ORDER_FIELD_DESC = new TField("order", (byte)8, (short)2);
        (schemes = new HashMap<Class<? extends IScheme>, SchemeFactory>()).put(StandardScheme.class, new OrderStandardSchemeFactory());
        Order.schemes.put(TupleScheme.class, new OrderTupleSchemeFactory());
        final Map<_Fields, FieldMetaData> tmpMap = new EnumMap<_Fields, FieldMetaData>(_Fields.class);
        tmpMap.put(_Fields.COL, new FieldMetaData("col", (byte)3, new FieldValueMetaData((byte)11)));
        tmpMap.put(_Fields.ORDER, new FieldMetaData("order", (byte)3, new FieldValueMetaData((byte)8)));
        FieldMetaData.addStructMetaDataMap(Order.class, metaDataMap = Collections.unmodifiableMap((Map<? extends _Fields, ? extends FieldMetaData>)tmpMap));
    }
    
    public enum _Fields implements TFieldIdEnum
    {
        COL((short)1, "col"), 
        ORDER((short)2, "order");
        
        private static final Map<String, _Fields> byName;
        private final short _thriftId;
        private final String _fieldName;
        
        public static _Fields findByThriftId(final int fieldId) {
            switch (fieldId) {
                case 1: {
                    return _Fields.COL;
                }
                case 2: {
                    return _Fields.ORDER;
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
    
    private static class OrderStandardSchemeFactory implements SchemeFactory
    {
        @Override
        public OrderStandardScheme getScheme() {
            return new OrderStandardScheme();
        }
    }
    
    private static class OrderStandardScheme extends StandardScheme<Order>
    {
        @Override
        public void read(final TProtocol iprot, final Order struct) throws TException {
            iprot.readStructBegin();
            while (true) {
                final TField schemeField = iprot.readFieldBegin();
                if (schemeField.type == 0) {
                    break;
                }
                switch (schemeField.id) {
                    case 1: {
                        if (schemeField.type == 11) {
                            struct.col = iprot.readString();
                            struct.setColIsSet(true);
                            break;
                        }
                        TProtocolUtil.skip(iprot, schemeField.type);
                        break;
                    }
                    case 2: {
                        if (schemeField.type == 8) {
                            struct.order = iprot.readI32();
                            struct.setOrderIsSet(true);
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
        public void write(final TProtocol oprot, final Order struct) throws TException {
            struct.validate();
            oprot.writeStructBegin(Order.STRUCT_DESC);
            if (struct.col != null) {
                oprot.writeFieldBegin(Order.COL_FIELD_DESC);
                oprot.writeString(struct.col);
                oprot.writeFieldEnd();
            }
            oprot.writeFieldBegin(Order.ORDER_FIELD_DESC);
            oprot.writeI32(struct.order);
            oprot.writeFieldEnd();
            oprot.writeFieldStop();
            oprot.writeStructEnd();
        }
    }
    
    private static class OrderTupleSchemeFactory implements SchemeFactory
    {
        @Override
        public OrderTupleScheme getScheme() {
            return new OrderTupleScheme();
        }
    }
    
    private static class OrderTupleScheme extends TupleScheme<Order>
    {
        @Override
        public void write(final TProtocol prot, final Order struct) throws TException {
            final TTupleProtocol oprot = (TTupleProtocol)prot;
            final BitSet optionals = new BitSet();
            if (struct.isSetCol()) {
                optionals.set(0);
            }
            if (struct.isSetOrder()) {
                optionals.set(1);
            }
            oprot.writeBitSet(optionals, 2);
            if (struct.isSetCol()) {
                oprot.writeString(struct.col);
            }
            if (struct.isSetOrder()) {
                oprot.writeI32(struct.order);
            }
        }
        
        @Override
        public void read(final TProtocol prot, final Order struct) throws TException {
            final TTupleProtocol iprot = (TTupleProtocol)prot;
            final BitSet incoming = iprot.readBitSet(2);
            if (incoming.get(0)) {
                struct.col = iprot.readString();
                struct.setColIsSet(true);
            }
            if (incoming.get(1)) {
                struct.order = iprot.readI32();
                struct.setOrderIsSet(true);
            }
        }
    }
}
