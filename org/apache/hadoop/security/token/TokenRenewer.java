// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.security.token;

import org.apache.hadoop.conf.Configuration;
import java.io.IOException;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.classification.InterfaceStability;
import org.apache.hadoop.classification.InterfaceAudience;

@InterfaceAudience.Public
@InterfaceStability.Evolving
public abstract class TokenRenewer
{
    public abstract boolean handleKind(final Text p0);
    
    public abstract boolean isManaged(final Token<?> p0) throws IOException;
    
    public abstract long renew(final Token<?> p0, final Configuration p1) throws IOException, InterruptedException;
    
    public abstract void cancel(final Token<?> p0, final Configuration p1) throws IOException, InterruptedException;
}
