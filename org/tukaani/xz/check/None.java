// 
// Decompiled by Procyon v0.5.36
// 

package org.tukaani.xz.check;

public class None extends Check
{
    public None() {
        this.size = 0;
        this.name = "None";
    }
    
    public void update(final byte[] array, final int n, final int n2) {
    }
    
    public byte[] finish() {
        return new byte[0];
    }
}
