// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.store.rdbms.mapping.java;

import org.datanucleus.metadata.DiscriminatorMetaData;
import org.datanucleus.store.rdbms.table.Table;

public final class DiscriminatorLongMapping extends DiscriminatorMapping
{
    public DiscriminatorLongMapping(final Table table, final JavaTypeMapping delegate, final DiscriminatorMetaData dismd) {
        super(table, delegate, dismd);
    }
}
