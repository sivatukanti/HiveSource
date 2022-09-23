// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.tools.ant.taskdefs.optional.depend.constantpool;

import java.io.IOException;
import java.io.DataInputStream;

public class MethodTypeCPInfo extends ConstantCPInfo
{
    private int methodDescriptorIndex;
    private String methodDescriptor;
    
    public MethodTypeCPInfo() {
        super(16, 1);
    }
    
    @Override
    public void read(final DataInputStream cpStream) throws IOException {
        this.methodDescriptorIndex = cpStream.readUnsignedShort();
    }
    
    @Override
    public void resolve(final ConstantPool constantPool) {
        final Utf8CPInfo methodClass = (Utf8CPInfo)constantPool.getEntry(this.methodDescriptorIndex);
        methodClass.resolve(constantPool);
        this.methodDescriptor = methodClass.getValue();
        super.resolve(constantPool);
    }
    
    @Override
    public String toString() {
        if (!this.isResolved()) {
            return "MethodDescriptorIndex: " + this.methodDescriptorIndex;
        }
        return "MethodDescriptor: " + this.methodDescriptor;
    }
}
