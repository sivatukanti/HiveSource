// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.impl.sql;

import org.apache.derby.iapi.sql.execute.CursorResultSet;
import java.util.Hashtable;
import java.util.Vector;
import org.apache.derby.iapi.sql.execute.TemporaryRowHolder;
import org.apache.derby.iapi.sql.execute.ConstantAction;
import org.apache.derby.iapi.sql.execute.NoPutResultSet;
import java.sql.SQLWarning;
import org.apache.derby.iapi.sql.depend.Provider;
import org.apache.derby.catalog.UUID;
import org.apache.derby.catalog.DependableFinder;
import org.apache.derby.iapi.sql.conn.SQLSessionContext;
import org.apache.derby.iapi.sql.dictionary.TableDescriptor;
import org.apache.derby.iapi.store.access.ScanController;
import org.apache.derby.iapi.store.access.ConglomerateController;
import org.apache.derby.iapi.types.RowLocation;
import org.apache.derby.iapi.types.DataValueFactory;
import org.apache.derby.iapi.sql.ResultDescription;
import org.apache.derby.iapi.sql.Row;
import org.apache.derby.iapi.sql.execute.ExecRow;
import org.apache.derby.iapi.sql.execute.CursorActivation;
import org.apache.derby.iapi.sql.ResultSet;
import org.apache.derby.iapi.sql.ParameterValueSet;
import org.apache.derby.iapi.sql.execute.ExecutionFactory;
import org.apache.derby.iapi.store.access.TransactionController;
import org.apache.derby.iapi.error.StandardException;
import org.apache.derby.iapi.services.context.Context;
import org.apache.derby.iapi.sql.conn.LanguageConnectionContext;
import org.apache.derby.iapi.types.DataTypeDescriptor;
import org.apache.derby.iapi.services.loader.GeneratedClass;
import org.apache.derby.iapi.sql.execute.ExecPreparedStatement;
import org.apache.derby.impl.sql.execute.BaseActivation;
import org.apache.derby.iapi.sql.Activation;

public final class GenericActivationHolder implements Activation
{
    public BaseActivation ac;
    ExecPreparedStatement ps;
    GeneratedClass gc;
    DataTypeDescriptor[] paramTypes;
    private final LanguageConnectionContext lcc;
    
    GenericActivationHolder(final LanguageConnectionContext lcc, final GeneratedClass gc, final ExecPreparedStatement ps, final boolean b) throws StandardException {
        this.lcc = lcc;
        this.gc = gc;
        this.ps = ps;
        (this.ac = (BaseActivation)gc.newInstance(lcc)).setupActivation(ps, b);
        this.paramTypes = ps.getParameterTypes();
    }
    
    public void reset() throws StandardException {
        this.ac.reset();
    }
    
    public boolean checkIfThisActivationHasHoldCursor(final String s) {
        return this.ac.checkIfThisActivationHasHoldCursor(s);
    }
    
    public void setCursorName(final String cursorName) {
        this.ac.setCursorName(cursorName);
    }
    
    public String getCursorName() {
        return this.ac.getCursorName();
    }
    
    public void setResultSetHoldability(final boolean resultSetHoldability) {
        this.ac.setResultSetHoldability(resultSetHoldability);
    }
    
    public boolean getResultSetHoldability() {
        return this.ac.getResultSetHoldability();
    }
    
    public void setAutoGeneratedKeysResultsetInfo(final int[] array, final String[] array2) {
        this.ac.setAutoGeneratedKeysResultsetInfo(array, array2);
    }
    
    public boolean getAutoGeneratedKeysResultsetMode() {
        return this.ac.getAutoGeneratedKeysResultsetMode();
    }
    
    public int[] getAutoGeneratedKeysColumnIndexes() {
        return this.ac.getAutoGeneratedKeysColumnIndexes();
    }
    
    public String[] getAutoGeneratedKeysColumnNames() {
        return this.ac.getAutoGeneratedKeysColumnNames();
    }
    
    public LanguageConnectionContext getLanguageConnectionContext() {
        return this.lcc;
    }
    
    public TransactionController getTransactionController() {
        return this.ac.getTransactionController();
    }
    
