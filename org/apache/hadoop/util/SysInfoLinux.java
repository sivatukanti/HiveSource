// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.util;

import org.slf4j.LoggerFactory;
import java.math.BigInteger;
import java.util.HashSet;
import java.util.regex.Matcher;
import java.io.FileNotFoundException;
import java.io.Reader;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.io.FileInputStream;
import com.google.common.annotations.VisibleForTesting;
import java.io.IOException;
import java.util.HashMap;
import java.util.regex.Pattern;
import org.slf4j.Logger;
import org.apache.hadoop.classification.InterfaceStability;
import org.apache.hadoop.classification.InterfaceAudience;

@InterfaceAudience.Private
@InterfaceStability.Evolving
public class SysInfoLinux extends SysInfo
{
    private static final Logger LOG;
    private static final String PROCFS_MEMFILE = "/proc/meminfo";
    private static final Pattern PROCFS_MEMFILE_FORMAT;
    private static final String MEMTOTAL_STRING = "MemTotal";
    private static final String SWAPTOTAL_STRING = "SwapTotal";
    private static final String MEMFREE_STRING = "MemFree";
    private static final String SWAPFREE_STRING = "SwapFree";
    private static final String INACTIVE_STRING = "Inactive";
    private static final String INACTIVEFILE_STRING = "Inactive(file)";
    private static final String HARDWARECORRUPTED_STRING = "HardwareCorrupted";
    private static final String HUGEPAGESTOTAL_STRING = "HugePages_Total";
    private static final String HUGEPAGESIZE_STRING = "Hugepagesize";
    private static final String PROCFS_CPUINFO = "/proc/cpuinfo";
    private static final Pattern PROCESSOR_FORMAT;
    private static final Pattern FREQUENCY_FORMAT;
    private static final Pattern PHYSICAL_ID_FORMAT;
    private static final Pattern CORE_ID_FORMAT;
    private static final String PROCFS_STAT = "/proc/stat";
    private static final Pattern CPU_TIME_FORMAT;
    private CpuTimeTracker cpuTimeTracker;
    private static final String PROCFS_NETFILE = "/proc/net/dev";
    private static final Pattern PROCFS_NETFILE_FORMAT;
    private static final String PROCFS_DISKSFILE = "/proc/diskstats";
    private static final Pattern PROCFS_DISKSFILE_FORMAT;
    private static final Pattern PROCFS_DISKSECTORFILE_FORMAT;
    private String procfsMemFile;
    private String procfsCpuFile;
    private String procfsStatFile;
    private String procfsNetFile;
    private String procfsDisksFile;
    private long jiffyLengthInMillis;
    private long ramSize;
    private long swapSize;
    private long ramSizeFree;
    private long swapSizeFree;
    private long inactiveSize;
    private long inactiveFileSize;
    private long hardwareCorruptSize;
    private long hugePagesTotal;
    private long hugePageSize;
    private int numProcessors;
    private int numCores;
    private long cpuFrequency;
    private long numNetBytesRead;
    private long numNetBytesWritten;
    private long numDisksBytesRead;
    private long numDisksBytesWritten;
    private boolean readMemInfoFile;
    private boolean readCpuInfoFile;
    private HashMap<String, Integer> perDiskSectorSize;
    public static final long PAGE_SIZE;
    public static final long JIFFY_LENGTH_IN_MILLIS;
    
    private static long getConf(final String attr) {
        if (Shell.LINUX) {
            try {
                final Shell.ShellCommandExecutor shellExecutorClk = new Shell.ShellCommandExecutor(new String[] { "getconf", attr });
                shellExecutorClk.execute();
                return Long.parseLong(shellExecutorClk.getOutput().replace("\n", ""));
            }
            catch (IOException | NumberFormatException ex2) {
                final Exception ex;
                final Exception e = ex;
                return -1L;
            }
        }
        return -1L;
    }
    
    long getCurrentTime() {
        return System.currentTimeMillis();
    }
    
    public SysInfoLinux() {
        this("/proc/meminfo", "/proc/cpuinfo", "/proc/stat", "/proc/net/dev", "/proc/diskstats", SysInfoLinux.JIFFY_LENGTH_IN_MILLIS);
    }
    
