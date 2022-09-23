// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.iapi.sql.dictionary;

import org.apache.derby.iapi.services.monitor.Monitor;
import org.apache.derby.iapi.sql.StorablePreparedStatement;
import org.apache.derby.catalog.DependableFinder;
import org.apache.derby.iapi.services.context.ContextService;
import java.util.List;
import java.util.ArrayList;
import org.apache.derby.iapi.sql.depend.DependencyManager;
import org.apache.derby.iapi.sql.Statement;
import org.apache.derby.iapi.sql.conn.LanguageConnectionFactory;
import org.apache.derby.iapi.services.context.ContextManager;
import org.apache.derby.iapi.sql.PreparedStatement;
import org.apache.derby.iapi.sql.conn.LanguageConnectionContext;
import org.apache.derby.iapi.store.access.TransactionController;
import org.apache.derby.iapi.error.StandardException;
import org.apache.derby.iapi.services.uuid.UUIDFactory;
import java.sql.Timestamp;
import org.apache.derby.iapi.types.DataTypeDescriptor;
import org.apache.derby.iapi.sql.execute.ExecPreparedStatement;
import org.apache.derby.catalog.UUID;
import org.apache.derby.iapi.sql.depend.Provider;
import org.apache.derby.iapi.sql.depend.Dependent;

public class SPSDescriptor extends TupleDescriptor implements UniqueSQLObjectDescriptor, Dependent, Provider
{
    public static final char SPS_TYPE_TRIGGER = 'T';
    public static final char SPS_TYPE_REGULAR = 'S';
    public static final char SPS_TYPE_EXPLAIN = 'X';
    private static final int RECOMPILE = 1;
    private static final int INVALIDATE = 0;
    private final SchemaDescriptor sd;
    private final String name;
    private final UUID compSchemaId;
    private final char type;
    private String text;
    private final String usingText;
    private final UUID uuid;
    private boolean valid;
    private ExecPreparedStatement preparedStatement;
    private DataTypeDescriptor[] params;
    private Timestamp compileTime;
    private Object[] paramDefaults;
    private final boolean initiallyCompilable;
    private boolean lookedUpParams;
    private UUIDFactory uuidFactory;
    
    public SPSDescriptor(final DataDictionary dataDictionary, final String s, final UUID uuid, final UUID uuid2, final UUID uuid3, final char c, final boolean b, final String s2, final boolean b2) throws StandardException {
        this(dataDictionary, s, uuid, uuid2, uuid3, c, b, s2, null, null, null, b2);
    }
    
    public SPSDescriptor(final DataDictionary dataDictionary, final String name, final UUID uuid, final UUID uuid2, final UUID compSchemaId, final char type, final boolean valid, final String text, final String usingText, final Timestamp compileTime, final ExecPreparedStatement preparedStatement, final boolean initiallyCompilable) throws StandardException {
        super(dataDictionary);
        if (uuid == null) {
            throw new IllegalArgumentException("UUID is null");
        }
        this.name = name;
        this.uuid = uuid;
        this.type = type;
        this.text = text;
        this.usingText = usingText;
        this.valid = valid;
        this.compileTime = compileTime;
        this.sd = dataDictionary.getSchemaDescriptor(uuid2, null);
        this.preparedStatement = preparedStatement;
        this.compSchemaId = compSchemaId;
        this.initiallyCompilable = initiallyCompilable;
    }
    
    public final synchronized void prepareAndRelease(final LanguageConnectionContext languageConnectionContext, final TableDescriptor tableDescriptor, final TransactionController transactionController) throws StandardException {
        this.compileStatement(languageConnectionContext, tableDescriptor, transactionController);
        this.preparedStatement.makeInvalid(11, languageConnectionContext);
    }
    
    public final synchronized void prepareAndRelease(final LanguageConnectionContext languageConnectionContext, final TableDescriptor tableDescriptor) throws StandardException {
        this.prepareAndRelease(languageConnectionContext, tableDescriptor, null);
    }
    
    public final synchronized void prepareAndRelease(final LanguageConnectionContext languageConnectionContext) throws StandardException {
        this.prepareAndRelease(languageConnectionContext, null, null);
    }
    
