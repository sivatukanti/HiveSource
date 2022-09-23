// 
// Decompiled by Procyon v0.5.36
// 

package antlr;

import antlr.collections.impl.BitSet;
import antlr.debug.TraceListener;
import antlr.debug.SyntacticPredicateListener;
import antlr.debug.SemanticPredicateListener;
import antlr.debug.ParserTokenListener;
import antlr.debug.ParserMatchListener;
import antlr.debug.ParserListener;
import antlr.debug.MessageListener;
import java.util.Hashtable;
import antlr.collections.AST;

public abstract class Parser
{
    protected ParserSharedInputState inputState;
    protected String[] tokenNames;
    protected AST returnAST;
    protected ASTFactory astFactory;
    protected Hashtable tokenTypeToASTClassMap;
    private boolean ignoreInvalidDebugCalls;
    protected int traceDepth;
    
    public Parser() {
        this(new ParserSharedInputState());
    }
    
    public Parser(final ParserSharedInputState inputState) {
        this.astFactory = null;
        this.tokenTypeToASTClassMap = null;
        this.ignoreInvalidDebugCalls = false;
        this.traceDepth = 0;
        this.inputState = inputState;
    }
    
    public Hashtable getTokenTypeToASTClassMap() {
        return this.tokenTypeToASTClassMap;
    }
    
    public void addMessageListener(final MessageListener messageListener) {
        if (!this.ignoreInvalidDebugCalls) {
            throw new IllegalArgumentException("addMessageListener() is only valid if parser built for debugging");
        }
    }
    
    public void addParserListener(final ParserListener parserListener) {
        if (!this.ignoreInvalidDebugCalls) {
            throw new IllegalArgumentException("addParserListener() is only valid if parser built for debugging");
        }
    }
    
    public void addParserMatchListener(final ParserMatchListener parserMatchListener) {
        if (!this.ignoreInvalidDebugCalls) {
            throw new IllegalArgumentException("addParserMatchListener() is only valid if parser built for debugging");
        }
    }
    
    public void addParserTokenListener(final ParserTokenListener parserTokenListener) {
        if (!this.ignoreInvalidDebugCalls) {
            throw new IllegalArgumentException("addParserTokenListener() is only valid if parser built for debugging");
        }
    }
    
    public void addSemanticPredicateListener(final SemanticPredicateListener semanticPredicateListener) {
        if (!this.ignoreInvalidDebugCalls) {
            throw new IllegalArgumentException("addSemanticPredicateListener() is only valid if parser built for debugging");
        }
    }
    
    public void addSyntacticPredicateListener(final SyntacticPredicateListener syntacticPredicateListener) {
        if (!this.ignoreInvalidDebugCalls) {
            throw new IllegalArgumentException("addSyntacticPredicateListener() is only valid if parser built for debugging");
        }
    }
    
    public void addTraceListener(final TraceListener traceListener) {
        if (!this.ignoreInvalidDebugCalls) {
            throw new IllegalArgumentException("addTraceListener() is only valid if parser built for debugging");
        }
    }
    
    public abstract void consume() throws TokenStreamException;
    
    public void consumeUntil(final int n) throws TokenStreamException {
        while (this.LA(1) != 1 && this.LA(1) != n) {
            this.consume();
        }
    }
    
    public void consumeUntil(final BitSet set) throws TokenStreamException {
        while (this.LA(1) != 1 && !set.member(this.LA(1))) {
            this.consume();
        }
    }
    
    protected void defaultDebuggingSetup(final TokenStream tokenStream, final TokenBuffer tokenBuffer) {
    }
    
    public AST getAST() {
        return this.returnAST;
    }
    
    public ASTFactory getASTFactory() {
        return this.astFactory;
    }
    
    public String getFilename() {
        return this.inputState.filename;
    }
    
    public ParserSharedInputState getInputState() {
        return this.inputState;
    }
    
    public void setInputState(final ParserSharedInputState inputState) {
        this.inputState = inputState;
    }
    
    public String getTokenName(final int n) {
        return this.tokenNames[n];
    }
    
    public String[] getTokenNames() {
        return this.tokenNames;
    }
    
    public boolean isDebugMode() {
        return false;
    }
    
    public abstract int LA(final int p0) throws TokenStreamException;
    
    public abstract Token LT(final int p0) throws TokenStreamException;
    
    public int mark() {
        return this.inputState.input.mark();
    }
    
    public void match(final int n) throws MismatchedTokenException, TokenStreamException {
        if (this.LA(1) != n) {
            throw new MismatchedTokenException(this.tokenNames, this.LT(1), n, false, this.getFilename());
        }
        this.consume();
    }
    
