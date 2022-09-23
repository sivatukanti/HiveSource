// 
// Decompiled by Procyon v0.5.36
// 

package antlr;

public class Token implements Cloneable
{
    public static final int MIN_USER_TYPE = 4;
    public static final int NULL_TREE_LOOKAHEAD = 3;
    public static final int INVALID_TYPE = 0;
    public static final int EOF_TYPE = 1;
    public static final int SKIP = -1;
    protected int type;
    public static Token badToken;
    
    public Token() {
        this.type = 0;
    }
    
    public Token(final int type) {
        this.type = 0;
        this.type = type;
    }
    
    public Token(final int type, final String text) {
        this.type = 0;
        this.type = type;
        this.setText(text);
    }
    
    public int getColumn() {
        return 0;
    }
    
    public int getLine() {
        return 0;
    }
    
    public String getFilename() {
        return null;
    }
    
    public void setFilename(final String s) {
    }
    
    public String getText() {
        return "<no text>";
    }
    
    public void setText(final String s) {
    }
    
    public void setColumn(final int n) {
    }
    
    public void setLine(final int n) {
    }
    
    public int getType() {
        return this.type;
    }
    
    public void setType(final int type) {
        this.type = type;
    }
    
    public String toString() {
        return "[\"" + this.getText() + "\",<" + this.getType() + ">]";
    }
    
    static {
        Token.badToken = new Token(0, "<no text>");
    }
}
