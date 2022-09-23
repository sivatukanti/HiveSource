// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.iapi.store.raw;

public final class RowLock
{
    private final int type;
    private final int typeBit;
    private final int compat;
    private static final String[] shortnames;
    public static final int R_NUMBER = 8;
    private static final boolean[][] R_COMPAT;
    public static final RowLock RS2;
    public static final RowLock RS3;
    public static final RowLock RU2;
    public static final RowLock RU3;
    public static final RowLock RIP;
    public static final RowLock RI;
    public static final RowLock RX2;
    public static final RowLock RX3;
    public static final String DIAG_INDEX = "index";
    public static final String DIAG_XACTID = "xactid";
    public static final String DIAG_LOCKTYPE = "locktype";
    public static final String DIAG_LOCKMODE = "lockmode";
    public static final String DIAG_CONGLOMID = "conglomId";
    public static final String DIAG_CONTAINERID = "containerId";
    public static final String DIAG_SEGMENTID = "segmentId";
    public static final String DIAG_PAGENUM = "pageNum";
    public static final String DIAG_RECID = "RecId";
    public static final String DIAG_COUNT = "count";
    public static final String DIAG_GROUP = "group";
    public static final String DIAG_STATE = "state";
    
    private RowLock(final int type) {
        this.type = type;
        this.typeBit = 1 << type;
        int compat = 0;
        for (int i = 0; i < 8; ++i) {
            if (RowLock.R_COMPAT[type][i]) {
                compat |= 1 << i;
            }
        }
        this.compat = compat;
    }
    
    public int getType() {
        return this.type;
    }
    
    public boolean isCompatible(final RowLock rowLock) {
        return (rowLock.typeBit & this.compat) != 0x0;
    }
    
    public String toString() {
        return RowLock.shortnames[this.getType()];
    }
    
    static {
        shortnames = new String[] { "S", "S", "U", "U", "X", "X", "X", "X" };
        R_COMPAT = new boolean[][] { { true, true, true, true, true, false, false, false }, { true, true, true, true, false, false, false, false }, { true, true, false, false, true, false, false, false }, { true, true, false, false, false, false, false, false }, { true, false, true, false, true, true, true, false }, { false, false, false, false, true, false, false, false }, { false, false, false, false, true, false, false, false }, { false, false, false, false, false, false, false, false } };
        RS2 = new RowLock(0);
        RS3 = new RowLock(1);
        RU2 = new RowLock(2);
        RU3 = new RowLock(3);
        RIP = new RowLock(4);
        RI = new RowLock(5);
        RX2 = new RowLock(6);
        RX3 = new RowLock(7);
    }
}
