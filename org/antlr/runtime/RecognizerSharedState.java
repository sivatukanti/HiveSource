// 
// Decompiled by Procyon v0.5.36
// 

package org.antlr.runtime;

import java.util.Map;

public class RecognizerSharedState
{
    public BitSet[] following;
    public int _fsp;
    public boolean errorRecovery;
    public int lastErrorIndex;
    public boolean failed;
    public int syntaxErrors;
    public int backtracking;
    public Map[] ruleMemo;
    public Token token;
    public int tokenStartCharIndex;
    public int tokenStartLine;
    public int tokenStartCharPositionInLine;
    public int channel;
    public int type;
    public String text;
    
    public RecognizerSharedState() {
        this.following = new BitSet[100];
        this._fsp = -1;
        this.errorRecovery = false;
        this.lastErrorIndex = -1;
        this.failed = false;
        this.syntaxErrors = 0;
        this.backtracking = 0;
        this.tokenStartCharIndex = -1;
    }
    
    public RecognizerSharedState(final RecognizerSharedState state) {
        this.following = new BitSet[100];
        this._fsp = -1;
        this.errorRecovery = false;
        this.lastErrorIndex = -1;
        this.failed = false;
        this.syntaxErrors = 0;
        this.backtracking = 0;
        this.tokenStartCharIndex = -1;
        if (this.following.length < state.following.length) {
            this.following = new BitSet[state.following.length];
        }
        System.arraycopy(state.following, 0, this.following, 0, state.following.length);
        this._fsp = state._fsp;
        this.errorRecovery = state.errorRecovery;
        this.lastErrorIndex = state.lastErrorIndex;
        this.failed = state.failed;
        this.syntaxErrors = state.syntaxErrors;
        this.backtracking = state.backtracking;
        if (state.ruleMemo != null) {
            this.ruleMemo = new Map[state.ruleMemo.length];
            System.arraycopy(state.ruleMemo, 0, this.ruleMemo, 0, state.ruleMemo.length);
        }
        this.token = state.token;
        this.tokenStartCharIndex = state.tokenStartCharIndex;
        this.tokenStartCharPositionInLine = state.tokenStartCharPositionInLine;
        this.channel = state.channel;
        this.type = state.type;
        this.text = state.text;
    }
}
