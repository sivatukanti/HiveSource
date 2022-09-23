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

public class SerDeInfo implements TBase<SerDeInfo, _Fields>, Serializable, Cloneable
{
    private static final TStruct STRUCT_DESC;
    private static final TField NAME_FIELD_DESC;
    private static final TField SERIALIZATION_LIB_FIELD_DESC;
    private static final TField PARAMETERS_FIELD_DESC;
    private static final Map<Class<? extends IScheme>, SchemeFactory> schemes;
    private String name;
    private String serializationLib;
    private Map<String, String> parameters;
    public static final Map<_Fields, FieldMetaData> metaDataMap;
    
    public SerDeInfo() {
    }
    
    public SerDeInfo(final String name, final String serializationLib, final Map<String, String> parameters) {
        this();
        this.name = name;
        this.serializationLib = serializationLib;
        this.parameters = parameters;
    }
    
    public SerDeInfo(final SerDeInfo other) {
        if (other.isSetName()) {
            this.name = other.name;
        }
        if (other.isSetSerializationLib()) {
            this.serializationLib = other.serializationLib;
        }
        if (other.isSetParameters()) {
            final Map<String, String> __this__parameters = new HashMap<String, String>();
            for (final Map.Entry<String, String> other_element : other.parameters.entrySet()) {
                final String other_element_key = other_element.getKey();
                final String other_element_value = other_element.getValue();
                final String __this__parameters_copy_key = other_element_key;
                final String __this__parameters_copy_value = other_element_value;
                __this__parameters.put(__this__parameters_copy_key, __this__parameters_copy_value);
            }
            this.parameters = __this__parameters;
        }
    }
    
    @Override
    public SerDeInfo deepCopy() {
        return new SerDeInfo(this);
    }
    
