// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hive.service.cli.thrift;

import java.util.BitSet;
import org.apache.thrift.protocol.TTupleProtocol;
import org.apache.thrift.protocol.TList;
import org.apache.thrift.protocol.TProtocolUtil;
import java.util.EnumSet;
import java.util.Collections;
import org.apache.thrift.meta_data.ListMetaData;
import org.apache.thrift.meta_data.FieldValueMetaData;
import org.apache.thrift.TEnum;
import org.apache.thrift.meta_data.EnumMetaData;
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

public class TStatus implements TBase<TStatus, _Fields>, Serializable, Cloneable
{
    private static final TStruct STRUCT_DESC;
    private static final TField STATUS_CODE_FIELD_DESC;
    private static final TField INFO_MESSAGES_FIELD_DESC;
    private static final TField SQL_STATE_FIELD_DESC;
    private static final TField ERROR_CODE_FIELD_DESC;
    private static final TField ERROR_MESSAGE_FIELD_DESC;
    private static final Map<Class<? extends IScheme>, SchemeFactory> schemes;
    private TStatusCode statusCode;
    private List<String> infoMessages;
    private String sqlState;
    private int errorCode;
    private String errorMessage;
    private static final int __ERRORCODE_ISSET_ID = 0;
    private byte __isset_bitfield;
    private _Fields[] optionals;
    public static final Map<_Fields, FieldMetaData> metaDataMap;
    
    public TStatus() {
        this.__isset_bitfield = 0;
        this.optionals = new _Fields[] { _Fields.INFO_MESSAGES, _Fields.SQL_STATE, _Fields.ERROR_CODE, _Fields.ERROR_MESSAGE };
    }
    
    public TStatus(final TStatusCode statusCode) {
        this();
        this.statusCode = statusCode;
    }
    
    public TStatus(final TStatus other) {
        this.__isset_bitfield = 0;
        this.optionals = new _Fields[] { _Fields.INFO_MESSAGES, _Fields.SQL_STATE, _Fields.ERROR_CODE, _Fields.ERROR_MESSAGE };
        this.__isset_bitfield = other.__isset_bitfield;
        if (other.isSetStatusCode()) {
            this.statusCode = other.statusCode;
        }
        if (other.isSetInfoMessages()) {
            final List<String> __this__infoMessages = new ArrayList<String>();
            for (final String other_element : other.infoMessages) {
                __this__infoMessages.add(other_element);
            }
            this.infoMessages = __this__infoMessages;
        }
        if (other.isSetSqlState()) {
            this.sqlState = other.sqlState;
        }
        this.errorCode = other.errorCode;
        if (other.isSetErrorMessage()) {
            this.errorMessage = other.errorMessage;
        }
    }
    
    @Override
    public TStatus deepCopy() {
        return new TStatus(this);
    }
    
    @Override
    public void clear() {
        this.statusCode = null;
        this.infoMessages = null;
        this.sqlState = null;
        this.setErrorCodeIsSet(false);
        this.errorCode = 0;
        this.errorMessage = null;
    }
    
    public TStatusCode getStatusCode() {
        return this.statusCode;
    }
    
    public void setStatusCode(final TStatusCode statusCode) {
        this.statusCode = statusCode;
    }
    
    public void unsetStatusCode() {
        this.statusCode = null;
    }
    
    public boolean isSetStatusCode() {
        return this.statusCode != null;
    }
    
    public void setStatusCodeIsSet(final boolean value) {
        if (!value) {
            this.statusCode = null;
        }
    }
    
    public int getInfoMessagesSize() {
        return (this.infoMessages == null) ? 0 : this.infoMessages.size();
    }
    
    public Iterator<String> getInfoMessagesIterator() {
        return (this.infoMessages == null) ? null : this.infoMessages.iterator();
    }
    
    public void addToInfoMessages(final String elem) {
        if (this.infoMessages == null) {
            this.infoMessages = new ArrayList<String>();
        }
        this.infoMessages.add(elem);
    }
    
    public List<String> getInfoMessages() {
        return this.infoMessages;
    }
    
