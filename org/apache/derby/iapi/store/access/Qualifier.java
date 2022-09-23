// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.iapi.store.access;

import org.apache.derby.iapi.error.StandardException;
import org.apache.derby.iapi.types.DataValueDescriptor;

public interface Qualifier
{
    public static final int VARIANT = 0;
    public static final int SCAN_INVARIANT = 1;
    public static final int QUERY_INVARIANT = 2;
    public static final int CONSTANT = 3;
    
    int getColumnId();
    
    DataValueDescriptor getOrderable() throws StandardException;
    
    int getOperator();
    
    boolean negateCompareResult();
    
    boolean getOrderedNulls();
    
    boolean getUnknownRV();
    
    void clearOrderableCache();
    
    void reinitialize();
}
