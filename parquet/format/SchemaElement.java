// 
// Decompiled by Procyon v0.5.36
// 

package parquet.format;

import java.util.Iterator;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Collections;
import parquet.org.apache.thrift.meta_data.FieldValueMetaData;
import parquet.org.apache.thrift.TEnum;
import parquet.org.apache.thrift.meta_data.EnumMetaData;
import java.util.EnumMap;
import parquet.org.apache.thrift.TFieldIdEnum;
import parquet.org.apache.thrift.protocol.TProtocolException;
import parquet.org.apache.thrift.TException;
import parquet.org.apache.thrift.protocol.TProtocolUtil;
import parquet.org.apache.thrift.protocol.TProtocol;
import parquet.org.apache.thrift.TBaseHelper;
import org.apache.commons.lang.builder.HashCodeBuilder;
import parquet.org.apache.thrift.meta_data.FieldMetaData;
import java.util.Map;
import java.util.BitSet;
import parquet.org.apache.thrift.protocol.TField;
import parquet.org.apache.thrift.protocol.TStruct;
import java.io.Serializable;
import parquet.org.apache.thrift.TBase;

public class SchemaElement implements TBase<SchemaElement, _Fields>, Serializable, Cloneable
{
    private static final TStruct STRUCT_DESC;
    private static final TField TYPE_FIELD_DESC;
    private static final TField TYPE_LENGTH_FIELD_DESC;
    private static final TField REPETITION_TYPE_FIELD_DESC;
    private static final TField NAME_FIELD_DESC;
    private static final TField NUM_CHILDREN_FIELD_DESC;
    private static final TField CONVERTED_TYPE_FIELD_DESC;
    private static final TField SCALE_FIELD_DESC;
    private static final TField PRECISION_FIELD_DESC;
    private static final TField FIELD_ID_FIELD_DESC;
    public Type type;
    public int type_length;
    public FieldRepetitionType repetition_type;
    public String name;
    public int num_children;
    public ConvertedType converted_type;
    public int scale;
    public int precision;
    public int field_id;
    private static final int __TYPE_LENGTH_ISSET_ID = 0;
    private static final int __NUM_CHILDREN_ISSET_ID = 1;
    private static final int __SCALE_ISSET_ID = 2;
    private static final int __PRECISION_ISSET_ID = 3;
    private static final int __FIELD_ID_ISSET_ID = 4;
    private BitSet __isset_bit_vector;
    public static final Map<_Fields, FieldMetaData> metaDataMap;
    
    public SchemaElement() {
        this.__isset_bit_vector = new BitSet(5);
    }
    
    public SchemaElement(final String name) {
        this();
        this.name = name;
    }
    
    public SchemaElement(final SchemaElement other) {
        (this.__isset_bit_vector = new BitSet(5)).clear();
        this.__isset_bit_vector.or(other.__isset_bit_vector);
        if (other.isSetType()) {
            this.type = other.type;
        }
        this.type_length = other.type_length;
        if (other.isSetRepetition_type()) {
            this.repetition_type = other.repetition_type;
        }
        if (other.isSetName()) {
            this.name = other.name;
        }
        this.num_children = other.num_children;
        if (other.isSetConverted_type()) {
            this.converted_type = other.converted_type;
        }
        this.scale = other.scale;
        this.precision = other.precision;
        this.field_id = other.field_id;
    }
    
    @Override
    public SchemaElement deepCopy() {
        return new SchemaElement(this);
    }
    
    @Override
    public void clear() {
        this.type = null;
        this.setType_lengthIsSet(false);
        this.type_length = 0;
        this.repetition_type = null;
        this.name = null;
        this.setNum_childrenIsSet(false);
        this.num_children = 0;
        this.converted_type = null;
        this.setScaleIsSet(false);
        this.scale = 0;
        this.setPrecisionIsSet(false);
        this.precision = 0;
        this.setField_idIsSet(false);
        this.field_id = 0;
    }
    
    public Type getType() {
        return this.type;
    }
    
