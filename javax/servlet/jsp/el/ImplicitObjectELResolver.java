// 
// Decompiled by Procyon v0.5.36
// 

package javax.servlet.jsp.el;

import java.util.Collection;
import java.util.Set;
import javax.servlet.http.Cookie;
import java.util.HashMap;
import javax.servlet.ServletContext;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import java.util.Enumeration;
import java.util.Map;
import java.util.ArrayList;
import java.beans.FeatureDescriptor;
import java.util.Iterator;
import javax.el.PropertyNotWritableException;
import javax.servlet.jsp.JspContext;
import javax.servlet.jsp.PageContext;
import javax.el.ELContext;
import javax.el.ELResolver;

public class ImplicitObjectELResolver extends ELResolver
{
    @Override
    public Object getValue(final ELContext context, final Object base, final Object property) {
        if (context == null) {
            throw new NullPointerException();
        }
        if (base != null) {
            return null;
        }
        final PageContext ctxt = (PageContext)context.getContext(JspContext.class);
        if ("pageContext".equals(property)) {
            context.setPropertyResolved(true);
            return ctxt;
        }
        final ImplicitObjects implicitObjects = ImplicitObjects.getImplicitObjects(ctxt);
        if ("pageScope".equals(property)) {
            context.setPropertyResolved(true);
            return implicitObjects.getPageScopeMap();
        }
        if ("requestScope".equals(property)) {
            context.setPropertyResolved(true);
            return implicitObjects.getRequestScopeMap();
        }
        if ("sessionScope".equals(property)) {
            context.setPropertyResolved(true);
            return implicitObjects.getSessionScopeMap();
        }
        if ("applicationScope".equals(property)) {
            context.setPropertyResolved(true);
            return implicitObjects.getApplicationScopeMap();
        }
        if ("param".equals(property)) {
            context.setPropertyResolved(true);
            return implicitObjects.getParamMap();
        }
        if ("paramValues".equals(property)) {
            context.setPropertyResolved(true);
            return implicitObjects.getParamsMap();
        }
        if ("header".equals(property)) {
            context.setPropertyResolved(true);
            return implicitObjects.getHeaderMap();
        }
        if ("headerValues".equals(property)) {
            context.setPropertyResolved(true);
            return implicitObjects.getHeadersMap();
        }
        if ("initParam".equals(property)) {
            context.setPropertyResolved(true);
            return implicitObjects.getInitParamMap();
        }
        if ("cookie".equals(property)) {
            context.setPropertyResolved(true);
            return implicitObjects.getCookieMap();
        }
        return null;
    }
    
    @Override
    public Class getType(final ELContext context, final Object base, final Object property) {
        if (context == null) {
            throw new NullPointerException();
        }
        if ((base == null && ("pageContext".equals(property) || "pageScope".equals(property))) || "requestScope".equals(property) || "sessionScope".equals(property) || "applicationScope".equals(property) || "param".equals(property) || "paramValues".equals(property) || "header".equals(property) || "headerValues".equals(property) || "initParam".equals(property) || "cookie".equals(property)) {
            context.setPropertyResolved(true);
        }
        return null;
    }
    
    @Override
    public void setValue(final ELContext context, final Object base, final Object property, final Object val) {
        if (context == null) {
            throw new NullPointerException();
        }
        if ((base == null && ("pageContext".equals(property) || "pageScope".equals(property))) || "requestScope".equals(property) || "sessionScope".equals(property) || "applicationScope".equals(property) || "param".equals(property) || "paramValues".equals(property) || "header".equals(property) || "headerValues".equals(property) || "initParam".equals(property) || "cookie".equals(property)) {
            throw new PropertyNotWritableException();
        }
    }
    
    @Override
    public boolean isReadOnly(final ELContext context, final Object base, final Object property) {
        if (context == null) {
            throw new NullPointerException();
        }
        if ((base == null && ("pageContext".equals(property) || "pageScope".equals(property))) || "requestScope".equals(property) || "sessionScope".equals(property) || "applicationScope".equals(property) || "param".equals(property) || "paramValues".equals(property) || "header".equals(property) || "headerValues".equals(property) || "initParam".equals(property) || "cookie".equals(property)) {
            context.setPropertyResolved(true);
            return true;
        }
        return false;
    }
    
