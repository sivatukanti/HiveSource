// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.hive.serde2.thrift.test;

import java.util.BitSet;
import org.apache.thrift.protocol.TTupleProtocol;
import org.apache.thrift.protocol.TMap;
import org.apache.thrift.protocol.TList;
import org.apache.thrift.protocol.TProtocolUtil;
import java.util.EnumSet;
import java.util.Collections;
import org.apache.thrift.meta_data.MapMetaData;
import org.apache.thrift.meta_data.StructMetaData;
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

public class Complex implements TBase<Complex, _Fields>, Serializable, Cloneable
{
    private static final TStruct STRUCT_DESC;
    private static final TField AINT_FIELD_DESC;
    private static final TField A_STRING_FIELD_DESC;
    private static final TField LINT_FIELD_DESC;
    private static final TField L_STRING_FIELD_DESC;
    private static final TField LINT_STRING_FIELD_DESC;
    private static final TField M_STRING_STRING_FIELD_DESC;
    private static final TField ATTRIBUTES_FIELD_DESC;
    private static final TField UNION_FIELD1_FIELD_DESC;
    private static final TField UNION_FIELD2_FIELD_DESC;
    private static final TField UNION_FIELD3_FIELD_DESC;
    private static final Map<Class<? extends IScheme>, SchemeFactory> schemes;
    private int aint;
    private String aString;
    private List<Integer> lint;
    private List<String> lString;
    private List<IntString> lintString;
    private Map<String, String> mStringString;
    private Map<String, Map<String, Map<String, PropValueUnion>>> attributes;
    private PropValueUnion unionField1;
    private PropValueUnion unionField2;
    private PropValueUnion unionField3;
    private static final int __AINT_ISSET_ID = 0;
    private byte __isset_bitfield;
    public static final Map<_Fields, FieldMetaData> metaDataMap;
    
    public Complex() {
        this.__isset_bitfield = 0;
    }
    
    public Complex(final int aint, final String aString, final List<Integer> lint, final List<String> lString, final List<IntString> lintString, final Map<String, String> mStringString, final Map<String, Map<String, Map<String, PropValueUnion>>> attributes, final PropValueUnion unionField1, final PropValueUnion unionField2, final PropValueUnion unionField3) {
        this();
        this.aint = aint;
        this.setAintIsSet(true);
        this.aString = aString;
        this.lint = lint;
        this.lString = lString;
        this.lintString = lintString;
        this.mStringString = mStringString;
        this.attributes = attributes;
        this.unionField1 = unionField1;
        this.unionField2 = unionField2;
        this.unionField3 = unionField3;
    }
    
    public Complex(final Complex other) {
        this.__isset_bitfield = 0;
        this.__isset_bitfield = other.__isset_bitfield;
        this.aint = other.aint;
        if (other.isSetAString()) {
            this.aString = other.aString;
        }
        if (other.isSetLint()) {
            final List<Integer> __this__lint = new ArrayList<Integer>();
            for (final Integer other_element : other.lint) {
                __this__lint.add(other_element);
            }
            this.lint = __this__lint;
        }
        if (other.isSetLString()) {
            final List<String> __this__lString = new ArrayList<String>();
            for (final String other_element2 : other.lString) {
                __this__lString.add(other_element2);
            }
            this.lString = __this__lString;
        }
        if (other.isSetLintString()) {
            final List<IntString> __this__lintString = new ArrayList<IntString>();
            for (final IntString other_element3 : other.lintString) {
                __this__lintString.add(new IntString(other_element3));
            }
            this.lintString = __this__lintString;
        }
        if (other.isSetMStringString()) {
            final Map<String, String> __this__mStringString = new HashMap<String, String>();
            for (final Map.Entry<String, String> other_element4 : other.mStringString.entrySet()) {
                final String other_element_key = other_element4.getKey();
                final String other_element_value = other_element4.getValue();
                final String __this__mStringString_copy_key = other_element_key;
                final String __this__mStringString_copy_value = other_element_value;
                __this__mStringString.put(__this__mStringString_copy_key, __this__mStringString_copy_value);
            }
            this.mStringString = __this__mStringString;
        }
        if (other.isSetAttributes()) {
            final Map<String, Map<String, Map<String, PropValueUnion>>> __this__attributes = new HashMap<String, Map<String, Map<String, PropValueUnion>>>();
            for (final Map.Entry<String, Map<String, Map<String, PropValueUnion>>> other_element5 : other.attributes.entrySet()) {
                final String other_element_key = other_element5.getKey();
                final Map<String, Map<String, PropValueUnion>> other_element_value2 = other_element5.getValue();
                final String __this__attributes_copy_key = other_element_key;
                final Map<String, Map<String, PropValueUnion>> __this__attributes_copy_value = new HashMap<String, Map<String, PropValueUnion>>();
                for (final Map.Entry<String, Map<String, PropValueUnion>> other_element_value_element : other_element_value2.entrySet()) {
                    final String other_element_value_element_key = other_element_value_element.getKey();
                    final Map<String, PropValueUnion> other_element_value_element_value = other_element_value_element.getValue();
                    final String __this__attributes_copy_value_copy_key = other_element_value_element_key;
                    final Map<String, PropValueUnion> __this__attributes_copy_value_copy_value = new HashMap<String, PropValueUnion>();
                    for (final Map.Entry<String, PropValueUnion> other_element_value_element_value_element : other_element_value_element_value.entrySet()) {
                        final String other_element_value_element_value_element_key = other_element_value_element_value_element.getKey();
                        final PropValueUnion other_element_value_element_value_element_value = other_element_value_element_value_element.getValue();
                        final String __this__attributes_copy_value_copy_value_copy_key = other_element_value_element_value_element_key;
                        final PropValueUnion __this__attributes_copy_value_copy_value_copy_value = new PropValueUnion(other_element_value_element_value_element_value);
                        __this__attributes_copy_value_copy_value.put(__this__attributes_copy_value_copy_value_copy_key, __this__attributes_copy_value_copy_value_copy_value);
                    }
                    __this__attributes_copy_value.put(__this__attributes_copy_value_copy_key, __this__attributes_copy_value_copy_value);
                }
                __this__attributes.put(__this__attributes_copy_key, __this__attributes_copy_value);
            }
            this.attributes = __this__attributes;
        }
        if (other.isSetUnionField1()) {
            this.unionField1 = new PropValueUnion(other.unionField1);
        }
        if (other.isSetUnionField2()) {
            this.unionField2 = new PropValueUnion(other.unionField2);
        }
        if (other.isSetUnionField3()) {
            this.unionField3 = new PropValueUnion(other.unionField3);
        }
    }
    
