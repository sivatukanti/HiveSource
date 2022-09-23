// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.iapi.db;

import java.sql.SQLException;
import org.apache.derby.iapi.sql.dictionary.ConstraintDescriptor;
import org.apache.derby.iapi.sql.dictionary.ConstraintDescriptorList;
import org.apache.derby.iapi.sql.dictionary.ColumnDescriptor;
import org.apache.derby.iapi.sql.dictionary.ColumnDescriptorList;
import org.apache.derby.iapi.sql.execute.ExecRow;
import org.apache.derby.iapi.sql.dictionary.ConglomerateDescriptor;
import org.apache.derby.iapi.sql.dictionary.TableDescriptor;
import org.apache.derby.iapi.sql.execute.ExecutionFactory;
import org.apache.derby.iapi.sql.dictionary.DataDictionary;
import org.apache.derby.iapi.store.access.TransactionController;
import org.apache.derby.iapi.sql.conn.LanguageConnectionContext;
import org.apache.derby.iapi.store.access.ConglomerateController;
import org.apache.derby.iapi.store.access.ScanController;
import org.apache.derby.iapi.error.PublicAPI;
import org.apache.derby.iapi.types.RowLocation;
import org.apache.derby.iapi.services.io.FormatableBitSet;
import org.apache.derby.iapi.types.DataValueDescriptor;
import org.apache.derby.iapi.store.access.Qualifier;
import org.apache.derby.iapi.store.access.RowUtil;
import org.apache.derby.iapi.error.StandardException;
import org.apache.derby.iapi.sql.conn.ConnectionUtil;

public class ConsistencyChecker
{
    private ConsistencyChecker() {
    }
    
