// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.iapi.sql.compile;

import org.apache.derby.iapi.util.JBitSet;
import org.apache.derby.iapi.error.StandardException;

public interface RequiredRowOrdering
{
    public static final int SORT_REQUIRED = 1;
    public static final int ELIMINATE_DUPS = 2;
    public static final int NOTHING_REQUIRED = 3;
    
    int sortRequired(final RowOrdering p0, final OptimizableList p1, final int[] p2) throws StandardException;
    
    int sortRequired(final RowOrdering p0, final JBitSet p1, final OptimizableList p2, final int[] p3) throws StandardException;
    
    void estimateCost(final double p0, final RowOrdering p1, final CostEstimate p2) throws StandardException;
    
    void sortNeeded();
    
    void sortNotNeeded();
    
    boolean getSortNeeded();
}
