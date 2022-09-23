// 
// Decompiled by Procyon v0.5.36
// 

package org.antlr.runtime;

import java.util.Map;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.List;

public abstract class BaseRecognizer
{
    public static final int MEMO_RULE_FAILED = -2;
    public static final int MEMO_RULE_UNKNOWN = -1;
    public static final int INITIAL_FOLLOW_STACK_SIZE = 100;
    public static final int DEFAULT_TOKEN_CHANNEL = 0;
    public static final int HIDDEN = 99;
    public static final String NEXT_TOKEN_RULE_NAME = "nextToken";
    protected RecognizerSharedState state;
    
    public BaseRecognizer() {
        this.state = new RecognizerSharedState();
    }
    
    public BaseRecognizer(RecognizerSharedState state) {
        if (state == null) {
            state = new RecognizerSharedState();
        }
        this.state = state;
    }
    
    public void reset() {
        if (this.state == null) {
            return;
        }
        this.state._fsp = -1;
        this.state.errorRecovery = false;
        this.state.lastErrorIndex = -1;
        this.state.failed = false;
        this.state.syntaxErrors = 0;
        this.state.backtracking = 0;
        for (int i = 0; this.state.ruleMemo != null && i < this.state.ruleMemo.length; ++i) {
            this.state.ruleMemo[i] = null;
        }
    }
    
    public Object match(final IntStream input, final int ttype, final BitSet follow) throws RecognitionException {
        Object matchedSymbol = this.getCurrentInputSymbol(input);
        if (input.LA(1) == ttype) {
            input.consume();
            this.state.errorRecovery = false;
            this.state.failed = false;
            return matchedSymbol;
        }
        if (this.state.backtracking > 0) {
            this.state.failed = true;
            return matchedSymbol;
        }
        matchedSymbol = this.recoverFromMismatchedToken(input, ttype, follow);
        return matchedSymbol;
    }
    
    public void matchAny(final IntStream input) {
        this.state.errorRecovery = false;
        this.state.failed = false;
        input.consume();
    }
    
    public boolean mismatchIsUnwantedToken(final IntStream input, final int ttype) {
        return input.LA(2) == ttype;
    }
    
    public boolean mismatchIsMissingToken(final IntStream input, BitSet follow) {
        if (follow == null) {
            return false;
        }
        if (follow.member(1)) {
            final BitSet viableTokensFollowingThisRule = this.computeContextSensitiveRuleFOLLOW();
            follow = follow.or(viableTokensFollowingThisRule);
            if (this.state._fsp >= 0) {
                follow.remove(1);
            }
        }
        return follow.member(input.LA(1)) || follow.member(1);
    }
    
    public void reportError(final RecognitionException e) {
        if (this.state.errorRecovery) {
            return;
        }
        final RecognizerSharedState state = this.state;
        ++state.syntaxErrors;
        this.state.errorRecovery = true;
        this.displayRecognitionError(this.getTokenNames(), e);
    }
    
    public void displayRecognitionError(final String[] tokenNames, final RecognitionException e) {
        final String hdr = this.getErrorHeader(e);
        final String msg = this.getErrorMessage(e, tokenNames);
        this.emitErrorMessage(hdr + " " + msg);
    }
    
    public String getErrorMessage(final RecognitionException e, final String[] tokenNames) {
        String msg = e.getMessage();
        if (e instanceof UnwantedTokenException) {
            final UnwantedTokenException ute = (UnwantedTokenException)e;
            String tokenName = "<unknown>";
            if (ute.expecting == -1) {
                tokenName = "EOF";
            }
            else {
                tokenName = tokenNames[ute.expecting];
            }
            msg = "extraneous input " + this.getTokenErrorDisplay(ute.getUnexpectedToken()) + " expecting " + tokenName;
        }
        else if (e instanceof MissingTokenException) {
            final MissingTokenException mte = (MissingTokenException)e;
            String tokenName = "<unknown>";
            if (mte.expecting == -1) {
                tokenName = "EOF";
            }
            else {
                tokenName = tokenNames[mte.expecting];
            }
            msg = "missing " + tokenName + " at " + this.getTokenErrorDisplay(e.token);
        }
        else if (e instanceof MismatchedTokenException) {
            final MismatchedTokenException mte2 = (MismatchedTokenException)e;
            String tokenName = "<unknown>";
            if (mte2.expecting == -1) {
                tokenName = "EOF";
            }
            else {
                tokenName = tokenNames[mte2.expecting];
            }
            msg = "mismatched input " + this.getTokenErrorDisplay(e.token) + " expecting " + tokenName;
        }
        else if (e instanceof MismatchedTreeNodeException) {
            final MismatchedTreeNodeException mtne = (MismatchedTreeNodeException)e;
            String tokenName = "<unknown>";
            if (mtne.expecting == -1) {
                tokenName = "EOF";
            }
            else {
                tokenName = tokenNames[mtne.expecting];
            }
            msg = "mismatched tree node: " + mtne.node + " expecting " + tokenName;
        }
        else if (e instanceof NoViableAltException) {
            msg = "no viable alternative at input " + this.getTokenErrorDisplay(e.token);
        }
        else if (e instanceof EarlyExitException) {
            msg = "required (...)+ loop did not match anything at input " + this.getTokenErrorDisplay(e.token);
        }
        else if (e instanceof MismatchedSetException) {
            final MismatchedSetException mse = (MismatchedSetException)e;
            msg = "mismatched input " + this.getTokenErrorDisplay(e.token) + " expecting set " + mse.expecting;
        }
        else if (e instanceof MismatchedNotSetException) {
            final MismatchedNotSetException mse2 = (MismatchedNotSetException)e;
            msg = "mismatched input " + this.getTokenErrorDisplay(e.token) + " expecting set " + mse2.expecting;
        }
        else if (e instanceof FailedPredicateException) {
            final FailedPredicateException fpe = (FailedPredicateException)e;
            msg = "rule " + fpe.ruleName + " failed predicate: {" + fpe.predicateText + "}?";
        }
        return msg;
    }
    
