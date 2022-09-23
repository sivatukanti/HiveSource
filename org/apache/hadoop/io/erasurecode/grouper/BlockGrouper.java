// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.io.erasurecode.grouper;

import org.apache.hadoop.io.erasurecode.ECBlockGroup;
import org.apache.hadoop.io.erasurecode.ECBlock;
import org.apache.hadoop.io.erasurecode.ECSchema;
import org.apache.hadoop.classification.InterfaceAudience;

@InterfaceAudience.Private
public class BlockGrouper
{
    private ECSchema schema;
    
    public void setSchema(final ECSchema schema) {
        this.schema = schema;
    }
    
    protected ECSchema getSchema() {
        return this.schema;
    }
    
    public int getRequiredNumDataBlocks() {
        return this.schema.getNumDataUnits();
    }
    
    public int getRequiredNumParityBlocks() {
        return this.schema.getNumParityUnits();
    }
    
    public ECBlockGroup makeBlockGroup(final ECBlock[] dataBlocks, final ECBlock[] parityBlocks) {
        final ECBlockGroup blockGroup = new ECBlockGroup(dataBlocks, parityBlocks);
        return blockGroup;
    }
    
    public boolean anyRecoverable(final ECBlockGroup blockGroup) {
        final int erasedCount = blockGroup.getErasedCount();
        return erasedCount > 0 && erasedCount <= this.getRequiredNumParityBlocks();
    }
}
