// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.impl.sql.compile;

import org.apache.derby.iapi.types.DataTypeDescriptor;
import org.apache.derby.iapi.services.compiler.MethodBuilder;
import org.apache.derby.iapi.error.StandardException;

public class VirtualColumnNode extends ValueNode
{
    private ResultSetNode sourceResultSet;
    private ResultColumn sourceColumn;
    int columnId;
    private boolean correlated;
    
    public VirtualColumnNode() {
        this.correlated = false;
    }
    
    public void init(final Object o, final Object o2, final Object o3) throws StandardException {
        final ResultColumn sourceColumn = (ResultColumn)o2;
        this.sourceResultSet = (ResultSetNode)o;
        this.sourceColumn = sourceColumn;
        this.columnId = (int)o3;
        this.setType(sourceColumn.getTypeServices());
    }
    
    public void printSubNodes(final int n) {
    }
    
    public ResultSetNode getSourceResultSet() {
        return this.sourceResultSet;
    }
    
    public ResultColumn getSourceColumn() {
        return this.sourceColumn;
    }
    
    public String getTableName() {
        return this.sourceColumn.getTableName();
    }
    
    public String getSchemaName() throws StandardException {
        return this.sourceColumn.getSchemaName();
    }
    
    public boolean updatableByCursor() {
        return this.sourceColumn.updatableByCursor();
    }
    
    public ResultColumn getSourceResultColumn() {
        return this.sourceColumn;
    }
    
    void setCorrelated() {
        this.correlated = true;
    }
    
    boolean getCorrelated() {
        return this.correlated;
    }
    
    public boolean isCloneable() {
        return true;
    }
    
    public void generateExpression(final ExpressionClassBuilder expressionClassBuilder, final MethodBuilder methodBuilder) throws StandardException {
        final int resultSetNumber = this.sourceColumn.getResultSetNumber();
        if (this.sourceColumn.isRedundant()) {
            this.sourceColumn.getExpression().generateExpression(expressionClassBuilder, methodBuilder);
            return;
        }
        expressionClassBuilder.pushColumnReference(methodBuilder, resultSetNumber, this.sourceColumn.getVirtualColumnId());
        methodBuilder.cast(this.sourceColumn.getTypeCompiler().interfaceName());
    }
    
    protected int getOrderableVariantType() throws StandardException {
        return this.sourceColumn.getOrderableVariantType();
    }
    
    public DataTypeDescriptor getTypeServices() {
        return this.sourceColumn.getTypeServices();
    }
    
    public void setType(final DataTypeDescriptor type) throws StandardException {
        this.sourceColumn.setType(type);
    }
    
    protected boolean isEquivalent(final ValueNode valueNode) throws StandardException {
        return this.isSameNodeType(valueNode) && this.sourceColumn.isEquivalent(((VirtualColumnNode)valueNode).sourceColumn);
    }
}
