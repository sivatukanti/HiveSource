// 
// Decompiled by Procyon v0.5.36
// 

package org.eclipse.jetty.server;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.ServletRequest;

public interface Authentication
{
    public static final Authentication UNAUTHENTICATED = new Authentication() {
        @Override
        public String toString() {
            return "UNAUTHENTICATED";
        }
    };
    public static final Authentication NOT_CHECKED = new Authentication() {
        @Override
        public String toString() {
            return "NOT CHECKED";
        }
    };
    public static final Authentication SEND_CONTINUE = new Challenge() {
        @Override
        public String toString() {
            return "CHALLENGE";
        }
    };
    public static final Authentication SEND_FAILURE = new Failure() {
        @Override
        public String toString() {
            return "FAILURE";
        }
    };
    public static final Authentication SEND_SUCCESS = new SendSuccess() {
        @Override
        public String toString() {
            return "SEND_SUCCESS";
        }
    };
    
    public static class Failed extends QuietServletException
    {
        public Failed(final String message) {
            super(message);
        }
    }
    
    public interface SendSuccess extends ResponseSent
    {
    }
    
    public interface ResponseSent extends Authentication
    {
    }
    
    public interface Failure extends ResponseSent
    {
    }
    
    public interface Challenge extends ResponseSent
    {
    }
    
    public interface Deferred extends Authentication
    {
        Authentication authenticate(final ServletRequest p0);
        
        Authentication authenticate(final ServletRequest p0, final ServletResponse p1);
        
        Authentication login(final String p0, final Object p1, final ServletRequest p2);
    }
    
    public interface Wrapped extends Authentication
    {
        HttpServletRequest getHttpServletRequest();
        
        HttpServletResponse getHttpServletResponse();
    }
    
    public interface User extends Authentication
    {
        String getAuthMethod();
        
        UserIdentity getUserIdentity();
        
        boolean isUserInRole(final UserIdentity.Scope p0, final String p1);
        
        void logout();
    }
}
