// 
// Decompiled by Procyon v0.5.36
// 

package org.eclipse.jetty.webapp;

import org.eclipse.jetty.util.log.Log;
import org.eclipse.jetty.servlet.ListenerHolder;
import java.util.EventListener;
import org.eclipse.jetty.security.ConstraintAware;
import org.eclipse.jetty.security.ConstraintMapping;
import org.eclipse.jetty.util.security.Constraint;
import javax.servlet.descriptor.JspPropertyGroupDescriptor;
import javax.servlet.descriptor.TaglibDescriptor;
import javax.servlet.descriptor.JspConfigDescriptor;
import org.eclipse.jetty.servlet.ServletContextHandler;
import java.util.EnumSet;
import javax.servlet.DispatcherType;
import java.util.ListIterator;
import org.eclipse.jetty.http.pathmap.ServletPathSpec;
import org.eclipse.jetty.util.ArrayUtil;
import org.eclipse.jetty.servlet.ErrorPageErrorHandler;
import java.util.Set;
import javax.servlet.SessionTrackingMode;
import java.util.HashSet;
import org.eclipse.jetty.server.session.AbstractSessionManager;
import java.math.BigDecimal;
import java.util.Iterator;
import javax.servlet.MultipartConfigElement;
import java.util.Locale;
import org.eclipse.jetty.util.Loader;
import org.eclipse.jetty.servlet.BaseHolder;
import org.eclipse.jetty.xml.XmlParser;
import java.util.Collection;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.HashMap;
import org.eclipse.jetty.servlet.ServletMapping;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.servlet.FilterMapping;
import java.util.List;
import org.eclipse.jetty.servlet.FilterHolder;
import java.util.Map;
import org.eclipse.jetty.util.log.Logger;

public class StandardDescriptorProcessor extends IterativeDescriptorProcessor
{
    private static final Logger LOG;
    public static final String STANDARD_PROCESSOR = "org.eclipse.jetty.standardDescriptorProcessor";
    final Map<String, FilterHolder> _filterHolderMap;
    final List<FilterHolder> _filterHolders;
    final List<FilterMapping> _filterMappings;
    final Map<String, ServletHolder> _servletHolderMap;
    final List<ServletHolder> _servletHolders;
    final List<ServletMapping> _servletMappings;
    
    public StandardDescriptorProcessor() {
        this._filterHolderMap = new HashMap<String, FilterHolder>();
        this._filterHolders = new ArrayList<FilterHolder>();
        this._filterMappings = new ArrayList<FilterMapping>();
        this._servletHolderMap = new HashMap<String, ServletHolder>();
        this._servletHolders = new ArrayList<ServletHolder>();
        this._servletMappings = new ArrayList<ServletMapping>();
        try {
            this.registerVisitor("context-param", this.getClass().getMethod("visitContextParam", StandardDescriptorProcessor.__signature));
            this.registerVisitor("display-name", this.getClass().getMethod("visitDisplayName", StandardDescriptorProcessor.__signature));
            this.registerVisitor("servlet", this.getClass().getMethod("visitServlet", StandardDescriptorProcessor.__signature));
            this.registerVisitor("servlet-mapping", this.getClass().getMethod("visitServletMapping", StandardDescriptorProcessor.__signature));
            this.registerVisitor("session-config", this.getClass().getMethod("visitSessionConfig", StandardDescriptorProcessor.__signature));
            this.registerVisitor("mime-mapping", this.getClass().getMethod("visitMimeMapping", StandardDescriptorProcessor.__signature));
            this.registerVisitor("welcome-file-list", this.getClass().getMethod("visitWelcomeFileList", StandardDescriptorProcessor.__signature));
            this.registerVisitor("locale-encoding-mapping-list", this.getClass().getMethod("visitLocaleEncodingList", StandardDescriptorProcessor.__signature));
            this.registerVisitor("error-page", this.getClass().getMethod("visitErrorPage", StandardDescriptorProcessor.__signature));
            this.registerVisitor("taglib", this.getClass().getMethod("visitTagLib", StandardDescriptorProcessor.__signature));
            this.registerVisitor("jsp-config", this.getClass().getMethod("visitJspConfig", StandardDescriptorProcessor.__signature));
            this.registerVisitor("security-constraint", this.getClass().getMethod("visitSecurityConstraint", StandardDescriptorProcessor.__signature));
            this.registerVisitor("login-config", this.getClass().getMethod("visitLoginConfig", StandardDescriptorProcessor.__signature));
            this.registerVisitor("security-role", this.getClass().getMethod("visitSecurityRole", StandardDescriptorProcessor.__signature));
            this.registerVisitor("filter", this.getClass().getMethod("visitFilter", StandardDescriptorProcessor.__signature));
            this.registerVisitor("filter-mapping", this.getClass().getMethod("visitFilterMapping", StandardDescriptorProcessor.__signature));
            this.registerVisitor("listener", this.getClass().getMethod("visitListener", StandardDescriptorProcessor.__signature));
            this.registerVisitor("distributable", this.getClass().getMethod("visitDistributable", StandardDescriptorProcessor.__signature));
            this.registerVisitor("deny-uncovered-http-methods", this.getClass().getMethod("visitDenyUncoveredHttpMethods", StandardDescriptorProcessor.__signature));
        }
        catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }
    
    @Override
    public void start(final WebAppContext context, final Descriptor descriptor) {
        for (final FilterHolder h : context.getServletHandler().getFilters()) {
            this._filterHolderMap.put(h.getName(), h);
            this._filterHolders.add(h);
        }
        if (context.getServletHandler().getFilterMappings() != null) {
            this._filterMappings.addAll(Arrays.asList(context.getServletHandler().getFilterMappings()));
        }
        for (final ServletHolder h2 : context.getServletHandler().getServlets()) {
            this._servletHolderMap.put(h2.getName(), h2);
            this._servletHolders.add(h2);
        }
        if (context.getServletHandler().getServletMappings() != null) {
            this._servletMappings.addAll(Arrays.asList(context.getServletHandler().getServletMappings()));
        }
    }
    
    @Override
    public void end(final WebAppContext context, final Descriptor descriptor) {
        context.getServletHandler().setFilters(this._filterHolders.toArray(new FilterHolder[this._filterHolderMap.size()]));
        context.getServletHandler().setServlets(this._servletHolders.toArray(new ServletHolder[this._servletHolderMap.size()]));
        context.getServletHandler().setFilterMappings(this._filterMappings.toArray(new FilterMapping[this._filterMappings.size()]));
        context.getServletHandler().setServletMappings(this._servletMappings.toArray(new ServletMapping[this._servletMappings.size()]));
        this._filterHolderMap.clear();
        this._filterHolders.clear();
        this._filterMappings.clear();
        this._servletHolderMap.clear();
        this._servletHolders.clear();
        this._servletMappings.clear();
    }
    
    public void visitContextParam(final WebAppContext context, final Descriptor descriptor, final XmlParser.Node node) {
        final String name = node.getString("param-name", false, true);
        final String value = node.getString("param-value", false, true);
        switch (context.getMetaData().getOrigin("context-param." + name)) {
            case NotSet: {
                context.getInitParams().put(name, value);
                context.getMetaData().setOrigin("context-param." + name, descriptor);
                break;
            }
            case WebXml:
            case WebDefaults:
            case WebOverride: {
                if (!(descriptor instanceof FragmentDescriptor)) {
                    context.getInitParams().put(name, value);
                    context.getMetaData().setOrigin("context-param." + name, descriptor);
                    break;
                }
                break;
            }
            case WebFragment: {
                if (descriptor instanceof FragmentDescriptor && !context.getInitParams().get(name).equals(value)) {
                    throw new IllegalStateException("Conflicting context-param " + name + "=" + value + " in " + descriptor.getResource());
                }
                break;
            }
            default: {
                StandardDescriptorProcessor.LOG.warn(new Throwable());
                break;
            }
        }
        if (StandardDescriptorProcessor.LOG.isDebugEnabled()) {
            StandardDescriptorProcessor.LOG.debug("ContextParam: " + name + "=" + value, new Object[0]);
        }
    }
    
