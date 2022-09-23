// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.impl.store.replication.master;

import org.apache.derby.iapi.error.StandardException;
import java.io.IOException;

interface LogShipper
{
    void flushedInstance(final long p0);
    
    void forceFlush() throws IOException, StandardException;
    
    void flushBuffer() throws IOException, StandardException;
    
    void workToDo();
}
