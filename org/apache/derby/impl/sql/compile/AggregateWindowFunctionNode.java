// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.impl.sql.compile;

import java.util.List;
import org.apache.derby.iapi.error.StandardException;

public final class AggregateWindowFunctionNode extends WindowFunctionNode
{
    private AggregateNode aggregateFunction;
    
    public void init(final Object o, final Object o2) throws StandardException {
        super.init(null, "?", o);
        this.aggregateFunction = (AggregateNode)o2;
        throw StandardException.newException("0A000.S", "WINDOW/" + this.aggregateFunction.getAggregateName());
    }
    
    public ValueNode bindExpression(final FromList list, final SubqueryList list2, final List list3) throws StandardException {
        this.aggregateFunction.bindExpression(list, list2, list3);
        return this;
    }
    
    public void printSubNodes(final int n) {
    }
}
