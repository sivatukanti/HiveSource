// 
// Decompiled by Procyon v0.5.36
// 

package org.mortbay.jetty.servlet;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Enumeration;
import java.util.Iterator;
import org.mortbay.util.Attributes;
import org.mortbay.util.LazyList;
import java.util.Map;
import org.mortbay.util.UrlEncoded;
import org.mortbay.util.MultiMap;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletRequest;
import org.mortbay.jetty.HttpConnection;
import org.mortbay.jetty.Request;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.ServletResponse;
import javax.servlet.ServletRequest;
import org.mortbay.jetty.handler.ContextHandler;
import javax.servlet.RequestDispatcher;

public class Dispatcher implements RequestDispatcher
{
    public static final String __INCLUDE_JETTY = "org.mortbay.jetty.included";
    public static final String __INCLUDE_PREFIX = "javax.servlet.include.";
    public static final String __INCLUDE_REQUEST_URI = "javax.servlet.include.request_uri";
    public static final String __INCLUDE_CONTEXT_PATH = "javax.servlet.include.context_path";
    public static final String __INCLUDE_SERVLET_PATH = "javax.servlet.include.servlet_path";
    public static final String __INCLUDE_PATH_INFO = "javax.servlet.include.path_info";
    public static final String __INCLUDE_QUERY_STRING = "javax.servlet.include.query_string";
    public static final String __FORWARD_JETTY = "org.mortbay.jetty.forwarded";
    public static final String __FORWARD_PREFIX = "javax.servlet.forward.";
    public static final String __FORWARD_REQUEST_URI = "javax.servlet.forward.request_uri";
    public static final String __FORWARD_CONTEXT_PATH = "javax.servlet.forward.context_path";
    public static final String __FORWARD_SERVLET_PATH = "javax.servlet.forward.servlet_path";
    public static final String __FORWARD_PATH_INFO = "javax.servlet.forward.path_info";
    public static final String __FORWARD_QUERY_STRING = "javax.servlet.forward.query_string";
    public static final String __JSP_FILE = "org.apache.catalina.jsp_file";
    private ContextHandler _contextHandler;
    private String _uri;
    private String _path;
    private String _dQuery;
    private String _named;
    
    public static int type(final String type) {
        if ("request".equalsIgnoreCase(type)) {
            return 1;
        }
        if ("forward".equalsIgnoreCase(type)) {
            return 2;
        }
        if ("include".equalsIgnoreCase(type)) {
            return 4;
        }
        if ("error".equalsIgnoreCase(type)) {
            return 8;
        }
        throw new IllegalArgumentException(type);
    }
    
    public Dispatcher(final ContextHandler contextHandler, final String uri, final String pathInContext, final String query) {
        this._contextHandler = contextHandler;
        this._uri = uri;
        this._path = pathInContext;
        this._dQuery = query;
    }
    
    public Dispatcher(final ContextHandler contextHandler, final String name) throws IllegalStateException {
        this._contextHandler = contextHandler;
        this._named = name;
    }
    
    public void forward(final ServletRequest request, final ServletResponse response) throws ServletException, IOException {
        this.forward(request, response, 2);
    }
    
    public void error(final ServletRequest request, final ServletResponse response) throws ServletException, IOException {
        this.forward(request, response, 8);
    }
    
