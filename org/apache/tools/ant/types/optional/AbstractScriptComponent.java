// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.tools.ant.types.optional;

import org.apache.tools.ant.types.Reference;
import org.apache.tools.ant.types.Path;
import java.io.File;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.util.ScriptRunnerBase;
import org.apache.tools.ant.util.ScriptRunnerHelper;
import org.apache.tools.ant.ProjectComponent;

public abstract class AbstractScriptComponent extends ProjectComponent
{
    private ScriptRunnerHelper helper;
    private ScriptRunnerBase runner;
    
    public AbstractScriptComponent() {
        this.helper = new ScriptRunnerHelper();
        this.runner = null;
    }
    
    @Override
    public void setProject(final Project project) {
        super.setProject(project);
        this.helper.setProjectComponent(this);
    }
    
    public ScriptRunnerBase getRunner() {
        this.initScriptRunner();
        return this.runner;
    }
    
    public void setSrc(final File file) {
        this.helper.setSrc(file);
    }
    
    public void addText(final String text) {
        this.helper.addText(text);
    }
    
    public void setManager(final String manager) {
        this.helper.setManager(manager);
    }
    
    public void setLanguage(final String language) {
        this.helper.setLanguage(language);
    }
    
    protected void initScriptRunner() {
        if (this.runner != null) {
            return;
        }
        this.helper.setProjectComponent(this);
        this.runner = this.helper.getScriptRunner();
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
    
    protected void executeScript(final String execName) {
        this.getRunner().executeScript(execName);
    }
    
    public void setSetBeans(final boolean setBeans) {
        this.helper.setSetBeans(setBeans);
    }
}
