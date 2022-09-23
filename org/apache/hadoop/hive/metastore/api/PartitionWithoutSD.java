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
import org.apache.thrift.meta_data.StructMetaData;
import org.apache.thrift.meta_data.MapMetaData;
import org.apache.thrift.meta_data.ListMetaData;
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
import org.apache.thrift.EncodingUtils;
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

public class PartitionWithoutSD implements TBase<PartitionWithoutSD, _Fields>, Serializable, Cloneable
{
    private static final TStruct STRUCT_DESC;
    private static final TField VALUES_FIELD_DESC;
    private static final TField CREATE_TIME_FIELD_DESC;
    private static final TField LAST_ACCESS_TIME_FIELD_DESC;
    private static final TField RELATIVE_PATH_FIELD_DESC;
    private static final TField PARAMETERS_FIELD_DESC;
    private static final TField PRIVILEGES_FIELD_DESC;
    private static final Map<Class<? extends IScheme>, SchemeFactory> schemes;
    private List<String> values;
    private int createTime;
    private int lastAccessTime;
    private String relativePath;
    private Map<String, String> parameters;
    private PrincipalPrivilegeSet privileges;
    private static final int __CREATETIME_ISSET_ID = 0;
    private static final int __LASTACCESSTIME_ISSET_ID = 1;
    private byte __isset_bitfield;
    private _Fields[] optionals;
    public static final Map<_Fields, FieldMetaData> metaDataMap;
    
    public PartitionWithoutSD() {
        this.__isset_bitfield = 0;
        this.optionals = new _Fields[] { _Fields.PRIVILEGES };
    }
    
    public PartitionWithoutSD(final List<String> values, final int createTime, final int lastAccessTime, final String relativePath, final Map<String, String> parameters) {
        this();
        this.values = values;
        this.createTime = createTime;
        this.setCreateTimeIsSet(true);
        this.lastAccessTime = lastAccessTime;
        this.setLastAccessTimeIsSet(true);
        this.relativePath = relativePath;
        this.parameters = parameters;
    }
    
    public PartitionWithoutSD(final PartitionWithoutSD other) {
        this.__isset_bitfield = 0;
        this.optionals = new _Fields[] { _Fields.PRIVILEGES };
        this.__isset_bitfield = other.__isset_bitfield;
        if (other.isSetValues()) {
            final List<String> __this__values = new ArrayList<String>();
            for (final String other_element : other.values) {
                __this__values.add(other_element);
            }
            this.values = __this__values;
        }
        this.createTime = other.createTime;
        this.lastAccessTime = other.lastAccessTime;
        if (other.isSetRelativePath()) {
            this.relativePath = other.relativePath;
        }
        if (other.isSetParameters()) {
            final Map<String, String> __this__parameters = new HashMap<String, String>();
            for (final Map.Entry<String, String> other_element2 : other.parameters.entrySet()) {
                final String other_element_key = other_element2.getKey();
                final String other_element_value = other_element2.getValue();
                final String __this__parameters_copy_key = other_element_key;
                final String __this__parameters_copy_value = other_element_value;
                __this__parameters.put(__this__parameters_copy_key, __this__parameters_copy_value);
            }
            this.parameters = __this__parameters;
        }
        if (other.isSetPrivileges()) {
            this.privileges = new PrincipalPrivilegeSet(other.privileges);
        }
    }
    
    @Override
    public PartitionWithoutSD deepCopy() {
        return new PartitionWithoutSD(this);
    }
    
    @Override
    public void clear() {
        this.values = null;
        this.setCreateTimeIsSet(false);
        this.createTime = 0;
        this.setLastAccessTimeIsSet(false);
        this.lastAccessTime = 0;
        this.relativePath = null;
        this.parameters = null;
        this.privileges = null;
    }
    
    public int getValuesSize() {
        return (this.values == null) ? 0 : this.values.size();
    }
    
    public Iterator<String> getValuesIterator() {
        return (this.values == null) ? null : this.values.iterator();
    }
    
    public void addToValues(final String elem) {
        if (this.values == null) {
            this.values = new ArrayList<String>();
        }
        this.values.add(elem);
    }
    
    public List<String> getValues() {
        return this.values;
    }
    
    public void setValues(final List<String> values) {
        this.values = values;
    }
    
