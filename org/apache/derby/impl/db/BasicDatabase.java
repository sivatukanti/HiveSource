// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.impl.db;

import org.apache.derby.iapi.store.access.FileResource;
import org.apache.derby.impl.sql.execute.JarUtil;
import org.apache.derby.io.StorageFile;
import org.apache.derby.iapi.services.property.PropertyUtil;
import org.apache.derby.iapi.services.daemon.Serviceable;
import org.apache.derby.iapi.sql.dictionary.FileInfoDescriptor;
import org.apache.derby.iapi.sql.dictionary.SchemaDescriptor;
import org.apache.derby.iapi.util.IdUtil;
import java.util.Dictionary;
import org.apache.derby.iapi.services.uuid.UUIDFactory;
import java.io.Serializable;
import org.apache.derby.iapi.services.context.ContextService;
import java.sql.SQLException;
import org.apache.derby.iapi.error.PublicAPI;
import org.apache.derby.iapi.store.access.TransactionController;
import org.apache.derby.iapi.services.i18n.LocaleFinder;
import org.apache.derby.iapi.sql.conn.LanguageConnectionContext;
import org.apache.derby.iapi.services.context.ContextManager;
import org.apache.derby.iapi.error.StandardException;
import org.apache.derby.iapi.services.monitor.ModuleFactory;
import org.apache.derby.iapi.util.DoubleProperties;
import org.apache.derby.iapi.types.DataValueFactory;
import org.apache.derby.iapi.services.monitor.Monitor;
import java.util.Properties;
import org.apache.derby.catalog.UUID;
import java.text.DateFormat;
import java.util.Locale;
import org.apache.derby.iapi.sql.LanguageFactory;
import org.apache.derby.iapi.sql.conn.LanguageConnectionFactory;
import org.apache.derby.iapi.sql.dictionary.DataDictionary;
import org.apache.derby.iapi.services.loader.ClassFactory;
import org.apache.derby.iapi.services.property.PropertyFactory;
import org.apache.derby.iapi.store.access.AccessFactory;
import org.apache.derby.iapi.jdbc.AuthenticationService;
import org.apache.derby.iapi.services.loader.JarReader;
import org.apache.derby.iapi.db.Database;
import org.apache.derby.iapi.services.property.PropertySetCallback;
import org.apache.derby.iapi.services.monitor.ModuleSupportable;
import org.apache.derby.iapi.services.monitor.ModuleControl;

public class BasicDatabase implements ModuleControl, ModuleSupportable, PropertySetCallback, Database, JarReader
{
    protected boolean active;
    private AuthenticationService authenticationService;
    protected AccessFactory af;
    protected PropertyFactory pf;
    protected ClassFactory cfDB;
    private DataDictionary dd;
    protected LanguageConnectionFactory lcf;
    protected LanguageFactory lf;
    protected Object resourceAdapter;
    private Locale databaseLocale;
    private DateFormat dateFormat;
    private DateFormat timeFormat;
    private DateFormat timestampFormat;
    private UUID myUUID;
    protected boolean lastToBoot;
    
    public boolean canSupport(final Properties properties) {
        boolean desiredCreateType = Monitor.isDesiredCreateType(properties, this.getEngineType());
        if (desiredCreateType) {
            final String property = properties.getProperty("replication.slave.mode");
            if (property != null && !property.equals("slavepremode")) {
                desiredCreateType = false;
            }
        }
        return desiredCreateType;
    }
    
