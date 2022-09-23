// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.yarn.util;

import org.apache.hadoop.util.StringUtils;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.io.IOUtils;
import java.io.IOException;
import java.math.BigInteger;
import java.io.FileNotFoundException;
import java.io.Reader;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.File;
import java.util.regex.Matcher;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Collection;
import java.util.LinkedList;
import org.apache.hadoop.util.Shell;
import java.util.HashMap;
import org.apache.hadoop.conf.Configuration;
import java.util.Map;
import java.util.regex.Pattern;
import org.apache.commons.logging.Log;
import org.apache.hadoop.classification.InterfaceStability;
import org.apache.hadoop.classification.InterfaceAudience;

@InterfaceAudience.Private
@InterfaceStability.Unstable
public class ProcfsBasedProcessTree extends ResourceCalculatorProcessTree
{
    static final Log LOG;
    private static final String PROCFS = "/proc/";
    private static final Pattern PROCFS_STAT_FILE_FORMAT;
    public static final String PROCFS_STAT_FILE = "stat";
    public static final String PROCFS_CMDLINE_FILE = "cmdline";
    public static final long PAGE_SIZE;
    public static final long JIFFY_LENGTH_IN_MILLIS;
    public static final String SMAPS = "smaps";
    public static final int KB_TO_BYTES = 1024;
    private static final String KB = "kB";
    private static final String READ_ONLY_WITH_SHARED_PERMISSION = "r--s";
    private static final String READ_EXECUTE_WITH_SHARED_PERMISSION = "r-xs";
    private static final Pattern ADDRESS_PATTERN;
    private static final Pattern MEM_INFO_PATTERN;
    private boolean smapsEnabled;
    protected Map<String, ProcessTreeSmapMemInfo> processSMAPTree;
    private String procfsDir;
    private static String deadPid;
    private String pid;
    private static Pattern numberPattern;
    private Long cpuTime;
    protected Map<String, ProcessInfo> processTree;
    private static final String PROCESSTREE_DUMP_FORMAT = "\t|- %s %s %d %d %s %d %d %d %d %s\n";
    
    public ProcfsBasedProcessTree(final String pid) {
        this(pid, "/proc/");
    }
    
    @Override
    public void setConf(final Configuration conf) {
        super.setConf(conf);
        if (conf != null) {
            this.smapsEnabled = conf.getBoolean("yarn.nodemanager..container-monitor.procfs-tree.smaps-based-rss.enabled", false);
        }
    }
    
    public ProcfsBasedProcessTree(final String pid, final String procfsDir) {
        super(pid);
        this.processSMAPTree = new HashMap<String, ProcessTreeSmapMemInfo>();
        this.pid = ProcfsBasedProcessTree.deadPid;
        this.cpuTime = 0L;
        this.processTree = new HashMap<String, ProcessInfo>();
        this.pid = getValidPID(pid);
        this.procfsDir = procfsDir;
    }
    
    public static boolean isAvailable() {
        try {
            if (!Shell.LINUX) {
                ProcfsBasedProcessTree.LOG.info("ProcfsBasedProcessTree currently is supported only on Linux.");
                return false;
            }
        }
        catch (SecurityException se) {
            ProcfsBasedProcessTree.LOG.warn("Failed to get Operating System name. " + se);
            return false;
        }
        return true;
    }
    
