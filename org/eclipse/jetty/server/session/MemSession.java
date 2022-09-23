// 
// Decompiled by Procyon v0.5.36
// 

package org.eclipse.jetty.server.session;

import java.util.Iterator;
import java.util.HashSet;
import java.util.Set;
import java.util.List;
import java.util.Collection;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import javax.servlet.http.HttpServletRequest;
import java.util.Map;

public class MemSession extends AbstractSession
{
    private final Map<String, Object> _attributes;
    
    protected MemSession(final AbstractSessionManager abstractSessionManager, final HttpServletRequest request) {
        super(abstractSessionManager, request);
        this._attributes = new HashMap<String, Object>();
    }
    
    public MemSession(final AbstractSessionManager abstractSessionManager, final long created, final long accessed, final String clusterId) {
        super(abstractSessionManager, created, accessed, clusterId);
        this._attributes = new HashMap<String, Object>();
    }
    
    @Override
    public Map<String, Object> getAttributeMap() {
        return this._attributes;
    }
    
    @Override
    public int getAttributes() {
        synchronized (this) {
            this.checkValid();
            return this._attributes.size();
        }
    }
    
    @Override
    public Enumeration<String> doGetAttributeNames() {
        final List<String> names = (this._attributes == null) ? Collections.EMPTY_LIST : new ArrayList<String>(this._attributes.keySet());
        return Collections.enumeration(names);
    }
    
    @Override
    public Set<String> getNames() {
        synchronized (this) {
            return new HashSet<String>(this._attributes.keySet());
        }
    }
    
    @Override
    public void clearAttributes() {
        while (this._attributes != null && this._attributes.size() > 0) {
            final ArrayList<String> keys;
            synchronized (this) {
                keys = new ArrayList<String>(this._attributes.keySet());
            }
            for (final String key : keys) {
                final Object value;
                synchronized (this) {
                    value = this.doPutOrRemove(key, null);
                }
                this.unbindValue(key, value);
                ((AbstractSessionManager)this.getSessionManager()).doSessionAttributeListeners(this, key, value, null);
            }
        }
        if (this._attributes != null) {
            this._attributes.clear();
        }
    }
    
    public void addAttributes(final Map<String, Object> map) {
        this._attributes.putAll(map);
    }
    
    @Override
    public Object doPutOrRemove(final String name, final Object value) {
        return (value == null) ? this._attributes.remove(name) : this._attributes.put(name, value);
    }
    
    @Override
    public Object doGet(final String name) {
        return this._attributes.get(name);
    }
}
