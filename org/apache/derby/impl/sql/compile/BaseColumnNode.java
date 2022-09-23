// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.impl.sql.compile;

import org.apache.derby.iapi.services.compiler.MethodBuilder;
import org.apache.derby.iapi.error.StandardException;
import org.apache.derby.iapi.types.DataTypeDescriptor;

public class BaseColumnNode extends ValueNode
{
    private String columnName;
    private TableName tableName;
    
    public void init(final Object o, final Object o2, final Object o3) throws StandardException {
        this.columnName = (String)o;
        this.tableName = (TableName)o2;
        this.setType((DataTypeDescriptor)o3);
    }
    
    public String toString() {
        return "";
    }
    
    public String getColumnName() {
        return this.columnName;
    }
    
    public String getTableName() {
        return (this.tableName != null) ? this.tableName.getTableName() : null;
    }
    
    public String getSchemaName() throws StandardException {
        return (this.tableName != null) ? this.tableName.getSchemaName() : null;
    }
    
    public void generateExpression(final ExpressionClassBuilder expressionClassBuilder, final MethodBuilder methodBuilder) throws StandardException {
        throw StandardException.newException("42Z50", this.nodeHeader());
    }
    
    protected int getOrderableVariantType() {
        return 1;
    }
    
    protected boolean isEquivalent(final ValueNode valueNode) {
        if (this.isSameNodeType(valueNode)) {
            final BaseColumnNode baseColumnNode = (BaseColumnNode)valueNode;
            return baseColumnNode.tableName.equals(this.tableName) && baseColumnNode.columnName.equals(this.columnName);
        }
        return false;
    }
}
