// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.security;

import org.slf4j.LoggerFactory;
import java.io.IOException;
import org.apache.hadoop.security.authentication.util.KerberosUtil;
import org.apache.hadoop.conf.Configuration;
import org.slf4j.Logger;
import org.apache.hadoop.classification.InterfaceStability;
import org.apache.hadoop.classification.InterfaceAudience;
import org.apache.hadoop.security.authentication.util.KerberosName;

@InterfaceAudience.LimitedPrivate({ "HDFS", "MapReduce" })
@InterfaceStability.Evolving
public class HadoopKerberosName extends KerberosName
{
    private static final Logger LOG;
    
    public HadoopKerberosName(final String name) {
        super(name);
    }
    
    public static void setConfiguration(final Configuration conf) throws IOException {
        String defaultRule = null;
        switch (SecurityUtil.getAuthenticationMethod(conf)) {
            case KERBEROS:
            case KERBEROS_SSL: {
                try {
                    KerberosUtil.getDefaultRealm();
                }
                catch (Exception ke) {
                    throw new IllegalArgumentException("Can't get Kerberos realm", ke);
                }
                defaultRule = "DEFAULT";
                break;
            }
            default: {
                defaultRule = "RULE:[1:$1] RULE:[2:$1]";
                break;
            }
        }
        final String ruleString = conf.get("hadoop.security.auth_to_local", defaultRule);
        KerberosName.setRules(ruleString);
        final String ruleMechanism = conf.get("hadoop.security.auth_to_local.mechanism", "hadoop");
        KerberosName.setRuleMechanism(ruleMechanism);
    }
    
    public static void main(final String[] args) throws Exception {
        setConfiguration(new Configuration());
        for (final String arg : args) {
            final HadoopKerberosName name = new HadoopKerberosName(arg);
            System.out.println("Name: " + name + " to " + name.getShortName());
        }
    }
    
    static {
        LOG = LoggerFactory.getLogger(HadoopKerberosName.class);
    }
}