    @VisibleForTesting
    public SysInfoLinux(final String procfsMemFile, final String procfsCpuFile, final String procfsStatFile, final String procfsNetFile, final String procfsDisksFile, final long jiffyLengthInMillis) {
        this.ramSize = 0L;
        this.swapSize = 0L;
        this.ramSizeFree = 0L;
        this.swapSizeFree = 0L;
        this.inactiveSize = 0L;
        this.inactiveFileSize = -1L;
        this.hardwareCorruptSize = 0L;
        this.hugePagesTotal = 0L;
        this.hugePageSize = 0L;
        this.numProcessors = 0;
        this.numCores = 0;
        this.cpuFrequency = 0L;
        this.numNetBytesRead = 0L;
        this.numNetBytesWritten = 0L;
        this.numDisksBytesRead = 0L;
        this.numDisksBytesWritten = 0L;
        this.readMemInfoFile = false;
        this.readCpuInfoFile = false;
        this.perDiskSectorSize = null;
        this.procfsMemFile = procfsMemFile;
        this.procfsCpuFile = procfsCpuFile;
        this.procfsStatFile = procfsStatFile;
        this.procfsNetFile = procfsNetFile;
        this.procfsDisksFile = procfsDisksFile;
        this.jiffyLengthInMillis = jiffyLengthInMillis;
        this.cpuTimeTracker = new CpuTimeTracker(jiffyLengthInMillis);
        this.perDiskSectorSize = new HashMap<String, Integer>();
    }
    
    private void readProcMemInfoFile() {
        this.readProcMemInfoFile(false);
    }
    
    private long safeParseLong(final String strVal) {
        long parsedVal;
        try {
            parsedVal = Long.parseLong(strVal);
        }
        catch (NumberFormatException nfe) {
            parsedVal = 0L;
        }
        return parsedVal;
    }
    
    private void readProcMemInfoFile(final boolean readAgain) {
        if (this.readMemInfoFile && !readAgain) {
            return;
        }
        InputStreamReader fReader;
        BufferedReader in;
        try {
            fReader = new InputStreamReader(new FileInputStream(this.procfsMemFile), Charset.forName("UTF-8"));
            in = new BufferedReader(fReader);
        }
        catch (FileNotFoundException f) {
            SysInfoLinux.LOG.warn("Couldn't read " + this.procfsMemFile + "; can't determine memory settings");
            return;
        }
        try {
            for (String str = in.readLine(); str != null; str = in.readLine()) {
                final Matcher mat = SysInfoLinux.PROCFS_MEMFILE_FORMAT.matcher(str);
                if (mat.find()) {
                    if (mat.group(1).equals("MemTotal")) {
                        this.ramSize = Long.parseLong(mat.group(2));
                    }
                    else if (mat.group(1).equals("SwapTotal")) {
                        this.swapSize = Long.parseLong(mat.group(2));
                    }
                    else if (mat.group(1).equals("MemFree")) {
                        this.ramSizeFree = this.safeParseLong(mat.group(2));
                    }
                    else if (mat.group(1).equals("SwapFree")) {
                        this.swapSizeFree = this.safeParseLong(mat.group(2));
                    }
                    else if (mat.group(1).equals("Inactive")) {
                        this.inactiveSize = Long.parseLong(mat.group(2));
                    }
                    else if (mat.group(1).equals("Inactive(file)")) {
                        this.inactiveFileSize = Long.parseLong(mat.group(2));
                    }
                    else if (mat.group(1).equals("HardwareCorrupted")) {
                        this.hardwareCorruptSize = Long.parseLong(mat.group(2));
                    }
                    else if (mat.group(1).equals("HugePages_Total")) {
                        this.hugePagesTotal = Long.parseLong(mat.group(2));
                    }
                    else if (mat.group(1).equals("Hugepagesize")) {
                        this.hugePageSize = Long.parseLong(mat.group(2));
                    }
                }
            }
        }
        catch (IOException io) {
            SysInfoLinux.LOG.warn("Error reading the stream " + io);
            try {
                fReader.close();
                try {
                    in.close();
                }
                catch (IOException i) {
                    SysInfoLinux.LOG.warn("Error closing the stream " + in);
                }
            }
            catch (IOException i) {
                SysInfoLinux.LOG.warn("Error closing the stream " + fReader);
            }
        }
        finally {
            try {
                fReader.close();
                try {
                    in.close();
                }
                catch (IOException j) {
                    SysInfoLinux.LOG.warn("Error closing the stream " + in);
                }
            }
            catch (IOException j) {
                SysInfoLinux.LOG.warn("Error closing the stream " + fReader);
            }
        }
        this.readMemInfoFile = true;
    }
    
