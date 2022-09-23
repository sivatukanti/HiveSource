// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.impl.sql.compile;

import org.apache.derby.iapi.sql.dictionary.SequenceDescriptor;
import org.apache.derby.iapi.sql.dictionary.StatementGenericPermission;
import org.apache.derby.iapi.sql.dictionary.StatementRoutinePermission;
import org.apache.derby.iapi.sql.dictionary.StatementRolePermission;
import org.apache.derby.iapi.sql.dictionary.StatementSchemaPermission;
import org.apache.derby.iapi.sql.dictionary.PrivilegedSQLObject;
import org.apache.derby.iapi.sql.dictionary.AliasDescriptor;
import org.apache.derby.catalog.UUID;
import org.apache.derby.iapi.sql.dictionary.TableDescriptor;
import org.apache.derby.iapi.services.io.FormatableBitSet;
import org.apache.derby.iapi.sql.dictionary.StatementColumnPermission;
import org.apache.derby.iapi.sql.dictionary.StatementTablePermission;
import org.apache.derby.iapi.sql.dictionary.ColumnDescriptor;
import org.apache.derby.iapi.services.context.ContextManager;
import java.util.Iterator;
import org.apache.derby.iapi.util.ReuseFactory;
import org.apache.derby.iapi.store.access.StoreCostController;
import org.apache.derby.iapi.sql.depend.Provider;
import org.apache.derby.iapi.services.compiler.JavaFactory;
import org.apache.derby.iapi.services.loader.ClassFactory;
import org.apache.derby.iapi.sql.compile.NodeFactory;
import org.apache.derby.iapi.error.StandardException;
import java.sql.SQLWarning;
import org.apache.derby.iapi.types.DataTypeDescriptor;
import org.apache.derby.iapi.store.access.SortCostController;
import java.util.HashMap;
import org.apache.derby.iapi.sql.depend.ProviderList;
import java.util.ArrayList;
import org.apache.derby.iapi.sql.dictionary.SchemaDescriptor;
import java.util.List;
import org.apache.derby.iapi.sql.depend.DependencyManager;
import org.apache.derby.iapi.sql.depend.Dependent;
import org.apache.derby.iapi.sql.compile.TypeCompilerFactory;
import org.apache.derby.iapi.sql.conn.LanguageConnectionFactory;
import org.apache.derby.iapi.sql.conn.LanguageConnectionContext;
import org.apache.derby.iapi.sql.compile.Parser;
import org.apache.derby.iapi.sql.compile.CompilerContext;
import org.apache.derby.iapi.services.context.ContextImpl;

public class CompilerContextImpl extends ContextImpl implements CompilerContext
{
    private final Parser parser;
    private final LanguageConnectionContext lcc;
    private final LanguageConnectionFactory lcf;
    private TypeCompilerFactory typeCompilerFactory;
    private Dependent currentDependent;
    private DependencyManager dm;
    private boolean firstOnStack;
    private boolean inUse;
    private int reliability;
    private int nextColumnNumber;
    private int nextTableNumber;
    private int nextSubqueryNumber;
    private int nextResultSetNumber;
    private int scanIsolationLevel;
    private int nextEquivalenceClass;
    private long nextClassName;
    private List savedObjects;
    private String classPrefix;
    private SchemaDescriptor compilationSchema;
    private ArrayList defaultSchemaStack;
    private ProviderList currentAPL;
    private boolean returnParameterFlag;
    private final HashMap storeCostControllers;
    private SortCostController sortCostController;
    private List parameterList;
    private DataTypeDescriptor[] parameterDescriptors;
    private Object cursorInfo;
    private SQLWarning warnings;
    private final ArrayList privTypeStack;
    private int currPrivType;
    private HashMap requiredColumnPrivileges;
    private HashMap requiredTablePrivileges;
    private HashMap requiredSchemaPrivileges;
    private HashMap requiredRoutinePrivileges;
    private HashMap requiredUsagePrivileges;
    private HashMap requiredRolePrivileges;
    private HashMap referencedSequences;
    
