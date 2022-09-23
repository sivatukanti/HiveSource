// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.impl.sql.execute;

import org.apache.derby.iapi.sql.conn.StatementContext;
import org.apache.derby.iapi.sql.conn.LanguageConnectionContext;
import org.apache.derby.iapi.error.StandardException;
import org.apache.derby.iapi.sql.Activation;

class SavepointConstantAction extends DDLConstantAction
{
    private final String savepointName;
    private final int savepointStatementType;
    
    SavepointConstantAction(final String savepointName, final int savepointStatementType) {
        this.savepointName = savepointName;
        this.savepointStatementType = savepointStatementType;
    }
    
    public String toString() {
        if (this.savepointStatementType == 1) {
            return this.constructToString("SAVEPOINT ", this.savepointName + " ON ROLLBACK RETAIN CURSORS ON ROLLBACK RETAIN LOCKS");
        }
        if (this.savepointStatementType == 2) {
            return this.constructToString("ROLLBACK WORK TO SAVEPOINT ", this.savepointName);
        }
        return this.constructToString("RELEASE TO SAVEPOINT ", this.savepointName);
    }
    
    public void executeConstantAction(final Activation activation) throws StandardException {
        final LanguageConnectionContext languageConnectionContext = activation.getLanguageConnectionContext();
        final StatementContext statementContext = languageConnectionContext.getStatementContext();
        if (statementContext != null && statementContext.inTrigger()) {
            throw StandardException.newException("XJ017.S");
        }
        if (this.savepointStatementType == 1) {
            if (this.savepointName.startsWith("SYS")) {
                throw StandardException.newException("42939", "SYS");
            }
            languageConnectionContext.languageSetSavePoint(this.savepointName, this.savepointName);
        }
        else if (this.savepointStatementType == 2) {
            languageConnectionContext.internalRollbackToSavepoint(this.savepointName, true, this.savepointName);
        }
        else {
            languageConnectionContext.releaseSavePoint(this.savepointName, this.savepointName);
        }
    }
}
