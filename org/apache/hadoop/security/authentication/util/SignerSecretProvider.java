// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.security.authentication.util;

import javax.servlet.ServletContext;
import java.util.Properties;
import org.apache.hadoop.classification.InterfaceAudience;
import org.apache.hadoop.classification.InterfaceStability;

@InterfaceStability.Unstable
@InterfaceAudience.Private
public abstract class SignerSecretProvider
{
    public abstract void init(final Properties p0, final ServletContext p1, final long p2) throws Exception;
    
    public void destroy() {
    }
    
    public abstract byte[] getCurrentSecret();
    
    public abstract byte[][] getAllSecrets();
}
