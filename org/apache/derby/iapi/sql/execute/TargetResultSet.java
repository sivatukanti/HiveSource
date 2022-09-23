// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.iapi.sql.execute;

import org.apache.derby.iapi.error.StandardException;
import org.apache.derby.iapi.types.RowLocation;
import org.apache.derby.iapi.sql.ResultSet;

public interface TargetResultSet extends ResultSet
{
    void changedRow(final ExecRow p0, final RowLocation p1) throws StandardException;
    
    ExecRow preprocessSourceRow(final ExecRow p0) throws StandardException;
}
