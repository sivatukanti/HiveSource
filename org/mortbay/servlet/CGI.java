// 
// Decompiled by Procyon v0.5.36
// 

package org.mortbay.servlet;

import java.util.HashMap;
import java.util.Map;
import java.io.InputStream;
import java.io.OutputStream;
import org.mortbay.util.IO;
import org.mortbay.util.StringUtil;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.ServletException;
import java.util.Enumeration;
import java.io.IOException;
import org.mortbay.log.Log;
import java.io.File;
import javax.servlet.http.HttpServlet;

public class CGI extends HttpServlet
{
    private boolean _ok;
    private File _docRoot;
    private String _path;
    private String _cmdPrefix;
    private EnvList _env;
    private boolean _ignoreExitState;
    
    public void init() throws ServletException {
        this._env = new EnvList();
        this._cmdPrefix = this.getInitParameter("commandPrefix");
        String tmp = this.getInitParameter("cgibinResourceBase");
        if (tmp == null) {
            tmp = this.getInitParameter("resourceBase");
            if (tmp == null) {
                tmp = this.getServletContext().getRealPath("/");
            }
        }
        if (tmp == null) {
            Log.warn("CGI: no CGI bin !");
            return;
        }
        final File dir = new File(tmp);
        if (!dir.exists()) {
            Log.warn("CGI: CGI bin does not exist - " + dir);
            return;
        }
        if (!dir.canRead()) {
            Log.warn("CGI: CGI bin is not readable - " + dir);
            return;
        }
        if (!dir.isDirectory()) {
            Log.warn("CGI: CGI bin is not a directory - " + dir);
            return;
        }
        try {
            this._docRoot = dir.getCanonicalFile();
        }
        catch (IOException e) {
            Log.warn("CGI: CGI bin failed - " + dir, e);
            return;
        }
        this._path = this.getInitParameter("Path");
        if (this._path != null) {
            this._env.set("PATH", this._path);
        }
        this._ignoreExitState = "true".equalsIgnoreCase(this.getInitParameter("ignoreExitState"));
        final Enumeration e2 = this.getInitParameterNames();
        while (e2.hasMoreElements()) {
            final String n = e2.nextElement();
            if (n != null && n.startsWith("ENV_")) {
                this._env.set(n.substring(4), this.getInitParameter(n));
            }
        }
        if (!this._env.envMap.containsKey("SystemRoot")) {
            final String os = System.getProperty("os.name");
            if (os != null && os.toLowerCase().indexOf("windows") != -1) {
                final String windir = System.getProperty("windir");
                this._env.set("SystemRoot", (windir != null) ? windir : "C:\\WINDOWS");
            }
        }
        this._ok = true;
    }
    
    public void service(final HttpServletRequest req, final HttpServletResponse res) throws ServletException, IOException {
        if (!this._ok) {
            res.sendError(503);
            return;
        }
        final String pathInContext = StringUtil.nonNull(req.getServletPath()) + StringUtil.nonNull(req.getPathInfo());
        if (Log.isDebugEnabled()) {
            Log.debug("CGI: ContextPath : " + req.getContextPath());
            Log.debug("CGI: ServletPath : " + req.getServletPath());
            Log.debug("CGI: PathInfo    : " + req.getPathInfo());
            Log.debug("CGI: _docRoot    : " + this._docRoot);
            Log.debug("CGI: _path       : " + this._path);
            Log.debug("CGI: _ignoreExitState: " + this._ignoreExitState);
        }
        String first;
        final String both = first = pathInContext;
        String last = "";
        File exe;
        int index;
        for (exe = new File(this._docRoot, first); (first.endsWith("/") || !exe.exists()) && first.length() >= 0; first = first.substring(0, index), last = both.substring(index, both.length()), exe = new File(this._docRoot, first)) {
            index = first.lastIndexOf(47);
        }
        if (first.length() == 0 || !exe.exists() || exe.isDirectory() || !exe.getCanonicalPath().equals(exe.getAbsolutePath())) {
            res.sendError(404);
        }
        else {
            if (Log.isDebugEnabled()) {
                Log.debug("CGI: script is " + exe);
                Log.debug("CGI: pathInfo is " + last);
            }
            this.exec(exe, last, req, res);
        }
    }
    
