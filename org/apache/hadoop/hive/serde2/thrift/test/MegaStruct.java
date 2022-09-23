// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.hive.serde2.thrift.test;

import java.util.BitSet;
import org.apache.thrift.protocol.TTupleProtocol;
import org.apache.thrift.protocol.TSet;
import org.apache.thrift.protocol.TList;
import org.apache.thrift.protocol.TMap;
import org.apache.thrift.protocol.TProtocolUtil;
import java.util.EnumSet;
import java.util.Collections;
import org.apache.thrift.meta_data.SetMetaData;
import org.apache.thrift.meta_data.ListMetaData;
import org.apache.thrift.meta_data.StructMetaData;
import org.apache.thrift.TEnum;
import org.apache.thrift.meta_data.EnumMetaData;
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
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.thrift.EncodingUtils;
import java.util.Iterator;
import java.util.HashSet;
import java.util.ArrayList;
import java.util.HashMap;
import org.apache.thrift.TBaseHelper;
import org.apache.thrift.meta_data.FieldMetaData;
import java.util.Set;
import java.util.List;
import java.nio.ByteBuffer;
import org.apache.thrift.scheme.SchemeFactory;
import org.apache.thrift.scheme.IScheme;
import java.util.Map;
import org.apache.thrift.protocol.TField;
import org.apache.thrift.protocol.TStruct;
import java.io.Serializable;
import org.apache.thrift.TBase;

public class MegaStruct implements TBase<MegaStruct, _Fields>, Serializable, Cloneable
{
    private static final TStruct STRUCT_DESC;
    private static final TField MY_BOOL_FIELD_DESC;
    private static final TField MY_BYTE_FIELD_DESC;
    private static final TField MY_16BIT_INT_FIELD_DESC;
    private static final TField MY_32BIT_INT_FIELD_DESC;
    private static final TField MY_64BIT_INT_FIELD_DESC;
    private static final TField MY_DOUBLE_FIELD_DESC;
    private static final TField MY_STRING_FIELD_DESC;
    private static final TField MY_BINARY_FIELD_DESC;
    private static final TField MY_STRING_STRING_MAP_FIELD_DESC;
    private static final TField MY_STRING_ENUM_MAP_FIELD_DESC;
    private static final TField MY_ENUM_STRING_MAP_FIELD_DESC;
    private static final TField MY_ENUM_STRUCT_MAP_FIELD_DESC;
    private static final TField MY_ENUM_STRINGLIST_MAP_FIELD_DESC;
    private static final TField MY_ENUM_STRUCTLIST_MAP_FIELD_DESC;
    private static final TField MY_STRINGLIST_FIELD_DESC;
    private static final TField MY_STRUCTLIST_FIELD_DESC;
    private static final TField MY_ENUMLIST_FIELD_DESC;
    private static final TField MY_STRINGSET_FIELD_DESC;
    private static final TField MY_ENUMSET_FIELD_DESC;
    private static final TField MY_STRUCTSET_FIELD_DESC;
    private static final Map<Class<? extends IScheme>, SchemeFactory> schemes;
    private boolean my_bool;
    private byte my_byte;
    private short my_16bit_int;
    private int my_32bit_int;
    private long my_64bit_int;
    private double my_double;
    private String my_string;
    private ByteBuffer my_binary;
    private Map<String, String> my_string_string_map;
    private Map<String, MyEnum> my_string_enum_map;
    private Map<MyEnum, String> my_enum_string_map;
    private Map<MyEnum, MiniStruct> my_enum_struct_map;
    private Map<MyEnum, List<String>> my_enum_stringlist_map;
    private Map<MyEnum, List<MiniStruct>> my_enum_structlist_map;
    private List<String> my_stringlist;
    private List<MiniStruct> my_structlist;
    private List<MyEnum> my_enumlist;
    private Set<String> my_stringset;
    private Set<MyEnum> my_enumset;
    private Set<MiniStruct> my_structset;
    private static final int __MY_BOOL_ISSET_ID = 0;
    private static final int __MY_BYTE_ISSET_ID = 1;
    private static final int __MY_16BIT_INT_ISSET_ID = 2;
    private static final int __MY_32BIT_INT_ISSET_ID = 3;
    private static final int __MY_64BIT_INT_ISSET_ID = 4;
    private static final int __MY_DOUBLE_ISSET_ID = 5;
    private byte __isset_bitfield;
    private _Fields[] optionals;
    public static final Map<_Fields, FieldMetaData> metaDataMap;
    
    public MegaStruct() {
        this.__isset_bitfield = 0;
        this.optionals = new _Fields[] { _Fields.MY_BOOL, _Fields.MY_BYTE, _Fields.MY_16BIT_INT, _Fields.MY_32BIT_INT, _Fields.MY_64BIT_INT, _Fields.MY_DOUBLE, _Fields.MY_STRING, _Fields.MY_BINARY, _Fields.MY_STRING_STRING_MAP, _Fields.MY_STRING_ENUM_MAP, _Fields.MY_ENUM_STRING_MAP, _Fields.MY_ENUM_STRUCT_MAP, _Fields.MY_ENUM_STRINGLIST_MAP, _Fields.MY_ENUM_STRUCTLIST_MAP, _Fields.MY_STRINGLIST, _Fields.MY_STRUCTLIST, _Fields.MY_ENUMLIST, _Fields.MY_STRINGSET, _Fields.MY_ENUMSET, _Fields.MY_STRUCTSET };
    }
    
    public MegaStruct(final MegaStruct other) {
        this.__isset_bitfield = 0;
        this.optionals = new _Fields[] { _Fields.MY_BOOL, _Fields.MY_BYTE, _Fields.MY_16BIT_INT, _Fields.MY_32BIT_INT, _Fields.MY_64BIT_INT, _Fields.MY_DOUBLE, _Fields.MY_STRING, _Fields.MY_BINARY, _Fields.MY_STRING_STRING_MAP, _Fields.MY_STRING_ENUM_MAP, _Fields.MY_ENUM_STRING_MAP, _Fields.MY_ENUM_STRUCT_MAP, _Fields.MY_ENUM_STRINGLIST_MAP, _Fields.MY_ENUM_STRUCTLIST_MAP, _Fields.MY_STRINGLIST, _Fields.MY_STRUCTLIST, _Fields.MY_ENUMLIST, _Fields.MY_STRINGSET, _Fields.MY_ENUMSET, _Fields.MY_STRUCTSET };
        this.__isset_bitfield = other.__isset_bitfield;
        this.my_bool = other.my_bool;
        this.my_byte = other.my_byte;
        this.my_16bit_int = other.my_16bit_int;
        this.my_32bit_int = other.my_32bit_int;
        this.my_64bit_int = other.my_64bit_int;
        this.my_double = other.my_double;
        if (other.isSetMy_string()) {
            this.my_string = other.my_string;
        }
        if (other.isSetMy_binary()) {
            this.my_binary = TBaseHelper.copyBinary(other.my_binary);
        }
        if (other.isSetMy_string_string_map()) {
            final Map<String, String> __this__my_string_string_map = new HashMap<String, String>();
            for (final Map.Entry<String, String> other_element : other.my_string_string_map.entrySet()) {
                final String other_element_key = other_element.getKey();
                final String other_element_value = other_element.getValue();
                final String __this__my_string_string_map_copy_key = other_element_key;
                final String __this__my_string_string_map_copy_value = other_element_value;
                __this__my_string_string_map.put(__this__my_string_string_map_copy_key, __this__my_string_string_map_copy_value);
            }
            this.my_string_string_map = __this__my_string_string_map;
        }
        if (other.isSetMy_string_enum_map()) {
            final Map<String, MyEnum> __this__my_string_enum_map = new HashMap<String, MyEnum>();
            for (final Map.Entry<String, MyEnum> other_element2 : other.my_string_enum_map.entrySet()) {
                final String other_element_key = other_element2.getKey();
                final MyEnum other_element_value2 = other_element2.getValue();
                final String __this__my_string_enum_map_copy_key = other_element_key;
                final MyEnum __this__my_string_enum_map_copy_value = other_element_value2;
                __this__my_string_enum_map.put(__this__my_string_enum_map_copy_key, __this__my_string_enum_map_copy_value);
            }
            this.my_string_enum_map = __this__my_string_enum_map;
        }
        if (other.isSetMy_enum_string_map()) {
            final Map<MyEnum, String> __this__my_enum_string_map = new HashMap<MyEnum, String>();
            for (final Map.Entry<MyEnum, String> other_element3 : other.my_enum_string_map.entrySet()) {
                final MyEnum other_element_key2 = other_element3.getKey();
                final String other_element_value = other_element3.getValue();
                final MyEnum __this__my_enum_string_map_copy_key = other_element_key2;
                final String __this__my_enum_string_map_copy_value = other_element_value;
                __this__my_enum_string_map.put(__this__my_enum_string_map_copy_key, __this__my_enum_string_map_copy_value);
            }
            this.my_enum_string_map = __this__my_enum_string_map;
        }
        if (other.isSetMy_enum_struct_map()) {
            final Map<MyEnum, MiniStruct> __this__my_enum_struct_map = new HashMap<MyEnum, MiniStruct>();
            for (final Map.Entry<MyEnum, MiniStruct> other_element4 : other.my_enum_struct_map.entrySet()) {
                final MyEnum other_element_key2 = other_element4.getKey();
                final MiniStruct other_element_value3 = other_element4.getValue();
                final MyEnum __this__my_enum_struct_map_copy_key = other_element_key2;
                final MiniStruct __this__my_enum_struct_map_copy_value = new MiniStruct(other_element_value3);
                __this__my_enum_struct_map.put(__this__my_enum_struct_map_copy_key, __this__my_enum_struct_map_copy_value);
            }
            this.my_enum_struct_map = __this__my_enum_struct_map;
        }
        if (other.isSetMy_enum_stringlist_map()) {
            final Map<MyEnum, List<String>> __this__my_enum_stringlist_map = new HashMap<MyEnum, List<String>>();
            for (final Map.Entry<MyEnum, List<String>> other_element5 : other.my_enum_stringlist_map.entrySet()) {
                final MyEnum other_element_key2 = other_element5.getKey();
                final List<String> other_element_value4 = other_element5.getValue();
                final MyEnum __this__my_enum_stringlist_map_copy_key = other_element_key2;
                final List<String> __this__my_enum_stringlist_map_copy_value = new ArrayList<String>();
                for (final String other_element_value_element : other_element_value4) {
                    __this__my_enum_stringlist_map_copy_value.add(other_element_value_element);
                }
                __this__my_enum_stringlist_map.put(__this__my_enum_stringlist_map_copy_key, __this__my_enum_stringlist_map_copy_value);
            }
            this.my_enum_stringlist_map = __this__my_enum_stringlist_map;
        }
        if (other.isSetMy_enum_structlist_map()) {
            final Map<MyEnum, List<MiniStruct>> __this__my_enum_structlist_map = new HashMap<MyEnum, List<MiniStruct>>();
            for (final Map.Entry<MyEnum, List<MiniStruct>> other_element6 : other.my_enum_structlist_map.entrySet()) {
                final MyEnum other_element_key2 = other_element6.getKey();
                final List<MiniStruct> other_element_value5 = other_element6.getValue();
                final MyEnum __this__my_enum_structlist_map_copy_key = other_element_key2;
                final List<MiniStruct> __this__my_enum_structlist_map_copy_value = new ArrayList<MiniStruct>();
                for (final MiniStruct other_element_value_element2 : other_element_value5) {
                    __this__my_enum_structlist_map_copy_value.add(new MiniStruct(other_element_value_element2));
                }
                __this__my_enum_structlist_map.put(__this__my_enum_structlist_map_copy_key, __this__my_enum_structlist_map_copy_value);
            }
            this.my_enum_structlist_map = __this__my_enum_structlist_map;
        }
        if (other.isSetMy_stringlist()) {
            final List<String> __this__my_stringlist = new ArrayList<String>();
            for (final String other_element7 : other.my_stringlist) {
                __this__my_stringlist.add(other_element7);
            }
            this.my_stringlist = __this__my_stringlist;
        }
        if (other.isSetMy_structlist()) {
            final List<MiniStruct> __this__my_structlist = new ArrayList<MiniStruct>();
            for (final MiniStruct other_element8 : other.my_structlist) {
                __this__my_structlist.add(new MiniStruct(other_element8));
            }
            this.my_structlist = __this__my_structlist;
        }
        if (other.isSetMy_enumlist()) {
            final List<MyEnum> __this__my_enumlist = new ArrayList<MyEnum>();
            for (final MyEnum other_element9 : other.my_enumlist) {
                __this__my_enumlist.add(other_element9);
            }
            this.my_enumlist = __this__my_enumlist;
        }
        if (other.isSetMy_stringset()) {
            final Set<String> __this__my_stringset = new HashSet<String>();
            for (final String other_element7 : other.my_stringset) {
                __this__my_stringset.add(other_element7);
            }
            this.my_stringset = __this__my_stringset;
        }
        if (other.isSetMy_enumset()) {
            final Set<MyEnum> __this__my_enumset = new HashSet<MyEnum>();
            for (final MyEnum other_element9 : other.my_enumset) {
                __this__my_enumset.add(other_element9);
            }
            this.my_enumset = __this__my_enumset;
        }
        if (other.isSetMy_structset()) {
            final Set<MiniStruct> __this__my_structset = new HashSet<MiniStruct>();
            for (final MiniStruct other_element8 : other.my_structset) {
                __this__my_structset.add(new MiniStruct(other_element8));
            }
            this.my_structset = __this__my_structset;
        }
    }
    
    @Override
    public MegaStruct deepCopy() {
        return new MegaStruct(this);
    }
    
