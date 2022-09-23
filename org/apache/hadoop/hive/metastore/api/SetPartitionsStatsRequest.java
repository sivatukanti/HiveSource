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

public class SetPartitionsStatsRequest implements TBase<SetPartitionsStatsRequest, _Fields>, Serializable, Cloneable
{
    private static final TStruct STRUCT_DESC;
    private static final TField COL_STATS_FIELD_DESC;
    private static final Map<Class<? extends IScheme>, SchemeFactory> schemes;
    private List<ColumnStatistics> colStats;
    public static final Map<_Fields, FieldMetaData> metaDataMap;
    
    public SetPartitionsStatsRequest() {
    }
    
    public SetPartitionsStatsRequest(final List<ColumnStatistics> colStats) {
        this();
        this.colStats = colStats;
    }
    
    public SetPartitionsStatsRequest(final SetPartitionsStatsRequest other) {
        if (other.isSetColStats()) {
            final List<ColumnStatistics> __this__colStats = new ArrayList<ColumnStatistics>();
            for (final ColumnStatistics other_element : other.colStats) {
                __this__colStats.add(new ColumnStatistics(other_element));
            }
            this.colStats = __this__colStats;
        }
    }
    
    @Override
    public SetPartitionsStatsRequest deepCopy() {
        return new SetPartitionsStatsRequest(this);
    }
    
    @Override
    public void clear() {
        this.colStats = null;
    }
    
    public int getColStatsSize() {
        return (this.colStats == null) ? 0 : this.colStats.size();
    }
    
    public Iterator<ColumnStatistics> getColStatsIterator() {
        return (this.colStats == null) ? null : this.colStats.iterator();
    }
    
    public void addToColStats(final ColumnStatistics elem) {
        if (this.colStats == null) {
            this.colStats = new ArrayList<ColumnStatistics>();
        }
        this.colStats.add(elem);
    }
    
    public List<ColumnStatistics> getColStats() {
        return this.colStats;
    }
    