    public void match(final BitSet set) throws MismatchedTokenException, TokenStreamException {
        if (!set.member(this.LA(1))) {
            throw new MismatchedTokenException(this.tokenNames, this.LT(1), set, false, this.getFilename());
        }
        this.consume();
    }
    
    public void matchNot(final int n) throws MismatchedTokenException, TokenStreamException {
        if (this.LA(1) == n) {
            throw new MismatchedTokenException(this.tokenNames, this.LT(1), n, true, this.getFilename());
        }
        this.consume();
    }
    
    public static void panic() {
        System.err.println("Parser: panic");
        System.exit(1);
    }
    
    public void removeMessageListener(final MessageListener messageListener) {
        if (!this.ignoreInvalidDebugCalls) {
            throw new RuntimeException("removeMessageListener() is only valid if parser built for debugging");
        }
    }
    
    public void removeParserListener(final ParserListener parserListener) {
        if (!this.ignoreInvalidDebugCalls) {
            throw new RuntimeException("removeParserListener() is only valid if parser built for debugging");
        }
    }
    
    public void removeParserMatchListener(final ParserMatchListener parserMatchListener) {
        if (!this.ignoreInvalidDebugCalls) {
            throw new RuntimeException("removeParserMatchListener() is only valid if parser built for debugging");
        }
    }
    
    public void removeParserTokenListener(final ParserTokenListener parserTokenListener) {
        if (!this.ignoreInvalidDebugCalls) {
            throw new RuntimeException("removeParserTokenListener() is only valid if parser built for debugging");
        }
    }
    
    public void removeSemanticPredicateListener(final SemanticPredicateListener semanticPredicateListener) {
        if (!this.ignoreInvalidDebugCalls) {
            throw new IllegalArgumentException("removeSemanticPredicateListener() is only valid if parser built for debugging");
        }
    }
    
    public void removeSyntacticPredicateListener(final SyntacticPredicateListener syntacticPredicateListener) {
        if (!this.ignoreInvalidDebugCalls) {
            throw new IllegalArgumentException("removeSyntacticPredicateListener() is only valid if parser built for debugging");
        }
    }
    
    public void removeTraceListener(final TraceListener traceListener) {
        if (!this.ignoreInvalidDebugCalls) {
            throw new RuntimeException("removeTraceListener() is only valid if parser built for debugging");
        }
    }
    
    public void reportError(final RecognitionException x) {
        System.err.println(x);
    }
    
    public void reportError(final String s) {
        if (this.getFilename() == null) {
            System.err.println("error: " + s);
        }
        else {
            System.err.println(this.getFilename() + ": error: " + s);
        }
    }
    
    public void reportWarning(final String s) {
        if (this.getFilename() == null) {
            System.err.println("warning: " + s);
        }
        else {
            System.err.println(this.getFilename() + ": warning: " + s);
        }
    }
    
    public void recover(final RecognitionException ex, final BitSet set) throws TokenStreamException {
        this.consume();
        this.consumeUntil(set);
    }
    
    public void rewind(final int n) {
        this.inputState.input.rewind(n);
    }
    
    public void setASTFactory(final ASTFactory astFactory) {
        this.astFactory = astFactory;
    }
    
    public void setASTNodeClass(final String astNodeType) {
        this.astFactory.setASTNodeType(astNodeType);
    }
    
    public void setASTNodeType(final String astNodeClass) {
        this.setASTNodeClass(astNodeClass);
    }
    
    public void setDebugMode(final boolean b) {
        if (!this.ignoreInvalidDebugCalls) {
            throw new RuntimeException("setDebugMode() only valid if parser built for debugging");
        }
    }
    
    public void setFilename(final String filename) {
        this.inputState.filename = filename;
    }
    
    public void setIgnoreInvalidDebugCalls(final boolean ignoreInvalidDebugCalls) {
        this.ignoreInvalidDebugCalls = ignoreInvalidDebugCalls;
    }
    
    public void setTokenBuffer(final TokenBuffer input) {
        this.inputState.input = input;
    }
    
    public void traceIndent() {
        for (int i = 0; i < this.traceDepth; ++i) {
            System.out.print(" ");
        }
    }
    
    public void traceIn(final String str) throws TokenStreamException {
        ++this.traceDepth;
        this.traceIndent();
        System.out.println("> " + str + "; LA(1)==" + this.LT(1).getText() + ((this.inputState.guessing > 0) ? " [guessing]" : ""));
    }
    
    public void traceOut(final String str) throws TokenStreamException {
        this.traceIndent();
        System.out.println("< " + str + "; LA(1)==" + this.LT(1).getText() + ((this.inputState.guessing > 0) ? " [guessing]" : ""));
        --this.traceDepth;
    }
}