    @Override
    public void clear() {
        this.setMy_boolIsSet(false);
        this.setMy_byteIsSet(this.my_bool = false);
        this.my_byte = 0;
        this.setMy_16bit_intIsSet(false);
        this.my_16bit_int = 0;
        this.setMy_32bit_intIsSet(false);
        this.my_32bit_int = 0;
        this.setMy_64bit_intIsSet(false);
        this.my_64bit_int = 0L;
        this.setMy_doubleIsSet(false);
        this.my_double = 0.0;
        this.my_string = null;
        this.my_binary = null;
        this.my_string_string_map = null;
        this.my_string_enum_map = null;
        this.my_enum_string_map = null;
        this.my_enum_struct_map = null;
        this.my_enum_stringlist_map = null;
        this.my_enum_structlist_map = null;
        this.my_stringlist = null;
        this.my_structlist = null;
        this.my_enumlist = null;
        this.my_stringset = null;
        this.my_enumset = null;
        this.my_structset = null;
    }
    
    public boolean isMy_bool() {
        return this.my_bool;
    }
    
    public void setMy_bool(final boolean my_bool) {
        this.my_bool = my_bool;
        this.setMy_boolIsSet(true);
    }
    
    public void unsetMy_bool() {
        this.__isset_bitfield = EncodingUtils.clearBit(this.__isset_bitfield, 0);
    }
    
    public boolean isSetMy_bool() {
        return EncodingUtils.testBit(this.__isset_bitfield, 0);
    }
    
    public void setMy_boolIsSet(final boolean value) {
        this.__isset_bitfield = EncodingUtils.setBit(this.__isset_bitfield, 0, value);
    }
    
    public byte getMy_byte() {
        return this.my_byte;
    }
    
    public void setMy_byte(final byte my_byte) {
        this.my_byte = my_byte;
        this.setMy_byteIsSet(true);
    }
    
    public void unsetMy_byte() {
        this.__isset_bitfield = EncodingUtils.clearBit(this.__isset_bitfield, 1);
    }
    
    public boolean isSetMy_byte() {
        return EncodingUtils.testBit(this.__isset_bitfield, 1);
    }
    
    public void setMy_byteIsSet(final boolean value) {
        this.__isset_bitfield = EncodingUtils.setBit(this.__isset_bitfield, 1, value);
    }
    
    public short getMy_16bit_int() {
        return this.my_16bit_int;
    }
    
    public void setMy_16bit_int(final short my_16bit_int) {
        this.my_16bit_int = my_16bit_int;
        this.setMy_16bit_intIsSet(true);
    }
    
    public void unsetMy_16bit_int() {
        this.__isset_bitfield = EncodingUtils.clearBit(this.__isset_bitfield, 2);
    }
    
    public boolean isSetMy_16bit_int() {
        return EncodingUtils.testBit(this.__isset_bitfield, 2);
    }
    
    public void setMy_16bit_intIsSet(final boolean value) {
        this.__isset_bitfield = EncodingUtils.setBit(this.__isset_bitfield, 2, value);
    }
    
    public int getMy_32bit_int() {
        return this.my_32bit_int;
    }
    
    public void setMy_32bit_int(final int my_32bit_int) {
        this.my_32bit_int = my_32bit_int;
        this.setMy_32bit_intIsSet(true);
    }
    
    public void unsetMy_32bit_int() {
        this.__isset_bitfield = EncodingUtils.clearBit(this.__isset_bitfield, 3);
    }
    
    public boolean isSetMy_32bit_int() {
        return EncodingUtils.testBit(this.__isset_bitfield, 3);
    }
    
    public void setMy_32bit_intIsSet(final boolean value) {
        this.__isset_bitfield = EncodingUtils.setBit(this.__isset_bitfield, 3, value);
    }
    
    public long getMy_64bit_int() {
        return this.my_64bit_int;
    }
    
    public void setMy_64bit_int(final long my_64bit_int) {
        this.my_64bit_int = my_64bit_int;
        this.setMy_64bit_intIsSet(true);
    }
    
    public void unsetMy_64bit_int() {
        this.__isset_bitfield = EncodingUtils.clearBit(this.__isset_bitfield, 4);
    }
    
    public boolean isSetMy_64bit_int() {
        return EncodingUtils.testBit(this.__isset_bitfield, 4);
    }
    
    public void setMy_64bit_intIsSet(final boolean value) {
        this.__isset_bitfield = EncodingUtils.setBit(this.__isset_bitfield, 4, value);
    }
    
    public double getMy_double() {
        return this.my_double;
    }
    
    public void setMy_double(final double my_double) {
        this.my_double = my_double;
        this.setMy_doubleIsSet(true);
    }
    
    public void unsetMy_double() {
        this.__isset_bitfield = EncodingUtils.clearBit(this.__isset_bitfield, 5);
    }
    
    public boolean isSetMy_double() {
        return EncodingUtils.testBit(this.__isset_bitfield, 5);
    }
    
    public void setMy_doubleIsSet(final boolean value) {
        this.__isset_bitfield = EncodingUtils.setBit(this.__isset_bitfield, 5, value);
    }
    
    public String getMy_string() {
        return this.my_string;
    }
    
    public void setMy_string(final String my_string) {
        this.my_string = my_string;
    }
    
    public void unsetMy_string() {
        this.my_string = null;
    }
    
    public boolean isSetMy_string() {
        return this.my_string != null;
    }
    
    public void setMy_stringIsSet(final boolean value) {
        if (!value) {
            this.my_string = null;
        }
    }
    
    public byte[] getMy_binary() {
        this.setMy_binary(TBaseHelper.rightSize(this.my_binary));
        return (byte[])((this.my_binary == null) ? null : this.my_binary.array());
    }
    
    public ByteBuffer bufferForMy_binary() {
        return this.my_binary;
    }
    
    public void setMy_binary(final byte[] my_binary) {
        this.setMy_binary((my_binary == null) ? ((ByteBuffer)null) : ByteBuffer.wrap(my_binary));
    }
    
    public void setMy_binary(final ByteBuffer my_binary) {
        this.my_binary = my_binary;
    }
    
    public void unsetMy_binary() {
        this.my_binary = null;
    }
    
    public boolean isSetMy_binary() {
        return this.my_binary != null;
    }
    
    public void setMy_binaryIsSet(final boolean value) {
        if (!value) {
            this.my_binary = null;
        }
    }
    
    public int getMy_string_string_mapSize() {
        return (this.my_string_string_map == null) ? 0 : this.my_string_string_map.size();
    }
    
    public void putToMy_string_string_map(final String key, final String val) {
        if (this.my_string_string_map == null) {
            this.my_string_string_map = new HashMap<String, String>();
        }
        this.my_string_string_map.put(key, val);
    }
    
    public Map<String, String> getMy_string_string_map() {
        return this.my_string_string_map;
    }
    
    public void setMy_string_string_map(final Map<String, String> my_string_string_map) {
        this.my_string_string_map = my_string_string_map;
    }
    
    public void unsetMy_string_string_map() {
        this.my_string_string_map = null;
    }
    
    public boolean isSetMy_string_string_map() {
        return this.my_string_string_map != null;
    }
    
    public void setMy_string_string_mapIsSet(final boolean value) {
        if (!value) {
            this.my_string_string_map = null;
        }
    }
    
    public int getMy_string_enum_mapSize() {
        return (this.my_string_enum_map == null) ? 0 : this.my_string_enum_map.size();
    }
    
    public void putToMy_string_enum_map(final String key, final MyEnum val) {
        if (this.my_string_enum_map == null) {
            this.my_string_enum_map = new HashMap<String, MyEnum>();
        }
        this.my_string_enum_map.put(key, val);
    }
    
    public Map<String, MyEnum> getMy_string_enum_map() {
        return this.my_string_enum_map;
    }
    
    public void setMy_string_enum_map(final Map<String, MyEnum> my_string_enum_map) {
        this.my_string_enum_map = my_string_enum_map;
    }
    
    public void unsetMy_string_enum_map() {
        this.my_string_enum_map = null;
    }
    
    public boolean isSetMy_string_enum_map() {
        return this.my_string_enum_map != null;
    }
    
    public void setMy_string_enum_mapIsSet(final boolean value) {
        if (!value) {
            this.my_string_enum_map = null;
        }
    }
    
    public int getMy_enum_string_mapSize() {
        return (this.my_enum_string_map == null) ? 0 : this.my_enum_string_map.size();
    }
    
    public void putToMy_enum_string_map(final MyEnum key, final String val) {
        if (this.my_enum_string_map == null) {
            this.my_enum_string_map = new HashMap<MyEnum, String>();
        }
        this.my_enum_string_map.put(key, val);
    }
    
    public Map<MyEnum, String> getMy_enum_string_map() {
        return this.my_enum_string_map;
    }
    
    public void setMy_enum_string_map(final Map<MyEnum, String> my_enum_string_map) {
        this.my_enum_string_map = my_enum_string_map;
    }
    
    public void unsetMy_enum_string_map() {
        this.my_enum_string_map = null;
    }
    
    public boolean isSetMy_enum_string_map() {
        return this.my_enum_string_map != null;
    }
    
    public void setMy_enum_string_mapIsSet(final boolean value) {
        if (!value) {
            this.my_enum_string_map = null;
        }
    }
    
    public int getMy_enum_struct_mapSize() {
        return (this.my_enum_struct_map == null) ? 0 : this.my_enum_struct_map.size();
    }
    
    public void putToMy_enum_struct_map(final MyEnum key, final MiniStruct val) {
        if (this.my_enum_struct_map == null) {
            this.my_enum_struct_map = new HashMap<MyEnum, MiniStruct>();
        }
        this.my_enum_struct_map.put(key, val);
    }
    
    public Map<MyEnum, MiniStruct> getMy_enum_struct_map() {
        return this.my_enum_struct_map;
    }
    
    public void setMy_enum_struct_map(final Map<MyEnum, MiniStruct> my_enum_struct_map) {
        this.my_enum_struct_map = my_enum_struct_map;
    }
    
    public void unsetMy_enum_struct_map() {
        this.my_enum_struct_map = null;
    }
    
    public boolean isSetMy_enum_struct_map() {
        return this.my_enum_struct_map != null;
    }
    
    public void setMy_enum_struct_mapIsSet(final boolean value) {
        if (!value) {
            this.my_enum_struct_map = null;
        }
    }
    
    public int getMy_enum_stringlist_mapSize() {
        return (this.my_enum_stringlist_map == null) ? 0 : this.my_enum_stringlist_map.size();
    }
    
    public void putToMy_enum_stringlist_map(final MyEnum key, final List<String> val) {
        if (this.my_enum_stringlist_map == null) {
            this.my_enum_stringlist_map = new HashMap<MyEnum, List<String>>();
        }
        this.my_enum_stringlist_map.put(key, val);
    }
    
    public Map<MyEnum, List<String>> getMy_enum_stringlist_map() {
        return this.my_enum_stringlist_map;
    }
    
    public void setMy_enum_stringlist_map(final Map<MyEnum, List<String>> my_enum_stringlist_map) {
        this.my_enum_stringlist_map = my_enum_stringlist_map;
    }
    
    public void unsetMy_enum_stringlist_map() {
        this.my_enum_stringlist_map = null;
    }
    
    public boolean isSetMy_enum_stringlist_map() {
        return this.my_enum_stringlist_map != null;
    }
    
    public void setMy_enum_stringlist_mapIsSet(final boolean value) {
        if (!value) {
            this.my_enum_stringlist_map = null;
        }
    }
    
    public int getMy_enum_structlist_mapSize() {
        return (this.my_enum_structlist_map == null) ? 0 : this.my_enum_structlist_map.size();
    }
    
    public void putToMy_enum_structlist_map(final MyEnum key, final List<MiniStruct> val) {
        if (this.my_enum_structlist_map == null) {
            this.my_enum_structlist_map = new HashMap<MyEnum, List<MiniStruct>>();
        }
        this.my_enum_structlist_map.put(key, val);
    }
    
    public Map<MyEnum, List<MiniStruct>> getMy_enum_structlist_map() {
        return this.my_enum_structlist_map;
    }
    
    public void setMy_enum_structlist_map(final Map<MyEnum, List<MiniStruct>> my_enum_structlist_map) {
        this.my_enum_structlist_map = my_enum_structlist_map;
    }
    
    public void unsetMy_enum_structlist_map() {
        this.my_enum_structlist_map = null;
    }
    
    public boolean isSetMy_enum_structlist_map() {
        return this.my_enum_structlist_map != null;
    }
    
    public void setMy_enum_structlist_mapIsSet(final boolean value) {
        if (!value) {
            this.my_enum_structlist_map = null;
        }
    }
    
    public int getMy_stringlistSize() {
        return (this.my_stringlist == null) ? 0 : this.my_stringlist.size();
    }
    
    public Iterator<String> getMy_stringlistIterator() {
        return (this.my_stringlist == null) ? null : this.my_stringlist.iterator();
    }
    
    public void addToMy_stringlist(final String elem) {
        if (this.my_stringlist == null) {
            this.my_stringlist = new ArrayList<String>();
        }
        this.my_stringlist.add(elem);
    }
    
    public List<String> getMy_stringlist() {
        return this.my_stringlist;
    }
    
    public void setMy_stringlist(final List<String> my_stringlist) {
        this.my_stringlist = my_stringlist;
    }
    
    public void unsetMy_stringlist() {
        this.my_stringlist = null;
    }
    
    public boolean isSetMy_stringlist() {
        return this.my_stringlist != null;
    }
    
    public void setMy_stringlistIsSet(final boolean value) {
        if (!value) {
            this.my_stringlist = null;
        }
    }
    
    public int getMy_structlistSize() {
        return (this.my_structlist == null) ? 0 : this.my_structlist.size();
    }
    
    public Iterator<MiniStruct> getMy_structlistIterator() {
        return (this.my_structlist == null) ? null : this.my_structlist.iterator();
    }
    
    public void addToMy_structlist(final MiniStruct elem) {
        if (this.my_structlist == null) {
            this.my_structlist = new ArrayList<MiniStruct>();
        }
        this.my_structlist.add(elem);
    }
    
    public List<MiniStruct> getMy_structlist() {
        return this.my_structlist;
    }
    
    public void setMy_structlist(final List<MiniStruct> my_structlist) {
        this.my_structlist = my_structlist;
    }
    
