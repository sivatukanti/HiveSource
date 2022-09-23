// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.commons.configuration2;

import javax.naming.NotContextException;
import java.util.List;
import org.apache.commons.configuration2.event.EventType;
import org.apache.commons.configuration2.event.ConfigurationErrorEvent;
import javax.naming.NameNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import org.apache.commons.lang3.StringUtils;
import java.util.Iterator;
import javax.naming.NamingEnumeration;
import javax.naming.NameClassPair;
import org.apache.commons.configuration2.io.ConfigurationLogger;
import java.util.HashSet;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import java.util.Set;
import javax.naming.Context;

public class JNDIConfiguration extends AbstractConfiguration
{
    private String prefix;
    private Context context;
    private Context baseContext;
    private final Set<String> clearedProperties;
    
    public JNDIConfiguration() throws NamingException {
        this((String)null);
    }
    
    public JNDIConfiguration(final String prefix) throws NamingException {
        this(new InitialContext(), prefix);
    }
    
    public JNDIConfiguration(final Context context) {
        this(context, null);
    }
    
    public JNDIConfiguration(final Context context, final String prefix) {
        this.clearedProperties = new HashSet<String>();
        this.context = context;
        this.prefix = prefix;
        this.initLogger(new ConfigurationLogger(JNDIConfiguration.class));
        this.addErrorLogListener();
    }
    
    private void recursiveGetKeys(final Set<String> keys, final Context context, final String prefix, final Set<Context> processedCtx) throws NamingException {
        processedCtx.add(context);
        NamingEnumeration<NameClassPair> elements = null;
        try {
            elements = context.list("");
            while (elements.hasMore()) {
                final NameClassPair nameClassPair = elements.next();
                final String name = nameClassPair.getName();
                final Object object = context.lookup(name);
                final StringBuilder key = new StringBuilder();
                key.append(prefix);
                if (key.length() > 0) {
                    key.append(".");
                }
                key.append(name);
                if (object instanceof Context) {
                    final Context subcontext = (Context)object;
                    if (processedCtx.contains(subcontext)) {
                        continue;
                    }
                    this.recursiveGetKeys(keys, subcontext, key.toString(), processedCtx);
                }
                else {
                    keys.add(key.toString());
                }
            }
        }
        finally {
            if (elements != null) {
                elements.close();
            }
        }
    }
    
    @Override
    protected Iterator<String> getKeysInternal() {
        return this.getKeysInternal("");
    }
    
    @Override
    protected Iterator<String> getKeysInternal(final String prefix) {
        final String[] splitPath = StringUtils.split(prefix, ".");
        final List<String> path = Arrays.asList(splitPath);
        try {
            final Context context = this.getContext(path, this.getBaseContext());
            final Set<String> keys = new HashSet<String>();
            if (context != null) {
                this.recursiveGetKeys(keys, context, prefix, new HashSet<Context>());
            }
            else if (this.containsKey(prefix)) {
                keys.add(prefix);
            }
            return keys.iterator();
        }
        catch (NameNotFoundException e2) {
            return new ArrayList<String>().iterator();
        }
        catch (NamingException e) {
            this.fireError(ConfigurationErrorEvent.READ, ConfigurationErrorEvent.READ, null, null, e);
            return new ArrayList<String>().iterator();
        }
    }
    
    private Context getContext(final List<String> path, final Context context) throws NamingException {
        if (path == null || path.isEmpty()) {
            return context;
        }
        final String key = path.get(0);
        NamingEnumeration<NameClassPair> elements = null;
        try {
            elements = context.list("");
            while (elements.hasMore()) {
                final NameClassPair nameClassPair = elements.next();
                final String name = nameClassPair.getName();
                final Object object = context.lookup(name);
                if (object instanceof Context && name.equals(key)) {
                    final Context subcontext = (Context)object;
                    return this.getContext(path.subList(1, path.size()), subcontext);
                }
            }
        }
        finally {
            if (elements != null) {
                elements.close();
            }
        }
        return null;
    }
    
    @Override
    protected boolean isEmptyInternal() {
        try {
            NamingEnumeration<NameClassPair> enumeration = null;
            try {
                enumeration = this.getBaseContext().list("");
                return !enumeration.hasMore();
            }
            finally {
                if (enumeration != null) {
                    enumeration.close();
                }
            }
        }
        catch (NamingException e) {
            this.fireError(ConfigurationErrorEvent.READ, ConfigurationErrorEvent.READ, null, null, e);
            return true;
        }
    }
    
    @Override
    protected void setPropertyInternal(final String key, final Object value) {
        throw new UnsupportedOperationException("This operation is not supported");
    }
    
    @Override
    protected void clearPropertyDirect(final String key) {
        this.clearedProperties.add(key);
    }
    
    @Override
    protected boolean containsKeyInternal(String key) {
        if (this.clearedProperties.contains(key)) {
            return false;
        }
        key = key.replaceAll("\\.", "/");
        try {
            this.getBaseContext().lookup(key);
            return true;
        }
        catch (NameNotFoundException e2) {
            return false;
        }
        catch (NamingException e) {
            this.fireError(ConfigurationErrorEvent.READ, ConfigurationErrorEvent.READ, key, null, e);
            return false;
        }
    }
    
    public String getPrefix() {
        return this.prefix;
    }
    
    public void setPrefix(final String prefix) {
        this.prefix = prefix;
        this.baseContext = null;
    }
    
    @Override
    protected Object getPropertyInternal(String key) {
        if (this.clearedProperties.contains(key)) {
            return null;
        }
        try {
            key = key.replaceAll("\\.", "/");
            return this.getBaseContext().lookup(key);
        }
        catch (NameNotFoundException e2) {
            return null;
        }
        catch (NotContextException nctxex) {
            return null;
        }
        catch (NamingException e) {
            this.fireError(ConfigurationErrorEvent.READ, ConfigurationErrorEvent.READ, key, null, e);
            return null;
        }
    }
    
    @Override
    protected void addPropertyDirect(final String key, final Object obj) {
        throw new UnsupportedOperationException("This operation is not supported");
    }
    
    public Context getBaseContext() throws NamingException {
        if (this.baseContext == null) {
            this.baseContext = (Context)this.getContext().lookup((this.prefix == null) ? "" : this.prefix);
        }
        return this.baseContext;
    }
    
    public Context getContext() {
        return this.context;
    }
    
    public void setContext(final Context context) {
        this.clearedProperties.clear();
        this.context = context;
    }
}
