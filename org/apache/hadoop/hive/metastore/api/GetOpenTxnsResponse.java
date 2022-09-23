// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.hive.metastore.api;

import org.apache.thrift.protocol.TTupleProtocol;
import org.apache.thrift.protocol.TSet;
import org.apache.thrift.protocol.TProtocolUtil;
import java.util.EnumSet;
import java.util.Collections;
import org.apache.thrift.meta_data.SetMetaData;
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
import java.util.Iterator;
import java.util.HashSet;
import org.apache.thrift.meta_data.FieldMetaData;
import java.util.Set;
import org.apache.thrift.scheme.SchemeFactory;
import org.apache.thrift.scheme.IScheme;
import java.util.Map;
import org.apache.thrift.protocol.TField;
import org.apache.thrift.protocol.TStruct;
import java.io.Serializable;
import org.apache.thrift.TBase;

public class GetOpenTxnsResponse implements TBase<GetOpenTxnsResponse, _Fields>, Serializable, Cloneable
{
    private static final TStruct STRUCT_DESC;
    private static final TField TXN_HIGH_WATER_MARK_FIELD_DESC;
    private static final TField OPEN_TXNS_FIELD_DESC;
    private static final Map<Class<? extends IScheme>, SchemeFactory> schemes;
    private long txn_high_water_mark;
    private Set<Long> open_txns;
    private static final int __TXN_HIGH_WATER_MARK_ISSET_ID = 0;
    private byte __isset_bitfield;
    public static final Map<_Fields, FieldMetaData> metaDataMap;
    
    public GetOpenTxnsResponse() {
        this.__isset_bitfield = 0;
    }
    
    public GetOpenTxnsResponse(final long txn_high_water_mark, final Set<Long> open_txns) {
        this();
        this.txn_high_water_mark = txn_high_water_mark;
        this.setTxn_high_water_markIsSet(true);
        this.open_txns = open_txns;
    }
    
    public GetOpenTxnsResponse(final GetOpenTxnsResponse other) {
        this.__isset_bitfield = 0;
        this.__isset_bitfield = other.__isset_bitfield;
        this.txn_high_water_mark = other.txn_high_water_mark;
        if (other.isSetOpen_txns()) {
            final Set<Long> __this__open_txns = new HashSet<Long>();
            for (final Long other_element : other.open_txns) {
                __this__open_txns.add(other_element);
            }
            this.open_txns = __this__open_txns;
        }
    }
    
    @Override
    public GetOpenTxnsResponse deepCopy() {
        return new GetOpenTxnsResponse(this);
    }
    
    @Override
    public void clear() {
        this.setTxn_high_water_markIsSet(false);
        this.txn_high_water_mark = 0L;
        this.open_txns = null;
    }
    
    public long getTxn_high_water_mark() {
        return this.txn_high_water_mark;
    }
    
    public void setTxn_high_water_mark(final long txn_high_water_mark) {
        this.txn_high_water_mark = txn_high_water_mark;
        this.setTxn_high_water_markIsSet(true);
    }
    
    public void unsetTxn_high_water_mark() {
        this.__isset_bitfield = EncodingUtils.clearBit(this.__isset_bitfield, 0);
    }
    
    public boolean isSetTxn_high_water_mark() {
        return EncodingUtils.testBit(this.__isset_bitfield, 0);
    }
    
    public void setTxn_high_water_markIsSet(final boolean value) {
        this.__isset_bitfield = EncodingUtils.setBit(this.__isset_bitfield, 0, value);
    }
    
    public int getOpen_txnsSize() {
        return (this.open_txns == null) ? 0 : this.open_txns.size();
    }
    
    public Iterator<Long> getOpen_txnsIterator() {
        return (this.open_txns == null) ? null : this.open_txns.iterator();
    }
    
    public void addToOpen_txns(final long elem) {
        if (this.open_txns == null) {
            this.open_txns = new HashSet<Long>();
        }
        this.open_txns.add(elem);
    }
    
    public Set<Long> getOpen_txns() {
        return this.open_txns;
    }
    
    public void setOpen_txns(final Set<Long> open_txns) {
        this.open_txns = open_txns;
    }
    
    public void unsetOpen_txns() {
        this.open_txns = null;
    }
    
    public boolean isSetOpen_txns() {
        return this.open_txns != null;
    }
    
    public void setOpen_txnsIsSet(final boolean value) {
        if (!value) {
            this.open_txns = null;
        }
    }
    
    @Override
    public void setFieldValue(final _Fields field, final Object value) {
        switch (field) {
            case TXN_HIGH_WATER_MARK: {
                if (value == null) {
                    this.unsetTxn_high_water_mark();
                    break;
                }
                this.setTxn_high_water_mark((long)value);
                break;
            }
            case OPEN_TXNS: {
                if (value == null) {
                    this.unsetOpen_txns();
                    break;
                }
                this.setOpen_txns((Set<Long>)value);
                break;
            }
        }
    }
    