    @Override
    public void updateProcessTree() {
        if (!this.pid.equals(ProcfsBasedProcessTree.deadPid)) {
            final List<String> processList = this.getProcessList();
            final Map<String, ProcessInfo> allProcessInfo = new HashMap<String, ProcessInfo>();
            final Map<String, ProcessInfo> oldProcs = new HashMap<String, ProcessInfo>(this.processTree);
            this.processTree.clear();
            ProcessInfo me = null;
            for (final String proc : processList) {
                final ProcessInfo pInfo = new ProcessInfo(proc);
                if (constructProcessInfo(pInfo, this.procfsDir) != null) {
                    allProcessInfo.put(proc, pInfo);
                    if (!proc.equals(this.pid)) {
                        continue;
                    }
                    me = pInfo;
                    this.processTree.put(proc, pInfo);
                }
            }
            if (me == null) {
                return;
            }
            for (final Map.Entry<String, ProcessInfo> entry : allProcessInfo.entrySet()) {
                final String pID = entry.getKey();
                if (!pID.equals("1")) {
                    final ProcessInfo pInfo2 = entry.getValue();
                    final ProcessInfo parentPInfo = allProcessInfo.get(pInfo2.getPpid());
                    if (parentPInfo == null) {
                        continue;
                    }
                    parentPInfo.addChild(pInfo2);
                }
            }
            final LinkedList<ProcessInfo> pInfoQueue = new LinkedList<ProcessInfo>();
            pInfoQueue.addAll(me.getChildren());
            while (!pInfoQueue.isEmpty()) {
                final ProcessInfo pInfo3 = pInfoQueue.remove();
                if (!this.processTree.containsKey(pInfo3.getPid())) {
                    this.processTree.put(pInfo3.getPid(), pInfo3);
                }
                pInfoQueue.addAll(pInfo3.getChildren());
            }
            for (final Map.Entry<String, ProcessInfo> procs : this.processTree.entrySet()) {
                final ProcessInfo oldInfo = oldProcs.get(procs.getKey());
                if (procs.getValue() != null) {
                    procs.getValue().updateJiffy(oldInfo);
                    if (oldInfo == null) {
                        continue;
                    }
                    procs.getValue().updateAge(oldInfo);
                }
            }
            if (ProcfsBasedProcessTree.LOG.isDebugEnabled()) {
                ProcfsBasedProcessTree.LOG.debug(this.toString());
            }
            if (this.smapsEnabled) {
                this.processSMAPTree.clear();
                for (final ProcessInfo p : this.processTree.values()) {
                    if (p != null) {
                        final ProcessTreeSmapMemInfo memInfo = new ProcessTreeSmapMemInfo(p.getPid());
                        constructProcessSMAPInfo(memInfo, this.procfsDir);
                        this.processSMAPTree.put(p.getPid(), memInfo);
                    }
                }
            }
        }
    }
    
    @Override
    public boolean checkPidPgrpidForMatch() {
        return checkPidPgrpidForMatch(this.pid, "/proc/");
    }
    
    public static boolean checkPidPgrpidForMatch(final String _pid, final String procfs) {
        ProcessInfo pInfo = new ProcessInfo(_pid);
        pInfo = constructProcessInfo(pInfo, procfs);
        if (pInfo == null) {
            return true;
        }
        final String pgrpId = pInfo.getPgrpId().toString();
        return pgrpId.equals(_pid);
    }
    
    public List<String> getCurrentProcessIDs() {
        final List<String> currentPIDs = new ArrayList<String>();
        currentPIDs.addAll(this.processTree.keySet());
        return currentPIDs;
    }
    
    @Override
    public String getProcessTreeDump() {
        final StringBuilder ret = new StringBuilder();
        ret.append(String.format("\t|- PID PPID PGRPID SESSID CMD_NAME USER_MODE_TIME(MILLIS) SYSTEM_TIME(MILLIS) VMEM_USAGE(BYTES) RSSMEM_USAGE(PAGES) FULL_CMD_LINE\n", new Object[0]));
        for (final ProcessInfo p : this.processTree.values()) {
            if (p != null) {
                ret.append(String.format("\t|- %s %s %d %d %s %d %d %d %d %s\n", p.getPid(), p.getPpid(), p.getPgrpId(), p.getSessionId(), p.getName(), p.getUtime(), p.getStime(), p.getVmem(), p.getRssmemPage(), p.getCmdLine(this.procfsDir)));
            }
        }
        return ret.toString();
    }
    
    @Override
    public long getCumulativeVmem(final int olderThanAge) {
        long total = 0L;
        for (final ProcessInfo p : this.processTree.values()) {
            if (p != null && p.getAge() > olderThanAge) {
                total += p.getVmem();
            }
        }
        return total;
    }
    
