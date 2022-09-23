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

public class CurrentNotificationEventId implements TBase<CurrentNotificationEventId, _Fields>, Serializable, Cloneable
{
    private static final TStruct STRUCT_DESC;
    private static final TField EVENT_ID_FIELD_DESC;
    private static final Map<Class<? extends IScheme>, SchemeFactory> schemes;
    private long eventId;
    private static final int __EVENTID_ISSET_ID = 0;
    private byte __isset_bitfield;
    public static final Map<_Fields, FieldMetaData> metaDataMap;
    
    public CurrentNotificationEventId() {
        this.__isset_bitfield = 0;
    }
    
    public CurrentNotificationEventId(final long eventId) {
        this();
        this.eventId = eventId;
        this.setEventIdIsSet(true);
    }
    
    public CurrentNotificationEventId(final CurrentNotificationEventId other) {
        this.__isset_bitfield = 0;
        this.__isset_bitfield = other.__isset_bitfield;
        this.eventId = other.eventId;
    }
    
    @Override
    public CurrentNotificationEventId deepCopy() {
        return new CurrentNotificationEventId(this);
    }
    
    @Override
    public void clear() {
        this.setEventIdIsSet(false);
        this.eventId = 0L;
    }
    
    public long getEventId() {
        return this.eventId;
    }
    
    public void setEventId(final long eventId) {
        this.eventId = eventId;
        this.setEventIdIsSet(true);
    }
    
    public void unsetEventId() {
        this.__isset_bitfield = EncodingUtils.clearBit(this.__isset_bitfield, 0);
    }
    
    public boolean isSetEventId() {
        return EncodingUtils.testBit(this.__isset_bitfield, 0);
    }
    
    public void setEventIdIsSet(final boolean value) {
        this.__isset_bitfield = EncodingUtils.setBit(this.__isset_bitfield, 0, value);
    }
    
    @Override
    public void setFieldValue(final _Fields field, final Object value) {
        switch (field) {
            case EVENT_ID: {
                if (value == null) {
                    this.unsetEventId();
                    break;
                }
                this.setEventId((long)value);
                break;
            }
        }
    }
    
