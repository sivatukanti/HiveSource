// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.crypto.key;

import java.net.URI;
import java.io.IOException;
import org.apache.hadoop.classification.InterfaceStability;
import org.apache.hadoop.classification.InterfaceAudience;
import org.apache.hadoop.security.token.DelegationTokenIssuer;

@InterfaceAudience.Private
@InterfaceStability.Unstable
public interface KeyProviderTokenIssuer extends DelegationTokenIssuer
{
    KeyProvider getKeyProvider() throws IOException;
    
    URI getKeyProviderUri() throws IOException;
}
