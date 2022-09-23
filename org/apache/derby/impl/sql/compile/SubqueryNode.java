// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.impl.sql.compile;

import org.apache.derby.iapi.types.DataTypeDescriptor;
import org.apache.derby.iapi.services.compiler.LocalField;
import org.apache.derby.iapi.sql.compile.CostEstimate;
import org.apache.derby.iapi.services.compiler.MethodBuilder;
import org.apache.derby.iapi.sql.dictionary.DataDictionary;
import org.apache.derby.iapi.sql.compile.Visitor;
import org.apache.derby.iapi.util.JBitSet;
import org.apache.derby.iapi.sql.compile.CompilerContext;
import java.util.List;
import org.apache.derby.iapi.error.StandardException;

public class SubqueryNode extends ValueNode
{
    ResultSetNode resultSet;
    int subqueryType;
    boolean underTopAndNode;
    boolean preprocessed;
    boolean distinctExpression;
    boolean whereSubquery;
    ValueNode leftOperand;
    boolean pushedNewPredicate;
    boolean havingSubquery;
    BinaryComparisonOperatorNode parentComparisonOperator;
    private BooleanConstantNode trueNode;
    private int subqueryNumber;
    private int pointOfAttachment;
    private boolean foundCorrelation;
    private boolean doneCorrelationCheck;
    private boolean foundVariant;
    private boolean doneInvariantCheck;
    private OrderByList orderByList;
    private ValueNode offset;
    private ValueNode fetchFirst;
    private boolean hasJDBClimitClause;
    public static final int NOTIMPLEMENTED_SUBQUERY = -1;
    public static final int FROM_SUBQUERY = 0;
    public static final int IN_SUBQUERY = 1;
    public static final int NOT_IN_SUBQUERY = 2;
    public static final int EQ_ANY_SUBQUERY = 3;
    public static final int EQ_ALL_SUBQUERY = 4;
    public static final int NE_ANY_SUBQUERY = 5;
    public static final int NE_ALL_SUBQUERY = 6;
    public static final int GT_ANY_SUBQUERY = 7;
    public static final int GT_ALL_SUBQUERY = 8;
    public static final int GE_ANY_SUBQUERY = 9;
    public static final int GE_ALL_SUBQUERY = 10;
    public static final int LT_ANY_SUBQUERY = 11;
    public static final int LT_ALL_SUBQUERY = 12;
    public static final int LE_ANY_SUBQUERY = 13;
    public static final int LE_ALL_SUBQUERY = 14;
    public static final int EXISTS_SUBQUERY = 15;
    public static final int NOT_EXISTS_SUBQUERY = 16;
    public static final int EXPRESSION_SUBQUERY = 17;
    
    public SubqueryNode() {
        this.havingSubquery = false;
        this.subqueryNumber = -1;
        this.pointOfAttachment = -1;
    }
    
    public void init(final Object o, final Object o2, final Object o3, final Object o4, final Object o5, final Object o6, final Object o7) {
        this.resultSet = (ResultSetNode)o;
        this.subqueryType = (int)o2;
        this.orderByList = (OrderByList)o4;
        this.offset = (ValueNode)o5;
        this.fetchFirst = (ValueNode)o6;
        this.hasJDBClimitClause = (o7 != null && (boolean)o7);
        this.underTopAndNode = false;
        this.leftOperand = (ValueNode)o3;
    }
    
    public String toString() {
        return "";
    }
    
    public void printSubNodes(final int n) {
    }
    
    public ResultSetNode getResultSet() {
        return this.resultSet;
    }
    
    public int getSubqueryType() {
        return this.subqueryType;
    }
    
    public void setSubqueryType(final int subqueryType) {
        this.subqueryType = subqueryType;
    }
    
    public void setPointOfAttachment(final int pointOfAttachment) throws StandardException {
        if (!this.isMaterializable()) {
            this.pointOfAttachment = pointOfAttachment;
        }
    }
    
    public boolean getUnderTopAndNode() {
        return this.underTopAndNode;
    }
    
    public int getPointOfAttachment() {
        return this.pointOfAttachment;
    }
    
    boolean getPreprocessed() {
        return this.preprocessed;
    }
    
    void setParentComparisonOperator(final BinaryComparisonOperatorNode parentComparisonOperator) {
        this.parentComparisonOperator = parentComparisonOperator;
    }
    
    public boolean referencesSessionSchema() throws StandardException {
        return this.resultSet.referencesSessionSchema();
    }
    
