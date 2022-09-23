// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.impl.sql.compile;

import org.apache.derby.iapi.sql.compile.Visitor;
import org.apache.derby.iapi.types.DataValueDescriptor;
import org.apache.derby.iapi.sql.compile.Optimizable;
import org.apache.derby.iapi.util.JBitSet;
import org.apache.derby.iapi.sql.compile.OptimizableList;
import org.apache.derby.iapi.sql.compile.RowOrdering;
import org.apache.derby.iapi.sql.compile.CostEstimate;
import org.apache.derby.iapi.sql.compile.CompilerContext;
import org.apache.derby.iapi.services.compiler.MethodBuilder;
import org.apache.derby.iapi.error.StandardException;
import org.apache.derby.iapi.store.access.ColumnOrdering;
import org.apache.derby.iapi.store.access.SortCostController;
import org.apache.derby.iapi.sql.compile.RequiredRowOrdering;

public class OrderByList extends OrderedColumnList implements RequiredRowOrdering
{
    private boolean allAscending;
    private boolean alwaysSort;
    private ResultSetNode resultToSort;
    private SortCostController scc;
    private Object[] resultRow;
    private ColumnOrdering[] columnOrdering;
    private int estimatedRowSize;
    private boolean sortNeeded;
    private int resultSetNumber;
    private boolean isTableValueCtorOrdering;
    
    public OrderByList() {
        this.allAscending = true;
        this.sortNeeded = true;
        this.resultSetNumber = -1;
    }
    
    public void init(final Object o) {
        this.isTableValueCtorOrdering = ((o instanceof UnionNode && ((UnionNode)o).tableConstructor()) || o instanceof RowResultSetNode);
    }
    
    public void addOrderByColumn(final OrderByColumn orderByColumn) {
        this.addElement(orderByColumn);
        if (!orderByColumn.isAscending()) {
            this.allAscending = false;
        }
    }
    
    boolean allAscending() {
        return this.allAscending;
    }
    
    public OrderByColumn getOrderByColumn(final int n) {
        return (OrderByColumn)this.elementAt(n);
    }
    
    public void bindOrderByColumns(final ResultSetNode resultToSort) throws StandardException {
        this.resultToSort = resultToSort;
        final int size = this.size();
        if (size > 1012) {
            throw StandardException.newException("54004");
        }
        for (int i = 0; i < size; ++i) {
            final OrderByColumn orderByColumn = (OrderByColumn)this.elementAt(i);
            orderByColumn.bindOrderByColumn(resultToSort, this);
            if (!(orderByColumn.getResultColumn().getExpression() instanceof ColumnReference)) {
                this.alwaysSort = true;
            }
        }
    }
    
    void closeGap(final int n) {
        for (int i = 0; i < this.size(); ++i) {
            ((OrderByColumn)this.elementAt(i)).collapseAddedColumnGap(n);
        }
    }
    
    public void pullUpOrderByColumns(final ResultSetNode resultToSort) throws StandardException {
        this.resultToSort = resultToSort;
        for (int size = this.size(), i = 0; i < size; ++i) {
            ((OrderByColumn)this.elementAt(i)).pullUpOrderByColumn(resultToSort);
        }
    }
    
    boolean isInOrderPrefix(final ResultColumnList list) {
        list.size();
        for (int size = this.size(), i = 0; i < size; ++i) {
            if (((OrderByColumn)this.elementAt(i)).getResultColumn() != list.elementAt(i)) {
                return false;
            }
        }
        return true;
    }
    
    void resetToSourceRCs() {
        for (int size = this.size(), i = 0; i < size; ++i) {
            ((OrderByColumn)this.elementAt(i)).resetToSourceRC();
        }
    }
    
    ResultColumnList reorderRCL(final ResultColumnList list) throws StandardException {
        final ResultColumnList list2 = (ResultColumnList)this.getNodeFactory().getNode(9, this.getContextManager());
        for (int size = this.size(), i = 0; i < size; ++i) {
            final OrderByColumn orderByColumn = (OrderByColumn)this.elementAt(i);
            list2.addElement(orderByColumn.getResultColumn());
            list.removeElement(orderByColumn.getResultColumn());
        }
        list2.destructiveAppend(list);
        list2.resetVirtualColumnIds();
        list2.copyOrderBySelect(list);
        return list2;
    }
    
    void removeConstantColumns(final PredicateList list) {
        for (int i = this.size() - 1; i >= 0; --i) {
            if (((OrderByColumn)this.elementAt(i)).constantColumn(list)) {
                this.removeElementAt(i);
            }
        }
    }
    
    void removeDupColumns() {
        for (int i = this.size() - 1; i > 0; --i) {
            final int columnPosition = ((OrderByColumn)this.elementAt(i)).getColumnPosition();
            for (int j = 0; j < i; ++j) {
                if (columnPosition == ((OrderByColumn)this.elementAt(j)).getColumnPosition()) {
                    this.removeElementAt(i);
                    break;
                }
            }
        }
    }
    
    public void generate(final ActivationClassBuilder activationClassBuilder, final MethodBuilder methodBuilder, final ResultSetNode resultSetNode) throws StandardException {
        if (!this.sortNeeded) {
            resultSetNode.generate(activationClassBuilder, methodBuilder);
            return;
        }
        final CompilerContext compilerContext = this.getCompilerContext();
        final int addItem = activationClassBuilder.addItem(activationClassBuilder.getColumnOrdering(this));
        activationClassBuilder.pushGetResultSetFactoryExpression(methodBuilder);
        resultSetNode.generate(activationClassBuilder, methodBuilder);
        this.resultSetNumber = compilerContext.getNextResultSetNumber();
        methodBuilder.push(false);
        methodBuilder.push(false);
        methodBuilder.push(addItem);
        methodBuilder.push(activationClassBuilder.addItem(resultSetNode.getResultColumns().buildRowTemplate()));
        methodBuilder.push(resultSetNode.getResultColumns().getTotalColumnSize());
        methodBuilder.push(this.resultSetNumber);
        final CostEstimate finalCostEstimate = resultSetNode.getFinalCostEstimate();
        methodBuilder.push(finalCostEstimate.rowCount());
        methodBuilder.push(finalCostEstimate.getEstimatedCost());
        methodBuilder.callMethod((short)185, null, "getSortResultSet", "org.apache.derby.iapi.sql.execute.NoPutResultSet", 9);
    }
    
