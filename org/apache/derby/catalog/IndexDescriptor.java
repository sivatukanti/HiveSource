// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.catalog;

public interface IndexDescriptor
{
    boolean isUnique();
    
    boolean isUniqueWithDuplicateNulls();
    
    int[] baseColumnPositions();
    
    int getKeyColumnPosition(final int p0);
    
    int numberOfOrderedColumns();
    
    String indexType();
    
    boolean[] isAscending();
    
    boolean isAscending(final Integer p0);
    
    boolean isDescending(final Integer p0);
    
    void setBaseColumnPositions(final int[] p0);
    
    void setIsAscending(final boolean[] p0);
    
    void setNumberOfOrderedColumns(final int p0);
}