    private void exec(final File command, final String pathInfo, final HttpServletRequest req, final HttpServletResponse res) throws IOException {
        final String path = command.getAbsolutePath();
        final File dir = command.getParentFile();
        final String scriptName = req.getRequestURI().substring(0, req.getRequestURI().length() - pathInfo.length());
        final String scriptPath = this.getServletContext().getRealPath(scriptName);
        String pathTranslated = req.getPathTranslated();
        int len = req.getContentLength();
        if (len < 0) {
            len = 0;
        }
        if (pathTranslated == null || pathTranslated.length() == 0) {
            pathTranslated = path;
        }
        final EnvList env = new EnvList(this._env);
        env.set("AUTH_TYPE", req.getAuthType());
        env.set("CONTENT_LENGTH", Integer.toString(len));
        env.set("CONTENT_TYPE", req.getContentType());
        env.set("GATEWAY_INTERFACE", "CGI/1.1");
        if (pathInfo != null && pathInfo.length() > 0) {
            env.set("PATH_INFO", pathInfo);
        }
        env.set("PATH_TRANSLATED", pathTranslated);
        env.set("QUERY_STRING", req.getQueryString());
        env.set("REMOTE_ADDR", req.getRemoteAddr());
        env.set("REMOTE_HOST", req.getRemoteHost());
        env.set("REMOTE_USER", req.getRemoteUser());
        env.set("REQUEST_METHOD", req.getMethod());
        env.set("SCRIPT_NAME", scriptName);
        env.set("SCRIPT_FILENAME", scriptPath);
        env.set("SERVER_NAME", req.getServerName());
        env.set("SERVER_PORT", Integer.toString(req.getServerPort()));
        env.set("SERVER_PROTOCOL", req.getProtocol());
        env.set("SERVER_SOFTWARE", this.getServletContext().getServerInfo());
        final Enumeration enm = req.getHeaderNames();
        while (enm.hasMoreElements()) {
            final String name = enm.nextElement();
            final String value = req.getHeader(name);
            env.set("HTTP_" + name.toUpperCase().replace('-', '_'), value);
        }
        env.set("HTTPS", req.isSecure() ? "ON" : "OFF");
        String execCmd = path;
        if (execCmd.charAt(0) != '\"' && execCmd.indexOf(" ") >= 0) {
            execCmd = "\"" + execCmd + "\"";
        }
        if (this._cmdPrefix != null) {
            execCmd = this._cmdPrefix + " " + execCmd;
        }
        final Process p = (dir == null) ? Runtime.getRuntime().exec(execCmd, env.getEnvArray()) : Runtime.getRuntime().exec(execCmd, env.getEnvArray(), dir);
        final InputStream inFromReq = req.getInputStream();
        final OutputStream outToCgi = p.getOutputStream();
        final int inLength = len;
        IO.copyThread(p.getErrorStream(), System.err);
        new Thread(new Runnable() {
            public void run() {
                try {
                    if (inLength > 0) {
                        IO.copy(inFromReq, outToCgi, inLength);
                    }
                    outToCgi.close();
                }
                catch (IOException e) {
                    Log.ignore(e);
                }
            }
        }).start();
        OutputStream os = null;
        try {
            String line = null;
            final InputStream inFromCgi = p.getInputStream();
            while ((line = this.getTextLineFromStream(inFromCgi)).length() > 0) {
                if (!line.startsWith("HTTP")) {
                    final int k = line.indexOf(58);
                    if (k <= 0) {
                        continue;
                    }
                    final String key = line.substring(0, k).trim();
                    final String value2 = line.substring(k + 1).trim();
                    if ("Location".equals(key)) {
                        res.sendRedirect(value2);
                    }
                    else if ("Status".equals(key)) {
                        final String[] token = value2.split(" ");
                        final int status = Integer.parseInt(token[0]);
                        res.setStatus(status);
                    }
                    else {
                        res.addHeader(key, value2);
                    }
                }
            }
            os = res.getOutputStream();
            IO.copy(inFromCgi, os);
            p.waitFor();
            if (!this._ignoreExitState) {
                final int exitValue = p.exitValue();
                if (0 != exitValue) {
                    Log.warn("Non-zero exit status (" + exitValue + ") from CGI program: " + path);
                    if (!res.isCommitted()) {
                        res.sendError(500, "Failed to exec CGI");
                    }
                }
            }
        }
        catch (IOException e2) {
            Log.debug("CGI: Client closed connection!");
        }
        catch (InterruptedException ie) {
            Log.debug("CGI: interrupted!");
        }
        finally {
            if (os != null) {
                try {
                    os.close();
                }
                catch (Exception e) {
                    Log.ignore(e);
                }
            }
            os = null;
            p.destroy();
        }
    }
    
    private String getTextLineFromStream(final InputStream is) throws IOException {
        final StringBuffer buffer = new StringBuffer();
        int b;
        while ((b = is.read()) != -1 && b != 10) {
            buffer.append((char)b);
        }
        return buffer.toString().trim();
    }
    
    private static class EnvList
    {
        private Map envMap;
        
        EnvList() {
            this.envMap = new HashMap();
        }
        
        EnvList(final EnvList l) {
            this.envMap = new HashMap(l.envMap);
        }
        
        public void set(final String name, final String value) {
            this.envMap.put(name, name + "=" + StringUtil.nonNull(value));
        }
        
        public String[] getEnvArray() {
            return (String[])this.envMap.values().toArray(new String[this.envMap.size()]);
        }
        
        public String toString() {
            return this.envMap.toString();
        }
    }
}
