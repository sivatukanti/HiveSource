// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.yarn.webapp;

import org.slf4j.LoggerFactory;
import java.util.TimerTask;
import java.util.Timer;
import com.google.common.collect.Iterables;
import com.google.common.base.Preconditions;
import java.io.IOException;
import javax.servlet.ServletException;
import org.apache.hadoop.yarn.webapp.view.ErrorPage;
import javax.servlet.http.Cookie;
import org.apache.hadoop.http.HtmlQuoting;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletRequest;
import com.google.inject.Inject;
import com.google.inject.Injector;
import org.slf4j.Logger;
import com.google.inject.Singleton;
import org.apache.hadoop.classification.InterfaceAudience;
import javax.servlet.http.HttpServlet;

@InterfaceAudience.LimitedPrivate({ "YARN", "MapReduce" })
@Singleton
public class Dispatcher extends HttpServlet
{
    private static final long serialVersionUID = 1L;
    static final Logger LOG;
    static final String ERROR_COOKIE = "last-error";
    static final String STATUS_COOKIE = "last-status";
    private final transient Injector injector;
    private final transient Router router;
    private final transient WebApp webApp;
    private volatile boolean devMode;
    
    @Inject
    Dispatcher(final WebApp webApp, final Injector injector, final Router router) {
        this.devMode = false;
        this.webApp = webApp;
        this.injector = injector;
        this.router = router;
    }
    
    public void doOptions(final HttpServletRequest req, final HttpServletResponse res) {
        res.setHeader("Allow", "GET, POST");
    }
    
    public void service(final HttpServletRequest req, final HttpServletResponse res) throws ServletException, IOException {
        res.setCharacterEncoding("UTF-8");
        String uri = HtmlQuoting.quoteHtmlChars(req.getRequestURI());
        if (uri == null) {
            uri = "/";
        }
        if (this.devMode && uri.equals("/__stop")) {
            res.setStatus(204);
            Dispatcher.LOG.info("dev mode restart requested");
            this.prepareToExit();
            return;
        }
        if (uri.equals("/")) {
            final String redirectPath = this.webApp.getRedirectPath();
            if (redirectPath != null && !redirectPath.isEmpty()) {
                res.sendRedirect(redirectPath);
                return;
            }
        }
        final String method = req.getMethod();
        if (method.equals("OPTIONS")) {
            this.doOptions(req, res);
            return;
        }
        if (method.equals("TRACE")) {
            this.doTrace(req, res);
            return;
        }
        if (method.equals("HEAD")) {
            this.doGet(req, res);
            return;
        }
        String pathInfo = req.getPathInfo();
        if (pathInfo == null) {
            pathInfo = "/";
        }
        final Controller.RequestContext rc = this.injector.getInstance(Controller.RequestContext.class);
        if (this.setCookieParams(rc, req) > 0) {
            final Cookie ec = rc.cookies().get("last-error");
            if (ec != null) {
                rc.setStatus(Integer.parseInt(rc.cookies().get("last-status").getValue()));
                removeErrorCookies(res, uri);
                rc.set("error.details", ec.getValue());
                this.render(ErrorPage.class);
                return;
            }
        }
        rc.prefix = this.webApp.name();
        Router.Dest dest = null;
        try {
            dest = this.router.resolve(method, pathInfo);
        }
        catch (WebAppException e) {
            rc.error = e;
            if (!e.getMessage().contains("not found")) {
                rc.setStatus(500);
                this.render(ErrorPage.class);
                return;
            }
        }
        if (dest == null) {
            rc.setStatus(404);
            this.render(ErrorPage.class);
            return;
        }
        rc.devMode = this.devMode;
        this.setMoreParams(rc, pathInfo, dest);
        final Controller controller = this.injector.getInstance(dest.controllerClass);
        try {
            dest.action.invoke(controller, (Object[])null);
            if (!rc.rendered) {
                if (dest.defaultViewClass != null) {
                    this.render(dest.defaultViewClass);
                }
                else if (rc.status == 200) {
                    throw new IllegalStateException("No view rendered for 200");
                }
            }
        }
        catch (Exception e2) {
            Dispatcher.LOG.error("error handling URI: " + uri, e2);
            redirectToErrorPage(res, e2, uri, this.devMode);
        }
    }
    
    public static void redirectToErrorPage(final HttpServletResponse res, final Throwable e, final String path, final boolean devMode) {
        final String st = devMode ? ErrorPage.toStackTrace(e, 3072) : "See logs for stack trace";
        res.setStatus(302);
        Cookie cookie = new Cookie("last-status", String.valueOf(500));
        cookie.setPath(path);
        res.addCookie(cookie);
        cookie = new Cookie("last-error", st);
        cookie.setPath(path);
        res.addCookie(cookie);
        res.setHeader("Location", path);
    }
    
    public static void removeErrorCookies(final HttpServletResponse res, final String path) {
        removeCookie(res, "last-error", path);
        removeCookie(res, "last-status", path);
    }
    
    public static void removeCookie(final HttpServletResponse res, final String name, final String path) {
        Dispatcher.LOG.debug("removing cookie {} on {}", name, path);
        final Cookie c = new Cookie(name, "");
        c.setMaxAge(0);
        c.setPath(path);
        res.addCookie(c);
    }
    
    private void render(final Class<? extends View> cls) {
        this.injector.getInstance(cls).render();
    }
    
    private void setMoreParams(final Controller.RequestContext rc, final String pathInfo, final Router.Dest dest) {
        Preconditions.checkState(pathInfo.startsWith(dest.prefix), (Object)"prefix should match");
        if (dest.pathParams.size() == 0 || dest.prefix.length() == pathInfo.length()) {
            return;
        }
        final String[] parts = Iterables.toArray(WebApp.pathSplitter.split(pathInfo.substring(dest.prefix.length())), String.class);
        Dispatcher.LOG.debug("parts={}, params={}", (Object)parts, dest.pathParams);
        for (int i = 0; i < dest.pathParams.size() && i < parts.length; ++i) {
            final String key = dest.pathParams.get(i);
            if (key.charAt(0) == ':') {
                rc.moreParams().put(key.substring(1), parts[i]);
            }
        }
    }
    
    private int setCookieParams(final Controller.RequestContext rc, final HttpServletRequest req) {
        final Cookie[] cookies = req.getCookies();
        if (cookies != null) {
            for (final Cookie cookie : cookies) {
                rc.cookies().put(cookie.getName(), cookie);
            }
            return cookies.length;
        }
        return 0;
    }
    
    public void setDevMode(final boolean choice) {
        this.devMode = choice;
    }
    
    private void prepareToExit() {
        Preconditions.checkState(this.devMode, (Object)"only in dev mode");
        new Timer("webapp exit", true).schedule(new TimerTask() {
            @Override
            public void run() {
                Dispatcher.LOG.info("WebAppp /{} exiting...", Dispatcher.this.webApp.name());
                Dispatcher.this.webApp.stop();
                System.exit(0);
            }
        }, 18L);
    }
    
    static {
        LOG = LoggerFactory.getLogger(Dispatcher.class);
    }
}
