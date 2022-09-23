// 
// Decompiled by Procyon v0.5.36
// 

package org.eclipse.jetty.server;

import org.eclipse.jetty.util.log.Log;
import javax.servlet.http.HttpUpgradeHandler;
import java.nio.charset.StandardCharsets;
import java.io.OutputStream;
import org.eclipse.jetty.util.IO;
import java.io.ByteArrayOutputStream;
import java.io.File;
import javax.servlet.MultipartConfigElement;
import javax.servlet.http.Part;
import javax.servlet.http.HttpServletResponse;
import java.nio.charset.UnsupportedCharsetException;
import java.nio.charset.Charset;
import org.eclipse.jetty.util.StringUtil;
import java.util.HashMap;
import javax.servlet.ServletRequestAttributeEvent;
import org.eclipse.jetty.http.HttpMethod;
import org.eclipse.jetty.http.HttpCookie;
import org.eclipse.jetty.server.session.AbstractSession;
import javax.servlet.ServletResponse;
import javax.servlet.ServletContext;
import org.eclipse.jetty.http.HttpURI;
import org.eclipse.jetty.http.HostPortHttpField;
import org.eclipse.jetty.http.HttpScheme;
import org.eclipse.jetty.util.URIUtil;
import javax.servlet.RequestDispatcher;
import java.security.Principal;
import java.io.Reader;
import java.io.InputStreamReader;
import org.eclipse.jetty.http.HttpVersion;
import java.net.UnknownHostException;
import java.net.InetAddress;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import javax.servlet.ServletInputStream;
import javax.servlet.http.Cookie;
import org.eclipse.jetty.util.AttributesMap;
import java.util.Collections;
import java.util.Enumeration;
import javax.servlet.AsyncContext;
import javax.servlet.ServletException;
import java.io.InputStream;
import java.io.IOException;
import org.eclipse.jetty.util.UrlEncoded;
import org.eclipse.jetty.http.MimeTypes;
import java.io.UnsupportedEncodingException;
import org.eclipse.jetty.http.BadMessageException;
import javax.servlet.AsyncListener;
import java.util.EventListener;
import org.eclipse.jetty.http.HttpHeader;
import java.util.Iterator;
import org.eclipse.jetty.http.HttpField;
import org.eclipse.jetty.http.HttpFields;
import java.util.ArrayList;
import javax.servlet.ServletRequestWrapper;
import javax.servlet.ServletRequest;
import org.eclipse.jetty.util.MultiPartInputStreamParser;
import javax.servlet.http.HttpSession;
import java.util.Map;
import java.net.InetSocketAddress;
import java.io.BufferedReader;
import javax.servlet.DispatcherType;
import org.eclipse.jetty.server.handler.ContextHandler;
import org.eclipse.jetty.util.Attributes;
import org.eclipse.jetty.http.MetaData;
import javax.servlet.ServletRequestAttributeListener;
import java.util.List;
import org.eclipse.jetty.util.MultiMap;
import java.util.Locale;
import java.util.Collection;
import org.eclipse.jetty.util.log.Logger;
import javax.servlet.http.HttpServletRequest;

public class Request implements HttpServletRequest
{
    public static final String __MULTIPART_CONFIG_ELEMENT = "org.eclipse.jetty.multipartConfig";
    public static final String __MULTIPART_INPUT_STREAM = "org.eclipse.jetty.multiPartInputStream";
    public static final String __MULTIPART_CONTEXT = "org.eclipse.jetty.multiPartContext";
    private static final Logger LOG;
    private static final Collection<Locale> __defaultLocale;
    private static final int __NONE = 0;
    private static final int _STREAM = 1;
    private static final int __READER = 2;
    private static final MultiMap<String> NO_PARAMS;
    private final HttpChannel _channel;
    private final List<ServletRequestAttributeListener> _requestAttributeListeners;
    private final HttpInput _input;
    private MetaData.Request _metaData;
    private String _contextPath;
    private String _servletPath;
    private String _pathInfo;
    private boolean _secure;
    private String _asyncNotSupportedSource;
    private boolean _newContext;
    private boolean _cookiesExtracted;
    private boolean _handled;
    private boolean _contentParamsExtracted;
    private boolean _requestedSessionIdFromCookie;
    private Attributes _attributes;
    private Authentication _authentication;
    private String _characterEncoding;
    private ContextHandler.Context _context;
    private CookieCutter _cookies;
    private DispatcherType _dispatcherType;
    private int _inputState;
    private MultiMap<String> _queryParameters;
    private MultiMap<String> _contentParameters;
    private MultiMap<String> _parameters;
    private String _queryEncoding;
    private BufferedReader _reader;
    private String _readerEncoding;
    private InetSocketAddress _remote;
    private String _requestedSessionId;
    private Map<Object, HttpSession> _savedNewSessions;
    private UserIdentity.Scope _scope;
    private HttpSession _session;
    private SessionManager _sessionManager;
    private long _timeStamp;
    private MultiPartInputStreamParser _multiPartInputStream;
    private AsyncContextState _async;
    
    public static Request getBaseRequest(ServletRequest request) {
        if (request instanceof Request) {
            return (Request)request;
        }
        final Object channel = request.getAttribute(HttpChannel.class.getName());
        if (channel instanceof HttpChannel) {
            return ((HttpChannel)channel).getRequest();
        }
        while (request instanceof ServletRequestWrapper) {
            request = ((ServletRequestWrapper)request).getRequest();
        }
        if (request instanceof Request) {
            return (Request)request;
        }
        return null;
    }
    
    public Request(final HttpChannel channel, final HttpInput input) {
        this._requestAttributeListeners = new ArrayList<ServletRequestAttributeListener>();
        this._asyncNotSupportedSource = null;
        this._cookiesExtracted = false;
        this._handled = false;
        this._requestedSessionIdFromCookie = false;
        this._inputState = 0;
        this._channel = channel;
        this._input = input;
    }
    
    public HttpFields getHttpFields() {
        final MetaData.Request metadata = this._metaData;
        if (metadata == null) {
            throw new IllegalStateException();
        }
        return metadata.getFields();
    }
    
    public HttpInput getHttpInput() {
        return this._input;
    }
    
    public boolean isPush() {
        return Boolean.TRUE.equals(this.getAttribute("org.eclipse.jetty.pushed"));
    }
    
    public boolean isPushSupported() {
        return this.getHttpChannel().getHttpTransport().isPushSupported();
    }
    
