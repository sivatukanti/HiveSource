// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.iapi.services.classfile;

import java.io.IOException;

public final class CONSTANT_Index_info extends ConstantPoolEntry
{
    private int i1;
    private int i2;
    
    CONSTANT_Index_info(final int n, final int i1, final int i2) {
        super(n);
        this.i1 = i1;
        this.i2 = i2;
    }
    
    public int hashCode() {
        return this.tag << 16 | (this.i1 << 8 ^ this.i2);
    }
    
    public boolean equals(final Object o) {
        if (o instanceof CONSTANT_Index_info) {
            final CONSTANT_Index_info constant_Index_info = (CONSTANT_Index_info)o;
            return this.tag == constant_Index_info.tag && this.i1 == constant_Index_info.i1 && this.i2 == constant_Index_info.i2;
        }
        return false;
    }
    
    void set(final int tag, final int i1, final int i2) {
        this.tag = tag;
        this.i1 = i1;
        this.i2 = i2;
    }
    
    int classFileSize() {
        return 3 + ((this.i2 != 0) ? 2 : 0);
    }
    
    void put(final ClassFormatOutput classFormatOutput) throws IOException {
        super.put(classFormatOutput);
        classFormatOutput.putU2(this.i1);
        if (this.i2 != 0) {
            classFormatOutput.putU2(this.i2);
        }
    }
    
    public int getI1() {
        return this.i1;
    }
    
    public int getI2() {
        return this.i2;
    }
}