    public void cleanupOnError(final Throwable t) throws StandardException {
        this.setInUse(false);
        this.resetContext();
        if (t instanceof StandardException) {
            final int severity = ((StandardException)t).getSeverity();
            if (severity < 50000) {
                if (this.currentDependent != null) {
                    this.currentDependent.makeInvalid(0, this.lcc);
                }
                this.closeStoreCostControllers();
                this.closeSortCostControllers();
            }
            if (severity >= 40000) {
                this.popMe();
            }
        }
    }
    
    public void resetContext() {
        this.nextColumnNumber = 1;
        this.nextTableNumber = 0;
        this.nextSubqueryNumber = 0;
        this.resetNextResultSetNumber();
        this.nextEquivalenceClass = -1;
        this.compilationSchema = null;
        this.parameterList = null;
        this.parameterDescriptors = null;
        this.scanIsolationLevel = 0;
        this.warnings = null;
        this.savedObjects = null;
        this.reliability = 1024;
        this.returnParameterFlag = false;
        this.initRequiredPriv();
        this.defaultSchemaStack = null;
        this.referencedSequences = null;
    }
    
    public Parser getParser() {
        return this.parser;
    }
    
    public NodeFactory getNodeFactory() {
        return this.lcf.getNodeFactory();
    }
    
    public int getNextColumnNumber() {
        return this.nextColumnNumber++;
    }
    
    public int getNextTableNumber() {
        return this.nextTableNumber++;
    }
    
    public int getNumTables() {
        return this.nextTableNumber;
    }
    
    public int getNextSubqueryNumber() {
        return this.nextSubqueryNumber++;
    }
    
    public int getNumSubquerys() {
        return this.nextSubqueryNumber;
    }
    
    public int getNextResultSetNumber() {
        return this.nextResultSetNumber++;
    }
    
    public void resetNextResultSetNumber() {
        this.nextResultSetNumber = 0;
    }
    
    public int getNumResultSets() {
        return this.nextResultSetNumber;
    }
    
    public String getUniqueClassName() {
        return this.classPrefix.concat(Long.toHexString(this.nextClassName++));
    }
    
    public int getNextEquivalenceClass() {
        return ++this.nextEquivalenceClass;
    }
    
    public ClassFactory getClassFactory() {
        return this.lcf.getClassFactory();
    }
    
    public JavaFactory getJavaFactory() {
        return this.lcf.getJavaFactory();
    }
    
    public void setCurrentDependent(final Dependent currentDependent) {
        this.currentDependent = currentDependent;
    }
    
    public ProviderList getCurrentAuxiliaryProviderList() {
        return this.currentAPL;
    }
    
    public void setCurrentAuxiliaryProviderList(final ProviderList currentAPL) {
        this.currentAPL = currentAPL;
    }
    
    public void createDependency(final Provider provider) throws StandardException {
        if (this.dm == null) {
            this.dm = this.lcc.getDataDictionary().getDependencyManager();
        }
        this.dm.addDependency(this.currentDependent, provider, this.getContextManager());
        this.addProviderToAuxiliaryList(provider);
    }
    
    public void createDependency(final Dependent dependent, final Provider provider) throws StandardException {
        if (this.dm == null) {
            this.dm = this.lcc.getDataDictionary().getDependencyManager();
        }
        this.dm.addDependency(dependent, provider, this.getContextManager());
        this.addProviderToAuxiliaryList(provider);
    }
    
    private void addProviderToAuxiliaryList(final Provider provider) {
        if (this.currentAPL != null) {
            this.currentAPL.addProvider(provider);
        }
    }
    
    public int addSavedObject(final Object o) {
        if (this.savedObjects == null) {
            this.savedObjects = new ArrayList();
        }
        this.savedObjects.add(o);
        return this.savedObjects.size() - 1;
    }
    
    public Object[] getSavedObjects() {
        if (this.savedObjects == null) {
            return null;
        }
        final Object[] array = this.savedObjects.toArray();
        this.savedObjects = null;
        return array;
    }
    
    public void setSavedObjects(final Object[] array) {
        if (array == null) {
            return;
        }
        for (int i = 0; i < array.length; ++i) {
            this.addSavedObject(array[i]);
        }
    }
    
    public void setCursorInfo(final Object cursorInfo) {
        this.cursorInfo = cursorInfo;
    }
    
    public Object getCursorInfo() {
        return this.cursorInfo;
    }
    