    public void include(final ServletRequest request, final ServletResponse response) throws ServletException, IOException {
        final Request base_request = (Request)((request instanceof Request) ? request : HttpConnection.getCurrentConnection().getRequest());
        request.removeAttribute("org.apache.catalina.jsp_file");
        final Attributes old_attr = base_request.getAttributes();
        final MultiMap old_params = base_request.getParameters();
        try {
            base_request.getConnection().include();
            if (this._named != null) {
                this._contextHandler.handle(this._named, (HttpServletRequest)request, (HttpServletResponse)response, 4);
            }
            else {
                final String query = this._dQuery;
                if (query != null) {
                    final MultiMap parameters = new MultiMap();
                    UrlEncoded.decodeTo(query, parameters, request.getCharacterEncoding());
                    if (old_params != null && old_params.size() > 0) {
                        for (final Map.Entry entry : old_params.entrySet()) {
                            final String name = entry.getKey();
                            final Object values = entry.getValue();
                            for (int i = 0; i < LazyList.size(values); ++i) {
                                parameters.add(name, LazyList.get(values, i));
                            }
                        }
                    }
                    base_request.setParameters(parameters);
                }
                final IncludeAttributes attr = new IncludeAttributes(old_attr);
                attr._requestURI = this._uri;
                attr._contextPath = this._contextHandler.getContextPath();
                attr._servletPath = null;
                attr._pathInfo = this._path;
                attr._query = query;
                base_request.setAttributes(attr);
                this._contextHandler.handle((this._named == null) ? this._path : this._named, (HttpServletRequest)request, (HttpServletResponse)response, 4);
            }
        }
        finally {
            base_request.setAttributes(old_attr);
            base_request.getConnection().included();
            base_request.setParameters(old_params);
        }
    }
    
    protected void forward(final ServletRequest request, final ServletResponse response, final int dispatch) throws ServletException, IOException {
        final Request base_request = (Request)((request instanceof Request) ? request : HttpConnection.getCurrentConnection().getRequest());
        response.resetBuffer();
        request.removeAttribute("org.apache.catalina.jsp_file");
        final String old_uri = base_request.getRequestURI();
        final String old_context_path = base_request.getContextPath();
        final String old_servlet_path = base_request.getServletPath();
        final String old_path_info = base_request.getPathInfo();
        final String old_query = base_request.getQueryString();
        final Attributes old_attr = base_request.getAttributes();
        MultiMap old_params = base_request.getParameters();
        try {
            if (this._named != null) {
                this._contextHandler.handle(this._named, (HttpServletRequest)request, (HttpServletResponse)response, dispatch);
            }
            else {
                String query = this._dQuery;
                if (query != null) {
                    final MultiMap parameters = new MultiMap();
                    UrlEncoded.decodeTo(query, parameters, request.getCharacterEncoding());
                    boolean rewrite_old_query = false;
                    if (old_params == null) {
                        base_request.getParameterNames();
                        old_params = base_request.getParameters();
                    }
                    if (old_params != null && old_params.size() > 0) {
                        for (final Map.Entry entry : old_params.entrySet()) {
                            final String name = entry.getKey();
                            if (parameters.containsKey(name)) {
                                rewrite_old_query = true;
                            }
                            final Object values = entry.getValue();
                            for (int i = 0; i < LazyList.size(values); ++i) {
                                parameters.add(name, LazyList.get(values, i));
                            }
                        }
                    }
                    if (old_query != null && old_query.length() > 0) {
                        if (rewrite_old_query) {
                            final StringBuffer overridden_query_string = new StringBuffer();
                            final MultiMap overridden_old_query = new MultiMap();
                            UrlEncoded.decodeTo(old_query, overridden_old_query, request.getCharacterEncoding());
                            final MultiMap overridden_new_query = new MultiMap();
                            UrlEncoded.decodeTo(query, overridden_new_query, request.getCharacterEncoding());
                            for (final Map.Entry entry2 : overridden_old_query.entrySet()) {
                                final String name2 = entry2.getKey();
                                if (!overridden_new_query.containsKey(name2)) {
                                    final Object values2 = entry2.getValue();
                                    for (int j = 0; j < LazyList.size(values2); ++j) {
                                        overridden_query_string.append("&" + name2 + "=" + LazyList.get(values2, j));
                                    }
                                }
                            }
                            query += (Object)overridden_query_string;
                        }
                        else {
                            query = query + "&" + old_query;
                        }
                    }
                    base_request.setParameters(parameters);
                    base_request.setQueryString(query);
                }
                final ForwardAttributes attr = new ForwardAttributes(old_attr);
                if (old_attr.getAttribute("javax.servlet.forward.request_uri") != null) {
                    attr._pathInfo = (String)old_attr.getAttribute("javax.servlet.forward.path_info");
                    attr._query = (String)old_attr.getAttribute("javax.servlet.forward.query_string");
                    attr._requestURI = (String)old_attr.getAttribute("javax.servlet.forward.request_uri");
                    attr._contextPath = (String)old_attr.getAttribute("javax.servlet.forward.context_path");
                    attr._servletPath = (String)old_attr.getAttribute("javax.servlet.forward.servlet_path");
                }
                else {
                    attr._pathInfo = old_path_info;
                    attr._query = old_query;
                    attr._requestURI = old_uri;
                    attr._contextPath = old_context_path;
                    attr._servletPath = old_servlet_path;
                }
                base_request.setRequestURI(this._uri);
                base_request.setContextPath(this._contextHandler.getContextPath());
                base_request.setAttributes(attr);
                base_request.setQueryString(query);
                this._contextHandler.handle(this._path, (HttpServletRequest)request, (HttpServletResponse)response, dispatch);
                if (base_request.getConnection().getResponse().isWriting()) {
                    try {
                        response.getWriter().close();
                    }
                    catch (IllegalStateException e) {
                        response.getOutputStream().close();
                    }
                }
                else {
                    try {
                        response.getOutputStream().close();
                    }
                    catch (IllegalStateException e) {
                        response.getWriter().close();
                    }
                }
            }
        }
        finally {
            base_request.setRequestURI(old_uri);
            base_request.setContextPath(old_context_path);
            base_request.setServletPath(old_servlet_path);
            base_request.setPathInfo(old_path_info);
            base_request.setAttributes(old_attr);
            base_request.setParameters(old_params);
            base_request.setQueryString(old_query);
        }
    }
    
