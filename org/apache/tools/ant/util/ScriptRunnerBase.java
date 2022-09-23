// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.tools.ant.util;

import org.apache.tools.ant.ProjectComponent;
import org.apache.tools.ant.types.ResourceCollection;
import java.io.InputStream;
import java.io.InputStreamReader;
import org.apache.tools.ant.types.Resource;
import java.io.IOException;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.Reader;
import java.io.FileReader;
import java.io.File;
import java.util.Iterator;
import org.apache.tools.ant.BuildException;
import java.util.HashMap;
import java.util.Map;
import org.apache.tools.ant.Project;

public abstract class ScriptRunnerBase
{
    private boolean keepEngine;
    private String language;
    private String script;
    private Project project;
    private ClassLoader scriptLoader;
    private Map beans;
    
    public ScriptRunnerBase() {
        this.keepEngine = false;
        this.script = "";
        this.beans = new HashMap();
    }
    
    public void addBeans(final Map dictionary) {
        for (final String key : dictionary.keySet()) {
            try {
                final Object val = dictionary.get(key);
                this.addBean(key, val);
            }
            catch (BuildException ex) {}
        }
    }
    
    public void addBean(final String key, final Object bean) {
        boolean isValid = key.length() > 0 && Character.isJavaIdentifierStart(key.charAt(0));
        for (int i = 1; isValid && i < key.length(); isValid = Character.isJavaIdentifierPart(key.charAt(i)), ++i) {}
        if (isValid) {
            this.beans.put(key, bean);
        }
    }
    
    protected Map getBeans() {
        return this.beans;
    }
    
    public abstract void executeScript(final String p0);
    
    public abstract Object evaluateScript(final String p0);
    
    public abstract boolean supportsLanguage();
    
    public abstract String getManagerName();
    
    public void setLanguage(final String language) {
        this.language = language;
    }
    
    public String getLanguage() {
        return this.language;
    }
    
    public void setScriptClassLoader(final ClassLoader classLoader) {
        this.scriptLoader = classLoader;
    }
    
    protected ClassLoader getScriptClassLoader() {
        return this.scriptLoader;
    }
    
    public void setKeepEngine(final boolean keepEngine) {
        this.keepEngine = keepEngine;
    }
    
    public boolean getKeepEngine() {
        return this.keepEngine;
    }
    
    public void setSrc(final File file) {
        final String filename = file.getPath();
        if (!file.exists()) {
            throw new BuildException("file " + filename + " not found.");
        }
        try {
            this.readSource(new FileReader(file), filename);
        }
        catch (FileNotFoundException e) {
            throw new BuildException("file " + filename + " not found.");
        }
    }
    
    private void readSource(final Reader reader, final String name) {
        BufferedReader in = null;
        try {
            in = new BufferedReader(reader);
            this.script += FileUtils.safeReadFully(in);
        }
        catch (IOException ex) {
            throw new BuildException("Failed to read " + name, ex);
        }
        finally {
            FileUtils.close(in);
        }
    }
    
    public void loadResource(final Resource sourceResource) {
        final String name = sourceResource.toLongString();
        InputStream in = null;
        try {
            in = sourceResource.getInputStream();
        }
        catch (IOException e) {
            throw new BuildException("Failed to open " + name, e);
        }
        catch (UnsupportedOperationException e2) {
            throw new BuildException("Failed to open " + name + " -it is not readable", e2);
        }
        this.readSource(new InputStreamReader(in), name);
    }
    
    public void loadResources(final ResourceCollection collection) {
        for (final Resource resource : collection) {
            this.loadResource(resource);
        }
    }
    
    public void addText(final String text) {
        this.script += text;
    }
    
    public String getScript() {
        return this.script;
    }
    
    public void clearScript() {
        this.script = "";
    }
    
    public void setProject(final Project project) {
        this.project = project;
    }
    
    public Project getProject() {
        return this.project;
    }
    
    public void bindToComponent(final ProjectComponent component) {
        this.project = component.getProject();
        this.addBeans(this.project.getProperties());
        this.addBeans(this.project.getUserProperties());
        this.addBeans(this.project.getCopyOfTargets());
        this.addBeans(this.project.getCopyOfReferences());
        this.addBean("project", this.project);
        this.addBean("self", component);
    }
    
    public void bindToComponentMinimum(final ProjectComponent component) {
        this.addBean("project", this.project = component.getProject());
        this.addBean("self", component);
    }
    
    protected void checkLanguage() {
        if (this.language == null) {
            throw new BuildException("script language must be specified");
        }
    }
    
    protected ClassLoader replaceContextLoader() {
        final ClassLoader origContextClassLoader = Thread.currentThread().getContextClassLoader();
        if (this.getScriptClassLoader() == null) {
            this.setScriptClassLoader(this.getClass().getClassLoader());
        }
        Thread.currentThread().setContextClassLoader(this.getScriptClassLoader());
        return origContextClassLoader;
    }
    
    protected void restoreContextLoader(final ClassLoader origLoader) {
        Thread.currentThread().setContextClassLoader(origLoader);
    }
}
