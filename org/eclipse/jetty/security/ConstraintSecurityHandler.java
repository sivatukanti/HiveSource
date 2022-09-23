// 
// Decompiled by Procyon v0.5.36
// 

package org.eclipse.jetty.security;

import org.eclipse.jetty.util.log.Log;
import org.eclipse.jetty.server.handler.ContextHandler;
import org.eclipse.jetty.server.UserIdentity;
import java.io.IOException;
import org.eclipse.jetty.server.HttpConfiguration;
import org.eclipse.jetty.util.URIUtil;
import javax.servlet.ServletRequest;
import org.eclipse.jetty.server.Response;
import org.eclipse.jetty.server.Request;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Arrays;
import java.util.Collection;
import javax.servlet.HttpMethodConstraintElement;
import javax.servlet.ServletSecurityElement;
import java.util.Iterator;
import java.util.ArrayList;
import java.util.Collections;
import javax.servlet.annotation.ServletSecurity;
import javax.servlet.HttpConstraintElement;
import org.eclipse.jetty.util.security.Constraint;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.Map;
import org.eclipse.jetty.http.PathMap;
import java.util.Set;
import java.util.List;
import org.eclipse.jetty.util.log.Logger;

public class ConstraintSecurityHandler extends SecurityHandler implements ConstraintAware
{
    private static final Logger LOG;
    private static final String OMISSION_SUFFIX = ".omission";
    private static final String ALL_METHODS = "*";
    private final List<ConstraintMapping> _constraintMappings;
    private final Set<String> _roles;
    private final PathMap<Map<String, RoleInfo>> _constraintMap;
    private boolean _denyUncoveredMethods;
    
    public ConstraintSecurityHandler() {
        this._constraintMappings = new CopyOnWriteArrayList<ConstraintMapping>();
        this._roles = new CopyOnWriteArraySet<String>();
        this._constraintMap = new PathMap<Map<String, RoleInfo>>();
        this._denyUncoveredMethods = false;
    }
    
    public static Constraint createConstraint() {
        return new Constraint();
    }
    
    public static Constraint createConstraint(final Constraint constraint) {
        try {
            return (Constraint)constraint.clone();
        }
        catch (CloneNotSupportedException e) {
            throw new IllegalStateException(e);
        }
    }
    
    public static Constraint createConstraint(final String name, final boolean authenticate, final String[] roles, final int dataConstraint) {
        final Constraint constraint = createConstraint();
        if (name != null) {
            constraint.setName(name);
        }
        constraint.setAuthenticate(authenticate);
        constraint.setRoles(roles);
        constraint.setDataConstraint(dataConstraint);
        return constraint;
    }
    
    public static Constraint createConstraint(final String name, final HttpConstraintElement element) {
        return createConstraint(name, element.getRolesAllowed(), element.getEmptyRoleSemantic(), element.getTransportGuarantee());
    }
    
    public static Constraint createConstraint(final String name, final String[] rolesAllowed, final ServletSecurity.EmptyRoleSemantic permitOrDeny, final ServletSecurity.TransportGuarantee transport) {
        final Constraint constraint = createConstraint();
        if (rolesAllowed == null || rolesAllowed.length == 0) {
            if (permitOrDeny.equals(ServletSecurity.EmptyRoleSemantic.DENY)) {
                constraint.setName(name + "-Deny");
                constraint.setAuthenticate(true);
            }
            else {
                constraint.setName(name + "-Permit");
                constraint.setAuthenticate(false);
            }
        }
        else {
            constraint.setAuthenticate(true);
            constraint.setRoles(rolesAllowed);
            constraint.setName(name + "-RolesAllowed");
        }
        constraint.setDataConstraint(transport.equals(ServletSecurity.TransportGuarantee.CONFIDENTIAL) ? 2 : 0);
        return constraint;
    }
    
