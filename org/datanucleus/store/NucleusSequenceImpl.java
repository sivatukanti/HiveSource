// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.store;

import org.datanucleus.ClassConstants;
import org.datanucleus.plugin.ConfigurationElement;
import org.datanucleus.store.valuegenerator.ValueGenerationManager;
import org.datanucleus.metadata.ExtensionMetaData;
import org.datanucleus.util.NucleusLogger;
import org.datanucleus.exceptions.NucleusException;
import org.datanucleus.store.connection.ManagedConnection;
import org.datanucleus.store.valuegenerator.ValueGenerationConnectionProvider;
import java.util.Properties;
import org.datanucleus.ExecutionContext;
import org.datanucleus.store.valuegenerator.ValueGenerator;
import org.datanucleus.metadata.SequenceMetaData;
import org.datanucleus.util.Localiser;

public class NucleusSequenceImpl implements NucleusSequence
{
    protected static final Localiser LOCALISER;
    protected final StoreManager storeManager;
    protected final SequenceMetaData seqMetaData;
    protected ValueGenerator generator;
    protected final ExecutionContext ec;
    
    public NucleusSequenceImpl(final ExecutionContext objectMgr, final StoreManager storeMgr, final SequenceMetaData seqmd) {
        this.ec = objectMgr;
        this.storeManager = storeMgr;
        this.seqMetaData = seqmd;
        this.setGenerator();
    }
    
    protected void setGenerator() {
        final String valueGeneratorName = "sequence";
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
                return this.mconn = NucleusSequenceImpl.this.storeManager.getConnection(NucleusSequenceImpl.this.ec);
            }
            
            @Override
            public void releaseConnection() {
                this.mconn.release();
                this.mconn = null;
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
        if (NucleusLogger.DATASTORE.isDebugEnabled()) {
            NucleusLogger.DATASTORE.debug(NucleusSequenceImpl.LOCALISER.msg("017003", this.seqMetaData.getName(), valueGeneratorName));
        }
    }
    
    @Override
    public String getName() {
        return this.seqMetaData.getName();
    }
    
    @Override
    public void allocate(final int additional) {
        this.generator.allocate(additional);
    }
    
    @Override
    public Object next() {
        return this.generator.next();
    }
    
    @Override
    public long nextValue() {
        return this.generator.nextValue();
    }
    
    @Override
    public Object current() {
        return this.generator.current();
    }
    
    @Override
    public long currentValue() {
        return this.generator.currentValue();
    }
    
    static {
        LOCALISER = Localiser.getInstance("org.datanucleus.Localisation", ClassConstants.NUCLEUS_CONTEXT_LOADER);
    }
}
