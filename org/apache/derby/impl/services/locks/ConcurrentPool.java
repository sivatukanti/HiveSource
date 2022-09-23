// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.impl.services.locks;

import org.apache.derby.iapi.services.locks.LockOwner;
import org.apache.derby.iapi.util.Matchable;
import org.apache.derby.iapi.services.locks.Lockable;
import org.apache.derby.iapi.services.locks.Limit;
import org.apache.derby.iapi.services.locks.CompatibilitySpace;
import java.util.Enumeration;
import org.apache.derby.iapi.error.StandardException;
import org.apache.derby.iapi.services.daemon.Serviceable;
import java.util.Dictionary;
import java.io.Serializable;

public final class ConcurrentPool extends AbstractPool
{
    @Override
    protected LockTable createLockTable() {
        return new ConcurrentLockSet(this);
    }
}