    public void visitDisplayName(final WebAppContext context, final Descriptor descriptor, final XmlParser.Node node) {
        if (!(descriptor instanceof FragmentDescriptor)) {
            context.setDisplayName(node.toString(false, true));
            context.getMetaData().setOrigin("display-name", descriptor);
        }
    }
    
    public void visitServlet(final WebAppContext context, final Descriptor descriptor, final XmlParser.Node node) {
        final String id = node.getAttribute("id");
        final String name = node.getString("servlet-name", false, true);
        ServletHolder holder = this._servletHolderMap.get(name);
        if (holder == null) {
            holder = context.getServletHandler().newServletHolder(BaseHolder.Source.DESCRIPTOR);
            holder.setName(name);
            this._servletHolderMap.put(name, holder);
            this._servletHolders.add(holder);
        }
        final Iterator<?> iParamsIter = node.iterator("init-param");
        while (iParamsIter.hasNext()) {
            final XmlParser.Node paramNode = (XmlParser.Node)iParamsIter.next();
            final String pname = paramNode.getString("param-name", false, true);
            final String pvalue = paramNode.getString("param-value", false, true);
            final String originName = name + ".servlet.init-param." + pname;
            final Descriptor originDescriptor = context.getMetaData().getOriginDescriptor(originName);
            switch (context.getMetaData().getOrigin(originName)) {
                case NotSet: {
                    holder.setInitParameter(pname, pvalue);
                    context.getMetaData().setOrigin(originName, descriptor);
                    continue;
                }
                case WebXml:
                case WebDefaults:
                case WebOverride: {
                    if (!(descriptor instanceof FragmentDescriptor) && descriptor != originDescriptor) {
                        holder.setInitParameter(pname, pvalue);
                        context.getMetaData().setOrigin(originName, descriptor);
                        continue;
                    }
                    continue;
                }
                case WebFragment: {
                    if (descriptor != originDescriptor && !holder.getInitParameter(pname).equals(pvalue)) {
                        throw new IllegalStateException("Mismatching init-param " + pname + "=" + pvalue + " in " + descriptor.getResource());
                    }
                    continue;
                }
                default: {
                    StandardDescriptorProcessor.LOG.warn(new Throwable());
                    continue;
                }
            }
        }
        String servlet_class = node.getString("servlet-class", false, true);
        if ("".equals(servlet_class)) {
            servlet_class = null;
        }
        if (id != null && id.equals("jsp") && servlet_class != null) {
            try {
                Loader.loadClass(this.getClass(), servlet_class);
            }
            catch (ClassNotFoundException e2) {
                StandardDescriptorProcessor.LOG.info("NO JSP Support for {}, did not find {}", context.getContextPath(), servlet_class);
                servlet_class = "org.eclipse.jetty.servlet.NoJspServlet";
            }
        }
        if (servlet_class != null) {
            ((WebDescriptor)descriptor).addClassName(servlet_class);
            switch (context.getMetaData().getOrigin(name + ".servlet.servlet-class")) {
                case NotSet: {
                    holder.setClassName(servlet_class);
                    context.getMetaData().setOrigin(name + ".servlet.servlet-class", descriptor);
                    break;
                }
                case WebXml:
                case WebDefaults:
                case WebOverride: {
                    if (!(descriptor instanceof FragmentDescriptor)) {
                        holder.setClassName(servlet_class);
                        context.getMetaData().setOrigin(name + ".servlet.servlet-class", descriptor);
                        break;
                    }
                    break;
                }
                case WebFragment: {
                    if (!servlet_class.equals(holder.getClassName())) {
                        throw new IllegalStateException("Conflicting servlet-class " + servlet_class + " in " + descriptor.getResource());
                    }
                    break;
                }
                default: {
                    StandardDescriptorProcessor.LOG.warn(new Throwable());
                    break;
                }
            }
        }
        final String jsp_file = node.getString("jsp-file", false, true);
        if (jsp_file != null) {
            holder.setForcedPath(jsp_file);
        }
        final XmlParser.Node startup = node.get("load-on-startup");
        if (startup != null) {
            final String s = startup.toString(false, true).toLowerCase(Locale.ENGLISH);
            int order = 0;
            if (s.startsWith("t")) {
                StandardDescriptorProcessor.LOG.warn("Deprecated boolean load-on-startup.  Please use integer", new Object[0]);
                order = 1;
            }
            else {
                try {
                    if (s != null && s.trim().length() > 0) {
                        order = Integer.parseInt(s);
                    }
                }
                catch (Exception e) {
                    StandardDescriptorProcessor.LOG.warn("Cannot parse load-on-startup " + s + ". Please use integer", new Object[0]);
                    StandardDescriptorProcessor.LOG.ignore(e);
                }
            }
            switch (context.getMetaData().getOrigin(name + ".servlet.load-on-startup")) {
                case NotSet: {
                    holder.setInitOrder(order);
                    context.getMetaData().setOrigin(name + ".servlet.load-on-startup", descriptor);
                    break;
                }
                case WebXml:
                case WebDefaults:
                case WebOverride: {
                    if (!(descriptor instanceof FragmentDescriptor)) {
                        holder.setInitOrder(order);
                        context.getMetaData().setOrigin(name + ".servlet.load-on-startup", descriptor);
                        break;
                    }
                    break;
                }
                case WebFragment: {
                    if (order != holder.getInitOrder()) {
                        throw new IllegalStateException("Conflicting load-on-startup value in " + descriptor.getResource());
                    }
                    break;
                }
                default: {
                    StandardDescriptorProcessor.LOG.warn(new Throwable());
                    break;
                }
            }
        }
        final Iterator<XmlParser.Node> sRefsIter = node.iterator("security-role-ref");
        while (sRefsIter.hasNext()) {
            final XmlParser.Node securityRef = sRefsIter.next();
            final String roleName = securityRef.getString("role-name", false, true);
            final String roleLink = securityRef.getString("role-link", false, true);
            if (roleName != null && roleName.length() > 0 && roleLink != null && roleLink.length() > 0) {
                if (StandardDescriptorProcessor.LOG.isDebugEnabled()) {
                    StandardDescriptorProcessor.LOG.debug("link role " + roleName + " to " + roleLink + " for " + this, new Object[0]);
                }
                switch (context.getMetaData().getOrigin(name + ".servlet.role-name." + roleName)) {
                    case NotSet: {
                        holder.setUserRoleLink(roleName, roleLink);
                        context.getMetaData().setOrigin(name + ".servlet.role-name." + roleName, descriptor);
                        continue;
                    }
                    case WebXml:
                    case WebDefaults:
                    case WebOverride: {
                        if (!(descriptor instanceof FragmentDescriptor)) {
                            holder.setUserRoleLink(roleName, roleLink);
                            context.getMetaData().setOrigin(name + ".servlet.role-name." + roleName, descriptor);
                            continue;
                        }
                        continue;
                    }
                    case WebFragment: {
                        if (!holder.getUserRoleLink(roleName).equals(roleLink)) {
                            throw new IllegalStateException("Conflicting role-link for role-name " + roleName + " for servlet " + name + " in " + descriptor.getResource());
                        }
                        continue;
                    }
                    default: {
                        StandardDescriptorProcessor.LOG.warn(new Throwable());
                        continue;
                    }
                }
            }
            else {
                StandardDescriptorProcessor.LOG.warn("Ignored invalid security-role-ref element: servlet-name=" + holder.getName() + ", " + securityRef, new Object[0]);
            }
        }
        final XmlParser.Node run_as = node.get("run-as");
        if (run_as != null) {
            final String roleName = run_as.getString("role-name", false, true);
            if (roleName != null) {
                switch (context.getMetaData().getOrigin(name + ".servlet.run-as")) {
                    case NotSet: {
                        holder.setRunAsRole(roleName);
                        context.getMetaData().setOrigin(name + ".servlet.run-as", descriptor);
                        break;
                    }
                    case WebXml:
                    case WebDefaults:
                    case WebOverride: {
                        if (!(descriptor instanceof FragmentDescriptor)) {
                            holder.setRunAsRole(roleName);
                            context.getMetaData().setOrigin(name + ".servlet.run-as", descriptor);
                            break;
                        }
                        break;
                    }
                    case WebFragment: {
                        if (!holder.getRunAsRole().equals(roleName)) {
                            throw new IllegalStateException("Conflicting run-as role " + roleName + " for servlet " + name + " in " + descriptor.getResource());
                        }
                        break;
                    }
                    default: {
                        StandardDescriptorProcessor.LOG.warn(new Throwable());
                        break;
                    }
                }
            }
        }
        final String async = node.getString("async-supported", false, true);
        if (async != null) {
            final boolean val = async.length() == 0 || Boolean.valueOf(async);
            switch (context.getMetaData().getOrigin(name + ".servlet.async-supported")) {
                case NotSet: {
                    holder.setAsyncSupported(val);
                    context.getMetaData().setOrigin(name + ".servlet.async-supported", descriptor);
                    break;
                }
                case WebXml:
                case WebDefaults:
                case WebOverride: {
                    if (!(descriptor instanceof FragmentDescriptor)) {
                        holder.setAsyncSupported(val);
                        context.getMetaData().setOrigin(name + ".servlet.async-supported", descriptor);
                        break;
                    }
                    break;
                }
                case WebFragment: {
                    if (holder.isAsyncSupported() != val) {
                        throw new IllegalStateException("Conflicting async-supported=" + async + " for servlet " + name + " in " + descriptor.getResource());
                    }
                    break;
                }
                default: {
                    StandardDescriptorProcessor.LOG.warn(new Throwable());
                    break;
                }
            }
        }
        final String enabled = node.getString("enabled", false, true);
        if (enabled != null) {
            final boolean is_enabled = enabled.length() == 0 || Boolean.valueOf(enabled);
            switch (context.getMetaData().getOrigin(name + ".servlet.enabled")) {
                case NotSet: {
                    holder.setEnabled(is_enabled);
                    context.getMetaData().setOrigin(name + ".servlet.enabled", descriptor);
                    break;
                }
                case WebXml:
                case WebDefaults:
                case WebOverride: {
                    if (!(descriptor instanceof FragmentDescriptor)) {
                        holder.setEnabled(is_enabled);
                        context.getMetaData().setOrigin(name + ".servlet.enabled", descriptor);
                        break;
                    }
                    break;
                }
                case WebFragment: {
                    if (holder.isEnabled() != is_enabled) {
                        throw new IllegalStateException("Conflicting value of servlet enabled for servlet " + name + " in " + descriptor.getResource());
                    }
                    break;
                }
                default: {
                    StandardDescriptorProcessor.LOG.warn(new Throwable());
                    break;
                }
            }
        }
        final XmlParser.Node multipart = node.get("multipart-config");
        if (multipart != null) {
            final String location = multipart.getString("location", false, true);
            final String maxFile = multipart.getString("max-file-size", false, true);
            final String maxRequest = multipart.getString("max-request-size", false, true);
            final String threshold = multipart.getString("file-size-threshold", false, true);
            final MultipartConfigElement element = new MultipartConfigElement(location, (maxFile == null || "".equals(maxFile)) ? -1L : Long.parseLong(maxFile), (maxRequest == null || "".equals(maxRequest)) ? -1L : Long.parseLong(maxRequest), (threshold == null || "".equals(threshold)) ? 0 : Integer.parseInt(threshold));
            switch (context.getMetaData().getOrigin(name + ".servlet.multipart-config")) {
                case NotSet: {
                    holder.getRegistration().setMultipartConfig(element);
                    context.getMetaData().setOrigin(name + ".servlet.multipart-config", descriptor);
                    break;
                }
                case WebXml:
                case WebDefaults:
                case WebOverride: {
                    if (!(descriptor instanceof FragmentDescriptor)) {
                        holder.getRegistration().setMultipartConfig(element);
                        context.getMetaData().setOrigin(name + ".servlet.multipart-config", descriptor);
                        break;
                    }
                    break;
                }
                case WebFragment: {
                    final MultipartConfigElement cfg = ((ServletHolder.Registration)holder.getRegistration()).getMultipartConfig();
                    if (cfg.getMaxFileSize() != element.getMaxFileSize()) {
                        throw new IllegalStateException("Conflicting multipart-config max-file-size for servlet " + name + " in " + descriptor.getResource());
                    }
                    if (cfg.getMaxRequestSize() != element.getMaxRequestSize()) {
                        throw new IllegalStateException("Conflicting multipart-config max-request-size for servlet " + name + " in " + descriptor.getResource());
                    }
                    if (cfg.getFileSizeThreshold() != element.getFileSizeThreshold()) {
                        throw new IllegalStateException("Conflicting multipart-config file-size-threshold for servlet " + name + " in " + descriptor.getResource());
                    }
                    if ((cfg.getLocation() != null && (element.getLocation() == null || element.getLocation().length() == 0)) || (cfg.getLocation() == null && (element.getLocation() != null || element.getLocation().length() > 0))) {
                        throw new IllegalStateException("Conflicting multipart-config location for servlet " + name + " in " + descriptor.getResource());
                    }
                    break;
                }
                default: {
                    StandardDescriptorProcessor.LOG.warn(new Throwable());
                    break;
                }
            }
        }
    }
    
