// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.iapi.services.classfile;

import java.util.Enumeration;
import org.apache.derby.iapi.util.ByteArray;
import java.io.IOException;
import java.util.Vector;
import java.util.Hashtable;

public class ClassHolder
{
    protected int minor_version;
    protected int major_version;
    protected int access_flags;
    protected int this_class;
    protected int super_class;
    protected int[] interfaces;
    protected MemberTable field_info;
    protected MemberTable method_info;
    protected Attributes attribute_info;
    protected Hashtable cptHashTable;
    protected Vector cptEntries;
    private int cptEstimatedSize;
    private final CONSTANT_Index_info searchIndex;
    
    protected ClassHolder(final int n) {
        this.minor_version = 3;
        this.major_version = 45;
        this.searchIndex = new CONSTANT_Index_info(0, 0, 0);
        this.cptEntries = new Vector(n);
        this.cptHashTable = new Hashtable(n, 0.75f);
        this.cptEntries.setSize(1);
    }
    
    public ClassHolder(final String s, final String s2, final int n) {
        this(100);
        this.access_flags = (n | 0x20);
        this.this_class = this.addClassReference(s);
        this.super_class = this.addClassReference(s2);
        this.method_info = new MemberTable(0);
    }
    
    private void put(final ClassFormatOutput classFormatOutput) throws IOException {
        classFormatOutput.putU4(-889275714);
        classFormatOutput.putU2(this.minor_version);
        classFormatOutput.putU2(this.major_version);
        classFormatOutput.putU2("constant_pool", this.cptEntries.size());
        this.cptPut(classFormatOutput);
        classFormatOutput.putU2(this.access_flags);
        classFormatOutput.putU2(this.this_class);
        classFormatOutput.putU2(this.super_class);
        if (this.interfaces != null) {
            final int length = this.interfaces.length;
            classFormatOutput.putU2(length);
            for (int i = 0; i < length; ++i) {
                classFormatOutput.putU2(this.interfaces[i]);
            }
        }
        else {
            classFormatOutput.putU2(0);
        }
        if (this.field_info != null) {
            classFormatOutput.putU2(this.field_info.size());
            this.field_info.put(classFormatOutput);
        }
        else {
            classFormatOutput.putU2(0);
        }
        if (this.method_info != null) {
            classFormatOutput.putU2(this.method_info.size());
            this.method_info.put(classFormatOutput);
        }
        else {
            classFormatOutput.putU2(0);
        }
        if (this.attribute_info != null) {
            classFormatOutput.putU2(this.attribute_info.size());
            this.attribute_info.put(classFormatOutput);
        }
        else {
            classFormatOutput.putU2(0);
        }
    }
    
    public ByteArray getFileFormat() throws IOException {
        int n = 24 + this.cptEstimatedSize;
        if (this.interfaces != null) {
            n += this.interfaces.length * 2;
        }
        if (this.field_info != null) {
            n += this.field_info.classFileSize();
        }
        if (this.method_info != null) {
            n += this.method_info.classFileSize();
        }
        if (this.attribute_info != null) {
            n += this.attribute_info.classFileSize();
        }
        final ClassFormatOutput classFormatOutput = new ClassFormatOutput(n + 200);
        this.put(classFormatOutput);
        return new ByteArray(classFormatOutput.getData(), 0, classFormatOutput.size());
    }
    
    public int getModifier() {
        return this.access_flags;
    }
    
    public String getName() {
        return this.className(this.this_class).replace('/', '.');
    }
    
    public ClassMember addMember(final String s, final String s2, final int n) {
        final ClassMember classMember = new ClassMember(this, n, this.addUtf8Entry(s).getIndex(), this.addUtf8Entry(s2).getIndex());
        MemberTable memberTable;
        if (s2.startsWith("(")) {
            memberTable = this.method_info;
            if (memberTable == null) {
                final MemberTable method_info = new MemberTable(0);
                this.method_info = method_info;
                memberTable = method_info;
            }
        }
        else {
            memberTable = this.field_info;
            if (memberTable == null) {
                final MemberTable field_info = new MemberTable(0);
                this.field_info = field_info;
                memberTable = field_info;
            }
        }
        memberTable.addEntry(classMember);
        return classMember;
    }
    
    public int addFieldReference(final String s, final String s2, final String s3) {
        return this.addReference(9, s, s2, s3);
    }
    
    public int addFieldReference(final ClassMember classMember) {
        return this.addReference(9, classMember);
    }
    
    public int addMethodReference(final String s, final String s2, final String s3, final boolean b) {
        return this.addReference(b ? 11 : 10, s, s2, s3);
    }
    
    private int addReference(final int n, final String s, final String s2, final String s3) {
        return this.addIndexReference(n, this.addClassReference(s), this.addNameAndType(s2, s3));
    }
    
    private int addReference(final int n, final ClassMember classMember) {
        return this.addIndexReference(n, this.this_class, this.addIndexReference(12, classMember.name_index, classMember.descriptor_index));
    }
    
