// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.store.valuegenerator;

import org.datanucleus.ClassConstants;
import java.lang.reflect.Constructor;
import org.datanucleus.util.NucleusLogger;
import org.datanucleus.store.StoreManager;
import java.util.Properties;
import java.util.HashMap;
import java.util.Map;
import org.datanucleus.util.Localiser;

public class ValueGenerationManager
{
    protected static final Localiser LOCALISER;
    protected Map<String, ValueGenerator> generatorsByName;
    
    public ValueGenerationManager() {
        this.generatorsByName = new HashMap<String, ValueGenerator>();
    }
    
    public void clear() {
        this.generatorsByName.clear();
    }
    
    public synchronized ValueGenerator getValueGenerator(final String name) {
        if (name == null) {
            return null;
        }
        return this.generatorsByName.get(name);
    }
    
    public synchronized ValueGenerator createValueGenerator(final String name, final Class generatorClass, final Properties props, final StoreManager storeMgr, final ValueGenerationConnectionProvider connectionProvider) {
        ValueGenerator generator;
        try {
            if (NucleusLogger.VALUEGENERATION.isDebugEnabled()) {
                NucleusLogger.VALUEGENERATION.debug(ValueGenerationManager.LOCALISER.msg("040001", generatorClass.getName(), name));
            }
            final Class[] argTypes = { String.class, Properties.class };
            final Object[] args = { name, props };
            final Constructor ctor = generatorClass.getConstructor((Class[])argTypes);
            generator = ctor.newInstance(args);
        }
        catch (Exception e) {
            NucleusLogger.VALUEGENERATION.error(e);
            throw new ValueGenerationException(ValueGenerationManager.LOCALISER.msg("040000", generatorClass.getName(), e), e);
        }
        if (generator instanceof AbstractDatastoreGenerator && storeMgr != null) {
            ((AbstractDatastoreGenerator)generator).setStoreManager(storeMgr);
            ((AbstractDatastoreGenerator)generator).setConnectionProvider(connectionProvider);
        }
        this.generatorsByName.put(name, generator);
        return generator;
    }
    
    static {
        LOCALISER = Localiser.getInstance("org.datanucleus.Localisation", ClassConstants.NUCLEUS_CONTEXT_LOADER);
    }
}
