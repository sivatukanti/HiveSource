// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.io.erasurecode;

import org.apache.hadoop.classification.InterfaceAudience;

@InterfaceAudience.Private
public final class ErasureCoderOptions
{
    private final int numDataUnits;
    private final int numParityUnits;
    private final int numAllUnits;
    private final boolean allowChangeInputs;
    private final boolean allowVerboseDump;
    
    public ErasureCoderOptions(final int numDataUnits, final int numParityUnits) {
        this(numDataUnits, numParityUnits, false, false);
    }
    
    public ErasureCoderOptions(final int numDataUnits, final int numParityUnits, final boolean allowChangeInputs, final boolean allowVerboseDump) {
        this.numDataUnits = numDataUnits;
        this.numParityUnits = numParityUnits;
        this.numAllUnits = numDataUnits + numParityUnits;
        this.allowChangeInputs = allowChangeInputs;
        this.allowVerboseDump = allowVerboseDump;
    }
    
    public int getNumDataUnits() {
        return this.numDataUnits;
    }
    
    public int getNumParityUnits() {
        return this.numParityUnits;
    }
    
    public int getNumAllUnits() {
        return this.numAllUnits;
    }
    
    public boolean allowChangeInputs() {
        return this.allowChangeInputs;
    }
    
    public boolean allowVerboseDump() {
        return this.allowVerboseDump;
    }
}
