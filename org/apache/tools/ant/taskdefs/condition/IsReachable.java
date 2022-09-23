// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.tools.ant.taskdefs.condition;

import java.lang.reflect.Method;
import java.lang.reflect.InvocationTargetException;
import java.net.UnknownHostException;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.URL;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.ProjectComponent;

public class IsReachable extends ProjectComponent implements Condition
{
    private static final int SECOND = 1000;
    private String host;
    private String url;
    public static final int DEFAULT_TIMEOUT = 30;
    private int timeout;
    public static final String ERROR_NO_HOSTNAME = "No hostname defined";
    public static final String ERROR_BAD_TIMEOUT = "Invalid timeout value";
    private static final String WARN_UNKNOWN_HOST = "Unknown host: ";
    public static final String ERROR_ON_NETWORK = "network error to ";
    public static final String ERROR_BOTH_TARGETS = "Both url and host have been specified";
    public static final String MSG_NO_REACHABLE_TEST = "cannot do a proper reachability test on this Java version";
    public static final String ERROR_BAD_URL = "Bad URL ";
    public static final String ERROR_NO_HOST_IN_URL = "No hostname in URL ";
    public static final String METHOD_NAME = "isReachable";
    private static Class[] parameterTypes;
    
    public IsReachable() {
        this.timeout = 30;
    }
    
    public void setHost(final String host) {
        this.host = host;
    }
    
    public void setUrl(final String url) {
        this.url = url;
    }
    
    public void setTimeout(final int timeout) {
        this.timeout = timeout;
    }
    
    private boolean empty(final String string) {
        return string == null || string.length() == 0;
    }
    
    public boolean eval() throws BuildException {
        if (this.empty(this.host) && this.empty(this.url)) {
            throw new BuildException("No hostname defined");
        }
        if (this.timeout < 0) {
            throw new BuildException("Invalid timeout value");
        }
        String target = this.host;
        if (!this.empty(this.url)) {
            if (!this.empty(this.host)) {
                throw new BuildException("Both url and host have been specified");
            }
            try {
                final URL realURL = new URL(this.url);
                target = realURL.getHost();
                if (this.empty(target)) {
                    throw new BuildException("No hostname in URL " + this.url);
                }
            }
            catch (MalformedURLException e) {
                throw new BuildException("Bad URL " + this.url, e);
            }
        }
        this.log("Probing host " + target, 3);
        InetAddress address;
        try {
            address = InetAddress.getByName(target);
        }
        catch (UnknownHostException e3) {
            this.log("Unknown host: " + target);
            return false;
        }
        this.log("Host address = " + address.getHostAddress(), 3);
        Method reachableMethod = null;
        boolean reachable;
        try {
            reachableMethod = InetAddress.class.getMethod("isReachable", (Class<?>[])IsReachable.parameterTypes);
            final Object[] params = { new Integer(this.timeout * 1000) };
            try {
                reachable = (boolean)reachableMethod.invoke(address, params);
            }
            catch (IllegalAccessException e4) {
                throw new BuildException("When calling " + reachableMethod);
            }
            catch (InvocationTargetException e2) {
                final Throwable nested = e2.getTargetException();
                this.log("network error to " + target + ": " + nested.toString());
                reachable = false;
            }
        }
        catch (NoSuchMethodException e5) {
            this.log("Not found: InetAddress.isReachable", 3);
            this.log("cannot do a proper reachability test on this Java version");
            reachable = true;
        }
        this.log("host is" + (reachable ? "" : " not") + " reachable", 3);
        return reachable;
    }
    
    static {
        IsReachable.parameterTypes = new Class[] { Integer.TYPE };
    }
}
