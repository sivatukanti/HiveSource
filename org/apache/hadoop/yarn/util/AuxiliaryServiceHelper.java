// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.yarn.util;

import org.apache.commons.codec.binary.Base64;
import java.nio.ByteBuffer;
import java.util.Map;

public class AuxiliaryServiceHelper
{
    public static final String NM_AUX_SERVICE = "NM_AUX_SERVICE_";
    
    public static ByteBuffer getServiceDataFromEnv(final String serviceName, final Map<String, String> env) {
        final String meta = env.get(getPrefixServiceName(serviceName));
        if (null == meta) {
            return null;
        }
        final byte[] metaData = Base64.decodeBase64(meta);
        return ByteBuffer.wrap(metaData);
    }
    
    public static void setServiceDataIntoEnv(final String serviceName, final ByteBuffer metaData, final Map<String, String> env) {
        final byte[] byteData = metaData.array();
        env.put(getPrefixServiceName(serviceName), Base64.encodeBase64String(byteData));
    }
    
    private static String getPrefixServiceName(final String serviceName) {
        return "NM_AUX_SERVICE_" + serviceName;
    }
}
