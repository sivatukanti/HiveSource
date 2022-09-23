// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.impl.sql.compile;

import org.apache.derby.iapi.sql.ResultDescription;
import org.apache.derby.iapi.util.JBitSet;
import org.apache.derby.iapi.sql.compile.Visitor;
import java.util.List;
import org.apache.derby.iapi.error.StandardException;
import org.apache.derby.iapi.sql.dictionary.DataDictionary;

abstract class DMLStatementNode extends StatementNode
{
    ResultSetNode resultSet;
    
    public void init(final Object o) {
        this.resultSet = (ResultSetNode)o;
    }
    
    public void printSubNodes(final int n) {
    }
    
    public ResultSetNode getResultSetNode() {
        return this.resultSet;
    }
    
    QueryTreeNode bind(final DataDictionary dataDictionary) throws StandardException {
        this.getCompilerContext().pushCurrentPrivType(this.getPrivType());
        try {
            this.bindTables(dataDictionary);
            this.bindExpressions();
        }
        finally {
            this.getCompilerContext().popCurrentPrivType();
        }
        return this;
    }
    
    public QueryTreeNode bindResultSetsWithTables(final DataDictionary dataDictionary) throws StandardException {
        this.bindTables(dataDictionary);
        this.bindExpressionsWithTables();
        return this;
    }
    
    protected void bindTables(final DataDictionary dataDictionary) throws StandardException {
        this.resultSet = this.resultSet.bindNonVTITables(dataDictionary, (FromList)this.getNodeFactory().getNode(37, this.getNodeFactory().doJoinOrderOptimization(), this.getContextManager()));
        this.resultSet = this.resultSet.bindVTITables((FromList)this.getNodeFactory().getNode(37, this.getNodeFactory().doJoinOrderOptimization(), this.getContextManager()));
    }
    
    protected void bindExpressions() throws StandardException {
        this.resultSet.bindExpressions((FromList)this.getNodeFactory().getNode(37, this.getNodeFactory().doJoinOrderOptimization(), this.getContextManager()));
    }
    
    protected void bindExpressionsWithTables() throws StandardException {
        this.resultSet.bindExpressionsWithTables((FromList)this.getNodeFactory().getNode(37, this.getNodeFactory().doJoinOrderOptimization(), this.getContextManager()));
    }
    
    int activationKind() {
        final List parameterList = this.getCompilerContext().getParameterList();
        if (parameterList != null && !parameterList.isEmpty()) {
            return 2;
        }
        return 1;
    }
    
    public void optimizeStatement() throws StandardException {
        this.resultSet = this.resultSet.preprocess(this.getCompilerContext().getNumTables(), null, null);
        this.accept(new ConstantExpressionVisitor());
        this.resultSet = this.resultSet.optimize(this.getDataDictionary(), null, 1.0);
        this.resultSet = this.resultSet.modifyAccessPaths();
        if (this instanceof CursorNode) {
            final ResultSetNode resultSet = this.resultSet;
            final ResultColumnList resultColumns = this.resultSet.getResultColumns();
            final ResultColumnList copyListAndObjects = resultColumns.copyListAndObjects();
            this.resultSet.setResultColumns(copyListAndObjects);
            resultColumns.genVirtualColumnNodes(this.resultSet, copyListAndObjects);
            this.resultSet = (ResultSetNode)this.getNodeFactory().getNode(123, this.resultSet, resultColumns, null, this.getContextManager());
            if (resultSet.getReferencedTableMap() != null) {
                this.resultSet.setReferencedTableMap((JBitSet)resultSet.getReferencedTableMap().clone());
            }
        }
    }
    
    public ResultDescription makeResultDescription() {
        return this.getExecutionFactory().getResultDescription(this.resultSet.makeResultDescriptors(), this.statementToString());
    }
    
    void generateParameterValueSet(final ActivationClassBuilder activationClassBuilder) throws StandardException {
        final List parameterList = this.getCompilerContext().getParameterList();
        final int n = (parameterList == null) ? 0 : parameterList.size();
        if (n <= 0) {
            return;
        }
        ParameterNode.generateParameterValueSet(activationClassBuilder, n, parameterList);
    }
    
    public boolean isAtomic() throws StandardException {
        final HasNodeVisitor hasNodeVisitor = new HasNodeVisitor(FromBaseTable.class, StaticMethodCallNode.class);
        this.accept(hasNodeVisitor);
        return hasNodeVisitor.hasNode();
    }
    
    void acceptChildren(final Visitor visitor) throws StandardException {
        super.acceptChildren(visitor);
        if (this.resultSet != null) {
            this.resultSet = (ResultSetNode)this.resultSet.accept(visitor);
        }
    }
    
    int getPrivType() {
        return 0;
    }
}
