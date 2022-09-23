// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.tools.ant.types.optional;

import org.apache.tools.ant.types.Reference;
import org.apache.tools.ant.types.Path;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.ProjectComponent;
import org.apache.tools.ant.Project;
import java.io.File;
import org.apache.tools.ant.util.ScriptRunnerBase;
import org.apache.tools.ant.util.ScriptRunnerHelper;
import org.apache.tools.ant.types.selectors.BaseSelector;

public class ScriptSelector extends BaseSelector
{
    private ScriptRunnerHelper helper;
    private ScriptRunnerBase runner;
    private File basedir;
    private String filename;
    private File file;
    private boolean selected;
    
    public ScriptSelector() {
        this.helper = new ScriptRunnerHelper();
    }
    
    @Override
    public void setProject(final Project project) {
        super.setProject(project);
        this.helper.setProjectComponent(this);
    }
    
    public void setManager(final String manager) {
        this.helper.setManager(manager);
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
    
    public void setSrc(final File file) {
        this.helper.setSrc(file);
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
    
    @Override
    public boolean isSelected(final File basedir, final String filename, final File file) {
        this.init();
        this.setSelected(true);
        this.file = file;
        this.basedir = basedir;
        this.filename = filename;
        this.runner.addBean("basedir", basedir);
        this.runner.addBean("filename", filename);
        this.runner.addBean("file", file);
        this.runner.executeScript("ant_selector");
        return this.isSelected();
    }
    
    public File getBasedir() {
        return this.basedir;
    }
    
    public String getFilename() {
        return this.filename;
    }
    
    public File getFile() {
        return this.file;
    }
    
    public boolean isSelected() {
        return this.selected;
    }
    
    public void setSelected(final boolean selected) {
        this.selected = selected;
    }
}
