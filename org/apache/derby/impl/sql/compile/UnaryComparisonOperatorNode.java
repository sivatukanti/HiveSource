// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.impl.sql.compile;

import org.apache.derby.iapi.sql.dictionary.ConglomerateDescriptor;
import org.apache.derby.iapi.services.compiler.MethodBuilder;
import org.apache.derby.iapi.sql.compile.Visitor;
import org.apache.derby.iapi.util.JBitSet;
import org.apache.derby.iapi.sql.compile.Optimizable;
import org.apache.derby.iapi.types.DataTypeDescriptor;
import org.apache.derby.iapi.types.TypeId;
import org.apache.derby.iapi.error.StandardException;
import java.util.List;

public abstract class UnaryComparisonOperatorNode extends UnaryOperatorNode
{
    public ValueNode bindExpression(final FromList list, final SubqueryList list2, final List list3) throws StandardException {
        this.bindOperand(list, list2, list3);
        this.bindComparisonOperator();
        return this;
    }
    
    public void bindComparisonOperator() throws StandardException {
        this.setType(new DataTypeDescriptor(TypeId.BOOLEAN_ID, false));
    }
    
    ValueNode eliminateNots(final boolean b) throws StandardException {
        if (!b) {
            return this;
        }
        return this.getNegation(this.operand);
    }
    
    abstract UnaryOperatorNode getNegation(final ValueNode p0) throws StandardException;
    
    public ColumnReference getColumnOperand(final Optimizable optimizable, final int n) {
        final FromBaseTable fromBaseTable = (FromBaseTable)optimizable;
        if (this.operand instanceof ColumnReference) {
            final ColumnReference columnReference = (ColumnReference)this.operand;
            if (columnReference.getTableNumber() == fromBaseTable.getTableNumber() && columnReference.getSource().getColumnPosition() == n) {
                return columnReference;
            }
        }
        return null;
    }
    
    public ColumnReference getColumnOperand(final Optimizable optimizable) {
        if (this.operand instanceof ColumnReference) {
            final ColumnReference columnReference = (ColumnReference)this.operand;
            if (columnReference.getTableNumber() == optimizable.getTableNumber()) {
                return columnReference;
            }
        }
        return null;
    }
    
    public ValueNode getOperand(final ColumnReference columnReference, final int n, final boolean b) {
        if (b) {
            return null;
        }
        if (this.operand instanceof ColumnReference) {
            final JBitSet tableMap = new JBitSet(n);
            final JBitSet set = new JBitSet(n);
            final BaseTableNumbersVisitor baseTableNumbersVisitor = new BaseTableNumbersVisitor(set);
            final ColumnReference columnReference2 = (ColumnReference)this.operand;
            try {
                columnReference2.accept(baseTableNumbersVisitor);
                baseTableNumbersVisitor.setTableMap(tableMap);
                columnReference.accept(baseTableNumbersVisitor);
            }
            catch (StandardException ex) {}
            set.and(tableMap);
            if (set.getFirstSetBit() != -1 && columnReference2.getSource().getColumnPosition() == columnReference.getColumnNumber()) {
                return this.operand;
            }
        }
        return null;
    }
    
    public boolean selfComparison(final ColumnReference columnReference) {
        return false;
    }
    
    public ValueNode getExpressionOperand(final int n, final int n2, final FromTable fromTable) {
        return null;
    }
    
    public void generateExpressionOperand(final Optimizable optimizable, final int n, final ExpressionClassBuilder expressionClassBuilder, final MethodBuilder methodBuilder) throws StandardException {
        expressionClassBuilder.generateNull(methodBuilder, this.operand.getTypeCompiler(), this.operand.getTypeServices().getCollationType());
    }
    
    public int getStartOperator(final Optimizable optimizable) {
        return 1;
    }
    
    public int getStopOperator(final Optimizable optimizable) {
        return -1;
    }
    
    public void generateOrderedNulls(final MethodBuilder methodBuilder) {
        methodBuilder.push(true);
    }
    
    public void generateQualMethod(final ExpressionClassBuilder expressionClassBuilder, final MethodBuilder methodBuilder, final Optimizable optimizable) throws StandardException {
        final MethodBuilder userExprFun = expressionClassBuilder.newUserExprFun();
        expressionClassBuilder.generateNull(userExprFun, this.operand.getTypeCompiler(), this.operand.getTypeServices().getCollationType());
        userExprFun.methodReturn();
        userExprFun.complete();
        expressionClassBuilder.pushMethodReference(methodBuilder, userExprFun);
    }
    
    public void generateAbsoluteColumnId(final MethodBuilder methodBuilder, final Optimizable optimizable) {
        methodBuilder.push(this.getAbsoluteColumnPosition(optimizable));
    }
    
    public void generateRelativeColumnId(final MethodBuilder methodBuilder, final Optimizable optimizable) {
        methodBuilder.push(optimizable.convertAbsoluteToRelativeColumnPosition(this.getAbsoluteColumnPosition(optimizable)));
    }
    
    private int getAbsoluteColumnPosition(final Optimizable optimizable) {
        int n = ((ColumnReference)this.operand).getSource().getColumnPosition();
        final ConglomerateDescriptor conglomerateDescriptor = optimizable.getTrulyTheBestAccessPath().getConglomerateDescriptor();
        if (conglomerateDescriptor.isIndex()) {
            n = conglomerateDescriptor.getIndexDescriptor().getKeyColumnPosition(n);
        }
        return n - 1;
    }
    
    public boolean orderedNulls() {
        return true;
    }
    
    public boolean isQualifier(final Optimizable optimizable, final boolean b) {
        return this.operand instanceof ColumnReference && ((ColumnReference)this.operand).getTableNumber() == ((FromTable)optimizable).getTableNumber();
    }
    
    public int getOrderableVariantType(final Optimizable optimizable) throws StandardException {
        return this.operand.getOrderableVariantType();
    }
}
