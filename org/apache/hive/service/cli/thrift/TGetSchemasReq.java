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

public class TGetSchemasReq implements TBase<TGetSchemasReq, _Fields>, Serializable, Cloneable
{
    private static final TStruct STRUCT_DESC;
    private static final TField SESSION_HANDLE_FIELD_DESC;
    private static final TField CATALOG_NAME_FIELD_DESC;
    private static final TField SCHEMA_NAME_FIELD_DESC;
    private static final Map<Class<? extends IScheme>, SchemeFactory> schemes;
    private TSessionHandle sessionHandle;
    private String catalogName;
    private String schemaName;
    private _Fields[] optionals;
    public static final Map<_Fields, FieldMetaData> metaDataMap;
    
    public TGetSchemasReq() {
        this.optionals = new _Fields[] { _Fields.CATALOG_NAME, _Fields.SCHEMA_NAME };
    }
    
    public TGetSchemasReq(final TSessionHandle sessionHandle) {
        this();
        this.sessionHandle = sessionHandle;
    }
    
    public TGetSchemasReq(final TGetSchemasReq other) {
        this.optionals = new _Fields[] { _Fields.CATALOG_NAME, _Fields.SCHEMA_NAME };
        if (other.isSetSessionHandle()) {
            this.sessionHandle = new TSessionHandle(other.sessionHandle);
        }
        if (other.isSetCatalogName()) {
            this.catalogName = other.catalogName;
        }
        if (other.isSetSchemaName()) {
            this.schemaName = other.schemaName;
        }
    }
    
    @Override
    public TGetSchemasReq deepCopy() {
        return new TGetSchemasReq(this);
    }
    
    @Override
    public void clear() {
        this.sessionHandle = null;
        this.catalogName = null;
        this.schemaName = null;
    }
    
    public TSessionHandle getSessionHandle() {
        return this.sessionHandle;
    }
    
    public void setSessionHandle(final TSessionHandle sessionHandle) {
        this.sessionHandle = sessionHandle;
    }
    
    public void unsetSessionHandle() {
        this.sessionHandle = null;
    }
    
    public boolean isSetSessionHandle() {
        return this.sessionHandle != null;
    }
    
    public void setSessionHandleIsSet(final boolean value) {
        if (!value) {
            this.sessionHandle = null;
        }
    }
    
    public String getCatalogName() {
        return this.catalogName;
    }
    
    public void setCatalogName(final String catalogName) {
        this.catalogName = catalogName;
    }
    
    public void unsetCatalogName() {
        this.catalogName = null;
    }
    
    public boolean isSetCatalogName() {
        return this.catalogName != null;
    }
    
    public void setCatalogNameIsSet(final boolean value) {
        if (!value) {
            this.catalogName = null;
        }
    }
    
    public String getSchemaName() {
        return this.schemaName;
    }
    
    public void setSchemaName(final String schemaName) {
        this.schemaName = schemaName;
    }
    
    public void unsetSchemaName() {
        this.schemaName = null;
    }
    
    public boolean isSetSchemaName() {
        return this.schemaName != null;
    }
    
    public void setSchemaNameIsSet(final boolean value) {
        if (!value) {
            this.schemaName = null;
        }
    }
    
    @Override
    public void setFieldValue(final _Fields field, final Object value) {
        switch (field) {
            case SESSION_HANDLE: {
                if (value == null) {
                    this.unsetSessionHandle();
                    break;
                }
                this.setSessionHandle((TSessionHandle)value);
                break;
            }
            case CATALOG_NAME: {
                if (value == null) {
                    this.unsetCatalogName();
                    break;
                }
                this.setCatalogName((String)value);
                break;
            }
            case SCHEMA_NAME: {
                if (value == null) {
                    this.unsetSchemaName();
                    break;
                }
                this.setSchemaName((String)value);
                break;
            }
        }
    }
    
