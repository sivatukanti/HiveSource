// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.hive.metastore.api;

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

public class BinaryColumnStatsData implements TBase<BinaryColumnStatsData, _Fields>, Serializable, Cloneable
{
    private static final TStruct STRUCT_DESC;
    private static final TField MAX_COL_LEN_FIELD_DESC;
    private static final TField AVG_COL_LEN_FIELD_DESC;
    private static final TField NUM_NULLS_FIELD_DESC;
    private static final Map<Class<? extends IScheme>, SchemeFactory> schemes;
    private long maxColLen;
    private double avgColLen;
    private long numNulls;
    private static final int __MAXCOLLEN_ISSET_ID = 0;
    private static final int __AVGCOLLEN_ISSET_ID = 1;
    private static final int __NUMNULLS_ISSET_ID = 2;
    private byte __isset_bitfield;
    public static final Map<_Fields, FieldMetaData> metaDataMap;
    
    public BinaryColumnStatsData() {
        this.__isset_bitfield = 0;
    }
    
    public BinaryColumnStatsData(final long maxColLen, final double avgColLen, final long numNulls) {
        this();
        this.maxColLen = maxColLen;
        this.setMaxColLenIsSet(true);
        this.avgColLen = avgColLen;
        this.setAvgColLenIsSet(true);
        this.numNulls = numNulls;
        this.setNumNullsIsSet(true);
    }
    
    public BinaryColumnStatsData(final BinaryColumnStatsData other) {
        this.__isset_bitfield = 0;
        this.__isset_bitfield = other.__isset_bitfield;
        this.maxColLen = other.maxColLen;
        this.avgColLen = other.avgColLen;
        this.numNulls = other.numNulls;
    }
    
    @Override
    public BinaryColumnStatsData deepCopy() {
        return new BinaryColumnStatsData(this);
    }
    
    @Override
    public void clear() {
        this.setMaxColLenIsSet(false);
        this.maxColLen = 0L;
        this.setAvgColLenIsSet(false);
        this.avgColLen = 0.0;
        this.setNumNullsIsSet(false);
        this.numNulls = 0L;
    }
    
    public long getMaxColLen() {
        return this.maxColLen;
    }
    
    public void setMaxColLen(final long maxColLen) {
        this.maxColLen = maxColLen;
        this.setMaxColLenIsSet(true);
    }
    
    public void unsetMaxColLen() {
        this.__isset_bitfield = EncodingUtils.clearBit(this.__isset_bitfield, 0);
    }
    
    public boolean isSetMaxColLen() {
        return EncodingUtils.testBit(this.__isset_bitfield, 0);
    }
    
    public void setMaxColLenIsSet(final boolean value) {
        this.__isset_bitfield = EncodingUtils.setBit(this.__isset_bitfield, 0, value);
    }
    
    public double getAvgColLen() {
        return this.avgColLen;
    }
    
    public void setAvgColLen(final double avgColLen) {
        this.avgColLen = avgColLen;
        this.setAvgColLenIsSet(true);
    }
    
    public void unsetAvgColLen() {
        this.__isset_bitfield = EncodingUtils.clearBit(this.__isset_bitfield, 1);
    }
    
    public boolean isSetAvgColLen() {
        return EncodingUtils.testBit(this.__isset_bitfield, 1);
    }
    
    public void setAvgColLenIsSet(final boolean value) {
        this.__isset_bitfield = EncodingUtils.setBit(this.__isset_bitfield, 1, value);
    }
    
    public long getNumNulls() {
        return this.numNulls;
    }
    
    public void setNumNulls(final long numNulls) {
        this.numNulls = numNulls;
        this.setNumNullsIsSet(true);
    }
    
    public void unsetNumNulls() {
        this.__isset_bitfield = EncodingUtils.clearBit(this.__isset_bitfield, 2);
    }
    
    public boolean isSetNumNulls() {
        return EncodingUtils.testBit(this.__isset_bitfield, 2);
    }
    
    public void setNumNullsIsSet(final boolean value) {
        this.__isset_bitfield = EncodingUtils.setBit(this.__isset_bitfield, 2, value);
    }
    
