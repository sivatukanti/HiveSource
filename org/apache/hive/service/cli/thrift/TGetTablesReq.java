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

public class TGetTablesReq implements TBase<TGetTablesReq, _Fields>, Serializable, Cloneable
{
    private static final TStruct STRUCT_DESC;
    private static final TField SESSION_HANDLE_FIELD_DESC;
    private static final TField CATALOG_NAME_FIELD_DESC;
    private static final TField SCHEMA_NAME_FIELD_DESC;
    private static final TField TABLE_NAME_FIELD_DESC;
    private static final TField TABLE_TYPES_FIELD_DESC;
    private static final Map<Class<? extends IScheme>, SchemeFactory> schemes;
    private TSessionHandle sessionHandle;
    private String catalogName;
    private String schemaName;
    private String tableName;
    private List<String> tableTypes;
    private _Fields[] optionals;
    public static final Map<_Fields, FieldMetaData> metaDataMap;
    
    public TGetTablesReq() {
        this.optionals = new _Fields[] { _Fields.CATALOG_NAME, _Fields.SCHEMA_NAME, _Fields.TABLE_NAME, _Fields.TABLE_TYPES };
    }
    
    public TGetTablesReq(final TSessionHandle sessionHandle) {
        this();
        this.sessionHandle = sessionHandle;
    }
    
    public TGetTablesReq(final TGetTablesReq other) {
        this.optionals = new _Fields[] { _Fields.CATALOG_NAME, _Fields.SCHEMA_NAME, _Fields.TABLE_NAME, _Fields.TABLE_TYPES };
        if (other.isSetSessionHandle()) {
            this.sessionHandle = new TSessionHandle(other.sessionHandle);
        }
        if (other.isSetCatalogName()) {
            this.catalogName = other.catalogName;
        }
        if (other.isSetSchemaName()) {
            this.schemaName = other.schemaName;
        }
        if (other.isSetTableName()) {
            this.tableName = other.tableName;
        }
        if (other.isSetTableTypes()) {
            final List<String> __this__tableTypes = new ArrayList<String>();
            for (final String other_element : other.tableTypes) {
                __this__tableTypes.add(other_element);
            }
            this.tableTypes = __this__tableTypes;
        }
    }
    
    @Override
    public TGetTablesReq deepCopy() {
        return new TGetTablesReq(this);
    }
    