    public static List<ConstraintMapping> getConstraintMappingsForPath(final String pathSpec, final List<ConstraintMapping> constraintMappings) {
        if (pathSpec == null || "".equals(pathSpec.trim()) || constraintMappings == null || constraintMappings.size() == 0) {
            return Collections.emptyList();
        }
        final List<ConstraintMapping> mappings = new ArrayList<ConstraintMapping>();
        for (final ConstraintMapping mapping : constraintMappings) {
            if (pathSpec.equals(mapping.getPathSpec())) {
                mappings.add(mapping);
            }
        }
        return mappings;
    }
    
    public static List<ConstraintMapping> removeConstraintMappingsForPath(final String pathSpec, final List<ConstraintMapping> constraintMappings) {
        if (pathSpec == null || "".equals(pathSpec.trim()) || constraintMappings == null || constraintMappings.size() == 0) {
            return Collections.emptyList();
        }
        final List<ConstraintMapping> mappings = new ArrayList<ConstraintMapping>();
        for (final ConstraintMapping mapping : constraintMappings) {
            if (!pathSpec.equals(mapping.getPathSpec())) {
                mappings.add(mapping);
            }
        }
        return mappings;
    }
    
    public static List<ConstraintMapping> createConstraintsWithMappingsForPath(final String name, final String pathSpec, final ServletSecurityElement securityElement) {
        final List<ConstraintMapping> mappings = new ArrayList<ConstraintMapping>();
        Constraint httpConstraint = null;
        ConstraintMapping httpConstraintMapping = null;
        if (securityElement.getEmptyRoleSemantic() != ServletSecurity.EmptyRoleSemantic.PERMIT || securityElement.getRolesAllowed().length != 0 || securityElement.getTransportGuarantee() != ServletSecurity.TransportGuarantee.NONE) {
            httpConstraint = createConstraint(name, securityElement);
            httpConstraintMapping = new ConstraintMapping();
            httpConstraintMapping.setPathSpec(pathSpec);
            httpConstraintMapping.setConstraint(httpConstraint);
            mappings.add(httpConstraintMapping);
        }
        final List<String> methodOmissions = new ArrayList<String>();
        final Collection<HttpMethodConstraintElement> methodConstraintElements = securityElement.getHttpMethodConstraints();
        if (methodConstraintElements != null) {
            for (final HttpMethodConstraintElement methodConstraintElement : methodConstraintElements) {
                final Constraint methodConstraint = createConstraint(name, methodConstraintElement);
                final ConstraintMapping mapping = new ConstraintMapping();
                mapping.setConstraint(methodConstraint);
                mapping.setPathSpec(pathSpec);
                if (methodConstraintElement.getMethodName() != null) {
                    mapping.setMethod(methodConstraintElement.getMethodName());
                    methodOmissions.add(methodConstraintElement.getMethodName());
                }
                mappings.add(mapping);
            }
        }
        if (methodOmissions.size() > 0 && httpConstraintMapping != null) {
            httpConstraintMapping.setMethodOmissions(methodOmissions.toArray(new String[methodOmissions.size()]));
        }
        return mappings;
    }
    
    @Override
    public List<ConstraintMapping> getConstraintMappings() {
        return this._constraintMappings;
    }
    
    @Override
    public Set<String> getRoles() {
        return this._roles;
    }
    
    public void setConstraintMappings(final List<ConstraintMapping> constraintMappings) {
        this.setConstraintMappings(constraintMappings, null);
    }
    
    public void setConstraintMappings(final ConstraintMapping[] constraintMappings) {
        this.setConstraintMappings(Arrays.asList(constraintMappings), null);
    }
    
    @Override
    public void setConstraintMappings(final List<ConstraintMapping> constraintMappings, Set<String> roles) {
        this._constraintMappings.clear();
        this._constraintMappings.addAll(constraintMappings);
        if (roles == null) {
            roles = new HashSet<String>();
            for (final ConstraintMapping cm : constraintMappings) {
                final String[] cmr = cm.getConstraint().getRoles();
                if (cmr != null) {
                    for (final String r : cmr) {
                        if (!"*".equals(r)) {
                            roles.add(r);
                        }
                    }
                }
            }
        }
        this.setRoles(roles);
        if (this.isStarted()) {
            for (final ConstraintMapping mapping : this._constraintMappings) {
                this.processConstraintMapping(mapping);
            }
        }
    }
    
