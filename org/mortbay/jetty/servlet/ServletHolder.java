// 
// Decompiled by Procyon v0.5.36
// 

package org.mortbay.jetty.servlet;

import java.util.Stack;
import java.util.Enumeration;
import javax.servlet.ServletContext;
import java.io.IOException;
import org.mortbay.jetty.Request;
import org.mortbay.jetty.HttpConnection;
import javax.servlet.ServletResponse;
import javax.servlet.ServletRequest;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import java.security.Principal;
import org.mortbay.log.Log;
import org.mortbay.jetty.handler.ContextHandler;
import org.mortbay.jetty.security.SecurityHandler;
import java.util.HashMap;
import javax.servlet.SingleThreadModel;
import javax.servlet.UnavailableException;
import javax.servlet.Servlet;
import org.mortbay.jetty.security.UserRealm;
import java.util.Map;

public class ServletHolder extends Holder implements Comparable
{
    private int _initOrder;
    private boolean _initOnStartup;
    private Map _roleMap;
    private String _forcedPath;
    private String _runAs;
    private UserRealm _realm;
    private transient Servlet _servlet;
    private transient Config _config;
    private transient long _unavailable;
    private transient UnavailableException _unavailableEx;
    
    public ServletHolder() {
        this._initOnStartup = false;
    }
    
    public ServletHolder(final Servlet servlet) {
        this._initOnStartup = false;
        this.setServlet(servlet);
    }
    
    public ServletHolder(final Class servlet) {
        super(servlet);
        this._initOnStartup = false;
    }
    
    public UnavailableException getUnavailableException() {
        return this._unavailableEx;
    }
    
    public synchronized void setServlet(final Servlet servlet) {
        if (servlet == null || servlet instanceof SingleThreadModel) {
            throw new IllegalArgumentException();
        }
        this._extInstance = true;
        this._servlet = servlet;
        this.setHeldClass(servlet.getClass());
        if (this.getName() == null) {
            this.setName(servlet.getClass().getName() + "-" + super.hashCode());
        }
    }
    
    public int getInitOrder() {
        return this._initOrder;
    }
    
    public void setInitOrder(final int order) {
        this._initOnStartup = true;
        this._initOrder = order;
    }
    
    public int compareTo(final Object o) {
        if (!(o instanceof ServletHolder)) {
            return 1;
        }
        final ServletHolder sh = (ServletHolder)o;
        if (sh == this) {
            return 0;
        }
        if (sh._initOrder < this._initOrder) {
            return 1;
        }
        if (sh._initOrder > this._initOrder) {
            return -1;
        }
        int c = (this._className != null && sh._className != null) ? this._className.compareTo(sh._className) : 0;
        if (c == 0) {
            c = this._name.compareTo(sh._name);
        }
        if (c == 0) {
            c = ((this.hashCode() > o.hashCode()) ? 1 : -1);
        }
        return c;
    }
    
    public boolean equals(final Object o) {
        return this.compareTo(o) == 0;
    }
    
    public int hashCode() {
        return (this._name == null) ? System.identityHashCode(this) : this._name.hashCode();
    }
    
    public synchronized void setUserRoleLink(final String name, final String link) {
        if (this._roleMap == null) {
            this._roleMap = new HashMap();
        }
        this._roleMap.put(name, link);
    }
    
    public String getUserRoleLink(final String name) {
        if (this._roleMap == null) {
            return name;
        }
        final String link = this._roleMap.get(name);
        return (link == null) ? name : link;
    }
    
    public Map getRoleMap() {
        return this._roleMap;
    }
    
    public void setRunAs(final String role) {
        this._runAs = role;
    }
    
    public String getRunAs() {
        return this._runAs;
    }
    
    public String getForcedPath() {
        return this._forcedPath;
    }
    
    public void setForcedPath(final String forcedPath) {
        this._forcedPath = forcedPath;
    }
    
