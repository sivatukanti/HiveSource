// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.impl.sql.execute;

import org.apache.derby.iapi.sql.StatementUtil;
import org.apache.derby.iapi.sql.execute.ExecRow;
import org.apache.derby.iapi.error.StandardException;
import org.apache.derby.iapi.store.access.TransactionController;

public class ForeignKeyRIChecker extends GenericRIChecker
{
    ForeignKeyRIChecker(final TransactionController transactionController, final FKInfo fkInfo) throws StandardException {
        super(transactionController, fkInfo);
    }
    
    void doCheck(final ExecRow execRow, final boolean b) throws StandardException {
        if (b) {
            return;
        }
        if (this.isAnyFieldNull(execRow)) {
            return;
        }
        if (!this.getScanController(this.fkInfo.refConglomNumber, this.refScoci, this.refDcoci, execRow).next()) {
            this.close();
            throw StandardException.newException("23503", this.fkInfo.fkConstraintNames[0], this.fkInfo.tableName, StatementUtil.typeName(this.fkInfo.stmtType), RowUtil.toString(execRow, this.fkInfo.colArray));
        }
    }
    
    int getRICheckIsolationLevel() {
        return 2;
    }
}
