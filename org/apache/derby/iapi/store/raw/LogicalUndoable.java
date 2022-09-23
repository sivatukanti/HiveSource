// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.iapi.store.raw;

import java.io.IOException;
import org.apache.derby.iapi.services.io.LimitObjectInput;
import org.apache.derby.iapi.error.StandardException;

public interface LogicalUndoable extends Undoable
{
    ContainerHandle getContainer() throws StandardException;
    
    RecordHandle getRecordHandle();
    
    void restoreLoggedRow(final Object[] p0, final LimitObjectInput p1) throws StandardException, IOException;
    
    void resetRecordHandle(final RecordHandle p0);
}
