// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.iapi.services.classfile;

import java.io.IOException;

final class CONSTANT_Double_info extends ConstantPoolEntry
{
    private final double value;
    
    CONSTANT_Double_info(final double value) {
        super(6);
        this.doubleSlot = true;
        this.value = value;
    }
    
    public int hashCode() {
        return (int)this.value;
    }
    
    int classFileSize() {
        return 9;
    }
    
    void put(final ClassFormatOutput classFormatOutput) throws IOException {
        super.put(classFormatOutput);
        classFormatOutput.writeDouble(this.value);
    }
    
    public boolean equals(final Object o) {
        return o instanceof CONSTANT_Double_info && this.value == ((CONSTANT_Double_info)o).value;
    }
}
