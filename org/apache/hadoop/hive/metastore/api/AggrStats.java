// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.hive.metastore.api;

import org.apache.thrift.protocol.TTupleProtocol;
import org.apache.thrift.protocol.TList;
import org.apache.thrift.protocol.TProtocolUtil;
import java.util.EnumSet;
import java.util.Collections;
import org.apache.thrift.meta_data.FieldValueMetaData;
import org.apache.thrift.meta_data.ListMetaData;
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

public class AggrStats implements TBase<AggrStats, _Fields>, Serializable, Cloneable
{
    private static final TStruct STRUCT_DESC;
    private static final TField COL_STATS_FIELD_DESC;
    private static final TField PARTS_FOUND_FIELD_DESC;
    private static final Map<Class<? extends IScheme>, SchemeFactory> schemes;
    private List<ColumnStatisticsObj> colStats;
    private long partsFound;
    private static final int __PARTSFOUND_ISSET_ID = 0;
    private byte __isset_bitfield;
    public static final Map<_Fields, FieldMetaData> metaDataMap;
    
    public AggrStats() {
        this.__isset_bitfield = 0;
    }
    
    public AggrStats(final List<ColumnStatisticsObj> colStats, final long partsFound) {
        this();
        this.colStats = colStats;
        this.partsFound = partsFound;
        this.setPartsFoundIsSet(true);
    }
    
    public AggrStats(final AggrStats other) {
        this.__isset_bitfield = 0;
        this.__isset_bitfield = other.__isset_bitfield;
        if (other.isSetColStats()) {
            final List<ColumnStatisticsObj> __this__colStats = new ArrayList<ColumnStatisticsObj>();
            for (final ColumnStatisticsObj other_element : other.colStats) {
                __this__colStats.add(new ColumnStatisticsObj(other_element));
            }
            this.colStats = __this__colStats;
        }
        this.partsFound = other.partsFound;
    }
    
    @Override
    public AggrStats deepCopy() {
        return new AggrStats(this);
    }
    
    @Override
    public void clear() {
        this.colStats = null;
        this.setPartsFoundIsSet(false);
        this.partsFound = 0L;
    }
    
    public int getColStatsSize() {
        return (this.colStats == null) ? 0 : this.colStats.size();
    }
    
    public Iterator<ColumnStatisticsObj> getColStatsIterator() {
        return (this.colStats == null) ? null : this.colStats.iterator();
    }
    
    public void addToColStats(final ColumnStatisticsObj elem) {
        if (this.colStats == null) {
            this.colStats = new ArrayList<ColumnStatisticsObj>();
        }
        this.colStats.add(elem);
    }
    
    public List<ColumnStatisticsObj> getColStats() {
        return this.colStats;
    }
    
    public void setColStats(final List<ColumnStatisticsObj> colStats) {
        this.colStats = colStats;
    }
    
    public void unsetColStats() {
        this.colStats = null;
    }
    
    public boolean isSetColStats() {
        return this.colStats != null;
    }
    
    public void setColStatsIsSet(final boolean value) {
        if (!value) {
            this.colStats = null;
        }
    }
    
    public long getPartsFound() {
        return this.partsFound;
    }
    
    public void setPartsFound(final long partsFound) {
        this.partsFound = partsFound;
        this.setPartsFoundIsSet(true);
    }
    
    public void unsetPartsFound() {
        this.__isset_bitfield = EncodingUtils.clearBit(this.__isset_bitfield, 0);
    }
    
    public boolean isSetPartsFound() {
        return EncodingUtils.testBit(this.__isset_bitfield, 0);
    }
    
    public void setPartsFoundIsSet(final boolean value) {
        this.__isset_bitfield = EncodingUtils.setBit(this.__isset_bitfield, 0, value);
    }
    
    @Override
    public void setFieldValue(final _Fields field, final Object value) {
        switch (field) {
            case COL_STATS: {
                if (value == null) {
                    this.unsetColStats();
                    break;
                }
                this.setColStats((List<ColumnStatisticsObj>)value);
                break;
            }
            case PARTS_FOUND: {
                if (value == null) {
                    this.unsetPartsFound();
                    break;
                }
                this.setPartsFound((long)value);
                break;
            }
        }
    }
    
