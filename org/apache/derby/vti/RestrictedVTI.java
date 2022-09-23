// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.vti;

import java.sql.SQLException;

public interface RestrictedVTI
{
    void initScan(final String[] p0, final Restriction p1) throws SQLException;
}
