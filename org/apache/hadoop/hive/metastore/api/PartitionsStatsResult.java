// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.hive.metastore.api;

import org.apache.thrift.protocol.TTupleProtocol;
import org.apache.thrift.protocol.TList;
import org.apache.thrift.protocol.TMap;
import org.apache.thrift.protocol.TProtocolUtil;
import java.util.EnumSet;
import java.util.Collections;
import org.apache.thrift.meta_data.MapMetaData;
import org.apache.thrift.meta_data.ListMetaData;
import org.apache.thrift.meta_data.StructMetaData;
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
import java.util.ArrayList;
import java.util.HashMap;
import org.apache.thrift.meta_data.FieldMetaData;
import java.util.List;
import org.apache.thrift.scheme.SchemeFactory;
import org.apache.thrift.scheme.IScheme;
import java.util.Map;
import org.apache.thrift.protocol.TField;
import org.apache.thrift.protocol.TStruct;
import java.io.Serializable;
import org.apache.thrift.TBase;

public class PartitionsStatsResult implements TBase<PartitionsStatsResult, _Fields>, Serializable, Cloneable
{
    private static final TStruct STRUCT_DESC;
    private static final TField PART_STATS_FIELD_DESC;
    private static final Map<Class<? extends IScheme>, SchemeFactory> schemes;
    private Map<String, List<ColumnStatisticsObj>> partStats;
    public static final Map<_Fields, FieldMetaData> metaDataMap;
    
    public PartitionsStatsResult() {
    }
    
    public PartitionsStatsResult(final Map<String, List<ColumnStatisticsObj>> partStats) {
        this();
        this.partStats = partStats;
    }
    
    public PartitionsStatsResult(final PartitionsStatsResult other) {
        if (other.isSetPartStats()) {
            final Map<String, List<ColumnStatisticsObj>> __this__partStats = new HashMap<String, List<ColumnStatisticsObj>>();
            for (final Map.Entry<String, List<ColumnStatisticsObj>> other_element : other.partStats.entrySet()) {
                final String other_element_key = other_element.getKey();
                final List<ColumnStatisticsObj> other_element_value = other_element.getValue();
                final String __this__partStats_copy_key = other_element_key;
                final List<ColumnStatisticsObj> __this__partStats_copy_value = new ArrayList<ColumnStatisticsObj>();
                for (final ColumnStatisticsObj other_element_value_element : other_element_value) {
                    __this__partStats_copy_value.add(new ColumnStatisticsObj(other_element_value_element));
                }
                __this__partStats.put(__this__partStats_copy_key, __this__partStats_copy_value);
            }
            this.partStats = __this__partStats;
        }
    }
    
    @Override
    public PartitionsStatsResult deepCopy() {
        return new PartitionsStatsResult(this);
    }
    
    @Override
    public void clear() {
        this.partStats = null;
    }
    
    public int getPartStatsSize() {
        return (this.partStats == null) ? 0 : this.partStats.size();
    }
    
    public void putToPartStats(final String key, final List<ColumnStatisticsObj> val) {
        if (this.partStats == null) {
            this.partStats = new HashMap<String, List<ColumnStatisticsObj>>();
        }
        this.partStats.put(key, val);
    }
    
    public Map<String, List<ColumnStatisticsObj>> getPartStats() {
        return this.partStats;
    }
    
    public void setPartStats(final Map<String, List<ColumnStatisticsObj>> partStats) {
        this.partStats = partStats;
    }
    
    public void unsetPartStats() {
        this.partStats = null;
    }
    
    public boolean isSetPartStats() {
        return this.partStats != null;
    }
    
    public void setPartStatsIsSet(final boolean value) {
        if (!value) {
            this.partStats = null;
        }
    }
    
    @Override
    public void setFieldValue(final _Fields field, final Object value) {
        switch (field) {
            case PART_STATS: {
                if (value == null) {
                    this.unsetPartStats();
                    break;
                }
                this.setPartStats((Map<String, List<ColumnStatisticsObj>>)value);
                break;
            }
        }
    }
    
