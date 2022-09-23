// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.impl.sql.execute;

import org.apache.derby.iapi.error.StandardException;
import org.apache.derby.iapi.sql.execute.NoPutResultSet;
import org.apache.derby.iapi.sql.Activation;
import org.apache.derby.iapi.sql.ResultSet;

public abstract class CursorActivation extends BaseActivation
{
    public void setCursorName(final String cursorName) {
        if (!this.isClosed()) {
            super.setCursorName(cursorName);
        }
    }
    
    public boolean isCursorActivation() {
        return true;
    }
    
    ResultSet decorateResultSet() throws StandardException {
        this.getLanguageConnectionContext().getAuthorizer().authorize(this, 1);
        final NoPutResultSet set = (NoPutResultSet)this.createResultSet();
        set.markAsTopResultSet();
        return set;
    }
}
