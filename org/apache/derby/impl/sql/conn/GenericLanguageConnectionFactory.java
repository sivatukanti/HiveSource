// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.impl.sql.conn;

import org.apache.derby.impl.sql.compile.ParserImpl;
import org.apache.derby.iapi.sql.compile.Parser;
import org.apache.derby.iapi.sql.compile.CompilerContext;
import org.apache.derby.iapi.services.daemon.Serviceable;
import org.apache.derby.iapi.util.IdUtil;
import org.apache.derby.iapi.util.StringUtil;
import java.io.Serializable;
import java.util.Dictionary;
import org.apache.derby.iapi.services.cache.CacheFactory;
import org.apache.derby.iapi.services.property.PropertyUtil;
import org.apache.derby.iapi.services.monitor.Monitor;
import java.util.Properties;
import org.apache.derby.iapi.services.cache.Cacheable;
import org.apache.derby.iapi.error.StandardException;
import org.apache.derby.iapi.sql.conn.LanguageConnectionContext;
import org.apache.derby.iapi.db.Database;
import org.apache.derby.iapi.sql.LanguageFactory;
import org.apache.derby.iapi.store.access.TransactionController;
import org.apache.derby.iapi.services.context.ContextManager;
import org.apache.derby.impl.sql.GenericStatement;
import org.apache.derby.iapi.sql.Statement;
import org.apache.derby.iapi.sql.dictionary.SchemaDescriptor;
import org.apache.derby.iapi.services.cache.CacheManager;
import org.apache.derby.iapi.services.property.PropertyFactory;
import org.apache.derby.iapi.sql.compile.NodeFactory;
import org.apache.derby.iapi.services.loader.ClassFactory;
import org.apache.derby.iapi.services.compiler.JavaFactory;
import org.apache.derby.iapi.services.uuid.UUIDFactory;
import org.apache.derby.iapi.types.DataValueFactory;
import org.apache.derby.iapi.sql.compile.TypeCompilerFactory;
import org.apache.derby.iapi.sql.compile.OptimizerFactory;
import org.apache.derby.iapi.sql.execute.ExecutionFactory;
import org.apache.derby.iapi.services.monitor.ModuleSupportable;
import org.apache.derby.iapi.services.monitor.ModuleControl;
import org.apache.derby.iapi.services.property.PropertySetCallback;
import org.apache.derby.iapi.services.cache.CacheableFactory;
import org.apache.derby.iapi.sql.conn.LanguageConnectionFactory;

public class GenericLanguageConnectionFactory implements LanguageConnectionFactory, CacheableFactory, PropertySetCallback, ModuleControl, ModuleSupportable
{
    private ExecutionFactory ef;
    private OptimizerFactory of;
    private TypeCompilerFactory tcf;
    private DataValueFactory dvf;
    private UUIDFactory uuidFactory;
    private JavaFactory javaFactory;
    private ClassFactory classFactory;
    private NodeFactory nodeFactory;
    private PropertyFactory pf;
    private int nextLCCInstanceNumber;
    private int cacheSize;
    private CacheManager singleStatementCache;
    
    public GenericLanguageConnectionFactory() {
        this.cacheSize = 100;
    }
    
    public Statement getStatement(final SchemaDescriptor schemaDescriptor, final String s, final boolean b) {
        return new GenericStatement(schemaDescriptor, s, b);
    }
    
    public LanguageConnectionContext newLanguageConnectionContext(final ContextManager contextManager, final TransactionController transactionController, final LanguageFactory languageFactory, final Database database, final String s, final String s2, final String s3) throws StandardException {
        return new GenericLanguageConnectionContext(contextManager, transactionController, languageFactory, this, database, s, this.getNextLCCInstanceNumber(), s2, s3);
    }
    
    public Cacheable newCacheable(final CacheManager cacheManager) {
        return new CachedStatement();
    }
    
    public UUIDFactory getUUIDFactory() {
        return this.uuidFactory;
    }
    
    public ClassFactory getClassFactory() {
        return this.classFactory;
    }
    
    public JavaFactory getJavaFactory() {
        return this.javaFactory;
    }
    
    public NodeFactory getNodeFactory() {
        return this.nodeFactory;
    }
    
    public ExecutionFactory getExecutionFactory() {
        return this.ef;
    }
    
    public PropertyFactory getPropertyFactory() {
        return this.pf;
    }
    
    public OptimizerFactory getOptimizerFactory() {
        return this.of;
    }
    
    public TypeCompilerFactory getTypeCompilerFactory() {
        return this.tcf;
    }
    
    public DataValueFactory getDataValueFactory() {
        return this.dvf;
    }
    
    public boolean canSupport(final Properties properties) {
        return Monitor.isDesiredType(properties, 130);
    }
    
