// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.impl.store.access;

import org.apache.derby.iapi.error.StandardException;
import org.apache.derby.iapi.services.property.PersistentSet;
import org.apache.derby.iapi.services.property.PropertyUtil;
import org.apache.derby.iapi.store.access.TransactionController;

public class RllRAMAccessManager extends RAMAccessManager
{
    private int system_lock_level;
    
    public RllRAMAccessManager() {
        this.system_lock_level = 6;
    }
    
    protected int getSystemLockLevel() {
        return this.system_lock_level;
    }
    
    protected void bootLookupSystemLockLevel(final TransactionController transactionController) throws StandardException {
        if (this.isReadOnly() || !PropertyUtil.getServiceBoolean(transactionController, "derby.storage.rowLocking", true)) {
            this.system_lock_level = 7;
        }
    }
}
