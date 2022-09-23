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

public class TOperationHandle implements TBase<TOperationHandle, _Fields>, Serializable, Cloneable
{
    private static final TStruct STRUCT_DESC;
    private static final TField OPERATION_ID_FIELD_DESC;
    private static final TField OPERATION_TYPE_FIELD_DESC;
    private static final TField HAS_RESULT_SET_FIELD_DESC;
    private static final TField MODIFIED_ROW_COUNT_FIELD_DESC;
    private static final Map<Class<? extends IScheme>, SchemeFactory> schemes;
    private THandleIdentifier operationId;
    private TOperationType operationType;
    private boolean hasResultSet;
    private double modifiedRowCount;
    private static final int __HASRESULTSET_ISSET_ID = 0;
    private static final int __MODIFIEDROWCOUNT_ISSET_ID = 1;
    private byte __isset_bitfield;
    private _Fields[] optionals;
    public static final Map<_Fields, FieldMetaData> metaDataMap;
    
    public TOperationHandle() {
        this.__isset_bitfield = 0;
        this.optionals = new _Fields[] { _Fields.MODIFIED_ROW_COUNT };
    }
    
    public TOperationHandle(final THandleIdentifier operationId, final TOperationType operationType, final boolean hasResultSet) {
        this();
        this.operationId = operationId;
        this.operationType = operationType;
        this.hasResultSet = hasResultSet;
        this.setHasResultSetIsSet(true);
    }
    
    public TOperationHandle(final TOperationHandle other) {
        this.__isset_bitfield = 0;
        this.optionals = new _Fields[] { _Fields.MODIFIED_ROW_COUNT };
        this.__isset_bitfield = other.__isset_bitfield;
        if (other.isSetOperationId()) {
            this.operationId = new THandleIdentifier(other.operationId);
        }
        if (other.isSetOperationType()) {
            this.operationType = other.operationType;
        }
        this.hasResultSet = other.hasResultSet;
        this.modifiedRowCount = other.modifiedRowCount;
    }
    
    @Override
    public TOperationHandle deepCopy() {
        return new TOperationHandle(this);
    }
    
    @Override
    public void clear() {
        this.operationId = null;
        this.operationType = null;
        this.setHasResultSetIsSet(false);
        this.setModifiedRowCountIsSet(this.hasResultSet = false);
        this.modifiedRowCount = 0.0;
    }
    
    public THandleIdentifier getOperationId() {
        return this.operationId;
    }
    
    public void setOperationId(final THandleIdentifier operationId) {
        this.operationId = operationId;
    }
    
    public void unsetOperationId() {
        this.operationId = null;
    }
    
    public boolean isSetOperationId() {
        return this.operationId != null;
    }
    
    public void setOperationIdIsSet(final boolean value) {
        if (!value) {
            this.operationId = null;
        }
    }
    
    public TOperationType getOperationType() {
        return this.operationType;
    }
    
    public void setOperationType(final TOperationType operationType) {
        this.operationType = operationType;
    }
    
    public void unsetOperationType() {
        this.operationType = null;
    }
    
    public boolean isSetOperationType() {
        return this.operationType != null;
    }
    
    public void setOperationTypeIsSet(final boolean value) {
        if (!value) {
            this.operationType = null;
        }
    }
    
    public boolean isHasResultSet() {
        return this.hasResultSet;
    }
    
    public void setHasResultSet(final boolean hasResultSet) {
        this.hasResultSet = hasResultSet;
        this.setHasResultSetIsSet(true);
    }
    
    public void unsetHasResultSet() {
        this.__isset_bitfield = EncodingUtils.clearBit(this.__isset_bitfield, 0);
    }
    
    public boolean isSetHasResultSet() {
        return EncodingUtils.testBit(this.__isset_bitfield, 0);
    }
    
    public void setHasResultSetIsSet(final boolean value) {
        this.__isset_bitfield = EncodingUtils.setBit(this.__isset_bitfield, 0, value);
    }
    
