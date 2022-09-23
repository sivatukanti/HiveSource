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
import org.apache.thrift.meta_data.FieldMetaData;
import org.apache.thrift.scheme.SchemeFactory;
import org.apache.thrift.scheme.IScheme;
import java.util.Map;
import org.apache.thrift.protocol.TField;
import org.apache.thrift.protocol.TStruct;
import java.io.Serializable;
import org.apache.thrift.TBase;

public class TGetCatalogsResp implements TBase<TGetCatalogsResp, _Fields>, Serializable, Cloneable
{
    private static final TStruct STRUCT_DESC;
    private static final TField STATUS_FIELD_DESC;
    private static final TField OPERATION_HANDLE_FIELD_DESC;
    private static final Map<Class<? extends IScheme>, SchemeFactory> schemes;
    private TStatus status;
    private TOperationHandle operationHandle;
    private _Fields[] optionals;
    public static final Map<_Fields, FieldMetaData> metaDataMap;
    
    public TGetCatalogsResp() {
        this.optionals = new _Fields[] { _Fields.OPERATION_HANDLE };
    }
    
    public TGetCatalogsResp(final TStatus status) {
        this();
        this.status = status;
    }
    
    public TGetCatalogsResp(final TGetCatalogsResp other) {
        this.optionals = new _Fields[] { _Fields.OPERATION_HANDLE };
        if (other.isSetStatus()) {
            this.status = new TStatus(other.status);
        }
        if (other.isSetOperationHandle()) {
            this.operationHandle = new TOperationHandle(other.operationHandle);
        }
    }
    
    @Override
    public TGetCatalogsResp deepCopy() {
        return new TGetCatalogsResp(this);
    }
    
