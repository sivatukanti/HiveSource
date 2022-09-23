// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.impl.sql.compile;

import org.apache.derby.iapi.sql.compile.Visitor;
import org.apache.derby.iapi.sql.execute.ExecCursorTableReference;
import org.apache.derby.impl.sql.CursorInfo;
import org.apache.derby.impl.sql.CursorTableReference;
import org.apache.derby.iapi.services.compiler.MethodBuilder;
import org.apache.derby.iapi.sql.dictionary.TableDescriptor;
import org.apache.derby.iapi.sql.dictionary.DataDictionary;
import org.apache.derby.iapi.error.StandardException;
import java.util.ArrayList;
import org.apache.derby.iapi.sql.ResultColumnDescriptor;
import java.util.List;

public class CursorNode extends DMLStatementNode
{
    public static final int UNSPECIFIED = 0;
    public static final int READ_ONLY = 1;
    public static final int UPDATE = 2;
    private String name;
    private OrderByList orderByList;
    private ValueNode offset;
    private ValueNode fetchFirst;
    private boolean hasJDBClimitClause;
    private String statementType;
    private int updateMode;
    private boolean needTarget;
    private List updatableColumns;
    private FromTable updateTable;
    private ResultColumnDescriptor[] targetColumnDescriptors;
    private ArrayList statsToUpdate;
    private boolean checkIndexStats;
    private int indexOfSessionTableNamesInSavedObjects;
    
    public CursorNode() {
        this.indexOfSessionTableNamesInSavedObjects = -1;
    }
    
    public void init(final Object o, final Object o2, final Object o3, final Object o4, final Object o5, final Object o6, final Object o7, final Object o8, final Object o9) {
        this.init(o2);
        this.name = (String)o3;
        this.statementType = (String)o;
        this.orderByList = (OrderByList)o4;
        this.offset = (ValueNode)o5;
        this.fetchFirst = (ValueNode)o6;
        this.hasJDBClimitClause = (o7 != null && (boolean)o7);
        this.updateMode = (int)o8;
        this.updatableColumns = (List)o9;
    }
    
    public String toString() {
        return "";
    }
    
    public String statementToString() {
        return this.statementType;
    }
    
    private static String updateModeString(final int n) {
        return "";
    }
    
    public void printSubNodes(final int n) {
    }
    
    public void bindStatement() throws StandardException {
        final DataDictionary dataDictionary = this.getDataDictionary();
        this.checkIndexStats = (dataDictionary.getIndexStatsRefresher(true) != null);
        if (this.orderByList != null) {
            this.orderByList.pullUpOrderByColumns(this.resultSet);
        }
        this.getCompilerContext().pushCurrentPrivType(this.getPrivType());
        try {
            final FromList list = (FromList)this.getNodeFactory().getNode(37, this.getNodeFactory().doJoinOrderOptimization(), this.getContextManager());
            this.resultSet.rejectParameters();
            super.bind(dataDictionary);
            this.resultSet.bindResultColumns(list);
            this.resultSet.bindUntypedNullsToResultColumns(null);
            this.resultSet.rejectXMLValues();
        }
        finally {
            this.getCompilerContext().popCurrentPrivType();
        }
        this.collectTablesWithPossiblyStaleStats();
        if (this.orderByList != null) {
            this.orderByList.bindOrderByColumns(this.resultSet);
        }
        QueryTreeNode.bindOffsetFetch(this.offset, this.fetchFirst);
        if (this.updateMode == 2 && this.updateMode != this.determineUpdateMode(dataDictionary)) {
            throw StandardException.newException("42Y90");
        }
        if (this.updateMode == 0) {
            if (this.getLanguageConnectionContext().getStatementContext().isForReadOnly()) {
                this.updateMode = 1;
            }
            else {
                this.updateMode = this.determineUpdateMode(dataDictionary);
            }
        }
        if (this.updateMode == 1) {
            this.updatableColumns = null;
        }
        if (this.updateMode == 2) {
            this.bindUpdateColumns(this.updateTable);
            if (this.updateTable instanceof FromTable) {
                this.updateTable.markUpdatableByCursor(this.updatableColumns);
                this.resultSet.getResultColumns().markColumnsInSelectListUpdatableByCursor(this.updatableColumns);
            }
        }
        this.resultSet.renameGeneratedResultNames();
        if (this.getLanguageConnectionContext().checkIfAnyDeclaredGlobalTempTablesForThisConnection()) {
            final ArrayList sessionSchemaTableNamesForCursor = this.getSessionSchemaTableNamesForCursor();
            if (sessionSchemaTableNamesForCursor != null) {
                this.indexOfSessionTableNamesInSavedObjects = this.getCompilerContext().addSavedObject(sessionSchemaTableNamesForCursor);
            }
        }
    }
    
