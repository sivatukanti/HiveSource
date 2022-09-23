// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.iapi.services.locks;

public class ShExQual
{
    private int lockState;
    public static final int SHARED = 0;
    public static final int EXCLUSIVE = 1;
    public static final ShExQual SH;
    public static final ShExQual EX;
    
    private ShExQual(final int lockState) {
        this.lockState = lockState;
    }
    
    public int getLockState() {
        return this.lockState;
    }
    
    public String toString() {
        if (this.lockState == 0) {
            return "S";
        }
        return "X";
    }
    
    static {
        SH = new ShExQual(0);
        EX = new ShExQual(1);
    }
}