    public void firstOnStack() {
        this.firstOnStack = true;
    }
    
    public boolean isFirstOnStack() {
        return this.firstOnStack;
    }
    
    public void setInUse(final boolean inUse) {
        if (!(this.inUse = inUse)) {
            this.closeStoreCostControllers();
            this.closeSortCostControllers();
        }
    }
    
    public boolean getInUse() {
        return this.inUse;
    }
    
    public void setReliability(final int reliability) {
        this.reliability = reliability;
    }
    
    public int getReliability() {
        return this.reliability;
    }
    
    public StoreCostController getStoreCostController(final long n) throws StandardException {
        final Long long1 = ReuseFactory.getLong(n);
        StoreCostController openStoreCost = this.storeCostControllers.get(long1);
        if (openStoreCost == null) {
            openStoreCost = this.lcc.getTransactionCompile().openStoreCost(n);
            this.storeCostControllers.put(long1, openStoreCost);
        }
        return openStoreCost;
    }
    
    private void closeStoreCostControllers() {
        for (final StoreCostController storeCostController : this.storeCostControllers.values()) {
            try {
                storeCostController.close();
            }
            catch (StandardException ex) {}
        }
        this.storeCostControllers.clear();
    }
    
    public SortCostController getSortCostController() throws StandardException {
        if (this.sortCostController == null) {
            this.sortCostController = this.lcc.getTransactionCompile().openSortCostController();
        }
        return this.sortCostController;
    }
    
    private void closeSortCostControllers() {
        if (this.sortCostController != null) {
            this.sortCostController.close();
            this.sortCostController = null;
        }
    }
    
    public SchemaDescriptor getCompilationSchema() {
        return this.compilationSchema;
    }
    
    public SchemaDescriptor setCompilationSchema(final SchemaDescriptor compilationSchema) {
        final SchemaDescriptor compilationSchema2 = this.compilationSchema;
        this.compilationSchema = compilationSchema;
        return compilationSchema2;
    }
    
    public void pushCompilationSchema(final SchemaDescriptor compilationSchema) {
        if (this.defaultSchemaStack == null) {
            this.defaultSchemaStack = new ArrayList(2);
        }
        this.defaultSchemaStack.add(this.defaultSchemaStack.size(), this.getCompilationSchema());
        this.setCompilationSchema(compilationSchema);
    }
    
    public void popCompilationSchema() {
        this.setCompilationSchema(this.defaultSchemaStack.remove(this.defaultSchemaStack.size() - 1));
    }
    
    public void setParameterList(final List parameterList) {
        this.parameterList = parameterList;
        final int n = (parameterList == null) ? 0 : parameterList.size();
        if (n > 0) {
            this.parameterDescriptors = new DataTypeDescriptor[n];
        }
    }
    
    public List getParameterList() {
        return this.parameterList;
    }
    
    public void setReturnParameterFlag() {
        this.returnParameterFlag = true;
    }
    
    public boolean getReturnParameterFlag() {
        return this.returnParameterFlag;
    }
    
    public DataTypeDescriptor[] getParameterTypes() {
        return this.parameterDescriptors;
    }
    
    public void setScanIsolationLevel(final int scanIsolationLevel) {
        this.scanIsolationLevel = scanIsolationLevel;
    }
    
    public int getScanIsolationLevel() {
        return this.scanIsolationLevel;
    }
    
    public TypeCompilerFactory getTypeCompilerFactory() {
        return this.typeCompilerFactory;
    }
    
    public void addWarning(final SQLWarning sqlWarning) {
        if (this.warnings == null) {
            this.warnings = sqlWarning;
        }
        else {
            this.warnings.setNextWarning(sqlWarning);
        }
    }
    
    public SQLWarning getWarnings() {
        return this.warnings;
    }
    
    public CompilerContextImpl(final ContextManager contextManager, final LanguageConnectionContext lcc, final TypeCompilerFactory typeCompilerFactory) {
        super(contextManager, "CompilerContext");
        this.reliability = 1024;
        this.nextColumnNumber = 1;
        this.nextEquivalenceClass = -1;
        this.storeCostControllers = new HashMap();
        this.privTypeStack = new ArrayList();
        this.currPrivType = -1;
        this.lcc = lcc;
        this.lcf = lcc.getLanguageConnectionFactory();
        this.parser = this.lcf.newParser(this);
        this.typeCompilerFactory = typeCompilerFactory;
        this.classPrefix = "ac" + this.lcf.getUUIDFactory().createUUID().toString().replace('-', 'x');
        this.initRequiredPriv();
    }
    