    public void visitServletMapping(final WebAppContext context, final Descriptor descriptor, final XmlParser.Node node) {
        final String servlet_name = node.getString("servlet-name", false, true);
        switch (context.getMetaData().getOrigin(servlet_name + ".servlet.mappings")) {
            case NotSet: {
                context.getMetaData().setOrigin(servlet_name + ".servlet.mappings", descriptor);
                this.addServletMapping(servlet_name, node, context, descriptor);
                break;
            }
            case WebXml:
            case WebDefaults:
            case WebOverride: {
                if (!(descriptor instanceof FragmentDescriptor)) {
                    this.addServletMapping(servlet_name, node, context, descriptor);
                    break;
                }
                break;
            }
            case WebFragment: {
                this.addServletMapping(servlet_name, node, context, descriptor);
                break;
            }
            default: {
                StandardDescriptorProcessor.LOG.warn(new Throwable());
                break;
            }
        }
    }
    
    public void visitSessionConfig(final WebAppContext context, final Descriptor descriptor, final XmlParser.Node node) {
        XmlParser.Node tNode = node.get("session-timeout");
        if (tNode != null) {
            final BigDecimal asDecimal = new BigDecimal(tNode.toString(false, true));
            if (asDecimal.compareTo(AbstractSessionManager.MAX_INACTIVE_MINUTES) > 0) {
                throw new IllegalStateException("Max session-timeout in minutes is " + AbstractSessionManager.MAX_INACTIVE_MINUTES);
            }
            context.getSessionHandler().getSessionManager().setMaxInactiveInterval(asDecimal.intValueExact() * 60);
        }
        final Iterator<XmlParser.Node> iter = node.iterator("tracking-mode");
        if (iter.hasNext()) {
            Set<SessionTrackingMode> modes = null;
            final Origin o = context.getMetaData().getOrigin("session.tracking-mode");
            switch (o) {
                case NotSet:
                case WebDefaults: {
                    modes = new HashSet<SessionTrackingMode>();
                    context.getMetaData().setOrigin("session.tracking-mode", descriptor);
                    break;
                }
                case WebXml:
                case WebOverride:
                case WebFragment: {
                    if (descriptor instanceof OverrideDescriptor) {
                        modes = new HashSet<SessionTrackingMode>();
                    }
                    else {
                        modes = new HashSet<SessionTrackingMode>(context.getSessionHandler().getSessionManager().getEffectiveSessionTrackingModes());
                    }
                    context.getMetaData().setOrigin("session.tracking-mode", descriptor);
                    break;
                }
                default: {
                    StandardDescriptorProcessor.LOG.warn(new Throwable());
                    break;
                }
            }
            while (iter.hasNext()) {
                final XmlParser.Node mNode = iter.next();
                final String trackMode = mNode.toString(false, true);
                modes.add(SessionTrackingMode.valueOf(trackMode));
            }
            context.getSessionHandler().getSessionManager().setSessionTrackingModes(modes);
        }
        final XmlParser.Node cookieConfig = node.get("cookie-config");
        if (cookieConfig != null) {
            final String name = cookieConfig.getString("name", false, true);
            if (name != null) {
                switch (context.getMetaData().getOrigin("cookie-config.name")) {
                    case NotSet: {
                        context.getSessionHandler().getSessionManager().getSessionCookieConfig().setName(name);
                        context.getMetaData().setOrigin("cookie-config.name", descriptor);
                        break;
                    }
                    case WebXml:
                    case WebDefaults:
                    case WebOverride: {
                        if (!(descriptor instanceof FragmentDescriptor)) {
                            context.getSessionHandler().getSessionManager().getSessionCookieConfig().setName(name);
                            context.getMetaData().setOrigin("cookie-config.name", descriptor);
                            break;
                        }
                        break;
                    }
                    case WebFragment: {
                        if (!context.getSessionHandler().getSessionManager().getSessionCookieConfig().getName().equals(name)) {
                            throw new IllegalStateException("Conflicting cookie-config name " + name + " in " + descriptor.getResource());
                        }
                        break;
                    }
                    default: {
                        StandardDescriptorProcessor.LOG.warn(new Throwable());
                        break;
                    }
                }
            }
            final String domain = cookieConfig.getString("domain", false, true);
            if (domain != null) {
                switch (context.getMetaData().getOrigin("cookie-config.domain")) {
                    case NotSet: {
                        context.getSessionHandler().getSessionManager().getSessionCookieConfig().setDomain(domain);
                        context.getMetaData().setOrigin("cookie-config.domain", descriptor);
                        break;
                    }
                    case WebXml:
                    case WebDefaults:
                    case WebOverride: {
                        if (!(descriptor instanceof FragmentDescriptor)) {
                            context.getSessionHandler().getSessionManager().getSessionCookieConfig().setDomain(domain);
                            context.getMetaData().setOrigin("cookie-config.domain", descriptor);
                            break;
                        }
                        break;
                    }
                    case WebFragment: {
                        if (!context.getSessionHandler().getSessionManager().getSessionCookieConfig().getDomain().equals(domain)) {
                            throw new IllegalStateException("Conflicting cookie-config domain " + domain + " in " + descriptor.getResource());
                        }
                        break;
                    }
                    default: {
                        StandardDescriptorProcessor.LOG.warn(new Throwable());
                        break;
                    }
                }
            }
            final String path = cookieConfig.getString("path", false, true);
            if (path != null) {
                switch (context.getMetaData().getOrigin("cookie-config.path")) {
                    case NotSet: {
                        context.getSessionHandler().getSessionManager().getSessionCookieConfig().setPath(path);
                        context.getMetaData().setOrigin("cookie-config.path", descriptor);
                        break;
                    }
                    case WebXml:
                    case WebDefaults:
                    case WebOverride: {
                        if (!(descriptor instanceof FragmentDescriptor)) {
                            context.getSessionHandler().getSessionManager().getSessionCookieConfig().setPath(path);
                            context.getMetaData().setOrigin("cookie-config.path", descriptor);
                            break;
                        }
                        break;
                    }
                    case WebFragment: {
                        if (!context.getSessionHandler().getSessionManager().getSessionCookieConfig().getPath().equals(path)) {
                            throw new IllegalStateException("Conflicting cookie-config path " + path + " in " + descriptor.getResource());
                        }
                        break;
                    }
                    default: {
                        StandardDescriptorProcessor.LOG.warn(new Throwable());
                        break;
                    }
                }
            }
            final String comment = cookieConfig.getString("comment", false, true);
            if (comment != null) {
                switch (context.getMetaData().getOrigin("cookie-config.comment")) {
                    case NotSet: {
                        context.getSessionHandler().getSessionManager().getSessionCookieConfig().setComment(comment);
                        context.getMetaData().setOrigin("cookie-config.comment", descriptor);
                        break;
                    }
                    case WebXml:
                    case WebDefaults:
                    case WebOverride: {
                        if (!(descriptor instanceof FragmentDescriptor)) {
                            context.getSessionHandler().getSessionManager().getSessionCookieConfig().setComment(comment);
                            context.getMetaData().setOrigin("cookie-config.comment", descriptor);
                            break;
                        }
                        break;
                    }
                    case WebFragment: {
                        if (!context.getSessionHandler().getSessionManager().getSessionCookieConfig().getComment().equals(comment)) {
                            throw new IllegalStateException("Conflicting cookie-config comment " + comment + " in " + descriptor.getResource());
                        }
                        break;
                    }
                    default: {
                        StandardDescriptorProcessor.LOG.warn(new Throwable());
                        break;
                    }
                }
            }
            tNode = cookieConfig.get("http-only");
            if (tNode != null) {
                final boolean httpOnly = Boolean.parseBoolean(tNode.toString(false, true));
                switch (context.getMetaData().getOrigin("cookie-config.http-only")) {
                    case NotSet: {
                        context.getSessionHandler().getSessionManager().getSessionCookieConfig().setHttpOnly(httpOnly);
                        context.getMetaData().setOrigin("cookie-config.http-only", descriptor);
                        break;
                    }
                    case WebXml:
                    case WebDefaults:
                    case WebOverride: {
                        if (!(descriptor instanceof FragmentDescriptor)) {
                            context.getSessionHandler().getSessionManager().getSessionCookieConfig().setHttpOnly(httpOnly);
                            context.getMetaData().setOrigin("cookie-config.http-only", descriptor);
                            break;
                        }
                        break;
                    }
                    case WebFragment: {
                        if (context.getSessionHandler().getSessionManager().getSessionCookieConfig().isHttpOnly() != httpOnly) {
                            throw new IllegalStateException("Conflicting cookie-config http-only " + httpOnly + " in " + descriptor.getResource());
                        }
                        break;
                    }
                    default: {
                        StandardDescriptorProcessor.LOG.warn(new Throwable());
                        break;
                    }
                }
            }
            tNode = cookieConfig.get("secure");
            if (tNode != null) {
                final boolean secure = Boolean.parseBoolean(tNode.toString(false, true));
                switch (context.getMetaData().getOrigin("cookie-config.secure")) {
                    case NotSet: {
                        context.getSessionHandler().getSessionManager().getSessionCookieConfig().setSecure(secure);
                        context.getMetaData().setOrigin("cookie-config.secure", descriptor);
                        break;
                    }
                    case WebXml:
                    case WebDefaults:
                    case WebOverride: {
                        if (!(descriptor instanceof FragmentDescriptor)) {
                            context.getSessionHandler().getSessionManager().getSessionCookieConfig().setSecure(secure);
                            context.getMetaData().setOrigin("cookie-config.secure", descriptor);
                            break;
                        }
                        break;
                    }
                    case WebFragment: {
                        if (context.getSessionHandler().getSessionManager().getSessionCookieConfig().isSecure() != secure) {
                            throw new IllegalStateException("Conflicting cookie-config secure " + secure + " in " + descriptor.getResource());
                        }
                        break;
                    }
                    default: {
                        StandardDescriptorProcessor.LOG.warn(new Throwable());
                        break;
                    }
                }
            }
            tNode = cookieConfig.get("max-age");
            if (tNode != null) {
                final int maxAge = Integer.parseInt(tNode.toString(false, true));
                switch (context.getMetaData().getOrigin("cookie-config.max-age")) {
                    case NotSet: {
                        context.getSessionHandler().getSessionManager().getSessionCookieConfig().setMaxAge(maxAge);
                        context.getMetaData().setOrigin("cookie-config.max-age", descriptor);
                        break;
                    }
                    case WebXml:
                    case WebDefaults:
                    case WebOverride: {
                        if (!(descriptor instanceof FragmentDescriptor)) {
                            context.getSessionHandler().getSessionManager().getSessionCookieConfig().setMaxAge(maxAge);
                            context.getMetaData().setOrigin("cookie-config.max-age", descriptor);
                            break;
                        }
                        break;
                    }
                    case WebFragment: {
                        if (context.getSessionHandler().getSessionManager().getSessionCookieConfig().getMaxAge() != maxAge) {
                            throw new IllegalStateException("Conflicting cookie-config max-age " + maxAge + " in " + descriptor.getResource());
                        }
                        break;
                    }
                    default: {
                        StandardDescriptorProcessor.LOG.warn(new Throwable());
                        break;
                    }
                }
            }
        }
    }
    
