// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.impl.services.reflect;

import org.apache.derby.iapi.services.locks.ShExQual;
import org.apache.derby.iapi.services.locks.Latch;
import org.apache.derby.iapi.services.locks.ShExLockable;

class ClassLoaderLock extends ShExLockable
{
    private UpdateLoader myLoader;
    
    ClassLoaderLock(final UpdateLoader myLoader) {
        this.myLoader = myLoader;
    }
    
    public void unlockEvent(final Latch latch) {
        super.unlockEvent(latch);
        if (latch.getQualifier().equals(ShExQual.EX)) {
            this.myLoader.needReload();
        }
    }
}
