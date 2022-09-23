// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.impl.sql.depend;

import org.apache.derby.catalog.UUID;
import org.apache.derby.iapi.sql.depend.Dependent;
import org.apache.derby.iapi.sql.depend.Provider;
import org.apache.derby.iapi.sql.depend.Dependency;

class BasicDependency implements Dependency
{
    private final Provider provider;
    private final Dependent dependent;
    
    public UUID getProviderKey() {
        return this.provider.getObjectID();
    }
    
    public Provider getProvider() {
        return this.provider;
    }
    
    public Dependent getDependent() {
        return this.dependent;
    }
    
    BasicDependency(final Dependent dependent, final Provider provider) {
        this.dependent = dependent;
        this.provider = provider;
    }
}
