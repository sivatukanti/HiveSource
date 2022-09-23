// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.hive.metastore.api;

import java.util.BitSet;
import org.apache.thrift.protocol.TTupleProtocol;
import org.apache.thrift.protocol.TMap;
import org.apache.thrift.protocol.TList;
import org.apache.thrift.protocol.TProtocolUtil;
import java.util.EnumSet;
import java.util.Collections;
import org.apache.thrift.meta_data.MapMetaData;
import org.apache.thrift.meta_data.FieldValueMetaData;
import org.apache.thrift.meta_data.ListMetaData;
import org.apache.thrift.meta_data.StructMetaData;
import java.util.EnumMap;
import org.apache.thrift.scheme.TupleScheme;
import org.apache.thrift.scheme.StandardScheme;
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
import java.util.Iterator;
import java.util.HashMap;
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

public class Schema implements TBase<Schema, _Fields>, Serializable, Cloneable
{
    private static final TStruct STRUCT_DESC;
    private static final TField FIELD_SCHEMAS_FIELD_DESC;
    private static final TField PROPERTIES_FIELD_DESC;
    private static final Map<Class<? extends IScheme>, SchemeFactory> schemes;
    private List<FieldSchema> fieldSchemas;
    private Map<String, String> properties;
    public static final Map<_Fields, FieldMetaData> metaDataMap;
    
    public Schema() {
    }
    
    public Schema(final List<FieldSchema> fieldSchemas, final Map<String, String> properties) {
        this();
        this.fieldSchemas = fieldSchemas;
        this.properties = properties;
    }
    
    public Schema(final Schema other) {
        if (other.isSetFieldSchemas()) {
            final List<FieldSchema> __this__fieldSchemas = new ArrayList<FieldSchema>();
            for (final FieldSchema other_element : other.fieldSchemas) {
                __this__fieldSchemas.add(new FieldSchema(other_element));
            }
            this.fieldSchemas = __this__fieldSchemas;
        }
        if (other.isSetProperties()) {
            final Map<String, String> __this__properties = new HashMap<String, String>();
            for (final Map.Entry<String, String> other_element2 : other.properties.entrySet()) {
                final String other_element_key = other_element2.getKey();
                final String other_element_value = other_element2.getValue();
                final String __this__properties_copy_key = other_element_key;
                final String __this__properties_copy_value = other_element_value;
                __this__properties.put(__this__properties_copy_key, __this__properties_copy_value);
            }
            this.properties = __this__properties;
        }
    }
    
    @Override
    public Schema deepCopy() {
        return new Schema(this);
    }
    
    @Override
    public void clear() {
        this.fieldSchemas = null;
        this.properties = null;
    }
    
    public int getFieldSchemasSize() {
        return (this.fieldSchemas == null) ? 0 : this.fieldSchemas.size();
    }
    
    public Iterator<FieldSchema> getFieldSchemasIterator() {
        return (this.fieldSchemas == null) ? null : this.fieldSchemas.iterator();
    }
    
    public void addToFieldSchemas(final FieldSchema elem) {
        if (this.fieldSchemas == null) {
            this.fieldSchemas = new ArrayList<FieldSchema>();
        }
        this.fieldSchemas.add(elem);
    }
    
    public List<FieldSchema> getFieldSchemas() {
        return this.fieldSchemas;
    }
    
    public void setFieldSchemas(final List<FieldSchema> fieldSchemas) {
        this.fieldSchemas = fieldSchemas;
    }
    
    public void unsetFieldSchemas() {
        this.fieldSchemas = null;
    }
    
    public boolean isSetFieldSchemas() {
        return this.fieldSchemas != null;
    }
    
    public void setFieldSchemasIsSet(final boolean value) {
        if (!value) {
            this.fieldSchemas = null;
        }
    }
    
    public int getPropertiesSize() {
        return (this.properties == null) ? 0 : this.properties.size();
    }
    
