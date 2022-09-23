// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.store.schema;

import java.util.Properties;
import java.util.Set;

public interface SchemaAwareStoreManager
{
    void createSchema(final Set<String> p0, final Properties p1);
    
    void deleteSchema(final Set<String> p0, final Properties p1);
    
    void validateSchema(final Set<String> p0, final Properties p1);
}
