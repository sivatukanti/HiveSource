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

public class FieldSchema implements TBase<FieldSchema, _Fields>, Serializable, Cloneable
{
    private static final TStruct STRUCT_DESC;
    private static final TField NAME_FIELD_DESC;
    private static final TField TYPE_FIELD_DESC;
    private static final TField COMMENT_FIELD_DESC;
    private static final Map<Class<? extends IScheme>, SchemeFactory> schemes;
    private String name;
    private String type;
    private String comment;
    public static final Map<_Fields, FieldMetaData> metaDataMap;
    
    public FieldSchema() {
    }
    
    public FieldSchema(final String name, final String type, final String comment) {
        this();
        this.name = name;
        this.type = type;
        this.comment = comment;
    }
    
    public FieldSchema(final FieldSchema other) {
        if (other.isSetName()) {
            this.name = other.name;
        }
        if (other.isSetType()) {
            this.type = other.type;
        }
        if (other.isSetComment()) {
            this.comment = other.comment;
        }
    }
    
    @Override
    public FieldSchema deepCopy() {
        return new FieldSchema(this);
    }
    
    @Override
    public void clear() {
        this.name = null;
        this.type = null;
        this.comment = null;
    }
    
    public String getName() {
        return this.name;
    }
    
    public void setName(final String name) {
        this.name = name;
    }
    
    public void unsetName() {
        this.name = null;
    }
    
    public boolean isSetName() {
        return this.name != null;
    }
    
    public void setNameIsSet(final boolean value) {
        if (!value) {
            this.name = null;
        }
    }
    
    public String getType() {
        return this.type;
    }
    
    public void setType(final String type) {
        this.type = type;
    }
    
    public void unsetType() {
        this.type = null;
    }
    
    public boolean isSetType() {
        return this.type != null;
    }
    
