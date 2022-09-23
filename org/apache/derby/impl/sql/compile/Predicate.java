// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.impl.sql.compile;

import org.apache.derby.iapi.sql.compile.Visitor;
import org.apache.derby.iapi.services.compiler.MethodBuilder;
import java.util.HashSet;
import org.apache.derby.iapi.util.ReuseFactory;
import org.apache.derby.iapi.types.DataValueDescriptor;
import org.apache.derby.iapi.error.StandardException;
import org.apache.derby.iapi.sql.compile.Optimizable;
import java.util.Set;
import org.apache.derby.iapi.util.JBitSet;
import org.apache.derby.iapi.sql.compile.OptimizablePredicate;

public final class Predicate extends QueryTreeNode implements OptimizablePredicate, Comparable
{
    AndNode andNode;
    boolean pushable;
    JBitSet referencedSet;
    int equivalenceClass;
    int indexPosition;
    protected boolean startKey;
    protected boolean stopKey;
    protected boolean isQualifier;
    private Set searchClauses;
    private boolean scoped;
    
    public Predicate() {
        this.equivalenceClass = -1;
    }
    
    public void init(final Object o, final Object o2) {
        this.andNode = (AndNode)o;
        this.pushable = false;
        this.referencedSet = (JBitSet)o2;
        this.scoped = false;
    }
    
    public JBitSet getReferencedMap() {
        return this.referencedSet;
    }
    
    public boolean hasSubquery() {
        return !this.pushable;
    }
    
    public boolean hasMethodCall() {
        return !this.pushable;
    }
    
    public void markStartKey() {
        this.startKey = true;
    }
    
    public boolean isStartKey() {
        return this.startKey;
    }
    
    public void markStopKey() {
        this.stopKey = true;
    }
    
    public boolean isStopKey() {
        return this.stopKey;
    }
    
    public void markQualifier() {
        this.isQualifier = true;
    }
    
    public boolean isQualifier() {
        return this.isQualifier;
    }
    
    public boolean compareWithKnownConstant(final Optimizable optimizable, final boolean b) {
        boolean b2 = false;
        final RelationalOperator relop = this.getRelop();
        if (!this.isRelationalOpPredicate()) {
            return false;
        }
        if (relop.compareWithKnownConstant(optimizable, b)) {
            b2 = true;
        }
        return b2;
    }
    
    public int hasEqualOnColumnList(final int[] array, final Optimizable optimizable) throws StandardException {
        final RelationalOperator relop = this.getRelop();
        if (!this.isRelationalOpPredicate()) {
            return -1;
        }
        if (relop.getOperator() != 1) {
            return -1;
        }
        for (int i = 0; i < array.length; ++i) {
            final ColumnReference columnOperand = relop.getColumnOperand(optimizable, array[i]);
            if (columnOperand != null) {
                if (!relop.selfComparison(columnOperand)) {
                    return i;
                }
            }
        }
        return -1;
    }
    
    public DataValueDescriptor getCompareValue(final Optimizable optimizable) throws StandardException {
        return this.getRelop().getCompareValue(optimizable);
    }
    
    public boolean equalsComparisonWithConstantExpression(final Optimizable optimizable) {
        boolean equalsComparisonWithConstantExpression = false;
        if (this.isRelationalOpPredicate()) {
            equalsComparisonWithConstantExpression = this.getRelop().equalsComparisonWithConstantExpression(optimizable);
        }
        return equalsComparisonWithConstantExpression;
    }
    
    public double selectivity(final Optimizable optimizable) throws StandardException {
        return this.andNode.getLeftOperand().selectivity(optimizable);
    }
    
    public int getIndexPosition() {
        return this.indexPosition;
    }
    
    public int compareTo(final Object o) {
        final Predicate predicate = (Predicate)o;
        final int indexPosition = predicate.getIndexPosition();
        if (this.indexPosition < indexPosition) {
            return -1;
        }
        if (this.indexPosition > indexPosition) {
            return 1;
        }
        boolean b = false;
        boolean b2 = false;
        boolean b3 = true;
        boolean b4 = true;
        if (this.isRelationalOpPredicate() || this.isInListProbePredicate()) {
            final int operator = ((RelationalOperator)this.andNode.getLeftOperand()).getOperator();
            b = (operator == 1 || operator == 7);
            b3 = (operator == 2 || operator == 8);
        }
        if (predicate.isRelationalOpPredicate() || predicate.isInListProbePredicate()) {
            final int operator2 = ((RelationalOperator)predicate.getAndNode().getLeftOperand()).getOperator();
            b2 = (operator2 == 1 || operator2 == 7);
            b4 = (operator2 == 2 || operator2 == 8);
        }
        if ((b && !b2) || (!b3 && b4)) {
            return -1;
        }
        if ((b2 && !b) || (!b4 && b3)) {
            return 1;
        }
        return 0;
    }
    