    private void readProcCpuInfoFile() {
        if (this.readCpuInfoFile) {
            return;
        }
        final HashSet<String> coreIdSet = new HashSet<String>();
        InputStreamReader fReader;
        BufferedReader in;
        try {
            fReader = new InputStreamReader(new FileInputStream(this.procfsCpuFile), Charset.forName("UTF-8"));
            in = new BufferedReader(fReader);
        }
        catch (FileNotFoundException f) {
            SysInfoLinux.LOG.warn("Couldn't read " + this.procfsCpuFile + "; can't determine cpu info");
            return;
        }
        try {
            this.numProcessors = 0;
            this.numCores = 1;
            String currentPhysicalId = "";
            for (String str = in.readLine(); str != null; str = in.readLine()) {
                Matcher mat = SysInfoLinux.PROCESSOR_FORMAT.matcher(str);
                if (mat.find()) {
                    ++this.numProcessors;
                }
                mat = SysInfoLinux.FREQUENCY_FORMAT.matcher(str);
                if (mat.find()) {
                    this.cpuFrequency = (long)(Double.parseDouble(mat.group(1)) * 1000.0);
                }
                mat = SysInfoLinux.PHYSICAL_ID_FORMAT.matcher(str);
                if (mat.find()) {
                    currentPhysicalId = str;
                }
                mat = SysInfoLinux.CORE_ID_FORMAT.matcher(str);
                if (mat.find()) {
                    coreIdSet.add(currentPhysicalId + " " + str);
                    this.numCores = coreIdSet.size();
                }
            }
        }
        catch (IOException io) {
            SysInfoLinux.LOG.warn("Error reading the stream " + io);
            try {
                fReader.close();
                try {
                    in.close();
                }
                catch (IOException i) {
                    SysInfoLinux.LOG.warn("Error closing the stream " + in);
                }
            }
            catch (IOException i) {
                SysInfoLinux.LOG.warn("Error closing the stream " + fReader);
            }
        }
        finally {
            try {
                fReader.close();
                try {
                    in.close();
                }
                catch (IOException j) {
                    SysInfoLinux.LOG.warn("Error closing the stream " + in);
                }
            }
            catch (IOException j) {
                SysInfoLinux.LOG.warn("Error closing the stream " + fReader);
            }
        }
        this.readCpuInfoFile = true;
    }
    
    private void readProcStatFile() {
        InputStreamReader fReader;
        BufferedReader in;
        try {
            fReader = new InputStreamReader(new FileInputStream(this.procfsStatFile), Charset.forName("UTF-8"));
            in = new BufferedReader(fReader);
        }
        catch (FileNotFoundException f) {
            return;
        }
        try {
            for (String str = in.readLine(); str != null; str = in.readLine()) {
                final Matcher mat = SysInfoLinux.CPU_TIME_FORMAT.matcher(str);
                if (mat.find()) {
                    final long uTime = Long.parseLong(mat.group(1));
                    final long nTime = Long.parseLong(mat.group(2));
                    final long sTime = Long.parseLong(mat.group(3));
                    this.cpuTimeTracker.updateElapsedJiffies(BigInteger.valueOf(uTime + nTime + sTime), this.getCurrentTime());
                    break;
                }
            }
        }
        catch (IOException io) {
            SysInfoLinux.LOG.warn("Error reading the stream " + io);
            try {
                fReader.close();
                try {
                    in.close();
                }
                catch (IOException i) {
                    SysInfoLinux.LOG.warn("Error closing the stream " + in);
                }
            }
            catch (IOException i) {
                SysInfoLinux.LOG.warn("Error closing the stream " + fReader);
            }
        }
        finally {
            try {
                fReader.close();
                try {
                    in.close();
                }
                catch (IOException j) {
                    SysInfoLinux.LOG.warn("Error closing the stream " + in);
                }
            }
            catch (IOException j) {
                SysInfoLinux.LOG.warn("Error closing the stream " + fReader);
            }
        }
    }
    
