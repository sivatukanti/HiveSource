// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.util;

import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

public class CombinedIPList implements IPList
{
    public static final Logger LOG;
    private final IPList[] networkLists;
    
    public CombinedIPList(final String fixedBlackListFile, final String variableBlackListFile, final long cacheExpiryInSeconds) {
        final IPList fixedNetworkList = new FileBasedIPList(fixedBlackListFile);
        if (variableBlackListFile != null) {
            final IPList variableNetworkList = new CacheableIPList(new FileBasedIPList(variableBlackListFile), cacheExpiryInSeconds);
            this.networkLists = new IPList[] { fixedNetworkList, variableNetworkList };
        }
        else {
            this.networkLists = new IPList[] { fixedNetworkList };
        }
    }
    
    @Override
    public boolean isIn(final String ipAddress) {
        if (ipAddress == null) {
            throw new IllegalArgumentException("ipAddress is null");
        }
        for (final IPList networkList : this.networkLists) {
            if (networkList.isIn(ipAddress)) {
                return true;
            }
        }
        return false;
    }
    
    static {
        LOG = LoggerFactory.getLogger(CombinedIPList.class);
    }
}
