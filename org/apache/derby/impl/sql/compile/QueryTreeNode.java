// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.impl.sql.compile;

import org.apache.derby.iapi.sql.dictionary.PrivilegedSQLObject;
import org.apache.derby.iapi.store.access.TransactionController;
import org.apache.derby.catalog.types.RowMultiSetImpl;
import org.apache.derby.catalog.TypeDescriptor;
import org.apache.derby.catalog.types.UserDefinedTypeIdImpl;
import org.apache.derby.iapi.services.i18n.MessageService;
import org.apache.derby.iapi.services.loader.ClassInspector;
import org.apache.derby.iapi.sql.dictionary.AliasDescriptor;
import org.apache.derby.catalog.types.SynonymAliasInfo;
import org.apache.derby.iapi.sql.depend.Provider;
import org.apache.derby.catalog.UUID;
import org.apache.derby.iapi.sql.dictionary.TableDescriptor;
import org.apache.derby.iapi.types.DataValueDescriptor;
import org.apache.derby.iapi.sql.compile.Visitor;
import org.apache.derby.iapi.sql.compile.TypeCompiler;
import org.apache.derby.iapi.types.TypeId;
import org.apache.derby.iapi.sql.compile.CompilerContext;
import org.apache.derby.iapi.sql.depend.DependencyManager;
import org.apache.derby.iapi.sql.dictionary.DataDictionary;
import org.apache.derby.iapi.sql.execute.ConstantAction;
import org.apache.derby.iapi.types.DataTypeDescriptor;
import org.apache.derby.iapi.services.compiler.MethodBuilder;
import org.apache.derby.iapi.sql.dictionary.SchemaDescriptor;
import org.apache.derby.iapi.error.StandardException;
import org.apache.derby.iapi.services.loader.ClassFactory;
import org.apache.derby.iapi.sql.execute.ExecutionFactory;
import org.apache.derby.impl.sql.execute.GenericExecutionFactory;
import org.apache.derby.iapi.sql.compile.NodeFactory;
import org.apache.derby.impl.sql.execute.GenericConstantActionFactory;
import org.apache.derby.iapi.sql.conn.LanguageConnectionContext;
import org.apache.derby.iapi.services.context.ContextManager;
import org.apache.derby.iapi.sql.compile.Visitable;
import org.apache.derby.iapi.sql.compile.Node;

public abstract class QueryTreeNode implements Node, Visitable
{
    public static final int AUTOINCREMENT_START_INDEX = 0;
    public static final int AUTOINCREMENT_INC_INDEX = 1;
    public static final int AUTOINCREMENT_IS_AUTOINCREMENT_INDEX = 2;
    public static final int AUTOINCREMENT_CREATE_MODIFY = 3;
    private int beginOffset;
    private int endOffset;
    private int nodeType;
    private ContextManager cm;
    private LanguageConnectionContext lcc;
    private GenericConstantActionFactory constantActionFactory;
    private boolean isPrivilegeCollectionRequired;
    
    public QueryTreeNode() {
        this.beginOffset = -1;
        this.endOffset = -1;
        this.isPrivilegeCollectionRequired = true;
    }
    
    public void setContextManager(final ContextManager cm) {
        this.cm = cm;
    }
    
    public final ContextManager getContextManager() {
        return this.cm;
    }
    
    public final NodeFactory getNodeFactory() {
        return this.getLanguageConnectionContext().getLanguageConnectionFactory().getNodeFactory();
    }
    
    public final GenericConstantActionFactory getGenericConstantActionFactory() {
        if (this.constantActionFactory == null) {
            this.constantActionFactory = ((GenericExecutionFactory)this.getExecutionFactory()).getConstantActionFactory();
        }
        return this.constantActionFactory;
    }
    
    public final ExecutionFactory getExecutionFactory() {
        return this.getLanguageConnectionContext().getLanguageConnectionFactory().getExecutionFactory();
    }
    
    protected final ClassFactory getClassFactory() {
        return this.getLanguageConnectionContext().getLanguageConnectionFactory().getClassFactory();
    }
    