    @Override
    public Object getFieldValue(final _Fields field) {
        switch (field) {
            case COL_STATS: {
                return this.getColStats();
            }
            case PARTS_FOUND: {
                return this.getPartsFound();
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
            case COL_STATS: {
                return this.isSetColStats();
            }
            case PARTS_FOUND: {
                return this.isSetPartsFound();
            }
            default: {
                throw new IllegalStateException();
            }
        }
    }
    
    @Override
    public boolean equals(final Object that) {
        return that != null && that instanceof AggrStats && this.equals((AggrStats)that);
    }
    
    public boolean equals(final AggrStats that) {
        if (that == null) {
            return false;
        }
        final boolean this_present_colStats = this.isSetColStats();
        final boolean that_present_colStats = that.isSetColStats();
        if (this_present_colStats || that_present_colStats) {
            if (!this_present_colStats || !that_present_colStats) {
                return false;
            }
            if (!this.colStats.equals(that.colStats)) {
                return false;
            }
        }
        final boolean this_present_partsFound = true;
        final boolean that_present_partsFound = true;
        if (this_present_partsFound || that_present_partsFound) {
            if (!this_present_partsFound || !that_present_partsFound) {
                return false;
            }
            if (this.partsFound != that.partsFound) {
                return false;
            }
        }
        return true;
    }
    
    @Override
    public int hashCode() {
        final HashCodeBuilder builder = new HashCodeBuilder();
        final boolean present_colStats = this.isSetColStats();
        builder.append(present_colStats);
        if (present_colStats) {
            builder.append(this.colStats);
        }
        final boolean present_partsFound = true;
        builder.append(present_partsFound);
        if (present_partsFound) {
            builder.append(this.partsFound);
        }
        return builder.toHashCode();
    }
    
    @Override
    public int compareTo(final AggrStats other) {
        if (!this.getClass().equals(other.getClass())) {
            return this.getClass().getName().compareTo(other.getClass().getName());
        }
        int lastComparison = 0;
        final AggrStats typedOther = other;
        lastComparison = Boolean.valueOf(this.isSetColStats()).compareTo(Boolean.valueOf(typedOther.isSetColStats()));
        if (lastComparison != 0) {
            return lastComparison;
        }
        if (this.isSetColStats()) {
            lastComparison = TBaseHelper.compareTo(this.colStats, typedOther.colStats);
            if (lastComparison != 0) {
                return lastComparison;
            }
        }
        lastComparison = Boolean.valueOf(this.isSetPartsFound()).compareTo(Boolean.valueOf(typedOther.isSetPartsFound()));
        if (lastComparison != 0) {
            return lastComparison;
        }
        if (this.isSetPartsFound()) {
            lastComparison = TBaseHelper.compareTo(this.partsFound, typedOther.partsFound);
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
        AggrStats.schemes.get(iprot.getScheme()).getScheme().read(iprot, this);
    }
    
    @Override
    public void write(final TProtocol oprot) throws TException {
        AggrStats.schemes.get(oprot.getScheme()).getScheme().write(oprot, this);
    }
    
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("AggrStats(");
        boolean first = true;
        sb.append("colStats:");
        if (this.colStats == null) {
            sb.append("null");
        }
        else {
            sb.append(this.colStats);
        }
        first = false;
        if (!first) {
            sb.append(", ");
        }
        sb.append("partsFound:");
        sb.append(this.partsFound);
        first = false;
        sb.append(")");
        return sb.toString();
    }
    
    public void validate() throws TException {
        if (!this.isSetColStats()) {
            throw new TProtocolException("Required field 'colStats' is unset! Struct:" + this.toString());
        }
        if (!this.isSetPartsFound()) {
            throw new TProtocolException("Required field 'partsFound' is unset! Struct:" + this.toString());
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
        STRUCT_DESC = new TStruct("AggrStats");
        COL_STATS_FIELD_DESC = new TField("colStats", (byte)15, (short)1);
        PARTS_FOUND_FIELD_DESC = new TField("partsFound", (byte)10, (short)2);
        (schemes = new HashMap<Class<? extends IScheme>, SchemeFactory>()).put(StandardScheme.class, new AggrStatsStandardSchemeFactory());
        AggrStats.schemes.put(TupleScheme.class, new AggrStatsTupleSchemeFactory());
        final Map<_Fields, FieldMetaData> tmpMap = new EnumMap<_Fields, FieldMetaData>(_Fields.class);
        tmpMap.put(_Fields.COL_STATS, new FieldMetaData("colStats", (byte)1, new ListMetaData((byte)15, new StructMetaData((byte)12, ColumnStatisticsObj.class))));
        tmpMap.put(_Fields.PARTS_FOUND, new FieldMetaData("partsFound", (byte)1, new FieldValueMetaData((byte)10)));
        FieldMetaData.addStructMetaDataMap(AggrStats.class, metaDataMap = Collections.unmodifiableMap((Map<? extends _Fields, ? extends FieldMetaData>)tmpMap));
    }
    
    public enum _Fields implements TFieldIdEnum
    {
        COL_STATS((short)1, "colStats"), 
        PARTS_FOUND((short)2, "partsFound");
        
        private static final Map<String, _Fields> byName;
        private final short _thriftId;
        private final String _fieldName;
        
        public static _Fields findByThriftId(final int fieldId) {
            switch (fieldId) {
                case 1: {
                    return _Fields.COL_STATS;
                }
                case 2: {
                    return _Fields.PARTS_FOUND;
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
    
    private static class AggrStatsStandardSchemeFactory implements SchemeFactory
    {
        @Override
        public AggrStatsStandardScheme getScheme() {
            return new AggrStatsStandardScheme();
        }
    }
    
    private static class AggrStatsStandardScheme extends StandardScheme<AggrStats>
    {
        @Override
        public void read(final TProtocol iprot, final AggrStats struct) throws TException {
            iprot.readStructBegin();
            while (true) {
                final TField schemeField = iprot.readFieldBegin();
                if (schemeField.type == 0) {
                    break;
                }
                switch (schemeField.id) {
                    case 1: {
                        if (schemeField.type == 15) {
                            final TList _list278 = iprot.readListBegin();
                            struct.colStats = (List<ColumnStatisticsObj>)new ArrayList(_list278.size);
                            for (int _i279 = 0; _i279 < _list278.size; ++_i279) {
                                final ColumnStatisticsObj _elem280 = new ColumnStatisticsObj();
                                _elem280.read(iprot);
                                struct.colStats.add(_elem280);
                            }
                            iprot.readListEnd();
                            struct.setColStatsIsSet(true);
                            break;
                        }
                        TProtocolUtil.skip(iprot, schemeField.type);
                        break;
                    }
                    case 2: {
                        if (schemeField.type == 10) {
                            struct.partsFound = iprot.readI64();
                            struct.setPartsFoundIsSet(true);
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
        public void write(final TProtocol oprot, final AggrStats struct) throws TException {
            struct.validate();
            oprot.writeStructBegin(AggrStats.STRUCT_DESC);
            if (struct.colStats != null) {
                oprot.writeFieldBegin(AggrStats.COL_STATS_FIELD_DESC);
                oprot.writeListBegin(new TList((byte)12, struct.colStats.size()));
                for (final ColumnStatisticsObj _iter281 : struct.colStats) {
                    _iter281.write(oprot);
                }
                oprot.writeListEnd();
                oprot.writeFieldEnd();
            }
            oprot.writeFieldBegin(AggrStats.PARTS_FOUND_FIELD_DESC);
            oprot.writeI64(struct.partsFound);
            oprot.writeFieldEnd();
            oprot.writeFieldStop();
            oprot.writeStructEnd();
        }
    }
    
    private static class AggrStatsTupleSchemeFactory implements SchemeFactory
    {
        @Override
        public AggrStatsTupleScheme getScheme() {
            return new AggrStatsTupleScheme();
        }
    }
    
    private static class AggrStatsTupleScheme extends TupleScheme<AggrStats>
    {
        @Override
        public void write(final TProtocol prot, final AggrStats struct) throws TException {
            final TTupleProtocol oprot = (TTupleProtocol)prot;
            oprot.writeI32(struct.colStats.size());
            for (final ColumnStatisticsObj _iter282 : struct.colStats) {
                _iter282.write(oprot);
            }
            oprot.writeI64(struct.partsFound);
        }
        
        @Override
        public void read(final TProtocol prot, final AggrStats struct) throws TException {
            final TTupleProtocol iprot = (TTupleProtocol)prot;
            final TList _list283 = new TList((byte)12, iprot.readI32());
            struct.colStats = (List<ColumnStatisticsObj>)new ArrayList(_list283.size);
            for (int _i284 = 0; _i284 < _list283.size; ++_i284) {
                final ColumnStatisticsObj _elem285 = new ColumnStatisticsObj();
                _elem285.read(iprot);
                struct.colStats.add(_elem285);
            }
            struct.setColStatsIsSet(true);
            struct.partsFound = iprot.readI64();
            struct.setPartsFoundIsSet(true);
        }
    }
}
