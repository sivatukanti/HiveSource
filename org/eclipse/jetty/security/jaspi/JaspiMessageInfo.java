// 
// Decompiled by Procyon v0.5.36
// 

package org.eclipse.jetty.security.jaspi;

import java.util.HashMap;
import java.util.Collection;
import java.util.Set;
import java.util.Iterator;
import java.util.Map;
import javax.servlet.ServletResponse;
import javax.servlet.ServletRequest;
import javax.security.auth.message.MessageInfo;

public class JaspiMessageInfo implements MessageInfo
{
    public static final String MANDATORY_KEY = "javax.security.auth.message.MessagePolicy.isMandatory";
    public static final String AUTH_METHOD_KEY = "javax.servlet.http.authType";
    private ServletRequest request;
    private ServletResponse response;
    private final MIMap map;
    
    public JaspiMessageInfo(final ServletRequest request, final ServletResponse response, final boolean isAuthMandatory) {
        this.request = request;
        this.response = response;
        this.map = new MIMap(isAuthMandatory);
    }
    
    public Map getMap() {
        return this.map;
    }
    
    public Object getRequestMessage() {
        return this.request;
    }
    
    public Object getResponseMessage() {
        return this.response;
    }
    
    public void setRequestMessage(final Object request) {
        this.request = (ServletRequest)request;
    }
    
    public void setResponseMessage(final Object response) {
        this.response = (ServletResponse)response;
    }
    
    public String getAuthMethod() {
        return this.map.getAuthMethod();
    }
    
    public boolean isAuthMandatory() {
        return this.map.isAuthMandatory();
    }
    
    private static class MIMap implements Map
    {
        private final boolean isMandatory;
        private String authMethod;
        private Map delegate;
        
        private MIMap(final boolean mandatory) {
            this.isMandatory = mandatory;
        }
        
        public int size() {
            return ((this.isMandatory + (this.authMethod != null)) ? 1 : 0) + ((this.delegate == null) ? 0 : this.delegate.size());
        }
        
        public boolean isEmpty() {
            return !this.isMandatory && this.authMethod == null && (this.delegate == null || this.delegate.isEmpty());
        }
        
        public boolean containsKey(final Object key) {
            if ("javax.security.auth.message.MessagePolicy.isMandatory".equals(key)) {
                return this.isMandatory;
            }
            if ("javax.servlet.http.authType".equals(key)) {
                return this.authMethod != null;
            }
            return this.delegate != null && this.delegate.containsKey(key);
        }
        
        public boolean containsValue(final Object value) {
            return (this.isMandatory && "true".equals(value)) || (this.authMethod == value || (this.authMethod != null && this.authMethod.equals(value))) || (this.delegate != null && this.delegate.containsValue(value));
        }
        
        public Object get(final Object key) {
            if ("javax.security.auth.message.MessagePolicy.isMandatory".equals(key)) {
                return this.isMandatory ? "true" : null;
            }
            if ("javax.servlet.http.authType".equals(key)) {
                return this.authMethod;
            }
            if (this.delegate == null) {
                return null;
            }
            return this.delegate.get(key);
        }
        
        public Object put(final Object key, final Object value) {
            if ("javax.security.auth.message.MessagePolicy.isMandatory".equals(key)) {
                throw new IllegalArgumentException("Mandatory not mutable");
            }
            if ("javax.servlet.http.authType".equals(key)) {
                final String authMethod = this.authMethod;
                this.authMethod = (String)value;
                if (this.delegate != null) {
                    this.delegate.put("javax.servlet.http.authType", value);
                }
                return authMethod;
            }
            return this.getDelegate(true).put(key, value);
        }
        
        public Object remove(final Object key) {
            if ("javax.security.auth.message.MessagePolicy.isMandatory".equals(key)) {
                throw new IllegalArgumentException("Mandatory not mutable");
            }
            if ("javax.servlet.http.authType".equals(key)) {
                final String authMethod = this.authMethod;
                this.authMethod = null;
                if (this.delegate != null) {
                    this.delegate.remove("javax.servlet.http.authType");
                }
                return authMethod;
            }
            if (this.delegate == null) {
                return null;
            }
            return this.delegate.remove(key);
        }
        
        public void putAll(final Map map) {
            if (map != null) {
                for (final Object o : map.entrySet()) {
                    final Entry entry = (Entry)o;
                    this.put(entry.getKey(), entry.getValue());
                }
            }
        }
        
        public void clear() {
            this.authMethod = null;
            this.delegate = null;
        }
        
        public Set keySet() {
            return this.getDelegate(true).keySet();
        }
        
        public Collection values() {
            return this.getDelegate(true).values();
        }
        
        public Set entrySet() {
            return this.getDelegate(true).entrySet();
        }
        
        private Map getDelegate(final boolean create) {
            if (!create || this.delegate != null) {
                return this.delegate;
            }
            if (create) {
                this.delegate = new HashMap();
                if (this.isMandatory) {
                    this.delegate.put("javax.security.auth.message.MessagePolicy.isMandatory", "true");
                }
                if (this.authMethod != null) {
                    this.delegate.put("javax.servlet.http.authType", this.authMethod);
                }
            }
            return this.delegate;
        }
        
        boolean isAuthMandatory() {
            return this.isMandatory;
        }
        
        String getAuthMethod() {
            return this.authMethod;
        }
    }
}