    public ValueNode remapColumnReferencesToExpressions() throws StandardException {
        if (this.resultSet instanceof SelectNode) {
            final ResultColumnList resultColumns = this.resultSet.getResultColumns();
            final PredicateList wherePredicates = ((SelectNode)this.resultSet).getWherePredicates();
            resultColumns.remapColumnReferencesToExpressions();
            wherePredicates.remapColumnReferencesToExpressions();
        }
        return this;
    }
    
    public ValueNode bindExpression(final FromList list, final SubqueryList list2, final List list3) throws StandardException {
        this.checkReliability(32, "42Z91");
        final ResultColumnList resultColumns = this.resultSet.getResultColumns();
        if (this.subqueryType != 15 && resultColumns.visibleSize() != 1) {
            throw StandardException.newException("42X39");
        }
        this.resultSet.verifySelectStarSubquery(list, this.subqueryType);
        if (this.subqueryType == 15) {
            this.resultSet = this.resultSet.setResultToBooleanTrueNode(true);
        }
        final CompilerContext compilerContext = this.getCompilerContext();
        compilerContext.pushCurrentPrivType(0);
        this.resultSet = this.resultSet.bindNonVTITables(this.getDataDictionary(), list);
        this.resultSet = this.resultSet.bindVTITables(list);
        if (this.subqueryNumber == -1) {
            this.subqueryNumber = compilerContext.getNextSubqueryNumber();
        }
        this.resultSet.rejectParameters();
        if (this.subqueryType == 15) {
            this.resultSet.bindTargetExpressions(list);
            this.resultSet.bindUntypedNullsToResultColumns(null);
            this.resultSet = this.resultSet.setResultToBooleanTrueNode(false);
        }
        if (this.leftOperand != null) {
            this.leftOperand = this.leftOperand.bindExpression(list, list2, list3);
        }
        if (this.orderByList != null) {
            this.orderByList.pullUpOrderByColumns(this.resultSet);
        }
        this.resultSet.bindExpressions(list);
        this.resultSet.bindResultColumns(list);
        if (this.orderByList != null) {
            this.orderByList.bindOrderByColumns(this.resultSet);
        }
        QueryTreeNode.bindOffsetFetch(this.offset, this.fetchFirst);
        this.resultSet.bindUntypedNullsToResultColumns(null);
        final ResultColumnList resultColumns2 = this.resultSet.getResultColumns();
        if (this.leftOperand != null && this.leftOperand.requiresTypeFromContext()) {
            this.leftOperand.setType(((ResultColumn)resultColumns2.elementAt(0)).getTypeServices());
        }
        this.setDataTypeServices(resultColumns2);
        list2.addSubqueryNode(this);
        compilerContext.popCurrentPrivType();
        return this;
    }
    
