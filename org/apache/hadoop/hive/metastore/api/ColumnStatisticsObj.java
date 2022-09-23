// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.hive.metastore.api;

import org.apache.thrift.protocol.TTupleProtocol;
import org.apache.thrift.protocol.TProtocolUtil;
import java.util.Iterator;
import java.util.EnumSet;
import java.util.Collections;
import org.apache.thrift.meta_data.StructMetaData;
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
import org.apache.thrift.meta_data.FieldMetaData;
import org.apache.thrift.scheme.SchemeFactory;
import org.apache.thrift.scheme.IScheme;
import java.util.Map;
import org.apache.thrift.protocol.TField;
import org.apache.thrift.protocol.TStruct;
import java.io.Serializable;
import org.apache.thrift.TBase;

public class ColumnStatisticsObj implements TBase<ColumnStatisticsObj, _Fields>, Serializable, Cloneable
{
    private static final TStruct STRUCT_DESC;
    private static final TField COL_NAME_FIELD_DESC;
    private static final TField COL_TYPE_FIELD_DESC;
    private static final TField STATS_DATA_FIELD_DESC;
    private static final Map<Class<? extends IScheme>, SchemeFactory> schemes;
    private String colName;
    private String colType;
    private ColumnStatisticsData statsData;
    public static final Map<_Fields, FieldMetaData> metaDataMap;
    
    public ColumnStatisticsObj() {
    }
    
    public ColumnStatisticsObj(final String colName, final String colType, final ColumnStatisticsData statsData) {
        this();
        this.colName = colName;
        this.colType = colType;
        this.statsData = statsData;
    }
    
    public ColumnStatisticsObj(final ColumnStatisticsObj other) {
        if (other.isSetColName()) {
            this.colName = other.colName;
        }
        if (other.isSetColType()) {
            this.colType = other.colType;
        }
        if (other.isSetStatsData()) {
            this.statsData = new ColumnStatisticsData(other.statsData);
        }
    }
    
    @Override
    public ColumnStatisticsObj deepCopy() {
        return new ColumnStatisticsObj(this);
    }
    
    @Override
    public void clear() {
        this.colName = null;
        this.colType = null;
        this.statsData = null;
    }
    
    public String getColName() {
        return this.colName;
    }
    
    public void setColName(final String colName) {
        this.colName = colName;
    }
    
    public void unsetColName() {
        this.colName = null;
    }
    
    public boolean isSetColName() {
        return this.colName != null;
    }
    
    public void setColNameIsSet(final boolean value) {
        if (!value) {
            this.colName = null;
        }
    }
    
    public String getColType() {
        return this.colType;
    }
    
    public void setColType(final String colType) {
        this.colType = colType;
    }
    
    public void unsetColType() {
        this.colType = null;
    }
    
    public boolean isSetColType() {
        return this.colType != null;
    }
    
    public void setColTypeIsSet(final boolean value) {
        if (!value) {
            this.colType = null;
        }
    }
    
    public ColumnStatisticsData getStatsData() {
        return this.statsData;
    }
    
    public void setStatsData(final ColumnStatisticsData statsData) {
        this.statsData = statsData;
    }
    
    public void unsetStatsData() {
        this.statsData = null;
    }
    
    public boolean isSetStatsData() {
        return this.statsData != null;
    }
    
    public void setStatsDataIsSet(final boolean value) {
        if (!value) {
            this.statsData = null;
        }
    }
    
    @Override
    public void setFieldValue(final _Fields field, final Object value) {
        switch (field) {
            case COL_NAME: {
                if (value == null) {
                    this.unsetColName();
                    break;
                }
                this.setColName((String)value);
                break;
            }
            case COL_TYPE: {
                if (value == null) {
                    this.unsetColType();
                    break;
                }
                this.setColType((String)value);
                break;
            }
            case STATS_DATA: {
                if (value == null) {
                    this.unsetStatsData();
                    break;
                }
                this.setStatsData((ColumnStatisticsData)value);
                break;
            }
        }
    }
    
