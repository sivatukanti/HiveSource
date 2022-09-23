// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.store.valuegenerator;

import java.util.UUID;
import java.util.Properties;

public class UUIDGenerator extends AbstractUIDGenerator
{
    public UUIDGenerator(final String name, final Properties props) {
        super(name, props);
    }
    
    @Override
    protected String getIdentifier() {
        return UUID.randomUUID().toString();
    }
}
