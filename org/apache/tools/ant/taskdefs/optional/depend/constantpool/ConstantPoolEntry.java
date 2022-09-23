// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.tools.ant.taskdefs.optional.depend.constantpool;

import java.io.IOException;
import java.io.DataInputStream;

public abstract class ConstantPoolEntry
{
    public static final int CONSTANT_UTF8 = 1;
    public static final int CONSTANT_INTEGER = 3;
    public static final int CONSTANT_FLOAT = 4;
    public static final int CONSTANT_LONG = 5;
    public static final int CONSTANT_DOUBLE = 6;
    public static final int CONSTANT_CLASS = 7;
    public static final int CONSTANT_STRING = 8;
    public static final int CONSTANT_FIELDREF = 9;
    public static final int CONSTANT_METHODREF = 10;
    public static final int CONSTANT_INTERFACEMETHODREF = 11;
    public static final int CONSTANT_NAMEANDTYPE = 12;
    public static final int CONSTANT_METHODHANDLE = 15;
    public static final int CONSTANT_METHODTYPE = 16;
    public static final int CONSTANT_INVOKEDYNAMIC = 18;
    private int tag;
    private int numEntries;
    private boolean resolved;
    
    public ConstantPoolEntry(final int tagValue, final int entries) {
        this.tag = tagValue;
        this.numEntries = entries;
        this.resolved = false;
    }
    
    public static ConstantPoolEntry readEntry(final DataInputStream cpStream) throws IOException {
        ConstantPoolEntry cpInfo = null;
        final int cpTag = cpStream.readUnsignedByte();
        switch (cpTag) {
            case 1: {
                cpInfo = new Utf8CPInfo();
                break;
            }
            case 3: {
                cpInfo = new IntegerCPInfo();
                break;
            }
            case 4: {
                cpInfo = new FloatCPInfo();
                break;
            }
            case 5: {
                cpInfo = new LongCPInfo();
                break;
            }
            case 6: {
                cpInfo = new DoubleCPInfo();
                break;
            }
            case 7: {
                cpInfo = new ClassCPInfo();
                break;
            }
            case 8: {
                cpInfo = new StringCPInfo();
                break;
            }
            case 9: {
                cpInfo = new FieldRefCPInfo();
                break;
            }
            case 10: {
                cpInfo = new MethodRefCPInfo();
                break;
            }
            case 11: {
                cpInfo = new InterfaceMethodRefCPInfo();
                break;
            }
            case 12: {
                cpInfo = new NameAndTypeCPInfo();
                break;
            }
            case 15: {
                cpInfo = new MethodHandleCPInfo();
                break;
            }
            case 16: {
                cpInfo = new MethodTypeCPInfo();
                break;
            }
            case 18: {
                cpInfo = new InvokeDynamicCPInfo();
                break;
            }
            default: {
                throw new ClassFormatError("Invalid Constant Pool entry Type " + cpTag);
            }
        }
        cpInfo.read(cpStream);
        return cpInfo;
    }
    
    public boolean isResolved() {
        return this.resolved;
    }
    
    public void resolve(final ConstantPool constantPool) {
        this.resolved = true;
    }
    
    public abstract void read(final DataInputStream p0) throws IOException;
    
    public int getTag() {
        return this.tag;
    }
    
    public final int getNumEntries() {
        return this.numEntries;
    }
}
