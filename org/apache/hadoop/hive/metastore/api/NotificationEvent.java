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

public class NotificationEvent implements TBase<NotificationEvent, _Fields>, Serializable, Cloneable
{
    private static final TStruct STRUCT_DESC;
    private static final TField EVENT_ID_FIELD_DESC;
    private static final TField EVENT_TIME_FIELD_DESC;
    private static final TField EVENT_TYPE_FIELD_DESC;
    private static final TField DB_NAME_FIELD_DESC;
    private static final TField TABLE_NAME_FIELD_DESC;
    private static final TField MESSAGE_FIELD_DESC;
    private static final Map<Class<? extends IScheme>, SchemeFactory> schemes;
    private long eventId;
    private int eventTime;
    private String eventType;
    private String dbName;
    private String tableName;
    private String message;
    private static final int __EVENTID_ISSET_ID = 0;
    private static final int __EVENTTIME_ISSET_ID = 1;
    private byte __isset_bitfield;
    private _Fields[] optionals;
    public static final Map<_Fields, FieldMetaData> metaDataMap;
    
    public NotificationEvent() {
        this.__isset_bitfield = 0;
        this.optionals = new _Fields[] { _Fields.DB_NAME, _Fields.TABLE_NAME };
    }
    
    public NotificationEvent(final long eventId, final int eventTime, final String eventType, final String message) {
        this();
        this.eventId = eventId;
        this.setEventIdIsSet(true);
        this.eventTime = eventTime;
        this.setEventTimeIsSet(true);
        this.eventType = eventType;
        this.message = message;
    }
    
    public NotificationEvent(final NotificationEvent other) {
        this.__isset_bitfield = 0;
        this.optionals = new _Fields[] { _Fields.DB_NAME, _Fields.TABLE_NAME };
        this.__isset_bitfield = other.__isset_bitfield;
        this.eventId = other.eventId;
        this.eventTime = other.eventTime;
        if (other.isSetEventType()) {
            this.eventType = other.eventType;
        }
        if (other.isSetDbName()) {
            this.dbName = other.dbName;
        }
        if (other.isSetTableName()) {
            this.tableName = other.tableName;
        }
        if (other.isSetMessage()) {
            this.message = other.message;
        }
    }
    
    @Override
    public NotificationEvent deepCopy() {
        return new NotificationEvent(this);
    }
    
