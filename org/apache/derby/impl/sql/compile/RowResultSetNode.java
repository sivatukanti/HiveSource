// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.impl.sql.compile;

import org.apache.derby.iapi.sql.compile.Visitor;
import org.apache.derby.iapi.sql.dictionary.TableDescriptor;
import org.apache.derby.iapi.services.compiler.MethodBuilder;
import org.apache.derby.iapi.types.TypeId;
import org.apache.derby.iapi.types.DataTypeDescriptor;
import org.apache.derby.iapi.sql.compile.OptimizableList;
import org.apache.derby.iapi.sql.compile.RequiredRowOrdering;
import org.apache.derby.iapi.util.JBitSet;
import java.util.ArrayList;
import org.apache.derby.iapi.sql.dictionary.DataDictionary;
import org.apache.derby.iapi.sql.compile.Optimizable;
import org.apache.derby.iapi.sql.compile.RowOrdering;
import org.apache.derby.iapi.sql.compile.Optimizer;
import org.apache.derby.iapi.sql.compile.CostEstimate;
import org.apache.derby.iapi.sql.dictionary.ConglomerateDescriptor;
import org.apache.derby.iapi.sql.compile.OptimizablePredicateList;
import org.apache.derby.iapi.error.StandardException;
import java.util.List;

public class RowResultSetNode extends FromTable
{
    SubqueryList subquerys;
    private List aggregateVector;
    OrderByList orderByList;
    ValueNode offset;
    ValueNode fetchFirst;
    boolean hasJDBClimitClause;
    
    public void init(final Object o, final Object o2) {
        super.init(null, o2);
        this.resultColumns = (ResultColumnList)o;
        if (this.resultColumns != null) {
            this.resultColumns.markInitialSize();
        }
    }
    
    public String toString() {
        return "";
    }
    
    public String statementToString() {
        return "VALUES";
    }
    
    public void printSubNodes(final int n) {
    }
    
    public boolean referencesSessionSchema() throws StandardException {
        return this.subquerys != null && this.subquerys.referencesSessionSchema();
    }
    
    ResultSetNode enhanceRCLForInsert(final InsertNode insertNode, final boolean b, final int[] array) throws StandardException {
        if (!b || this.resultColumns.size() < insertNode.resultColumnList.size()) {
            this.resultColumns = this.getRCLForInsert(insertNode, array);
        }
        return this;
    }
    
    public CostEstimate estimateCost(final OptimizablePredicateList list, final ConglomerateDescriptor conglomerateDescriptor, final CostEstimate costEstimate, final Optimizer optimizer, final RowOrdering rowOrdering) throws StandardException {
        if (this.costEstimate == null) {
            this.costEstimate = optimizer.newCostEstimate();
        }
        this.costEstimate.setCost(0.0, 1.0, 1.0);
        rowOrdering.optimizableAlwaysOrdered(this);
        return this.costEstimate;
    }
    
    public ResultSetNode bindNonVTITables(final DataDictionary dataDictionary, final FromList list) throws StandardException {
        if (this.tableNumber == -1) {
            this.tableNumber = this.getCompilerContext().getNextTableNumber();
        }
        return this;
    }
    
    public void bindExpressions(final FromList list) throws StandardException {
        this.subquerys = (SubqueryList)this.getNodeFactory().getNode(11, this.getContextManager());
        this.aggregateVector = new ArrayList();
        this.resultColumns.checkForInvalidDefaults();
        int level;
        if (list.size() == 0) {
            level = 0;
        }
        else {
            level = ((FromTable)list.elementAt(0)).getLevel() + 1;
        }
        this.setLevel(level);
        list.insertElementAt(this, 0);
        this.resultColumns.bindExpressions(list, this.subquerys, this.aggregateVector);
        list.removeElementAt(0);
        if (!this.aggregateVector.isEmpty()) {
            throw StandardException.newException("42903");
        }
        SelectNode.checkNoWindowFunctions(this.resultColumns, "VALUES");
        if (this.orderByList != null) {
            this.orderByList.pullUpOrderByColumns(this);
            this.orderByList.bindOrderByColumns(this);
        }
        QueryTreeNode.bindOffsetFetch(this.offset, this.fetchFirst);
    }
    
    public void bindExpressionsWithTables(final FromList list) throws StandardException {
    }
    
