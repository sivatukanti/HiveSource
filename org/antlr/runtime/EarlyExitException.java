// 
// Decompiled by Procyon v0.5.36
// 

package org.antlr.runtime;

public class EarlyExitException extends RecognitionException
{
    public int decisionNumber;
    
    public EarlyExitException() {
    }
    
    public EarlyExitException(final int decisionNumber, final IntStream input) {
        super(input);
        this.decisionNumber = decisionNumber;
    }
}
