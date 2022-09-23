// 
// Decompiled by Procyon v0.5.36
// 

package antlr;

import antlr.collections.impl.Vector;

class AlternativeBlock extends AlternativeElement
{
    protected String initAction;
    protected Vector alternatives;
    protected String label;
    protected int alti;
    protected int altj;
    protected int analysisAlt;
    protected boolean hasAnAction;
    protected boolean hasASynPred;
    protected int ID;
    protected static int nblks;
    boolean not;
    boolean greedy;
    boolean greedySet;
    protected boolean doAutoGen;
    protected boolean warnWhenFollowAmbig;
    protected boolean generateAmbigWarnings;
    
    public AlternativeBlock(final Grammar grammar) {
        super(grammar);
        this.initAction = null;
        this.hasAnAction = false;
        this.hasASynPred = false;
        this.ID = 0;
        this.not = false;
        this.greedy = true;
        this.greedySet = false;
        this.doAutoGen = true;
        this.warnWhenFollowAmbig = true;
        this.generateAmbigWarnings = true;
        this.alternatives = new Vector(5);
        this.not = false;
        ++AlternativeBlock.nblks;
        this.ID = AlternativeBlock.nblks;
    }
    
    public AlternativeBlock(final Grammar grammar, final Token token, final boolean not) {
        super(grammar, token);
        this.initAction = null;
        this.hasAnAction = false;
        this.hasASynPred = false;
        this.ID = 0;
        this.not = false;
        this.greedy = true;
        this.greedySet = false;
        this.doAutoGen = true;
        this.warnWhenFollowAmbig = true;
        this.generateAmbigWarnings = true;
        this.alternatives = new Vector(5);
        this.not = not;
        ++AlternativeBlock.nblks;
        this.ID = AlternativeBlock.nblks;
    }
    
    public void addAlternative(final Alternative alternative) {
        this.alternatives.appendElement(alternative);
    }
    
    public void generate() {
        this.grammar.generator.gen(this);
    }
    
    public Alternative getAlternativeAt(final int n) {
        return (Alternative)this.alternatives.elementAt(n);
    }
    
    public Vector getAlternatives() {
        return this.alternatives;
    }
    
    public boolean getAutoGen() {
        return this.doAutoGen;
    }
    
    public String getInitAction() {
        return this.initAction;
    }
    
    public String getLabel() {
        return this.label;
    }
    
    public Lookahead look(final int n) {
        return this.grammar.theLLkAnalyzer.look(n, this);
    }
    
    public void prepareForAnalysis() {
        for (int i = 0; i < this.alternatives.size(); ++i) {
            final Alternative alternative = (Alternative)this.alternatives.elementAt(i);
            alternative.cache = new Lookahead[this.grammar.maxk + 1];
            alternative.lookaheadDepth = -1;
        }
    }
    
    public void removeTrackingOfRuleRefs(final Grammar grammar) {
        for (int i = 0; i < this.alternatives.size(); ++i) {
            for (AlternativeElement alternativeElement = this.getAlternativeAt(i).head; alternativeElement != null; alternativeElement = alternativeElement.next) {
                if (alternativeElement instanceof RuleRefElement) {
                    final RuleRefElement ruleRefElement = (RuleRefElement)alternativeElement;
                    final RuleSymbol ruleSymbol = (RuleSymbol)grammar.getSymbol(ruleRefElement.targetRule);
                    if (ruleSymbol == null) {
                        this.grammar.antlrTool.error("rule " + ruleRefElement.targetRule + " referenced in (...)=>, but not defined");
                    }
                    else {
                        ruleSymbol.references.removeElement(ruleRefElement);
                    }
                }
                else if (alternativeElement instanceof AlternativeBlock) {
                    ((AlternativeBlock)alternativeElement).removeTrackingOfRuleRefs(grammar);
                }
            }
        }
    }
    
    public void setAlternatives(final Vector alternatives) {
        this.alternatives = alternatives;
    }
    
    public void setAutoGen(final boolean doAutoGen) {
        this.doAutoGen = doAutoGen;
    }
    
    public void setInitAction(final String initAction) {
        this.initAction = initAction;
    }
    
    public void setLabel(final String label) {
        this.label = label;
    }
    
    public void setOption(final Token token, final Token token2) {
        if (token.getText().equals("warnWhenFollowAmbig")) {
            if (token2.getText().equals("true")) {
                this.warnWhenFollowAmbig = true;
            }
            else if (token2.getText().equals("false")) {
                this.warnWhenFollowAmbig = false;
            }
            else {
                this.grammar.antlrTool.error("Value for warnWhenFollowAmbig must be true or false", this.grammar.getFilename(), token.getLine(), token.getColumn());
            }
        }
        else if (token.getText().equals("generateAmbigWarnings")) {
            if (token2.getText().equals("true")) {
                this.generateAmbigWarnings = true;
            }
            else if (token2.getText().equals("false")) {
                this.generateAmbigWarnings = false;
            }
            else {
                this.grammar.antlrTool.error("Value for generateAmbigWarnings must be true or false", this.grammar.getFilename(), token.getLine(), token.getColumn());
            }
        }
        else if (token.getText().equals("greedy")) {
            if (token2.getText().equals("true")) {
                this.greedy = true;
                this.greedySet = true;
            }
            else if (token2.getText().equals("false")) {
                this.greedy = false;
                this.greedySet = true;
            }
            else {
                this.grammar.antlrTool.error("Value for greedy must be true or false", this.grammar.getFilename(), token.getLine(), token.getColumn());
            }
        }
        else {
            this.grammar.antlrTool.error("Invalid subrule option: " + token.getText(), this.grammar.getFilename(), token.getLine(), token.getColumn());
        }
    }
    
    public String toString() {
        String str = " (";
        if (this.initAction != null) {
            str += this.initAction;
        }
        for (int i = 0; i < this.alternatives.size(); ++i) {
            final Alternative alternative = this.getAlternativeAt(i);
            final Lookahead[] cache = alternative.cache;
            final int lookaheadDepth = alternative.lookaheadDepth;
            if (lookaheadDepth != -1) {
                if (lookaheadDepth == Integer.MAX_VALUE) {
                    str += "{?}:";
                }
                else {
                    String str2 = str + " {";
                    for (int j = 1; j <= lookaheadDepth; ++j) {
                        str2 += cache[j].toString(",", this.grammar.tokenManager.getVocabulary());
                        if (j < lookaheadDepth && cache[j + 1] != null) {
                            str2 += ";";
                        }
                    }
                    str = str2 + "}:";
                }
            }
            AlternativeElement obj = alternative.head;
            final String semPred = alternative.semPred;
            if (semPred != null) {
                str += semPred;
            }
            while (obj != null) {
                str += obj;
                obj = obj.next;
            }
            if (i < this.alternatives.size() - 1) {
                str += " |";
            }
        }
        return str + " )";
    }
}
