// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.iapi.sql.compile;

import org.apache.derby.iapi.error.StandardException;
import org.apache.derby.iapi.types.DataValueDescriptor;
import org.apache.derby.iapi.util.JBitSet;

public interface OptimizablePredicate
{
    JBitSet getReferencedMap();
    
    boolean hasSubquery();
    
    boolean hasMethodCall();
    
    void markStartKey();
    
    boolean isStartKey();
    
    void markStopKey();
    
    boolean isStopKey();
    
    void markQualifier();
    
    boolean isQualifier();
    
    boolean compareWithKnownConstant(final Optimizable p0, final boolean p1);
    
    DataValueDescriptor getCompareValue(final Optimizable p0) throws StandardException;
    
    boolean equalsComparisonWithConstantExpression(final Optimizable p0);
    
    int hasEqualOnColumnList(final int[] p0, final Optimizable p1) throws StandardException;
    
    double selectivity(final Optimizable p0) throws StandardException;
    
    int getIndexPosition();
}
