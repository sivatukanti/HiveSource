// 
// Decompiled by Procyon v0.5.36
// 

package antlr;

class RuleRefElement extends AlternativeElement
{
    protected String targetRule;
    protected String args;
    protected String idAssign;
    protected String label;
    
    public RuleRefElement(final Grammar grammar, final Token token, final int n) {
        super(grammar, token, n);
        this.args = null;
        this.idAssign = null;
        this.targetRule = token.getText();
        if (token.type == 24) {
            this.targetRule = CodeGenerator.encodeLexerRuleName(this.targetRule);
        }
    }
    
    public void generate() {
        this.grammar.generator.gen(this);
    }
    
    public String getArgs() {
        return this.args;
    }
    
    public String getIdAssign() {
        return this.idAssign;
    }
    
    public String getLabel() {
        return this.label;
    }
    
    public Lookahead look(final int n) {
        return this.grammar.theLLkAnalyzer.look(n, this);
    }
    
    public void setArgs(final String args) {
        this.args = args;
    }
    
    public void setIdAssign(final String idAssign) {
        this.idAssign = idAssign;
    }
    
    public void setLabel(final String label) {
        this.label = label;
    }
    
    public String toString() {
        if (this.args != null) {
            return " " + this.targetRule + this.args;
        }
        return " " + this.targetRule;
    }
}