    public void unsetMy_structlist() {
        this.my_structlist = null;
    }
    
    public boolean isSetMy_structlist() {
        return this.my_structlist != null;
    }
    
    public void setMy_structlistIsSet(final boolean value) {
        if (!value) {
            this.my_structlist = null;
        }
    }
    
    public int getMy_enumlistSize() {
        return (this.my_enumlist == null) ? 0 : this.my_enumlist.size();
    }
    
    public Iterator<MyEnum> getMy_enumlistIterator() {
        return (this.my_enumlist == null) ? null : this.my_enumlist.iterator();
    }
    
    public void addToMy_enumlist(final MyEnum elem) {
        if (this.my_enumlist == null) {
            this.my_enumlist = new ArrayList<MyEnum>();
        }
        this.my_enumlist.add(elem);
    }
    
    public List<MyEnum> getMy_enumlist() {
        return this.my_enumlist;
    }
    
    public void setMy_enumlist(final List<MyEnum> my_enumlist) {
        this.my_enumlist = my_enumlist;
    }
    
    public void unsetMy_enumlist() {
        this.my_enumlist = null;
    }
    
    public boolean isSetMy_enumlist() {
        return this.my_enumlist != null;
    }
    
    public void setMy_enumlistIsSet(final boolean value) {
        if (!value) {
            this.my_enumlist = null;
        }
    }
    
    public int getMy_stringsetSize() {
        return (this.my_stringset == null) ? 0 : this.my_stringset.size();
    }
    
    public Iterator<String> getMy_stringsetIterator() {
        return (this.my_stringset == null) ? null : this.my_stringset.iterator();
    }
    
    public void addToMy_stringset(final String elem) {
        if (this.my_stringset == null) {
            this.my_stringset = new HashSet<String>();
        }
        this.my_stringset.add(elem);
    }
    
    public Set<String> getMy_stringset() {
        return this.my_stringset;
    }
    
    public void setMy_stringset(final Set<String> my_stringset) {
        this.my_stringset = my_stringset;
    }
    
    public void unsetMy_stringset() {
        this.my_stringset = null;
    }
    
    public boolean isSetMy_stringset() {
        return this.my_stringset != null;
    }
    
    public void setMy_stringsetIsSet(final boolean value) {
        if (!value) {
            this.my_stringset = null;
        }
    }
    
    public int getMy_enumsetSize() {
        return (this.my_enumset == null) ? 0 : this.my_enumset.size();
    }
    
    public Iterator<MyEnum> getMy_enumsetIterator() {
        return (this.my_enumset == null) ? null : this.my_enumset.iterator();
    }
    
    public void addToMy_enumset(final MyEnum elem) {
        if (this.my_enumset == null) {
            this.my_enumset = new HashSet<MyEnum>();
        }
        this.my_enumset.add(elem);
    }
    
    public Set<MyEnum> getMy_enumset() {
        return this.my_enumset;
    }
    
    public void setMy_enumset(final Set<MyEnum> my_enumset) {
        this.my_enumset = my_enumset;
    }
    
    public void unsetMy_enumset() {
        this.my_enumset = null;
    }
    
    public boolean isSetMy_enumset() {
        return this.my_enumset != null;
    }
    
    public void setMy_enumsetIsSet(final boolean value) {
        if (!value) {
            this.my_enumset = null;
        }
    }
    
    public int getMy_structsetSize() {
        return (this.my_structset == null) ? 0 : this.my_structset.size();
    }
    
    public Iterator<MiniStruct> getMy_structsetIterator() {
        return (this.my_structset == null) ? null : this.my_structset.iterator();
    }
    
    public void addToMy_structset(final MiniStruct elem) {
        if (this.my_structset == null) {
            this.my_structset = new HashSet<MiniStruct>();
        }
        this.my_structset.add(elem);
    }
    
    public Set<MiniStruct> getMy_structset() {
        return this.my_structset;
    }
    
    public void setMy_structset(final Set<MiniStruct> my_structset) {
        this.my_structset = my_structset;
    }
    
    public void unsetMy_structset() {
        this.my_structset = null;
    }
    
    public boolean isSetMy_structset() {
        return this.my_structset != null;
    }
    
    public void setMy_structsetIsSet(final boolean value) {
        if (!value) {
            this.my_structset = null;
        }
    }
    
    @Override
    public void setFieldValue(final _Fields field, final Object value) {
        switch (field) {
            case MY_BOOL: {
                if (value == null) {
                    this.unsetMy_bool();
                    break;
                }
                this.setMy_bool((boolean)value);
                break;
            }
            case MY_BYTE: {
                if (value == null) {
                    this.unsetMy_byte();
                    break;
                }
                this.setMy_byte((byte)value);
                break;
            }
            case MY_16BIT_INT: {
                if (value == null) {
                    this.unsetMy_16bit_int();
                    break;
                }
                this.setMy_16bit_int((short)value);
                break;
            }
            case MY_32BIT_INT: {
                if (value == null) {
                    this.unsetMy_32bit_int();
                    break;
                }
                this.setMy_32bit_int((int)value);
                break;
            }
            case MY_64BIT_INT: {
                if (value == null) {
                    this.unsetMy_64bit_int();
                    break;
                }
                this.setMy_64bit_int((long)value);
                break;
            }
            case MY_DOUBLE: {
                if (value == null) {
                    this.unsetMy_double();
                    break;
                }
                this.setMy_double((double)value);
                break;
            }
            case MY_STRING: {
                if (value == null) {
                    this.unsetMy_string();
                    break;
                }
                this.setMy_string((String)value);
                break;
            }
            case MY_BINARY: {
                if (value == null) {
                    this.unsetMy_binary();
                    break;
                }
                this.setMy_binary((ByteBuffer)value);
                break;
            }
            case MY_STRING_STRING_MAP: {
                if (value == null) {
                    this.unsetMy_string_string_map();
                    break;
                }
                this.setMy_string_string_map((Map<String, String>)value);
                break;
            }
            case MY_STRING_ENUM_MAP: {
                if (value == null) {
                    this.unsetMy_string_enum_map();
                    break;
                }
                this.setMy_string_enum_map((Map<String, MyEnum>)value);
                break;
            }
            case MY_ENUM_STRING_MAP: {
                if (value == null) {
                    this.unsetMy_enum_string_map();
                    break;
                }
                this.setMy_enum_string_map((Map<MyEnum, String>)value);
                break;
            }
            case MY_ENUM_STRUCT_MAP: {
                if (value == null) {
                    this.unsetMy_enum_struct_map();
                    break;
                }
                this.setMy_enum_struct_map((Map<MyEnum, MiniStruct>)value);
                break;
            }
            case MY_ENUM_STRINGLIST_MAP: {
                if (value == null) {
                    this.unsetMy_enum_stringlist_map();
                    break;
                }
                this.setMy_enum_stringlist_map((Map<MyEnum, List<String>>)value);
                break;
            }
            case MY_ENUM_STRUCTLIST_MAP: {
                if (value == null) {
                    this.unsetMy_enum_structlist_map();
                    break;
                }
                this.setMy_enum_structlist_map((Map<MyEnum, List<MiniStruct>>)value);
                break;
            }
            case MY_STRINGLIST: {
                if (value == null) {
                    this.unsetMy_stringlist();
                    break;
                }
                this.setMy_stringlist((List<String>)value);
                break;
            }
            case MY_STRUCTLIST: {
                if (value == null) {
                    this.unsetMy_structlist();
                    break;
                }
                this.setMy_structlist((List<MiniStruct>)value);
                break;
            }
            case MY_ENUMLIST: {
                if (value == null) {
                    this.unsetMy_enumlist();
                    break;
                }
                this.setMy_enumlist((List<MyEnum>)value);
                break;
            }
            case MY_STRINGSET: {
                if (value == null) {
                    this.unsetMy_stringset();
                    break;
                }
                this.setMy_stringset((Set<String>)value);
                break;
            }
            case MY_ENUMSET: {
                if (value == null) {
                    this.unsetMy_enumset();
                    break;
                }
                this.setMy_enumset((Set<MyEnum>)value);
                break;
            }
            case MY_STRUCTSET: {
                if (value == null) {
                    this.unsetMy_structset();
                    break;
                }
                this.setMy_structset((Set<MiniStruct>)value);
                break;
            }
        }
    }
    