    public void boot(final boolean b, final Properties properties) throws StandardException {
        final ModuleFactory monitor = Monitor.getMonitor();
        if (b) {
            if (properties.getProperty("derby.__rt.storage.createWithNoLog") == null) {
                properties.put("derby.__rt.storage.createWithNoLog", "true");
            }
            String s = properties.getProperty("territory");
            if (s == null) {
                s = Locale.getDefault().toString();
            }
            this.databaseLocale = monitor.setLocale(properties, s);
        }
        else {
            this.databaseLocale = monitor.getLocale(this);
        }
        this.setLocale(this.databaseLocale);
        this.bootValidation(b, properties);
        final DataValueFactory dataValueFactory = (DataValueFactory)Monitor.bootServiceModule(b, this, "org.apache.derby.iapi.types.DataValueFactory", properties);
        this.bootStore(b, properties);
        this.myUUID = this.makeDatabaseID(b, properties);
        final DoubleProperties doubleProperties = new DoubleProperties(this.getAllDatabaseProperties(), properties);
        if (this.pf != null) {
            this.pf.addPropertySetNotification(this);
        }
        this.bootClassFactory(b, doubleProperties);
        this.dd = (DataDictionary)Monitor.bootServiceModule(b, this, "org.apache.derby.iapi.sql.dictionary.DataDictionary", doubleProperties);
        this.lcf = (LanguageConnectionFactory)Monitor.bootServiceModule(b, this, "org.apache.derby.iapi.sql.conn.LanguageConnectionFactory", doubleProperties);
        this.lf = (LanguageFactory)Monitor.bootServiceModule(b, this, "org.apache.derby.iapi.sql.LanguageFactory", doubleProperties);
        this.bootResourceAdapter(b, doubleProperties);
        this.authenticationService = this.bootAuthenticationService(b, doubleProperties);
        if (b && this.lastToBoot && properties.getProperty("derby.__rt.storage.createWithNoLog") != null) {
            this.createFinished();
        }
        this.active = true;
        if (this.dd.doCreateIndexStatsRefresher()) {
            this.dd.createIndexStatsRefresher(this, doubleProperties.getProperty("derby.__rt.serviceDirectory"));
        }
    }
    
    public void stop() {
        if (this.dd != null) {
            try {
                this.dd.clearSequenceCaches();
            }
            catch (StandardException ex) {
                ex.printStackTrace(Monitor.getStream().getPrintWriter());
            }
        }
        this.active = false;
    }
    
    public int getEngineType() {
        return 2;
    }
    
    public boolean isReadOnly() {
        return this.af.isReadOnly();
    }
    
    public LanguageConnectionContext setupConnection(final ContextManager contextManager, final String s, final String s2, final String s3) throws StandardException {
        final TransactionController connectionTransaction = this.getConnectionTransaction(contextManager);
        contextManager.setLocaleFinder(this);
        this.pushDbContext(contextManager);
        final LanguageConnectionContext languageConnectionContext = this.lcf.newLanguageConnectionContext(contextManager, connectionTransaction, this.lf, this, s, s2, s3);
        this.pushClassFactoryContext(contextManager, this.lcf.getClassFactory());
        this.lcf.getExecutionFactory().newExecutionContext(contextManager);
        languageConnectionContext.initialize();
        languageConnectionContext.internalCommitNoSync(5);
        return languageConnectionContext;
    }
    
    public final DataDictionary getDataDictionary() {
        return this.dd;
    }
    
    public void pushDbContext(final ContextManager contextManager) {
        final DatabaseContextImpl databaseContextImpl = new DatabaseContextImpl(contextManager, this);
    }
    
    public AuthenticationService getAuthenticationService() throws StandardException {
        return this.authenticationService;
    }
    
    public void startReplicationMaster(final String s, final String s2, final int n, final String s3) throws SQLException {
        try {
            this.af.startReplicationMaster(s, s2, n, s3);
        }
        catch (StandardException ex) {
            throw PublicAPI.wrapStandardException(ex);
        }
    }
    
    public void stopReplicationMaster() throws SQLException {
        try {
            this.af.stopReplicationMaster();
        }
        catch (StandardException ex) {
            throw PublicAPI.wrapStandardException(ex);
        }
    }
    
    public void stopReplicationSlave() throws SQLException {
        throw PublicAPI.wrapStandardException(StandardException.newException("XRE40"));
    }
    
    public boolean isInSlaveMode() {
        return false;
    }
    
    public void failover(final String s) throws StandardException {
        this.af.failover(s);
    }
    
    public void freeze() throws SQLException {
        try {
            this.af.freeze();
        }
        catch (StandardException ex) {
            throw PublicAPI.wrapStandardException(ex);
        }
    }
    
    public void unfreeze() throws SQLException {
        try {
            this.af.unfreeze();
        }
        catch (StandardException ex) {
            throw PublicAPI.wrapStandardException(ex);
        }
    }
    
