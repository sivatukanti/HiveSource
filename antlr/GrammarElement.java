// 
// Decompiled by Procyon v0.5.36
// 

package antlr;

abstract class GrammarElement
{
    public static final int AUTO_GEN_NONE = 1;
    public static final int AUTO_GEN_CARET = 2;
    public static final int AUTO_GEN_BANG = 3;
    protected Grammar grammar;
    protected int line;
    protected int column;
    
    public GrammarElement(final Grammar grammar) {
        this.grammar = grammar;
        this.line = -1;
        this.column = -1;
    }
    
    public GrammarElement(final Grammar grammar, final Token token) {
        this.grammar = grammar;
        this.line = token.getLine();
        this.column = token.getColumn();
    }
    
    public void generate() {
    }
    
    public int getLine() {
        return this.line;
    }
    
    public int getColumn() {
        return this.column;
    }
    
    public Lookahead look(final int n) {
        return null;
    }
    
    public abstract String toString();
}
