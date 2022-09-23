// 
// Decompiled by Procyon v0.5.36
// 

package antlr;

abstract class GrammarAtom extends AlternativeElement
{
    protected String label;
    protected String atomText;
    protected int tokenType;
    protected boolean not;
    protected String ASTNodeType;
    
    public GrammarAtom(final Grammar grammar, final Token token, final int n) {
        super(grammar, token, n);
        this.tokenType = 0;
        this.not = false;
        this.ASTNodeType = null;
        this.atomText = token.getText();
    }
    
    public String getLabel() {
        return this.label;
    }
    
    public String getText() {
        return this.atomText;
    }
    
    public int getType() {
        return this.tokenType;
    }
    
    public void setLabel(final String label) {
        this.label = label;
    }
    
    public String getASTNodeType() {
        return this.ASTNodeType;
    }
    
    public void setASTNodeType(final String astNodeType) {
        this.ASTNodeType = astNodeType;
    }
    
    public void setOption(final Token token, final Token token2) {
        if (token.getText().equals("AST")) {
            this.setASTNodeType(token2.getText());
        }
        else {
            this.grammar.antlrTool.error("Invalid element option:" + token.getText(), this.grammar.getFilename(), token.getLine(), token.getColumn());
        }
    }
    
    public String toString() {
        String str = " ";
        if (this.label != null) {
            str = str + this.label + ":";
        }
        if (this.not) {
            str += "~";
        }
        return str + this.atomText;
    }
}
