// 
// Decompiled by Procyon v0.5.36
// 

package org.antlr.runtime;

public class MismatchedNotSetException extends MismatchedSetException
{
    public MismatchedNotSetException() {
    }
    
    public MismatchedNotSetException(final BitSet expecting, final IntStream input) {
        super(expecting, input);
    }
    
    public String toString() {
        return "MismatchedNotSetException(" + this.getUnexpectedType() + "!=" + this.expecting + ")";
    }
}
