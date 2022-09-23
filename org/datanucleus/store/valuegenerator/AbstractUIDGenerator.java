// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.store.valuegenerator;

import org.datanucleus.util.NucleusLogger;
import java.util.Properties;

public abstract class AbstractUIDGenerator extends AbstractGenerator
{
    public AbstractUIDGenerator(final String name, final Properties props) {
        super(name, props);
        this.allocationSize = 1;
    }
    
    public static Class getStorageClass() {
        return String.class;
    }
    
    @Override
    protected ValueGenerationBlock reserveBlock(final long size) {
        final Object[] ids = new Object[(int)size];
        for (int i = 0; i < size; ++i) {
            ids[i] = this.getIdentifier();
        }
        if (NucleusLogger.VALUEGENERATION.isDebugEnabled()) {
            NucleusLogger.VALUEGENERATION.debug(AbstractUIDGenerator.LOCALISER.msg("040004", "" + size));
        }
        return new ValueGenerationBlock(ids);
    }
    
    protected abstract String getIdentifier();
}