    public void unsetValues() {
        this.values = null;
    }
    
    public boolean isSetValues() {
        return this.values != null;
    }
    
    public void setValuesIsSet(final boolean value) {
        if (!value) {
            this.values = null;
        }
    }
    
    public int getCreateTime() {
        return this.createTime;
    }
    
    public void setCreateTime(final int createTime) {
        this.createTime = createTime;
        this.setCreateTimeIsSet(true);
    }
    
    public void unsetCreateTime() {
        this.__isset_bitfield = EncodingUtils.clearBit(this.__isset_bitfield, 0);
    }
    
    public boolean isSetCreateTime() {
        return EncodingUtils.testBit(this.__isset_bitfield, 0);
    }
    
    public void setCreateTimeIsSet(final boolean value) {
        this.__isset_bitfield = EncodingUtils.setBit(this.__isset_bitfield, 0, value);
    }
    
    public int getLastAccessTime() {
        return this.lastAccessTime;
    }
    
    public void setLastAccessTime(final int lastAccessTime) {
        this.lastAccessTime = lastAccessTime;
        this.setLastAccessTimeIsSet(true);
    }
    
    public void unsetLastAccessTime() {
        this.__isset_bitfield = EncodingUtils.clearBit(this.__isset_bitfield, 1);
    }
    
    public boolean isSetLastAccessTime() {
        return EncodingUtils.testBit(this.__isset_bitfield, 1);
    }
    
    public void setLastAccessTimeIsSet(final boolean value) {
        this.__isset_bitfield = EncodingUtils.setBit(this.__isset_bitfield, 1, value);
    }
    
    public String getRelativePath() {
        return this.relativePath;
    }
    
    public void setRelativePath(final String relativePath) {
        this.relativePath = relativePath;
    }
    
    public void unsetRelativePath() {
        this.relativePath = null;
    }
    
    public boolean isSetRelativePath() {
        return this.relativePath != null;
    }
    