    public AndNode getAndNode() {
        return this.andNode;
    }
    
    public void setAndNode(final AndNode andNode) {
        this.andNode = andNode;
    }
    
    public boolean getPushable() {
        return this.pushable;
    }
    
    public void setPushable(final boolean pushable) {
        this.pushable = pushable;
    }
    
    public JBitSet getReferencedSet() {
        return this.referencedSet;
    }
    
    void setEquivalenceClass(final int equivalenceClass) {
        this.equivalenceClass = equivalenceClass;
    }
    
    int getEquivalenceClass() {
        return this.equivalenceClass;
    }
    
    public void categorize() throws StandardException {
        this.pushable = this.andNode.categorize(this.referencedSet, false);
    }
    
    public RelationalOperator getRelop() {
        if (this.andNode.getLeftOperand() instanceof RelationalOperator) {
            return (RelationalOperator)this.andNode.getLeftOperand();
        }
        return null;
    }
    
    public final boolean isOrList() {
        return this.andNode.getLeftOperand() instanceof OrNode;
    }
    
    public final boolean isStoreQualifier() {
        return this.andNode.getLeftOperand() instanceof RelationalOperator || this.andNode.getLeftOperand() instanceof OrNode;
    }
    
    public final boolean isPushableOrClause(final Optimizable optimizable) throws StandardException {
        if (this.andNode.getLeftOperand() instanceof OrNode) {
            OrNode orNode;
            for (ValueNode valueNode = this.andNode.getLeftOperand(); valueNode instanceof OrNode; valueNode = orNode.getRightOperand()) {
                orNode = (OrNode)valueNode;
                if (!(orNode.getLeftOperand() instanceof RelationalOperator)) {
                    return false;
                }
                if (!((RelationalOperator)orNode.getLeftOperand()).isQualifier(optimizable, true)) {
                    return false;
                }
            }
            return true;
        }
        return false;
    }
    
    boolean transitiveSearchClauseAdded(final RelationalOperator relationalOperator) {
        return this.searchClauses != null && this.searchClauses.contains(ReuseFactory.getInteger(relationalOperator.getOperator()));
    }
    
    void setTransitiveSearchClauseAdded(final RelationalOperator relationalOperator) {
        if (this.searchClauses == null) {
            this.searchClauses = new HashSet();
        }
        this.searchClauses.add(ReuseFactory.getInteger(relationalOperator.getOperator()));
    }
    
    int getStartOperator(final Optimizable optimizable) {
        if (this.andNode.getLeftOperand() instanceof InListOperatorNode) {
            return 1;
        }
        return this.getRelop().getStartOperator(optimizable);
    }
    
    int getStopOperator(final Optimizable optimizable) {
        if (this.andNode.getLeftOperand() instanceof InListOperatorNode) {
            return -1;
        }
        return this.getRelop().getStopOperator(optimizable);
    }
    
    void setIndexPosition(final int indexPosition) {
        this.indexPosition = indexPosition;
    }
    
    void clearScanFlags() {
        this.startKey = false;
        this.stopKey = false;
        this.isQualifier = false;
    }
    
    void clearQualifierFlag() {
        this.isQualifier = false;
    }
    
    void generateExpressionOperand(final Optimizable optimizable, final int n, final ExpressionClassBuilder expressionClassBuilder, final MethodBuilder methodBuilder) throws StandardException {
        this.getRelop().generateExpressionOperand(optimizable, n, expressionClassBuilder, methodBuilder);
    }
    
    void generateAbsoluteColumnId(final MethodBuilder methodBuilder, final Optimizable optimizable) {
        this.getRelop().generateAbsoluteColumnId(methodBuilder, optimizable);
    }
    
    void generateRelativeColumnId(final MethodBuilder methodBuilder, final Optimizable optimizable) {
        this.getRelop().generateRelativeColumnId(methodBuilder, optimizable);
    }
    
    void generateOperator(final MethodBuilder methodBuilder, final Optimizable optimizable) {
        this.getRelop().generateOperator(methodBuilder, optimizable);
    }
    