    public ValueNode preprocess(final int n, final FromList list, final SubqueryList list2, final PredicateList list3) throws StandardException {
        if (this.preprocessed) {
            return this;
        }
        this.preprocessed = true;
        ValueNode rightOperand = this;
        this.resultSet = this.resultSet.preprocess(n, null, null);
        if (this.leftOperand != null) {
            this.leftOperand = this.leftOperand.preprocess(n, list, list2, list3);
        }
        if (this.resultSet instanceof SelectNode && ((SelectNode)this.resultSet).hasDistinct()) {
            ((SelectNode)this.resultSet).clearDistinct();
            if (this.subqueryType == 17) {
                this.distinctExpression = true;
            }
        }
        if ((this.isIN() || this.isANY()) && this.resultSet.returnsAtMostOneRow() && !this.hasCorrelatedCRs()) {
            this.changeToCorrespondingExpressionType();
        }
        if (this.resultSet instanceof RowResultSetNode && this.underTopAndNode && !this.havingSubquery && this.orderByList == null && this.offset == null && this.fetchFirst == null && !this.isWhereExistsAnyInWithWhereSubquery() && this.parentComparisonOperator != null) {
            this.leftOperand = this.parentComparisonOperator.getLeftOperand();
            final RowResultSetNode rowResultSetNode = (RowResultSetNode)this.resultSet;
            final FromList list4 = new FromList();
            list2.removeElement(this);
            if (rowResultSetNode.subquerys.size() != 0) {
                list4.addElement(rowResultSetNode);
                list.destructiveAppend(list4);
            }
            list2.destructiveAppend(rowResultSetNode.subquerys);
            return this.getNewJoinCondition(this.leftOperand, this.getRightOperand());
        }
        final boolean b = this.isNOT_EXISTS() || this.canAllBeFlattened();
        if (this.resultSet instanceof SelectNode && !((SelectNode)this.resultSet).hasWindows() && this.orderByList == null && this.offset == null && this.fetchFirst == null && this.underTopAndNode && !this.havingSubquery && !this.isWhereExistsAnyInWithWhereSubquery() && (this.isIN() || this.isANY() || this.isEXISTS() || b || this.parentComparisonOperator != null)) {
            final SelectNode selectNode = (SelectNode)this.resultSet;
            if (!selectNode.hasAggregatesInSelectList() && selectNode.havingClause == null) {
                final ValueNode leftOperand = this.leftOperand;
                final boolean b2 = (this.subqueryType == 1 || this.subqueryType == 3) && (this.leftOperand instanceof ConstantNode || this.leftOperand instanceof ColumnReference || this.leftOperand.requiresTypeFromContext());
                if (this.parentComparisonOperator != null) {
                    this.leftOperand = this.parentComparisonOperator.getLeftOperand();
                }
                if (!b && selectNode.uniqueSubquery(b2)) {
                    return this.flattenToNormalJoin(n, list, list2, list3);
                }
                Label_0682: {
                    if (this.isIN() || this.isANY() || this.isEXISTS() || b) {
                        if (this.leftOperand != null) {
                            if (!this.leftOperand.categorize(new JBitSet(n), false)) {
                                break Label_0682;
                            }
                        }
                        if (selectNode.getWherePredicates().allPushable()) {
                            final FromBaseTable singleFromBaseTable = this.singleFromBaseTable(selectNode.getFromList());
                            if (singleFromBaseTable != null && (!b || (selectNode.getWherePredicates().allReference(singleFromBaseTable) && this.rightOperandFlattenableToNotExists(n, singleFromBaseTable)))) {
                                return this.flattenToExistsJoin(n, list, list2, list3, b);
                            }
                        }
                    }
                }
                this.leftOperand = leftOperand;
            }
        }
        if (this.orderByList != null) {
            if (this.orderByList.size() > 1) {
                this.orderByList.removeDupColumns();
            }
            this.resultSet.pushOrderByList(this.orderByList);
            this.orderByList = null;
        }
        this.resultSet.pushOffsetFetchFirst(this.offset, this.fetchFirst, this.hasJDBClimitClause);
        if (this.leftOperand != null) {
            rightOperand = this.pushNewPredicate(n);
            this.pushedNewPredicate = true;
        }
        else if (this.isEXISTS() || this.isNOT_EXISTS()) {
            rightOperand = this.genIsNullTree(this.isEXISTS());
            this.subqueryType = 15;
        }
        this.isInvariant();
        this.hasCorrelatedCRs();
        if (this.parentComparisonOperator != null) {
            this.parentComparisonOperator.setRightOperand(rightOperand);
            return this.parentComparisonOperator;
        }
        return rightOperand;
    }
    
    private FromBaseTable singleFromBaseTable(final FromList list) {
        FromBaseTable fromBaseTable = null;
        if (list.size() == 1) {
            final FromTable fromTable = (FromTable)list.elementAt(0);
            if (fromTable instanceof FromBaseTable) {
                fromBaseTable = (FromBaseTable)fromTable;
            }
            else if (fromTable instanceof ProjectRestrictNode) {
                final ResultSetNode childResult = ((ProjectRestrictNode)fromTable).getChildResult();
                if (childResult instanceof FromBaseTable) {
                    fromBaseTable = (FromBaseTable)childResult;
                }
            }
        }
        return fromBaseTable;
    }
    
    private boolean rightOperandFlattenableToNotExists(final int n, final FromBaseTable fromBaseTable) throws StandardException {
        boolean value = true;
        if (this.leftOperand != null) {
            final JBitSet set = new JBitSet(n);
            this.getRightOperand().categorize(set, false);
            value = set.get(fromBaseTable.getTableNumber());
        }
        return value;
    }
    
    private boolean canAllBeFlattened() throws StandardException {
        boolean b = false;
        if (this.isNOT_IN() || this.isALL()) {
            b = (!this.leftOperand.getTypeServices().isNullable() && !this.getRightOperand().getTypeServices().isNullable());
        }
        return b;
    }
    
