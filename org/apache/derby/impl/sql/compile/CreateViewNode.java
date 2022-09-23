// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.impl.sql.compile;

import org.apache.derby.iapi.sql.compile.Visitor;
import org.apache.derby.catalog.DefaultInfo;
import org.apache.derby.iapi.types.DataValueDescriptor;
import org.apache.derby.catalog.UUID;
import org.apache.derby.iapi.sql.execute.ConstantAction;
import org.apache.derby.iapi.sql.depend.DependencyManager;
import org.apache.derby.iapi.sql.depend.ProviderList;
import org.apache.derby.iapi.services.context.ContextManager;
import org.apache.derby.iapi.sql.compile.NodeFactory;
import org.apache.derby.iapi.sql.conn.LanguageConnectionContext;
import org.apache.derby.iapi.sql.compile.CompilerContext;
import org.apache.derby.iapi.sql.dictionary.DataDictionary;
import org.apache.derby.iapi.error.StandardException;
import org.apache.derby.impl.sql.execute.ColumnInfo;
import org.apache.derby.iapi.sql.depend.ProviderInfo;

public class CreateViewNode extends DDLStatementNode
{
    ResultColumnList resultColumns;
    ResultSetNode queryExpression;
    String qeText;
    int checkOption;
    ProviderInfo[] providerInfos;
    ColumnInfo[] colInfos;
    private OrderByList orderByList;
    private ValueNode offset;
    private ValueNode fetchFirst;
    private boolean hasJDBClimitClause;
    
    public void init(final Object o, final Object o2, final Object o3, final Object o4, final Object o5, final Object o6, final Object o7, final Object o8, final Object o9) throws StandardException {
        this.initAndCheck(o);
        this.resultColumns = (ResultColumnList)o2;
        this.queryExpression = (ResultSetNode)o3;
        this.checkOption = (int)o4;
        this.qeText = ((String)o5).trim();
        this.orderByList = (OrderByList)o6;
        this.offset = (ValueNode)o7;
        this.fetchFirst = (ValueNode)o8;
        this.hasJDBClimitClause = (o9 != null && (boolean)o9);
        this.implicitCreateSchema = true;
    }
    
    public String toString() {
        return "";
    }
    
    public String statementToString() {
        return "CREATE VIEW";
    }
    
    public void printSubNodes(final int n) {
    }
    
    public int getCheckOption() {
        return this.checkOption;
    }
    
    public ProviderInfo[] getProviderInfo() {
        return this.providerInfos;
    }
    
    public ColumnInfo[] getColumnInfo() {
        return this.colInfos;
    }
    
    public void bindStatement() throws StandardException {
        this.providerInfos = this.bindViewDefinition(this.getDataDictionary(), this.getCompilerContext(), this.getLanguageConnectionContext(), this.getNodeFactory(), this.queryExpression, this.getContextManager());
        final ResultColumnList resultColumns = this.queryExpression.getResultColumns();
        if (this.resultColumns != null) {
            if (this.resultColumns.size() != resultColumns.visibleSize()) {
                throw StandardException.newException("42X56", this.getFullName());
            }
            resultColumns.copyResultColumnNames(this.resultColumns);
        }
        final String verifyUniqueNames = resultColumns.verifyUniqueNames(this.resultColumns == null);
        if (verifyUniqueNames != null) {
            throw StandardException.newException("42Y13", verifyUniqueNames);
        }
        if (this.queryExpression.getResultColumns().size() > 5000) {
            throw StandardException.newException("54011", String.valueOf(this.queryExpression.getResultColumns().size()), this.getRelativeName(), String.valueOf(5000));
        }
        this.genColumnInfos(this.colInfos = new ColumnInfo[this.queryExpression.getResultColumns().visibleSize()]);
    }
    
    private ProviderInfo[] bindViewDefinition(final DataDictionary dataDictionary, final CompilerContext compilerContext, final LanguageConnectionContext languageConnectionContext, final NodeFactory nodeFactory, ResultSetNode resultSetNode, final ContextManager contextManager) throws StandardException {
        final FromList list = (FromList)nodeFactory.getNode(37, nodeFactory.doJoinOrderOptimization(), contextManager);
        final ProviderList currentAuxiliaryProviderList = compilerContext.getCurrentAuxiliaryProviderList();
        final ProviderList currentAuxiliaryProviderList2 = new ProviderList();
        try {
            compilerContext.setCurrentAuxiliaryProviderList(currentAuxiliaryProviderList2);
            compilerContext.pushCurrentPrivType(0);
            resultSetNode = resultSetNode.bindNonVTITables(dataDictionary, list);
            resultSetNode = resultSetNode.bindVTITables(list);
            resultSetNode.bindExpressions(list);
            if (resultSetNode instanceof SelectNode && resultSetNode.referencesSessionSchema()) {
                throw StandardException.newException("XCL51.S");
            }
            resultSetNode.bindResultColumns(list);
            resultSetNode.bindUntypedNullsToResultColumns(null);
        }
        finally {
            compilerContext.popCurrentPrivType();
            compilerContext.setCurrentAuxiliaryProviderList(currentAuxiliaryProviderList);
        }
        final DependencyManager dependencyManager = dataDictionary.getDependencyManager();
        final ProviderInfo[] persistentProviderInfos = dependencyManager.getPersistentProviderInfos(currentAuxiliaryProviderList2);
        dependencyManager.clearColumnInfoInProviders(currentAuxiliaryProviderList2);
        return persistentProviderInfos;
    }
    
    public boolean referencesSessionSchema() throws StandardException {
        return this.queryExpression.referencesSessionSchema();
    }
    
    public ConstantAction makeConstantAction() throws StandardException {
        return this.getGenericConstantActionFactory().getCreateViewConstantAction(this.getSchemaDescriptor().getSchemaName(), this.getRelativeName(), 2, this.qeText, this.checkOption, this.colInfos, this.providerInfos, null);
    }
    
    private void genColumnInfos(final ColumnInfo[] array) {
        final ResultColumnList resultColumns = this.queryExpression.getResultColumns();
        for (int i = 0; i < array.length; ++i) {
            final ResultColumn resultColumn = (ResultColumn)resultColumns.elementAt(i);
            array[i] = new ColumnInfo(resultColumn.getName(), resultColumn.getType(), null, null, null, null, null, 0, 0L, 0L, 0L);
        }
    }
    
    ResultSetNode getParsedQueryExpression() {
        return this.queryExpression;
    }
    
    void acceptChildren(final Visitor visitor) throws StandardException {
        super.acceptChildren(visitor);
        if (this.queryExpression != null) {
            this.queryExpression = (ResultSetNode)this.queryExpression.accept(visitor);
        }
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