    public PushBuilder getPushBuilder() {
        if (!this.isPushSupported()) {
            throw new IllegalStateException();
        }
        final HttpFields fields = new HttpFields(this.getHttpFields().size() + 5);
        boolean conditional = false;
        for (final HttpField field : this.getHttpFields()) {
            final HttpHeader header = field.getHeader();
            if (header == null) {
                fields.add(field);
            }
            else {
                switch (header) {
                    case IF_MATCH:
                    case IF_RANGE:
                    case IF_UNMODIFIED_SINCE:
                    case RANGE:
                    case EXPECT:
                    case REFERER:
                    case COOKIE: {
                        continue;
                    }
                    case AUTHORIZATION: {
                        continue;
                    }
                    case IF_NONE_MATCH:
                    case IF_MODIFIED_SINCE: {
                        conditional = true;
                        continue;
                    }
                    default: {
                        fields.add(field);
                        continue;
                    }
                }
            }
        }
        String id = null;
        try {
            final HttpSession session = this.getSession();
            if (session != null) {
                session.getLastAccessedTime();
                id = session.getId();
            }
            else {
                id = this.getRequestedSessionId();
            }
        }
        catch (IllegalStateException e) {
            id = this.getRequestedSessionId();
        }
        final PushBuilder builder = new PushBuilderImpl(this, fields, this.getMethod(), this.getQueryString(), id, conditional);
        builder.addHeader("referer", this.getRequestURL().toString());
        return builder;
    }
    
    public void addEventListener(final EventListener listener) {
        if (listener instanceof ServletRequestAttributeListener) {
            this._requestAttributeListeners.add((ServletRequestAttributeListener)listener);
        }
        if (listener instanceof AsyncListener) {
            throw new IllegalArgumentException(listener.getClass().toString());
        }
    }
    
    private MultiMap<String> getParameters() {
        if (!this._contentParamsExtracted) {
            this._contentParamsExtracted = true;
            if (this._contentParameters == null) {
                try {
                    this.extractContentParameters();
                }
                catch (IllegalStateException | IllegalArgumentException ex3) {
                    final RuntimeException ex;
                    final RuntimeException e = ex;
                    throw new BadMessageException("Unable to parse form content", e);
                }
            }
        }
        if (this._queryParameters == null) {
            try {
                this.extractQueryParameters();
            }
            catch (IllegalStateException | IllegalArgumentException ex4) {
                final RuntimeException ex2;
                final RuntimeException e = ex2;
                throw new BadMessageException("Unable to parse URI query", e);
            }
        }
        if (this._queryParameters == Request.NO_PARAMS || this._queryParameters.size() == 0) {
            this._parameters = this._contentParameters;
        }
        else if (this._contentParameters == Request.NO_PARAMS || this._contentParameters.size() == 0) {
            this._parameters = this._queryParameters;
        }
        else {
            (this._parameters = new MultiMap<String>()).addAllValues(this._queryParameters);
            this._parameters.addAllValues(this._contentParameters);
        }
        final MultiMap<String> parameters = this._parameters;
        return (parameters == null) ? Request.NO_PARAMS : parameters;
    }
    
    private void extractQueryParameters() {
        final MetaData.Request metadata = this._metaData;
        if (metadata == null || metadata.getURI() == null || !metadata.getURI().hasQuery()) {
            this._queryParameters = Request.NO_PARAMS;
        }
        else {
            this._queryParameters = new MultiMap<String>();
            if (this._queryEncoding == null) {
                metadata.getURI().decodeQueryTo(this._queryParameters);
            }
            else {
                try {
                    metadata.getURI().decodeQueryTo(this._queryParameters, this._queryEncoding);
                }
                catch (UnsupportedEncodingException e) {
                    if (Request.LOG.isDebugEnabled()) {
                        Request.LOG.warn(e);
                    }
                    else {
                        Request.LOG.warn(e.toString(), new Object[0]);
                    }
                }
            }
        }
    }
    
    private void extractContentParameters() {
        String contentType = this.getContentType();
        if (contentType == null || contentType.isEmpty()) {
            this._contentParameters = Request.NO_PARAMS;
        }
        else {
            this._contentParameters = new MultiMap<String>();
            contentType = HttpFields.valueParameters(contentType, null);
            final int contentLength = this.getContentLength();
            if (contentLength != 0) {
                if (MimeTypes.Type.FORM_ENCODED.is(contentType) && this._inputState == 0 && this._channel.getHttpConfiguration().isFormEncodedMethod(this.getMethod())) {
                    this.extractFormParameters(this._contentParameters);
                }
                else if (contentType.startsWith("multipart/form-data") && this.getAttribute("org.eclipse.jetty.multipartConfig") != null && this._multiPartInputStream == null) {
                    this.extractMultipartParameters(this._contentParameters);
                }
            }
        }
    }
    
    public void extractFormParameters(final MultiMap<String> params) {
        try {
            int maxFormContentSize = -1;
            int maxFormKeys = -1;
            if (this._context != null) {
                maxFormContentSize = this._context.getContextHandler().getMaxFormContentSize();
                maxFormKeys = this._context.getContextHandler().getMaxFormKeys();
            }
            if (maxFormContentSize < 0) {
                final Object obj = this._channel.getServer().getAttribute("org.eclipse.jetty.server.Request.maxFormContentSize");
                if (obj == null) {
                    maxFormContentSize = 200000;
                }
                else if (obj instanceof Number) {
                    final Number size = (Number)obj;
                    maxFormContentSize = size.intValue();
                }
                else if (obj instanceof String) {
                    maxFormContentSize = Integer.valueOf((String)obj);
                }
            }
            if (maxFormKeys < 0) {
                final Object obj = this._channel.getServer().getAttribute("org.eclipse.jetty.server.Request.maxFormKeys");
                if (obj == null) {
                    maxFormKeys = 1000;
                }
                else if (obj instanceof Number) {
                    final Number keys = (Number)obj;
                    maxFormKeys = keys.intValue();
                }
                else if (obj instanceof String) {
                    maxFormKeys = Integer.valueOf((String)obj);
                }
            }
            final int contentLength = this.getContentLength();
            if (contentLength > maxFormContentSize && maxFormContentSize > 0) {
                throw new IllegalStateException("Form too large: " + contentLength + " > " + maxFormContentSize);
            }
            final InputStream in = this.getInputStream();
            if (this._input.isAsync()) {
                throw new IllegalStateException("Cannot extract parameters with async IO");
            }
            UrlEncoded.decodeTo(in, params, this.getCharacterEncoding(), (contentLength < 0) ? maxFormContentSize : -1, maxFormKeys);
        }
        catch (IOException e) {
            if (Request.LOG.isDebugEnabled()) {
                Request.LOG.warn(e);
            }
            else {
                Request.LOG.warn(e.toString(), new Object[0]);
            }
        }
    }
    
