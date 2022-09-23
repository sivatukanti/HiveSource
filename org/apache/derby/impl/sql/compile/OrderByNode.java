// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.impl.sql.compile;

import org.apache.derby.iapi.services.compiler.MethodBuilder;
import org.apache.derby.iapi.sql.ResultColumnDescriptor;
import org.apache.derby.iapi.error.StandardException;

public class OrderByNode extends SingleChildResultSetNode
{
    OrderByList orderByList;
    
    public void init(final Object o, final Object o2, final Object o3) throws StandardException {
        final ResultSetNode resultSetNode = (ResultSetNode)o;
        super.init(o, o3);
        this.orderByList = (OrderByList)o2;
        final ResultColumnList copyListAndObjects = resultSetNode.getResultColumns().copyListAndObjects();
        this.resultColumns = resultSetNode.getResultColumns();
        resultSetNode.setResultColumns(copyListAndObjects);
        this.resultColumns.genVirtualColumnNodes(this, copyListAndObjects);
    }
    
    public void printSubNodes(final int n) {
    }
    
    ResultColumnDescriptor[] makeResultDescriptors() {
        return this.childResult.makeResultDescriptors();
    }
    
    public void generate(final ActivationClassBuilder activationClassBuilder, final MethodBuilder methodBuilder) throws StandardException {
        if (this.costEstimate == null) {
            this.costEstimate = this.childResult.getFinalCostEstimate();
        }
        this.orderByList.generate(activationClassBuilder, methodBuilder, this.childResult);
        this.resultSetNumber = this.orderByList.getResultSetNumber();
        this.resultColumns.setResultSetNumber(this.resultSetNumber);
    }
}
