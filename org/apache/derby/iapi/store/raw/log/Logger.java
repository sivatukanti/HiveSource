// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.iapi.store.raw.log;

import org.apache.derby.iapi.store.raw.xact.TransactionId;
import org.apache.derby.iapi.services.io.LimitObjectInput;
import org.apache.derby.iapi.store.raw.Compensation;
import org.apache.derby.iapi.error.StandardException;
import org.apache.derby.iapi.store.raw.Loggable;
import org.apache.derby.iapi.store.raw.xact.RawTransaction;

public interface Logger
{
    LogInstant logAndDo(final RawTransaction p0, final Loggable p1) throws StandardException;
    
    LogInstant logAndUndo(final RawTransaction p0, final Compensation p1, final LogInstant p2, final LimitObjectInput p3) throws StandardException;
    
    void flush(final LogInstant p0) throws StandardException;
    
    void flushAll() throws StandardException;
    
    void reprepare(final RawTransaction p0, final TransactionId p1, final LogInstant p2, final LogInstant p3) throws StandardException;
    
    void undo(final RawTransaction p0, final TransactionId p1, final LogInstant p2, final LogInstant p3) throws StandardException;
}
