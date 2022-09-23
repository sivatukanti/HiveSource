// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.impl.sql.compile;

import org.apache.derby.iapi.util.JBitSet;
import java.util.Iterator;
import org.apache.derby.iapi.sql.compile.CompilerContext;
import org.apache.derby.iapi.sql.compile.Visitor;
import org.apache.derby.iapi.sql.dictionary.DataDictionary;
import org.apache.derby.iapi.error.StandardException;
import org.apache.derby.iapi.sql.dictionary.SchemaDescriptor;

public class FromSubquery extends FromTable
{
    ResultSetNode subquery;
    private OrderByList orderByList;
    private ValueNode offset;
    private ValueNode fetchFirst;
    private boolean hasJDBClimitClause;
    private SchemaDescriptor origCompilationSchema;
    
    public FromSubquery() {
        this.origCompilationSchema = null;
    }
    
    public void init(final Object o, final Object o2, final Object o3, final Object o4, final Object o5, final Object o6, final Object o7, final Object o8) {
        super.init(o6, o8);
        this.subquery = (ResultSetNode)o;
        this.orderByList = (OrderByList)o2;
        this.offset = (ValueNode)o3;
        this.fetchFirst = (ValueNode)o4;
        this.hasJDBClimitClause = (o5 != null && (boolean)o5);
        this.resultColumns = (ResultColumnList)o7;
    }
    
    public void printSubNodes(final int n) {
    }
    
    public ResultSetNode getSubquery() {
        return this.subquery;
    }
    
    protected FromTable getFromTableByName(final String anObject, final String s, final boolean b) throws StandardException {
        if (s != null && this.origTableName != null && !s.equals(this.origTableName.schemaName)) {
            return null;
        }
        if (this.getExposedName().equals(anObject)) {
            return this;
        }
        return null;
    }
    
    public ResultSetNode bindNonVTITables(final DataDictionary dataDictionary, final FromList list) throws StandardException {
        if (this.tableNumber == -1) {
            this.tableNumber = this.getCompilerContext().getNextTableNumber();
        }
        this.subquery = this.subquery.bindNonVTITables(dataDictionary, list);
        return this;
    }
    
    public ResultSetNode bindVTITables(final FromList list) throws StandardException {
        this.subquery = this.subquery.bindVTITables(list);
        return this;
    }
    
    public void rejectParameters() throws StandardException {
        this.subquery.rejectParameters();
    }
    
    public void bindExpressions(final FromList list) throws StandardException {
        final FromList list2 = (FromList)this.getNodeFactory().getNode(37, this.getNodeFactory().doJoinOrderOptimization(), this.getContextManager());
        final ResultColumnList resultColumns = this.resultColumns;
        if (this.orderByList != null) {
            this.orderByList.pullUpOrderByColumns(this.subquery);
        }
        final FromList list3 = list2;
        final CompilerContext compilerContext = this.getCompilerContext();
        if (this.origCompilationSchema != null) {
            compilerContext.pushCompilationSchema(this.origCompilationSchema);
        }
        final CollectNodesVisitor collectNodesVisitor = new CollectNodesVisitor(FromVTI.class);
        this.subquery.accept(collectNodesVisitor);
        final Iterator iterator = collectNodesVisitor.getList().iterator();
        while (iterator.hasNext()) {
            iterator.next().addOuterFromList(list);
        }
        try {
            this.subquery.bindExpressions(list3);
            this.subquery.bindResultColumns(list3);
        }
        finally {
            if (this.origCompilationSchema != null) {
                compilerContext.popCompilationSchema();
            }
        }
        if (this.orderByList != null) {
            this.orderByList.bindOrderByColumns(this.subquery);
        }
        QueryTreeNode.bindOffsetFetch(this.offset, this.fetchFirst);
        final ResultColumnList resultColumns2 = this.subquery.getResultColumns();
        if (this.resultColumns != null && this.resultColumns.getCountMismatchAllowed() && this.resultColumns.size() < resultColumns2.size()) {
            for (int i = resultColumns2.size() - 1; i >= this.resultColumns.size(); --i) {
                resultColumns2.removeElementAt(i);
            }
        }
        final ResultColumnList copyListAndObjects = resultColumns2.copyListAndObjects();
        copyListAndObjects.genVirtualColumnNodes(this.subquery, this.subquery.getResultColumns());
        this.resultColumns = copyListAndObjects;
        if (resultColumns != null) {
            this.resultColumns.propagateDCLInfo(resultColumns, this.correlationName);
        }
    }
    
    public ResultColumn getMatchingColumn(final ColumnReference columnReference) throws StandardException {
        ResultColumn resultColumn = null;
        final String tableName = columnReference.getTableName();
        if (columnReference.getGeneratedToReplaceAggregate()) {
            resultColumn = this.resultColumns.getResultColumn(columnReference.getColumnName());
        }
        else if (tableName == null || tableName.equals(this.correlationName)) {
            resultColumn = this.resultColumns.getAtMostOneResultColumn(columnReference, this.correlationName, false);
        }
        if (resultColumn != null) {
            columnReference.setTableNumber(this.tableNumber);
            columnReference.setColumnNumber(resultColumn.getColumnPosition());
        }
        return resultColumn;
    }
    
