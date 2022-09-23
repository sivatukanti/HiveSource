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
import org.apache.thrift.TEnum;
import org.apache.thrift.meta_data.EnumMetaData;
import org.apache.thrift.meta_data.StructMetaData;
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

public class Database implements TBase<Database, _Fields>, Serializable, Cloneable
{
    private static final TStruct STRUCT_DESC;
    private static final TField NAME_FIELD_DESC;
    private static final TField DESCRIPTION_FIELD_DESC;
    private static final TField LOCATION_URI_FIELD_DESC;
    private static final TField PARAMETERS_FIELD_DESC;
    private static final TField PRIVILEGES_FIELD_DESC;
    private static final TField OWNER_NAME_FIELD_DESC;
    private static final TField OWNER_TYPE_FIELD_DESC;
    private static final Map<Class<? extends IScheme>, SchemeFactory> schemes;
    private String name;
    private String description;
    private String locationUri;
    private Map<String, String> parameters;
    private PrincipalPrivilegeSet privileges;
    private String ownerName;
    private PrincipalType ownerType;
    private _Fields[] optionals;
    public static final Map<_Fields, FieldMetaData> metaDataMap;
    
    public Database() {
        this.optionals = new _Fields[] { _Fields.PRIVILEGES, _Fields.OWNER_NAME, _Fields.OWNER_TYPE };
    }
    
    public Database(final String name, final String description, final String locationUri, final Map<String, String> parameters) {
        this();
        this.name = name;
        this.description = description;
        this.locationUri = locationUri;
        this.parameters = parameters;
    }
    
