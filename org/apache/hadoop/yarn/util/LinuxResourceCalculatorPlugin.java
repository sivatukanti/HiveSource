// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.yarn.util;

import org.apache.commons.logging.LogFactory;
import java.util.regex.Matcher;
import java.io.IOException;
import java.io.FileNotFoundException;
import java.io.Reader;
import java.io.BufferedReader;
import java.io.FileReader;
import java.util.regex.Pattern;
import org.apache.commons.logging.Log;
import org.apache.hadoop.classification.InterfaceStability;
import org.apache.hadoop.classification.InterfaceAudience;

@InterfaceAudience.Private
@InterfaceStability.Unstable
public class LinuxResourceCalculatorPlugin extends ResourceCalculatorPlugin
{
    private static final Log LOG;
    public static final int UNAVAILABLE = -1;
    private static final String PROCFS_MEMFILE = "/proc/meminfo";
    private static final Pattern PROCFS_MEMFILE_FORMAT;
    private static final String MEMTOTAL_STRING = "MemTotal";
    private static final String SWAPTOTAL_STRING = "SwapTotal";
    private static final String MEMFREE_STRING = "MemFree";
    private static final String SWAPFREE_STRING = "SwapFree";
    private static final String INACTIVE_STRING = "Inactive";
    private static final String PROCFS_CPUINFO = "/proc/cpuinfo";
    private static final Pattern PROCESSOR_FORMAT;
    private static final Pattern FREQUENCY_FORMAT;
    private static final String PROCFS_STAT = "/proc/stat";
    private static final Pattern CPU_TIME_FORMAT;
    private String procfsMemFile;
    private String procfsCpuFile;
    private String procfsStatFile;
    long jiffyLengthInMillis;
    private long ramSize;
    private long swapSize;
    private long ramSizeFree;
    private long swapSizeFree;
    private long inactiveSize;
    private int numProcessors;
    private long cpuFrequency;
    private long cumulativeCpuTime;
    private long lastCumulativeCpuTime;
    private float cpuUsage;
    private long sampleTime;
    private long lastSampleTime;
    boolean readMemInfoFile;
    boolean readCpuInfoFile;
    
    long getCurrentTime() {
        return System.currentTimeMillis();
    }
    
    public LinuxResourceCalculatorPlugin() {
        this.ramSize = 0L;
        this.swapSize = 0L;
        this.ramSizeFree = 0L;
        this.swapSizeFree = 0L;
        this.inactiveSize = 0L;
        this.numProcessors = 0;
        this.cpuFrequency = 0L;
        this.cumulativeCpuTime = 0L;
        this.lastCumulativeCpuTime = 0L;
        this.cpuUsage = -1.0f;
        this.sampleTime = -1L;
        this.lastSampleTime = -1L;
        this.readMemInfoFile = false;
        this.readCpuInfoFile = false;
        this.procfsMemFile = "/proc/meminfo";
        this.procfsCpuFile = "/proc/cpuinfo";
        this.procfsStatFile = "/proc/stat";
        this.jiffyLengthInMillis = ProcfsBasedProcessTree.JIFFY_LENGTH_IN_MILLIS;
    }
    
    public LinuxResourceCalculatorPlugin(final String procfsMemFile, final String procfsCpuFile, final String procfsStatFile, final long jiffyLengthInMillis) {
        this.ramSize = 0L;
        this.swapSize = 0L;
        this.ramSizeFree = 0L;
        this.swapSizeFree = 0L;
        this.inactiveSize = 0L;
        this.numProcessors = 0;
        this.cpuFrequency = 0L;
        this.cumulativeCpuTime = 0L;
        this.lastCumulativeCpuTime = 0L;
        this.cpuUsage = -1.0f;
        this.sampleTime = -1L;
        this.lastSampleTime = -1L;
        this.readMemInfoFile = false;
        this.readCpuInfoFile = false;
        this.procfsMemFile = procfsMemFile;
        this.procfsCpuFile = procfsCpuFile;
        this.procfsStatFile = procfsStatFile;
        this.jiffyLengthInMillis = jiffyLengthInMillis;
    }
    
    private void readProcMemInfoFile() {
        this.readProcMemInfoFile(false);
    }
    
