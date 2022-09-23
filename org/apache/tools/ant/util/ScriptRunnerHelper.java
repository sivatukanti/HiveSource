// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.tools.ant.util;

import org.apache.tools.ant.types.Reference;
import org.apache.tools.ant.types.Path;
import org.apache.tools.ant.types.ResourceCollection;
import org.apache.tools.ant.types.resources.Union;
import org.apache.tools.ant.ProjectComponent;
import java.io.File;

public class ScriptRunnerHelper
{
    private ClasspathUtils.Delegate cpDelegate;
    private File srcFile;
    private String manager;
    private String language;
    private String text;
    private boolean setBeans;
    private ProjectComponent projectComponent;
    private ClassLoader scriptLoader;
    private Union resources;
    
    public ScriptRunnerHelper() {
        this.cpDelegate = null;
        this.manager = "auto";
        this.setBeans = true;
        this.scriptLoader = null;
        this.resources = new Union();
    }
    
    public void setProjectComponent(final ProjectComponent component) {
        this.projectComponent = component;
    }
    
    public ScriptRunnerBase getScriptRunner() {
        final ScriptRunnerBase runner = this.getRunner();
        if (this.srcFile != null) {
            runner.setSrc(this.srcFile);
        }
        if (this.text != null) {
            runner.addText(this.text);
        }
        if (this.resources != null) {
            runner.loadResources(this.resources);
        }
        if (this.setBeans) {
            runner.bindToComponent(this.projectComponent);
        }
        else {
            runner.bindToComponentMinimum(this.projectComponent);
        }
        return runner;
    }
    
    public Path createClasspath() {
        return this.getClassPathDelegate().createClasspath();
    }
    
    public void setClasspath(final Path classpath) {
        this.getClassPathDelegate().setClasspath(classpath);
    }
    
    public void setClasspathRef(final Reference r) {
        this.getClassPathDelegate().setClasspathref(r);
    }
    
    public void setSrc(final File file) {
        this.srcFile = file;
    }
    
    public void addText(final String text) {
        this.text = text;
    }
    
    public void setManager(final String manager) {
        this.manager = manager;
    }
    
    public void setLanguage(final String language) {
        this.language = language;
    }
    
    public String getLanguage() {
        return this.language;
    }
    
    public void setSetBeans(final boolean setBeans) {
        this.setBeans = setBeans;
    }
    
    public void setClassLoader(final ClassLoader loader) {
        this.scriptLoader = loader;
    }
    
    private synchronized ClassLoader generateClassLoader() {
        if (this.scriptLoader != null) {
            return this.scriptLoader;
        }
        if (this.cpDelegate == null) {
            return this.scriptLoader = this.getClass().getClassLoader();
        }
        return this.scriptLoader = this.cpDelegate.getClassLoader();
    }
    
    private ClasspathUtils.Delegate getClassPathDelegate() {
        if (this.cpDelegate == null) {
            this.cpDelegate = ClasspathUtils.getDelegate(this.projectComponent);
        }
        return this.cpDelegate;
    }
    
    private ScriptRunnerBase getRunner() {
        return new ScriptRunnerCreator(this.projectComponent.getProject()).createRunner(this.manager, this.language, this.generateClassLoader());
    }
    
    public void add(final ResourceCollection resource) {
        this.resources.add(resource);
    }
}