    protected final LanguageConnectionContext getLanguageConnectionContext() {
        if (this.lcc == null) {
            this.lcc = (LanguageConnectionContext)this.getContextManager().getContext("LanguageConnectionContext");
        }
        return this.lcc;
    }
    
    public int getBeginOffset() {
        return this.beginOffset;
    }
    
    public void setBeginOffset(final int beginOffset) {
        this.beginOffset = beginOffset;
    }
    
    public int getEndOffset() {
        return this.endOffset;
    }
    
    public void setEndOffset(final int endOffset) {
        this.endOffset = endOffset;
    }
    
    protected String nodeHeader() {
        return "";
    }
    
    public static String formatNodeString(final String s, final int n) {
        return "";
    }
    
    public void treePrint() {
    }
    
    public void stackPrint() {
    }
    
    public void treePrint(final int n) {
    }
    
    private static boolean containsInfo(final String s) {
        for (int i = 0; i < s.length(); ++i) {
            if (s.charAt(i) != '\t' && s.charAt(i) != '\n') {
                return true;
            }
        }
        return false;
    }
    
    public static void debugPrint(final String s) {
    }
    
    protected static void debugFlush() {
    }
    
    public void printSubNodes(final int n) {
    }
    
    public String toString() {
        return "";
    }
    
    public void printLabel(final int n, final String s) {
    }
    
    public boolean referencesSessionSchema() throws StandardException {
        return false;
    }
    
    final boolean isSessionSchema(final SchemaDescriptor schemaDescriptor) {
        return this.isSessionSchema(schemaDescriptor.getSchemaName());
    }
    
    final boolean isSessionSchema(final String anObject) {
        return "SESSION".equals(anObject);
    }
    
    final void disablePrivilegeCollection() {
        this.isPrivilegeCollectionRequired = false;
    }
    
    public boolean isPrivilegeCollectionRequired() {
        return this.isPrivilegeCollectionRequired;
    }
    
    protected void generate(final ActivationClassBuilder activationClassBuilder, final MethodBuilder methodBuilder) throws StandardException {
        throw StandardException.newException("42Z50", this.nodeHeader());
    }
    
    public DataTypeDescriptor[] getParameterTypes() throws StandardException {
        return this.getCompilerContext().getParameterTypes();
    }
    
    public ConstantAction makeConstantAction() throws StandardException {
        return null;
    }
    
    public void setNodeType(final int nodeType) {
        this.nodeType = nodeType;
    }
    
    protected int getNodeType() {
        return this.nodeType;
    }
    
    protected boolean isInstanceOf(final int n) {
        return this.nodeType == n;
    }
    
    public final DataDictionary getDataDictionary() {
        return this.getLanguageConnectionContext().getDataDictionary();
    }
    
    public final DependencyManager getDependencyManager() {
        return this.getDataDictionary().getDependencyManager();
    }
    
    protected final CompilerContext getCompilerContext() {
        return (CompilerContext)this.getContextManager().getContext("CompilerContext");
    }
    
    protected final TypeCompiler getTypeCompiler(final TypeId typeId) {
        return this.getCompilerContext().getTypeCompilerFactory().getTypeCompiler(typeId);
    }
    
    public final Visitable accept(final Visitor visitor) throws StandardException {
        final boolean visitChildrenFirst = visitor.visitChildrenFirst(this);
        final boolean skipChildren = visitor.skipChildren(this);
        if (visitChildrenFirst && !skipChildren && !visitor.stopTraversal()) {
            this.acceptChildren(visitor);
        }
        final Visitable visitable = visitor.stopTraversal() ? this : visitor.visit(this);
        if (!visitChildrenFirst && !skipChildren && !visitor.stopTraversal()) {
            this.acceptChildren(visitor);
        }
        return visitable;
    }
    
    void acceptChildren(final Visitor visitor) throws StandardException {
    }
    
