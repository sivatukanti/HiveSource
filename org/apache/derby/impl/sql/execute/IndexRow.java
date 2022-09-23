// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.impl.sql.execute;

import org.apache.derby.iapi.sql.execute.ExecRow;
import org.apache.derby.iapi.sql.execute.ExecIndexRow;

public class IndexRow extends ValueRow implements ExecIndexRow
{
    private boolean[] orderedNulls;
    
    IndexRow(final int n) {
        super(n);
        this.orderedNulls = new boolean[n];
    }
    
    public void orderedNulls(final int n) {
        this.orderedNulls[n] = true;
    }
    
    public boolean areNullsOrdered(final int n) {
        return this.orderedNulls[n];
    }
    
    public void execRowToExecIndexRow(final ExecRow execRow) {
    }
    
    ExecRow cloneMe() {
        return new IndexRow(this.nColumns());
    }
}
