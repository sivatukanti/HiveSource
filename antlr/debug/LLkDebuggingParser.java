// 
// Decompiled by Procyon v0.5.36
// 

package antlr.debug;

import antlr.Utils;
import antlr.RecognitionException;
import antlr.collections.impl.BitSet;
import antlr.MismatchedTokenException;
import antlr.TokenStreamException;
import antlr.TokenStream;
import antlr.TokenBuffer;
import antlr.ParserSharedInputState;
import antlr.LLkParser;

public class LLkDebuggingParser extends LLkParser implements DebuggingParser
{
    protected ParserEventSupport parserEventSupport;
    private boolean _notDebugMode;
    protected String[] ruleNames;
    protected String[] semPredNames;
    
    public LLkDebuggingParser(final int n) {
        super(n);
        this.parserEventSupport = new ParserEventSupport(this);
        this._notDebugMode = false;
    }
    
    public LLkDebuggingParser(final ParserSharedInputState parserSharedInputState, final int n) {
        super(parserSharedInputState, n);
        this.parserEventSupport = new ParserEventSupport(this);
        this._notDebugMode = false;
    }
    
    public LLkDebuggingParser(final TokenBuffer tokenBuffer, final int n) {
        super(tokenBuffer, n);
        this.parserEventSupport = new ParserEventSupport(this);
        this._notDebugMode = false;
    }
    
    public LLkDebuggingParser(final TokenStream tokenStream, final int n) {
        super(tokenStream, n);
        this.parserEventSupport = new ParserEventSupport(this);
        this._notDebugMode = false;
    }
    
    public void addMessageListener(final MessageListener messageListener) {
        this.parserEventSupport.addMessageListener(messageListener);
    }
    
    public void addParserListener(final ParserListener parserListener) {
        this.parserEventSupport.addParserListener(parserListener);
    }
    
    public void addParserMatchListener(final ParserMatchListener parserMatchListener) {
        this.parserEventSupport.addParserMatchListener(parserMatchListener);
    }
    
    public void addParserTokenListener(final ParserTokenListener parserTokenListener) {
        this.parserEventSupport.addParserTokenListener(parserTokenListener);
    }
    
    public void addSemanticPredicateListener(final SemanticPredicateListener semanticPredicateListener) {
        this.parserEventSupport.addSemanticPredicateListener(semanticPredicateListener);
    }
    
    public void addSyntacticPredicateListener(final SyntacticPredicateListener syntacticPredicateListener) {
        this.parserEventSupport.addSyntacticPredicateListener(syntacticPredicateListener);
    }
    
    public void addTraceListener(final TraceListener traceListener) {
        this.parserEventSupport.addTraceListener(traceListener);
    }
    
    public void consume() throws TokenStreamException {
        final int la = this.LA(1);
        super.consume();
        this.parserEventSupport.fireConsume(la);
    }
    
    protected void fireEnterRule(final int n, final int n2) {
        if (this.isDebugMode()) {
            this.parserEventSupport.fireEnterRule(n, this.inputState.guessing, n2);
        }
    }
    
    protected void fireExitRule(final int n, final int n2) {
        if (this.isDebugMode()) {
            this.parserEventSupport.fireExitRule(n, this.inputState.guessing, n2);
        }
    }
    
    protected boolean fireSemanticPredicateEvaluated(final int n, final int n2, final boolean b) {
        if (this.isDebugMode()) {
            return this.parserEventSupport.fireSemanticPredicateEvaluated(n, n2, b, this.inputState.guessing);
        }
        return b;
    }
    
    protected void fireSyntacticPredicateFailed() {
        if (this.isDebugMode()) {
            this.parserEventSupport.fireSyntacticPredicateFailed(this.inputState.guessing);
        }
    }
    
    protected void fireSyntacticPredicateStarted() {
        if (this.isDebugMode()) {
            this.parserEventSupport.fireSyntacticPredicateStarted(this.inputState.guessing);
        }
    }
    
    protected void fireSyntacticPredicateSucceeded() {
        if (this.isDebugMode()) {
            this.parserEventSupport.fireSyntacticPredicateSucceeded(this.inputState.guessing);
        }
    }
    
    public String getRuleName(final int n) {
        return this.ruleNames[n];
    }
    
    public String getSemPredName(final int n) {
        return this.semPredNames[n];
    }
    
    public synchronized void goToSleep() {
        try {
            this.wait();
        }
        catch (InterruptedException ex) {}
    }
    
    public boolean isDebugMode() {
        return !this._notDebugMode;
    }
    
