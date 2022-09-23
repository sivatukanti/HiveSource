// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.util;

import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

public class CombinedIPWhiteList implements IPList
{
    public static final Logger LOG;
    private static final String LOCALHOST_IP = "127.0.0.1";
    private final IPList[] networkLists;
    
    public CombinedIPWhiteList(final String fixedWhiteListFile, final String variableWhiteListFile, final long cacheExpiryInSeconds) {
        final IPList fixedNetworkList = new FileBasedIPList(fixedWhiteListFile);
        if (variableWhiteListFile != null) {
            final IPList variableNetworkList = new CacheableIPList(new FileBasedIPList(variableWhiteListFile), cacheExpiryInSeconds);
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
        if ("127.0.0.1".equals(ipAddress)) {
            return true;
        }
        for (final IPList networkList : this.networkLists) {
            if (networkList.isIn(ipAddress)) {
                return true;
            }
        }
        return false;
    }
    
    static {
        LOG = LoggerFactory.getLogger(CombinedIPWhiteList.class);
    }
}
