// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.impl.sql.compile;

import org.apache.derby.iapi.services.compiler.MethodBuilder;
import org.apache.derby.iapi.error.StandardException;
import org.apache.derby.iapi.types.DataValueDescriptor;
import org.apache.derby.iapi.types.SQLBoolean;
import org.apache.derby.iapi.util.ReuseFactory;
import org.apache.derby.iapi.types.TypeId;
import org.apache.derby.iapi.util.StringUtil;

public class SQLBooleanConstantNode extends ConstantNode
{
    public void init(final Object o) throws StandardException {
        final String s = (String)o;
        Boolean b = null;
        if (StringUtil.SQLEqualsIgnoreCase(s, "true")) {
            b = Boolean.TRUE;
        }
        else if (StringUtil.SQLEqualsIgnoreCase(s, "false")) {
            b = Boolean.FALSE;
        }
        super.init(TypeId.BOOLEAN_ID, Boolean.TRUE, ReuseFactory.getInteger(1));
        this.setValue(new SQLBoolean(b));
    }
    
    void generateConstant(final ExpressionClassBuilder expressionClassBuilder, final MethodBuilder methodBuilder) throws StandardException {
        methodBuilder.push(this.value.getBoolean());
    }
}