    private void initRequiredPriv() {
        this.currPrivType = -1;
        this.privTypeStack.clear();
        this.requiredColumnPrivileges = null;
        this.requiredTablePrivileges = null;
        this.requiredSchemaPrivileges = null;
        this.requiredRoutinePrivileges = null;
        this.requiredUsagePrivileges = null;
        this.requiredRolePrivileges = null;
        if (((LanguageConnectionContext)this.getContextManager().getContext("LanguageConnectionContext")).usesSqlAuthorization()) {
            this.requiredColumnPrivileges = new HashMap();
            this.requiredTablePrivileges = new HashMap();
            this.requiredSchemaPrivileges = new HashMap();
            this.requiredRoutinePrivileges = new HashMap();
            this.requiredUsagePrivileges = new HashMap();
            this.requiredRolePrivileges = new HashMap();
        }
    }
    
    public void pushCurrentPrivType(final int currPrivType) {
        this.privTypeStack.add(ReuseFactory.getInteger(this.currPrivType));
        this.currPrivType = currPrivType;
    }
    
    public void popCurrentPrivType() {
        this.currPrivType = this.privTypeStack.remove(this.privTypeStack.size() - 1);
    }
    
    public void addRequiredColumnPriv(final ColumnDescriptor columnDescriptor) {
        if (this.requiredColumnPrivileges == null || this.currPrivType == -1 || this.currPrivType == 4 || this.currPrivType == 3 || this.currPrivType == 5 || this.currPrivType == 6 || columnDescriptor == null) {
            return;
        }
        final TableDescriptor tableDescriptor = columnDescriptor.getTableDescriptor();
        if (tableDescriptor == null) {
            return;
        }
        if (tableDescriptor.getTableType() == 3) {
            return;
        }
        final UUID uuid = tableDescriptor.getUUID();
        if (this.currPrivType == 8) {
            final StatementTablePermission statementTablePermission = new StatementTablePermission(uuid, 0);
            if (this.requiredColumnPrivileges.containsKey(statementTablePermission) || this.requiredTablePrivileges.containsKey(statementTablePermission)) {
                return;
            }
        }
        if (this.currPrivType == 0) {
            this.requiredColumnPrivileges.remove(new StatementTablePermission(uuid, 8));
        }
        final StatementTablePermission statementTablePermission2 = new StatementTablePermission(uuid, this.currPrivType);
        StatementColumnPermission value = this.requiredColumnPrivileges.get(statementTablePermission2);
        if (value == null) {
            value = new StatementColumnPermission(uuid, this.currPrivType, new FormatableBitSet(tableDescriptor.getNumberOfColumns()));
            this.requiredColumnPrivileges.put(statementTablePermission2, value);
        }
        value.getColumns().set(columnDescriptor.getPosition() - 1);
    }
    
    public void addRequiredTablePriv(final TableDescriptor tableDescriptor) {
        if (this.requiredTablePrivileges == null || tableDescriptor == null) {
            return;
        }
        if (tableDescriptor.getTableType() == 3) {
            return;
        }
        if (this.currPrivType == 0) {
            this.requiredColumnPrivileges.remove(new StatementTablePermission(tableDescriptor.getUUID(), 8));
        }
        final StatementTablePermission statementTablePermission = new StatementTablePermission(tableDescriptor.getUUID(), this.currPrivType);
        this.requiredTablePrivileges.put(statementTablePermission, statementTablePermission);
    }
    
    public void addRequiredRoutinePriv(final AliasDescriptor aliasDescriptor) {
        if (this.requiredRoutinePrivileges == null || aliasDescriptor == null) {
            return;
        }
        if (aliasDescriptor.getSchemaUUID().toString().equals("c013800d-00fb-2642-07ec-000000134f30")) {
            return;
        }
        if (this.requiredRoutinePrivileges.get(aliasDescriptor.getUUID()) == null) {
            this.requiredRoutinePrivileges.put(aliasDescriptor.getUUID(), ReuseFactory.getInteger(1));
        }
    }
    
