// 
// Decompiled by Procyon v0.5.36
// 

package javax.security.auth.message;

public class AuthStatus
{
    public static final AuthStatus FAILURE;
    public static final AuthStatus SEND_CONTINUE;
    public static final AuthStatus SEND_FAILURE;
    public static final AuthStatus SEND_SUCCESS;
    public static final AuthStatus SUCCESS;
    private final String name;
    
    private AuthStatus(final String name) {
        this.name = name;
    }
    
    public String toString() {
        return this.name;
    }
    
    static {
        FAILURE = new AuthStatus("FAILURE");
        SEND_CONTINUE = new AuthStatus("SEND_CONTINUE");
        SEND_FAILURE = new AuthStatus("SEND_FAILURE");
        SEND_SUCCESS = new AuthStatus("SEND_SUCCESS");
        SUCCESS = new AuthStatus("SUCCESS");
    }
}
