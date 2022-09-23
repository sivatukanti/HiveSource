// 
// Decompiled by Procyon v0.5.36
// 

package org.antlr.runtime;

public class MismatchedSetException extends RecognitionException
{
    public BitSet expecting;
    
    public MismatchedSetException() {
    }
    
    public MismatchedSetException(final BitSet expecting, final IntStream input) {
        super(input);
        this.expecting = expecting;
    }
    
    public String toString() {
        return "MismatchedSetException(" + this.getUnexpectedType() + "!=" + this.expecting + ")";
    }
}
