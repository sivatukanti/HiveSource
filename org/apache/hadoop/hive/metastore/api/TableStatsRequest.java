// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.hive.metastore.api;

import org.apache.thrift.protocol.TTupleProtocol;
import org.apache.thrift.protocol.TList;
import org.apache.thrift.protocol.TProtocolUtil;
import java.util.EnumSet;
import java.util.Collections;
import org.apache.thrift.meta_data.ListMetaData;
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

public class TableStatsRequest implements TBase<TableStatsRequest, _Fields>, Serializable, Cloneable
{
    private static final TStruct STRUCT_DESC;
    private static final TField DB_NAME_FIELD_DESC;
    private static final TField TBL_NAME_FIELD_DESC;
    private static final TField COL_NAMES_FIELD_DESC;
    private static final Map<Class<? extends IScheme>, SchemeFactory> schemes;
    private String dbName;
    private String tblName;
    private List<String> colNames;
    public static final Map<_Fields, FieldMetaData> metaDataMap;
    
    public TableStatsRequest() {
    }
    
    public TableStatsRequest(final String dbName, final String tblName, final List<String> colNames) {
        this();
        this.dbName = dbName;
        this.tblName = tblName;
        this.colNames = colNames;
    }
    
    public TableStatsRequest(final TableStatsRequest other) {
        if (other.isSetDbName()) {
            this.dbName = other.dbName;
        }
        if (other.isSetTblName()) {
            this.tblName = other.tblName;
        }
        if (other.isSetColNames()) {
            final List<String> __this__colNames = new ArrayList<String>();
            for (final String other_element : other.colNames) {
                __this__colNames.add(other_element);
            }
            this.colNames = __this__colNames;
        }
    }
    
    @Override
    public TableStatsRequest deepCopy() {
        return new TableStatsRequest(this);
    }
    
    @Override
    public void clear() {
        this.dbName = null;
        this.tblName = null;
        this.colNames = null;
    }
    
    public String getDbName() {
        return this.dbName;
    }
    
    public void setDbName(final String dbName) {
        this.dbName = dbName;
    }
    
    public void unsetDbName() {
        this.dbName = null;
    }
    
    public boolean isSetDbName() {
        return this.dbName != null;
    }
    
    public void setDbNameIsSet(final boolean value) {
        if (!value) {
            this.dbName = null;
        }
    }
    
    public String getTblName() {
        return this.tblName;
    }
    
    public void setTblName(final String tblName) {
        this.tblName = tblName;
    }
    
    public void unsetTblName() {
        this.tblName = null;
    }
    
    public boolean isSetTblName() {
        return this.tblName != null;
    }
    
    public void setTblNameIsSet(final boolean value) {
        if (!value) {
            this.tblName = null;
        }
    }
    
    public int getColNamesSize() {
        return (this.colNames == null) ? 0 : this.colNames.size();
    }
    
    public Iterator<String> getColNamesIterator() {
        return (this.colNames == null) ? null : this.colNames.iterator();
    }
    
    public void addToColNames(final String elem) {
        if (this.colNames == null) {
            this.colNames = new ArrayList<String>();
        }
        this.colNames.add(elem);
    }
    
    public List<String> getColNames() {
        return this.colNames;
    }
    
    public void setColNames(final List<String> colNames) {
        this.colNames = colNames;
    }
    
    public void unsetColNames() {
        this.colNames = null;
    }
    
    public boolean isSetColNames() {
        return this.colNames != null;
    }
    
    public void setColNamesIsSet(final boolean value) {
        if (!value) {
            this.colNames = null;
        }
    }
    
    @Override
    public void setFieldValue(final _Fields field, final Object value) {
        switch (field) {
            case DB_NAME: {
                if (value == null) {
                    this.unsetDbName();
                    break;
                }
                this.setDbName((String)value);
                break;
            }
            case TBL_NAME: {
                if (value == null) {
                    this.unsetTblName();
                    break;
                }
                this.setTblName((String)value);
                break;
            }
            case COL_NAMES: {
                if (value == null) {
                    this.unsetColNames();
                    break;
                }
                this.setColNames((List<String>)value);
                break;
            }
        }
    }
    
