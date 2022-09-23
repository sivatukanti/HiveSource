// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.iapi.services.locks;

public interface VirtualLockTable
{
    public static final int LATCH = 1;
    public static final int TABLE_AND_ROWLOCK = 2;
    public static final int SHEXLOCK = 4;
    public static final int ALL = -1;
    public static final String LOCKTYPE = "TYPE";
    public static final String LOCKNAME = "LOCKNAME";
    public static final String CONGLOMID = "CONGLOMID";
    public static final String CONTAINERID = "CONTAINERID";
    public static final String SEGMENTID = "SEGMENTID";
    public static final String PAGENUM = "PAGENUM";
    public static final String RECID = "RECID";
    public static final String XACTID = "XID";
    public static final String LOCKCOUNT = "LOCKCOUNT";
    public static final String LOCKMODE = "MODE";
    public static final String STATE = "STATE";
    public static final String LOCKOBJ = "LOCKOBJ";
    public static final String TABLENAME = "TABLENAME";
    public static final String INDEXNAME = "INDEXNAME";
    public static final String TABLETYPE = "TABLETYPE";
}
