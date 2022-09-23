// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.impl.sql.execute;

import java.util.Enumeration;
import org.apache.derby.iapi.sql.execute.ExecRow;
import java.util.Vector;
import org.apache.derby.iapi.sql.execute.TemporaryRowHolder;
import java.util.Hashtable;
import org.apache.derby.iapi.error.StandardException;
import org.apache.derby.iapi.sql.execute.ConstantAction;
import org.apache.derby.iapi.sql.Activation;
import org.apache.derby.iapi.sql.execute.NoPutResultSet;
import org.apache.derby.iapi.sql.execute.CursorResultSet;
import org.apache.derby.iapi.sql.ResultSet;

public class DeleteCascadeResultSet extends DeleteResultSet
{
    public ResultSet[] dependentResultSets;
    private int noDependents;
    private CursorResultSet parentSource;
    private FKInfo parentFKInfo;
    private long fkIndexConglomNumber;
    private String resultSetId;
    private boolean mainNodeForTable;
    private boolean affectedRows;
    private int tempRowHolderId;
    
    public DeleteCascadeResultSet(final NoPutResultSet set, final Activation activation, final int n, final ResultSet[] dependentResultSets, final String resultSetId) throws StandardException {
        super(set, (n == -1) ? activation.getConstantAction() : ((ConstantAction)activation.getPreparedStatement().getSavedObject(n)), activation);
        this.noDependents = 0;
        this.mainNodeForTable = true;
        this.affectedRows = false;
        if (n == -1) {
            activation.getConstantAction();
        }
        else {
            final ConstantAction constantAction = (ConstantAction)activation.getPreparedStatement().getSavedObject(n);
            this.resultDescription = this.constants.resultDescription;
        }
        this.cascadeDelete = true;
        this.resultSetId = resultSetId;
        if (dependentResultSets != null) {
            this.noDependents = dependentResultSets.length;
            this.dependentResultSets = dependentResultSets;
        }
    }
    
    public void open() throws StandardException {
        try {
            this.setup();
            if (this.isMultipleDeletePathsExist()) {
                this.setRowHoldersTypeToUniqueStream();
                while (this.collectAffectedRows(false)) {}
            }
            else {
                this.collectAffectedRows(false);
            }
            if (!this.affectedRows) {
                this.activation.addWarning(StandardException.newWarning("02000"));
            }
            this.runFkChecker(true);
            final Hashtable hashtable = new Hashtable();
            this.mergeRowHolders(hashtable);
            this.fireBeforeTriggers(hashtable);
            this.deleteDeferredRows();
            this.runFkChecker(false);
            this.rowChangerFinish();
            this.fireAfterTriggers();
        }
        finally {
            this.cleanUp();
            this.activation.clearParentResultSets();
        }
        this.endTime = this.getCurrentTimeMillis();
    }
    
    void setup() throws StandardException {
        if (this.lcc.getRunTimeStatisticsMode()) {
            this.savedSource = this.source;
        }
        super.setup();
        this.activation.setParentResultSet(this.rowHolder, this.resultSetId);
        this.tempRowHolderId = this.activation.getParentResultSet(this.resultSetId).size() - 1;
        for (int i = 0; i < this.noDependents; ++i) {
            if (this.dependentResultSets[i] instanceof UpdateResultSet) {
                ((UpdateResultSet)this.dependentResultSets[i]).setup();
            }
            else {
                ((DeleteCascadeResultSet)this.dependentResultSets[i]).setup();
            }
        }
    }
    
    boolean collectAffectedRows(boolean b) throws StandardException {
        if (super.collectAffectedRows()) {
            this.affectedRows = true;
            b = true;
        }
        for (int i = 0; i < this.noDependents; ++i) {
            if (this.dependentResultSets[i] instanceof UpdateResultSet) {
                if (((UpdateResultSet)this.dependentResultSets[i]).collectAffectedRows()) {
                    b = true;
                }
            }
            else if (((DeleteCascadeResultSet)this.dependentResultSets[i]).collectAffectedRows(b)) {
                b = true;
            }
        }
        return b;
    }
    
    void fireBeforeTriggers(final Hashtable hashtable) throws StandardException {
        if (!this.mainNodeForTable && !hashtable.containsKey(this.resultSetId)) {
            this.mainNodeForTable = true;
            hashtable.put(this.resultSetId, this.resultSetId);
        }
        for (int i = 0; i < this.noDependents; ++i) {
            if (this.dependentResultSets[i] instanceof UpdateResultSet) {
                ((UpdateResultSet)this.dependentResultSets[i]).fireBeforeTriggers();
            }
            else {
                ((DeleteCascadeResultSet)this.dependentResultSets[i]).fireBeforeTriggers(hashtable);
            }
        }
        if (this.mainNodeForTable && this.constants.deferred) {
            super.fireBeforeTriggers();
        }
    }
    
