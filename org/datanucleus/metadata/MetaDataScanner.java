// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.metadata;

import java.util.Set;

public interface MetaDataScanner
{
    Set<String> scanForPersistableClasses(final PersistenceUnitMetaData p0);
}
