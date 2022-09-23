// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.yarn.webapp;

import java.util.Collection;
import java.util.EnumSet;
import org.slf4j.LoggerFactory;
import com.google.common.base.Preconditions;
import org.apache.commons.lang.StringUtils;
import java.util.Map;
import org.apache.hadoop.yarn.util.StringHelper;
import java.lang.reflect.Method;
import java.util.List;
import com.google.common.collect.Maps;
import java.util.TreeMap;
import java.util.regex.Pattern;
import com.google.common.base.CharMatcher;
import com.google.common.collect.ImmutableList;
import org.slf4j.Logger;
import org.apache.hadoop.classification.InterfaceAudience;

@InterfaceAudience.LimitedPrivate({ "YARN", "MapReduce" })
class Router
{
    static final Logger LOG;
    static final ImmutableList<String> EMPTY_LIST;
    static final CharMatcher SLASH;
    static final Pattern controllerRe;
    Class<?> hostClass;
    final TreeMap<String, Dest> routes;
    
    Router() {
        this.routes = Maps.newTreeMap();
    }
    
    synchronized Dest add(final WebApp.HTTP httpMethod, final String path, final Class<? extends Controller> cls, final String action, final List<String> names) {
        Router.LOG.debug("adding {}({})->{}#{}", path, names, cls, action);
        final Dest dest = this.addController(httpMethod, path, cls, action, names);
        this.addDefaultView(dest);
        return dest;
    }
    
    private Dest addController(final WebApp.HTTP httpMethod, final String path, final Class<? extends Controller> cls, final String action, final List<String> names) {
        try {
            final Method method = cls.getMethod(action, (Class<?>[])null);
            Dest dest = this.routes.get(path);
            if (dest == null) {
                method.setAccessible(true);
                dest = new Dest(path, method, cls, names, httpMethod);
                this.routes.put(path, dest);
                return dest;
            }
            dest.methods.add(httpMethod);
            return dest;
        }
        catch (NoSuchMethodException nsme) {
            throw new WebAppException(action + "() not found in " + cls);
        }
        catch (SecurityException se) {
            throw new WebAppException("Security exception thrown for " + action + "() in " + cls);
        }
    }
    
    private void addDefaultView(final Dest dest) {
        String controllerName = dest.controllerClass.getSimpleName();
        if (controllerName.endsWith("Controller")) {
            controllerName = controllerName.substring(0, controllerName.length() - 10);
        }
        dest.defaultViewClass = this.find(View.class, dest.controllerClass.getPackage().getName(), StringHelper.join(controllerName + "View"));
    }
    
    void setHostClass(final Class<?> cls) {
        this.hostClass = cls;
    }
    
    synchronized Dest resolve(final String httpMethod, final String path) {
        final WebApp.HTTP method = WebApp.HTTP.valueOf(httpMethod);
        final Dest dest = this.lookupRoute(method, path);
        if (dest == null) {
            return this.resolveDefault(method, path);
        }
        return dest;
    }
    
    private Dest lookupRoute(final WebApp.HTTP method, final String path) {
        String key = path;
        while (true) {
            Dest dest = this.routes.get(key);
            if (dest != null && methodAllowed(method, dest)) {
                if (key == path) {
                    Router.LOG.debug("exact match for {}: {}", key, dest.action);
                    return dest;
                }
                if (isGoodMatch(dest, path)) {
                    Router.LOG.debug("prefix match2 for {}: {}", key, dest.action);
                    return dest;
                }
                return this.resolveAction(method, dest, path);
            }
            else {
                final Map.Entry<String, Dest> lower = this.routes.lowerEntry(key);
                if (lower == null) {
                    return null;
                }
                dest = lower.getValue();
                if (prefixMatches(dest, path)) {
                    if (methodAllowed(method, dest)) {
                        if (isGoodMatch(dest, path)) {
                            Router.LOG.debug("prefix match for {}: {}", lower.getKey(), dest.action);
                            return dest;
                        }
                        return this.resolveAction(method, dest, path);
                    }
                    else {
                        final int slashPos = key.lastIndexOf(47);
                        key = ((slashPos > 0) ? path.substring(0, slashPos) : "/");
                    }
                }
                else {
                    key = "/";
                }
            }
        }
    }
    
    static boolean methodAllowed(final WebApp.HTTP method, final Dest dest) {
        return dest.methods.contains(method) || (dest.methods.size() == 1 && dest.methods.contains(WebApp.HTTP.GET));
    }
    
