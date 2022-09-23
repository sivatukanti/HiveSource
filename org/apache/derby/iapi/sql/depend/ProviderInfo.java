// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.iapi.sql.depend;

import org.apache.derby.catalog.UUID;
import org.apache.derby.catalog.DependableFinder;
import org.apache.derby.iapi.services.io.Formatable;

public interface ProviderInfo extends Formatable
{
    DependableFinder getDependableFinder();
    
    UUID getObjectId();
    
    String getProviderName();
}