    private void extractMultipartParameters(final MultiMap<String> result) {
        try {
            this.getParts(result);
        }
        catch (IOException | ServletException ex2) {
            final Exception ex;
            final Exception e = ex;
            Request.LOG.warn(e);
            throw new RuntimeException(e);
        }
    }
    
    @Override
    public AsyncContext getAsyncContext() {
        final HttpChannelState state = this.getHttpChannelState();
        if (this._async == null || !state.isAsyncStarted()) {
            throw new IllegalStateException(state.getStatusString());
        }
        return this._async;
    }
    
    public HttpChannelState getHttpChannelState() {
        return this._channel.getState();
    }
    
    @Override
    public Object getAttribute(final String name) {
        if (name.startsWith("org.eclipse.jetty")) {
            if (Server.class.getName().equals(name)) {
                return this._channel.getServer();
            }
            if (HttpChannel.class.getName().equals(name)) {
                return this._channel;
            }
            if (HttpConnection.class.getName().equals(name) && this._channel.getHttpTransport() instanceof HttpConnection) {
                return this._channel.getHttpTransport();
            }
        }
        return (this._attributes == null) ? null : this._attributes.getAttribute(name);
    }
    
    @Override
    public Enumeration<String> getAttributeNames() {
        if (this._attributes == null) {
            return Collections.enumeration((Collection<String>)Collections.emptyList());
        }
        return AttributesMap.getAttributeNamesCopy(this._attributes);
    }
    
    public Attributes getAttributes() {
        if (this._attributes == null) {
            this._attributes = new AttributesMap();
        }
        return this._attributes;
    }
    
    public Authentication getAuthentication() {
        return this._authentication;
    }
    
    @Override
    public String getAuthType() {
        if (this._authentication instanceof Authentication.Deferred) {
            this.setAuthentication(((Authentication.Deferred)this._authentication).authenticate(this));
        }
        if (this._authentication instanceof Authentication.User) {
            return ((Authentication.User)this._authentication).getAuthMethod();
        }
        return null;
    }
    
    @Override
    public String getCharacterEncoding() {
        if (this._characterEncoding == null) {
            this.getContentType();
        }
        return this._characterEncoding;
    }
    
    public HttpChannel getHttpChannel() {
        return this._channel;
    }
    
    @Override
    public int getContentLength() {
        final MetaData.Request metadata = this._metaData;
        if (metadata == null) {
            return -1;
        }
        if (metadata.getContentLength() != Long.MIN_VALUE) {
            return (int)metadata.getContentLength();
        }
        return (int)metadata.getFields().getLongField(HttpHeader.CONTENT_LENGTH.toString());
    }
    
    @Override
    public long getContentLengthLong() {
        final MetaData.Request metadata = this._metaData;
        if (metadata == null) {
            return -1L;
        }
        if (metadata.getContentLength() != Long.MIN_VALUE) {
            return metadata.getContentLength();
        }
        return metadata.getFields().getLongField(HttpHeader.CONTENT_LENGTH.toString());
    }
    
    public long getContentRead() {
        return this._input.getContentConsumed();
    }
    
    @Override
    public String getContentType() {
        final MetaData.Request metadata = this._metaData;
        if (metadata == null) {
            return null;
        }
        final String content_type = metadata.getFields().get(HttpHeader.CONTENT_TYPE);
        if (this._characterEncoding == null && content_type != null) {
            final MimeTypes.Type mime = MimeTypes.CACHE.get(content_type);
            final String charset = (mime == null || mime.getCharset() == null) ? MimeTypes.getCharsetFromContentType(content_type) : mime.getCharset().toString();
            if (charset != null) {
                this._characterEncoding = charset;
            }
        }
        return content_type;
    }
    
    public ContextHandler.Context getContext() {
        return this._context;
    }
    
    @Override
    public String getContextPath() {
        return this._contextPath;
    }
    
    @Override
    public Cookie[] getCookies() {
        final MetaData.Request metadata = this._metaData;
        if (metadata == null || this._cookiesExtracted) {
            if (this._cookies == null || this._cookies.getCookies().length == 0) {
                return null;
            }
            return this._cookies.getCookies();
        }
        else {
            this._cookiesExtracted = true;
            for (final String c : metadata.getFields().getValuesList(HttpHeader.COOKIE)) {
                if (this._cookies == null) {
                    this._cookies = new CookieCutter();
                }
                this._cookies.addCookieField(c);
            }
            if (this._cookies == null || this._cookies.getCookies().length == 0) {
                return null;
            }
            return this._cookies.getCookies();
        }
    }
    
    @Override
    public long getDateHeader(final String name) {
        final MetaData.Request metadata = this._metaData;
        return (metadata == null) ? -1L : metadata.getFields().getDateField(name);
    }
    
    @Override
    public DispatcherType getDispatcherType() {
        return this._dispatcherType;
    }
    
    @Override
    public String getHeader(final String name) {
        final MetaData.Request metadata = this._metaData;
        return (metadata == null) ? null : metadata.getFields().get(name);
    }
    
    @Override
    public Enumeration<String> getHeaderNames() {
        final MetaData.Request metadata = this._metaData;
        return (metadata == null) ? Collections.emptyEnumeration() : metadata.getFields().getFieldNames();
    }
    
    @Override
    public Enumeration<String> getHeaders(final String name) {
        final MetaData.Request metadata = this._metaData;
        if (metadata == null) {
            return Collections.emptyEnumeration();
        }
        final Enumeration<String> e = metadata.getFields().getValues(name);
        if (e == null) {
            return Collections.enumeration((Collection<String>)Collections.emptyList());
        }
        return e;
    }
    
    public int getInputState() {
        return this._inputState;
    }
    
    @Override
    public ServletInputStream getInputStream() throws IOException {
        if (this._inputState != 0 && this._inputState != 1) {
            throw new IllegalStateException("READER");
        }
        this._inputState = 1;
        if (this._channel.isExpecting100Continue()) {
            this._channel.continue100(this._input.available());
        }
        return this._input;
    }
    
    @Override
    public int getIntHeader(final String name) {
        final MetaData.Request metadata = this._metaData;
        return (metadata == null) ? -1 : ((int)metadata.getFields().getLongField(name));
    }
    
