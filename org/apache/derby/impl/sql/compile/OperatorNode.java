// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.impl.sql.compile;

import org.apache.derby.iapi.types.SqlXmlUtil;
import org.apache.derby.iapi.services.compiler.LocalField;
import org.apache.derby.iapi.services.compiler.MethodBuilder;

abstract class OperatorNode extends ValueNode
{
    static void pushSqlXmlUtil(final ExpressionClassBuilder expressionClassBuilder, final MethodBuilder methodBuilder, final String s, final String s2) {
        final LocalField fieldDeclaration = expressionClassBuilder.newFieldDeclaration(18, SqlXmlUtil.class.getName());
        final MethodBuilder constructor = expressionClassBuilder.getConstructor();
        constructor.pushNewStart(SqlXmlUtil.class.getName());
        constructor.pushNewComplete(0);
        constructor.putField(fieldDeclaration);
        if (s == null) {
            constructor.pop();
        }
        else {
            constructor.push(s);
            constructor.push(s2);
            constructor.callMethod((short)182, SqlXmlUtil.class.getName(), "compileXQExpr", "void", 2);
        }
        methodBuilder.getField(fieldDeclaration);
    }
}
