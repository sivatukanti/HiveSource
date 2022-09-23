// 
// Decompiled by Procyon v0.5.36
// 

package net.sf.jpam;

import java.util.Collections;
import java.util.Arrays;
import java.util.List;

public class PamReturnValue
{
    public static final PamReturnValue PAM_SUCCESS;
    public static final PamReturnValue PAM_OPEN_ERR;
    public static final PamReturnValue PAM_SYMBOL_ERR;
    public static final PamReturnValue PAM_SERVICE_ERR;
    public static final PamReturnValue PAM_SYSTEM_ERR;
    public static final PamReturnValue PAM_BUF_ERR;
    public static final PamReturnValue PAM_PERM_DENIED;
    public static final PamReturnValue PAM_AUTH_ERR;
    public static final PamReturnValue PAM_CRED_INSUFFICIENT;
    public static final PamReturnValue PAM_AUTHINFO_UNAVAIL;
    public static final PamReturnValue PAM_USER_UNKNOWN;
    public static final PamReturnValue PAM_MAXTRIES;
    public static final PamReturnValue PAM_NEW_AUTHTOK_REQD;
    public static final PamReturnValue PAM_ACCT_EXPIRED;
    public static final PamReturnValue PAM_SESSION_ERR;
    public static final PamReturnValue PAM_CRED_UNAVAIL;
    public static final PamReturnValue PAM_CRED_EXPIRED;
    public static final PamReturnValue PAM_CRED_ERR;
    public static final PamReturnValue PAM_NO_MODULE_DATA;
    public static final PamReturnValue PAM_CONV_ERR;
    public static final PamReturnValue PAM_AUTHTOK_ERR;
    public static final PamReturnValue PAM_AUTHTOK_RECOVER_ERR;
    public static final PamReturnValue PAM_AUTHTOK_LOCK_BUSY;
    public static final PamReturnValue PAM_AUTHTOK_DISABLE_AGING;
    public static final PamReturnValue PAM_TRY_AGAIN;
    public static final PamReturnValue PAM_IGNORE;
    public static final PamReturnValue PAM_ABORT;
    public static final PamReturnValue PAM_AUTHTOK_EXPIRED;
    public static final PamReturnValue PAM_MODULE_UNKNOWN;
    public static final PamReturnValue PAM_BAD_ITEM;
    public static final PamReturnValue PAM_CONV_AGAIN;
    public static final PamReturnValue PAM_INCOMPLETE;
    private static final PamReturnValue[] PRIVATE_VALUES;
    private final String description;
    private final int id;
    public static final List VALUES;
    
