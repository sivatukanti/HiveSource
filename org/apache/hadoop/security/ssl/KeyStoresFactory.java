// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.security.ssl;

import javax.net.ssl.TrustManager;
import javax.net.ssl.KeyManager;
import java.security.GeneralSecurityException;
import java.io.IOException;
import org.apache.hadoop.classification.InterfaceStability;
import org.apache.hadoop.classification.InterfaceAudience;
import org.apache.hadoop.conf.Configurable;

@InterfaceAudience.Private
@InterfaceStability.Evolving
public interface KeyStoresFactory extends Configurable
{
    void init(final SSLFactory.Mode p0) throws IOException, GeneralSecurityException;
    
    void destroy();
    
    KeyManager[] getKeyManagers();
    
    TrustManager[] getTrustManagers();
}
