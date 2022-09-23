// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.iapi.store.raw;

import org.apache.derby.iapi.services.io.Formatable;

public interface GlobalTransactionId extends Formatable
{
    int getFormat_Id();
    
    byte[] getGlobalTransactionId();
    
    byte[] getBranchQualifier();
}