    @Override
    public Object getFieldValue(final _Fields field) {
        switch (field) {
            case DB_NAME: {
                return this.getDbName();
            }
            case TBL_NAME: {
                return this.getTblName();
            }
            case COL_NAMES: {
                return this.getColNames();
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
            case DB_NAME: {
                return this.isSetDbName();
            }
            case TBL_NAME: {
                return this.isSetTblName();
            }
            case COL_NAMES: {
                return this.isSetColNames();
            }
            default: {
                throw new IllegalStateException();
            }
        }
    }
    
    @Override
    public boolean equals(final Object that) {
        return that != null && that instanceof TableStatsRequest && this.equals((TableStatsRequest)that);
    }
    
    public boolean equals(final TableStatsRequest that) {
        if (that == null) {
            return false;
        }
        final boolean this_present_dbName = this.isSetDbName();
        final boolean that_present_dbName = that.isSetDbName();
        if (this_present_dbName || that_present_dbName) {
            if (!this_present_dbName || !that_present_dbName) {
                return false;
            }
            if (!this.dbName.equals(that.dbName)) {
                return false;
            }
        }
        final boolean this_present_tblName = this.isSetTblName();
        final boolean that_present_tblName = that.isSetTblName();
        if (this_present_tblName || that_present_tblName) {
            if (!this_present_tblName || !that_present_tblName) {
                return false;
            }
            if (!this.tblName.equals(that.tblName)) {
                return false;
            }
        }
        final boolean this_present_colNames = this.isSetColNames();
        final boolean that_present_colNames = that.isSetColNames();
        if (this_present_colNames || that_present_colNames) {
            if (!this_present_colNames || !that_present_colNames) {
                return false;
            }
            if (!this.colNames.equals(that.colNames)) {
                return false;
            }
        }
        return true;
    }
    
    @Override
    public int hashCode() {
        final HashCodeBuilder builder = new HashCodeBuilder();
        final boolean present_dbName = this.isSetDbName();
        builder.append(present_dbName);
        if (present_dbName) {
            builder.append(this.dbName);
        }
        final boolean present_tblName = this.isSetTblName();
        builder.append(present_tblName);
        if (present_tblName) {
            builder.append(this.tblName);
        }
        final boolean present_colNames = this.isSetColNames();
        builder.append(present_colNames);
        if (present_colNames) {
            builder.append(this.colNames);
        }
        return builder.toHashCode();
    }
    
    @Override
    public int compareTo(final TableStatsRequest other) {
        if (!this.getClass().equals(other.getClass())) {
            return this.getClass().getName().compareTo(other.getClass().getName());
        }
        int lastComparison = 0;
        final TableStatsRequest typedOther = other;
        lastComparison = Boolean.valueOf(this.isSetDbName()).compareTo(Boolean.valueOf(typedOther.isSetDbName()));
        if (lastComparison != 0) {
            return lastComparison;
        }
        if (this.isSetDbName()) {
            lastComparison = TBaseHelper.compareTo(this.dbName, typedOther.dbName);
            if (lastComparison != 0) {
                return lastComparison;
            }
        }
        lastComparison = Boolean.valueOf(this.isSetTblName()).compareTo(Boolean.valueOf(typedOther.isSetTblName()));
        if (lastComparison != 0) {
            return lastComparison;
        }
        if (this.isSetTblName()) {
            lastComparison = TBaseHelper.compareTo(this.tblName, typedOther.tblName);
            if (lastComparison != 0) {
                return lastComparison;
            }
        }
        lastComparison = Boolean.valueOf(this.isSetColNames()).compareTo(Boolean.valueOf(typedOther.isSetColNames()));
        if (lastComparison != 0) {
            return lastComparison;
        }
        if (this.isSetColNames()) {
            lastComparison = TBaseHelper.compareTo(this.colNames, typedOther.colNames);
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
        TableStatsRequest.schemes.get(iprot.getScheme()).getScheme().read(iprot, this);
    }
    
    @Override
    public void write(final TProtocol oprot) throws TException {
        TableStatsRequest.schemes.get(oprot.getScheme()).getScheme().write(oprot, this);
    }
    
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("TableStatsRequest(");
        boolean first = true;
        sb.append("dbName:");
        if (this.dbName == null) {
            sb.append("null");
        }
        else {
            sb.append(this.dbName);
        }
        first = false;
        if (!first) {
            sb.append(", ");
        }
        sb.append("tblName:");
        if (this.tblName == null) {
            sb.append("null");
        }
        else {
            sb.append(this.tblName);
        }
        first = false;
        if (!first) {
            sb.append(", ");
        }
        sb.append("colNames:");
        if (this.colNames == null) {
            sb.append("null");
        }
        else {
            sb.append(this.colNames);
        }
        first = false;
        sb.append(")");
        return sb.toString();
    }
    
    public void validate() throws TException {
        if (!this.isSetDbName()) {
            throw new TProtocolException("Required field 'dbName' is unset! Struct:" + this.toString());
        }
        if (!this.isSetTblName()) {
            throw new TProtocolException("Required field 'tblName' is unset! Struct:" + this.toString());
        }
        if (!this.isSetColNames()) {
            throw new TProtocolException("Required field 'colNames' is unset! Struct:" + this.toString());
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
        STRUCT_DESC = new TStruct("TableStatsRequest");
        DB_NAME_FIELD_DESC = new TField("dbName", (byte)11, (short)1);
        TBL_NAME_FIELD_DESC = new TField("tblName", (byte)11, (short)2);
        COL_NAMES_FIELD_DESC = new TField("colNames", (byte)15, (short)3);
        (schemes = new HashMap<Class<? extends IScheme>, SchemeFactory>()).put(StandardScheme.class, new TableStatsRequestStandardSchemeFactory());
        TableStatsRequest.schemes.put(TupleScheme.class, new TableStatsRequestTupleSchemeFactory());
        final Map<_Fields, FieldMetaData> tmpMap = new EnumMap<_Fields, FieldMetaData>(_Fields.class);
        tmpMap.put(_Fields.DB_NAME, new FieldMetaData("dbName", (byte)1, new FieldValueMetaData((byte)11)));
        tmpMap.put(_Fields.TBL_NAME, new FieldMetaData("tblName", (byte)1, new FieldValueMetaData((byte)11)));
        tmpMap.put(_Fields.COL_NAMES, new FieldMetaData("colNames", (byte)1, new ListMetaData((byte)15, new FieldValueMetaData((byte)11))));
        FieldMetaData.addStructMetaDataMap(TableStatsRequest.class, metaDataMap = Collections.unmodifiableMap((Map<? extends _Fields, ? extends FieldMetaData>)tmpMap));
    }
    
    public enum _Fields implements TFieldIdEnum
    {
        DB_NAME((short)1, "dbName"), 
        TBL_NAME((short)2, "tblName"), 
        COL_NAMES((short)3, "colNames");
        
        private static final Map<String, _Fields> byName;
        private final short _thriftId;
        private final String _fieldName;
        
        public static _Fields findByThriftId(final int fieldId) {
            switch (fieldId) {
                case 1: {
                    return _Fields.DB_NAME;
                }
                case 2: {
                    return _Fields.TBL_NAME;
                }
                case 3: {
                    return _Fields.COL_NAMES;
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
    
    private static class TableStatsRequestStandardSchemeFactory implements SchemeFactory
    {
        @Override
        public TableStatsRequestStandardScheme getScheme() {
            return new TableStatsRequestStandardScheme();
        }
    }
    
    private static class TableStatsRequestStandardScheme extends StandardScheme<TableStatsRequest>
    {
        @Override
        public void read(final TProtocol iprot, final TableStatsRequest struct) throws TException {
            iprot.readStructBegin();
            while (true) {
                final TField schemeField = iprot.readFieldBegin();
                if (schemeField.type == 0) {
                    break;
                }
                switch (schemeField.id) {
                    case 1: {
                        if (schemeField.type == 11) {
                            struct.dbName = iprot.readString();
                            struct.setDbNameIsSet(true);
                            break;
                        }
                        TProtocolUtil.skip(iprot, schemeField.type);
                        break;
                    }
                    case 2: {
                        if (schemeField.type == 11) {
                            struct.tblName = iprot.readString();
                            struct.setTblNameIsSet(true);
                            break;
                        }
                        TProtocolUtil.skip(iprot, schemeField.type);
                        break;
                    }
                    case 3: {
                        if (schemeField.type == 15) {
                            final TList _list356 = iprot.readListBegin();
                            struct.colNames = (List<String>)new ArrayList(_list356.size);
                            for (int _i357 = 0; _i357 < _list356.size; ++_i357) {
                                final String _elem358 = iprot.readString();
                                struct.colNames.add(_elem358);
                            }
                            iprot.readListEnd();
                            struct.setColNamesIsSet(true);
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
        public void write(final TProtocol oprot, final TableStatsRequest struct) throws TException {
            struct.validate();
            oprot.writeStructBegin(TableStatsRequest.STRUCT_DESC);
            if (struct.dbName != null) {
                oprot.writeFieldBegin(TableStatsRequest.DB_NAME_FIELD_DESC);
                oprot.writeString(struct.dbName);
                oprot.writeFieldEnd();
            }
            if (struct.tblName != null) {
                oprot.writeFieldBegin(TableStatsRequest.TBL_NAME_FIELD_DESC);
                oprot.writeString(struct.tblName);
                oprot.writeFieldEnd();
            }
            if (struct.colNames != null) {
                oprot.writeFieldBegin(TableStatsRequest.COL_NAMES_FIELD_DESC);
                oprot.writeListBegin(new TList((byte)11, struct.colNames.size()));
                for (final String _iter359 : struct.colNames) {
                    oprot.writeString(_iter359);
                }
                oprot.writeListEnd();
                oprot.writeFieldEnd();
            }
            oprot.writeFieldStop();
            oprot.writeStructEnd();
        }
    }
    
    private static class TableStatsRequestTupleSchemeFactory implements SchemeFactory
    {
        @Override
        public TableStatsRequestTupleScheme getScheme() {
            return new TableStatsRequestTupleScheme();
        }
    }
    
    private static class TableStatsRequestTupleScheme extends TupleScheme<TableStatsRequest>
    {
        @Override
        public void write(final TProtocol prot, final TableStatsRequest struct) throws TException {
            final TTupleProtocol oprot = (TTupleProtocol)prot;
            oprot.writeString(struct.dbName);
            oprot.writeString(struct.tblName);
            oprot.writeI32(struct.colNames.size());
            for (final String _iter360 : struct.colNames) {
                oprot.writeString(_iter360);
            }
        }
        
        @Override
        public void read(final TProtocol prot, final TableStatsRequest struct) throws TException {
            final TTupleProtocol iprot = (TTupleProtocol)prot;
            struct.dbName = iprot.readString();
            struct.setDbNameIsSet(true);
            struct.tblName = iprot.readString();
            struct.setTblNameIsSet(true);
            final TList _list361 = new TList((byte)11, iprot.readI32());
            struct.colNames = (List<String>)new ArrayList(_list361.size);
            for (int _i362 = 0; _i362 < _list361.size; ++_i362) {
                final String _elem363 = iprot.readString();
                struct.colNames.add(_elem363);
            }
            struct.setColNamesIsSet(true);
        }
    }
}
