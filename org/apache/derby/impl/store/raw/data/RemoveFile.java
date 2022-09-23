// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.impl.store.raw.data;

import java.security.PrivilegedActionException;
import org.apache.derby.iapi.error.StandardException;
import java.security.AccessController;
import org.apache.derby.iapi.services.context.ContextManager;
import org.apache.derby.io.StorageFile;
import java.security.PrivilegedExceptionAction;
import org.apache.derby.iapi.services.daemon.Serviceable;

final class RemoveFile implements Serviceable, PrivilegedExceptionAction
{
    private final StorageFile fileToGo;
    
    RemoveFile(final StorageFile fileToGo) {
        this.fileToGo = fileToGo;
    }
    
    public int performWork(final ContextManager contextManager) throws StandardException {
        try {
            AccessController.doPrivileged((PrivilegedExceptionAction<Object>)this);
        }
        catch (PrivilegedActionException ex) {
            throw (StandardException)ex.getException();
        }
        return 1;
    }
    
    public boolean serviceASAP() {
        return false;
    }
    
    public boolean serviceImmediately() {
        return true;
    }
    
    public Object run() throws StandardException {
        if (this.fileToGo.exists()) {
            if (this.fileToGo.isDirectory()) {
                if (!this.fileToGo.deleteAll()) {
                    throw StandardException.newException("XSDFK.S", this.fileToGo);
                }
            }
            else if (!this.fileToGo.delete()) {
                throw StandardException.newException("XSDFK.S", this.fileToGo);
            }
        }
        return null;
    }
}
