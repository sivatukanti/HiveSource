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

public class SkewedInfo implements TBase<SkewedInfo, _Fields>, Serializable, Cloneable
{
    private static final TStruct STRUCT_DESC;
    private static final TField SKEWED_COL_NAMES_FIELD_DESC;
    private static final TField SKEWED_COL_VALUES_FIELD_DESC;
    private static final TField SKEWED_COL_VALUE_LOCATION_MAPS_FIELD_DESC;
    private static final Map<Class<? extends IScheme>, SchemeFactory> schemes;
    private List<String> skewedColNames;
    private List<List<String>> skewedColValues;
    private Map<List<String>, String> skewedColValueLocationMaps;
    public static final Map<_Fields, FieldMetaData> metaDataMap;
    
    public SkewedInfo() {
    }
    
    public SkewedInfo(final List<String> skewedColNames, final List<List<String>> skewedColValues, final Map<List<String>, String> skewedColValueLocationMaps) {
        this();
        this.skewedColNames = skewedColNames;
        this.skewedColValues = skewedColValues;
        this.skewedColValueLocationMaps = skewedColValueLocationMaps;
    }
    
    public SkewedInfo(final SkewedInfo other) {
        if (other.isSetSkewedColNames()) {
            final List<String> __this__skewedColNames = new ArrayList<String>();
            for (final String other_element : other.skewedColNames) {
                __this__skewedColNames.add(other_element);
            }
            this.skewedColNames = __this__skewedColNames;
        }
        if (other.isSetSkewedColValues()) {
            final List<List<String>> __this__skewedColValues = new ArrayList<List<String>>();
            for (final List<String> other_element2 : other.skewedColValues) {
                final List<String> __this__skewedColValues_copy = new ArrayList<String>();
                for (final String other_element_element : other_element2) {
                    __this__skewedColValues_copy.add(other_element_element);
                }
                __this__skewedColValues.add(__this__skewedColValues_copy);
            }
            this.skewedColValues = __this__skewedColValues;
        }
        if (other.isSetSkewedColValueLocationMaps()) {
            final Map<List<String>, String> __this__skewedColValueLocationMaps = new HashMap<List<String>, String>();
            for (final Map.Entry<List<String>, String> other_element3 : other.skewedColValueLocationMaps.entrySet()) {
                final List<String> other_element_key = other_element3.getKey();
                final String other_element_value = other_element3.getValue();
                final List<String> __this__skewedColValueLocationMaps_copy_key = new ArrayList<String>();
                for (final String other_element_key_element : other_element_key) {
                    __this__skewedColValueLocationMaps_copy_key.add(other_element_key_element);
                }
                final String __this__skewedColValueLocationMaps_copy_value = other_element_value;
                __this__skewedColValueLocationMaps.put(__this__skewedColValueLocationMaps_copy_key, __this__skewedColValueLocationMaps_copy_value);
            }
            this.skewedColValueLocationMaps = __this__skewedColValueLocationMaps;
        }
    }
    
    @Override
    public SkewedInfo deepCopy() {
        return new SkewedInfo(this);
    }
    
    @Override
    public void clear() {
        this.skewedColNames = null;
        this.skewedColValues = null;
        this.skewedColValueLocationMaps = null;
    }
    
    public int getSkewedColNamesSize() {
        return (this.skewedColNames == null) ? 0 : this.skewedColNames.size();
    }
    
    public Iterator<String> getSkewedColNamesIterator() {
        return (this.skewedColNames == null) ? null : this.skewedColNames.iterator();
    }
    
    public void addToSkewedColNames(final String elem) {
        if (this.skewedColNames == null) {
            this.skewedColNames = new ArrayList<String>();
        }
        this.skewedColNames.add(elem);
    }
    
    public List<String> getSkewedColNames() {
        return this.skewedColNames;
    }
    
    public void setSkewedColNames(final List<String> skewedColNames) {
        this.skewedColNames = skewedColNames;
    }
    
    public void unsetSkewedColNames() {
        this.skewedColNames = null;
    }
    
    public boolean isSetSkewedColNames() {
        return this.skewedColNames != null;
    }
    
