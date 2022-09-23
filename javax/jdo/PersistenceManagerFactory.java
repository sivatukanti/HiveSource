// 
// Decompiled by Procyon v0.5.36
// 

package javax.jdo;

import javax.jdo.metadata.TypeMetadata;
import javax.jdo.metadata.JDOMetadata;
import java.util.Set;
import javax.jdo.listener.InstanceLifecycleListener;
import javax.jdo.datastore.DataStoreCache;
import java.util.Collection;
import java.util.Properties;
import java.io.Serializable;

public interface PersistenceManagerFactory extends Serializable
{
    void close();
    
    boolean isClosed();
    
    PersistenceManager getPersistenceManager();
    
    PersistenceManager getPersistenceManagerProxy();
    
    PersistenceManager getPersistenceManager(final String p0, final String p1);
    
    void setConnectionUserName(final String p0);
    
    String getConnectionUserName();
    
    void setConnectionPassword(final String p0);
    
    void setConnectionURL(final String p0);
    
    String getConnectionURL();
    
    void setConnectionDriverName(final String p0);
    
    String getConnectionDriverName();
    
    void setConnectionFactoryName(final String p0);
    
    String getConnectionFactoryName();
    
    void setConnectionFactory(final Object p0);
    
    Object getConnectionFactory();
    
    void setConnectionFactory2Name(final String p0);
    
    String getConnectionFactory2Name();
    
    void setConnectionFactory2(final Object p0);
    
    Object getConnectionFactory2();
    
    void setMultithreaded(final boolean p0);
    
    boolean getMultithreaded();
    
    void setMapping(final String p0);
    
    String getMapping();
    
    void setOptimistic(final boolean p0);
    
    boolean getOptimistic();
    
    void setRetainValues(final boolean p0);
    
    boolean getRetainValues();
    
    void setRestoreValues(final boolean p0);
    
    boolean getRestoreValues();
    
    void setNontransactionalRead(final boolean p0);
    
    boolean getNontransactionalRead();
    
    void setNontransactionalWrite(final boolean p0);
    
    boolean getNontransactionalWrite();
    
    void setIgnoreCache(final boolean p0);
    
    boolean getIgnoreCache();
    
    boolean getDetachAllOnCommit();
    
    void setDetachAllOnCommit(final boolean p0);
    
    boolean getCopyOnAttach();
    
    void setCopyOnAttach(final boolean p0);
    
    void setName(final String p0);
    
    String getName();
    
    void setPersistenceUnitName(final String p0);
    
    String getPersistenceUnitName();
    
    void setServerTimeZoneID(final String p0);
    
    String getServerTimeZoneID();
    
    void setTransactionType(final String p0);
    
    String getTransactionType();
    
    boolean getReadOnly();
    
    void setReadOnly(final boolean p0);
    
    String getTransactionIsolationLevel();
    
    void setTransactionIsolationLevel(final String p0);
    
    void setDatastoreReadTimeoutMillis(final Integer p0);
    
    Integer getDatastoreReadTimeoutMillis();
    
    void setDatastoreWriteTimeoutMillis(final Integer p0);
    
    Integer getDatastoreWriteTimeoutMillis();
    
    Properties getProperties();
    
    Collection<String> supportedOptions();
    
    DataStoreCache getDataStoreCache();
    
    void addInstanceLifecycleListener(final InstanceLifecycleListener p0, final Class[] p1);
    
    void removeInstanceLifecycleListener(final InstanceLifecycleListener p0);
    
    void addFetchGroups(final FetchGroup... p0);
    
    void removeFetchGroups(final FetchGroup... p0);
    
    void removeAllFetchGroups();
    
    FetchGroup getFetchGroup(final Class p0, final String p1);
    
    Set getFetchGroups();
    
    void registerMetadata(final JDOMetadata p0);
    
    JDOMetadata newMetadata();
    
    TypeMetadata getMetadata(final String p0);
}
