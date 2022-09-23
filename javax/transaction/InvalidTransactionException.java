// 
// Decompiled by Procyon v0.5.36
// 

package javax.transaction;

import java.rmi.RemoteException;

public class InvalidTransactionException extends RemoteException
{
    public InvalidTransactionException() {
    }
    
    public InvalidTransactionException(final String message) {
        super(message);
    }
}
