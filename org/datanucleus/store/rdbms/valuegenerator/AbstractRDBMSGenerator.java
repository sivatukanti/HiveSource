// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.store.rdbms.valuegenerator;

import org.datanucleus.store.rdbms.RDBMSStoreManager;
import org.datanucleus.store.valuegenerator.ValueGenerationException;
import org.datanucleus.util.NucleusLogger;
import org.datanucleus.store.valuegenerator.ValueGenerationBlock;
import java.util.Properties;
import org.datanucleus.store.connection.ManagedConnection;
import org.datanucleus.util.Localiser;
import org.datanucleus.store.valuegenerator.AbstractDatastoreGenerator;

public abstract class AbstractRDBMSGenerator extends AbstractDatastoreGenerator
{
    protected static final Localiser LOCALISER_RDBMS;
    protected ManagedConnection connection;
    
    public AbstractRDBMSGenerator(final String name, final Properties props) {
        super(name, props);
        this.allocationSize = 1;
    }
    
    public boolean requiresConnection() {
        return true;
    }
    
    @Override
    protected ValueGenerationBlock obtainGenerationBlock(final int number) {
        ValueGenerationBlock block = null;
        boolean repository_exists = true;
        try {
            if (this.requiresConnection()) {
                this.connection = this.connectionProvider.retrieveConnection();
            }
            if (this.requiresRepository() && !this.repositoryExists && !(this.repositoryExists = this.repositoryExists())) {
                this.createRepository();
                this.repositoryExists = true;
            }
            try {
                if (number < 0) {
                    block = this.reserveBlock();
                }
                else {
                    block = this.reserveBlock(number);
                }
            }
            catch (ValueGenerationException poidex) {
                NucleusLogger.VALUEGENERATION.info(AbstractRDBMSGenerator.LOCALISER.msg("040003", poidex.getMessage()));
                if (NucleusLogger.VALUEGENERATION.isDebugEnabled()) {
                    NucleusLogger.VALUEGENERATION.debug("Caught exception", poidex);
                }
                if (!this.requiresRepository()) {
                    throw poidex;
                }
                repository_exists = false;
            }
            catch (RuntimeException ex) {
                NucleusLogger.VALUEGENERATION.info(AbstractRDBMSGenerator.LOCALISER.msg("040003", ex.getMessage()));
                if (NucleusLogger.VALUEGENERATION.isDebugEnabled()) {
                    NucleusLogger.VALUEGENERATION.debug("Caught exception", ex);
                }
                if (!this.requiresRepository()) {
                    throw ex;
                }
                repository_exists = false;
            }
        }
        finally {
            if (this.connection != null && this.requiresConnection()) {
                this.connectionProvider.releaseConnection();
                this.connection = null;
            }
        }
        if (!repository_exists) {
            try {
                if (this.requiresConnection()) {
                    this.connection = this.connectionProvider.retrieveConnection();
                }
                NucleusLogger.VALUEGENERATION.info(AbstractRDBMSGenerator.LOCALISER.msg("040005"));
                if (!this.createRepository()) {
                    throw new ValueGenerationException(AbstractRDBMSGenerator.LOCALISER.msg("040002"));
                }
                if (number < 0) {
                    block = this.reserveBlock();
                }
                else {
                    block = this.reserveBlock(number);
                }
            }
            finally {
                if (this.requiresConnection()) {
                    this.connectionProvider.releaseConnection();
                    this.connection = null;
                }
            }
        }
        return block;
    }
    
    static {
        LOCALISER_RDBMS = Localiser.getInstance("org.datanucleus.store.rdbms.Localisation", RDBMSStoreManager.class.getClassLoader());
    }
}
