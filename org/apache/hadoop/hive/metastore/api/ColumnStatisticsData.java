// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.hive.metastore.api;

import java.util.Iterator;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Collections;
import org.apache.thrift.meta_data.FieldValueMetaData;
import org.apache.thrift.meta_data.StructMetaData;
import java.util.EnumMap;
import org.apache.thrift.TBase;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.IOException;
import org.apache.thrift.transport.TTransport;
import org.apache.thrift.protocol.TCompactProtocol;
import java.io.OutputStream;
import org.apache.thrift.transport.TIOStreamTransport;
import java.io.ObjectOutputStream;
import org.apache.thrift.TFieldIdEnum;
import org.apache.thrift.TEnum;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.thrift.TBaseHelper;
import org.apache.thrift.protocol.TProtocolException;
import org.apache.thrift.TException;
import org.apache.thrift.protocol.TProtocolUtil;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.meta_data.FieldMetaData;
import java.util.Map;
import org.apache.thrift.protocol.TField;
import org.apache.thrift.protocol.TStruct;
import org.apache.thrift.TUnion;

public class ColumnStatisticsData extends TUnion<ColumnStatisticsData, _Fields>
{
    private static final TStruct STRUCT_DESC;
    private static final TField BOOLEAN_STATS_FIELD_DESC;
    private static final TField LONG_STATS_FIELD_DESC;
    private static final TField DOUBLE_STATS_FIELD_DESC;
    private static final TField STRING_STATS_FIELD_DESC;
    private static final TField BINARY_STATS_FIELD_DESC;
    private static final TField DECIMAL_STATS_FIELD_DESC;
    private static final TField DATE_STATS_FIELD_DESC;
    public static final Map<_Fields, FieldMetaData> metaDataMap;
    
    public ColumnStatisticsData() {
    }
    
    public ColumnStatisticsData(final _Fields setField, final Object value) {
        super(setField, value);
    }
    
    public ColumnStatisticsData(final ColumnStatisticsData other) {
        super(other);
    }
    
    @Override
    public ColumnStatisticsData deepCopy() {
        return new ColumnStatisticsData(this);
    }
    
    public static ColumnStatisticsData booleanStats(final BooleanColumnStatsData value) {
        final ColumnStatisticsData x = new ColumnStatisticsData();
        x.setBooleanStats(value);
        return x;
    }
    
    public static ColumnStatisticsData longStats(final LongColumnStatsData value) {
        final ColumnStatisticsData x = new ColumnStatisticsData();
        x.setLongStats(value);
        return x;
    }
    
    public static ColumnStatisticsData doubleStats(final DoubleColumnStatsData value) {
        final ColumnStatisticsData x = new ColumnStatisticsData();
        x.setDoubleStats(value);
        return x;
    }
    
    public static ColumnStatisticsData stringStats(final StringColumnStatsData value) {
        final ColumnStatisticsData x = new ColumnStatisticsData();
        x.setStringStats(value);
        return x;
    }
    
    public static ColumnStatisticsData binaryStats(final BinaryColumnStatsData value) {
        final ColumnStatisticsData x = new ColumnStatisticsData();
        x.setBinaryStats(value);
        return x;
    }
    
    public static ColumnStatisticsData decimalStats(final DecimalColumnStatsData value) {
        final ColumnStatisticsData x = new ColumnStatisticsData();
        x.setDecimalStats(value);
        return x;
    }
    
    public static ColumnStatisticsData dateStats(final DateColumnStatsData value) {
        final ColumnStatisticsData x = new ColumnStatisticsData();
        x.setDateStats(value);
        return x;
    }
    
