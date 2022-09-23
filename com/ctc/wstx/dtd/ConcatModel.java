// 
// Decompiled by Procyon v0.5.36
// 

package com.ctc.wstx.dtd;

import java.util.List;
import java.util.BitSet;

public class ConcatModel extends ModelNode
{
    ModelNode mLeftModel;
    ModelNode mRightModel;
    final boolean mNullable;
    BitSet mFirstPos;
    BitSet mLastPos;
    
    public ConcatModel(final ModelNode left, final ModelNode right) {
        this.mLeftModel = left;
        this.mRightModel = right;
        this.mNullable = (this.mLeftModel.isNullable() && this.mRightModel.isNullable());
    }
    
    @Override
    public ModelNode cloneModel() {
        return new ConcatModel(this.mLeftModel.cloneModel(), this.mRightModel.cloneModel());
    }
    
    @Override
    public boolean isNullable() {
        return this.mNullable;
    }
    
    @Override
    public void indexTokens(final List<TokenModel> tokens) {
        this.mLeftModel.indexTokens(tokens);
        this.mRightModel.indexTokens(tokens);
    }
    
    @Override
    public void addFirstPos(final BitSet pos) {
        if (this.mFirstPos == null) {
            this.mFirstPos = new BitSet();
            this.mLeftModel.addFirstPos(this.mFirstPos);
            if (this.mLeftModel.isNullable()) {
                this.mRightModel.addFirstPos(this.mFirstPos);
            }
        }
        pos.or(this.mFirstPos);
    }
    
    @Override
    public void addLastPos(final BitSet pos) {
        if (this.mLastPos == null) {
            this.mLastPos = new BitSet();
            this.mRightModel.addLastPos(this.mLastPos);
            if (this.mRightModel.isNullable()) {
                this.mLeftModel.addLastPos(this.mLastPos);
            }
        }
        pos.or(this.mLastPos);
    }
    
    @Override
    public void calcFollowPos(final BitSet[] followPosSets) {
        this.mLeftModel.calcFollowPos(followPosSets);
        this.mRightModel.calcFollowPos(followPosSets);
        final BitSet foll = new BitSet();
        this.mRightModel.addFirstPos(foll);
        final BitSet toAddTo = new BitSet();
        this.mLeftModel.addLastPos(toAddTo);
        int ix = 0;
        while ((ix = toAddTo.nextSetBit(ix + 1)) >= 0) {
            followPosSets[ix].or(foll);
        }
    }
    
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append('(');
        sb.append(this.mLeftModel.toString());
        sb.append(", ");
        sb.append(this.mRightModel.toString());
        sb.append(')');
        return sb.toString();
    }
}