    @Override
    public void setFieldValue(final _Fields field, final Object value) {
        switch (field) {
            case MAX_COL_LEN: {
                if (value == null) {
                    this.unsetMaxColLen();
                    break;
                }
                this.setMaxColLen((long)value);
                break;
            }
            case AVG_COL_LEN: {
                if (value == null) {
                    this.unsetAvgColLen();
                    break;
                }
                this.setAvgColLen((double)value);
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
        }
    }
    
    @Override
    public Object getFieldValue(final _Fields field) {
        switch (field) {
            case MAX_COL_LEN: {
                return this.getMaxColLen();
            }
            case AVG_COL_LEN: {
                return this.getAvgColLen();
            }
            case NUM_NULLS: {
                return this.getNumNulls();
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
            case MAX_COL_LEN: {
                return this.isSetMaxColLen();
            }
            case AVG_COL_LEN: {
                return this.isSetAvgColLen();
            }
            case NUM_NULLS: {
                return this.isSetNumNulls();
            }
            default: {
                throw new IllegalStateException();
            }
        }
    }
    
    @Override
    public boolean equals(final Object that) {
        return that != null && that instanceof BinaryColumnStatsData && this.equals((BinaryColumnStatsData)that);
    }
    
    public boolean equals(final BinaryColumnStatsData that) {
        if (that == null) {
            return false;
        }
        final boolean this_present_maxColLen = true;
        final boolean that_present_maxColLen = true;
        if (this_present_maxColLen || that_present_maxColLen) {
            if (!this_present_maxColLen || !that_present_maxColLen) {
                return false;
            }
            if (this.maxColLen != that.maxColLen) {
                return false;
            }
        }
        final boolean this_present_avgColLen = true;
        final boolean that_present_avgColLen = true;
        if (this_present_avgColLen || that_present_avgColLen) {
            if (!this_present_avgColLen || !that_present_avgColLen) {
                return false;
            }
            if (this.avgColLen != that.avgColLen) {
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
        return true;
    }
    
    @Override
    public int hashCode() {
        final HashCodeBuilder builder = new HashCodeBuilder();
        final boolean present_maxColLen = true;
        builder.append(present_maxColLen);
        if (present_maxColLen) {
            builder.append(this.maxColLen);
        }
        final boolean present_avgColLen = true;
        builder.append(present_avgColLen);
        if (present_avgColLen) {
            builder.append(this.avgColLen);
        }
        final boolean present_numNulls = true;
        builder.append(present_numNulls);
        if (present_numNulls) {
            builder.append(this.numNulls);
        }
        return builder.toHashCode();
    }
    
    @Override
    public int compareTo(final BinaryColumnStatsData other) {
        if (!this.getClass().equals(other.getClass())) {
            return this.getClass().getName().compareTo(other.getClass().getName());
        }
        int lastComparison = 0;
        final BinaryColumnStatsData typedOther = other;
        lastComparison = Boolean.valueOf(this.isSetMaxColLen()).compareTo(Boolean.valueOf(typedOther.isSetMaxColLen()));
        if (lastComparison != 0) {
            return lastComparison;
        }
        if (this.isSetMaxColLen()) {
            lastComparison = TBaseHelper.compareTo(this.maxColLen, typedOther.maxColLen);
            if (lastComparison != 0) {
                return lastComparison;
            }
        }
        lastComparison = Boolean.valueOf(this.isSetAvgColLen()).compareTo(Boolean.valueOf(typedOther.isSetAvgColLen()));
        if (lastComparison != 0) {
            return lastComparison;
        }
        if (this.isSetAvgColLen()) {
            lastComparison = TBaseHelper.compareTo(this.avgColLen, typedOther.avgColLen);
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
        return 0;
    }
    
    @Override
    public _Fields fieldForId(final int fieldId) {
        return _Fields.findByThriftId(fieldId);
    }
    
    @Override
    public void read(final TProtocol iprot) throws TException {
        BinaryColumnStatsData.schemes.get(iprot.getScheme()).getScheme().read(iprot, this);
    }
    
    @Override
    public void write(final TProtocol oprot) throws TException {
        BinaryColumnStatsData.schemes.get(oprot.getScheme()).getScheme().write(oprot, this);
    }
    
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("BinaryColumnStatsData(");
        boolean first = true;
        sb.append("maxColLen:");
        sb.append(this.maxColLen);
        first = false;
        if (!first) {
            sb.append(", ");
        }
        sb.append("avgColLen:");
        sb.append(this.avgColLen);
        first = false;
        if (!first) {
            sb.append(", ");
        }
        sb.append("numNulls:");
        sb.append(this.numNulls);
        first = false;
        sb.append(")");
        return sb.toString();
    }
    
    public void validate() throws TException {
        if (!this.isSetMaxColLen()) {
            throw new TProtocolException("Required field 'maxColLen' is unset! Struct:" + this.toString());
        }
        if (!this.isSetAvgColLen()) {
            throw new TProtocolException("Required field 'avgColLen' is unset! Struct:" + this.toString());
        }
        if (!this.isSetNumNulls()) {
            throw new TProtocolException("Required field 'numNulls' is unset! Struct:" + this.toString());
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
        STRUCT_DESC = new TStruct("BinaryColumnStatsData");
        MAX_COL_LEN_FIELD_DESC = new TField("maxColLen", (byte)10, (short)1);
        AVG_COL_LEN_FIELD_DESC = new TField("avgColLen", (byte)4, (short)2);
        NUM_NULLS_FIELD_DESC = new TField("numNulls", (byte)10, (short)3);
        (schemes = new HashMap<Class<? extends IScheme>, SchemeFactory>()).put(StandardScheme.class, new BinaryColumnStatsDataStandardSchemeFactory());
        BinaryColumnStatsData.schemes.put(TupleScheme.class, new BinaryColumnStatsDataTupleSchemeFactory());
        final Map<_Fields, FieldMetaData> tmpMap = new EnumMap<_Fields, FieldMetaData>(_Fields.class);
        tmpMap.put(_Fields.MAX_COL_LEN, new FieldMetaData("maxColLen", (byte)1, new FieldValueMetaData((byte)10)));
        tmpMap.put(_Fields.AVG_COL_LEN, new FieldMetaData("avgColLen", (byte)1, new FieldValueMetaData((byte)4)));
        tmpMap.put(_Fields.NUM_NULLS, new FieldMetaData("numNulls", (byte)1, new FieldValueMetaData((byte)10)));
        FieldMetaData.addStructMetaDataMap(BinaryColumnStatsData.class, metaDataMap = Collections.unmodifiableMap((Map<? extends _Fields, ? extends FieldMetaData>)tmpMap));
    }
    
    public enum _Fields implements TFieldIdEnum
    {
        MAX_COL_LEN((short)1, "maxColLen"), 
        AVG_COL_LEN((short)2, "avgColLen"), 
        NUM_NULLS((short)3, "numNulls");
        
        private static final Map<String, _Fields> byName;
        private final short _thriftId;
        private final String _fieldName;
        
        public static _Fields findByThriftId(final int fieldId) {
            switch (fieldId) {
                case 1: {
                    return _Fields.MAX_COL_LEN;
                }
                case 2: {
                    return _Fields.AVG_COL_LEN;
                }
                case 3: {
                    return _Fields.NUM_NULLS;
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
    
    private static class BinaryColumnStatsDataStandardSchemeFactory implements SchemeFactory
    {
        @Override
        public BinaryColumnStatsDataStandardScheme getScheme() {
            return new BinaryColumnStatsDataStandardScheme();
        }
    }
    
    private static class BinaryColumnStatsDataStandardScheme extends StandardScheme<BinaryColumnStatsData>
    {
        @Override
        public void read(final TProtocol iprot, final BinaryColumnStatsData struct) throws TException {
            iprot.readStructBegin();
            while (true) {
                final TField schemeField = iprot.readFieldBegin();
                if (schemeField.type == 0) {
                    break;
                }
                switch (schemeField.id) {
                    case 1: {
                        if (schemeField.type == 10) {
                            struct.maxColLen = iprot.readI64();
                            struct.setMaxColLenIsSet(true);
                            break;
                        }
                        TProtocolUtil.skip(iprot, schemeField.type);
                        break;
                    }
                    case 2: {
                        if (schemeField.type == 4) {
                            struct.avgColLen = iprot.readDouble();
                            struct.setAvgColLenIsSet(true);
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
        public void write(final TProtocol oprot, final BinaryColumnStatsData struct) throws TException {
            struct.validate();
            oprot.writeStructBegin(BinaryColumnStatsData.STRUCT_DESC);
            oprot.writeFieldBegin(BinaryColumnStatsData.MAX_COL_LEN_FIELD_DESC);
            oprot.writeI64(struct.maxColLen);
            oprot.writeFieldEnd();
            oprot.writeFieldBegin(BinaryColumnStatsData.AVG_COL_LEN_FIELD_DESC);
            oprot.writeDouble(struct.avgColLen);
            oprot.writeFieldEnd();
            oprot.writeFieldBegin(BinaryColumnStatsData.NUM_NULLS_FIELD_DESC);
            oprot.writeI64(struct.numNulls);
            oprot.writeFieldEnd();
            oprot.writeFieldStop();
            oprot.writeStructEnd();
        }
    }
    
    private static class BinaryColumnStatsDataTupleSchemeFactory implements SchemeFactory
    {
        @Override
        public BinaryColumnStatsDataTupleScheme getScheme() {
            return new BinaryColumnStatsDataTupleScheme();
        }
    }
    
    private static class BinaryColumnStatsDataTupleScheme extends TupleScheme<BinaryColumnStatsData>
    {
        @Override
        public void write(final TProtocol prot, final BinaryColumnStatsData struct) throws TException {
            final TTupleProtocol oprot = (TTupleProtocol)prot;
            oprot.writeI64(struct.maxColLen);
            oprot.writeDouble(struct.avgColLen);
            oprot.writeI64(struct.numNulls);
        }
        
        @Override
        public void read(final TProtocol prot, final BinaryColumnStatsData struct) throws TException {
            final TTupleProtocol iprot = (TTupleProtocol)prot;
            struct.maxColLen = iprot.readI64();
            struct.setMaxColLenIsSet(true);
            struct.avgColLen = iprot.readDouble();
            struct.setAvgColLenIsSet(true);
            struct.numNulls = iprot.readI64();
            struct.setNumNullsIsSet(true);
        }
    }
}