    @Override
    protected void checkType(final _Fields setField, final Object value) throws ClassCastException {
        switch (setField) {
            case BOOLEAN_STATS: {
                if (value instanceof BooleanColumnStatsData) {
                    break;
                }
                throw new ClassCastException("Was expecting value of type BooleanColumnStatsData for field 'booleanStats', but got " + value.getClass().getSimpleName());
            }
            case LONG_STATS: {
                if (value instanceof LongColumnStatsData) {
                    break;
                }
                throw new ClassCastException("Was expecting value of type LongColumnStatsData for field 'longStats', but got " + value.getClass().getSimpleName());
            }
            case DOUBLE_STATS: {
                if (value instanceof DoubleColumnStatsData) {
                    break;
                }
                throw new ClassCastException("Was expecting value of type DoubleColumnStatsData for field 'doubleStats', but got " + value.getClass().getSimpleName());
            }
            case STRING_STATS: {
                if (value instanceof StringColumnStatsData) {
                    break;
                }
                throw new ClassCastException("Was expecting value of type StringColumnStatsData for field 'stringStats', but got " + value.getClass().getSimpleName());
            }
            case BINARY_STATS: {
                if (value instanceof BinaryColumnStatsData) {
                    break;
                }
                throw new ClassCastException("Was expecting value of type BinaryColumnStatsData for field 'binaryStats', but got " + value.getClass().getSimpleName());
            }
            case DECIMAL_STATS: {
                if (value instanceof DecimalColumnStatsData) {
                    break;
                }
                throw new ClassCastException("Was expecting value of type DecimalColumnStatsData for field 'decimalStats', but got " + value.getClass().getSimpleName());
            }
            case DATE_STATS: {
                if (value instanceof DateColumnStatsData) {
                    break;
                }
                throw new ClassCastException("Was expecting value of type DateColumnStatsData for field 'dateStats', but got " + value.getClass().getSimpleName());
            }
            default: {
                throw new IllegalArgumentException("Unknown field id " + setField);
            }
        }
    }
    
    @Override
    protected Object standardSchemeReadValue(final TProtocol iprot, final TField field) throws TException {
        final _Fields setField = _Fields.findByThriftId(field.id);
        if (setField == null) {
            return null;
        }
        switch (setField) {
            case BOOLEAN_STATS: {
                if (field.type == ColumnStatisticsData.BOOLEAN_STATS_FIELD_DESC.type) {
                    final BooleanColumnStatsData booleanStats = new BooleanColumnStatsData();
                    booleanStats.read(iprot);
                    return booleanStats;
                }
                TProtocolUtil.skip(iprot, field.type);
                return null;
            }
            case LONG_STATS: {
                if (field.type == ColumnStatisticsData.LONG_STATS_FIELD_DESC.type) {
                    final LongColumnStatsData longStats = new LongColumnStatsData();
                    longStats.read(iprot);
                    return longStats;
                }
                TProtocolUtil.skip(iprot, field.type);
                return null;
            }
            case DOUBLE_STATS: {
                if (field.type == ColumnStatisticsData.DOUBLE_STATS_FIELD_DESC.type) {
                    final DoubleColumnStatsData doubleStats = new DoubleColumnStatsData();
                    doubleStats.read(iprot);
                    return doubleStats;
                }
                TProtocolUtil.skip(iprot, field.type);
                return null;
            }
            case STRING_STATS: {
                if (field.type == ColumnStatisticsData.STRING_STATS_FIELD_DESC.type) {
                    final StringColumnStatsData stringStats = new StringColumnStatsData();
                    stringStats.read(iprot);
                    return stringStats;
                }
                TProtocolUtil.skip(iprot, field.type);
                return null;
            }
            case BINARY_STATS: {
                if (field.type == ColumnStatisticsData.BINARY_STATS_FIELD_DESC.type) {
                    final BinaryColumnStatsData binaryStats = new BinaryColumnStatsData();
                    binaryStats.read(iprot);
                    return binaryStats;
                }
                TProtocolUtil.skip(iprot, field.type);
                return null;
            }
            case DECIMAL_STATS: {
                if (field.type == ColumnStatisticsData.DECIMAL_STATS_FIELD_DESC.type) {
                    final DecimalColumnStatsData decimalStats = new DecimalColumnStatsData();
                    decimalStats.read(iprot);
                    return decimalStats;
                }
                TProtocolUtil.skip(iprot, field.type);
                return null;
            }
            case DATE_STATS: {
                if (field.type == ColumnStatisticsData.DATE_STATS_FIELD_DESC.type) {
                    final DateColumnStatsData dateStats = new DateColumnStatsData();
                    dateStats.read(iprot);
                    return dateStats;
                }
                TProtocolUtil.skip(iprot, field.type);
                return null;
            }
            default: {
                throw new IllegalStateException("setField wasn't null, but didn't match any of the case statements!");
            }
        }
    }
    
