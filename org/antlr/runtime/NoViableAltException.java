// 
// Decompiled by Procyon v0.5.36
// 

package org.antlr.runtime;

public class NoViableAltException extends RecognitionException
{
    public String grammarDecisionDescription;
    public int decisionNumber;
    public int stateNumber;
    
    public NoViableAltException() {
    }
    
    public NoViableAltException(final String grammarDecisionDescription, final int decisionNumber, final int stateNumber, final IntStream input) {
        super(input);
        this.grammarDecisionDescription = grammarDecisionDescription;
        this.decisionNumber = decisionNumber;
        this.stateNumber = stateNumber;
    }
    
    public String toString() {
        if (this.input instanceof CharStream) {
            return "NoViableAltException('" + (char)this.getUnexpectedType() + "'@[" + this.grammarDecisionDescription + "])";
        }
        return "NoViableAltException(" + this.getUnexpectedType() + "@[" + this.grammarDecisionDescription + "])";
    }
}
