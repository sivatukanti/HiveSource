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

public class TFetchResultsReq implements TBase<TFetchResultsReq, _Fields>, Serializable, Cloneable
{
    private static final TStruct STRUCT_DESC;
    private static final TField OPERATION_HANDLE_FIELD_DESC;
    private static final TField ORIENTATION_FIELD_DESC;
    private static final TField MAX_ROWS_FIELD_DESC;
    private static final TField FETCH_TYPE_FIELD_DESC;
    private static final Map<Class<? extends IScheme>, SchemeFactory> schemes;
    private TOperationHandle operationHandle;
    private TFetchOrientation orientation;
    private long maxRows;
    private short fetchType;
    private static final int __MAXROWS_ISSET_ID = 0;
    private static final int __FETCHTYPE_ISSET_ID = 1;
    private byte __isset_bitfield;
    private _Fields[] optionals;
    public static final Map<_Fields, FieldMetaData> metaDataMap;
    
    public TFetchResultsReq() {
        this.__isset_bitfield = 0;
        this.optionals = new _Fields[] { _Fields.FETCH_TYPE };
        this.orientation = TFetchOrientation.FETCH_NEXT;
        this.fetchType = 0;
    }
    
    public TFetchResultsReq(final TOperationHandle operationHandle, final TFetchOrientation orientation, final long maxRows) {
        this();
        this.operationHandle = operationHandle;
        this.orientation = orientation;
        this.maxRows = maxRows;
        this.setMaxRowsIsSet(true);
    }
    
    public TFetchResultsReq(final TFetchResultsReq other) {
        this.__isset_bitfield = 0;
        this.optionals = new _Fields[] { _Fields.FETCH_TYPE };
        this.__isset_bitfield = other.__isset_bitfield;
        if (other.isSetOperationHandle()) {
            this.operationHandle = new TOperationHandle(other.operationHandle);
        }
        if (other.isSetOrientation()) {
            this.orientation = other.orientation;
        }
        this.maxRows = other.maxRows;
        this.fetchType = other.fetchType;
    }
    
    @Override
    public TFetchResultsReq deepCopy() {
        return new TFetchResultsReq(this);
    }
    
    @Override
    public void clear() {
        this.operationHandle = null;
        this.orientation = TFetchOrientation.FETCH_NEXT;
        this.setMaxRowsIsSet(false);
        this.maxRows = 0L;
        this.fetchType = 0;
    }
    
    public TOperationHandle getOperationHandle() {
        return this.operationHandle;
    }
    
    public void setOperationHandle(final TOperationHandle operationHandle) {
        this.operationHandle = operationHandle;
    }
    
    public void unsetOperationHandle() {
        this.operationHandle = null;
    }
    
    public boolean isSetOperationHandle() {
        return this.operationHandle != null;
    }
    
    public void setOperationHandleIsSet(final boolean value) {
        if (!value) {
            this.operationHandle = null;
        }
    }
    
    public TFetchOrientation getOrientation() {
        return this.orientation;
    }
    
    public void setOrientation(final TFetchOrientation orientation) {
        this.orientation = orientation;
    }
    
    public void unsetOrientation() {
        this.orientation = null;
    }
    
    public boolean isSetOrientation() {
        return this.orientation != null;
    }
    
    public void setOrientationIsSet(final boolean value) {
        if (!value) {
            this.orientation = null;
        }
    }
    
    public long getMaxRows() {
        return this.maxRows;
    }
    
    public void setMaxRows(final long maxRows) {
        this.maxRows = maxRows;
        this.setMaxRowsIsSet(true);
    }
    
    public void unsetMaxRows() {
        this.__isset_bitfield = EncodingUtils.clearBit(this.__isset_bitfield, 0);
    }
    
    public boolean isSetMaxRows() {
        return EncodingUtils.testBit(this.__isset_bitfield, 0);
    }
    