    @Override
    public Locale getLocale() {
        final MetaData.Request metadata = this._metaData;
        if (metadata == null) {
            return Locale.getDefault();
        }
        final List<String> acceptable = metadata.getFields().getQualityCSV(HttpHeader.ACCEPT_LANGUAGE);
        if (acceptable.isEmpty()) {
            return Locale.getDefault();
        }
        String language = acceptable.get(0);
        language = HttpFields.stripParameters(language);
        String country = "";
        final int dash = language.indexOf(45);
        if (dash > -1) {
            country = language.substring(dash + 1).trim();
            language = language.substring(0, dash).trim();
        }
        return new Locale(language, country);
    }
    
    @Override
    public Enumeration<Locale> getLocales() {
        final MetaData.Request metadata = this._metaData;
        if (metadata == null) {
            return Collections.enumeration(Request.__defaultLocale);
        }
        final List<String> acceptable = metadata.getFields().getQualityCSV(HttpHeader.ACCEPT_LANGUAGE);
        if (acceptable.isEmpty()) {
            return Collections.enumeration(Request.__defaultLocale);
        }
        String country;
        final int dash;
        final List<Locale> locales = acceptable.stream().map(language -> {
            language = HttpFields.stripParameters(language);
            country = "";
            dash = language.indexOf(45);
            if (dash > -1) {
                country = language.substring(dash + 1).trim();
                language = language.substring(0, dash).trim();
            }
            return new Locale(language, country);
        }).collect((Collector<? super Object, ?, List<Locale>>)Collectors.toList());
        return Collections.enumeration(locales);
    }
    
    @Override
    public String getLocalAddr() {
        if (this._channel == null) {
            try {
                final String name = InetAddress.getLocalHost().getHostAddress();
                if ("0.0.0.0".equals(name)) {
                    return null;
                }
                return name;
            }
            catch (UnknownHostException e) {
                Request.LOG.ignore(e);
            }
        }
        final InetSocketAddress local = this._channel.getLocalAddress();
        if (local == null) {
            return "";
        }
        final InetAddress address = local.getAddress();
        if (address == null) {
            return local.getHostString();
        }
        return address.getHostAddress();
    }
    
    @Override
    public String getLocalName() {
        if (this._channel == null) {
            try {
                final String name = InetAddress.getLocalHost().getHostName();
                if ("0.0.0.0".equals(name)) {
                    return null;
                }
                return name;
            }
            catch (UnknownHostException e) {
                Request.LOG.ignore(e);
            }
        }
        final InetSocketAddress local = this._channel.getLocalAddress();
        return local.getHostString();
    }
    
    @Override
    public int getLocalPort() {
        if (this._channel == null) {
            return 0;
        }
        final InetSocketAddress local = this._channel.getLocalAddress();
        return local.getPort();
    }
    
    @Override
    public String getMethod() {
        final MetaData.Request metadata = this._metaData;
        return (metadata == null) ? null : metadata.getMethod();
    }
    
    @Override
    public String getParameter(final String name) {
        return this.getParameters().getValue(name, 0);
    }
    
    @Override
    public Map<String, String[]> getParameterMap() {
        return Collections.unmodifiableMap((Map<? extends String, ? extends String[]>)this.getParameters().toStringArrayMap());
    }
    
    @Override
    public Enumeration<String> getParameterNames() {
        return Collections.enumeration((Collection<String>)this.getParameters().keySet());
    }
    
    @Override
    public String[] getParameterValues(final String name) {
        final List<String> vals = this.getParameters().getValues(name);
        if (vals == null) {
            return null;
        }
        return vals.toArray(new String[vals.size()]);
    }
    
    public MultiMap<String> getQueryParameters() {
        return this._queryParameters;
    }
    
    public void setQueryParameters(final MultiMap<String> queryParameters) {
        this._queryParameters = queryParameters;
    }
    
    public void setContentParameters(final MultiMap<String> contentParameters) {
        this._contentParameters = contentParameters;
    }
    
    public void resetParameters() {
        this._parameters = null;
    }
    
    @Override
    public String getPathInfo() {
        return this._pathInfo;
    }
    
    @Override
    public String getPathTranslated() {
        if (this._pathInfo == null || this._context == null) {
            return null;
        }
        return this._context.getRealPath(this._pathInfo);
    }
    
    @Override
    public String getProtocol() {
        final MetaData.Request metadata = this._metaData;
        if (metadata == null) {
            return null;
        }
        final HttpVersion version = metadata.getHttpVersion();
        if (version == null) {
            return null;
        }
        return version.toString();
    }
    
    public HttpVersion getHttpVersion() {
        final MetaData.Request metadata = this._metaData;
        return (metadata == null) ? null : metadata.getHttpVersion();
    }
    
    public String getQueryEncoding() {
        return this._queryEncoding;
    }
    
    @Override
    public String getQueryString() {
        final MetaData.Request metadata = this._metaData;
        return metadata.getURI().getQuery();
    }
    
    @Override
    public BufferedReader getReader() throws IOException {
        if (this._inputState != 0 && this._inputState != 2) {
            throw new IllegalStateException("STREAMED");
        }
        if (this._inputState == 2) {
            return this._reader;
        }
        String encoding = this.getCharacterEncoding();
        if (encoding == null) {
            encoding = "iso-8859-1";
        }
        if (this._reader == null || !encoding.equalsIgnoreCase(this._readerEncoding)) {
            final ServletInputStream in = this.getInputStream();
            this._readerEncoding = encoding;
            this._reader = new BufferedReader(new InputStreamReader(in, encoding)) {
                @Override
                public void close() throws IOException {
                    in.close();
                }
            };
        }
        this._inputState = 2;
        return this._reader;
    }
    
    @Override
    public String getRealPath(final String path) {
        if (this._context == null) {
            return null;
        }
        return this._context.getRealPath(path);
    }
    
    public InetSocketAddress getRemoteInetSocketAddress() {
        InetSocketAddress remote = this._remote;
        if (remote == null) {
            remote = this._channel.getRemoteAddress();
        }
        return remote;
    }
    
    @Override
    public String getRemoteAddr() {
        InetSocketAddress remote = this._remote;
        if (remote == null) {
            remote = this._channel.getRemoteAddress();
        }
        if (remote == null) {
            return "";
        }
        final InetAddress address = remote.getAddress();
        if (address == null) {
            return remote.getHostString();
        }
        return address.getHostAddress();
    }
    
    @Override
    public String getRemoteHost() {
        InetSocketAddress remote = this._remote;
        if (remote == null) {
            remote = this._channel.getRemoteAddress();
        }
        return (remote == null) ? "" : remote.getHostString();
    }
    
