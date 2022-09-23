// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.impl.sql.compile;

import org.apache.derby.iapi.services.compiler.MethodBuilder;
import org.apache.derby.iapi.types.StringDataValue;
import java.util.List;
import org.apache.derby.iapi.error.StandardException;
import org.apache.derby.iapi.types.DataValueDescriptor;
import org.apache.derby.iapi.util.ReuseFactory;
import org.apache.derby.iapi.types.TypeId;

public final class CharConstantNode extends ConstantNode
{
    public void init(final Object o) throws StandardException {
        if (o instanceof TypeId) {
            super.init(o, Boolean.TRUE, ReuseFactory.getInteger(0));
        }
        else {
            final String s = (String)o;
            super.init(TypeId.CHAR_ID, (s == null) ? Boolean.TRUE : Boolean.FALSE, (s != null) ? ReuseFactory.getInteger(s.length()) : ReuseFactory.getInteger(0));
            this.setValue(this.getDataValueFactory().getCharDataValue(s));
        }
    }
    
    public void init(final Object o, final Object o2) throws StandardException {
        String string = (String)o;
        final int intValue = (int)o2;
        super.init(TypeId.CHAR_ID, (string == null) ? Boolean.TRUE : Boolean.FALSE, o2);
        if (string.length() > intValue) {
            throw StandardException.newException("22001", "CHAR", string, String.valueOf(intValue));
        }
        while (string.length() < intValue) {
            string += ' ';
        }
        this.setValue(this.getDataValueFactory().getCharDataValue(string));
    }
    
    public String getString() throws StandardException {
        return this.value.getString();
    }
    
    Object getConstantValueAsObject() throws StandardException {
        return this.value.getString();
    }
    
    public ValueNode bindExpression(final FromList list, final SubqueryList list2, final List list3) throws StandardException {
        this.setCollationUsingCompilationSchema();
        this.value = ((StringDataValue)this.value).getValue(this.getLanguageConnectionContext().getDataValueFactory().getCharacterCollator(this.getTypeServices().getCollationType()));
        return this;
    }
    
    void generateConstant(final ExpressionClassBuilder expressionClassBuilder, final MethodBuilder methodBuilder) throws StandardException {
        methodBuilder.push(this.getString());
    }
}