    public void doStart() throws Exception {
        this._unavailable = 0L;
        try {
            super.doStart();
            this.checkServletType();
        }
        catch (UnavailableException ue) {
            this.makeUnavailable(ue);
        }
        this._config = new Config();
        if (this._runAs != null) {
            this._realm = ((SecurityHandler)ContextHandler.getCurrentContext().getContextHandler().getChildHandlerByClass(SecurityHandler.class)).getUserRealm();
        }
        if (SingleThreadModel.class.isAssignableFrom(this._class)) {
            this._servlet = new SingleThreadedWrapper();
        }
        if (!this._extInstance) {
            if (!this._initOnStartup) {
                return;
            }
        }
        try {
            this.initServlet();
        }
        catch (Exception e) {
            if (!this._servletHandler.isStartWithUnavailable()) {
                throw e;
            }
            Log.ignore(e);
        }
    }
    
    public void doStop() {
        Principal user = null;
        try {
            if (this._runAs != null && this._realm != null) {
                user = this._realm.pushRole(null, this._runAs);
            }
            if (this._servlet != null) {
                try {
                    this.destroyInstance(this._servlet);
                }
                catch (Exception e) {
                    Log.warn(e);
                }
            }
            if (!this._extInstance) {
                this._servlet = null;
            }
            this._config = null;
        }
        finally {
            super.doStop();
            if (this._runAs != null && this._realm != null && user != null) {
                this._realm.popRole(user);
            }
        }
    }
    
    public void destroyInstance(final Object o) throws Exception {
        if (o == null) {
            return;
        }
        final Servlet servlet = (Servlet)o;
        servlet.destroy();
        this.getServletHandler().customizeServletDestroy(servlet);
    }
    
    public synchronized Servlet getServlet() throws ServletException {
        if (this._unavailable != 0L) {
            if (this._unavailable < 0L || (this._unavailable > 0L && System.currentTimeMillis() < this._unavailable)) {
                throw this._unavailableEx;
            }
            this._unavailable = 0L;
            this._unavailableEx = null;
        }
        if (this._servlet == null) {
            this.initServlet();
        }
        return this._servlet;
    }
    
    public Servlet getServletInstance() {
        return this._servlet;
    }
    
    public void checkServletType() throws UnavailableException {
        if (!Servlet.class.isAssignableFrom(this._class)) {
            throw new UnavailableException("Servlet " + this._class + " is not a javax.servlet.Servlet");
        }
    }
    
    public boolean isAvailable() {
        if (this.isStarted() && this._unavailable == 0L) {
            return true;
        }
        try {
            this.getServlet();
        }
        catch (Exception e) {
            Log.ignore(e);
        }
        return this.isStarted() && this._unavailable == 0L;
    }
    
    private void makeUnavailable(final UnavailableException e) {
        if (this._unavailableEx == e && this._unavailable != 0L) {
            return;
        }
        this._servletHandler.getServletContext().log("Unavailable " + e);
        this._unavailableEx = e;
        this._unavailable = -1L;
        if (e.isPermanent()) {
            this._unavailable = -1L;
        }
        else if (this._unavailableEx.getUnavailableSeconds() > 0) {
            this._unavailable = System.currentTimeMillis() + 1000 * this._unavailableEx.getUnavailableSeconds();
        }
        else {
            this._unavailable = System.currentTimeMillis() + 5000L;
        }
    }
    
    private void makeUnavailable(final Throwable e) {
        if (e instanceof UnavailableException) {
            this.makeUnavailable((UnavailableException)e);
        }
        else {
            this._servletHandler.getServletContext().log("unavailable", e);
            this._unavailableEx = new UnavailableException(e.toString(), -1);
            this._unavailable = -1L;
        }
    }
    
    private void initServlet() throws ServletException {
        Principal user = null;
        try {
            if (this._servlet == null) {
                this._servlet = (Servlet)this.newInstance();
            }
            if (this._config == null) {
                this._config = new Config();
            }
            if (!(this._servlet instanceof SingleThreadedWrapper)) {
                this._servlet = this.getServletHandler().customizeServlet(this._servlet);
            }
            if (this._runAs != null && this._realm != null) {
                user = this._realm.pushRole(null, this._runAs);
            }
            this._servlet.init(this._config);
        }
        catch (UnavailableException e) {
            this.makeUnavailable(e);
            this._servlet = null;
            this._config = null;
            throw e;
        }
        catch (ServletException e2) {
            this.makeUnavailable((e2.getCause() == null) ? e2 : e2.getCause());
            this._servlet = null;
            this._config = null;
            throw e2;
        }
        catch (Exception e3) {
            this.makeUnavailable(e3);
            this._servlet = null;
            this._config = null;
            throw new ServletException(e3);
        }
        finally {
            if (this._runAs != null && this._realm != null && user != null) {
                this._realm.popRole(user);
            }
        }
    }
    
