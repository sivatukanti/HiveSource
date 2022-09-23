// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.store.valuegenerator;

import java.util.Properties;

public class AUIDGenerator extends AbstractUIDGenerator
{
    public AUIDGenerator(final String name, final Properties props) {
        super(name, props);
        this.allocationSize = 1;
    }
    
    @Override
    protected String getIdentifier() {
        return new AUID().toString();
    }
}
