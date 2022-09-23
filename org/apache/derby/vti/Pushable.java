// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.vti;

import java.sql.SQLException;

public interface Pushable
{
    boolean pushProjection(final VTIEnvironment p0, final int[] p1) throws SQLException;
}