    public void bindTargetExpressions(final FromList list) throws StandardException {
        this.bindExpressions(list);
    }
    
    public void bindUntypedNullsToResultColumns(ResultColumnList resultColumns) throws StandardException {
        if (resultColumns == null) {
            resultColumns = this.resultColumns;
        }
        this.resultColumns.bindUntypedNullsToResultColumns(resultColumns);
    }
    
    public ResultColumn getMatchingColumn(final ColumnReference columnReference) throws StandardException {
        return null;
    }
    
    public String getExposedName() throws StandardException {
        return null;
    }
    
    public void verifySelectStarSubquery(final FromList list, final int n) throws StandardException {
    }
    
    void pushOrderByList(final OrderByList orderByList) {
        this.orderByList = orderByList;
    }
    
    void pushOffsetFetchFirst(final ValueNode offset, final ValueNode fetchFirst, final boolean hasJDBClimitClause) {
        this.offset = offset;
        this.fetchFirst = fetchFirst;
        this.hasJDBClimitClause = hasJDBClimitClause;
    }
    
    public ResultSetNode preprocess(final int n, final GroupByList list, final FromList list2) throws StandardException {
        this.getResultColumns().preprocess(n, list2, this.subquerys, (PredicateList)this.getNodeFactory().getNode(8, this.getContextManager()));
        (this.referencedTableMap = new JBitSet(n)).set(this.tableNumber);
        if (this.orderByList != null && this.orderByList.size() > 1) {
            this.orderByList.removeDupColumns();
        }
        return this;
    }
    
    public ResultSetNode ensurePredicateList(final int n) throws StandardException {
        return this.genProjectRestrict(n);
    }
    
    public ResultSetNode addNewPredicate(final Predicate predicate) throws StandardException {
        final ResultColumnList resultColumns = this.resultColumns;
        resultColumns.genVirtualColumnNodes(this, this.resultColumns = this.resultColumns.copyListAndObjects());
        final PredicateList list = (PredicateList)this.getNodeFactory().getNode(8, this.getContextManager());
        list.addPredicate(predicate);
        return (ResultSetNode)this.getNodeFactory().getNode(151, this, resultColumns, null, list, null, null, this.tableProperties, this.getContextManager());
    }
    
    public boolean flattenableInFromSubquery(final FromList list) {
        if (this.subquerys != null && this.subquerys.size() > 0) {
            return false;
        }
        if (this.aggregateVector != null && this.aggregateVector.size() > 0) {
            return false;
        }
        if (!this.resultColumns.isCloneable()) {
            return false;
        }
        boolean b = false;
        for (int size = list.size(), i = 0; i < size; ++i) {
            final FromTable fromTable = (FromTable)list.elementAt(i);
            if (!(fromTable instanceof FromSubquery)) {
                b = true;
                break;
            }
            if (!(((FromSubquery)fromTable).getSubquery() instanceof RowResultSetNode)) {
                b = true;
                break;
            }
        }
        return b;
    }
    
    public ResultSetNode optimize(final DataDictionary dataDictionary, final PredicateList list, final double n) throws StandardException {
        (this.costEstimate = this.getOptimizer((OptimizableList)this.getNodeFactory().getNode(37, this.getNodeFactory().doJoinOrderOptimization(), this.getContextManager()), list, dataDictionary, null).newCostEstimate()).setCost(0.0, n, n);
        this.subquerys.optimize(dataDictionary, n);
        return this;
    }
    
    public Optimizable modifyAccessPath(final JBitSet set) throws StandardException {
        return (Optimizable)this.modifyAccessPaths();
    }
    
    public ResultSetNode modifyAccessPaths() throws StandardException {
        ResultSetNode resultSetNode = this;
        this.subquerys.modifyAccessPaths();
        if (this.orderByList != null) {
            resultSetNode = (ResultSetNode)this.getNodeFactory().getNode(140, resultSetNode, this.orderByList, this.tableProperties, this.getContextManager());
        }
        if (this.offset != null || this.fetchFirst != null) {
            final ResultColumnList copyListAndObjects = resultSetNode.getResultColumns().copyListAndObjects();
            copyListAndObjects.genVirtualColumnNodes(resultSetNode, resultSetNode.getResultColumns());
            resultSetNode = (ResultSetNode)this.getNodeFactory().getNode(223, resultSetNode, copyListAndObjects, this.offset, this.fetchFirst, this.hasJDBClimitClause, this.getContextManager());
        }
        return resultSetNode;
    }
    
