// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.impl.sql.compile;

import org.apache.derby.iapi.error.StandardException;
import org.apache.derby.iapi.services.compiler.MethodBuilder;

public class ScrollInsensitiveResultSetNode extends SingleChildResultSetNode
{
    public void init(final Object o, final Object o2, final Object o3) {
        this.init(o, o3);
        this.resultColumns = (ResultColumnList)o2;
    }
    
    public void generate(final ActivationClassBuilder activationClassBuilder, final MethodBuilder methodBuilder) throws StandardException {
        this.assignResultSetNumber();
        this.costEstimate = this.childResult.getFinalCostEstimate();
        activationClassBuilder.addItem(this.makeResultDescription());
        activationClassBuilder.pushGetResultSetFactoryExpression(methodBuilder);
        this.childResult.generate(activationClassBuilder, methodBuilder);
        activationClassBuilder.pushThisAsActivation(methodBuilder);
        methodBuilder.push(this.resultSetNumber);
        methodBuilder.push(this.resultColumns.size());
        methodBuilder.pushThis();
        methodBuilder.callMethod((short)182, "org.apache.derby.impl.sql.execute.BaseActivation", "getScrollable", "boolean", 0);
        methodBuilder.push(this.costEstimate.rowCount());
        methodBuilder.push(this.costEstimate.getEstimatedCost());
        methodBuilder.callMethod((short)185, null, "getScrollInsensitiveResultSet", "org.apache.derby.iapi.sql.execute.NoPutResultSet", 7);
    }
}
