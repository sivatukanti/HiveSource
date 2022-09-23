// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.hive.conf;

import org.apache.hadoop.hive.common.classification.InterfaceAudience;

@InterfaceAudience.Private
public class HiveConfUtil
{
    public static boolean isEmbeddedMetaStore(final String msUri) {
        return msUri == null || msUri.trim().isEmpty();
    }
}
