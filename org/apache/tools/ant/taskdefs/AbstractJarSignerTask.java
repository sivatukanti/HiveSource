// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.tools.ant.taskdefs;

import org.apache.tools.ant.types.ResourceCollection;
import org.apache.tools.ant.util.JavaEnvUtils;
import org.apache.tools.ant.BuildException;
import java.util.Iterator;
import org.apache.tools.ant.types.RegularExpression;
import org.apache.tools.ant.filters.LineContainsRegExp;
import org.apache.tools.ant.types.Path;
import org.apache.tools.ant.types.Environment;
import org.apache.tools.ant.types.RedirectorElement;
import org.apache.tools.ant.types.FileSet;
import java.util.Vector;
import java.io.File;
import org.apache.tools.ant.Task;

public abstract class AbstractJarSignerTask extends Task
{
    protected File jar;
    protected String alias;
    protected String keystore;
    protected String storepass;
    protected String storetype;
    protected String keypass;
    protected boolean verbose;
    protected boolean strict;
    protected String maxMemory;
    protected Vector<FileSet> filesets;
    protected static final String JARSIGNER_COMMAND = "jarsigner";
    private RedirectorElement redirector;
    private Environment sysProperties;
    public static final String ERROR_NO_SOURCE = "jar must be set through jar attribute or nested filesets";
    private Path path;
    private String executable;
    
    public AbstractJarSignerTask() {
        this.strict = false;
        this.filesets = new Vector<FileSet>();
        this.sysProperties = new Environment();
        this.path = null;
    }
    
    public void setMaxmemory(final String max) {
        this.maxMemory = max;
    }
    
    public void setJar(final File jar) {
        this.jar = jar;
    }
    
    public void setAlias(final String alias) {
        this.alias = alias;
    }
    
    public void setKeystore(final String keystore) {
        this.keystore = keystore;
    }
    
    public void setStorepass(final String storepass) {
        this.storepass = storepass;
    }
    
    public void setStoretype(final String storetype) {
        this.storetype = storetype;
    }
    
    public void setKeypass(final String keypass) {
        this.keypass = keypass;
    }
    
    public void setVerbose(final boolean verbose) {
        this.verbose = verbose;
    }
    
    public void setStrict(final boolean strict) {
        this.strict = strict;
    }
    
    public void addFileset(final FileSet set) {
        this.filesets.addElement(set);
    }
    
    public void addSysproperty(final Environment.Variable sysp) {
        this.sysProperties.addVariable(sysp);
    }
    
    public Path createPath() {
        if (this.path == null) {
            this.path = new Path(this.getProject());
        }
        return this.path.createPath();
    }
    
    protected void beginExecution() {
        this.redirector = this.createRedirector();
    }
    
    protected void endExecution() {
        this.redirector = null;
    }
    
    private RedirectorElement createRedirector() {
        final RedirectorElement result = new RedirectorElement();
        if (this.storepass != null) {
            final StringBuffer input = new StringBuffer(this.storepass).append('\n');
            if (this.keypass != null) {
                input.append(this.keypass).append('\n');
            }
            result.setInputString(input.toString());
            result.setLogInputString(false);
            final LineContainsRegExp filter = new LineContainsRegExp();
            final RegularExpression rx = new RegularExpression();
            rx.setPattern("^(Enter Passphrase for keystore: |Enter key password for .+: )$");
            filter.addConfiguredRegexp(rx);
            filter.setNegate(true);
            result.createErrorFilterChain().addLineContainsRegExp(filter);
        }
        return result;
    }
    
    public RedirectorElement getRedirector() {
        return this.redirector;
    }
    
    public void setExecutable(final String executable) {
        this.executable = executable;
    }
    
    protected void setCommonOptions(final ExecTask cmd) {
        if (this.maxMemory != null) {
            this.addValue(cmd, "-J-Xmx" + this.maxMemory);
        }
        if (this.verbose) {
            this.addValue(cmd, "-verbose");
        }
        if (this.strict) {
            this.addValue(cmd, "-strict");
        }
        for (final Environment.Variable variable : this.sysProperties.getVariablesVector()) {
            this.declareSysProperty(cmd, variable);
        }
    }
    
    protected void declareSysProperty(final ExecTask cmd, final Environment.Variable property) throws BuildException {
        this.addValue(cmd, "-J-D" + property.getContent());
    }
    
    protected void bindToKeystore(final ExecTask cmd) {
        if (null != this.keystore) {
            this.addValue(cmd, "-keystore");
            final File keystoreFile = this.getProject().resolveFile(this.keystore);
            String loc;
            if (keystoreFile.exists()) {
                loc = keystoreFile.getPath();
            }
            else {
                loc = this.keystore;
            }
            this.addValue(cmd, loc);
        }
        if (null != this.storetype) {
            this.addValue(cmd, "-storetype");
            this.addValue(cmd, this.storetype);
        }
    }
    
    protected ExecTask createJarSigner() {
        final ExecTask cmd = new ExecTask(this);
        if (this.executable == null) {
            cmd.setExecutable(JavaEnvUtils.getJdkExecutable("jarsigner"));
        }
        else {
            cmd.setExecutable(this.executable);
        }
        cmd.setTaskType("jarsigner");
        cmd.setFailonerror(true);
        cmd.addConfiguredRedirector(this.redirector);
        return cmd;
    }
    
    protected Vector<FileSet> createUnifiedSources() {
        final Vector<FileSet> sources = (Vector<FileSet>)this.filesets.clone();
        if (this.jar != null) {
            final FileSet sourceJar = new FileSet();
            sourceJar.setProject(this.getProject());
            sourceJar.setFile(this.jar);
            sourceJar.setDir(this.jar.getParentFile());
            sources.add(sourceJar);
        }
        return sources;
    }
    
    protected Path createUnifiedSourcePath() {
        final Path p = (Path)((this.path == null) ? new Path(this.getProject()) : this.path.clone());
        for (final FileSet fileSet : this.createUnifiedSources()) {
            p.add(fileSet);
        }
        return p;
    }
    
    protected boolean hasResources() {
        return this.path != null || this.filesets.size() > 0;
    }
    
    protected void addValue(final ExecTask cmd, final String value) {
        cmd.createArg().setValue(value);
    }
}
