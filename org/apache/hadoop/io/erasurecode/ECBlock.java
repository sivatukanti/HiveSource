// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.io.erasurecode;

import org.apache.hadoop.classification.InterfaceAudience;

@InterfaceAudience.Private
public class ECBlock
{
    private boolean isParity;
    private boolean isErased;
    
    public ECBlock() {
        this(false, false);
    }
    
    public ECBlock(final boolean isParity, final boolean isErased) {
        this.isParity = isParity;
        this.isErased = isErased;
    }
    
    public void setParity(final boolean isParity) {
        this.isParity = isParity;
    }
    
    public void setErased(final boolean isErased) {
        this.isErased = isErased;
    }
    
    public boolean isParity() {
        return this.isParity;
    }
    
    public boolean isErased() {
        return this.isErased;
    }
}