    void generateQualMethod(final ExpressionClassBuilder expressionClassBuilder, final MethodBuilder methodBuilder, final Optimizable optimizable) throws StandardException {
        this.getRelop().generateQualMethod(expressionClassBuilder, methodBuilder, optimizable);
    }
    
    void generateOrderedNulls(final MethodBuilder methodBuilder) {
        this.getRelop().generateOrderedNulls(methodBuilder);
    }
    
    void generateNegate(final MethodBuilder methodBuilder, final Optimizable optimizable) {
        this.getRelop().generateNegate(methodBuilder, optimizable);
    }
    
    void generateOrderableVariantType(final MethodBuilder methodBuilder, final Optimizable optimizable) throws StandardException {
        methodBuilder.push(this.getRelop().getOrderableVariantType(optimizable));
    }
    
    public String toString() {
        return "";
    }
    
    public String binaryRelOpColRefsToString() {
        if (!(this.getAndNode().getLeftOperand() instanceof BinaryRelationalOperatorNode)) {
            return "";
        }
        final StringBuffer sb = new StringBuffer();
        final BinaryRelationalOperatorNode binaryRelationalOperatorNode = (BinaryRelationalOperatorNode)this.getAndNode().getLeftOperand();
        if (binaryRelationalOperatorNode.getLeftOperand() instanceof ColumnReference) {
            sb.append(((ColumnReference)binaryRelationalOperatorNode.getLeftOperand()).getTableName() + "." + ((ColumnReference)binaryRelationalOperatorNode.getLeftOperand()).getColumnName());
        }
        else {
            sb.append("<expr>");
        }
        sb.append(" " + binaryRelationalOperatorNode.operator + " ");
        if (binaryRelationalOperatorNode.getRightOperand() instanceof ColumnReference) {
            sb.append(((ColumnReference)binaryRelationalOperatorNode.getRightOperand()).getTableName() + "." + ((ColumnReference)binaryRelationalOperatorNode.getRightOperand()).getColumnName());
        }
        else {
            sb.append("<expr>");
        }
        return sb.toString();
    }
    
    public void printSubNodes(final int n) {
    }
    
    void acceptChildren(final Visitor visitor) throws StandardException {
        super.acceptChildren(visitor);
        if (this.andNode != null) {
            this.andNode = (AndNode)this.andNode.accept(visitor);
        }
    }
    
    public void copyFields(final Predicate predicate) {
        this.equivalenceClass = predicate.getEquivalenceClass();
        this.indexPosition = predicate.getIndexPosition();
        this.startKey = predicate.isStartKey();
        this.stopKey = predicate.isStopKey();
        this.isQualifier = predicate.isQualifier();
        this.searchClauses = predicate.searchClauses;
    }
    
    protected boolean pushableToSubqueries() throws StandardException {
        if (!this.isJoinPredicate()) {
            return false;
        }
        final BinaryRelationalOperatorNode binaryRelationalOperatorNode = (BinaryRelationalOperatorNode)this.getAndNode().getLeftOperand();
        final JBitSet set = new JBitSet(this.getReferencedSet().size());
        final BaseTableNumbersVisitor baseTableNumbersVisitor = new BaseTableNumbersVisitor(set);
        binaryRelationalOperatorNode.getLeftOperand().accept(baseTableNumbersVisitor);
        if (set.getFirstSetBit() == -1) {
            return false;
        }
        set.clearAll();
        binaryRelationalOperatorNode.getRightOperand().accept(baseTableNumbersVisitor);
        return set.getFirstSetBit() != -1;
    }
    
    protected boolean isJoinPredicate() {
        if (!(this.getAndNode().getLeftOperand() instanceof BinaryRelationalOperatorNode)) {
            return false;
        }
        final BinaryRelationalOperatorNode binaryRelationalOperatorNode = (BinaryRelationalOperatorNode)this.getAndNode().getLeftOperand();
        return binaryRelationalOperatorNode.getLeftOperand() instanceof ColumnReference && binaryRelationalOperatorNode.getRightOperand() instanceof ColumnReference && ((ColumnReference)binaryRelationalOperatorNode.getLeftOperand()).getTableNumber() != ((ColumnReference)binaryRelationalOperatorNode.getRightOperand()).getTableNumber();
    }
    