    public int getNumberOfSyntaxErrors() {
        return this.state.syntaxErrors;
    }
    
    public String getErrorHeader(final RecognitionException e) {
        if (this.getSourceName() != null) {
            return this.getSourceName() + " line " + e.line + ":" + e.charPositionInLine;
        }
        return "line " + e.line + ":" + e.charPositionInLine;
    }
    
    public String getTokenErrorDisplay(final Token t) {
        String s = t.getText();
        if (s == null) {
            if (t.getType() == -1) {
                s = "<EOF>";
            }
            else {
                s = "<" + t.getType() + ">";
            }
        }
        s = s.replaceAll("\n", "\\\\n");
        s = s.replaceAll("\r", "\\\\r");
        s = s.replaceAll("\t", "\\\\t");
        return "'" + s + "'";
    }
    
    public void emitErrorMessage(final String msg) {
        System.err.println(msg);
    }
    
    public void recover(final IntStream input, final RecognitionException re) {
        if (this.state.lastErrorIndex == input.index()) {
            input.consume();
        }
        this.state.lastErrorIndex = input.index();
        final BitSet followSet = this.computeErrorRecoverySet();
        this.beginResync();
        this.consumeUntil(input, followSet);
        this.endResync();
    }
    
    public void beginResync() {
    }
    
    public void endResync() {
    }
    
    protected BitSet computeErrorRecoverySet() {
        return this.combineFollows(false);
    }
    
    protected BitSet computeContextSensitiveRuleFOLLOW() {
        return this.combineFollows(true);
    }
    
    protected BitSet combineFollows(final boolean exact) {
        final int top = this.state._fsp;
        final BitSet followSet = new BitSet();
        for (int i = top; i >= 0; --i) {
            final BitSet localFollowSet = this.state.following[i];
            followSet.orInPlace(localFollowSet);
            if (exact) {
                if (!localFollowSet.member(1)) {
                    break;
                }
                if (i > 0) {
                    followSet.remove(1);
                }
            }
        }
        return followSet;
    }
    
    protected Object recoverFromMismatchedToken(final IntStream input, final int ttype, final BitSet follow) throws RecognitionException {
        RecognitionException e = null;
        if (this.mismatchIsUnwantedToken(input, ttype)) {
            e = new UnwantedTokenException(ttype, input);
            this.beginResync();
            input.consume();
            this.endResync();
            this.reportError(e);
            final Object matchedSymbol = this.getCurrentInputSymbol(input);
            input.consume();
            return matchedSymbol;
        }
        if (this.mismatchIsMissingToken(input, follow)) {
            final Object inserted = this.getMissingSymbol(input, e, ttype, follow);
            e = new MissingTokenException(ttype, input, inserted);
            this.reportError(e);
            return inserted;
        }
        e = new MismatchedTokenException(ttype, input);
        throw e;
    }
    
    public Object recoverFromMismatchedSet(final IntStream input, final RecognitionException e, final BitSet follow) throws RecognitionException {
        if (this.mismatchIsMissingToken(input, follow)) {
            this.reportError(e);
            return this.getMissingSymbol(input, e, 0, follow);
        }
        throw e;
    }
    
    protected Object getCurrentInputSymbol(final IntStream input) {
        return null;
    }
    
    protected Object getMissingSymbol(final IntStream input, final RecognitionException e, final int expectedTokenType, final BitSet follow) {
        return null;
    }
    
