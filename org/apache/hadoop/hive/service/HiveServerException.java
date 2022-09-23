// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.hive.service;

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
import org.apache.thrift.TException;

public class HiveServerException extends TException implements TBase<HiveServerException, _Fields>, Serializable, Cloneable
{
    private static final TStruct STRUCT_DESC;
    private static final TField MESSAGE_FIELD_DESC;
    private static final TField ERROR_CODE_FIELD_DESC;
    private static final TField SQLSTATE_FIELD_DESC;
    private static final Map<Class<? extends IScheme>, SchemeFactory> schemes;
    private String message;
    private int errorCode;
    private String SQLState;
    private static final int __ERRORCODE_ISSET_ID = 0;
    private byte __isset_bitfield;
    public static final Map<_Fields, FieldMetaData> metaDataMap;
    
    public HiveServerException() {
        this.__isset_bitfield = 0;
    }
    
    public HiveServerException(final String message, final int errorCode, final String SQLState) {
        this();
        this.message = message;
        this.errorCode = errorCode;
        this.setErrorCodeIsSet(true);
        this.SQLState = SQLState;
    }
    
    public HiveServerException(final HiveServerException other) {
        this.__isset_bitfield = 0;
        this.__isset_bitfield = other.__isset_bitfield;
        if (other.isSetMessage()) {
            this.message = other.message;
        }
        this.errorCode = other.errorCode;
        if (other.isSetSQLState()) {
            this.SQLState = other.SQLState;
        }
    }
    
    @Override
    public HiveServerException deepCopy() {
        return new HiveServerException(this);
    }
    
    @Override
    public void clear() {
        this.message = null;
        this.setErrorCodeIsSet(false);
        this.errorCode = 0;
        this.SQLState = null;
    }
    
    @Override
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
    
    public String getSQLState() {
        return this.SQLState;
    }
    
    public void setSQLState(final String SQLState) {
        this.SQLState = SQLState;
    }
    
    public void unsetSQLState() {
        this.SQLState = null;
    }
    
    public boolean isSetSQLState() {
        return this.SQLState != null;
    }
    
    public void setSQLStateIsSet(final boolean value) {
        if (!value) {
            this.SQLState = null;
        }
    }
    
