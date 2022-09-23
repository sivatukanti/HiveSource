// 
// Decompiled by Procyon v0.5.36
// 

package org.antlr.runtime;

public class UnwantedTokenException extends MismatchedTokenException
{
    public UnwantedTokenException() {
    }
    
    public UnwantedTokenException(final int expecting, final IntStream input) {
        super(expecting, input);
    }
    
    public Token getUnexpectedToken() {
        return this.token;
    }
    
    public String toString() {
        String exp = ", expected " + this.expecting;
        if (this.expecting == 0) {
            exp = "";
        }
        if (this.token == null) {
            return "UnwantedTokenException(found=" + (Object)null + exp + ")";
        }
        return "UnwantedTokenException(found=" + this.token.getText() + exp + ")";
    }
}
