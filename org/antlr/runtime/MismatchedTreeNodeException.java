// 
// Decompiled by Procyon v0.5.36
// 

package org.antlr.runtime;

import org.antlr.runtime.tree.TreeNodeStream;

public class MismatchedTreeNodeException extends RecognitionException
{
    public int expecting;
    
    public MismatchedTreeNodeException() {
    }
    
    public MismatchedTreeNodeException(final int expecting, final TreeNodeStream input) {
        super(input);
        this.expecting = expecting;
    }
    
    public String toString() {
        return "MismatchedTreeNodeException(" + this.getUnexpectedType() + "!=" + this.expecting + ")";
    }
}
