// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.ha;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import com.google.common.annotations.VisibleForTesting;
import org.slf4j.LoggerFactory;
import java.util.Collection;
import java.io.InputStream;
import com.jcraft.jsch.ChannelExec;
import java.io.IOException;
import java.util.Iterator;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;
import java.net.InetSocketAddress;
import com.jcraft.jsch.JSchException;
import org.slf4j.Logger;
import org.apache.hadoop.conf.Configured;

public class SshFenceByTcpPort extends Configured implements FenceMethod
{
    static final Logger LOG;
    static final String CONF_CONNECT_TIMEOUT_KEY = "dfs.ha.fencing.ssh.connect-timeout";
    private static final int CONF_CONNECT_TIMEOUT_DEFAULT = 30000;
    static final String CONF_IDENTITIES_KEY = "dfs.ha.fencing.ssh.private-key-files";
    
    @Override
    public void checkArgs(final String argStr) throws BadFencingConfigurationException {
        if (argStr != null) {
            new Args(argStr);
        }
    }
    
    @Override
    public boolean tryFence(final HAServiceTarget target, final String argsStr) throws BadFencingConfigurationException {
        final Args args = new Args(argsStr);
        final InetSocketAddress serviceAddr = target.getAddress();
        final String host = serviceAddr.getHostName();
        Session session;
        try {
            session = this.createSession(serviceAddr.getHostName(), args);
        }
        catch (JSchException e) {
            SshFenceByTcpPort.LOG.warn("Unable to create SSH session", e);
            return false;
        }
        SshFenceByTcpPort.LOG.info("Connecting to " + host + "...");
        try {
            session.connect(this.getSshConnectTimeout());
        }
        catch (JSchException e) {
            SshFenceByTcpPort.LOG.warn("Unable to connect to " + host + " as user " + args.user, e);
            return false;
        }
        SshFenceByTcpPort.LOG.info("Connected to " + host);
        try {
            return this.doFence(session, serviceAddr);
        }
        catch (JSchException e) {
            SshFenceByTcpPort.LOG.warn("Unable to achieve fencing on remote host", e);
            return false;
        }
        finally {
            session.disconnect();
        }
    }
    
    private Session createSession(final String host, final Args args) throws JSchException {
        final JSch jsch = new JSch();
        for (final String keyFile : this.getKeyFiles()) {
            jsch.addIdentity(keyFile);
        }
        JSch.setLogger(new LogAdapter());
        final Session session = jsch.getSession(args.user, host, args.sshPort);
        session.setConfig("StrictHostKeyChecking", "no");
        return session;
    }
    
    private boolean doFence(final Session session, final InetSocketAddress serviceAddr) throws JSchException {
        final int port = serviceAddr.getPort();
        try {
            SshFenceByTcpPort.LOG.info("Looking for process running on port " + port);
            int rc = this.execCommand(session, "PATH=$PATH:/sbin:/usr/sbin fuser -v -k -n tcp " + port);
            if (rc == 0) {
                SshFenceByTcpPort.LOG.info("Successfully killed process that was listening on port " + port);
                return true;
            }
            if (rc != 1) {
                SshFenceByTcpPort.LOG.info("rc: " + rc);
                return rc == 0;
            }
            SshFenceByTcpPort.LOG.info("Indeterminate response from trying to kill service. Verifying whether it is running using nc...");
            rc = this.execCommand(session, "nc -z " + serviceAddr.getHostName() + " " + serviceAddr.getPort());
            if (rc == 0) {
                SshFenceByTcpPort.LOG.warn("Unable to fence - it is running but we cannot kill it");
                return false;
            }
            SshFenceByTcpPort.LOG.info("Verified that the service is down.");
            return true;
        }
        catch (InterruptedException e) {
            SshFenceByTcpPort.LOG.warn("Interrupted while trying to fence via ssh", e);
            return false;
        }
        catch (IOException e2) {
            SshFenceByTcpPort.LOG.warn("Unknown failure while trying to fence via ssh", e2);
            return false;
        }
    }
    
