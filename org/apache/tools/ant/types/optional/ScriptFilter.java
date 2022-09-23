// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.tools.ant.types.optional;

import org.apache.tools.ant.types.Reference;
import org.apache.tools.ant.types.Path;
import java.io.File;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.ProjectComponent;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.util.ScriptRunnerBase;
import org.apache.tools.ant.util.ScriptRunnerHelper;
import org.apache.tools.ant.filters.TokenFilter;

public class ScriptFilter extends TokenFilter.ChainableReaderFilter
{
    private ScriptRunnerHelper helper;
    private ScriptRunnerBase runner;
    private String token;
    
    public ScriptFilter() {
        this.helper = new ScriptRunnerHelper();
        this.runner = null;
    }
    
    @Override
    public void setProject(final Project project) {
        super.setProject(project);
        this.helper.setProjectComponent(this);
    }
    
    public void setLanguage(final String language) {
        this.helper.setLanguage(language);
    }
    
    private void init() throws BuildException {
        if (this.runner != null) {
            return;
        }
        this.runner = this.helper.getScriptRunner();
    }
    
    public void setToken(final String token) {
        this.token = token;
    }
    
    public String getToken() {
        return this.token;
    }
    
    public String filter(final String token) {
        this.init();
        this.setToken(token);
        this.runner.executeScript("ant_filter");
        return this.getToken();
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
