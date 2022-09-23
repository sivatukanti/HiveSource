// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.iapi.services.classfile;

import java.io.IOException;
import java.util.Vector;

class Attributes extends Vector
{
    private int classFileSize;
    
    Attributes(final int initialCapacity) {
        super(initialCapacity);
    }
    
    void put(final ClassFormatOutput classFormatOutput) throws IOException {
        for (int size = this.size(), i = 0; i < size; ++i) {
            ((AttributeEntry)this.elementAt(i)).put(classFormatOutput);
        }
    }
    
    int classFileSize() {
        return this.classFileSize;
    }
    
    void addEntry(final AttributeEntry obj) {
        this.addElement(obj);
        this.classFileSize += obj.classFileSize();
    }
}
