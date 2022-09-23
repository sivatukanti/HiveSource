// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.impl.sql.compile;

import org.apache.derby.iapi.services.loader.ClassInspector;
import org.apache.derby.iapi.services.compiler.MethodBuilder;
import java.util.List;
import org.apache.derby.iapi.util.JBitSet;
import org.apache.derby.iapi.types.TypeId;
import org.apache.derby.iapi.error.StandardException;
import org.apache.derby.iapi.types.DataTypeDescriptor;
import org.apache.derby.iapi.services.compiler.LocalField;
import org.apache.derby.iapi.types.JSQLType;

abstract class JavaValueNode extends QueryTreeNode
{
    private boolean mustCastToPrimitive;
    protected boolean forCallStatement;
    private boolean valueReturnedToSQLDomain;
    private boolean returnValueDiscarded;
    protected JSQLType jsqlType;
    private LocalField receiverField;
    private int collationType;
    
    public DataTypeDescriptor getDataType() throws StandardException {
        return DataTypeDescriptor.getSQLDataTypeDescriptor(this.getJavaTypeName());
    }
    
    public boolean isPrimitiveType() throws StandardException {
        final JSQLType jsqlType = this.getJSQLType();
        return jsqlType != null && jsqlType.getCategory() == 2;
    }
    
    public String getJavaTypeName() throws StandardException {
        final JSQLType jsqlType = this.getJSQLType();
        if (jsqlType == null) {
            return "";
        }
        switch (jsqlType.getCategory()) {
            case 1: {
                return jsqlType.getJavaClassName();
            }
            case 2: {
                return JSQLType.getPrimitiveName(jsqlType.getPrimitiveKind());
            }
            default: {
                return "";
            }
        }
    }
    
    public void setJavaTypeName(final String s) {
        this.jsqlType = new JSQLType(s);
    }
    
    public String getPrimitiveTypeName() throws StandardException {
        final JSQLType jsqlType = this.getJSQLType();
        if (jsqlType == null) {
            return "";
        }
        switch (jsqlType.getCategory()) {
            case 2: {
                return JSQLType.getPrimitiveName(jsqlType.getPrimitiveKind());
            }
            default: {
                return "";
            }
        }
    }
    
    public void castToPrimitive(final boolean mustCastToPrimitive) {
        this.mustCastToPrimitive = mustCastToPrimitive;
    }
    
    public boolean mustCastToPrimitive() {
        return this.mustCastToPrimitive;
    }
    
    public JSQLType getJSQLType() throws StandardException {
        return this.jsqlType;
    }
    
    public static TypeId mapToTypeID(final JSQLType jsqlType) throws StandardException {
        final DataTypeDescriptor sqlType = jsqlType.getSQLType();
        if (sqlType == null) {
            return null;
        }
        return sqlType.getTypeId();
    }
    
    public void markForCallStatement() {
        this.forCallStatement = true;
    }
    
    public abstract JavaValueNode remapColumnReferencesToExpressions() throws StandardException;
    
    public abstract boolean categorize(final JBitSet p0, final boolean p1) throws StandardException;
    
    abstract JavaValueNode bindExpression(final FromList p0, final SubqueryList p1, final List p2) throws StandardException;
    
    public abstract void preprocess(final int p0, final FromList p1, final SubqueryList p2, final PredicateList p3) throws StandardException;
    
    Object getConstantValueAsObject() throws StandardException {
        return null;
    }
    
    protected final void generate(final ActivationClassBuilder activationClassBuilder, final MethodBuilder methodBuilder) throws StandardException {
        this.generateExpression(activationClassBuilder, methodBuilder);
    }
    
    protected boolean generateReceiver(final ExpressionClassBuilder expressionClassBuilder, final MethodBuilder methodBuilder) throws StandardException {
        return false;
    }
    
    protected int getOrderableVariantType() throws StandardException {
        return 0;
    }
    
    protected abstract void generateExpression(final ExpressionClassBuilder p0, final MethodBuilder p1) throws StandardException;
    
    protected final boolean generateReceiver(final ExpressionClassBuilder expressionClassBuilder, final MethodBuilder methodBuilder, final JavaValueNode javaValueNode) throws StandardException {
        if (!this.valueReturnedToSQLDomain() && ClassInspector.primitiveType(this.getJavaTypeName())) {
            return false;
        }
        this.receiverField = expressionClassBuilder.newFieldDeclaration(2, javaValueNode.getJavaTypeName());
        javaValueNode.generateExpression(expressionClassBuilder, methodBuilder);
        methodBuilder.putField(this.receiverField);
        return true;
    }
    
    protected final void getReceiverExpression(final ExpressionClassBuilder expressionClassBuilder, final MethodBuilder methodBuilder, final JavaValueNode javaValueNode) throws StandardException {
        if (this.receiverField != null) {
            methodBuilder.getField(this.receiverField);
        }
        else {
            javaValueNode.generateExpression(expressionClassBuilder, methodBuilder);
        }
    }
    
    protected void returnValueToSQLDomain() {
        this.valueReturnedToSQLDomain = true;
    }
    
    protected boolean valueReturnedToSQLDomain() {
        return this.valueReturnedToSQLDomain;
    }
    
    protected void markReturnValueDiscarded() {
        this.returnValueDiscarded = true;
    }
    
    protected boolean returnValueDiscarded() {
        return this.returnValueDiscarded;
    }
    
    public void checkReliability(final ValueNode valueNode) throws StandardException {
        valueNode.checkReliability(4, "42Z00.U");
    }
    
    public int getCollationType() {
        return this.collationType;
    }
    
    public void setCollationType(final int collationType) {
        this.collationType = collationType;
    }
}
