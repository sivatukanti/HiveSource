// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.hive.metastore.api;

import java.util.BitSet;
import org.apache.thrift.protocol.TTupleProtocol;
import org.apache.thrift.protocol.TProtocolUtil;
import java.util.Iterator;
import java.util.EnumSet;
import java.util.Collections;
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
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.thrift.EncodingUtils;
import org.apache.thrift.TBaseHelper;
import org.apache.thrift.meta_data.FieldMetaData;
import java.nio.ByteBuffer;
import org.apache.thrift.scheme.SchemeFactory;
import org.apache.thrift.scheme.IScheme;
import java.util.Map;
import org.apache.thrift.protocol.TField;
import org.apache.thrift.protocol.TStruct;
import java.io.Serializable;
import org.apache.thrift.TBase;

public class PartitionsByExprRequest implements TBase<PartitionsByExprRequest, _Fields>, Serializable, Cloneable
{
    private static final TStruct STRUCT_DESC;
    private static final TField DB_NAME_FIELD_DESC;
    private static final TField TBL_NAME_FIELD_DESC;
    private static final TField EXPR_FIELD_DESC;
    private static final TField DEFAULT_PARTITION_NAME_FIELD_DESC;
    private static final TField MAX_PARTS_FIELD_DESC;
    private static final Map<Class<? extends IScheme>, SchemeFactory> schemes;
    private String dbName;
    private String tblName;
    private ByteBuffer expr;
    private String defaultPartitionName;
    private short maxParts;
    private static final int __MAXPARTS_ISSET_ID = 0;
    private byte __isset_bitfield;
    private _Fields[] optionals;
    public static final Map<_Fields, FieldMetaData> metaDataMap;
    
    public PartitionsByExprRequest() {
        this.__isset_bitfield = 0;
        this.optionals = new _Fields[] { _Fields.DEFAULT_PARTITION_NAME, _Fields.MAX_PARTS };
        this.maxParts = -1;
    }
    
    public PartitionsByExprRequest(final String dbName, final String tblName, final ByteBuffer expr) {
        this();
        this.dbName = dbName;
        this.tblName = tblName;
        this.expr = expr;
    }
    
    public PartitionsByExprRequest(final PartitionsByExprRequest other) {
        this.__isset_bitfield = 0;
        this.optionals = new _Fields[] { _Fields.DEFAULT_PARTITION_NAME, _Fields.MAX_PARTS };
        this.__isset_bitfield = other.__isset_bitfield;
        if (other.isSetDbName()) {
            this.dbName = other.dbName;
        }
        if (other.isSetTblName()) {
            this.tblName = other.tblName;
        }
        if (other.isSetExpr()) {
            this.expr = TBaseHelper.copyBinary(other.expr);
        }
        if (other.isSetDefaultPartitionName()) {
            this.defaultPartitionName = other.defaultPartitionName;
        }
        this.maxParts = other.maxParts;
    }
    
    @Override
    public PartitionsByExprRequest deepCopy() {
        return new PartitionsByExprRequest(this);
    }
    
