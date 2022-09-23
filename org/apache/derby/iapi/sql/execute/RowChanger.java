// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.iapi.sql.execute;

import org.apache.derby.iapi.store.access.ConglomerateController;
import org.apache.derby.iapi.types.RowLocation;
import org.apache.derby.iapi.error.StandardException;

public interface RowChanger
{
    void open(final int p0) throws StandardException;
    
    void setRowHolder(final TemporaryRowHolder p0);
    
    void setIndexNames(final String[] p0);
    
    void openForUpdate(final boolean[] p0, final int p1, final boolean p2) throws StandardException;
    
    void insertRow(final ExecRow p0) throws StandardException;
    
    void deleteRow(final ExecRow p0, final RowLocation p1) throws StandardException;
    
    void updateRow(final ExecRow p0, final ExecRow p1, final RowLocation p2) throws StandardException;
    
    void finish() throws StandardException;
    
    void close() throws StandardException;
    
    ConglomerateController getHeapConglomerateController();
    
    void open(final int p0, final boolean p1) throws StandardException;
    
    int findSelectedCol(final int p0);
}
