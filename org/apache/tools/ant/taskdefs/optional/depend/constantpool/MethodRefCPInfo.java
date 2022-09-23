// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.tools.ant.taskdefs.optional.depend.constantpool;

import java.io.IOException;
import java.io.DataInputStream;

public class MethodRefCPInfo extends ConstantPoolEntry
{
    private String methodClassName;
    private String methodName;
    private String methodType;
    private int classIndex;
    private int nameAndTypeIndex;
    
    public MethodRefCPInfo() {
        super(10, 1);
    }
    
    @Override
    public void read(final DataInputStream cpStream) throws IOException {
        this.classIndex = cpStream.readUnsignedShort();
        this.nameAndTypeIndex = cpStream.readUnsignedShort();
    }
    
    @Override
    public String toString() {
        String value;
        if (this.isResolved()) {
            value = "Method : Class = " + this.methodClassName + ", name = " + this.methodName + ", type = " + this.methodType;
        }
        else {
            value = "Method : Class index = " + this.classIndex + ", name and type index = " + this.nameAndTypeIndex;
        }
        return value;
    }
    
    @Override
    public void resolve(final ConstantPool constantPool) {
        final ClassCPInfo methodClass = (ClassCPInfo)constantPool.getEntry(this.classIndex);
        methodClass.resolve(constantPool);
        this.methodClassName = methodClass.getClassName();
        final NameAndTypeCPInfo nt = (NameAndTypeCPInfo)constantPool.getEntry(this.nameAndTypeIndex);
        nt.resolve(constantPool);
        this.methodName = nt.getName();
        this.methodType = nt.getType();
        super.resolve(constantPool);
    }
    
    public String getMethodClassName() {
        return this.methodClassName;
    }
    
    public String getMethodName() {
        return this.methodName;
    }
    
    public String getMethodType() {
        return this.methodType;
    }
}
