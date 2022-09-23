// 
// Decompiled by Procyon v0.5.36
// 

package org.mortbay.jetty.security;

import java.util.StringTokenizer;
import java.io.Reader;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashSet;
import org.mortbay.log.Log;
import java.security.Principal;
import javax.servlet.ServletException;
import java.io.IOException;
import java.util.Map;
import org.mortbay.resource.Resource;
import org.mortbay.jetty.handler.ContextHandler;
import org.mortbay.util.URIUtil;
import org.mortbay.util.StringUtil;
import org.mortbay.jetty.Response;
import org.mortbay.jetty.HttpConnection;
import org.mortbay.jetty.Request;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import org.mortbay.log.Logger;
import org.mortbay.jetty.Handler;

public class HTAccessHandler extends SecurityHandler
{
    private Handler protegee;
    private static Logger log;
    String _default;
    String _accessFile;
    transient HashMap _htCache;
    
    public HTAccessHandler() {
        this._default = null;
        this._accessFile = ".htaccess";
        this._htCache = new HashMap();
    }
    
    public void handle(final String target, final HttpServletRequest request, final HttpServletResponse response, final int dispatch) throws IOException, ServletException {
        final Request base_request = (Request)((request instanceof Request) ? request : HttpConnection.getCurrentConnection().getRequest());
        final Response base_response = (Response)((response instanceof Response) ? response : HttpConnection.getCurrentConnection().getResponse());
        final String pathInContext = target;
        String user = null;
        String password = null;
        boolean IPValid = true;
        if (HTAccessHandler.log.isDebugEnabled()) {
            HTAccessHandler.log.debug("HTAccessHandler pathInContext=" + pathInContext, null, null);
        }
        String credentials = request.getHeader("Authorization");
        if (credentials != null) {
            credentials = credentials.substring(credentials.indexOf(32) + 1);
            credentials = B64Code.decode(credentials, StringUtil.__ISO_8859_1);
            final int i = credentials.indexOf(58);
            user = credentials.substring(0, i);
            password = credentials.substring(i + 1);
            if (HTAccessHandler.log.isDebugEnabled()) {
                HTAccessHandler.log.debug("User=" + user + ", password=" + "******************************".substring(0, password.length()), null, null);
            }
        }
        HTAccess ht = null;
        try {
            Resource resource = null;
            for (String directory = pathInContext.endsWith("/") ? pathInContext : URIUtil.parentPath(pathInContext); directory != null; directory = URIUtil.parentPath(directory)) {
                final String htPath = directory + this._accessFile;
                resource = ((ContextHandler)this.getProtegee()).getResource(htPath);
                if (HTAccessHandler.log.isDebugEnabled()) {
                    HTAccessHandler.log.debug("directory=" + directory + " resource=" + resource, null, null);
                }
                if (resource != null && resource.exists() && !resource.isDirectory()) {
                    break;
                }
                resource = null;
            }
            boolean haveHtAccess = true;
            if (resource == null && this._default != null) {
                resource = Resource.newResource(this._default);
                if (!resource.exists() || resource.isDirectory()) {
                    haveHtAccess = false;
                }
            }
            if (resource == null) {
                haveHtAccess = false;
            }
            if (pathInContext.endsWith(this._accessFile) || pathInContext.endsWith(this._accessFile + "~") || pathInContext.endsWith(this._accessFile + ".bak")) {
                response.sendError(403);
                base_request.setHandled(true);
                return;
            }
            if (haveHtAccess) {
                if (HTAccessHandler.log.isDebugEnabled()) {
                    HTAccessHandler.log.debug("HTACCESS=" + resource, null, null);
                }
                ht = this._htCache.get(resource);
                if (ht == null || ht.getLastModified() != resource.lastModified()) {
                    ht = new HTAccess(resource);
                    this._htCache.put(resource, ht);
                    if (HTAccessHandler.log.isDebugEnabled()) {
                        HTAccessHandler.log.debug("HTCache loaded " + ht, null, null);
                    }
                }
                if (ht.isForbidden()) {
                    HTAccessHandler.log.warn("Mis-configured htaccess: " + ht, null, null);
                    response.sendError(403);
                    base_request.setHandled(true);
                    return;
                }
                final Map methods = ht.getMethods();
                if (methods.size() > 0 && !methods.containsKey(request.getMethod())) {
                    this.callWrappedHandler(target, request, response, dispatch);
                    return;
                }
                final int satisfy = ht.getSatisfy();
                IPValid = ht.checkAccess("", request.getRemoteAddr());
                if (HTAccessHandler.log.isDebugEnabled()) {
                    HTAccessHandler.log.debug("IPValid = " + IPValid, null, null);
                }
                if (IPValid && satisfy == 0) {
                    this.callWrappedHandler(target, request, response, dispatch);
                    return;
                }
                if (!IPValid && satisfy == 1) {
                    response.sendError(403);
                    base_request.setHandled(true);
                    return;
                }
                if (!ht.checkAuth(user, password, this.getUserRealm(), base_request)) {
                    HTAccessHandler.log.debug("Auth Failed", null, null);
                    response.setHeader("WWW-Authenticate", "basic realm=" + ht.getName());
                    response.sendError(401);
                    base_response.complete();
                    base_request.setHandled(true);
                    return;
                }
                if (user != null) {
                    base_request.setAuthType("BASIC");
                    base_request.setUserPrincipal(this.getPrincipal(user, this.getUserRealm()));
                }
            }
            this.callWrappedHandler(target, request, response, dispatch);
        }
        catch (Exception ex) {
            HTAccessHandler.log.warn("Exception", ex);
            if (ht != null) {
                response.sendError(500);
                base_request.setHandled(true);
            }
        }
    }
    
