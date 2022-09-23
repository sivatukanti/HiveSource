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

public class Date implements TBase<Date, _Fields>, Serializable, Cloneable
{
    private static final TStruct STRUCT_DESC;
    private static final TField DAYS_SINCE_EPOCH_FIELD_DESC;
    private static final Map<Class<? extends IScheme>, SchemeFactory> schemes;
    private long daysSinceEpoch;
    private static final int __DAYSSINCEEPOCH_ISSET_ID = 0;
    private byte __isset_bitfield;
    public static final Map<_Fields, FieldMetaData> metaDataMap;
    
    public Date() {
        this.__isset_bitfield = 0;
    }
    
    public Date(final long daysSinceEpoch) {
        this();
        this.daysSinceEpoch = daysSinceEpoch;
        this.setDaysSinceEpochIsSet(true);
    }
    
    public Date(final Date other) {
        this.__isset_bitfield = 0;
        this.__isset_bitfield = other.__isset_bitfield;
        this.daysSinceEpoch = other.daysSinceEpoch;
    }
    
    @Override
    public Date deepCopy() {
        return new Date(this);
    }
    
    @Override
    public void clear() {
        this.setDaysSinceEpochIsSet(false);
        this.daysSinceEpoch = 0L;
    }
    
    public long getDaysSinceEpoch() {
        return this.daysSinceEpoch;
    }
    
    public void setDaysSinceEpoch(final long daysSinceEpoch) {
        this.daysSinceEpoch = daysSinceEpoch;
        this.setDaysSinceEpochIsSet(true);
    }
    
    public void unsetDaysSinceEpoch() {
        this.__isset_bitfield = EncodingUtils.clearBit(this.__isset_bitfield, 0);
    }
    
    public boolean isSetDaysSinceEpoch() {
        return EncodingUtils.testBit(this.__isset_bitfield, 0);
    }
    
    public void setDaysSinceEpochIsSet(final boolean value) {
        this.__isset_bitfield = EncodingUtils.setBit(this.__isset_bitfield, 0, value);
    }
    
    @Override
    public void setFieldValue(final _Fields field, final Object value) {
        switch (field) {
            case DAYS_SINCE_EPOCH: {
                if (value == null) {
                    this.unsetDaysSinceEpoch();
                    break;
                }
                this.setDaysSinceEpoch((long)value);
                break;
            }
        }
    }
    