    private void readProcNetInfoFile() {
        this.numNetBytesRead = 0L;
        this.numNetBytesWritten = 0L;
        InputStreamReader fReader;
        BufferedReader in;
        try {
            fReader = new InputStreamReader(new FileInputStream(this.procfsNetFile), Charset.forName("UTF-8"));
            in = new BufferedReader(fReader);
        }
        catch (FileNotFoundException f) {
            return;
        }
        try {
            String str = in.readLine();
            while (str != null) {
                final Matcher mat = SysInfoLinux.PROCFS_NETFILE_FORMAT.matcher(str);
                if (mat.find()) {
                    assert mat.groupCount() >= 16;
                    if (mat.group(1).equals("lo")) {
                        str = in.readLine();
                        continue;
                    }
                    this.numNetBytesRead += Long.parseLong(mat.group(2));
                    this.numNetBytesWritten += Long.parseLong(mat.group(10));
                }
                str = in.readLine();
            }
        }
        catch (IOException io) {
            SysInfoLinux.LOG.warn("Error reading the stream " + io);
            try {
                fReader.close();
                try {
                    in.close();
                }
                catch (IOException i) {
                    SysInfoLinux.LOG.warn("Error closing the stream " + in);
                }
            }
            catch (IOException i) {
                SysInfoLinux.LOG.warn("Error closing the stream " + fReader);
            }
        }
        finally {
            try {
                fReader.close();
                try {
                    in.close();
                }
                catch (IOException j) {
                    SysInfoLinux.LOG.warn("Error closing the stream " + in);
                }
            }
            catch (IOException j) {
                SysInfoLinux.LOG.warn("Error closing the stream " + fReader);
            }
        }
    }
    
    private void readProcDisksInfoFile() {
        this.numDisksBytesRead = 0L;
        this.numDisksBytesWritten = 0L;
        BufferedReader in;
        try {
            in = new BufferedReader(new InputStreamReader(new FileInputStream(this.procfsDisksFile), Charset.forName("UTF-8")));
        }
        catch (FileNotFoundException f) {
            return;
        }
        try {
            String str = in.readLine();
            while (str != null) {
                final Matcher mat = SysInfoLinux.PROCFS_DISKSFILE_FORMAT.matcher(str);
                Label_0304: {
                    if (mat.find()) {
                        final String diskName = mat.group(4);
                        assert diskName != null;
                        if (diskName.contains("loop") || diskName.contains("ram")) {
                            str = in.readLine();
                            continue;
                        }
                        Integer sectorSize;
                        synchronized (this.perDiskSectorSize) {
                            sectorSize = this.perDiskSectorSize.get(diskName);
                            if (null == sectorSize) {
                                sectorSize = this.readDiskBlockInformation(diskName, 512);
                                this.perDiskSectorSize.put(diskName, sectorSize);
                            }
                        }
                        final String sectorsRead = mat.group(7);
                        final String sectorsWritten = mat.group(11);
                        if (null != sectorsRead) {
                            if (null != sectorsWritten) {
                                this.numDisksBytesRead += Long.parseLong(sectorsRead) * sectorSize;
                                this.numDisksBytesWritten += Long.parseLong(sectorsWritten) * sectorSize;
                                break Label_0304;
                            }
                        }
                        return;
                    }
                }
                str = in.readLine();
            }
        }
        catch (IOException e) {
            SysInfoLinux.LOG.warn("Error reading the stream " + this.procfsDisksFile, e);
            try {
                in.close();
            }
            catch (IOException e) {
                SysInfoLinux.LOG.warn("Error closing the stream " + this.procfsDisksFile, e);
            }
        }
        finally {
            try {
                in.close();
            }
            catch (IOException e2) {
                SysInfoLinux.LOG.warn("Error closing the stream " + this.procfsDisksFile, e2);
            }
        }
    }
    
