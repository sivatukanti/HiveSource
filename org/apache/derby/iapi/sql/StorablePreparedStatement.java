// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.iapi.sql;

import org.apache.derby.iapi.error.StandardException;
import org.apache.derby.iapi.sql.execute.ExecPreparedStatement;

public interface StorablePreparedStatement extends ExecPreparedStatement
{
    void loadGeneratedClass() throws StandardException;
}
