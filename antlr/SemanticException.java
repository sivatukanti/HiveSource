// 
// Decompiled by Procyon v0.5.36
// 

package antlr;

public class SemanticException extends RecognitionException
{
    public SemanticException(final String s) {
        super(s);
    }
    
    public SemanticException(final String s, final String s2, final int n) {
        this(s, s2, n, -1);
    }
    
    public SemanticException(final String s, final String s2, final int n, final int n2) {
        super(s, s2, n, n2);
    }
}
