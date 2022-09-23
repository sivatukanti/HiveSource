// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hive.service.cli.session;

import org.apache.hadoop.hive.conf.HiveConf;

public class HiveSessionHookContextImpl implements HiveSessionHookContext
{
    private final HiveSession hiveSession;
    
    HiveSessionHookContextImpl(final HiveSession hiveSession) {
        this.hiveSession = hiveSession;
    }
    
    @Override
    public HiveConf getSessionConf() {
        return this.hiveSession.getHiveConf();
    }
    
    @Override
    public String getSessionUser() {
        return this.hiveSession.getUserName();
    }
    
    @Override
    public String getSessionHandle() {
        return this.hiveSession.getSessionHandle().toString();
    }
}
