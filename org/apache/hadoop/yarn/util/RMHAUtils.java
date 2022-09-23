// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.yarn.util;

import java.util.ArrayList;
import java.util.List;
import org.apache.hadoop.ha.HAServiceTarget;
import org.apache.hadoop.yarn.client.RMHAServiceTarget;
import java.util.Iterator;
import java.util.Collection;
import org.apache.hadoop.ha.HAServiceProtocol;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.yarn.conf.YarnConfiguration;
import org.apache.hadoop.classification.InterfaceStability;
import org.apache.hadoop.classification.InterfaceAudience;

@InterfaceAudience.Private
@InterfaceStability.Unstable
public class RMHAUtils
{
    public static String findActiveRMHAId(final YarnConfiguration conf) {
        final YarnConfiguration yarnConf = new YarnConfiguration(conf);
        final Collection<String> rmIds = yarnConf.getStringCollection("yarn.resourcemanager.ha.rm-ids");
        for (final String currentId : rmIds) {
            yarnConf.set("yarn.resourcemanager.ha.id", currentId);
            try {
                final HAServiceProtocol.HAServiceState haState = getHAState(yarnConf);
                if (haState.equals(HAServiceProtocol.HAServiceState.ACTIVE)) {
                    return currentId;
                }
                continue;
            }
            catch (Exception ex) {}
        }
        return null;
    }
    
    private static HAServiceProtocol.HAServiceState getHAState(final YarnConfiguration yarnConf) throws Exception {
        final int rpcTimeoutForChecks = yarnConf.getInt("ha.failover-controller.cli-check.rpc-timeout.ms", 20000);
        yarnConf.set("hadoop.security.service.user.name.key", yarnConf.get("yarn.resourcemanager.principal", ""));
        final HAServiceTarget haServiceTarget = new RMHAServiceTarget(yarnConf);
        final HAServiceProtocol proto = haServiceTarget.getProxy(yarnConf, rpcTimeoutForChecks);
        final HAServiceProtocol.HAServiceState haState = proto.getServiceStatus().getState();
        return haState;
    }
    
    public static List<String> getRMHAWebappAddresses(final YarnConfiguration conf) {
        final Collection<String> rmIds = conf.getStringCollection("yarn.resourcemanager.ha.rm-ids");
        final List<String> addrs = new ArrayList<String>();
        if (YarnConfiguration.useHttps(conf)) {
            for (final String id : rmIds) {
                final String addr = conf.get("yarn.resourcemanager.webapp.https.address." + id);
                if (addr != null) {
                    addrs.add(addr);
                }
            }
        }
        else {
            for (final String id : rmIds) {
                final String addr = conf.get("yarn.resourcemanager.webapp.address." + id);
                if (addr != null) {
                    addrs.add(addr);
                }
            }
        }
        return addrs;
    }
}
