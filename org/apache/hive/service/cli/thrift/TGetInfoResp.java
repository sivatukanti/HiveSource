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

public class TGetInfoResp implements TBase<TGetInfoResp, _Fields>, Serializable, Cloneable
{
    private static final TStruct STRUCT_DESC;
    private static final TField STATUS_FIELD_DESC;
    private static final TField INFO_VALUE_FIELD_DESC;
    private static final Map<Class<? extends IScheme>, SchemeFactory> schemes;
    private TStatus status;
    private TGetInfoValue infoValue;
    public static final Map<_Fields, FieldMetaData> metaDataMap;
    
    public TGetInfoResp() {
    }
    
    public TGetInfoResp(final TStatus status, final TGetInfoValue infoValue) {
        this();
        this.status = status;
        this.infoValue = infoValue;
    }
    
    public TGetInfoResp(final TGetInfoResp other) {
        if (other.isSetStatus()) {
            this.status = new TStatus(other.status);
        }
        if (other.isSetInfoValue()) {
            this.infoValue = new TGetInfoValue(other.infoValue);
        }
    }
    
    @Override
    public TGetInfoResp deepCopy() {
        return new TGetInfoResp(this);
    }
    
    @Override
    public void clear() {
        this.status = null;
        this.infoValue = null;
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
    
    public TGetInfoValue getInfoValue() {
        return this.infoValue;
    }
    
    public void setInfoValue(final TGetInfoValue infoValue) {
        this.infoValue = infoValue;
    }
    
    public void unsetInfoValue() {
        this.infoValue = null;
    }
    
    public boolean isSetInfoValue() {
        return this.infoValue != null;
    }
    
    public void setInfoValueIsSet(final boolean value) {
        if (!value) {
            this.infoValue = null;
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
            case INFO_VALUE: {
                if (value == null) {
                    this.unsetInfoValue();
                    break;
                }
                this.setInfoValue((TGetInfoValue)value);
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
            case INFO_VALUE: {
                return this.getInfoValue();
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
            case INFO_VALUE: {
                return this.isSetInfoValue();
            }
            default: {
                throw new IllegalStateException();
            }
        }
    }
    
    @Override
    public boolean equals(final Object that) {
        return that != null && that instanceof TGetInfoResp && this.equals((TGetInfoResp)that);
    }
    
    public boolean equals(final TGetInfoResp that) {
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
        final boolean this_present_infoValue = this.isSetInfoValue();
        final boolean that_present_infoValue = that.isSetInfoValue();
        if (this_present_infoValue || that_present_infoValue) {
            if (!this_present_infoValue || !that_present_infoValue) {
                return false;
            }
            if (!this.infoValue.equals(that.infoValue)) {
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
        final boolean present_infoValue = this.isSetInfoValue();
        builder.append(present_infoValue);
        if (present_infoValue) {
            builder.append(this.infoValue);
        }
        return builder.toHashCode();
    }
    
    @Override
    public int compareTo(final TGetInfoResp other) {
        if (!this.getClass().equals(other.getClass())) {
            return this.getClass().getName().compareTo(other.getClass().getName());
        }
        int lastComparison = 0;
        final TGetInfoResp typedOther = other;
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
        lastComparison = Boolean.valueOf(this.isSetInfoValue()).compareTo(Boolean.valueOf(typedOther.isSetInfoValue()));
        if (lastComparison != 0) {
            return lastComparison;
        }
        if (this.isSetInfoValue()) {
            lastComparison = TBaseHelper.compareTo(this.infoValue, typedOther.infoValue);
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
        TGetInfoResp.schemes.get(iprot.getScheme()).getScheme().read(iprot, this);
    }
    
    @Override
    public void write(final TProtocol oprot) throws TException {
        TGetInfoResp.schemes.get(oprot.getScheme()).getScheme().write(oprot, this);
    }
    
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("TGetInfoResp(");
        boolean first = true;
        sb.append("status:");
        if (this.status == null) {
            sb.append("null");
        }
        else {
            sb.append(this.status);
        }
        first = false;
        if (!first) {
            sb.append(", ");
        }
        sb.append("infoValue:");
        if (this.infoValue == null) {
            sb.append("null");
        }
        else {
            sb.append(this.infoValue);
        }
        first = false;
        sb.append(")");
        return sb.toString();
    }
    
    public void validate() throws TException {
        if (!this.isSetStatus()) {
            throw new TProtocolException("Required field 'status' is unset! Struct:" + this.toString());
        }
        if (!this.isSetInfoValue()) {
            throw new TProtocolException("Required field 'infoValue' is unset! Struct:" + this.toString());
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
            this.read(new TCompactProtocol(new TIOStreamTransport(in)));
        }
        catch (TException te) {
            throw new IOException(te);
        }
    }
    
    static {
        STRUCT_DESC = new TStruct("TGetInfoResp");
        STATUS_FIELD_DESC = new TField("status", (byte)12, (short)1);
        INFO_VALUE_FIELD_DESC = new TField("infoValue", (byte)12, (short)2);
        (schemes = new HashMap<Class<? extends IScheme>, SchemeFactory>()).put(StandardScheme.class, new TGetInfoRespStandardSchemeFactory());
        TGetInfoResp.schemes.put(TupleScheme.class, new TGetInfoRespTupleSchemeFactory());
        final Map<_Fields, FieldMetaData> tmpMap = new EnumMap<_Fields, FieldMetaData>(_Fields.class);
        tmpMap.put(_Fields.STATUS, new FieldMetaData("status", (byte)1, new StructMetaData((byte)12, TStatus.class)));
        tmpMap.put(_Fields.INFO_VALUE, new FieldMetaData("infoValue", (byte)1, new StructMetaData((byte)12, TGetInfoValue.class)));
        FieldMetaData.addStructMetaDataMap(TGetInfoResp.class, metaDataMap = Collections.unmodifiableMap((Map<? extends _Fields, ? extends FieldMetaData>)tmpMap));
    }
    
    public enum _Fields implements TFieldIdEnum
    {
        STATUS((short)1, "status"), 
        INFO_VALUE((short)2, "infoValue");
        
        private static final Map<String, _Fields> byName;
        private final short _thriftId;
        private final String _fieldName;
        
        public static _Fields findByThriftId(final int fieldId) {
            switch (fieldId) {
                case 1: {
                    return _Fields.STATUS;
                }
                case 2: {
                    return _Fields.INFO_VALUE;
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
    
    private static class TGetInfoRespStandardSchemeFactory implements SchemeFactory
    {
        @Override
        public TGetInfoRespStandardScheme getScheme() {
            return new TGetInfoRespStandardScheme();
        }
    }
    
    private static class TGetInfoRespStandardScheme extends StandardScheme<TGetInfoResp>
    {
        @Override
        public void read(final TProtocol iprot, final TGetInfoResp struct) throws TException {
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
                            struct.infoValue = new TGetInfoValue();
                            struct.infoValue.read(iprot);
                            struct.setInfoValueIsSet(true);
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
        public void write(final TProtocol oprot, final TGetInfoResp struct) throws TException {
            struct.validate();
            oprot.writeStructBegin(TGetInfoResp.STRUCT_DESC);
            if (struct.status != null) {
                oprot.writeFieldBegin(TGetInfoResp.STATUS_FIELD_DESC);
                struct.status.write(oprot);
                oprot.writeFieldEnd();
            }
            if (struct.infoValue != null) {
                oprot.writeFieldBegin(TGetInfoResp.INFO_VALUE_FIELD_DESC);
                struct.infoValue.write(oprot);
                oprot.writeFieldEnd();
            }
            oprot.writeFieldStop();
            oprot.writeStructEnd();
        }
    }
    
    private static class TGetInfoRespTupleSchemeFactory implements SchemeFactory
    {
        @Override
        public TGetInfoRespTupleScheme getScheme() {
            return new TGetInfoRespTupleScheme();
        }
    }
    
    private static class TGetInfoRespTupleScheme extends TupleScheme<TGetInfoResp>
    {
        @Override
        public void write(final TProtocol prot, final TGetInfoResp struct) throws TException {
            final TTupleProtocol oprot = (TTupleProtocol)prot;
            struct.status.write(oprot);
            struct.infoValue.write(oprot);
        }
        
        @Override
        public void read(final TProtocol prot, final TGetInfoResp struct) throws TException {
            final TTupleProtocol iprot = (TTupleProtocol)prot;
            struct.status = new TStatus();
            struct.status.read(iprot);
            struct.setStatusIsSet(true);
            struct.infoValue = new TGetInfoValue();
            struct.infoValue.read(iprot);
            struct.setInfoValueIsSet(true);
        }
    }
}
