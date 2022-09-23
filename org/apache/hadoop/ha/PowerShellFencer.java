// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.ha;

import org.slf4j.LoggerFactory;
import org.apache.hadoop.util.StringUtils;
import java.io.Writer;
import java.io.BufferedWriter;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.io.FileOutputStream;
import java.io.File;
import java.net.InetSocketAddress;
import java.io.IOException;
import org.slf4j.Logger;
import org.apache.hadoop.conf.Configured;

public class PowerShellFencer extends Configured implements FenceMethod
{
    private static final Logger LOG;
    
    @Override
    public void checkArgs(final String argStr) throws BadFencingConfigurationException {
        PowerShellFencer.LOG.info("The parameter for the PowerShell fencer is " + argStr);
    }
    
    @Override
    public boolean tryFence(final HAServiceTarget target, final String argsStr) throws BadFencingConfigurationException {
        final String processName = argsStr;
        final InetSocketAddress serviceAddr = target.getAddress();
        final String hostname = serviceAddr.getHostName();
        final String ps1script = this.buildPSScript(processName, hostname);
        if (ps1script == null) {
            PowerShellFencer.LOG.error("Cannot build PowerShell script");
            return false;
        }
        PowerShellFencer.LOG.info("Executing " + ps1script);
        final ProcessBuilder builder = new ProcessBuilder(new String[] { "powershell.exe", ps1script });
        Process p = null;
        try {
            p = builder.start();
            p.getOutputStream().close();
        }
        catch (IOException e) {
            PowerShellFencer.LOG.warn("Unable to execute " + ps1script, e);
            return false;
        }
        final StreamPumper errPumper = new StreamPumper(PowerShellFencer.LOG, "fencer", p.getErrorStream(), StreamPumper.StreamType.STDERR);
        errPumper.start();
        final StreamPumper outPumper = new StreamPumper(PowerShellFencer.LOG, "fencer", p.getInputStream(), StreamPumper.StreamType.STDOUT);
        outPumper.start();
        int rc = 0;
        try {
            rc = p.waitFor();
            errPumper.join();
            outPumper.join();
        }
        catch (InterruptedException ie) {
            PowerShellFencer.LOG.warn("Interrupted while waiting for fencing command: " + ps1script);
            return false;
        }
        return rc == 0;
    }
    
    private String buildPSScript(final String processName, final String host) {
        PowerShellFencer.LOG.info("Building PowerShell script to kill " + processName + " at " + host);
        String ps1script = null;
        BufferedWriter writer = null;
        try {
            final File file = File.createTempFile("temp-fence-command", ".ps1");
            file.deleteOnExit();
            final FileOutputStream fos = new FileOutputStream(file, false);
            final OutputStreamWriter osw = new OutputStreamWriter(fos, StandardCharsets.UTF_8);
            writer = new BufferedWriter(osw);
            final String filter = StringUtils.join(" and ", new String[] { "Name LIKE '%java.exe%'", "CommandLine LIKE '%" + processName + "%'" });
            String cmd = "Get-WmiObject Win32_Process";
            cmd = cmd + " -Filter \"" + filter + "\"";
            cmd = cmd + " -Computer " + host;
            cmd += " |% { $_.Terminate() }";
            PowerShellFencer.LOG.info("PowerShell command: " + cmd);
            writer.write(cmd);
            writer.flush();
            ps1script = file.getAbsolutePath();
        }
        catch (IOException ioe) {
            PowerShellFencer.LOG.error("Cannot create PowerShell script", ioe);
            if (writer != null) {
                try {
                    writer.close();
                }
                catch (IOException ioe) {
                    PowerShellFencer.LOG.error("Cannot close PowerShell script", ioe);
                }
            }
        }
        finally {
            if (writer != null) {
                try {
                    writer.close();
                }
                catch (IOException ioe2) {
                    PowerShellFencer.LOG.error("Cannot close PowerShell script", ioe2);
                }
            }
        }
        return ps1script;
    }
    
    static {
        LOG = LoggerFactory.getLogger(PowerShellFencer.class);
    }
}