    public void setRoles(final Set<String> roles) {
        this._roles.clear();
        this._roles.addAll(roles);
    }
    
    @Override
    public void addConstraintMapping(final ConstraintMapping mapping) {
        this._constraintMappings.add(mapping);
        if (mapping.getConstraint() != null && mapping.getConstraint().getRoles() != null) {
            for (final String role : mapping.getConstraint().getRoles()) {
                if (!"*".equals(role)) {
                    if (!"**".equals(role)) {
                        this.addRole(role);
                    }
                }
            }
        }
        if (this.isStarted()) {
            this.processConstraintMapping(mapping);
        }
    }
    
    @Override
    public void addRole(final String role) {
        final boolean modified = this._roles.add(role);
        if (this.isStarted() && modified) {
            for (final Map<String, RoleInfo> map : this._constraintMap.values()) {
                for (final RoleInfo info : map.values()) {
                    if (info.isAnyRole()) {
                        info.addRole(role);
                    }
                }
            }
        }
    }
    
    @Override
    protected void doStart() throws Exception {
        this._constraintMap.clear();
        if (this._constraintMappings != null) {
            for (final ConstraintMapping mapping : this._constraintMappings) {
                this.processConstraintMapping(mapping);
            }
        }
        this.checkPathsWithUncoveredHttpMethods();
        super.doStart();
    }
    
    @Override
    protected void doStop() throws Exception {
        super.doStop();
        this._constraintMap.clear();
    }
    
    protected void processConstraintMapping(final ConstraintMapping mapping) {
        Map<String, RoleInfo> mappings = this._constraintMap.get(mapping.getPathSpec());
        if (mappings == null) {
            mappings = new HashMap<String, RoleInfo>();
            this._constraintMap.put(mapping.getPathSpec(), mappings);
        }
        final RoleInfo allMethodsRoleInfo = mappings.get("*");
        if (allMethodsRoleInfo != null && allMethodsRoleInfo.isForbidden()) {
            return;
        }
        if (mapping.getMethodOmissions() != null && mapping.getMethodOmissions().length > 0) {
            this.processConstraintMappingWithMethodOmissions(mapping, mappings);
            return;
        }
        String httpMethod = mapping.getMethod();
        if (httpMethod == null) {
            httpMethod = "*";
        }
        RoleInfo roleInfo = mappings.get(httpMethod);
        if (roleInfo == null) {
            roleInfo = new RoleInfo();
            mappings.put(httpMethod, roleInfo);
            if (allMethodsRoleInfo != null) {
                roleInfo.combine(allMethodsRoleInfo);
            }
        }
        if (roleInfo.isForbidden()) {
            return;
        }
        this.configureRoleInfo(roleInfo, mapping);
        if (roleInfo.isForbidden() && httpMethod.equals("*")) {
            mappings.clear();
            mappings.put("*", roleInfo);
        }
    }
    
    protected void processConstraintMappingWithMethodOmissions(final ConstraintMapping mapping, final Map<String, RoleInfo> mappings) {
        final String[] omissions = mapping.getMethodOmissions();
        final StringBuilder sb = new StringBuilder();
        for (int i = 0; i < omissions.length; ++i) {
            if (i > 0) {
                sb.append(".");
            }
            sb.append(omissions[i]);
        }
        sb.append(".omission");
        final RoleInfo ri = new RoleInfo();
        mappings.put(sb.toString(), ri);
        this.configureRoleInfo(ri, mapping);
    }
    
