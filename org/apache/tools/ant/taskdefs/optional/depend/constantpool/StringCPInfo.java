// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.tools.ant.taskdefs.optional.depend.constantpool;

import java.io.IOException;
import java.io.DataInputStream;

public class StringCPInfo extends ConstantCPInfo
{
    private int index;
    
    public StringCPInfo() {
        super(8, 1);
    }
    
    @Override
    public void read(final DataInputStream cpStream) throws IOException {
        this.index = cpStream.readUnsignedShort();
        this.setValue("unresolved");
    }
    
    @Override
    public String toString() {
        return "String Constant Pool Entry for " + this.getValue() + "[" + this.index + "]";
    }
    
    @Override
    public void resolve(final ConstantPool constantPool) {
        this.setValue(((Utf8CPInfo)constantPool.getEntry(this.index)).getValue());
        super.resolve(constantPool);
    }
}
