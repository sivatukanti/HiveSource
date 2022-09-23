// 
// Decompiled by Procyon v0.5.36
// 

package net.sf.jpam;

import org.apache.commons.logging.LogFactory;
import org.apache.commons.logging.Log;

public class Pam
{
    private static final Log LOG;
    private static final String JPAM_SHARED_LIBRARY_NAME = "jpam";
    private String serviceName;
    public static final String DEFAULT_SERVICE_NAME = "net-sf-jpam";
    static /* synthetic */ Class class$net$sf$jpam$Pam;
    
    public Pam() {
        this("net-sf-jpam");
    }
    
    public Pam(final String serviceName) throws NullPointerException, IllegalArgumentException {
        if (serviceName == null) {
            throw new NullPointerException("Service name is null");
        }
        if (serviceName.length() == 0) {
            throw new IllegalArgumentException("Service name is empty");
        }
        this.serviceName = serviceName;
    }
    
    native boolean isSharedLibraryWorking();
    
    private void callback() {
    }
    
    public boolean authenticateSuccessful(final String username, final String credentials) {
        final PamReturnValue success = PamReturnValue.PAM_SUCCESS;
        final PamReturnValue actual = this.authenticate(username, credentials);
        return actual.equals(success);
    }
    
    public PamReturnValue authenticate(final String username, final String credentials) throws NullPointerException {
        final boolean debug = Pam.LOG.isDebugEnabled();
        Pam.LOG.debug("Debug mode active.");
        if (this.serviceName == null) {
            throw new NullPointerException("Service name is null");
        }
        if (username == null) {
            throw new NullPointerException("User name is null");
        }
        if (credentials == null) {
            throw new NullPointerException("Credentials are null");
        }
        Class class$;
        Class class$net$sf$jpam$Pam;
        if (Pam.class$net$sf$jpam$Pam == null) {
            class$net$sf$jpam$Pam = (Pam.class$net$sf$jpam$Pam = (class$ = class$("net.sf.jpam.Pam")));
        }
        else {
            class$ = (class$net$sf$jpam$Pam = Pam.class$net$sf$jpam$Pam);
        }
        final Class clazz = class$net$sf$jpam$Pam;
        synchronized (class$) {
            final PamReturnValue pamReturnValue = PamReturnValue.fromId(this.authenticate(this.serviceName, username, credentials, debug));
            return pamReturnValue;
        }
    }
    
    public static void main(final String[] args) {
        final Pam pam = new Pam();
        final PamReturnValue pamReturnValue = pam.authenticate(args[0], args[1]);
        System.out.println("Response: " + pamReturnValue);
    }
    
    private native int authenticate(final String p0, final String p1, final String p2, final boolean p3);
    
    public static String getLibraryName() {
        return System.mapLibraryName("jpam");
    }
    
    public String getServiceName() {
        return this.serviceName;
    }
    
    static /* synthetic */ Class class$(final String x0) {
        try {
            return Class.forName(x0);
        }
        catch (ClassNotFoundException x) {
            throw new NoClassDefFoundError(x.getMessage());
        }
    }
    
    static {
        LOG = LogFactory.getLog(Pam.class.getName());
        System.loadLibrary("jpam");
    }
}