    private int execCommand(final Session session, final String cmd) throws JSchException, InterruptedException, IOException {
        SshFenceByTcpPort.LOG.debug("Running cmd: " + cmd);
        ChannelExec exec = null;
        try {
            exec = (ChannelExec)session.openChannel("exec");
            exec.setCommand(cmd);
            exec.setInputStream(null);
            exec.connect();
            final StreamPumper outPumper = new StreamPumper(SshFenceByTcpPort.LOG, cmd + " via ssh", exec.getInputStream(), StreamPumper.StreamType.STDOUT);
            outPumper.start();
            final StreamPumper errPumper = new StreamPumper(SshFenceByTcpPort.LOG, cmd + " via ssh", exec.getErrStream(), StreamPumper.StreamType.STDERR);
            errPumper.start();
            outPumper.join();
            errPumper.join();
            return exec.getExitStatus();
        }
        finally {
            cleanup(exec);
        }
    }
    
    private static void cleanup(final ChannelExec exec) {
        if (exec != null) {
            try {
                exec.disconnect();
            }
            catch (Throwable t) {
                SshFenceByTcpPort.LOG.warn("Couldn't disconnect ssh channel", t);
            }
        }
    }
    
    private int getSshConnectTimeout() {
        return this.getConf().getInt("dfs.ha.fencing.ssh.connect-timeout", 30000);
    }
    
    private Collection<String> getKeyFiles() {
        return this.getConf().getTrimmedStringCollection("dfs.ha.fencing.ssh.private-key-files");
    }
    
    static {
        LOG = LoggerFactory.getLogger(SshFenceByTcpPort.class);
    }
    
    @VisibleForTesting
    static class Args
    {
        private static final Pattern USER_PORT_RE;
        private static final int DEFAULT_SSH_PORT = 22;
        String user;
        int sshPort;
        
        public Args(final String arg) throws BadFencingConfigurationException {
            this.user = System.getProperty("user.name");
            this.sshPort = 22;
            if (arg != null && !arg.isEmpty()) {
                final Matcher m = Args.USER_PORT_RE.matcher(arg);
                if (!m.matches()) {
                    throw new BadFencingConfigurationException("Unable to parse user and SSH port: " + arg);
                }
                if (m.group(1) != null) {
                    this.user = m.group(1);
                }
                if (m.group(2) != null) {
                    this.sshPort = this.parseConfiggedPort(m.group(2));
                }
            }
        }
        
        private int parseConfiggedPort(final String portStr) throws BadFencingConfigurationException {
            try {
                return Integer.parseInt(portStr);
            }
            catch (NumberFormatException nfe) {
                throw new BadFencingConfigurationException("Port number '" + portStr + "' invalid");
            }
        }
        
        static {
            USER_PORT_RE = Pattern.compile("([^:]+?)?(?:\\:(\\d+))?");
        }
    }
    
    private static class LogAdapter implements Logger
    {
        static final org.slf4j.Logger LOG;
        
        @Override
        public boolean isEnabled(final int level) {
            switch (level) {
                case 0: {
                    return LogAdapter.LOG.isDebugEnabled();
                }
                case 1: {
                    return LogAdapter.LOG.isInfoEnabled();
                }
                case 2: {
                    return LogAdapter.LOG.isWarnEnabled();
                }
                case 3:
                case 4: {
                    return LogAdapter.LOG.isErrorEnabled();
                }
                default: {
                    return false;
                }
            }
        }
        
        @Override
        public void log(final int level, final String message) {
            switch (level) {
                case 0: {
                    LogAdapter.LOG.debug(message);
                    break;
                }
                case 1: {
                    LogAdapter.LOG.info(message);
                    break;
                }
                case 2: {
                    LogAdapter.LOG.warn(message);
                    break;
                }
                case 3:
                case 4: {
                    LogAdapter.LOG.error(message);
                    break;
                }
            }
        }
        
        static {
            LOG = LoggerFactory.getLogger(SshFenceByTcpPort.class.getName() + ".jsch");
        }
    }
}