    public void visitMimeMapping(final WebAppContext context, final Descriptor descriptor, final XmlParser.Node node) {
        String extension = node.getString("extension", false, true);
        if (extension != null && extension.startsWith(".")) {
            extension = extension.substring(1);
        }
        final String mimeType = node.getString("mime-type", false, true);
        if (extension != null) {
            switch (context.getMetaData().getOrigin("extension." + extension)) {
                case NotSet: {
                    context.getMimeTypes().addMimeMapping(extension, mimeType);
                    context.getMetaData().setOrigin("extension." + extension, descriptor);
                    break;
                }
                case WebXml:
                case WebDefaults:
                case WebOverride: {
                    if (!(descriptor instanceof FragmentDescriptor)) {
                        context.getMimeTypes().addMimeMapping(extension, mimeType);
                        context.getMetaData().setOrigin("extension." + extension, descriptor);
                        break;
                    }
                    break;
                }
                case WebFragment: {
                    if (!context.getMimeTypes().getMimeByExtension("." + extension).equals(mimeType)) {
                        throw new IllegalStateException("Conflicting mime-type " + mimeType + " for extension " + extension + " in " + descriptor.getResource());
                    }
                    break;
                }
                default: {
                    StandardDescriptorProcessor.LOG.warn(new Throwable());
                    break;
                }
            }
        }
    }
    
