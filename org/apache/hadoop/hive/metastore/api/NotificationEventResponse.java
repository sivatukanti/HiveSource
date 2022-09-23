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

public class NotificationEventResponse implements TBase<NotificationEventResponse, _Fields>, Serializable, Cloneable
{
    private static final TStruct STRUCT_DESC;
    private static final TField EVENTS_FIELD_DESC;
    private static final Map<Class<? extends IScheme>, SchemeFactory> schemes;
    private List<NotificationEvent> events;
    public static final Map<_Fields, FieldMetaData> metaDataMap;
    
    public NotificationEventResponse() {
    }
    
    public NotificationEventResponse(final List<NotificationEvent> events) {
        this();
        this.events = events;
    }
    
    public NotificationEventResponse(final NotificationEventResponse other) {
        if (other.isSetEvents()) {
            final List<NotificationEvent> __this__events = new ArrayList<NotificationEvent>();
            for (final NotificationEvent other_element : other.events) {
                __this__events.add(new NotificationEvent(other_element));
            }
            this.events = __this__events;
        }
    }
    
    @Override
    public NotificationEventResponse deepCopy() {
        return new NotificationEventResponse(this);
    }
    
    @Override
    public void clear() {
        this.events = null;
    }
    
    public int getEventsSize() {
        return (this.events == null) ? 0 : this.events.size();
    }
    
    public Iterator<NotificationEvent> getEventsIterator() {
        return (this.events == null) ? null : this.events.iterator();
    }
    
    public void addToEvents(final NotificationEvent elem) {
        if (this.events == null) {
            this.events = new ArrayList<NotificationEvent>();
        }
        this.events.add(elem);
    }
    
    public List<NotificationEvent> getEvents() {
        return this.events;
    }
    
    public void setEvents(final List<NotificationEvent> events) {
        this.events = events;
    }
    
    public void unsetEvents() {
        this.events = null;
    }
    
    public boolean isSetEvents() {
        return this.events != null;
    }
    
    public void setEventsIsSet(final boolean value) {
        if (!value) {
            this.events = null;
        }
    }
    
    @Override
    public void setFieldValue(final _Fields field, final Object value) {
        switch (field) {
            case EVENTS: {
                if (value == null) {
                    this.unsetEvents();
                    break;
                }
                this.setEvents((List<NotificationEvent>)value);
                break;
            }
        }
    }
    
