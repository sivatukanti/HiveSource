// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.store.schema.table;

import org.datanucleus.metadata.AbstractMemberMetaData;
import org.datanucleus.metadata.AbstractClassMetaData;

public interface Table
{
    String getIdentifier();
    
    AbstractClassMetaData getClassMetaData();
    
    int getNumberOfColumns();
    
    BasicColumn getColumnForMember(final AbstractMemberMetaData p0);
    
    BasicColumn getColumnForPosition(final int p0);
}
