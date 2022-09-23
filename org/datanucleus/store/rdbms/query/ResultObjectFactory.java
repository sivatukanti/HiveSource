// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.store.rdbms.query;

import java.sql.ResultSet;
import org.datanucleus.ExecutionContext;

public interface ResultObjectFactory
{
    Object getObject(final ExecutionContext p0, final ResultSet p1);
}