    protected int getIntProperty(final String s, final String s2) throws StandardException {
        int int1;
        try {
            int1 = Integer.parseInt(s);
        }
        catch (NumberFormatException ex) {
            throw StandardException.newException("42Y58", s, s2);
        }
        return int1;
    }
    
    StatementNode parseStatement(final String s, final boolean b) throws StandardException {
        final LanguageConnectionContext languageConnectionContext = this.getLanguageConnectionContext();
        final CompilerContext pushCompilerContext = languageConnectionContext.pushCompilerContext();
        if (b) {
            pushCompilerContext.setReliability(0);
        }
        try {
            return (StatementNode)pushCompilerContext.getParser().parseStatement(s);
        }
        finally {
            languageConnectionContext.popCompilerContext(pushCompilerContext);
        }
    }
    
    protected int getStatementType() {
        return 0;
    }
    
    public boolean foundString(final String[] array, final String anObject) {
        if (array == null) {
            return false;
        }
        for (int i = 0; i < array.length; ++i) {
            if (array[i].equals(anObject)) {
                return true;
            }
        }
        return false;
    }
    
    public ConstantNode getNullNode(final DataTypeDescriptor dataTypeDescriptor) throws StandardException {
        int n = 0;
        switch (dataTypeDescriptor.getTypeId().getJDBCTypeId()) {
            case 12: {
                n = 77;
                break;
            }
            case 1: {
                n = 61;
                break;
            }
            case -6: {
                n = 75;
                break;
            }
            case 5: {
                n = 74;
                break;
            }
            case 4: {
                n = 70;
                break;
            }
            case -5: {
                n = 71;
                break;
            }
            case 7: {
                n = 69;
                break;
            }
            case 8: {
                n = 68;
                break;
            }
            case 2:
            case 3: {
                n = 67;
                break;
            }
            case 91:
            case 92:
            case 93: {
                n = 76;
                break;
            }
            case -2: {
                n = 58;
                break;
            }
            case -3: {
                n = 59;
                break;
            }
            case -1: {
                n = 73;
                break;
            }
            case 2005: {
                n = 196;
                break;
            }
            case -4: {
                n = 72;
                break;
            }
            case 2004: {
                n = 195;
                break;
            }
            case 2009: {
                n = 199;
                break;
            }
            case 16: {
                n = 38;
                break;
            }
            default: {
                if (dataTypeDescriptor.getTypeId().userType()) {
                    n = 76;
                    break;
                }
                return null;
            }
        }
        final ConstantNode constantNode = (ConstantNode)this.getNodeFactory().getNode(n, dataTypeDescriptor.getTypeId(), this.cm);
        constantNode.setType(dataTypeDescriptor.getNullabilityType(true));
        return constantNode;
    }
    
    public DataValueDescriptor convertDefaultNode(final DataTypeDescriptor dataTypeDescriptor) throws StandardException {
        return null;
    }
    
    public void init(final Object o) throws StandardException {
    }
    
    public void init(final Object o, final Object o2) throws StandardException {
    }
    
    public void init(final Object o, final Object o2, final Object o3) throws StandardException {
    }
    
    public void init(final Object o, final Object o2, final Object o3, final Object o4) throws StandardException {
    }
    
    public void init(final Object o, final Object o2, final Object o3, final Object o4, final Object o5) throws StandardException {
    }
    
    public void init(final Object o, final Object o2, final Object o3, final Object o4, final Object o5, final Object o6) throws StandardException {
    }
    
    public void init(final Object o, final Object o2, final Object o3, final Object o4, final Object o5, final Object o6, final Object o7) throws StandardException {
    }
    
    public void init(final Object o, final Object o2, final Object o3, final Object o4, final Object o5, final Object o6, final Object o7, final Object o8) throws StandardException {
    }
    
    public void init(final Object o, final Object o2, final Object o3, final Object o4, final Object o5, final Object o6, final Object o7, final Object o8, final Object o9) throws StandardException {
    }
    
    public void init(final Object o, final Object o2, final Object o3, final Object o4, final Object o5, final Object o6, final Object o7, final Object o8, final Object o9, final Object o10) throws StandardException {
    }
    
