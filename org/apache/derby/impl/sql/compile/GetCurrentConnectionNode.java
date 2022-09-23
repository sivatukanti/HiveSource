// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.impl.sql.compile;

import org.apache.derby.iapi.services.compiler.MethodBuilder;
import org.apache.derby.iapi.util.JBitSet;
import org.apache.derby.iapi.error.StandardException;
import java.util.List;

public final class GetCurrentConnectionNode extends JavaValueNode
{
    public GetCurrentConnectionNode() {
        this.setJavaTypeName("java.sql.Connection");
    }
    
    public JavaValueNode bindExpression(final FromList list, final SubqueryList list2, final List list3) throws StandardException {
        return this;
    }
    
    public void preprocess(final int n, final FromList list, final SubqueryList list2, final PredicateList list3) throws StandardException {
    }
    
    public boolean categorize(final JBitSet set, final boolean b) {
        return false;
    }
    
    public JavaValueNode remapColumnReferencesToExpressions() {
        return this;
    }
    
    void bindParameter() {
    }
    
    protected int getOrderableVariantType() {
        return 2;
    }
    
    public void generateExpression(final ExpressionClassBuilder expressionClassBuilder, final MethodBuilder methodBuilder) throws StandardException {
        methodBuilder.pushThis();
        methodBuilder.callMethod((short)182, "org.apache.derby.impl.sql.execute.BaseActivation", "getCurrentConnection", this.getJavaTypeName(), 0);
    }
    
    public void checkReliability(final ValueNode valueNode) throws StandardException {
        valueNode.checkReliability("getCurrentConnection()", 2);
    }
}
