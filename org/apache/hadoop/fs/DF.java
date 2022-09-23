// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.fs;

import java.util.Iterator;
import com.google.common.annotations.VisibleForTesting;
import java.util.NoSuchElementException;
import java.util.StringTokenizer;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import org.apache.hadoop.conf.Configuration;
import java.util.ArrayList;
import java.io.File;
import org.apache.hadoop.classification.InterfaceStability;
import org.apache.hadoop.classification.InterfaceAudience;
import org.apache.hadoop.util.Shell;

@InterfaceAudience.LimitedPrivate({ "HDFS", "MapReduce" })
@InterfaceStability.Evolving
public class DF extends Shell
{
    private final String dirPath;
    private final File dirFile;
    private String filesystem;
    private String mount;
    private ArrayList<String> output;
    
    public DF(final File path, final Configuration conf) throws IOException {
        this(path, conf.getLong("fs.df.interval", 60000L));
    }
    
    public DF(final File path, final long dfInterval) throws IOException {
        super(dfInterval);
        this.dirPath = path.getCanonicalPath();
        this.dirFile = new File(this.dirPath);
        this.output = new ArrayList<String>();
    }
    
    public String getDirPath() {
        return this.dirPath;
    }
    
    public String getFilesystem() throws IOException {
        if (Shell.WINDOWS) {
            return this.filesystem = this.dirFile.getCanonicalPath().substring(0, 2);
        }
        this.run();
        this.verifyExitCode();
        this.parseOutput();
        return this.filesystem;
    }
    
    public long getCapacity() {
        return this.dirFile.getTotalSpace();
    }
    
    public long getUsed() {
        return this.dirFile.getTotalSpace() - this.dirFile.getFreeSpace();
    }
    
    public long getAvailable() {
        return this.dirFile.getUsableSpace();
    }
    
    public int getPercentUsed() {
        final double cap = (double)this.getCapacity();
        final double used = cap - this.getAvailable();
        return (int)(used * 100.0 / cap);
    }
    
    public String getMount() throws IOException {
        if (!this.dirFile.exists()) {
            throw new FileNotFoundException("Specified path " + this.dirFile.getPath() + "does not exist");
        }
        if (Shell.WINDOWS) {
            this.mount = this.dirFile.getCanonicalPath().substring(0, 2);
        }
        else {
            this.run();
            this.verifyExitCode();
            this.parseOutput();
        }
        return this.mount;
    }
    
    @Override
    public String toString() {
        return "df -k " + this.mount + "\n" + this.filesystem + "\t" + this.getCapacity() / 1024L + "\t" + this.getUsed() / 1024L + "\t" + this.getAvailable() / 1024L + "\t" + this.getPercentUsed() + "%\t" + this.mount;
    }
    
    @Override
    protected String[] getExecString() {
        if (Shell.WINDOWS) {
            throw new AssertionError((Object)"DF.getExecString() should never be called on Windows");
        }
        return new String[] { "bash", "-c", "exec 'df' '-k' '-P' '" + this.dirPath + "' 2>/dev/null" };
    }
    
    @Override
    protected void parseExecResult(final BufferedReader lines) throws IOException {
        this.output.clear();
        for (String line = lines.readLine(); line != null; line = lines.readLine()) {
            this.output.add(line);
        }
    }
    
    @VisibleForTesting
    protected void parseOutput() throws IOException {
        if (this.output.size() < 2) {
            final StringBuffer sb = new StringBuffer("Fewer lines of output than expected");
            if (this.output.size() > 0) {
                sb.append(": " + this.output.get(0));
            }
            throw new IOException(sb.toString());
        }
        String line = this.output.get(1);
        StringTokenizer tokens = new StringTokenizer(line, " \t\n\r\f%");
        try {
            this.filesystem = tokens.nextToken();
        }
        catch (NoSuchElementException e) {
            throw new IOException("Unexpected empty line");
        }
        if (!tokens.hasMoreTokens()) {
            if (this.output.size() <= 2) {
                throw new IOException("Expecting additional output after line: " + line);
            }
            line = this.output.get(2);
            tokens = new StringTokenizer(line, " \t\n\r\f%");
        }
        try {
            Long.parseLong(tokens.nextToken());
            Long.parseLong(tokens.nextToken());
            Long.parseLong(tokens.nextToken());
            Integer.parseInt(tokens.nextToken());
            this.mount = tokens.nextToken();
        }
        catch (NoSuchElementException e) {
            throw new IOException("Could not parse line: " + line);
        }
        catch (NumberFormatException e2) {
            throw new IOException("Could not parse line: " + line);
        }
    }
    
    private void verifyExitCode() throws IOException {
        if (this.getExitCode() != 0) {
            final StringBuilder sb = new StringBuilder("df could not be run successfully: ");
            for (final String line : this.output) {
                sb.append(line);
            }
            throw new IOException(sb.toString());
        }
    }
    
    public static void main(final String[] args) throws Exception {
        String path = ".";
        if (args.length > 0) {
            path = args[0];
        }
        System.out.println(new DF(new File(path), 60000L).toString());
    }
}