    int readDiskBlockInformation(final String diskName, final int defSector) {
        assert this.perDiskSectorSize != null && diskName != null;
        final String procfsDiskSectorFile = "/sys/block/" + diskName + "/queue/hw_sector_size";
        BufferedReader in;
        try {
            in = new BufferedReader(new InputStreamReader(new FileInputStream(procfsDiskSectorFile), Charset.forName("UTF-8")));
        }
        catch (FileNotFoundException f) {
            return defSector;
        }
        try {
            for (String str = in.readLine(); str != null; str = in.readLine()) {
                final Matcher mat = SysInfoLinux.PROCFS_DISKSECTORFILE_FORMAT.matcher(str);
                if (mat.find()) {
                    final String secSize = mat.group(1);
                    if (secSize != null) {
                        return Integer.parseInt(secSize);
                    }
                }
            }
            return defSector;
        }
        catch (IOException ex) {}
        catch (NumberFormatException e) {
            SysInfoLinux.LOG.warn("Error reading the stream " + procfsDiskSectorFile, e);
            return defSector;
        }
        finally {
            try {
                in.close();
            }
            catch (IOException e2) {
                SysInfoLinux.LOG.warn("Error closing the stream " + procfsDiskSectorFile, e2);
            }
        }
    }
    
    @Override
    public long getPhysicalMemorySize() {
        this.readProcMemInfoFile();
        return (this.ramSize - this.hardwareCorruptSize - this.hugePagesTotal * this.hugePageSize) * 1024L;
    }
    
    @Override
    public long getVirtualMemorySize() {
        return this.getPhysicalMemorySize() + this.swapSize * 1024L;
    }
    
    @Override
    public long getAvailablePhysicalMemorySize() {
        this.readProcMemInfoFile(true);
        final long inactive = (this.inactiveFileSize != -1L) ? this.inactiveFileSize : this.inactiveSize;
        return (this.ramSizeFree + inactive) * 1024L;
    }
    
    @Override
    public long getAvailableVirtualMemorySize() {
        return this.getAvailablePhysicalMemorySize() + this.swapSizeFree * 1024L;
    }
    
    @Override
    public int getNumProcessors() {
        this.readProcCpuInfoFile();
        return this.numProcessors;
    }
    
    @Override
    public int getNumCores() {
        this.readProcCpuInfoFile();
        return this.numCores;
    }
    
    @Override
    public long getCpuFrequency() {
        this.readProcCpuInfoFile();
        return this.cpuFrequency;
    }
    
    @Override
    public long getCumulativeCpuTime() {
        this.readProcStatFile();
        return this.cpuTimeTracker.getCumulativeCpuTime();
    }
    
    @Override
    public float getCpuUsagePercentage() {
        this.readProcStatFile();
        float overallCpuUsage = this.cpuTimeTracker.getCpuTrackerUsagePercent();
        if (overallCpuUsage != -1.0f) {
            overallCpuUsage /= this.getNumProcessors();
        }
        return overallCpuUsage;
    }
    
    @Override
    public float getNumVCoresUsed() {
        this.readProcStatFile();
        float overallVCoresUsage = this.cpuTimeTracker.getCpuTrackerUsagePercent();
        if (overallVCoresUsage != -1.0f) {
            overallVCoresUsage /= 100.0f;
        }
        return overallVCoresUsage;
    }
    
    @Override
    public long getNetworkBytesRead() {
        this.readProcNetInfoFile();
        return this.numNetBytesRead;
    }
    
    @Override
    public long getNetworkBytesWritten() {
        this.readProcNetInfoFile();
        return this.numNetBytesWritten;
    }
    
    @Override
    public long getStorageBytesRead() {
        this.readProcDisksInfoFile();
        return this.numDisksBytesRead;
    }
    
    @Override
    public long getStorageBytesWritten() {
        this.readProcDisksInfoFile();
        return this.numDisksBytesWritten;
    }
    
