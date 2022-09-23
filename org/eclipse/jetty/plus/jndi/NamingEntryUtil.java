// 
// Decompiled by Procyon v0.5.36
// 

package org.eclipse.jetty.plus.jndi;

import org.eclipse.jetty.jndi.NamingUtil;
import javax.naming.NamingEnumeration;
import javax.naming.Binding;
import java.util.Collections;
import java.util.ArrayList;
import javax.naming.Context;
import java.util.List;
import javax.naming.NameParser;
import javax.naming.Name;
import javax.naming.NameNotFoundException;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import org.eclipse.jetty.util.log.Logger;

public class NamingEntryUtil
{
    private static Logger __log;
    
    public static boolean bindToENC(final Object scope, final String asName, String mappedName) throws NamingException {
        if (asName == null || asName.trim().equals("")) {
            throw new NamingException("No name for NamingEntry");
        }
        if (mappedName == null || "".equals(mappedName)) {
            mappedName = asName;
        }
        final NamingEntry entry = lookupNamingEntry(scope, mappedName);
        if (entry == null) {
            return false;
        }
        entry.bindToENC(asName);
        return true;
    }
    
    public static NamingEntry lookupNamingEntry(final Object scope, final String jndiName) throws NamingException {
        NamingEntry entry = null;
        try {
            final Name scopeName = getNameForScope(scope);
            final InitialContext ic = new InitialContext();
            final NameParser parser = ic.getNameParser("");
            final Name namingEntryName = makeNamingEntryName(parser, jndiName);
            scopeName.addAll(namingEntryName);
            entry = (NamingEntry)ic.lookup(scopeName);
        }
        catch (NameNotFoundException ex) {}
        return entry;
    }
    
    public static Object lookup(final Object scope, final String jndiName) throws NamingException {
        final Name scopeName = getNameForScope(scope);
        final InitialContext ic = new InitialContext();
        final NameParser parser = ic.getNameParser("");
        scopeName.addAll(parser.parse(jndiName));
        return ic.lookup(scopeName);
    }
    
    public static List<Object> lookupNamingEntries(final Object scope, final Class<?> clazz) throws NamingException {
        try {
            final Context scopeContext = getContextForScope(scope);
            final Context namingEntriesContext = (Context)scopeContext.lookup("__");
            final ArrayList<Object> list = new ArrayList<Object>();
            lookupNamingEntries(list, namingEntriesContext, clazz);
            return list;
        }
        catch (NameNotFoundException e) {
            return Collections.emptyList();
        }
    }
    
    public static Name makeNamingEntryName(final NameParser parser, final NamingEntry namingEntry) throws NamingException {
        return makeNamingEntryName(parser, (namingEntry == null) ? null : namingEntry.getJndiName());
    }
    
    public static Name makeNamingEntryName(NameParser parser, final String jndiName) throws NamingException {
        if (jndiName == null) {
            return null;
        }
        if (parser == null) {
            final InitialContext ic = new InitialContext();
            parser = ic.getNameParser("");
        }
        final Name name = parser.parse("");
        name.add("__");
        name.addAll(parser.parse(jndiName));
        return name;
    }
    
    public static Name getNameForScope(final Object scope) {
        try {
            final InitialContext ic = new InitialContext();
            final NameParser parser = ic.getNameParser("");
            final Name name = parser.parse("");
            if (scope != null) {
                name.add(canonicalizeScope(scope));
            }
            return name;
        }
        catch (NamingException e) {
            NamingEntryUtil.__log.warn(e);
            return null;
        }
    }
    
    public static Context getContextForScope(final Object scope) throws NamingException {
        final InitialContext ic = new InitialContext();
        final NameParser parser = ic.getNameParser("");
        final Name name = parser.parse("");
        if (scope != null) {
            name.add(canonicalizeScope(scope));
        }
        return (Context)ic.lookup(name);
    }
    
    public static Context getContextForNamingEntries(final Object scope) throws NamingException {
        final Context scopeContext = getContextForScope(scope);
        return (Context)scopeContext.lookup("__");
    }
    
    private static List<Object> lookupNamingEntries(final List<Object> list, final Context context, final Class<?> clazz) throws NamingException {
        try {
            final NamingEnumeration<Binding> nenum = context.listBindings("");
            while (nenum.hasMoreElements()) {
                final Binding binding = nenum.next();
                if (binding.getObject() instanceof Context) {
                    lookupNamingEntries(list, (Context)binding.getObject(), clazz);
                }
                else {
                    if (!clazz.isInstance(binding.getObject())) {
                        continue;
                    }
                    list.add(binding.getObject());
                }
            }
        }
        catch (NameNotFoundException e) {
            NamingEntryUtil.__log.debug("No entries of type " + clazz.getName() + " in context=" + context, new Object[0]);
        }
        return list;
    }
    
    private static String canonicalizeScope(final Object scope) {
        if (scope == null) {
            return "";
        }
        String str = scope.getClass().getName() + "@" + Long.toHexString(scope.hashCode());
        str = str.replace('/', '_').replace(' ', '_');
        return str;
    }
    
    static {
        NamingEntryUtil.__log = NamingUtil.__log;
    }
}