    @Override
    public Object getFieldValue(final _Fields field) {
        switch (field) {
            case MY_BOOL: {
                return this.isMy_bool();
            }
            case MY_BYTE: {
                return this.getMy_byte();
            }
            case MY_16BIT_INT: {
                return this.getMy_16bit_int();
            }
            case MY_32BIT_INT: {
                return this.getMy_32bit_int();
            }
            case MY_64BIT_INT: {
                return this.getMy_64bit_int();
            }
            case MY_DOUBLE: {
                return this.getMy_double();
            }
            case MY_STRING: {
                return this.getMy_string();
            }
            case MY_BINARY: {
                return this.getMy_binary();
            }
            case MY_STRING_STRING_MAP: {
                return this.getMy_string_string_map();
            }
            case MY_STRING_ENUM_MAP: {
                return this.getMy_string_enum_map();
            }
            case MY_ENUM_STRING_MAP: {
                return this.getMy_enum_string_map();
            }
            case MY_ENUM_STRUCT_MAP: {
                return this.getMy_enum_struct_map();
            }
            case MY_ENUM_STRINGLIST_MAP: {
                return this.getMy_enum_stringlist_map();
            }
            case MY_ENUM_STRUCTLIST_MAP: {
                return this.getMy_enum_structlist_map();
            }
            case MY_STRINGLIST: {
                return this.getMy_stringlist();
            }
            case MY_STRUCTLIST: {
                return this.getMy_structlist();
            }
            case MY_ENUMLIST: {
                return this.getMy_enumlist();
            }
            case MY_STRINGSET: {
                return this.getMy_stringset();
            }
            case MY_ENUMSET: {
                return this.getMy_enumset();
            }
            case MY_STRUCTSET: {
                return this.getMy_structset();
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
            case MY_BOOL: {
                return this.isSetMy_bool();
            }
            case MY_BYTE: {
                return this.isSetMy_byte();
            }
            case MY_16BIT_INT: {
                return this.isSetMy_16bit_int();
            }
            case MY_32BIT_INT: {
                return this.isSetMy_32bit_int();
            }
            case MY_64BIT_INT: {
                return this.isSetMy_64bit_int();
            }
            case MY_DOUBLE: {
                return this.isSetMy_double();
            }
            case MY_STRING: {
                return this.isSetMy_string();
            }
            case MY_BINARY: {
                return this.isSetMy_binary();
            }
            case MY_STRING_STRING_MAP: {
                return this.isSetMy_string_string_map();
            }
            case MY_STRING_ENUM_MAP: {
                return this.isSetMy_string_enum_map();
            }
            case MY_ENUM_STRING_MAP: {
                return this.isSetMy_enum_string_map();
            }
            case MY_ENUM_STRUCT_MAP: {
                return this.isSetMy_enum_struct_map();
            }
            case MY_ENUM_STRINGLIST_MAP: {
                return this.isSetMy_enum_stringlist_map();
            }
            case MY_ENUM_STRUCTLIST_MAP: {
                return this.isSetMy_enum_structlist_map();
            }
            case MY_STRINGLIST: {
                return this.isSetMy_stringlist();
            }
            case MY_STRUCTLIST: {
                return this.isSetMy_structlist();
            }
            case MY_ENUMLIST: {
                return this.isSetMy_enumlist();
            }
            case MY_STRINGSET: {
                return this.isSetMy_stringset();
            }
            case MY_ENUMSET: {
                return this.isSetMy_enumset();
            }
            case MY_STRUCTSET: {
                return this.isSetMy_structset();
            }
            default: {
                throw new IllegalStateException();
            }
        }
    }
    
    @Override
    public boolean equals(final Object that) {
        return that != null && that instanceof MegaStruct && this.equals((MegaStruct)that);
    }
    
    public boolean equals(final MegaStruct that) {
        if (that == null) {
            return false;
        }
        final boolean this_present_my_bool = this.isSetMy_bool();
        final boolean that_present_my_bool = that.isSetMy_bool();
        if (this_present_my_bool || that_present_my_bool) {
            if (!this_present_my_bool || !that_present_my_bool) {
                return false;
            }
            if (this.my_bool != that.my_bool) {
                return false;
            }
        }
        final boolean this_present_my_byte = this.isSetMy_byte();
        final boolean that_present_my_byte = that.isSetMy_byte();
        if (this_present_my_byte || that_present_my_byte) {
            if (!this_present_my_byte || !that_present_my_byte) {
                return false;
            }
            if (this.my_byte != that.my_byte) {
                return false;
            }
        }
        final boolean this_present_my_16bit_int = this.isSetMy_16bit_int();
        final boolean that_present_my_16bit_int = that.isSetMy_16bit_int();
        if (this_present_my_16bit_int || that_present_my_16bit_int) {
            if (!this_present_my_16bit_int || !that_present_my_16bit_int) {
                return false;
            }
            if (this.my_16bit_int != that.my_16bit_int) {
                return false;
            }
        }
        final boolean this_present_my_32bit_int = this.isSetMy_32bit_int();
        final boolean that_present_my_32bit_int = that.isSetMy_32bit_int();
        if (this_present_my_32bit_int || that_present_my_32bit_int) {
            if (!this_present_my_32bit_int || !that_present_my_32bit_int) {
                return false;
            }
            if (this.my_32bit_int != that.my_32bit_int) {
                return false;
            }
        }
        final boolean this_present_my_64bit_int = this.isSetMy_64bit_int();
        final boolean that_present_my_64bit_int = that.isSetMy_64bit_int();
        if (this_present_my_64bit_int || that_present_my_64bit_int) {
            if (!this_present_my_64bit_int || !that_present_my_64bit_int) {
                return false;
            }
            if (this.my_64bit_int != that.my_64bit_int) {
                return false;
            }
        }
        final boolean this_present_my_double = this.isSetMy_double();
        final boolean that_present_my_double = that.isSetMy_double();
        if (this_present_my_double || that_present_my_double) {
            if (!this_present_my_double || !that_present_my_double) {
                return false;
            }
            if (this.my_double != that.my_double) {
                return false;
            }
        }
        final boolean this_present_my_string = this.isSetMy_string();
        final boolean that_present_my_string = that.isSetMy_string();
        if (this_present_my_string || that_present_my_string) {
            if (!this_present_my_string || !that_present_my_string) {
                return false;
            }
            if (!this.my_string.equals(that.my_string)) {
                return false;
            }
        }
        final boolean this_present_my_binary = this.isSetMy_binary();
        final boolean that_present_my_binary = that.isSetMy_binary();
        if (this_present_my_binary || that_present_my_binary) {
            if (!this_present_my_binary || !that_present_my_binary) {
                return false;
            }
            if (!this.my_binary.equals(that.my_binary)) {
                return false;
            }
        }
        final boolean this_present_my_string_string_map = this.isSetMy_string_string_map();
        final boolean that_present_my_string_string_map = that.isSetMy_string_string_map();
        if (this_present_my_string_string_map || that_present_my_string_string_map) {
            if (!this_present_my_string_string_map || !that_present_my_string_string_map) {
                return false;
            }
            if (!this.my_string_string_map.equals(that.my_string_string_map)) {
                return false;
            }
        }
        final boolean this_present_my_string_enum_map = this.isSetMy_string_enum_map();
        final boolean that_present_my_string_enum_map = that.isSetMy_string_enum_map();
        if (this_present_my_string_enum_map || that_present_my_string_enum_map) {
            if (!this_present_my_string_enum_map || !that_present_my_string_enum_map) {
                return false;
            }
            if (!this.my_string_enum_map.equals(that.my_string_enum_map)) {
                return false;
            }
        }
        final boolean this_present_my_enum_string_map = this.isSetMy_enum_string_map();
        final boolean that_present_my_enum_string_map = that.isSetMy_enum_string_map();
        if (this_present_my_enum_string_map || that_present_my_enum_string_map) {
            if (!this_present_my_enum_string_map || !that_present_my_enum_string_map) {
                return false;
            }
            if (!this.my_enum_string_map.equals(that.my_enum_string_map)) {
                return false;
            }
        }
        final boolean this_present_my_enum_struct_map = this.isSetMy_enum_struct_map();
        final boolean that_present_my_enum_struct_map = that.isSetMy_enum_struct_map();
        if (this_present_my_enum_struct_map || that_present_my_enum_struct_map) {
            if (!this_present_my_enum_struct_map || !that_present_my_enum_struct_map) {
                return false;
            }
            if (!this.my_enum_struct_map.equals(that.my_enum_struct_map)) {
                return false;
            }
        }
        final boolean this_present_my_enum_stringlist_map = this.isSetMy_enum_stringlist_map();
        final boolean that_present_my_enum_stringlist_map = that.isSetMy_enum_stringlist_map();
        if (this_present_my_enum_stringlist_map || that_present_my_enum_stringlist_map) {
            if (!this_present_my_enum_stringlist_map || !that_present_my_enum_stringlist_map) {
                return false;
            }
            if (!this.my_enum_stringlist_map.equals(that.my_enum_stringlist_map)) {
                return false;
            }
        }
        final boolean this_present_my_enum_structlist_map = this.isSetMy_enum_structlist_map();
        final boolean that_present_my_enum_structlist_map = that.isSetMy_enum_structlist_map();
        if (this_present_my_enum_structlist_map || that_present_my_enum_structlist_map) {
            if (!this_present_my_enum_structlist_map || !that_present_my_enum_structlist_map) {
                return false;
            }
            if (!this.my_enum_structlist_map.equals(that.my_enum_structlist_map)) {
                return false;
            }
        }
        final boolean this_present_my_stringlist = this.isSetMy_stringlist();
        final boolean that_present_my_stringlist = that.isSetMy_stringlist();
        if (this_present_my_stringlist || that_present_my_stringlist) {
            if (!this_present_my_stringlist || !that_present_my_stringlist) {
                return false;
            }
            if (!this.my_stringlist.equals(that.my_stringlist)) {
                return false;
            }
        }
        final boolean this_present_my_structlist = this.isSetMy_structlist();
        final boolean that_present_my_structlist = that.isSetMy_structlist();
        if (this_present_my_structlist || that_present_my_structlist) {
            if (!this_present_my_structlist || !that_present_my_structlist) {
                return false;
            }
            if (!this.my_structlist.equals(that.my_structlist)) {
                return false;
            }
        }
        final boolean this_present_my_enumlist = this.isSetMy_enumlist();
        final boolean that_present_my_enumlist = that.isSetMy_enumlist();
        if (this_present_my_enumlist || that_present_my_enumlist) {
            if (!this_present_my_enumlist || !that_present_my_enumlist) {
                return false;
            }
            if (!this.my_enumlist.equals(that.my_enumlist)) {
                return false;
            }
        }
        final boolean this_present_my_stringset = this.isSetMy_stringset();
        final boolean that_present_my_stringset = that.isSetMy_stringset();
        if (this_present_my_stringset || that_present_my_stringset) {
            if (!this_present_my_stringset || !that_present_my_stringset) {
                return false;
            }
            if (!this.my_stringset.equals(that.my_stringset)) {
                return false;
            }
        }
        final boolean this_present_my_enumset = this.isSetMy_enumset();
        final boolean that_present_my_enumset = that.isSetMy_enumset();
        if (this_present_my_enumset || that_present_my_enumset) {
            if (!this_present_my_enumset || !that_present_my_enumset) {
                return false;
            }
            if (!this.my_enumset.equals(that.my_enumset)) {
                return false;
            }
        }
        final boolean this_present_my_structset = this.isSetMy_structset();
        final boolean that_present_my_structset = that.isSetMy_structset();
        if (this_present_my_structset || that_present_my_structset) {
            if (!this_present_my_structset || !that_present_my_structset) {
                return false;
            }
            if (!this.my_structset.equals(that.my_structset)) {
                return false;
            }
        }
        return true;
    }
    
    @Override
    public int hashCode() {
        final HashCodeBuilder builder = new HashCodeBuilder();
        final boolean present_my_bool = this.isSetMy_bool();
        builder.append(present_my_bool);
        if (present_my_bool) {
            builder.append(this.my_bool);
        }
        final boolean present_my_byte = this.isSetMy_byte();
        builder.append(present_my_byte);
        if (present_my_byte) {
            builder.append(this.my_byte);
        }
        final boolean present_my_16bit_int = this.isSetMy_16bit_int();
        builder.append(present_my_16bit_int);
        if (present_my_16bit_int) {
            builder.append(this.my_16bit_int);
        }
        final boolean present_my_32bit_int = this.isSetMy_32bit_int();
        builder.append(present_my_32bit_int);
        if (present_my_32bit_int) {
            builder.append(this.my_32bit_int);
        }
        final boolean present_my_64bit_int = this.isSetMy_64bit_int();
        builder.append(present_my_64bit_int);
        if (present_my_64bit_int) {
            builder.append(this.my_64bit_int);
        }
        final boolean present_my_double = this.isSetMy_double();
        builder.append(present_my_double);
        if (present_my_double) {
            builder.append(this.my_double);
        }
        final boolean present_my_string = this.isSetMy_string();
        builder.append(present_my_string);
        if (present_my_string) {
            builder.append(this.my_string);
        }
        final boolean present_my_binary = this.isSetMy_binary();
        builder.append(present_my_binary);
        if (present_my_binary) {
            builder.append(this.my_binary);
        }
        final boolean present_my_string_string_map = this.isSetMy_string_string_map();
        builder.append(present_my_string_string_map);
        if (present_my_string_string_map) {
            builder.append(this.my_string_string_map);
        }
        final boolean present_my_string_enum_map = this.isSetMy_string_enum_map();
        builder.append(present_my_string_enum_map);
        if (present_my_string_enum_map) {
            builder.append(this.my_string_enum_map);
        }
        final boolean present_my_enum_string_map = this.isSetMy_enum_string_map();
        builder.append(present_my_enum_string_map);
        if (present_my_enum_string_map) {
            builder.append(this.my_enum_string_map);
        }
        final boolean present_my_enum_struct_map = this.isSetMy_enum_struct_map();
        builder.append(present_my_enum_struct_map);
        if (present_my_enum_struct_map) {
            builder.append(this.my_enum_struct_map);
        }
        final boolean present_my_enum_stringlist_map = this.isSetMy_enum_stringlist_map();
        builder.append(present_my_enum_stringlist_map);
        if (present_my_enum_stringlist_map) {
            builder.append(this.my_enum_stringlist_map);
        }
        final boolean present_my_enum_structlist_map = this.isSetMy_enum_structlist_map();
        builder.append(present_my_enum_structlist_map);
        if (present_my_enum_structlist_map) {
            builder.append(this.my_enum_structlist_map);
        }
        final boolean present_my_stringlist = this.isSetMy_stringlist();
        builder.append(present_my_stringlist);
        if (present_my_stringlist) {
            builder.append(this.my_stringlist);
        }
        final boolean present_my_structlist = this.isSetMy_structlist();
        builder.append(present_my_structlist);
        if (present_my_structlist) {
            builder.append(this.my_structlist);
        }
        final boolean present_my_enumlist = this.isSetMy_enumlist();
        builder.append(present_my_enumlist);
        if (present_my_enumlist) {
            builder.append(this.my_enumlist);
        }
        final boolean present_my_stringset = this.isSetMy_stringset();
        builder.append(present_my_stringset);
        if (present_my_stringset) {
            builder.append(this.my_stringset);
        }
        final boolean present_my_enumset = this.isSetMy_enumset();
        builder.append(present_my_enumset);
        if (present_my_enumset) {
            builder.append(this.my_enumset);
        }
        final boolean present_my_structset = this.isSetMy_structset();
        builder.append(present_my_structset);
        if (present_my_structset) {
            builder.append(this.my_structset);
        }
        return builder.toHashCode();
    }
    
    @Override
    public int compareTo(final MegaStruct other) {
        if (!this.getClass().equals(other.getClass())) {
            return this.getClass().getName().compareTo(other.getClass().getName());
        }
        int lastComparison = 0;
        final MegaStruct typedOther = other;
        lastComparison = Boolean.valueOf(this.isSetMy_bool()).compareTo(Boolean.valueOf(typedOther.isSetMy_bool()));
        if (lastComparison != 0) {
            return lastComparison;
        }
        if (this.isSetMy_bool()) {
            lastComparison = TBaseHelper.compareTo(this.my_bool, typedOther.my_bool);
            if (lastComparison != 0) {
                return lastComparison;
            }
        }
        lastComparison = Boolean.valueOf(this.isSetMy_byte()).compareTo(Boolean.valueOf(typedOther.isSetMy_byte()));
        if (lastComparison != 0) {
            return lastComparison;
        }
        if (this.isSetMy_byte()) {
            lastComparison = TBaseHelper.compareTo(this.my_byte, typedOther.my_byte);
            if (lastComparison != 0) {
                return lastComparison;
            }
        }
        lastComparison = Boolean.valueOf(this.isSetMy_16bit_int()).compareTo(Boolean.valueOf(typedOther.isSetMy_16bit_int()));
        if (lastComparison != 0) {
            return lastComparison;
        }
        if (this.isSetMy_16bit_int()) {
            lastComparison = TBaseHelper.compareTo(this.my_16bit_int, typedOther.my_16bit_int);
            if (lastComparison != 0) {
                return lastComparison;
            }
        }
        lastComparison = Boolean.valueOf(this.isSetMy_32bit_int()).compareTo(Boolean.valueOf(typedOther.isSetMy_32bit_int()));
        if (lastComparison != 0) {
            return lastComparison;
        }
        if (this.isSetMy_32bit_int()) {
            lastComparison = TBaseHelper.compareTo(this.my_32bit_int, typedOther.my_32bit_int);
            if (lastComparison != 0) {
                return lastComparison;
            }
        }
        lastComparison = Boolean.valueOf(this.isSetMy_64bit_int()).compareTo(Boolean.valueOf(typedOther.isSetMy_64bit_int()));
        if (lastComparison != 0) {
            return lastComparison;
        }
        if (this.isSetMy_64bit_int()) {
            lastComparison = TBaseHelper.compareTo(this.my_64bit_int, typedOther.my_64bit_int);
            if (lastComparison != 0) {
                return lastComparison;
            }
        }
        lastComparison = Boolean.valueOf(this.isSetMy_double()).compareTo(Boolean.valueOf(typedOther.isSetMy_double()));
        if (lastComparison != 0) {
            return lastComparison;
        }
        if (this.isSetMy_double()) {
            lastComparison = TBaseHelper.compareTo(this.my_double, typedOther.my_double);
            if (lastComparison != 0) {
                return lastComparison;
            }
        }
        lastComparison = Boolean.valueOf(this.isSetMy_string()).compareTo(Boolean.valueOf(typedOther.isSetMy_string()));
        if (lastComparison != 0) {
            return lastComparison;
        }
        if (this.isSetMy_string()) {
            lastComparison = TBaseHelper.compareTo(this.my_string, typedOther.my_string);
            if (lastComparison != 0) {
                return lastComparison;
            }
        }
        lastComparison = Boolean.valueOf(this.isSetMy_binary()).compareTo(Boolean.valueOf(typedOther.isSetMy_binary()));
        if (lastComparison != 0) {
            return lastComparison;
        }
        if (this.isSetMy_binary()) {
            lastComparison = TBaseHelper.compareTo(this.my_binary, typedOther.my_binary);
            if (lastComparison != 0) {
                return lastComparison;
            }
        }
        lastComparison = Boolean.valueOf(this.isSetMy_string_string_map()).compareTo(Boolean.valueOf(typedOther.isSetMy_string_string_map()));
        if (lastComparison != 0) {
            return lastComparison;
        }
        if (this.isSetMy_string_string_map()) {
            lastComparison = TBaseHelper.compareTo(this.my_string_string_map, typedOther.my_string_string_map);
            if (lastComparison != 0) {
                return lastComparison;
            }
        }
        lastComparison = Boolean.valueOf(this.isSetMy_string_enum_map()).compareTo(Boolean.valueOf(typedOther.isSetMy_string_enum_map()));
        if (lastComparison != 0) {
            return lastComparison;
        }
        if (this.isSetMy_string_enum_map()) {
            lastComparison = TBaseHelper.compareTo(this.my_string_enum_map, typedOther.my_string_enum_map);
            if (lastComparison != 0) {
                return lastComparison;
            }
        }
        lastComparison = Boolean.valueOf(this.isSetMy_enum_string_map()).compareTo(Boolean.valueOf(typedOther.isSetMy_enum_string_map()));
        if (lastComparison != 0) {
            return lastComparison;
        }
        if (this.isSetMy_enum_string_map()) {
            lastComparison = TBaseHelper.compareTo(this.my_enum_string_map, typedOther.my_enum_string_map);
            if (lastComparison != 0) {
                return lastComparison;
            }
        }
        lastComparison = Boolean.valueOf(this.isSetMy_enum_struct_map()).compareTo(Boolean.valueOf(typedOther.isSetMy_enum_struct_map()));
        if (lastComparison != 0) {
            return lastComparison;
        }
        if (this.isSetMy_enum_struct_map()) {
            lastComparison = TBaseHelper.compareTo(this.my_enum_struct_map, typedOther.my_enum_struct_map);
            if (lastComparison != 0) {
                return lastComparison;
            }
        }
        lastComparison = Boolean.valueOf(this.isSetMy_enum_stringlist_map()).compareTo(Boolean.valueOf(typedOther.isSetMy_enum_stringlist_map()));
        if (lastComparison != 0) {
            return lastComparison;
        }
        if (this.isSetMy_enum_stringlist_map()) {
            lastComparison = TBaseHelper.compareTo(this.my_enum_stringlist_map, typedOther.my_enum_stringlist_map);
            if (lastComparison != 0) {
                return lastComparison;
            }
        }
        lastComparison = Boolean.valueOf(this.isSetMy_enum_structlist_map()).compareTo(Boolean.valueOf(typedOther.isSetMy_enum_structlist_map()));
        if (lastComparison != 0) {
            return lastComparison;
        }
        if (this.isSetMy_enum_structlist_map()) {
            lastComparison = TBaseHelper.compareTo(this.my_enum_structlist_map, typedOther.my_enum_structlist_map);
            if (lastComparison != 0) {
                return lastComparison;
            }
        }
        lastComparison = Boolean.valueOf(this.isSetMy_stringlist()).compareTo(Boolean.valueOf(typedOther.isSetMy_stringlist()));
        if (lastComparison != 0) {
            return lastComparison;
        }
        if (this.isSetMy_stringlist()) {
            lastComparison = TBaseHelper.compareTo(this.my_stringlist, typedOther.my_stringlist);
            if (lastComparison != 0) {
                return lastComparison;
            }
        }
        lastComparison = Boolean.valueOf(this.isSetMy_structlist()).compareTo(Boolean.valueOf(typedOther.isSetMy_structlist()));
        if (lastComparison != 0) {
            return lastComparison;
        }
        if (this.isSetMy_structlist()) {
            lastComparison = TBaseHelper.compareTo(this.my_structlist, typedOther.my_structlist);
            if (lastComparison != 0) {
                return lastComparison;
            }
        }
        lastComparison = Boolean.valueOf(this.isSetMy_enumlist()).compareTo(Boolean.valueOf(typedOther.isSetMy_enumlist()));
        if (lastComparison != 0) {
            return lastComparison;
        }
        if (this.isSetMy_enumlist()) {
            lastComparison = TBaseHelper.compareTo(this.my_enumlist, typedOther.my_enumlist);
            if (lastComparison != 0) {
                return lastComparison;
            }
        }
        lastComparison = Boolean.valueOf(this.isSetMy_stringset()).compareTo(Boolean.valueOf(typedOther.isSetMy_stringset()));
        if (lastComparison != 0) {
            return lastComparison;
        }
        if (this.isSetMy_stringset()) {
            lastComparison = TBaseHelper.compareTo(this.my_stringset, typedOther.my_stringset);
            if (lastComparison != 0) {
                return lastComparison;
            }
        }
        lastComparison = Boolean.valueOf(this.isSetMy_enumset()).compareTo(Boolean.valueOf(typedOther.isSetMy_enumset()));
        if (lastComparison != 0) {
            return lastComparison;
        }
        if (this.isSetMy_enumset()) {
            lastComparison = TBaseHelper.compareTo(this.my_enumset, typedOther.my_enumset);
            if (lastComparison != 0) {
                return lastComparison;
            }
        }
        lastComparison = Boolean.valueOf(this.isSetMy_structset()).compareTo(Boolean.valueOf(typedOther.isSetMy_structset()));
        if (lastComparison != 0) {
            return lastComparison;
        }
        if (this.isSetMy_structset()) {
            lastComparison = TBaseHelper.compareTo(this.my_structset, typedOther.my_structset);
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
        MegaStruct.schemes.get(iprot.getScheme()).getScheme().read(iprot, this);
    }
    
    @Override
    public void write(final TProtocol oprot) throws TException {
        MegaStruct.schemes.get(oprot.getScheme()).getScheme().write(oprot, this);
    }
    
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("MegaStruct(");
        boolean first = true;
        if (this.isSetMy_bool()) {
            sb.append("my_bool:");
            sb.append(this.my_bool);
            first = false;
        }
        if (this.isSetMy_byte()) {
            if (!first) {
                sb.append(", ");
            }
            sb.append("my_byte:");
            sb.append(this.my_byte);
            first = false;
        }
        if (this.isSetMy_16bit_int()) {
            if (!first) {
                sb.append(", ");
            }
            sb.append("my_16bit_int:");
            sb.append(this.my_16bit_int);
            first = false;
        }
        if (this.isSetMy_32bit_int()) {
            if (!first) {
                sb.append(", ");
            }
            sb.append("my_32bit_int:");
            sb.append(this.my_32bit_int);
            first = false;
        }
        if (this.isSetMy_64bit_int()) {
            if (!first) {
                sb.append(", ");
            }
            sb.append("my_64bit_int:");
            sb.append(this.my_64bit_int);
            first = false;
        }
        if (this.isSetMy_double()) {
            if (!first) {
                sb.append(", ");
            }
            sb.append("my_double:");
            sb.append(this.my_double);
            first = false;
        }
        if (this.isSetMy_string()) {
            if (!first) {
                sb.append(", ");
            }
            sb.append("my_string:");
            if (this.my_string == null) {
                sb.append("null");
            }
            else {
                sb.append(this.my_string);
            }
            first = false;
        }
        if (this.isSetMy_binary()) {
            if (!first) {
                sb.append(", ");
            }
            sb.append("my_binary:");
            if (this.my_binary == null) {
                sb.append("null");
            }
            else {
                TBaseHelper.toString(this.my_binary, sb);
            }
            first = false;
        }
        if (this.isSetMy_string_string_map()) {
            if (!first) {
                sb.append(", ");
            }
            sb.append("my_string_string_map:");
            if (this.my_string_string_map == null) {
                sb.append("null");
            }
            else {
                sb.append(this.my_string_string_map);
            }
            first = false;
        }
        if (this.isSetMy_string_enum_map()) {
            if (!first) {
                sb.append(", ");
            }
            sb.append("my_string_enum_map:");
            if (this.my_string_enum_map == null) {
                sb.append("null");
            }
            else {
                sb.append(this.my_string_enum_map);
            }
            first = false;
        }
        if (this.isSetMy_enum_string_map()) {
            if (!first) {
                sb.append(", ");
            }
            sb.append("my_enum_string_map:");
            if (this.my_enum_string_map == null) {
                sb.append("null");
            }
            else {
                sb.append(this.my_enum_string_map);
            }
            first = false;
        }
        if (this.isSetMy_enum_struct_map()) {
            if (!first) {
                sb.append(", ");
            }
            sb.append("my_enum_struct_map:");
            if (this.my_enum_struct_map == null) {
                sb.append("null");
            }
            else {
                sb.append(this.my_enum_struct_map);
            }
            first = false;
        }
        if (this.isSetMy_enum_stringlist_map()) {
            if (!first) {
                sb.append(", ");
            }
            sb.append("my_enum_stringlist_map:");
            if (this.my_enum_stringlist_map == null) {
                sb.append("null");
            }
            else {
                sb.append(this.my_enum_stringlist_map);
            }
            first = false;
        }
        if (this.isSetMy_enum_structlist_map()) {
            if (!first) {
                sb.append(", ");
            }
            sb.append("my_enum_structlist_map:");
            if (this.my_enum_structlist_map == null) {
                sb.append("null");
            }
            else {
                sb.append(this.my_enum_structlist_map);
            }
            first = false;
        }
        if (this.isSetMy_stringlist()) {
            if (!first) {
                sb.append(", ");
            }
            sb.append("my_stringlist:");
            if (this.my_stringlist == null) {
                sb.append("null");
            }
            else {
                sb.append(this.my_stringlist);
            }
            first = false;
        }
        if (this.isSetMy_structlist()) {
            if (!first) {
                sb.append(", ");
            }
            sb.append("my_structlist:");
            if (this.my_structlist == null) {
                sb.append("null");
            }
            else {
                sb.append(this.my_structlist);
            }
            first = false;
        }
        if (this.isSetMy_enumlist()) {
            if (!first) {
                sb.append(", ");
            }
            sb.append("my_enumlist:");
            if (this.my_enumlist == null) {
                sb.append("null");
            }
            else {
                sb.append(this.my_enumlist);
            }
            first = false;
        }
        if (this.isSetMy_stringset()) {
            if (!first) {
                sb.append(", ");
            }
            sb.append("my_stringset:");
            if (this.my_stringset == null) {
                sb.append("null");
            }
            else {
                sb.append(this.my_stringset);
            }
            first = false;
        }
        if (this.isSetMy_enumset()) {
            if (!first) {
                sb.append(", ");
            }
            sb.append("my_enumset:");
            if (this.my_enumset == null) {
                sb.append("null");
            }
            else {
                sb.append(this.my_enumset);
            }
            first = false;
        }
        if (this.isSetMy_structset()) {
            if (!first) {
                sb.append(", ");
            }
            sb.append("my_structset:");
            if (this.my_structset == null) {
                sb.append("null");
            }
            else {
                sb.append(this.my_structset);
            }
            first = false;
        }
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
            this.__isset_bitfield = 0;
            this.read(new TCompactProtocol(new TIOStreamTransport(in)));
        }
        catch (TException te) {
            throw new IOException(te);
        }
    }
    
