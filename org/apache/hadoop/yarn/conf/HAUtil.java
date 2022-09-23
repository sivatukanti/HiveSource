// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.yarn.conf;

import org.apache.commons.logging.LogFactory;
import com.google.common.annotations.VisibleForTesting;
import java.net.InetSocketAddress;
import org.apache.hadoop.HadoopIllegalArgumentException;
import org.apache.hadoop.net.NetUtils;
import java.util.Iterator;
import java.util.Collection;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.yarn.exceptions.YarnRuntimeException;
import org.apache.commons.logging.Log;
import org.apache.hadoop.classification.InterfaceAudience;

@InterfaceAudience.Private
public class HAUtil
{
    private static Log LOG;
    public static final String BAD_CONFIG_MESSAGE_PREFIX = "Invalid configuration! ";
    
    private HAUtil() {
    }
    
    private static void throwBadConfigurationException(final String msg) {
        throw new YarnRuntimeException("Invalid configuration! " + msg);
    }
    
    public static boolean isHAEnabled(final Configuration conf) {
        return conf.getBoolean("yarn.resourcemanager.ha.enabled", false);
    }
    
    public static boolean isAutomaticFailoverEnabled(final Configuration conf) {
        return conf.getBoolean("yarn.resourcemanager.ha.automatic-failover.enabled", true);
    }
    
    public static boolean isAutomaticFailoverEnabledAndEmbedded(final Configuration conf) {
        return isAutomaticFailoverEnabled(conf) && isAutomaticFailoverEmbedded(conf);
    }
    
    public static boolean isAutomaticFailoverEmbedded(final Configuration conf) {
        return conf.getBoolean("yarn.resourcemanager.ha.automatic-failover.embedded", true);
    }
    
    public static void verifyAndSetConfiguration(final Configuration conf) throws YarnRuntimeException {
        verifyAndSetRMHAIdsList(conf);
        verifyAndSetCurrentRMHAId(conf);
        verifyAndSetAllServiceAddresses(conf);
    }
    
    private static void verifyAndSetRMHAIdsList(final Configuration conf) {
        final Collection<String> ids = conf.getTrimmedStringCollection("yarn.resourcemanager.ha.rm-ids");
        if (ids.size() < 2) {
            throwBadConfigurationException(getInvalidValueMessage("yarn.resourcemanager.ha.rm-ids", conf.get("yarn.resourcemanager.ha.rm-ids") + "\nHA mode requires atleast two RMs"));
        }
        final StringBuilder setValue = new StringBuilder();
        for (final String id : ids) {
            for (final String prefix : YarnConfiguration.getServiceAddressConfKeys(conf)) {
                checkAndSetRMRPCAddress(prefix, id, conf);
            }
            setValue.append(id);
            setValue.append(",");
        }
        conf.set("yarn.resourcemanager.ha.rm-ids", setValue.substring(0, setValue.length() - 1));
    }
    
    private static void verifyAndSetCurrentRMHAId(final Configuration conf) {
        final String rmId = getRMHAId(conf);
        if (rmId == null) {
            final StringBuilder msg = new StringBuilder();
            msg.append("Can not find valid RM_HA_ID. None of ");
            for (final String id : conf.getTrimmedStringCollection("yarn.resourcemanager.ha.rm-ids")) {
                msg.append(addSuffix("yarn.resourcemanager.address", id) + " ");
            }
            msg.append(" are matching the local address OR yarn.resourcemanager.ha.id is not specified in HA Configuration");
            throwBadConfigurationException(msg.toString());
        }
        else {
            final Collection<String> ids = getRMHAIds(conf);
            if (!ids.contains(rmId)) {
                throwBadConfigurationException(getRMHAIdNeedToBeIncludedMessage(ids.toString(), rmId));
            }
        }
        conf.set("yarn.resourcemanager.ha.id", rmId);
    }
    
    private static void verifyAndSetConfValue(final String prefix, final Configuration conf) {
        String confKey = null;
        String confValue = null;
        try {
            confKey = getConfKeyForRMInstance(prefix, conf);
            confValue = getConfValueForRMInstance(prefix, conf);
            conf.set(prefix, confValue);
        }
        catch (YarnRuntimeException yre) {
            throw yre;
        }
        catch (IllegalArgumentException iae) {
            String errmsg;
            if (confKey == null) {
                errmsg = getInvalidValueMessage("yarn.resourcemanager.ha.id", getRMHAId(conf));
            }
            else {
                errmsg = getNeedToSetValueMessage(confKey);
            }
            throwBadConfigurationException(errmsg);
        }
    }
    
    public static void verifyAndSetAllServiceAddresses(final Configuration conf) {
        for (final String confKey : YarnConfiguration.getServiceAddressConfKeys(conf)) {
            verifyAndSetConfValue(confKey, conf);
        }
    }
    
