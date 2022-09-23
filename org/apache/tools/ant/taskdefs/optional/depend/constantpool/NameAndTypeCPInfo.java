// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.tools.ant.taskdefs.optional.depend.constantpool;

import java.io.IOException;
import java.io.DataInputStream;

public class NameAndTypeCPInfo extends ConstantPoolEntry
{
    private String name;
    private String type;
    private int nameIndex;
    private int descriptorIndex;
    
    public NameAndTypeCPInfo() {
        super(12, 1);
    }
    
    @Override
    public void read(final DataInputStream cpStream) throws IOException {
        this.nameIndex = cpStream.readUnsignedShort();
        this.descriptorIndex = cpStream.readUnsignedShort();
    }
    
    @Override
    public String toString() {
        String value;
        if (this.isResolved()) {
            value = "Name = " + this.name + ", type = " + this.type;
        }
        else {
            value = "Name index = " + this.nameIndex + ", descriptor index = " + this.descriptorIndex;
        }
        return value;
    }
    
    @Override
    public void resolve(final ConstantPool constantPool) {
        this.name = ((Utf8CPInfo)constantPool.getEntry(this.nameIndex)).getValue();
        this.type = ((Utf8CPInfo)constantPool.getEntry(this.descriptorIndex)).getValue();
        super.resolve(constantPool);
    }
    
    public String getName() {
        return this.name;
    }
    
    public String getType() {
        return this.type;
    }
}
