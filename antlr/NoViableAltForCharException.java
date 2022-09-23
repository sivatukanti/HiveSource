// 
// Decompiled by Procyon v0.5.36
// 

package antlr;

public class NoViableAltForCharException extends RecognitionException
{
    public char foundChar;
    
    public NoViableAltForCharException(final char foundChar, final CharScanner charScanner) {
        super("NoViableAlt", charScanner.getFilename(), charScanner.getLine(), charScanner.getColumn());
        this.foundChar = foundChar;
    }
    
    public NoViableAltForCharException(final char c, final String s, final int n) {
        this(c, s, n, -1);
    }
    
    public NoViableAltForCharException(final char foundChar, final String s, final int n, final int n2) {
        super("NoViableAlt", s, n, n2);
        this.foundChar = foundChar;
    }
    
    public String getMessage() {
        final String s = "unexpected char: ";
        String s2;
        if (this.foundChar >= ' ' && this.foundChar <= '~') {
            s2 = s + '\'' + this.foundChar + '\'';
        }
        else {
            s2 = s + "0x" + Integer.toHexString(this.foundChar).toUpperCase();
        }
        return s2;
    }
}
