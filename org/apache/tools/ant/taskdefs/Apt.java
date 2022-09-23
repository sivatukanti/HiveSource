// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.tools.ant.taskdefs;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.types.Reference;
import org.apache.tools.ant.util.JavaEnvUtils;
import org.apache.tools.ant.taskdefs.compilers.AptExternalCompilerAdapter;
import java.io.File;
import java.util.Vector;
import org.apache.tools.ant.types.Path;

public class Apt extends Javac
{
    private boolean compile;
    private String factory;
    private Path factoryPath;
    private Vector<Option> options;
    private File preprocessDir;
    public static final String EXECUTABLE_NAME = "apt";
    public static final String ERROR_IGNORING_COMPILER_OPTION = "Ignoring compiler attribute for the APT task, as it is fixed";
    public static final String ERROR_WRONG_JAVA_VERSION = "Apt task requires Java 1.5+";
    public static final String WARNING_IGNORING_FORK = "Apt only runs in its own JVM; fork=false option ignored";
    
    public Apt() {
        this.compile = true;
        this.options = new Vector<Option>();
        super.setCompiler(AptExternalCompilerAdapter.class.getName());
        super.setFork(true);
    }
    
    public String getAptExecutable() {
        final String exe = this.getExecutable();
        return (exe != null) ? exe : JavaEnvUtils.getJdkExecutable("apt");
    }
    
    @Override
    public void setCompiler(final String compiler) {
        this.log("Ignoring compiler attribute for the APT task, as it is fixed", 1);
    }
    
    @Override
    public void setFork(final boolean fork) {
        if (!fork) {
            this.log("Apt only runs in its own JVM; fork=false option ignored", 1);
        }
    }
    
    @Override
    public String getCompiler() {
        return super.getCompiler();
    }
    
    public boolean isCompile() {
        return this.compile;
    }
    
    public void setCompile(final boolean compile) {
        this.compile = compile;
    }
    
    public String getFactory() {
        return this.factory;
    }
    
    public void setFactory(final String factory) {
        this.factory = factory;
    }
    
    public void setFactoryPathRef(final Reference ref) {
        this.createFactoryPath().setRefid(ref);
    }
    
    public Path createFactoryPath() {
        if (this.factoryPath == null) {
            this.factoryPath = new Path(this.getProject());
        }
        return this.factoryPath.createPath();
    }
    
    public Path getFactoryPath() {
        return this.factoryPath;
    }
    
    public Option createOption() {
        final Option opt = new Option();
        this.options.add(opt);
        return opt;
    }
    
    public Vector<Option> getOptions() {
        return this.options;
    }
    
    public File getPreprocessDir() {
        return this.preprocessDir;
    }
    
    public void setPreprocessDir(final File preprocessDir) {
        this.preprocessDir = preprocessDir;
    }
    
    @Override
    public void execute() throws BuildException {
        super.execute();
    }
    
    public static final class Option
    {
        private String name;
        private String value;
        
        public String getName() {
            return this.name;
        }
        
        public void setName(final String name) {
            this.name = name;
        }
        
        public String getValue() {
            return this.value;
        }
        
        public void setValue(final String value) {
            this.value = value;
        }
    }
}