    @Override
    public Iterator<FeatureDescriptor> getFeatureDescriptors(final ELContext context, final Object base) {
        final ArrayList<FeatureDescriptor> list = new ArrayList<FeatureDescriptor>(11);
        FeatureDescriptor descriptor = new FeatureDescriptor();
        descriptor.setName("pageContext");
        descriptor.setDisplayName("pageContext");
        descriptor.setExpert(false);
        descriptor.setHidden(false);
        descriptor.setPreferred(true);
        descriptor.setValue("type", PageContext.class);
        descriptor.setValue("resolvableAtDesignTime", Boolean.TRUE);
        list.add(descriptor);
        descriptor = new FeatureDescriptor();
        descriptor.setName("pageScope");
        descriptor.setDisplayName("pageScope");
        descriptor.setExpert(false);
        descriptor.setHidden(false);
        descriptor.setPreferred(true);
        descriptor.setValue("type", Map.class);
        descriptor.setValue("resolvableAtDesignTime", Boolean.TRUE);
        list.add(descriptor);
        descriptor = new FeatureDescriptor();
        descriptor.setName("requestScope");
        descriptor.setDisplayName("requestScope");
        descriptor.setExpert(false);
        descriptor.setHidden(false);
        descriptor.setPreferred(true);
        descriptor.setValue("type", Map.class);
        descriptor.setValue("resolvableAtDesignTime", Boolean.TRUE);
        list.add(descriptor);
        descriptor = new FeatureDescriptor();
        descriptor.setName("sessionScope");
        descriptor.setDisplayName("sessionScope");
        descriptor.setExpert(false);
        descriptor.setHidden(false);
        descriptor.setPreferred(true);
        descriptor.setValue("type", Map.class);
        descriptor.setValue("resolvableAtDesignTime", Boolean.TRUE);
        list.add(descriptor);
        descriptor = new FeatureDescriptor();
        descriptor.setName("applicationScope");
        descriptor.setDisplayName("applicationScope");
        descriptor.setExpert(false);
        descriptor.setHidden(false);
        descriptor.setPreferred(true);
        descriptor.setValue("type", Map.class);
        descriptor.setValue("resolvableAtDesignTime", Boolean.TRUE);
        list.add(descriptor);
        descriptor = new FeatureDescriptor();
        descriptor.setName("param");
        descriptor.setDisplayName("param");
        descriptor.setExpert(false);
        descriptor.setHidden(false);
        descriptor.setPreferred(true);
        descriptor.setValue("type", Map.class);
        descriptor.setValue("resolvableAtDesignTime", Boolean.TRUE);
        list.add(descriptor);
        descriptor = new FeatureDescriptor();
        descriptor.setName("paramValues");
        descriptor.setDisplayName("paramValues");
        descriptor.setExpert(false);
        descriptor.setHidden(false);
        descriptor.setPreferred(true);
        descriptor.setValue("type", Map.class);
        descriptor.setValue("resolvableAtDesignTime", Boolean.TRUE);
        list.add(descriptor);
        descriptor = new FeatureDescriptor();
        descriptor.setName("header");
        descriptor.setDisplayName("header");
        descriptor.setExpert(false);
        descriptor.setHidden(false);
        descriptor.setPreferred(true);
        descriptor.setValue("type", Map.class);
        descriptor.setValue("resolvableAtDesignTime", Boolean.TRUE);
        list.add(descriptor);
        descriptor = new FeatureDescriptor();
        descriptor.setName("headerValues");
        descriptor.setDisplayName("headerValues");
        descriptor.setExpert(false);
        descriptor.setHidden(false);
        descriptor.setPreferred(true);
        descriptor.setValue("type", Map.class);
        descriptor.setValue("resolvableAtDesignTime", Boolean.TRUE);
        list.add(descriptor);
        descriptor = new FeatureDescriptor();
        descriptor.setName("cookie");
        descriptor.setDisplayName("cookie");
        descriptor.setExpert(false);
        descriptor.setHidden(false);
        descriptor.setPreferred(true);
        descriptor.setValue("type", Map.class);
        descriptor.setValue("resolvableAtDesignTime", Boolean.TRUE);
        list.add(descriptor);
        descriptor = new FeatureDescriptor();
        descriptor.setName("initParam");
        descriptor.setDisplayName("initParam");
        descriptor.setExpert(false);
        descriptor.setHidden(false);
        descriptor.setPreferred(true);
        descriptor.setValue("type", Map.class);
        descriptor.setValue("resolvableAtDesignTime", Boolean.TRUE);
        list.add(descriptor);
        return list.iterator();
    }
    
    @Override
    public Class<String> getCommonPropertyType(final ELContext context, final Object base) {
        if (base == null) {
            return String.class;
        }
        return null;
    }
    
    private static class ImplicitObjects
    {
        static final String sAttributeName = "org.apache.taglibs.standard.ImplicitObjects";
        PageContext mContext;
        Map mPage;
        Map mRequest;
        Map mSession;
        Map mApplication;
        Map mParam;
        Map mParams;
        Map mHeader;
        Map mHeaders;
        Map mInitParam;
        Map mCookie;
        
        public ImplicitObjects(final PageContext pContext) {
            this.mContext = pContext;
        }
        
