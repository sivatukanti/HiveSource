// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.security;

import java.util.ArrayList;
import java.util.HashSet;
import java.security.Permission;
import java.util.StringTokenizer;
import java.util.Locale;
import java.util.List;
import java.util.Set;
import java.security.BasicPermission;

public final class SystemPermission extends BasicPermission
{
    private static final long serialVersionUID = 1965420504091489898L;
    public static final String SERVER = "server";
    public static final String ENGINE = "engine";
    public static final String JMX = "jmx";
    public static final String SHUTDOWN = "shutdown";
    public static final String CONTROL = "control";
    public static final String MONITOR = "monitor";
    private static final Set LEGAL_NAMES;
    private static final List LEGAL_ACTIONS;
    private final String actions;
    
    public SystemPermission(final String s, final String s2) {
        super(s);
        if (!SystemPermission.LEGAL_NAMES.contains(s)) {
            throw new IllegalArgumentException("Unknown permission " + s);
        }
        this.actions = getCanonicalForm(s2);
    }
    
    public String getActions() {
        return this.actions;
    }
    
    private static String getCanonicalForm(String lowerCase) {
        lowerCase = lowerCase.trim().toLowerCase(Locale.ENGLISH);
        final boolean[] array = new boolean[SystemPermission.LEGAL_ACTIONS.size()];
        final StringTokenizer stringTokenizer = new StringTokenizer(lowerCase, ",");
        while (stringTokenizer.hasMoreTokens()) {
            final int index = SystemPermission.LEGAL_ACTIONS.indexOf(stringTokenizer.nextToken().trim().toLowerCase(Locale.ENGLISH));
            if (index != -1) {
                array[index] = true;
            }
        }
        final StringBuffer sb = new StringBuffer();
        for (int i = 0; i < array.length; ++i) {
            if (array[i]) {
                if (sb.length() != 0) {
                    sb.append(",");
                }
                sb.append(SystemPermission.LEGAL_ACTIONS.get(i));
            }
        }
        return sb.toString();
    }
    
    public boolean equals(final Object obj) {
        return super.equals(obj) && this.getActions().equals(((SystemPermission)obj).getActions());
    }
    
    public boolean implies(final Permission p) {
        if (!super.implies(p)) {
            return false;
        }
        final int actionMask = getActionMask(this.getActions());
        final int actionMask2 = getActionMask(p.getActions());
        return (actionMask & actionMask2) == actionMask2;
    }
    
    private static int getActionMask(final String str) {
        int n = 0;
        final StringTokenizer stringTokenizer = new StringTokenizer(str, ",");
        while (stringTokenizer.hasMoreTokens()) {
            final int index = SystemPermission.LEGAL_ACTIONS.indexOf(stringTokenizer.nextElement());
            if (index != -1) {
                n |= 1 << index;
            }
        }
        return n;
    }
    
    static {
        (LEGAL_NAMES = new HashSet()).add("server");
        SystemPermission.LEGAL_NAMES.add("engine");
        SystemPermission.LEGAL_NAMES.add("jmx");
        (LEGAL_ACTIONS = new ArrayList()).add("control");
        SystemPermission.LEGAL_ACTIONS.add("monitor");
        SystemPermission.LEGAL_ACTIONS.add("shutdown");
    }
}