    @Override
    public void clear() {
        this.status = null;
        this.operationHandle = null;
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
            case OPERATION_HANDLE: {
                if (value == null) {
                    this.unsetOperationHandle();
                    break;
                }
                this.setOperationHandle((TOperationHandle)value);
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
            case OPERATION_HANDLE: {
                return this.getOperationHandle();
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
            case OPERATION_HANDLE: {
                return this.isSetOperationHandle();
            }
            default: {
                throw new IllegalStateException();
            }
        }
    }
    
    @Override
    public boolean equals(final Object that) {
        return that != null && that instanceof TGetCatalogsResp && this.equals((TGetCatalogsResp)that);
    }
    
    public boolean equals(final TGetCatalogsResp that) {
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
        final boolean present_operationHandle = this.isSetOperationHandle();
        builder.append(present_operationHandle);
        if (present_operationHandle) {
            builder.append(this.operationHandle);
        }
        return builder.toHashCode();
    }
    
    @Override
    public int compareTo(final TGetCatalogsResp other) {
        if (!this.getClass().equals(other.getClass())) {
            return this.getClass().getName().compareTo(other.getClass().getName());
        }
        int lastComparison = 0;
        final TGetCatalogsResp typedOther = other;
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
        return 0;
    }
    
    @Override
    public _Fields fieldForId(final int fieldId) {
        return _Fields.findByThriftId(fieldId);
    }
    
    @Override
    public void read(final TProtocol iprot) throws TException {
        TGetCatalogsResp.schemes.get(iprot.getScheme()).getScheme().read(iprot, this);
    }
    
    @Override
    public void write(final TProtocol oprot) throws TException {
        TGetCatalogsResp.schemes.get(oprot.getScheme()).getScheme().write(oprot, this);
    }
    
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("TGetCatalogsResp(");
        boolean first = true;
        sb.append("status:");
        if (this.status == null) {
            sb.append("null");
        }
        else {
            sb.append(this.status);
        }
        first = false;
        if (this.isSetOperationHandle()) {
            if (!first) {
                sb.append(", ");
            }
            sb.append("operationHandle:");
            if (this.operationHandle == null) {
                sb.append("null");
            }
            else {
                sb.append(this.operationHandle);
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
            this.read(new TCompactProtocol(new TIOStreamTransport(in)));
        }
        catch (TException te) {
            throw new IOException(te);
        }
    }
    
    static {
        STRUCT_DESC = new TStruct("TGetCatalogsResp");
        STATUS_FIELD_DESC = new TField("status", (byte)12, (short)1);
        OPERATION_HANDLE_FIELD_DESC = new TField("operationHandle", (byte)12, (short)2);
        (schemes = new HashMap<Class<? extends IScheme>, SchemeFactory>()).put(StandardScheme.class, new TGetCatalogsRespStandardSchemeFactory());
        TGetCatalogsResp.schemes.put(TupleScheme.class, new TGetCatalogsRespTupleSchemeFactory());
        final Map<_Fields, FieldMetaData> tmpMap = new EnumMap<_Fields, FieldMetaData>(_Fields.class);
        tmpMap.put(_Fields.STATUS, new FieldMetaData("status", (byte)1, new StructMetaData((byte)12, TStatus.class)));
        tmpMap.put(_Fields.OPERATION_HANDLE, new FieldMetaData("operationHandle", (byte)2, new StructMetaData((byte)12, TOperationHandle.class)));
        FieldMetaData.addStructMetaDataMap(TGetCatalogsResp.class, metaDataMap = Collections.unmodifiableMap((Map<? extends _Fields, ? extends FieldMetaData>)tmpMap));
    }
    
    public enum _Fields implements TFieldIdEnum
    {
        STATUS((short)1, "status"), 
        OPERATION_HANDLE((short)2, "operationHandle");
        
        private static final Map<String, _Fields> byName;
        private final short _thriftId;
        private final String _fieldName;
        
        public static _Fields findByThriftId(final int fieldId) {
            switch (fieldId) {
                case 1: {
                    return _Fields.STATUS;
                }
                case 2: {
                    return _Fields.OPERATION_HANDLE;
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
    
    private static class TGetCatalogsRespStandardSchemeFactory implements SchemeFactory
    {
        @Override
        public TGetCatalogsRespStandardScheme getScheme() {
            return new TGetCatalogsRespStandardScheme();
        }
    }
    
    private static class TGetCatalogsRespStandardScheme extends StandardScheme<TGetCatalogsResp>
    {
        @Override
        public void read(final TProtocol iprot, final TGetCatalogsResp struct) throws TException {
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
                        if (schemeField.type == 12) {
                            struct.operationHandle = new TOperationHandle();
                            struct.operationHandle.read(iprot);
                            struct.setOperationHandleIsSet(true);
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
        public void write(final TProtocol oprot, final TGetCatalogsResp struct) throws TException {
            struct.validate();
            oprot.writeStructBegin(TGetCatalogsResp.STRUCT_DESC);
            if (struct.status != null) {
                oprot.writeFieldBegin(TGetCatalogsResp.STATUS_FIELD_DESC);
                struct.status.write(oprot);
                oprot.writeFieldEnd();
            }
            if (struct.operationHandle != null && struct.isSetOperationHandle()) {
                oprot.writeFieldBegin(TGetCatalogsResp.OPERATION_HANDLE_FIELD_DESC);
                struct.operationHandle.write(oprot);
                oprot.writeFieldEnd();
            }
            oprot.writeFieldStop();
            oprot.writeStructEnd();
        }
    }
    
    private static class TGetCatalogsRespTupleSchemeFactory implements SchemeFactory
    {
        @Override
        public TGetCatalogsRespTupleScheme getScheme() {
            return new TGetCatalogsRespTupleScheme();
        }
    }
    
    private static class TGetCatalogsRespTupleScheme extends TupleScheme<TGetCatalogsResp>
    {
        @Override
        public void write(final TProtocol prot, final TGetCatalogsResp struct) throws TException {
            final TTupleProtocol oprot = (TTupleProtocol)prot;
            struct.status.write(oprot);
            final BitSet optionals = new BitSet();
            if (struct.isSetOperationHandle()) {
                optionals.set(0);
            }
            oprot.writeBitSet(optionals, 1);
            if (struct.isSetOperationHandle()) {
                struct.operationHandle.write(oprot);
            }
        }
        
        @Override
        public void read(final TProtocol prot, final TGetCatalogsResp struct) throws TException {
            final TTupleProtocol iprot = (TTupleProtocol)prot;
            struct.status = new TStatus();
            struct.status.read(iprot);
            struct.setStatusIsSet(true);
            final BitSet incoming = iprot.readBitSet(1);
            if (incoming.get(0)) {
                struct.operationHandle = new TOperationHandle();
                struct.operationHandle.read(iprot);
                struct.setOperationHandleIsSet(true);
            }
        }
    }
}