    static {
        STRUCT_DESC = new TStruct("MegaStruct");
        MY_BOOL_FIELD_DESC = new TField("my_bool", (byte)2, (short)1);
        MY_BYTE_FIELD_DESC = new TField("my_byte", (byte)3, (short)2);
        MY_16BIT_INT_FIELD_DESC = new TField("my_16bit_int", (byte)6, (short)3);
        MY_32BIT_INT_FIELD_DESC = new TField("my_32bit_int", (byte)8, (short)4);
        MY_64BIT_INT_FIELD_DESC = new TField("my_64bit_int", (byte)10, (short)5);
        MY_DOUBLE_FIELD_DESC = new TField("my_double", (byte)4, (short)6);
        MY_STRING_FIELD_DESC = new TField("my_string", (byte)11, (short)7);
        MY_BINARY_FIELD_DESC = new TField("my_binary", (byte)11, (short)8);
        MY_STRING_STRING_MAP_FIELD_DESC = new TField("my_string_string_map", (byte)13, (short)9);
        MY_STRING_ENUM_MAP_FIELD_DESC = new TField("my_string_enum_map", (byte)13, (short)10);
        MY_ENUM_STRING_MAP_FIELD_DESC = new TField("my_enum_string_map", (byte)13, (short)11);
        MY_ENUM_STRUCT_MAP_FIELD_DESC = new TField("my_enum_struct_map", (byte)13, (short)12);
        MY_ENUM_STRINGLIST_MAP_FIELD_DESC = new TField("my_enum_stringlist_map", (byte)13, (short)13);
        MY_ENUM_STRUCTLIST_MAP_FIELD_DESC = new TField("my_enum_structlist_map", (byte)13, (short)14);
        MY_STRINGLIST_FIELD_DESC = new TField("my_stringlist", (byte)15, (short)15);
        MY_STRUCTLIST_FIELD_DESC = new TField("my_structlist", (byte)15, (short)16);
        MY_ENUMLIST_FIELD_DESC = new TField("my_enumlist", (byte)15, (short)17);
        MY_STRINGSET_FIELD_DESC = new TField("my_stringset", (byte)14, (short)18);
        MY_ENUMSET_FIELD_DESC = new TField("my_enumset", (byte)14, (short)19);
        MY_STRUCTSET_FIELD_DESC = new TField("my_structset", (byte)14, (short)20);
        (schemes = new HashMap<Class<? extends IScheme>, SchemeFactory>()).put(StandardScheme.class, new MegaStructStandardSchemeFactory());
        MegaStruct.schemes.put(TupleScheme.class, new MegaStructTupleSchemeFactory());
        final Map<_Fields, FieldMetaData> tmpMap = new EnumMap<_Fields, FieldMetaData>(_Fields.class);
        tmpMap.put(_Fields.MY_BOOL, new FieldMetaData("my_bool", (byte)2, new FieldValueMetaData((byte)2)));
        tmpMap.put(_Fields.MY_BYTE, new FieldMetaData("my_byte", (byte)2, new FieldValueMetaData((byte)3)));
        tmpMap.put(_Fields.MY_16BIT_INT, new FieldMetaData("my_16bit_int", (byte)2, new FieldValueMetaData((byte)6)));
        tmpMap.put(_Fields.MY_32BIT_INT, new FieldMetaData("my_32bit_int", (byte)2, new FieldValueMetaData((byte)8)));
        tmpMap.put(_Fields.MY_64BIT_INT, new FieldMetaData("my_64bit_int", (byte)2, new FieldValueMetaData((byte)10)));
        tmpMap.put(_Fields.MY_DOUBLE, new FieldMetaData("my_double", (byte)2, new FieldValueMetaData((byte)4)));
        tmpMap.put(_Fields.MY_STRING, new FieldMetaData("my_string", (byte)2, new FieldValueMetaData((byte)11)));
        tmpMap.put(_Fields.MY_BINARY, new FieldMetaData("my_binary", (byte)2, new FieldValueMetaData((byte)11, true)));
        tmpMap.put(_Fields.MY_STRING_STRING_MAP, new FieldMetaData("my_string_string_map", (byte)2, new MapMetaData((byte)13, new FieldValueMetaData((byte)11), new FieldValueMetaData((byte)11))));
        tmpMap.put(_Fields.MY_STRING_ENUM_MAP, new FieldMetaData("my_string_enum_map", (byte)2, new MapMetaData((byte)13, new FieldValueMetaData((byte)11), new EnumMetaData((byte)16, MyEnum.class))));
        tmpMap.put(_Fields.MY_ENUM_STRING_MAP, new FieldMetaData("my_enum_string_map", (byte)2, new MapMetaData((byte)13, new EnumMetaData((byte)16, MyEnum.class), new FieldValueMetaData((byte)11))));
        tmpMap.put(_Fields.MY_ENUM_STRUCT_MAP, new FieldMetaData("my_enum_struct_map", (byte)2, new MapMetaData((byte)13, new EnumMetaData((byte)16, MyEnum.class), new StructMetaData((byte)12, MiniStruct.class))));
        tmpMap.put(_Fields.MY_ENUM_STRINGLIST_MAP, new FieldMetaData("my_enum_stringlist_map", (byte)2, new MapMetaData((byte)13, new EnumMetaData((byte)16, MyEnum.class), new ListMetaData((byte)15, new FieldValueMetaData((byte)11)))));
        tmpMap.put(_Fields.MY_ENUM_STRUCTLIST_MAP, new FieldMetaData("my_enum_structlist_map", (byte)2, new MapMetaData((byte)13, new EnumMetaData((byte)16, MyEnum.class), new ListMetaData((byte)15, new StructMetaData((byte)12, MiniStruct.class)))));
        tmpMap.put(_Fields.MY_STRINGLIST, new FieldMetaData("my_stringlist", (byte)2, new ListMetaData((byte)15, new FieldValueMetaData((byte)11))));
        tmpMap.put(_Fields.MY_STRUCTLIST, new FieldMetaData("my_structlist", (byte)2, new ListMetaData((byte)15, new StructMetaData((byte)12, MiniStruct.class))));
        tmpMap.put(_Fields.MY_ENUMLIST, new FieldMetaData("my_enumlist", (byte)2, new ListMetaData((byte)15, new EnumMetaData((byte)16, MyEnum.class))));
        tmpMap.put(_Fields.MY_STRINGSET, new FieldMetaData("my_stringset", (byte)2, new SetMetaData((byte)14, new FieldValueMetaData((byte)11))));
        tmpMap.put(_Fields.MY_ENUMSET, new FieldMetaData("my_enumset", (byte)2, new SetMetaData((byte)14, new EnumMetaData((byte)16, MyEnum.class))));
        tmpMap.put(_Fields.MY_STRUCTSET, new FieldMetaData("my_structset", (byte)2, new SetMetaData((byte)14, new StructMetaData((byte)12, MiniStruct.class))));
        FieldMetaData.addStructMetaDataMap(MegaStruct.class, metaDataMap = Collections.unmodifiableMap((Map<? extends _Fields, ? extends FieldMetaData>)tmpMap));
    }
    
