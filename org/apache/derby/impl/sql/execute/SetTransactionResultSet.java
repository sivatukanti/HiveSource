// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.impl.sql.execute;

import org.apache.derby.iapi.sql.Activation;

class SetTransactionResultSet extends MiscResultSet
{
    SetTransactionResultSet(final Activation activation) {
        super(activation);
    }
    
    public boolean doesCommit() {
        return true;
    }
}