    public double getModifiedRowCount() {
        return this.modifiedRowCount;
    }
    
    public void setModifiedRowCount(final double modifiedRowCount) {
        this.modifiedRowCount = modifiedRowCount;
        this.setModifiedRowCountIsSet(true);
    }
    
    public void unsetModifiedRowCount() {
        this.__isset_bitfield = EncodingUtils.clearBit(this.__isset_bitfield, 1);
    }
    
    public boolean isSetModifiedRowCount() {
        return EncodingUtils.testBit(this.__isset_bitfield, 1);
    }
    
    public void setModifiedRowCountIsSet(final boolean value) {
        this.__isset_bitfield = EncodingUtils.setBit(this.__isset_bitfield, 1, value);
    }
    
    @Override
    public void setFieldValue(final _Fields field, final Object value) {
        switch (field) {
            case OPERATION_ID: {
                if (value == null) {
                    this.unsetOperationId();
                    break;
                }
                this.setOperationId((THandleIdentifier)value);
                break;
            }
            case OPERATION_TYPE: {
                if (value == null) {
                    this.unsetOperationType();
                    break;
                }
                this.setOperationType((TOperationType)value);
                break;
            }
            case HAS_RESULT_SET: {
                if (value == null) {
                    this.unsetHasResultSet();
                    break;
                }
                this.setHasResultSet((boolean)value);
                break;
            }
            case MODIFIED_ROW_COUNT: {
                if (value == null) {
                    this.unsetModifiedRowCount();
                    break;
                }
                this.setModifiedRowCount((double)value);
                break;
            }
        }
    }
    
