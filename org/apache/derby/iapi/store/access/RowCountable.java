// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.iapi.store.access;

import org.apache.derby.iapi.error.StandardException;

public interface RowCountable
{
    long getEstimatedRowCount() throws StandardException;
    
    void setEstimatedRowCount(final long p0) throws StandardException;
}
