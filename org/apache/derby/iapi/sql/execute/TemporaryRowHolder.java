// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.iapi.sql.execute;

import org.apache.derby.iapi.error.StandardException;

public interface TemporaryRowHolder
{
    void insert(final ExecRow p0) throws StandardException;
    
    CursorResultSet getResultSet();
    
    void close() throws StandardException;
    
    long getTemporaryConglomId();
    
    long getPositionIndexConglomId();
    
    void setRowHolderTypeToUniqueStream();
}
