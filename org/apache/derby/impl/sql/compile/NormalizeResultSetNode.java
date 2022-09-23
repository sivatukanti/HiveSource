// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.impl.sql.compile;

import org.apache.derby.iapi.services.compiler.MethodBuilder;
import org.apache.derby.iapi.error.StandardException;
import org.apache.derby.iapi.util.JBitSet;

public class NormalizeResultSetNode extends SingleChildResultSetNode
{
    private boolean forUpdate;
    
    public void init(final Object o, final Object o2, final Object o3, final Object o4) throws StandardException {
        super.init(o, o3);
        this.forUpdate = (boolean)o4;
        final ResultSetNode resultSetNode = (ResultSetNode)o;
        final ResultColumnList resultColumns = resultSetNode.getResultColumns();
        final ResultColumnList list = (ResultColumnList)o2;
        final ResultColumnList resultColumns2 = resultColumns;
        resultSetNode.setResultColumns(resultColumns.copyListAndObjects());
        resultColumns2.removeGeneratedGroupingColumns();
        resultColumns2.removeOrderByColumns();
        resultColumns2.genVirtualColumnNodes(resultSetNode, resultSetNode.getResultColumns());
        this.resultColumns = resultColumns2;
        if (resultSetNode.getReferencedTableMap() != null) {
            this.setReferencedTableMap((JBitSet)this.getReferencedTableMap().clone());
        }
        if (o2 != null) {
            for (int min = Math.min(list.size(), this.resultColumns.size()), i = 0; i < min; ++i) {
                ((ResultColumn)this.resultColumns.elementAt(i)).setType(((ResultColumn)list.elementAt(i)).getTypeServices());
            }
        }
    }
    
    public void generate(final ActivationClassBuilder activationClassBuilder, final MethodBuilder methodBuilder) throws StandardException {
        this.assignResultSetNumber();
        this.costEstimate = this.childResult.getFinalCostEstimate();
        final int addItem = activationClassBuilder.addItem(this.makeResultDescription());
        activationClassBuilder.pushGetResultSetFactoryExpression(methodBuilder);
        this.childResult.generate(activationClassBuilder, methodBuilder);
        methodBuilder.push(this.resultSetNumber);
        methodBuilder.push(addItem);
        methodBuilder.push(this.costEstimate.rowCount());
        methodBuilder.push(this.costEstimate.getEstimatedCost());
        methodBuilder.push(this.forUpdate);
        methodBuilder.callMethod((short)185, null, "getNormalizeResultSet", "org.apache.derby.iapi.sql.execute.NoPutResultSet", 6);
    }
    
    public void setRefActionInfo(final long n, final int[] array, final String s, final boolean b) {
        this.childResult.setRefActionInfo(n, array, s, b);
    }
    
    void pushOrderByList(final OrderByList list) {
        this.childResult.pushOrderByList(list);
    }
    
    void pushOffsetFetchFirst(final ValueNode valueNode, final ValueNode valueNode2, final boolean b) {
        this.childResult.pushOffsetFetchFirst(valueNode, valueNode2, b);
    }
}
