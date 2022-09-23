// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.security;

import java.net.InetAddress;
import org.apache.hadoop.util.StringUtils;
import java.util.TreeMap;
import org.apache.hadoop.util.ReflectionUtils;
import org.apache.hadoop.conf.Configuration;
import java.util.Map;
import org.apache.hadoop.conf.Configurable;

public class SaslPropertiesResolver implements Configurable
{
    private Map<String, String> properties;
    Configuration conf;
    
    public static SaslPropertiesResolver getInstance(final Configuration conf) {
        final Class<? extends SaslPropertiesResolver> clazz = conf.getClass("hadoop.security.saslproperties.resolver.class", SaslPropertiesResolver.class, SaslPropertiesResolver.class);
        return ReflectionUtils.newInstance(clazz, conf);
    }
    
    @Override
    public void setConf(final Configuration conf) {
        this.conf = conf;
        this.properties = new TreeMap<String, String>();
        final String[] qop = conf.getTrimmedStrings("hadoop.rpc.protection", SaslRpcServer.QualityOfProtection.AUTHENTICATION.toString());
        for (int i = 0; i < qop.length; ++i) {
            qop[i] = SaslRpcServer.QualityOfProtection.valueOf(StringUtils.toUpperCase(qop[i])).getSaslQop();
        }
        this.properties.put("javax.security.sasl.qop", StringUtils.join(",", qop));
        this.properties.put("javax.security.sasl.server.authentication", "true");
    }
    
    @Override
    public Configuration getConf() {
        return this.conf;
    }
    
    public Map<String, String> getDefaultProperties() {
        return this.properties;
    }
    
    public Map<String, String> getServerProperties(final InetAddress clientAddress) {
        return this.properties;
    }
    
    public Map<String, String> getServerProperties(final InetAddress clientAddress, final int ingressPort) {
        return this.properties;
    }
    
    public Map<String, String> getClientProperties(final InetAddress serverAddress) {
        return this.properties;
    }
    
    public Map<String, String> getClientProperties(final InetAddress serverAddress, final int ingressPort) {
        return this.properties;
    }
    
    static Map<String, String> getSaslProperties(final Configuration conf, final String configKey, final SaslRpcServer.QualityOfProtection defaultQOP) {
        final Map<String, String> saslProps = new TreeMap<String, String>();
        final String[] qop = conf.getStrings(configKey, defaultQOP.toString());
        for (int i = 0; i < qop.length; ++i) {
            qop[i] = SaslRpcServer.QualityOfProtection.valueOf(StringUtils.toUpperCase(qop[i])).getSaslQop();
        }
        saslProps.put("javax.security.sasl.qop", StringUtils.join(",", qop));
        saslProps.put("javax.security.sasl.server.authentication", "true");
        return saslProps;
    }
}
