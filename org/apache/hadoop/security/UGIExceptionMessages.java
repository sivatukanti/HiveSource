// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.security;

final class UGIExceptionMessages
{
    public static final String FAILURE_TO_LOGIN = "failure to login:";
    public static final String FOR_USER = " for user: ";
    public static final String FOR_PRINCIPAL = " for principal: ";
    public static final String FROM_KEYTAB = " from keytab ";
    public static final String LOGIN_FAILURE = "Login failure";
    public static final String LOGOUT_FAILURE = "Logout failure";
    public static final String MUST_FIRST_LOGIN = "login must be done first";
    public static final String MUST_FIRST_LOGIN_FROM_KEYTAB = "loginUserFromKeyTab must be done first";
    public static final String SUBJECT_MUST_CONTAIN_PRINCIPAL = "Provided Subject must contain a KerberosPrincipal";
    public static final String SUBJECT_MUST_NOT_BE_NULL = "Subject must not be null";
    public static final String USING_TICKET_CACHE_FILE = " using ticket cache file: ";
    
    private UGIExceptionMessages() {
    }
}