        public static ImplicitObjects getImplicitObjects(final PageContext pContext) {
            ImplicitObjects objs = (ImplicitObjects)pContext.getAttribute("org.apache.taglibs.standard.ImplicitObjects", 1);
            if (objs == null) {
                objs = new ImplicitObjects(pContext);
                pContext.setAttribute("org.apache.taglibs.standard.ImplicitObjects", objs, 1);
            }
            return objs;
        }
        
        public Map getPageScopeMap() {
            if (this.mPage == null) {
                this.mPage = createPageScopeMap(this.mContext);
            }
            return this.mPage;
        }
        
        public Map getRequestScopeMap() {
            if (this.mRequest == null) {
                this.mRequest = createRequestScopeMap(this.mContext);
            }
            return this.mRequest;
        }
        
        public Map getSessionScopeMap() {
            if (this.mSession == null) {
                this.mSession = createSessionScopeMap(this.mContext);
            }
            return this.mSession;
        }
        
        public Map getApplicationScopeMap() {
            if (this.mApplication == null) {
                this.mApplication = createApplicationScopeMap(this.mContext);
            }
            return this.mApplication;
        }
        
        public Map getParamMap() {
            if (this.mParam == null) {
                this.mParam = createParamMap(this.mContext);
            }
            return this.mParam;
        }
        
        public Map getParamsMap() {
            if (this.mParams == null) {
                this.mParams = createParamsMap(this.mContext);
            }
            return this.mParams;
        }
        
        public Map getHeaderMap() {
            if (this.mHeader == null) {
                this.mHeader = createHeaderMap(this.mContext);
            }
            return this.mHeader;
        }
        
        public Map getHeadersMap() {
            if (this.mHeaders == null) {
                this.mHeaders = createHeadersMap(this.mContext);
            }
            return this.mHeaders;
        }
        
        public Map getInitParamMap() {
            if (this.mInitParam == null) {
                this.mInitParam = createInitParamMap(this.mContext);
            }
            return this.mInitParam;
        }
        
        public Map getCookieMap() {
            if (this.mCookie == null) {
                this.mCookie = createCookieMap(this.mContext);
            }
            return this.mCookie;
        }
        
        public static Map createPageScopeMap(final PageContext pContext) {
            final PageContext context = pContext;
            return new EnumeratedMap() {
                @Override
                public Enumeration enumerateKeys() {
                    return context.getAttributeNamesInScope(1);
                }
                
                @Override
                public Object getValue(final Object pKey) {
                    if (pKey instanceof String) {
                        return context.getAttribute((String)pKey, 1);
                    }
                    return null;
                }
                
                @Override
                public boolean isMutable() {
                    return true;
                }
            };
        }
        
        public static Map createRequestScopeMap(final PageContext pContext) {
            final PageContext context = pContext;
            return new EnumeratedMap() {
                @Override
                public Enumeration enumerateKeys() {
                    return context.getAttributeNamesInScope(2);
                }
                
                @Override
                public Object getValue(final Object pKey) {
                    if (pKey instanceof String) {
                        return context.getAttribute((String)pKey, 2);
                    }
                    return null;
                }
                
                @Override
                public boolean isMutable() {
                    return true;
                }
            };
        }
        
        public static Map createSessionScopeMap(final PageContext pContext) {
            final PageContext context = pContext;
            return new EnumeratedMap() {
                @Override
                public Enumeration enumerateKeys() {
                    return context.getAttributeNamesInScope(3);
                }
                
                @Override
                public Object getValue(final Object pKey) {
                    if (pKey instanceof String) {
                        return context.getAttribute((String)pKey, 3);
                    }
                    return null;
                }
                
                @Override
                public boolean isMutable() {
                    return true;
                }
            };
        }
        
        public static Map createApplicationScopeMap(final PageContext pContext) {
            final PageContext context = pContext;
            return new EnumeratedMap() {
                @Override
                public Enumeration enumerateKeys() {
                    return context.getAttributeNamesInScope(4);
                }
                
                @Override
                public Object getValue(final Object pKey) {
                    if (pKey instanceof String) {
                        return context.getAttribute((String)pKey, 4);
                    }
                    return null;
                }
                
                @Override
                public boolean isMutable() {
                    return true;
                }
            };
        }
        
        public static Map createParamMap(final PageContext pContext) {
            final HttpServletRequest request = (HttpServletRequest)pContext.getRequest();
            return new EnumeratedMap() {
                @Override
                public Enumeration enumerateKeys() {
                    return request.getParameterNames();
                }
                
                @Override
                public Object getValue(final Object pKey) {
                    if (pKey instanceof String) {
                        return request.getParameter((String)pKey);
                    }
                    return null;
                }
                
                @Override
                public boolean isMutable() {
                    return false;
                }
            };
        }
        
