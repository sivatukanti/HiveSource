// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.iapi.store.raw.xact;

import org.apache.derby.iapi.services.io.Formatable;

public interface TransactionId extends Formatable
{
    int getMaxStoredSize();
}
