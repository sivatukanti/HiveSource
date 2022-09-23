// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.iapi.store.access;

import org.apache.derby.iapi.error.StandardException;
import org.apache.derby.iapi.types.DataValueDescriptor;

public interface SortObserver
{
    DataValueDescriptor[] insertNonDuplicateKey(final DataValueDescriptor[] p0) throws StandardException;
    
    DataValueDescriptor[] insertDuplicateKey(final DataValueDescriptor[] p0, final DataValueDescriptor[] p1) throws StandardException;
    
    void addToFreeList(final DataValueDescriptor[] p0, final int p1);
    
    DataValueDescriptor[] getArrayClone() throws StandardException;
}
