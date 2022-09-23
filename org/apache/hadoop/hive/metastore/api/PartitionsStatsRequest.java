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

public class PartitionsStatsRequest implements TBase<PartitionsStatsRequest, _Fields>, Serializable, Cloneable
{
    private static final TStruct STRUCT_DESC;
    private static final TField DB_NAME_FIELD_DESC;
    private static final TField TBL_NAME_FIELD_DESC;
    private static final TField COL_NAMES_FIELD_DESC;
    private static final TField PART_NAMES_FIELD_DESC;
    private static final Map<Class<? extends IScheme>, SchemeFactory> schemes;
    private String dbName;
    private String tblName;
    private List<String> colNames;
    private List<String> partNames;
    public static final Map<_Fields, FieldMetaData> metaDataMap;
    
    public PartitionsStatsRequest() {
    }
    
    public PartitionsStatsRequest(final String dbName, final String tblName, final List<String> colNames, final List<String> partNames) {
        this();
        this.dbName = dbName;
        this.tblName = tblName;
        this.colNames = colNames;
        this.partNames = partNames;
    }
    
    public PartitionsStatsRequest(final PartitionsStatsRequest other) {
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
        if (other.isSetPartNames()) {
            final List<String> __this__partNames = new ArrayList<String>();
            for (final String other_element : other.partNames) {
                __this__partNames.add(other_element);
            }
            this.partNames = __this__partNames;
        }
    }
    
    @Override
    public PartitionsStatsRequest deepCopy() {
        return new PartitionsStatsRequest(this);
    }
    
