// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.impl.sql.compile;

import org.apache.derby.iapi.types.DataValueDescriptor;
import org.apache.derby.iapi.error.StandardException;
import org.apache.derby.iapi.services.compiler.MethodBuilder;
import org.apache.derby.iapi.sql.compile.Optimizable;

public interface RelationalOperator
{
    public static final int EQUALS_RELOP = 1;
    public static final int NOT_EQUALS_RELOP = 2;
    public static final int GREATER_THAN_RELOP = 3;
    public static final int GREATER_EQUALS_RELOP = 4;
    public static final int LESS_THAN_RELOP = 5;
    public static final int LESS_EQUALS_RELOP = 6;
    public static final int IS_NULL_RELOP = 7;
    public static final int IS_NOT_NULL_RELOP = 8;
    
    ColumnReference getColumnOperand(final Optimizable p0, final int p1);
    
    ColumnReference getColumnOperand(final Optimizable p0);
    
    ValueNode getOperand(final ColumnReference p0, final int p1, final boolean p2);
    
    ValueNode getExpressionOperand(final int p0, final int p1, final FromTable p2);
    
    void generateExpressionOperand(final Optimizable p0, final int p1, final ExpressionClassBuilder p2, final MethodBuilder p3) throws StandardException;
    
    boolean selfComparison(final ColumnReference p0) throws StandardException;
    
    boolean usefulStartKey(final Optimizable p0);
    
    boolean usefulStopKey(final Optimizable p0);
    
    int getStartOperator(final Optimizable p0);
    
    int getStopOperator(final Optimizable p0);
    
    void generateAbsoluteColumnId(final MethodBuilder p0, final Optimizable p1);
    
    void generateRelativeColumnId(final MethodBuilder p0, final Optimizable p1);
    
    void generateOperator(final MethodBuilder p0, final Optimizable p1);
    
    void generateQualMethod(final ExpressionClassBuilder p0, final MethodBuilder p1, final Optimizable p2) throws StandardException;
    
    void generateOrderedNulls(final MethodBuilder p0);
    
    void generateNegate(final MethodBuilder p0, final Optimizable p1);
    
    boolean orderedNulls();
    
    boolean isQualifier(final Optimizable p0, final boolean p1) throws StandardException;
    
    int getOperator();
    
    int getOrderableVariantType(final Optimizable p0) throws StandardException;
    
    boolean compareWithKnownConstant(final Optimizable p0, final boolean p1);
    
    DataValueDescriptor getCompareValue(final Optimizable p0) throws StandardException;
    
    boolean equalsComparisonWithConstantExpression(final Optimizable p0);
    
    RelationalOperator getTransitiveSearchClause(final ColumnReference p0) throws StandardException;
}
