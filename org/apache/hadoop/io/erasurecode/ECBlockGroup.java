// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.io.erasurecode;

import org.apache.hadoop.classification.InterfaceAudience;

@InterfaceAudience.Private
public class ECBlockGroup
{
    private ECBlock[] dataBlocks;
    private ECBlock[] parityBlocks;
    
    public ECBlockGroup(final ECBlock[] dataBlocks, final ECBlock[] parityBlocks) {
        this.dataBlocks = dataBlocks;
        this.parityBlocks = parityBlocks;
    }
    
    public ECBlock[] getDataBlocks() {
        return this.dataBlocks;
    }
    
    public ECBlock[] getParityBlocks() {
        return this.parityBlocks;
    }
    
    public boolean anyErasedDataBlock() {
        for (int i = 0; i < this.dataBlocks.length; ++i) {
            if (this.dataBlocks[i].isErased()) {
                return true;
            }
        }
        return false;
    }
    
    public boolean anyErasedParityBlock() {
        for (int i = 0; i < this.parityBlocks.length; ++i) {
            if (this.parityBlocks[i].isErased()) {
                return true;
            }
        }
        return false;
    }
    
    public int getErasedCount() {
        int erasedCount = 0;
        for (final ECBlock dataBlock : this.dataBlocks) {
            if (dataBlock.isErased()) {
                ++erasedCount;
            }
        }
        for (final ECBlock parityBlock : this.parityBlocks) {
            if (parityBlock.isErased()) {
                ++erasedCount;
            }
        }
        return erasedCount;
    }
}