    public void setMaxRowsIsSet(final boolean value) {
        this.__isset_bitfield = EncodingUtils.setBit(this.__isset_bitfield, 0, value);
    }
    
    public short getFetchType() {
        return this.fetchType;
    }
    
    public void setFetchType(final short fetchType) {
        this.fetchType = fetchType;
        this.setFetchTypeIsSet(true);
    }
    
    public void unsetFetchType() {
        this.__isset_bitfield = EncodingUtils.clearBit(this.__isset_bitfield, 1);
    }
    
    public boolean isSetFetchType() {
        return EncodingUtils.testBit(this.__isset_bitfield, 1);
    }
    
    public void setFetchTypeIsSet(final boolean value) {
        this.__isset_bitfield = EncodingUtils.setBit(this.__isset_bitfield, 1, value);
    }
    
    @Override
    public void setFieldValue(final _Fields field, final Object value) {
        switch (field) {
            case OPERATION_HANDLE: {
                if (value == null) {
                    this.unsetOperationHandle();
                    break;
                }
                this.setOperationHandle((TOperationHandle)value);
                break;
            }
            case ORIENTATION: {
                if (value == null) {
                    this.unsetOrientation();
                    break;
                }
                this.setOrientation((TFetchOrientation)value);
                break;
            }
            case MAX_ROWS: {
                if (value == null) {
                    this.unsetMaxRows();
                    break;
                }
                this.setMaxRows((long)value);
                break;
            }
            case FETCH_TYPE: {
                if (value == null) {
                    this.unsetFetchType();
                    break;
                }
                this.setFetchType((short)value);
                break;
            }
        }
    }
    
