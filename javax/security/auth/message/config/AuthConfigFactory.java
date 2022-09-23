// 
// Decompiled by Procyon v0.5.36
// 

package javax.security.auth.message.config;

import java.util.Map;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;
import java.security.AccessController;
import java.security.Security;
import java.security.PrivilegedAction;
import java.security.Permission;
import javax.security.auth.AuthPermission;

public abstract class AuthConfigFactory
{
    public static final String DEFAULT_FACTORY_SECURITY_PROPERTY = "authconfigprovider.factory";
    private static final String DEFAULT_JASPI_AUTHCONFIGFACTORYIMPL = "org.apache.geronimo.components.jaspi.AuthConfigFactoryImpl";
    private static AuthConfigFactory factory;
    private static ClassLoader contextClassLoader;
    
    public static AuthConfigFactory getFactory() {
        final SecurityManager sm = System.getSecurityManager();
        if (sm != null) {
            sm.checkPermission(new AuthPermission("getAuthConfigFactory"));
        }
        if (AuthConfigFactory.factory == null) {
            String className = AccessController.doPrivileged((PrivilegedAction<String>)new PrivilegedAction() {
                public Object run() {
                    return Security.getProperty("authconfigprovider.factory");
                }
            });
            if (className == null) {
                className = "org.apache.geronimo.components.jaspi.AuthConfigFactoryImpl";
            }
            try {
                final String finalClassName = className;
                AuthConfigFactory.factory = AccessController.doPrivileged((PrivilegedExceptionAction<AuthConfigFactory>)new PrivilegedExceptionAction() {
                    public Object run() throws ClassNotFoundException, InstantiationException, IllegalAccessException {
                        return Class.forName(finalClassName, true, AuthConfigFactory.contextClassLoader).newInstance();
                    }
                });
            }
            catch (PrivilegedActionException e) {
                final Exception inner = e.getException();
                if (inner instanceof InstantiationException) {
                    throw (SecurityException)new SecurityException("AuthConfigFactory error:" + inner.getCause().getMessage()).initCause(inner.getCause());
                }
                throw (SecurityException)new SecurityException("AuthConfigFactory error: " + inner).initCause(inner);
            }
        }
        return AuthConfigFactory.factory;
    }
    
    public static void setFactory(final AuthConfigFactory factory) {
        final SecurityManager sm = System.getSecurityManager();
        if (sm != null) {
            sm.checkPermission(new AuthPermission("setAuthConfigFactory"));
        }
        AuthConfigFactory.factory = factory;
    }
    
    public abstract String[] detachListener(final RegistrationListener p0, final String p1, final String p2);
    
    public abstract AuthConfigProvider getConfigProvider(final String p0, final String p1, final RegistrationListener p2);
    
    public abstract RegistrationContext getRegistrationContext(final String p0);
    
    public abstract String[] getRegistrationIDs(final AuthConfigProvider p0);
    
    public abstract void refresh();
    
    public abstract String registerConfigProvider(final AuthConfigProvider p0, final String p1, final String p2, final String p3);
    
    public abstract String registerConfigProvider(final String p0, final Map p1, final String p2, final String p3, final String p4);
    
    public abstract boolean removeRegistration(final String p0);
    
    static {
        AuthConfigFactory.contextClassLoader = AccessController.doPrivileged((PrivilegedAction<ClassLoader>)new PrivilegedAction() {
            public Object run() {
                return Thread.currentThread().getContextClassLoader();
            }
        });
    }
    
    public interface RegistrationContext
    {
        String getAppContext();
        
        String getDescription();
        
        String getMessageLayer();
        
        boolean isPersistent();
    }
}