    @Override
    public void clear() {
        this.dbName = null;
        this.tblName = null;
        this.expr = null;
        this.defaultPartitionName = null;
        this.maxParts = -1;
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
    
    public byte[] getExpr() {
        this.setExpr(TBaseHelper.rightSize(this.expr));
        return (byte[])((this.expr == null) ? null : this.expr.array());
    }
    
    public ByteBuffer bufferForExpr() {
        return this.expr;
    }
    
    public void setExpr(final byte[] expr) {
        this.setExpr((expr == null) ? ((ByteBuffer)null) : ByteBuffer.wrap(expr));
    }
    
    public void setExpr(final ByteBuffer expr) {
        this.expr = expr;
    }
    
    public void unsetExpr() {
        this.expr = null;
    }
    
    public boolean isSetExpr() {
        return this.expr != null;
    }
    
    public void setExprIsSet(final boolean value) {
        if (!value) {
            this.expr = null;
        }
    }
    
    public String getDefaultPartitionName() {
        return this.defaultPartitionName;
    }
    
    public void setDefaultPartitionName(final String defaultPartitionName) {
        this.defaultPartitionName = defaultPartitionName;
    }
    
    public void unsetDefaultPartitionName() {
        this.defaultPartitionName = null;
    }
    
    public boolean isSetDefaultPartitionName() {
        return this.defaultPartitionName != null;
    }
    
    public void setDefaultPartitionNameIsSet(final boolean value) {
        if (!value) {
            this.defaultPartitionName = null;
        }
    }
    
    public short getMaxParts() {
        return this.maxParts;
    }
    
    public void setMaxParts(final short maxParts) {
        this.maxParts = maxParts;
        this.setMaxPartsIsSet(true);
    }
    
    public void unsetMaxParts() {
        this.__isset_bitfield = EncodingUtils.clearBit(this.__isset_bitfield, 0);
    }
    
    public boolean isSetMaxParts() {
        return EncodingUtils.testBit(this.__isset_bitfield, 0);
    }
    
    public void setMaxPartsIsSet(final boolean value) {
        this.__isset_bitfield = EncodingUtils.setBit(this.__isset_bitfield, 0, value);
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
            case EXPR: {
                if (value == null) {
                    this.unsetExpr();
                    break;
                }
                this.setExpr((ByteBuffer)value);
                break;
            }
            case DEFAULT_PARTITION_NAME: {
                if (value == null) {
                    this.unsetDefaultPartitionName();
                    break;
                }
                this.setDefaultPartitionName((String)value);
                break;
            }
            case MAX_PARTS: {
                if (value == null) {
                    this.unsetMaxParts();
                    break;
                }
                this.setMaxParts((short)value);
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
            case EXPR: {
                return this.getExpr();
            }
            case DEFAULT_PARTITION_NAME: {
                return this.getDefaultPartitionName();
            }
            case MAX_PARTS: {
                return this.getMaxParts();
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
            case EXPR: {
                return this.isSetExpr();
            }
            case DEFAULT_PARTITION_NAME: {
                return this.isSetDefaultPartitionName();
            }
            case MAX_PARTS: {
                return this.isSetMaxParts();
            }
            default: {
                throw new IllegalStateException();
            }
        }
    }
    
    @Override
    public boolean equals(final Object that) {
        return that != null && that instanceof PartitionsByExprRequest && this.equals((PartitionsByExprRequest)that);
    }
    
    public boolean equals(final PartitionsByExprRequest that) {
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
        final boolean this_present_expr = this.isSetExpr();
        final boolean that_present_expr = that.isSetExpr();
        if (this_present_expr || that_present_expr) {
            if (!this_present_expr || !that_present_expr) {
                return false;
            }
            if (!this.expr.equals(that.expr)) {
                return false;
            }
        }
        final boolean this_present_defaultPartitionName = this.isSetDefaultPartitionName();
        final boolean that_present_defaultPartitionName = that.isSetDefaultPartitionName();
        if (this_present_defaultPartitionName || that_present_defaultPartitionName) {
            if (!this_present_defaultPartitionName || !that_present_defaultPartitionName) {
                return false;
            }
            if (!this.defaultPartitionName.equals(that.defaultPartitionName)) {
                return false;
            }
        }
        final boolean this_present_maxParts = this.isSetMaxParts();
        final boolean that_present_maxParts = that.isSetMaxParts();
        if (this_present_maxParts || that_present_maxParts) {
            if (!this_present_maxParts || !that_present_maxParts) {
                return false;
            }
            if (this.maxParts != that.maxParts) {
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
        final boolean present_expr = this.isSetExpr();
        builder.append(present_expr);
        if (present_expr) {
            builder.append(this.expr);
        }
        final boolean present_defaultPartitionName = this.isSetDefaultPartitionName();
        builder.append(present_defaultPartitionName);
        if (present_defaultPartitionName) {
            builder.append(this.defaultPartitionName);
        }
        final boolean present_maxParts = this.isSetMaxParts();
        builder.append(present_maxParts);
        if (present_maxParts) {
            builder.append(this.maxParts);
        }
        return builder.toHashCode();
    }
    
    @Override
    public int compareTo(final PartitionsByExprRequest other) {
        if (!this.getClass().equals(other.getClass())) {
            return this.getClass().getName().compareTo(other.getClass().getName());
        }
        int lastComparison = 0;
        final PartitionsByExprRequest typedOther = other;
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
        lastComparison = Boolean.valueOf(this.isSetExpr()).compareTo(Boolean.valueOf(typedOther.isSetExpr()));
        if (lastComparison != 0) {
            return lastComparison;
        }
        if (this.isSetExpr()) {
            lastComparison = TBaseHelper.compareTo(this.expr, typedOther.expr);
            if (lastComparison != 0) {
                return lastComparison;
            }
        }
        lastComparison = Boolean.valueOf(this.isSetDefaultPartitionName()).compareTo(Boolean.valueOf(typedOther.isSetDefaultPartitionName()));
        if (lastComparison != 0) {
            return lastComparison;
        }
        if (this.isSetDefaultPartitionName()) {
            lastComparison = TBaseHelper.compareTo(this.defaultPartitionName, typedOther.defaultPartitionName);
            if (lastComparison != 0) {
                return lastComparison;
            }
        }
        lastComparison = Boolean.valueOf(this.isSetMaxParts()).compareTo(Boolean.valueOf(typedOther.isSetMaxParts()));
        if (lastComparison != 0) {
            return lastComparison;
        }
        if (this.isSetMaxParts()) {
            lastComparison = TBaseHelper.compareTo(this.maxParts, typedOther.maxParts);
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
        PartitionsByExprRequest.schemes.get(iprot.getScheme()).getScheme().read(iprot, this);
    }
    
    @Override
    public void write(final TProtocol oprot) throws TException {
        PartitionsByExprRequest.schemes.get(oprot.getScheme()).getScheme().write(oprot, this);
    }
    
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("PartitionsByExprRequest(");
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
        sb.append("expr:");
        if (this.expr == null) {
            sb.append("null");
        }
        else {
            TBaseHelper.toString(this.expr, sb);
        }
        first = false;
        if (this.isSetDefaultPartitionName()) {
            if (!first) {
                sb.append(", ");
            }
            sb.append("defaultPartitionName:");
            if (this.defaultPartitionName == null) {
                sb.append("null");
            }
            else {
                sb.append(this.defaultPartitionName);
            }
            first = false;
        }
        if (this.isSetMaxParts()) {
            if (!first) {
                sb.append(", ");
            }
            sb.append("maxParts:");
            sb.append(this.maxParts);
            first = false;
        }
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
        if (!this.isSetExpr()) {
            throw new TProtocolException("Required field 'expr' is unset! Struct:" + this.toString());
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
        STRUCT_DESC = new TStruct("PartitionsByExprRequest");
        DB_NAME_FIELD_DESC = new TField("dbName", (byte)11, (short)1);
        TBL_NAME_FIELD_DESC = new TField("tblName", (byte)11, (short)2);
        EXPR_FIELD_DESC = new TField("expr", (byte)11, (short)3);
        DEFAULT_PARTITION_NAME_FIELD_DESC = new TField("defaultPartitionName", (byte)11, (short)4);
        MAX_PARTS_FIELD_DESC = new TField("maxParts", (byte)6, (short)5);
        (schemes = new HashMap<Class<? extends IScheme>, SchemeFactory>()).put(StandardScheme.class, new PartitionsByExprRequestStandardSchemeFactory());
        PartitionsByExprRequest.schemes.put(TupleScheme.class, new PartitionsByExprRequestTupleSchemeFactory());
        final Map<_Fields, FieldMetaData> tmpMap = new EnumMap<_Fields, FieldMetaData>(_Fields.class);
        tmpMap.put(_Fields.DB_NAME, new FieldMetaData("dbName", (byte)1, new FieldValueMetaData((byte)11)));
        tmpMap.put(_Fields.TBL_NAME, new FieldMetaData("tblName", (byte)1, new FieldValueMetaData((byte)11)));
        tmpMap.put(_Fields.EXPR, new FieldMetaData("expr", (byte)1, new FieldValueMetaData((byte)11, true)));
        tmpMap.put(_Fields.DEFAULT_PARTITION_NAME, new FieldMetaData("defaultPartitionName", (byte)2, new FieldValueMetaData((byte)11)));
        tmpMap.put(_Fields.MAX_PARTS, new FieldMetaData("maxParts", (byte)2, new FieldValueMetaData((byte)6)));
        FieldMetaData.addStructMetaDataMap(PartitionsByExprRequest.class, metaDataMap = Collections.unmodifiableMap((Map<? extends _Fields, ? extends FieldMetaData>)tmpMap));
    }
    
    public enum _Fields implements TFieldIdEnum
    {
        DB_NAME((short)1, "dbName"), 
        TBL_NAME((short)2, "tblName"), 
        EXPR((short)3, "expr"), 
        DEFAULT_PARTITION_NAME((short)4, "defaultPartitionName"), 
        MAX_PARTS((short)5, "maxParts");
        
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
                    return _Fields.EXPR;
                }
                case 4: {
                    return _Fields.DEFAULT_PARTITION_NAME;
                }
                case 5: {
                    return _Fields.MAX_PARTS;
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
    
    private static class PartitionsByExprRequestStandardSchemeFactory implements SchemeFactory
    {
        @Override
        public PartitionsByExprRequestStandardScheme getScheme() {
            return new PartitionsByExprRequestStandardScheme();
        }
    }
    
    private static class PartitionsByExprRequestStandardScheme extends StandardScheme<PartitionsByExprRequest>
    {
        @Override
        public void read(final TProtocol iprot, final PartitionsByExprRequest struct) throws TException {
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
                        if (schemeField.type == 11) {
                            struct.expr = iprot.readBinary();
                            struct.setExprIsSet(true);
                            break;
                        }
                        TProtocolUtil.skip(iprot, schemeField.type);
                        break;
                    }
                    case 4: {
                        if (schemeField.type == 11) {
                            struct.defaultPartitionName = iprot.readString();
                            struct.setDefaultPartitionNameIsSet(true);
                            break;
                        }
                        TProtocolUtil.skip(iprot, schemeField.type);
                        break;
                    }
                    case 5: {
                        if (schemeField.type == 6) {
                            struct.maxParts = iprot.readI16();
                            struct.setMaxPartsIsSet(true);
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
        public void write(final TProtocol oprot, final PartitionsByExprRequest struct) throws TException {
            struct.validate();
            oprot.writeStructBegin(PartitionsByExprRequest.STRUCT_DESC);
            if (struct.dbName != null) {
                oprot.writeFieldBegin(PartitionsByExprRequest.DB_NAME_FIELD_DESC);
                oprot.writeString(struct.dbName);
                oprot.writeFieldEnd();
            }
            if (struct.tblName != null) {
                oprot.writeFieldBegin(PartitionsByExprRequest.TBL_NAME_FIELD_DESC);
                oprot.writeString(struct.tblName);
                oprot.writeFieldEnd();
            }
            if (struct.expr != null) {
                oprot.writeFieldBegin(PartitionsByExprRequest.EXPR_FIELD_DESC);
                oprot.writeBinary(struct.expr);
                oprot.writeFieldEnd();
            }
            if (struct.defaultPartitionName != null && struct.isSetDefaultPartitionName()) {
                oprot.writeFieldBegin(PartitionsByExprRequest.DEFAULT_PARTITION_NAME_FIELD_DESC);
                oprot.writeString(struct.defaultPartitionName);
                oprot.writeFieldEnd();
            }
            if (struct.isSetMaxParts()) {
                oprot.writeFieldBegin(PartitionsByExprRequest.MAX_PARTS_FIELD_DESC);
                oprot.writeI16(struct.maxParts);
                oprot.writeFieldEnd();
            }
            oprot.writeFieldStop();
            oprot.writeStructEnd();
        }
    }
    
    private static class PartitionsByExprRequestTupleSchemeFactory implements SchemeFactory
    {
        @Override
        public PartitionsByExprRequestTupleScheme getScheme() {
            return new PartitionsByExprRequestTupleScheme();
        }
    }
    
    private static class PartitionsByExprRequestTupleScheme extends TupleScheme<PartitionsByExprRequest>
    {
        @Override
        public void write(final TProtocol prot, final PartitionsByExprRequest struct) throws TException {
            final TTupleProtocol oprot = (TTupleProtocol)prot;
            oprot.writeString(struct.dbName);
            oprot.writeString(struct.tblName);
            oprot.writeBinary(struct.expr);
            final BitSet optionals = new BitSet();
            if (struct.isSetDefaultPartitionName()) {
                optionals.set(0);
            }
            if (struct.isSetMaxParts()) {
                optionals.set(1);
            }
            oprot.writeBitSet(optionals, 2);
            if (struct.isSetDefaultPartitionName()) {
                oprot.writeString(struct.defaultPartitionName);
            }
            if (struct.isSetMaxParts()) {
                oprot.writeI16(struct.maxParts);
            }
        }
        
        @Override
        public void read(final TProtocol prot, final PartitionsByExprRequest struct) throws TException {
            final TTupleProtocol iprot = (TTupleProtocol)prot;
            struct.dbName = iprot.readString();
            struct.setDbNameIsSet(true);
            struct.tblName = iprot.readString();
            struct.setTblNameIsSet(true);
            struct.expr = iprot.readBinary();
            struct.setExprIsSet(true);
            final BitSet incoming = iprot.readBitSet(2);
            if (incoming.get(0)) {
                struct.defaultPartitionName = iprot.readString();
                struct.setDefaultPartitionNameIsSet(true);
            }
            if (incoming.get(1)) {
                struct.maxParts = iprot.readI16();
                struct.setMaxPartsIsSet(true);
            }
        }
    }
}