    private void collectTablesWithPossiblyStaleStats() throws StandardException {
        if (!this.checkIndexStats) {
            return;
        }
        final FromList fromList = this.resultSet.getFromList();
        for (int i = 0; i < fromList.size(); ++i) {
            final FromTable fromTable = (FromTable)fromList.elementAt(i);
            if (fromTable.isBaseTable()) {
                final TableDescriptor tableDescriptor = fromTable.getTableDescriptor();
                if (tableDescriptor.getTableType() == 0) {
                    if (this.statsToUpdate == null) {
                        this.statsToUpdate = new ArrayList();
                    }
                    this.statsToUpdate.add(tableDescriptor);
                }
            }
        }
    }
    
    public boolean referencesSessionSchema() throws StandardException {
        return this.resultSet.referencesSessionSchema();
    }
    
    protected ArrayList getSessionSchemaTableNamesForCursor() throws StandardException {
        final FromList fromList = this.resultSet.getFromList();
        final int size = fromList.size();
        ArrayList<String> list = null;
        for (int i = 0; i < size; ++i) {
            final FromTable fromTable = (FromTable)fromList.elementAt(i);
            if (fromTable instanceof FromBaseTable && this.isSessionSchema(fromTable.getTableDescriptor().getSchemaDescriptor())) {
                if (list == null) {
                    list = new ArrayList<String>();
                }
                list.add(fromTable.getTableName().getTableName());
            }
        }
        return list;
    }
    
    private int determineUpdateMode(final DataDictionary dataDictionary) throws StandardException {
        if (this.updateMode == 1) {
            return 1;
        }
        if (this.orderByList != null) {
            return 1;
        }
        if (!this.resultSet.isUpdatableCursor(dataDictionary)) {
            return 1;
        }
        this.updateTable = this.resultSet.getCursorTargetTable();
        if (this.updateTable.markAsCursorTargetTable()) {
            this.needTarget = true;
            this.genTargetResultColList();
        }
        return 2;
    }
    
    public void optimizeStatement() throws StandardException {
        if (this.orderByList != null) {
            if (this.orderByList.size() > 1) {
                this.orderByList.removeDupColumns();
            }
            this.resultSet.pushOrderByList(this.orderByList);
            this.orderByList = null;
        }
        this.resultSet.pushOffsetFetchFirst(this.offset, this.fetchFirst, this.hasJDBClimitClause);
        super.optimizeStatement();
    }
    
    int activationKind() {
        return 4;
    }
    
    public void generate(final ActivationClassBuilder activationClassBuilder, final MethodBuilder methodBuilder) throws StandardException {
        if (this.indexOfSessionTableNamesInSavedObjects != -1) {
            final MethodBuilder constructor = activationClassBuilder.getConstructor();
            constructor.pushThis();
            constructor.push(this.indexOfSessionTableNamesInSavedObjects);
            constructor.putField("org.apache.derby.impl.sql.execute.BaseActivation", "indexOfSessionTableNamesInSavedObjects", "int");
            constructor.endStatement();
        }
        this.generateParameterValueSet(activationClassBuilder);
        this.resultSet.markStatementResultSet();
        this.resultSet.generate(activationClassBuilder, methodBuilder);
        if (this.needTarget) {
            activationClassBuilder.rememberCursor(methodBuilder);
            activationClassBuilder.addCursorPositionCode();
        }
    }
    
    public String getUpdateBaseTableName() {
        return (this.updateTable == null) ? null : this.updateTable.getBaseTableName();
    }
    
    public String getUpdateExposedTableName() throws StandardException {
        return (this.updateTable == null) ? null : this.updateTable.getExposedName();
    }
    