    @Override
    public Object getFieldValue(final _Fields field) {
        switch (field) {
            case SESSION_HANDLE: {
                return this.getSessionHandle();
            }
            case CATALOG_NAME: {
                return this.getCatalogName();
            }
            case SCHEMA_NAME: {
                return this.getSchemaName();
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
            case SESSION_HANDLE: {
                return this.isSetSessionHandle();
            }
            case CATALOG_NAME: {
                return this.isSetCatalogName();
            }
            case SCHEMA_NAME: {
                return this.isSetSchemaName();
            }
            default: {
                throw new IllegalStateException();
            }
        }
    }
    
    @Override
    public boolean equals(final Object that) {
        return that != null && that instanceof TGetSchemasReq && this.equals((TGetSchemasReq)that);
    }
    
    public boolean equals(final TGetSchemasReq that) {
        if (that == null) {
            return false;
        }
        final boolean this_present_sessionHandle = this.isSetSessionHandle();
        final boolean that_present_sessionHandle = that.isSetSessionHandle();
        if (this_present_sessionHandle || that_present_sessionHandle) {
            if (!this_present_sessionHandle || !that_present_sessionHandle) {
                return false;
            }
            if (!this.sessionHandle.equals(that.sessionHandle)) {
                return false;
            }
        }
        final boolean this_present_catalogName = this.isSetCatalogName();
        final boolean that_present_catalogName = that.isSetCatalogName();
        if (this_present_catalogName || that_present_catalogName) {
            if (!this_present_catalogName || !that_present_catalogName) {
                return false;
            }
            if (!this.catalogName.equals(that.catalogName)) {
                return false;
            }
        }
        final boolean this_present_schemaName = this.isSetSchemaName();
        final boolean that_present_schemaName = that.isSetSchemaName();
        if (this_present_schemaName || that_present_schemaName) {
            if (!this_present_schemaName || !that_present_schemaName) {
                return false;
            }
            if (!this.schemaName.equals(that.schemaName)) {
                return false;
            }
        }
        return true;
    }
    
    @Override
    public int hashCode() {
        final HashCodeBuilder builder = new HashCodeBuilder();
        final boolean present_sessionHandle = this.isSetSessionHandle();
        builder.append(present_sessionHandle);
        if (present_sessionHandle) {
            builder.append(this.sessionHandle);
        }
        final boolean present_catalogName = this.isSetCatalogName();
        builder.append(present_catalogName);
        if (present_catalogName) {
            builder.append(this.catalogName);
        }
        final boolean present_schemaName = this.isSetSchemaName();
        builder.append(present_schemaName);
        if (present_schemaName) {
            builder.append(this.schemaName);
        }
        return builder.toHashCode();
    }
    
    @Override
    public int compareTo(final TGetSchemasReq other) {
        if (!this.getClass().equals(other.getClass())) {
            return this.getClass().getName().compareTo(other.getClass().getName());
        }
        int lastComparison = 0;
        final TGetSchemasReq typedOther = other;
        lastComparison = Boolean.valueOf(this.isSetSessionHandle()).compareTo(Boolean.valueOf(typedOther.isSetSessionHandle()));
        if (lastComparison != 0) {
            return lastComparison;
        }
        if (this.isSetSessionHandle()) {
            lastComparison = TBaseHelper.compareTo(this.sessionHandle, typedOther.sessionHandle);
            if (lastComparison != 0) {
                return lastComparison;
            }
        }
        lastComparison = Boolean.valueOf(this.isSetCatalogName()).compareTo(Boolean.valueOf(typedOther.isSetCatalogName()));
        if (lastComparison != 0) {
            return lastComparison;
        }
        if (this.isSetCatalogName()) {
            lastComparison = TBaseHelper.compareTo(this.catalogName, typedOther.catalogName);
            if (lastComparison != 0) {
                return lastComparison;
            }
        }
        lastComparison = Boolean.valueOf(this.isSetSchemaName()).compareTo(Boolean.valueOf(typedOther.isSetSchemaName()));
        if (lastComparison != 0) {
            return lastComparison;
        }
        if (this.isSetSchemaName()) {
            lastComparison = TBaseHelper.compareTo(this.schemaName, typedOther.schemaName);
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
        TGetSchemasReq.schemes.get(iprot.getScheme()).getScheme().read(iprot, this);
    }
    
    @Override
    public void write(final TProtocol oprot) throws TException {
        TGetSchemasReq.schemes.get(oprot.getScheme()).getScheme().write(oprot, this);
    }
    
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("TGetSchemasReq(");
        boolean first = true;
        sb.append("sessionHandle:");
        if (this.sessionHandle == null) {
            sb.append("null");
        }
        else {
            sb.append(this.sessionHandle);
        }
        first = false;
        if (this.isSetCatalogName()) {
            if (!first) {
                sb.append(", ");
            }
            sb.append("catalogName:");
            if (this.catalogName == null) {
                sb.append("null");
            }
            else {
                sb.append(this.catalogName);
            }
            first = false;
        }
        if (this.isSetSchemaName()) {
            if (!first) {
                sb.append(", ");
            }
            sb.append("schemaName:");
            if (this.schemaName == null) {
                sb.append("null");
            }
            else {
                sb.append(this.schemaName);
            }
            first = false;
        }
        sb.append(")");
        return sb.toString();
    }
    
    public void validate() throws TException {
        if (!this.isSetSessionHandle()) {
            throw new TProtocolException("Required field 'sessionHandle' is unset! Struct:" + this.toString());
        }
        if (this.sessionHandle != null) {
            this.sessionHandle.validate();
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
        STRUCT_DESC = new TStruct("TGetSchemasReq");
        SESSION_HANDLE_FIELD_DESC = new TField("sessionHandle", (byte)12, (short)1);
        CATALOG_NAME_FIELD_DESC = new TField("catalogName", (byte)11, (short)2);
        SCHEMA_NAME_FIELD_DESC = new TField("schemaName", (byte)11, (short)3);
        (schemes = new HashMap<Class<? extends IScheme>, SchemeFactory>()).put(StandardScheme.class, new TGetSchemasReqStandardSchemeFactory());
        TGetSchemasReq.schemes.put(TupleScheme.class, new TGetSchemasReqTupleSchemeFactory());
        final Map<_Fields, FieldMetaData> tmpMap = new EnumMap<_Fields, FieldMetaData>(_Fields.class);
        tmpMap.put(_Fields.SESSION_HANDLE, new FieldMetaData("sessionHandle", (byte)1, new StructMetaData((byte)12, TSessionHandle.class)));
        tmpMap.put(_Fields.CATALOG_NAME, new FieldMetaData("catalogName", (byte)2, new FieldValueMetaData((byte)11, "TIdentifier")));
        tmpMap.put(_Fields.SCHEMA_NAME, new FieldMetaData("schemaName", (byte)2, new FieldValueMetaData((byte)11, "TPatternOrIdentifier")));
        FieldMetaData.addStructMetaDataMap(TGetSchemasReq.class, metaDataMap = Collections.unmodifiableMap((Map<? extends _Fields, ? extends FieldMetaData>)tmpMap));
    }
    
    public enum _Fields implements TFieldIdEnum
    {
        SESSION_HANDLE((short)1, "sessionHandle"), 
        CATALOG_NAME((short)2, "catalogName"), 
        SCHEMA_NAME((short)3, "schemaName");
        
        private static final Map<String, _Fields> byName;
        private final short _thriftId;
        private final String _fieldName;
        
        public static _Fields findByThriftId(final int fieldId) {
            switch (fieldId) {
                case 1: {
                    return _Fields.SESSION_HANDLE;
                }
                case 2: {
                    return _Fields.CATALOG_NAME;
                }
                case 3: {
                    return _Fields.SCHEMA_NAME;
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
    
    private static class TGetSchemasReqStandardSchemeFactory implements SchemeFactory
    {
        @Override
        public TGetSchemasReqStandardScheme getScheme() {
            return new TGetSchemasReqStandardScheme();
        }
    }
    
    private static class TGetSchemasReqStandardScheme extends StandardScheme<TGetSchemasReq>
    {
        @Override
        public void read(final TProtocol iprot, final TGetSchemasReq struct) throws TException {
            iprot.readStructBegin();
            while (true) {
                final TField schemeField = iprot.readFieldBegin();
                if (schemeField.type == 0) {
                    break;
                }
                switch (schemeField.id) {
                    case 1: {
                        if (schemeField.type == 12) {
                            struct.sessionHandle = new TSessionHandle();
                            struct.sessionHandle.read(iprot);
                            struct.setSessionHandleIsSet(true);
                            break;
                        }
                        TProtocolUtil.skip(iprot, schemeField.type);
                        break;
                    }
                    case 2: {
                        if (schemeField.type == 11) {
                            struct.catalogName = iprot.readString();
                            struct.setCatalogNameIsSet(true);
                            break;
                        }
                        TProtocolUtil.skip(iprot, schemeField.type);
                        break;
                    }
                    case 3: {
                        if (schemeField.type == 11) {
                            struct.schemaName = iprot.readString();
                            struct.setSchemaNameIsSet(true);
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
        public void write(final TProtocol oprot, final TGetSchemasReq struct) throws TException {
            struct.validate();
            oprot.writeStructBegin(TGetSchemasReq.STRUCT_DESC);
            if (struct.sessionHandle != null) {
                oprot.writeFieldBegin(TGetSchemasReq.SESSION_HANDLE_FIELD_DESC);
                struct.sessionHandle.write(oprot);
                oprot.writeFieldEnd();
            }
            if (struct.catalogName != null && struct.isSetCatalogName()) {
                oprot.writeFieldBegin(TGetSchemasReq.CATALOG_NAME_FIELD_DESC);
                oprot.writeString(struct.catalogName);
                oprot.writeFieldEnd();
            }
            if (struct.schemaName != null && struct.isSetSchemaName()) {
                oprot.writeFieldBegin(TGetSchemasReq.SCHEMA_NAME_FIELD_DESC);
                oprot.writeString(struct.schemaName);
                oprot.writeFieldEnd();
            }
            oprot.writeFieldStop();
            oprot.writeStructEnd();
        }
    }
    
    private static class TGetSchemasReqTupleSchemeFactory implements SchemeFactory
    {
        @Override
        public TGetSchemasReqTupleScheme getScheme() {
            return new TGetSchemasReqTupleScheme();
        }
    }
    
    private static class TGetSchemasReqTupleScheme extends TupleScheme<TGetSchemasReq>
    {
        @Override
        public void write(final TProtocol prot, final TGetSchemasReq struct) throws TException {
            final TTupleProtocol oprot = (TTupleProtocol)prot;
            struct.sessionHandle.write(oprot);
            final BitSet optionals = new BitSet();
            if (struct.isSetCatalogName()) {
                optionals.set(0);
            }
            if (struct.isSetSchemaName()) {
                optionals.set(1);
            }
            oprot.writeBitSet(optionals, 2);
            if (struct.isSetCatalogName()) {
                oprot.writeString(struct.catalogName);
            }
            if (struct.isSetSchemaName()) {
                oprot.writeString(struct.schemaName);
            }
        }
        
        @Override
        public void read(final TProtocol prot, final TGetSchemasReq struct) throws TException {
            final TTupleProtocol iprot = (TTupleProtocol)prot;
            struct.sessionHandle = new TSessionHandle();
            struct.sessionHandle.read(iprot);
            struct.setSessionHandleIsSet(true);
            final BitSet incoming = iprot.readBitSet(2);
            if (incoming.get(0)) {
                struct.catalogName = iprot.readString();
                struct.setCatalogNameIsSet(true);
            }
            if (incoming.get(1)) {
                struct.schemaName = iprot.readString();
                struct.setSchemaNameIsSet(true);
            }
        }
    }
}
