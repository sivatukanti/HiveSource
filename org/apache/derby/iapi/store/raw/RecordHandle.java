// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.iapi.store.raw;

import org.apache.derby.iapi.services.locks.Lockable;

public interface RecordHandle extends Lockable
{
    public static final int INVALID_RECORD_HANDLE = 0;
    public static final int RESERVED1_RECORD_HANDLE = 1;
    public static final int DEALLOCATE_PROTECTION_HANDLE = 2;
    public static final int PREVIOUS_KEY_HANDLE = 3;
    public static final int RESERVED4_RECORD_HANDLE = 4;
    public static final int RESERVED5_RECORD_HANDLE = 5;
    public static final int FIRST_RECORD_ID = 6;
    
    int getId();
    
    long getPageNumber();
    
    int getSlotNumberHint();
    
    ContainerKey getContainerId();
    
    Object getPageId();
}
