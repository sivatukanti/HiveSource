// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hive.service.cli.thrift;

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

public class TCancelOperationReq implements TBase<TCancelOperationReq, _Fields>, Serializable, Cloneable
{
    private static final TStruct STRUCT_DESC;
    private static final TField OPERATION_HANDLE_FIELD_DESC;
    private static final Map<Class<? extends IScheme>, SchemeFactory> schemes;
    private TOperationHandle operationHandle;
    public static final Map<_Fields, FieldMetaData> metaDataMap;
    
    public TCancelOperationReq() {
    }
    
    public TCancelOperationReq(final TOperationHandle operationHandle) {
        this();
        this.operationHandle = operationHandle;
    }
    
    public TCancelOperationReq(final TCancelOperationReq other) {
        if (other.isSetOperationHandle()) {
            this.operationHandle = new TOperationHandle(other.operationHandle);
        }
    }
    
    @Override
    public TCancelOperationReq deepCopy() {
        return new TCancelOperationReq(this);
    }
    
    @Override
    public void clear() {
        this.operationHandle = null;
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
        return that != null && that instanceof TCancelOperationReq && this.equals((TCancelOperationReq)that);
    }
    
    public boolean equals(final TCancelOperationReq that) {
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
        return builder.toHashCode();
    }
    
    @Override
    public int compareTo(final TCancelOperationReq other) {
        if (!this.getClass().equals(other.getClass())) {
            return this.getClass().getName().compareTo(other.getClass().getName());
        }
        int lastComparison = 0;
        final TCancelOperationReq typedOther = other;
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
        TCancelOperationReq.schemes.get(iprot.getScheme()).getScheme().read(iprot, this);
    }
    
    @Override
    public void write(final TProtocol oprot) throws TException {
        TCancelOperationReq.schemes.get(oprot.getScheme()).getScheme().write(oprot, this);
    }
    
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("TCancelOperationReq(");
        boolean first = true;
        sb.append("operationHandle:");
        if (this.operationHandle == null) {
            sb.append("null");
        }
        else {
            sb.append(this.operationHandle);
        }
        first = false;
        sb.append(")");
        return sb.toString();
    }
    
    public void validate() throws TException {
        if (!this.isSetOperationHandle()) {
            throw new TProtocolException("Required field 'operationHandle' is unset! Struct:" + this.toString());
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
        STRUCT_DESC = new TStruct("TCancelOperationReq");
        OPERATION_HANDLE_FIELD_DESC = new TField("operationHandle", (byte)12, (short)1);
        (schemes = new HashMap<Class<? extends IScheme>, SchemeFactory>()).put(StandardScheme.class, new TCancelOperationReqStandardSchemeFactory());
        TCancelOperationReq.schemes.put(TupleScheme.class, new TCancelOperationReqTupleSchemeFactory());
        final Map<_Fields, FieldMetaData> tmpMap = new EnumMap<_Fields, FieldMetaData>(_Fields.class);
        tmpMap.put(_Fields.OPERATION_HANDLE, new FieldMetaData("operationHandle", (byte)1, new StructMetaData((byte)12, TOperationHandle.class)));
        FieldMetaData.addStructMetaDataMap(TCancelOperationReq.class, metaDataMap = Collections.unmodifiableMap((Map<? extends _Fields, ? extends FieldMetaData>)tmpMap));
    }
    
    public enum _Fields implements TFieldIdEnum
    {
        OPERATION_HANDLE((short)1, "operationHandle");
        
        private static final Map<String, _Fields> byName;
        private final short _thriftId;
        private final String _fieldName;
        
        public static _Fields findByThriftId(final int fieldId) {
            switch (fieldId) {
                case 1: {
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
    
    private static class TCancelOperationReqStandardSchemeFactory implements SchemeFactory
    {
        @Override
        public TCancelOperationReqStandardScheme getScheme() {
            return new TCancelOperationReqStandardScheme();
        }
    }
    
    private static class TCancelOperationReqStandardScheme extends StandardScheme<TCancelOperationReq>
    {
        @Override
        public void read(final TProtocol iprot, final TCancelOperationReq struct) throws TException {
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
        public void write(final TProtocol oprot, final TCancelOperationReq struct) throws TException {
            struct.validate();
            oprot.writeStructBegin(TCancelOperationReq.STRUCT_DESC);
            if (struct.operationHandle != null) {
                oprot.writeFieldBegin(TCancelOperationReq.OPERATION_HANDLE_FIELD_DESC);
                struct.operationHandle.write(oprot);
                oprot.writeFieldEnd();
            }
            oprot.writeFieldStop();
            oprot.writeStructEnd();
        }
    }
    
    private static class TCancelOperationReqTupleSchemeFactory implements SchemeFactory
    {
        @Override
        public TCancelOperationReqTupleScheme getScheme() {
            return new TCancelOperationReqTupleScheme();
        }
    }
    
    private static class TCancelOperationReqTupleScheme extends TupleScheme<TCancelOperationReq>
    {
        @Override
        public void write(final TProtocol prot, final TCancelOperationReq struct) throws TException {
            final TTupleProtocol oprot = (TTupleProtocol)prot;
            struct.operationHandle.write(oprot);
        }
        
        @Override
        public void read(final TProtocol prot, final TCancelOperationReq struct) throws TException {
            final TTupleProtocol iprot = (TTupleProtocol)prot;
            struct.operationHandle = new TOperationHandle();
            struct.operationHandle.read(iprot);
            struct.setOperationHandleIsSet(true);
        }
    }
}
