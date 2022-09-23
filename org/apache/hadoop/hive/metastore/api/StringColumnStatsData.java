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

public class StringColumnStatsData implements TBase<StringColumnStatsData, _Fields>, Serializable, Cloneable
{
    private static final TStruct STRUCT_DESC;
    private static final TField MAX_COL_LEN_FIELD_DESC;
    private static final TField AVG_COL_LEN_FIELD_DESC;
    private static final TField NUM_NULLS_FIELD_DESC;
    private static final TField NUM_DVS_FIELD_DESC;
    private static final Map<Class<? extends IScheme>, SchemeFactory> schemes;
    private long maxColLen;
    private double avgColLen;
    private long numNulls;
    private long numDVs;
    private static final int __MAXCOLLEN_ISSET_ID = 0;
    private static final int __AVGCOLLEN_ISSET_ID = 1;
    private static final int __NUMNULLS_ISSET_ID = 2;
    private static final int __NUMDVS_ISSET_ID = 3;
    private byte __isset_bitfield;
    public static final Map<_Fields, FieldMetaData> metaDataMap;
    
    public StringColumnStatsData() {
        this.__isset_bitfield = 0;
    }
    
    public StringColumnStatsData(final long maxColLen, final double avgColLen, final long numNulls, final long numDVs) {
        this();
        this.maxColLen = maxColLen;
        this.setMaxColLenIsSet(true);
        this.avgColLen = avgColLen;
        this.setAvgColLenIsSet(true);
        this.numNulls = numNulls;
        this.setNumNullsIsSet(true);
        this.numDVs = numDVs;
        this.setNumDVsIsSet(true);
    }
    
    public StringColumnStatsData(final StringColumnStatsData other) {
        this.__isset_bitfield = 0;
        this.__isset_bitfield = other.__isset_bitfield;
        this.maxColLen = other.maxColLen;
        this.avgColLen = other.avgColLen;
        this.numNulls = other.numNulls;
        this.numDVs = other.numDVs;
    }
    
    @Override
    public StringColumnStatsData deepCopy() {
        return new StringColumnStatsData(this);
    }
    
