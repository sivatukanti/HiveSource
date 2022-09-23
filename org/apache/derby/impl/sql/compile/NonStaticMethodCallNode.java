// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.impl.sql.compile;

import org.apache.derby.iapi.sql.compile.Visitor;
import org.apache.derby.iapi.services.compiler.MethodBuilder;
import org.apache.derby.iapi.util.JBitSet;
import java.lang.reflect.Modifier;
import org.apache.derby.iapi.services.loader.ClassInspector;
import java.util.List;
import org.apache.derby.iapi.error.StandardException;

public class NonStaticMethodCallNode extends MethodCallNode
{
    JavaValueNode receiver;
    private boolean isStatic;
    
    public void init(final Object o, final Object o2) throws StandardException {
        super.init(o);
        if (o2 instanceof JavaToSQLValueNode) {
            this.receiver = ((JavaToSQLValueNode)o2).getJavaValueNode();
        }
        else {
            this.receiver = (JavaValueNode)this.getNodeFactory().getNode(28, o2, this.getContextManager());
        }
    }
    
    public JavaValueNode bindExpression(final FromList list, final SubqueryList list2, final List list3) throws StandardException {
        if (this.receiver instanceof SQLToJavaValueNode) {
            final ValueNode sqlValueNode = ((SQLToJavaValueNode)this.receiver).getSQLValueNode();
            if (sqlValueNode.requiresTypeFromContext() && sqlValueNode.getTypeServices() == null) {
                throw StandardException.newException("42X54", this.methodName);
            }
        }
        this.bindParameters(list, list2, list3);
        this.receiver = this.receiver.bindExpression(list, list2, list3);
        final String sqlTypeName = this.receiver.getJSQLType().getSQLType().getTypeId().getSQLTypeName();
        if (sqlTypeName.equals("BLOB") || sqlTypeName.equals("CLOB") || sqlTypeName.equals("NCLOB")) {
            throw StandardException.newException("XJ082.U");
        }
        this.javaClassName = this.receiver.getJavaTypeName();
        if (ClassInspector.primitiveType(this.javaClassName)) {
            throw StandardException.newException("42X52", this.methodName, this.javaClassName);
        }
        this.resolveMethodCall(this.javaClassName, false);
        this.isStatic = Modifier.isStatic(this.method.getModifiers());
        return this;
    }
    
    public boolean categorize(final JBitSet set, final boolean b) throws StandardException {
        if (b) {
            return false;
        }
        boolean b2 = true && super.categorize(set, b);
        if (this.receiver != null) {
            b2 = (b2 && this.receiver.categorize(set, b));
        }
        return b2;
    }
    
    protected int getOrderableVariantType() throws StandardException {
        int orderableVariantType = this.receiver.getOrderableVariantType();
        if (orderableVariantType > 1 && this.receiver.getJavaTypeName().equals("org.apache.derby.iapi.db.TriggerExecutionContext")) {
            orderableVariantType = 1;
        }
        final int orderableVariantType2 = super.getOrderableVariantType();
        if (orderableVariantType < orderableVariantType2) {
            return orderableVariantType;
        }
        return orderableVariantType2;
    }
    
    public JavaValueNode remapColumnReferencesToExpressions() throws StandardException {
        if (this.receiver != null) {
            this.receiver.remapColumnReferencesToExpressions();
        }
        return super.remapColumnReferencesToExpressions();
    }
    
    public void printSubNodes(final int n) {
    }
    
    public void preprocess(final int n, final FromList list, final SubqueryList list2, final PredicateList list3) throws StandardException {
        super.preprocess(n, list, list2, list3);
        this.receiver.preprocess(n, list, list2, list3);
    }
    
    public void generateExpression(final ExpressionClassBuilder expressionClassBuilder, final MethodBuilder methodBuilder) throws StandardException {
        boolean b = false;
        if (!this.valueReturnedToSQLDomain() && !this.returnValueDiscarded() && this.generateReceiver(expressionClassBuilder, methodBuilder, this.receiver)) {
            b = true;
            methodBuilder.conditionalIfNull();
            methodBuilder.pushNull(this.getJavaTypeName());
            methodBuilder.startElseCode();
        }
        final Class<?> declaringClass = this.method.getDeclaringClass();
        short n;
        if (declaringClass.isInterface()) {
            n = 185;
        }
        else if (this.isStatic) {
            n = 184;
        }
        else {
            n = 182;
        }
        this.getReceiverExpression(expressionClassBuilder, methodBuilder, this.receiver);
        if (this.isStatic) {
            methodBuilder.endStatement();
        }
        methodBuilder.callMethod(n, declaringClass.getName(), this.methodName, this.getJavaTypeName(), this.generateParameters(expressionClassBuilder, methodBuilder));
        if (b) {
            methodBuilder.completeConditional();
        }
    }
    
    protected boolean generateReceiver(final ExpressionClassBuilder expressionClassBuilder, final MethodBuilder methodBuilder) throws StandardException {
        return !this.isStatic && this.generateReceiver(expressionClassBuilder, methodBuilder, this.receiver);
    }
    
    void acceptChildren(final Visitor visitor) throws StandardException {
        super.acceptChildren(visitor);
        if (this.receiver != null) {
            this.receiver = (JavaValueNode)this.receiver.accept(visitor);
        }
    }
}
