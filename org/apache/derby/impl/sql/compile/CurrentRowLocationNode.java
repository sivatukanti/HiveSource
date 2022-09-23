// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.impl.sql.compile;

import org.apache.derby.iapi.services.compiler.LocalField;
import org.apache.derby.iapi.services.compiler.MethodBuilder;
import org.apache.derby.iapi.error.StandardException;
import org.apache.derby.iapi.types.DataTypeDescriptor;
import org.apache.derby.iapi.types.TypeId;
import java.util.List;

public class CurrentRowLocationNode extends ValueNode
{
    public ValueNode bindExpression(final FromList list, final SubqueryList list2, final List list3) throws StandardException {
        this.setType(new DataTypeDescriptor(TypeId.getBuiltInTypeId("REF"), false));
        return this;
    }
    
    public void generateExpression(final ExpressionClassBuilder expressionClassBuilder, final MethodBuilder methodBuilder) throws StandardException {
        final MethodBuilder generatedFun = expressionClassBuilder.newGeneratedFun("org.apache.derby.iapi.types.DataValueDescriptor", 4);
        final LocalField fieldDeclaration = expressionClassBuilder.newFieldDeclaration(2, "org.apache.derby.iapi.types.RefDataValue");
        generatedFun.pushThis();
        generatedFun.getField(null, expressionClassBuilder.getRowLocationScanResultSetName(), "org.apache.derby.iapi.sql.execute.CursorResultSet");
        generatedFun.callMethod((short)185, null, "getRowLocation", "org.apache.derby.iapi.types.RowLocation", 0);
        expressionClassBuilder.generateDataValue(generatedFun, this.getTypeCompiler(), this.getTypeServices().getCollationType(), fieldDeclaration);
        generatedFun.putField(fieldDeclaration);
        generatedFun.methodReturn();
        generatedFun.complete();
        methodBuilder.pushThis();
        methodBuilder.callMethod((short)182, null, generatedFun.getName(), "org.apache.derby.iapi.types.DataValueDescriptor", 0);
    }
    
    protected boolean isEquivalent(final ValueNode valueNode) {
        return false;
    }
}