    public static void main(final String[] args) {
        final SysInfoLinux plugin = new SysInfoLinux();
        System.out.println("Physical memory Size (bytes) : " + plugin.getPhysicalMemorySize());
        System.out.println("Total Virtual memory Size (bytes) : " + plugin.getVirtualMemorySize());
        System.out.println("Available Physical memory Size (bytes) : " + plugin.getAvailablePhysicalMemorySize());
        System.out.println("Total Available Virtual memory Size (bytes) : " + plugin.getAvailableVirtualMemorySize());
        System.out.println("Number of Processors : " + plugin.getNumProcessors());
        System.out.println("CPU frequency (kHz) : " + plugin.getCpuFrequency());
        System.out.println("Cumulative CPU time (ms) : " + plugin.getCumulativeCpuTime());
        System.out.println("Total network read (bytes) : " + plugin.getNetworkBytesRead());
        System.out.println("Total network written (bytes) : " + plugin.getNetworkBytesWritten());
        System.out.println("Total storage read (bytes) : " + plugin.getStorageBytesRead());
        System.out.println("Total storage written (bytes) : " + plugin.getStorageBytesWritten());
        try {
            Thread.sleep(500L);
        }
        catch (InterruptedException ex) {}
        System.out.println("CPU usage % : " + plugin.getCpuUsagePercentage());
    }
    
    @VisibleForTesting
    void setReadCpuInfoFile(final boolean readCpuInfoFileValue) {
        this.readCpuInfoFile = readCpuInfoFileValue;
    }
    
    public long getJiffyLengthInMillis() {
        return this.jiffyLengthInMillis;
    }
    
    static {
        LOG = LoggerFactory.getLogger(SysInfoLinux.class);
        PROCFS_MEMFILE_FORMAT = Pattern.compile("^([a-zA-Z_()]*):[ \t]*([0-9]*)[ \t]*(kB)?");
        PROCESSOR_FORMAT = Pattern.compile("^processor[ \t]:[ \t]*([0-9]*)");
        FREQUENCY_FORMAT = Pattern.compile("^cpu MHz[ \t]*:[ \t]*([0-9.]*)");
        PHYSICAL_ID_FORMAT = Pattern.compile("^physical id[ \t]*:[ \t]*([0-9]*)");
        CORE_ID_FORMAT = Pattern.compile("^core id[ \t]*:[ \t]*([0-9]*)");
        CPU_TIME_FORMAT = Pattern.compile("^cpu[ \t]*([0-9]*)[ \t]*([0-9]*)[ \t]*([0-9]*)[ \t].*");
        PROCFS_NETFILE_FORMAT = Pattern.compile("^[ \t]*([a-zA-Z]+[0-9]*):[ \t]*([0-9]+)[ \t]*([0-9]+)[ \t]*([0-9]+)[ \t]*([0-9]+)[ \t]*([0-9]+)[ \t]*([0-9]+)[ \t]*([0-9]+)[ \t]*([0-9]+)[ \t]*([0-9]+)[ \t]*([0-9]+)[ \t]*([0-9]+)[ \t]*([0-9]+)[ \t]*([0-9]+)[ \t]*([0-9]+)[ \t]*([0-9]+)[ \t]*([0-9]+).*");
        PROCFS_DISKSFILE_FORMAT = Pattern.compile("^[ \t]*([0-9]+)[ \t]*([0-9 ]+)(?!([a-zA-Z]+[0-9]+))([a-zA-Z]+)[ \t]*([0-9]+)[ \t]*([0-9]+)[ \t]*([0-9]+)[ \t]*([0-9]+)[ \t]*([0-9]+)[ \t]*([0-9]+)[ \t]*([0-9]+)[ \t]*([0-9]+)[ \t]*([0-9]+)[ \t]*([0-9]+)[ \t]*([0-9]+)");
        PROCFS_DISKSECTORFILE_FORMAT = Pattern.compile("^([0-9]+)");
        PAGE_SIZE = getConf("PAGESIZE");
        JIFFY_LENGTH_IN_MILLIS = Math.max(Math.round(1000.0 / getConf("CLK_TCK")), -1L);
    }
}