    public void setTypeIsSet(final boolean value) {
        if (!value) {
            this.type = null;
        }
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
            case NAME: {
                if (value == null) {
                    this.unsetName();
                    break;
                }
                this.setName((String)value);
                break;
            }
            case TYPE: {
                if (value == null) {
                    this.unsetType();
                    break;
                }
                this.setType((String)value);
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
            case NAME: {
                return this.getName();
            }
            case TYPE: {
                return this.getType();
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
            case NAME: {
                return this.isSetName();
            }
            case TYPE: {
                return this.isSetType();
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
        return that != null && that instanceof FieldSchema && this.equals((FieldSchema)that);
    }
    
    public boolean equals(final FieldSchema that) {
        if (that == null) {
            return false;
        }
        final boolean this_present_name = this.isSetName();
        final boolean that_present_name = that.isSetName();
        if (this_present_name || that_present_name) {
            if (!this_present_name || !that_present_name) {
                return false;
            }
            if (!this.name.equals(that.name)) {
                return false;
            }
        }
        final boolean this_present_type = this.isSetType();
        final boolean that_present_type = that.isSetType();
        if (this_present_type || that_present_type) {
            if (!this_present_type || !that_present_type) {
                return false;
            }
            if (!this.type.equals(that.type)) {
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
        final boolean present_name = this.isSetName();
        builder.append(present_name);
        if (present_name) {
            builder.append(this.name);
        }
        final boolean present_type = this.isSetType();
        builder.append(present_type);
        if (present_type) {
            builder.append(this.type);
        }
        final boolean present_comment = this.isSetComment();
        builder.append(present_comment);
        if (present_comment) {
            builder.append(this.comment);
        }
        return builder.toHashCode();
    }
    
    @Override
    public int compareTo(final FieldSchema other) {
        if (!this.getClass().equals(other.getClass())) {
            return this.getClass().getName().compareTo(other.getClass().getName());
        }
        int lastComparison = 0;
        final FieldSchema typedOther = other;
        lastComparison = Boolean.valueOf(this.isSetName()).compareTo(Boolean.valueOf(typedOther.isSetName()));
        if (lastComparison != 0) {
            return lastComparison;
        }
        if (this.isSetName()) {
            lastComparison = TBaseHelper.compareTo(this.name, typedOther.name);
            if (lastComparison != 0) {
                return lastComparison;
            }
        }
        lastComparison = Boolean.valueOf(this.isSetType()).compareTo(Boolean.valueOf(typedOther.isSetType()));
        if (lastComparison != 0) {
            return lastComparison;
        }
        if (this.isSetType()) {
            lastComparison = TBaseHelper.compareTo(this.type, typedOther.type);
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
        FieldSchema.schemes.get(iprot.getScheme()).getScheme().read(iprot, this);
    }
    
    @Override
    public void write(final TProtocol oprot) throws TException {
        FieldSchema.schemes.get(oprot.getScheme()).getScheme().write(oprot, this);
    }
    
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("FieldSchema(");
        boolean first = true;
        sb.append("name:");
        if (this.name == null) {
            sb.append("null");
        }
        else {
            sb.append(this.name);
        }
        first = false;
        if (!first) {
            sb.append(", ");
        }
        sb.append("type:");
        if (this.type == null) {
            sb.append("null");
        }
        else {
            sb.append(this.type);
        }
        first = false;
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
        sb.append(")");
        return sb.toString();
    }
    
    public void validate() throws TException {
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
        STRUCT_DESC = new TStruct("FieldSchema");
        NAME_FIELD_DESC = new TField("name", (byte)11, (short)1);
        TYPE_FIELD_DESC = new TField("type", (byte)11, (short)2);
        COMMENT_FIELD_DESC = new TField("comment", (byte)11, (short)3);
        (schemes = new HashMap<Class<? extends IScheme>, SchemeFactory>()).put(StandardScheme.class, new FieldSchemaStandardSchemeFactory());
        FieldSchema.schemes.put(TupleScheme.class, new FieldSchemaTupleSchemeFactory());
        final Map<_Fields, FieldMetaData> tmpMap = new EnumMap<_Fields, FieldMetaData>(_Fields.class);
        tmpMap.put(_Fields.NAME, new FieldMetaData("name", (byte)3, new FieldValueMetaData((byte)11)));
        tmpMap.put(_Fields.TYPE, new FieldMetaData("type", (byte)3, new FieldValueMetaData((byte)11)));
        tmpMap.put(_Fields.COMMENT, new FieldMetaData("comment", (byte)3, new FieldValueMetaData((byte)11)));
        FieldMetaData.addStructMetaDataMap(FieldSchema.class, metaDataMap = Collections.unmodifiableMap((Map<? extends _Fields, ? extends FieldMetaData>)tmpMap));
    }
    
    public enum _Fields implements TFieldIdEnum
    {
        NAME((short)1, "name"), 
        TYPE((short)2, "type"), 
        COMMENT((short)3, "comment");
        
        private static final Map<String, _Fields> byName;
        private final short _thriftId;
        private final String _fieldName;
        
        public static _Fields findByThriftId(final int fieldId) {
            switch (fieldId) {
                case 1: {
                    return _Fields.NAME;
                }
                case 2: {
                    return _Fields.TYPE;
                }
                case 3: {
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
    
    private static class FieldSchemaStandardSchemeFactory implements SchemeFactory
    {
        @Override
        public FieldSchemaStandardScheme getScheme() {
            return new FieldSchemaStandardScheme();
        }
    }
    
    private static class FieldSchemaStandardScheme extends StandardScheme<FieldSchema>
    {
        @Override
        public void read(final TProtocol iprot, final FieldSchema struct) throws TException {
            iprot.readStructBegin();
            while (true) {
                final TField schemeField = iprot.readFieldBegin();
                if (schemeField.type == 0) {
                    break;
                }
                switch (schemeField.id) {
                    case 1: {
                        if (schemeField.type == 11) {
                            struct.name = iprot.readString();
                            struct.setNameIsSet(true);
                            break;
                        }
                        TProtocolUtil.skip(iprot, schemeField.type);
                        break;
                    }
                    case 2: {
                        if (schemeField.type == 11) {
                            struct.type = iprot.readString();
                            struct.setTypeIsSet(true);
                            break;
                        }
                        TProtocolUtil.skip(iprot, schemeField.type);
                        break;
                    }
                    case 3: {
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
        public void write(final TProtocol oprot, final FieldSchema struct) throws TException {
            struct.validate();
            oprot.writeStructBegin(FieldSchema.STRUCT_DESC);
            if (struct.name != null) {
                oprot.writeFieldBegin(FieldSchema.NAME_FIELD_DESC);
                oprot.writeString(struct.name);
                oprot.writeFieldEnd();
            }
            if (struct.type != null) {
                oprot.writeFieldBegin(FieldSchema.TYPE_FIELD_DESC);
                oprot.writeString(struct.type);
                oprot.writeFieldEnd();
            }
            if (struct.comment != null) {
                oprot.writeFieldBegin(FieldSchema.COMMENT_FIELD_DESC);
                oprot.writeString(struct.comment);
                oprot.writeFieldEnd();
            }
            oprot.writeFieldStop();
            oprot.writeStructEnd();
        }
    }
    
    private static class FieldSchemaTupleSchemeFactory implements SchemeFactory
    {
        @Override
        public FieldSchemaTupleScheme getScheme() {
            return new FieldSchemaTupleScheme();
        }
    }
    
    private static class FieldSchemaTupleScheme extends TupleScheme<FieldSchema>
    {
        @Override
        public void write(final TProtocol prot, final FieldSchema struct) throws TException {
            final TTupleProtocol oprot = (TTupleProtocol)prot;
            final BitSet optionals = new BitSet();
            if (struct.isSetName()) {
                optionals.set(0);
            }
            if (struct.isSetType()) {
                optionals.set(1);
            }
            if (struct.isSetComment()) {
                optionals.set(2);
            }
            oprot.writeBitSet(optionals, 3);
            if (struct.isSetName()) {
                oprot.writeString(struct.name);
            }
            if (struct.isSetType()) {
                oprot.writeString(struct.type);
            }
            if (struct.isSetComment()) {
                oprot.writeString(struct.comment);
            }
        }
        
        @Override
        public void read(final TProtocol prot, final FieldSchema struct) throws TException {
            final TTupleProtocol iprot = (TTupleProtocol)prot;
            final BitSet incoming = iprot.readBitSet(3);
            if (incoming.get(0)) {
                struct.name = iprot.readString();
                struct.setNameIsSet(true);
            }
            if (incoming.get(1)) {
                struct.type = iprot.readString();
                struct.setTypeIsSet(true);
            }
            if (incoming.get(2)) {
                struct.comment = iprot.readString();
                struct.setCommentIsSet(true);
            }
        }
    }
}