    boolean returnsAtMostOneRow() {
        return true;
    }
    
    void setTableConstructorTypes(final ResultColumnList list) throws StandardException {
        for (int size = this.resultColumns.size(), i = 0; i < size; ++i) {
            final ValueNode expression = ((ResultColumn)this.resultColumns.elementAt(i)).getExpression();
            if (expression.requiresTypeFromContext()) {
                expression.setType(((ResultColumn)list.elementAt(i)).getTypeServices());
            }
            else if (expression instanceof CharConstantNode) {
                final ResultColumn resultColumn = (ResultColumn)list.elementAt(i);
                final TypeId typeId = resultColumn.getTypeId();
                if (typeId.isStringTypeId()) {
                    if (typeId.getJDBCTypeId() != 1) {
                        expression.setType(new DataTypeDescriptor(typeId, true, expression.getTypeServices().getMaximumWidth()));
                    }
                }
                else if (typeId.isBitTypeId()) {
                    if (typeId.getJDBCTypeId() == -3) {
                        expression.setType(new DataTypeDescriptor(TypeId.getBuiltInTypeId(12), true));
                        list.setElementAt(resultColumn, i);
                    }
                    else if (typeId.getJDBCTypeId() == -4) {
                        expression.setType(new DataTypeDescriptor(TypeId.getBuiltInTypeId(-1), true));
                        list.setElementAt(resultColumn, i);
                    }
                }
            }
            else if (expression instanceof BitConstantNode) {
                final ResultColumn resultColumn2 = (ResultColumn)list.elementAt(i);
                final TypeId typeId2 = resultColumn2.getTypeId();
                if (typeId2.isBitTypeId()) {
                    if (typeId2.getJDBCTypeId() != -2 && typeId2.getJDBCTypeId() != 2004) {
                        expression.setType(new DataTypeDescriptor(typeId2, true, expression.getTypeServices().getMaximumWidth()));
                    }
                }
                else if (typeId2.isStringTypeId()) {
                    if (typeId2.getJDBCTypeId() == 12) {
                        expression.setType(new DataTypeDescriptor(TypeId.getBuiltInTypeId(-3), true));
                        list.setElementAt(resultColumn2, i);
                    }
                    else if (typeId2.getJDBCTypeId() == -1) {
                        expression.setType(new DataTypeDescriptor(TypeId.getBuiltInTypeId(-4), true));
                        list.setElementAt(resultColumn2, i);
                    }
                }
            }
        }
    }
    
    public void generate(final ActivationClassBuilder activationClassBuilder, final MethodBuilder methodBuilder) throws StandardException {
        this.costEstimate = this.getFinalCostEstimate();
        final boolean canWeCacheResults = this.canWeCacheResults();
        this.assignResultSetNumber();
        activationClassBuilder.pushGetResultSetFactoryExpression(methodBuilder);
        activationClassBuilder.pushThisAsActivation(methodBuilder);
        this.resultColumns.generate(activationClassBuilder, methodBuilder);
        methodBuilder.push(canWeCacheResults);
        methodBuilder.push(this.resultSetNumber);
        methodBuilder.push(this.costEstimate.rowCount());
        methodBuilder.push(this.costEstimate.getEstimatedCost());
        methodBuilder.callMethod((short)185, null, "getRowResultSet", "org.apache.derby.iapi.sql.execute.NoPutResultSet", 6);
    }
    
    void replaceOrForbidDefaults(final TableDescriptor tableDescriptor, final ResultColumnList list, final boolean b) throws StandardException {
        this.resultColumns.replaceOrForbidDefaults(tableDescriptor, list, b);
    }
    
    void optimizeSubqueries(final DataDictionary dataDictionary, final double n) throws StandardException {
        this.subquerys.optimize(dataDictionary, n);
    }
    
    void adjustForSortElimination() {
    }
    
    private boolean canWeCacheResults() throws StandardException {
        final HasVariantValueNodeVisitor hasVariantValueNodeVisitor = new HasVariantValueNodeVisitor(2, true);
        super.accept(hasVariantValueNodeVisitor);
        return !hasVariantValueNodeVisitor.hasVariant();
    }
}