    private int statementCacheSize(final Properties properties) {
        final String propertyFromSet = PropertyUtil.getPropertyFromSet(properties, "derby.language.statementCacheSize");
        if (propertyFromSet != null) {
            try {
                this.cacheSize = Integer.parseInt(propertyFromSet);
            }
            catch (NumberFormatException ex) {
                this.cacheSize = 100;
            }
        }
        return this.cacheSize;
    }
    
    public void boot(final boolean b, final Properties properties) throws StandardException {
        this.dvf = (DataValueFactory)Monitor.bootServiceModule(b, this, "org.apache.derby.iapi.types.DataValueFactory", properties);
        this.javaFactory = (JavaFactory)Monitor.startSystemModule("org.apache.derby.iapi.services.compiler.JavaFactory");
        this.uuidFactory = Monitor.getMonitor().getUUIDFactory();
        this.classFactory = (ClassFactory)Monitor.getServiceModule(this, "org.apache.derby.iapi.services.loader.ClassFactory");
        if (this.classFactory == null) {
            this.classFactory = (ClassFactory)Monitor.findSystemModule("org.apache.derby.iapi.services.loader.ClassFactory");
        }
        this.setValidation();
        this.ef = (ExecutionFactory)Monitor.bootServiceModule(b, this, "org.apache.derby.iapi.sql.execute.ExecutionFactory", properties);
        this.of = (OptimizerFactory)Monitor.bootServiceModule(b, this, "org.apache.derby.iapi.sql.compile.OptimizerFactory", properties);
        this.tcf = (TypeCompilerFactory)Monitor.startSystemModule("org.apache.derby.iapi.sql.compile.TypeCompilerFactory");
        this.nodeFactory = (NodeFactory)Monitor.bootServiceModule(b, this, "org.apache.derby.iapi.sql.compile.NodeFactory", properties);
        final int statementCacheSize = this.statementCacheSize(properties);
        if (statementCacheSize > 0) {
            this.singleStatementCache = ((CacheFactory)Monitor.startSystemModule("org.apache.derby.iapi.services.cache.CacheFactory")).newCacheManager(this, "StatementCache", statementCacheSize / 4, statementCacheSize);
        }
    }
    
    public CacheManager getStatementCache() {
        return this.singleStatementCache;
    }
    
    public void stop() {
    }
    
    public void init(final boolean b, final Dictionary dictionary) {
    }
    
    public boolean validate(final String s, final Serializable s2, final Dictionary dictionary) throws StandardException {
        if (s2 == null) {
            return true;
        }
        if (s.equals("derby.database.defaultConnectionMode")) {
            final String s3 = (String)s2;
            if (s3 != null && !StringUtil.SQLEqualsIgnoreCase(s3, "NOACCESS") && !StringUtil.SQLEqualsIgnoreCase(s3, "READONLYACCESS") && !StringUtil.SQLEqualsIgnoreCase(s3, "FULLACCESS")) {
                throw StandardException.newException("4250B", s, s3);
            }
            return true;
        }
        else {
            if (!s.equals("derby.database.readOnlyAccessUsers") && !s.equals("derby.database.fullAccessUsers")) {
                return false;
            }
            final String s4 = (String)s2;
            String[] idList;
            try {
                idList = IdUtil.parseIdList(s4);
            }
            catch (StandardException ex) {
                throw StandardException.newException("4250B", ex, s, s4);
            }
            final String dups = IdUtil.dups(idList);
            if (dups != null) {
                throw StandardException.newException("4250D", s, dups);
            }
            String s5;
            if (s.equals("derby.database.readOnlyAccessUsers")) {
                s5 = dictionary.get("derby.database.fullAccessUsers");
            }
            else {
                s5 = dictionary.get("derby.database.readOnlyAccessUsers");
            }
            final String intersect = IdUtil.intersect(idList, IdUtil.parseIdList(s5));
            if (intersect != null) {
                throw StandardException.newException("4250C", intersect);
            }
            return true;
        }
    }
    
    public Serviceable apply(final String s, final Serializable s2, final Dictionary dictionary) {
        return null;
    }
    
    public Serializable map(final String s, final Serializable s2, final Dictionary dictionary) {
        return null;
    }
    
    protected void setValidation() throws StandardException {
        (this.pf = (PropertyFactory)Monitor.findServiceModule(this, "org.apache.derby.iapi.services.property.PropertyFactory")).addPropertySetNotification(this);
    }
    
    public Parser newParser(final CompilerContext compilerContext) {
        return new ParserImpl(compilerContext);
    }
    
    protected synchronized int getNextLCCInstanceNumber() {
        return this.nextLCCInstanceNumber++;
    }
}