    private ValueNode flattenToNormalJoin(final int n, final FromList list, final SubqueryList list2, final PredicateList list3) throws StandardException {
        final SelectNode selectNode = (SelectNode)this.resultSet;
        final FromList fromList = selectNode.getFromList();
        final int[] tableNumbers = fromList.getTableNumbers();
        list2.removeElement(this);
        selectNode.decrementLevel(1);
        list.destructiveAppend(fromList);
        list3.destructiveAppend(selectNode.getWherePredicates());
        list2.destructiveAppend(selectNode.getWhereSubquerys());
        list2.destructiveAppend(selectNode.getSelectSubquerys());
        if (this.leftOperand == null) {
            return (ValueNode)this.getNodeFactory().getNode(38, Boolean.TRUE, this.getContextManager());
        }
        final ValueNode rightOperand = this.getRightOperand();
        if (rightOperand instanceof ColumnReference) {
            final ColumnReference columnReference = (ColumnReference)rightOperand;
            final int tableNumber = columnReference.getTableNumber();
            for (int i = 0; i < tableNumbers.length; ++i) {
                if (tableNumber == tableNumbers[i]) {
                    columnReference.setSourceLevel(columnReference.getSourceLevel() - 1);
                    break;
                }
            }
        }
        return this.getNewJoinCondition(this.leftOperand, rightOperand);
    }
    
    private ValueNode flattenToExistsJoin(final int n, final FromList list, final SubqueryList list2, final PredicateList list3, final boolean b) throws StandardException {
        ((SelectNode)this.resultSet).getFromList().genExistsBaseTables(this.resultSet.getReferencedTableMap(), list, b);
        return this.flattenToNormalJoin(n, list, list2, list3);
    }
    
    private ValueNode getRightOperand() {
        return ((ResultColumn)this.resultSet.getResultColumns().elementAt(0)).getExpression();
    }
    
    private boolean isInvariant() throws StandardException {
        if (this.doneInvariantCheck) {
            return !this.foundVariant;
        }
        this.doneInvariantCheck = true;
        final HasVariantValueNodeVisitor hasVariantValueNodeVisitor = new HasVariantValueNodeVisitor();
        this.resultSet.accept(hasVariantValueNodeVisitor);
        this.foundVariant = hasVariantValueNodeVisitor.hasVariant();
        return !this.foundVariant;
    }
    
    public boolean hasCorrelatedCRs() throws StandardException {
        if (this.doneCorrelationCheck) {
            return this.foundCorrelation;
        }
        this.doneCorrelationCheck = true;
        ResultSetNode resultSetNode = this.resultSet;
        ResultColumnList resultColumns = null;
        if (this.pushedNewPredicate) {
            resultSetNode = ((ProjectRestrictNode)this.resultSet).getChildResult();
            resultColumns = resultSetNode.getResultColumns();
            if (resultColumns.size() > 1) {
                final ResultColumnList resultColumns2 = new ResultColumnList();
                resultColumns2.addResultColumn(resultColumns.getResultColumn(1));
                resultSetNode.setResultColumns(resultColumns2);
            }
        }
        final HasCorrelatedCRsVisitor hasCorrelatedCRsVisitor = new HasCorrelatedCRsVisitor();
        resultSetNode.accept(hasCorrelatedCRsVisitor);
        this.foundCorrelation = hasCorrelatedCRsVisitor.hasCorrelatedCRs();
        if (this.pushedNewPredicate && resultColumns.size() > 1) {
            resultSetNode.setResultColumns(resultColumns);
        }
        return this.foundCorrelation;
    }
    
