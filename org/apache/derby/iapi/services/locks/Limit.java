// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.iapi.services.locks;

import org.apache.derby.iapi.error.StandardException;
import java.util.Enumeration;

public interface Limit
{
    void reached(final CompatibilitySpace p0, final Object p1, final int p2, final Enumeration p3, final int p4) throws StandardException;
}