    @Override
    protected void standardSchemeWriteValue(final TProtocol oprot) throws TException {
        switch ((_Fields)this.setField_) {
            case BOOLEAN_STATS: {
                final BooleanColumnStatsData booleanStats = (BooleanColumnStatsData)this.value_;
                booleanStats.write(oprot);
            }
            case LONG_STATS: {
                final LongColumnStatsData longStats = (LongColumnStatsData)this.value_;
                longStats.write(oprot);
            }
            case DOUBLE_STATS: {
                final DoubleColumnStatsData doubleStats = (DoubleColumnStatsData)this.value_;
                doubleStats.write(oprot);
            }
            case STRING_STATS: {
                final StringColumnStatsData stringStats = (StringColumnStatsData)this.value_;
                stringStats.write(oprot);
            }
            case BINARY_STATS: {
                final BinaryColumnStatsData binaryStats = (BinaryColumnStatsData)this.value_;
                binaryStats.write(oprot);
            }
            case DECIMAL_STATS: {
                final DecimalColumnStatsData decimalStats = (DecimalColumnStatsData)this.value_;
                decimalStats.write(oprot);
            }
            case DATE_STATS: {
                final DateColumnStatsData dateStats = (DateColumnStatsData)this.value_;
                dateStats.write(oprot);
            }
            default: {
                throw new IllegalStateException("Cannot write union with unknown field " + this.setField_);
            }
        }
    }
    
    @Override
    protected Object tupleSchemeReadValue(final TProtocol iprot, final short fieldID) throws TException {
        final _Fields setField = _Fields.findByThriftId(fieldID);
        if (setField == null) {
            throw new TProtocolException("Couldn't find a field with field id " + fieldID);
        }
        switch (setField) {
            case BOOLEAN_STATS: {
                final BooleanColumnStatsData booleanStats = new BooleanColumnStatsData();
                booleanStats.read(iprot);
                return booleanStats;
            }
            case LONG_STATS: {
                final LongColumnStatsData longStats = new LongColumnStatsData();
                longStats.read(iprot);
                return longStats;
            }
            case DOUBLE_STATS: {
                final DoubleColumnStatsData doubleStats = new DoubleColumnStatsData();
                doubleStats.read(iprot);
                return doubleStats;
            }
            case STRING_STATS: {
                final StringColumnStatsData stringStats = new StringColumnStatsData();
                stringStats.read(iprot);
                return stringStats;
            }
            case BINARY_STATS: {
                final BinaryColumnStatsData binaryStats = new BinaryColumnStatsData();
                binaryStats.read(iprot);
                return binaryStats;
            }
            case DECIMAL_STATS: {
                final DecimalColumnStatsData decimalStats = new DecimalColumnStatsData();
                decimalStats.read(iprot);
                return decimalStats;
            }
            case DATE_STATS: {
                final DateColumnStatsData dateStats = new DateColumnStatsData();
                dateStats.read(iprot);
                return dateStats;
            }
            default: {
                throw new IllegalStateException("setField wasn't null, but didn't match any of the case statements!");
            }
        }
    }
    