        public static Map createParamsMap(final PageContext pContext) {
            final HttpServletRequest request = (HttpServletRequest)pContext.getRequest();
            return new EnumeratedMap() {
                @Override
                public Enumeration enumerateKeys() {
                    return request.getParameterNames();
                }
                
                @Override
                public Object getValue(final Object pKey) {
                    if (pKey instanceof String) {
                        return request.getParameterValues((String)pKey);
                    }
                    return null;
                }
                
                @Override
                public boolean isMutable() {
                    return false;
                }
            };
        }
        
        public static Map createHeaderMap(final PageContext pContext) {
            final HttpServletRequest request = (HttpServletRequest)pContext.getRequest();
            return new EnumeratedMap() {
                @Override
                public Enumeration enumerateKeys() {
                    return request.getHeaderNames();
                }
                
                @Override
                public Object getValue(final Object pKey) {
                    if (pKey instanceof String) {
                        return request.getHeader((String)pKey);
                    }
                    return null;
                }
                
                @Override
                public boolean isMutable() {
                    return false;
                }
            };
        }
        
        public static Map createHeadersMap(final PageContext pContext) {
            final HttpServletRequest request = (HttpServletRequest)pContext.getRequest();
            return new EnumeratedMap() {
                @Override
                public Enumeration enumerateKeys() {
                    return request.getHeaderNames();
                }
                
                @Override
                public Object getValue(final Object pKey) {
                    if (pKey instanceof String) {
                        final List l = new ArrayList();
                        final Enumeration e = request.getHeaders((String)pKey);
                        if (e != null) {
                            while (e.hasMoreElements()) {
                                l.add(e.nextElement());
                            }
                        }
                        final String[] ret = l.toArray(new String[l.size()]);
                        return ret;
                    }
                    return null;
                }
                
                @Override
                public boolean isMutable() {
                    return false;
                }
            };
        }
        
        public static Map createInitParamMap(final PageContext pContext) {
            final ServletContext context = pContext.getServletContext();
            return new EnumeratedMap() {
                @Override
                public Enumeration enumerateKeys() {
                    return context.getInitParameterNames();
                }
                
                @Override
                public Object getValue(final Object pKey) {
                    if (pKey instanceof String) {
                        return context.getInitParameter((String)pKey);
                    }
                    return null;
                }
                
                @Override
                public boolean isMutable() {
                    return false;
                }
            };
        }
        
        public static Map createCookieMap(final PageContext pContext) {
            final HttpServletRequest request = (HttpServletRequest)pContext.getRequest();
            final Cookie[] cookies = request.getCookies();
            final Map ret = new HashMap();
            for (int i = 0; cookies != null && i < cookies.length; ++i) {
                final Cookie cookie = cookies[i];
                if (cookie != null) {
                    final String name = cookie.getName();
                    if (!ret.containsKey(name)) {
                        ret.put(name, cookie);
                    }
                }
            }
            return ret;
        }
    }
    
    private abstract static class EnumeratedMap implements Map
    {
        Map mMap;
        
        public void clear() {
            throw new UnsupportedOperationException();
        }
        
        public boolean containsKey(final Object pKey) {
            return this.getValue(pKey) != null;
        }
        
        public boolean containsValue(final Object pValue) {
            return this.getAsMap().containsValue(pValue);
        }
        
        public Set entrySet() {
            return this.getAsMap().entrySet();
        }
        
        public Object get(final Object pKey) {
            return this.getValue(pKey);
        }
        
        public boolean isEmpty() {
            return !this.enumerateKeys().hasMoreElements();
        }
        
        public Set keySet() {
            return this.getAsMap().keySet();
        }
        
        public Object put(final Object pKey, final Object pValue) {
            throw new UnsupportedOperationException();
        }
        
        public void putAll(final Map pMap) {
            throw new UnsupportedOperationException();
        }
        
        public Object remove(final Object pKey) {
            throw new UnsupportedOperationException();
        }
        
        public int size() {
            return this.getAsMap().size();
        }
        
        public Collection values() {
            return this.getAsMap().values();
        }
        
        public abstract Enumeration enumerateKeys();
        
        public abstract boolean isMutable();
        
        public abstract Object getValue(final Object p0);
        
        public Map getAsMap() {
            if (this.mMap != null) {
                return this.mMap;
            }
            final Map m = this.convertToMap();
            if (!this.isMutable()) {
                this.mMap = m;
            }
            return m;
        }
        
        Map convertToMap() {
            final Map ret = new HashMap();
            final Enumeration e = this.enumerateKeys();
            while (e.hasMoreElements()) {
                final Object key = e.nextElement();
                final Object value = this.getValue(key);
                ret.put(key, value);
            }
            return ret;
        }
    }
}
