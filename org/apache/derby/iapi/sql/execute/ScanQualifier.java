// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.iapi.sql.execute;

import org.apache.derby.iapi.types.DataValueDescriptor;
import org.apache.derby.iapi.store.access.Qualifier;

public interface ScanQualifier extends Qualifier
{
    void setQualifier(final int p0, final DataValueDescriptor p1, final int p2, final boolean p3, final boolean p4, final boolean p5);
}
