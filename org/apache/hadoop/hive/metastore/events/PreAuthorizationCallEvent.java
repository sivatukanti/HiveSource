// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.hive.metastore.events;

import org.apache.hadoop.hive.metastore.HiveMetaStore;

public class PreAuthorizationCallEvent extends PreEventContext
{
    public PreAuthorizationCallEvent(final HiveMetaStore.HMSHandler handler) {
        super(PreEventType.AUTHORIZATION_API_CALL, handler);
    }
}
