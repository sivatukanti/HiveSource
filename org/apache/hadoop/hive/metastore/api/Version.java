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

public class Version implements TBase<Version, _Fields>, Serializable, Cloneable
{
    private static final TStruct STRUCT_DESC;
    private static final TField VERSION_FIELD_DESC;
    private static final TField COMMENTS_FIELD_DESC;
    private static final Map<Class<? extends IScheme>, SchemeFactory> schemes;
    private String version;
    private String comments;
    public static final Map<_Fields, FieldMetaData> metaDataMap;
    
    public Version() {
    }
    
    public Version(final String version, final String comments) {
        this();
        this.version = version;
        this.comments = comments;
    }
    
    public Version(final Version other) {
        if (other.isSetVersion()) {
            this.version = other.version;
        }
        if (other.isSetComments()) {
            this.comments = other.comments;
        }
    }
    
    @Override
    public Version deepCopy() {
        return new Version(this);
    }
    
    @Override
    public void clear() {
        this.version = null;
        this.comments = null;
    }
    
    public String getVersion() {
        return this.version;
    }
    
    public void setVersion(final String version) {
        this.version = version;
    }
    
    public void unsetVersion() {
        this.version = null;
    }
    
    public boolean isSetVersion() {
        return this.version != null;
    }
    
    public void setVersionIsSet(final boolean value) {
        if (!value) {
            this.version = null;
        }
    }
    
    public String getComments() {
        return this.comments;
    }
    
    public void setComments(final String comments) {
        this.comments = comments;
    }
    
    public void unsetComments() {
        this.comments = null;
    }
    
    public boolean isSetComments() {
        return this.comments != null;
    }
    
    public void setCommentsIsSet(final boolean value) {
        if (!value) {
            this.comments = null;
        }
    }
    
    @Override
    public void setFieldValue(final _Fields field, final Object value) {
        switch (field) {
            case VERSION: {
                if (value == null) {
                    this.unsetVersion();
                    break;
                }
                this.setVersion((String)value);
                break;
            }
            case COMMENTS: {
                if (value == null) {
                    this.unsetComments();
                    break;
                }
                this.setComments((String)value);
                break;
            }
        }
    }
    