    public boolean isGuessing() {
        return this.inputState.guessing > 0;
    }
    
    public int LA(final int n) throws TokenStreamException {
        final int la = super.LA(n);
        this.parserEventSupport.fireLA(n, la);
        return la;
    }
    
    public void match(final int n) throws MismatchedTokenException, TokenStreamException {
        final String text = this.LT(1).getText();
        final int la = this.LA(1);
        try {
            super.match(n);
            this.parserEventSupport.fireMatch(n, text, this.inputState.guessing);
        }
        catch (MismatchedTokenException ex) {
            if (this.inputState.guessing == 0) {
                this.parserEventSupport.fireMismatch(la, n, text, this.inputState.guessing);
            }
            throw ex;
        }
    }
    
    public void match(final BitSet set) throws MismatchedTokenException, TokenStreamException {
        final String text = this.LT(1).getText();
        final int la = this.LA(1);
        try {
            super.match(set);
            this.parserEventSupport.fireMatch(la, set, text, this.inputState.guessing);
        }
        catch (MismatchedTokenException ex) {
            if (this.inputState.guessing == 0) {
                this.parserEventSupport.fireMismatch(la, set, text, this.inputState.guessing);
            }
            throw ex;
        }
    }
    
    public void matchNot(final int n) throws MismatchedTokenException, TokenStreamException {
        final String text = this.LT(1).getText();
        final int la = this.LA(1);
        try {
            super.matchNot(n);
            this.parserEventSupport.fireMatchNot(la, n, text, this.inputState.guessing);
        }
        catch (MismatchedTokenException ex) {
            if (this.inputState.guessing == 0) {
                this.parserEventSupport.fireMismatchNot(la, n, text, this.inputState.guessing);
            }
            throw ex;
        }
    }
    
    public void removeMessageListener(final MessageListener messageListener) {
        this.parserEventSupport.removeMessageListener(messageListener);
    }
    
    public void removeParserListener(final ParserListener parserListener) {
        this.parserEventSupport.removeParserListener(parserListener);
    }
    
    public void removeParserMatchListener(final ParserMatchListener parserMatchListener) {
        this.parserEventSupport.removeParserMatchListener(parserMatchListener);
    }
    
    public void removeParserTokenListener(final ParserTokenListener parserTokenListener) {
        this.parserEventSupport.removeParserTokenListener(parserTokenListener);
    }
    
    public void removeSemanticPredicateListener(final SemanticPredicateListener semanticPredicateListener) {
        this.parserEventSupport.removeSemanticPredicateListener(semanticPredicateListener);
    }
    
    public void removeSyntacticPredicateListener(final SyntacticPredicateListener syntacticPredicateListener) {
        this.parserEventSupport.removeSyntacticPredicateListener(syntacticPredicateListener);
    }
    
    public void removeTraceListener(final TraceListener traceListener) {
        this.parserEventSupport.removeTraceListener(traceListener);
    }
    
    public void reportError(final RecognitionException ex) {
        this.parserEventSupport.fireReportError(ex);
        super.reportError(ex);
    }
    
    public void reportError(final String s) {
        this.parserEventSupport.fireReportError(s);
        super.reportError(s);
    }
    
    public void reportWarning(final String s) {
        this.parserEventSupport.fireReportWarning(s);
        super.reportWarning(s);
    }
    
    public void setDebugMode(final boolean b) {
        this._notDebugMode = !b;
    }
    
    public void setupDebugging(final TokenBuffer tokenBuffer) {
        this.setupDebugging(null, tokenBuffer);
    }
    
    public void setupDebugging(final TokenStream tokenStream) {
        this.setupDebugging(tokenStream, null);
    }
    
    protected void setupDebugging(final TokenStream tokenStream, final TokenBuffer tokenBuffer) {
        this.setDebugMode(true);
        try {
            try {
                Utils.loadClass("javax.swing.JButton");
            }
            catch (ClassNotFoundException ex) {
                System.err.println("Swing is required to use ParseView, but is not present in your CLASSPATH");
                System.exit(1);
            }
            Utils.loadClass("antlr.parseview.ParseView").getConstructor(LLkDebuggingParser.class, TokenStream.class, TokenBuffer.class).newInstance(this, tokenStream, tokenBuffer);
        }
        catch (Exception obj) {
            System.err.println("Error initializing ParseView: " + obj);
            System.err.println("Please report this to Scott Stanchfield, thetick@magelang.com");
            System.exit(1);
        }
    }
    
    public synchronized void wakeUp() {
        this.notify();
    }
}
