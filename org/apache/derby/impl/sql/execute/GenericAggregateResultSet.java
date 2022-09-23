// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.impl.sql.execute;

import org.apache.derby.iapi.error.SQLWarningFactory;
import org.apache.derby.iapi.sql.execute.ExecRow;
import org.apache.derby.iapi.services.loader.ClassFactory;
import java.util.Vector;
import org.apache.derby.iapi.sql.conn.LanguageConnectionContext;
import org.apache.derby.iapi.error.StandardException;
import org.apache.derby.iapi.sql.execute.ExecutionFactory;
import org.apache.derby.iapi.sql.execute.ExecPreparedStatement;
import org.apache.derby.iapi.sql.execute.ExecRowBuilder;
import org.apache.derby.iapi.sql.Activation;
import org.apache.derby.iapi.sql.execute.ExecIndexRow;
import org.apache.derby.iapi.sql.execute.NoPutResultSet;

abstract class GenericAggregateResultSet extends NoPutResultSetImpl
{
    protected GenericAggregator[] aggregates;
    protected AggregatorInfoList aggInfoList;
    public NoPutResultSet source;
    protected NoPutResultSet originalSource;
    private final ExecIndexRow rowTemplate;
    
    GenericAggregateResultSet(final NoPutResultSet set, final int n, final Activation activation, final int n2, final int n3, final double n4, final double n5) throws StandardException {
        super(activation, n3, n4, n5);
        this.source = set;
        this.originalSource = set;
        final ExecPreparedStatement preparedStatement = activation.getPreparedStatement();
        final ExecutionFactory executionFactory = activation.getExecutionFactory();
        this.rowTemplate = executionFactory.getIndexableRow(((ExecRowBuilder)preparedStatement.getSavedObject(n2)).build(executionFactory));
        this.aggInfoList = (AggregatorInfoList)preparedStatement.getSavedObject(n);
        this.aggregates = this.getSortAggregators(this.aggInfoList, false, activation.getLanguageConnectionContext(), set);
    }
    
    ExecIndexRow getRowTemplate() {
        return this.rowTemplate;
    }
    
    protected final GenericAggregator[] getSortAggregators(final AggregatorInfoList list, final boolean b, final LanguageConnectionContext languageConnectionContext, final NoPutResultSet set) throws StandardException {
        final Vector vector = new Vector<GenericAggregator>();
        final ClassFactory classFactory = languageConnectionContext.getLanguageConnectionFactory().getClassFactory();
        for (int size = list.size(), i = 0; i < size; ++i) {
            final AggregatorInfo aggregatorInfo = list.elementAt(i);
            if (!b || !aggregatorInfo.isDistinct()) {
                vector.addElement(new GenericAggregator(aggregatorInfo, classFactory));
            }
        }
        final GenericAggregator[] anArray = new GenericAggregator[vector.size()];
        vector.copyInto(anArray);
        return anArray;
    }
    
    protected final ExecIndexRow finishAggregation(ExecIndexRow rowTemplate) throws StandardException {
        final int length = this.aggregates.length;
        if (rowTemplate == null) {
            rowTemplate = this.getRowTemplate();
        }
        this.setCurrentRow(rowTemplate);
        boolean b = false;
        for (int i = 0; i < length; ++i) {
            if (this.aggregates[i].finish(rowTemplate)) {
                b = true;
            }
        }
        if (b) {
            this.addWarning(SQLWarningFactory.newSQLWarning("01003"));
        }
        return rowTemplate;
    }
    
    public void finish() throws StandardException {
        this.source.finish();
        super.finish();
    }
}
