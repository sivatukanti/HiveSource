// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hive.service.cli.thrift;

import java.util.BitSet;
import org.apache.thrift.protocol.TTupleProtocol;
import org.apache.thrift.protocol.TProtocolUtil;
import java.util.Iterator;
import java.util.EnumSet;
import java.util.Collections;
import org.apache.thrift.TEnum;
import org.apache.thrift.meta_data.EnumMetaData;
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
import org.apache.thrift.EncodingUtils;
import org.apache.thrift.meta_data.FieldMetaData;
import org.apache.thrift.scheme.SchemeFactory;
import org.apache.thrift.scheme.IScheme;
import java.util.Map;
import org.apache.thrift.protocol.TField;
import org.apache.thrift.protocol.TStruct;
import java.io.Serializable;
import org.apache.thrift.TBase;

public class TGetOperationStatusResp implements TBase<TGetOperationStatusResp, _Fields>, Serializable, Cloneable
{
    private static final TStruct STRUCT_DESC;
    private static final TField STATUS_FIELD_DESC;
    private static final TField OPERATION_STATE_FIELD_DESC;
    private static final TField SQL_STATE_FIELD_DESC;
    private static final TField ERROR_CODE_FIELD_DESC;
    private static final TField ERROR_MESSAGE_FIELD_DESC;
    private static final Map<Class<? extends IScheme>, SchemeFactory> schemes;
    private TStatus status;
    private TOperationState operationState;
    private String sqlState;
    private int errorCode;
    private String errorMessage;
    private static final int __ERRORCODE_ISSET_ID = 0;
    private byte __isset_bitfield;
    private _Fields[] optionals;
    public static final Map<_Fields, FieldMetaData> metaDataMap;
    
    public TGetOperationStatusResp() {
        this.__isset_bitfield = 0;
        this.optionals = new _Fields[] { _Fields.OPERATION_STATE, _Fields.SQL_STATE, _Fields.ERROR_CODE, _Fields.ERROR_MESSAGE };
    }
    
    public TGetOperationStatusResp(final TStatus status) {
        this();
        this.status = status;
    }
    