    public void init(final Object o, final Object o2, final Object o3, final Object o4, final Object o5, final Object o6, final Object o7, final Object o8, final Object o9, final Object o10, final Object o11) throws StandardException {
    }
    
    public void init(final Object o, final Object o2, final Object o3, final Object o4, final Object o5, final Object o6, final Object o7, final Object o8, final Object o9, final Object o10, final Object o11, final Object o12) throws StandardException {
    }
    
    public void init(final Object o, final Object o2, final Object o3, final Object o4, final Object o5, final Object o6, final Object o7, final Object o8, final Object o9, final Object o10, final Object o11, final Object o12, final Object o13) throws StandardException {
    }
    
    public void init(final Object o, final Object o2, final Object o3, final Object o4, final Object o5, final Object o6, final Object o7, final Object o8, final Object o9, final Object o10, final Object o11, final Object o12, final Object o13, final Object o14) throws StandardException {
    }
    
    public TableName makeTableName(final String s, final String s2) throws StandardException {
        return makeTableName(this.getNodeFactory(), this.getContextManager(), s, s2);
    }
    
    public static TableName makeTableName(final NodeFactory nodeFactory, final ContextManager contextManager, final String s, final String s2) throws StandardException {
        return (TableName)nodeFactory.getNode(34, s, s2, contextManager);
    }
    
    public boolean isAtomic() throws StandardException {
        return false;
    }
    
    public Object getCursorInfo() throws StandardException {
        return null;
    }
    
    protected final TableDescriptor getTableDescriptor(final String s, final SchemaDescriptor schemaDescriptor) throws StandardException {
        if (this.isSessionSchema(schemaDescriptor)) {
            final TableDescriptor tableDescriptorForDeclaredGlobalTempTable = this.getLanguageConnectionContext().getTableDescriptorForDeclaredGlobalTempTable(s);
            if (tableDescriptorForDeclaredGlobalTempTable != null) {
                return tableDescriptorForDeclaredGlobalTempTable;
            }
        }
        if (schemaDescriptor.getUUID() == null) {
            return null;
        }
        final TableDescriptor tableDescriptor = this.getDataDictionary().getTableDescriptor(s, schemaDescriptor, this.getLanguageConnectionContext().getTransactionCompile());
        if (tableDescriptor == null || tableDescriptor.isSynonymDescriptor()) {
            return null;
        }
        return tableDescriptor;
    }
    
    final SchemaDescriptor getSchemaDescriptor(final String s) throws StandardException {
        return this.getSchemaDescriptor(s, true);
    }
    
    final SchemaDescriptor getSchemaDescriptor(String schemaName, final boolean b) throws StandardException {
        SchemaDescriptor compilationSchema = null;
        boolean b2 = false;
        boolean b3 = false;
        if (schemaName == null) {
            final CompilerContext compilerContext = this.getCompilerContext();
            compilationSchema = compilerContext.getCompilationSchema();
            if (compilationSchema == null) {
                compilationSchema = this.getLanguageConnectionContext().getDefaultSchema();
                b2 = true;
                compilerContext.setCompilationSchema(compilationSchema);
            }
            else {
                b3 = true;
            }
            schemaName = compilationSchema.getSchemaName();
        }
        SchemaDescriptor schemaDescriptor = this.getDataDictionary().getSchemaDescriptor(schemaName, this.getLanguageConnectionContext().getTransactionCompile(), b);
        if (b2 || b3) {
            if (schemaDescriptor != null && schemaDescriptor.getUUID() != null) {
                if (!schemaDescriptor.getUUID().equals(compilationSchema.getUUID())) {
                    if (b2) {
                        this.getLanguageConnectionContext().setDefaultSchema(schemaDescriptor);
                    }
                    this.getCompilerContext().setCompilationSchema(schemaDescriptor);
                }
            }
            else {
                compilationSchema.setUUID(null);
                schemaDescriptor = compilationSchema;
            }
        }
        return schemaDescriptor;
    }
    
