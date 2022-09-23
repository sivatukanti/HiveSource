// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.store.scostore;

import org.datanucleus.store.StoreManager;
import org.datanucleus.metadata.AbstractMemberMetaData;

public interface Store
{
    AbstractMemberMetaData getOwnerMemberMetaData();
    
    StoreManager getStoreManager();
}
