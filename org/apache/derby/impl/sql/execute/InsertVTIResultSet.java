// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.impl.sql.execute;

import org.apache.derby.iapi.types.DataValueDescriptor;
import org.apache.derby.iapi.sql.execute.CursorResultSet;
import org.apache.derby.iapi.sql.execute.ExecRow;
import java.util.Properties;
import org.apache.derby.vti.DeferModification;
import org.apache.derby.iapi.error.StandardException;
import org.apache.derby.iapi.sql.Activation;
import org.apache.derby.iapi.sql.execute.NoPutResultSet;
import java.sql.ResultSet;
import java.sql.PreparedStatement;

class InsertVTIResultSet extends DMLVTIResultSet
{
    private PreparedStatement ps;
    private VTIResultSet vtiRS;
    private java.sql.ResultSet rs;
    private TemporaryRowHolderImpl rowHolder;
    
    public InsertVTIResultSet(final NoPutResultSet set, final NoPutResultSet set2, final Activation activation) throws StandardException {
        super(set, activation);
        this.vtiRS = (VTIResultSet)set2;
    }
    
    protected void openCore() throws StandardException {
        if (this.ps == null) {
            this.ps = (PreparedStatement)this.vtiRS.getVTIConstructor().invoke(this.activation);
        }
        if (this.ps instanceof DeferModification) {
            try {
                ((DeferModification)this.ps).modificationNotify(1, this.constants.deferred);
            }
            catch (Throwable t) {
                throw StandardException.unexpectedUserException(t);
            }
        }
        ExecRow execRow = this.getNextRowCore(this.sourceResultSet);
        try {
            this.rs = this.ps.executeQuery();
        }
        catch (Throwable t2) {
            throw StandardException.unexpectedUserException(t2);
        }
        if (this.constants.deferred) {
            this.activation.clearIndexScanInfo();
        }
        if (this.firstExecute && this.constants.deferred) {
            this.rowHolder = new TemporaryRowHolderImpl(this.activation, new Properties(), this.resultDescription);
        }
        while (execRow != null) {
            if (this.constants.deferred) {
                this.rowHolder.insert(execRow);
            }
            else {
                this.insertIntoVTI(this.rs, execRow);
            }
            ++this.rowCount;
            if (this.constants.singleRowSource) {
                execRow = null;
            }
            else {
                execRow = this.getNextRowCore(this.sourceResultSet);
            }
        }
        if (this.constants.deferred) {
            final CursorResultSet resultSet = this.rowHolder.getResultSet();
            try {
                resultSet.open();
                ExecRow nextRow;
                while ((nextRow = resultSet.getNextRow()) != null) {
                    this.insertIntoVTI(this.rs, nextRow);
                }
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
    
    private void insertIntoVTI(final java.sql.ResultSet set, final ExecRow execRow) throws StandardException {
        try {
            set.moveToInsertRow();
            final DataValueDescriptor[] rowArray = execRow.getRowArray();
            for (int i = 0; i < rowArray.length; ++i) {
                final DataValueDescriptor dataValueDescriptor = rowArray[i];
                try {
                    if (dataValueDescriptor.isNull()) {
                        set.updateNull(i + 1);
                    }
                    else {
                        dataValueDescriptor.setInto(set, i + 1);
                    }
                }
                catch (Throwable t2) {
                    set.updateObject(i + 1, dataValueDescriptor.getObject());
                }
            }
            set.insertRow();
        }
        catch (Throwable t) {
            throw StandardException.unexpectedUserException(t);
        }
    }
    
    public void cleanUp() throws StandardException {
        if (this.rowHolder != null) {
            this.rowHolder.close();
        }
        if (this.rs != null) {
            try {
                this.rs.close();
            }
            catch (Throwable t) {
                throw StandardException.unexpectedUserException(t);
            }
            this.rs = null;
        }
        if (!this.vtiRS.isReuseablePs() && this.ps != null) {
            try {
                this.ps.close();
                this.ps = null;
            }
            catch (Throwable t2) {
                throw StandardException.unexpectedUserException(t2);
            }
        }
        super.cleanUp();
    }
    
    public void finish() throws StandardException {
        if (this.ps != null && !this.vtiRS.isReuseablePs()) {
            try {
                this.ps.close();
                this.ps = null;
            }
            catch (Throwable t) {
                throw StandardException.unexpectedUserException(t);
            }
        }
        super.finish();
    }
}
