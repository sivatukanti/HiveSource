// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.htrace.core;

import org.apache.htrace.shaded.commons.logging.LogFactory;
import java.lang.management.ManagementFactory;
import java.io.IOException;
import java.io.Reader;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Locale;
import java.util.Enumeration;
import java.net.InetAddress;
import java.util.TreeSet;
import java.net.SocketException;
import java.net.NetworkInterface;
import org.apache.htrace.shaded.commons.logging.Log;

public final class TracerId
{
    private static final Log LOG;
    public static final String TRACER_ID_KEY = "tracer.id";
    private static final String DEFAULT_TRACER_ID = "%{tname}/%{ip}";
    private final String tracerName;
    private final String tracerId;
    
    public TracerId(final HTraceConfiguration conf, final String tracerName) {
        this.tracerName = tracerName;
        final String fmt = conf.get("tracer.id", "%{tname}/%{ip}");
        final StringBuilder bld = new StringBuilder();
        StringBuilder varBld = null;
        boolean escaping = false;
        int varSeen = 0;
        for (int i = 0, len = fmt.length(); i < len; ++i) {
            final char c = fmt.charAt(i);
            if (c == '\\' && !escaping) {
                escaping = true;
            }
            else {
                switch (varSeen) {
                    case 0: {
                        if (c == '%' && !escaping) {
                            varSeen = 1;
                            break;
                        }
                        escaping = false;
                        varSeen = 0;
                        bld.append(c);
                        break;
                    }
                    case 1: {
                        if (c == '{' && !escaping) {
                            varSeen = 2;
                            varBld = new StringBuilder();
                            break;
                        }
                        escaping = false;
                        varSeen = 0;
                        bld.append("%").append(c);
                        break;
                    }
                    default: {
                        if (c == '}' && !escaping) {
                            final String var = varBld.toString();
                            bld.append(this.processShellVar(var));
                            varBld = null;
                            varSeen = 0;
                            break;
                        }
                        escaping = false;
                        varBld.append(c);
                        ++varSeen;
                        break;
                    }
                }
            }
        }
        if (varSeen > 0) {
            TracerId.LOG.warn("Unterminated process ID substitution variable at the end of format string " + fmt);
        }
        this.tracerId = bld.toString();
        if (TracerId.LOG.isTraceEnabled()) {
            TracerId.LOG.trace("ProcessID(fmt=" + fmt + "): computed process ID of \"" + this.tracerId + "\"");
        }
    }
    
    private String processShellVar(final String var) {
        if (var.equals("tname")) {
            return this.tracerName;
        }
        if (var.equals("pname")) {
            return getProcessName();
        }
        if (var.equals("ip")) {
            return getBestIpString();
        }
        if (var.equals("pid")) {
            return Long.valueOf(getOsPid()).toString();
        }
        TracerId.LOG.warn("unknown ProcessID variable " + var);
        return "";
    }
    
    static String getProcessName() {
        String cmdLine = System.getProperty("sun.java.command");
        if (cmdLine != null && !cmdLine.isEmpty()) {
            final String fullClassName = cmdLine.split("\\s+")[0];
            final String[] classParts = fullClassName.split("\\.");
            cmdLine = classParts[classParts.length - 1];
        }
        return (cmdLine == null || cmdLine.isEmpty()) ? "Unknown" : cmdLine;
    }
    
    static String getBestIpString() {
        Enumeration<NetworkInterface> ifaces;
        try {
            ifaces = NetworkInterface.getNetworkInterfaces();
        }
        catch (SocketException e) {
            TracerId.LOG.error("Error getting network interfaces", e);
            return "127.0.0.1";
        }
        final TreeSet<String> siteLocalCandidates = new TreeSet<String>();
        final TreeSet<String> candidates = new TreeSet<String>();
        while (ifaces.hasMoreElements()) {
            final NetworkInterface iface = ifaces.nextElement();
            final Enumeration<InetAddress> addrs = iface.getInetAddresses();
            while (addrs.hasMoreElements()) {
                final InetAddress addr = addrs.nextElement();
                if (!addr.isLoopbackAddress()) {
                    if (addr.isSiteLocalAddress()) {
                        siteLocalCandidates.add(addr.getHostAddress());
                    }
                    else {
                        candidates.add(addr.getHostAddress());
                    }
                }
            }
        }
        if (!siteLocalCandidates.isEmpty()) {
            return siteLocalCandidates.first();
        }
        if (!candidates.isEmpty()) {
            return candidates.first();
        }
        return "127.0.0.1";
    }
    
    static long getOsPid() {
        if (System.getProperty("os.name", "generic").toLowerCase(Locale.ENGLISH).contains("windows")) {
            return getOsPidFromManagementFactory();
        }
        return getOsPidFromShellPpid();
    }
    
    private static long getOsPidFromShellPpid() {
        Process p = null;
        final StringBuilder sb = new StringBuilder();
        try {
            p = new ProcessBuilder(new String[] { "/usr/bin/env", "sh", "-c", "echo $PPID" }).redirectErrorStream(true).start();
            final BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));
            String line = "";
            while ((line = reader.readLine()) != null) {
                sb.append(line.trim());
            }
            final int exitVal = p.waitFor();
            if (exitVal != 0) {
                throw new IOException("Process exited with error code " + Integer.valueOf(exitVal).toString());
            }
        }
        catch (InterruptedException e) {
            TracerId.LOG.error("Interrupted while getting operating system pid from the shell.", e);
            return 0L;
        }
        catch (IOException e2) {
            TracerId.LOG.error("Error getting operating system pid from the shell.", e2);
            return 0L;
        }
        finally {
            if (p != null) {
                p.destroy();
            }
        }
        try {
            return Long.parseLong(sb.toString());
        }
        catch (NumberFormatException e3) {
            TracerId.LOG.error("Error parsing operating system pid from the shell.", e3);
            return 0L;
        }
    }
    
    private static long getOsPidFromManagementFactory() {
        try {
            return Long.parseLong(ManagementFactory.getRuntimeMXBean().getName().split("@")[0]);
        }
        catch (NumberFormatException e) {
            TracerId.LOG.error("Failed to get the operating system process ID from the name of the managed bean for the JVM.", e);
            return 0L;
        }
    }
    
    public String get() {
        return this.tracerId;
    }
    
    static {
        LOG = LogFactory.getLog(TracerId.class);
    }
}
