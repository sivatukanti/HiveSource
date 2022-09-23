// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.impl.store.access.btree.index;

import java.io.IOException;
import java.io.ObjectOutput;

public class B2I_10_3 extends B2I
{
    public int getTypeFormatId() {
        return 466;
    }
    
    public void writeExternal(final ObjectOutput objectOutput) throws IOException {
        this.writeExternal_v10_3(objectOutput);
    }
}