    public String getUpdateSchemaName() throws StandardException {
        return (this.updateTable == null) ? null : ((FromBaseTable)this.updateTable).getTableNameField().getSchemaName();
    }
    
    public int getUpdateMode() {
        return this.updateMode;
    }
    
    private String[] getUpdatableColumns() {
        return (String[])((this.updatableColumns == null) ? null : this.getUpdateColumnNames());
    }
    
    private ResultColumnDescriptor[] genTargetResultColList() throws StandardException {
        if (this.updateTable == null) {
            return null;
        }
        if (this.targetColumnDescriptors != null) {
            return this.targetColumnDescriptors;
        }
        final ResultColumnList list = (ResultColumnList)this.getNodeFactory().getNode(9, this.getContextManager());
        final ResultColumnList resultColumns = this.updateTable.getResultColumns();
        for (int size = resultColumns.size(), i = 0; i < size; ++i) {
            final ResultColumn resultColumn = (ResultColumn)resultColumns.elementAt(i);
            list.addResultColumn((ResultColumn)this.getNodeFactory().getNode(80, resultColumn.columnDescriptor, this.getNodeFactory().getNode(94, resultColumn.getName(), this.makeTableName(resultColumn.getSchemaName(), resultColumn.getTableName()), resultColumn.getTypeServices(), this.getContextManager()), this.getContextManager()));
        }
        return this.targetColumnDescriptors = list.makeResultDescriptors();
    }
    
    public boolean needsSavepoint() {
        return false;
    }
    
    public Object getCursorInfo() throws StandardException {
        if (!this.needTarget) {
            return null;
        }
        return new CursorInfo(this.updateMode, new CursorTableReference(this.getUpdateExposedTableName(), this.getUpdateBaseTableName(), this.getUpdateSchemaName()), this.genTargetResultColList(), this.getUpdatableColumns());
    }
    
    private void bindUpdateColumns(final FromTable fromTable) throws StandardException {
        final int size = this.updatableColumns.size();
        final ResultColumnList resultColumns = this.resultSet.getResultColumns();
        for (int i = 0; i < size; ++i) {
            final String s = this.updatableColumns.get(i);
            if (fromTable.getTableDescriptor().getColumnDescriptor(s) == null) {
                throw StandardException.newException("42X04", s);
            }
            for (int j = 0; j < resultColumns.size(); ++j) {
                final ResultColumn resultColumn = (ResultColumn)resultColumns.elementAt(j);
                if (resultColumn.getSourceTableName() != null) {
                    if (resultColumn.getExpression() != null && resultColumn.getExpression().getColumnName().equals(s) && !resultColumn.getName().equals(s)) {
                        throw StandardException.newException("42X42", s);
                    }
                }
            }
        }
    }
    
    private String[] getUpdateColumnNames() {
        final int size = this.updatableColumns.size();
        if (size == 0) {
            return null;
        }
        return this.updatableColumns.toArray(new String[size]);
    }
    
    public String getXML() {
        return null;
    }
    
    public TableDescriptor[] updateIndexStatisticsFor() throws StandardException {
        if (!this.checkIndexStats || this.statsToUpdate == null) {
            return CursorNode.EMPTY_TD_LIST;
        }
        for (int i = this.statsToUpdate.size() - 1; i >= 0; --i) {
            if (((TableDescriptor)this.statsToUpdate.get(i)).getAndClearIndexStatsIsUpToDate()) {
                this.statsToUpdate.remove(i);
            }
        }
        if (this.statsToUpdate.isEmpty()) {
            return CursorNode.EMPTY_TD_LIST;
        }
        final TableDescriptor[] a = new TableDescriptor[this.statsToUpdate.size()];
        this.statsToUpdate.toArray(a);
        this.statsToUpdate.clear();
        return a;
    }
    
    void acceptChildren(final Visitor visitor) throws StandardException {
        super.acceptChildren(visitor);
        if (this.orderByList != null) {
            this.orderByList.acceptChildren(visitor);
        }
        if (this.offset != null) {
            this.offset.acceptChildren(visitor);
        }
        if (this.fetchFirst != null) {
            this.fetchFirst.acceptChildren(visitor);
        }
    }
}
