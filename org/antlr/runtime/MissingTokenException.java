// 
// Decompiled by Procyon v0.5.36
// 

package org.antlr.runtime;

public class MissingTokenException extends MismatchedTokenException
{
    public Object inserted;
    
    public MissingTokenException() {
    }
    
    public MissingTokenException(final int expecting, final IntStream input, final Object inserted) {
        super(expecting, input);
        this.inserted = inserted;
    }
    
    public int getMissingType() {
        return this.expecting;
    }
    
    public String toString() {
        if (this.inserted != null && this.token != null) {
            return "MissingTokenException(inserted " + this.inserted + " at " + this.token.getText() + ")";
        }
        if (this.token != null) {
            return "MissingTokenException(at " + this.token.getText() + ")";
        }
        return "MissingTokenException";
    }
}
