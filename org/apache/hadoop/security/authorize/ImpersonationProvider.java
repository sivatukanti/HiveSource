// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.security.authorize;

import org.apache.hadoop.security.UserGroupInformation;
import org.apache.hadoop.classification.InterfaceAudience;
import org.apache.hadoop.classification.InterfaceStability;
import org.apache.hadoop.conf.Configurable;

@InterfaceStability.Unstable
@InterfaceAudience.Public
public interface ImpersonationProvider extends Configurable
{
    void init(final String p0);
    
    void authorize(final UserGroupInformation p0, final String p1) throws AuthorizationException;
}
