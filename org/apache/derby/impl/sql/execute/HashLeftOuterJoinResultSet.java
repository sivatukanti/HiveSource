// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.impl.sql.execute;

import org.apache.derby.iapi.services.loader.GeneratedMethod;
import org.apache.derby.iapi.sql.Activation;
import org.apache.derby.iapi.sql.execute.NoPutResultSet;

class HashLeftOuterJoinResultSet extends NestedLoopLeftOuterJoinResultSet
{
    HashLeftOuterJoinResultSet(final NoPutResultSet set, final int n, final NoPutResultSet set2, final int n2, final Activation activation, final GeneratedMethod generatedMethod, final int n3, final GeneratedMethod generatedMethod2, final boolean b, final boolean b2, final boolean b3, final double n4, final double n5, final String s) {
        super(set, n, set2, n2, activation, generatedMethod, n3, generatedMethod2, b, b2, b3, n4, n5, s);
    }
}
