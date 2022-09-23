// 
// Decompiled by Procyon v0.5.36
// 

package org.antlr.runtime;

public abstract class Lexer extends BaseRecognizer implements TokenSource
{
    protected CharStream input;
    
    public Lexer() {
    }
    
    public Lexer(final CharStream input) {
        this.input = input;
    }
    
    public Lexer(final CharStream input, final RecognizerSharedState state) {
        super(state);
        this.input = input;
    }
    
    public void reset() {
        super.reset();
        if (this.input != null) {
            this.input.seek(0);
        }
        if (this.state == null) {
            return;
        }
        this.state.token = null;
        this.state.type = 0;
        this.state.channel = 0;
        this.state.tokenStartCharIndex = -1;
        this.state.tokenStartCharPositionInLine = -1;
        this.state.tokenStartLine = -1;
        this.state.text = null;
    }
    
    public Token nextToken() {
        while (true) {
            this.state.token = null;
            this.state.channel = 0;
            this.state.tokenStartCharIndex = this.input.index();
            this.state.tokenStartCharPositionInLine = this.input.getCharPositionInLine();
            this.state.tokenStartLine = this.input.getLine();
            this.state.text = null;
            if (this.input.LA(1) == -1) {
                break;
            }
            try {
                this.mTokens();
                if (this.state.token == null) {
                    this.emit();
                }
                else if (this.state.token == Token.SKIP_TOKEN) {
                    continue;
                }
                return this.state.token;
            }
            catch (MismatchedRangeException re) {
                this.reportError(re);
            }
            catch (MismatchedTokenException re2) {
                this.reportError(re2);
            }
            catch (RecognitionException re3) {
                this.reportError(re3);
                this.recover(re3);
            }
        }
        final Token eof = new CommonToken(this.input, -1, 0, this.input.index(), this.input.index());
        eof.setLine(this.getLine());
        eof.setCharPositionInLine(this.getCharPositionInLine());
        return eof;
    }
    
    public void skip() {
        this.state.token = Token.SKIP_TOKEN;
    }
    
    public abstract void mTokens() throws RecognitionException;
    
    public void setCharStream(final CharStream input) {
        this.input = null;
        this.reset();
        this.input = input;
    }
    
    public CharStream getCharStream() {
        return this.input;
    }
    
    public String getSourceName() {
        return this.input.getSourceName();
    }
    
    public void emit(final Token token) {
        this.state.token = token;
    }
    
    public Token emit() {
        final Token t = new CommonToken(this.input, this.state.type, this.state.channel, this.state.tokenStartCharIndex, this.getCharIndex() - 1);
        t.setLine(this.state.tokenStartLine);
        t.setText(this.state.text);
        t.setCharPositionInLine(this.state.tokenStartCharPositionInLine);
        this.emit(t);
        return t;
    }
    
    public void match(final String s) throws MismatchedTokenException {
        int i = 0;
        while (i < s.length()) {
            if (this.input.LA(1) != s.charAt(i)) {
                if (this.state.backtracking > 0) {
                    this.state.failed = true;
                    return;
                }
                final MismatchedTokenException mte = new MismatchedTokenException(s.charAt(i), this.input);
                this.recover(mte);
                throw mte;
            }
            else {
                ++i;
                this.input.consume();
                this.state.failed = false;
            }
        }
    }
    
    public void matchAny() {
        this.input.consume();
    }
    
    public void match(final int c) throws MismatchedTokenException {
        if (this.input.LA(1) == c) {
            this.input.consume();
            this.state.failed = false;
            return;
        }
        if (this.state.backtracking > 0) {
            this.state.failed = true;
            return;
        }
        final MismatchedTokenException mte = new MismatchedTokenException(c, this.input);
        this.recover(mte);
        throw mte;
    }
    
    public void matchRange(final int a, final int b) throws MismatchedRangeException {
        if (this.input.LA(1) >= a && this.input.LA(1) <= b) {
            this.input.consume();
            this.state.failed = false;
            return;
        }
        if (this.state.backtracking > 0) {
            this.state.failed = true;
            return;
        }
        final MismatchedRangeException mre = new MismatchedRangeException(a, b, this.input);
        this.recover(mre);
        throw mre;
    }
    
    public int getLine() {
        return this.input.getLine();
    }
    
    public int getCharPositionInLine() {
        return this.input.getCharPositionInLine();
    }
    
    public int getCharIndex() {
        return this.input.index();
    }
    
    public String getText() {
        if (this.state.text != null) {
            return this.state.text;
        }
        return this.input.substring(this.state.tokenStartCharIndex, this.getCharIndex() - 1);
    }
    
    public void setText(final String text) {
        this.state.text = text;
    }
    
    public void reportError(final RecognitionException e) {
        this.displayRecognitionError(this.getTokenNames(), e);
    }
    
    public String getErrorMessage(final RecognitionException e, final String[] tokenNames) {
        String msg = null;
        if (e instanceof MismatchedTokenException) {
            final MismatchedTokenException mte = (MismatchedTokenException)e;
            msg = "mismatched character " + this.getCharErrorDisplay(e.c) + " expecting " + this.getCharErrorDisplay(mte.expecting);
        }
        else if (e instanceof NoViableAltException) {
            final NoViableAltException nvae = (NoViableAltException)e;
            msg = "no viable alternative at character " + this.getCharErrorDisplay(e.c);
        }
        else if (e instanceof EarlyExitException) {
            final EarlyExitException eee = (EarlyExitException)e;
            msg = "required (...)+ loop did not match anything at character " + this.getCharErrorDisplay(e.c);
        }
        else if (e instanceof MismatchedNotSetException) {
            final MismatchedNotSetException mse = (MismatchedNotSetException)e;
            msg = "mismatched character " + this.getCharErrorDisplay(e.c) + " expecting set " + mse.expecting;
        }
        else if (e instanceof MismatchedSetException) {
            final MismatchedSetException mse2 = (MismatchedSetException)e;
            msg = "mismatched character " + this.getCharErrorDisplay(e.c) + " expecting set " + mse2.expecting;
        }
        else if (e instanceof MismatchedRangeException) {
            final MismatchedRangeException mre = (MismatchedRangeException)e;
            msg = "mismatched character " + this.getCharErrorDisplay(e.c) + " expecting set " + this.getCharErrorDisplay(mre.a) + ".." + this.getCharErrorDisplay(mre.b);
        }
        else {
            msg = super.getErrorMessage(e, tokenNames);
        }
        return msg;
    }
    
    public String getCharErrorDisplay(final int c) {
        String s = String.valueOf((char)c);
        switch (c) {
            case -1: {
                s = "<EOF>";
                break;
            }
            case 10: {
                s = "\\n";
                break;
            }
            case 9: {
                s = "\\t";
                break;
            }
            case 13: {
                s = "\\r";
                break;
            }
        }
        return "'" + s + "'";
    }
    
    public void recover(final RecognitionException re) {
        this.input.consume();
    }
    
    public void traceIn(final String ruleName, final int ruleIndex) {
        final String inputSymbol = (char)this.input.LT(1) + " line=" + this.getLine() + ":" + this.getCharPositionInLine();
        super.traceIn(ruleName, ruleIndex, inputSymbol);
    }
    
    public void traceOut(final String ruleName, final int ruleIndex) {
        final String inputSymbol = (char)this.input.LT(1) + " line=" + this.getLine() + ":" + this.getCharPositionInLine();
        super.traceOut(ruleName, ruleIndex, inputSymbol);
    }
}
