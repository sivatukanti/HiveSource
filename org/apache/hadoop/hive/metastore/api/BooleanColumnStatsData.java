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

public class BooleanColumnStatsData implements TBase<BooleanColumnStatsData, _Fields>, Serializable, Cloneable
{
    private static final TStruct STRUCT_DESC;
    private static final TField NUM_TRUES_FIELD_DESC;
    private static final TField NUM_FALSES_FIELD_DESC;
    private static final TField NUM_NULLS_FIELD_DESC;
    private static final Map<Class<? extends IScheme>, SchemeFactory> schemes;
    private long numTrues;
    private long numFalses;
    private long numNulls;
    private static final int __NUMTRUES_ISSET_ID = 0;
    private static final int __NUMFALSES_ISSET_ID = 1;
    private static final int __NUMNULLS_ISSET_ID = 2;
    private byte __isset_bitfield;
    public static final Map<_Fields, FieldMetaData> metaDataMap;
    
    public BooleanColumnStatsData() {
        this.__isset_bitfield = 0;
    }
    
    public BooleanColumnStatsData(final long numTrues, final long numFalses, final long numNulls) {
        this();
        this.numTrues = numTrues;
        this.setNumTruesIsSet(true);
        this.numFalses = numFalses;
        this.setNumFalsesIsSet(true);
        this.numNulls = numNulls;
        this.setNumNullsIsSet(true);
    }
    
    public BooleanColumnStatsData(final BooleanColumnStatsData other) {
        this.__isset_bitfield = 0;
        this.__isset_bitfield = other.__isset_bitfield;
        this.numTrues = other.numTrues;
        this.numFalses = other.numFalses;
        this.numNulls = other.numNulls;
    }
    
    @Override
    public BooleanColumnStatsData deepCopy() {
        return new BooleanColumnStatsData(this);
    }
    
    @Override
    public void clear() {
        this.setNumTruesIsSet(false);
        this.numTrues = 0L;
        this.setNumFalsesIsSet(false);
        this.numFalses = 0L;
        this.setNumNullsIsSet(false);
        this.numNulls = 0L;
    }
    
    public long getNumTrues() {
        return this.numTrues;
    }
    
    public void setNumTrues(final long numTrues) {
        this.numTrues = numTrues;
        this.setNumTruesIsSet(true);
    }
    
    public void unsetNumTrues() {
        this.__isset_bitfield = EncodingUtils.clearBit(this.__isset_bitfield, 0);
    }
    
    public boolean isSetNumTrues() {
        return EncodingUtils.testBit(this.__isset_bitfield, 0);
    }
    
    public void setNumTruesIsSet(final boolean value) {
        this.__isset_bitfield = EncodingUtils.setBit(this.__isset_bitfield, 0, value);
    }
    
    public long getNumFalses() {
        return this.numFalses;
    }
    
    public void setNumFalses(final long numFalses) {
        this.numFalses = numFalses;
        this.setNumFalsesIsSet(true);
    }
    
    public void unsetNumFalses() {
        this.__isset_bitfield = EncodingUtils.clearBit(this.__isset_bitfield, 1);
    }
    
    public boolean isSetNumFalses() {
        return EncodingUtils.testBit(this.__isset_bitfield, 1);
    }
    
