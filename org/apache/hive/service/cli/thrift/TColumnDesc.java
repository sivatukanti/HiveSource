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
import org.apache.thrift.meta_data.StructMetaData;
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
import org.apache.thrift.EncodingUtils;
import org.apache.thrift.meta_data.FieldMetaData;
import org.apache.thrift.scheme.SchemeFactory;
import org.apache.thrift.scheme.IScheme;
import java.util.Map;
import org.apache.thrift.protocol.TField;
import org.apache.thrift.protocol.TStruct;
import java.io.Serializable;
import org.apache.thrift.TBase;

public class TColumnDesc implements TBase<TColumnDesc, _Fields>, Serializable, Cloneable
{
    private static final TStruct STRUCT_DESC;
    private static final TField COLUMN_NAME_FIELD_DESC;
    private static final TField TYPE_DESC_FIELD_DESC;
    private static final TField POSITION_FIELD_DESC;
    private static final TField COMMENT_FIELD_DESC;
    private static final Map<Class<? extends IScheme>, SchemeFactory> schemes;
    private String columnName;
    private TTypeDesc typeDesc;
    private int position;
    private String comment;
    private static final int __POSITION_ISSET_ID = 0;
    private byte __isset_bitfield;
    private _Fields[] optionals;
    public static final Map<_Fields, FieldMetaData> metaDataMap;
    
    public TColumnDesc() {
        this.__isset_bitfield = 0;
        this.optionals = new _Fields[] { _Fields.COMMENT };
    }
    
    public TColumnDesc(final String columnName, final TTypeDesc typeDesc, final int position) {
        this();
        this.columnName = columnName;
        this.typeDesc = typeDesc;
        this.position = position;
        this.setPositionIsSet(true);
    }
    
    public TColumnDesc(final TColumnDesc other) {
        this.__isset_bitfield = 0;
        this.optionals = new _Fields[] { _Fields.COMMENT };
        this.__isset_bitfield = other.__isset_bitfield;
        if (other.isSetColumnName()) {
            this.columnName = other.columnName;
        }
        if (other.isSetTypeDesc()) {
            this.typeDesc = new TTypeDesc(other.typeDesc);
        }
        this.position = other.position;
        if (other.isSetComment()) {
            this.comment = other.comment;
        }
    }
    
    @Override
    public TColumnDesc deepCopy() {
        return new TColumnDesc(this);
    }
    
    @Override
    public void clear() {
        this.columnName = null;
        this.typeDesc = null;
        this.setPositionIsSet(false);
        this.position = 0;
        this.comment = null;
    }
    
    public String getColumnName() {
        return this.columnName;
    }
    
    public void setColumnName(final String columnName) {
        this.columnName = columnName;
    }
    
    public void unsetColumnName() {
        this.columnName = null;
    }
    
    public boolean isSetColumnName() {
        return this.columnName != null;
    }
    
    public void setColumnNameIsSet(final boolean value) {
        if (!value) {
            this.columnName = null;
        }
    }
    
    public TTypeDesc getTypeDesc() {
        return this.typeDesc;
    }
    
    public void setTypeDesc(final TTypeDesc typeDesc) {
        this.typeDesc = typeDesc;
    }
    
    public void unsetTypeDesc() {
        this.typeDesc = null;
    }
    
    public boolean isSetTypeDesc() {
        return this.typeDesc != null;
    }
    
    public void setTypeDescIsSet(final boolean value) {
        if (!value) {
            this.typeDesc = null;
        }
    }
    
    public int getPosition() {
        return this.position;
    }
    
    public void setPosition(final int position) {
        this.position = position;
        this.setPositionIsSet(true);
    }
    
    public void unsetPosition() {
        this.__isset_bitfield = EncodingUtils.clearBit(this.__isset_bitfield, 0);
    }
    
    public boolean isSetPosition() {
        return EncodingUtils.testBit(this.__isset_bitfield, 0);
    }
    
    public void setPositionIsSet(final boolean value) {
        this.__isset_bitfield = EncodingUtils.setBit(this.__isset_bitfield, 0, value);
    }
    
    public String getComment() {
        return this.comment;
    }
    
    public void setComment(final String comment) {
        this.comment = comment;
    }
    
    public void unsetComment() {
        this.comment = null;
    }
    
    public boolean isSetComment() {
        return this.comment != null;
    }
    
    public void setCommentIsSet(final boolean value) {
        if (!value) {
            this.comment = null;
        }
    }
    
