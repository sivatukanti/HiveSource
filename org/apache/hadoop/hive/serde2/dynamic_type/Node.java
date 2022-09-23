// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.hive.serde2.dynamic_type;

public interface Node
{
    void jjtOpen();
    
    void jjtClose();
    
    void jjtSetParent(final Node p0);
    
    Node jjtGetParent();
    
    void jjtAddChild(final Node p0, final int p1);
    
    Node jjtGetChild(final int p0);
    
    int jjtGetNumChildren();
}
