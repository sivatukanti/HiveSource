// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.impl.services.timer;

import org.apache.derby.iapi.error.StandardException;
import java.util.Properties;
import java.util.TimerTask;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Timer;
import org.apache.derby.iapi.services.monitor.ModuleControl;
import org.apache.derby.iapi.services.timer.TimerFactory;

public class SingletonTimerFactory implements TimerFactory, ModuleControl
{
    private Timer singletonTimer;
    
    public SingletonTimerFactory() {
        ClassLoader classLoader = null;
        boolean b = false;
        try {
            classLoader = AccessController.doPrivileged((PrivilegedAction<ClassLoader>)new PrivilegedAction() {
                public Object run() {
                    return Thread.currentThread().getContextClassLoader();
                }
            });
            b = true;
        }
        catch (SecurityException ex) {}
        if (b) {
            try {
                AccessController.doPrivileged((PrivilegedAction<Object>)new PrivilegedAction() {
                    public Object run() {
                        Thread.currentThread().setContextClassLoader(null);
                        return null;
                    }
                });
            }
            catch (SecurityException ex2) {}
        }
        this.singletonTimer = new Timer(true);
        if (b) {
            try {
                AccessController.doPrivileged((PrivilegedAction<Object>)new PrivilegedAction() {
                    public Object run() {
                        Thread.currentThread().setContextClassLoader(classLoader);
                        return null;
                    }
                });
            }
            catch (SecurityException ex3) {}
        }
    }
    
    Timer getCancellationTimer() {
        return this.singletonTimer;
    }
    
    public void schedule(final TimerTask task, final long delay) {
        this.singletonTimer.schedule(task, delay);
    }
    
    public void cancel(final TimerTask timerTask) {
        timerTask.cancel();
    }
    
    public void boot(final boolean b, final Properties properties) throws StandardException {
    }
    
    public void stop() {
        this.singletonTimer.cancel();
    }
}
