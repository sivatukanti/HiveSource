// 
// Decompiled by Procyon v0.5.36
// 

package antlr;

import java.util.Hashtable;
import antlr.collections.impl.Vector;

public class RuleBlock extends AlternativeBlock
{
    protected String ruleName;
    protected String argAction;
    protected String throwsSpec;
    protected String returnAction;
    protected RuleEndElement endNode;
    protected boolean testLiterals;
    Vector labeledElements;
    protected boolean[] lock;
    protected Lookahead[] cache;
    Hashtable exceptionSpecs;
    protected boolean defaultErrorHandler;
    protected String ignoreRule;
    
    public RuleBlock(final Grammar grammar, final String ruleName) {
        super(grammar);
        this.argAction = null;
        this.throwsSpec = null;
        this.returnAction = null;
        this.testLiterals = false;
        this.defaultErrorHandler = true;
        this.ignoreRule = null;
        this.ruleName = ruleName;
        this.labeledElements = new Vector();
        this.cache = new Lookahead[grammar.maxk + 1];
        this.exceptionSpecs = new Hashtable();
        this.setAutoGen(grammar instanceof ParserGrammar);
    }
    
    public RuleBlock(final Grammar grammar, final String s, final int line, final boolean autoGen) {
        this(grammar, s);
        this.line = line;
        this.setAutoGen(autoGen);
    }
    
    public void addExceptionSpec(final ExceptionSpec value) {
        if (this.findExceptionSpec(value.label) != null) {
            if (value.label != null) {
                this.grammar.antlrTool.error("Rule '" + this.ruleName + "' already has an exception handler for label: " + value.label);
            }
            else {
                this.grammar.antlrTool.error("Rule '" + this.ruleName + "' already has an exception handler");
            }
        }
        else {
            this.exceptionSpecs.put((value.label == null) ? "" : value.label.getText(), value);
        }
    }
    
    public ExceptionSpec findExceptionSpec(final Token token) {
        return this.exceptionSpecs.get((token == null) ? "" : token.getText());
    }
    
    public ExceptionSpec findExceptionSpec(final String s) {
        return this.exceptionSpecs.get((s == null) ? "" : s);
    }
    
    public void generate() {
        this.grammar.generator.gen(this);
    }
    
    public boolean getDefaultErrorHandler() {
        return this.defaultErrorHandler;
    }
    
    public RuleEndElement getEndElement() {
        return this.endNode;
    }
    
    public String getIgnoreRule() {
        return this.ignoreRule;
    }
    
    public String getRuleName() {
        return this.ruleName;
    }
    
    public boolean getTestLiterals() {
        return this.testLiterals;
    }
    
    public boolean isLexerAutoGenRule() {
        return this.ruleName.equals("nextToken");
    }
    
    public Lookahead look(final int n) {
        return this.grammar.theLLkAnalyzer.look(n, this);
    }
    
    public void prepareForAnalysis() {
        super.prepareForAnalysis();
        this.lock = new boolean[this.grammar.maxk + 1];
    }
    
    public void setDefaultErrorHandler(final boolean defaultErrorHandler) {
        this.defaultErrorHandler = defaultErrorHandler;
    }
    
    public void setEndElement(final RuleEndElement endNode) {
        this.endNode = endNode;
    }
    
    public void setOption(final Token token, final Token token2) {
        if (token.getText().equals("defaultErrorHandler")) {
            if (token2.getText().equals("true")) {
                this.defaultErrorHandler = true;
            }
            else if (token2.getText().equals("false")) {
                this.defaultErrorHandler = false;
            }
            else {
                this.grammar.antlrTool.error("Value for defaultErrorHandler must be true or false", this.grammar.getFilename(), token.getLine(), token.getColumn());
            }
        }
        else if (token.getText().equals("testLiterals")) {
            if (!(this.grammar instanceof LexerGrammar)) {
                this.grammar.antlrTool.error("testLiterals option only valid for lexer rules", this.grammar.getFilename(), token.getLine(), token.getColumn());
            }
            else if (token2.getText().equals("true")) {
                this.testLiterals = true;
            }
            else if (token2.getText().equals("false")) {
                this.testLiterals = false;
            }
            else {
                this.grammar.antlrTool.error("Value for testLiterals must be true or false", this.grammar.getFilename(), token.getLine(), token.getColumn());
            }
        }
        else if (token.getText().equals("ignore")) {
            if (!(this.grammar instanceof LexerGrammar)) {
                this.grammar.antlrTool.error("ignore option only valid for lexer rules", this.grammar.getFilename(), token.getLine(), token.getColumn());
            }
            else {
                this.ignoreRule = token2.getText();
            }
        }
        else if (token.getText().equals("paraphrase")) {
            if (!(this.grammar instanceof LexerGrammar)) {
                this.grammar.antlrTool.error("paraphrase option only valid for lexer rules", this.grammar.getFilename(), token.getLine(), token.getColumn());
            }
            else {
                final TokenSymbol tokenSymbol = this.grammar.tokenManager.getTokenSymbol(this.ruleName);
                if (tokenSymbol == null) {
                    this.grammar.antlrTool.panic("cannot find token associated with rule " + this.ruleName);
                }
                tokenSymbol.setParaphrase(token2.getText());
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
        else {
            this.grammar.antlrTool.error("Invalid rule option: " + token.getText(), this.grammar.getFilename(), token.getLine(), token.getColumn());
        }
    }
    
    public String toString() {
        String str = " FOLLOW={";
        final Lookahead[] cache = this.endNode.cache;
        final int maxk = this.grammar.maxk;
        boolean b = true;
        for (int i = 1; i <= maxk; ++i) {
            if (cache[i] != null) {
                str += cache[i].toString(",", this.grammar.tokenManager.getVocabulary());
                b = false;
                if (i < maxk && cache[i + 1] != null) {
                    str += ";";
                }
            }
        }
        String string = str + "}";
        if (b) {
            string = "";
        }
        return this.ruleName + ": " + super.toString() + " ;" + string;
    }
}