    private void callWrappedHandler(final String target, final HttpServletRequest request, final HttpServletResponse response, final int dispatch) throws IOException, ServletException {
        final Handler handler = this.getHandler();
        if (handler != null) {
            handler.handle(target, request, response, dispatch);
        }
    }
    
    public Principal getPrincipal(final String user, final UserRealm realm) {
        if (realm == null) {
            return new DummyPrincipal(user);
        }
        return realm.getPrincipal(user);
    }
    
    public void setDefault(final String dir) {
        this._default = dir;
    }
    
    public void setAccessFile(final String anArg) {
        if (anArg == null) {
            this._accessFile = ".htaccess";
        }
        else {
            this._accessFile = anArg;
        }
    }
    
    protected Handler getProtegee() {
        return this.protegee;
    }
    
    public void setProtegee(final Handler protegee) {
        this.protegee = protegee;
    }
    
    static {
        HTAccessHandler.log = Log.getLogger(HTAccessHandler.class.getName());
    }
    
    class DummyPrincipal implements Principal
    {
        private String _userName;
        
        public DummyPrincipal(final String name) {
            this._userName = name;
        }
        
        public String getName() {
            return this._userName;
        }
        
        public String toString() {
            return this.getName();
        }
    }
    
    private static class HTAccess
    {
        static final int ANY = 0;
        static final int ALL = 1;
        static final String USER = "user";
        static final String GROUP = "group";
        static final String VALID_USER = "valid-user";
        String _userFile;
        Resource _userResource;
        HashMap _users;
        long _userModified;
        String _groupFile;
        Resource _groupResource;
        HashMap _groups;
        long _groupModified;
        int _satisfy;
        String _type;
        String _name;
        HashMap _methods;
        HashSet _requireEntities;
        String _requireName;
        int _order;
        ArrayList _allowList;
        ArrayList _denyList;
        long _lastModified;
        boolean _forbidden;
        
        public HTAccess(final Resource resource) {
            this._users = null;
            this._groups = null;
            this._satisfy = 0;
            this._methods = new HashMap();
            this._requireEntities = new HashSet();
            this._allowList = new ArrayList();
            this._denyList = new ArrayList();
            this._forbidden = false;
            BufferedReader htin = null;
            try {
                htin = new BufferedReader(new InputStreamReader(resource.getInputStream()));
                this.parse(htin);
                this._lastModified = resource.lastModified();
                if (this._userFile != null) {
                    this._userResource = Resource.newResource(this._userFile);
                    if (!this._userResource.exists()) {
                        this._forbidden = true;
                        HTAccessHandler.log.warn("Could not find ht user file: " + this._userFile, null, null);
                    }
                    else if (HTAccessHandler.log.isDebugEnabled()) {
                        HTAccessHandler.log.debug("user file: " + this._userResource, null, null);
                    }
                }
                if (this._groupFile != null) {
                    this._groupResource = Resource.newResource(this._groupFile);
                    if (!this._groupResource.exists()) {
                        this._forbidden = true;
                        HTAccessHandler.log.warn("Could not find ht group file: " + this._groupResource, null, null);
                    }
                    else if (HTAccessHandler.log.isDebugEnabled()) {
                        HTAccessHandler.log.debug("group file: " + this._groupResource, null, null);
                    }
                }
            }
            catch (IOException e) {
                this._forbidden = true;
                HTAccessHandler.log.warn("LogSupport.EXCEPTION", e);
            }
        }
        
