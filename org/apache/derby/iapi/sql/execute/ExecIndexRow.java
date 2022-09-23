// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.iapi.sql.execute;

public interface ExecIndexRow extends ExecRow
{
    void orderedNulls(final int p0);
    
    boolean areNullsOrdered(final int p0);
    
    void execRowToExecIndexRow(final ExecRow p0);
}