    public void putToProperties(final String key, final String val) {
        if (this.properties == null) {
            this.properties = new HashMap<String, String>();
        }
        this.properties.put(key, val);
    }
    
    public Map<String, String> getProperties() {
        return this.properties;
    }
    
    public void setProperties(final Map<String, String> properties) {
        this.properties = properties;
    }
    
    public void unsetProperties() {
        this.properties = null;
    }
    
    public boolean isSetProperties() {
        return this.properties != null;
    }
    
    public void setPropertiesIsSet(final boolean value) {
        if (!value) {
            this.properties = null;
        }
    }
    
    @Override
    public void setFieldValue(final _Fields field, final Object value) {
        switch (field) {
            case FIELD_SCHEMAS: {
                if (value == null) {
                    this.unsetFieldSchemas();
                    break;
                }
                this.setFieldSchemas((List<FieldSchema>)value);
                break;
            }
            case PROPERTIES: {
                if (value == null) {
                    this.unsetProperties();
                    break;
                }
                this.setProperties((Map<String, String>)value);
                break;
            }
        }
    }
    
    @Override
    public Object getFieldValue(final _Fields field) {
        switch (field) {
            case FIELD_SCHEMAS: {
                return this.getFieldSchemas();
            }
            case PROPERTIES: {
                return this.getProperties();
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
            case FIELD_SCHEMAS: {
                return this.isSetFieldSchemas();
            }
            case PROPERTIES: {
                return this.isSetProperties();
            }
            default: {
                throw new IllegalStateException();
            }
        }
    }
    
    @Override
    public boolean equals(final Object that) {
        return that != null && that instanceof Schema && this.equals((Schema)that);
    }
    
    public boolean equals(final Schema that) {
        if (that == null) {
            return false;
        }
        final boolean this_present_fieldSchemas = this.isSetFieldSchemas();
        final boolean that_present_fieldSchemas = that.isSetFieldSchemas();
        if (this_present_fieldSchemas || that_present_fieldSchemas) {
            if (!this_present_fieldSchemas || !that_present_fieldSchemas) {
                return false;
            }
            if (!this.fieldSchemas.equals(that.fieldSchemas)) {
                return false;
            }
        }
        final boolean this_present_properties = this.isSetProperties();
        final boolean that_present_properties = that.isSetProperties();
        if (this_present_properties || that_present_properties) {
            if (!this_present_properties || !that_present_properties) {
                return false;
            }
            if (!this.properties.equals(that.properties)) {
                return false;
            }
        }
        return true;
    }
    
    @Override
    public int hashCode() {
        final HashCodeBuilder builder = new HashCodeBuilder();
        final boolean present_fieldSchemas = this.isSetFieldSchemas();
        builder.append(present_fieldSchemas);
        if (present_fieldSchemas) {
            builder.append(this.fieldSchemas);
        }
        final boolean present_properties = this.isSetProperties();
        builder.append(present_properties);
        if (present_properties) {
            builder.append(this.properties);
        }
        return builder.toHashCode();
    }
    
    @Override
    public int compareTo(final Schema other) {
        if (!this.getClass().equals(other.getClass())) {
            return this.getClass().getName().compareTo(other.getClass().getName());
        }
        int lastComparison = 0;
        final Schema typedOther = other;
        lastComparison = Boolean.valueOf(this.isSetFieldSchemas()).compareTo(Boolean.valueOf(typedOther.isSetFieldSchemas()));
        if (lastComparison != 0) {
            return lastComparison;
        }
        if (this.isSetFieldSchemas()) {
            lastComparison = TBaseHelper.compareTo(this.fieldSchemas, typedOther.fieldSchemas);
            if (lastComparison != 0) {
                return lastComparison;
            }
        }
        lastComparison = Boolean.valueOf(this.isSetProperties()).compareTo(Boolean.valueOf(typedOther.isSetProperties()));
        if (lastComparison != 0) {
            return lastComparison;
        }
        if (this.isSetProperties()) {
            lastComparison = TBaseHelper.compareTo(this.properties, typedOther.properties);
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
        Schema.schemes.get(iprot.getScheme()).getScheme().read(iprot, this);
    }
    
    @Override
    public void write(final TProtocol oprot) throws TException {
        Schema.schemes.get(oprot.getScheme()).getScheme().write(oprot, this);
    }
    
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("Schema(");
        boolean first = true;
        sb.append("fieldSchemas:");
        if (this.fieldSchemas == null) {
            sb.append("null");
        }
        else {
            sb.append(this.fieldSchemas);
        }
        first = false;
        if (!first) {
            sb.append(", ");
        }
        sb.append("properties:");
        if (this.properties == null) {
            sb.append("null");
        }
        else {
            sb.append(this.properties);
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
        STRUCT_DESC = new TStruct("Schema");
        FIELD_SCHEMAS_FIELD_DESC = new TField("fieldSchemas", (byte)15, (short)1);
        PROPERTIES_FIELD_DESC = new TField("properties", (byte)13, (short)2);
        (schemes = new HashMap<Class<? extends IScheme>, SchemeFactory>()).put(StandardScheme.class, new SchemaStandardSchemeFactory());
        Schema.schemes.put(TupleScheme.class, new SchemaTupleSchemeFactory());
        final Map<_Fields, FieldMetaData> tmpMap = new EnumMap<_Fields, FieldMetaData>(_Fields.class);
        tmpMap.put(_Fields.FIELD_SCHEMAS, new FieldMetaData("fieldSchemas", (byte)3, new ListMetaData((byte)15, new StructMetaData((byte)12, FieldSchema.class))));
        tmpMap.put(_Fields.PROPERTIES, new FieldMetaData("properties", (byte)3, new MapMetaData((byte)13, new FieldValueMetaData((byte)11), new FieldValueMetaData((byte)11))));
        FieldMetaData.addStructMetaDataMap(Schema.class, metaDataMap = Collections.unmodifiableMap((Map<? extends _Fields, ? extends FieldMetaData>)tmpMap));
    }
    
    public enum _Fields implements TFieldIdEnum
    {
        FIELD_SCHEMAS((short)1, "fieldSchemas"), 
        PROPERTIES((short)2, "properties");
        
        private static final Map<String, _Fields> byName;
        private final short _thriftId;
        private final String _fieldName;
        
        public static _Fields findByThriftId(final int fieldId) {
            switch (fieldId) {
                case 1: {
                    return _Fields.FIELD_SCHEMAS;
                }
                case 2: {
                    return _Fields.PROPERTIES;
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
    
    private static class SchemaStandardSchemeFactory implements SchemeFactory
    {
        @Override
        public SchemaStandardScheme getScheme() {
            return new SchemaStandardScheme();
        }
    }
    
    private static class SchemaStandardScheme extends StandardScheme<Schema>
    {
        @Override
        public void read(final TProtocol iprot, final Schema struct) throws TException {
            iprot.readStructBegin();
            while (true) {
                final TField schemeField = iprot.readFieldBegin();
                if (schemeField.type == 0) {
                    break;
                }
                switch (schemeField.id) {
                    case 1: {
                        if (schemeField.type == 15) {
                            final TList _list294 = iprot.readListBegin();
                            struct.fieldSchemas = (List<FieldSchema>)new ArrayList(_list294.size);
                            for (int _i295 = 0; _i295 < _list294.size; ++_i295) {
                                final FieldSchema _elem296 = new FieldSchema();
                                _elem296.read(iprot);
                                struct.fieldSchemas.add(_elem296);
                            }
                            iprot.readListEnd();
                            struct.setFieldSchemasIsSet(true);
                            break;
                        }
                        TProtocolUtil.skip(iprot, schemeField.type);
                        break;
                    }
                    case 2: {
                        if (schemeField.type == 13) {
                            final TMap _map297 = iprot.readMapBegin();
                            struct.properties = (Map<String, String>)new HashMap(2 * _map297.size);
                            for (int _i296 = 0; _i296 < _map297.size; ++_i296) {
                                final String _key299 = iprot.readString();
                                final String _val300 = iprot.readString();
                                struct.properties.put(_key299, _val300);
                            }
                            iprot.readMapEnd();
                            struct.setPropertiesIsSet(true);
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
        public void write(final TProtocol oprot, final Schema struct) throws TException {
            struct.validate();
            oprot.writeStructBegin(Schema.STRUCT_DESC);
            if (struct.fieldSchemas != null) {
                oprot.writeFieldBegin(Schema.FIELD_SCHEMAS_FIELD_DESC);
                oprot.writeListBegin(new TList((byte)12, struct.fieldSchemas.size()));
                for (final FieldSchema _iter301 : struct.fieldSchemas) {
                    _iter301.write(oprot);
                }
                oprot.writeListEnd();
                oprot.writeFieldEnd();
            }
            if (struct.properties != null) {
                oprot.writeFieldBegin(Schema.PROPERTIES_FIELD_DESC);
                oprot.writeMapBegin(new TMap((byte)11, (byte)11, struct.properties.size()));
                for (final Map.Entry<String, String> _iter302 : struct.properties.entrySet()) {
                    oprot.writeString(_iter302.getKey());
                    oprot.writeString(_iter302.getValue());
                }
                oprot.writeMapEnd();
                oprot.writeFieldEnd();
            }
            oprot.writeFieldStop();
            oprot.writeStructEnd();
        }
    }
    
    private static class SchemaTupleSchemeFactory implements SchemeFactory
    {
        @Override
        public SchemaTupleScheme getScheme() {
            return new SchemaTupleScheme();
        }
    }
    
    private static class SchemaTupleScheme extends TupleScheme<Schema>
    {
        @Override
        public void write(final TProtocol prot, final Schema struct) throws TException {
            final TTupleProtocol oprot = (TTupleProtocol)prot;
            final BitSet optionals = new BitSet();
            if (struct.isSetFieldSchemas()) {
                optionals.set(0);
            }
            if (struct.isSetProperties()) {
                optionals.set(1);
            }
            oprot.writeBitSet(optionals, 2);
            if (struct.isSetFieldSchemas()) {
                oprot.writeI32(struct.fieldSchemas.size());
                for (final FieldSchema _iter303 : struct.fieldSchemas) {
                    _iter303.write(oprot);
                }
            }
            if (struct.isSetProperties()) {
                oprot.writeI32(struct.properties.size());
                for (final Map.Entry<String, String> _iter304 : struct.properties.entrySet()) {
                    oprot.writeString(_iter304.getKey());
                    oprot.writeString(_iter304.getValue());
                }
            }
        }
        
        @Override
        public void read(final TProtocol prot, final Schema struct) throws TException {
            final TTupleProtocol iprot = (TTupleProtocol)prot;
            final BitSet incoming = iprot.readBitSet(2);
            if (incoming.get(0)) {
                final TList _list305 = new TList((byte)12, iprot.readI32());
                struct.fieldSchemas = (List<FieldSchema>)new ArrayList(_list305.size);
                for (int _i306 = 0; _i306 < _list305.size; ++_i306) {
                    final FieldSchema _elem307 = new FieldSchema();
                    _elem307.read(iprot);
                    struct.fieldSchemas.add(_elem307);
                }
                struct.setFieldSchemasIsSet(true);
            }
            if (incoming.get(1)) {
                final TMap _map308 = new TMap((byte)11, (byte)11, iprot.readI32());
                struct.properties = (Map<String, String>)new HashMap(2 * _map308.size);
                for (int _i307 = 0; _i307 < _map308.size; ++_i307) {
                    final String _key310 = iprot.readString();
                    final String _val311 = iprot.readString();
                    struct.properties.put(_key310, _val311);
                }
                struct.setPropertiesIsSet(true);
            }
        }
    }
}