    @Override
    public void clear() {
        this.sessionHandle = null;
        this.catalogName = null;
        this.schemaName = null;
        this.tableName = null;
        this.tableTypes = null;
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
    
    public String getTableName() {
        return this.tableName;
    }
    
    public void setTableName(final String tableName) {
        this.tableName = tableName;
    }
    
    public void unsetTableName() {
        this.tableName = null;
    }
    
    public boolean isSetTableName() {
        return this.tableName != null;
    }
    
    public void setTableNameIsSet(final boolean value) {
        if (!value) {
            this.tableName = null;
        }
    }
    
    public int getTableTypesSize() {
        return (this.tableTypes == null) ? 0 : this.tableTypes.size();
    }
    
    public Iterator<String> getTableTypesIterator() {
        return (this.tableTypes == null) ? null : this.tableTypes.iterator();
    }
    
    public void addToTableTypes(final String elem) {
        if (this.tableTypes == null) {
            this.tableTypes = new ArrayList<String>();
        }
        this.tableTypes.add(elem);
    }
    
    public List<String> getTableTypes() {
        return this.tableTypes;
    }
    
    public void setTableTypes(final List<String> tableTypes) {
        this.tableTypes = tableTypes;
    }
    
    public void unsetTableTypes() {
        this.tableTypes = null;
    }
    
    public boolean isSetTableTypes() {
        return this.tableTypes != null;
    }
    
    public void setTableTypesIsSet(final boolean value) {
        if (!value) {
            this.tableTypes = null;
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
            case TABLE_NAME: {
                if (value == null) {
                    this.unsetTableName();
                    break;
                }
                this.setTableName((String)value);
                break;
            }
            case TABLE_TYPES: {
                if (value == null) {
                    this.unsetTableTypes();
                    break;
                }
                this.setTableTypes((List<String>)value);
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
            case TABLE_NAME: {
                return this.getTableName();
            }
            case TABLE_TYPES: {
                return this.getTableTypes();
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
            case TABLE_NAME: {
                return this.isSetTableName();
            }
            case TABLE_TYPES: {
                return this.isSetTableTypes();
            }
            default: {
                throw new IllegalStateException();
            }
        }
    }
    
    @Override
    public boolean equals(final Object that) {
        return that != null && that instanceof TGetTablesReq && this.equals((TGetTablesReq)that);
    }
    
    public boolean equals(final TGetTablesReq that) {
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
        final boolean this_present_tableName = this.isSetTableName();
        final boolean that_present_tableName = that.isSetTableName();
        if (this_present_tableName || that_present_tableName) {
            if (!this_present_tableName || !that_present_tableName) {
                return false;
            }
            if (!this.tableName.equals(that.tableName)) {
                return false;
            }
        }
        final boolean this_present_tableTypes = this.isSetTableTypes();
        final boolean that_present_tableTypes = that.isSetTableTypes();
        if (this_present_tableTypes || that_present_tableTypes) {
            if (!this_present_tableTypes || !that_present_tableTypes) {
                return false;
            }
            if (!this.tableTypes.equals(that.tableTypes)) {
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
        final boolean present_tableName = this.isSetTableName();
        builder.append(present_tableName);
        if (present_tableName) {
            builder.append(this.tableName);
        }
        final boolean present_tableTypes = this.isSetTableTypes();
        builder.append(present_tableTypes);
        if (present_tableTypes) {
            builder.append(this.tableTypes);
        }
        return builder.toHashCode();
    }
    
    @Override
    public int compareTo(final TGetTablesReq other) {
        if (!this.getClass().equals(other.getClass())) {
            return this.getClass().getName().compareTo(other.getClass().getName());
        }
        int lastComparison = 0;
        final TGetTablesReq typedOther = other;
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
        lastComparison = Boolean.valueOf(this.isSetTableName()).compareTo(Boolean.valueOf(typedOther.isSetTableName()));
        if (lastComparison != 0) {
            return lastComparison;
        }
        if (this.isSetTableName()) {
            lastComparison = TBaseHelper.compareTo(this.tableName, typedOther.tableName);
            if (lastComparison != 0) {
                return lastComparison;
            }
        }
        lastComparison = Boolean.valueOf(this.isSetTableTypes()).compareTo(Boolean.valueOf(typedOther.isSetTableTypes()));
        if (lastComparison != 0) {
            return lastComparison;
        }
        if (this.isSetTableTypes()) {
            lastComparison = TBaseHelper.compareTo(this.tableTypes, typedOther.tableTypes);
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
        TGetTablesReq.schemes.get(iprot.getScheme()).getScheme().read(iprot, this);
    }
    
    @Override
    public void write(final TProtocol oprot) throws TException {
        TGetTablesReq.schemes.get(oprot.getScheme()).getScheme().write(oprot, this);
    }
    
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("TGetTablesReq(");
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
        if (this.isSetTableName()) {
            if (!first) {
                sb.append(", ");
            }
            sb.append("tableName:");
            if (this.tableName == null) {
                sb.append("null");
            }
            else {
                sb.append(this.tableName);
            }
            first = false;
        }
        if (this.isSetTableTypes()) {
            if (!first) {
                sb.append(", ");
            }
            sb.append("tableTypes:");
            if (this.tableTypes == null) {
                sb.append("null");
            }
            else {
                sb.append(this.tableTypes);
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
        STRUCT_DESC = new TStruct("TGetTablesReq");
        SESSION_HANDLE_FIELD_DESC = new TField("sessionHandle", (byte)12, (short)1);
        CATALOG_NAME_FIELD_DESC = new TField("catalogName", (byte)11, (short)2);
        SCHEMA_NAME_FIELD_DESC = new TField("schemaName", (byte)11, (short)3);
        TABLE_NAME_FIELD_DESC = new TField("tableName", (byte)11, (short)4);
        TABLE_TYPES_FIELD_DESC = new TField("tableTypes", (byte)15, (short)5);
        (schemes = new HashMap<Class<? extends IScheme>, SchemeFactory>()).put(StandardScheme.class, new TGetTablesReqStandardSchemeFactory());
        TGetTablesReq.schemes.put(TupleScheme.class, new TGetTablesReqTupleSchemeFactory());
        final Map<_Fields, FieldMetaData> tmpMap = new EnumMap<_Fields, FieldMetaData>(_Fields.class);
        tmpMap.put(_Fields.SESSION_HANDLE, new FieldMetaData("sessionHandle", (byte)1, new StructMetaData((byte)12, TSessionHandle.class)));
        tmpMap.put(_Fields.CATALOG_NAME, new FieldMetaData("catalogName", (byte)2, new FieldValueMetaData((byte)11, "TPatternOrIdentifier")));
        tmpMap.put(_Fields.SCHEMA_NAME, new FieldMetaData("schemaName", (byte)2, new FieldValueMetaData((byte)11, "TPatternOrIdentifier")));
        tmpMap.put(_Fields.TABLE_NAME, new FieldMetaData("tableName", (byte)2, new FieldValueMetaData((byte)11, "TPatternOrIdentifier")));
        tmpMap.put(_Fields.TABLE_TYPES, new FieldMetaData("tableTypes", (byte)2, new ListMetaData((byte)15, new FieldValueMetaData((byte)11))));
        FieldMetaData.addStructMetaDataMap(TGetTablesReq.class, metaDataMap = Collections.unmodifiableMap((Map<? extends _Fields, ? extends FieldMetaData>)tmpMap));
    }
    
    public enum _Fields implements TFieldIdEnum
    {
        SESSION_HANDLE((short)1, "sessionHandle"), 
        CATALOG_NAME((short)2, "catalogName"), 
        SCHEMA_NAME((short)3, "schemaName"), 
        TABLE_NAME((short)4, "tableName"), 
        TABLE_TYPES((short)5, "tableTypes");
        
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
                case 4: {
                    return _Fields.TABLE_NAME;
                }
                case 5: {
                    return _Fields.TABLE_TYPES;
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
    
    private static class TGetTablesReqStandardSchemeFactory implements SchemeFactory
    {
        @Override
        public TGetTablesReqStandardScheme getScheme() {
            return new TGetTablesReqStandardScheme();
        }
    }
    
    private static class TGetTablesReqStandardScheme extends StandardScheme<TGetTablesReq>
    {
        @Override
        public void read(final TProtocol iprot, final TGetTablesReq struct) throws TException {
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
                    case 4: {
                        if (schemeField.type == 11) {
                            struct.tableName = iprot.readString();
                            struct.setTableNameIsSet(true);
                            break;
                        }
                        TProtocolUtil.skip(iprot, schemeField.type);
                        break;
                    }
                    case 5: {
                        if (schemeField.type == 15) {
                            final TList _list172 = iprot.readListBegin();
                            struct.tableTypes = (List<String>)new ArrayList(_list172.size);
                            for (int _i173 = 0; _i173 < _list172.size; ++_i173) {
                                final String _elem174 = iprot.readString();
                                struct.tableTypes.add(_elem174);
                            }
                            iprot.readListEnd();
                            struct.setTableTypesIsSet(true);
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
        public void write(final TProtocol oprot, final TGetTablesReq struct) throws TException {
            struct.validate();
            oprot.writeStructBegin(TGetTablesReq.STRUCT_DESC);
            if (struct.sessionHandle != null) {
                oprot.writeFieldBegin(TGetTablesReq.SESSION_HANDLE_FIELD_DESC);
                struct.sessionHandle.write(oprot);
                oprot.writeFieldEnd();
            }
            if (struct.catalogName != null && struct.isSetCatalogName()) {
                oprot.writeFieldBegin(TGetTablesReq.CATALOG_NAME_FIELD_DESC);
                oprot.writeString(struct.catalogName);
                oprot.writeFieldEnd();
            }
            if (struct.schemaName != null && struct.isSetSchemaName()) {
                oprot.writeFieldBegin(TGetTablesReq.SCHEMA_NAME_FIELD_DESC);
                oprot.writeString(struct.schemaName);
                oprot.writeFieldEnd();
            }
            if (struct.tableName != null && struct.isSetTableName()) {
                oprot.writeFieldBegin(TGetTablesReq.TABLE_NAME_FIELD_DESC);
                oprot.writeString(struct.tableName);
                oprot.writeFieldEnd();
            }
            if (struct.tableTypes != null && struct.isSetTableTypes()) {
                oprot.writeFieldBegin(TGetTablesReq.TABLE_TYPES_FIELD_DESC);
                oprot.writeListBegin(new TList((byte)11, struct.tableTypes.size()));
                for (final String _iter175 : struct.tableTypes) {
                    oprot.writeString(_iter175);
                }
                oprot.writeListEnd();
                oprot.writeFieldEnd();
            }
            oprot.writeFieldStop();
            oprot.writeStructEnd();
        }
    }
    
    private static class TGetTablesReqTupleSchemeFactory implements SchemeFactory
    {
        @Override
        public TGetTablesReqTupleScheme getScheme() {
            return new TGetTablesReqTupleScheme();
        }
    }
    
    private static class TGetTablesReqTupleScheme extends TupleScheme<TGetTablesReq>
    {
        @Override
        public void write(final TProtocol prot, final TGetTablesReq struct) throws TException {
            final TTupleProtocol oprot = (TTupleProtocol)prot;
            struct.sessionHandle.write(oprot);
            final BitSet optionals = new BitSet();
            if (struct.isSetCatalogName()) {
                optionals.set(0);
            }
            if (struct.isSetSchemaName()) {
                optionals.set(1);
            }
            if (struct.isSetTableName()) {
                optionals.set(2);
            }
            if (struct.isSetTableTypes()) {
                optionals.set(3);
            }
            oprot.writeBitSet(optionals, 4);
            if (struct.isSetCatalogName()) {
                oprot.writeString(struct.catalogName);
            }
            if (struct.isSetSchemaName()) {
                oprot.writeString(struct.schemaName);
            }
            if (struct.isSetTableName()) {
                oprot.writeString(struct.tableName);
            }
            if (struct.isSetTableTypes()) {
                oprot.writeI32(struct.tableTypes.size());
                for (final String _iter176 : struct.tableTypes) {
                    oprot.writeString(_iter176);
                }
            }
        }
        
        @Override
        public void read(final TProtocol prot, final TGetTablesReq struct) throws TException {
            final TTupleProtocol iprot = (TTupleProtocol)prot;
            struct.sessionHandle = new TSessionHandle();
            struct.sessionHandle.read(iprot);
            struct.setSessionHandleIsSet(true);
            final BitSet incoming = iprot.readBitSet(4);
            if (incoming.get(0)) {
                struct.catalogName = iprot.readString();
                struct.setCatalogNameIsSet(true);
            }
            if (incoming.get(1)) {
                struct.schemaName = iprot.readString();
                struct.setSchemaNameIsSet(true);
            }
            if (incoming.get(2)) {
                struct.tableName = iprot.readString();
                struct.setTableNameIsSet(true);
            }
            if (incoming.get(3)) {
                final TList _list177 = new TList((byte)11, iprot.readI32());
                struct.tableTypes = (List<String>)new ArrayList(_list177.size);
                for (int _i178 = 0; _i178 < _list177.size; ++_i178) {
                    final String _elem179 = iprot.readString();
                    struct.tableTypes.add(_elem179);
                }
                struct.setTableTypesIsSet(true);
            }
        }
    }
}
