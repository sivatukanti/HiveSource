// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.hive.metastore.api;

import java.util.BitSet;
import org.apache.thrift.protocol.TTupleProtocol;
import org.apache.thrift.protocol.TMap;
import org.apache.thrift.protocol.TProtocolUtil;
import java.util.EnumSet;
import java.util.Collections;
import org.apache.thrift.meta_data.MapMetaData;
import org.apache.thrift.meta_data.FieldValueMetaData;
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
import org.apache.thrift.meta_data.FieldMetaData;
import org.apache.thrift.scheme.SchemeFactory;
import org.apache.thrift.scheme.IScheme;
import java.util.Map;
import org.apache.thrift.protocol.TField;
import org.apache.thrift.protocol.TStruct;
import java.io.Serializable;
import org.apache.thrift.TBase;

public class EnvironmentContext implements TBase<EnvironmentContext, _Fields>, Serializable, Cloneable
{
    private static final TStruct STRUCT_DESC;
    private static final TField PROPERTIES_FIELD_DESC;
    private static final Map<Class<? extends IScheme>, SchemeFactory> schemes;
    private Map<String, String> properties;
    public static final Map<_Fields, FieldMetaData> metaDataMap;
    
    public EnvironmentContext() {
    }
    
    public EnvironmentContext(final Map<String, String> properties) {
        this();
        this.properties = properties;
    }
    
    public EnvironmentContext(final EnvironmentContext other) {
        if (other.isSetProperties()) {
            final Map<String, String> __this__properties = new HashMap<String, String>();
            for (final Map.Entry<String, String> other_element : other.properties.entrySet()) {
                final String other_element_key = other_element.getKey();
                final String other_element_value = other_element.getValue();
                final String __this__properties_copy_key = other_element_key;
                final String __this__properties_copy_value = other_element_value;
                __this__properties.put(__this__properties_copy_key, __this__properties_copy_value);
            }
            this.properties = __this__properties;
        }
    }
    
    @Override
    public EnvironmentContext deepCopy() {
        return new EnvironmentContext(this);
    }
    
    @Override
    public void clear() {
        this.properties = null;
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
        return that != null && that instanceof EnvironmentContext && this.equals((EnvironmentContext)that);
    }
    
    public boolean equals(final EnvironmentContext that) {
        if (that == null) {
            return false;
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
        final boolean present_properties = this.isSetProperties();
        builder.append(present_properties);
        if (present_properties) {
            builder.append(this.properties);
        }
        return builder.toHashCode();
    }
    
    @Override
    public int compareTo(final EnvironmentContext other) {
        if (!this.getClass().equals(other.getClass())) {
            return this.getClass().getName().compareTo(other.getClass().getName());
        }
        int lastComparison = 0;
        final EnvironmentContext typedOther = other;
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
        EnvironmentContext.schemes.get(iprot.getScheme()).getScheme().read(iprot, this);
    }
    
    @Override
    public void write(final TProtocol oprot) throws TException {
        EnvironmentContext.schemes.get(oprot.getScheme()).getScheme().write(oprot, this);
    }
    
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("EnvironmentContext(");
        boolean first = true;
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
        STRUCT_DESC = new TStruct("EnvironmentContext");
        PROPERTIES_FIELD_DESC = new TField("properties", (byte)13, (short)1);
        (schemes = new HashMap<Class<? extends IScheme>, SchemeFactory>()).put(StandardScheme.class, new EnvironmentContextStandardSchemeFactory());
        EnvironmentContext.schemes.put(TupleScheme.class, new EnvironmentContextTupleSchemeFactory());
        final Map<_Fields, FieldMetaData> tmpMap = new EnumMap<_Fields, FieldMetaData>(_Fields.class);
        tmpMap.put(_Fields.PROPERTIES, new FieldMetaData("properties", (byte)3, new MapMetaData((byte)13, new FieldValueMetaData((byte)11), new FieldValueMetaData((byte)11))));
        FieldMetaData.addStructMetaDataMap(EnvironmentContext.class, metaDataMap = Collections.unmodifiableMap((Map<? extends _Fields, ? extends FieldMetaData>)tmpMap));
    }
    
