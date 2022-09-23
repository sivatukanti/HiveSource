// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.impl.sql.execute;

import org.apache.derby.iapi.sql.ResultSet;
import org.apache.derby.iapi.types.NumberDataValue;
import org.apache.derby.iapi.sql.conn.LanguageConnectionContext;
import org.apache.derby.iapi.sql.dictionary.TableDescriptor;
import org.apache.derby.iapi.store.access.GroupFetchScanController;
import org.apache.derby.iapi.error.StandardException;
import org.apache.derby.iapi.store.access.ConglomerateController;
import org.apache.derby.iapi.store.access.Qualifier;
import org.apache.derby.iapi.types.DataValueDescriptor;
import org.apache.derby.iapi.services.io.FormatableBitSet;
import org.apache.derby.iapi.sql.execute.ExecRow;
import org.apache.derby.iapi.sql.dictionary.ReferencedKeyConstraintDescriptor;
import org.apache.derby.iapi.sql.dictionary.ForeignKeyConstraintDescriptor;
import org.apache.derby.iapi.sql.dictionary.DataDictionary;
import org.apache.derby.iapi.store.access.TransactionController;
import org.apache.derby.catalog.UUID;

public abstract class ConstraintConstantAction extends DDLSingleTableConstantAction
{
    protected String constraintName;
    protected int constraintType;
    protected String tableName;
    protected String schemaName;
    protected UUID schemaId;
    protected IndexConstantAction indexAction;
    
    ConstraintConstantAction(final String constraintName, final int constraintType, final String tableName, final UUID uuid, final String schemaName, final IndexConstantAction indexAction) {
        super(uuid);
        this.constraintName = constraintName;
        this.constraintType = constraintType;
        this.tableName = tableName;
        this.indexAction = indexAction;
        this.schemaName = schemaName;
    }
    
    public int getConstraintType() {
        return this.constraintType;
    }
    
    public String getConstraintName() {
        return this.constraintName;
    }
    
    public IndexConstantAction getIndexAction() {
        return this.indexAction;
    }
    
    static void validateFKConstraint(final TransactionController transactionController, final DataDictionary dataDictionary, final ForeignKeyConstraintDescriptor foreignKeyConstraintDescriptor, final ReferencedKeyConstraintDescriptor referencedKeyConstraintDescriptor, final ExecRow execRow) throws StandardException {
        GroupFetchScanController openGroupFetchScan = null;
        final GroupFetchScanController openGroupFetchScan2 = transactionController.openGroupFetchScan(foreignKeyConstraintDescriptor.getIndexConglomerateDescriptor(dataDictionary).getConglomerateNumber(), false, 0, 7, 2, null, null, 1, null, null, -1);
        try {
            if (!openGroupFetchScan2.next()) {
                openGroupFetchScan2.close();
                return;
            }
            openGroupFetchScan2.reopenScan(null, 1, null, null, -1);
            openGroupFetchScan = transactionController.openGroupFetchScan(referencedKeyConstraintDescriptor.getIndexConglomerateDescriptor(dataDictionary).getConglomerateNumber(), false, 0, 6, 2, null, null, 1, null, null, -1);
            if (new RIBulkChecker(openGroupFetchScan, openGroupFetchScan2, execRow, true, null, null).doCheck() > 0) {
                throw StandardException.newException("X0Y45.S", foreignKeyConstraintDescriptor.getConstraintName(), foreignKeyConstraintDescriptor.getTableDescriptor().getName());
            }
        }
        finally {
            if (openGroupFetchScan2 != null) {
                openGroupFetchScan2.close();
            }
            if (openGroupFetchScan != null) {
                openGroupFetchScan.close();
            }
        }
    }
    
    static boolean validateConstraint(final String s, final String str, final TableDescriptor tableDescriptor, final LanguageConnectionContext languageConnectionContext, final boolean b) throws StandardException {
        final StringBuffer sb = new StringBuffer();
        sb.append("SELECT COUNT(*) FROM ");
        sb.append(tableDescriptor.getQualifiedName());
        sb.append(" WHERE NOT(");
        sb.append(str);
        sb.append(")");
        ResultSet executeSubStatement = null;
        try {
            executeSubStatement = languageConnectionContext.prepareInternalStatement(sb.toString()).executeSubStatement(languageConnectionContext, false, 0L);
            final ExecRow nextRow = executeSubStatement.getNextRow();
            nextRow.getRowArray();
            final Number n = (Number)((NumberDataValue)nextRow.getRowArray()[0]).getObject();
            if (n != null && n.longValue() != 0L) {
                if (b) {
                    throw StandardException.newException("X0Y59.S", s, tableDescriptor.getQualifiedName(), n.toString());
                }
                return false;
            }
        }
        finally {
            if (executeSubStatement != null) {
                executeSubStatement.close();
            }
        }
        return true;
    }
}
