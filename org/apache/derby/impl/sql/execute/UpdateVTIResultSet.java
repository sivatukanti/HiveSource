// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.impl.sql.execute;

import org.apache.derby.iapi.sql.execute.CursorResultSet;
import org.apache.derby.iapi.types.DataValueDescriptor;
import org.apache.derby.iapi.sql.execute.ExecRow;
import java.util.Properties;
import org.apache.derby.iapi.error.StandardException;
import org.apache.derby.iapi.sql.Activation;
import org.apache.derby.iapi.sql.execute.NoPutResultSet;
import java.sql.ResultSet;

class UpdateVTIResultSet extends DMLVTIResultSet
{
    private java.sql.ResultSet rs;
    private TemporaryRowHolderImpl rowHolder;
    
    public UpdateVTIResultSet(final NoPutResultSet set, final Activation activation) throws StandardException {
        super(set, activation);
    }
    
    protected void openCore() throws StandardException {
        int nColumns = -1;
        int n = 1;
        this.rs = this.activation.getTargetVTI();
        ExecRow execRow = this.getNextRowCore(this.sourceResultSet);
        if (null != execRow) {
            nColumns = execRow.nColumns();
        }
        if (this.constants.deferred) {
            this.activation.clearIndexScanInfo();
        }
        if (null == this.rowHolder && this.constants.deferred) {
            this.rowHolder = new TemporaryRowHolderImpl(this.activation, new Properties(), this.resultDescription);
        }
        try {
            while (execRow != null) {
                if (this.constants.deferred) {
                    if (n != 0) {
                        execRow.getColumn(nColumns).setValue(this.rs.getRow());
                        n = 0;
                    }
                    else {
                        final DataValueDescriptor cloneColumn = execRow.cloneColumn(nColumns);
                        cloneColumn.setValue(this.rs.getRow());
                        execRow.setColumn(nColumns, cloneColumn);
                    }
                    this.rowHolder.insert(execRow);
                }
                else {
                    this.updateVTI(this.rs, execRow);
                }
                ++this.rowCount;
                if (this.constants.singleRowSource) {
                    execRow = null;
                }
                else {
                    execRow = this.getNextRowCore(this.sourceResultSet);
                }
            }
        }
        catch (StandardException ex) {
            throw ex;
        }
        catch (Throwable t) {
            throw StandardException.unexpectedUserException(t);
        }
        if (this.constants.deferred) {
            final CursorResultSet resultSet = this.rowHolder.getResultSet();
            try {
                resultSet.open();
                ExecRow nextRow;
                while ((nextRow = resultSet.getNextRow()) != null) {
                    this.rs.absolute(nextRow.getColumn(nColumns).getInt());
                    this.updateVTI(this.rs, nextRow);
                }
            }
            catch (Throwable t2) {
                throw StandardException.unexpectedUserException(t2);
            }
            finally {
                this.sourceResultSet.clearCurrentRow();
                resultSet.close();
            }
        }
        if (this.rowHolder != null) {
            this.rowHolder.close();
        }
    }
    
    private void updateVTI(final java.sql.ResultSet set, final ExecRow execRow) throws StandardException {
        final int[] changedColumnIds = this.constants.changedColumnIds;
        try {
            for (int i = 0; i < changedColumnIds.length; ++i) {
                final int n = changedColumnIds[i];
                final DataValueDescriptor column = execRow.getColumn(i + 1);
                if (column.isNull()) {
                    set.updateNull(n);
                }
                else {
                    column.setInto(set, n);
                }
            }
            set.updateRow();
        }
        catch (Throwable t) {
            throw StandardException.unexpectedUserException(t);
        }
    }
}