    @Override
    public Complex deepCopy() {
        return new Complex(this);
    }
    
    @Override
    public void clear() {
        this.setAintIsSet(false);
        this.aint = 0;
        this.aString = null;
        this.lint = null;
        this.lString = null;
        this.lintString = null;
        this.mStringString = null;
        this.attributes = null;
        this.unionField1 = null;
        this.unionField2 = null;
        this.unionField3 = null;
    }
    
    public int getAint() {
        return this.aint;
    }
    
    public void setAint(final int aint) {
        this.aint = aint;
        this.setAintIsSet(true);
    }
    
    public void unsetAint() {
        this.__isset_bitfield = EncodingUtils.clearBit(this.__isset_bitfield, 0);
    }
    
    public boolean isSetAint() {
        return EncodingUtils.testBit(this.__isset_bitfield, 0);
    }
    
    public void setAintIsSet(final boolean value) {
        this.__isset_bitfield = EncodingUtils.setBit(this.__isset_bitfield, 0, value);
    }
    
    public String getAString() {
        return this.aString;
    }
    
    public void setAString(final String aString) {
        this.aString = aString;
    }
    
    public void unsetAString() {
        this.aString = null;
    }
    
    public boolean isSetAString() {
        return this.aString != null;
    }
    
    public void setAStringIsSet(final boolean value) {
        if (!value) {
            this.aString = null;
        }
    }
    
    public int getLintSize() {
        return (this.lint == null) ? 0 : this.lint.size();
    }
    
    public Iterator<Integer> getLintIterator() {
        return (this.lint == null) ? null : this.lint.iterator();
    }
    
    public void addToLint(final int elem) {
        if (this.lint == null) {
            this.lint = new ArrayList<Integer>();
        }
        this.lint.add(elem);
    }
    
    public List<Integer> getLint() {
        return this.lint;
    }
    
    public void setLint(final List<Integer> lint) {
        this.lint = lint;
    }
    
    public void unsetLint() {
        this.lint = null;
    }
    
    public boolean isSetLint() {
        return this.lint != null;
    }
    
    public void setLintIsSet(final boolean value) {
        if (!value) {
            this.lint = null;
        }
    }
    
    public int getLStringSize() {
        return (this.lString == null) ? 0 : this.lString.size();
    }
    
    public Iterator<String> getLStringIterator() {
        return (this.lString == null) ? null : this.lString.iterator();
    }
    
    public void addToLString(final String elem) {
        if (this.lString == null) {
            this.lString = new ArrayList<String>();
        }
        this.lString.add(elem);
    }
    
    public List<String> getLString() {
        return this.lString;
    }
    
    public void setLString(final List<String> lString) {
        this.lString = lString;
    }
    
    public void unsetLString() {
        this.lString = null;
    }
    
    public boolean isSetLString() {
        return this.lString != null;
    }
    
    public void setLStringIsSet(final boolean value) {
        if (!value) {
            this.lString = null;
        }
    }
    
    public int getLintStringSize() {
        return (this.lintString == null) ? 0 : this.lintString.size();
    }
    
    public Iterator<IntString> getLintStringIterator() {
        return (this.lintString == null) ? null : this.lintString.iterator();
    }
    
    public void addToLintString(final IntString elem) {
        if (this.lintString == null) {
            this.lintString = new ArrayList<IntString>();
        }
        this.lintString.add(elem);
    }
    
    public List<IntString> getLintString() {
        return this.lintString;
    }
    
    public void setLintString(final List<IntString> lintString) {
        this.lintString = lintString;
    }
    
    public void unsetLintString() {
        this.lintString = null;
    }
    
    public boolean isSetLintString() {
        return this.lintString != null;
    }
    
    public void setLintStringIsSet(final boolean value) {
        if (!value) {
            this.lintString = null;
        }
    }
    
    public int getMStringStringSize() {
        return (this.mStringString == null) ? 0 : this.mStringString.size();
    }
    
    public void putToMStringString(final String key, final String val) {
        if (this.mStringString == null) {
            this.mStringString = new HashMap<String, String>();
        }
        this.mStringString.put(key, val);
    }
    
    public Map<String, String> getMStringString() {
        return this.mStringString;
    }
    
    public void setMStringString(final Map<String, String> mStringString) {
        this.mStringString = mStringString;
    }
    
    public void unsetMStringString() {
        this.mStringString = null;
    }
    
    public boolean isSetMStringString() {
        return this.mStringString != null;
    }
    
    public void setMStringStringIsSet(final boolean value) {
        if (!value) {
            this.mStringString = null;
        }
    }
    
    public int getAttributesSize() {
        return (this.attributes == null) ? 0 : this.attributes.size();
    }
    
    public void putToAttributes(final String key, final Map<String, Map<String, PropValueUnion>> val) {
        if (this.attributes == null) {
            this.attributes = new HashMap<String, Map<String, Map<String, PropValueUnion>>>();
        }
        this.attributes.put(key, val);
    }
    
    public Map<String, Map<String, Map<String, PropValueUnion>>> getAttributes() {
        return this.attributes;
    }
    
    public void setAttributes(final Map<String, Map<String, Map<String, PropValueUnion>>> attributes) {
        this.attributes = attributes;
    }
    
    public void unsetAttributes() {
        this.attributes = null;
    }
    
    public boolean isSetAttributes() {
        return this.attributes != null;
    }
    
    public void setAttributesIsSet(final boolean value) {
        if (!value) {
            this.attributes = null;
        }
    }
    
    public PropValueUnion getUnionField1() {
        return this.unionField1;
    }
    
    public void setUnionField1(final PropValueUnion unionField1) {
        this.unionField1 = unionField1;
    }
    
    public void unsetUnionField1() {
        this.unionField1 = null;
    }
    
    public boolean isSetUnionField1() {
        return this.unionField1 != null;
    }
    
    public void setUnionField1IsSet(final boolean value) {
        if (!value) {
            this.unionField1 = null;
        }
    }
    
    public PropValueUnion getUnionField2() {
        return this.unionField2;
    }
    
    public void setUnionField2(final PropValueUnion unionField2) {
        this.unionField2 = unionField2;
    }
    
