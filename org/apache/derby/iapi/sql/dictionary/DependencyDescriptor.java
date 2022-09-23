// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.iapi.sql.dictionary;

import org.apache.derby.iapi.sql.depend.Provider;
import org.apache.derby.iapi.sql.depend.Dependent;
import org.apache.derby.catalog.DependableFinder;
import org.apache.derby.catalog.UUID;

public class DependencyDescriptor extends TupleDescriptor implements UniqueTupleDescriptor
{
    private final UUID dependentID;
    private final DependableFinder dependentBloodhound;
    private final UUID providerID;
    private final DependableFinder providerBloodhound;
    
    public DependencyDescriptor(final Dependent dependent, final Provider provider) {
        this.dependentID = dependent.getObjectID();
        this.dependentBloodhound = dependent.getDependableFinder();
        this.providerID = provider.getObjectID();
        this.providerBloodhound = provider.getDependableFinder();
    }
    
    public DependencyDescriptor(final UUID dependentID, final DependableFinder dependentBloodhound, final UUID providerID, final DependableFinder providerBloodhound) {
        this.dependentID = dependentID;
        this.dependentBloodhound = dependentBloodhound;
        this.providerID = providerID;
        this.providerBloodhound = providerBloodhound;
    }
    
    public UUID getUUID() {
        return this.dependentID;
    }
    
    public DependableFinder getDependentFinder() {
        return this.dependentBloodhound;
    }
    
    public UUID getProviderID() {
        return this.providerID;
    }
    
    public DependableFinder getProviderFinder() {
        return this.providerBloodhound;
    }
}