    public void addRequiredUsagePriv(final PrivilegedSQLObject privilegedSQLObject) {
        if (this.requiredUsagePrivileges == null || privilegedSQLObject == null) {
            return;
        }
        final UUID uuid = privilegedSQLObject.getUUID();
        final String objectTypeName = privilegedSQLObject.getObjectTypeName();
        if (this.requiredUsagePrivileges.get(uuid) == null) {
            this.requiredUsagePrivileges.put(uuid, objectTypeName);
        }
    }
    
    public void addRequiredSchemaPriv(final String s, final String s2, final int n) {
        if (this.requiredSchemaPrivileges == null || s == null) {
            return;
        }
        final StatementSchemaPermission statementSchemaPermission = new StatementSchemaPermission(s, s2, n);
        this.requiredSchemaPrivileges.put(statementSchemaPermission, statementSchemaPermission);
    }
    
    public void addRequiredRolePriv(final String s, final int n) {
        if (this.requiredRolePrivileges == null) {
            return;
        }
        final StatementRolePermission statementRolePermission = new StatementRolePermission(s, n);
        this.requiredRolePrivileges.put(statementRolePermission, statementRolePermission);
    }
    
    public List getRequiredPermissionsList() {
        int initialCapacity = 0;
        if (this.requiredRoutinePrivileges != null) {
            initialCapacity += this.requiredRoutinePrivileges.size();
        }
        if (this.requiredUsagePrivileges != null) {
            initialCapacity += this.requiredUsagePrivileges.size();
        }
        if (this.requiredTablePrivileges != null) {
            initialCapacity += this.requiredTablePrivileges.size();
        }
        if (this.requiredSchemaPrivileges != null) {
            initialCapacity += this.requiredSchemaPrivileges.size();
        }
        if (this.requiredColumnPrivileges != null) {
            initialCapacity += this.requiredColumnPrivileges.size();
        }
        if (this.requiredRolePrivileges != null) {
            initialCapacity += this.requiredRolePrivileges.size();
        }
        final ArrayList list = new ArrayList<StatementRoutinePermission>(initialCapacity);
        if (this.requiredRoutinePrivileges != null) {
            final Iterator<UUID> iterator = this.requiredRoutinePrivileges.keySet().iterator();
            while (iterator.hasNext()) {
                list.add(new StatementRoutinePermission(iterator.next()));
            }
        }
        if (this.requiredUsagePrivileges != null) {
            for (final UUID key : this.requiredUsagePrivileges.keySet()) {
                list.add((StatementRoutinePermission)new StatementGenericPermission(key, (String)this.requiredUsagePrivileges.get(key), "USAGE"));
            }
        }
        if (this.requiredTablePrivileges != null) {
            final Iterator<StatementRoutinePermission> iterator3 = this.requiredTablePrivileges.values().iterator();
            while (iterator3.hasNext()) {
                list.add(iterator3.next());
            }
        }
        if (this.requiredSchemaPrivileges != null) {
            final Iterator<StatementRoutinePermission> iterator4 = this.requiredSchemaPrivileges.values().iterator();
            while (iterator4.hasNext()) {
                list.add(iterator4.next());
            }
        }
        if (this.requiredColumnPrivileges != null) {
            final Iterator<StatementRoutinePermission> iterator5 = this.requiredColumnPrivileges.values().iterator();
            while (iterator5.hasNext()) {
                list.add(iterator5.next());
            }
        }
        if (this.requiredRolePrivileges != null) {
            final Iterator<StatementRoutinePermission> iterator6 = this.requiredRolePrivileges.values().iterator();
            while (iterator6.hasNext()) {
                list.add(iterator6.next());
            }
        }
        return list;
    }
    
    public void addReferencedSequence(final SequenceDescriptor value) {
        if (this.referencedSequences == null) {
            this.referencedSequences = new HashMap();
        }
        this.referencedSequences.put(value.getUUID(), value);
    }
    
    public boolean isReferenced(final SequenceDescriptor sequenceDescriptor) {
        return this.referencedSequences != null && this.referencedSequences.containsKey(sequenceDescriptor.getUUID());
    }
}
