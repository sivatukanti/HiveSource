// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.security;

import org.slf4j.LoggerFactory;
import com.google.common.annotations.VisibleForTesting;
import java.net.InetAddress;
import java.util.Iterator;
import java.util.Collection;
import org.apache.hadoop.conf.Configuration;
import java.util.Map;
import java.util.HashMap;
import org.slf4j.Logger;

public class IngressPortBasedResolver extends SaslPropertiesResolver
{
    public static final Logger LOG;
    static final String INGRESS_PORT_SASL_PROP_PREFIX = "ingress.port.sasl.prop";
    static final String INGRESS_PORT_SASL_CONFIGURED_PORTS = "ingress.port.sasl.configured.ports";
    private HashMap<Integer, Map<String, String>> portPropMapping;
    
    @Override
    public void setConf(final Configuration conf) {
        super.setConf(conf);
        this.portPropMapping = new HashMap<Integer, Map<String, String>>();
        final Collection<String> portStrings = conf.getTrimmedStringCollection("ingress.port.sasl.configured.ports");
        for (final String portString : portStrings) {
            final int port = Integer.parseInt(portString);
            final String configKey = "ingress.port.sasl.prop." + portString;
            final Map<String, String> props = SaslPropertiesResolver.getSaslProperties(conf, configKey, SaslRpcServer.QualityOfProtection.PRIVACY);
            this.portPropMapping.put(port, props);
        }
        IngressPortBasedResolver.LOG.debug("Configured with port to QOP mapping as:" + this.portPropMapping);
    }
    
    @VisibleForTesting
    @Override
    public Map<String, String> getServerProperties(final InetAddress clientAddress, final int ingressPort) {
        IngressPortBasedResolver.LOG.debug("Resolving SASL properties for " + clientAddress + " " + ingressPort);
        if (!this.portPropMapping.containsKey(ingressPort)) {
            IngressPortBasedResolver.LOG.warn("An un-configured port is being requested " + ingressPort + " using default");
            return this.getDefaultProperties();
        }
        return this.portPropMapping.get(ingressPort);
    }
    
    static {
        LOG = LoggerFactory.getLogger(IngressPortBasedResolver.class.getName());
    }
}