    @Override
    public Object getFieldValue(final _Fields field) {
        switch (field) {
            case EVENT_ID: {
                return this.getEventId();
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
            case EVENT_ID: {
                return this.isSetEventId();
            }
            default: {
                throw new IllegalStateException();
            }
        }
    }
    
    @Override
    public boolean equals(final Object that) {
        return that != null && that instanceof CurrentNotificationEventId && this.equals((CurrentNotificationEventId)that);
    }
    
    public boolean equals(final CurrentNotificationEventId that) {
        if (that == null) {
            return false;
        }
        final boolean this_present_eventId = true;
        final boolean that_present_eventId = true;
        if (this_present_eventId || that_present_eventId) {
            if (!this_present_eventId || !that_present_eventId) {
                return false;
            }
            if (this.eventId != that.eventId) {
                return false;
            }
        }
        return true;
    }
    
    @Override
    public int hashCode() {
        final HashCodeBuilder builder = new HashCodeBuilder();
        final boolean present_eventId = true;
        builder.append(present_eventId);
        if (present_eventId) {
            builder.append(this.eventId);
        }
        return builder.toHashCode();
    }
    
    @Override
    public int compareTo(final CurrentNotificationEventId other) {
        if (!this.getClass().equals(other.getClass())) {
            return this.getClass().getName().compareTo(other.getClass().getName());
        }
        int lastComparison = 0;
        final CurrentNotificationEventId typedOther = other;
        lastComparison = Boolean.valueOf(this.isSetEventId()).compareTo(Boolean.valueOf(typedOther.isSetEventId()));
        if (lastComparison != 0) {
            return lastComparison;
        }
        if (this.isSetEventId()) {
            lastComparison = TBaseHelper.compareTo(this.eventId, typedOther.eventId);
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
        CurrentNotificationEventId.schemes.get(iprot.getScheme()).getScheme().read(iprot, this);
    }
    
    @Override
    public void write(final TProtocol oprot) throws TException {
        CurrentNotificationEventId.schemes.get(oprot.getScheme()).getScheme().write(oprot, this);
    }
    
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("CurrentNotificationEventId(");
        boolean first = true;
        sb.append("eventId:");
        sb.append(this.eventId);
        first = false;
        sb.append(")");
        return sb.toString();
    }
    
    public void validate() throws TException {
        if (!this.isSetEventId()) {
            throw new TProtocolException("Required field 'eventId' is unset! Struct:" + this.toString());
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
        STRUCT_DESC = new TStruct("CurrentNotificationEventId");
        EVENT_ID_FIELD_DESC = new TField("eventId", (byte)10, (short)1);
        (schemes = new HashMap<Class<? extends IScheme>, SchemeFactory>()).put(StandardScheme.class, new CurrentNotificationEventIdStandardSchemeFactory());
        CurrentNotificationEventId.schemes.put(TupleScheme.class, new CurrentNotificationEventIdTupleSchemeFactory());
        final Map<_Fields, FieldMetaData> tmpMap = new EnumMap<_Fields, FieldMetaData>(_Fields.class);
        tmpMap.put(_Fields.EVENT_ID, new FieldMetaData("eventId", (byte)1, new FieldValueMetaData((byte)10)));
        FieldMetaData.addStructMetaDataMap(CurrentNotificationEventId.class, metaDataMap = Collections.unmodifiableMap((Map<? extends _Fields, ? extends FieldMetaData>)tmpMap));
    }
    
    public enum _Fields implements TFieldIdEnum
    {
        EVENT_ID((short)1, "eventId");
        
        private static final Map<String, _Fields> byName;
        private final short _thriftId;
        private final String _fieldName;
        
        public static _Fields findByThriftId(final int fieldId) {
            switch (fieldId) {
                case 1: {
                    return _Fields.EVENT_ID;
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
    
    private static class CurrentNotificationEventIdStandardSchemeFactory implements SchemeFactory
    {
        @Override
        public CurrentNotificationEventIdStandardScheme getScheme() {
            return new CurrentNotificationEventIdStandardScheme();
        }
    }
    
    private static class CurrentNotificationEventIdStandardScheme extends StandardScheme<CurrentNotificationEventId>
    {
        @Override
        public void read(final TProtocol iprot, final CurrentNotificationEventId struct) throws TException {
            iprot.readStructBegin();
            while (true) {
                final TField schemeField = iprot.readFieldBegin();
                if (schemeField.type == 0) {
                    break;
                }
                switch (schemeField.id) {
                    case 1: {
                        if (schemeField.type == 10) {
                            struct.eventId = iprot.readI64();
                            struct.setEventIdIsSet(true);
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
        public void write(final TProtocol oprot, final CurrentNotificationEventId struct) throws TException {
            struct.validate();
            oprot.writeStructBegin(CurrentNotificationEventId.STRUCT_DESC);
            oprot.writeFieldBegin(CurrentNotificationEventId.EVENT_ID_FIELD_DESC);
            oprot.writeI64(struct.eventId);
            oprot.writeFieldEnd();
            oprot.writeFieldStop();
            oprot.writeStructEnd();
        }
    }
    
    private static class CurrentNotificationEventIdTupleSchemeFactory implements SchemeFactory
    {
        @Override
        public CurrentNotificationEventIdTupleScheme getScheme() {
            return new CurrentNotificationEventIdTupleScheme();
        }
    }
    
    private static class CurrentNotificationEventIdTupleScheme extends TupleScheme<CurrentNotificationEventId>
    {
        @Override
        public void write(final TProtocol prot, final CurrentNotificationEventId struct) throws TException {
            final TTupleProtocol oprot = (TTupleProtocol)prot;
            oprot.writeI64(struct.eventId);
        }
        
        @Override
        public void read(final TProtocol prot, final CurrentNotificationEventId struct) throws TException {
            final TTupleProtocol iprot = (TTupleProtocol)prot;
            struct.eventId = iprot.readI64();
            struct.setEventIdIsSet(true);
        }
    }
}
