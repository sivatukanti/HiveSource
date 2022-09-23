// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.impl.sql.execute;

import org.apache.derby.iapi.error.StandardException;
import org.apache.derby.iapi.sql.Activation;
import org.apache.derby.iapi.sql.execute.NoPutResultSet;

class DistinctGroupedAggregateResultSet extends GroupedAggregateResultSet
{
    DistinctGroupedAggregateResultSet(final NoPutResultSet set, final boolean b, final int n, final int n2, final Activation activation, final int n3, final int n4, final int n5, final double n6, final double n7, final boolean b2) throws StandardException {
        super(set, b, n, n2, activation, n3, n4, n5, n6, n7, b2);
    }
}
