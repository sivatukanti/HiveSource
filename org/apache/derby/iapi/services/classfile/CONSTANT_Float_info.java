// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.iapi.services.classfile;

import java.io.IOException;

final class CONSTANT_Float_info extends ConstantPoolEntry
{
    private final float value;
    
    CONSTANT_Float_info(final float value) {
        super(4);
        this.value = value;
    }
    
    public int hashCode() {
        return (int)this.value;
    }
    
    public boolean equals(final Object o) {
        return o instanceof CONSTANT_Float_info && this.value == ((CONSTANT_Float_info)o).value;
    }
    
    int classFileSize() {
        return 5;
    }
    
    void put(final ClassFormatOutput classFormatOutput) throws IOException {
        super.put(classFormatOutput);
        classFormatOutput.writeFloat(this.value);
    }
}
