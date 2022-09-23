// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.impl.sql.compile;

import org.apache.derby.iapi.sql.compile.Visitor;
import org.apache.derby.iapi.sql.compile.ExpressionClassBuilderInterface;
import org.apache.derby.iapi.services.io.FormatableArrayHolder;
import org.apache.derby.iapi.services.io.FormatableIntHolder;
import org.apache.derby.catalog.types.ReferencedColumnsDescriptorImpl;
import org.apache.derby.iapi.services.compiler.MethodBuilder;
import org.apache.derby.iapi.error.StandardException;
import org.apache.derby.iapi.sql.compile.Optimizable;
import org.apache.derby.iapi.sql.compile.Optimizer;
import org.apache.derby.iapi.util.JBitSet;
import org.apache.derby.iapi.sql.compile.CostEstimate;

public class HashTableNode extends SingleChildResultSetNode
{
    PredicateList searchPredicateList;
    PredicateList joinPredicateList;
    SubqueryList pSubqueryList;
    SubqueryList rSubqueryList;
    
    public void init(final Object o, final Object o2, final Object o3, final Object o4, final Object o5, final Object o6, final Object o7, final Object o8, final Object o9, final Object o10) {
        super.init(o, o2);
        this.resultColumns = (ResultColumnList)o3;
        this.searchPredicateList = (PredicateList)o4;
        this.joinPredicateList = (PredicateList)o5;
        this.trulyTheBestAccessPath = (AccessPathImpl)o6;
        this.costEstimate = (CostEstimate)o7;
        this.pSubqueryList = (SubqueryList)o8;
        this.rSubqueryList = (SubqueryList)o9;
        this.setHashKeyColumns((int[])o10);
    }
    
    public Optimizable modifyAccessPath(final JBitSet set, final Optimizer optimizer) throws StandardException {
        return this;
    }
    
    public void printSubNodes(final int n) {
    }
    
    public void generate(final ActivationClassBuilder activationClassBuilder, final MethodBuilder methodBuilder) throws StandardException {
        if (this.childResult instanceof FromVTI) {
            ((FromVTI)this.childResult).computeProjectionAndRestriction(this.searchPredicateList);
        }
        this.generateMinion(activationClassBuilder, methodBuilder, false);
    }
    
    public void generateResultSet(final ExpressionClassBuilder expressionClassBuilder, final MethodBuilder methodBuilder) throws StandardException {
        this.generateMinion(expressionClassBuilder, methodBuilder, true);
    }
    
    private void generateMinion(final ExpressionClassBuilder expressionClassBuilder, final MethodBuilder methodBuilder, final boolean b) throws StandardException {
        ValueNode restorePredicates = null;
        this.verifyProperties(this.getDataDictionary());
        if (this.searchPredicateList != null) {
            this.searchPredicateList.removeRedundantPredicates();
            restorePredicates = this.searchPredicateList.restorePredicates();
            this.searchPredicateList = null;
        }
        final int addItem = expressionClassBuilder.addItem(new ReferencedColumnsDescriptorImpl(this.resultColumns.mapSourceColumns().mapArray));
        final int addItem2 = expressionClassBuilder.addItem(new FormatableArrayHolder(FormatableIntHolder.getFormatableIntHolders(this.hashKeyColumns())));
        expressionClassBuilder.pushGetResultSetFactoryExpression(methodBuilder);
        if (b) {
            this.childResult.generateResultSet(expressionClassBuilder, methodBuilder);
        }
        else {
            this.childResult.generate((ActivationClassBuilder)expressionClassBuilder, methodBuilder);
        }
        this.assignResultSetNumber();
        if (this.pSubqueryList != null && this.pSubqueryList.size() > 0) {
            this.pSubqueryList.setPointOfAttachment(this.resultSetNumber);
        }
        if (this.rSubqueryList != null && this.rSubqueryList.size() > 0) {
            this.rSubqueryList.setPointOfAttachment(this.resultSetNumber);
        }
        this.costEstimate = this.childResult.getFinalCostEstimate();
        if (restorePredicates == null) {
            methodBuilder.pushNull("org.apache.derby.iapi.services.loader.GeneratedMethod");
        }
        else {
            final MethodBuilder userExprFun = expressionClassBuilder.newUserExprFun();
            restorePredicates.generateExpression(expressionClassBuilder, userExprFun);
            userExprFun.methodReturn();
            userExprFun.complete();
            expressionClassBuilder.pushMethodReference(methodBuilder, userExprFun);
        }
        this.joinPredicateList.generateQualifiers(expressionClassBuilder, methodBuilder, (Optimizable)this.childResult, false);
        if (this.reflectionNeededForProjection()) {
            this.resultColumns.generateCore(expressionClassBuilder, methodBuilder, false);
        }
        else {
            methodBuilder.pushNull("org.apache.derby.iapi.services.loader.GeneratedMethod");
        }
        methodBuilder.push(this.resultSetNumber);
        methodBuilder.push(addItem);
        methodBuilder.push(this.resultColumns.reusableResult());
        methodBuilder.push(addItem2);
        methodBuilder.push(false);
        methodBuilder.push(-1L);
        methodBuilder.push(this.initialCapacity);
        methodBuilder.push(this.loadFactor);
        methodBuilder.push(this.costEstimate.singleScanRowCount());
        methodBuilder.push(this.costEstimate.getEstimatedCost());
        methodBuilder.callMethod((short)185, null, "getHashTableResultSet", "org.apache.derby.iapi.sql.execute.NoPutResultSet", 14);
    }
    
    void acceptChildren(final Visitor visitor) throws StandardException {
        super.acceptChildren(visitor);
        if (this.searchPredicateList != null) {
            this.searchPredicateList = (PredicateList)this.searchPredicateList.accept(visitor);
        }
        if (this.joinPredicateList != null) {
            this.joinPredicateList = (PredicateList)this.joinPredicateList.accept(visitor);
        }
    }
}
