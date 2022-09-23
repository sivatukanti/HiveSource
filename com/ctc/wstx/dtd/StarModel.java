// 
// Decompiled by Procyon v0.5.36
// 

package com.ctc.wstx.dtd;

import java.util.BitSet;
import java.util.List;

public class StarModel extends ModelNode
{
    ModelNode mModel;
    
    public StarModel(final ModelNode model) {
        this.mModel = model;
    }
    
    @Override
    public ModelNode cloneModel() {
        return new StarModel(this.mModel.cloneModel());
    }
    
    @Override
    public boolean isNullable() {
        return true;
    }
    
    @Override
    public void indexTokens(final List<TokenModel> tokens) {
        this.mModel.indexTokens(tokens);
    }
    
    @Override
    public void addFirstPos(final BitSet pos) {
        this.mModel.addFirstPos(pos);
    }
    
    @Override
    public void addLastPos(final BitSet pos) {
        this.mModel.addLastPos(pos);
    }
    
    @Override
    public void calcFollowPos(final BitSet[] followPosSets) {
        this.mModel.calcFollowPos(followPosSets);
        final BitSet foll = new BitSet();
        this.mModel.addFirstPos(foll);
        final BitSet toAddTo = new BitSet();
        this.mModel.addLastPos(toAddTo);
        int ix = 0;
        while ((ix = toAddTo.nextSetBit(ix + 1)) >= 0) {
            followPosSets[ix].or(foll);
        }
    }
    
    @Override
    public String toString() {
        return this.mModel.toString() + "*";
    }
}
