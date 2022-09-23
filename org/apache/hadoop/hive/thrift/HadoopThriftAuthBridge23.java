// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.hive.thrift;

import org.apache.hadoop.conf.Configurable;
import org.apache.hadoop.security.SaslRpcServer;
import java.util.Map;
import org.apache.hadoop.conf.Configuration;
import java.lang.reflect.Method;
import java.lang.reflect.Field;

public class HadoopThriftAuthBridge23 extends HadoopThriftAuthBridge
{
    private static Field SASL_PROPS_FIELD;
    private static Class<?> SASL_PROPERTIES_RESOLVER_CLASS;
    private static Method RES_GET_INSTANCE_METHOD;
    private static Method GET_DEFAULT_PROP_METHOD;
    
    @Override
    public Map<String, String> getHadoopSaslProperties(final Configuration conf) {
        if (HadoopThriftAuthBridge23.SASL_PROPS_FIELD != null) {
            SaslRpcServer.init(conf);
            try {
                return (Map<String, String>)HadoopThriftAuthBridge23.SASL_PROPS_FIELD.get(null);
            }
            catch (Exception e) {
                throw new IllegalStateException("Error finding hadoop SASL properties", e);
            }
        }
        try {
            final Configurable saslPropertiesResolver = (Configurable)HadoopThriftAuthBridge23.RES_GET_INSTANCE_METHOD.invoke(null, conf);
            saslPropertiesResolver.setConf(conf);
            return (Map<String, String>)HadoopThriftAuthBridge23.GET_DEFAULT_PROP_METHOD.invoke(saslPropertiesResolver, new Object[0]);
        }
        catch (Exception e) {
            throw new IllegalStateException("Error finding hadoop SASL properties", e);
        }
    }
    
    static {
        HadoopThriftAuthBridge23.SASL_PROPERTIES_RESOLVER_CLASS = null;
        HadoopThriftAuthBridge23.SASL_PROPS_FIELD = null;
        final String SASL_PROP_RES_CLASSNAME = "org.apache.hadoop.security.SaslPropertiesResolver";
        try {
            HadoopThriftAuthBridge23.SASL_PROPERTIES_RESOLVER_CLASS = Class.forName("org.apache.hadoop.security.SaslPropertiesResolver");
        }
        catch (ClassNotFoundException ex) {}
        if (HadoopThriftAuthBridge23.SASL_PROPERTIES_RESOLVER_CLASS != null) {
            try {
                HadoopThriftAuthBridge23.RES_GET_INSTANCE_METHOD = HadoopThriftAuthBridge23.SASL_PROPERTIES_RESOLVER_CLASS.getMethod("getInstance", Configuration.class);
                HadoopThriftAuthBridge23.GET_DEFAULT_PROP_METHOD = HadoopThriftAuthBridge23.SASL_PROPERTIES_RESOLVER_CLASS.getMethod("getDefaultProperties", (Class<?>[])new Class[0]);
            }
            catch (Exception ex2) {}
        }
        if (HadoopThriftAuthBridge23.SASL_PROPERTIES_RESOLVER_CLASS != null) {
            if (HadoopThriftAuthBridge23.GET_DEFAULT_PROP_METHOD != null) {
                return;
            }
        }
        try {
            HadoopThriftAuthBridge23.SASL_PROPS_FIELD = SaslRpcServer.class.getField("SASL_PROPS");
        }
        catch (NoSuchFieldException e) {
            throw new IllegalStateException("Error finding hadoop SASL_PROPS field in " + SaslRpcServer.class.getSimpleName(), e);
        }
    }
}
