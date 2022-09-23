// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.iapi.store.access;

import org.apache.derby.iapi.error.StandardException;
import org.apache.derby.iapi.types.DataValueDescriptor;

public interface SortController
{
    void completedInserts();
    
    void insert(final DataValueDescriptor[] p0) throws StandardException;
    
    SortInfo getSortInfo() throws StandardException;
}
