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
import org.apache.thrift.EncodingUtils;
import org.apache.thrift.meta_data.FieldMetaData;
import org.apache.thrift.scheme.SchemeFactory;
import org.apache.thrift.scheme.IScheme;
import java.util.Map;
import org.apache.thrift.protocol.TField;
import org.apache.thrift.protocol.TStruct;
import java.io.Serializable;
import org.apache.thrift.TBase;

public class DateColumnStatsData implements TBase<DateColumnStatsData, _Fields>, Serializable, Cloneable
{
    private static final TStruct STRUCT_DESC;
    private static final TField LOW_VALUE_FIELD_DESC;
    private static final TField HIGH_VALUE_FIELD_DESC;
    private static final TField NUM_NULLS_FIELD_DESC;
    private static final TField NUM_DVS_FIELD_DESC;
    private static final Map<Class<? extends IScheme>, SchemeFactory> schemes;
    private Date lowValue;
    private Date highValue;
    private long numNulls;
    private long numDVs;
    private static final int __NUMNULLS_ISSET_ID = 0;
    private static final int __NUMDVS_ISSET_ID = 1;
    private byte __isset_bitfield;
    private _Fields[] optionals;
    public static final Map<_Fields, FieldMetaData> metaDataMap;
    
    public DateColumnStatsData() {
        this.__isset_bitfield = 0;
        this.optionals = new _Fields[] { _Fields.LOW_VALUE, _Fields.HIGH_VALUE };
    }
    
    public DateColumnStatsData(final long numNulls, final long numDVs) {
        this();
        this.numNulls = numNulls;
        this.setNumNullsIsSet(true);
        this.numDVs = numDVs;
        this.setNumDVsIsSet(true);
    }
    
    public DateColumnStatsData(final DateColumnStatsData other) {
        this.__isset_bitfield = 0;
        this.optionals = new _Fields[] { _Fields.LOW_VALUE, _Fields.HIGH_VALUE };
        this.__isset_bitfield = other.__isset_bitfield;
        if (other.isSetLowValue()) {
            this.lowValue = new Date(other.lowValue);
        }
        if (other.isSetHighValue()) {
            this.highValue = new Date(other.highValue);
        }
        this.numNulls = other.numNulls;
        this.numDVs = other.numDVs;
    }
    
    @Override
    public DateColumnStatsData deepCopy() {
        return new DateColumnStatsData(this);
    }
    
    @Override
    public void clear() {
        this.lowValue = null;
        this.highValue = null;
        this.setNumNullsIsSet(false);
        this.numNulls = 0L;
        this.setNumDVsIsSet(false);
        this.numDVs = 0L;
    }
    
    public Date getLowValue() {
        return this.lowValue;
    }
    
    public void setLowValue(final Date lowValue) {
        this.lowValue = lowValue;
    }
    
    public void unsetLowValue() {
        this.lowValue = null;
    }
    
    public boolean isSetLowValue() {
        return this.lowValue != null;
    }
    
    public void setLowValueIsSet(final boolean value) {
        if (!value) {
            this.lowValue = null;
        }
    }
    
    public Date getHighValue() {
        return this.highValue;
    }
    
    public void setHighValue(final Date highValue) {
        this.highValue = highValue;
    }
    
    public void unsetHighValue() {
        this.highValue = null;
    }
    
    public boolean isSetHighValue() {
        return this.highValue != null;
    }
    
    public void setHighValueIsSet(final boolean value) {
        if (!value) {
            this.highValue = null;
        }
    }
    
    public long getNumNulls() {
        return this.numNulls;
    }
    
    public void setNumNulls(final long numNulls) {
        this.numNulls = numNulls;
        this.setNumNullsIsSet(true);
    }
    
    public void unsetNumNulls() {
        this.__isset_bitfield = EncodingUtils.clearBit(this.__isset_bitfield, 0);
    }
    
    public boolean isSetNumNulls() {
        return EncodingUtils.testBit(this.__isset_bitfield, 0);
    }
    
    public void setNumNullsIsSet(final boolean value) {
        this.__isset_bitfield = EncodingUtils.setBit(this.__isset_bitfield, 0, value);
    }
    
    public long getNumDVs() {
        return this.numDVs;
    }
    
    public void setNumDVs(final long numDVs) {
        this.numDVs = numDVs;
        this.setNumDVsIsSet(true);
    }
    
    public void unsetNumDVs() {
        this.__isset_bitfield = EncodingUtils.clearBit(this.__isset_bitfield, 1);
    }
    
    public boolean isSetNumDVs() {
        return EncodingUtils.testBit(this.__isset_bitfield, 1);
    }
    
    public void setNumDVsIsSet(final boolean value) {
        this.__isset_bitfield = EncodingUtils.setBit(this.__isset_bitfield, 1, value);
    }
    