    @Override
    public int getRemotePort() {
        InetSocketAddress remote = this._remote;
        if (remote == null) {
            remote = this._channel.getRemoteAddress();
        }
        return (remote == null) ? 0 : remote.getPort();
    }
    
    @Override
    public String getRemoteUser() {
        final Principal p = this.getUserPrincipal();
        if (p == null) {
            return null;
        }
        return p.getName();
    }
    
    @Override
    public RequestDispatcher getRequestDispatcher(String path) {
        if (path == null || this._context == null) {
            return null;
        }
        if (!path.startsWith("/")) {
            String relTo = URIUtil.addPaths(this._servletPath, this._pathInfo);
            final int slash = relTo.lastIndexOf("/");
            if (slash > 1) {
                relTo = relTo.substring(0, slash + 1);
            }
            else {
                relTo = "/";
            }
            path = URIUtil.addPaths(relTo, path);
        }
        return this._context.getRequestDispatcher(path);
    }
    
    @Override
    public String getRequestedSessionId() {
        return this._requestedSessionId;
    }
    
    @Override
    public String getRequestURI() {
        final MetaData.Request metadata = this._metaData;
        return (metadata == null) ? null : metadata.getURI().getPath();
    }
    
    @Override
    public StringBuffer getRequestURL() {
        final StringBuffer url = new StringBuffer(128);
        URIUtil.appendSchemeHostPort(url, this.getScheme(), this.getServerName(), this.getServerPort());
        url.append(this.getRequestURI());
        return url;
    }
    
    public Response getResponse() {
        return this._channel.getResponse();
    }
    
    public StringBuilder getRootURL() {
        final StringBuilder url = new StringBuilder(128);
        URIUtil.appendSchemeHostPort(url, this.getScheme(), this.getServerName(), this.getServerPort());
        return url;
    }
    
    @Override
    public String getScheme() {
        final MetaData.Request metadata = this._metaData;
        final String scheme = (metadata == null) ? null : metadata.getURI().getScheme();
        return (scheme == null) ? HttpScheme.HTTP.asString() : scheme;
    }
    
    @Override
    public String getServerName() {
        final MetaData.Request metadata = this._metaData;
        final String name = (metadata == null) ? null : metadata.getURI().getHost();
        if (name != null) {
            return name;
        }
        return this.findServerName();
    }
    
    private String findServerName() {
        final MetaData.Request metadata = this._metaData;
        HttpField host = (metadata == null) ? null : metadata.getFields().getField(HttpHeader.HOST);
        if (host != null) {
            if (!(host instanceof HostPortHttpField) && host.getValue() != null && !host.getValue().isEmpty()) {
                host = new HostPortHttpField(host.getValue());
            }
            if (host instanceof HostPortHttpField) {
                final HostPortHttpField authority = (HostPortHttpField)host;
                metadata.getURI().setAuthority(authority.getHost(), authority.getPort());
                return authority.getHost();
            }
        }
        final String name = this.getLocalName();
        if (name != null) {
            return name;
        }
        try {
            return InetAddress.getLocalHost().getHostAddress();
        }
        catch (UnknownHostException e) {
            Request.LOG.ignore(e);
            return null;
        }
    }
    
    @Override
    public int getServerPort() {
        final MetaData.Request metadata = this._metaData;
        final HttpURI uri = (metadata == null) ? null : metadata.getURI();
        final int port = (uri == null || uri.getHost() == null) ? this.findServerPort() : uri.getPort();
        if (port > 0) {
            return port;
        }
        if (this.getScheme().equalsIgnoreCase("https")) {
            return 443;
        }
        return 80;
    }
    
    private int findServerPort() {
        final MetaData.Request metadata = this._metaData;
        final HttpField host = (metadata == null) ? null : metadata.getFields().getField(HttpHeader.HOST);
        if (host != null) {
            final HostPortHttpField authority = (HostPortHttpField)((host instanceof HostPortHttpField) ? host : new HostPortHttpField(host.getValue()));
            metadata.getURI().setAuthority(authority.getHost(), authority.getPort());
            return authority.getPort();
        }
        if (this._channel != null) {
            return this.getLocalPort();
        }
        return -1;
    }
    
    @Override
    public ServletContext getServletContext() {
        return this._context;
    }
    
    public String getServletName() {
        if (this._scope != null) {
            return this._scope.getName();
        }
        return null;
    }
    
    @Override
    public String getServletPath() {
        if (this._servletPath == null) {
            this._servletPath = "";
        }
        return this._servletPath;
    }
    
    public ServletResponse getServletResponse() {
        return this._channel.getResponse();
    }
    
    @Override
    public String changeSessionId() {
        final HttpSession session = this.getSession(false);
        if (session == null) {
            throw new IllegalStateException("No session");
        }
        if (session instanceof AbstractSession) {
            final AbstractSession abstractSession = (AbstractSession)session;
            abstractSession.renewId(this);
            if (this.getRemoteUser() != null) {
                abstractSession.setAttribute("org.eclipse.jetty.security.sessionCreatedSecure", Boolean.TRUE);
            }
            if (abstractSession.isIdChanged()) {
                this._channel.getResponse().addCookie(this._sessionManager.getSessionCookie(abstractSession, this.getContextPath(), this.isSecure()));
            }
        }
        return session.getId();
    }
    
    @Override
    public HttpSession getSession() {
        return this.getSession(true);
    }
    
    @Override
    public HttpSession getSession(final boolean create) {
        if (this._session != null) {
            if (this._sessionManager == null || this._sessionManager.isValid(this._session)) {
                return this._session;
            }
            this._session = null;
        }
        if (!create) {
            return null;
        }
        if (this.getResponse().isCommitted()) {
            throw new IllegalStateException("Response is committed");
        }
        if (this._sessionManager == null) {
            throw new IllegalStateException("No SessionManager");
        }
        this._session = this._sessionManager.newHttpSession(this);
        final HttpCookie cookie = this._sessionManager.getSessionCookie(this._session, this.getContextPath(), this.isSecure());
        if (cookie != null) {
            this._channel.getResponse().addCookie(cookie);
        }
        return this._session;
    }
    
    public SessionManager getSessionManager() {
        return this._sessionManager;
    }
    
    public long getTimeStamp() {
        return this._timeStamp;
    }
    
    public HttpURI getHttpURI() {
        final MetaData.Request metadata = this._metaData;
        return (metadata == null) ? null : metadata.getURI();
    }
    
    public void setHttpURI(final HttpURI uri) {
        final MetaData.Request metadata = this._metaData;
        metadata.setURI(uri);
    }
    
