// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.impl.sql.compile;

import org.apache.derby.iapi.services.compiler.MethodBuilder;
import java.util.List;
import org.apache.derby.iapi.sql.compile.OptimizableList;
import org.apache.derby.iapi.sql.compile.RequiredRowOrdering;
import org.apache.derby.iapi.sql.dictionary.DataDictionary;
import org.apache.derby.iapi.sql.compile.OptimizablePredicate;
import org.apache.derby.iapi.sql.dictionary.ConglomerateDescriptor;
import org.apache.derby.iapi.sql.compile.Optimizable;
import org.apache.derby.iapi.sql.compile.RowOrdering;
import org.apache.derby.iapi.sql.compile.CostEstimate;
import org.apache.derby.iapi.sql.compile.OptimizablePredicateList;
import org.apache.derby.iapi.sql.compile.Optimizer;
import org.apache.derby.iapi.error.StandardException;

public class DistinctNode extends SingleChildResultSetNode
{
    boolean inSortedOrder;
    
    public void init(final Object o, final Object o2, final Object o3) throws StandardException {
        super.init(o, o3);
        final ResultColumnList copyListAndObjects = this.childResult.getResultColumns().copyListAndObjects();
        this.resultColumns = this.childResult.getResultColumns();
        this.childResult.setResultColumns(copyListAndObjects);
        this.resultColumns.genVirtualColumnNodes(this, copyListAndObjects);
        this.resultColumns.verifyAllOrderable();
        this.inSortedOrder = (boolean)o2;
    }
    
    public CostEstimate optimizeIt(final Optimizer optimizer, final OptimizablePredicateList list, final CostEstimate costEstimate, final RowOrdering rowOrdering) throws StandardException {
        ((Optimizable)this.childResult).optimizeIt(optimizer, list, costEstimate, rowOrdering);
        return super.optimizeIt(optimizer, list, costEstimate, rowOrdering);
    }
    
    public CostEstimate estimateCost(final OptimizablePredicateList list, final ConglomerateDescriptor conglomerateDescriptor, final CostEstimate costEstimate, final Optimizer optimizer, final RowOrdering rowOrdering) throws StandardException {
        final CostEstimate estimateCost = ((Optimizable)this.childResult).estimateCost(list, conglomerateDescriptor, costEstimate, optimizer, rowOrdering);
        (this.costEstimate = this.getCostEstimate(optimizer)).setCost(estimateCost.getEstimatedCost(), estimateCost.rowCount(), estimateCost.singleScanRowCount());
        return this.costEstimate;
    }
    
    public boolean pushOptPredicate(final OptimizablePredicate optimizablePredicate) throws StandardException {
        return false;
    }
    
    public ResultSetNode optimize(final DataDictionary dataDictionary, final PredicateList list, final double n) throws StandardException {
        this.childResult = this.childResult.optimize(dataDictionary, list, n);
        (this.costEstimate = this.getOptimizer((OptimizableList)this.getNodeFactory().getNode(37, this.getNodeFactory().doJoinOrderOptimization(), this, this.getContextManager()), list, dataDictionary, null).newCostEstimate()).setCost(this.childResult.getCostEstimate().getEstimatedCost(), this.childResult.getCostEstimate().rowCount(), this.childResult.getCostEstimate().singleScanRowCount());
        return this;
    }
    
    boolean isOrderedOn(final ColumnReference[] array, final boolean b, final List list) {
        return false;
    }
    
    public void generate(final ActivationClassBuilder activationClassBuilder, final MethodBuilder methodBuilder) throws StandardException {
        this.assignResultSetNumber();
        this.costEstimate = this.childResult.getFinalCostEstimate();
        final int addItem = activationClassBuilder.addItem(activationClassBuilder.getColumnOrdering(this.resultColumns));
        activationClassBuilder.pushGetResultSetFactoryExpression(methodBuilder);
        this.childResult.generate(activationClassBuilder, methodBuilder);
        methodBuilder.push(true);
        methodBuilder.push(this.inSortedOrder);
        methodBuilder.push(addItem);
        methodBuilder.push(activationClassBuilder.addItem(this.resultColumns.buildRowTemplate()));
        methodBuilder.push(this.resultColumns.getTotalColumnSize());
        methodBuilder.push(this.resultSetNumber);
        methodBuilder.push(this.costEstimate.rowCount());
        methodBuilder.push(this.costEstimate.getEstimatedCost());
        methodBuilder.callMethod((short)185, null, "getSortResultSet", "org.apache.derby.iapi.sql.execute.NoPutResultSet", 9);
    }
}
