// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.iapi.sql.compile;

import org.apache.derby.iapi.error.StandardException;

public interface RowOrdering
{
    public static final int ASCENDING = 1;
    public static final int DESCENDING = 2;
    public static final int DONTCARE = 3;
    
    boolean orderedOnColumn(final int p0, final int p1, final int p2, final int p3) throws StandardException;
    
    boolean orderedOnColumn(final int p0, final int p1, final int p2) throws StandardException;
    
    void addOrderedColumn(final int p0, final int p1, final int p2);
    
    void nextOrderPosition(final int p0);
    
    void optimizableAlwaysOrdered(final Optimizable p0);
    
    void columnAlwaysOrdered(final Optimizable p0, final int p1);
    
    boolean isColumnAlwaysOrdered(final int p0, final int p1);
    
    boolean alwaysOrdered(final int p0);
    
    void removeOptimizable(final int p0);
    
    void addUnorderedOptimizable(final Optimizable p0);
    
    void copy(final RowOrdering p0);
}