    public UserIdentity getUserIdentity() {
        if (this._authentication instanceof Authentication.Deferred) {
            this.setAuthentication(((Authentication.Deferred)this._authentication).authenticate(this));
        }
        if (this._authentication instanceof Authentication.User) {
            return ((Authentication.User)this._authentication).getUserIdentity();
        }
        return null;
    }
    
    public UserIdentity getResolvedUserIdentity() {
        if (this._authentication instanceof Authentication.User) {
            return ((Authentication.User)this._authentication).getUserIdentity();
        }
        return null;
    }
    
    public UserIdentity.Scope getUserIdentityScope() {
        return this._scope;
    }
    
    @Override
    public Principal getUserPrincipal() {
        if (this._authentication instanceof Authentication.Deferred) {
            this.setAuthentication(((Authentication.Deferred)this._authentication).authenticate(this));
        }
        if (this._authentication instanceof Authentication.User) {
            final UserIdentity user = ((Authentication.User)this._authentication).getUserIdentity();
            return user.getUserPrincipal();
        }
        return null;
    }
    
    public boolean isHandled() {
        return this._handled;
    }
    
    @Override
    public boolean isAsyncStarted() {
        return this.getHttpChannelState().isAsyncStarted();
    }
    
    @Override
    public boolean isAsyncSupported() {
        return this._asyncNotSupportedSource == null;
    }
    
    @Override
    public boolean isRequestedSessionIdFromCookie() {
        return this._requestedSessionId != null && this._requestedSessionIdFromCookie;
    }
    
    @Override
    public boolean isRequestedSessionIdFromUrl() {
        return this._requestedSessionId != null && !this._requestedSessionIdFromCookie;
    }
    
    @Override
    public boolean isRequestedSessionIdFromURL() {
        return this._requestedSessionId != null && !this._requestedSessionIdFromCookie;
    }
    
    @Override
    public boolean isRequestedSessionIdValid() {
        if (this._requestedSessionId == null) {
            return false;
        }
        final HttpSession session = this.getSession(false);
        return session != null && this._sessionManager.getSessionIdManager().getClusterId(this._requestedSessionId).equals(this._sessionManager.getClusterId(session));
    }
    
    @Override
    public boolean isSecure() {
        return this._secure;
    }
    
    public void setSecure(final boolean secure) {
        this._secure = secure;
    }
    
    @Override
    public boolean isUserInRole(final String role) {
        if (this._authentication instanceof Authentication.Deferred) {
            this.setAuthentication(((Authentication.Deferred)this._authentication).authenticate(this));
        }
        return this._authentication instanceof Authentication.User && ((Authentication.User)this._authentication).isUserInRole(this._scope, role);
    }
    
    public HttpSession recoverNewSession(final Object key) {
        if (this._savedNewSessions == null) {
            return null;
        }
        return this._savedNewSessions.get(key);
    }
    
    public void setMetaData(final MetaData.Request request) {
        this._metaData = request;
        this.setMethod(request.getMethod());
        final HttpURI uri = request.getURI();
        String path = uri.getDecodedPath();
        String info;
        if (path == null || path.length() == 0) {
            if (!uri.isAbsolute()) {
                this.setPathInfo("");
                throw new BadMessageException(400, "Bad URI");
            }
            path = "/";
            uri.setPath(path);
            info = path;
        }
        else if (!path.startsWith("/")) {
            if (!"*".equals(path) && !HttpMethod.CONNECT.is(this.getMethod())) {
                this.setPathInfo(path);
                throw new BadMessageException(400, "Bad URI");
            }
            info = path;
        }
        else {
            info = URIUtil.canonicalPath(path);
        }
        if (info == null) {
            this.setPathInfo(path);
            throw new BadMessageException(400, "Bad URI");
        }
        this.setPathInfo(info);
    }
    
    public MetaData.Request getMetaData() {
        return this._metaData;
    }
    
    public boolean hasMetaData() {
        return this._metaData != null;
    }
    
    protected void recycle() {
        this._metaData = null;
        if (this._context != null) {
            throw new IllegalStateException("Request in context!");
        }
        if (this._inputState == 2) {
            try {
                for (int r = this._reader.read(); r != -1; r = this._reader.read()) {}
            }
            catch (Exception e) {
                Request.LOG.ignore(e);
                this._reader = null;
            }
        }
        this._dispatcherType = null;
        this.setAuthentication(Authentication.NOT_CHECKED);
        this.getHttpChannelState().recycle();
        if (this._async != null) {
            this._async.reset();
        }
        this._async = null;
        this._asyncNotSupportedSource = null;
        this._handled = false;
        if (this._attributes != null) {
            this._attributes.clearAttributes();
        }
        this._characterEncoding = null;
        this._contextPath = null;
        if (this._cookies != null) {
            this._cookies.reset();
        }
        this._cookiesExtracted = false;
        this._context = null;
        this._newContext = false;
        this._pathInfo = null;
        this._queryEncoding = null;
        this._requestedSessionId = null;
        this._requestedSessionIdFromCookie = false;
        this._secure = false;
        this._session = null;
        this._sessionManager = null;
        this._scope = null;
        this._servletPath = null;
        this._timeStamp = 0L;
        this._queryParameters = null;
        this._contentParameters = null;
        this._parameters = null;
        this._contentParamsExtracted = false;
        this._inputState = 0;
        if (this._savedNewSessions != null) {
            this._savedNewSessions.clear();
        }
        this._savedNewSessions = null;
        this._multiPartInputStream = null;
        this._remote = null;
        this._input.recycle();
    }
    
    @Override
    public void removeAttribute(final String name) {
        final Object old_value = (this._attributes == null) ? null : this._attributes.getAttribute(name);
        if (this._attributes != null) {
            this._attributes.removeAttribute(name);
        }
        if (old_value != null && !this._requestAttributeListeners.isEmpty()) {
            final ServletRequestAttributeEvent event = new ServletRequestAttributeEvent(this._context, this, name, old_value);
            for (final ServletRequestAttributeListener listener : this._requestAttributeListeners) {
                listener.attributeRemoved(event);
            }
        }
    }
    
    public void removeEventListener(final EventListener listener) {
        this._requestAttributeListeners.remove(listener);
    }
    
    public void saveNewSession(final Object key, final HttpSession session) {
        if (this._savedNewSessions == null) {
            this._savedNewSessions = new HashMap<Object, HttpSession>();
        }
        this._savedNewSessions.put(key, session);
    }
    