    @Override
    public void setFieldValue(final _Fields field, final Object value) {
        switch (field) {
            case COLUMN_NAME: {
                if (value == null) {
                    this.unsetColumnName();
                    break;
                }
                this.setColumnName((String)value);
                break;
            }
            case TYPE_DESC: {
                if (value == null) {
                    this.unsetTypeDesc();
                    break;
                }
                this.setTypeDesc((TTypeDesc)value);
                break;
            }
            case POSITION: {
                if (value == null) {
                    this.unsetPosition();
                    break;
                }
                this.setPosition((int)value);
                break;
            }
            case COMMENT: {
                if (value == null) {
                    this.unsetComment();
                    break;
                }
                this.setComment((String)value);
                break;
            }
        }
    }
    
    @Override
    public Object getFieldValue(final _Fields field) {
        switch (field) {
            case COLUMN_NAME: {
                return this.getColumnName();
            }
            case TYPE_DESC: {
                return this.getTypeDesc();
            }
            case POSITION: {
                return this.getPosition();
            }
            case COMMENT: {
                return this.getComment();
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
            case COLUMN_NAME: {
                return this.isSetColumnName();
            }
            case TYPE_DESC: {
                return this.isSetTypeDesc();
            }
            case POSITION: {
                return this.isSetPosition();
            }
            case COMMENT: {
                return this.isSetComment();
            }
            default: {
                throw new IllegalStateException();
            }
        }
    }
    
    @Override
    public boolean equals(final Object that) {
        return that != null && that instanceof TColumnDesc && this.equals((TColumnDesc)that);
    }
    
    public boolean equals(final TColumnDesc that) {
        if (that == null) {
            return false;
        }
        final boolean this_present_columnName = this.isSetColumnName();
        final boolean that_present_columnName = that.isSetColumnName();
        if (this_present_columnName || that_present_columnName) {
            if (!this_present_columnName || !that_present_columnName) {
                return false;
            }
            if (!this.columnName.equals(that.columnName)) {
                return false;
            }
        }
        final boolean this_present_typeDesc = this.isSetTypeDesc();
        final boolean that_present_typeDesc = that.isSetTypeDesc();
        if (this_present_typeDesc || that_present_typeDesc) {
            if (!this_present_typeDesc || !that_present_typeDesc) {
                return false;
            }
            if (!this.typeDesc.equals(that.typeDesc)) {
                return false;
            }
        }
        final boolean this_present_position = true;
        final boolean that_present_position = true;
        if (this_present_position || that_present_position) {
            if (!this_present_position || !that_present_position) {
                return false;
            }
            if (this.position != that.position) {
                return false;
            }
        }
        final boolean this_present_comment = this.isSetComment();
        final boolean that_present_comment = that.isSetComment();
        if (this_present_comment || that_present_comment) {
            if (!this_present_comment || !that_present_comment) {
                return false;
            }
            if (!this.comment.equals(that.comment)) {
                return false;
            }
        }
        return true;
    }
    
    @Override
    public int hashCode() {
        final HashCodeBuilder builder = new HashCodeBuilder();
        final boolean present_columnName = this.isSetColumnName();
        builder.append(present_columnName);
        if (present_columnName) {
            builder.append(this.columnName);
        }
        final boolean present_typeDesc = this.isSetTypeDesc();
        builder.append(present_typeDesc);
        if (present_typeDesc) {
            builder.append(this.typeDesc);
        }
        final boolean present_position = true;
        builder.append(present_position);
        if (present_position) {
            builder.append(this.position);
        }
        final boolean present_comment = this.isSetComment();
        builder.append(present_comment);
        if (present_comment) {
            builder.append(this.comment);
        }
        return builder.toHashCode();
    }
    
    @Override
    public int compareTo(final TColumnDesc other) {
        if (!this.getClass().equals(other.getClass())) {
            return this.getClass().getName().compareTo(other.getClass().getName());
        }
        int lastComparison = 0;
        final TColumnDesc typedOther = other;
        lastComparison = Boolean.valueOf(this.isSetColumnName()).compareTo(Boolean.valueOf(typedOther.isSetColumnName()));
        if (lastComparison != 0) {
            return lastComparison;
        }
        if (this.isSetColumnName()) {
            lastComparison = TBaseHelper.compareTo(this.columnName, typedOther.columnName);
            if (lastComparison != 0) {
                return lastComparison;
            }
        }
        lastComparison = Boolean.valueOf(this.isSetTypeDesc()).compareTo(Boolean.valueOf(typedOther.isSetTypeDesc()));
        if (lastComparison != 0) {
            return lastComparison;
        }
        if (this.isSetTypeDesc()) {
            lastComparison = TBaseHelper.compareTo(this.typeDesc, typedOther.typeDesc);
            if (lastComparison != 0) {
                return lastComparison;
            }
        }
        lastComparison = Boolean.valueOf(this.isSetPosition()).compareTo(Boolean.valueOf(typedOther.isSetPosition()));
        if (lastComparison != 0) {
            return lastComparison;
        }
        if (this.isSetPosition()) {
            lastComparison = TBaseHelper.compareTo(this.position, typedOther.position);
            if (lastComparison != 0) {
                return lastComparison;
            }
        }
        lastComparison = Boolean.valueOf(this.isSetComment()).compareTo(Boolean.valueOf(typedOther.isSetComment()));
        if (lastComparison != 0) {
            return lastComparison;
        }
        if (this.isSetComment()) {
            lastComparison = TBaseHelper.compareTo(this.comment, typedOther.comment);
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
        TColumnDesc.schemes.get(iprot.getScheme()).getScheme().read(iprot, this);
    }
    
    @Override
    public void write(final TProtocol oprot) throws TException {
        TColumnDesc.schemes.get(oprot.getScheme()).getScheme().write(oprot, this);
    }
    
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("TColumnDesc(");
        boolean first = true;
        sb.append("columnName:");
        if (this.columnName == null) {
            sb.append("null");
        }
        else {
            sb.append(this.columnName);
        }
        first = false;
        if (!first) {
            sb.append(", ");
        }
        sb.append("typeDesc:");
        if (this.typeDesc == null) {
            sb.append("null");
        }
        else {
            sb.append(this.typeDesc);
        }
        first = false;
        if (!first) {
            sb.append(", ");
        }
        sb.append("position:");
        sb.append(this.position);
        first = false;
        if (this.isSetComment()) {
            if (!first) {
                sb.append(", ");
            }
            sb.append("comment:");
            if (this.comment == null) {
                sb.append("null");
            }
            else {
                sb.append(this.comment);
            }
            first = false;
        }
        sb.append(")");
        return sb.toString();
    }
    
    public void validate() throws TException {
        if (!this.isSetColumnName()) {
            throw new TProtocolException("Required field 'columnName' is unset! Struct:" + this.toString());
        }
        if (!this.isSetTypeDesc()) {
            throw new TProtocolException("Required field 'typeDesc' is unset! Struct:" + this.toString());
        }
        if (!this.isSetPosition()) {
            throw new TProtocolException("Required field 'position' is unset! Struct:" + this.toString());
        }
        if (this.typeDesc != null) {
            this.typeDesc.validate();
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
        STRUCT_DESC = new TStruct("TColumnDesc");
        COLUMN_NAME_FIELD_DESC = new TField("columnName", (byte)11, (short)1);
        TYPE_DESC_FIELD_DESC = new TField("typeDesc", (byte)12, (short)2);
        POSITION_FIELD_DESC = new TField("position", (byte)8, (short)3);
        COMMENT_FIELD_DESC = new TField("comment", (byte)11, (short)4);
        (schemes = new HashMap<Class<? extends IScheme>, SchemeFactory>()).put(StandardScheme.class, new TColumnDescStandardSchemeFactory());
        TColumnDesc.schemes.put(TupleScheme.class, new TColumnDescTupleSchemeFactory());
        final Map<_Fields, FieldMetaData> tmpMap = new EnumMap<_Fields, FieldMetaData>(_Fields.class);
        tmpMap.put(_Fields.COLUMN_NAME, new FieldMetaData("columnName", (byte)1, new FieldValueMetaData((byte)11)));
        tmpMap.put(_Fields.TYPE_DESC, new FieldMetaData("typeDesc", (byte)1, new StructMetaData((byte)12, TTypeDesc.class)));
        tmpMap.put(_Fields.POSITION, new FieldMetaData("position", (byte)1, new FieldValueMetaData((byte)8)));
        tmpMap.put(_Fields.COMMENT, new FieldMetaData("comment", (byte)2, new FieldValueMetaData((byte)11)));
        FieldMetaData.addStructMetaDataMap(TColumnDesc.class, metaDataMap = Collections.unmodifiableMap((Map<? extends _Fields, ? extends FieldMetaData>)tmpMap));
    }
    
    public enum _Fields implements TFieldIdEnum
    {
        COLUMN_NAME((short)1, "columnName"), 
        TYPE_DESC((short)2, "typeDesc"), 
        POSITION((short)3, "position"), 
        COMMENT((short)4, "comment");
        
        private static final Map<String, _Fields> byName;
        private final short _thriftId;
        private final String _fieldName;
        
        public static _Fields findByThriftId(final int fieldId) {
            switch (fieldId) {
                case 1: {
                    return _Fields.COLUMN_NAME;
                }
                case 2: {
                    return _Fields.TYPE_DESC;
                }
                case 3: {
                    return _Fields.POSITION;
                }
                case 4: {
                    return _Fields.COMMENT;
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
    
    private static class TColumnDescStandardSchemeFactory implements SchemeFactory
    {
        @Override
        public TColumnDescStandardScheme getScheme() {
            return new TColumnDescStandardScheme();
        }
    }
    
    private static class TColumnDescStandardScheme extends StandardScheme<TColumnDesc>
    {
        @Override
        public void read(final TProtocol iprot, final TColumnDesc struct) throws TException {
            iprot.readStructBegin();
            while (true) {
                final TField schemeField = iprot.readFieldBegin();
                if (schemeField.type == 0) {
                    break;
                }
                switch (schemeField.id) {
                    case 1: {
                        if (schemeField.type == 11) {
                            struct.columnName = iprot.readString();
                            struct.setColumnNameIsSet(true);
                            break;
                        }
                        TProtocolUtil.skip(iprot, schemeField.type);
                        break;
                    }
                    case 2: {
                        if (schemeField.type == 12) {
                            struct.typeDesc = new TTypeDesc();
                            struct.typeDesc.read(iprot);
                            struct.setTypeDescIsSet(true);
                            break;
                        }
                        TProtocolUtil.skip(iprot, schemeField.type);
                        break;
                    }
                    case 3: {
                        if (schemeField.type == 8) {
                            struct.position = iprot.readI32();
                            struct.setPositionIsSet(true);
                            break;
                        }
                        TProtocolUtil.skip(iprot, schemeField.type);
                        break;
                    }
                    case 4: {
                        if (schemeField.type == 11) {
                            struct.comment = iprot.readString();
                            struct.setCommentIsSet(true);
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
        public void write(final TProtocol oprot, final TColumnDesc struct) throws TException {
            struct.validate();
            oprot.writeStructBegin(TColumnDesc.STRUCT_DESC);
            if (struct.columnName != null) {
                oprot.writeFieldBegin(TColumnDesc.COLUMN_NAME_FIELD_DESC);
                oprot.writeString(struct.columnName);
                oprot.writeFieldEnd();
            }
            if (struct.typeDesc != null) {
                oprot.writeFieldBegin(TColumnDesc.TYPE_DESC_FIELD_DESC);
                struct.typeDesc.write(oprot);
                oprot.writeFieldEnd();
            }
            oprot.writeFieldBegin(TColumnDesc.POSITION_FIELD_DESC);
            oprot.writeI32(struct.position);
            oprot.writeFieldEnd();
            if (struct.comment != null && struct.isSetComment()) {
                oprot.writeFieldBegin(TColumnDesc.COMMENT_FIELD_DESC);
                oprot.writeString(struct.comment);
                oprot.writeFieldEnd();
            }
            oprot.writeFieldStop();
            oprot.writeStructEnd();
        }
    }
    
    private static class TColumnDescTupleSchemeFactory implements SchemeFactory
    {
        @Override
        public TColumnDescTupleScheme getScheme() {
            return new TColumnDescTupleScheme();
        }
    }
    
    private static class TColumnDescTupleScheme extends TupleScheme<TColumnDesc>
    {
        @Override
        public void write(final TProtocol prot, final TColumnDesc struct) throws TException {
            final TTupleProtocol oprot = (TTupleProtocol)prot;
            oprot.writeString(struct.columnName);
            struct.typeDesc.write(oprot);
            oprot.writeI32(struct.position);
            final BitSet optionals = new BitSet();
            if (struct.isSetComment()) {
                optionals.set(0);
            }
            oprot.writeBitSet(optionals, 1);
            if (struct.isSetComment()) {
                oprot.writeString(struct.comment);
            }
        }
        
        @Override
        public void read(final TProtocol prot, final TColumnDesc struct) throws TException {
            final TTupleProtocol iprot = (TTupleProtocol)prot;
            struct.columnName = iprot.readString();
            struct.setColumnNameIsSet(true);
            struct.typeDesc = new TTypeDesc();
            struct.typeDesc.read(iprot);
            struct.setTypeDescIsSet(true);
            struct.position = iprot.readI32();
            struct.setPositionIsSet(true);
            final BitSet incoming = iprot.readBitSet(1);
            if (incoming.get(0)) {
                struct.comment = iprot.readString();
                struct.setCommentIsSet(true);
            }
        }
    }
}
