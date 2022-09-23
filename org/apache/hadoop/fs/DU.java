// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.fs;

import java.io.BufferedReader;
import org.apache.hadoop.util.Shell;
import org.apache.hadoop.conf.Configuration;
import java.io.IOException;
import com.google.common.annotations.VisibleForTesting;
import java.io.File;
import org.apache.hadoop.classification.InterfaceStability;
import org.apache.hadoop.classification.InterfaceAudience;

@InterfaceAudience.LimitedPrivate({ "HDFS", "MapReduce" })
@InterfaceStability.Evolving
public class DU extends CachingGetSpaceUsed
{
    private final DUShell duShell;
    
    @VisibleForTesting
    public DU(final File path, final long interval, final long jitter, final long initialUsed) throws IOException {
        super(path, interval, jitter, initialUsed);
        this.duShell = new DUShell();
    }
    
    public DU(final GetSpaceUsed.Builder builder) throws IOException {
        this(builder.getPath(), builder.getInterval(), builder.getJitter(), builder.getInitialUsed());
    }
    
    @Override
    protected synchronized void refresh() {
        try {
            this.duShell.startRefresh();
        }
        catch (IOException ioe) {
            DU.LOG.warn("Could not get disk usage information for path {}", this.getDirPath(), ioe);
        }
    }
    
    public static void main(final String[] args) throws Exception {
        String path = ".";
        if (args.length > 0) {
            path = args[0];
        }
        final GetSpaceUsed du = new GetSpaceUsed.Builder().setPath(new File(path)).setConf(new Configuration()).build();
        final String duResult = du.toString();
        System.out.println(duResult);
    }
    
    private final class DUShell extends Shell
    {
        void startRefresh() throws IOException {
            this.run();
        }
        
        @Override
        public String toString() {
            return "du -sk " + DU.this.getDirPath() + "\n" + DU.this.used.get() + "\t" + DU.this.getDirPath();
        }
        
        @Override
        protected String[] getExecString() {
            return new String[] { "du", "-sk", DU.this.getDirPath() };
        }
        
        @Override
        protected void parseExecResult(final BufferedReader lines) throws IOException {
            final String line = lines.readLine();
            if (line == null) {
                throw new IOException("Expecting a line not the end of stream");
            }
            final String[] tokens = line.split("\t");
            if (tokens.length == 0) {
                throw new IOException("Illegal du output");
            }
            DU.this.setUsed(Long.parseLong(tokens[0]) * 1024L);
        }
    }
}