    private UnaryComparisonOperatorNode pushNewPredicate(final int n) throws StandardException {
        UnaryComparisonOperatorNode unaryComparisonOperatorNode = null;
        this.resultSet = this.resultSet.ensurePredicateList(n);
        final ResultColumnList resultColumns = this.resultSet.getResultColumns();
        final ResultColumnList copyListAndObjects = resultColumns.copyListAndObjects();
        copyListAndObjects.genVirtualColumnNodes(this.resultSet, resultColumns);
        this.resultSet = (ResultSetNode)this.getNodeFactory().getNode(151, this.resultSet, copyListAndObjects, null, null, null, null, null, this.getContextManager());
        final ResultColumn resultColumn = (ResultColumn)copyListAndObjects.elementAt(0);
        final ValueNode expression = resultColumn.getExpression();
        BinaryOperatorNode newJoinCondition;
        final BinaryComparisonOperatorNode binaryComparisonOperatorNode = (BinaryComparisonOperatorNode)(newJoinCondition = this.getNewJoinCondition(this.leftOperand, expression));
        if (this.isNOT_IN() || this.isALL()) {
            final boolean nullable = this.leftOperand.getTypeServices().isNullable();
            final boolean nullable2 = expression.getTypeServices().isNullable();
            if (nullable || nullable2) {
                final OrNode orNode = (OrNode)this.getNodeFactory().getNode(52, binaryComparisonOperatorNode, this.getNodeFactory().getNode(38, Boolean.FALSE, this.getContextManager()), this.getContextManager());
                orNode.postBindFixup();
                newJoinCondition = orNode;
                if (nullable) {
                    final UnaryComparisonOperatorNode unaryComparisonOperatorNode2 = (UnaryComparisonOperatorNode)this.getNodeFactory().getNode(25, this.leftOperand, this.getContextManager());
                    unaryComparisonOperatorNode2.bindComparisonOperator();
                    final OrNode orNode2 = (OrNode)this.getNodeFactory().getNode(52, unaryComparisonOperatorNode2, newJoinCondition, this.getContextManager());
                    orNode2.postBindFixup();
                    newJoinCondition = orNode2;
                }
                if (nullable2) {
                    final UnaryComparisonOperatorNode unaryComparisonOperatorNode3 = (UnaryComparisonOperatorNode)this.getNodeFactory().getNode(25, expression, this.getContextManager());
                    unaryComparisonOperatorNode3.bindComparisonOperator();
                    final OrNode orNode3 = (OrNode)this.getNodeFactory().getNode(52, unaryComparisonOperatorNode3, newJoinCondition, this.getContextManager());
                    orNode3.postBindFixup();
                    newJoinCondition = orNode3;
                }
            }
        }
        final AndNode andNode = (AndNode)this.getNodeFactory().getNode(39, newJoinCondition, this.getTrueNode(), this.getContextManager());
        final JBitSet set = new JBitSet(n);
        andNode.postBindFixup();
        final Predicate predicate = (Predicate)this.getNodeFactory().getNode(78, andNode, set, this.getContextManager());
        predicate.categorize();
        this.resultSet = this.resultSet.addNewPredicate(predicate);
        this.leftOperand = null;
        resultColumn.setType(this.getTypeServices());
        resultColumn.setExpression(this.getTrueNode());
        switch (this.subqueryType) {
            case 1:
            case 3:
            case 5:
            case 7:
            case 9:
            case 11:
            case 13: {
                unaryComparisonOperatorNode = (UnaryComparisonOperatorNode)this.getNodeFactory().getNode(24, this, this.getContextManager());
                break;
            }
            case 2:
            case 4:
            case 6:
            case 8:
            case 10:
            case 12:
            case 14: {
                unaryComparisonOperatorNode = (UnaryComparisonOperatorNode)this.getNodeFactory().getNode(25, this, this.getContextManager());
                break;
            }
        }
        unaryComparisonOperatorNode.bindComparisonOperator();
        return unaryComparisonOperatorNode;
    }
    
    private BinaryComparisonOperatorNode getNewJoinCondition(final ValueNode valueNode, final ValueNode valueNode2) throws StandardException {
        int subqueryType = this.subqueryType;
        if (this.subqueryType == 17) {
            int operator = -1;
            if (this.parentComparisonOperator.isRelationalOperator()) {
                operator = ((RelationalOperator)this.parentComparisonOperator).getOperator();
            }
            if (operator == 1) {
                subqueryType = 3;
            }
            else if (operator == 2) {
                subqueryType = 5;
            }
            else if (operator == 6) {
                subqueryType = 13;
            }
            else if (operator == 5) {
                subqueryType = 11;
            }
            else if (operator == 4) {
                subqueryType = 9;
            }
            else if (operator == 3) {
                subqueryType = 7;
            }
        }
        int n = 0;
        switch (subqueryType) {
            case 1:
            case 2:
            case 3:
            case 6: {
                n = 41;
                break;
            }
            case 4:
            case 5: {
                n = 47;
                break;
            }
            case 8:
            case 13: {
                n = 44;
                break;
            }
            case 10:
            case 11: {
                n = 45;
                break;
            }
            case 9:
            case 12: {
                n = 42;
                break;
            }
            case 7:
            case 14: {
                n = 43;
                break;
            }
        }
        final BinaryComparisonOperatorNode binaryComparisonOperatorNode = (BinaryComparisonOperatorNode)this.getNodeFactory().getNode(n, valueNode, valueNode2, Boolean.FALSE, this.getContextManager());
        binaryComparisonOperatorNode.bindComparisonOperator();
        return binaryComparisonOperatorNode;
    }
    
