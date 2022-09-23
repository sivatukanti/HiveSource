// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.yarn.util;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.yarn.factory.providers.RecordFactoryProvider;
import org.apache.hadoop.yarn.factories.RecordFactory;
import org.apache.hadoop.classification.InterfaceStability;
import org.apache.hadoop.classification.InterfaceAudience;

@InterfaceAudience.LimitedPrivate({ "MapReduce", "YARN" })
@InterfaceStability.Unstable
public class Records
{
    private static final RecordFactory factory;
    
    public static <T> T newRecord(final Class<T> cls) {
        return Records.factory.newRecordInstance(cls);
    }
    
    static {
        factory = RecordFactoryProvider.getRecordFactory(null);
    }
}
