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

public class TableStatsResult implements TBase<TableStatsResult, _Fields>, Serializable, Cloneable
{
    private static final TStruct STRUCT_DESC;
    private static final TField TABLE_STATS_FIELD_DESC;
    private static final Map<Class<? extends IScheme>, SchemeFactory> schemes;
    private List<ColumnStatisticsObj> tableStats;
    public static final Map<_Fields, FieldMetaData> metaDataMap;
    
    public TableStatsResult() {
    }
    
    public TableStatsResult(final List<ColumnStatisticsObj> tableStats) {
        this();
        this.tableStats = tableStats;
    }
    
    public TableStatsResult(final TableStatsResult other) {
        if (other.isSetTableStats()) {
            final List<ColumnStatisticsObj> __this__tableStats = new ArrayList<ColumnStatisticsObj>();
            for (final ColumnStatisticsObj other_element : other.tableStats) {
                __this__tableStats.add(new ColumnStatisticsObj(other_element));
            }
            this.tableStats = __this__tableStats;
        }
    }
    
    @Override
    public TableStatsResult deepCopy() {
        return new TableStatsResult(this);
    }
    
    @Override
    public void clear() {
        this.tableStats = null;
    }
    
    public int getTableStatsSize() {
        return (this.tableStats == null) ? 0 : this.tableStats.size();
    }
    
    public Iterator<ColumnStatisticsObj> getTableStatsIterator() {
        return (this.tableStats == null) ? null : this.tableStats.iterator();
    }
    
    public void addToTableStats(final ColumnStatisticsObj elem) {
        if (this.tableStats == null) {
            this.tableStats = new ArrayList<ColumnStatisticsObj>();
        }
        this.tableStats.add(elem);
    }
    
    public List<ColumnStatisticsObj> getTableStats() {
        return this.tableStats;
    }
    
    public void setTableStats(final List<ColumnStatisticsObj> tableStats) {
        this.tableStats = tableStats;
    }
    
    public void unsetTableStats() {
        this.tableStats = null;
    }
    
    public boolean isSetTableStats() {
        return this.tableStats != null;
    }
    
    public void setTableStatsIsSet(final boolean value) {
        if (!value) {
            this.tableStats = null;
        }
    }
    
    @Override
    public void setFieldValue(final _Fields field, final Object value) {
        switch (field) {
            case TABLE_STATS: {
                if (value == null) {
                    this.unsetTableStats();
                    break;
                }
                this.setTableStats((List<ColumnStatisticsObj>)value);
                break;
            }
        }
    }
    
