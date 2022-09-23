// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.iapi.sql.execute;

import org.apache.derby.iapi.error.StandardException;

public interface ExecutionStmtValidator
{
    void validateStatement(final ConstantAction p0) throws StandardException;
}
