// 
// Decompiled by Procyon v0.5.36
// 

package com.ctc.wstx.dtd;

import java.util.List;
import java.util.BitSet;

public class ChoiceModel extends ModelNode
{
    final ModelNode[] mSubModels;
    boolean mNullable;
    BitSet mFirstPos;
    BitSet mLastPos;
    
    protected ChoiceModel(final ModelNode[] subModels) {
        this.mNullable = false;
        this.mSubModels = subModels;
        boolean nullable = false;
        for (int i = 0, len = subModels.length; i < len; ++i) {
            if (subModels[i].isNullable()) {
                nullable = true;
                break;
            }
        }
        this.mNullable = nullable;
    }
    
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        for (int i = 0; i < this.mSubModels.length; ++i) {
            if (i > 0) {
                sb.append(" | ");
            }
            sb.append(this.mSubModels[i].toString());
        }
        sb.append(')');
        return sb.toString();
    }
    
    @Override
    public ModelNode cloneModel() {
        final int len = this.mSubModels.length;
        final ModelNode[] newModels = new ModelNode[len];
        for (int i = 0; i < len; ++i) {
            newModels[i] = this.mSubModels[i].cloneModel();
        }
        return new ChoiceModel(newModels);
    }
    
    @Override
    public boolean isNullable() {
        return this.mNullable;
    }
    
    @Override
    public void indexTokens(final List<TokenModel> tokens) {
        for (int i = 0, len = this.mSubModels.length; i < len; ++i) {
            this.mSubModels[i].indexTokens(tokens);
        }
    }
    
    @Override
    public void addFirstPos(final BitSet firstPos) {
        if (this.mFirstPos == null) {
            this.mFirstPos = new BitSet();
            for (int i = 0, len = this.mSubModels.length; i < len; ++i) {
                this.mSubModels[i].addFirstPos(this.mFirstPos);
            }
        }
        firstPos.or(this.mFirstPos);
    }
    
    @Override
    public void addLastPos(final BitSet lastPos) {
        if (this.mLastPos == null) {
            this.mLastPos = new BitSet();
            for (int i = 0, len = this.mSubModels.length; i < len; ++i) {
                this.mSubModels[i].addLastPos(this.mLastPos);
            }
        }
        lastPos.or(this.mLastPos);
    }
    
    @Override
    public void calcFollowPos(final BitSet[] followPosSets) {
        for (int i = 0, len = this.mSubModels.length; i < len; ++i) {
            this.mSubModels[i].calcFollowPos(followPosSets);
        }
    }
}