    public void backup(final String s, final boolean b) throws SQLException {
        try {
            this.af.backup(s, b);
        }
        catch (StandardException ex) {
            throw PublicAPI.wrapStandardException(ex);
        }
    }
    
    public void backupAndEnableLogArchiveMode(final String s, final boolean b, final boolean b2) throws SQLException {
        try {
            this.af.backupAndEnableLogArchiveMode(s, b, b2);
        }
        catch (StandardException ex) {
            throw PublicAPI.wrapStandardException(ex);
        }
    }
    
    public void disableLogArchiveMode(final boolean b) throws SQLException {
        try {
            this.af.disableLogArchiveMode(b);
        }
        catch (StandardException ex) {
            throw PublicAPI.wrapStandardException(ex);
        }
    }
    
    public void checkpoint() throws SQLException {
        try {
            this.af.checkpoint();
        }
        catch (StandardException ex) {
            throw PublicAPI.wrapStandardException(ex);
        }
    }
    
    public Locale getLocale() {
        return this.databaseLocale;
    }
    
    public final UUID getId() {
        return this.myUUID;
    }
    
    public Locale getCurrentLocale() throws StandardException {
        if (this.databaseLocale != null) {
            return this.databaseLocale;
        }
        throw noLocale();
    }
    
    public DateFormat getDateFormat() throws StandardException {
        if (this.databaseLocale != null) {
            if (this.dateFormat == null) {
                this.dateFormat = DateFormat.getDateInstance(1, this.databaseLocale);
            }
            return this.dateFormat;
        }
        throw noLocale();
    }
    
    public DateFormat getTimeFormat() throws StandardException {
        if (this.databaseLocale != null) {
            if (this.timeFormat == null) {
                this.timeFormat = DateFormat.getTimeInstance(1, this.databaseLocale);
            }
            return this.timeFormat;
        }
        throw noLocale();
    }
    
    public DateFormat getTimestampFormat() throws StandardException {
        if (this.databaseLocale != null) {
            if (this.timestampFormat == null) {
                this.timestampFormat = DateFormat.getDateTimeInstance(1, 1, this.databaseLocale);
            }
            return this.timestampFormat;
        }
        throw noLocale();
    }
    
    private static StandardException noLocale() {
        return StandardException.newException("XCXE0.S");
    }
    
    public void setLocale(final Locale databaseLocale) {
        this.databaseLocale = databaseLocale;
        this.dateFormat = null;
        this.timeFormat = null;
        this.timestampFormat = null;
    }
    
    public boolean isActive() {
        return this.active;
    }
    
    public BasicDatabase() {
        this.lastToBoot = true;
    }
    
    protected UUID makeDatabaseID(final boolean b, final Properties properties) throws StandardException {
        final TransactionController transaction = this.af.getTransaction(ContextService.getFactory().getCurrentContextManager());
        String property = null;
        UUID uuid;
        if ((uuid = (UUID)transaction.getProperty("derby.databaseID")) == null) {
            final UUIDFactory uuidFactory = Monitor.getMonitor().getUUIDFactory();
            property = properties.getProperty("derby.databaseID");
            if (property == null) {
                uuid = uuidFactory.createUUID();
            }
            else {
                uuid = uuidFactory.recreateUUID(property);
            }
            transaction.setProperty("derby.databaseID", uuid, true);
        }
        if (property != null) {
            properties.remove("derby.databaseID");
        }
        transaction.commit();
        transaction.destroy();
        return uuid;
    }
    
    public Object getResourceAdapter() {
        return this.resourceAdapter;
    }
    
    public void init(final boolean b, final Dictionary dictionary) {
    }
    
    public boolean validate(final String s, final Serializable s2, final Dictionary dictionary) throws StandardException {
        if (s.equals("derby.engineType")) {
            throw StandardException.newException("XCY02.S", s, s2);
        }
        if (!s.equals("derby.database.classpath")) {
            return false;
        }
        final String s3 = (String)s2;
        String[][] dbClassPath = null;
        if (s3 != null) {
            dbClassPath = IdUtil.parseDbClassPath(s3);
        }
        if (dbClassPath != null) {
            for (int i = 0; i < dbClassPath.length; ++i) {
                final SchemaDescriptor schemaDescriptor = this.dd.getSchemaDescriptor(dbClassPath[i][0], null, false);
                FileInfoDescriptor fileInfoDescriptor = null;
                if (schemaDescriptor != null) {
                    fileInfoDescriptor = this.dd.getFileInfoDescriptor(schemaDescriptor, dbClassPath[i][1]);
                }
                if (fileInfoDescriptor == null) {
                    throw StandardException.newException("42X96", IdUtil.mkQualifiedName(dbClassPath[i]));
                }
            }
        }
        return true;
    }
    
