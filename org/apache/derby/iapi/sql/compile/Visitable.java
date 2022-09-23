// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.iapi.sql.compile;

import org.apache.derby.iapi.error.StandardException;

public interface Visitable
{
    Visitable accept(final Visitor p0) throws StandardException;
}
