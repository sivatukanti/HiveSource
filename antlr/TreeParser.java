// 
// Decompiled by Procyon v0.5.36
// 

package antlr;

import antlr.collections.impl.BitSet;
import antlr.collections.AST;

public class TreeParser
{
    public static ASTNULLType ASTNULL;
    protected AST _retTree;
    protected TreeParserSharedInputState inputState;
    protected String[] tokenNames;
    protected AST returnAST;
    protected ASTFactory astFactory;
    protected int traceDepth;
    
    public TreeParser() {
        this.astFactory = new ASTFactory();
        this.traceDepth = 0;
        this.inputState = new TreeParserSharedInputState();
    }
    
    public AST getAST() {
        return this.returnAST;
    }
    
    public ASTFactory getASTFactory() {
        return this.astFactory;
    }
    
    public String getTokenName(final int n) {
        return this.tokenNames[n];
    }
    
    public String[] getTokenNames() {
        return this.tokenNames;
    }
    
    protected void match(final AST ast, final int n) throws MismatchedTokenException {
        if (ast == null || ast == TreeParser.ASTNULL || ast.getType() != n) {
            throw new MismatchedTokenException(this.getTokenNames(), ast, n, false);
        }
    }
    
    public void match(final AST ast, final BitSet set) throws MismatchedTokenException {
        if (ast == null || ast == TreeParser.ASTNULL || !set.member(ast.getType())) {
            throw new MismatchedTokenException(this.getTokenNames(), ast, set, false);
        }
    }
    
    protected void matchNot(final AST ast, final int n) throws MismatchedTokenException {
        if (ast == null || ast == TreeParser.ASTNULL || ast.getType() == n) {
            throw new MismatchedTokenException(this.getTokenNames(), ast, n, true);
        }
    }
    
    public static void panic() {
        System.err.println("TreeWalker: panic");
        Utils.error("");
    }
    
    public void reportError(final RecognitionException ex) {
        System.err.println(ex.toString());
    }
    
    public void reportError(final String str) {
        System.err.println("error: " + str);
    }
    
    public void reportWarning(final String str) {
        System.err.println("warning: " + str);
    }
    
    public void setASTFactory(final ASTFactory astFactory) {
        this.astFactory = astFactory;
    }
    
    public void setASTNodeType(final String astNodeClass) {
        this.setASTNodeClass(astNodeClass);
    }
    
    public void setASTNodeClass(final String astNodeType) {
        this.astFactory.setASTNodeType(astNodeType);
    }
    
    public void traceIndent() {
        for (int i = 0; i < this.traceDepth; ++i) {
            System.out.print(" ");
        }
    }
    
    public void traceIn(final String str, final AST ast) {
        ++this.traceDepth;
        this.traceIndent();
        System.out.println("> " + str + "(" + ((ast != null) ? ast.toString() : "null") + ")" + ((this.inputState.guessing > 0) ? " [guessing]" : ""));
    }
    
    public void traceOut(final String str, final AST ast) {
        this.traceIndent();
        System.out.println("< " + str + "(" + ((ast != null) ? ast.toString() : "null") + ")" + ((this.inputState.guessing > 0) ? " [guessing]" : ""));
        --this.traceDepth;
    }
    
    static {
        TreeParser.ASTNULL = new ASTNULLType();
    }
}
