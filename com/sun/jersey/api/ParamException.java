// 
// Decompiled by Procyon v0.5.36
// 

package com.sun.jersey.api;

import javax.ws.rs.FormParam;
import javax.ws.rs.CookieParam;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.QueryParam;
import javax.ws.rs.MatrixParam;
import javax.ws.rs.PathParam;
import java.lang.annotation.Annotation;
import javax.ws.rs.WebApplicationException;

public abstract class ParamException extends WebApplicationException
{
    private final Class<? extends Annotation> parameterType;
    private final String name;
    private final String defaultStringValue;
    
    protected ParamException(final Throwable cause, final int status, final Class<? extends Annotation> parameterType, final String name, final String defaultStringValue) {
        super(cause, status);
        this.parameterType = parameterType;
        this.name = name;
        this.defaultStringValue = defaultStringValue;
    }
    
    public Class<? extends Annotation> getParameterType() {
        return this.parameterType;
    }
    
    public String getParameterName() {
        return this.name;
    }
    
    public String getDefaultStringValue() {
        return this.defaultStringValue;
    }
    
    public abstract static class URIParamException extends ParamException
    {
        protected URIParamException(final Throwable cause, final Class<? extends Annotation> parameterType, final String name, final String defaultStringValue) {
            super(cause, 404, parameterType, name, defaultStringValue);
        }
    }
    
    public static class PathParamException extends URIParamException
    {
        public PathParamException(final Throwable cause, final String name, final String defaultStringValue) {
            super(cause, PathParam.class, name, defaultStringValue);
        }
    }
    
    public static class MatrixParamException extends URIParamException
    {
        public MatrixParamException(final Throwable cause, final String name, final String defaultStringValue) {
            super(cause, MatrixParam.class, name, defaultStringValue);
        }
    }
    
    public static class QueryParamException extends URIParamException
    {
        public QueryParamException(final Throwable cause, final String name, final String defaultStringValue) {
            super(cause, QueryParam.class, name, defaultStringValue);
        }
    }
    
    public static class HeaderParamException extends ParamException
    {
        public HeaderParamException(final Throwable cause, final String name, final String defaultStringValue) {
            super(cause, 400, HeaderParam.class, name, defaultStringValue);
        }
    }
    
    public static class CookieParamException extends ParamException
    {
        public CookieParamException(final Throwable cause, final String name, final String defaultStringValue) {
            super(cause, 400, CookieParam.class, name, defaultStringValue);
        }
    }
    
    public static class FormParamException extends ParamException
    {
        public FormParamException(final Throwable cause, final String name, final String defaultStringValue) {
            super(cause, 400, FormParam.class, name, defaultStringValue);
        }
    }
}