    ValueNode eliminateNots(final boolean b) throws StandardException {
        ValueNode genEqualsFalseTree = this;
        if (b) {
            switch (this.subqueryType) {
                case 17: {
                    genEqualsFalseTree = this.genEqualsFalseTree();
                    break;
                }
                case 15: {
                    this.subqueryType = 16;
                    break;
                }
                case 1:
                case 3: {
                    this.subqueryType = 2;
                    break;
                }
                case 5: {
                    this.subqueryType = 4;
                    break;
                }
                case 9: {
                    this.subqueryType = 12;
                    break;
                }
                case 7: {
                    this.subqueryType = 14;
                    break;
                }
                case 13: {
                    this.subqueryType = 8;
                    break;
                }
                case 11: {
                    this.subqueryType = 10;
                    break;
                }
                case 4: {
                    this.subqueryType = 5;
                    break;
                }
                case 6: {
                    this.subqueryType = 3;
                    break;
                }
                case 10: {
                    this.subqueryType = 11;
                    break;
                }
                case 8: {
                    this.subqueryType = 13;
                    break;
                }
                case 14: {
                    this.subqueryType = 7;
                    break;
                }
                case 12: {
                    this.subqueryType = 9;
                    break;
                }
            }
        }
        return genEqualsFalseTree;
    }
    
    public ValueNode changeToCNF(final boolean underTopAndNode) throws StandardException {
        this.underTopAndNode = underTopAndNode;
        return this;
    }
    
    public boolean categorize(final JBitSet set, final boolean b) throws StandardException {
        return !b && this.isMaterializable();
    }
    
    boolean isMaterializable() throws StandardException {
        final boolean b = this.subqueryType == 17 && !this.hasCorrelatedCRs() && this.isInvariant();
        if (b && this.resultSet instanceof SelectNode) {
            ((SelectNode)this.resultSet).getFromList().setLevel(0);
        }
        return b;
    }
    
    public void optimize(final DataDictionary dataDictionary, final double n) throws StandardException {
        this.resultSet = this.resultSet.optimize(dataDictionary, null, n);
    }
    
    public void modifyAccessPaths() throws StandardException {
        this.resultSet = this.resultSet.modifyAccessPaths();
    }
    
    protected int getOrderableVariantType() throws StandardException {
        if (!this.isInvariant()) {
            return 0;
        }
        if (!this.hasCorrelatedCRs() && this.subqueryType == 17) {
            return 2;
        }
        return 1;
    }
    