    protected Predicate getPredScopedForResultSet(final JBitSet set, final ResultSetNode resultSetNode, final int[] array) throws StandardException {
        if (!(this.getAndNode().getLeftOperand() instanceof BinaryRelationalOperatorNode)) {
            return this;
        }
        final ValueNode valueNode = (ValueNode)this.getNodeFactory().getNode(38, Boolean.TRUE, this.getContextManager());
        final BinaryRelationalOperatorNode binaryRelationalOperatorNode = (BinaryRelationalOperatorNode)this.getAndNode().getLeftOperand();
        final BinaryRelationalOperatorNode binaryRelationalOperatorNode2 = (BinaryRelationalOperatorNode)this.getNodeFactory().getNode(binaryRelationalOperatorNode.getNodeType(), binaryRelationalOperatorNode.getScopedOperand(-1, set, resultSetNode, array), binaryRelationalOperatorNode.getScopedOperand(1, set, resultSetNode, array), binaryRelationalOperatorNode.getForQueryRewrite(), this.getContextManager());
        binaryRelationalOperatorNode2.bindComparisonOperator();
        final AndNode andNode = (AndNode)this.getNodeFactory().getNode(39, binaryRelationalOperatorNode2, valueNode, this.getContextManager());
        andNode.postBindFixup();
        final JBitSet set2 = new JBitSet(resultSetNode.getReferencedTableMap().size());
        andNode.categorize(set2, false);
        final Predicate predicate = (Predicate)this.getNodeFactory().getNode(78, andNode, set2, this.getContextManager());
        predicate.clearScanFlags();
        predicate.copyFields(this);
        predicate.setPushable(this.getPushable());
        predicate.markAsScopedForPush();
        return predicate;
    }
    
    protected void markAsScopedForPush() {
        this.scoped = true;
    }
    
    protected boolean isScopedForPush() {
        return this.scoped;
    }
    
    protected boolean remapScopedPred() {
        if (!this.scoped) {
            return false;
        }
        final BinaryRelationalOperatorNode binaryRelationalOperatorNode = (BinaryRelationalOperatorNode)this.andNode.getLeftOperand();
        final ValueNode leftOperand = binaryRelationalOperatorNode.getLeftOperand();
        if (leftOperand instanceof ColumnReference && ((ColumnReference)leftOperand).isScoped()) {
            ((ColumnReference)leftOperand).remapColumnReferences();
        }
        else {
            final ValueNode rightOperand = binaryRelationalOperatorNode.getRightOperand();
            if (rightOperand instanceof ColumnReference && ((ColumnReference)rightOperand).isScoped()) {
                ((ColumnReference)rightOperand).remapColumnReferences();
            }
        }
        return true;
    }
    
    protected boolean isScopedToSourceResultSet() throws StandardException {
        if (!this.scoped) {
            return false;
        }
        final BinaryRelationalOperatorNode binaryRelationalOperatorNode = (BinaryRelationalOperatorNode)this.andNode.getLeftOperand();
        final ValueNode leftOperand = binaryRelationalOperatorNode.getLeftOperand();
        if (!(leftOperand instanceof ColumnReference)) {
            return false;
        }
        final ColumnReference columnReference = (ColumnReference)leftOperand;
        if (columnReference.isScoped()) {
            final ValueNode expression = columnReference.getSource().getExpression();
            return expression instanceof VirtualColumnNode || expression instanceof ColumnReference;
        }
        final ValueNode rightOperand = binaryRelationalOperatorNode.getRightOperand();
        if (!(rightOperand instanceof ColumnReference)) {
            return false;
        }
        final ValueNode expression2 = ((ColumnReference)rightOperand).getSource().getExpression();
        return expression2 instanceof VirtualColumnNode || expression2 instanceof ColumnReference;
    }
    
    protected boolean isRelationalOpPredicate() {
        return this.andNode.getLeftOperand().isRelationalOperator();
    }
    
    protected boolean isInListProbePredicate() {
        return this.andNode.getLeftOperand().isInListProbeNode();
    }
    
    protected InListOperatorNode getSourceInList() {
        return this.getSourceInList(false);
    }
    
    protected InListOperatorNode getSourceInList(final boolean b) {
        final ValueNode leftOperand = this.andNode.getLeftOperand();
        if (this.isInListProbePredicate()) {
            return ((BinaryRelationalOperatorNode)leftOperand).getInListOp();
        }
        if (b) {
            return null;
        }
        if (leftOperand instanceof InListOperatorNode) {
            return (InListOperatorNode)leftOperand;
        }
        return null;
    }
}