    @Override
    protected void tupleSchemeWriteValue(final TProtocol oprot) throws TException {
        switch ((_Fields)this.setField_) {
            case BOOLEAN_STATS: {
                final BooleanColumnStatsData booleanStats = (BooleanColumnStatsData)this.value_;
                booleanStats.write(oprot);
            }
            case LONG_STATS: {
                final LongColumnStatsData longStats = (LongColumnStatsData)this.value_;
                longStats.write(oprot);
            }
            case DOUBLE_STATS: {
                final DoubleColumnStatsData doubleStats = (DoubleColumnStatsData)this.value_;
                doubleStats.write(oprot);
            }
            case STRING_STATS: {
                final StringColumnStatsData stringStats = (StringColumnStatsData)this.value_;
                stringStats.write(oprot);
            }
            case BINARY_STATS: {
                final BinaryColumnStatsData binaryStats = (BinaryColumnStatsData)this.value_;
                binaryStats.write(oprot);
            }
            case DECIMAL_STATS: {
                final DecimalColumnStatsData decimalStats = (DecimalColumnStatsData)this.value_;
                decimalStats.write(oprot);
            }
            case DATE_STATS: {
                final DateColumnStatsData dateStats = (DateColumnStatsData)this.value_;
                dateStats.write(oprot);
            }
            default: {
                throw new IllegalStateException("Cannot write union with unknown field " + this.setField_);
            }
        }
    }
    
    @Override
    protected TField getFieldDesc(final _Fields setField) {
        switch (setField) {
            case BOOLEAN_STATS: {
                return ColumnStatisticsData.BOOLEAN_STATS_FIELD_DESC;
            }
            case LONG_STATS: {
                return ColumnStatisticsData.LONG_STATS_FIELD_DESC;
            }
            case DOUBLE_STATS: {
                return ColumnStatisticsData.DOUBLE_STATS_FIELD_DESC;
            }
            case STRING_STATS: {
                return ColumnStatisticsData.STRING_STATS_FIELD_DESC;
            }
            case BINARY_STATS: {
                return ColumnStatisticsData.BINARY_STATS_FIELD_DESC;
            }
            case DECIMAL_STATS: {
                return ColumnStatisticsData.DECIMAL_STATS_FIELD_DESC;
            }
            case DATE_STATS: {
                return ColumnStatisticsData.DATE_STATS_FIELD_DESC;
            }
            default: {
                throw new IllegalArgumentException("Unknown field id " + setField);
            }
        }
    }
    
    @Override
    protected TStruct getStructDesc() {
        return ColumnStatisticsData.STRUCT_DESC;
    }
    
    @Override
    protected _Fields enumForId(final short id) {
        return _Fields.findByThriftIdOrThrow(id);
    }
    
    @Override
    public _Fields fieldForId(final int fieldId) {
        return _Fields.findByThriftId(fieldId);
    }
    
    public BooleanColumnStatsData getBooleanStats() {
        if (this.getSetField() == _Fields.BOOLEAN_STATS) {
            return (BooleanColumnStatsData)this.getFieldValue();
        }
        throw new RuntimeException("Cannot get field 'booleanStats' because union is currently set to " + this.getFieldDesc(((TUnion<T, _Fields>)this).getSetField()).name);
    }
    
    public void setBooleanStats(final BooleanColumnStatsData value) {
        if (value == null) {
            throw new NullPointerException();
        }
        this.setField_ = (F)_Fields.BOOLEAN_STATS;
        this.value_ = value;
    }
    
    public LongColumnStatsData getLongStats() {
        if (this.getSetField() == _Fields.LONG_STATS) {
            return (LongColumnStatsData)this.getFieldValue();
        }
        throw new RuntimeException("Cannot get field 'longStats' because union is currently set to " + this.getFieldDesc(((TUnion<T, _Fields>)this).getSetField()).name);
    }
    
    public void setLongStats(final LongColumnStatsData value) {
        if (value == null) {
            throw new NullPointerException();
        }
        this.setField_ = (F)_Fields.LONG_STATS;
        this.value_ = value;
    }
    
    public DoubleColumnStatsData getDoubleStats() {
        if (this.getSetField() == _Fields.DOUBLE_STATS) {
            return (DoubleColumnStatsData)this.getFieldValue();
        }
        throw new RuntimeException("Cannot get field 'doubleStats' because union is currently set to " + this.getFieldDesc(((TUnion<T, _Fields>)this).getSetField()).name);
    }
    
    public void setDoubleStats(final DoubleColumnStatsData value) {
        if (value == null) {
            throw new NullPointerException();
        }
        this.setField_ = (F)_Fields.DOUBLE_STATS;
        this.value_ = value;
    }
    
