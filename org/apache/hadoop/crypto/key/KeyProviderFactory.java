// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.crypto.key;

import java.util.Iterator;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.io.IOException;
import org.apache.hadoop.conf.Configuration;
import java.net.URI;
import java.util.ServiceLoader;
import org.apache.hadoop.classification.InterfaceStability;
import org.apache.hadoop.classification.InterfaceAudience;

@InterfaceAudience.Public
@InterfaceStability.Unstable
public abstract class KeyProviderFactory
{
    public static final String KEY_PROVIDER_PATH = "hadoop.security.key.provider.path";
    private static final ServiceLoader<KeyProviderFactory> serviceLoader;
    
    public abstract KeyProvider createProvider(final URI p0, final Configuration p1) throws IOException;
    
    public static List<KeyProvider> getProviders(final Configuration conf) throws IOException {
        final List<KeyProvider> result = new ArrayList<KeyProvider>();
        for (final String path : conf.getStringCollection("hadoop.security.key.provider.path")) {
            try {
                final URI uri = new URI(path);
                final KeyProvider kp = get(uri, conf);
                if (kp == null) {
                    throw new IOException("No KeyProviderFactory for " + uri + " in " + "hadoop.security.key.provider.path");
                }
                result.add(kp);
            }
            catch (URISyntaxException error) {
                throw new IOException("Bad configuration of hadoop.security.key.provider.path at " + path, error);
            }
        }
        return result;
    }
    
    public static KeyProvider get(final URI uri, final Configuration conf) throws IOException {
        KeyProvider kp = null;
        for (final KeyProviderFactory factory : KeyProviderFactory.serviceLoader) {
            kp = factory.createProvider(uri, conf);
            if (kp != null) {
                break;
            }
        }
        return kp;
    }
    
    static {
        serviceLoader = ServiceLoader.load(KeyProviderFactory.class, KeyProviderFactory.class.getClassLoader());
        final Iterator<KeyProviderFactory> iterServices = KeyProviderFactory.serviceLoader.iterator();
        while (iterServices.hasNext()) {
            iterServices.next();
        }
    }
}
