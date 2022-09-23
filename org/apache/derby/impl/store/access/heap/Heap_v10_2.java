// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.impl.store.access.heap;

import java.io.IOException;
import java.io.ObjectOutput;

public class Heap_v10_2 extends Heap
{
    public int getTypeFormatId() {
        return 91;
    }
    
    public void writeExternal(final ObjectOutput objectOutput) throws IOException {
        super.writeExternal_v10_2(objectOutput);
    }
}