    public StringColumnStatsData getStringStats() {
        if (this.getSetField() == _Fields.STRING_STATS) {
            return (StringColumnStatsData)this.getFieldValue();
        }
        throw new RuntimeException("Cannot get field 'stringStats' because union is currently set to " + this.getFieldDesc(((TUnion<T, _Fields>)this).getSetField()).name);
    }
    
    public void setStringStats(final StringColumnStatsData value) {
        if (value == null) {
            throw new NullPointerException();
        }
        this.setField_ = (F)_Fields.STRING_STATS;
        this.value_ = value;
    }
    
    public BinaryColumnStatsData getBinaryStats() {
        if (this.getSetField() == _Fields.BINARY_STATS) {
            return (BinaryColumnStatsData)this.getFieldValue();
        }
        throw new RuntimeException("Cannot get field 'binaryStats' because union is currently set to " + this.getFieldDesc(((TUnion<T, _Fields>)this).getSetField()).name);
    }
    
    public void setBinaryStats(final BinaryColumnStatsData value) {
        if (value == null) {
            throw new NullPointerException();
        }
        this.setField_ = (F)_Fields.BINARY_STATS;
        this.value_ = value;
    }
    
    public DecimalColumnStatsData getDecimalStats() {
        if (this.getSetField() == _Fields.DECIMAL_STATS) {
            return (DecimalColumnStatsData)this.getFieldValue();
        }
        throw new RuntimeException("Cannot get field 'decimalStats' because union is currently set to " + this.getFieldDesc(((TUnion<T, _Fields>)this).getSetField()).name);
    }
    
    public void setDecimalStats(final DecimalColumnStatsData value) {
        if (value == null) {
            throw new NullPointerException();
        }
        this.setField_ = (F)_Fields.DECIMAL_STATS;
        this.value_ = value;
    }
    
    public DateColumnStatsData getDateStats() {
        if (this.getSetField() == _Fields.DATE_STATS) {
            return (DateColumnStatsData)this.getFieldValue();
        }
        throw new RuntimeException("Cannot get field 'dateStats' because union is currently set to " + this.getFieldDesc(((TUnion<T, _Fields>)this).getSetField()).name);
    }
    
    public void setDateStats(final DateColumnStatsData value) {
        if (value == null) {
            throw new NullPointerException();
        }
        this.setField_ = (F)_Fields.DATE_STATS;
        this.value_ = value;
    }
    
    public boolean isSetBooleanStats() {
        return this.setField_ == _Fields.BOOLEAN_STATS;
    }
    
    public boolean isSetLongStats() {
        return this.setField_ == _Fields.LONG_STATS;
    }
    
    public boolean isSetDoubleStats() {
        return this.setField_ == _Fields.DOUBLE_STATS;
    }
    
    public boolean isSetStringStats() {
        return this.setField_ == _Fields.STRING_STATS;
    }
    
    public boolean isSetBinaryStats() {
        return this.setField_ == _Fields.BINARY_STATS;
    }
    
    public boolean isSetDecimalStats() {
        return this.setField_ == _Fields.DECIMAL_STATS;
    }
    
    public boolean isSetDateStats() {
        return this.setField_ == _Fields.DATE_STATS;
    }
    
    @Override
    public boolean equals(final Object other) {
        return other instanceof ColumnStatisticsData && this.equals((ColumnStatisticsData)other);
    }
    
    public boolean equals(final ColumnStatisticsData other) {
        return other != null && this.getSetField() == other.getSetField() && this.getFieldValue().equals(other.getFieldValue());
    }
    
    @Override
    public int compareTo(final ColumnStatisticsData other) {
        final int lastComparison = TBaseHelper.compareTo(((TUnion<T, Comparable>)this).getSetField(), ((TUnion<T, Comparable>)other).getSetField());
        if (lastComparison == 0) {
            return TBaseHelper.compareTo(this.getFieldValue(), other.getFieldValue());
        }
        return lastComparison;
    }
    