    private class ForwardAttributes implements Attributes
    {
        Attributes _attr;
        String _requestURI;
        String _contextPath;
        String _servletPath;
        String _pathInfo;
        String _query;
        
        ForwardAttributes(final Attributes attributes) {
            this._attr = attributes;
        }
        
        public Object getAttribute(final String key) {
            if (Dispatcher.this._named == null) {
                if (key.equals("javax.servlet.forward.path_info")) {
                    return this._pathInfo;
                }
                if (key.equals("javax.servlet.forward.request_uri")) {
                    return this._requestURI;
                }
                if (key.equals("javax.servlet.forward.servlet_path")) {
                    return this._servletPath;
                }
                if (key.equals("javax.servlet.forward.context_path")) {
                    return this._contextPath;
                }
                if (key.equals("javax.servlet.forward.query_string")) {
                    return this._query;
                }
            }
            if (key.startsWith("javax.servlet.include.") || key.equals("org.mortbay.jetty.included")) {
                return null;
            }
            if (key.equals("org.mortbay.jetty.forwarded")) {
                return Boolean.TRUE;
            }
            return this._attr.getAttribute(key);
        }
        
        public Enumeration getAttributeNames() {
            final HashSet set = new HashSet();
            final Enumeration e = this._attr.getAttributeNames();
            while (e.hasMoreElements()) {
                final String name = e.nextElement();
                if (!name.startsWith("javax.servlet.include.") && !name.startsWith("javax.servlet.forward.")) {
                    set.add(name);
                }
            }
            if (Dispatcher.this._named == null) {
                if (this._pathInfo != null) {
                    set.add("javax.servlet.forward.path_info");
                }
                else {
                    set.remove("javax.servlet.forward.path_info");
                }
                set.add("javax.servlet.forward.request_uri");
                set.add("javax.servlet.forward.servlet_path");
                set.add("javax.servlet.forward.context_path");
                if (this._query != null) {
                    set.add("javax.servlet.forward.query_string");
                }
                else {
                    set.remove("javax.servlet.forward.query_string");
                }
            }
            return Collections.enumeration((Collection<Object>)set);
        }
        