    void fireAfterTriggers() throws StandardException {
        for (int n = 0; n < this.noDependents && this.affectedRows; ++n) {
            if (this.dependentResultSets[n] instanceof UpdateResultSet) {
                ((UpdateResultSet)this.dependentResultSets[n]).fireAfterTriggers();
            }
            else {
                ((DeleteCascadeResultSet)this.dependentResultSets[n]).fireAfterTriggers();
            }
        }
        if (this.mainNodeForTable && this.constants.deferred) {
            super.fireAfterTriggers();
        }
    }
    
    void deleteDeferredRows() throws StandardException {
        for (int i = 0; i < this.noDependents; ++i) {
            if (this.dependentResultSets[i] instanceof UpdateResultSet) {
                ((UpdateResultSet)this.dependentResultSets[i]).updateDeferredRows();
            }
            else {
                ((DeleteCascadeResultSet)this.dependentResultSets[i]).deleteDeferredRows();
            }
        }
        if (this.mainNodeForTable) {
            super.deleteDeferredRows();
        }
    }
    
    void runFkChecker(final boolean b) throws StandardException {
        for (int i = 0; i < this.noDependents; ++i) {
            if (this.dependentResultSets[i] instanceof UpdateResultSet) {
                ((UpdateResultSet)this.dependentResultSets[i]).runChecker(b);
            }
            else {
                ((DeleteCascadeResultSet)this.dependentResultSets[i]).runFkChecker(b);
            }
        }
        if (this.mainNodeForTable) {
            super.runFkChecker(b);
        }
    }
    
    public void cleanUp() throws StandardException {
        super.cleanUp();
        for (int i = 0; i < this.noDependents; ++i) {
            if (this.dependentResultSets[i] instanceof UpdateResultSet) {
                ((UpdateResultSet)this.dependentResultSets[i]).cleanUp();
            }
            else {
                ((DeleteCascadeResultSet)this.dependentResultSets[i]).cleanUp();
            }
        }
        this.endTime = this.getCurrentTimeMillis();
    }
    
    private void rowChangerFinish() throws StandardException {
        this.rc.finish();
        for (int i = 0; i < this.noDependents; ++i) {
            if (this.dependentResultSets[i] instanceof UpdateResultSet) {
                ((UpdateResultSet)this.dependentResultSets[i]).rowChangerFinish();
            }
            else {
                ((DeleteCascadeResultSet)this.dependentResultSets[i]).rowChangerFinish();
            }
        }
    }
    
    private void mergeRowHolders(final Hashtable hashtable) throws StandardException {
        if (hashtable.containsKey(this.resultSetId) || this.rowCount == 0L) {
            this.mainNodeForTable = false;
        }
        else {
            this.mergeResultSets();
            this.mainNodeForTable = true;
            hashtable.put(this.resultSetId, this.resultSetId);
        }
        for (int i = 0; i < this.noDependents; ++i) {
            if (this.dependentResultSets[i] instanceof UpdateResultSet) {
                return;
            }
            ((DeleteCascadeResultSet)this.dependentResultSets[i]).mergeRowHolders(hashtable);
        }
    }
    
    private void mergeResultSets() throws StandardException {
        final Vector parentResultSet = this.activation.getParentResultSet(this.resultSetId);
        final int size = parentResultSet.size();
        if (size > 1) {
            for (int i = 0; i < size; ++i) {
                if (i != this.tempRowHolderId) {
                    final CursorResultSet resultSet = parentResultSet.elementAt(i).getResultSet();
                    resultSet.open();
                    ExecRow nextRow;
                    while ((nextRow = resultSet.getNextRow()) != null) {
                        this.rowHolder.insert(nextRow);
                    }
                    resultSet.close();
                }
            }
        }
    }
    
    public void finish() throws StandardException {
        super.finish();
        this.activation.clearParentResultSets();
    }
    
    private boolean isMultipleDeletePathsExist() {
        final Enumeration<String> keys = this.activation.getParentResultSets().keys();
        while (keys.hasMoreElements()) {
            if (this.activation.getParentResultSet(keys.nextElement()).size() > 1) {
                return true;
            }
        }
        return false;
    }
    
    private void setRowHoldersTypeToUniqueStream() {
        final Enumeration<String> keys = (Enumeration<String>)this.activation.getParentResultSets().keys();
        while (keys.hasMoreElements()) {
            final Vector parentResultSet = this.activation.getParentResultSet(keys.nextElement());
            for (int size = parentResultSet.size(), i = 0; i < size; ++i) {
                parentResultSet.elementAt(i).setRowHolderTypeToUniqueStream();
            }
        }
    }
}
