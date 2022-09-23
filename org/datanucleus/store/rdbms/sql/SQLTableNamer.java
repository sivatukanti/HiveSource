// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.store.rdbms.sql;

import org.datanucleus.store.rdbms.table.Table;

public interface SQLTableNamer
{
    String getAliasForTable(final SQLStatement p0, final Table p1, final String p2);
}