    public void setAsyncSupported(final boolean supported, final String source) {
        this._asyncNotSupportedSource = (supported ? null : ((source == null) ? "unknown" : source));
    }
    
    @Override
    public void setAttribute(final String name, final Object value) {
        final Object old_value = (this._attributes == null) ? null : this._attributes.getAttribute(name);
        if ("org.eclipse.jetty.server.Request.queryEncoding".equals(name)) {
            this.setQueryEncoding((value == null) ? null : value.toString());
        }
        else if ("org.eclipse.jetty.server.sendContent".equals(name)) {
            Request.LOG.warn("Deprecated: org.eclipse.jetty.server.sendContent", new Object[0]);
        }
        if (this._attributes == null) {
            this._attributes = new AttributesMap();
        }
        this._attributes.setAttribute(name, value);
        if (!this._requestAttributeListeners.isEmpty()) {
            final ServletRequestAttributeEvent event = new ServletRequestAttributeEvent(this._context, this, name, (old_value == null) ? value : old_value);
            for (final ServletRequestAttributeListener l : this._requestAttributeListeners) {
                if (old_value == null) {
                    l.attributeAdded(event);
                }
                else if (value == null) {
                    l.attributeRemoved(event);
                }
                else {
                    l.attributeReplaced(event);
                }
            }
        }
    }
    
    public void setAttributes(final Attributes attributes) {
        this._attributes = attributes;
    }
    
    public void setAuthentication(final Authentication authentication) {
        this._authentication = authentication;
    }
    
    @Override
    public void setCharacterEncoding(final String encoding) throws UnsupportedEncodingException {
        if (this._inputState != 0) {
            return;
        }
        this._characterEncoding = encoding;
        if (!StringUtil.isUTF8(encoding)) {
            try {
                Charset.forName(encoding);
            }
            catch (UnsupportedCharsetException e) {
                throw new UnsupportedEncodingException(e.getMessage());
            }
        }
    }
    
    public void setCharacterEncodingUnchecked(final String encoding) {
        this._characterEncoding = encoding;
    }
    
    public void setContentType(final String contentType) {
        final MetaData.Request metadata = this._metaData;
        if (metadata != null) {
            metadata.getFields().put(HttpHeader.CONTENT_TYPE, contentType);
        }
    }
    
    public void setContext(final ContextHandler.Context context) {
        this._newContext = (this._context != context);
        this._context = context;
    }
    
    public boolean takeNewContext() {
        final boolean nc = this._newContext;
        this._newContext = false;
        return nc;
    }
    
    public void setContextPath(final String contextPath) {
        this._contextPath = contextPath;
    }
    
    public void setCookies(final Cookie[] cookies) {
        if (this._cookies == null) {
            this._cookies = new CookieCutter();
        }
        this._cookies.setCookies(cookies);
    }
    
    public void setDispatcherType(final DispatcherType type) {
        this._dispatcherType = type;
    }
    
    public void setHandled(final boolean h) {
        this._handled = h;
    }
    
    public void setMethod(final String method) {
        final MetaData.Request metadata = this._metaData;
        if (metadata != null) {
            metadata.setMethod(method);
        }
    }
    
    public void setHttpVersion(final HttpVersion version) {
        final MetaData.Request metadata = this._metaData;
        if (metadata != null) {
            metadata.setHttpVersion(version);
        }
    }
    
    public boolean isHead() {
        final MetaData.Request metadata = this._metaData;
        return metadata != null && HttpMethod.HEAD.is(metadata.getMethod());
    }
    
    public void setPathInfo(final String pathInfo) {
        this._pathInfo = pathInfo;
    }
    
    public void setQueryEncoding(final String queryEncoding) {
        this._queryEncoding = queryEncoding;
    }
    
    public void setQueryString(final String queryString) {
        final MetaData.Request metadata = this._metaData;
        if (metadata != null) {
            metadata.getURI().setQuery(queryString);
        }
        this._queryEncoding = null;
    }
    
    public void setRemoteAddr(final InetSocketAddress addr) {
        this._remote = addr;
    }
    
    public void setRequestedSessionId(final String requestedSessionId) {
        this._requestedSessionId = requestedSessionId;
    }
    
    public void setRequestedSessionIdFromCookie(final boolean requestedSessionIdCookie) {
        this._requestedSessionIdFromCookie = requestedSessionIdCookie;
    }
    
    public void setURIPathQuery(final String requestURI) {
        final MetaData.Request metadata = this._metaData;
        if (metadata != null) {
            metadata.getURI().setPathQuery(requestURI);
        }
    }
    
    public void setScheme(final String scheme) {
        final MetaData.Request metadata = this._metaData;
        if (metadata != null) {
            metadata.getURI().setScheme(scheme);
        }
    }
    
    public void setAuthority(final String host, final int port) {
        final MetaData.Request metadata = this._metaData;
        if (metadata != null) {
            metadata.getURI().setAuthority(host, port);
        }
    }
    
    public void setServletPath(final String servletPath) {
        this._servletPath = servletPath;
    }
    
    public void setSession(final HttpSession session) {
        this._session = session;
    }
    
    public void setSessionManager(final SessionManager sessionManager) {
        this._sessionManager = sessionManager;
    }
    
    public void setTimeStamp(final long ts) {
        this._timeStamp = ts;
    }
    
    public void setUserIdentityScope(final UserIdentity.Scope scope) {
        this._scope = scope;
    }
    
    @Override
    public AsyncContext startAsync() throws IllegalStateException {
        if (this._asyncNotSupportedSource != null) {
            throw new IllegalStateException("!asyncSupported: " + this._asyncNotSupportedSource);
        }
        final HttpChannelState state = this.getHttpChannelState();
        if (this._async == null) {
            this._async = new AsyncContextState(state);
        }
        final AsyncContextEvent event = new AsyncContextEvent(this._context, this._async, state, this, this, this.getResponse());
        state.startAsync(event);
        return this._async;
    }
    
    @Override
    public AsyncContext startAsync(final ServletRequest servletRequest, final ServletResponse servletResponse) throws IllegalStateException {
        if (this._asyncNotSupportedSource != null) {
            throw new IllegalStateException("!asyncSupported: " + this._asyncNotSupportedSource);
        }
        final HttpChannelState state = this.getHttpChannelState();
        if (this._async == null) {
            this._async = new AsyncContextState(state);
        }
        final AsyncContextEvent event = new AsyncContextEvent(this._context, this._async, state, this, servletRequest, servletResponse);
        event.setDispatchContext(this.getServletContext());
        event.setDispatchPath(URIUtil.encodePath(URIUtil.addPaths(this.getServletPath(), this.getPathInfo())));
        state.startAsync(event);
        return this._async;
    }
    
