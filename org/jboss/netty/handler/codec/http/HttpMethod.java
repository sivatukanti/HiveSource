// 
// Decompiled by Procyon v0.5.36
// 

package org.jboss.netty.handler.codec.http;

import java.util.HashMap;
import java.util.Map;

public class HttpMethod implements Comparable<HttpMethod>
{
    public static final HttpMethod OPTIONS;
    public static final HttpMethod GET;
    public static final HttpMethod HEAD;
    public static final HttpMethod POST;
    public static final HttpMethod PUT;
    public static final HttpMethod PATCH;
    public static final HttpMethod DELETE;
    public static final HttpMethod TRACE;
    public static final HttpMethod CONNECT;
    private static final Map<String, HttpMethod> methodMap;
    private final String name;
    
    public static HttpMethod valueOf(String name) {
        if (name == null) {
            throw new NullPointerException("name");
        }
        name = name.trim();
        if (name.length() == 0) {
            throw new IllegalArgumentException("empty name");
        }
        final HttpMethod result = HttpMethod.methodMap.get(name);
        if (result != null) {
            return result;
        }
        return new HttpMethod(name);
    }
    
    public HttpMethod(String name) {
        if (name == null) {
            throw new NullPointerException("name");
        }
        name = name.trim();
        if (name.length() == 0) {
            throw new IllegalArgumentException("empty name");
        }
        for (int i = 0; i < name.length(); ++i) {
            if (Character.isISOControl(name.charAt(i)) || Character.isWhitespace(name.charAt(i))) {
                throw new IllegalArgumentException("invalid character in name");
            }
        }
        this.name = name;
    }
    
    public String getName() {
        return this.name;
    }
    
    @Override
    public int hashCode() {
        return this.getName().hashCode();
    }
    
    @Override
    public boolean equals(final Object o) {
        if (!(o instanceof HttpMethod)) {
            return false;
        }
        final HttpMethod that = (HttpMethod)o;
        return this.getName().equals(that.getName());
    }
    
    @Override
    public String toString() {
        return this.getName();
    }
    
    public int compareTo(final HttpMethod o) {
        return this.getName().compareTo(o.getName());
    }
    
    static {
        OPTIONS = new HttpMethod("OPTIONS");
        GET = new HttpMethod("GET");
        HEAD = new HttpMethod("HEAD");
        POST = new HttpMethod("POST");
        PUT = new HttpMethod("PUT");
        PATCH = new HttpMethod("PATCH");
        DELETE = new HttpMethod("DELETE");
        TRACE = new HttpMethod("TRACE");
        CONNECT = new HttpMethod("CONNECT");
        (methodMap = new HashMap<String, HttpMethod>()).put(HttpMethod.OPTIONS.toString(), HttpMethod.OPTIONS);
        HttpMethod.methodMap.put(HttpMethod.GET.toString(), HttpMethod.GET);
        HttpMethod.methodMap.put(HttpMethod.HEAD.toString(), HttpMethod.HEAD);
        HttpMethod.methodMap.put(HttpMethod.POST.toString(), HttpMethod.POST);
        HttpMethod.methodMap.put(HttpMethod.PUT.toString(), HttpMethod.PUT);
        HttpMethod.methodMap.put(HttpMethod.PATCH.toString(), HttpMethod.PATCH);
        HttpMethod.methodMap.put(HttpMethod.DELETE.toString(), HttpMethod.DELETE);
        HttpMethod.methodMap.put(HttpMethod.TRACE.toString(), HttpMethod.TRACE);
        HttpMethod.methodMap.put(HttpMethod.CONNECT.toString(), HttpMethod.CONNECT);
    }
}
