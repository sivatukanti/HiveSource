// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.hive.metastore.api;

import java.util.BitSet;
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

public class NotificationEventRequest implements TBase<NotificationEventRequest, _Fields>, Serializable, Cloneable
{
    private static final TStruct STRUCT_DESC;
    private static final TField LAST_EVENT_FIELD_DESC;
    private static final TField MAX_EVENTS_FIELD_DESC;
    private static final Map<Class<? extends IScheme>, SchemeFactory> schemes;
    private long lastEvent;
    private int maxEvents;
    private static final int __LASTEVENT_ISSET_ID = 0;
    private static final int __MAXEVENTS_ISSET_ID = 1;
    private byte __isset_bitfield;
    private _Fields[] optionals;
    public static final Map<_Fields, FieldMetaData> metaDataMap;
    
    public NotificationEventRequest() {
        this.__isset_bitfield = 0;
        this.optionals = new _Fields[] { _Fields.MAX_EVENTS };
    }
    
    public NotificationEventRequest(final long lastEvent) {
        this();
        this.lastEvent = lastEvent;
        this.setLastEventIsSet(true);
    }
    
    public NotificationEventRequest(final NotificationEventRequest other) {
        this.__isset_bitfield = 0;
        this.optionals = new _Fields[] { _Fields.MAX_EVENTS };
        this.__isset_bitfield = other.__isset_bitfield;
        this.lastEvent = other.lastEvent;
        this.maxEvents = other.maxEvents;
    }
    
    @Override
    public NotificationEventRequest deepCopy() {
        return new NotificationEventRequest(this);
    }
    
    @Override
    public void clear() {
        this.setLastEventIsSet(false);
        this.lastEvent = 0L;
        this.setMaxEventsIsSet(false);
        this.maxEvents = 0;
    }
    
    public long getLastEvent() {
        return this.lastEvent;
    }
    
    public void setLastEvent(final long lastEvent) {
        this.lastEvent = lastEvent;
        this.setLastEventIsSet(true);
    }
    
    public void unsetLastEvent() {
        this.__isset_bitfield = EncodingUtils.clearBit(this.__isset_bitfield, 0);
    }
    
    public boolean isSetLastEvent() {
        return EncodingUtils.testBit(this.__isset_bitfield, 0);
    }
    
    public void setLastEventIsSet(final boolean value) {
        this.__isset_bitfield = EncodingUtils.setBit(this.__isset_bitfield, 0, value);
    }
    
    public int getMaxEvents() {
        return this.maxEvents;
    }
    
    public void setMaxEvents(final int maxEvents) {
        this.maxEvents = maxEvents;
        this.setMaxEventsIsSet(true);
    }
    
    public void unsetMaxEvents() {
        this.__isset_bitfield = EncodingUtils.clearBit(this.__isset_bitfield, 1);
    }
    
    public boolean isSetMaxEvents() {
        return EncodingUtils.testBit(this.__isset_bitfield, 1);
    }
    
    public void setMaxEventsIsSet(final boolean value) {
        this.__isset_bitfield = EncodingUtils.setBit(this.__isset_bitfield, 1, value);
    }
    
    @Override
    public void setFieldValue(final _Fields field, final Object value) {
        switch (field) {
            case LAST_EVENT: {
                if (value == null) {
                    this.unsetLastEvent();
                    break;
                }
                this.setLastEvent((long)value);
                break;
            }
            case MAX_EVENTS: {
                if (value == null) {
                    this.unsetMaxEvents();
                    break;
                }
                this.setMaxEvents((int)value);
                break;
            }
        }
    }
    
