// 
// Decompiled by Procyon v0.5.36
// 

package com.ctc.wstx.dtd;

import java.util.BitSet;
import java.util.List;

public class OptionalModel extends ModelNode
{
    ModelNode mModel;
    
    public OptionalModel(final ModelNode model) {
        this.mModel = model;
    }
    
    @Override
    public ModelNode cloneModel() {
        return new OptionalModel(this.mModel.cloneModel());
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
    }
    
    @Override
    public String toString() {
        return this.mModel + "[?]";
    }
}
