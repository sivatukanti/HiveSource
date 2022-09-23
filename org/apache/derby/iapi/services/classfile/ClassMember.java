// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.iapi.services.classfile;

import java.io.IOException;

public class ClassMember
{
    protected ClassHolder cpt;
    protected int access_flags;
    protected int name_index;
    protected int descriptor_index;
    protected Attributes attribute_info;
    
    ClassMember(final ClassHolder cpt, final int access_flags, final int name_index, final int descriptor_index) {
        this.cpt = cpt;
        this.name_index = name_index;
        this.descriptor_index = descriptor_index;
        this.access_flags = access_flags;
    }
    
    public int getModifier() {
        return this.access_flags;
    }
    
    public String getDescriptor() {
        return this.cpt.nameIndexToString(this.descriptor_index);
    }
    
    public String getName() {
        return this.cpt.nameIndexToString(this.name_index);
    }
    
    public void addAttribute(final String s, final ClassFormatOutput classFormatOutput) {
        if (this.attribute_info == null) {
            this.attribute_info = new Attributes(1);
        }
        this.attribute_info.addEntry(new AttributeEntry(this.cpt.addUtf8(s), classFormatOutput));
    }
    
    void put(final ClassFormatOutput classFormatOutput) throws IOException {
        classFormatOutput.putU2(this.access_flags);
        classFormatOutput.putU2(this.name_index);
        classFormatOutput.putU2(this.descriptor_index);
        if (this.attribute_info != null) {
            classFormatOutput.putU2(this.attribute_info.size());
            this.attribute_info.put(classFormatOutput);
        }
        else {
            classFormatOutput.putU2(0);
        }
    }
    
    int classFileSize() {
        int n = 8;
        if (this.attribute_info != null) {
            n += this.attribute_info.classFileSize();
        }
        return n;
    }
}
