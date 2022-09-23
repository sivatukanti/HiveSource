// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.iapi.store.access.conglomerate;

import org.apache.derby.iapi.store.access.SortCostController;
import org.apache.derby.iapi.error.StandardException;
import org.apache.derby.iapi.store.access.SortObserver;
import org.apache.derby.iapi.store.access.ColumnOrdering;
import org.apache.derby.iapi.types.DataValueDescriptor;
import java.util.Properties;
import org.apache.derby.iapi.store.access.TransactionController;

public interface SortFactory extends MethodFactory
{
    public static final String MODULE = "org.apache.derby.iapi.store.access.conglomerate.SortFactory";
    
    Sort createSort(final TransactionController p0, final int p1, final Properties p2, final DataValueDescriptor[] p3, final ColumnOrdering[] p4, final SortObserver p5, final boolean p6, final long p7, final int p8) throws StandardException;
    
    SortCostController openSortCostController() throws StandardException;
}
