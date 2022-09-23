// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.hive.metastore.api;

import org.apache.thrift.protocol.TTupleProtocol;
import org.apache.thrift.protocol.TList;
import org.apache.thrift.protocol.TProtocolUtil;
import java.util.EnumSet;
import java.util.Collections;
import org.apache.thrift.meta_data.ListMetaData;
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

public class ColumnStatistics implements TBase<ColumnStatistics, _Fields>, Serializable, Cloneable
{
    private static final TStruct STRUCT_DESC;
    private static final TField STATS_DESC_FIELD_DESC;
    private static final TField STATS_OBJ_FIELD_DESC;
    private static final Map<Class<? extends IScheme>, SchemeFactory> schemes;
    private ColumnStatisticsDesc statsDesc;
    private List<ColumnStatisticsObj> statsObj;
    public static final Map<_Fields, FieldMetaData> metaDataMap;
    
    public ColumnStatistics() {
    }
    
    public ColumnStatistics(final ColumnStatisticsDesc statsDesc, final List<ColumnStatisticsObj> statsObj) {
        this();
        this.statsDesc = statsDesc;
        this.statsObj = statsObj;
    }
    
    public ColumnStatistics(final ColumnStatistics other) {
        if (other.isSetStatsDesc()) {
            this.statsDesc = new ColumnStatisticsDesc(other.statsDesc);
        }
        if (other.isSetStatsObj()) {
            final List<ColumnStatisticsObj> __this__statsObj = new ArrayList<ColumnStatisticsObj>();
            for (final ColumnStatisticsObj other_element : other.statsObj) {
                __this__statsObj.add(new ColumnStatisticsObj(other_element));
            }
            this.statsObj = __this__statsObj;
        }
    }
    
    @Override
    public ColumnStatistics deepCopy() {
        return new ColumnStatistics(this);
    }
    
    @Override
    public void clear() {
        this.statsDesc = null;
        this.statsObj = null;
    }
    
    public ColumnStatisticsDesc getStatsDesc() {
        return this.statsDesc;
    }
    
    public void setStatsDesc(final ColumnStatisticsDesc statsDesc) {
        this.statsDesc = statsDesc;
    }
    
    public void unsetStatsDesc() {
        this.statsDesc = null;
    }
    
    public boolean isSetStatsDesc() {
        return this.statsDesc != null;
    }
    
    public void setStatsDescIsSet(final boolean value) {
        if (!value) {
            this.statsDesc = null;
        }
    }
    
    public int getStatsObjSize() {
        return (this.statsObj == null) ? 0 : this.statsObj.size();
    }
    
    public Iterator<ColumnStatisticsObj> getStatsObjIterator() {
        return (this.statsObj == null) ? null : this.statsObj.iterator();
    }
    
    public void addToStatsObj(final ColumnStatisticsObj elem) {
        if (this.statsObj == null) {
            this.statsObj = new ArrayList<ColumnStatisticsObj>();
        }
        this.statsObj.add(elem);
    }
    
    public List<ColumnStatisticsObj> getStatsObj() {
        return this.statsObj;
    }
    
    public void setStatsObj(final List<ColumnStatisticsObj> statsObj) {
        this.statsObj = statsObj;
    }
    
    public void unsetStatsObj() {
        this.statsObj = null;
    }
    
    public boolean isSetStatsObj() {
        return this.statsObj != null;
    }
    
    public void setStatsObjIsSet(final boolean value) {
        if (!value) {
            this.statsObj = null;
        }
    }
    
    @Override
    public void setFieldValue(final _Fields field, final Object value) {
        switch (field) {
            case STATS_DESC: {
                if (value == null) {
                    this.unsetStatsDesc();
                    break;
                }
                this.setStatsDesc((ColumnStatisticsDesc)value);
                break;
            }
            case STATS_OBJ: {
                if (value == null) {
                    this.unsetStatsObj();
                    break;
                }
                this.setStatsObj((List<ColumnStatisticsObj>)value);
                break;
            }
        }
    }
    