    public static Collection<String> getRMHAIds(final Configuration conf) {
        return conf.getStringCollection("yarn.resourcemanager.ha.rm-ids");
    }
    
    public static String getRMHAId(final Configuration conf) {
        int found = 0;
        String currentRMId = conf.getTrimmed("yarn.resourcemanager.ha.id");
        if (currentRMId == null) {
            for (final String rmId : getRMHAIds(conf)) {
                final String key = addSuffix("yarn.resourcemanager.address", rmId);
                final String addr = conf.get(key);
                if (addr == null) {
                    continue;
                }
                InetSocketAddress s;
                try {
                    s = NetUtils.createSocketAddr(addr);
                }
                catch (Exception e) {
                    HAUtil.LOG.warn("Exception in creating socket address " + addr, e);
                    continue;
                }
                if (s.isUnresolved() || !NetUtils.isLocalAddress(s.getAddress())) {
                    continue;
                }
                currentRMId = rmId.trim();
                ++found;
            }
        }
        if (found > 1) {
            final String msg = "The HA Configuration has multiple addresses that match local node's address.";
            throw new HadoopIllegalArgumentException(msg);
        }
        return currentRMId;
    }
    
    @VisibleForTesting
    static String getNeedToSetValueMessage(final String confKey) {
        return confKey + " needs to be set in a HA configuration.";
    }
    
    @VisibleForTesting
    static String getInvalidValueMessage(final String confKey, final String invalidValue) {
        return "Invalid value of " + confKey + ". " + "Current value is " + invalidValue;
    }
    
    @VisibleForTesting
    static String getRMHAIdNeedToBeIncludedMessage(final String ids, final String rmId) {
        return "yarn.resourcemanager.ha.rm-ids(" + ids + ") need to contain " + "yarn.resourcemanager.ha.id" + "(" + rmId + ") in a HA configuration.";
    }
    
    @VisibleForTesting
    static String getRMHAIdsWarningMessage(final String ids) {
        return "Resource Manager HA is enabled, but yarn.resourcemanager.ha.rm-ids has only one id(" + ids.toString() + ")";
    }
    
    @InterfaceAudience.Private
    @VisibleForTesting
    static String getConfKeyForRMInstance(final String prefix, final Configuration conf) {
        if (!YarnConfiguration.getServiceAddressConfKeys(conf).contains(prefix)) {
            return prefix;
        }
        final String RMId = getRMHAId(conf);
        checkAndSetRMRPCAddress(prefix, RMId, conf);
        return addSuffix(prefix, RMId);
    }
    
    public static String getConfValueForRMInstance(final String prefix, final Configuration conf) {
        final String confKey = getConfKeyForRMInstance(prefix, conf);
        final String retVal = conf.getTrimmed(confKey);
        if (HAUtil.LOG.isTraceEnabled()) {
            HAUtil.LOG.trace("getConfValueForRMInstance: prefix = " + prefix + "; confKey being looked up = " + confKey + "; value being set to = " + retVal);
        }
        return retVal;
    }
    
    public static String getConfValueForRMInstance(final String prefix, final String defaultValue, final Configuration conf) {
        final String value = getConfValueForRMInstance(prefix, conf);
        return (value == null) ? defaultValue : value;
    }
    
    public static String addSuffix(final String key, final String suffix) {
        if (suffix == null || suffix.isEmpty()) {
            return key;
        }
        if (suffix.startsWith(".")) {
            throw new IllegalArgumentException("suffix '" + suffix + "' should not " + "already have '.' prepended.");
        }
        return key + "." + suffix;
    }
    
    private static void checkAndSetRMRPCAddress(final String prefix, final String RMId, final Configuration conf) {
        String rpcAddressConfKey = null;
        try {
            rpcAddressConfKey = addSuffix(prefix, RMId);
            if (conf.getTrimmed(rpcAddressConfKey) == null) {
                final String hostNameConfKey = addSuffix("yarn.resourcemanager.hostname", RMId);
                final String confVal = conf.getTrimmed(hostNameConfKey);
                if (confVal == null) {
                    throwBadConfigurationException(getNeedToSetValueMessage(hostNameConfKey + " or " + addSuffix(prefix, RMId)));
                }
                else {
                    conf.set(addSuffix(prefix, RMId), confVal + ":" + YarnConfiguration.getRMDefaultPortNumber(prefix, conf));
                }
            }
        }
        catch (IllegalArgumentException iae) {
            String errmsg = iae.getMessage();
            if (rpcAddressConfKey == null) {
                errmsg = getInvalidValueMessage("yarn.resourcemanager.ha.id", RMId);
            }
            throwBadConfigurationException(errmsg);
        }
    }
    
    static {
        HAUtil.LOG = LogFactory.getLog(HAUtil.class);
    }
}