    public TableName resolveTableToSynonym(final TableName tableName) throws StandardException {
        final DataDictionary dataDictionary = this.getDataDictionary();
        String s = tableName.getTableName();
        String s2 = tableName.getSchemaName();
        boolean b = false;
        final CompilerContext compilerContext = this.getCompilerContext();
        while (true) {
            final SchemaDescriptor schemaDescriptor = this.getSchemaDescriptor(s2, false);
            if (schemaDescriptor == null) {
                break;
            }
            if (schemaDescriptor.getUUID() == null) {
                break;
            }
            final AliasDescriptor aliasDescriptor = dataDictionary.getAliasDescriptor(schemaDescriptor.getUUID().toString(), s, 'S');
            if (aliasDescriptor == null) {
                break;
            }
            compilerContext.createDependency(aliasDescriptor);
            b = true;
            final SynonymAliasInfo synonymAliasInfo = (SynonymAliasInfo)aliasDescriptor.getAliasInfo();
            s = synonymAliasInfo.getSynonymTable();
            s2 = synonymAliasInfo.getSynonymSchema();
        }
        if (!b) {
            return null;
        }
        final TableName tableName2 = new TableName();
        tableName2.init(s2, s);
        return tableName2;
    }
    
    void verifyClassExist(final String s) throws StandardException {
        final ClassInspector classInspector = this.getClassFactory().getClassInspector();
        Throwable t = null;
        boolean accessible = false;
        try {
            accessible = classInspector.accessible(s);
        }
        catch (ClassNotFoundException ex) {
            t = ex;
        }
        if (!accessible) {
            throw StandardException.newException("42X51", t, s);
        }
        if (ClassInspector.primitiveType(s)) {
            throw StandardException.newException("42Y37", s);
        }
    }
    
    public void setRefActionInfo(final long n, final int[] array, final String s, final boolean b) {
    }
    
    void generateAuthorizeCheck(final ActivationClassBuilder activationClassBuilder, final MethodBuilder methodBuilder, final int n) {
        activationClassBuilder.pushThisAsActivation(methodBuilder);
        methodBuilder.callMethod((short)185, null, "getLanguageConnectionContext", "org.apache.derby.iapi.sql.conn.LanguageConnectionContext", 0);
        methodBuilder.callMethod((short)185, null, "getAuthorizer", "org.apache.derby.iapi.sql.conn.Authorizer", 0);
        activationClassBuilder.pushThisAsActivation(methodBuilder);
        methodBuilder.push(n);
        methodBuilder.callMethod((short)185, null, "authorize", "void", 2);
    }
    
    public void checkReliability(final String s, final int n) throws StandardException {
        if ((this.getCompilerContext().getReliability() & n) != 0x0) {
            this.throwReliabilityException(s, n);
        }
    }
    
    public void checkReliability(final int n, final String s) throws StandardException {
        if ((this.getCompilerContext().getReliability() & n) != 0x0) {
            this.throwReliabilityException(MessageService.getTextMessage(s), n);
        }
    }
    
    public DataTypeDescriptor bindUserType(final DataTypeDescriptor dataTypeDescriptor) throws StandardException {
        if (dataTypeDescriptor.getCatalogType().isRowMultiSet()) {
            return this.bindRowMultiSet(dataTypeDescriptor);
        }
        if (!dataTypeDescriptor.getTypeId().userType()) {
            return dataTypeDescriptor;
        }
        final UserDefinedTypeIdImpl userDefinedTypeIdImpl = (UserDefinedTypeIdImpl)dataTypeDescriptor.getTypeId().getBaseTypeId();
        if (userDefinedTypeIdImpl.isBound()) {
            return dataTypeDescriptor;
        }
        final DataDictionary dataDictionary = this.getDataDictionary();
        final SchemaDescriptor schemaDescriptor = this.getSchemaDescriptor(userDefinedTypeIdImpl.getSchemaName());
        final char c = 'A';
        final String unqualifiedName = userDefinedTypeIdImpl.getUnqualifiedName();
        final AliasDescriptor aliasDescriptor = dataDictionary.getAliasDescriptor(schemaDescriptor.getUUID().toString(), unqualifiedName, c);
        if (aliasDescriptor == null) {
            throw StandardException.newException("42X94", AliasDescriptor.getAliasType(c), unqualifiedName);
        }
        this.createTypeDependency(aliasDescriptor);
        return new DataTypeDescriptor(TypeId.getUserDefinedTypeId(schemaDescriptor.getSchemaName(), unqualifiedName, aliasDescriptor.getJavaClassName()), dataTypeDescriptor.isNullable());
    }
    