        public boolean isForbidden() {
            return this._forbidden;
        }
        
        public HashMap getMethods() {
            return this._methods;
        }
        
        public long getLastModified() {
            return this._lastModified;
        }
        
        public Resource getUserResource() {
            return this._userResource;
        }
        
        public Resource getGroupResource() {
            return this._groupResource;
        }
        
        public int getSatisfy() {
            return this._satisfy;
        }
        
        public String getName() {
            return this._name;
        }
        
        public String getType() {
            return this._type;
        }
        
        public boolean checkAccess(final String host, final String ip) {
            boolean alp = false;
            boolean dep = false;
            if (this._allowList.size() == 0 && this._denyList.size() == 0) {
                return true;
            }
            for (int i = 0; i < this._allowList.size(); ++i) {
                final String elm = this._allowList.get(i);
                if (elm.equals("all")) {
                    alp = true;
                    break;
                }
                final char c = elm.charAt(0);
                if (c >= '0' && c <= '9') {
                    if (ip.startsWith(elm)) {
                        alp = true;
                        break;
                    }
                }
                else if (host.endsWith(elm)) {
                    alp = true;
                    break;
                }
            }
            for (int i = 0; i < this._denyList.size(); ++i) {
                final String elm = this._denyList.get(i);
                if (elm.equals("all")) {
                    dep = true;
                    break;
                }
                final char c = elm.charAt(0);
                if (c >= '0' && c <= '9') {
                    if (ip.startsWith(elm)) {
                        dep = true;
                        break;
                    }
                }
                else if (host.endsWith(elm)) {
                    dep = true;
                    break;
                }
            }
            if (this._order < 0) {
                return !dep || alp;
            }
            return alp && !dep;
        }
        
        public boolean checkAuth(final String user, final String pass, final UserRealm realm, final Request request) {
            if (this._requireName == null) {
                return true;
            }
            final Principal principal = (realm == null) ? null : realm.authenticate(user, pass, request);
            if (principal == null) {
                final String code = this.getUserCode(user);
                final String salt = (code != null) ? code.substring(0, 2) : user;
                final String cred = (user != null && pass != null) ? UnixCrypt.crypt(pass, salt) : null;
                if (code == null || (code.equals("") && !pass.equals("")) || !code.equals(cred)) {
                    return false;
                }
            }
            if (this._requireName.equalsIgnoreCase("user")) {
                if (this._requireEntities.contains(user)) {
                    return true;
                }
            }
            else if (this._requireName.equalsIgnoreCase("group")) {
                final ArrayList gps = this.getUserGroups(user);
                if (gps != null) {
                    int g = gps.size();
                    while (g-- > 0) {
                        if (this._requireEntities.contains(gps.get(g))) {
                            return true;
                        }
                    }
                }
            }
            else if (this._requireName.equalsIgnoreCase("valid-user")) {
                return true;
            }
            return false;
        }
        
        public boolean isAccessLimited() {
            return this._allowList.size() > 0 || this._denyList.size() > 0;
        }
        
        public boolean isAuthLimited() {
            return this._requireName != null;
        }
        
