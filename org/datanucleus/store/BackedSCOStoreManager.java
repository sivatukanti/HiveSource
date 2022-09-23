// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.store;

import org.datanucleus.store.scostore.Store;
import org.datanucleus.metadata.AbstractMemberMetaData;
import org.datanucleus.ClassLoaderResolver;

public interface BackedSCOStoreManager
{
    Store getBackingStoreForField(final ClassLoaderResolver p0, final AbstractMemberMetaData p1, final Class p2);
}