    @Override
    public void clear() {
        this.dbName = null;
        this.tblName = null;
        this.colNames = null;
        this.partNames = null;
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
    
    public int getPartNamesSize() {
        return (this.partNames == null) ? 0 : this.partNames.size();
    }
    
    public Iterator<String> getPartNamesIterator() {
        return (this.partNames == null) ? null : this.partNames.iterator();
    }
    
    public void addToPartNames(final String elem) {
        if (this.partNames == null) {
            this.partNames = new ArrayList<String>();
        }
        this.partNames.add(elem);
    }
    
    public List<String> getPartNames() {
        return this.partNames;
    }
    
    public void setPartNames(final List<String> partNames) {
        this.partNames = partNames;
    }
    
    public void unsetPartNames() {
        this.partNames = null;
    }
    
    public boolean isSetPartNames() {
        return this.partNames != null;
    }
    
    public void setPartNamesIsSet(final boolean value) {
        if (!value) {
            this.partNames = null;
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
            case PART_NAMES: {
                if (value == null) {
                    this.unsetPartNames();
                    break;
                }
                this.setPartNames((List<String>)value);
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
            case PART_NAMES: {
                return this.getPartNames();
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
            case PART_NAMES: {
                return this.isSetPartNames();
            }
            default: {
                throw new IllegalStateException();
            }
        }
    }
    
    @Override
    public boolean equals(final Object that) {
        return that != null && that instanceof PartitionsStatsRequest && this.equals((PartitionsStatsRequest)that);
    }
    
    public boolean equals(final PartitionsStatsRequest that) {
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
        final boolean this_present_partNames = this.isSetPartNames();
        final boolean that_present_partNames = that.isSetPartNames();
        if (this_present_partNames || that_present_partNames) {
            if (!this_present_partNames || !that_present_partNames) {
                return false;
            }
            if (!this.partNames.equals(that.partNames)) {
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
        final boolean present_partNames = this.isSetPartNames();
        builder.append(present_partNames);
        if (present_partNames) {
            builder.append(this.partNames);
        }
        return builder.toHashCode();
    }
    
    @Override
    public int compareTo(final PartitionsStatsRequest other) {
        if (!this.getClass().equals(other.getClass())) {
            return this.getClass().getName().compareTo(other.getClass().getName());
        }
        int lastComparison = 0;
        final PartitionsStatsRequest typedOther = other;
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
        lastComparison = Boolean.valueOf(this.isSetPartNames()).compareTo(Boolean.valueOf(typedOther.isSetPartNames()));
        if (lastComparison != 0) {
            return lastComparison;
        }
        if (this.isSetPartNames()) {
            lastComparison = TBaseHelper.compareTo(this.partNames, typedOther.partNames);
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
        PartitionsStatsRequest.schemes.get(iprot.getScheme()).getScheme().read(iprot, this);
    }
    
    @Override
    public void write(final TProtocol oprot) throws TException {
        PartitionsStatsRequest.schemes.get(oprot.getScheme()).getScheme().write(oprot, this);
    }
    
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("PartitionsStatsRequest(");
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
        if (!first) {
            sb.append(", ");
        }
        sb.append("partNames:");
        if (this.partNames == null) {
            sb.append("null");
        }
        else {
            sb.append(this.partNames);
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
        if (!this.isSetPartNames()) {
            throw new TProtocolException("Required field 'partNames' is unset! Struct:" + this.toString());
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
        STRUCT_DESC = new TStruct("PartitionsStatsRequest");
        DB_NAME_FIELD_DESC = new TField("dbName", (byte)11, (short)1);
        TBL_NAME_FIELD_DESC = new TField("tblName", (byte)11, (short)2);
        COL_NAMES_FIELD_DESC = new TField("colNames", (byte)15, (short)3);
        PART_NAMES_FIELD_DESC = new TField("partNames", (byte)15, (short)4);
        (schemes = new HashMap<Class<? extends IScheme>, SchemeFactory>()).put(StandardScheme.class, new PartitionsStatsRequestStandardSchemeFactory());
        PartitionsStatsRequest.schemes.put(TupleScheme.class, new PartitionsStatsRequestTupleSchemeFactory());
        final Map<_Fields, FieldMetaData> tmpMap = new EnumMap<_Fields, FieldMetaData>(_Fields.class);
        tmpMap.put(_Fields.DB_NAME, new FieldMetaData("dbName", (byte)1, new FieldValueMetaData((byte)11)));
        tmpMap.put(_Fields.TBL_NAME, new FieldMetaData("tblName", (byte)1, new FieldValueMetaData((byte)11)));
        tmpMap.put(_Fields.COL_NAMES, new FieldMetaData("colNames", (byte)1, new ListMetaData((byte)15, new FieldValueMetaData((byte)11))));
        tmpMap.put(_Fields.PART_NAMES, new FieldMetaData("partNames", (byte)1, new ListMetaData((byte)15, new FieldValueMetaData((byte)11))));
        FieldMetaData.addStructMetaDataMap(PartitionsStatsRequest.class, metaDataMap = Collections.unmodifiableMap((Map<? extends _Fields, ? extends FieldMetaData>)tmpMap));
    }
    
    public enum _Fields implements TFieldIdEnum
    {
        DB_NAME((short)1, "dbName"), 
        TBL_NAME((short)2, "tblName"), 
        COL_NAMES((short)3, "colNames"), 
        PART_NAMES((short)4, "partNames");
        
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
                case 4: {
                    return _Fields.PART_NAMES;
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
    
    private static class PartitionsStatsRequestStandardSchemeFactory implements SchemeFactory
    {
        @Override
        public PartitionsStatsRequestStandardScheme getScheme() {
            return new PartitionsStatsRequestStandardScheme();
        }
    }
    
    private static class PartitionsStatsRequestStandardScheme extends StandardScheme<PartitionsStatsRequest>
    {
        @Override
        public void read(final TProtocol iprot, final PartitionsStatsRequest struct) throws TException {
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
                            final TList _list364 = iprot.readListBegin();
                            struct.colNames = (List<String>)new ArrayList(_list364.size);
                            for (int _i365 = 0; _i365 < _list364.size; ++_i365) {
                                final String _elem366 = iprot.readString();
                                struct.colNames.add(_elem366);
                            }
                            iprot.readListEnd();
                            struct.setColNamesIsSet(true);
                            break;
                        }
                        TProtocolUtil.skip(iprot, schemeField.type);
                        break;
                    }
                    case 4: {
                        if (schemeField.type == 15) {
                            final TList _list365 = iprot.readListBegin();
                            struct.partNames = (List<String>)new ArrayList(_list365.size);
                            for (int _i366 = 0; _i366 < _list365.size; ++_i366) {
                                final String _elem367 = iprot.readString();
                                struct.partNames.add(_elem367);
                            }
                            iprot.readListEnd();
                            struct.setPartNamesIsSet(true);
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
        public void write(final TProtocol oprot, final PartitionsStatsRequest struct) throws TException {
            struct.validate();
            oprot.writeStructBegin(PartitionsStatsRequest.STRUCT_DESC);
            if (struct.dbName != null) {
                oprot.writeFieldBegin(PartitionsStatsRequest.DB_NAME_FIELD_DESC);
                oprot.writeString(struct.dbName);
                oprot.writeFieldEnd();
            }
            if (struct.tblName != null) {
                oprot.writeFieldBegin(PartitionsStatsRequest.TBL_NAME_FIELD_DESC);
                oprot.writeString(struct.tblName);
                oprot.writeFieldEnd();
            }
            if (struct.colNames != null) {
                oprot.writeFieldBegin(PartitionsStatsRequest.COL_NAMES_FIELD_DESC);
                oprot.writeListBegin(new TList((byte)11, struct.colNames.size()));
                for (final String _iter370 : struct.colNames) {
                    oprot.writeString(_iter370);
                }
                oprot.writeListEnd();
                oprot.writeFieldEnd();
            }
            if (struct.partNames != null) {
                oprot.writeFieldBegin(PartitionsStatsRequest.PART_NAMES_FIELD_DESC);
                oprot.writeListBegin(new TList((byte)11, struct.partNames.size()));
                for (final String _iter371 : struct.partNames) {
                    oprot.writeString(_iter371);
                }
                oprot.writeListEnd();
                oprot.writeFieldEnd();
            }
            oprot.writeFieldStop();
            oprot.writeStructEnd();
        }
    }
    
    private static class PartitionsStatsRequestTupleSchemeFactory implements SchemeFactory
    {
        @Override
        public PartitionsStatsRequestTupleScheme getScheme() {
            return new PartitionsStatsRequestTupleScheme();
        }
    }
    
    private static class PartitionsStatsRequestTupleScheme extends TupleScheme<PartitionsStatsRequest>
    {
        @Override
        public void write(final TProtocol prot, final PartitionsStatsRequest struct) throws TException {
            final TTupleProtocol oprot = (TTupleProtocol)prot;
            oprot.writeString(struct.dbName);
            oprot.writeString(struct.tblName);
            oprot.writeI32(struct.colNames.size());
            for (final String _iter372 : struct.colNames) {
                oprot.writeString(_iter372);
            }
            oprot.writeI32(struct.partNames.size());
            for (final String _iter373 : struct.partNames) {
                oprot.writeString(_iter373);
            }
        }
        
        @Override
        public void read(final TProtocol prot, final PartitionsStatsRequest struct) throws TException {
            final TTupleProtocol iprot = (TTupleProtocol)prot;
            struct.dbName = iprot.readString();
            struct.setDbNameIsSet(true);
            struct.tblName = iprot.readString();
            struct.setTblNameIsSet(true);
            final TList _list374 = new TList((byte)11, iprot.readI32());
            struct.colNames = (List<String>)new ArrayList(_list374.size);
            for (int _i375 = 0; _i375 < _list374.size; ++_i375) {
                final String _elem376 = iprot.readString();
                struct.colNames.add(_elem376);
            }
            struct.setColNamesIsSet(true);
            final TList _list375 = new TList((byte)11, iprot.readI32());
            struct.partNames = (List<String>)new ArrayList(_list375.size);
            for (int _i376 = 0; _i376 < _list375.size; ++_i376) {
                final String _elem377 = iprot.readString();
                struct.partNames.add(_elem377);
            }
            struct.setPartNamesIsSet(true);
        }
    }
}
