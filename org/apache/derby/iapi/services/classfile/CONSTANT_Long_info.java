// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.iapi.services.classfile;

import java.io.IOException;

final class CONSTANT_Long_info extends ConstantPoolEntry
{
    private final long value;
    
    CONSTANT_Long_info(final long value) {
        super(5);
        this.doubleSlot = true;
        this.value = value;
    }
    
    public int hashCode() {
        return (int)this.value;
    }
    
    public boolean equals(final Object o) {
        return o instanceof CONSTANT_Long_info && this.value == ((CONSTANT_Long_info)o).value;
    }
    
    int classFileSize() {
        return 9;
    }
    
    void put(final ClassFormatOutput classFormatOutput) throws IOException {
        super.put(classFormatOutput);
        classFormatOutput.writeLong(this.value);
    }
}
