// 
// Decompiled by Procyon v0.5.36
// 

package javax.ws.rs.ext;

import javax.ws.rs.core.Application;
import javax.ws.rs.core.Variant;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;
import java.security.Permission;
import java.net.URL;
import java.lang.reflect.ReflectPermission;

public abstract class RuntimeDelegate
{
    public static final String JAXRS_RUNTIME_DELEGATE_PROPERTY = "javax.ws.rs.ext.RuntimeDelegate";
    private static final String JAXRS_DEFAULT_RUNTIME_DELEGATE = "com.sun.ws.rs.ext.RuntimeDelegateImpl";
    private static ReflectPermission rp;
    private static volatile RuntimeDelegate rd;
    
    protected RuntimeDelegate() {
    }
    
    public static RuntimeDelegate getInstance() {
        RuntimeDelegate result = RuntimeDelegate.rd;
        if (result == null) {
            synchronized (RuntimeDelegate.class) {
                result = RuntimeDelegate.rd;
                if (result == null) {
                    result = (RuntimeDelegate.rd = findDelegate());
                }
            }
        }
        return result;
    }
    
    private static RuntimeDelegate findDelegate() {
        try {
            final Object delegate = FactoryFinder.find("javax.ws.rs.ext.RuntimeDelegate", "com.sun.ws.rs.ext.RuntimeDelegateImpl");
            if (!(delegate instanceof RuntimeDelegate)) {
                final Class pClass = RuntimeDelegate.class;
                final String classnameAsResource = pClass.getName().replace('.', '/') + ".class";
                ClassLoader loader = pClass.getClassLoader();
                if (loader == null) {
                    loader = ClassLoader.getSystemClassLoader();
                }
                final URL targetTypeURL = loader.getResource(classnameAsResource);
                throw new LinkageError("ClassCastException: attempting to cast" + delegate.getClass().getClassLoader().getResource(classnameAsResource) + "to" + targetTypeURL.toString());
            }
            return (RuntimeDelegate)delegate;
        }
        catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }
    
    public static void setInstance(final RuntimeDelegate rd) throws SecurityException {
        final SecurityManager security = System.getSecurityManager();
        if (security != null) {
            security.checkPermission(RuntimeDelegate.rp);
        }
        synchronized (RuntimeDelegate.class) {
            RuntimeDelegate.rd = rd;
        }
    }
    
    public abstract UriBuilder createUriBuilder();
    
    public abstract Response.ResponseBuilder createResponseBuilder();
    
    public abstract Variant.VariantListBuilder createVariantListBuilder();
    
    public abstract <T> T createEndpoint(final Application p0, final Class<T> p1) throws IllegalArgumentException, UnsupportedOperationException;
    
    public abstract <T> HeaderDelegate<T> createHeaderDelegate(final Class<T> p0);
    
    static {
        RuntimeDelegate.rp = new ReflectPermission("suppressAccessChecks");
    }
    
    public interface HeaderDelegate<T>
    {
        T fromString(final String p0) throws IllegalArgumentException;
        
        String toString(final T p0);
    }
}
