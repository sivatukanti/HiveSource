// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.impl.sql.compile;

import java.util.List;
import org.apache.derby.iapi.error.StandardException;
import org.apache.derby.iapi.types.TypeId;

public final class RowNumberFunctionNode extends WindowFunctionNode
{
    public void init(final Object o, final Object o2) throws StandardException {
        super.init(o, "ROW_NUMBER", o2);
        this.setType(TypeId.getBuiltInTypeId(-5), 19, 0, false, 8);
    }
    
    public ValueNode bindExpression(final FromList list, final SubqueryList list2, final List list3) throws StandardException {
        super.bindExpression(list, list2, list3);
        return this;
    }
}