    public void visitWelcomeFileList(final WebAppContext context, final Descriptor descriptor, final XmlParser.Node node) {
        switch (context.getMetaData().getOrigin("welcome-file-list")) {
            case NotSet: {
                context.getMetaData().setOrigin("welcome-file-list", descriptor);
                this.addWelcomeFiles(context, node);
                break;
            }
            case WebXml: {
                this.addWelcomeFiles(context, node);
                break;
            }
            case WebDefaults: {
                if (!(descriptor instanceof DefaultsDescriptor) && !(descriptor instanceof OverrideDescriptor) && !(descriptor instanceof FragmentDescriptor)) {
                    context.setWelcomeFiles(new String[0]);
                }
                this.addWelcomeFiles(context, node);
                break;
            }
            case WebOverride: {
                this.addWelcomeFiles(context, node);
                break;
            }
            case WebFragment: {
                this.addWelcomeFiles(context, node);
                break;
            }
            default: {
                StandardDescriptorProcessor.LOG.warn(new Throwable());
                break;
            }
        }
    }
    
    public void visitLocaleEncodingList(final WebAppContext context, final Descriptor descriptor, final XmlParser.Node node) {
        final Iterator<XmlParser.Node> iter = node.iterator("locale-encoding-mapping");
        while (iter.hasNext()) {
            final XmlParser.Node mapping = iter.next();
            final String locale = mapping.getString("locale", false, true);
            final String encoding = mapping.getString("encoding", false, true);
            if (encoding != null) {
                switch (context.getMetaData().getOrigin("locale-encoding." + locale)) {
                    case NotSet: {
                        context.addLocaleEncoding(locale, encoding);
                        context.getMetaData().setOrigin("locale-encoding." + locale, descriptor);
                        continue;
                    }
                    case WebXml:
                    case WebDefaults:
                    case WebOverride: {
                        if (!(descriptor instanceof FragmentDescriptor)) {
                            context.addLocaleEncoding(locale, encoding);
                            context.getMetaData().setOrigin("locale-encoding." + locale, descriptor);
                            continue;
                        }
                        continue;
                    }
                    case WebFragment: {
                        if (!encoding.equals(context.getLocaleEncoding(locale))) {
                            throw new IllegalStateException("Conflicting loacle-encoding mapping for locale " + locale + " in " + descriptor.getResource());
                        }
                        continue;
                    }
                    default: {
                        StandardDescriptorProcessor.LOG.warn(new Throwable());
                        continue;
                    }
                }
            }
        }
    }
    
    public void visitErrorPage(final WebAppContext context, final Descriptor descriptor, final XmlParser.Node node) {
        String error = node.getString("error-code", false, true);
        int code = 0;
        if (error == null || error.length() == 0) {
            error = node.getString("exception-type", false, true);
            if (error == null || error.length() == 0) {
                error = "org.eclipse.jetty.server.error_page.global";
            }
        }
        else {
            code = Integer.valueOf(error);
        }
        final String location = node.getString("location", false, true);
        if (!location.startsWith("/")) {
            throw new IllegalStateException("Missing leading '/' for location: " + location);
        }
        final ErrorPageErrorHandler handler = (ErrorPageErrorHandler)context.getErrorHandler();
        final String originName = "error." + error;
        switch (context.getMetaData().getOrigin(originName)) {
            case NotSet: {
                if (code > 0) {
                    handler.addErrorPage(code, location);
                }
                else {
                    handler.addErrorPage(error, location);
                }
                context.getMetaData().setOrigin("error." + error, descriptor);
                break;
            }
            case WebXml:
            case WebDefaults:
            case WebOverride: {
                if (descriptor instanceof FragmentDescriptor) {
                    break;
                }
                final Descriptor originDescriptor = context.getMetaData().getOriginDescriptor(originName);
                if (descriptor == originDescriptor) {
                    throw new IllegalStateException("Duplicate error-page " + error + " at " + location);
                }
                if (code > 0) {
                    handler.addErrorPage(code, location);
                }
                else {
                    handler.addErrorPage(error, location);
                }
                context.getMetaData().setOrigin("error." + error, descriptor);
                break;
            }
            case WebFragment: {
                if (!handler.getErrorPages().get(error).equals(location)) {
                    throw new IllegalStateException("Conflicting error-code or exception-type " + error + " in " + descriptor.getResource());
                }
                break;
            }
            default: {
                StandardDescriptorProcessor.LOG.warn(new Throwable());
                break;
            }
        }
    }
    
    public void addWelcomeFiles(final WebAppContext context, final XmlParser.Node node) {
        final Iterator<XmlParser.Node> iter = node.iterator("welcome-file");
        while (iter.hasNext()) {
            final XmlParser.Node indexNode = iter.next();
            final String welcome = indexNode.toString(false, true);
            if (welcome != null && welcome.trim().length() > 0) {
                context.setWelcomeFiles(ArrayUtil.addToArray(context.getWelcomeFiles(), welcome, String.class));
            }
        }
    }
    
