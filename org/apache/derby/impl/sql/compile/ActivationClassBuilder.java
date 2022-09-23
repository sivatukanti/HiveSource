// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.impl.sql.compile;

import org.apache.derby.iapi.error.StandardException;
import org.apache.derby.iapi.sql.compile.CompilerContext;
import org.apache.derby.iapi.services.compiler.MethodBuilder;
import org.apache.derby.iapi.services.compiler.LocalField;

class ActivationClassBuilder extends ExpressionClassBuilder
{
    private LocalField targetResultSetField;
    private LocalField cursorResultSetField;
    private MethodBuilder closeActivationMethod;
    
    ActivationClassBuilder(final String s, final CompilerContext compilerContext) throws StandardException {
        super(s, null, compilerContext);
    }
    
    public String getPackageName() {
        return "org.apache.derby.exe.";
    }
    
    String getBaseClassName() {
        return "org.apache.derby.impl.sql.execute.BaseActivation";
    }
    
    public int getRowCount() throws StandardException {
        return this.myCompCtx.getNumResultSets();
    }
    
    public void setNumSubqueries() {
        final int numSubquerys = this.myCompCtx.getNumSubquerys();
        if (numSubquerys == 0) {
            return;
        }
        this.constructor.pushThis();
        this.constructor.push(numSubquerys);
        this.constructor.putField("org.apache.derby.impl.sql.execute.BaseActivation", "numSubqueries", "int");
        this.constructor.endStatement();
    }
    
    MethodBuilder startResetMethod() {
        final MethodBuilder methodBuilder = this.cb.newMethodBuilder(1, "void", "reset");
        methodBuilder.addThrownException("org.apache.derby.iapi.error.StandardException");
        methodBuilder.pushThis();
        methodBuilder.callMethod((short)183, "org.apache.derby.impl.sql.execute.BaseActivation", "reset", "void", 0);
        return methodBuilder;
    }
    
    void finishExecuteMethod() {
        if (this.executeMethod != null) {
            this.executeMethod.methodReturn();
            this.executeMethod.complete();
        }
        if (this.closeActivationMethod != null) {
            this.closeActivationMethod.methodReturn();
            this.closeActivationMethod.complete();
        }
    }
    
    void addCursorPositionCode() {
        final MethodBuilder methodBuilder = this.cb.newMethodBuilder(1, "org.apache.derby.iapi.sql.execute.CursorResultSet", "getTargetResultSet");
        methodBuilder.getField(this.targetResultSetField);
        methodBuilder.methodReturn();
        methodBuilder.complete();
        final MethodBuilder methodBuilder2 = this.cb.newMethodBuilder(1, "org.apache.derby.iapi.sql.execute.CursorResultSet", "getCursorResultSet");
        methodBuilder2.getField(this.cursorResultSetField);
        methodBuilder2.methodReturn();
        methodBuilder2.complete();
    }
    
    void rememberCursorTarget(final MethodBuilder methodBuilder) {
        this.targetResultSetField = this.cb.addField("org.apache.derby.iapi.sql.execute.CursorResultSet", "targetResultSet", 2);
        methodBuilder.cast("org.apache.derby.iapi.sql.execute.CursorResultSet");
        methodBuilder.putField(this.targetResultSetField);
        methodBuilder.cast("org.apache.derby.iapi.sql.execute.NoPutResultSet");
    }
    
    void rememberCursor(final MethodBuilder methodBuilder) {
        this.cursorResultSetField = this.cb.addField("org.apache.derby.iapi.sql.execute.CursorResultSet", "cursorResultSet", 2);
        methodBuilder.cast("org.apache.derby.iapi.sql.execute.CursorResultSet");
        methodBuilder.putField(this.cursorResultSetField);
        methodBuilder.cast("org.apache.derby.iapi.sql.ResultSet");
    }
    
    protected LocalField getCurrentSetup() {
        if (this.cdtField != null) {
            return this.cdtField;
        }
        final LocalField currentSetup = super.getCurrentSetup();
        final MethodBuilder executeMethod = this.getExecuteMethod();
        executeMethod.getField(currentSetup);
        executeMethod.callMethod((short)182, null, "forget", "void", 0);
        return currentSetup;
    }
    
    MethodBuilder getCloseActivationMethod() {
        if (this.closeActivationMethod == null) {
            (this.closeActivationMethod = this.cb.newMethodBuilder(1, "void", "closeActivationAction")).addThrownException("java.lang.Exception");
        }
        return this.closeActivationMethod;
    }
}