    @Override
    public void setFieldValue(final _Fields field, final Object value) {
        switch (field) {
            case LOW_VALUE: {
                if (value == null) {
                    this.unsetLowValue();
                    break;
                }
                this.setLowValue((Date)value);
                break;
            }
            case HIGH_VALUE: {
                if (value == null) {
                    this.unsetHighValue();
                    break;
                }
                this.setHighValue((Date)value);
                break;
            }
            case NUM_NULLS: {
                if (value == null) {
                    this.unsetNumNulls();
                    break;
                }
                this.setNumNulls((long)value);
                break;
            }
            case NUM_DVS: {
                if (value == null) {
                    this.unsetNumDVs();
                    break;
                }
                this.setNumDVs((long)value);
                break;
            }
        }
    }
    
    @Override
    public Object getFieldValue(final _Fields field) {
        switch (field) {
            case LOW_VALUE: {
                return this.getLowValue();
            }
            case HIGH_VALUE: {
                return this.getHighValue();
            }
            case NUM_NULLS: {
                return this.getNumNulls();
            }
            case NUM_DVS: {
                return this.getNumDVs();
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
            case LOW_VALUE: {
                return this.isSetLowValue();
            }
            case HIGH_VALUE: {
                return this.isSetHighValue();
            }
            case NUM_NULLS: {
                return this.isSetNumNulls();
            }
            case NUM_DVS: {
                return this.isSetNumDVs();
            }
            default: {
                throw new IllegalStateException();
            }
        }
    }
    
    @Override
    public boolean equals(final Object that) {
        return that != null && that instanceof DateColumnStatsData && this.equals((DateColumnStatsData)that);
    }
    
    public boolean equals(final DateColumnStatsData that) {
        if (that == null) {
            return false;
        }
        final boolean this_present_lowValue = this.isSetLowValue();
        final boolean that_present_lowValue = that.isSetLowValue();
        if (this_present_lowValue || that_present_lowValue) {
            if (!this_present_lowValue || !that_present_lowValue) {
                return false;
            }
            if (!this.lowValue.equals(that.lowValue)) {
                return false;
            }
        }
        final boolean this_present_highValue = this.isSetHighValue();
        final boolean that_present_highValue = that.isSetHighValue();
        if (this_present_highValue || that_present_highValue) {
            if (!this_present_highValue || !that_present_highValue) {
                return false;
            }
            if (!this.highValue.equals(that.highValue)) {
                return false;
            }
        }
        final boolean this_present_numNulls = true;
        final boolean that_present_numNulls = true;
        if (this_present_numNulls || that_present_numNulls) {
            if (!this_present_numNulls || !that_present_numNulls) {
                return false;
            }
            if (this.numNulls != that.numNulls) {
                return false;
            }
        }
        final boolean this_present_numDVs = true;
        final boolean that_present_numDVs = true;
        if (this_present_numDVs || that_present_numDVs) {
            if (!this_present_numDVs || !that_present_numDVs) {
                return false;
            }
            if (this.numDVs != that.numDVs) {
                return false;
            }
        }
        return true;
    }
    
    @Override
    public int hashCode() {
        final HashCodeBuilder builder = new HashCodeBuilder();
        final boolean present_lowValue = this.isSetLowValue();
        builder.append(present_lowValue);
        if (present_lowValue) {
            builder.append(this.lowValue);
        }
        final boolean present_highValue = this.isSetHighValue();
        builder.append(present_highValue);
        if (present_highValue) {
            builder.append(this.highValue);
        }
        final boolean present_numNulls = true;
        builder.append(present_numNulls);
        if (present_numNulls) {
            builder.append(this.numNulls);
        }
        final boolean present_numDVs = true;
        builder.append(present_numDVs);
        if (present_numDVs) {
            builder.append(this.numDVs);
        }
        return builder.toHashCode();
    }
    
    @Override
    public int compareTo(final DateColumnStatsData other) {
        if (!this.getClass().equals(other.getClass())) {
            return this.getClass().getName().compareTo(other.getClass().getName());
        }
        int lastComparison = 0;
        final DateColumnStatsData typedOther = other;
        lastComparison = Boolean.valueOf(this.isSetLowValue()).compareTo(Boolean.valueOf(typedOther.isSetLowValue()));
        if (lastComparison != 0) {
            return lastComparison;
        }
        if (this.isSetLowValue()) {
            lastComparison = TBaseHelper.compareTo(this.lowValue, typedOther.lowValue);
            if (lastComparison != 0) {
                return lastComparison;
            }
        }
        lastComparison = Boolean.valueOf(this.isSetHighValue()).compareTo(Boolean.valueOf(typedOther.isSetHighValue()));
        if (lastComparison != 0) {
            return lastComparison;
        }
        if (this.isSetHighValue()) {
            lastComparison = TBaseHelper.compareTo(this.highValue, typedOther.highValue);
            if (lastComparison != 0) {
                return lastComparison;
            }
        }
        lastComparison = Boolean.valueOf(this.isSetNumNulls()).compareTo(Boolean.valueOf(typedOther.isSetNumNulls()));
        if (lastComparison != 0) {
            return lastComparison;
        }
        if (this.isSetNumNulls()) {
            lastComparison = TBaseHelper.compareTo(this.numNulls, typedOther.numNulls);
            if (lastComparison != 0) {
                return lastComparison;
            }
        }
        lastComparison = Boolean.valueOf(this.isSetNumDVs()).compareTo(Boolean.valueOf(typedOther.isSetNumDVs()));
        if (lastComparison != 0) {
            return lastComparison;
        }
        if (this.isSetNumDVs()) {
            lastComparison = TBaseHelper.compareTo(this.numDVs, typedOther.numDVs);
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
        DateColumnStatsData.schemes.get(iprot.getScheme()).getScheme().read(iprot, this);
    }
    
    @Override
    public void write(final TProtocol oprot) throws TException {
        DateColumnStatsData.schemes.get(oprot.getScheme()).getScheme().write(oprot, this);
    }
    
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("DateColumnStatsData(");
        boolean first = true;
        if (this.isSetLowValue()) {
            sb.append("lowValue:");
            if (this.lowValue == null) {
                sb.append("null");
            }
            else {
                sb.append(this.lowValue);
            }
            first = false;
        }
        if (this.isSetHighValue()) {
            if (!first) {
                sb.append(", ");
            }
            sb.append("highValue:");
            if (this.highValue == null) {
                sb.append("null");
            }
            else {
                sb.append(this.highValue);
            }
            first = false;
        }
        if (!first) {
            sb.append(", ");
        }
        sb.append("numNulls:");
        sb.append(this.numNulls);
        first = false;
        if (!first) {
            sb.append(", ");
        }
        sb.append("numDVs:");
        sb.append(this.numDVs);
        first = false;
        sb.append(")");
        return sb.toString();
    }
    
    public void validate() throws TException {
        if (!this.isSetNumNulls()) {
            throw new TProtocolException("Required field 'numNulls' is unset! Struct:" + this.toString());
        }
        if (!this.isSetNumDVs()) {
            throw new TProtocolException("Required field 'numDVs' is unset! Struct:" + this.toString());
        }
        if (this.lowValue != null) {
            this.lowValue.validate();
        }
        if (this.highValue != null) {
            this.highValue.validate();
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
        STRUCT_DESC = new TStruct("DateColumnStatsData");
        LOW_VALUE_FIELD_DESC = new TField("lowValue", (byte)12, (short)1);
        HIGH_VALUE_FIELD_DESC = new TField("highValue", (byte)12, (short)2);
        NUM_NULLS_FIELD_DESC = new TField("numNulls", (byte)10, (short)3);
        NUM_DVS_FIELD_DESC = new TField("numDVs", (byte)10, (short)4);
        (schemes = new HashMap<Class<? extends IScheme>, SchemeFactory>()).put(StandardScheme.class, new DateColumnStatsDataStandardSchemeFactory());
        DateColumnStatsData.schemes.put(TupleScheme.class, new DateColumnStatsDataTupleSchemeFactory());
        final Map<_Fields, FieldMetaData> tmpMap = new EnumMap<_Fields, FieldMetaData>(_Fields.class);
        tmpMap.put(_Fields.LOW_VALUE, new FieldMetaData("lowValue", (byte)2, new StructMetaData((byte)12, Date.class)));
        tmpMap.put(_Fields.HIGH_VALUE, new FieldMetaData("highValue", (byte)2, new StructMetaData((byte)12, Date.class)));
        tmpMap.put(_Fields.NUM_NULLS, new FieldMetaData("numNulls", (byte)1, new FieldValueMetaData((byte)10)));
        tmpMap.put(_Fields.NUM_DVS, new FieldMetaData("numDVs", (byte)1, new FieldValueMetaData((byte)10)));
        FieldMetaData.addStructMetaDataMap(DateColumnStatsData.class, metaDataMap = Collections.unmodifiableMap((Map<? extends _Fields, ? extends FieldMetaData>)tmpMap));
    }
    
    public enum _Fields implements TFieldIdEnum
    {
        LOW_VALUE((short)1, "lowValue"), 
        HIGH_VALUE((short)2, "highValue"), 
        NUM_NULLS((short)3, "numNulls"), 
        NUM_DVS((short)4, "numDVs");
        
        private static final Map<String, _Fields> byName;
        private final short _thriftId;
        private final String _fieldName;
        
        public static _Fields findByThriftId(final int fieldId) {
            switch (fieldId) {
                case 1: {
                    return _Fields.LOW_VALUE;
                }
                case 2: {
                    return _Fields.HIGH_VALUE;
                }
                case 3: {
                    return _Fields.NUM_NULLS;
                }
                case 4: {
                    return _Fields.NUM_DVS;
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
    
    private static class DateColumnStatsDataStandardSchemeFactory implements SchemeFactory
    {
        @Override
        public DateColumnStatsDataStandardScheme getScheme() {
            return new DateColumnStatsDataStandardScheme();
        }
    }
    
    private static class DateColumnStatsDataStandardScheme extends StandardScheme<DateColumnStatsData>
    {
        @Override
        public void read(final TProtocol iprot, final DateColumnStatsData struct) throws TException {
            iprot.readStructBegin();
            while (true) {
                final TField schemeField = iprot.readFieldBegin();
                if (schemeField.type == 0) {
                    break;
                }
                switch (schemeField.id) {
                    case 1: {
                        if (schemeField.type == 12) {
                            struct.lowValue = new Date();
                            struct.lowValue.read(iprot);
                            struct.setLowValueIsSet(true);
                            break;
                        }
                        TProtocolUtil.skip(iprot, schemeField.type);
                        break;
                    }
                    case 2: {
                        if (schemeField.type == 12) {
                            struct.highValue = new Date();
                            struct.highValue.read(iprot);
                            struct.setHighValueIsSet(true);
                            break;
                        }
                        TProtocolUtil.skip(iprot, schemeField.type);
                        break;
                    }
                    case 3: {
                        if (schemeField.type == 10) {
                            struct.numNulls = iprot.readI64();
                            struct.setNumNullsIsSet(true);
                            break;
                        }
                        TProtocolUtil.skip(iprot, schemeField.type);
                        break;
                    }
                    case 4: {
                        if (schemeField.type == 10) {
                            struct.numDVs = iprot.readI64();
                            struct.setNumDVsIsSet(true);
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
        public void write(final TProtocol oprot, final DateColumnStatsData struct) throws TException {
            struct.validate();
            oprot.writeStructBegin(DateColumnStatsData.STRUCT_DESC);
            if (struct.lowValue != null && struct.isSetLowValue()) {
                oprot.writeFieldBegin(DateColumnStatsData.LOW_VALUE_FIELD_DESC);
                struct.lowValue.write(oprot);
                oprot.writeFieldEnd();
            }
            if (struct.highValue != null && struct.isSetHighValue()) {
                oprot.writeFieldBegin(DateColumnStatsData.HIGH_VALUE_FIELD_DESC);
                struct.highValue.write(oprot);
                oprot.writeFieldEnd();
            }
            oprot.writeFieldBegin(DateColumnStatsData.NUM_NULLS_FIELD_DESC);
            oprot.writeI64(struct.numNulls);
            oprot.writeFieldEnd();
            oprot.writeFieldBegin(DateColumnStatsData.NUM_DVS_FIELD_DESC);
            oprot.writeI64(struct.numDVs);
            oprot.writeFieldEnd();
            oprot.writeFieldStop();
            oprot.writeStructEnd();
        }
    }
    
    private static class DateColumnStatsDataTupleSchemeFactory implements SchemeFactory
    {
        @Override
        public DateColumnStatsDataTupleScheme getScheme() {
            return new DateColumnStatsDataTupleScheme();
        }
    }
    
    private static class DateColumnStatsDataTupleScheme extends TupleScheme<DateColumnStatsData>
    {
        @Override
        public void write(final TProtocol prot, final DateColumnStatsData struct) throws TException {
            final TTupleProtocol oprot = (TTupleProtocol)prot;
            oprot.writeI64(struct.numNulls);
            oprot.writeI64(struct.numDVs);
            final BitSet optionals = new BitSet();
            if (struct.isSetLowValue()) {
                optionals.set(0);
            }
            if (struct.isSetHighValue()) {
                optionals.set(1);
            }
            oprot.writeBitSet(optionals, 2);
            if (struct.isSetLowValue()) {
                struct.lowValue.write(oprot);
            }
            if (struct.isSetHighValue()) {
                struct.highValue.write(oprot);
            }
        }
        
        @Override
        public void read(final TProtocol prot, final DateColumnStatsData struct) throws TException {
            final TTupleProtocol iprot = (TTupleProtocol)prot;
            struct.numNulls = iprot.readI64();
            struct.setNumNullsIsSet(true);
            struct.numDVs = iprot.readI64();
            struct.setNumDVsIsSet(true);
            final BitSet incoming = iprot.readBitSet(2);
            if (incoming.get(0)) {
                struct.lowValue = new Date();
                struct.lowValue.read(iprot);
                struct.setLowValueIsSet(true);
            }
            if (incoming.get(1)) {
                struct.highValue = new Date();
                struct.highValue.read(iprot);
                struct.setHighValueIsSet(true);
            }
        }
    }
}
