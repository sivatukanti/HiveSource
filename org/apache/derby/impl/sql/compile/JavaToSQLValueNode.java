// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.impl.sql.compile;

import org.apache.derby.iapi.sql.compile.Visitor;
import org.apache.derby.iapi.util.JBitSet;
import org.apache.derby.catalog.TypeDescriptor;
import org.apache.derby.iapi.types.DataTypeDescriptor;
import java.util.List;
import org.apache.derby.iapi.sql.compile.TypeCompiler;
import org.apache.derby.iapi.services.compiler.LocalField;
import org.apache.derby.iapi.services.compiler.MethodBuilder;
import org.apache.derby.iapi.error.StandardException;

public class JavaToSQLValueNode extends ValueNode
{
    JavaValueNode javaNode;
    
    public void init(final Object o) {
        this.javaNode = (JavaValueNode)o;
    }
    
    public ValueNode preprocess(final int n, final FromList list, final SubqueryList list2, final PredicateList list3) throws StandardException {
        this.javaNode.preprocess(n, list, list2, list3);
        return this;
    }
    
    public void generateExpression(final ExpressionClassBuilder expressionClassBuilder, final MethodBuilder methodBuilder) throws StandardException {
        this.javaNode.returnValueToSQLDomain();
        final boolean generateReceiver = this.javaNode.generateReceiver(expressionClassBuilder, methodBuilder);
        if (generateReceiver) {
            final LocalField fieldDeclaration = expressionClassBuilder.newFieldDeclaration(2, this.getTypeCompiler().interfaceName());
            methodBuilder.conditionalIfNull();
            methodBuilder.getField(fieldDeclaration);
            expressionClassBuilder.generateNullWithExpress(methodBuilder, this.getTypeCompiler(), this.getTypeServices().getCollationType());
            methodBuilder.startElseCode();
        }
        this.getTypeId();
        final TypeCompiler typeCompiler = this.getTypeCompiler();
        final LocalField fieldDeclaration2 = expressionClassBuilder.newFieldDeclaration(2, typeCompiler.interfaceName());
        this.javaNode.generateExpression(expressionClassBuilder, methodBuilder);
        expressionClassBuilder.generateDataValue(methodBuilder, typeCompiler, this.getTypeServices().getCollationType(), fieldDeclaration2);
        if (generateReceiver) {
            methodBuilder.completeConditional();
        }
    }
    
    public void printSubNodes(final int n) {
    }
    
    public JavaValueNode getJavaValueNode() {
        return this.javaNode;
    }
    
    public ValueNode bindExpression(final FromList list, final SubqueryList list2, final List list3) throws StandardException {
        this.javaNode.checkReliability(this);
        this.javaNode = this.javaNode.bindExpression(list, list2, list3);
        if (this.javaNode instanceof StaticMethodCallNode) {
            final AggregateNode resolvedAggregate = ((StaticMethodCallNode)this.javaNode).getResolvedAggregate();
            if (resolvedAggregate != null) {
                return resolvedAggregate.bindExpression(list, list2, list3);
            }
        }
        final DataTypeDescriptor dataType = this.javaNode.getDataType();
        if (dataType == null) {
            throw StandardException.newException("X0X57.S", this.javaNode.getJavaTypeName());
        }
        final TypeDescriptor catalogType = dataType.getCatalogType();
        if (catalogType.isRowMultiSet() || catalogType.getTypeName().equals("java.sql.ResultSet")) {
            throw StandardException.newException("42ZB6");
        }
        this.setType(dataType);
        if (dataType.getTypeId().isStringTypeId()) {
            this.setCollationInfo(this.javaNode.getCollationType(), 1);
        }
        return this;
    }
    
    public ValueNode remapColumnReferencesToExpressions() throws StandardException {
        this.javaNode = this.javaNode.remapColumnReferencesToExpressions();
        return this;
    }
    
    public boolean categorize(final JBitSet set, final boolean b) throws StandardException {
        return this.javaNode.categorize(set, b);
    }
    
    protected int getOrderableVariantType() throws StandardException {
        return this.javaNode.getOrderableVariantType();
    }
    
    void acceptChildren(final Visitor visitor) throws StandardException {
        super.acceptChildren(visitor);
        if (this.javaNode != null) {
            this.javaNode = (JavaValueNode)this.javaNode.accept(visitor);
        }
    }
    
    protected boolean isEquivalent(final ValueNode valueNode) {
        return false;
    }
}