    @Override
    public long getCumulativeRssmem(final int olderThanAge) {
        if (ProcfsBasedProcessTree.PAGE_SIZE < 0L) {
            return 0L;
        }
        if (this.smapsEnabled) {
            return this.getSmapBasedCumulativeRssmem(olderThanAge);
        }
        long totalPages = 0L;
        for (final ProcessInfo p : this.processTree.values()) {
            if (p != null && p.getAge() > olderThanAge) {
                totalPages += p.getRssmemPage();
            }
        }
        return totalPages * ProcfsBasedProcessTree.PAGE_SIZE;
    }
    
    private long getSmapBasedCumulativeRssmem(final int olderThanAge) {
        long total = 0L;
        for (final ProcessInfo p : this.processTree.values()) {
            if (p != null && p.getAge() > olderThanAge) {
                final ProcessTreeSmapMemInfo procMemInfo = this.processSMAPTree.get(p.getPid());
                if (procMemInfo != null) {
                    for (final ProcessSmapMemoryInfo info : procMemInfo.getMemoryInfoList()) {
                        if (!info.getPermission().trim().equalsIgnoreCase("r--s")) {
                            if (info.getPermission().trim().equalsIgnoreCase("r-xs")) {
                                continue;
                            }
                            total += Math.min(info.sharedDirty, info.pss) + info.privateDirty + info.privateClean;
                            if (!ProcfsBasedProcessTree.LOG.isDebugEnabled()) {
                                continue;
                            }
                            ProcfsBasedProcessTree.LOG.debug(" total(" + olderThanAge + "): PID : " + p.getPid() + ", SharedDirty : " + info.sharedDirty + ", PSS : " + info.pss + ", Private_Dirty : " + info.privateDirty + ", Private_Clean : " + info.privateClean + ", total : " + total * 1024L);
                        }
                    }
                }
                if (!ProcfsBasedProcessTree.LOG.isDebugEnabled()) {
                    continue;
                }
                ProcfsBasedProcessTree.LOG.debug(procMemInfo.toString());
            }
        }
        total *= 1024L;
        ProcfsBasedProcessTree.LOG.info("SmapBasedCumulativeRssmem (bytes) : " + total);
        return total;
    }
    
    @Override
    public long getCumulativeCpuTime() {
        if (ProcfsBasedProcessTree.JIFFY_LENGTH_IN_MILLIS < 0L) {
            return 0L;
        }
        long incJiffies = 0L;
        for (final ProcessInfo p : this.processTree.values()) {
            if (p != null) {
                incJiffies += p.getDtime();
            }
        }
        this.cpuTime += incJiffies * ProcfsBasedProcessTree.JIFFY_LENGTH_IN_MILLIS;
        return this.cpuTime;
    }
    
    private static String getValidPID(final String pid) {
        if (pid == null) {
            return ProcfsBasedProcessTree.deadPid;
        }
        final Matcher m = ProcfsBasedProcessTree.numberPattern.matcher(pid);
        if (m.matches()) {
            return pid;
        }
        return ProcfsBasedProcessTree.deadPid;
    }
    
    private List<String> getProcessList() {
        final String[] processDirs = new File(this.procfsDir).list();
        final List<String> processList = new ArrayList<String>();
        for (final String dir : processDirs) {
            final Matcher m = ProcfsBasedProcessTree.numberPattern.matcher(dir);
            if (m.matches()) {
                try {
                    if (new File(this.procfsDir, dir).isDirectory()) {
                        processList.add(dir);
                    }
                }
                catch (SecurityException ex) {}
            }
        }
        return processList;
    }
    