    @Override
    public Object getFieldValue(final _Fields field) {
        switch (field) {
            case OPERATION_ID: {
                return this.getOperationId();
            }
            case OPERATION_TYPE: {
                return this.getOperationType();
            }
            case HAS_RESULT_SET: {
                return this.isHasResultSet();
            }
            case MODIFIED_ROW_COUNT: {
                return this.getModifiedRowCount();
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
            case OPERATION_ID: {
                return this.isSetOperationId();
            }
            case OPERATION_TYPE: {
                return this.isSetOperationType();
            }
            case HAS_RESULT_SET: {
                return this.isSetHasResultSet();
            }
            case MODIFIED_ROW_COUNT: {
                return this.isSetModifiedRowCount();
            }
            default: {
                throw new IllegalStateException();
            }
        }
    }
    
    @Override
    public boolean equals(final Object that) {
        return that != null && that instanceof TOperationHandle && this.equals((TOperationHandle)that);
    }
    
    public boolean equals(final TOperationHandle that) {
        if (that == null) {
            return false;
        }
        final boolean this_present_operationId = this.isSetOperationId();
        final boolean that_present_operationId = that.isSetOperationId();
        if (this_present_operationId || that_present_operationId) {
            if (!this_present_operationId || !that_present_operationId) {
                return false;
            }
            if (!this.operationId.equals(that.operationId)) {
                return false;
            }
        }
        final boolean this_present_operationType = this.isSetOperationType();
        final boolean that_present_operationType = that.isSetOperationType();
        if (this_present_operationType || that_present_operationType) {
            if (!this_present_operationType || !that_present_operationType) {
                return false;
            }
            if (!this.operationType.equals(that.operationType)) {
                return false;
            }
        }
        final boolean this_present_hasResultSet = true;
        final boolean that_present_hasResultSet = true;
        if (this_present_hasResultSet || that_present_hasResultSet) {
            if (!this_present_hasResultSet || !that_present_hasResultSet) {
                return false;
            }
            if (this.hasResultSet != that.hasResultSet) {
                return false;
            }
        }
        final boolean this_present_modifiedRowCount = this.isSetModifiedRowCount();
        final boolean that_present_modifiedRowCount = that.isSetModifiedRowCount();
        if (this_present_modifiedRowCount || that_present_modifiedRowCount) {
            if (!this_present_modifiedRowCount || !that_present_modifiedRowCount) {
                return false;
            }
            if (this.modifiedRowCount != that.modifiedRowCount) {
                return false;
            }
        }
        return true;
    }
    
    @Override
    public int hashCode() {
        final HashCodeBuilder builder = new HashCodeBuilder();
        final boolean present_operationId = this.isSetOperationId();
        builder.append(present_operationId);
        if (present_operationId) {
            builder.append(this.operationId);
        }
        final boolean present_operationType = this.isSetOperationType();
        builder.append(present_operationType);
        if (present_operationType) {
            builder.append(this.operationType.getValue());
        }
        final boolean present_hasResultSet = true;
        builder.append(present_hasResultSet);
        if (present_hasResultSet) {
            builder.append(this.hasResultSet);
        }
        final boolean present_modifiedRowCount = this.isSetModifiedRowCount();
        builder.append(present_modifiedRowCount);
        if (present_modifiedRowCount) {
            builder.append(this.modifiedRowCount);
        }
        return builder.toHashCode();
    }
    
    @Override
    public int compareTo(final TOperationHandle other) {
        if (!this.getClass().equals(other.getClass())) {
            return this.getClass().getName().compareTo(other.getClass().getName());
        }
        int lastComparison = 0;
        final TOperationHandle typedOther = other;
        lastComparison = Boolean.valueOf(this.isSetOperationId()).compareTo(Boolean.valueOf(typedOther.isSetOperationId()));
        if (lastComparison != 0) {
            return lastComparison;
        }
        if (this.isSetOperationId()) {
            lastComparison = TBaseHelper.compareTo(this.operationId, typedOther.operationId);
            if (lastComparison != 0) {
                return lastComparison;
            }
        }
        lastComparison = Boolean.valueOf(this.isSetOperationType()).compareTo(Boolean.valueOf(typedOther.isSetOperationType()));
        if (lastComparison != 0) {
            return lastComparison;
        }
        if (this.isSetOperationType()) {
            lastComparison = TBaseHelper.compareTo(this.operationType, typedOther.operationType);
            if (lastComparison != 0) {
                return lastComparison;
            }
        }
        lastComparison = Boolean.valueOf(this.isSetHasResultSet()).compareTo(Boolean.valueOf(typedOther.isSetHasResultSet()));
        if (lastComparison != 0) {
            return lastComparison;
        }
        if (this.isSetHasResultSet()) {
            lastComparison = TBaseHelper.compareTo(this.hasResultSet, typedOther.hasResultSet);
            if (lastComparison != 0) {
                return lastComparison;
            }
        }
        lastComparison = Boolean.valueOf(this.isSetModifiedRowCount()).compareTo(Boolean.valueOf(typedOther.isSetModifiedRowCount()));
        if (lastComparison != 0) {
            return lastComparison;
        }
        if (this.isSetModifiedRowCount()) {
            lastComparison = TBaseHelper.compareTo(this.modifiedRowCount, typedOther.modifiedRowCount);
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
        TOperationHandle.schemes.get(iprot.getScheme()).getScheme().read(iprot, this);
    }
    
    @Override
    public void write(final TProtocol oprot) throws TException {
        TOperationHandle.schemes.get(oprot.getScheme()).getScheme().write(oprot, this);
    }
    
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("TOperationHandle(");
        boolean first = true;
        sb.append("operationId:");
        if (this.operationId == null) {
            sb.append("null");
        }
        else {
            sb.append(this.operationId);
        }
        first = false;
        if (!first) {
            sb.append(", ");
        }
        sb.append("operationType:");
        if (this.operationType == null) {
            sb.append("null");
        }
        else {
            sb.append(this.operationType);
        }
        first = false;
        if (!first) {
            sb.append(", ");
        }
        sb.append("hasResultSet:");
        sb.append(this.hasResultSet);
        first = false;
        if (this.isSetModifiedRowCount()) {
            if (!first) {
                sb.append(", ");
            }
            sb.append("modifiedRowCount:");
            sb.append(this.modifiedRowCount);
            first = false;
        }
        sb.append(")");
        return sb.toString();
    }
    
    public void validate() throws TException {
        if (!this.isSetOperationId()) {
            throw new TProtocolException("Required field 'operationId' is unset! Struct:" + this.toString());
        }
        if (!this.isSetOperationType()) {
            throw new TProtocolException("Required field 'operationType' is unset! Struct:" + this.toString());
        }
        if (!this.isSetHasResultSet()) {
            throw new TProtocolException("Required field 'hasResultSet' is unset! Struct:" + this.toString());
        }
        if (this.operationId != null) {
            this.operationId.validate();
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
        STRUCT_DESC = new TStruct("TOperationHandle");
        OPERATION_ID_FIELD_DESC = new TField("operationId", (byte)12, (short)1);
        OPERATION_TYPE_FIELD_DESC = new TField("operationType", (byte)8, (short)2);
        HAS_RESULT_SET_FIELD_DESC = new TField("hasResultSet", (byte)2, (short)3);
        MODIFIED_ROW_COUNT_FIELD_DESC = new TField("modifiedRowCount", (byte)4, (short)4);
        (schemes = new HashMap<Class<? extends IScheme>, SchemeFactory>()).put(StandardScheme.class, new TOperationHandleStandardSchemeFactory());
        TOperationHandle.schemes.put(TupleScheme.class, new TOperationHandleTupleSchemeFactory());
        final Map<_Fields, FieldMetaData> tmpMap = new EnumMap<_Fields, FieldMetaData>(_Fields.class);
        tmpMap.put(_Fields.OPERATION_ID, new FieldMetaData("operationId", (byte)1, new StructMetaData((byte)12, THandleIdentifier.class)));
        tmpMap.put(_Fields.OPERATION_TYPE, new FieldMetaData("operationType", (byte)1, new EnumMetaData((byte)16, TOperationType.class)));
        tmpMap.put(_Fields.HAS_RESULT_SET, new FieldMetaData("hasResultSet", (byte)1, new FieldValueMetaData((byte)2)));
        tmpMap.put(_Fields.MODIFIED_ROW_COUNT, new FieldMetaData("modifiedRowCount", (byte)2, new FieldValueMetaData((byte)4)));
        FieldMetaData.addStructMetaDataMap(TOperationHandle.class, metaDataMap = Collections.unmodifiableMap((Map<? extends _Fields, ? extends FieldMetaData>)tmpMap));
    }
    
    public enum _Fields implements TFieldIdEnum
    {
        OPERATION_ID((short)1, "operationId"), 
        OPERATION_TYPE((short)2, "operationType"), 
        HAS_RESULT_SET((short)3, "hasResultSet"), 
        MODIFIED_ROW_COUNT((short)4, "modifiedRowCount");
        
        private static final Map<String, _Fields> byName;
        private final short _thriftId;
        private final String _fieldName;
        
        public static _Fields findByThriftId(final int fieldId) {
            switch (fieldId) {
                case 1: {
                    return _Fields.OPERATION_ID;
                }
                case 2: {
                    return _Fields.OPERATION_TYPE;
                }
                case 3: {
                    return _Fields.HAS_RESULT_SET;
                }
                case 4: {
                    return _Fields.MODIFIED_ROW_COUNT;
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
    
    private static class TOperationHandleStandardSchemeFactory implements SchemeFactory
    {
        @Override
        public TOperationHandleStandardScheme getScheme() {
            return new TOperationHandleStandardScheme();
        }
    }
    
    private static class TOperationHandleStandardScheme extends StandardScheme<TOperationHandle>
    {
        @Override
        public void read(final TProtocol iprot, final TOperationHandle struct) throws TException {
            iprot.readStructBegin();
            while (true) {
                final TField schemeField = iprot.readFieldBegin();
                if (schemeField.type == 0) {
                    break;
                }
                switch (schemeField.id) {
                    case 1: {
                        if (schemeField.type == 12) {
                            struct.operationId = new THandleIdentifier();
                            struct.operationId.read(iprot);
                            struct.setOperationIdIsSet(true);
                            break;
                        }
                        TProtocolUtil.skip(iprot, schemeField.type);
                        break;
                    }
                    case 2: {
                        if (schemeField.type == 8) {
                            struct.operationType = TOperationType.findByValue(iprot.readI32());
                            struct.setOperationTypeIsSet(true);
                            break;
                        }
                        TProtocolUtil.skip(iprot, schemeField.type);
                        break;
                    }
                    case 3: {
                        if (schemeField.type == 2) {
                            struct.hasResultSet = iprot.readBool();
                            struct.setHasResultSetIsSet(true);
                            break;
                        }
                        TProtocolUtil.skip(iprot, schemeField.type);
                        break;
                    }
                    case 4: {
                        if (schemeField.type == 4) {
                            struct.modifiedRowCount = iprot.readDouble();
                            struct.setModifiedRowCountIsSet(true);
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
        public void write(final TProtocol oprot, final TOperationHandle struct) throws TException {
            struct.validate();
            oprot.writeStructBegin(TOperationHandle.STRUCT_DESC);
            if (struct.operationId != null) {
                oprot.writeFieldBegin(TOperationHandle.OPERATION_ID_FIELD_DESC);
                struct.operationId.write(oprot);
                oprot.writeFieldEnd();
            }
            if (struct.operationType != null) {
                oprot.writeFieldBegin(TOperationHandle.OPERATION_TYPE_FIELD_DESC);
                oprot.writeI32(struct.operationType.getValue());
                oprot.writeFieldEnd();
            }
            oprot.writeFieldBegin(TOperationHandle.HAS_RESULT_SET_FIELD_DESC);
            oprot.writeBool(struct.hasResultSet);
            oprot.writeFieldEnd();
            if (struct.isSetModifiedRowCount()) {
                oprot.writeFieldBegin(TOperationHandle.MODIFIED_ROW_COUNT_FIELD_DESC);
                oprot.writeDouble(struct.modifiedRowCount);
                oprot.writeFieldEnd();
            }
            oprot.writeFieldStop();
            oprot.writeStructEnd();
        }
    }
    
    private static class TOperationHandleTupleSchemeFactory implements SchemeFactory
    {
        @Override
        public TOperationHandleTupleScheme getScheme() {
            return new TOperationHandleTupleScheme();
        }
    }
    
    private static class TOperationHandleTupleScheme extends TupleScheme<TOperationHandle>
    {
        @Override
        public void write(final TProtocol prot, final TOperationHandle struct) throws TException {
            final TTupleProtocol oprot = (TTupleProtocol)prot;
            struct.operationId.write(oprot);
            oprot.writeI32(struct.operationType.getValue());
            oprot.writeBool(struct.hasResultSet);
            final BitSet optionals = new BitSet();
            if (struct.isSetModifiedRowCount()) {
                optionals.set(0);
            }
            oprot.writeBitSet(optionals, 1);
            if (struct.isSetModifiedRowCount()) {
                oprot.writeDouble(struct.modifiedRowCount);
            }
        }
        
        @Override
        public void read(final TProtocol prot, final TOperationHandle struct) throws TException {
            final TTupleProtocol iprot = (TTupleProtocol)prot;
            struct.operationId = new THandleIdentifier();
            struct.operationId.read(iprot);
            struct.setOperationIdIsSet(true);
            struct.operationType = TOperationType.findByValue(iprot.readI32());
            struct.setOperationTypeIsSet(true);
            struct.hasResultSet = iprot.readBool();
            struct.setHasResultSetIsSet(true);
            final BitSet incoming = iprot.readBitSet(1);
            if (incoming.get(0)) {
                struct.modifiedRowCount = iprot.readDouble();
                struct.setModifiedRowCountIsSet(true);
            }
        }
    }
}