    public enum _Fields implements TFieldIdEnum
    {
        MY_BOOL((short)1, "my_bool"), 
        MY_BYTE((short)2, "my_byte"), 
        MY_16BIT_INT((short)3, "my_16bit_int"), 
        MY_32BIT_INT((short)4, "my_32bit_int"), 
        MY_64BIT_INT((short)5, "my_64bit_int"), 
        MY_DOUBLE((short)6, "my_double"), 
        MY_STRING((short)7, "my_string"), 
        MY_BINARY((short)8, "my_binary"), 
        MY_STRING_STRING_MAP((short)9, "my_string_string_map"), 
        MY_STRING_ENUM_MAP((short)10, "my_string_enum_map"), 
        MY_ENUM_STRING_MAP((short)11, "my_enum_string_map"), 
        MY_ENUM_STRUCT_MAP((short)12, "my_enum_struct_map"), 
        MY_ENUM_STRINGLIST_MAP((short)13, "my_enum_stringlist_map"), 
        MY_ENUM_STRUCTLIST_MAP((short)14, "my_enum_structlist_map"), 
        MY_STRINGLIST((short)15, "my_stringlist"), 
        MY_STRUCTLIST((short)16, "my_structlist"), 
        MY_ENUMLIST((short)17, "my_enumlist"), 
        MY_STRINGSET((short)18, "my_stringset"), 
        MY_ENUMSET((short)19, "my_enumset"), 
        MY_STRUCTSET((short)20, "my_structset");
        
        private static final Map<String, _Fields> byName;
        private final short _thriftId;
        private final String _fieldName;
        
