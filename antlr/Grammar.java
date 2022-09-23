// 
// Decompiled by Procyon v0.5.36
// 

package antlr;

import java.util.Enumeration;
import java.io.IOException;
import antlr.collections.impl.Vector;
import java.util.Hashtable;

public abstract class Grammar
{
    protected Tool antlrTool;
    protected CodeGenerator generator;
    protected LLkGrammarAnalyzer theLLkAnalyzer;
    protected Hashtable symbols;
    protected boolean buildAST;
    protected boolean analyzerDebug;
    protected boolean interactive;
    protected String superClass;
    protected TokenManager tokenManager;
    protected String exportVocab;
    protected String importVocab;
    protected Hashtable options;
    protected Vector rules;
    protected Token preambleAction;
    protected String className;
    protected String fileName;
    protected Token classMemberAction;
    protected boolean hasSyntacticPredicate;
    protected boolean hasUserErrorHandling;
    protected int maxk;
    protected boolean traceRules;
    protected boolean debuggingOutput;
    protected boolean defaultErrorHandler;
    protected String comment;
    
    public Grammar(final String className, final Tool antlrTool, final String superClass) {
        this.buildAST = false;
        this.analyzerDebug = false;
        this.interactive = false;
        this.superClass = null;
        this.exportVocab = null;
        this.importVocab = null;
        this.preambleAction = new CommonToken(0, "");
        this.className = null;
        this.fileName = null;
        this.classMemberAction = new CommonToken(0, "");
        this.hasSyntacticPredicate = false;
        this.hasUserErrorHandling = false;
        this.maxk = 1;
        this.traceRules = false;
        this.debuggingOutput = false;
        this.defaultErrorHandler = true;
        this.comment = null;
        this.className = className;
        this.antlrTool = antlrTool;
        this.symbols = new Hashtable();
        this.options = new Hashtable();
        this.rules = new Vector(100);
        this.superClass = superClass;
    }
    
    public void define(final RuleSymbol value) {
        this.rules.appendElement(value);
        this.symbols.put(value.getId(), value);
    }
    
    public abstract void generate() throws IOException;
    
    protected String getClassName() {
        return this.className;
    }
    
    public boolean getDefaultErrorHandler() {
        return this.defaultErrorHandler;
    }
    
    public String getFilename() {
        return this.fileName;
    }
    
    public int getIntegerOption(final String key) throws NumberFormatException {
        final Token token = this.options.get(key);
        if (token == null || token.getType() != 20) {
            throw new NumberFormatException();
        }
        return Integer.parseInt(token.getText());
    }
    
    public Token getOption(final String key) {
        return this.options.get(key);
    }
    
    protected abstract String getSuperClass();
    
    public GrammarSymbol getSymbol(final String key) {
        return this.symbols.get(key);
    }
    
    public Enumeration getSymbols() {
        return this.symbols.elements();
    }
    
    public boolean hasOption(final String key) {
        return this.options.containsKey(key);
    }
    
    public boolean isDefined(final String key) {
        return this.symbols.containsKey(key);
    }
    
    public abstract void processArguments(final String[] p0);
    
    public void setCodeGenerator(final CodeGenerator generator) {
        this.generator = generator;
    }
    
    public void setFilename(final String fileName) {
        this.fileName = fileName;
    }
    
    public void setGrammarAnalyzer(final LLkGrammarAnalyzer theLLkAnalyzer) {
        this.theLLkAnalyzer = theLLkAnalyzer;
    }
    
    public boolean setOption(final String key, final Token value) {
        this.options.put(key, value);
        final String text = value.getText();
        if (key.equals("k")) {
            try {
                this.maxk = this.getIntegerOption("k");
                if (this.maxk <= 0) {
                    this.antlrTool.error("option 'k' must be greater than 0 (was " + value.getText() + ")", this.getFilename(), value.getLine(), value.getColumn());
                    this.maxk = 1;
                }
            }
            catch (NumberFormatException ex) {
                this.antlrTool.error("option 'k' must be an integer (was " + value.getText() + ")", this.getFilename(), value.getLine(), value.getColumn());
            }
            return true;
        }
        if (key.equals("codeGenMakeSwitchThreshold")) {
            try {
                this.getIntegerOption("codeGenMakeSwitchThreshold");
            }
            catch (NumberFormatException ex2) {
                this.antlrTool.error("option 'codeGenMakeSwitchThreshold' must be an integer", this.getFilename(), value.getLine(), value.getColumn());
            }
            return true;
        }
        if (key.equals("codeGenBitsetTestThreshold")) {
            try {
                this.getIntegerOption("codeGenBitsetTestThreshold");
            }
            catch (NumberFormatException ex3) {
                this.antlrTool.error("option 'codeGenBitsetTestThreshold' must be an integer", this.getFilename(), value.getLine(), value.getColumn());
            }
            return true;
        }
        if (key.equals("defaultErrorHandler")) {
            if (text.equals("true")) {
                this.defaultErrorHandler = true;
            }
            else if (text.equals("false")) {
                this.defaultErrorHandler = false;
            }
            else {
                this.antlrTool.error("Value for defaultErrorHandler must be true or false", this.getFilename(), value.getLine(), value.getColumn());
            }
            return true;
        }
        if (key.equals("analyzerDebug")) {
            if (text.equals("true")) {
                this.analyzerDebug = true;
            }
            else if (text.equals("false")) {
                this.analyzerDebug = false;
            }
            else {
                this.antlrTool.error("option 'analyzerDebug' must be true or false", this.getFilename(), value.getLine(), value.getColumn());
            }
            return true;
        }
        if (key.equals("codeGenDebug")) {
            if (text.equals("true")) {
                this.analyzerDebug = true;
            }
            else if (text.equals("false")) {
                this.analyzerDebug = false;
            }
            else {
                this.antlrTool.error("option 'codeGenDebug' must be true or false", this.getFilename(), value.getLine(), value.getColumn());
            }
            return true;
        }
        return key.equals("classHeaderSuffix") || key.equals("classHeaderPrefix") || key.equals("namespaceAntlr") || key.equals("namespaceStd") || key.equals("genHashLines") || key.equals("noConstructors");
    }
    
    public void setTokenManager(final TokenManager tokenManager) {
        this.tokenManager = tokenManager;
    }
    
    public String toString() {
        final StringBuffer sb = new StringBuffer(20000);
        final Enumeration elements = this.rules.elements();
        while (elements.hasMoreElements()) {
            final RuleSymbol ruleSymbol = elements.nextElement();
            if (!ruleSymbol.id.equals("mnextToken")) {
                sb.append(ruleSymbol.getBlock().toString());
                sb.append("\n\n");
            }
        }
        return sb.toString();
    }
}