    @Override
    public Object getFieldValue(final _Fields field) {
        switch (field) {
            case TABLE_STATS: {
                return this.getTableStats();
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
            case TABLE_STATS: {
                return this.isSetTableStats();
            }
            default: {
                throw new IllegalStateException();
            }
        }
    }
    
    @Override
    public boolean equals(final Object that) {
        return that != null && that instanceof TableStatsResult && this.equals((TableStatsResult)that);
    }
    
    public boolean equals(final TableStatsResult that) {
        if (that == null) {
            return false;
        }
        final boolean this_present_tableStats = this.isSetTableStats();
        final boolean that_present_tableStats = that.isSetTableStats();
        if (this_present_tableStats || that_present_tableStats) {
            if (!this_present_tableStats || !that_present_tableStats) {
                return false;
            }
            if (!this.tableStats.equals(that.tableStats)) {
                return false;
            }
        }
        return true;
    }
    
    @Override
    public int hashCode() {
        final HashCodeBuilder builder = new HashCodeBuilder();
        final boolean present_tableStats = this.isSetTableStats();
        builder.append(present_tableStats);
        if (present_tableStats) {
            builder.append(this.tableStats);
        }
        return builder.toHashCode();
    }
    
    @Override
    public int compareTo(final TableStatsResult other) {
        if (!this.getClass().equals(other.getClass())) {
            return this.getClass().getName().compareTo(other.getClass().getName());
        }
        int lastComparison = 0;
        final TableStatsResult typedOther = other;
        lastComparison = Boolean.valueOf(this.isSetTableStats()).compareTo(Boolean.valueOf(typedOther.isSetTableStats()));
        if (lastComparison != 0) {
            return lastComparison;
        }
        if (this.isSetTableStats()) {
            lastComparison = TBaseHelper.compareTo(this.tableStats, typedOther.tableStats);
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
        TableStatsResult.schemes.get(iprot.getScheme()).getScheme().read(iprot, this);
    }
    
    @Override
    public void write(final TProtocol oprot) throws TException {
        TableStatsResult.schemes.get(oprot.getScheme()).getScheme().write(oprot, this);
    }
    
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("TableStatsResult(");
        boolean first = true;
        sb.append("tableStats:");
        if (this.tableStats == null) {
            sb.append("null");
        }
        else {
            sb.append(this.tableStats);
        }
        first = false;
        sb.append(")");
        return sb.toString();
    }
    
    public void validate() throws TException {
        if (!this.isSetTableStats()) {
            throw new TProtocolException("Required field 'tableStats' is unset! Struct:" + this.toString());
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
        STRUCT_DESC = new TStruct("TableStatsResult");
        TABLE_STATS_FIELD_DESC = new TField("tableStats", (byte)15, (short)1);
        (schemes = new HashMap<Class<? extends IScheme>, SchemeFactory>()).put(StandardScheme.class, new TableStatsResultStandardSchemeFactory());
        TableStatsResult.schemes.put(TupleScheme.class, new TableStatsResultTupleSchemeFactory());
        final Map<_Fields, FieldMetaData> tmpMap = new EnumMap<_Fields, FieldMetaData>(_Fields.class);
        tmpMap.put(_Fields.TABLE_STATS, new FieldMetaData("tableStats", (byte)1, new ListMetaData((byte)15, new StructMetaData((byte)12, ColumnStatisticsObj.class))));
        FieldMetaData.addStructMetaDataMap(TableStatsResult.class, metaDataMap = Collections.unmodifiableMap((Map<? extends _Fields, ? extends FieldMetaData>)tmpMap));
    }
    
    public enum _Fields implements TFieldIdEnum
    {
        TABLE_STATS((short)1, "tableStats");
        
        private static final Map<String, _Fields> byName;
        private final short _thriftId;
        private final String _fieldName;
        
        public static _Fields findByThriftId(final int fieldId) {
            switch (fieldId) {
                case 1: {
                    return _Fields.TABLE_STATS;
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
    
    private static class TableStatsResultStandardSchemeFactory implements SchemeFactory
    {
        @Override
        public TableStatsResultStandardScheme getScheme() {
            return new TableStatsResultStandardScheme();
        }
    }
    
    private static class TableStatsResultStandardScheme extends StandardScheme<TableStatsResult>
    {
        @Override
        public void read(final TProtocol iprot, final TableStatsResult struct) throws TException {
            iprot.readStructBegin();
            while (true) {
                final TField schemeField = iprot.readFieldBegin();
                if (schemeField.type == 0) {
                    break;
                }
                switch (schemeField.id) {
                    case 1: {
                        if (schemeField.type == 15) {
                            final TList _list330 = iprot.readListBegin();
                            struct.tableStats = (List<ColumnStatisticsObj>)new ArrayList(_list330.size);
                            for (int _i331 = 0; _i331 < _list330.size; ++_i331) {
                                final ColumnStatisticsObj _elem332 = new ColumnStatisticsObj();
                                _elem332.read(iprot);
                                struct.tableStats.add(_elem332);
                            }
                            iprot.readListEnd();
                            struct.setTableStatsIsSet(true);
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
        public void write(final TProtocol oprot, final TableStatsResult struct) throws TException {
            struct.validate();
            oprot.writeStructBegin(TableStatsResult.STRUCT_DESC);
            if (struct.tableStats != null) {
                oprot.writeFieldBegin(TableStatsResult.TABLE_STATS_FIELD_DESC);
                oprot.writeListBegin(new TList((byte)12, struct.tableStats.size()));
                for (final ColumnStatisticsObj _iter333 : struct.tableStats) {
                    _iter333.write(oprot);
                }
                oprot.writeListEnd();
                oprot.writeFieldEnd();
            }
            oprot.writeFieldStop();
            oprot.writeStructEnd();
        }
    }
    
    private static class TableStatsResultTupleSchemeFactory implements SchemeFactory
    {
        @Override
        public TableStatsResultTupleScheme getScheme() {
            return new TableStatsResultTupleScheme();
        }
    }
    
    private static class TableStatsResultTupleScheme extends TupleScheme<TableStatsResult>
    {
        @Override
        public void write(final TProtocol prot, final TableStatsResult struct) throws TException {
            final TTupleProtocol oprot = (TTupleProtocol)prot;
            oprot.writeI32(struct.tableStats.size());
            for (final ColumnStatisticsObj _iter334 : struct.tableStats) {
                _iter334.write(oprot);
            }
        }
        
        @Override
        public void read(final TProtocol prot, final TableStatsResult struct) throws TException {
            final TTupleProtocol iprot = (TTupleProtocol)prot;
            final TList _list335 = new TList((byte)12, iprot.readI32());
            struct.tableStats = (List<ColumnStatisticsObj>)new ArrayList(_list335.size);
            for (int _i336 = 0; _i336 < _list335.size; ++_i336) {
                final ColumnStatisticsObj _elem337 = new ColumnStatisticsObj();
                _elem337.read(iprot);
                struct.tableStats.add(_elem337);
            }
            struct.setTableStatsIsSet(true);
        }
    }
}
