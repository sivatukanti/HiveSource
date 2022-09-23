// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.iapi.sql.execute;

import org.apache.derby.iapi.types.BooleanDataValue;
import org.apache.derby.iapi.error.StandardException;

public interface TupleFilter
{
    void init(final ExecRow p0) throws StandardException;
    
    BooleanDataValue execute(final ExecRow p0) throws StandardException;
}