    public int sortRequired(final RowOrdering rowOrdering, final OptimizableList list, final int[] array) throws StandardException {
        return this.sortRequired(rowOrdering, null, list, array);
    }
    
    public int sortRequired(final RowOrdering rowOrdering, final JBitSet set, final OptimizableList list, final int[] array) throws StandardException {
        if (this.alwaysSort) {
            return 1;
        }
        int n = 0;
        for (int size = this.size(), i = 0; i < size; ++i) {
            final OrderByColumn orderByColumn = this.getOrderByColumn(i);
            if (orderByColumn.isNullsOrderedLow()) {
                return 1;
            }
            final ValueNode expression = orderByColumn.getResultColumn().getExpression();
            if (!(expression instanceof ColumnReference)) {
                return 1;
            }
            final ColumnReference columnReference = (ColumnReference)expression;
            if (set != null && !set.get(columnReference.getTableNumber())) {
                for (int j = i + 1; j < this.size(); ++j) {
                    final ValueNode expression2 = this.getOrderByColumn(i).getResultColumn().getExpression();
                    if (expression2 instanceof ColumnReference && set.get(((ColumnReference)expression2).getTableNumber())) {
                        return 1;
                    }
                }
                return 3;
            }
            if (set != null && !set.hasSingleBitSet() && !rowOrdering.alwaysOrdered(columnReference.getTableNumber()) && !rowOrdering.isColumnAlwaysOrdered(columnReference.getTableNumber(), columnReference.getColumnNumber())) {
                for (int n2 = 0; n2 < array.length && array[n2] != -1; ++n2) {
                    final Optimizable optimizable = list.getOptimizable(array[n2]);
                    if (optimizable.getTableNumber() == columnReference.getTableNumber()) {
                        break;
                    }
                    if (!rowOrdering.alwaysOrdered(optimizable.getTableNumber())) {
                        return 1;
                    }
                }
            }
            if (!rowOrdering.alwaysOrdered(columnReference.getTableNumber())) {
                if (!rowOrdering.orderedOnColumn(orderByColumn.isAscending() ? 1 : 2, n, columnReference.getTableNumber(), columnReference.getColumnNumber())) {
                    return 1;
                }
                ++n;
            }
        }
        return 3;
    }
    
    public void estimateCost(final double n, final RowOrdering rowOrdering, final CostEstimate costEstimate) throws StandardException {
        if (this.scc == null) {
            this.scc = this.getCompilerContext().getSortCostController();
            this.resultRow = this.resultToSort.getResultColumns().buildEmptyRow().getRowArray();
            this.columnOrdering = this.getColumnOrdering();
            this.estimatedRowSize = this.resultToSort.getResultColumns().getTotalColumnSize();
        }
        final long n2 = (long)n;
        costEstimate.setCost(this.scc.getSortCost((DataValueDescriptor[])this.resultRow, this.columnOrdering, false, n2, n2, this.estimatedRowSize), n, n);
    }
    
    public void sortNeeded() {
        this.sortNeeded = true;
    }
    
    public void sortNotNeeded() {
        this.sortNeeded = false;
    }
    
    void remapColumnReferencesToExpressions() throws StandardException {
    }
    
    public boolean getSortNeeded() {
        return this.sortNeeded;
    }
    
    boolean requiresDescending(final ColumnReference columnReference, final int n) throws StandardException {
        final int size = this.size();
        final JBitSet set = new JBitSet(n);
        final BaseTableNumbersVisitor baseTableNumbersVisitor = new BaseTableNumbersVisitor(set);
        columnReference.accept(baseTableNumbersVisitor);
        final int firstSetBit = set.getFirstSetBit();
        final int columnNumber = baseTableNumbersVisitor.getColumnNumber();
        for (int i = 0; i < size; ++i) {
            final OrderByColumn orderByColumn = this.getOrderByColumn(i);
            final ResultColumn resultColumn = orderByColumn.getResultColumn();
            baseTableNumbersVisitor.reset();
            resultColumn.accept(baseTableNumbersVisitor);
            final int firstSetBit2 = set.getFirstSetBit();
            final int columnNumber2 = baseTableNumbersVisitor.getColumnNumber();
            if (firstSetBit == firstSetBit2) {
                if (columnNumber == columnNumber2) {
                    return !orderByColumn.isAscending();
                }
            }
        }
        return false;
    }
    
    public String toString() {
        final StringBuffer sb = new StringBuffer();
        if (this.columnOrdering != null) {
            for (int i = 0; i < this.columnOrdering.length; ++i) {
                sb.append("[" + i + "] " + this.columnOrdering[i] + "\n");
            }
        }
        return "allAscending: " + this.allAscending + "\n" + "alwaysSort:" + this.allAscending + "\n" + "sortNeeded: " + this.sortNeeded + "\n" + "columnOrdering: " + "\n" + sb.toString() + "\n" + super.toString();
    }
    
    public int getResultSetNumber() {
        return this.resultSetNumber;
    }
    
    public boolean isTableValueCtorOrdering() {
        return this.isTableValueCtorOrdering;
    }
}
