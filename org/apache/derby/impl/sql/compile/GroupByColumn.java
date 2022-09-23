// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.impl.sql.compile;

import org.apache.derby.iapi.sql.compile.Visitor;
import org.apache.derby.iapi.types.TypeId;
import org.apache.derby.iapi.error.StandardException;
import java.util.List;

public class GroupByColumn extends OrderedColumn
{
    private ValueNode columnExpression;
    
    public void init(final Object o) {
        this.columnExpression = (ValueNode)o;
    }
    
    public void printSubNodes(final int n) {
    }
    
    public String getColumnName() {
        return this.columnExpression.getColumnName();
    }
    
    public void bindExpression(final FromList list, final SubqueryList list2, final List list3) throws StandardException {
        final int orReliability = this.orReliability(16384);
        this.columnExpression = this.columnExpression.bindExpression(list, list2, list3);
        this.getCompilerContext().setReliability(orReliability);
        if (this.columnExpression.isParameterNode()) {
            throw StandardException.newException("42Y36", this.columnExpression);
        }
        final TypeId typeId = this.columnExpression.getTypeId();
        if (!typeId.orderable(this.getClassFactory())) {
            throw StandardException.newException("X0X67.S", typeId.getSQLTypeName());
        }
    }
    
    public ValueNode getColumnExpression() {
        return this.columnExpression;
    }
    
    public void setColumnExpression(final ValueNode columnExpression) {
        this.columnExpression = columnExpression;
    }
    
    void acceptChildren(final Visitor visitor) throws StandardException {
        super.acceptChildren(visitor);
        if (this.columnExpression != null) {
            this.columnExpression = (ValueNode)this.columnExpression.accept(visitor);
        }
    }
}
