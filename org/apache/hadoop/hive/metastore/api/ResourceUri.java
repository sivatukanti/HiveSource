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
import org.apache.thrift.TEnum;
import org.apache.thrift.meta_data.EnumMetaData;
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

public class ResourceUri implements TBase<ResourceUri, _Fields>, Serializable, Cloneable
{
    private static final TStruct STRUCT_DESC;
    private static final TField RESOURCE_TYPE_FIELD_DESC;
    private static final TField URI_FIELD_DESC;
    private static final Map<Class<? extends IScheme>, SchemeFactory> schemes;
    private ResourceType resourceType;
    private String uri;
    public static final Map<_Fields, FieldMetaData> metaDataMap;
    
    public ResourceUri() {
    }
    
    public ResourceUri(final ResourceType resourceType, final String uri) {
        this();
        this.resourceType = resourceType;
        this.uri = uri;
    }
    
    public ResourceUri(final ResourceUri other) {
        if (other.isSetResourceType()) {
            this.resourceType = other.resourceType;
        }
        if (other.isSetUri()) {
            this.uri = other.uri;
        }
    }
    
    @Override
    public ResourceUri deepCopy() {
        return new ResourceUri(this);
    }
    
    @Override
    public void clear() {
        this.resourceType = null;
        this.uri = null;
    }
    
    public ResourceType getResourceType() {
        return this.resourceType;
    }
    
    public void setResourceType(final ResourceType resourceType) {
        this.resourceType = resourceType;
    }
    
    public void unsetResourceType() {
        this.resourceType = null;
    }
    
    public boolean isSetResourceType() {
        return this.resourceType != null;
    }
    
    public void setResourceTypeIsSet(final boolean value) {
        if (!value) {
            this.resourceType = null;
        }
    }
    
    public String getUri() {
        return this.uri;
    }
    
    public void setUri(final String uri) {
        this.uri = uri;
    }
    
    public void unsetUri() {
        this.uri = null;
    }
    
    public boolean isSetUri() {
        return this.uri != null;
    }
    
    public void setUriIsSet(final boolean value) {
        if (!value) {
            this.uri = null;
        }
    }
    
    @Override
    public void setFieldValue(final _Fields field, final Object value) {
        switch (field) {
            case RESOURCE_TYPE: {
                if (value == null) {
                    this.unsetResourceType();
                    break;
                }
                this.setResourceType((ResourceType)value);
                break;
            }
            case URI: {
                if (value == null) {
                    this.unsetUri();
                    break;
                }
                this.setUri((String)value);
                break;
            }
        }
    }
    