    private static ProcessInfo constructProcessInfo(final ProcessInfo pinfo, final String procfsDir) {
        ProcessInfo ret = null;
        BufferedReader in = null;
        FileReader fReader = null;
        try {
            final File pidDir = new File(procfsDir, pinfo.getPid());
            fReader = new FileReader(new File(pidDir, "stat"));
            in = new BufferedReader(fReader);
        }
        catch (FileNotFoundException f) {
            return ret;
        }
        ret = pinfo;
        try {
            final String str = in.readLine();
            final Matcher m = ProcfsBasedProcessTree.PROCFS_STAT_FILE_FORMAT.matcher(str);
            final boolean mat = m.find();
            if (mat) {
                pinfo.updateProcessInfo(m.group(2), m.group(3), Integer.parseInt(m.group(4)), Integer.parseInt(m.group(5)), Long.parseLong(m.group(7)), new BigInteger(m.group(8)), Long.parseLong(m.group(10)), Long.parseLong(m.group(11)));
            }
            else {
                ProcfsBasedProcessTree.LOG.warn("Unexpected: procfs stat file is not in the expected format for process with pid " + pinfo.getPid());
                ret = null;
            }
        }
        catch (IOException io) {
            ProcfsBasedProcessTree.LOG.warn("Error reading the stream " + io);
            ret = null;
            try {
                fReader.close();
                try {
                    in.close();
                }
                catch (IOException i) {
                    ProcfsBasedProcessTree.LOG.warn("Error closing the stream " + in);
                }
            }
            catch (IOException i) {
                ProcfsBasedProcessTree.LOG.warn("Error closing the stream " + fReader);
            }
        }
        finally {
            try {
                fReader.close();
                try {
                    in.close();
                }
                catch (IOException j) {
                    ProcfsBasedProcessTree.LOG.warn("Error closing the stream " + in);
                }
            }
            catch (IOException j) {
                ProcfsBasedProcessTree.LOG.warn("Error closing the stream " + fReader);
            }
        }
        return ret;
    }
    
    @Override
    public String toString() {
        final StringBuffer pTree = new StringBuffer("[ ");
        for (final String p : this.processTree.keySet()) {
            pTree.append(p);
            pTree.append(" ");
        }
        return pTree.substring(0, pTree.length()) + "]";
    }
    
    private static void constructProcessSMAPInfo(final ProcessTreeSmapMemInfo pInfo, final String procfsDir) {
        BufferedReader in = null;
        FileReader fReader = null;
        try {
            final File pidDir = new File(procfsDir, pInfo.getPid());
            final File file = new File(pidDir, "smaps");
            if (!file.exists()) {
                return;
            }
            fReader = new FileReader(file);
            in = new BufferedReader(fReader);
            ProcessSmapMemoryInfo memoryMappingInfo = null;
            final List<String> lines = IOUtils.readLines(in);
            for (String line : lines) {
                line = line.trim();
                try {
                    final Matcher address = ProcfsBasedProcessTree.ADDRESS_PATTERN.matcher(line);
                    if (address.find()) {
                        memoryMappingInfo = new ProcessSmapMemoryInfo(line);
                        memoryMappingInfo.setPermission(address.group(4));
                        pInfo.getMemoryInfoList().add(memoryMappingInfo);
                    }
                    else {
                        final Matcher memInfo = ProcfsBasedProcessTree.MEM_INFO_PATTERN.matcher(line);
                        if (!memInfo.find()) {
                            continue;
                        }
                        final String key = memInfo.group(1).trim();
                        final String value = memInfo.group(2).replace("kB", "").trim();
                        if (ProcfsBasedProcessTree.LOG.isDebugEnabled()) {
                            ProcfsBasedProcessTree.LOG.debug("MemInfo : " + key + " : Value  : " + value);
                        }
                        memoryMappingInfo.setMemInfo(key, value);
                    }
                }
                catch (Throwable t) {
                    ProcfsBasedProcessTree.LOG.warn("Error parsing smaps line : " + line + "; " + t.getMessage());
                }
            }
        }
        catch (FileNotFoundException f) {
            ProcfsBasedProcessTree.LOG.error(f.getMessage());
        }
        catch (IOException e) {
            ProcfsBasedProcessTree.LOG.error(e.getMessage());
        }
        catch (Throwable t2) {
            ProcfsBasedProcessTree.LOG.error(t2.getMessage());
        }
        finally {
            IOUtils.closeQuietly(in);
        }
    }
    
