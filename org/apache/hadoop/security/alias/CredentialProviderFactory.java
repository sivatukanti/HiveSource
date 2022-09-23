// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.security.alias;

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
public abstract class CredentialProviderFactory
{
    public static final String CREDENTIAL_PROVIDER_PATH = "hadoop.security.credential.provider.path";
    private static final ServiceLoader<CredentialProviderFactory> serviceLoader;
    
    public abstract CredentialProvider createProvider(final URI p0, final Configuration p1) throws IOException;
    
    public static List<CredentialProvider> getProviders(final Configuration conf) throws IOException {
        final List<CredentialProvider> result = new ArrayList<CredentialProvider>();
        for (final String path : conf.getStringCollection("hadoop.security.credential.provider.path")) {
            try {
                final URI uri = new URI(path);
                boolean found = false;
                synchronized (CredentialProviderFactory.serviceLoader) {
                    for (final CredentialProviderFactory factory : CredentialProviderFactory.serviceLoader) {
                        final CredentialProvider kp = factory.createProvider(uri, conf);
                        if (kp != null) {
                            result.add(kp);
                            found = true;
                            break;
                        }
                    }
                }
                if (!found) {
                    throw new IOException("No CredentialProviderFactory for " + uri + " in " + "hadoop.security.credential.provider.path");
                }
                continue;
            }
            catch (URISyntaxException error) {
                throw new IOException("Bad configuration of hadoop.security.credential.provider.path at " + path, error);
            }
        }
        return result;
    }
    
    static {
        serviceLoader = ServiceLoader.load(CredentialProviderFactory.class, CredentialProviderFactory.class.getClassLoader());
        final Iterator<CredentialProviderFactory> iterServices = CredentialProviderFactory.serviceLoader.iterator();
        while (iterServices.hasNext()) {
            iterServices.next();
        }
    }
}
