// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.impl.sql.compile;

import org.apache.derby.iapi.services.compiler.LocalField;
import org.apache.derby.iapi.services.compiler.MethodBuilder;
import org.apache.derby.iapi.error.StandardException;
import org.apache.derby.iapi.types.DataTypeDescriptor;
import java.util.List;

public class CurrentDatetimeOperatorNode extends ValueNode
{
    public static final int CURRENT_DATE = 0;
    public static final int CURRENT_TIME = 1;
    public static final int CURRENT_TIMESTAMP = 2;
    private static final int[] jdbcTypeId;
    private static final String[] methodName;
    private int whichType;
    
    public void init(final Object o) {
        this.whichType = (int)o;
    }
    
    public ValueNode bindExpression(final FromList list, final SubqueryList list2, final List list3) throws StandardException {
        this.checkReliability(CurrentDatetimeOperatorNode.methodName[this.whichType], 1);
        this.setType(DataTypeDescriptor.getBuiltInDataTypeDescriptor(CurrentDatetimeOperatorNode.jdbcTypeId[this.whichType], false));
        return this;
    }
    
    protected int getOrderableVariantType() {
        return 2;
    }
    
    public void generateExpression(final ExpressionClassBuilder expressionClassBuilder, final MethodBuilder methodBuilder) throws StandardException {
        switch (this.whichType) {
            case 0: {
                expressionClassBuilder.getCurrentDateExpression(methodBuilder);
                break;
            }
            case 1: {
                expressionClassBuilder.getCurrentTimeExpression(methodBuilder);
                break;
            }
            case 2: {
                expressionClassBuilder.getCurrentTimestampExpression(methodBuilder);
                break;
            }
        }
        expressionClassBuilder.generateDataValue(methodBuilder, this.getTypeCompiler(), this.getTypeServices().getCollationType(), null);
    }
    
    public String toString() {
        return "";
    }
    
    protected boolean isEquivalent(final ValueNode valueNode) {
        return this.isSameNodeType(valueNode) && ((CurrentDatetimeOperatorNode)valueNode).whichType == this.whichType;
    }
    
    static {
        jdbcTypeId = new int[] { 91, 92, 93 };
        methodName = new String[] { "CURRENT DATE", "CURRENT TIME", "CURRENT TIMSTAMP" };
    }
}