    @Override
    public Object getFieldValue(final _Fields field) {
        switch (field) {
            case TXN_HIGH_WATER_MARK: {
                return this.getTxn_high_water_mark();
            }
            case OPEN_TXNS: {
                return this.getOpen_txns();
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
            case TXN_HIGH_WATER_MARK: {
                return this.isSetTxn_high_water_mark();
            }
            case OPEN_TXNS: {
                return this.isSetOpen_txns();
            }
            default: {
                throw new IllegalStateException();
            }
        }
    }
    
    @Override
    public boolean equals(final Object that) {
        return that != null && that instanceof GetOpenTxnsResponse && this.equals((GetOpenTxnsResponse)that);
    }
    
    public boolean equals(final GetOpenTxnsResponse that) {
        if (that == null) {
            return false;
        }
        final boolean this_present_txn_high_water_mark = true;
        final boolean that_present_txn_high_water_mark = true;
        if (this_present_txn_high_water_mark || that_present_txn_high_water_mark) {
            if (!this_present_txn_high_water_mark || !that_present_txn_high_water_mark) {
                return false;
            }
            if (this.txn_high_water_mark != that.txn_high_water_mark) {
                return false;
            }
        }
        final boolean this_present_open_txns = this.isSetOpen_txns();
        final boolean that_present_open_txns = that.isSetOpen_txns();
        if (this_present_open_txns || that_present_open_txns) {
            if (!this_present_open_txns || !that_present_open_txns) {
                return false;
            }
            if (!this.open_txns.equals(that.open_txns)) {
                return false;
            }
        }
        return true;
    }
    
    @Override
    public int hashCode() {
        final HashCodeBuilder builder = new HashCodeBuilder();
        final boolean present_txn_high_water_mark = true;
        builder.append(present_txn_high_water_mark);
        if (present_txn_high_water_mark) {
            builder.append(this.txn_high_water_mark);
        }
        final boolean present_open_txns = this.isSetOpen_txns();
        builder.append(present_open_txns);
        if (present_open_txns) {
            builder.append(this.open_txns);
        }
        return builder.toHashCode();
    }
    
    @Override
    public int compareTo(final GetOpenTxnsResponse other) {
        if (!this.getClass().equals(other.getClass())) {
            return this.getClass().getName().compareTo(other.getClass().getName());
        }
        int lastComparison = 0;
        final GetOpenTxnsResponse typedOther = other;
        lastComparison = Boolean.valueOf(this.isSetTxn_high_water_mark()).compareTo(Boolean.valueOf(typedOther.isSetTxn_high_water_mark()));
        if (lastComparison != 0) {
            return lastComparison;
        }
        if (this.isSetTxn_high_water_mark()) {
            lastComparison = TBaseHelper.compareTo(this.txn_high_water_mark, typedOther.txn_high_water_mark);
            if (lastComparison != 0) {
                return lastComparison;
            }
        }
        lastComparison = Boolean.valueOf(this.isSetOpen_txns()).compareTo(Boolean.valueOf(typedOther.isSetOpen_txns()));
        if (lastComparison != 0) {
            return lastComparison;
        }
        if (this.isSetOpen_txns()) {
            lastComparison = TBaseHelper.compareTo(this.open_txns, typedOther.open_txns);
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
        GetOpenTxnsResponse.schemes.get(iprot.getScheme()).getScheme().read(iprot, this);
    }
    
    @Override
    public void write(final TProtocol oprot) throws TException {
        GetOpenTxnsResponse.schemes.get(oprot.getScheme()).getScheme().write(oprot, this);
    }
    
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("GetOpenTxnsResponse(");
        boolean first = true;
        sb.append("txn_high_water_mark:");
        sb.append(this.txn_high_water_mark);
        first = false;
        if (!first) {
            sb.append(", ");
        }
        sb.append("open_txns:");
        if (this.open_txns == null) {
            sb.append("null");
        }
        else {
            sb.append(this.open_txns);
        }
        first = false;
        sb.append(")");
        return sb.toString();
    }
    
    public void validate() throws TException {
        if (!this.isSetTxn_high_water_mark()) {
            throw new TProtocolException("Required field 'txn_high_water_mark' is unset! Struct:" + this.toString());
        }
        if (!this.isSetOpen_txns()) {
            throw new TProtocolException("Required field 'open_txns' is unset! Struct:" + this.toString());
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
        STRUCT_DESC = new TStruct("GetOpenTxnsResponse");
        TXN_HIGH_WATER_MARK_FIELD_DESC = new TField("txn_high_water_mark", (byte)10, (short)1);
        OPEN_TXNS_FIELD_DESC = new TField("open_txns", (byte)14, (short)2);
        (schemes = new HashMap<Class<? extends IScheme>, SchemeFactory>()).put(StandardScheme.class, new GetOpenTxnsResponseStandardSchemeFactory());
        GetOpenTxnsResponse.schemes.put(TupleScheme.class, new GetOpenTxnsResponseTupleSchemeFactory());
        final Map<_Fields, FieldMetaData> tmpMap = new EnumMap<_Fields, FieldMetaData>(_Fields.class);
        tmpMap.put(_Fields.TXN_HIGH_WATER_MARK, new FieldMetaData("txn_high_water_mark", (byte)1, new FieldValueMetaData((byte)10)));
        tmpMap.put(_Fields.OPEN_TXNS, new FieldMetaData("open_txns", (byte)1, new SetMetaData((byte)14, new FieldValueMetaData((byte)10))));
        FieldMetaData.addStructMetaDataMap(GetOpenTxnsResponse.class, metaDataMap = Collections.unmodifiableMap((Map<? extends _Fields, ? extends FieldMetaData>)tmpMap));
    }
    
    public enum _Fields implements TFieldIdEnum
    {
        TXN_HIGH_WATER_MARK((short)1, "txn_high_water_mark"), 
        OPEN_TXNS((short)2, "open_txns");
        
        private static final Map<String, _Fields> byName;
        private final short _thriftId;
        private final String _fieldName;
        
        public static _Fields findByThriftId(final int fieldId) {
            switch (fieldId) {
                case 1: {
                    return _Fields.TXN_HIGH_WATER_MARK;
                }
                case 2: {
                    return _Fields.OPEN_TXNS;
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
    
    private static class GetOpenTxnsResponseStandardSchemeFactory implements SchemeFactory
    {
        @Override
        public GetOpenTxnsResponseStandardScheme getScheme() {
            return new GetOpenTxnsResponseStandardScheme();
        }
    }
    
    private static class GetOpenTxnsResponseStandardScheme extends StandardScheme<GetOpenTxnsResponse>
    {
        @Override
        public void read(final TProtocol iprot, final GetOpenTxnsResponse struct) throws TException {
            iprot.readStructBegin();
            while (true) {
                final TField schemeField = iprot.readFieldBegin();
                if (schemeField.type == 0) {
                    break;
                }
                switch (schemeField.id) {
                    case 1: {
                        if (schemeField.type == 10) {
                            struct.txn_high_water_mark = iprot.readI64();
                            struct.setTxn_high_water_markIsSet(true);
                            break;
                        }
                        TProtocolUtil.skip(iprot, schemeField.type);
                        break;
                    }
                    case 2: {
                        if (schemeField.type == 14) {
                            final TSet _set436 = iprot.readSetBegin();
                            struct.open_txns = (Set<Long>)new HashSet(2 * _set436.size);
                            for (int _i437 = 0; _i437 < _set436.size; ++_i437) {
                                final long _elem438 = iprot.readI64();
                                struct.open_txns.add(_elem438);
                            }
                            iprot.readSetEnd();
                            struct.setOpen_txnsIsSet(true);
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
        public void write(final TProtocol oprot, final GetOpenTxnsResponse struct) throws TException {
            struct.validate();
            oprot.writeStructBegin(GetOpenTxnsResponse.STRUCT_DESC);
            oprot.writeFieldBegin(GetOpenTxnsResponse.TXN_HIGH_WATER_MARK_FIELD_DESC);
            oprot.writeI64(struct.txn_high_water_mark);
            oprot.writeFieldEnd();
            if (struct.open_txns != null) {
                oprot.writeFieldBegin(GetOpenTxnsResponse.OPEN_TXNS_FIELD_DESC);
                oprot.writeSetBegin(new TSet((byte)10, struct.open_txns.size()));
                for (final long _iter439 : struct.open_txns) {
                    oprot.writeI64(_iter439);
                }
                oprot.writeSetEnd();
                oprot.writeFieldEnd();
            }
            oprot.writeFieldStop();
            oprot.writeStructEnd();
        }
    }
    
    private static class GetOpenTxnsResponseTupleSchemeFactory implements SchemeFactory
    {
        @Override
        public GetOpenTxnsResponseTupleScheme getScheme() {
            return new GetOpenTxnsResponseTupleScheme();
        }
    }
    
    private static class GetOpenTxnsResponseTupleScheme extends TupleScheme<GetOpenTxnsResponse>
    {
        @Override
        public void write(final TProtocol prot, final GetOpenTxnsResponse struct) throws TException {
            final TTupleProtocol oprot = (TTupleProtocol)prot;
            oprot.writeI64(struct.txn_high_water_mark);
            oprot.writeI32(struct.open_txns.size());
            for (final long _iter440 : struct.open_txns) {
                oprot.writeI64(_iter440);
            }
        }
        
        @Override
        public void read(final TProtocol prot, final GetOpenTxnsResponse struct) throws TException {
            final TTupleProtocol iprot = (TTupleProtocol)prot;
            struct.txn_high_water_mark = iprot.readI64();
            struct.setTxn_high_water_markIsSet(true);
            final TSet _set441 = new TSet((byte)10, iprot.readI32());
            struct.open_txns = (Set<Long>)new HashSet(2 * _set441.size);
            for (int _i442 = 0; _i442 < _set441.size; ++_i442) {
                final long _elem443 = iprot.readI64();
                struct.open_txns.add(_elem443);
            }
            struct.setOpen_txnsIsSet(true);
        }
    }
}