    public void setSkewedColNamesIsSet(final boolean value) {
        if (!value) {
            this.skewedColNames = null;
        }
    }
    
    public int getSkewedColValuesSize() {
        return (this.skewedColValues == null) ? 0 : this.skewedColValues.size();
    }
    
    public Iterator<List<String>> getSkewedColValuesIterator() {
        return (this.skewedColValues == null) ? null : this.skewedColValues.iterator();
    }
    
    public void addToSkewedColValues(final List<String> elem) {
        if (this.skewedColValues == null) {
            this.skewedColValues = new ArrayList<List<String>>();
        }
        this.skewedColValues.add(elem);
    }
    
    public List<List<String>> getSkewedColValues() {
        return this.skewedColValues;
    }
    
    public void setSkewedColValues(final List<List<String>> skewedColValues) {
        this.skewedColValues = skewedColValues;
    }
    
    public void unsetSkewedColValues() {
        this.skewedColValues = null;
    }
    
    public boolean isSetSkewedColValues() {
        return this.skewedColValues != null;
    }
    
    public void setSkewedColValuesIsSet(final boolean value) {
        if (!value) {
            this.skewedColValues = null;
        }
    }
    
    public int getSkewedColValueLocationMapsSize() {
        return (this.skewedColValueLocationMaps == null) ? 0 : this.skewedColValueLocationMaps.size();
    }
    
    public void putToSkewedColValueLocationMaps(final List<String> key, final String val) {
        if (this.skewedColValueLocationMaps == null) {
            this.skewedColValueLocationMaps = new HashMap<List<String>, String>();
        }
        this.skewedColValueLocationMaps.put(key, val);
    }
    
    public Map<List<String>, String> getSkewedColValueLocationMaps() {
        return this.skewedColValueLocationMaps;
    }
    
    public void setSkewedColValueLocationMaps(final Map<List<String>, String> skewedColValueLocationMaps) {
        this.skewedColValueLocationMaps = skewedColValueLocationMaps;
    }
    
    public void unsetSkewedColValueLocationMaps() {
        this.skewedColValueLocationMaps = null;
    }
    
    public boolean isSetSkewedColValueLocationMaps() {
        return this.skewedColValueLocationMaps != null;
    }
    
    public void setSkewedColValueLocationMapsIsSet(final boolean value) {
        if (!value) {
            this.skewedColValueLocationMaps = null;
        }
    }
    
    @Override
    public void setFieldValue(final _Fields field, final Object value) {
        switch (field) {
            case SKEWED_COL_NAMES: {
                if (value == null) {
                    this.unsetSkewedColNames();
                    break;
                }
                this.setSkewedColNames((List<String>)value);
                break;
            }
            case SKEWED_COL_VALUES: {
                if (value == null) {
                    this.unsetSkewedColValues();
                    break;
                }
                this.setSkewedColValues((List<List<String>>)value);
                break;
            }
            case SKEWED_COL_VALUE_LOCATION_MAPS: {
                if (value == null) {
                    this.unsetSkewedColValueLocationMaps();
                    break;
                }
                this.setSkewedColValueLocationMaps((Map<List<String>, String>)value);
                break;
            }
        }
    }
    
