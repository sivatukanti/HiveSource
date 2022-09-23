// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.store.rdbms.mapping.java;

import org.datanucleus.ClassLoaderResolver;
import org.datanucleus.store.rdbms.table.Table;
import org.datanucleus.metadata.AbstractMemberMetaData;

public class EmbeddedValuePCMapping extends EmbeddedMapping
{
    @Override
    public void initialize(final AbstractMemberMetaData mmd, final Table table, final ClassLoaderResolver clr) {
        this.initialize(mmd, table, clr, mmd.getValueMetaData().getEmbeddedMetaData(), mmd.getMap().getValueType(), 4);
    }
}