    public Database(final Database other) {
        this.optionals = new _Fields[] { _Fields.PRIVILEGES, _Fields.OWNER_NAME, _Fields.OWNER_TYPE };
        if (other.isSetName()) {
            this.name = other.name;
        }
        if (other.isSetDescription()) {
            this.description = other.description;
        }
        if (other.isSetLocationUri()) {
            this.locationUri = other.locationUri;
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
        if (other.isSetPrivileges()) {
            this.privileges = new PrincipalPrivilegeSet(other.privileges);
        }
        if (other.isSetOwnerName()) {
            this.ownerName = other.ownerName;
        }
        if (other.isSetOwnerType()) {
            this.ownerType = other.ownerType;
        }
    }
    
    @Override
    public Database deepCopy() {
        return new Database(this);
    }
    
    @Override
    public void clear() {
        this.name = null;
        this.description = null;
        this.locationUri = null;
        this.parameters = null;
        this.privileges = null;
        this.ownerName = null;
        this.ownerType = null;
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
    
    public String getDescription() {
        return this.description;
    }
    
    public void setDescription(final String description) {
        this.description = description;
    }
    
    public void unsetDescription() {
        this.description = null;
    }
    
    public boolean isSetDescription() {
        return this.description != null;
    }
    
    public void setDescriptionIsSet(final boolean value) {
        if (!value) {
            this.description = null;
        }
    }
    
    public String getLocationUri() {
        return this.locationUri;
    }
    
    public void setLocationUri(final String locationUri) {
        this.locationUri = locationUri;
    }
    
    public void unsetLocationUri() {
        this.locationUri = null;
    }
    
    public boolean isSetLocationUri() {
        return this.locationUri != null;
    }
    
    public void setLocationUriIsSet(final boolean value) {
        if (!value) {
            this.locationUri = null;
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
    
    public PrincipalPrivilegeSet getPrivileges() {
        return this.privileges;
    }
    
    public void setPrivileges(final PrincipalPrivilegeSet privileges) {
        this.privileges = privileges;
    }
    
    public void unsetPrivileges() {
        this.privileges = null;
    }
    
    public boolean isSetPrivileges() {
        return this.privileges != null;
    }
    
    public void setPrivilegesIsSet(final boolean value) {
        if (!value) {
            this.privileges = null;
        }
    }
    
    public String getOwnerName() {
        return this.ownerName;
    }
    
    public void setOwnerName(final String ownerName) {
        this.ownerName = ownerName;
    }
    
    public void unsetOwnerName() {
        this.ownerName = null;
    }
    
    public boolean isSetOwnerName() {
        return this.ownerName != null;
    }
    
    public void setOwnerNameIsSet(final boolean value) {
        if (!value) {
            this.ownerName = null;
        }
    }
    
    public PrincipalType getOwnerType() {
        return this.ownerType;
    }
    
    public void setOwnerType(final PrincipalType ownerType) {
        this.ownerType = ownerType;
    }
    
    public void unsetOwnerType() {
        this.ownerType = null;
    }
    
    public boolean isSetOwnerType() {
        return this.ownerType != null;
    }
    
    public void setOwnerTypeIsSet(final boolean value) {
        if (!value) {
            this.ownerType = null;
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
            case DESCRIPTION: {
                if (value == null) {
                    this.unsetDescription();
                    break;
                }
                this.setDescription((String)value);
                break;
            }
            case LOCATION_URI: {
                if (value == null) {
                    this.unsetLocationUri();
                    break;
                }
                this.setLocationUri((String)value);
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
            case PRIVILEGES: {
                if (value == null) {
                    this.unsetPrivileges();
                    break;
                }
                this.setPrivileges((PrincipalPrivilegeSet)value);
                break;
            }
            case OWNER_NAME: {
                if (value == null) {
                    this.unsetOwnerName();
                    break;
                }
                this.setOwnerName((String)value);
                break;
            }
            case OWNER_TYPE: {
                if (value == null) {
                    this.unsetOwnerType();
                    break;
                }
                this.setOwnerType((PrincipalType)value);
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
            case DESCRIPTION: {
                return this.getDescription();
            }
            case LOCATION_URI: {
                return this.getLocationUri();
            }
            case PARAMETERS: {
                return this.getParameters();
            }
            case PRIVILEGES: {
                return this.getPrivileges();
            }
            case OWNER_NAME: {
                return this.getOwnerName();
            }
            case OWNER_TYPE: {
                return this.getOwnerType();
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
            case DESCRIPTION: {
                return this.isSetDescription();
            }
            case LOCATION_URI: {
                return this.isSetLocationUri();
            }
            case PARAMETERS: {
                return this.isSetParameters();
            }
            case PRIVILEGES: {
                return this.isSetPrivileges();
            }
            case OWNER_NAME: {
                return this.isSetOwnerName();
            }
            case OWNER_TYPE: {
                return this.isSetOwnerType();
            }
            default: {
                throw new IllegalStateException();
            }
        }
    }
    
    @Override
    public boolean equals(final Object that) {
        return that != null && that instanceof Database && this.equals((Database)that);
    }
    
    public boolean equals(final Database that) {
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
        final boolean this_present_description = this.isSetDescription();
        final boolean that_present_description = that.isSetDescription();
        if (this_present_description || that_present_description) {
            if (!this_present_description || !that_present_description) {
                return false;
            }
            if (!this.description.equals(that.description)) {
                return false;
            }
        }
        final boolean this_present_locationUri = this.isSetLocationUri();
        final boolean that_present_locationUri = that.isSetLocationUri();
        if (this_present_locationUri || that_present_locationUri) {
            if (!this_present_locationUri || !that_present_locationUri) {
                return false;
            }
            if (!this.locationUri.equals(that.locationUri)) {
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
        final boolean this_present_privileges = this.isSetPrivileges();
        final boolean that_present_privileges = that.isSetPrivileges();
        if (this_present_privileges || that_present_privileges) {
            if (!this_present_privileges || !that_present_privileges) {
                return false;
            }
            if (!this.privileges.equals(that.privileges)) {
                return false;
            }
        }
        final boolean this_present_ownerName = this.isSetOwnerName();
        final boolean that_present_ownerName = that.isSetOwnerName();
        if (this_present_ownerName || that_present_ownerName) {
            if (!this_present_ownerName || !that_present_ownerName) {
                return false;
            }
            if (!this.ownerName.equals(that.ownerName)) {
                return false;
            }
        }
        final boolean this_present_ownerType = this.isSetOwnerType();
        final boolean that_present_ownerType = that.isSetOwnerType();
        if (this_present_ownerType || that_present_ownerType) {
            if (!this_present_ownerType || !that_present_ownerType) {
                return false;
            }
            if (!this.ownerType.equals(that.ownerType)) {
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
        final boolean present_description = this.isSetDescription();
        builder.append(present_description);
        if (present_description) {
            builder.append(this.description);
        }
        final boolean present_locationUri = this.isSetLocationUri();
        builder.append(present_locationUri);
        if (present_locationUri) {
            builder.append(this.locationUri);
        }
        final boolean present_parameters = this.isSetParameters();
        builder.append(present_parameters);
        if (present_parameters) {
            builder.append(this.parameters);
        }
        final boolean present_privileges = this.isSetPrivileges();
        builder.append(present_privileges);
        if (present_privileges) {
            builder.append(this.privileges);
        }
        final boolean present_ownerName = this.isSetOwnerName();
        builder.append(present_ownerName);
        if (present_ownerName) {
            builder.append(this.ownerName);
        }
        final boolean present_ownerType = this.isSetOwnerType();
        builder.append(present_ownerType);
        if (present_ownerType) {
            builder.append(this.ownerType.getValue());
        }
        return builder.toHashCode();
    }
    
    @Override
    public int compareTo(final Database other) {
        if (!this.getClass().equals(other.getClass())) {
            return this.getClass().getName().compareTo(other.getClass().getName());
        }
        int lastComparison = 0;
        final Database typedOther = other;
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
        lastComparison = Boolean.valueOf(this.isSetDescription()).compareTo(Boolean.valueOf(typedOther.isSetDescription()));
        if (lastComparison != 0) {
            return lastComparison;
        }
        if (this.isSetDescription()) {
            lastComparison = TBaseHelper.compareTo(this.description, typedOther.description);
            if (lastComparison != 0) {
                return lastComparison;
            }
        }
        lastComparison = Boolean.valueOf(this.isSetLocationUri()).compareTo(Boolean.valueOf(typedOther.isSetLocationUri()));
        if (lastComparison != 0) {
            return lastComparison;
        }
        if (this.isSetLocationUri()) {
            lastComparison = TBaseHelper.compareTo(this.locationUri, typedOther.locationUri);
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
        lastComparison = Boolean.valueOf(this.isSetPrivileges()).compareTo(Boolean.valueOf(typedOther.isSetPrivileges()));
        if (lastComparison != 0) {
            return lastComparison;
        }
        if (this.isSetPrivileges()) {
            lastComparison = TBaseHelper.compareTo(this.privileges, typedOther.privileges);
            if (lastComparison != 0) {
                return lastComparison;
            }
        }
        lastComparison = Boolean.valueOf(this.isSetOwnerName()).compareTo(Boolean.valueOf(typedOther.isSetOwnerName()));
        if (lastComparison != 0) {
            return lastComparison;
        }
        if (this.isSetOwnerName()) {
            lastComparison = TBaseHelper.compareTo(this.ownerName, typedOther.ownerName);
            if (lastComparison != 0) {
                return lastComparison;
            }
        }
        lastComparison = Boolean.valueOf(this.isSetOwnerType()).compareTo(Boolean.valueOf(typedOther.isSetOwnerType()));
        if (lastComparison != 0) {
            return lastComparison;
        }
        if (this.isSetOwnerType()) {
            lastComparison = TBaseHelper.compareTo(this.ownerType, typedOther.ownerType);
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
        Database.schemes.get(iprot.getScheme()).getScheme().read(iprot, this);
    }
    
    @Override
    public void write(final TProtocol oprot) throws TException {
        Database.schemes.get(oprot.getScheme()).getScheme().write(oprot, this);
    }
    
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("Database(");
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
        sb.append("description:");
        if (this.description == null) {
            sb.append("null");
        }
        else {
            sb.append(this.description);
        }
        first = false;
        if (!first) {
            sb.append(", ");
        }
        sb.append("locationUri:");
        if (this.locationUri == null) {
            sb.append("null");
        }
        else {
            sb.append(this.locationUri);
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
        if (this.isSetPrivileges()) {
            if (!first) {
                sb.append(", ");
            }
            sb.append("privileges:");
            if (this.privileges == null) {
                sb.append("null");
            }
            else {
                sb.append(this.privileges);
            }
            first = false;
        }
        if (this.isSetOwnerName()) {
            if (!first) {
                sb.append(", ");
            }
            sb.append("ownerName:");
            if (this.ownerName == null) {
                sb.append("null");
            }
            else {
                sb.append(this.ownerName);
            }
            first = false;
        }
        if (this.isSetOwnerType()) {
            if (!first) {
                sb.append(", ");
            }
            sb.append("ownerType:");
            if (this.ownerType == null) {
                sb.append("null");
            }
            else {
                sb.append(this.ownerType);
            }
            first = false;
        }
        sb.append(")");
        return sb.toString();
    }
    
    public void validate() throws TException {
        if (this.privileges != null) {
            this.privileges.validate();
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
            this.read(new TCompactProtocol(new TIOStreamTransport(in)));
        }
        catch (TException te) {
            throw new IOException(te);
        }
    }
    
    static {
        STRUCT_DESC = new TStruct("Database");
        NAME_FIELD_DESC = new TField("name", (byte)11, (short)1);
        DESCRIPTION_FIELD_DESC = new TField("description", (byte)11, (short)2);
        LOCATION_URI_FIELD_DESC = new TField("locationUri", (byte)11, (short)3);
        PARAMETERS_FIELD_DESC = new TField("parameters", (byte)13, (short)4);
        PRIVILEGES_FIELD_DESC = new TField("privileges", (byte)12, (short)5);
        OWNER_NAME_FIELD_DESC = new TField("ownerName", (byte)11, (short)6);
        OWNER_TYPE_FIELD_DESC = new TField("ownerType", (byte)8, (short)7);
        (schemes = new HashMap<Class<? extends IScheme>, SchemeFactory>()).put(StandardScheme.class, new DatabaseStandardSchemeFactory());
        Database.schemes.put(TupleScheme.class, new DatabaseTupleSchemeFactory());
        final Map<_Fields, FieldMetaData> tmpMap = new EnumMap<_Fields, FieldMetaData>(_Fields.class);
        tmpMap.put(_Fields.NAME, new FieldMetaData("name", (byte)3, new FieldValueMetaData((byte)11)));
        tmpMap.put(_Fields.DESCRIPTION, new FieldMetaData("description", (byte)3, new FieldValueMetaData((byte)11)));
        tmpMap.put(_Fields.LOCATION_URI, new FieldMetaData("locationUri", (byte)3, new FieldValueMetaData((byte)11)));
        tmpMap.put(_Fields.PARAMETERS, new FieldMetaData("parameters", (byte)3, new MapMetaData((byte)13, new FieldValueMetaData((byte)11), new FieldValueMetaData((byte)11))));
        tmpMap.put(_Fields.PRIVILEGES, new FieldMetaData("privileges", (byte)2, new StructMetaData((byte)12, PrincipalPrivilegeSet.class)));
        tmpMap.put(_Fields.OWNER_NAME, new FieldMetaData("ownerName", (byte)2, new FieldValueMetaData((byte)11)));
        tmpMap.put(_Fields.OWNER_TYPE, new FieldMetaData("ownerType", (byte)2, new EnumMetaData((byte)16, PrincipalType.class)));
        FieldMetaData.addStructMetaDataMap(Database.class, metaDataMap = Collections.unmodifiableMap((Map<? extends _Fields, ? extends FieldMetaData>)tmpMap));
    }
    
    public enum _Fields implements TFieldIdEnum
    {
        NAME((short)1, "name"), 
        DESCRIPTION((short)2, "description"), 
        LOCATION_URI((short)3, "locationUri"), 
        PARAMETERS((short)4, "parameters"), 
        PRIVILEGES((short)5, "privileges"), 
        OWNER_NAME((short)6, "ownerName"), 
        OWNER_TYPE((short)7, "ownerType");
        
        private static final Map<String, _Fields> byName;
        private final short _thriftId;
        private final String _fieldName;
        
        public static _Fields findByThriftId(final int fieldId) {
            switch (fieldId) {
                case 1: {
                    return _Fields.NAME;
                }
                case 2: {
                    return _Fields.DESCRIPTION;
                }
                case 3: {
                    return _Fields.LOCATION_URI;
                }
                case 4: {
                    return _Fields.PARAMETERS;
                }
                case 5: {
                    return _Fields.PRIVILEGES;
                }
                case 6: {
                    return _Fields.OWNER_NAME;
                }
                case 7: {
                    return _Fields.OWNER_TYPE;
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
    
    private static class DatabaseStandardSchemeFactory implements SchemeFactory
    {
        @Override
        public DatabaseStandardScheme getScheme() {
            return new DatabaseStandardScheme();
        }
    }
    
    private static class DatabaseStandardScheme extends StandardScheme<Database>
    {
        @Override
        public void read(final TProtocol iprot, final Database struct) throws TException {
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
                            struct.description = iprot.readString();
                            struct.setDescriptionIsSet(true);
                            break;
                        }
                        TProtocolUtil.skip(iprot, schemeField.type);
                        break;
                    }
                    case 3: {
                        if (schemeField.type == 11) {
                            struct.locationUri = iprot.readString();
                            struct.setLocationUriIsSet(true);
                            break;
                        }
                        TProtocolUtil.skip(iprot, schemeField.type);
                        break;
                    }
                    case 4: {
                        if (schemeField.type == 13) {
                            final TMap _map94 = iprot.readMapBegin();
                            struct.parameters = (Map<String, String>)new HashMap(2 * _map94.size);
                            for (int _i95 = 0; _i95 < _map94.size; ++_i95) {
                                final String _key96 = iprot.readString();
                                final String _val97 = iprot.readString();
                                struct.parameters.put(_key96, _val97);
                            }
                            iprot.readMapEnd();
                            struct.setParametersIsSet(true);
                            break;
                        }
                        TProtocolUtil.skip(iprot, schemeField.type);
                        break;
                    }
                    case 5: {
                        if (schemeField.type == 12) {
                            struct.privileges = new PrincipalPrivilegeSet();
                            struct.privileges.read(iprot);
                            struct.setPrivilegesIsSet(true);
                            break;
                        }
                        TProtocolUtil.skip(iprot, schemeField.type);
                        break;
                    }
                    case 6: {
                        if (schemeField.type == 11) {
                            struct.ownerName = iprot.readString();
                            struct.setOwnerNameIsSet(true);
                            break;
                        }
                        TProtocolUtil.skip(iprot, schemeField.type);
                        break;
                    }
                    case 7: {
                        if (schemeField.type == 8) {
                            struct.ownerType = PrincipalType.findByValue(iprot.readI32());
                            struct.setOwnerTypeIsSet(true);
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
        public void write(final TProtocol oprot, final Database struct) throws TException {
            struct.validate();
            oprot.writeStructBegin(Database.STRUCT_DESC);
            if (struct.name != null) {
                oprot.writeFieldBegin(Database.NAME_FIELD_DESC);
                oprot.writeString(struct.name);
                oprot.writeFieldEnd();
            }
            if (struct.description != null) {
                oprot.writeFieldBegin(Database.DESCRIPTION_FIELD_DESC);
                oprot.writeString(struct.description);
                oprot.writeFieldEnd();
            }
            if (struct.locationUri != null) {
                oprot.writeFieldBegin(Database.LOCATION_URI_FIELD_DESC);
                oprot.writeString(struct.locationUri);
                oprot.writeFieldEnd();
            }
            if (struct.parameters != null) {
                oprot.writeFieldBegin(Database.PARAMETERS_FIELD_DESC);
                oprot.writeMapBegin(new TMap((byte)11, (byte)11, struct.parameters.size()));
                for (final Map.Entry<String, String> _iter98 : struct.parameters.entrySet()) {
                    oprot.writeString(_iter98.getKey());
                    oprot.writeString(_iter98.getValue());
                }
                oprot.writeMapEnd();
                oprot.writeFieldEnd();
            }
            if (struct.privileges != null && struct.isSetPrivileges()) {
                oprot.writeFieldBegin(Database.PRIVILEGES_FIELD_DESC);
                struct.privileges.write(oprot);
                oprot.writeFieldEnd();
            }
            if (struct.ownerName != null && struct.isSetOwnerName()) {
                oprot.writeFieldBegin(Database.OWNER_NAME_FIELD_DESC);
                oprot.writeString(struct.ownerName);
                oprot.writeFieldEnd();
            }
            if (struct.ownerType != null && struct.isSetOwnerType()) {
                oprot.writeFieldBegin(Database.OWNER_TYPE_FIELD_DESC);
                oprot.writeI32(struct.ownerType.getValue());
                oprot.writeFieldEnd();
            }
            oprot.writeFieldStop();
            oprot.writeStructEnd();
        }
    }
    
    private static class DatabaseTupleSchemeFactory implements SchemeFactory
    {
        @Override
        public DatabaseTupleScheme getScheme() {
            return new DatabaseTupleScheme();
        }
    }
    
    private static class DatabaseTupleScheme extends TupleScheme<Database>
    {
        @Override
        public void write(final TProtocol prot, final Database struct) throws TException {
            final TTupleProtocol oprot = (TTupleProtocol)prot;
            final BitSet optionals = new BitSet();
            if (struct.isSetName()) {
                optionals.set(0);
            }
            if (struct.isSetDescription()) {
                optionals.set(1);
            }
            if (struct.isSetLocationUri()) {
                optionals.set(2);
            }
            if (struct.isSetParameters()) {
                optionals.set(3);
            }
            if (struct.isSetPrivileges()) {
                optionals.set(4);
            }
            if (struct.isSetOwnerName()) {
                optionals.set(5);
            }
            if (struct.isSetOwnerType()) {
                optionals.set(6);
            }
            oprot.writeBitSet(optionals, 7);
            if (struct.isSetName()) {
                oprot.writeString(struct.name);
            }
            if (struct.isSetDescription()) {
                oprot.writeString(struct.description);
            }
            if (struct.isSetLocationUri()) {
                oprot.writeString(struct.locationUri);
            }
            if (struct.isSetParameters()) {
                oprot.writeI32(struct.parameters.size());
                for (final Map.Entry<String, String> _iter99 : struct.parameters.entrySet()) {
                    oprot.writeString(_iter99.getKey());
                    oprot.writeString(_iter99.getValue());
                }
            }
            if (struct.isSetPrivileges()) {
                struct.privileges.write(oprot);
            }
            if (struct.isSetOwnerName()) {
                oprot.writeString(struct.ownerName);
            }
            if (struct.isSetOwnerType()) {
                oprot.writeI32(struct.ownerType.getValue());
            }
        }
        
        @Override
        public void read(final TProtocol prot, final Database struct) throws TException {
            final TTupleProtocol iprot = (TTupleProtocol)prot;
            final BitSet incoming = iprot.readBitSet(7);
            if (incoming.get(0)) {
                struct.name = iprot.readString();
                struct.setNameIsSet(true);
            }
            if (incoming.get(1)) {
                struct.description = iprot.readString();
                struct.setDescriptionIsSet(true);
            }
            if (incoming.get(2)) {
                struct.locationUri = iprot.readString();
                struct.setLocationUriIsSet(true);
            }
            if (incoming.get(3)) {
                final TMap _map100 = new TMap((byte)11, (byte)11, iprot.readI32());
                struct.parameters = (Map<String, String>)new HashMap(2 * _map100.size);
                for (int _i101 = 0; _i101 < _map100.size; ++_i101) {
                    final String _key102 = iprot.readString();
                    final String _val103 = iprot.readString();
                    struct.parameters.put(_key102, _val103);
                }
                struct.setParametersIsSet(true);
            }
            if (incoming.get(4)) {
                struct.privileges = new PrincipalPrivilegeSet();
                struct.privileges.read(iprot);
                struct.setPrivilegesIsSet(true);
            }
            if (incoming.get(5)) {
                struct.ownerName = iprot.readString();
                struct.setOwnerNameIsSet(true);
            }
            if (incoming.get(6)) {
                struct.ownerType = PrincipalType.findByValue(iprot.readI32());
                struct.setOwnerTypeIsSet(true);
            }
        }
    }
}