    @Override
    public String toString() {
        return String.format("%s%s%s %s%s@%x", this.getClass().getSimpleName(), this._handled ? "[" : "(", this.getMethod(), this.getHttpURI(), this._handled ? "]" : ")", this.hashCode());
    }
    
    @Override
    public boolean authenticate(final HttpServletResponse response) throws IOException, ServletException {
        if (this._authentication instanceof Authentication.Deferred) {
            this.setAuthentication(((Authentication.Deferred)this._authentication).authenticate(this, response));
            return !(this._authentication instanceof Authentication.ResponseSent);
        }
        response.sendError(401);
        return false;
    }
    
    @Override
    public Part getPart(final String name) throws IOException, ServletException {
        this.getParts();
        return this._multiPartInputStream.getPart(name);
    }
    
    @Override
    public Collection<Part> getParts() throws IOException, ServletException {
        if (this.getContentType() == null || !this.getContentType().startsWith("multipart/form-data")) {
            throw new ServletException("Content-Type != multipart/form-data");
        }
        return this.getParts(null);
    }
    
    private Collection<Part> getParts(final MultiMap<String> params) throws IOException, ServletException {
        if (this._multiPartInputStream == null) {
            this._multiPartInputStream = (MultiPartInputStreamParser)this.getAttribute("org.eclipse.jetty.multiPartInputStream");
        }
        if (this._multiPartInputStream == null) {
            final MultipartConfigElement config = (MultipartConfigElement)this.getAttribute("org.eclipse.jetty.multipartConfig");
            if (config == null) {
                throw new IllegalStateException("No multipart config for servlet");
            }
            this.setAttribute("org.eclipse.jetty.multiPartInputStream", this._multiPartInputStream = new MultiPartInputStreamParser(this.getInputStream(), this.getContentType(), config, (this._context != null) ? ((File)this._context.getAttribute("javax.servlet.context.tempdir")) : null));
            this.setAttribute("org.eclipse.jetty.multiPartContext", this._context);
            final Collection<Part> parts = this._multiPartInputStream.getParts();
            ByteArrayOutputStream os = null;
            for (final Part p : parts) {
                final MultiPartInputStreamParser.MultiPart mp = (MultiPartInputStreamParser.MultiPart)p;
                if (mp.getContentDispositionFilename() == null) {
                    String charset = null;
                    if (mp.getContentType() != null) {
                        charset = MimeTypes.getCharsetFromContentType(mp.getContentType());
                    }
                    try (final InputStream is = mp.getInputStream()) {
                        if (os == null) {
                            os = new ByteArrayOutputStream();
                        }
                        IO.copy(is, os);
                        final String content = new String(os.toByteArray(), (charset == null) ? StandardCharsets.UTF_8 : Charset.forName(charset));
                        if (this._contentParameters == null) {
                            this._contentParameters = ((params == null) ? new MultiMap<String>() : params);
                        }
                        this._contentParameters.add(mp.getName(), content);
                    }
                    os.reset();
                }
            }
        }
        return this._multiPartInputStream.getParts();
    }
    
    @Override
    public void login(final String username, final String password) throws ServletException {
        if (!(this._authentication instanceof Authentication.Deferred)) {
            throw new Authentication.Failed("Authenticated failed for username '" + username + "'. Already authenticated as " + this._authentication);
        }
        this._authentication = ((Authentication.Deferred)this._authentication).login(username, password, this);
        if (this._authentication == null) {
            throw new Authentication.Failed("Authentication failed for username '" + username + "'");
        }
    }
    
    @Override
    public void logout() throws ServletException {
        if (this._authentication instanceof Authentication.User) {
            ((Authentication.User)this._authentication).logout();
        }
        this._authentication = Authentication.UNAUTHENTICATED;
    }
    
    public void mergeQueryParameters(final String oldQuery, final String newQuery, final boolean updateQueryString) {
        MultiMap<String> newQueryParams = null;
        if (newQuery != null) {
            newQueryParams = new MultiMap<String>();
            UrlEncoded.decodeTo(newQuery, newQueryParams, UrlEncoded.ENCODING);
        }
        MultiMap<String> oldQueryParams = this._queryParameters;
        if (oldQueryParams == null && oldQuery != null) {
            oldQueryParams = new MultiMap<String>();
            UrlEncoded.decodeTo(oldQuery, oldQueryParams, this.getQueryEncoding());
        }
        MultiMap<String> mergedQueryParams;
        if (newQueryParams == null || newQueryParams.size() == 0) {
            mergedQueryParams = ((oldQueryParams == null) ? Request.NO_PARAMS : oldQueryParams);
        }
        else if (oldQueryParams == null || oldQueryParams.size() == 0) {
            mergedQueryParams = ((newQueryParams == null) ? Request.NO_PARAMS : newQueryParams);
        }
        else {
            mergedQueryParams = new MultiMap<String>(newQueryParams);
            mergedQueryParams.addAllValues(oldQueryParams);
        }
        this.setQueryParameters(mergedQueryParams);
        this.resetParameters();
        if (updateQueryString) {
            if (newQuery == null) {
                this.setQueryString(oldQuery);
            }
            else if (oldQuery == null) {
                this.setQueryString(newQuery);
            }
            else {
                final StringBuilder mergedQuery = new StringBuilder();
                if (newQuery != null) {
                    mergedQuery.append(newQuery);
                }
                for (final Map.Entry<String, List<String>> entry : mergedQueryParams.entrySet()) {
                    if (newQueryParams != null && newQueryParams.containsKey(entry.getKey())) {
                        continue;
                    }
                    for (final String value : entry.getValue()) {
                        if (mergedQuery.length() > 0) {
                            mergedQuery.append("&");
                        }
                        URIUtil.encodePath(mergedQuery, entry.getKey());
                        mergedQuery.append('=');
                        URIUtil.encodePath(mergedQuery, value);
                    }
                }
                this.setQueryString(mergedQuery.toString());
            }
        }
    }
    
    @Override
    public <T extends HttpUpgradeHandler> T upgrade(final Class<T> handlerClass) throws IOException, ServletException {
        throw new ServletException("HttpServletRequest.upgrade() not supported in Jetty");
    }
    
    static {
        LOG = Log.getLogger(Request.class);
        __defaultLocale = Collections.singleton(Locale.getDefault());
        NO_PARAMS = new MultiMap<String>();
    }
}
