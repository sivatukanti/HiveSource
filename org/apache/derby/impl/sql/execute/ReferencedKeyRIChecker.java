// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.impl.sql.execute;

import org.apache.derby.iapi.store.access.ScanController;
import org.apache.derby.iapi.sql.StatementUtil;
import org.apache.derby.iapi.sql.execute.ExecRow;
import org.apache.derby.iapi.error.StandardException;
import org.apache.derby.iapi.store.access.TransactionController;

public class ReferencedKeyRIChecker extends GenericRIChecker
{
    ReferencedKeyRIChecker(final TransactionController transactionController, final FKInfo fkInfo) throws StandardException {
        super(transactionController, fkInfo);
    }
    
    void doCheck(final ExecRow execRow, final boolean b) throws StandardException {
        if (this.isAnyFieldNull(execRow)) {
            return;
        }
        for (int i = 0; i < this.fkInfo.fkConglomNumbers.length; ++i) {
            if (!b || this.fkInfo.raRules[i] == 1) {
                final ScanController scanController = this.getScanController(this.fkInfo.fkConglomNumbers[i], this.fkScocis[i], this.fkDcocis[i], execRow);
                if (scanController.next()) {
                    this.close();
                    throw StandardException.newException("23503", this.fkInfo.fkConstraintNames[i], this.fkInfo.tableName, StatementUtil.typeName(this.fkInfo.stmtType), RowUtil.toString(execRow, this.fkInfo.colArray));
                }
                scanController.next();
            }
        }
    }
}