    private void compileStatement(final LanguageConnectionContext languageConnectionContext, TableDescriptor tableDescriptor, final TransactionController transactionController) throws StandardException {
        final ContextManager contextManager = languageConnectionContext.getContextManager();
        final LanguageConnectionFactory languageConnectionFactory = languageConnectionContext.getLanguageConnectionFactory();
        final DataDictionary dataDictionary = this.getDataDictionary();
        if (this.type == 'T' && tableDescriptor == null) {
            tableDescriptor = dataDictionary.getTableDescriptor(this.recreateUUID(this.name.substring(49)));
        }
        if (tableDescriptor != null) {
            languageConnectionContext.pushTriggerTable(tableDescriptor);
        }
        final Statement statement = languageConnectionFactory.getStatement(dataDictionary.getSchemaDescriptor(this.compSchemaId, null), this.text, true);
        try {
            this.preparedStatement = (ExecPreparedStatement)statement.prepareStorable(languageConnectionContext, this.preparedStatement, this.getParameterDefaults(), this.getSchemaDescriptor(), this.type == 'T');
        }
        finally {
            if (tableDescriptor != null) {
                languageConnectionContext.popTriggerTable(tableDescriptor);
            }
        }
        if (this.preparedStatement.referencesSessionSchema()) {
            throw StandardException.newException("XCL51.S");
        }
        this.setCompileTime();
        this.setParams(this.preparedStatement.getParameterTypes());
        if (!dataDictionary.isReadOnlyUpgrade()) {
            dataDictionary.startWriting(languageConnectionContext);
            final DependencyManager dependencyManager = dataDictionary.getDependencyManager();
            dependencyManager.clearDependencies(languageConnectionContext, this, transactionController);
            dependencyManager.copyDependencies(this.preparedStatement, this, false, contextManager, transactionController);
            if (tableDescriptor != null) {
                dependencyManager.addDependency(this, tableDescriptor, languageConnectionContext.getContextManager());
            }
        }
        this.valid = true;
    }
    
    public final String getName() {
        return this.name;
    }
    
    public final String getQualifiedName() {
        return this.sd.getSchemaName() + "." + this.name;
    }
    
    public final SchemaDescriptor getSchemaDescriptor() {
        return this.sd;
    }
    
    public final char getType() {
        return this.type;
    }
    
    public final String getTypeAsString() {
        return String.valueOf(this.type);
    }
    
    public boolean initiallyCompilable() {
        return this.initiallyCompilable;
    }
    
    public static boolean validType(final char c) {
        return c == 'S' || c == 'T';
    }
    
    public final synchronized Timestamp getCompileTime() {
        return this.compileTime;
    }
    
    public final synchronized void setCompileTime() {
        this.compileTime = new Timestamp(System.currentTimeMillis());
    }
    
    public final synchronized String getText() {
        return this.text;
    }
    
    public final synchronized void setText(final String text) {
        this.text = text;
    }
    
    public final String getUsingText() {
        return this.usingText;
    }
    
    public final UUID getUUID() {
        return this.uuid;
    }
    
    public final synchronized DataTypeDescriptor[] getParams() throws StandardException {
        if (this.params == null && !this.lookedUpParams) {
            final ArrayList list = new ArrayList();
            this.params = this.getDataDictionary().getSPSParams(this, list);
            this.paramDefaults = list.toArray();
            this.lookedUpParams = true;
        }
        return this.params;
    }
    
    public final synchronized void setParams(final DataTypeDescriptor[] params) {
        this.params = params;
    }
    
    public final synchronized Object[] getParameterDefaults() throws StandardException {
        if (this.paramDefaults == null) {
            this.getParams();
        }
        return this.paramDefaults;
    }
    
    public final synchronized void setParameterDefaults(final Object[] paramDefaults) {
        this.paramDefaults = paramDefaults;
    }
    
    public final ExecPreparedStatement getPreparedStatement() throws StandardException {
        return this.getPreparedStatement(true);
    }
    
    public final synchronized ExecPreparedStatement getPreparedStatement(final boolean b) throws StandardException {
        if (b && (!this.valid || this.preparedStatement == null)) {
            final LanguageConnectionContext languageConnectionContext = (LanguageConnectionContext)ContextService.getFactory().getCurrentContextManager().getContext("LanguageConnectionContext");
            if (!languageConnectionContext.getDataDictionary().isReadOnlyUpgrade()) {
                final String uniqueSavepointName = languageConnectionContext.getUniqueSavepointName();
                TransactionController startNestedUserTransaction;
                try {
                    startNestedUserTransaction = languageConnectionContext.getTransactionCompile().startNestedUserTransaction(false, true);
                    startNestedUserTransaction.setNoLockWait(true);
                    startNestedUserTransaction.setSavePoint(uniqueSavepointName, null);
                }
                catch (StandardException ex2) {
                    startNestedUserTransaction = null;
                }
                try {
                    this.prepareAndRelease(languageConnectionContext, null, startNestedUserTransaction);
                    this.updateSYSSTATEMENTS(languageConnectionContext, 1, startNestedUserTransaction);
                }
                catch (StandardException ex) {
                    if (startNestedUserTransaction != null) {
                        startNestedUserTransaction.rollbackToSavePoint(uniqueSavepointName, false, null);
                    }
                    if (startNestedUserTransaction == null || !ex.isLockTimeout()) {
                        throw ex;
                    }
                    startNestedUserTransaction.commit();
                    startNestedUserTransaction.destroy();
                    startNestedUserTransaction = null;
                    this.prepareAndRelease(languageConnectionContext, null, null);
                    this.updateSYSSTATEMENTS(languageConnectionContext, 1, null);
                }
                finally {
                    if (startNestedUserTransaction != null) {
                        startNestedUserTransaction.commit();
                        startNestedUserTransaction.destroy();
                    }
                }
            }
        }
        return this.preparedStatement;
    }
    
