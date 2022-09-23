// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.tools.ant.taskdefs.optional.depend.constantpool;

import java.util.Iterator;
import java.io.IOException;
import java.io.DataInputStream;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.Map;
import java.util.List;

public class ConstantPool
{
    private final List<ConstantPoolEntry> entries;
    private final Map<String, Integer> utf8Indexes;
    
    public ConstantPool() {
        this.entries = new ArrayList<ConstantPoolEntry>();
        this.utf8Indexes = new HashMap<String, Integer>();
        this.entries.add(null);
    }
    
    public void read(final DataInputStream classStream) throws IOException {
        final int numEntries = classStream.readUnsignedShort();
        int i = 1;
        while (i < numEntries) {
            final ConstantPoolEntry nextEntry = ConstantPoolEntry.readEntry(classStream);
            i += nextEntry.getNumEntries();
            this.addEntry(nextEntry);
        }
    }
    
    public int size() {
        return this.entries.size();
    }
    
    public int addEntry(final ConstantPoolEntry entry) {
        final int index = this.entries.size();
        this.entries.add(entry);
        for (int numSlots = entry.getNumEntries(), j = 0; j < numSlots - 1; ++j) {
            this.entries.add(null);
        }
        if (entry instanceof Utf8CPInfo) {
            final Utf8CPInfo utf8Info = (Utf8CPInfo)entry;
            this.utf8Indexes.put(utf8Info.getValue(), new Integer(index));
        }
        return index;
    }
    
    public void resolve() {
        for (final ConstantPoolEntry poolInfo : this.entries) {
            if (poolInfo != null && !poolInfo.isResolved()) {
                poolInfo.resolve(this);
            }
        }
    }
    
    public ConstantPoolEntry getEntry(final int index) {
        return this.entries.get(index);
    }
    
    public int getUTF8Entry(final String value) {
        int index = -1;
        final Integer indexInteger = this.utf8Indexes.get(value);
        if (indexInteger != null) {
            index = indexInteger;
        }
        return index;
    }
    
    public int getClassEntry(final String className) {
        int index = -1;
        for (int size = this.entries.size(), i = 0; i < size && index == -1; ++i) {
            final Object element = this.entries.get(i);
            if (element instanceof ClassCPInfo) {
                final ClassCPInfo classinfo = (ClassCPInfo)element;
                if (classinfo.getClassName().equals(className)) {
                    index = i;
                }
            }
        }
        return index;
    }
    
    public int getConstantEntry(final Object constantValue) {
        int index = -1;
        for (int size = this.entries.size(), i = 0; i < size && index == -1; ++i) {
            final Object element = this.entries.get(i);
            if (element instanceof ConstantCPInfo) {
                final ConstantCPInfo constantEntry = (ConstantCPInfo)element;
                if (constantEntry.getValue().equals(constantValue)) {
                    index = i;
                }
            }
        }
        return index;
    }
    
    public int getMethodRefEntry(final String methodClassName, final String methodName, final String methodType) {
        int index = -1;
        for (int size = this.entries.size(), i = 0; i < size && index == -1; ++i) {
            final Object element = this.entries.get(i);
            if (element instanceof MethodRefCPInfo) {
                final MethodRefCPInfo methodRefEntry = (MethodRefCPInfo)element;
                if (methodRefEntry.getMethodClassName().equals(methodClassName) && methodRefEntry.getMethodName().equals(methodName) && methodRefEntry.getMethodType().equals(methodType)) {
                    index = i;
                }
            }
        }
        return index;
    }
    
    public int getInterfaceMethodRefEntry(final String interfaceMethodClassName, final String interfaceMethodName, final String interfaceMethodType) {
        int index = -1;
        for (int size = this.entries.size(), i = 0; i < size && index == -1; ++i) {
            final Object element = this.entries.get(i);
            if (element instanceof InterfaceMethodRefCPInfo) {
                final InterfaceMethodRefCPInfo interfaceMethodRefEntry = (InterfaceMethodRefCPInfo)element;
                if (interfaceMethodRefEntry.getInterfaceMethodClassName().equals(interfaceMethodClassName) && interfaceMethodRefEntry.getInterfaceMethodName().equals(interfaceMethodName) && interfaceMethodRefEntry.getInterfaceMethodType().equals(interfaceMethodType)) {
                    index = i;
                }
            }
        }
        return index;
    }
    
    public int getFieldRefEntry(final String fieldClassName, final String fieldName, final String fieldType) {
        int index = -1;
        for (int size = this.entries.size(), i = 0; i < size && index == -1; ++i) {
            final Object element = this.entries.get(i);
            if (element instanceof FieldRefCPInfo) {
                final FieldRefCPInfo fieldRefEntry = (FieldRefCPInfo)element;
                if (fieldRefEntry.getFieldClassName().equals(fieldClassName) && fieldRefEntry.getFieldName().equals(fieldName) && fieldRefEntry.getFieldType().equals(fieldType)) {
                    index = i;
                }
            }
        }
        return index;
    }
    
    public int getNameAndTypeEntry(final String name, final String type) {
        int index = -1;
        for (int size = this.entries.size(), i = 0; i < size && index == -1; ++i) {
            final Object element = this.entries.get(i);
            if (element instanceof NameAndTypeCPInfo) {
                final NameAndTypeCPInfo nameAndTypeEntry = (NameAndTypeCPInfo)element;
                if (nameAndTypeEntry.getName().equals(name) && nameAndTypeEntry.getType().equals(type)) {
                    index = i;
                }
            }
        }
        return index;
    }
    
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("\n");
        for (int size = this.entries.size(), i = 0; i < size; ++i) {
            sb.append("[" + i + "] = " + this.getEntry(i) + "\n");
        }
        return sb.toString();
    }
}
