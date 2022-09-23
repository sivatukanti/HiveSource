// 
// Decompiled by Procyon v0.5.36
// 

package org.objectweb.asm.commons;

import java.util.BitSet;

public class JSRInlinerAdapter$Subroutine
{
    public final BitSet instructions;
    
    protected JSRInlinerAdapter$Subroutine() {
        this.instructions = new BitSet();
    }
    
    public void addInstruction(final int bitIndex) {
        this.instructions.set(bitIndex);
    }
    
    public boolean ownsInstruction(final int bitIndex) {
        return this.instructions.get(bitIndex);
    }
    
    public String toString() {
        return "Subroutine: " + this.instructions;
    }
}