    static {
        LOG = LogFactory.getLog(ProcfsBasedProcessTree.class);
        PROCFS_STAT_FILE_FORMAT = Pattern.compile("^([0-9-]+)\\s([^\\s]+)\\s[^\\s]\\s([0-9-]+)\\s([0-9-]+)\\s([0-9-]+)\\s([0-9-]+\\s){7}([0-9]+)\\s([0-9]+)\\s([0-9-]+\\s){7}([0-9]+)\\s([0-9]+)(\\s[0-9-]+){15}");
        ADDRESS_PATTERN = Pattern.compile("([[a-f]|(0-9)]*)-([[a-f]|(0-9)]*)(\\s)*([rxwps\\-]*)");
        MEM_INFO_PATTERN = Pattern.compile("(^[A-Z].*):[\\s ]*(.*)");
        long jiffiesPerSecond = -1L;
        long pageSize = -1L;
        try {
            if (Shell.LINUX) {
                final Shell.ShellCommandExecutor shellExecutorClk = new Shell.ShellCommandExecutor(new String[] { "getconf", "CLK_TCK" });
                shellExecutorClk.execute();
                jiffiesPerSecond = Long.parseLong(shellExecutorClk.getOutput().replace("\n", ""));
                final Shell.ShellCommandExecutor shellExecutorPage = new Shell.ShellCommandExecutor(new String[] { "getconf", "PAGESIZE" });
                shellExecutorPage.execute();
                pageSize = Long.parseLong(shellExecutorPage.getOutput().replace("\n", ""));
            }
        }
        catch (IOException e) {
            ProcfsBasedProcessTree.LOG.error(StringUtils.stringifyException(e));
        }
        finally {
            JIFFY_LENGTH_IN_MILLIS = ((jiffiesPerSecond != -1L) ? Math.round(1000.0 / jiffiesPerSecond) : -1L);
            PAGE_SIZE = pageSize;
        }
        ProcfsBasedProcessTree.deadPid = "-1";
        ProcfsBasedProcessTree.numberPattern = Pattern.compile("[1-9][0-9]*");
    }
    
    enum MemInfo
    {
        SIZE("Size"), 
        RSS("Rss"), 
        PSS("Pss"), 
        SHARED_CLEAN("Shared_Clean"), 
        SHARED_DIRTY("Shared_Dirty"), 
        PRIVATE_CLEAN("Private_Clean"), 
        PRIVATE_DIRTY("Private_Dirty"), 
        REFERENCED("Referenced"), 
        ANONYMOUS("Anonymous"), 
        ANON_HUGE_PAGES("AnonHugePages"), 
        SWAP("swap"), 
        KERNEL_PAGE_SIZE("kernelPageSize"), 
        MMU_PAGE_SIZE("mmuPageSize"), 
        INVALID("invalid");
        
        private String name;
        
        private MemInfo(final String name) {
            this.name = name;
        }
        
        public static MemInfo getMemInfoByName(final String name) {
            for (final MemInfo info : values()) {
                if (info.name.trim().equalsIgnoreCase(name.trim())) {
                    return info;
                }
            }
            return MemInfo.INVALID;
        }
    }
    
    private static class ProcessInfo
    {
        private String pid;
        private String name;
        private Integer pgrpId;
        private String ppid;
        private Integer sessionId;
        private Long vmem;
        private Long rssmemPage;
        private Long utime;
        private final BigInteger MAX_LONG;
        private BigInteger stime;
        private int age;
        private Long dtime;
        private List<ProcessInfo> children;
        
        public ProcessInfo(final String pid) {
            this.utime = 0L;
            this.MAX_LONG = BigInteger.valueOf(Long.MAX_VALUE);
            this.stime = new BigInteger("0");
            this.dtime = 0L;
            this.children = new ArrayList<ProcessInfo>();
            this.pid = pid;
            this.age = 1;
        }
        
        public String getPid() {
            return this.pid;
        }
        
        public String getName() {
            return this.name;
        }
        
        public Integer getPgrpId() {
            return this.pgrpId;
        }
        
