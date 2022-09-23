// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.tools.ant.taskdefs.optional.depend.constantpool;

import java.io.IOException;
import java.io.DataInputStream;

public class ClassCPInfo extends ConstantPoolEntry
{
    private String className;
    private int index;
    
    public ClassCPInfo() {
        super(7, 1);
    }
    
    @Override
    public void read(final DataInputStream cpStream) throws IOException {
        this.index = cpStream.readUnsignedShort();
        this.className = "unresolved";
    }
    
    @Override
    public String toString() {
        return "Class Constant Pool Entry for " + this.className + "[" + this.index + "]";
    }
    
    @Override
    public void resolve(final ConstantPool constantPool) {
        this.className = ((Utf8CPInfo)constantPool.getEntry(this.index)).getValue();
        super.resolve(constantPool);
    }
    
    public String getClassName() {
        return this.className;
    }
}
