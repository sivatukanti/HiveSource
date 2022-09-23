// 
// Decompiled by Procyon v0.5.36
// 

package javax.servlet.http;

import java.util.Collection;
import java.io.IOException;
import javax.servlet.ServletResponse;

public interface HttpServletResponse extends ServletResponse
{
    public static final int SC_CONTINUE = 100;
    public static final int SC_SWITCHING_PROTOCOLS = 101;
    public static final int SC_OK = 200;
    public static final int SC_CREATED = 201;
    public static final int SC_ACCEPTED = 202;
    public static final int SC_NON_AUTHORITATIVE_INFORMATION = 203;
    public static final int SC_NO_CONTENT = 204;
    public static final int SC_RESET_CONTENT = 205;
    public static final int SC_PARTIAL_CONTENT = 206;
    public static final int SC_MULTIPLE_CHOICES = 300;
    public static final int SC_MOVED_PERMANENTLY = 301;
    public static final int SC_MOVED_TEMPORARILY = 302;
    public static final int SC_FOUND = 302;
    public static final int SC_SEE_OTHER = 303;
    public static final int SC_NOT_MODIFIED = 304;
    public static final int SC_USE_PROXY = 305;
    public static final int SC_TEMPORARY_REDIRECT = 307;
    public static final int SC_BAD_REQUEST = 400;
    public static final int SC_UNAUTHORIZED = 401;
    public static final int SC_PAYMENT_REQUIRED = 402;
    public static final int SC_FORBIDDEN = 403;
    public static final int SC_NOT_FOUND = 404;
    public static final int SC_METHOD_NOT_ALLOWED = 405;
    public static final int SC_NOT_ACCEPTABLE = 406;
    public static final int SC_PROXY_AUTHENTICATION_REQUIRED = 407;
    public static final int SC_REQUEST_TIMEOUT = 408;
    public static final int SC_CONFLICT = 409;
    public static final int SC_GONE = 410;
    public static final int SC_LENGTH_REQUIRED = 411;
    public static final int SC_PRECONDITION_FAILED = 412;
    public static final int SC_REQUEST_ENTITY_TOO_LARGE = 413;
    public static final int SC_REQUEST_URI_TOO_LONG = 414;
    public static final int SC_UNSUPPORTED_MEDIA_TYPE = 415;
    public static final int SC_REQUESTED_RANGE_NOT_SATISFIABLE = 416;
    public static final int SC_EXPECTATION_FAILED = 417;
    public static final int SC_INTERNAL_SERVER_ERROR = 500;
    public static final int SC_NOT_IMPLEMENTED = 501;
    public static final int SC_BAD_GATEWAY = 502;
    public static final int SC_SERVICE_UNAVAILABLE = 503;
    public static final int SC_GATEWAY_TIMEOUT = 504;
    public static final int SC_HTTP_VERSION_NOT_SUPPORTED = 505;
    
    void addCookie(final Cookie p0);
    
    boolean containsHeader(final String p0);
    
    String encodeURL(final String p0);
    
    String encodeRedirectURL(final String p0);
    
    @Deprecated
    String encodeUrl(final String p0);
    
    @Deprecated
    String encodeRedirectUrl(final String p0);
    
    void sendError(final int p0, final String p1) throws IOException;
    
    void sendError(final int p0) throws IOException;
    
    void sendRedirect(final String p0) throws IOException;
    
    void setDateHeader(final String p0, final long p1);
    
    void addDateHeader(final String p0, final long p1);
    
    void setHeader(final String p0, final String p1);
    
    void addHeader(final String p0, final String p1);
    
    void setIntHeader(final String p0, final int p1);
    
    void addIntHeader(final String p0, final int p1);
    
    void setStatus(final int p0);
    
    @Deprecated
    void setStatus(final int p0, final String p1);
    
    int getStatus();
    
    String getHeader(final String p0);
    
    Collection<String> getHeaders(final String p0);
    
    Collection<String> getHeaderNames();
}