    private void readProcMemInfoFile(final boolean readAgain) {
        if (this.readMemInfoFile && !readAgain) {
            return;
        }
        BufferedReader in = null;
        FileReader fReader = null;
        try {
            fReader = new FileReader(this.procfsMemFile);
            in = new BufferedReader(fReader);
        }
        catch (FileNotFoundException f) {
            return;
        }
        Matcher mat = null;
        try {
            for (String str = in.readLine(); str != null; str = in.readLine()) {
                mat = LinuxResourceCalculatorPlugin.PROCFS_MEMFILE_FORMAT.matcher(str);
                if (mat.find()) {
                    if (mat.group(1).equals("MemTotal")) {
                        this.ramSize = Long.parseLong(mat.group(2));
                    }
                    else if (mat.group(1).equals("SwapTotal")) {
                        this.swapSize = Long.parseLong(mat.group(2));
                    }
                    else if (mat.group(1).equals("MemFree")) {
                        this.ramSizeFree = Long.parseLong(mat.group(2));
                    }
                    else if (mat.group(1).equals("SwapFree")) {
                        this.swapSizeFree = Long.parseLong(mat.group(2));
                    }
                    else if (mat.group(1).equals("Inactive")) {
                        this.inactiveSize = Long.parseLong(mat.group(2));
                    }
                }
            }
        }
        catch (IOException io) {
            LinuxResourceCalculatorPlugin.LOG.warn("Error reading the stream " + io);
            try {
                fReader.close();
                try {
                    in.close();
                }
                catch (IOException i) {
                    LinuxResourceCalculatorPlugin.LOG.warn("Error closing the stream " + in);
                }
            }
            catch (IOException i) {
                LinuxResourceCalculatorPlugin.LOG.warn("Error closing the stream " + fReader);
            }
        }
        finally {
            try {
                fReader.close();
                try {
                    in.close();
                }
                catch (IOException j) {
                    LinuxResourceCalculatorPlugin.LOG.warn("Error closing the stream " + in);
                }
            }
            catch (IOException j) {
                LinuxResourceCalculatorPlugin.LOG.warn("Error closing the stream " + fReader);
            }
        }
        this.readMemInfoFile = true;
    }
    
    private void readProcCpuInfoFile() {
        if (this.readCpuInfoFile) {
            return;
        }
        BufferedReader in = null;
        FileReader fReader = null;
        try {
            fReader = new FileReader(this.procfsCpuFile);
            in = new BufferedReader(fReader);
        }
        catch (FileNotFoundException f) {
            return;
        }
        Matcher mat = null;
        try {
            this.numProcessors = 0;
            for (String str = in.readLine(); str != null; str = in.readLine()) {
                mat = LinuxResourceCalculatorPlugin.PROCESSOR_FORMAT.matcher(str);
                if (mat.find()) {
                    ++this.numProcessors;
                }
                mat = LinuxResourceCalculatorPlugin.FREQUENCY_FORMAT.matcher(str);
                if (mat.find()) {
                    this.cpuFrequency = (long)(Double.parseDouble(mat.group(1)) * 1000.0);
                }
            }
        }
        catch (IOException io) {
            LinuxResourceCalculatorPlugin.LOG.warn("Error reading the stream " + io);
            try {
                fReader.close();
                try {
                    in.close();
                }
                catch (IOException i) {
                    LinuxResourceCalculatorPlugin.LOG.warn("Error closing the stream " + in);
                }
            }
            catch (IOException i) {
                LinuxResourceCalculatorPlugin.LOG.warn("Error closing the stream " + fReader);
            }
        }
        finally {
            try {
                fReader.close();
                try {
                    in.close();
                }
                catch (IOException j) {
                    LinuxResourceCalculatorPlugin.LOG.warn("Error closing the stream " + in);
                }
            }
            catch (IOException j) {
                LinuxResourceCalculatorPlugin.LOG.warn("Error closing the stream " + fReader);
            }
        }
        this.readCpuInfoFile = true;
    }
    
    private void readProcStatFile() {
        BufferedReader in = null;
        FileReader fReader = null;
        try {
            fReader = new FileReader(this.procfsStatFile);
            in = new BufferedReader(fReader);
        }
        catch (FileNotFoundException f) {
            return;
        }
        Matcher mat = null;
        try {
            for (String str = in.readLine(); str != null; str = in.readLine()) {
                mat = LinuxResourceCalculatorPlugin.CPU_TIME_FORMAT.matcher(str);
                if (mat.find()) {
                    final long uTime = Long.parseLong(mat.group(1));
                    final long nTime = Long.parseLong(mat.group(2));
                    final long sTime = Long.parseLong(mat.group(3));
                    this.cumulativeCpuTime = uTime + nTime + sTime;
                    break;
                }
            }
            this.cumulativeCpuTime *= this.jiffyLengthInMillis;
        }
        catch (IOException io) {
            LinuxResourceCalculatorPlugin.LOG.warn("Error reading the stream " + io);
            try {
                fReader.close();
                try {
                    in.close();
                }
                catch (IOException i) {
                    LinuxResourceCalculatorPlugin.LOG.warn("Error closing the stream " + in);
                }
            }
            catch (IOException i) {
                LinuxResourceCalculatorPlugin.LOG.warn("Error closing the stream " + fReader);
            }
        }
        finally {
            try {
                fReader.close();
                try {
                    in.close();
                }
                catch (IOException j) {
                    LinuxResourceCalculatorPlugin.LOG.warn("Error closing the stream " + in);
                }
            }
            catch (IOException j) {
                LinuxResourceCalculatorPlugin.LOG.warn("Error closing the stream " + fReader);
            }
        }
    }
    