    public void generateExpression(final ExpressionClassBuilder expressionClassBuilder, final MethodBuilder methodBuilder) throws StandardException {
        final CompilerContext compilerContext = this.getCompilerContext();
        final ActivationClassBuilder activationClassBuilder = (ActivationClassBuilder)expressionClassBuilder;
        String s;
        if (this.subqueryType == 17) {
            s = "getOnceResultSet";
        }
        else {
            s = "getAnyResultSet";
        }
        final CostEstimate finalCostEstimate = this.resultSet.getFinalCostEstimate();
        final String interfaceName = this.getTypeCompiler().interfaceName();
        final MethodBuilder generatedFun = activationClassBuilder.newGeneratedFun(interfaceName, 4);
        final LocalField fieldDeclaration = activationClassBuilder.newFieldDeclaration(2, "org.apache.derby.iapi.sql.execute.NoPutResultSet");
        ResultSetNode childResult = null;
        if (!this.isMaterializable()) {
            final MethodBuilder executeMethod = activationClassBuilder.getExecuteMethod();
            if (this.pushedNewPredicate && !this.hasCorrelatedCRs()) {
                childResult = ((ProjectRestrictNode)this.resultSet).getChildResult();
                final LocalField fieldDeclaration2 = activationClassBuilder.newFieldDeclaration(2, "org.apache.derby.iapi.sql.execute.NoPutResultSet");
                generatedFun.getField(fieldDeclaration2);
                generatedFun.conditionalIfNull();
                final MaterializeSubqueryNode childResult2 = new MaterializeSubqueryNode(fieldDeclaration2);
                childResult2.costEstimate = this.resultSet.getFinalCostEstimate();
                ((ProjectRestrictNode)this.resultSet).setChildResult(childResult2);
                childResult.generate(activationClassBuilder, generatedFun);
                generatedFun.startElseCode();
                generatedFun.getField(fieldDeclaration2);
                generatedFun.completeConditional();
                generatedFun.setField(fieldDeclaration2);
                executeMethod.pushNull("org.apache.derby.iapi.sql.execute.NoPutResultSet");
                executeMethod.setField(fieldDeclaration2);
            }
            executeMethod.pushNull("org.apache.derby.iapi.sql.execute.NoPutResultSet");
            executeMethod.setField(fieldDeclaration);
            generatedFun.getField(fieldDeclaration);
            generatedFun.conditionalIfNull();
        }
        activationClassBuilder.pushGetResultSetFactoryExpression(generatedFun);
        this.resultSet.generate(activationClassBuilder, generatedFun);
        final int nextResultSetNumber = compilerContext.getNextResultSetNumber();
        this.resultSet.getResultColumns().setResultSetNumber(nextResultSetNumber);
        this.resultSet.getResultColumns().generateNulls(activationClassBuilder, generatedFun);
        int n2;
        if (this.subqueryType == 17) {
            int n;
            if (this.distinctExpression) {
                n = 3;
            }
            else if (this.resultSet.returnsAtMostOneRow()) {
                n = 2;
            }
            else {
                n = 1;
            }
            generatedFun.push(n);
            n2 = 8;
        }
        else {
            n2 = 7;
        }
        generatedFun.push(nextResultSetNumber);
        generatedFun.push(this.subqueryNumber);
        generatedFun.push(this.pointOfAttachment);
        generatedFun.push(finalCostEstimate.rowCount());
        generatedFun.push(finalCostEstimate.getEstimatedCost());
        generatedFun.callMethod((short)185, null, s, "org.apache.derby.iapi.sql.execute.NoPutResultSet", n2);
        if (!this.isMaterializable()) {
            if (this.pushedNewPredicate && !this.hasCorrelatedCRs()) {
                ((ProjectRestrictNode)this.resultSet).setChildResult(childResult);
            }
            generatedFun.startElseCode();
            generatedFun.getField(fieldDeclaration);
            generatedFun.completeConditional();
        }
        generatedFun.setField(fieldDeclaration);
        generatedFun.getField(fieldDeclaration);
        generatedFun.callMethod((short)185, null, "openCore", "void", 0);
        generatedFun.getField(fieldDeclaration);
        generatedFun.callMethod((short)185, null, "getNextRowCore", "org.apache.derby.iapi.sql.execute.ExecRow", 0);
        generatedFun.push(1);
        generatedFun.callMethod((short)185, "org.apache.derby.iapi.sql.Row", "getColumn", "org.apache.derby.iapi.types.DataValueDescriptor", 1);
        generatedFun.cast(interfaceName);
        if (this.isMaterializable()) {
            generatedFun.getField(fieldDeclaration);
            generatedFun.callMethod((short)185, "org.apache.derby.iapi.sql.ResultSet", "close", "void", 0);
        }
        generatedFun.methodReturn();
        generatedFun.complete();
        if (this.isMaterializable()) {
            methodBuilder.getField(this.generateMaterialization(activationClassBuilder, generatedFun, interfaceName));
        }
        else {
            methodBuilder.pushThis();
            methodBuilder.callMethod((short)182, null, generatedFun.getName(), interfaceName, 0);
        }
    }
    
    private LocalField generateMaterialization(final ActivationClassBuilder activationClassBuilder, final MethodBuilder methodBuilder, final String s) {
        final MethodBuilder executeMethod = activationClassBuilder.getExecuteMethod();
        final LocalField fieldDeclaration = activationClassBuilder.newFieldDeclaration(2, s);
        executeMethod.pushThis();
        executeMethod.callMethod((short)182, null, methodBuilder.getName(), s, 0);
        executeMethod.setField(fieldDeclaration);
        return fieldDeclaration;
    }
    
    private BooleanConstantNode getTrueNode() throws StandardException {
        if (this.trueNode == null) {
            this.trueNode = (BooleanConstantNode)this.getNodeFactory().getNode(38, Boolean.TRUE, this.getContextManager());
        }
        return this.trueNode;
    }
    