    @Override
    public void clear() {
        this.name = null;
        this.serializationLib = null;
        this.parameters = null;
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
    
    public String getSerializationLib() {
        return this.serializationLib;
    }
    
    public void setSerializationLib(final String serializationLib) {
        this.serializationLib = serializationLib;
    }
    
    public void unsetSerializationLib() {
        this.serializationLib = null;
    }
    
    public boolean isSetSerializationLib() {
        return this.serializationLib != null;
    }
    
    public void setSerializationLibIsSet(final boolean value) {
        if (!value) {
            this.serializationLib = null;
        }
    }
    
    public int getParametersSize() {
        return (this.parameters == null) ? 0 : this.parameters.size();
    }
    
    public void putToParameters(final String key, final String val) {
        if (this.parameters == null) {
            this.parameters = new HashMap<String, String>();
        }
        this.parameters.put(key, val);
    }
    
    public Map<String, String> getParameters() {
        return this.parameters;
    }
    
    public void setParameters(final Map<String, String> parameters) {
        this.parameters = parameters;
    }
    
    public void unsetParameters() {
        this.parameters = null;
    }
    
    public boolean isSetParameters() {
        return this.parameters != null;
    }
    
    public void setParametersIsSet(final boolean value) {
        if (!value) {
            this.parameters = null;
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
            case SERIALIZATION_LIB: {
                if (value == null) {
                    this.unsetSerializationLib();
                    break;
                }
                this.setSerializationLib((String)value);
                break;
            }
            case PARAMETERS: {
                if (value == null) {
                    this.unsetParameters();
                    break;
                }
                this.setParameters((Map<String, String>)value);
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
            case SERIALIZATION_LIB: {
                return this.getSerializationLib();
            }
            case PARAMETERS: {
                return this.getParameters();
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
            case SERIALIZATION_LIB: {
                return this.isSetSerializationLib();
            }
            case PARAMETERS: {
                return this.isSetParameters();
            }
            default: {
                throw new IllegalStateException();
            }
        }
    }
    
    @Override
    public boolean equals(final Object that) {
        return that != null && that instanceof SerDeInfo && this.equals((SerDeInfo)that);
    }
    
    public boolean equals(final SerDeInfo that) {
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
        final boolean this_present_serializationLib = this.isSetSerializationLib();
        final boolean that_present_serializationLib = that.isSetSerializationLib();
        if (this_present_serializationLib || that_present_serializationLib) {
            if (!this_present_serializationLib || !that_present_serializationLib) {
                return false;
            }
            if (!this.serializationLib.equals(that.serializationLib)) {
                return false;
            }
        }
        final boolean this_present_parameters = this.isSetParameters();
        final boolean that_present_parameters = that.isSetParameters();
        if (this_present_parameters || that_present_parameters) {
            if (!this_present_parameters || !that_present_parameters) {
                return false;
            }
            if (!this.parameters.equals(that.parameters)) {
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
        final boolean present_serializationLib = this.isSetSerializationLib();
        builder.append(present_serializationLib);
        if (present_serializationLib) {
            builder.append(this.serializationLib);
        }
        final boolean present_parameters = this.isSetParameters();
        builder.append(present_parameters);
        if (present_parameters) {
            builder.append(this.parameters);
        }
        return builder.toHashCode();
    }
    
    @Override
    public int compareTo(final SerDeInfo other) {
        if (!this.getClass().equals(other.getClass())) {
            return this.getClass().getName().compareTo(other.getClass().getName());
        }
        int lastComparison = 0;
        final SerDeInfo typedOther = other;
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
        lastComparison = Boolean.valueOf(this.isSetSerializationLib()).compareTo(Boolean.valueOf(typedOther.isSetSerializationLib()));
        if (lastComparison != 0) {
            return lastComparison;
        }
        if (this.isSetSerializationLib()) {
            lastComparison = TBaseHelper.compareTo(this.serializationLib, typedOther.serializationLib);
            if (lastComparison != 0) {
                return lastComparison;
            }
        }
        lastComparison = Boolean.valueOf(this.isSetParameters()).compareTo(Boolean.valueOf(typedOther.isSetParameters()));
        if (lastComparison != 0) {
            return lastComparison;
        }
        if (this.isSetParameters()) {
            lastComparison = TBaseHelper.compareTo(this.parameters, typedOther.parameters);
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
        SerDeInfo.schemes.get(iprot.getScheme()).getScheme().read(iprot, this);
    }
    
    @Override
    public void write(final TProtocol oprot) throws TException {
        SerDeInfo.schemes.get(oprot.getScheme()).getScheme().write(oprot, this);
    }
    
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("SerDeInfo(");
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
        sb.append("serializationLib:");
        if (this.serializationLib == null) {
            sb.append("null");
        }
        else {
            sb.append(this.serializationLib);
        }
        first = false;
        if (!first) {
            sb.append(", ");
        }
        sb.append("parameters:");
        if (this.parameters == null) {
            sb.append("null");
        }
        else {
            sb.append(this.parameters);
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
        STRUCT_DESC = new TStruct("SerDeInfo");
        NAME_FIELD_DESC = new TField("name", (byte)11, (short)1);
        SERIALIZATION_LIB_FIELD_DESC = new TField("serializationLib", (byte)11, (short)2);
        PARAMETERS_FIELD_DESC = new TField("parameters", (byte)13, (short)3);
        (schemes = new HashMap<Class<? extends IScheme>, SchemeFactory>()).put(StandardScheme.class, new SerDeInfoStandardSchemeFactory());
        SerDeInfo.schemes.put(TupleScheme.class, new SerDeInfoTupleSchemeFactory());
        final Map<_Fields, FieldMetaData> tmpMap = new EnumMap<_Fields, FieldMetaData>(_Fields.class);
        tmpMap.put(_Fields.NAME, new FieldMetaData("name", (byte)3, new FieldValueMetaData((byte)11)));
        tmpMap.put(_Fields.SERIALIZATION_LIB, new FieldMetaData("serializationLib", (byte)3, new FieldValueMetaData((byte)11)));
        tmpMap.put(_Fields.PARAMETERS, new FieldMetaData("parameters", (byte)3, new MapMetaData((byte)13, new FieldValueMetaData((byte)11), new FieldValueMetaData((byte)11))));
        FieldMetaData.addStructMetaDataMap(SerDeInfo.class, metaDataMap = Collections.unmodifiableMap((Map<? extends _Fields, ? extends FieldMetaData>)tmpMap));
    }
    
    public enum _Fields implements TFieldIdEnum
    {
        NAME((short)1, "name"), 
        SERIALIZATION_LIB((short)2, "serializationLib"), 
        PARAMETERS((short)3, "parameters");
        
        private static final Map<String, _Fields> byName;
        private final short _thriftId;
        private final String _fieldName;
        
        public static _Fields findByThriftId(final int fieldId) {
            switch (fieldId) {
                case 1: {
                    return _Fields.NAME;
                }
                case 2: {
                    return _Fields.SERIALIZATION_LIB;
                }
                case 3: {
                    return _Fields.PARAMETERS;
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
    
    private static class SerDeInfoStandardSchemeFactory implements SchemeFactory
    {
        @Override
        public SerDeInfoStandardScheme getScheme() {
            return new SerDeInfoStandardScheme();
        }
    }
    
    private static class SerDeInfoStandardScheme extends StandardScheme<SerDeInfo>
    {
        @Override
        public void read(final TProtocol iprot, final SerDeInfo struct) throws TException {
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
                            struct.serializationLib = iprot.readString();
                            struct.setSerializationLibIsSet(true);
                            break;
                        }
                        TProtocolUtil.skip(iprot, schemeField.type);
                        break;
                    }
                    case 3: {
                        if (schemeField.type == 13) {
                            final TMap _map104 = iprot.readMapBegin();
                            struct.parameters = (Map<String, String>)new HashMap(2 * _map104.size);
                            for (int _i105 = 0; _i105 < _map104.size; ++_i105) {
                                final String _key106 = iprot.readString();
                                final String _val107 = iprot.readString();
                                struct.parameters.put(_key106, _val107);
                            }
                            iprot.readMapEnd();
                            struct.setParametersIsSet(true);
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
        public void write(final TProtocol oprot, final SerDeInfo struct) throws TException {
            struct.validate();
            oprot.writeStructBegin(SerDeInfo.STRUCT_DESC);
            if (struct.name != null) {
                oprot.writeFieldBegin(SerDeInfo.NAME_FIELD_DESC);
                oprot.writeString(struct.name);
                oprot.writeFieldEnd();
            }
            if (struct.serializationLib != null) {
                oprot.writeFieldBegin(SerDeInfo.SERIALIZATION_LIB_FIELD_DESC);
                oprot.writeString(struct.serializationLib);
                oprot.writeFieldEnd();
            }
            if (struct.parameters != null) {
                oprot.writeFieldBegin(SerDeInfo.PARAMETERS_FIELD_DESC);
                oprot.writeMapBegin(new TMap((byte)11, (byte)11, struct.parameters.size()));
                for (final Map.Entry<String, String> _iter108 : struct.parameters.entrySet()) {
                    oprot.writeString(_iter108.getKey());
                    oprot.writeString(_iter108.getValue());
                }
                oprot.writeMapEnd();
                oprot.writeFieldEnd();
            }
            oprot.writeFieldStop();
            oprot.writeStructEnd();
        }
    }
    
    private static class SerDeInfoTupleSchemeFactory implements SchemeFactory
    {
        @Override
        public SerDeInfoTupleScheme getScheme() {
            return new SerDeInfoTupleScheme();
        }
    }
    
    private static class SerDeInfoTupleScheme extends TupleScheme<SerDeInfo>
    {
        @Override
        public void write(final TProtocol prot, final SerDeInfo struct) throws TException {
            final TTupleProtocol oprot = (TTupleProtocol)prot;
            final BitSet optionals = new BitSet();
            if (struct.isSetName()) {
                optionals.set(0);
            }
            if (struct.isSetSerializationLib()) {
                optionals.set(1);
            }
            if (struct.isSetParameters()) {
                optionals.set(2);
            }
            oprot.writeBitSet(optionals, 3);
            if (struct.isSetName()) {
                oprot.writeString(struct.name);
            }
            if (struct.isSetSerializationLib()) {
                oprot.writeString(struct.serializationLib);
            }
            if (struct.isSetParameters()) {
                oprot.writeI32(struct.parameters.size());
                for (final Map.Entry<String, String> _iter109 : struct.parameters.entrySet()) {
                    oprot.writeString(_iter109.getKey());
                    oprot.writeString(_iter109.getValue());
                }
            }
        }
        
        @Override
        public void read(final TProtocol prot, final SerDeInfo struct) throws TException {
            final TTupleProtocol iprot = (TTupleProtocol)prot;
            final BitSet incoming = iprot.readBitSet(3);
            if (incoming.get(0)) {
                struct.name = iprot.readString();
                struct.setNameIsSet(true);
            }
            if (incoming.get(1)) {
                struct.serializationLib = iprot.readString();
                struct.setSerializationLibIsSet(true);
            }
            if (incoming.get(2)) {
                final TMap _map110 = new TMap((byte)11, (byte)11, iprot.readI32());
                struct.parameters = (Map<String, String>)new HashMap(2 * _map110.size);
                for (int _i111 = 0; _i111 < _map110.size; ++_i111) {
                    final String _key112 = iprot.readString();
                    final String _val113 = iprot.readString();
                    struct.parameters.put(_key112, _val113);
                }
                struct.setParametersIsSet(true);
            }
        }
    }
}