    public int addConstant(final String s) {
        return this.addString(s);
    }
    
    public int addUtf8(final String s) {
        return this.addUtf8Entry(s).getIndex();
    }
    
    public int addConstant(final int n) {
        return this.addDirectEntry(new CONSTANT_Integer_info(n));
    }
    
    public int addConstant(final float n) {
        return this.addDirectEntry(new CONSTANT_Float_info(n));
    }
    
    public int addConstant(final long n) {
        return this.addDirectEntry(new CONSTANT_Long_info(n));
    }
    
    public int addConstant(final double n) {
        return this.addDirectEntry(new CONSTANT_Double_info(n));
    }
    
    public int getConstantPoolIndex() {
        return this.this_class;
    }
    
    public void addAttribute(final String s, final ClassFormatOutput classFormatOutput) {
        if (this.attribute_info == null) {
            this.attribute_info = new Attributes(1);
        }
        this.attribute_info.addEntry(new AttributeEntry(this.addUtf8Entry(s).getIndex(), classFormatOutput));
    }
    
    public String getSuperClassName() {
        if (this.super_class == 0) {
            return null;
        }
        return this.className(this.super_class).replace('/', '.');
    }
    
    protected int addEntry(final Object key, final ConstantPoolEntry constantPoolEntry) {
        constantPoolEntry.setIndex(this.cptEntries.size());
        if (key != null) {
            this.cptHashTable.put(key, constantPoolEntry);
        }
        this.cptEntries.add(constantPoolEntry);
        this.cptEstimatedSize += constantPoolEntry.classFileSize();
        if (constantPoolEntry.doubleSlot()) {
            this.cptEntries.add(null);
            return 2;
        }
        return 1;
    }
    
    private int addDirectEntry(ConstantPoolEntry constantPoolEntry) {
        final ConstantPoolEntry matchingEntry = this.findMatchingEntry(constantPoolEntry);
        if (matchingEntry != null) {
            constantPoolEntry = matchingEntry;
        }
        else {
            this.addEntry(constantPoolEntry.getKey(), constantPoolEntry);
        }
        return constantPoolEntry.getIndex();
    }
    
    private int addIndexReference(final int n, final int n2, final int n3) {
        this.searchIndex.set(n, n2, n3);
        ConstantPoolEntry matchingEntry = this.findMatchingEntry(this.searchIndex);
        if (matchingEntry == null) {
            matchingEntry = new CONSTANT_Index_info(n, n2, n3);
            this.addEntry(matchingEntry.getKey(), matchingEntry);
        }
        return matchingEntry.getIndex();
    }
    
    public int addClassReference(String convertToInternalClassName) {
        if (isExternalClassName(convertToInternalClassName)) {
            convertToInternalClassName = convertToInternalClassName(convertToInternalClassName);
        }
        return this.addIndexReference(7, this.addUtf8Entry(convertToInternalClassName).getIndex(), 0);
    }
    
    private int addNameAndType(final String s, final String s2) {
        return this.addIndexReference(12, this.addUtf8Entry(s).getIndex(), this.addUtf8Entry(s2).getIndex());
    }
    
    private CONSTANT_Utf8_info addUtf8Entry(final String s) {
        CONSTANT_Utf8_info constant_Utf8_info = (CONSTANT_Utf8_info)this.findMatchingEntry(s);
        if (constant_Utf8_info == null) {
            constant_Utf8_info = new CONSTANT_Utf8_info(s);
            this.addEntry(s, constant_Utf8_info);
        }
        return constant_Utf8_info;
    }
    
    private CONSTANT_Utf8_info addExtraUtf8(final String s) {
        final CONSTANT_Utf8_info constant_Utf8_info = new CONSTANT_Utf8_info(s);
        this.addEntry(null, constant_Utf8_info);
        return constant_Utf8_info;
    }
    
    private int addString(final String s) {
        final CONSTANT_Utf8_info addUtf8Entry = this.addUtf8Entry(s);
        int alternative = addUtf8Entry.setAsString();
        if (alternative == 0) {
            alternative = this.addExtraUtf8(s).getIndex();
            addUtf8Entry.setAlternative(alternative);
        }
        return this.addIndexReference(8, alternative, 0);
    }
    
    protected void cptPut(final ClassFormatOutput classFormatOutput) throws IOException {
        final Enumeration<ConstantPoolEntry> elements = this.cptEntries.elements();
        while (elements.hasMoreElements()) {
            final ConstantPoolEntry constantPoolEntry = elements.nextElement();
            if (constantPoolEntry == null) {
                continue;
            }
            constantPoolEntry.put(classFormatOutput);
        }
    }
    
    public ConstantPoolEntry getEntry(final int index) {
        return this.cptEntries.get(index);
    }
    
    protected String className(final int n) {
        return this.nameIndexToString(((CONSTANT_Index_info)this.getEntry(n)).getI1()).replace('/', '.');
    }
    