    static boolean prefixMatches(final Dest dest, final String path) {
        Router.LOG.debug("checking prefix {}{} for path: {}", dest.prefix, dest.pathParams, path);
        if (!path.startsWith(dest.prefix)) {
            return false;
        }
        final int prefixLen = dest.prefix.length();
        return prefixLen <= 1 || path.length() <= prefixLen || path.charAt(prefixLen) == '/';
    }
    
    static boolean isGoodMatch(final Dest dest, final String path) {
        if (Router.SLASH.countIn(dest.prefix) > 1) {
            return true;
        }
        if (dest.prefix.length() == 1) {
            return dest.pathParams.size() > 0 && !maybeController(path);
        }
        return dest.pathParams.size() > 0 || (path.endsWith("/") && Router.SLASH.countIn(path) == 2);
    }
    
    static boolean maybeController(final String path) {
        return Router.controllerRe.matcher(path).matches();
    }
    
    private Dest resolveDefault(final WebApp.HTTP method, final String path) {
        final List<String> parts = WebApp.parseRoute(path);
        final String controller = parts.get(1);
        final String action = parts.get(2);
        Class<? extends Controller> cls = this.find(Controller.class, StringHelper.join(controller, "Controller"));
        if (cls == null) {
            cls = this.find(Controller.class, controller);
        }
        if (cls == null) {
            throw new WebAppException(StringHelper.join(path, ": controller for ", controller, " not found"));
        }
        return this.add(method, this.defaultPrefix(controller, action), cls, action, null);
    }
    
    private String defaultPrefix(final String controller, final String action) {
        if (controller.equals("default") && action.equals("index")) {
            return "/";
        }
        if (action.equals("index")) {
            return StringHelper.join('/', controller);
        }
        return StringHelper.pjoin("", controller, action);
    }
    
    private <T> Class<? extends T> find(final Class<T> cls, final String cname) {
        final String pkg = this.hostClass.getPackage().getName();
        return this.find(cls, pkg, cname);
    }
    
    private <T> Class<? extends T> find(final Class<T> cls, final String pkg, final String cname) {
        final String name = StringUtils.capitalize(cname);
        Class<? extends T> found = this.load(cls, StringHelper.djoin(pkg, name));
        if (found == null) {
            found = this.load(cls, StringHelper.djoin(pkg, "webapp", name));
        }
        if (found == null) {
            found = this.load(cls, StringHelper.join(this.hostClass.getName(), '$', name));
        }
        return found;
    }
    
    private <T> Class<? extends T> load(final Class<T> cls, final String className) {
        Router.LOG.debug("trying: {}", className);
        try {
            final Class<?> found = Class.forName(className);
            if (cls.isAssignableFrom(found)) {
                Router.LOG.debug("found {}", className);
                return (Class<? extends T>)found;
            }
            Router.LOG.warn("found a {} but it's not a {}", className, cls.getName());
        }
        catch (ClassNotFoundException ex) {}
        return null;
    }
    
    private Dest resolveAction(final WebApp.HTTP method, final Dest dest, final String path) {
        if (dest.prefix.length() == 1) {
            return null;
        }
        Preconditions.checkState(!isGoodMatch(dest, path), (Object)dest.prefix);
        Preconditions.checkState(Router.SLASH.countIn(path) > 1, (Object)path);
        final List<String> parts = WebApp.parseRoute(path);
        final String controller = parts.get(1);
        final String action = parts.get(2);
        return this.add(method, StringHelper.pjoin("", controller, action), dest.controllerClass, action, null);
    }
    
    static {
        LOG = LoggerFactory.getLogger(Router.class);
        EMPTY_LIST = ImmutableList.of();
        SLASH = CharMatcher.is('/');
        controllerRe = Pattern.compile("^/[A-Za-z_]\\w*(?:/.*)?");
    }
    
    static class Dest
    {
        final String prefix;
        final ImmutableList<String> pathParams;
        final Method action;
        final Class<? extends Controller> controllerClass;
        Class<? extends View> defaultViewClass;
        final EnumSet<WebApp.HTTP> methods;
        
        Dest(final String path, final Method method, final Class<? extends Controller> cls, final List<String> pathParams, final WebApp.HTTP httpMethod) {
            this.prefix = Preconditions.checkNotNull(path);
            this.action = Preconditions.checkNotNull(method);
            this.controllerClass = Preconditions.checkNotNull(cls);
            this.pathParams = ((pathParams != null) ? ImmutableList.copyOf((Collection<? extends String>)pathParams) : Router.EMPTY_LIST);
            this.methods = EnumSet.of(httpMethod);
        }
    }
}