        public String getPpid() {
            return this.ppid;
        }
        
        public Integer getSessionId() {
            return this.sessionId;
        }
        
        public Long getVmem() {
            return this.vmem;
        }
        
        public Long getUtime() {
            return this.utime;
        }
        
        public BigInteger getStime() {
            return this.stime;
        }
        
        public Long getDtime() {
            return this.dtime;
        }
        
        public Long getRssmemPage() {
            return this.rssmemPage;
        }
        
        public int getAge() {
            return this.age;
        }
        
        public void updateProcessInfo(final String name, final String ppid, final Integer pgrpId, final Integer sessionId, final Long utime, final BigInteger stime, final Long vmem, final Long rssmem) {
            this.name = name;
            this.ppid = ppid;
            this.pgrpId = pgrpId;
            this.sessionId = sessionId;
            this.utime = utime;
            this.stime = stime;
            this.vmem = vmem;
            this.rssmemPage = rssmem;
        }
        
        public void updateJiffy(final ProcessInfo oldInfo) {
            if (oldInfo == null) {
                final BigInteger sum = this.stime.add(BigInteger.valueOf(this.utime));
                if (sum.compareTo(this.MAX_LONG) > 0) {
                    this.dtime = 0L;
                    ProcfsBasedProcessTree.LOG.warn("Sum of stime (" + this.stime + ") and utime (" + this.utime + ") is greater than " + Long.MAX_VALUE);
                }
                else {
                    this.dtime = sum.longValue();
                }
                return;
            }
            this.dtime = this.utime - oldInfo.utime + this.stime.subtract(oldInfo.stime).longValue();
        }
        
        public void updateAge(final ProcessInfo oldInfo) {
            this.age = oldInfo.age + 1;
        }
        
        public boolean addChild(final ProcessInfo p) {
            return this.children.add(p);
        }
        
        public List<ProcessInfo> getChildren() {
            return this.children;
        }
        
        public String getCmdLine(final String procfsDir) {
            String ret = "N/A";
            if (this.pid == null) {
                return ret;
            }
            BufferedReader in = null;
            FileReader fReader = null;
            try {
                fReader = new FileReader(new File(new File(procfsDir, this.pid.toString()), "cmdline"));
            }
            catch (FileNotFoundException f) {
                return ret;
            }
            in = new BufferedReader(fReader);
            try {
                ret = in.readLine();
                if (ret == null) {
                    ret = "N/A";
                }
                else {
                    ret = ret.replace('\0', ' ');
                    if (ret.equals("")) {
                        ret = "N/A";
                    }
                }
            }
            catch (IOException io) {
                ProcfsBasedProcessTree.LOG.warn("Error reading the stream " + io);
                ret = "N/A";
                try {
                    fReader.close();
                    try {
                        in.close();
                    }
                    catch (IOException i) {
                        ProcfsBasedProcessTree.LOG.warn("Error closing the stream " + in);
                    }
                }
                catch (IOException i) {
                    ProcfsBasedProcessTree.LOG.warn("Error closing the stream " + fReader);
                }
            }
            finally {
                try {
                    fReader.close();
                    try {
                        in.close();
                    }
                    catch (IOException j) {
                        ProcfsBasedProcessTree.LOG.warn("Error closing the stream " + in);
                    }
                }
                catch (IOException j) {
                    ProcfsBasedProcessTree.LOG.warn("Error closing the stream " + fReader);
                }
            }
            return ret;
        }
    }
    
    static class ProcessTreeSmapMemInfo
    {
        private String pid;
        private List<ProcessSmapMemoryInfo> memoryInfoList;
        
        public ProcessTreeSmapMemInfo(final String pid) {
            this.pid = pid;
            this.memoryInfoList = new LinkedList<ProcessSmapMemoryInfo>();
        }
        
        public List<ProcessSmapMemoryInfo> getMemoryInfoList() {
            return this.memoryInfoList;
        }
        
        public String getPid() {
            return this.pid;
        }
        
