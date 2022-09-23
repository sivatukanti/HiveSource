// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.fs;

import com.google.common.annotations.VisibleForTesting;
import java.io.Closeable;
import org.apache.hadoop.io.IOUtils;
import java.io.Reader;
import java.io.BufferedReader;
import java.io.StringReader;
import org.apache.hadoop.util.Shell;
import java.io.FileNotFoundException;
import java.nio.file.Files;
import java.io.IOException;
import java.io.File;

public class HardLink
{
    private static HardLinkCommandGetter getHardLinkCommand;
    public final LinkStats linkStats;
    
    public HardLink() {
        this.linkStats = new LinkStats();
    }
    
    public static void createHardLink(final File file, final File linkName) throws IOException {
        if (file == null) {
            throw new IOException("invalid arguments to createHardLink: source file is null");
        }
        if (linkName == null) {
            throw new IOException("invalid arguments to createHardLink: link name is null");
        }
        Files.createLink(linkName.toPath(), file.toPath());
    }
    
    public static void createHardLinkMult(final File parentDir, final String[] fileBaseNames, final File linkDir) throws IOException {
        if (parentDir == null) {
            throw new IOException("invalid arguments to createHardLinkMult: parent directory is null");
        }
        if (linkDir == null) {
            throw new IOException("invalid arguments to createHardLinkMult: link directory is null");
        }
        if (fileBaseNames == null) {
            throw new IOException("invalid arguments to createHardLinkMult: filename list can be empty but not null");
        }
        if (!linkDir.exists()) {
            throw new FileNotFoundException(linkDir + " not found.");
        }
        for (final String name : fileBaseNames) {
            Files.createLink(linkDir.toPath().resolve(name), parentDir.toPath().resolve(name));
        }
    }
    
    public static int getLinkCount(final File fileName) throws IOException {
        if (fileName == null) {
            throw new IOException("invalid argument to getLinkCount: file name is null");
        }
        if (!fileName.exists()) {
            throw new FileNotFoundException(fileName + " not found.");
        }
        final String[] cmd = HardLink.getHardLinkCommand.linkCount(fileName);
        String inpMsg = null;
        String errMsg = null;
        int exitValue = -1;
        BufferedReader in = null;
        final Shell.ShellCommandExecutor shexec = new Shell.ShellCommandExecutor(cmd);
        try {
            shexec.execute();
            in = new BufferedReader(new StringReader(shexec.getOutput()));
            inpMsg = in.readLine();
            exitValue = shexec.getExitCode();
            if (inpMsg == null || exitValue != 0) {
                throw createIOException(fileName, inpMsg, errMsg, exitValue, null);
            }
            if (Shell.SOLARIS) {
                final String[] result = inpMsg.split("\\s+");
                return Integer.parseInt(result[1]);
            }
            return Integer.parseInt(inpMsg);
        }
        catch (Shell.ExitCodeException e) {
            inpMsg = shexec.getOutput();
            errMsg = e.getMessage();
            exitValue = e.getExitCode();
            throw createIOException(fileName, inpMsg, errMsg, exitValue, e);
        }
        catch (NumberFormatException e2) {
            throw createIOException(fileName, inpMsg, errMsg, exitValue, e2);
        }
        finally {
            IOUtils.closeStream(in);
        }
    }
    
    private static IOException createIOException(final File f, final String message, final String error, final int exitvalue, final Exception cause) {
        final String s = "Failed to get link count on file " + f + ": message=" + message + "; error=" + error + "; exit value=" + exitvalue;
        return (cause == null) ? new IOException(s) : new IOException(s, cause);
    }
    
    static {
        if (Shell.WINDOWS) {
            HardLink.getHardLinkCommand = new HardLinkCGWin();
        }
        else {
            HardLink.getHardLinkCommand = new HardLinkCGUnix();
            if (Shell.MAC || Shell.FREEBSD) {
                final String[] linkCountCmdTemplate = { "/usr/bin/stat", "-f%l", null };
                setLinkCountCmdTemplate(linkCountCmdTemplate);
            }
            else if (Shell.SOLARIS) {
                final String[] linkCountCmdTemplate = { "ls", "-l", null };
                setLinkCountCmdTemplate(linkCountCmdTemplate);
            }
        }
    }
    
    private abstract static class HardLinkCommandGetter
    {
        abstract String[] linkCount(final File p0) throws IOException;
    }
    
    private static class HardLinkCGUnix extends HardLinkCommandGetter
    {
        private static String[] getLinkCountCommand;
        
        private static synchronized void setLinkCountCmdTemplate(final String[] template) {
            HardLinkCGUnix.getLinkCountCommand = template;
        }
        
        @Override
        String[] linkCount(final File file) throws IOException {
            final String[] buf = new String[HardLinkCGUnix.getLinkCountCommand.length];
            System.arraycopy(HardLinkCGUnix.getLinkCountCommand, 0, buf, 0, HardLinkCGUnix.getLinkCountCommand.length);
            buf[HardLinkCGUnix.getLinkCountCommand.length - 1] = FileUtil.makeShellPath(file, true);
            return buf;
        }
        
        static {
            HardLinkCGUnix.getLinkCountCommand = new String[] { "stat", "-c%h", null };
        }
    }
    
    @VisibleForTesting
    static class HardLinkCGWin extends HardLinkCommandGetter
    {
        static String[] getLinkCountCommand;
        
        @Override
        String[] linkCount(final File file) throws IOException {
            Shell.getWinUtilsFile();
            final String[] buf = new String[HardLinkCGWin.getLinkCountCommand.length];
            System.arraycopy(HardLinkCGWin.getLinkCountCommand, 0, buf, 0, HardLinkCGWin.getLinkCountCommand.length);
            buf[HardLinkCGWin.getLinkCountCommand.length - 1] = file.getCanonicalPath();
            return buf;
        }
        
        static {
            HardLinkCGWin.getLinkCountCommand = new String[] { Shell.WINUTILS, "hardlink", "stat", null };
        }
    }
    
    public static class LinkStats
    {
        public int countDirs;
        public int countSingleLinks;
        public int countMultLinks;
        public int countFilesMultLinks;
        public int countEmptyDirs;
        public int countPhysicalFileCopies;
        
        public LinkStats() {
            this.countDirs = 0;
            this.countSingleLinks = 0;
            this.countMultLinks = 0;
            this.countFilesMultLinks = 0;
            this.countEmptyDirs = 0;
            this.countPhysicalFileCopies = 0;
        }
        
        public void clear() {
            this.countDirs = 0;
            this.countSingleLinks = 0;
            this.countMultLinks = 0;
            this.countFilesMultLinks = 0;
            this.countEmptyDirs = 0;
            this.countPhysicalFileCopies = 0;
        }
        
        public String report() {
            return "HardLinkStats: " + this.countDirs + " Directories, including " + this.countEmptyDirs + " Empty Directories, " + this.countSingleLinks + " single Link operations, " + this.countMultLinks + " multi-Link operations, linking " + this.countFilesMultLinks + " files, total " + (this.countSingleLinks + this.countFilesMultLinks) + " linkable files.  Also physically copied " + this.countPhysicalFileCopies + " other files.";
        }
    }
}
