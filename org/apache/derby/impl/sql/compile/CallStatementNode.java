// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.impl.sql.compile;

import org.apache.derby.iapi.sql.compile.Visitor;
import org.apache.derby.iapi.sql.ResultDescription;
import org.apache.derby.iapi.services.compiler.MethodBuilder;
import org.apache.derby.iapi.error.StandardException;
import java.util.List;

public class CallStatementNode extends DMLStatementNode
{
    private JavaToSQLValueNode methodCall;
    
    public void init(final Object o) {
        super.init(null);
        this.methodCall = (JavaToSQLValueNode)o;
        this.methodCall.getJavaValueNode().markForCallStatement();
    }
    
    public String statementToString() {
        return "CALL";
    }
    
    public void printSubNodes(final int n) {
    }
    
    public void bindStatement() throws StandardException {
        this.getDataDictionary();
        final SubqueryList list = (SubqueryList)this.getNodeFactory().getNode(11, this.getContextManager());
        this.getCompilerContext().pushCurrentPrivType(this.getPrivType());
        this.methodCall = (JavaToSQLValueNode)this.methodCall.bindExpression((FromList)this.getNodeFactory().getNode(37, this.getNodeFactory().doJoinOrderOptimization(), this.getContextManager()), list, null);
        if (list.size() != 0) {
            throw StandardException.newException("42X74");
        }
        this.checkReliability();
        this.getCompilerContext().popCurrentPrivType();
    }
    
    public void optimizeStatement() throws StandardException {
        this.getDataDictionary();
        this.methodCall = (JavaToSQLValueNode)this.methodCall.preprocess(this.getCompilerContext().getNumTables(), (FromList)this.getNodeFactory().getNode(37, this.getNodeFactory().doJoinOrderOptimization(), this.getContextManager()), null, null);
    }
    
    public void generate(final ActivationClassBuilder activationClassBuilder, final MethodBuilder methodBuilder) throws StandardException {
        this.generateParameterValueSet(activationClassBuilder);
        final JavaValueNode javaValueNode = this.methodCall.getJavaValueNode();
        javaValueNode.markReturnValueDiscarded();
        final MethodBuilder generatedFun = activationClassBuilder.newGeneratedFun("void", 1);
        generatedFun.addThrownException("java.lang.Exception");
        javaValueNode.generate(activationClassBuilder, generatedFun);
        generatedFun.endStatement();
        generatedFun.methodReturn();
        generatedFun.complete();
        activationClassBuilder.pushGetResultSetFactoryExpression(methodBuilder);
        activationClassBuilder.pushMethodReference(methodBuilder, generatedFun);
        activationClassBuilder.pushThisAsActivation(methodBuilder);
        methodBuilder.callMethod((short)185, null, "getCallStatementResultSet", "org.apache.derby.iapi.sql.ResultSet", 2);
    }
    
    public ResultDescription makeResultDescription() {
        return null;
    }
    
    void acceptChildren(final Visitor visitor) throws StandardException {
        super.acceptChildren(visitor);
        if (this.methodCall != null) {
            this.methodCall = (JavaToSQLValueNode)this.methodCall.accept(visitor);
        }
    }
    
    int getPrivType() {
        return 6;
    }
    
    private void checkReliability() throws StandardException {
        if (this.getSQLAllowedInProcedure() == 0 && this.getCompilerContext().getReliability() == 2048) {
            throw StandardException.newException("42Z9D.S.1");
        }
    }
    
    private short getSQLAllowedInProcedure() {
        return ((MethodCallNode)this.methodCall.getJavaValueNode()).routineInfo.getSQLAllowed();
    }
}
