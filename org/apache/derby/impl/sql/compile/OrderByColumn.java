// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.impl.sql.compile;

import java.util.Iterator;
import org.apache.derby.iapi.sql.compile.Visitor;
import org.apache.derby.iapi.error.StandardException;

public class OrderByColumn extends OrderedColumn
{
    private ResultColumn resultCol;
    private boolean ascending;
    private boolean nullsOrderedLow;
    private ValueNode expression;
    private OrderByList list;
    private int addedColumnOffset;
    
    public OrderByColumn() {
        this.ascending = true;
        this.nullsOrderedLow = false;
        this.addedColumnOffset = -1;
    }
    
    public void init(final Object o) {
        this.expression = (ValueNode)o;
    }
    
    public String toString() {
        return "";
    }
    
    public void printSubNodes(final int n) {
    }
    
    public void setDescending() {
        this.ascending = false;
    }
    
    public boolean isAscending() {
        return this.ascending;
    }
    
    public void setNullsOrderedLow() {
        this.nullsOrderedLow = true;
    }
    
    public boolean isNullsOrderedLow() {
        return this.nullsOrderedLow;
    }
    
    ResultColumn getResultColumn() {
        return this.resultCol;
    }
    
    ValueNode getNonRedundantExpression() {
        ColumnReference columnReference;
        ResultColumn resultColumn;
        ValueNode expression;
        for (columnReference = null, resultColumn = this.resultCol; resultColumn.isRedundant(); resultColumn = columnReference.getSource()) {
            expression = resultColumn.getExpression();
            if (expression instanceof ColumnReference) {
                columnReference = (ColumnReference)expression;
            }
        }
        return resultColumn.getExpression();
    }
    
    public void bindOrderByColumn(final ResultSetNode resultSetNode, final OrderByList list) throws StandardException {
        this.list = list;
        if (this.expression instanceof ColumnReference) {
            final ColumnReference columnReference = (ColumnReference)this.expression;
            this.resultCol = this.resolveColumnReference(resultSetNode, columnReference);
            this.columnPosition = this.resultCol.getColumnPosition();
            if (this.addedColumnOffset >= 0 && resultSetNode instanceof SelectNode && ((SelectNode)resultSetNode).hasDistinct()) {
                throw StandardException.newException("42879", columnReference.columnName);
            }
        }
        else if (isReferedColByNum(this.expression)) {
            final ResultColumnList resultColumns = resultSetNode.getResultColumns();
            this.columnPosition = (int)this.expression.getConstantValueAsObject();
            this.resultCol = resultColumns.getOrderByColumn(this.columnPosition);
            if (this.resultCol == null || this.resultCol.getColumnPosition() > resultColumns.visibleSize()) {
                throw StandardException.newException("42X77", String.valueOf(this.columnPosition));
            }
        }
        else {
            if (this.list.isTableValueCtorOrdering()) {
                throw StandardException.newException("4287B");
            }
            if (this.addedColumnOffset >= 0 && resultSetNode instanceof SelectNode && ((SelectNode)resultSetNode).hasDistinct() && !this.expressionMatch(resultSetNode)) {
                final CollectNodesVisitor collectNodesVisitor = new CollectNodesVisitor(ColumnReference.class);
                this.expression.accept(collectNodesVisitor);
                for (final ColumnReference columnReference2 : collectNodesVisitor.getList()) {
                    final String columnName = columnReference2.getColumnName();
                    if (!this.columnMatchFound(resultSetNode, columnReference2)) {
                        throw StandardException.newException("42879", columnName);
                    }
                }
            }
            this.resolveAddedColumn(resultSetNode);
            if (this.resultCol == null) {
                throw StandardException.newException("42878");
            }
        }
        this.resultCol.verifyOrderable();
    }
    
    private boolean expressionMatch(final ResultSetNode resultSetNode) throws StandardException {
        final ResultColumnList resultColumns = resultSetNode.getResultColumns();
        for (int i = 1; i <= resultColumns.visibleSize(); ++i) {
            if (resultColumns.getResultColumn(i).isEquivalent(this.resultCol)) {
                return true;
            }
        }
        return false;
    }
    
    private boolean columnMatchFound(final ResultSetNode resultSetNode, final ColumnReference columnReference) throws StandardException {
        final ResultColumnList resultColumns = resultSetNode.getResultColumns();
        for (int i = 1; i <= resultColumns.visibleSize(); ++i) {
            final ValueNode expression = resultColumns.getResultColumn(i).getExpression();
            if (expression instanceof ColumnReference && columnReference.isEquivalent(expression)) {
                return true;
            }
        }
        return false;
    }
    