    @Override
    public Object getFieldValue(final _Fields field) {
        switch (field) {
            case EVENTS: {
                return this.getEvents();
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
            case EVENTS: {
                return this.isSetEvents();
            }
            default: {
                throw new IllegalStateException();
            }
        }
    }
    
    @Override
    public boolean equals(final Object that) {
        return that != null && that instanceof NotificationEventResponse && this.equals((NotificationEventResponse)that);
    }
    
    public boolean equals(final NotificationEventResponse that) {
        if (that == null) {
            return false;
        }
        final boolean this_present_events = this.isSetEvents();
        final boolean that_present_events = that.isSetEvents();
        if (this_present_events || that_present_events) {
            if (!this_present_events || !that_present_events) {
                return false;
            }
            if (!this.events.equals(that.events)) {
                return false;
            }
        }
        return true;
    }
    
    @Override
    public int hashCode() {
        final HashCodeBuilder builder = new HashCodeBuilder();
        final boolean present_events = this.isSetEvents();
        builder.append(present_events);
        if (present_events) {
            builder.append(this.events);
        }
        return builder.toHashCode();
    }
    
    @Override
    public int compareTo(final NotificationEventResponse other) {
        if (!this.getClass().equals(other.getClass())) {
            return this.getClass().getName().compareTo(other.getClass().getName());
        }
        int lastComparison = 0;
        final NotificationEventResponse typedOther = other;
        lastComparison = Boolean.valueOf(this.isSetEvents()).compareTo(Boolean.valueOf(typedOther.isSetEvents()));
        if (lastComparison != 0) {
            return lastComparison;
        }
        if (this.isSetEvents()) {
            lastComparison = TBaseHelper.compareTo(this.events, typedOther.events);
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
        NotificationEventResponse.schemes.get(iprot.getScheme()).getScheme().read(iprot, this);
    }
    
    @Override
    public void write(final TProtocol oprot) throws TException {
        NotificationEventResponse.schemes.get(oprot.getScheme()).getScheme().write(oprot, this);
    }
    
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("NotificationEventResponse(");
        boolean first = true;
        sb.append("events:");
        if (this.events == null) {
            sb.append("null");
        }
        else {
            sb.append(this.events);
        }
        first = false;
        sb.append(")");
        return sb.toString();
    }
    
    public void validate() throws TException {
        if (!this.isSetEvents()) {
            throw new TProtocolException("Required field 'events' is unset! Struct:" + this.toString());
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
        STRUCT_DESC = new TStruct("NotificationEventResponse");
        EVENTS_FIELD_DESC = new TField("events", (byte)15, (short)1);
        (schemes = new HashMap<Class<? extends IScheme>, SchemeFactory>()).put(StandardScheme.class, new NotificationEventResponseStandardSchemeFactory());
        NotificationEventResponse.schemes.put(TupleScheme.class, new NotificationEventResponseTupleSchemeFactory());
        final Map<_Fields, FieldMetaData> tmpMap = new EnumMap<_Fields, FieldMetaData>(_Fields.class);
        tmpMap.put(_Fields.EVENTS, new FieldMetaData("events", (byte)1, new ListMetaData((byte)15, new StructMetaData((byte)12, NotificationEvent.class))));
        FieldMetaData.addStructMetaDataMap(NotificationEventResponse.class, metaDataMap = Collections.unmodifiableMap((Map<? extends _Fields, ? extends FieldMetaData>)tmpMap));
    }
    
    public enum _Fields implements TFieldIdEnum
    {
        EVENTS((short)1, "events");
        
        private static final Map<String, _Fields> byName;
        private final short _thriftId;
        private final String _fieldName;
        
        public static _Fields findByThriftId(final int fieldId) {
            switch (fieldId) {
                case 1: {
                    return _Fields.EVENTS;
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
    
    private static class NotificationEventResponseStandardSchemeFactory implements SchemeFactory
    {
        @Override
        public NotificationEventResponseStandardScheme getScheme() {
            return new NotificationEventResponseStandardScheme();
        }
    }
    
    private static class NotificationEventResponseStandardScheme extends StandardScheme<NotificationEventResponse>
    {
        @Override
        public void read(final TProtocol iprot, final NotificationEventResponse struct) throws TException {
            iprot.readStructBegin();
            while (true) {
                final TField schemeField = iprot.readFieldBegin();
                if (schemeField.type == 0) {
                    break;
                }
                switch (schemeField.id) {
                    case 1: {
                        if (schemeField.type == 15) {
                            final TList _list500 = iprot.readListBegin();
                            struct.events = (List<NotificationEvent>)new ArrayList(_list500.size);
                            for (int _i501 = 0; _i501 < _list500.size; ++_i501) {
                                final NotificationEvent _elem502 = new NotificationEvent();
                                _elem502.read(iprot);
                                struct.events.add(_elem502);
                            }
                            iprot.readListEnd();
                            struct.setEventsIsSet(true);
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
        public void write(final TProtocol oprot, final NotificationEventResponse struct) throws TException {
            struct.validate();
            oprot.writeStructBegin(NotificationEventResponse.STRUCT_DESC);
            if (struct.events != null) {
                oprot.writeFieldBegin(NotificationEventResponse.EVENTS_FIELD_DESC);
                oprot.writeListBegin(new TList((byte)12, struct.events.size()));
                for (final NotificationEvent _iter503 : struct.events) {
                    _iter503.write(oprot);
                }
                oprot.writeListEnd();
                oprot.writeFieldEnd();
            }
            oprot.writeFieldStop();
            oprot.writeStructEnd();
        }
    }
    
    private static class NotificationEventResponseTupleSchemeFactory implements SchemeFactory
    {
        @Override
        public NotificationEventResponseTupleScheme getScheme() {
            return new NotificationEventResponseTupleScheme();
        }
    }
    
    private static class NotificationEventResponseTupleScheme extends TupleScheme<NotificationEventResponse>
    {
        @Override
        public void write(final TProtocol prot, final NotificationEventResponse struct) throws TException {
            final TTupleProtocol oprot = (TTupleProtocol)prot;
            oprot.writeI32(struct.events.size());
            for (final NotificationEvent _iter504 : struct.events) {
                _iter504.write(oprot);
            }
        }
        
        @Override
        public void read(final TProtocol prot, final NotificationEventResponse struct) throws TException {
            final TTupleProtocol iprot = (TTupleProtocol)prot;
            final TList _list505 = new TList((byte)12, iprot.readI32());
            struct.events = (List<NotificationEvent>)new ArrayList(_list505.size);
            for (int _i506 = 0; _i506 < _list505.size; ++_i506) {
                final NotificationEvent _elem507 = new NotificationEvent();
                _elem507.read(iprot);
                struct.events.add(_elem507);
            }
            struct.setEventsIsSet(true);
        }
    }
}