        public static _Fields findByThriftId(final int fieldId) {
            switch (fieldId) {
                case 1: {
                    return _Fields.MY_BOOL;
                }
                case 2: {
                    return _Fields.MY_BYTE;
                }
                case 3: {
                    return _Fields.MY_16BIT_INT;
                }
                case 4: {
                    return _Fields.MY_32BIT_INT;
                }
                case 5: {
                    return _Fields.MY_64BIT_INT;
                }
                case 6: {
                    return _Fields.MY_DOUBLE;
                }
                case 7: {
                    return _Fields.MY_STRING;
                }
                case 8: {
                    return _Fields.MY_BINARY;
                }
                case 9: {
                    return _Fields.MY_STRING_STRING_MAP;
                }
                case 10: {
                    return _Fields.MY_STRING_ENUM_MAP;
                }
                case 11: {
                    return _Fields.MY_ENUM_STRING_MAP;
                }
                case 12: {
                    return _Fields.MY_ENUM_STRUCT_MAP;
                }
                case 13: {
                    return _Fields.MY_ENUM_STRINGLIST_MAP;
                }
                case 14: {
                    return _Fields.MY_ENUM_STRUCTLIST_MAP;
                }
                case 15: {
                    return _Fields.MY_STRINGLIST;
                }
                case 16: {
                    return _Fields.MY_STRUCTLIST;
                }
                case 17: {
                    return _Fields.MY_ENUMLIST;
                }
                case 18: {
                    return _Fields.MY_STRINGSET;
                }
                case 19: {
                    return _Fields.MY_ENUMSET;
                }
                case 20: {
                    return _Fields.MY_STRUCTSET;
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
    
    private static class MegaStructStandardSchemeFactory implements SchemeFactory
    {
        @Override
        public MegaStructStandardScheme getScheme() {
            return new MegaStructStandardScheme();
        }
    }
    
    private static class MegaStructStandardScheme extends StandardScheme<MegaStruct>
    {
        @Override
        public void read(final TProtocol iprot, final MegaStruct struct) throws TException {
            iprot.readStructBegin();
            while (true) {
                final TField schemeField = iprot.readFieldBegin();
                if (schemeField.type == 0) {
                    break;
                }
                switch (schemeField.id) {
                    case 1: {
                        if (schemeField.type == 2) {
                            struct.my_bool = iprot.readBool();
                            struct.setMy_boolIsSet(true);
                            break;
                        }
                        TProtocolUtil.skip(iprot, schemeField.type);
                        break;
                    }
                    case 2: {
                        if (schemeField.type == 3) {
                            struct.my_byte = iprot.readByte();
                            struct.setMy_byteIsSet(true);
                            break;
                        }
                        TProtocolUtil.skip(iprot, schemeField.type);
                        break;
                    }
                    case 3: {
                        if (schemeField.type == 6) {
                            struct.my_16bit_int = iprot.readI16();
                            struct.setMy_16bit_intIsSet(true);
                            break;
                        }
                        TProtocolUtil.skip(iprot, schemeField.type);
                        break;
                    }
                    case 4: {
                        if (schemeField.type == 8) {
                            struct.my_32bit_int = iprot.readI32();
                            struct.setMy_32bit_intIsSet(true);
                            break;
                        }
                        TProtocolUtil.skip(iprot, schemeField.type);
                        break;
                    }
                    case 5: {
                        if (schemeField.type == 10) {
                            struct.my_64bit_int = iprot.readI64();
                            struct.setMy_64bit_intIsSet(true);
                            break;
                        }
                        TProtocolUtil.skip(iprot, schemeField.type);
                        break;
                    }
                    case 6: {
                        if (schemeField.type == 4) {
                            struct.my_double = iprot.readDouble();
                            struct.setMy_doubleIsSet(true);
                            break;
                        }
                        TProtocolUtil.skip(iprot, schemeField.type);
                        break;
                    }
                    case 7: {
                        if (schemeField.type == 11) {
                            struct.my_string = iprot.readString();
                            struct.setMy_stringIsSet(true);
                            break;
                        }
                        TProtocolUtil.skip(iprot, schemeField.type);
                        break;
                    }
                    case 8: {
                        if (schemeField.type == 11) {
                            struct.my_binary = iprot.readBinary();
                            struct.setMy_binaryIsSet(true);
                            break;
                        }
                        TProtocolUtil.skip(iprot, schemeField.type);
                        break;
                    }
                    case 9: {
                        if (schemeField.type == 13) {
                            final TMap _map0 = iprot.readMapBegin();
                            struct.my_string_string_map = (Map<String, String>)new HashMap(2 * _map0.size);
                            for (int _i1 = 0; _i1 < _map0.size; ++_i1) {
                                final String _key2 = iprot.readString();
                                final String _val3 = iprot.readString();
                                struct.my_string_string_map.put(_key2, _val3);
                            }
                            iprot.readMapEnd();
                            struct.setMy_string_string_mapIsSet(true);
                            break;
                        }
                        TProtocolUtil.skip(iprot, schemeField.type);
                        break;
                    }
                    case 10: {
                        if (schemeField.type == 13) {
                            final TMap _map2 = iprot.readMapBegin();
                            struct.my_string_enum_map = (Map<String, MyEnum>)new HashMap(2 * _map2.size);
                            for (int _i2 = 0; _i2 < _map2.size; ++_i2) {
                                final String _key3 = iprot.readString();
                                final MyEnum _val4 = MyEnum.findByValue(iprot.readI32());
                                struct.my_string_enum_map.put(_key3, _val4);
                            }
                            iprot.readMapEnd();
                            struct.setMy_string_enum_mapIsSet(true);
                            break;
                        }
                        TProtocolUtil.skip(iprot, schemeField.type);
                        break;
                    }
                    case 11: {
                        if (schemeField.type == 13) {
                            final TMap _map3 = iprot.readMapBegin();
                            struct.my_enum_string_map = (Map<MyEnum, String>)new HashMap(2 * _map3.size);
                            for (int _i3 = 0; _i3 < _map3.size; ++_i3) {
                                final MyEnum _key4 = MyEnum.findByValue(iprot.readI32());
                                final String _val5 = iprot.readString();
                                struct.my_enum_string_map.put(_key4, _val5);
                            }
                            iprot.readMapEnd();
                            struct.setMy_enum_string_mapIsSet(true);
                            break;
                        }
                        TProtocolUtil.skip(iprot, schemeField.type);
                        break;
                    }
                    case 12: {
                        if (schemeField.type == 13) {
                            final TMap _map4 = iprot.readMapBegin();
                            struct.my_enum_struct_map = (Map<MyEnum, MiniStruct>)new HashMap(2 * _map4.size);
                            for (int _i4 = 0; _i4 < _map4.size; ++_i4) {
                                final MyEnum _key5 = MyEnum.findByValue(iprot.readI32());
                                final MiniStruct _val6 = new MiniStruct();
                                _val6.read(iprot);
                                struct.my_enum_struct_map.put(_key5, _val6);
                            }
                            iprot.readMapEnd();
                            struct.setMy_enum_struct_mapIsSet(true);
                            break;
                        }
                        TProtocolUtil.skip(iprot, schemeField.type);
                        break;
                    }
                    case 13: {
                        if (schemeField.type == 13) {
                            final TMap _map5 = iprot.readMapBegin();
                            struct.my_enum_stringlist_map = (Map<MyEnum, List<String>>)new HashMap(2 * _map5.size);
                            for (int _i5 = 0; _i5 < _map5.size; ++_i5) {
                                final MyEnum _key6 = MyEnum.findByValue(iprot.readI32());
                                final TList _list20 = iprot.readListBegin();
                                final List<String> _val7 = new ArrayList<String>(_list20.size);
                                for (int _i6 = 0; _i6 < _list20.size; ++_i6) {
                                    final String _elem22 = iprot.readString();
                                    _val7.add(_elem22);
                                }
                                iprot.readListEnd();
                                struct.my_enum_stringlist_map.put(_key6, _val7);
                            }
                            iprot.readMapEnd();
                            struct.setMy_enum_stringlist_mapIsSet(true);
                            break;
                        }
                        TProtocolUtil.skip(iprot, schemeField.type);
                        break;
                    }
                    case 14: {
                        if (schemeField.type == 13) {
                            final TMap _map6 = iprot.readMapBegin();
                            struct.my_enum_structlist_map = (Map<MyEnum, List<MiniStruct>>)new HashMap(2 * _map6.size);
                            for (int _i7 = 0; _i7 < _map6.size; ++_i7) {
                                final MyEnum _key7 = MyEnum.findByValue(iprot.readI32());
                                final TList _list21 = iprot.readListBegin();
                                final List<MiniStruct> _val8 = new ArrayList<MiniStruct>(_list21.size);
                                for (int _i8 = 0; _i8 < _list21.size; ++_i8) {
                                    final MiniStruct _elem23 = new MiniStruct();
                                    _elem23.read(iprot);
                                    _val8.add(_elem23);
                                }
                                iprot.readListEnd();
                                struct.my_enum_structlist_map.put(_key7, _val8);
                            }
                            iprot.readMapEnd();
                            struct.setMy_enum_structlist_mapIsSet(true);
                            break;
                        }
                        TProtocolUtil.skip(iprot, schemeField.type);
                        break;
                    }
                    case 15: {
                        if (schemeField.type == 15) {
                            final TList _list22 = iprot.readListBegin();
                            struct.my_stringlist = (List<String>)new ArrayList(_list22.size);
                            for (int _i9 = 0; _i9 < _list22.size; ++_i9) {
                                final String _elem24 = iprot.readString();
                                struct.my_stringlist.add(_elem24);
                            }
                            iprot.readListEnd();
                            struct.setMy_stringlistIsSet(true);
                            break;
                        }
                        TProtocolUtil.skip(iprot, schemeField.type);
                        break;
                    }
                    case 16: {
                        if (schemeField.type == 15) {
                            final TList _list23 = iprot.readListBegin();
                            struct.my_structlist = (List<MiniStruct>)new ArrayList(_list23.size);
                            for (int _i10 = 0; _i10 < _list23.size; ++_i10) {
                                final MiniStruct _elem25 = new MiniStruct();
                                _elem25.read(iprot);
                                struct.my_structlist.add(_elem25);
                            }
                            iprot.readListEnd();
                            struct.setMy_structlistIsSet(true);
                            break;
                        }
                        TProtocolUtil.skip(iprot, schemeField.type);
                        break;
                    }
                    case 17: {
                        if (schemeField.type == 15) {
                            final TList _list24 = iprot.readListBegin();
                            struct.my_enumlist = (List<MyEnum>)new ArrayList(_list24.size);
                            for (int _i11 = 0; _i11 < _list24.size; ++_i11) {
                                final MyEnum _elem26 = MyEnum.findByValue(iprot.readI32());
                                struct.my_enumlist.add(_elem26);
                            }
                            iprot.readListEnd();
                            struct.setMy_enumlistIsSet(true);
                            break;
                        }
                        TProtocolUtil.skip(iprot, schemeField.type);
                        break;
                    }
                    case 18: {
                        if (schemeField.type == 14) {
                            final TSet _set39 = iprot.readSetBegin();
                            struct.my_stringset = (Set<String>)new HashSet(2 * _set39.size);
                            for (int _i12 = 0; _i12 < _set39.size; ++_i12) {
                                final String _elem27 = iprot.readString();
                                struct.my_stringset.add(_elem27);
                            }
                            iprot.readSetEnd();
                            struct.setMy_stringsetIsSet(true);
                            break;
                        }
                        TProtocolUtil.skip(iprot, schemeField.type);
                        break;
                    }
                    case 19: {
                        if (schemeField.type == 14) {
                            final TSet _set40 = iprot.readSetBegin();
                            struct.my_enumset = (Set<MyEnum>)new HashSet(2 * _set40.size);
                            for (int _i13 = 0; _i13 < _set40.size; ++_i13) {
                                final MyEnum _elem28 = MyEnum.findByValue(iprot.readI32());
                                struct.my_enumset.add(_elem28);
                            }
                            iprot.readSetEnd();
                            struct.setMy_enumsetIsSet(true);
                            break;
                        }
                        TProtocolUtil.skip(iprot, schemeField.type);
                        break;
                    }
                    case 20: {
                        if (schemeField.type == 14) {
                            final TSet _set41 = iprot.readSetBegin();
                            struct.my_structset = (Set<MiniStruct>)new HashSet(2 * _set41.size);
                            for (int _i14 = 0; _i14 < _set41.size; ++_i14) {
                                final MiniStruct _elem29 = new MiniStruct();
                                _elem29.read(iprot);
                                struct.my_structset.add(_elem29);
                            }
                            iprot.readSetEnd();
                            struct.setMy_structsetIsSet(true);
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
        public void write(final TProtocol oprot, final MegaStruct struct) throws TException {
            struct.validate();
            oprot.writeStructBegin(MegaStruct.STRUCT_DESC);
            if (struct.isSetMy_bool()) {
                oprot.writeFieldBegin(MegaStruct.MY_BOOL_FIELD_DESC);
                oprot.writeBool(struct.my_bool);
                oprot.writeFieldEnd();
            }
            if (struct.isSetMy_byte()) {
                oprot.writeFieldBegin(MegaStruct.MY_BYTE_FIELD_DESC);
                oprot.writeByte(struct.my_byte);
                oprot.writeFieldEnd();
            }
            if (struct.isSetMy_16bit_int()) {
                oprot.writeFieldBegin(MegaStruct.MY_16BIT_INT_FIELD_DESC);
                oprot.writeI16(struct.my_16bit_int);
                oprot.writeFieldEnd();
            }
            if (struct.isSetMy_32bit_int()) {
                oprot.writeFieldBegin(MegaStruct.MY_32BIT_INT_FIELD_DESC);
                oprot.writeI32(struct.my_32bit_int);
                oprot.writeFieldEnd();
            }
            if (struct.isSetMy_64bit_int()) {
                oprot.writeFieldBegin(MegaStruct.MY_64BIT_INT_FIELD_DESC);
                oprot.writeI64(struct.my_64bit_int);
                oprot.writeFieldEnd();
            }
            if (struct.isSetMy_double()) {
                oprot.writeFieldBegin(MegaStruct.MY_DOUBLE_FIELD_DESC);
                oprot.writeDouble(struct.my_double);
                oprot.writeFieldEnd();
            }
            if (struct.my_string != null && struct.isSetMy_string()) {
                oprot.writeFieldBegin(MegaStruct.MY_STRING_FIELD_DESC);
                oprot.writeString(struct.my_string);
                oprot.writeFieldEnd();
            }
            if (struct.my_binary != null && struct.isSetMy_binary()) {
                oprot.writeFieldBegin(MegaStruct.MY_BINARY_FIELD_DESC);
                oprot.writeBinary(struct.my_binary);
                oprot.writeFieldEnd();
            }
            if (struct.my_string_string_map != null && struct.isSetMy_string_string_map()) {
                oprot.writeFieldBegin(MegaStruct.MY_STRING_STRING_MAP_FIELD_DESC);
                oprot.writeMapBegin(new TMap((byte)11, (byte)11, struct.my_string_string_map.size()));
                for (final Map.Entry<String, String> _iter48 : struct.my_string_string_map.entrySet()) {
                    oprot.writeString(_iter48.getKey());
                    oprot.writeString(_iter48.getValue());
                }
                oprot.writeMapEnd();
                oprot.writeFieldEnd();
            }
            if (struct.my_string_enum_map != null && struct.isSetMy_string_enum_map()) {
                oprot.writeFieldBegin(MegaStruct.MY_STRING_ENUM_MAP_FIELD_DESC);
                oprot.writeMapBegin(new TMap((byte)11, (byte)8, struct.my_string_enum_map.size()));
                for (final Map.Entry<String, MyEnum> _iter49 : struct.my_string_enum_map.entrySet()) {
                    oprot.writeString(_iter49.getKey());
                    oprot.writeI32(_iter49.getValue().getValue());
                }
                oprot.writeMapEnd();
                oprot.writeFieldEnd();
            }
            if (struct.my_enum_string_map != null && struct.isSetMy_enum_string_map()) {
                oprot.writeFieldBegin(MegaStruct.MY_ENUM_STRING_MAP_FIELD_DESC);
                oprot.writeMapBegin(new TMap((byte)8, (byte)11, struct.my_enum_string_map.size()));
                for (final Map.Entry<MyEnum, String> _iter50 : struct.my_enum_string_map.entrySet()) {
                    oprot.writeI32(_iter50.getKey().getValue());
                    oprot.writeString(_iter50.getValue());
                }
                oprot.writeMapEnd();
                oprot.writeFieldEnd();
            }
            if (struct.my_enum_struct_map != null && struct.isSetMy_enum_struct_map()) {
                oprot.writeFieldBegin(MegaStruct.MY_ENUM_STRUCT_MAP_FIELD_DESC);
                oprot.writeMapBegin(new TMap((byte)8, (byte)12, struct.my_enum_struct_map.size()));
                for (final Map.Entry<MyEnum, MiniStruct> _iter51 : struct.my_enum_struct_map.entrySet()) {
                    oprot.writeI32(_iter51.getKey().getValue());
                    _iter51.getValue().write(oprot);
                }
                oprot.writeMapEnd();
                oprot.writeFieldEnd();
            }
            if (struct.my_enum_stringlist_map != null && struct.isSetMy_enum_stringlist_map()) {
                oprot.writeFieldBegin(MegaStruct.MY_ENUM_STRINGLIST_MAP_FIELD_DESC);
                oprot.writeMapBegin(new TMap((byte)8, (byte)15, struct.my_enum_stringlist_map.size()));
                for (final Map.Entry<MyEnum, List<String>> _iter52 : struct.my_enum_stringlist_map.entrySet()) {
                    oprot.writeI32(_iter52.getKey().getValue());
                    oprot.writeListBegin(new TList((byte)11, _iter52.getValue().size()));
                    for (final String _iter53 : _iter52.getValue()) {
                        oprot.writeString(_iter53);
                    }
                    oprot.writeListEnd();
                }
                oprot.writeMapEnd();
                oprot.writeFieldEnd();
            }
            if (struct.my_enum_structlist_map != null && struct.isSetMy_enum_structlist_map()) {
                oprot.writeFieldBegin(MegaStruct.MY_ENUM_STRUCTLIST_MAP_FIELD_DESC);
                oprot.writeMapBegin(new TMap((byte)8, (byte)15, struct.my_enum_structlist_map.size()));
                for (final Map.Entry<MyEnum, List<MiniStruct>> _iter54 : struct.my_enum_structlist_map.entrySet()) {
                    oprot.writeI32(_iter54.getKey().getValue());
                    oprot.writeListBegin(new TList((byte)12, _iter54.getValue().size()));
                    for (final MiniStruct _iter55 : _iter54.getValue()) {
                        _iter55.write(oprot);
                    }
                    oprot.writeListEnd();
                }
                oprot.writeMapEnd();
                oprot.writeFieldEnd();
            }
            if (struct.my_stringlist != null && struct.isSetMy_stringlist()) {
                oprot.writeFieldBegin(MegaStruct.MY_STRINGLIST_FIELD_DESC);
                oprot.writeListBegin(new TList((byte)11, struct.my_stringlist.size()));
                for (final String _iter56 : struct.my_stringlist) {
                    oprot.writeString(_iter56);
                }
                oprot.writeListEnd();
                oprot.writeFieldEnd();
            }
            if (struct.my_structlist != null && struct.isSetMy_structlist()) {
                oprot.writeFieldBegin(MegaStruct.MY_STRUCTLIST_FIELD_DESC);
                oprot.writeListBegin(new TList((byte)12, struct.my_structlist.size()));
                for (final MiniStruct _iter57 : struct.my_structlist) {
                    _iter57.write(oprot);
                }
                oprot.writeListEnd();
                oprot.writeFieldEnd();
            }
            if (struct.my_enumlist != null && struct.isSetMy_enumlist()) {
                oprot.writeFieldBegin(MegaStruct.MY_ENUMLIST_FIELD_DESC);
                oprot.writeListBegin(new TList((byte)8, struct.my_enumlist.size()));
                for (final MyEnum _iter58 : struct.my_enumlist) {
                    oprot.writeI32(_iter58.getValue());
                }
                oprot.writeListEnd();
                oprot.writeFieldEnd();
            }
            if (struct.my_stringset != null && struct.isSetMy_stringset()) {
                oprot.writeFieldBegin(MegaStruct.MY_STRINGSET_FIELD_DESC);
                oprot.writeSetBegin(new TSet((byte)11, struct.my_stringset.size()));
                for (final String _iter59 : struct.my_stringset) {
                    oprot.writeString(_iter59);
                }
                oprot.writeSetEnd();
                oprot.writeFieldEnd();
            }
            if (struct.my_enumset != null && struct.isSetMy_enumset()) {
                oprot.writeFieldBegin(MegaStruct.MY_ENUMSET_FIELD_DESC);
                oprot.writeSetBegin(new TSet((byte)8, struct.my_enumset.size()));
                for (final MyEnum _iter60 : struct.my_enumset) {
                    oprot.writeI32(_iter60.getValue());
                }
                oprot.writeSetEnd();
                oprot.writeFieldEnd();
            }
            if (struct.my_structset != null && struct.isSetMy_structset()) {
                oprot.writeFieldBegin(MegaStruct.MY_STRUCTSET_FIELD_DESC);
                oprot.writeSetBegin(new TSet((byte)12, struct.my_structset.size()));
                for (final MiniStruct _iter61 : struct.my_structset) {
                    _iter61.write(oprot);
                }
                oprot.writeSetEnd();
                oprot.writeFieldEnd();
            }
            oprot.writeFieldStop();
            oprot.writeStructEnd();
        }
    }
    
    private static class MegaStructTupleSchemeFactory implements SchemeFactory
    {
        @Override
        public MegaStructTupleScheme getScheme() {
            return new MegaStructTupleScheme();
        }
    }
    
    private static class MegaStructTupleScheme extends TupleScheme<MegaStruct>
    {
        @Override
        public void write(final TProtocol prot, final MegaStruct struct) throws TException {
            final TTupleProtocol oprot = (TTupleProtocol)prot;
            final BitSet optionals = new BitSet();
            if (struct.isSetMy_bool()) {
                optionals.set(0);
            }
            if (struct.isSetMy_byte()) {
                optionals.set(1);
            }
            if (struct.isSetMy_16bit_int()) {
                optionals.set(2);
            }
            if (struct.isSetMy_32bit_int()) {
                optionals.set(3);
            }
            if (struct.isSetMy_64bit_int()) {
                optionals.set(4);
            }
            if (struct.isSetMy_double()) {
                optionals.set(5);
            }
            if (struct.isSetMy_string()) {
                optionals.set(6);
            }
            if (struct.isSetMy_binary()) {
                optionals.set(7);
            }
            if (struct.isSetMy_string_string_map()) {
                optionals.set(8);
            }
            if (struct.isSetMy_string_enum_map()) {
                optionals.set(9);
            }
            if (struct.isSetMy_enum_string_map()) {
                optionals.set(10);
            }
            if (struct.isSetMy_enum_struct_map()) {
                optionals.set(11);
            }
            if (struct.isSetMy_enum_stringlist_map()) {
                optionals.set(12);
            }
            if (struct.isSetMy_enum_structlist_map()) {
                optionals.set(13);
            }
            if (struct.isSetMy_stringlist()) {
                optionals.set(14);
            }
            if (struct.isSetMy_structlist()) {
                optionals.set(15);
            }
            if (struct.isSetMy_enumlist()) {
                optionals.set(16);
            }
            if (struct.isSetMy_stringset()) {
                optionals.set(17);
            }
            if (struct.isSetMy_enumset()) {
                optionals.set(18);
            }
            if (struct.isSetMy_structset()) {
                optionals.set(19);
            }
            oprot.writeBitSet(optionals, 20);
            if (struct.isSetMy_bool()) {
                oprot.writeBool(struct.my_bool);
            }
            if (struct.isSetMy_byte()) {
                oprot.writeByte(struct.my_byte);
            }
            if (struct.isSetMy_16bit_int()) {
                oprot.writeI16(struct.my_16bit_int);
            }
            if (struct.isSetMy_32bit_int()) {
                oprot.writeI32(struct.my_32bit_int);
            }
            if (struct.isSetMy_64bit_int()) {
                oprot.writeI64(struct.my_64bit_int);
            }
            if (struct.isSetMy_double()) {
                oprot.writeDouble(struct.my_double);
            }
            if (struct.isSetMy_string()) {
                oprot.writeString(struct.my_string);
            }
            if (struct.isSetMy_binary()) {
                oprot.writeBinary(struct.my_binary);
            }
            if (struct.isSetMy_string_string_map()) {
                oprot.writeI32(struct.my_string_string_map.size());
                for (final Map.Entry<String, String> _iter62 : struct.my_string_string_map.entrySet()) {
                    oprot.writeString(_iter62.getKey());
                    oprot.writeString(_iter62.getValue());
                }
            }
            if (struct.isSetMy_string_enum_map()) {
                oprot.writeI32(struct.my_string_enum_map.size());
                for (final Map.Entry<String, MyEnum> _iter63 : struct.my_string_enum_map.entrySet()) {
                    oprot.writeString(_iter63.getKey());
                    oprot.writeI32(_iter63.getValue().getValue());
                }
            }
            if (struct.isSetMy_enum_string_map()) {
                oprot.writeI32(struct.my_enum_string_map.size());
                for (final Map.Entry<MyEnum, String> _iter64 : struct.my_enum_string_map.entrySet()) {
                    oprot.writeI32(_iter64.getKey().getValue());
                    oprot.writeString(_iter64.getValue());
                }
            }
            if (struct.isSetMy_enum_struct_map()) {
                oprot.writeI32(struct.my_enum_struct_map.size());
                for (final Map.Entry<MyEnum, MiniStruct> _iter65 : struct.my_enum_struct_map.entrySet()) {
                    oprot.writeI32(_iter65.getKey().getValue());
                    _iter65.getValue().write(oprot);
                }
            }
            if (struct.isSetMy_enum_stringlist_map()) {
                oprot.writeI32(struct.my_enum_stringlist_map.size());
                for (final Map.Entry<MyEnum, List<String>> _iter66 : struct.my_enum_stringlist_map.entrySet()) {
                    oprot.writeI32(_iter66.getKey().getValue());
                    oprot.writeI32(_iter66.getValue().size());
                    for (final String _iter67 : _iter66.getValue()) {
                        oprot.writeString(_iter67);
                    }
                }
            }
            if (struct.isSetMy_enum_structlist_map()) {
                oprot.writeI32(struct.my_enum_structlist_map.size());
                for (final Map.Entry<MyEnum, List<MiniStruct>> _iter68 : struct.my_enum_structlist_map.entrySet()) {
                    oprot.writeI32(_iter68.getKey().getValue());
                    oprot.writeI32(_iter68.getValue().size());
                    for (final MiniStruct _iter69 : _iter68.getValue()) {
                        _iter69.write(oprot);
                    }
                }
            }
            if (struct.isSetMy_stringlist()) {
                oprot.writeI32(struct.my_stringlist.size());
                for (final String _iter70 : struct.my_stringlist) {
                    oprot.writeString(_iter70);
                }
            }
            if (struct.isSetMy_structlist()) {
                oprot.writeI32(struct.my_structlist.size());
                for (final MiniStruct _iter71 : struct.my_structlist) {
                    _iter71.write(oprot);
                }
            }
            if (struct.isSetMy_enumlist()) {
                oprot.writeI32(struct.my_enumlist.size());
                for (final MyEnum _iter72 : struct.my_enumlist) {
                    oprot.writeI32(_iter72.getValue());
                }
            }
            if (struct.isSetMy_stringset()) {
                oprot.writeI32(struct.my_stringset.size());
                for (final String _iter73 : struct.my_stringset) {
                    oprot.writeString(_iter73);
                }
            }
            if (struct.isSetMy_enumset()) {
                oprot.writeI32(struct.my_enumset.size());
                for (final MyEnum _iter74 : struct.my_enumset) {
                    oprot.writeI32(_iter74.getValue());
                }
            }
            if (struct.isSetMy_structset()) {
                oprot.writeI32(struct.my_structset.size());
                for (final MiniStruct _iter75 : struct.my_structset) {
                    _iter75.write(oprot);
                }
            }
        }
        
        @Override
        public void read(final TProtocol prot, final MegaStruct struct) throws TException {
            final TTupleProtocol iprot = (TTupleProtocol)prot;
            final BitSet incoming = iprot.readBitSet(20);
            if (incoming.get(0)) {
                struct.my_bool = iprot.readBool();
                struct.setMy_boolIsSet(true);
            }
            if (incoming.get(1)) {
                struct.my_byte = iprot.readByte();
                struct.setMy_byteIsSet(true);
            }
            if (incoming.get(2)) {
                struct.my_16bit_int = iprot.readI16();
                struct.setMy_16bit_intIsSet(true);
            }
            if (incoming.get(3)) {
                struct.my_32bit_int = iprot.readI32();
                struct.setMy_32bit_intIsSet(true);
            }
            if (incoming.get(4)) {
                struct.my_64bit_int = iprot.readI64();
                struct.setMy_64bit_intIsSet(true);
            }
            if (incoming.get(5)) {
                struct.my_double = iprot.readDouble();
                struct.setMy_doubleIsSet(true);
            }
            if (incoming.get(6)) {
                struct.my_string = iprot.readString();
                struct.setMy_stringIsSet(true);
            }
            if (incoming.get(7)) {
                struct.my_binary = iprot.readBinary();
                struct.setMy_binaryIsSet(true);
            }
            if (incoming.get(8)) {
                final TMap _map76 = new TMap((byte)11, (byte)11, iprot.readI32());
                struct.my_string_string_map = (Map<String, String>)new HashMap(2 * _map76.size);
                for (int _i77 = 0; _i77 < _map76.size; ++_i77) {
                    final String _key78 = iprot.readString();
                    final String _val79 = iprot.readString();
                    struct.my_string_string_map.put(_key78, _val79);
                }
                struct.setMy_string_string_mapIsSet(true);
            }
            if (incoming.get(9)) {
                final TMap _map77 = new TMap((byte)11, (byte)8, iprot.readI32());
                struct.my_string_enum_map = (Map<String, MyEnum>)new HashMap(2 * _map77.size);
                for (int _i78 = 0; _i78 < _map77.size; ++_i78) {
                    final String _key79 = iprot.readString();
                    final MyEnum _val80 = MyEnum.findByValue(iprot.readI32());
                    struct.my_string_enum_map.put(_key79, _val80);
                }
                struct.setMy_string_enum_mapIsSet(true);
            }
            if (incoming.get(10)) {
                final TMap _map78 = new TMap((byte)8, (byte)11, iprot.readI32());
                struct.my_enum_string_map = (Map<MyEnum, String>)new HashMap(2 * _map78.size);
                for (int _i79 = 0; _i79 < _map78.size; ++_i79) {
                    final MyEnum _key80 = MyEnum.findByValue(iprot.readI32());
                    final String _val81 = iprot.readString();
                    struct.my_enum_string_map.put(_key80, _val81);
                }
                struct.setMy_enum_string_mapIsSet(true);
            }
            if (incoming.get(11)) {
                final TMap _map79 = new TMap((byte)8, (byte)12, iprot.readI32());
                struct.my_enum_struct_map = (Map<MyEnum, MiniStruct>)new HashMap(2 * _map79.size);
                for (int _i80 = 0; _i80 < _map79.size; ++_i80) {
                    final MyEnum _key81 = MyEnum.findByValue(iprot.readI32());
                    final MiniStruct _val82 = new MiniStruct();
                    _val82.read(iprot);
                    struct.my_enum_struct_map.put(_key81, _val82);
                }
                struct.setMy_enum_struct_mapIsSet(true);
            }
            if (incoming.get(12)) {
                final TMap _map80 = new TMap((byte)8, (byte)15, iprot.readI32());
                struct.my_enum_stringlist_map = (Map<MyEnum, List<String>>)new HashMap(2 * _map80.size);
                for (int _i81 = 0; _i81 < _map80.size; ++_i81) {
                    final MyEnum _key82 = MyEnum.findByValue(iprot.readI32());
                    final TList _list96 = new TList((byte)11, iprot.readI32());
                    final List<String> _val83 = new ArrayList<String>(_list96.size);
                    for (int _i82 = 0; _i82 < _list96.size; ++_i82) {
                        final String _elem98 = iprot.readString();
                        _val83.add(_elem98);
                    }
                    struct.my_enum_stringlist_map.put(_key82, _val83);
                }
                struct.setMy_enum_stringlist_mapIsSet(true);
            }
            if (incoming.get(13)) {
                final TMap _map81 = new TMap((byte)8, (byte)15, iprot.readI32());
                struct.my_enum_structlist_map = (Map<MyEnum, List<MiniStruct>>)new HashMap(2 * _map81.size);
                for (int _i83 = 0; _i83 < _map81.size; ++_i83) {
                    final MyEnum _key83 = MyEnum.findByValue(iprot.readI32());
                    final TList _list97 = new TList((byte)12, iprot.readI32());
                    final List<MiniStruct> _val84 = new ArrayList<MiniStruct>(_list97.size);
                    for (int _i84 = 0; _i84 < _list97.size; ++_i84) {
                        final MiniStruct _elem99 = new MiniStruct();
                        _elem99.read(iprot);
                        _val84.add(_elem99);
                    }
                    struct.my_enum_structlist_map.put(_key83, _val84);
                }
                struct.setMy_enum_structlist_mapIsSet(true);
            }
            if (incoming.get(14)) {
                final TList _list98 = new TList((byte)11, iprot.readI32());
                struct.my_stringlist = (List<String>)new ArrayList(_list98.size);
                for (int _i85 = 0; _i85 < _list98.size; ++_i85) {
                    final String _elem100 = iprot.readString();
                    struct.my_stringlist.add(_elem100);
                }
                struct.setMy_stringlistIsSet(true);
            }
            if (incoming.get(15)) {
                final TList _list99 = new TList((byte)12, iprot.readI32());
                struct.my_structlist = (List<MiniStruct>)new ArrayList(_list99.size);
                for (int _i86 = 0; _i86 < _list99.size; ++_i86) {
                    final MiniStruct _elem101 = new MiniStruct();
                    _elem101.read(iprot);
                    struct.my_structlist.add(_elem101);
                }
                struct.setMy_structlistIsSet(true);
            }
            if (incoming.get(16)) {
                final TList _list100 = new TList((byte)8, iprot.readI32());
                struct.my_enumlist = (List<MyEnum>)new ArrayList(_list100.size);
                for (int _i87 = 0; _i87 < _list100.size; ++_i87) {
                    final MyEnum _elem102 = MyEnum.findByValue(iprot.readI32());
                    struct.my_enumlist.add(_elem102);
                }
                struct.setMy_enumlistIsSet(true);
            }
            if (incoming.get(17)) {
                final TSet _set115 = new TSet((byte)11, iprot.readI32());
                struct.my_stringset = (Set<String>)new HashSet(2 * _set115.size);
                for (int _i88 = 0; _i88 < _set115.size; ++_i88) {
                    final String _elem103 = iprot.readString();
                    struct.my_stringset.add(_elem103);
                }
                struct.setMy_stringsetIsSet(true);
            }
            if (incoming.get(18)) {
                final TSet _set116 = new TSet((byte)8, iprot.readI32());
                struct.my_enumset = (Set<MyEnum>)new HashSet(2 * _set116.size);
                for (int _i89 = 0; _i89 < _set116.size; ++_i89) {
                    final MyEnum _elem104 = MyEnum.findByValue(iprot.readI32());
                    struct.my_enumset.add(_elem104);
                }
                struct.setMy_enumsetIsSet(true);
            }
            if (incoming.get(19)) {
                final TSet _set117 = new TSet((byte)12, iprot.readI32());
                struct.my_structset = (Set<MiniStruct>)new HashSet(2 * _set117.size);
                for (int _i90 = 0; _i90 < _set117.size; ++_i90) {
                    final MiniStruct _elem105 = new MiniStruct();
                    _elem105.read(iprot);
                    struct.my_structset.add(_elem105);
                }
                struct.setMy_structsetIsSet(true);
            }
        }
    }
}
