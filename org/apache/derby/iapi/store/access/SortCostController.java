// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.iapi.store.access;

import org.apache.derby.iapi.error.StandardException;
import org.apache.derby.iapi.types.DataValueDescriptor;

public interface SortCostController
{
    void close();
    
    double getSortCost(final DataValueDescriptor[] p0, final ColumnOrdering[] p1, final boolean p2, final long p3, final long p4, final int p5) throws StandardException;
}
