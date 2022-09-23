// 
// Decompiled by Procyon v0.5.36
// 

package org.antlr.runtime;

public class MismatchedTokenException extends RecognitionException
{
    public int expecting;
    
    public MismatchedTokenException() {
        this.expecting = 0;
    }
    
    public MismatchedTokenException(final int expecting, final IntStream input) {
        super(input);
        this.expecting = 0;
        this.expecting = expecting;
    }
    
    public String toString() {
        return "MismatchedTokenException(" + this.getUnexpectedType() + "!=" + this.expecting + ")";
    }
}
