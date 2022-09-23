// 
// Decompiled by Procyon v0.5.36
// 

package javax.transaction;

import java.rmi.RemoteException;

public class TransactionRolledbackException extends RemoteException
{
    public TransactionRolledbackException() {
    }
    
    public TransactionRolledbackException(final String message) {
        super(message);
    }
}