    @Override
    public Object getFieldValue(final _Fields field) {
        switch (field) {
            case RESOURCE_TYPE: {
                return this.getResourceType();
            }
            case URI: {
                return this.getUri();
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
            case RESOURCE_TYPE: {
                return this.isSetResourceType();
            }
            case URI: {
                return this.isSetUri();
            }
            default: {
                throw new IllegalStateException();
            }
        }
    }
    
    @Override
    public boolean equals(final Object that) {
        return that != null && that instanceof ResourceUri && this.equals((ResourceUri)that);
    }
    
    public boolean equals(final ResourceUri that) {
        if (that == null) {
            return false;
        }
        final boolean this_present_resourceType = this.isSetResourceType();
        final boolean that_present_resourceType = that.isSetResourceType();
        if (this_present_resourceType || that_present_resourceType) {
            if (!this_present_resourceType || !that_present_resourceType) {
                return false;
            }
            if (!this.resourceType.equals(that.resourceType)) {
                return false;
            }
        }
        final boolean this_present_uri = this.isSetUri();
        final boolean that_present_uri = that.isSetUri();
        if (this_present_uri || that_present_uri) {
            if (!this_present_uri || !that_present_uri) {
                return false;
            }
            if (!this.uri.equals(that.uri)) {
                return false;
            }
        }
        return true;
    }
    
    @Override
    public int hashCode() {
        final HashCodeBuilder builder = new HashCodeBuilder();
        final boolean present_resourceType = this.isSetResourceType();
        builder.append(present_resourceType);
        if (present_resourceType) {
            builder.append(this.resourceType.getValue());
        }
        final boolean present_uri = this.isSetUri();
        builder.append(present_uri);
        if (present_uri) {
            builder.append(this.uri);
        }
        return builder.toHashCode();
    }
    
    @Override
    public int compareTo(final ResourceUri other) {
        if (!this.getClass().equals(other.getClass())) {
            return this.getClass().getName().compareTo(other.getClass().getName());
        }
        int lastComparison = 0;
        final ResourceUri typedOther = other;
        lastComparison = Boolean.valueOf(this.isSetResourceType()).compareTo(Boolean.valueOf(typedOther.isSetResourceType()));
        if (lastComparison != 0) {
            return lastComparison;
        }
        if (this.isSetResourceType()) {
            lastComparison = TBaseHelper.compareTo(this.resourceType, typedOther.resourceType);
            if (lastComparison != 0) {
                return lastComparison;
            }
        }
        lastComparison = Boolean.valueOf(this.isSetUri()).compareTo(Boolean.valueOf(typedOther.isSetUri()));
        if (lastComparison != 0) {
            return lastComparison;
        }
        if (this.isSetUri()) {
            lastComparison = TBaseHelper.compareTo(this.uri, typedOther.uri);
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
        ResourceUri.schemes.get(iprot.getScheme()).getScheme().read(iprot, this);
    }
    
    @Override
    public void write(final TProtocol oprot) throws TException {
        ResourceUri.schemes.get(oprot.getScheme()).getScheme().write(oprot, this);
    }
    
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("ResourceUri(");
        boolean first = true;
        sb.append("resourceType:");
        if (this.resourceType == null) {
            sb.append("null");
        }
        else {
            sb.append(this.resourceType);
        }
        first = false;
        if (!first) {
            sb.append(", ");
        }
        sb.append("uri:");
        if (this.uri == null) {
            sb.append("null");
        }
        else {
            sb.append(this.uri);
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
        STRUCT_DESC = new TStruct("ResourceUri");
        RESOURCE_TYPE_FIELD_DESC = new TField("resourceType", (byte)8, (short)1);
        URI_FIELD_DESC = new TField("uri", (byte)11, (short)2);
        (schemes = new HashMap<Class<? extends IScheme>, SchemeFactory>()).put(StandardScheme.class, new ResourceUriStandardSchemeFactory());
        ResourceUri.schemes.put(TupleScheme.class, new ResourceUriTupleSchemeFactory());
        final Map<_Fields, FieldMetaData> tmpMap = new EnumMap<_Fields, FieldMetaData>(_Fields.class);
        tmpMap.put(_Fields.RESOURCE_TYPE, new FieldMetaData("resourceType", (byte)3, new EnumMetaData((byte)16, ResourceType.class)));
        tmpMap.put(_Fields.URI, new FieldMetaData("uri", (byte)3, new FieldValueMetaData((byte)11)));
        FieldMetaData.addStructMetaDataMap(ResourceUri.class, metaDataMap = Collections.unmodifiableMap((Map<? extends _Fields, ? extends FieldMetaData>)tmpMap));
    }
    
    public enum _Fields implements TFieldIdEnum
    {
        RESOURCE_TYPE((short)1, "resourceType"), 
        URI((short)2, "uri");
        
        private static final Map<String, _Fields> byName;
        private final short _thriftId;
        private final String _fieldName;
        
        public static _Fields findByThriftId(final int fieldId) {
            switch (fieldId) {
                case 1: {
                    return _Fields.RESOURCE_TYPE;
                }
                case 2: {
                    return _Fields.URI;
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
    
    private static class ResourceUriStandardSchemeFactory implements SchemeFactory
    {
        @Override
        public ResourceUriStandardScheme getScheme() {
            return new ResourceUriStandardScheme();
        }
    }
    
    private static class ResourceUriStandardScheme extends StandardScheme<ResourceUri>
    {
        @Override
        public void read(final TProtocol iprot, final ResourceUri struct) throws TException {
            iprot.readStructBegin();
            while (true) {
                final TField schemeField = iprot.readFieldBegin();
                if (schemeField.type == 0) {
                    break;
                }
                switch (schemeField.id) {
                    case 1: {
                        if (schemeField.type == 8) {
                            struct.resourceType = ResourceType.findByValue(iprot.readI32());
                            struct.setResourceTypeIsSet(true);
                            break;
                        }
                        TProtocolUtil.skip(iprot, schemeField.type);
                        break;
                    }
                    case 2: {
                        if (schemeField.type == 11) {
                            struct.uri = iprot.readString();
                            struct.setUriIsSet(true);
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
        public void write(final TProtocol oprot, final ResourceUri struct) throws TException {
            struct.validate();
            oprot.writeStructBegin(ResourceUri.STRUCT_DESC);
            if (struct.resourceType != null) {
                oprot.writeFieldBegin(ResourceUri.RESOURCE_TYPE_FIELD_DESC);
                oprot.writeI32(struct.resourceType.getValue());
                oprot.writeFieldEnd();
            }
            if (struct.uri != null) {
                oprot.writeFieldBegin(ResourceUri.URI_FIELD_DESC);
                oprot.writeString(struct.uri);
                oprot.writeFieldEnd();
            }
            oprot.writeFieldStop();
            oprot.writeStructEnd();
        }
    }
    
    private static class ResourceUriTupleSchemeFactory implements SchemeFactory
    {
        @Override
        public ResourceUriTupleScheme getScheme() {
            return new ResourceUriTupleScheme();
        }
    }
    
    private static class ResourceUriTupleScheme extends TupleScheme<ResourceUri>
    {
        @Override
        public void write(final TProtocol prot, final ResourceUri struct) throws TException {
            final TTupleProtocol oprot = (TTupleProtocol)prot;
            final BitSet optionals = new BitSet();
            if (struct.isSetResourceType()) {
                optionals.set(0);
            }
            if (struct.isSetUri()) {
                optionals.set(1);
            }
            oprot.writeBitSet(optionals, 2);
            if (struct.isSetResourceType()) {
                oprot.writeI32(struct.resourceType.getValue());
            }
            if (struct.isSetUri()) {
                oprot.writeString(struct.uri);
            }
        }
        
        @Override
        public void read(final TProtocol prot, final ResourceUri struct) throws TException {
            final TTupleProtocol iprot = (TTupleProtocol)prot;
            final BitSet incoming = iprot.readBitSet(2);
            if (incoming.get(0)) {
                struct.resourceType = ResourceType.findByValue(iprot.readI32());
                struct.setResourceTypeIsSet(true);
            }
            if (incoming.get(1)) {
                struct.uri = iprot.readString();
                struct.setUriIsSet(true);
            }
        }
    }
}
