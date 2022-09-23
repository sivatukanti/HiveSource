// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.query.compiler;

import org.datanucleus.query.node.Node;

public interface Parser
{
    Node parse(final String p0);
    
    Node[] parseFrom(final String p0);
    
    Node[] parseUpdate(final String p0);
    
    Node[] parseOrder(final String p0);
    
    Node[] parseResult(final String p0);
    
    Node[] parseTupple(final String p0);
    
    Node[][] parseVariables(final String p0);
    
    Node parseVariable(final String p0);
    
    Node[][] parseParameters(final String p0);
}
