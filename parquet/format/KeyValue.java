// 
// Decompiled by Procyon v0.5.36
// 

package parquet.format;

import java.util.Iterator;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Collections;
import parquet.org.apache.thrift.meta_data.FieldValueMetaData;
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
import parquet.org.apache.thrift.protocol.TField;
import parquet.org.apache.thrift.protocol.TStruct;
import java.io.Serializable;
import parquet.org.apache.thrift.TBase;

public class KeyValue implements TBase<KeyValue, _Fields>, Serializable, Cloneable
{
    private static final TStruct STRUCT_DESC;
    private static final TField KEY_FIELD_DESC;
    private static final TField VALUE_FIELD_DESC;
    public String key;
    public String value;
    public static final Map<_Fields, FieldMetaData> metaDataMap;
    
    public KeyValue() {
    }
    
    public KeyValue(final String key) {
        this();
        this.key = key;
    }
    
    public KeyValue(final KeyValue other) {
        if (other.isSetKey()) {
            this.key = other.key;
        }
        if (other.isSetValue()) {
            this.value = other.value;
        }
    }
    
    @Override
    public KeyValue deepCopy() {
        return new KeyValue(this);
    }
    
    @Override
    public void clear() {
        this.key = null;
        this.value = null;
    }
    
    public String getKey() {
        return this.key;
    }
    
    public KeyValue setKey(final String key) {
        this.key = key;
        return this;
    }
    
    public void unsetKey() {
        this.key = null;
    }
    
    public boolean isSetKey() {
        return this.key != null;
    }
    
    public void setKeyIsSet(final boolean value) {
        if (!value) {
            this.key = null;
        }
    }
    
    public String getValue() {
        return this.value;
    }
    
    public KeyValue setValue(final String value) {
        this.value = value;
        return this;
    }
    
    public void unsetValue() {
        this.value = null;
    }
    
    public boolean isSetValue() {
        return this.value != null;
    }
    
    public void setValueIsSet(final boolean value) {
        if (!value) {
            this.value = null;
        }
    }
    
    @Override
    public void setFieldValue(final _Fields field, final Object value) {
        switch (field) {
            case KEY: {
                if (value == null) {
                    this.unsetKey();
                    break;
                }
                this.setKey((String)value);
                break;
            }
            case VALUE: {
                if (value == null) {
                    this.unsetValue();
                    break;
                }
                this.setValue((String)value);
                break;
            }
        }
    }
    
