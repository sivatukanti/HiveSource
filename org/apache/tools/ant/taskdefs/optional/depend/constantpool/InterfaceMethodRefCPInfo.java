// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.tools.ant.taskdefs.optional.depend.constantpool;

import java.io.IOException;
import java.io.DataInputStream;

public class InterfaceMethodRefCPInfo extends ConstantPoolEntry
{
    private String interfaceMethodClassName;
    private String interfaceMethodName;
    private String interfaceMethodType;
    private int classIndex;
    private int nameAndTypeIndex;
    
    public InterfaceMethodRefCPInfo() {
        super(11, 1);
    }
    
    @Override
    public void read(final DataInputStream cpStream) throws IOException {
        this.classIndex = cpStream.readUnsignedShort();
        this.nameAndTypeIndex = cpStream.readUnsignedShort();
    }
    
    @Override
    public void resolve(final ConstantPool constantPool) {
        final ClassCPInfo interfaceMethodClass = (ClassCPInfo)constantPool.getEntry(this.classIndex);
        interfaceMethodClass.resolve(constantPool);
        this.interfaceMethodClassName = interfaceMethodClass.getClassName();
        final NameAndTypeCPInfo nt = (NameAndTypeCPInfo)constantPool.getEntry(this.nameAndTypeIndex);
        nt.resolve(constantPool);
        this.interfaceMethodName = nt.getName();
        this.interfaceMethodType = nt.getType();
        super.resolve(constantPool);
    }
    
    @Override
    public String toString() {
        String value;
        if (this.isResolved()) {
            value = "InterfaceMethod : Class = " + this.interfaceMethodClassName + ", name = " + this.interfaceMethodName + ", type = " + this.interfaceMethodType;
        }
        else {
            value = "InterfaceMethod : Class index = " + this.classIndex + ", name and type index = " + this.nameAndTypeIndex;
        }
        return value;
    }
    
    public String getInterfaceMethodClassName() {
        return this.interfaceMethodClassName;
    }
    
    public String getInterfaceMethodName() {
        return this.interfaceMethodName;
    }
    
    public String getInterfaceMethodType() {
        return this.interfaceMethodType;
    }
}