    public void setColStats(final List<ColumnStatistics> colStats) {
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
    
    @Override
    public void setFieldValue(final _Fields field, final Object value) {
        switch (field) {
            case COL_STATS: {
                if (value == null) {
                    this.unsetColStats();
                    break;
                }
                this.setColStats((List<ColumnStatistics>)value);
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
            default: {
                throw new IllegalStateException();
            }
        }
    }
    
    @Override
    public boolean equals(final Object that) {
        return that != null && that instanceof SetPartitionsStatsRequest && this.equals((SetPartitionsStatsRequest)that);
    }
    
    public boolean equals(final SetPartitionsStatsRequest that) {
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
        return builder.toHashCode();
    }
    
    @Override
    public int compareTo(final SetPartitionsStatsRequest other) {
        if (!this.getClass().equals(other.getClass())) {
            return this.getClass().getName().compareTo(other.getClass().getName());
        }
        int lastComparison = 0;
        final SetPartitionsStatsRequest typedOther = other;
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
        return 0;
    }
    
    @Override
    public _Fields fieldForId(final int fieldId) {
        return _Fields.findByThriftId(fieldId);
    }
    
    @Override
    public void read(final TProtocol iprot) throws TException {
        SetPartitionsStatsRequest.schemes.get(iprot.getScheme()).getScheme().read(iprot, this);
    }
    
    @Override
    public void write(final TProtocol oprot) throws TException {
        SetPartitionsStatsRequest.schemes.get(oprot.getScheme()).getScheme().write(oprot, this);
    }
    
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("SetPartitionsStatsRequest(");
        boolean first = true;
        sb.append("colStats:");
        if (this.colStats == null) {
            sb.append("null");
        }
        else {
            sb.append(this.colStats);
        }
        first = false;
        sb.append(")");
        return sb.toString();
    }
    
    public void validate() throws TException {
        if (!this.isSetColStats()) {
            throw new TProtocolException("Required field 'colStats' is unset! Struct:" + this.toString());
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
        STRUCT_DESC = new TStruct("SetPartitionsStatsRequest");
        COL_STATS_FIELD_DESC = new TField("colStats", (byte)15, (short)1);
        (schemes = new HashMap<Class<? extends IScheme>, SchemeFactory>()).put(StandardScheme.class, new SetPartitionsStatsRequestStandardSchemeFactory());
        SetPartitionsStatsRequest.schemes.put(TupleScheme.class, new SetPartitionsStatsRequestTupleSchemeFactory());
        final Map<_Fields, FieldMetaData> tmpMap = new EnumMap<_Fields, FieldMetaData>(_Fields.class);
        tmpMap.put(_Fields.COL_STATS, new FieldMetaData("colStats", (byte)1, new ListMetaData((byte)15, new StructMetaData((byte)12, ColumnStatistics.class))));
        FieldMetaData.addStructMetaDataMap(SetPartitionsStatsRequest.class, metaDataMap = Collections.unmodifiableMap((Map<? extends _Fields, ? extends FieldMetaData>)tmpMap));
    }
    
    public enum _Fields implements TFieldIdEnum
    {
        COL_STATS((short)1, "colStats");
        
        private static final Map<String, _Fields> byName;
        private final short _thriftId;
        private final String _fieldName;
        
        public static _Fields findByThriftId(final int fieldId) {
            switch (fieldId) {
                case 1: {
                    return _Fields.COL_STATS;
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
    
    private static class SetPartitionsStatsRequestStandardSchemeFactory implements SchemeFactory
    {
        @Override
        public SetPartitionsStatsRequestStandardScheme getScheme() {
            return new SetPartitionsStatsRequestStandardScheme();
        }
    }
    
    private static class SetPartitionsStatsRequestStandardScheme extends StandardScheme<SetPartitionsStatsRequest>
    {
        @Override
        public void read(final TProtocol iprot, final SetPartitionsStatsRequest struct) throws TException {
            iprot.readStructBegin();
            while (true) {
                final TField schemeField = iprot.readFieldBegin();
                if (schemeField.type == 0) {
                    break;
                }
                switch (schemeField.id) {
                    case 1: {
                        if (schemeField.type == 15) {
                            final TList _list286 = iprot.readListBegin();
                            struct.colStats = (List<ColumnStatistics>)new ArrayList(_list286.size);
                            for (int _i287 = 0; _i287 < _list286.size; ++_i287) {
                                final ColumnStatistics _elem288 = new ColumnStatistics();
                                _elem288.read(iprot);
                                struct.colStats.add(_elem288);
                            }
                            iprot.readListEnd();
                            struct.setColStatsIsSet(true);
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
        public void write(final TProtocol oprot, final SetPartitionsStatsRequest struct) throws TException {
            struct.validate();
            oprot.writeStructBegin(SetPartitionsStatsRequest.STRUCT_DESC);
            if (struct.colStats != null) {
                oprot.writeFieldBegin(SetPartitionsStatsRequest.COL_STATS_FIELD_DESC);
                oprot.writeListBegin(new TList((byte)12, struct.colStats.size()));
                for (final ColumnStatistics _iter289 : struct.colStats) {
                    _iter289.write(oprot);
                }
                oprot.writeListEnd();
                oprot.writeFieldEnd();
            }
            oprot.writeFieldStop();
            oprot.writeStructEnd();
        }
    }
    
    private static class SetPartitionsStatsRequestTupleSchemeFactory implements SchemeFactory
    {
        @Override
        public SetPartitionsStatsRequestTupleScheme getScheme() {
            return new SetPartitionsStatsRequestTupleScheme();
        }
    }
    
    private static class SetPartitionsStatsRequestTupleScheme extends TupleScheme<SetPartitionsStatsRequest>
    {
        @Override
        public void write(final TProtocol prot, final SetPartitionsStatsRequest struct) throws TException {
            final TTupleProtocol oprot = (TTupleProtocol)prot;
            oprot.writeI32(struct.colStats.size());
            for (final ColumnStatistics _iter290 : struct.colStats) {
                _iter290.write(oprot);
            }
        }
        
        @Override
        public void read(final TProtocol prot, final SetPartitionsStatsRequest struct) throws TException {
            final TTupleProtocol iprot = (TTupleProtocol)prot;
            final TList _list291 = new TList((byte)12, iprot.readI32());
            struct.colStats = (List<ColumnStatistics>)new ArrayList(_list291.size);
            for (int _i292 = 0; _i292 < _list291.size; ++_i292) {
                final ColumnStatistics _elem293 = new ColumnStatistics();
                _elem293.read(iprot);
                struct.colStats.add(_elem293);
            }
            struct.setColStatsIsSet(true);
        }
    }
}