        private String getUserCode(final String user) {
            if (this._userResource == null) {
                return null;
            }
            if (this._users == null || this._userModified != this._userResource.lastModified()) {
                if (HTAccessHandler.log.isDebugEnabled()) {
                    HTAccessHandler.log.debug("LOAD " + this._userResource, null, null);
                }
                this._users = new HashMap();
                BufferedReader ufin = null;
                try {
                    ufin = new BufferedReader(new InputStreamReader(this._userResource.getInputStream()));
                    this._userModified = this._userResource.lastModified();
                    String line;
                    while ((line = ufin.readLine()) != null) {
                        line = line.trim();
                        if (line.startsWith("#")) {
                            continue;
                        }
                        final int spos = line.indexOf(58);
                        if (spos < 0) {
                            continue;
                        }
                        final String u = line.substring(0, spos).trim();
                        final String p = line.substring(spos + 1).trim();
                        this._users.put(u, p);
                    }
                }
                catch (IOException e) {
                    HTAccessHandler.log.warn("LogSupport.EXCEPTION", e);
                }
                finally {
                    try {
                        if (ufin != null) {
                            ufin.close();
                        }
                    }
                    catch (IOException e2) {
                        HTAccessHandler.log.warn("LogSupport.EXCEPTION", e2);
                    }
                }
            }
            return this._users.get(user);
        }
        
        private ArrayList getUserGroups(final String group) {
            if (this._groupResource == null) {
                return null;
            }
            if (this._groups == null || this._groupModified != this._groupResource.lastModified()) {
                if (HTAccessHandler.log.isDebugEnabled()) {
                    HTAccessHandler.log.debug("LOAD " + this._groupResource, null, null);
                }
                this._groups = new HashMap();
                BufferedReader ufin = null;
                try {
                    ufin = new BufferedReader(new InputStreamReader(this._groupResource.getInputStream()));
                    this._groupModified = this._groupResource.lastModified();
                    String line;
                    while ((line = ufin.readLine()) != null) {
                        line = line.trim();
                        if (!line.startsWith("#")) {
                            if (line.length() == 0) {
                                continue;
                            }
                            final StringTokenizer tok = new StringTokenizer(line, ": \t");
                            if (!tok.hasMoreTokens()) {
                                continue;
                            }
                            final String g = tok.nextToken();
                            if (!tok.hasMoreTokens()) {
                                continue;
                            }
                            while (tok.hasMoreTokens()) {
                                final String u = tok.nextToken();
                                ArrayList gl = this._groups.get(u);
                                if (gl == null) {
                                    gl = new ArrayList();
                                    this._groups.put(u, gl);
                                }
                                gl.add(g);
                            }
                        }
                    }
                }
                catch (IOException e) {
                    HTAccessHandler.log.warn("LogSupport.EXCEPTION", e);
                }
                finally {
                    try {
                        if (ufin != null) {
                            ufin.close();
                        }
                    }
                    catch (IOException e2) {
                        HTAccessHandler.log.warn("LogSupport.EXCEPTION", e2);
                    }
                }
            }
            return this._groups.get(group);
        }
        
        public String toString() {
            final StringBuffer buf = new StringBuffer();
            buf.append("AuthUserFile=");
            buf.append(this._userFile);
            buf.append(", AuthGroupFile=");
            buf.append(this._groupFile);
            buf.append(", AuthName=");
            buf.append(this._name);
            buf.append(", AuthType=");
            buf.append(this._type);
            buf.append(", Methods=");
            buf.append(this._methods);
            buf.append(", satisfy=");
            buf.append(this._satisfy);
            if (this._order < 0) {
                buf.append(", order=deny,allow");
            }
            else if (this._order > 0) {
                buf.append(", order=allow,deny");
            }
            else {
                buf.append(", order=mutual-failure");
            }
            buf.append(", Allow from=");
            buf.append(this._allowList);
            buf.append(", deny from=");
            buf.append(this._denyList);
            buf.append(", requireName=");
            buf.append(this._requireName);
            buf.append(" ");
            buf.append(this._requireEntities);
            return buf.toString();
        }
        