    public enum _Fields implements TFieldIdEnum
    {
        PROPERTIES((short)1, "properties");
        
        private static final Map<String, _Fields> byName;
        private final short _thriftId;
        private final String _fieldName;
        
        public static _Fields findByThriftId(final int fieldId) {
            switch (fieldId) {
                case 1: {
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
    
    private static class EnvironmentContextStandardSchemeFactory implements SchemeFactory
    {
        @Override
        public EnvironmentContextStandardScheme getScheme() {
            return new EnvironmentContextStandardScheme();
        }
    }
    
    private static class EnvironmentContextStandardScheme extends StandardScheme<EnvironmentContext>
    {
        @Override
        public void read(final TProtocol iprot, final EnvironmentContext struct) throws TException {
            iprot.readStructBegin();
            while (true) {
                final TField schemeField = iprot.readFieldBegin();
                if (schemeField.type == 0) {
                    break;
                }
                switch (schemeField.id) {
                    case 1: {
                        if (schemeField.type == 13) {
                            final TMap _map312 = iprot.readMapBegin();
                            struct.properties = (Map<String, String>)new HashMap(2 * _map312.size);
                            for (int _i313 = 0; _i313 < _map312.size; ++_i313) {
                                final String _key314 = iprot.readString();
                                final String _val315 = iprot.readString();
                                struct.properties.put(_key314, _val315);
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
        public void write(final TProtocol oprot, final EnvironmentContext struct) throws TException {
            struct.validate();
            oprot.writeStructBegin(EnvironmentContext.STRUCT_DESC);
            if (struct.properties != null) {
                oprot.writeFieldBegin(EnvironmentContext.PROPERTIES_FIELD_DESC);
                oprot.writeMapBegin(new TMap((byte)11, (byte)11, struct.properties.size()));
                for (final Map.Entry<String, String> _iter316 : struct.properties.entrySet()) {
                    oprot.writeString(_iter316.getKey());
                    oprot.writeString(_iter316.getValue());
                }
                oprot.writeMapEnd();
                oprot.writeFieldEnd();
            }
            oprot.writeFieldStop();
            oprot.writeStructEnd();
        }
    }
    
    private static class EnvironmentContextTupleSchemeFactory implements SchemeFactory
    {
        @Override
        public EnvironmentContextTupleScheme getScheme() {
            return new EnvironmentContextTupleScheme();
        }
    }
    
    private static class EnvironmentContextTupleScheme extends TupleScheme<EnvironmentContext>
    {
        @Override
        public void write(final TProtocol prot, final EnvironmentContext struct) throws TException {
            final TTupleProtocol oprot = (TTupleProtocol)prot;
            final BitSet optionals = new BitSet();
            if (struct.isSetProperties()) {
                optionals.set(0);
            }
            oprot.writeBitSet(optionals, 1);
            if (struct.isSetProperties()) {
                oprot.writeI32(struct.properties.size());
                for (final Map.Entry<String, String> _iter317 : struct.properties.entrySet()) {
                    oprot.writeString(_iter317.getKey());
                    oprot.writeString(_iter317.getValue());
                }
            }
        }
        
        @Override
        public void read(final TProtocol prot, final EnvironmentContext struct) throws TException {
            final TTupleProtocol iprot = (TTupleProtocol)prot;
            final BitSet incoming = iprot.readBitSet(1);
            if (incoming.get(0)) {
                final TMap _map318 = new TMap((byte)11, (byte)11, iprot.readI32());
                struct.properties = (Map<String, String>)new HashMap(2 * _map318.size);
                for (int _i319 = 0; _i319 < _map318.size; ++_i319) {
                    final String _key320 = iprot.readString();
                    final String _val321 = iprot.readString();
                    struct.properties.put(_key320, _val321);
                }
                struct.setPropertiesIsSet(true);
            }
        }
    }
}