    public TGetOperationStatusResp(final TGetOperationStatusResp other) {
        this.__isset_bitfield = 0;
        this.optionals = new _Fields[] { _Fields.OPERATION_STATE, _Fields.SQL_STATE, _Fields.ERROR_CODE, _Fields.ERROR_MESSAGE };
        this.__isset_bitfield = other.__isset_bitfield;
        if (other.isSetStatus()) {
            this.status = new TStatus(other.status);
        }
        if (other.isSetOperationState()) {
            this.operationState = other.operationState;
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
    public TGetOperationStatusResp deepCopy() {
        return new TGetOperationStatusResp(this);
    }
    
    @Override
    public void clear() {
        this.status = null;
        this.operationState = null;
        this.sqlState = null;
        this.setErrorCodeIsSet(false);
        this.errorCode = 0;
        this.errorMessage = null;
    }
    
    public TStatus getStatus() {
        return this.status;
    }
    
    public void setStatus(final TStatus status) {
        this.status = status;
    }
    
    public void unsetStatus() {
        this.status = null;
    }
    
    public boolean isSetStatus() {
        return this.status != null;
    }
    
    public void setStatusIsSet(final boolean value) {
        if (!value) {
            this.status = null;
        }
    }
    
    public TOperationState getOperationState() {
        return this.operationState;
    }
    
    public void setOperationState(final TOperationState operationState) {
        this.operationState = operationState;
    }
    
    public void unsetOperationState() {
        this.operationState = null;
    }
    
    public boolean isSetOperationState() {
        return this.operationState != null;
    }
    
    public void setOperationStateIsSet(final boolean value) {
        if (!value) {
            this.operationState = null;
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
            case STATUS: {
                if (value == null) {
                    this.unsetStatus();
                    break;
                }
                this.setStatus((TStatus)value);
                break;
            }
            case OPERATION_STATE: {
                if (value == null) {
                    this.unsetOperationState();
                    break;
                }
                this.setOperationState((TOperationState)value);
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
            case STATUS: {
                return this.getStatus();
            }
            case OPERATION_STATE: {
                return this.getOperationState();
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
            case STATUS: {
                return this.isSetStatus();
            }
            case OPERATION_STATE: {
                return this.isSetOperationState();
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
        return that != null && that instanceof TGetOperationStatusResp && this.equals((TGetOperationStatusResp)that);
    }
    
    public boolean equals(final TGetOperationStatusResp that) {
        if (that == null) {
            return false;
        }
        final boolean this_present_status = this.isSetStatus();
        final boolean that_present_status = that.isSetStatus();
        if (this_present_status || that_present_status) {
            if (!this_present_status || !that_present_status) {
                return false;
            }
            if (!this.status.equals(that.status)) {
                return false;
            }
        }
        final boolean this_present_operationState = this.isSetOperationState();
        final boolean that_present_operationState = that.isSetOperationState();
        if (this_present_operationState || that_present_operationState) {
            if (!this_present_operationState || !that_present_operationState) {
                return false;
            }
            if (!this.operationState.equals(that.operationState)) {
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
        final boolean present_status = this.isSetStatus();
        builder.append(present_status);
        if (present_status) {
            builder.append(this.status);
        }
        final boolean present_operationState = this.isSetOperationState();
        builder.append(present_operationState);
        if (present_operationState) {
            builder.append(this.operationState.getValue());
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
    public int compareTo(final TGetOperationStatusResp other) {
        if (!this.getClass().equals(other.getClass())) {
            return this.getClass().getName().compareTo(other.getClass().getName());
        }
        int lastComparison = 0;
        final TGetOperationStatusResp typedOther = other;
        lastComparison = Boolean.valueOf(this.isSetStatus()).compareTo(Boolean.valueOf(typedOther.isSetStatus()));
        if (lastComparison != 0) {
            return lastComparison;
        }
        if (this.isSetStatus()) {
            lastComparison = TBaseHelper.compareTo(this.status, typedOther.status);
            if (lastComparison != 0) {
                return lastComparison;
            }
        }
        lastComparison = Boolean.valueOf(this.isSetOperationState()).compareTo(Boolean.valueOf(typedOther.isSetOperationState()));
        if (lastComparison != 0) {
            return lastComparison;
        }
        if (this.isSetOperationState()) {
            lastComparison = TBaseHelper.compareTo(this.operationState, typedOther.operationState);
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
        TGetOperationStatusResp.schemes.get(iprot.getScheme()).getScheme().read(iprot, this);
    }
    
    @Override
    public void write(final TProtocol oprot) throws TException {
        TGetOperationStatusResp.schemes.get(oprot.getScheme()).getScheme().write(oprot, this);
    }
    
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("TGetOperationStatusResp(");
        boolean first = true;
        sb.append("status:");
        if (this.status == null) {
            sb.append("null");
        }
        else {
            sb.append(this.status);
        }
        first = false;
        if (this.isSetOperationState()) {
            if (!first) {
                sb.append(", ");
            }
            sb.append("operationState:");
            if (this.operationState == null) {
                sb.append("null");
            }
            else {
                sb.append(this.operationState);
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
        if (!this.isSetStatus()) {
            throw new TProtocolException("Required field 'status' is unset! Struct:" + this.toString());
        }
        if (this.status != null) {
            this.status.validate();
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
        STRUCT_DESC = new TStruct("TGetOperationStatusResp");
        STATUS_FIELD_DESC = new TField("status", (byte)12, (short)1);
        OPERATION_STATE_FIELD_DESC = new TField("operationState", (byte)8, (short)2);
        SQL_STATE_FIELD_DESC = new TField("sqlState", (byte)11, (short)3);
        ERROR_CODE_FIELD_DESC = new TField("errorCode", (byte)8, (short)4);
        ERROR_MESSAGE_FIELD_DESC = new TField("errorMessage", (byte)11, (short)5);
        (schemes = new HashMap<Class<? extends IScheme>, SchemeFactory>()).put(StandardScheme.class, new TGetOperationStatusRespStandardSchemeFactory());
        TGetOperationStatusResp.schemes.put(TupleScheme.class, new TGetOperationStatusRespTupleSchemeFactory());
        final Map<_Fields, FieldMetaData> tmpMap = new EnumMap<_Fields, FieldMetaData>(_Fields.class);
        tmpMap.put(_Fields.STATUS, new FieldMetaData("status", (byte)1, new StructMetaData((byte)12, TStatus.class)));
        tmpMap.put(_Fields.OPERATION_STATE, new FieldMetaData("operationState", (byte)2, new EnumMetaData((byte)16, TOperationState.class)));
        tmpMap.put(_Fields.SQL_STATE, new FieldMetaData("sqlState", (byte)2, new FieldValueMetaData((byte)11)));
        tmpMap.put(_Fields.ERROR_CODE, new FieldMetaData("errorCode", (byte)2, new FieldValueMetaData((byte)8)));
        tmpMap.put(_Fields.ERROR_MESSAGE, new FieldMetaData("errorMessage", (byte)2, new FieldValueMetaData((byte)11)));
        FieldMetaData.addStructMetaDataMap(TGetOperationStatusResp.class, metaDataMap = Collections.unmodifiableMap((Map<? extends _Fields, ? extends FieldMetaData>)tmpMap));
    }
    
    public enum _Fields implements TFieldIdEnum
    {
        STATUS((short)1, "status"), 
        OPERATION_STATE((short)2, "operationState"), 
        SQL_STATE((short)3, "sqlState"), 
        ERROR_CODE((short)4, "errorCode"), 
        ERROR_MESSAGE((short)5, "errorMessage");
        
        private static final Map<String, _Fields> byName;
        private final short _thriftId;
        private final String _fieldName;
        
        public static _Fields findByThriftId(final int fieldId) {
            switch (fieldId) {
                case 1: {
                    return _Fields.STATUS;
                }
                case 2: {
                    return _Fields.OPERATION_STATE;
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
    
    private static class TGetOperationStatusRespStandardSchemeFactory implements SchemeFactory
    {
        @Override
        public TGetOperationStatusRespStandardScheme getScheme() {
            return new TGetOperationStatusRespStandardScheme();
        }
    }
    
    private static class TGetOperationStatusRespStandardScheme extends StandardScheme<TGetOperationStatusResp>
    {
        @Override
        public void read(final TProtocol iprot, final TGetOperationStatusResp struct) throws TException {
            iprot.readStructBegin();
            while (true) {
                final TField schemeField = iprot.readFieldBegin();
                if (schemeField.type == 0) {
                    break;
                }
                switch (schemeField.id) {
                    case 1: {
                        if (schemeField.type == 12) {
                            struct.status = new TStatus();
                            struct.status.read(iprot);
                            struct.setStatusIsSet(true);
                            break;
                        }
                        TProtocolUtil.skip(iprot, schemeField.type);
                        break;
                    }
                    case 2: {
                        if (schemeField.type == 8) {
                            struct.operationState = TOperationState.findByValue(iprot.readI32());
                            struct.setOperationStateIsSet(true);
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
        public void write(final TProtocol oprot, final TGetOperationStatusResp struct) throws TException {
            struct.validate();
            oprot.writeStructBegin(TGetOperationStatusResp.STRUCT_DESC);
            if (struct.status != null) {
                oprot.writeFieldBegin(TGetOperationStatusResp.STATUS_FIELD_DESC);
                struct.status.write(oprot);
                oprot.writeFieldEnd();
            }
            if (struct.operationState != null && struct.isSetOperationState()) {
                oprot.writeFieldBegin(TGetOperationStatusResp.OPERATION_STATE_FIELD_DESC);
                oprot.writeI32(struct.operationState.getValue());
                oprot.writeFieldEnd();
            }
            if (struct.sqlState != null && struct.isSetSqlState()) {
                oprot.writeFieldBegin(TGetOperationStatusResp.SQL_STATE_FIELD_DESC);
                oprot.writeString(struct.sqlState);
                oprot.writeFieldEnd();
            }
            if (struct.isSetErrorCode()) {
                oprot.writeFieldBegin(TGetOperationStatusResp.ERROR_CODE_FIELD_DESC);
                oprot.writeI32(struct.errorCode);
                oprot.writeFieldEnd();
            }
            if (struct.errorMessage != null && struct.isSetErrorMessage()) {
                oprot.writeFieldBegin(TGetOperationStatusResp.ERROR_MESSAGE_FIELD_DESC);
                oprot.writeString(struct.errorMessage);
                oprot.writeFieldEnd();
            }
            oprot.writeFieldStop();
            oprot.writeStructEnd();
        }
    }
    
    private static class TGetOperationStatusRespTupleSchemeFactory implements SchemeFactory
    {
        @Override
        public TGetOperationStatusRespTupleScheme getScheme() {
            return new TGetOperationStatusRespTupleScheme();
        }
    }
    
    private static class TGetOperationStatusRespTupleScheme extends TupleScheme<TGetOperationStatusResp>
    {
        @Override
        public void write(final TProtocol prot, final TGetOperationStatusResp struct) throws TException {
            final TTupleProtocol oprot = (TTupleProtocol)prot;
            struct.status.write(oprot);
            final BitSet optionals = new BitSet();
            if (struct.isSetOperationState()) {
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
            if (struct.isSetOperationState()) {
                oprot.writeI32(struct.operationState.getValue());
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
        public void read(final TProtocol prot, final TGetOperationStatusResp struct) throws TException {
            final TTupleProtocol iprot = (TTupleProtocol)prot;
            struct.status = new TStatus();
            struct.status.read(iprot);
            struct.setStatusIsSet(true);
            final BitSet incoming = iprot.readBitSet(4);
            if (incoming.get(0)) {
                struct.operationState = TOperationState.findByValue(iprot.readI32());
                struct.setOperationStateIsSet(true);
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
