// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.impl.sql.compile;

import org.apache.derby.iapi.services.compiler.MethodBuilder;
import org.apache.derby.iapi.error.StandardException;
import org.apache.derby.iapi.types.DataTypeDescriptor;
import org.apache.derby.iapi.sql.dictionary.DataDictionary;
import java.util.List;

public class SpecialFunctionNode extends ValueNode
{
    String sqlName;
    private String methodName;
    private String methodType;
    
    public ValueNode bindExpression(final FromList list, final SubqueryList list2, final List list3) throws StandardException {
        final int nodeType = this.getNodeType();
        DataTypeDescriptor type = null;
        switch (nodeType) {
            case 109:
            case 110:
            case 126: {
                switch (nodeType) {
                    case 110: {
                        this.sqlName = "USER";
                        break;
                    }
                    case 109: {
                        this.sqlName = "CURRENT_USER";
                        break;
                    }
                    case 126: {
                        this.sqlName = "SYSTEM_USER";
                        break;
                    }
                }
                this.methodName = "getCurrentUserId";
                this.methodType = "java.lang.String";
                type = DataDictionary.TYPE_SYSTEM_IDENTIFIER;
                break;
            }
            case 125: {
                this.methodName = "getSessionUserId";
                this.methodType = "java.lang.String";
                this.sqlName = "SESSION_USER";
                type = DataDictionary.TYPE_SYSTEM_IDENTIFIER;
                break;
            }
            case 6: {
                this.sqlName = "CURRENT SCHEMA";
                this.methodName = "getCurrentSchemaName";
                this.methodType = "java.lang.String";
                type = DataDictionary.TYPE_SYSTEM_IDENTIFIER;
                break;
            }
            case 210: {
                this.sqlName = "CURRENT_ROLE";
                this.methodName = "getCurrentRoleIdDelimited";
                this.methodType = "java.lang.String";
                type = DataTypeDescriptor.getBuiltInDataTypeDescriptor(12, true, 258);
                break;
            }
            case 5: {
                this.sqlName = "IDENTITY_VAL_LOCAL";
                this.methodName = "getIdentityValue";
                this.methodType = "java.lang.Long";
                type = DataTypeDescriptor.getSQLDataTypeDescriptor("java.math.BigDecimal", 31, 0, true, 31);
                break;
            }
            case 4: {
                this.sqlName = "CURRENT ISOLATION";
                this.methodName = "getCurrentIsolationLevelStr";
                this.methodType = "java.lang.String";
                type = DataTypeDescriptor.getBuiltInDataTypeDescriptor(1, 2);
                break;
            }
            default: {
                type = null;
                break;
            }
        }
        this.checkReliability(this.sqlName, 64);
        this.setType(type);
        return this;
    }
    
    protected int getOrderableVariantType() {
        return 2;
    }
    
    public void generateExpression(final ExpressionClassBuilder expressionClassBuilder, final MethodBuilder methodBuilder) throws StandardException {
        methodBuilder.pushThis();
        methodBuilder.callMethod((short)185, "org.apache.derby.iapi.sql.Activation", "getLanguageConnectionContext", "org.apache.derby.iapi.sql.conn.LanguageConnectionContext", 0);
        int n = 0;
        if (this.methodName.equals("getCurrentRoleIdDelimited") || this.methodName.equals("getCurrentSchemaName") || this.methodName.equals("getCurrentUserId")) {
            expressionClassBuilder.pushThisAsActivation(methodBuilder);
            ++n;
        }
        methodBuilder.callMethod((short)185, null, this.methodName, this.methodType, n);
        expressionClassBuilder.generateDataValue(methodBuilder, this.getTypeCompiler(), this.getTypeServices().getCollationType(), expressionClassBuilder.newFieldDeclaration(2, this.getTypeCompiler().interfaceName()));
    }
    
    public String toString() {
        return "";
    }
    
    protected boolean isEquivalent(final ValueNode valueNode) {
        return this.isSameNodeType(valueNode) && this.methodName.equals(((SpecialFunctionNode)valueNode).methodName);
    }
}
