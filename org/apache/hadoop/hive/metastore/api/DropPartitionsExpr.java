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

public class DropPartitionsExpr implements TBase<DropPartitionsExpr, _Fields>, Serializable, Cloneable
{
    private static final TStruct STRUCT_DESC;
    private static final TField EXPR_FIELD_DESC;
    private static final TField PART_ARCHIVE_LEVEL_FIELD_DESC;
    private static final Map<Class<? extends IScheme>, SchemeFactory> schemes;
    private ByteBuffer expr;
    private int partArchiveLevel;
    private static final int __PARTARCHIVELEVEL_ISSET_ID = 0;
    private byte __isset_bitfield;
    private _Fields[] optionals;
    public static final Map<_Fields, FieldMetaData> metaDataMap;
    
    public DropPartitionsExpr() {
        this.__isset_bitfield = 0;
        this.optionals = new _Fields[] { _Fields.PART_ARCHIVE_LEVEL };
    }
    
    public DropPartitionsExpr(final ByteBuffer expr) {
        this();
        this.expr = expr;
    }
    
    public DropPartitionsExpr(final DropPartitionsExpr other) {
        this.__isset_bitfield = 0;
        this.optionals = new _Fields[] { _Fields.PART_ARCHIVE_LEVEL };
        this.__isset_bitfield = other.__isset_bitfield;
        if (other.isSetExpr()) {
            this.expr = TBaseHelper.copyBinary(other.expr);
        }
        this.partArchiveLevel = other.partArchiveLevel;
    }
    
    @Override
    public DropPartitionsExpr deepCopy() {
        return new DropPartitionsExpr(this);
    }
    
