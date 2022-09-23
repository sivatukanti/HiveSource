// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.impl.sql.execute;

import org.apache.derby.iapi.sql.execute.CursorResultSet;
import org.apache.derby.iapi.sql.execute.ExecRow;
import org.apache.derby.iapi.types.DataValueDescriptor;
import org.apache.derby.iapi.types.SQLInteger;
import org.apache.derby.iapi.sql.ResultDescription;
import java.util.Properties;
import org.apache.derby.iapi.error.StandardException;
import org.apache.derby.iapi.sql.Activation;
import org.apache.derby.iapi.sql.execute.NoPutResultSet;
import java.sql.ResultSet;

class DeleteVTIResultSet extends DMLVTIResultSet
{
    private java.sql.ResultSet rs;
    private TemporaryRowHolderImpl rowHolder;
    
    public DeleteVTIResultSet(final NoPutResultSet set, final Activation activation) throws StandardException {
        super(set, activation);
    }
    
    protected void openCore() throws StandardException {
        ExecRow execRow = this.getNextRowCore(this.sourceResultSet);
        if (execRow != null) {
            this.rs = this.activation.getTargetVTI();
        }
        if (this.constants.deferred) {
            this.activation.clearIndexScanInfo();
            if (null == this.rowHolder) {
                this.rowHolder = new TemporaryRowHolderImpl(this.activation, new Properties(), null);
            }
        }
        try {
            while (execRow != null) {
                if (!this.constants.deferred) {
                    this.rs.deleteRow();
                }
                else {
                    final ValueRow valueRow = new ValueRow(1);
                    valueRow.setColumn(1, new SQLInteger(this.rs.getRow()));
                    this.rowHolder.insert(valueRow);
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
                    this.rs.absolute(nextRow.getColumn(1).getInt());
                    this.rs.deleteRow();
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
}