    @Override
    public Object getFieldValue(final _Fields field) {
        switch (field) {
            case PART_STATS: {
                return this.getPartStats();
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
            case PART_STATS: {
                return this.isSetPartStats();
            }
            default: {
                throw new IllegalStateException();
            }
        }
    }
    
    @Override
    public boolean equals(final Object that) {
        return that != null && that instanceof PartitionsStatsResult && this.equals((PartitionsStatsResult)that);
    }
    
    public boolean equals(final PartitionsStatsResult that) {
        if (that == null) {
            return false;
        }
        final boolean this_present_partStats = this.isSetPartStats();
        final boolean that_present_partStats = that.isSetPartStats();
        if (this_present_partStats || that_present_partStats) {
            if (!this_present_partStats || !that_present_partStats) {
                return false;
            }
            if (!this.partStats.equals(that.partStats)) {
                return false;
            }
        }
        return true;
    }
    
    @Override
    public int hashCode() {
        final HashCodeBuilder builder = new HashCodeBuilder();
        final boolean present_partStats = this.isSetPartStats();
        builder.append(present_partStats);
        if (present_partStats) {
            builder.append(this.partStats);
        }
        return builder.toHashCode();
    }
    
    @Override
    public int compareTo(final PartitionsStatsResult other) {
        if (!this.getClass().equals(other.getClass())) {
            return this.getClass().getName().compareTo(other.getClass().getName());
        }
        int lastComparison = 0;
        final PartitionsStatsResult typedOther = other;
        lastComparison = Boolean.valueOf(this.isSetPartStats()).compareTo(Boolean.valueOf(typedOther.isSetPartStats()));
        if (lastComparison != 0) {
            return lastComparison;
        }
        if (this.isSetPartStats()) {
            lastComparison = TBaseHelper.compareTo(this.partStats, typedOther.partStats);
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
        PartitionsStatsResult.schemes.get(iprot.getScheme()).getScheme().read(iprot, this);
    }
    
    @Override
    public void write(final TProtocol oprot) throws TException {
        PartitionsStatsResult.schemes.get(oprot.getScheme()).getScheme().write(oprot, this);
    }
    
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("PartitionsStatsResult(");
        boolean first = true;
        sb.append("partStats:");
        if (this.partStats == null) {
            sb.append("null");
        }
        else {
            sb.append(this.partStats);
        }
        first = false;
        sb.append(")");
        return sb.toString();
    }
    
    public void validate() throws TException {
        if (!this.isSetPartStats()) {
            throw new TProtocolException("Required field 'partStats' is unset! Struct:" + this.toString());
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
        STRUCT_DESC = new TStruct("PartitionsStatsResult");
        PART_STATS_FIELD_DESC = new TField("partStats", (byte)13, (short)1);
        (schemes = new HashMap<Class<? extends IScheme>, SchemeFactory>()).put(StandardScheme.class, new PartitionsStatsResultStandardSchemeFactory());
        PartitionsStatsResult.schemes.put(TupleScheme.class, new PartitionsStatsResultTupleSchemeFactory());
        final Map<_Fields, FieldMetaData> tmpMap = new EnumMap<_Fields, FieldMetaData>(_Fields.class);
        tmpMap.put(_Fields.PART_STATS, new FieldMetaData("partStats", (byte)1, new MapMetaData((byte)13, new FieldValueMetaData((byte)11), new ListMetaData((byte)15, new StructMetaData((byte)12, ColumnStatisticsObj.class)))));
        FieldMetaData.addStructMetaDataMap(PartitionsStatsResult.class, metaDataMap = Collections.unmodifiableMap((Map<? extends _Fields, ? extends FieldMetaData>)tmpMap));
    }
    
    public enum _Fields implements TFieldIdEnum
    {
        PART_STATS((short)1, "partStats");
        
        private static final Map<String, _Fields> byName;
        private final short _thriftId;
        private final String _fieldName;
        
        public static _Fields findByThriftId(final int fieldId) {
            switch (fieldId) {
                case 1: {
                    return _Fields.PART_STATS;
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
    
    private static class PartitionsStatsResultStandardSchemeFactory implements SchemeFactory
    {
        @Override
        public PartitionsStatsResultStandardScheme getScheme() {
            return new PartitionsStatsResultStandardScheme();
        }
    }
    
    private static class PartitionsStatsResultStandardScheme extends StandardScheme<PartitionsStatsResult>
    {
        @Override
        public void read(final TProtocol iprot, final PartitionsStatsResult struct) throws TException {
            iprot.readStructBegin();
            while (true) {
                final TField schemeField = iprot.readFieldBegin();
                if (schemeField.type == 0) {
                    break;
                }
                switch (schemeField.id) {
                    case 1: {
                        if (schemeField.type == 13) {
                            final TMap _map338 = iprot.readMapBegin();
                            struct.partStats = (Map<String, List<ColumnStatisticsObj>>)new HashMap(2 * _map338.size);
                            for (int _i339 = 0; _i339 < _map338.size; ++_i339) {
                                final String _key340 = iprot.readString();
                                final TList _list342 = iprot.readListBegin();
                                final List<ColumnStatisticsObj> _val341 = new ArrayList<ColumnStatisticsObj>(_list342.size);
                                for (int _i340 = 0; _i340 < _list342.size; ++_i340) {
                                    final ColumnStatisticsObj _elem344 = new ColumnStatisticsObj();
                                    _elem344.read(iprot);
                                    _val341.add(_elem344);
                                }
                                iprot.readListEnd();
                                struct.partStats.put(_key340, _val341);
                            }
                            iprot.readMapEnd();
                            struct.setPartStatsIsSet(true);
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
        public void write(final TProtocol oprot, final PartitionsStatsResult struct) throws TException {
            struct.validate();
            oprot.writeStructBegin(PartitionsStatsResult.STRUCT_DESC);
            if (struct.partStats != null) {
                oprot.writeFieldBegin(PartitionsStatsResult.PART_STATS_FIELD_DESC);
                oprot.writeMapBegin(new TMap((byte)11, (byte)15, struct.partStats.size()));
                for (final Map.Entry<String, List<ColumnStatisticsObj>> _iter345 : struct.partStats.entrySet()) {
                    oprot.writeString(_iter345.getKey());
                    oprot.writeListBegin(new TList((byte)12, _iter345.getValue().size()));
                    for (final ColumnStatisticsObj _iter346 : _iter345.getValue()) {
                        _iter346.write(oprot);
                    }
                    oprot.writeListEnd();
                }
                oprot.writeMapEnd();
                oprot.writeFieldEnd();
            }
            oprot.writeFieldStop();
            oprot.writeStructEnd();
        }
    }
    
    private static class PartitionsStatsResultTupleSchemeFactory implements SchemeFactory
    {
        @Override
        public PartitionsStatsResultTupleScheme getScheme() {
            return new PartitionsStatsResultTupleScheme();
        }
    }
    
    private static class PartitionsStatsResultTupleScheme extends TupleScheme<PartitionsStatsResult>
    {
        @Override
        public void write(final TProtocol prot, final PartitionsStatsResult struct) throws TException {
            final TTupleProtocol oprot = (TTupleProtocol)prot;
            oprot.writeI32(struct.partStats.size());
            for (final Map.Entry<String, List<ColumnStatisticsObj>> _iter347 : struct.partStats.entrySet()) {
                oprot.writeString(_iter347.getKey());
                oprot.writeI32(_iter347.getValue().size());
                for (final ColumnStatisticsObj _iter348 : _iter347.getValue()) {
                    _iter348.write(oprot);
                }
            }
        }
        
        @Override
        public void read(final TProtocol prot, final PartitionsStatsResult struct) throws TException {
            final TTupleProtocol iprot = (TTupleProtocol)prot;
            final TMap _map349 = new TMap((byte)11, (byte)15, iprot.readI32());
            struct.partStats = (Map<String, List<ColumnStatisticsObj>>)new HashMap(2 * _map349.size);
            for (int _i350 = 0; _i350 < _map349.size; ++_i350) {
                final String _key351 = iprot.readString();
                final TList _list353 = new TList((byte)12, iprot.readI32());
                final List<ColumnStatisticsObj> _val352 = new ArrayList<ColumnStatisticsObj>(_list353.size);
                for (int _i351 = 0; _i351 < _list353.size; ++_i351) {
                    final ColumnStatisticsObj _elem355 = new ColumnStatisticsObj();
                    _elem355.read(iprot);
                    _val352.add(_elem355);
                }
                struct.partStats.put(_key351, _val352);
            }
            struct.setPartStatsIsSet(true);
        }
    }
}