    public ServletMapping addServletMapping(final String servletName, final XmlParser.Node node, final WebAppContext context, final Descriptor descriptor) {
        final ServletMapping mapping = new ServletMapping();
        mapping.setServletName(servletName);
        mapping.setDefault(descriptor instanceof DefaultsDescriptor);
        final List<String> paths = new ArrayList<String>();
        final Iterator<XmlParser.Node> iter = node.iterator("url-pattern");
        while (iter.hasNext()) {
            String p = iter.next().toString(false, true);
            p = ServletPathSpec.normalize(p);
            final ListIterator<ServletMapping> listItor = this._servletMappings.listIterator();
            for (boolean found = false; listItor.hasNext() && !found; found = true) {
                final ServletMapping sm = listItor.next();
                if (sm.getPathSpecs() != null) {
                    for (final String ps : sm.getPathSpecs()) {
                        if (p.equals(ps) && (sm.isDefault() || servletName.equals(sm.getServletName()))) {
                            if (sm.isDefault()) {
                                if (StandardDescriptorProcessor.LOG.isDebugEnabled()) {
                                    StandardDescriptorProcessor.LOG.debug("{} in mapping {} from defaults descriptor is overridden by ", ps, sm, servletName);
                                }
                            }
                            else {
                                StandardDescriptorProcessor.LOG.warn("Duplicate mapping from {} to {}", p, servletName);
                            }
                            final String[] updatedPaths = ArrayUtil.removeFromArray(sm.getPathSpecs(), ps);
                            if (updatedPaths == null || updatedPaths.length == 0) {
                                if (StandardDescriptorProcessor.LOG.isDebugEnabled()) {
                                    StandardDescriptorProcessor.LOG.debug("Removed empty mapping {}", sm);
                                }
                                listItor.remove();
                            }
                            else {
                                sm.setPathSpecs(updatedPaths);
                                if (StandardDescriptorProcessor.LOG.isDebugEnabled()) {
                                    StandardDescriptorProcessor.LOG.debug("Removed path {} from mapping {}", p, sm);
                                }
                            }
                            break;
                        }
                    }
                }
            }
            paths.add(p);
            context.getMetaData().setOrigin(servletName + ".servlet.mapping." + p, descriptor);
        }
        mapping.setPathSpecs(paths.toArray(new String[paths.size()]));
        if (StandardDescriptorProcessor.LOG.isDebugEnabled()) {
            StandardDescriptorProcessor.LOG.debug("Added mapping {} ", mapping);
        }
        this._servletMappings.add(mapping);
        return mapping;
    }
    
    public void addFilterMapping(final String filterName, final XmlParser.Node node, final WebAppContext context, final Descriptor descriptor) {
        final FilterMapping mapping = new FilterMapping();
        mapping.setFilterName(filterName);
        final List<String> paths = new ArrayList<String>();
        Iterator<XmlParser.Node> iter = node.iterator("url-pattern");
        while (iter.hasNext()) {
            String p = iter.next().toString(false, true);
            p = ServletPathSpec.normalize(p);
            paths.add(p);
            context.getMetaData().setOrigin(filterName + ".filter.mapping." + p, descriptor);
        }
        mapping.setPathSpecs(paths.toArray(new String[paths.size()]));
        final List<String> names = new ArrayList<String>();
        iter = node.iterator("servlet-name");
        while (iter.hasNext()) {
            final String n = iter.next().toString(false, true);
            names.add(n);
        }
        mapping.setServletNames(names.toArray(new String[names.size()]));
        final List<DispatcherType> dispatches = new ArrayList<DispatcherType>();
        iter = node.iterator("dispatcher");
        while (iter.hasNext()) {
            final String d = iter.next().toString(false, true);
            dispatches.add(FilterMapping.dispatch(d));
        }
        if (dispatches.size() > 0) {
            mapping.setDispatcherTypes(EnumSet.copyOf(dispatches));
        }
        this._filterMappings.add(mapping);
    }
    
    public void visitTagLib(final WebAppContext context, final Descriptor descriptor, final XmlParser.Node node) {
        final String uri = node.getString("taglib-uri", false, true);
        final String location = node.getString("taglib-location", false, true);
        context.setResourceAlias(uri, location);
        ServletContextHandler.JspConfig config = (ServletContextHandler.JspConfig)context.getServletContext().getJspConfigDescriptor();
        if (config == null) {
            config = new ServletContextHandler.JspConfig();
            context.getServletContext().setJspConfigDescriptor(config);
        }
        final ServletContextHandler.TagLib tl = new ServletContextHandler.TagLib();
        tl.setTaglibLocation(location);
        tl.setTaglibURI(uri);
        config.addTaglibDescriptor(tl);
    }
    
    public void visitJspConfig(final WebAppContext context, final Descriptor descriptor, final XmlParser.Node node) {
        ServletContextHandler.JspConfig config = (ServletContextHandler.JspConfig)context.getServletContext().getJspConfigDescriptor();
        if (config == null) {
            config = new ServletContextHandler.JspConfig();
            context.getServletContext().setJspConfigDescriptor(config);
        }
        for (int i = 0; i < node.size(); ++i) {
            final Object o = node.get(i);
            if (o instanceof XmlParser.Node && "taglib".equals(((XmlParser.Node)o).getTag())) {
                this.visitTagLib(context, descriptor, (XmlParser.Node)o);
            }
        }
        final Iterator<XmlParser.Node> iter = node.iterator("jsp-property-group");
        final List<String> paths = new ArrayList<String>();
        while (iter.hasNext()) {
            final ServletContextHandler.JspPropertyGroup jpg = new ServletContextHandler.JspPropertyGroup();
            config.addJspPropertyGroup(jpg);
            final XmlParser.Node group = iter.next();
            final Iterator<XmlParser.Node> iter2 = group.iterator("url-pattern");
            while (iter2.hasNext()) {
                String url = iter2.next().toString(false, true);
                url = ServletPathSpec.normalize(url);
                paths.add(url);
                jpg.addUrlPattern(url);
            }
            jpg.setElIgnored(group.getString("el-ignored", false, true));
            jpg.setPageEncoding(group.getString("page-encoding", false, true));
            jpg.setScriptingInvalid(group.getString("scripting-invalid", false, true));
            jpg.setIsXml(group.getString("is-xml", false, true));
            jpg.setDeferredSyntaxAllowedAsLiteral(group.getString("deferred-syntax-allowed-as-literal", false, true));
            jpg.setTrimDirectiveWhitespaces(group.getString("trim-directive-whitespaces", false, true));
            jpg.setDefaultContentType(group.getString("default-content-type", false, true));
            jpg.setBuffer(group.getString("buffer", false, true));
            jpg.setErrorOnUndeclaredNamespace(group.getString("error-on-undeclared-namespace", false, true));
            final Iterator<XmlParser.Node> preludes = group.iterator("include-prelude");
            while (preludes.hasNext()) {
                final String prelude = preludes.next().toString(false, true);
                jpg.addIncludePrelude(prelude);
            }
            final Iterator<XmlParser.Node> codas = group.iterator("include-coda");
            while (codas.hasNext()) {
                final String coda = codas.next().toString(false, true);
                jpg.addIncludeCoda(coda);
            }
            if (StandardDescriptorProcessor.LOG.isDebugEnabled()) {
                StandardDescriptorProcessor.LOG.debug(config.toString(), new Object[0]);
            }
        }
        if (paths.size() > 0) {
            ServletMapping jspMapping = null;
            for (final ServletMapping m : this._servletMappings) {
                if (m.getServletName().equals("jsp")) {
                    jspMapping = m;
                    break;
                }
            }
            if (jspMapping != null) {
                if (jspMapping.getPathSpecs() == null) {
                    if (StandardDescriptorProcessor.LOG.isDebugEnabled()) {
                        StandardDescriptorProcessor.LOG.debug("Adding all paths from jsp-config to jsp servlet mapping", new Object[0]);
                    }
                    jspMapping.setPathSpecs(paths.toArray(new String[paths.size()]));
                }
                else {
                    final ListIterator<String> piterator = paths.listIterator();
                    while (piterator.hasNext()) {
                        final String p = piterator.next();
                        if (jspMapping.containsPathSpec(p)) {
                            piterator.remove();
                        }
                    }
                    if (paths.size() > 0) {
                        for (final String p2 : jspMapping.getPathSpecs()) {
                            paths.add(p2);
                        }
                        if (StandardDescriptorProcessor.LOG.isDebugEnabled()) {
                            StandardDescriptorProcessor.LOG.debug("Adding extra paths from jsp-config to jsp servlet mapping", new Object[0]);
                        }
                        jspMapping.setPathSpecs(paths.toArray(new String[paths.size()]));
                    }
                }
            }
            else {
                final ServletMapping mapping = new ServletMapping();
                mapping.setServletName("jsp");
                mapping.setPathSpecs(paths.toArray(new String[paths.size()]));
                this._servletMappings.add(mapping);
            }
        }
    }
    
