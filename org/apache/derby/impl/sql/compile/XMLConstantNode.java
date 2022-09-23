// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.impl.sql.compile;

import org.apache.derby.iapi.services.compiler.MethodBuilder;
import org.apache.derby.iapi.error.StandardException;
import org.apache.derby.iapi.util.ReuseFactory;

public final class XMLConstantNode extends ConstantNode
{
    public void init(final Object o) throws StandardException {
        super.init(o, Boolean.TRUE, ReuseFactory.getInteger(0));
    }
    
    Object getConstantValueAsObject() throws StandardException {
        return this.value.getObject();
    }
    
    void generateConstant(final ExpressionClassBuilder expressionClassBuilder, final MethodBuilder methodBuilder) throws StandardException {
        methodBuilder.push(this.value.getString());
    }
}
