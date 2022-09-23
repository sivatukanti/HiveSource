// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.iapi.store.access;

import org.apache.derby.iapi.error.StandardException;
import org.apache.derby.iapi.types.RowLocation;

public interface RowLocationRetRowSource extends RowSource
{
    boolean needsRowLocation();
    
    void rowLocation(final RowLocation p0) throws StandardException;
}
