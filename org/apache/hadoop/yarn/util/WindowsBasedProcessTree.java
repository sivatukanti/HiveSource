// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.yarn.util;

import org.apache.commons.logging.LogFactory;
import java.util.Iterator;
import java.util.HashMap;
import java.io.IOException;
import org.apache.hadoop.util.StringUtils;
import org.apache.hadoop.util.Shell;
import java.util.Map;
import org.apache.commons.logging.Log;
import org.apache.hadoop.classification.InterfaceAudience;

@InterfaceAudience.Private
public class WindowsBasedProcessTree extends ResourceCalculatorProcessTree
{
    static final Log LOG;
    private String taskProcessId;
    private long cpuTimeMs;
    private Map<String, ProcessInfo> processTree;
    
    public static boolean isAvailable() {
        if (Shell.WINDOWS) {
            final Shell.ShellCommandExecutor shellExecutor = new Shell.ShellCommandExecutor(new String[] { Shell.WINUTILS, "help" });
            try {
                shellExecutor.execute();
            }
            catch (IOException e) {
                WindowsBasedProcessTree.LOG.error(StringUtils.stringifyException(e));
            }
            finally {
                final String output = shellExecutor.getOutput();
                if (output != null && output.contains("Prints to stdout a list of processes in the task")) {
                    return true;
                }
            }
        }
        return false;
    }
    
    public WindowsBasedProcessTree(final String pid) {
        super(pid);
        this.taskProcessId = null;
        this.cpuTimeMs = 0L;
        this.processTree = new HashMap<String, ProcessInfo>();
        this.taskProcessId = pid;
    }
    
    String getAllProcessInfoFromShell() {
        final Shell.ShellCommandExecutor shellExecutor = new Shell.ShellCommandExecutor(new String[] { Shell.WINUTILS, "task", "processList", this.taskProcessId });
        try {
            shellExecutor.execute();
            return shellExecutor.getOutput();
        }
        catch (IOException e) {
            WindowsBasedProcessTree.LOG.error(StringUtils.stringifyException(e));
            return null;
        }
    }
    
    Map<String, ProcessInfo> createProcessInfo(final String processesInfoStr) {
        final String[] processesStr = processesInfoStr.split("\r\n");
        final Map<String, ProcessInfo> allProcs = new HashMap<String, ProcessInfo>();
        final int procInfoSplitCount = 4;
        for (final String processStr : processesStr) {
            if (processStr != null) {
                final String[] procInfo = processStr.split(",");
                if (procInfo.length == 4) {
                    try {
                        final ProcessInfo pInfo = new ProcessInfo();
                        pInfo.pid = procInfo[0];
                        pInfo.vmem = Long.parseLong(procInfo[1]);
                        pInfo.workingSet = Long.parseLong(procInfo[2]);
                        pInfo.cpuTimeMs = Long.parseLong(procInfo[3]);
                        allProcs.put(pInfo.pid, pInfo);
                    }
                    catch (NumberFormatException nfe) {
                        WindowsBasedProcessTree.LOG.debug("Error parsing procInfo." + nfe);
                    }
                }
                else {
                    WindowsBasedProcessTree.LOG.debug("Expected split length of proc info to be 4. Got " + procInfo.length);
                }
            }
        }
        return allProcs;
    }
    
    @Override
    public void updateProcessTree() {
        if (this.taskProcessId != null) {
            final String processesInfoStr = this.getAllProcessInfoFromShell();
            if (processesInfoStr != null && processesInfoStr.length() > 0) {
                final Map<String, ProcessInfo> allProcessInfo = this.createProcessInfo(processesInfoStr);
                for (final Map.Entry<String, ProcessInfo> entry : allProcessInfo.entrySet()) {
                    final String pid = entry.getKey();
                    final ProcessInfo pInfo = entry.getValue();
                    final ProcessInfo oldInfo = this.processTree.get(pid);
                    if (oldInfo != null) {
                        final ProcessInfo processInfo = pInfo;
                        processInfo.age += oldInfo.age;
                        pInfo.cpuTimeMsDelta = pInfo.cpuTimeMs - oldInfo.cpuTimeMs;
                    }
                    else {
                        pInfo.cpuTimeMsDelta = pInfo.cpuTimeMs;
                    }
                }
                this.processTree.clear();
                this.processTree = allProcessInfo;
            }
            else {
                this.processTree.clear();
            }
        }
    }
    
    @Override
    public boolean checkPidPgrpidForMatch() {
        return true;
    }
    
    @Override
    public String getProcessTreeDump() {
        final StringBuilder ret = new StringBuilder();
        ret.append(String.format("\t|- PID CPU_TIME(MILLIS) VMEM(BYTES) WORKING_SET(BYTES)\n", new Object[0]));
        for (final ProcessInfo p : this.processTree.values()) {
            if (p != null) {
                ret.append(String.format("\t|- %s %d %d %d\n", p.pid, p.cpuTimeMs, p.vmem, p.workingSet));
            }
        }
        return ret.toString();
    }
    
    @Override
    public long getCumulativeVmem(final int olderThanAge) {
        long total = 0L;
        for (final ProcessInfo p : this.processTree.values()) {
            if (p != null && p.age > olderThanAge) {
                total += p.vmem;
            }
        }
        return total;
    }
    
    @Override
    public long getCumulativeRssmem(final int olderThanAge) {
        long total = 0L;
        for (final ProcessInfo p : this.processTree.values()) {
            if (p != null && p.age > olderThanAge) {
                total += p.workingSet;
            }
        }
        return total;
    }
    
    @Override
    public long getCumulativeCpuTime() {
        for (final ProcessInfo p : this.processTree.values()) {
            this.cpuTimeMs += p.cpuTimeMsDelta;
        }
        return this.cpuTimeMs;
    }
    
    static {
        LOG = LogFactory.getLog(WindowsBasedProcessTree.class);
    }
    
    static class ProcessInfo
    {
        String pid;
        long vmem;
        long workingSet;
        long cpuTimeMs;
        long cpuTimeMsDelta;
        int age;
        
        ProcessInfo() {
            this.age = 1;
        }
    }
}