    private PamReturnValue(final int id, final String description) {
        this.id = id;
        this.description = description;
    }
    
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof PamReturnValue)) {
            return false;
        }
        final PamReturnValue pamReturnValue = (PamReturnValue)o;
        return this.id == pamReturnValue.id;
    }
    
    public static PamReturnValue fromId(final int id) throws IllegalArgumentException {
        final int maxId = PamReturnValue.VALUES.size() - 1;
        if (id > maxId || id < 0) {
            throw new IllegalArgumentException("id " + id + " is not between 0 and " + maxId);
        }
        return PamReturnValue.VALUES.get(id);
    }
    
    public int hashCode() {
        return this.id;
    }
    
    public String toString() {
        return this.description;
    }
    
    static {
        PAM_SUCCESS = new PamReturnValue(0, "Successful function return.");
        PAM_OPEN_ERR = new PamReturnValue(1, "dlopen() failure when dynamically loading a service module.");
        PAM_SYMBOL_ERR = new PamReturnValue(2, "Symbol not found.");
        PAM_SERVICE_ERR = new PamReturnValue(3, "Error in service module.");
        PAM_SYSTEM_ERR = new PamReturnValue(4, "System error.");
        PAM_BUF_ERR = new PamReturnValue(5, "Memory buffer error.");
        PAM_PERM_DENIED = new PamReturnValue(6, "Permission denied.");
        PAM_AUTH_ERR = new PamReturnValue(7, "Authentication failure.");
        PAM_CRED_INSUFFICIENT = new PamReturnValue(8, "Can not access authentication data due to insufficient credentials.");
        PAM_AUTHINFO_UNAVAIL = new PamReturnValue(9, "Underlying authentication service can not retrieve authentication information.");
        PAM_USER_UNKNOWN = new PamReturnValue(10, "User not known to the underlying authentication module.");
        PAM_MAXTRIES = new PamReturnValue(11, "An authentication service has maintained a retry count which has been reached.  No further retries should be attempted.");
        PAM_NEW_AUTHTOK_REQD = new PamReturnValue(12, "New authentication token required. This is normally returned if the machine security policies require that the password should be changed because the password is NULL or it has aged.");
        PAM_ACCT_EXPIRED = new PamReturnValue(13, "User account has expired.");
        PAM_SESSION_ERR = new PamReturnValue(14, "Can not make/remove an entry for the specified session.");
        PAM_CRED_UNAVAIL = new PamReturnValue(15, "Underlying authentication service can not retrieve user credentials unavailable.");
        PAM_CRED_EXPIRED = new PamReturnValue(16, "User credentials expired.");
        PAM_CRED_ERR = new PamReturnValue(17, "Failure setting user credentials.");
        PAM_NO_MODULE_DATA = new PamReturnValue(18, "No module specific data is present.");
        PAM_CONV_ERR = new PamReturnValue(19, "Conversation error.");
        PAM_AUTHTOK_ERR = new PamReturnValue(20, "Authentication token manipulation error.");
        PAM_AUTHTOK_RECOVER_ERR = new PamReturnValue(21, "Authentication information cannot be recovered.");
        PAM_AUTHTOK_LOCK_BUSY = new PamReturnValue(22, "Authentication token lock busy.");
        PAM_AUTHTOK_DISABLE_AGING = new PamReturnValue(23, "Authentication token aging disabled.");
        PAM_TRY_AGAIN = new PamReturnValue(24, "Preliminary check by password service.");
        PAM_IGNORE = new PamReturnValue(25, "Ignore underlying account module regardless of whether the control flagis required, optional, or sufficient.");
        PAM_ABORT = new PamReturnValue(26, "Critical error (?module fail now request).");
        PAM_AUTHTOK_EXPIRED = new PamReturnValue(27, "User's authentication token has expired.");
        PAM_MODULE_UNKNOWN = new PamReturnValue(28, "Module is not known.");
        PAM_BAD_ITEM = new PamReturnValue(29, "Bad item passed to pam_*_item().");
        PAM_CONV_AGAIN = new PamReturnValue(30, "Conversation function is event driven and data is not available yet.");
        PAM_INCOMPLETE = new PamReturnValue(31, "Please call this function again to complete authentication stack. Before calling again, verify that conversation is completed.");
        PRIVATE_VALUES = new PamReturnValue[] { PamReturnValue.PAM_SUCCESS, PamReturnValue.PAM_OPEN_ERR, PamReturnValue.PAM_SYMBOL_ERR, PamReturnValue.PAM_SERVICE_ERR, PamReturnValue.PAM_SYSTEM_ERR, PamReturnValue.PAM_BUF_ERR, PamReturnValue.PAM_PERM_DENIED, PamReturnValue.PAM_AUTH_ERR, PamReturnValue.PAM_CRED_INSUFFICIENT, PamReturnValue.PAM_AUTHINFO_UNAVAIL, PamReturnValue.PAM_USER_UNKNOWN, PamReturnValue.PAM_MAXTRIES, PamReturnValue.PAM_NEW_AUTHTOK_REQD, PamReturnValue.PAM_ACCT_EXPIRED, PamReturnValue.PAM_SESSION_ERR, PamReturnValue.PAM_CRED_UNAVAIL, PamReturnValue.PAM_CRED_EXPIRED, PamReturnValue.PAM_CRED_ERR, PamReturnValue.PAM_NO_MODULE_DATA, PamReturnValue.PAM_CONV_ERR, PamReturnValue.PAM_AUTHTOK_ERR, PamReturnValue.PAM_AUTHTOK_RECOVER_ERR, PamReturnValue.PAM_AUTHTOK_LOCK_BUSY, PamReturnValue.PAM_AUTHTOK_DISABLE_AGING, PamReturnValue.PAM_TRY_AGAIN, PamReturnValue.PAM_IGNORE, PamReturnValue.PAM_ABORT, PamReturnValue.PAM_AUTHTOK_EXPIRED, PamReturnValue.PAM_MODULE_UNKNOWN, PamReturnValue.PAM_BAD_ITEM, PamReturnValue.PAM_CONV_AGAIN, PamReturnValue.PAM_INCOMPLETE };
        VALUES = Collections.unmodifiableList((List<?>)Arrays.asList((T[])PamReturnValue.PRIVATE_VALUES));
    }
}