    public ExecutionFactory getExecutionFactory() {
        return this.ac.getExecutionFactory();
    }
    
    public ParameterValueSet getParameterValueSet() {
        return this.ac.getParameterValueSet();
    }
    
    public void setParameters(final ParameterValueSet set, final DataTypeDescriptor[] array) throws StandardException {
        this.ac.setParameters(set, array);
    }
    
    public ResultSet execute() throws StandardException {
        final boolean b = this.gc == null || this.gc != this.ps.getActivationClass();
        if (b || !this.ac.isValid()) {
            GeneratedClass gc;
            if (b) {
                gc = this.ps.getActivationClass();
                if (gc == null) {
                    throw StandardException.newException("XCL32.S");
                }
            }
            else {
                gc = this.gc;
            }
            final BaseActivation ac = (BaseActivation)gc.newInstance(this.lcc);
            final DataTypeDescriptor[] parameterTypes = this.ps.getParameterTypes();
            ac.setupActivation(this.ps, this.ac.getScrollable());
            ac.setParameters(this.ac.getParameterValueSet(), this.paramTypes);
            if (this.ac.isSingleExecution()) {
                ac.setSingleExecution();
            }
            ac.setCursorName(this.ac.getCursorName());
            ac.setResultSetHoldability(this.ac.getResultSetHoldability());
            if (this.ac.getAutoGeneratedKeysResultsetMode()) {
                ac.setAutoGeneratedKeysResultsetInfo(this.ac.getAutoGeneratedKeysColumnIndexes(), this.ac.getAutoGeneratedKeysColumnNames());
            }
            ac.setMaxRows(this.ac.getMaxRows());
            this.ac.setupActivation(null, false);
            this.ac.close();
            this.ac = ac;
            this.gc = gc;
            this.paramTypes = parameterTypes;
        }
        final String cursorName = this.ac.getCursorName();
        if (cursorName != null) {
            final CursorActivation lookupCursorActivation = this.lcc.lookupCursorActivation(cursorName);
            if (lookupCursorActivation != null && lookupCursorActivation != this.ac) {
                throw StandardException.newException("X0X60.S", cursorName);
            }
        }
        return this.ac.execute();
    }
    
    public ResultSet getResultSet() {
        return this.ac.getResultSet();
    }
    
    public void setCurrentRow(final ExecRow execRow, final int n) {
        this.ac.setCurrentRow(execRow, n);
    }
    
    public Row getCurrentRow(final int n) {
        return this.ac.getCurrentRow(n);
    }
    
    public void clearCurrentRow(final int n) {
        this.ac.clearCurrentRow(n);
    }
    
    public ExecPreparedStatement getPreparedStatement() {
        return this.ps;
    }
    
    public void checkStatementValidity() throws StandardException {
        this.ac.checkStatementValidity();
    }
    
    public ResultDescription getResultDescription() {
        return this.ac.getResultDescription();
    }
    
    public DataValueFactory getDataValueFactory() {
        return this.ac.getDataValueFactory();
    }
    
    public RowLocation getRowLocationTemplate(final int n) {
        return this.ac.getRowLocationTemplate(n);
    }
    
    public ConglomerateController getHeapConglomerateController() {
        return this.ac.getHeapConglomerateController();
    }
    
    public void setHeapConglomerateController(final ConglomerateController heapConglomerateController) {
        this.ac.setHeapConglomerateController(heapConglomerateController);
    }
    
    public void clearHeapConglomerateController() {
        this.ac.clearHeapConglomerateController();
    }
    
    public ScanController getIndexScanController() {
        return this.ac.getIndexScanController();
    }
    
    public void setIndexScanController(final ScanController indexScanController) {
        this.ac.setIndexScanController(indexScanController);
    }
    
    public long getIndexConglomerateNumber() {
        return this.ac.getIndexConglomerateNumber();
    }
    
    public void setIndexConglomerateNumber(final long indexConglomerateNumber) {
        this.ac.setIndexConglomerateNumber(indexConglomerateNumber);
    }
    
    public void clearIndexScanInfo() {
        this.ac.clearIndexScanInfo();
    }
    