    @Override
    public long getPhysicalMemorySize() {
        this.readProcMemInfoFile();
        return this.ramSize * 1024L;
    }
    
    @Override
    public long getVirtualMemorySize() {
        this.readProcMemInfoFile();
        return (this.ramSize + this.swapSize) * 1024L;
    }
    
    @Override
    public long getAvailablePhysicalMemorySize() {
        this.readProcMemInfoFile(true);
        return (this.ramSizeFree + this.inactiveSize) * 1024L;
    }
    
    @Override
    public long getAvailableVirtualMemorySize() {
        this.readProcMemInfoFile(true);
        return (this.ramSizeFree + this.swapSizeFree + this.inactiveSize) * 1024L;
    }
    
    @Override
    public int getNumProcessors() {
        this.readProcCpuInfoFile();
        return this.numProcessors;
    }
    
    @Override
    public long getCpuFrequency() {
        this.readProcCpuInfoFile();
        return this.cpuFrequency;
    }
    
    @Override
    public long getCumulativeCpuTime() {
        this.readProcStatFile();
        return this.cumulativeCpuTime;
    }
    
    @Override
    public float getCpuUsage() {
        this.readProcStatFile();
        this.sampleTime = this.getCurrentTime();
        if (this.lastSampleTime == -1L || this.lastSampleTime > this.sampleTime) {
            this.lastSampleTime = this.sampleTime;
            this.lastCumulativeCpuTime = this.cumulativeCpuTime;
            return this.cpuUsage;
        }
        final long MINIMUM_UPDATE_INTERVAL = 10L * this.jiffyLengthInMillis;
        if (this.sampleTime > this.lastSampleTime + MINIMUM_UPDATE_INTERVAL) {
            this.cpuUsage = (this.cumulativeCpuTime - this.lastCumulativeCpuTime) * 100.0f / ((this.sampleTime - this.lastSampleTime) * (float)this.getNumProcessors());
            this.lastSampleTime = this.sampleTime;
            this.lastCumulativeCpuTime = this.cumulativeCpuTime;
        }
        return this.cpuUsage;
    }
    
    public static void main(final String[] args) {
        final LinuxResourceCalculatorPlugin plugin = new LinuxResourceCalculatorPlugin();
        System.out.println("Physical memory Size (bytes) : " + plugin.getPhysicalMemorySize());
        System.out.println("Total Virtual memory Size (bytes) : " + plugin.getVirtualMemorySize());
        System.out.println("Available Physical memory Size (bytes) : " + plugin.getAvailablePhysicalMemorySize());
        System.out.println("Total Available Virtual memory Size (bytes) : " + plugin.getAvailableVirtualMemorySize());
        System.out.println("Number of Processors : " + plugin.getNumProcessors());
        System.out.println("CPU frequency (kHz) : " + plugin.getCpuFrequency());
        System.out.println("Cumulative CPU time (ms) : " + plugin.getCumulativeCpuTime());
        try {
            Thread.sleep(500L);
        }
        catch (InterruptedException ex) {}
        System.out.println("CPU usage % : " + plugin.getCpuUsage());
    }
    
    static {
        LOG = LogFactory.getLog(LinuxResourceCalculatorPlugin.class);
        PROCFS_MEMFILE_FORMAT = Pattern.compile("^([a-zA-Z]*):[ \t]*([0-9]*)[ \t]kB");
        PROCESSOR_FORMAT = Pattern.compile("^processor[ \t]:[ \t]*([0-9]*)");
        FREQUENCY_FORMAT = Pattern.compile("^cpu MHz[ \t]*:[ \t]*([0-9.]*)");
        CPU_TIME_FORMAT = Pattern.compile("^cpu[ \t]*([0-9]*)[ \t]*([0-9]*)[ \t]*([0-9]*)[ \t].*");
    }
}
