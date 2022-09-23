// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.store.rdbms;

import org.datanucleus.util.Localiser;
import org.datanucleus.plugin.ConfigurationElement;
import org.datanucleus.store.valuegenerator.ValueGenerationManager;
import org.datanucleus.metadata.ExtensionMetaData;
import org.datanucleus.exceptions.NucleusException;
import org.datanucleus.util.NucleusLogger;
import org.datanucleus.PersistenceConfiguration;
import org.datanucleus.transaction.TransactionUtils;
import org.datanucleus.store.connection.ManagedConnection;
import org.datanucleus.store.valuegenerator.ValueGenerationConnectionProvider;
import java.util.Properties;
import org.datanucleus.store.StoreManager;
import org.datanucleus.metadata.SequenceMetaData;
import org.datanucleus.ExecutionContext;

public class NucleusSequenceImpl extends org.datanucleus.store.NucleusSequenceImpl
{
    public NucleusSequenceImpl(final ExecutionContext objectMgr, final RDBMSStoreManager storeMgr, final SequenceMetaData seqmd) {
        super(objectMgr, storeMgr, seqmd);
    }
    
    public void setGenerator() {
        String valueGeneratorName = null;
        if (((RDBMSStoreManager)this.storeManager).getDatastoreAdapter().supportsOption("Sequences")) {
            valueGeneratorName = "sequence";
        }
        else {
            valueGeneratorName = "table-sequence";
        }
        final Properties props = new Properties();
        final ExtensionMetaData[] seqExtensions = this.seqMetaData.getExtensions();
        if (seqExtensions != null && seqExtensions.length > 0) {
            for (int i = 0; i < seqExtensions.length; ++i) {
                props.put(seqExtensions[i].getKey(), seqExtensions[i].getValue());
            }
        }
        props.put("sequence-name", this.seqMetaData.getDatastoreSequence());
        final ValueGenerationManager mgr = this.storeManager.getValueGenerationManager();
        final ValueGenerationConnectionProvider connProvider = new ValueGenerationConnectionProvider() {
            ManagedConnection mconn;
            
            @Override
            public ManagedConnection retrieveConnection() {
                final PersistenceConfiguration conf = NucleusSequenceImpl.this.ec.getNucleusContext().getPersistenceConfiguration();
                final int isolationLevel = TransactionUtils.getTransactionIsolationLevelForName(conf.getStringProperty("datanucleus.valuegeneration.transactionIsolation"));
                return this.mconn = ((RDBMSStoreManager)NucleusSequenceImpl.this.storeManager).getConnection(isolationLevel);
            }
            
            @Override
            public void releaseConnection() {
                try {
                    this.mconn.release();
                }
                catch (NucleusException e) {
                    NucleusLogger.PERSISTENCE.error(NucleusSequenceImpl.LOCALISER.msg("017007", e));
                    throw e;
                }
            }
        };
        Class cls = null;
        final ConfigurationElement elem = this.ec.getNucleusContext().getPluginManager().getConfigurationElementForExtension("org.datanucleus.store_valuegenerator", new String[] { "name", "datastore" }, new String[] { valueGeneratorName, this.storeManager.getStoreManagerKey() });
        if (elem != null) {
            cls = this.ec.getNucleusContext().getPluginManager().loadClass(elem.getExtension().getPlugin().getSymbolicName(), elem.getAttribute("class-name"));
        }
        if (cls == null) {
            throw new NucleusException("Cannot create ValueGenerator for strategy " + valueGeneratorName);
        }
        this.generator = mgr.createValueGenerator(this.seqMetaData.getName(), cls, props, this.storeManager, connProvider);
        if (NucleusLogger.PERSISTENCE.isDebugEnabled()) {
            NucleusLogger.PERSISTENCE.debug(NucleusSequenceImpl.LOCALISER.msg("017003", this.seqMetaData.getName(), valueGeneratorName));
        }
    }
}
