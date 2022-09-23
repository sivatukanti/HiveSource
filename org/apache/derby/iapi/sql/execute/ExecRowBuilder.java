// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.iapi.sql.execute;

import org.apache.derby.iapi.error.StandardException;
import org.apache.derby.iapi.types.DataTypeDescriptor;
import org.apache.derby.iapi.types.DataValueDescriptor;
import java.io.Serializable;

public class ExecRowBuilder implements Serializable
{
    private static final long serialVersionUID = -1078823466492523202L;
    private final boolean indexable;
    private final Object[] template;
    private final int[] columns;
    private int count;
    private int maxColumnNumber;
    
    public ExecRowBuilder(final int n, final boolean indexable) {
        this.template = new Object[n];
        this.columns = new int[n];
        this.indexable = indexable;
    }
    
    public void setColumn(final int b, final Object o) {
        this.template[this.count] = o;
        this.columns[this.count] = b;
        ++this.count;
        this.maxColumnNumber = Math.max(this.maxColumnNumber, b);
    }
    
    public ExecRow build(final ExecutionFactory executionFactory) throws StandardException {
        final ExecRow execRow = this.indexable ? executionFactory.getIndexableRow(this.maxColumnNumber) : executionFactory.getValueRow(this.maxColumnNumber);
        for (int i = 0; i < this.count; ++i) {
            final Object o = this.template[i];
            execRow.setColumn(this.columns[i], (o instanceof DataValueDescriptor) ? ((DataValueDescriptor)o).getNewNull() : ((DataTypeDescriptor)o).getNull());
        }
        return execRow;
    }
    
    public void reset(final ExecRow execRow) throws StandardException {
        for (int i = 0; i < this.count; ++i) {
            final int n = this.columns[i];
            execRow.setColumn(n, execRow.getColumn(n).getNewNull());
        }
    }
}
