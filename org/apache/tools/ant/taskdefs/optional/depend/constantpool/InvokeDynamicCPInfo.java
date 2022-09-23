// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.tools.ant.taskdefs.optional.depend.constantpool;

import java.io.IOException;
import java.io.DataInputStream;

public class InvokeDynamicCPInfo extends ConstantCPInfo
{
    private int bootstrapMethodAttrIndex;
    private int nameAndTypeIndex;
    private NameAndTypeCPInfo nameAndTypeCPInfo;
    
    public InvokeDynamicCPInfo() {
        super(18, 1);
    }
    
    @Override
    public void read(final DataInputStream cpStream) throws IOException {
        this.bootstrapMethodAttrIndex = cpStream.readUnsignedShort();
        this.nameAndTypeIndex = cpStream.readUnsignedShort();
    }
    
    @Override
    public String toString() {
        String value;
        if (this.isResolved()) {
            value = "Name = " + this.nameAndTypeCPInfo.getName() + ", type = " + this.nameAndTypeCPInfo.getType();
        }
        else {
            value = "BootstrapMethodAttrIndex inx = " + this.bootstrapMethodAttrIndex + "NameAndType index = " + this.nameAndTypeIndex;
        }
        return value;
    }
    
    @Override
    public void resolve(final ConstantPool constantPool) {
        (this.nameAndTypeCPInfo = (NameAndTypeCPInfo)constantPool.getEntry(this.nameAndTypeIndex)).resolve(constantPool);
        super.resolve(constantPool);
    }
}
