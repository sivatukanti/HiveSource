// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.impl.sql.compile;

import org.apache.derby.iapi.services.compiler.MethodBuilder;
import org.apache.derby.iapi.error.StandardException;

public final class RowCountNode extends SingleChildResultSetNode
{
    private ValueNode offset;
    private ValueNode fetchFirst;
    private boolean hasJDBClimitClause;
    
    public void init(final Object o, final Object o2, final Object o3, final Object o4, final Object o5) throws StandardException {
        this.init(o, null);
        this.resultColumns = (ResultColumnList)o2;
        this.offset = (ValueNode)o3;
        this.fetchFirst = (ValueNode)o4;
        this.hasJDBClimitClause = (o5 != null && (boolean)o5);
    }
    
    public void generate(final ActivationClassBuilder activationClassBuilder, final MethodBuilder methodBuilder) throws StandardException {
        this.assignResultSetNumber();
        this.costEstimate = this.childResult.getFinalCostEstimate();
        activationClassBuilder.pushGetResultSetFactoryExpression(methodBuilder);
        this.childResult.generate(activationClassBuilder, methodBuilder);
        activationClassBuilder.pushThisAsActivation(methodBuilder);
        methodBuilder.push(this.resultSetNumber);
        if (this.offset != null) {
            this.generateExprFun(activationClassBuilder, methodBuilder, this.offset);
        }
        else {
            methodBuilder.pushNull("org.apache.derby.iapi.services.loader.GeneratedMethod");
        }
        if (this.fetchFirst != null) {
            this.generateExprFun(activationClassBuilder, methodBuilder, this.fetchFirst);
        }
        else {
            methodBuilder.pushNull("org.apache.derby.iapi.services.loader.GeneratedMethod");
        }
        methodBuilder.push(this.hasJDBClimitClause);
        methodBuilder.push(this.costEstimate.rowCount());
        methodBuilder.push(this.costEstimate.getEstimatedCost());
        methodBuilder.callMethod((short)185, null, "getRowCountResultSet", "org.apache.derby.iapi.sql.execute.NoPutResultSet", 8);
    }
    
    private void generateExprFun(final ExpressionClassBuilder expressionClassBuilder, final MethodBuilder methodBuilder, final ValueNode valueNode) throws StandardException {
        final MethodBuilder exprFun = expressionClassBuilder.newExprFun();
        valueNode.generateExpression(expressionClassBuilder, exprFun);
        exprFun.methodReturn();
        exprFun.complete();
        expressionClassBuilder.pushMethodReference(methodBuilder, exprFun);
    }
    
    public String toString() {
        return "";
    }
}
