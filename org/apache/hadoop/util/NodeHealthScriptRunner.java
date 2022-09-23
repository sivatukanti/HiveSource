// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.util;

import java.util.Map;
import java.util.Collection;
import java.util.Arrays;
import java.util.ArrayList;
import org.slf4j.LoggerFactory;
import org.apache.hadoop.fs.FileUtil;
import java.io.File;
import org.apache.hadoop.conf.Configuration;
import java.util.TimerTask;
import java.util.Timer;
import org.slf4j.Logger;
import org.apache.hadoop.service.AbstractService;

public class NodeHealthScriptRunner extends AbstractService
{
    private static final Logger LOG;
    private String nodeHealthScript;
    private long intervalTime;
    private long scriptTimeout;
    private Timer nodeHealthScriptScheduler;
    Shell.ShellCommandExecutor shexec;
    private static final String ERROR_PATTERN = "ERROR";
    public static final String NODE_HEALTH_SCRIPT_TIMED_OUT_MSG = "Node health script timed out";
    private boolean isHealthy;
    private String healthReport;
    private long lastReportedTime;
    private TimerTask timer;
    
    public NodeHealthScriptRunner(final String scriptName, final long chkInterval, final long timeout, final String[] scriptArgs) {
        super(NodeHealthScriptRunner.class.getName());
        this.shexec = null;
        this.lastReportedTime = System.currentTimeMillis();
        this.isHealthy = true;
        this.healthReport = "";
        this.nodeHealthScript = scriptName;
        this.intervalTime = chkInterval;
        this.scriptTimeout = timeout;
        this.timer = new NodeHealthMonitorExecutor(scriptArgs);
    }
    
    @Override
    protected void serviceInit(final Configuration conf) throws Exception {
        super.serviceInit(conf);
    }
    
    @Override
    protected void serviceStart() throws Exception {
        (this.nodeHealthScriptScheduler = new Timer("NodeHealthMonitor-Timer", true)).scheduleAtFixedRate(this.timer, 0L, this.intervalTime);
        super.serviceStart();
    }
    
    @Override
    protected void serviceStop() {
        if (this.nodeHealthScriptScheduler != null) {
            this.nodeHealthScriptScheduler.cancel();
        }
        if (this.shexec != null) {
            final Process p = this.shexec.getProcess();
            if (p != null) {
                p.destroy();
            }
        }
    }
    
    public boolean isHealthy() {
        return this.isHealthy;
    }
    
    private synchronized void setHealthy(final boolean isHealthy) {
        this.isHealthy = isHealthy;
    }
    
    public String getHealthReport() {
        return this.healthReport;
    }
    
    private synchronized void setHealthReport(final String healthReport) {
        this.healthReport = healthReport;
    }
    
    public long getLastReportedTime() {
        return this.lastReportedTime;
    }
    
    private synchronized void setLastReportedTime(final long lastReportedTime) {
        this.lastReportedTime = lastReportedTime;
    }
    
    public static boolean shouldRun(final String healthScript) {
        if (healthScript == null || healthScript.trim().isEmpty()) {
            return false;
        }
        final File f = new File(healthScript);
        return f.exists() && FileUtil.canExecute(f);
    }
    
    private synchronized void setHealthStatus(final boolean isHealthy, final String output) {
        NodeHealthScriptRunner.LOG.info("health status being set as " + output);
        this.setHealthy(isHealthy);
        this.setHealthReport(output);
    }
    
    private synchronized void setHealthStatus(final boolean isHealthy, final String output, final long time) {
        NodeHealthScriptRunner.LOG.info("health status being set as " + output);
        this.setHealthStatus(isHealthy, output);
        this.setLastReportedTime(time);
    }
    
    public TimerTask getTimerTask() {
        return this.timer;
    }
    
    static {
        LOG = LoggerFactory.getLogger(NodeHealthScriptRunner.class);
    }
    
    private enum HealthCheckerExitStatus
    {
        SUCCESS, 
        TIMED_OUT, 
        FAILED_WITH_EXIT_CODE, 
        FAILED_WITH_EXCEPTION, 
        FAILED;
    }
    
    private class NodeHealthMonitorExecutor extends TimerTask
    {
        String exceptionStackTrace;
        
        public NodeHealthMonitorExecutor(final String[] args) {
            this.exceptionStackTrace = "";
            final ArrayList<String> execScript = new ArrayList<String>();
            execScript.add(NodeHealthScriptRunner.this.nodeHealthScript);
            if (args != null) {
                execScript.addAll(Arrays.asList(args));
            }
            NodeHealthScriptRunner.this.shexec = new Shell.ShellCommandExecutor(execScript.toArray(new String[execScript.size()]), null, null, NodeHealthScriptRunner.this.scriptTimeout);
        }
        
        @Override
        public void run() {
            HealthCheckerExitStatus status = HealthCheckerExitStatus.SUCCESS;
            try {
                NodeHealthScriptRunner.this.shexec.execute();
            }
            catch (Shell.ExitCodeException e2) {
                status = HealthCheckerExitStatus.FAILED_WITH_EXIT_CODE;
                if (Shell.WINDOWS && NodeHealthScriptRunner.this.shexec.isTimedOut()) {
                    status = HealthCheckerExitStatus.TIMED_OUT;
                }
            }
            catch (Exception e) {
                NodeHealthScriptRunner.LOG.warn("Caught exception : " + e.getMessage());
                if (!NodeHealthScriptRunner.this.shexec.isTimedOut()) {
                    status = HealthCheckerExitStatus.FAILED_WITH_EXCEPTION;
                }
                else {
                    status = HealthCheckerExitStatus.TIMED_OUT;
                }
                this.exceptionStackTrace = StringUtils.stringifyException(e);
            }
            finally {
                if (status == HealthCheckerExitStatus.SUCCESS && this.hasErrors(NodeHealthScriptRunner.this.shexec.getOutput())) {
                    status = HealthCheckerExitStatus.FAILED;
                }
                this.reportHealthStatus(status);
            }
        }
        
        void reportHealthStatus(final HealthCheckerExitStatus status) {
            final long now = System.currentTimeMillis();
            switch (status) {
                case SUCCESS: {
                    NodeHealthScriptRunner.this.setHealthStatus(true, "", now);
                    break;
                }
                case TIMED_OUT: {
                    NodeHealthScriptRunner.this.setHealthStatus(false, "Node health script timed out");
                    break;
                }
                case FAILED_WITH_EXCEPTION: {
                    NodeHealthScriptRunner.this.setHealthStatus(false, this.exceptionStackTrace);
                    break;
                }
                case FAILED_WITH_EXIT_CODE: {
                    NodeHealthScriptRunner.this.setHealthStatus(true, "", now);
                    break;
                }
                case FAILED: {
                    NodeHealthScriptRunner.this.setHealthStatus(false, NodeHealthScriptRunner.this.shexec.getOutput());
                    break;
                }
            }
        }
        
        private boolean hasErrors(final String output) {
            final String[] split2;
            final String[] splits = split2 = output.split("\n");
            for (final String split : split2) {
                if (split.startsWith("ERROR")) {
                    return true;
                }
            }
            return false;
        }
    }
}
