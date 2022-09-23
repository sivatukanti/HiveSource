// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.yarn.security.client;

import org.apache.hadoop.classification.InterfaceStability;
import org.apache.hadoop.classification.InterfaceAudience;

@InterfaceAudience.Private
@InterfaceStability.Unstable
public class TimelineAuthenticationConsts
{
    public static final String ERROR_EXCEPTION_JSON = "exception";
    public static final String ERROR_CLASSNAME_JSON = "javaClassName";
    public static final String ERROR_MESSAGE_JSON = "message";
    public static final String OP_PARAM = "op";
    public static final String DELEGATION_PARAM = "delegation";
    public static final String TOKEN_PARAM = "token";
    public static final String RENEWER_PARAM = "renewer";
    public static final String DELEGATION_TOKEN_URL = "url";
    public static final String DELEGATION_TOKEN_EXPIRATION_TIME = "expirationTime";
}