    @Override
    public Object getFieldValue(final _Fields field) {
        switch (field) {
            case SKEWED_COL_NAMES: {
                return this.getSkewedColNames();
            }
            case SKEWED_COL_VALUES: {
                return this.getSkewedColValues();
            }
            case SKEWED_COL_VALUE_LOCATION_MAPS: {
                return this.getSkewedColValueLocationMaps();
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
            case SKEWED_COL_NAMES: {
                return this.isSetSkewedColNames();
            }
            case SKEWED_COL_VALUES: {
                return this.isSetSkewedColValues();
            }
            case SKEWED_COL_VALUE_LOCATION_MAPS: {
                return this.isSetSkewedColValueLocationMaps();
            }
            default: {
                throw new IllegalStateException();
            }
        }
    }
    
    @Override
    public boolean equals(final Object that) {
        return that != null && that instanceof SkewedInfo && this.equals((SkewedInfo)that);
    }
    
    public boolean equals(final SkewedInfo that) {
        if (that == null) {
            return false;
        }
        final boolean this_present_skewedColNames = this.isSetSkewedColNames();
        final boolean that_present_skewedColNames = that.isSetSkewedColNames();
        if (this_present_skewedColNames || that_present_skewedColNames) {
            if (!this_present_skewedColNames || !that_present_skewedColNames) {
                return false;
            }
            if (!this.skewedColNames.equals(that.skewedColNames)) {
                return false;
            }
        }
        final boolean this_present_skewedColValues = this.isSetSkewedColValues();
        final boolean that_present_skewedColValues = that.isSetSkewedColValues();
        if (this_present_skewedColValues || that_present_skewedColValues) {
            if (!this_present_skewedColValues || !that_present_skewedColValues) {
                return false;
            }
            if (!this.skewedColValues.equals(that.skewedColValues)) {
                return false;
            }
        }
        final boolean this_present_skewedColValueLocationMaps = this.isSetSkewedColValueLocationMaps();
        final boolean that_present_skewedColValueLocationMaps = that.isSetSkewedColValueLocationMaps();
        if (this_present_skewedColValueLocationMaps || that_present_skewedColValueLocationMaps) {
            if (!this_present_skewedColValueLocationMaps || !that_present_skewedColValueLocationMaps) {
                return false;
            }
            if (!this.skewedColValueLocationMaps.equals(that.skewedColValueLocationMaps)) {
                return false;
            }
        }
        return true;
    }
    
    @Override
    public int hashCode() {
        final HashCodeBuilder builder = new HashCodeBuilder();
        final boolean present_skewedColNames = this.isSetSkewedColNames();
        builder.append(present_skewedColNames);
        if (present_skewedColNames) {
            builder.append(this.skewedColNames);
        }
        final boolean present_skewedColValues = this.isSetSkewedColValues();
        builder.append(present_skewedColValues);
        if (present_skewedColValues) {
            builder.append(this.skewedColValues);
        }
        final boolean present_skewedColValueLocationMaps = this.isSetSkewedColValueLocationMaps();
        builder.append(present_skewedColValueLocationMaps);
        if (present_skewedColValueLocationMaps) {
            builder.append(this.skewedColValueLocationMaps);
        }
        return builder.toHashCode();
    }
    
    @Override
    public int compareTo(final SkewedInfo other) {
        if (!this.getClass().equals(other.getClass())) {
            return this.getClass().getName().compareTo(other.getClass().getName());
        }
        int lastComparison = 0;
        final SkewedInfo typedOther = other;
        lastComparison = Boolean.valueOf(this.isSetSkewedColNames()).compareTo(Boolean.valueOf(typedOther.isSetSkewedColNames()));
        if (lastComparison != 0) {
            return lastComparison;
        }
        if (this.isSetSkewedColNames()) {
            lastComparison = TBaseHelper.compareTo(this.skewedColNames, typedOther.skewedColNames);
            if (lastComparison != 0) {
                return lastComparison;
            }
        }
        lastComparison = Boolean.valueOf(this.isSetSkewedColValues()).compareTo(Boolean.valueOf(typedOther.isSetSkewedColValues()));
        if (lastComparison != 0) {
            return lastComparison;
        }
        if (this.isSetSkewedColValues()) {
            lastComparison = TBaseHelper.compareTo(this.skewedColValues, typedOther.skewedColValues);
            if (lastComparison != 0) {
                return lastComparison;
            }
        }
        lastComparison = Boolean.valueOf(this.isSetSkewedColValueLocationMaps()).compareTo(Boolean.valueOf(typedOther.isSetSkewedColValueLocationMaps()));
        if (lastComparison != 0) {
            return lastComparison;
        }
        if (this.isSetSkewedColValueLocationMaps()) {
            lastComparison = TBaseHelper.compareTo(this.skewedColValueLocationMaps, typedOther.skewedColValueLocationMaps);
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
        SkewedInfo.schemes.get(iprot.getScheme()).getScheme().read(iprot, this);
    }
    
    @Override
    public void write(final TProtocol oprot) throws TException {
        SkewedInfo.schemes.get(oprot.getScheme()).getScheme().write(oprot, this);
    }
    
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("SkewedInfo(");
        boolean first = true;
        sb.append("skewedColNames:");
        if (this.skewedColNames == null) {
            sb.append("null");
        }
        else {
            sb.append(this.skewedColNames);
        }
        first = false;
        if (!first) {
            sb.append(", ");
        }
        sb.append("skewedColValues:");
        if (this.skewedColValues == null) {
            sb.append("null");
        }
        else {
            sb.append(this.skewedColValues);
        }
        first = false;
        if (!first) {
            sb.append(", ");
        }
        sb.append("skewedColValueLocationMaps:");
        if (this.skewedColValueLocationMaps == null) {
            sb.append("null");
        }
        else {
            sb.append(this.skewedColValueLocationMaps);
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
        STRUCT_DESC = new TStruct("SkewedInfo");
        SKEWED_COL_NAMES_FIELD_DESC = new TField("skewedColNames", (byte)15, (short)1);
        SKEWED_COL_VALUES_FIELD_DESC = new TField("skewedColValues", (byte)15, (short)2);
        SKEWED_COL_VALUE_LOCATION_MAPS_FIELD_DESC = new TField("skewedColValueLocationMaps", (byte)13, (short)3);
        (schemes = new HashMap<Class<? extends IScheme>, SchemeFactory>()).put(StandardScheme.class, new SkewedInfoStandardSchemeFactory());
        SkewedInfo.schemes.put(TupleScheme.class, new SkewedInfoTupleSchemeFactory());
        final Map<_Fields, FieldMetaData> tmpMap = new EnumMap<_Fields, FieldMetaData>(_Fields.class);
        tmpMap.put(_Fields.SKEWED_COL_NAMES, new FieldMetaData("skewedColNames", (byte)3, new ListMetaData((byte)15, new FieldValueMetaData((byte)11))));
        tmpMap.put(_Fields.SKEWED_COL_VALUES, new FieldMetaData("skewedColValues", (byte)3, new ListMetaData((byte)15, new ListMetaData((byte)15, new FieldValueMetaData((byte)11)))));
        tmpMap.put(_Fields.SKEWED_COL_VALUE_LOCATION_MAPS, new FieldMetaData("skewedColValueLocationMaps", (byte)3, new MapMetaData((byte)13, new ListMetaData((byte)15, new FieldValueMetaData((byte)11)), new FieldValueMetaData((byte)11))));
        FieldMetaData.addStructMetaDataMap(SkewedInfo.class, metaDataMap = Collections.unmodifiableMap((Map<? extends _Fields, ? extends FieldMetaData>)tmpMap));
    }
    
    public enum _Fields implements TFieldIdEnum
    {
        SKEWED_COL_NAMES((short)1, "skewedColNames"), 
        SKEWED_COL_VALUES((short)2, "skewedColValues"), 
        SKEWED_COL_VALUE_LOCATION_MAPS((short)3, "skewedColValueLocationMaps");
        
        private static final Map<String, _Fields> byName;
        private final short _thriftId;
        private final String _fieldName;
        
        public static _Fields findByThriftId(final int fieldId) {
            switch (fieldId) {
                case 1: {
                    return _Fields.SKEWED_COL_NAMES;
                }
                case 2: {
                    return _Fields.SKEWED_COL_VALUES;
                }
                case 3: {
                    return _Fields.SKEWED_COL_VALUE_LOCATION_MAPS;
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
    
    private static class SkewedInfoStandardSchemeFactory implements SchemeFactory
    {
        @Override
        public SkewedInfoStandardScheme getScheme() {
            return new SkewedInfoStandardScheme();
        }
    }
    
    private static class SkewedInfoStandardScheme extends StandardScheme<SkewedInfo>
    {
        @Override
        public void read(final TProtocol iprot, final SkewedInfo struct) throws TException {
            iprot.readStructBegin();
            while (true) {
                final TField schemeField = iprot.readFieldBegin();
                if (schemeField.type == 0) {
                    break;
                }
                switch (schemeField.id) {
                    case 1: {
                        if (schemeField.type == 15) {
                            final TList _list114 = iprot.readListBegin();
                            struct.skewedColNames = (List<String>)new ArrayList(_list114.size);
                            for (int _i115 = 0; _i115 < _list114.size; ++_i115) {
                                final String _elem116 = iprot.readString();
                                struct.skewedColNames.add(_elem116);
                            }
                            iprot.readListEnd();
                            struct.setSkewedColNamesIsSet(true);
                            break;
                        }
                        TProtocolUtil.skip(iprot, schemeField.type);
                        break;
                    }
                    case 2: {
                        if (schemeField.type == 15) {
                            final TList _list115 = iprot.readListBegin();
                            struct.skewedColValues = (List<List<String>>)new ArrayList(_list115.size);
                            for (int _i116 = 0; _i116 < _list115.size; ++_i116) {
                                final TList _list116 = iprot.readListBegin();
                                final List<String> _elem117 = new ArrayList<String>(_list116.size);
                                for (int _i117 = 0; _i117 < _list116.size; ++_i117) {
                                    final String _elem118 = iprot.readString();
                                    _elem117.add(_elem118);
                                }
                                iprot.readListEnd();
                                struct.skewedColValues.add(_elem117);
                            }
                            iprot.readListEnd();
                            struct.setSkewedColValuesIsSet(true);
                            break;
                        }
                        TProtocolUtil.skip(iprot, schemeField.type);
                        break;
                    }
                    case 3: {
                        if (schemeField.type == 13) {
                            final TMap _map123 = iprot.readMapBegin();
                            struct.skewedColValueLocationMaps = (Map<List<String>, String>)new HashMap(2 * _map123.size);
                            for (int _i118 = 0; _i118 < _map123.size; ++_i118) {
                                final TList _list117 = iprot.readListBegin();
                                final List<String> _key125 = new ArrayList<String>(_list117.size);
                                for (int _i119 = 0; _i119 < _list117.size; ++_i119) {
                                    final String _elem119 = iprot.readString();
                                    _key125.add(_elem119);
                                }
                                iprot.readListEnd();
                                final String _val126 = iprot.readString();
                                struct.skewedColValueLocationMaps.put(_key125, _val126);
                            }
                            iprot.readMapEnd();
                            struct.setSkewedColValueLocationMapsIsSet(true);
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
        public void write(final TProtocol oprot, final SkewedInfo struct) throws TException {
            struct.validate();
            oprot.writeStructBegin(SkewedInfo.STRUCT_DESC);
            if (struct.skewedColNames != null) {
                oprot.writeFieldBegin(SkewedInfo.SKEWED_COL_NAMES_FIELD_DESC);
                oprot.writeListBegin(new TList((byte)11, struct.skewedColNames.size()));
                for (final String _iter130 : struct.skewedColNames) {
                    oprot.writeString(_iter130);
                }
                oprot.writeListEnd();
                oprot.writeFieldEnd();
            }
            if (struct.skewedColValues != null) {
                oprot.writeFieldBegin(SkewedInfo.SKEWED_COL_VALUES_FIELD_DESC);
                oprot.writeListBegin(new TList((byte)15, struct.skewedColValues.size()));
                for (final List<String> _iter131 : struct.skewedColValues) {
                    oprot.writeListBegin(new TList((byte)11, _iter131.size()));
                    for (final String _iter132 : _iter131) {
                        oprot.writeString(_iter132);
                    }
                    oprot.writeListEnd();
                }
                oprot.writeListEnd();
                oprot.writeFieldEnd();
            }
            if (struct.skewedColValueLocationMaps != null) {
                oprot.writeFieldBegin(SkewedInfo.SKEWED_COL_VALUE_LOCATION_MAPS_FIELD_DESC);
                oprot.writeMapBegin(new TMap((byte)15, (byte)11, struct.skewedColValueLocationMaps.size()));
                for (final Map.Entry<List<String>, String> _iter133 : struct.skewedColValueLocationMaps.entrySet()) {
                    oprot.writeListBegin(new TList((byte)11, _iter133.getKey().size()));
                    for (final String _iter134 : _iter133.getKey()) {
                        oprot.writeString(_iter134);
                    }
                    oprot.writeListEnd();
                    oprot.writeString(_iter133.getValue());
                }
                oprot.writeMapEnd();
                oprot.writeFieldEnd();
            }
            oprot.writeFieldStop();
            oprot.writeStructEnd();
        }
    }
    
    private static class SkewedInfoTupleSchemeFactory implements SchemeFactory
    {
        @Override
        public SkewedInfoTupleScheme getScheme() {
            return new SkewedInfoTupleScheme();
        }
    }
    
    private static class SkewedInfoTupleScheme extends TupleScheme<SkewedInfo>
    {
        @Override
        public void write(final TProtocol prot, final SkewedInfo struct) throws TException {
            final TTupleProtocol oprot = (TTupleProtocol)prot;
            final BitSet optionals = new BitSet();
            if (struct.isSetSkewedColNames()) {
                optionals.set(0);
            }
            if (struct.isSetSkewedColValues()) {
                optionals.set(1);
            }
            if (struct.isSetSkewedColValueLocationMaps()) {
                optionals.set(2);
            }
            oprot.writeBitSet(optionals, 3);
            if (struct.isSetSkewedColNames()) {
                oprot.writeI32(struct.skewedColNames.size());
                for (final String _iter135 : struct.skewedColNames) {
                    oprot.writeString(_iter135);
                }
            }
            if (struct.isSetSkewedColValues()) {
                oprot.writeI32(struct.skewedColValues.size());
                for (final List<String> _iter136 : struct.skewedColValues) {
                    oprot.writeI32(_iter136.size());
                    for (final String _iter137 : _iter136) {
                        oprot.writeString(_iter137);
                    }
                }
            }
            if (struct.isSetSkewedColValueLocationMaps()) {
                oprot.writeI32(struct.skewedColValueLocationMaps.size());
                for (final Map.Entry<List<String>, String> _iter138 : struct.skewedColValueLocationMaps.entrySet()) {
                    oprot.writeI32(_iter138.getKey().size());
                    for (final String _iter139 : _iter138.getKey()) {
                        oprot.writeString(_iter139);
                    }
                    oprot.writeString(_iter138.getValue());
                }
            }
        }
        
        @Override
        public void read(final TProtocol prot, final SkewedInfo struct) throws TException {
            final TTupleProtocol iprot = (TTupleProtocol)prot;
            final BitSet incoming = iprot.readBitSet(3);
            if (incoming.get(0)) {
                final TList _list140 = new TList((byte)11, iprot.readI32());
                struct.skewedColNames = (List<String>)new ArrayList(_list140.size);
                for (int _i141 = 0; _i141 < _list140.size; ++_i141) {
                    final String _elem142 = iprot.readString();
                    struct.skewedColNames.add(_elem142);
                }
                struct.setSkewedColNamesIsSet(true);
            }
            if (incoming.get(1)) {
                final TList _list141 = new TList((byte)15, iprot.readI32());
                struct.skewedColValues = (List<List<String>>)new ArrayList(_list141.size);
                for (int _i142 = 0; _i142 < _list141.size; ++_i142) {
                    final TList _list142 = new TList((byte)11, iprot.readI32());
                    final List<String> _elem143 = new ArrayList<String>(_list142.size);
                    for (int _i143 = 0; _i143 < _list142.size; ++_i143) {
                        final String _elem144 = iprot.readString();
                        _elem143.add(_elem144);
                    }
                    struct.skewedColValues.add(_elem143);
                }
                struct.setSkewedColValuesIsSet(true);
            }
            if (incoming.get(2)) {
                final TMap _map149 = new TMap((byte)15, (byte)11, iprot.readI32());
                struct.skewedColValueLocationMaps = (Map<List<String>, String>)new HashMap(2 * _map149.size);
                for (int _i144 = 0; _i144 < _map149.size; ++_i144) {
                    final TList _list143 = new TList((byte)11, iprot.readI32());
                    final List<String> _key151 = new ArrayList<String>(_list143.size);
                    for (int _i145 = 0; _i145 < _list143.size; ++_i145) {
                        final String _elem145 = iprot.readString();
                        _key151.add(_elem145);
                    }
                    final String _val152 = iprot.readString();
                    struct.skewedColValueLocationMaps.put(_key151, _val152);
                }
                struct.setSkewedColValueLocationMapsIsSet(true);
            }
        }
    }
}
