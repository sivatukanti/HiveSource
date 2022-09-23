// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.store.schema.table;

import org.datanucleus.metadata.AbstractMemberMetaData;
import org.datanucleus.metadata.ColumnMetaData;

public interface Column
{
    Table getTable();
    
    ColumnMetaData getColumnMetaData();
    
    AbstractMemberMetaData getMemberMetaData();
    
    String getIdentifier();
}
