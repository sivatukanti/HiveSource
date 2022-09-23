// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.iapi.services.classfile;

import java.io.IOException;

class CONSTANT_Integer_info extends ConstantPoolEntry
{
    private final int value;
    
    CONSTANT_Integer_info(final int value) {
        super(3);
        this.value = value;
    }
    
    public int hashCode() {
        return this.value;
    }
    
    void put(final ClassFormatOutput classFormatOutput) throws IOException {
        super.put(classFormatOutput);
        classFormatOutput.putU4(this.value);
    }
    
    public boolean equals(final Object o) {
        return o instanceof CONSTANT_Integer_info && this.value == ((CONSTANT_Integer_info)o).value;
    }
    
    int classFileSize() {
        return 5;
    }
}
