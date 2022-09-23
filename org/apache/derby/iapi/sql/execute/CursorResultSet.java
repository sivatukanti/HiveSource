// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.iapi.sql.execute;

import org.apache.derby.iapi.error.StandardException;
import org.apache.derby.iapi.types.RowLocation;
import org.apache.derby.iapi.sql.ResultSet;

public interface CursorResultSet extends ResultSet
{
    RowLocation getRowLocation() throws StandardException;
    
    ExecRow getCurrentRow() throws StandardException;
}