    @Override
    public int hashCode() {
        final HashCodeBuilder hcb = new HashCodeBuilder();
        hcb.append(this.getClass().getName());
        final TFieldIdEnum setField = ((TUnion<T, TFieldIdEnum>)this).getSetField();
        if (setField != null) {
            hcb.append(setField.getThriftFieldId());
            final Object value = this.getFieldValue();
            if (value instanceof TEnum) {
                hcb.append(((TEnum)this.getFieldValue()).getValue());
            }
            else {
                hcb.append(value);
            }
        }
        return hcb.toHashCode();
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
        STRUCT_DESC = new TStruct("ColumnStatisticsData");
        BOOLEAN_STATS_FIELD_DESC = new TField("booleanStats", (byte)12, (short)1);
        LONG_STATS_FIELD_DESC = new TField("longStats", (byte)12, (short)2);
        DOUBLE_STATS_FIELD_DESC = new TField("doubleStats", (byte)12, (short)3);
        STRING_STATS_FIELD_DESC = new TField("stringStats", (byte)12, (short)4);
        BINARY_STATS_FIELD_DESC = new TField("binaryStats", (byte)12, (short)5);
        DECIMAL_STATS_FIELD_DESC = new TField("decimalStats", (byte)12, (short)6);
        DATE_STATS_FIELD_DESC = new TField("dateStats", (byte)12, (short)7);
        final Map<_Fields, FieldMetaData> tmpMap = new EnumMap<_Fields, FieldMetaData>(_Fields.class);
        tmpMap.put(_Fields.BOOLEAN_STATS, new FieldMetaData("booleanStats", (byte)3, new StructMetaData((byte)12, BooleanColumnStatsData.class)));
        tmpMap.put(_Fields.LONG_STATS, new FieldMetaData("longStats", (byte)3, new StructMetaData((byte)12, LongColumnStatsData.class)));
        tmpMap.put(_Fields.DOUBLE_STATS, new FieldMetaData("doubleStats", (byte)3, new StructMetaData((byte)12, DoubleColumnStatsData.class)));
        tmpMap.put(_Fields.STRING_STATS, new FieldMetaData("stringStats", (byte)3, new StructMetaData((byte)12, StringColumnStatsData.class)));
        tmpMap.put(_Fields.BINARY_STATS, new FieldMetaData("binaryStats", (byte)3, new StructMetaData((byte)12, BinaryColumnStatsData.class)));
        tmpMap.put(_Fields.DECIMAL_STATS, new FieldMetaData("decimalStats", (byte)3, new StructMetaData((byte)12, DecimalColumnStatsData.class)));
        tmpMap.put(_Fields.DATE_STATS, new FieldMetaData("dateStats", (byte)3, new StructMetaData((byte)12, DateColumnStatsData.class)));
        FieldMetaData.addStructMetaDataMap(ColumnStatisticsData.class, metaDataMap = Collections.unmodifiableMap((Map<? extends _Fields, ? extends FieldMetaData>)tmpMap));
    }
    
    public enum _Fields implements TFieldIdEnum
    {
        BOOLEAN_STATS((short)1, "booleanStats"), 
        LONG_STATS((short)2, "longStats"), 
        DOUBLE_STATS((short)3, "doubleStats"), 
        STRING_STATS((short)4, "stringStats"), 
        BINARY_STATS((short)5, "binaryStats"), 
        DECIMAL_STATS((short)6, "decimalStats"), 
        DATE_STATS((short)7, "dateStats");
        
        private static final Map<String, _Fields> byName;
        private final short _thriftId;
        private final String _fieldName;
        
        public static _Fields findByThriftId(final int fieldId) {
            switch (fieldId) {
                case 1: {
                    return _Fields.BOOLEAN_STATS;
                }
                case 2: {
                    return _Fields.LONG_STATS;
                }
                case 3: {
                    return _Fields.DOUBLE_STATS;
                }
                case 4: {
                    return _Fields.STRING_STATS;
                }
                case 5: {
                    return _Fields.BINARY_STATS;
                }
                case 6: {
                    return _Fields.DECIMAL_STATS;
                }
                case 7: {
                    return _Fields.DATE_STATS;
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
}
