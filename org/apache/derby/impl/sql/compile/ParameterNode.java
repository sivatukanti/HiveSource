// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.impl.sql.compile;

import org.apache.derby.iapi.types.TypeId;
import org.apache.derby.iapi.services.compiler.MethodBuilder;
import java.util.List;
import org.apache.derby.iapi.error.StandardException;
import org.apache.derby.iapi.types.JSQLType;
import org.apache.derby.iapi.types.DataValueDescriptor;
import org.apache.derby.iapi.types.DataTypeDescriptor;

public class ParameterNode extends ValueNode
{
    private int parameterNumber;
    private DataTypeDescriptor[] userParameterTypes;
    private DataValueDescriptor defaultValue;
    private JSQLType jsqlType;
    private int orderableVariantType;
    private ValueNode returnOutputParameter;
    private ValueNode valToGenerate;
    
    public ParameterNode() {
        this.orderableVariantType = 2;
    }
    
    public void init(final Object o, final Object o2) {
        this.defaultValue = (DataValueDescriptor)o2;
        this.parameterNumber = (int)o;
    }
    
    int getParameterNumber() {
        return this.parameterNumber;
    }
    
    void setDescriptors(final DataTypeDescriptor[] userParameterTypes) {
        this.userParameterTypes = userParameterTypes;
    }
    
    public void setType(DataTypeDescriptor nullabilityType) throws StandardException {
        nullabilityType = nullabilityType.getNullabilityType(true);
        if (this.userParameterTypes != null) {
            this.userParameterTypes[this.parameterNumber] = nullabilityType;
        }
        super.setType(nullabilityType);
        if (this.getJSQLType() == null) {
            this.setJSQLType(new JSQLType(nullabilityType));
        }
    }
    
    public void setReturnOutputParam(final ValueNode returnOutputParameter) {
        this.returnOutputParameter = returnOutputParameter;
    }
    
    public boolean isReturnOutputParam() {
        return this.returnOutputParameter != null;
    }
    
    public ValueNode bindExpression(final FromList list, final SubqueryList list2, final List list3) throws StandardException {
        this.checkReliability("?", 8);
        return this;
    }
    
    public boolean isConstantExpression() {
        return true;
    }
    
    public boolean constantExpression(final PredicateList list) {
        return true;
    }
    
    protected int getOrderableVariantType() {
        return this.orderableVariantType;
    }
    
    void setOrderableVariantType(final int orderableVariantType) {
        this.orderableVariantType = orderableVariantType;
    }
    
    public void setJSQLType(final JSQLType jsqlType) {
        this.jsqlType = jsqlType;
    }
    
    public JSQLType getJSQLType() {
        return this.jsqlType;
    }
    
    public void generateExpression(final ExpressionClassBuilder expressionClassBuilder, final MethodBuilder methodBuilder) throws StandardException {
        if (this.valToGenerate != null) {
            this.valToGenerate.generateExpression(expressionClassBuilder, methodBuilder);
            return;
        }
        final DataTypeDescriptor typeServices = this.getTypeServices();
        if (typeServices != null && typeServices.getTypeId().isXMLTypeId()) {
            throw StandardException.newException("42Z70");
        }
        methodBuilder.pushThis();
        methodBuilder.push(this.parameterNumber);
        methodBuilder.callMethod((short)182, "org.apache.derby.impl.sql.execute.BaseActivation", "getParameter", "org.apache.derby.iapi.types.DataValueDescriptor", 1);
        switch (typeServices.getJDBCTypeId()) {
            case -4:
            case -3:
            case -2:
            case 2004: {
                methodBuilder.dup();
                methodBuilder.push(typeServices.getMaximumWidth());
                methodBuilder.callMethod((short)185, null, "checkHostVariable", "void", 1);
                break;
            }
        }
        methodBuilder.cast(this.getTypeCompiler().interfaceName());
    }
    
    public TypeId getTypeId() throws StandardException {
        return (this.returnOutputParameter != null) ? this.returnOutputParameter.getTypeId() : super.getTypeId();
    }
    
    public static void generateParameterValueSet(final ExpressionClassBuilder expressionClassBuilder, final int n, final List list) throws StandardException {
        if (n > 0) {
            final MethodBuilder constructor = expressionClassBuilder.getConstructor();
            final boolean returnOutputParam = list.get(0).isReturnOutputParam();
            constructor.pushThis();
            constructor.push(n);
            constructor.push(returnOutputParam);
            constructor.callMethod((short)182, "org.apache.derby.impl.sql.execute.BaseActivation", "setParameterValueSet", "void", 2);
            final MethodBuilder executeMethod = expressionClassBuilder.getExecuteMethod();
            executeMethod.pushThis();
            executeMethod.callMethod((short)182, "org.apache.derby.impl.sql.execute.BaseActivation", "throwIfMissingParms", "void", 0);
        }
    }
    
    DataValueDescriptor getDefaultValue() {
        return this.defaultValue;
    }
    
    public boolean requiresTypeFromContext() {
        return true;
    }
    
    public boolean isParameterNode() {
        return true;
    }
    
    protected boolean isEquivalent(final ValueNode valueNode) {
        return false;
    }
    
    protected void setValueToGenerate(final ValueNode valToGenerate) {
        this.valToGenerate = valToGenerate;
    }
}
