// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.tools.ant.types;

import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Enumeration;
import java.util.Vector;
import java.util.Properties;
import org.apache.tools.ant.BuildException;
import java.util.ListIterator;
import java.util.List;
import java.util.LinkedList;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.util.JavaEnvUtils;

public class CommandlineJava implements Cloneable
{
    private Commandline vmCommand;
    private Commandline javaCommand;
    private SysProperties sysProperties;
    private Path classpath;
    private Path bootclasspath;
    private String vmVersion;
    private String maxMemory;
    private Assertions assertions;
    private boolean executeJar;
    private boolean cloneVm;
    
    public CommandlineJava() {
        this.vmCommand = new Commandline();
        this.javaCommand = new Commandline();
        this.sysProperties = new SysProperties();
        this.classpath = null;
        this.bootclasspath = null;
        this.maxMemory = null;
        this.assertions = null;
        this.executeJar = false;
        this.cloneVm = false;
        this.setVm(JavaEnvUtils.getJreExecutable("java"));
        this.setVmversion(JavaEnvUtils.getJavaVersion());
    }
    
    public Commandline.Argument createArgument() {
        return this.javaCommand.createArgument();
    }
    
    public Commandline.Argument createVmArgument() {
        return this.vmCommand.createArgument();
    }
    
    public void addSysproperty(final Environment.Variable sysp) {
        this.sysProperties.addVariable(sysp);
    }
    
    public void addSyspropertyset(final PropertySet sysp) {
        this.sysProperties.addSyspropertyset(sysp);
    }
    
    public void addSysproperties(final SysProperties sysp) {
        this.sysProperties.addSysproperties(sysp);
    }
    
    public void setVm(final String vm) {
        this.vmCommand.setExecutable(vm);
    }
    
    public void setVmversion(final String value) {
        this.vmVersion = value;
    }
    
    public void setCloneVm(final boolean cloneVm) {
        this.cloneVm = cloneVm;
    }
    
    public Assertions getAssertions() {
        return this.assertions;
    }
    
    public void setAssertions(final Assertions assertions) {
        this.assertions = assertions;
    }
    
    public void setJar(final String jarpathname) {
        this.javaCommand.setExecutable(jarpathname);
        this.executeJar = true;
    }
    
    public String getJar() {
        if (this.executeJar) {
            return this.javaCommand.getExecutable();
        }
        return null;
    }
    
    public void setClassname(final String classname) {
        this.javaCommand.setExecutable(classname);
        this.executeJar = false;
    }
    
    public String getClassname() {
        if (!this.executeJar) {
            return this.javaCommand.getExecutable();
        }
        return null;
    }
    
    public Path createClasspath(final Project p) {
        if (this.classpath == null) {
            this.classpath = new Path(p);
        }
        return this.classpath;
    }
    
    public Path createBootclasspath(final Project p) {
        if (this.bootclasspath == null) {
            this.bootclasspath = new Path(p);
        }
        return this.bootclasspath;
    }
    
    public String getVmversion() {
        return this.vmVersion;
    }
    
    public String[] getCommandline() {
        final List<String> commands = new LinkedList<String>();
        this.addCommandsToList(commands.listIterator());
        return commands.toArray(new String[commands.size()]);
    }
    
    private void addCommandsToList(final ListIterator<String> listIterator) {
        this.getActualVMCommand().addCommandToList(listIterator);
        this.sysProperties.addDefinitionsToList(listIterator);
        if (this.isCloneVm()) {
            final SysProperties clonedSysProperties = new SysProperties();
            final PropertySet ps = new PropertySet();
            final PropertySet.BuiltinPropertySetName sys = new PropertySet.BuiltinPropertySetName();
            sys.setValue("system");
            ps.appendBuiltin(sys);
            clonedSysProperties.addSyspropertyset(ps);
            clonedSysProperties.addDefinitionsToList(listIterator);
        }
        final Path bcp = this.calculateBootclasspath(true);
        if (bcp.size() > 0) {
            listIterator.add("-Xbootclasspath:" + bcp.toString());
        }
        if (this.haveClasspath()) {
            listIterator.add("-classpath");
            listIterator.add(this.classpath.concatSystemClasspath("ignore").toString());
        }
        if (this.getAssertions() != null) {
            this.getAssertions().applyAssertions(listIterator);
        }
        if (this.executeJar) {
            listIterator.add("-jar");
        }
        this.javaCommand.addCommandToList(listIterator);
    }
    
    public void setMaxmemory(final String max) {
        this.maxMemory = max;
    }
    
    @Override
    public String toString() {
        return Commandline.toString(this.getCommandline());
    }
    
    public String describeCommand() {
        return Commandline.describeCommand(this.getCommandline());
    }
    
    public String describeJavaCommand() {
        return Commandline.describeCommand(this.getJavaCommand());
    }
    
    protected Commandline getActualVMCommand() {
        final Commandline actualVMCommand = (Commandline)this.vmCommand.clone();
        if (this.maxMemory != null) {
            if (this.vmVersion.startsWith("1.1")) {
                actualVMCommand.createArgument().setValue("-mx" + this.maxMemory);
            }
            else {
                actualVMCommand.createArgument().setValue("-Xmx" + this.maxMemory);
            }
        }
        return actualVMCommand;
    }
    
    @Deprecated
    public int size() {
        int size = this.getActualVMCommand().size() + this.javaCommand.size() + this.sysProperties.size();
        if (this.isCloneVm()) {
            size += System.getProperties().size();
        }
        if (this.haveClasspath()) {
            size += 2;
        }
        if (this.calculateBootclasspath(true).size() > 0) {
            ++size;
        }
        if (this.executeJar) {
            ++size;
        }
        if (this.getAssertions() != null) {
            size += this.getAssertions().size();
        }
        return size;
    }
    