    public Serviceable apply(final String s, final Serializable s2, final Dictionary dictionary) throws StandardException {
        if (!s.equals("derby.database.classpath")) {
            return null;
        }
        if (this.cfDB != null) {
            this.getDataDictionary().invalidateAllSPSPlans();
            String s3 = (String)s2;
            if (s3 == null) {
                s3 = "";
            }
            this.cfDB.notifyModifyClasspath(s3);
        }
        return null;
    }
    
    public Serializable map(final String s, final Serializable s2, final Dictionary dictionary) {
        return null;
    }
    
    protected void createFinished() throws StandardException {
        this.af.createFinished();
    }
    
    protected String getClasspath(final Properties properties) {
        String s = PropertyUtil.getPropertyFromSet(properties, "derby.database.classpath");
        if (s == null) {
            s = PropertyUtil.getSystemProperty("derby.database.classpath", "");
        }
        return s;
    }
    
    protected void bootClassFactory(final boolean b, final Properties properties) throws StandardException {
        final String classpath = this.getClasspath(properties);
        IdUtil.parseDbClassPath(classpath);
        properties.put("derby.__rt.database.classpath", classpath);
        this.cfDB = (ClassFactory)Monitor.bootServiceModule(b, this, "org.apache.derby.iapi.services.loader.ClassFactory", properties);
    }
    
    protected TransactionController getConnectionTransaction(final ContextManager contextManager) throws StandardException {
        return this.af.getTransaction(contextManager);
    }
    
    protected AuthenticationService bootAuthenticationService(final boolean b, final Properties properties) throws StandardException {
        return (AuthenticationService)Monitor.bootServiceModule(b, this, "org.apache.derby.iapi.jdbc.AuthenticationService", properties);
    }
    
    protected void bootValidation(final boolean b, final Properties properties) throws StandardException {
        this.pf = (PropertyFactory)Monitor.bootServiceModule(b, this, "org.apache.derby.iapi.services.property.PropertyFactory", properties);
    }
    
    protected void bootStore(final boolean b, final Properties properties) throws StandardException {
        this.af = (AccessFactory)Monitor.bootServiceModule(b, this, "org.apache.derby.iapi.store.access.AccessFactory", properties);
    }
    
    protected Properties getAllDatabaseProperties() throws StandardException {
        final TransactionController transaction = this.af.getTransaction(ContextService.getFactory().getCurrentContextManager());
        final Properties properties = transaction.getProperties();
        transaction.commit();
        transaction.destroy();
        return properties;
    }
    
    protected void bootResourceAdapter(final boolean b, final Properties properties) {
        try {
            this.resourceAdapter = Monitor.bootServiceModule(b, this, "org.apache.derby.iapi.jdbc.ResourceAdapter", properties);
        }
        catch (StandardException ex) {}
    }
    
    protected void pushClassFactoryContext(final ContextManager contextManager, final ClassFactory classFactory) {
        new StoreClassFactoryContext(contextManager, classFactory, this.af, this);
    }
    
    public StorageFile getJarFile(final String s, final String s2) throws StandardException {
        final FileInfoDescriptor fileInfoDescriptor = this.dd.getFileInfoDescriptor(this.dd.getSchemaDescriptor(s, null, true), s2);
        if (fileInfoDescriptor == null) {
            throw StandardException.newException("X0X13.S", s2, s);
        }
        final long generationId = fileInfoDescriptor.getGenerationId();
        final FileResource fileHandler = this.af.getTransaction(ContextService.getFactory().getCurrentContextManager()).getFileHandler();
        return fileHandler.getAsFile(JarUtil.mkExternalName(fileInfoDescriptor.getUUID(), s, s2, fileHandler.getSeparatorChar()), generationId);
    }
}