    public void visitSecurityConstraint(final WebAppContext context, final Descriptor descriptor, final XmlParser.Node node) {
        final Constraint scBase = new Constraint();
        try {
            final XmlParser.Node auths = node.get("auth-constraint");
            if (auths != null) {
                scBase.setAuthenticate(true);
                final Iterator<XmlParser.Node> iter = auths.iterator("role-name");
                final List<String> roles = new ArrayList<String>();
                while (iter.hasNext()) {
                    final String role = iter.next().toString(false, true);
                    roles.add(role);
                }
                scBase.setRoles(roles.toArray(new String[roles.size()]));
            }
            XmlParser.Node data = node.get("user-data-constraint");
            if (data != null) {
                data = data.get("transport-guarantee");
                final String guarantee = data.toString(false, true).toUpperCase(Locale.ENGLISH);
                if (guarantee == null || guarantee.length() == 0 || "NONE".equals(guarantee)) {
                    scBase.setDataConstraint(0);
                }
                else if ("INTEGRAL".equals(guarantee)) {
                    scBase.setDataConstraint(1);
                }
                else if ("CONFIDENTIAL".equals(guarantee)) {
                    scBase.setDataConstraint(2);
                }
                else {
                    StandardDescriptorProcessor.LOG.warn("Unknown user-data-constraint:" + guarantee, new Object[0]);
                    scBase.setDataConstraint(2);
                }
            }
            final Iterator<XmlParser.Node> iter2 = node.iterator("web-resource-collection");
            while (iter2.hasNext()) {
                final XmlParser.Node collection = iter2.next();
                final String name = collection.getString("web-resource-name", false, true);
                final Constraint sc = (Constraint)scBase.clone();
                sc.setName(name);
                final Iterator<XmlParser.Node> iter3 = collection.iterator("url-pattern");
                while (iter3.hasNext()) {
                    String url = iter3.next().toString(false, true);
                    url = ServletPathSpec.normalize(url);
                    context.getMetaData().setOrigin("constraint.url." + url, descriptor);
                    final Iterator<XmlParser.Node> methods = collection.iterator("http-method");
                    final Iterator<XmlParser.Node> ommissions = collection.iterator("http-method-omission");
                    if (methods.hasNext()) {
                        if (ommissions.hasNext()) {
                            throw new IllegalStateException("web-resource-collection cannot contain both http-method and http-method-omission");
                        }
                        while (methods.hasNext()) {
                            final String method = methods.next().toString(false, true);
                            final ConstraintMapping mapping = new ConstraintMapping();
                            mapping.setMethod(method);
                            mapping.setPathSpec(url);
                            mapping.setConstraint(sc);
                            ((ConstraintAware)context.getSecurityHandler()).addConstraintMapping(mapping);
                        }
                    }
                    else if (ommissions.hasNext()) {
                        while (ommissions.hasNext()) {
                            final String method = ommissions.next().toString(false, true);
                            final ConstraintMapping mapping = new ConstraintMapping();
                            mapping.setMethodOmissions(new String[] { method });
                            mapping.setPathSpec(url);
                            mapping.setConstraint(sc);
                            ((ConstraintAware)context.getSecurityHandler()).addConstraintMapping(mapping);
                        }
                    }
                    else {
                        final ConstraintMapping mapping2 = new ConstraintMapping();
                        mapping2.setPathSpec(url);
                        mapping2.setConstraint(sc);
                        ((ConstraintAware)context.getSecurityHandler()).addConstraintMapping(mapping2);
                    }
                }
            }
        }
        catch (CloneNotSupportedException e) {
            StandardDescriptorProcessor.LOG.warn(e);
        }
    }
    
    public void visitLoginConfig(final WebAppContext context, final Descriptor descriptor, final XmlParser.Node node) throws Exception {
        final XmlParser.Node method = node.get("auth-method");
        if (method != null) {
            switch (context.getMetaData().getOrigin("auth-method")) {
                case NotSet: {
                    context.getSecurityHandler().setAuthMethod(method.toString(false, true));
                    context.getMetaData().setOrigin("auth-method", descriptor);
                    break;
                }
                case WebXml:
                case WebDefaults:
                case WebOverride: {
                    if (!(descriptor instanceof FragmentDescriptor)) {
                        context.getSecurityHandler().setAuthMethod(method.toString(false, true));
                        context.getMetaData().setOrigin("auth-method", descriptor);
                        break;
                    }
                    break;
                }
                case WebFragment: {
                    if (!context.getSecurityHandler().getAuthMethod().equals(method.toString(false, true))) {
                        throw new IllegalStateException("Conflicting auth-method value in " + descriptor.getResource());
                    }
                    break;
                }
                default: {
                    StandardDescriptorProcessor.LOG.warn(new Throwable());
                    break;
                }
            }
            final XmlParser.Node name = node.get("realm-name");
            final String nameStr = (name == null) ? "default" : name.toString(false, true);
            switch (context.getMetaData().getOrigin("realm-name")) {
                case NotSet: {
                    context.getSecurityHandler().setRealmName(nameStr);
                    context.getMetaData().setOrigin("realm-name", descriptor);
                    break;
                }
                case WebXml:
                case WebDefaults:
                case WebOverride: {
                    if (!(descriptor instanceof FragmentDescriptor)) {
                        context.getSecurityHandler().setRealmName(nameStr);
                        context.getMetaData().setOrigin("realm-name", descriptor);
                        break;
                    }
                    break;
                }
                case WebFragment: {
                    if (!context.getSecurityHandler().getRealmName().equals(nameStr)) {
                        throw new IllegalStateException("Conflicting realm-name value in " + descriptor.getResource());
                    }
                    break;
                }
                default: {
                    StandardDescriptorProcessor.LOG.warn(new Throwable());
                    break;
                }
            }
            if ("FORM".equalsIgnoreCase(context.getSecurityHandler().getAuthMethod())) {
                final XmlParser.Node formConfig = node.get("form-login-config");
                if (formConfig == null) {
                    throw new IllegalStateException("!form-login-config");
                }
                String loginPageName = null;
                final XmlParser.Node loginPage = formConfig.get("form-login-page");
                if (loginPage != null) {
                    loginPageName = loginPage.toString(false, true);
                }
                String errorPageName = null;
                final XmlParser.Node errorPage = formConfig.get("form-error-page");
                if (errorPage != null) {
                    errorPageName = errorPage.toString(false, true);
                }
                switch (context.getMetaData().getOrigin("form-login-page")) {
                    case NotSet: {
                        context.getSecurityHandler().setInitParameter("org.eclipse.jetty.security.form_login_page", loginPageName);
                        context.getMetaData().setOrigin("form-login-page", descriptor);
                        break;
                    }
                    case WebXml:
                    case WebDefaults:
                    case WebOverride: {
                        if (!(descriptor instanceof FragmentDescriptor)) {
                            context.getSecurityHandler().setInitParameter("org.eclipse.jetty.security.form_login_page", loginPageName);
                            context.getMetaData().setOrigin("form-login-page", descriptor);
                            break;
                        }
                        break;
                    }
                    case WebFragment: {
                        if (!context.getSecurityHandler().getInitParameter("org.eclipse.jetty.security.form_login_page").equals(loginPageName)) {
                            throw new IllegalStateException("Conflicting form-login-page value in " + descriptor.getResource());
                        }
                        break;
                    }
                    default: {
                        StandardDescriptorProcessor.LOG.warn(new Throwable());
                        break;
                    }
                }
                switch (context.getMetaData().getOrigin("form-error-page")) {
                    case NotSet: {
                        context.getSecurityHandler().setInitParameter("org.eclipse.jetty.security.form_error_page", errorPageName);
                        context.getMetaData().setOrigin("form-error-page", descriptor);
                        break;
                    }
                    case WebXml:
                    case WebDefaults:
                    case WebOverride: {
                        if (!(descriptor instanceof FragmentDescriptor)) {
                            context.getSecurityHandler().setInitParameter("org.eclipse.jetty.security.form_error_page", errorPageName);
                            context.getMetaData().setOrigin("form-error-page", descriptor);
                            break;
                        }
                        break;
                    }
                    case WebFragment: {
                        if (!context.getSecurityHandler().getInitParameter("org.eclipse.jetty.security.form_error_page").equals(errorPageName)) {
                            throw new IllegalStateException("Conflicting form-error-page value in " + descriptor.getResource());
                        }
                        break;
                    }
                    default: {
                        StandardDescriptorProcessor.LOG.warn(new Throwable());
                        break;
                    }
                }
            }
        }
    }
    