    @Override
    public void setFieldValue(final _Fields field, final Object value) {
        switch (field) {
            case MESSAGE: {
                if (value == null) {
                    this.unsetMessage();
                    break;
                }
                this.setMessage((String)value);
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
            case SQLSTATE: {
                if (value == null) {
                    this.unsetSQLState();
                    break;
                }
                this.setSQLState((String)value);
                break;
            }
        }
    }
    
    @Override
    public Object getFieldValue(final _Fields field) {
        switch (field) {
            case MESSAGE: {
                return this.getMessage();
            }
            case ERROR_CODE: {
                return this.getErrorCode();
            }
            case SQLSTATE: {
                return this.getSQLState();
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
            case MESSAGE: {
                return this.isSetMessage();
            }
            case ERROR_CODE: {
                return this.isSetErrorCode();
            }
            case SQLSTATE: {
                return this.isSetSQLState();
            }
            default: {
                throw new IllegalStateException();
            }
        }
    }
    
    @Override
    public boolean equals(final Object that) {
        return that != null && that instanceof HiveServerException && this.equals((HiveServerException)that);
    }
    
    public boolean equals(final HiveServerException that) {
        if (that == null) {
            return false;
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
        final boolean this_present_errorCode = true;
        final boolean that_present_errorCode = true;
        if (this_present_errorCode || that_present_errorCode) {
            if (!this_present_errorCode || !that_present_errorCode) {
                return false;
            }
            if (this.errorCode != that.errorCode) {
                return false;
            }
        }
        final boolean this_present_SQLState = this.isSetSQLState();
        final boolean that_present_SQLState = that.isSetSQLState();
        if (this_present_SQLState || that_present_SQLState) {
            if (!this_present_SQLState || !that_present_SQLState) {
                return false;
            }
            if (!this.SQLState.equals(that.SQLState)) {
                return false;
            }
        }
        return true;
    }
    
    @Override
    public int hashCode() {
        final HashCodeBuilder builder = new HashCodeBuilder();
        final boolean present_message = this.isSetMessage();
        builder.append(present_message);
        if (present_message) {
            builder.append(this.message);
        }
        final boolean present_errorCode = true;
        builder.append(present_errorCode);
        if (present_errorCode) {
            builder.append(this.errorCode);
        }
        final boolean present_SQLState = this.isSetSQLState();
        builder.append(present_SQLState);
        if (present_SQLState) {
            builder.append(this.SQLState);
        }
        return builder.toHashCode();
    }
    
    @Override
    public int compareTo(final HiveServerException other) {
        if (!this.getClass().equals(other.getClass())) {
            return this.getClass().getName().compareTo(other.getClass().getName());
        }
        int lastComparison = 0;
        final HiveServerException typedOther = other;
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
        lastComparison = Boolean.valueOf(this.isSetSQLState()).compareTo(Boolean.valueOf(typedOther.isSetSQLState()));
        if (lastComparison != 0) {
            return lastComparison;
        }
        if (this.isSetSQLState()) {
            lastComparison = TBaseHelper.compareTo(this.SQLState, typedOther.SQLState);
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
        HiveServerException.schemes.get(iprot.getScheme()).getScheme().read(iprot, this);
    }
    
    @Override
    public void write(final TProtocol oprot) throws TException {
        HiveServerException.schemes.get(oprot.getScheme()).getScheme().write(oprot, this);
    }
    
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("HiveServerException(");
        boolean first = true;
        sb.append("message:");
        if (this.message == null) {
            sb.append("null");
        }
        else {
            sb.append(this.message);
        }
        first = false;
        if (!first) {
            sb.append(", ");
        }
        sb.append("errorCode:");
        sb.append(this.errorCode);
        first = false;
        if (!first) {
            sb.append(", ");
        }
        sb.append("SQLState:");
        if (this.SQLState == null) {
            sb.append("null");
        }
        else {
            sb.append(this.SQLState);
        }
        first = false;
        sb.append(")");
        return sb.toString();
    }
    
    public void validate() throws TException {
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
        STRUCT_DESC = new TStruct("HiveServerException");
        MESSAGE_FIELD_DESC = new TField("message", (byte)11, (short)1);
        ERROR_CODE_FIELD_DESC = new TField("errorCode", (byte)8, (short)2);
        SQLSTATE_FIELD_DESC = new TField("SQLState", (byte)11, (short)3);
        (schemes = new HashMap<Class<? extends IScheme>, SchemeFactory>()).put(StandardScheme.class, new HiveServerExceptionStandardSchemeFactory());
        HiveServerException.schemes.put(TupleScheme.class, new HiveServerExceptionTupleSchemeFactory());
        final Map<_Fields, FieldMetaData> tmpMap = new EnumMap<_Fields, FieldMetaData>(_Fields.class);
        tmpMap.put(_Fields.MESSAGE, new FieldMetaData("message", (byte)3, new FieldValueMetaData((byte)11)));
        tmpMap.put(_Fields.ERROR_CODE, new FieldMetaData("errorCode", (byte)3, new FieldValueMetaData((byte)8)));
        tmpMap.put(_Fields.SQLSTATE, new FieldMetaData("SQLState", (byte)3, new FieldValueMetaData((byte)11)));
        FieldMetaData.addStructMetaDataMap(HiveServerException.class, metaDataMap = Collections.unmodifiableMap((Map<? extends _Fields, ? extends FieldMetaData>)tmpMap));
    }
    
    public enum _Fields implements TFieldIdEnum
    {
        MESSAGE((short)1, "message"), 
        ERROR_CODE((short)2, "errorCode"), 
        SQLSTATE((short)3, "SQLState");
        
        private static final Map<String, _Fields> byName;
        private final short _thriftId;
        private final String _fieldName;
        
        public static _Fields findByThriftId(final int fieldId) {
            switch (fieldId) {
                case 1: {
                    return _Fields.MESSAGE;
                }
                case 2: {
                    return _Fields.ERROR_CODE;
                }
                case 3: {
                    return _Fields.SQLSTATE;
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
    
    private static class HiveServerExceptionStandardSchemeFactory implements SchemeFactory
    {
        @Override
        public HiveServerExceptionStandardScheme getScheme() {
            return new HiveServerExceptionStandardScheme();
        }
    }
    
    private static class HiveServerExceptionStandardScheme extends StandardScheme<HiveServerException>
    {
        @Override
        public void read(final TProtocol iprot, final HiveServerException struct) throws TException {
            iprot.readStructBegin();
            while (true) {
                final TField schemeField = iprot.readFieldBegin();
                if (schemeField.type == 0) {
                    break;
                }
                switch (schemeField.id) {
                    case 1: {
                        if (schemeField.type == 11) {
                            struct.message = iprot.readString();
                            struct.setMessageIsSet(true);
                            break;
                        }
                        TProtocolUtil.skip(iprot, schemeField.type);
                        break;
                    }
                    case 2: {
                        if (schemeField.type == 8) {
                            struct.errorCode = iprot.readI32();
                            struct.setErrorCodeIsSet(true);
                            break;
                        }
                        TProtocolUtil.skip(iprot, schemeField.type);
                        break;
                    }
                    case 3: {
                        if (schemeField.type == 11) {
                            struct.SQLState = iprot.readString();
                            struct.setSQLStateIsSet(true);
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
        public void write(final TProtocol oprot, final HiveServerException struct) throws TException {
            struct.validate();
            oprot.writeStructBegin(HiveServerException.STRUCT_DESC);
            if (struct.message != null) {
                oprot.writeFieldBegin(HiveServerException.MESSAGE_FIELD_DESC);
                oprot.writeString(struct.message);
                oprot.writeFieldEnd();
            }
            oprot.writeFieldBegin(HiveServerException.ERROR_CODE_FIELD_DESC);
            oprot.writeI32(struct.errorCode);
            oprot.writeFieldEnd();
            if (struct.SQLState != null) {
                oprot.writeFieldBegin(HiveServerException.SQLSTATE_FIELD_DESC);
                oprot.writeString(struct.SQLState);
                oprot.writeFieldEnd();
            }
            oprot.writeFieldStop();
            oprot.writeStructEnd();
        }
    }
    
    private static class HiveServerExceptionTupleSchemeFactory implements SchemeFactory
    {
        @Override
        public HiveServerExceptionTupleScheme getScheme() {
            return new HiveServerExceptionTupleScheme();
        }
    }
    
    private static class HiveServerExceptionTupleScheme extends TupleScheme<HiveServerException>
    {
        @Override
        public void write(final TProtocol prot, final HiveServerException struct) throws TException {
            final TTupleProtocol oprot = (TTupleProtocol)prot;
            final BitSet optionals = new BitSet();
            if (struct.isSetMessage()) {
                optionals.set(0);
            }
            if (struct.isSetErrorCode()) {
                optionals.set(1);
            }
            if (struct.isSetSQLState()) {
                optionals.set(2);
            }
            oprot.writeBitSet(optionals, 3);
            if (struct.isSetMessage()) {
                oprot.writeString(struct.message);
            }
            if (struct.isSetErrorCode()) {
                oprot.writeI32(struct.errorCode);
            }
            if (struct.isSetSQLState()) {
                oprot.writeString(struct.SQLState);
            }
        }
        
        @Override
        public void read(final TProtocol prot, final HiveServerException struct) throws TException {
            final TTupleProtocol iprot = (TTupleProtocol)prot;
            final BitSet incoming = iprot.readBitSet(3);
            if (incoming.get(0)) {
                struct.message = iprot.readString();
                struct.setMessageIsSet(true);
            }
            if (incoming.get(1)) {
                struct.errorCode = iprot.readI32();
                struct.setErrorCodeIsSet(true);
            }
            if (incoming.get(2)) {
                struct.SQLState = iprot.readString();
                struct.setSQLStateIsSet(true);
            }
        }
    }
}
