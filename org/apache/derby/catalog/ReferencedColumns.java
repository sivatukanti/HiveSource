// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.catalog;

public interface ReferencedColumns
{
    int[] getReferencedColumnPositions();
    
    int[] getTriggerActionReferencedColumnPositions();
}