    public TypeDescriptor bindUserCatalogType(final TypeDescriptor typeDescriptor) throws StandardException {
        if (!typeDescriptor.isUserDefinedType()) {
            return typeDescriptor;
        }
        return this.bindUserType(DataTypeDescriptor.getType(typeDescriptor)).getCatalogType();
    }
    
    public DataTypeDescriptor bindRowMultiSet(final DataTypeDescriptor dataTypeDescriptor) throws StandardException {
        if (!dataTypeDescriptor.getCatalogType().isRowMultiSet()) {
            return dataTypeDescriptor;
        }
        final RowMultiSetImpl rowMultiSetImpl = (RowMultiSetImpl)dataTypeDescriptor.getTypeId().getBaseTypeId();
        rowMultiSetImpl.getColumnNames();
        final TypeDescriptor[] types = rowMultiSetImpl.getTypes();
        for (int length = types.length, i = 0; i < length; ++i) {
            types[i] = this.bindUserCatalogType(types[i]);
        }
        return dataTypeDescriptor;
    }
    
    public void createTypeDependency(final DataTypeDescriptor dataTypeDescriptor) throws StandardException {
        final AliasDescriptor aliasDescriptorForUDT = this.getDataDictionary().getAliasDescriptorForUDT(null, dataTypeDescriptor);
        if (aliasDescriptorForUDT != null) {
            this.createTypeDependency(aliasDescriptorForUDT);
        }
    }
    
    private void createTypeDependency(final AliasDescriptor aliasDescriptor) throws StandardException {
        this.getCompilerContext().createDependency(aliasDescriptor);
        if (this.isPrivilegeCollectionRequired()) {
            this.getCompilerContext().addRequiredUsagePriv(aliasDescriptor);
        }
    }
    
    private void throwReliabilityException(final String s, final int n) throws StandardException {
        String s2 = null;
        if (this.getCompilerContext().getReliability() == 1192) {
            s2 = "42Y84";
        }
        else if (this.getCompilerContext().getReliability() == 30329) {
            switch (n) {
                case 8192: {
                    s2 = "42XA5";
                    break;
                }
                default: {
                    s2 = "42XA2";
                    break;
                }
            }
        }
        else {
            s2 = "42Y39";
        }
        throw StandardException.newException(s2, s);
    }
    
    public int orReliability(final int n) {
        final CompilerContext compilerContext = this.getCompilerContext();
        final int reliability = compilerContext.getReliability();
        compilerContext.setReliability(reliability | n);
        return reliability;
    }
    
    public static void bindOffsetFetch(final ValueNode valueNode, final ValueNode valueNode2) throws StandardException {
        if (valueNode instanceof ConstantNode) {
            final long long1 = ((ConstantNode)valueNode).getValue().getLong();
            if (long1 < 0L) {
                throw StandardException.newException("2201X", Long.toString(long1));
            }
        }
        else if (valueNode instanceof ParameterNode) {
            valueNode.setType(new DataTypeDescriptor(TypeId.getBuiltInTypeId(-5), false));
        }
        if (valueNode2 instanceof ConstantNode) {
            final long long2 = ((ConstantNode)valueNode2).getValue().getLong();
            if (long2 < 1L) {
                throw StandardException.newException("2201W", Long.toString(long2));
            }
        }
        else if (valueNode2 instanceof ParameterNode) {
            valueNode2.setType(new DataTypeDescriptor(TypeId.getBuiltInTypeId(-5), false));
        }
    }
}