    @Override
    public Object getFieldValue(final _Fields field) {
        switch (field) {
            case LAST_EVENT: {
                return this.getLastEvent();
            }
            case MAX_EVENTS: {
                return this.getMaxEvents();
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
            case LAST_EVENT: {
                return this.isSetLastEvent();
            }
            case MAX_EVENTS: {
                return this.isSetMaxEvents();
            }
            default: {
                throw new IllegalStateException();
            }
        }
    }
    
    @Override
    public boolean equals(final Object that) {
        return that != null && that instanceof NotificationEventRequest && this.equals((NotificationEventRequest)that);
    }
    
    public boolean equals(final NotificationEventRequest that) {
        if (that == null) {
            return false;
        }
        final boolean this_present_lastEvent = true;
        final boolean that_present_lastEvent = true;
        if (this_present_lastEvent || that_present_lastEvent) {
            if (!this_present_lastEvent || !that_present_lastEvent) {
                return false;
            }
            if (this.lastEvent != that.lastEvent) {
                return false;
            }
        }
        final boolean this_present_maxEvents = this.isSetMaxEvents();
        final boolean that_present_maxEvents = that.isSetMaxEvents();
        if (this_present_maxEvents || that_present_maxEvents) {
            if (!this_present_maxEvents || !that_present_maxEvents) {
                return false;
            }
            if (this.maxEvents != that.maxEvents) {
                return false;
            }
        }
        return true;
    }
    
    @Override
    public int hashCode() {
        final HashCodeBuilder builder = new HashCodeBuilder();
        final boolean present_lastEvent = true;
        builder.append(present_lastEvent);
        if (present_lastEvent) {
            builder.append(this.lastEvent);
        }
        final boolean present_maxEvents = this.isSetMaxEvents();
        builder.append(present_maxEvents);
        if (present_maxEvents) {
            builder.append(this.maxEvents);
        }
        return builder.toHashCode();
    }
    
    @Override
    public int compareTo(final NotificationEventRequest other) {
        if (!this.getClass().equals(other.getClass())) {
            return this.getClass().getName().compareTo(other.getClass().getName());
        }
        int lastComparison = 0;
        final NotificationEventRequest typedOther = other;
        lastComparison = Boolean.valueOf(this.isSetLastEvent()).compareTo(Boolean.valueOf(typedOther.isSetLastEvent()));
        if (lastComparison != 0) {
            return lastComparison;
        }
        if (this.isSetLastEvent()) {
            lastComparison = TBaseHelper.compareTo(this.lastEvent, typedOther.lastEvent);
            if (lastComparison != 0) {
                return lastComparison;
            }
        }
        lastComparison = Boolean.valueOf(this.isSetMaxEvents()).compareTo(Boolean.valueOf(typedOther.isSetMaxEvents()));
        if (lastComparison != 0) {
            return lastComparison;
        }
        if (this.isSetMaxEvents()) {
            lastComparison = TBaseHelper.compareTo(this.maxEvents, typedOther.maxEvents);
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
        NotificationEventRequest.schemes.get(iprot.getScheme()).getScheme().read(iprot, this);
    }
    
    @Override
    public void write(final TProtocol oprot) throws TException {
        NotificationEventRequest.schemes.get(oprot.getScheme()).getScheme().write(oprot, this);
    }
    
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("NotificationEventRequest(");
        boolean first = true;
        sb.append("lastEvent:");
        sb.append(this.lastEvent);
        first = false;
        if (this.isSetMaxEvents()) {
            if (!first) {
                sb.append(", ");
            }
            sb.append("maxEvents:");
            sb.append(this.maxEvents);
            first = false;
        }
        sb.append(")");
        return sb.toString();
    }
    
    public void validate() throws TException {
        if (!this.isSetLastEvent()) {
            throw new TProtocolException("Required field 'lastEvent' is unset! Struct:" + this.toString());
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
        STRUCT_DESC = new TStruct("NotificationEventRequest");
        LAST_EVENT_FIELD_DESC = new TField("lastEvent", (byte)10, (short)1);
        MAX_EVENTS_FIELD_DESC = new TField("maxEvents", (byte)8, (short)2);
        (schemes = new HashMap<Class<? extends IScheme>, SchemeFactory>()).put(StandardScheme.class, new NotificationEventRequestStandardSchemeFactory());
        NotificationEventRequest.schemes.put(TupleScheme.class, new NotificationEventRequestTupleSchemeFactory());
        final Map<_Fields, FieldMetaData> tmpMap = new EnumMap<_Fields, FieldMetaData>(_Fields.class);
        tmpMap.put(_Fields.LAST_EVENT, new FieldMetaData("lastEvent", (byte)1, new FieldValueMetaData((byte)10)));
        tmpMap.put(_Fields.MAX_EVENTS, new FieldMetaData("maxEvents", (byte)2, new FieldValueMetaData((byte)8)));
        FieldMetaData.addStructMetaDataMap(NotificationEventRequest.class, metaDataMap = Collections.unmodifiableMap((Map<? extends _Fields, ? extends FieldMetaData>)tmpMap));
    }
    
    public enum _Fields implements TFieldIdEnum
    {
        LAST_EVENT((short)1, "lastEvent"), 
        MAX_EVENTS((short)2, "maxEvents");
        
        private static final Map<String, _Fields> byName;
        private final short _thriftId;
        private final String _fieldName;
        
        public static _Fields findByThriftId(final int fieldId) {
            switch (fieldId) {
                case 1: {
                    return _Fields.LAST_EVENT;
                }
                case 2: {
                    return _Fields.MAX_EVENTS;
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
    
    private static class NotificationEventRequestStandardSchemeFactory implements SchemeFactory
    {
        @Override
        public NotificationEventRequestStandardScheme getScheme() {
            return new NotificationEventRequestStandardScheme();
        }
    }
    
    private static class NotificationEventRequestStandardScheme extends StandardScheme<NotificationEventRequest>
    {
        @Override
        public void read(final TProtocol iprot, final NotificationEventRequest struct) throws TException {
            iprot.readStructBegin();
            while (true) {
                final TField schemeField = iprot.readFieldBegin();
                if (schemeField.type == 0) {
                    break;
                }
                switch (schemeField.id) {
                    case 1: {
                        if (schemeField.type == 10) {
                            struct.lastEvent = iprot.readI64();
                            struct.setLastEventIsSet(true);
                            break;
                        }
                        TProtocolUtil.skip(iprot, schemeField.type);
                        break;
                    }
                    case 2: {
                        if (schemeField.type == 8) {
                            struct.maxEvents = iprot.readI32();
                            struct.setMaxEventsIsSet(true);
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
        public void write(final TProtocol oprot, final NotificationEventRequest struct) throws TException {
            struct.validate();
            oprot.writeStructBegin(NotificationEventRequest.STRUCT_DESC);
            oprot.writeFieldBegin(NotificationEventRequest.LAST_EVENT_FIELD_DESC);
            oprot.writeI64(struct.lastEvent);
            oprot.writeFieldEnd();
            if (struct.isSetMaxEvents()) {
                oprot.writeFieldBegin(NotificationEventRequest.MAX_EVENTS_FIELD_DESC);
                oprot.writeI32(struct.maxEvents);
                oprot.writeFieldEnd();
            }
            oprot.writeFieldStop();
            oprot.writeStructEnd();
        }
    }
    
    private static class NotificationEventRequestTupleSchemeFactory implements SchemeFactory
    {
        @Override
        public NotificationEventRequestTupleScheme getScheme() {
            return new NotificationEventRequestTupleScheme();
        }
    }
    
    private static class NotificationEventRequestTupleScheme extends TupleScheme<NotificationEventRequest>
    {
        @Override
        public void write(final TProtocol prot, final NotificationEventRequest struct) throws TException {
            final TTupleProtocol oprot = (TTupleProtocol)prot;
            oprot.writeI64(struct.lastEvent);
            final BitSet optionals = new BitSet();
            if (struct.isSetMaxEvents()) {
                optionals.set(0);
            }
            oprot.writeBitSet(optionals, 1);
            if (struct.isSetMaxEvents()) {
                oprot.writeI32(struct.maxEvents);
            }
        }
        
        @Override
        public void read(final TProtocol prot, final NotificationEventRequest struct) throws TException {
            final TTupleProtocol iprot = (TTupleProtocol)prot;
            struct.lastEvent = iprot.readI64();
            struct.setLastEventIsSet(true);
            final BitSet incoming = iprot.readBitSet(1);
            if (incoming.get(0)) {
                struct.maxEvents = iprot.readI32();
                struct.setMaxEventsIsSet(true);
            }
        }
    }
}