    @Override
    public Object getFieldValue(final _Fields field) {
        switch (field) {
            case KEY: {
                return this.getKey();
            }
            case VALUE: {
                return this.getValue();
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
            case KEY: {
                return this.isSetKey();
            }
            case VALUE: {
                return this.isSetValue();
            }
            default: {
                throw new IllegalStateException();
            }
        }
    }
    
    @Override
    public boolean equals(final Object that) {
        return that != null && that instanceof KeyValue && this.equals((KeyValue)that);
    }
    
    public boolean equals(final KeyValue that) {
        if (that == null) {
            return false;
        }
        final boolean this_present_key = this.isSetKey();
        final boolean that_present_key = that.isSetKey();
        if (this_present_key || that_present_key) {
            if (!this_present_key || !that_present_key) {
                return false;
            }
            if (!this.key.equals(that.key)) {
                return false;
            }
        }
        final boolean this_present_value = this.isSetValue();
        final boolean that_present_value = that.isSetValue();
        if (this_present_value || that_present_value) {
            if (!this_present_value || !that_present_value) {
                return false;
            }
            if (!this.value.equals(that.value)) {
                return false;
            }
        }
        return true;
    }
    
    @Override
    public int hashCode() {
        final HashCodeBuilder builder = new HashCodeBuilder();
        final boolean present_key = this.isSetKey();
        builder.append(present_key);
        if (present_key) {
            builder.append(this.key);
        }
        final boolean present_value = this.isSetValue();
        builder.append(present_value);
        if (present_value) {
            builder.append(this.value);
        }
        return builder.toHashCode();
    }
    
    @Override
    public int compareTo(final KeyValue other) {
        if (!this.getClass().equals(other.getClass())) {
            return this.getClass().getName().compareTo(other.getClass().getName());
        }
        int lastComparison = 0;
        final KeyValue typedOther = other;
        lastComparison = Boolean.valueOf(this.isSetKey()).compareTo(Boolean.valueOf(typedOther.isSetKey()));
        if (lastComparison != 0) {
            return lastComparison;
        }
        if (this.isSetKey()) {
            lastComparison = TBaseHelper.compareTo(this.key, typedOther.key);
            if (lastComparison != 0) {
                return lastComparison;
            }
        }
        lastComparison = Boolean.valueOf(this.isSetValue()).compareTo(Boolean.valueOf(typedOther.isSetValue()));
        if (lastComparison != 0) {
            return lastComparison;
        }
        if (this.isSetValue()) {
            lastComparison = TBaseHelper.compareTo(this.value, typedOther.value);
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
                    if (field.type == 11) {
                        this.key = iprot.readString();
                        break;
                    }
                    TProtocolUtil.skip(iprot, field.type);
                    break;
                }
                case 2: {
                    if (field.type == 11) {
                        this.value = iprot.readString();
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
        oprot.writeStructBegin(KeyValue.STRUCT_DESC);
        if (this.key != null) {
            oprot.writeFieldBegin(KeyValue.KEY_FIELD_DESC);
            oprot.writeString(this.key);
            oprot.writeFieldEnd();
        }
        if (this.value != null && this.isSetValue()) {
            oprot.writeFieldBegin(KeyValue.VALUE_FIELD_DESC);
            oprot.writeString(this.value);
            oprot.writeFieldEnd();
        }
        oprot.writeFieldStop();
        oprot.writeStructEnd();
    }
    
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("KeyValue(");
        boolean first = true;
        sb.append("key:");
        if (this.key == null) {
            sb.append("null");
        }
        else {
            sb.append(this.key);
        }
        first = false;
        if (this.isSetValue()) {
            if (!first) {
                sb.append(", ");
            }
            sb.append("value:");
            if (this.value == null) {
                sb.append("null");
            }
            else {
                sb.append(this.value);
            }
            first = false;
        }
        sb.append(")");
        return sb.toString();
    }
    
    public void validate() throws TException {
        if (this.key == null) {
            throw new TProtocolException("Required field 'key' was not present! Struct: " + this.toString());
        }
    }
    
    static {
        STRUCT_DESC = new TStruct("KeyValue");
        KEY_FIELD_DESC = new TField("key", (byte)11, (short)1);
        VALUE_FIELD_DESC = new TField("value", (byte)11, (short)2);
        final Map<_Fields, FieldMetaData> tmpMap = new EnumMap<_Fields, FieldMetaData>(_Fields.class);
        tmpMap.put(_Fields.KEY, new FieldMetaData("key", (byte)1, new FieldValueMetaData((byte)11)));
        tmpMap.put(_Fields.VALUE, new FieldMetaData("value", (byte)2, new FieldValueMetaData((byte)11)));
        FieldMetaData.addStructMetaDataMap(KeyValue.class, metaDataMap = Collections.unmodifiableMap((Map<? extends _Fields, ? extends FieldMetaData>)tmpMap));
    }
    
    public enum _Fields implements TFieldIdEnum
    {
        KEY((short)1, "key"), 
        VALUE((short)2, "value");
        
        private static final Map<String, _Fields> byName;
        private final short _thriftId;
        private final String _fieldName;
        
        public static _Fields findByThriftId(final int fieldId) {
            switch (fieldId) {
                case 1: {
                    return _Fields.KEY;
                }
                case 2: {
                    return _Fields.VALUE;
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
