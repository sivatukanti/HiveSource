// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.impl.sql.compile;

import java.util.List;
import org.apache.derby.iapi.error.StandardException;
import org.apache.derby.iapi.types.DataValueDescriptor;
import org.apache.derby.iapi.types.DataTypeDescriptor;
import org.apache.derby.iapi.services.compiler.MethodBuilder;

public final class UntypedNullConstantNode extends ConstantNode
{
    void generateConstant(final ExpressionClassBuilder expressionClassBuilder, final MethodBuilder methodBuilder) {
    }
    
    public DataValueDescriptor convertDefaultNode(final DataTypeDescriptor dataTypeDescriptor) throws StandardException {
        return dataTypeDescriptor.getNull();
    }
    
    public ValueNode bindExpression(final FromList list, final SubqueryList list2, final List list3) {
        return this;
    }
}
