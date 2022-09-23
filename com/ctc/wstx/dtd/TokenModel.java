// 
// Decompiled by Procyon v0.5.36
// 

package com.ctc.wstx.dtd;

import java.util.BitSet;
import java.util.List;
import com.ctc.wstx.util.PrefixedName;

public final class TokenModel extends ModelNode
{
    static final TokenModel NULL_TOKEN;
    final PrefixedName mElemName;
    int mTokenIndex;
    
    public TokenModel(final PrefixedName elemName) {
        this.mTokenIndex = -1;
        this.mElemName = elemName;
    }
    
    public static TokenModel getNullToken() {
        return TokenModel.NULL_TOKEN;
    }
    
    public PrefixedName getName() {
        return this.mElemName;
    }
    
    @Override
    public ModelNode cloneModel() {
        return new TokenModel(this.mElemName);
    }
    
    @Override
    public boolean isNullable() {
        return false;
    }
    
    @Override
    public void indexTokens(final List<TokenModel> tokens) {
        if (this != TokenModel.NULL_TOKEN) {
            final int index = tokens.size();
            this.mTokenIndex = index;
            tokens.add(this);
        }
    }
    
    @Override
    public void addFirstPos(final BitSet firstPos) {
        firstPos.set(this.mTokenIndex);
    }
    
    @Override
    public void addLastPos(final BitSet lastPos) {
        lastPos.set(this.mTokenIndex);
    }
    
    @Override
    public void calcFollowPos(final BitSet[] followPosSets) {
    }
    
    @Override
    public String toString() {
        return (this.mElemName == null) ? "[null]" : this.mElemName.toString();
    }
    
    static {
        NULL_TOKEN = new TokenModel(null);
        TokenModel.NULL_TOKEN.mTokenIndex = 0;
    }
}