    public void consumeUntil(final IntStream input, final int tokenType) {
        for (int ttype = input.LA(1); ttype != -1 && ttype != tokenType; ttype = input.LA(1)) {
            input.consume();
        }
    }
    
    public void consumeUntil(final IntStream input, final BitSet set) {
        for (int ttype = input.LA(1); ttype != -1 && !set.member(ttype); ttype = input.LA(1)) {
            input.consume();
        }
    }
    
    protected void pushFollow(final BitSet fset) {
        if (this.state._fsp + 1 >= this.state.following.length) {
            final BitSet[] f = new BitSet[this.state.following.length * 2];
            System.arraycopy(this.state.following, 0, f, 0, this.state.following.length);
            this.state.following = f;
        }
        this.state.following[++this.state._fsp] = fset;
    }
    
    public List getRuleInvocationStack() {
        final String parserClassName = this.getClass().getName();
        return getRuleInvocationStack(new Throwable(), parserClassName);
    }
    
    public static List getRuleInvocationStack(final Throwable e, final String recognizerClassName) {
        final List rules = new ArrayList();
        final StackTraceElement[] stack = e.getStackTrace();
        int i;
        StackTraceElement t;
        for (i = 0, i = stack.length - 1; i >= 0; --i) {
            t = stack[i];
            if (!t.getClassName().startsWith("org.antlr.runtime.")) {
                if (!t.getMethodName().equals("nextToken")) {
                    if (t.getClassName().equals(recognizerClassName)) {
                        rules.add(t.getMethodName());
                    }
                }
            }
        }
        return rules;
    }
    
    public int getBacktrackingLevel() {
        return this.state.backtracking;
    }
    
    public void setBacktrackingLevel(final int n) {
        this.state.backtracking = n;
    }
    
    public boolean failed() {
        return this.state.failed;
    }
    
    public String[] getTokenNames() {
        return null;
    }
    
    public String getGrammarFileName() {
        return null;
    }
    
    public abstract String getSourceName();
    
    public List toStrings(final List tokens) {
        if (tokens == null) {
            return null;
        }
        final List strings = new ArrayList(tokens.size());
        for (int i = 0; i < tokens.size(); ++i) {
            strings.add(tokens.get(i).getText());
        }
        return strings;
    }
    
    public int getRuleMemoization(final int ruleIndex, final int ruleStartIndex) {
        if (this.state.ruleMemo[ruleIndex] == null) {
            this.state.ruleMemo[ruleIndex] = new HashMap();
        }
        final Integer stopIndexI = this.state.ruleMemo[ruleIndex].get(new Integer(ruleStartIndex));
        if (stopIndexI == null) {
            return -1;
        }
        return stopIndexI;
    }
    
    public boolean alreadyParsedRule(final IntStream input, final int ruleIndex) {
        final int stopIndex = this.getRuleMemoization(ruleIndex, input.index());
        if (stopIndex == -1) {
            return false;
        }
        if (stopIndex == -2) {
            this.state.failed = true;
        }
        else {
            input.seek(stopIndex + 1);
        }
        return true;
    }
    
    public void memoize(final IntStream input, final int ruleIndex, final int ruleStartIndex) {
        final int stopTokenIndex = this.state.failed ? -2 : (input.index() - 1);
        if (this.state.ruleMemo == null) {
            System.err.println("!!!!!!!!! memo array is null for " + this.getGrammarFileName());
        }
        if (ruleIndex >= this.state.ruleMemo.length) {
            System.err.println("!!!!!!!!! memo size is " + this.state.ruleMemo.length + ", but rule index is " + ruleIndex);
        }
        if (this.state.ruleMemo[ruleIndex] != null) {
            this.state.ruleMemo[ruleIndex].put(new Integer(ruleStartIndex), new Integer(stopTokenIndex));
        }
    }
    
    public int getRuleMemoizationCacheSize() {
        int n = 0;
        for (int i = 0; this.state.ruleMemo != null && i < this.state.ruleMemo.length; ++i) {
            final Map ruleMap = this.state.ruleMemo[i];
            if (ruleMap != null) {
                n += ruleMap.size();
            }
        }
        return n;
    }
    
    public void traceIn(final String ruleName, final int ruleIndex, final Object inputSymbol) {
        System.out.print("enter " + ruleName + " " + inputSymbol);
        if (this.state.backtracking > 0) {
            System.out.print(" backtracking=" + this.state.backtracking);
        }
        System.out.println();
    }
    
    public void traceOut(final String ruleName, final int ruleIndex, final Object inputSymbol) {
        System.out.print("exit " + ruleName + " " + inputSymbol);
        if (this.state.backtracking > 0) {
            System.out.print(" backtracking=" + this.state.backtracking);
            if (this.state.failed) {
                System.out.print(" failed");
            }
            else {
                System.out.print(" succeeded");
            }
        }
        System.out.println();
    }
}
