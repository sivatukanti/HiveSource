// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.tools.ant.taskdefs;

import java.util.Arrays;
import java.util.Iterator;
import java.net.Inet6Address;
import java.net.Inet4Address;
import java.util.Enumeration;
import java.net.NetworkInterface;
import java.util.LinkedList;
import org.apache.tools.ant.BuildException;
import java.util.List;
import java.net.InetAddress;
import org.apache.tools.ant.Task;

public class HostInfo extends Task
{
    private static final String DEF_REM_ADDR6 = "::";
    private static final String DEF_REM_ADDR4 = "0.0.0.0";
    private static final String DEF_LOCAL_ADDR6 = "::1";
    private static final String DEF_LOCAL_ADDR4 = "127.0.0.1";
    private static final String DEF_LOCAL_NAME = "localhost";
    private static final String DEF_DOMAIN = "localdomain";
    private static final String DOMAIN = "DOMAIN";
    private static final String NAME = "NAME";
    private static final String ADDR4 = "ADDR4";
    private static final String ADDR6 = "ADDR6";
    private String prefix;
    private String host;
    private InetAddress nameAddr;
    private InetAddress best6;
    private InetAddress best4;
    private List<InetAddress> inetAddrs;
    
    public HostInfo() {
        this.prefix = "";
    }
    
    public void setPrefix(final String aPrefix) {
        this.prefix = aPrefix;
        if (!this.prefix.endsWith(".")) {
            this.prefix += ".";
        }
    }
    
    public void setHost(final String aHost) {
        this.host = aHost;
    }
    
    @Override
    public void execute() throws BuildException {
        if (this.host == null || "".equals(this.host)) {
            this.executeLocal();
        }
        else {
            this.executeRemote();
        }
    }
    
    private void executeLocal() {
        try {
            this.inetAddrs = new LinkedList<InetAddress>();
            final Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
            while (interfaces.hasMoreElements()) {
                final NetworkInterface currentif = interfaces.nextElement();
                final Enumeration<InetAddress> addrs = currentif.getInetAddresses();
                while (addrs.hasMoreElements()) {
                    this.inetAddrs.add(addrs.nextElement());
                }
            }
            this.selectAddresses();
            if (this.nameAddr != null && this.hasHostName(this.nameAddr)) {
                this.setDomainAndName(this.nameAddr.getCanonicalHostName());
            }
            else {
                this.setProperty("DOMAIN", "localdomain");
                this.setProperty("NAME", "localhost");
            }
            if (this.best4 != null) {
                this.setProperty("ADDR4", this.best4.getHostAddress());
            }
            else {
                this.setProperty("ADDR4", "127.0.0.1");
            }
            if (this.best6 != null) {
                this.setProperty("ADDR6", this.best6.getHostAddress());
            }
            else {
                this.setProperty("ADDR6", "::1");
            }
        }
        catch (Exception e) {
            this.log("Error retrieving local host information", e, 1);
            this.setProperty("DOMAIN", "localdomain");
            this.setProperty("NAME", "localhost");
            this.setProperty("ADDR4", "127.0.0.1");
            this.setProperty("ADDR6", "::1");
        }
    }
    
    private boolean hasHostName(final InetAddress addr) {
        return !addr.getHostAddress().equals(addr.getCanonicalHostName());
    }
    
    private void selectAddresses() {
        for (final InetAddress current : this.inetAddrs) {
            if (!current.isMulticastAddress()) {
                if (current instanceof Inet4Address) {
                    this.best4 = this.selectBestAddress(this.best4, current);
                }
                else {
                    if (!(current instanceof Inet6Address)) {
                        continue;
                    }
                    this.best6 = this.selectBestAddress(this.best6, current);
                }
            }
        }
        this.nameAddr = this.selectBestAddress(this.best4, this.best6);
    }
    
    private InetAddress selectBestAddress(final InetAddress bestSoFar, final InetAddress current) {
        InetAddress best = bestSoFar;
        if (best == null) {
            best = current;
        }
        else if (current != null) {
            if (!current.isLoopbackAddress()) {
                if (current.isLinkLocalAddress()) {
                    if (best.isLoopbackAddress()) {
                        best = current;
                    }
                }
                else if (current.isSiteLocalAddress()) {
                    if (best.isLoopbackAddress() || best.isLinkLocalAddress() || (best.isSiteLocalAddress() && !this.hasHostName(best))) {
                        best = current;
                    }
                }
                else if (best.isLoopbackAddress() || best.isLinkLocalAddress() || best.isSiteLocalAddress() || !this.hasHostName(best)) {
                    best = current;
                }
            }
        }
        return best;
    }
    
    private void executeRemote() {
        try {
            this.inetAddrs = Arrays.asList(InetAddress.getAllByName(this.host));
            this.selectAddresses();
            if (this.nameAddr != null && this.hasHostName(this.nameAddr)) {
                this.setDomainAndName(this.nameAddr.getCanonicalHostName());
            }
            else {
                this.setDomainAndName(this.host);
            }
            if (this.best4 != null) {
                this.setProperty("ADDR4", this.best4.getHostAddress());
            }
            else {
                this.setProperty("ADDR4", "0.0.0.0");
            }
            if (this.best6 != null) {
                this.setProperty("ADDR6", this.best6.getHostAddress());
            }
            else {
                this.setProperty("ADDR6", "::");
            }
        }
        catch (Exception e) {
            this.log("Error retrieving remote host information for host:" + this.host + ".", e, 1);
            this.setDomainAndName(this.host);
            this.setProperty("ADDR4", "0.0.0.0");
            this.setProperty("ADDR6", "::");
        }
    }
    
    private void setDomainAndName(final String fqdn) {
        final int idx = fqdn.indexOf(46);
        if (idx > 0) {
            this.setProperty("NAME", fqdn.substring(0, idx));
            this.setProperty("DOMAIN", fqdn.substring(idx + 1));
        }
        else {
            this.setProperty("NAME", fqdn);
            this.setProperty("DOMAIN", "localdomain");
        }
    }
    
    private void setProperty(final String name, final String value) {
        this.getProject().setNewProperty(this.prefix + name, value);
    }
}
