// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.iapi.store.access;

import org.apache.derby.iapi.error.StandardException;
import java.util.Properties;

public interface SortInfo
{
    Properties getAllSortInfo(final Properties p0) throws StandardException;
}