    public void visitSecurityRole(final WebAppContext context, final Descriptor descriptor, final XmlParser.Node node) {
        final XmlParser.Node roleNode = node.get("role-name");
        final String role = roleNode.toString(false, true);
        ((ConstraintAware)context.getSecurityHandler()).addRole(role);
    }
    
    public void visitFilter(final WebAppContext context, final Descriptor descriptor, final XmlParser.Node node) {
        final String name = node.getString("filter-name", false, true);
        FilterHolder holder = this._filterHolderMap.get(name);
        if (holder == null) {
            holder = context.getServletHandler().newFilterHolder(BaseHolder.Source.DESCRIPTOR);
            holder.setName(name);
            this._filterHolderMap.put(name, holder);
            this._filterHolders.add(holder);
        }
        final String filter_class = node.getString("filter-class", false, true);
        if (filter_class != null) {
            ((WebDescriptor)descriptor).addClassName(filter_class);
            switch (context.getMetaData().getOrigin(name + ".filter.filter-class")) {
                case NotSet: {
                    holder.setClassName(filter_class);
                    context.getMetaData().setOrigin(name + ".filter.filter-class", descriptor);
                    break;
                }
                case WebXml:
                case WebDefaults:
                case WebOverride: {
                    if (!(descriptor instanceof FragmentDescriptor)) {
                        holder.setClassName(filter_class);
                        context.getMetaData().setOrigin(name + ".filter.filter-class", descriptor);
                        break;
                    }
                    break;
                }
                case WebFragment: {
                    if (!holder.getClassName().equals(filter_class)) {
                        throw new IllegalStateException("Conflicting filter-class for filter " + name + " in " + descriptor.getResource());
                    }
                    break;
                }
                default: {
                    StandardDescriptorProcessor.LOG.warn(new Throwable());
                    break;
                }
            }
        }
        final Iterator<XmlParser.Node> iter = node.iterator("init-param");
        while (iter.hasNext()) {
            final XmlParser.Node paramNode = iter.next();
            final String pname = paramNode.getString("param-name", false, true);
            final String pvalue = paramNode.getString("param-value", false, true);
            switch (context.getMetaData().getOrigin(name + ".filter.init-param." + pname)) {
                case NotSet: {
                    holder.setInitParameter(pname, pvalue);
                    context.getMetaData().setOrigin(name + ".filter.init-param." + pname, descriptor);
                    continue;
                }
                case WebXml:
                case WebDefaults:
                case WebOverride: {
                    if (!(descriptor instanceof FragmentDescriptor)) {
                        holder.setInitParameter(pname, pvalue);
                        context.getMetaData().setOrigin(name + ".filter.init-param." + pname, descriptor);
                        continue;
                    }
                    continue;
                }
                case WebFragment: {
                    if (!holder.getInitParameter(pname).equals(pvalue)) {
                        throw new IllegalStateException("Mismatching init-param " + pname + "=" + pvalue + " in " + descriptor.getResource());
                    }
                    continue;
                }
                default: {
                    StandardDescriptorProcessor.LOG.warn(new Throwable());
                    continue;
                }
            }
        }
        final String async = node.getString("async-supported", false, true);
        if (async != null) {
            holder.setAsyncSupported(async.length() == 0 || Boolean.valueOf(async));
        }
        if (async != null) {
            final boolean val = async.length() == 0 || Boolean.valueOf(async);
            switch (context.getMetaData().getOrigin(name + ".filter.async-supported")) {
                case NotSet: {
                    holder.setAsyncSupported(val);
                    context.getMetaData().setOrigin(name + ".filter.async-supported", descriptor);
                    break;
                }
                case WebXml:
                case WebDefaults:
                case WebOverride: {
                    if (!(descriptor instanceof FragmentDescriptor)) {
                        holder.setAsyncSupported(val);
                        context.getMetaData().setOrigin(name + ".filter.async-supported", descriptor);
                        break;
                    }
                    break;
                }
                case WebFragment: {
                    if (holder.isAsyncSupported() != val) {
                        throw new IllegalStateException("Conflicting async-supported=" + async + " for filter " + name + " in " + descriptor.getResource());
                    }
                    break;
                }
                default: {
                    StandardDescriptorProcessor.LOG.warn(new Throwable());
                    break;
                }
            }
        }
    }
    
    public void visitFilterMapping(final WebAppContext context, final Descriptor descriptor, final XmlParser.Node node) {
        final String filter_name = node.getString("filter-name", false, true);
        switch (context.getMetaData().getOrigin(filter_name + ".filter.mappings")) {
            case NotSet: {
                context.getMetaData().setOrigin(filter_name + ".filter.mappings", descriptor);
                this.addFilterMapping(filter_name, node, context, descriptor);
                break;
            }
            case WebXml:
            case WebDefaults:
            case WebOverride: {
                if (!(descriptor instanceof FragmentDescriptor)) {
                    this.addFilterMapping(filter_name, node, context, descriptor);
                    break;
                }
                break;
            }
            case WebFragment: {
                this.addFilterMapping(filter_name, node, context, descriptor);
                break;
            }
            default: {
                StandardDescriptorProcessor.LOG.warn(new Throwable());
                break;
            }
        }
    }
    
    public void visitListener(final WebAppContext context, final Descriptor descriptor, final XmlParser.Node node) {
        final String className = node.getString("listener-class", false, true);
        EventListener listener = null;
        try {
            if (className != null && className.length() > 0) {
                final EventListener[] listeners = context.getEventListeners();
                if (listeners != null) {
                    for (final EventListener l : listeners) {
                        if (l.getClass().getName().equals(className)) {
                            return;
                        }
                    }
                }
                ((WebDescriptor)descriptor).addClassName(className);
                final Class<? extends EventListener> listenerClass = (Class<? extends EventListener>)context.loadClass(className);
                listener = this.newListenerInstance(context, listenerClass);
                if (!(listener instanceof EventListener)) {
                    StandardDescriptorProcessor.LOG.warn("Not an EventListener: " + listener, new Object[0]);
                    return;
                }
                context.addEventListener(listener);
                context.getMetaData().setOrigin(className + ".listener", descriptor);
            }
        }
        catch (Exception e) {
            StandardDescriptorProcessor.LOG.warn("Could not instantiate listener " + className, e);
        }
    }
    
    public void visitDistributable(final WebAppContext context, final Descriptor descriptor, final XmlParser.Node node) {
        ((WebDescriptor)descriptor).setDistributable(true);
    }
    
    public void visitDenyUncoveredHttpMethods(final WebAppContext context, final Descriptor descriptor, final XmlParser.Node node) {
        ((ConstraintAware)context.getSecurityHandler()).setDenyUncoveredHttpMethods(true);
    }
    
    public EventListener newListenerInstance(final WebAppContext context, final Class<? extends EventListener> clazz) throws Exception {
        final ListenerHolder h = context.getServletHandler().newListenerHolder(BaseHolder.Source.DESCRIPTOR);
        final EventListener l = context.getServletContext().createInstance(clazz);
        h.setListener(l);
        context.getServletHandler().addListener(h);
        return l;
    }
    
    static {
        LOG = Log.getLogger(StandardDescriptorProcessor.class);
    }
}
