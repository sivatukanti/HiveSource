// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.impl.sql.execute;

import org.apache.derby.iapi.error.StandardException;
import org.apache.derby.iapi.sql.Activation;
import org.apache.derby.iapi.sql.ResultSet;

public final class ConstantActionActivation extends BaseActivation
{
    protected boolean shouldWeCheckRowCounts() {
        return false;
    }
    
    protected ResultSet createResultSet() throws StandardException {
        return this.getResultSetFactory().getDDLResultSet(this);
    }
    
    public void postConstructor() {
    }
}
