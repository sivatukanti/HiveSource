// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.impl.sql.compile;

import org.apache.derby.iapi.sql.dictionary.ConglomerateDescriptor;
import org.apache.derby.iapi.util.PropertyUtil;
import org.apache.derby.iapi.error.StandardException;
import org.apache.derby.iapi.store.access.StaticCompiledOpenConglomInfo;
import org.apache.derby.iapi.sql.compile.ExpressionClassBuilderInterface;
import org.apache.derby.iapi.sql.compile.OptimizablePredicateList;
import org.apache.derby.iapi.sql.compile.Optimizable;
import org.apache.derby.iapi.services.compiler.MethodBuilder;
import org.apache.derby.iapi.store.access.TransactionController;
import org.apache.derby.iapi.sql.compile.JoinStrategy;

abstract class BaseJoinStrategy implements JoinStrategy
{
    public boolean bulkFetchOK() {
        return true;
    }
    
    public boolean ignoreBulkFetch() {
        return false;
    }
    
    void fillInScanArgs1(final TransactionController transactionController, final MethodBuilder methodBuilder, final Optimizable optimizable, final OptimizablePredicateList list, final ExpressionClassBuilderInterface expressionClassBuilderInterface, final int n) throws StandardException {
        final boolean sameStartStopPosition = list.sameStartStopPosition();
        final ExpressionClassBuilder expressionClassBuilder = (ExpressionClassBuilder)expressionClassBuilderInterface;
        final long conglomerateNumber = optimizable.getTrulyTheBestAccessPath().getConglomerateDescriptor().getConglomerateNumber();
        final StaticCompiledOpenConglomInfo staticCompiledConglomInfo = transactionController.getStaticCompiledConglomInfo(conglomerateNumber);
        expressionClassBuilder.pushThisAsActivation(methodBuilder);
        methodBuilder.push(conglomerateNumber);
        methodBuilder.push(expressionClassBuilder.addItem(staticCompiledConglomInfo));
        methodBuilder.push(n);
        methodBuilder.push(optimizable.getResultSetNumber());
        list.generateStartKey(expressionClassBuilder, methodBuilder, optimizable);
        methodBuilder.push(list.startOperator(optimizable));
        if (!sameStartStopPosition) {
            list.generateStopKey(expressionClassBuilder, methodBuilder, optimizable);
        }
        else {
            methodBuilder.pushNull("org.apache.derby.iapi.services.loader.GeneratedMethod");
        }
        methodBuilder.push(list.stopOperator(optimizable));
        methodBuilder.push(sameStartStopPosition);
        list.generateQualifiers(expressionClassBuilder, methodBuilder, optimizable, true);
        methodBuilder.upCast("org.apache.derby.iapi.store.access.Qualifier[][]");
    }
    
    final void fillInScanArgs2(final MethodBuilder methodBuilder, final Optimizable optimizable, final int n, final int n2, final int n3, final int n4, final boolean b, final int n5) throws StandardException {
        methodBuilder.push(optimizable.getBaseTableName());
        if (optimizable.getProperties() != null) {
            methodBuilder.push(PropertyUtil.sortProperties(optimizable.getProperties()));
        }
        else {
            methodBuilder.pushNull("java.lang.String");
        }
        final ConglomerateDescriptor conglomerateDescriptor = optimizable.getTrulyTheBestAccessPath().getConglomerateDescriptor();
        if (conglomerateDescriptor.isConstraint()) {
            methodBuilder.push(optimizable.getDataDictionary().getConstraintDescriptor(optimizable.getTableDescriptor(), conglomerateDescriptor.getUUID()).getConstraintName());
        }
        else if (conglomerateDescriptor.isIndex()) {
            methodBuilder.push(conglomerateDescriptor.getConglomerateName());
        }
        else {
            methodBuilder.pushNull("java.lang.String");
        }
        methodBuilder.push(conglomerateDescriptor.isConstraint());
        methodBuilder.push(optimizable.forUpdate());
        methodBuilder.push(n2);
        methodBuilder.push(n3);
        methodBuilder.push(n4);
        methodBuilder.push(b);
        methodBuilder.push(n5);
        if (n > 0) {
            methodBuilder.push(n);
            methodBuilder.push(optimizable.hasLargeObjectColumns());
        }
        if (this.validForOutermostTable()) {
            methodBuilder.push(optimizable.isOneRowScan());
        }
        methodBuilder.push(optimizable.getTrulyTheBestAccessPath().getCostEstimate().rowCount());
        methodBuilder.push(optimizable.getTrulyTheBestAccessPath().getCostEstimate().getEstimatedCost());
    }
    
    public boolean isHashJoin() {
        return false;
    }
    
    protected boolean validForOutermostTable() {
        return false;
    }
}
