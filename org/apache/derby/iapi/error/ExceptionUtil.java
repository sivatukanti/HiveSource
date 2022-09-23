// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.iapi.error;

import java.security.AccessControlException;
import java.security.PrivilegedActionException;
import java.security.AccessController;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.security.PrivilegedExceptionAction;
import java.io.Writer;
import java.io.PrintWriter;
import java.io.StringWriter;

public class ExceptionUtil
{
    public static String getSQLStateFromIdentifier(final String s) {
        if (s.length() == 5) {
            return s;
        }
        return s.substring(0, 5);
    }
    
    public static int getSeverityFromIdentifier(final String s) {
        int n = 0;
        Label_0318: {
            switch (s.length()) {
                case 5: {
                    Label_0183: {
                        switch (s.charAt(0)) {
                            case '0': {
                                switch (s.charAt(1)) {
                                    case '1': {
                                        n = 10000;
                                        break;
                                    }
                                    case '7':
                                    case 'A': {
                                        n = 20000;
                                        break;
                                    }
                                    case '8': {
                                        n = 40000;
                                        break;
                                    }
                                }
                                break;
                            }
                            case '2':
                            case '3': {
                                n = 20000;
                                break;
                            }
                            case '4': {
                                switch (s.charAt(1)) {
                                    case '0': {
                                        n = 30000;
                                        break Label_0183;
                                    }
                                    case '2': {
                                        n = 20000;
                                        break Label_0183;
                                    }
                                }
                                break;
                            }
                        }
                    }
                    break;
                }
                default: {
                    switch (s.charAt(6)) {
                        case 'M': {
                            n = 50000;
                            break Label_0318;
                        }
                        case 'D': {
                            n = 45000;
                            break Label_0318;
                        }
                        case 'C': {
                            n = 40000;
                            break Label_0318;
                        }
                        case 'T': {
                            n = 30000;
                            break Label_0318;
                        }
                        case 'S': {
                            n = 20000;
                            break Label_0318;
                        }
                        case 'U': {
                            n = 0;
                            break Label_0318;
                        }
                    }
                    break;
                }
            }
        }
        return n;
    }
    
    public static String dumpThreads() {
        final StringWriter out = new StringWriter();
        final PrintWriter s = new PrintWriter(out, true);
        try {
            Thread.class.getMethod("getAllStackTraces", (Class[])new Class[0]);
            final String str = AccessController.doPrivileged((PrivilegedExceptionAction<String>)new PrivilegedExceptionAction() {
                private final /* synthetic */ Method val$m = Class.forName("org.apache.derby.iapi.error.ThreadDump").getMethod("getStackDumpString", (Class<?>[])new Class[0]);
                
                public Object run() throws IllegalArgumentException, IllegalAccessException, InvocationTargetException {
                    return this.val$m.invoke(null, (Object[])null);
                }
            });
            s.print("---------------\nStack traces for all live threads:");
            s.println("\n" + str);
            s.println("---------------");
        }
        catch (NoSuchMethodException ex2) {
            s.println("(Skipping thread dump because it is not supported on JVM 1.4)");
        }
        catch (Exception ex) {
            if (ex instanceof PrivilegedActionException && ex.getCause() instanceof InvocationTargetException && ex.getCause().getCause() instanceof AccessControlException) {
                s.println("(Skipping thread dump because of insufficient permissions:\n" + ex.getCause().getCause() + ")\n");
            }
            else {
                s.println("\nAssertFailure tried to do a thread dump, but there was an error:");
                ex.getCause().printStackTrace(s);
            }
        }
        return out.toString();
    }
}
