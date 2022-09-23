// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.tools.ant.taskdefs.optional.script;

import java.util.ArrayList;
import java.util.List;
import org.apache.tools.ant.BuildException;
import java.util.HashMap;
import java.util.Map;
import org.apache.tools.ant.DynamicConfigurator;
import org.apache.tools.ant.Task;

public class ScriptDefBase extends Task implements DynamicConfigurator
{
    private Map nestedElementMap;
    private Map attributes;
    private String text;
    
    public ScriptDefBase() {
        this.nestedElementMap = new HashMap();
        this.attributes = new HashMap();
    }
    
    @Override
    public void execute() {
        this.getScript().executeScript(this.attributes, this.nestedElementMap, this);
    }
    
    private ScriptDef getScript() {
        final String name = this.getTaskType();
        final Map scriptRepository = this.getProject().getReference("org.apache.ant.scriptrepo");
        if (scriptRepository == null) {
            throw new BuildException("Script repository not found for " + name);
        }
        final ScriptDef definition = scriptRepository.get(this.getTaskType());
        if (definition == null) {
            throw new BuildException("Script definition not found for " + name);
        }
        return definition;
    }
    
    public Object createDynamicElement(final String name) {
        List nestedElementList = this.nestedElementMap.get(name);
        if (nestedElementList == null) {
            nestedElementList = new ArrayList();
            this.nestedElementMap.put(name, nestedElementList);
        }
        final Object element = this.getScript().createNestedElement(name);
        nestedElementList.add(element);
        return element;
    }
    
    public void setDynamicAttribute(final String name, final String value) {
        final ScriptDef definition = this.getScript();
        if (!definition.isAttributeSupported(name)) {
            throw new BuildException("<" + this.getTaskType() + "> does not support the \"" + name + "\" attribute");
        }
        this.attributes.put(name, value);
    }
    
    public void addText(final String text) {
        this.text = this.getProject().replaceProperties(text);
    }
    
    public String getText() {
        return this.text;
    }
    
    public void fail(final String message) {
        throw new BuildException(message);
    }
}
