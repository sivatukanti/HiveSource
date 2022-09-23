// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.iapi.store.raw;

import org.apache.derby.iapi.store.access.DatabaseInstant;
import java.io.InputStream;
import org.apache.derby.iapi.error.StandardException;

public interface ScanHandle
{
    boolean next() throws StandardException;
    
    int getGroup() throws StandardException;
    
    Loggable getLoggable() throws StandardException;
    
    InputStream getOptionalData() throws StandardException;
    
    DatabaseInstant getInstant() throws StandardException;
    
    Object getTransactionId() throws StandardException;
    
    void close();
}