    public void setInfoMessages(final List<String> infoMessages) {
        this.infoMessages = infoMessages;
    }
    
    public void unsetInfoMessages() {
        this.infoMessages = null;
    }
    
    public boolean isSetInfoMessages() {
        return this.infoMessages != null;
    }
    
    public void setInfoMessagesIsSet(final boolean value) {
        if (!value) {
            this.infoMessages = null;
        }
    }
    
    public String getSqlState() {
        return this.sqlState;
    }
    
    public void setSqlState(final String sqlState) {
        this.sqlState = sqlState;
    }
    
    public void unsetSqlState() {
        this.sqlState = null;
    }
    
    public boolean isSetSqlState() {
        return this.sqlState != null;
    }
    
    public void setSqlStateIsSet(final boolean value) {
        if (!value) {
            this.sqlState = null;
        }
    }
    
    public int getErrorCode() {
        return this.errorCode;
    }
    
    public void setErrorCode(final int errorCode) {
        this.errorCode = errorCode;
        this.setErrorCodeIsSet(true);
    }
    
    public void unsetErrorCode() {
        this.__isset_bitfield = EncodingUtils.clearBit(this.__isset_bitfield, 0);
    }
    
    public boolean isSetErrorCode() {
        return EncodingUtils.testBit(this.__isset_bitfield, 0);
    }
    
    public void setErrorCodeIsSet(final boolean value) {
        this.__isset_bitfield = EncodingUtils.setBit(this.__isset_bitfield, 0, value);
    }
    
    public String getErrorMessage() {
        return this.errorMessage;
    }
    
    public void setErrorMessage(final String errorMessage) {
        this.errorMessage = errorMessage;
    }
    
    public void unsetErrorMessage() {
        this.errorMessage = null;
    }
    
    public boolean isSetErrorMessage() {
        return this.errorMessage != null;
    }
    
    public void setErrorMessageIsSet(final boolean value) {
        if (!value) {
            this.errorMessage = null;
        }
    }
    
    @Override
    public void setFieldValue(final _Fields field, final Object value) {
        switch (field) {
            case STATUS_CODE: {
                if (value == null) {
                    this.unsetStatusCode();
                    break;
                }
                this.setStatusCode((TStatusCode)value);
                break;
            }
            case INFO_MESSAGES: {
                if (value == null) {
                    this.unsetInfoMessages();
                    break;
                }
                this.setInfoMessages((List<String>)value);
                break;
            }
            case SQL_STATE: {
                if (value == null) {
                    this.unsetSqlState();
                    break;
                }
                this.setSqlState((String)value);
                break;
            }
            case ERROR_CODE: {
                if (value == null) {
                    this.unsetErrorCode();
                    break;
                }
                this.setErrorCode((int)value);
                break;
            }
            case ERROR_MESSAGE: {
                if (value == null) {
                    this.unsetErrorMessage();
                    break;
                }
                this.setErrorMessage((String)value);
                break;
            }
        }
    }
    