    protected void configureRoleInfo(final RoleInfo ri, final ConstraintMapping mapping) {
        final Constraint constraint = mapping.getConstraint();
        final boolean forbidden = constraint.isForbidden();
        ri.setForbidden(forbidden);
        final UserDataConstraint userDataConstraint = UserDataConstraint.get(mapping.getConstraint().getDataConstraint());
        ri.setUserDataConstraint(userDataConstraint);
        if (!ri.isForbidden()) {
            final boolean checked = mapping.getConstraint().getAuthenticate();
            ri.setChecked(checked);
            if (ri.isChecked()) {
                if (mapping.getConstraint().isAnyRole()) {
                    for (final String role : this._roles) {
                        ri.addRole(role);
                    }
                    ri.setAnyRole(true);
                }
                else if (mapping.getConstraint().isAnyAuth()) {
                    ri.setAnyAuth(true);
                }
                else {
                    final String[] roles;
                    final String[] newRoles = roles = mapping.getConstraint().getRoles();
                    for (final String role2 : roles) {
                        if (!this._roles.contains(role2)) {
                            throw new IllegalArgumentException("Attempt to use undeclared role: " + role2 + ", known roles: " + this._roles);
                        }
                        ri.addRole(role2);
                    }
                }
            }
        }
    }
    
    @Override
    protected RoleInfo prepareConstraintInfo(final String pathInContext, final Request request) {
        final Map<String, RoleInfo> mappings = this._constraintMap.match(pathInContext);
        if (mappings != null) {
            final String httpMethod = request.getMethod();
            RoleInfo roleInfo = mappings.get(httpMethod);
            if (roleInfo == null) {
                final List<RoleInfo> applicableConstraints = new ArrayList<RoleInfo>();
                final RoleInfo all = mappings.get("*");
                if (all != null) {
                    applicableConstraints.add(all);
                }
                for (final Map.Entry<String, RoleInfo> entry : mappings.entrySet()) {
                    if (entry.getKey() != null && entry.getKey().endsWith(".omission") && !entry.getKey().contains(httpMethod)) {
                        applicableConstraints.add(entry.getValue());
                    }
                }
                if (applicableConstraints.size() == 0 && this.isDenyUncoveredHttpMethods()) {
                    roleInfo = new RoleInfo();
                    roleInfo.setForbidden(true);
                }
                else if (applicableConstraints.size() == 1) {
                    roleInfo = applicableConstraints.get(0);
                }
                else {
                    roleInfo = new RoleInfo();
                    roleInfo.setUserDataConstraint(UserDataConstraint.None);
                    for (final RoleInfo r : applicableConstraints) {
                        roleInfo.combine(r);
                    }
                }
            }
            return roleInfo;
        }
        return null;
    }
    
    @Override
    protected boolean checkUserDataPermissions(final String pathInContext, final Request request, final Response response, final RoleInfo roleInfo) throws IOException {
        if (roleInfo == null) {
            return true;
        }
        if (roleInfo.isForbidden()) {
            return false;
        }
        final UserDataConstraint dataConstraint = roleInfo.getUserDataConstraint();
        if (dataConstraint == null || dataConstraint == UserDataConstraint.None) {
            return true;
        }
        final HttpConfiguration httpConfig = Request.getBaseRequest(request).getHttpChannel().getHttpConfiguration();
        if (dataConstraint != UserDataConstraint.Confidential && dataConstraint != UserDataConstraint.Integral) {
            throw new IllegalArgumentException("Invalid dataConstraint value: " + dataConstraint);
        }
        if (request.isSecure()) {
            return true;
        }
        if (httpConfig.getSecurePort() > 0) {
            final String scheme = httpConfig.getSecureScheme();
            final int port = httpConfig.getSecurePort();
            final String url = URIUtil.newURI(scheme, request.getServerName(), port, request.getRequestURI(), request.getQueryString());
            response.setContentLength(0);
            response.sendRedirect(url);
        }
        else {
            response.sendError(403, "!Secure");
        }
        request.setHandled(true);
        return false;
    }
    
