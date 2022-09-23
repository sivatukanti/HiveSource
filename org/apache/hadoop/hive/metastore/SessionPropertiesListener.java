// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.hive.metastore;

import org.apache.hadoop.hive.metastore.api.MetaException;
import java.util.concurrent.TimeUnit;
import org.apache.hadoop.hive.conf.HiveConf;
import org.apache.hadoop.hive.metastore.events.ConfigChangeEvent;
import org.apache.hadoop.conf.Configuration;

public class SessionPropertiesListener extends MetaStoreEventListener
{
    public SessionPropertiesListener(final Configuration configuration) {
        super(configuration);
    }
    
    @Override
    public void onConfigChange(final ConfigChangeEvent changeEvent) throws MetaException {
        if (changeEvent.getKey().equals(HiveConf.ConfVars.METASTORE_CLIENT_SOCKET_TIMEOUT.varname)) {
            Deadline.resetTimeout(HiveConf.toTime(changeEvent.getNewValue(), TimeUnit.SECONDS, TimeUnit.MILLISECONDS));
        }
    }
}