    int findUtf8(final String s) {
        final ConstantPoolEntry matchingEntry = this.findMatchingEntry(s);
        if (matchingEntry == null) {
            return -1;
        }
        return matchingEntry.getIndex();
    }
    
    public int findClass(final String s) {
        final int utf8 = this.findUtf8(convertToInternalClassName(s));
        if (utf8 < 0) {
            return -1;
        }
        return this.findIndexIndex(7, utf8, 0);
    }
    
    public int findNameAndType(final String s, final String s2) {
        final int utf8 = this.findUtf8(s);
        if (utf8 < 0) {
            return -1;
        }
        final int utf9 = this.findUtf8(s2);
        if (utf9 < 0) {
            return -1;
        }
        return this.findIndexIndex(12, utf8, utf9);
    }
    
    protected CONSTANT_Index_info findIndexEntry(final int n, final int n2, final int n3) {
        this.searchIndex.set(n, n2, n3);
        return (CONSTANT_Index_info)this.findMatchingEntry(this.searchIndex);
    }
    
    protected int findIndexIndex(final int n, final int n2, final int n3) {
        final CONSTANT_Index_info indexEntry = this.findIndexEntry(n, n2, n3);
        if (indexEntry == null) {
            return -1;
        }
        return indexEntry.getIndex();
    }
    
    protected ConstantPoolEntry findMatchingEntry(final Object key) {
        return this.cptHashTable.get(key);
    }
    
    String nameIndexToString(final int n) {
        return this.getEntry(n).toString();
    }
    
    protected String getClassName(final int n) {
        if (n == 0) {
            return "";
        }
        return this.nameIndexToString(this.getEntry(n).getI1());
    }
    
    public static boolean isExternalClassName(final String s) {
        final int length;
        return s.indexOf(46) != -1 || ((length = s.length()) != 0 && s.charAt(length - 1) == ']');
    }
    
    public static String convertToInternalClassName(final String s) {
        return convertToInternal(s, false);
    }
    
    public static String convertToInternalDescriptor(final String s) {
        return convertToInternal(s, true);
    }
    
    private static String convertToInternal(final String s, final boolean b) {
        int length = s.length();
        String s2 = null;
        final int n = length;
        int n2 = 0;
        if (s.charAt(length - 1) == ']') {
            while (length > 0 && s.charAt(length - 1) == ']' && s.charAt(length - 2) == '[') {
                length -= 2;
                ++n2;
            }
        }
        final String anObject = (n == length) ? s : s.substring(0, length);
        switch (length) {
            case 7: {
                if ("boolean".equals(anObject)) {
                    s2 = makeDesc('Z', n2);
                    break;
                }
                break;
            }
            case 4: {
                if ("void".equals(anObject)) {
                    s2 = makeDesc('V', n2);
                    break;
                }
                if ("long".equals(anObject)) {
                    s2 = makeDesc('J', n2);
                    break;
                }
                if ("byte".equals(anObject)) {
                    s2 = makeDesc('B', n2);
                    break;
                }
                if ("char".equals(anObject)) {
                    s2 = makeDesc('C', n2);
                    break;
                }
                break;
            }
            case 3: {
                if ("int".equals(anObject)) {
                    s2 = makeDesc('I', n2);
                    break;
                }
                break;
            }
            case 6: {
                if ("double".equals(anObject)) {
                    s2 = makeDesc('D', n2);
                    break;
                }
                break;
            }
            case 5: {
                if ("short".equals(anObject)) {
                    s2 = makeDesc('S', n2);
                    break;
                }
                if ("float".equals(anObject)) {
                    s2 = makeDesc('F', n2);
                    break;
                }
                break;
            }
        }
        if (s2 == null) {
            s2 = makeDesc(anObject, n2, b);
        }
        return s2;
    }
    
    private static String makeDesc(final char c, final int n) {
        if (n != 0) {
            final StringBuffer sb = new StringBuffer(n + 3);
            for (int i = 0; i < n; ++i) {
                sb.append('[');
            }
            sb.append(makeDesc(c, 0));
            return sb.toString();
        }
        switch (c) {
            case 'B': {
                return "B";
            }
            case 'C': {
                return "C";
            }
            case 'D': {
                return "D";
            }
            case 'F': {
                return "F";
            }
            case 'I': {
                return "I";
            }
            case 'J': {
                return "J";
            }
            case 'S': {
                return "S";
            }
            case 'Z': {
                return "Z";
            }
            case 'V': {
                return "V";
            }
            default: {
                return null;
            }
        }
    }
    
    private static String makeDesc(final String s, final int n, final boolean b) {
        if (!b && n == 0) {
            return s.replace('.', '/');
        }
        final StringBuffer sb = new StringBuffer(n + 2 + s.length());
        for (int i = 0; i < n; ++i) {
            sb.append('[');
        }
        sb.append('L');
        sb.append(s.replace('.', '/'));
        sb.append(';');
        return sb.toString();
    }
}