    @Override
    public void clear() {
        this.expr = null;
        this.setPartArchiveLevelIsSet(false);
        this.partArchiveLevel = 0;
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
    
    public int getPartArchiveLevel() {
        return this.partArchiveLevel;
    }
    
    public void setPartArchiveLevel(final int partArchiveLevel) {
        this.partArchiveLevel = partArchiveLevel;
        this.setPartArchiveLevelIsSet(true);
    }
    
    public void unsetPartArchiveLevel() {
        this.__isset_bitfield = EncodingUtils.clearBit(this.__isset_bitfield, 0);
    }
    
    public boolean isSetPartArchiveLevel() {
        return EncodingUtils.testBit(this.__isset_bitfield, 0);
    }
    
    public void setPartArchiveLevelIsSet(final boolean value) {
        this.__isset_bitfield = EncodingUtils.setBit(this.__isset_bitfield, 0, value);
    }
    
    @Override
    public void setFieldValue(final _Fields field, final Object value) {
        switch (field) {
            case EXPR: {
                if (value == null) {
                    this.unsetExpr();
                    break;
                }
                this.setExpr((ByteBuffer)value);
                break;
            }
            case PART_ARCHIVE_LEVEL: {
                if (value == null) {
                    this.unsetPartArchiveLevel();
                    break;
                }
                this.setPartArchiveLevel((int)value);
                break;
            }
        }
    }
    
    @Override
    public Object getFieldValue(final _Fields field) {
        switch (field) {
            case EXPR: {
                return this.getExpr();
            }
            case PART_ARCHIVE_LEVEL: {
                return this.getPartArchiveLevel();
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
            case EXPR: {
                return this.isSetExpr();
            }
            case PART_ARCHIVE_LEVEL: {
                return this.isSetPartArchiveLevel();
            }
            default: {
                throw new IllegalStateException();
            }
        }
    }
    
    @Override
    public boolean equals(final Object that) {
        return that != null && that instanceof DropPartitionsExpr && this.equals((DropPartitionsExpr)that);
    }
    
    public boolean equals(final DropPartitionsExpr that) {
        if (that == null) {
            return false;
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
        final boolean this_present_partArchiveLevel = this.isSetPartArchiveLevel();
        final boolean that_present_partArchiveLevel = that.isSetPartArchiveLevel();
        if (this_present_partArchiveLevel || that_present_partArchiveLevel) {
            if (!this_present_partArchiveLevel || !that_present_partArchiveLevel) {
                return false;
            }
            if (this.partArchiveLevel != that.partArchiveLevel) {
                return false;
            }
        }
        return true;
    }
    
    @Override
    public int hashCode() {
        final HashCodeBuilder builder = new HashCodeBuilder();
        final boolean present_expr = this.isSetExpr();
        builder.append(present_expr);
        if (present_expr) {
            builder.append(this.expr);
        }
        final boolean present_partArchiveLevel = this.isSetPartArchiveLevel();
        builder.append(present_partArchiveLevel);
        if (present_partArchiveLevel) {
            builder.append(this.partArchiveLevel);
        }
        return builder.toHashCode();
    }
    
    @Override
    public int compareTo(final DropPartitionsExpr other) {
        if (!this.getClass().equals(other.getClass())) {
            return this.getClass().getName().compareTo(other.getClass().getName());
        }
        int lastComparison = 0;
        final DropPartitionsExpr typedOther = other;
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
        lastComparison = Boolean.valueOf(this.isSetPartArchiveLevel()).compareTo(Boolean.valueOf(typedOther.isSetPartArchiveLevel()));
        if (lastComparison != 0) {
            return lastComparison;
        }
        if (this.isSetPartArchiveLevel()) {
            lastComparison = TBaseHelper.compareTo(this.partArchiveLevel, typedOther.partArchiveLevel);
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
        DropPartitionsExpr.schemes.get(iprot.getScheme()).getScheme().read(iprot, this);
    }
    
    @Override
    public void write(final TProtocol oprot) throws TException {
        DropPartitionsExpr.schemes.get(oprot.getScheme()).getScheme().write(oprot, this);
    }
    
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("DropPartitionsExpr(");
        boolean first = true;
        sb.append("expr:");
        if (this.expr == null) {
            sb.append("null");
        }
        else {
            TBaseHelper.toString(this.expr, sb);
        }
        first = false;
        if (this.isSetPartArchiveLevel()) {
            if (!first) {
                sb.append(", ");
            }
            sb.append("partArchiveLevel:");
            sb.append(this.partArchiveLevel);
            first = false;
        }
        sb.append(")");
        return sb.toString();
    }
    
    public void validate() throws TException {
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
        STRUCT_DESC = new TStruct("DropPartitionsExpr");
        EXPR_FIELD_DESC = new TField("expr", (byte)11, (short)1);
        PART_ARCHIVE_LEVEL_FIELD_DESC = new TField("partArchiveLevel", (byte)8, (short)2);
        (schemes = new HashMap<Class<? extends IScheme>, SchemeFactory>()).put(StandardScheme.class, new DropPartitionsExprStandardSchemeFactory());
        DropPartitionsExpr.schemes.put(TupleScheme.class, new DropPartitionsExprTupleSchemeFactory());
        final Map<_Fields, FieldMetaData> tmpMap = new EnumMap<_Fields, FieldMetaData>(_Fields.class);
        tmpMap.put(_Fields.EXPR, new FieldMetaData("expr", (byte)1, new FieldValueMetaData((byte)11, true)));
        tmpMap.put(_Fields.PART_ARCHIVE_LEVEL, new FieldMetaData("partArchiveLevel", (byte)2, new FieldValueMetaData((byte)8)));
        FieldMetaData.addStructMetaDataMap(DropPartitionsExpr.class, metaDataMap = Collections.unmodifiableMap((Map<? extends _Fields, ? extends FieldMetaData>)tmpMap));
    }
    
    public enum _Fields implements TFieldIdEnum
    {
        EXPR((short)1, "expr"), 
        PART_ARCHIVE_LEVEL((short)2, "partArchiveLevel");
        
        private static final Map<String, _Fields> byName;
        private final short _thriftId;
        private final String _fieldName;
        
        public static _Fields findByThriftId(final int fieldId) {
            switch (fieldId) {
                case 1: {
                    return _Fields.EXPR;
                }
                case 2: {
                    return _Fields.PART_ARCHIVE_LEVEL;
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
    
    private static class DropPartitionsExprStandardSchemeFactory implements SchemeFactory
    {
        @Override
        public DropPartitionsExprStandardScheme getScheme() {
            return new DropPartitionsExprStandardScheme();
        }
    }
    
    private static class DropPartitionsExprStandardScheme extends StandardScheme<DropPartitionsExpr>
    {
        @Override
        public void read(final TProtocol iprot, final DropPartitionsExpr struct) throws TException {
            iprot.readStructBegin();
            while (true) {
                final TField schemeField = iprot.readFieldBegin();
                if (schemeField.type == 0) {
                    break;
                }
                switch (schemeField.id) {
                    case 1: {
                        if (schemeField.type == 11) {
                            struct.expr = iprot.readBinary();
                            struct.setExprIsSet(true);
                            break;
                        }
                        TProtocolUtil.skip(iprot, schemeField.type);
                        break;
                    }
                    case 2: {
                        if (schemeField.type == 8) {
                            struct.partArchiveLevel = iprot.readI32();
                            struct.setPartArchiveLevelIsSet(true);
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
        public void write(final TProtocol oprot, final DropPartitionsExpr struct) throws TException {
            struct.validate();
            oprot.writeStructBegin(DropPartitionsExpr.STRUCT_DESC);
            if (struct.expr != null) {
                oprot.writeFieldBegin(DropPartitionsExpr.EXPR_FIELD_DESC);
                oprot.writeBinary(struct.expr);
                oprot.writeFieldEnd();
            }
            if (struct.isSetPartArchiveLevel()) {
                oprot.writeFieldBegin(DropPartitionsExpr.PART_ARCHIVE_LEVEL_FIELD_DESC);
                oprot.writeI32(struct.partArchiveLevel);
                oprot.writeFieldEnd();
            }
            oprot.writeFieldStop();
            oprot.writeStructEnd();
        }
    }
    
    private static class DropPartitionsExprTupleSchemeFactory implements SchemeFactory
    {
        @Override
        public DropPartitionsExprTupleScheme getScheme() {
            return new DropPartitionsExprTupleScheme();
        }
    }
    
    private static class DropPartitionsExprTupleScheme extends TupleScheme<DropPartitionsExpr>
    {
        @Override
        public void write(final TProtocol prot, final DropPartitionsExpr struct) throws TException {
            final TTupleProtocol oprot = (TTupleProtocol)prot;
            oprot.writeBinary(struct.expr);
            final BitSet optionals = new BitSet();
            if (struct.isSetPartArchiveLevel()) {
                optionals.set(0);
            }
            oprot.writeBitSet(optionals, 1);
            if (struct.isSetPartArchiveLevel()) {
                oprot.writeI32(struct.partArchiveLevel);
            }
        }
        
        @Override
        public void read(final TProtocol prot, final DropPartitionsExpr struct) throws TException {
            final TTupleProtocol iprot = (TTupleProtocol)prot;
            struct.expr = iprot.readBinary();
            struct.setExprIsSet(true);
            final BitSet incoming = iprot.readBitSet(1);
            if (incoming.get(0)) {
                struct.partArchiveLevel = iprot.readI32();
                struct.setPartArchiveLevelIsSet(true);
            }
        }
    }
}