    @Override
    public void clear() {
        this.setEventIdIsSet(false);
        this.eventId = 0L;
        this.setEventTimeIsSet(false);
        this.eventTime = 0;
        this.eventType = null;
        this.dbName = null;
        this.tableName = null;
        this.message = null;
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
    
    public int getEventTime() {
        return this.eventTime;
    }
    
    public void setEventTime(final int eventTime) {
        this.eventTime = eventTime;
        this.setEventTimeIsSet(true);
    }
    
    public void unsetEventTime() {
        this.__isset_bitfield = EncodingUtils.clearBit(this.__isset_bitfield, 1);
    }
    
    public boolean isSetEventTime() {
        return EncodingUtils.testBit(this.__isset_bitfield, 1);
    }
    
    public void setEventTimeIsSet(final boolean value) {
        this.__isset_bitfield = EncodingUtils.setBit(this.__isset_bitfield, 1, value);
    }
    
    public String getEventType() {
        return this.eventType;
    }
    
    public void setEventType(final String eventType) {
        this.eventType = eventType;
    }
    
    public void unsetEventType() {
        this.eventType = null;
    }
    
    public boolean isSetEventType() {
        return this.eventType != null;
    }
    
    public void setEventTypeIsSet(final boolean value) {
        if (!value) {
            this.eventType = null;
        }
    }
    
    public String getDbName() {
        return this.dbName;
    }
    
    public void setDbName(final String dbName) {
        this.dbName = dbName;
    }
    
    public void unsetDbName() {
        this.dbName = null;
    }
    
    public boolean isSetDbName() {
        return this.dbName != null;
    }
    
    public void setDbNameIsSet(final boolean value) {
        if (!value) {
            this.dbName = null;
        }
    }
    
    public String getTableName() {
        return this.tableName;
    }
    
    public void setTableName(final String tableName) {
        this.tableName = tableName;
    }
    
    public void unsetTableName() {
        this.tableName = null;
    }
    
    public boolean isSetTableName() {
        return this.tableName != null;
    }
    
    public void setTableNameIsSet(final boolean value) {
        if (!value) {
            this.tableName = null;
        }
    }
    
    public String getMessage() {
        return this.message;
    }
    
    public void setMessage(final String message) {
        this.message = message;
    }
    
    public void unsetMessage() {
        this.message = null;
    }
    
    public boolean isSetMessage() {
        return this.message != null;
    }
    
    public void setMessageIsSet(final boolean value) {
        if (!value) {
            this.message = null;
        }
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
            case EVENT_TIME: {
                if (value == null) {
                    this.unsetEventTime();
                    break;
                }
                this.setEventTime((int)value);
                break;
            }
            case EVENT_TYPE: {
                if (value == null) {
                    this.unsetEventType();
                    break;
                }
                this.setEventType((String)value);
                break;
            }
            case DB_NAME: {
                if (value == null) {
                    this.unsetDbName();
                    break;
                }
                this.setDbName((String)value);
                break;
            }
            case TABLE_NAME: {
                if (value == null) {
                    this.unsetTableName();
                    break;
                }
                this.setTableName((String)value);
                break;
            }
            case MESSAGE: {
                if (value == null) {
                    this.unsetMessage();
                    break;
                }
                this.setMessage((String)value);
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
            case EVENT_TIME: {
                return this.getEventTime();
            }
            case EVENT_TYPE: {
                return this.getEventType();
            }
            case DB_NAME: {
                return this.getDbName();
            }
            case TABLE_NAME: {
                return this.getTableName();
            }
            case MESSAGE: {
                return this.getMessage();
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
            case EVENT_TIME: {
                return this.isSetEventTime();
            }
            case EVENT_TYPE: {
                return this.isSetEventType();
            }
            case DB_NAME: {
                return this.isSetDbName();
            }
            case TABLE_NAME: {
                return this.isSetTableName();
            }
            case MESSAGE: {
                return this.isSetMessage();
            }
            default: {
                throw new IllegalStateException();
            }
        }
    }
    
    @Override
    public boolean equals(final Object that) {
        return that != null && that instanceof NotificationEvent && this.equals((NotificationEvent)that);
    }
    
    public boolean equals(final NotificationEvent that) {
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
        final boolean this_present_eventTime = true;
        final boolean that_present_eventTime = true;
        if (this_present_eventTime || that_present_eventTime) {
            if (!this_present_eventTime || !that_present_eventTime) {
                return false;
            }
            if (this.eventTime != that.eventTime) {
                return false;
            }
        }
        final boolean this_present_eventType = this.isSetEventType();
        final boolean that_present_eventType = that.isSetEventType();
        if (this_present_eventType || that_present_eventType) {
            if (!this_present_eventType || !that_present_eventType) {
                return false;
            }
            if (!this.eventType.equals(that.eventType)) {
                return false;
            }
        }
        final boolean this_present_dbName = this.isSetDbName();
        final boolean that_present_dbName = that.isSetDbName();
        if (this_present_dbName || that_present_dbName) {
            if (!this_present_dbName || !that_present_dbName) {
                return false;
            }
            if (!this.dbName.equals(that.dbName)) {
                return false;
            }
        }
        final boolean this_present_tableName = this.isSetTableName();
        final boolean that_present_tableName = that.isSetTableName();
        if (this_present_tableName || that_present_tableName) {
            if (!this_present_tableName || !that_present_tableName) {
                return false;
            }
            if (!this.tableName.equals(that.tableName)) {
                return false;
            }
        }
        final boolean this_present_message = this.isSetMessage();
        final boolean that_present_message = that.isSetMessage();
        if (this_present_message || that_present_message) {
            if (!this_present_message || !that_present_message) {
                return false;
            }
            if (!this.message.equals(that.message)) {
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
        final boolean present_eventTime = true;
        builder.append(present_eventTime);
        if (present_eventTime) {
            builder.append(this.eventTime);
        }
        final boolean present_eventType = this.isSetEventType();
        builder.append(present_eventType);
        if (present_eventType) {
            builder.append(this.eventType);
        }
        final boolean present_dbName = this.isSetDbName();
        builder.append(present_dbName);
        if (present_dbName) {
            builder.append(this.dbName);
        }
        final boolean present_tableName = this.isSetTableName();
        builder.append(present_tableName);
        if (present_tableName) {
            builder.append(this.tableName);
        }
        final boolean present_message = this.isSetMessage();
        builder.append(present_message);
        if (present_message) {
            builder.append(this.message);
        }
        return builder.toHashCode();
    }
    
    @Override
    public int compareTo(final NotificationEvent other) {
        if (!this.getClass().equals(other.getClass())) {
            return this.getClass().getName().compareTo(other.getClass().getName());
        }
        int lastComparison = 0;
        final NotificationEvent typedOther = other;
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
        lastComparison = Boolean.valueOf(this.isSetEventTime()).compareTo(Boolean.valueOf(typedOther.isSetEventTime()));
        if (lastComparison != 0) {
            return lastComparison;
        }
        if (this.isSetEventTime()) {
            lastComparison = TBaseHelper.compareTo(this.eventTime, typedOther.eventTime);
            if (lastComparison != 0) {
                return lastComparison;
            }
        }
        lastComparison = Boolean.valueOf(this.isSetEventType()).compareTo(Boolean.valueOf(typedOther.isSetEventType()));
        if (lastComparison != 0) {
            return lastComparison;
        }
        if (this.isSetEventType()) {
            lastComparison = TBaseHelper.compareTo(this.eventType, typedOther.eventType);
            if (lastComparison != 0) {
                return lastComparison;
            }
        }
        lastComparison = Boolean.valueOf(this.isSetDbName()).compareTo(Boolean.valueOf(typedOther.isSetDbName()));
        if (lastComparison != 0) {
            return lastComparison;
        }
        if (this.isSetDbName()) {
            lastComparison = TBaseHelper.compareTo(this.dbName, typedOther.dbName);
            if (lastComparison != 0) {
                return lastComparison;
            }
        }
        lastComparison = Boolean.valueOf(this.isSetTableName()).compareTo(Boolean.valueOf(typedOther.isSetTableName()));
        if (lastComparison != 0) {
            return lastComparison;
        }
        if (this.isSetTableName()) {
            lastComparison = TBaseHelper.compareTo(this.tableName, typedOther.tableName);
            if (lastComparison != 0) {
                return lastComparison;
            }
        }
        lastComparison = Boolean.valueOf(this.isSetMessage()).compareTo(Boolean.valueOf(typedOther.isSetMessage()));
        if (lastComparison != 0) {
            return lastComparison;
        }
        if (this.isSetMessage()) {
            lastComparison = TBaseHelper.compareTo(this.message, typedOther.message);
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
        NotificationEvent.schemes.get(iprot.getScheme()).getScheme().read(iprot, this);
    }
    
    @Override
    public void write(final TProtocol oprot) throws TException {
        NotificationEvent.schemes.get(oprot.getScheme()).getScheme().write(oprot, this);
    }
    
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("NotificationEvent(");
        boolean first = true;
        sb.append("eventId:");
        sb.append(this.eventId);
        first = false;
        if (!first) {
            sb.append(", ");
        }
        sb.append("eventTime:");
        sb.append(this.eventTime);
        first = false;
        if (!first) {
            sb.append(", ");
        }
        sb.append("eventType:");
        if (this.eventType == null) {
            sb.append("null");
        }
        else {
            sb.append(this.eventType);
        }
        first = false;
        if (this.isSetDbName()) {
            if (!first) {
                sb.append(", ");
            }
            sb.append("dbName:");
            if (this.dbName == null) {
                sb.append("null");
            }
            else {
                sb.append(this.dbName);
            }
            first = false;
        }
        if (this.isSetTableName()) {
            if (!first) {
                sb.append(", ");
            }
            sb.append("tableName:");
            if (this.tableName == null) {
                sb.append("null");
            }
            else {
                sb.append(this.tableName);
            }
            first = false;
        }
        if (!first) {
            sb.append(", ");
        }
        sb.append("message:");
        if (this.message == null) {
            sb.append("null");
        }
        else {
            sb.append(this.message);
        }
        first = false;
        sb.append(")");
        return sb.toString();
    }
    
    public void validate() throws TException {
        if (!this.isSetEventId()) {
            throw new TProtocolException("Required field 'eventId' is unset! Struct:" + this.toString());
        }
        if (!this.isSetEventTime()) {
            throw new TProtocolException("Required field 'eventTime' is unset! Struct:" + this.toString());
        }
        if (!this.isSetEventType()) {
            throw new TProtocolException("Required field 'eventType' is unset! Struct:" + this.toString());
        }
        if (!this.isSetMessage()) {
            throw new TProtocolException("Required field 'message' is unset! Struct:" + this.toString());
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
        STRUCT_DESC = new TStruct("NotificationEvent");
        EVENT_ID_FIELD_DESC = new TField("eventId", (byte)10, (short)1);
        EVENT_TIME_FIELD_DESC = new TField("eventTime", (byte)8, (short)2);
        EVENT_TYPE_FIELD_DESC = new TField("eventType", (byte)11, (short)3);
        DB_NAME_FIELD_DESC = new TField("dbName", (byte)11, (short)4);
        TABLE_NAME_FIELD_DESC = new TField("tableName", (byte)11, (short)5);
        MESSAGE_FIELD_DESC = new TField("message", (byte)11, (short)6);
        (schemes = new HashMap<Class<? extends IScheme>, SchemeFactory>()).put(StandardScheme.class, new NotificationEventStandardSchemeFactory());
        NotificationEvent.schemes.put(TupleScheme.class, new NotificationEventTupleSchemeFactory());
        final Map<_Fields, FieldMetaData> tmpMap = new EnumMap<_Fields, FieldMetaData>(_Fields.class);
        tmpMap.put(_Fields.EVENT_ID, new FieldMetaData("eventId", (byte)1, new FieldValueMetaData((byte)10)));
        tmpMap.put(_Fields.EVENT_TIME, new FieldMetaData("eventTime", (byte)1, new FieldValueMetaData((byte)8)));
        tmpMap.put(_Fields.EVENT_TYPE, new FieldMetaData("eventType", (byte)1, new FieldValueMetaData((byte)11)));
        tmpMap.put(_Fields.DB_NAME, new FieldMetaData("dbName", (byte)2, new FieldValueMetaData((byte)11)));
        tmpMap.put(_Fields.TABLE_NAME, new FieldMetaData("tableName", (byte)2, new FieldValueMetaData((byte)11)));
        tmpMap.put(_Fields.MESSAGE, new FieldMetaData("message", (byte)1, new FieldValueMetaData((byte)11)));
        FieldMetaData.addStructMetaDataMap(NotificationEvent.class, metaDataMap = Collections.unmodifiableMap((Map<? extends _Fields, ? extends FieldMetaData>)tmpMap));
    }
    
    public enum _Fields implements TFieldIdEnum
    {
        EVENT_ID((short)1, "eventId"), 
        EVENT_TIME((short)2, "eventTime"), 
        EVENT_TYPE((short)3, "eventType"), 
        DB_NAME((short)4, "dbName"), 
        TABLE_NAME((short)5, "tableName"), 
        MESSAGE((short)6, "message");
        
        private static final Map<String, _Fields> byName;
        private final short _thriftId;
        private final String _fieldName;
        
        public static _Fields findByThriftId(final int fieldId) {
            switch (fieldId) {
                case 1: {
                    return _Fields.EVENT_ID;
                }
                case 2: {
                    return _Fields.EVENT_TIME;
                }
                case 3: {
                    return _Fields.EVENT_TYPE;
                }
                case 4: {
                    return _Fields.DB_NAME;
                }
                case 5: {
                    return _Fields.TABLE_NAME;
                }
                case 6: {
                    return _Fields.MESSAGE;
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
    
    private static class NotificationEventStandardSchemeFactory implements SchemeFactory
    {
        @Override
        public NotificationEventStandardScheme getScheme() {
            return new NotificationEventStandardScheme();
        }
    }
    
    private static class NotificationEventStandardScheme extends StandardScheme<NotificationEvent>
    {
        @Override
        public void read(final TProtocol iprot, final NotificationEvent struct) throws TException {
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
                    case 2: {
                        if (schemeField.type == 8) {
                            struct.eventTime = iprot.readI32();
                            struct.setEventTimeIsSet(true);
                            break;
                        }
                        TProtocolUtil.skip(iprot, schemeField.type);
                        break;
                    }
                    case 3: {
                        if (schemeField.type == 11) {
                            struct.eventType = iprot.readString();
                            struct.setEventTypeIsSet(true);
                            break;
                        }
                        TProtocolUtil.skip(iprot, schemeField.type);
                        break;
                    }
                    case 4: {
                        if (schemeField.type == 11) {
                            struct.dbName = iprot.readString();
                            struct.setDbNameIsSet(true);
                            break;
                        }
                        TProtocolUtil.skip(iprot, schemeField.type);
                        break;
                    }
                    case 5: {
                        if (schemeField.type == 11) {
                            struct.tableName = iprot.readString();
                            struct.setTableNameIsSet(true);
                            break;
                        }
                        TProtocolUtil.skip(iprot, schemeField.type);
                        break;
                    }
                    case 6: {
                        if (schemeField.type == 11) {
                            struct.message = iprot.readString();
                            struct.setMessageIsSet(true);
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
        public void write(final TProtocol oprot, final NotificationEvent struct) throws TException {
            struct.validate();
            oprot.writeStructBegin(NotificationEvent.STRUCT_DESC);
            oprot.writeFieldBegin(NotificationEvent.EVENT_ID_FIELD_DESC);
            oprot.writeI64(struct.eventId);
            oprot.writeFieldEnd();
            oprot.writeFieldBegin(NotificationEvent.EVENT_TIME_FIELD_DESC);
            oprot.writeI32(struct.eventTime);
            oprot.writeFieldEnd();
            if (struct.eventType != null) {
                oprot.writeFieldBegin(NotificationEvent.EVENT_TYPE_FIELD_DESC);
                oprot.writeString(struct.eventType);
                oprot.writeFieldEnd();
            }
            if (struct.dbName != null && struct.isSetDbName()) {
                oprot.writeFieldBegin(NotificationEvent.DB_NAME_FIELD_DESC);
                oprot.writeString(struct.dbName);
                oprot.writeFieldEnd();
            }
            if (struct.tableName != null && struct.isSetTableName()) {
                oprot.writeFieldBegin(NotificationEvent.TABLE_NAME_FIELD_DESC);
                oprot.writeString(struct.tableName);
                oprot.writeFieldEnd();
            }
            if (struct.message != null) {
                oprot.writeFieldBegin(NotificationEvent.MESSAGE_FIELD_DESC);
                oprot.writeString(struct.message);
                oprot.writeFieldEnd();
            }
            oprot.writeFieldStop();
            oprot.writeStructEnd();
        }
    }
    
    private static class NotificationEventTupleSchemeFactory implements SchemeFactory
    {
        @Override
        public NotificationEventTupleScheme getScheme() {
            return new NotificationEventTupleScheme();
        }
    }
    
    private static class NotificationEventTupleScheme extends TupleScheme<NotificationEvent>
    {
        @Override
        public void write(final TProtocol prot, final NotificationEvent struct) throws TException {
            final TTupleProtocol oprot = (TTupleProtocol)prot;
            oprot.writeI64(struct.eventId);
            oprot.writeI32(struct.eventTime);
            oprot.writeString(struct.eventType);
            oprot.writeString(struct.message);
            final BitSet optionals = new BitSet();
            if (struct.isSetDbName()) {
                optionals.set(0);
            }
            if (struct.isSetTableName()) {
                optionals.set(1);
            }
            oprot.writeBitSet(optionals, 2);
            if (struct.isSetDbName()) {
                oprot.writeString(struct.dbName);
            }
            if (struct.isSetTableName()) {
                oprot.writeString(struct.tableName);
            }
        }
        
        @Override
        public void read(final TProtocol prot, final NotificationEvent struct) throws TException {
            final TTupleProtocol iprot = (TTupleProtocol)prot;
            struct.eventId = iprot.readI64();
            struct.setEventIdIsSet(true);
            struct.eventTime = iprot.readI32();
            struct.setEventTimeIsSet(true);
            struct.eventType = iprot.readString();
            struct.setEventTypeIsSet(true);
            struct.message = iprot.readString();
            struct.setMessageIsSet(true);
            final BitSet incoming = iprot.readBitSet(2);
            if (incoming.get(0)) {
                struct.dbName = iprot.readString();
                struct.setDbNameIsSet(true);
            }
            if (incoming.get(1)) {
                struct.tableName = iprot.readString();
                struct.setTableNameIsSet(true);
            }
        }
    }
}