    @Override
    public void clear() {
        this.setMaxColLenIsSet(false);
        this.maxColLen = 0L;
        this.setAvgColLenIsSet(false);
        this.avgColLen = 0.0;
        this.setNumNullsIsSet(false);
        this.numNulls = 0L;
        this.setNumDVsIsSet(false);
        this.numDVs = 0L;
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
    
    public long getNumDVs() {
        return this.numDVs;
    }
    
    public void setNumDVs(final long numDVs) {
        this.numDVs = numDVs;
        this.setNumDVsIsSet(true);
    }
    
    public void unsetNumDVs() {
        this.__isset_bitfield = EncodingUtils.clearBit(this.__isset_bitfield, 3);
    }
    
    public boolean isSetNumDVs() {
        return EncodingUtils.testBit(this.__isset_bitfield, 3);
    }
    
    public void setNumDVsIsSet(final boolean value) {
        this.__isset_bitfield = EncodingUtils.setBit(this.__isset_bitfield, 3, value);
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
            case MAX_COL_LEN: {
                return this.getMaxColLen();
            }
            case AVG_COL_LEN: {
                return this.getAvgColLen();
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
            case MAX_COL_LEN: {
                return this.isSetMaxColLen();
            }
            case AVG_COL_LEN: {
                return this.isSetAvgColLen();
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
        return that != null && that instanceof StringColumnStatsData && this.equals((StringColumnStatsData)that);
    }
    
    public boolean equals(final StringColumnStatsData that) {
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
        final boolean present_numDVs = true;
        builder.append(present_numDVs);
        if (present_numDVs) {
            builder.append(this.numDVs);
        }
        return builder.toHashCode();
    }
    
    @Override
    public int compareTo(final StringColumnStatsData other) {
        if (!this.getClass().equals(other.getClass())) {
            return this.getClass().getName().compareTo(other.getClass().getName());
        }
        int lastComparison = 0;
        final StringColumnStatsData typedOther = other;
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
        StringColumnStatsData.schemes.get(iprot.getScheme()).getScheme().read(iprot, this);
    }
    
    @Override
    public void write(final TProtocol oprot) throws TException {
        StringColumnStatsData.schemes.get(oprot.getScheme()).getScheme().write(oprot, this);
    }
    
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("StringColumnStatsData(");
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
        if (!this.isSetMaxColLen()) {
            throw new TProtocolException("Required field 'maxColLen' is unset! Struct:" + this.toString());
        }
        if (!this.isSetAvgColLen()) {
            throw new TProtocolException("Required field 'avgColLen' is unset! Struct:" + this.toString());
        }
        if (!this.isSetNumNulls()) {
            throw new TProtocolException("Required field 'numNulls' is unset! Struct:" + this.toString());
        }
        if (!this.isSetNumDVs()) {
            throw new TProtocolException("Required field 'numDVs' is unset! Struct:" + this.toString());
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
        STRUCT_DESC = new TStruct("StringColumnStatsData");
        MAX_COL_LEN_FIELD_DESC = new TField("maxColLen", (byte)10, (short)1);
        AVG_COL_LEN_FIELD_DESC = new TField("avgColLen", (byte)4, (short)2);
        NUM_NULLS_FIELD_DESC = new TField("numNulls", (byte)10, (short)3);
        NUM_DVS_FIELD_DESC = new TField("numDVs", (byte)10, (short)4);
        (schemes = new HashMap<Class<? extends IScheme>, SchemeFactory>()).put(StandardScheme.class, new StringColumnStatsDataStandardSchemeFactory());
        StringColumnStatsData.schemes.put(TupleScheme.class, new StringColumnStatsDataTupleSchemeFactory());
        final Map<_Fields, FieldMetaData> tmpMap = new EnumMap<_Fields, FieldMetaData>(_Fields.class);
        tmpMap.put(_Fields.MAX_COL_LEN, new FieldMetaData("maxColLen", (byte)1, new FieldValueMetaData((byte)10)));
        tmpMap.put(_Fields.AVG_COL_LEN, new FieldMetaData("avgColLen", (byte)1, new FieldValueMetaData((byte)4)));
        tmpMap.put(_Fields.NUM_NULLS, new FieldMetaData("numNulls", (byte)1, new FieldValueMetaData((byte)10)));
        tmpMap.put(_Fields.NUM_DVS, new FieldMetaData("numDVs", (byte)1, new FieldValueMetaData((byte)10)));
        FieldMetaData.addStructMetaDataMap(StringColumnStatsData.class, metaDataMap = Collections.unmodifiableMap((Map<? extends _Fields, ? extends FieldMetaData>)tmpMap));
    }
    
    public enum _Fields implements TFieldIdEnum
    {
        MAX_COL_LEN((short)1, "maxColLen"), 
        AVG_COL_LEN((short)2, "avgColLen"), 
        NUM_NULLS((short)3, "numNulls"), 
        NUM_DVS((short)4, "numDVs");
        
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
    
    private static class StringColumnStatsDataStandardSchemeFactory implements SchemeFactory
    {
        @Override
        public StringColumnStatsDataStandardScheme getScheme() {
            return new StringColumnStatsDataStandardScheme();
        }
    }
    
    private static class StringColumnStatsDataStandardScheme extends StandardScheme<StringColumnStatsData>
    {
        @Override
        public void read(final TProtocol iprot, final StringColumnStatsData struct) throws TException {
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
        public void write(final TProtocol oprot, final StringColumnStatsData struct) throws TException {
            struct.validate();
            oprot.writeStructBegin(StringColumnStatsData.STRUCT_DESC);
            oprot.writeFieldBegin(StringColumnStatsData.MAX_COL_LEN_FIELD_DESC);
            oprot.writeI64(struct.maxColLen);
            oprot.writeFieldEnd();
            oprot.writeFieldBegin(StringColumnStatsData.AVG_COL_LEN_FIELD_DESC);
            oprot.writeDouble(struct.avgColLen);
            oprot.writeFieldEnd();
            oprot.writeFieldBegin(StringColumnStatsData.NUM_NULLS_FIELD_DESC);
            oprot.writeI64(struct.numNulls);
            oprot.writeFieldEnd();
            oprot.writeFieldBegin(StringColumnStatsData.NUM_DVS_FIELD_DESC);
            oprot.writeI64(struct.numDVs);
            oprot.writeFieldEnd();
            oprot.writeFieldStop();
            oprot.writeStructEnd();
        }
    }
    
    private static class StringColumnStatsDataTupleSchemeFactory implements SchemeFactory
    {
        @Override
        public StringColumnStatsDataTupleScheme getScheme() {
            return new StringColumnStatsDataTupleScheme();
        }
    }
    
    private static class StringColumnStatsDataTupleScheme extends TupleScheme<StringColumnStatsData>
    {
        @Override
        public void write(final TProtocol prot, final StringColumnStatsData struct) throws TException {
            final TTupleProtocol oprot = (TTupleProtocol)prot;
            oprot.writeI64(struct.maxColLen);
            oprot.writeDouble(struct.avgColLen);
            oprot.writeI64(struct.numNulls);
            oprot.writeI64(struct.numDVs);
        }
        
        @Override
        public void read(final TProtocol prot, final StringColumnStatsData struct) throws TException {
            final TTupleProtocol iprot = (TTupleProtocol)prot;
            struct.maxColLen = iprot.readI64();
            struct.setMaxColLenIsSet(true);
            struct.avgColLen = iprot.readDouble();
            struct.setAvgColLenIsSet(true);
            struct.numNulls = iprot.readI64();
            struct.setNumNullsIsSet(true);
            struct.numDVs = iprot.readI64();
            struct.setNumDVsIsSet(true);
        }
    }
}
