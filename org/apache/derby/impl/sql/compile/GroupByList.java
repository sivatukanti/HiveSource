// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.impl.sql.compile;

import org.apache.derby.iapi.error.StandardException;
import java.util.List;

public class GroupByList extends OrderedColumnList
{
    int numGroupingColsAdded;
    boolean rollup;
    
    public GroupByList() {
        this.numGroupingColsAdded = 0;
        this.rollup = false;
    }
    
    public void addGroupByColumn(final GroupByColumn groupByColumn) {
        this.addElement(groupByColumn);
    }
    
    public GroupByColumn getGroupByColumn(final int n) {
        return (GroupByColumn)this.elementAt(n);
    }
    
    public void setRollup() {
        this.rollup = true;
    }
    
    public boolean isRollup() {
        return this.rollup;
    }
    
    public int getNumNeedToAddGroupingCols() {
        return this.numGroupingColsAdded;
    }
    
    void bindGroupByColumns(final SelectNode selectNode, final List list) throws StandardException {
        final FromList fromList = selectNode.getFromList();
        final ResultColumnList resultColumns = selectNode.getResultColumns();
        final SubqueryList list2 = (SubqueryList)this.getNodeFactory().getNode(11, this.getContextManager());
        int n = 0;
        final int size = this.size();
        if (size > 32677) {
            throw StandardException.newException("54004");
        }
        for (int i = 0; i < size; ++i) {
            ((GroupByColumn)this.elementAt(i)).bindExpression(fromList, list2, list);
        }
        final int size2 = resultColumns.size();
        for (int j = 0; j < size; ++j) {
            boolean b = false;
            final GroupByColumn groupByColumn = (GroupByColumn)this.elementAt(j);
            for (int k = 0; k < size2; ++k) {
                final ResultColumn resultColumn = (ResultColumn)resultColumns.elementAt(k);
                if (resultColumn.getExpression() instanceof ColumnReference) {
                    if (((ColumnReference)resultColumn.getExpression()).isEquivalent(groupByColumn.getColumnExpression())) {
                        groupByColumn.setColumnPosition(k + 1);
                        resultColumn.markAsGroupingColumn();
                        b = true;
                        break;
                    }
                }
            }
            if (!b && !selectNode.hasDistinct() && groupByColumn.getColumnExpression() instanceof ColumnReference) {
                final ResultColumn resultColumn2 = (ResultColumn)this.getNodeFactory().getNode(80, groupByColumn.getColumnName(), groupByColumn.getColumnExpression().getClone(), this.getContextManager());
                resultColumn2.setVirtualColumnId(resultColumns.size() + 1);
                resultColumn2.markGenerated();
                resultColumn2.markAsGroupingColumn();
                resultColumns.addElement(resultColumn2);
                groupByColumn.setColumnPosition(resultColumns.size());
                resultColumns.setCountMismatchAllowed(true);
                ++n;
            }
            if (groupByColumn.getColumnExpression() instanceof JavaToSQLValueNode) {
                throw StandardException.newException("42Y30");
            }
        }
        this.numGroupingColsAdded += n;
    }
    
    public GroupByColumn findGroupingColumn(final ValueNode valueNode) throws StandardException {
        for (int size = this.size(), i = 0; i < size; ++i) {
            final GroupByColumn groupByColumn = (GroupByColumn)this.elementAt(i);
            if (groupByColumn.getColumnExpression().isEquivalent(valueNode)) {
                return groupByColumn;
            }
        }
        return null;
    }
    
    public void remapColumnReferencesToExpressions() throws StandardException {
        for (int size = this.size(), i = 0; i < size; ++i) {
            final GroupByColumn groupByColumn = (GroupByColumn)this.elementAt(i);
            groupByColumn.setColumnExpression(groupByColumn.getColumnExpression().remapColumnReferencesToExpressions());
        }
    }
    
    public String toString() {
        return "";
    }
    
    public void preprocess(final int n, final FromList list, final SubqueryList list2, final PredicateList list3) throws StandardException {
        for (int i = 0; i < this.size(); ++i) {
            final GroupByColumn groupByColumn = (GroupByColumn)this.elementAt(i);
            groupByColumn.setColumnExpression(groupByColumn.getColumnExpression().preprocess(n, list, list2, list3));
        }
    }
}
