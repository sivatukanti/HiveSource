// 
// Decompiled by Procyon v0.5.36
// 

package antlr.debug;

import antlr.RecognitionException;
import antlr.collections.impl.BitSet;
import antlr.MismatchedCharException;
import antlr.Token;
import antlr.CharStreamException;
import antlr.LexerSharedInputState;
import antlr.InputBuffer;
import antlr.CharScanner;

public abstract class DebuggingCharScanner extends CharScanner implements DebuggingParser
{
    private ParserEventSupport parserEventSupport;
    private boolean _notDebugMode;
    protected String[] ruleNames;
    protected String[] semPredNames;
    
    public DebuggingCharScanner(final InputBuffer inputBuffer) {
        super(inputBuffer);
        this.parserEventSupport = new ParserEventSupport(this);
        this._notDebugMode = false;
    }
    
    public DebuggingCharScanner(final LexerSharedInputState lexerSharedInputState) {
        super(lexerSharedInputState);
        this.parserEventSupport = new ParserEventSupport(this);
        this._notDebugMode = false;
    }
    
    public void addMessageListener(final MessageListener messageListener) {
        this.parserEventSupport.addMessageListener(messageListener);
    }
    
    public void addNewLineListener(final NewLineListener newLineListener) {
        this.parserEventSupport.addNewLineListener(newLineListener);
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
    
    public void consume() throws CharStreamException {
        int la = -99;
        try {
            la = this.LA(1);
        }
        catch (CharStreamException ex) {}
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
    
    public char LA(final int n) throws CharStreamException {
        final char la = super.LA(n);
        this.parserEventSupport.fireLA(n, la);
        return la;
    }
    
    protected Token makeToken(final int n) {
        return super.makeToken(n);
    }
    
    public void match(final char c) throws MismatchedCharException, CharStreamException {
        final char la = this.LA(1);
        try {
            super.match(c);
            this.parserEventSupport.fireMatch(c, this.inputState.guessing);
        }
        catch (MismatchedCharException ex) {
            if (this.inputState.guessing == 0) {
                this.parserEventSupport.fireMismatch(la, c, this.inputState.guessing);
            }
            throw ex;
        }
    }
    
    public void match(final BitSet set) throws MismatchedCharException, CharStreamException {
        final String string = this.text.toString();
        final char la = this.LA(1);
        try {
            super.match(set);
            this.parserEventSupport.fireMatch(la, set, string, this.inputState.guessing);
        }
        catch (MismatchedCharException ex) {
            if (this.inputState.guessing == 0) {
                this.parserEventSupport.fireMismatch(la, set, string, this.inputState.guessing);
            }
            throw ex;
        }
    }
    
    public void match(final String s) throws MismatchedCharException, CharStreamException {
        final StringBuffer sb = new StringBuffer("");
        final int length = s.length();
        try {
            for (int i = 1; i <= length; ++i) {
                sb.append(super.LA(i));
            }
        }
        catch (Exception ex2) {}
        try {
            super.match(s);
            this.parserEventSupport.fireMatch(s, this.inputState.guessing);
        }
        catch (MismatchedCharException ex) {
            if (this.inputState.guessing == 0) {
                this.parserEventSupport.fireMismatch(sb.toString(), s, this.inputState.guessing);
            }
            throw ex;
        }
    }
    
    public void matchNot(final char c) throws MismatchedCharException, CharStreamException {
        final char la = this.LA(1);
        try {
            super.matchNot(c);
            this.parserEventSupport.fireMatchNot(la, c, this.inputState.guessing);
        }
        catch (MismatchedCharException ex) {
            if (this.inputState.guessing == 0) {
                this.parserEventSupport.fireMismatchNot(la, c, this.inputState.guessing);
            }
            throw ex;
        }
    }
    
    public void matchRange(final char c, final char c2) throws MismatchedCharException, CharStreamException {
        final char la = this.LA(1);
        try {
            super.matchRange(c, c2);
            this.parserEventSupport.fireMatch(la, "" + c + c2, this.inputState.guessing);
        }
        catch (MismatchedCharException ex) {
            if (this.inputState.guessing == 0) {
                this.parserEventSupport.fireMismatch(la, "" + c + c2, this.inputState.guessing);
            }
            throw ex;
        }
    }
    
    public void newline() {
        super.newline();
        this.parserEventSupport.fireNewLine(this.getLine());
    }
    
    public void removeMessageListener(final MessageListener messageListener) {
        this.parserEventSupport.removeMessageListener(messageListener);
    }
    
    public void removeNewLineListener(final NewLineListener newLineListener) {
        this.parserEventSupport.removeNewLineListener(newLineListener);
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
    
    public void reportError(final MismatchedCharException ex) {
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
    
    public void setupDebugging() {
    }
    
    public synchronized void wakeUp() {
        this.notify();
    }
}
