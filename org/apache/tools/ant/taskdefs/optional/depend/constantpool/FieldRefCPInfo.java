// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.tools.ant.taskdefs.optional.depend.constantpool;

import java.io.IOException;
import java.io.DataInputStream;

public class FieldRefCPInfo extends ConstantPoolEntry
{
    private String fieldClassName;
    private String fieldName;
    private String fieldType;
    private int classIndex;
    private int nameAndTypeIndex;
    
    public FieldRefCPInfo() {
        super(9, 1);
    }
    
    @Override
    public void read(final DataInputStream cpStream) throws IOException {
        this.classIndex = cpStream.readUnsignedShort();
        this.nameAndTypeIndex = cpStream.readUnsignedShort();
    }
    
    @Override
    public void resolve(final ConstantPool constantPool) {
        final ClassCPInfo fieldClass = (ClassCPInfo)constantPool.getEntry(this.classIndex);
        fieldClass.resolve(constantPool);
        this.fieldClassName = fieldClass.getClassName();
        final NameAndTypeCPInfo nt = (NameAndTypeCPInfo)constantPool.getEntry(this.nameAndTypeIndex);
        nt.resolve(constantPool);
        this.fieldName = nt.getName();
        this.fieldType = nt.getType();
        super.resolve(constantPool);
    }
    
    @Override
    public String toString() {
        String value;
        if (this.isResolved()) {
            value = "Field : Class = " + this.fieldClassName + ", name = " + this.fieldName + ", type = " + this.fieldType;
        }
        else {
            value = "Field : Class index = " + this.classIndex + ", name and type index = " + this.nameAndTypeIndex;
        }
        return value;
    }
    
    public String getFieldClassName() {
        return this.fieldClassName;
    }
    
    public String getFieldName() {
        return this.fieldName;
    }
    
    public String getFieldType() {
        return this.fieldType;
    }
}