    public void setRelativePathIsSet(final boolean value) {
        if (!value) {
            this.relativePath = null;
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
    
    @Override
    public void setFieldValue(final _Fields field, final Object value) {
        switch (field) {
            case VALUES: {
                if (value == null) {
                    this.unsetValues();
                    break;
                }
                this.setValues((List<String>)value);
                break;
            }
            case CREATE_TIME: {
                if (value == null) {
                    this.unsetCreateTime();
                    break;
                }
                this.setCreateTime((int)value);
                break;
            }
            case LAST_ACCESS_TIME: {
                if (value == null) {
                    this.unsetLastAccessTime();
                    break;
                }
                this.setLastAccessTime((int)value);
                break;
            }
            case RELATIVE_PATH: {
                if (value == null) {
                    this.unsetRelativePath();
                    break;
                }
                this.setRelativePath((String)value);
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
        }
    }
    
    @Override
    public Object getFieldValue(final _Fields field) {
        switch (field) {
            case VALUES: {
                return this.getValues();
            }
            case CREATE_TIME: {
                return this.getCreateTime();
            }
            case LAST_ACCESS_TIME: {
                return this.getLastAccessTime();
            }
            case RELATIVE_PATH: {
                return this.getRelativePath();
            }
            case PARAMETERS: {
                return this.getParameters();
            }
            case PRIVILEGES: {
                return this.getPrivileges();
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
            case VALUES: {
                return this.isSetValues();
            }
            case CREATE_TIME: {
                return this.isSetCreateTime();
            }
            case LAST_ACCESS_TIME: {
                return this.isSetLastAccessTime();
            }
            case RELATIVE_PATH: {
                return this.isSetRelativePath();
            }
            case PARAMETERS: {
                return this.isSetParameters();
            }
            case PRIVILEGES: {
                return this.isSetPrivileges();
            }
            default: {
                throw new IllegalStateException();
            }
        }
    }
    
    @Override
    public boolean equals(final Object that) {
        return that != null && that instanceof PartitionWithoutSD && this.equals((PartitionWithoutSD)that);
    }
    
    public boolean equals(final PartitionWithoutSD that) {
        if (that == null) {
            return false;
        }
        final boolean this_present_values = this.isSetValues();
        final boolean that_present_values = that.isSetValues();
        if (this_present_values || that_present_values) {
            if (!this_present_values || !that_present_values) {
                return false;
            }
            if (!this.values.equals(that.values)) {
                return false;
            }
        }
        final boolean this_present_createTime = true;
        final boolean that_present_createTime = true;
        if (this_present_createTime || that_present_createTime) {
            if (!this_present_createTime || !that_present_createTime) {
                return false;
            }
            if (this.createTime != that.createTime) {
                return false;
            }
        }
        final boolean this_present_lastAccessTime = true;
        final boolean that_present_lastAccessTime = true;
        if (this_present_lastAccessTime || that_present_lastAccessTime) {
            if (!this_present_lastAccessTime || !that_present_lastAccessTime) {
                return false;
            }
            if (this.lastAccessTime != that.lastAccessTime) {
                return false;
            }
        }
        final boolean this_present_relativePath = this.isSetRelativePath();
        final boolean that_present_relativePath = that.isSetRelativePath();
        if (this_present_relativePath || that_present_relativePath) {
            if (!this_present_relativePath || !that_present_relativePath) {
                return false;
            }
            if (!this.relativePath.equals(that.relativePath)) {
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
        return true;
    }
    
    @Override
    public int hashCode() {
        final HashCodeBuilder builder = new HashCodeBuilder();
        final boolean present_values = this.isSetValues();
        builder.append(present_values);
        if (present_values) {
            builder.append(this.values);
        }
        final boolean present_createTime = true;
        builder.append(present_createTime);
        if (present_createTime) {
            builder.append(this.createTime);
        }
        final boolean present_lastAccessTime = true;
        builder.append(present_lastAccessTime);
        if (present_lastAccessTime) {
            builder.append(this.lastAccessTime);
        }
        final boolean present_relativePath = this.isSetRelativePath();
        builder.append(present_relativePath);
        if (present_relativePath) {
            builder.append(this.relativePath);
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
        return builder.toHashCode();
    }
    
    @Override
    public int compareTo(final PartitionWithoutSD other) {
        if (!this.getClass().equals(other.getClass())) {
            return this.getClass().getName().compareTo(other.getClass().getName());
        }
        int lastComparison = 0;
        final PartitionWithoutSD typedOther = other;
        lastComparison = Boolean.valueOf(this.isSetValues()).compareTo(Boolean.valueOf(typedOther.isSetValues()));
        if (lastComparison != 0) {
            return lastComparison;
        }
        if (this.isSetValues()) {
            lastComparison = TBaseHelper.compareTo(this.values, typedOther.values);
            if (lastComparison != 0) {
                return lastComparison;
            }
        }
        lastComparison = Boolean.valueOf(this.isSetCreateTime()).compareTo(Boolean.valueOf(typedOther.isSetCreateTime()));
        if (lastComparison != 0) {
            return lastComparison;
        }
        if (this.isSetCreateTime()) {
            lastComparison = TBaseHelper.compareTo(this.createTime, typedOther.createTime);
            if (lastComparison != 0) {
                return lastComparison;
            }
        }
        lastComparison = Boolean.valueOf(this.isSetLastAccessTime()).compareTo(Boolean.valueOf(typedOther.isSetLastAccessTime()));
        if (lastComparison != 0) {
            return lastComparison;
        }
        if (this.isSetLastAccessTime()) {
            lastComparison = TBaseHelper.compareTo(this.lastAccessTime, typedOther.lastAccessTime);
            if (lastComparison != 0) {
                return lastComparison;
            }
        }
        lastComparison = Boolean.valueOf(this.isSetRelativePath()).compareTo(Boolean.valueOf(typedOther.isSetRelativePath()));
        if (lastComparison != 0) {
            return lastComparison;
        }
        if (this.isSetRelativePath()) {
            lastComparison = TBaseHelper.compareTo(this.relativePath, typedOther.relativePath);
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
        return 0;
    }
    
    @Override
    public _Fields fieldForId(final int fieldId) {
        return _Fields.findByThriftId(fieldId);
    }
    
    @Override
    public void read(final TProtocol iprot) throws TException {
        PartitionWithoutSD.schemes.get(iprot.getScheme()).getScheme().read(iprot, this);
    }
    
    @Override
    public void write(final TProtocol oprot) throws TException {
        PartitionWithoutSD.schemes.get(oprot.getScheme()).getScheme().write(oprot, this);
    }
    
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("PartitionWithoutSD(");
        boolean first = true;
        sb.append("values:");
        if (this.values == null) {
            sb.append("null");
        }
        else {
            sb.append(this.values);
        }
        first = false;
        if (!first) {
            sb.append(", ");
        }
        sb.append("createTime:");
        sb.append(this.createTime);
        first = false;
        if (!first) {
            sb.append(", ");
        }
        sb.append("lastAccessTime:");
        sb.append(this.lastAccessTime);
        first = false;
        if (!first) {
            sb.append(", ");
        }
        sb.append("relativePath:");
        if (this.relativePath == null) {
            sb.append("null");
        }
        else {
            sb.append(this.relativePath);
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
            this.__isset_bitfield = 0;
            this.read(new TCompactProtocol(new TIOStreamTransport(in)));
        }
        catch (TException te) {
            throw new IOException(te);
        }
    }
    
    static {
        STRUCT_DESC = new TStruct("PartitionWithoutSD");
        VALUES_FIELD_DESC = new TField("values", (byte)15, (short)1);
        CREATE_TIME_FIELD_DESC = new TField("createTime", (byte)8, (short)2);
        LAST_ACCESS_TIME_FIELD_DESC = new TField("lastAccessTime", (byte)8, (short)3);
        RELATIVE_PATH_FIELD_DESC = new TField("relativePath", (byte)11, (short)4);
        PARAMETERS_FIELD_DESC = new TField("parameters", (byte)13, (short)5);
        PRIVILEGES_FIELD_DESC = new TField("privileges", (byte)12, (short)6);
        (schemes = new HashMap<Class<? extends IScheme>, SchemeFactory>()).put(StandardScheme.class, new PartitionWithoutSDStandardSchemeFactory());
        PartitionWithoutSD.schemes.put(TupleScheme.class, new PartitionWithoutSDTupleSchemeFactory());
        final Map<_Fields, FieldMetaData> tmpMap = new EnumMap<_Fields, FieldMetaData>(_Fields.class);
        tmpMap.put(_Fields.VALUES, new FieldMetaData("values", (byte)3, new ListMetaData((byte)15, new FieldValueMetaData((byte)11))));
        tmpMap.put(_Fields.CREATE_TIME, new FieldMetaData("createTime", (byte)3, new FieldValueMetaData((byte)8)));
        tmpMap.put(_Fields.LAST_ACCESS_TIME, new FieldMetaData("lastAccessTime", (byte)3, new FieldValueMetaData((byte)8)));
        tmpMap.put(_Fields.RELATIVE_PATH, new FieldMetaData("relativePath", (byte)3, new FieldValueMetaData((byte)11)));
        tmpMap.put(_Fields.PARAMETERS, new FieldMetaData("parameters", (byte)3, new MapMetaData((byte)13, new FieldValueMetaData((byte)11), new FieldValueMetaData((byte)11))));
        tmpMap.put(_Fields.PRIVILEGES, new FieldMetaData("privileges", (byte)2, new StructMetaData((byte)12, PrincipalPrivilegeSet.class)));
        FieldMetaData.addStructMetaDataMap(PartitionWithoutSD.class, metaDataMap = Collections.unmodifiableMap((Map<? extends _Fields, ? extends FieldMetaData>)tmpMap));
    }
    
    public enum _Fields implements TFieldIdEnum
    {
        VALUES((short)1, "values"), 
        CREATE_TIME((short)2, "createTime"), 
        LAST_ACCESS_TIME((short)3, "lastAccessTime"), 
        RELATIVE_PATH((short)4, "relativePath"), 
        PARAMETERS((short)5, "parameters"), 
        PRIVILEGES((short)6, "privileges");
        
        private static final Map<String, _Fields> byName;
        private final short _thriftId;
        private final String _fieldName;
        
        public static _Fields findByThriftId(final int fieldId) {
            switch (fieldId) {
                case 1: {
                    return _Fields.VALUES;
                }
                case 2: {
                    return _Fields.CREATE_TIME;
                }
                case 3: {
                    return _Fields.LAST_ACCESS_TIME;
                }
                case 4: {
                    return _Fields.RELATIVE_PATH;
                }
                case 5: {
                    return _Fields.PARAMETERS;
                }
                case 6: {
                    return _Fields.PRIVILEGES;
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
    
    private static class PartitionWithoutSDStandardSchemeFactory implements SchemeFactory
    {
        @Override
        public PartitionWithoutSDStandardScheme getScheme() {
            return new PartitionWithoutSDStandardScheme();
        }
    }
    
    private static class PartitionWithoutSDStandardScheme extends StandardScheme<PartitionWithoutSD>
    {
        @Override
        public void read(final TProtocol iprot, final PartitionWithoutSD struct) throws TException {
            iprot.readStructBegin();
            while (true) {
                final TField schemeField = iprot.readFieldBegin();
                if (schemeField.type == 0) {
                    break;
                }
                switch (schemeField.id) {
                    case 1: {
                        if (schemeField.type == 15) {
                            final TList _list226 = iprot.readListBegin();
                            struct.values = (List<String>)new ArrayList(_list226.size);
                            for (int _i227 = 0; _i227 < _list226.size; ++_i227) {
                                final String _elem228 = iprot.readString();
                                struct.values.add(_elem228);
                            }
                            iprot.readListEnd();
                            struct.setValuesIsSet(true);
                            break;
                        }
                        TProtocolUtil.skip(iprot, schemeField.type);
                        break;
                    }
                    case 2: {
                        if (schemeField.type == 8) {
                            struct.createTime = iprot.readI32();
                            struct.setCreateTimeIsSet(true);
                            break;
                        }
                        TProtocolUtil.skip(iprot, schemeField.type);
                        break;
                    }
                    case 3: {
                        if (schemeField.type == 8) {
                            struct.lastAccessTime = iprot.readI32();
                            struct.setLastAccessTimeIsSet(true);
                            break;
                        }
                        TProtocolUtil.skip(iprot, schemeField.type);
                        break;
                    }
                    case 4: {
                        if (schemeField.type == 11) {
                            struct.relativePath = iprot.readString();
                            struct.setRelativePathIsSet(true);
                            break;
                        }
                        TProtocolUtil.skip(iprot, schemeField.type);
                        break;
                    }
                    case 5: {
                        if (schemeField.type == 13) {
                            final TMap _map229 = iprot.readMapBegin();
                            struct.parameters = (Map<String, String>)new HashMap(2 * _map229.size);
                            for (int _i228 = 0; _i228 < _map229.size; ++_i228) {
                                final String _key231 = iprot.readString();
                                final String _val232 = iprot.readString();
                                struct.parameters.put(_key231, _val232);
                            }
                            iprot.readMapEnd();
                            struct.setParametersIsSet(true);
                            break;
                        }
                        TProtocolUtil.skip(iprot, schemeField.type);
                        break;
                    }
                    case 6: {
                        if (schemeField.type == 12) {
                            struct.privileges = new PrincipalPrivilegeSet();
                            struct.privileges.read(iprot);
                            struct.setPrivilegesIsSet(true);
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
        public void write(final TProtocol oprot, final PartitionWithoutSD struct) throws TException {
            struct.validate();
            oprot.writeStructBegin(PartitionWithoutSD.STRUCT_DESC);
            if (struct.values != null) {
                oprot.writeFieldBegin(PartitionWithoutSD.VALUES_FIELD_DESC);
                oprot.writeListBegin(new TList((byte)11, struct.values.size()));
                for (final String _iter233 : struct.values) {
                    oprot.writeString(_iter233);
                }
                oprot.writeListEnd();
                oprot.writeFieldEnd();
            }
            oprot.writeFieldBegin(PartitionWithoutSD.CREATE_TIME_FIELD_DESC);
            oprot.writeI32(struct.createTime);
            oprot.writeFieldEnd();
            oprot.writeFieldBegin(PartitionWithoutSD.LAST_ACCESS_TIME_FIELD_DESC);
            oprot.writeI32(struct.lastAccessTime);
            oprot.writeFieldEnd();
            if (struct.relativePath != null) {
                oprot.writeFieldBegin(PartitionWithoutSD.RELATIVE_PATH_FIELD_DESC);
                oprot.writeString(struct.relativePath);
                oprot.writeFieldEnd();
            }
            if (struct.parameters != null) {
                oprot.writeFieldBegin(PartitionWithoutSD.PARAMETERS_FIELD_DESC);
                oprot.writeMapBegin(new TMap((byte)11, (byte)11, struct.parameters.size()));
                for (final Map.Entry<String, String> _iter234 : struct.parameters.entrySet()) {
                    oprot.writeString(_iter234.getKey());
                    oprot.writeString(_iter234.getValue());
                }
                oprot.writeMapEnd();
                oprot.writeFieldEnd();
            }
            if (struct.privileges != null && struct.isSetPrivileges()) {
                oprot.writeFieldBegin(PartitionWithoutSD.PRIVILEGES_FIELD_DESC);
                struct.privileges.write(oprot);
                oprot.writeFieldEnd();
            }
            oprot.writeFieldStop();
            oprot.writeStructEnd();
        }
    }
    
    private static class PartitionWithoutSDTupleSchemeFactory implements SchemeFactory
    {
        @Override
        public PartitionWithoutSDTupleScheme getScheme() {
            return new PartitionWithoutSDTupleScheme();
        }
    }
    
    private static class PartitionWithoutSDTupleScheme extends TupleScheme<PartitionWithoutSD>
    {
        @Override
        public void write(final TProtocol prot, final PartitionWithoutSD struct) throws TException {
            final TTupleProtocol oprot = (TTupleProtocol)prot;
            final BitSet optionals = new BitSet();
            if (struct.isSetValues()) {
                optionals.set(0);
            }
            if (struct.isSetCreateTime()) {
                optionals.set(1);
            }
            if (struct.isSetLastAccessTime()) {
                optionals.set(2);
            }
            if (struct.isSetRelativePath()) {
                optionals.set(3);
            }
            if (struct.isSetParameters()) {
                optionals.set(4);
            }
            if (struct.isSetPrivileges()) {
                optionals.set(5);
            }
            oprot.writeBitSet(optionals, 6);
            if (struct.isSetValues()) {
                oprot.writeI32(struct.values.size());
                for (final String _iter235 : struct.values) {
                    oprot.writeString(_iter235);
                }
            }
            if (struct.isSetCreateTime()) {
                oprot.writeI32(struct.createTime);
            }
            if (struct.isSetLastAccessTime()) {
                oprot.writeI32(struct.lastAccessTime);
            }
            if (struct.isSetRelativePath()) {
                oprot.writeString(struct.relativePath);
            }
            if (struct.isSetParameters()) {
                oprot.writeI32(struct.parameters.size());
                for (final Map.Entry<String, String> _iter236 : struct.parameters.entrySet()) {
                    oprot.writeString(_iter236.getKey());
                    oprot.writeString(_iter236.getValue());
                }
            }
            if (struct.isSetPrivileges()) {
                struct.privileges.write(oprot);
            }
        }
        
        @Override
        public void read(final TProtocol prot, final PartitionWithoutSD struct) throws TException {
            final TTupleProtocol iprot = (TTupleProtocol)prot;
            final BitSet incoming = iprot.readBitSet(6);
            if (incoming.get(0)) {
                final TList _list237 = new TList((byte)11, iprot.readI32());
                struct.values = (List<String>)new ArrayList(_list237.size);
                for (int _i238 = 0; _i238 < _list237.size; ++_i238) {
                    final String _elem239 = iprot.readString();
                    struct.values.add(_elem239);
                }
                struct.setValuesIsSet(true);
            }
            if (incoming.get(1)) {
                struct.createTime = iprot.readI32();
                struct.setCreateTimeIsSet(true);
            }
            if (incoming.get(2)) {
                struct.lastAccessTime = iprot.readI32();
                struct.setLastAccessTimeIsSet(true);
            }
            if (incoming.get(3)) {
                struct.relativePath = iprot.readString();
                struct.setRelativePathIsSet(true);
            }
            if (incoming.get(4)) {
                final TMap _map240 = new TMap((byte)11, (byte)11, iprot.readI32());
                struct.parameters = (Map<String, String>)new HashMap(2 * _map240.size);
                for (int _i239 = 0; _i239 < _map240.size; ++_i239) {
                    final String _key242 = iprot.readString();
                    final String _val243 = iprot.readString();
                    struct.parameters.put(_key242, _val243);
                }
                struct.setParametersIsSet(true);
            }
            if (incoming.get(5)) {
                struct.privileges = new PrincipalPrivilegeSet();
                struct.privileges.read(iprot);
                struct.setPrivilegesIsSet(true);
            }
        }
    }
}