    public Commandline getJavaCommand() {
        return this.javaCommand;
    }
    
    public Commandline getVmCommand() {
        return this.getActualVMCommand();
    }
    
    public Path getClasspath() {
        return this.classpath;
    }
    
    public Path getBootclasspath() {
        return this.bootclasspath;
    }
    
    public void setSystemProperties() throws BuildException {
        this.sysProperties.setSystem();
    }
    
    public void restoreSystemProperties() throws BuildException {
        this.sysProperties.restoreSystem();
    }
    
    public SysProperties getSystemProperties() {
        return this.sysProperties;
    }
    
    public Object clone() throws CloneNotSupportedException {
        try {
            final CommandlineJava c = (CommandlineJava)super.clone();
            c.vmCommand = (Commandline)this.vmCommand.clone();
            c.javaCommand = (Commandline)this.javaCommand.clone();
            c.sysProperties = (SysProperties)this.sysProperties.clone();
            if (this.classpath != null) {
                c.classpath = (Path)this.classpath.clone();
            }
            if (this.bootclasspath != null) {
                c.bootclasspath = (Path)this.bootclasspath.clone();
            }
            if (this.assertions != null) {
                c.assertions = (Assertions)this.assertions.clone();
            }
            return c;
        }
        catch (CloneNotSupportedException e) {
            throw new BuildException(e);
        }
    }
    
    public void clearJavaArgs() {
        this.javaCommand.clearArgs();
    }
    
    public boolean haveClasspath() {
        final Path fullClasspath = (this.classpath != null) ? this.classpath.concatSystemClasspath("ignore") : null;
        return fullClasspath != null && fullClasspath.toString().trim().length() > 0;
    }
    
    protected boolean haveBootclasspath(final boolean log) {
        return this.calculateBootclasspath(log).size() > 0;
    }
    
    private Path calculateBootclasspath(final boolean log) {
        if (this.vmVersion.startsWith("1.1")) {
            if (this.bootclasspath != null && log) {
                this.bootclasspath.log("Ignoring bootclasspath as the target VM doesn't support it.");
            }
            return new Path(null);
        }
        Path b = this.bootclasspath;
        if (b == null) {
            b = new Path(null);
        }
        return b.concatSystemBootClasspath(this.isCloneVm() ? "last" : "ignore");
    }
    
    private boolean isCloneVm() {
        return this.cloneVm || "true".equals(System.getProperty("ant.build.clonevm"));
    }
    
    public static class SysProperties extends Environment implements Cloneable
    {
        Properties sys;
        private Vector<PropertySet> propertySets;
        
        public SysProperties() {
            this.sys = null;
            this.propertySets = new Vector<PropertySet>();
        }
        
        @Override
        public String[] getVariables() throws BuildException {
            final List<String> definitions = new LinkedList<String>();
            this.addDefinitionsToList(definitions.listIterator());
            if (definitions.size() == 0) {
                return null;
            }
            return definitions.toArray(new String[definitions.size()]);
        }
        
        public void addDefinitionsToList(final ListIterator<String> listIt) {
            final String[] props = super.getVariables();
            if (props != null) {
                for (int i = 0; i < props.length; ++i) {
                    listIt.add("-D" + props[i]);
                }
            }
            final Properties propertySetProperties = this.mergePropertySets();
            final Enumeration<?> e = propertySetProperties.keys();
            while (e.hasMoreElements()) {
                final String key = (String)e.nextElement();
                final String value = propertySetProperties.getProperty(key);
                listIt.add("-D" + key + "=" + value);
            }
        }
        
        public int size() {
            final Properties p = this.mergePropertySets();
            return this.variables.size() + p.size();
        }
        
        public void setSystem() throws BuildException {
            try {
                this.sys = System.getProperties();
                final Properties p = new Properties();
                final Enumeration<?> e = this.sys.propertyNames();
                while (e.hasMoreElements()) {
                    final String name = (String)e.nextElement();
                    final String value = this.sys.getProperty(name);
                    if (name != null && value != null) {
                        p.put(name, value);
                    }
                }
                p.putAll(this.mergePropertySets());
                for (final Variable v : this.variables) {
                    v.validate();
                    p.put(v.getKey(), v.getValue());
                }
                System.setProperties(p);
            }
            catch (SecurityException e2) {
                throw new BuildException("Cannot modify system properties", e2);
            }
        }
        
        public void restoreSystem() throws BuildException {
            if (this.sys == null) {
                throw new BuildException("Unbalanced nesting of SysProperties");
            }
            try {
                System.setProperties(this.sys);
                this.sys = null;
            }
            catch (SecurityException e) {
                throw new BuildException("Cannot modify system properties", e);
            }
        }
        
        public Object clone() throws CloneNotSupportedException {
            try {
                final SysProperties c = (SysProperties)super.clone();
                c.variables = (Vector<Variable>)this.variables.clone();
                c.propertySets = (Vector<PropertySet>)this.propertySets.clone();
                return c;
            }
            catch (CloneNotSupportedException e) {
                return null;
            }
        }
        
        public void addSyspropertyset(final PropertySet ps) {
            this.propertySets.addElement(ps);
        }
        
        public void addSysproperties(final SysProperties ps) {
            this.variables.addAll(ps.variables);
            this.propertySets.addAll(ps.propertySets);
        }
        
        private Properties mergePropertySets() {
            final Properties p = new Properties();
            for (final PropertySet ps : this.propertySets) {
                p.putAll(ps.getProperties());
            }
            return p;
        }
    }
}
