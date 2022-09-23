// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.impl.sql.compile;

import org.apache.derby.iapi.util.JBitSet;
import org.apache.derby.iapi.sql.compile.Visitor;
import org.apache.derby.iapi.services.compiler.LocalField;
import org.apache.derby.iapi.services.compiler.MethodBuilder;
import org.apache.derby.iapi.error.StandardException;
import java.util.List;

public class CoalesceFunctionNode extends ValueNode
{
    String functionName;
    ValueNodeList argumentsList;
    private int firstNonParameterNodeIdx;
    
    public CoalesceFunctionNode() {
        this.firstNonParameterNodeIdx = -1;
    }
    
    public void init(final Object o, final Object o2) {
        this.functionName = (String)o;
        this.argumentsList = (ValueNodeList)o2;
    }
    
    public ValueNode bindExpression(final FromList list, final SubqueryList list2, final List list3) throws StandardException {
        this.argumentsList.bindExpression(list, list2, list3);
        if (this.argumentsList.size() < 2) {
            throw StandardException.newException("42605", this.functionName);
        }
        if (this.argumentsList.containsAllParameterNodes()) {
            throw StandardException.newException("42610");
        }
        final int size = this.argumentsList.size();
        for (int i = 0; i < size; ++i) {
            if (!((ValueNode)this.argumentsList.elementAt(i)).requiresTypeFromContext()) {
                this.firstNonParameterNodeIdx = i;
                break;
            }
        }
        for (int j = 0; j < size; ++j) {
            if (!((ValueNode)this.argumentsList.elementAt(j)).requiresTypeFromContext()) {
                this.argumentsList.compatible((ValueNode)this.argumentsList.elementAt(j));
            }
        }
        this.setType(this.argumentsList.getDominantTypeServices());
        for (int k = 0; k < size; ++k) {
            if (((ValueNode)this.argumentsList.elementAt(k)).requiresTypeFromContext()) {
                ((ValueNode)this.argumentsList.elementAt(k)).setType(this.getTypeServices());
            }
        }
        return this;
    }
    
    public void generateExpression(final ExpressionClassBuilder expressionClassBuilder, final MethodBuilder methodBuilder) throws StandardException {
        final int size = this.argumentsList.size();
        final String s = "org.apache.derby.iapi.types.DataValueDescriptor";
        final LocalField fieldDeclaration = expressionClassBuilder.newFieldDeclaration(2, "org.apache.derby.iapi.types.DataValueDescriptor[]");
        final MethodBuilder constructor = expressionClassBuilder.getConstructor();
        constructor.pushNewArray("org.apache.derby.iapi.types.DataValueDescriptor", size);
        constructor.setField(fieldDeclaration);
        int n = 0;
        MethodBuilder generatedFun = null;
        MethodBuilder methodBuilder2 = constructor;
        for (int i = 0; i < size; ++i) {
            MethodBuilder methodBuilder3;
            if (this.argumentsList.elementAt(i) instanceof ConstantNode) {
                ++n;
                if (methodBuilder2.statementNumHitLimit(1)) {
                    final MethodBuilder generatedFun2 = expressionClassBuilder.newGeneratedFun("void", 2);
                    methodBuilder2.pushThis();
                    methodBuilder2.callMethod((short)182, null, generatedFun2.getName(), "void", 0);
                    if (methodBuilder2 != constructor) {
                        methodBuilder2.methodReturn();
                        methodBuilder2.complete();
                    }
                    methodBuilder2 = generatedFun2;
                }
                methodBuilder3 = methodBuilder2;
            }
            else {
                if (generatedFun == null) {
                    generatedFun = expressionClassBuilder.newGeneratedFun("void", 4);
                }
                methodBuilder3 = generatedFun;
            }
            methodBuilder3.getField(fieldDeclaration);
            ((ValueNode)this.argumentsList.elementAt(i)).generateExpression(expressionClassBuilder, methodBuilder3);
            methodBuilder3.upCast(s);
            methodBuilder3.setArrayElement(i);
        }
        if (methodBuilder2 != constructor) {
            methodBuilder2.methodReturn();
            methodBuilder2.complete();
        }
        if (generatedFun != null) {
            generatedFun.methodReturn();
            generatedFun.complete();
            methodBuilder.pushThis();
            methodBuilder.callMethod((short)182, null, generatedFun.getName(), "void", 0);
        }
        ((ValueNode)this.argumentsList.elementAt(this.firstNonParameterNodeIdx)).generateExpression(expressionClassBuilder, methodBuilder);
        methodBuilder.upCast("org.apache.derby.iapi.types.DataValueDescriptor");
        methodBuilder.getField(fieldDeclaration);
        final LocalField fieldDeclaration2 = expressionClassBuilder.newFieldDeclaration(2, s);
        expressionClassBuilder.generateNull(methodBuilder, this.getTypeCompiler(), this.getTypeServices().getCollationType());
        methodBuilder.upCast("org.apache.derby.iapi.types.DataValueDescriptor");
        methodBuilder.putField(fieldDeclaration2);
        methodBuilder.callMethod((short)185, s, "coalesce", s, 2);
        if (this.getTypeId().variableLength()) {
            final boolean numericTypeId = this.getTypeId().isNumericTypeId();
            methodBuilder.dup();
            methodBuilder.push(numericTypeId ? this.getTypeServices().getPrecision() : this.getTypeServices().getMaximumWidth());
            methodBuilder.push(this.getTypeServices().getScale());
            methodBuilder.push(true);
            methodBuilder.callMethod((short)185, "org.apache.derby.iapi.types.VariableSizeDataValue", "setWidth", "void", 3);
        }
    }
    
    public String toString() {
        return "";
    }
    
    public void printSubNodes(final int n) {
    }
    
    protected boolean isEquivalent(final ValueNode valueNode) throws StandardException {
        return this.isSameNodeType(valueNode) && this.argumentsList.isEquivalent(((CoalesceFunctionNode)valueNode).argumentsList);
    }
    
    void acceptChildren(final Visitor visitor) throws StandardException {
        super.acceptChildren(visitor);
        this.argumentsList = (ValueNodeList)this.argumentsList.accept(visitor);
    }
    
    public boolean categorize(final JBitSet set, final boolean b) throws StandardException {
        return this.argumentsList.categorize(set, b);
    }
    
    public ValueNode preprocess(final int n, final FromList list, final SubqueryList list2, final PredicateList list3) throws StandardException {
        this.argumentsList.preprocess(n, list, list2, list3);
        return this;
    }
    
    public ValueNode remapColumnReferencesToExpressions() throws StandardException {
        this.argumentsList = this.argumentsList.remapColumnReferencesToExpressions();
        return this;
    }
}
