// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.store.rdbms.mapping.java;

import org.datanucleus.ClassLoaderResolver;
import org.datanucleus.store.rdbms.table.Table;
import org.datanucleus.metadata.AbstractMemberMetaData;

public class EmbeddedElementPCMapping extends EmbeddedMapping
{
    @Override
    public void initialize(final AbstractMemberMetaData fmd, final Table table, final ClassLoaderResolver clr) {
        this.initialize(fmd, table, clr, fmd.getElementMetaData().getEmbeddedMetaData(), fmd.getCollection().getElementType(), 2);
    }
}