        @Override
        public String toString() {
            final StringBuilder sb = new StringBuilder();
            for (final ProcessSmapMemoryInfo info : this.memoryInfoList) {
                sb.append("\n");
                sb.append(info.toString());
            }
            return sb.toString();
        }
    }
    
    static class ProcessSmapMemoryInfo
    {
        private int size;
        private int rss;
        private int pss;
        private int sharedClean;
        private int sharedDirty;
        private int privateClean;
        private int privateDirty;
        private int referenced;
        private String regionName;
        private String permission;
        
        public ProcessSmapMemoryInfo(final String name) {
            this.regionName = name;
        }
        
        public String getName() {
            return this.regionName;
        }
        
        public void setPermission(final String permission) {
            this.permission = permission;
        }
        
        public String getPermission() {
            return this.permission;
        }
        
        public int getSize() {
            return this.size;
        }
        
        public int getRss() {
            return this.rss;
        }
        
        public int getPss() {
            return this.pss;
        }
        
        public int getSharedClean() {
            return this.sharedClean;
        }
        
        public int getSharedDirty() {
            return this.sharedDirty;
        }
        
        public int getPrivateClean() {
            return this.privateClean;
        }
        
        public int getPrivateDirty() {
            return this.privateDirty;
        }
        
        public int getReferenced() {
            return this.referenced;
        }
        
        public void setMemInfo(final String key, final String value) {
            final MemInfo info = MemInfo.getMemInfoByName(key);
            int val = 0;
            try {
                val = Integer.parseInt(value.trim());
            }
            catch (NumberFormatException ne) {
                ProcfsBasedProcessTree.LOG.error("Error in parsing : " + info + " : value" + value.trim());
                return;
            }
            if (info == null) {
                return;
            }
            if (ProcfsBasedProcessTree.LOG.isDebugEnabled()) {
                ProcfsBasedProcessTree.LOG.debug("setMemInfo : memInfo : " + info);
            }
            switch (info) {
                case SIZE: {
                    this.size = val;
                    break;
                }
                case RSS: {
                    this.rss = val;
                    break;
                }
                case PSS: {
                    this.pss = val;
                    break;
                }
                case SHARED_CLEAN: {
                    this.sharedClean = val;
                    break;
                }
                case SHARED_DIRTY: {
                    this.sharedDirty = val;
                    break;
                }
                case PRIVATE_CLEAN: {
                    this.privateClean = val;
                    break;
                }
                case PRIVATE_DIRTY: {
                    this.privateDirty = val;
                    break;
                }
                case REFERENCED: {
                    this.referenced = val;
                    break;
                }
            }
        }
        
        @Override
        public String toString() {
            final StringBuilder sb = new StringBuilder();
            sb.append("\t").append(this.getName()).append("\n");
            sb.append("\t").append(MemInfo.SIZE.name + ":" + this.getSize()).append(" kB\n");
            sb.append("\t").append(MemInfo.PSS.name + ":" + this.getPss()).append(" kB\n");
            sb.append("\t").append(MemInfo.RSS.name + ":" + this.getRss()).append(" kB\n");
            sb.append("\t").append(MemInfo.SHARED_CLEAN.name + ":" + this.getSharedClean()).append(" kB\n");
            sb.append("\t").append(MemInfo.SHARED_DIRTY.name + ":" + this.getSharedDirty()).append(" kB\n");
            sb.append("\t").append(MemInfo.PRIVATE_CLEAN.name + ":" + this.getPrivateClean()).append(" kB\n");
            sb.append("\t").append(MemInfo.PRIVATE_DIRTY.name + ":" + this.getPrivateDirty()).append(" kB\n");
            sb.append("\t").append(MemInfo.REFERENCED.name + ":" + this.getReferenced()).append(" kB\n");
            sb.append("\t").append(MemInfo.PRIVATE_DIRTY.name + ":" + this.getPrivateDirty()).append(" kB\n");
            sb.append("\t").append(MemInfo.PRIVATE_DIRTY.name + ":" + this.getPrivateDirty()).append(" kB\n");
            return sb.toString();
        }
    }
}
