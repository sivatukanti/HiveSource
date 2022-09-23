// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.impl.sql.compile;

import org.apache.derby.iapi.services.compiler.MethodBuilder;
import org.apache.derby.iapi.services.loader.GeneratedClass;
import org.apache.derby.iapi.util.ByteArray;
import org.apache.derby.iapi.sql.ResultDescription;
import org.apache.derby.iapi.error.StandardException;
import org.apache.derby.iapi.sql.dictionary.TableDescriptor;

public abstract class StatementNode extends QueryTreeNode
{
    static final TableDescriptor[] EMPTY_TD_LIST;
    static final int NEED_DDL_ACTIVATION = 5;
    static final int NEED_CURSOR_ACTIVATION = 4;
    static final int NEED_PARAM_ACTIVATION = 2;
    static final int NEED_ROW_ACTIVATION = 1;
    static final int NEED_NOTHING_ACTIVATION = 0;
    
    public boolean isAtomic() throws StandardException {
        return true;
    }
    
    public boolean needsSavepoint() {
        return true;
    }
    
    public String getSPSName() {
        return null;
    }
    
    public String executeStatementName() {
        return null;
    }
    
    public String executeSchemaName() {
        return null;
    }
    
    public ResultDescription makeResultDescription() {
        return null;
    }
    
    public String toString() {
        return "";
    }
    
    public abstract String statementToString();
    
    public void bindStatement() throws StandardException {
    }
    
    public void optimizeStatement() throws StandardException {
    }
    
    abstract int activationKind();
    
    protected TableDescriptor lockTableForCompilation(TableDescriptor tableDescriptor) throws StandardException {
        if (this.getDataDictionary().getCacheMode() == 1) {
            this.getLanguageConnectionContext().getTransactionCompile().openConglomerate(tableDescriptor.getHeapConglomerateId(), false, 68, 6, 5).close();
            final String name = tableDescriptor.getName();
            tableDescriptor = this.getTableDescriptor(tableDescriptor.getName(), this.getSchemaDescriptor(tableDescriptor.getSchemaName()));
            if (tableDescriptor == null) {
                throw StandardException.newException("42X05", name);
            }
        }
        return tableDescriptor;
    }
    
    public GeneratedClass generate(final ByteArray byteArray) throws StandardException {
        final int activationKind = this.activationKind();
        String s = null;
        switch (activationKind) {
            case 4: {
                s = "org.apache.derby.impl.sql.execute.CursorActivation";
                break;
            }
            case 5: {
                return this.getClassFactory().loadGeneratedClass("org.apache.derby.impl.sql.execute.ConstantActionActivation", null);
            }
            case 0:
            case 1:
            case 2: {
                s = "org.apache.derby.impl.sql.execute.BaseActivation";
                break;
            }
            default: {
                throw StandardException.newException("42Z53", String.valueOf(activationKind));
            }
        }
        final ActivationClassBuilder activationClassBuilder = new ActivationClassBuilder(s, this.getCompilerContext());
        final MethodBuilder methodBuilder = activationClassBuilder.getClassBuilder().newMethodBuilder(4, "org.apache.derby.iapi.sql.ResultSet", "createResultSet");
        methodBuilder.addThrownException("org.apache.derby.iapi.error.StandardException");
        this.generate(activationClassBuilder, methodBuilder);
        methodBuilder.methodReturn();
        methodBuilder.complete();
        activationClassBuilder.finishExecuteMethod();
        activationClassBuilder.finishConstructor();
        try {
            return activationClassBuilder.getGeneratedClass(byteArray);
        }
        catch (StandardException ex) {
            final String messageId = ex.getMessageId();
            if ("XBCM4.S".equals(messageId) || "XBCM1.S".equals(messageId)) {
                throw StandardException.newException("42ZA0", ex);
            }
            throw ex;
        }
    }
    
    public TableDescriptor[] updateIndexStatisticsFor() throws StandardException {
        return StatementNode.EMPTY_TD_LIST;
    }
    
    static {
        EMPTY_TD_LIST = new TableDescriptor[0];
    }
}