    public SchemaElement setType(final Type type) {
        this.type = type;
        return this;
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
    
    public int getType_length() {
        return this.type_length;
    }
    
    public SchemaElement setType_length(final int type_length) {
        this.type_length = type_length;
        this.setType_lengthIsSet(true);
        return this;
    }
    
    public void unsetType_length() {
        this.__isset_bit_vector.clear(0);
    }
    
    public boolean isSetType_length() {
        return this.__isset_bit_vector.get(0);
    }
    
    public void setType_lengthIsSet(final boolean value) {
        this.__isset_bit_vector.set(0, value);
    }
    
    public FieldRepetitionType getRepetition_type() {
        return this.repetition_type;
    }
    
    public SchemaElement setRepetition_type(final FieldRepetitionType repetition_type) {
        this.repetition_type = repetition_type;
        return this;
    }
    
    public void unsetRepetition_type() {
        this.repetition_type = null;
    }
    
    public boolean isSetRepetition_type() {
        return this.repetition_type != null;
    }
    
    public void setRepetition_typeIsSet(final boolean value) {
        if (!value) {
            this.repetition_type = null;
        }
    }
    
    public String getName() {
        return this.name;
    }
    
    public SchemaElement setName(final String name) {
        this.name = name;
        return this;
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
    
    public int getNum_children() {
        return this.num_children;
    }
    
    public SchemaElement setNum_children(final int num_children) {
        this.num_children = num_children;
        this.setNum_childrenIsSet(true);
        return this;
    }
    
    public void unsetNum_children() {
        this.__isset_bit_vector.clear(1);
    }
    
    public boolean isSetNum_children() {
        return this.__isset_bit_vector.get(1);
    }
    
    public void setNum_childrenIsSet(final boolean value) {
        this.__isset_bit_vector.set(1, value);
    }
    
    public ConvertedType getConverted_type() {
        return this.converted_type;
    }
    
    public SchemaElement setConverted_type(final ConvertedType converted_type) {
        this.converted_type = converted_type;
        return this;
    }
    
    public void unsetConverted_type() {
        this.converted_type = null;
    }
    
    public boolean isSetConverted_type() {
        return this.converted_type != null;
    }
    
    public void setConverted_typeIsSet(final boolean value) {
        if (!value) {
            this.converted_type = null;
        }
    }
    
    public int getScale() {
        return this.scale;
    }
    
    public SchemaElement setScale(final int scale) {
        this.scale = scale;
        this.setScaleIsSet(true);
        return this;
    }
    
    public void unsetScale() {
        this.__isset_bit_vector.clear(2);
    }
    
    public boolean isSetScale() {
        return this.__isset_bit_vector.get(2);
    }
    
    public void setScaleIsSet(final boolean value) {
        this.__isset_bit_vector.set(2, value);
    }
    
    public int getPrecision() {
        return this.precision;
    }
    
    public SchemaElement setPrecision(final int precision) {
        this.precision = precision;
        this.setPrecisionIsSet(true);
        return this;
    }
    
    public void unsetPrecision() {
        this.__isset_bit_vector.clear(3);
    }
    
    public boolean isSetPrecision() {
        return this.__isset_bit_vector.get(3);
    }
    
    public void setPrecisionIsSet(final boolean value) {
        this.__isset_bit_vector.set(3, value);
    }
    
    public int getField_id() {
        return this.field_id;
    }
    
    public SchemaElement setField_id(final int field_id) {
        this.field_id = field_id;
        this.setField_idIsSet(true);
        return this;
    }
    
    public void unsetField_id() {
        this.__isset_bit_vector.clear(4);
    }
    
    public boolean isSetField_id() {
        return this.__isset_bit_vector.get(4);
    }
    
    public void setField_idIsSet(final boolean value) {
        this.__isset_bit_vector.set(4, value);
    }
    
    @Override
    public void setFieldValue(final _Fields field, final Object value) {
        switch (field) {
            case TYPE: {
                if (value == null) {
                    this.unsetType();
                    break;
                }
                this.setType((Type)value);
                break;
            }
            case TYPE_LENGTH: {
                if (value == null) {
                    this.unsetType_length();
                    break;
                }
                this.setType_length((int)value);
                break;
            }
            case REPETITION_TYPE: {
                if (value == null) {
                    this.unsetRepetition_type();
                    break;
                }
                this.setRepetition_type((FieldRepetitionType)value);
                break;
            }
            case NAME: {
                if (value == null) {
                    this.unsetName();
                    break;
                }
                this.setName((String)value);
                break;
            }
            case NUM_CHILDREN: {
                if (value == null) {
                    this.unsetNum_children();
                    break;
                }
                this.setNum_children((int)value);
                break;
            }
            case CONVERTED_TYPE: {
                if (value == null) {
                    this.unsetConverted_type();
                    break;
                }
                this.setConverted_type((ConvertedType)value);
                break;
            }
            case SCALE: {
                if (value == null) {
                    this.unsetScale();
                    break;
                }
                this.setScale((int)value);
                break;
            }
            case PRECISION: {
                if (value == null) {
                    this.unsetPrecision();
                    break;
                }
                this.setPrecision((int)value);
                break;
            }
            case FIELD_ID: {
                if (value == null) {
                    this.unsetField_id();
                    break;
                }
                this.setField_id((int)value);
                break;
            }
        }
    }
    
    @Override
    public Object getFieldValue(final _Fields field) {
        switch (field) {
            case TYPE: {
                return this.getType();
            }
            case TYPE_LENGTH: {
                return new Integer(this.getType_length());
            }
            case REPETITION_TYPE: {
                return this.getRepetition_type();
            }
            case NAME: {
                return this.getName();
            }
            case NUM_CHILDREN: {
                return new Integer(this.getNum_children());
            }
            case CONVERTED_TYPE: {
                return this.getConverted_type();
            }
            case SCALE: {
                return new Integer(this.getScale());
            }
            case PRECISION: {
                return new Integer(this.getPrecision());
            }
            case FIELD_ID: {
                return new Integer(this.getField_id());
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
            case TYPE: {
                return this.isSetType();
            }
            case TYPE_LENGTH: {
                return this.isSetType_length();
            }
            case REPETITION_TYPE: {
                return this.isSetRepetition_type();
            }
            case NAME: {
                return this.isSetName();
            }
            case NUM_CHILDREN: {
                return this.isSetNum_children();
            }
            case CONVERTED_TYPE: {
                return this.isSetConverted_type();
            }
            case SCALE: {
                return this.isSetScale();
            }
            case PRECISION: {
                return this.isSetPrecision();
            }
            case FIELD_ID: {
                return this.isSetField_id();
            }
            default: {
                throw new IllegalStateException();
            }
        }
    }
    
    @Override
    public boolean equals(final Object that) {
        return that != null && that instanceof SchemaElement && this.equals((SchemaElement)that);
    }
    
    public boolean equals(final SchemaElement that) {
        if (that == null) {
            return false;
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
        final boolean this_present_type_length = this.isSetType_length();
        final boolean that_present_type_length = that.isSetType_length();
        if (this_present_type_length || that_present_type_length) {
            if (!this_present_type_length || !that_present_type_length) {
                return false;
            }
            if (this.type_length != that.type_length) {
                return false;
            }
        }
        final boolean this_present_repetition_type = this.isSetRepetition_type();
        final boolean that_present_repetition_type = that.isSetRepetition_type();
        if (this_present_repetition_type || that_present_repetition_type) {
            if (!this_present_repetition_type || !that_present_repetition_type) {
                return false;
            }
            if (!this.repetition_type.equals(that.repetition_type)) {
                return false;
            }
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
        final boolean this_present_num_children = this.isSetNum_children();
        final boolean that_present_num_children = that.isSetNum_children();
        if (this_present_num_children || that_present_num_children) {
            if (!this_present_num_children || !that_present_num_children) {
                return false;
            }
            if (this.num_children != that.num_children) {
                return false;
            }
        }
        final boolean this_present_converted_type = this.isSetConverted_type();
        final boolean that_present_converted_type = that.isSetConverted_type();
        if (this_present_converted_type || that_present_converted_type) {
            if (!this_present_converted_type || !that_present_converted_type) {
                return false;
            }
            if (!this.converted_type.equals(that.converted_type)) {
                return false;
            }
        }
        final boolean this_present_scale = this.isSetScale();
        final boolean that_present_scale = that.isSetScale();
        if (this_present_scale || that_present_scale) {
            if (!this_present_scale || !that_present_scale) {
                return false;
            }
            if (this.scale != that.scale) {
                return false;
            }
        }
        final boolean this_present_precision = this.isSetPrecision();
        final boolean that_present_precision = that.isSetPrecision();
        if (this_present_precision || that_present_precision) {
            if (!this_present_precision || !that_present_precision) {
                return false;
            }
            if (this.precision != that.precision) {
                return false;
            }
        }
        final boolean this_present_field_id = this.isSetField_id();
        final boolean that_present_field_id = that.isSetField_id();
        if (this_present_field_id || that_present_field_id) {
            if (!this_present_field_id || !that_present_field_id) {
                return false;
            }
            if (this.field_id != that.field_id) {
                return false;
            }
        }
        return true;
    }
    
    @Override
    public int hashCode() {
        final HashCodeBuilder builder = new HashCodeBuilder();
        final boolean present_type = this.isSetType();
        builder.append(present_type);
        if (present_type) {
            builder.append(this.type.getValue());
        }
        final boolean present_type_length = this.isSetType_length();
        builder.append(present_type_length);
        if (present_type_length) {
            builder.append(this.type_length);
        }
        final boolean present_repetition_type = this.isSetRepetition_type();
        builder.append(present_repetition_type);
        if (present_repetition_type) {
            builder.append(this.repetition_type.getValue());
        }
        final boolean present_name = this.isSetName();
        builder.append(present_name);
        if (present_name) {
            builder.append(this.name);
        }
        final boolean present_num_children = this.isSetNum_children();
        builder.append(present_num_children);
        if (present_num_children) {
            builder.append(this.num_children);
        }
        final boolean present_converted_type = this.isSetConverted_type();
        builder.append(present_converted_type);
        if (present_converted_type) {
            builder.append(this.converted_type.getValue());
        }
        final boolean present_scale = this.isSetScale();
        builder.append(present_scale);
        if (present_scale) {
            builder.append(this.scale);
        }
        final boolean present_precision = this.isSetPrecision();
        builder.append(present_precision);
        if (present_precision) {
            builder.append(this.precision);
        }
        final boolean present_field_id = this.isSetField_id();
        builder.append(present_field_id);
        if (present_field_id) {
            builder.append(this.field_id);
        }
        return builder.toHashCode();
    }
    
    @Override
    public int compareTo(final SchemaElement other) {
        if (!this.getClass().equals(other.getClass())) {
            return this.getClass().getName().compareTo(other.getClass().getName());
        }
        int lastComparison = 0;
        final SchemaElement typedOther = other;
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
        lastComparison = Boolean.valueOf(this.isSetType_length()).compareTo(Boolean.valueOf(typedOther.isSetType_length()));
        if (lastComparison != 0) {
            return lastComparison;
        }
        if (this.isSetType_length()) {
            lastComparison = TBaseHelper.compareTo(this.type_length, typedOther.type_length);
            if (lastComparison != 0) {
                return lastComparison;
            }
        }
        lastComparison = Boolean.valueOf(this.isSetRepetition_type()).compareTo(Boolean.valueOf(typedOther.isSetRepetition_type()));
        if (lastComparison != 0) {
            return lastComparison;
        }
        if (this.isSetRepetition_type()) {
            lastComparison = TBaseHelper.compareTo(this.repetition_type, typedOther.repetition_type);
            if (lastComparison != 0) {
                return lastComparison;
            }
        }
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
        lastComparison = Boolean.valueOf(this.isSetNum_children()).compareTo(Boolean.valueOf(typedOther.isSetNum_children()));
        if (lastComparison != 0) {
            return lastComparison;
        }
        if (this.isSetNum_children()) {
            lastComparison = TBaseHelper.compareTo(this.num_children, typedOther.num_children);
            if (lastComparison != 0) {
                return lastComparison;
            }
        }
        lastComparison = Boolean.valueOf(this.isSetConverted_type()).compareTo(Boolean.valueOf(typedOther.isSetConverted_type()));
        if (lastComparison != 0) {
            return lastComparison;
        }
        if (this.isSetConverted_type()) {
            lastComparison = TBaseHelper.compareTo(this.converted_type, typedOther.converted_type);
            if (lastComparison != 0) {
                return lastComparison;
            }
        }
        lastComparison = Boolean.valueOf(this.isSetScale()).compareTo(Boolean.valueOf(typedOther.isSetScale()));
        if (lastComparison != 0) {
            return lastComparison;
        }
        if (this.isSetScale()) {
            lastComparison = TBaseHelper.compareTo(this.scale, typedOther.scale);
            if (lastComparison != 0) {
                return lastComparison;
            }
        }
        lastComparison = Boolean.valueOf(this.isSetPrecision()).compareTo(Boolean.valueOf(typedOther.isSetPrecision()));
        if (lastComparison != 0) {
            return lastComparison;
        }
        if (this.isSetPrecision()) {
            lastComparison = TBaseHelper.compareTo(this.precision, typedOther.precision);
            if (lastComparison != 0) {
                return lastComparison;
            }
        }
        lastComparison = Boolean.valueOf(this.isSetField_id()).compareTo(Boolean.valueOf(typedOther.isSetField_id()));
        if (lastComparison != 0) {
            return lastComparison;
        }
        if (this.isSetField_id()) {
            lastComparison = TBaseHelper.compareTo(this.field_id, typedOther.field_id);
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
        iprot.readStructBegin();
        while (true) {
            final TField field = iprot.readFieldBegin();
            if (field.type == 0) {
                break;
            }
            switch (field.id) {
                case 1: {
                    if (field.type == 8) {
                        this.type = Type.findByValue(iprot.readI32());
                        break;
                    }
                    TProtocolUtil.skip(iprot, field.type);
                    break;
                }
                case 2: {
                    if (field.type == 8) {
                        this.type_length = iprot.readI32();
                        this.setType_lengthIsSet(true);
                        break;
                    }
                    TProtocolUtil.skip(iprot, field.type);
                    break;
                }
                case 3: {
                    if (field.type == 8) {
                        this.repetition_type = FieldRepetitionType.findByValue(iprot.readI32());
                        break;
                    }
                    TProtocolUtil.skip(iprot, field.type);
                    break;
                }
                case 4: {
                    if (field.type == 11) {
                        this.name = iprot.readString();
                        break;
                    }
                    TProtocolUtil.skip(iprot, field.type);
                    break;
                }
                case 5: {
                    if (field.type == 8) {
                        this.num_children = iprot.readI32();
                        this.setNum_childrenIsSet(true);
                        break;
                    }
                    TProtocolUtil.skip(iprot, field.type);
                    break;
                }
                case 6: {
                    if (field.type == 8) {
                        this.converted_type = ConvertedType.findByValue(iprot.readI32());
                        break;
                    }
                    TProtocolUtil.skip(iprot, field.type);
                    break;
                }
                case 7: {
                    if (field.type == 8) {
                        this.scale = iprot.readI32();
                        this.setScaleIsSet(true);
                        break;
                    }
                    TProtocolUtil.skip(iprot, field.type);
                    break;
                }
                case 8: {
                    if (field.type == 8) {
                        this.precision = iprot.readI32();
                        this.setPrecisionIsSet(true);
                        break;
                    }
                    TProtocolUtil.skip(iprot, field.type);
                    break;
                }
                case 9: {
                    if (field.type == 8) {
                        this.field_id = iprot.readI32();
                        this.setField_idIsSet(true);
                        break;
                    }
                    TProtocolUtil.skip(iprot, field.type);
                    break;
                }
                default: {
                    TProtocolUtil.skip(iprot, field.type);
                    break;
                }
            }
            iprot.readFieldEnd();
        }
        iprot.readStructEnd();
        this.validate();
    }
    
    @Override
    public void write(final TProtocol oprot) throws TException {
        this.validate();
        oprot.writeStructBegin(SchemaElement.STRUCT_DESC);
        if (this.type != null && this.isSetType()) {
            oprot.writeFieldBegin(SchemaElement.TYPE_FIELD_DESC);
            oprot.writeI32(this.type.getValue());
            oprot.writeFieldEnd();
        }
        if (this.isSetType_length()) {
            oprot.writeFieldBegin(SchemaElement.TYPE_LENGTH_FIELD_DESC);
            oprot.writeI32(this.type_length);
            oprot.writeFieldEnd();
        }
        if (this.repetition_type != null && this.isSetRepetition_type()) {
            oprot.writeFieldBegin(SchemaElement.REPETITION_TYPE_FIELD_DESC);
            oprot.writeI32(this.repetition_type.getValue());
            oprot.writeFieldEnd();
        }
        if (this.name != null) {
            oprot.writeFieldBegin(SchemaElement.NAME_FIELD_DESC);
            oprot.writeString(this.name);
            oprot.writeFieldEnd();
        }
        if (this.isSetNum_children()) {
            oprot.writeFieldBegin(SchemaElement.NUM_CHILDREN_FIELD_DESC);
            oprot.writeI32(this.num_children);
            oprot.writeFieldEnd();
        }
        if (this.converted_type != null && this.isSetConverted_type()) {
            oprot.writeFieldBegin(SchemaElement.CONVERTED_TYPE_FIELD_DESC);
            oprot.writeI32(this.converted_type.getValue());
            oprot.writeFieldEnd();
        }
        if (this.isSetScale()) {
            oprot.writeFieldBegin(SchemaElement.SCALE_FIELD_DESC);
            oprot.writeI32(this.scale);
            oprot.writeFieldEnd();
        }
        if (this.isSetPrecision()) {
            oprot.writeFieldBegin(SchemaElement.PRECISION_FIELD_DESC);
            oprot.writeI32(this.precision);
            oprot.writeFieldEnd();
        }
        if (this.isSetField_id()) {
            oprot.writeFieldBegin(SchemaElement.FIELD_ID_FIELD_DESC);
            oprot.writeI32(this.field_id);
            oprot.writeFieldEnd();
        }
        oprot.writeFieldStop();
        oprot.writeStructEnd();
    }
    
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("SchemaElement(");
        boolean first = true;
        if (this.isSetType()) {
            sb.append("type:");
            if (this.type == null) {
                sb.append("null");
            }
            else {
                sb.append(this.type);
            }
            first = false;
        }
        if (this.isSetType_length()) {
            if (!first) {
                sb.append(", ");
            }
            sb.append("type_length:");
            sb.append(this.type_length);
            first = false;
        }
        if (this.isSetRepetition_type()) {
            if (!first) {
                sb.append(", ");
            }
            sb.append("repetition_type:");
            if (this.repetition_type == null) {
                sb.append("null");
            }
            else {
                sb.append(this.repetition_type);
            }
            first = false;
        }
        if (!first) {
            sb.append(", ");
        }
        sb.append("name:");
        if (this.name == null) {
            sb.append("null");
        }
        else {
            sb.append(this.name);
        }
        first = false;
        if (this.isSetNum_children()) {
            if (!first) {
                sb.append(", ");
            }
            sb.append("num_children:");
            sb.append(this.num_children);
            first = false;
        }
        if (this.isSetConverted_type()) {
            if (!first) {
                sb.append(", ");
            }
            sb.append("converted_type:");
            if (this.converted_type == null) {
                sb.append("null");
            }
            else {
                sb.append(this.converted_type);
            }
            first = false;
        }
        if (this.isSetScale()) {
            if (!first) {
                sb.append(", ");
            }
            sb.append("scale:");
            sb.append(this.scale);
            first = false;
        }
        if (this.isSetPrecision()) {
            if (!first) {
                sb.append(", ");
            }
            sb.append("precision:");
            sb.append(this.precision);
            first = false;
        }
        if (this.isSetField_id()) {
            if (!first) {
                sb.append(", ");
            }
            sb.append("field_id:");
            sb.append(this.field_id);
            first = false;
        }
        sb.append(")");
        return sb.toString();
    }
    
    public void validate() throws TException {
        if (this.name == null) {
            throw new TProtocolException("Required field 'name' was not present! Struct: " + this.toString());
        }
    }
    
    static {
        STRUCT_DESC = new TStruct("SchemaElement");
        TYPE_FIELD_DESC = new TField("type", (byte)8, (short)1);
        TYPE_LENGTH_FIELD_DESC = new TField("type_length", (byte)8, (short)2);
        REPETITION_TYPE_FIELD_DESC = new TField("repetition_type", (byte)8, (short)3);
        NAME_FIELD_DESC = new TField("name", (byte)11, (short)4);
        NUM_CHILDREN_FIELD_DESC = new TField("num_children", (byte)8, (short)5);
        CONVERTED_TYPE_FIELD_DESC = new TField("converted_type", (byte)8, (short)6);
        SCALE_FIELD_DESC = new TField("scale", (byte)8, (short)7);
        PRECISION_FIELD_DESC = new TField("precision", (byte)8, (short)8);
        FIELD_ID_FIELD_DESC = new TField("field_id", (byte)8, (short)9);
        final Map<_Fields, FieldMetaData> tmpMap = new EnumMap<_Fields, FieldMetaData>(_Fields.class);
        tmpMap.put(_Fields.TYPE, new FieldMetaData("type", (byte)2, new EnumMetaData((byte)16, Type.class)));
        tmpMap.put(_Fields.TYPE_LENGTH, new FieldMetaData("type_length", (byte)2, new FieldValueMetaData((byte)8)));
        tmpMap.put(_Fields.REPETITION_TYPE, new FieldMetaData("repetition_type", (byte)2, new EnumMetaData((byte)16, FieldRepetitionType.class)));
        tmpMap.put(_Fields.NAME, new FieldMetaData("name", (byte)1, new FieldValueMetaData((byte)11)));
        tmpMap.put(_Fields.NUM_CHILDREN, new FieldMetaData("num_children", (byte)2, new FieldValueMetaData((byte)8)));
        tmpMap.put(_Fields.CONVERTED_TYPE, new FieldMetaData("converted_type", (byte)2, new EnumMetaData((byte)16, ConvertedType.class)));
        tmpMap.put(_Fields.SCALE, new FieldMetaData("scale", (byte)2, new FieldValueMetaData((byte)8)));
        tmpMap.put(_Fields.PRECISION, new FieldMetaData("precision", (byte)2, new FieldValueMetaData((byte)8)));
        tmpMap.put(_Fields.FIELD_ID, new FieldMetaData("field_id", (byte)2, new FieldValueMetaData((byte)8)));
        FieldMetaData.addStructMetaDataMap(SchemaElement.class, metaDataMap = Collections.unmodifiableMap((Map<? extends _Fields, ? extends FieldMetaData>)tmpMap));
    }
    
    public enum _Fields implements TFieldIdEnum
    {
        TYPE((short)1, "type"), 
        TYPE_LENGTH((short)2, "type_length"), 
        REPETITION_TYPE((short)3, "repetition_type"), 
        NAME((short)4, "name"), 
        NUM_CHILDREN((short)5, "num_children"), 
        CONVERTED_TYPE((short)6, "converted_type"), 
        SCALE((short)7, "scale"), 
        PRECISION((short)8, "precision"), 
        FIELD_ID((short)9, "field_id");
        
        private static final Map<String, _Fields> byName;
        private final short _thriftId;
        private final String _fieldName;
        
        public static _Fields findByThriftId(final int fieldId) {
            switch (fieldId) {
                case 1: {
                    return _Fields.TYPE;
                }
                case 2: {
                    return _Fields.TYPE_LENGTH;
                }
                case 3: {
                    return _Fields.REPETITION_TYPE;
                }
                case 4: {
                    return _Fields.NAME;
                }
                case 5: {
                    return _Fields.NUM_CHILDREN;
                }
                case 6: {
                    return _Fields.CONVERTED_TYPE;
                }
                case 7: {
                    return _Fields.SCALE;
                }
                case 8: {
                    return _Fields.PRECISION;
                }
                case 9: {
                    return _Fields.FIELD_ID;
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
}