    @Override
    public Object getFieldValue(final _Fields field) {
        switch (field) {
            case OPERATION_HANDLE: {
                return this.getOperationHandle();
            }
            case ORIENTATION: {
                return this.getOrientation();
            }
            case MAX_ROWS: {
                return this.getMaxRows();
            }
            case FETCH_TYPE: {
                return this.getFetchType();
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
            case OPERATION_HANDLE: {
                return this.isSetOperationHandle();
            }
            case ORIENTATION: {
                return this.isSetOrientation();
            }
            case MAX_ROWS: {
                return this.isSetMaxRows();
            }
            case FETCH_TYPE: {
                return this.isSetFetchType();
            }
            default: {
                throw new IllegalStateException();
            }
        }
    }
    
    @Override
    public boolean equals(final Object that) {
        return that != null && that instanceof TFetchResultsReq && this.equals((TFetchResultsReq)that);
    }
    
    public boolean equals(final TFetchResultsReq that) {
        if (that == null) {
            return false;
        }
        final boolean this_present_operationHandle = this.isSetOperationHandle();
        final boolean that_present_operationHandle = that.isSetOperationHandle();
        if (this_present_operationHandle || that_present_operationHandle) {
            if (!this_present_operationHandle || !that_present_operationHandle) {
                return false;
            }
            if (!this.operationHandle.equals(that.operationHandle)) {
                return false;
            }
        }
        final boolean this_present_orientation = this.isSetOrientation();
        final boolean that_present_orientation = that.isSetOrientation();
        if (this_present_orientation || that_present_orientation) {
            if (!this_present_orientation || !that_present_orientation) {
                return false;
            }
            if (!this.orientation.equals(that.orientation)) {
                return false;
            }
        }
        final boolean this_present_maxRows = true;
        final boolean that_present_maxRows = true;
        if (this_present_maxRows || that_present_maxRows) {
            if (!this_present_maxRows || !that_present_maxRows) {
                return false;
            }
            if (this.maxRows != that.maxRows) {
                return false;
            }
        }
        final boolean this_present_fetchType = this.isSetFetchType();
        final boolean that_present_fetchType = that.isSetFetchType();
        if (this_present_fetchType || that_present_fetchType) {
            if (!this_present_fetchType || !that_present_fetchType) {
                return false;
            }
            if (this.fetchType != that.fetchType) {
                return false;
            }
        }
        return true;
    }
    
    @Override
    public int hashCode() {
        final HashCodeBuilder builder = new HashCodeBuilder();
        final boolean present_operationHandle = this.isSetOperationHandle();
        builder.append(present_operationHandle);
        if (present_operationHandle) {
            builder.append(this.operationHandle);
        }
        final boolean present_orientation = this.isSetOrientation();
        builder.append(present_orientation);
        if (present_orientation) {
            builder.append(this.orientation.getValue());
        }
        final boolean present_maxRows = true;
        builder.append(present_maxRows);
        if (present_maxRows) {
            builder.append(this.maxRows);
        }
        final boolean present_fetchType = this.isSetFetchType();
        builder.append(present_fetchType);
        if (present_fetchType) {
            builder.append(this.fetchType);
        }
        return builder.toHashCode();
    }
    
    @Override
    public int compareTo(final TFetchResultsReq other) {
        if (!this.getClass().equals(other.getClass())) {
            return this.getClass().getName().compareTo(other.getClass().getName());
        }
        int lastComparison = 0;
        final TFetchResultsReq typedOther = other;
        lastComparison = Boolean.valueOf(this.isSetOperationHandle()).compareTo(Boolean.valueOf(typedOther.isSetOperationHandle()));
        if (lastComparison != 0) {
            return lastComparison;
        }
        if (this.isSetOperationHandle()) {
            lastComparison = TBaseHelper.compareTo(this.operationHandle, typedOther.operationHandle);
            if (lastComparison != 0) {
                return lastComparison;
            }
        }
        lastComparison = Boolean.valueOf(this.isSetOrientation()).compareTo(Boolean.valueOf(typedOther.isSetOrientation()));
        if (lastComparison != 0) {
            return lastComparison;
        }
        if (this.isSetOrientation()) {
            lastComparison = TBaseHelper.compareTo(this.orientation, typedOther.orientation);
            if (lastComparison != 0) {
                return lastComparison;
            }
        }
        lastComparison = Boolean.valueOf(this.isSetMaxRows()).compareTo(Boolean.valueOf(typedOther.isSetMaxRows()));
        if (lastComparison != 0) {
            return lastComparison;
        }
        if (this.isSetMaxRows()) {
            lastComparison = TBaseHelper.compareTo(this.maxRows, typedOther.maxRows);
            if (lastComparison != 0) {
                return lastComparison;
            }
        }
        lastComparison = Boolean.valueOf(this.isSetFetchType()).compareTo(Boolean.valueOf(typedOther.isSetFetchType()));
        if (lastComparison != 0) {
            return lastComparison;
        }
        if (this.isSetFetchType()) {
            lastComparison = TBaseHelper.compareTo(this.fetchType, typedOther.fetchType);
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
        TFetchResultsReq.schemes.get(iprot.getScheme()).getScheme().read(iprot, this);
    }
    
    @Override
    public void write(final TProtocol oprot) throws TException {
        TFetchResultsReq.schemes.get(oprot.getScheme()).getScheme().write(oprot, this);
    }
    
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("TFetchResultsReq(");
        boolean first = true;
        sb.append("operationHandle:");
        if (this.operationHandle == null) {
            sb.append("null");
        }
        else {
            sb.append(this.operationHandle);
        }
        first = false;
        if (!first) {
            sb.append(", ");
        }
        sb.append("orientation:");
        if (this.orientation == null) {
            sb.append("null");
        }
        else {
            sb.append(this.orientation);
        }
        first = false;
        if (!first) {
            sb.append(", ");
        }
        sb.append("maxRows:");
        sb.append(this.maxRows);
        first = false;
        if (this.isSetFetchType()) {
            if (!first) {
                sb.append(", ");
            }
            sb.append("fetchType:");
            sb.append(this.fetchType);
            first = false;
        }
        sb.append(")");
        return sb.toString();
    }
    
    public void validate() throws TException {
        if (!this.isSetOperationHandle()) {
            throw new TProtocolException("Required field 'operationHandle' is unset! Struct:" + this.toString());
        }
        if (!this.isSetOrientation()) {
            throw new TProtocolException("Required field 'orientation' is unset! Struct:" + this.toString());
        }
        if (!this.isSetMaxRows()) {
            throw new TProtocolException("Required field 'maxRows' is unset! Struct:" + this.toString());
        }
        if (this.operationHandle != null) {
            this.operationHandle.validate();
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
        STRUCT_DESC = new TStruct("TFetchResultsReq");
        OPERATION_HANDLE_FIELD_DESC = new TField("operationHandle", (byte)12, (short)1);
        ORIENTATION_FIELD_DESC = new TField("orientation", (byte)8, (short)2);
        MAX_ROWS_FIELD_DESC = new TField("maxRows", (byte)10, (short)3);
        FETCH_TYPE_FIELD_DESC = new TField("fetchType", (byte)6, (short)4);
        (schemes = new HashMap<Class<? extends IScheme>, SchemeFactory>()).put(StandardScheme.class, new TFetchResultsReqStandardSchemeFactory());
        TFetchResultsReq.schemes.put(TupleScheme.class, new TFetchResultsReqTupleSchemeFactory());
        final Map<_Fields, FieldMetaData> tmpMap = new EnumMap<_Fields, FieldMetaData>(_Fields.class);
        tmpMap.put(_Fields.OPERATION_HANDLE, new FieldMetaData("operationHandle", (byte)1, new StructMetaData((byte)12, TOperationHandle.class)));
        tmpMap.put(_Fields.ORIENTATION, new FieldMetaData("orientation", (byte)1, new EnumMetaData((byte)16, TFetchOrientation.class)));
        tmpMap.put(_Fields.MAX_ROWS, new FieldMetaData("maxRows", (byte)1, new FieldValueMetaData((byte)10)));
        tmpMap.put(_Fields.FETCH_TYPE, new FieldMetaData("fetchType", (byte)2, new FieldValueMetaData((byte)6)));
        FieldMetaData.addStructMetaDataMap(TFetchResultsReq.class, metaDataMap = Collections.unmodifiableMap((Map<? extends _Fields, ? extends FieldMetaData>)tmpMap));
    }
    
    public enum _Fields implements TFieldIdEnum
    {
        OPERATION_HANDLE((short)1, "operationHandle"), 
        ORIENTATION((short)2, "orientation"), 
        MAX_ROWS((short)3, "maxRows"), 
        FETCH_TYPE((short)4, "fetchType");
        
        private static final Map<String, _Fields> byName;
        private final short _thriftId;
        private final String _fieldName;
        
        public static _Fields findByThriftId(final int fieldId) {
            switch (fieldId) {
                case 1: {
                    return _Fields.OPERATION_HANDLE;
                }
                case 2: {
                    return _Fields.ORIENTATION;
                }
                case 3: {
                    return _Fields.MAX_ROWS;
                }
                case 4: {
                    return _Fields.FETCH_TYPE;
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
    
    private static class TFetchResultsReqStandardSchemeFactory implements SchemeFactory
    {
        @Override
        public TFetchResultsReqStandardScheme getScheme() {
            return new TFetchResultsReqStandardScheme();
        }
    }
    
    private static class TFetchResultsReqStandardScheme extends StandardScheme<TFetchResultsReq>
    {
        @Override
        public void read(final TProtocol iprot, final TFetchResultsReq struct) throws TException {
            iprot.readStructBegin();
            while (true) {
                final TField schemeField = iprot.readFieldBegin();
                if (schemeField.type == 0) {
                    break;
                }
                switch (schemeField.id) {
                    case 1: {
                        if (schemeField.type == 12) {
                            struct.operationHandle = new TOperationHandle();
                            struct.operationHandle.read(iprot);
                            struct.setOperationHandleIsSet(true);
                            break;
                        }
                        TProtocolUtil.skip(iprot, schemeField.type);
                        break;
                    }
                    case 2: {
                        if (schemeField.type == 8) {
                            struct.orientation = TFetchOrientation.findByValue(iprot.readI32());
                            struct.setOrientationIsSet(true);
                            break;
                        }
                        TProtocolUtil.skip(iprot, schemeField.type);
                        break;
                    }
                    case 3: {
                        if (schemeField.type == 10) {
                            struct.maxRows = iprot.readI64();
                            struct.setMaxRowsIsSet(true);
                            break;
                        }
                        TProtocolUtil.skip(iprot, schemeField.type);
                        break;
                    }
                    case 4: {
                        if (schemeField.type == 6) {
                            struct.fetchType = iprot.readI16();
                            struct.setFetchTypeIsSet(true);
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
        public void write(final TProtocol oprot, final TFetchResultsReq struct) throws TException {
            struct.validate();
            oprot.writeStructBegin(TFetchResultsReq.STRUCT_DESC);
            if (struct.operationHandle != null) {
                oprot.writeFieldBegin(TFetchResultsReq.OPERATION_HANDLE_FIELD_DESC);
                struct.operationHandle.write(oprot);
                oprot.writeFieldEnd();
            }
            if (struct.orientation != null) {
                oprot.writeFieldBegin(TFetchResultsReq.ORIENTATION_FIELD_DESC);
                oprot.writeI32(struct.orientation.getValue());
                oprot.writeFieldEnd();
            }
            oprot.writeFieldBegin(TFetchResultsReq.MAX_ROWS_FIELD_DESC);
            oprot.writeI64(struct.maxRows);
            oprot.writeFieldEnd();
            if (struct.isSetFetchType()) {
                oprot.writeFieldBegin(TFetchResultsReq.FETCH_TYPE_FIELD_DESC);
                oprot.writeI16(struct.fetchType);
                oprot.writeFieldEnd();
            }
            oprot.writeFieldStop();
            oprot.writeStructEnd();
        }
    }
    
    private static class TFetchResultsReqTupleSchemeFactory implements SchemeFactory
    {
        @Override
        public TFetchResultsReqTupleScheme getScheme() {
            return new TFetchResultsReqTupleScheme();
        }
    }
    
    private static class TFetchResultsReqTupleScheme extends TupleScheme<TFetchResultsReq>
    {
        @Override
        public void write(final TProtocol prot, final TFetchResultsReq struct) throws TException {
            final TTupleProtocol oprot = (TTupleProtocol)prot;
            struct.operationHandle.write(oprot);
            oprot.writeI32(struct.orientation.getValue());
            oprot.writeI64(struct.maxRows);
            final BitSet optionals = new BitSet();
            if (struct.isSetFetchType()) {
                optionals.set(0);
            }
            oprot.writeBitSet(optionals, 1);
            if (struct.isSetFetchType()) {
                oprot.writeI16(struct.fetchType);
            }
        }
        
        @Override
        public void read(final TProtocol prot, final TFetchResultsReq struct) throws TException {
            final TTupleProtocol iprot = (TTupleProtocol)prot;
            struct.operationHandle = new TOperationHandle();
            struct.operationHandle.read(iprot);
            struct.setOperationHandleIsSet(true);
            struct.orientation = TFetchOrientation.findByValue(iprot.readI32());
            struct.setOrientationIsSet(true);
            struct.maxRows = iprot.readI64();
            struct.setMaxRowsIsSet(true);
            final BitSet incoming = iprot.readBitSet(1);
            if (incoming.get(0)) {
                struct.fetchType = iprot.readI16();
                struct.setFetchTypeIsSet(true);
            }
        }
    }
}
