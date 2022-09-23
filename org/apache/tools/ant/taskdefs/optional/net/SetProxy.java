// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.tools.ant.taskdefs.optional.net;

import java.net.PasswordAuthentication;
import org.apache.tools.ant.BuildException;
import java.util.Properties;
import java.net.Authenticator;
import org.apache.tools.ant.Task;

public class SetProxy extends Task
{
    private static final int HTTP_PORT = 80;
    private static final int SOCKS_PORT = 1080;
    protected String proxyHost;
    protected int proxyPort;
    private String socksProxyHost;
    private int socksProxyPort;
    private String nonProxyHosts;
    private String proxyUser;
    private String proxyPassword;
    
    public SetProxy() {
        this.proxyHost = null;
        this.proxyPort = 80;
        this.socksProxyHost = null;
        this.socksProxyPort = 1080;
        this.nonProxyHosts = null;
        this.proxyUser = null;
        this.proxyPassword = null;
    }
    
    public void setProxyHost(final String hostname) {
        this.proxyHost = hostname;
    }
    
    public void setProxyPort(final int port) {
        this.proxyPort = port;
    }
    
    public void setSocksProxyHost(final String host) {
        this.socksProxyHost = host;
    }
    
    public void setSocksProxyPort(final int port) {
        this.socksProxyPort = port;
    }
    
    public void setNonProxyHosts(final String nonProxyHosts) {
        this.nonProxyHosts = nonProxyHosts;
    }
    
    public void setProxyUser(final String proxyUser) {
        this.proxyUser = proxyUser;
    }
    
    public void setProxyPassword(final String proxyPassword) {
        this.proxyPassword = proxyPassword;
    }
    
    public void applyWebProxySettings() {
        boolean settingsChanged = false;
        boolean enablingProxy = false;
        final Properties sysprops = System.getProperties();
        if (this.proxyHost != null) {
            settingsChanged = true;
            if (this.proxyHost.length() != 0) {
                this.traceSettingInfo();
                enablingProxy = true;
                sysprops.put("http.proxyHost", this.proxyHost);
                final String portString = Integer.toString(this.proxyPort);
                sysprops.put("http.proxyPort", portString);
                sysprops.put("https.proxyHost", this.proxyHost);
                sysprops.put("https.proxyPort", portString);
                sysprops.put("ftp.proxyHost", this.proxyHost);
                sysprops.put("ftp.proxyPort", portString);
                if (this.nonProxyHosts != null) {
                    sysprops.put("http.nonProxyHosts", this.nonProxyHosts);
                    sysprops.put("https.nonProxyHosts", this.nonProxyHosts);
                    sysprops.put("ftp.nonProxyHosts", this.nonProxyHosts);
                }
                if (this.proxyUser != null) {
                    sysprops.put("http.proxyUser", this.proxyUser);
                    sysprops.put("http.proxyPassword", this.proxyPassword);
                }
            }
            else {
                this.log("resetting http proxy", 3);
                sysprops.remove("http.proxyHost");
                sysprops.remove("http.proxyPort");
                sysprops.remove("http.proxyUser");
                sysprops.remove("http.proxyPassword");
                sysprops.remove("https.proxyHost");
                sysprops.remove("https.proxyPort");
                sysprops.remove("ftp.proxyHost");
                sysprops.remove("ftp.proxyPort");
            }
        }
        if (this.socksProxyHost != null) {
            settingsChanged = true;
            if (this.socksProxyHost.length() != 0) {
                enablingProxy = true;
                sysprops.put("socksProxyHost", this.socksProxyHost);
                sysprops.put("socksProxyPort", Integer.toString(this.socksProxyPort));
                if (this.proxyUser != null) {
                    sysprops.put("java.net.socks.username", this.proxyUser);
                    sysprops.put("java.net.socks.password", this.proxyPassword);
                }
            }
            else {
                this.log("resetting socks proxy", 3);
                sysprops.remove("socksProxyHost");
                sysprops.remove("socksProxyPort");
                sysprops.remove("java.net.socks.username");
                sysprops.remove("java.net.socks.password");
            }
        }
        if (this.proxyUser != null) {
            if (enablingProxy) {
                Authenticator.setDefault(new ProxyAuth(this.proxyUser, this.proxyPassword));
            }
            else if (settingsChanged) {
                Authenticator.setDefault(new ProxyAuth("", ""));
            }
        }
    }
    
    private void traceSettingInfo() {
        this.log("Setting proxy to " + ((this.proxyHost != null) ? this.proxyHost : "''") + ":" + this.proxyPort, 3);
    }
    
    @Override
    public void execute() throws BuildException {
        this.applyWebProxySettings();
    }
    
    private static final class ProxyAuth extends Authenticator
    {
        private PasswordAuthentication auth;
        
        private ProxyAuth(final String user, final String pass) {
            this.auth = new PasswordAuthentication(user, pass.toCharArray());
        }
        
        @Override
        protected PasswordAuthentication getPasswordAuthentication() {
            return this.auth;
        }
    }
}
