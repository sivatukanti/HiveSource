// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.iapi.sql.compile;

import org.apache.derby.iapi.sql.dictionary.SequenceDescriptor;
import org.apache.derby.iapi.sql.dictionary.PrivilegedSQLObject;
import org.apache.derby.iapi.sql.dictionary.AliasDescriptor;
import org.apache.derby.iapi.sql.dictionary.TableDescriptor;
import org.apache.derby.iapi.sql.dictionary.ColumnDescriptor;
import java.sql.SQLWarning;
import org.apache.derby.iapi.types.DataTypeDescriptor;
import java.util.List;
import org.apache.derby.iapi.store.access.SortCostController;
import org.apache.derby.iapi.store.access.StoreCostController;
import org.apache.derby.iapi.sql.dictionary.SchemaDescriptor;
import org.apache.derby.iapi.error.StandardException;
import org.apache.derby.iapi.sql.depend.Provider;
import org.apache.derby.iapi.sql.depend.ProviderList;
import org.apache.derby.iapi.sql.depend.Dependent;
import org.apache.derby.iapi.services.compiler.JavaFactory;
import org.apache.derby.iapi.services.loader.ClassFactory;
import org.apache.derby.iapi.services.context.Context;

public interface CompilerContext extends Context
{
    public static final String CONTEXT_ID = "CompilerContext";
    public static final int DATETIME_ILLEGAL = 1;
    public static final int CURRENT_CONNECTION_ILLEGAL = 2;
    public static final int FUNCTION_CALL_ILLEGAL = 4;
    public static final int UNNAMED_PARAMETER_ILLEGAL = 8;
    public static final int DIAGNOSTICS_ILLEGAL = 16;
    public static final int SUBQUERY_ILLEGAL = 32;
    public static final int USER_ILLEGAL = 64;
    public static final int COLUMN_REFERENCE_ILLEGAL = 128;
    public static final int IGNORE_MISSING_CLASSES = 256;
    public static final int SCHEMA_ILLEGAL = 512;
    public static final int INTERNAL_SQL_ILLEGAL = 1024;
    public static final int MODIFIES_SQL_DATA_PROCEDURE_ILLEGAL = 2048;
    public static final int NON_DETERMINISTIC_ILLEGAL = 4096;
    public static final int SQL_IN_ROUTINES_ILLEGAL = 8192;
    public static final int NEXT_VALUE_FOR_ILLEGAL = 16384;
    public static final int SQL_LEGAL = 1024;
    public static final int INTERNAL_SQL_LEGAL = 0;
    public static final int CHECK_CONSTRAINT = 18041;
    public static final int DEFAULT_RESTRICTION = 1192;
    public static final int GENERATION_CLAUSE_RESTRICTION = 30329;
    public static final int WHERE_CLAUSE_RESTRICTION = 16384;
    public static final int HAVING_CLAUSE_RESTRICTION = 16384;
    public static final int ON_CLAUSE_RESTRICTION = 16384;
    public static final int AGGREGATE_RESTRICTION = 16384;
    public static final int CONDITIONAL_RESTRICTION = 16384;
    public static final int GROUP_BY_RESTRICTION = 16384;
    
    Parser getParser();
    
    NodeFactory getNodeFactory();
    
    TypeCompilerFactory getTypeCompilerFactory();
    
    ClassFactory getClassFactory();
    
    JavaFactory getJavaFactory();
    
    int getNextColumnNumber();
    
    void resetContext();
    
    int getNextTableNumber();
    
    int getNumTables();
    
    int getNextSubqueryNumber();
    
    int getNumSubquerys();
    
    int getNextResultSetNumber();
    
    void resetNextResultSetNumber();
    
    int getNumResultSets();
    
    String getUniqueClassName();
    
    void setCurrentDependent(final Dependent p0);
    
    ProviderList getCurrentAuxiliaryProviderList();
    
    void setCurrentAuxiliaryProviderList(final ProviderList p0);
    
    void createDependency(final Provider p0) throws StandardException;
    
    void createDependency(final Dependent p0, final Provider p1) throws StandardException;
    
    int addSavedObject(final Object p0);
    
    Object[] getSavedObjects();
    
    void setSavedObjects(final Object[] p0);
    
    void setInUse(final boolean p0);
    
    boolean getInUse();
    
    void firstOnStack();
    
    boolean isFirstOnStack();
    
    void setReliability(final int p0);
    
    int getReliability();
    
    SchemaDescriptor getCompilationSchema();
    
    SchemaDescriptor setCompilationSchema(final SchemaDescriptor p0);
    
    void pushCompilationSchema(final SchemaDescriptor p0);
    
    void popCompilationSchema();
    
    StoreCostController getStoreCostController(final long p0) throws StandardException;
    
    SortCostController getSortCostController() throws StandardException;
    
    void setParameterList(final List p0);
    
    List getParameterList();
    
    void setReturnParameterFlag();
    
    boolean getReturnParameterFlag();
    
    DataTypeDescriptor[] getParameterTypes();
    
    Object getCursorInfo();
    
    void setCursorInfo(final Object p0);
    
    void setScanIsolationLevel(final int p0);
    
    int getScanIsolationLevel();
    
    int getNextEquivalenceClass();
    
    void addWarning(final SQLWarning p0);
    
    SQLWarning getWarnings();
    
    void pushCurrentPrivType(final int p0);
    
    void popCurrentPrivType();
    
    void addRequiredColumnPriv(final ColumnDescriptor p0);
    
    void addRequiredTablePriv(final TableDescriptor p0);
    
    void addRequiredSchemaPriv(final String p0, final String p1, final int p2);
    
    void addRequiredRoutinePriv(final AliasDescriptor p0);
    
    void addRequiredUsagePriv(final PrivilegedSQLObject p0);
    
    void addRequiredRolePriv(final String p0, final int p1);
    
    List getRequiredPermissionsList();
    
    void addReferencedSequence(final SequenceDescriptor p0);
    
    boolean isReferenced(final SequenceDescriptor p0);
}
