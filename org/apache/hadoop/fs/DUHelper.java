// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.fs;

import org.apache.hadoop.util.Shell;
import java.io.File;

public class DUHelper
{
    private int folderCount;
    private int fileCount;
    private double usage;
    private long folderSize;
    
    private DUHelper() {
        this.folderCount = 0;
        this.fileCount = 0;
        this.usage = 0.0;
        this.folderSize = -1L;
    }
    
    public static long getFolderUsage(final String folder) {
        return new DUHelper().calculateFolderSize(folder);
    }
    
    private long calculateFolderSize(final String folder) {
        if (folder == null) {
            throw new IllegalArgumentException("folder");
        }
        final File f = new File(folder);
        return this.folderSize = this.getFileSize(f);
    }
    
    public String check(final String folder) {
        if (folder == null) {
            throw new IllegalArgumentException("folder");
        }
        final File f = new File(folder);
        this.folderSize = this.getFileSize(f);
        this.usage = 1.0 * (f.getTotalSpace() - f.getFreeSpace()) / f.getTotalSpace();
        return String.format("used %d files %d disk in use %f", this.folderSize, this.fileCount, this.usage);
    }
    
    public long getFileCount() {
        return this.fileCount;
    }
    
    public double getUsage() {
        return this.usage;
    }
    
    private long getFileSize(final File folder) {
        ++this.folderCount;
        long foldersize = 0L;
        if (folder.isFile()) {
            return folder.length();
        }
        final File[] filelist = folder.listFiles();
        if (filelist == null) {
            return 0L;
        }
        for (int i = 0; i < filelist.length; ++i) {
            if (filelist[i].isDirectory()) {
                foldersize += this.getFileSize(filelist[i]);
            }
            else {
                ++this.fileCount;
                foldersize += filelist[i].length();
            }
        }
        return foldersize;
    }
    
    public static void main(final String[] args) {
        if (Shell.WINDOWS) {
            System.out.println("Windows: " + getFolderUsage(args[0]));
        }
        else {
            System.out.println("Other: " + getFolderUsage(args[0]));
        }
    }
}