        private void parse(final BufferedReader htin) throws IOException {
            String line;
            while ((line = htin.readLine()) != null) {
                line = line.trim();
                if (line.startsWith("#")) {
                    continue;
                }
                if (line.startsWith("AuthUserFile")) {
                    this._userFile = line.substring(13).trim();
                }
                else if (line.startsWith("AuthGroupFile")) {
                    this._groupFile = line.substring(14).trim();
                }
                else if (line.startsWith("AuthName")) {
                    this._name = line.substring(8).trim();
                }
                else if (line.startsWith("AuthType")) {
                    this._type = line.substring(8).trim();
                }
                else {
                    if (!line.startsWith("<Limit")) {
                        continue;
                    }
                    int limit = line.length();
                    int endp = line.indexOf(62);
                    if (endp < 0) {
                        endp = limit;
                    }
                    StringTokenizer tkns = new StringTokenizer(line.substring(6, endp));
                    while (tkns.hasMoreTokens()) {
                        this._methods.put(tkns.nextToken(), Boolean.TRUE);
                    }
                    while ((line = htin.readLine()) != null) {
                        line = line.trim();
                        if (line.startsWith("#")) {
                            continue;
                        }
                        if (line.startsWith("satisfy")) {
                            int pos1;
                            for (pos1 = 7, limit = line.length(); pos1 < limit && line.charAt(pos1) <= ' '; ++pos1) {}
                            int pos2;
                            for (pos2 = pos1; pos2 < limit && line.charAt(pos2) > ' '; ++pos2) {}
                            final String l_string = line.substring(pos1, pos2);
                            if (l_string.equals("all")) {
                                this._satisfy = 1;
                            }
                            else {
                                if (!l_string.equals("any")) {
                                    continue;
                                }
                                this._satisfy = 0;
                            }
                        }
                        else if (line.startsWith("require")) {
                            int pos1;
                            for (pos1 = 7, limit = line.length(); pos1 < limit && line.charAt(pos1) <= ' '; ++pos1) {}
                            int pos2;
                            for (pos2 = pos1; pos2 < limit && line.charAt(pos2) > ' '; ++pos2) {}
                            this._requireName = line.substring(pos1, pos2).toLowerCase();
                            if ("user".equals(this._requireName)) {
                                this._requireName = "user";
                            }
                            else if ("group".equals(this._requireName)) {
                                this._requireName = "group";
                            }
                            else if ("valid-user".equals(this._requireName)) {
                                this._requireName = "valid-user";
                            }
                            pos1 = pos2 + 1;
                            if (pos1 >= limit) {
                                continue;
                            }
                            while (pos1 < limit && line.charAt(pos1) <= ' ') {
                                ++pos1;
                            }
                            tkns = new StringTokenizer(line.substring(pos1));
                            while (tkns.hasMoreTokens()) {
                                this._requireEntities.add(tkns.nextToken());
                            }
                        }
                        else if (line.startsWith("order")) {
                            if (HTAccessHandler.log.isDebugEnabled()) {
                                HTAccessHandler.log.debug("orderline=" + line + "order=" + this._order, null, null);
                            }
                            if (line.indexOf("allow,deny") > 0) {
                                HTAccessHandler.log.debug("==>allow+deny", null, null);
                                this._order = 1;
                            }
                            else if (line.indexOf("deny,allow") > 0) {
                                HTAccessHandler.log.debug("==>deny,allow", null, null);
                                this._order = -1;
                            }
                            else {
                                if (line.indexOf("mutual-failure") <= 0) {
                                    continue;
                                }
                                HTAccessHandler.log.debug("==>mutual", null, null);
                                this._order = 0;
                            }
                        }
                        else if (line.startsWith("allow from")) {
                            int pos1;
                            for (pos1 = 10, limit = line.length(); pos1 < limit && line.charAt(pos1) <= ' '; ++pos1) {}
                            if (HTAccessHandler.log.isDebugEnabled()) {
                                HTAccessHandler.log.debug("allow process:" + line.substring(pos1), null, null);
                            }
                            tkns = new StringTokenizer(line.substring(pos1));
                            while (tkns.hasMoreTokens()) {
                                this._allowList.add(tkns.nextToken());
                            }
                        }
                        else if (line.startsWith("deny from")) {
                            int pos1;
                            for (pos1 = 9, limit = line.length(); pos1 < limit && line.charAt(pos1) <= ' '; ++pos1) {}
                            if (HTAccessHandler.log.isDebugEnabled()) {
                                HTAccessHandler.log.debug("deny process:" + line.substring(pos1), null, null);
                            }
                            tkns = new StringTokenizer(line.substring(pos1));
                            while (tkns.hasMoreTokens()) {
                                this._denyList.add(tkns.nextToken());
                            }
                        }
                        else {
                            if (line.startsWith("</Limit>")) {
                                break;
                            }
                            continue;
                        }
                    }
                }
            }
        }
    }
}
