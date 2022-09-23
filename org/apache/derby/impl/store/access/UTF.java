// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.impl.store.access;

import org.apache.derby.iapi.types.DataValueDescriptor;
import org.apache.derby.iapi.types.UserType;

public class UTF extends UserType
{
    public UTF() {
    }
    
    public UTF(final String s) {
        super(s);
    }
    
    public int compare(final DataValueDescriptor dataValueDescriptor) {
        return ((String)this.getObject()).compareTo((String)((UTF)dataValueDescriptor).getObject());
    }
}
