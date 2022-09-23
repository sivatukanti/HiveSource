// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.ha;

import org.slf4j.LoggerFactory;
import java.util.Iterator;
import java.util.Map;
import java.lang.reflect.Field;
import java.io.IOException;
import org.apache.hadoop.util.Shell;
import com.google.common.annotations.VisibleForTesting;
import org.slf4j.Logger;
import org.apache.hadoop.conf.Configured;

public class ShellCommandFencer extends Configured implements FenceMethod
{
    private static final int ABBREV_LENGTH = 20;
    private static final String TARGET_PREFIX = "target_";
    @VisibleForTesting
    static Logger LOG;
    
    @Override
    public void checkArgs(final String args) throws BadFencingConfigurationException {
        if (args == null || args.isEmpty()) {
            throw new BadFencingConfigurationException("No argument passed to 'shell' fencing method");
        }
    }
    
    @Override
    public boolean tryFence(final HAServiceTarget target, final String cmd) {
        ProcessBuilder builder;
        if (!Shell.WINDOWS) {
            builder = new ProcessBuilder(new String[] { "bash", "-e", "-c", cmd });
        }
        else {
            builder = new ProcessBuilder(new String[] { "cmd.exe", "/c", cmd });
        }
        this.setConfAsEnvVars(builder.environment());
        this.addTargetInfoAsEnvVars(target, builder.environment());
        Process p;
        try {
            p = builder.start();
            p.getOutputStream().close();
        }
        catch (IOException e) {
            ShellCommandFencer.LOG.warn("Unable to execute " + cmd, e);
            return false;
        }
        final String pid = tryGetPid(p);
        ShellCommandFencer.LOG.info("Launched fencing command '" + cmd + "' with " + ((pid != null) ? ("pid " + pid) : "unknown pid"));
        String logPrefix = abbreviate(cmd, 20);
        if (pid != null) {
            logPrefix = "[PID " + pid + "] " + logPrefix;
        }
        final StreamPumper errPumper = new StreamPumper(ShellCommandFencer.LOG, logPrefix, p.getErrorStream(), StreamPumper.StreamType.STDERR);
        errPumper.start();
        final StreamPumper outPumper = new StreamPumper(ShellCommandFencer.LOG, logPrefix, p.getInputStream(), StreamPumper.StreamType.STDOUT);
        outPumper.start();
        int rc;
        try {
            rc = p.waitFor();
            errPumper.join();
            outPumper.join();
        }
        catch (InterruptedException ie) {
            ShellCommandFencer.LOG.warn("Interrupted while waiting for fencing command: " + cmd);
            return false;
        }
        return rc == 0;
    }
    
    static String abbreviate(final String cmd, final int len) {
        if (cmd.length() > len && len >= 5) {
            final int firstHalf = (len - 3) / 2;
            final int rem = len - firstHalf - 3;
            return cmd.substring(0, firstHalf) + "..." + cmd.substring(cmd.length() - rem);
        }
        return cmd;
    }
    
    private static String tryGetPid(final Process p) {
        try {
            final Class<? extends Process> clazz = p.getClass();
            if (clazz.getName().equals("java.lang.UNIXProcess")) {
                final Field f = clazz.getDeclaredField("pid");
                f.setAccessible(true);
                return String.valueOf(f.getInt(p));
            }
            ShellCommandFencer.LOG.trace("Unable to determine pid for " + p + " since it is not a UNIXProcess");
            return null;
        }
        catch (Throwable t) {
            ShellCommandFencer.LOG.trace("Unable to determine pid for " + p, t);
            return null;
        }
    }
    
    private void setConfAsEnvVars(final Map<String, String> env) {
        for (final Map.Entry<String, String> pair : this.getConf()) {
            env.put(pair.getKey().replace('.', '_'), pair.getValue());
        }
    }
    
    private void addTargetInfoAsEnvVars(final HAServiceTarget target, final Map<String, String> environment) {
        for (final Map.Entry<String, String> e : target.getFencingParameters().entrySet()) {
            String key = "target_" + e.getKey();
            key = key.replace('.', '_');
            environment.put(key, e.getValue());
        }
    }
    
    static {
        ShellCommandFencer.LOG = LoggerFactory.getLogger(ShellCommandFencer.class);
    }
}
