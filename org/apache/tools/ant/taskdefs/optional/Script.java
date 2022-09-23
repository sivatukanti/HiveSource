// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.tools.ant.taskdefs.optional;

import org.apache.tools.ant.types.Reference;
import org.apache.tools.ant.types.Path;
import java.io.File;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.ProjectComponent;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.util.ScriptRunnerHelper;
import org.apache.tools.ant.Task;

public class Script extends Task
{
    private ScriptRunnerHelper helper;
    
    public Script() {
        this.helper = new ScriptRunnerHelper();
    }
    
    @Override
    public void setProject(final Project project) {
        super.setProject(project);
        this.helper.setProjectComponent(this);
    }
    
    @Override
    public void execute() throws BuildException {
        this.helper.getScriptRunner().executeScript("ANT");
    }
    
    public void setManager(final String manager) {
        this.helper.setManager(manager);
    }
    
    public void setLanguage(final String language) {
        this.helper.setLanguage(language);
    }
    
    public void setSrc(final String fileName) {
        this.helper.setSrc(new File(fileName));
    }
    
    public void addText(final String text) {
        this.helper.addText(text);
    }
    
    public void setClasspath(final Path classpath) {
        this.helper.setClasspath(classpath);
    }
    
    public Path createClasspath() {
        return this.helper.createClasspath();
    }
    
    public void setClasspathRef(final Reference r) {
        this.helper.setClasspathRef(r);
    }
    
    public void setSetBeans(final boolean setBeans) {
        this.helper.setSetBeans(setBeans);
    }
}
