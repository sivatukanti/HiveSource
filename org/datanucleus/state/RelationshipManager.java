// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.state;

public interface RelationshipManager
{
    void clearFields();
    
    void relationChange(final int p0, final Object p1, final Object p2);
    
    void relationAdd(final int p0, final Object p1);
    
    void relationRemove(final int p0, final Object p1);
    
    boolean managesField(final int p0);
    
    void checkConsistency();
    
    void process();
}
