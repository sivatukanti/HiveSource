// 
// Decompiled by Procyon v0.5.36
// 

package antlr;

public class CommonToken extends Token
{
    protected int line;
    protected String text;
    protected int col;
    
    public CommonToken() {
        this.text = null;
    }
    
    public CommonToken(final int type, final String text) {
        this.text = null;
        this.type = type;
        this.setText(text);
    }
    
    public CommonToken(final String text) {
        this.text = null;
        this.text = text;
    }
    
    public int getLine() {
        return this.line;
    }
    
    public String getText() {
        return this.text;
    }
    
    public void setLine(final int line) {
        this.line = line;
    }
    
    public void setText(final String text) {
        this.text = text;
    }
    
    public String toString() {
        return "[\"" + this.getText() + "\",<" + this.type + ">,line=" + this.line + ",col=" + this.col + "]";
    }
    
    public int getColumn() {
        return this.col;
    }
    
    public void setColumn(final int col) {
        this.col = col;
    }
}
