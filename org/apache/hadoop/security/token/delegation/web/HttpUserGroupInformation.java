// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.security.token.delegation.web;

import org.apache.hadoop.security.UserGroupInformation;
import org.apache.hadoop.classification.InterfaceAudience;

@InterfaceAudience.Private
public class HttpUserGroupInformation
{
    public static UserGroupInformation get() {
        return DelegationTokenAuthenticationFilter.getHttpUserGroupInformationInContext();
    }
}
