// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.fs.shell.find;

import java.util.Date;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.shell.CommandFactory;
import java.io.InputStream;
import java.io.PrintStream;

public class FindOptions
{
    private PrintStream out;
    private PrintStream err;
    private InputStream in;
    private boolean depthFirst;
    private boolean followLink;
    private boolean followArgLink;
    private long startTime;
    private int minDepth;
    private int maxDepth;
    private CommandFactory commandFactory;
    private Configuration configuration;
    
    public FindOptions() {
        this.depthFirst = false;
        this.followLink = false;
        this.followArgLink = false;
        this.startTime = new Date().getTime();
        this.minDepth = 0;
        this.maxDepth = Integer.MAX_VALUE;
        this.configuration = new Configuration();
    }
    
    public void setOut(final PrintStream out) {
        this.out = out;
    }
    
    public PrintStream getOut() {
        return this.out;
    }
    
    public void setErr(final PrintStream err) {
        this.err = err;
    }
    
    public PrintStream getErr() {
        return this.err;
    }
    
    public void setIn(final InputStream in) {
        this.in = in;
    }
    
    public InputStream getIn() {
        return this.in;
    }
    
    public void setDepthFirst(final boolean depthFirst) {
        this.depthFirst = depthFirst;
    }
    
    public boolean isDepthFirst() {
        return this.depthFirst;
    }
    
    public void setFollowLink(final boolean followLink) {
        this.followLink = followLink;
    }
    
    public boolean isFollowLink() {
        return this.followLink;
    }
    
    public void setFollowArgLink(final boolean followArgLink) {
        this.followArgLink = followArgLink;
    }
    
    public boolean isFollowArgLink() {
        return this.followArgLink;
    }
    
    public long getStartTime() {
        return this.startTime;
    }
    
    public void setStartTime(final long time) {
        this.startTime = time;
    }
    
    public int getMinDepth() {
        return this.minDepth;
    }
    
    public void setMinDepth(final int minDepth) {
        this.minDepth = minDepth;
    }
    
    public int getMaxDepth() {
        return this.maxDepth;
    }
    
    public void setMaxDepth(final int maxDepth) {
        this.maxDepth = maxDepth;
    }
    
    public void setCommandFactory(final CommandFactory factory) {
        this.commandFactory = factory;
    }
    
    public CommandFactory getCommandFactory() {
        return this.commandFactory;
    }
    
    public void setConfiguration(final Configuration configuration) {
        this.configuration = configuration;
    }
    
    public Configuration getConfiguration() {
        return this.configuration;
    }
}