    @Override
    public Object getFieldValue(final _Fields field) {
        switch (field) {
            case STATS_DESC: {
                return this.getStatsDesc();
            }
            case STATS_OBJ: {
                return this.getStatsObj();
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
            case STATS_DESC: {
                return this.isSetStatsDesc();
            }
            case STATS_OBJ: {
                return this.isSetStatsObj();
            }
            default: {
                throw new IllegalStateException();
            }
        }
    }
    
    @Override
    public boolean equals(final Object that) {
        return that != null && that instanceof ColumnStatistics && this.equals((ColumnStatistics)that);
    }
    
    public boolean equals(final ColumnStatistics that) {
        if (that == null) {
            return false;
        }
        final boolean this_present_statsDesc = this.isSetStatsDesc();
        final boolean that_present_statsDesc = that.isSetStatsDesc();
        if (this_present_statsDesc || that_present_statsDesc) {
            if (!this_present_statsDesc || !that_present_statsDesc) {
                return false;
            }
            if (!this.statsDesc.equals(that.statsDesc)) {
                return false;
            }
        }
        final boolean this_present_statsObj = this.isSetStatsObj();
        final boolean that_present_statsObj = that.isSetStatsObj();
        if (this_present_statsObj || that_present_statsObj) {
            if (!this_present_statsObj || !that_present_statsObj) {
                return false;
            }
            if (!this.statsObj.equals(that.statsObj)) {
                return false;
            }
        }
        return true;
    }
    
    @Override
    public int hashCode() {
        final HashCodeBuilder builder = new HashCodeBuilder();
        final boolean present_statsDesc = this.isSetStatsDesc();
        builder.append(present_statsDesc);
        if (present_statsDesc) {
            builder.append(this.statsDesc);
        }
        final boolean present_statsObj = this.isSetStatsObj();
        builder.append(present_statsObj);
        if (present_statsObj) {
            builder.append(this.statsObj);
        }
        return builder.toHashCode();
    }
    
    @Override
    public int compareTo(final ColumnStatistics other) {
        if (!this.getClass().equals(other.getClass())) {
            return this.getClass().getName().compareTo(other.getClass().getName());
        }
        int lastComparison = 0;
        final ColumnStatistics typedOther = other;
        lastComparison = Boolean.valueOf(this.isSetStatsDesc()).compareTo(Boolean.valueOf(typedOther.isSetStatsDesc()));
        if (lastComparison != 0) {
            return lastComparison;
        }
        if (this.isSetStatsDesc()) {
            lastComparison = TBaseHelper.compareTo(this.statsDesc, typedOther.statsDesc);
            if (lastComparison != 0) {
                return lastComparison;
            }
        }
        lastComparison = Boolean.valueOf(this.isSetStatsObj()).compareTo(Boolean.valueOf(typedOther.isSetStatsObj()));
        if (lastComparison != 0) {
            return lastComparison;
        }
        if (this.isSetStatsObj()) {
            lastComparison = TBaseHelper.compareTo(this.statsObj, typedOther.statsObj);
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
        ColumnStatistics.schemes.get(iprot.getScheme()).getScheme().read(iprot, this);
    }
    
    @Override
    public void write(final TProtocol oprot) throws TException {
        ColumnStatistics.schemes.get(oprot.getScheme()).getScheme().write(oprot, this);
    }
    
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("ColumnStatistics(");
        boolean first = true;
        sb.append("statsDesc:");
        if (this.statsDesc == null) {
            sb.append("null");
        }
        else {
            sb.append(this.statsDesc);
        }
        first = false;
        if (!first) {
            sb.append(", ");
        }
        sb.append("statsObj:");
        if (this.statsObj == null) {
            sb.append("null");
        }
        else {
            sb.append(this.statsObj);
        }
        first = false;
        sb.append(")");
        return sb.toString();
    }
    
    public void validate() throws TException {
        if (!this.isSetStatsDesc()) {
            throw new TProtocolException("Required field 'statsDesc' is unset! Struct:" + this.toString());
        }
        if (!this.isSetStatsObj()) {
            throw new TProtocolException("Required field 'statsObj' is unset! Struct:" + this.toString());
        }
        if (this.statsDesc != null) {
            this.statsDesc.validate();
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
        STRUCT_DESC = new TStruct("ColumnStatistics");
        STATS_DESC_FIELD_DESC = new TField("statsDesc", (byte)12, (short)1);
        STATS_OBJ_FIELD_DESC = new TField("statsObj", (byte)15, (short)2);
        (schemes = new HashMap<Class<? extends IScheme>, SchemeFactory>()).put(StandardScheme.class, new ColumnStatisticsStandardSchemeFactory());
        ColumnStatistics.schemes.put(TupleScheme.class, new ColumnStatisticsTupleSchemeFactory());
        final Map<_Fields, FieldMetaData> tmpMap = new EnumMap<_Fields, FieldMetaData>(_Fields.class);
        tmpMap.put(_Fields.STATS_DESC, new FieldMetaData("statsDesc", (byte)1, new StructMetaData((byte)12, ColumnStatisticsDesc.class)));
        tmpMap.put(_Fields.STATS_OBJ, new FieldMetaData("statsObj", (byte)1, new ListMetaData((byte)15, new StructMetaData((byte)12, ColumnStatisticsObj.class))));
        FieldMetaData.addStructMetaDataMap(ColumnStatistics.class, metaDataMap = Collections.unmodifiableMap((Map<? extends _Fields, ? extends FieldMetaData>)tmpMap));
    }
    
    public enum _Fields implements TFieldIdEnum
    {
        STATS_DESC((short)1, "statsDesc"), 
        STATS_OBJ((short)2, "statsObj");
        
        private static final Map<String, _Fields> byName;
        private final short _thriftId;
        private final String _fieldName;
        
        public static _Fields findByThriftId(final int fieldId) {
            switch (fieldId) {
                case 1: {
                    return _Fields.STATS_DESC;
                }
                case 2: {
                    return _Fields.STATS_OBJ;
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
    
    private static class ColumnStatisticsStandardSchemeFactory implements SchemeFactory
    {
        @Override
        public ColumnStatisticsStandardScheme getScheme() {
            return new ColumnStatisticsStandardScheme();
        }
    }
    
    private static class ColumnStatisticsStandardScheme extends StandardScheme<ColumnStatistics>
    {
        @Override
        public void read(final TProtocol iprot, final ColumnStatistics struct) throws TException {
            iprot.readStructBegin();
            while (true) {
                final TField schemeField = iprot.readFieldBegin();
                if (schemeField.type == 0) {
                    break;
                }
                switch (schemeField.id) {
                    case 1: {
                        if (schemeField.type == 12) {
                            struct.statsDesc = new ColumnStatisticsDesc();
                            struct.statsDesc.read(iprot);
                            struct.setStatsDescIsSet(true);
                            break;
                        }
                        TProtocolUtil.skip(iprot, schemeField.type);
                        break;
                    }
                    case 2: {
                        if (schemeField.type == 15) {
                            final TList _list270 = iprot.readListBegin();
                            struct.statsObj = (List<ColumnStatisticsObj>)new ArrayList(_list270.size);
                            for (int _i271 = 0; _i271 < _list270.size; ++_i271) {
                                final ColumnStatisticsObj _elem272 = new ColumnStatisticsObj();
                                _elem272.read(iprot);
                                struct.statsObj.add(_elem272);
                            }
                            iprot.readListEnd();
                            struct.setStatsObjIsSet(true);
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
        public void write(final TProtocol oprot, final ColumnStatistics struct) throws TException {
            struct.validate();
            oprot.writeStructBegin(ColumnStatistics.STRUCT_DESC);
            if (struct.statsDesc != null) {
                oprot.writeFieldBegin(ColumnStatistics.STATS_DESC_FIELD_DESC);
                struct.statsDesc.write(oprot);
                oprot.writeFieldEnd();
            }
            if (struct.statsObj != null) {
                oprot.writeFieldBegin(ColumnStatistics.STATS_OBJ_FIELD_DESC);
                oprot.writeListBegin(new TList((byte)12, struct.statsObj.size()));
                for (final ColumnStatisticsObj _iter273 : struct.statsObj) {
                    _iter273.write(oprot);
                }
                oprot.writeListEnd();
                oprot.writeFieldEnd();
            }
            oprot.writeFieldStop();
            oprot.writeStructEnd();
        }
    }
    
    private static class ColumnStatisticsTupleSchemeFactory implements SchemeFactory
    {
        @Override
        public ColumnStatisticsTupleScheme getScheme() {
            return new ColumnStatisticsTupleScheme();
        }
    }
    
    private static class ColumnStatisticsTupleScheme extends TupleScheme<ColumnStatistics>
    {
        @Override
        public void write(final TProtocol prot, final ColumnStatistics struct) throws TException {
            final TTupleProtocol oprot = (TTupleProtocol)prot;
            struct.statsDesc.write(oprot);
            oprot.writeI32(struct.statsObj.size());
            for (final ColumnStatisticsObj _iter274 : struct.statsObj) {
                _iter274.write(oprot);
            }
        }
        
        @Override
        public void read(final TProtocol prot, final ColumnStatistics struct) throws TException {
            final TTupleProtocol iprot = (TTupleProtocol)prot;
            struct.statsDesc = new ColumnStatisticsDesc();
            struct.statsDesc.read(iprot);
            struct.setStatsDescIsSet(true);
            final TList _list275 = new TList((byte)12, iprot.readI32());
            struct.statsObj = (List<ColumnStatisticsObj>)new ArrayList(_list275.size);
            for (int _i276 = 0; _i276 < _list275.size; ++_i276) {
                final ColumnStatisticsObj _elem277 = new ColumnStatisticsObj();
                _elem277.read(iprot);
                struct.statsObj.add(_elem277);
            }
            struct.setStatsObjIsSet(true);
        }
    }
}