    @Override
    public Object getFieldValue(final _Fields field) {
        switch (field) {
            case COL_NAME: {
                return this.getColName();
            }
            case COL_TYPE: {
                return this.getColType();
            }
            case STATS_DATA: {
                return this.getStatsData();
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
            case COL_NAME: {
                return this.isSetColName();
            }
            case COL_TYPE: {
                return this.isSetColType();
            }
            case STATS_DATA: {
                return this.isSetStatsData();
            }
            default: {
                throw new IllegalStateException();
            }
        }
    }
    
    @Override
    public boolean equals(final Object that) {
        return that != null && that instanceof ColumnStatisticsObj && this.equals((ColumnStatisticsObj)that);
    }
    
    public boolean equals(final ColumnStatisticsObj that) {
        if (that == null) {
            return false;
        }
        final boolean this_present_colName = this.isSetColName();
        final boolean that_present_colName = that.isSetColName();
        if (this_present_colName || that_present_colName) {
            if (!this_present_colName || !that_present_colName) {
                return false;
            }
            if (!this.colName.equals(that.colName)) {
                return false;
            }
        }
        final boolean this_present_colType = this.isSetColType();
        final boolean that_present_colType = that.isSetColType();
        if (this_present_colType || that_present_colType) {
            if (!this_present_colType || !that_present_colType) {
                return false;
            }
            if (!this.colType.equals(that.colType)) {
                return false;
            }
        }
        final boolean this_present_statsData = this.isSetStatsData();
        final boolean that_present_statsData = that.isSetStatsData();
        if (this_present_statsData || that_present_statsData) {
            if (!this_present_statsData || !that_present_statsData) {
                return false;
            }
            if (!this.statsData.equals(that.statsData)) {
                return false;
            }
        }
        return true;
    }
    
    @Override
    public int hashCode() {
        final HashCodeBuilder builder = new HashCodeBuilder();
        final boolean present_colName = this.isSetColName();
        builder.append(present_colName);
        if (present_colName) {
            builder.append(this.colName);
        }
        final boolean present_colType = this.isSetColType();
        builder.append(present_colType);
        if (present_colType) {
            builder.append(this.colType);
        }
        final boolean present_statsData = this.isSetStatsData();
        builder.append(present_statsData);
        if (present_statsData) {
            builder.append(this.statsData);
        }
        return builder.toHashCode();
    }
    
    @Override
    public int compareTo(final ColumnStatisticsObj other) {
        if (!this.getClass().equals(other.getClass())) {
            return this.getClass().getName().compareTo(other.getClass().getName());
        }
        int lastComparison = 0;
        final ColumnStatisticsObj typedOther = other;
        lastComparison = Boolean.valueOf(this.isSetColName()).compareTo(Boolean.valueOf(typedOther.isSetColName()));
        if (lastComparison != 0) {
            return lastComparison;
        }
        if (this.isSetColName()) {
            lastComparison = TBaseHelper.compareTo(this.colName, typedOther.colName);
            if (lastComparison != 0) {
                return lastComparison;
            }
        }
        lastComparison = Boolean.valueOf(this.isSetColType()).compareTo(Boolean.valueOf(typedOther.isSetColType()));
        if (lastComparison != 0) {
            return lastComparison;
        }
        if (this.isSetColType()) {
            lastComparison = TBaseHelper.compareTo(this.colType, typedOther.colType);
            if (lastComparison != 0) {
                return lastComparison;
            }
        }
        lastComparison = Boolean.valueOf(this.isSetStatsData()).compareTo(Boolean.valueOf(typedOther.isSetStatsData()));
        if (lastComparison != 0) {
            return lastComparison;
        }
        if (this.isSetStatsData()) {
            lastComparison = TBaseHelper.compareTo(this.statsData, typedOther.statsData);
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
        ColumnStatisticsObj.schemes.get(iprot.getScheme()).getScheme().read(iprot, this);
    }
    
    @Override
    public void write(final TProtocol oprot) throws TException {
        ColumnStatisticsObj.schemes.get(oprot.getScheme()).getScheme().write(oprot, this);
    }
    
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("ColumnStatisticsObj(");
        boolean first = true;
        sb.append("colName:");
        if (this.colName == null) {
            sb.append("null");
        }
        else {
            sb.append(this.colName);
        }
        first = false;
        if (!first) {
            sb.append(", ");
        }
        sb.append("colType:");
        if (this.colType == null) {
            sb.append("null");
        }
        else {
            sb.append(this.colType);
        }
        first = false;
        if (!first) {
            sb.append(", ");
        }
        sb.append("statsData:");
        if (this.statsData == null) {
            sb.append("null");
        }
        else {
            sb.append(this.statsData);
        }
        first = false;
        sb.append(")");
        return sb.toString();
    }
    
    public void validate() throws TException {
        if (!this.isSetColName()) {
            throw new TProtocolException("Required field 'colName' is unset! Struct:" + this.toString());
        }
        if (!this.isSetColType()) {
            throw new TProtocolException("Required field 'colType' is unset! Struct:" + this.toString());
        }
        if (!this.isSetStatsData()) {
            throw new TProtocolException("Required field 'statsData' is unset! Struct:" + this.toString());
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
        STRUCT_DESC = new TStruct("ColumnStatisticsObj");
        COL_NAME_FIELD_DESC = new TField("colName", (byte)11, (short)1);
        COL_TYPE_FIELD_DESC = new TField("colType", (byte)11, (short)2);
        STATS_DATA_FIELD_DESC = new TField("statsData", (byte)12, (short)3);
        (schemes = new HashMap<Class<? extends IScheme>, SchemeFactory>()).put(StandardScheme.class, new ColumnStatisticsObjStandardSchemeFactory());
        ColumnStatisticsObj.schemes.put(TupleScheme.class, new ColumnStatisticsObjTupleSchemeFactory());
        final Map<_Fields, FieldMetaData> tmpMap = new EnumMap<_Fields, FieldMetaData>(_Fields.class);
        tmpMap.put(_Fields.COL_NAME, new FieldMetaData("colName", (byte)1, new FieldValueMetaData((byte)11)));
        tmpMap.put(_Fields.COL_TYPE, new FieldMetaData("colType", (byte)1, new FieldValueMetaData((byte)11)));
        tmpMap.put(_Fields.STATS_DATA, new FieldMetaData("statsData", (byte)1, new StructMetaData((byte)12, ColumnStatisticsData.class)));
        FieldMetaData.addStructMetaDataMap(ColumnStatisticsObj.class, metaDataMap = Collections.unmodifiableMap((Map<? extends _Fields, ? extends FieldMetaData>)tmpMap));
    }
    
    public enum _Fields implements TFieldIdEnum
    {
        COL_NAME((short)1, "colName"), 
        COL_TYPE((short)2, "colType"), 
        STATS_DATA((short)3, "statsData");
        
        private static final Map<String, _Fields> byName;
        private final short _thriftId;
        private final String _fieldName;
        
        public static _Fields findByThriftId(final int fieldId) {
            switch (fieldId) {
                case 1: {
                    return _Fields.COL_NAME;
                }
                case 2: {
                    return _Fields.COL_TYPE;
                }
                case 3: {
                    return _Fields.STATS_DATA;
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
    
    private static class ColumnStatisticsObjStandardSchemeFactory implements SchemeFactory
    {
        @Override
        public ColumnStatisticsObjStandardScheme getScheme() {
            return new ColumnStatisticsObjStandardScheme();
        }
    }
    
    private static class ColumnStatisticsObjStandardScheme extends StandardScheme<ColumnStatisticsObj>
    {
        @Override
        public void read(final TProtocol iprot, final ColumnStatisticsObj struct) throws TException {
            iprot.readStructBegin();
            while (true) {
                final TField schemeField = iprot.readFieldBegin();
                if (schemeField.type == 0) {
                    break;
                }
                switch (schemeField.id) {
                    case 1: {
                        if (schemeField.type == 11) {
                            struct.colName = iprot.readString();
                            struct.setColNameIsSet(true);
                            break;
                        }
                        TProtocolUtil.skip(iprot, schemeField.type);
                        break;
                    }
                    case 2: {
                        if (schemeField.type == 11) {
                            struct.colType = iprot.readString();
                            struct.setColTypeIsSet(true);
                            break;
                        }
                        TProtocolUtil.skip(iprot, schemeField.type);
                        break;
                    }
                    case 3: {
                        if (schemeField.type == 12) {
                            struct.statsData = new ColumnStatisticsData();
                            struct.statsData.read(iprot);
                            struct.setStatsDataIsSet(true);
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
        public void write(final TProtocol oprot, final ColumnStatisticsObj struct) throws TException {
            struct.validate();
            oprot.writeStructBegin(ColumnStatisticsObj.STRUCT_DESC);
            if (struct.colName != null) {
                oprot.writeFieldBegin(ColumnStatisticsObj.COL_NAME_FIELD_DESC);
                oprot.writeString(struct.colName);
                oprot.writeFieldEnd();
            }
            if (struct.colType != null) {
                oprot.writeFieldBegin(ColumnStatisticsObj.COL_TYPE_FIELD_DESC);
                oprot.writeString(struct.colType);
                oprot.writeFieldEnd();
            }
            if (struct.statsData != null) {
                oprot.writeFieldBegin(ColumnStatisticsObj.STATS_DATA_FIELD_DESC);
                struct.statsData.write(oprot);
                oprot.writeFieldEnd();
            }
            oprot.writeFieldStop();
            oprot.writeStructEnd();
        }
    }
    
    private static class ColumnStatisticsObjTupleSchemeFactory implements SchemeFactory
    {
        @Override
        public ColumnStatisticsObjTupleScheme getScheme() {
            return new ColumnStatisticsObjTupleScheme();
        }
    }
    
    private static class ColumnStatisticsObjTupleScheme extends TupleScheme<ColumnStatisticsObj>
    {
        @Override
        public void write(final TProtocol prot, final ColumnStatisticsObj struct) throws TException {
            final TTupleProtocol oprot = (TTupleProtocol)prot;
            oprot.writeString(struct.colName);
            oprot.writeString(struct.colType);
            struct.statsData.write(oprot);
        }
        
        @Override
        public void read(final TProtocol prot, final ColumnStatisticsObj struct) throws TException {
            final TTupleProtocol iprot = (TTupleProtocol)prot;
            struct.colName = iprot.readString();
            struct.setColNameIsSet(true);
            struct.colType = iprot.readString();
            struct.setColTypeIsSet(true);
            struct.statsData = new ColumnStatisticsData();
            struct.statsData.read(iprot);
            struct.setStatsDataIsSet(true);
        }
    }
}
