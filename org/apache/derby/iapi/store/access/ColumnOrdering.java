// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.iapi.store.access;

public interface ColumnOrdering
{
    int getColumnId();
    
    boolean getIsAscending();
    
    boolean getIsNullsOrderedLow();
}
