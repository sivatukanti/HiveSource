// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.iapi.jdbc;

import java.security.PrivilegedAction;
import java.lang.reflect.InvocationTargetException;
import java.security.PrivilegedActionException;
import java.security.AccessController;
import java.security.PrivilegedExceptionAction;
import java.lang.reflect.Constructor;
import org.apache.derby.iapi.services.monitor.Monitor;
import java.util.Properties;
import java.io.Writer;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.lang.reflect.Method;
import org.apache.derby.iapi.services.monitor.ModuleControl;

public final class DRDAServerStarter implements ModuleControl, Runnable
{
    private Object server;
    private Method runServerMethod;
    private Method serverShutdownMethod;
    private Thread serverThread;
    private static final String serverClassName = "org.apache.derby.impl.drda.NetworkServerControlImpl";
    private Class serverClass;
    private InetAddress listenAddress;
    private int portNumber;
    private String userArg;
    private String passwordArg;
    private PrintWriter consoleWriter;
    
    public DRDAServerStarter() {
        this.listenAddress = null;
        this.portNumber = -1;
        this.userArg = null;
        this.passwordArg = null;
        this.consoleWriter = null;
    }
    
    public void setStartInfo(final InetAddress inetAddress, final int n, final String userArg, final String passwordArg, final PrintWriter printWriter) {
        this.userArg = userArg;
        this.passwordArg = passwordArg;
        this.setStartInfo(inetAddress, n, printWriter);
    }
    
    public void setStartInfo(final InetAddress listenAddress, final int portNumber, final PrintWriter printWriter) {
        this.listenAddress = listenAddress;
        this.portNumber = portNumber;
        if (printWriter != null) {
            this.consoleWriter = new PrintWriter(printWriter, true);
        }
        else {
            this.consoleWriter = printWriter;
        }
    }
    
    private void findStartStopMethods(final Class clazz) throws SecurityException, NoSuchMethodException {
        this.runServerMethod = clazz.getMethod("blockingStart", PrintWriter.class);
        this.serverShutdownMethod = clazz.getMethod("directShutdown", (Class[])null);
    }
    
    public void boot(final boolean b, final Properties properties) {
        if (this.server != null) {
            return;
        }
        try {
            this.serverClass = Class.forName("org.apache.derby.impl.drda.NetworkServerControlImpl");
        }
        catch (ClassNotFoundException ex3) {
            Monitor.logTextMessage("J100", "org.apache.derby.impl.drda.NetworkServerControlImpl");
            return;
        }
        catch (Error error) {
            Monitor.logTextMessage("J101", "org.apache.derby.impl.drda.NetworkServerControlImpl", error.getMessage());
            return;
        }
        try {
            Constructor<Object> constructor;
            try {
                constructor = AccessController.doPrivileged((PrivilegedExceptionAction<Constructor<Object>>)new PrivilegedExceptionAction() {
                    public Object run() throws NoSuchMethodException, SecurityException {
                        if (DRDAServerStarter.this.listenAddress == null) {
                            return DRDAServerStarter.this.serverClass.getConstructor(String.class, String.class);
                        }
                        return DRDAServerStarter.this.serverClass.getConstructor(InetAddress.class, Integer.TYPE, String.class, String.class);
                    }
                });
            }
            catch (PrivilegedActionException ex) {
                Monitor.logTextMessage("J102", ex.getException().getMessage());
                ex.printStackTrace(Monitor.getStream().getPrintWriter());
                return;
            }
            this.findStartStopMethods(this.serverClass);
            if (this.listenAddress == null) {
                this.server = constructor.newInstance(this.userArg, this.passwordArg);
            }
            else {
                this.server = constructor.newInstance(this.listenAddress, new Integer(this.portNumber), this.userArg, this.passwordArg);
            }
            (this.serverThread = Monitor.getMonitor().getDaemonThread(this, "NetworkServerStarter", false)).start();
        }
        catch (Exception ex2) {
            Monitor.logTextMessage("J102", ex2.getMessage());
            this.server = null;
            ex2.printStackTrace(Monitor.getStream().getPrintWriter());
        }
    }
    
    public void run() {
        try {
            this.runServerMethod.invoke(this.server, this.consoleWriter);
        }
        catch (InvocationTargetException ex) {
            Monitor.logTextMessage("J102", ex.getTargetException().getMessage());
            ex.printStackTrace(Monitor.getStream().getPrintWriter());
            this.server = null;
        }
        catch (Exception ex2) {
            Monitor.logTextMessage("J102", ex2.getMessage());
            this.server = null;
            ex2.printStackTrace(Monitor.getStream().getPrintWriter());
        }
    }
    
    public void stop() {
        try {
            if (this.serverThread != null && this.serverThread.isAlive()) {
                this.serverShutdownMethod.invoke(this.server, (Object[])null);
                AccessController.doPrivileged((PrivilegedAction<Object>)new PrivilegedAction() {
                    public Object run() {
                        DRDAServerStarter.this.serverThread.interrupt();
                        return null;
                    }
                });
                this.serverThread = null;
            }
        }
        catch (InvocationTargetException ex) {
            Monitor.logTextMessage("J103", ex.getTargetException().getMessage());
            ex.printStackTrace(Monitor.getStream().getPrintWriter());
        }
        catch (Exception ex2) {
            Monitor.logTextMessage("J103", ex2.getMessage());
            ex2.printStackTrace(Monitor.getStream().getPrintWriter());
        }
        this.serverThread = null;
        this.server = null;
        this.serverClass = null;
        this.listenAddress = null;
        this.portNumber = -1;
        this.consoleWriter = null;
    }
}
