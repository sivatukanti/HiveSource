// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.iapi.services.classfile;

import java.io.IOException;

public abstract class ConstantPoolEntry
{
    protected int tag;
    protected boolean doubleSlot;
    protected int index;
    
    protected ConstantPoolEntry(final int tag) {
        this.tag = tag;
    }
    
    int getIndex() {
        return this.index;
    }
    
    void setIndex(final int index) {
        this.index = index;
    }
    
    boolean doubleSlot() {
        return this.doubleSlot;
    }
    
    Object getKey() {
        return this;
    }
    
    abstract int classFileSize();
    
    void put(final ClassFormatOutput classFormatOutput) throws IOException {
        classFormatOutput.putU1(this.tag);
    }
    
    final int getTag() {
        return this.tag;
    }
    
    int getI1() {
        return 0;
    }
    
    int getI2() {
        return 0;
    }
}