    private void resolveAddedColumn(final ResultSetNode resultSetNode) {
        final ResultColumnList resultColumns = resultSetNode.getResultColumns();
        this.columnPosition = resultColumns.visibleSize() + this.addedColumnOffset + 1;
        this.resultCol = resultColumns.getResultColumn(this.columnPosition);
    }
    
    public void pullUpOrderByColumn(final ResultSetNode resultSetNode) throws StandardException {
        final ResultColumnList resultColumns = resultSetNode.getResultColumns();
        if (this.expression instanceof ColumnReference) {
            final ColumnReference columnReference = (ColumnReference)this.expression;
            this.resultCol = resultColumns.findResultColumnForOrderBy(columnReference.getColumnName(), columnReference.getTableNameNode());
            if (this.resultCol == null) {
                resultColumns.addResultColumn(this.resultCol = (ResultColumn)this.getNodeFactory().getNode(80, columnReference.getColumnName(), columnReference, this.getContextManager()));
                this.addedColumnOffset = resultColumns.getOrderBySelect();
                resultColumns.incOrderBySelect();
            }
        }
        else if (!isReferedColByNum(this.expression)) {
            resultColumns.addResultColumn(this.resultCol = (ResultColumn)this.getNodeFactory().getNode(80, null, this.expression, this.getContextManager()));
            this.addedColumnOffset = resultColumns.getOrderBySelect();
            resultColumns.incOrderBySelect();
        }
    }
    
    void resetToSourceRC() {
        this.resultCol = this.resultCol.getExpression().getSourceResultColumn();
    }
    
    boolean constantColumn(final PredicateList list) {
        return this.resultCol.getExpression().constantExpression(list);
    }
    
    void remapColumnReferencesToExpressions() throws StandardException {
        this.resultCol.setExpression(this.resultCol.getExpression().remapColumnReferencesToExpressions());
    }
    
    private static boolean isReferedColByNum(final ValueNode valueNode) throws StandardException {
        return valueNode instanceof NumericConstantNode && valueNode.getConstantValueAsObject() instanceof Integer;
    }
    
    private ResultColumn resolveColumnReference(final ResultSetNode resultSetNode, final ColumnReference columnReference) throws StandardException {
        int n = -1;
        if (resultSetNode instanceof SetOperatorNode && columnReference.getTableName() != null) {
            throw StandardException.newException("42877", columnReference.getSQLColumnName());
        }
        if (columnReference.getTableNameNode() != null) {
            final TableName tableNameNode = columnReference.getTableNameNode();
            FromTable fromTable = resultSetNode.getFromTableByName(tableNameNode.getTableName(), tableNameNode.hasSchema() ? tableNameNode.getSchemaName() : null, true);
            if (fromTable == null) {
                fromTable = resultSetNode.getFromTableByName(tableNameNode.getTableName(), tableNameNode.hasSchema() ? tableNameNode.getSchemaName() : null, false);
                if (fromTable == null) {
                    throw StandardException.newException("42X10", columnReference.getTableNameNode().toString());
                }
            }
            if (resultSetNode instanceof SetOperatorNode) {
                n = ((FromTable)resultSetNode).getTableNumber();
            }
            else {
                n = fromTable.getTableNumber();
            }
        }
        final ResultColumn orderByColumnToBind = resultSetNode.getResultColumns().getOrderByColumnToBind(columnReference.getColumnName(), columnReference.getTableNameNode(), n, this);
        if (orderByColumnToBind == null && this.addedColumnOffset >= 0) {
            this.resolveAddedColumn(resultSetNode);
        }
        if (orderByColumnToBind == null || orderByColumnToBind.isNameGenerated()) {
            throw StandardException.newException("42X78", columnReference.columnName);
        }
        return orderByColumnToBind;
    }
    
    void clearAddedColumnOffset() {
        this.list.closeGap(this.addedColumnOffset);
        this.addedColumnOffset = -1;
    }
    
    void collapseAddedColumnGap(final int n) {
        if (this.addedColumnOffset > n) {
            --this.addedColumnOffset;
        }
    }
    
    void acceptChildren(final Visitor visitor) throws StandardException {
        super.acceptChildren(visitor);
        if (this.expression != null) {
            this.expression = (ValueNode)this.expression.accept(visitor);
        }
    }
    
    ValueNode getExpression() {
        return this.expression;
    }
}