    @Override
    public Object getFieldValue(final _Fields field) {
        switch (field) {
            case DAYS_SINCE_EPOCH: {
                return this.getDaysSinceEpoch();
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
            case DAYS_SINCE_EPOCH: {
                return this.isSetDaysSinceEpoch();
            }
            default: {
                throw new IllegalStateException();
            }
        }
    }
    
    @Override
    public boolean equals(final Object that) {
        return that != null && that instanceof Date && this.equals((Date)that);
    }
    
    public boolean equals(final Date that) {
        if (that == null) {
            return false;
        }
        final boolean this_present_daysSinceEpoch = true;
        final boolean that_present_daysSinceEpoch = true;
        if (this_present_daysSinceEpoch || that_present_daysSinceEpoch) {
            if (!this_present_daysSinceEpoch || !that_present_daysSinceEpoch) {
                return false;
            }
            if (this.daysSinceEpoch != that.daysSinceEpoch) {
                return false;
            }
        }
        return true;
    }
    
    @Override
    public int hashCode() {
        final HashCodeBuilder builder = new HashCodeBuilder();
        final boolean present_daysSinceEpoch = true;
        builder.append(present_daysSinceEpoch);
        if (present_daysSinceEpoch) {
            builder.append(this.daysSinceEpoch);
        }
        return builder.toHashCode();
    }
    
    @Override
    public int compareTo(final Date other) {
        if (!this.getClass().equals(other.getClass())) {
            return this.getClass().getName().compareTo(other.getClass().getName());
        }
        int lastComparison = 0;
        final Date typedOther = other;
        lastComparison = Boolean.valueOf(this.isSetDaysSinceEpoch()).compareTo(Boolean.valueOf(typedOther.isSetDaysSinceEpoch()));
        if (lastComparison != 0) {
            return lastComparison;
        }
        if (this.isSetDaysSinceEpoch()) {
            lastComparison = TBaseHelper.compareTo(this.daysSinceEpoch, typedOther.daysSinceEpoch);
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
        Date.schemes.get(iprot.getScheme()).getScheme().read(iprot, this);
    }
    
    @Override
    public void write(final TProtocol oprot) throws TException {
        Date.schemes.get(oprot.getScheme()).getScheme().write(oprot, this);
    }
    
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("Date(");
        boolean first = true;
        sb.append("daysSinceEpoch:");
        sb.append(this.daysSinceEpoch);
        first = false;
        sb.append(")");
        return sb.toString();
    }
    
    public void validate() throws TException {
        if (!this.isSetDaysSinceEpoch()) {
            throw new TProtocolException("Required field 'daysSinceEpoch' is unset! Struct:" + this.toString());
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
        STRUCT_DESC = new TStruct("Date");
        DAYS_SINCE_EPOCH_FIELD_DESC = new TField("daysSinceEpoch", (byte)10, (short)1);
        (schemes = new HashMap<Class<? extends IScheme>, SchemeFactory>()).put(StandardScheme.class, new DateStandardSchemeFactory());
        Date.schemes.put(TupleScheme.class, new DateTupleSchemeFactory());
        final Map<_Fields, FieldMetaData> tmpMap = new EnumMap<_Fields, FieldMetaData>(_Fields.class);
        tmpMap.put(_Fields.DAYS_SINCE_EPOCH, new FieldMetaData("daysSinceEpoch", (byte)1, new FieldValueMetaData((byte)10)));
        FieldMetaData.addStructMetaDataMap(Date.class, metaDataMap = Collections.unmodifiableMap((Map<? extends _Fields, ? extends FieldMetaData>)tmpMap));
    }
    
    public enum _Fields implements TFieldIdEnum
    {
        DAYS_SINCE_EPOCH((short)1, "daysSinceEpoch");
        
        private static final Map<String, _Fields> byName;
        private final short _thriftId;
        private final String _fieldName;
        
        public static _Fields findByThriftId(final int fieldId) {
            switch (fieldId) {
                case 1: {
                    return _Fields.DAYS_SINCE_EPOCH;
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
    
    private static class DateStandardSchemeFactory implements SchemeFactory
    {
        @Override
        public DateStandardScheme getScheme() {
            return new DateStandardScheme();
        }
    }
    
    private static class DateStandardScheme extends StandardScheme<Date>
    {
        @Override
        public void read(final TProtocol iprot, final Date struct) throws TException {
            iprot.readStructBegin();
            while (true) {
                final TField schemeField = iprot.readFieldBegin();
                if (schemeField.type == 0) {
                    break;
                }
                switch (schemeField.id) {
                    case 1: {
                        if (schemeField.type == 10) {
                            struct.daysSinceEpoch = iprot.readI64();
                            struct.setDaysSinceEpochIsSet(true);
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
        public void write(final TProtocol oprot, final Date struct) throws TException {
            struct.validate();
            oprot.writeStructBegin(Date.STRUCT_DESC);
            oprot.writeFieldBegin(Date.DAYS_SINCE_EPOCH_FIELD_DESC);
            oprot.writeI64(struct.daysSinceEpoch);
            oprot.writeFieldEnd();
            oprot.writeFieldStop();
            oprot.writeStructEnd();
        }
    }
    
    private static class DateTupleSchemeFactory implements SchemeFactory
    {
        @Override
        public DateTupleScheme getScheme() {
            return new DateTupleScheme();
        }
    }
    
    private static class DateTupleScheme extends TupleScheme<Date>
    {
        @Override
        public void write(final TProtocol prot, final Date struct) throws TException {
            final TTupleProtocol oprot = (TTupleProtocol)prot;
            oprot.writeI64(struct.daysSinceEpoch);
        }
        
        @Override
        public void read(final TProtocol prot, final Date struct) throws TException {
            final TTupleProtocol iprot = (TTupleProtocol)prot;
            struct.daysSinceEpoch = iprot.readI64();
            struct.setDaysSinceEpochIsSet(true);
        }
    }
}
