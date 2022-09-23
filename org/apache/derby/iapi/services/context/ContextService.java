// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.iapi.services.context;

import java.util.Iterator;
import java.security.AccessControlException;
import org.apache.derby.iapi.services.info.JVMInfo;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Stack;
import org.apache.derby.iapi.error.ShutdownException;
import org.apache.derby.iapi.services.monitor.Monitor;
import java.util.HashSet;
import org.apache.derby.iapi.services.stream.HeaderPrintWriter;

public final class ContextService
{
    private static ContextService factory;
    private HeaderPrintWriter errorStream;
    private ThreadLocal threadContextList;
    private HashSet allContexts;
    
    public ContextService() {
        this.threadContextList = new ThreadLocal();
        this.errorStream = Monitor.getStream();
        ContextService.factory = this;
        this.allContexts = new HashSet();
    }
    
    public static void stop() {
        final ContextService factory = ContextService.factory;
        if (factory != null) {
            synchronized (factory) {
                factory.allContexts = null;
                factory.threadContextList = null;
                ContextService.factory = null;
            }
        }
    }
    
    public static ContextService getFactory() {
        final ContextService factory = ContextService.factory;
        if (factory == null) {
            throw new ShutdownException();
        }
        return factory;
    }
    
    public static Context getContext(final String s) {
        final ContextManager currentContextManager = getFactory().getCurrentContextManager();
        if (currentContextManager == null) {
            return null;
        }
        return currentContextManager.getContext(s);
    }
    
    public static Context getContextOrNull(final String s) {
        final ContextService factory = ContextService.factory;
        if (factory == null) {
            return null;
        }
        final ContextManager currentContextManager = factory.getCurrentContextManager();
        if (currentContextManager == null) {
            return null;
        }
        return currentContextManager.getContext(s);
    }
    
    public ContextManager getCurrentContextManager() {
        final ThreadLocal threadContextList = this.threadContextList;
        if (threadContextList == null) {
            return null;
        }
        final ContextManager value = threadContextList.get();
        if (value instanceof ContextManager) {
            final Thread currentThread = Thread.currentThread();
            final ContextManager contextManager = value;
            if (contextManager.activeThread == currentThread) {
                return contextManager;
            }
            return null;
        }
        else {
            if (value == null) {
                return null;
            }
            return ((Stack<ContextManager>)value).peek();
        }
    }
    
    public void resetCurrentContextManager(final ContextManager contextManager) {
        final ThreadLocal threadContextList = this.threadContextList;
        if (threadContextList == null) {
            return;
        }
        if (contextManager.activeCount != -1) {
            if (--contextManager.activeCount == 0) {
                contextManager.activeThread = null;
                if (contextManager.isEmpty()) {
                    threadContextList.set(null);
                }
            }
            return;
        }
        final Stack<ContextManager> stack = threadContextList.get();
        stack.pop();
        final ContextManager value = stack.peek();
        boolean b = false;
        boolean b2 = false;
        for (int i = 0; i < stack.size(); ++i) {
            final Object element = stack.elementAt(i);
            if (element != value) {
                b = true;
            }
            if (element == contextManager) {
                b2 = true;
            }
        }
        if (!b2) {
            contextManager.activeThread = null;
            contextManager.activeCount = 0;
        }
        if (!b) {
            value.activeCount = stack.size();
            threadContextList.set(value);
        }
    }
    
    private boolean addToThreadList(Thread currentThread, final ContextManager item) {
        final ThreadLocal threadContextList = this.threadContextList;
        if (threadContextList == null) {
            return false;
        }
        final ContextManager value = threadContextList.get();
        if (item == value) {
            return true;
        }
        if (value == null) {
            threadContextList.set(item);
            return true;
        }
        Stack<ContextManager> value2;
        if (value instanceof ContextManager) {
            final ContextManager item2 = value;
            if (currentThread == null) {
                currentThread = Thread.currentThread();
            }
            if (item2.activeThread != currentThread) {
                threadContextList.set(item);
                return true;
            }
            value2 = new Stack<ContextManager>();
            threadContextList.set(value2);
            for (int i = 0; i < item2.activeCount; ++i) {
                value2.push(item2);
            }
            item2.activeCount = -1;
        }
        else {
            value2 = (Stack<ContextManager>)value;
        }
        value2.push(item);
        item.activeCount = -1;
        return false;
    }
    
    public void setCurrentContextManager(final ContextManager contextManager) {
        Thread thread = null;
        if (contextManager.activeThread == null) {
            thread = (contextManager.activeThread = Thread.currentThread());
        }
        if (this.addToThreadList(thread, contextManager)) {
            ++contextManager.activeCount;
        }
    }
    
    public ContextManager newContextManager() {
        final ContextManager e = new ContextManager(this, this.errorStream);
        new SystemContext(e);
        synchronized (this) {
            this.allContexts.add(e);
        }
        return e;
    }
    
    public void notifyAllActiveThreads(final Context interrupted) {
        final Thread currentThread = Thread.currentThread();
        synchronized (this) {
            for (final ContextManager contextManager : this.allContexts) {
                final Thread activeThread = contextManager.activeThread;
                if (activeThread == currentThread) {
                    continue;
                }
                if (activeThread == null) {
                    continue;
                }
                final Thread thread = activeThread;
                if (!contextManager.setInterrupted(interrupted)) {
                    continue;
                }
                try {
                    AccessController.doPrivileged((PrivilegedAction<Object>)new PrivilegedAction() {
                        public Object run() {
                            thread.interrupt();
                            return null;
                        }
                    });
                }
                catch (AccessControlException ex) {
                    if (JVMInfo.isIBMJVM()) {
                        JVMInfo.javaDump();
                    }
                    throw ex;
                }
            }
        }
    }
    
    synchronized void removeContext(final ContextManager o) {
        if (this.allContexts != null) {
            this.allContexts.remove(o);
        }
    }
}
