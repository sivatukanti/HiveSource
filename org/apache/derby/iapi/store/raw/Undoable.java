// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.iapi.store.raw;

import java.io.IOException;
import org.apache.derby.iapi.error.StandardException;
import org.apache.derby.iapi.services.io.LimitObjectInput;

public interface Undoable extends Loggable
{
    Compensation generateUndo(final Transaction p0, final LimitObjectInput p1) throws StandardException, IOException;
}