    @Override
    public Object getFieldValue(final _Fields field) {
        switch (field) {
            case VERSION: {
                return this.getVersion();
            }
            case COMMENTS: {
                return this.getComments();
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
            case VERSION: {
                return this.isSetVersion();
            }
            case COMMENTS: {
                return this.isSetComments();
            }
            default: {
                throw new IllegalStateException();
            }
        }
    }
    
    @Override
    public boolean equals(final Object that) {
        return that != null && that instanceof Version && this.equals((Version)that);
    }
    
    public boolean equals(final Version that) {
        if (that == null) {
            return false;
        }
        final boolean this_present_version = this.isSetVersion();
        final boolean that_present_version = that.isSetVersion();
        if (this_present_version || that_present_version) {
            if (!this_present_version || !that_present_version) {
                return false;
            }
            if (!this.version.equals(that.version)) {
                return false;
            }
        }
        final boolean this_present_comments = this.isSetComments();
        final boolean that_present_comments = that.isSetComments();
        if (this_present_comments || that_present_comments) {
            if (!this_present_comments || !that_present_comments) {
                return false;
            }
            if (!this.comments.equals(that.comments)) {
                return false;
            }
        }
        return true;
    }
    
    @Override
    public int hashCode() {
        final HashCodeBuilder builder = new HashCodeBuilder();
        final boolean present_version = this.isSetVersion();
        builder.append(present_version);
        if (present_version) {
            builder.append(this.version);
        }
        final boolean present_comments = this.isSetComments();
        builder.append(present_comments);
        if (present_comments) {
            builder.append(this.comments);
        }
        return builder.toHashCode();
    }
    
    @Override
    public int compareTo(final Version other) {
        if (!this.getClass().equals(other.getClass())) {
            return this.getClass().getName().compareTo(other.getClass().getName());
        }
        int lastComparison = 0;
        final Version typedOther = other;
        lastComparison = Boolean.valueOf(this.isSetVersion()).compareTo(Boolean.valueOf(typedOther.isSetVersion()));
        if (lastComparison != 0) {
            return lastComparison;
        }
        if (this.isSetVersion()) {
            lastComparison = TBaseHelper.compareTo(this.version, typedOther.version);
            if (lastComparison != 0) {
                return lastComparison;
            }
        }
        lastComparison = Boolean.valueOf(this.isSetComments()).compareTo(Boolean.valueOf(typedOther.isSetComments()));
        if (lastComparison != 0) {
            return lastComparison;
        }
        if (this.isSetComments()) {
            lastComparison = TBaseHelper.compareTo(this.comments, typedOther.comments);
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
        Version.schemes.get(iprot.getScheme()).getScheme().read(iprot, this);
    }
    
    @Override
    public void write(final TProtocol oprot) throws TException {
        Version.schemes.get(oprot.getScheme()).getScheme().write(oprot, this);
    }
    
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("Version(");
        boolean first = true;
        sb.append("version:");
        if (this.version == null) {
            sb.append("null");
        }
        else {
            sb.append(this.version);
        }
        first = false;
        if (!first) {
            sb.append(", ");
        }
        sb.append("comments:");
        if (this.comments == null) {
            sb.append("null");
        }
        else {
            sb.append(this.comments);
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
        STRUCT_DESC = new TStruct("Version");
        VERSION_FIELD_DESC = new TField("version", (byte)11, (short)1);
        COMMENTS_FIELD_DESC = new TField("comments", (byte)11, (short)2);
        (schemes = new HashMap<Class<? extends IScheme>, SchemeFactory>()).put(StandardScheme.class, new VersionStandardSchemeFactory());
        Version.schemes.put(TupleScheme.class, new VersionTupleSchemeFactory());
        final Map<_Fields, FieldMetaData> tmpMap = new EnumMap<_Fields, FieldMetaData>(_Fields.class);
        tmpMap.put(_Fields.VERSION, new FieldMetaData("version", (byte)3, new FieldValueMetaData((byte)11)));
        tmpMap.put(_Fields.COMMENTS, new FieldMetaData("comments", (byte)3, new FieldValueMetaData((byte)11)));
        FieldMetaData.addStructMetaDataMap(Version.class, metaDataMap = Collections.unmodifiableMap((Map<? extends _Fields, ? extends FieldMetaData>)tmpMap));
    }
    
    public enum _Fields implements TFieldIdEnum
    {
        VERSION((short)1, "version"), 
        COMMENTS((short)2, "comments");
        
        private static final Map<String, _Fields> byName;
        private final short _thriftId;
        private final String _fieldName;
        
        public static _Fields findByThriftId(final int fieldId) {
            switch (fieldId) {
                case 1: {
                    return _Fields.VERSION;
                }
                case 2: {
                    return _Fields.COMMENTS;
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
    
    private static class VersionStandardSchemeFactory implements SchemeFactory
    {
        @Override
        public VersionStandardScheme getScheme() {
            return new VersionStandardScheme();
        }
    }
    
    private static class VersionStandardScheme extends StandardScheme<Version>
    {
        @Override
        public void read(final TProtocol iprot, final Version struct) throws TException {
            iprot.readStructBegin();
            while (true) {
                final TField schemeField = iprot.readFieldBegin();
                if (schemeField.type == 0) {
                    break;
                }
                switch (schemeField.id) {
                    case 1: {
                        if (schemeField.type == 11) {
                            struct.version = iprot.readString();
                            struct.setVersionIsSet(true);
                            break;
                        }
                        TProtocolUtil.skip(iprot, schemeField.type);
                        break;
                    }
                    case 2: {
                        if (schemeField.type == 11) {
                            struct.comments = iprot.readString();
                            struct.setCommentsIsSet(true);
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
        public void write(final TProtocol oprot, final Version struct) throws TException {
            struct.validate();
            oprot.writeStructBegin(Version.STRUCT_DESC);
            if (struct.version != null) {
                oprot.writeFieldBegin(Version.VERSION_FIELD_DESC);
                oprot.writeString(struct.version);
                oprot.writeFieldEnd();
            }
            if (struct.comments != null) {
                oprot.writeFieldBegin(Version.COMMENTS_FIELD_DESC);
                oprot.writeString(struct.comments);
                oprot.writeFieldEnd();
            }
            oprot.writeFieldStop();
            oprot.writeStructEnd();
        }
    }
    
    private static class VersionTupleSchemeFactory implements SchemeFactory
    {
        @Override
        public VersionTupleScheme getScheme() {
            return new VersionTupleScheme();
        }
    }
    
    private static class VersionTupleScheme extends TupleScheme<Version>
    {
        @Override
        public void write(final TProtocol prot, final Version struct) throws TException {
            final TTupleProtocol oprot = (TTupleProtocol)prot;
            final BitSet optionals = new BitSet();
            if (struct.isSetVersion()) {
                optionals.set(0);
            }
            if (struct.isSetComments()) {
                optionals.set(1);
            }
            oprot.writeBitSet(optionals, 2);
            if (struct.isSetVersion()) {
                oprot.writeString(struct.version);
            }
            if (struct.isSetComments()) {
                oprot.writeString(struct.comments);
            }
        }
        
        @Override
        public void read(final TProtocol prot, final Version struct) throws TException {
            final TTupleProtocol iprot = (TTupleProtocol)prot;
            final BitSet incoming = iprot.readBitSet(2);
            if (incoming.get(0)) {
                struct.version = iprot.readString();
                struct.setVersionIsSet(true);
            }
            if (incoming.get(1)) {
                struct.comments = iprot.readString();
                struct.setCommentsIsSet(true);
            }
        }
    }
}
