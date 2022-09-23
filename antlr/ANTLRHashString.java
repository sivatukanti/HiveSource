// 
// Decompiled by Procyon v0.5.36
// 

package antlr;

public class ANTLRHashString
{
    private String s;
    private char[] buf;
    private int len;
    private CharScanner lexer;
    private static final int prime = 151;
    
    public ANTLRHashString(final char[] array, final int n, final CharScanner lexer) {
        this.lexer = lexer;
        this.setBuffer(array, n);
    }
    
    public ANTLRHashString(final CharScanner lexer) {
        this.lexer = lexer;
    }
    
    public ANTLRHashString(final String string, final CharScanner lexer) {
        this.lexer = lexer;
        this.setString(string);
    }
    
    private final char charAt(final int index) {
        return (this.s != null) ? this.s.charAt(index) : this.buf[index];
    }
    
    public boolean equals(final Object o) {
        if (!(o instanceof ANTLRHashString) && !(o instanceof String)) {
            return false;
        }
        ANTLRHashString antlrHashString;
        if (o instanceof String) {
            antlrHashString = new ANTLRHashString((String)o, this.lexer);
        }
        else {
            antlrHashString = (ANTLRHashString)o;
        }
        final int length = this.length();
        if (antlrHashString.length() != length) {
            return false;
        }
        if (this.lexer.getCaseSensitiveLiterals()) {
            for (int i = 0; i < length; ++i) {
                if (this.charAt(i) != antlrHashString.charAt(i)) {
                    return false;
                }
            }
        }
        else {
            for (int j = 0; j < length; ++j) {
                if (this.lexer.toLower(this.charAt(j)) != this.lexer.toLower(antlrHashString.charAt(j))) {
                    return false;
                }
            }
        }
        return true;
    }
    
    public int hashCode() {
        int n = 0;
        final int length = this.length();
        if (this.lexer.getCaseSensitiveLiterals()) {
            for (int i = 0; i < length; ++i) {
                n = n * 151 + this.charAt(i);
            }
        }
        else {
            for (int j = 0; j < length; ++j) {
                n = n * 151 + this.lexer.toLower(this.charAt(j));
            }
        }
        return n;
    }
    
    private final int length() {
        return (this.s != null) ? this.s.length() : this.len;
    }
    
    public void setBuffer(final char[] buf, final int len) {
        this.buf = buf;
        this.len = len;
        this.s = null;
    }
    
    public void setString(final String s) {
        this.s = s;
        this.buf = null;
    }
}