    public static boolean checkTable(final String s, final String s2) throws SQLException {
        long i = -1L;
        DataValueDescriptor rowLocationTemplate = null;
        ScanController scanController = null;
        ConglomerateController openConglomerate = null;
        ConglomerateController openConglomerate2 = null;
        final LanguageConnectionContext currentLCC = ConnectionUtil.getCurrentLCC();
        final TransactionController transactionExecute = currentLCC.getTransactionExecute();
        try {
            final DataDictionary dataDictionary = currentLCC.getDataDictionary();
            currentLCC.getDataValueFactory();
            final ExecutionFactory executionFactory = currentLCC.getLanguageConnectionFactory().getExecutionFactory();
            final TableDescriptor tableDescriptor = dataDictionary.getTableDescriptor(s2, dataDictionary.getSchemaDescriptor(s, transactionExecute, true), transactionExecute);
            if (tableDescriptor == null) {
                throw StandardException.newException("42X05", s + "." + s2);
            }
            if (tableDescriptor.getTableType() == 2) {
                return true;
            }
            openConglomerate = transactionExecute.openConglomerate(tableDescriptor.getHeapConglomerateId(), false, 0, 7, 5);
            openConglomerate.checkConsistency();
            final ConglomerateDescriptor conglomerateDescriptor = tableDescriptor.getConglomerateDescriptor(tableDescriptor.getHeapConglomerateId());
            final ExecRow valueRow = executionFactory.getValueRow(tableDescriptor.getNumberOfColumns());
            final ColumnDescriptorList columnDescriptorList = tableDescriptor.getColumnDescriptorList();
            for (int size = columnDescriptorList.size(), j = 0; j < size; ++j) {
                final ColumnDescriptor element = columnDescriptorList.elementAt(j);
                valueRow.setColumn(element.getPosition(), element.getType().getNull());
            }
            final ConglomerateDescriptor[] conglomerateDescriptors = tableDescriptor.getConglomerateDescriptors();
            for (int k = 0; k < conglomerateDescriptors.length; ++k) {
                final ConglomerateDescriptor conglomerateDescriptor2 = conglomerateDescriptors[k];
                if (conglomerateDescriptor2.isIndex()) {
                    openConglomerate2 = transactionExecute.openConglomerate(conglomerateDescriptor2.getConglomerateNumber(), false, 0, 7, 5);
                    openConglomerate2.checkConsistency();
                    openConglomerate2.close();
                    openConglomerate2 = null;
                    if (conglomerateDescriptor2.isConstraint() && dataDictionary.getConstraintDescriptor(tableDescriptor, conglomerateDescriptor2.getUUID()) == null) {
                        throw StandardException.newException("42X94", "CONSTRAINT for INDEX", conglomerateDescriptor2.getConglomerateName());
                    }
                    if (i < 0L) {
                        scanController = transactionExecute.openScan(conglomerateDescriptor.getConglomerateNumber(), false, 0, 7, 5, RowUtil.EMPTY_ROW_BITSET, null, 0, null, null, 0);
                        rowLocationTemplate = scanController.newRowLocationTemplate();
                        scanController.newRowLocationTemplate();
                        i = 0L;
                        while (scanController.next()) {
                            ++i;
                        }
                        scanController.close();
                        scanController = null;
                    }
                    final int[] baseColumnPositions = conglomerateDescriptor2.getIndexDescriptor().baseColumnPositions();
                    final int length = baseColumnPositions.length;
                    final FormatableBitSet set = new FormatableBitSet();
                    for (int l = 0; l < length; ++l) {
                        set.grow(baseColumnPositions[l]);
                        set.set(baseColumnPositions[l] - 1);
                    }
                    final ExecRow valueRow2 = executionFactory.getValueRow(length + 1);
                    for (int n = 0; n < length; ++n) {
                        valueRow2.setColumn(n + 1, tableDescriptor.getColumnDescriptor(baseColumnPositions[n]).getType().getNull());
                    }
                    valueRow2.setColumn(length + 1, rowLocationTemplate);
                    scanController = transactionExecute.openScan(conglomerateDescriptor2.getConglomerateNumber(), false, 0, 7, 5, null, null, 0, null, null, 0);
                    final DataValueDescriptor[] array = new DataValueDescriptor[length];
                    final DataValueDescriptor[] rowArray = valueRow.getRowArray();
                    for (int n2 = 0; n2 < length; ++n2) {
                        array[n2] = rowArray[baseColumnPositions[n2] - 1];
                    }
                    long m = 0L;
                    while (scanController.fetchNext(valueRow2.getRowArray())) {
                        final RowLocation rowLocation = (RowLocation)valueRow2.getColumn(length + 1);
                        if (!openConglomerate.fetch(rowLocation, rowArray, set)) {
                            throw StandardException.newException("X0X62.S", s + "." + s2, conglomerateDescriptor2.getConglomerateName(), rowLocation.toString(), valueRow2.toString());
                        }
                        for (int n3 = 0; n3 < length; ++n3) {
                            final DataValueDescriptor column = valueRow2.getColumn(n3 + 1);
                            final DataValueDescriptor dataValueDescriptor = array[n3];
                            if (column.compare(dataValueDescriptor) != 0) {
                                throw StandardException.newException("X0X61.S", conglomerateDescriptor2.getConglomerateName(), tableDescriptor.getSchemaName(), tableDescriptor.getName(), rowLocation.toString(), tableDescriptor.getColumnDescriptor(baseColumnPositions[n3]).getColumnName(), column.toString(), dataValueDescriptor.toString(), valueRow2.toString());
                            }
                        }
                        ++m;
                    }
                    scanController.close();
                    scanController = null;
                    if (m != i) {
                        throw StandardException.newException("X0Y55.S", conglomerateDescriptor2.getConglomerateName(), tableDescriptor.getSchemaName(), tableDescriptor.getName(), Long.toString(m), Long.toString(i));
                    }
                }
            }
            final ConstraintDescriptorList constraintDescriptors = dataDictionary.getConstraintDescriptors(tableDescriptor);
            for (int n4 = 0; n4 < constraintDescriptors.size(); ++n4) {
                final ConstraintDescriptor element2 = constraintDescriptors.elementAt(n4);
                if (element2.hasBackingIndex() && tableDescriptor.getConglomerateDescriptor(element2.getConglomerateId()) == null) {
                    throw StandardException.newException("42X94", "INDEX for CONSTRAINT", element2.getConstraintName());
                }
            }
        }
        catch (StandardException ex) {
            throw PublicAPI.wrapStandardException(ex);
        }
        finally {
            try {
                if (openConglomerate != null) {
                    openConglomerate.close();
                }
                if (openConglomerate2 != null) {
                    openConglomerate2.close();
                }
                if (scanController != null) {
                    scanController.close();
                }
            }
            catch (StandardException ex2) {
                throw PublicAPI.wrapStandardException(ex2);
            }
        }
        return true;
    }
}
