// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.store;

import org.datanucleus.metadata.AbstractClassMetaData;
import org.datanucleus.ClassLoaderResolver;
import org.datanucleus.metadata.AbstractMemberMetaData;
import java.io.PrintStream;
import java.util.Date;
import org.datanucleus.NucleusContext;
import org.datanucleus.api.ApiAdapter;
import org.datanucleus.store.valuegenerator.ValueGenerationManager;
import java.util.Map;
import org.datanucleus.store.connection.ManagedConnection;
import org.datanucleus.store.connection.ConnectionManager;
import org.datanucleus.metadata.SequenceMetaData;
import org.datanucleus.ExecutionContext;
import org.datanucleus.store.schema.StoreSchemaHandler;
import org.datanucleus.store.query.QueryManager;
import org.datanucleus.store.schema.naming.NamingFactory;
import org.datanucleus.flush.FlushProcess;
import java.util.Collection;

public interface StoreManager
{
    Collection getSupportedOptions();
    
    void close();
    
    StorePersistenceHandler getPersistenceHandler();
    
    FlushProcess getFlushProcess();
    
    NamingFactory getNamingFactory();
    
    QueryManager getQueryManager();
    
    StoreSchemaHandler getSchemaHandler();
    
    NucleusSequence getNucleusSequence(final ExecutionContext p0, final SequenceMetaData p1);
    
    NucleusConnection getNucleusConnection(final ExecutionContext p0);
    
    ConnectionManager getConnectionManager();
    
    ManagedConnection getConnection(final ExecutionContext p0);
    
    ManagedConnection getConnection(final ExecutionContext p0, final Map p1);
    
    String getConnectionDriverName();
    
    String getConnectionURL();
    
    String getConnectionUserName();
    
    String getConnectionPassword();
    
    Object getConnectionFactory();
    
    String getConnectionFactoryName();
    
    Object getConnectionFactory2();
    
    String getConnectionFactory2Name();
    
    ValueGenerationManager getValueGenerationManager();
    
    ApiAdapter getApiAdapter();
    
    String getStoreManagerKey();
    
    String getQueryCacheKey();
    
    NucleusContext getNucleusContext();
    
    Date getDatastoreDate();
    
    boolean isJdbcStore();
    
    void printInformation(final String p0, final PrintStream p1) throws Exception;
    
    boolean useBackedSCOWrapperForMember(final AbstractMemberMetaData p0, final ExecutionContext p1);
    
    boolean managesClass(final String p0);
    
    void addClass(final String p0, final ClassLoaderResolver p1);
    
    void addClasses(final String[] p0, final ClassLoaderResolver p1);
    
    void removeAllClasses(final ClassLoaderResolver p0);
    
    String manageClassForIdentity(final Object p0, final ClassLoaderResolver p1);
    
    Extent getExtent(final ExecutionContext p0, final Class p1, final boolean p2);
    
    boolean supportsQueryLanguage(final String p0);
    
    boolean supportsValueStrategy(final String p0);
    
    String getClassNameForObjectID(final Object p0, final ClassLoaderResolver p1, final ExecutionContext p2);
    
    boolean isStrategyDatastoreAttributed(final AbstractClassMetaData p0, final int p1);
    
    Object getStrategyValue(final ExecutionContext p0, final AbstractClassMetaData p1, final int p2);
    
    Collection<String> getSubClassesForClass(final String p0, final boolean p1, final ClassLoaderResolver p2);
    
    Object getProperty(final String p0);
    
    boolean hasProperty(final String p0);
    
    int getIntProperty(final String p0);
    
    boolean getBooleanProperty(final String p0);
    
    boolean getBooleanProperty(final String p0, final boolean p1);
    
    Boolean getBooleanObjectProperty(final String p0);
    
    String getStringProperty(final String p0);
    
    void transactionStarted(final ExecutionContext p0);
    
    void transactionCommitted(final ExecutionContext p0);
    
    void transactionRolledBack(final ExecutionContext p0);
    
    boolean isAutoCreateTables();
    
    boolean isAutoCreateConstraints();
    
    boolean isAutoCreateColumns();
    
    String getDefaultObjectProviderClassName();
}