    @Override
    protected boolean isAuthMandatory(final Request baseRequest, final Response base_response, final Object constraintInfo) {
        return constraintInfo != null && ((RoleInfo)constraintInfo).isChecked();
    }
    
    @Override
    protected boolean checkWebResourcePermissions(final String pathInContext, final Request request, final Response response, final Object constraintInfo, final UserIdentity userIdentity) throws IOException {
        if (constraintInfo == null) {
            return true;
        }
        final RoleInfo roleInfo = (RoleInfo)constraintInfo;
        if (!roleInfo.isChecked()) {
            return true;
        }
        if (roleInfo.isAnyAuth() && request.getUserPrincipal() != null) {
            return true;
        }
        boolean isUserInRole = false;
        for (final String role : roleInfo.getRoles()) {
            if (userIdentity.isUserInRole(role, null)) {
                isUserInRole = true;
                break;
            }
        }
        return (roleInfo.isAnyRole() && request.getUserPrincipal() != null && isUserInRole) || isUserInRole;
    }
    
    @Override
    public void dump(final Appendable out, final String indent) throws IOException {
        this.dumpBeans(out, indent, Collections.singleton(this.getLoginService()), Collections.singleton(this.getIdentityService()), Collections.singleton(this.getAuthenticator()), Collections.singleton(this._roles), this._constraintMap.entrySet());
    }
    
    @Override
    public void setDenyUncoveredHttpMethods(final boolean deny) {
        this._denyUncoveredMethods = deny;
    }
    
    @Override
    public boolean isDenyUncoveredHttpMethods() {
        return this._denyUncoveredMethods;
    }
    
    @Override
    public boolean checkPathsWithUncoveredHttpMethods() {
        final Set<String> paths = this.getPathsWithUncoveredHttpMethods();
        if (paths != null && !paths.isEmpty()) {
            for (final String p : paths) {
                ConstraintSecurityHandler.LOG.warn("{} has uncovered http methods for path: {}", ContextHandler.getCurrentContext(), p);
            }
            if (ConstraintSecurityHandler.LOG.isDebugEnabled()) {
                ConstraintSecurityHandler.LOG.debug(new Throwable());
            }
            return true;
        }
        return false;
    }
    
    public Set<String> getPathsWithUncoveredHttpMethods() {
        if (this._denyUncoveredMethods) {
            return Collections.emptySet();
        }
        final Set<String> uncoveredPaths = new HashSet<String>();
        for (final String path : this._constraintMap.keySet()) {
            final Map<String, RoleInfo> methodMappings = this._constraintMap.get(path);
            if (methodMappings.get("*") != null) {
                continue;
            }
            final boolean hasOmissions = this.omissionsExist(path, methodMappings);
            for (final String method : methodMappings.keySet()) {
                if (method.endsWith(".omission")) {
                    final Set<String> omittedMethods = this.getOmittedMethods(method);
                    for (final String m : omittedMethods) {
                        if (!methodMappings.containsKey(m)) {
                            uncoveredPaths.add(path);
                        }
                    }
                }
                else {
                    if (hasOmissions) {
                        continue;
                    }
                    uncoveredPaths.add(path);
                }
            }
        }
        return uncoveredPaths;
    }
    
    protected boolean omissionsExist(final String path, final Map<String, RoleInfo> methodMappings) {
        if (methodMappings == null) {
            return false;
        }
        boolean hasOmissions = false;
        for (final String m : methodMappings.keySet()) {
            if (m.endsWith(".omission")) {
                hasOmissions = true;
            }
        }
        return hasOmissions;
    }
    
    protected Set<String> getOmittedMethods(final String omission) {
        if (omission == null || !omission.endsWith(".omission")) {
            return Collections.emptySet();
        }
        final String[] strings = omission.split("\\.");
        final Set<String> methods = new HashSet<String>();
        for (int i = 0; i < strings.length - 1; ++i) {
            methods.add(strings[i]);
        }
        return methods;
    }
    
    static {
        LOG = Log.getLogger(SecurityHandler.class);
    }
}