    public void unsetUnionField2() {
        this.unionField2 = null;
    }
    
    public boolean isSetUnionField2() {
        return this.unionField2 != null;
    }
    
    public void setUnionField2IsSet(final boolean value) {
        if (!value) {
            this.unionField2 = null;
        }
    }
    
    public PropValueUnion getUnionField3() {
        return this.unionField3;
    }
    
    public void setUnionField3(final PropValueUnion unionField3) {
        this.unionField3 = unionField3;
    }
    
    public void unsetUnionField3() {
        this.unionField3 = null;
    }
    
    public boolean isSetUnionField3() {
        return this.unionField3 != null;
    }
    
    public void setUnionField3IsSet(final boolean value) {
        if (!value) {
            this.unionField3 = null;
        }
    }
    
    @Override
    public void setFieldValue(final _Fields field, final Object value) {
        switch (field) {
            case AINT: {
                if (value == null) {
                    this.unsetAint();
                    break;
                }
                this.setAint((int)value);
                break;
            }
            case A_STRING: {
                if (value == null) {
                    this.unsetAString();
                    break;
                }
                this.setAString((String)value);
                break;
            }
            case LINT: {
                if (value == null) {
                    this.unsetLint();
                    break;
                }
                this.setLint((List<Integer>)value);
                break;
            }
            case L_STRING: {
                if (value == null) {
                    this.unsetLString();
                    break;
                }
                this.setLString((List<String>)value);
                break;
            }
            case LINT_STRING: {
                if (value == null) {
                    this.unsetLintString();
                    break;
                }
                this.setLintString((List<IntString>)value);
                break;
            }
            case M_STRING_STRING: {
                if (value == null) {
                    this.unsetMStringString();
                    break;
                }
                this.setMStringString((Map<String, String>)value);
                break;
            }
            case ATTRIBUTES: {
                if (value == null) {
                    this.unsetAttributes();
                    break;
                }
                this.setAttributes((Map<String, Map<String, Map<String, PropValueUnion>>>)value);
                break;
            }
            case UNION_FIELD1: {
                if (value == null) {
                    this.unsetUnionField1();
                    break;
                }
                this.setUnionField1((PropValueUnion)value);
                break;
            }
            case UNION_FIELD2: {
                if (value == null) {
                    this.unsetUnionField2();
                    break;
                }
                this.setUnionField2((PropValueUnion)value);
                break;
            }
            case UNION_FIELD3: {
                if (value == null) {
                    this.unsetUnionField3();
                    break;
                }
                this.setUnionField3((PropValueUnion)value);
                break;
            }
        }
    }
    
