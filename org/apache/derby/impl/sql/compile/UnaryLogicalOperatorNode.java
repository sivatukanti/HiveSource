// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.impl.sql.compile;

import org.apache.derby.iapi.types.DataTypeDescriptor;
import org.apache.derby.iapi.types.TypeId;
import org.apache.derby.iapi.error.StandardException;
import java.util.List;

public abstract class UnaryLogicalOperatorNode extends UnaryOperatorNode
{
    public void init(final Object o, final Object o2) {
        super.init(o, o2, o2);
    }
    
    public ValueNode bindExpression(final FromList list, final SubqueryList list2, final List list3) throws StandardException {
        this.bindOperand(list, list2, list3);
        if (!this.operand.getTypeServices().getTypeId().isBooleanTypeId()) {
            throw StandardException.newException("42X40");
        }
        this.setFullTypeInfo();
        return this;
    }
    
    protected void setFullTypeInfo() throws StandardException {
        this.setType(new DataTypeDescriptor(TypeId.BOOLEAN_ID, this.operand.getTypeServices().isNullable()));
    }
}
