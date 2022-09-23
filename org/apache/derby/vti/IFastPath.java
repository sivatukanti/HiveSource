// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.vti;

import java.sql.ResultSet;
import org.apache.derby.iapi.types.DataValueDescriptor;
import java.sql.SQLException;
import org.apache.derby.iapi.error.StandardException;

public interface IFastPath
{
    public static final int SCAN_COMPLETED = -1;
    public static final int GOT_ROW = 0;
    public static final int NEED_RS = 1;
    
    boolean executeAsFastPath() throws StandardException, SQLException;
    
    int nextRow(final DataValueDescriptor[] p0) throws StandardException, SQLException;
    
    void currentRow(final ResultSet p0, final DataValueDescriptor[] p1) throws StandardException, SQLException;
    
    void rowsDone() throws StandardException, SQLException;
}
