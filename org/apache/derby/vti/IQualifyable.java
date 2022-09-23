// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.vti;

import java.sql.SQLException;
import org.apache.derby.iapi.store.access.Qualifier;

public interface IQualifyable
{
    void setQualifiers(final VTIEnvironment p0, final Qualifier[][] p1) throws SQLException;
}