    public void close() throws StandardException {
        this.ac.close();
    }
    
    public boolean isClosed() {
        return this.ac.isClosed();
    }
    
    public void setSingleExecution() {
        this.ac.setSingleExecution();
    }
    
    public boolean isSingleExecution() {
        return this.ac.isSingleExecution();
    }
    
    public int getNumSubqueries() {
        return this.ac.getNumSubqueries();
    }
    
    public void setForCreateTable() {
        this.ac.setForCreateTable();
    }
    
    public boolean getForCreateTable() {
        return this.ac.getForCreateTable();
    }
    
    public void setDDLTableDescriptor(final TableDescriptor ddlTableDescriptor) {
        this.ac.setDDLTableDescriptor(ddlTableDescriptor);
    }
    
    public TableDescriptor getDDLTableDescriptor() {
        return this.ac.getDDLTableDescriptor();
    }
    
    public void setMaxRows(final long maxRows) {
        this.ac.setMaxRows(maxRows);
    }
    
    public long getMaxRows() {
        return this.ac.getMaxRows();
    }
    
    public void setTargetVTI(final java.sql.ResultSet targetVTI) {
        this.ac.setTargetVTI(targetVTI);
    }
    
    public java.sql.ResultSet getTargetVTI() {
        return this.ac.getTargetVTI();
    }
    
    public SQLSessionContext getSQLSessionContextForChildren() {
        return this.ac.getSQLSessionContextForChildren();
    }
    
    public SQLSessionContext setupSQLSessionContextForChildren(final boolean b) {
        return this.ac.setupSQLSessionContextForChildren(b);
    }
    
    public void setParentActivation(final Activation parentActivation) {
        this.ac.setParentActivation(parentActivation);
    }
    
    public Activation getParentActivation() {
        return this.ac.getParentActivation();
    }
    
    public DependableFinder getDependableFinder() {
        return null;
    }
    
    public String getObjectName() {
        return null;
    }
    
    public UUID getObjectID() {
        return null;
    }
    
    public String getClassType() {
        return null;
    }
    
    public boolean isPersistent() {
        return false;
    }
    
    public boolean isValid() {
        return false;
    }
    
    public void makeInvalid(final int n, final LanguageConnectionContext languageConnectionContext) throws StandardException {
    }
    
    public void prepareToInvalidate(final Provider provider, final int n, final LanguageConnectionContext languageConnectionContext) throws StandardException {
    }
    
    public void markUnused() {
        this.ac.markUnused();
    }
    
    public boolean isInUse() {
        return this.ac.isInUse();
    }
    
    public void addWarning(final SQLWarning sqlWarning) {
        this.ac.addWarning(sqlWarning);
    }
    
    public SQLWarning getWarnings() {
        return this.ac.getWarnings();
    }
    
    public void clearWarnings() {
        this.ac.clearWarnings();
    }
    
    public void informOfRowCount(final NoPutResultSet set, final long n) throws StandardException {
        this.ac.informOfRowCount(set, n);
    }
    
    public boolean isCursorActivation() {
        return this.ac.isCursorActivation();
    }
    
    public ConstantAction getConstantAction() {
        return this.ac.getConstantAction();
    }
    
    public void setParentResultSet(final TemporaryRowHolder temporaryRowHolder, final String s) {
        this.ac.setParentResultSet(temporaryRowHolder, s);
    }
    
    public Vector getParentResultSet(final String s) {
        return this.ac.getParentResultSet(s);
    }
    
    public void clearParentResultSets() {
        this.ac.clearParentResultSets();
    }
    
    public Hashtable getParentResultSets() {
        return this.ac.getParentResultSets();
    }
    
    public void setForUpdateIndexScan(final CursorResultSet forUpdateIndexScan) {
        this.ac.setForUpdateIndexScan(forUpdateIndexScan);
    }
    
    public CursorResultSet getForUpdateIndexScan() {
        return this.ac.getForUpdateIndexScan();
    }
    
    public java.sql.ResultSet[][] getDynamicResults() {
        return this.ac.getDynamicResults();
    }
    
    public int getMaxDynamicResults() {
        return this.ac.getMaxDynamicResults();
    }
}