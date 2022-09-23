// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.impl.sql.compile;

import org.apache.derby.iapi.sql.compile.Visitor;
import org.apache.derby.iapi.services.compiler.MethodBuilder;
import org.apache.derby.iapi.util.JBitSet;
import java.util.List;
import org.apache.derby.iapi.types.DataTypeDescriptor;
import org.apache.derby.iapi.error.StandardException;
import org.apache.derby.iapi.types.JSQLType;
import org.apache.derby.iapi.services.compiler.LocalField;

public class SQLToJavaValueNode extends JavaValueNode
{
    ValueNode value;
    LocalField returnsNullOnNullState;
    
    public void init(final Object o) {
        this.value = (ValueNode)o;
    }
    
    public void printSubNodes(final int n) {
    }
    
    public String getJavaTypeName() throws StandardException {
        final JSQLType jsqlType = this.getJSQLType();
        if (jsqlType == null) {
            return "";
        }
        return JavaValueNode.mapToTypeID(jsqlType).getCorrespondingJavaTypeName();
    }
    
    public String getPrimitiveTypeName() throws StandardException {
        final JSQLType jsqlType = this.getJSQLType();
        if (jsqlType == null) {
            return "";
        }
        return this.getTypeCompiler(JavaValueNode.mapToTypeID(jsqlType)).getCorrespondingPrimitiveTypeName();
    }
    
    public JSQLType getJSQLType() throws StandardException {
        if (this.jsqlType == null) {
            if (this.value.requiresTypeFromContext()) {
                ParameterNode parameterOperand;
                if (this.value instanceof UnaryOperatorNode) {
                    parameterOperand = ((UnaryOperatorNode)this.value).getParameterOperand();
                }
                else {
                    parameterOperand = (ParameterNode)this.value;
                }
                this.jsqlType = parameterOperand.getJSQLType();
            }
            else {
                final DataTypeDescriptor typeServices = this.value.getTypeServices();
                if (typeServices != null) {
                    this.jsqlType = new JSQLType(typeServices);
                }
            }
        }
        return this.jsqlType;
    }
    
    public JavaValueNode bindExpression(final FromList list, final SubqueryList list2, final List list3) throws StandardException {
        this.value = this.value.bindExpression(list, list2, list3);
        return this;
    }
    
    public DataTypeDescriptor getDataType() throws StandardException {
        return this.value.getTypeServices();
    }
    
    public JavaValueNode remapColumnReferencesToExpressions() throws StandardException {
        this.value = this.value.remapColumnReferencesToExpressions();
        return this;
    }
    
    public boolean categorize(final JBitSet set, final boolean b) throws StandardException {
        return this.value.categorize(set, b);
    }
    
    public void preprocess(final int n, final FromList list, final SubqueryList list2, final PredicateList list3) throws StandardException {
        this.value.preprocess(n, list, list2, list3);
    }
    
    protected int getOrderableVariantType() throws StandardException {
        return this.value.getOrderableVariantType();
    }
    
    public void generateExpression(final ExpressionClassBuilder expressionClassBuilder, final MethodBuilder methodBuilder) throws StandardException {
        this.generateSQLValue(expressionClassBuilder, methodBuilder);
        this.generateJavaValue(expressionClassBuilder, methodBuilder);
    }
    
    private void generateSQLValue(final ExpressionClassBuilder expressionClassBuilder, final MethodBuilder methodBuilder) throws StandardException {
        this.value.generateExpression(expressionClassBuilder, methodBuilder);
    }
    
    private void generateJavaValue(final ExpressionClassBuilder expressionClassBuilder, final MethodBuilder methodBuilder) throws StandardException {
        if (this.isPrimitiveType() || this.mustCastToPrimitive()) {
            final String correspondingPrimitiveTypeName = this.value.getTypeCompiler().getCorrespondingPrimitiveTypeName();
            final MethodBuilder generatedFun = expressionClassBuilder.newGeneratedFun(correspondingPrimitiveTypeName, 2, new String[] { this.getSQLValueInterfaceName() });
            generatedFun.getParameter(0);
            if (this.returnsNullOnNullState != null) {
                this.generateReturnsNullOnNullCheck(generatedFun);
            }
            else {
                generatedFun.dup();
                generatedFun.upCast("org.apache.derby.iapi.types.DataValueDescriptor");
                generatedFun.push(correspondingPrimitiveTypeName);
                generatedFun.callMethod((short)184, "org.apache.derby.impl.sql.execute.BaseActivation", "nullToPrimitiveTest", "void", 2);
            }
            generatedFun.callMethod((short)185, "org.apache.derby.iapi.types.DataValueDescriptor", this.value.getTypeCompiler().getPrimitiveMethodName(), correspondingPrimitiveTypeName, 0);
            generatedFun.methodReturn();
            generatedFun.complete();
            methodBuilder.pushThis();
            methodBuilder.swap();
            methodBuilder.callMethod((short)182, null, generatedFun.getName(), correspondingPrimitiveTypeName, 1);
        }
        else {
            if (this.returnsNullOnNullState != null) {
                this.generateReturnsNullOnNullCheck(methodBuilder);
            }
            methodBuilder.callMethod((short)185, "org.apache.derby.iapi.types.DataValueDescriptor", "getObject", "java.lang.Object", 0);
            methodBuilder.cast(this.value.getTypeId().getCorrespondingJavaTypeName());
        }
    }
    
    private void generateReturnsNullOnNullCheck(final MethodBuilder methodBuilder) {
        methodBuilder.dup();
        methodBuilder.callMethod((short)185, "org.apache.derby.iapi.services.io.Storable", "isNull", "boolean", 0);
        methodBuilder.conditionalIf();
        methodBuilder.push(true);
        methodBuilder.startElseCode();
        methodBuilder.getField(this.returnsNullOnNullState);
        methodBuilder.completeConditional();
        methodBuilder.setField(this.returnsNullOnNullState);
    }
    
    private String getSQLValueInterfaceName() throws StandardException {
        return this.value.getTypeCompiler().interfaceName();
    }
    
    ValueNode getSQLValueNode() {
        return this.value;
    }
    
    Object getConstantValueAsObject() throws StandardException {
        return this.value.getConstantValueAsObject();
    }
    
    void acceptChildren(final Visitor visitor) throws StandardException {
        super.acceptChildren(visitor);
        if (this.value != null) {
            this.value = (ValueNode)this.value.accept(visitor);
        }
    }
}