    public void handle(final ServletRequest request, final ServletResponse response) throws ServletException, UnavailableException, IOException {
        if (this._class == null) {
            throw new UnavailableException("Servlet Not Initialized");
        }
        Servlet servlet = this._servlet;
        synchronized (this) {
            if (this._unavailable != 0L || !this._initOnStartup) {
                servlet = this.getServlet();
            }
            if (servlet == null) {
                throw new UnavailableException("Could not instantiate " + this._class);
            }
        }
        boolean servlet_error = true;
        Principal user = null;
        Request base_request = null;
        try {
            if (this._forcedPath != null) {
                request.setAttribute("org.apache.catalina.jsp_file", this._forcedPath);
            }
            if (this._runAs != null && this._realm != null) {
                base_request = HttpConnection.getCurrentConnection().getRequest();
                user = this._realm.pushRole(base_request.getUserPrincipal(), this._runAs);
                base_request.setUserPrincipal(user);
            }
            servlet.service(request, response);
            servlet_error = false;
        }
        catch (UnavailableException e) {
            this.makeUnavailable(e);
            throw this._unavailableEx;
        }
        finally {
            if (this._runAs != null && this._realm != null && user != null && base_request != null) {
                user = this._realm.popRole(user);
                base_request.setUserPrincipal(user);
            }
            if (servlet_error) {
                request.setAttribute("javax.servlet.error.servlet_name", this.getName());
            }
        }
    }
    
    class Config implements ServletConfig
    {
        public String getServletName() {
            return ServletHolder.this.getName();
        }
        
        public ServletContext getServletContext() {
            return ServletHolder.this._servletHandler.getServletContext();
        }
        
        public String getInitParameter(final String param) {
            return ServletHolder.this.getInitParameter(param);
        }
        
        public Enumeration getInitParameterNames() {
            return ServletHolder.this.getInitParameterNames();
        }
    }
    
    private class SingleThreadedWrapper implements Servlet
    {
        Stack _stack;
        
        private SingleThreadedWrapper() {
            this._stack = new Stack();
        }
        
        public void destroy() {
            synchronized (this) {
                while (this._stack.size() > 0) {
                    try {
                        this._stack.pop().destroy();
                    }
                    catch (Exception e) {
                        Log.warn(e);
                    }
                }
            }
        }
        
        public ServletConfig getServletConfig() {
            return ServletHolder.this._config;
        }
        
        public String getServletInfo() {
            return null;
        }
        
        public void init(final ServletConfig config) throws ServletException {
            synchronized (this) {
                if (this._stack.size() == 0) {
                    try {
                        Servlet s = (Servlet)ServletHolder.this.newInstance();
                        s = ServletHolder.this.getServletHandler().customizeServlet(s);
                        s.init(config);
                        this._stack.push(s);
                    }
                    catch (ServletException e) {
                        throw e;
                    }
                    catch (Exception e2) {
                        throw new ServletException(e2);
                    }
                }
            }
        }
        
        public void service(final ServletRequest req, final ServletResponse res) throws ServletException, IOException {
            Servlet s;
            synchronized (this) {
                if (this._stack.size() > 0) {
                    s = this._stack.pop();
                }
                else {
                    try {
                        s = (Servlet)ServletHolder.this.newInstance();
                        s = ServletHolder.this.getServletHandler().customizeServlet(s);
                        s.init(ServletHolder.this._config);
                    }
                    catch (ServletException e) {
                        throw e;
                    }
                    catch (IOException e2) {
                        throw e2;
                    }
                    catch (Exception e3) {
                        throw new ServletException(e3);
                    }
                }
            }
            try {
                s.service(req, res);
            }
            finally {
                synchronized (this) {
                    this._stack.push(s);
                }
            }
        }
    }
}
