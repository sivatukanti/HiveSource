// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.iapi.services.diag;

public class DiagnosticUtil
{
    private DiagnosticUtil() {
    }
    
    public static Diagnosticable findDiagnostic(final Object o) {
        Class<?> clazz = o.getClass();
        try {
            Class<?> forName;
            while (true) {
                final String name = clazz.getName();
                final int n = name.lastIndexOf(46) + 1;
                final String string = name.substring(0, n) + "D_" + name.substring(n);
                try {
                    forName = Class.forName(string);
                }
                catch (ClassNotFoundException ex) {
                    clazz = clazz.getSuperclass();
                    if (clazz == null) {
                        return null;
                    }
                    continue;
                }
                break;
            }
            final Diagnosticable diagnosticable = (Diagnosticable)forName.newInstance();
            diagnosticable.init(o);
            return diagnosticable;
        }
        catch (Exception ex2) {
            return null;
        }
    }
    
    public static String toDiagString(final Object o) {
        String s = null;
        if (o == null) {
            return "null";
        }
        try {
            final Diagnosticable diagnostic = findDiagnostic(o);
            if (diagnostic != null) {
                s = diagnostic.diag();
            }
        }
        catch (Throwable t) {}
        if (s == null) {
            s = o.toString();
        }
        return s;
    }
}