    public void setNumFalsesIsSet(final boolean value) {
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
            case NUM_TRUES: {
                if (value == null) {
                    this.unsetNumTrues();
                    break;
                }
                this.setNumTrues((long)value);
                break;
            }
            case NUM_FALSES: {
                if (value == null) {
                    this.unsetNumFalses();
                    break;
                }
                this.setNumFalses((long)value);
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
            case NUM_TRUES: {
                return this.getNumTrues();
            }
            case NUM_FALSES: {
                return this.getNumFalses();
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
            case NUM_TRUES: {
                return this.isSetNumTrues();
            }
            case NUM_FALSES: {
                return this.isSetNumFalses();
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
        return that != null && that instanceof BooleanColumnStatsData && this.equals((BooleanColumnStatsData)that);
    }
    
    public boolean equals(final BooleanColumnStatsData that) {
        if (that == null) {
            return false;
        }
        final boolean this_present_numTrues = true;
        final boolean that_present_numTrues = true;
        if (this_present_numTrues || that_present_numTrues) {
            if (!this_present_numTrues || !that_present_numTrues) {
                return false;
            }
            if (this.numTrues != that.numTrues) {
                return false;
            }
        }
        final boolean this_present_numFalses = true;
        final boolean that_present_numFalses = true;
        if (this_present_numFalses || that_present_numFalses) {
            if (!this_present_numFalses || !that_present_numFalses) {
                return false;
            }
            if (this.numFalses != that.numFalses) {
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
        final boolean present_numTrues = true;
        builder.append(present_numTrues);
        if (present_numTrues) {
            builder.append(this.numTrues);
        }
        final boolean present_numFalses = true;
        builder.append(present_numFalses);
        if (present_numFalses) {
            builder.append(this.numFalses);
        }
        final boolean present_numNulls = true;
        builder.append(present_numNulls);
        if (present_numNulls) {
            builder.append(this.numNulls);
        }
        return builder.toHashCode();
    }
    
    @Override
    public int compareTo(final BooleanColumnStatsData other) {
        if (!this.getClass().equals(other.getClass())) {
            return this.getClass().getName().compareTo(other.getClass().getName());
        }
        int lastComparison = 0;
        final BooleanColumnStatsData typedOther = other;
        lastComparison = Boolean.valueOf(this.isSetNumTrues()).compareTo(Boolean.valueOf(typedOther.isSetNumTrues()));
        if (lastComparison != 0) {
            return lastComparison;
        }
        if (this.isSetNumTrues()) {
            lastComparison = TBaseHelper.compareTo(this.numTrues, typedOther.numTrues);
            if (lastComparison != 0) {
                return lastComparison;
            }
        }
        lastComparison = Boolean.valueOf(this.isSetNumFalses()).compareTo(Boolean.valueOf(typedOther.isSetNumFalses()));
        if (lastComparison != 0) {
            return lastComparison;
        }
        if (this.isSetNumFalses()) {
            lastComparison = TBaseHelper.compareTo(this.numFalses, typedOther.numFalses);
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
        BooleanColumnStatsData.schemes.get(iprot.getScheme()).getScheme().read(iprot, this);
    }
    
    @Override
    public void write(final TProtocol oprot) throws TException {
        BooleanColumnStatsData.schemes.get(oprot.getScheme()).getScheme().write(oprot, this);
    }
    
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("BooleanColumnStatsData(");
        boolean first = true;
        sb.append("numTrues:");
        sb.append(this.numTrues);
        first = false;
        if (!first) {
            sb.append(", ");
        }
        sb.append("numFalses:");
        sb.append(this.numFalses);
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
        if (!this.isSetNumTrues()) {
            throw new TProtocolException("Required field 'numTrues' is unset! Struct:" + this.toString());
        }
        if (!this.isSetNumFalses()) {
            throw new TProtocolException("Required field 'numFalses' is unset! Struct:" + this.toString());
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
        STRUCT_DESC = new TStruct("BooleanColumnStatsData");
        NUM_TRUES_FIELD_DESC = new TField("numTrues", (byte)10, (short)1);
        NUM_FALSES_FIELD_DESC = new TField("numFalses", (byte)10, (short)2);
        NUM_NULLS_FIELD_DESC = new TField("numNulls", (byte)10, (short)3);
        (schemes = new HashMap<Class<? extends IScheme>, SchemeFactory>()).put(StandardScheme.class, new BooleanColumnStatsDataStandardSchemeFactory());
        BooleanColumnStatsData.schemes.put(TupleScheme.class, new BooleanColumnStatsDataTupleSchemeFactory());
        final Map<_Fields, FieldMetaData> tmpMap = new EnumMap<_Fields, FieldMetaData>(_Fields.class);
        tmpMap.put(_Fields.NUM_TRUES, new FieldMetaData("numTrues", (byte)1, new FieldValueMetaData((byte)10)));
        tmpMap.put(_Fields.NUM_FALSES, new FieldMetaData("numFalses", (byte)1, new FieldValueMetaData((byte)10)));
        tmpMap.put(_Fields.NUM_NULLS, new FieldMetaData("numNulls", (byte)1, new FieldValueMetaData((byte)10)));
        FieldMetaData.addStructMetaDataMap(BooleanColumnStatsData.class, metaDataMap = Collections.unmodifiableMap((Map<? extends _Fields, ? extends FieldMetaData>)tmpMap));
    }
    
    public enum _Fields implements TFieldIdEnum
    {
        NUM_TRUES((short)1, "numTrues"), 
        NUM_FALSES((short)2, "numFalses"), 
        NUM_NULLS((short)3, "numNulls");
        
        private static final Map<String, _Fields> byName;
        private final short _thriftId;
        private final String _fieldName;
        
        public static _Fields findByThriftId(final int fieldId) {
            switch (fieldId) {
                case 1: {
                    return _Fields.NUM_TRUES;
                }
                case 2: {
                    return _Fields.NUM_FALSES;
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
    
    private static class BooleanColumnStatsDataStandardSchemeFactory implements SchemeFactory
    {
        @Override
        public BooleanColumnStatsDataStandardScheme getScheme() {
            return new BooleanColumnStatsDataStandardScheme();
        }
    }
    
    private static class BooleanColumnStatsDataStandardScheme extends StandardScheme<BooleanColumnStatsData>
    {
        @Override
        public void read(final TProtocol iprot, final BooleanColumnStatsData struct) throws TException {
            iprot.readStructBegin();
            while (true) {
                final TField schemeField = iprot.readFieldBegin();
                if (schemeField.type == 0) {
                    break;
                }
                switch (schemeField.id) {
                    case 1: {
                        if (schemeField.type == 10) {
                            struct.numTrues = iprot.readI64();
                            struct.setNumTruesIsSet(true);
                            break;
                        }
                        TProtocolUtil.skip(iprot, schemeField.type);
                        break;
                    }
                    case 2: {
                        if (schemeField.type == 10) {
                            struct.numFalses = iprot.readI64();
                            struct.setNumFalsesIsSet(true);
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
        public void write(final TProtocol oprot, final BooleanColumnStatsData struct) throws TException {
            struct.validate();
            oprot.writeStructBegin(BooleanColumnStatsData.STRUCT_DESC);
            oprot.writeFieldBegin(BooleanColumnStatsData.NUM_TRUES_FIELD_DESC);
            oprot.writeI64(struct.numTrues);
            oprot.writeFieldEnd();
            oprot.writeFieldBegin(BooleanColumnStatsData.NUM_FALSES_FIELD_DESC);
            oprot.writeI64(struct.numFalses);
            oprot.writeFieldEnd();
            oprot.writeFieldBegin(BooleanColumnStatsData.NUM_NULLS_FIELD_DESC);
            oprot.writeI64(struct.numNulls);
            oprot.writeFieldEnd();
            oprot.writeFieldStop();
            oprot.writeStructEnd();
        }
    }
    
    private static class BooleanColumnStatsDataTupleSchemeFactory implements SchemeFactory
    {
        @Override
        public BooleanColumnStatsDataTupleScheme getScheme() {
            return new BooleanColumnStatsDataTupleScheme();
        }
    }
    
    private static class BooleanColumnStatsDataTupleScheme extends TupleScheme<BooleanColumnStatsData>
    {
        @Override
        public void write(final TProtocol prot, final BooleanColumnStatsData struct) throws TException {
            final TTupleProtocol oprot = (TTupleProtocol)prot;
            oprot.writeI64(struct.numTrues);
            oprot.writeI64(struct.numFalses);
            oprot.writeI64(struct.numNulls);
        }
        
        @Override
        public void read(final TProtocol prot, final BooleanColumnStatsData struct) throws TException {
            final TTupleProtocol iprot = (TTupleProtocol)prot;
            struct.numTrues = iprot.readI64();
            struct.setNumTruesIsSet(true);
            struct.numFalses = iprot.readI64();
            struct.setNumFalsesIsSet(true);
            struct.numNulls = iprot.readI64();
            struct.setNumNullsIsSet(true);
        }
    }
}