    public ResultSetNode preprocess(final int n, final GroupByList list, final FromList list2) throws StandardException {
        if (this.orderByList != null) {
            if (this.orderByList.size() > 1) {
                this.orderByList.removeDupColumns();
            }
            this.subquery.pushOrderByList(this.orderByList);
            this.orderByList = null;
        }
        this.subquery.pushOffsetFetchFirst(this.offset, this.fetchFirst, this.hasJDBClimitClause);
        this.subquery = this.subquery.preprocess(n, list, list2);
        if ((list == null || list.size() == 0) && this.tableProperties == null && this.subquery.flattenableInFromSubquery(list2)) {
            this.setReferencedTableMap(this.subquery.getReferencedTableMap());
            return this;
        }
        return this.extractSubquery(n);
    }
    
    public ResultSetNode extractSubquery(final int n) throws StandardException {
        final ResultSetNode resultSetNode = (ResultSetNode)this.getNodeFactory().getNode(151, this.subquery, this.resultColumns, null, null, null, null, this.tableProperties, this.getContextManager());
        final JBitSet referencedTableMap = new JBitSet(n);
        referencedTableMap.set(this.tableNumber);
        resultSetNode.setReferencedTableMap(referencedTableMap);
        ((FromTable)resultSetNode).setTableNumber(this.tableNumber);
        return resultSetNode;
    }
    
    public FromList flatten(final ResultColumnList list, final PredicateList list2, final SubqueryList list3, final GroupByList list4, final ValueNode valueNode) throws StandardException {
        FromList fromList = null;
        this.resultColumns.setRedundant();
        this.subquery.getResultColumns().setRedundant();
        if (this.subquery instanceof SelectNode) {
            final SelectNode selectNode = (SelectNode)this.subquery;
            fromList = selectNode.getFromList();
            if (selectNode.getWherePredicates().size() > 0) {
                list2.destructiveAppend(selectNode.getWherePredicates());
            }
            if (selectNode.getWhereSubquerys().size() > 0) {
                list3.destructiveAppend(selectNode.getWhereSubquerys());
            }
        }
        else if (!(this.subquery instanceof RowResultSetNode)) {}
        list.remapColumnReferencesToExpressions();
        list2.remapColumnReferencesToExpressions();
        if (list4 != null) {
            list4.remapColumnReferencesToExpressions();
        }
        if (valueNode != null) {
            valueNode.remapColumnReferencesToExpressions();
        }
        return fromList;
    }
    
    public String getExposedName() {
        return this.correlationName;
    }
    
    public ResultColumnList getAllResultColumns(final TableName tableName) throws StandardException {
        TableName tableName2;
        if (tableName != null) {
            tableName2 = this.makeTableName(tableName.getSchemaName(), this.correlationName);
        }
        else {
            tableName2 = this.makeTableName(null, this.correlationName);
        }
        if (tableName != null && !tableName.equals(tableName2)) {
            return null;
        }
        final TableName tableName3 = this.makeTableName(null, this.correlationName);
        final ResultColumnList list = (ResultColumnList)this.getNodeFactory().getNode(9, this.getContextManager());
        for (int visibleSize = this.resultColumns.visibleSize(), i = 0; i < visibleSize; ++i) {
            final ResultColumn resultColumn = (ResultColumn)this.resultColumns.elementAt(i);
            if (!resultColumn.isGenerated()) {
                final String name = resultColumn.getName();
                final boolean nameGenerated = resultColumn.isNameGenerated();
                final ResultColumn resultColumn2 = (ResultColumn)this.getNodeFactory().getNode(80, name, this.getNodeFactory().getNode(62, name, tableName3, this.getContextManager()), this.getContextManager());
                resultColumn2.setNameGenerated(nameGenerated);
                list.addResultColumn(resultColumn2);
            }
        }
        return list;
    }
    
    public boolean referencesTarget(final String s, final boolean b) throws StandardException {
        return this.subquery.referencesTarget(s, b);
    }
    
    public boolean referencesSessionSchema() throws StandardException {
        return this.subquery.referencesSessionSchema();
    }
    
    public void bindUntypedNullsToResultColumns(final ResultColumnList list) throws StandardException {
        this.subquery.bindUntypedNullsToResultColumns(list);
    }
    
    void decrementLevel(final int n) {
        super.decrementLevel(n);
        this.subquery.decrementLevel(n);
    }
    
    public void setOrigCompilationSchema(final SchemaDescriptor origCompilationSchema) {
        this.origCompilationSchema = origCompilationSchema;
    }
    
    void acceptChildren(final Visitor visitor) throws StandardException {
        super.acceptChildren(visitor);
        this.subquery.accept(visitor);
        if (this.orderByList != null) {
            this.orderByList.accept(visitor);
        }
        if (this.offset != null) {
            this.offset.accept(visitor);
        }
        if (this.fetchFirst != null) {
            this.fetchFirst.accept(visitor);
        }
    }
}
