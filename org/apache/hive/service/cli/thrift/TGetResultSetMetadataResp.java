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

public class TGetResultSetMetadataResp implements TBase<TGetResultSetMetadataResp, _Fields>, Serializable, Cloneable
{
    private static final TStruct STRUCT_DESC;
    private static final TField STATUS_FIELD_DESC;
    private static final TField SCHEMA_FIELD_DESC;
    private static final Map<Class<? extends IScheme>, SchemeFactory> schemes;
    private TStatus status;
    private TTableSchema schema;
    private _Fields[] optionals;
    public static final Map<_Fields, FieldMetaData> metaDataMap;
    
    public TGetResultSetMetadataResp() {
        this.optionals = new _Fields[] { _Fields.SCHEMA };
    }
    
    public TGetResultSetMetadataResp(final TStatus status) {
        this();
        this.status = status;
    }
    
    public TGetResultSetMetadataResp(final TGetResultSetMetadataResp other) {
        this.optionals = new _Fields[] { _Fields.SCHEMA };
        if (other.isSetStatus()) {
            this.status = new TStatus(other.status);
        }
        if (other.isSetSchema()) {
            this.schema = new TTableSchema(other.schema);
        }
    }
    
    @Override
    public TGetResultSetMetadataResp deepCopy() {
        return new TGetResultSetMetadataResp(this);
    }
    
