// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.iapi.store.raw;

public final class ContainerLock
{
    private final int type;
    private final int typeBit;
    private final int compat;
    public static final int C_NUMBER = 5;
    private static final boolean[][] C_COMPAT;
    private static String[] shortnames;
    public static final ContainerLock CIS;
    public static final ContainerLock CIX;
    public static final ContainerLock CS;
    public static final ContainerLock CU;
    public static final ContainerLock CX;
    
    private ContainerLock(final int type) {
        this.type = type;
        this.typeBit = 1 << type;
        int compat = 0;
        for (int i = 0; i < 5; ++i) {
            if (ContainerLock.C_COMPAT[type][i]) {
                compat |= 1 << i;
            }
        }
        this.compat = compat;
    }
    
    public int getType() {
        return this.type;
    }
    
    public boolean isCompatible(final ContainerLock containerLock) {
        return (containerLock.typeBit & this.compat) != 0x0;
    }
    
    public String toString() {
        return ContainerLock.shortnames[this.getType()];
    }
    
    static {
        C_COMPAT = new boolean[][] { { true, true, true, false, false }, { true, true, false, false, false }, { true, false, true, false, false }, { false, false, true, false, false }, { false, false, false, false, false } };
        ContainerLock.shortnames = new String[] { "IS", "IX", "S", "U", "X" };
        CIS = new ContainerLock(0);
        CIX = new ContainerLock(1);
        CS = new ContainerLock(2);
        CU = new ContainerLock(3);
        CX = new ContainerLock(4);
    }
}