        public void setAttribute(final String key, final Object value) {
            if (Dispatcher.this._named == null && key.startsWith("javax.servlet.")) {
                if (key.equals("javax.servlet.forward.path_info")) {
                    this._pathInfo = (String)value;
                }
                else if (key.equals("javax.servlet.forward.request_uri")) {
                    this._requestURI = (String)value;
                }
                else if (key.equals("javax.servlet.forward.servlet_path")) {
                    this._servletPath = (String)value;
                }
                else if (key.equals("javax.servlet.forward.context_path")) {
                    this._contextPath = (String)value;
                }
                else if (key.equals("javax.servlet.forward.query_string")) {
                    this._query = (String)value;
                }
                else if (value == null) {
                    this._attr.removeAttribute(key);
                }
                else {
                    this._attr.setAttribute(key, value);
                }
            }
            else if (value == null) {
                this._attr.removeAttribute(key);
            }
            else {
                this._attr.setAttribute(key, value);
            }
        }
        
        public String toString() {
            return "FORWARD+" + this._attr.toString();
        }
        
        public void clearAttributes() {
            throw new IllegalStateException();
        }
        
        public void removeAttribute(final String name) {
            this.setAttribute(name, null);
        }
    }
    
    private class IncludeAttributes implements Attributes
    {
        Attributes _attr;
        String _requestURI;
        String _contextPath;
        String _servletPath;
        String _pathInfo;
        String _query;
        
        IncludeAttributes(final Attributes attributes) {
            this._attr = attributes;
        }
        
        public Object getAttribute(final String key) {
            if (Dispatcher.this._named == null) {
                if (key.equals("javax.servlet.include.path_info")) {
                    return this._pathInfo;
                }
                if (key.equals("javax.servlet.include.servlet_path")) {
                    return this._servletPath;
                }
                if (key.equals("javax.servlet.include.context_path")) {
                    return this._contextPath;
                }
                if (key.equals("javax.servlet.include.query_string")) {
                    return this._query;
                }
                if (key.equals("javax.servlet.include.request_uri")) {
                    return this._requestURI;
                }
            }
            else if (key.startsWith("javax.servlet.include.")) {
                return null;
            }
            if (key.equals("org.mortbay.jetty.included")) {
                return Boolean.TRUE;
            }
            return this._attr.getAttribute(key);
        }
        
        public Enumeration getAttributeNames() {
            final HashSet set = new HashSet();
            final Enumeration e = this._attr.getAttributeNames();
            while (e.hasMoreElements()) {
                final String name = e.nextElement();
                if (!name.startsWith("javax.servlet.include.")) {
                    set.add(name);
                }
            }
            if (Dispatcher.this._named == null) {
                if (this._pathInfo != null) {
                    set.add("javax.servlet.include.path_info");
                }
                else {
                    set.remove("javax.servlet.include.path_info");
                }
                set.add("javax.servlet.include.request_uri");
                set.add("javax.servlet.include.servlet_path");
                set.add("javax.servlet.include.context_path");
                if (this._query != null) {
                    set.add("javax.servlet.include.query_string");
                }
                else {
                    set.remove("javax.servlet.include.query_string");
                }
            }
            return Collections.enumeration((Collection<Object>)set);
        }
        
        public void setAttribute(final String key, final Object value) {
            if (Dispatcher.this._named == null && key.startsWith("javax.servlet.")) {
                if (key.equals("javax.servlet.include.path_info")) {
                    this._pathInfo = (String)value;
                }
                else if (key.equals("javax.servlet.include.request_uri")) {
                    this._requestURI = (String)value;
                }
                else if (key.equals("javax.servlet.include.servlet_path")) {
                    this._servletPath = (String)value;
                }
                else if (key.equals("javax.servlet.include.context_path")) {
                    this._contextPath = (String)value;
                }
                else if (key.equals("javax.servlet.include.query_string")) {
                    this._query = (String)value;
                }
                else if (value == null) {
                    this._attr.removeAttribute(key);
                }
                else {
                    this._attr.setAttribute(key, value);
                }
            }
            else if (value == null) {
                this._attr.removeAttribute(key);
            }
            else {
                this._attr.setAttribute(key, value);
            }
        }
        
        public String toString() {
            return "INCLUDE+" + this._attr.toString();
        }
        
        public void clearAttributes() {
            throw new IllegalStateException();
        }
        
        public void removeAttribute(final String name) {
            this.setAttribute(name, null);
        }
    }
}
