// 
// Decompiled by Procyon v0.5.36
// 

package antlr.debug;

import antlr.collections.AST;
import antlr.Token;
import antlr.ParseTreeToken;
import antlr.CommonToken;
import antlr.collections.impl.BitSet;
import antlr.TokenStreamException;
import antlr.MismatchedTokenException;
import antlr.ParseTree;
import antlr.TokenStream;
import antlr.TokenBuffer;
import antlr.ParserSharedInputState;
import antlr.ParseTreeRule;
import java.util.Stack;
import antlr.LLkParser;

public class ParseTreeDebugParser extends LLkParser
{
    protected Stack currentParseTreeRoot;
    protected ParseTreeRule mostRecentParseTreeRoot;
    protected int numberOfDerivationSteps;
    
    public ParseTreeDebugParser(final int n) {
        super(n);
        this.currentParseTreeRoot = new Stack();
        this.mostRecentParseTreeRoot = null;
        this.numberOfDerivationSteps = 1;
    }
    
    public ParseTreeDebugParser(final ParserSharedInputState parserSharedInputState, final int n) {
        super(parserSharedInputState, n);
        this.currentParseTreeRoot = new Stack();
        this.mostRecentParseTreeRoot = null;
        this.numberOfDerivationSteps = 1;
    }
    
    public ParseTreeDebugParser(final TokenBuffer tokenBuffer, final int n) {
        super(tokenBuffer, n);
        this.currentParseTreeRoot = new Stack();
        this.mostRecentParseTreeRoot = null;
        this.numberOfDerivationSteps = 1;
    }
    
    public ParseTreeDebugParser(final TokenStream tokenStream, final int n) {
        super(tokenStream, n);
        this.currentParseTreeRoot = new Stack();
        this.mostRecentParseTreeRoot = null;
        this.numberOfDerivationSteps = 1;
    }
    
    public ParseTree getParseTree() {
        return this.mostRecentParseTreeRoot;
    }
    
    public int getNumberOfDerivationSteps() {
        return this.numberOfDerivationSteps;
    }
    
    public void match(final int n) throws MismatchedTokenException, TokenStreamException {
        this.addCurrentTokenToParseTree();
        super.match(n);
    }
    
    public void match(final BitSet set) throws MismatchedTokenException, TokenStreamException {
        this.addCurrentTokenToParseTree();
        super.match(set);
    }
    
    public void matchNot(final int n) throws MismatchedTokenException, TokenStreamException {
        this.addCurrentTokenToParseTree();
        super.matchNot(n);
    }
    
    protected void addCurrentTokenToParseTree() throws TokenStreamException {
        if (this.inputState.guessing > 0) {
            return;
        }
        final ParseTreeRule parseTreeRule = this.currentParseTreeRoot.peek();
        ParseTreeToken parseTreeToken;
        if (this.LA(1) == 1) {
            parseTreeToken = new ParseTreeToken(new CommonToken("EOF"));
        }
        else {
            parseTreeToken = new ParseTreeToken(this.LT(1));
        }
        parseTreeRule.addChild(parseTreeToken);
    }
    
    public void traceIn(final String s) throws TokenStreamException {
        if (this.inputState.guessing > 0) {
            return;
        }
        final ParseTreeRule item = new ParseTreeRule(s);
        if (this.currentParseTreeRoot.size() > 0) {
            this.currentParseTreeRoot.peek().addChild(item);
        }
        this.currentParseTreeRoot.push(item);
        ++this.numberOfDerivationSteps;
    }
    
    public void traceOut(final String s) throws TokenStreamException {
        if (this.inputState.guessing > 0) {
            return;
        }
        this.mostRecentParseTreeRoot = this.currentParseTreeRoot.pop();
    }
}