    @Override
    public Object getFieldValue(final _Fields field) {
        switch (field) {
            case STATUS_CODE: {
                return this.getStatusCode();
            }
            case INFO_MESSAGES: {
                return this.getInfoMessages();
            }
            case SQL_STATE: {
                return this.getSqlState();
            }
            case ERROR_CODE: {
                return this.getErrorCode();
            }
            case ERROR_MESSAGE: {
                return this.getErrorMessage();
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
            case STATUS_CODE: {
                return this.isSetStatusCode();
            }
            case INFO_MESSAGES: {
                return this.isSetInfoMessages();
            }
            case SQL_STATE: {
                return this.isSetSqlState();
            }
            case ERROR_CODE: {
                return this.isSetErrorCode();
            }
            case ERROR_MESSAGE: {
                return this.isSetErrorMessage();
            }
            default: {
                throw new IllegalStateException();
            }
        }
    }
    
    @Override
    public boolean equals(final Object that) {
        return that != null && that instanceof TStatus && this.equals((TStatus)that);
    }
    
    public boolean equals(final TStatus that) {
        if (that == null) {
            return false;
        }
        final boolean this_present_statusCode = this.isSetStatusCode();
        final boolean that_present_statusCode = that.isSetStatusCode();
        if (this_present_statusCode || that_present_statusCode) {
            if (!this_present_statusCode || !that_present_statusCode) {
                return false;
            }
            if (!this.statusCode.equals(that.statusCode)) {
                return false;
            }
        }
        final boolean this_present_infoMessages = this.isSetInfoMessages();
        final boolean that_present_infoMessages = that.isSetInfoMessages();
        if (this_present_infoMessages || that_present_infoMessages) {
            if (!this_present_infoMessages || !that_present_infoMessages) {
                return false;
            }
            if (!this.infoMessages.equals(that.infoMessages)) {
                return false;
            }
        }
        final boolean this_present_sqlState = this.isSetSqlState();
        final boolean that_present_sqlState = that.isSetSqlState();
        if (this_present_sqlState || that_present_sqlState) {
            if (!this_present_sqlState || !that_present_sqlState) {
                return false;
            }
            if (!this.sqlState.equals(that.sqlState)) {
                return false;
            }
        }
        final boolean this_present_errorCode = this.isSetErrorCode();
        final boolean that_present_errorCode = that.isSetErrorCode();
        if (this_present_errorCode || that_present_errorCode) {
            if (!this_present_errorCode || !that_present_errorCode) {
                return false;
            }
            if (this.errorCode != that.errorCode) {
                return false;
            }
        }
        final boolean this_present_errorMessage = this.isSetErrorMessage();
        final boolean that_present_errorMessage = that.isSetErrorMessage();
        if (this_present_errorMessage || that_present_errorMessage) {
            if (!this_present_errorMessage || !that_present_errorMessage) {
                return false;
            }
            if (!this.errorMessage.equals(that.errorMessage)) {
                return false;
            }
        }
        return true;
    }
    
    @Override
    public int hashCode() {
        final HashCodeBuilder builder = new HashCodeBuilder();
        final boolean present_statusCode = this.isSetStatusCode();
        builder.append(present_statusCode);
        if (present_statusCode) {
            builder.append(this.statusCode.getValue());
        }
        final boolean present_infoMessages = this.isSetInfoMessages();
        builder.append(present_infoMessages);
        if (present_infoMessages) {
            builder.append(this.infoMessages);
        }
        final boolean present_sqlState = this.isSetSqlState();
        builder.append(present_sqlState);
        if (present_sqlState) {
            builder.append(this.sqlState);
        }
        final boolean present_errorCode = this.isSetErrorCode();
        builder.append(present_errorCode);
        if (present_errorCode) {
            builder.append(this.errorCode);
        }
        final boolean present_errorMessage = this.isSetErrorMessage();
        builder.append(present_errorMessage);
        if (present_errorMessage) {
            builder.append(this.errorMessage);
        }
        return builder.toHashCode();
    }
    
    @Override
    public int compareTo(final TStatus other) {
        if (!this.getClass().equals(other.getClass())) {
            return this.getClass().getName().compareTo(other.getClass().getName());
        }
        int lastComparison = 0;
        final TStatus typedOther = other;
        lastComparison = Boolean.valueOf(this.isSetStatusCode()).compareTo(Boolean.valueOf(typedOther.isSetStatusCode()));
        if (lastComparison != 0) {
            return lastComparison;
        }
        if (this.isSetStatusCode()) {
            lastComparison = TBaseHelper.compareTo(this.statusCode, typedOther.statusCode);
            if (lastComparison != 0) {
                return lastComparison;
            }
        }
        lastComparison = Boolean.valueOf(this.isSetInfoMessages()).compareTo(Boolean.valueOf(typedOther.isSetInfoMessages()));
        if (lastComparison != 0) {
            return lastComparison;
        }
        if (this.isSetInfoMessages()) {
            lastComparison = TBaseHelper.compareTo(this.infoMessages, typedOther.infoMessages);
            if (lastComparison != 0) {
                return lastComparison;
            }
        }
        lastComparison = Boolean.valueOf(this.isSetSqlState()).compareTo(Boolean.valueOf(typedOther.isSetSqlState()));
        if (lastComparison != 0) {
            return lastComparison;
        }
        if (this.isSetSqlState()) {
            lastComparison = TBaseHelper.compareTo(this.sqlState, typedOther.sqlState);
            if (lastComparison != 0) {
                return lastComparison;
            }
        }
        lastComparison = Boolean.valueOf(this.isSetErrorCode()).compareTo(Boolean.valueOf(typedOther.isSetErrorCode()));
        if (lastComparison != 0) {
            return lastComparison;
        }
        if (this.isSetErrorCode()) {
            lastComparison = TBaseHelper.compareTo(this.errorCode, typedOther.errorCode);
            if (lastComparison != 0) {
                return lastComparison;
            }
        }
        lastComparison = Boolean.valueOf(this.isSetErrorMessage()).compareTo(Boolean.valueOf(typedOther.isSetErrorMessage()));
        if (lastComparison != 0) {
            return lastComparison;
        }
        if (this.isSetErrorMessage()) {
            lastComparison = TBaseHelper.compareTo(this.errorMessage, typedOther.errorMessage);
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
        TStatus.schemes.get(iprot.getScheme()).getScheme().read(iprot, this);
    }
    
    @Override
    public void write(final TProtocol oprot) throws TException {
        TStatus.schemes.get(oprot.getScheme()).getScheme().write(oprot, this);
    }
    
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("TStatus(");
        boolean first = true;
        sb.append("statusCode:");
        if (this.statusCode == null) {
            sb.append("null");
        }
        else {
            sb.append(this.statusCode);
        }
        first = false;
        if (this.isSetInfoMessages()) {
            if (!first) {
                sb.append(", ");
            }
            sb.append("infoMessages:");
            if (this.infoMessages == null) {
                sb.append("null");
            }
            else {
                sb.append(this.infoMessages);
            }
            first = false;
        }
        if (this.isSetSqlState()) {
            if (!first) {
                sb.append(", ");
            }
            sb.append("sqlState:");
            if (this.sqlState == null) {
                sb.append("null");
            }
            else {
                sb.append(this.sqlState);
            }
            first = false;
        }
        if (this.isSetErrorCode()) {
            if (!first) {
                sb.append(", ");
            }
            sb.append("errorCode:");
            sb.append(this.errorCode);
            first = false;
        }
        if (this.isSetErrorMessage()) {
            if (!first) {
                sb.append(", ");
            }
            sb.append("errorMessage:");
            if (this.errorMessage == null) {
                sb.append("null");
            }
            else {
                sb.append(this.errorMessage);
            }
            first = false;
        }
        sb.append(")");
        return sb.toString();
    }
    
    public void validate() throws TException {
        if (!this.isSetStatusCode()) {
            throw new TProtocolException("Required field 'statusCode' is unset! Struct:" + this.toString());
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
        STRUCT_DESC = new TStruct("TStatus");
        STATUS_CODE_FIELD_DESC = new TField("statusCode", (byte)8, (short)1);
        INFO_MESSAGES_FIELD_DESC = new TField("infoMessages", (byte)15, (short)2);
        SQL_STATE_FIELD_DESC = new TField("sqlState", (byte)11, (short)3);
        ERROR_CODE_FIELD_DESC = new TField("errorCode", (byte)8, (short)4);
        ERROR_MESSAGE_FIELD_DESC = new TField("errorMessage", (byte)11, (short)5);
        (schemes = new HashMap<Class<? extends IScheme>, SchemeFactory>()).put(StandardScheme.class, new TStatusStandardSchemeFactory());
        TStatus.schemes.put(TupleScheme.class, new TStatusTupleSchemeFactory());
        final Map<_Fields, FieldMetaData> tmpMap = new EnumMap<_Fields, FieldMetaData>(_Fields.class);
        tmpMap.put(_Fields.STATUS_CODE, new FieldMetaData("statusCode", (byte)1, new EnumMetaData((byte)16, TStatusCode.class)));
        tmpMap.put(_Fields.INFO_MESSAGES, new FieldMetaData("infoMessages", (byte)2, new ListMetaData((byte)15, new FieldValueMetaData((byte)11))));
        tmpMap.put(_Fields.SQL_STATE, new FieldMetaData("sqlState", (byte)2, new FieldValueMetaData((byte)11)));
        tmpMap.put(_Fields.ERROR_CODE, new FieldMetaData("errorCode", (byte)2, new FieldValueMetaData((byte)8)));
        tmpMap.put(_Fields.ERROR_MESSAGE, new FieldMetaData("errorMessage", (byte)2, new FieldValueMetaData((byte)11)));
        FieldMetaData.addStructMetaDataMap(TStatus.class, metaDataMap = Collections.unmodifiableMap((Map<? extends _Fields, ? extends FieldMetaData>)tmpMap));
    }
    
    public enum _Fields implements TFieldIdEnum
    {
        STATUS_CODE((short)1, "statusCode"), 
        INFO_MESSAGES((short)2, "infoMessages"), 
        SQL_STATE((short)3, "sqlState"), 
        ERROR_CODE((short)4, "errorCode"), 
        ERROR_MESSAGE((short)5, "errorMessage");
        
        private static final Map<String, _Fields> byName;
        private final short _thriftId;
        private final String _fieldName;
        
        public static _Fields findByThriftId(final int fieldId) {
            switch (fieldId) {
                case 1: {
                    return _Fields.STATUS_CODE;
                }
                case 2: {
                    return _Fields.INFO_MESSAGES;
                }
                case 3: {
                    return _Fields.SQL_STATE;
                }
                case 4: {
                    return _Fields.ERROR_CODE;
                }
                case 5: {
                    return _Fields.ERROR_MESSAGE;
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
    
    private static class TStatusStandardSchemeFactory implements SchemeFactory
    {
        @Override
        public TStatusStandardScheme getScheme() {
            return new TStatusStandardScheme();
        }
    }
    
    private static class TStatusStandardScheme extends StandardScheme<TStatus>
    {
        @Override
        public void read(final TProtocol iprot, final TStatus struct) throws TException {
            iprot.readStructBegin();
            while (true) {
                final TField schemeField = iprot.readFieldBegin();
                if (schemeField.type == 0) {
                    break;
                }
                switch (schemeField.id) {
                    case 1: {
                        if (schemeField.type == 8) {
                            struct.statusCode = TStatusCode.findByValue(iprot.readI32());
                            struct.setStatusCodeIsSet(true);
                            break;
                        }
                        TProtocolUtil.skip(iprot, schemeField.type);
                        break;
                    }
                    case 2: {
                        if (schemeField.type == 15) {
                            final TList _list134 = iprot.readListBegin();
                            struct.infoMessages = (List<String>)new ArrayList(_list134.size);
                            for (int _i135 = 0; _i135 < _list134.size; ++_i135) {
                                final String _elem136 = iprot.readString();
                                struct.infoMessages.add(_elem136);
                            }
                            iprot.readListEnd();
                            struct.setInfoMessagesIsSet(true);
                            break;
                        }
                        TProtocolUtil.skip(iprot, schemeField.type);
                        break;
                    }
                    case 3: {
                        if (schemeField.type == 11) {
                            struct.sqlState = iprot.readString();
                            struct.setSqlStateIsSet(true);
                            break;
                        }
                        TProtocolUtil.skip(iprot, schemeField.type);
                        break;
                    }
                    case 4: {
                        if (schemeField.type == 8) {
                            struct.errorCode = iprot.readI32();
                            struct.setErrorCodeIsSet(true);
                            break;
                        }
                        TProtocolUtil.skip(iprot, schemeField.type);
                        break;
                    }
                    case 5: {
                        if (schemeField.type == 11) {
                            struct.errorMessage = iprot.readString();
                            struct.setErrorMessageIsSet(true);
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
        public void write(final TProtocol oprot, final TStatus struct) throws TException {
            struct.validate();
            oprot.writeStructBegin(TStatus.STRUCT_DESC);
            if (struct.statusCode != null) {
                oprot.writeFieldBegin(TStatus.STATUS_CODE_FIELD_DESC);
                oprot.writeI32(struct.statusCode.getValue());
                oprot.writeFieldEnd();
            }
            if (struct.infoMessages != null && struct.isSetInfoMessages()) {
                oprot.writeFieldBegin(TStatus.INFO_MESSAGES_FIELD_DESC);
                oprot.writeListBegin(new TList((byte)11, struct.infoMessages.size()));
                for (final String _iter137 : struct.infoMessages) {
                    oprot.writeString(_iter137);
                }
                oprot.writeListEnd();
                oprot.writeFieldEnd();
            }
            if (struct.sqlState != null && struct.isSetSqlState()) {
                oprot.writeFieldBegin(TStatus.SQL_STATE_FIELD_DESC);
                oprot.writeString(struct.sqlState);
                oprot.writeFieldEnd();
            }
            if (struct.isSetErrorCode()) {
                oprot.writeFieldBegin(TStatus.ERROR_CODE_FIELD_DESC);
                oprot.writeI32(struct.errorCode);
                oprot.writeFieldEnd();
            }
            if (struct.errorMessage != null && struct.isSetErrorMessage()) {
                oprot.writeFieldBegin(TStatus.ERROR_MESSAGE_FIELD_DESC);
                oprot.writeString(struct.errorMessage);
                oprot.writeFieldEnd();
            }
            oprot.writeFieldStop();
            oprot.writeStructEnd();
        }
    }
    
    private static class TStatusTupleSchemeFactory implements SchemeFactory
    {
        @Override
        public TStatusTupleScheme getScheme() {
            return new TStatusTupleScheme();
        }
    }
    
    private static class TStatusTupleScheme extends TupleScheme<TStatus>
    {
        @Override
        public void write(final TProtocol prot, final TStatus struct) throws TException {
            final TTupleProtocol oprot = (TTupleProtocol)prot;
            oprot.writeI32(struct.statusCode.getValue());
            final BitSet optionals = new BitSet();
            if (struct.isSetInfoMessages()) {
                optionals.set(0);
            }
            if (struct.isSetSqlState()) {
                optionals.set(1);
            }
            if (struct.isSetErrorCode()) {
                optionals.set(2);
            }
            if (struct.isSetErrorMessage()) {
                optionals.set(3);
            }
            oprot.writeBitSet(optionals, 4);
            if (struct.isSetInfoMessages()) {
                oprot.writeI32(struct.infoMessages.size());
                for (final String _iter138 : struct.infoMessages) {
                    oprot.writeString(_iter138);
                }
            }
            if (struct.isSetSqlState()) {
                oprot.writeString(struct.sqlState);
            }
            if (struct.isSetErrorCode()) {
                oprot.writeI32(struct.errorCode);
            }
            if (struct.isSetErrorMessage()) {
                oprot.writeString(struct.errorMessage);
            }
        }
        
        @Override
        public void read(final TProtocol prot, final TStatus struct) throws TException {
            final TTupleProtocol iprot = (TTupleProtocol)prot;
            struct.statusCode = TStatusCode.findByValue(iprot.readI32());
            struct.setStatusCodeIsSet(true);
            final BitSet incoming = iprot.readBitSet(4);
            if (incoming.get(0)) {
                final TList _list139 = new TList((byte)11, iprot.readI32());
                struct.infoMessages = (List<String>)new ArrayList(_list139.size);
                for (int _i140 = 0; _i140 < _list139.size; ++_i140) {
                    final String _elem141 = iprot.readString();
                    struct.infoMessages.add(_elem141);
                }
                struct.setInfoMessagesIsSet(true);
            }
            if (incoming.get(1)) {
                struct.sqlState = iprot.readString();
                struct.setSqlStateIsSet(true);
            }
            if (incoming.get(2)) {
                struct.errorCode = iprot.readI32();
                struct.setErrorCodeIsSet(true);
            }
            if (incoming.get(3)) {
                struct.errorMessage = iprot.readString();
                struct.setErrorMessageIsSet(true);
            }
        }
    }
}