    void acceptChildren(final Visitor visitor) throws StandardException {
        super.acceptChildren(visitor);
        if (visitor instanceof HasCorrelatedCRsVisitor && this.doneCorrelationCheck) {
            ((HasCorrelatedCRsVisitor)visitor).setHasCorrelatedCRs(this.foundCorrelation);
            return;
        }
        if (this.resultSet != null) {
            this.resultSet = (ResultSetNode)this.resultSet.accept(visitor);
        }
        if (this.leftOperand != null) {
            this.leftOperand = (ValueNode)this.leftOperand.accept(visitor);
        }
    }
    
    private boolean isIN() {
        return this.subqueryType == 1;
    }
    
    private boolean isNOT_IN() {
        return this.subqueryType == 2;
    }
    
    private boolean isANY() {
        switch (this.subqueryType) {
            case 3:
            case 5:
            case 7:
            case 9:
            case 11:
            case 13: {
                return true;
            }
            default: {
                return false;
            }
        }
    }
    
    private boolean isALL() {
        switch (this.subqueryType) {
            case 4:
            case 6:
            case 8:
            case 10:
            case 12:
            case 14: {
                return true;
            }
            default: {
                return false;
            }
        }
    }
    
    private boolean isEXISTS() {
        return this.subqueryType == 15;
    }
    
    private boolean isNOT_EXISTS() {
        return this.subqueryType == 16;
    }
    
    private void changeToCorrespondingExpressionType() throws StandardException {
        BinaryOperatorNode binaryOperatorNode = null;
        switch (this.subqueryType) {
            case 1:
            case 3: {
                binaryOperatorNode = (BinaryOperatorNode)this.getNodeFactory().getNode(41, this.leftOperand, this, Boolean.FALSE, this.getContextManager());
                break;
            }
            case 5: {
                binaryOperatorNode = (BinaryOperatorNode)this.getNodeFactory().getNode(47, this.leftOperand, this, Boolean.FALSE, this.getContextManager());
                break;
            }
            case 13: {
                binaryOperatorNode = (BinaryOperatorNode)this.getNodeFactory().getNode(44, this.leftOperand, this, Boolean.FALSE, this.getContextManager());
                break;
            }
            case 11: {
                binaryOperatorNode = (BinaryOperatorNode)this.getNodeFactory().getNode(45, this.leftOperand, this, Boolean.FALSE, this.getContextManager());
                break;
            }
            case 9: {
                binaryOperatorNode = (BinaryOperatorNode)this.getNodeFactory().getNode(42, this.leftOperand, this, Boolean.FALSE, this.getContextManager());
                break;
            }
            case 7: {
                binaryOperatorNode = (BinaryOperatorNode)this.getNodeFactory().getNode(43, this.leftOperand, this, Boolean.FALSE, this.getContextManager());
                break;
            }
        }
        this.subqueryType = 17;
        this.setDataTypeServices(this.resultSet.getResultColumns());
        (this.parentComparisonOperator = (BinaryComparisonOperatorNode)binaryOperatorNode).bindComparisonOperator();
        this.leftOperand = null;
    }
    
    private void setDataTypeServices(final ResultColumnList list) throws StandardException {
        DataTypeDescriptor dataTypeDescriptor;
        if (this.subqueryType == 17) {
            dataTypeDescriptor = ((ResultColumn)list.elementAt(0)).getTypeServices();
        }
        else {
            dataTypeDescriptor = this.getTrueNode().getTypeServices();
        }
        this.setType(dataTypeDescriptor.getNullabilityType(true));
    }
    
    protected boolean isEquivalent(final ValueNode valueNode) {
        return false;
    }
    
    public boolean isHavingSubquery() {
        return this.havingSubquery;
    }
    
    public void setHavingSubquery(final boolean havingSubquery) {
        this.havingSubquery = havingSubquery;
    }
    
    public boolean isWhereSubquery() {
        return this.whereSubquery;
    }
    
    public void setWhereSubquery(final boolean whereSubquery) {
        this.whereSubquery = whereSubquery;
    }
    
    public boolean isWhereExistsAnyInWithWhereSubquery() throws StandardException {
        return this.isWhereSubquery() && (this.isEXISTS() || this.isANY() || this.isIN()) && (this.resultSet instanceof SelectNode && ((SelectNode)this.resultSet).originalWhereClauseHadSubqueries);
    }
    
    public OrderByList getOrderByList() {
        return this.orderByList;
    }
    
    public ValueNode getOffset() {
        return this.offset;
    }
    
    public ValueNode getFetchFirst() {
        return this.fetchFirst;
    }
    
    public boolean hasJDBClimitClause() {
        return this.hasJDBClimitClause;
    }
}