    @Override
    public void clear() {
        this.status = null;
        this.schema = null;
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
    
    public TTableSchema getSchema() {
        return this.schema;
    }
    
    public void setSchema(final TTableSchema schema) {
        this.schema = schema;
    }
    
    public void unsetSchema() {
        this.schema = null;
    }
    
    public boolean isSetSchema() {
        return this.schema != null;
    }
    
    public void setSchemaIsSet(final boolean value) {
        if (!value) {
            this.schema = null;
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
            case SCHEMA: {
                if (value == null) {
                    this.unsetSchema();
                    break;
                }
                this.setSchema((TTableSchema)value);
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
            case SCHEMA: {
                return this.getSchema();
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
            case SCHEMA: {
                return this.isSetSchema();
            }
            default: {
                throw new IllegalStateException();
            }
        }
    }
    
    @Override
    public boolean equals(final Object that) {
        return that != null && that instanceof TGetResultSetMetadataResp && this.equals((TGetResultSetMetadataResp)that);
    }
    
    public boolean equals(final TGetResultSetMetadataResp that) {
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
        final boolean this_present_schema = this.isSetSchema();
        final boolean that_present_schema = that.isSetSchema();
        if (this_present_schema || that_present_schema) {
            if (!this_present_schema || !that_present_schema) {
                return false;
            }
            if (!this.schema.equals(that.schema)) {
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
        final boolean present_schema = this.isSetSchema();
        builder.append(present_schema);
        if (present_schema) {
            builder.append(this.schema);
        }
        return builder.toHashCode();
    }
    
    @Override
    public int compareTo(final TGetResultSetMetadataResp other) {
        if (!this.getClass().equals(other.getClass())) {
            return this.getClass().getName().compareTo(other.getClass().getName());
        }
        int lastComparison = 0;
        final TGetResultSetMetadataResp typedOther = other;
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
        lastComparison = Boolean.valueOf(this.isSetSchema()).compareTo(Boolean.valueOf(typedOther.isSetSchema()));
        if (lastComparison != 0) {
            return lastComparison;
        }
        if (this.isSetSchema()) {
            lastComparison = TBaseHelper.compareTo(this.schema, typedOther.schema);
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
        TGetResultSetMetadataResp.schemes.get(iprot.getScheme()).getScheme().read(iprot, this);
    }
    
    @Override
    public void write(final TProtocol oprot) throws TException {
        TGetResultSetMetadataResp.schemes.get(oprot.getScheme()).getScheme().write(oprot, this);
    }
    
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("TGetResultSetMetadataResp(");
        boolean first = true;
        sb.append("status:");
        if (this.status == null) {
            sb.append("null");
        }
        else {
            sb.append(this.status);
        }
        first = false;
        if (this.isSetSchema()) {
            if (!first) {
                sb.append(", ");
            }
            sb.append("schema:");
            if (this.schema == null) {
                sb.append("null");
            }
            else {
                sb.append(this.schema);
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
        if (this.schema != null) {
            this.schema.validate();
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
        STRUCT_DESC = new TStruct("TGetResultSetMetadataResp");
        STATUS_FIELD_DESC = new TField("status", (byte)12, (short)1);
        SCHEMA_FIELD_DESC = new TField("schema", (byte)12, (short)2);
        (schemes = new HashMap<Class<? extends IScheme>, SchemeFactory>()).put(StandardScheme.class, new TGetResultSetMetadataRespStandardSchemeFactory());
        TGetResultSetMetadataResp.schemes.put(TupleScheme.class, new TGetResultSetMetadataRespTupleSchemeFactory());
        final Map<_Fields, FieldMetaData> tmpMap = new EnumMap<_Fields, FieldMetaData>(_Fields.class);
        tmpMap.put(_Fields.STATUS, new FieldMetaData("status", (byte)1, new StructMetaData((byte)12, TStatus.class)));
        tmpMap.put(_Fields.SCHEMA, new FieldMetaData("schema", (byte)2, new StructMetaData((byte)12, TTableSchema.class)));
        FieldMetaData.addStructMetaDataMap(TGetResultSetMetadataResp.class, metaDataMap = Collections.unmodifiableMap((Map<? extends _Fields, ? extends FieldMetaData>)tmpMap));
    }
    
    public enum _Fields implements TFieldIdEnum
    {
        STATUS((short)1, "status"), 
        SCHEMA((short)2, "schema");
        
        private static final Map<String, _Fields> byName;
        private final short _thriftId;
        private final String _fieldName;
        
        public static _Fields findByThriftId(final int fieldId) {
            switch (fieldId) {
                case 1: {
                    return _Fields.STATUS;
                }
                case 2: {
                    return _Fields.SCHEMA;
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
    
    private static class TGetResultSetMetadataRespStandardSchemeFactory implements SchemeFactory
    {
        @Override
        public TGetResultSetMetadataRespStandardScheme getScheme() {
            return new TGetResultSetMetadataRespStandardScheme();
        }
    }
    
    private static class TGetResultSetMetadataRespStandardScheme extends StandardScheme<TGetResultSetMetadataResp>
    {
        @Override
        public void read(final TProtocol iprot, final TGetResultSetMetadataResp struct) throws TException {
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
                            struct.schema = new TTableSchema();
                            struct.schema.read(iprot);
                            struct.setSchemaIsSet(true);
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
        public void write(final TProtocol oprot, final TGetResultSetMetadataResp struct) throws TException {
            struct.validate();
            oprot.writeStructBegin(TGetResultSetMetadataResp.STRUCT_DESC);
            if (struct.status != null) {
                oprot.writeFieldBegin(TGetResultSetMetadataResp.STATUS_FIELD_DESC);
                struct.status.write(oprot);
                oprot.writeFieldEnd();
            }
            if (struct.schema != null && struct.isSetSchema()) {
                oprot.writeFieldBegin(TGetResultSetMetadataResp.SCHEMA_FIELD_DESC);
                struct.schema.write(oprot);
                oprot.writeFieldEnd();
            }
            oprot.writeFieldStop();
            oprot.writeStructEnd();
        }
    }
    
    private static class TGetResultSetMetadataRespTupleSchemeFactory implements SchemeFactory
    {
        @Override
        public TGetResultSetMetadataRespTupleScheme getScheme() {
            return new TGetResultSetMetadataRespTupleScheme();
        }
    }
    
    private static class TGetResultSetMetadataRespTupleScheme extends TupleScheme<TGetResultSetMetadataResp>
    {
        @Override
        public void write(final TProtocol prot, final TGetResultSetMetadataResp struct) throws TException {
            final TTupleProtocol oprot = (TTupleProtocol)prot;
            struct.status.write(oprot);
            final BitSet optionals = new BitSet();
            if (struct.isSetSchema()) {
                optionals.set(0);
            }
            oprot.writeBitSet(optionals, 1);
            if (struct.isSetSchema()) {
                struct.schema.write(oprot);
            }
        }
        
        @Override
        public void read(final TProtocol prot, final TGetResultSetMetadataResp struct) throws TException {
            final TTupleProtocol iprot = (TTupleProtocol)prot;
            struct.status = new TStatus();
            struct.status.read(iprot);
            struct.setStatusIsSet(true);
            final BitSet incoming = iprot.readBitSet(1);
            if (incoming.get(0)) {
                struct.schema = new TTableSchema();
                struct.schema.read(iprot);
                struct.setSchemaIsSet(true);
            }
        }
    }
}
