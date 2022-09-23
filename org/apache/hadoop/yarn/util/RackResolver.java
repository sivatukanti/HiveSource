// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.yarn.util;

import org.apache.commons.logging.LogFactory;
import com.google.common.annotations.VisibleForTesting;
import java.util.List;
import org.apache.hadoop.net.NodeBase;
import java.util.ArrayList;
import org.apache.hadoop.net.Node;
import org.apache.hadoop.net.CachedDNSToSwitchMapping;
import org.apache.hadoop.util.ReflectionUtils;
import org.apache.hadoop.net.ScriptBasedMapping;
import org.apache.hadoop.conf.Configuration;
import org.apache.commons.logging.Log;
import org.apache.hadoop.net.DNSToSwitchMapping;
import org.apache.hadoop.classification.InterfaceAudience;

@InterfaceAudience.LimitedPrivate({ "YARN", "MAPREDUCE" })
public class RackResolver
{
    private static DNSToSwitchMapping dnsToSwitchMapping;
    private static boolean initCalled;
    private static final Log LOG;
    
    public static synchronized void init(final Configuration conf) {
        if (RackResolver.initCalled) {
            return;
        }
        RackResolver.initCalled = true;
        final Class<? extends DNSToSwitchMapping> dnsToSwitchMappingClass = conf.getClass("net.topology.node.switch.mapping.impl", ScriptBasedMapping.class, DNSToSwitchMapping.class);
        try {
            final DNSToSwitchMapping newInstance = ReflectionUtils.newInstance(dnsToSwitchMappingClass, conf);
            RackResolver.dnsToSwitchMapping = ((newInstance instanceof CachedDNSToSwitchMapping) ? newInstance : new CachedDNSToSwitchMapping(newInstance));
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    
    public static Node resolve(final Configuration conf, final String hostName) {
        init(conf);
        return coreResolve(hostName);
    }
    
    public static Node resolve(final String hostName) {
        if (!RackResolver.initCalled) {
            throw new IllegalStateException("RackResolver class not yet initialized");
        }
        return coreResolve(hostName);
    }
    
    private static Node coreResolve(final String hostName) {
        final List<String> tmpList = new ArrayList<String>(1);
        tmpList.add(hostName);
        final List<String> rNameList = RackResolver.dnsToSwitchMapping.resolve(tmpList);
        String rName = null;
        if (rNameList == null || rNameList.get(0) == null) {
            rName = "/default-rack";
            RackResolver.LOG.info("Couldn't resolve " + hostName + ". Falling back to " + "/default-rack");
        }
        else {
            rName = rNameList.get(0);
            RackResolver.LOG.info("Resolved " + hostName + " to " + rName);
        }
        return new NodeBase(hostName, rName);
    }
    
    @InterfaceAudience.Private
    @VisibleForTesting
    static DNSToSwitchMapping getDnsToSwitchMapping() {
        return RackResolver.dnsToSwitchMapping;
    }
    
    static {
        RackResolver.initCalled = false;
        LOG = LogFactory.getLog(RackResolver.class);
    }
}
