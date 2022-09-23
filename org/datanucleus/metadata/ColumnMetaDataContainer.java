// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.metadata;

public interface ColumnMetaDataContainer
{
    ColumnMetaData[] getColumnMetaData();
    
    void addColumn(final ColumnMetaData p0);
}