    @Override
    public Object getFieldValue(final _Fields field) {
        switch (field) {
            case AINT: {
                return this.getAint();
            }
            case A_STRING: {
                return this.getAString();
            }
            case LINT: {
                return this.getLint();
            }
            case L_STRING: {
                return this.getLString();
            }
            case LINT_STRING: {
                return this.getLintString();
            }
            case M_STRING_STRING: {
                return this.getMStringString();
            }
            case ATTRIBUTES: {
                return this.getAttributes();
            }
            case UNION_FIELD1: {
                return this.getUnionField1();
            }
            case UNION_FIELD2: {
                return this.getUnionField2();
            }
            case UNION_FIELD3: {
                return this.getUnionField3();
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
            case AINT: {
                return this.isSetAint();
            }
            case A_STRING: {
                return this.isSetAString();
            }
            case LINT: {
                return this.isSetLint();
            }
            case L_STRING: {
                return this.isSetLString();
            }
            case LINT_STRING: {
                return this.isSetLintString();
            }
            case M_STRING_STRING: {
                return this.isSetMStringString();
            }
            case ATTRIBUTES: {
                return this.isSetAttributes();
            }
            case UNION_FIELD1: {
                return this.isSetUnionField1();
            }
            case UNION_FIELD2: {
                return this.isSetUnionField2();
            }
            case UNION_FIELD3: {
                return this.isSetUnionField3();
            }
            default: {
                throw new IllegalStateException();
            }
        }
    }
    
    @Override
    public boolean equals(final Object that) {
        return that != null && that instanceof Complex && this.equals((Complex)that);
    }
    
    public boolean equals(final Complex that) {
        if (that == null) {
            return false;
        }
        final boolean this_present_aint = true;
        final boolean that_present_aint = true;
        if (this_present_aint || that_present_aint) {
            if (!this_present_aint || !that_present_aint) {
                return false;
            }
            if (this.aint != that.aint) {
                return false;
            }
        }
        final boolean this_present_aString = this.isSetAString();
        final boolean that_present_aString = that.isSetAString();
        if (this_present_aString || that_present_aString) {
            if (!this_present_aString || !that_present_aString) {
                return false;
            }
            if (!this.aString.equals(that.aString)) {
                return false;
            }
        }
        final boolean this_present_lint = this.isSetLint();
        final boolean that_present_lint = that.isSetLint();
        if (this_present_lint || that_present_lint) {
            if (!this_present_lint || !that_present_lint) {
                return false;
            }
            if (!this.lint.equals(that.lint)) {
                return false;
            }
        }
        final boolean this_present_lString = this.isSetLString();
        final boolean that_present_lString = that.isSetLString();
        if (this_present_lString || that_present_lString) {
            if (!this_present_lString || !that_present_lString) {
                return false;
            }
            if (!this.lString.equals(that.lString)) {
                return false;
            }
        }
        final boolean this_present_lintString = this.isSetLintString();
        final boolean that_present_lintString = that.isSetLintString();
        if (this_present_lintString || that_present_lintString) {
            if (!this_present_lintString || !that_present_lintString) {
                return false;
            }
            if (!this.lintString.equals(that.lintString)) {
                return false;
            }
        }
        final boolean this_present_mStringString = this.isSetMStringString();
        final boolean that_present_mStringString = that.isSetMStringString();
        if (this_present_mStringString || that_present_mStringString) {
            if (!this_present_mStringString || !that_present_mStringString) {
                return false;
            }
            if (!this.mStringString.equals(that.mStringString)) {
                return false;
            }
        }
        final boolean this_present_attributes = this.isSetAttributes();
        final boolean that_present_attributes = that.isSetAttributes();
        if (this_present_attributes || that_present_attributes) {
            if (!this_present_attributes || !that_present_attributes) {
                return false;
            }
            if (!this.attributes.equals(that.attributes)) {
                return false;
            }
        }
        final boolean this_present_unionField1 = this.isSetUnionField1();
        final boolean that_present_unionField1 = that.isSetUnionField1();
        if (this_present_unionField1 || that_present_unionField1) {
            if (!this_present_unionField1 || !that_present_unionField1) {
                return false;
            }
            if (!this.unionField1.equals(that.unionField1)) {
                return false;
            }
        }
        final boolean this_present_unionField2 = this.isSetUnionField2();
        final boolean that_present_unionField2 = that.isSetUnionField2();
        if (this_present_unionField2 || that_present_unionField2) {
            if (!this_present_unionField2 || !that_present_unionField2) {
                return false;
            }
            if (!this.unionField2.equals(that.unionField2)) {
                return false;
            }
        }
        final boolean this_present_unionField3 = this.isSetUnionField3();
        final boolean that_present_unionField3 = that.isSetUnionField3();
        if (this_present_unionField3 || that_present_unionField3) {
            if (!this_present_unionField3 || !that_present_unionField3) {
                return false;
            }
            if (!this.unionField3.equals(that.unionField3)) {
                return false;
            }
        }
        return true;
    }
    
    @Override
    public int hashCode() {
        final HashCodeBuilder builder = new HashCodeBuilder();
        final boolean present_aint = true;
        builder.append(present_aint);
        if (present_aint) {
            builder.append(this.aint);
        }
        final boolean present_aString = this.isSetAString();
        builder.append(present_aString);
        if (present_aString) {
            builder.append(this.aString);
        }
        final boolean present_lint = this.isSetLint();
        builder.append(present_lint);
        if (present_lint) {
            builder.append(this.lint);
        }
        final boolean present_lString = this.isSetLString();
        builder.append(present_lString);
        if (present_lString) {
            builder.append(this.lString);
        }
        final boolean present_lintString = this.isSetLintString();
        builder.append(present_lintString);
        if (present_lintString) {
            builder.append(this.lintString);
        }
        final boolean present_mStringString = this.isSetMStringString();
        builder.append(present_mStringString);
        if (present_mStringString) {
            builder.append(this.mStringString);
        }
        final boolean present_attributes = this.isSetAttributes();
        builder.append(present_attributes);
        if (present_attributes) {
            builder.append(this.attributes);
        }
        final boolean present_unionField1 = this.isSetUnionField1();
        builder.append(present_unionField1);
        if (present_unionField1) {
            builder.append(this.unionField1);
        }
        final boolean present_unionField2 = this.isSetUnionField2();
        builder.append(present_unionField2);
        if (present_unionField2) {
            builder.append(this.unionField2);
        }
        final boolean present_unionField3 = this.isSetUnionField3();
        builder.append(present_unionField3);
        if (present_unionField3) {
            builder.append(this.unionField3);
        }
        return builder.toHashCode();
    }
    
    @Override
    public int compareTo(final Complex other) {
        if (!this.getClass().equals(other.getClass())) {
            return this.getClass().getName().compareTo(other.getClass().getName());
        }
        int lastComparison = 0;
        final Complex typedOther = other;
        lastComparison = Boolean.valueOf(this.isSetAint()).compareTo(Boolean.valueOf(typedOther.isSetAint()));
        if (lastComparison != 0) {
            return lastComparison;
        }
        if (this.isSetAint()) {
            lastComparison = TBaseHelper.compareTo(this.aint, typedOther.aint);
            if (lastComparison != 0) {
                return lastComparison;
            }
        }
        lastComparison = Boolean.valueOf(this.isSetAString()).compareTo(Boolean.valueOf(typedOther.isSetAString()));
        if (lastComparison != 0) {
            return lastComparison;
        }
        if (this.isSetAString()) {
            lastComparison = TBaseHelper.compareTo(this.aString, typedOther.aString);
            if (lastComparison != 0) {
                return lastComparison;
            }
        }
        lastComparison = Boolean.valueOf(this.isSetLint()).compareTo(Boolean.valueOf(typedOther.isSetLint()));
        if (lastComparison != 0) {
            return lastComparison;
        }
        if (this.isSetLint()) {
            lastComparison = TBaseHelper.compareTo(this.lint, typedOther.lint);
            if (lastComparison != 0) {
                return lastComparison;
            }
        }
        lastComparison = Boolean.valueOf(this.isSetLString()).compareTo(Boolean.valueOf(typedOther.isSetLString()));
        if (lastComparison != 0) {
            return lastComparison;
        }
        if (this.isSetLString()) {
            lastComparison = TBaseHelper.compareTo(this.lString, typedOther.lString);
            if (lastComparison != 0) {
                return lastComparison;
            }
        }
        lastComparison = Boolean.valueOf(this.isSetLintString()).compareTo(Boolean.valueOf(typedOther.isSetLintString()));
        if (lastComparison != 0) {
            return lastComparison;
        }
        if (this.isSetLintString()) {
            lastComparison = TBaseHelper.compareTo(this.lintString, typedOther.lintString);
            if (lastComparison != 0) {
                return lastComparison;
            }
        }
        lastComparison = Boolean.valueOf(this.isSetMStringString()).compareTo(Boolean.valueOf(typedOther.isSetMStringString()));
        if (lastComparison != 0) {
            return lastComparison;
        }
        if (this.isSetMStringString()) {
            lastComparison = TBaseHelper.compareTo(this.mStringString, typedOther.mStringString);
            if (lastComparison != 0) {
                return lastComparison;
            }
        }
        lastComparison = Boolean.valueOf(this.isSetAttributes()).compareTo(Boolean.valueOf(typedOther.isSetAttributes()));
        if (lastComparison != 0) {
            return lastComparison;
        }
        if (this.isSetAttributes()) {
            lastComparison = TBaseHelper.compareTo(this.attributes, typedOther.attributes);
            if (lastComparison != 0) {
                return lastComparison;
            }
        }
        lastComparison = Boolean.valueOf(this.isSetUnionField1()).compareTo(Boolean.valueOf(typedOther.isSetUnionField1()));
        if (lastComparison != 0) {
            return lastComparison;
        }
        if (this.isSetUnionField1()) {
            lastComparison = TBaseHelper.compareTo(this.unionField1, typedOther.unionField1);
            if (lastComparison != 0) {
                return lastComparison;
            }
        }
        lastComparison = Boolean.valueOf(this.isSetUnionField2()).compareTo(Boolean.valueOf(typedOther.isSetUnionField2()));
        if (lastComparison != 0) {
            return lastComparison;
        }
        if (this.isSetUnionField2()) {
            lastComparison = TBaseHelper.compareTo(this.unionField2, typedOther.unionField2);
            if (lastComparison != 0) {
                return lastComparison;
            }
        }
        lastComparison = Boolean.valueOf(this.isSetUnionField3()).compareTo(Boolean.valueOf(typedOther.isSetUnionField3()));
        if (lastComparison != 0) {
            return lastComparison;
        }
        if (this.isSetUnionField3()) {
            lastComparison = TBaseHelper.compareTo(this.unionField3, typedOther.unionField3);
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
        Complex.schemes.get(iprot.getScheme()).getScheme().read(iprot, this);
    }
    
    @Override
    public void write(final TProtocol oprot) throws TException {
        Complex.schemes.get(oprot.getScheme()).getScheme().write(oprot, this);
    }
    
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("Complex(");
        boolean first = true;
        sb.append("aint:");
        sb.append(this.aint);
        first = false;
        if (!first) {
            sb.append(", ");
        }
        sb.append("aString:");
        if (this.aString == null) {
            sb.append("null");
        }
        else {
            sb.append(this.aString);
        }
        first = false;
        if (!first) {
            sb.append(", ");
        }
        sb.append("lint:");
        if (this.lint == null) {
            sb.append("null");
        }
        else {
            sb.append(this.lint);
        }
        first = false;
        if (!first) {
            sb.append(", ");
        }
        sb.append("lString:");
        if (this.lString == null) {
            sb.append("null");
        }
        else {
            sb.append(this.lString);
        }
        first = false;
        if (!first) {
            sb.append(", ");
        }
        sb.append("lintString:");
        if (this.lintString == null) {
            sb.append("null");
        }
        else {
            sb.append(this.lintString);
        }
        first = false;
        if (!first) {
            sb.append(", ");
        }
        sb.append("mStringString:");
        if (this.mStringString == null) {
            sb.append("null");
        }
        else {
            sb.append(this.mStringString);
        }
        first = false;
        if (!first) {
            sb.append(", ");
        }
        sb.append("attributes:");
        if (this.attributes == null) {
            sb.append("null");
        }
        else {
            sb.append(this.attributes);
        }
        first = false;
        if (!first) {
            sb.append(", ");
        }
        sb.append("unionField1:");
        if (this.unionField1 == null) {
            sb.append("null");
        }
        else {
            sb.append(this.unionField1);
        }
        first = false;
        if (!first) {
            sb.append(", ");
        }
        sb.append("unionField2:");
        if (this.unionField2 == null) {
            sb.append("null");
        }
        else {
            sb.append(this.unionField2);
        }
        first = false;
        if (!first) {
            sb.append(", ");
        }
        sb.append("unionField3:");
        if (this.unionField3 == null) {
            sb.append("null");
        }
        else {
            sb.append(this.unionField3);
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
            this.__isset_bitfield = 0;
            this.read(new TCompactProtocol(new TIOStreamTransport(in)));
        }
        catch (TException te) {
            throw new IOException(te);
        }
    }
    
    static {
        STRUCT_DESC = new TStruct("Complex");
        AINT_FIELD_DESC = new TField("aint", (byte)8, (short)1);
        A_STRING_FIELD_DESC = new TField("aString", (byte)11, (short)2);
        LINT_FIELD_DESC = new TField("lint", (byte)15, (short)3);
        L_STRING_FIELD_DESC = new TField("lString", (byte)15, (short)4);
        LINT_STRING_FIELD_DESC = new TField("lintString", (byte)15, (short)5);
        M_STRING_STRING_FIELD_DESC = new TField("mStringString", (byte)13, (short)6);
        ATTRIBUTES_FIELD_DESC = new TField("attributes", (byte)13, (short)7);
        UNION_FIELD1_FIELD_DESC = new TField("unionField1", (byte)12, (short)8);
        UNION_FIELD2_FIELD_DESC = new TField("unionField2", (byte)12, (short)9);
        UNION_FIELD3_FIELD_DESC = new TField("unionField3", (byte)12, (short)10);
        (schemes = new HashMap<Class<? extends IScheme>, SchemeFactory>()).put(StandardScheme.class, new ComplexStandardSchemeFactory());
        Complex.schemes.put(TupleScheme.class, new ComplexTupleSchemeFactory());
        final Map<_Fields, FieldMetaData> tmpMap = new EnumMap<_Fields, FieldMetaData>(_Fields.class);
        tmpMap.put(_Fields.AINT, new FieldMetaData("aint", (byte)3, new FieldValueMetaData((byte)8)));
        tmpMap.put(_Fields.A_STRING, new FieldMetaData("aString", (byte)3, new FieldValueMetaData((byte)11)));
        tmpMap.put(_Fields.LINT, new FieldMetaData("lint", (byte)3, new ListMetaData((byte)15, new FieldValueMetaData((byte)8))));
        tmpMap.put(_Fields.L_STRING, new FieldMetaData("lString", (byte)3, new ListMetaData((byte)15, new FieldValueMetaData((byte)11))));
        tmpMap.put(_Fields.LINT_STRING, new FieldMetaData("lintString", (byte)3, new ListMetaData((byte)15, new StructMetaData((byte)12, IntString.class))));
        tmpMap.put(_Fields.M_STRING_STRING, new FieldMetaData("mStringString", (byte)3, new MapMetaData((byte)13, new FieldValueMetaData((byte)11), new FieldValueMetaData((byte)11))));
        tmpMap.put(_Fields.ATTRIBUTES, new FieldMetaData("attributes", (byte)3, new MapMetaData((byte)13, new FieldValueMetaData((byte)11), new MapMetaData((byte)13, new FieldValueMetaData((byte)11), new MapMetaData((byte)13, new FieldValueMetaData((byte)11), new StructMetaData((byte)12, PropValueUnion.class))))));
        tmpMap.put(_Fields.UNION_FIELD1, new FieldMetaData("unionField1", (byte)3, new StructMetaData((byte)12, PropValueUnion.class)));
        tmpMap.put(_Fields.UNION_FIELD2, new FieldMetaData("unionField2", (byte)3, new StructMetaData((byte)12, PropValueUnion.class)));
        tmpMap.put(_Fields.UNION_FIELD3, new FieldMetaData("unionField3", (byte)3, new StructMetaData((byte)12, PropValueUnion.class)));
        FieldMetaData.addStructMetaDataMap(Complex.class, metaDataMap = Collections.unmodifiableMap((Map<? extends _Fields, ? extends FieldMetaData>)tmpMap));
    }
    
    public enum _Fields implements TFieldIdEnum
    {
        AINT((short)1, "aint"), 
        A_STRING((short)2, "aString"), 
        LINT((short)3, "lint"), 
        L_STRING((short)4, "lString"), 
        LINT_STRING((short)5, "lintString"), 
        M_STRING_STRING((short)6, "mStringString"), 
        ATTRIBUTES((short)7, "attributes"), 
        UNION_FIELD1((short)8, "unionField1"), 
        UNION_FIELD2((short)9, "unionField2"), 
        UNION_FIELD3((short)10, "unionField3");
        
        private static final Map<String, _Fields> byName;
        private final short _thriftId;
        private final String _fieldName;
        
        public static _Fields findByThriftId(final int fieldId) {
            switch (fieldId) {
                case 1: {
                    return _Fields.AINT;
                }
                case 2: {
                    return _Fields.A_STRING;
                }
                case 3: {
                    return _Fields.LINT;
                }
                case 4: {
                    return _Fields.L_STRING;
                }
                case 5: {
                    return _Fields.LINT_STRING;
                }
                case 6: {
                    return _Fields.M_STRING_STRING;
                }
                case 7: {
                    return _Fields.ATTRIBUTES;
                }
                case 8: {
                    return _Fields.UNION_FIELD1;
                }
                case 9: {
                    return _Fields.UNION_FIELD2;
                }
                case 10: {
                    return _Fields.UNION_FIELD3;
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
    
    private static class ComplexStandardSchemeFactory implements SchemeFactory
    {
        @Override
        public ComplexStandardScheme getScheme() {
            return new ComplexStandardScheme();
        }
    }
    
    private static class ComplexStandardScheme extends StandardScheme<Complex>
    {
        @Override
        public void read(final TProtocol iprot, final Complex struct) throws TException {
            iprot.readStructBegin();
            while (true) {
                final TField schemeField = iprot.readFieldBegin();
                if (schemeField.type == 0) {
                    break;
                }
                switch (schemeField.id) {
                    case 1: {
                        if (schemeField.type == 8) {
                            struct.aint = iprot.readI32();
                            struct.setAintIsSet(true);
                            break;
                        }
                        TProtocolUtil.skip(iprot, schemeField.type);
                        break;
                    }
                    case 2: {
                        if (schemeField.type == 11) {
                            struct.aString = iprot.readString();
                            struct.setAStringIsSet(true);
                            break;
                        }
                        TProtocolUtil.skip(iprot, schemeField.type);
                        break;
                    }
                    case 3: {
                        if (schemeField.type == 15) {
                            final TList _list18 = iprot.readListBegin();
                            struct.lint = (List<Integer>)new ArrayList(_list18.size);
                            for (int _i19 = 0; _i19 < _list18.size; ++_i19) {
                                final int _elem20 = iprot.readI32();
                                struct.lint.add(_elem20);
                            }
                            iprot.readListEnd();
                            struct.setLintIsSet(true);
                            break;
                        }
                        TProtocolUtil.skip(iprot, schemeField.type);
                        break;
                    }
                    case 4: {
                        if (schemeField.type == 15) {
                            final TList _list19 = iprot.readListBegin();
                            struct.lString = (List<String>)new ArrayList(_list19.size);
                            for (int _i20 = 0; _i20 < _list19.size; ++_i20) {
                                final String _elem21 = iprot.readString();
                                struct.lString.add(_elem21);
                            }
                            iprot.readListEnd();
                            struct.setLStringIsSet(true);
                            break;
                        }
                        TProtocolUtil.skip(iprot, schemeField.type);
                        break;
                    }
                    case 5: {
                        if (schemeField.type == 15) {
                            final TList _list20 = iprot.readListBegin();
                            struct.lintString = (List<IntString>)new ArrayList(_list20.size);
                            for (int _i21 = 0; _i21 < _list20.size; ++_i21) {
                                final IntString _elem22 = new IntString();
                                _elem22.read(iprot);
                                struct.lintString.add(_elem22);
                            }
                            iprot.readListEnd();
                            struct.setLintStringIsSet(true);
                            break;
                        }
                        TProtocolUtil.skip(iprot, schemeField.type);
                        break;
                    }
                    case 6: {
                        if (schemeField.type == 13) {
                            final TMap _map27 = iprot.readMapBegin();
                            struct.mStringString = (Map<String, String>)new HashMap(2 * _map27.size);
                            for (int _i22 = 0; _i22 < _map27.size; ++_i22) {
                                final String _key29 = iprot.readString();
                                final String _val30 = iprot.readString();
                                struct.mStringString.put(_key29, _val30);
                            }
                            iprot.readMapEnd();
                            struct.setMStringStringIsSet(true);
                            break;
                        }
                        TProtocolUtil.skip(iprot, schemeField.type);
                        break;
                    }
                    case 7: {
                        if (schemeField.type == 13) {
                            final TMap _map28 = iprot.readMapBegin();
                            struct.attributes = (Map<String, Map<String, Map<String, PropValueUnion>>>)new HashMap(2 * _map28.size);
                            for (int _i23 = 0; _i23 < _map28.size; ++_i23) {
                                final String _key30 = iprot.readString();
                                final TMap _map29 = iprot.readMapBegin();
                                final Map<String, Map<String, PropValueUnion>> _val31 = new HashMap<String, Map<String, PropValueUnion>>(2 * _map29.size);
                                for (int _i24 = 0; _i24 < _map29.size; ++_i24) {
                                    final String _key31 = iprot.readString();
                                    final TMap _map30 = iprot.readMapBegin();
                                    final Map<String, PropValueUnion> _val32 = new HashMap<String, PropValueUnion>(2 * _map30.size);
                                    for (int _i25 = 0; _i25 < _map30.size; ++_i25) {
                                        final String _key32 = iprot.readString();
                                        final PropValueUnion _val33 = new PropValueUnion();
                                        _val33.read(iprot);
                                        _val32.put(_key32, _val33);
                                    }
                                    iprot.readMapEnd();
                                    _val31.put(_key31, _val32);
                                }
                                iprot.readMapEnd();
                                struct.attributes.put(_key30, _val31);
                            }
                            iprot.readMapEnd();
                            struct.setAttributesIsSet(true);
                            break;
                        }
                        TProtocolUtil.skip(iprot, schemeField.type);
                        break;
                    }
                    case 8: {
                        if (schemeField.type == 12) {
                            struct.unionField1 = new PropValueUnion();
                            struct.unionField1.read(iprot);
                            struct.setUnionField1IsSet(true);
                            break;
                        }
                        TProtocolUtil.skip(iprot, schemeField.type);
                        break;
                    }
                    case 9: {
                        if (schemeField.type == 12) {
                            struct.unionField2 = new PropValueUnion();
                            struct.unionField2.read(iprot);
                            struct.setUnionField2IsSet(true);
                            break;
                        }
                        TProtocolUtil.skip(iprot, schemeField.type);
                        break;
                    }
                    case 10: {
                        if (schemeField.type == 12) {
                            struct.unionField3 = new PropValueUnion();
                            struct.unionField3.read(iprot);
                            struct.setUnionField3IsSet(true);
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
        public void write(final TProtocol oprot, final Complex struct) throws TException {
            struct.validate();
            oprot.writeStructBegin(Complex.STRUCT_DESC);
            oprot.writeFieldBegin(Complex.AINT_FIELD_DESC);
            oprot.writeI32(struct.aint);
            oprot.writeFieldEnd();
            if (struct.aString != null) {
                oprot.writeFieldBegin(Complex.A_STRING_FIELD_DESC);
                oprot.writeString(struct.aString);
                oprot.writeFieldEnd();
            }
            if (struct.lint != null) {
                oprot.writeFieldBegin(Complex.LINT_FIELD_DESC);
                oprot.writeListBegin(new TList((byte)8, struct.lint.size()));
                for (final int _iter43 : struct.lint) {
                    oprot.writeI32(_iter43);
                }
                oprot.writeListEnd();
                oprot.writeFieldEnd();
            }
            if (struct.lString != null) {
                oprot.writeFieldBegin(Complex.L_STRING_FIELD_DESC);
                oprot.writeListBegin(new TList((byte)11, struct.lString.size()));
                for (final String _iter44 : struct.lString) {
                    oprot.writeString(_iter44);
                }
                oprot.writeListEnd();
                oprot.writeFieldEnd();
            }
            if (struct.lintString != null) {
                oprot.writeFieldBegin(Complex.LINT_STRING_FIELD_DESC);
                oprot.writeListBegin(new TList((byte)12, struct.lintString.size()));
                for (final IntString _iter45 : struct.lintString) {
                    _iter45.write(oprot);
                }
                oprot.writeListEnd();
                oprot.writeFieldEnd();
            }
            if (struct.mStringString != null) {
                oprot.writeFieldBegin(Complex.M_STRING_STRING_FIELD_DESC);
                oprot.writeMapBegin(new TMap((byte)11, (byte)11, struct.mStringString.size()));
                for (final Map.Entry<String, String> _iter46 : struct.mStringString.entrySet()) {
                    oprot.writeString(_iter46.getKey());
                    oprot.writeString(_iter46.getValue());
                }
                oprot.writeMapEnd();
                oprot.writeFieldEnd();
            }
            if (struct.attributes != null) {
                oprot.writeFieldBegin(Complex.ATTRIBUTES_FIELD_DESC);
                oprot.writeMapBegin(new TMap((byte)11, (byte)13, struct.attributes.size()));
                for (final Map.Entry<String, Map<String, Map<String, PropValueUnion>>> _iter47 : struct.attributes.entrySet()) {
                    oprot.writeString(_iter47.getKey());
                    oprot.writeMapBegin(new TMap((byte)11, (byte)13, _iter47.getValue().size()));
                    for (final Map.Entry<String, Map<String, PropValueUnion>> _iter48 : _iter47.getValue().entrySet()) {
                        oprot.writeString(_iter48.getKey());
                        oprot.writeMapBegin(new TMap((byte)11, (byte)12, _iter48.getValue().size()));
                        for (final Map.Entry<String, PropValueUnion> _iter49 : _iter48.getValue().entrySet()) {
                            oprot.writeString(_iter49.getKey());
                            _iter49.getValue().write(oprot);
                        }
                        oprot.writeMapEnd();
                    }
                    oprot.writeMapEnd();
                }
                oprot.writeMapEnd();
                oprot.writeFieldEnd();
            }
            if (struct.unionField1 != null) {
                oprot.writeFieldBegin(Complex.UNION_FIELD1_FIELD_DESC);
                struct.unionField1.write(oprot);
                oprot.writeFieldEnd();
            }
            if (struct.unionField2 != null) {
                oprot.writeFieldBegin(Complex.UNION_FIELD2_FIELD_DESC);
                struct.unionField2.write(oprot);
                oprot.writeFieldEnd();
            }
            if (struct.unionField3 != null) {
                oprot.writeFieldBegin(Complex.UNION_FIELD3_FIELD_DESC);
                struct.unionField3.write(oprot);
                oprot.writeFieldEnd();
            }
            oprot.writeFieldStop();
            oprot.writeStructEnd();
        }
    }
    
    private static class ComplexTupleSchemeFactory implements SchemeFactory
    {
        @Override
        public ComplexTupleScheme getScheme() {
            return new ComplexTupleScheme();
        }
    }
    
    private static class ComplexTupleScheme extends TupleScheme<Complex>
    {
        @Override
        public void write(final TProtocol prot, final Complex struct) throws TException {
            final TTupleProtocol oprot = (TTupleProtocol)prot;
            final BitSet optionals = new BitSet();
            if (struct.isSetAint()) {
                optionals.set(0);
            }
            if (struct.isSetAString()) {
                optionals.set(1);
            }
            if (struct.isSetLint()) {
                optionals.set(2);
            }
            if (struct.isSetLString()) {
                optionals.set(3);
            }
            if (struct.isSetLintString()) {
                optionals.set(4);
            }
            if (struct.isSetMStringString()) {
                optionals.set(5);
            }
            if (struct.isSetAttributes()) {
                optionals.set(6);
            }
            if (struct.isSetUnionField1()) {
                optionals.set(7);
            }
            if (struct.isSetUnionField2()) {
                optionals.set(8);
            }
            if (struct.isSetUnionField3()) {
                optionals.set(9);
            }
            oprot.writeBitSet(optionals, 10);
            if (struct.isSetAint()) {
                oprot.writeI32(struct.aint);
            }
            if (struct.isSetAString()) {
                oprot.writeString(struct.aString);
            }
            if (struct.isSetLint()) {
                oprot.writeI32(struct.lint.size());
                for (final int _iter50 : struct.lint) {
                    oprot.writeI32(_iter50);
                }
            }
            if (struct.isSetLString()) {
                oprot.writeI32(struct.lString.size());
                for (final String _iter51 : struct.lString) {
                    oprot.writeString(_iter51);
                }
            }
            if (struct.isSetLintString()) {
                oprot.writeI32(struct.lintString.size());
                for (final IntString _iter52 : struct.lintString) {
                    _iter52.write(oprot);
                }
            }
            if (struct.isSetMStringString()) {
                oprot.writeI32(struct.mStringString.size());
                for (final Map.Entry<String, String> _iter53 : struct.mStringString.entrySet()) {
                    oprot.writeString(_iter53.getKey());
                    oprot.writeString(_iter53.getValue());
                }
            }
            if (struct.isSetAttributes()) {
                oprot.writeI32(struct.attributes.size());
                for (final Map.Entry<String, Map<String, Map<String, PropValueUnion>>> _iter54 : struct.attributes.entrySet()) {
                    oprot.writeString(_iter54.getKey());
                    oprot.writeI32(_iter54.getValue().size());
                    for (final Map.Entry<String, Map<String, PropValueUnion>> _iter55 : _iter54.getValue().entrySet()) {
                        oprot.writeString(_iter55.getKey());
                        oprot.writeI32(_iter55.getValue().size());
                        for (final Map.Entry<String, PropValueUnion> _iter56 : _iter55.getValue().entrySet()) {
                            oprot.writeString(_iter56.getKey());
                            _iter56.getValue().write(oprot);
                        }
                    }
                }
            }
            if (struct.isSetUnionField1()) {
                struct.unionField1.write(oprot);
            }
            if (struct.isSetUnionField2()) {
                struct.unionField2.write(oprot);
            }
            if (struct.isSetUnionField3()) {
                struct.unionField3.write(oprot);
            }
        }
        
        @Override
        public void read(final TProtocol prot, final Complex struct) throws TException {
            final TTupleProtocol iprot = (TTupleProtocol)prot;
            final BitSet incoming = iprot.readBitSet(10);
            if (incoming.get(0)) {
                struct.aint = iprot.readI32();
                struct.setAintIsSet(true);
            }
            if (incoming.get(1)) {
                struct.aString = iprot.readString();
                struct.setAStringIsSet(true);
            }
            if (incoming.get(2)) {
                final TList _list57 = new TList((byte)8, iprot.readI32());
                struct.lint = (List<Integer>)new ArrayList(_list57.size);
                for (int _i58 = 0; _i58 < _list57.size; ++_i58) {
                    final int _elem59 = iprot.readI32();
                    struct.lint.add(_elem59);
                }
                struct.setLintIsSet(true);
            }
            if (incoming.get(3)) {
                final TList _list58 = new TList((byte)11, iprot.readI32());
                struct.lString = (List<String>)new ArrayList(_list58.size);
                for (int _i59 = 0; _i59 < _list58.size; ++_i59) {
                    final String _elem60 = iprot.readString();
                    struct.lString.add(_elem60);
                }
                struct.setLStringIsSet(true);
            }
            if (incoming.get(4)) {
                final TList _list59 = new TList((byte)12, iprot.readI32());
                struct.lintString = (List<IntString>)new ArrayList(_list59.size);
                for (int _i60 = 0; _i60 < _list59.size; ++_i60) {
                    final IntString _elem61 = new IntString();
                    _elem61.read(iprot);
                    struct.lintString.add(_elem61);
                }
                struct.setLintStringIsSet(true);
            }
            if (incoming.get(5)) {
                final TMap _map66 = new TMap((byte)11, (byte)11, iprot.readI32());
                struct.mStringString = (Map<String, String>)new HashMap(2 * _map66.size);
                for (int _i61 = 0; _i61 < _map66.size; ++_i61) {
                    final String _key68 = iprot.readString();
                    final String _val69 = iprot.readString();
                    struct.mStringString.put(_key68, _val69);
                }
                struct.setMStringStringIsSet(true);
            }
            if (incoming.get(6)) {
                final TMap _map67 = new TMap((byte)11, (byte)13, iprot.readI32());
                struct.attributes = (Map<String, Map<String, Map<String, PropValueUnion>>>)new HashMap(2 * _map67.size);
                for (int _i62 = 0; _i62 < _map67.size; ++_i62) {
                    final String _key69 = iprot.readString();
                    final TMap _map68 = new TMap((byte)11, (byte)13, iprot.readI32());
                    final Map<String, Map<String, PropValueUnion>> _val70 = new HashMap<String, Map<String, PropValueUnion>>(2 * _map68.size);
                    for (int _i63 = 0; _i63 < _map68.size; ++_i63) {
                        final String _key70 = iprot.readString();
                        final TMap _map69 = new TMap((byte)11, (byte)12, iprot.readI32());
                        final Map<String, PropValueUnion> _val71 = new HashMap<String, PropValueUnion>(2 * _map69.size);
                        for (int _i64 = 0; _i64 < _map69.size; ++_i64) {
                            final String _key71 = iprot.readString();
                            final PropValueUnion _val72 = new PropValueUnion();
                            _val72.read(iprot);
                            _val71.put(_key71, _val72);
                        }
                        _val70.put(_key70, _val71);
                    }
                    struct.attributes.put(_key69, _val70);
                }
                struct.setAttributesIsSet(true);
            }
            if (incoming.get(7)) {
                struct.unionField1 = new PropValueUnion();
                struct.unionField1.read(iprot);
                struct.setUnionField1IsSet(true);
            }
            if (incoming.get(8)) {
                struct.unionField2 = new PropValueUnion();
                struct.unionField2.read(iprot);
                struct.setUnionField2IsSet(true);
            }
            if (incoming.get(9)) {
                struct.unionField3 = new PropValueUnion();
                struct.unionField3.read(iprot);
                struct.setUnionField3IsSet(true);
            }
        }
    }
}