    public final UUID getCompSchemaId() {
        return this.compSchemaId;
    }
    
    public final String toString() {
        return "";
    }
    
    public final DependableFinder getDependableFinder() {
        return this.getDependableFinder(226);
    }
    
    public final String getObjectName() {
        return this.name;
    }
    
    public final UUID getObjectID() {
        return this.uuid;
    }
    
    public final String getClassType() {
        return "StoredPreparedStatement";
    }
    
    public final synchronized boolean isValid() {
        return this.valid;
    }
    
    public final void prepareToInvalidate(final Provider provider, final int n, final LanguageConnectionContext languageConnectionContext) throws StandardException {
        switch (n) {
            case 1:
            case 2:
            case 3:
            case 4:
            case 5:
            case 6:
            case 9:
            case 10:
            case 11:
            case 12:
            case 14:
            case 15:
            case 19:
            case 20:
            case 21:
            case 22:
            case 23:
            case 27:
            case 28:
            case 29:
            case 30:
            case 33:
            case 34:
            case 37:
            case 39:
            case 40:
            case 41:
            case 42:
            case 43:
            case 46: {}
            default: {
                throw StandardException.newException("X0Y24.S", this.getDataDictionary().getDependencyManager().getActionString(n), provider.getObjectName(), this.name);
            }
        }
    }
    
    public final synchronized void makeInvalid(final int n, final LanguageConnectionContext languageConnectionContext) throws StandardException {
        final DependencyManager dependencyManager = this.getDataDictionary().getDependencyManager();
        switch (n) {
            case 1:
            case 2:
            case 3:
            case 4:
            case 5:
            case 6:
            case 9:
            case 12:
            case 14:
            case 15:
            case 19:
            case 20:
            case 21:
            case 22:
            case 23:
            case 27:
            case 28:
            case 29:
            case 30:
            case 33:
            case 34:
            case 37:
            case 39:
            case 40:
            case 41:
            case 42:
            case 43:
            case 46: {
                if (this.valid) {
                    this.valid = false;
                    this.preparedStatement = null;
                    this.updateSYSSTATEMENTS(languageConnectionContext, 0, null);
                }
                dependencyManager.invalidateFor(this, 14, languageConnectionContext);
                break;
            }
            case 13: {
                dependencyManager.clearDependencies(languageConnectionContext, this);
                break;
            }
        }
    }
    
    public final synchronized void revalidate(final LanguageConnectionContext languageConnectionContext) throws StandardException {
        this.valid = false;
        this.makeInvalid(14, languageConnectionContext);
        this.prepareAndRelease(languageConnectionContext);
        this.updateSYSSTATEMENTS(languageConnectionContext, 1, null);
    }
    
    public void loadGeneratedClass() throws StandardException {
        if (this.preparedStatement != null) {
            ((StorablePreparedStatement)this.preparedStatement).loadGeneratedClass();
        }
    }
    
    private void updateSYSSTATEMENTS(final LanguageConnectionContext languageConnectionContext, final int n, TransactionController transactionExecute) throws StandardException {
        final DataDictionary dataDictionary = this.getDataDictionary();
        if (dataDictionary.isReadOnlyUpgrade()) {
            return;
        }
        dataDictionary.startWriting(languageConnectionContext);
        if (transactionExecute == null) {
            transactionExecute = languageConnectionContext.getTransactionExecute();
        }
        dataDictionary.updateSPS(this, transactionExecute, n == 1);
    }
    
    private UUID recreateUUID(final String s) {
        if (this.uuidFactory == null) {
            this.uuidFactory = Monitor.getMonitor().getUUIDFactory();
        }
        return this.uuidFactory.recreateUUID(s);
    }
    
    public String getDescriptorType() {
        return "Statement";
    }
    
    public String getDescriptorName() {
        return this.name;
    }
}
