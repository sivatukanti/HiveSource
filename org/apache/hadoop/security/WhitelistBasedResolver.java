// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.security;

import org.slf4j.LoggerFactory;
import java.net.UnknownHostException;
import java.net.InetAddress;
import org.apache.hadoop.conf.Configuration;
import java.util.Map;
import org.apache.hadoop.util.CombinedIPWhiteList;
import org.slf4j.Logger;

public class WhitelistBasedResolver extends SaslPropertiesResolver
{
    public static final Logger LOG;
    private static final String FIXEDWHITELIST_DEFAULT_LOCATION = "/etc/hadoop/fixedwhitelist";
    private static final String VARIABLEWHITELIST_DEFAULT_LOCATION = "/etc/hadoop/whitelist";
    public static final String HADOOP_SECURITY_SASL_FIXEDWHITELIST_FILE = "hadoop.security.sasl.fixedwhitelist.file";
    public static final String HADOOP_SECURITY_SASL_VARIABLEWHITELIST_ENABLE = "hadoop.security.sasl.variablewhitelist.enable";
    public static final String HADOOP_SECURITY_SASL_VARIABLEWHITELIST_FILE = "hadoop.security.sasl.variablewhitelist.file";
    public static final String HADOOP_SECURITY_SASL_VARIABLEWHITELIST_CACHE_SECS = "hadoop.security.sasl.variablewhitelist.cache.secs";
    public static final String HADOOP_RPC_PROTECTION_NON_WHITELIST = "hadoop.rpc.protection.non-whitelist";
    private CombinedIPWhiteList whiteList;
    private Map<String, String> saslProps;
    
    @Override
    public void setConf(final Configuration conf) {
        super.setConf(conf);
        final String fixedFile = conf.get("hadoop.security.sasl.fixedwhitelist.file", "/etc/hadoop/fixedwhitelist");
        String variableFile = null;
        long expiryTime = 0L;
        if (conf.getBoolean("hadoop.security.sasl.variablewhitelist.enable", false)) {
            variableFile = conf.get("hadoop.security.sasl.variablewhitelist.file", "/etc/hadoop/whitelist");
            expiryTime = conf.getLong("hadoop.security.sasl.variablewhitelist.cache.secs", 3600L) * 1000L;
        }
        this.whiteList = new CombinedIPWhiteList(fixedFile, variableFile, expiryTime);
        this.saslProps = getSaslProperties(conf);
    }
    
    @Override
    public Map<String, String> getServerProperties(final InetAddress clientAddress) {
        if (clientAddress == null) {
            return this.saslProps;
        }
        return this.whiteList.isIn(clientAddress.getHostAddress()) ? this.getDefaultProperties() : this.saslProps;
    }
    
    public Map<String, String> getServerProperties(final String clientAddress) throws UnknownHostException {
        if (clientAddress == null) {
            return this.saslProps;
        }
        return this.getServerProperties(InetAddress.getByName(clientAddress));
    }
    
    static Map<String, String> getSaslProperties(final Configuration conf) {
        return SaslPropertiesResolver.getSaslProperties(conf, "hadoop.rpc.protection.non-whitelist", SaslRpcServer.QualityOfProtection.PRIVACY);
    }
    
    static {
        LOG = LoggerFactory.getLogger(WhitelistBasedResolver.class);
    }
}
