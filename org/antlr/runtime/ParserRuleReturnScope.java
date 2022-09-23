// 
// Decompiled by Procyon v0.5.36
// 

package org.antlr.runtime;

public class ParserRuleReturnScope extends RuleReturnScope
{
    public Token start;
    public Token stop;
    public Object tree;
    
    public Object getStart() {
        return this.start;
    }
    
    public Object getStop() {
        return this.stop;
    }
    
    public Object getTree() {
        return this.tree;
    }
}
